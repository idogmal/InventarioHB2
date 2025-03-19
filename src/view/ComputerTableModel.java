package view;

import model.Computer;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class ComputerTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Etiqueta TI", "Modelo", "Marca", "Estado", "Usuário",
            "Número de Série", "Versão do Windows", "Versão do Office", "Localização", "Data de Compra"};
    private List<Computer> computers;

    public ComputerTableModel(List<Computer> computers) {
        this.computers = computers;
    }

    public void setComputers(List<Computer> computers) {
        this.computers = computers;
        fireTableDataChanged();
    }

    public Computer getComputerAt(int rowIndex) {
        return computers.get(rowIndex);
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
        return false;
    }
}
