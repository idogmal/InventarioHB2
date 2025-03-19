package view;

import controller.InventoryController;
import model.Computer;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryPanel extends JPanel {
    private MainApp mainApp;
    private InventoryController controller;
    private JTable table;
    private ComputerTableModel tableModel;
    private JLabel computerCountLabel;
    private JTextField searchField;
    private String currentLocation = ""; // Armazena o local atual

    public InventoryPanel(MainApp mainApp, InventoryController controller) {
        this.mainApp = mainApp;
        this.controller = controller;
        initComponents();

        // Após construir o painel, recarrega os computadores do banco e atualiza a tabela filtrada
        controller.refreshComputers();
        List<Computer> initialList = (currentLocation.isEmpty()) ? controller.getComputerList()
                : controller.getComputersByLocation(currentLocation);
        tableModel.setComputers(initialList);
        updateComputerCountLabel();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Painel superior: botão voltar, contador e campo de busca
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        JButton backButton = new JButton("⬅ Voltar");
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        backButton.addActionListener(e -> mainApp.showLocalSelectionPanel(controller.getCurrentUser()));

        computerCountLabel = new JLabel("Total de Computadores: " + controller.getComputerCount(), SwingConstants.CENTER);
        computerCountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        searchField = new JTextField(30);
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, searchField.getPreferredSize().height));
        searchField.setToolTipText("Buscar por etiqueta, modelo, marca ou usuário");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });

        topPanel.add(backButton);
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(computerCountLabel);
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(searchField);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(topPanel, BorderLayout.NORTH);

        // Tabela
        tableModel = new ComputerTableModel(controller.getComputerList());
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(table);
        add(tableScrollPane, BorderLayout.CENTER);

        // Painel inferior: ações básicas
        JPanel basicActionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Cadastrar");
        JButton editButton = new JButton("Editar");
        JButton deleteButton = new JButton("Excluir");
        basicActionPanel.add(addButton);
        basicActionPanel.add(editButton);
        basicActionPanel.add(deleteButton);

        // Painel inferior: funções adicionais
        JPanel extraActionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton exportButton = new JButton("Exportar para CSV");
        JButton backupButton = new JButton("Salvar Backup");
        JButton restoreButton = new JButton("Restaurar Backup");
        JButton historyButton = new JButton("Histórico");
        JButton manageUsersButton = new JButton("Gerenciar Usuários");
        extraActionPanel.add(exportButton);
        extraActionPanel.add(backupButton);
        extraActionPanel.add(restoreButton);
        extraActionPanel.add(historyButton);
        extraActionPanel.add(manageUsersButton);

        // Agrupa os dois painéis de ações na parte inferior
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.add(basicActionPanel);
        bottomPanel.add(extraActionPanel);
        add(bottomPanel, BorderLayout.SOUTH);

        // Ações dos botões básicos

        addButton.addActionListener(e -> {
            ComputerFormHandler formHandler = new ComputerFormHandler(controller);
            formHandler.openForm(null, controller.getCurrentUser());
            controller.refreshComputers();
            tableModel.setComputers(controller.getComputersByLocation(currentLocation));
            updateComputerCountLabel();
        });

        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                Computer selectedComputer = tableModel.getComputerAt(selectedRow);
                ComputerFormHandler formHandler = new ComputerFormHandler(controller);
                formHandler.openForm(selectedComputer, controller.getCurrentUser());
                controller.refreshComputers();
                tableModel.setComputers(controller.getComputersByLocation(currentLocation));
                updateComputerCountLabel();
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um computador para editar.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                Computer selectedComputer = tableModel.getComputerAt(selectedRow);
                controller.deleteComputer(selectedComputer, controller.getCurrentUser());
                controller.refreshComputers();
                tableModel.setComputers(controller.getComputersByLocation(currentLocation));
                updateComputerCountLabel();
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um computador para excluir.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Ações das funções adicionais
        exportButton.addActionListener(e -> handleExportAction(this));
        backupButton.addActionListener(e -> handleBackupAction(this));
        restoreButton.addActionListener(e -> handleRestoreAction(this));
        historyButton.addActionListener(e -> openHistoryWindow());
        manageUsersButton.addActionListener(e -> openUserManagementWindow());
    }

    /**
     * Atualiza a lista de computadores aplicando a filtragem do local (currentLocation)
     * e, se houver texto na busca, filtra também por esse critério.
     */
    private void filterTable() {
        String query = searchField.getText().toLowerCase();
        // Obtém a lista filtrada pelo local
        List<Computer> list = controller.getComputersByLocation(currentLocation);
        // Se houver um termo de busca, filtra adicionalmente
        if (!query.trim().isEmpty()) {
            list = list.stream().filter(computer ->
                    computer.getTag().toLowerCase().contains(query) ||
                            computer.getModel().toLowerCase().contains(query) ||
                            computer.getBrand().toLowerCase().contains(query) ||
                            computer.getUserName().toLowerCase().contains(query)
            ).collect(Collectors.toList());
        }
        tableModel.setComputers(list);
        computerCountLabel.setText("Total de Computadores: " + list.size());
    }

    private void updateComputerCountLabel() {
        // Atualiza a contagem com base na lista filtrada pelo local
        List<Computer> list = controller.getComputersByLocation(currentLocation);
        computerCountLabel.setText("Total de Computadores: " + list.size());
    }

    /**
     * Define o filtro de localidade e atualiza a tabela.
     */
    public void setLocationFilter(String location) {
        this.currentLocation = (location != null) ? location.trim() : "";
        controller.refreshComputers(); // Atualiza a lista com os dados persistidos
        List<Computer> filteredList = controller.getComputersByLocation(currentLocation);
        tableModel.setComputers(filteredList);
        computerCountLabel.setText("Total de Computadores: " + filteredList.size());
    }

    private void handleExportAction(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar arquivo CSV");
        int userSelection = fileChooser.showSaveDialog(parent);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                controller.exportToCSV(file.getAbsolutePath());
                JOptionPane.showMessageDialog(parent, "Dados exportados para " + file.getName(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent, "Não foi possível exportar os dados.", "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void handleBackupAction(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Backup");
        int userSelection = fileChooser.showSaveDialog(parent);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                controller.backupData(file.getAbsolutePath());
                JOptionPane.showMessageDialog(parent, "Backup realizado com sucesso em " + file.getName(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent, "Falha ao realizar o backup.", "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void handleRestoreAction(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Restaurar Backup");
        int userSelection = fileChooser.showOpenDialog(parent);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                controller.restoreData(file.getAbsolutePath());
                controller.refreshComputers();
                tableModel.setComputers(controller.getComputersByLocation(currentLocation));
                updateComputerCountLabel();
                JOptionPane.showMessageDialog(parent, "Dados restaurados com sucesso de " + file.getName(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent, "Falha ao restaurar os dados.", "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void openHistoryWindow() {
        HistoryWindow historyWindow = new HistoryWindow(controller);
        historyWindow.showHistory();
    }

    private void openUserManagementWindow() {
        JFrame userManagementFrame = new JFrame("Gerenciar Usuários");
        userManagementFrame.setSize(300, 300);
        userManagementFrame.setLocationRelativeTo(null);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        controller.getUsers().stream()
                .map(user -> user.getUsername())
                .forEach(listModel::addElement);
        JList<String> userList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(userList);
        scrollPane.setPreferredSize(new Dimension(250, 150));

        JButton editPasswordButton = new JButton("Editar Senha");
        JButton deleteUserButton = new JButton("Excluir Usuário");

        editPasswordButton.addActionListener(e -> {
            String selectedUser = userList.getSelectedValue();
            if (selectedUser != null) {
                openEditPasswordWindow(selectedUser);
            } else {
                JOptionPane.showMessageDialog(userManagementFrame, "Selecione um usuário.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteUserButton.addActionListener(e -> {
            String selectedUser = userList.getSelectedValue();
            if (selectedUser != null) {
                controller.deleteUser(selectedUser);
                listModel.removeElement(selectedUser);
                JOptionPane.showMessageDialog(userManagementFrame, "Usuário excluído com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(userManagementFrame, "Selecione um usuário.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(scrollPane);
        panel.add(Box.createVerticalStrut(10));
        panel.add(editPasswordButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(deleteUserButton);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        userManagementFrame.getContentPane().add(panel);
        userManagementFrame.setVisible(true);
    }

    private void openEditPasswordWindow(String username) {
        JFrame editPasswordFrame = new JFrame("Editar Senha");
        editPasswordFrame.setSize(300, 150);
        editPasswordFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPasswordField newPasswordField = new JPasswordField(20);
        JButton saveButton = new JButton("Salvar");

        panel.add(new JLabel("Nova Senha:"));
        panel.add(newPasswordField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(saveButton);

        editPasswordFrame.getContentPane().add(panel);
        editPasswordFrame.setVisible(true);

        saveButton.addActionListener(e -> {
            String newPassword = new String(newPasswordField.getPassword());
            if (newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(editPasswordFrame, "A senha não pode estar vazia.", "Erro", JOptionPane.ERROR_MESSAGE);
            } else {
                controller.editUserPassword(username, newPassword);
                JOptionPane.showMessageDialog(editPasswordFrame, "Senha atualizada com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                editPasswordFrame.dispose();
            }
        });
    }
}
