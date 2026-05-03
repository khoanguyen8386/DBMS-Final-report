package com.courseapp.ui.student;

import com.courseapp.model.Registration;
import com.courseapp.model.Student;
import com.courseapp.service.RegistrationService;
import com.courseapp.service.ReportService;
import com.courseapp.ui.UITheme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {

    private final Student             student;
    private final RegistrationService regService    = new RegistrationService();
    private final ReportService       reportService = new ReportService();

    public DashboardPanel(Student student) {
        this.student = student;
        setLayout(new BorderLayout(0, 12));
        setBackground(UITheme.BG_LIGHT);
        setBorder(UITheme.paddingBorder(16, 16));
        build();
    }

    private void build() {
        List<Registration> myRegs = regService.getRegistrationsByStudent(student.getId());
        long enrolled  = myRegs.stream().filter(r -> r.getStatus() == Registration.Status.enrolled).count();
        long completed = myRegs.stream().filter(r -> r.getStatus() == Registration.Status.completed).count();
        double gpa     = reportService.calculateGPA(student.getId());

        // ── Welcome header ────────────────────────────────────────────────────
        JPanel welcomePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        welcomePanel.setBackground(UITheme.BG_WHITE);
        welcomePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(14, 20, 14, 20)
        ));

        JLabel welcomeLbl = new JLabel("Welcome, " + student.getName() + "!");
        welcomeLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLbl.setForeground(UITheme.TEXT_PRIMARY);

        JLabel infoLbl = new JLabel(
            "Student ID: " + student.getId() +
            "   |   Department: " + (student.getDeptName() != null ? student.getDeptName() : "—") +
            "   |   Enroll Year: " + student.getEnrollYear()
        );
        infoLbl.setFont(UITheme.FONT_SMALL);
        infoLbl.setForeground(UITheme.TEXT_SECONDARY);
        welcomePanel.add(welcomeLbl);
        welcomePanel.add(infoLbl);

        // ── Stat cards ────────────────────────────────────────────────────────
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 12, 0));
        statsPanel.setOpaque(false);
        statsPanel.setPreferredSize(new Dimension(0, 90));
        statsPanel.add(UITheme.statCard("Enrolled Courses",  String.valueOf(enrolled),  UITheme.PRIMARY));
        statsPanel.add(UITheme.statCard("Completed Courses", String.valueOf(completed), UITheme.SUCCESS));
        statsPanel.add(UITheme.statCard("Current GPA",
            gpa > 0 ? String.format("%.2f", gpa) : "N/A", UITheme.WARNING));

        // ── Registrations table ───────────────────────────────────────────────
        String[] cols = {"Course Code", "Course Title", "Status", "Grade", "Registered On"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        myRegs.forEach(r -> model.addRow(new Object[]{
            r.getCourseCode(),
            r.getCourseTitle(),
            r.getStatusDisplay(),
            r.getGrade() != null ? r.getGrade() : "—",
            r.getRegisteredAt() != null ? r.getRegisteredAt().toString().substring(0, 16) : "—"
        }));

        JTable table = new JTable(model);
        UITheme.styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));

        // Header row above table
        JPanel tableHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        tableHeader.setBackground(UITheme.HEADER_BG);
        JLabel tableTitle = new JLabel("My Registered Courses");
        tableTitle.setFont(UITheme.FONT_HEADER);
        tableTitle.setForeground(UITheme.HEADER_FG);
        tableHeader.add(tableTitle);

        JPanel tableSection = new JPanel(new BorderLayout());
        tableSection.setBackground(UITheme.BG_WHITE);
        tableSection.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        tableSection.add(tableHeader, BorderLayout.NORTH);
        tableSection.add(scroll,      BorderLayout.CENTER);

        // ── Top section: welcome + stats stacked ──────────────────────────────
        JPanel topSection = new JPanel(new BorderLayout(0, 12));
        topSection.setOpaque(false);
        topSection.add(welcomePanel, BorderLayout.NORTH);
        topSection.add(statsPanel,   BorderLayout.CENTER);

        add(topSection,   BorderLayout.NORTH);
        add(tableSection, BorderLayout.CENTER);
    }
}
