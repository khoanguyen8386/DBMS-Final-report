DROP DATABASE IF EXISTS course_registration;
CREATE DATABASE course_registration CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE course_registration;
-- -------------------------------------------------------------
-- 1. DATABASE SCHEMA DEFINITION
-- -------------------------------------------------------------

-- Table to store University Departments information
CREATE TABLE departments (
    id      INT AUTO_INCREMENT PRIMARY KEY,
    code    VARCHAR(10)  NOT NULL UNIQUE,
    name    VARCHAR(100) NOT NULL,
    faculty VARCHAR(100),
    office  VARCHAR(100),
    phone   VARCHAR(20)
);

-- Table for Instructor/Faculty details
CREATE TABLE instructors (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(100) NOT NULL,
    email    VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    title    VARCHAR(50),
    office   VARCHAR(100),
    dept_id  INT,
    FOREIGN KEY (dept_id) REFERENCES departments(id)
);

-- Table for Student records
CREATE TABLE students (
    id          VARCHAR(20) PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(100) UNIQUE NOT NULL,
    password    VARCHAR(255) NOT NULL,
    phone       VARCHAR(20),
    dept_id     INT,
    enroll_year INT,
    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (dept_id) REFERENCES departments(id)
);

-- Table for System Administrators
CREATE TABLE admins (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(100) NOT NULL,
    email    VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- Table for Course information
CREATE TABLE courses (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    code          VARCHAR(20)  UNIQUE NOT NULL,
    title         VARCHAR(150) NOT NULL,
    credits       INT DEFAULT 3,
    capacity      INT DEFAULT 30,
    enrolled      INT DEFAULT 0,
    dept_id       INT,
    instructor_id INT,
    FOREIGN KEY (dept_id)       REFERENCES departments(id),
    FOREIGN KEY (instructor_id) REFERENCES instructors(id)
);

-- Table for Course Schedules
CREATE TABLE schedules (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    course_id   INT NOT NULL,
    day_of_week VARCHAR(10) NOT NULL,
    start_time  TIME NOT NULL,
    end_time    TIME NOT NULL,
    room        VARCHAR(50),
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- Table for Enrollment/Registration link
CREATE TABLE registrations (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    student_id    VARCHAR(20) NOT NULL,
    course_id     INT NOT NULL,
    status        ENUM('enrolled','waitlisted','dropped','completed') DEFAULT 'enrolled',
    grade         VARCHAR(5),
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (course_id)  REFERENCES courses(id),
    UNIQUE (student_id, course_id)
);
-- -------------------------------------------------------------
-- 2. SEED DATA (INITIALIZATION)
-- -------------------------------------------------------------

-- Departments (English)
INSERT INTO departments (code, name, faculty, office) VALUES
('CS',   'Computer Science', 'Engineering', 'Room 101'),
('MATH', 'Mathematics',      'Science',     'Room 202'),
('BUS',  'Business',         'Commerce',    'Room 303'),
('ENG',  'English Studies',  'Humanities',  'Room 404');

-- Admins (English name, it's a system account)
INSERT INTO admins (name, email, password) VALUES
('System Admin', 'admin@university.edu', 'admin123');

-- Instructors (Vietnamese names only, titles in English)
INSERT INTO instructors (name, email, password, title, office, dept_id) VALUES
('Nguyen Van An',   'an.nv@university.edu',    'pass123', 'Associate Professor', 'Room 101A', 1),
('Tran Thi Bich',   'bich.tt@university.edu',  'pass123', 'Lecturer',            'Room 101B', 1),
('Le Hoang Minh',   'minh.lh@university.edu',  'pass123', 'Doctor',              'Room 202A', 2),
('Pham Thu Huong',  'huong.pt@university.edu',  'pass123', 'Lecturer',            'Room 303A', 3),
('Vu Duc Thang',    'thang.vd@university.edu',  'pass123', 'Associate Professor', 'Room 101C', 1);

-- Students (Vietnamese names only, everything else English)
INSERT INTO students (id, name, email, password, phone, dept_id, enroll_year) VALUES
('CS21001',   'Nguyen Thi Lan Anh',  'lananh@student.edu',   '1234', '0901234567', 1, 2021),
('CS21002',   'Tran Van Bao',         'bao.tv@student.edu',   '1234', '0912345678', 1, 2021),
('CS22101',   'Le Thi Cam Nhung',     'nhung.ltc@student.edu','1234', '0923456789', 1, 2022),
('CS22102',   'Pham Hoang Dung',      'dung.ph@student.edu',  '1234', '0934567890', 1, 2022),
('CS23201',   'Hoang Thi My Linh',    'linh.htm@student.edu', '1234', '0945678901', 1, 2023),
('MATH21051', 'Vo Van Duc',           'duc.vv@student.edu',   '1234', '0956789012', 2, 2021),
('MATH22071', 'Dang Thi Kieu Oanh',   'oanh.dtk@student.edu', '1234', '0967890123', 2, 2022),
('BUS21301',  'Bui Quoc Hung',        'hung.bq@student.edu',  '1234', '0978901234', 3, 2021),
('BUS22311',  'Dinh Thi Hong Nhung',  'nhung.dth@student.edu','1234', '0989012345', 3, 2022),
('CS24301',   'Ngo Minh Triet',       'triet.nm@student.edu', '1234', '0990123456', 1, 2024);

-- Courses (English)
INSERT INTO courses (code, title, credits, capacity, dept_id, instructor_id) VALUES
('CS101',   'Introduction to Programming', 3, 30, 1, 1),
('CS201',   'Data Structures',             3, 25, 1, 2),
('CS301',   'Database Systems',            3, 20, 1, 1),
('CS401',   'Software Engineering',        4, 30, 1, 5),
('CS302',   'Computer Networks',           3, 28, 1, 2),
('MATH101', 'Calculus I',                  3, 35, 2, 3),
('MATH201', 'Linear Algebra',              3, 30, 2, 3),
('BUS101',  'Microeconomics',              3, 40, 3, 4),
('BUS201',  'Business Management',         3, 35, 3, 4);

-- Schedules (English days)
INSERT INTO schedules (course_id, day_of_week, start_time, end_time, room) VALUES
(1, 'Monday',    '07:30', '09:00', 'H1-101'),
(1, 'Wednesday', '07:30', '09:00', 'H1-101'),
(2, 'Tuesday',   '09:15', '10:45', 'H1-203'),
(2, 'Thursday',  '09:15', '10:45', 'H1-203'),
(3, 'Wednesday', '13:00', '14:30', 'H2-301'),
(3, 'Friday',    '13:00', '14:30', 'H2-301'),
(4, 'Tuesday',   '07:30', '09:00', 'H3-401'),
(4, 'Thursday',  '07:30', '09:00', 'H3-401'),
(5, 'Monday',    '13:00', '14:30', 'H2-205'),
(6, 'Monday',    '09:15', '10:45', 'H4-101'),
(6, 'Friday',    '09:15', '10:45', 'H4-101'),
(7, 'Tuesday',   '13:00', '14:30', 'H4-102'),
(8, 'Wednesday', '09:15', '10:45', 'H5-201'),
(9, 'Thursday',  '13:00', '14:30', 'H5-202');

-- Registrations (status in English — matches ENUM)
INSERT INTO registrations (student_id, course_id, status) VALUES
('CS21001',   1, 'enrolled'),
('CS21001',   2, 'enrolled'),
('CS21001',   6, 'enrolled'),
('CS21002',   1, 'enrolled'),
('CS21002',   3, 'enrolled'),
('CS22101',   2, 'enrolled'),
('CS22101',   4, 'enrolled'),
('MATH21051', 6, 'enrolled'),
('MATH21051', 7, 'enrolled'),
('BUS21301',  8, 'enrolled'),
('BUS22311',  9, 'enrolled'),
('CS22102',   1, 'enrolled'),
('CS23201',   2, 'enrolled'),
('CS24301',   1, 'enrolled');
