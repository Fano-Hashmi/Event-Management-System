package gui;

import model.Event;
import service.EventService;
import service.PaymentService;
import service.RegistrationService;
import util.SessionManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PaymentPanel extends JPanel {
    private MainFrame mainFrame;
    private int currentEventId;
    private JLabel eventNameLabel, priceLabel, errorLabel;
    private JTextField cardNumberField, cardNameField;

    public PaymentPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(ThemeConfig.BG_PRIMARY);
        setLayout(new GridBagLayout());
        initUI();
    }

    private void initUI() {
        JPanel card = ThemeConfig.createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(460, 500));

        JButton backBtn = ThemeConfig.createSecondaryButton("\u2190 Back");
        backBtn.setPreferredSize(new Dimension(90, 32));
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        backBtn.addActionListener(e -> mainFrame.showEventDetail(currentEventId));

        JLabel title = new JLabel("\uD83D\uDCB3 Payment");
        title.setFont(ThemeConfig.FONT_TITLE); title.setForeground(ThemeConfig.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        eventNameLabel = new JLabel("Event: ");
        eventNameLabel.setFont(ThemeConfig.FONT_SUBHEADING); eventNameLabel.setForeground(ThemeConfig.TEXT_SECONDARY);
        eventNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        priceLabel = new JLabel("Amount: $0.00");
        priceLabel.setFont(ThemeConfig.FONT_HEADING); priceLabel.setForeground(ThemeConfig.ACCENT_GREEN);
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(ThemeConfig.DIVIDER); sep.setMaximumSize(new Dimension(420, 2));

        errorLabel = new JLabel(" ");
        errorLabel.setFont(ThemeConfig.FONT_SMALL); errorLabel.setForeground(ThemeConfig.ACCENT_RED);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        Dimension fs = new Dimension(420, ThemeConfig.INPUT_HEIGHT);
        JLabel cardLabel = ThemeConfig.createLabel("Card Number"); cardLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardNumberField = ThemeConfig.createTextField("1234 5678 9012 3456");
        cardNumberField.setMaximumSize(fs); cardNumberField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLabel = ThemeConfig.createLabel("Cardholder Name"); nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardNameField = ThemeConfig.createTextField("John Doe");
        cardNameField.setMaximumSize(fs); cardNameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton payBtn = ThemeConfig.createPrimaryButton("Pay Now");
        payBtn.setMaximumSize(new Dimension(420, ThemeConfig.BUTTON_HEIGHT));
        payBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        payBtn.addActionListener(e -> handlePayment());

        card.add(backBtn); card.add(Box.createVerticalStrut(8));
        card.add(title); card.add(Box.createVerticalStrut(12));
        card.add(eventNameLabel); card.add(Box.createVerticalStrut(8));
        card.add(priceLabel); card.add(Box.createVerticalStrut(16));
        card.add(sep); card.add(Box.createVerticalStrut(16));
        card.add(errorLabel); card.add(Box.createVerticalStrut(8));
        card.add(cardLabel); card.add(Box.createVerticalStrut(4));
        card.add(cardNumberField); card.add(Box.createVerticalStrut(12));
        card.add(nameLabel); card.add(Box.createVerticalStrut(4));
        card.add(cardNameField); card.add(Box.createVerticalStrut(24));
        card.add(payBtn);
        add(card);
    }

    public void loadEvent(int eventId) {
        this.currentEventId = eventId;
        Event event = EventService.getEventById(eventId);
        if (event != null) {
            eventNameLabel.setText("Event: " + event.getName());
            priceLabel.setText("Amount: $" + String.format("%.2f", event.getPrice()));
        }
        cardNumberField.setText(""); cardNameField.setText(""); errorLabel.setText(" ");
    }

    private void handlePayment() {
        int userId = SessionManager.getCurrentUserId();
        String error = PaymentService.processPayment(userId, currentEventId,
            cardNumberField.getText().trim(), cardNameField.getText().trim());
        if (error != null) { errorLabel.setText(error); return; }

        String regError = RegistrationService.registerForEvent(userId, currentEventId);
        if (regError != null && !"WAITING_LIST".equals(regError))
            System.err.println("Registration after payment: " + regError);

        ThemeConfig.showSuccess(this, "Payment successful! Your event pass has been generated.");
        mainFrame.showPasses();
    }
}
