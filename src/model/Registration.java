package model;

/**
 * Represents a user's registration for an event.
 */
public class Registration {
    private int regId;
    private int userId;
    private int eventId;
    private String registrationDate;

    public Registration() {}

    public Registration(int regId, int userId, int eventId, String registrationDate) {
        this.regId = regId;
        this.userId = userId;
        this.eventId = eventId;
        this.registrationDate = registrationDate;
    }

    public Registration(int userId, int eventId, String registrationDate) {
        this.userId = userId;
        this.eventId = eventId;
        this.registrationDate = registrationDate;
    }

    // Getters and Setters
    public int getRegId() { return regId; }
    public void setRegId(int regId) { this.regId = regId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public String getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(String registrationDate) { this.registrationDate = registrationDate; }

    @Override
    public String toString() {
        return "Registration{regId=" + regId + ", userId=" + userId + ", eventId=" + eventId + "}";
    }
}
