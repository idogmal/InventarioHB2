package view;

import controller.InventoryController;
import model.Computer;
import model.User;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class InventoryApp {

    private JTable table;                 // Tabela para exibir os computadores
    private ComputerTableModel tableModel; // Modelo da tabela
    private InventoryController controller; // Controlador principal
    private JLabel computerCountLabel;     // Label para contagem de computadores
    private JTextField searchField;        // Campo de busca

    /**
     * Método para iniciar a aplicação passando um JFrame já criado.
     */
    public void start(JFrame frame) {
        // Inicializar o controlador, caso não esteja definido
        if (controller == null) {
            initializeController();
        }

        // Criação do modelo e da tabela
        tableModel = new ComputerTableModel(controller.getComputerList());
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Label de contagem de computadores
        computerCountLabel = new JLabel();
        updateComputerCountLabel();

        // Campo de busca com listener para atualizar a tabela ao digitar
        searchField = new JTextField(30);
        searchField.setToolTipText("Buscar por etiqueta, modelo, marca ou usuário");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                filterTable();
            }
            public void removeUpdate(DocumentEvent e) {
                filterTable();
            }
            public void changedUpdate(DocumentEvent e) {
                filterTable();
            }
        });

        // Botões de ação
        JButton addButton = new JButton("Cadastrar");
        JButton editButton = new JButton("Editar");
        JButton deleteButton = new JButton("Excluir");
        JButton exportButton = new JButton("Exportar para CSV");
        JButton backupButton = new JButton("Salvar");
        JButton restoreButton = new JButton("Restaurar");
        JButton historyButton = new JButton("Visualizar Histórico");
        JButton manageUsersButton = new JButton("Gerenciar Usuários");
        JButton backButton = new JButton("⬅ Voltar");

        // Verificar se o usuário logado é admin
        if ("admin".equals(controller.getCurrentUser())) {
            manageUsersButton.setVisible(true);
        } else {
            manageUsersButton.setVisible(false);
        }

        // Ações dos botões
        addButton.addActionListener(e -> openComputerForm(null));
        editButton.addActionListener(e -> handleEditAction());
        deleteButton.addActionListener(e -> handleDeleteAction());
        exportButton.addActionListener(e -> handleExportAction(frame));
        backupButton.addActionListener(e -> handleBackupAction(frame));
        restoreButton.addActionListener(e -> handleRestoreAction(frame));
        historyButton.addActionListener(e -> openHistoryWindow());
        manageUsersButton.addActionListener(e -> openUserManagementWindow());
        backButton.addActionListener(e -> {
            String currentUser = controller.getCurrentUser();
            if (currentUser == null || currentUser.isEmpty()) {
                showAlert("Erro", "Usuário atual não está definido. Não é possível voltar para a seleção de localidade.");
                return;
            }
            frame.dispose();
            reopenLocationSelection(currentUser);
        });

        // Painel superior com o botão voltar, contador e campo de busca
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        computerCountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, searchField.getPreferredSize().height));
        topPanel.add(backButton);
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(computerCountLabel);
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(searchField);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel central com a tabela
        JScrollPane tableScrollPane = new JScrollPane(table);

        // Painel inferior com os botões de ação
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(backupButton);
        buttonPanel.add(restoreButton);
        buttonPanel.add(historyButton);
        buttonPanel.add(manageUsersButton);

        // Layout principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.setTitle("Inventário de Computadores");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.setSize(900, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Atualiza o label com a contagem de computadores.
     */
    private void updateComputerCountLabel() {
        computerCountLabel.setText("Total de Computadores: " + controller.getComputerCount());
    }

    /**
     * Filtra a tabela conforme o texto digitado no campo de busca.
     */
    private void filterTable() {
        String query = searchField.getText();
        List<Computer> filteredList = controller.searchComputers(query);
        tableModel.setComputers(filteredList);
        updateComputerCountLabel();
    }

    /**
     * Método para reabrir a seleção de local (chama o método da classe LoginApp).
     */
    private void reopenLocationSelection(String currentUser) {
        if (currentUser == null || currentUser.isEmpty()) {
            System.out.println("Erro: Usuário atual é nulo ou vazio ao tentar reabrir a seleção de localidade.");
            return;
        }
        // Reutiliza a implementação da classe LoginApp
        LoginApp loginApp = new LoginApp();
        loginApp.showLocationSelection(null, currentUser);
    }

    /**
     * Inicializa o controlador principal.
     */
    private void initializeController() {
        controller = new InventoryController();
    }

    /**
     * Define o controlador externo na aplicação.
     */
    public void setController(InventoryController controller) {
        this.controller = controller;
    }

    /**
     * Define o filtro de localidade na tabela.
     */
    public void setLocationFilter(String location) {
        List<Computer> filteredList = controller.getComputersByLocation(location);
        tableModel.setComputers(filteredList);
        updateComputerCountLabel();
    }

    /**
     * Abre o formulário para cadastrar ou editar um computador.
     */
    private void openComputerForm(Computer computer) {
        String currentUser = controller.getCurrentUser();
        if (currentUser == null || currentUser.isEmpty()) {
            showAlert("Erro", "Nenhum usuário logado. Não é possível cadastrar computadores.");
            return;
        }
        // Abre o formulário (Swing) para cadastro/edição
        ComputerFormHandler formHandler = new ComputerFormHandler(controller);
        formHandler.openForm(computer, currentUser);
        // Atualiza a tabela após cadastro ou edição
        tableModel.setComputers(controller.getComputerList());
        updateComputerCountLabel();
    }

    /**
     * Ação para editar um computador selecionado.
     */
    private void handleEditAction() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            Computer selectedComputer = tableModel.getComputerAt(selectedRow);
            openComputerForm(selectedComputer);
        } else {
            showAlert("Seleção necessária", "Por favor, selecione um computador para editar.");
        }
    }

    /**
     * Ação para excluir um computador selecionado.
     */
    private void handleDeleteAction() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            Computer selectedComputer = tableModel.getComputerAt(selectedRow);
            controller.deleteComputer(selectedComputer, controller.getCurrentUser());
            tableModel.setComputers(controller.getComputerList());
            updateComputerCountLabel();
        } else {
            showAlert("Seleção necessária", "Por favor, selecione um computador para excluir.");
        }
    }

    /**
     * Ação para exportar os dados da tabela para um arquivo CSV.
     */
    private void handleExportAction(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar arquivo CSV");
        int userSelection = fileChooser.showSaveDialog(parent);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                controller.exportToCSV(file.getAbsolutePath());
                showAlert("Sucesso", "Dados exportados para " + file.getName());
            } catch (IOException ex) {
                showAlert("Erro", "Não foi possível exportar os dados.");
                ex.printStackTrace();
            }
        }
    }

    /**
     * Ação para realizar o backup dos dados.
     */
    private void handleBackupAction(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Backup");
        int userSelection = fileChooser.showSaveDialog(parent);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                controller.backupData(file.getAbsolutePath());
                showAlert("Sucesso", "Backup realizado com sucesso em " + file.getName());
            } catch (IOException ex) {
                showAlert("Erro", "Falha ao realizar o backup.");
                ex.printStackTrace();
            }
        }
    }

    /**
     * Ação para restaurar dados a partir de um backup.
     */
    private void handleRestoreAction(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Restaurar Backup");
        int userSelection = fileChooser.showOpenDialog(parent);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                controller.restoreData(file.getAbsolutePath());
                tableModel.setComputers(controller.getComputerList());
                updateComputerCountLabel();
                showAlert("Sucesso", "Dados restaurados com sucesso de " + file.getName());
            } catch (IOException ex) {
                showAlert("Erro", "Falha ao restaurar os dados.");
                ex.printStackTrace();
            }
        }
    }

    /**
     * Ação para abrir a janela de histórico.
     */
    private void openHistoryWindow() {
        // Assume que HistoryWindow foi convertido para Swing
        HistoryWindow historyWindow = new HistoryWindow(controller);
        historyWindow.showHistory();
    }

    /**
     * Ação para abrir a janela de gerenciamento de usuários.
     */
    private void openUserManagementWindow() {
        // Recria uma janela similar à implementada no LoginApp
        JFrame userManagementFrame = new JFrame("Gerenciar Usuários");
        userManagementFrame.setSize(300, 300);
        userManagementFrame.setLocationRelativeTo(null);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        controller.getUsers().stream()
                .map(User::getUsername)
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
                showAlert("Erro", "Selecione um usuário.");
            }
        });

        deleteUserButton.addActionListener(e -> {
            String selectedUser = userList.getSelectedValue();
            if (selectedUser != null) {
                controller.deleteUser(selectedUser);
                listModel.removeElement(selectedUser);
                showAlert("Sucesso", "Usuário excluído com sucesso.");
            } else {
                showAlert("Erro", "Selecione um usuário.");
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

    /**
     * Abre a janela para editar a senha de um usuário.
     */
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
                showAlert("Erro", "A senha não pode estar vazia.");
            } else {
                controller.editUserPassword(username, newPassword);
                showAlert("Sucesso", "Senha atualizada com sucesso.");
                editPasswordFrame.dispose();
            }
        });
    }

    /**
     * Exibe um alerta para o usuário.
     */
    private void showAlert(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Modelo da tabela para exibir os dados dos computadores.
     */
    private class ComputerTableModel extends AbstractTableModel {

        private final String[] columnNames = {
                "Etiqueta TI", "Modelo", "Marca", "Estado", "Usuário",
                "Número de Série", "Versão do Windows", "Versão do Office",
                "Localização", "Data de Compra"
        };
        private List<Computer> computers;

        public ComputerTableModel(List<Computer> computers) {
            this.computers = computers;
        }

        public void setComputers(List<Computer> computers) {
            this.computers = computers;
            fireTableDataChanged();
        }

        public Computer getComputerAt(int rowIndex) {
            return computers.get(rowIndex);
        }

        @Override
        public int getRowCount() {
            return computers == null ? 0 : computers.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Computer c = computers.get(rowIndex);
            switch (columnIndex) {
                case 0: return c.getTag();
                case 1: return c.getModel();
                case 2: return c.getBrand();
                case 3: return c.getState();
                case 4: return c.getUserName();
                case 5: return c.getSerialNumber();
                case 6: return c.getWindowsVersion();
                case 7: return c.getOfficeVersion();
                case 8: return c.getLocation();
                case 9: return c.getPurchaseDate();
                default: return "";
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            // Se desejar permitir edição direta na tabela, retorne true para as colunas editáveis
            return false;
        }
    }

    /**
     * Substitui o placeholder por código real para abrir o inventário.
     * Chamado pelo LoginApp (showLocationSelection) depois que o usuário escolhe NPD ou INFAN.
     */
    public static void openInventoryApp(String location, String currentUser) {
        // Cria o controlador do inventário
        InventoryController invController = new InventoryController();
        invController.setCurrentUser(currentUser);

        // Cria a janela do inventário
        JFrame inventoryFrame = new JFrame("Inventário - " + location);
        inventoryFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Instancia o InventoryApp
        InventoryApp inventoryApp = new InventoryApp();
        inventoryApp.setController(invController);
        // Monta a interface
        inventoryApp.start(inventoryFrame);
        // Aplica o filtro de localidade
        inventoryApp.setLocationFilter(location);

        // Exibe a janela
        inventoryFrame.setVisible(true);
    }
}
