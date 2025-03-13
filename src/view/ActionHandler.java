package view;

import controller.InventoryController;
import model.Computer;
import model.HistoryEntry;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class ActionHandler {

    private final InventoryController controller;
    private final JTable table;
    private final ComputerFormHandler formHandler; // Versão Swing do manipulador de formulários

    public ActionHandler(InventoryController controller, JTable table) {
        this.controller = controller;
        this.table = table;
        this.formHandler = new ComputerFormHandler(controller); // Assume que também foi convertido para Swing
    }

    public void handleExportAction() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar arquivo CSV");
        int userSelection = fileChooser.showSaveDialog(table);
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

    public void handleEditAction() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            // Assume que o modelo da tabela possui o método getComputerAt(int)
            Computer selectedComputer = ((ComputerTableModel) table.getModel()).getComputerAt(selectedRow);
            formHandler.openForm(selectedComputer, controller.getCurrentUser());
        } else {
            showAlert("Seleção necessária", "Por favor, selecione um computador para editar.");
        }
    }

    public void openHistoryWindow() {
        JFrame historyFrame = new JFrame("Histórico de Alterações");
        historyFrame.setSize(600, 400);
        historyFrame.setLocationRelativeTo(null);

        String[] columnNames = {"Ação", "Usuário", "Data e Hora", "Descrição"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        for (HistoryEntry entry : controller.getHistoryList()) {
            String formattedTimestamp = entry.getTimestamp().format(formatter);
            Object[] row = {entry.getAction(), entry.getUser(), formattedTimestamp, entry.getDescription()};
            model.addRow(row);
        }

        JTable historyTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(historyTable);

        JPanel layout = new JPanel(new BorderLayout());
        layout.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        layout.add(scrollPane, BorderLayout.CENTER);

        historyFrame.getContentPane().add(layout);
        historyFrame.setVisible(true);
    }

    public void handleDeleteAction() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            Computer selectedComputer = ((ComputerTableModel) table.getModel()).getComputerAt(selectedRow);
            controller.deleteComputer(selectedComputer, controller.getCurrentUser());
        } else {
            showAlert("Seleção necessária", "Por favor, selecione um computador para excluir.");
        }
    }

    public void handleBackupAction() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Backup");
        int userSelection = fileChooser.showSaveDialog(table);
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

    public void handleRestoreAction() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Restaurar Backup");
        int userSelection = fileChooser.showOpenDialog(table);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                controller.restoreData(file.getAbsolutePath());
                // Atualiza o modelo da tabela com a nova lista de computadores
                ((ComputerTableModel) table.getModel()).setComputers(controller.getComputerList());
                table.repaint();
                showAlert("Sucesso", "Dados restaurados com sucesso de " + file.getName());
            } catch (IOException ex) {
                showAlert("Erro", "Falha ao restaurar os dados.");
                ex.printStackTrace();
            }
        }
    }

    private void showAlert(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
