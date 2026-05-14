package service;

import dao.EventDAO;
import ds.EventBST;
import ds.EventStore;
import ds.UndoStack;
import ds.WaitingQueue;
import model.Event;
import util.Validation;
import java.util.List;

/**
 * Business logic for event management.
 */
public class EventService {

    public static void initialize() {
        EventStore.refresh();
        EventBST.rebuild(EventStore.getAllEvents());
    }

    public static String createEvent(String name, String description, String category,
                                     String date, String time, String venue, String capacityStr,
                                     String eventType, String priceStr, int assignedStaffId) {
        if (Validation.isEmpty(name)) return "Event name is required.";
        if (!Validation.isValidDate(date)) return "Invalid date format (YYYY-MM-DD).";
        if (!Validation.isValidTime(time)) return "Invalid time format (HH:MM).";
        if (Validation.isEmpty(venue)) return "Venue is required.";
        if (!Validation.isNonNegativeInteger(capacityStr)) return "Capacity must be a positive number.";

        int capacity = Integer.parseInt(capacityStr);
        if (capacity <= 0) return "Capacity must be greater than 0.";

        double price = 0.0;
        if ("PAID".equalsIgnoreCase(eventType)) {
            if (!Validation.isPositiveNumber(priceStr)) return "Price must be a positive number for paid events.";
            price = Double.parseDouble(priceStr);
        }

        Event event = new Event(name, description != null ? description : "", 
                                category != null ? category : "OTHER",
                                date, time, venue, capacity, eventType.toUpperCase(), price, assignedStaffId);
        int eventId = EventDAO.insert(event);
        if (eventId == -1) return "Failed to create event.";

        event.setEventId(eventId);
        EventStore.addEvent(event);
        EventBST.insert(event);
        return null;
    }

    public static String updateEvent(int eventId, String name, String description, String category,
                                     String date, String time, String venue, String capacityStr,
                                     String eventType, String priceStr, int assignedStaffId) {
        if (Validation.isEmpty(name)) return "Event name is required.";
        if (!Validation.isValidDate(date)) return "Invalid date format (YYYY-MM-DD).";
        if (!Validation.isValidTime(time)) return "Invalid time format (HH:MM).";
        if (Validation.isEmpty(venue)) return "Venue is required.";
        if (!Validation.isNonNegativeInteger(capacityStr)) return "Capacity must be a positive number.";

        int capacity = Integer.parseInt(capacityStr);
        double price = 0.0;
        if ("PAID".equalsIgnoreCase(eventType)) {
            if (!Validation.isPositiveNumber(priceStr)) return "Price must be a positive number.";
            price = Double.parseDouble(priceStr);
        }

        Event event = new Event(eventId, name, description != null ? description : "",
                                category != null ? category : "OTHER",
                                date, time, venue, capacity, eventType.toUpperCase(), price, assignedStaffId);
        boolean success = EventDAO.update(event);
        if (!success) return "Failed to update event.";

        EventStore.updateEvent(event);
        EventBST.rebuild(EventStore.getAllEvents());
        return null;
    }

    public static String deleteEvent(int eventId) {
        Event event = EventDAO.findById(eventId);
        if (event == null) return "Event not found.";
        boolean success = EventDAO.delete(eventId);
        if (!success) return "Failed to delete event.";
        UndoStack.push(event);
        EventStore.removeEvent(eventId);
        EventBST.remove(eventId);
        WaitingQueue.clearEvent(eventId);
        return null;
    }

    public static String undoDelete() {
        if (UndoStack.isEmpty()) return "No deleted events to restore.";
        Event event = UndoStack.pop();
        int newId = EventDAO.insert(event);
        if (newId == -1) return "Failed to restore event.";
        event.setEventId(newId);
        EventStore.addEvent(event);
        EventBST.insert(event);
        return null;
    }

    public static List<Event> getAllEvents() { return EventStore.getAllEvents(); }
    public static List<Event> searchByName(String name) { return EventStore.searchByName(name); }
    public static List<Event> searchByDate(String date) { return EventBST.searchByDate(date); }
    public static List<Event> filterByType(String type) { return EventStore.filterByType(type); }
    public static List<Event> filterByCategory(String cat) { return EventStore.filterByCategory(cat); }
    public static List<Event> getStaffEvents(int staffId) { return EventDAO.findByStaff(staffId); }
    public static Event getEventById(int eventId) { return EventDAO.findById(eventId); }
    public static boolean canUndoDelete() { return !UndoStack.isEmpty(); }
}
