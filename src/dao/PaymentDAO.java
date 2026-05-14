package dao;

import database.DBConnection;
import model.Payment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    public static int insert(Payment payment) {
        String sql = "INSERT INTO payments (user_id, event_id, amount, payment_status) VALUES (?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, payment.getUserId());
            ps.setInt(2, payment.getEventId());
            ps.setDouble(3, payment.getAmount());
            ps.setString(4, payment.getStatus());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            int id = keys.next() ? keys.getInt(1) : -1;
            ps.close(); return id;
        } catch (SQLException e) { System.err.println("Insert payment error: " + e.getMessage()); return -1; }
    }

    public static boolean updateStatus(int paymentId, String status) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE payments SET payment_status = ? WHERE payment_id = ?");
            ps.setString(1, status); ps.setInt(2, paymentId);
            int rows = ps.executeUpdate(); ps.close(); return rows > 0;
        } catch (SQLException e) { return false; }
    }

    public static Payment findByUserAndEvent(int userId, int eventId) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM payments WHERE user_id = ? AND event_id = ? ORDER BY payment_id DESC LIMIT 1");
            ps.setInt(1, userId); ps.setInt(2, eventId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) { Payment p = extractPayment(rs); ps.close(); return p; }
            ps.close();
        } catch (SQLException e) { System.err.println("Find payment error: " + e.getMessage()); }
        return null;
    }

    public static List<Payment> findByUser(int userId) {
        List<Payment> list = new ArrayList<>();
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM payments WHERE user_id = ? ORDER BY payment_id DESC");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(extractPayment(rs));
            ps.close();
        } catch (SQLException e) { System.err.println("Find payments error: " + e.getMessage()); }
        return list;
    }

    public static boolean hasCompletedPayment(int userId, int eventId) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM payments WHERE user_id = ? AND event_id = ? AND payment_status = 'COMPLETED'");
            ps.setInt(1, userId); ps.setInt(2, eventId);
            ResultSet rs = ps.executeQuery();
            boolean r = rs.next() && rs.getInt(1) > 0; ps.close(); return r;
        } catch (SQLException e) { return false; }
    }

    private static Payment extractPayment(ResultSet rs) throws SQLException {
        return new Payment(rs.getInt("payment_id"), rs.getInt("user_id"), rs.getInt("event_id"), rs.getDouble("amount"), rs.getString("payment_status"));
    }
}
