package com.courseapp.ui.admin;

import com.courseapp.model.Department;
import com.courseapp.service.AdminService;
import com.courseapp.ui.UITheme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManageDepartmentsPanel extends JPanel {

    private final AdminService    adminService = new AdminService();
    private JTable            table;
    private DefaultTableModel model;

    private static final String[] COLS = {"ID","Code","Name","Faculty","Office","Phone"};

    public ManageDepartmentsPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UITheme.BG_LIGHT);
        setBorder(UITheme.paddingBorder(16, 16));
        buildUI();
        loadDepts();
    }

    private void buildUI() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topBar.setOpaque(false);
        topBar.add(UITheme.headerLabel("Manage Departments"));

        model = new DefaultTableModel(COLS, 0) { public boolean isCellEditable(int r,int c){return false;} };
        table = new JTable(model);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnBar.setOpaque(false);
        JButton addBtn    = UITheme.successButton("Add");
        JButton editBtn   = UITheme.primaryButton("Edit");
        JButton deleteBtn = UITheme.dangerButton("Delete");
        addBtn.addActionListener(e    -> showDialog(null));
        editBtn.addActionListener(e   -> editSelected());
        deleteBtn.addActionListener(e -> deleteSelected());
        btnBar.add(addBtn); btnBar.add(editBtn); btnBar.add(deleteBtn);

        add(topBar,                 BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btnBar,                 BorderLayout.SOUTH);
    }

    private void loadDepts() {
        model.setRowCount(0);
        adminService.getAllDepartments().forEach(d ->
            model.addRow(new Object[]{ d.getId(), d.getCode(), d.getName(),
                d.getFaculty(), d.getOffice(), d.getPhone() }));
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Select a department first."); return; }
        int id = (int) model.getValueAt(row, 0);
        showDialog(adminService.getDepartmentById(id));
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Select a department first."); return; }
        int id = (int) model.getValueAt(row, 0);
        String name = (String) model.getValueAt(row, 2);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete department: " + name + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) { adminService.deleteDepartment(id); loadDepts(); }
    }

    private void showDialog(Department existing) {
        boolean isEdit = existing != null;
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            isEdit ? "Edit Department" : "Add Department", true);
        dlg.setSize(400, 360);
        dlg.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG_WHITE);
        form.setBorder(UITheme.paddingBorder(20, 24));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL; g.insets = new Insets(6,4,6,4);

        JTextField codeF    = UITheme.styledTextField(15);
        JTextField nameF    = UITheme.styledTextField(15);
        JTextField facultyF = UITheme.styledTextField(15);
        JTextField officeF  = UITheme.styledTextField(15);
        JTextField phoneF   = UITheme.styledTextField(15);

        if (isEdit) {
            codeF.setText(existing.getCode());
            nameF.setText(existing.getName());
            facultyF.setText(existing.getFaculty());
            officeF.setText(existing.getOffice());
            phoneF.setText(existing.getPhone());
        }

        String[][] rows = {{"Code *",codeF.getText()},{"Name *",""},{"Faculty",""},{"Office",""},{"Phone",""}};
        JTextField[] fields = {codeF, nameF, facultyF, officeF, phoneF};
        String[] labels = {"Code *","Name *","Faculty","Office","Phone"};
        for (int i=0;i<labels.length;i++) {
            g.gridwidth=1; g.weightx=0; g.gridx=0; g.gridy=i;
            JLabel l = new JLabel(labels[i]); l.setFont(UITheme.FONT_SMALL); form.add(l,g);
            g.gridx=1; g.weightx=1; form.add(fields[i],g);
        }

        JLabel status = new JLabel(" ");
        status.setForeground(UITheme.DANGER); status.setFont(UITheme.FONT_SMALL);
        g.gridx=0; g.gridy=5; g.gridwidth=2; form.add(status,g);

        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,10));
        btnP.setBackground(UITheme.BG_LIGHT);
        JButton cancel = UITheme.grayButton("Cancel");
        JButton save   = UITheme.primaryButton(isEdit ? "Save" : "Add");
        cancel.addActionListener(e -> dlg.dispose());
        save.addActionListener(e -> {
            Department d = isEdit ? existing : new Department();
            d.setCode(codeF.getText().trim().toUpperCase());
            d.setName(nameF.getText().trim());
            d.setFaculty(facultyF.getText().trim());
            d.setOffice(officeF.getText().trim());
            d.setPhone(phoneF.getText().trim());
            boolean ok = isEdit ? adminService.updateDepartment(d) : adminService.addDepartment(d);
            if (ok) { loadDepts(); dlg.dispose(); }
            else status.setText("Failed. Check code is unique.");
        });
        btnP.add(cancel); btnP.add(save);
        dlg.add(form, BorderLayout.CENTER);
        dlg.add(btnP, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }
}
