package com.JPresent.ui;

import com.JPresent.model.Presentation;
import com.JPresent.model.Slide;
import com.JPresent.model.SlideObject;
import com.JPresent.model.SlideConstants;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 现代化缩略图面板。
 * 缩略图按PPT标准尺寸（1920x1080）等比缩放
 */
public class ThumbnailPanel extends JPanel {

    private static final int THUMB_SPACING = 12;
    private static final int THUMB_MARGIN = 16;
    // 缩略图宽度，高度按16:9比例自动计算
    private static final int THUMB_WIDTH = 160;

    private final Presentation presentation;
    private final SlidePanel slidePanel;
    private int hoverIndex = -1;

    public ThumbnailPanel(Presentation presentation, SlidePanel slidePanel) {
        this.presentation = presentation;
        this.slidePanel = slidePanel;
        setPreferredSize(new Dimension(200, 600));
        setBackground(ModernTheme.SIDEBAR_BG);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = getSlideIndexAt(e.getY());
                if (index >= 0 && index < presentation.getSlides().size()) {
                    presentation.setCurrentIndex(index);
                    if (slidePanel.getSelectionController() != null) {
                        slidePanel.getSelectionController().setSelectedObject(null);
                    }
                    slidePanel.repaint();
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoverIndex = -1;
                repaint();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int newHoverIndex = getSlideIndexAt(e.getY());
                if (newHoverIndex != hoverIndex) {
                    hoverIndex = newHoverIndex;
                    repaint();
                }
            }
        });
    }

    private int getSlideIndexAt(int y) {
        int thumbHeight = (int) (THUMB_WIDTH / SlideConstants.ASPECT_RATIO);
        int itemHeight = thumbHeight + THUMB_SPACING + 20;
        return (y - THUMB_MARGIN) / itemHeight;
    }

    private int getThumbHeight() {
        return (int) (THUMB_WIDTH / SlideConstants.ASPECT_RATIO);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int y = THUMB_MARGIN;
        int index = 0;
        int currentIndex = presentation.getCurrentIndex();

        for (Slide slide : presentation.getSlides()) {
            int x = THUMB_MARGIN;
            int width = THUMB_WIDTH;
            int height = getThumbHeight();

            boolean isCurrent = (index == currentIndex);
            boolean isHover = (index == hoverIndex);

            // 绘制阴影效果
            if (isCurrent || isHover) {
                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.fillRoundRect(x + 2, y + 2, width, height, 8, 8);
            }

            // 绘制缩略图背景
            g2d.setColor(Color.WHITE);
            g2d.fillRoundRect(x, y, width, height, 8, 8);

            // 绘制缩略图内容 - 按PPT标准尺寸等比缩放
            double scale = (double) width / SlideConstants.SLIDE_WIDTH;
            java.awt.geom.AffineTransform old = g2d.getTransform();
            g2d.translate(x, y);
            g2d.scale(scale, scale);

            // 设置裁剪区域
            g2d.setClip(0, 0, SlideConstants.SLIDE_WIDTH, SlideConstants.SLIDE_HEIGHT);
            for (SlideObject object : slide.getObjects()) {
                object.draw(g2d);
            }
            g2d.setTransform(old);
            g2d.setClip(null); // 清除裁剪区域

            // 绘制边框
            if (isCurrent) {
                g2d.setColor(ModernTheme.PRIMARY);
                g2d.setStroke(new BasicStroke(2.5f));
                g2d.drawRoundRect(x, y, width - 1, height - 1, 8, 8);
            } else if (isHover) {
                g2d.setColor(ModernTheme.BORDER_FOCUS);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(x, y, width - 1, height - 1, 8, 8);
            } else {
                g2d.setColor(ModernTheme.BORDER);
                g2d.setStroke(new BasicStroke(1f));
                g2d.drawRoundRect(x, y, width - 1, height - 1, 8, 8);
            }

            // 绘制幻灯片编号
            g2d.setFont(new Font("微软雅黑", Font.PLAIN, 11));
            String slideNum = String.valueOf(index + 1);
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(slideNum);
            int textX = x + (width - textWidth) / 2;
            int textY = y + height + 16;

            g2d.setColor(isCurrent ? ModernTheme.PRIMARY : ModernTheme.TEXT_SECONDARY);
            g2d.drawString(slideNum, textX, textY);

            y += height + THUMB_SPACING + 20;
            index++;
        }
        g2d.dispose();
    }
}
