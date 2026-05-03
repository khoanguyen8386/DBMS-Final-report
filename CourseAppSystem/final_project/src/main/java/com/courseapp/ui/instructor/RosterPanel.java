package com.courseapp.ui.instructor;

import com.courseapp.model.Course;
import com.courseapp.model.Instructor;
import com.courseapp.model.Registration;
import com.courseapp.service.InstructorService;
import com.courseapp.ui.UITheme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RosterPanel extends JPanel {

    private final Instructor       instructor;
    private final InstructorService instructorService = new InstructorService();

    private JComboBox<Course>  courseCombo;
    private JTable             table;
    private DefaultTableModel  model;
    private JLabel             countLabel;

    private static final String[] COLS = {"Student ID","Name","Email","Status","Grade","Registered On"};

    public RosterPanel(Instructor instructor) {
        this.instructor = instructor;
        setLayout(new BorderLayout(0, 12));
        setBackground(UITheme.BG_LIGHT);
        setBorder(UITheme.paddingBorder(16, 16));
        buildUI();
    }

    private void buildUI() {
        // Course selector
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topBar.setOpaque(false);
        topBar.add(UITheme.headerLabel("Class Roster"));
        topBar.add(Box.createHorizontalStrut(16));
        topBar.add(new JLabel("Select Course:"));

        courseCombo = new JComboBox<>();
        courseCombo.setFont(UITheme.FONT_NORMAL);
        courseCombo.setPreferredSize(new Dimension(300, 36));
        List<Course> myCourses = instructorService.getMyCourses(instructor.getId());
        if (myCourses.isEmpty()) {
            courseCombo.addItem(null);
        } else {
            myCourses.forEach(courseCombo::addItem);
        }
        courseCombo.addActionListener(e -> loadRoster());
        topBar.add(courseCombo);

        JButton refreshBtn = UITheme.grayButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(50, 36));
        refreshBtn.addActionListener(e -> loadRoster());
        topBar.add(refreshBtn);

        // Table
        model = new DefaultTableModel(COLS, 0) { public boolean isCellEditable(int r,int c){return false;} };
        table = new JTable(model);
        UITheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Bottom
        countLabel = UITheme.secondaryLabel("No course selected.");

        add(topBar,                 BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(countLabel,             BorderLayout.SOUTH);

        if (!myCourses.isEmpty()) loadRoster();
    }

    void loadRoster() {
        model.setRowCount(0);
        Course selected = (Course) courseCombo.getSelectedItem();
        if (selected == null) return;
        List<Registration> roster = instructorService.getRoster(selected.getId());
        roster.forEach(r -> model.addRow(new Object[]{
            r.getStudentId(), r.getStudentName(),
            "—", // email not in Registration — ok for display
            r.getStatusDisplay(),
            r.getGrade() != null ? r.getGrade() : "—",
            r.getRegisteredAt() != null ? r.getRegisteredAt().toString().substring(0,16) : "—"
        }));
        countLabel.setText(roster.size() + " student(s) enrolled in " + selected.getCode());
    }

    /** Used by GradeEntryPanel to get the currently selected course */
    public Course getSelectedCourse() {
        return (Course) courseCombo.getSelectedItem();
    }
}
