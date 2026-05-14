package gui;

import model.Event;
import service.EventService;
import service.RegistrationService;
import util.IDGenerator;
import util.SessionManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.print.*;

public class CertificatePanel extends JPanel {
    private MainFrame mainFrame;
    private JPanel contentPanel;
    private JPanel certificateDisplay;

    public CertificatePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(ThemeConfig.BG_PRIMARY);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(24, 24, 24, 24));

        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JScrollPane sp = ThemeConfig.createScrollPane(contentPanel);
        sp.setBorder(null);
        sp.getViewport().setBackground(ThemeConfig.BG_PRIMARY);
        add(sp, BorderLayout.CENTER);
    }

    public void loadCertificate(int eventId) {
        contentPanel.removeAll();
        certificateDisplay = null;

        Event event = EventService.getEventById(eventId);
        if (event == null) {
            JLabel msg = new JLabel("Event not found.");
            msg.setFont(ThemeConfig.FONT_BODY);
            msg.setForeground(ThemeConfig.ACCENT_RED);
            contentPanel.add(msg);
            refreshContent();
            return;
        }

        int userId = SessionManager.getCurrentUserId();
        boolean registered = RegistrationService.isRegistered(userId, eventId);
        if (!registered) {
            JLabel msg = new JLabel("You must be registered for this event to get a certificate.");
            msg.setFont(ThemeConfig.FONT_BODY);
            msg.setForeground(ThemeConfig.ACCENT_RED);
            contentPanel.add(msg);
            refreshContent();
            return;
        }

        JButton backBtn = ThemeConfig.createSecondaryButton("\u2190 Back");
        backBtn.setPreferredSize(new Dimension(90, 36));
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        backBtn.addActionListener(e -> mainFrame.showEventDetail(eventId));
        contentPanel.add(backBtn);
        contentPanel.add(Box.createVerticalStrut(16));

        certificateDisplay = createCertificate(event, IDGenerator.generateCertificateId(), SessionManager.getCurrentUserName());
        contentPanel.add(certificateDisplay);
        contentPanel.add(Box.createVerticalStrut(20));

        JButton printBtn = ThemeConfig.createPrimaryButton("\uD83D\uDDA8 Print / Save as PDF");
        printBtn.setMaximumSize(new Dimension(250, ThemeConfig.BUTTON_HEIGHT));
        printBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        printBtn.addActionListener(e -> printCertificate());
        contentPanel.add(printBtn);

        refreshContent();
    }

    private JPanel createCertificate(Event event, String certId, String userName) {
        JPanel certificate = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                g2.setPaint(new GradientPaint(0, 0, new Color(255, 253, 244), w, h, new Color(236, 247, 252)));
                g2.fillRoundRect(0, 0, w, h, 20, 20);

                g2.setColor(new Color(245, 204, 94, 75));
                g2.fillOval(-74, -74, 190, 190);
                g2.setColor(new Color(51, 128, 153, 54));
                g2.fillOval(w - 132, h - 118, 220, 220);

                g2.setColor(new Color(182, 129, 39));
                g2.setStroke(new BasicStroke(5f));
                g2.drawRoundRect(8, 8, w - 17, h - 17, 18, 18);

                g2.setColor(new Color(30, 83, 104));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(22, 22, w - 45, h - 45, 12, 12);

                g2.setColor(new Color(182, 129, 39));
                g2.fillRect(96, 98, w - 192, 2);
                g2.fillRect(96, h - 104, w - 192, 2);

                g2.dispose();
            }
        };

        certificate.setOpaque(false);
        certificate.setLayout(new BoxLayout(certificate, BoxLayout.Y_AXIS));
        certificate.setPreferredSize(new Dimension(760, 520));
        certificate.setMaximumSize(new Dimension(760, 520));
        certificate.setAlignmentX(Component.LEFT_ALIGNMENT);
        certificate.setBorder(new EmptyBorder(44, 58, 38, 58));

        JLabel title = centeredLabel("CERTIFICATE OF PARTICIPATION", new Font("Serif", Font.BOLD, 30), new Color(30, 83, 104));
        JLabel system = centeredLabel("Event Management System", new Font("SansSerif", Font.BOLD, 13), new Color(182, 129, 39));
        JLabel certify = centeredLabel("This is to certify that", new Font("Serif", Font.ITALIC, 18), new Color(72, 78, 88));
        JLabel name = centeredLabel(userName, new Font("Serif", Font.BOLD, 40), new Color(29, 55, 84));
        JLabel participated = centeredLabel("has successfully participated in", new Font("Serif", Font.ITALIC, 18), new Color(72, 78, 88));
        JLabel eventName = centeredLabel(event.getName(), new Font("Serif", Font.BOLD, 28), new Color(116, 64, 132));
        JLabel eventDetails = centeredLabel("held on " + event.getDate() + " at " + event.getVenue(), new Font("Serif", Font.PLAIN, 16), new Color(72, 78, 88));

        certificate.add(Box.createVerticalStrut(10));
        certificate.add(title);
        certificate.add(Box.createVerticalStrut(6));
        certificate.add(system);
        certificate.add(Box.createVerticalStrut(44));
        certificate.add(certify);
        certificate.add(Box.createVerticalStrut(14));
        certificate.add(name);
        certificate.add(Box.createVerticalStrut(16));
        certificate.add(participated);
        certificate.add(Box.createVerticalStrut(14));
        certificate.add(eventName);
        certificate.add(Box.createVerticalStrut(10));
        certificate.add(eventDetails);
        certificate.add(Box.createVerticalGlue());
        certificate.add(createFooter(certId));

        return certificate;
    }

    private JLabel centeredLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JPanel createFooter(String certId) {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        JLabel certIdLabel = new JLabel("Certificate ID: " + certId);
        certIdLabel.setFont(new Font("Consolas", Font.PLAIN, 12));
        certIdLabel.setForeground(new Color(116, 124, 136));

        JPanel signatureBox = new JPanel();
        signatureBox.setOpaque(false);
        signatureBox.setLayout(new BoxLayout(signatureBox, BoxLayout.Y_AXIS));

        JLabel line = new JLabel("________________________");
        line.setFont(new Font("Serif", Font.PLAIN, 14));
        line.setForeground(new Color(182, 129, 39));
        line.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel signature = new JLabel("Event Management System");
        signature.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 16));
        signature.setForeground(new Color(30, 83, 104));
        signature.setAlignmentX(Component.RIGHT_ALIGNMENT);

        signatureBox.add(line);
        signatureBox.add(Box.createVerticalStrut(2));
        signatureBox.add(signature);

        footer.add(certIdLabel, BorderLayout.WEST);
        footer.add(signatureBox, BorderLayout.EAST);
        return footer;
    }

    private void refreshContent() {
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void printCertificate() {
        if (certificateDisplay == null) return;
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
            Graphics2D g2 = (Graphics2D) graphics;
            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            double scaleX = pageFormat.getImageableWidth() / certificateDisplay.getWidth();
            double scaleY = pageFormat.getImageableHeight() / certificateDisplay.getHeight();
            double scale = Math.min(scaleX, scaleY);
            g2.scale(scale, scale);
            certificateDisplay.printAll(g2);
            return Printable.PAGE_EXISTS;
        });
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException ex) {
                ThemeConfig.showError(this, "Print failed: " + ex.getMessage());
            }
        }
    }
}
