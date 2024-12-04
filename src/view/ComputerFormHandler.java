package view;

import controller.InventoryController;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Computer;

public class ComputerFormHandler {

    private final InventoryController controller;

    public ComputerFormHandler(InventoryController controller) {
        this.controller = controller;
    }

    public void openForm(Computer computer, String currentUser) {
        Stage formStage = new Stage();
        formStage.setTitle(computer == null ? "Cadastrar Computador" : "Editar Computador");

        // Campos do formulário
        TextField tagField = new TextField();
        TextField modelField = new TextField();
        TextField brandField = new TextField();
        TextField stateField = new TextField();
        TextField userField = new TextField();
        TextField serialField = new TextField();
        TextField windowsField = new TextField();
        TextField officeField = new TextField();
        TextField locationField = new TextField();
        TextField purchaseField = new TextField();

        // Preencher os campos se for edição
        if (computer != null) {
            populateFields(computer, tagField, modelField, brandField, stateField, userField, serialField, windowsField, officeField, locationField, purchaseField);
        } else if (currentUser != null) {
            userField.setText(currentUser); // Preencher automaticamente o usuário logado
        }

        // GridPane para o layout do formulário
        GridPane gridPane = createFormGrid(tagField, modelField, brandField, stateField, userField, serialField, windowsField, officeField, locationField, purchaseField);

        // Botão de salvar
        Button saveButton = new Button("Salvar");
        saveButton.setOnAction(e -> {
            // Validação dos campos obrigatórios
            if (tagField.getText().trim().isEmpty() || userField.getText().trim().isEmpty()) {
                showAlert("Erro", "Os campos 'Etiqueta TI' e 'Usuário' não podem estar vazios.");
                return;
            }

            if (computer == null) {
                // Adicionar novo computador
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
                controller.addComputer(newComputer, currentUser);
                System.out.println("Novo computador cadastrado: " + newComputer); // Depuração
            } else {
                // Atualizar computador existente
                Computer updatedComputer = new Computer(
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
                controller.editComputer(computer, updatedComputer, currentUser);
                System.out.println("Computador atualizado: " + updatedComputer); // Depuração
            }
            formStage.close();
        });

        // Adicionar botão ao layout
        gridPane.add(saveButton, 1, 10);

        // Configurar e mostrar a cena
        Scene scene = new Scene(gridPane, 400, 400);
        formStage.setScene(scene);
        formStage.show();
    }

    private void populateFields(Computer computer, TextField... fields) {
        fields[0].setText(computer.getTag());
        fields[1].setText(computer.getModel());
        fields[2].setText(computer.getBrand());
        fields[3].setText(computer.getState());
        fields[4].setText(computer.getUserName());
        fields[5].setText(computer.getSerialNumber());
        fields[6].setText(computer.getWindowsVersion());
        fields[7].setText(computer.getOfficeVersion());
        fields[8].setText(computer.getLocation());
        fields[9].setText(computer.getPurchaseDate());
    }

    private GridPane createFormGrid(TextField... fields) {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        String[] labels = {"Etiqueta TI:", "Modelo:", "Marca:", "Estado:", "Usuário:", "Número de Série:", "Versão do Windows:", "Versão do Office:", "Localização:", "Data de Compra:"};

        for (int i = 0; i < labels.length; i++) {
            gridPane.add(new Label(labels[i]), 0, i);
            gridPane.add(fields[i], 1, i);
        }

        return gridPane;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
