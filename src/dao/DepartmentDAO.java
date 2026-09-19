package dao;

import model.Department;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {

    public List<Department> getAllDepartments() {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT dept_id, dept_code, dept_name FROM departments ORDER BY dept_name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Department(
                        rs.getInt("dept_id"),
                        rs.getString("dept_code"),
                        rs.getString("dept_name")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[DepartmentDAO.getAllDepartments] Error: " + e.getMessage());
        }
        return list;
    }

    public Department getDepartmentById(int deptId) {
        String sql = "SELECT dept_id, dept_code, dept_name FROM departments WHERE dept_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, deptId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Department(
                            rs.getInt("dept_id"),
                            rs.getString("dept_code"),
                            rs.getString("dept_name")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[DepartmentDAO.getDepartmentById] Error: " + e.getMessage());
        }
        return null;
    }
}
