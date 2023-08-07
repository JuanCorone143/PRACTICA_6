package vista.Utilidades;

import controlador.listas.ListaEnlazada;

public class MapeoLista {
    private ListaEnlazada<MapeoEscuela> lista = new ListaEnlazada<>();
    
    public MapeoLista() {
    }
    
    public MapeoLista(ListaEnlazada<MapeoEscuela> lista) {
        this.lista = lista;
    }
        
    public ListaEnlazada<MapeoEscuela> getLista() {
        return lista;
    }

    public void setLista(ListaEnlazada<MapeoEscuela> lista) {
        this.lista = lista;
    }
}
