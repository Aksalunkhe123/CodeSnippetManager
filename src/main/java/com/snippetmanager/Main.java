package com.snippetmanager;

import com.snippetmanager.config.DatabaseConfig;
import com.snippetmanager.service.SnippetService;
import com.snippetmanager.service.UserService;
import com.snippetmanager.ui.DashboardUI;
import com.snippetmanager.ui.LoginUI;
import com.snippetmanager.ui.SignupUI;
import com.snippetmanager.utils.ThemeManager;

import javax.swing.*;

public class Main {
    
    private static UserService userService;
    private static SnippetService snippetService;
    private static JFrame currentFrame;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                initializeApplication();
            } catch (Exception e) {
                System.err.println("Fatal error starting application: " + e.getMessage());
                JOptionPane.showMessageDialog(null,
                    "Failed to start application:\n" + e.getMessage(),
                    "Fatal Error",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
    
    private static void initializeApplication() {
        System.out.println("Starting Code Snippet Manager application...");
        
        ThemeManager.setupLookAndFeel();
        
        if (!testDatabaseConnection()) {
            int choice = JOptionPane.showConfirmDialog(null,
                "Cannot connect to MongoDB.\n\n" +
                "Please ensure:\n" +
                "1. MongoDB is installed and running\n" +
                "2. MongoDB is running on localhost:27017\n\n" +
                "Do you want to continue anyway?",
                "Database Connection Error",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (choice != JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
        
        userService = new UserService();
        snippetService = new SnippetService(userService);
        
        showLoginScreen();
        
        System.out.println("Application started successfully");
    }
    
    private static boolean testDatabaseConnection() {
        try {
            return DatabaseConfig.testConnection();
        } catch (Exception e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    private static void showLoginScreen() {
        if (currentFrame != null) {
            currentFrame.dispose();
        }
        
        LoginUI loginUI = new LoginUI(userService, 
            () -> showDashboard(),
            () -> showSignupScreen()
        );
        loginUI.clearFields();
        loginUI.setVisible(true);
        currentFrame = loginUI;
    }
    
    private static void showSignupScreen() {
        if (currentFrame != null) {
            currentFrame.dispose();
        }
        
        SignupUI signupUI = new SignupUI(userService,
            () -> showLoginScreen(),
            () -> showLoginScreen()
        );
        signupUI.clearFields();
        signupUI.setVisible(true);
        currentFrame = signupUI;
    }
    
    private static void showDashboard() {
        if (currentFrame != null) {
            currentFrame.dispose();
        }
        
        DashboardUI dashboardUI = new DashboardUI(userService, snippetService,
            () -> showLoginScreen()
        );
        dashboardUI.setVisible(true);
        currentFrame = dashboardUI;
    }
}
