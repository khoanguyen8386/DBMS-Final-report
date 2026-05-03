package com.courseapp.service;

import com.courseapp.dao.AdminDAO;
import com.courseapp.dao.InstructorDAO;
import com.courseapp.dao.StudentDAO;
import com.courseapp.model.Admin;
import com.courseapp.model.Instructor;
import com.courseapp.model.Student;
import com.courseapp.util.PasswordUtil;

public class AuthService {

    public enum Role { STUDENT, INSTRUCTOR, ADMIN }

    public static class LoginResult {
        private final Role   role;
        private final Object user;

        public LoginResult(Role role, Object user) {
            this.role = role;
            this.user = user;
        }

        public Role   getRole()        { return role; }
        public Object getUser()        { return user; }

        public Student    asStudent()    { return (Student)    user; }
        public Instructor asInstructor() { return (Instructor) user; }
        public Admin      asAdmin()      { return (Admin)      user; }

        public boolean isStudent()    { return role == Role.STUDENT; }
        public boolean isInstructor() { return role == Role.INSTRUCTOR; }
        public boolean isAdmin()      { return role == Role.ADMIN; }
    }

    private final StudentDAO    studentDAO    = new StudentDAO();
    private final InstructorDAO instructorDAO = new InstructorDAO();
    private final AdminDAO      adminDAO      = new AdminDAO();

    /**
     * Fetches user by email, then verifies password with BCrypt.
     * Never compares plain text to the stored hash in SQL.
     */
    public LoginResult login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return null;
        }

        String e = email.trim();
        String p = password.trim();

        // 1. Admin
        Admin admin = adminDAO.findByEmail(e);
        if (admin != null && PasswordUtil.checkPassword(p, admin.getPassword())) {
            return new LoginResult(Role.ADMIN, admin);
        }

        // 2. Instructor
        Instructor instructor = instructorDAO.findByEmail(e);
        if (instructor != null && PasswordUtil.checkPassword(p, instructor.getPassword())) {
            return new LoginResult(Role.INSTRUCTOR, instructor);
        }

        // 3. Student
        Student student = studentDAO.findByEmail(e);
        if (student != null && PasswordUtil.checkPassword(p, student.getPassword())) {
            return new LoginResult(Role.STUDENT, student);
        }

        return null;
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 4;
    }
}
