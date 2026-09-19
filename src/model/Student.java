package model;

import java.sql.Timestamp;

public class Student {
    private int studentId;
    private int userId;
    private String registerNo;
    private String fullName;
    private int deptId;
    private String deptName;
    private String deptCode;
    private int currentYear;
    private String phone;
    private String bio;
    private String email;
    private Timestamp createdAt;

    public Student() {}

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getRegisterNo() { return registerNo; }
    public void setRegisterNo(String registerNo) { this.registerNo = registerNo; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public int getDeptId() { return deptId; }
    public void setDeptId(int deptId) { this.deptId = deptId; }

    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }

    public String getDeptCode() { return deptCode; }
    public void setDeptCode(String deptCode) { this.deptCode = deptCode; }

    public int getCurrentYear() { return currentYear; }
    public void setCurrentYear(int currentYear) { this.currentYear = currentYear; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
