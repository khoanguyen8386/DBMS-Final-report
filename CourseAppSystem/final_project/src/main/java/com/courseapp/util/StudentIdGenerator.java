package com.courseapp.util;

import com.courseapp.db.DBConnection;
import java.sql.*;
import java.util.Random;

/**
 * Generates unique student IDs in the format:
 *   DEPT_CODE + last2DigitsOfYear + 3randomDigits
 *
 * Examples:
 *   CS21224   → Computer Science, enrolled 2021
 *   MATH23087 → Mathematics, enrolled 2023
 *   BUS22451  → Business, enrolled 2022
 */
public class StudentIdGenerator {

    private static final Random RND = new Random();

    /**
     * Generates a guaranteed-unique student ID.
     *
     * @param deptCode   Department code from departments.code (e.g. "CS")
     * @param enrollYear Full 4-digit year (e.g. 2024)
     * @return           Unique student ID string (e.g. "CS24738")
     */
    public static String generate(String deptCode, int enrollYear) throws SQLException {
        String yearPart = String.valueOf(enrollYear).substring(2); // "2024" → "24"
        String candidate;

        do {
            int digits = 100 + RND.nextInt(900); // always 3 digits: 100–999
            candidate = deptCode.toUpperCase() + yearPart + digits;
        } while (alreadyExists(candidate));

        return candidate;
    }

    /**
     * Checks whether a student ID already exists in the database.
     */
    private static boolean alreadyExists(String id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM students WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    /**
     * Decodes the enroll year from an existing student ID.
     * e.g. "CS21001"   → 2021
     *      "MATH22071" → 2022
     */
    public static int decodeYear(String studentId) {
        for (int i = 0; i < studentId.length(); i++) {
            if (Character.isDigit(studentId.charAt(i))) {
                int yy = Integer.parseInt(studentId.substring(i, i + 2));
                return 2000 + yy;
            }
        }
        throw new IllegalArgumentException("Cannot decode year from ID: " + studentId);
    }

    /**
     * Decodes the department code from an existing student ID.
     * e.g. "CS21001"   → "CS"
     *      "MATH22071" → "MATH"
     */
    public static String decodeDeptCode(String studentId) {
        StringBuilder sb = new StringBuilder();
        for (char c : studentId.toCharArray()) {
            if (Character.isLetter(c)) sb.append(c);
            else break;
        }
        return sb.toString();
    }
}