package com.JPresent.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import java.awt.*;

/**
 * UI主题配置类
 */
public class ModernTheme {

    // 主色调 - 现代蓝
    public static final Color PRIMARY = new Color(37, 99, 235);
    public static final Color PRIMARY_HOVER = new Color(29, 78, 216);
    public static final Color PRIMARY_PRESSED = new Color(30, 64, 175);

    // 背景色
    public static final Color BACKGROUND = new Color(248, 250, 252);
    public static final Color SURFACE = Color.WHITE;
    public static final Color SURFACE_HOVER = new Color(241, 245, 249);

    // 文字颜色
    public static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    public static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    public static final Color TEXT_DISABLED = new Color(203, 213, 225);

    // 边框颜色
    public static final Color BORDER = new Color(226, 232, 240);
    public static final Color BORDER_FOCUS = PRIMARY;

    // 工具栏颜色
    public static final Color TOOLBAR_BG = SURFACE;
    public static final Color TOOLBAR_BORDER = new Color(229, 231, 235);

    // 侧边栏颜色
    public static final Color SIDEBAR_BG = new Color(249, 250, 251);

    // 成功/警告/错误颜色
    public static final Color SUCCESS = new Color(34, 197, 94);
    public static final Color WARNING = new Color(251, 146, 60);
    public static final Color ERROR = new Color(239, 68, 68);

    // 字体
    public static final Font FONT_REGULAR = new Font("微软雅黑", Font.PLAIN, 13);
    public static final Font FONT_MEDIUM = new Font("微软雅黑", Font.PLAIN, 14);
    public static final Font FONT_LARGE = new Font("微软雅黑", Font.PLAIN, 16);
    public static final Font FONT_TITLE = new Font("微软雅黑", Font.BOLD, 20);

    // 圆角
    public static final int RADIUS_SMALL = 6;
    public static final int RADIUS_MEDIUM = 8;
    public static final int RADIUS_LARGE = 12;

    // 阴影
    public static final Color SHADOW = new Color(0, 0, 0, 8);

    public static void apply() {
        try {
            // 设置系统外观
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // 自定义UI组件样式
            UIManager.put("control", BACKGROUND);
            UIManager.put("text", TEXT_PRIMARY);
            UIManager.put("nimbusBase", PRIMARY);
            UIManager.put("nimbusFocus", PRIMARY);
            UIManager.put("nimbusSelectedText", Color.WHITE);
            UIManager.put("nimbusSelectionBackground", PRIMARY);

            // 按钮样式
            UIManager.put("Button.font", FONT_REGULAR);
            UIManager.put("Button.background", SURFACE);
            UIManager.put("Button.foreground", TEXT_PRIMARY);
            UIManager.put("Button.select", PRIMARY_HOVER);

            // 文本框样式
            UIManager.put("TextField.font", FONT_REGULAR);
            UIManager.put("TextField.background", SURFACE);
            UIManager.put("TextField.foreground", TEXT_PRIMARY);
            UIManager.put("TextField.selectionBackground", PRIMARY);
            UIManager.put("TextField.selectionForeground", Color.WHITE);

            // 菜单样式
            UIManager.put("Menu.font", FONT_REGULAR);
            UIManager.put("MenuItem.font", FONT_REGULAR);
            UIManager.put("Menu.selectionBackground", SURFACE_HOVER);
            UIManager.put("Menu.selectionForeground", TEXT_PRIMARY);
            UIManager.put("MenuItem.selectionBackground", SURFACE_HOVER);
            UIManager.put("MenuItem.selectionForeground", TEXT_PRIMARY);

            // 标签样式
            UIManager.put("Label.font", FONT_REGULAR);
            UIManager.put("Label.foreground", TEXT_PRIMARY);

            // 面板样式
            UIManager.put("Panel.background", SURFACE);

            // 选项卡样式
            UIManager.put("TabbedPane.font", FONT_REGULAR);
            UIManager.put("TabbedPane.selected", SURFACE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JButton createButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, false);
        return button;
    }

    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, true);
        return button;
    }

    public static void styleButton(JButton button, boolean isPrimary) {
        button.setFont(FONT_REGULAR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (isPrimary) {
            button.setBackground(PRIMARY);
            button.setForeground(Color.WHITE);
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(PRIMARY_HOVER);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(PRIMARY);
                }
                public void mousePressed(java.awt.event.MouseEvent evt) {
                    button.setBackground(PRIMARY_PRESSED);
                }
                public void mouseReleased(java.awt.event.MouseEvent evt) {
                    button.setBackground(PRIMARY_HOVER);
                }
            });
        } else {
            button.setBackground(SURFACE);
            button.setForeground(TEXT_PRIMARY);
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
            ));
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(SURFACE_HOVER);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(SURFACE);
                }
            });
        }

        // 设置内边距
        button.setMargin(new Insets(8, 16, 8, 16));
    }

    public static JButton createToolbarButton(String text) {
        JButton button = new JButton(text);
        button.putClientProperty("selected", Boolean.FALSE);
        button.setFont(FONT_REGULAR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setForeground(TEXT_PRIMARY);
        button.setMargin(new Insets(6, 12, 6, 12));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (Boolean.TRUE.equals(button.getClientProperty("selected"))) {
                    return;
                }
                button.setContentAreaFilled(true);
                button.setBackground(SURFACE_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (Boolean.TRUE.equals(button.getClientProperty("selected"))) {
                    return;
                }
                button.setContentAreaFilled(false);
                button.setBackground(null);
            }
        });

        return button;
    }

    public static Border createRoundedBorder(int radius) {
        return BorderFactory.createCompoundBorder(
            new RoundedBorder(radius, BORDER),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        );
    }

    public static JPanel createCard() {
        JPanel panel = new JPanel();
        panel.setBackground(SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }


    static class RoundedBorder implements Border {
        private final int radius;
        private final Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(1, 1, 1, 1);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }
}
