package database;

import models.Airport;
import models.Flight;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:flights.db";

    // ─── Setup ────────────────────────────────────────────────────────────────

    public static void initialize() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
            System.exit(1);
        }

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            System.out.println("Initializing database...");

            stmt.execute("CREATE TABLE IF NOT EXISTS Airports (" +
                    "airport_code TEXT PRIMARY KEY, " +
                    "airport_name TEXT NOT NULL, " +
                    "city TEXT NOT NULL, " +
                    "country TEXT NOT NULL, " +
                    "latitude REAL NOT NULL, " +
                    "longitude REAL NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS Flights (" +
                    "flight_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "flight_number TEXT NOT NULL UNIQUE, " +
                    "airline TEXT NOT NULL, " +
                    "origin TEXT NOT NULL, " +
                    "destination TEXT NOT NULL, " +
                    "departure_date TEXT, " +
                    "departure_time TEXT, " +
                    "status TEXT CHECK(status IN ('Scheduled','In Air','Landed','Delayed','Cancelled')) DEFAULT 'Scheduled', " +
                    "FOREIGN KEY (origin) REFERENCES Airports(airport_code), " +
                    "FOREIGN KEY (destination) REFERENCES Airports(airport_code))");

            seedAirports(stmt);
            seedFlights(stmt);
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void seedAirports(Statement stmt) throws SQLException {
        stmt.execute("INSERT OR IGNORE INTO Airports VALUES " +
                "('JFK','John F. Kennedy International','New York','USA',40.6398,-73.7789)," +
                "('LAX','Los Angeles International','Los Angeles','USA',33.9425,-118.4081)," +
                "('DXB','Dubai International','Dubai','UAE',25.2528,55.3644)," +
                "('DOH','Hamad International','Doha','Qatar',25.2731,51.6081)," +
                "('SIN','Changi Airport','Singapore','Singapore',1.3502,103.9944)," +
                "('CDG','Charles de Gaulle','Paris','France',49.0128,2.5500)," +
                "('NRT','Narita International','Tokyo','Japan',35.7647,140.3863)");
    }

    private static void seedFlights(Statement stmt) throws SQLException {
        stmt.execute("INSERT OR IGNORE INTO Flights " +
                "(flight_number,airline,origin,destination,departure_date,departure_time,status) VALUES " +
                "('AA123','American Airlines','JFK','LAX','2025-06-01','08:00:00','Scheduled')," +
                "('QR789','Qatar Airways','DOH','CDG','2025-06-01','10:00:00','Scheduled')," +
                "('EK201','Emirates','DXB','JFK','2025-06-02','09:00:00','Scheduled')");
    }

    // ─── Connection ───────────────────────────────────────────────────────────

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // ─── Flights ──────────────────────────────────────────────────────────────

    public List<Flight> getAllFlights() {
        List<Flight> flights = new ArrayList<>();
        String sql = "SELECT * FROM Flights";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) flights.add(mapFlight(rs));
        } catch (SQLException e) {
            System.err.println("Error loading flights: " + e.getMessage());
        }
        return flights;
    }

    public List<Flight> searchFlights(String query) {
        List<Flight> flights = new ArrayList<>();
        String sql = "SELECT * FROM Flights WHERE LOWER(flight_number) LIKE ? " +
                     "OR LOWER(airline) LIKE ? OR LOWER(status) LIKE ?";
        String like = "%" + query.toLowerCase() + "%";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) flights.add(mapFlight(rs));
        } catch (SQLException e) {
            System.err.println("Error searching flights: " + e.getMessage());
        }
        return flights;
    }

    public boolean addFlight(Flight f) {
        String sql = "INSERT INTO Flights (flight_number,airline,origin,destination," +
                     "departure_date,departure_time,status) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setFlightParams(ps, f);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding flight: " + e.getMessage());
            return false;
        }
    }

    public boolean updateFlight(String oldFlightNumber, Flight f) {
        String sql = "UPDATE Flights SET flight_number=?,airline=?,origin=?,destination=?," +
                     "departure_date=?,departure_time=?,status=? WHERE flight_number=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setFlightParams(ps, f);
            ps.setString(8, oldFlightNumber);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating flight: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteFlight(String flightNumber) {
        String sql = "DELETE FROM Flights WHERE flight_number=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, flightNumber);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting flight: " + e.getMessage());
            return false;
        }
    }

    // ─── Airports ─────────────────────────────────────────────────────────────

    public List<Airport> getAllAirports() {
        List<Airport> airports = new ArrayList<>();
        String sql = "SELECT * FROM Airports";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) airports.add(mapAirport(rs));
        } catch (SQLException e) {
            System.err.println("Error loading airports: " + e.getMessage());
        }
        return airports;
    }

    public Airport getAirport(String code) {
        String sql = "SELECT * FROM Airports WHERE airport_code=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapAirport(rs);
        } catch (SQLException e) {
            System.err.println("Error fetching airport: " + e.getMessage());
        }
        return null;
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private Flight mapFlight(ResultSet rs) throws SQLException {
        return new Flight(
                rs.getInt("flight_id"),
                rs.getString("flight_number"),
                rs.getString("airline"),
                rs.getString("origin"),
                rs.getString("destination"),
                rs.getString("departure_date") != null ? rs.getString("departure_date") : "",
                rs.getString("departure_time"),
                rs.getString("status")
        );
    }

    private Airport mapAirport(ResultSet rs) throws SQLException {
        return new Airport(
                rs.getString("airport_code"),
                rs.getString("airport_name"),
                rs.getString("city"),
                rs.getString("country"),
                rs.getDouble("latitude"),
                rs.getDouble("longitude")
        );
    }

    private void setFlightParams(PreparedStatement ps, Flight f) throws SQLException {
        ps.setString(1, f.getFlightNumber());
        ps.setString(2, f.getAirline());
        ps.setString(3, f.getOrigin());
        ps.setString(4, f.getDestination());
        ps.setString(5, f.getDepartureDate());
        ps.setString(6, f.getDepartureTime());
        ps.setString(7, f.getStatus());
    }
}
