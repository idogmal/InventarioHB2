package view;

import controller.InventoryController;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Computer;
import model.User;
import javafx.stage.FileChooser;

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

        // Vincular o ObservableList à tabela
        table.setItems(controller.getComputerList());

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

        // Botão de voltar
        Button backButton = new Button("⬅ Voltar");
        backButton.setOnAction(e -> {
            String currentUser = controller.getCurrentUser();
            if (currentUser == null || currentUser.isEmpty()) {
                showAlert("Erro", "Usuário atual não está definido. Não é possível voltar para a seleção de localidade.");
                return;
            }
            primaryStage.close();
            reopenLocationSelection(currentUser);
        });





        // Verificar se o usuário logado é admin
        if ("admin".equals(controller.getCurrentUser())) {
            manageUsersButton.setVisible(true);
        } else {
            manageUsersButton.setVisible(false);
        }

        // Ação do botão "Gerenciar Usuários"
        manageUsersButton.setOnAction(e -> openUserManagementWindow());

        // Ações dos botões
        addButton.setOnAction(e -> openComputerForm(null));
        editButton.setOnAction(e -> handleEditAction());
        deleteButton.setOnAction(e -> handleDeleteAction());
        exportButton.setOnAction(e -> handleExportAction());
        backupButton.setOnAction(e -> handleBackupAction());
        restoreButton.setOnAction(e -> handleRestoreAction());
        historyButton.setOnAction(e -> openHistoryWindow());

        // Layout dos botões
        HBox buttonLayout = new HBox(10, addButton, editButton, deleteButton, exportButton,
                backupButton, restoreButton, historyButton, manageUsersButton);
        buttonLayout.setPadding(new Insets(10));

        // Layout principal com VBox
        VBox mainLayout = new VBox(10, backButton, computerCountLabel, searchField, table, buttonLayout);
        mainLayout.setPadding(new Insets(10));

        // Configuração da cena
        Scene scene = new Scene(mainLayout, 900, 500);
        primaryStage.setTitle("Inventário de Computadores");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void reopenLocationSelection(String currentUser) {
        if (currentUser == null || currentUser.isEmpty()) {
            System.out.println("Erro: Usuário atual é nulo ou vazio ao tentar reabrir a seleção de localidade.");
            return; // Impede que o método continue se o usuário estiver inválido
        }

        LoginApp loginApp = new LoginApp();
        try {
            Stage locationStage = new Stage();
            loginApp.showLocationSelection(locationStage, currentUser); // Passa o Stage e o usuário logado
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
     * Define o filtro de localidade na tabela.
     *
     * @param location Localidade para filtrar (NPD ou INFAN).
     */
    public void setLocationFilter(String location) {
        if (table == null) {
            throw new IllegalStateException("A tabela não foi inicializada. Certifique-se de que o método start foi chamado.");
        }
        ObservableList<Computer> filteredList = controller.getComputersByLocation(location);
        table.setItems(filteredList); // Atualiza a tabela com os dados filtrados
        table.refresh(); // Atualiza a exibição da tabela
    }

    /**
     * Abre o formulário para cadastrar ou editar um computador.
     *
     * @param computer Computador a ser editado ou nulo para cadastrar um novo.
     */
    private void openComputerForm(Computer computer) {
        String currentUser = controller.getCurrentUser(); // Obter o nome do usuário logado
        if (currentUser == null || currentUser.isEmpty()) {
            showAlert("Erro", "Nenhum usuário logado. Não é possível cadastrar computadores.");
            return;
        }
        ComputerFormHandler formHandler = new ComputerFormHandler(controller);
        formHandler.openForm(computer, currentUser);
        table.setItems(controller.getComputerList()); // Atualiza a tabela após cadastro
        table.refresh();
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
            table.setItems(controller.getComputerList()); // Atualiza a tabela após exclusão
            table.refresh();
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
     */
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
     * Ação para abrir a janela de gerenciamento de usuários.
     */
    private void openUserManagementWindow() {
        Stage userManagementStage = new Stage();
        userManagementStage.setTitle("Gerenciar Usuários");

        ListView<String> userList = new ListView<>();
        userList.getItems().addAll(controller.getUsers().stream().map(User::getUsername).toList());

        Button editPasswordButton = new Button("Editar Senha");
        Button deleteUserButton = new Button("Excluir Usuário");

        // Ação para editar a senha do usuário selecionado
        editPasswordButton.setOnAction(e -> {
            String selectedUser = userList.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                openEditPasswordWindow(selectedUser);
            } else {
                showAlert("Erro", "Selecione um usuário.");
            }
        });

        // Ação para excluir o usuário selecionado
        deleteUserButton.setOnAction(e -> {
            String selectedUser = userList.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                controller.deleteUser(selectedUser);
                userList.getItems().remove(selectedUser);
                showAlert("Sucesso", "Usuário excluído com sucesso.");
            } else {
                showAlert("Erro", "Selecione um usuário.");
            }
        });

        VBox layout = new VBox(10, userList, editPasswordButton, deleteUserButton);
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(10);
        layout.setStyle("-fx-padding: 20;");

        Scene scene = new Scene(layout, 300, 300);
        userManagementStage.setScene(scene);
        userManagementStage.show();
    }

    /**
     * Abre a janela para editar a senha de um usuário.
     */
    private void openEditPasswordWindow(String username) {
        Stage editPasswordStage = new Stage();
        editPasswordStage.setTitle("Editar Senha");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Nova Senha");

        Button saveButton = new Button("Salvar");

        saveButton.setOnAction(e -> {
            String newPassword = newPasswordField.getText();
            if (newPassword.isEmpty()) {
                showAlert("Erro", "A senha não pode estar vazia.");
            } else {
                controller.editUserPassword(username, newPassword);
                showAlert("Sucesso", "Senha atualizada com sucesso.");
                editPasswordStage.close();
            }
        });

        VBox layout = new VBox(10, newPasswordField, saveButton);
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(10);
        layout.setStyle("-fx-padding: 20;");

        Scene scene = new Scene(layout, 300, 150);
        editPasswordStage.setScene(scene);
        editPasswordStage.show();
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
