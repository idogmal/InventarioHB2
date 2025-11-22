package controller;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import model.DatabaseHelper;
import model.User;

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

    /**
     * Exemplo de método para abrir a tela principal do sistema em Swing.
     * Ajuste conforme sua lógica de navegação (MainApp, LocalSelectionPanel etc.).
     */
    public void openInventoryScreen() {
        if (loggedInUser == null) {
            throw new IllegalStateException("Nenhum usuário logado. Faça login antes de abrir a tela do inventário.");
        }

        // Exemplo simples: apenas exibe uma mensagem
        JOptionPane.showMessageDialog(null,
                "Abrindo tela de inventário em Swing para o usuário: " + loggedInUser,
                "Inventário", JOptionPane.INFORMATION_MESSAGE);

        // Se tiver uma classe MainApp, você pode instanciá-la aqui:
        // MainApp mainApp = new MainApp();
        // mainApp.setVisible(true);
        // ou outra forma de mostrar o painel de inventário.
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
