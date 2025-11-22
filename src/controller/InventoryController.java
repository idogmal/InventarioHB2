package controller;

import model.Computer;
import model.HistoryEntry;
import model.HistoryEntry.ActionType;
import model.User;
import model.DatabaseHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryController {

    private final List<Computer> computerList;
    private final List<HistoryEntry> historyList;
    private final List<User> users;
    private final DatabaseHelper dbHelper;
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";
    private String currentUser;

    public InventoryController() {
        this.dbHelper = new DatabaseHelper();
        this.dbHelper.createTable();
        // Carrega os computadores persistidos
        this.computerList = new ArrayList<>(dbHelper.loadComputers());
        // Carrega o histórico persistido
        this.historyList = new ArrayList<>(dbHelper.loadHistory());
        // Carrega os usuários persistidos
        this.users = new ArrayList<>(dbHelper.loadUsers());
        initializeAdminUser();
    }

    private void initializeAdminUser() {
        if (users.stream().noneMatch(user -> "admin".equals(user.getUsername()))) {
            dbHelper.insertUser(ADMIN_USERNAME, ADMIN_PASSWORD);
            users.add(new User(ADMIN_USERNAME, ADMIN_PASSWORD));
        }
    }

    public List<Computer> getComputerList() {
        return computerList;
    }

    public List<HistoryEntry> getHistoryList() {
        return historyList;
    }

    public List<User> getUsers() {
        return users;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public boolean authenticate(String username, String password) {
        return users.stream()
                .anyMatch(user -> user.getUsername().equals(username) && user.getPassword().equals(password));
    }

    public boolean isAdmin(String username, String password) {
        return ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password);
    }

    public void addUser(String username, String password) {
        if (!dbHelper.isUserExists(username)) {
            dbHelper.insertUser(username, password);
            users.add(new User(username, password));
        } else {
            throw new IllegalArgumentException("Usuário já cadastrado.");
        }
    }

    public void editUserPassword(String username, String newPassword) {
        if (dbHelper.editUserPassword(username, newPassword)) {
            users.stream()
                    .filter(user -> user.getUsername().equals(username))
                    .findFirst()
                    .ifPresent(user -> user.setPassword(newPassword));
        } else {
            throw new IllegalArgumentException("Erro ao alterar senha do usuário: " + username);
        }
    }

    public void deleteUser(String username) {
        if (dbHelper.deleteUser(username)) {
            users.removeIf(user -> user.getUsername().equals(username));
        } else {
            throw new IllegalArgumentException("Erro ao excluir o usuário: " + username);
        }
    }

    public int getUserCount() {
        return users.size();
    }

    public List<String> getUsernames() {
        return users.stream().map(User::getUsername).collect(Collectors.toList());
    }

    public int getComputerCount() {
        return computerList.size();
    }

    /**
     * Adiciona um computador, persistindo-o no banco.
     */
    public void addComputer(Computer computer, String user) {
        if (isValidUser(user)) {
            computerList.add(computer);
            dbHelper.insertComputer(computer);
            addHistory(ActionType.ADICIONAR, user, "Adicionado computador: " + computer.getTag());
        }
    }

    public void editComputer(Computer oldComputer, Computer updatedComputer, String user) {
        if (isValidUser(user)) {
            // Preserva o ID do computador original
            updatedComputer.setId(oldComputer.getId());

            if (dbHelper.updateComputer(updatedComputer)) {
                // Atualiza a lista em memória
                int index = computerList.indexOf(oldComputer);
                if (index >= 0) {
                    computerList.set(index, updatedComputer);
                    addHistory(ActionType.EDITAR, user, "Editado computador: " + oldComputer.getTag());
                } else {
                    // Se não achar na lista (estranho, mas possível), recarrega tudo
                    refreshComputers();
                }
            } else {
                log("Erro ao atualizar computador no banco de dados.");
            }
        }
    }

    public boolean updateComputer(Computer computer, String user) {
        if (isValidUser(user)) {
            if (dbHelper.updateComputer(computer)) {
                // A lista em memória já tem o objeto atualizado (pois é passado por
                // referência),
                // mas precisamos registrar o histórico.
                addHistory(ActionType.EDITAR, user, "Atualizado observação: " + computer.getTag());
                return true;
            } else {
                log("Erro ao atualizar computador no banco de dados.");
                return false;
            }
        }
        return false;
    }

    public void deleteComputer(Computer computer, String user) {
        if (isValidUser(user)) {
            // Tenta remover do banco de dados
            if (dbHelper.deleteComputer(computer)) {
                // Se remover do banco, remove da lista em memória e registra o histórico
                computerList.remove(computer);
                addHistory(ActionType.EXCLUIR, user, "Excluído computador: " + computer.getTag());
            } else {
                log("Erro ao remover o computador do banco.");
            }
        }
    }

    public List<Computer> searchComputers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(computerList);
        }
        String lowerQuery = query.toLowerCase();
        return computerList.stream().filter(computer -> computer.getTag().toLowerCase().contains(lowerQuery) ||
                computer.getModel().toLowerCase().contains(lowerQuery) ||
                computer.getBrand().toLowerCase().contains(lowerQuery) ||
                computer.getUserName().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }

    public List<Computer> getComputersByLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            return new ArrayList<>(computerList);
        }
        String loc = location.trim();
        return computerList.stream()
                .filter(computer -> computer.getLocation() != null
                        && computer.getLocation().trim().equalsIgnoreCase(loc))
                .collect(Collectors.toList());
    }

    public void exportToCSV(List<Computer> computersToExport, String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(
                "Etiqueta TI;Modelo;Marca;Estado;Usuário;Número de Série;Versão do Windows;Versão do Office;Localização;Data de Compra;Observações\n");
        for (Computer computer : computersToExport) {
            sb.append(formatCSV(computer)).append("\n");
        }
        Files.write(Paths.get(filePath), sb.toString().getBytes("UTF-8"));
        log("Dados exportados para o arquivo: " + filePath);
    }

    public void backupData(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("INVENTARIO\n");
        sb.append(
                "Etiqueta TI;Modelo;Marca;Estado;Usuário;Número de Série;Versão do Windows;Versão do Office;Localização;Data de Compra;Observações\n");
        for (Computer computer : computerList) {
            sb.append(formatCSV(computer)).append("\n");
        }

        sb.append("\nHISTORICO\n");
        sb.append("Action;User;Timestamp;Description\n");
        for (HistoryEntry history : historyList) {
            sb.append(formatCSV(history)).append("\n");
        }

        Files.write(Paths.get(filePath), sb.toString().getBytes("UTF-8"));
        log("Backup realizado no arquivo: " + filePath);
    }

    public void restoreData(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        boolean isInventory = false, isHistory = false;

        computerList.clear();
        historyList.clear();

        for (String line : lines) {
            if ("INVENTARIO".equalsIgnoreCase(line)) {
                isInventory = true;
                isHistory = false;
            } else if ("HISTORICO".equalsIgnoreCase(line)) {
                isInventory = false;
                isHistory = true;
            } else if (!line.isBlank() && !line.startsWith("Etiqueta") && !line.startsWith("Action")) {
                if (isInventory) {
                    computerList.add(parseComputer(line));
                } else if (isHistory) {
                    historyList.add(parseHistory(line));
                }
            }
        }
        log("Dados restaurados do arquivo: " + filePath);
    }

    /**
     * Adiciona um registro de histórico, persistindo-o no banco.
     */
    public void addHistory(ActionType action, String user, String description) {
        HistoryEntry entry = new HistoryEntry(action, user, LocalDateTime.now(), description);
        historyList.add(entry);
        dbHelper.insertHistory(entry);
        log("Histórico adicionado: " + action + " - " + description);
    }

    /**
     * Atualiza a lista de computadores carregando-os novamente do banco de dados.
     */
    public void refreshComputers() {
        List<Computer> loaded = dbHelper.loadComputers();
        computerList.clear();
        computerList.addAll(loaded);
    }

    private String formatCSV(Computer computer) {
        return String.format("\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\"",
                computer.getTag(), computer.getModel(), computer.getBrand(), computer.getState(),
                computer.getUserName(), computer.getSerialNumber(), computer.getWindowsVersion(),
                computer.getOfficeVersion(), computer.getLocation(), computer.getPurchaseDate(),
                computer.getObservation() != null ? computer.getObservation() : "");
    }

    private String formatCSV(HistoryEntry history) {
        return String.format("\"%s\";\"%s\";\"%s\";\"%s\"",
                history.getAction(), history.getUser(), history.getTimestamp(), history.getDescription());
    }

    private Computer parseComputer(String line) {
        String[] data = line.split(";", -1);
        String observation = "";
        if (data.length > 10) {
            observation = data[10].replace("\"", "");
        }
        return new Computer(
                data[0].replace("\"", ""), // tag
                data[1].replace("\"", ""), // model
                data[2].replace("\"", ""), // brand
                data[3].replace("\"", ""), // state
                data[4].replace("\"", ""), // user
                data[5].replace("\"", ""), // serial
                data[6].replace("\"", ""), // win
                data[7].replace("\"", ""), // office
                data[9].replace("\"", ""), // purchase (index 9)
                data[8].replace("\"", ""), // location (index 8)
                observation // observation
        );
    }

    private HistoryEntry parseHistory(String line) {
        String[] data = line.split(";", -1);
        return new HistoryEntry(ActionType.valueOf(data[0].replace("\"", "").toUpperCase()),
                data[1].replace("\"", ""), LocalDateTime.parse(data[2].replace("\"", "")),
                data[3].replace("\"", ""));
    }

    private boolean isValidUser(String user) {
        if (user == null || user.isEmpty()) {
            log("Erro: Operação sem usuário válido.");
            return false;
        }
        return true;
    }

    private void log(String message) {
        System.out.println(message);
    }
}
