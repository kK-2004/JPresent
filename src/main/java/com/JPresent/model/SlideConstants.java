package com.JPresent.model;

/**
 * 幻灯片标准尺寸常量
 * 使用Microsoft PowerPoint的标准16:9宽屏尺寸
 */
public class SlideConstants {

    /**
     * 标准幻灯片宽度（像素）
     * 对应PowerPoint的16:9宽屏格式：1920像素
     */
    public static final int SLIDE_WIDTH = 1920;

    /**
     * 标准幻灯片高度（像素）
     * 对应PowerPoint的16:9宽屏格式：1080像素
     */
    public static final int SLIDE_HEIGHT = 1080;

    /**
     * 幻灯片宽高比
     */
    public static final double ASPECT_RATIO = (double) SLIDE_WIDTH / SLIDE_HEIGHT;

    /**
     * 私有构造函数，防止实例化
     */
    private SlideConstants() {
        throw new AssertionError("Cannot instantiate SlideConstants");
    }
}
