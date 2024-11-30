package view;

import controller.InventoryController;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.HistoryEntry;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HistoryWindow {

    private final InventoryController controller;

    public HistoryWindow(InventoryController controller) {
        this.controller = controller;
    }

    public void showHistory() {
        Stage historyStage = new Stage();
        historyStage.setTitle("Histórico de Alterações");

        TableView<HistoryEntry> historyTable = new TableView<>();
        historyTable.setEditable(false);

        // Coluna: Ação
        TableColumn<HistoryEntry, String> actionColumn = new TableColumn<>("Ação");
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));

        // Coluna: Usuário
        TableColumn<HistoryEntry, String> userColumn = new TableColumn<>("Usuário");
        userColumn.setCellValueFactory(new PropertyValueFactory<>("user"));

        // Coluna: Data e Hora
        TableColumn<HistoryEntry, String> timestampColumn = new TableColumn<>("Data e Hora");
        timestampColumn.setCellValueFactory(cellData -> {
            LocalDateTime timestamp = cellData.getValue().getTimestamp();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            return new javafx.beans.property.SimpleStringProperty(timestamp.format(formatter));
        });

        // Coluna: Descrição
        TableColumn<HistoryEntry, String> descriptionColumn = new TableColumn<>("Descrição");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Adicionar colunas à tabela
        historyTable.getColumns().addAll(actionColumn, userColumn, timestampColumn, descriptionColumn);

        // Preencher a tabela com os dados do histórico
        ObservableList<HistoryEntry> historyList = controller.getHistoryList();
        historyTable.setItems(historyList);

        // Configurar o layout
        javafx.scene.layout.VBox layout = new javafx.scene.layout.VBox(10, historyTable);
        layout.setPadding(new Insets(20));

        // Configurar e mostrar a cena
        Scene scene = new Scene(layout, 600, 400);
        historyStage.setScene(scene);
        historyStage.show();
    }
}
