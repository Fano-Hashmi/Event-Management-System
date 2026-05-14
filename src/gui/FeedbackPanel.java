package gui;

import model.Event;
import model.Feedback;
import service.EventService;
import service.FeedbackService;
import dao.UserDAO;
import model.User;
import util.SessionManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class FeedbackPanel extends JPanel {
    private MainFrame mainFrame;
    private JPanel contentPanel;
    private int currentEventId;
    private int selectedRating = 0;

    public FeedbackPanel(MainFrame mainFrame) {
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
        this.selectedRating = 0;
        contentPanel.removeAll();

        Event event = EventService.getEventById(eventId);
        if (event == null) { contentPanel.add(new JLabel("Event not found.")); return; }

        JButton backBtn = ThemeConfig.createSecondaryButton("\u2190 Back");
        backBtn.setPreferredSize(new Dimension(90, 36));
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        backBtn.addActionListener(e -> mainFrame.showEventDetail(eventId));
        contentPanel.add(backBtn); contentPanel.add(Box.createVerticalStrut(16));

        JLabel title = new JLabel("\u2B50 Rate Event: " + event.getName());
        title.setFont(ThemeConfig.FONT_TITLE); title.setForeground(ThemeConfig.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(title); contentPanel.add(Box.createVerticalStrut(20));

        int userId = SessionManager.getCurrentUserId();
        boolean alreadySubmitted = FeedbackService.hasSubmitted(userId, eventId);

        if (!alreadySubmitted) {
            // Rating form
            JPanel formCard = ThemeConfig.createCard();
            formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
            formCard.setMaximumSize(new Dimension(500, 350));
            formCard.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel ratingLabel = ThemeConfig.createLabel("Your Rating:");
            ratingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            formCard.add(ratingLabel); formCard.add(Box.createVerticalStrut(8));

            // Star buttons
            JPanel starPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            starPanel.setOpaque(false);
            JLabel ratingDisplay = new JLabel("Select a rating");
            ratingDisplay.setFont(ThemeConfig.FONT_BODY); ratingDisplay.setForeground(ThemeConfig.ACCENT_AMBER);

            JButton[] starButtons = new JButton[5];
            for (int i = 0; i < 5; i++) {
                final int star = i + 1;
                starButtons[i] = new JButton("\u2606");
                starButtons[i].setFont(new Font("Segoe UI", Font.PLAIN, 28));
                starButtons[i].setForeground(ThemeConfig.ACCENT_AMBER);
                starButtons[i].setBorderPainted(false); starButtons[i].setContentAreaFilled(false);
                starButtons[i].setFocusPainted(false);
                starButtons[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                starButtons[i].addActionListener(e -> {
                    selectedRating = star;
                    ratingDisplay.setText(star + " / 5 Stars");
                    for (int j = 0; j < 5; j++) {
                        starButtons[j].setText(j < star ? "\u2605" : "\u2606");
                    }
                });
                starPanel.add(starButtons[i]);
            }

            formCard.add(starPanel); formCard.add(Box.createVerticalStrut(4));
            formCard.add(ratingDisplay); formCard.add(Box.createVerticalStrut(12));

            JLabel commentLabel = ThemeConfig.createLabel("Comment (optional):");
            commentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            JTextArea commentArea = ThemeConfig.createTextArea("Share your experience...");
            JScrollPane commentScroll = new JScrollPane(commentArea);
            commentScroll.setPreferredSize(new Dimension(440, 80));
            commentScroll.setMaximumSize(new Dimension(440, 80));
            commentScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel errorLabel = new JLabel(" ");
            errorLabel.setFont(ThemeConfig.FONT_SMALL); errorLabel.setForeground(ThemeConfig.ACCENT_RED);
            errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JButton submitBtn = ThemeConfig.createPrimaryButton("Submit Feedback");
            submitBtn.setMaximumSize(new Dimension(440, ThemeConfig.BUTTON_HEIGHT));
            submitBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
            submitBtn.addActionListener(e -> {
                if (selectedRating == 0) { errorLabel.setText("Please select a rating."); return; }
                String err = FeedbackService.submitFeedback(userId, eventId, selectedRating, commentArea.getText().trim());
                if (err != null) errorLabel.setText(err);
                else {
                    ThemeConfig.showSuccess(this, "Thank you for your feedback!");
                    mainFrame.showEventDetail(eventId);
                }
            });

            formCard.add(commentLabel); formCard.add(Box.createVerticalStrut(4));
            formCard.add(commentScroll); formCard.add(Box.createVerticalStrut(8));
            formCard.add(errorLabel); formCard.add(Box.createVerticalStrut(8));
            formCard.add(submitBtn);
            contentPanel.add(formCard);
        } else {
            JLabel done = new JLabel("\u2705 You have already submitted feedback for this event.");
            done.setFont(ThemeConfig.FONT_BODY); done.setForeground(ThemeConfig.ACCENT_GREEN);
            done.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(done);
        }

        // Existing feedback
        contentPanel.add(Box.createVerticalStrut(24));
        List<Feedback> feedbacks = FeedbackService.getEventFeedback(eventId);
        if (!feedbacks.isEmpty()) {
            double avg = FeedbackService.getAverageRating(eventId);
            JLabel fbTitle = new JLabel("All Reviews (" + feedbacks.size() + ") — Average: " + String.format("%.1f", avg) + " \u2B50");
            fbTitle.setFont(ThemeConfig.FONT_HEADING); fbTitle.setForeground(ThemeConfig.TEXT_PRIMARY);
            fbTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(fbTitle); contentPanel.add(Box.createVerticalStrut(8));
            for (Feedback fb : feedbacks) {
                User reviewer = UserDAO.findById(fb.getUserId());
                String name = reviewer != null ? reviewer.getName() : "User #" + fb.getUserId();
                JPanel row = ThemeConfig.createCard();
                row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
                row.setMaximumSize(new Dimension(500, 70));
                row.setAlignmentX(Component.LEFT_ALIGNMENT);
                JLabel stars = new JLabel(fb.getStarDisplay() + "  " + name);
                stars.setFont(ThemeConfig.FONT_BODY); stars.setForeground(ThemeConfig.ACCENT_AMBER);
                row.add(stars);
                if (!fb.getComment().isEmpty()) {
                    JLabel comment = new JLabel("\"" + fb.getComment() + "\"");
                    comment.setFont(ThemeConfig.FONT_SMALL); comment.setForeground(ThemeConfig.TEXT_SECONDARY);
                    row.add(Box.createVerticalStrut(4)); row.add(comment);
                }
                contentPanel.add(row); contentPanel.add(Box.createVerticalStrut(6));
            }
        }

        contentPanel.revalidate(); contentPanel.repaint();
    }
}
