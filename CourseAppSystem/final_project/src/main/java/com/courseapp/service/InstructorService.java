package com.courseapp.service;

import com.courseapp.dao.CourseDAO;
import com.courseapp.dao.InstructorDAO;
import com.courseapp.dao.RegistrationDAO;
import com.courseapp.dao.ScheduleDAO;
import com.courseapp.model.Course;
import com.courseapp.model.Instructor;
import com.courseapp.model.Registration;
import com.courseapp.model.Schedule;
import java.util.List;

public class InstructorService {

    private final InstructorDAO    instructorDAO    = new InstructorDAO();
    private final CourseDAO        courseDAO        = new CourseDAO();
    private final RegistrationDAO  registrationDAO  = new RegistrationDAO();
    private final ScheduleDAO      scheduleDAO      = new ScheduleDAO();

    // ── Read ──────────────────────────────────────────────────────────────────

    public List<Instructor> getAllInstructors() {
        return instructorDAO.findAll();
    }

    public List<Instructor> getInstructorsByDept(int deptId) {
        return instructorDAO.findByDept(deptId);
    }

    public Instructor getInstructorById(int id) {
        return instructorDAO.findById(id);
    }

    /** Courses this instructor teaches */
    public List<Course> getMyCourses(int instructorId) {
        return courseDAO.findByInstructor(instructorId);
    }

    /** Class roster — students enrolled in a specific course */
    public List<Registration> getRoster(int courseId) {
        return registrationDAO.findByCourse(courseId);
    }

    /** Full weekly schedule for all courses taught by this instructor */
    public List<Schedule> getMySchedule(int instructorId) {
        return scheduleDAO.findByInstructor(instructorId);
    }

    /** Schedule slots for a specific course */
    public List<Schedule> getCourseSchedule(int courseId) {
        return scheduleDAO.findByCourse(courseId);
    }

    // ── Grade ─────────────────────────────────────────────────────────────────

    public RegistrationService.GradeResult assignGrade(String studentId, int courseId, String grade) {
        RegistrationService svc = new RegistrationService();
        return svc.assignGrade(studentId, courseId, grade);
    }

    // ── Profile update ────────────────────────────────────────────────────────

    public boolean updateProfile(Instructor instructor) {
        if (instructor.getName() == null || instructor.getName().isBlank()) return false;
        if (!AuthService.isValidEmail(instructor.getEmail()))               return false;
        return instructorDAO.update(instructor);
    }

    // ── Schedule management ───────────────────────────────────────────────────

    public boolean addScheduleSlot(Schedule schedule) {
        if (!isValidSchedule(schedule)) return false;
        return scheduleDAO.insert(schedule);
    }

    public boolean updateScheduleSlot(Schedule schedule) {
        if (!isValidSchedule(schedule)) return false;
        return scheduleDAO.update(schedule);
    }

    public boolean deleteScheduleSlot(int scheduleId) {
        return scheduleDAO.delete(scheduleId);
    }

    // ── Admin CRUD for instructors ────────────────────────────────────────────

    public boolean addInstructor(Instructor instructor) {
        if (instructor.getName() == null || instructor.getName().isBlank()) return false;
        if (!AuthService.isValidEmail(instructor.getEmail()))               return false;
        return instructorDAO.insert(instructor);
    }

    public boolean deleteInstructor(int id) {
        return instructorDAO.delete(id);
    }

    // ── Validation ────────────────────────────────────────────────────────────

    private boolean isValidSchedule(Schedule s) {
        return s != null
            && s.getCourseId() > 0
            && s.getDayOfWeek() != null && !s.getDayOfWeek().isBlank()
            && s.getStartTime() != null && !s.getStartTime().isBlank()
            && s.getEndTime()   != null && !s.getEndTime().isBlank()
            && s.getRoom()      != null && !s.getRoom().isBlank();
    }
}