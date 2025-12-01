package com.JPresent.ui;

import com.JPresent.controller.DrawingController;
import com.JPresent.controller.FileController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;

/**
 * 现代化菜单栏。
 */
public class AppMenuBar extends JMenuBar {

    public AppMenuBar(MainWindow window, FileController fileController, DrawingController drawingController) {
        // 菜单栏样式
        setBackground(ModernTheme.SURFACE);
        setBorder(new EmptyBorder(4, 8, 4, 8));

        // 文件菜单
        JMenu fileMenu = createMenu("文件");
        fileMenu.add(createMenuItem("新建", "Ctrl+N", e -> fileController.newPresentation()));
        fileMenu.add(createMenuItem("打开...", "Ctrl+O", e -> fileController.openPresentation()));
        fileMenu.add(createMenuItem("保存", "Ctrl+S", e -> fileController.savePresentation()));
        fileMenu.add(createMenuItem("另存为...", "Ctrl+Shift+S", e -> fileController.savePresentationAs()));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem("导出当前页为图片", null, e -> fileController.exportCurrentSlideAsImage()));
        fileMenu.add(createMenuItem("导出为 PDF", null, e -> fileController.exportAsPdf()));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem("退出", "Alt+F4", e -> window.dispose()));

        // 插入菜单
        JMenu insertMenu = createMenu("插入");
        insertMenu.add(createMenuItem("新建幻灯片", "Ctrl+M", e -> fileController.addNewSlide()));
        insertMenu.addSeparator();
        insertMenu.add(createMenuItem("文本框", null, e -> drawingController.setTool(DrawingController.Tool.TEXT)));
        insertMenu.add(createMenuItem("矩形", null, e -> drawingController.setTool(DrawingController.Tool.RECTANGLE)));
        insertMenu.add(createMenuItem("圆形", null, e -> drawingController.setTool(DrawingController.Tool.CIRCLE)));
        insertMenu.add(createMenuItem("椭圆", null, e -> drawingController.setTool(DrawingController.Tool.ELLIPSE)));
        insertMenu.add(createMenuItem("直线", null, e -> drawingController.setTool(DrawingController.Tool.LINE)));
        insertMenu.add(createMenuItem("图片", null, e -> drawingController.insertImage()));

        // 放映菜单
        JMenu slideShowMenu = createMenu("放映");
        slideShowMenu.add(createMenuItem("从第一张开始播放", "F5", e -> window.startSlideShowFromBeginning()));
        slideShowMenu.add(createMenuItem("从当前幻灯片开始播放", "Shift+F5", e -> window.startSlideShowFromCurrent()));

        add(fileMenu);
        add(insertMenu);
        add(slideShowMenu);
    }

    private JMenu createMenu(String text) {
        JMenu menu = new JMenu(text);
        menu.setFont(ModernTheme.FONT_REGULAR);
        menu.setForeground(ModernTheme.TEXT_PRIMARY);
        return menu;
    }

    private JMenuItem createMenuItem(String text, String accelerator, java.awt.event.ActionListener listener) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(ModernTheme.FONT_REGULAR);
        item.setForeground(ModernTheme.TEXT_PRIMARY);
        item.addActionListener(listener);

        if (accelerator != null) {
            item.setAccelerator(KeyStroke.getKeyStroke(accelerator.replace("Ctrl", "control").replace("Shift", "shift").replace("Alt", "alt")));
        }

        return item;
    }
}
