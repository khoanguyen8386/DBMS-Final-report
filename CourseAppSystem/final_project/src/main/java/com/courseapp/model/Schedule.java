package com.courseapp.model;

public class Schedule {

    private int    id;
    private int    courseId;
    private String dayOfWeek;   // e.g. "Thứ Hai", "Thứ Ba"
    private String startTime;   // stored as String for easy display "07:30"
    private String endTime;
    private String room;

    // joined display field
    private String courseCode;
    private String courseTitle;

    public Schedule() {}

    public Schedule(int courseId, String dayOfWeek, String startTime, String endTime, String room) {
        this.courseId   = courseId;
        this.dayOfWeek  = dayOfWeek;
        this.startTime  = startTime;
        this.endTime    = endTime;
        this.room       = room;
    }

    // ── Getters ──────────────────────────────────────────────
    public int    getId()           { return id; }
    public int    getCourseId()     { return courseId; }
    public String getDayOfWeek()    { return dayOfWeek; }
    public String getStartTime()    { return startTime; }
    public String getEndTime()      { return endTime; }
    public String getRoom()         { return room; }
    public String getCourseCode()   { return courseCode; }
    public String getCourseTitle()  { return courseTitle; }

    // ── Setters ──────────────────────────────────────────────
    public void setId(int id)                  { this.id = id; }
    public void setCourseId(int courseId)      { this.courseId = courseId; }
    public void setDayOfWeek(String d)         { this.dayOfWeek = d; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime)     { this.endTime = endTime; }
    public void setRoom(String room)           { this.room = room; }
    public void setCourseCode(String code)     { this.courseCode = code; }
    public void setCourseTitle(String title)   { this.courseTitle = title; }

    // ── Utility ──────────────────────────────────────────────
    public String getDisplaySlot() {
        return dayOfWeek + "  |  " + startTime + " - " + endTime + "  |  " + room;
    }

    @Override
    public String toString() {
        return getDisplaySlot();
    }
}