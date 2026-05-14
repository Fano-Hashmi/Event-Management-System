package gui;

import model.User;
import model.Pass;
import service.AuthService;
import service.UserService;
import service.PaymentService;
import util.SessionManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ProfilePanel extends JPanel {
    private MainFrame mainFrame;
    private JPanel contentArea;

    public ProfilePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(ThemeConfig.BG_PRIMARY);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(24, 24, 24, 24));
        initUI();
    }

    private void initUI() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        header.setOpaque(false);
        JButton backBtn = ThemeConfig.createSecondaryButton("\u2190 Back");
        backBtn.setPreferredSize(new Dimension(90, 36));
        backBtn.addActionListener(e -> mainFrame.showUserDashboard());
        JLabel title = new JLabel("\uD83D\uDC64 My Profile");
        title.setFont(ThemeConfig.FONT_TITLE); title.setForeground(ThemeConfig.TEXT_PRIMARY);
        header.add(backBtn); header.add(title);

        contentArea = new JPanel();
        contentArea.setOpaque(false);
        contentArea.setLayout(new BoxLayout(contentArea, BoxLayout.Y_AXIS));
        JScrollPane sp = ThemeConfig.createScrollPane(contentArea);
        sp.setBorder(null); sp.getViewport().setBackground(ThemeConfig.BG_PRIMARY);

        add(header, BorderLayout.NORTH);
        add(sp, BorderLayout.CENTER);
    }

    public void refresh() {
        contentArea.removeAll();
        User user = SessionManager.getCurrentUser();
        if (user == null) return;

        contentArea.add(Box.createVerticalStrut(16));

        // Profile card
        JPanel profileCard = ThemeConfig.createCard();
        profileCard.setLayout(new BoxLayout(profileCard, BoxLayout.Y_AXIS));
        profileCard.setMaximumSize(new Dimension(500, 220));
        profileCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel avatar = new JLabel("\uD83D\uDC64");
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        JLabel name = new JLabel(user.getName());
        name.setFont(ThemeConfig.FONT_HEADING); name.setForeground(ThemeConfig.TEXT_PRIMARY);
        JLabel email = new JLabel(user.getEmail());
        email.setFont(ThemeConfig.FONT_BODY); email.setForeground(ThemeConfig.TEXT_SECONDARY);
        JLabel role = new JLabel("Role: " + user.getRole());
        role.setFont(ThemeConfig.FONT_BODY); role.setForeground(ThemeConfig.ACCENT_BLUE);
        JLabel status = new JLabel("Status: " + user.getStatus());
        status.setFont(ThemeConfig.FONT_BODY);
        status.setForeground(user.isActive() ? ThemeConfig.ACCENT_GREEN : ThemeConfig.ACCENT_RED);

        profileCard.add(avatar); profileCard.add(Box.createVerticalStrut(8));
        profileCard.add(name); profileCard.add(Box.createVerticalStrut(4));
        profileCard.add(email); profileCard.add(Box.createVerticalStrut(8));
        profileCard.add(role); profileCard.add(Box.createVerticalStrut(4));
        profileCard.add(status);
        contentArea.add(profileCard); contentArea.add(Box.createVerticalStrut(20));

        // Pass history
        JLabel passTitle = new JLabel("\uD83C\uDFAB Pass History");
        passTitle.setFont(ThemeConfig.FONT_HEADING); passTitle.setForeground(ThemeConfig.TEXT_PRIMARY);
        passTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentArea.add(passTitle); contentArea.add(Box.createVerticalStrut(8));

        List<Pass> passes = PaymentService.getUserPasses(user.getUserId());
        if (passes.isEmpty()) {
            JLabel np = new JLabel("No passes in history.");
            np.setFont(ThemeConfig.FONT_BODY); np.setForeground(ThemeConfig.TEXT_MUTED);
            np.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentArea.add(np);
        } else {
            for (Pass p : passes) {
                JPanel row = ThemeConfig.createCard();
                row.setLayout(new BorderLayout());
                row.setMaximumSize(new Dimension(500, 50));
                row.setAlignmentX(Component.LEFT_ALIGNMENT);
                JLabel pName = new JLabel(p.getEventName() + " — " + p.getIssueDate());
                pName.setFont(ThemeConfig.FONT_BODY); pName.setForeground(ThemeConfig.TEXT_PRIMARY);
                JLabel pStatus = new JLabel(p.isValidStatus() ? "VALID" : "USED");
                pStatus.setFont(ThemeConfig.FONT_SMALL);
                pStatus.setForeground(p.isValidStatus() ? ThemeConfig.ACCENT_GREEN : ThemeConfig.ACCENT_RED);
                row.add(pName, BorderLayout.CENTER); row.add(pStatus, BorderLayout.EAST);
                contentArea.add(row); contentArea.add(Box.createVerticalStrut(6));
            }
        }
        contentArea.add(Box.createVerticalStrut(24));

        // Delete account button
        JButton deleteBtn = ThemeConfig.createDangerButton("Delete My Account");
        deleteBtn.setMaximumSize(new Dimension(250, ThemeConfig.BUTTON_HEIGHT));
        deleteBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        deleteBtn.addActionListener(e -> {
            if (ThemeConfig.showConfirm(this, "Are you sure? This action is permanent and cannot be undone.")) {
                String err = UserService.deleteSelfAccount(user.getUserId());
                if (err != null) ThemeConfig.showError(this, err);
                else {
                    ThemeConfig.showSuccess(this, "Account deleted.");
                    AuthService.logout();
                    mainFrame.showScreen(MainFrame.LOGIN);
                }
            }
        });
        contentArea.add(deleteBtn);
        contentArea.revalidate(); contentArea.repaint();
    }
}
