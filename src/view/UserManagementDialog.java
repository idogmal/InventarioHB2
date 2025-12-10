package view;

import controller.LoginController;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class UserManagementDialog extends JDialog {
    private final LoginController loginController;
    private final MainApp mainApp;
    private DefaultListModel<User> listModel;
    private JList<User> userList;

    public UserManagementDialog(MainApp mainApp, LoginController loginController) {
        super(mainApp, "Gerenciamento de Usuários", true);
        this.mainApp = mainApp;
        this.loginController = loginController;

        initComponents();
        loadUsers();
    }

    private void initComponents() {
        setSize(400, 500);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Usuários Cadastrados");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerPanel.add(titleLabel);

        add(headerPanel, BorderLayout.NORTH);

        // List
        listModel = new DefaultListModel<>();
        userList = new JList<>(listModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setCellRenderer(new UserListCellRenderer());
        userList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userList.setFixedCellHeight(40);

        JScrollPane scrollPane = new JScrollPane(userList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JButton addButton = new JButton("Adicionar");
        styleButton(addButton, new Color(40, 167, 69)); // Green
        addButton.addActionListener(e -> {
            RegisterDialog.showDialog(mainApp, loginController);
            loadUsers(); // Refresh list after closing dialog
        });

        JButton deleteButton = new JButton("Remover");
        styleButton(deleteButton, new Color(220, 53, 69)); // Red
        deleteButton.addActionListener(e -> deleteSelectedUser());

        JButton closeButton = new JButton("Fechar");
        styleButton(closeButton, new Color(108, 117, 125)); // Grey
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadUsers() {
        listModel.clear();
        List<User> users = loginController.getUsers();
        for (User user : users) {
            listModel.addElement(user);
        }
    }

    private void deleteSelectedUser() {
        User selectedUser = userList.getSelectedValue();
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para remover.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if ("admin".equalsIgnoreCase(selectedUser.getUsername())) {
            JOptionPane.showMessageDialog(this, "O usuário administrador não pode ser removido.", "Ação Proibida",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja apagar o usuário '" + selectedUser.getUsername() + "'?",
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                loginController.deleteUser(selectedUser.getUsername());
                loadUsers();
                JOptionPane.showMessageDialog(this, "Usuário removido com sucesso.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao remover: " + e.getMessage(), "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(100, 35));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // Custom Renderer for nicer look
    private static class UserListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setBorder(new EmptyBorder(0, 10, 0, 10));
            if (value instanceof User) {
                User u = (User) value;
                setText(u.getUsername());
                setIcon(new ModernIcon(ModernIcon.IconType.USERS, 16, isSelected ? Color.WHITE : Color.DARK_GRAY));
            }
            return this;
        }
    }
}
