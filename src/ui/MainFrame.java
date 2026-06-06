package ui;

import database.DatabaseManager;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import models.Flight;
import utils.InputValidator;

public class MainFrame extends JFrame {
    private final DatabaseManager db;

    private JTable flightTable;
    private DefaultTableModel tableModel;
    private FlightFormPanel formPanel;
    private ToolbarPanel toolbarPanel;
    private MapDialog mapDialog;

    private final String[] columns = {
        "Flight №", "Airline", "Origin", "Destination", "Date", "Time", "Status"
    };

    public MainFrame(DatabaseManager db) {
        super("Flight Tracker");
        this.db = db;
        buildUI();
        applyTheme();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));

        toolbarPanel = new ToolbarPanel();
        formPanel    = new FlightFormPanel(db);
        mapDialog    = new MapDialog(this, db);
        flightTable  = buildTable();

        JScrollPane scrollPane = new JScrollPane(flightTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(ThemeManager.getBg());

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(ThemeManager.getBg());
        centerPanel.setBorder(ThemeManager.titledBorder("Flight List"));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        wireToolbar();

        add(toolbarPanel, BorderLayout.NORTH);
        add(formPanel,    BorderLayout.WEST);
        add(centerPanel,  BorderLayout.CENTER);

        loadFlights();
    }

    private JTable buildTable() {
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (isRowSelected(row)) {
                    c.setBackground(ThemeManager.getAccent());
                    c.setForeground(Color.WHITE);
                    return c;
                }
                String status = (String) getValueAt(row, 6);
                if (status != null) {
                    c.setBackground(switch (status) {
                        case "Scheduled" -> ThemeManager.getScheduled();
                        case "In Air"    -> ThemeManager.getInAir();
                        case "Landed"    -> ThemeManager.getLanded();
                        case "Delayed"   -> ThemeManager.getDelayed();
                        case "Cancelled" -> ThemeManager.getCancelled();
                        default -> (row % 2 == 0) ? ThemeManager.getTableRow1() : ThemeManager.getTableRow2();
                    });
                } else {
                    c.setBackground((row % 2 == 0) ? ThemeManager.getTableRow1() : ThemeManager.getTableRow2());
                }
                c.setForeground(ThemeManager.getText());
                return c;
            }
        };

        table.setFont(ThemeManager.getFont(13, Font.PLAIN));
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setBackground(ThemeManager.getBg());
        table.setForeground(ThemeManager.getText());
        table.setSelectionBackground(ThemeManager.getAccent());
        table.setSelectionForeground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setBackground(ThemeManager.getTableHeader());
        header.setForeground(ThemeManager.getText());
        header.setFont(ThemeManager.getFont(12, Font.BOLD));
        header.setPreferredSize(new Dimension(0, 38));
        header.setReorderingAllowed(false);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row < 0) return;
                formPanel.populate(
                    (String) table.getValueAt(row, 0),
                    (String) table.getValueAt(row, 1),
                    (String) table.getValueAt(row, 2),
                    (String) table.getValueAt(row, 3),
                    (String) table.getValueAt(row, 6),
                    (String) table.getValueAt(row, 5)
                );
                if (e.getClickCount() == 2)
                    showAirportInfo((String) table.getValueAt(row, 2));
            }
        });

        return table;
    }

    private void wireToolbar() {
        toolbarPanel.setOnAdd(this::addFlight);
        toolbarPanel.setOnEdit(this::editFlight);
        toolbarPanel.setOnDelete(this::deleteFlight);
        toolbarPanel.setOnRefresh(this::loadFlights);
        toolbarPanel.setOnClear(formPanel::clear);
        toolbarPanel.setOnSearch(this::searchFlights);
        toolbarPanel.setOnViewMap(this::viewOnMap);
        formPanel.setOnConfirm(this::confirmAction);
        toolbarPanel.setOnThemeChange(theme -> {
            ThemeManager.setTheme(ThemeManager.Theme.valueOf(theme));
            applyTheme();
        });
    }

    private void applyTheme() {
        getContentPane().setBackground(ThemeManager.getBg());
        toolbarPanel.setBackground(ThemeManager.getBg());
        formPanel.applyTheme();
        flightTable.setBackground(ThemeManager.getBg());
        flightTable.setForeground(ThemeManager.getText());
        flightTable.getTableHeader().setBackground(ThemeManager.getTableHeader());
        flightTable.getTableHeader().setForeground(ThemeManager.getText());
        flightTable.repaint();
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void addFlight() {
        if (!validateForm()) return;
        Flight f = flightFromForm();
        if (db.addFlight(f)) {
            showInfo("Flight added successfully!");
            loadFlights();
            formPanel.clear();
        } else {
            showError("Could not add flight. Flight number may already exist.");
        }
    }

    private void editFlight() {
        int row = flightTable.getSelectedRow();
        if (row < 0) { showError("Please select a flight to edit."); return; }
        if (!validateForm()) return;
        String oldNumber = (String) flightTable.getValueAt(row, 0);
        Flight f = flightFromForm();
        if (db.updateFlight(oldNumber, f)) {
            showInfo("Flight updated!");
            loadFlights();
            formPanel.clear();
        } else {
            showError("Update failed.");
        }
    }

    private void confirmAction() {
        int row = flightTable.getSelectedRow();
        if (row >= 0) {
            editFlight();
        } else {
            addFlight();
        }
    }

    private void deleteFlight() {
        int row = flightTable.getSelectedRow();
        if (row < 0) { showError("Please select a flight to delete."); return; }
        String number = (String) flightTable.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete flight " + number + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION && db.deleteFlight(number)) {
            showInfo("Flight deleted.");
            loadFlights();
            formPanel.clear();
        }
    }

    private void viewOnMap() {
        int row = flightTable.getSelectedRow();
        if (row < 0) { showError("Please select a flight first."); return; }
        mapDialog.showRoute(
            (String) flightTable.getValueAt(row, 2),
            (String) flightTable.getValueAt(row, 3)
        );
    }

    private void searchFlights() {
        String q = toolbarPanel.getSearchQuery();
        populateTable(q.isEmpty() ? db.getAllFlights() : db.searchFlights(q));
    }

    private void loadFlights() { populateTable(db.getAllFlights()); }

    private void populateTable(List<Flight> flights) {
        tableModel.setRowCount(0);
        for (Flight f : flights) {
            tableModel.addRow(new Object[]{
                f.getFlightNumber(), f.getAirline(), f.getOrigin(),
                f.getDestination(), f.getDepartureDate(),
                f.getDepartureTime(), f.getStatus()
            });
        }
    }

    private boolean validateForm() {
        return InputValidator.validateFlight(
                formPanel.getFlightNumber(), formPanel.getAirline(),
                formPanel.getOrigin(), formPanel.getDestination(),
                formPanel.getDepartureDate(), formPanel.getDepartureTime(), this);
    }

    private Flight flightFromForm() {
        return new Flight(
                formPanel.getFlightNumber(), formPanel.getAirline(),
                formPanel.getOrigin(), formPanel.getDestination(),
                formPanel.getDepartureDate(), formPanel.getDepartureTime(),
                formPanel.getStatus());
    }

    private void showAirportInfo(String code) {
        var airport = db.getAirport(code);
        if (airport == null) return;
        JOptionPane.showMessageDialog(this,
                String.format("%s\n%s, %s", airport.getName(), airport.getCity(), airport.getCountry()),
                "Airport Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showInfo(String msg)  {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}