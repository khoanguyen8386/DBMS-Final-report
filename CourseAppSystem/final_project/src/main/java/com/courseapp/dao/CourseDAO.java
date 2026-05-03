package com.courseapp.dao;

import com.courseapp.db.DBConnection;
import com.courseapp.model.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    private static final String BASE_SELECT =
        "SELECT c.*, d.name AS dept_name, i.name AS instructor_name " +
        "FROM courses c " +
        "LEFT JOIN departments d ON c.dept_id = d.id " +
        "LEFT JOIN instructors i ON c.instructor_id = i.id ";

    // ── CREATE ────────────────────────────────────────────────────────────────

    public boolean insert(Course course) {
        String sql = "INSERT INTO courses (code, title, credits, capacity, dept_id, instructor_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, course.getCode());
            stmt.setString(2, course.getTitle());
            stmt.setInt(3, course.getCredits());
            stmt.setInt(4, course.getCapacity());
            stmt.setInt(5, course.getDeptId());
            stmt.setInt(6, course.getInstructorId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    public List<Course> findAll() {
        List<Course> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY c.code";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Course findById(int id) {
        String sql = BASE_SELECT + "WHERE c.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Course> findByDept(int deptId) {
        List<Course> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE c.dept_id = ? ORDER BY c.code";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, deptId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Course> findByInstructor(int instructorId) {
        List<Course> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE c.instructor_id = ? ORDER BY c.code";
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

    /** Returns all courses a student is currently enrolled in */
    public List<Course> findByStudent(String studentId) {
        List<Course> list = new ArrayList<>();
        String sql = BASE_SELECT +
                     "JOIN registrations r ON c.id = r.course_id " +
                     "WHERE r.student_id = ? AND r.status = 'enrolled' " +
                     "ORDER BY c.code";
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

    /** Search courses by title or code keyword */
    public List<Course> search(String keyword) {
        List<Course> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE c.title LIKE ? OR c.code LIKE ? ORDER BY c.code";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            stmt.setString(1, kw);
            stmt.setString(2, kw);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    public boolean update(Course course) {
        String sql = "UPDATE courses SET code=?, title=?, credits=?, capacity=?, " +
                     "dept_id=?, instructor_id=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, course.getCode());
            stmt.setString(2, course.getTitle());
            stmt.setInt(3, course.getCredits());
            stmt.setInt(4, course.getCapacity());
            stmt.setInt(5, course.getDeptId());
            stmt.setInt(6, course.getInstructorId());
            stmt.setInt(7, course.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public boolean delete(int id) {
        String sql = "DELETE FROM courses WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ── STATS ─────────────────────────────────────────────────────────────────

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM courses";
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

    private Course mapRow(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setId(rs.getInt("id"));
        c.setCode(rs.getString("code"));
        c.setTitle(rs.getString("title"));
        c.setCredits(rs.getInt("credits"));
        c.setCapacity(rs.getInt("capacity"));
        c.setEnrolled(rs.getInt("enrolled"));
        c.setDeptId(rs.getInt("dept_id"));
        c.setInstructorId(rs.getInt("instructor_id"));
        c.setDeptName(rs.getString("dept_name"));
        c.setInstructorName(rs.getString("instructor_name"));
        return c;
    }
}