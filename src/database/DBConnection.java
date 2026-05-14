package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database connection manager using SQLite.
 * Singleton pattern ensures a single connection instance.
 * Auto-creates all 8 required tables on first connection.
 */
public class DBConnection {
    private static final String DB_URL = "jdbc:sqlite:event.db";
    private static Connection connection = null;

    /**
     * Get the database connection. Creates tables if they don't exist.
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(DB_URL);
                connection.setAutoCommit(true);
                // Enable foreign keys
                connection.createStatement().execute("PRAGMA foreign_keys = ON");
                initializeTables();
                insertDefaultAdmin();
                System.out.println("Database connected successfully.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
        return connection;
    }

    /**
     * Create all 8 required tables if they don't exist.
     */
    private static void initializeTables() throws SQLException {
        Statement stmt = connection.createStatement();

        // 1. USERS table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS users (
                user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                role TEXT NOT NULL DEFAULT 'USER',
                status TEXT NOT NULL DEFAULT 'ACTIVE'
            )
        """);

        // 2. EVENTS table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS events (
                event_id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                description TEXT DEFAULT '',
                category TEXT DEFAULT 'OTHER',
                date TEXT NOT NULL,
                time TEXT NOT NULL,
                venue TEXT NOT NULL,
                capacity INTEGER NOT NULL,
                is_paid INTEGER NOT NULL DEFAULT 0,
                price REAL DEFAULT 0.0,
                assigned_staff_id INTEGER DEFAULT NULL,
                FOREIGN KEY (assigned_staff_id) REFERENCES users(user_id)
            )
        """);

        // 3. REGISTRATIONS table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS registrations (
                reg_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                event_id INTEGER NOT NULL,
                registration_date TEXT NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(user_id),
                FOREIGN KEY (event_id) REFERENCES events(event_id),
                UNIQUE(user_id, event_id)
            )
        """);

        // 4. PAYMENTS table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS payments (
                payment_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                event_id INTEGER NOT NULL,
                amount REAL NOT NULL,
                payment_status TEXT NOT NULL DEFAULT 'PENDING',
                FOREIGN KEY (user_id) REFERENCES users(user_id),
                FOREIGN KEY (event_id) REFERENCES events(event_id)
            )
        """);

        // 5. PASSES table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS passes (
                pass_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                event_id INTEGER NOT NULL,
                event_name TEXT NOT NULL,
                user_name TEXT NOT NULL,
                qr_code TEXT NOT NULL,
                valid_status INTEGER NOT NULL DEFAULT 1,
                issue_date TEXT NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(user_id),
                FOREIGN KEY (event_id) REFERENCES events(event_id)
            )
        """);

        // 6. WAITING_LIST table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS waiting_list (
                queue_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                event_id INTEGER NOT NULL,
                position INTEGER NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(user_id),
                FOREIGN KEY (event_id) REFERENCES events(event_id),
                UNIQUE(user_id, event_id)
            )
        """);

        // 7. FEEDBACK table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS feedback (
                feedback_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                event_id INTEGER NOT NULL,
                rating INTEGER NOT NULL CHECK(rating >= 1 AND rating <= 5),
                comment TEXT DEFAULT '',
                FOREIGN KEY (user_id) REFERENCES users(user_id),
                FOREIGN KEY (event_id) REFERENCES events(event_id),
                UNIQUE(user_id, event_id)
            )
        """);

        // 8. ATTENDANCE table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS attendance (
                attendance_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                event_id INTEGER NOT NULL,
                qr_verified INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY (user_id) REFERENCES users(user_id),
                FOREIGN KEY (event_id) REFERENCES events(event_id),
                UNIQUE(user_id, event_id)
            )
        """);

        stmt.close();
        normalizeAssignedStaffIds();
        System.out.println("All 8 tables initialized.");
    }

    private static void normalizeAssignedStaffIds() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("""
                UPDATE events
                SET assigned_staff_id = NULL
                WHERE assigned_staff_id IS NOT NULL
                  AND (
                    assigned_staff_id = 0
                    OR assigned_staff_id NOT IN (SELECT user_id FROM users)
                  )
            """);
        }
    }

    /**
     * Insert default admin account if it doesn't exist.
     */
    private static void insertDefaultAdmin() {
        try {
            var ps = connection.prepareStatement(
                "SELECT COUNT(*) FROM users WHERE email = ?"
            );
            ps.setString(1, "admin@admin.com");
            var rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                var insert = connection.prepareStatement(
                    "INSERT INTO users (name, email, password, role, status) VALUES (?, ?, ?, ?, ?)"
                );
                insert.setString(1, "Administrator");
                insert.setString(2, "admin@admin.com");
                insert.setString(3, util.Validation.hashPassword("admin123"));
                insert.setString(4, "ADMIN");
                insert.setString(5, "ACTIVE");
                insert.executeUpdate();
                insert.close();
                System.out.println("Default admin created: admin@admin.com / admin123");
            }
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error creating default admin: " + e.getMessage());
        }
    }

    /**
     * Close the database connection.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
