package gui;

import model.Event;
import model.User;
import model.Feedback;
import service.EventService;
import service.RegistrationService;
import service.PaymentService;
import service.FeedbackService;
import dao.UserDAO;
import util.SessionManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class EventDetailPanel extends JPanel {
    private MainFrame mainFrame;
    private JPanel contentPanel;
    private int currentEventId;

    public EventDetailPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(ThemeConfig.BG_PRIMARY);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(24, 24, 24, 24));
        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        JScrollPane sp = ThemeConfig.createScrollPane(contentPanel);
        sp.setBorder(null); sp.getViewport().setBackground(ThemeConfig.BG_PRIMARY);
        add(sp, BorderLayout.CENTER);
    }

    public void loadEvent(int eventId) {
        this.currentEventId = eventId;
        contentPanel.removeAll();
        Event event = EventService.getEventById(eventId);
        if (event == null) { contentPanel.add(new JLabel("Event not found.")); return; }

        // Back button
        JButton backBtn = ThemeConfig.createSecondaryButton("\u2190 Back");
        backBtn.setPreferredSize(new Dimension(90, 36));
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        backBtn.addActionListener(e -> mainFrame.showEventList());
        contentPanel.add(backBtn); contentPanel.add(Box.createVerticalStrut(16));

        // Title card
        JPanel titleCard = ThemeConfig.createCard();
        titleCard.setLayout(new BoxLayout(titleCard, BoxLayout.Y_AXIS));
        titleCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        titleCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel(event.getName());
        nameLabel.setFont(ThemeConfig.FONT_TITLE); nameLabel.setForeground(ThemeConfig.TEXT_PRIMARY);
        JLabel typeLabel = new JLabel(event.isPaid() ? "\uD83D\uDCB3 PAID EVENT" : "\u2705 FREE EVENT");
        typeLabel.setFont(ThemeConfig.FONT_SUBHEADING);
        typeLabel.setForeground(event.isPaid() ? ThemeConfig.ACCENT_AMBER : ThemeConfig.ACCENT_GREEN);
        JLabel catLabel = new JLabel("Category: " + event.getCategory());
        catLabel.setFont(ThemeConfig.FONT_BODY); catLabel.setForeground(ThemeConfig.TEXT_SECONDARY);

        titleCard.add(nameLabel); titleCard.add(Box.createVerticalStrut(8));
        titleCard.add(typeLabel); titleCard.add(Box.createVerticalStrut(4)); titleCard.add(catLabel);

        // Description
        if (event.getDescription() != null && !event.getDescription().isEmpty()) {
            titleCard.add(Box.createVerticalStrut(8));
            JLabel descLabel = new JLabel("<html><p style='width:500px'>" + event.getDescription() + "</p></html>");
            descLabel.setFont(ThemeConfig.FONT_BODY); descLabel.setForeground(ThemeConfig.TEXT_SECONDARY);
            titleCard.add(descLabel);
        }

        // Assigned staff
        if (event.getAssignedStaffId() > 0) {
            User staff = UserDAO.findById(event.getAssignedStaffId());
            if (staff != null) {
                titleCard.add(Box.createVerticalStrut(4));
                JLabel staffLbl = new JLabel("Assigned Staff: " + staff.getName());
                staffLbl.setFont(ThemeConfig.FONT_SMALL); staffLbl.setForeground(ThemeConfig.ACCENT_CYAN);
                titleCard.add(staffLbl);
            }
        }

        contentPanel.add(titleCard); contentPanel.add(Box.createVerticalStrut(16));

        // Details grid
        JPanel detailsCard = ThemeConfig.createCard();
        detailsCard.setLayout(new GridLayout(2, 3, 20, 12));
        detailsCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        detailsCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        addDetail(detailsCard, "\uD83D\uDCC5 Date", event.getDate());
        addDetail(detailsCard, "\u23F0 Time", event.getTime());
        addDetail(detailsCard, "\uD83D\uDCCD Venue", event.getVenue());
        addDetail(detailsCard, "\uD83D\uDC65 Capacity", event.getRegisteredCount() + " / " + event.getCapacity());
        addDetail(detailsCard, "\uD83D\uDCB0 Price", event.isPaid() ? "$" + String.format("%.2f", event.getPrice()) : "Free");
        addDetail(detailsCard, "\uD83D\uDCCA Available", event.getAvailableSlots() + " slots");
        contentPanel.add(detailsCard); contentPanel.add(Box.createVerticalStrut(16));

        if (SessionManager.isAdmin()) {
            JPanel adminActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
            adminActions.setOpaque(false);
            adminActions.setAlignmentX(Component.LEFT_ALIGNMENT);

            JButton editBtn = ThemeConfig.createSecondaryButton("Edit Event");
            editBtn.addActionListener(e -> mainFrame.showEditEvent(eventId));
            adminActions.add(editBtn);

            JButton deleteBtn = ThemeConfig.createDangerButton("Delete Event");
            deleteBtn.addActionListener(e -> {
                if (ThemeConfig.showConfirm(this, "Delete this event?")) {
                    String err = EventService.deleteEvent(eventId);
                    if (err != null) ThemeConfig.showError(this, err);
                    else {
                        ThemeConfig.showSuccess(this, "Event deleted.");
                        mainFrame.showEventList();
                    }
                }
            });
            adminActions.add(deleteBtn);
            contentPanel.add(adminActions);
            contentPanel.add(Box.createVerticalStrut(16));
        }

        // Action buttons for normal users and staff.
        if (!SessionManager.isAdmin()) {
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
            actionPanel.setOpaque(false); actionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            int userId = SessionManager.getCurrentUserId();
            boolean isRegistered = RegistrationService.isRegistered(userId, eventId);

            if (isRegistered) {
                JButton cancelBtn = ThemeConfig.createDangerButton("Cancel Registration");
                cancelBtn.addActionListener(e -> {
                    if (ThemeConfig.showConfirm(this, "Cancel your registration?")) {
                        String err = RegistrationService.cancelRegistration(userId, eventId);
                        if (err != null) ThemeConfig.showError(this, err);
                        else { ThemeConfig.showSuccess(this, "Registration cancelled."); loadEvent(eventId); }
                    }
                });
                actionPanel.add(cancelBtn);

                if (event.isPaid() && PaymentService.hasCompletedPayment(userId, eventId)) {
                    JButton passBtn = ThemeConfig.createPrimaryButton("View Pass");
                    passBtn.addActionListener(e -> mainFrame.showPasses());
                    actionPanel.add(passBtn);
                }

                // Feedback button (if registered)
                if (!FeedbackService.hasSubmitted(userId, eventId)) {
                    JButton fbBtn = ThemeConfig.createSecondaryButton("\u2B50 Rate Event");
                    fbBtn.addActionListener(e -> mainFrame.showFeedback(eventId));
                    actionPanel.add(fbBtn);
                }

                // Certificate button
                JButton certBtn = ThemeConfig.createSecondaryButton("\uD83C\uDF93 Certificate");
                certBtn.addActionListener(e -> mainFrame.showCertificate(eventId));
                actionPanel.add(certBtn);
            } else {
                if (event.isPaid()) {
                    JButton payBtn = ThemeConfig.createPrimaryButton("Pay & Register");
                    payBtn.addActionListener(e -> mainFrame.showPayment(eventId));
                    actionPanel.add(payBtn);
                } else {
                    JButton regBtn = ThemeConfig.createPrimaryButton("Register Now");
                    regBtn.addActionListener(e -> {
                        String err = RegistrationService.registerForEvent(userId, eventId);
                        if ("WAITING_LIST".equals(err)) ThemeConfig.showSuccess(this, "Event full. Added to waiting list.");
                        else if (err != null) ThemeConfig.showError(this, err);
                        else ThemeConfig.showSuccess(this, "Registered successfully!");
                        loadEvent(eventId);
                    });
                    actionPanel.add(regBtn);
                }
            }
            contentPanel.add(actionPanel); contentPanel.add(Box.createVerticalStrut(16));
        }

        // Participants
        JPanel partCard = ThemeConfig.createCard();
        partCard.setLayout(new BoxLayout(partCard, BoxLayout.Y_AXIS));
        partCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        partCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        JLabel partTitle = new JLabel("Registered Participants");
        partTitle.setFont(ThemeConfig.FONT_HEADING); partTitle.setForeground(ThemeConfig.TEXT_PRIMARY);
        partCard.add(partTitle); partCard.add(Box.createVerticalStrut(8));
        List<User> participants = RegistrationService.getEventParticipants(eventId);
        if (participants.isEmpty()) {
            JLabel np = new JLabel("No participants yet.");
            np.setFont(ThemeConfig.FONT_BODY); np.setForeground(ThemeConfig.TEXT_MUTED);
            partCard.add(np);
        } else {
            for (int i = 0; i < participants.size(); i++) {
                User u = participants.get(i);
                JLabel pl = new JLabel((i + 1) + ". " + u.getName() + " (" + u.getEmail() + ")");
                pl.setFont(ThemeConfig.FONT_BODY); pl.setForeground(ThemeConfig.TEXT_SECONDARY);
                pl.setBorder(new EmptyBorder(2, 8, 2, 0));
                partCard.add(pl);
            }
        }
        contentPanel.add(partCard); contentPanel.add(Box.createVerticalStrut(16));

        // Feedback section
        List<Feedback> feedbacks = FeedbackService.getEventFeedback(eventId);
        if (!feedbacks.isEmpty()) {
            JPanel fbCard = ThemeConfig.createCard();
            fbCard.setLayout(new BoxLayout(fbCard, BoxLayout.Y_AXIS));
            fbCard.setAlignmentX(Component.LEFT_ALIGNMENT);
            fbCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
            double avg = FeedbackService.getAverageRating(eventId);
            JLabel fbTitle = new JLabel("Reviews (" + feedbacks.size() + ") — Avg: " + String.format("%.1f", avg) + " \u2B50");
            fbTitle.setFont(ThemeConfig.FONT_HEADING); fbTitle.setForeground(ThemeConfig.TEXT_PRIMARY);
            fbCard.add(fbTitle); fbCard.add(Box.createVerticalStrut(8));
            for (Feedback fb : feedbacks) {
                User reviewer = UserDAO.findById(fb.getUserId());
                String name = reviewer != null ? reviewer.getName() : "User #" + fb.getUserId();
                JLabel rev = new JLabel(fb.getStarDisplay() + "  " + name + (fb.getComment().isEmpty() ? "" : " — \"" + fb.getComment() + "\""));
                rev.setFont(ThemeConfig.FONT_BODY); rev.setForeground(ThemeConfig.TEXT_SECONDARY);
                rev.setBorder(new EmptyBorder(2, 8, 2, 0));
                fbCard.add(rev);
            }
            contentPanel.add(fbCard);
        }

        contentPanel.revalidate(); contentPanel.repaint();
    }

    private void addDetail(JPanel panel, String label, String value) {
        JPanel item = new JPanel(new BorderLayout()); item.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(ThemeConfig.FONT_SMALL); lbl.setForeground(ThemeConfig.TEXT_MUTED);
        JLabel val = new JLabel(value);
        val.setFont(ThemeConfig.FONT_SUBHEADING); val.setForeground(ThemeConfig.TEXT_PRIMARY);
        item.add(lbl, BorderLayout.NORTH); item.add(val, BorderLayout.CENTER);
        panel.add(item);
    }
}
