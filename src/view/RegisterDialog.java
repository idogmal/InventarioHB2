package view;

import controller.LoginController;
import javax.swing.*;
import java.awt.*;

public class RegisterDialog {
    public static void showDialog(Frame parent, LoginController loginController) {
        JDialog dialog = new JDialog(parent, "Cadastrar Usuário", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Nome de usuário:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(userLabel, gbc);

        JTextField userField = new JTextField(20);
        userField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(userField, gbc);

        JLabel passLabel = new JLabel("Senha:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField(20);
        passField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(passField, gbc);

        JButton registerButton = new JButton("Cadastrar");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        panel.add(registerButton, gbc);

        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);

        registerButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Preencha todos os campos.", "Erro", JOptionPane.ERROR_MESSAGE);
            } else if (loginController.registerUser(username, password)) {
                JOptionPane.showMessageDialog(dialog, "Usuário cadastrado com sucesso!", "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Nome de usuário já existe.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
