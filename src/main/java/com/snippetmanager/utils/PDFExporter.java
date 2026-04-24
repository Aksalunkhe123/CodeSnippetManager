package com.snippetmanager.utils;

import com.snippetmanager.model.Snippet;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.PDFont;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PDFExporter {
    
    private static final float MARGIN = 50;
    
    // Cache font instances to avoid recreating them
    private static final PDFont HELVETICA = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDFont HELVETICA_BOLD = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private static final PDFont COURIER = new PDType1Font(Standard14Fonts.FontName.COURIER);
    private static final float FONT_SIZE_TITLE = 16;
    private static final float FONT_SIZE_SUBTITLE = 12;
    private static final float FONT_SIZE_NORMAL = 10;
    private static final float FONT_SIZE_CODE = 9;
    private static final float LINE_SPACING = 15;
    private static final float CODE_LINE_SPACING = 12;
    
    public static boolean exportSnippet(Snippet snippet, Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Snippet as PDF");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files (*.pdf)", "pdf"));
        
        String defaultFileName = sanitizeFileName(snippet.getTitle()) + ".pdf";
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        int result = fileChooser.showSaveDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION) {
            return false;
        }
        
        File file = fileChooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".pdf")) {
            file = new File(file.getAbsolutePath() + ".pdf");
        }
        
        if (file.exists()) {
            int overwrite = JOptionPane.showConfirmDialog(parent,
                "File already exists. Do you want to overwrite it?",
                "Confirm Overwrite",
                JOptionPane.YES_NO_OPTION);
            if (overwrite != JOptionPane.YES_OPTION) {
                return false;
            }
        }
        
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float pageWidth = page.getMediaBox().getWidth();
                float pageHeight = page.getMediaBox().getHeight();
                float yPosition = pageHeight - MARGIN;
                
                yPosition = drawTitle(contentStream, snippet.getTitle(), pageWidth, yPosition);
                yPosition -= LINE_SPACING;
                
                yPosition = drawSubtitle(contentStream, "Language: " + snippet.getProgrammingLanguage(), pageWidth, yPosition);
                yPosition -= LINE_SPACING / 2;
                
                if (snippet.getTags() != null && !snippet.getTags().isEmpty()) {
                    yPosition = drawSubtitle(contentStream, "Tags: " + String.join(", ", snippet.getTags()), pageWidth, yPosition);
                    yPosition -= LINE_SPACING / 2;
                }
                
                if (snippet.getCreatedAt() != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    yPosition = drawSubtitle(contentStream, "Created: " + snippet.getCreatedAt().format(formatter), pageWidth, yPosition);
                    yPosition -= LINE_SPACING;
                }
                
                if (snippet.getDescription() != null && !snippet.getDescription().isEmpty()) {
                    yPosition = drawSubtitle(contentStream, "Description:", pageWidth, yPosition);
                    yPosition -= LINE_SPACING / 2;
                    yPosition = drawWrappedText(contentStream, snippet.getDescription(), MARGIN, yPosition, 
                            pageWidth - 2 * MARGIN, FONT_SIZE_NORMAL);
                    yPosition -= LINE_SPACING;
                }
                
                yPosition = drawSubtitle(contentStream, "Code:", pageWidth, yPosition);
                yPosition -= LINE_SPACING / 2;
                
                drawCode(contentStream, snippet.getCode(), MARGIN, yPosition, pageWidth - 2 * MARGIN);
            }
            
            document.save(file);
            System.out.println("Snippet exported to PDF: " + file.getAbsolutePath());
            
            JOptionPane.showMessageDialog(parent,
                "Snippet exported successfully to:\n" + file.getAbsolutePath(),
                "Export Successful",
                JOptionPane.INFORMATION_MESSAGE);
            
            return true;
            
        } catch (IOException e) {
            System.err.println("Error exporting snippet to PDF: " + e.getMessage());
            JOptionPane.showMessageDialog(parent,
                "Error exporting to PDF:\n" + e.getMessage(),
                "Export Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public static boolean exportSnippets(List<Snippet> snippets, Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Snippets as PDF");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files (*.pdf)", "pdf"));
        fileChooser.setSelectedFile(new File("snippets_export.pdf"));
        
        int result = fileChooser.showSaveDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION) {
            return false;
        }
        
        File file = fileChooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".pdf")) {
            file = new File(file.getAbsolutePath() + ".pdf");
        }
        
        if (file.exists()) {
            int overwrite = JOptionPane.showConfirmDialog(parent,
                "File already exists. Do you want to overwrite it?",
                "Confirm Overwrite",
                JOptionPane.YES_NO_OPTION);
            if (overwrite != JOptionPane.YES_OPTION) {
                return false;
            }
        }
        
        try (PDDocument document = new PDDocument()) {
            for (Snippet snippet : snippets) {
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    float pageWidth = page.getMediaBox().getWidth();
                    float pageHeight = page.getMediaBox().getHeight();
                    float yPosition = pageHeight - MARGIN;
                    
                    yPosition = drawTitle(contentStream, snippet.getTitle(), pageWidth, yPosition);
                    yPosition -= LINE_SPACING;
                    
                    yPosition = drawSubtitle(contentStream, "Language: " + snippet.getProgrammingLanguage(), pageWidth, yPosition);
                    yPosition -= LINE_SPACING / 2;
                    
                    if (snippet.getTags() != null && !snippet.getTags().isEmpty()) {
                        yPosition = drawSubtitle(contentStream, "Tags: " + String.join(", ", snippet.getTags()), pageWidth, yPosition);
                        yPosition -= LINE_SPACING / 2;
                    }
                    
                    if (snippet.getCreatedAt() != null) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                        yPosition = drawSubtitle(contentStream, "Created: " + snippet.getCreatedAt().format(formatter), pageWidth, yPosition);
                        yPosition -= LINE_SPACING;
                    }
                    
                    if (snippet.getDescription() != null && !snippet.getDescription().isEmpty()) {
                        yPosition = drawSubtitle(contentStream, "Description:", pageWidth, yPosition);
                        yPosition -= LINE_SPACING / 2;
                        yPosition = drawWrappedText(contentStream, snippet.getDescription(), MARGIN, yPosition, 
                                pageWidth - 2 * MARGIN, FONT_SIZE_NORMAL);
                        yPosition -= LINE_SPACING;
                    }
                    
                    yPosition = drawSubtitle(contentStream, "Code:", pageWidth, yPosition);
                    yPosition -= LINE_SPACING / 2;
                    
                    drawCode(contentStream, snippet.getCode(), MARGIN, yPosition, pageWidth - 2 * MARGIN);
                }
            }
            
            document.save(file);
            System.out.println(snippets.size() + " snippets exported to PDF: " + file.getAbsolutePath());
            
            JOptionPane.showMessageDialog(parent,
                snippets.size() + " snippets exported successfully to:\n" + file.getAbsolutePath(),
                "Export Successful",
                JOptionPane.INFORMATION_MESSAGE);
            
            return true;
            
        } catch (IOException e) {
            System.err.println("Error exporting snippets to PDF: " + e.getMessage());
            JOptionPane.showMessageDialog(parent,
                "Error exporting to PDF:\n" + e.getMessage(),
                "Export Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private static float drawTitle(PDPageContentStream contentStream, String text, float pageWidth, float y) throws IOException {
        contentStream.beginText();
        contentStream.setFont(HELVETICA_BOLD, FONT_SIZE_TITLE);
        contentStream.newLineAtOffset(MARGIN, y);
        contentStream.showText(text);
        contentStream.endText();
        return y - LINE_SPACING;
    }
    
    private static float drawSubtitle(PDPageContentStream contentStream, String text, float pageWidth, float y) throws IOException {
        contentStream.beginText();
        contentStream.setFont(HELVETICA_BOLD, FONT_SIZE_SUBTITLE);
        contentStream.newLineAtOffset(MARGIN, y);
        contentStream.showText(text);
        contentStream.endText();
        return y - LINE_SPACING;
    }
    
    private static float drawWrappedText(PDPageContentStream contentStream, String text, float x, float y, 
                                          float maxWidth, float fontSize) throws IOException {
        contentStream.beginText();
        contentStream.setFont(HELVETICA, fontSize);
        contentStream.newLineAtOffset(x, y);
        
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        float currentY = y;
        
        for (String word : words) {
            String testLine = line.length() > 0 ? line + " " + word : word;
            float textWidth = HELVETICA.getStringWidth(testLine) / 1000 * fontSize;
            
            if (textWidth > maxWidth && line.length() > 0) {
                contentStream.showText(line.toString());
                contentStream.newLineAtOffset(0, -LINE_SPACING);
                currentY -= LINE_SPACING;
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(testLine);
            }
        }
        
        if (line.length() > 0) {
            contentStream.showText(line.toString());
        }
        
        contentStream.endText();
        return currentY - LINE_SPACING;
    }
    
    private static void drawCode(PDPageContentStream contentStream, String code, float x, float y, 
                                  float maxWidth) throws IOException {
        String[] lines = code.split("\\r?\\n");
        float currentY = y;
        
        contentStream.beginText();
        contentStream.setFont(COURIER, FONT_SIZE_CODE);
        contentStream.newLineAtOffset(x, currentY);
        
        for (String line : lines) {
            if (currentY < MARGIN) {
                break;
            }
            
            String truncatedLine = truncateLine(line, maxWidth, FONT_SIZE_CODE);
            contentStream.showText(truncatedLine);
            contentStream.newLineAtOffset(0, -CODE_LINE_SPACING);
            currentY -= CODE_LINE_SPACING;
        }
        
        contentStream.endText();
    }
    
    private static String truncateLine(String line, float maxWidth, float fontSize) throws IOException {
        float textWidth = COURIER.getStringWidth(line) / 1000 * fontSize;
        
        if (textWidth <= maxWidth) {
            return line;
        }
        
        StringBuilder result = new StringBuilder();
        for (char c : line.toCharArray()) {
            String test = result.toString() + c;
            float testWidth = COURIER.getStringWidth(test) / 1000 * fontSize;
            if (testWidth > maxWidth - 10) {
                break;
            }
            result.append(c);
        }
        return result.toString();
    }
    
    private static String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "snippet";
        }
        return fileName.replaceAll("[^a-zA-Z0-9.-]", "_").substring(0, Math.min(fileName.length(), 50));
    }
}
