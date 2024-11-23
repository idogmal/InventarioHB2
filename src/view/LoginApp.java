package view;

import controller.LoginController;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginApp extends Application {

    private LoginController loginController = new LoginController(); // Instância do controlador

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

        // Ação ao clicar no botão de login
        loginButton.setOnAction(e -> {
            String userName = userNameField.getText();
            String password = passwordField.getText();

            // Usar o LoginController para verificar login
            if (loginController.login(userName, password)) {
                loginController.openInventoryScreen();
                primaryStage.close(); // Fechar a janela de login
            } else {
                showAlert("Falha no Login", "Nome de usuário ou senha incorretos.");
            }
        });

        // Layout do formulário de login
        VBox layout = new VBox(10, userNameField, passwordField, loginButton);
        layout.setAlignment(Pos.CENTER); // Centraliza os elementos
        layout.setSpacing(10);           // Espaçamento entre os elementos
        layout.setStyle("-fx-padding: 20;"); // Adiciona um padding externo

        // Definir a cena e a janela
        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Função para exibir alertas
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
