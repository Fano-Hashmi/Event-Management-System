package model;

/**
 * Represents attendance record for an event.
 * Tracks QR verification status.
 */
public class Attendance {
    private int attendanceId;
    private int userId;
    private int eventId;
    private boolean qrVerified;

    public Attendance() {}

    public Attendance(int attendanceId, int userId, int eventId, boolean qrVerified) {
        this.attendanceId = attendanceId;
        this.userId = userId;
        this.eventId = eventId;
        this.qrVerified = qrVerified;
    }

    public Attendance(int userId, int eventId, boolean qrVerified) {
        this.userId = userId;
        this.eventId = eventId;
        this.qrVerified = qrVerified;
    }

    // Getters and Setters
    public int getAttendanceId() { return attendanceId; }
    public void setAttendanceId(int attendanceId) { this.attendanceId = attendanceId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public boolean isQrVerified() { return qrVerified; }
    public void setQrVerified(boolean qrVerified) { this.qrVerified = qrVerified; }

    @Override
    public String toString() {
        return "Attendance{id=" + attendanceId + ", userId=" + userId +
               ", eventId=" + eventId + ", verified=" + qrVerified + "}";
    }
}
