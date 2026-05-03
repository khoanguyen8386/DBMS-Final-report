package com.courseapp.ui.admin;

import com.courseapp.model.Student;
import com.courseapp.service.StudentService;
import com.courseapp.ui.UITheme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageStudentsPanel extends JPanel {

    private final StudentService studentService = new StudentService();
    private JTable            table;
    private DefaultTableModel model;
    private JTextField        searchField;

    private static final String[] COLS = {"ID","Name","Email","Phone","Department","Enroll Year","Enrolled At"};

    public ManageStudentsPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UITheme.BG_LIGHT);
        setBorder(UITheme.paddingBorder(16, 16));
        buildUI();
        loadStudents();
    }

    private void buildUI() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topBar.setOpaque(false);
        topBar.add(UITheme.headerLabel("Manage Students"));

        searchField = UITheme.styledTextField(22);
        JButton searchBtn = UITheme.primaryButton("Search");
        searchBtn.setPreferredSize(new Dimension(90, 36));
        JButton clearBtn = UITheme.grayButton("Clear");
        clearBtn.setPreferredSize(new Dimension(70, 36));
        searchBtn.addActionListener(e -> loadFiltered());
        clearBtn.addActionListener(e  -> { searchField.setText(""); loadStudents(); });
        searchField.addActionListener(e -> loadFiltered());
        topBar.add(Box.createHorizontalStrut(10));
        topBar.add(searchField); topBar.add(searchBtn); topBar.add(clearBtn);

        model = new DefaultTableModel(COLS, 0) { public boolean isCellEditable(int r,int c){return false;} };
        table = new JTable(model);
        UITheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnBar.setOpaque(false);
        JButton viewBtn   = UITheme.primaryButton("View Details");
        JButton deleteBtn = UITheme.dangerButton("Delete Student");
        viewBtn.addActionListener(e   -> viewSelected());
        deleteBtn.addActionListener(e -> deleteSelected());
        btnBar.add(viewBtn); btnBar.add(deleteBtn);

        add(topBar,                 BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btnBar,                 BorderLayout.SOUTH);
    }

    private void loadStudents() {
        model.setRowCount(0);
        studentService.getAllStudents().forEach(s -> model.addRow(row(s)));
    }

    private void loadFiltered() {
        model.setRowCount(0);
        studentService.searchStudents(searchField.getText().trim()).forEach(s -> model.addRow(row(s)));
    }

    private Object[] row(Student s) {
        return new Object[]{ s.getId(), s.getName(), s.getEmail(), s.getPhone(),
            s.getDeptName(), s.getEnrollYear(),
            s.getEnrolledAt() != null ? s.getEnrolledAt().toString().substring(0,10) : "—" };
    }

    private void viewSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Select a student first."); return; }
        String id = (String) model.getValueAt(row, 0);
        Student s = studentService.getStudentById(id);
        if (s == null) return;
        JOptionPane.showMessageDialog(this,
            "Student ID : " + s.getId() + "\n" +
            "Name       : " + s.getName() + "\n" +
            "Email      : " + s.getEmail() + "\n" +
            "Phone      : " + (s.getPhone() != null ? s.getPhone() : "—") + "\n" +
            "Department : " + (s.getDeptName() != null ? s.getDeptName() : "—") + "\n" +
            "Enroll Year: " + s.getEnrollYear(),
            "Student Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Select a student first."); return; }
        String id   = (String) model.getValueAt(row, 0);
        String name = (String) model.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete student: " + name + " (" + id + ")?\n" +
            "All their registrations will also be deleted.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            studentService.deleteStudent(id);
            loadStudents();
        }
    }
}
