package view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Computer;
import model.DatabaseHelper;

public class InventoryApp extends Application {

    private DatabaseHelper dbHelper = new DatabaseHelper();

    @Override
    public void start(Stage primaryStage) {
        dbHelper.createTable();

        TextField tagField = new TextField();
        tagField.setPromptText("Etiqueta TI");

        TextField serialNumberField = new TextField();
        serialNumberField.setPromptText("Número de Série");

        TextField modelField = new TextField();
        modelField.setPromptText("Modelo");

        TextField brandField = new TextField();
        brandField.setPromptText("Marca");

        Button saveButton = new Button("Salvar Computador");
        saveButton.setOnAction(e -> {
            Computer computer = new Computer(
                    tagField.getText(),
                    serialNumberField.getText(),
                    modelField.getText(),
                    brandField.getText(),
                    "Novo",
                    "Usuário",
                    "Windows 10",
                    "Office 2019",
                    "Empresa",
                    "2023-01-01"
            );
            dbHelper.insertComputer(computer);
        });

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        gridPane.add(new Label("Etiqueta TI:"), 0, 0);
        gridPane.add(tagField, 1, 0);
        gridPane.add(new Label("Número de Série:"), 0, 1);
        gridPane.add(serialNumberField, 1, 1);
        gridPane.add(new Label("Modelo:"), 0, 2);
        gridPane.add(modelField, 1, 2);
        gridPane.add(new Label("Marca:"), 0, 3);
        gridPane.add(brandField, 1, 3);

        gridPane.add(saveButton, 1, 4);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().add(gridPane);

        Scene scene = new Scene(layout, 600, 400);
        primaryStage.setTitle("Cadastro de Computadores");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
