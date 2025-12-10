package util;

import model.Computer;
import model.HistoryEntry;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CSVExporter {

    // Método para exportar dados para CSV
    public static void exportToCSV(List<Computer> computerList, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, StandardCharsets.UTF_8))) {
            // Escreve o cabeçalho (adicionado STATUS)
            writer.write(
                    "ETIQUETA TI;NOME DO PC;USUÁRIO;LOCALIZAÇÃO;SETOR;VERSÃO DO WINDOWS;VERSÃO DO OFFICE;MODELO;NÚMERO DE SÉRIE;DATA DE COMPRA;TEMPO DE USO;PATRIMÔNIO;OBSERVAÇÕES;STATUS");
            writer.newLine();

            // Escreve os dados dos computadores
            for (Computer computer : computerList) {
                String line = String.format(
                        "\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\"",
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
                        computer.getDetailedUsageTime(), // Alterado para tempo detalhado
                        computer.getPatrimony() != null ? computer.getPatrimony() : "",
                        computer.getObservation() != null ? computer.getObservation() : "",
                        computer.getActivityStatus() != null ? computer.getActivityStatus() : "Ativo"); // Status
                writer.write(line);
                writer.newLine();
            }
        }
    }

    // Método para importar dados de um CSV
    public static List<Computer> importFromCSV(String filePath) throws IOException {
        List<Computer> computers = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            // Ignorar a primeira linha (cabeçalhos)
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] fields = parseCSVLine(line);
                if (fields.length == 10) {
                    Computer computer = new Computer(
                            fields[0], // etiqueta
                            fields[1], // modelo
                            fields[2], // marca
                            fields[3], // estado
                            fields[4], // usuario
                            fields[5], // serie
                            fields[6], // win
                            fields[7], // office
                            fields[8], // localizacao
                            fields[9] // compra
                    );
                    computers.add(computer);
                } else {
                    System.err.println("Linha ignorada (formato inválido): " + line);
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
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }
}
