package gui;

import service.AuthService;
import service.EventService;
import service.FeedbackService;
import service.UserService;
import model.Event;
import model.User;
import dao.RegistrationDAO;
import dao.UserDAO;
import model.Registration;
import util.SessionManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Staff dashboard — shows only assigned events.
 */
public class StaffDashboard extends JPanel {
    private MainFrame mainFrame;
    private JPanel contentArea;
    private JLabel welcomeLabel;

    public StaffDashboard(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(ThemeConfig.BG_PRIMARY);
        setLayout(new BorderLayout());
        initUI();
    }

    private void initUI() {
        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setBackground(ThemeConfig.BG_SECONDARY);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(20, 16, 20, 16));

        JLabel brand = new JLabel("  \uD83D\uDCCB Staff Panel");
        brand.setFont(ThemeConfig.FONT_HEADING); brand.setForeground(ThemeConfig.TEXT_PRIMARY);
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        brandPanel.setOpaque(false); brandPanel.setMaximumSize(new Dimension(220, 40));
        brandPanel.add(brand);
        sidebar.add(brandPanel); sidebar.add(Box.createVerticalStrut(30));

        String[][] menuItems = {
            {"My Events", "EVENTS"}, {"Verify QR Pass", "QR"},
            {"Delete My Account", "DELETE"}, {"Logout", "LOGOUT"}
        };
        for (String[] item : menuItems) {
            JButton btn = ThemeConfig.createSidebarButton(item[0]);
            String action = item[1];
            btn.addActionListener(e -> handleAction(action));
            sidebar.add(btn); sidebar.add(Box.createVerticalStrut(4));
        }
        sidebar.add(Box.createVerticalGlue());

        // Content
        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBackground(ThemeConfig.BG_PRIMARY);
        content.setBorder(new EmptyBorder(30, 30, 30, 30));

        welcomeLabel = new JLabel("Welcome, Staff");
        welcomeLabel.setFont(ThemeConfig.FONT_TITLE); welcomeLabel.setForeground(ThemeConfig.TEXT_PRIMARY);

        contentArea = new JPanel();
        contentArea.setOpaque(false);
        contentArea.setLayout(new BoxLayout(contentArea, BoxLayout.Y_AXIS));

        JScrollPane sp = new JScrollPane(contentArea);
        sp.setBorder(null); sp.setOpaque(false); sp.getViewport().setOpaque(false);

        content.add(welcomeLabel, BorderLayout.NORTH);
        content.add(sp, BorderLayout.CENTER);

        add(sidebar, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
    }

    private void handleAction(String action) {
        switch (action) {
            case "EVENTS" -> refresh();
            case "QR" -> showQRVerification();
            case "DELETE" -> handleDeleteAccount();
            case "LOGOUT" -> { AuthService.logout(); mainFrame.showScreen(MainFrame.LOGIN); }
        }
    }

    private void showQRVerification() {
        contentArea.removeAll();
        JLabel title = new JLabel("\uD83D\uDD0D Verify QR Event Pass");
        title.setFont(ThemeConfig.FONT_HEADING); title.setForeground(ThemeConfig.TEXT_PRIMARY);
        contentArea.add(title); contentArea.add(Box.createVerticalStrut(16));

        JPanel card = ThemeConfig.createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setMaximumSize(new Dimension(500, 200));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = ThemeConfig.createLabel("Enter QR Code:");
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField qrField = ThemeConfig.createTextField("QR-XXXX-XXXX-XXXX");
        qrField.setMaximumSize(new Dimension(440, ThemeConfig.INPUT_HEIGHT));
        qrField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel resultLabel = new JLabel(" ");
        resultLabel.setFont(ThemeConfig.FONT_BODY); resultLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton verifyBtn = ThemeConfig.createPrimaryButton("Verify & Mark Attendance");
        verifyBtn.setMaximumSize(new Dimension(440, ThemeConfig.BUTTON_HEIGHT));
        verifyBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        verifyBtn.addActionListener(e -> {
            String qr = qrField.getText().trim();
            if (qr.isEmpty()) { resultLabel.setText("Please enter a QR code."); resultLabel.setForeground(ThemeConfig.ACCENT_RED); return; }
            String err = FeedbackService.verifyQRAndMarkAttendance(qr);
            if (err != null) { resultLabel.setText(err); resultLabel.setForeground(ThemeConfig.ACCENT_RED); }
            else { resultLabel.setText("\u2705 Attendance verified successfully!"); resultLabel.setForeground(ThemeConfig.ACCENT_GREEN); qrField.setText(""); }
        });

        card.add(lbl); card.add(Box.createVerticalStrut(8));
        card.add(qrField); card.add(Box.createVerticalStrut(12));
        card.add(verifyBtn); card.add(Box.createVerticalStrut(8));
        card.add(resultLabel);
        contentArea.add(card);
        contentArea.revalidate(); contentArea.repaint();
    }

