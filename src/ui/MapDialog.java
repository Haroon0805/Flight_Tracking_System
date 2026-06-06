package ui;

import database.DatabaseManager;
import models.Airport;
import org.openstreetmap.gui.jmapviewer.*;

import javax.swing.*;
import java.awt.*;

public class MapDialog extends JDialog {
    private final JMapViewer mapViewer;
    private final DatabaseManager db;

    public MapDialog(JFrame parent, DatabaseManager db) {
        super(parent, "Flight Route Map", false);
        this.db = db;
        this.mapViewer = new JMapViewer();
        buildUI();
        setSize(800, 600);
        setLocationRelativeTo(parent);
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeManager.getBg());
        mapViewer.setZoom(3);
        add(mapViewer, BorderLayout.CENTER);
    }

    public void showRoute(String originCode, String destinationCode) {
        Airport origin      = db.getAirport(originCode);
        Airport destination = db.getAirport(destinationCode);
        if (origin == null || destination == null) return;

        mapViewer.getMapMarkerList().clear();

        MapMarkerDot originMarker = new MapMarkerDot(
                origin.getLatitude(), origin.getLongitude());
        MapMarkerDot destMarker = new MapMarkerDot(
                destination.getLatitude(), destination.getLongitude());

        originMarker.setColor(Color.GREEN);
        destMarker.setColor(Color.RED);

        mapViewer.addMapMarker(originMarker);
        mapViewer.addMapMarker(destMarker);

        double centerLat = (origin.getLatitude() + destination.getLatitude()) / 2;
        double centerLon = (origin.getLongitude() + destination.getLongitude()) / 2;
        mapViewer.setDisplayPosition(new Coordinate(centerLat, centerLon), 3);
        
        setTitle("Route: " + originCode + " → " + destinationCode);
        setVisible(true);
    }
}