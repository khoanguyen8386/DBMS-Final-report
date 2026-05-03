package com.courseapp.dao;

import com.courseapp.db.DBConnection;
import com.courseapp.model.Registration;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegistrationDAO {

    private static final String BASE_SELECT =
        "SELECT r.*, s.name AS student_name, c.code AS course_code, c.title AS course_title " +
        "FROM registrations r " +
        "JOIN students s ON r.student_id = s.id " +
        "JOIN courses  c ON r.course_id  = c.id ";

    // ── ENROLL (transactional) ────────────────────────────────────────────────

    public boolean enroll(String studentId, int courseId) {
        String checkCapacity = "SELECT enrolled, capacity FROM courses WHERE id = ? FOR UPDATE";
        String insertReg     = "INSERT INTO registrations (student_id, course_id) VALUES (?, ?)";
        String incrementSeat = "UPDATE courses SET enrolled = enrolled + 1 WHERE id = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Check capacity
            PreparedStatement chk = conn.prepareStatement(checkCapacity);
            chk.setInt(1, courseId);
            ResultSet rs = chk.executeQuery();
            if (!rs.next() || rs.getInt("enrolled") >= rs.getInt("capacity")) {
                conn.rollback();
                return false; // course is full
            }

            // 2. Insert registration
            PreparedStatement ins = conn.prepareStatement(insertReg);
            ins.setString(1, studentId);
            ins.setInt(2, courseId);
            ins.executeUpdate();

            // 3. Increment enrolled count
            PreparedStatement upd = conn.prepareStatement(incrementSeat);
            upd.setInt(1, courseId);
            upd.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLIntegrityConstraintViolationException e) {
            // UNIQUE constraint — student already enrolled
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // ── DROP (transactional) ──────────────────────────────────────────────────

    public boolean drop(String studentId, int courseId) {
        String deleteReg     = "DELETE FROM registrations WHERE student_id = ? AND course_id = ?";
        String decrementSeat = "UPDATE courses SET enrolled = enrolled - 1 WHERE id = ? AND enrolled > 0";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement del = conn.prepareStatement(deleteReg);
            del.setString(1, studentId);
            del.setInt(2, courseId);
            int deleted = del.executeUpdate();

            if (deleted > 0) {
                PreparedStatement upd = conn.prepareStatement(decrementSeat);
                upd.setInt(1, courseId);
                upd.executeUpdate();
                conn.commit();
                return true;
            }

            conn.rollback();
            return false;

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    /** All registrations for a student */
    public List<Registration> findByStudent(String studentId) {
        List<Registration> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE r.student_id = ? ORDER BY c.code";
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

    /** All students enrolled in a course (roster) */
    public List<Registration> findByCourse(int courseId) {
        List<Registration> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE r.course_id = ? ORDER BY s.name";
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

    /** All registrations in the system (admin view) */
    public List<Registration> findAll() {
        List<Registration> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY r.registered_at DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean isEnrolled(String studentId, int courseId) {
        String sql = "SELECT COUNT(*) FROM registrations WHERE student_id = ? AND course_id = ? AND status = 'enrolled'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.setInt(2, courseId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    /** Instructor assigns a grade */
    public boolean updateGrade(String studentId, int courseId, String grade) {
        String sql = "UPDATE registrations SET grade=?, status='completed' WHERE student_id=? AND course_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, grade);
            stmt.setString(2, studentId);
            stmt.setInt(3, courseId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Admin override — force status change */
    public boolean updateStatus(String studentId, int courseId, String status) {
        String sql = "UPDATE registrations SET status=? WHERE student_id=? AND course_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, studentId);
            stmt.setInt(3, courseId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ── STATS ─────────────────────────────────────────────────────────────────

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM registrations WHERE status = 'enrolled'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ── MAPPER ────────────────────────────────────────────────────────────────

    private Registration mapRow(ResultSet rs) throws SQLException {
        Registration r = new Registration();
        r.setId(rs.getInt("id"));
        r.setStudentId(rs.getString("student_id"));
        r.setCourseId(rs.getInt("course_id"));
        r.setStatus(rs.getString("status"));
        r.setGrade(rs.getString("grade"));
        r.setRegisteredAt(rs.getTimestamp("registered_at"));
        r.setStudentName(rs.getString("student_name"));
        r.setCourseCode(rs.getString("course_code"));
        r.setCourseTitle(rs.getString("course_title"));
        return r;
    }
}