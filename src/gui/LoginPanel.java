package gui;

import service.AuthService;
import util.SessionManager;
import javax.swing.*;
import java.awt.*;

/**
 * Login screen with email/password authentication and role-based routing.
 */
public class LoginPanel extends JPanel {
    private MainFrame mainFrame;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel errorLabel;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(ThemeConfig.BG_PRIMARY);
        setLayout(new GridBagLayout());
        initUI();
    }

    private void initUI() {
        JPanel card = ThemeConfig.createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(420, 480));

        JLabel icon = new JLabel("\uD83C\uDFAD");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Event Manager");
        title.setFont(ThemeConfig.FONT_TITLE);
        title.setForeground(ThemeConfig.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign in to your account");
        subtitle.setFont(ThemeConfig.FONT_BODY);
        subtitle.setForeground(ThemeConfig.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        errorLabel = new JLabel(" ");
        errorLabel.setFont(ThemeConfig.FONT_SMALL);
        errorLabel.setForeground(ThemeConfig.ACCENT_RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emailLabel = ThemeConfig.createLabel("Email Address");
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailField = ThemeConfig.createTextField("Enter your email");
        emailField.setMaximumSize(new Dimension(340, ThemeConfig.INPUT_HEIGHT));
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel passLabel = ThemeConfig.createLabel("Password");
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField = ThemeConfig.createPasswordField("Enter your password");
        passwordField.setMaximumSize(new Dimension(340, ThemeConfig.INPUT_HEIGHT));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton loginBtn = ThemeConfig.createPrimaryButton("Sign In");
        loginBtn.setMaximumSize(new Dimension(340, ThemeConfig.BUTTON_HEIGHT));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.addActionListener(e -> handleLogin());

        JButton registerLink = new JButton("Don't have an account? Create one");
        registerLink.setFont(ThemeConfig.FONT_SMALL);
        registerLink.setForeground(ThemeConfig.ACCENT_BLUE);
        registerLink.setBorderPainted(false); registerLink.setContentAreaFilled(false);
        registerLink.setFocusPainted(false);
        registerLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerLink.addActionListener(e -> mainFrame.showScreen(MainFrame.REGISTER));

        card.add(Box.createVerticalStrut(10)); card.add(icon);
        card.add(Box.createVerticalStrut(8)); card.add(title);
        card.add(Box.createVerticalStrut(4)); card.add(subtitle);
        card.add(Box.createVerticalStrut(16)); card.add(errorLabel);
        card.add(Box.createVerticalStrut(8)); card.add(emailLabel);
        card.add(Box.createVerticalStrut(4)); card.add(emailField);
        card.add(Box.createVerticalStrut(12)); card.add(passLabel);
        card.add(Box.createVerticalStrut(4)); card.add(passwordField);
        card.add(Box.createVerticalStrut(24)); card.add(loginBtn);
        card.add(Box.createVerticalStrut(16)); card.add(registerLink);

        passwordField.addActionListener(e -> handleLogin());
        emailField.addActionListener(e -> passwordField.requestFocus());
        add(card);
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        String error = AuthService.login(email, password);
        if (error != null) { errorLabel.setText(error); return; }

        errorLabel.setText(" "); emailField.setText(""); passwordField.setText("");

        if (SessionManager.isAdmin()) mainFrame.showAdminDashboard();
        else if (SessionManager.isStaff()) mainFrame.showStaffDashboard();
        else mainFrame.showUserDashboard();
    }
}
