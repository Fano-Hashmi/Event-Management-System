package main;

import com.formdev.flatlaf.FlatDarkLaf;
import database.DBConnection;
import gui.MainFrame;
import gui.SplashScreen;
import service.EventService;
import javax.swing.*;

/**
 * Application entry point.
 * Configures FlatLaf theme, initializes DB, shows splash, launches MainFrame.
 */
public class Main {
    public static void main(String[] args) {
        // Configure FlatLaf Dark theme
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            System.err.println("FlatLaf init failed: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            // Show splash screen
            SplashScreen splash = new SplashScreen();
            splash.showSplash(() -> {
                // Initialize database
                DBConnection.getConnection();
                // Initialize event service (loads data into BST/Store)
                EventService.initialize();

                // Launch main frame
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            });
        });
    }
}
