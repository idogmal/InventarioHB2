package view;

import controller.InventoryController;
import model.HistoryEntry;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class HistoryWindow {

    private final InventoryController controller;

    public HistoryWindow(InventoryController controller) {
        this.controller = controller;
    }

    public void showHistory() {
        JFrame historyFrame = new JFrame("Histórico de Alterações");
        historyFrame.setSize(600, 400);
        historyFrame.setLocationRelativeTo(null);
        historyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] columnNames = { "Ação", "Usuário", "Data e Hora", "Descrição" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        // Recarrega o histórico do banco de dados para garantir dados atualizados
        controller.refreshHistory();

        // Obtém a lista e ordena por data (mais recente primeiro)
        java.util.List<HistoryEntry> sortedList = new java.util.ArrayList<>(controller.getHistoryList());
        sortedList.sort((h1, h2) -> h2.getTimestamp().compareTo(h1.getTimestamp()));

        // Preencher o modelo com os dados do histórico
        for (HistoryEntry entry : sortedList) {
            String formattedTimestamp = entry.getTimestamp().format(formatter);
            Object[] row = { entry.getAction(), entry.getUser(), formattedTimestamp, entry.getDescription() };
            tableModel.addRow(row);
        }

        JTable historyTable = new JTable(tableModel);
        historyTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(historyTable);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(scrollPane, BorderLayout.CENTER);

        historyFrame.getContentPane().add(panel);
        historyFrame.setVisible(true);
    }
}
