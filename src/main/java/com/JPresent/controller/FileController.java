package com.JPresent.controller;

import com.JPresent.model.Presentation;
import com.JPresent.service.ExportService;
import com.JPresent.service.FileService;
import com.JPresent.ui.SlidePanel;
import com.JPresent.ui.ThumbnailPanel;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;

/**
 * 文件控制器。
 */
public class FileController {

    private final JFrame parent;
    private final Presentation presentation;
    private final FileService fileService;
    private final ExportService exportService;
    private final SlidePanel slidePanel;
    private final ThumbnailPanel thumbnailPanel;
    private File currentFile;

    public FileController(JFrame parent, Presentation presentation, FileService fileService,
                          ExportService exportService, SlidePanel slidePanel, ThumbnailPanel thumbnailPanel) {
        this.parent = parent;
        this.presentation = presentation;
        this.fileService = fileService;
        this.exportService = exportService;
        this.slidePanel = slidePanel;
        this.thumbnailPanel = thumbnailPanel;
    }

    public void newPresentation() {
        presentation.getSlides().clear();
        presentation.addSlide(new com.JPresent.model.Slide());
        presentation.setCurrentIndex(0);
        currentFile = null;
        slidePanel.repaint();
        if (thumbnailPanel != null) {
            thumbnailPanel.repaint();
        }
        if (slidePanel.getSelectionController() != null) {
            slidePanel.getSelectionController().setSelectedObject(null);
        }
    }

    public void openPresentation() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                openPresentation(file);
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(parent, "打开失败: " + ex.getMessage(), "错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 从指定 JSON 文件打开演示文稿。
     */
    public void openPresentation(File file) {
        if (file == null) {
            return;
        }
        try {
            fileService.load(presentation, file);
            currentFile = file;
            slidePanel.repaint();
            if (thumbnailPanel != null) {
                thumbnailPanel.repaint();
            }
            if (slidePanel.getSelectionController() != null) {
                slidePanel.getSelectionController().setSelectedObject(null);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void savePresentation() {
        if (currentFile == null) {
            savePresentationAs();
            return;
        }
        try {
            fileService.save(presentation, currentFile);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parent, "保存失败: " + ex.getMessage(), "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void savePresentationAs() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("保存为 JSON");
        chooser.setFileFilter(new FileNameExtensionFilter("JSON 文件 (*.json)", "json"));
        String baseName = getBaseName();
        chooser.setSelectedFile(new File(baseName + ".json"));
        int result = chooser.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".json")) {
                file = new File(file.getParentFile(), file.getName() + ".json");
            }
            try {
                fileService.save(presentation, file);
                currentFile = file;
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent, "保存失败: " + ex.getMessage(), "错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void exportCurrentSlideAsImage() {
        Object[] options = {"PNG", "JPG"};
        int choice = JOptionPane.showOptionDialog(parent, "选择导出图像格式：", "导出图片",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
        if (choice != 0 && choice != 1) {
            return;
        }
        String ext = (choice == 0) ? "png" : "jpg";
        String description = (choice == 0) ? "PNG 图片 (*.png)" : "JPG 图片 (*.jpg)";

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("导出当前页为图片");
        chooser.setFileFilter(new FileNameExtensionFilter(description, ext));
        String baseName = getBaseName();
        chooser.setSelectedFile(new File(baseName + "." + ext));
        int result = chooser.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String name = file.getName().toLowerCase();
            if (!name.endsWith("." + ext)) {
                file = new File(file.getParentFile(), file.getName() + "." + ext);
            }
            try {
                exportService.exportSlideAsImage(presentation.getCurrentSlide(), file);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent, "导出失败: " + ex.getMessage(), "错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void exportAsPdf() {
        Object[] options = {"当前页", "整个文档"};
        int choice = JOptionPane.showOptionDialog(parent, "选择导出范围：", "导出为 PDF",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[1]);
        if (choice != 0 && choice != 1) {
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("导出为 PDF");
        chooser.setFileFilter(new FileNameExtensionFilter("PDF 文件 (*.pdf)", "pdf"));
        String baseName = getBaseName();
        chooser.setSelectedFile(new File(baseName + ".pdf"));
        int result = chooser.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".pdf")) {
                file = new File(file.getParentFile(), file.getName() + ".pdf");
            }
            try {
                if (choice == 0) {
                    exportService.exportSingleSlideAsPdf(presentation.getCurrentSlide(), file);
                } else {
                    exportService.exportPresentationAsPdf(presentation, file);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent, "导出失败: " + ex.getMessage(), "错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 新建空白幻灯片并切换到新页。
     */
    public void addNewSlide() {
        presentation.addSlide(new com.JPresent.model.Slide());
        int lastIndex = presentation.getSlides().size() - 1;
        presentation.setCurrentIndex(lastIndex);
        slidePanel.repaint();
        if (thumbnailPanel != null) {
            thumbnailPanel.repaint();
        }
        if (slidePanel.getSelectionController() != null) {
            slidePanel.getSelectionController().setSelectedObject(null);
        }
    }

    private String getBaseName() {
        if (currentFile != null) {
            String name = currentFile.getName();
            int dot = name.lastIndexOf('.');
            if (dot > 0) {
                name = name.substring(0, dot);
            }
            return name;
        }
        return "presentation";
    }
}
