package view;

import controller.LoginController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginPanel extends JPanel {
    private MainApp mainApp;
    private LoginController loginController;

    public LoginPanel(MainApp mainApp, LoginController loginController) {
        this.mainApp = mainApp;
        this.loginController = loginController;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        // Cabeçalho
        JLabel welcomeLabel = new JLabel("Bem-vindo ao Sistema de Inventário", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(welcomeLabel, BorderLayout.NORTH);

        // Painel central com GridBagLayout para os campos
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nome de usuário
        JLabel userLabel = new JLabel("Nome de usuário:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        centerPanel.add(userLabel, gbc);

        JTextField userField = new JTextField(20);
        userField.setPreferredSize(new Dimension(250,30));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        centerPanel.add(userField, gbc);

        // Senha
        JLabel passLabel = new JLabel("Senha:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        centerPanel.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField(20);
        passField.setPreferredSize(new Dimension(250,30));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        centerPanel.add(passField, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // Painel inferior com botões
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,20,10));
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Cadastrar");
        bottomPanel.add(loginButton);
        bottomPanel.add(registerButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Ação do botão Login
        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            if(loginController.login(username, password)){
                JOptionPane.showMessageDialog(this, "Login bem-sucedido!");
                mainApp.showLocalSelectionPanel(username);
            } else {
                JOptionPane.showMessageDialog(this, "Nome de usuário ou senha incorretos.",
                        "Falha no Login", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Suporte à tecla Enter
        passField.addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e){
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    loginButton.doClick();
                }
            }
        });

        userField.addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e){
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    passField.requestFocusInWindow();
                }
            }
        });

        // Ação do botão Cadastrar (abre um diálogo de registro ou outro painel)
        registerButton.addActionListener(e -> {
            // Exemplo: abre uma nova janela de registro (pode ser integrado a um painel também)
            RegisterDialog.showDialog(mainApp, loginController);
        });
    }
}
