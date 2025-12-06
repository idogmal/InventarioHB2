package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SidebarPanel extends JPanel {
    private InventoryPanel inventoryPanel;
    private MainApp mainApp;

    public SidebarPanel(MainApp mainApp, InventoryPanel inventoryPanel) {
        this.mainApp = mainApp;
        this.inventoryPanel = inventoryPanel;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(33, 37, 41)); // Dark background
        setPreferredSize(new Dimension(100, 0)); // Increased width for text
        setBorder(BorderFactory.createEmptyBorder(20, 5, 20, 5));

        // -- Actions --
        addButton(ModernIcon.IconType.ADD, "Novo", "Cadastrar Novo", e -> inventoryPanel.openRegisterForm(),
                Color.WHITE);
        add(Box.createVerticalStrut(15));

        addButton(ModernIcon.IconType.EDIT, "Editar", "Editar Selecionado", e -> inventoryPanel.editSelectedComputer(),
                Color.WHITE);
        add(Box.createVerticalStrut(15));

        // Delete is Red
        addButton(ModernIcon.IconType.DELETE, "Excluir", "Mover para Lixeira",
                e -> inventoryPanel.deleteSelectedComputer(), new Color(220, 53, 69));
        add(Box.createVerticalStrut(15));

        addButton(ModernIcon.IconType.EXPORT, "Exportar", "Exportar CSV", e -> inventoryPanel.exportCSV(), Color.WHITE);
        add(Box.createVerticalStrut(15));

        addButton(ModernIcon.IconType.HISTORY, "Histórico", "Ver Histórico", e -> inventoryPanel.openHistoryWindow(),
                Color.WHITE);
        add(Box.createVerticalStrut(15));

        // Spacer
        add(Box.createVerticalGlue());

        addButton(ModernIcon.IconType.RECYCLE_BIN, "Lixeira", "Abrir Lixeira",
                e -> inventoryPanel.openRecycleBinWindow(), Color.WHITE);
        add(Box.createVerticalStrut(15));

        addButton(ModernIcon.IconType.EXIT, "Sair", "Sair do Sistema", e -> mainApp.showLoginPanel(), Color.WHITE);
    }

    private void addButton(ModernIcon.IconType type, String label, String tooltip, ActionListener action,
            Color iconColor) {
        JButton btn = new JButton(label, new ModernIcon(type, 24, iconColor));
        btn.setToolTipText(tooltip);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(85, 75)); // Wider and taller
        btn.setPreferredSize(new Dimension(85, 75));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(Color.WHITE); // Text color
        btn.setBackground(new Color(52, 58, 64)); // Slightly lighter dark
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(73, 80, 87)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(73, 80, 87));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(52, 58, 64));
            }
        });

        add(btn);
    }
}
