package controller;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import model.Computer;
import model.HistoryEntry;
import util.CSVExporter;

import java.time.LocalDateTime;
import java.io.IOException;

public class InventoryController {

    private ObservableList<Computer> computerList;
    private ObservableList<HistoryEntry> historyList;

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
        CSVExporter.exportToCSV(computerList, filePath);  // Reutilizando a exportação para CSV como backup
    }

    // Função para restaurar dados de um arquivo CSV
    public void restoreData(String filePath) throws IOException {
        ObservableList<Computer> restoredList = CSVExporter.importFromCSV(filePath);
        computerList.setAll(restoredList);  // Substitui a lista atual pela lista restaurada
    }

    public void addHistory(String action, String user, String description) {
        HistoryEntry historyEntry = new HistoryEntry(action, user, LocalDateTime.now(), description);
        historyList.add(historyEntry);
    }

}
