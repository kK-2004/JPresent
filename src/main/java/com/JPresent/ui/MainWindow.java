package com.JPresent.ui;

import com.JPresent.controller.FileController;
import com.JPresent.controller.SelectionController;
import com.JPresent.controller.DrawingController;
import com.JPresent.model.Presentation;
import com.JPresent.service.ExportService;
import com.JPresent.service.FileService;
import com.JPresent.service.UndoRedoService;

import javax.swing.*;
import java.awt.BorderLayout;
import java.io.IOException;

/**
 * 主窗口。
 */
public class MainWindow extends JFrame {

    private final FileService fileService;
    private final ExportService exportService;
    private final UndoRedoService undoRedoService;
    private final Presentation presentation;
    private final SlidePanel slidePanel;
    private final ThumbnailPanel thumbnailPanel;
    private final DrawingController drawingController;
    private final SelectionController selectionController;
    private final FileController fileController;

    public MainWindow() {
        super(java.util.ResourceBundle.getBundle("config").getString("app.title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        ModernTheme.apply();
        getContentPane().setBackground(ModernTheme.BACKGROUND);

        this.presentation = new Presentation();
        this.fileService = new FileService();
        this.exportService = new ExportService();
        this.undoRedoService = new UndoRedoService();

        this.slidePanel = new SlidePanel(presentation);
        this.slidePanel.setUndoRedoService(undoRedoService);
        this.thumbnailPanel = new ThumbnailPanel(presentation, slidePanel);
        this.slidePanel.setThumbnailPanel(thumbnailPanel);

        this.drawingController = new DrawingController(presentation, slidePanel);
        this.selectionController = new SelectionController(presentation, slidePanel);
        this.fileController = new FileController(this, presentation, fileService, exportService, slidePanel, thumbnailPanel);

        slidePanel.setControllers(drawingController, selectionController);

        setLayout(new BorderLayout());

        AppMenuBar menuBar = new AppMenuBar(this, fileController, drawingController);
        setJMenuBar(menuBar);

        AppToolBar toolBar = new AppToolBar(drawingController);
        add(toolBar, BorderLayout.NORTH);

        JScrollPane slideScroll = new JScrollPane(slidePanel);
        slideScroll.setBorder(null);
        slideScroll.getViewport().setBackground(ModernTheme.BACKGROUND);

        JScrollPane thumbScroll = new JScrollPane(thumbnailPanel);
        thumbScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        thumbScroll.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ModernTheme.BORDER));
        thumbScroll.getViewport().setBackground(ModernTheme.SIDEBAR_BG);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, thumbScroll, slideScroll);
        splitPane.setDividerLocation(220);
        splitPane.setDividerSize(1);
        splitPane.setBorder(null);
        splitPane.setContinuousLayout(true);

        add(splitPane, BorderLayout.CENTER);
    }

    public FileController getFileController() {
        return fileController;
    }

    public Presentation getPresentation() {
        return presentation;
    }

    public SlidePanel getSlidePanel() {
        return slidePanel;
    }

    public ThumbnailPanel getThumbnailPanel() {
        return thumbnailPanel;
    }

    /**
     * 从第一张开始播放幻灯片。
     */

    public void startSlideShowFromBeginning() {
        SlideShowWindow window = new SlideShowWindow(
                this,
                presentation,
                slidePanel,
                thumbnailPanel,
                0
        );
        window.start();
    }

    public void startSlideShowFromCurrent() {
        SlideShowWindow window = new SlideShowWindow(
                this,
                presentation,
                slidePanel,
                thumbnailPanel,
                presentation.getCurrentIndex()
        );
        window.start();
    }

    /**
     * 撤销上一步操作。
     */
    public void undo() {
        try {
            undoRedoService.undo(presentation);
            if (slidePanel.getSelectionController() != null) {
                slidePanel.getSelectionController().setSelectedObject(null);
            }
            slidePanel.repaint();
            if (thumbnailPanel != null) {
                thumbnailPanel.repaint();
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "撤销失败: " + ex.getMessage(), "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 重做上一步撤销。
     */
    public void redo() {
        try {
            undoRedoService.redo(presentation);
            if (slidePanel.getSelectionController() != null) {
                slidePanel.getSelectionController().setSelectedObject(null);
            }
            slidePanel.repaint();
            if (thumbnailPanel != null) {
                thumbnailPanel.repaint();
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "重做失败: " + ex.getMessage(), "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
