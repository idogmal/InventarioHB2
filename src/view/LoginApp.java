package view;

import controller.LoginController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginApp {

    private final LoginController loginController = new LoginController();

    public LoginApp() {
        setLookAndFeel();
        createAndShowGUI();
    }

    // Define o Nimbus LookAndFeel, se disponível
    private void setLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Nimbus LookAndFeel não disponível, usando o padrão.");
        }
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Painel principal com BorderLayout e margens
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Painel superior: mensagem de boas-vindas
        JLabel welcomeLabel = new JLabel("Bem-vindo ao Sistema de Inventário", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Painel central: campos de login
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Label e campo "Nome de usuário"
        JLabel userLabel = new JLabel("Nome de usuário:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0; // label não expande
        centerPanel.add(userLabel, gbc);

        JTextField userNameField = new JTextField(20);
        // Ajustar peso para expandir o campo de texto
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        centerPanel.add(userNameField, gbc);

        // Label e campo "Senha"
        JLabel passLabel = new JLabel("Senha:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        centerPanel.add(passLabel, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        centerPanel.add(passwordField, gbc);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Painel inferior: botões
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Cadastrar");
        bottomPanel.add(loginButton);
        bottomPanel.add(registerButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        frame.getContentPane().add(mainPanel);

        // Deixe o layout calcular o tamanho ideal
        frame.pack();
        // Define um tamanho mínimo, caso queira garantir que não fique muito pequeno
        frame.setMinimumSize(new Dimension(400, 300));

        // Centraliza na tela
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Ação do botão Login
        loginButton.addActionListener(e -> {
            String username = userNameField.getText();
            String password = new String(passwordField.getPassword());
            if (loginController.login(username, password)) {
                JOptionPane.showMessageDialog(frame, "Login bem-sucedido!");
                // Exibe a seleção de local
                showLocationSelection(frame, username);
            } else {
                JOptionPane.showMessageDialog(frame, "Nome de usuário ou senha incorretos.",
                        "Falha no Login", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Suporte à tecla Enter
        passwordField.addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e){
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginButton.doClick();
                }
            }
        });

        userNameField.addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e){
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocusInWindow();
                }
            }
        });

        // Ação do botão Cadastrar
        registerButton.addActionListener(e -> openRegisterWindow());
    }

    // Janela modal para seleção de local
    public void showLocationSelection(JFrame parentFrame, String currentUser) {
        JDialog dialog = new JDialog(parentFrame, "Selecione o Local", true);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(parentFrame);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton npdButton = new JButton("NPD");
        JButton infanButton = new JButton("INFAN");
        npdButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        infanButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(npdButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(infanButton);

        dialog.getContentPane().add(panel);

        npdButton.addActionListener(e -> {
            openInventoryApp("NPD", currentUser);
            dialog.dispose();
        });

        infanButton.addActionListener(e -> {
            openInventoryApp("INFAN", currentUser);
            dialog.dispose();
        });

        dialog.setVisible(true);
    }

    // Abre o inventário (aqui, apenas um placeholder)
    private void openInventoryApp(String location, String currentUser) {
        JOptionPane.showMessageDialog(null, "Abrindo inventário para " + location + " como " + currentUser);
    }

    // Janela de cadastro de usuário aprimorada
    private void openRegisterWindow() {
        JFrame registerFrame = new JFrame("Cadastrar Usuário");
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

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
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(userField, gbc);

        JLabel passLabel = new JLabel("Senha:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(passField, gbc);

        JButton registerButton = new JButton("Cadastrar");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        panel.add(registerButton, gbc);

        registerFrame.getContentPane().add(panel);
        registerFrame.pack();
        registerFrame.setMinimumSize(new Dimension(350, 200));
        registerFrame.setLocationRelativeTo(null);
        registerFrame.setVisible(true);

        registerButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(registerFrame, "Preencha todos os campos.",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            } else if (loginController.registerUser(username, password)) {
                JOptionPane.showMessageDialog(registerFrame, "Usuário cadastrado com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                registerFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(registerFrame, "Nome de usuário já existe.",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void showAlert(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginApp::new);
    }
}
