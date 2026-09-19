package ui;

import javax.swing.*;
import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class JobPortal extends JFrame {

    JLabel title, jobTitle, company, location, salary, description;

    JTextField txtJobTitle, txtCompany, txtLocation, txtSalary;

    JTextArea txtDescription;

    JButton postJob, viewJobs, back;

    public JobPortal() {

        setTitle("Job Portal");

        setSize(600,500);

        setLayout(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        title = new JLabel("JOB PORTAL");
        title.setBounds(240,20,150,30);

        jobTitle = new JLabel("Job Title");
        jobTitle.setBounds(50,70,100,25);

        txtJobTitle = new JTextField();
        txtJobTitle.setBounds(170,70,250,25);

        company = new JLabel("Company");
        company.setBounds(50,110,100,25);

        txtCompany = new JTextField();
        txtCompany.setBounds(170,110,250,25);

        location = new JLabel("Location");
        location.setBounds(50,150,100,25);

        txtLocation = new JTextField();
        txtLocation.setBounds(170,150,250,25);

        salary = new JLabel("Salary");
        salary.setBounds(50,190,100,25);

        txtSalary = new JTextField();
        txtSalary.setBounds(170,190,250,25);

        description = new JLabel("Description");
        description.setBounds(50,230,100,25);

        txtDescription = new JTextArea();
        txtDescription.setBounds(170,230,300,80);

        postJob = new JButton("Post Job");
        postJob.setBounds(80,340,120,30);

        viewJobs = new JButton("View Jobs");
        viewJobs.setBounds(230,340,120,30);

        back = new JButton("Back");
        back.setBounds(380,340,120,30);

        add(title);
        add(jobTitle);
        add(txtJobTitle);
        add(company);
        add(txtCompany);
        add(location);
        add(txtLocation);
        add(salary);
        add(txtSalary);
        add(description);
        add(txtDescription);
        add(postJob);
        add(viewJobs);
        add(back);
        postJob.addActionListener(e -> {

            try {

                Connection con = DBConnection.getConnection();

                String sql = "INSERT INTO jobs(job_title, company, location, salary, description) VALUES(?,?,?,?,?)";

                PreparedStatement ps = con.prepareStatement(sql);

                ps.setString(1, txtJobTitle.getText());
                ps.setString(2, txtCompany.getText());
                ps.setString(3, txtLocation.getText());
                ps.setString(4, txtSalary.getText());
                ps.setString(5, txtDescription.getText());

                ps.executeUpdate();

                JOptionPane.showMessageDialog(null, "Job Posted Successfully!");

            } catch(Exception ex) {

                JOptionPane.showMessageDialog(null, ex.getMessage());

            }

        });
        viewJobs.addActionListener(e -> {

            JOptionPane.showMessageDialog(
                    null,
                    "Available Jobs"
            );

        });
        back.addActionListener(e -> {

            new AlumniDashboard();

            dispose();

        });

        setVisible(true);
    }
}