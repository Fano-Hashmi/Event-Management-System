package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Main application frame. Uses CardLayout to switch between screens.
 */
public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPanel;

    public static final String LOGIN = "LOGIN";
    public static final String REGISTER = "REGISTER";
    public static final String ADMIN_DASHBOARD = "ADMIN_DASHBOARD";
    public static final String STAFF_DASHBOARD = "STAFF_DASHBOARD";
    public static final String USER_DASHBOARD = "USER_DASHBOARD";
    public static final String EVENT_LIST = "EVENT_LIST";
    public static final String EVENT_DETAIL = "EVENT_DETAIL";
    public static final String ADD_EVENT = "ADD_EVENT";
    public static final String EDIT_EVENT = "EDIT_EVENT";
    public static final String PAYMENT = "PAYMENT";
    public static final String PASS_VIEW = "PASS_VIEW";
    public static final String USER_MANAGEMENT = "USER_MANAGEMENT";
    public static final String PROFILE = "PROFILE";
    public static final String FEEDBACK = "FEEDBACK";
    public static final String CERTIFICATE = "CERTIFICATE";

    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private AdminDashboard adminDashboard;
    private StaffDashboard staffDashboard;
    private UserDashboard userDashboard;
    private EventListPanel eventListPanel;
    private EventDetailPanel eventDetailPanel;
    private AddEditEventPanel addEventPanel;
    private AddEditEventPanel editEventPanel;
    private PaymentPanel paymentPanel;
    private PassPanel passPanel;
    private UserManagementPanel userManagementPanel;
    private ProfilePanel profilePanel;
    private FeedbackPanel feedbackPanel;
    private CertificatePanel certificatePanel;

    public MainFrame() {
        setTitle("Event Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);
        getContentPane().setBackground(ThemeConfig.BG_PRIMARY);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(ThemeConfig.BG_PRIMARY);

        initPanels();
        add(contentPanel);
        showScreen(LOGIN);
    }

    private void initPanels() {
        loginPanel = new LoginPanel(this);
        registerPanel = new RegisterPanel(this);
        adminDashboard = new AdminDashboard(this);
        staffDashboard = new StaffDashboard(this);
        userDashboard = new UserDashboard(this);
        eventListPanel = new EventListPanel(this);
        eventDetailPanel = new EventDetailPanel(this);
        addEventPanel = new AddEditEventPanel(this, false);
        editEventPanel = new AddEditEventPanel(this, true);
        paymentPanel = new PaymentPanel(this);
        passPanel = new PassPanel(this);
        userManagementPanel = new UserManagementPanel(this);
        profilePanel = new ProfilePanel(this);
        feedbackPanel = new FeedbackPanel(this);
        certificatePanel = new CertificatePanel(this);

        contentPanel.add(loginPanel, LOGIN);
        contentPanel.add(registerPanel, REGISTER);
        contentPanel.add(adminDashboard, ADMIN_DASHBOARD);
        contentPanel.add(staffDashboard, STAFF_DASHBOARD);
        contentPanel.add(userDashboard, USER_DASHBOARD);
        contentPanel.add(eventListPanel, EVENT_LIST);
        contentPanel.add(eventDetailPanel, EVENT_DETAIL);
        contentPanel.add(addEventPanel, ADD_EVENT);
        contentPanel.add(editEventPanel, EDIT_EVENT);
        contentPanel.add(paymentPanel, PAYMENT);
        contentPanel.add(passPanel, PASS_VIEW);
        contentPanel.add(userManagementPanel, USER_MANAGEMENT);
        contentPanel.add(profilePanel, PROFILE);
        contentPanel.add(feedbackPanel, FEEDBACK);
        contentPanel.add(certificatePanel, CERTIFICATE);
    }

    public void showScreen(String name) { cardLayout.show(contentPanel, name); }

    public void showAdminDashboard() { adminDashboard.refresh(); showScreen(ADMIN_DASHBOARD); }
    public void showStaffDashboard() { staffDashboard.refresh(); showScreen(STAFF_DASHBOARD); }
    public void showUserDashboard() { userDashboard.refresh(); showScreen(USER_DASHBOARD); }
    public void showEventList() { eventListPanel.refresh(); showScreen(EVENT_LIST); }
    public void showEventDetail(int eventId) { eventDetailPanel.loadEvent(eventId); showScreen(EVENT_DETAIL); }
    public void showAddEvent() { addEventPanel.resetForm(); showScreen(ADD_EVENT); }
    public void showEditEvent(int eventId) { editEventPanel.loadEvent(eventId); showScreen(EDIT_EVENT); }
    public void showPayment(int eventId) { paymentPanel.loadEvent(eventId); showScreen(PAYMENT); }
    public void showPasses() { passPanel.refresh(); showScreen(PASS_VIEW); }
    public void showUserManagement() { userManagementPanel.refresh(); showScreen(USER_MANAGEMENT); }
    public void showProfile() { profilePanel.refresh(); showScreen(PROFILE); }
    public void showFeedback(int eventId) { feedbackPanel.loadEvent(eventId); showScreen(FEEDBACK); }
    public void showCertificate(int eventId) { certificatePanel.loadCertificate(eventId); showScreen(CERTIFICATE); }
}
