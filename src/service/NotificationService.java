package service;

import dao.NotificationDAO;
import model.Notification;

import java.util.List;

public class NotificationService {

    private final NotificationDAO notificationDAO = new NotificationDAO();

    public List<Notification> getUserNotifications(int userId) {
        return notificationDAO.getNotificationsByUserId(userId);
    }

    public boolean markRead(int notificationId, int userId) {
        return notificationDAO.markAsRead(notificationId, userId);
    }
}
