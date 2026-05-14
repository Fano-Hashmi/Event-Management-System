package model;

/**
 * Represents user feedback for an event.
 * Includes a 1-5 star rating and optional comment.
 */
public class Feedback {
    private int feedbackId;
    private int userId;
    private int eventId;
    private int rating; // 1-5
    private String comment;

    public Feedback() {}

    public Feedback(int feedbackId, int userId, int eventId, int rating, String comment) {
        this.feedbackId = feedbackId;
        this.userId = userId;
        this.eventId = eventId;
        this.rating = rating;
        this.comment = comment;
    }

    public Feedback(int userId, int eventId, int rating, String comment) {
        this.userId = userId;
        this.eventId = eventId;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters and Setters
    public int getFeedbackId() { return feedbackId; }
    public void setFeedbackId(int feedbackId) { this.feedbackId = feedbackId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = Math.max(1, Math.min(5, rating)); }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getStarDisplay() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(i < rating ? "\u2605" : "\u2606");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Feedback{id=" + feedbackId + ", userId=" + userId + ", eventId=" + eventId +
               ", rating=" + rating + "}";
    }
}
