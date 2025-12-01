package com.JPresent.ui;

import com.JPresent.controller.DrawingController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AppToolBar extends JToolBar {

    private JButton currentSelectedButton;

    public AppToolBar(DrawingController drawingController) {
        // 工具栏样式设置
        setFloatable(false);
        setBackground(ModernTheme.TOOLBAR_BG);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ModernTheme.TOOLBAR_BORDER),
            new EmptyBorder(8, 12, 8, 12)
        ));

        // 创建工具按钮
        JButton selectButton = createToolButton("选择", DrawingController.Tool.SELECT, drawingController);
        JButton rectButton = createToolButton("矩形", DrawingController.Tool.RECTANGLE, drawingController);
        JButton circleButton = createToolButton("圆形", DrawingController.Tool.CIRCLE, drawingController);
        JButton ellipseButton = createToolButton("椭圆", DrawingController.Tool.ELLIPSE, drawingController);
        JButton lineButton = createToolButton("直线", DrawingController.Tool.LINE, drawingController);
        JButton textButton = createToolButton("文本", DrawingController.Tool.TEXT, drawingController);

        // 默认选中选择工具
        currentSelectedButton = selectButton;
        applySelectedStyle(selectButton);

        add(selectButton);
        addSeparator(new Dimension(8, 0));
        add(rectButton);
        add(circleButton);
        add(ellipseButton);
        addSeparator(new Dimension(8, 0));
        add(lineButton);
        add(textButton);
        add(Box.createHorizontalGlue());
    }

    private JButton createToolButton(String text, DrawingController.Tool tool, DrawingController controller) {
        JButton button = ModernTheme.createToolbarButton(text);

        button.addActionListener(e -> {
            controller.setTool(tool);

            // 重置之前选中的按钮
            if (currentSelectedButton != null) {
                clearSelectedStyle(currentSelectedButton);
            }

            // 高亮当前按钮
            applySelectedStyle(button);
            currentSelectedButton = button;
        });

        return button;
    }

    private void applySelectedStyle(JButton button) {
        button.putClientProperty("selected", Boolean.TRUE);
        button.setBackground(ModernTheme.PRIMARY);
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(true);
    }

    private void clearSelectedStyle(JButton button) {
        button.putClientProperty("selected", Boolean.FALSE);
        button.setBackground(ModernTheme.TOOLBAR_BG);
        button.setForeground(ModernTheme.TEXT_PRIMARY);
        button.setContentAreaFilled(false);
    }
}
