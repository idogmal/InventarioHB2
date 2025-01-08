package controller;

import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import model.DatabaseHelper;
import model.User;
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

    // Método para verificar se o usuário é administrador
    public boolean isAdmin(String userName, String password) {
        return "admin".equals(userName) && "admin".equals(password);
    }


    // Método para cadastrar novo usuário
    public boolean registerUser(String userName, String password) {
        if (dbHelper.isUserExists(userName)) {
            System.out.println("Usuário já existe.");
            return false; // Falha ao registrar
        }
        return dbHelper.insertUser(userName, password); // Tenta registrar
    }

    // Método para listar todos os usuários cadastrados
    public ObservableList<User> getUsers() {
        return dbHelper.getUsers(); // Usa o método do DatabaseHelper
    }

    // Método para excluir um usuário
    public void deleteUser(String userName) {
        if ("admin".equals(userName)) {
            throw new IllegalArgumentException("O administrador não pode ser excluído.");
        }
        if (dbHelper.deleteUser(userName)) {
            System.out.println("Usuário excluído: " + userName);
        } else {
            System.out.println("Erro ao excluir usuário: " + userName);
        }
    }

    // Método para editar a senha de um usuário
    public void editUserPassword(String userName, String newPassword) {
        if (dbHelper.editUserPassword(userName, newPassword)) {
            System.out.println("Senha alterada com sucesso para o usuário: " + userName);
        } else {
            System.out.println("Erro ao alterar senha para o usuário: " + userName);
        }
    }

    // Método para abrir a tela do inventário
    public void openInventoryScreen() {
        if (loggedInUser == null) {
            throw new IllegalStateException("Nenhum usuário logado. Faça login antes de abrir a tela do inventário.");
        }

        try {
            InventoryApp inventoryApp = new InventoryApp();

            // Configurar o controlador do inventário com uma lista vazia
            InventoryController inventoryController = new InventoryController();
            inventoryApp.setController(inventoryController);

            // Definir o usuário logado no inventário
            inventoryController.setCurrentUser(loggedInUser);

            // Abrir a tela do inventário
            inventoryApp.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Adicionar suporte à tecla Enter no login
    public void enableEnterKeyLogin(TextField userField, PasswordField passwordField, Button loginButton) {
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                loginButton.fire();
            }
        });

        userField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                passwordField.requestFocus();
            }
        });
    }

    // Método para obter o nome do usuário logado
    public String getLoggedInUser() {
        return loggedInUser;
    }
}
