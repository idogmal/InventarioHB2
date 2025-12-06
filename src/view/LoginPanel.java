package view;

import controller.LoginController;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class LoginPanel extends JPanel {
    private MainApp mainApp;
    private LoginController loginController;

    // Colors
    private final Color BACKGROUND_COLOR = new Color(240, 242, 245); // Soft Gray
    private final Color ACCENT_COLOR = new Color(59, 130, 246); // Blue
    private final Color TEXT_COLOR = new Color(33, 37, 41);

    public LoginPanel(MainApp mainApp, LoginController loginController) {
        this.mainApp = mainApp;
        this.loginController = loginController;
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout()); // To center the card
        setBackground(BACKGROUND_COLOR);

        // --- Card Panel ---
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 20, 20);

                // Main Body
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 20, 20);

                g2.dispose();
            }
        };
        cardPanel.setLayout(new GridBagLayout()); // Content within card
        cardPanel.setPreferredSize(new Dimension(350, 450));
        cardPanel.setOpaque(false); // Let custom paint handle background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20); // Padding inside card
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // 1. Logo
        JLabel logoLabel = new JLabel(new ModernIcon(ModernIcon.IconType.USERS, 64, ACCENT_COLOR));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(30, 20, 10, 20); // Top padding
        cardPanel.add(logoLabel, gbc);

        // 2. Title
        JLabel titleLabel = new JLabel("INVENTÁRIO");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 20, 20, 20);
        cardPanel.add(titleLabel, gbc);

        // 3. User Input
        JLabel userLabel = new JLabel("Nome de Usuário");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userLabel.setForeground(Color.GRAY);
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 20, 5, 20);
        cardPanel.add(userLabel, gbc);

        PlaceholderTextField userField = new PlaceholderTextField("Digite seu user...", 20);
        styleInput(userField);
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 20, 15, 20);
        cardPanel.add(userField, gbc);

        // 4. Password Input
        JLabel passLabel = new JLabel("Senha");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLabel.setForeground(Color.GRAY);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 20, 5, 20);
        cardPanel.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField(20);
        styleInput(passField);
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 20, 25, 20);
        cardPanel.add(passField, gbc);

        // 5. Login Button
        JButton loginButton = new JButton("ENTRAR");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(ACCENT_COLOR);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setPreferredSize(new Dimension(0, 40));

        // Rounded border for button (simulation via simple connection)
        // Login Logic
        loginButton.addActionListener(e -> attemptLogin(userField, passField));

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 20, 15, 20);
        cardPanel.add(loginButton, gbc);

        // 6. Create Account Link
        JButton createAccountBtn = new JButton("Criar nova conta");
        createAccountBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        createAccountBtn.setForeground(ACCENT_COLOR);
        createAccountBtn.setContentAreaFilled(false);
        createAccountBtn.setBorderPainted(false);
        createAccountBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createAccountBtn.addActionListener(e -> RegisterDialog.showDialog(mainApp, loginController));

        gbc.gridy = 7;
        gbc.insets = new Insets(0, 20, 30, 20);
        cardPanel.add(createAccountBtn, gbc);

        // Key Listeners
        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginButton.doClick();
                }
            }
        };
        userField.addKeyListener(enterKey);
        passField.addKeyListener(enterKey);

        add(cardPanel); // Add card to main panel (centered by GridBagLayout default)
    }

    private void styleInput(JComponent field) {
        field.setPreferredSize(new Dimension(0, 35));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        if (field instanceof JTextField) {
            // Placeholder for future specific styling needs
        }
    }

    private void attemptLogin(JTextField userField, JPasswordField passField) {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (loginController.login(username, password)) {
            // Success
            mainApp.showInventoryPanel(username);
        } else {
            JOptionPane.showMessageDialog(this, "Usuário ou senha incorretos.",
                    "Erro de Login", JOptionPane.ERROR_MESSAGE);
        }
    }
}
