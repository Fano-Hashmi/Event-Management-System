package service;

import dao.FeedbackDAO;
import dao.AttendanceDAO;
import dao.PassDAO;
import dao.RegistrationDAO;
import database.DBConnection;
import model.Feedback;
import model.Pass;
import util.QRGenerator;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Business logic for feedback, attendance, and QR verification.
 */
public class FeedbackService {

    public static String submitFeedback(int userId, int eventId, int rating, String comment) {
        if (rating < 1 || rating > 5) return "Rating must be between 1 and 5.";
        if (FeedbackDAO.hasSubmitted(userId, eventId))
            return "You have already submitted feedback for this event.";
        if (!RegistrationDAO.isRegistered(userId, eventId))
            return "You must be registered for this event to give feedback.";

        Feedback fb = new Feedback(userId, eventId, rating, comment != null ? comment : "");
        boolean success = FeedbackDAO.insert(fb);
        return success ? null : "Failed to submit feedback.";
    }

    public static List<Feedback> getEventFeedback(int eventId) {
        return FeedbackDAO.findByEvent(eventId);
    }

    public static double getAverageRating(int eventId) {
        return FeedbackDAO.getAverageRating(eventId);
    }

    public static int getFeedbackCount(int eventId) {
        return FeedbackDAO.getCount(eventId);
    }

    public static boolean hasSubmitted(int userId, int eventId) {
        return FeedbackDAO.hasSubmitted(userId, eventId);
    }

    // --- Attendance & QR Verification ---

    public static String verifyQRAndMarkAttendance(String qrCode) {
        if (!QRGenerator.isValidQRCode(qrCode)) return "Invalid QR code format.";
        Pass pass = PassDAO.findByQRCode(qrCode.trim());
        if (pass == null) return "Invalid QR code. Pass not found.";
        if (!pass.isValidStatus()) return "This pass has already been used.";
        if (!RegistrationDAO.isRegistered(pass.getUserId(), pass.getEventId()))
            return "This pass user is not registered for the event.";

        try {
            Connection conn = DBConnection.getConnection();
            boolean previousAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            boolean attended = AttendanceDAO.markAttendance(pass.getUserId(), pass.getEventId(), true);
            if (!attended) {
                conn.rollback();
                conn.setAutoCommit(previousAutoCommit);
                return "Failed to mark attendance.";
            }

            boolean invalidated = PassDAO.invalidate(pass.getPassId());
            if (!invalidated) {
                conn.rollback();
                conn.setAutoCommit(previousAutoCommit);
                return "Failed to update pass status.";
            }

            conn.commit();
            conn.setAutoCommit(previousAutoCommit);
            return null;
        } catch (SQLException e) {
            try {
                Connection conn = DBConnection.getConnection();
                if (conn != null) {
                    conn.rollback();
                    conn.setAutoCommit(true);
                }
            } catch (SQLException rollbackError) {
                System.err.println("Rollback QR verification error: " + rollbackError.getMessage());
            }
            System.err.println("QR verification error: " + e.getMessage());
            return "Failed to verify QR code.";
        }
    }

    public static boolean markAttendance(int userId, int eventId) {
        return AttendanceDAO.markAttendance(userId, eventId, false);
    }

    public static boolean isAttended(int userId, int eventId) {
        return AttendanceDAO.isAttended(userId, eventId);
    }

    public static int getAttendanceCount(int eventId) {
        return AttendanceDAO.getAttendanceCount(eventId);
    }
}
