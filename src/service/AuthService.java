package service;

import dao.UserDAO;
import model.User;
import util.SessionManager;
import util.Validation;

/**
 * Authentication service handling login, registration, and logout.
 */
public class AuthService {

    public static String login(String email, String password) {
        if (Validation.isEmpty(email) || Validation.isEmpty(password))
            return "Email and password are required.";
        if (!Validation.isValidEmail(email))
            return "Invalid email format.";

        User user = UserDAO.authenticate(email, password);
        if (user == null)
            return "Invalid email or password.";

        // Check ban status
        if (user.isBanned())
            return "Your account has been banned. Contact admin.";

        SessionManager.login(user);
        return null; // success
    }

    public static String register(String name, String email, String password, String confirmPassword) {
        if (Validation.isEmpty(name)) return "Name is required.";
        if (!Validation.isValidName(name)) return "Name must be at least 2 characters.";
        if (Validation.isEmpty(email)) return "Email is required.";
        if (!Validation.isValidEmail(email)) return "Invalid email format.";
        if (Validation.isEmpty(password)) return "Password is required.";
        if (!Validation.isValidPassword(password)) return "Password must be at least 4 characters.";
        if (!password.equals(confirmPassword)) return "Passwords do not match.";

        User existing = UserDAO.findByEmail(email);
        if (existing != null) return "An account with this email already exists.";

        String hashedPassword = Validation.hashPassword(password);
        User newUser = new User(name, email, hashedPassword, "USER");
        boolean success = UserDAO.insert(newUser);
        if (!success) return "Registration failed. Please try again.";

        return null; // success
    }

    public static void logout() {
        SessionManager.logout();
    }
}
