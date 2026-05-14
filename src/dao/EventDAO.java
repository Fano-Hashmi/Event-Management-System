package dao;

import database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Event;

/**
 * Data Access Object for Event operations.
 * Supports description, staff assignment, category search, and analytics.
 */
public class EventDAO {

    /**
     * Insert a new event. Returns generated event_id or -1 on failure.
     */
    public static int insert(Event event) {
        String sql = "INSERT INTO events (event_id, name, description, category, date, time, venue, capacity, is_paid, price, assigned_staff_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            boolean previousAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            int nextEventId = getNextAvailableEventId(conn);
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, nextEventId);
            ps.setString(2, event.getName());
            ps.setString(3, event.getDescription() != null ? event.getDescription() : "");
            ps.setString(4, event.getCategory() != null ? event.getCategory() : "OTHER");
            ps.setString(5, event.getDate());
            ps.setString(6, event.getTime());
            ps.setString(7, event.getVenue());
            ps.setInt(8, event.getCapacity());
            ps.setInt(9, event.isPaid() ? 1 : 0);
            ps.setDouble(10, event.getPrice());
            setAssignedStaff(ps, 11, event.getAssignedStaffId());
            ps.executeUpdate();
            ps.close();
            conn.commit();
            conn.setAutoCommit(previousAutoCommit);
            return nextEventId;
        } catch (SQLException e) {
            System.err.println("Insert event error: " + e.getMessage());
            try {
                Connection conn = DBConnection.getConnection();
                if (conn != null) {
                    conn.rollback();
                    conn.setAutoCommit(true);
                }
            } catch (SQLException rollbackError) {
                System.err.println("Rollback event insert error: " + rollbackError.getMessage());
            }
            return -1;
        }
    }

    /**
     * Update an existing event.
     */
    public static boolean update(Event event) {
        String sql = "UPDATE events SET name=?, description=?, category=?, date=?, time=?, venue=?, capacity=?, is_paid=?, price=?, assigned_staff_id=? WHERE event_id=?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, event.getName());
            ps.setString(2, event.getDescription() != null ? event.getDescription() : "");
            ps.setString(3, event.getCategory() != null ? event.getCategory() : "OTHER");
            ps.setString(4, event.getDate());
            ps.setString(5, event.getTime());
            ps.setString(6, event.getVenue());
            ps.setInt(7, event.getCapacity());
            ps.setInt(8, event.isPaid() ? 1 : 0);
            ps.setDouble(9, event.getPrice());
            setAssignedStaff(ps, 10, event.getAssignedStaffId());
            ps.setInt(11, event.getEventId());
            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Update event error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete an event by ID (cascading delete of related data).
     */
    public static boolean delete(int eventId) {
        try {
            Connection conn = DBConnection.getConnection();
            boolean previousAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            String[] tables = {"attendance", "feedback", "waiting_list", "passes", "payments", "registrations"};
            for (String table : tables) {
                PreparedStatement ps = conn.prepareStatement("DELETE FROM " + table + " WHERE event_id = ?");
                ps.setInt(1, eventId);
                ps.executeUpdate();
                ps.close();
            }
            PreparedStatement ps = conn.prepareStatement("DELETE FROM events WHERE event_id = ?");
            ps.setInt(1, eventId);
            int rows = ps.executeUpdate();
            ps.close();
            conn.commit();
            conn.setAutoCommit(previousAutoCommit);
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Delete event error: " + e.getMessage());
            try {
                Connection conn = DBConnection.getConnection();
                if (conn != null) {
                    conn.rollback();
                    conn.setAutoCommit(true);
                }
            } catch (SQLException rollbackError) {
                System.err.println("Rollback event delete error: " + rollbackError.getMessage());
            }
            return false;
        }
    }

    /**
     * Find an event by ID (includes registration count).
     */
    public static Event findById(int eventId) {
        String sql = "SELECT e.*, (SELECT COUNT(*) FROM registrations r WHERE r.event_id = e.event_id) as reg_count FROM events e WHERE e.event_id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Event ev = extractEvent(rs);
                ps.close();
                return ev;
            }
            ps.close();
        } catch (SQLException e) {
            System.err.println("Find event error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Get all events.
     */
    public static List<Event> getAll() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT e.*, (SELECT COUNT(*) FROM registrations r WHERE r.event_id = e.event_id) as reg_count FROM events e ORDER BY e.date ASC";
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                events.add(extractEvent(rs));
            }
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Get all events error: " + e.getMessage());
        }
        return events;
    }

    /**
     * Get events assigned to a specific staff member.
     */
    public static List<Event> findByStaff(int staffId) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT e.*, (SELECT COUNT(*) FROM registrations r WHERE r.event_id = e.event_id) as reg_count FROM events e WHERE e.assigned_staff_id = ? ORDER BY e.date ASC";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, staffId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                events.add(extractEvent(rs));
            }
            ps.close();
        } catch (SQLException e) {
            System.err.println("Find by staff error: " + e.getMessage());
        }
        return events;
    }

    /**
     * Search events by name (partial match).
     */
    public static List<Event> searchByName(String name) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT e.*, (SELECT COUNT(*) FROM registrations r WHERE r.event_id = e.event_id) as reg_count FROM events e WHERE LOWER(e.name) LIKE LOWER(?) ORDER BY e.date ASC";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) events.add(extractEvent(rs));
            ps.close();
        } catch (SQLException e) {
            System.err.println("Search events error: " + e.getMessage());
        }
        return events;
    }

    /**
     * Search events by date.
     */
    public static List<Event> searchByDate(String date) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT e.*, (SELECT COUNT(*) FROM registrations r WHERE r.event_id = e.event_id) as reg_count FROM events e WHERE e.date = ? ORDER BY e.time ASC";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, date);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) events.add(extractEvent(rs));
            ps.close();
        } catch (SQLException e) {
            System.err.println("Search by date error: " + e.getMessage());
        }
        return events;
    }

    /**
     * Search events by category.
     */
    public static List<Event> searchByCategory(String category) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT e.*, (SELECT COUNT(*) FROM registrations r WHERE r.event_id = e.event_id) as reg_count FROM events e WHERE UPPER(e.category) = UPPER(?) ORDER BY e.date ASC";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, category);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) events.add(extractEvent(rs));
            ps.close();
        } catch (SQLException e) {
            System.err.println("Search by category error: " + e.getMessage());
        }
        return events;
    }

    /**
     * Get total event count.
     */
    public static int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM events";
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) { int c = rs.getInt(1); stmt.close(); return c; }
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Count events error: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Get count of paid events.
     */
    public static int getPaidCount() {
        String sql = "SELECT COUNT(*) FROM events WHERE is_paid = 1";
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) { int c = rs.getInt(1); stmt.close(); return c; }
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Paid count error: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Get count of free events.
     */
    public static int getFreeCount() {
        String sql = "SELECT COUNT(*) FROM events WHERE is_paid = 0";
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) { int c = rs.getInt(1); stmt.close(); return c; }
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Free count error: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Get total revenue from completed payments.
     */
    public static double getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE payment_status = 'COMPLETED'";
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) { double v = rs.getDouble(1); stmt.close(); return v; }
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Revenue error: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Get the most popular events (by registration count).
     */
    public static List<Event> getMostPopular(int limit) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT e.*, (SELECT COUNT(*) FROM registrations r WHERE r.event_id = e.event_id) as reg_count FROM events e ORDER BY reg_count DESC LIMIT ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) events.add(extractEvent(rs));
            ps.close();
        } catch (SQLException e) {
            System.err.println("Most popular error: " + e.getMessage());
        }
        return events;
    }

    /**
     * Extract an Event object from a ResultSet row.
     */
    private static Event extractEvent(ResultSet rs) throws SQLException {
        Event event = new Event(
            rs.getInt("event_id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getString("category"),
            rs.getString("date"),
            rs.getString("time"),
            rs.getString("venue"),
            rs.getInt("capacity"),
            rs.getInt("is_paid") == 1 ? "PAID" : "FREE",
            rs.getDouble("price"),
            rs.getInt("assigned_staff_id")
        );
        try {
            event.setRegisteredCount(rs.getInt("reg_count"));
        } catch (SQLException ignored) {}
        return event;
    }

    private static void setAssignedStaff(PreparedStatement ps, int parameterIndex, int staffId) throws SQLException {
        if (staffId > 0) ps.setInt(parameterIndex, staffId);
        else ps.setNull(parameterIndex, Types.INTEGER);
    }

    private static int getNextAvailableEventId(Connection conn) throws SQLException {
        int expectedId = 1;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT event_id FROM events WHERE event_id > 0 ORDER BY event_id");
        while (rs.next()) {
            int currentId = rs.getInt("event_id");
            if (currentId > expectedId) break;
            if (currentId == expectedId) expectedId++;
        }
        rs.close();
        stmt.close();
        return expectedId;
    }
}
