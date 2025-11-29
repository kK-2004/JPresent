package com.JPresent.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 幻灯片文档。
 */
public class Presentation {

    private List<Slide> slides = new ArrayList<>();
    private int currentIndex = 0;

    public Presentation() {
        if (slides.isEmpty()) {
            slides.add(new Slide());
        }
    }

    public List<Slide> getSlides() {
        return slides;
    }

    public void setSlides(List<Slide> slides) {
        this.slides = slides;
        if (slides == null || slides.isEmpty()) {
            this.slides = new ArrayList<>();
            this.slides.add(new Slide());
            this.currentIndex = 0;
        } else if (currentIndex >= this.slides.size()) {
            currentIndex = 0;
        }
    }

    public Slide getCurrentSlide() {
        if (slides.isEmpty()) {
            slides.add(new Slide());
            currentIndex = 0;
        }
        return slides.get(currentIndex);
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        if (currentIndex >= 0 && currentIndex < slides.size()) {
            this.currentIndex = currentIndex;
        }
    }

    public void addSlide(Slide slide) {
        slides.add(slide);
    }
}
