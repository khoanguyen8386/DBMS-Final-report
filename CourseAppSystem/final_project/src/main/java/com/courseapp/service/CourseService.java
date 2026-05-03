package com.courseapp.service;

import com.courseapp.dao.CourseDAO;
import com.courseapp.model.Course;
import java.util.List;
import java.util.stream.Collectors;

public class CourseService {

    private final CourseDAO courseDAO = new CourseDAO();

    // ── Read ──────────────────────────────────────────────────────────────────

    public List<Course> getAllCourses() {
        return courseDAO.findAll();
    }

    public Course getCourseById(int id) {
        return courseDAO.findById(id);
    }

    public List<Course> getCoursesByDept(int deptId) {
        return courseDAO.findByDept(deptId);
    }

    public List<Course> getCoursesByInstructor(int instructorId) {
        return courseDAO.findByInstructor(instructorId);
    }

    /** Returns courses a student is currently enrolled in */
    public List<Course> getCoursesByStudent(String studentId) {
        return courseDAO.findByStudent(studentId);
    }

    public List<Course> searchCourses(String keyword) {
        if (keyword == null || keyword.isBlank()) return getAllCourses();
        return courseDAO.search(keyword.trim());
    }

    /** Filter by dept and/or keyword — either can be null */
    public List<Course> filterCourses(String keyword, Integer deptId) {
        List<Course> all = (keyword != null && !keyword.isBlank())
            ? courseDAO.search(keyword.trim())
            : courseDAO.findAll();

        if (deptId != null && deptId > 0) {
            all = all.stream()
                     .filter(c -> c.getDeptId() == deptId)
                     .collect(Collectors.toList());
        }
        return all;
    }

    public int getTotalCourseCount() {
        return courseDAO.countAll();
    }

    // ── Create / Update / Delete (Admin) ─────────────────────────────────────

    public enum CourseResult { SUCCESS, DUPLICATE_CODE, INVALID_DATA, ERROR }

    public CourseResult addCourse(Course course) {
        if (!isValidCourse(course)) return CourseResult.INVALID_DATA;
        boolean ok = courseDAO.insert(course);
        return ok ? CourseResult.SUCCESS : CourseResult.DUPLICATE_CODE;
    }

    public CourseResult updateCourse(Course course) {
        if (!isValidCourse(course)) return CourseResult.INVALID_DATA;
        boolean ok = courseDAO.update(course);
        return ok ? CourseResult.SUCCESS : CourseResult.ERROR;
    }

    public boolean deleteCourse(int id) {
        return courseDAO.delete(id);
    }

    // ── Validation ────────────────────────────────────────────────────────────

    private boolean isValidCourse(Course c) {
        return c != null
            && c.getCode()  != null && !c.getCode().isBlank()
            && c.getTitle() != null && !c.getTitle().isBlank()
            && c.getCredits()  > 0
            && c.getCapacity() > 0
            && c.getDeptId()   > 0
            && c.getInstructorId() > 0;
    }
}