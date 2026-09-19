package service;

import dao.*;
import model.*;
import util.PasswordUtil;

import java.util.HashMap;
import java.util.Map;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final AlumniDAO alumniDAO = new AlumniDAO();
    private final VerifiedGraduateDAO verifiedGraduateDAO = new VerifiedGraduateDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    public Map<String, Object> login(String email, String password, String requestedRole) {
        Map<String, Object> result = new HashMap<>();

        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            result.put("success", false);
            result.put("message", "Email and password are required.");
            return result;
        }

        User user = userDAO.findByEmail(email.trim());
        if (user == null) {
            result.put("success", false);
            result.put("message", "Invalid email or password.");
            return result;
        }

        if (!"Active".equalsIgnoreCase(user.getStatus())) {
            result.put("success", false);
            result.put("message", "Your account has been deactivated or suspended.");
            return result;
        }

        // Role verification
        if (requestedRole != null && !requestedRole.trim().isEmpty() && !user.getRole().equalsIgnoreCase(requestedRole.trim())) {
            result.put("success", false);
            result.put("message", "User exists, but is registered as " + user.getRole() + ", not " + requestedRole + ".");
            return result;
        }

        // Password verification using salted SHA-256
        boolean valid = PasswordUtil.verifyPassword(password, user.getPasswordHash(), user.getSalt());
        if (!valid) {
            result.put("success", false);
            result.put("message", "Invalid email or password.");
            return result;
        }

        // Build authenticated profile payload
        result.put("success", true);
        result.put("message", "Login successful!");
        result.put("userId", user.getUserId());
        result.put("email", user.getEmail());
        result.put("role", user.getRole());

        if ("Student".equalsIgnoreCase(user.getRole())) {
            Student s = studentDAO.findByUserId(user.getUserId());
            if (s != null) {
                result.put("profileId", s.getStudentId());
                result.put("name", s.getFullName());
                result.put("registerNo", s.getRegisterNo());
                result.put("deptName", s.getDeptName());
                result.put("currentYear", s.getCurrentYear());
            }
        } else if ("Alumni".equalsIgnoreCase(user.getRole())) {
            Alumni a = alumniDAO.findByUserId(user.getUserId());
            if (a != null) {
                result.put("profileId", a.getAlumniId());
                result.put("name", a.getFullName());
                result.put("registerNo", a.getRegisterNo());
                result.put("deptName", a.getDeptName());
                result.put("company", a.getCompany());
                result.put("designation", a.getDesignation());
                result.put("isMentorAvailable", a.isMentorAvailable());
            }
        } else if ("Admin".equalsIgnoreCase(user.getRole())) {
            result.put("name", "System Administrator");
        }

        return result;
    }

    public Map<String, Object> verifyAlumniEligibility(String registerNo, int deptId, int batchYear) {
        Map<String, Object> res = new HashMap<>();
        if (registerNo == null || registerNo.trim().isEmpty() || deptId <= 0 || batchYear <= 0) {
            res.put("verified", false);
            res.put("message", "Register number, department, and graduation year are required.");
            return res;
        }

        VerifiedGraduate vg = verifiedGraduateDAO.verifyGraduate(registerNo.trim(), deptId, batchYear);
        if (vg == null) {
            res.put("verified", false);
            res.put("message", "No matching graduation record found in college registry.");
            return res;
        }
        if (vg.isRegistered()) {
            res.put("verified", false);
            res.put("message", "An alumni account is already registered with this register number (" + registerNo + ").");
            return res;
        }

        res.put("verified", true);
        res.put("fullName", vg.getFullName());
        res.put("degree", vg.getDegree());
        res.put("deptName", vg.getDeptName());
        res.put("message", "Official institutional graduate record verified!");
        return res;
    }

    public Map<String, Object> registerStudent(String email, String password, String name,
                                              String registerNo, int deptId, int currentYear,
                                              String phone, String bio) {
        Map<String, Object> res = new HashMap<>();

        // Validation
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            res.put("success", false);
            res.put("message", "Please provide a valid email address.");
            return res;
        }
        if (password == null || password.length() < 6) {
            res.put("success", false);
            res.put("message", "Password must be at least 6 characters long.");
            return res;
        }
        if (name == null || name.trim().isEmpty() || registerNo == null || registerNo.trim().isEmpty()) {
            res.put("success", false);
            res.put("message", "Name and register number are required.");
            return res;
        }
        if (phone == null || !phone.matches("\\d{10}")) {
            res.put("success", false);
            res.put("message", "Phone number must be exactly 10 digits.");
            return res;
        }
        if (currentYear < 1 || currentYear > 5) {
            res.put("success", false);
            res.put("message", "Academic year must be between 1 and 5.");
            return res;
        }

        // Check if email already registered
        if (userDAO.findByEmail(email) != null) {
            res.put("success", false);
            res.put("message", "Email address is already registered.");
            return res;
        }

        // Create user record
        String salt = PasswordUtil.generateSalt(16);
        String hash = PasswordUtil.hashPassword(password, salt);

        User user = new User();
        user.setEmail(email.trim().toLowerCase());
        user.setPasswordHash(hash);
        user.setSalt(salt);
        user.setRole("Student");
        user.setStatus("Active");

        int userId = userDAO.createUser(user);
        if (userId <= 0) {
            res.put("success", false);
            res.put("message", "Failed to create student account.");
            return res;
        }

        // Create student profile
        Student s = new Student();
        s.setUserId(userId);
        s.setRegisterNo(registerNo.trim());
        s.setFullName(name.trim());
        s.setDeptId(deptId);
        s.setCurrentYear(currentYear);
        s.setPhone(phone.trim());
        s.setBio(bio != null ? bio.trim() : "");

        boolean created = studentDAO.createStudent(s);
        if (!created) {
            res.put("success", false);
            res.put("message", "Student profile creation failed (duplicate register number).");
            return res;
        }

        // Send welcome notification
        Notification notif = new Notification();
        notif.setUserId(userId);
        notif.setTitle("Welcome to Smart Alumni Network!");
        notif.setMessage("Hello " + name.trim() + ", your student account has been registered successfully.");
        notificationDAO.createNotification(notif);

        res.put("success", true);
        res.put("message", "Registration successful! You can now log in.");
        return res;
    }

    public Map<String, Object> registerAlumni(String email, String password, String name,
                                             String registerNo, int deptId, int batchYear,
                                             String company, String designation, String location,
                                             String phone, String linkedinUrl, boolean isMentorAvailable) {
        Map<String, Object> res = new HashMap<>();

        // Validation
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            res.put("success", false);
            res.put("message", "Please enter a valid email address.");
            return res;
        }
        if (password == null || password.length() < 6) {
            res.put("success", false);
            res.put("message", "Password must be at least 6 characters.");
            return res;
        }
        if (registerNo == null || registerNo.trim().isEmpty()) {
            res.put("success", false);
            res.put("message", "Graduation register number is required.");
            return res;
        }
        if (phone == null || !phone.matches("\\d{10}")) {
            res.put("success", false);
            res.put("message", "Phone number must be exactly 10 digits.");
            return res;
        }
        if (company == null || company.trim().isEmpty() || designation == null || designation.trim().isEmpty()) {
            res.put("success", false);
            res.put("message", "Company and designation are required.");
            return res;
        }

        // Mandatory Alumni Verification (FR-01)
        VerifiedGraduate vg = verifiedGraduateDAO.verifyGraduate(registerNo.trim(), deptId, batchYear);
        if (vg == null) {
            res.put("success", false);
            res.put("message", "Verification Failed: Register number " + registerNo + " was not found in college graduation records for department and batch year.");
            return res;
        }
        if (vg.isRegistered()) {
            res.put("success", false);
            res.put("message", "This graduate register number is already registered.");
            return res;
        }

        // Email uniqueness
        if (userDAO.findByEmail(email) != null) {
            res.put("success", false);
            res.put("message", "Email address is already in use.");
            return res;
        }

        // Create user
        String salt = PasswordUtil.generateSalt(16);
        String hash = PasswordUtil.hashPassword(password, salt);

        User user = new User();
        user.setEmail(email.trim().toLowerCase());
        user.setPasswordHash(hash);
        user.setSalt(salt);
        user.setRole("Alumni");
        user.setStatus("Active");

        int userId = userDAO.createUser(user);
        if (userId <= 0) {
            res.put("success", false);
            res.put("message", "Failed to create alumni account.");
            return res;
        }

        // Create alumni profile
        Alumni a = new Alumni();
        a.setUserId(userId);
        a.setRegisterNo(registerNo.trim());
        a.setFullName(name != null && !name.trim().isEmpty() ? name.trim() : vg.getFullName());
        a.setDeptId(deptId);
        a.setBatchYear(batchYear);
        a.setCompany(company.trim());
        a.setDesignation(designation.trim());
        a.setLocation(location != null ? location.trim() : "");
        a.setPhone(phone.trim());
        a.setLinkedinUrl(linkedinUrl != null ? linkedinUrl.trim() : "");
        a.setMentorAvailable(isMentorAvailable);

        boolean created = alumniDAO.createAlumni(a);
        if (!created) {
            res.put("success", false);
            res.put("message", "Alumni profile creation failed.");
            return res;
        }

        // Mark graduate registry as registered
        verifiedGraduateDAO.markAsRegistered(registerNo.trim());

        // Welcome notification
        Notification notif = new Notification();
        notif.setUserId(userId);
        notif.setTitle("Alumni Account Verified & Active");
        notif.setMessage("Welcome back to your alma mater! Your verified alumni credentials are now active.");
        notificationDAO.createNotification(notif);

        res.put("success", true);
        res.put("message", "Verified Alumni Registration Successful! You can now log in.");
        return res;
    }
}
