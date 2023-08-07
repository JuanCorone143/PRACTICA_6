package vista;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.handler.mxEdgeHandler;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.view.mxGraph;
import controlador.grafo.Adyacencia;
import controlador.grafo.Grafo;
import controlador.grafo.GrafoDirigidoEtiquetado;
import controlador.listas.ListaEnlazada;
import controlador.listas.exepciones.ListaVaciaException;
import controlador.listas.exepciones.PosicionNoEncontradaException;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class FrmGrafo extends javax.swing.JDialog {
    private Grafo grafo;
    private GrafoDirigidoEtiquetado grafoE;

    /**
     * Creates new form Grafo
     */
    public FrmGrafo(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public FrmGrafo(java.awt.Frame parent, boolean modal, Grafo grafo) {
        super(parent, modal);
        this.grafo = grafo;
        initComponents();
        cargarDatos();
    }
    
    public FrmGrafo(java.awt.Frame parent, boolean modal, GrafoDirigidoEtiquetado grafoE) {
        super(parent, modal);
        this.grafoE = grafoE;
        initComponents();
        cargarDatosE();
    }
        
    private void cargarDatos(){
        mxGraph graph = new mxGraph();
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        graphComponent.setSize(new Dimension(900, 900));
        jPanel1.add(graphComponent, BorderLayout.CENTER);
        
        Object objectParent = graph.getDefaultParent();
        graph.getModel().beginUpdate();
        
        cargar(objectParent, graph);
        
        morphGraph(graph, graphComponent);
        new mxHierarchicalLayout(graph).execute(graph.getDefaultParent());
    }
       
    private void cargar(Object objectParent, mxGraph graph){
        ListaEnlazada<Object> listaObjects = new ListaEnlazada<>();
        try {
            for (int i = 1; i <= grafo.nroVertices(); i++) {
                Object inicio = graph.insertVertex(objectParent, String.valueOf(i), String.valueOf(i), 100, 100, 80, 30);
                listaObjects.insertar(inicio);
            }
            
            for (int i = 0; i < listaObjects.getTamanio(); i++) {
                ListaEnlazada<Adyacencia> listaAdyacentes = grafo.adyacentes(i+1);
                for (int j = 0; j < listaAdyacentes.getTamanio(); j++) {
                    Adyacencia a = listaAdyacentes.obtener(j);
                    Object inicio = listaObjects.obtener(i);
                    Object destino = listaObjects.obtener(a.getDestino()-1);
                    graph.insertEdge(objectParent, null, String.valueOf(a.getPeso()), inicio, destino);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            graph.getModel().endUpdate();
        }
    }
    
    private void cargarDatosE(){
        mxGraph graph = new mxGraph();
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        graphComponent.setSize(new Dimension(900, 900));
        jPanel1.add(graphComponent, BorderLayout.CENTER);
        
        Object objectParent = graph.getDefaultParent();
        graph.getModel().beginUpdate();
        
        cargarE(objectParent, graph);
        
        morphGraph(graph, graphComponent);
        new mxHierarchicalLayout(graph).execute(graph.getDefaultParent());
    }
       
    private void cargarE(Object objectParent, mxGraph graph){
        ListaEnlazada<Object> listaObjects = new ListaEnlazada<>();
        try {
            for (int i = 1; i <= grafoE.nroVertices(); i++) {
                Object inicio = graph.insertVertex(objectParent, String.valueOf(grafoE.obtenerEtiqueta(i)), String.valueOf(grafoE.obtenerEtiqueta(i)), 100, 100, 80, 30);
                listaObjects.insertar(inicio);
            }
            
            for (int i = 0; i < listaObjects.getTamanio(); i++) {
                ListaEnlazada<Adyacencia> listaAdyacentes = grafoE.adyacentes(i+1);
                for (int j = 0; j < listaAdyacentes.getTamanio(); j++) {
                    Adyacencia a = listaAdyacentes.obtener(j);
                    Object inicio = listaObjects.obtener(i);
                    Object destino = listaObjects.obtener(a.getDestino()-1);
                    graph.insertEdge(objectParent, null, String.valueOf(a.getPeso()), inicio, destino);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            graph.getModel().endUpdate();
        }
    }
    
    private static void morphGraph(mxGraph graph, mxGraphComponent graphComponent){
        mxIGraphLayout layaut = new mxFastOrganicLayout(graph);
        graph.getModel().beginUpdate();
        try {
            layaut.execute(graph.getDefaultParent());
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            mxMorphing morph = new mxMorphing(graphComponent, 20, 1.5, 20);
            morph.addListener(mxEvent.DONE, new mxEventSource.mxIEventListener() {
                @Override
                public void invoke(Object o, mxEventObject eo) {
                    graph.getModel().endUpdate();
                }
            });
            morph.startAnimation();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        jPanel1.setLayout(null);
        jScrollPane1.setViewportView(jPanel1);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(0, 0, 690, 480);

        setSize(new java.awt.Dimension(700, 510));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrmGrafo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmGrafo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmGrafo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmGrafo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                FrmGrafo dialog = new FrmGrafo(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
