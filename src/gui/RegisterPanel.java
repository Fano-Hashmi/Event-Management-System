package gui;

import service.AuthService;
import javax.swing.*;
import java.awt.*;

public class RegisterPanel extends JPanel {
    private MainFrame mainFrame;
    private JTextField nameField, emailField;
    private JPasswordField passwordField, confirmField;
    private JLabel errorLabel;

    public RegisterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(ThemeConfig.BG_PRIMARY);
        setLayout(new GridBagLayout());
        initUI();
    }

    private void initUI() {
        JPanel card = ThemeConfig.createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(420, 560));

        JLabel title = new JLabel("Create Account");
        title.setFont(ThemeConfig.FONT_TITLE); title.setForeground(ThemeConfig.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Join our event platform");
        subtitle.setFont(ThemeConfig.FONT_BODY); subtitle.setForeground(ThemeConfig.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        errorLabel = new JLabel(" ");
        errorLabel.setFont(ThemeConfig.FONT_SMALL); errorLabel.setForeground(ThemeConfig.ACCENT_RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        nameField = ThemeConfig.createTextField("Enter your name");
        emailField = ThemeConfig.createTextField("Enter your email");
        passwordField = ThemeConfig.createPasswordField("Create a password");
        confirmField = ThemeConfig.createPasswordField("Re-enter password");

        Dimension fs = new Dimension(340, ThemeConfig.INPUT_HEIGHT);
        for (JComponent f : new JComponent[]{nameField, emailField, passwordField, confirmField}) {
            f.setMaximumSize(fs); f.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        JButton registerBtn = ThemeConfig.createPrimaryButton("Create Account");
        registerBtn.setMaximumSize(new Dimension(340, ThemeConfig.BUTTON_HEIGHT));
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.addActionListener(e -> handleRegister());

        JButton loginLink = new JButton("Already have an account? Sign in");
        loginLink.setFont(ThemeConfig.FONT_SMALL); loginLink.setForeground(ThemeConfig.ACCENT_BLUE);
        loginLink.setBorderPainted(false); loginLink.setContentAreaFilled(false);
        loginLink.setFocusPainted(false); loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginLink.addActionListener(e -> mainFrame.showScreen(MainFrame.LOGIN));

        String[] labels = {"Full Name", "Email Address", "Password", "Confirm Password"};
        JComponent[] fields = {nameField, emailField, passwordField, confirmField};

        card.add(Box.createVerticalStrut(10)); card.add(title);
        card.add(Box.createVerticalStrut(4)); card.add(subtitle);
        card.add(Box.createVerticalStrut(12)); card.add(errorLabel);
        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = ThemeConfig.createLabel(labels[i]);
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(Box.createVerticalStrut(6)); card.add(lbl);
            card.add(Box.createVerticalStrut(4)); card.add(fields[i]);
        }
        card.add(Box.createVerticalStrut(20)); card.add(registerBtn);
        card.add(Box.createVerticalStrut(12)); card.add(loginLink);

        confirmField.addActionListener(e -> handleRegister());
        add(card);
    }

    private void handleRegister() {
        String error = AuthService.register(nameField.getText().trim(), emailField.getText().trim(),
            new String(passwordField.getPassword()), new String(confirmField.getPassword()));
        if (error != null) { errorLabel.setText(error); return; }
        ThemeConfig.showSuccess(this, "Account created successfully! Please sign in.");
        nameField.setText(""); emailField.setText(""); passwordField.setText(""); confirmField.setText("");
        errorLabel.setText(" ");
        mainFrame.showScreen(MainFrame.LOGIN);
    }
}
