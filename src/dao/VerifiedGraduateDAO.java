package dao;

import model.VerifiedGraduate;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VerifiedGraduateDAO {

    public VerifiedGraduate verifyGraduate(String registerNo, int deptId, int batchYear) {
        String sql = "SELECT vg.grad_id, vg.register_no, vg.full_name, vg.dept_id, d.dept_name, vg.batch_year, vg.degree, vg.is_registered " +
                     "FROM verified_graduates vg " +
                     "JOIN departments d ON vg.dept_id = d.dept_id " +
                     "WHERE LOWER(TRIM(vg.register_no)) = LOWER(TRIM(?)) " +
                     "AND vg.dept_id = ? " +
                     "AND vg.batch_year = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, registerNo);
            ps.setInt(2, deptId);
            ps.setInt(3, batchYear);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new VerifiedGraduate(
                            rs.getInt("grad_id"),
                            rs.getString("register_no"),
                            rs.getString("full_name"),
                            rs.getInt("dept_id"),
                            rs.getString("dept_name"),
                            rs.getInt("batch_year"),
                            rs.getString("degree"),
                            rs.getBoolean("is_registered")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[VerifiedGraduateDAO.verifyGraduate] Error: " + e.getMessage());
        }
        return null;
    }

    public boolean markAsRegistered(String registerNo) {
        String sql = "UPDATE verified_graduates SET is_registered = TRUE WHERE LOWER(TRIM(register_no)) = LOWER(TRIM(?))";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, registerNo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[VerifiedGraduateDAO.markAsRegistered] Error: " + e.getMessage());
            return false;
        }
    }
}
