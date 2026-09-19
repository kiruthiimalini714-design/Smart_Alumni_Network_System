package ui;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import database.DBConnection;

public class StudentDashboard extends JFrame {

    JButton profile, search, jobs, events, mentorship, notifications, logout;
    JLabel title;

    public StudentDashboard() {

        setTitle("Student Dashboard");
        setSize(500,500);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        title = new JLabel("WELCOME STUDENT");
        title.setBounds(170,20,200,30);

        profile = new JButton("View Profile");
        profile.setBounds(140,70,200,30);

        search = new JButton("Search Alumni");
        search.setBounds(140,120,200,30);

        jobs = new JButton("View Jobs");
        jobs.setBounds(140,170,200,30);

        events = new JButton("View Events");
        events.setBounds(140,220,200,30);

        mentorship = new JButton("Request Mentorship");
        mentorship.setBounds(140,270,200,30);

        notifications = new JButton("Notifications");
        notifications.setBounds(140,320,200,30);

        logout = new JButton("Logout");
        logout.setBounds(140,370,200,30);

        add(title);
        add(profile);
        add(search);
        add(jobs);
        add(events);
        add(mentorship);
        add(notifications);
        add(logout);
        profile.addActionListener(e -> {

            JFrame frame = new JFrame("Student Profile");
            frame.setSize(400, 350);
            frame.setLayout(null);
            frame.setLocationRelativeTo(null);

            JLabel heading = new JLabel("STUDENT PROFILE");
            heading.setBounds(130, 20, 180, 30);

            JLabel details = new JLabel();
            details.setBounds(50, 70, 300, 180);

            try {

                Connection con = DBConnection.getConnection();

                String sql = "SELECT name,email,department,year,phone " +
                        "FROM students ORDER BY student_id DESC LIMIT 1";

                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {

                    details.setText(
                            "<html>" +
                                    "Name: " + rs.getString("name") + "<br><br>" +
                                    "Email: " + rs.getString("email") + "<br><br>" +
                                    "Department: " + rs.getString("department") + "<br><br>" +
                                    "Year: " + rs.getInt("year") + "<br><br>" +
                                    "Phone: " + rs.getString("phone") +
                                    "</html>"
                    );

                } else {

                    details.setText("No student profile found.");

                }

            } catch (Exception ex) {

                details.setText("Unable to load profile.");

                ex.printStackTrace();

            }

            frame.add(heading);
            frame.add(details);

            frame.setVisible(true);

        });
        search.addActionListener(e -> {

            JFrame frame = new JFrame("Search Alumni");
            frame.setSize(600, 450);
            frame.setLayout(null);
            frame.setLocationRelativeTo(null);

            JLabel label = new JLabel("Search by Name / Department / Company:");
            label.setBounds(30, 30, 250, 30);

            JTextField searchField = new JTextField();
            searchField.setBounds(280, 30, 200, 30);

            JButton searchButton = new JButton("Search");
            searchButton.setBounds(490, 30, 80, 30);

            JTextArea resultArea = new JTextArea();
            resultArea.setBounds(30, 80, 540, 280);
            resultArea.setEditable(false);

            frame.add(label);
            frame.add(searchField);
            frame.add(searchButton);
            frame.add(resultArea);

            searchButton.addActionListener(ev -> {

                String keyword = searchField.getText().trim();

                if (keyword.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            frame,
                            "Please enter something to search."
                    );
                    return;
                }

                try {

                    Connection con = DBConnection.getConnection();

                    String sql =
                            "SELECT name, email, department, batch_year, company, designation " +
                                    "FROM alumni " +
                                    "WHERE name LIKE ? " +
                                    "OR department LIKE ? " +
                                    "OR company LIKE ?";

                    PreparedStatement ps = con.prepareStatement(sql);

                    String value = "%" + keyword + "%";

                    ps.setString(1, value);
                    ps.setString(2, value);
                    ps.setString(3, value);

                    ResultSet rs = ps.executeQuery();

                    StringBuilder results = new StringBuilder();

                    while (rs.next()) {

                        results.append("Name: ")
                                .append(rs.getString("name"))
                                .append("\n");

                        results.append("Email: ")
                                .append(rs.getString("email"))
                                .append("\n");

                        results.append("Department: ")
                                .append(rs.getString("department"))
                                .append("\n");

                        results.append("Batch: ")
                                .append(rs.getInt("batch_year"))
                                .append("\n");

                        results.append("Company: ")
                                .append(rs.getString("company"))
                                .append("\n");

                        results.append("Designation: ")
                                .append(rs.getString("designation"))
                                .append("\n");

                        results.append("-----------------------------\n");
                    }

                    if (results.length() == 0) {
                        resultArea.setText("No alumni found.");
                    } else {
                        resultArea.setText(results.toString());
                    }

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Error searching alumni: " + ex.getMessage()
                    );

                }

            });

            frame.setVisible(true);

        });
        jobs.addActionListener(e -> {

            JFrame frame = new JFrame("Available Jobs");
            frame.setSize(650, 500);
            frame.setLayout(null);
            frame.setLocationRelativeTo(null);

            JLabel titleLabel = new JLabel("AVAILABLE JOB OPPORTUNITIES");
            titleLabel.setBounds(220, 20, 250, 30);

            JTextArea jobArea = new JTextArea();
            jobArea.setBounds(30, 70, 570, 330);
            jobArea.setEditable(false);

            JScrollPane scrollPane = new JScrollPane(jobArea);
            scrollPane.setBounds(30, 70, 570, 330);

            JButton closeButton = new JButton("Close");
            closeButton.setBounds(270, 420, 100, 30);

            frame.add(titleLabel);
            frame.add(scrollPane);
            frame.add(closeButton);

            try {

                Connection con = DBConnection.getConnection();

                String sql = "SELECT job_title, company, location, salary, description FROM jobs";

                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                StringBuilder jobsList = new StringBuilder();

                while (rs.next()) {

                    jobsList.append("Job Title: ")
                            .append(rs.getString("job_title"))
                            .append("\n");

                    jobsList.append("Company: ")
                            .append(rs.getString("company"))
                            .append("\n");

                    jobsList.append("Location: ")
                            .append(rs.getString("location"))
                            .append("\n");

                    jobsList.append("Salary: ")
                            .append(rs.getString("salary"))
                            .append("\n");

                    jobsList.append("Description: ")
                            .append(rs.getString("description"))
                            .append("\n");

                    jobsList.append("----------------------------------------\n\n");
                }

                if (jobsList.length() == 0) {

                    jobArea.setText("No job opportunities available.");

                } else {

                    jobArea.setText(jobsList.toString());

                }

            } catch (Exception ex) {

                jobArea.setText(
                        "Unable to load jobs.\n\nError: " + ex.getMessage()
                );

            }

            closeButton.addActionListener(ev -> frame.dispose());

            frame.setVisible(true);

        });
        events.addActionListener(e -> {

            JFrame frame = new JFrame("Upcoming Events");
            frame.setSize(650, 500);
            frame.setLayout(null);
            frame.setLocationRelativeTo(null);

            JLabel titleLabel = new JLabel("UPCOMING ALUMNI EVENTS");
            titleLabel.setBounds(220, 20, 250, 30);

            JTextArea eventArea = new JTextArea();
            eventArea.setEditable(false);

            JScrollPane scrollPane = new JScrollPane(eventArea);
            scrollPane.setBounds(30, 70, 570, 330);

            JButton closeButton = new JButton("Close");
            closeButton.setBounds(270, 420, 100, 30);

            frame.add(titleLabel);
            frame.add(scrollPane);
            frame.add(closeButton);

            try {

                Connection con = DBConnection.getConnection();

                String sql =
                        "SELECT event_name, event_date, venue, event_description " +
                                "FROM events ORDER BY event_date";

                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                StringBuilder eventList = new StringBuilder();

                while (rs.next()) {

                    eventList.append("Event: ")
                            .append(rs.getString("event_name"))
                            .append("\n");

                    eventList.append("Date: ")
                            .append(rs.getString("event_date"))
                            .append("\n");

                    eventList.append("Venue: ")
                            .append(rs.getString("venue"))
                            .append("\n");

                    eventList.append("Description: ")
                            .append(rs.getString("event_description"))
                            .append("\n");

                    eventList.append("----------------------------------------\n\n");
                }

                if (eventList.length() == 0) {
                    eventArea.setText("No upcoming events available.");
                } else {
                    eventArea.setText(eventList.toString());
                }

            } catch (Exception ex) {

                eventArea.setText(
                        "Unable to load events.\n\nError: "
                                + ex.getMessage()
                );

            }

            closeButton.addActionListener(ev -> frame.dispose());

            frame.setVisible(true);

        });
        mentorship.addActionListener(e -> {

            JFrame frame = new JFrame("Request Mentorship");
            frame.setSize(500, 350);
            frame.setLayout(null);
            frame.setLocationRelativeTo(null);

            JLabel titleLabel = new JLabel("MENTORSHIP REQUEST");
            titleLabel.setBounds(160, 20, 200, 30);

            JLabel mentorLabel = new JLabel("Mentor Name:");
            mentorLabel.setBounds(50, 80, 120, 25);

            JTextField mentorField = new JTextField();
            mentorField.setBounds(180, 80, 220, 25);

            JLabel messageLabel = new JLabel("Message:");
            messageLabel.setBounds(50, 125, 120, 25);

            JTextArea messageArea = new JTextArea();
            messageArea.setLineWrap(true);
            messageArea.setWrapStyleWord(true);

            JScrollPane messageScroll = new JScrollPane(messageArea);
            messageScroll.setBounds(180, 125, 220, 80);

            JButton sendButton = new JButton("Send Request");
            sendButton.setBounds(180, 240, 130, 30);

            frame.add(titleLabel);
            frame.add(mentorLabel);
            frame.add(mentorField);
            frame.add(messageLabel);
            frame.add(messageScroll);
            frame.add(sendButton);

            sendButton.addActionListener(ev -> {

                String mentor = mentorField.getText().trim();
                String message = messageArea.getText().trim();

                if (mentor.isEmpty() || message.isEmpty()) {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Please enter mentor name and message!"
                    );

                    return;
                }

                try {

                    Connection con = DBConnection.getConnection();

                    String sql =
                            "INSERT INTO mentorship " +
                                    "(student_name, mentor_name, message, status) " +
                                    "VALUES (?, ?, ?, ?)";

                    PreparedStatement ps = con.prepareStatement(sql);

                    ps.setString(1, "Student");
                    ps.setString(2, mentor);
                    ps.setString(3, message);
                    ps.setString(4, "Pending");

                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(
                            frame,
                            "Mentorship request sent successfully!"
                    );

                    frame.dispose();

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Error sending request: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );

                }

            });

            frame.setVisible(true);

        });
        notifications.addActionListener(e -> {

            JFrame frame = new JFrame("Notifications");
            frame.setSize(600, 450);
            frame.setLayout(null);
            frame.setLocationRelativeTo(null);

            JLabel titleLabel = new JLabel("NOTIFICATIONS");
            titleLabel.setBounds(240, 20, 150, 30);

            JTextArea notificationArea = new JTextArea();
            notificationArea.setEditable(false);
            notificationArea.setLineWrap(true);
            notificationArea.setWrapStyleWord(true);

            JScrollPane scrollPane = new JScrollPane(notificationArea);
            scrollPane.setBounds(30, 70, 520, 280);

            JButton closeButton = new JButton("Close");
            closeButton.setBounds(250, 370, 100, 30);

            frame.add(titleLabel);
            frame.add(scrollPane);
            frame.add(closeButton);

            try {

                Connection con = DBConnection.getConnection();

                String sql =
                        "SELECT title, message, created_at " +
                                "FROM notifications " +
                                "ORDER BY created_at DESC";

                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                StringBuilder notificationsList = new StringBuilder();

                while (rs.next()) {

                    notificationsList.append("Title: ")
                            .append(rs.getString("title"))
                            .append("\n");

                    notificationsList.append("Message: ")
                            .append(rs.getString("message"))
                            .append("\n");

                    notificationsList.append("Date: ")
                            .append(rs.getString("created_at"))
                            .append("\n");

                    notificationsList.append(
                            "--------------------------------\n\n"
                    );
                }

                if (notificationsList.length() == 0) {
                    notificationArea.setText("No notifications available.");
                } else {
                    notificationArea.setText(
                            notificationsList.toString()
                    );
                }

            } catch (Exception ex) {

                notificationArea.setText(
                        "Unable to load notifications.\n\nError: "
                                + ex.getMessage()
                );

            }

            closeButton.addActionListener(ev -> frame.dispose());

            frame.setVisible(true);

        });
        logout.addActionListener(e -> {

            new LoginForm();

            dispose();

        });
        setVisible(true);
    }
}