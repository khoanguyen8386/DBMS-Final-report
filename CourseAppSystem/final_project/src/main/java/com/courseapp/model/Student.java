package com.courseapp.model;

import java.sql.Timestamp;

public class Student {

    private String    id;          // e.g. "CS21001"
    private String    name;
    private String    email;
    private String    password;
    private String    phone;
    private int       deptId;
    private int       enrollYear;
    private Timestamp enrolledAt;

    // used for display — joined from departments table
    private String deptName;
    private String deptCode;

    public Student() {}

    public Student(String id, String name, String email) {
        this.id    = id;
        this.name  = name;
        this.email = email;
    }

    // ── Getters ──────────────────────────────────────────────
    public String    getId()         { return id; }
    public String    getName()       { return name; }
    public String    getEmail()      { return email; }
    public String    getPassword()   { return password; }
    public String    getPhone()      { return phone; }
    public int       getDeptId()     { return deptId; }
    public int       getEnrollYear() { return enrollYear; }
    public Timestamp getEnrolledAt() { return enrolledAt; }
    public String    getDeptName()   { return deptName; }
    public String    getDeptCode()   { return deptCode; }

    // ── Setters ──────────────────────────────────────────────
    public void setId(String id)               { this.id = id; }
    public void setName(String name)           { this.name = name; }
    public void setEmail(String email)         { this.email = email; }
    public void setPassword(String password)   { this.password = password; }
    public void setPhone(String phone)         { this.phone = phone; }
    public void setDeptId(int deptId)          { this.deptId = deptId; }
    public void setEnrollYear(int enrollYear)  { this.enrollYear = enrollYear; }
    public void setEnrolledAt(Timestamp t)     { this.enrolledAt = t; }
    public void setDeptName(String deptName)   { this.deptName = deptName; }
    public void setDeptCode(String deptCode)   { this.deptCode = deptCode; }

    // ── Utility ──────────────────────────────────────────────
    /**
     * Decodes the enroll year from the student ID.
     * e.g. "CS21001" → 2021, "MATH22071" → 2022
     */
    public static int parseYearFromId(String id) {
        // Find the first digit in the ID string
        for (int i = 0; i < id.length(); i++) {
            if (Character.isDigit(id.charAt(i))) {
                String yy = id.substring(i, i + 2);
                return 2000 + Integer.parseInt(yy);
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return id + " — " + name;
    }
}