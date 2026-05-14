package service;

import dao.RegistrationDAO;
import dao.EventDAO;
import dao.UserDAO;
import ds.EventStore;
import ds.WaitingQueue;
import model.Event;
import model.Registration;
import model.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RegistrationService {

    public static String registerForEvent(int userId, int eventId) {
        if (RegistrationDAO.isRegistered(userId, eventId))
            return "You are already registered for this event.";

        Event event = EventDAO.findById(eventId);
        if (event == null) return "Event not found.";

        int count = RegistrationDAO.getCountByEvent(eventId);
        if (count >= event.getCapacity()) {
            WaitingQueue.enqueue(eventId, userId);
            return "WAITING_LIST";
        }

        Registration reg = new Registration(userId, eventId, LocalDate.now().toString());
        boolean success = RegistrationDAO.insert(reg);
        if (!success) return "Registration failed.";

        User user = UserDAO.findById(userId);
        if (user != null) EventStore.addParticipant(eventId, user);
        return null;
    }

    public static String cancelRegistration(int userId, int eventId) {
        if (!RegistrationDAO.isRegistered(userId, eventId))
            return "You are not registered for this event.";

        boolean success = RegistrationDAO.delete(userId, eventId);
        if (!success) return "Cancellation failed.";
        EventStore.removeParticipant(eventId, userId);

        // Process waiting queue
        Integer nextUserId = WaitingQueue.dequeue(eventId);
        if (nextUserId != null) {
            Registration reg = new Registration(nextUserId, eventId, LocalDate.now().toString());
            RegistrationDAO.insert(reg);
            User user = UserDAO.findById(nextUserId);
            if (user != null) EventStore.addParticipant(eventId, user);
        }
        return null;
    }

    public static List<Event> getRegisteredEvents(int userId) {
        List<Registration> regs = RegistrationDAO.findByUser(userId);
        List<Event> events = new ArrayList<>();
        for (Registration r : regs) {
            Event e = EventDAO.findById(r.getEventId());
            if (e != null) events.add(e);
        }
        return events;
    }

    public static List<User> getEventParticipants(int eventId) {
        return EventStore.getParticipants(eventId);
    }

    public static boolean isRegistered(int userId, int eventId) {
        return RegistrationDAO.isRegistered(userId, eventId);
    }

    public static boolean isInWaitingList(int eventId, int userId) {
        return WaitingQueue.isInQueue(eventId, userId);
    }

    public static int getWaitingListSize(int eventId) {
        return WaitingQueue.size(eventId);
    }
}
