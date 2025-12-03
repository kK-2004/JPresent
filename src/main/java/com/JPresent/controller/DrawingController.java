package com.JPresent.controller;

import com.JPresent.model.CircleShape;
import com.JPresent.model.EllipseShape;
import com.JPresent.model.ImageObject;
import com.JPresent.model.LineShape;
import com.JPresent.model.Presentation;
import com.JPresent.model.RectangleShape;
import com.JPresent.model.Slide;
import com.JPresent.model.SlideObject;
import com.JPresent.model.TextBox;
import com.JPresent.ui.SlidePanel;
import com.JPresent.ui.TextEditDialog;

import javax.swing.JFileChooser;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * 绘图控制器。
 */
public class DrawingController {

    public enum Tool {
        SELECT,
        RECTANGLE,
        CIRCLE,
        ELLIPSE,
        LINE,
        TEXT
    }

    private final Presentation presentation;
    private final SlidePanel slidePanel;
    private Tool currentTool = Tool.SELECT;

    public DrawingController(Presentation presentation, SlidePanel slidePanel) {
        this.presentation = presentation;
        this.slidePanel = slidePanel;
    }

    public void setTool(Tool tool) {
        this.currentTool = tool;
    }

    public Tool getCurrentTool() {
        return currentTool;
    }

    public SlideObject handleMousePressed(MouseEvent e) {
        Slide slide = presentation.getCurrentSlide();
        // 将视图坐标转换为幻灯片坐标
        int x = slidePanel.toSlideX(e.getX());
        int y = slidePanel.toSlideY(e.getY());
        // 对创建新对象的操作记录撤销快照
        if (currentTool != Tool.SELECT) {
            slidePanel.snapshotForUndo();
        }
        switch (currentTool) {
            case RECTANGLE:
                RectangleShape rectangleShape = new RectangleShape(x, y);
                slide.getObjects().add(rectangleShape);
                return rectangleShape;
            case CIRCLE:
                CircleShape circleShape = new CircleShape(x, y);
                slide.getObjects().add(circleShape);
                return circleShape;
            case ELLIPSE:
                EllipseShape ellipseShape = new EllipseShape(x, y);
                slide.getObjects().add(ellipseShape);
                return ellipseShape;
            case LINE:
                LineShape lineShape = new LineShape(x, y, x + 100, y);
                slide.getObjects().add(lineShape);
                return lineShape;
            case TEXT:
                TextBox textBox = new TextBox(x, y);
                boolean ok = TextEditDialog.editText(slidePanel, textBox, true);
                if (ok && textBox.getText() != null && !textBox.getText().isEmpty()) {
                    slide.getObjects().add(textBox);
                    return textBox;
                }
                return null;
            default:
                return null;
        }
    }

    public void insertImage() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(slidePanel);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            slidePanel.snapshotForUndo();
            Slide slide = presentation.getCurrentSlide();
            ImageObject imageObject = new ImageObject(50, 50, file.getAbsolutePath());
            slide.getObjects().add(imageObject);
            if (slidePanel.getSelectionController() != null) {
                slidePanel.getSelectionController().setSelectedObject(imageObject);
            }
            setTool(Tool.SELECT);
            slidePanel.repaint();
        }
    }
}