    private void handleDeleteAccount() {
        if (ThemeConfig.showConfirm(this, "Are you sure you want to delete your account?\nThis action cannot be undone.")) {
            String err = UserService.deleteSelfAccount(SessionManager.getCurrentUserId());
            if (err != null) ThemeConfig.showError(this, err);
            else {
                ThemeConfig.showSuccess(this, "Account deleted successfully.");
                AuthService.logout();
                mainFrame.showScreen(MainFrame.LOGIN);
            }
        }
    }

    public void refresh() {
        if (SessionManager.isLoggedIn())
            welcomeLabel.setText("Welcome, " + SessionManager.getCurrentUserName());

        contentArea.removeAll();
        int staffId = SessionManager.getCurrentUserId();
        List<Event> events = EventService.getStaffEvents(staffId);

        JLabel title = new JLabel("My Assigned Events (" + events.size() + ")");
        title.setFont(ThemeConfig.FONT_HEADING); title.setForeground(ThemeConfig.TEXT_PRIMARY);
        contentArea.add(title); contentArea.add(Box.createVerticalStrut(12));

        if (events.isEmpty()) {
            JLabel empty = new JLabel("No events assigned to you yet.");
            empty.setFont(ThemeConfig.FONT_BODY); empty.setForeground(ThemeConfig.TEXT_MUTED);
            contentArea.add(empty);
        } else {
            for (Event ev : events) {
                JPanel card = ThemeConfig.createCard();
                card.setLayout(new BorderLayout(12, 0));
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
                card.setAlignmentX(Component.LEFT_ALIGNMENT);

                JPanel info = new JPanel(); info.setOpaque(false);
                info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
                JLabel name = new JLabel(ev.getName());
                name.setFont(ThemeConfig.FONT_SUBHEADING); name.setForeground(ThemeConfig.TEXT_PRIMARY);
                JLabel details = new JLabel(ev.getDate() + " | " + ev.getTime() + " | " + ev.getVenue());
                details.setFont(ThemeConfig.FONT_SMALL); details.setForeground(ThemeConfig.TEXT_SECONDARY);
                JLabel seats = new JLabel("Seats: " + ev.getRegisteredCount() + "/" + ev.getCapacity() +
                    " | Available: " + ev.getAvailableSlots());
                seats.setFont(ThemeConfig.FONT_SMALL); seats.setForeground(ThemeConfig.ACCENT_GREEN);
                info.add(name); info.add(Box.createVerticalStrut(4)); info.add(details);
                info.add(Box.createVerticalStrut(4)); info.add(seats);

                JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
                btns.setOpaque(false);
                JButton viewBtn = ThemeConfig.createPrimaryButton("View Details");
                viewBtn.setPreferredSize(new Dimension(120, 34));
                viewBtn.addActionListener(e -> mainFrame.showEventDetail(ev.getEventId()));
                JButton attendBtn = ThemeConfig.createSecondaryButton("Attendance");
                attendBtn.setPreferredSize(new Dimension(110, 34));
                attendBtn.addActionListener(e -> showAttendanceDialog(ev));
                btns.add(attendBtn); btns.add(viewBtn);

                card.add(info, BorderLayout.CENTER);
                card.add(btns, BorderLayout.EAST);
                contentArea.add(card); contentArea.add(Box.createVerticalStrut(10));
            }
        }
        contentArea.revalidate(); contentArea.repaint();
    }

    private void showAttendanceDialog(Event event) {
        List<Registration> regs = RegistrationDAO.findByEvent(event.getEventId());
        StringBuilder sb = new StringBuilder("Participants for: " + event.getName() + "\n\n");
        int i = 1;
        for (Registration r : regs) {
            User u = UserDAO.findById(r.getUserId());
            if (u != null) {
                boolean attended = FeedbackService.isAttended(u.getUserId(), event.getEventId());
                sb.append(i++).append(". ").append(u.getName()).append(" (").append(u.getEmail()).append(") - ");
                sb.append(attended ? "ATTENDED" : "NOT YET").append("\n");
            }
        }
        if (regs.isEmpty()) sb.append("No participants registered yet.");
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false); area.setFont(ThemeConfig.FONT_BODY);
        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(500, 300));
        JOptionPane.showMessageDialog(this, sp, "Attendance - " + event.getName(), JOptionPane.PLAIN_MESSAGE);
    }
}
