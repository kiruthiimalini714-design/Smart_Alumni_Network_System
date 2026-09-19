package dao;

import model.Student;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public boolean createStudent(Student s) {
        String sql = "INSERT INTO students (user_id, register_no, full_name, dept_id, current_year, phone, bio) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, s.getUserId());
            ps.setString(2, s.getRegisterNo().trim());
            ps.setString(3, s.getFullName().trim());
            ps.setInt(4, s.getDeptId());
            ps.setInt(5, s.getCurrentYear());
            ps.setString(6, s.getPhone().trim());
            ps.setString(7, s.getBio() != null ? s.getBio().trim() : "");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[StudentDAO.createStudent] Error: " + e.getMessage());
            return false;
        }
    }

    public Student findByUserId(int userId) {
        String sql = "SELECT s.student_id, s.user_id, s.register_no, s.full_name, s.dept_id, " +
                     "d.dept_name, d.dept_code, s.current_year, s.phone, s.bio, u.email, s.created_at " +
                     "FROM students s " +
                     "JOIN departments d ON s.dept_id = d.dept_id " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "WHERE s.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapStudent(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[StudentDAO.findByUserId] Error: " + e.getMessage());
        }
        return null;
    }

    public Student findByStudentId(int studentId) {
        String sql = "SELECT s.student_id, s.user_id, s.register_no, s.full_name, s.dept_id, " +
                     "d.dept_name, d.dept_code, s.current_year, s.phone, s.bio, u.email, s.created_at " +
                     "FROM students s " +
                     "JOIN departments d ON s.dept_id = d.dept_id " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "WHERE s.student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapStudent(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[StudentDAO.findByStudentId] Error: " + e.getMessage());
        }
        return null;
    }

    public boolean updateStudent(Student s) {
        String sql = "UPDATE students SET full_name = ?, phone = ?, current_year = ?, bio = ? WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getFullName());
            ps.setString(2, s.getPhone());
            ps.setInt(3, s.getCurrentYear());
            ps.setString(4, s.getBio());
            ps.setInt(5, s.getStudentId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[StudentDAO.updateStudent] Error: " + e.getMessage());
            return false;
        }
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.student_id, s.user_id, s.register_no, s.full_name, s.dept_id, " +
                     "d.dept_name, d.dept_code, s.current_year, s.phone, s.bio, u.email, s.created_at " +
                     "FROM students s " +
                     "JOIN departments d ON s.dept_id = d.dept_id " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "ORDER BY s.full_name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapStudent(rs));
            }
        } catch (SQLException e) {
            System.err.println("[StudentDAO.getAllStudents] Error: " + e.getMessage());
        }
        return list;
    }

    private Student mapStudent(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getInt("student_id"));
        s.setUserId(rs.getInt("user_id"));
        s.setRegisterNo(rs.getString("register_no"));
        s.setFullName(rs.getString("full_name"));
        s.setDeptId(rs.getInt("dept_id"));
        s.setDeptName(rs.getString("dept_name"));
        s.setDeptCode(rs.getString("dept_code"));
        s.setCurrentYear(rs.getInt("current_year"));
        s.setPhone(rs.getString("phone"));
        s.setBio(rs.getString("bio"));
        s.setEmail(rs.getString("email"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        return s;
    }
}
