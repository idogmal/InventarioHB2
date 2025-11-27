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
            // Tenta remover do banco de dados (soft delete)
            if (dbHelper.deleteComputer(computer)) {
                // Remove da lista principal
                computerList.remove(computer);
                addHistory(ActionType.EXCLUIR, user, "Movido para a lixeira: " + computer.getTag());
            } else {
                log("Erro ao mover o computador para a lixeira.");
            }
        }
    }

    public void restoreComputer(Computer computer, String user) {
        if (isValidUser(user)) {
            if (dbHelper.restoreComputer(computer)) {
                computer.setDeleted(false);
                computerList.add(computer);
                addHistory(ActionType.EDITAR, user, "Restaurado da lixeira: " + computer.getTag());
            } else {
                log("Erro ao restaurar o computador.");
            }
        }
    }

    public List<Computer> getDeletedComputers() {
        return dbHelper.loadDeletedComputers();
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

    public List<String> getCompanies() {
        return dbHelper.getCompanies();
    }

    public boolean addCompany(String name) {
        return dbHelper.addCompany(name);
    }

    public boolean deleteCompany(String name) {
        return dbHelper.deleteCompany(name);
    }

    public void exportToCSV(List<Computer> computersToExport, String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(
                "ETIQUETA TI;NOME DO PC;USUÁRIO;LOCALIZAÇÃO;SETOR;VERSÃO DO WINDOWS;VERSÃO DO OFFICE;MODELO;NÚMERO DE SÉRIE;DATA DE COMPRA;PATRIMÔNIO;OBSERVAÇÕES\n");
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
                "ETIQUETA TI;NOME DO PC;USUÁRIO;LOCALIZAÇÃO;SETOR;VERSÃO DO WINDOWS;VERSÃO DO OFFICE;MODELO;NÚMERO DE SÉRIE;DATA DE COMPRA;PATRIMÔNIO;OBSERVAÇÕES\n");
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

    public void refreshHistory() {
        List<HistoryEntry> loaded = dbHelper.loadHistory();
        historyList.clear();
        historyList.addAll(loaded);
    }

    private String formatCSV(Computer computer) {
        return String.format("\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\"",
                computer.getTag(),
                computer.getHostname() != null ? computer.getHostname() : "",
                computer.getUserName(),
                computer.getLocation(),
                computer.getSector() != null ? computer.getSector() : "",
                computer.getWindowsVersion(),
                computer.getOfficeVersion(),
                computer.getModel(),
                computer.getSerialNumber(),
                computer.getPurchaseDate(),
                computer.getPatrimony() != null ? computer.getPatrimony() : "",
                computer.getObservation() != null ? computer.getObservation() : "");
    }

    private String formatCSV(HistoryEntry history) {
        return String.format("\"%s\";\"%s\";\"%s\";\"%s\"",
                history.getAction(), history.getUser(), history.getTimestamp(), history.getDescription());
    }

    private Computer parseComputer(String line) {
        String[] data = line.split(";", -1);
        // Remove aspas
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i].replace("\"", "");
        }

        // Verifica se é o novo formato (12 colunas) ou antigo (11 colunas)
        if (data.length >= 12) {
            return new Computer(
                    data[0], // etiqueta
                    data[7], // modelo (agora index 7)
                    "", // marca (removido do CSV, default vazio)
                    "", // estado (removido do CSV, default vazio)
                    data[2], // usuario (index 2)
                    data[8], // serie (index 8)
                    data[5], // win (index 5)
                    data[6], // office (index 6)
                    data[9], // compra (index 9)
                    data[3], // localizacao (index 3)
                    data[11], // observacao (index 11)
                    data[1], // hostname (index 1)
                    data[4], // setor (index 4)
                    data[10] // patrimonio (index 10)
            );
        } else {
            // Formato antigo (compatibilidade)
            String observation = "";
            if (data.length > 10) {
                observation = data[10];
            }
            return new Computer(
                    data[0], // etiqueta
                    data[1], // modelo
                    data[2], // marca
                    data[3], // estado
                    data[4], // usuario
                    data[5], // serie
                    data[6], // win
                    data[7], // office
                    data[9], // compra
                    data[8], // localizacao
                    observation,
                    "", "", "" // Novos campos vazios
            );
        }
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
