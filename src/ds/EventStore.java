package ds;

import model.Event;
import model.User;
import dao.EventDAO;
import dao.UserDAO;
import dao.RegistrationDAO;
import model.Registration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * In-memory event store using ArrayList and HashMap.
 * ArrayList stores all events; HashMap maps eventId to participant list.
 */
public class EventStore {
    private static ArrayList<Event> allEvents = new ArrayList<>();
    private static HashMap<Integer, List<User>> eventParticipants = new HashMap<>();

    public static void refresh() {
        allEvents.clear();
        eventParticipants.clear();
        allEvents.addAll(EventDAO.getAll());
        for (Event e : allEvents) {
            List<Registration> regs = RegistrationDAO.findByEvent(e.getEventId());
            List<User> users = new ArrayList<>();
            for (Registration r : regs) {
                User u = UserDAO.findById(r.getUserId());
                if (u != null) users.add(u);
            }
            eventParticipants.put(e.getEventId(), users);
        }
    }

    public static ArrayList<Event> getAllEvents() {
        if (allEvents.isEmpty()) refresh();
        return allEvents;
    }

    public static void addEvent(Event event) {
        allEvents.add(event);
        eventParticipants.put(event.getEventId(), new ArrayList<>());
    }

    public static void removeEvent(int eventId) {
        allEvents.removeIf(e -> e.getEventId() == eventId);
        eventParticipants.remove(eventId);
    }

    public static void updateEvent(Event updated) {
        for (int i = 0; i < allEvents.size(); i++) {
            if (allEvents.get(i).getEventId() == updated.getEventId()) {
                allEvents.set(i, updated); break;
            }
        }
    }

    public static List<User> getParticipants(int eventId) {
        return eventParticipants.getOrDefault(eventId, new ArrayList<>());
    }

    public static void addParticipant(int eventId, User user) {
        eventParticipants.computeIfAbsent(eventId, k -> new ArrayList<>()).add(user);
    }

    public static void removeParticipant(int eventId, int userId) {
        List<User> list = eventParticipants.get(eventId);
        if (list != null) list.removeIf(u -> u.getUserId() == userId);
    }

    public static Event findById(int eventId) {
        for (Event e : allEvents) {
            if (e.getEventId() == eventId) return e;
        }
        return null;
    }

    public static List<Event> searchByName(String name) {
        String lower = name.toLowerCase();
        return allEvents.stream()
            .filter(e -> e.getName().toLowerCase().contains(lower))
            .collect(Collectors.toList());
    }

    public static List<Event> filterByType(String type) {
        return allEvents.stream()
            .filter(e -> e.getEventType().equalsIgnoreCase(type))
            .collect(Collectors.toList());
    }

    public static List<Event> filterByCategory(String category) {
        return allEvents.stream()
            .filter(e -> e.getCategory().equalsIgnoreCase(category))
            .collect(Collectors.toList());
    }

    public static Map<Integer, List<User>> getEventParticipantsMap() {
        return eventParticipants;
    }
}
