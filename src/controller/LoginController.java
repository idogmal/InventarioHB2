package controller;

import javafx.stage.Stage;
import view.InventoryApp;

public class LoginController {

    // Método para verificar as credenciais de login
    public boolean login(String userName, String password) {
        // Verificar se o nome de usuário e senha estão corretos
        return userName.equals("admin") && password.equals("1234");
    }

    // Método para abrir a tela do inventário
    public void openInventoryScreen() {
        Stage inventoryStage = new Stage();
        inventoryStage.setTitle("Inventário de Computadores");

        InventoryApp inventoryApp = new InventoryApp();
        try {
            inventoryApp.start(inventoryStage); // Abre a tela de inventário
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
