package controlador.grafo;

import controlador.grafo.excepciones.VerticeOfSizeException;
import controlador.listas.ListaEnlazada;

public class GrafoDirigido extends Grafo{
    private Integer numVertices;
    private Integer numAristas;
    private ListaEnlazada<Adyacencia> listaAdyacente[];

    public GrafoDirigido(Integer numVertices) {
        this.numVertices = numVertices;
        this.numAristas = 0;
        this.listaAdyacente = new ListaEnlazada[numVertices + 1];
        for (int i = 0; i < this.listaAdyacente.length; i++) {
            listaAdyacente[i] = new ListaEnlazada<>();
        }
    }

    public Integer getNumVertices() {
        return numVertices;
    }

    public void setNumVertices(Integer numVertices) {
        this.numVertices = numVertices;
    }

    @Override
    public Integer nroVertices() {
        return numVertices;
    }

    @Override
    public Integer nroAristas() {
        return numAristas;
    }

    @Override
    public Boolean existeArista(Integer o, Integer d) throws Exception {
        Boolean existe = false;
        
        if (o.intValue() <= numVertices && d.intValue() <= numVertices) {
            ListaEnlazada<Adyacencia> lista = listaAdyacente[o];
            if (lista != null) {
                for (int i = 0; i < lista.getTamanio(); i++) {
                    Adyacencia a = lista.obtener(i);
                    if (a.getDestino().intValue() == d.intValue()) {
                        existe = true;
                        break;
                    }
                }
            }            
        }else{
            throw new VerticeOfSizeException();
        }
        
        return existe;
    }

    @Override
    public Double pesoArista(Integer o, Integer d) {
        Double peso = Double.NaN;
        try {
            if (existeArista(o, d)) {
                ListaEnlazada<Adyacencia> listaA = listaAdyacente[o];
                for (int i = 0; i < listaA.getTamanio(); i++) {
                    Adyacencia a = listaA.obtener(i);
                    if (a.getDestino().intValue() == d.intValue()) {
                        peso = (Double)a.getPeso();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        return peso;
    }

    @Override
    public void insertarArista(Integer o, Integer d) throws Exception{
        insertarArista(o, d, Double.NaN);
    }

    @Override
    public void insertarArista(Integer o, Integer d, Double peso) throws Exception{
        try {
            if (!existeArista(o, d)) {
                if (o.intValue() <= numVertices && d.intValue() <= numVertices) {
                    if(!existeArista(o, d)){
                        numAristas++;
                        listaAdyacente[o].insertar(new Adyacencia(d, peso));
                    }
                }else{
                    throw new VerticeOfSizeException();
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public ListaEnlazada<Adyacencia> adyacentes(Integer v) {
        return listaAdyacente[v];
    }

    public Integer getNumAristas() {
        return numAristas;
    }

    public void setNumAristas(Integer numAristas) {
        this.numAristas = numAristas;
    }

    public ListaEnlazada<Adyacencia>[] getListaAdyacente() {
        return listaAdyacente;
    }
}
