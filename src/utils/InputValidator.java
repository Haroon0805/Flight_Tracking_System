package utils;

import javax.swing.*;

public class InputValidator {

    public static boolean validateFlight(String flightNumber, String airline,
                                          String origin, String destination,
                                          String departureDate, String departureTime,
                                          java.awt.Component parent) {
        if (flightNumber.isEmpty() || airline.isEmpty() || origin == null ||
            destination == null || departureDate.isEmpty() || departureTime.isEmpty()) {
            showError(parent, "Please fill in all required fields.");
            return false;
        }
        if (origin.equals(destination)) {
            showError(parent, "Origin and destination cannot be the same.");
            return false;
        }
        if (!departureTime.matches("\\d{2}:\\d{2}:\\d{2}")) {
            showError(parent, "Departure time must be in HH:MM:SS format.");
            return false;
        }
        return true;
    }

    private static void showError(java.awt.Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }
}
