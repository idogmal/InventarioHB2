package view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Campo de usuário
        TextField userNameField = new TextField();
        userNameField.setPromptText("Nome de usuário");

        // Campo de senha
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Senha");

        // Botão de login
        Button loginButton = new Button("Login");

        // Layout do formulário de login
        VBox layout = new VBox(10, userNameField, passwordField, loginButton);
        layout.setSpacing(10);

        // Definir a cena e a janela
        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
