package util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Computer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVExporter {

    // Método para exportar dados para CSV
    public static void exportToCSV(List<Computer> computerList, String filePath) throws IOException {
        FileWriter writer = new FileWriter(filePath);

        // Cabeçalhos do CSV
        writer.append("Etiqueta TI,Modelo,Marca,Estado,Usuário,Número de Série,Versão do Windows,Versão do Office,Localização,Data de Compra\n");

        // Dados do CSV
        for (Computer computer : computerList) {
            writer.append(computer.getTag()).append(",");
            writer.append(computer.getModel()).append(",");
            writer.append(computer.getBrand()).append(",");
            writer.append(computer.getState()).append(",");
            writer.append(computer.getUserName()).append(",");
            writer.append(computer.getSerialNumber()).append(",");
            writer.append(computer.getWindowsVersion()).append(",");
            writer.append(computer.getOfficeVersion()).append(",");
            writer.append(computer.getLocation()).append(",");
            writer.append(computer.getPurchaseDate()).append("\n");
        }

        writer.flush();
        writer.close();
    }

    // Método para importar dados de um CSV
    public static ObservableList<Computer> importFromCSV(String filePath) throws IOException {
        ObservableList<Computer> computers = FXCollections.observableArrayList();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;

        // Ignorar a primeira linha (cabeçalhos)
        reader.readLine(); // Isso pula a primeira linha do arquivo CSV

        while ((line = reader.readLine()) != null) {
            String[] fields = line.split(","); // Supondo que o separador seja vírgula
            if (fields.length == 10) {  // Verifica se temos todas as 10 colunas de dados
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
            }
        }
        reader.close();
        return computers;
    }

}
