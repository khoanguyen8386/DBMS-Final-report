package com.courseapp.model;

public class Department {

    private int    id;
    private String code;     // e.g. "CS", "MATH", "BUS"
    private String name;     // e.g. "Công nghệ Thông tin"
    private String faculty;
    private String office;
    private String phone;

    public Department() {}

    public Department(int id, String code, String name) {
        this.id   = id;
        this.code = code;
        this.name = name;
    }

    // ── Getters ──────────────────────────────────────────────
    public int    getId()      { return id; }
    public String getCode()    { return code; }
    public String getName()    { return name; }
    public String getFaculty() { return faculty; }
    public String getOffice()  { return office; }
    public String getPhone()   { return phone; }

    // ── Setters ──────────────────────────────────────────────
    public void setId(int id)          { this.id = id; }
    public void setCode(String code)   { this.code = code; }
    public void setName(String name)   { this.name = name; }
    public void setFaculty(String f)   { this.faculty = f; }
    public void setOffice(String o)    { this.office = o; }
    public void setPhone(String p)     { this.phone = p; }

    /** Used by JComboBox dropdowns to display department name */
    @Override
    public String toString() {
        return code + " — " + name;
    }
}