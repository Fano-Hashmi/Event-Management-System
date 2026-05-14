package util;

import model.User;

/**
 * Manages the current user session.
 * Stores the logged-in user and provides role-checking utilities.
 */
public class SessionManager {
    private static User currentUser = null;

    public static void login(User user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    public static boolean isStaff() {
        return currentUser != null && currentUser.isStaff();
    }

    public static boolean isUser() {
        return currentUser != null && currentUser.isUser();
    }

    /**
     * Check if the current user can manage events (admin or staff for assigned).
     */
    public static boolean canManageEvents() {
        return isAdmin() || isStaff();
    }

    public static int getCurrentUserId() {
        return currentUser != null ? currentUser.getUserId() : -1;
    }

    public static String getCurrentUserName() {
        return currentUser != null ? currentUser.getName() : "Guest";
    }

    public static String getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole() : "NONE";
    }
}
