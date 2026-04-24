package com.snippetmanager.ui;

import com.snippetmanager.model.Snippet;
import com.snippetmanager.service.SnippetService;
import com.snippetmanager.utils.ThemeManager;
import org.bson.types.ObjectId;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class SnippetFormDialog extends JDialog {
    
    private final SnippetService snippetService;
    private final Snippet existingSnippet;
    private JTextField titleField;
    private JComboBox<String> languageCombo;
    private RSyntaxTextArea codeArea;
    private JTextField tagsField;
    private JTextArea descriptionArea;
    private JLabel statusLabel;
    private final Runnable onSaveCallback;
    
    private static final String[] LANGUAGES = {
        "Java", "Python", "JavaScript", "TypeScript", "C", "C++", "C#", 
        "Go", "Rust", "Ruby", "PHP", "Swift", "Kotlin", "Scala",
        "HTML", "CSS", "SQL", "Bash", "PowerShell", "JSON", "XML",
        "YAML", "Markdown", "Dockerfile", "Groovy", "Perl", "Lua",
        "R", "MATLAB", "VBA", "Assembly", "COBOL", "Fortran"
    };
    
    public SnippetFormDialog(JFrame parent, SnippetService snippetService, 
                             Snippet existingSnippet, Runnable onSaveCallback) {
        super(parent, existingSnippet == null ? "Add New Snippet" : "Edit Snippet", true);
        this.snippetService = snippetService;
        this.existingSnippet = existingSnippet;
        this.onSaveCallback = onSaveCallback;
        
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setResizable(true);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(ThemeManager.DARK_BACKGROUND);
        
        mainPanel.add(createFormPanel(), BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);
        
        add(mainPanel);
        
        if (existingSnippet != null) {
            populateFields();
        }
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ThemeManager.DARK_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        JLabel titleLabel = new JLabel("Title *");
        titleLabel.setForeground(ThemeManager.DARK_FOREGROUND);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(titleLabel, gbc);
        
        titleField = new JTextField(30);
        titleField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0;
        panel.add(titleField, gbc);
        
        JLabel languageLabel = new JLabel("Language *");
        languageLabel.setForeground(ThemeManager.DARK_FOREGROUND);
        languageLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(languageLabel, gbc);
        
        languageCombo = new JComboBox<>(LANGUAGES);
        languageCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        languageCombo.setEditable(true);
        languageCombo.addActionListener(e -> updateSyntaxHighlighting());
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1.0;
        panel.add(languageCombo, gbc);
        
        JLabel tagsLabel = new JLabel("Tags");
        tagsLabel.setForeground(ThemeManager.DARK_FOREGROUND);
        tagsLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(tagsLabel, gbc);
        
        tagsField = new JTextField(30);
        tagsField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tagsField.setToolTipText("Enter tags separated by commas (e.g., java, spring, web)");
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.weightx = 1.0;
        panel.add(tagsField, gbc);
        
        JLabel descriptionLabel = new JLabel("Description");
        descriptionLabel.setForeground(ThemeManager.DARK_FOREGROUND);
        descriptionLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weightx = 0;
        panel.add(descriptionLabel, gbc);
        
        descriptionArea = new JTextArea(3, 30);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(descScroll, gbc);
        
        JLabel codeLabel = new JLabel("Code *");
        codeLabel.setForeground(ThemeManager.DARK_FOREGROUND);
        codeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.weightx = 0;
        gbc.weighty = 0;
        panel.add(codeLabel, gbc);
        
        codeArea = new RSyntaxTextArea(20, 80);
        codeArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        codeArea.setLineWrap(false);
        codeArea.setTabSize(4);
        codeArea.setCodeFoldingEnabled(true);
        codeArea.setClearWhitespaceLinesEnabled(false);
        updateSyntaxHighlighting();
        
        RTextScrollPane codeScroll = new RTextScrollPane(codeArea);
        codeScroll.setLineNumbersEnabled(true);
        codeScroll.setFoldIndicatorEnabled(true);
        
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(codeScroll, gbc);
        
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(ThemeManager.ERROR_COLOR);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        panel.add(statusLabel, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(ThemeManager.DARK_BACKGROUND);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(100, 35));
        ThemeManager.styleSecondaryButton(cancelButton);
        cancelButton.addActionListener(e -> dispose());
        
        JButton saveButton = new JButton(existingSnippet == null ? "Save" : "Update");
        saveButton.setPreferredSize(new Dimension(100, 35));
        ThemeManager.stylePrimaryButton(saveButton);
        saveButton.addActionListener(e -> saveSnippet());
        
        panel.add(cancelButton);
        panel.add(saveButton);
        
        return panel;
    }
    
    private void updateSyntaxHighlighting() {
        String language = (String) languageCombo.getSelectedItem();
        if (language == null) return;
        
        String syntaxStyle = switch (language.toLowerCase()) {
            case "java" -> SyntaxConstants.SYNTAX_STYLE_JAVA;
            case "python" -> SyntaxConstants.SYNTAX_STYLE_PYTHON;
            case "javascript" -> SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT;
            case "typescript" -> SyntaxConstants.SYNTAX_STYLE_TYPESCRIPT;
            case "c" -> SyntaxConstants.SYNTAX_STYLE_C;
            case "c++" -> SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS;
            case "c#" -> SyntaxConstants.SYNTAX_STYLE_CSHARP;
            case "go" -> SyntaxConstants.SYNTAX_STYLE_GO;
            case "rust" -> SyntaxConstants.SYNTAX_STYLE_NONE;
            case "ruby" -> SyntaxConstants.SYNTAX_STYLE_RUBY;
            case "php" -> SyntaxConstants.SYNTAX_STYLE_PHP;
            case "swift" -> SyntaxConstants.SYNTAX_STYLE_NONE;
            case "kotlin" -> SyntaxConstants.SYNTAX_STYLE_KOTLIN;
            case "scala" -> SyntaxConstants.SYNTAX_STYLE_SCALA;
            case "html" -> SyntaxConstants.SYNTAX_STYLE_HTML;
            case "css" -> SyntaxConstants.SYNTAX_STYLE_CSS;
            case "sql" -> SyntaxConstants.SYNTAX_STYLE_SQL;
            case "bash" -> SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL;
            case "powershell" -> SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL;
            case "json" -> SyntaxConstants.SYNTAX_STYLE_JSON;
            case "xml" -> SyntaxConstants.SYNTAX_STYLE_XML;
            case "yaml" -> SyntaxConstants.SYNTAX_STYLE_YAML;
            case "markdown" -> SyntaxConstants.SYNTAX_STYLE_MARKDOWN;
            case "dockerfile" -> SyntaxConstants.SYNTAX_STYLE_DOCKERFILE;
            case "groovy" -> SyntaxConstants.SYNTAX_STYLE_GROOVY;
            case "perl" -> SyntaxConstants.SYNTAX_STYLE_PERL;
            case "lua" -> SyntaxConstants.SYNTAX_STYLE_LUA;
            case "r" -> SyntaxConstants.SYNTAX_STYLE_NONE;
            case "matlab" -> SyntaxConstants.SYNTAX_STYLE_NONE;
            case "vba" -> SyntaxConstants.SYNTAX_STYLE_VISUAL_BASIC;
            case "assembly" -> SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_X86;
            default -> SyntaxConstants.SYNTAX_STYLE_NONE;
        };
        
        codeArea.setSyntaxEditingStyle(syntaxStyle);
    }
    
    private void populateFields() {
        titleField.setText(existingSnippet.getTitle());
        languageCombo.setSelectedItem(existingSnippet.getProgrammingLanguage());
        codeArea.setText(existingSnippet.getCode());
        descriptionArea.setText(existingSnippet.getDescription());
        
        if (existingSnippet.getTags() != null) {
            tagsField.setText(String.join(", ", existingSnippet.getTags()));
        }
    }
    
    private void saveSnippet() {
        String title = titleField.getText().trim();
        String language = (String) languageCombo.getSelectedItem();
        String code = codeArea.getText();
        String tags = tagsField.getText().trim();
        String description = descriptionArea.getText().trim();
        
        if (title.isEmpty()) {
            statusLabel.setText("Title is required");
            titleField.requestFocus();
            return;
        }
        
        if (language == null || language.trim().isEmpty()) {
            statusLabel.setText("Programming language is required");
            languageCombo.requestFocus();
            return;
        }
        
        if (code.trim().isEmpty()) {
            statusLabel.setText("Code is required");
            codeArea.requestFocus();
            return;
        }
        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        statusLabel.setText("Saving...");
        statusLabel.setForeground(ThemeManager.ACCENT_COLOR);
        
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (existingSnippet == null) {
                    snippetService.createSnippet(title, language, code, tags, description);
                } else {
                    snippetService.updateSnippet(existingSnippet.getId(), title, language, code, tags, description);
                }
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    setCursor(Cursor.getDefaultCursor());
                    dispose();
                    if (onSaveCallback != null) {
                        onSaveCallback.run();
                    }
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
}
