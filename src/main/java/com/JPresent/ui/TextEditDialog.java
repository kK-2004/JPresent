package com.JPresent.ui;

import com.JPresent.model.TextBox;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

/**
 * 文本编辑对话框，支持同时设置内容、字体、大小和颜色。
 */
public class TextEditDialog extends JDialog {

    private final TextBox target;
    private boolean applied = false;

    private JTextArea textArea;
    private JComboBox<String> fontNameBox;
    private JTextField fontSizeField;
    private JComboBox<String> styleBox;
    private Color selectedColor;

    public TextEditDialog(Window owner, TextBox target, boolean isNew) {
        super(owner, isNew ? "新建文本" : "编辑文本", ModalityType.APPLICATION_MODAL);
        this.target = target;
        this.selectedColor = target.getColor();
        initUi();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initUi() {
        setLayout(new BorderLayout(8, 8));
        getContentPane().setBackground(ModernTheme.BACKGROUND);

        textArea = new JTextArea(5, 30);
        textArea.setFont(ModernTheme.FONT_REGULAR);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setText(target.getText() != null ? target.getText() : "");

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(12, 12, 12, 12),
            BorderFactory.createLineBorder(ModernTheme.BORDER, 1)
        ));
        add(scrollPane, BorderLayout.CENTER);

        JPanel optionsPanel = new JPanel(new GridBagLayout());
        optionsPanel.setBackground(ModernTheme.SURFACE);
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;

        // 字体
        c.gridx = 0;
        c.gridy = 0;
        optionsPanel.add(new JLabel("字体："), c);

        String[] fontNames = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();
        fontNameBox = new JComboBox<>(fontNames);
        fontNameBox.setEditable(false);
        fontNameBox.setSelectedItem(target.getFontName());
        c.gridx = 1;
        optionsPanel.add(fontNameBox, c);

        // 大小
        c.gridx = 0;
        c.gridy = 1;
        optionsPanel.add(new JLabel("大小："), c);

        fontSizeField = new JTextField(String.valueOf(target.getFontSize()), 5);
        c.gridx = 1;
        optionsPanel.add(fontSizeField, c);

        // 样式
        c.gridx = 0;
        c.gridy = 2;
        optionsPanel.add(new JLabel("样式："), c);

        styleBox = new JComboBox<>(new String[]{"常规", "加粗", "斜体", "加粗斜体"});
        styleBox.setSelectedIndex(styleFromFontStyle(target.getFontStyle()));
        c.gridx = 1;
        optionsPanel.add(styleBox, c);

        // 颜色
        c.gridx = 0;
        c.gridy = 3;
        optionsPanel.add(new JLabel("颜色："), c);

        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        JButton colorButton = new JButton("选择颜色");
        JPanel preview = new JPanel();
        preview.setBackground(selectedColor);
        preview.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        preview.setPreferredSize(new java.awt.Dimension(40, 16));
        colorButton.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "选择文本颜色", selectedColor);
            if (chosen != null) {
                selectedColor = chosen;
                preview.setBackground(chosen);
            }
        });
        colorPanel.add(colorButton);
        colorPanel.add(preview);

        c.gridx = 1;
        optionsPanel.add(colorPanel, c);

        add(optionsPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        buttonPanel.setBackground(ModernTheme.SURFACE);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ModernTheme.BORDER));

        JButton okButton = ModernTheme.createPrimaryButton("确定");
        JButton cancelButton = ModernTheme.createButton("取消");

        okButton.addActionListener(e -> {
            if (applyChanges()) {
                applied = true;
                dispose();
            }
        });
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private int styleFromFontStyle(int fontStyle) {
        if (fontStyle == Font.BOLD) {
            return 1;
        } else if (fontStyle == Font.ITALIC) {
            return 2;
        } else if (fontStyle == (Font.BOLD | Font.ITALIC)) {
            return 3;
        }
        return 0;
    }

    private int fontStyleFromSelection(int index) {
        switch (index) {
            case 1:
                return Font.BOLD;
            case 2:
                return Font.ITALIC;
            case 3:
                return Font.BOLD | Font.ITALIC;
            default:
                return Font.PLAIN;
        }
    }

    private boolean applyChanges() {
        String text = textArea.getText();
        if (text == null || text.trim().isEmpty()) {
            // 允许空文本，但可以按需限制
            target.setText("");
        } else {
            target.setText(text);
        }
        try {
            int size = Integer.parseInt(fontSizeField.getText().trim());
            target.setFontSize(Math.max(8, size));
        } catch (NumberFormatException ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "字体大小必须为数字。", "错误",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (fontNameBox.getSelectedItem() != null) {
            target.setFontName(fontNameBox.getSelectedItem().toString());
        }
        target.setFontStyle(fontStyleFromSelection(styleBox.getSelectedIndex()));
        if (selectedColor != null) {
            target.setColor(selectedColor);
        }
        return true;
    }

    public static boolean editText(SlidePanel slidePanel, TextBox target, boolean isNew) {
        Window owner = SwingUtilities.getWindowAncestor(slidePanel);
        TextEditDialog dialog = new TextEditDialog(owner, target, isNew);
        dialog.setVisible(true);
        return dialog.applied;
    }
}

