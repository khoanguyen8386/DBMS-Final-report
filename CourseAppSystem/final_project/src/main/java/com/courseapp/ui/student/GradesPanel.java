package com.courseapp.ui.student;

import com.courseapp.model.Registration;
import com.courseapp.model.Student;
import com.courseapp.service.RegistrationService;
import com.courseapp.service.ReportService;
import com.courseapp.ui.UITheme;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GradesPanel extends JPanel {

    private final Student             student;
    private final RegistrationService regService    = new RegistrationService();
    private final ReportService       reportService = new ReportService();

    public GradesPanel(Student student) {
        this.student = student;
        setLayout(new BorderLayout(0, 12));
        setBackground(UITheme.BG_LIGHT);
        setBorder(UITheme.paddingBorder(16, 16));
        buildUI();
    }

    private void buildUI() {
        // ── Header ────────────────────────────────────────────────────────────
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topBar.setOpaque(false);
        topBar.add(UITheme.headerLabel("Academic Record & Grades"));

        // ── GPA summary card ──────────────────────────────────────────────────
        List<Registration> transcript = reportService.getTranscript(student.getId());
        double gpa     = reportService.calculateGPA(student.getId());
        long completed = transcript.stream()
                                   .filter(r -> r.getStatus() == Registration.Status.completed)
                                   .count();
        long enrolled  = transcript.stream()
                                   .filter(r -> r.getStatus() == Registration.Status.enrolled)
                                   .count();

        JPanel summaryRow = new JPanel(new GridLayout(1, 3, 16, 0));
        summaryRow.setOpaque(false);
        summaryRow.add(UITheme.statCard("Current GPA",
            gpa > 0 ? String.format("%.2f", gpa) : "N/A", UITheme.PRIMARY));
        summaryRow.add(UITheme.statCard("Completed",
            String.valueOf(completed), UITheme.SUCCESS));
        summaryRow.add(UITheme.statCard("In Progress",
            String.valueOf(enrolled), UITheme.WARNING));

        // ── Grades table ──────────────────────────────────────────────────────
        String[] cols = {"Course Code", "Course Title", "Status", "Grade", "GPA Points"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        for (Registration r : transcript) {
            String gpaPoints = getGpaPoints(r.getGrade());
            model.addRow(new Object[]{
                r.getCourseCode(),
                r.getCourseTitle(),
                r.getStatusDisplay(),
                r.getGrade() != null ? r.getGrade() : "—",
                gpaPoints
            });
        }

        JTable table = new JTable(model);
        UITheme.styleTable(table);

        // Color-code grade column
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                String grade = value != null ? value.toString() : "—";
                if (!isSelected) {
                    setForeground(gradeColor(grade));
                    setFont(UITheme.FONT_HEADER);
                }
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));

        // ── Grade legend ──────────────────────────────────────────────────────
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 6));
        legend.setBackground(UITheme.BG_WHITE);
        legend.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        legend.add(coloredLabel("A+/A = 4.0", UITheme.SUCCESS));
        legend.add(coloredLabel("B+/B = 3.5/3.0", UITheme.PRIMARY));
        legend.add(coloredLabel("C+/C = 2.5/2.0", UITheme.WARNING));
        legend.add(coloredLabel("D+/D = 1.5/1.0", new Color(234, 88, 12)));
        legend.add(coloredLabel("F = 0.0", UITheme.DANGER));

        JPanel tableSection = new JPanel(new BorderLayout());
        tableSection.setBackground(UITheme.BG_WHITE);
        tableSection.add(scroll,  BorderLayout.CENTER);
        tableSection.add(legend,  BorderLayout.SOUTH);

        add(topBar,      BorderLayout.NORTH);
        add(summaryRow,  BorderLayout.CENTER);
        add(tableSection,BorderLayout.SOUTH);
    }

    private Color gradeColor(String grade) {
        if (grade == null || grade.equals("—")) return UITheme.TEXT_SECONDARY;
        return switch (grade) {
            case "A+", "A" -> UITheme.SUCCESS;
            case "B+", "B" -> UITheme.PRIMARY;
            case "C+", "C" -> UITheme.WARNING;
            case "D+", "D" -> new Color(234, 88, 12);
            case "F"       -> UITheme.DANGER;
            default        -> UITheme.TEXT_PRIMARY;
        };
    }

    private String getGpaPoints(String grade) {
        if (grade == null) return "—";
        return switch (grade) {
            case "A+", "A" -> "4.0";
            case "B+"      -> "3.5";
            case "B"       -> "3.0";
            case "C+"      -> "2.5";
            case "C"       -> "2.0";
            case "D+"      -> "1.5";
            case "D"       -> "1.0";
            case "F"       -> "0.0";
            default        -> "—";
        };
    }

    private JLabel coloredLabel(String text, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_SMALL);
        lbl.setForeground(color);
        return lbl;
    }
}
