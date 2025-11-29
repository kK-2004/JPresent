package com.JPresent.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.awt.Color;

/**
 * 图形基类。
 */
public abstract class ShapeObject extends SlideObject {

    @JsonIgnore
    private Color fillColor = Color.WHITE;
    @JsonIgnore
    private Color strokeColor = Color.BLACK;
    private int strokeWidth = 2;

    public ShapeObject() {
        super();
    }

    public ShapeObject(int x, int y) {
        super(x, y);
    }

    @JsonIgnore
    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    @JsonIgnore
    public Color getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        if (strokeWidth <= 0) {
            strokeWidth = 1;
        }
        this.strokeWidth = strokeWidth;
    }

    @JsonProperty("fillColor")
    public int getFillColorValue() {
        return fillColor.getRGB();
    }

    @JsonProperty("fillColor")
    public void setFillColorValue(int rgb) {
        this.fillColor = new Color(rgb, true);
    }

    @JsonProperty("strokeColor")
    public int getStrokeColorValue() {
        return strokeColor.getRGB();
    }

    @JsonProperty("strokeColor")
    public void setStrokeColorValue(int rgb) {
        this.strokeColor = new Color(rgb, true);
    }
}
