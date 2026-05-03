package com.courseapp.ui.student;

import com.courseapp.model.Course;
import com.courseapp.model.Department;
import com.courseapp.model.Student;
import com.courseapp.service.AdminService;
import com.courseapp.service.CourseService;
import com.courseapp.service.RegistrationService;
import com.courseapp.service.RegistrationService.EnrollResult;
import com.courseapp.service.ScheduleService;
import com.courseapp.ui.UITheme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CourseListPanel extends JPanel {

    private final Student             student;
    private final CourseService       courseService = new CourseService();
    private final RegistrationService regService    = new RegistrationService();
    private final ScheduleService     scheduleService = new ScheduleService();
    private final AdminService        adminService  = new AdminService();

    private JTable             table;
    private DefaultTableModel  model;
    private JTextField         searchField;
    private JComboBox<Object>  deptFilter;
    private JLabel             statusLabel;

    // Hidden column 0 stores course ID
    private static final String[] COLUMNS = {"ID", "Code", "Title", "Department", "Instructor", "Credits", "Available Seats"};

    public CourseListPanel(Student student) {
        this.student = student;
        setLayout(new BorderLayout(0, 12));
        setBackground(UITheme.BG_LIGHT);
        setBorder(UITheme.paddingBorder(16, 16));
        buildUI();
        loadCourses(null, 0);
    }

    private void buildUI() {
        // ── Top bar: search + filter ──────────────────────────────────────────
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topBar.setOpaque(false);

        searchField = UITheme.styledTextField(22);
        searchField.putClientProperty("JTextField.placeholderText", "Search by title or code…");

        JButton searchBtn = UITheme.primaryButton("Search");
        searchBtn.setPreferredSize(new Dimension(100, 36));

        deptFilter = UITheme.styledComboBox();
        deptFilter.addItem("All Departments");
        adminService.getAllDepartments().forEach(deptFilter::addItem);
        deptFilter.setPreferredSize(new Dimension(200, 36));

        JButton clearBtn = UITheme.grayButton("Clear");
        clearBtn.setPreferredSize(new Dimension(80, 36));

        topBar.add(UITheme.headerLabel("Browse Courses"));
        topBar.add(Box.createHorizontalStrut(16));
        topBar.add(searchField);
        topBar.add(searchBtn);
        topBar.add(deptFilter);
        topBar.add(clearBtn);

        searchBtn.addActionListener(e -> applyFilter());
        clearBtn.addActionListener(e -> { searchField.setText(""); deptFilter.setSelectedIndex(0); loadCourses(null, 0); });
        searchField.addActionListener(e -> applyFilter());

        // ── Table ─────────────────────────────────────────────────────────────
        model = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);  // hide ID column
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));

        // ── Bottom bar: status + enroll button ────────────────────────────────
        JPanel bottomBar = new JPanel(new BorderLayout(10, 0));
        bottomBar.setOpaque(false);

        statusLabel = UITheme.secondaryLabel("Select a course to enroll.");

        JButton enrollBtn = UITheme.primaryButton("Enroll");
        enrollBtn.addActionListener(e -> handleEnroll());

        bottomBar.add(statusLabel, BorderLayout.WEST);
        bottomBar.add(enrollBtn,   BorderLayout.EAST);

        add(topBar,    BorderLayout.NORTH);
        add(scroll,    BorderLayout.CENTER);
        add(bottomBar, BorderLayout.SOUTH);
    }

    private void applyFilter() {
        String keyword = searchField.getText().trim();
        Object selected = deptFilter.getSelectedItem();
        int deptId = (selected instanceof Department d) ? d.getId() : 0;
        loadCourses(keyword.isEmpty() ? null : keyword, deptId);
    }

    private void loadCourses(String keyword, int deptId) {
        model.setRowCount(0);
        List<Course> courses = courseService.filterCourses(keyword, deptId > 0 ? deptId : null);
        for (Course c : courses) {
            String seats = c.isFull()
                ? "FULL"
                : c.getAvailableSeats() + " / " + c.getCapacity();
            model.addRow(new Object[]{
                c.getId(), c.getCode(), c.getTitle(),
                c.getDeptName(), c.getInstructorName(),
                c.getCredits(), seats
            });
        }
        statusLabel.setText(courses.size() + " course(s) found.");
    }

    private void handleEnroll() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a course first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int    courseId    = (int)    model.getValueAt(row, 0);
        String courseCode  = (String) model.getValueAt(row, 1);
        String courseTitle = (String) model.getValueAt(row, 2);
        String seatsText   = (String) model.getValueAt(row, 6);

        if ("FULL".equals(seatsText)) {
            JOptionPane.showMessageDialog(this, "This course is full.", "Course Full", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check schedule conflict
        if (scheduleService.hasConflict(student.getId(), courseId)) {
            JOptionPane.showMessageDialog(this,
                "Schedule conflict detected!\nThis course overlaps with one you are already enrolled in.",
                "Schedule Conflict", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Enroll in: [" + courseCode + "] " + courseTitle + "?",
            "Confirm Enrollment", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        EnrollResult result = regService.enroll(student.getId(), courseId);

        switch (result) {
            case SUCCESS -> {
                JOptionPane.showMessageDialog(this,
                    "Successfully enrolled in " + courseTitle + "!",
                    "Enrolled", JOptionPane.INFORMATION_MESSAGE);
                loadCourses(null, 0);
            }
            case ALREADY_ENROLLED  -> JOptionPane.showMessageDialog(this, "You are already enrolled in this course.");
            case COURSE_FULL       -> JOptionPane.showMessageDialog(this, "This course is now full.");
            case COURSE_NOT_FOUND  -> JOptionPane.showMessageDialog(this, "Course not found.");
            case ERROR             -> JOptionPane.showMessageDialog(this, "An error occurred. Please try again.");
        }
    }
}
