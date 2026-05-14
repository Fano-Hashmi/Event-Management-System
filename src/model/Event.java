package model;

/**
 * Represents an event in the Event Management System.
 * Supports paid/free events, staff assignment, and descriptions.
 */
public class Event {
    private int eventId;
    private String name;
    private String description;
    private String category;   // CONFERENCE, WORKSHOP, SEMINAR, CONCERT, SPORTS, OTHER
    private String date;       // Format: YYYY-MM-DD
    private String time;       // Format: HH:MM
    private String venue;
    private int capacity;
    private String eventType;  // PAID or FREE
    private double price;
    private int assignedStaffId;
    private int registeredCount;

    public Event() {}

    public Event(int eventId, String name, String description, String category, String date,
                 String time, String venue, int capacity, String eventType, double price, int assignedStaffId) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.category = category;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.capacity = capacity;
        this.eventType = eventType;
        this.price = price;
        this.assignedStaffId = assignedStaffId;
        this.registeredCount = 0;
    }

    public Event(String name, String description, String category, String date, String time,
                 String venue, int capacity, String eventType, double price, int assignedStaffId) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.capacity = capacity;
        this.eventType = eventType;
        this.price = price;
        this.assignedStaffId = assignedStaffId;
        this.registeredCount = 0;
    }

    // Getters and Setters
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getAssignedStaffId() { return assignedStaffId; }
    public void setAssignedStaffId(int assignedStaffId) { this.assignedStaffId = assignedStaffId; }

    public int getRegisteredCount() { return registeredCount; }
    public void setRegisteredCount(int registeredCount) { this.registeredCount = registeredCount; }

    public boolean isPaid() { return "PAID".equalsIgnoreCase(eventType); }
    public boolean isFull() { return registeredCount >= capacity; }
    public int getAvailableSlots() { return Math.max(0, capacity - registeredCount); }

    @Override
    public String toString() {
        return "Event{id=" + eventId + ", name='" + name + "', date='" + date +
               "', venue='" + venue + "', type='" + eventType + "'}";
    }
}
