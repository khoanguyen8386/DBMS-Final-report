package com.courseapp.service;

import com.courseapp.dao.CourseDAO;
import com.courseapp.dao.RegistrationDAO;
import com.courseapp.dao.StudentDAO;
import com.courseapp.model.Course;
import com.courseapp.model.Registration;
import java.util.*;
import java.util.stream.Collectors;

public class ReportService {

    private final StudentDAO      studentDAO      = new StudentDAO();
    private final CourseDAO       courseDAO       = new CourseDAO();
    private final RegistrationDAO registrationDAO = new RegistrationDAO();

    // ── Summary stats ─────────────────────────────────────────────────────────

    public int getTotalStudents()       { return studentDAO.countAll(); }
    public int getTotalCourses()        { return courseDAO.countAll(); }
    public int getTotalEnrollments()    { return registrationDAO.countAll(); }

    // ── Course fill rate ──────────────────────────────────────────────────────

    public static class CourseFillRate {
        public final String courseCode;
        public final String courseTitle;
        public final int    capacity;
        public final int    enrolled;
        public final double fillPercent;

        public CourseFillRate(Course c) {
            this.courseCode   = c.getCode();
            this.courseTitle  = c.getTitle();
            this.capacity     = c.getCapacity();
            this.enrolled     = c.getEnrolled();
            this.fillPercent  = capacity > 0 ? (enrolled * 100.0 / capacity) : 0;
        }

        public String getFillBar() {
            int bars = (int) (fillPercent / 10);
            return "█".repeat(bars) + "░".repeat(10 - bars) +
                   String.format(" %.0f%%", fillPercent);
        }
    }

    public List<CourseFillRate> getCourseFillRates() {
        return courseDAO.findAll()
                        .stream()
                        .map(CourseFillRate::new)
                        .sorted(Comparator.comparingDouble(r -> -r.fillPercent))
                        .collect(Collectors.toList());
    }

    // ── Per-department summary ────────────────────────────────────────────────

    public static class DeptSummary {
        public final String deptName;
        public int courseCount;
        public int studentCount;
        public int totalEnrolled;

        public DeptSummary(String deptName) {
            this.deptName = deptName;
        }
    }

    public List<DeptSummary> getDeptSummaries() {
        Map<String, DeptSummary> map = new LinkedHashMap<>();

        // Count courses per dept
        courseDAO.findAll().forEach(c -> {
            String dept = c.getDeptName() != null ? c.getDeptName() : "Unknown";
            map.computeIfAbsent(dept, DeptSummary::new).courseCount++;
            map.get(dept).totalEnrolled += c.getEnrolled();
        });

        // Count students per dept
        studentDAO.findAll().forEach(s -> {
            String dept = s.getDeptName() != null ? s.getDeptName() : "Unknown";
            map.computeIfAbsent(dept, DeptSummary::new).studentCount++;
        });

        return new ArrayList<>(map.values());
    }

    // ── Recent registrations ──────────────────────────────────────────────────

    /**
     * Returns the most recent N registrations, for a live activity feed.
     */
    public List<Registration> getRecentRegistrations(int limit) {
        List<Registration> all = registrationDAO.findAll();
        return all.subList(0, Math.min(limit, all.size()));
    }

    // ── Student transcript ────────────────────────────────────────────────────

    /**
     * Returns all registrations for a student, sorted by course code.
     * Used to display a student's full academic record.
     */
    public List<Registration> getTranscript(String studentId) {
        return registrationDAO.findByStudent(studentId)
                              .stream()
                              .sorted(Comparator.comparing(Registration::getCourseCode))
                              .collect(Collectors.toList());
    }

    // ── GPA calculation ───────────────────────────────────────────────────────

    private static final Map<String, Double> GRADE_POINTS = Map.of(
        "A+", 4.0, "A", 4.0,
        "B+", 3.5, "B", 3.0,
        "C+", 2.5, "C", 2.0,
        "D+", 1.5, "D", 1.0,
        "F",  0.0
    );

    /**
     * Calculates GPA for a student from their completed registrations.
     */
    public double calculateGPA(String studentId) {
        List<Registration> regs = registrationDAO.findByStudent(studentId);

        double totalPoints = 0;
        int    graded      = 0;

        for (Registration r : regs) {
            if (r.getGrade() != null && GRADE_POINTS.containsKey(r.getGrade())) {
                totalPoints += GRADE_POINTS.get(r.getGrade());
                graded++;
            }
        }

        return graded > 0 ? Math.round((totalPoints / graded) * 100.0) / 100.0 : 0.0;
    }
}