package controlador.grafo;

import controlador.listas.ListaEnlazada;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class GrafoDirigidoEtiquetado<E> extends GrafoDirigido {

    protected E etiquetas[];
    protected HashMap<E, Integer> dicVertices;
    private Class<E> clazz;
    private Boolean marcaEtiqueta[];

    public GrafoDirigidoEtiquetado(Integer numVertices, Class clazz) {
        super(numVertices);
        this.clazz = clazz;
        this.etiquetas = (E[]) Array.newInstance(clazz, numVertices + 1);
        this.dicVertices = new HashMap(numVertices);
        this.marcaEtiqueta = new Boolean[etiquetas.length];
    }

    public Boolean existeAristaE(E o, E d) throws Exception {
        return this.existeArista(obtenerCodigoE(o), obtenerCodigoE(d));
    }

    public void insertarAristaE(E o, E d, Double peso) throws Exception {
        insertarArista(obtenerCodigoE(o), obtenerCodigoE(d), peso);
    }

    public void insertarAristaE(E o, E d) throws Exception {
        insertarArista(obtenerCodigoE(o), obtenerCodigoE(d));
    }

    public ListaEnlazada<Adyacencia> adyacentesE(E o) {
        System.out.println(obtenerCodigoE(o));
        return adyacentes(obtenerCodigoE(o));
    }

    public Integer obtenerCodigoE(E etiqueta) {
        return dicVertices.get(etiqueta);
    }

    public E obtenerEtiqueta(Integer codigo) {
        return etiquetas[codigo];
    }

    public void etiquetarVertice(Integer codigo, E etiqueta) {
        etiquetas[codigo] = etiqueta;
        dicVertices.put(etiqueta, codigo);
    }

    public String toString() {
        StringBuffer grafo = new StringBuffer("");
        try {
            for (int i = 1; i <= nroVertices(); i++) {
                grafo.append("Vertice " + String.valueOf(etiquetas[i]));
                ListaEnlazada<Adyacencia> lista = adyacentes(i);
                if (lista != null) {
                    for (int j = 0; j < lista.getTamanio(); j++) {
                        Adyacencia a = lista.obtener(j);
                        if (a.getPeso().toString().equalsIgnoreCase(String.valueOf(Double.NaN))) {
                            grafo.append(" -- Vertice destino -- " + etiquetas[a.getDestino()] + " Sin Peso");
                        } else {
                            grafo.append(" -- Vertice destino -- " + etiquetas[a.getDestino()] + " Peso " + a.getPeso());
                        }
                    }
                }
                grafo.append("\n");
            }
        } catch (Exception e) {
            grafo.append(e);
            e.printStackTrace();
        }
        return grafo.toString();
    }
    
    public ListaEnlazada<E> bellmanFord(E origen, E destino) {
        try {
            Double distancias[] = new Double[nroVertices() + 1];
            Integer padres[] = new Integer[nroVertices() + 1];

            
            for (int i = 1; i <= nroVertices(); i++) {
                distancias[i] = Double.POSITIVE_INFINITY;
                padres[i] = null;
            }
            distancias[obtenerCodigoE(origen)] = 0.0;

            
            for (int i = 1; i < nroVertices(); i++) {
                for (int u = 1; u <= nroVertices(); u++) {
                    ListaEnlazada<Adyacencia> ady = adyacentes(u);
                    if (ady != null) {
                        for (int j = 0; j < ady.getTamanio(); j++) {
                            Adyacencia a = ady.obtener(j);
                            int v = a.getDestino();
                            double weight = a.getPeso();
                            if (distancias[u] + weight < distancias[v]) {
                                distancias[v] = distancias[u] + weight;
                                padres[v] = u;
                            }
                        }
                    }
                }
            }

            
            for (int u = 1; u <= nroVertices(); u++) {
                ListaEnlazada<Adyacencia> ady = adyacentes(u);
                if (ady != null) {
                    for (int j = 0; j < ady.getTamanio(); j++) {
                        Adyacencia a = ady.obtener(j);
                        int v = a.getDestino();
                        double weight = a.getPeso();
                        if (distancias[u] + weight < distancias[v]) {
                            throw new Exception("El grafo contiene un ciclo de pesos negativos.");
                        }
                    }
                }
            }

            return recuperarCaminoCortoBellmanFord(origen, destino, padres);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private ListaEnlazada<E> recuperarCaminoCortoBellmanFord(E origen, E destino, Integer padres[]) {
        ListaEnlazada<E> camino = new ListaEnlazada<>();
        construirCamino(camino, origen, destino, padres);
        return camino;
    }

    private void construirCamino(ListaEnlazada<E> camino, E origen, E destino, Integer padres[]) {
        int destinoCodigo = obtenerCodigoE(destino);
        int actual = destinoCodigo;
        if (actual != obtenerCodigoE(origen)) {
            E etiqueta = obtenerEtiqueta(padres[actual]);
            construirCamino(camino, origen, etiqueta, padres);
        }
        camino.insertar(destino);
    }
    
}
