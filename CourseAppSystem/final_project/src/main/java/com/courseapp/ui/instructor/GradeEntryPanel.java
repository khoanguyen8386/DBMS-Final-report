package com.courseapp.ui.instructor;

import com.courseapp.model.Course;
import com.courseapp.model.Instructor;
import com.courseapp.model.Registration;
import com.courseapp.service.InstructorService;
import com.courseapp.service.RegistrationService;
import com.courseapp.ui.UITheme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GradeEntryPanel extends JPanel {

    private final Instructor        instructor;
    private final InstructorService instructorService = new InstructorService();

    private JComboBox<Course>  courseCombo;
    private JTable             table;
    private DefaultTableModel  model;
    private JLabel             statusLabel;

    private static final String[] COLS    = {"Student ID","Student Name","Current Grade","Status"};
    private static final String[] GRADES  = {"—","A+","A","B+","B","C+","C","D+","D","F","I","W"};

    public GradeEntryPanel(Instructor instructor) {
        this.instructor = instructor;
        setLayout(new BorderLayout(0, 12));
        setBackground(UITheme.BG_LIGHT);
        setBorder(UITheme.paddingBorder(16, 16));
        buildUI();
    }

    private void buildUI() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topBar.setOpaque(false);
        topBar.add(UITheme.headerLabel("Grade Entry"));
        topBar.add(Box.createHorizontalStrut(16));
        topBar.add(new JLabel("Course:"));

        courseCombo = new JComboBox<>();
        courseCombo.setFont(UITheme.FONT_NORMAL);
        courseCombo.setPreferredSize(new Dimension(300, 36));
        instructorService.getMyCourses(instructor.getId()).forEach(courseCombo::addItem);
        courseCombo.addActionListener(e -> loadStudents());
        topBar.add(courseCombo);

        model = new DefaultTableModel(COLS, 0) { public boolean isCellEditable(int r,int c){return false;} };
        table = new JTable(model);
        UITheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        statusLabel = UITheme.secondaryLabel(" ");

        // Grade entry controls
        JPanel gradeBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        gradeBar.setBackground(UITheme.BG_WHITE);
        gradeBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR));

        gradeBar.add(new JLabel("Assign grade to selected student:"));
        JComboBox<String> gradeCombo = new JComboBox<>(GRADES);
        gradeCombo.setFont(UITheme.FONT_NORMAL);
        gradeCombo.setPreferredSize(new Dimension(80, 32));
        gradeBar.add(gradeCombo);

        JButton assignBtn = UITheme.successButton("Assign Grade");
        assignBtn.setPreferredSize(new Dimension(150, 36));
        assignBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { statusLabel.setText("Select a student first."); return; }
            String grade = (String) gradeCombo.getSelectedItem();
            if ("—".equals(grade)) { statusLabel.setText("Select a valid grade."); return; }

            String studentId = (String) model.getValueAt(row, 0);
            Course course    = (Course) courseCombo.getSelectedItem();
            if (course == null) return;

            RegistrationService.GradeResult result =
                instructorService.assignGrade(studentId, course.getId(), grade);

            switch (result) {
                case SUCCESS       -> { statusLabel.setText("Grade " + grade + " assigned to " + model.getValueAt(row,1)); loadStudents(); }
                case INVALID_GRADE -> statusLabel.setText("Invalid grade value.");
                case NOT_FOUND     -> statusLabel.setText("Student not enrolled in this course.");
                case ERROR         -> statusLabel.setText("Error saving grade.");
            }
        });
        gradeBar.add(assignBtn);
        gradeBar.add(Box.createHorizontalStrut(20));
        gradeBar.add(statusLabel);

        add(topBar,                 BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(gradeBar,               BorderLayout.SOUTH);

        if (courseCombo.getItemCount() > 0) loadStudents();
    }

    private void loadStudents() {
        model.setRowCount(0);
        Course selected = (Course) courseCombo.getSelectedItem();
        if (selected == null) return;
        List<Registration> roster = instructorService.getRoster(selected.getId());
        roster.forEach(r -> model.addRow(new Object[]{
            r.getStudentId(),
            r.getStudentName(),
            r.getGrade() != null ? r.getGrade() : "—",
            r.getStatusDisplay()
        }));
        statusLabel.setText(roster.size() + " students in " + selected.getCode());
    }
}
