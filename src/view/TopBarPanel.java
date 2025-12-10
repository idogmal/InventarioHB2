package view;

import controller.LoginController;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TopBarPanel extends JPanel {
    private InventoryPanel inventoryPanel;
    private LoginController loginController; // Added controller
    private MainApp mainApp; // Added mainApp reference for dialog

    private JLabel userLabel;
    private JButton btnTotal;
    private JButton btnActive;
    private JButton btnInactive;
    private JTextField searchField;
    private java.util.function.Consumer<String> filterListener;

    public TopBarPanel(MainApp mainApp, InventoryPanel inventoryPanel, LoginController loginController) {
        this.mainApp = mainApp;
        this.inventoryPanel = inventoryPanel;
        this.loginController = loginController;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(222, 226, 230))); // Bottom border
        setPreferredSize(new Dimension(0, 60));

        // -- Left: Logo/Brand + Count --
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        leftPanel.setOpaque(false);

        JLabel brandLabel = new JLabel("INVENTÁRIO HB");
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brandLabel.setForeground(new Color(33, 37, 41));

        // -- Stats Buttons --
        btnTotal = createFilterButton("Total: 0", new Color(50, 50, 50));
        btnActive = createFilterButton("Ativos: 0", new Color(40, 167, 69)); // Green
        btnInactive = createFilterButton("Inativos: 0", new Color(220, 53, 69)); // Red

        btnTotal.addActionListener(e -> fireFilterEvent("ALL"));
        btnActive.addActionListener(e -> fireFilterEvent("Ativo"));
        btnInactive.addActionListener(e -> fireFilterEvent("Inativo"));

        leftPanel.add(brandLabel);
        leftPanel.add(new JSeparator(SwingConstants.VERTICAL));
        leftPanel.add(btnTotal);
        leftPanel.add(Box.createHorizontalStrut(10));
        leftPanel.add(btnActive);
        leftPanel.add(Box.createHorizontalStrut(10));
        leftPanel.add(btnInactive);

        add(leftPanel, BorderLayout.WEST);

        // -- Center: Search Bar --
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        searchField = new PlaceholderTextField("Digite para pesquisar...", 30);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(300, 30));
        searchField.setMinimumSize(new Dimension(100, 30));

        // Search Listener
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateFilter();
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 10, 0, 5); // spacing
        centerPanel.add(new JLabel("🔍 "), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0; // Fill horizontal space
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);
        centerPanel.add(searchField, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // -- Right: User Info --
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 18));
        rightPanel.setOpaque(false);

        userLabel = new JLabel("Usuário: Admin");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(new Color(108, 117, 125));

        // Add mouse listener for hover/click effects
        userLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String currentUser = loginController.getLoggedInUser();
                if ("admin".equals(currentUser)) {
                    openUserManagement();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if ("admin".equals(loginController.getLoggedInUser())) {
                    userLabel.setForeground(new Color(0, 123, 255)); // Blue on hover
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                userLabel.setForeground(new Color(108, 117, 125)); // Reset color
            }
        });

        rightPanel.add(userLabel);
        add(rightPanel, BorderLayout.EAST);
    }

    private void openUserManagement() {
        UserManagementDialog dialog = new UserManagementDialog(mainApp, loginController);
        dialog.setVisible(true);
    }

    private void updateFilter() {
        if (inventoryPanel != null) {
            inventoryPanel.filterList(searchField.getText());
        }
    }

    public void updateStats(int total, int active, int inactive) {
        btnTotal.setText("Total: " + total);
        btnActive.setText("Ativos: " + active);
        btnInactive.setText("Inativos: " + inactive);
    }

    private JButton createFilterButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(color);
        btn.setBackground(new Color(245, 245, 245));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(230, 230, 230));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(245, 245, 245));
            }
        });
        return btn;
    }

    public void setFilterListener(java.util.function.Consumer<String> listener) {
        this.filterListener = listener;
    }

    private void fireFilterEvent(String status) {
        if (filterListener != null) {
            filterListener.accept(status);
        }
    }

    public void setCurrentUser(String user) {
        userLabel.setText("👤 " + user);

        if ("admin".equals(user)) {
            userLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            userLabel.setToolTipText("Clique para gerenciar usuários");
        } else {
            userLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            userLabel.setToolTipText(null);
        }
    }
}
