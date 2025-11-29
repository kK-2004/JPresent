package com.JPresent.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * 文本框。
 */
public class TextBox extends SlideObject {

    private String text = "双击编辑文本";
    private String fontName = "SansSerif";
    private int fontStyle = Font.PLAIN;
    private int fontSize = 18;
    @JsonIgnore
    private Color color = Color.BLACK;
    private int width = 200;
    private int height = 100;

    public TextBox() {
        super();
    }

    public TextBox(int x, int y) {
        super(x, y);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public int getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    @JsonIgnore
    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @JsonProperty("color")
    public int getColorValue() {
        return color.getRGB();
    }

    @JsonProperty("color")
    public void setColorValue(int rgb) {
        this.color = new Color(rgb, true);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = Math.max(10, width);
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = Math.max(10, height);
    }

    @Override
    public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        Font font = new Font(fontName, fontStyle, fontSize);
        g2d.setFont(font);
        FontMetrics metrics = g2d.getFontMetrics(font);
        int lineHeight = metrics.getHeight();
        int x = getX();
        int y = getY() + metrics.getAscent();
        if (text != null) {
            String[] lines = text.split("\n");
            for (String line : lines) {
                g2d.drawString(line, x, y);
                y += lineHeight;
            }
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), width, height);
    }
}
