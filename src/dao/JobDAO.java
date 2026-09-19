package dao;

import model.Job;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobDAO {

    public boolean createJob(Job j) {
        String sql = "INSERT INTO jobs (alumni_id, job_title, company, location, job_type, salary_range, description, required_skills, application_link) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, j.getAlumniId());
            ps.setString(2, j.getJobTitle().trim());
            ps.setString(3, j.getCompany().trim());
            ps.setString(4, j.getLocation().trim());
            ps.setString(5, j.getJobType() != null ? j.getJobType().trim() : "Full-Time");
            ps.setString(6, j.getSalaryRange() != null ? j.getSalaryRange().trim() : "");
            ps.setString(7, j.getDescription().trim());
            ps.setString(8, j.getRequiredSkills().trim());
            ps.setString(9, j.getApplicationLink().trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[JobDAO.createJob] Error: " + e.getMessage());
            return false;
        }
    }

    public Job getJobById(int jobId) {
        String sql = "SELECT j.job_id, j.alumni_id, a.full_name AS alumni_name, u.email AS alumni_email, " +
                     "j.job_title, j.company, j.location, j.job_type, j.salary_range, j.description, " +
                     "j.required_skills, j.application_link, j.created_at " +
                     "FROM jobs j " +
                     "JOIN alumni a ON j.alumni_id = a.alumni_id " +
                     "JOIN users u ON a.user_id = u.user_id " +
                     "WHERE j.job_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapJob(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[JobDAO.getJobById] Error: " + e.getMessage());
        }
        return null;
    }

    public List<Job> getAllJobs(String search, String location) {
        List<Job> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT j.job_id, j.alumni_id, a.full_name AS alumni_name, u.email AS alumni_email, " +
                "j.job_title, j.company, j.location, j.job_type, j.salary_range, j.description, " +
                "j.required_skills, j.application_link, j.created_at " +
                "FROM jobs j " +
                "JOIN alumni a ON j.alumni_id = a.alumni_id " +
                "JOIN users u ON a.user_id = u.user_id WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();
        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (LOWER(j.job_title) LIKE ? OR LOWER(j.company) LIKE ? OR LOWER(j.required_skills) LIKE ?) ");
            String term = "%" + search.trim().toLowerCase() + "%";
            params.add(term);
            params.add(term);
            params.add(term);
        }
        if (location != null && !location.trim().isEmpty()) {
            sql.append("AND LOWER(j.location) LIKE ? ");
            params.add("%" + location.trim().toLowerCase() + "%");
        }
        sql.append("ORDER BY j.created_at DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapJob(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[JobDAO.getAllJobs] Error: " + e.getMessage());
        }
        return list;
    }

    public List<Job> getJobsByAlumniId(int alumniId) {
        List<Job> list = new ArrayList<>();
        String sql = "SELECT j.job_id, j.alumni_id, a.full_name AS alumni_name, u.email AS alumni_email, " +
                     "j.job_title, j.company, j.location, j.job_type, j.salary_range, j.description, " +
                     "j.required_skills, j.application_link, j.created_at " +
                     "FROM jobs j " +
                     "JOIN alumni a ON j.alumni_id = a.alumni_id " +
                     "JOIN users u ON a.user_id = u.user_id " +
                     "WHERE j.alumni_id = ? ORDER BY j.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, alumniId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapJob(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[JobDAO.getJobsByAlumniId] Error: " + e.getMessage());
        }
        return list;
    }

    public boolean updateJob(Job j) {
        String sql = "UPDATE jobs SET job_title = ?, company = ?, location = ?, job_type = ?, " +
                     "salary_range = ?, description = ?, required_skills = ?, application_link = ? " +
                     "WHERE job_id = ? AND alumni_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, j.getJobTitle().trim());
            ps.setString(2, j.getCompany().trim());
            ps.setString(3, j.getLocation().trim());
            ps.setString(4, j.getJobType().trim());
            ps.setString(5, j.getSalaryRange().trim());
            ps.setString(6, j.getDescription().trim());
            ps.setString(7, j.getRequiredSkills().trim());
            ps.setString(8, j.getApplicationLink().trim());
            ps.setInt(9, j.getJobId());
            ps.setInt(10, j.getAlumniId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[JobDAO.updateJob] Error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteJob(int jobId, int alumniId) {
        String sql = alumniId > 0 ? "DELETE FROM jobs WHERE job_id = ? AND alumni_id = ?" : "DELETE FROM jobs WHERE job_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            if (alumniId > 0) {
                ps.setInt(2, alumniId);
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[JobDAO.deleteJob] Error: " + e.getMessage());
            return false;
        }
    }

    public int getTotalJobsCount() {
        String sql = "SELECT COUNT(*) FROM jobs";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[JobDAO.getTotalJobsCount] Error: " + e.getMessage());
        }
        return 0;
    }

    private Job mapJob(ResultSet rs) throws SQLException {
        Job j = new Job();
        j.setJobId(rs.getInt("job_id"));
        j.setAlumniId(rs.getInt("alumni_id"));
        j.setAlumniName(rs.getString("alumni_name"));
        j.setAlumniEmail(rs.getString("alumni_email"));
        j.setJobTitle(rs.getString("job_title"));
        j.setCompany(rs.getString("company"));
        j.setLocation(rs.getString("location"));
        j.setJobType(rs.getString("job_type"));
        j.setSalaryRange(rs.getString("salary_range"));
        j.setDescription(rs.getString("description"));
        j.setRequiredSkills(rs.getString("required_skills"));
        j.setApplicationLink(rs.getString("application_link"));
        j.setCreatedAt(rs.getTimestamp("created_at"));
        return j;
    }
}
