package view;

import controller.InventoryController;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;

public class UserDetailsWindow {

    private final InventoryController controller;

    public UserDetailsWindow(InventoryController controller) {
        this.controller = controller;
    }

    public void show() {
        if (!controller.isAdmin(controller.getCurrentUser(), "admin")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Apenas o administrador pode acessar esta funcionalidade.");
            alert.showAndWait();
            return;
        }

        Stage stage = new Stage();
        stage.setTitle("Detalhes dos Usuários");

        Label userCountLabel = new Label("Total de Usuários: " + controller.getUserCount());
        ListView<String> userListView = new ListView<>();
        userListView.getItems().addAll(controller.getUsernames());

        VBox layout = new VBox(10, userCountLabel, userListView);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 300, 400);
        stage.setScene(scene);
        stage.show();
    }
}
