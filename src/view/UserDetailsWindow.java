package view;

import controller.InventoryController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UserDetailsWindow {

    private final InventoryController controller;

    public UserDetailsWindow(InventoryController controller) {
        this.controller = controller;
    }

    public void show() {
        // Verifica se o usuário atual é admin
        if (!controller.isAdmin(controller.getCurrentUser(), "admin")) {
            JOptionPane.showMessageDialog(null, "Apenas o administrador pode acessar esta funcionalidade.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Cria a janela principal
        JFrame frame = new JFrame("Detalhes dos Usuários");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 400);
        frame.setLocationRelativeTo(null);

        // Cria o label com o total de usuários
        JLabel userCountLabel = new JLabel("Total de Usuários: " + controller.getUserCount());

        // Cria o JList para exibir os nomes dos usuários
        // Supondo que controller.getUsernames() retorne uma List<String>
        DefaultListModel<String> listModel = new DefaultListModel<>();
        controller.getUsernames().forEach(listModel::addElement);
        JList<String> userList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(userList);

        // Organiza os componentes verticalmente usando BoxLayout
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(userCountLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(scrollPane);

        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }
}
