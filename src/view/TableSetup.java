package view;

import controller.InventoryController;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import model.Computer;
import model.HistoryEntry;

public class TableSetup {

    private final InventoryController controller;

    public TableSetup(InventoryController controller) {
        this.controller = controller;
    }

    public TableView<Computer> createTable(ObservableList<Computer> computerList) {
        TableView<Computer> table = new TableView<>();
        table.setEditable(true);
        table.setItems(computerList);

        // Criar todas as colunas
        TableColumn<Computer, String> tagColumn = createColumn("Etiqueta TI", "tag");
        TableColumn<Computer, String> modelColumn = createColumn("Modelo", "model");
        TableColumn<Computer, String> brandColumn = createColumn("Marca", "brand");
        TableColumn<Computer, String> stateColumn = createColumn("Estado", "state");
        TableColumn<Computer, String> userColumn = createColumn("Usuário", "userName");
        TableColumn<Computer, String> serialColumn = createColumn("Número de Série", "serialNumber");
        TableColumn<Computer, String> windowsColumn = createColumn("Versão do Windows", "windowsVersion");
        TableColumn<Computer, String> officeColumn = createColumn("Versão do Office", "officeVersion");
        TableColumn<Computer, String> locationColumn = createColumn("Localização", "location");
        TableColumn<Computer, String> purchaseColumn = createColumn("Data de Compra", "purchaseDate");

        // Adicionar colunas à tabela
        table.getColumns().addAll(tagColumn, modelColumn, brandColumn, stateColumn, userColumn,
                serialColumn, windowsColumn, officeColumn, locationColumn, purchaseColumn);

        return table;
    }

    private TableColumn<Computer, String> createColumn(String title, String property) {
        TableColumn<Computer, String> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit(event -> {
            String newValue = event.getNewValue();

            // Atualizar o valor na linha usando um switch
            switch (property) {
                case "tag":
                    event.getRowValue().setTag(newValue);
                    break;
                case "model":
                    event.getRowValue().setModel(newValue);
                    break;
                case "brand":
                    event.getRowValue().setBrand(newValue);
                    break;
                case "state":
                    event.getRowValue().setState(newValue);
                    break;
                case "userName":
                    event.getRowValue().setUserName(newValue);
                    break;
                case "serialNumber":
                    event.getRowValue().setSerialNumber(newValue);
                    break;
                case "windowsVersion":
                    event.getRowValue().setWindowsVersion(newValue);
                    break;
                case "officeVersion":
                    event.getRowValue().setOfficeVersion(newValue);
                    break;
                case "location":
                    event.getRowValue().setLocation(newValue);
                    break;
                case "purchaseDate":
                    event.getRowValue().setPurchaseDate(newValue);
                    break;
            }

            // Verificar se o método `addHistory` está acessível
            if (controller != null) {
                controller.addHistory(HistoryEntry.ActionType.EDITAR, controller.getCurrentUser(),
                        String.format("%s alterado para %s", title, newValue));
            } else {
                System.err.println("Erro: Controlador não inicializado.");
            }
        });
        return column;
    }
}
