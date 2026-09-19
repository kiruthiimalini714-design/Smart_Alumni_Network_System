package model;

import java.sql.Timestamp;

public class Pledge {
    private int pledgeId;
    private int projectId;
    private String projectTitle;
    private int userId;
    private String donorName;
    private String donorEmail;
    private String donorRole;
    private double pledgeAmount;
    private Timestamp pledgeDate;
    private String paymentStatus; // "Pledged", "Fulfilled", "Cancelled"
    private String notes;

    public Pledge() {}

    public int getPledgeId() { return pledgeId; }
    public void setPledgeId(int pledgeId) { this.pledgeId = pledgeId; }

    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }

    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getDonorName() { return donorName; }
    public void setDonorName(String donorName) { this.donorName = donorName; }

    public String getDonorEmail() { return donorEmail; }
    public void setDonorEmail(String donorEmail) { this.donorEmail = donorEmail; }

    public String getDonorRole() { return donorRole; }
    public void setDonorRole(String donorRole) { this.donorRole = donorRole; }

    public double getPledgeAmount() { return pledgeAmount; }
    public void setPledgeAmount(double pledgeAmount) { this.pledgeAmount = pledgeAmount; }

    public Timestamp getPledgeDate() { return pledgeDate; }
    public void setPledgeDate(Timestamp pledgeDate) { this.pledgeDate = pledgeDate; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
