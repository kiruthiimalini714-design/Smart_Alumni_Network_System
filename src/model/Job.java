package model;

import java.sql.Timestamp;

public class Job {
    private int jobId;
    private int alumniId;
    private String alumniName;
    private String alumniEmail;
    private String company;
    private String jobTitle;
    private String location;
    private String jobType;
    private String salaryRange;
    private String description;
    private String requiredSkills;
    private String applicationLink;
    private Timestamp createdAt;

    public Job() {}

    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }

    public int getAlumniId() { return alumniId; }
    public void setAlumniId(int alumniId) { this.alumniId = alumniId; }

    public String getAlumniName() { return alumniName; }
    public void setAlumniName(String alumniName) { this.alumniName = alumniName; }

    public String getAlumniEmail() { return alumniEmail; }
    public void setAlumniEmail(String alumniEmail) { this.alumniEmail = alumniEmail; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }

    public String getSalaryRange() { return salaryRange; }
    public void setSalaryRange(String salaryRange) { this.salaryRange = salaryRange; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(String requiredSkills) { this.requiredSkills = requiredSkills; }

    public String getApplicationLink() { return applicationLink; }
    public void setApplicationLink(String applicationLink) { this.applicationLink = applicationLink; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
