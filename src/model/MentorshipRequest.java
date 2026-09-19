package model;

import java.sql.Date;
import java.sql.Timestamp;

public class MentorshipRequest {
    private int sessionId;
    private int studentId;
    private String studentName;
    private String studentEmail;
    private String studentDept;
    private int alumniId;
    private String alumniName;
    private String alumniCompany;
    private String topic;
    private String message;
    private String preferredDate;
    private String preferredTime;
    private String status; // "Pending", "Accepted", "Rejected", "Completed"
    private String responseNotes;
    private Timestamp createdAt;

    public MentorshipRequest() {}

    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public String getStudentDept() { return studentDept; }
    public void setStudentDept(String studentDept) { this.studentDept = studentDept; }

    public int getAlumniId() { return alumniId; }
    public void setAlumniId(int alumniId) { this.alumniId = alumniId; }

    public String getAlumniName() { return alumniName; }
    public void setAlumniName(String alumniName) { this.alumniName = alumniName; }

    public String getAlumniCompany() { return alumniCompany; }
    public void setAlumniCompany(String alumniCompany) { this.alumniCompany = alumniCompany; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPreferredDate() { return preferredDate; }
    public void setPreferredDate(String preferredDate) { this.preferredDate = preferredDate; }

    public String getPreferredTime() { return preferredTime; }
    public void setPreferredTime(String preferredTime) { this.preferredTime = preferredTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getResponseNotes() { return responseNotes; }
    public void setResponseNotes(String responseNotes) { this.responseNotes = responseNotes; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
