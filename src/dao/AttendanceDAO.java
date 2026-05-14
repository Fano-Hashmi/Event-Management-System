package dao;

import database.DBConnection;
import model.Attendance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    public static boolean markAttendance(int userId, int eventId, boolean qrVerified) {
        String sql = """
            INSERT INTO attendance (user_id, event_id, qr_verified)
            VALUES (?, ?, ?)
            ON CONFLICT(user_id, event_id) DO UPDATE SET
                qr_verified = CASE
                    WHEN excluded.qr_verified = 1 THEN 1
                    ELSE attendance.qr_verified
                END
        """;
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId); ps.setInt(2, eventId); ps.setInt(3, qrVerified ? 1 : 0);
            ps.executeUpdate(); ps.close();
            return true;
        } catch (SQLException e) { System.err.println("Mark attendance error: " + e.getMessage()); return false; }
    }

    public static boolean isAttended(int userId, int eventId) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM attendance WHERE user_id = ? AND event_id = ?");
            ps.setInt(1, userId); ps.setInt(2, eventId);
            ResultSet rs = ps.executeQuery();
            boolean r = rs.next() && rs.getInt(1) > 0; ps.close(); return r;
        } catch (SQLException e) { return false; }
    }

    public static List<Attendance> findByEvent(int eventId) {
        List<Attendance> list = new ArrayList<>();
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM attendance WHERE event_id = ?");
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new Attendance(rs.getInt("attendance_id"), rs.getInt("user_id"), rs.getInt("event_id"), rs.getInt("qr_verified") == 1));
            ps.close();
        } catch (SQLException e) { System.err.println("Find attendance error: " + e.getMessage()); }
        return list;
    }

    public static int getAttendanceCount(int eventId) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM attendance WHERE event_id = ?");
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            int c = rs.next() ? rs.getInt(1) : 0; ps.close(); return c;
        } catch (SQLException e) { return 0; }
    }
}
