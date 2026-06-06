package models;

public class Flight {
    private int flightId;
    private String flightNumber;
    private String airline;
    private String origin;
    private String destination;
    private String departureDate;
    private String departureTime;
    private String status;

    public Flight(int flightId, String flightNumber, String airline, String origin,
                  String destination, String departureDate, String departureTime, String status) {
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.airline = airline;
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.status = status;
    }

    // Constructor without ID (for new flights before DB insert)
    public Flight(String flightNumber, String airline, String origin, String destination,
                  String departureDate, String departureTime, String status) {
        this(-1, flightNumber, airline, origin, destination, departureDate, departureTime, status);
    }

    public int getFlightId()         { return flightId; }
    public String getFlightNumber()  { return flightNumber; }
    public String getAirline()       { return airline; }
    public String getOrigin()        { return origin; }
    public String getDestination()   { return destination; }
    public String getDepartureDate() { return departureDate; }
    public String getDepartureTime() { return departureTime; }
    public String getStatus()        { return status; }

    public void setFlightNumber(String flightNumber)   { this.flightNumber = flightNumber; }
    public void setAirline(String airline)             { this.airline = airline; }
    public void setOrigin(String origin)               { this.origin = origin; }
    public void setDestination(String destination)     { this.destination = destination; }
    public void setDepartureDate(String departureDate) { this.departureDate = departureDate; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    public void setStatus(String status)               { this.status = status; }

    @Override
    public String toString() {
        return String.format("Flight[%s | %s | %s -> %s | %s %s | %s]",
                flightNumber, airline, origin, destination, departureDate, departureTime, status);
    }
}
