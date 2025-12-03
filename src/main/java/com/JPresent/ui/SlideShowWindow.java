package com.JPresent.ui;

import com.JPresent.model.Presentation;
import com.JPresent.model.Slide;
import com.JPresent.model.SlideConstants;
import com.JPresent.model.SlideObject;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * 幻灯片放映窗口（全屏播放模式）。
 */
public class SlideShowWindow extends JFrame {

    private final Presentation presentation;
    private final SlidePanel editSlidePanel;
    private final ThumbnailPanel editThumbnailPanel;
    private final SlideShowPanel slideShowPanel;

    private enum TransitionEffect {
        NONE,
        FADE,
        SLIDE,
        ZOOM
    }

    private TransitionEffect transitionEffect = TransitionEffect.FADE;
    private boolean animating = false;
    private boolean animForward = true;
    private float animationProgress = 1.0f;
    private Slide animFromSlide;
    private Slide animToSlide;
    private int animTargetIndex = -1;
    private javax.swing.Timer animationTimer;

    public SlideShowWindow(JFrame owner,
                           Presentation presentation,
                           SlidePanel editSlidePanel,
                           ThumbnailPanel editThumbnailPanel,
                           int startIndex) {
        super("幻灯片放映");
        this.presentation = presentation;
        this.editSlidePanel = editSlidePanel;
        this.editThumbnailPanel = editThumbnailPanel;

        if (startIndex < 0 || startIndex >= presentation.getSlides().size()) {
            startIndex = 0;
        }
        presentation.setCurrentIndex(startIndex);

        ModernTheme.apply();

        setUndecorated(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        slideShowPanel = new SlideShowPanel(presentation);
        slideShowPanel.setBackground(Color.BLACK);
        add(slideShowPanel, BorderLayout.CENTER);

        setupKeyBindings();

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(owner);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                refreshEditorViews();
            }
        });
    }

    /**
     * 显示放映窗口。
     */
    public void start() {
        setVisible(true);
        requestFocusInWindow();
    }

    private void setupKeyBindings() {
        JRootPane root = getRootPane();
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        // 退出放映
        im.put(KeyStroke.getKeyStroke("ESCAPE"), "exit");
        am.put("exit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // 下一页
        String[] nextKeys = {"RIGHT", "DOWN", "SPACE", "ENTER", "PAGE_DOWN"};
        for (String key : nextKeys) {
            im.put(KeyStroke.getKeyStroke(key), "nextSlide");
        }
        am.put("nextSlide", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextSlide();
            }
        });

        // 上一页
        String[] prevKeys = {"LEFT", "UP", "BACK_SPACE", "PAGE_UP"};
        for (String key : prevKeys) {
            im.put(KeyStroke.getKeyStroke(key), "prevSlide");
        }
        am.put("prevSlide", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                previousSlide();
            }
        });
    }

    private void nextSlide() {
        int index = presentation.getCurrentIndex();
        if (index < presentation.getSlides().size() - 1) {
            startTransition(index + 1, true);
        } else {
            // 最后一页时退出
            dispose();
        }
    }

    private void previousSlide() {
        int index = presentation.getCurrentIndex();
        if (index > 0) {
            startTransition(index - 1, false);
        }
    }

    private void startTransition(int newIndex, boolean forward) {
        if (newIndex < 0 || newIndex >= presentation.getSlides().size()) {
            return;
        }
        if (transitionEffect == TransitionEffect.NONE || presentation.getSlides().size() <= 1) {
            presentation.setCurrentIndex(newIndex);
            slideShowPanel.repaint();
            refreshEditorViews();
            return;
        }
        if (animating) {
            return;
        }
        animating = true;
        animForward = forward;
        animTargetIndex = newIndex;
        animFromSlide = presentation.getCurrentSlide();
        animToSlide = presentation.getSlides().get(newIndex);
        animationProgress = 0.0f;

        int durationMs = 500;
        int steps = 25;
        int delay = durationMs / steps;
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
        animationTimer = new javax.swing.Timer(delay, e -> {
            animationProgress += 1.0f / steps;
            if (animationProgress >= 1.0f) {
                animationProgress = 1.0f;
                animationTimer.stop();
                animating = false;
                presentation.setCurrentIndex(animTargetIndex);
                animFromSlide = null;
                animToSlide = null;
                animTargetIndex = -1;
                refreshEditorViews();
            }
            slideShowPanel.repaint();
        });
        animationTimer.start();
    }

    private void refreshEditorViews() {
        if (editSlidePanel != null) {
            editSlidePanel.repaint();
        }
        if (editThumbnailPanel != null) {
            editThumbnailPanel.repaint();
        }
    }

    /**
     * 全屏播放用的幻灯片显示面板。
     */
    private class SlideShowPanel extends JPanel {

        private final Presentation presentation;

        SlideShowPanel(Presentation presentation) {
            this.presentation = presentation;
            setBackground(Color.BLACK);

            // 鼠标左键：下一页；右键：弹出结束菜单
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        nextSlide();
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        showContextMenu(e.getX(), e.getY());
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            double scale = Math.min(
                    (double) width / SlideConstants.SLIDE_WIDTH,
                    (double) height / SlideConstants.SLIDE_HEIGHT
            );

            int slideDrawWidth = (int) (SlideConstants.SLIDE_WIDTH * scale);
            int slideDrawHeight = (int) (SlideConstants.SLIDE_HEIGHT * scale);

            int x = (width - slideDrawWidth) / 2;
            int y = (height - slideDrawHeight) / 2;

            g2d.translate(x, y);
            g2d.scale(scale, scale);

            if (animating && animFromSlide != null && animToSlide != null
                    && transitionEffect != TransitionEffect.NONE) {
                switch (transitionEffect) {
                    case FADE:
                        paintFadeTransition(g2d);
                        break;
                    case SLIDE:
                        paintSlideTransition(g2d);
                        break;
                    case ZOOM:
                        paintZoomTransition(g2d);
                        break;
                    default:
                        paintSlideContent(g2d, presentation.getCurrentSlide());
                        break;
                }
            } else {
                paintSlideContent(g2d, presentation.getCurrentSlide());
            }

            g2d.dispose();

            // 左下角绘制页码指示（例如：1 / 5）
            Graphics2D overlay = (Graphics2D) g.create();
            overlay.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            overlay.setFont(ModernTheme.FONT_REGULAR);
            overlay.setColor(Color.WHITE);

            String text = (presentation.getCurrentIndex() + 1) + " / " + presentation.getSlides().size();
            int margin = 16;
            FontMetrics fm = overlay.getFontMetrics();
            int textY = getHeight() - margin;
            int textX = margin;

            // 为文字加一层半透明背景，提高可读性
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();
            overlay.setColor(new Color(0, 0, 0, 120));
            overlay.fillRoundRect(textX - 6, textY - textHeight + fm.getDescent() - 4,
                    textWidth + 12, textHeight + 8, 10, 10);

            overlay.setColor(Color.WHITE);
            overlay.drawString(text, textX, textY);
            overlay.dispose();
        }

        private void showContextMenu(int x, int y) {
            JPopupMenu menu = new JPopupMenu();
            JMenuItem prevItem = new JMenuItem("返回上一页");
            prevItem.addActionListener(e -> previousSlide());
            menu.add(prevItem);

            JMenuItem exitItem = new JMenuItem("结束放映");
            exitItem.addActionListener(e -> SlideShowWindow.this.dispose());
            menu.add(exitItem);

            JMenu effectMenu = new JMenu("切换效果");
            JRadioButtonMenuItem noneItem = new JRadioButtonMenuItem("无");
            JRadioButtonMenuItem fadeItem = new JRadioButtonMenuItem("淡入淡出");
            JRadioButtonMenuItem slideItem = new JRadioButtonMenuItem("滑动");
            JRadioButtonMenuItem zoomItem = new JRadioButtonMenuItem("缩放");

            ButtonGroup group = new ButtonGroup();
            group.add(noneItem);
            group.add(fadeItem);
            group.add(slideItem);
            group.add(zoomItem);

            switch (transitionEffect) {
                case NONE:
                    noneItem.setSelected(true);
                    break;
                case FADE:
                    fadeItem.setSelected(true);
                    break;
                case SLIDE:
                    slideItem.setSelected(true);
                    break;
                case ZOOM:
                    zoomItem.setSelected(true);
                    break;
                default:
                    fadeItem.setSelected(true);
            }

            noneItem.addActionListener(e -> transitionEffect = TransitionEffect.NONE);
            fadeItem.addActionListener(e -> transitionEffect = TransitionEffect.FADE);
            slideItem.addActionListener(e -> transitionEffect = TransitionEffect.SLIDE);
            zoomItem.addActionListener(e -> transitionEffect = TransitionEffect.ZOOM);

            effectMenu.add(noneItem);
            effectMenu.add(fadeItem);
            effectMenu.add(slideItem);
            effectMenu.add(zoomItem);
            menu.addSeparator();
            menu.add(effectMenu);

            menu.show(this, x, y);
        }

        private void paintSlideContent(Graphics2D g2d, Slide slide) {
            Color bg = slide.getBackgroundColor();
            if (bg == null) {
                bg = Color.WHITE;
            }
            g2d.setColor(bg);
            g2d.fillRect(0, 0, SlideConstants.SLIDE_WIDTH, SlideConstants.SLIDE_HEIGHT);
            for (SlideObject object : slide.getObjects()) {
                object.draw(g2d);
            }
        }

        private void paintFadeTransition(Graphics2D base) {
            Graphics2D gFrom = (Graphics2D) base.create();
            Graphics2D gTo = (Graphics2D) base.create();

            float alphaFrom = 1.0f - animationProgress;
            gFrom.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaFrom));
            paintSlideContent(gFrom, animFromSlide);
            gFrom.dispose();

            float alphaTo = animationProgress;
            gTo.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaTo));
            paintSlideContent(gTo, animToSlide);
            gTo.dispose();
        }

        private void paintSlideTransition(Graphics2D base) {
            double offset = SlideConstants.SLIDE_WIDTH * animationProgress;

            Graphics2D gFrom = (Graphics2D) base.create();
            Graphics2D gTo = (Graphics2D) base.create();

            if (animForward) {
                gFrom.translate(-offset, 0);
                paintSlideContent(gFrom, animFromSlide);
                gTo.translate(SlideConstants.SLIDE_WIDTH - offset, 0);
                paintSlideContent(gTo, animToSlide);
            } else {
                gFrom.translate(offset, 0);
                paintSlideContent(gFrom, animFromSlide);
                gTo.translate(-SlideConstants.SLIDE_WIDTH + offset, 0);
                paintSlideContent(gTo, animToSlide);
            }

            gFrom.dispose();
            gTo.dispose();
        }

        private void paintZoomTransition(Graphics2D base) {
            Graphics2D gFrom = (Graphics2D) base.create();
            Graphics2D gTo = (Graphics2D) base.create();

            float alphaFrom = 1.0f - animationProgress;
            gFrom.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaFrom));
            paintSlideContent(gFrom, animFromSlide);
            gFrom.dispose();

            double zoom = 0.8 + 0.2 * animationProgress;
            int cx = SlideConstants.SLIDE_WIDTH / 2;
            int cy = SlideConstants.SLIDE_HEIGHT / 2;
            AffineTransform old = gTo.getTransform();
            gTo.translate(cx, cy);
            gTo.scale(zoom, zoom);
            gTo.translate(-cx, -cy);

            float alphaTo = 0.5f + 0.5f * animationProgress;
            gTo.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaTo));
            paintSlideContent(gTo, animToSlide);
            gTo.setTransform(old);
            gTo.dispose();
        }
    }
}
