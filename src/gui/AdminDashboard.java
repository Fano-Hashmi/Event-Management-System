package gui;

import dao.EventDAO;
import dao.RegistrationDAO;
import dao.UserDAO;
import service.AuthService;
import service.EventService;
import model.Event;
import util.SessionManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Admin dashboard with full analytics, sidebar navigation.
 */
public class AdminDashboard extends JPanel {
    private MainFrame mainFrame;
    private JPanel statsPanel, popularPanel;
    private JLabel welcomeLabel;

    public AdminDashboard(MainFrame mainFrame) {
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

        JLabel brand = new JLabel("  \uD83C\uDFAD Admin Panel");
        brand.setFont(ThemeConfig.FONT_HEADING); brand.setForeground(ThemeConfig.TEXT_PRIMARY);
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        brandPanel.setOpaque(false); brandPanel.setMaximumSize(new Dimension(220, 40));
        brandPanel.add(brand);
        sidebar.add(brandPanel); sidebar.add(Box.createVerticalStrut(30));

        String[][] menuItems = {
            {"Dashboard", "HOME"}, {"All Events", "EVENTS"}, {"Add Event", "ADD"},
            {"Manage Users", "USERS"}, {"View Feedback", "FEEDBACK"},
            {"Undo Delete", "UNDO"}, {"Logout", "LOGOUT"}
        };
        for (String[] item : menuItems) {
            JButton btn = ThemeConfig.createSidebarButton(item[0]);
            String action = item[1];
            btn.addActionListener(e -> handleAction(action));
            sidebar.add(btn); sidebar.add(Box.createVerticalStrut(4));
        }
        sidebar.add(Box.createVerticalGlue());

        // Main content
        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setBackground(ThemeConfig.BG_PRIMARY);
        content.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header
        welcomeLabel = new JLabel("Welcome back, Admin");
        welcomeLabel.setFont(ThemeConfig.FONT_TITLE); welcomeLabel.setForeground(ThemeConfig.TEXT_PRIMARY);
        JLabel sub = new JLabel("Event management overview & analytics");
        sub.setFont(ThemeConfig.FONT_BODY); sub.setForeground(ThemeConfig.TEXT_SECONDARY);
        JPanel header = new JPanel();
        header.setOpaque(false); header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(welcomeLabel); header.add(Box.createVerticalStrut(4)); header.add(sub);

        // Stats row
        statsPanel = new JPanel(new GridLayout(1, 5, 12, 0));
        statsPanel.setOpaque(false);

        // Popular events
        popularPanel = new JPanel();
        popularPanel.setOpaque(false);
        popularPanel.setLayout(new BoxLayout(popularPanel, BoxLayout.Y_AXIS));

        // Quick actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        actionsPanel.setOpaque(false);
        JButton addBtn = ThemeConfig.createPrimaryButton("+ Add New Event");
        addBtn.addActionListener(e -> mainFrame.showAddEvent());
        JButton viewBtn = ThemeConfig.createSecondaryButton("View All Events");
        viewBtn.addActionListener(e -> mainFrame.showEventList());
        JButton usersBtn = ThemeConfig.createSecondaryButton("Manage Users");
        usersBtn.addActionListener(e -> mainFrame.showUserManagement());
        actionsPanel.add(addBtn); actionsPanel.add(viewBtn); actionsPanel.add(usersBtn);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(statsPanel); center.add(Box.createVerticalStrut(24));
        center.add(actionsPanel); center.add(Box.createVerticalStrut(24));
        center.add(popularPanel);

        JScrollPane sp = new JScrollPane(center);
        sp.setBorder(null); sp.setOpaque(false);
        sp.getViewport().setOpaque(false);

        content.add(header, BorderLayout.NORTH);
        content.add(sp, BorderLayout.CENTER);

        add(sidebar, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
    }

    private void handleAction(String action) {
        switch (action) {
            case "HOME" -> refresh();
            case "EVENTS" -> mainFrame.showEventList();
            case "ADD" -> mainFrame.showAddEvent();
            case "USERS" -> mainFrame.showUserManagement();
            case "FEEDBACK" -> mainFrame.showEventList(); // View events to select feedback
            case "UNDO" -> {
                String err = EventService.undoDelete();
                if (err != null) ThemeConfig.showError(this, err);
                else { ThemeConfig.showSuccess(this, "Event restored!"); refresh(); }
            }
            case "LOGOUT" -> { AuthService.logout(); mainFrame.showScreen(MainFrame.LOGIN); }
        }
    }

    public void refresh() {
        if (SessionManager.isLoggedIn())
            welcomeLabel.setText("Welcome back, " + SessionManager.getCurrentUserName());

        statsPanel.removeAll();
        statsPanel.add(ThemeConfig.createStatCard("Total Events", String.valueOf(EventDAO.getTotalCount()), ThemeConfig.ACCENT_BLUE));
        statsPanel.add(ThemeConfig.createStatCard("Total Users", String.valueOf(UserDAO.getTotalCount()), ThemeConfig.ACCENT_GREEN));
        statsPanel.add(ThemeConfig.createStatCard("Registrations", String.valueOf(RegistrationDAO.getTotalCount()), ThemeConfig.ACCENT_AMBER));
        statsPanel.add(ThemeConfig.createStatCard("Paid / Free", EventDAO.getPaidCount() + " / " + EventDAO.getFreeCount(), ThemeConfig.ACCENT_CYAN));
        statsPanel.add(ThemeConfig.createStatCard("Revenue", "$" + String.format("%.0f", EventDAO.getTotalRevenue()), ThemeConfig.ACCENT_PURPLE));
        statsPanel.revalidate(); statsPanel.repaint();

        // Popular events
        popularPanel.removeAll();
        JLabel popTitle = new JLabel("\uD83D\uDD25 Most Popular Events");
        popTitle.setFont(ThemeConfig.FONT_HEADING); popTitle.setForeground(ThemeConfig.TEXT_PRIMARY);
        popularPanel.add(popTitle); popularPanel.add(Box.createVerticalStrut(12));

        List<Event> popular = EventDAO.getMostPopular(5);
        if (popular.isEmpty()) {
            JLabel empty = new JLabel("No events yet.");
            empty.setFont(ThemeConfig.FONT_BODY); empty.setForeground(ThemeConfig.TEXT_MUTED);
            popularPanel.add(empty);
        } else {
            for (int i = 0; i < popular.size(); i++) {
                Event ev = popular.get(i);
                JPanel row = ThemeConfig.createCard();
                row.setLayout(new BorderLayout(12, 0));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

                JLabel rank = new JLabel("#" + (i + 1));
                rank.setFont(ThemeConfig.FONT_HEADING);
                rank.setForeground(ThemeConfig.ACCENT_AMBER);
                rank.setPreferredSize(new Dimension(40, 40));

                JLabel name = new JLabel(ev.getName() + "  (" + ev.getRegisteredCount() + " registered)");
                name.setFont(ThemeConfig.FONT_BODY); name.setForeground(ThemeConfig.TEXT_PRIMARY);

                JLabel cat = new JLabel(ev.getCategory() + " | " + ev.getDate());
                cat.setFont(ThemeConfig.FONT_SMALL); cat.setForeground(ThemeConfig.TEXT_SECONDARY);

                JPanel info = new JPanel(); info.setOpaque(false);
                info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
                info.add(name); info.add(cat);

                row.add(rank, BorderLayout.WEST);
                row.add(info, BorderLayout.CENTER);
                popularPanel.add(row); popularPanel.add(Box.createVerticalStrut(8));
            }
        }
        popularPanel.revalidate(); popularPanel.repaint();
    }
}
