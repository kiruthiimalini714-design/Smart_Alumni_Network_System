package ui;

import javax.swing.*;
import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class Notifications extends JFrame {

    JLabel title, notificationTitle, message;

    JTextField txtTitle;

    JTextArea txtMessage;

    JButton send, view, back;

    public Notifications() {

        setTitle("Notifications");

        setSize(600,450);

        setLayout(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        title = new JLabel("NOTIFICATIONS");
        title.setBounds(220,20,200,30);

        notificationTitle = new JLabel("Title");
        notificationTitle.setBounds(50,70,100,25);

        txtTitle = new JTextField();
        txtTitle.setBounds(170,70,250,25);

        message = new JLabel("Message");
        message.setBounds(50,120,100,25);

        txtMessage = new JTextArea();
        txtMessage.setBounds(170,120,300,100);

        send = new JButton("Send Notification");
        send.setBounds(50,270,170,30);

        view = new JButton("View Notifications");
        view.setBounds(240,270,170,30);

        back = new JButton("Back");
        back.setBounds(430,270,100,30);

        add(title);
        add(notificationTitle);
        add(txtTitle);
        add(message);
        add(txtMessage);
        add(send);
        add(view);
        add(back);
        send.addActionListener(e -> {

            try {

                Connection con = DBConnection.getConnection();

                String sql = "INSERT INTO notifications(title, message) VALUES(?,?)";

                PreparedStatement ps = con.prepareStatement(sql);

                ps.setString(1, txtTitle.getText());
                ps.setString(2, txtMessage.getText());

                ps.executeUpdate();

                JOptionPane.showMessageDialog(
                        null,
                        "Notification Sent Successfully!"
                );

            } catch(Exception ex){

                JOptionPane.showMessageDialog(
                        null,
                        ex.getMessage()
                );

            }

        });
        view.addActionListener(e -> {

            try {

                Connection con = DBConnection.getConnection();

                String sql = "SELECT * FROM notifications ORDER BY created_at DESC";

                PreparedStatement ps = con.prepareStatement(sql);

                java.sql.ResultSet rs = ps.executeQuery();

                StringBuilder data = new StringBuilder();

                while(rs.next()){

                    data.append("Title : ")
                            .append(rs.getString("title"))
                            .append("\n");

                    data.append("Message : ")
                            .append(rs.getString("message"))
                            .append("\n");

                    data.append("Date : ")
                            .append(rs.getString("created_at"))
                            .append("\n");

                    data.append("---------------------------------\n");

                }

                JOptionPane.showMessageDialog(null, data.toString());

            } catch(Exception ex){

                JOptionPane.showMessageDialog(null, ex.getMessage());

            }

        });
        back.addActionListener(e -> {

            new AdminDashboard();

            dispose();

        });

        setVisible(true);
    }
}