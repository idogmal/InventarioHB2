package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:computers.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS computers ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "tag TEXT,"
                + "serial_number TEXT,"
                + "model TEXT,"
                + "brand TEXT,"
                + "state TEXT,"
                + "user_name TEXT,"
                + "windows_version TEXT,"
                + "office_version TEXT,"
                + "location TEXT,"
                + "purchase_date TEXT"
                + ");";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertComputer(Computer computer) {
        String sql = "INSERT INTO computers(tag, serial_number, model, brand, state, user_name, windows_version, office_version, location, purchase_date) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, computer.getTag());
            pstmt.setString(2, computer.getSerialNumber());
            pstmt.setString(3, computer.getModel());
            pstmt.setString(4, computer.getBrand());
            pstmt.setString(5, computer.getState());
            pstmt.setString(6, computer.getUserName());
            pstmt.setString(7, computer.getWindowsVersion());
            pstmt.setString(8, computer.getOfficeVersion());
            pstmt.setString(9, computer.getLocation());
            pstmt.setString(10, computer.getPurchaseDate());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
