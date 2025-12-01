package com.JPresent.ui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class NavigationWindow extends JFrame {

    public NavigationWindow() {
        super("JPresent");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(null);
        setUndecorated(false);

        // 应用主题
        ModernTheme.apply();
        getContentPane().setBackground(ModernTheme.BACKGROUND);
        setLayout(new BorderLayout());

        // 顶部面板
        JPanel topPanel = new JPanel();
        topPanel.setBackground(ModernTheme.SURFACE);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 30, 40));

        JLabel titleLabel = new JLabel("JPresent");
        titleLabel.setFont(ModernTheme.FONT_TITLE);
        titleLabel.setForeground(ModernTheme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("演示文稿编辑器");
        subtitleLabel.setFont(ModernTheme.FONT_REGULAR);
        subtitleLabel.setForeground(ModernTheme.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(8));
        topPanel.add(subtitleLabel);

        // 中间按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(ModernTheme.BACKGROUND);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JButton newButton = ModernTheme.createPrimaryButton("新建演示文稿");
        newButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        newButton.setMaximumSize(new Dimension(300, 45));
        newButton.setPreferredSize(new Dimension(300, 45));

        JButton openButton = ModernTheme.createButton("打开已有文件");
        openButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        openButton.setMaximumSize(new Dimension(300, 45));
        openButton.setPreferredSize(new Dimension(300, 45));

        newButton.addActionListener(e -> {
            MainWindow mainWindow = new MainWindow();
            mainWindow.setVisible(true);
            dispose();
        });

        openButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("打开演示文稿");
            chooser.setFileFilter(new FileNameExtensionFilter("JSON 文件 (*.json)", "json"));
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                MainWindow mainWindow = new MainWindow();
                try {
                    mainWindow.getFileController().openPresentation(file);
                    mainWindow.setVisible(true);
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "打开失败: " + ex.getMessage(),
                            "错误",
                            JOptionPane.ERROR_MESSAGE
                    );
                    mainWindow.dispose();
                }
            }
        });

        buttonPanel.add(newButton);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(openButton);

        // 底部信息
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(ModernTheme.BACKGROUND);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        JLabel infoLabel = new JLabel("v1.0 - 基于 Java Swing 开发");
        infoLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        infoLabel.setForeground(ModernTheme.TEXT_DISABLED);
        bottomPanel.add(infoLabel);

        add(topPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
//        add(bottomPanel, BorderLayout.SOUTH);
    }
}

