package ui;

import javax.swing.*;

public class AdminDashboard extends JFrame {

    JButton manageStudents, manageAlumni, manageJobs,
            manageEvents, notifications, reports, logout;

    JLabel title;

    public AdminDashboard() {

        setTitle("Admin Dashboard");
        setSize(500,500);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        title = new JLabel("WELCOME ADMIN");
        title.setBounds(170,20,200,30);

        manageStudents = new JButton("Manage Students");
        manageStudents.setBounds(140,70,200,30);

        manageAlumni = new JButton("Manage Alumni");
        manageAlumni.setBounds(140,120,200,30);

        manageJobs = new JButton("Manage Jobs");
        manageJobs.setBounds(140,170,200,30);

        manageEvents = new JButton("Manage Events");
        manageEvents.setBounds(140,220,200,30);

        notifications = new JButton("Notifications");
        notifications.setBounds(140,270,200,30);

        reports = new JButton("View Reports");
        reports.setBounds(140,320,200,30);

        logout = new JButton("Logout");
        logout.setBounds(140,370,200,30);

        add(title);
        add(manageStudents);
        add(manageAlumni);
        add(manageJobs);
        add(manageEvents);
        add(notifications);
        add(reports);
        add(logout);

        // Action Listeners

        manageStudents.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Manage Students");
        });

        manageAlumni.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Manage Alumni");
        });

        manageJobs.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Manage Jobs");
        });

        manageEvents.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Manage Events");
        });

        notifications.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Notifications");
        });
        reports.addActionListener(e -> {

            new Reports();

            dispose();

        });

        logout.addActionListener(e -> {
            new LoginForm();
            dispose();
        });

        setVisible(true);
    }
}