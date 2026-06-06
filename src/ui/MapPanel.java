package ui;

import database.DatabaseManager;
import models.Airport;
import org.openstreetmap.gui.jmapviewer.*;

import javax.swing.*;
import java.awt.*;

public class MapPanel extends JPanel {
    private final JMapViewer mapViewer;
    private final DatabaseManager db;

    public MapPanel(DatabaseManager db) {
        super(new BorderLayout());
        this.db = db;
        this.mapViewer = new JMapViewer();
        setBorder(BorderFactory.createTitledBorder("Flight Route Map"));
        setPreferredSize(new Dimension(400, 0));
        setupMap();
        add(mapViewer, BorderLayout.CENTER);
    }

    private void setupMap() {
        mapViewer.setZoom(5);
    }

    public void updateRoute(String originCode, String destinationCode) {
        Airport origin      = db.getAirport(originCode);
        Airport destination = db.getAirport(destinationCode);
        if (origin == null || destination == null) return;

        mapViewer.getMapMarkerList().clear();

        MapMarkerDot originMarker = new MapMarkerDot(
                origin.getLatitude(), origin.getLongitude());
        MapMarkerDot destMarker = new MapMarkerDot(
                destination.getLatitude(), destination.getLongitude());

        mapViewer.addMapMarker(originMarker);
        mapViewer.addMapMarker(destMarker);

        double centerLat = (origin.getLatitude() + destination.getLatitude()) / 2;
        double centerLon = (origin.getLongitude() + destination.getLongitude()) / 2;
        mapViewer.setDisplayPosition(new Coordinate(centerLat, centerLon), 3);
    }

    public void clearRoute() {
        mapViewer.getMapMarkerList().clear();
        mapViewer.repaint();
    }
}
