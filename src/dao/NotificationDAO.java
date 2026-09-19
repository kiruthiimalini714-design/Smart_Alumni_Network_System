package dao;

import model.Notification;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public boolean createNotification(Notification n) {
        String sql = "INSERT INTO notifications (user_id, title, message, is_read) VALUES (?, ?, ?, FALSE)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, n.getUserId());
            ps.setString(2, n.getTitle().trim());
            ps.setString(3, n.getMessage().trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[NotificationDAO.createNotification] Error: " + e.getMessage());
            return false;
        }
    }

    public List<Notification> getNotificationsByUserId(int userId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT notification_id, user_id, title, message, is_read, created_at " +
                     "FROM notifications WHERE user_id = ? ORDER BY created_at DESC LIMIT 50";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Notification(
                            rs.getInt("notification_id"),
                            rs.getInt("user_id"),
                            rs.getString("title"),
                            rs.getString("message"),
                            rs.getBoolean("is_read"),
                            rs.getTimestamp("created_at")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("[NotificationDAO.getNotificationsByUserId] Error: " + e.getMessage());
        }
        return list;
    }

    public boolean markAsRead(int notificationId, int userId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE notification_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[NotificationDAO.markAsRead] Error: " + e.getMessage());
            return false;
        }
    }
}
