package com.snippetmanager.utils;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class ThemeManager {
    
    public static final Color DARK_BACKGROUND = new Color(43, 43, 43);
    public static final Color DARK_FOREGROUND = new Color(187, 187, 187);
    public static final Color DARK_CARD_BACKGROUND = new Color(60, 63, 65);
    public static final Color ACCENT_COLOR = new Color(75, 110, 175);
    public static final Color ACCENT_HOVER = new Color(88, 130, 200);
    public static final Color SUCCESS_COLOR = new Color(75, 175, 80);
    public static final Color ERROR_COLOR = new Color(220, 53, 69);
    public static final Color WARNING_COLOR = new Color(255, 193, 7);
    
    private static boolean darkMode = true;
    
    private ThemeManager() {
    }
    
    public static void setupLookAndFeel() {
        try {
            if (darkMode) {
                FlatDarkLaf.setup();
            } else {
                FlatLightLaf.setup();
            }
            
            FlatLaf.setUseNativeWindowDecorations(true);
            
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("ProgressBar.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            
            System.out.println("Look and Feel set up successfully");
        } catch (Exception e) {
            System.err.println("Error setting up Look and Feel: " + e.getMessage());
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Failed to set fallback Look and Feel: " + ex.getMessage());
            }
        }
    }
    
    public static void toggleTheme() {
        darkMode = !darkMode;
        setupLookAndFeel();
        updateAllWindows();
    }
    
    private static void updateAllWindows() {
        for (Window window : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(window);
        }
    }
    
    public static boolean isDarkMode() {
        return darkMode;
    }
    
    public static Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        );
    }
    
    public static Border createPanelBorder(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
            title,
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            darkMode ? DARK_FOREGROUND : Color.BLACK
        );
    }
    
    public static void stylePrimaryButton(JButton button) {
        button.setBackground(ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(ACCENT_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(ACCENT_COLOR);
            }
        });
    }
    
    public static void styleSecondaryButton(JButton button) {
        button.setBackground(DARK_CARD_BACKGROUND);
        button.setForeground(DARK_FOREGROUND);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    public static void styleDangerButton(JButton button) {
        button.setBackground(ERROR_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
