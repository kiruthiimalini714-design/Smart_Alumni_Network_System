package service;

import dao.AlumniDAO;
import dao.JobDAO;
import dao.NotificationDAO;
import model.Alumni;
import model.Job;
import model.Notification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobService {

    private final JobDAO jobDAO = new JobDAO();
    private final AlumniDAO alumniDAO = new AlumniDAO();

    public List<Job> getAllJobs(String search, String location) {
        return jobDAO.getAllJobs(search, location);
    }

    public Job getJobById(int jobId) {
        return jobDAO.getJobById(jobId);
    }

    public List<Job> getJobsByAlumni(int userId) {
        Alumni alumni = alumniDAO.findByUserId(userId);
        if (alumni == null) return List.of();
        return jobDAO.getJobsByAlumniId(alumni.getAlumniId());
    }

    public Map<String, Object> createJob(int userId, String jobTitle, String company,
                                        String location, String jobType, String salaryRange,
                                        String description, String requiredSkills, String applicationLink) {
        Map<String, Object> res = new HashMap<>();

        Alumni alumni = alumniDAO.findByUserId(userId);
        if (alumni == null) {
            res.put("success", false);
            res.put("message", "Only verified alumni can publish job postings.");
            return res;
        }

        if (jobTitle == null || jobTitle.trim().isEmpty() || company == null || company.trim().isEmpty() ||
            description == null || description.trim().isEmpty() || requiredSkills == null || requiredSkills.trim().isEmpty() ||
            applicationLink == null || applicationLink.trim().isEmpty()) {
            res.put("success", false);
            res.put("message", "Job title, company, description, required skills, and application link are required.");
            return res;
        }

        Job job = new Job();
        job.setAlumniId(alumni.getAlumniId());
        job.setJobTitle(jobTitle.trim());
        job.setCompany(company.trim());
        job.setLocation(location != null && !location.trim().isEmpty() ? location.trim() : "Flexible / Hybrid");
        job.setJobType(jobType != null && !jobType.trim().isEmpty() ? jobType.trim() : "Full-Time");
        job.setSalaryRange(salaryRange != null ? salaryRange.trim() : "Competitive");
        job.setDescription(description.trim());
        job.setRequiredSkills(requiredSkills.trim());
        job.setApplicationLink(applicationLink.trim());

        boolean created = jobDAO.createJob(job);
        res.put("success", created);
        res.put("message", created ? "Job opportunity posted successfully!" : "Failed to post job.");
        return res;
    }

    public Map<String, Object> updateJob(int userId, int jobId, String jobTitle, String company,
                                        String location, String jobType, String salaryRange,
                                        String description, String requiredSkills, String applicationLink) {
        Map<String, Object> res = new HashMap<>();

        Alumni alumni = alumniDAO.findByUserId(userId);
        if (alumni == null) {
            res.put("success", false);
            res.put("message", "Alumni profile not found.");
            return res;
        }

        Job existing = jobDAO.getJobById(jobId);
        if (existing == null) {
            res.put("success", false);
            res.put("message", "Job posting not found.");
            return res;
        }

        // Ownership validation
        if (existing.getAlumniId() != alumni.getAlumniId()) {
            res.put("success", false);
            res.put("message", "Unauthorized: You can only edit jobs you posted.");
            return res;
        }

        existing.setJobTitle(jobTitle != null && !jobTitle.trim().isEmpty() ? jobTitle.trim() : existing.getJobTitle());
        existing.setCompany(company != null && !company.trim().isEmpty() ? company.trim() : existing.getCompany());
        existing.setLocation(location != null ? location.trim() : existing.getLocation());
        existing.setJobType(jobType != null ? jobType.trim() : existing.getJobType());
        existing.setSalaryRange(salaryRange != null ? salaryRange.trim() : existing.getSalaryRange());
        existing.setDescription(description != null && !description.trim().isEmpty() ? description.trim() : existing.getDescription());
        existing.setRequiredSkills(requiredSkills != null && !requiredSkills.trim().isEmpty() ? requiredSkills.trim() : existing.getRequiredSkills());
        existing.setApplicationLink(applicationLink != null && !applicationLink.trim().isEmpty() ? applicationLink.trim() : existing.getApplicationLink());

        boolean updated = jobDAO.updateJob(existing);
        res.put("success", updated);
        res.put("message", updated ? "Job posting updated successfully!" : "Failed to update job.");
        return res;
    }

    public Map<String, Object> deleteJob(int userId, String role, int jobId) {
        Map<String, Object> res = new HashMap<>();

        Job existing = jobDAO.getJobById(jobId);
        if (existing == null) {
            res.put("success", false);
            res.put("message", "Job not found.");
            return res;
        }

        if ("Admin".equalsIgnoreCase(role)) {
            boolean deleted = jobDAO.deleteJob(jobId, -1);
            res.put("success", deleted);
            res.put("message", deleted ? "Job deleted by Administrator." : "Failed to delete job.");
            return res;
        }

        Alumni alumni = alumniDAO.findByUserId(userId);
        if (alumni == null || existing.getAlumniId() != alumni.getAlumniId()) {
            res.put("success", false);
            res.put("message", "Unauthorized: You can only delete job postings created by yourself.");
            return res;
        }

        boolean deleted = jobDAO.deleteJob(jobId, alumni.getAlumniId());
        res.put("success", deleted);
        res.put("message", deleted ? "Job posting removed successfully." : "Failed to delete job.");
        return res;
    }
}
