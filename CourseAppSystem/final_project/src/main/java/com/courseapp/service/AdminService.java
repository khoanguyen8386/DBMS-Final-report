package com.courseapp.service;

import com.courseapp.dao.DepartmentDAO;
import com.courseapp.dao.InstructorDAO;
import com.courseapp.dao.RegistrationDAO;
import com.courseapp.dao.StudentDAO;
import com.courseapp.model.Department;
import com.courseapp.model.Instructor;
import com.courseapp.model.Registration;
import java.util.List;

public class AdminService {

    private final DepartmentDAO   departmentDAO   = new DepartmentDAO();
    private final InstructorDAO   instructorDAO   = new InstructorDAO();
    private final StudentDAO      studentDAO      = new StudentDAO();
    private final RegistrationDAO registrationDAO = new RegistrationDAO();

    // ── Department management ─────────────────────────────────────────────────

    public List<Department> getAllDepartments() {
        return departmentDAO.findAll();
    }

    public Department getDepartmentById(int id) {
        return departmentDAO.findById(id);
    }

    public Department getDepartmentByCode(String code) {
        return departmentDAO.findByCode(code);
    }

    public boolean addDepartment(Department dept) {
        if (dept.getCode() == null || dept.getCode().isBlank()) return false;
        if (dept.getName() == null || dept.getName().isBlank()) return false;
        dept.setCode(dept.getCode().toUpperCase().trim());
        return departmentDAO.insert(dept);
    }

    public boolean updateDepartment(Department dept) {
        if (dept.getCode() == null || dept.getCode().isBlank()) return false;
        if (dept.getName() == null || dept.getName().isBlank()) return false;
        return departmentDAO.update(dept);
    }

    public boolean deleteDepartment(int id) {
        return departmentDAO.delete(id);
    }

    // ── Instructor management ─────────────────────────────────────────────────

    public List<Instructor> getAllInstructors() {
        return instructorDAO.findAll();
    }

    public boolean addInstructor(Instructor instructor) {
        if (instructor.getName()  == null || instructor.getName().isBlank())  return false;
        if (instructor.getEmail() == null || instructor.getEmail().isBlank())  return false;
        if (instructor.getPassword() == null || instructor.getPassword().isBlank()) return false;
        return instructorDAO.insert(instructor);
    }

    public boolean updateInstructor(Instructor instructor) {
        return instructorDAO.update(instructor);
    }

    public boolean deleteInstructor(int id) {
        return instructorDAO.delete(id);
    }

    // ── Student management ────────────────────────────────────────────────────

    public boolean deleteStudent(String id) {
        return studentDAO.delete(id);
    }

    // ── Override enrollment ───────────────────────────────────────────────────

    /**
     * Force-enroll a student ignoring capacity limits.
     * Used by admin to override the normal enrollment rules.
     */
    public boolean forceEnroll(String studentId, int courseId) {
        return registrationDAO.enroll(studentId, courseId);
    }

    /**
     * Force-drop a student from a course.
     */
    public boolean forceDrop(String studentId, int courseId) {
        return registrationDAO.drop(studentId, courseId);
    }

    /**
     * Override the registration status directly.
     * e.g. move from 'waitlisted' to 'enrolled'
     */
    public boolean overrideStatus(String studentId, int courseId, String newStatus) {
        return registrationDAO.updateStatus(studentId, courseId, newStatus);
    }

    // ── All registrations (for reports) ──────────────────────────────────────

    public List<Registration> getAllRegistrations() {
        return registrationDAO.findAll();
    }
}