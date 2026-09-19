package ui;

import javax.swing.*;
import java.awt.*;
import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SearchAlumni extends JFrame {

    JLabel title, name;
    JTextField txtName;
    JButton search, back;
    JTextArea result;

    public SearchAlumni() {

        setTitle("Search Alumni");
        setSize(600,500);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        title = new JLabel("SEARCH ALUMNI");
        title.setBounds(220,20,200,30);

        name = new JLabel("Name");
        name.setBounds(50,70,100,30);

        txtName = new JTextField();
        txtName.setBounds(150,70,250,30);

        search = new JButton("Search");
        search.setBounds(420,70,100,30);

        result = new JTextArea();
        result.setBounds(50,130,470,250);

        back = new JButton("Back");
        back.setBounds(230,400,100,30);

        add(title);
        add(name);
        add(txtName);
        add(search);
        add(result);
        add(back);
        search.addActionListener(e -> {

            try {

                Connection con = DBConnection.getConnection();

                String sql = "SELECT * FROM alumni WHERE name LIKE ?";

                PreparedStatement ps = con.prepareStatement(sql);

                ps.setString(1, "%" + txtName.getText() + "%");

                ResultSet rs = ps.executeQuery();

                result.setText("");

                while(rs.next()){

                    result.append("Name : "
                            + rs.getString("name") + "\n");

                    result.append("Department : "
                            + rs.getString("department") + "\n");

                    result.append("Batch : "
                            + rs.getInt("batch_year") + "\n");

                    result.append("Company : "
                            + rs.getString("company") + "\n");

                    result.append("Designation : "
                            + rs.getString("designation") + "\n");

                    result.append("------------------------------\n");

                }

            }

            catch(Exception ex){

                JOptionPane.showMessageDialog(null, ex);

            }

        });
        setVisible(true);
    }
}