package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Computer;
import model.HistoryEntry;
import model.HistoryEntry.ActionType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

public class InventoryController {

    private ObservableList<Computer> computerList;
    private ObservableList<HistoryEntry> historyList;
    private String currentUser;

    public InventoryController(ObservableList<Computer> computerList) {
        this.computerList = computerList;
        this.historyList = FXCollections.observableArrayList();
    }

    public ObservableList<Computer> getComputerList() {
        return computerList;
    }

    public ObservableList<HistoryEntry> getHistoryList() {
        return historyList;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
        log("Usuário logado: " + currentUser);
    }

    public void addComputer(Computer computer, String user) {
        if (isValidUser(user)) {
            computerList.add(computer);
            log("Computador adicionado: " + computer);
            addHistory(ActionType.ADICIONAR, user, "Adicionado computador: " + computer.getTag());
        }
    }

    public void editComputer(Computer oldComputer, Computer updatedComputer, String user) {
        if (isValidUser(user)) {
            int index = computerList.indexOf(oldComputer);
            if (index >= 0) {
                computerList.set(index, updatedComputer);
                log("Computador editado: " + oldComputer + " -> " + updatedComputer);
                addHistory(ActionType.EDITAR, user, "Editado computador: " + oldComputer.getTag());
            } else {
                log("Erro: Computador para edição não encontrado.");
            }
        }
    }

    public void deleteComputer(Computer computer, String user) {
        if (isValidUser(user)) {
            computerList.remove(computer);
            log("Computador excluído: " + computer);
            addHistory(ActionType.EXCLUIR, user, "Excluído computador: " + computer.getTag());
        }
    }

    public ObservableList<Computer> searchComputers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return computerList; // Retorna a lista completa se a consulta for vazia
        }
        return computerList.filtered(computer ->
                computer.getTag().toLowerCase().contains(query.toLowerCase()) ||
                        computer.getModel().toLowerCase().contains(query.toLowerCase()) ||
                        computer.getBrand().toLowerCase().contains(query.toLowerCase()) ||
                        computer.getUserName().toLowerCase().contains(query.toLowerCase())
        );
    }

    public void exportToCSV(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("Etiqueta TI;Modelo;Marca;Estado;Usuário;Número de Série;Versão do Windows;Versão do Office;Localização;Data de Compra\n");

        for (Computer computer : computerList) {
            sb.append(String.format("\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\"\n",
                    computer.getTag(), computer.getModel(), computer.getBrand(), computer.getState(),
                    computer.getUserName(), computer.getSerialNumber(), computer.getWindowsVersion(),
                    computer.getOfficeVersion(), computer.getLocation(), computer.getPurchaseDate()));
        }

        Files.write(Paths.get(filePath), sb.toString().getBytes("UTF-8"));
        log("Dados exportados para o arquivo: " + filePath);
    }

    public void backupData(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("INVENTARIO\n");
        sb.append("Etiqueta TI;Modelo;Marca;Estado;Usuário;Número de Série;Versão do Windows;Versão do Office;Localização;Data de Compra\n");

        for (Computer computer : computerList) {
            sb.append(String.format("\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\"\n",
                    computer.getTag(), computer.getModel(), computer.getBrand(), computer.getState(),
                    computer.getUserName(), computer.getSerialNumber(), computer.getWindowsVersion(),
                    computer.getOfficeVersion(), computer.getLocation(), computer.getPurchaseDate()));
        }

        sb.append("\nHISTORICO\n");
        sb.append("Action;User;Timestamp;Description\n");

        for (HistoryEntry history : historyList) {
            sb.append(String.format("\"%s\";\"%s\";\"%s\";\"%s\"\n",
                    history.getAction(), history.getUser(), history.getTimestamp(), history.getDescription()));
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
            if (line.equalsIgnoreCase("INVENTARIO")) {
                isInventory = true;
                isHistory = false;
            } else if (line.equalsIgnoreCase("HISTORICO")) {
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
        if (historyList == null) {
            historyList = FXCollections.observableArrayList();
        }
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
