-- ==========================================================
-- SMART ALUMNI NETWORK SYSTEM
-- MySQL 8.0 Relational Database Schema (3NF)
-- ==========================================================

CREATE DATABASE IF NOT EXISTS smart_alumni;
USE smart_alumni;

-- Disable foreign key checks for clean teardown and reconstruction
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS pledges;
DROP TABLE IF EXISTS endowment_projects;
DROP TABLE IF EXISTS jobs;
DROP TABLE IF EXISTS mentorship_sessions;
DROP TABLE IF EXISTS alumni;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS verified_graduates;
DROP TABLE IF EXISTS departments;

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------------------------------------
-- 1. DEPARTMENTS TABLE
-- ----------------------------------------------------------
CREATE TABLE departments (
    dept_id INT AUTO_INCREMENT PRIMARY KEY,
    dept_code VARCHAR(20) NOT NULL UNIQUE,
    dept_name VARCHAR(150) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------
-- 2. VERIFIED GRADUATES TABLE (Institutional Verification Vault)
-- ----------------------------------------------------------
CREATE TABLE verified_graduates (
    grad_id INT AUTO_INCREMENT PRIMARY KEY,
    register_no VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(120) NOT NULL,
    dept_id INT NOT NULL,
    batch_year INT NOT NULL,
    degree VARCHAR(50) NOT NULL,
    is_registered BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_grad_dept FOREIGN KEY (dept_id) REFERENCES departments(dept_id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------
-- 3. USERS TABLE (Authentication & Account Records)
-- ----------------------------------------------------------
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(128) NOT NULL,
    salt VARCHAR(64) NOT NULL,
    role ENUM('Student', 'Alumni', 'Admin') NOT NULL,
    status ENUM('Active', 'Suspended') DEFAULT 'Active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------
-- 4. STUDENTS TABLE (Undergraduate Profiles)
-- ----------------------------------------------------------
CREATE TABLE students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    register_no VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(120) NOT NULL,
    dept_id INT NOT NULL,
    current_year INT NOT NULL CHECK (current_year BETWEEN 1 AND 5),
    phone VARCHAR(20) NOT NULL,
    bio TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_student_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_student_dept FOREIGN KEY (dept_id) REFERENCES departments(dept_id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------
-- 5. ALUMNI TABLE (Verified Alumni Profiles)
-- ----------------------------------------------------------
CREATE TABLE alumni (
    alumni_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    register_no VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(120) NOT NULL,
    dept_id INT NOT NULL,
    batch_year INT NOT NULL CHECK (batch_year >= 1970 AND batch_year <= 2030),
    company VARCHAR(120) NOT NULL,
    designation VARCHAR(120) NOT NULL,
    location VARCHAR(100),
    phone VARCHAR(20) NOT NULL,
    linkedin_url VARCHAR(255),
    is_mentor_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_alumni_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_alumni_dept FOREIGN KEY (dept_id) REFERENCES departments(dept_id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------
-- 6. MENTORSHIP SESSIONS TABLE (Student-Alumni Scheduling)
-- ----------------------------------------------------------
CREATE TABLE mentorship_sessions (
    session_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    alumni_id INT NOT NULL,
    topic VARCHAR(180) NOT NULL,
    message TEXT NOT NULL,
    preferred_date DATE NOT NULL,
    preferred_time VARCHAR(50) NOT NULL,
    status ENUM('Pending', 'Accepted', 'Rejected', 'Completed') DEFAULT 'Pending',
    response_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_mentorship_student FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    CONSTRAINT fk_mentorship_alumni FOREIGN KEY (alumni_id) REFERENCES alumni(alumni_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------
-- 7. JOBS TABLE (Alumni Referral Opportunities)
-- ----------------------------------------------------------
CREATE TABLE jobs (
    job_id INT AUTO_INCREMENT PRIMARY KEY,
    alumni_id INT NOT NULL,
    job_title VARCHAR(150) NOT NULL,
    company VARCHAR(120) NOT NULL,
    location VARCHAR(100) NOT NULL,
    job_type VARCHAR(50) DEFAULT 'Full-Time',
    salary_range VARCHAR(80),
    description TEXT NOT NULL,
    required_skills VARCHAR(255) NOT NULL,
    application_link VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_job_alumni FOREIGN KEY (alumni_id) REFERENCES alumni(alumni_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------
-- 8. ENDOWMENT PROJECTS TABLE (Department Funding Campaigns)
-- ----------------------------------------------------------
CREATE TABLE endowment_projects (
    project_id INT AUTO_INCREMENT PRIMARY KEY,
    dept_id INT NOT NULL,
    title VARCHAR(180) NOT NULL,
    description TEXT NOT NULL,
    target_amount DECIMAL(12,2) NOT NULL CHECK (target_amount > 0),
    current_amount DECIMAL(12,2) DEFAULT 0.00 CHECK (current_amount >= 0),
    status ENUM('Active', 'Funded', 'Completed') DEFAULT 'Active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_endowment_dept FOREIGN KEY (dept_id) REFERENCES departments(dept_id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------
-- 9. PLEDGES TABLE (Financial Giving Commitments)
-- ----------------------------------------------------------
CREATE TABLE pledges (
    pledge_id INT AUTO_INCREMENT PRIMARY KEY,
    project_id INT NOT NULL,
    user_id INT NOT NULL,
    pledge_amount DECIMAL(10,2) NOT NULL CHECK (pledge_amount > 0),
    pledge_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_status ENUM('Pledged', 'Fulfilled', 'Cancelled') DEFAULT 'Pledged',
    notes VARCHAR(255),
    CONSTRAINT fk_pledge_project FOREIGN KEY (project_id) REFERENCES endowment_projects(project_id) ON DELETE CASCADE,
    CONSTRAINT fk_pledge_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------
-- 10. NOTIFICATIONS TABLE (System & Activity Feed)
-- ----------------------------------------------------------
CREATE TABLE notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
