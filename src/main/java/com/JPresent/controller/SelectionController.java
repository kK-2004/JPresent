package com.JPresent.controller;

import com.JPresent.model.LineShape;
import com.JPresent.model.Presentation;
import com.JPresent.model.Slide;
import com.JPresent.model.SlideObject;
import com.JPresent.ui.SlidePanel;

/**
 * 选择控制器。
 */
public class SelectionController {

    private final Presentation presentation;
    private final SlidePanel slidePanel;
    private SlideObject selectedObject;

    public SelectionController(Presentation presentation, SlidePanel slidePanel) {
        this.presentation = presentation;
        this.slidePanel = slidePanel;
    }

    public void selectAt(int viewX, int viewY) {
        int x = slidePanel.toSlideX(viewX);
        int y = slidePanel.toSlideY(viewY);
        Slide slide = presentation.getCurrentSlide();
        selectedObject = null;
        for (SlideObject object : slide.getObjects()) {
            if (object instanceof LineShape) {
                if (isNearLine((LineShape) object, x, y)) {
                    selectedObject = object;
                }
            } else if (object.getBounds().contains(x, y)) {
                selectedObject = object;
            }
        }
    }

    public SlideObject getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(SlideObject selectedObject) {
        this.selectedObject = selectedObject;
    }

    private boolean isNearLine(LineShape line, int px, int py) {
        int x1 = line.getX();
        int y1 = line.getY();
        int x2 = line.getX2();
        int y2 = line.getY2();
        double dist = distanceToSegment(px, py, x1, y1, x2, y2);
        return dist <= 6.0;
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

    private double distance(int x1, int y1, int x2, int y2) {
        int dx = x1 - x2;
        int dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
