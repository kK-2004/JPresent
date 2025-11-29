package com.JPresent.ui;

import com.JPresent.model.CircleShape;
import com.JPresent.model.EllipseShape;
import com.JPresent.model.ImageObject;
import com.JPresent.model.LineShape;
import com.JPresent.model.RectangleShape;
import com.JPresent.model.SlideObject;
import com.JPresent.model.TextBox;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Window;

/**
 * 元素样式设置对话框。
 */
public class StyleDialog extends JDialog {

    private final SlideObject target;

    private Color textColor;
    private Color fillColor;
    private Color strokeColor;

    private JTextField fontSizeField;
    private JComboBox<String> fontNameBox;

    private JTextField widthField;
    private JTextField heightField;
    private JTextField radiusField;

    private JTextField strokeWidthField;

    private JTextField lineX2Field;
    private JTextField lineY2Field;

    public StyleDialog(Window owner, SlideObject target) {
        super(owner, "样式设置", ModalityType.APPLICATION_MODAL);
        this.target = target;
        initValuesFromTarget();
        initUi();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initValuesFromTarget() {
        if (target instanceof TextBox) {
            TextBox textBox = (TextBox) target;
            textColor = textBox.getColor();
        } else if (target instanceof RectangleShape) {
            RectangleShape rectangleShape = (RectangleShape) target;
            fillColor = rectangleShape.getFillColor();
            strokeColor = rectangleShape.getStrokeColor();
        } else if (target instanceof EllipseShape) {
            EllipseShape ellipseShape = (EllipseShape) target;
            fillColor = ellipseShape.getFillColor();
            strokeColor = ellipseShape.getStrokeColor();
        } else if (target instanceof CircleShape) {
            CircleShape circleShape = (CircleShape) target;
            fillColor = circleShape.getFillColor();
            strokeColor = circleShape.getStrokeColor();
        } else if (target instanceof LineShape) {
            LineShape lineShape = (LineShape) target;
            strokeColor = lineShape.getStrokeColor();
        }
    }

    private void initUi() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(ModernTheme.BACKGROUND);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(ModernTheme.FONT_REGULAR);
        tabbedPane.setBackground(ModernTheme.SURFACE);
        tabbedPane.addTab("颜色", createColorPanel());
        tabbedPane.addTab("大小", createSizePanel());
        tabbedPane.addTab("样式", createStylePanel());

        add(tabbedPane, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createColorPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBackground(ModernTheme.SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        if (target instanceof TextBox) {
            TextBox textBox = (TextBox) target;
            JButton colorButton = new JButton("选择文字颜色");
            JPanel preview = createColorPreview(textBox.getColor());
            colorButton.addActionListener(e -> {
                Color chosen = JColorChooser.showDialog(this, "选择文字颜色", textBox.getColor());
                if (chosen != null) {
                    textColor = chosen;
                    preview.setBackground(chosen);
                }
            });
            panel.add(colorButton);
            panel.add(preview);
        } else if (target instanceof RectangleShape || target instanceof EllipseShape || target instanceof CircleShape) {
            JButton fillButton = new JButton("选择填充颜色");
            JButton strokeButton = new JButton("选择边框颜色");
            JPanel fillPreview = createColorPreview(fillColor != null ? fillColor : Color.WHITE);
            JPanel strokePreview = createColorPreview(strokeColor != null ? strokeColor : Color.BLACK);

            fillButton.addActionListener(e -> {
                Color chosen = JColorChooser.showDialog(this, "选择填充颜色", fillColor);
                if (chosen != null) {
                    fillColor = chosen;
                    fillPreview.setBackground(chosen);
                }
            });
            strokeButton.addActionListener(e -> {
                Color chosen = JColorChooser.showDialog(this, "选择边框颜色", strokeColor);
                if (chosen != null) {
                    strokeColor = chosen;
                    strokePreview.setBackground(chosen);
                }
            });

            panel.add(fillButton);
            panel.add(fillPreview);
            panel.add(strokeButton);
            panel.add(strokePreview);
        } else if (target instanceof LineShape) {
            JButton strokeButton = new JButton("选择线条颜色");
            JPanel strokePreview = createColorPreview(strokeColor != null ? strokeColor : Color.BLACK);
            strokeButton.addActionListener(e -> {
                Color chosen = JColorChooser.showDialog(this, "选择线条颜色", strokeColor);
                if (chosen != null) {
                    strokeColor = chosen;
                    strokePreview.setBackground(chosen);
                }
            });
            panel.add(strokeButton);
            panel.add(strokePreview);
        } else {
            panel.add(new JLabel("当前类型不支持颜色设置"));
        }

        return panel;
    }

    private JPanel createSizePanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBackground(ModernTheme.SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        if (target instanceof RectangleShape) {
            RectangleShape rectangleShape = (RectangleShape) target;
            panel.add(new JLabel("宽度："));
            widthField = new JTextField(String.valueOf(rectangleShape.getWidth()));
            panel.add(widthField);
            panel.add(new JLabel("高度："));
            heightField = new JTextField(String.valueOf(rectangleShape.getHeight()));
            panel.add(heightField);
        } else if (target instanceof EllipseShape) {
            EllipseShape ellipseShape = (EllipseShape) target;
            panel.add(new JLabel("宽度："));
            widthField = new JTextField(String.valueOf(ellipseShape.getWidth()));
            panel.add(widthField);
            panel.add(new JLabel("高度："));
            heightField = new JTextField(String.valueOf(ellipseShape.getHeight()));
            panel.add(heightField);
        } else if (target instanceof CircleShape) {
            CircleShape circleShape = (CircleShape) target;
            panel.add(new JLabel("半径："));
            radiusField = new JTextField(String.valueOf(circleShape.getRadius()));
            panel.add(radiusField);
        } else if (target instanceof ImageObject) {
            ImageObject imageObject = (ImageObject) target;
            panel.add(new JLabel("宽度："));
            widthField = new JTextField(String.valueOf(imageObject.getWidth()));
            panel.add(widthField);
            panel.add(new JLabel("高度："));
            heightField = new JTextField(String.valueOf(imageObject.getHeight()));
            panel.add(heightField);
        } else if (target instanceof LineShape) {
            LineShape lineShape = (LineShape) target;
            panel.add(new JLabel("终点 X："));
            lineX2Field = new JTextField(String.valueOf(lineShape.getX2()));
            panel.add(lineX2Field);
            panel.add(new JLabel("终点 Y："));
            lineY2Field = new JTextField(String.valueOf(lineShape.getY2()));
            panel.add(lineY2Field);
        } else if (target instanceof TextBox) {
            TextBox textBox = (TextBox) target;
            panel.add(new JLabel("字体大小："));
            fontSizeField = new JTextField(String.valueOf(textBox.getFontSize()));
            panel.add(fontSizeField);
        } else {
            panel.setLayout(new FlowLayout(FlowLayout.LEFT));
            panel.add(new JLabel("当前类型不支持大小设置"));
        }

        return panel;
    }

    private JPanel createStylePanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBackground(ModernTheme.SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        if (target instanceof TextBox) {
            TextBox textBox = (TextBox) target;
            panel.add(new JLabel("字体名称："));
            String[] fontNames = GraphicsEnvironment
                    .getLocalGraphicsEnvironment()
                    .getAvailableFontFamilyNames();
            fontNameBox = new JComboBox<>(fontNames);
            fontNameBox.setEditable(false);
            fontNameBox.setSelectedItem(textBox.getFontName());
            panel.add(fontNameBox);
        } else if (target instanceof com.JPresent.model.ShapeObject) {
            com.JPresent.model.ShapeObject shapeObject = (com.JPresent.model.ShapeObject) target;
            panel.add(new JLabel("边框粗细："));
            strokeWidthField = new JTextField(String.valueOf(shapeObject.getStrokeWidth()));
            panel.add(strokeWidthField);
        } else {
            panel.setLayout(new FlowLayout(FlowLayout.LEFT));
            panel.add(new JLabel("当前类型暂无额外样式选项"));
        }

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        panel.setBackground(ModernTheme.SURFACE);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ModernTheme.BORDER));

        JButton okButton = ModernTheme.createPrimaryButton("确定");
        JButton cancelButton = ModernTheme.createButton("取消");

        okButton.addActionListener(e -> {
            if (applyChanges()) {
                dispose();
            }
        });
        cancelButton.addActionListener(e -> dispose());

        panel.add(cancelButton);
        panel.add(okButton);
        return panel;
    }

