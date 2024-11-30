package util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Computer;
import model.HistoryEntry;

import java.io.*;
import java.time.LocalDateTime;
import java.util.List;

public class CSVExporter {

    // Método para exportar dados para CSV
    public static void exportToCSV(List<Computer> computerList, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {

            // Cabeçalhos do CSV
            writer.append("\"Etiqueta TI\",\"Modelo\",\"Marca\",\"Estado\",\"Usuário\",\"Número de Série\",")
                    .append("\"Versão do Windows\",\"Versão do Office\",\"Localização\",\"Data de Compra\"\n");

            // Dados do CSV
            for (Computer computer : computerList) {
                writer.append(formatCSVField(computer.getTag()))
                        .append(",").append(formatCSVField(computer.getModel()))
                        .append(",").append(formatCSVField(computer.getBrand()))
                        .append(",").append(formatCSVField(computer.getState()))
                        .append(",").append(formatCSVField(computer.getUserName()))
                        .append(",").append(formatCSVField(computer.getSerialNumber()))
                        .append(",").append(formatCSVField(computer.getWindowsVersion()))
                        .append(",").append(formatCSVField(computer.getOfficeVersion()))
                        .append(",").append(formatCSVField(computer.getLocation()))
                        .append(",").append(formatCSVField(computer.getPurchaseDate()))
                        .append("\n");
            }
        }
    }

    // Método para importar dados de um CSV
    public static ObservableList<Computer> importFromCSV(String filePath) throws IOException {
        ObservableList<Computer> computers = FXCollections.observableArrayList();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            // Ignorar a primeira linha (cabeçalhos)
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] fields = parseCSVLine(line);
                if (fields.length == 10) {
                    Computer computer = new Computer(
                            fields[0], // tag
                            fields[1], // model
                            fields[2], // brand
                            fields[3], // state
                            fields[4], // userName
                            fields[5], // serialNumber
                            fields[6], // windowsVersion
                            fields[7], // officeVersion
                            fields[8], // location
                            fields[9]  // purchaseDate
                    );
                    computers.add(computer);
                } else {
                    System.out.println("Linha ignorada (formato inválido): " + line);
                }
            }
        }
        return computers;
    }

    // Método para exportar histórico para CSV
    public static void exportHistoryToCSV(List<HistoryEntry> historyList, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {

            // Cabeçalhos do CSV
            writer.append("\"Ação\",\"Usuário\",\"Data e Hora\",\"Descrição\"\n");

            // Dados do histórico
            for (HistoryEntry history : historyList) {
                writer.append(formatCSVField(history.getAction().name()))
                        .append(",").append(formatCSVField(history.getUser()))
                        .append(",").append(formatCSVField(history.getTimestamp().toString()))
                        .append(",").append(formatCSVField(history.getDescription()))
                        .append("\n");
            }
        }
    }

    // Método para formatar campos de CSV
    private static String formatCSVField(String field) {
        return "\"" + (field == null ? "" : field.replace("\"", "\"\"")) + "\"";
    }

    // Método para parsear uma linha de CSV
    private static String[] parseCSVLine(String line) {
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1); // Suporta campos com aspas
    }
}
