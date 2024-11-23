package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Computer;
import model.HistoryEntry;
import util.CSVExporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

public class InventoryController {

    private ObservableList<Computer> computerList;
    private ObservableList<HistoryEntry> historyList;

    public ObservableList<Computer> getComputerList() {
        return computerList;
    }

    public InventoryController(ObservableList<Computer> computerList) {
        this.computerList = computerList;
        this.historyList = FXCollections.observableArrayList();  // Inicializa a lista de histórico
    }

    public ObservableList<HistoryEntry> getHistoryList() {
        return historyList;
    }

    // Método para adicionar um computador
    public void addComputer(Computer computer, String user) {
        computerList.add(computer);
        historyList.add(new HistoryEntry("Adicionar", user, LocalDateTime.now(), "Adicionou o computador: " + computer.getTag()));
    }

    // Método para editar um computador
    public void editComputer(Computer oldComputer, Computer updatedComputer, String user) {
        int index = computerList.indexOf(oldComputer);
        if (index >= 0) {
            computerList.set(index, updatedComputer);
            historyList.add(new HistoryEntry("Editar", user, LocalDateTime.now(), "Editou o computador: " + oldComputer.getTag()));
        }
    }

    // Método para excluir um computador
    public void deleteComputer(Computer computer, String user) {
        computerList.remove(computer);
        historyList.add(new HistoryEntry("Excluir", user, LocalDateTime.now(), "Excluiu o computador: " + computer.getTag()));
    }

    // Método para exportar a lista de computadores para CSV
    public void exportToCSV(String filePath) throws IOException {
        CSVExporter.exportToCSV(computerList, filePath);
    }

    // Método para buscar computadores
    public ObservableList<Computer> searchComputers(String query) {
        return computerList.filtered(computer ->
                computer.getTag().toLowerCase().contains(query.toLowerCase()) ||
                        computer.getModel().toLowerCase().contains(query.toLowerCase()) ||
                        computer.getBrand().toLowerCase().contains(query.toLowerCase()) ||
                        computer.getUserName().toLowerCase().contains(query.toLowerCase())
        );
    }

    // Função para realizar backup dos dados
    public void backupData(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("INVENTARIO\n");
        sb.append("Tag,Serial Number,Model,Brand,State,User Name,Windows Version,Office Version,Location,Purchase Date\n");

        for (Computer computer : computerList) {
            sb.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                    computer.getTag() == null ? "" : computer.getTag(),
                    computer.getSerialNumber() == null ? "" : computer.getSerialNumber(),
                    computer.getModel() == null ? "" : computer.getModel(),
                    computer.getBrand() == null ? "" : computer.getBrand(),
                    computer.getState() == null ? "" : computer.getState(),
                    computer.getUserName() == null ? "" : computer.getUserName(),
                    computer.getWindowsVersion() == null ? "" : computer.getWindowsVersion(),
                    computer.getOfficeVersion() == null ? "" : computer.getOfficeVersion(),
                    computer.getLocation() == null ? "" : computer.getLocation(),
                    computer.getPurchaseDate() == null ? "" : computer.getPurchaseDate()
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

    // Função para restaurar dados de um arquivo CSV
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

            String[] data = line.split(",", -1); // -1 mantém campos vazios

            if (isInventory) {
                while (data.length < 10) {
                    // Garante que a linha tenha 10 colunas
                    data = expandArray(data, 10, "N/A");
                }

                Computer computer = new Computer(
                        data[0].isBlank() ? "N/A" : data[0],
                        data[1].isBlank() ? "N/A" : data[1],
                        data[2].isBlank() ? "N/A" : data[2],
                        data[3].isBlank() ? "N/A" : data[3],
                        data[4].isBlank() ? "N/A" : data[4],
                        data[5].isBlank() ? "N/A" : data[5],
                        data[6].isBlank() ? "N/A" : data[6],
                        data[7].isBlank() ? "N/A" : data[7],
                        data[8].isBlank() ? "N/A" : data[8],
                        data[9].isBlank() ? "N/A" : data[9]
                );
                computerList.add(computer);
            }

            if (isHistory) {
                if (data.length >= 4) {
                    try {
                        HistoryEntry history = new HistoryEntry(
                                data[0], data[1], LocalDateTime.parse(data[2]), data[3]
                        );
                        historyList.add(history);
                    } catch (Exception e) {
                        System.out.println("Erro ao restaurar histórico: " + line);
                    }
                }
            }
        }
    }

    // Expande um array com um valor padrão até o tamanho especificado
    private String[] expandArray(String[] array, int newSize, String defaultValue) {
        String[] expanded = new String[newSize];
        for (int i = 0; i < newSize; i++) {
            expanded[i] = i < array.length ? array[i] : defaultValue;
        }
        return expanded;
    }

    public void addHistory(String action, String user, String description) {
        HistoryEntry historyEntry = new HistoryEntry(action, user, LocalDateTime.now(), description);
        historyList.add(historyEntry);
    }
}
