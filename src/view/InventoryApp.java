package view;

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
import javafx.stage.Stage;
import model.Computer;

public class InventoryApp extends Application {

    private TableView<Computer> table;
    private ObservableList<Computer> computerList;

    @Override
    public void start(Stage primaryStage) {
        // Configuração da tabela
        setupTable();

        // Botões de ação
        Button addButton = new Button("Cadastrar");
        Button editButton = new Button("Editar");
        Button deleteButton = new Button("Excluir");

        // Ações dos botões
        addButton.setOnAction(e -> openComputerForm(null));  // Para cadastrar novo
        editButton.setOnAction(e -> handleEditAction());     // Para editar o selecionado
        deleteButton.setOnAction(e -> handleDeleteAction()); // Para excluir o selecionado

        // Layout dos botões
        HBox buttonLayout = new HBox(10, addButton, editButton, deleteButton);

        // Layout principal
        VBox layout = new VBox(10, table, buttonLayout);
        layout.setPadding(new Insets(20));

        // Configurar a cena
        Scene scene = new Scene(layout, 900, 500);  // Aumentei o tamanho da janela para acomodar todas as colunas
        primaryStage.setTitle("Inventário de Computadores");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Configuração da tabela e suas colunas
    private void setupTable() {
        table = new TableView<>();
        computerList = FXCollections.observableArrayList();

        // Criar todas as colunas para cada campo
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

        // Adicionar todas as colunas na tabela
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
            computerList.remove(selectedComputer);
        } else {
            showAlert("Seleção necessária", "Por favor, selecione um computador para excluir.");
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
        saveButton.setOnAction(e -> saveComputer(computer, tagField, modelField, brandField, stateField, userField, serialField, windowsField, officeField, locationField, purchaseField, formStage));

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

    // Salva o computador na lista (novo ou atualizado)
    private void saveComputer(Computer computer, TextField tagField, TextField modelField, TextField brandField, TextField stateField, TextField userField, TextField serialField, TextField windowsField, TextField officeField, TextField locationField, TextField purchaseField, Stage stage) {
        if (computer == null) {
            // Criar novo computador
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
            computerList.add(newComputer);
        } else {
            // Atualizar computador existente
            computer.setTag(tagField.getText());
            computer.setModel(modelField.getText());
            computer.setBrand(brandField.getText());
            computer.setState(stateField.getText());
            computer.setUserName(userField.getText());
            computer.setSerialNumber(serialField.getText());
            computer.setWindowsVersion(windowsField.getText());
            computer.setOfficeVersion(officeField.getText());
            computer.setLocation(locationField.getText());
            computer.setPurchaseDate(purchaseField.getText());
            table.refresh();  // Atualiza a tabela com os novos dados
        }
        stage.close();
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
