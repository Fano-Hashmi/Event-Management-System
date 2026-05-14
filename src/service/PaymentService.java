package service;

import dao.PaymentDAO;
import dao.PassDAO;
import dao.EventDAO;
import dao.UserDAO;
import model.Payment;
import model.Pass;
import model.Event;
import model.User;
import util.QRGenerator;
import java.time.LocalDate;
import java.util.List;

public class PaymentService {

    public static String processPayment(int userId, int eventId, String cardNumber, String cardName) {
        Event event = EventDAO.findById(eventId);
        if (event == null) return "Event not found.";
        if (!event.isPaid()) return "This is a free event. No payment needed.";
        if (PaymentDAO.hasCompletedPayment(userId, eventId))
            return "Payment already completed for this event.";
        if (cardNumber == null || cardNumber.replaceAll("\\s", "").length() < 12)
            return "Invalid card number.";
        if (cardName == null || cardName.trim().length() < 2)
            return "Invalid card holder name.";

        // Simulate payment (always succeeds)
        Payment payment = new Payment(userId, eventId, event.getPrice(), "COMPLETED");
        int paymentId = PaymentDAO.insert(payment);
        if (paymentId == -1) return "Payment processing failed.";

        // Generate QR pass
        User user = UserDAO.findById(userId);
        String userName = user != null ? user.getName() : "Unknown";
        String qrCode = QRGenerator.generateQRCode(userId, eventId);
        Pass pass = new Pass(userId, eventId, event.getName(), userName, qrCode, true, LocalDate.now().toString());
        PassDAO.insert(pass);

        return null;
    }

    public static boolean hasCompletedPayment(int userId, int eventId) {
        return PaymentDAO.hasCompletedPayment(userId, eventId);
    }

    public static Pass getPass(int userId, int eventId) {
        return PassDAO.findByUserAndEvent(userId, eventId);
    }

    public static List<Pass> getUserPasses(int userId) {
        return PassDAO.findByUser(userId);
    }

    public static Payment getPayment(int userId, int eventId) {
        return PaymentDAO.findByUserAndEvent(userId, eventId);
    }
}
