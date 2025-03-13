package view;

import controller.InventoryController;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class UserManagementApp {

    private final InventoryController controller;

    public UserManagementApp(InventoryController controller) {
        this.controller = controller;
    }

    public void showUserManagement(JFrame parentFrame) {
        JFrame frame = new JFrame("Gerenciamento de Usuários");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 400);
        frame.setLocationRelativeTo(parentFrame);

        // Criação do modelo e JList para exibir os usuários
        DefaultListModel<String> listModel = new DefaultListModel<>();
        List<User> users = controller.getUsers();
        for (User user : users) {
            listModel.addElement(user.getUsername());
        }
        JList<String> userList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(userList);
        scrollPane.setPreferredSize(new Dimension(250, 150));

        // Botões para editar e excluir
        JButton editButton = new JButton("Editar Senha");
        JButton deleteButton = new JButton("Excluir Usuário");

        // Ação do botão "Editar Senha"
        editButton.addActionListener(e -> handleEditPassword(userList.getSelectedValue(), listModel));

        // Ação do botão "Excluir Usuário"
        deleteButton.addActionListener(e -> handleDeleteUser(userList.getSelectedValue(), listModel));

        // Layout usando BoxLayout para organizar os componentes verticalmente
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(scrollPane);
        panel.add(Box.createVerticalStrut(10));
        panel.add(editButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(deleteButton);

        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }

    private void handleEditPassword(String username, DefaultListModel<String> listModel) {
        if (username == null) {
            showAlert("Erro", "Selecione um usuário para editar.");
            return;
        }
        // Usa JOptionPane para solicitar a nova senha
        String newPassword = JOptionPane.showInputDialog(null,
                "Nova senha para " + username + ":", "Editar Senha", JOptionPane.PLAIN_MESSAGE);
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            try {
                controller.editUserPassword(username, newPassword);
                showAlert("Sucesso", "Senha editada com sucesso.");
            } catch (Exception e) {
                showAlert("Erro", e.getMessage());
            }
        }
    }

    private void handleDeleteUser(String username, DefaultListModel<String> listModel) {
        if (username == null) {
            showAlert("Erro", "Selecione um usuário para excluir.");
            return;
        }
        if ("admin".equals(username)) {
            showAlert("Erro", "Não é possível excluir o usuário administrador.");
            return;
        }
        controller.deleteUser(username);
        // Atualiza o modelo da lista
        listModel.clear();
        for (User user : controller.getUsers()) {
            listModel.addElement(user.getUsername());
        }
        showAlert("Sucesso", "Usuário excluído com sucesso.");
    }

    private void showAlert(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
