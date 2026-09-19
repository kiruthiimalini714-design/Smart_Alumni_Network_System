package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBConnection {

    private static final String URL = "jdbc:sqlite:alumni.db";

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL);
    }

    public static void createTables() {

        try (Connection con = getConnection();
             Statement st = con.createStatement()) {

            // Students table
            st.executeUpdate("""
    CREATE TABLE IF NOT EXISTS students (
        student_id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT,
        email TEXT UNIQUE,
        password TEXT,
        department TEXT,
        year INTEGER,
        phone TEXT
    )
""");

            // Alumni table
            st.executeUpdate("""
    CREATE TABLE IF NOT EXISTS alumni (
        alumni_id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT,
        email TEXT UNIQUE,
        password TEXT,
        department TEXT,
        batch_year INTEGER,
        company TEXT,
        designation TEXT,
        phone TEXT
    )
""");
            // Admin table
            st.executeUpdate("""
    CREATE TABLE IF NOT EXISTS admin (
        admin_id INTEGER PRIMARY KEY AUTOINCREMENT,
        email TEXT UNIQUE,
        password TEXT
    )
""");
            st.executeUpdate("""
    INSERT OR IGNORE INTO admin(email, password)
    VALUES ('admin@gmail.com', 'admin123')
""");

            // Jobs table
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS jobs (
                    job_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    job_title TEXT,
                    company TEXT,
                    location TEXT,
                    salary TEXT,
                    description TEXT
                )
            """);

            // Events table
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS events (
                    event_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    event_name TEXT,
                    event_date TEXT,
                    venue TEXT,
                    event_description TEXT
                )
            """);

            // Mentorship table
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS mentorship (
                    request_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    student_name TEXT,
                    alumni_name TEXT,
                    message TEXT,
                    status TEXT DEFAULT 'Pending'
                )
            """);

            // Notifications table
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS notifications (
                    notification_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT,
                    message TEXT,
                    created_at TEXT DEFAULT CURRENT_TIMESTAMP
                )
            """);
            addColumnIfMissing(con, "students", "year", "INTEGER");
            addColumnIfMissing(con, "students", "phone", "TEXT");

            addColumnIfMissing(con, "alumni", "batch_year", "INTEGER");
            addColumnIfMissing(con, "alumni", "company", "TEXT");
            addColumnIfMissing(con, "alumni", "designation", "TEXT");
            addColumnIfMissing(con, "alumni", "phone", "TEXT");

            System.out.println("All tables created successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void addColumnIfMissing(
            Connection con,
            String table,
            String column,
            String type) throws Exception {

        var rs = con.createStatement().executeQuery(
                "PRAGMA table_info(" + table + ")"
        );

        while (rs.next()) {
            if (rs.getString("name").equalsIgnoreCase(column)) {
                return;
            }
        }

        con.createStatement().executeUpdate(
                "ALTER TABLE " + table +
                        " ADD COLUMN " + column + " " + type
        );

        System.out.println("Added column: " + table + "." + column);
    }


    // ⬇️ YOUR MAIN METHOD COMES AFTER IT

    public static void main(String[] args) {

        try {

            Connection con = getConnection();

            System.out.println("SQLite Database Connected Successfully!");

            con.close();

            createTables();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}