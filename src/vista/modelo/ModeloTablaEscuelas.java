package vista.modelo;

import controlador.listas.ListaEnlazada;
import controlador.listas.exepciones.ListaVaciaException;
import controlador.listas.exepciones.PosicionNoEncontradaException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;
import modelo.Escuela;

public class ModeloTablaEscuelas extends AbstractTableModel {

    private ListaEnlazada<Escuela> lista = new ListaEnlazada<>();

    public ListaEnlazada<Escuela> getLista() {
        return lista;
    }

    public void setLista(ListaEnlazada<Escuela> lista) {
        this.lista = lista;
    }

    public ModeloTablaEscuelas() {
    }

    public ModeloTablaEscuelas(ListaEnlazada<Escuela> lista) {
        this.lista = lista;
    }

    @Override
    public int getRowCount() {
        return lista.getTamanio();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Id";
            case 1:
                return "Nombre";
            default:
                return null;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Escuela e = null;
        try {
            if (lista.obtener(rowIndex) != null) {
                e = lista.obtener(rowIndex);
            }
        } catch (ListaVaciaException ex) {
            Logger.getLogger(ModeloTablaEscuelas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PosicionNoEncontradaException ex) {
            Logger.getLogger(ModeloTablaEscuelas.class.getName()).log(Level.SEVERE, null, ex);
        }
        switch (columnIndex) {
            case 0:
                return (e != null) ? e.getId() : "No definido";
            case 1:
                return (e != null) ? e.getNombre() : "No definido";
            default:
                return null;
        }

    }

}
