package view;

import controller.InventoryController;

import model.Computer;
import javax.swing.*;

import java.awt.*;
import java.io.File;
import javax.swing.table.DefaultTableCellRenderer;

import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;

public class InventoryPanel extends JPanel {
    private MainApp mainApp;
    private InventoryController controller;
    private JTable table;
    private ComputerTableModel tableModel;

    private String currentLocation = ""; // Armazena o local atual
    private String statusFilter = "ALL"; // ALL, Ativo, Inativo
    private StatsListener statsListener;

    public interface StatsListener {
        void onStatsChange(int total, int active, int inactive);
    }

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
        updateStats(initialList); // Calcula e emite estatísticas
    }

    private void initComponents() {
        // Layout principal: Bordas com espaçamento
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // --- Painel superior antigo removido ---
        // A busca e contagem agora estão no TopBarPanel

        // --- Tabela (Central) ---

        table = new JTable(tableModel);
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD)); // Cabeçalho em negrito
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true); // Habilita ordenação

        table.setFillsViewportHeight(true); // Ocupa altura disponível
        table.setShowGrid(true); // Habilita grades
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);

        // Configura o renderer da primeira coluna (Etiqueta TI) para mostrar o status
        table.getColumnModel().getColumn(0).setCellRenderer(new StatusCellRenderer());

        // Configura o renderizador e editor para a coluna de observações (índice 12 -
        // OBS)
        table.getColumn("OBS").setCellRenderer(new ButtonRenderer());
        table.getColumn("OBS").setCellEditor(new ButtonEditor(new JCheckBox()));

        // Configura tooltip para a coluna "TEMPO DE USO" (índice 10)
        table.getColumn("TEMPO DE USO").setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (c instanceof JComponent) {
                    int modelRow = table.convertRowIndexToModel(row);
                    Computer computer = tableModel.getComputerAt(modelRow);
                    ((JComponent) c).setToolTipText(computer.getDetailedUsageTime());
                }
                return c;
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(table);
        add(tableScrollPane, BorderLayout.CENTER); // Adiciona tabela ao Centro

        // --- Rodapé com Versão ---
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);

        JLabel versionLabel = new JLabel("v" + MainApp.APP_VERSION);
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        versionLabel.setForeground(Color.GRAY);
        versionLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 5)); // Margem direita

        footerPanel.add(versionLabel, BorderLayout.EAST);
        add(footerPanel, BorderLayout.SOUTH);

        // --- Painel de Botões à Esquerda antigo removido ---
        // Botões agora estão no SidebarPanel

        // --- Action Listeners (Removidos, pois os botões foram movidos) ---
        // Os listeners agora serão chamados por métodos públicos.
    }

    // --- Métodos Públicos para o Dashboard (Sidebar/TopBar) ---

    public void openRegisterForm() {
        openForm(null); // Abre formulário vazio para novo cadastro
    }

    public void editSelectedComputer() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            Computer selectedComputer = tableModel.getComputerAt(modelRow);
            openForm(selectedComputer);
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um computador para editar.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    public void deleteSelectedComputer() {
        handleDeleteAction();
    }

    public void exportCSV() {
        handleExportAction(this);
    }

    // Tornando público para acesso externo
    public void openRecycleBinWindow() {
        JDialog recycleBinDialog = new JDialog(mainApp, "Lixeira", true);
        recycleBinDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        recycleBinDialog.setSize(800, 600);
        recycleBinDialog.setLocationRelativeTo(mainApp);

        RecycleBinPanel recycleBinPanel = new RecycleBinPanel(mainApp, controller);
        recycleBinDialog.add(recycleBinPanel);

        recycleBinDialog.setVisible(true);

        // Após fechar a lixeira, atualiza a tabela principal
        controller.refreshComputers();
        filterList(""); // Re-aplica o filtro atual (ou limpa se não houver)
    }

    // Tornando público
    public void openHistoryWindow() {
        new HistoryWindow(controller).showHistory();
    }

    public void filterList(String query) {
        this.lastQuery = (query != null) ? query : "";
        // Implementação da busca vinda do TopBar
        List<Computer> filteredList;
        List<Computer> baseList = (currentLocation != null && !currentLocation.isEmpty())
                ? controller.getComputersByLocation(currentLocation)
                : controller.getComputerList();

        if (query == null) {
            query = "";
        }

        String lowerQuery = query.toLowerCase().trim();

        filteredList = baseList.stream()
                // 1. Filter by Status
                .filter(c -> {
                    if (statusFilter.equals("ALL"))
                        return true;
                    // Normaliza para comparar (Assumindo "Ativo" e "Inativo" no Computer)
                    return c.getActivityStatus() != null && c.getActivityStatus().equalsIgnoreCase(statusFilter);
                })
                // 2. Filter by Search Query
                .filter(c -> {
                    if (lowerQuery.isEmpty())
                        return true;
                    return (c.getTag() != null && c.getTag().toLowerCase().contains(lowerQuery))
                            || (c.getModel() != null && c.getModel().toLowerCase().contains(lowerQuery))
                            || (c.getBrand() != null && c.getBrand().toLowerCase().contains(lowerQuery))
                            || (c.getUserName() != null && c.getUserName().toLowerCase().contains(lowerQuery))
                            || (c.getHostname() != null && c.getHostname().toLowerCase().contains(lowerQuery))
                            || (c.getLocation() != null && c.getLocation().toLowerCase().contains(lowerQuery))
                            || (c.getSector() != null && c.getSector().toLowerCase().contains(lowerQuery))
                            || (c.getWindowsVersion() != null
                                    && c.getWindowsVersion().toLowerCase().contains(lowerQuery))
                            || (c.getOfficeVersion() != null && c.getOfficeVersion().toLowerCase().contains(lowerQuery))
                            || (c.getSerialNumber() != null && c.getSerialNumber().toLowerCase().contains(lowerQuery))
                            || (c.getPurchaseDate() != null && c.getPurchaseDate().toLowerCase().contains(lowerQuery))
                            || (c.getPatrimony() != null && c.getPatrimony().toLowerCase().contains(lowerQuery))
                            || (c.getObservation() != null && c.getObservation().toLowerCase().contains(lowerQuery));
                })
                .collect(Collectors.toList());

        tableModel.setComputers(filteredList);

        // Update stats based on the BASE list (Location only), so buttons show total
        // availability
        updateStats(baseList);
    }

    public void setStatusFilter(String status) {
        this.statusFilter = status;
        // Re-apply filter with current search text
        // (Since we don't store the search text in a field, we might need to store it
        // or accept it cleared.
        // However, keeping the text filter is better UX. Ideally searchField handles
        // text).
        // Fix: TopBar calls filterList, so MainApp should coordinate.
        // Actually, InventoryPanel doesn't "know" the current search text unless
        // stored.
        // Let's store the last query.
        filterList(lastQuery);
    }

    private String lastQuery = ""; // Store last query to re-apply on status change

    // Override filterList to capture query
    // (Wait, I can't override the one I'm editing easily. modifying filterList
    // above to update lastQuery)

    // Let's just fix filterList implementation below.

    // --- Fim Métodos Públicos ---

    // Mantendo métodos privados auxiliares original
    private void openForm(Computer computerToEdit) {
        new ComputerFormHandler(controller).openForm(computerToEdit,
                controller.getCurrentUser(), currentLocation);
        controller.refreshComputers();
        filterList(""); // Re-aplica o filtro atual (ou limpa se não houver)
    }

    private void handleDeleteAction() {
        int selectedRowView = table.getSelectedRow();
        if (selectedRowView >= 0) {
            int selectedRowModel = table.convertRowIndexToModel(selectedRowView);
            Computer selectedComputer = tableModel.getComputerAt(selectedRowModel);
            if (selectedComputer != null) {
                // Confirmação de exclusão
                int option = JOptionPane.showConfirmDialog(this,
                        "Tem certeza que deseja mover o computador " + selectedComputer.getTag()
                                + " para a lixeira?",
                        "Confirmar Exclusão",
                        JOptionPane.YES_NO_OPTION);

                if (option == JOptionPane.YES_OPTION) {
                    controller.deleteComputer(selectedComputer, controller.getCurrentUser());
                    controller.refreshComputers();
                    filterList(""); // Re-aplica o filtro atual (ou limpa se não houver)
                }
            } else {
                JOptionPane.showMessageDialog(this, "Não foi possível obter os dados do computador selecionado.",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um computador para excluir.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    // Atualiza as estatísticas de contagem
    private void updateStats(List<Computer> list) {
        int total = list.size();
        int active = 0;
        int inactive = 0;

        for (Computer c : list) {
            if ("Inativo".equalsIgnoreCase(c.getActivityStatus())) {
                inactive++;
            } else {
                active++;
            }
        }

        // Notifica o listener externo (TopBarPanel)
        if (statsListener != null) {
            statsListener.onStatsChange(total, active, inactive);
        }
    }

    public void setStatsListener(StatsListener listener) {
        this.statsListener = listener;
    }

    /**
     * Define o filtro de localidade e atualiza a tabela.
     * (Usando a lógica original que chama refreshComputers)
     */
    public void setLocationFilter(String location) {
        this.currentLocation = (location != null) ? location.trim() : "";
        // searchField removido
        controller.refreshComputers(); // Recarrega do DB
        // Recalcula a lista filtrada (apenas por local) e atualiza
        List<Computer> filteredList = controller.getComputersByLocation(currentLocation);
        tableModel.setComputers(filteredList);
        updateStats(filteredList); // Atualiza contagem com base no filtro de local
    }

    // Mantém o handleExportAction original que chama controller.exportToCSV(path)
    private void handleExportAction(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exportar para CSV");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File("inventario.csv"));

        int userSelection = fileChooser.showSaveDialog(parent);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getAbsolutePath().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }

            File finalFile = fileToSave;
            // SwingWorker para rodar a exportação em background
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    // Obtém a lista correta para exportar
                    List<Computer> listToExport;
                    if (currentLocation != null && !currentLocation.equals("Todas")) {
                        listToExport = controller.getComputersByLocation(currentLocation);
                    } else {
                        listToExport = controller.getComputerList();
                    }

                    // Se houver texto na busca, filtra essa lista também (opcional, mas bom para
                    // consistência)
                    // Assumindo sem filtro de texto no export por enquanto ou passando via
                    // parametro se necessário
                    // String query = searchField.getText();

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
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(parent, "Não foi possível exportar os dados: " + ex.getMessage(),
                                "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }

    // Renderer para desenhar o indicador de status (Ativo/Inativo)
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Obtém o objeto Computer associado à linha
            int modelRow = table.convertRowIndexToModel(row);
            Computer computer = tableModel.getComputerAt(modelRow);

            if (computer != null) {
                if ("Inativo".equalsIgnoreCase(computer.getActivityStatus())) {
                    setIcon(new StatusIcon(Color.RED));
                    setToolTipText("Inativo");
                } else {
                    setIcon(new StatusIcon(new Color(0, 153, 0))); // Verde escuro
                    setToolTipText("Ativo");
                }
            } else {
                setIcon(null);
            }
            return this;
        }
    }

    // Ícone simples de bolinha
    private static class StatusIcon implements Icon {
        private final Color color;

        public StatusIcon(Color color) {
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(color);
            g.fillOval(x, y + 2, 10, 10); // Centralizado verticalmente aprox
        }

        @Override
        public int getIconWidth() {
            return 12;
        }

        @Override
        public int getIconHeight() {
            return 12;
        }
    }

    // Métodos handleBackupAction e handleRestoreAction foram REMOVIDOS

    // Mantém os métodos originais para abrir janelas auxiliares (usando JFrame)
    // Método privado original renomeado/mantido para implementação

    // Renderizador de botão para a tabela using DefaultTableCellRenderer to
    // preserve grid lines
    class ButtonRenderer extends DefaultTableCellRenderer {
        private final Icon normalIcon = new ModernIcon(ModernIcon.IconType.EYE, 16, Color.DARK_GRAY);
        private final Icon selectedIcon = new ModernIcon(ModernIcon.IconType.EYE, 16, Color.WHITE);

        public ButtonRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
            setToolTipText("Ver Observações");
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            // Let super handle all the standard coloring and opaque settings
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (isSelected) {
                setIcon(selectedIcon);
            } else {
                setIcon(normalIcon);
            }
            setText(""); // Ensure no text is displayed
            return this;
        }
    }

    // Editor de botão para a tabela
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private Computer currentComputer;
        private final Icon normalIcon = new ModernIcon(ModernIcon.IconType.EYE, 16, Color.DARK_GRAY);
        private final Icon selectedIcon = new ModernIcon(ModernIcon.IconType.EYE, 16, Color.WHITE);

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setContentAreaFilled(true);
            button.setBorderPainted(false);
            button.setIcon(normalIcon);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            if (isSelected) {
                button.setBackground(table.getSelectionBackground());
                button.setIcon(selectedIcon);
            } else {
                button.setBackground(table.getBackground());
                button.setIcon(normalIcon);
            }
            label = (value == null) ? "" : value.toString();
            button.setText(""); // No text
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
            return label; // Keep label value in model, just visual is different
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
            String currentObservation = computer.getObservation();

            // Verifica se houve alteração, tratando null como vazio
            String safeCurrent = (currentObservation == null) ? "" : currentObservation;

            if (safeCurrent.equals(newObservation)) {
                dialog.dispose();
                return;
            }

            computer.setObservation(newObservation);
            if (controller.updateComputer(computer, controller.getCurrentUser())) {
                // Atualiza na tabela visualmente (embora o modelo já tenha o objeto atualizado)
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