package vista.modelo;

import controlador.EscuelaController;
import javax.swing.table.AbstractTableModel;
import modelo.Escuela;

public class ModeloTablaAdyacencias extends AbstractTableModel {
    
    private EscuelaController ec = new EscuelaController();

    public EscuelaController getEc() {
        return ec;
    }

    public void setEc(EscuelaController ec) {
        this.ec = ec;
    }

    @Override
    public int getRowCount() {
        return ec.getListaEscuela().getTamanio();
    }

    @Override
    public int getColumnCount() {
        return ec.getListaEscuela().getTamanio() + 1;
    }

    @Override
    public String getColumnName(int column) {
        try {
            if (column == 0) {
                return "--";
            } else {
                return ec.getListaEscuela().obtener(column - 1).toString();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return "**";
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Escuela e = null;
        try {
            if (columnIndex == 0) {
                return ec.getListaEscuela().obtener(rowIndex).toString();
            } else {
                if (ec.getGrafoEscuela().existeAristaE(ec.getGrafoEscuela().obtenerEtiqueta(rowIndex + 1), ec.getGrafoEscuela().obtenerEtiqueta(columnIndex))) {
                    return ec.caclucarDistancia(rowIndex+1, columnIndex);
                }
                return "--";
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
        return "--";
    }
}
