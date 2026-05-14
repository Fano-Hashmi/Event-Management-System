package gui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Theme configuration for the Event Management System.
 * Modern dark-themed design system.
 */
public class ThemeConfig {
    // Color Palette
    public static final Color BG_PRIMARY = new Color(18, 18, 24);
    public static final Color BG_SECONDARY = new Color(26, 27, 38);
    public static final Color BG_CARD = new Color(34, 36, 50);
    public static final Color BG_INPUT = new Color(42, 44, 60);
    public static final Color BG_HOVER = new Color(50, 52, 70);

    public static final Color ACCENT_BLUE = new Color(99, 102, 241);
    public static final Color ACCENT_PURPLE = new Color(139, 92, 246);
    public static final Color ACCENT_GREEN = new Color(52, 211, 153);
    public static final Color ACCENT_AMBER = new Color(251, 191, 36);
    public static final Color ACCENT_RED = new Color(239, 68, 68);
    public static final Color ACCENT_CYAN = new Color(34, 211, 238);

    public static final Color TEXT_PRIMARY = new Color(237, 237, 245);
    public static final Color TEXT_SECONDARY = new Color(163, 163, 180);
    public static final Color TEXT_MUTED = new Color(113, 113, 130);

    public static final Color BORDER_COLOR = new Color(55, 57, 75);
    public static final Color DIVIDER = new Color(45, 47, 65);

    public static final Color GRADIENT_START = new Color(99, 102, 241);
    public static final Color GRADIENT_END = new Color(139, 92, 246);

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBHEADING = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_MONO = new Font("Consolas", Font.PLAIN, 11);

    public static final int CORNER_RADIUS = 12;
    public static final int INPUT_HEIGHT = 42;
    public static final int BUTTON_HEIGHT = 44;
    public static final Dimension BUTTON_SIZE = new Dimension(160, BUTTON_HEIGHT);

    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, GRADIENT_START, getWidth(), 0, GRADIENT_END);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BUTTON); btn.setForeground(Color.WHITE);
        btn.setPreferredSize(BUTTON_SIZE); btn.setContentAreaFilled(false);
        btn.setOpaque(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON); btn.setForeground(TEXT_PRIMARY);
        btn.setBackground(BG_CARD); btn.setPreferredSize(BUTTON_SIZE);
        btn.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        btn.setFocusPainted(false); btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(true); btn.setOpaque(true);
        return btn;
    }

    public static JButton createDangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON); btn.setForeground(Color.WHITE);
        btn.setBackground(ACCENT_RED); btn.setPreferredSize(BUTTON_SIZE);
        btn.setBorder(new EmptyBorder(8, 20, 8, 20));
        btn.setFocusPainted(false); btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        return btn;
    }

    public static JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(FONT_BODY); field.setForeground(TEXT_PRIMARY);
        field.setBackground(BG_INPUT); field.setCaretColor(TEXT_PRIMARY);
        field.setPreferredSize(new Dimension(300, INPUT_HEIGHT));
        field.setBorder(new CompoundBorder(new LineBorder(BORDER_COLOR, 1, true), new EmptyBorder(5, 12, 5, 12)));
        field.putClientProperty("JTextField.placeholderText", placeholder);
        return field;
    }

    public static JPasswordField createPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.setFont(FONT_BODY); field.setForeground(TEXT_PRIMARY);
        field.setBackground(BG_INPUT); field.setCaretColor(TEXT_PRIMARY);
        field.setPreferredSize(new Dimension(300, INPUT_HEIGHT));
        field.setBorder(new CompoundBorder(new LineBorder(BORDER_COLOR, 1, true), new EmptyBorder(5, 12, 5, 12)));
        field.putClientProperty("JTextField.placeholderText", placeholder);
        return field;
    }

    public static JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(FONT_BODY); combo.setForeground(TEXT_PRIMARY);
        combo.setBackground(BG_INPUT);
        combo.setPreferredSize(new Dimension(300, INPUT_HEIGHT));
        return combo;
    }

    public static JTextArea createTextArea(String placeholder) {
        JTextArea area = new JTextArea();
        area.setFont(FONT_BODY); area.setForeground(TEXT_PRIMARY);
        area.setBackground(BG_INPUT); area.setCaretColor(TEXT_PRIMARY);
        area.setLineWrap(true); area.setWrapStyleWord(true);
        area.setBorder(new EmptyBorder(8, 12, 8, 12));
        return area;
    }

    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL); label.setForeground(TEXT_SECONDARY);
        return label;
    }

    public static JLabel createHeading(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_HEADING); label.setForeground(TEXT_PRIMARY);
        return label;
    }

    public static JPanel createCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        return card;
    }

    public static JPanel createStatCard(String title, String value, Color accentColor) {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 8));
        card.setPreferredSize(new Dimension(200, 110));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_SMALL); titleLabel.setForeground(TEXT_SECONDARY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(FONT_TITLE); valueLabel.setForeground(accentColor);

        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accentColor); g2.fillOval(0, 2, 8, 8); g2.dispose();
            }
        };
        dot.setOpaque(false); dot.setPreferredSize(new Dimension(12, 12));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        titleRow.setOpaque(false); titleRow.add(dot); titleRow.add(titleLabel);

        card.add(titleRow, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY); table.setForeground(TEXT_PRIMARY);
        table.setBackground(BG_SECONDARY); table.setSelectionBackground(ACCENT_BLUE.darker());
        table.setSelectionForeground(Color.WHITE); table.setGridColor(DIVIDER);
        table.setRowHeight(40); table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1)); table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_SUBHEADING); header.setForeground(TEXT_PRIMARY);
        header.setBackground(BG_CARD); header.setBorder(new LineBorder(DIVIDER));
        header.setPreferredSize(new Dimension(header.getWidth(), 44));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBackground(BG_SECONDARY); renderer.setForeground(TEXT_PRIMARY);
        renderer.setBorder(new EmptyBorder(8, 12, 8, 12));
        table.setDefaultRenderer(Object.class, renderer);
    }

    public static JScrollPane createScrollPane(Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBorder(new LineBorder(BORDER_COLOR, 1));
        sp.getViewport().setBackground(BG_SECONDARY);
        sp.setBackground(BG_SECONDARY);
        return sp;
    }

    public static JPanel createGradientPanel() {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, BG_PRIMARY, 0, getHeight(), BG_SECONDARY);
                g2.setPaint(gp); g2.fillRect(0, 0, getWidth(), getHeight()); g2.dispose();
            }
        };
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean showConfirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Confirm",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    /** Create a styled sidebar button with hover effects */
    public static JButton createSidebarButton(String text) {
        JButton btn = new JButton("  " + text);
        btn.setFont(FONT_BODY); btn.setForeground(TEXT_SECONDARY);
        btn.setBackground(BG_SECONDARY);
        btn.setMaximumSize(new Dimension(220, 40));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(BG_HOVER); btn.setForeground(TEXT_PRIMARY);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(BG_SECONDARY); btn.setForeground(TEXT_SECONDARY);
            }
        });
        return btn;
    }
}
