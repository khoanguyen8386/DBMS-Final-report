package com.courseapp.model;

public class Instructor {

    private int    id;
    private String name;
    private String email;
    private String password;
    private String title;    // e.g. "Phó Giáo Sư", "Tiến Sĩ"
    private String office;
    private int    deptId;

    // joined display field
    private String deptName;

    public Instructor() {}

    public Instructor(int id, String name, String email) {
        this.id    = id;
        this.name  = name;
        this.email = email;
    }

    // ── Getters ──────────────────────────────────────────────
    public int    getId()       { return id; }
    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }
    public String getTitle()    { return title; }
    public String getOffice()   { return office; }
    public int    getDeptId()   { return deptId; }
    public String getDeptName() { return deptName; }

    // ── Setters ──────────────────────────────────────────────
    public void setId(int id)              { this.id = id; }
    public void setName(String name)       { this.name = name; }
    public void setEmail(String email)     { this.email = email; }
    public void setPassword(String pw)     { this.password = pw; }
    public void setTitle(String title)     { this.title = title; }
    public void setOffice(String office)   { this.office = office; }
    public void setDeptId(int deptId)      { this.deptId = deptId; }
    public void setDeptName(String dn)     { this.deptName = dn; }

    /** Used by JComboBox dropdowns */
    @Override
    public String toString() {
        return (title != null ? title + " " : "") + name;
    }
}