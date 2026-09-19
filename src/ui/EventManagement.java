package ui;

import javax.swing.*;
import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class EventManagement extends JFrame {

    JLabel title, eventName, date, venue, description;

    JTextField txtEventName, txtDate, txtVenue;

    JTextArea txtDescription;

    JButton addEvent, viewEvents, back;

    public EventManagement() {

        setTitle("Event Management");
        setSize(600,500);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        title = new JLabel("EVENT MANAGEMENT");
        title.setBounds(200,20,200,30);

        eventName = new JLabel("Event Name");
        eventName.setBounds(50,70,100,25);

        txtEventName = new JTextField();
        txtEventName.setBounds(170,70,250,25);

        date = new JLabel("Date");
        date.setBounds(50,110,100,25);

        txtDate = new JTextField();
        txtDate.setBounds(170,110,250,25);

        venue = new JLabel("Venue");
        venue.setBounds(50,150,100,25);

        txtVenue = new JTextField();
        txtVenue.setBounds(170,150,250,25);

        description = new JLabel("Description");
        description.setBounds(50,190,100,25);

        txtDescription = new JTextArea();
        txtDescription.setBounds(170,190,300,80);

        addEvent = new JButton("Add Event");
        addEvent.setBounds(70,310,120,30);

        viewEvents = new JButton("View Events");
        viewEvents.setBounds(230,310,120,30);

        back = new JButton("Back");
        back.setBounds(390,310,120,30);

        add(title);
        add(eventName);
        add(txtEventName);
        add(date);
        add(txtDate);
        add(venue);
        add(txtVenue);
        add(description);
        add(txtDescription);
        add(addEvent);
        add(viewEvents);
        add(back);
        addEvent.addActionListener(e -> {

            try {

                Connection con = DBConnection.getConnection();

                String sql = "INSERT INTO events(event_name, event_date, venue, event_description) VALUES(?,?,?,?)";

                PreparedStatement ps = con.prepareStatement(sql);

                ps.setString(1, txtEventName.getText());
                ps.setString(2, txtDate.getText());
                ps.setString(3, txtVenue.getText());
                ps.setString(4, txtDescription.getText());

                ps.executeUpdate();

                JOptionPane.showMessageDialog(null, "Event Added Successfully!");

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(null, ex.getMessage());

            }

        });
        viewEvents.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "View Events");
        });
        back.addActionListener(e -> {
            new AdminDashboard();
            dispose();
        });

        setVisible(true);
    }
}