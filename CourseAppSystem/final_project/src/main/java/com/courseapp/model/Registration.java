package com.courseapp.model;

import java.sql.Timestamp;

public class Registration {

    public enum Status {
        enrolled, waitlisted, dropped, completed
    }

    private int       id;
    private String    studentId;   // VARCHAR — e.g. "CS21001"
    private int       courseId;
    private Status    status;
    private String    grade;
    private Timestamp registeredAt;

    // joined display fields
    private String studentName;
    private String courseCode;
    private String courseTitle;

    public Registration() {}

    public Registration(String studentId, int courseId) {
        this.studentId = studentId;
        this.courseId  = courseId;
        this.status    = Status.enrolled;
    }

    // ── Getters ──────────────────────────────────────────────
    public int       getId()            { return id; }
    public String    getStudentId()     { return studentId; }
    public int       getCourseId()      { return courseId; }
    public Status    getStatus()        { return status; }
    public String    getGrade()         { return grade; }
    public Timestamp getRegisteredAt()  { return registeredAt; }
    public String    getStudentName()   { return studentName; }
    public String    getCourseCode()    { return courseCode; }
    public String    getCourseTitle()   { return courseTitle; }

    // ── Setters ──────────────────────────────────────────────
    public void setId(int id)                    { this.id = id; }
    public void setStudentId(String studentId)   { this.studentId = studentId; }
    public void setCourseId(int courseId)        { this.courseId = courseId; }
    public void setStatus(Status status)         { this.status = status; }
    public void setStatus(String status)         { this.status = Status.valueOf(status); }
    public void setGrade(String grade)           { this.grade = grade; }
    public void setRegisteredAt(Timestamp t)     { this.registeredAt = t; }
    public void setStudentName(String name)      { this.studentName = name; }
    public void setCourseCode(String code)       { this.courseCode = code; }
    public void setCourseTitle(String title)     { this.courseTitle = title; }

    // ── Utility ──────────────────────────────────────────────
    public boolean isActive() {
        return status == Status.enrolled || status == Status.waitlisted;
    }

    public String getStatusDisplay() {
    return switch (status) {
        case enrolled   -> "Enrolled";
        case waitlisted -> "Waitlisted";
        case dropped    -> "Dropped";
        case completed  -> "Completed";
        };
    }
}