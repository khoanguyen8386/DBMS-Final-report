// Run once: mvn exec:java -Dexec.mainClass="com.courseapp.util.HashAllPasswords"
package com.courseapp.util;

import com.courseapp.db.DBConnection;
import java.sql.*;

public class HashAllPasswords {
    public static void main(String[] args) throws Exception {
        Connection conn = DBConnection.getConnection();

        // Hash all plain passwords that don't start with $2a$ (BCrypt prefix)
        for (String table : new String[]{"students", "instructors", "admins"}) {
            ResultSet rs = conn.createStatement()
                .executeQuery("SELECT id, password FROM " + table + " WHERE password NOT LIKE '$2a$%'");
            while (rs.next()) {
                String id   = rs.getString("id") != null ? rs.getString("id") : String.valueOf(rs.getInt("id"));
                String hash = PasswordUtil.hashPassword(rs.getString("password"));
                PreparedStatement upd = conn.prepareStatement(
                    "UPDATE " + table + " SET password = ? WHERE id = ?");
                upd.setString(1, hash);
                upd.setString(2, id);
                upd.executeUpdate();
                System.out.println("Hashed password for " + table + " id=" + id);
            }
        }
        System.out.println("Done.");
        conn.close();
    }
}