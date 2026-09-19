package service;

import dao.AlumniDAO;
import dao.DepartmentDAO;
import model.Alumni;
import model.Department;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlumniService {

    private final AlumniDAO alumniDAO = new AlumniDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();

    public List<Alumni> searchAlumni(String query, Integer deptId, Integer batchYear, String company, Boolean mentorOnly) {
        return alumniDAO.searchAlumni(query, deptId, batchYear, company, mentorOnly);
    }

    public Alumni getAlumniById(int alumniId) {
        return alumniDAO.findByAlumniId(alumniId);
    }

    public Alumni getAlumniByUserId(int userId) {
        return alumniDAO.findByUserId(userId);
    }

    public Map<String, Object> updateProfile(int userId, String company, String designation,
                                            String location, String phone, String linkedinUrl,
                                            boolean isMentorAvailable) {
        Map<String, Object> res = new HashMap<>();

        Alumni existing = alumniDAO.findByUserId(userId);
        if (existing == null) {
            res.put("success", false);
            res.put("message", "Alumni profile not found for current user.");
            return res;
        }

        if (company == null || company.trim().isEmpty() || designation == null || designation.trim().isEmpty()) {
            res.put("success", false);
            res.put("message", "Company and designation cannot be blank.");
            return res;
        }

        if (phone != null && !phone.trim().isEmpty() && !phone.trim().matches("\\d{10}")) {
            res.put("success", false);
            res.put("message", "Phone number must be exactly 10 digits.");
            return res;
        }

        existing.setCompany(company.trim());
        existing.setDesignation(designation.trim());
        existing.setLocation(location != null ? location.trim() : "");
        if (phone != null && !phone.trim().isEmpty()) {
            existing.setPhone(phone.trim());
        }
        existing.setLinkedinUrl(linkedinUrl != null ? linkedinUrl.trim() : "");
        existing.setMentorAvailable(isMentorAvailable);

        boolean updated = alumniDAO.updateAlumni(existing);
        res.put("success", updated);
        res.put("message", updated ? "Profile updated successfully!" : "Failed to update profile.");
        return res;
    }

    public List<Department> getAllDepartments() {
        return departmentDAO.getAllDepartments();
    }
}
