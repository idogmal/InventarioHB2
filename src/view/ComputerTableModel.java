package view;

import model.Computer;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class ComputerTableModel extends AbstractTableModel {
    private final String[] columnNames = { "ETIQUETA-TI", "NOME DO PC", "USUÁRIO", "LOCALIZACAO", "SETOR",
            "VERSAO SO", "VERSAO OFFICE", "MODELO", "Nº SERIE", "DATA DE COMPRA", "PATRIMONIO", "OBS" };
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
            case 0:
                return c.getTag();
            case 1:
                return c.getHostname();
            case 2:
                return c.getUserName();
            case 3:
                return c.getLocation();
            case 4:
                return c.getSector();
            case 5:
                return c.getWindowsVersion();
            case 6:
                return c.getOfficeVersion();
            case 7:
                return c.getModel();
            case 8:
                return c.getSerialNumber();
            case 9:
                return c.getPurchaseDate();
            case 10:
                return c.getPatrimony();
            case 11:
                return "Ver/Editar"; // Botão
            default:
                return "";
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 11; // Apenas a coluna de observações é editável (para o botão funcionar)
    }
}
