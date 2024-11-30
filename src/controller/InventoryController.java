package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Computer;
import model.HistoryEntry;
import model.HistoryEntry.ActionType;
import util.CSVExporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

public class InventoryController {

    private ObservableList<Computer> computerList;
    private ObservableList<HistoryEntry> historyList;
    private String currentUser; // Adicionado para rastrear o usuário atual

    public InventoryController(ObservableList<Computer> computerList) {
        this.computerList = computerList;
        this.historyList = FXCollections.observableArrayList(); // Inicializa a lista de histórico
    }

    public ObservableList<Computer> getComputerList() {
        return computerList;
    }

    public ObservableList<HistoryEntry> getHistoryList() {
        return historyList;
    }

    // Métodos para gerenciar o usuário atual
    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    // Método para adicionar um computador
    public void addComputer(Computer computer, String user) {
        computerList.add(computer);
        addHistory(ActionType.ADICIONAR, user, "Adicionado computador: " + computer.getTag());
    }

    // Método para editar um computador
    public void editComputer(Computer oldComputer, Computer updatedComputer, String user) {
        int index = computerList.indexOf(oldComputer);
        if (index >= 0) {
            computerList.set(index, updatedComputer);
            addHistory(ActionType.EDITAR, user, String.format("Editado computador [%s]", oldComputer.getTag()));
        }
    }

    // Método para excluir um computador
    public void deleteComputer(Computer computer, String user) {
        computerList.remove(computer);
        addHistory(ActionType.EXCLUIR, user, "Excluído computador: " + computer.getTag());
    }

    // Métodos de exportação e importação de dados
    public void exportToCSV(String filePath) throws IOException {
        CSVExporter.exportToCSV(computerList, filePath);
    }

    public ObservableList<Computer> searchComputers(String query) {
        return computerList.filtered(computer ->
                computer.getTag().toLowerCase().contains(query.toLowerCase()) ||
                        computer.getModel().toLowerCase().contains(query.toLowerCase()) ||
                        computer.getBrand().toLowerCase().contains(query.toLowerCase()) ||
                        computer.getUserName().toLowerCase().contains(query.toLowerCase())
        );
    }

    // Backup e restauração
    public void backupData(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("INVENTARIO\n");
        sb.append("Tag,Serial Number,Model,Brand,State,User Name,Windows Version,Office Version,Location,Purchase Date\n");

        for (Computer computer : computerList) {
            sb.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                    computer.getTag(),
                    computer.getSerialNumber(),
                    computer.getModel(),
                    computer.getBrand(),
                    computer.getState(),
                    computer.getUserName(),
                    computer.getWindowsVersion(),
                    computer.getOfficeVersion(),
                    computer.getLocation(),
                    computer.getPurchaseDate()
            ));
        }

        sb.append("\nHISTORICO\n");
        sb.append("Action,User,Timestamp,Description\n");
        for (HistoryEntry history : historyList) {
            sb.append(String.format("%s,%s,%s,%s\n",
                    history.getAction(), history.getUser(),
                    history.getTimestamp(), history.getDescription()));
        }

        Files.write(Paths.get(filePath), sb.toString().getBytes());
    }

    public void restoreData(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));

        computerList.clear();
        historyList.clear();

        boolean isInventory = false;
        boolean isHistory = false;

        for (String line : lines) {
            if (line.equals("INVENTARIO")) {
                isInventory = true;
                isHistory = false;
                continue;
            }
            if (line.equals("HISTORICO")) {
                isInventory = false;
                isHistory = true;
                continue;
            }
            if (line.isBlank() || line.startsWith("Tag") || line.startsWith("Action")) {
                continue;
            }

            String[] data = line.split(",", -1);

            if (isInventory) {
                Computer computer = new Computer(
                        data[0], data[1], data[2], data[3], data[4],
                        data[5], data[6], data[7], data[8], data[9]
                );
                computerList.add(computer);
            }

            if (isHistory && data.length >= 4) {
                try {
                    ActionType actionType = ActionType.valueOf(data[0].toUpperCase());
                    HistoryEntry history = new HistoryEntry(
                            actionType, data[1], LocalDateTime.parse(data[2]), data[3]
                    );
                    historyList.add(history);
                } catch (Exception e) {
                    System.out.println("Erro ao restaurar histórico: " + line);
                }
            }
        }
    }

    public void addHistory(ActionType action, String user, String description) {
        historyList.add(new HistoryEntry(action, user, description));
    }
}
