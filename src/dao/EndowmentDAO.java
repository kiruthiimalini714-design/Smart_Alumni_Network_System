package dao;

import model.EndowmentProject;
import model.Pledge;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EndowmentDAO {

    public List<EndowmentProject> getAllProjects() {
        List<EndowmentProject> list = new ArrayList<>();
        String sql = "SELECT ep.project_id, ep.dept_id, d.dept_name, d.dept_code, ep.title, ep.description, " +
                     "ep.target_amount, ep.current_amount, ep.status, ep.created_at " +
                     "FROM endowment_projects ep " +
                     "JOIN departments d ON ep.dept_id = d.dept_id " +
                     "ORDER BY ep.created_at ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapProject(rs));
            }
        } catch (SQLException e) {
            System.err.println("[EndowmentDAO.getAllProjects] Error: " + e.getMessage());
        }
        return list;
    }

    public EndowmentProject getProjectById(int projectId) {
        String sql = "SELECT ep.project_id, ep.dept_id, d.dept_name, d.dept_code, ep.title, ep.description, " +
                     "ep.target_amount, ep.current_amount, ep.status, ep.created_at " +
                     "FROM endowment_projects ep " +
                     "JOIN departments d ON ep.dept_id = d.dept_id " +
                     "WHERE ep.project_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapProject(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[EndowmentDAO.getProjectById] Error: " + e.getMessage());
        }
        return null;
    }

    public boolean createPledge(Pledge pledge) {
        String insertSql = "INSERT INTO pledges (project_id, user_id, pledge_amount, payment_status, notes) VALUES (?, ?, ?, ?, ?)";
        String updateSql = "UPDATE endowment_projects SET current_amount = current_amount + ? WHERE project_id = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin Transaction

            try (PreparedStatement insertPs = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement updatePs = conn.prepareStatement(updateSql)) {

                // 1. Insert Pledge
                insertPs.setInt(1, pledge.getProjectId());
                insertPs.setInt(2, pledge.getUserId());
                insertPs.setDouble(3, pledge.getPledgeAmount());
                insertPs.setString(4, pledge.getPaymentStatus() != null ? pledge.getPaymentStatus() : "Pledged");
                insertPs.setString(5, pledge.getNotes() != null ? pledge.getNotes().trim() : "");
                insertPs.executeUpdate();

                // 2. Update Project Accumulated Amount
                updatePs.setDouble(1, pledge.getPledgeAmount());
                updatePs.setInt(2, pledge.getProjectId());
                updatePs.executeUpdate();

                conn.commit(); // Commit Transaction
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("[EndowmentDAO.createPledge] Error: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    public List<Pledge> getPledgesByProjectId(int projectId) {
        List<Pledge> list = new ArrayList<>();
        String sql = "SELECT p.pledge_id, p.project_id, ep.title AS project_title, p.user_id, " +
                     "u.email AS donor_email, u.role AS donor_role, p.pledge_amount, p.pledge_date, " +
                     "p.payment_status, p.notes, " +
                     "COALESCE(a.full_name, s.full_name, 'Administrator') AS donor_name " +
                     "FROM pledges p " +
                     "JOIN endowment_projects ep ON p.project_id = ep.project_id " +
                     "JOIN users u ON p.user_id = u.user_id " +
                     "LEFT JOIN alumni a ON u.user_id = a.user_id " +
                     "LEFT JOIN students s ON u.user_id = s.user_id " +
                     "WHERE p.project_id = ? ORDER BY p.pledge_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPledge(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[EndowmentDAO.getPledgesByProjectId] Error: " + e.getMessage());
        }
        return list;
    }

    public List<Pledge> getPledgesByUserId(int userId) {
        List<Pledge> list = new ArrayList<>();
        String sql = "SELECT p.pledge_id, p.project_id, ep.title AS project_title, p.user_id, " +
                     "u.email AS donor_email, u.role AS donor_role, p.pledge_amount, p.pledge_date, " +
                     "p.payment_status, p.notes, " +
                     "COALESCE(a.full_name, s.full_name, 'Administrator') AS donor_name " +
                     "FROM pledges p " +
                     "JOIN endowment_projects ep ON p.project_id = ep.project_id " +
                     "JOIN users u ON p.user_id = u.user_id " +
                     "LEFT JOIN alumni a ON u.user_id = a.user_id " +
                     "LEFT JOIN students s ON u.user_id = s.user_id " +
                     "WHERE p.user_id = ? ORDER BY p.pledge_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPledge(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[EndowmentDAO.getPledgesByUserId] Error: " + e.getMessage());
        }
        return list;
    }

    public double getTotalRaisedAmount() {
        String sql = "SELECT COALESCE(SUM(current_amount), 0) FROM endowment_projects";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("[EndowmentDAO.getTotalRaisedAmount] Error: " + e.getMessage());
        }
        return 0.0;
    }

    private EndowmentProject mapProject(ResultSet rs) throws SQLException {
        EndowmentProject ep = new EndowmentProject();
        ep.setProjectId(rs.getInt("project_id"));
        ep.setDeptId(rs.getInt("dept_id"));
        ep.setDeptName(rs.getString("dept_name"));
        ep.setDeptCode(rs.getString("dept_code"));
        ep.setTitle(rs.getString("title"));
        ep.setDescription(rs.getString("description"));
        ep.setTargetAmount(rs.getDouble("target_amount"));
        ep.setCurrentAmount(rs.getDouble("current_amount"));
        ep.setStatus(rs.getString("status"));
        ep.setCreatedAt(rs.getTimestamp("created_at"));
        return ep;
    }

    private Pledge mapPledge(ResultSet rs) throws SQLException {
        Pledge p = new Pledge();
        p.setPledgeId(rs.getInt("pledge_id"));
        p.setProjectId(rs.getInt("project_id"));
        p.setProjectTitle(rs.getString("project_title"));
        p.setUserId(rs.getInt("user_id"));
        p.setDonorName(rs.getString("donor_name"));
        p.setDonorEmail(rs.getString("donor_email"));
        p.setDonorRole(rs.getString("donor_role"));
        p.setPledgeAmount(rs.getDouble("pledge_amount"));
        p.setPledgeDate(rs.getTimestamp("pledge_date"));
        p.setPaymentStatus(rs.getString("payment_status"));
        p.setNotes(rs.getString("notes"));
        return p;
    }
}
