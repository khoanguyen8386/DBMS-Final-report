package com.courseapp.model;

public class Course {

    private int    id;
    private String code;
    private String title;
    private int    credits;
    private int    capacity;
    private int    enrolled;
    private int    deptId;
    private int    instructorId;

    // joined display fields
    private String deptName;
    private String instructorName;

    public Course() {}

    public Course(int id, String code, String title) {
        this.id    = id;
        this.code  = code;
        this.title = title;
    }

    // ── Getters ──────────────────────────────────────────────
    public int    getId()             { return id; }
    public String getCode()           { return code; }
    public String getTitle()          { return title; }
    public int    getCredits()        { return credits; }
    public int    getCapacity()       { return capacity; }
    public int    getEnrolled()       { return enrolled; }
    public int    getDeptId()         { return deptId; }
    public int    getInstructorId()   { return instructorId; }
    public String getDeptName()       { return deptName; }
    public String getInstructorName() { return instructorName; }

    // ── Setters ──────────────────────────────────────────────
    public void setId(int id)                      { this.id = id; }
    public void setCode(String code)               { this.code = code; }
    public void setTitle(String title)             { this.title = title; }
    public void setCredits(int credits)            { this.credits = credits; }
    public void setCapacity(int capacity)          { this.capacity = capacity; }
    public void setEnrolled(int enrolled)          { this.enrolled = enrolled; }
    public void setDeptId(int deptId)              { this.deptId = deptId; }
    public void setInstructorId(int instructorId)  { this.instructorId = instructorId; }
    public void setDeptName(String deptName)       { this.deptName = deptName; }
    public void setInstructorName(String name)     { this.instructorName = name; }

    // ── Utility ──────────────────────────────────────────────
    public boolean isFull() {
        return enrolled >= capacity;
    }

    public int getAvailableSeats() {
        return capacity - enrolled;
    }

    @Override
    public String toString() {
        return code + " — " + title;
    }
}