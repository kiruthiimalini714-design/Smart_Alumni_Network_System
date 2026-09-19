package dao;

import model.User;
import util.DBConnection;

import java.sql.*;

public class UserDAO {

    public User findByEmail(String email) {
        String sql = "SELECT user_id, email, password_hash, salt, role, status, created_at FROM users WHERE LOWER(email) = LOWER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("email"),
                            rs.getString("password_hash"),
                            rs.getString("salt"),
                            rs.getString("role"),
                            rs.getString("status"),
                            rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO.findByEmail] Error: " + e.getMessage());
        }
        return null;
    }

    public User findById(int userId) {
        String sql = "SELECT user_id, email, password_hash, salt, role, status, created_at FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("email"),
                            rs.getString("password_hash"),
                            rs.getString("salt"),
                            rs.getString("role"),
                            rs.getString("status"),
                            rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO.findById] Error: " + e.getMessage());
        }
        return null;
    }

    public int createUser(User user) {
        String sql = "INSERT INTO users (email, password_hash, salt, role, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getEmail().trim());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getSalt());
            ps.setString(4, user.getRole());
            ps.setString(5, user.getStatus() != null ? user.getStatus() : "Active");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO.createUser] Error: " + e.getMessage());
        }
        return -1;
    }

    public int getCountByRole(String role) {
        String sql = role == null ? "SELECT COUNT(*) FROM users" : "SELECT COUNT(*) FROM users WHERE role = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (role != null) {
                ps.setString(1, role);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO.getCountByRole] Error: " + e.getMessage());
        }
        return 0;
    }
}
