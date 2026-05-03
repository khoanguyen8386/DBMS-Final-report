package com.courseapp.ui.student;

import com.courseapp.model.Registration;
import com.courseapp.model.Student;
import com.courseapp.service.RegistrationService;
import com.courseapp.service.RegistrationService.DropResult;
import com.courseapp.ui.UITheme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MyCoursesPanel extends JPanel {

    private final Student             student;
    private final RegistrationService regService = new RegistrationService();

    private JTable            table;
    private DefaultTableModel model;
    private JLabel            statusLabel;

    private static final String[] COLUMNS = {"Course ID", "Code", "Title", "Status", "Grade", "Registered On"};

    public MyCoursesPanel(Student student) {
        this.student = student;
        setLayout(new BorderLayout(0, 12));
        setBackground(UITheme.BG_LIGHT);
        setBorder(UITheme.paddingBorder(16, 16));
        buildUI();
        loadMyCourses();
    }

    private void buildUI() {
        // ── Header ────────────────────────────────────────────────────────────
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topBar.setOpaque(false);
        topBar.add(UITheme.headerLabel("My Registered Courses"));

        JButton refreshBtn = UITheme.grayButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(110, 36));
        refreshBtn.addActionListener(e -> loadMyCourses());
        topBar.add(Box.createHorizontalStrut(16));
        topBar.add(refreshBtn);

        // ── Table ─────────────────────────────────────────────────────────────
        model = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);

        // Hide Course ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));

        // ── Bottom bar ────────────────────────────────────────────────────────
        JPanel bottomBar = new JPanel(new BorderLayout(10, 0));
        bottomBar.setOpaque(false);

        statusLabel = UITheme.secondaryLabel("Loading…");

        JButton dropBtn = UITheme.dangerButton("Drop Course");
        dropBtn.addActionListener(e -> handleDrop());

        bottomBar.add(statusLabel, BorderLayout.WEST);
        bottomBar.add(dropBtn,     BorderLayout.EAST);

        add(topBar,    BorderLayout.NORTH);
        add(scroll,    BorderLayout.CENTER);
        add(bottomBar, BorderLayout.SOUTH);
    }

    private void loadMyCourses() {
        model.setRowCount(0);
        List<Registration> regs = regService.getRegistrationsByStudent(student.getId());
        for (Registration r : regs) {
            model.addRow(new Object[]{
                r.getCourseId(),
                r.getCourseCode(),
                r.getCourseTitle(),
                r.getStatusDisplay(),
                r.getGrade() != null ? r.getGrade() : "—",
                r.getRegisteredAt() != null
                    ? r.getRegisteredAt().toString().substring(0, 16) : "—"
            });
        }
        statusLabel.setText(regs.size() + " course(s) registered.");
    }

    private void handleDrop() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a course to drop.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int    courseId    = (int)    model.getValueAt(row, 0);
        String courseCode  = (String) model.getValueAt(row, 1);
        String courseTitle = (String) model.getValueAt(row, 2);
        String status      = (String) model.getValueAt(row, 3);

        if ("Completed".equals(status) || "Dropped".equals(status)) {
            JOptionPane.showMessageDialog(this,
                "You cannot drop a course that is already " + status.toLowerCase() + ".",
                "Cannot Drop", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Drop [" + courseCode + "] " + courseTitle + "?\n" +
            "This action cannot be undone.",
            "Confirm Drop", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        DropResult result = regService.drop(student.getId(), courseId);

        switch (result) {
            case SUCCESS     -> {
                JOptionPane.showMessageDialog(this,
                    "Successfully dropped " + courseTitle + ".",
                    "Dropped", JOptionPane.INFORMATION_MESSAGE);
                loadMyCourses();
            }
            case NOT_ENROLLED -> JOptionPane.showMessageDialog(this,
                "You are not enrolled in this course.");
            case ERROR        -> JOptionPane.showMessageDialog(this,
                "An error occurred. Please try again.");
        }
    }
}
