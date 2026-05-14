package dao;

import database.DBConnection;
import model.Pass;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PassDAO {

    public static int insert(Pass pass) {
        String sql = "INSERT INTO passes (user_id, event_id, event_name, user_name, qr_code, valid_status, issue_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, pass.getUserId());
            ps.setInt(2, pass.getEventId());
            ps.setString(3, pass.getEventName());
            ps.setString(4, pass.getUserName());
            ps.setString(5, pass.getQrCode());
            ps.setInt(6, pass.isValidStatus() ? 1 : 0);
            ps.setString(7, pass.getIssueDate());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            int id = keys.next() ? keys.getInt(1) : -1;
            ps.close(); return id;
        } catch (SQLException e) { System.err.println("Insert pass error: " + e.getMessage()); return -1; }
    }

    public static Pass findByUserAndEvent(int userId, int eventId) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM passes WHERE user_id = ? AND event_id = ? ORDER BY pass_id DESC LIMIT 1");
            ps.setInt(1, userId); ps.setInt(2, eventId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) { Pass p = extractPass(rs); ps.close(); return p; }
            ps.close();
        } catch (SQLException e) { System.err.println("Find pass error: " + e.getMessage()); }
        return null;
    }

    public static Pass findByQRCode(String qrCode) {
        if (qrCode == null) return null;
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM passes WHERE UPPER(qr_code) = UPPER(?) LIMIT 1");
            ps.setString(1, qrCode.trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) { Pass p = extractPass(rs); ps.close(); return p; }
            ps.close();
        } catch (SQLException e) { System.err.println("Find by QR error: " + e.getMessage()); }
        return null;
    }

    public static List<Pass> findByUser(int userId) {
        List<Pass> list = new ArrayList<>();
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM passes WHERE user_id = ? ORDER BY pass_id DESC");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(extractPass(rs));
            ps.close();
        } catch (SQLException e) { System.err.println("Find passes error: " + e.getMessage()); }
        return list;
    }

    public static boolean invalidate(int passId) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE passes SET valid_status = 0 WHERE pass_id = ?");
            ps.setInt(1, passId);
            int rows = ps.executeUpdate(); ps.close(); return rows > 0;
        } catch (SQLException e) { return false; }
    }

    private static Pass extractPass(ResultSet rs) throws SQLException {
        return new Pass(rs.getInt("pass_id"), rs.getInt("user_id"), rs.getInt("event_id"),
            rs.getString("event_name"), rs.getString("user_name"), rs.getString("qr_code"),
            rs.getInt("valid_status") == 1, rs.getString("issue_date"));
    }
}
