package dao;

import model.MentorshipRequest;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MentorshipDAO {

    public boolean createRequest(MentorshipRequest req) {
        String sql = "INSERT INTO mentorship_sessions (student_id, alumni_id, topic, message, preferred_date, preferred_time, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, 'Pending')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, req.getStudentId());
            ps.setInt(2, req.getAlumniId());
            ps.setString(3, req.getTopic().trim());
            ps.setString(4, req.getMessage().trim());
            ps.setString(5, req.getPreferredDate());
            ps.setString(6, req.getPreferredTime().trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MentorshipDAO.createRequest] Error: " + e.getMessage());
            return false;
        }
    }

    public MentorshipRequest getRequestById(int sessionId) {
        String sql = baseQuery() + " WHERE ms.session_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapMentorship(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[MentorshipDAO.getRequestById] Error: " + e.getMessage());
        }
        return null;
    }

    public List<MentorshipRequest> getRequestsByStudentId(int studentId) {
        List<MentorshipRequest> list = new ArrayList<>();
        String sql = baseQuery() + " WHERE ms.student_id = ? ORDER BY ms.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapMentorship(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[MentorshipDAO.getRequestsByStudentId] Error: " + e.getMessage());
        }
        return list;
    }

    public List<MentorshipRequest> getRequestsByAlumniId(int alumniId) {
        List<MentorshipRequest> list = new ArrayList<>();
        String sql = baseQuery() + " WHERE ms.alumni_id = ? ORDER BY ms.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, alumniId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapMentorship(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[MentorshipDAO.getRequestsByAlumniId] Error: " + e.getMessage());
        }
        return list;
    }

    public boolean updateStatus(int sessionId, String status, String responseNotes) {
        String sql = "UPDATE mentorship_sessions SET status = ?, response_notes = ? WHERE session_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, responseNotes != null ? responseNotes.trim() : "");
            ps.setInt(3, sessionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MentorshipDAO.updateStatus] Error: " + e.getMessage());
            return false;
        }
    }

    public int getTotalMentorshipCount() {
        String sql = "SELECT COUNT(*) FROM mentorship_sessions";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[MentorshipDAO.getTotalMentorshipCount] Error: " + e.getMessage());
        }
        return 0;
    }

    private String baseQuery() {
        return "SELECT ms.session_id, ms.student_id, s.full_name AS student_name, su.email AS student_email, " +
               "d.dept_name AS student_dept, ms.alumni_id, a.full_name AS alumni_name, a.company AS alumni_company, " +
               "ms.topic, ms.message, ms.preferred_date, ms.preferred_time, ms.status, ms.response_notes, ms.created_at " +
               "FROM mentorship_sessions ms " +
               "JOIN students s ON ms.student_id = s.student_id " +
               "JOIN users su ON s.user_id = su.user_id " +
               "JOIN departments d ON s.dept_id = d.dept_id " +
               "JOIN alumni a ON ms.alumni_id = a.alumni_id";
    }

    private MentorshipRequest mapMentorship(ResultSet rs) throws SQLException {
        MentorshipRequest req = new MentorshipRequest();
        req.setSessionId(rs.getInt("session_id"));
        req.setStudentId(rs.getInt("student_id"));
        req.setStudentName(rs.getString("student_name"));
        req.setStudentEmail(rs.getString("student_email"));
        req.setStudentDept(rs.getString("student_dept"));
        req.setAlumniId(rs.getInt("alumni_id"));
        req.setAlumniName(rs.getString("alumni_name"));
        req.setAlumniCompany(rs.getString("alumni_company"));
        req.setTopic(rs.getString("topic"));
        req.setMessage(rs.getString("message"));
        req.setPreferredDate(rs.getString("preferred_date"));
        req.setPreferredTime(rs.getString("preferred_time"));
        req.setStatus(rs.getString("status"));
        req.setResponseNotes(rs.getString("response_notes"));
        req.setCreatedAt(rs.getTimestamp("created_at"));
        return req;
    }
}
