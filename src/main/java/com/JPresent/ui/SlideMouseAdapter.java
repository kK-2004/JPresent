package com.JPresent.ui;

import com.JPresent.controller.DrawingController;
import com.JPresent.controller.SelectionController;
import com.JPresent.model.CircleShape;
import com.JPresent.model.EllipseShape;
import com.JPresent.model.ImageObject;
import com.JPresent.model.LineShape;
import com.JPresent.model.RectangleShape;
import com.JPresent.model.SlideObject;
import com.JPresent.model.TextBox;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 画布鼠标事件。
 */
public class SlideMouseAdapter extends MouseAdapter {

    private final SlidePanel slidePanel;
    // 记录上一次拖动位置（幻灯片坐标）
    private int lastSlideX;
    private int lastSlideY;

    private enum DragMode {
        NONE,
        MOVE,
        RESIZE
    }

    private enum LineHandle {
        NONE,
        START,
        END
    }

    private DragMode dragMode = DragMode.NONE;
    private LineHandle lineHandle = LineHandle.NONE;

    public SlideMouseAdapter(SlidePanel slidePanel) {
        this.slidePanel = slidePanel;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        slidePanel.requestFocusInWindow();

        if (SwingUtilities.isRightMouseButton(e)) {
            handleContextMenu(e);
            return;
        }

        // 记录按下时的幻灯片坐标
        lastSlideX = slidePanel.toSlideX(e.getX());
        lastSlideY = slidePanel.toSlideY(e.getY());
        DrawingController drawingController = slidePanel.getDrawingController();
        SelectionController selectionController = slidePanel.getSelectionController();

        dragMode = DragMode.NONE;
        lineHandle = LineHandle.NONE;
        if (selectionController != null && selectionController.getSelectedObject() != null) {
            SlideObject selected = selectionController.getSelectedObject();
            int sx = slidePanel.toSlideX(e.getX());
            int sy = slidePanel.toSlideY(e.getY());
            // 直线：两个端点可拉伸，中间区域整体拖动
            if (selected instanceof LineShape) {
                LineShape line = (LineShape) selected;
                int x1 = line.getX();
                int y1 = line.getY();
                int x2 = line.getX2();
                int y2 = line.getY2();
                int threshold = 8;

                double distStart = distance(sx, sy, x1, y1);
                double distEnd = distance(sx, sy, x2, y2);
                if (distStart <= threshold) {
                    slidePanel.snapshotForUndo();
                    dragMode = DragMode.RESIZE;
                    lineHandle = LineHandle.START;
                    return;
                } else if (distEnd <= threshold) {
                    slidePanel.snapshotForUndo();
                    dragMode = DragMode.RESIZE;
                    lineHandle = LineHandle.END;
                    return;
                }

                double distToSegment = distanceToSegment(sx, sy, x1, y1, x2, y2);
                if (distToSegment <= threshold) {
                    slidePanel.snapshotForUndo();
                    dragMode = DragMode.MOVE;
                    return;
                }
            } else {
                Rectangle bounds = selected.getBounds();
                if (bounds != null && bounds.contains(sx, sy)) {
                    int cornerX = bounds.x + bounds.width;
                    int cornerY = bounds.y + bounds.height;
                    if (Math.abs(sx - cornerX) <= 8 && Math.abs(sy - cornerY) <= 8) {
                        slidePanel.snapshotForUndo();
                        dragMode = DragMode.RESIZE;
                        return;
                    } else {
                        slidePanel.snapshotForUndo();
                        dragMode = DragMode.MOVE;
                    }
                }
            }
        }

        if (drawingController != null) {
            SlideObject created = drawingController.handleMousePressed(e);
            if (created != null) {
                if (selectionController != null) {
                    selectionController.setSelectedObject(created);
                }
                drawingController.setTool(DrawingController.Tool.SELECT);
                slidePanel.repaint();
                if (slidePanel.getThumbnailPanel() != null) {
                    slidePanel.getThumbnailPanel().repaint();
                }
                return;
            }
        }
        if (selectionController != null) {
            selectionController.selectAt(e.getX(), e.getY());
            dragMode = DragMode.MOVE;
            slidePanel.repaint();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() >= 2 && SwingUtilities.isLeftMouseButton(e)) {
            SelectionController selectionController = slidePanel.getSelectionController();
            if (selectionController == null) {
                return;
            }
            selectionController.selectAt(e.getX(), e.getY());
            SlideObject selected = selectionController.getSelectedObject();
            if (selected instanceof TextBox) {
                slidePanel.snapshotForUndo();
                if (TextEditDialog.editText(slidePanel, (TextBox) selected, false)) {
                    slidePanel.repaint();
                    if (slidePanel.getThumbnailPanel() != null) {
                        slidePanel.getThumbnailPanel().repaint();
                    }
                }
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        SelectionController selectionController = slidePanel.getSelectionController();
        if (selectionController == null) {
            return;
        }
        SlideObject selected = selectionController.getSelectedObject();
        if (selected == null) {
            return;
        }

        int sx = slidePanel.toSlideX(e.getX());
        int sy = slidePanel.toSlideY(e.getY());

        if (dragMode == DragMode.RESIZE) {
            resizeSelected(selected, sx, sy);
            slidePanel.repaint();
            if (slidePanel.getThumbnailPanel() != null) {
                slidePanel.getThumbnailPanel().repaint();
            }
            return;
        }

        if (dragMode == DragMode.MOVE) {
            int dx = sx - lastSlideX;
            int dy = sy - lastSlideY;
            if (selected instanceof LineShape) {
                LineShape line = (LineShape) selected;
                line.setX(line.getX() + dx);
                line.setY(line.getY() + dy);
                line.setX2(line.getX2() + dx);
                line.setY2(line.getY2() + dy);
            } else {
                selected.setX(selected.getX() + dx);
                selected.setY(selected.getY() + dy);
            }
            lastSlideX = sx;
            lastSlideY = sy;
            slidePanel.repaint();
            if (slidePanel.getThumbnailPanel() != null) {
                slidePanel.getThumbnailPanel().repaint();
            }
        }
    }

    private void resizeSelected(SlideObject selected, int mouseX, int mouseY) {
        if (selected instanceof RectangleShape) {
            RectangleShape rectangleShape = (RectangleShape) selected;
            int newWidth = Math.max(10, mouseX - rectangleShape.getX());
            int newHeight = Math.max(10, mouseY - rectangleShape.getY());
            rectangleShape.setWidth(newWidth);
            rectangleShape.setHeight(newHeight);
        } else if (selected instanceof TextBox) {
            TextBox textBox = (TextBox) selected;
            int newWidth = Math.max(10, mouseX - textBox.getX());
            int newHeight = Math.max(10, mouseY - textBox.getY());
            textBox.setWidth(newWidth);
            textBox.setHeight(newHeight);
        } else if (selected instanceof EllipseShape) {
            EllipseShape ellipseShape = (EllipseShape) selected;
            int newWidth = Math.max(10, mouseX - ellipseShape.getX());
            int newHeight = Math.max(10, mouseY - ellipseShape.getY());
            ellipseShape.setWidth(newWidth);
            ellipseShape.setHeight(newHeight);
        } else if (selected instanceof CircleShape) {
            CircleShape circleShape = (CircleShape) selected;
            int dx = mouseX - circleShape.getX();
            int dy = mouseY - circleShape.getY();
            int radius = Math.max(10, Math.max(dx, dy) / 2);
            circleShape.setRadius(radius);
        } else if (selected instanceof LineShape) {
            LineShape lineShape = (LineShape) selected;
            if (lineHandle == LineHandle.START) {
                lineShape.setX(mouseX);
                lineShape.setY(mouseY);
            } else {
                lineShape.setX2(mouseX);
                lineShape.setY2(mouseY);
            }
        } else if (selected instanceof ImageObject) {
            ImageObject imageObject = (ImageObject) selected;
            int newWidth = Math.max(10, mouseX - imageObject.getX());
            int newHeight = Math.max(10, mouseY - imageObject.getY());
            imageObject.setWidth(newWidth);
            imageObject.setHeight(newHeight);
        }
    }

    private void handleContextMenu(MouseEvent e) {
        SelectionController selectionController = slidePanel.getSelectionController();
        if (selectionController == null) {
            return;
        }
        selectionController.selectAt(e.getX(), e.getY());
        SlideObject selected = selectionController.getSelectedObject();
        slidePanel.repaint();
        if (selected == null) {
            return;
        }

        JPopupMenu menu = new JPopupMenu();

        if (selected instanceof TextBox) {
            JMenuItem editTextItem = new JMenuItem("编辑文本...");
            editTextItem.addActionListener(ae -> {
                if (TextEditDialog.editText(slidePanel, (TextBox) selected, false)) {
                    slidePanel.repaint();
                    if (slidePanel.getThumbnailPanel() != null) {
                        slidePanel.getThumbnailPanel().repaint();
                    }
                }
            });
            menu.add(editTextItem);
            menu.addSeparator();
        }

        JMenuItem styleItem = new JMenuItem("样式设置...");
        styleItem.addActionListener(ae -> {
            slidePanel.snapshotForUndo();
            StyleDialog.showDialog(slidePanel, selected);
            slidePanel.repaint();
        });

        JMenuItem deleteItem = new JMenuItem("删除");
        deleteItem.addActionListener(ae -> {
            slidePanel.snapshotForUndo();
            var slide = slidePanel.getCurrentSlide();
            slide.getObjects().remove(selected);
            selectionController.setSelectedObject(null);
            slidePanel.repaint();
            if (slidePanel.getThumbnailPanel() != null) {
                slidePanel.getThumbnailPanel().repaint();
            }
        });

        menu.add(styleItem);
        menu.add(deleteItem);

        menu.show(slidePanel, e.getX(), e.getY());
    }

    private void showStyleDialog(SlideObject selected) {
        // 保留方法签名以兼容旧代码，实际逻辑已移动到 StyleDialog
        StyleDialog.showDialog(slidePanel, selected);
    }

    private double distance(int x1, int y1, int x2, int y2) {
        int dx = x1 - x2;
        int dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private double distanceToSegment(int px, int py, int x1, int y1, int x2, int y2) {
        double vx = x2 - x1;
        double vy = y2 - y1;
        double wx = px - x1;
        double wy = py - y1;

        double c1 = vx * wx + vy * wy;
        if (c1 <= 0) {
            return distance(px, py, x1, y1);
        }
        double c2 = vx * vx + vy * vy;
        if (c2 <= c1) {
            return distance(px, py, x2, y2);
        }
        double b = c1 / c2;
        double bx = x1 + b * vx;
        double by = y1 + b * vy;
        return distance(px, py, (int) Math.round(bx), (int) Math.round(by));
    }
}
