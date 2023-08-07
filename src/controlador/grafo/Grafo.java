package controlador.grafo;

import controlador.listas.ListaEnlazada;
import controlador.listas.exepciones.ListaVaciaException;
import controlador.listas.exepciones.PosicionNoEncontradaException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Grafo {
    public abstract Integer nroVertices();
    
    public abstract Integer nroAristas();
    
    public abstract Boolean existeArista(Integer o, Integer d) throws Exception;
    
    public abstract Double pesoArista(Integer o, Integer d);

    public abstract void insertarArista(Integer o, Integer d) throws Exception; 
    
    public abstract void insertarArista(Integer o, Integer d, Double peso) throws Exception;
    
    public abstract ListaEnlazada<Adyacencia> adyacentes(Integer v);

    @Override
    public String toString() {
        StringBuffer grafo = new StringBuffer("");
        try {
            for (int i = 1; i <= nroVertices(); i++) {
                grafo.append("Vertice " + String.valueOf(i));
                ListaEnlazada<Adyacencia> lista = adyacentes(i);
                if (lista != null) {
                    for (int j = 0; j < lista.getTamanio(); j++) {
                        Adyacencia a = lista.obtener(j);
                        if (a.getPeso().toString().equalsIgnoreCase(String.valueOf(Double.NaN))) {
                            grafo.append(" -- Vertice destino -- " + a.getDestino() + " Sin Peso");
                        } else {
                            grafo.append(" -- Vertice destino -- " + a.getDestino() + " Peso " + a.getPeso());
                        }
                    }
                }
                grafo.append("\n");
            }
        } catch (Exception e){
            grafo.append(e);
            e.printStackTrace();
        }
        return grafo.toString();
    }
    
    public ListaEnlazada caminoMinimo(Integer origen, Integer destino) throws Exception{
        ListaEnlazada camino = new ListaEnlazada();
        
        if (estaConectado()) {
            ListaEnlazada pesos = new ListaEnlazada();
            Boolean finalizar = false;
            Integer inicial = origen;
            camino.insertar(inicial);
            while(!finalizar){
                ListaEnlazada<Adyacencia> listaAyacencias = adyacentes(inicial);
                Double peso = 10000000.0;
                int T = -1;
                for (int i = 0; i < listaAyacencias.getTamanio(); i++) {
                    Adyacencia ad = listaAyacencias.obtener(i);
                    if (!estaEnCamino(camino, destino)) {
                        Double pesoArista =  (Double) ad.getPeso();
                        if (destino.intValue() == ad.getDestino().intValue()) {
                            T = ad.getDestino();
                            peso = pesoArista;
                            break;
                        }else if(pesoArista < peso){
                            T = ad.getDestino();
                            peso = pesoArista;
                        }
                    }
                }
                pesos.insertar(peso);
                camino.insertar(T);
                inicial = T;
                if (destino.intValue() == inicial.intValue()) {
                    finalizar = true;
                }
            }
        }else{
            throw new Exception("Grafo no conectado");
        }
        
        return camino;
    }
    
    public Boolean estaConectado(){
        Boolean band = true;
        
        for (int i = 0; i <= nroVertices(); i++) {
            ListaEnlazada<Adyacencia> listaAdyacencias = adyacentes(i);
            if (listaAdyacencias.estaVacia() || listaAdyacencias.getTamanio() == 0) {
                band = false;
                break;
            }
        }
        
        return band;
    }
    
    public Boolean estaEnCamino(ListaEnlazada<Integer> lista, Integer vertice) throws ListaVaciaException, PosicionNoEncontradaException{
        Boolean band = false;
        
        for (int i = 0; i < lista.getTamanio(); i++) {
            if (lista.obtener(i).intValue() == vertice.intValue()) {
                band = true;
                break;
            }
        }
        
        return band;
    }
}
