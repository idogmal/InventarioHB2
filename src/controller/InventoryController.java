package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Computer;
import model.HistoryEntry;
import model.HistoryEntry.ActionType;
import model.User;
import model.DatabaseHelper;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class InventoryController {

    private final ObservableList<Computer> computerList;
    private final ObservableList<HistoryEntry> historyList;
    private final ObservableList<User> users;
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";
    private String currentUser;

    public InventoryController() {
        this.computerList = FXCollections.observableArrayList();
        this.historyList = FXCollections.observableArrayList();
        this.users = FXCollections.observableArrayList();
        loadUsersFromDatabase();
        initializeAdminUser();
    }

    private void loadUsersFromDatabase() {
        DatabaseHelper dbHelper = new DatabaseHelper();
        users.addAll(dbHelper.loadUsers());
    }

    private void initializeAdminUser() {
        if (users.stream().noneMatch(user -> "admin".equals(user.getUsername()))) {
            users.add(new User(ADMIN_USERNAME, ADMIN_PASSWORD));
        }
    }

    public ObservableList<Computer> getComputerList() {
        return computerList;
    }

    public ObservableList<HistoryEntry> getHistoryList() {
        return historyList;
    }

    public ObservableList<User> getUsers() {
        return users;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
        log("Usuário logado: " + currentUser);
    }

    public boolean authenticate(String username, String password) {
        return users.stream().anyMatch(user ->
                user.getUsername().equals(username) && user.getPassword().equals(password));
    }

    public boolean isAdmin(String username, String password) {
        return ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password);
    }

    public void addComputer(Computer computer, String user) {
        if (isValidUser(user)) {
            computerList.add(computer);
            addHistory(ActionType.ADICIONAR, user, "Adicionado computador: " + computer.getTag());
        }
    }

    public void editComputer(Computer oldComputer, Computer updatedComputer, String user) {
        if (isValidUser(user)) {
            int index = computerList.indexOf(oldComputer);
            if (index >= 0) {
                computerList.set(index, updatedComputer);
                addHistory(ActionType.EDITAR, user, "Editado computador: " + oldComputer.getTag());
            } else {
                log("Erro: Computador para edição não encontrado.");
            }
        }
    }

    public void deleteComputer(Computer computer, String user) {
        if (isValidUser(user)) {
            computerList.remove(computer);
            addHistory(ActionType.EXCLUIR, user, "Excluído computador: " + computer.getTag());
        }
    }

    public ObservableList<Computer> searchComputers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return computerList;
        }
        String lowerQuery = query.toLowerCase();
        return computerList.filtered(computer ->
                computer.getTag().toLowerCase().contains(lowerQuery) ||
                        computer.getModel().toLowerCase().contains(lowerQuery) ||
                        computer.getBrand().toLowerCase().contains(lowerQuery) ||
                        computer.getUserName().toLowerCase().contains(lowerQuery));
    }

    public void exportToCSV(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("Etiqueta TI;Modelo;Marca;Estado;Usuário;Número de Série;Versão do Windows;Versão do Office;Localização;Data de Compra\n");
        computerList.forEach(computer -> sb.append(formatCSV(computer)).append("\n"));
        Files.write(Paths.get(filePath), sb.toString().getBytes("UTF-8"));
        log("Dados exportados para o arquivo: " + filePath);
    }

    public void backupData(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("INVENTARIO\n");
        sb.append("Etiqueta TI;Modelo;Marca;Estado;Usuário;Número de Série;Versão do Windows;Versão do Office;Localização;Data de Compra\n");
        computerList.forEach(computer -> sb.append(formatCSV(computer)).append("\n"));

        sb.append("\nHISTORICO\n");
        sb.append("Action;User;Timestamp;Description\n");
        historyList.forEach(history -> sb.append(formatCSV(history)).append("\n"));

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

    public void addUser(String username, String password) {
        if (users.stream().noneMatch(user -> user.getUsername().equals(username))) {
            users.add(new User(username, password));
            DatabaseHelper dbHelper = new DatabaseHelper();
            dbHelper.insertUser(username, password);
        } else {
            throw new IllegalArgumentException("Usuário já cadastrado.");
        }
    }

    public void editUserPassword(String username, String newPassword) {
        users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .ifPresentOrElse(user -> user.setPassword(newPassword),
                        () -> { throw new IllegalArgumentException("Usuário não encontrado."); });
    }

    public void deleteUser(String username) {
        users.removeIf(user -> user.getUsername().equals(username) && !isAdmin(username, ADMIN_PASSWORD));
    }

    public int getUserCount() {
        return users.size();
    }

    public List<String> getUsernames() {
        return users.stream().map(User::getUsername).toList();
    }

    public int getComputerCount() {
        return computerList.size();
    }

    private String formatCSV(Computer computer) {
        return String.format("\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\"",
                computer.getTag(), computer.getModel(), computer.getBrand(), computer.getState(),
                computer.getUserName(), computer.getSerialNumber(), computer.getWindowsVersion(),
                computer.getOfficeVersion(), computer.getLocation(), computer.getPurchaseDate());
    }

    private String formatCSV(HistoryEntry history) {
        return String.format("\"%s\";\"%s\";\"%s\";\"%s\"",
                history.getAction(), history.getUser(), history.getTimestamp(), history.getDescription());
    }

    private Computer parseComputer(String line) {
        String[] data = line.split(";", -1);
        return new Computer(data[0].replace("\"", ""), data[1].replace("\"", ""), data[2].replace("\"", ""),
                data[3].replace("\"", ""), data[4].replace("\"", ""), data[5].replace("\"", ""),
                data[6].replace("\"", ""), data[7].replace("\"", ""), data[8].replace("\"", ""),
                data[9].replace("\"", ""));
    }

    private HistoryEntry parseHistory(String line) {
        String[] data = line.split(";", -1);
        return new HistoryEntry(ActionType.valueOf(data[0].replace("\"", "").toUpperCase()),
                data[1].replace("\"", ""), LocalDateTime.parse(data[2].replace("\"", "")),
                data[3].replace("\"", ""));
    }

    public void addHistory(ActionType action, String user, String description) {
        historyList.add(new HistoryEntry(action, user, LocalDateTime.now(), description));
        log("Histórico adicionado: " + action + " - " + description);
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
