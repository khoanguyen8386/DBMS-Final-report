package com.courseapp.dao;

import com.courseapp.db.DBConnection;
import com.courseapp.model.Department;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {

    // ── CREATE ────────────────────────────────────────────────────────────────

    public boolean insert(Department dept) {
        String sql = "INSERT INTO departments (code, name, faculty, office, phone) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dept.getCode());
            stmt.setString(2, dept.getName());
            stmt.setString(3, dept.getFaculty());
            stmt.setString(4, dept.getOffice());
            stmt.setString(5, dept.getPhone());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    public List<Department> findAll() {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT * FROM departments ORDER BY code";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Department findById(int id) {
        String sql = "SELECT * FROM departments WHERE id = ?";
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

    public Department findByCode(String code) {
        String sql = "SELECT * FROM departments WHERE code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    public boolean update(Department dept) {
        String sql = "UPDATE departments SET code=?, name=?, faculty=?, office=?, phone=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dept.getCode());
            stmt.setString(2, dept.getName());
            stmt.setString(3, dept.getFaculty());
            stmt.setString(4, dept.getOffice());
            stmt.setString(5, dept.getPhone());
            stmt.setInt(6, dept.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public boolean delete(int id) {
        String sql = "DELETE FROM departments WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ── MAPPER ────────────────────────────────────────────────────────────────

    private Department mapRow(ResultSet rs) throws SQLException {
        Department d = new Department();
        d.setId(rs.getInt("id"));
        d.setCode(rs.getString("code"));
        d.setName(rs.getString("name"));
        d.setFaculty(rs.getString("faculty"));
        d.setOffice(rs.getString("office"));
        d.setPhone(rs.getString("phone"));
        return d;
    }
} 