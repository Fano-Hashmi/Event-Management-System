package dao;

import database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for waiting list persistence in SQLite.
 */
public class WaitingListDAO {

    public static boolean enqueue(int userId, int eventId) {
        try {
            Connection conn = DBConnection.getConnection();
            // Get next position
            PreparedStatement ps1 = conn.prepareStatement("SELECT COALESCE(MAX(position), 0) + 1 FROM waiting_list WHERE event_id = ?");
            ps1.setInt(1, eventId);
            ResultSet rs = ps1.executeQuery();
            int pos = rs.next() ? rs.getInt(1) : 1;
            ps1.close();
            // Insert
            PreparedStatement ps2 = conn.prepareStatement("INSERT INTO waiting_list (user_id, event_id, position) VALUES (?, ?, ?)");
            ps2.setInt(1, userId); ps2.setInt(2, eventId); ps2.setInt(3, pos);
            ps2.executeUpdate(); ps2.close();
            return true;
        } catch (SQLException e) { System.err.println("Enqueue error: " + e.getMessage()); return false; }
    }

    public static int dequeue(int eventId) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT queue_id, user_id FROM waiting_list WHERE event_id = ? ORDER BY position ASC LIMIT 1");
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int queueId = rs.getInt("queue_id");
                int userId = rs.getInt("user_id");
                ps.close();
                PreparedStatement del = conn.prepareStatement("DELETE FROM waiting_list WHERE queue_id = ?");
                del.setInt(1, queueId); del.executeUpdate(); del.close();
                return userId;
            }
            ps.close();
        } catch (SQLException e) { System.err.println("Dequeue error: " + e.getMessage()); }
        return -1;
    }

    public static boolean isInQueue(int userId, int eventId) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM waiting_list WHERE user_id = ? AND event_id = ?");
            ps.setInt(1, userId); ps.setInt(2, eventId);
            ResultSet rs = ps.executeQuery();
            boolean r = rs.next() && rs.getInt(1) > 0; ps.close(); return r;
        } catch (SQLException e) { return false; }
    }

    public static int getQueueSize(int eventId) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM waiting_list WHERE event_id = ?");
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            int c = rs.next() ? rs.getInt(1) : 0; ps.close(); return c;
        } catch (SQLException e) { return 0; }
    }

    public static List<Integer> getWaitingUserIds(int eventId) {
        List<Integer> ids = new ArrayList<>();
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT user_id FROM waiting_list WHERE event_id = ? ORDER BY position ASC");
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) ids.add(rs.getInt("user_id"));
            ps.close();
        } catch (SQLException e) { System.err.println("Get waiting list error: " + e.getMessage()); }
        return ids;
    }

    public static void clearEvent(int eventId) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM waiting_list WHERE event_id = ?");
            ps.setInt(1, eventId); ps.executeUpdate(); ps.close();
        } catch (SQLException e) { System.err.println("Clear waiting list error: " + e.getMessage()); }
    }

    public static void removeUser(int userId, int eventId) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM waiting_list WHERE user_id = ? AND event_id = ?");
            ps.setInt(1, userId); ps.setInt(2, eventId);
            ps.executeUpdate(); ps.close();
        } catch (SQLException e) { System.err.println("Remove from queue error: " + e.getMessage()); }
    }
}
