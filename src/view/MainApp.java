package view;

import controller.InventoryController;
import controller.LoginController;
import javax.swing.*;
import java.awt.*;

import com.formdev.flatlaf.FlatLightLaf;

public class MainApp extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;

    private LoginPanel loginPanel;
    private InventoryPanel inventoryPanel;

    private LoginController loginController;
    private InventoryController inventoryController;

    // Armazena o usuário logado
    private String currentUser;

    // Componentes do Dashboard
    private SidebarPanel sidebarPanel;
    private TopBarPanel topBarPanel;
    private JPanel dashboardPanel; // Painel que contém o layout do dashboard

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
        inventoryPanel = new InventoryPanel(this, inventoryController);

        // Adiciona os painéis ao cardPanel
        cardPanel.add(loginPanel, "Login");

        // --- Montagem do Dashboard ---
        dashboardPanel = new JPanel(new BorderLayout());

        sidebarPanel = new SidebarPanel(this, inventoryPanel);
        topBarPanel = new TopBarPanel(this, inventoryPanel, loginController);

        // Conecta filtro de status
        topBarPanel.setFilterListener(status -> inventoryPanel.setStatusFilter(status));

        // Conecta o listener de contagem
        inventoryPanel.setStatsListener((total, active, inactive) -> topBarPanel.updateStats(total, active, inactive));

        dashboardPanel.add(sidebarPanel, BorderLayout.WEST);
        dashboardPanel.add(topBarPanel, BorderLayout.NORTH);
        dashboardPanel.add(inventoryPanel, BorderLayout.CENTER);

        cardPanel.add(dashboardPanel, "Inventory"); // Agora adiciona o Dashboard inteiro, não só o InventoryPanel

        getContentPane().add(cardPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }

    // Exibe o painel de login
    public void showLoginPanel() {
        cardLayout.show(cardPanel, "Login");
    }

    // Exibe o painel de inventário e define o usuário logado
    public void showInventoryPanel(String user) {
        currentUser = user;
        inventoryController.setCurrentUser(user);

        // Atualiza info do usuário logado no TopBar
        topBarPanel.setCurrentUser(user);

        inventoryPanel.setLocationFilter(""); // Limpa filtro para mostrar tudo
        cardLayout.show(cardPanel, "Inventory");
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public static final String APP_VERSION = "1.0.2";

    public static void main(String[] args) {
        // Configura o FlatLaf antes de iniciar a GUI
        FlatLightLaf.setup();

        // Customização de Cores (Soft Gray para reduzir cansaço visual)
        UIManager.put("Panel.background", new Color(240, 242, 245));
        UIManager.put("Table.background", Color.WHITE);

        SwingUtilities.invokeLater(() -> {
            MainApp app = new MainApp();
            app.setVisible(true);

            // Verifica atualizações
            util.UpdateManager.checkForUpdates(app, APP_VERSION);
        });
    }
}
