package vista.Utilidades;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controlador.EscuelaController;
import controlador.grafo.Adyacencia;
import controlador.grafo.GrafoDirigidoEtiquetado;
import controlador.listas.ListaEnlazada;
import controlador.listas.exepciones.ListaVaciaException;
import controlador.listas.exepciones.PosicionNoEncontradaException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import modelo.Escuela;

public class Utilidades {

    public static Integer generarId(ListaEnlazada<Escuela> lista) {
        return lista.getTamanio() + 1;
    }

    public static JComboBox cargarCombo(JComboBox cbx, ListaEnlazada<Escuela> lista) throws ListaVaciaException, PosicionNoEncontradaException {
        cbx.removeAllItems();
        for (int i = 0; i < lista.getTamanio(); i++) {
            cbx.addItem(lista.obtener(i));
        }
        return cbx;
    }

    public static Double calcularDistancia(Double y, Double y1, Double x, Double x1) {
        Double yy = y - y1;
        Double xx = x - x1;
        return redondear(Math.sqrt((yy * yy) + (xx * xx)));
    }

    public static Double redondear(Double dato) {
        return Math.round(dato * 100.0) / 100.0;
    }

    private static MapeoEscuela mapeoEscuela(Escuela escuela, GrafoDirigidoEtiquetado<Escuela> grafoEscuela) throws ListaVaciaException, PosicionNoEncontradaException {
        MapeoEscuela mpe = new MapeoEscuela();

        ListaEnlazada<Adyacencia> adyacencias = grafoEscuela.adyacentesE(escuela);

        mpe.setAdyacencias(adyacencias);
        mpe.setEscuela(escuela);

        return mpe;
    }

    public static void guardarJSON(EscuelaController ec) throws FileNotFoundException {
        MapeoLista mpl = new MapeoLista();
        for (int i = 0; i < ec.getListaEscuela().getTamanio(); i++) {
            try {
                System.out.println(ec.getListaEscuela().obtener(i));
                mpl.getLista().insertar(mapeoEscuela(ec.getListaEscuela().obtener(i), ec.getGrafoEscuela()));
            } catch (ListaVaciaException ex) {
                Logger.getLogger(Utilidades.class.getName()).log(Level.SEVERE, null, ex);
            } catch (PosicionNoEncontradaException ex) {
                Logger.getLogger(Utilidades.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(mpl);
        try {
            PrintWriter escritor = new PrintWriter(new File("Escuelas.json"));
            escritor.write(json);
            escritor.flush();
            escritor.close();
            JOptionPane.showMessageDialog(null, "Se guardó");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al guardar");
            System.out.println(e);
        }

    }

    public static EscuelaController leerJson() {
        MapeoLista mpl = new MapeoLista();
        
        Reader lector;
        try {
            lector = Files.newBufferedReader(Paths.get("Escuelas.json"));
            Gson gson = new Gson();
            mpl = (gson.fromJson(lector, MapeoLista.class));
            EscuelaController ec = recuperarController(mpl);
            return ec;
        } catch (IOException | ListaVaciaException | PosicionNoEncontradaException ex) {
            Logger.getLogger(Utilidades.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    private static EscuelaController recuperarController(MapeoLista mpl) throws ListaVaciaException, PosicionNoEncontradaException {
        EscuelaController ec = new EscuelaController();
        ListaEnlazada<Escuela> listaEscuelas = new ListaEnlazada<>();
        for (int i = 0; i < mpl.getLista().getTamanio(); i++) {
            listaEscuelas.insertar(mpl.getLista().obtener(i).getEscuela());
        }
        ec.setListaEscuela(listaEscuelas);
        ec.crearGrafo();
        for (int i = 0; i < mpl.getLista().getTamanio(); i++) {
            for (int j = 0; j < mpl.getLista().obtener(i).getAdyacencias().getTamanio(); j++) {
                Adyacencia aux = mpl.getLista().obtener(i).getAdyacencias().obtener(j);
                ec.insertarAdyacenciaPeso(mpl.getLista().obtener(i).getEscuela(), ec.getGrafoEscuela().obtenerEtiqueta(aux.getDestino()), aux.getPeso());
            }
        }

        return ec;
    }

    public static Long[] tiempoTranscurrido(LocalTime horaInicio, LocalTime horaFin) {
        Long[] tiempo = new Long[3];
        tiempo[0] = ChronoUnit.MINUTES.between(horaInicio, horaFin);
        tiempo[1] = ChronoUnit.SECONDS.between(horaInicio, horaFin);
        tiempo[2] = ChronoUnit.MILLIS.between(horaInicio, horaFin);

        return tiempo;
    }
}
