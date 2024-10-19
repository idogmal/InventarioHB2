// Código ajustado do InventoryApp.java

package view;

import controller.InventoryController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Computer;
import javafx.scene.control.cell.TextFieldTableCell;


import java.io.File;
import java.io.IOException;

public class InventoryApp extends Application {

    private TableView<Computer> table;
    private ObservableList<Computer> computerList;
    private InventoryController controller;

    @Override
    public void start(Stage primaryStage) {
        computerList = FXCollections.observableArrayList();
        controller = new InventoryController(computerList);

        // Configuração da tabela
        setupTable();

        // Adicionar barra de busca
        TextField searchField = new TextField();
        searchField.setPromptText("Buscar por etiqueta, modelo, marca ou usuário");

        // Lógica de atualização da tabela com base na busca
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            table.setItems(controller.searchComputers(newValue));
        });

        // Botões de ação
        Button addButton = new Button("Cadastrar");
        Button editButton = new Button("Editar");
        Button deleteButton = new Button("Excluir");
        Button exportButton = new Button("Exportar para CSV");
        Button backupButton = new Button("Backup");
        Button restoreButton = new Button("Restaurar");

        // Ações dos botões
        addButton.setOnAction(e -> openComputerForm(null));  // Para cadastrar novo
        editButton.setOnAction(e -> handleEditAction());     // Para editar o selecionado
        deleteButton.setOnAction(e -> handleDeleteAction()); // Para excluir o selecionado
        exportButton.setOnAction(e -> handleExportAction()); // Para exportar a lista para CSV
        backupButton.setOnAction(e -> handleBackupAction()); // Para fazer o backup
        restoreButton.setOnAction(e -> handleRestoreAction()); // Para restaurar o backup feito

        // Layout dos botões
        HBox buttonLayout = new HBox(10, addButton, editButton, deleteButton, exportButton, backupButton, restoreButton);

        // Layout principal com a barra de busca
        VBox layout = new VBox(10, searchField, table, buttonLayout);
        layout.setPadding(new Insets(20));

        // Configurar a cena
        Scene scene = new Scene(layout, 900, 500);
        primaryStage.setTitle("Inventário de Computadores");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setupTable() {
        table = new TableView<>();
        table.setEditable(true);

        // Criar todas as colunas
        TableColumn<Computer, String> tagColumn = createTableColumn("Etiqueta TI", "tag", 100);
        TableColumn<Computer, String> modelColumn = createTableColumn("Modelo", "model", 100);
        TableColumn<Computer, String> brandColumn = createTableColumn("Marca", "brand", 100);
        TableColumn<Computer, String> stateColumn = createTableColumn("Estado", "state", 100);
        TableColumn<Computer, String> userColumn = createTableColumn("Usuário", "userName", 100);
        TableColumn<Computer, String> serialColumn = createTableColumn("Número de Série", "serialNumber", 150);
        TableColumn<Computer, String> windowsColumn = createTableColumn("Versão do Windows", "windowsVersion", 150);
        TableColumn<Computer, String> officeColumn = createTableColumn("Versão do Office", "officeVersion", 150);
        TableColumn<Computer, String> locationColumn = createTableColumn("Localização", "location", 150);
        TableColumn<Computer, String> purchaseColumn = createTableColumn("Data de Compra", "purchaseDate", 120);

        // Configurar colunas para serem editáveis
        tagColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        tagColumn.setOnEditCommit(event -> event.getRowValue().setTag(event.getNewValue()));

        modelColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        modelColumn.setOnEditCommit(event -> event.getRowValue().setModel(event.getNewValue()));

        brandColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        brandColumn.setOnEditCommit(event -> event.getRowValue().setBrand(event.getNewValue()));

        stateColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        stateColumn.setOnEditCommit(event -> event.getRowValue().setState(event.getNewValue()));

        userColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        userColumn.setOnEditCommit(event -> event.getRowValue().setUserName(event.getNewValue()));

        serialColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        serialColumn.setOnEditCommit(event -> event.getRowValue().setSerialNumber(event.getNewValue()));

        windowsColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        windowsColumn.setOnEditCommit(event -> event.getRowValue().setWindowsVersion(event.getNewValue()));

        officeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        officeColumn.setOnEditCommit(event -> event.getRowValue().setOfficeVersion(event.getNewValue()));

        locationColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        locationColumn.setOnEditCommit(event -> event.getRowValue().setLocation(event.getNewValue()));

        purchaseColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        purchaseColumn.setOnEditCommit(event -> event.getRowValue().setPurchaseDate(event.getNewValue()));

        // Adicionar colunas à tabela
        table.getColumns().addAll(tagColumn, modelColumn, brandColumn, stateColumn, userColumn, serialColumn, windowsColumn, officeColumn, locationColumn, purchaseColumn);
        table.setItems(computerList);
    }





