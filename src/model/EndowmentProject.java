package model;

import java.sql.Timestamp;

public class EndowmentProject {
    private int projectId;
    private int deptId;
    private String deptName;
    private String deptCode;
    private String title;
    private String description;
    private double targetAmount;
    private double currentAmount;
    private String status; // "Active", "Funded", "Completed"
    private int percentageFunded;
    private Timestamp createdAt;

    public EndowmentProject() {}

    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }

    public int getDeptId() { return deptId; }
    public void setDeptId(int deptId) { this.deptId = deptId; }

    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }

    public String getDeptCode() { return deptCode; }
    public void setDeptCode(String deptCode) { this.deptCode = deptCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getTargetAmount() { return targetAmount; }
    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
        calculatePercentage();
    }

    public double getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
        calculatePercentage();
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getPercentageFunded() { return percentageFunded; }
    public void setPercentageFunded(int percentageFunded) { this.percentageFunded = percentageFunded; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    private void calculatePercentage() {
        if (targetAmount > 0) {
            this.percentageFunded = (int) Math.min(100, Math.round((currentAmount / targetAmount) * 100));
        } else {
            this.percentageFunded = 0;
        }
    }
}
