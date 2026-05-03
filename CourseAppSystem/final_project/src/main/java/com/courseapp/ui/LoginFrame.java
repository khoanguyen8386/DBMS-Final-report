package com.courseapp.ui;

import com.courseapp.service.AuthService;
import com.courseapp.service.AuthService.LoginResult;
import com.courseapp.ui.admin.AdminMainFrame;
import com.courseapp.ui.instructor.InstructorMainFrame;
import com.courseapp.ui.student.StudentMainFrame;
import javax.swing.*;
import java.awt.*;

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
        setSize(420, 480);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setContentPane(buildUI());
    }

    private JPanel buildUI() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(UITheme.BG_LIGHT);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UITheme.BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(36, 48, 36, 48)
        ));
        card.setPreferredSize(new Dimension(360, 420));

        // App title instead of emoji
        JLabel appTitle = new JLabel("Course Registration", SwingConstants.CENTER);
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        appTitle.setForeground(UITheme.PRIMARY);
        appTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = UITheme.secondaryLabel("Sign in to your account");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(UITheme.BORDER_COLOR);

        JLabel emailLbl = new JLabel("Email address");
        emailLbl.setFont(UITheme.FONT_SMALL);
        emailLbl.setForeground(UITheme.TEXT_SECONDARY);
        emailField = UITheme.styledTextField(20);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        emailField.addActionListener(e -> passwordField.requestFocus());

        JLabel passLbl = new JLabel("Password");
        passLbl.setFont(UITheme.FONT_SMALL);
        passLbl.setForeground(UITheme.TEXT_SECONDARY);

        // Password row with show/hide
        JPanel passRow = new JPanel(new BorderLayout(0, 0));
        passRow.setOpaque(false);
        passRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
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
        passRow.add(passwordField, BorderLayout.CENTER);
        passRow.add(toggleBtn,     BorderLayout.EAST);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.DANGER);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton loginBtn = UITheme.primaryButton("Sign In");
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.addActionListener(e -> handleLogin());

        JButton registerLink = new JButton("New student? Create an account");
        registerLink.setFont(UITheme.FONT_SMALL);
        registerLink.setForeground(UITheme.PRIMARY);
        registerLink.setBorderPainted(false);
        registerLink.setContentAreaFilled(false);
        registerLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerLink.addActionListener(e -> new RegisterFrame(this).setVisible(true));

        JLabel hint = UITheme.secondaryLabel("Admin / Instructor / Student login");
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(appTitle);
        card.add(Box.createVerticalStrut(6));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(18));
        card.add(sep);
        card.add(Box.createVerticalStrut(18));
        card.add(emailLbl);
        card.add(Box.createVerticalStrut(5));
        card.add(emailField);
        card.add(Box.createVerticalStrut(14));
        card.add(passLbl);
        card.add(Box.createVerticalStrut(5));
        card.add(passRow);
        card.add(Box.createVerticalStrut(6));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(14));
        card.add(registerLink);
        card.add(Box.createVerticalStrut(6));
        card.add(hint);

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
