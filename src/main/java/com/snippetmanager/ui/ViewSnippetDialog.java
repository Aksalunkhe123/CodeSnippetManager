package com.snippetmanager.ui;

import com.snippetmanager.model.Snippet;
import com.snippetmanager.utils.DateUtil;
import com.snippetmanager.utils.PDFExporter;
import com.snippetmanager.utils.ThemeManager;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ViewSnippetDialog extends JDialog {
    
    private final Snippet snippet;
    private RSyntaxTextArea codeArea;
    
    public ViewSnippetDialog(JFrame parent, Snippet snippet) {
        super(parent, "View Snippet - " + snippet.getTitle(), true);
        this.snippet = snippet;
        
        setSize(900, 700);
        setLocationRelativeTo(parent);
        setResizable(true);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(ThemeManager.DARK_BACKGROUND);
        
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createCodePanel(), BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ThemeManager.DARK_CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(snippet.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        infoPanel.setBackground(ThemeManager.DARK_CARD_BACKGROUND);
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel languageLabel = new JLabel("Language: " + snippet.getProgrammingLanguage());
        languageLabel.setForeground(ThemeManager.ACCENT_COLOR);
        languageLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        infoPanel.add(languageLabel);
        
        if (snippet.getTags() != null && !snippet.getTags().isEmpty()) {
            JLabel tagsLabel = new JLabel("Tags: " + String.join(", ", snippet.getTags()));
            tagsLabel.setForeground(ThemeManager.DARK_FOREGROUND);
            tagsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            infoPanel.add(tagsLabel);
        }
        
        if (snippet.getCreatedAt() != null) {
            JLabel dateLabel = new JLabel("Created: " + DateUtil.formatForDisplay(snippet.getCreatedAt()));
            dateLabel.setForeground(ThemeManager.DARK_FOREGROUND);
            dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            infoPanel.add(dateLabel);
        }
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(infoPanel);
        
        if (snippet.getDescription() != null && !snippet.getDescription().isEmpty()) {
            panel.add(Box.createVerticalStrut(15));
            
            JLabel descTitleLabel = new JLabel("Description:");
            descTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            descTitleLabel.setForeground(ThemeManager.DARK_FOREGROUND);
            descTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(descTitleLabel);
            
            JTextArea descArea = new JTextArea(snippet.getDescription());
            descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            descArea.setForeground(ThemeManager.DARK_FOREGROUND);
            descArea.setBackground(ThemeManager.DARK_CARD_BACKGROUND);
            descArea.setLineWrap(true);
            descArea.setWrapStyleWord(true);
            descArea.setEditable(false);
            descArea.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
            descArea.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(descArea);
        }
        
        return panel;
    }
    
    private JPanel createCodePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeManager.DARK_BACKGROUND);
        
        JLabel codeLabel = new JLabel("Code:");
        codeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        codeLabel.setForeground(ThemeManager.DARK_FOREGROUND);
        panel.add(codeLabel, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(5), BorderLayout.CENTER);
        
        codeArea = new RSyntaxTextArea(25, 80);
        codeArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        codeArea.setText(snippet.getCode());
        codeArea.setEditable(false);
        codeArea.setLineWrap(false);
        codeArea.setTabSize(4);
        
        String syntaxStyle = getSyntaxStyle(snippet.getProgrammingLanguage());
        codeArea.setSyntaxEditingStyle(syntaxStyle);
        
        RTextScrollPane scrollPane = new RTextScrollPane(codeArea);
        scrollPane.setLineNumbersEnabled(true);
        scrollPane.setFoldIndicatorEnabled(true);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(ThemeManager.DARK_BACKGROUND);
        
        JButton exportButton = new JButton("Export to PDF");
        exportButton.setPreferredSize(new Dimension(120, 35));
        ThemeManager.styleSecondaryButton(exportButton);
        exportButton.addActionListener(e -> PDFExporter.exportSnippet(snippet, this));
        
        JButton closeButton = new JButton("Close");
        closeButton.setPreferredSize(new Dimension(100, 35));
        ThemeManager.stylePrimaryButton(closeButton);
        closeButton.addActionListener(e -> dispose());
        
        panel.add(exportButton);
        panel.add(closeButton);
        
        return panel;
    }
    
    private String getSyntaxStyle(String language) {
        if (language == null) return SyntaxConstants.SYNTAX_STYLE_NONE;
        
        return switch (language.toLowerCase()) {
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
            case "json" -> SyntaxConstants.SYNTAX_STYLE_JSON;
            case "xml" -> SyntaxConstants.SYNTAX_STYLE_XML;
            case "yaml" -> SyntaxConstants.SYNTAX_STYLE_YAML;
            case "markdown" -> SyntaxConstants.SYNTAX_STYLE_MARKDOWN;
            default -> SyntaxConstants.SYNTAX_STYLE_NONE;
        };
    }
}
