package gui;

import model.Event;
import model.User;
import service.EventService;
import service.UserService;
import util.SessionManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class AddEditEventPanel extends JPanel {
    private MainFrame mainFrame;
    private boolean isEditMode;
    private int editEventId = -1;
    private JTextField nameField, dateField, timeField, venueField, capacityField, priceField;
    private JTextArea descArea;
    private JComboBox<String> typeCombo, categoryCombo, staffCombo;
    private JLabel titleLabel, errorLabel;
    private int[] staffIds = new int[0];

    public AddEditEventPanel(MainFrame mainFrame, boolean isEditMode) {
        this.mainFrame = mainFrame;
        this.isEditMode = isEditMode;
        setBackground(ThemeConfig.BG_PRIMARY);
        setLayout(new GridBagLayout());
        initUI();
    }

    private void initUI() {
        JPanel card = ThemeConfig.createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(520, 700));

        JButton backBtn = ThemeConfig.createSecondaryButton("\u2190 Back");
        backBtn.setPreferredSize(new Dimension(90, 32));
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        backBtn.addActionListener(e -> mainFrame.showEventList());

        titleLabel = new JLabel(isEditMode ? "Edit Event" : "Create New Event");
        titleLabel.setFont(ThemeConfig.FONT_TITLE); titleLabel.setForeground(ThemeConfig.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        errorLabel = new JLabel(" ");
        errorLabel.setFont(ThemeConfig.FONT_SMALL); errorLabel.setForeground(ThemeConfig.ACCENT_RED);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        Dimension fieldSize = new Dimension(460, ThemeConfig.INPUT_HEIGHT);
        nameField = ThemeConfig.createTextField("Event name");
        dateField = ThemeConfig.createTextField("YYYY-MM-DD");
        timeField = ThemeConfig.createTextField("HH:MM");
        venueField = ThemeConfig.createTextField("Venue location");
        capacityField = ThemeConfig.createTextField("Max participants");
        priceField = ThemeConfig.createTextField("0.00");

        descArea = ThemeConfig.createTextArea("Event description...");
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(460, 70));
        descScroll.setMaximumSize(new Dimension(460, 70));
        descScroll.setBorder(nameField.getBorder());

        typeCombo = ThemeConfig.createComboBox(new String[]{"FREE", "PAID"});
        categoryCombo = ThemeConfig.createComboBox(new String[]{"CONFERENCE", "WORKSHOP", "SEMINAR", "CONCERT", "SPORTS", "OTHER"});
        staffCombo = ThemeConfig.createComboBox(new String[]{"None"});

        for (JComponent f : new JComponent[]{nameField, dateField, timeField, venueField, capacityField, priceField, typeCombo, categoryCombo, staffCombo}) {
            f.setMaximumSize(fieldSize); f.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        typeCombo.addActionListener(e -> {
            boolean isPaid = "PAID".equals(typeCombo.getSelectedItem());
            priceField.setEnabled(isPaid);
            if (!isPaid) priceField.setText("0.00");
        });
        priceField.setEnabled(false);

        JButton submitBtn = ThemeConfig.createPrimaryButton(isEditMode ? "Update Event" : "Create Event");
        submitBtn.setMaximumSize(new Dimension(460, ThemeConfig.BUTTON_HEIGHT));
        submitBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitBtn.addActionListener(e -> handleSubmit());

        card.add(backBtn); card.add(Box.createVerticalStrut(8));
        card.add(titleLabel); card.add(Box.createVerticalStrut(4));
        card.add(errorLabel); card.add(Box.createVerticalStrut(6));
        addField(card, "Event Name", nameField);
        addField(card, "Description", descScroll);
        addField(card, "Date", dateField);
        addField(card, "Time", timeField);
        addField(card, "Venue", venueField);
        addField(card, "Category", categoryCombo);
        addField(card, "Event Type", typeCombo);
        addField(card, "Price ($)", priceField);
        addField(card, "Capacity", capacityField);
        addField(card, "Assign Staff", staffCombo);
        card.add(Box.createVerticalStrut(12)); card.add(submitBtn);

        JScrollPane cardScroll = new JScrollPane(card);
        cardScroll.setBorder(null); cardScroll.setOpaque(false);
        cardScroll.getViewport().setOpaque(false);
        cardScroll.setPreferredSize(new Dimension(560, 720));
        add(cardScroll);
    }

    private void addField(JPanel parent, String label, JComponent field) {
        JLabel lbl = ThemeConfig.createLabel(label);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(lbl); parent.add(Box.createVerticalStrut(3));
        parent.add(field); parent.add(Box.createVerticalStrut(6));
    }

    private void refreshStaffCombo() {
        staffCombo.removeAllItems();
        staffCombo.addItem("None");
        List<User> staffList = UserService.getAllStaff();
        staffIds = new int[staffList.size()];
        for (int i = 0; i < staffList.size(); i++) {
            User s = staffList.get(i);
            staffIds[i] = s.getUserId();
            staffCombo.addItem(s.getName() + " (" + s.getEmail() + ")");
        }
    }

    public void resetForm() {
        editEventId = -1;
        nameField.setText(""); descArea.setText(""); dateField.setText("");
        timeField.setText(""); venueField.setText(""); capacityField.setText("");
        priceField.setText("0.00"); typeCombo.setSelectedIndex(0);
        categoryCombo.setSelectedIndex(0); priceField.setEnabled(false);
        errorLabel.setText(" "); titleLabel.setText("Create New Event");
        refreshStaffCombo();
    }

    public void loadEvent(int eventId) {
        refreshStaffCombo();
        Event event = EventService.getEventById(eventId);
        if (event == null) return;
        editEventId = eventId;
        nameField.setText(event.getName());
        descArea.setText(event.getDescription() != null ? event.getDescription() : "");
        dateField.setText(event.getDate()); timeField.setText(event.getTime());
        venueField.setText(event.getVenue());
        capacityField.setText(String.valueOf(event.getCapacity()));
        priceField.setText(String.format("%.2f", event.getPrice()));
        typeCombo.setSelectedItem(event.getEventType());
        categoryCombo.setSelectedItem(event.getCategory());
        priceField.setEnabled(event.isPaid());
        titleLabel.setText("Edit Event: " + event.getName());
        // Select staff
        if (event.getAssignedStaffId() > 0) {
            for (int i = 0; i < staffIds.length; i++) {
                if (staffIds[i] == event.getAssignedStaffId()) { staffCombo.setSelectedIndex(i + 1); break; }
            }
        }
    }

    private void handleSubmit() {
        int selectedStaff = 0;
        int staffIdx = staffCombo.getSelectedIndex();
        if (staffIdx > 0 && staffIdx <= staffIds.length) selectedStaff = staffIds[staffIdx - 1];

        String error;
        if (isEditMode && editEventId > 0) {
            error = EventService.updateEvent(editEventId, nameField.getText().trim(), descArea.getText().trim(),
                (String) categoryCombo.getSelectedItem(), dateField.getText().trim(), timeField.getText().trim(),
                venueField.getText().trim(), capacityField.getText().trim(), (String) typeCombo.getSelectedItem(),
                priceField.getText().trim(), selectedStaff);
        } else {
            error = EventService.createEvent(nameField.getText().trim(), descArea.getText().trim(),
                (String) categoryCombo.getSelectedItem(), dateField.getText().trim(), timeField.getText().trim(),
                venueField.getText().trim(), capacityField.getText().trim(), (String) typeCombo.getSelectedItem(),
                priceField.getText().trim(), selectedStaff);
        }
        if (error != null) errorLabel.setText(error);
        else { ThemeConfig.showSuccess(this, isEditMode ? "Event updated!" : "Event created!"); mainFrame.showEventList(); }
    }
}
