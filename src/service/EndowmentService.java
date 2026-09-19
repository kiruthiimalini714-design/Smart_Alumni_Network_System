package service;

import dao.EndowmentDAO;
import dao.NotificationDAO;
import model.EndowmentProject;
import model.Notification;
import model.Pledge;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EndowmentService {

    private final EndowmentDAO endowmentDAO = new EndowmentDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    public List<EndowmentProject> getAllProjects() {
        return endowmentDAO.getAllProjects();
    }

    public Map<String, Object> getProjectDetails(int projectId) {
        Map<String, Object> res = new HashMap<>();
        EndowmentProject project = endowmentDAO.getProjectById(projectId);
        if (project == null) {
            res.put("success", false);
            res.put("message", "Project not found.");
            return res;
        }

        List<Pledge> pledges = endowmentDAO.getPledgesByProjectId(projectId);
        res.put("success", true);
        res.put("project", project);
        res.put("pledges", pledges);
        return res;
    }

    public Map<String, Object> createPledge(int userId, int projectId, double amount, String notes) {
        Map<String, Object> res = new HashMap<>();

        if (amount <= 0) {
            res.put("success", false);
            res.put("message", "Pledge amount must be greater than zero.");
            return res;
        }

        EndowmentProject project = endowmentDAO.getProjectById(projectId);
        if (project == null) {
            res.put("success", false);
            res.put("message", "Endowment campaign not found.");
            return res;
        }

        Pledge pledge = new Pledge();
        pledge.setProjectId(projectId);
        pledge.setUserId(userId);
        pledge.setPledgeAmount(amount);
        pledge.setPaymentStatus("Pledged");
        pledge.setNotes(notes != null ? notes.trim() : "");

        boolean created = endowmentDAO.createPledge(pledge);
        if (created) {
            // Notification
            Notification notif = new Notification();
            notif.setUserId(userId);
            notif.setTitle("Pledge Acknowledgment");
            notif.setMessage("Thank you! Your pledge of ₹" + String.format("%,.2f", amount) + " towards '" + project.getTitle() + "' has been recorded.");
            notificationDAO.createNotification(notif);

            res.put("success", true);
            res.put("message", "Thank you! Your pledge of ₹" + String.format("%,.2f", amount) + " has been recorded successfully.");
        } else {
            res.put("success", false);
            res.put("message", "Failed to record pledge commitment.");
        }
        return res;
    }

    public List<Pledge> getUserPledges(int userId) {
        return endowmentDAO.getPledgesByUserId(userId);
    }

    public double getTotalRaised() {
        return endowmentDAO.getTotalRaisedAmount();
    }
}
