package controller;

import javafx.collections.FXCollections;
import javafx.stage.Stage;
import model.DatabaseHelper;
import view.InventoryApp;

public class LoginController {

    private final DatabaseHelper dbHelper = new DatabaseHelper();
    private String loggedInUser; // Atributo para armazenar o usuário logado

    public LoginController() {
        // Garantir que a tabela de usuários está criada
        dbHelper.createTable();
    }

    // Método para validar login
    public boolean login(String userName, String password) {
        if (dbHelper.validateLogin(userName, password)) {
            loggedInUser = userName; // Armazena o nome do usuário logado
            return true;
        }
        return false;
    }

    // Método para cadastrar novo usuário
    public boolean registerUser(String userName, String password) {
        if (dbHelper.isUserExists(userName)) {
            System.out.println("Usuário já existe.");
            return false; // Falha ao registrar
        }
        return dbHelper.insertUser(userName, password); // Tenta registrar
    }

    // Método para abrir a tela do inventário
    public void openInventoryScreen() {
        if (loggedInUser == null) {
            throw new IllegalStateException("Nenhum usuário logado. Faça login antes de abrir a tela do inventário.");
        }

        try {
            InventoryApp inventoryApp = new InventoryApp();

            // Configurar o controlador do inventário com uma lista vazia
            inventoryApp.setController(new InventoryController(FXCollections.observableArrayList()));

            // Definir o usuário logado no inventário
            inventoryApp.setLoggedInUser(loggedInUser);

            // Abrir a tela do inventário
            inventoryApp.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para obter o nome do usuário logado
    public String getLoggedInUser() {
        return loggedInUser;
    }
}
