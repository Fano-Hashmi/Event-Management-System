package dao;

import database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.User;
import util.Validation;

/**
 * Data Access Object for User operations.
 * Supports authentication, CRUD, ban/unban, and cascading delete.
 */
public class UserDAO {

    /**
     * Authenticate a user by email and password. Checks ban status.
     */
    public static User authenticate(String email, String password) {
        String hashedPassword = Validation.hashPassword(password);
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, hashedPassword);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = extractUser(rs);
                ps.close();
                return user;
            }
            ps.close();
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Insert a new user into the database.
     */
    public static boolean insert(User user) {
        String sql = "INSERT INTO users (user_id, name, email, password, role, status) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            boolean previousAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            int nextUserId = getNextAvailableUserId(conn);
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, nextUserId);
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getRole());
            ps.setString(6, user.getStatus() != null ? user.getStatus() : "ACTIVE");
            ps.executeUpdate();
            ps.close();
            conn.commit();
            conn.setAutoCommit(previousAutoCommit);
            user.setUserId(nextUserId);
            return true;
        } catch (SQLException e) {
            System.err.println("Insert user error: " + e.getMessage());
            try {
                Connection conn = DBConnection.getConnection();
                if (conn != null) {
                    conn.rollback();
                    conn.setAutoCommit(true);
                }
            } catch (SQLException rollbackError) {
                System.err.println("Rollback user insert error: " + rollbackError.getMessage());
            }
            return false;
        }
    }

    /**
     * Find a user by email.
     */
    public static User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = extractUser(rs);
                ps.close();
                return u;
            }
            ps.close();
        } catch (SQLException e) {
            System.err.println("Find user by email error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Find a user by ID.
     */
    public static User findById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = extractUser(rs);
                ps.close();
                return u;
            }
            ps.close();
        } catch (SQLException e) {
            System.err.println("Find user by ID error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Get all users from the database.
     */
    public static List<User> getAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY user_id";
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                users.add(extractUser(rs));
            }
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Get all users error: " + e.getMessage());
        }
        return users;
    }

    /**
     * Get all staff users.
     */
    public static List<User> getAllStaff() {
        List<User> staff = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'STAFF' ORDER BY user_id";
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                staff.add(extractUser(rs));
            }
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Get all staff error: " + e.getMessage());
        }
        return staff;
    }

    /**
     * Update a user's role.
     */
    public static boolean updateRole(int userId, String newRole) {
        String sql = "UPDATE users SET role = ? WHERE user_id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newRole);
            ps.setInt(2, userId);
            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Update role error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update a user's status (ACTIVE/BANNED).
     */
    public static boolean updateStatus(int userId, String newStatus) {
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newStatus);
            ps.setInt(2, userId);
            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Update status error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete a user and all related data (cascading delete).
     */
    public static boolean delete(int userId) {
        try {
            Connection conn = DBConnection.getConnection();
            boolean previousAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            // Delete attendance records
            PreparedStatement ps1 = conn.prepareStatement("DELETE FROM attendance WHERE user_id = ?");
            ps1.setInt(1, userId); ps1.executeUpdate(); ps1.close();
            // Delete feedback
            PreparedStatement ps2 = conn.prepareStatement("DELETE FROM feedback WHERE user_id = ?");
            ps2.setInt(1, userId); ps2.executeUpdate(); ps2.close();
            // Delete waiting list entries
            PreparedStatement ps3 = conn.prepareStatement("DELETE FROM waiting_list WHERE user_id = ?");
            ps3.setInt(1, userId); ps3.executeUpdate(); ps3.close();
            // Delete passes
            PreparedStatement ps4 = conn.prepareStatement("DELETE FROM passes WHERE user_id = ?");
            ps4.setInt(1, userId); ps4.executeUpdate(); ps4.close();
            // Delete payments
            PreparedStatement ps5 = conn.prepareStatement("DELETE FROM payments WHERE user_id = ?");
            ps5.setInt(1, userId); ps5.executeUpdate(); ps5.close();
            // Delete registrations
            PreparedStatement ps6 = conn.prepareStatement("DELETE FROM registrations WHERE user_id = ?");
            ps6.setInt(1, userId); ps6.executeUpdate(); ps6.close();
            // Unassign from events
            PreparedStatement ps7 = conn.prepareStatement("UPDATE events SET assigned_staff_id = NULL WHERE assigned_staff_id = ?");
            ps7.setInt(1, userId); ps7.executeUpdate(); ps7.close();
            // Delete user
            PreparedStatement ps8 = conn.prepareStatement("DELETE FROM users WHERE user_id = ?");
            ps8.setInt(1, userId);
            int rows = ps8.executeUpdate();
            ps8.close();
            conn.commit();
            conn.setAutoCommit(previousAutoCommit);
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Delete user error: " + e.getMessage());
            try {
                Connection conn = DBConnection.getConnection();
                if (conn != null) {
                    conn.rollback();
                    conn.setAutoCommit(true);
                }
            } catch (SQLException rollbackError) {
                System.err.println("Rollback user delete error: " + rollbackError.getMessage());
            }
            return false;
        }
    }

    /**
     * Get total user count.
     */
    public static int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM users";
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                int count = rs.getInt(1);
                stmt.close();
                return count;
            }
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Count users error: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Extract a User object from a ResultSet row.
     */
    private static User extractUser(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("user_id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("password"),
            rs.getString("role"),
            rs.getString("status")
        );
    }

    private static int getNextAvailableUserId(Connection conn) throws SQLException {
        int expectedId = 1;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT user_id FROM users WHERE user_id > 0 ORDER BY user_id");
        while (rs.next()) {
            int currentId = rs.getInt("user_id");
            if (currentId > expectedId) break;
            if (currentId == expectedId) expectedId++;
        }
        rs.close();
        stmt.close();
        return expectedId;
    }
}
