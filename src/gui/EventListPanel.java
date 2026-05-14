package gui;

import model.Event;
import service.EventService;
import util.SessionManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EventListPanel extends JPanel {
    private MainFrame mainFrame;
    private JTable eventTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> typeFilter, categoryFilter;
    private String renderedRole;

    public EventListPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(ThemeConfig.BG_PRIMARY);
        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(24, 24, 24, 24));
        rebuildUIForCurrentRole();
    }

    private void initUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);
        JButton backBtn = ThemeConfig.createSecondaryButton("\u2190 Back");
        backBtn.setPreferredSize(new Dimension(90, 36));
        backBtn.addActionListener(e -> {
            if (SessionManager.isAdmin()) mainFrame.showAdminDashboard();
            else if (SessionManager.isStaff()) mainFrame.showStaffDashboard();
            else mainFrame.showUserDashboard();
        });
        JLabel title = new JLabel("All Events");
        title.setFont(ThemeConfig.FONT_TITLE); title.setForeground(ThemeConfig.TEXT_PRIMARY);
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftHeader.setOpaque(false); leftHeader.add(backBtn); leftHeader.add(title);
        header.add(leftHeader, BorderLayout.WEST);

        if (SessionManager.canManageEvents()) {
            JButton addBtn = ThemeConfig.createPrimaryButton("+ Add Event");
            addBtn.setPreferredSize(new Dimension(130, 36));
            addBtn.addActionListener(e -> mainFrame.showAddEvent());
            header.add(addBtn, BorderLayout.EAST);
        }

        // Search Bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchPanel.setOpaque(false);
        searchField = ThemeConfig.createTextField("Search by name or date (YYYY-MM-DD)...");
        searchField.setPreferredSize(new Dimension(280, 38));
        searchField.addActionListener(e -> performSearch());
        JButton searchBtn = ThemeConfig.createSecondaryButton("Search");
        searchBtn.setPreferredSize(new Dimension(80, 38));
        searchBtn.addActionListener(e -> performSearch());
        typeFilter = ThemeConfig.createComboBox(new String[]{"All Types", "FREE", "PAID"});
        typeFilter.setPreferredSize(new Dimension(120, 38));
        typeFilter.addActionListener(e -> performSearch());
        categoryFilter = ThemeConfig.createComboBox(new String[]{"All Categories", "CONFERENCE", "WORKSHOP", "SEMINAR", "CONCERT", "SPORTS", "OTHER"});
        categoryFilter.setPreferredSize(new Dimension(150, 38));
        categoryFilter.addActionListener(e -> performSearch());
        JButton resetBtn = ThemeConfig.createSecondaryButton("Reset");
        resetBtn.setPreferredSize(new Dimension(70, 38));
        resetBtn.addActionListener(e -> { searchField.setText(""); typeFilter.setSelectedIndex(0); categoryFilter.setSelectedIndex(0); refresh(); });

        searchPanel.add(searchField); searchPanel.add(searchBtn);
        searchPanel.add(typeFilter); searchPanel.add(categoryFilter); searchPanel.add(resetBtn);

        // Table
        String[] columns = {"ID", "Event Name", "Category", "Date", "Time", "Venue", "Type", "Price", "Capacity", "Registered"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        eventTable = new JTable(tableModel);
        ThemeConfig.styleTable(eventTable);
        eventTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        eventTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        eventTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = eventTable.getSelectedRow();
                    if (row >= 0) mainFrame.showEventDetail((int) tableModel.getValueAt(row, 0));
                }
            }
        });

        // Bottom buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        bottomPanel.setOpaque(false);
        JButton viewBtn = ThemeConfig.createPrimaryButton("View Details");
        viewBtn.setPreferredSize(new Dimension(130, 38));
        viewBtn.addActionListener(e -> {
            int row = eventTable.getSelectedRow();
            if (row >= 0) mainFrame.showEventDetail((int) tableModel.getValueAt(row, 0));
            else ThemeConfig.showError(this, "Please select an event first.");
        });
        bottomPanel.add(viewBtn);

        if (SessionManager.isAdmin()) {
            JButton editBtn = ThemeConfig.createSecondaryButton("Edit");
            editBtn.setPreferredSize(new Dimension(90, 38));
            editBtn.addActionListener(e -> { int row = eventTable.getSelectedRow(); if (row >= 0) mainFrame.showEditEvent((int) tableModel.getValueAt(row, 0)); });
            JButton deleteBtn = ThemeConfig.createDangerButton("Delete");
            deleteBtn.setPreferredSize(new Dimension(90, 38));
            deleteBtn.addActionListener(e -> {
                int row = eventTable.getSelectedRow();
                if (row >= 0 && ThemeConfig.showConfirm(this, "Delete this event?")) {
                    String err = EventService.deleteEvent((int) tableModel.getValueAt(row, 0));
                    if (err != null) ThemeConfig.showError(this, err); else refresh();
                }
            });
            bottomPanel.add(editBtn); bottomPanel.add(deleteBtn);
        }

        JPanel topPanel = new JPanel(); topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(header); topPanel.add(Box.createVerticalStrut(12)); topPanel.add(searchPanel);

        add(topPanel, BorderLayout.NORTH);
        add(ThemeConfig.createScrollPane(eventTable), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        String type = (String) typeFilter.getSelectedItem();
        String category = (String) categoryFilter.getSelectedItem();

        List<Event> results;
        if (!query.isEmpty() && query.matches("\\d{4}-\\d{2}-\\d{2}")) {
            results = EventService.searchByDate(query);
        } else if (!query.isEmpty()) {
            results = EventService.searchByName(query);
        } else {
            results = EventService.getAllEvents();
        }
        if (type != null && !type.equals("All Types"))
            results = results.stream().filter(e -> e.getEventType().equalsIgnoreCase(type)).collect(java.util.stream.Collectors.toList());
        if (category != null && !category.equals("All Categories"))
            results = results.stream().filter(e -> e.getCategory().equalsIgnoreCase(category)).collect(java.util.stream.Collectors.toList());
        loadEvents(results);
    }

    public void refresh() {
        if (!SessionManager.getCurrentUserRole().equals(renderedRole)) {
            rebuildUIForCurrentRole();
        }
        EventService.initialize();
        loadEvents(EventService.getAllEvents());
    }

    private void rebuildUIForCurrentRole() {
        removeAll();
        initUI();
        renderedRole = SessionManager.getCurrentUserRole();
        revalidate();
        repaint();
    }

    private void loadEvents(List<Event> events) {
        tableModel.setRowCount(0);
        for (Event e : events) {
            tableModel.addRow(new Object[]{e.getEventId(), e.getName(), e.getCategory(), e.getDate(), e.getTime(),
                e.getVenue(), e.getEventType(), e.isPaid() ? "$" + String.format("%.2f", e.getPrice()) : "Free",
                e.getCapacity(), e.getRegisteredCount()});
        }
    }
}
