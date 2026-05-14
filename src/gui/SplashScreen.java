package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Animated splash screen with progress bar.
 */
public class SplashScreen extends JWindow {
    private JProgressBar progressBar;

    public SplashScreen() {
        setSize(500, 350);
        setLocationRelativeTo(null);

        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(18, 18, 24),
                    0, getHeight(), new Color(34, 36, 50));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Decorative circles
                g2.setColor(new Color(99, 102, 241, 30));
                g2.fillOval(-50, -50, 200, 200);
                g2.setColor(new Color(139, 92, 246, 20));
                g2.fillOval(350, 200, 200, 200);
                g2.dispose();
            }
        };
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createLineBorder(new Color(99, 102, 241), 2));

        content.add(Box.createVerticalStrut(60));

        JLabel icon = new JLabel("\uD83C\uDFAD");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(icon);
        content.add(Box.createVerticalStrut(16));

        JLabel title = new JLabel("Event Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(237, 237, 245));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(8));

        JLabel subtitle = new JLabel("Loading application...");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(163, 163, 180));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(subtitle);
        content.add(Box.createVerticalStrut(40));

        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(300, 6));
        progressBar.setMaximumSize(new Dimension(300, 6));
        progressBar.setBorderPainted(false);
        progressBar.setBackground(new Color(42, 44, 60));
        progressBar.setForeground(new Color(99, 102, 241));
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(progressBar);

        content.add(Box.createVerticalGlue());

        JLabel version = new JLabel("v1.0.0  |  Java Swing + SQLite");
        version.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        version.setForeground(new Color(113, 113, 130));
        version.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(version);
        content.add(Box.createVerticalStrut(16));

        setContentPane(content);
    }

    /**
     * Show splash with animated progress, then run callback.
     */
    public void showSplash(Runnable onComplete) {
        setVisible(true);
        new Thread(() -> {
            try {
                for (int i = 0; i <= 100; i += 2) {
                    final int val = i;
                    SwingUtilities.invokeLater(() -> progressBar.setValue(val));
                    Thread.sleep(30);
                }
                Thread.sleep(200);
            } catch (InterruptedException ignored) {}
            SwingUtilities.invokeLater(() -> {
                setVisible(false);
                dispose();
                onComplete.run();
            });
        }).start();
    }
}