// Método genérico para criar colunas de tabela
    private TableColumn<Computer, String> createTableColumn(String title, String property, int width) {
        TableColumn<Computer, String> column = new TableColumn<>(title);
        column.setMinWidth(width);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        return column;
    }

    // Ação para exportar a lista para CSV
    private void handleExportAction() {
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

    // Ação do botão "Editar"
    private void handleEditAction() {
        Computer selectedComputer = table.getSelectionModel().getSelectedItem();
        if (selectedComputer != null) {
            openComputerForm(selectedComputer);
        } else {
            showAlert("Seleção necessária", "Por favor, selecione um computador para editar.");
        }
    }

    // Ação do botão "Excluir"
    private void handleDeleteAction() {
        Computer selectedComputer = table.getSelectionModel().getSelectedItem();
        if (selectedComputer != null) {
            controller.deleteComputer(selectedComputer);
        } else {
            showAlert("Seleção necessária", "Por favor, selecione um computador para excluir.");
        }
    }

    // Função de Backup
    private void handleBackupAction() {
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

    // Função de Restauração
    private void handleRestoreAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Restaurar Backup");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(table.getScene().getWindow());

        if (file != null) {
            try {
                controller.restoreData(file.getAbsolutePath());
                table.refresh();
                showAlert("Sucesso", "Dados restaurados com sucesso de " + file.getName());
            } catch (IOException ex) {
                showAlert("Erro", "Falha ao restaurar os dados.");
                ex.printStackTrace();
            }
        }
    }


    // Abre o formulário de cadastro/edição
    private void openComputerForm(Computer computer) {
        Stage formStage = new Stage();
        formStage.setTitle(computer == null ? "Cadastrar Computador" : "Editar Computador");

        // Campos do formulário
        TextField tagField = new TextField();
        TextField modelField = new TextField();
        TextField brandField = new TextField();
        TextField stateField = new TextField();
        TextField userField = new TextField();
        TextField serialField = new TextField();
        TextField windowsField = new TextField();
        TextField officeField = new TextField();
        TextField locationField = new TextField();
        TextField purchaseField = new TextField();

        // Preencher os campos se for edição
        if (computer != null) {
            populateFields(computer, tagField, modelField, brandField, stateField, userField, serialField, windowsField, officeField, locationField, purchaseField);
        }

        // GridPane para o layout do formulário
        GridPane gridPane = createFormGrid(tagField, modelField, brandField, stateField, userField, serialField, windowsField, officeField, locationField, purchaseField);

        // Botão de salvar
        Button saveButton = new Button("Salvar");
        saveButton.setOnAction(e -> {
            if (computer == null) {
                // Adicionar novo computador
                Computer newComputer = new Computer(
                        tagField.getText(),
                        modelField.getText(),
                        brandField.getText(),
                        stateField.getText(),
                        userField.getText(),
                        serialField.getText(),
                        windowsField.getText(),
                        officeField.getText(),
                        locationField.getText(),
                        purchaseField.getText()
                );
                controller.addComputer(newComputer);
            } else {
                // Atualizar computador existente
                Computer updatedComputer = new Computer(
                        tagField.getText(),
                        modelField.getText(),
                        brandField.getText(),
                        stateField.getText(),
                        userField.getText(),
                        serialField.getText(),
                        windowsField.getText(),
                        officeField.getText(),
                        locationField.getText(),
                        purchaseField.getText()
                );
                controller.editComputer(computer, updatedComputer);
            }
            formStage.close();
        });

        // Adicionar botão ao layout
        gridPane.add(saveButton, 1, 10);

        // Configurar e mostrar a cena
        Scene scene = new Scene(gridPane, 400, 400);
        formStage.setScene(scene);
        formStage.show();
    }

    // Preenche os campos no formulário durante a edição
    private void populateFields(Computer computer, TextField... fields) {
        fields[0].setText(computer.getTag());
        fields[1].setText(computer.getModel());
        fields[2].setText(computer.getBrand());
        fields[3].setText(computer.getState());
        fields[4].setText(computer.getUserName());
        fields[5].setText(computer.getSerialNumber());
        fields[6].setText(computer.getWindowsVersion());
        fields[7].setText(computer.getOfficeVersion());
        fields[8].setText(computer.getLocation());
        fields[9].setText(computer.getPurchaseDate());
    }

    // Cria o layout do formulário de cadastro/edição
    private GridPane createFormGrid(TextField... fields) {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        String[] labels = {"Etiqueta TI:", "Modelo:", "Marca:", "Estado:", "Usuário:", "Número de Série:", "Versão do Windows:", "Versão do Office:", "Localização:", "Data de Compra:"};

        for (int i = 0; i < labels.length; i++) {
            gridPane.add(new Label(labels[i]), 0, i);
            gridPane.add(fields[i], 1, i);
        }

        return gridPane;
    }

    // Exibe alertas
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
