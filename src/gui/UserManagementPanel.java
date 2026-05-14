package gui;

import model.User;
import service.UserService;
import util.SessionManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class UserManagementPanel extends JPanel {
    private MainFrame mainFrame;
    private JTable userTable;
    private DefaultTableModel tableModel;

    public UserManagementPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(ThemeConfig.BG_PRIMARY);
        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(24, 24, 24, 24));
        initUI();
    }

    private void initUI() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        header.setOpaque(false);
        JButton backBtn = ThemeConfig.createSecondaryButton("\u2190 Back");
        backBtn.setPreferredSize(new Dimension(90, 36));
        backBtn.addActionListener(e -> mainFrame.showAdminDashboard());
        JLabel title = new JLabel("User Management");
        title.setFont(ThemeConfig.FONT_TITLE); title.setForeground(ThemeConfig.TEXT_PRIMARY);
        header.add(backBtn); header.add(title);

        String[] columns = {"ID", "Name", "Email", "Role", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        userTable = new JTable(tableModel);
        ThemeConfig.styleTable(userTable);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Custom status cell coloring
        userTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, sel, focus, row, col);
                String status = value != null ? value.toString() : "";
                if (!sel) {
                    c.setForeground("BANNED".equals(status) ? ThemeConfig.ACCENT_RED : ThemeConfig.ACCENT_GREEN);
                    c.setBackground(ThemeConfig.BG_SECONDARY);
                }
                return c;
            }
        });

        // Bottom actions
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        bottomPanel.setOpaque(false);

        JLabel roleLabel = ThemeConfig.createLabel("Change Role:");
        JComboBox<String> roleCombo = ThemeConfig.createComboBox(new String[]{"USER", "STAFF", "ADMIN"});
        roleCombo.setPreferredSize(new Dimension(120, 38));
        JButton changeRoleBtn = ThemeConfig.createPrimaryButton("Update Role");
        changeRoleBtn.setPreferredSize(new Dimension(120, 38));
        changeRoleBtn.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row < 0) { ThemeConfig.showError(this, "Select a user first."); return; }
            int userId = (int) tableModel.getValueAt(row, 0);
            if (userId == SessionManager.getCurrentUserId()) { ThemeConfig.showError(this, "Cannot change your own role."); return; }
            String newRole = (String) roleCombo.getSelectedItem();
            if (ThemeConfig.showConfirm(this, "Change role to " + newRole + "?")) {
                if (UserService.updateUserRole(userId, newRole)) { ThemeConfig.showSuccess(this, "Role updated!"); refresh(); }
                else ThemeConfig.showError(this, "Failed to update role.");
            }
        });

        JButton banBtn = ThemeConfig.createDangerButton("Ban User");
        banBtn.setPreferredSize(new Dimension(100, 38));
        banBtn.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row < 0) { ThemeConfig.showError(this, "Select a user first."); return; }
            int userId = (int) tableModel.getValueAt(row, 0);
            if (userId == SessionManager.getCurrentUserId()) { ThemeConfig.showError(this, "Cannot ban yourself."); return; }
            String status = (String) tableModel.getValueAt(row, 4);
            if ("BANNED".equals(status)) {
                String err = UserService.unbanUser(userId);
                if (err != null) ThemeConfig.showError(this, err);
                else { ThemeConfig.showSuccess(this, "User unbanned!"); refresh(); }
            } else {
                if (ThemeConfig.showConfirm(this, "Ban this user?")) {
                    String err = UserService.banUser(userId);
                    if (err != null) ThemeConfig.showError(this, err);
                    else { ThemeConfig.showSuccess(this, "User banned."); refresh(); }
                }
            }
        });

        JButton deleteBtn = ThemeConfig.createDangerButton("Delete User");
        deleteBtn.setPreferredSize(new Dimension(110, 38));
        deleteBtn.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row < 0) { ThemeConfig.showError(this, "Select a user first."); return; }
            int userId = (int) tableModel.getValueAt(row, 0);
            if (userId == SessionManager.getCurrentUserId()) { ThemeConfig.showError(this, "Cannot delete yourself here."); return; }
            if (ThemeConfig.showConfirm(this, "Delete this user and all their data?")) {
                String err = UserService.deleteUser(userId);
                if (err != null) ThemeConfig.showError(this, err);
                else { ThemeConfig.showSuccess(this, "User deleted."); refresh(); }
            }
        });

        JButton createStaffBtn = ThemeConfig.createSecondaryButton("+ Create Staff");
        createStaffBtn.setPreferredSize(new Dimension(130, 38));
        createStaffBtn.addActionListener(e -> showCreateStaffDialog());

        bottomPanel.add(roleLabel); bottomPanel.add(roleCombo); bottomPanel.add(changeRoleBtn);
        bottomPanel.add(banBtn); bottomPanel.add(deleteBtn); bottomPanel.add(createStaffBtn);

        add(header, BorderLayout.NORTH);
        add(ThemeConfig.createScrollPane(userTable), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void showCreateStaffDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 8, 8));
        JTextField nameF = new JTextField(); JTextField emailF = new JTextField(); JPasswordField passF = new JPasswordField();
        panel.add(new JLabel("Name:")); panel.add(nameF);
        panel.add(new JLabel("Email:")); panel.add(emailF);
        panel.add(new JLabel("Password:")); panel.add(passF);
        int result = JOptionPane.showConfirmDialog(this, panel, "Create Staff Account", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String err = UserService.createStaffAccount(nameF.getText().trim(), emailF.getText().trim(), new String(passF.getPassword()));
            if (err != null) ThemeConfig.showError(this, err);
            else { ThemeConfig.showSuccess(this, "Staff account created!"); refresh(); }
        }
    }

    public void refresh() {
        tableModel.setRowCount(0);
        List<User> users = UserService.getAllUsers();
        for (User u : users) {
            tableModel.addRow(new Object[]{u.getUserId(), u.getName(), u.getEmail(), u.getRole(), u.getStatus()});
        }
    }
}
