package controller;

import javafx.collections.ObservableList;
import model.Computer;
import util.CSVExporter;

import java.io.IOException;

public class InventoryController {

    private ObservableList<Computer> computerList;

    public InventoryController(ObservableList<Computer> computerList) {
        this.computerList = computerList;
    }

    public void addComputer(Computer computer) {
        computerList.add(computer);
    }

    public void editComputer(Computer computer, Computer updatedComputer) {
        int index = computerList.indexOf(computer);
        if (index >= 0) {
            computerList.set(index, updatedComputer);
        }
    }

    public void deleteComputer(Computer computer) {
        computerList.remove(computer);
    }

    public void exportToCSV(String filePath) throws IOException {
        CSVExporter.exportToCSV(computerList, filePath);
    }

    public ObservableList<Computer> getComputerList() {
        return computerList;
    }

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

    // Função para restaurar dados de um arquivo
    public void restoreData(String filePath) throws IOException {
        ObservableList<Computer> restoredList = CSVExporter.importFromCSV(filePath);
        computerList.setAll(restoredList);  // Substitui a lista atual pela lista restaurada
    }


}
