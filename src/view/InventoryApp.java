package view;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Computer;

public class InventoryApp extends Application {

    private TableView<Computer> table;
    private ObservableList<Computer> computerList;

    @Override
    public void start(Stage primaryStage) {
        // Criando as colunas da tabela
        TableColumn<Computer, String> tagColumn = new TableColumn<>("Etiqueta TI");
        tagColumn.setMinWidth(100);
        tagColumn.setCellValueFactory(new PropertyValueFactory<>("tag"));

        TableColumn<Computer, String> modelColumn = new TableColumn<>("Modelo");
        modelColumn.setMinWidth(100);
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));

        TableColumn<Computer, String> brandColumn = new TableColumn<>("Marca");
        brandColumn.setMinWidth(100);
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));

        // Criando a tabela
        table = new TableView<>();
        computerList = FXCollections.observableArrayList();
        table.setItems(computerList);
        table.getColumns().addAll(tagColumn, modelColumn, brandColumn);

        // Campo de busca
        TextField searchField = new TextField();
        searchField.setPromptText("Pesquisar...");
        searchField.setMinWidth(200);

        // Botões de ação
        Button addButton = new Button("Cadastrar");
        Button editButton = new Button("Editar");
        Button deleteButton = new Button("Excluir");

        // Layout dos botões
        HBox buttonLayout = new HBox(10);
        buttonLayout.getChildren().addAll(addButton, editButton, deleteButton);

        // Layout principal
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(searchField, table, buttonLayout);

        // Definindo a cena e a janela
        Scene scene = new Scene(layout, 600, 400);
        primaryStage.setTitle("Inventário de Computadores");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
