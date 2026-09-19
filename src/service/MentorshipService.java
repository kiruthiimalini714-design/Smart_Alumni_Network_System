package service;

import dao.AlumniDAO;
import dao.MentorshipDAO;
import dao.NotificationDAO;
import dao.StudentDAO;
import model.Alumni;
import model.MentorshipRequest;
import model.Notification;
import model.Student;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MentorshipService {

    private final MentorshipDAO mentorshipDAO = new MentorshipDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final AlumniDAO alumniDAO = new AlumniDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    public Map<String, Object> requestSession(int studentUserId, int alumniId, String topic,
                                             String message, String preferredDate, String preferredTime) {
        Map<String, Object> res = new HashMap<>();

        Student student = studentDAO.findByUserId(studentUserId);
        if (student == null) {
            res.put("success", false);
            res.put("message", "Only registered students can schedule mentorship sessions.");
            return res;
        }

        Alumni alumni = alumniDAO.findByAlumniId(alumniId);
        if (alumni == null) {
            res.put("success", false);
            res.put("message", "Selected alumni mentor not found.");
            return res;
        }

        if (!alumni.isMentorAvailable()) {
            res.put("success", false);
            res.put("message", "This alumnus is currently not accepting new mentorship requests.");
            return res;
        }

        if (topic == null || topic.trim().isEmpty() || message == null || message.trim().isEmpty() ||
            preferredDate == null || preferredDate.trim().isEmpty() || preferredTime == null || preferredTime.trim().isEmpty()) {
            res.put("success", false);
            res.put("message", "Topic, message, preferred date, and preferred time are required.");
            return res;
        }

        MentorshipRequest req = new MentorshipRequest();
        req.setStudentId(student.getStudentId());
        req.setAlumniId(alumni.getAlumniId());
        req.setTopic(topic.trim());
        req.setMessage(message.trim());
        req.setPreferredDate(preferredDate.trim());
        req.setPreferredTime(preferredTime.trim());

        boolean created = mentorshipDAO.createRequest(req);
        if (created) {
            // Notify Alumnus
            Notification notif = new Notification();
            notif.setUserId(alumni.getUserId());
            notif.setTitle("New Mentorship Request");
            notif.setMessage("Student " + student.getFullName() + " requested a mentorship session on '" + topic.trim() + "'.");
            notificationDAO.createNotification(notif);

            res.put("success", true);
            res.put("message", "Mentorship session request sent successfully to " + alumni.getFullName() + "!");
        } else {
            res.put("success", false);
            res.put("message", "Failed to submit mentorship request.");
        }
        return res;
    }

    public List<MentorshipRequest> getSessionsForUser(int userId, String role) {
        if ("Student".equalsIgnoreCase(role)) {
            Student s = studentDAO.findByUserId(userId);
            if (s != null) {
                return mentorshipDAO.getRequestsByStudentId(s.getStudentId());
            }
        } else if ("Alumni".equalsIgnoreCase(role)) {
            Alumni a = alumniDAO.findByUserId(userId);
            if (a != null) {
                return mentorshipDAO.getRequestsByAlumniId(a.getAlumniId());
            }
        }
        return List.of();
    }

    public Map<String, Object> updateSessionStatus(int alumniUserId, int sessionId, String status, String responseNotes) {
        Map<String, Object> res = new HashMap<>();

        Alumni alumni = alumniDAO.findByUserId(alumniUserId);
        if (alumni == null) {
            res.put("success", false);
            res.put("message", "Alumni credentials required.");
            return res;
        }

        MentorshipRequest req = mentorshipDAO.getRequestById(sessionId);
        if (req == null) {
            res.put("success", false);
            res.put("message", "Mentorship request not found.");
            return res;
        }

        if (req.getAlumniId() != alumni.getAlumniId()) {
            res.put("success", false);
            res.put("message", "Unauthorized: You can only respond to requests sent to you.");
            return res;
        }

        if (!"Accepted".equalsIgnoreCase(status) && !"Rejected".equalsIgnoreCase(status) && !"Completed".equalsIgnoreCase(status)) {
            res.put("success", false);
            res.put("message", "Status must be 'Accepted', 'Rejected', or 'Completed'.");
            return res;
        }

        boolean updated = mentorshipDAO.updateStatus(sessionId, status, responseNotes);
        if (updated) {
            // Notify Student
            Student student = studentDAO.findByStudentId(req.getStudentId());
            if (student != null) {
                Notification notif = new Notification();
                notif.setUserId(student.getUserId());
                notif.setTitle("Mentorship Request " + status);
                String noteMsg = (responseNotes != null && !responseNotes.trim().isEmpty()) ? " Note: " + responseNotes.trim() : "";
                notif.setMessage("Alumnus " + alumni.getFullName() + " marked your request on '" + req.getTopic() + "' as " + status + "." + noteMsg);
                notificationDAO.createNotification(notif);
            }

            res.put("success", true);
            res.put("message", "Session status updated to " + status + ".");
        } else {
            res.put("success", false);
            res.put("message", "Failed to update session status.");
        }
        return res;
    }
}
