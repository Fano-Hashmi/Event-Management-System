package ds;

import model.Event;
import java.util.ArrayList;
import java.util.List;

/**
 * Binary Search Tree for fast event search by date.
 */
public class EventBST {
    private static BSTNode root = null;

    private static class BSTNode {
        Event event;
        BSTNode left, right;
        BSTNode(Event event) { this.event = event; }
    }

    public static void clear() { root = null; }

    public static void insert(Event event) { root = insertRec(root, event); }

    private static BSTNode insertRec(BSTNode node, Event event) {
        if (node == null) return new BSTNode(event);
        int cmp = event.getDate().compareTo(node.event.getDate());
        if (cmp < 0) node.left = insertRec(node.left, event);
        else if (cmp > 0) node.right = insertRec(node.right, event);
        else {
            if (event.getEventId() < node.event.getEventId()) node.left = insertRec(node.left, event);
            else node.right = insertRec(node.right, event);
        }
        return node;
    }

    public static List<Event> searchByDate(String date) {
        List<Event> results = new ArrayList<>();
        searchByDateRec(root, date, results);
        return results;
    }

    private static void searchByDateRec(BSTNode node, String date, List<Event> results) {
        if (node == null) return;
        int cmp = date.compareTo(node.event.getDate());
        if (cmp <= 0) searchByDateRec(node.left, date, results);
        if (node.event.getDate().equals(date)) results.add(node.event);
        if (cmp >= 0) searchByDateRec(node.right, date, results);
    }

    public static Event searchById(int eventId) { return searchByIdRec(root, eventId); }

    private static Event searchByIdRec(BSTNode node, int eventId) {
        if (node == null) return null;
        if (node.event.getEventId() == eventId) return node.event;
        Event left = searchByIdRec(node.left, eventId);
        return left != null ? left : searchByIdRec(node.right, eventId);
    }

    public static List<Event> inorderTraversal() {
        List<Event> result = new ArrayList<>();
        inorderRec(root, result); return result;
    }

    private static void inorderRec(BSTNode node, List<Event> result) {
        if (node == null) return;
        inorderRec(node.left, result);
        result.add(node.event);
        inorderRec(node.right, result);
    }

    public static void remove(int eventId) { root = removeRec(root, eventId); }

    private static BSTNode removeRec(BSTNode node, int eventId) {
        if (node == null) return null;
        if (node.event.getEventId() == eventId) {
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;
            BSTNode min = findMin(node.right);
            node.event = min.event;
            node.right = removeRec(node.right, min.event.getEventId());
        } else {
            node.left = removeRec(node.left, eventId);
            node.right = removeRec(node.right, eventId);
        }
        return node;
    }

    private static BSTNode findMin(BSTNode node) {
        while (node.left != null) node = node.left;
        return node;
    }

    public static void rebuild(List<Event> events) {
        clear();
        for (Event e : events) insert(e);
    }
}
