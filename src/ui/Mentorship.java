package ui;

import javax.swing.*;
import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class Mentorship extends JFrame {

    JLabel title, studentName, alumniName, message;

    JTextField txtStudentName, txtAlumniName;

    JTextArea txtMessage;

    JButton sendRequest, viewRequests, back;

    public Mentorship() {

        setTitle("Mentorship");
        setSize(600,500);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        title = new JLabel("MENTORSHIP REQUEST");
        title.setBounds(180,20,250,30);

        studentName = new JLabel("Student Name");
        studentName.setBounds(50,70,120,25);

        txtStudentName = new JTextField();
        txtStudentName.setBounds(180,70,250,25);

        alumniName = new JLabel("Alumni Name");
        alumniName.setBounds(50,110,120,25);

        txtAlumniName = new JTextField();
        txtAlumniName.setBounds(180,110,250,25);

        message = new JLabel("Message");
        message.setBounds(50,150,120,25);

        txtMessage = new JTextArea();
        txtMessage.setBounds(180,150,300,100);

        sendRequest = new JButton("Send Request");
        sendRequest.setBounds(50,300,150,30);

        viewRequests = new JButton("View Requests");
        viewRequests.setBounds(220,300,150,30);

        back = new JButton("Back");
        back.setBounds(390,300,100,30);

        add(title);
        add(studentName);
        add(txtStudentName);
        add(alumniName);
        add(txtAlumniName);
        add(message);
        add(txtMessage);
        add(sendRequest);
        add(viewRequests);
        add(back);
        sendRequest.addActionListener(e -> {

            try {

                Connection con = DBConnection.getConnection();

                String sql = "INSERT INTO mentorship(student_name, alumni_name, message) VALUES(?,?,?)";

                PreparedStatement ps = con.prepareStatement(sql);

                ps.setString(1, txtStudentName.getText());
                ps.setString(2, txtAlumniName.getText());
                ps.setString(3, txtMessage.getText());

                ps.executeUpdate();

                JOptionPane.showMessageDialog(null,
                        "Mentorship Request Sent Successfully!");

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(null,
                        ex.getMessage());

            }

        });
        viewRequests.addActionListener(e -> {

            try {

                Connection con = DBConnection.getConnection();

                String sql = "SELECT * FROM mentorship";

                PreparedStatement ps = con.prepareStatement(sql);

                java.sql.ResultSet rs = ps.executeQuery();

                StringBuilder data = new StringBuilder();

                while(rs.next()){

                    data.append("Student: ")
                            .append(rs.getString("student_name"))
                            .append("\n");

                    data.append("Alumni: ")
                            .append(rs.getString("alumni_name"))
                            .append("\n");

                    data.append("Message: ")
                            .append(rs.getString("message"))
                            .append("\n");

                    data.append("Status: ")
                            .append(rs.getString("status"))
                            .append("\n\n");
                }

                JOptionPane.showMessageDialog(null, data.toString());

            } catch(Exception ex){

                JOptionPane.showMessageDialog(null,
                        ex.getMessage());

            }

        });
        back.addActionListener(e -> {
            new StudentDashboard();
            dispose();
        });

        setVisible(true);
    }
}
