package ds;

import model.Event;
import java.util.Stack;

/**
 * Undo stack for deleted events.
 */
public class UndoStack {
    private static Stack<Event> deletedEvents = new Stack<>();

    public static void push(Event event) { deletedEvents.push(event); }
    public static Event pop() { return !deletedEvents.isEmpty() ? deletedEvents.pop() : null; }
    public static Event peek() { return !deletedEvents.isEmpty() ? deletedEvents.peek() : null; }
    public static boolean isEmpty() { return deletedEvents.isEmpty(); }
    public static int size() { return deletedEvents.size(); }
    public static void clear() { deletedEvents.clear(); }
}
