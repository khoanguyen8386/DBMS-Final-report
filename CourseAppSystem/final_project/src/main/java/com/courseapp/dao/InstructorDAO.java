package com.courseapp.dao;

import com.courseapp.db.DBConnection;
import com.courseapp.model.Instructor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstructorDAO {

    public boolean insert(Instructor inst) {
        String sql = "INSERT INTO instructors (name, email, password, title, office, dept_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, inst.getName());
            stmt.setString(2, inst.getEmail());
            stmt.setString(3, inst.getPassword());
            stmt.setString(4, inst.getTitle());
            stmt.setString(5, inst.getOffice());
            stmt.setInt(6, inst.getDeptId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<Instructor> findAll() {
        List<Instructor> list = new ArrayList<>();
        String sql = "SELECT i.*, d.name AS dept_name FROM instructors i " +
                     "LEFT JOIN departments d ON i.dept_id = d.id ORDER BY i.name";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Instructor> findByDept(int deptId) {
        List<Instructor> list = new ArrayList<>();
        String sql = "SELECT i.*, d.name AS dept_name FROM instructors i " +
                     "LEFT JOIN departments d ON i.dept_id = d.id WHERE i.dept_id = ? ORDER BY i.name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, deptId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Instructor findById(int id) {
        String sql = "SELECT i.*, d.name AS dept_name FROM instructors i " +
                     "LEFT JOIN departments d ON i.dept_id = d.id WHERE i.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Fetch by email only — BCrypt check happens in AuthService */
    public Instructor findByEmail(String email) {
        String sql = "SELECT i.*, d.name AS dept_name FROM instructors i " +
                     "LEFT JOIN departments d ON i.dept_id = d.id WHERE i.email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Keep old method so nothing else breaks */
    public Instructor findByEmailAndPassword(String email, String password) {
        return findByEmail(email);
    }

    public boolean update(Instructor inst) {
        String sql = "UPDATE instructors SET name=?, email=?, title=?, office=?, dept_id=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, inst.getName());
            stmt.setString(2, inst.getEmail());
            stmt.setString(3, inst.getTitle());
            stmt.setString(4, inst.getOffice());
            stmt.setInt(5, inst.getDeptId());
            stmt.setInt(6, inst.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM instructors WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private Instructor mapRow(ResultSet rs) throws SQLException {
        Instructor i = new Instructor();
        i.setId(rs.getInt("id"));
        i.setName(rs.getString("name"));
        i.setEmail(rs.getString("email"));
        i.setPassword(rs.getString("password"));
        i.setTitle(rs.getString("title"));
        i.setOffice(rs.getString("office"));
        i.setDeptId(rs.getInt("dept_id"));
        i.setDeptName(rs.getString("dept_name"));
        return i;
    }
}
