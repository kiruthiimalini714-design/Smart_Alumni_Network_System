package service;

import dao.AlumniDAO;
import dao.DepartmentDAO;
import dao.EndowmentDAO;
import dao.JobDAO;
import dao.MentorshipDAO;
import dao.UserDAO;

import java.util.HashMap;
import java.util.Map;

public class StatsService {

    private final UserDAO userDAO = new UserDAO();
    private final JobDAO jobDAO = new JobDAO();
    private final MentorshipDAO mentorshipDAO = new MentorshipDAO();
    private final EndowmentDAO endowmentDAO = new EndowmentDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", userDAO.getCountByRole("Student"));
        stats.put("totalAlumni", userDAO.getCountByRole("Alumni"));
        stats.put("totalJobs", jobDAO.getTotalJobsCount());
        stats.put("totalMentorships", mentorshipDAO.getTotalMentorshipCount());
        stats.put("totalEndowmentRaised", endowmentDAO.getTotalRaisedAmount());
        stats.put("totalDepartments", departmentDAO.getAllDepartments().size());
        return stats;
    }
}
