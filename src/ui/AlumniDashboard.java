package ui;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import database.DBConnection;

public class AlumniDashboard extends JFrame {

    JButton updateProfile, postJob, viewStudents, mentorshipRequests,
            viewEvents, notifications, logout;
    JLabel title;

    public AlumniDashboard() {

        setTitle("Alumni Dashboard");
        setSize(500,500);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        title = new JLabel("WELCOME ALUMNI");
        title.setBounds(170,20,200,30);

        updateProfile = new JButton("Update Profile");
        updateProfile.setBounds(140,70,200,30);

        postJob = new JButton("Post Job");
        postJob.setBounds(140,120,200,30);

        viewStudents = new JButton("View Students");
        viewStudents.setBounds(140,170,200,30);

        mentorshipRequests = new JButton("Mentorship Requests");
        mentorshipRequests.setBounds(140,220,200,30);

        viewEvents = new JButton("View Events");
        viewEvents.setBounds(140,270,200,30);

        notifications = new JButton("Notifications");
        notifications.setBounds(140,320,200,30);

        logout = new JButton("Logout");
        logout.setBounds(140,370,200,30);

        add(title);
        add(updateProfile);
        add(postJob);
        add(viewStudents);
        add(mentorshipRequests);
        add(viewEvents);
        add(notifications);
        add(logout);
        updateProfile.addActionListener(e -> {

            JFrame frame = new JFrame("Update Alumni Profile");
            frame.setSize(500, 500);
            frame.setLayout(null);
            frame.setLocationRelativeTo(null);

            JLabel nameLabel = new JLabel("Name:");
            nameLabel.setBounds(50, 50, 120, 25);

            JTextField nameField = new JTextField();
            nameField.setBounds(180, 50, 220, 25);

            JLabel deptLabel = new JLabel("Department:");
            deptLabel.setBounds(50, 90, 120, 25);

            JTextField deptField = new JTextField();
            deptField.setBounds(180, 90, 220, 25);

            JLabel batchLabel = new JLabel("Batch Year:");
            batchLabel.setBounds(50, 130, 120, 25);

            JTextField batchField = new JTextField();
            batchField.setBounds(180, 130, 220, 25);

            JLabel companyLabel = new JLabel("Company:");
            companyLabel.setBounds(50, 170, 120, 25);

            JTextField companyField = new JTextField();
            companyField.setBounds(180, 170, 220, 25);

            JLabel designationLabel = new JLabel("Designation:");
            designationLabel.setBounds(50, 210, 120, 25);

            JTextField designationField = new JTextField();
            designationField.setBounds(180, 210, 220, 25);

            JLabel phoneLabel = new JLabel("Phone:");
            phoneLabel.setBounds(50, 250, 120, 25);

            JTextField phoneField = new JTextField();
            phoneField.setBounds(180, 250, 220, 25);

            JButton updateButton = new JButton("Update");
            updateButton.setBounds(180, 310, 100, 30);

            frame.add(nameLabel);
            frame.add(nameField);
            frame.add(deptLabel);
            frame.add(deptField);
            frame.add(batchLabel);
            frame.add(batchField);
            frame.add(companyLabel);
            frame.add(companyField);
            frame.add(designationLabel);
            frame.add(designationField);
            frame.add(phoneLabel);
            frame.add(phoneField);
            frame.add(updateButton);

            try {

                Connection con = DBConnection.getConnection();

                String sql =
                        "SELECT name, department, batch_year, company, " +
                                "designation, phone FROM alumni " +
                                "ORDER BY alumni_id DESC LIMIT 1";

                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {

                    nameField.setText(rs.getString("name"));
                    deptField.setText(rs.getString("department"));
                    batchField.setText(String.valueOf(rs.getInt("batch_year")));
                    companyField.setText(rs.getString("company"));
                    designationField.setText(rs.getString("designation"));
                    phoneField.setText(rs.getString("phone"));
                }

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Unable to load profile: " + ex.getMessage()
                );

            }

            updateButton.addActionListener(ev -> {

                try {

                    Connection con = DBConnection.getConnection();

                    String sql =
                            "UPDATE alumni SET name=?, department=?, " +
                                    "batch_year=?, company=?, designation=?, phone=? " +
                                    "WHERE alumni_id = " +
                                    "(SELECT alumni_id FROM alumni " +
                                    "ORDER BY alumni_id DESC LIMIT 1)";

                    PreparedStatement ps = con.prepareStatement(sql);

                    ps.setString(1, nameField.getText().trim());
                    ps.setString(2, deptField.getText().trim());
                    ps.setInt(3, Integer.parseInt(batchField.getText().trim()));
                    ps.setString(4, companyField.getText().trim());
                    ps.setString(5, designationField.getText().trim());
                    ps.setString(6, phoneField.getText().trim());

                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(
                            frame,
                            "Profile updated successfully!"
                    );

                    frame.dispose();

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Error updating profile: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );

                }

            });

            frame.setVisible(true);

        });
        postJob.addActionListener(e -> {

            JFrame frame = new JFrame("Post Job");
            frame.setSize(500, 500);
            frame.setLayout(null);
            frame.setLocationRelativeTo(null);

            JLabel titleLabel = new JLabel("POST JOB");
            titleLabel.setBounds(200, 20, 120, 30);

            JLabel jobLabel = new JLabel("Job Title:");
            jobLabel.setBounds(50, 70, 120, 25);

            JTextField jobField = new JTextField();
            jobField.setBounds(180, 70, 220, 25);

            JLabel companyLabel = new JLabel("Company:");
            companyLabel.setBounds(50, 110, 120, 25);

            JTextField companyField = new JTextField();
            companyField.setBounds(180, 110, 220, 25);

            JLabel locationLabel = new JLabel("Location:");
            locationLabel.setBounds(50, 150, 120, 25);

            JTextField locationField = new JTextField();
            locationField.setBounds(180, 150, 220, 25);

            JLabel salaryLabel = new JLabel("Salary:");
            salaryLabel.setBounds(50, 190, 120, 25);

            JTextField salaryField = new JTextField();
            salaryField.setBounds(180, 190, 220, 25);

            JLabel descriptionLabel = new JLabel("Description:");
            descriptionLabel.setBounds(50, 230, 120, 25);

            JTextArea descriptionArea = new JTextArea();
            descriptionArea.setLineWrap(true);
            descriptionArea.setWrapStyleWord(true);

            JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
            descriptionScroll.setBounds(180, 230, 220, 80);

            JButton postButton = new JButton("Post Job");
            postButton.setBounds(180, 350, 100, 30);

            frame.add(titleLabel);
            frame.add(jobLabel);
            frame.add(jobField);
            frame.add(companyLabel);
            frame.add(companyField);
            frame.add(locationLabel);
            frame.add(locationField);
            frame.add(salaryLabel);
            frame.add(salaryField);
            frame.add(descriptionLabel);
            frame.add(descriptionScroll);
            frame.add(postButton);

            postButton.addActionListener(ev -> {

                if (jobField.getText().trim().isEmpty() ||
                        companyField.getText().trim().isEmpty() ||
                        locationField.getText().trim().isEmpty()) {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Please fill Job Title, Company and Location!"
                    );

                    return;
                }

                try {

                    Connection con = DBConnection.getConnection();

                    String sql =
                            "INSERT INTO jobs " +
                                    "(job_title, company, location, salary, description) " +
                                    "VALUES (?, ?, ?, ?, ?)";

                    PreparedStatement ps = con.prepareStatement(sql);

                    ps.setString(1, jobField.getText().trim());
                    ps.setString(2, companyField.getText().trim());
                    ps.setString(3, locationField.getText().trim());
                    ps.setString(4, salaryField.getText().trim());
                    ps.setString(5, descriptionArea.getText().trim());

                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(
                            frame,
                            "Job posted successfully!"
                    );

                    frame.dispose();

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Error posting job: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );

                }

            });

            frame.setVisible(true);

        });
        viewStudents.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "View Students");
        });
        mentorshipRequests.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Mentorship Requests");
        });
        viewEvents.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "View Events");
        });
        notifications.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Notifications");
        });
        logout.addActionListener(e -> {
            new LoginForm();
            dispose();
        });
        setVisible(true);
    }
}