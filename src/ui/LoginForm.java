package ui;

import javax.swing.*;
import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class LoginForm extends JFrame {

    JLabel title, email, password, role;
    JTextField tEmail;
    JPasswordField tPassword;
    JComboBox<String> cbRole;
    JButton login, register, clear;

    public LoginForm() {

        setTitle("Smart Alumni Network System - Login");
        setSize(450, 350);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        title = new JLabel("LOGIN");
        title.setBounds(180, 20, 100, 30);

        email = new JLabel("Email");
        email.setBounds(50, 70, 100, 25);

        tEmail = new JTextField();
        tEmail.setBounds(150, 70, 200, 25);

        password = new JLabel("Password");
        password.setBounds(50, 110, 100, 25);

        tPassword = new JPasswordField();
        tPassword.setBounds(150, 110, 200, 25);

        role = new JLabel("Role");
        role.setBounds(50, 150, 100, 25);

        cbRole = new JComboBox<>(new String[]{"Student", "Alumni", "Admin"});
        cbRole.setBounds(150, 150, 200, 25);

        login = new JButton("Login");
        login.setBounds(50, 220, 100, 30);

        register = new JButton("Register");
        register.setBounds(170, 220, 100, 30);

        clear = new JButton("Clear");
        clear.setBounds(290, 220, 100, 30);

        add(title);
        add(email);
        add(tEmail);
        add(password);
        add(tPassword);
        add(role);
        add(cbRole);
        add(login);
        add(register);
        register.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new RegistrationForm();
                dispose();
            }
        });
        add(clear);
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tEmail.setText("");
                tPassword.setText("");
                cbRole.setSelectedIndex(0);
                tEmail.requestFocus();
            }
        });
        login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {

                    Connection con = DBConnection.getConnection();

                    String email = tEmail.getText();
                    String password = String.valueOf(tPassword.getPassword());
                    String role = cbRole.getSelectedItem().toString();

                    String sql = "";

                    if(role.equals("Student")){
                        sql = "SELECT * FROM students WHERE email=? AND password=?";
                    }
                    else if(role.equals("Alumni")){
                        sql = "SELECT * FROM alumni WHERE email=? AND password=?";
                    }
                    else{
                        sql = "SELECT * FROM admin WHERE email=? AND password=?";
                    }

                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setString(1, email);
                    ps.setString(2, password);

                    ResultSet rs = ps.executeQuery();

                    if(rs.next()){

                            JOptionPane.showMessageDialog(null, "Login Successful!");

                            if(role.equals("Student")){
                                new StudentDashboard();
                            }
                            else if(role.equals("Alumni")){
                                new AlumniDashboard();
                            }
                            else{
                                new AdminDashboard();
                            }

                            dispose();   // Closes the Login window
                        }
                    else{

                        JOptionPane.showMessageDialog(
                                null,
                                "Invalid Email or Password!",
                                "Login Failed",
                                JOptionPane.ERROR_MESSAGE
                        );

                        tPassword.setText("");
                        tPassword.requestFocus();
                }
                } catch(Exception ex){
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }

            }
        });
        setLocationRelativeTo(null);
        setVisible(true);
    }
}