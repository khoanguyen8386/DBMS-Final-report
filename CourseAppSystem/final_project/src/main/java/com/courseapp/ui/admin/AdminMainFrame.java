package com.courseapp.ui.admin;

import com.courseapp.model.Admin;
import com.courseapp.ui.LoginFrame;
import com.courseapp.ui.UITheme;
import javax.swing.*;
import java.awt.*;

public class AdminMainFrame extends JFrame {

    private final Admin admin;

    public AdminMainFrame(Admin admin) {
        this.admin = admin;
        setTitle("Admin Portal — " + admin.getName());
        setSize(1100, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(buildUI());
    }

    private JPanel buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_LIGHT);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(15, 23, 42));
        topBar.setBorder(UITheme.paddingBorder(10, 20));
        topBar.setPreferredSize(new Dimension(0, 60));

        JLabel appName = new JLabel("Course Registration — Admin Portal");
        appName.setFont(UITheme.FONT_HEADER);
        appName.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setOpaque(false);
        JLabel userLbl = new JLabel("Admin: " + admin.getName());
        userLbl.setFont(UITheme.FONT_SMALL);
        userLbl.setForeground(new Color(148, 163, 184));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(UITheme.FONT_SMALL);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(UITheme.DANGER);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        userPanel.add(userLbl);
        userPanel.add(logoutBtn);
        topBar.add(appName,   BorderLayout.WEST);
        topBar.add(userPanel, BorderLayout.EAST);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.FONT_NORMAL);
        tabs.addTab("Dashboard",      new ReportsPanel());
        tabs.addTab("Courses",        new ManageCoursesPanel());
        tabs.addTab("Students",       new ManageStudentsPanel());
        tabs.addTab("Departments",    new ManageDepartmentsPanel());

        root.add(topBar, BorderLayout.NORTH);
        root.add(tabs,   BorderLayout.CENTER);
        return root;
    }
}
