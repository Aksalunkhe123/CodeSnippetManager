package com.snippetmanager.ui;

import com.snippetmanager.model.User;
import com.snippetmanager.service.UserService;
import com.snippetmanager.utils.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Optional;

public class LoginUI extends JFrame {
    
    private final UserService userService;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    private final Runnable onLoginSuccess;
    private final Runnable onSignupClick;
    
    public LoginUI(UserService userService, Runnable onLoginSuccess, Runnable onSignupClick) {
        this.userService = userService;
        this.onLoginSuccess = onLoginSuccess;
        this.onSignupClick = onSignupClick;
        
        setTitle("Code Snippet Manager - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        mainPanel.setBackground(ThemeManager.DARK_BACKGROUND);
        
        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ThemeManager.DARK_BACKGROUND);
        
        JLabel titleLabel = new JLabel("Code Snippet Manager");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Sign in to your account");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(ThemeManager.DARK_FOREGROUND);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(subtitleLabel);
        panel.add(Box.createVerticalStrut(30));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(ThemeManager.DARK_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setForeground(ThemeManager.DARK_FOREGROUND);
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);
        
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(300, 40));
        gbc.gridy = 1;
        formPanel.add(usernameField, gbc);
        
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setForeground(ThemeManager.DARK_FOREGROUND);
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = 2;
        formPanel.add(passwordLabel, gbc);
        
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(300, 40));
        passwordField.addActionListener(this::performLogin);
        gbc.gridy = 3;
        formPanel.add(passwordField, gbc);
        
        panel.add(formPanel);
        panel.add(Box.createVerticalStrut(20));
        
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(ThemeManager.ERROR_COLOR);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(statusLabel);
        panel.add(Box.createVerticalStrut(10));
        
        JButton loginButton = new JButton("Sign In");
        loginButton.setPreferredSize(new Dimension(300, 45));
        ThemeManager.stylePrimaryButton(loginButton);
        loginButton.addActionListener(this::performLogin);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(loginButton);
        panel.add(Box.createVerticalStrut(20));
        
        JPanel signupPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        signupPanel.setBackground(ThemeManager.DARK_BACKGROUND);
        
        JLabel noAccountLabel = new JLabel("Don't have an account?");
        noAccountLabel.setForeground(ThemeManager.DARK_FOREGROUND);
        noAccountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        JButton signupButton = new JButton("Sign Up");
        signupButton.setForeground(ThemeManager.ACCENT_COLOR);
        signupButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        signupButton.setBorderPainted(false);
        signupButton.setContentAreaFilled(false);
        signupButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupButton.addActionListener(e -> onSignupClick.run());
        
        signupPanel.add(noAccountLabel);
        signupPanel.add(signupButton);
        signupPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(signupPanel);
        
        return panel;
    }
    
    private void performLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty()) {
            statusLabel.setText("Please enter your username");
            usernameField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            statusLabel.setText("Please enter your password");
            passwordField.requestFocus();
            return;
        }
        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        statusLabel.setText("Signing in...");
        statusLabel.setForeground(ThemeManager.ACCENT_COLOR);
        
        SwingWorker<Optional<User>, Void> worker = new SwingWorker<>() {
            @Override
            protected Optional<User> doInBackground() {
                return userService.login(username, password);
            }
            
            @Override
            protected void done() {
                try {
                    Optional<User> result = get();
                    setCursor(Cursor.getDefaultCursor());
                    
                    if (result.isPresent()) {
                        statusLabel.setText("Login successful!");
                        statusLabel.setForeground(ThemeManager.SUCCESS_COLOR);
                        System.out.println("User " + username + " logged in successfully");
                        dispose();
                        onLoginSuccess.run();
                    } else {
                        statusLabel.setText("Invalid username or password");
                        statusLabel.setForeground(ThemeManager.ERROR_COLOR);
                        passwordField.setText("");
                        passwordField.requestFocus();
                    }
                } catch (Exception ex) {
                    setCursor(Cursor.getDefaultCursor());
                    statusLabel.setText("Error: " + ex.getMessage());
                    statusLabel.setForeground(ThemeManager.ERROR_COLOR);
                    ex.printStackTrace();
                }
            }
        };
        
        worker.execute();
    }
    
    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
    }
}
