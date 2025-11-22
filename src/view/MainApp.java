package view;

import controller.InventoryController;
import controller.LoginController;
import javax.swing.*;
import java.awt.*;

public class MainApp extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;

    private LoginPanel loginPanel;
    private LocalSelectionPanel localSelectionPanel;
    private InventoryPanel inventoryPanel;

    private LoginController loginController;
    private InventoryController inventoryController;

    // Armazena o usuário logado
    private String currentUser;

    public MainApp() {
        super("Sistema de Inventário");
        initControllers();
        initComponents();
    }

    private void initControllers() {
        loginController = new LoginController();
        inventoryController = new InventoryController();
    }

    private void initComponents() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Cria os painéis passando o MainApp para facilitar a troca de telas
        loginPanel = new LoginPanel(this, loginController);
        localSelectionPanel = new LocalSelectionPanel(this);
        inventoryPanel = new InventoryPanel(this, inventoryController);

        // Adiciona os painéis ao cardPanel
        cardPanel.add(loginPanel, "Login");
        cardPanel.add(localSelectionPanel, "LocalSelection");
        cardPanel.add(inventoryPanel, "Inventory");

        getContentPane().add(cardPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }

    // Exibe o painel de login
    public void showLoginPanel() {
        cardLayout.show(cardPanel, "Login");
    }

    // Exibe o painel de seleção de local e define o usuário logado
    public void showLocalSelectionPanel(String user) {
        currentUser = user;
        inventoryController.setCurrentUser(user);
        cardLayout.show(cardPanel, "LocalSelection");
    }

    // Exibe o painel de inventário e aplica o filtro de local
    public void showInventoryPanel(String location) {
        inventoryPanel.setLocationFilter(location);
        cardLayout.show(cardPanel, "Inventory");
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainApp app = new MainApp();
            app.setVisible(true);
        });
    }
}
