import service.EventService;
import database.DBConnection;

public class Test {
    public static void main(String[] args) {
        DBConnection.getConnection();
        String res = EventService.createEvent("Test Error", "Desc", "OTHER", "2026-10-10", "10:00", "Venue", "100", "FREE", "0", 0);
        System.out.println("Result: " + res);
    }
}
