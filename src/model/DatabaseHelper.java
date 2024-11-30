package model;

import java.sql.*;

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
        String computerTableSQL = "CREATE TABLE IF NOT EXISTS computers ("
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

        String userTableSQL = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "user_name TEXT UNIQUE NOT NULL,"
                + "password TEXT NOT NULL"
                + ");";

        try (Connection conn = connect();
             PreparedStatement computerStmt = conn.prepareStatement(computerTableSQL);
             PreparedStatement userStmt = conn.prepareStatement(userTableSQL)) {
            computerStmt.execute();
            userStmt.execute();
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

    // Método para inserir novo usuário
    public boolean insertUser(String userName, String password) {
        String sql = "INSERT INTO users(user_name, password) VALUES(?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true; // Cadastro bem-sucedido
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                System.out.println("Usuário já existe: " + userName);
            } else {
                System.out.println(e.getMessage());
            }
            return false; // Falha no cadastro
        }
    }

    // Método para verificar se um usuário existe
    public boolean isUserExists(String userName) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_name = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0; // Retorna true se o usuário existir
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    // Método para validar login
    public boolean validateLogin(String userName, String password) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_name = ? AND password = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0; // Retorna true se as credenciais forem válidas
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
