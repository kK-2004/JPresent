package com.JPresent.ui;

import com.JPresent.controller.DrawingController;
import com.JPresent.controller.SelectionController;
import com.JPresent.model.Presentation;
import com.JPresent.model.LineShape;
import com.JPresent.model.Slide;
import com.JPresent.model.SlideObject;
import com.JPresent.model.SlideConstants;
import com.JPresent.service.UndoRedoService;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * 幻灯片编辑面板。
 * 使用Microsoft PowerPoint标准16:9尺寸（1920x1080）
 */
public class SlidePanel extends JPanel implements Scrollable {

    private final Presentation presentation;
    private DrawingController drawingController;
    private SelectionController selectionController;
    private ThumbnailPanel thumbnailPanel;
    private UndoRedoService undoRedoService;
    private int slideMargin = 20;

    public SlidePanel(Presentation presentation) {
        this.presentation = presentation;
        setBackground(Color.WHITE);

        setFocusable(true);

        setSlideMargin(20);

        SlideMouseAdapter adapter = new SlideMouseAdapter(this);
        addMouseListener(adapter);
        addMouseMotionListener(adapter);
        addMouseWheelListener(adapter);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPressed(e);
            }
        });
    }

    /**
     * 获取显示缩放比例
     */
    public double getDisplayScale() {
        double scaleX = (double) getWidth() / SlideConstants.SLIDE_WIDTH;
        double scaleY = (double) getHeight() / SlideConstants.SLIDE_HEIGHT;
        double scale = Math.min(scaleX, scaleY);
        // 只缩小，不放大，避免超过100%比例
        return Math.min(1.0, scale);
    }

    public void setControllers(DrawingController drawingController, SelectionController selectionController) {
        this.drawingController = drawingController;
        this.selectionController = selectionController;
    }

    public DrawingController getDrawingController() {
        return drawingController;
    }

    public SelectionController getSelectionController() {
        return selectionController;
    }

    public void setThumbnailPanel(ThumbnailPanel thumbnailPanel) {
        this.thumbnailPanel = thumbnailPanel;
    }

    public ThumbnailPanel getThumbnailPanel() {
        return thumbnailPanel;
    }

    public void setUndoRedoService(UndoRedoService undoRedoService) {
        this.undoRedoService = undoRedoService;
    }

    public UndoRedoService getUndoRedoService() {
        return undoRedoService;
    }

    public Slide getCurrentSlide() {
        return presentation.getCurrentSlide();
    }

    public int getSlideMargin() {
        return slideMargin;
    }

    public void setSlideMargin(int margin) {
        this.slideMargin = Math.max(0, margin);
        setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createEmptyBorder(slideMargin, slideMargin, slideMargin, slideMargin),
                javax.swing.BorderFactory.createLineBorder(ModernTheme.BORDER, 1)
        ));
        revalidate();
        repaint();
    }

    public void snapshotForUndo() {
        if (undoRedoService != null) {
            undoRedoService.snapshot(presentation);
        }
    }

    /**
     * 视图坐标 -> 幻灯片坐标（考虑缩放）
     */
    public int toSlideX(int viewX) {
        double scale = getDisplayScale();
        if (scale <= 0) {
            return viewX;
        }
        return (int) Math.round(viewX / scale);
    }

    public int toSlideY(int viewY) {
        double scale = getDisplayScale();
        if (scale <= 0) {
            return viewY;
        }
        return (int) Math.round(viewY / scale);
    }

    private void handleKeyPressed(KeyEvent e) {
        if (selectionController == null) {
            return;
        }
        var selected = selectionController.getSelectedObject();
        if (selected == null) {
            return;
        }
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_DELETE || code == KeyEvent.VK_BACK_SPACE) {
            snapshotForUndo();
            Slide slide = presentation.getCurrentSlide();
            slide.getObjects().remove(selected);
            selectionController.setSelectedObject(null);
            repaint();
            if (thumbnailPanel != null) {
                thumbnailPanel.repaint();
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        // 默认按标准幻灯片尺寸，交给滚动容器决定最终显示大小
        return new Dimension(SlideConstants.SLIDE_WIDTH, SlideConstants.SLIDE_HEIGHT);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 100;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        // 随视口宽度变化，便于自适应窗口
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        // 随视口高度变化，便于自适应窗口
        return true;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 计算缩放比例并应用变换
        double scale = getDisplayScale();
        g2d.scale(scale, scale);

        Slide slide = presentation.getCurrentSlide();
        Color bg = slide.getBackgroundColor();
        if (bg == null) {
            bg = Color.WHITE;
        }
        g2d.setColor(bg);
        g2d.fillRect(0, 0, SlideConstants.SLIDE_WIDTH, SlideConstants.SLIDE_HEIGHT);
        for (SlideObject object : slide.getObjects()) {
            object.draw(g2d);
        }

        if (selectionController != null && selectionController.getSelectedObject() != null) {
            SlideObject selected = selectionController.getSelectedObject();
            if (!slide.getObjects().contains(selected)) {
                // 跨页后自动清除无效选择
                selectionController.setSelectedObject(null);
            } else if (selected.getBounds() != null) {
                if (selected instanceof LineShape) {
                    // 直线：在两个端点绘制可见控制点
                    LineShape line = (LineShape) selected;
                    int handleSize = 10;

                    int x1 = line.getX();
                    int y1 = line.getY();
                    int x2 = line.getX2();
                    int y2 = line.getY2();

                    g2d.setColor(ModernTheme.PRIMARY);
                    g2d.setStroke(new java.awt.BasicStroke(2f));

                    // 起点控制点
                    g2d.fillOval(x1 - handleSize / 2, y1 - handleSize / 2, handleSize, handleSize);
                    g2d.setColor(Color.WHITE);
                    g2d.setStroke(new java.awt.BasicStroke(1.5f));
                    g2d.drawOval(x1 - handleSize / 2, y1 - handleSize / 2, handleSize, handleSize);

                    // 终点控制点
                    g2d.setColor(ModernTheme.PRIMARY);
                    g2d.setStroke(new java.awt.BasicStroke(2f));
                    g2d.fillOval(x2 - handleSize / 2, y2 - handleSize / 2, handleSize, handleSize);
                    g2d.setColor(Color.WHITE);
                    g2d.setStroke(new java.awt.BasicStroke(1.5f));
                    g2d.drawOval(x2 - handleSize / 2, y2 - handleSize / 2, handleSize, handleSize);
                } else {
                    // 其他形状：绘制矩形选择框和右下角控制点
                    g2d.setColor(ModernTheme.PRIMARY);
                    g2d.setStroke(new java.awt.BasicStroke(2f));
                    var bounds = selected.getBounds();
                    g2d.draw(bounds);

                    int handleSize = 10;
                    int hx = bounds.x + bounds.width - handleSize / 2;
                    int hy = bounds.y + bounds.height - handleSize / 2;
                    g2d.setColor(ModernTheme.PRIMARY);
                    g2d.fillOval(hx - handleSize / 2, hy - handleSize / 2, handleSize, handleSize);
                    g2d.setColor(Color.WHITE);
                    g2d.setStroke(new java.awt.BasicStroke(1.5f));
                    g2d.drawOval(hx - handleSize / 2, hy - handleSize / 2, handleSize, handleSize);
                }
            }
        }
        g2d.dispose();
    }
}
