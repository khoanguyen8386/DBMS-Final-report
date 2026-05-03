package com.courseapp.ui;

import com.courseapp.model.Department;
import com.courseapp.service.AdminService;
import com.courseapp.service.StudentService;
import com.courseapp.service.StudentService.RegisterResult;
import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.List;

public class RegisterFrame extends JDialog {

    private JTextField     nameField, emailField, phoneField;
    private JPasswordField passField, confirmField;
    private JComboBox<Department> deptCombo;
    private JComboBox<Integer>    yearCombo;
    private JLabel         statusLabel;

    private final StudentService studentService = new StudentService();
    private final AdminService   adminService   = new AdminService();

    public RegisterFrame(JFrame parent) {
        super(parent, "Create Student Account", true);
        setSize(480, 580);
        setLocationRelativeTo(parent);
        setResizable(false);
        setContentPane(buildUI());
    }

    private JPanel buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_LIGHT);

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 16));
        header.setBackground(UITheme.PRIMARY);
        JLabel title = new JLabel("Create Student Account");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(Color.WHITE);
        header.add(title);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG_WHITE);
        form.setBorder(UITheme.paddingBorder(20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);

        nameField    = UITheme.styledTextField(20);
        emailField   = UITheme.styledTextField(20);
        phoneField   = UITheme.styledTextField(20);
        passField    = UITheme.styledPasswordField(20);
        confirmField = UITheme.styledPasswordField(20);

        // Department combo
        deptCombo = new JComboBox<>();
        deptCombo.setFont(UITheme.FONT_NORMAL);
        List<Department> depts = adminService.getAllDepartments();
        depts.forEach(deptCombo::addItem);

        // Enroll year combo (last 5 years + current)
        yearCombo = new JComboBox<>();
        yearCombo.setFont(UITheme.FONT_NORMAL);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int y = currentYear; y >= currentYear - 5; y--) yearCombo.addItem(y);

        addRow(form, gbc, 0, "Full Name *",    nameField);
        addRow(form, gbc, 1, "Email *",         emailField);
        addRow(form, gbc, 2, "Phone",           phoneField);
        addRow(form, gbc, 3, "Department *",    deptCombo);
        addRow(form, gbc, 4, "Enroll Year *",   yearCombo);
        addRow(form, gbc, 5, "Password *",      passField);
        addRow(form, gbc, 6, "Confirm Password *", confirmField);

        // Status
        statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.DANGER);
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        form.add(statusLabel, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        btnPanel.setBackground(UITheme.BG_LIGHT);
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR));

        JButton cancelBtn = UITheme.grayButton("Cancel");
        JButton registerBtn = UITheme.primaryButton("Create Account");

        cancelBtn.addActionListener(e -> dispose());
        registerBtn.addActionListener(e -> handleRegister());

        btnPanel.add(cancelBtn);
        btnPanel.add(registerBtn);

        root.add(header, BorderLayout.NORTH);
        root.add(new JScrollPane(form), BorderLayout.CENTER);
        root.add(btnPanel, BorderLayout.SOUTH);
        return root;
    }

    private void addRow(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridwidth = 1; gbc.weightx = 0;
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_SMALL);
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        form.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        form.add(field, gbc);
    }

    private void handleRegister() {
        String name    = nameField.getText().trim();
        String email   = emailField.getText().trim().toLowerCase();
        String phone   = phoneField.getText().trim();
        String pass    = new String(passField.getPassword());
        String confirm = new String(confirmField.getPassword());

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("Please fill in all required fields.");
            return;
        }
        if (!pass.equals(confirm)) {
            statusLabel.setText("Passwords do not match.");
            return;
        }

        Department dept = (Department) deptCombo.getSelectedItem();
        int year = (int) yearCombo.getSelectedItem();

        if (dept == null) {
            statusLabel.setText("Please select a department.");
            return;
        }

        RegisterResult result = studentService.register(
            name, email, pass, phone, dept.getId(), dept.getCode(), year
        );

        switch (result) {
            case SUCCESS -> {
                JOptionPane.showMessageDialog(this,
                    "Account created successfully!\nYou can now log in with your email.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
            case EMAIL_ALREADY_EXISTS -> statusLabel.setText("This email is already registered.");
            case INVALID_EMAIL        -> statusLabel.setText("Please enter a valid email address.");
            case INVALID_PASSWORD     -> statusLabel.setText("Password must be at least 4 characters.");
            case ERROR                -> statusLabel.setText("An error occurred. Please try again.");
        }
    }
}
