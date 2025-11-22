package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

/**
 * Classe DatabaseHelper - Gerencia as operações do banco de dados para
 * computadores, usuários e histórico.
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
                "purchase_date TEXT, " +
                "observation TEXT" + // Adicionado campo de observação
                ");";

        String userTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_name TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL" +
                ");";

        // Nova tabela para histórico
        String historyTableSQL = "CREATE TABLE IF NOT EXISTS history (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "action TEXT, " +
                "user TEXT, " +
                "timestamp TEXT, " +
                "description TEXT" +
                ");";

        try (Connection conn = connect();
                Statement stmt = conn.createStatement()) {
            stmt.execute(computerTableSQL);
            stmt.execute(userTableSQL);
            stmt.execute(historyTableSQL);

            // Migração: Verifica se a coluna 'observation' existe na tabela 'computers'
            try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(computers)")) {
                boolean hasObservation = false;
                while (rs.next()) {
                    if ("observation".equalsIgnoreCase(rs.getString("name"))) {
                        hasObservation = true;
                        break;
                    }
                }
                if (!hasObservation) {
                    stmt.execute("ALTER TABLE computers ADD COLUMN observation TEXT");
                    System.out.println("Coluna 'observation' adicionada à tabela 'computers'.");
                }
            }

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
        String sql = "INSERT INTO computers(tag, serial_number, model, brand, state, user_name, windows_version, office_version, location, purchase_date, observation) "
                +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            pstmt.setString(11, computer.getObservation());
            pstmt.executeUpdate();
            System.out.println("Computador inserido com sucesso.");
        } catch (SQLException e) {
            System.out.println("Erro ao inserir computador: " + e.getMessage());
        }
    }

    /**
     * Exclui um computador do banco de dados.
     * Utiliza o ID para identificar o registro.
     *
     * @param computer Computador a ser removido.
     * @return true se o registro foi removido com sucesso, caso contrário false.
     */
    public boolean deleteComputer(Computer computer) {
        String sql = "DELETE FROM computers WHERE id = ?";
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, computer.getId());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Computador deletado com sucesso.");
                return true;
            } else {
                System.out.println("Nenhum computador foi deletado.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao excluir computador: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza os dados de um computador no banco de dados.
     *
     * @param computer Computador com os dados atualizados.
     * @return true se atualizado com sucesso, caso contrário false.
     */
    public boolean updateComputer(Computer computer) {
        String sql = "UPDATE computers SET tag = ?, serial_number = ?, model = ?, brand = ?, state = ?, " +
                "user_name = ?, windows_version = ?, office_version = ?, location = ?, purchase_date = ?, observation = ? "
                +
                "WHERE id = ?";
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
            pstmt.setString(11, computer.getObservation());
            pstmt.setInt(12, computer.getId());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Computador atualizado com sucesso.");
                return true;
            } else {
                System.out.println("Nenhum computador foi atualizado.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar computador: " + e.getMessage());
            return false;
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
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
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

    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
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
     * @param userName    Nome do usuário.
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

    /**
     * Carrega os computadores do banco de dados.
     *
     * @return Lista de computadores.
     */
    public List<Computer> loadComputers() {
        List<Computer> computers = new ArrayList<>();
        String sql = "SELECT id, tag, serial_number, model, brand, state, user_name, windows_version, office_version, location, purchase_date, observation FROM computers";
        try (Connection conn = connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Computer computer = new Computer(
                        rs.getInt("id"),
                        rs.getString("tag"),
                        rs.getString("model"),
                        rs.getString("brand"),
                        rs.getString("state"),
                        rs.getString("user_name"),
                        rs.getString("serial_number"),
                        rs.getString("windows_version"),
                        rs.getString("office_version"),
                        rs.getString("purchase_date"),
                        rs.getString("location"),
                        rs.getString("observation"));
                computers.add(computer);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao carregar computadores: " + e.getMessage());
        }
        return computers;
    }

    /**
     * Insere um registro de histórico no banco de dados.
     *
     * @param history Registro de histórico a ser inserido.
     */
    public void insertHistory(HistoryEntry history) {
        String sql = "INSERT INTO history(action, user, timestamp, description) VALUES(?, ?, ?, ?)";
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, history.getAction().name());
            pstmt.setString(2, history.getUser());
            pstmt.setString(3, history.getTimestamp().toString());
            pstmt.setString(4, history.getDescription());
            pstmt.executeUpdate();
            System.out.println("Histórico inserido com sucesso.");
        } catch (SQLException e) {
            System.out.println("Erro ao inserir histórico: " + e.getMessage());
        }
    }

    /**
     * Carrega os registros de histórico do banco de dados.
     *
     * @return Lista de registros de histórico.
     */
    public List<HistoryEntry> loadHistory() {
        List<HistoryEntry> historyList = new ArrayList<>();
        String sql = "SELECT action, user, timestamp, description FROM history";
        try (Connection conn = connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                HistoryEntry history = new HistoryEntry(
                        HistoryEntry.ActionType.valueOf(rs.getString("action")),
                        rs.getString("user"),
                        LocalDateTime.parse(rs.getString("timestamp")),
                        rs.getString("description"));
                historyList.add(history);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao carregar histórico: " + e.getMessage());
        }
        return historyList;
    }

}
