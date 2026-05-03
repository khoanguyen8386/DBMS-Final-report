package com.courseapp.dao;

import com.courseapp.db.DBConnection;
import com.courseapp.model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // ── CREATE ────────────────────────────────────────────────────────────────

    public boolean insert(Student student) {
        String sql = "INSERT INTO students (id, name, email, password, phone, dept_id, enroll_year) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getId());
            stmt.setString(2, student.getName());
            stmt.setString(3, student.getEmail());
            stmt.setString(4, student.getPassword());
            stmt.setString(5, student.getPhone());
            stmt.setInt(6, student.getDeptId());
            stmt.setInt(7, student.getEnrollYear());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    public List<Student> findAll() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.*, d.name AS dept_name, d.code AS dept_code " +
                     "FROM students s " +
                     "LEFT JOIN departments d ON s.dept_id = d.id " +
                     "ORDER BY s.id";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Student> findByDept(int deptId) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.*, d.name AS dept_name, d.code AS dept_code " +
                     "FROM students s " +
                     "LEFT JOIN departments d ON s.dept_id = d.id " +
                     "WHERE s.dept_id = ? ORDER BY s.id";
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

    public Student findById(String id) {
        String sql = "SELECT s.*, d.name AS dept_name, d.code AS dept_code " +
                     "FROM students s " +
                     "LEFT JOIN departments d ON s.dept_id = d.id " +
                     "WHERE s.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Fetch by email only — BCrypt check happens in AuthService */
    public Student findByEmail(String email) {
        String sql = "SELECT s.*, d.name AS dept_name, d.code AS dept_code " +
                     "FROM students s " +
                     "LEFT JOIN departments d ON s.dept_id = d.id " +
                     "WHERE s.email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Keep old method so nothing else breaks */
    public Student findByEmailAndPassword(String email, String password) {
        return findByEmail(email);
    }

    public List<Student> searchByName(String keyword) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.*, d.name AS dept_name, d.code AS dept_code " +
                     "FROM students s " +
                     "LEFT JOIN departments d ON s.dept_id = d.id " +
                     "WHERE s.name LIKE ? OR s.id LIKE ? ORDER BY s.id";
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

    public boolean update(Student student) {
        String sql = "UPDATE students SET name=?, email=?, phone=?, dept_id=?, enroll_year=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getEmail());
            stmt.setString(3, student.getPhone());
            stmt.setInt(4, student.getDeptId());
            stmt.setInt(5, student.getEnrollYear());
            stmt.setString(6, student.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePassword(String id, String newPassword) {
        String sql = "UPDATE students SET password=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public boolean delete(String id) {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ── STATS ─────────────────────────────────────────────────────────────────

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM students";
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

    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getString("id"));
        s.setName(rs.getString("name"));
        s.setEmail(rs.getString("email"));
        s.setPassword(rs.getString("password"));
        s.setPhone(rs.getString("phone"));
        s.setDeptId(rs.getInt("dept_id"));
        s.setEnrollYear(rs.getInt("enroll_year"));
        s.setEnrolledAt(rs.getTimestamp("enrolled_at"));
        s.setDeptName(rs.getString("dept_name"));
        s.setDeptCode(rs.getString("dept_code"));
        return s;
    }
}