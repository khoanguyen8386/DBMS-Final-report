package com.courseapp.service;

import com.courseapp.dao.StudentDAO;
import com.courseapp.model.Student;
import com.courseapp.util.PasswordUtil;
import com.courseapp.util.StudentIdGenerator;
import java.sql.SQLException;
import java.util.List;
import com.courseapp.util.PasswordUtil;

public class StudentService {

    private final StudentDAO studentDAO = new StudentDAO();

    // ── Register new student ──────────────────────────────────────────────────

    public enum RegisterResult {
        SUCCESS,
        EMAIL_ALREADY_EXISTS,
        INVALID_EMAIL,
        INVALID_PASSWORD,
        ERROR
    }

    /**
     * Registers a new student.
     * Auto-generates a unique student ID from dept code + enroll year.
     */
    public RegisterResult register(String name, String email, String password,
                               String phone, int deptId, String deptCode, int enrollYear) {
    
        if (!AuthService.isValidEmail(email))    return RegisterResult.INVALID_EMAIL;
        if (!AuthService.isValidPassword(password)) return RegisterResult.INVALID_PASSWORD;

        try {
            String studentId = StudentIdGenerator.generate(deptCode, enrollYear);

            Student s = new Student();
            s.setId(studentId);
            s.setName(name.trim());
            s.setEmail(email.trim().toLowerCase());
            
            // === HASH THE PASSWORD HERE ===
            String hashedPassword = PasswordUtil.hashPassword(password);
            s.setPassword(hashedPassword);
            
            s.setPhone(phone);
            s.setDeptId(deptId);
            s.setEnrollYear(enrollYear);

            boolean ok = studentDAO.insert(s);
            return ok ? RegisterResult.SUCCESS : RegisterResult.EMAIL_ALREADY_EXISTS;

        } catch (SQLException e) {
            e.printStackTrace();
            return RegisterResult.ERROR;
        }
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    public List<Student> getAllStudents() {
        return studentDAO.findAll();
    }

    public List<Student> getStudentsByDept(int deptId) {
        return studentDAO.findByDept(deptId);
    }

    public Student getStudentById(String id) {
        return studentDAO.findById(id);
    }

    public List<Student> searchStudents(String keyword) {
        return studentDAO.searchByName(keyword);
    }

    public int getTotalStudentCount() {
        return studentDAO.countAll();
    }

    // ── Update ────────────────────────────────────────────────────────────────

    public boolean updateProfile(Student student) {
        if (student.getName() == null || student.getName().isBlank()) return false;
        if (!AuthService.isValidEmail(student.getEmail()))             return false;
        return studentDAO.update(student);
    }

    public enum PasswordResult { SUCCESS, WRONG_CURRENT, INVALID_NEW, ERROR }

    public PasswordResult changePassword(String studentId, String currentPass, String newPass) {
        Student s = studentDAO.findById(studentId);
        if (s == null) {
            return PasswordResult.ERROR;
        }

        // Verify current password using hash
        if (!PasswordUtil.checkPassword(currentPass, s.getPassword())) {
            return PasswordResult.WRONG_CURRENT;
        }

        if (!AuthService.isValidPassword(newPass)) {
            return PasswordResult.INVALID_NEW;
        }

        String hashedNew = PasswordUtil.hashPassword(newPass);
        boolean ok = studentDAO.updatePassword(studentId, hashedNew);

        return ok ? PasswordResult.SUCCESS : PasswordResult.ERROR;
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    public boolean deleteStudent(String id) {
        return studentDAO.delete(id);
    }
}