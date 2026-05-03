package com.courseapp.ui.admin;

import com.courseapp.model.Registration;
import com.courseapp.service.ReportService;
import com.courseapp.service.ReportService.CourseFillRate;
import com.courseapp.service.ReportService.DeptSummary;
import com.courseapp.ui.UITheme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReportsPanel extends JPanel {

    private final ReportService reportService = new ReportService();

    public ReportsPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(UITheme.BG_LIGHT);
        setBorder(UITheme.paddingBorder(16, 16));
        buildUI();
    }

    private void buildUI() {
        // Stat cards
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 16, 0));
        statsRow.setOpaque(false);
        statsRow.add(UITheme.statCard("Total Students",     String.valueOf(reportService.getTotalStudents()),    UITheme.PRIMARY));
        statsRow.add(UITheme.statCard("Total Courses",      String.valueOf(reportService.getTotalCourses()),     UITheme.SUCCESS));
        statsRow.add(UITheme.statCard("Total Enrollments",  String.valueOf(reportService.getTotalEnrollments()), UITheme.WARNING));

        // Tabbed sub-sections
        JTabbedPane subTabs = new JTabbedPane();
        subTabs.setFont(UITheme.FONT_NORMAL);
        subTabs.addTab("Course Fill Rates",      buildFillRatePanel());
        subTabs.addTab("Department Summary",     buildDeptSummaryPanel());
        subTabs.addTab("Recent Registrations",   buildRecentRegPanel());

        add(statsRow, BorderLayout.NORTH);
        add(subTabs,  BorderLayout.CENTER);
    }

    private JPanel buildFillRatePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.BG_WHITE);
        String[] cols = {"Code","Title","Enrolled","Capacity","Fill Rate"};
        DefaultTableModel m = new DefaultTableModel(cols,0) { public boolean isCellEditable(int r,int c){return false;} };
        List<CourseFillRate> rates = reportService.getCourseFillRates();
        rates.forEach(r -> m.addRow(new Object[]{
            r.courseCode, r.courseTitle, r.enrolled, r.capacity,
            String.format("%.0f%%", r.fillPercent)
        }));
        JTable t = new JTable(m);
        UITheme.styleTable(t);
        p.add(new JScrollPane(t), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildDeptSummaryPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.BG_WHITE);
        String[] cols = {"Department","Courses","Students","Total Enrolled"};
        DefaultTableModel m = new DefaultTableModel(cols,0) { public boolean isCellEditable(int r,int c){return false;} };
        reportService.getDeptSummaries().forEach(d ->
            m.addRow(new Object[]{ d.deptName, d.courseCount, d.studentCount, d.totalEnrolled }));
        JTable t = new JTable(m);
        UITheme.styleTable(t);
        p.add(new JScrollPane(t), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildRecentRegPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.BG_WHITE);
        String[] cols = {"Student ID","Student Name","Course Code","Course Title","Status","Registered On"};
        DefaultTableModel m = new DefaultTableModel(cols,0) { public boolean isCellEditable(int r,int c){return false;} };
        reportService.getRecentRegistrations(50).forEach(r ->
            m.addRow(new Object[]{
                r.getStudentId(), r.getStudentName(),
                r.getCourseCode(), r.getCourseTitle(),
                r.getStatusDisplay(),
                r.getRegisteredAt() != null ? r.getRegisteredAt().toString().substring(0,16) : "—"
            }));
        JTable t = new JTable(m);
        UITheme.styleTable(t);
        p.add(new JScrollPane(t), BorderLayout.CENTER);
        return p;
    }
}
