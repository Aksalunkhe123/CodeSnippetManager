package com.snippetmanager.ui;

import com.snippetmanager.model.Snippet;
import com.snippetmanager.model.User;
import com.snippetmanager.service.SnippetService;
import com.snippetmanager.service.UserService;
import com.snippetmanager.utils.DateUtil;
import com.snippetmanager.utils.PDFExporter;
import com.snippetmanager.utils.ThemeManager;
import org.bson.types.ObjectId;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class DashboardUI extends JFrame {
    
    private final UserService userService;
    private final SnippetService snippetService;
    private final Runnable onLogout;
    
    private JTable snippetTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> filterCombo;
    private JComboBox<String> languageFilterCombo;
    private JLabel statusLabel;
    private JLabel statsLabel;
    private List<Snippet> currentSnippets;
    
    private static final String[] COLUMN_NAMES = {"Title", "Language", "Tags", "Created"};
    private static final String[] FILTER_OPTIONS = {"All", "Title", "Language", "Tags"};
    
    public DashboardUI(UserService userService, SnippetService snippetService, Runnable onLogout) {
        this.userService = userService;
        this.snippetService = snippetService;
        this.onLogout = onLogout;
        this.currentSnippets = new ArrayList<>();
        
        User currentUser = userService.getCurrentUser();
        setTitle("Code Snippet Manager - " + (currentUser != null ? currentUser.getUsername() : ""));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));
        
        setJMenuBar(createMenuBar());
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(ThemeManager.DARK_BACKGROUND);
        
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
        mainPanel.add(createStatusPanel(), BorderLayout.SOUTH);
        
        add(mainPanel);
        
        loadSnippets();
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        
        JMenuItem exportAllItem = new JMenuItem("Export All to PDF");
        exportAllItem.addActionListener(e -> exportAllSnippets());
        fileMenu.add(exportAllItem);
        
        fileMenu.addSeparator();
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        JMenu snippetMenu = new JMenu("Snippet");
        snippetMenu.setMnemonic('S');
        
        JMenuItem addItem = new JMenuItem("Add New");
        addItem.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
        addItem.addActionListener(e -> showAddSnippetDialog());
        snippetMenu.add(addItem);
        
        JMenuItem refreshItem = new JMenuItem("Refresh");
        refreshItem.setAccelerator(KeyStroke.getKeyStroke("F5"));
        refreshItem.addActionListener(e -> loadSnippets());
        snippetMenu.add(refreshItem);
        
        JMenu accountMenu = new JMenu("Account");
        accountMenu.setMnemonic('A');
        
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> logout());
        accountMenu.add(logoutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(snippetMenu);
        menuBar.add(accountMenu);
        
        return menuBar;
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(ThemeManager.DARK_BACKGROUND);
        panel.setBorder(new EmptyBorder(5, 5, 15, 5));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        leftPanel.setBackground(ThemeManager.DARK_BACKGROUND);
        
        JButton addButton = new JButton("+ New Snippet");
        addButton.setPreferredSize(new Dimension(130, 35));
        ThemeManager.stylePrimaryButton(addButton);
        addButton.addActionListener(e -> showAddSnippetDialog());
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setPreferredSize(new Dimension(100, 35));
        ThemeManager.styleSecondaryButton(refreshButton);
        refreshButton.addActionListener(e -> loadSnippets());
        
        leftPanel.add(addButton);
        leftPanel.add(refreshButton);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightPanel.setBackground(ThemeManager.DARK_BACKGROUND);
        
        User currentUser = userService.getCurrentUser();
        JLabel welcomeLabel = new JLabel("Welcome, " + (currentUser != null ? currentUser.getUsername() : "User"));
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcomeLabel.setForeground(ThemeManager.DARK_FOREGROUND);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(80, 35));
        ThemeManager.styleDangerButton(logoutButton);
        logoutButton.addActionListener(e -> logout());
        
        rightPanel.add(welcomeLabel);
        rightPanel.add(logoutButton);
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(ThemeManager.DARK_BACKGROUND);
        
        panel.add(createSearchPanel(), BorderLayout.NORTH);
        panel.add(createTablePanel(), BorderLayout.CENTER);
        panel.add(createActionPanel(), BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(ThemeManager.DARK_CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setForeground(ThemeManager.DARK_FOREGROUND);
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(250, 30));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { performSearch(); }
            @Override
            public void removeUpdate(DocumentEvent e) { performSearch(); }
            @Override
            public void changedUpdate(DocumentEvent e) { performSearch(); }
        });
        
        filterCombo = new JComboBox<>(FILTER_OPTIONS);
        filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterCombo.setPreferredSize(new Dimension(100, 30));
        filterCombo.addActionListener(e -> performSearch());
        
        JLabel languageLabel = new JLabel("Filter by Language:");
        languageLabel.setForeground(ThemeManager.DARK_FOREGROUND);
        languageLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        languageFilterCombo = new JComboBox<>(new String[]{"All Languages"});
        languageFilterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        languageFilterCombo.setPreferredSize(new Dimension(150, 30));
        languageFilterCombo.addActionListener(e -> filterByLanguage());
        
        JButton clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        clearButton.addActionListener(e -> clearSearch());
        
        panel.add(searchLabel);
        panel.add(searchField);
        panel.add(filterCombo);
        panel.add(languageLabel);
        panel.add(languageFilterCombo);
        panel.add(clearButton);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeManager.DARK_BACKGROUND);
        
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        snippetTable = new JTable(tableModel);
        snippetTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        snippetTable.setRowHeight(35);
        snippetTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        snippetTable.setShowGrid(false);
        snippetTable.setIntercellSpacing(new Dimension(10, 5));
        snippetTable.setBackground(ThemeManager.DARK_CARD_BACKGROUND);
        snippetTable.setForeground(ThemeManager.DARK_FOREGROUND);
        snippetTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        snippetTable.getTableHeader().setBackground(ThemeManager.DARK_CARD_BACKGROUND);
        snippetTable.getTableHeader().setForeground(ThemeManager.DARK_FOREGROUND);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        snippetTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        snippetTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        
        snippetTable.getColumnModel().getColumn(0).setPreferredWidth(300);
        snippetTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        snippetTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        snippetTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        
        snippetTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewSelectedSnippet();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(snippetTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 1));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createActionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ThemeManager.DARK_BACKGROUND);
        panel.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        JButton viewButton = new JButton("View");
        viewButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        viewButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        ThemeManager.styleSecondaryButton(viewButton);
        viewButton.addActionListener(e -> viewSelectedSnippet());
        
        JButton editButton = new JButton("Edit");
        editButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        editButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        ThemeManager.styleSecondaryButton(editButton);
        editButton.addActionListener(e -> editSelectedSnippet());
        
        JButton deleteButton = new JButton("Delete");
        deleteButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        ThemeManager.styleDangerButton(deleteButton);
        deleteButton.addActionListener(e -> deleteSelectedSnippet());
        
        JButton exportButton = new JButton("Export PDF");
        exportButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        exportButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        ThemeManager.styleSecondaryButton(exportButton);
        exportButton.addActionListener(e -> exportSelectedSnippet());
        
        panel.add(viewButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(editButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(deleteButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(exportButton);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeManager.DARK_CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(80, 80, 80)),
            new EmptyBorder(8, 10, 8, 10)
        ));
        
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(ThemeManager.DARK_FOREGROUND);
        
        statsLabel = new JLabel("0 snippets");
        statsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statsLabel.setForeground(ThemeManager.DARK_FOREGROUND);
        
        panel.add(statusLabel, BorderLayout.WEST);
        panel.add(statsLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void loadSnippets() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        statusLabel.setText("Loading snippets...");
        
        SwingWorker<List<Snippet>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Snippet> doInBackground() {
                return snippetService.getUserSnippets();
            }
            
            @Override
            protected void done() {
                try {
                    currentSnippets = get();
                    refreshTable();
                    updateLanguageFilter();
                    statsLabel.setText(currentSnippets.size() + " snippet" + 
                        (currentSnippets.size() != 1 ? "s" : ""));
                    statusLabel.setText("Ready");
                    setCursor(Cursor.getDefaultCursor());
                } catch (Exception ex) {
                    setCursor(Cursor.getDefaultCursor());
                    statusLabel.setText("Error loading snippets: " + ex.getMessage());
                    statusLabel.setForeground(ThemeManager.ERROR_COLOR);
                    ex.printStackTrace();
                }
            }
        };
        
        worker.execute();
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        
        for (Snippet snippet : currentSnippets) {
            String tags = snippet.getTags() != null ? String.join(", ", snippet.getTags()) : "";
            String created = snippet.getCreatedAt() != null ? 
                DateUtil.formatForDisplay(snippet.getCreatedAt()) : "";
            
            tableModel.addRow(new Object[]{
                snippet.getTitle(),
                snippet.getProgrammingLanguage(),
                tags,
                created
            });
        }
    }
    
    private void updateLanguageFilter() {
        String currentSelection = (String) languageFilterCombo.getSelectedItem();
        
        List<String> languages = snippetService.getAllLanguages();
        languageFilterCombo.removeAllItems();
        languageFilterCombo.addItem("All Languages");
        
        for (String lang : languages) {
            languageFilterCombo.addItem(lang);
        }
        
        if (currentSelection != null) {
            languageFilterCombo.setSelectedItem(currentSelection);
        }
    }
    
    private void performSearch() {
        String query = searchField.getText().trim();
        String filterType = (String) filterCombo.getSelectedItem();
        
        if (query.isEmpty()) {
            currentSnippets = snippetService.getUserSnippets();
        } else {
            currentSnippets = snippetService.searchSnippets(query, 
                filterType != null ? filterType.toLowerCase() : "all");
        }
        
        refreshTable();
        statsLabel.setText(currentSnippets.size() + " result" + 
            (currentSnippets.size() != 1 ? "s" : ""));
    }
    
    private void filterByLanguage() {
        String selectedLanguage = (String) languageFilterCombo.getSelectedItem();
        
        if ("All Languages".equals(selectedLanguage)) {
            currentSnippets = snippetService.getUserSnippets();
        } else {
            currentSnippets = snippetService.filterByLanguage(selectedLanguage);
        }
        
        refreshTable();
        statsLabel.setText(currentSnippets.size() + " snippet" + 
            (currentSnippets.size() != 1 ? "s" : ""));
    }
    
    private void clearSearch() {
        searchField.setText("");
        filterCombo.setSelectedIndex(0);
        languageFilterCombo.setSelectedIndex(0);
        loadSnippets();
    }
    
    private Snippet getSelectedSnippet() {
        int selectedRow = snippetTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < currentSnippets.size()) {
            return currentSnippets.get(selectedRow);
        }
        return null;
    }
    
    private void showAddSnippetDialog() {
        SnippetFormDialog dialog = new SnippetFormDialog(this, snippetService, null, this::loadSnippets);
        dialog.setVisible(true);
    }
    
    private void viewSelectedSnippet() {
        Snippet snippet = getSelectedSnippet();
        if (snippet != null) {
            ViewSnippetDialog dialog = new ViewSnippetDialog(this, snippet);
            dialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select a snippet to view.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void editSelectedSnippet() {
        Snippet snippet = getSelectedSnippet();
        if (snippet != null) {
            SnippetFormDialog dialog = new SnippetFormDialog(this, snippetService, snippet, this::loadSnippets);
            dialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select a snippet to edit.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void deleteSelectedSnippet() {
        Snippet snippet = getSelectedSnippet();
        if (snippet == null) {
            JOptionPane.showMessageDialog(this, 
                "Please select a snippet to delete.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete \"" + snippet.getTitle() + "\"?\n\nThis action cannot be undone.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                snippetService.deleteSnippet(snippet.getId());
                loadSnippets();
                JOptionPane.showMessageDialog(this, 
                    "Snippet deleted successfully.", 
                    "Deleted", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error deleting snippet: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportSelectedSnippet() {
        Snippet snippet = getSelectedSnippet();
        if (snippet != null) {
            PDFExporter.exportSnippet(snippet, this);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select a snippet to export.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void exportAllSnippets() {
        if (currentSnippets.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No snippets to export.", 
                "Export", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        PDFExporter.exportSnippets(currentSnippets, this);
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            userService.logout();
            dispose();
            onLogout.run();
        }
    }
}
