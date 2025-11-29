package com.JPresent.model;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * 矩形。
 */
public class RectangleShape extends ShapeObject {

    private int width = 120;
    private int height = 80;

    public RectangleShape() {
        super();
    }

    public RectangleShape(int x, int y) {
        super(x, y);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void draw(Graphics2D g2d) {
        g2d.setColor(getFillColor());
        g2d.fillRect(getX(), getY(), width, height);
        g2d.setColor(getStrokeColor());
        g2d.setStroke(new BasicStroke(getStrokeWidth()));
        g2d.drawRect(getX(), getY(), width, height);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), width, height);
    }
}
