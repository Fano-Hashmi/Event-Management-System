package service;

import dao.UserDAO;
import model.User;
import util.Validation;
import java.util.List;

/**
 * Business logic for user management (admin operations).
 * Supports ban/unban, delete, staff creation.
 */
public class UserService {

    public static List<User> getAllUsers() { return UserDAO.getAll(); }
    public static List<User> getAllStaff() { return UserDAO.getAllStaff(); }
    public static User getUserById(int userId) { return UserDAO.findById(userId); }
    public static int getTotalUserCount() { return UserDAO.getTotalCount(); }

    public static boolean updateUserRole(int userId, String newRole) {
        return UserDAO.updateRole(userId, newRole);
    }

    public static String banUser(int userId) {
        User user = UserDAO.findById(userId);
        if (user == null) return "User not found.";
        if (user.isAdmin()) return "Cannot ban an admin.";
        if (user.isBanned()) return "User is already banned.";
        boolean success = UserDAO.updateStatus(userId, "BANNED");
        return success ? null : "Failed to ban user.";
    }

    public static String unbanUser(int userId) {
        User user = UserDAO.findById(userId);
        if (user == null) return "User not found.";
        if (!user.isBanned()) return "User is not banned.";
        boolean success = UserDAO.updateStatus(userId, "ACTIVE");
        return success ? null : "Failed to unban user.";
    }

    public static String deleteUser(int userId) {
        User user = UserDAO.findById(userId);
        if (user == null) return "User not found.";
        if (user.isAdmin()) return "Cannot delete an admin account.";
        boolean success = UserDAO.delete(userId);
        return success ? null : "Failed to delete user.";
    }

    public static String deleteSelfAccount(int userId) {
        User user = UserDAO.findById(userId);
        if (user == null) return "User not found.";
        boolean success = UserDAO.delete(userId);
        return success ? null : "Failed to delete account.";
    }

    public static String createStaffAccount(String name, String email, String password) {
        if (Validation.isEmpty(name)) return "Name is required.";
        if (!Validation.isValidEmail(email)) return "Invalid email format.";
        if (!Validation.isValidPassword(password)) return "Password must be at least 4 characters.";

        User existing = UserDAO.findByEmail(email);
        if (existing != null) return "Email already registered.";

        String hashed = Validation.hashPassword(password);
        User staff = new User(name, email, hashed, "STAFF");
        boolean success = UserDAO.insert(staff);
        return success ? null : "Failed to create staff account.";
    }
}
