package view;

import controller.InventoryController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.User;

public class UserManagementApp {

    private final InventoryController controller;

    public UserManagementApp(InventoryController controller) {
        this.controller = controller;
    }

    public void showUserManagement(Stage parentStage) {
        Stage stage = new Stage();
        stage.setTitle("Gerenciamento de Usuários");

        ObservableList<User> userList = FXCollections.observableArrayList(controller.getUsers());
        ListView<User> userListView = new ListView<>(userList);

        Button editButton = new Button("Editar Senha");
        Button deleteButton = new Button("Excluir Usuário");

        editButton.setOnAction(e -> handleEditPassword(userListView.getSelectionModel().getSelectedItem(), userList));
        deleteButton.setOnAction(e -> handleDeleteUser(userListView.getSelectionModel().getSelectedItem(), userList));

        VBox layout = new VBox(10, userListView, editButton, deleteButton);
        layout.setPadding(new javafx.geometry.Insets(10));

        stage.setScene(new Scene(layout, 300, 400));
        stage.initOwner(parentStage);
        stage.show();
    }

    private void handleEditPassword(User user, ObservableList<User> userList) {
        if (user == null) {
            showAlert("Erro", "Selecione um usuário para editar.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Editar Senha");
        dialog.setHeaderText("Editar senha do usuário: " + user.getUsername());
        dialog.setContentText("Nova senha:");

        dialog.showAndWait().ifPresent(newPassword -> {
            try {
                controller.editUserPassword(user.getUsername(), newPassword);
                showAlert("Sucesso", "Senha editada com sucesso.");
            } catch (Exception e) {
                showAlert("Erro", e.getMessage());
            }
        });
    }

    private void handleDeleteUser(User user, ObservableList<User> userList) {
        if (user == null) {
            showAlert("Erro", "Selecione um usuário para excluir.");
            return;
        }
        if ("admin".equals(user.getUsername())) {
            showAlert("Erro", "Não é possível excluir o usuário administrador.");
            return;
        }
        controller.deleteUser(user.getUsername());
        userList.setAll(controller.getUsers());
        showAlert("Sucesso", "Usuário excluído com sucesso.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
