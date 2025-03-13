package controller;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import model.DatabaseHelper;
import model.User;
import view.InventoryApp;

public class LoginController {

    private final DatabaseHelper dbHelper = new DatabaseHelper();
    private String loggedInUser; // Armazena o usuário logado

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
    public java.util.List<User> getUsers() {
        // Supondo que o método dbHelper.getUsers() retorne um List<User>
        return dbHelper.getUsers();
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

    // Método para abrir a tela do inventário usando Swing
    public void openInventoryScreen() {
        if (loggedInUser == null) {
            throw new IllegalStateException("Nenhum usuário logado. Faça login antes de abrir a tela do inventário.");
        }

        try {
            InventoryApp inventoryApp = new InventoryApp();
            InventoryController inventoryController = new InventoryController();
            inventoryApp.setController(inventoryController);
            inventoryController.setCurrentUser(loggedInUser);
            // Cria um JFrame para o inventário
            JFrame frame = new JFrame("Inventário de Computadores");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            inventoryApp.start(frame);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Adicionar suporte à tecla Enter no login utilizando Swing
    public void enableEnterKeyLogin(JTextField userField, JPasswordField passwordField, JButton loginButton) {
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginButton.doClick();
                }
            }
        });

        userField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocusInWindow();
                }
            }
        });
    }

    // Método para obter o nome do usuário logado
    public String getLoggedInUser() {
        return loggedInUser;
    }
}
