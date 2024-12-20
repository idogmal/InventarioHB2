package view;

import controller.InventoryController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Computer;
import javafx.stage.FileChooser;
import model.User;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.io.IOException;

public class InventoryApp extends Application {

    private TableView<Computer> table;
    private ObservableList<Computer> computerList;
    private InventoryController controller;

    @Override
    public void start(Stage primaryStage) {
        if (controller == null) {
            initializeController(); // Garante a inicialização do controlador
        }

        // Configurar a tabela usando a classe auxiliar TableSetup
        TableSetup tableSetup = new TableSetup(controller);
        table = tableSetup.createTable(controller.getComputerList());

        // Adicionar barra de busca
        TextField searchField = new TextField();
        searchField.setPromptText("Buscar por etiqueta, modelo, marca ou usuário");

        // Atualizar tabela com base na busca
        searchField.textProperty().addListener((observable, oldValue, newValue) ->
                table.setItems(controller.searchComputers(newValue))
        );

        // Criar botões de ação
        Button addButton = new Button("Cadastrar");
        Button editButton = new Button("Editar");
        Button deleteButton = new Button("Excluir");
        Button exportButton = new Button("Exportar para CSV");
        Button backupButton = new Button("Salvar");
        Button restoreButton = new Button("Restaurar");
        Button historyButton = new Button("Visualizar Histórico");
        Button viewUsersButton = new Button("Visualizar Usuários"); // Novo botão

        // Adicionar ações aos botões
        addButton.setOnAction(e -> openComputerForm(null));
        editButton.setOnAction(e -> handleEditAction());
        deleteButton.setOnAction(e -> handleDeleteAction());
        exportButton.setOnAction(e -> handleExportAction());
        backupButton.setOnAction(e -> handleBackupAction());
        restoreButton.setOnAction(e -> handleRestoreAction());
        historyButton.setOnAction(e -> openHistoryWindow());
        viewUsersButton.setOnAction(e -> showUsersWindow()); // Ação do novo botão

        // Layout dos botões
        HBox buttonLayout = new HBox(10, addButton, editButton, deleteButton, exportButton,
                backupButton, restoreButton, historyButton, viewUsersButton);
        buttonLayout.setPadding(new Insets(10));

        // Layout principal com BorderPane
        BorderPane layout = new BorderPane();
        layout.setTop(searchField);
        layout.setCenter(table);
        layout.setBottom(buttonLayout);

        // Configurar a cena
        Scene scene = new Scene(layout, 900, 500);
        primaryStage.setTitle("Inventário de Computadores");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void initializeController() {
        controller = new InventoryController(); // Usa o construtor sem argumentos
    }

    // Novo método para definir o controlador de fora da classe
    public void setController(InventoryController controller) {
        this.controller = controller;
    }

    public void setLoggedInUser(String user) {
        if (controller == null) {
            throw new IllegalStateException("O controlador não foi inicializado antes de definir o usuário logado.");
        }
        controller.setCurrentUser(user);
    }

    // Método para abrir o formulário de computador (usando ComputerFormHandler)
    private void openComputerForm(Computer computer) {
        ComputerFormHandler formHandler = new ComputerFormHandler(controller);
        formHandler.openForm(computer, controller.getCurrentUser());
    }

    // Ação para editar um computador
    private void handleEditAction() {
        Computer selectedComputer = table.getSelectionModel().getSelectedItem();
        if (selectedComputer != null) {
            openComputerForm(selectedComputer);
        } else {
            showAlert("Seleção necessária", "Por favor, selecione um computador para editar.");
        }
    }

    // Ação para excluir um computador
    private void handleDeleteAction() {
        Computer selectedComputer = table.getSelectionModel().getSelectedItem();
        if (selectedComputer != null) {
            controller.deleteComputer(selectedComputer, controller.getCurrentUser());
        } else {
            showAlert("Seleção necessária", "Por favor, selecione um computador para excluir.");
        }
    }

    // Ação para exportar a tabela para CSV
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

    // Ação para realizar backup
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

    // Ação para restaurar dados de um backup
    private void handleRestoreAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Restaurar Backup");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(table.getScene().getWindow());

        if (file != null) {
            try {
                controller.restoreData(file.getAbsolutePath());
                table.setItems(controller.getComputerList()); // Atualiza a tabela
                table.refresh();
                showAlert("Sucesso", "Dados restaurados com sucesso de " + file.getName());
            } catch (IOException ex) {
                showAlert("Erro", "Falha ao restaurar os dados.");
                ex.printStackTrace();
            }
        }
    }

    // Ação para abrir o histórico
    private void openHistoryWindow() {
        HistoryWindow historyWindow = new HistoryWindow(controller);
        historyWindow.showHistory();
    }

    // Método para exibir alertas
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showUsersWindow() {
        Stage userStage = new Stage();
        userStage.setTitle("Usuários Cadastrados");

        // Configurar a tabela de usuários
        TableView<User> userTable = new TableView<>();
        userTable.setItems(controller.getUsers());

        TableColumn<User, String> usernameColumn = new TableColumn<>("Usuário");
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<User, String> passwordColumn = new TableColumn<>("Senha");
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));

        userTable.getColumns().addAll(usernameColumn, passwordColumn);

        // Layout
        BorderPane layout = new BorderPane();
        layout.setCenter(userTable);
        layout.setPadding(new Insets(10));

        // Configurar a cena e mostrar
        Scene scene = new Scene(layout, 400, 300);
        userStage.setScene(scene);
        userStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
