package view;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
        // Colunas da tabela
        TableColumn<Computer, String> tagColumn = new TableColumn<>("Etiqueta TI");
        tagColumn.setMinWidth(100);
        tagColumn.setCellValueFactory(new PropertyValueFactory<>("tag"));

        TableColumn<Computer, String> modelColumn = new TableColumn<>("Modelo");
        modelColumn.setMinWidth(100);
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));

        TableColumn<Computer, String> brandColumn = new TableColumn<>("Marca");
        brandColumn.setMinWidth(100);
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));

        // Criar a tabela
        table = new TableView<>();
        computerList = FXCollections.observableArrayList();
        table.setItems(computerList);
        table.getColumns().addAll(tagColumn, modelColumn, brandColumn);

        // Botões de ação
        Button addButton = new Button("Cadastrar");
        Button editButton = new Button("Editar");
        Button deleteButton = new Button("Excluir");

        // Ação do botão "Cadastrar"
        addButton.setOnAction(e -> openAddComputerForm());

        // Layout dos botões
        HBox buttonLayout = new HBox(10);
        buttonLayout.getChildren().addAll(addButton, editButton, deleteButton);

        // Layout principal
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(table, buttonLayout);

        // Definir a cena e a janela
        Scene scene = new Scene(layout, 600, 400);
        primaryStage.setTitle("Inventário de Computadores");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Função para abrir o formulário de cadastro
    private void openAddComputerForm() {
        Stage addStage = new Stage();
        addStage.setTitle("Cadastrar Computador");

        // Formulário de cadastro
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Campos do formulário
        Label tagLabel = new Label("Etiqueta TI:");
        TextField tagField = new TextField();
        Label modelLabel = new Label("Modelo:");
        TextField modelField = new TextField();
        Label brandLabel = new Label("Marca:");
        TextField brandField = new TextField();
        Label stateLabel = new Label("Estado:");
        TextField stateField = new TextField();
        Label userLabel = new Label("Usuário:");
        TextField userField = new TextField();
        Label serialLabel = new Label("Número de Série:");
        TextField serialField = new TextField();
        Label windowsLabel = new Label("Versão do Windows:");
        TextField windowsField = new TextField();
        Label officeLabel = new Label("Versão do Office:");
        TextField officeField = new TextField();
        Label locationLabel = new Label("Localização:");
        TextField locationField = new TextField();
        Label purchaseLabel = new Label("Data de Compra:");
        TextField purchaseField = new TextField();

        // Adicionar os campos ao formulário
        gridPane.add(tagLabel, 0, 0);
        gridPane.add(tagField, 1, 0);
        gridPane.add(modelLabel, 0, 1);
        gridPane.add(modelField, 1, 1);
        gridPane.add(brandLabel, 0, 2);
        gridPane.add(brandField, 1, 2);
        gridPane.add(stateLabel, 0, 3);
        gridPane.add(stateField, 1, 3);
        gridPane.add(userLabel, 0, 4);
        gridPane.add(userField, 1, 4);
        gridPane.add(serialLabel, 0, 5);
        gridPane.add(serialField, 1, 5);
        gridPane.add(windowsLabel, 0, 6);
        gridPane.add(windowsField, 1, 6);
        gridPane.add(officeLabel, 0, 7);
        gridPane.add(officeField, 1, 7);
        gridPane.add(locationLabel, 0, 8);
        gridPane.add(locationField, 1, 8);
        gridPane.add(purchaseLabel, 0, 9);
        gridPane.add(purchaseField, 1, 9);

        // Botão de salvar
        Button saveButton = new Button("Salvar");
        saveButton.setOnAction(e -> {
            // Criar um novo computador a partir dos campos do formulário
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
            computerList.add(newComputer);  // Adicionar à lista
            addStage.close();  // Fechar o formulário
        });

        // Adicionar o botão ao formulário
        gridPane.add(saveButton, 1, 10);

        // Definir a cena e a janela
        Scene scene = new Scene(gridPane, 400, 400);
        addStage.setScene(scene);
        addStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
