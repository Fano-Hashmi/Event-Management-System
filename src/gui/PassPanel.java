package gui;

import model.Pass;
import service.PaymentService;
import util.QRGenerator;
import util.SessionManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class PassPanel extends JPanel {
    private MainFrame mainFrame;
    private JPanel passContainer;

    public PassPanel(MainFrame mainFrame) {
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
        backBtn.addActionListener(e -> {
            if (SessionManager.isAdmin()) mainFrame.showAdminDashboard();
            else mainFrame.showUserDashboard();
        });
        JLabel title = new JLabel("\uD83C\uDFAB My Event Passes");
        title.setFont(ThemeConfig.FONT_TITLE); title.setForeground(ThemeConfig.TEXT_PRIMARY);
        header.add(backBtn); header.add(title);

        passContainer = new JPanel();
        passContainer.setOpaque(false);
        passContainer.setLayout(new BoxLayout(passContainer, BoxLayout.Y_AXIS));
        JScrollPane sp = ThemeConfig.createScrollPane(passContainer);
        sp.setBorder(null); sp.getViewport().setBackground(ThemeConfig.BG_PRIMARY);

        add(header, BorderLayout.NORTH);
        add(sp, BorderLayout.CENTER);
    }

    public void refresh() {
        passContainer.removeAll();
        int userId = SessionManager.getCurrentUserId();
        List<Pass> passes = PaymentService.getUserPasses(userId);

        if (passes.isEmpty()) {
            passContainer.add(Box.createVerticalStrut(40));
            JLabel empty = new JLabel("No passes yet. Register for paid events to get passes.");
            empty.setFont(ThemeConfig.FONT_BODY); empty.setForeground(ThemeConfig.TEXT_MUTED);
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            passContainer.add(empty);
        } else {
            passContainer.add(Box.createVerticalStrut(16));
            for (Pass pass : passes) {
                passContainer.add(createPassCard(pass));
                passContainer.add(Box.createVerticalStrut(16));
            }
        }
        passContainer.revalidate(); passContainer.repaint();
    }

    private JPanel createPassCard(Pass pass) {
        JPanel ticket = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(40, 42, 60), getWidth(), 0, new Color(50, 35, 70));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(pass.isValidStatus() ? ThemeConfig.ACCENT_GREEN : ThemeConfig.ACCENT_RED);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
                // Dotted separator
                g2.setColor(ThemeConfig.DIVIDER);
                float[] dash = {6, 4};
                g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dash, 0));
                int sepX = getWidth() - 200;
                g2.drawLine(sepX, 15, sepX, getHeight() - 15);
                g2.dispose();
            }
        };
        ticket.setOpaque(false);
        ticket.setLayout(new BorderLayout(20, 0));
        ticket.setBorder(new EmptyBorder(20, 24, 20, 24));
        ticket.setPreferredSize(new Dimension(750, 180));
        ticket.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        ticket.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Left: info
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel h = new JLabel("EVENT PASS"); h.setFont(ThemeConfig.FONT_SMALL); h.setForeground(ThemeConfig.TEXT_MUTED);
        JLabel en = new JLabel(pass.getEventName()); en.setFont(ThemeConfig.FONT_HEADING); en.setForeground(ThemeConfig.TEXT_PRIMARY);
        JLabel det = new JLabel("Attendee: " + pass.getUserName()); det.setFont(ThemeConfig.FONT_BODY); det.setForeground(ThemeConfig.TEXT_SECONDARY);
        JLabel ids = new JLabel("Pass #" + pass.getPassId() + " | Event #" + pass.getEventId());
        ids.setFont(ThemeConfig.FONT_SMALL); ids.setForeground(ThemeConfig.TEXT_MUTED);
        JLabel dt = new JLabel("Issued: " + pass.getIssueDate()); dt.setFont(ThemeConfig.FONT_SMALL); dt.setForeground(ThemeConfig.TEXT_MUTED);
        info.add(h); info.add(Box.createVerticalStrut(4)); info.add(en);
        info.add(Box.createVerticalStrut(6)); info.add(det);
        info.add(Box.createVerticalStrut(4)); info.add(ids);
        info.add(Box.createVerticalStrut(2)); info.add(dt);

        // Right: QR code & status
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(170, 0));

        JLabel qrLabel = new JLabel("QR CODE");
        qrLabel.setFont(ThemeConfig.FONT_SMALL); qrLabel.setForeground(ThemeConfig.TEXT_MUTED);
        qrLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel qrCode = new JLabel(pass.getQrCode());
        qrCode.setFont(ThemeConfig.FONT_MONO); qrCode.setForeground(ThemeConfig.ACCENT_CYAN);
        qrCode.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel statusLabel = new JLabel(pass.isValidStatus() ? "\u2705 VALID" : "\u274C USED");
        statusLabel.setFont(ThemeConfig.FONT_SUBHEADING);
        statusLabel.setForeground(pass.isValidStatus() ? ThemeConfig.ACCENT_GREEN : ThemeConfig.ACCENT_RED);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(qrLabel); rightPanel.add(Box.createVerticalStrut(4));
        rightPanel.add(qrCode); rightPanel.add(Box.createVerticalStrut(8));
        rightPanel.add(statusLabel);
        rightPanel.add(Box.createVerticalGlue());

        ticket.add(info, BorderLayout.CENTER);
        ticket.add(rightPanel, BorderLayout.EAST);
        return ticket;
    }
}
