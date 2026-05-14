package gui;

import service.AuthService;
import service.EventService;
import service.RegistrationService;
import model.Event;
import util.SessionManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class UserDashboard extends JPanel {
    private MainFrame mainFrame;
    private JPanel eventsContainer;
    private JLabel welcomeLabel;

    public UserDashboard(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(ThemeConfig.BG_PRIMARY);
        setLayout(new BorderLayout());
        initUI();
    }

    private void initUI() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(ThemeConfig.BG_SECONDARY);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(20, 16, 20, 16));

        JLabel brand = new JLabel("  \uD83C\uDFAB Event Hub");
        brand.setFont(ThemeConfig.FONT_HEADING); brand.setForeground(ThemeConfig.TEXT_PRIMARY);
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        brandPanel.setOpaque(false); brandPanel.setMaximumSize(new Dimension(220, 40));
        brandPanel.add(brand);
        sidebar.add(brandPanel); sidebar.add(Box.createVerticalStrut(30));

        String[][] menuItems = {
            {"Dashboard", "HOME"}, {"Browse Events", "EVENTS"},
            {"My Registrations", "MY_REG"}, {"My Passes", "PASSES"},
            {"Profile", "PROFILE"}, {"Logout", "LOGOUT"}
        };
        for (String[] item : menuItems) {
            JButton btn = ThemeConfig.createSidebarButton(item[0]);
            String action = item[1];
            btn.addActionListener(e -> handleAction(action));
            sidebar.add(btn); sidebar.add(Box.createVerticalStrut(4));
        }
        sidebar.add(Box.createVerticalGlue());

        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setBackground(ThemeConfig.BG_PRIMARY);
        content.setBorder(new EmptyBorder(30, 30, 30, 30));

        welcomeLabel = new JLabel("Welcome!");
        welcomeLabel.setFont(ThemeConfig.FONT_TITLE); welcomeLabel.setForeground(ThemeConfig.TEXT_PRIMARY);
        JLabel sub = new JLabel("Discover and register for upcoming events");
        sub.setFont(ThemeConfig.FONT_BODY); sub.setForeground(ThemeConfig.TEXT_SECONDARY);
        JPanel header = new JPanel();
        header.setOpaque(false); header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(welcomeLabel); header.add(Box.createVerticalStrut(4)); header.add(sub);

        eventsContainer = new JPanel();
        eventsContainer.setOpaque(false);
        eventsContainer.setLayout(new BoxLayout(eventsContainer, BoxLayout.Y_AXIS));
        JScrollPane sp = ThemeConfig.createScrollPane(eventsContainer);
        sp.setBorder(null); sp.getViewport().setBackground(ThemeConfig.BG_PRIMARY);

        content.add(header, BorderLayout.NORTH);
        content.add(sp, BorderLayout.CENTER);
        add(sidebar, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
    }

    private void handleAction(String action) {
        switch (action) {
            case "HOME" -> refresh();
            case "EVENTS" -> mainFrame.showEventList();
            case "MY_REG" -> showMyRegistrations();
            case "PASSES" -> mainFrame.showPasses();
            case "PROFILE" -> mainFrame.showProfile();
            case "LOGOUT" -> { AuthService.logout(); mainFrame.showScreen(MainFrame.LOGIN); }
        }
    }

    private void showMyRegistrations() {
        eventsContainer.removeAll();
        List<Event> events = RegistrationService.getRegisteredEvents(SessionManager.getCurrentUserId());
        JLabel title = new JLabel("My Registrations (" + events.size() + ")");
        title.setFont(ThemeConfig.FONT_HEADING); title.setForeground(ThemeConfig.TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, 12, 0));
        eventsContainer.add(title);
        if (events.isEmpty()) {
            JLabel empty = new JLabel("You haven't registered for any events yet.");
            empty.setFont(ThemeConfig.FONT_BODY); empty.setForeground(ThemeConfig.TEXT_MUTED);
            eventsContainer.add(empty);
        } else {
            for (Event e : events) { eventsContainer.add(createEventCard(e)); eventsContainer.add(Box.createVerticalStrut(10)); }
        }
        eventsContainer.revalidate(); eventsContainer.repaint();
    }

    public void refresh() {
        if (SessionManager.isLoggedIn()) welcomeLabel.setText("Welcome, " + SessionManager.getCurrentUserName() + "!");
        eventsContainer.removeAll();
        List<Event> events = EventService.getAllEvents();
        JLabel title = new JLabel("Upcoming Events (" + events.size() + ")");
        title.setFont(ThemeConfig.FONT_HEADING); title.setForeground(ThemeConfig.TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, 12, 0));
        eventsContainer.add(title);
        if (events.isEmpty()) {
            JLabel empty = new JLabel("No events available at the moment.");
            empty.setFont(ThemeConfig.FONT_BODY); empty.setForeground(ThemeConfig.TEXT_MUTED);
            eventsContainer.add(empty);
        } else {
            for (Event e : events) { eventsContainer.add(createEventCard(e)); eventsContainer.add(Box.createVerticalStrut(10)); }
        }
        eventsContainer.revalidate(); eventsContainer.repaint();
    }

    private JPanel createEventCard(Event event) {
        JPanel card = ThemeConfig.createCard();
        card.setLayout(new BorderLayout(12, 0));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel info = new JPanel(); info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel name = new JLabel(event.getName());
        name.setFont(ThemeConfig.FONT_SUBHEADING); name.setForeground(ThemeConfig.TEXT_PRIMARY);
        JLabel details = new JLabel(event.getDate() + " at " + event.getTime() + " | " + event.getVenue());
        details.setFont(ThemeConfig.FONT_SMALL); details.setForeground(ThemeConfig.TEXT_SECONDARY);
        JLabel type = new JLabel(event.isPaid() ? "Paid - $" + String.format("%.2f", event.getPrice()) : "Free");
        type.setFont(ThemeConfig.FONT_SMALL);
        type.setForeground(event.isPaid() ? ThemeConfig.ACCENT_AMBER : ThemeConfig.ACCENT_GREEN);
        info.add(name); info.add(Box.createVerticalStrut(4)); info.add(details);
        info.add(Box.createVerticalStrut(4)); info.add(type);

        JButton viewBtn = ThemeConfig.createPrimaryButton("View Details");
        viewBtn.setPreferredSize(new Dimension(130, 36));
        viewBtn.addActionListener(e -> mainFrame.showEventDetail(event.getEventId()));
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 20));
        right.setOpaque(false); right.add(viewBtn);

        card.add(info, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);
        return card;
    }
}
