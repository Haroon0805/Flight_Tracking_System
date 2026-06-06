package ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ToolbarPanel extends JPanel {
    private JTextField searchField;
    private JComboBox<String> themeSelector;
    private Runnable onAdd, onEdit, onDelete, onRefresh, onClear, onSearch, onViewMap;
    private java.util.function.Consumer<String> onThemeChange;

    public ToolbarPanel() {
        super(new FlowLayout(FlowLayout.LEFT, 6, 8));
        buildUI();
    }

    private void buildUI() {
        setBackground(ThemeManager.getBg());
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.getBorder()));

        add(styledButton("Add Flight",  new Color(34, 197, 94),  e -> { if (onAdd     != null) onAdd.run(); }));
        add(styledButton("Edit",        new Color(251, 191, 36), e -> { if (onEdit    != null) onEdit.run(); }));
        add(styledButton("Delete",      new Color(239, 68, 68),  e -> { if (onDelete  != null) onDelete.run(); }));
        add(styledButton("View on Map", new Color(0, 180, 170),  e -> { if (onViewMap != null) onViewMap.run(); }));
        add(styledButton("Refresh",     new Color(99, 102, 241), e -> { if (onRefresh != null) onRefresh.run(); }));
        add(styledButton("Clear",       new Color(100, 116, 139),e -> { if (onClear   != null) onClear.run(); }));

        add(separator());

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setForeground(ThemeManager.getSubText());
        searchLabel.setFont(ThemeManager.getFont(12, Font.PLAIN));
        add(searchLabel);

        searchField = new JTextField(15);
        searchField.setBackground(ThemeManager.getPanelBg());
        searchField.setForeground(ThemeManager.getText());
        searchField.setCaretColor(ThemeManager.getText());
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getBorder()),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        searchField.setFont(ThemeManager.getFont(13, Font.PLAIN));
        searchField.addActionListener(e -> { if (onSearch != null) onSearch.run(); });
        add(searchField);

        add(styledButton("Search", new Color(99, 102, 241), e -> { if (onSearch != null) onSearch.run(); }));

        add(separator());

        JLabel themeLabel = new JLabel("Theme:");
        themeLabel.setForeground(ThemeManager.getSubText());
        themeLabel.setFont(ThemeManager.getFont(12, Font.PLAIN));
        add(themeLabel);

        themeSelector = new JComboBox<>(new String[]{"Dark", "Light", "Aviation"});
        themeSelector.setBackground(ThemeManager.getPanelBg());
        themeSelector.setForeground(ThemeManager.getText());
        themeSelector.setFont(ThemeManager.getFont(12, Font.PLAIN));
        themeSelector.setPreferredSize(new Dimension(110, 32));
        themeSelector.addActionListener(e -> {
            if (onThemeChange == null) return;
            switch (themeSelector.getSelectedIndex()) {
                case 0 -> onThemeChange.accept("DARK");
                case 1 -> onThemeChange.accept("LIGHT");
                case 2 -> onThemeChange.accept("AVIATION");
            }
        });
        add(themeSelector);
    }

    private JButton styledButton(String text, Color bg, ActionListener action) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(ThemeManager.getFont(12, Font.BOLD));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.addActionListener(action);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    private JSeparator separator() {
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 30));
        sep.setForeground(ThemeManager.getBorder());
        return sep;
    }

    public String getSearchQuery() { return searchField.getText().trim(); }

    public void setOnAdd(Runnable r)     { onAdd = r; }
    public void setOnEdit(Runnable r)    { onEdit = r; }
    public void setOnDelete(Runnable r)  { onDelete = r; }
    public void setOnRefresh(Runnable r) { onRefresh = r; }
    public void setOnClear(Runnable r)   { onClear = r; }
    public void setOnSearch(Runnable r)  { onSearch = r; }
    public void setOnViewMap(Runnable r) { onViewMap = r; }
    public void setOnThemeChange(java.util.function.Consumer<String> c) { onThemeChange = c; }
}