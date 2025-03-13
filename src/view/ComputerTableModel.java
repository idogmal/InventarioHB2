package view;

import controller.InventoryController;
import model.Computer;
import model.HistoryEntry;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class ComputerTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Etiqueta TI", "Modelo", "Marca", "Estado", "Usuário", "Número de Série", "Versão do Windows", "Versão do Office", "Localização", "Data de Compra"};
    private List<Computer> computers;
    private final InventoryController controller;

    public ComputerTableModel(List<Computer> computers, InventoryController controller) {
        this.computers = computers;
        this.controller = controller;
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
        return true; // Permite edição direta; ajuste se necessário
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Computer c = computers.get(rowIndex);
        String newValue = aValue.toString();
        String title = columnNames[columnIndex];
        switch (columnIndex) {
            case 0: c.setTag(newValue); break;
            case 1: c.setModel(newValue); break;
            case 2: c.setBrand(newValue); break;
            case 3: c.setState(newValue); break;
            case 4: c.setUserName(newValue); break;
            case 5: c.setSerialNumber(newValue); break;
            case 6: c.setWindowsVersion(newValue); break;
            case 7: c.setOfficeVersion(newValue); break;
            case 8: c.setLocation(newValue); break;
            case 9: c.setPurchaseDate(newValue); break;
        }
        if (controller != null) {
            controller.addHistory(HistoryEntry.ActionType.EDITAR, controller.getCurrentUser(),
                    String.format("%s alterado para %s", title, newValue));
        } else {
            System.err.println("Erro: Controlador não inicializado.");
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    public void setComputers(List<Computer> computers) {
        this.computers = computers;
        fireTableDataChanged();
    }

    public Computer getComputerAt(int rowIndex) {
        return computers.get(rowIndex);
    }
}
