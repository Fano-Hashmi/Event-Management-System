package ds;

import dao.WaitingListDAO;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Waiting queue for events that are full.
 * Uses LinkedList-based Queue per event with DB persistence.
 */
public class WaitingQueue {
    private static HashMap<Integer, LinkedList<Integer>> waitingLists = new HashMap<>();

    public static void enqueue(int eventId, int userId) {
        waitingLists.computeIfAbsent(eventId, k -> new LinkedList<>()).add(userId);
        WaitingListDAO.enqueue(userId, eventId);
    }

    public static Integer dequeue(int eventId) {
        // Try in-memory first
        LinkedList<Integer> queue = waitingLists.get(eventId);
        if (queue != null && !queue.isEmpty()) {
            int userId = queue.poll();
            WaitingListDAO.removeUser(userId, eventId);
            return userId;
        }
        // Fallback to DB
        int userId = WaitingListDAO.dequeue(eventId);
        return userId > 0 ? userId : null;
    }

    public static int size(int eventId) {
        LinkedList<Integer> queue = waitingLists.get(eventId);
        if (queue != null) return queue.size();
        return WaitingListDAO.getQueueSize(eventId);
    }

    public static boolean isInQueue(int eventId, int userId) {
        LinkedList<Integer> queue = waitingLists.get(eventId);
        if (queue != null && queue.contains(userId)) return true;
        return WaitingListDAO.isInQueue(userId, eventId);
    }

    public static void removeFromQueue(int eventId, int userId) {
        LinkedList<Integer> queue = waitingLists.get(eventId);
        if (queue != null) queue.removeFirstOccurrence(userId);
        WaitingListDAO.removeUser(userId, eventId);
    }

    public static List<Integer> getWaitingList(int eventId) {
        LinkedList<Integer> queue = waitingLists.get(eventId);
        if (queue != null && !queue.isEmpty()) return new LinkedList<>(queue);
        return WaitingListDAO.getWaitingUserIds(eventId);
    }

    public static void clearEvent(int eventId) {
        waitingLists.remove(eventId);
        WaitingListDAO.clearEvent(eventId);
    }
}
