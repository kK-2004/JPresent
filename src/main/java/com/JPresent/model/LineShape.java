package com.JPresent.model;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * 直线。
 */
public class LineShape extends ShapeObject {

    private int x2;
    private int y2;

    public LineShape() {
        super();
    }

    public LineShape(int x, int y, int x2, int y2) {
        super(x, y);
        this.x2 = x2;
        this.y2 = y2;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    @Override
    public void draw(Graphics2D g2d) {
        g2d.setColor(getStrokeColor());
        g2d.setStroke(new BasicStroke(getStrokeWidth()));
        g2d.drawLine(getX(), getY(), x2, y2);
    }

    @Override
    public Rectangle getBounds() {
        int minX = Math.min(getX(), x2);
        int minY = Math.min(getY(), y2);
        int width = Math.abs(getX() - x2);
        int height = Math.abs(getY() - y2);
        return new Rectangle(minX, minY, width, height);
    }
}
