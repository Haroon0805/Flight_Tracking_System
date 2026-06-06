import com.formdev.flatlaf.FlatDarkLaf;
import database.DatabaseManager;
import ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            System.err.println("FlatLaf setup failed: " + e.getMessage());
        }

        DatabaseManager db = new DatabaseManager();
        DatabaseManager.initialize();
        SwingUtilities.invokeLater(() -> new MainFrame(db));
    }
}