package model;

/**
 * Represents a payment for a paid event.
 */
public class Payment {
    private int paymentId;
    private int userId;
    private int eventId;
    private double amount;
    private String status; // PENDING, COMPLETED, FAILED

    public Payment() {}

    public Payment(int paymentId, int userId, int eventId, double amount, String status) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.eventId = eventId;
        this.amount = amount;
        this.status = status;
    }

    public Payment(int userId, int eventId, double amount, String status) {
        this.userId = userId;
        this.eventId = eventId;
        this.amount = amount;
        this.status = status;
    }

    // Getters and Setters
    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isCompleted() { return "COMPLETED".equalsIgnoreCase(status); }

    @Override
    public String toString() {
        return "Payment{id=" + paymentId + ", userId=" + userId + ", eventId=" + eventId +
               ", amount=" + amount + ", status='" + status + "'}";
    }
}
