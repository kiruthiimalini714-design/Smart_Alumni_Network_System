package model;

import java.sql.Timestamp;

public class Alumni {
    private int alumniId;
    private int userId;
    private String registerNo;
    private String fullName;
    private int deptId;
    private String deptName;
    private String deptCode;
    private int batchYear;
    private String company;
    private String designation;
    private String location;
    private String phone;
    private String linkedinUrl;
    private boolean isMentorAvailable;
    private String email;
    private Timestamp createdAt;

    public Alumni() {}

    public int getAlumniId() { return alumniId; }
    public void setAlumniId(int alumniId) { this.alumniId = alumniId; }

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

    public int getBatchYear() { return batchYear; }
    public void setBatchYear(int batchYear) { this.batchYear = batchYear; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }

    public boolean isMentorAvailable() { return isMentorAvailable; }
    public void setMentorAvailable(boolean mentorAvailable) { isMentorAvailable = mentorAvailable; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
