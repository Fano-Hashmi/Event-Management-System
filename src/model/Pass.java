package model;

/**
 * Represents an event pass issued after successful payment or free registration.
 */
public class Pass {
    private int passId;
    private int userId;
    private int eventId;
    private String eventName;
    private String userName;
    private String qrCode;
    private boolean validStatus;
    private String issueDate;

    public Pass() {}

    public Pass(int passId, int userId, int eventId, String eventName, String userName,
                String qrCode, boolean validStatus, String issueDate) {
        this.passId = passId;
        this.userId = userId;
        this.eventId = eventId;
        this.eventName = eventName;
        this.userName = userName;
        this.qrCode = qrCode;
        this.validStatus = validStatus;
        this.issueDate = issueDate;
    }

    public Pass(int userId, int eventId, String eventName, String userName,
                String qrCode, boolean validStatus, String issueDate) {
        this.userId = userId;
        this.eventId = eventId;
        this.eventName = eventName;
        this.userName = userName;
        this.qrCode = qrCode;
        this.validStatus = validStatus;
        this.issueDate = issueDate;
    }

    // Getters and Setters
    public int getPassId() { return passId; }
    public void setPassId(int passId) { this.passId = passId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }

    public boolean isValidStatus() { return validStatus; }
    public void setValidStatus(boolean validStatus) { this.validStatus = validStatus; }

    public String getIssueDate() { return issueDate; }
    public void setIssueDate(String issueDate) { this.issueDate = issueDate; }

    @Override
    public String toString() {
        return "Pass{id=" + passId + ", userId=" + userId + ", eventId=" + eventId +
               ", qr='" + qrCode + "', valid=" + validStatus + "}";
    }
}
