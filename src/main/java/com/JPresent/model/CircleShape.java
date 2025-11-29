package com.JPresent.model;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * 圆形。
 */
public class CircleShape extends ShapeObject {

    private int radius = 50;

    public CircleShape() {
        super();
    }

    public CircleShape(int x, int y) {
        super(x, y);
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    public void draw(Graphics2D g2d) {
        int d = radius * 2;
        g2d.setColor(getFillColor());
        g2d.fillOval(getX(), getY(), d, d);
        g2d.setColor(getStrokeColor());
        g2d.setStroke(new BasicStroke(getStrokeWidth()));
        g2d.drawOval(getX(), getY(), d, d);
    }

    @Override
    public Rectangle getBounds() {
        int d = radius * 2;
        return new Rectangle(getX(), getY(), d, d);
    }
}
