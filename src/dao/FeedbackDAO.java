package dao;

import database.DBConnection;
import model.Feedback;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO {

    public static boolean insert(Feedback fb) {
        String sql = "INSERT INTO feedback (user_id, event_id, rating, comment) VALUES (?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, fb.getUserId());
            ps.setInt(2, fb.getEventId());
            ps.setInt(3, fb.getRating());
            ps.setString(4, fb.getComment() != null ? fb.getComment() : "");
            ps.executeUpdate(); ps.close();
            return true;
        } catch (SQLException e) { System.err.println("Insert feedback error: " + e.getMessage()); return false; }
    }

    public static boolean hasSubmitted(int userId, int eventId) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM feedback WHERE user_id = ? AND event_id = ?");
            ps.setInt(1, userId); ps.setInt(2, eventId);
            ResultSet rs = ps.executeQuery();
            boolean r = rs.next() && rs.getInt(1) > 0; ps.close(); return r;
        } catch (SQLException e) { return false; }
    }

    public static List<Feedback> findByEvent(int eventId) {
        List<Feedback> list = new ArrayList<>();
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM feedback WHERE event_id = ? ORDER BY feedback_id DESC");
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(extract(rs));
            ps.close();
        } catch (SQLException e) { System.err.println("Find feedback error: " + e.getMessage()); }
        return list;
    }

    public static List<Feedback> findByUser(int userId) {
        List<Feedback> list = new ArrayList<>();
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM feedback WHERE user_id = ? ORDER BY feedback_id DESC");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(extract(rs));
            ps.close();
        } catch (SQLException e) { System.err.println("Find user feedback error: " + e.getMessage()); }
        return list;
    }

    public static double getAverageRating(int eventId) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT AVG(rating) FROM feedback WHERE event_id = ?");
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            double avg = rs.next() ? rs.getDouble(1) : 0; ps.close(); return avg;
        } catch (SQLException e) { return 0; }
    }

    public static int getCount(int eventId) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM feedback WHERE event_id = ?");
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            int c = rs.next() ? rs.getInt(1) : 0; ps.close(); return c;
        } catch (SQLException e) { return 0; }
    }

    private static Feedback extract(ResultSet rs) throws SQLException {
        return new Feedback(rs.getInt("feedback_id"), rs.getInt("user_id"), rs.getInt("event_id"), rs.getInt("rating"), rs.getString("comment"));
    }
}
