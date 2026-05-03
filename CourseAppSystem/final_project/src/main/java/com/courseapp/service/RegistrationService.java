package com.courseapp.service;

import com.courseapp.dao.CourseDAO;
import com.courseapp.dao.RegistrationDAO;
import com.courseapp.model.Course;
import com.courseapp.model.Registration;
import java.util.List;

public class RegistrationService {

    private final RegistrationDAO registrationDAO = new RegistrationDAO();
    private final CourseDAO       courseDAO       = new CourseDAO();

    // ── Enroll ────────────────────────────────────────────────────────────────

    public enum EnrollResult {
        SUCCESS,
        ALREADY_ENROLLED,
        COURSE_FULL,
        COURSE_NOT_FOUND,
        ERROR
    }

    public EnrollResult enroll(String studentId, int courseId) {
        // Check course exists
        Course course = courseDAO.findById(courseId);
        if (course == null) return EnrollResult.COURSE_NOT_FOUND;

        // Check already enrolled
        if (registrationDAO.isEnrolled(studentId, courseId)) {
            return EnrollResult.ALREADY_ENROLLED;
        }

        // Check capacity
        if (course.isFull()) return EnrollResult.COURSE_FULL;

        // Attempt enroll (DAO handles transaction + seat increment)
        boolean ok = registrationDAO.enroll(studentId, courseId);
        return ok ? EnrollResult.SUCCESS : EnrollResult.ERROR;
    }

    // ── Drop ──────────────────────────────────────────────────────────────────

    public enum DropResult {
        SUCCESS,
        NOT_ENROLLED,
        ERROR
    }

    public DropResult drop(String studentId, int courseId) {
        if (!registrationDAO.isEnrolled(studentId, courseId)) {
            return DropResult.NOT_ENROLLED;
        }
        boolean ok = registrationDAO.drop(studentId, courseId);
        return ok ? DropResult.SUCCESS : DropResult.ERROR;
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    /** Student: view my registered courses */
    public List<Registration> getRegistrationsByStudent(String studentId) {
        return registrationDAO.findByStudent(studentId);
    }

    /** Instructor: view class roster for a course */
    public List<Registration> getRosterByCourse(int courseId) {
        return registrationDAO.findByCourse(courseId);
    }

    /** Admin: view all registrations */
    public List<Registration> getAllRegistrations() {
        return registrationDAO.findAll();
    }

    public boolean isEnrolled(String studentId, int courseId) {
        return registrationDAO.isEnrolled(studentId, courseId);
    }

    public int getTotalEnrollmentCount() {
        return registrationDAO.countAll();
    }

    // ── Grade ─────────────────────────────────────────────────────────────────

    public enum GradeResult {
        SUCCESS,
        INVALID_GRADE,
        NOT_FOUND,
        ERROR
    }

    private static final java.util.Set<String> VALID_GRADES =
        java.util.Set.of("A+", "A", "B+", "B", "C+", "C", "D+", "D", "F", "I", "W");

    public GradeResult assignGrade(String studentId, int courseId, String grade) {
        if (grade == null || !VALID_GRADES.contains(grade.toUpperCase())) {
            return GradeResult.INVALID_GRADE;
        }
        if (!registrationDAO.isEnrolled(studentId, courseId)) {
            return GradeResult.NOT_FOUND;
        }
        boolean ok = registrationDAO.updateGrade(studentId, courseId, grade.toUpperCase());
        return ok ? GradeResult.SUCCESS : GradeResult.ERROR;
    }

    // ── Admin override ────────────────────────────────────────────────────────

    public boolean overrideStatus(String studentId, int courseId, String status) {
        return registrationDAO.updateStatus(studentId, courseId, status);
    }
}