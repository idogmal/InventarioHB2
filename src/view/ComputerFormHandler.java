package view;

import controller.InventoryController;
import model.Computer;

import javax.swing.*;
import java.awt.*;

public class ComputerFormHandler {

    private final InventoryController controller;

    public ComputerFormHandler(InventoryController controller) {
        this.controller = controller;
    }

    public void openForm(Computer computer, String currentUser) {
        // Cria um JDialog modal
        JDialog dialog = new JDialog((Frame) null, computer == null ? "Cadastrar Computador" : "Editar Computador", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(null);

        // Criação dos campos de formulário
        JTextField tagField = new JTextField(20);
        JTextField modelField = new JTextField(20);
        JTextField brandField = new JTextField(20);
        JTextField stateField = new JTextField(20);
        JTextField userField = new JTextField(20);
        JTextField serialField = new JTextField(20);
        JTextField windowsField = new JTextField(20);
        JTextField officeField = new JTextField(20);
        JTextField locationField = new JTextField(20);
        JTextField purchaseField = new JTextField(20);

        // Se for edição, preenche os campos com os dados do computador
        if (computer != null) {
            populateFields(computer, tagField, modelField, brandField, stateField, userField,
                    serialField, windowsField, officeField, locationField, purchaseField);
        } else if (currentUser != null) {
            userField.setText(currentUser); // Preenche automaticamente o campo usuário
        }

        // Cria um painel com GridLayout para os rótulos e campos
        String[] labels = {"Etiqueta TI:", "Modelo:", "Marca:", "Estado:", "Usuário:", "Número de Série:",
                "Versão do Windows:", "Versão do Office:", "Localização:", "Data de Compra:"};
        JTextField[] fields = {tagField, modelField, brandField, stateField, userField, serialField,
                windowsField, officeField, locationField, purchaseField};

        JPanel formPanel = new JPanel(new GridLayout(10, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        for (int i = 0; i < labels.length; i++) {
            formPanel.add(new JLabel(labels[i]));
            formPanel.add(fields[i]);
        }

        // Botão de salvar
        JButton saveButton = new JButton("Salvar");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);

        // Painel principal para agrupar o formulário e o botão
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.getContentPane().add(mainPanel);

        // Ação do botão salvar
        saveButton.addActionListener(e -> {
            if (tagField.getText().trim().isEmpty() || userField.getText().trim().isEmpty()) {
                showAlert("Erro", "Os campos 'Etiqueta TI' e 'Usuário' não podem estar vazios.");
                return;
            }
            if (computer == null) {
                // Adiciona um novo computador
                Computer newComputer = new Computer(
                        tagField.getText(),
                        modelField.getText(),
                        brandField.getText(),
                        stateField.getText(),
                        userField.getText(),
                        serialField.getText(),
                        windowsField.getText(),
                        officeField.getText(),
                        locationField.getText(),
                        purchaseField.getText()
                );
                controller.addComputer(newComputer, currentUser);
                System.out.println("Novo computador cadastrado: " + newComputer);
            } else {
                // Atualiza o computador existente
                Computer updatedComputer = new Computer(
                        tagField.getText(),
                        modelField.getText(),
                        brandField.getText(),
                        stateField.getText(),
                        userField.getText(),
                        serialField.getText(),
                        windowsField.getText(),
                        officeField.getText(),
                        locationField.getText(),
                        purchaseField.getText()
                );
                controller.editComputer(computer, updatedComputer, currentUser);
                System.out.println("Computador atualizado: " + updatedComputer);
            }
            dialog.dispose();
        });

        dialog.setVisible(true);
    }

    private void populateFields(Computer computer, JTextField... fields) {
        fields[0].setText(computer.getTag());
        fields[1].setText(computer.getModel());
        fields[2].setText(computer.getBrand());
        fields[3].setText(computer.getState());
        fields[4].setText(computer.getUserName());
        fields[5].setText(computer.getSerialNumber());
        fields[6].setText(computer.getWindowsVersion());
        fields[7].setText(computer.getOfficeVersion());
        fields[8].setText(computer.getLocation());
        fields[9].setText(computer.getPurchaseDate());
    }

    private void showAlert(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
