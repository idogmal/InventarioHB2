
package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

/**
 * Classe DatabaseHelper - Gerencia as operações do banco de dados para computadores e usuários.
 */
public class DatabaseHelper {

    private static final String DB_URL = "jdbc:sqlite:computers.db";

    /**
     * Conecta ao banco de dados SQLite.
     *
     * @return Conexão ativa ou null em caso de erro.
     */
    public static Connection connect() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao banco de dados: " + e.getMessage());
            return null;
        }
    }

    /**
     * Cria tabelas no banco de dados, se não existirem.
     */
    public void createTable() {
        String computerTableSQL = "CREATE TABLE IF NOT EXISTS computers (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tag TEXT, " +
                "serial_number TEXT, " +
                "model TEXT, " +
                "brand TEXT, " +
                "state TEXT, " +
                "user_name TEXT, " +
                "windows_version TEXT, " +
                "office_version TEXT, " +
                "location TEXT, " +
                "purchase_date TEXT" +
                ");";

        String userTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_name TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL" +
                ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(computerTableSQL);
            stmt.execute(userTableSQL);
            System.out.println("Tabelas criadas/verificadas com sucesso.");
        } catch (SQLException e) {
            System.out.println("Erro ao criar tabelas: " + e.getMessage());
        }
    }

    /**
     * Insere um computador no banco de dados.
     *
     * @param computer Computador a ser inserido.
     */
    public void insertComputer(Computer computer) {
        String sql = "INSERT INTO computers(tag, serial_number, model, brand, state, user_name, windows_version, office_version, location, purchase_date) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            System.out.println("Computador inserido com sucesso.");
        } catch (SQLException e) {
            System.out.println("Erro ao inserir computador: " + e.getMessage());
        }
    }

    /**
     * Valida o login de um usuário verificando nome e senha.
     *
     * @param userName Nome do usuário.
     * @param password Senha do usuário.
     * @return true se válido, false caso contrário.
     */
    public boolean validateLogin(String userName, String password) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_name = ? AND password = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao validar login: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retorna uma lista de usuários do banco de dados.
     *
     * @return Lista de usuários.
     */
    public ObservableList<User> getUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();
        String sql = "SELECT user_name, password FROM users";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(new User(rs.getString("user_name"), rs.getString("password")));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao obter lista de usuários: " + e.getMessage());
        }
        return users;
    }

    public ObservableList<User> loadUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();
        String sql = "SELECT user_name, password FROM users";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(new User(rs.getString("user_name"), rs.getString("password")));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao carregar usuários: " + e.getMessage());
        }
        return users;
    }


    /**
     * Insere um novo usuário no banco de dados.
     *
     * @param userName Nome do usuário.
     * @param password Senha do usuário.
     * @return true se inserido com sucesso, caso contrário false.
     */
    public boolean insertUser(String userName, String password) {
        String sql = "INSERT INTO users(user_name, password) VALUES(?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            System.out.println("Usuário inserido com sucesso: " + userName);
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir usuário: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica se um usuário já existe no banco de dados.
     *
     * @param userName Nome do usuário.
     * @return true se o usuário existir, caso contrário false.
     */
    public boolean isUserExists(String userName) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_name = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao verificar existência de usuário: " + e.getMessage());
            return false;
        }
    }

    /**
     * Edita a senha de um usuário no banco de dados.
     *
     * @param userName Nome do usuário.
     * @param newPassword Nova senha.
     * @return true se atualizado com sucesso, caso contrário false.
     */
    public boolean editUserPassword(String userName, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE user_name = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, userName);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao alterar senha do usuário: " + e.getMessage());
            return false;
        }
    }

    /**
     * Exclui um usuário do banco de dados.
     *
     * @param userName Nome do usuário.
     * @return true se excluído com sucesso, caso contrário false.
     */
    public boolean deleteUser(String userName) {
        String sql = "DELETE FROM users WHERE user_name = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir usuário: " + e.getMessage());
            return false;
        }
    }
}
