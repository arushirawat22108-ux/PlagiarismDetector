package com.plagcoders.database;

import java.sql.*;

/**
 * DatabaseManager – H2 embedded database (no MySQL server needed).
 * Database is auto-created as plagiarism_data.mv.db in the project folder.
 * Team: PlagCoders | TCS-408 | JAVA-IV-T167
 */
public class DatabaseManager {

    // H2 embedded – file lives next to the project, no server required
    private static final String URL  = "jdbc:h2:./plagiarism_data;AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=TRUE";
    private static final String USER = "sa";
    private static final String PASS = "";

    private static DatabaseManager instance;
    private Connection connection;
    private boolean connected = false;
    private String  statusMsg = "Not connected";

    private DatabaseManager() {}

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) instance = new DatabaseManager();
        return instance;
    }

    // ── Connect and create tables ─────────────────────────────────────────────
    public boolean connect() {
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection(URL, USER, PASS);
            initSchema();
            connected = true;
            statusMsg = "Connected (H2 Embedded)";
            System.out.println("[DB] " + statusMsg);
            return true;
        } catch (ClassNotFoundException e) {
            statusMsg = "H2 JAR missing – place h2-*.jar in lib/ folder";
            System.err.println("[DB] " + statusMsg);
            return false;
        } catch (SQLException e) {
            statusMsg = "DB Error: " + e.getMessage();
            System.err.println("[DB] " + statusMsg);
            return false;
        }
    }

    private void initSchema() throws SQLException {
        try (Statement s = connection.createStatement()) {
            s.execute("""
                CREATE TABLE IF NOT EXISTS documents (
                    id         INT AUTO_INCREMENT PRIMARY KEY,
                    file_name  VARCHAR(255),
                    file_path  VARCHAR(1000),
                    file_type  VARCHAR(20),
                    word_count INT DEFAULT 0,
                    upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )""");
            s.execute("""
                CREATE TABLE IF NOT EXISTS comparison_reports (
                    id               INT AUTO_INCREMENT PRIMARY KEY,
                    doc1_id          INT,
                    doc2_id          INT,
                    doc1_name        VARCHAR(255),
                    doc2_name        VARCHAR(255),
                    jaccard_score    DOUBLE,
                    cosine_score     DOUBLE,
                    ngram_score      DOUBLE,
                    final_score      DOUBLE,
                    plagiarism_level VARCHAR(20),
                    comparison_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )""");
            s.execute("""
                CREATE TABLE IF NOT EXISTS session_logs (
                    id       INT AUTO_INCREMENT PRIMARY KEY,
                    action   VARCHAR(100),
                    details  VARCHAR(2000),
                    log_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )""");
        }
    }

    public boolean isConnected() { return connected; }
    public String  getStatus()   { return statusMsg; }

    public Connection getConnection() throws SQLException {
        if (!connected || connection == null || connection.isClosed())
            throw new SQLException("Not connected.");
        return connection;
    }

    public void close() {
        try { if (connection != null && !connection.isClosed()) connection.close(); }
        catch (SQLException ignored) {}
        connected = false;
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────
    public int saveDocument(String fileName, String filePath, String fileType, int wordCount) {
        if (!connected) return -1;
        String sql = "INSERT INTO documents(file_name,file_path,file_type,word_count) VALUES(?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, fileName); ps.setString(2, filePath);
            ps.setString(3, fileType); ps.setInt(4, wordCount);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { System.err.println("[DB] saveDocument: " + e.getMessage()); }
        return -1;
    }

    public int saveReport(int d1, int d2, String n1, String n2,
                          double j, double c, double ng, double f, String lv) {
        if (!connected) return -1;
        String sql = "INSERT INTO comparison_reports(doc1_id,doc2_id,doc1_name,doc2_name," +
                     "jaccard_score,cosine_score,ngram_score,final_score,plagiarism_level)" +
                     " VALUES(?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1,d1); ps.setInt(2,d2); ps.setString(3,n1); ps.setString(4,n2);
            ps.setDouble(5,j); ps.setDouble(6,c); ps.setDouble(7,ng);
            ps.setDouble(8,f); ps.setString(9,lv);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { System.err.println("[DB] saveReport: " + e.getMessage()); }
        return -1;
    }

    public ResultSet getAllReports() throws SQLException {
        return connection.createStatement()
            .executeQuery("SELECT * FROM comparison_reports ORDER BY comparison_time DESC");
    }

    public void log(String action, String details) {
        if (!connected) return;
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO session_logs(action,details) VALUES(?,?)")) {
            ps.setString(1, action);
            ps.setString(2, details != null && details.length() > 1990
                            ? details.substring(0,1990) : details);
            ps.executeUpdate();
        } catch (SQLException ignored) {}
    }
}
