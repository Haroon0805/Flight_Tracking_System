package ui;

import database.DatabaseManager;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import models.Airport;

public class FlightFormPanel extends JPanel {
    private JTextField flightNumberField;
    private JTextField departureTimeField;
    private JTextField departureDateField;
    private JComboBox<String> airlineComboBox;
    private JComboBox<String> originComboBox;
    private JComboBox<String> destinationComboBox;
    private JComboBox<String> statusComboBox;
    private JButton confirmButton;
    private Runnable onConfirm;

    public FlightFormPanel(DatabaseManager db) {
        super(new BorderLayout());
        initComponents(db);
        layoutComponents();
        applyTheme();
    }

    private void initComponents(DatabaseManager db) {
        flightNumberField  = styledField();
        departureTimeField = styledField();
        departureTimeField.setText("00:00:00");
        departureDateField = styledField();
        departureDateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        String[] airlines = {
            "Emirates", "Qatar Airways", "American Airlines",
            "United Airlines", "Turkish Airlines", "Singapore Airlines",
            "British Airways", "Lufthansa", "Air France", "PIA",
            "Etihad Airways", "Fly Dubai", "Air Arabia",
            "Malaysia Airlines", "Thai Airways", "KLM"
        };
        airlineComboBox = new JComboBox<>(airlines);
        airlineComboBox.setEditable(true);
        airlineComboBox.setFont(ThemeManager.getFont(13, Font.PLAIN));

        String[] codes = db.getAllAirports().stream()
                           .map(Airport::getCode).toArray(String[]::new);
        originComboBox = new JComboBox<>(codes);
        originComboBox.setFont(ThemeManager.getFont(13, Font.PLAIN));

        destinationComboBox = new JComboBox<>(codes);
        destinationComboBox.setFont(ThemeManager.getFont(13, Font.PLAIN));

        statusComboBox = new JComboBox<>(new String[]{
                "Scheduled", "In Air", "Landed", "Delayed", "Cancelled"});
        statusComboBox.setFont(ThemeManager.getFont(13, Font.PLAIN));

        confirmButton = new JButton("Save / Confirm");
        confirmButton.setBackground(ThemeManager.getAccent());
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFont(ThemeManager.getFont(13, Font.BOLD));
        confirmButton.setFocusPainted(false);
        confirmButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        confirmButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        confirmButton.setOpaque(true);
        confirmButton.addActionListener(e -> { if (onConfirm != null) onConfirm.run(); });
    }

    private void layoutComponents() {
        JPanel innerPanel = new JPanel(new GridBagLayout());
        innerPanel.setBackground(ThemeManager.getPanelBg());
        innerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(6, 8, 6, 8);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        addRow(innerPanel, gbc, 0, "Flight Number",             flightNumberField);
        addRow(innerPanel, gbc, 1, "Airline",                   airlineComboBox);
        addRow(innerPanel, gbc, 2, "Origin",                    originComboBox);
        addRow(innerPanel, gbc, 3, "Destination",               destinationComboBox);
        addRow(innerPanel, gbc, 4, "Departure Date (yyyy-MM-dd)", departureDateField);
        addRow(innerPanel, gbc, 5, "Departure Time (HH:MM:SS)", departureTimeField);
        addRow(innerPanel, gbc, 6, "Status",                    statusComboBox);

        // Calendar button next to date field
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 8, 8, 8);
        JButton calBtn = new JButton("Pick Date from Calendar");
        calBtn.setBackground(new Color(60, 60, 90));
        calBtn.setForeground(Color.WHITE);
        calBtn.setFont(ThemeManager.getFont(12, Font.PLAIN));
        calBtn.setFocusPainted(false);
        calBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        calBtn.addActionListener(e -> showCalendar());
        innerPanel.add(calBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 8, 8, 8);
        innerPanel.add(confirmButton, gbc);

