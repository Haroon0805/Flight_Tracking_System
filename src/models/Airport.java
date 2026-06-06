package models;

public class Airport {
    private String code;
    private String name;
    private String city;
    private String country;
    private double latitude;
    private double longitude;

    public Airport(String code, String name, String city, String country,
                   double latitude, double longitude) {
        this.code = code;
        this.name = name;
        this.city = city;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCode()      { return code; }
    public String getName()      { return name; }
    public String getCity()      { return city; }
    public String getCountry()   { return country; }
    public double getLatitude()  { return latitude; }
    public double getLongitude() { return longitude; }

    @Override
    public String toString() {
        return String.format("%s - %s (%s, %s)", code, name, city, country);
    }
}
