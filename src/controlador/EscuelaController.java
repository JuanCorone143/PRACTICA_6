package controlador;

import controlador.grafo.GrafoDirigidoEtiquetado;
import controlador.listas.ListaEnlazada;
import controlador.listas.exepciones.ListaVaciaException;
import controlador.listas.exepciones.PosicionNoEncontradaException;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Escuela;

public class EscuelaController {
    private ListaEnlazada<Escuela> listaEscuela = new ListaEnlazada<>();
    private GrafoDirigidoEtiquetado<Escuela> grafoEscuela;
    
    public EscuelaController() {
    }
    
    public void crearGrafo(){
        if (this.listaEscuela != null && this.listaEscuela.getTamanio() != 0) {
            this.grafoEscuela = new GrafoDirigidoEtiquetado<>(listaEscuela.getTamanio(), Escuela.class);
            etiquetarVertices();
        }else{
            System.out.println("Lista de escuelas vacía");
        }
    }
    
    private void etiquetarVertices(){
        for (int i = 0; i < listaEscuela.getTamanio(); i++) {
            try {
                this.grafoEscuela.etiquetarVertice(i+1, listaEscuela.obtener(i));
            } catch (ListaVaciaException ex) {
                Logger.getLogger(EscuelaController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (PosicionNoEncontradaException ex) {
                Logger.getLogger(EscuelaController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void insertarAdyacencia(Escuela origen, Escuela destino){
        if (this.grafoEscuela != null) {
            try {
                this.grafoEscuela.insertarAristaE(origen, destino);
            } catch (Exception ex) {
                Logger.getLogger(EscuelaController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            System.out.println("Cree el grafo primero");
        }
    }
    
    public void insertarAdyacenciaPeso(Escuela origen, Escuela destino, Double peso){
        if (this.grafoEscuela != null) {
            try {
                this.grafoEscuela.insertarAristaE(origen, destino, peso);
            } catch (Exception ex) {
                Logger.getLogger(EscuelaController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            System.out.println("Cree el grafo primero");
        }
    }
    
    public ListaEnlazada<Escuela> ford(Escuela origen, Escuela destino){
        if (this.grafoEscuela != null) {
            return this.grafoEscuela.bellmanFord(origen, destino);
        }else{
            System.out.println("Cree el grafo primero");
        }
        return null;
    }
    
    public Double caclucarDistancia(Integer or, Integer de){
        Integer origen = getGrafoEscuela().obtenerCodigoE(getGrafoEscuela().obtenerEtiqueta(or));
        Integer destino = getGrafoEscuela().obtenerCodigoE(getGrafoEscuela().obtenerEtiqueta(de));
        return getGrafoEscuela().pesoArista(origen, destino);
    }

    public ListaEnlazada<Escuela> getListaEscuela() {
        return listaEscuela;
    }

    public void setListaEscuela(ListaEnlazada<Escuela> listaEscuela) {
        this.listaEscuela = listaEscuela;
    }

    public GrafoDirigidoEtiquetado<Escuela> getGrafoEscuela() {
        return grafoEscuela;
    }

    public void setGrafoEscuela(GrafoDirigidoEtiquetado<Escuela> grafoEscuela) {
        this.grafoEscuela = grafoEscuela;
    }
    
    
}
