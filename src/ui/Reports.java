package ui;

import database.DBConnection;
import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Reports extends JFrame {

    JLabel title;
    JLabel students, alumni, jobs, events, mentorship, notifications;

    JLabel lblStudents, lblAlumni, lblJobs,
            lblEvents, lblMentorship, lblNotifications;

    JButton load, back;

    public Reports() {

        setTitle("Reports");

        setSize(500,450);

        setLayout(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        title = new JLabel("REPORTS");
        title.setBounds(200,20,100,30);

        students = new JLabel("Total Students");
        students.setBounds(50,70,150,25);

        lblStudents = new JLabel("0");
        lblStudents.setBounds(250,70,100,25);

        alumni = new JLabel("Total Alumni");
        alumni.setBounds(50,110,150,25);

        lblAlumni = new JLabel("0");
        lblAlumni.setBounds(250,110,100,25);

        jobs = new JLabel("Total Jobs");
        jobs.setBounds(50,150,150,25);

        lblJobs = new JLabel("0");
        lblJobs.setBounds(250,150,100,25);

        events = new JLabel("Total Events");
        events.setBounds(50,190,150,25);

        lblEvents = new JLabel("0");
        lblEvents.setBounds(250,190,100,25);

        mentorship = new JLabel("Total Mentorship");
        mentorship.setBounds(50,230,150,25);

        lblMentorship = new JLabel("0");
        lblMentorship.setBounds(250,230,100,25);

        notifications = new JLabel("Total Notifications");
        notifications.setBounds(50,270,150,25);

        lblNotifications = new JLabel("0");
        lblNotifications.setBounds(250,270,100,25);

        load = new JButton("Load Report");
        load.setBounds(100,330,120,30);

        back = new JButton("Back");
        back.setBounds(260,330,100,30);

        add(title);
        add(students);
        add(lblStudents);
        add(alumni);
        add(lblAlumni);
        add(jobs);
        add(lblJobs);
        add(events);
        add(lblEvents);
        add(mentorship);
        add(lblMentorship);
        add(notifications);
        add(lblNotifications);
        add(load);
        add(back);
        load.addActionListener(e -> {

            try {

                Connection con = DBConnection.getConnection();

                lblStudents.setText(getCount(con,"students"));

                lblAlumni.setText(getCount(con,"alumni"));

                lblJobs.setText(getCount(con,"jobs"));

                lblEvents.setText(getCount(con,"events"));

                lblMentorship.setText(getCount(con,"mentorship"));

                lblNotifications.setText(getCount(con,"notifications"));

            }

            catch(Exception ex){

                JOptionPane.showMessageDialog(null,ex.getMessage());

            }

        });
        back.addActionListener(e -> {

            new AdminDashboard();

            dispose();

        });

        setVisible(true);
    }
    private String getCount(Connection con, String tableName) throws Exception {

        String sql = "SELECT COUNT(*) FROM " + tableName;

        PreparedStatement ps = con.prepareStatement(sql);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getString(1);
        }

        return "0";
    }
}