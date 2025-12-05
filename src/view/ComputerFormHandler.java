package view;

import controller.InventoryController;
import model.Computer;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;

public class ComputerFormHandler {

    private final InventoryController controller;

    public ComputerFormHandler(InventoryController controller) {
        this.controller = controller;
    }

    public void openForm(Computer computer, String currentUser, String defaultLocation) {
        // Cria um JDialog modal
        JDialog dialog = new JDialog((Frame) null, computer == null ? "Cadastrar Computador" : "Editar Computador",
                true);
        // Configuração inicial do dialog
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Criação dos campos de formulário
        JTextField tagField = new JTextField(20);
        JTextField hostnameField = new JTextField(20); // Nome do PC
        JTextField userField = new JTextField(20);
        JTextField sectorField = new JTextField(20); // Setor
        JTextField patrimonyField = new JTextField(20); // Patrimônio
        JTextField modelField = new JTextField(20);
        JTextField serialField = new JTextField(20);
        JTextField windowsField = new JTextField(20);
        JTextField officeField = new JTextField(20);
        // Para o campo de localização, utiliza JComboBox
        // Para o campo de localização, utiliza JComboBox
        JComboBox<String> locationComboBox = new JComboBox<>();
        loadLocations(locationComboBox);
        locationComboBox.setRenderer(new CompanyListCellRenderer());

        // Adiciona MouseListener ao popup do JComboBox para detectar cliques no "X"
        locationComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                JComboBox<?> combo = (JComboBox<?>) e.getSource();
                Object child = combo.getUI().getAccessibleChild(combo, 0);
                if (child instanceof JPopupMenu) {
                    JScrollPane scrollPane = (JScrollPane) ((JPopupMenu) child).getComponent(0);
                    JList<?> list = (JList<?>) scrollPane.getViewport().getView();

                    // Remove listeners anteriores para evitar duplicação
                    for (java.awt.event.MouseListener ml : list.getMouseListeners()) {
                        if (ml instanceof CompanyDeleteMouseListener) {
                            list.removeMouseListener(ml);
                        }
                    }
                    list.addMouseListener(new CompanyDeleteMouseListener(list, locationComboBox, dialog));
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {
            }
        });

        // JPanel locationPanel = new JPanel(new BorderLayout()); // Removido painel
        // auxiliar
        // locationPanel.add(locationComboBox, BorderLayout.CENTER); // Removido
        // locationPanel.add(deleteCompanyButton, BorderLayout.EAST); // Removido

        // Configuração do Campo com Máscara
        JFormattedTextField purchaseFieldTemp = null;
        try {
            MaskFormatter dateMask = new MaskFormatter("##/##/####");
            dateMask.setPlaceholderCharacter('_');
            purchaseFieldTemp = new JFormattedTextField(dateMask);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        final JFormattedTextField purchaseField = purchaseFieldTemp;

        // Adiciona listener para validação em tempo real
        purchaseField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                validateDate();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                validateDate();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                validateDate();
            }

