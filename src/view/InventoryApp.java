
package view;

import controller.InventoryController;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Computer;
import javafx.stage.FileChooser;
import model.User;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.io.IOException;

/**
 * InventoryApp - Classe principal para a aplicação de inventário de computadores.
 * Responsável por gerenciar a interface gráfica e as interações do usuário.
 */
public class InventoryApp extends Application {

    private TableView<Computer> table; // Tabela para exibir os computadores
    private ObservableList<Computer> computerList; // Lista de computadores
    private InventoryController controller; // Controlador principal

    @Override
    public void start(Stage primaryStage) {
        // Inicializar o controlador, caso não esteja definido
        if (controller == null) {
            initializeController();
        }

        // Configurar a tabela usando a classe auxiliar TableSetup
        TableSetup tableSetup = new TableSetup(controller);
        table = tableSetup.createTable(controller.getComputerList());

        // Adicionar contador de computadores
        Label computerCountLabel = new Label();
        computerCountLabel.setText("Total de Computadores: " + controller.getComputerCount());
        computerCountLabel.setStyle("-fx-font-weight: bold;"); // Adiciona negrito ao texto
        controller.getComputerList().addListener((javafx.collections.ListChangeListener<Computer>) change -> {
            computerCountLabel.setText("Total de Computadores: " + controller.getComputerCount());
        });

        // Barra de busca para filtrar a tabela
        TextField searchField = new TextField();
        searchField.setPromptText("Buscar por etiqueta, modelo, marca ou usuário");
        searchField.textProperty().addListener((observable, oldValue, newValue) ->
                table.setItems(controller.searchComputers(newValue))
        );

        // Botões de ação
        Button addButton = new Button("Cadastrar");
        Button editButton = new Button("Editar");
        Button deleteButton = new Button("Excluir");
        Button exportButton = new Button("Exportar para CSV");
        Button backupButton = new Button("Salvar");
        Button restoreButton = new Button("Restaurar");
        Button historyButton = new Button("Visualizar Histórico");
        Button manageUsersButton = new Button("Gerenciar Usuários");

        // Ações dos botões
        addButton.setOnAction(e -> openComputerForm(null));
        editButton.setOnAction(e -> handleEditAction());
        deleteButton.setOnAction(e -> handleDeleteAction());
        exportButton.setOnAction(e -> handleExportAction());
        backupButton.setOnAction(e -> handleBackupAction());
        restoreButton.setOnAction(e -> handleRestoreAction());
        historyButton.setOnAction(e -> openHistoryWindow());
        manageUsersButton.setOnAction(e -> {
            UserManagementApp userManagementApp = new UserManagementApp(controller);
            userManagementApp.showUserManagement(primaryStage);
        });

        // Layout dos botões
        HBox buttonLayout = new HBox(10, addButton, editButton, deleteButton, exportButton,
                backupButton, restoreButton, historyButton, manageUsersButton);
        buttonLayout.setPadding(new Insets(10));

        // Layout principal da aplicação
        VBox layout = new VBox(10, computerCountLabel, searchField, table, buttonLayout);
        layout.setPadding(new Insets(10));

        // Configuração da cena
        Scene scene = new Scene(layout, 900, 500);
        primaryStage.setTitle("Inventário de Computadores");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Inicializa o controlador principal.
     */
    private void initializeController() {
        controller = new InventoryController();
    }

    /**
     * Define o controlador externo na aplicação.
     *
     * @param controller Controlador a ser usado.
     */
    public void setController(InventoryController controller) {
        this.controller = controller;
    }

    /**
     * Define o usuário logado no sistema.
     *
     * @param user Nome do usuário logado.
     */
    public void setLoggedInUser(String user) {
        if (controller == null) {
            throw new IllegalStateException("O controlador não foi inicializado antes de definir o usuário logado.");
        }
        controller.setCurrentUser(user);
    }

    /**
     * Abre o formulário para cadastrar ou editar um computador.
     *
     * @param computer Computador a ser editado ou nulo para cadastrar um novo.
     */
    private void openComputerForm(Computer computer) {
        ComputerFormHandler formHandler = new ComputerFormHandler(controller);
        formHandler.openForm(computer, controller.getCurrentUser());
    }

    /**
     * Ação para editar um computador selecionado.
     */
    private void handleEditAction() {
        Computer selectedComputer = table.getSelectionModel().getSelectedItem();
        if (selectedComputer != null) {
            openComputerForm(selectedComputer);
        } else {
            showAlert("Seleção necessária", "Por favor, selecione um computador para editar.");
        }
    }

    /**
     * Ação para excluir um computador selecionado.
     */
    private void handleDeleteAction() {
        Computer selectedComputer = table.getSelectionModel().getSelectedItem();
        if (selectedComputer != null) {
            controller.deleteComputer(selectedComputer, controller.getCurrentUser());
        } else {
            showAlert("Seleção necessária", "Por favor, selecione um computador para excluir.");
        }
    }

    /**
     * Ação para exportar os dados da tabela para um arquivo CSV.
     */
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

    /**
     * Ação para realizar o backup dos dados.
     * */
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

    /**
     * Ação para restaurar dados a partir de um backup.
     */
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

    /**
     * Ação para abrir a janela de histórico.
     */
    private void openHistoryWindow() {
        HistoryWindow historyWindow = new HistoryWindow(controller);
        historyWindow.showHistory();
    }

    /**
     * Exibe um alerta para o usuário.
     *
     * @param title   Título do alerta.
     * @param message Mensagem do alerta.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
