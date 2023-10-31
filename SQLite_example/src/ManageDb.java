package sqlite_example.db_crud;

import java.sql.*;

public class ManageDb {
    String dbUrl;

    private Connection conn;

    public ManageDb(String dbAddress, String dbName) {
        this.dbUrl = "jdbc:sqlite:" + dbAddress + "/" + dbName;
        this.connect();
        this.createTable();
        this.insertRows("Daniel", "1");
        this.insertRows("Moreira", "2");
    }

    public String getResults() {
        String selectQuery = "SELECT * FROM users";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(selectQuery);
            String results = "";
            while (rs.next()) {
                results += "USER '" + rs.getInt("id") + "' --> name: " + rs.getString("name") + ", number: " + rs.getString("number") + "\n";
            }
            return results;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "";
        }
    }

    public void clearDb() {
        String deleteQuery = "DELETE * FROM users";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(deleteQuery);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void connect() {
        try {
            this.conn = DriverManager.getConnection(this.dbUrl);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            this.closeConnection();
        }
    }

    private void createTable() {
        String createQuery = "CREATE TABLE IF NOT EXISTS users (id integer PRIMARY KEY, name text NOT NULL, number text NOT NULL)";

        try {
            Statement stmt = this.conn.createStatement();
            stmt.execute(createQuery);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void insertRows(String name, String number) {
        String insertQuery = "INSERT INTO users(name, number) VALUES(?,?)";

        try {
            PreparedStatement pstmt = conn.prepareStatement(insertQuery);
            pstmt.setString(1, name);
            pstmt.setString(2, number);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void closeConnection() {
        try {
            if (this.conn != null) {
                this.conn.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
