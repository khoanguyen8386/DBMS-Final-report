package com.courseapp.dao;

import com.courseapp.db.DBConnection;
import com.courseapp.model.Schedule;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDAO {

    private static final String BASE_SELECT =
        "SELECT sc.*, c.code AS course_code, c.title AS course_title " +
        "FROM schedules sc " +
        "JOIN courses c ON sc.course_id = c.id ";

    // ── CREATE ────────────────────────────────────────────────────────────────

    public boolean insert(Schedule schedule) {
        String sql = "INSERT INTO schedules (course_id, day_of_week, start_time, end_time, room) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, schedule.getCourseId());
            stmt.setString(2, schedule.getDayOfWeek());
            stmt.setString(3, schedule.getStartTime());
            stmt.setString(4, schedule.getEndTime());
            stmt.setString(5, schedule.getRoom());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    public List<Schedule> findByCourse(int courseId) {
        List<Schedule> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE sc.course_id = ? " +
                     "ORDER BY FIELD(sc.day_of_week,'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'), sc.start_time";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Returns all schedule slots for courses a student is enrolled in */
    public List<Schedule> findByStudent(String studentId) {
        List<Schedule> list = new ArrayList<>();
        String sql = BASE_SELECT +
                     "JOIN registrations r ON sc.course_id = r.course_id " +
                     "WHERE r.student_id = ? AND r.status = 'enrolled' " +
                     "ORDER BY FIELD(sc.day_of_week,'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'), sc.start_time";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Returns all schedule slots for courses taught by an instructor */
    public List<Schedule> findByInstructor(int instructorId) {
        List<Schedule> list = new ArrayList<>();
        String sql = BASE_SELECT +
                     "WHERE c.instructor_id = ? " +
                     "ORDER BY FIELD(sc.day_of_week,'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'), sc.start_time";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    public boolean update(Schedule schedule) {
        String sql = "UPDATE schedules SET day_of_week=?, start_time=?, end_time=?, room=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, schedule.getDayOfWeek());
            stmt.setString(2, schedule.getStartTime());
            stmt.setString(3, schedule.getEndTime());
            stmt.setString(4, schedule.getRoom());
            stmt.setInt(5, schedule.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public boolean delete(int id) {
        String sql = "DELETE FROM schedules WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteByCourse(int courseId) {
        String sql = "DELETE FROM schedules WHERE course_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ── MAPPER ────────────────────────────────────────────────────────────────

    private Schedule mapRow(ResultSet rs) throws SQLException {
        Schedule s = new Schedule();
        s.setId(rs.getInt("id"));
        s.setCourseId(rs.getInt("course_id"));
        s.setDayOfWeek(rs.getString("day_of_week"));
        s.setStartTime(rs.getString("start_time"));
        s.setEndTime(rs.getString("end_time"));
        s.setRoom(rs.getString("room"));
        s.setCourseCode(rs.getString("course_code"));
        s.setCourseTitle(rs.getString("course_title"));
        return s;
    }
}