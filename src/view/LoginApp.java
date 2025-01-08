package view;

import controller.InventoryController;
import controller.LoginController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginApp extends Application {

    private final LoginController loginController = new LoginController(); // Instância do controlador

    @Override
    public void start(Stage primaryStage) {
        // Campo de usuário
        TextField userNameField = new TextField();
        userNameField.setPromptText("Nome de usuário");
        userNameField.setMaxWidth(200); // Define largura máxima

        // Campo de senha
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Senha");
        passwordField.setMaxWidth(200); // Define largura máxima

        // Botão de login
        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(100); // Largura preferencial para o botão

        // Botão de cadastro
        Button registerButton = new Button("Cadastrar");
        registerButton.setPrefWidth(100); // Largura preferencial para o botão

        // Botão de gerenciamento de usuários (apenas para admin)
        Button manageUsersButton = new Button("Gerenciar Usuários");
        manageUsersButton.setPrefWidth(150);
        manageUsersButton.setVisible(false); // Visível apenas após login como admin

        // Ação ao clicar no botão de login
        loginButton.setOnAction(e -> {
            String userName = userNameField.getText();
            String password = passwordField.getText();

            if (loginController.login(userName, password)) {
                if (loginController.isAdmin(userName, password)) {
                    manageUsersButton.setVisible(true);
                    System.out.println("Usuário admin logado. Exibindo botão de gerenciar usuários.");
                } else {
                    manageUsersButton.setVisible(false);
                    System.out.println("Usuário padrão logado. Botão de gerenciar usuários oculto.");
                }
                showLocationSelection(primaryStage, userName); // Atualizado para incluir o currentUser
            } else {
                showAlert("Falha no Login", "Nome de usuário ou senha incorretos.");
            }
        });


        // Ação ao clicar no botão de cadastro
        registerButton.setOnAction(e -> openRegisterWindow());

        // Ação ao clicar no botão de gerenciamento de usuários
        manageUsersButton.setOnAction(e -> openUserManagementWindow());

        // Layout do formulário de login
        VBox layout = new VBox(10, userNameField, passwordField, loginButton, registerButton, manageUsersButton);
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(10);
        layout.setStyle("-fx-padding: 20;");

        // Configurar a cena e a janela
        Scene scene = new Scene(layout, 300, 300);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openRegisterWindow() {
        Stage registerStage = new Stage();
        registerStage.setTitle("Cadastrar Usuário");

        TextField userNameField = new TextField();
        userNameField.setPromptText("Nome de usuário");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Senha");

        Button registerButton = new Button("Cadastrar");

        registerButton.setOnAction(e -> {
            String userName = userNameField.getText();
            String password = passwordField.getText();

            if (userName.isEmpty() || password.isEmpty()) {
                showAlert("Erro", "Preencha todos os campos.");
            } else if (loginController.registerUser(userName, password)) {
                showAlert("Sucesso", "Usuário cadastrado com sucesso!");
                registerStage.close();
            } else {
                showAlert("Erro", "Nome de usuário já existe.");
            }
        });

        VBox layout = new VBox(10, userNameField, passwordField, registerButton);
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(10);
        layout.setStyle("-fx-padding: 20;");

        Scene scene = new Scene(layout, 300, 200);
        registerStage.setScene(scene);
        registerStage.show();
    }

    private void openUserManagementWindow() {
        Stage userManagementStage = new Stage();
        userManagementStage.setTitle("Gerenciar Usuários");

        ListView<String> userList = new ListView<>();
        userList.getItems().addAll(loginController.getUsers().stream().map(u -> u.getUsername()).toList());

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
                loginController.deleteUser(selectedUser);
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
                loginController.editUserPassword(username, newPassword);
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

    public void showLocationSelection(Stage stage, String currentUser) {
        System.out.println("showLocationSelection chamado com usuário: " + currentUser); // Log para depuração

        stage.setTitle("Selecione o Local");

        Button npdButton = new Button("NPD");
        Button infanButton = new Button("INFAN");

        npdButton.setOnAction(e -> {
            openInventoryApp("NPD", currentUser); // Passa o currentUser
            stage.close();
        });

        infanButton.setOnAction(e -> {
            openInventoryApp("INFAN", currentUser); // Passa o currentUser
            stage.close();
        });

        VBox layout = new VBox(10, npdButton, infanButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 300, 200);
        stage.setScene(scene);
        stage.show();
    }




    private void openInventoryApp(String location, String currentUser) {
        try {
            InventoryApp inventoryApp = new InventoryApp();
            InventoryController controller = new InventoryController();
            controller.setCurrentUser(currentUser); // Define o usuário logado
            inventoryApp.setController(controller);
            Stage stage = new Stage();
            inventoryApp.start(stage);
            inventoryApp.setLocationFilter(location); // Aplica o filtro de localidade
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
