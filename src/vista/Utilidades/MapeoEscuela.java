package vista.Utilidades;

import controlador.grafo.Adyacencia;
import controlador.listas.ListaEnlazada;
import modelo.Escuela;

public class MapeoEscuela {
    private Escuela escuela;
    private ListaEnlazada<Adyacencia> adyacencias;

    public MapeoEscuela() {
    }

    public MapeoEscuela(Escuela escuela) {
        this.escuela = escuela;
    }

    public Escuela getEscuela() {
        return escuela;
    }

    public void setEscuela(Escuela escuela) {
        this.escuela = escuela;
    }

    public ListaEnlazada<Adyacencia> getAdyacencias() {
        return adyacencias;
    }

    public void setAdyacencias(ListaEnlazada<Adyacencia> adyacencias) {
        this.adyacencias = adyacencias;
    }
}
