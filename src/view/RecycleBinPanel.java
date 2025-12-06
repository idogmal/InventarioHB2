package view;

import controller.InventoryController;
import model.Computer;
import javax.swing.*;
import java.awt.*;

public class RecycleBinPanel extends JPanel {

    private InventoryController controller;
    private JTable recycleBinTable;
    private ComputerTableModel recycleBinModel;

    public RecycleBinPanel(MainApp mainApp, InventoryController controller) {

        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Modelo da tabela para a lixeira (sem edição)
        recycleBinModel = new ComputerTableModel(controller.getDeletedComputers());
        recycleBinTable = new JTable(recycleBinModel);
        recycleBinTable.getTableHeader().setFont(recycleBinTable.getTableHeader().getFont().deriveFont(Font.BOLD));
        recycleBinTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recycleBinTable.setAutoCreateRowSorter(true);
        recycleBinTable.setShowGrid(true);
        recycleBinTable.setShowVerticalLines(true);
        recycleBinTable.setShowHorizontalLines(true);

        // Renderers customizados se necessário, mas para lixeira o padrão serve por
        // enquanto
        // Se precisar dos mesmos rendereres da tabela principal, teríamos que duplicar
        // ou compartilhar lógica.

        JScrollPane scrollPane = new JScrollPane(recycleBinTable);
        add(scrollPane, BorderLayout.CENTER);

        JButton restoreButton = new JButton("Restaurar");
        restoreButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        restoreButton.setBackground(new Color(40, 167, 69)); // Bootstap Success Green style
        restoreButton.setForeground(Color.WHITE);

        restoreButton.addActionListener(e -> restoreSelectedComputer());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        buttonPanel.add(restoreButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void restoreSelectedComputer() {
        int selectedRowView = recycleBinTable.getSelectedRow();
        if (selectedRowView >= 0) {
            int selectedRowModel = recycleBinTable.convertRowIndexToModel(selectedRowView);
            Computer selectedComputer = recycleBinModel.getComputerAt(selectedRowModel);
            if (selectedComputer != null) {
                controller.restoreComputer(selectedComputer, controller.getCurrentUser());

                // Atualiza a tabela da lixeira
                recycleBinModel.setComputers(controller.getDeletedComputers());

                JOptionPane.showMessageDialog(this, "Computador restaurado com sucesso!", "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um computador para restaurar.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
}
