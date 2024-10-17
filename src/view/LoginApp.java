package view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class LoginApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Criando os componentes da interface
        Label userLabel = new Label("Usuário:");
        TextField userField = new TextField();

        Label passwordLabel = new Label("Senha:");
        PasswordField passwordField = new PasswordField();

        Button loginButton = new Button("Login");

        // Layout da tela
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(userLabel, 0, 0);
        gridPane.add(userField, 1, 0);
        gridPane.add(passwordLabel, 0, 1);
        gridPane.add(passwordField, 1, 1);
        gridPane.add(loginButton, 1, 2);

        // Ação ao clicar no botão de login
        loginButton.setOnAction(e -> {
            String username = userField.getText();
            String password = passwordField.getText();

            // Autenticação simples (mais tarde podemos conectar com o banco de dados)
            if (username.equals("admin") && password.equals("1234")) {
                // Se o login for bem-sucedido, abrir a tela principal
                InventoryApp inventoryApp = new InventoryApp();
                try {
                    inventoryApp.start(primaryStage);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            } else {
                System.out.println("Usuário ou senha incorretos");
            }
        });

        // Definindo a cena e a janela
        Scene scene = new Scene(gridPane, 300, 200);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
