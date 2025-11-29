package com.JPresent.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 单页幻灯片。
 */
public class Slide {

    private List<SlideObject> objects = new ArrayList<>();

    public List<SlideObject> getObjects() {
        return objects;
    }

    public void setObjects(List<SlideObject> objects) {
        this.objects = objects;
    }
}
