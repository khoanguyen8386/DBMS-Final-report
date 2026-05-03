package com.courseapp.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class UITheme {

    // ── Palette ───────────────────────────────────────────────────────────────
    public static final Color PRIMARY       = new Color(37,  99,  235);
    public static final Color PRIMARY_DARK  = new Color(29,  78,  216);
    public static final Color SUCCESS       = new Color(22,  163, 74);
    public static final Color DANGER        = new Color(220, 38,  38);
    public static final Color WARNING       = new Color(217, 119, 6);
    public static final Color BG_LIGHT      = new Color(248, 250, 252);
    public static final Color BG_WHITE      = Color.WHITE;
    public static final Color BORDER_COLOR  = new Color(226, 232, 240);
    public static final Color TEXT_PRIMARY  = new Color(15,  23,  42);
    public static final Color TEXT_SECONDARY= new Color(100, 116, 139);
    public static final Color ROW_ALT       = new Color(241, 245, 249);
    public static final Color HEADER_BG     = new Color(37,  99,  235);
    public static final Color HEADER_FG     = Color.WHITE;

    // ── Fonts ─────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  20);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD,  14);
    public static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 12);

    // ── Borders ───────────────────────────────────────────────────────────────
    public static Border paddingBorder(int v, int h) {
        return BorderFactory.createEmptyBorder(v, h, v, h);
    }

    public static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        );
    }

    // ── Button factory ────────────────────────────────────────────────────────
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        styleBtn(btn, PRIMARY);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(PRIMARY_DARK); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(PRIMARY); }
        });
        return btn;
    }

    public static JButton dangerButton(String text) {
        JButton btn = new JButton(text);
        styleBtn(btn, DANGER);
        return btn;
    }

    public static JButton successButton(String text) {
        JButton btn = new JButton(text);
        styleBtn(btn, SUCCESS);
        return btn;
    }

    public static JButton grayButton(String text) {
        JButton btn = new JButton(text);
        styleBtn(btn, new Color(100, 116, 139));
        return btn;
    }

    private static void styleBtn(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_NORMAL);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 36));
    }

    // ── TextField factory ─────────────────────────────────────────────────────
    public static JTextField styledTextField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setFont(FONT_NORMAL);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return tf;
    }

    public static JPasswordField styledPasswordField(int cols) {
        JPasswordField pf = new JPasswordField(cols);
        pf.setFont(FONT_NORMAL);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return pf;
    }

    @SuppressWarnings("unchecked")
    public static JComboBox<Object> styledComboBox() {
        JComboBox<Object> cb = new JComboBox<>();
        cb.setFont(FONT_NORMAL);
        cb.setBackground(BG_WHITE);
        return cb;
    }

    // ── Label factory ─────────────────────────────────────────────────────────
    public static JLabel titleLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_TITLE);
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    public static JLabel headerLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_HEADER);
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    public static JLabel secondaryLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(TEXT_SECONDARY);
        return lbl;
    }

    // ── Table styling — fixes invisible headers ───────────────────────────────
    public static void styleTable(JTable table) {
        table.setFont(FONT_NORMAL);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setBackground(BG_WHITE);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Alternating row renderer
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? BG_WHITE : ROW_ALT);
                    setForeground(TEXT_PRIMARY);
                }
                return this;
            }
        });

        // Header — force colors explicitly so system L&F cannot override
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_HEADER);
        header.setPreferredSize(new Dimension(0, 38));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel lbl = new JLabel(value != null ? value.toString() : "");
                lbl.setFont(FONT_HEADER);
                lbl.setForeground(HEADER_FG);
                lbl.setBackground(HEADER_BG);
                lbl.setOpaque(true);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(255,255,255,40)),
                    BorderFactory.createEmptyBorder(0, 10, 0, 10)
                ));
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                return lbl;
            }
        });
    }

    // ── Stat card ─────────────────────────────────────────────────────────────
    public static JPanel statCard(String label, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout(4, 6));
        card.setBackground(BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
            )
        ));

        JLabel valLabel = new JLabel(value);
        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        valLabel.setForeground(accent);

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(FONT_SMALL);
        lblLabel.setForeground(TEXT_SECONDARY);

        card.add(valLabel, BorderLayout.CENTER);
        card.add(lblLabel, BorderLayout.SOUTH);
        return card;
    }

    // ── Section panel ─────────────────────────────────────────────────────────
    public static JPanel sectionPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        hdr.setBackground(HEADER_BG);
        hdr.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        JLabel lbl = new JLabel(title);
        lbl.setFont(FONT_HEADER);
        lbl.setForeground(HEADER_FG);
        hdr.add(lbl);

        panel.add(hdr, BorderLayout.NORTH);
        return panel;
    }

    // ── Global defaults ───────────────────────────────────────────────────────
    public static void applyGlobalDefaults() {
        UIManager.put("Panel.background",       BG_LIGHT);
        UIManager.put("Label.font",             FONT_NORMAL);
        UIManager.put("Button.font",            FONT_NORMAL);
        UIManager.put("TextField.font",         FONT_NORMAL);
        UIManager.put("ComboBox.font",          FONT_NORMAL);
        UIManager.put("Table.font",             FONT_NORMAL);
        UIManager.put("TableHeader.font",       FONT_HEADER);
        UIManager.put("TabbedPane.font",        FONT_NORMAL);
        UIManager.put("OptionPane.messageFont", FONT_NORMAL);
        UIManager.put("OptionPane.buttonFont",  FONT_NORMAL);
        // Force table headers globally
        UIManager.put("TableHeader.background", HEADER_BG);
        UIManager.put("TableHeader.foreground", HEADER_FG);
    }
}
