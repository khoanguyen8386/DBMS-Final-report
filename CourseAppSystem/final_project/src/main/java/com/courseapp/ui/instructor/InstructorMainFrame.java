package com.courseapp.ui.instructor;

import com.courseapp.model.Instructor;
import com.courseapp.ui.LoginFrame;
import com.courseapp.ui.UITheme;
import javax.swing.*;
import java.awt.*;

public class InstructorMainFrame extends JFrame {

    private final Instructor instructor;

    public InstructorMainFrame(Instructor instructor) {
        this.instructor = instructor;
        setTitle("Instructor Portal — " + instructor.getName());
        setSize(1024, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(buildUI());
    }

    private JPanel buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_LIGHT);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(6, 78, 59)); // emerald-900
        topBar.setBorder(UITheme.paddingBorder(10, 20));
        topBar.setPreferredSize(new Dimension(0, 60));

        JLabel appName = new JLabel("Course Registration — Instructor Portal");
        appName.setFont(UITheme.FONT_HEADER);
        appName.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setOpaque(false);
        JLabel userLbl = new JLabel(instructor.getName()
            + (instructor.getTitle() != null ? "  |  " + instructor.getTitle() : ""));
        userLbl.setFont(UITheme.FONT_SMALL);
        userLbl.setForeground(new Color(167, 243, 208));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(UITheme.FONT_SMALL);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(UITheme.DANGER);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });

        userPanel.add(userLbl);
        userPanel.add(logoutBtn);
        topBar.add(appName,   BorderLayout.WEST);
        topBar.add(userPanel, BorderLayout.EAST);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.FONT_NORMAL);
        tabs.addTab("My Courses & Roster", new RosterPanel(instructor));
        tabs.addTab("Grade Entry",         new GradeEntryPanel(instructor));
        tabs.addTab("My Schedule",         new ScheduleManagerPanel(instructor));

        root.add(topBar, BorderLayout.NORTH);
        root.add(tabs,   BorderLayout.CENTER);
        return root;
    }
}
