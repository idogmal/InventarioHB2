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
        dialog.setSize(400, 450);
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
        // Para o campo de localização, utiliza JComboBox
        String[] locations = {"NPD", "INFAN"};
        JComboBox<String> locationComboBox = new JComboBox<>(locations);
        JTextField purchaseField = new JTextField(20);

        // Se for edição, preenche os campos com os dados do computador
        if (computer != null) {
            populateFields(computer, tagField, modelField, brandField, stateField, userField,
                    serialField, windowsField, officeField, locationComboBox, purchaseField);
        } else if (currentUser != null) {
            userField.setText(currentUser); // Preenche automaticamente o campo usuário
        }

        // Cria um painel com GridBagLayout para organizar os rótulos e campos
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        // Etiqueta TI
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Etiqueta TI:"), gbc);
        gbc.gridx = 1;
        formPanel.add(tagField, gbc);

        // Modelo
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Modelo:"), gbc);
        gbc.gridx = 1;
        formPanel.add(modelField, gbc);

        // Marca
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Marca:"), gbc);
        gbc.gridx = 1;
        formPanel.add(brandField, gbc);

        // Estado
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1;
        formPanel.add(stateField, gbc);

        // Usuário
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Usuário:"), gbc);
        gbc.gridx = 1;
        formPanel.add(userField, gbc);

        // Número de Série
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Número de Série:"), gbc);
        gbc.gridx = 1;
        formPanel.add(serialField, gbc);

        // Versão do Windows
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Versão do Windows:"), gbc);
        gbc.gridx = 1;
        formPanel.add(windowsField, gbc);

        // Versão do Office
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Versão do Office:"), gbc);
        gbc.gridx = 1;
        formPanel.add(officeField, gbc);

        // Localização (utiliza JComboBox)
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Localização:"), gbc);
        gbc.gridx = 1;
        formPanel.add(locationComboBox, gbc);

        // Data de Compra
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Data de Compra:"), gbc);
        gbc.gridx = 1;
        formPanel.add(purchaseField, gbc);

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
                // Adiciona um novo computador, utilizando o valor selecionado do JComboBox
                Computer newComputer = new Computer(
                        tagField.getText(),
                        modelField.getText(),
                        brandField.getText(),
                        stateField.getText(),
                        userField.getText(),
                        serialField.getText(),
                        windowsField.getText(),
                        officeField.getText(),
                        (String) locationComboBox.getSelectedItem(),
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
                        (String) locationComboBox.getSelectedItem(),
                        purchaseField.getText()
                );
                controller.editComputer(computer, updatedComputer, currentUser);
                System.out.println("Computador atualizado: " + updatedComputer);
            }
            dialog.dispose();
        });

        dialog.setVisible(true);
    }

    private void populateFields(Computer computer, JTextField tagField, JTextField modelField, JTextField brandField,
                                JTextField stateField, JTextField userField, JTextField serialField,
                                JTextField windowsField, JTextField officeField, JComboBox<String> locationComboBox,
                                JTextField purchaseField) {
        tagField.setText(computer.getTag());
        modelField.setText(computer.getModel());
        brandField.setText(computer.getBrand());
        stateField.setText(computer.getState());
        userField.setText(computer.getUserName());
        serialField.setText(computer.getSerialNumber());
        windowsField.setText(computer.getWindowsVersion());
        officeField.setText(computer.getOfficeVersion());
        // Seleciona a opção correspondente à localização
        String loc = computer.getLocation();
        if (loc != null) {
            locationComboBox.setSelectedItem(loc);
        }
        purchaseField.setText(computer.getPurchaseDate());
    }

    private void showAlert(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
