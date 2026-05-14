package dao;

import database.DBConnection;
import model.Registration;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegistrationDAO {

    public static boolean insert(Registration reg) {
        String sql = "INSERT INTO registrations (user_id, event_id, registration_date) VALUES (?, ?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, reg.getUserId());
            ps.setInt(2, reg.getEventId());
            ps.setString(3, reg.getRegistrationDate());
            ps.executeUpdate(); ps.close();
            return true;
        } catch (SQLException e) {
            System.err.println("Insert registration error: " + e.getMessage());
            return false;
        }
    }

    public static boolean delete(int userId, int eventId) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM registrations WHERE user_id = ? AND event_id = ?");
            ps.setInt(1, userId); ps.setInt(2, eventId);
            int rows = ps.executeUpdate(); ps.close();
            return rows > 0;
        } catch (SQLException e) { return false; }
    }

    public static boolean isRegistered(int userId, int eventId) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM registrations WHERE user_id = ? AND event_id = ?");
            ps.setInt(1, userId); ps.setInt(2, eventId);
            ResultSet rs = ps.executeQuery();
            boolean r = rs.next() && rs.getInt(1) > 0; ps.close(); return r;
        } catch (SQLException e) { return false; }
    }

    public static List<Registration> findByUser(int userId) {
        List<Registration> list = new ArrayList<>();
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM registrations WHERE user_id = ? ORDER BY registration_date DESC");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new Registration(rs.getInt("reg_id"), rs.getInt("user_id"), rs.getInt("event_id"), rs.getString("registration_date")));
            ps.close();
        } catch (SQLException e) { System.err.println("Find reg error: " + e.getMessage()); }
        return list;
    }

    public static List<Registration> findByEvent(int eventId) {
        List<Registration> list = new ArrayList<>();
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM registrations WHERE event_id = ? ORDER BY registration_date ASC");
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new Registration(rs.getInt("reg_id"), rs.getInt("user_id"), rs.getInt("event_id"), rs.getString("registration_date")));
            ps.close();
        } catch (SQLException e) { System.err.println("Find by event error: " + e.getMessage()); }
        return list;
    }

    public static int getCountByEvent(int eventId) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM registrations WHERE event_id = ?");
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            int c = rs.next() ? rs.getInt(1) : 0; ps.close(); return c;
        } catch (SQLException e) { return 0; }
    }

    public static int getTotalCount() {
        try {
            Statement stmt = DBConnection.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM registrations");
            int c = rs.next() ? rs.getInt(1) : 0; stmt.close(); return c;
        } catch (SQLException e) { return 0; }
    }
}
