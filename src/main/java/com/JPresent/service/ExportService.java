package com.JPresent.service;

import com.JPresent.model.Presentation;
import com.JPresent.model.Slide;
import com.JPresent.model.SlideObject;
import com.JPresent.model.SlideConstants;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 导出服务。
 * 使用Microsoft PowerPoint标准16:9尺寸（1920x1080）导出
 */
public class ExportService {

    public void exportSlideAsImage(Slide slide, File file) throws IOException {
        BufferedImage image = new BufferedImage(
            SlideConstants.SLIDE_WIDTH,
            SlideConstants.SLIDE_HEIGHT,
            BufferedImage.TYPE_INT_RGB
        );
        Graphics2D g2d = image.createGraphics();
        // 启用抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Color bg = slide.getBackgroundColor();
        if (bg == null) {
            bg = Color.WHITE;
        }
        g2d.setColor(bg);
        g2d.fillRect(0, 0, SlideConstants.SLIDE_WIDTH, SlideConstants.SLIDE_HEIGHT);
        for (SlideObject object : slide.getObjects()) {
            object.draw(g2d);
        }
        g2d.dispose();

        String format = "png";
        String name = file.getName().toLowerCase();
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
            format = "jpg";
        }
        ImageIO.write(image, format, file);
    }

    public void exportPresentationAsPdf(Presentation presentation, File file) throws IOException {
        try (PDDocument document = new PDDocument()) {
            for (Slide slide : presentation.getSlides()) {
                // 使用16:9宽屏尺寸的PDF页面
                PDPage page = new PDPage(new PDRectangle(
                    SlideConstants.SLIDE_WIDTH * 0.75f,
                    SlideConstants.SLIDE_HEIGHT * 0.75f
                ));
                document.addPage(page);

                BufferedImage image = new BufferedImage(
                    SlideConstants.SLIDE_WIDTH,
                    SlideConstants.SLIDE_HEIGHT,
                    BufferedImage.TYPE_INT_RGB
                );
                Graphics2D g2d = image.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                Color bg = slide.getBackgroundColor();
                if (bg == null) {
                    bg = Color.WHITE;
                }
                g2d.setColor(bg);
                g2d.fillRect(0, 0, SlideConstants.SLIDE_WIDTH, SlideConstants.SLIDE_HEIGHT);
                for (SlideObject object : slide.getObjects()) {
                    object.draw(g2d);
                }
                g2d.dispose();

                var pdImage = LosslessFactory.createFromImage(document, image);
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    float pageWidth = page.getMediaBox().getWidth();
                    float pageHeight = page.getMediaBox().getHeight();
                    contentStream.drawImage(pdImage, 0, 0, pageWidth, pageHeight);
                }
            }
            document.save(file);
        }
    }

    public void exportSingleSlideAsPdf(Slide slide, File file) throws IOException {
        try (PDDocument document = new PDDocument()) {
            // 使用16:9宽屏尺寸的PDF页面
            PDPage page = new PDPage(new PDRectangle(
                SlideConstants.SLIDE_WIDTH * 0.75f,
                SlideConstants.SLIDE_HEIGHT * 0.75f
            ));
            document.addPage(page);

            BufferedImage image = new BufferedImage(
                SlideConstants.SLIDE_WIDTH,
                SlideConstants.SLIDE_HEIGHT,
                BufferedImage.TYPE_INT_RGB
            );
            Graphics2D g2d = image.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            Color bg = slide.getBackgroundColor();
            if (bg == null) {
                bg = Color.WHITE;
            }
            g2d.setColor(bg);
            g2d.fillRect(0, 0, SlideConstants.SLIDE_WIDTH, SlideConstants.SLIDE_HEIGHT);
            for (SlideObject object : slide.getObjects()) {
                object.draw(g2d);
            }
            g2d.dispose();

            var pdImage = LosslessFactory.createFromImage(document, image);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float pageWidth = page.getMediaBox().getWidth();
                float pageHeight = page.getMediaBox().getHeight();
                contentStream.drawImage(pdImage, 0, 0, pageWidth, pageHeight);
            }
            document.save(file);
        }
    }
}
