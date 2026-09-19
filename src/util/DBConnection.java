package util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;

public class DBConnection {

    private static String host = "localhost";
    private static String port = "3306";
    private static String dbName = "smart_alumni";
    private static String user = "root";
    private static String password = "";
    private static boolean initialized = false;

    static {
        loadConfiguration();
    }

    private static void loadConfiguration() {
        Properties props = new Properties();

        // Check db.properties in working directory
        File propFile = new File("db.properties");
        if (!propFile.exists()) {
            propFile = new File("../db.properties");
        }

        if (propFile.exists()) {
            try (InputStream in = new FileInputStream(propFile)) {
                props.load(in);
                System.out.println("[DBConnection] Loaded configuration from " + propFile.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("[DBConnection] Could not read db.properties: " + e.getMessage());
            }
        }

        host = props.getProperty("db.host", System.getenv().getOrDefault("DB_HOST", "localhost"));
        port = props.getProperty("db.port", System.getenv().getOrDefault("DB_PORT", "3306"));
        dbName = props.getProperty("db.name", System.getenv().getOrDefault("DB_NAME", "smart_alumni"));
        user = props.getProperty("db.user", System.getenv().getOrDefault("DB_USER", "root"));
        password = props.getProperty("db.password", System.getenv().getOrDefault("DB_PASSWORD", ""));

        // Register MySQL JDBC Driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnection] Warning: MySQL JDBC Driver not found in classpath! Check lib/mysql-connector-j.jar.");
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName +
                "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
        return DriverManager.getConnection(url, user, password);
    }

    public static Connection getServerConnection() throws SQLException {
        String url = "jdbc:mysql://" + host + ":" + port + "/" +
                "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
        return DriverManager.getConnection(url, user, password);
    }

    public static synchronized void checkAndInitializeDatabase() {
        if (initialized) return;

        System.out.println("[DBConnection] Checking MySQL database '" + dbName + "' on " + host + ":" + port + " (user: " + user + ")...");
        try {
            // First check direct connection
            try (Connection conn = getConnection()) {
                System.out.println("[DBConnection] Successfully connected to database '" + dbName + "'.");
                checkTablesExist(conn);
                initialized = true;
                return;
            } catch (SQLException ex) {
                // Database might not exist yet, attempt creation
                System.out.println("[DBConnection] Database '" + dbName + "' not accessible (" + ex.getMessage() + "). Attempting auto-creation on MySQL server...");
            }

            try (Connection serverConn = getServerConnection();
                 Statement stmt = serverConn.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
                System.out.println("[DBConnection] Database '" + dbName + "' verified/created successfully.");
            }

            try (Connection conn = getConnection()) {
                checkTablesExist(conn);
                initialized = true;
            }

        } catch (SQLException e) {
            System.err.println("================================================================================");
            System.err.println("[DBConnection] ERROR CONNECTING TO MYSQL SERVER!");
            System.err.println("Details: " + e.getMessage());
            System.err.println("Please check:");
            System.err.println("  1. MySQL Service is running (run 'net start MySQL80' in cmd as Administrator)");
            System.err.println("  2. Verify your MySQL password in 'db.properties' (db.password=your_password)");
            System.err.println("  3. Run database/schema.sql and database/sample_data.sql in MySQL Workbench");
            System.err.println("================================================================================");
        }
    }

    private static void checkTablesExist(Connection conn) {
        try {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getTables(dbName, null, "users", new String[]{"TABLE"})) {
                if (!rs.next()) {
                    System.out.println("[DBConnection] Tables not found in '" + dbName + "'. Executing database/schema.sql and sample_data.sql...");
                    executeSqlFile(conn, "database/schema.sql");
                    executeSqlFile(conn, "database/sample_data.sql");
                    System.out.println("[DBConnection] Database schema and sample data initialized successfully!");
                } else {
                    System.out.println("[DBConnection] Tables verified in database '" + dbName + "'.");
                }
            }
        } catch (Exception e) {
            System.err.println("[DBConnection] Could not verify tables: " + e.getMessage());
        }
    }

    private static void executeSqlFile(Connection conn, String relativePath) {
        Path path = Paths.get(relativePath);
        if (!Files.exists(path)) {
            path = Paths.get("../" + relativePath);
        }
        if (!Files.exists(path)) {
            System.err.println("[DBConnection] SQL file not found: " + relativePath);
            return;
        }

        try {
            String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            // Split statements by semicolon while ignoring comments
            String[] statements = content.split(";\\s*(\\r?\\n)+");
            try (Statement stmt = conn.createStatement()) {
                for (String sql : statements) {
                    String trimmed = sql.trim();
                    if (!trimmed.isEmpty() && !trimmed.startsWith("--") && !trimmed.startsWith("/*")) {
                        try {
                            stmt.execute(trimmed);
                        } catch (SQLException e) {
                            // Suppress already exists or harmless warnings
                            if (!e.getMessage().toLowerCase().contains("already exists")) {
                                System.err.println("[DBConnection] Warning executing statement: " + e.getMessage());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[DBConnection] Error executing SQL file " + relativePath + ": " + e.getMessage());
        }
    }
}