            private void validateDate() {
                String text = purchaseField.getText();
                if (text.contains("_")) {
                    purchaseField.setBackground(Color.WHITE); // Incompleto
                } else {
                    if (isValidDate(text)) {
                        purchaseField.setBackground(Color.WHITE); // Válido
                    } else {
                        purchaseField.setBackground(new Color(255, 200, 200)); // Inválido (Vermelho claro)
                    }
                }
            }
        });

        // Se for edição, preenche os campos com os dados do computador
        if (computer != null) {
            populateFields(computer, tagField, hostnameField, userField, sectorField, patrimonyField,
                    modelField, serialField, windowsField, officeField, locationComboBox, purchaseField);
        } else {

            // Define a localização padrão se fornecida
            if (defaultLocation != null && !defaultLocation.isEmpty()) {
                locationComboBox.setSelectedItem(defaultLocation);
            }

            // Pre-fill TI tag for new computers
            tagField.setText("TI");
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

        // Nome do PC (Hostname)
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Nome do PC:"), gbc);
        gbc.gridx = 1;
        formPanel.add(hostnameField, gbc);

        // Usuário
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Usuário:"), gbc);
        gbc.gridx = 1;
        formPanel.add(userField, gbc);

        // Localização (utiliza JComboBox) - Movido para cá conforme ordem da imagem
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Localização:"), gbc);
        gbc.gridx = 1;
        formPanel.add(locationComboBox, gbc);

        // Setor
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Setor:"), gbc);
        gbc.gridx = 1;
        formPanel.add(sectorField, gbc);

        // Versão do Windows
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Versão S.O:"), gbc);
        gbc.gridx = 1;
        formPanel.add(windowsField, gbc);

        // Versão do Office
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Versão do Office:"), gbc);
        gbc.gridx = 1;
        formPanel.add(officeField, gbc);

        // Modelo
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Modelo:"), gbc);
        gbc.gridx = 1;
        formPanel.add(modelField, gbc);

        // Número de Série
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Número de Série:"), gbc);
        gbc.gridx = 1;
        formPanel.add(serialField, gbc);

        // Data de Compra
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Data de Compra:"), gbc);
        gbc.gridx = 1;
        formPanel.add(purchaseField, gbc);

        // Patrimônio
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Patrimônio:"), gbc);
        gbc.gridx = 1;
        formPanel.add(patrimonyField, gbc);

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

            String dateText = purchaseField.getText();
            if (!isValidDate(dateText)) {
                showAlert("Erro", "Data inválida. Use o formato dd/MM/yyyy e certifique-se que a data existe.");
                return;
            }

            if (computer == null) {
                // Adiciona um novo computador
                Computer newComputer = new Computer(
                        tagField.getText(),
                        modelField.getText(),
                        "", // Brand (removido)
                        "", // State (removido)
                        userField.getText(),
                        serialField.getText(),
                        windowsField.getText(),
                        officeField.getText(),
                        dateText,
                        (String) locationComboBox.getSelectedItem(),
                        "", // Observation (inicialmente vazio)
                        hostnameField.getText(),
                        sectorField.getText(),
                        patrimonyField.getText());
                controller.addComputer(newComputer, currentUser);
                System.out.println("Novo computador cadastrado: " + newComputer);
            } else {
                // Atualiza o computador existente
                // Preserva brand e state originais
                Computer updatedComputer = new Computer(
                        tagField.getText(),
                        modelField.getText(),
                        computer.getBrand(), // Preserva
                        computer.getState(), // Preserva
                        userField.getText(),
                        serialField.getText(),
                        windowsField.getText(),
                        officeField.getText(),
                        dateText,
                        (String) locationComboBox.getSelectedItem(),
                        computer.getObservation(), // Preserva observação
                        hostnameField.getText(),
                        sectorField.getText(),
                        patrimonyField.getText());
                controller.editComputer(computer, updatedComputer, currentUser);
                System.out.println("Computador atualizado: " + updatedComputer);
            }
            dialog.dispose();
            dialog.dispose();
        });

        // Ação do ComboBox para detectar seleção de "+"
        locationComboBox.addActionListener(e -> {
            String selected = (String) locationComboBox.getSelectedItem();
            if ("+".equals(selected)) {
                String newCompany = JOptionPane.showInputDialog(dialog, "Nome da nova empresa:", "Cadastrar Empresa",
                        JOptionPane.PLAIN_MESSAGE);
                if (newCompany != null && !newCompany.trim().isEmpty()) {
                    if (controller.addCompany(newCompany.trim().toUpperCase())) {
                        loadLocations(locationComboBox);
                        locationComboBox.setSelectedItem(newCompany.trim().toUpperCase());
                    } else {
                        showAlert("Erro", "Não foi possível cadastrar a empresa. Verifique se já existe.");
                        locationComboBox.setSelectedIndex(0); // Volta para o primeiro item
                    }
                } else {
                    locationComboBox.setSelectedIndex(0); // Cancelou ou vazio
                }
            }
        });

        dialog.pack(); // Ajusta o tamanho do dialog ao conteúdo
        dialog.setLocationRelativeTo(null); // Centraliza na tela
        dialog.setVisible(true);
    }

    private void populateFields(Computer computer, JTextField tagField, JTextField hostnameField, JTextField userField,
            JTextField sectorField, JTextField patrimonyField, JTextField modelField, JTextField serialField,
            JTextField windowsField, JTextField officeField, JComboBox<String> locationComboBox,
            JFormattedTextField purchaseField) {
        tagField.setText(computer.getTag());
        hostnameField.setText(computer.getHostname());
        userField.setText(computer.getUserName());
        sectorField.setText(computer.getSector());
        patrimonyField.setText(computer.getPatrimony());
        modelField.setText(computer.getModel());
        serialField.setText(computer.getSerialNumber());
        windowsField.setText(computer.getWindowsVersion());
        officeField.setText(computer.getOfficeVersion());
        // Seleciona a opção correspondente à localização
        String loc = computer.getLocation();
        if (loc != null) {
            locationComboBox.setSelectedItem(loc);
        }
        if (computer.getPurchaseDate() != null && !computer.getPurchaseDate().isEmpty()) {
            purchaseField.setText(computer.getPurchaseDate());
        } else {
            // Se vazio, pode definir a data atual ou deixar em branco (máscara)
            // purchaseField.setValue(new java.util.Date()); // Se quiser preencher com hoje
            // Deixando vazio (com máscara)
            purchaseField.setText("");
        }
    }

    private boolean isValidDate(String dateStr) {
        if (dateStr == null || dateStr.contains("_"))
            return false; // Incompleto
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            sdf.parse(dateStr);
            return true;
        } catch (java.text.ParseException e) {
            return false;
        }
    }

    private void loadLocations(JComboBox<String> comboBox) {
        comboBox.removeAllItems();
        java.util.List<String> companies = controller.getCompanies();
        for (String company : companies) {
            comboBox.addItem(company);
        }
        comboBox.addItem("+"); // Opção para adicionar nova
    }

    private void showAlert(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    // Custom Renderer para exibir o "X" de exclusão
    private static class CompanyListCellRenderer extends JPanel implements ListCellRenderer<String> {
        private final JLabel nameLabel = new JLabel();
        private final JLabel deleteLabel = new JLabel("X");

        public CompanyListCellRenderer() {
            setLayout(new BorderLayout());
            setOpaque(true);
            add(nameLabel, BorderLayout.CENTER);
            deleteLabel.setForeground(Color.RED);
            deleteLabel.setFont(deleteLabel.getFont().deriveFont(Font.BOLD));
            deleteLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            add(deleteLabel, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
                boolean isSelected, boolean cellHasFocus) {
            nameLabel.setText(value);

            // Configura cores
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
                nameLabel.setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
                nameLabel.setForeground(list.getForeground());
            }

            // Estilização especial para o item "+"
            if ("+".equals(value)) {
                nameLabel.setForeground(Color.GRAY); // Deixa o "+" mais claro (cinza)
                nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD)); // Opcional: negrito
                deleteLabel.setVisible(false);
            } else {
                // Reseta para o padrão (necessário pois o renderer é reutilizado)
                if (!isSelected) {
                    nameLabel.setForeground(list.getForeground());
                }
                nameLabel.setFont(list.getFont());

                // Mostra o "X" apenas se não for null (item vazio) e se estiver na lista (index
                // >= 0)
                // index -1 indica que é o item selecionado exibido no próprio combo box
                if (value == null || index == -1) {
                    deleteLabel.setVisible(false);
                } else {
                    deleteLabel.setVisible(true);
                }
            }

            return this;
        }
    }

    // Listener para tratar cliques no "X" dentro da lista
    private class CompanyDeleteMouseListener extends java.awt.event.MouseAdapter {
        private final JList<?> list;
        private final JComboBox<String> comboBox;
        private final JDialog parentDialog;

        public CompanyDeleteMouseListener(JList<?> list, JComboBox<String> comboBox, JDialog parentDialog) {
            this.list = list;
            this.comboBox = comboBox;
            this.parentDialog = parentDialog;
        }

        @Override
        public void mouseReleased(java.awt.event.MouseEvent e) {
            Point p = e.getPoint();
            int index = list.locationToIndex(p);
            if (index >= 0) {
                String value = (String) list.getModel().getElementAt(index);
                if ("+".equals(value))
                    return;

                // Verifica se o clique foi na área do "X" (lado direito)
                Rectangle cellBounds = list.getCellBounds(index, index);
                if (cellBounds != null && cellBounds.contains(p)) {
                    // Assume que o "X" ocupa os últimos 30 pixels
                    if (p.x > cellBounds.x + cellBounds.width - 30) {
                        e.consume(); // Tenta impedir a seleção do item

                        int confirm = JOptionPane.showConfirmDialog(parentDialog,
                                "Tem certeza que deseja excluir a empresa '" + value + "'?",
                                "Excluir Empresa", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            if (controller.deleteCompany(value)) {
                                loadLocations(comboBox);
                                // Fecha o popup para atualizar visualmente
                                comboBox.setPopupVisible(false);
                            } else {
                                showAlert("Erro", "Não foi possível excluir a empresa.");
                            }
                        }
                    }
                }
            }
        }
    }
}
