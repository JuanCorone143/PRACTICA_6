package controlador.grafo.excepciones;

public class VerticeOfSizeException extends Exception{

    public VerticeOfSizeException(String msg) {
        super(msg);
    }
    
    public VerticeOfSizeException() {
        System.out.println("Número de vértices superado");
    }
    
}
