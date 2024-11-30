package view;

import controller.InventoryController;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Computer;
import model.HistoryEntry;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ActionHandler {

    private final InventoryController controller;
    private final TableView<Computer> table;
    private final ComputerFormHandler formHandler; // Novo manipulador de formulários

    public ActionHandler(InventoryController controller, TableView<Computer> table) {
        this.controller = controller;
        this.table = table;
        this.formHandler = new ComputerFormHandler(controller); // Instância do manipulador de formulários
    }

    public void handleExportAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar arquivo CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(table.getScene().getWindow());

        if (file != null) {
            try {
                controller.exportToCSV(file.getAbsolutePath());
                showAlert("Sucesso", "Dados exportados para " + file.getName());
            } catch (IOException ex) {
                showAlert("Erro", "Não foi possível exportar os dados.");
                ex.printStackTrace();
            }
        }
    }

    public void handleEditAction() {
        Computer selectedComputer = table.getSelectionModel().getSelectedItem();
        if (selectedComputer != null) {
            // Use o ComputerFormHandler para abrir o formulário
            formHandler.openForm(selectedComputer, controller.getCurrentUser());
        } else {
            showAlert("Seleção necessária", "Por favor, selecione um computador para editar.");
        }
    }

    public void openHistoryWindow() {
        Stage historyStage = new Stage();
        historyStage.setTitle("Histórico de Alterações");

        TableView<HistoryEntry> historyTable = new TableView<>();
        historyTable.setEditable(false);

        // Colunas para a tabela de histórico
        TableColumn<HistoryEntry, String> actionColumn = new TableColumn<>("Ação");
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));

        TableColumn<HistoryEntry, String> userColumn = new TableColumn<>("Usuário");
        userColumn.setCellValueFactory(new PropertyValueFactory<>("user"));

        TableColumn<HistoryEntry, String> timestampColumn = new TableColumn<>("Data e Hora");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        timestampColumn.setCellValueFactory(cellData -> {
            LocalDateTime timestamp = cellData.getValue().getTimestamp();
            return new SimpleStringProperty(timestamp.format(formatter));
        });

        TableColumn<HistoryEntry, String> descriptionColumn = new TableColumn<>("Descrição");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        historyTable.getColumns().addAll(actionColumn, userColumn, timestampColumn, descriptionColumn);
        historyTable.setItems(controller.getHistoryList());

        VBox layout = new VBox(10, historyTable);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 600, 400);
        historyStage.setScene(scene);
        historyStage.show();
    }

    public void handleDeleteAction() {
        Computer selectedComputer = table.getSelectionModel().getSelectedItem();
        if (selectedComputer != null) {
            controller.deleteComputer(selectedComputer, controller.getCurrentUser());
        } else {
            showAlert("Seleção necessária", "Por favor, selecione um computador para excluir.");
        }
    }

    public void handleBackupAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Backup");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(table.getScene().getWindow());

        if (file != null) {
            try {
                controller.backupData(file.getAbsolutePath());
                showAlert("Sucesso", "Backup realizado com sucesso em " + file.getName());
            } catch (IOException ex) {
                showAlert("Erro", "Falha ao realizar o backup.");
                ex.printStackTrace();
            }
        }
    }

    public void handleRestoreAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Restaurar Backup");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(table.getScene().getWindow());

        if (file != null) {
            try {
                controller.restoreData(file.getAbsolutePath());
                table.setItems(controller.getComputerList());
                table.refresh();
                showAlert("Sucesso", "Dados restaurados com sucesso de " + file.getName());
            } catch (IOException ex) {
                showAlert("Erro", "Falha ao restaurar os dados.");
                ex.printStackTrace();
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
