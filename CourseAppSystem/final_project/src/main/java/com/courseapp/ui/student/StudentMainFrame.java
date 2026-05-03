package com.courseapp.ui.student;

import com.courseapp.model.Student;
import com.courseapp.ui.LoginFrame;
import com.courseapp.ui.UITheme;
import javax.swing.*;
import java.awt.*;

public class StudentMainFrame extends JFrame {

    private final Student student;

    public StudentMainFrame(Student student) {
        this.student = student;
        setTitle("Course Registration — " + student.getName() + " (" + student.getId() + ")");
        setSize(1024, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(buildUI());
    }

    private JPanel buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_LIGHT);

        // ── Top bar ───────────────────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.PRIMARY);
        topBar.setBorder(UITheme.paddingBorder(10, 20));
        topBar.setPreferredSize(new Dimension(0, 60));

        JLabel appName = new JLabel("Course Registration System");
        appName.setFont(UITheme.FONT_HEADER);
        appName.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setOpaque(false);

        JLabel userInfo = new JLabel(student.getName() + "  |  " + student.getId());
        userInfo.setFont(UITheme.FONT_SMALL);
        userInfo.setForeground(new Color(186, 230, 253));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(UITheme.FONT_SMALL);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(new Color(239, 68, 68));
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        userPanel.add(userInfo);
        userPanel.add(logoutBtn);
        topBar.add(appName,   BorderLayout.WEST);
        topBar.add(userPanel, BorderLayout.EAST);

        // ── Tabs ──────────────────────────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.FONT_NORMAL);
        tabs.setBackground(UITheme.BG_WHITE);

        tabs.addTab("Dashboard",        new DashboardPanel(student));
        tabs.addTab("Browse Courses",   new CourseListPanel(student));
        tabs.addTab("My Courses",       new MyCoursesPanel(student));
        tabs.addTab("Schedule",         new SchedulePanel(student));
        tabs.addTab("Grades",           new GradesPanel(student));

        root.add(topBar, BorderLayout.NORTH);
        root.add(tabs,   BorderLayout.CENTER);
        return root;
    }
}