        JScrollPane scrollPane = new JScrollPane(innerPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(ThemeManager.getPanelBg());
        scrollPane.getViewport().setBackground(ThemeManager.getPanelBg());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        setBorder(ThemeManager.titledBorder("Add / Edit Flight"));
        setPreferredSize(new Dimension(400, 0));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void showCalendar() {
        JDialog cal = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Pick Date", true);
        cal.setLayout(new BorderLayout(5, 5));
        cal.setSize(300, 280);
        cal.setLocationRelativeTo(this);

        LocalDate[] current = {LocalDate.now()};

        JLabel monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(ThemeManager.getFont(13, Font.BOLD));

        JPanel daysPanel = new JPanel(new GridLayout(0, 7, 2, 2));
        daysPanel.setBackground(ThemeManager.getPanelBg());

        JButton prev = new JButton("<");
        JButton next = new JButton(">");
        prev.setFont(ThemeManager.getFont(12, Font.BOLD));
        next.setFont(ThemeManager.getFont(12, Font.BOLD));

        Runnable render = () -> {
            daysPanel.removeAll();
            monthLabel.setText(current[0].getMonth().toString() + " " + current[0].getYear());
            for (String d : new String[]{"Su","Mo","Tu","We","Th","Fr","Sa"}) {
                JLabel h = new JLabel(d, SwingConstants.CENTER);
                h.setFont(ThemeManager.getFont(11, Font.BOLD));
                h.setForeground(ThemeManager.getSubText());
                daysPanel.add(h);
            }
            LocalDate first = current[0].withDayOfMonth(1);
            int startDay = first.getDayOfWeek().getValue() % 7;
            for (int i = 0; i < startDay; i++) daysPanel.add(new JLabel(""));
            for (int d = 1; d <= current[0].lengthOfMonth(); d++) {
                int day = d;
                JButton btn = new JButton(String.valueOf(d));
                btn.setFont(ThemeManager.getFont(12, Font.PLAIN));
                btn.setFocusPainted(false);
                btn.setBackground(ThemeManager.getPanelBg());
                btn.setForeground(ThemeManager.getText());
                btn.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
                btn.addActionListener(e -> {
                    LocalDate selected = current[0].withDayOfMonth(day);
                    departureDateField.setText(selected.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    cal.dispose();
                });
                daysPanel.add(btn);
            }
            daysPanel.revalidate();
            daysPanel.repaint();
        };

        prev.addActionListener(e -> { current[0] = current[0].minusMonths(1); render.run(); });
        next.addActionListener(e -> { current[0] = current[0].plusMonths(1); render.run(); });

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(ThemeManager.getPanelBg());
        top.add(prev, BorderLayout.WEST);
        top.add(monthLabel, BorderLayout.CENTER);
        top.add(next, BorderLayout.EAST);

        cal.getContentPane().setBackground(ThemeManager.getPanelBg());
        cal.add(top, BorderLayout.NORTH);
        cal.add(daysPanel, BorderLayout.CENTER);

        render.run();
        cal.setVisible(true);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, Component field) {
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel label = new JLabel(labelText);
        label.setForeground(ThemeManager.getSubText());
        label.setFont(ThemeManager.getFont(12, Font.PLAIN));
        panel.add(label, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    private JTextField styledField() {
        JTextField f = new JTextField(12);
        f.setFont(ThemeManager.getFont(13, Font.PLAIN));
        return f;
    }

    public void applyTheme() {
        setBackground(ThemeManager.getPanelBg());
        setBorder(ThemeManager.titledBorder("Add / Edit Flight"));
        confirmButton.setBackground(ThemeManager.getAccent());
    }

    public void setOnConfirm(Runnable r) { onConfirm = r; }

    public String getFlightNumber()  { return flightNumberField.getText().trim(); }
    public String getAirline()       { return airlineComboBox.getSelectedItem().toString().trim(); }
    public String getOrigin()        { return (String) originComboBox.getSelectedItem(); }
    public String getDestination()   { return (String) destinationComboBox.getSelectedItem(); }
    public String getStatus()        { return (String) statusComboBox.getSelectedItem(); }
    public String getDepartureTime() { return departureTimeField.getText().trim(); }
    public String getDepartureDate() { return departureDateField.getText().trim(); }

    public void populate(String flightNumber, String airline, String origin,
                         String destination, String status, String departureTime) {
        flightNumberField.setText(flightNumber);
        airlineComboBox.setSelectedItem(airline);
        originComboBox.setSelectedItem(origin);
        destinationComboBox.setSelectedItem(destination);
        statusComboBox.setSelectedItem(status);
        departureTimeField.setText(departureTime);
    }

    public void clear() {
        flightNumberField.setText("");
        airlineComboBox.setSelectedIndex(0);
        originComboBox.setSelectedIndex(0);
        destinationComboBox.setSelectedIndex(0);
        departureDateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        departureTimeField.setText("00:00:00");
        statusComboBox.setSelectedIndex(0);
    }
}