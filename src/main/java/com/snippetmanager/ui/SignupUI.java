package com.snippetmanager.ui;

import com.snippetmanager.model.User;
import com.snippetmanager.service.UserService;
import com.snippetmanager.utils.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SignupUI extends JFrame {
    
    private final UserService userService;
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JLabel statusLabel;
    private final Runnable onSignupSuccess;
    private final Runnable onLoginClick;
    
    public SignupUI(UserService userService, Runnable onSignupSuccess, Runnable onLoginClick) {
        this.userService = userService;
        this.onSignupSuccess = onSignupSuccess;
        this.onLoginClick = onLoginClick;
        
        setTitle("Code Snippet Manager - Sign Up");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 600);
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
        
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Join Code Snippet Manager today");
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
        
        JLabel usernameLabel = new JLabel("Username *");
        usernameLabel.setForeground(ThemeManager.DARK_FOREGROUND);
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);
        
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(300, 40));
        usernameField.setToolTipText("3-30 characters, letters, numbers and underscores only");
        gbc.gridy = 1;
        formPanel.add(usernameField, gbc);
        
        JLabel emailLabel = new JLabel("Email *");
        emailLabel.setForeground(ThemeManager.DARK_FOREGROUND);
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = 2;
        formPanel.add(emailLabel, gbc);
        
        emailField = new JTextField(20);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setPreferredSize(new Dimension(300, 40));
        gbc.gridy = 3;
        formPanel.add(emailField, gbc);
        
        JLabel passwordLabel = new JLabel("Password *");
        passwordLabel.setForeground(ThemeManager.DARK_FOREGROUND);
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = 4;
        formPanel.add(passwordLabel, gbc);
        
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(300, 40));
        passwordField.setToolTipText("Minimum 6 characters");
        gbc.gridy = 5;
        formPanel.add(passwordField, gbc);
        
        JLabel confirmPasswordLabel = new JLabel("Confirm Password *");
        confirmPasswordLabel.setForeground(ThemeManager.DARK_FOREGROUND);
        confirmPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = 6;
        formPanel.add(confirmPasswordLabel, gbc);
        
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmPasswordField.setPreferredSize(new Dimension(300, 40));
        confirmPasswordField.addActionListener(this::performSignup);
        gbc.gridy = 7;
        formPanel.add(confirmPasswordField, gbc);
        
        panel.add(formPanel);
        panel.add(Box.createVerticalStrut(20));
        
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(ThemeManager.ERROR_COLOR);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(statusLabel);
        panel.add(Box.createVerticalStrut(10));
        
        JButton signupButton = new JButton("Create Account");
        signupButton.setPreferredSize(new Dimension(300, 45));
        ThemeManager.stylePrimaryButton(signupButton);
        signupButton.addActionListener(this::performSignup);
        signupButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(signupButton);
        panel.add(Box.createVerticalStrut(20));
        
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginPanel.setBackground(ThemeManager.DARK_BACKGROUND);
        
        JLabel haveAccountLabel = new JLabel("Already have an account?");
        haveAccountLabel.setForeground(ThemeManager.DARK_FOREGROUND);
        haveAccountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        JButton loginButton = new JButton("Sign In");
        loginButton.setForeground(ThemeManager.ACCENT_COLOR);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginButton.setBorderPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> onLoginClick.run());
        
        loginPanel.add(haveAccountLabel);
        loginPanel.add(loginButton);
        loginPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(loginPanel);
        
        return panel;
    }
    
    private void performSignup(ActionEvent e) {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        if (username.isEmpty()) {
            statusLabel.setText("Please enter a username");
            usernameField.requestFocus();
            return;
        }
        
        if (email.isEmpty()) {
            statusLabel.setText("Please enter your email");
            emailField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            statusLabel.setText("Please enter a password");
            passwordField.requestFocus();
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            statusLabel.setText("Passwords do not match");
            confirmPasswordField.setText("");
            confirmPasswordField.requestFocus();
            return;
        }
        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        statusLabel.setText("Creating account...");
        statusLabel.setForeground(ThemeManager.ACCENT_COLOR);
        
        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                return userService.signup(username, email, password);
            }
            
            @Override
            protected void done() {
                try {
                    User user = get();
                    setCursor(Cursor.getDefaultCursor());
                    statusLabel.setText("Account created successfully!");
                    statusLabel.setForeground(ThemeManager.SUCCESS_COLOR);
                    System.out.println("New user registered: " + username);
                    
                    JOptionPane.showMessageDialog(SignupUI.this,
                        "Account created successfully!\nPlease sign in with your credentials.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    dispose();
                    onSignupSuccess.run();
                    
                } catch (Exception ex) {
                    setCursor(Cursor.getDefaultCursor());
                    String errorMessage = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    statusLabel.setText("Error: " + errorMessage);
                    statusLabel.setForeground(ThemeManager.ERROR_COLOR);
                    ex.printStackTrace();
                }
            }
        };
        
        worker.execute();
    }
    
    public void clearFields() {
        usernameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        statusLabel.setText(" ");
    }
}
