package com.courseapp.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.courseapp.service.AuthService;
import com.courseapp.service.AuthService.LoginResult;
import com.courseapp.ui.admin.AdminMainFrame;
import com.courseapp.ui.instructor.InstructorMainFrame;
import com.courseapp.ui.student.StudentMainFrame;

public class LoginFrame extends JFrame {

    private JTextField     emailField;
    private JPasswordField passwordField;
    private JLabel         statusLabel;
    private JButton        toggleBtn;
    private boolean        passVisible = false;
    private final AuthService authService = new AuthService();

    public LoginFrame() {
        UITheme.applyGlobalDefaults();
        setTitle("Course Registration System");
        setSize(440, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setContentPane(buildUI());
    }

    private JPanel buildUI() {
        // Root — light grey background, centers the card
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(UITheme.BG_LIGHT);

        // Card — white box, fixed size, GridBagLayout for clean column alignment
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UITheme.BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(36, 40, 36, 40)
        ));
        card.setPreferredSize(new Dimension(380, 450));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill  = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.anchor  = GridBagConstraints.WEST;   // everything anchors LEFT

        // ── App title (centred text inside left-anchored label) ───────────────
        JLabel appTitle = new JLabel("Course Registration", SwingConstants.CENTER);
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        appTitle.setForeground(UITheme.PRIMARY);
        c.gridy = 0; c.insets = new Insets(0, 0, 4, 0);
        card.add(appTitle, c);

        // ── Subtitle ──────────────────────────────────────────────────────────
        JLabel subtitle = new JLabel("Sign in to your account", SwingConstants.CENTER);
        subtitle.setFont(UITheme.FONT_SMALL);
        subtitle.setForeground(UITheme.TEXT_SECONDARY);
        c.gridy = 1; c.insets = new Insets(0, 0, 16, 0);
        card.add(subtitle, c);

        // ── Separator ─────────────────────────────────────────────────────────
        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.BORDER_COLOR);
        c.gridy = 2; c.insets = new Insets(0, 0, 18, 0);
        card.add(sep, c);

        // ── Email label ───────────────────────────────────────────────────────
        JLabel emailLbl = new JLabel("Email address");
        emailLbl.setFont(UITheme.FONT_SMALL);
        emailLbl.setForeground(UITheme.TEXT_SECONDARY);
        c.gridy = 3; c.insets = new Insets(0, 0, 5, 0);
        card.add(emailLbl, c);

        // ── Email field ───────────────────────────────────────────────────────
        emailField = UITheme.styledTextField(20);
        emailField.addActionListener(e -> passwordField.requestFocus());
        c.gridy = 4; c.insets = new Insets(0, 0, 14, 0);
        card.add(emailField, c);

        // ── Password label ────────────────────────────────────────────────────
        JLabel passLbl = new JLabel("Password");
        passLbl.setFont(UITheme.FONT_SMALL);
        passLbl.setForeground(UITheme.TEXT_SECONDARY);
        c.gridy = 5; c.insets = new Insets(0, 0, 5, 0);
        card.add(passLbl, c);

        // ── Password field + show/hide ────────────────────────────────────────
        passwordField = UITheme.styledPasswordField(20);
        passwordField.addActionListener(e -> handleLogin());

        toggleBtn = new JButton("Show");
        toggleBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        toggleBtn.setForeground(UITheme.PRIMARY);
        toggleBtn.setBackground(UITheme.BG_WHITE);
        toggleBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(0, 8, 0, 8)
        ));
        toggleBtn.setFocusable(false);
        toggleBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggleBtn.setPreferredSize(new Dimension(52, 38));
        toggleBtn.addActionListener(e -> {
            passVisible = !passVisible;
            passwordField.setEchoChar(passVisible ? (char) 0 : '\u2022');
            toggleBtn.setText(passVisible ? "Hide" : "Show");
        });

        JPanel passRow = new JPanel(new BorderLayout());
        passRow.setOpaque(false);
        passRow.add(passwordField, BorderLayout.CENTER);
        passRow.add(toggleBtn,     BorderLayout.EAST);

        c.gridy = 6; c.insets = new Insets(0, 0, 6, 0);
        card.add(passRow, c);

        // ── Status label ──────────────────────────────────────────────────────
        statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.DANGER);
        c.gridy = 7; c.insets = new Insets(0, 0, 10, 0);
        card.add(statusLabel, c);

        // ── Sign In button ────────────────────────────────────────────────────
        JButton loginBtn = UITheme.primaryButton("Sign In");
        loginBtn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        loginBtn.addActionListener(e -> handleLogin());
        c.gridy = 8; c.insets = new Insets(0, 0, 14, 0);
        card.add(loginBtn, c);

        // ── Register link ─────────────────────────────────────────────────────
        JButton registerLink = new JButton("New student? Create an account");
        registerLink.setFont(UITheme.FONT_SMALL);
        registerLink.setForeground(UITheme.PRIMARY);
        registerLink.setBorderPainted(false);
        registerLink.setContentAreaFilled(false);
        registerLink.setHorizontalAlignment(SwingConstants.CENTER);
        registerLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLink.addActionListener(e -> new RegisterFrame(this).setVisible(true));
        c.anchor = GridBagConstraints.CENTER;
        c.gridy = 9; c.insets = new Insets(0, 0, 4, 0);
        card.add(registerLink, c);

        // ── Hint ──────────────────────────────────────────────────────────────
        JLabel hint = new JLabel("Admin / Instructor / Student login", SwingConstants.CENTER);
        hint.setFont(UITheme.FONT_SMALL);
        hint.setForeground(UITheme.TEXT_SECONDARY);
        c.gridy = 10; c.insets = new Insets(0, 0, 0, 0);
        card.add(hint, c);

        root.add(card);
        return root;
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String pass  = new String(passwordField.getPassword());
        if (email.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("Please enter your email and password.");
            return;
        }
        LoginResult result = authService.login(email, pass);
        if (result == null) {
            statusLabel.setText("Invalid email or password.");
            passwordField.setText("");
            return;
        }
        dispose();
        switch (result.getRole()) {
            case STUDENT    -> new StudentMainFrame(result.asStudent()).setVisible(true);
            case INSTRUCTOR -> new InstructorMainFrame(result.asInstructor()).setVisible(true);
            case ADMIN      -> new AdminMainFrame(result.asAdmin()).setVisible(true);
        }
    }
}