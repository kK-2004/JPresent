package com.JPresent.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * 幻灯片对象基类。
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TextBox.class, name = "text"),
        @JsonSubTypes.Type(value = RectangleShape.class, name = "rectangle"),
        @JsonSubTypes.Type(value = CircleShape.class, name = "circle"),
        @JsonSubTypes.Type(value = EllipseShape.class, name = "ellipse"),
        @JsonSubTypes.Type(value = LineShape.class, name = "line"),
        @JsonSubTypes.Type(value = ImageObject.class, name = "image")
})
public abstract class SlideObject {

    private int x;
    private int y;

    public SlideObject() {
    }

    public SlideObject(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public abstract void draw(Graphics2D g2d);

    @JsonIgnore
    public abstract Rectangle getBounds();
}
