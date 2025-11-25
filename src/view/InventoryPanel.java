package view;

import controller.InventoryController;
import model.Computer;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;

import java.util.ArrayList; // Adicionado para inicialização segura
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
        // Inicializa tableModel antes de initComponents para evitar NPE
        this.tableModel = new ComputerTableModel(
                controller.getComputerList() != null ? controller.getComputerList() : new ArrayList<>());
        initComponents();

        // Após construir o painel, recarrega os computadores do banco e atualiza a
        // tabela filtrada
        // (Mantendo a lógica original)
        controller.refreshComputers();
        List<Computer> initialList = (currentLocation.isEmpty()) ? controller.getComputerList()
                : controller.getComputersByLocation(currentLocation);
        tableModel.setComputers(initialList);
        updateComputerCountLabel(initialList.size()); // Passa o tamanho da lista inicial
    }

    private void initComponents() {
        // Layout principal: Bordas com espaçamento
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // --- Painel superior: botão voltar, contador e campo de busca ---
        // Usando GridBagLayout para melhor controle do alinhamento
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcTop = new GridBagConstraints();
        gbcTop.insets = new Insets(5, 5, 5, 5); // Espaçamento

        JButton backButton = new JButton("Sair");
        gbcTop.gridx = 0;
        gbcTop.gridy = 0;
        gbcTop.anchor = GridBagConstraints.WEST; // Alinha à esquerda
        topPanel.add(backButton, gbcTop);

        computerCountLabel = new JLabel("Total de Computadores: 0", SwingConstants.CENTER); // Será atualizado
        gbcTop.gridx = 1;
        gbcTop.gridy = 0;
        gbcTop.weightx = 1.0; // Ocupa espaço horizontalmente
        gbcTop.anchor = GridBagConstraints.CENTER; // Centraliza
        topPanel.add(computerCountLabel, gbcTop);

        searchField = new JTextField(25); // Campo de busca
        searchField.setToolTipText("Buscar por etiqueta, modelo, marca ou usuário");
        gbcTop.gridx = 0;
        gbcTop.gridy = 1; // Segunda linha
        gbcTop.gridwidth = 3; // Ocupa toda a largura
        gbcTop.fill = GridBagConstraints.HORIZONTAL;
        gbcTop.anchor = GridBagConstraints.CENTER;
        gbcTop.weightx = 1.0; // Reset do peso
        topPanel.add(searchField, gbcTop);

        add(topPanel, BorderLayout.NORTH); // Adiciona painel superior ao Norte

        // --- Tabela (Central) ---
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true); // Habilita ordenação
        table.setFillsViewportHeight(true); // Ocupa altura disponível

        // Configura o renderizador e editor para a coluna de observações (índice 11 -
        // OBS)
        table.getColumn("OBS").setCellRenderer(new ButtonRenderer());
        table.getColumn("OBS").setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane tableScrollPane = new JScrollPane(table);
        add(tableScrollPane, BorderLayout.CENTER); // Adiciona tabela ao Centro

        // --- Painel de Botões à Esquerda (NOVO LAYOUT) ---
        JPanel leftButtonPanel = new JPanel();
        leftButtonPanel.setLayout(new BoxLayout(leftButtonPanel, BoxLayout.Y_AXIS)); // Layout vertical
        leftButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        // Criar os botões (SEM backup/restore)
        JButton addButton = new JButton("Cadastrar");
        JButton editButton = new JButton("Editar");
        JButton deleteButton = new JButton("Excluir");
        JButton exportButton = new JButton("Exportar CSV");
        JButton historyButton = new JButton("Histórico");
        JButton manageUsersButton = new JButton("Usuários");

        // Define um tamanho preferencial/máximo para tentar uniformizar
        Dimension buttonMaxDim = new Dimension(130, 30);
        addButton.setPreferredSize(buttonMaxDim);
        editButton.setPreferredSize(buttonMaxDim);
        deleteButton.setPreferredSize(buttonMaxDim);
        exportButton.setPreferredSize(buttonMaxDim);
        historyButton.setPreferredSize(buttonMaxDim);
        manageUsersButton.setPreferredSize(buttonMaxDim);
        addButton.setMaximumSize(buttonMaxDim);
        editButton.setMaximumSize(buttonMaxDim);
        deleteButton.setMaximumSize(buttonMaxDim);
        exportButton.setMaximumSize(buttonMaxDim);
        historyButton.setMaximumSize(buttonMaxDim);
        manageUsersButton.setMaximumSize(buttonMaxDim);

        // Alinha os botões no centro horizontalmente dentro do BoxLayout vertical
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        editButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exportButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        historyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        manageUsersButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Adicionar botões ao painel esquerdo com espaçamento
        leftButtonPanel.add(addButton);
        leftButtonPanel.add(Box.createRigidArea(new Dimension(0, 8))); // Espaçador vertical
        leftButtonPanel.add(editButton);
        leftButtonPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        leftButtonPanel.add(deleteButton);
        leftButtonPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Espaço maior
        leftButtonPanel.add(exportButton);
        leftButtonPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        leftButtonPanel.add(historyButton);
        leftButtonPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        leftButtonPanel.add(manageUsersButton);
        leftButtonPanel.add(Box.createVerticalGlue()); // Empurra os botões para cima

        add(leftButtonPanel, BorderLayout.WEST); // Adiciona o painel de botões à Esquerda

        // --- Action Listeners (Originais, exceto backup/restore que foram removidos)
        // ---

        // Listener do botão Sair (no topPanel)
        backButton.addActionListener(e -> {
            if (searchField != null)
                searchField.setText(""); // Limpa busca ao sair
            mainApp.showLoginPanel();
        });

        // Listener do campo de Busca (no topPanel)
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

        // Listener do botão Adicionar
        addButton.addActionListener(e -> {
            ComputerFormHandler formHandler = new ComputerFormHandler(controller);
            formHandler.openForm(null, controller.getCurrentUser(), currentLocation);
            // Ações após o form fechar (lógica original)
            controller.refreshComputers();
            filterTable(); // Aplica filtro de local/busca e atualiza tabela/contagem
            // updateComputerCountLabel(); // Chamado dentro de filterTable
        });

        // Listener do botão Editar
        editButton.addActionListener(e -> {
            int selectedRowView = table.getSelectedRow(); // Índice na visão da tabela
            if (selectedRowView >= 0) {
                int selectedRowModel = table.convertRowIndexToModel(selectedRowView); // Converte para índice do modelo
                Computer selectedComputer = tableModel.getComputerAt(selectedRowModel); // Pega do modelo
                if (selectedComputer != null) {
                    ComputerFormHandler formHandler = new ComputerFormHandler(controller);
                    formHandler.openForm(selectedComputer, controller.getCurrentUser(), currentLocation);
                    // Ações após o form fechar (lógica original)
                    controller.refreshComputers();
                    filterTable();
                    // updateComputerCountLabel();
                } else {
                    JOptionPane.showMessageDialog(this, "Não foi possível obter os dados do computador selecionado.",
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um computador para editar.", "Aviso",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        // Listener do botão Excluir
        deleteButton.addActionListener(e -> {
            int selectedRowView = table.getSelectedRow();
            if (selectedRowView >= 0) {
                int selectedRowModel = table.convertRowIndexToModel(selectedRowView);
                Computer selectedComputer = tableModel.getComputerAt(selectedRowModel);
                if (selectedComputer != null) {
                    // NENHUMA confirmação extra adicionada, mantendo lógica original
                    controller.deleteComputer(selectedComputer, controller.getCurrentUser());
                    controller.refreshComputers();
                    filterTable();
                    // updateComputerCountLabel();
                } else {
                    JOptionPane.showMessageDialog(this, "Não foi possível obter os dados do computador selecionado.",
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um computador para excluir.", "Aviso",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        // Listener do botão Exportar CSV (chama o método original)
        exportButton.addActionListener(e -> handleExportAction(this));

        // Listener do botão Histórico
        historyButton.addActionListener(e -> openHistoryWindow());

        // Listener do botão Gerenciar Usuários
        manageUsersButton.addActionListener(e -> openUserManagementWindow());
    }

    private void filterTable() {
        String query = "";
        // Verifica se searchField já foi inicializado
        if (searchField != null) {
            query = searchField.getText().toLowerCase().trim();
        }

        List<Computer> listToFilter;
        // Pega a lista completa ou já filtrada por local
        // É mais eficiente pegar a lista completa e aplicar ambos os filtros
        List<Computer> fullList = controller.getComputerList();

        // 1. Filtra por Localização (se houver)
        if (currentLocation != null && !currentLocation.isEmpty()) {
            String loc = currentLocation.trim();
            listToFilter = fullList.stream()
                    .filter(computer -> computer.getLocation() != null
                            && computer.getLocation().trim().equalsIgnoreCase(loc))
                    .collect(Collectors.toList());
        } else {
            // Se não há local, começa com todos (fazendo cópia)
            listToFilter = new ArrayList<>(fullList);
        }

        // 2. Filtra por Texto (se houver query) na lista resultante do passo 1
        if (!query.isEmpty()) {
            String finalQuery = query; // Necessário para lambda
            listToFilter = listToFilter.stream().filter(
                    computer -> (computer.getTag() != null && computer.getTag().toLowerCase().contains(finalQuery)) ||
                            (computer.getHostname() != null
                                    && computer.getHostname().toLowerCase().contains(finalQuery))
                            ||
                            (computer.getModel() != null && computer.getModel().toLowerCase().contains(finalQuery)) ||
                            (computer.getBrand() != null && computer.getBrand().toLowerCase().contains(finalQuery)) ||
                            (computer.getUserName() != null
                                    && computer.getUserName().toLowerCase().contains(finalQuery)))
                    .collect(Collectors.toList());
        }

        // Atualiza o modelo da tabela
        if (tableModel != null) {
            tableModel.setComputers(listToFilter);
        }
        // Atualiza a contagem exibida
        updateComputerCountLabel(listToFilter.size());
    }

    // Método auxiliar para atualizar o label de contagem
    private void updateComputerCountLabel(int count) {
        if (computerCountLabel != null) {
            computerCountLabel.setText("Total de Computadores: " + count);
        }
    }

    /**
     * Define o filtro de localidade e atualiza a tabela.
     * (Usando a lógica original que chama refreshComputers)
     */
    public void setLocationFilter(String location) {
        this.currentLocation = (location != null) ? location.trim() : "";
        if (searchField != null) {
            searchField.setText("");
        } // Limpa busca
        controller.refreshComputers(); // Recarrega do DB
        // Recalcula a lista filtrada (apenas por local) e atualiza
        List<Computer> filteredList = controller.getComputersByLocation(currentLocation);
        tableModel.setComputers(filteredList);
        updateComputerCountLabel(filteredList.size()); // Atualiza contagem com base no filtro de local
    }

    // Mantém o handleExportAction original que chama controller.exportToCSV(path)
    private void handleExportAction(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar arquivo CSV");
        String suggestedName = "inventario" +
                (currentLocation != null && !currentLocation.isEmpty() ? "_" + currentLocation : "") +
                ".csv";
        fileChooser.setSelectedFile(new File(suggestedName));

        int userSelection = fileChooser.showSaveDialog(parent);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            // Garante extensão .csv
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getParentFile(), file.getName() + ".csv");
            }

            final File finalFile = file;

            // SwingWorker para rodar a exportação em background
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    // Obtém a lista correta para exportar
                    List<Computer> listToExport;
                    if (currentLocation != null && !currentLocation.isEmpty()) {
                        listToExport = controller.getComputersByLocation(currentLocation);
                    } else {
                        listToExport = controller.getComputerList();
                    }

                    // Chama o controller passando a lista filtrada
                    controller.exportToCSV(listToExport, finalFile.getAbsolutePath());
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get(); // Verifica se houve exceção
                        JOptionPane.showMessageDialog(parent, "Dados exportados para " + finalFile.getName(), "Sucesso",
                                JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(parent, "Não foi possível exportar os dados: " + ex.getMessage(),
                                "Erro", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            };

            worker.execute();
        }
    }

    // Métodos handleBackupAction e handleRestoreAction foram REMOVIDOS

    // Mantém os métodos originais para abrir janelas auxiliares (usando JFrame)
    private void openHistoryWindow() {
        HistoryWindow historyWindow = new HistoryWindow(controller);
        historyWindow.showHistory(); // Assume que este método cria e exibe o JFrame
    }

    private void openUserManagementWindow() {
        // Código original para abrir janela de gerenciamento de usuários
        JFrame userManagementFrame = new JFrame("Gerenciar Usuários");
        userManagementFrame.setSize(300, 300);
        userManagementFrame.setLocationRelativeTo(this.mainApp); // Relativo à janela principal
        userManagementFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        try {
            controller.getUsers().stream()
                    .map(user -> user.getUsername())
                    .sorted() // Ordena
                    .forEach(listModel::addElement);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar usuários: " + e.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return; // Não abre a janela se falhar
        }

        JList<String> userList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(userList);
        scrollPane.setPreferredSize(new Dimension(250, 150));

        JButton editPasswordButton = new JButton("Editar Senha");
        JButton deleteUserButton = new JButton("Excluir Usuário");

        editPasswordButton.addActionListener(e -> {
            String selectedUser = userList.getSelectedValue();
            if (selectedUser != null) {
                openEditPasswordWindow(selectedUser); // Chama método original
            } else {
                JOptionPane.showMessageDialog(userManagementFrame, "Selecione um usuário.", "Aviso",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        deleteUserButton.addActionListener(e -> {
            String selectedUser = userList.getSelectedValue();
            if (selectedUser != null) {
                // Sem confirmação extra
                try {
                    controller.deleteUser(selectedUser);
                    listModel.removeElement(selectedUser); // Remove da lista visível
                    JOptionPane.showMessageDialog(userManagementFrame, "Usuário excluído com sucesso.", "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(userManagementFrame, "Erro ao excluir usuário: " + ex.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(userManagementFrame, "Selecione um usuário.", "Aviso",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        // Layout original (talvez ajustar para melhor aparência)
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(scrollPane);
        panel.add(Box.createVerticalStrut(10));
        panel.add(editPasswordButton);
        panel.add(Box.createVerticalStrut(10)); // Ajustado espaçamento
        panel.add(deleteUserButton);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Centraliza botões
        editPasswordButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteUserButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        userManagementFrame.getContentPane().add(panel);
        userManagementFrame.setVisible(true);
    }

    private void openEditPasswordWindow(String username) {
        // Código original para abrir janela de edição de senha
        JFrame editPasswordFrame = new JFrame("Editar Senha para " + username);
        editPasswordFrame.setSize(300, 150);
        editPasswordFrame.setLocationRelativeTo(this.mainApp); // Relativo à janela principal
        editPasswordFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPasswordField newPasswordField = new JPasswordField(20);
        newPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, newPasswordField.getPreferredSize().height));
        JButton saveButton = new JButton("Salvar");
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(new JLabel("Nova Senha:"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(newPasswordField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(saveButton);

        editPasswordFrame.getContentPane().add(panel);
        editPasswordFrame.setVisible(true);

        saveButton.addActionListener(e -> {
            String newPassword = new String(newPasswordField.getPassword());
            if (newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(editPasswordFrame, "A senha não pode estar vazia.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    controller.editUserPassword(username, newPassword);
                    JOptionPane.showMessageDialog(editPasswordFrame, "Senha atualizada com sucesso.", "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);
                    editPasswordFrame.dispose(); // Fecha janela
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(editPasswordFrame, "Erro ao atualizar senha: " + ex.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // Renderizador de botão para a tabela
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(UIManager.getColor("Button.background"));
            }
            setText((value == null) ? "Ver/Editar" : value.toString());
            return this;
        }
    }

    // Editor de botão para a tabela
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private Computer currentComputer;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(table.getBackground());
            }
            label = (value == null) ? "Ver/Editar" : value.toString();
            button.setText(label);
            isPushed = true;

            // Obtém o computador correspondente à linha
            int modelRow = table.convertRowIndexToModel(row);
            currentComputer = tableModel.getComputerAt(modelRow);

            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                // Abre o dialog de observação
                openObservationDialog(currentComputer);
            }
            isPushed = false;
            return label;
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    private void openObservationDialog(Computer computer) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Observações - " + computer.getTag(), true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JTextArea textArea = new JTextArea(computer.getObservation());
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JButton saveButton = new JButton("Salvar");
        saveButton.addActionListener(e -> {
            String newObservation = textArea.getText();
            computer.setObservation(newObservation);
            if (controller.updateComputer(computer, controller.getCurrentUser())) {
                // Atualiza na tabela visualmente (embora o modelo já tenha o objeto atualizado)
                // tableModel.fireTableDataChanged(); // Não é estritamente necessário se o
                // objeto é o mesmo, mas bom para garantir
                JOptionPane.showMessageDialog(dialog, "Observação salva com sucesso!", "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Erro ao salvar observação.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

}