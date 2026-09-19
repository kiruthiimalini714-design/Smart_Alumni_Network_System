package ui;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import database.DBConnection;

public class RegistrationForm extends JFrame {

    JLabel title, name, email, password, department, role, year, phone, company, designation;
    JTextField tName, tEmail, tDepartment, tYear, tPhone, tCompany, tDesignation;
    JPasswordField tPassword;
    JComboBox<String> cbRole;
    JButton register, clear;

    public RegistrationForm() {

        setTitle("Smart Alumni Network System");
        setSize(500, 600);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        title = new JLabel("Registration Form");
        title.setBounds(170, 20, 200, 30);

        name = new JLabel("Name");
        name.setBounds(50, 70, 100, 25);
        tName = new JTextField();
        tName.setBounds(180, 70, 200, 25);

        email = new JLabel("Email");
        email.setBounds(50, 110, 100, 25);
        tEmail = new JTextField();
        tEmail.setBounds(180, 110, 200, 25);

        password = new JLabel("Password");
        password.setBounds(50, 150, 100, 25);
        tPassword = new JPasswordField();
        tPassword.setBounds(180, 150, 200, 25);

        department = new JLabel("Department");
        department.setBounds(50, 190, 100, 25);
        tDepartment = new JTextField();
        tDepartment.setBounds(180, 190, 200, 25);

        role = new JLabel("Role");
        role.setBounds(50, 230, 100, 25);
        cbRole = new JComboBox<>(new String[]{"Student", "Alumni"});
        cbRole.setBounds(180, 230, 200, 25);

        year = new JLabel("Year/Batch");
        year.setBounds(50, 270, 100, 25);
        tYear = new JTextField();
        tYear.setBounds(180, 270, 200, 25);

        phone = new JLabel("Phone");
        phone.setBounds(50, 310, 100, 25);
        tPhone = new JTextField();
        tPhone.setBounds(180, 310, 200, 25);

        company = new JLabel("Company");
        company.setBounds(50, 350, 100, 25);
        tCompany = new JTextField();
        tCompany.setBounds(180, 350, 200, 25);

        designation = new JLabel("Designation");
        designation.setBounds(50, 390, 100, 25);
        tDesignation = new JTextField();
        tDesignation.setBounds(180, 390, 200, 25);

        register = new JButton("Register");
        register.setBounds(100, 460, 120, 30);

        clear = new JButton("Clear");
        clear.setBounds(250, 460, 120, 30);

        add(title);
        add(name);
        add(tName);
        add(email);
        add(tEmail);
        add(password);
        add(tPassword);
        add(department);
        add(tDepartment);
        add(role);
        add(cbRole);
        add(year);
        add(tYear);
        add(phone);
        add(tPhone);
        add(company);
        add(tCompany);
        add(designation);
        add(tDesignation);
        cbRole.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                boolean alumni = cbRole.getSelectedItem()
                        .toString()
                        .equals("Alumni");

                company.setVisible(alumni);
                tCompany.setVisible(alumni);
                designation.setVisible(alumni);
                tDesignation.setVisible(alumni);
            }
        });
        company.setVisible(false);
        tCompany.setVisible(false);
        designation.setVisible(false);
        tDesignation.setVisible(false);
        add(register);
        add(clear);
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                tName.setText("");
                tEmail.setText("");
                tPassword.setText("");
                tDepartment.setText("");
                tYear.setText("");
                tPhone.setText("");
                tCompany.setText("");
                tDesignation.setText("");

                cbRole.setSelectedIndex(0);

                tName.requestFocus();
            }
        });
        register.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (tName.getText().trim().isEmpty() ||
                        tEmail.getText().trim().isEmpty() ||
                        tPassword.getPassword().length == 0 ||
                        tDepartment.getText().trim().isEmpty() ||
                        tYear.getText().trim().isEmpty() ||
                        tPhone.getText().trim().isEmpty()) {

                    JOptionPane.showMessageDialog(
                            null,
                            "Please fill all required fields!",
                            "Registration Error",
                            JOptionPane.WARNING_MESSAGE
                    );

                    return;
                }

                String emailText = tEmail.getText().trim();

                if (!emailText.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

                    JOptionPane.showMessageDialog(
                            null,
                            "Please enter a valid email address!",
                            "Invalid Email",
                            JOptionPane.WARNING_MESSAGE
                    );

                    tEmail.requestFocus();
                    return;
                }
                // Phone validation
                String phoneText = tPhone.getText().trim();

                if (!phoneText.matches("\\d{10}")) {

                    JOptionPane.showMessageDialog(
                            null,
                            "Phone number must contain exactly 10 digits!",
                            "Invalid Phone Number",
                            JOptionPane.WARNING_MESSAGE
                    );

                    tPhone.requestFocus();
                    return;
                }

// Year validation
                String yearText = tYear.getText().trim();

                if (!yearText.matches("\\d{4}")) {

                    JOptionPane.showMessageDialog(
                            null,
                            "Year must contain 4 digits!",
                            "Invalid Year",
                            JOptionPane.WARNING_MESSAGE
                    );

                    tYear.requestFocus();
                    return;
                }

                try {

                    Connection con = DBConnection.getConnection();

                    String r = cbRole.getSelectedItem().toString();

                    String checkSql;

                    if (r.equals("Student")) {
                        checkSql = "SELECT email FROM students WHERE email = ?";
                    } else {
                        checkSql = "SELECT email FROM alumni WHERE email = ?";
                    }

                    PreparedStatement checkPs = con.prepareStatement(checkSql);
                    checkPs.setString(1, tEmail.getText().trim());

                    ResultSet checkRs = checkPs.executeQuery();

                    if (checkRs.next()) {
                        JOptionPane.showMessageDialog(
                                null,
                                "This email is already registered!",
                                "Registration Error",
                                JOptionPane.WARNING_MESSAGE
                        );

                        tEmail.requestFocus();
                        return;
                    }

                    if(r.equals("Student")){

                        String sql = "INSERT INTO students(name,email,password,department,year,phone) VALUES(?,?,?,?,?,?)";

                        PreparedStatement ps = con.prepareStatement(sql);

                        ps.setString(1,tName.getText());
                        ps.setString(2,tEmail.getText());
                        ps.setString(3,String.valueOf(tPassword.getPassword()));
                        ps.setString(4,tDepartment.getText());
                        ps.setInt(5,Integer.parseInt(tYear.getText()));
                        ps.setString(6,tPhone.getText());

                        ps.executeUpdate();

                    }

                    else{

                        String sql = "INSERT INTO alumni(name,email,password,department,batch_year,company,designation,phone) VALUES(?,?,?,?,?,?,?,?)";

                        PreparedStatement ps = con.prepareStatement(sql);

                        ps.setString(1,tName.getText());
                        ps.setString(2,tEmail.getText());
                        ps.setString(3,String.valueOf(tPassword.getPassword()));
                        ps.setString(4,tDepartment.getText());
                        ps.setInt(5,Integer.parseInt(tYear.getText()));
                        ps.setString(6,tCompany.getText());
                        ps.setString(7,tDesignation.getText());
                        ps.setString(8,tPhone.getText());

                        ps.executeUpdate();

                    }

                    JOptionPane.showMessageDialog(null,"Registration Successful!");

                }

                catch(Exception ex){

                    JOptionPane.showMessageDialog(null,ex);

                }

            }
        });

        setVisible(true);
    }
}