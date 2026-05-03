package com.courseapp.model;

public class Admin {

    private int    id;
    private String name;
    private String email;
    private String password;

    public Admin() {}

    public Admin(int id, String name, String email) {
        this.id    = id;
        this.name  = name;
        this.email = email;
    }

    // ── Getters ──────────────────────────────────────────────
    public int    getId()       { return id; }
    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }

    // ── Setters ──────────────────────────────────────────────
    public void setId(int id)            { this.id = id; }
    public void setName(String name)     { this.name = name; }
    public void setEmail(String email)   { this.email = email; }
    public void setPassword(String pw)   { this.password = pw; }

    @Override
    public String toString() {
        return name + " (Admin)";
    }
}