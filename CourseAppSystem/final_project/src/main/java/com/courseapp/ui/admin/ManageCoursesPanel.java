package com.courseapp.ui.admin;

import com.courseapp.model.Course;
import com.courseapp.model.Department;
import com.courseapp.model.Instructor;
import com.courseapp.service.AdminService;
import com.courseapp.service.CourseService;
import com.courseapp.service.InstructorService;
import com.courseapp.ui.UITheme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageCoursesPanel extends JPanel {

    private final CourseService      courseService      = new CourseService();
    private final AdminService       adminService       = new AdminService();
    private final InstructorService  instructorService  = new InstructorService();

    private JTable            table;
    private DefaultTableModel model;
    private JTextField        searchField;

    private static final String[] COLS = {"ID","Code","Title","Department","Instructor","Credits","Capacity","Enrolled"};

    public ManageCoursesPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UITheme.BG_LIGHT);
        setBorder(UITheme.paddingBorder(16, 16));
        buildUI();
        loadCourses();
    }

    private void buildUI() {
        // Top bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topBar.setOpaque(false);
        topBar.add(UITheme.headerLabel("Manage Courses"));

        searchField = UITheme.styledTextField(20);
        JButton searchBtn = UITheme.primaryButton("Search");
        searchBtn.setPreferredSize(new Dimension(90, 36));
        JButton clearBtn  = UITheme.grayButton("Clear");
        clearBtn.setPreferredSize(new Dimension(70, 36));
        searchBtn.addActionListener(e -> loadFiltered());
        clearBtn.addActionListener(e  -> { searchField.setText(""); loadCourses(); });
        searchField.addActionListener(e -> loadFiltered());
        topBar.add(Box.createHorizontalStrut(10));
        topBar.add(searchField); topBar.add(searchBtn); topBar.add(clearBtn);

        // Table
        model = new DefaultTableModel(COLS, 0) { public boolean isCellEditable(int r,int c){return false;} };
        table = new JTable(model);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Bottom buttons
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnBar.setOpaque(false);
        JButton addBtn    = UITheme.successButton("Add Course");
        JButton editBtn   = UITheme.primaryButton("Edit");
        JButton deleteBtn = UITheme.dangerButton("Delete");
        addBtn.addActionListener(e    -> showCourseDialog(null));
        editBtn.addActionListener(e   -> editSelected());
        deleteBtn.addActionListener(e -> deleteSelected());
        btnBar.add(addBtn); btnBar.add(editBtn); btnBar.add(deleteBtn);

        add(topBar,                        BorderLayout.NORTH);
        add(new JScrollPane(table),        BorderLayout.CENTER);
        add(btnBar,                        BorderLayout.SOUTH);
    }

    private void loadCourses() {
        model.setRowCount(0);
        courseService.getAllCourses().forEach(c -> model.addRow(row(c)));
    }

    private void loadFiltered() {
        model.setRowCount(0);
        courseService.searchCourses(searchField.getText().trim()).forEach(c -> model.addRow(row(c)));
    }

    private Object[] row(Course c) {
        return new Object[]{ c.getId(), c.getCode(), c.getTitle(),
            c.getDeptName(), c.getInstructorName(), c.getCredits(), c.getCapacity(), c.getEnrolled() };
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Select a course first."); return; }
        int id = (int) model.getValueAt(row, 0);
        showCourseDialog(courseService.getCourseById(id));
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Select a course first."); return; }
        int id = (int) model.getValueAt(row, 0);
        String title = (String) model.getValueAt(row, 2);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete course: " + title + "?\nAll related schedules will also be deleted.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            courseService.deleteCourse(id);
            loadCourses();
        }
    }

    private void showCourseDialog(Course existing) {
        boolean isEdit = existing != null;
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            isEdit ? "Edit Course" : "Add Course", true);
        dlg.setSize(460, 420);
        dlg.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG_WHITE);
        form.setBorder(UITheme.paddingBorder(20, 24));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL; g.insets = new Insets(6,4,6,4);

        JTextField codeF    = UITheme.styledTextField(15);
        JTextField titleF   = UITheme.styledTextField(15);
        JTextField creditsF = UITheme.styledTextField(5);
        JTextField capF     = UITheme.styledTextField(5);

        JComboBox<Department>  deptCb = new JComboBox<>();
        JComboBox<Instructor>  instCb = new JComboBox<>();
        adminService.getAllDepartments().forEach(deptCb::addItem);
        instructorService.getAllInstructors().forEach(instCb::addItem);

        if (isEdit) {
            codeF.setText(existing.getCode());
            titleF.setText(existing.getTitle());
            creditsF.setText(String.valueOf(existing.getCredits()));
            capF.setText(String.valueOf(existing.getCapacity()));
            for (int i=0;i<deptCb.getItemCount();i++) if (deptCb.getItemAt(i).getId()==existing.getDeptId()) { deptCb.setSelectedIndex(i); break; }
            for (int i=0;i<instCb.getItemCount();i++) if (instCb.getItemAt(i).getId()==existing.getInstructorId()) { instCb.setSelectedIndex(i); break; }
        }

        addFormRow(form,g,0,"Code *",       codeF);
        addFormRow(form,g,1,"Title *",      titleF);
        addFormRow(form,g,2,"Credits *",    creditsF);
        addFormRow(form,g,3,"Capacity *",   capF);
        addFormRow(form,g,4,"Department *", deptCb);
        addFormRow(form,g,5,"Instructor *", instCb);

        JLabel status = new JLabel(" ");
        status.setForeground(UITheme.DANGER); status.setFont(UITheme.FONT_SMALL);
        g.gridx=0; g.gridy=6; g.gridwidth=2; form.add(status,g);

        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,10));
        btnP.setBackground(UITheme.BG_LIGHT);
        JButton cancel = UITheme.grayButton("Cancel");
        JButton save   = UITheme.primaryButton(isEdit ? "Save Changes" : "Add Course");
        cancel.addActionListener(e -> dlg.dispose());
        save.addActionListener(e -> {
            try {
                Course c = isEdit ? existing : new Course();
                if (!isEdit) c.setId(0);
                c.setCode(codeF.getText().trim());
                c.setTitle(titleF.getText().trim());
                c.setCredits(Integer.parseInt(creditsF.getText().trim()));
                c.setCapacity(Integer.parseInt(capF.getText().trim()));
                c.setDeptId(((Department) deptCb.getSelectedItem()).getId());
                c.setInstructorId(((Instructor) instCb.getSelectedItem()).getId());
                CourseService.CourseResult r = isEdit
                    ? courseService.updateCourse(c) : courseService.addCourse(c);
                if (r == CourseService.CourseResult.SUCCESS) { loadCourses(); dlg.dispose(); }
                else status.setText("Failed: " + r.name());
            } catch (Exception ex) { status.setText("Invalid input: " + ex.getMessage()); }
        });
        btnP.add(cancel); btnP.add(save);

        dlg.add(form, BorderLayout.CENTER);
        dlg.add(btnP, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void addFormRow(JPanel p, GridBagConstraints g, int row, String lbl, JComponent field) {
        g.gridwidth=1; g.weightx=0; g.gridx=0; g.gridy=row;
        JLabel l = new JLabel(lbl); l.setFont(UITheme.FONT_SMALL); p.add(l,g);
        g.gridx=1; g.weightx=1; p.add(field,g);
    }
}