    private boolean applyChanges() {
        try {
            if (target instanceof TextBox) {
                TextBox textBox = (TextBox) target;
                if (textColor != null) {
                    textBox.setColor(textColor);
                }
                if (fontSizeField != null && !fontSizeField.getText().isEmpty()) {
                    int size = Integer.parseInt(fontSizeField.getText());
                    textBox.setFontSize(Math.max(8, size));
                }
                if (fontNameBox != null && fontNameBox.getSelectedItem() != null) {
                    textBox.setFontName(fontNameBox.getSelectedItem().toString());
                }
            } else if (target instanceof RectangleShape) {
                RectangleShape rectangleShape = (RectangleShape) target;
                if (fillColor != null) {
                    rectangleShape.setFillColor(fillColor);
                }
                if (strokeColor != null) {
                    rectangleShape.setStrokeColor(strokeColor);
                }
                if (strokeWidthField != null && !strokeWidthField.getText().isEmpty()) {
                    int s = Integer.parseInt(strokeWidthField.getText());
                    rectangleShape.setStrokeWidth(s);
                }
                if (widthField != null && !widthField.getText().isEmpty()) {
                    int w = Integer.parseInt(widthField.getText());
                    rectangleShape.setWidth(Math.max(10, w));
                }
                if (heightField != null && !heightField.getText().isEmpty()) {
                    int h = Integer.parseInt(heightField.getText());
                    rectangleShape.setHeight(Math.max(10, h));
                }
            } else if (target instanceof EllipseShape) {
                EllipseShape ellipseShape = (EllipseShape) target;
                if (fillColor != null) {
                    ellipseShape.setFillColor(fillColor);
                }
                if (strokeColor != null) {
                    ellipseShape.setStrokeColor(strokeColor);
                }
                if (strokeWidthField != null && !strokeWidthField.getText().isEmpty()) {
                    int s = Integer.parseInt(strokeWidthField.getText());
                    ellipseShape.setStrokeWidth(s);
                }
                if (widthField != null && !widthField.getText().isEmpty()) {
                    int w = Integer.parseInt(widthField.getText());
                    ellipseShape.setWidth(Math.max(10, w));
                }
                if (heightField != null && !heightField.getText().isEmpty()) {
                    int h = Integer.parseInt(heightField.getText());
                    ellipseShape.setHeight(Math.max(10, h));
                }
            } else if (target instanceof CircleShape) {
                CircleShape circleShape = (CircleShape) target;
                if (fillColor != null) {
                    circleShape.setFillColor(fillColor);
                }
                if (strokeColor != null) {
                    circleShape.setStrokeColor(strokeColor);
                }
                 if (strokeWidthField != null && !strokeWidthField.getText().isEmpty()) {
                    int s = Integer.parseInt(strokeWidthField.getText());
                    circleShape.setStrokeWidth(s);
                }
                if (radiusField != null && !radiusField.getText().isEmpty()) {
                    int r = Integer.parseInt(radiusField.getText());
                    circleShape.setRadius(Math.max(10, r));
                }
            } else if (target instanceof ImageObject) {
                ImageObject imageObject = (ImageObject) target;
                if (widthField != null && !widthField.getText().isEmpty()) {
                    int w = Integer.parseInt(widthField.getText());
                    imageObject.setWidth(Math.max(10, w));
                }
                if (heightField != null && !heightField.getText().isEmpty()) {
                    int h = Integer.parseInt(heightField.getText());
                    imageObject.setHeight(Math.max(10, h));
                }
            } else if (target instanceof LineShape) {
                LineShape lineShape = (LineShape) target;
                if (strokeColor != null) {
                    lineShape.setStrokeColor(strokeColor);
                }
                if (strokeWidthField != null && !strokeWidthField.getText().isEmpty()) {
                    int s = Integer.parseInt(strokeWidthField.getText());
                    lineShape.setStrokeWidth(s);
                }
                if (lineX2Field != null && !lineX2Field.getText().isEmpty()) {
                    int x2 = Integer.parseInt(lineX2Field.getText());
                    lineShape.setX2(x2);
                }
                if (lineY2Field != null && !lineY2Field.getText().isEmpty()) {
                    int y2 = Integer.parseInt(lineY2Field.getText());
                    lineShape.setY2(y2);
                }
            }
            return true;
        } catch (NumberFormatException ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "输入的数值不合法。", "错误",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private JPanel createColorPreview(Color initial) {
        JPanel preview = new JPanel();
        preview.setBackground(initial);
        preview.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        preview.setPreferredSize(new java.awt.Dimension(60, 20));
        return preview;
    }

    public static void showDialog(SlidePanel slidePanel, SlideObject target) {
        Window window = SwingUtilities.getWindowAncestor(slidePanel);
        StyleDialog dialog = new StyleDialog(window, target);
        dialog.setVisible(true);
    }
}
