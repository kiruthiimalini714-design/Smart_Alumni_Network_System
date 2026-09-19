package dao;

import model.Alumni;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlumniDAO {

    public boolean createAlumni(Alumni a) {
        String sql = "INSERT INTO alumni (user_id, register_no, full_name, dept_id, batch_year, company, designation, location, phone, linkedin_url, is_mentor_available) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, a.getUserId());
            ps.setString(2, a.getRegisterNo().trim());
            ps.setString(3, a.getFullName().trim());
            ps.setInt(4, a.getDeptId());
            ps.setInt(5, a.getBatchYear());
            ps.setString(6, a.getCompany().trim());
            ps.setString(7, a.getDesignation().trim());
            ps.setString(8, a.getLocation() != null ? a.getLocation().trim() : "");
            ps.setString(9, a.getPhone().trim());
            ps.setString(10, a.getLinkedinUrl() != null ? a.getLinkedinUrl().trim() : "");
            ps.setBoolean(11, a.isMentorAvailable());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AlumniDAO.createAlumni] Error: " + e.getMessage());
            return false;
        }
    }

    public Alumni findByUserId(int userId) {
        String sql = "SELECT a.alumni_id, a.user_id, a.register_no, a.full_name, a.dept_id, " +
                     "d.dept_name, d.dept_code, a.batch_year, a.company, a.designation, a.location, " +
                     "a.phone, a.linkedin_url, a.is_mentor_available, u.email, a.created_at " +
                     "FROM alumni a " +
                     "JOIN departments d ON a.dept_id = d.dept_id " +
                     "JOIN users u ON a.user_id = u.user_id " +
                     "WHERE a.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAlumni(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[AlumniDAO.findByUserId] Error: " + e.getMessage());
        }
        return null;
    }

    public Alumni findByAlumniId(int alumniId) {
        String sql = "SELECT a.alumni_id, a.user_id, a.register_no, a.full_name, a.dept_id, " +
                     "d.dept_name, d.dept_code, a.batch_year, a.company, a.designation, a.location, " +
                     "a.phone, a.linkedin_url, a.is_mentor_available, u.email, a.created_at " +
                     "FROM alumni a " +
                     "JOIN departments d ON a.dept_id = d.dept_id " +
                     "JOIN users u ON a.user_id = u.user_id " +
                     "WHERE a.alumni_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, alumniId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAlumni(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[AlumniDAO.findByAlumniId] Error: " + e.getMessage());
        }
        return null;
    }

    public boolean updateAlumni(Alumni a) {
        String sql = "UPDATE alumni SET company = ?, designation = ?, location = ?, phone = ?, " +
                     "linkedin_url = ?, is_mentor_available = ? WHERE alumni_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getCompany());
            ps.setString(2, a.getDesignation());
            ps.setString(3, a.getLocation());
            ps.setString(4, a.getPhone());
            ps.setString(5, a.getLinkedinUrl());
            ps.setBoolean(6, a.isMentorAvailable());
            ps.setInt(7, a.getAlumniId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AlumniDAO.updateAlumni] Error: " + e.getMessage());
            return false;
        }
    }

    public List<Alumni> searchAlumni(String search, Integer deptId, Integer batchYear, String company, Boolean mentorOnly) {
        List<Alumni> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT a.alumni_id, a.user_id, a.register_no, a.full_name, a.dept_id, " +
                "d.dept_name, d.dept_code, a.batch_year, a.company, a.designation, a.location, " +
                "a.phone, a.linkedin_url, a.is_mentor_available, u.email, a.created_at " +
                "FROM alumni a " +
                "JOIN departments d ON a.dept_id = d.dept_id " +
                "JOIN users u ON a.user_id = u.user_id WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();
        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (LOWER(a.full_name) LIKE ? OR LOWER(a.company) LIKE ? OR LOWER(a.designation) LIKE ? OR LOWER(a.location) LIKE ?) ");
            String term = "%" + search.trim().toLowerCase() + "%";
            params.add(term);
            params.add(term);
            params.add(term);
            params.add(term);
        }
        if (deptId != null && deptId > 0) {
            sql.append("AND a.dept_id = ? ");
            params.add(deptId);
        }
        if (batchYear != null && batchYear > 0) {
            sql.append("AND a.batch_year = ? ");
            params.add(batchYear);
        }
        if (company != null && !company.trim().isEmpty()) {
            sql.append("AND LOWER(a.company) LIKE ? ");
            params.add("%" + company.trim().toLowerCase() + "%");
        }
        if (mentorOnly != null && mentorOnly) {
            sql.append("AND a.is_mentor_available = TRUE ");
        }
        sql.append("ORDER BY a.full_name ASC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAlumni(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[AlumniDAO.searchAlumni] Error: " + e.getMessage());
        }
        return list;
    }

    public List<Alumni> getAllAlumni() {
        return searchAlumni(null, null, null, null, null);
    }

    private Alumni mapAlumni(ResultSet rs) throws SQLException {
        Alumni a = new Alumni();
        a.setAlumniId(rs.getInt("alumni_id"));
        a.setUserId(rs.getInt("user_id"));
        a.setRegisterNo(rs.getString("register_no"));
        a.setFullName(rs.getString("full_name"));
        a.setDeptId(rs.getInt("dept_id"));
        a.setDeptName(rs.getString("dept_name"));
        a.setDeptCode(rs.getString("dept_code"));
        a.setBatchYear(rs.getInt("batch_year"));
        a.setCompany(rs.getString("company"));
        a.setDesignation(rs.getString("designation"));
        a.setLocation(rs.getString("location"));
        a.setPhone(rs.getString("phone"));
        a.setLinkedinUrl(rs.getString("linkedin_url"));
        a.setMentorAvailable(rs.getBoolean("is_mentor_available"));
        a.setEmail(rs.getString("email"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        return a;
    }
}
