package com.JPresent.model;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

/**
 * 图片对象。
 */
public class ImageObject extends SlideObject {

    private String path;
    private int width = 200;
    private int height = 150;

    public ImageObject() {
        super();
    }

    public ImageObject(int x, int y, String path) {
        super(x, y);
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
        if (path == null) {
            return;
        }
        try {
            Image img = ImageIO.read(new File(path));
            if (img != null) {
                g2d.drawImage(img, getX(), getY(), width, height, null);
            }
        } catch (IOException e) {
            // Ignore draw error for robustness.
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), width, height);
    }
}
