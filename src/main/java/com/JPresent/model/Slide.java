package com.JPresent.model;

import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 单页幻灯片。
 */
public class Slide {

    private List<SlideObject> objects = new ArrayList<>();
    @JsonIgnore
    private Color backgroundColor = Color.WHITE;

    public List<SlideObject> getObjects() {
        return objects;
    }

    public void setObjects(List<SlideObject> objects) {
        this.objects = objects;
    }

    @JsonIgnore
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor != null ? backgroundColor : Color.WHITE;
    }

    @JsonProperty("backgroundColor")
    public int getBackgroundColorValue() {
        return backgroundColor.getRGB();
    }

    @JsonProperty("backgroundColor")
    public void setBackgroundColorValue(int rgb) {
        this.backgroundColor = new Color(rgb, true);
    }
}
