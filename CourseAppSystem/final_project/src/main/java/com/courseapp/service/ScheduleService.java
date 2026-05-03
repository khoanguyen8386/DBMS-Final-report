package com.courseapp.service;

import com.courseapp.dao.ScheduleDAO;
import com.courseapp.model.Schedule;
import java.util.*;
import java.util.stream.Collectors;

public class ScheduleService {

    private final ScheduleDAO scheduleDAO = new ScheduleDAO();

    private static final List<String> DAY_ORDER = List.of(
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    );

    // ── Student schedule ──────────────────────────────────────────────────────

    /** Returns all schedule slots for a student, grouped by day */
    public Map<String, List<Schedule>> getStudentScheduleByDay(String studentId) {
        List<Schedule> slots = scheduleDAO.findByStudent(studentId);
        return groupByDay(slots);
    }

    /** Returns flat list — used for table display */
    public List<Schedule> getStudentSchedule(String studentId) {
        return scheduleDAO.findByStudent(studentId);
    }

    // ── Instructor schedule ───────────────────────────────────────────────────

    public Map<String, List<Schedule>> getInstructorScheduleByDay(int instructorId) {
        List<Schedule> slots = scheduleDAO.findByInstructor(instructorId);
        return groupByDay(slots);
    }

    public List<Schedule> getInstructorSchedule(int instructorId) {
        return scheduleDAO.findByInstructor(instructorId);
    }

    // ── Course schedule ───────────────────────────────────────────────────────

    public List<Schedule> getCourseSchedule(int courseId) {
        return scheduleDAO.findByCourse(courseId);
    }

    // ── Manage schedule slots ─────────────────────────────────────────────────

    public boolean addSlot(Schedule schedule) {
        return scheduleDAO.insert(schedule);
    }

    public boolean updateSlot(Schedule schedule) {
        return scheduleDAO.update(schedule);
    }

    public boolean deleteSlot(int scheduleId) {
        return scheduleDAO.delete(scheduleId);
    }

    public boolean deleteAllSlotsForCourse(int courseId) {
        return scheduleDAO.deleteByCourse(courseId);
    }

    // ── Conflict detection ────────────────────────────────────────────────────

    /**
     * Checks if two time slots overlap on the same day.
     * Used before enrolling to detect schedule conflicts.
     * Times are in "HH:mm" format.
     */
    public boolean hasConflict(String studentId, int newCourseId) {
        List<Schedule> existing = scheduleDAO.findByStudent(studentId);
        List<Schedule> newSlots = scheduleDAO.findByCourse(newCourseId);

        for (Schedule ex : existing) {
            for (Schedule nw : newSlots) {
                if (ex.getDayOfWeek().equals(nw.getDayOfWeek())
                        && timesOverlap(ex.getStartTime(), ex.getEndTime(),
                                        nw.getStartTime(), nw.getEndTime())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean timesOverlap(String s1, String e1, String s2, String e2) {
        return toMinutes(s1) < toMinutes(e2) && toMinutes(s2) < toMinutes(e1);
    }

    private int toMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Map<String, List<Schedule>> groupByDay(List<Schedule> slots) {
        Map<String, List<Schedule>> grouped = new LinkedHashMap<>();

        // Initialize in correct weekday order
        DAY_ORDER.forEach(day -> grouped.put(day, new ArrayList<>()));

        slots.forEach(s -> grouped
            .computeIfAbsent(s.getDayOfWeek(), k -> new ArrayList<>())
            .add(s));

        // Remove empty days
        grouped.entrySet().removeIf(e -> e.getValue().isEmpty());
        return grouped;
    }
}