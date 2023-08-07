package vista;

import controlador.EscuelaController;
import controlador.grafo.GrafoDirigidoEtiquetado;
import controlador.listas.ListaEnlazada;
import controlador.listas.exepciones.ListaVaciaException;
import controlador.listas.exepciones.PosicionNoEncontradaException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import modelo.Escuela;
import vista.Utilidades.Utilidades;
import vista.modelo.ModeloTablaAdyacencias;
import vista.modelo.ModeloTablaEscuelas;

public class PrincipalDialog extends javax.swing.JDialog {

    private EscuelaController ec = new EscuelaController();
    private ModeloTablaEscuelas mte = new ModeloTablaEscuelas();
    private ModeloTablaAdyacencias mta = new ModeloTablaAdyacencias();

    /**
     * Creates new form PrincipalDialog
     */
    public PrincipalDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        ec = Utilidades.leerJson();
        cargarTablaEscuelas();
        cargarCombos();
        cargarMatriz();
    }

    private void cargarCombos() {
        try {
            cbxDestinoAdyacencia = Utilidades.cargarCombo(cbxDestinoAdyacencia, ec.getListaEscuela());
            cbxDestinoBusqueda = Utilidades.cargarCombo(cbxDestinoBusqueda, ec.getListaEscuela());
            cbxOrigenAdyacencia = Utilidades.cargarCombo(cbxOrigenAdyacencia, ec.getListaEscuela());
            cbxOrigenBusqueda = Utilidades.cargarCombo(cbxOrigenBusqueda, ec.getListaEscuela());
        } catch (ListaVaciaException ex) {
            Logger.getLogger(PrincipalDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PosicionNoEncontradaException ex) {
            Logger.getLogger(PrincipalDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void cargarTablaEscuelas() {
        mte.setLista(ec.getListaEscuela());
        tblEscuelas.setModel(mte);
        tblEscuelas.updateUI();
        cargarCombos();
    }

    private void eliminarEscuela() throws ListaVaciaException, PosicionNoEncontradaException {
        if (tblEscuelas.getSelectedRow() >= 0) {
            ec.getListaEscuela().eliminarPosicion(tblEscuelas.getSelectedRow());
            for (int i = 0; i < ec.getListaEscuela().getTamanio(); i++) {
                Escuela e = ec.getListaEscuela().obtener(i);
                e.setId(i + 1);
                ec.getListaEscuela().modificarPoscicion(e, i);
            }
        }
    }

    private void fijarAdyacencia() throws Exception {
        Integer inicio = cbxOrigenAdyacencia.getSelectedIndex();
        Integer destino = cbxDestinoAdyacencia.getSelectedIndex();
        if (inicio == destino) {
            JOptionPane.showMessageDialog(null, "No se debe escoger la misma ubicación");
        } else if (txtDistanciaAdyacencia.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Indique la distancia de la adyacencia");
        } else {
            ec.insertarAdyacenciaPeso(ec.getListaEscuela().obtener(inicio), ec.getListaEscuela().obtener(destino), Double.parseDouble(txtDistanciaAdyacencia.getText()));
            Utilidades.guardarJSON(ec);
        }
        cargarMatriz();
    }

    private void cargarMatriz() {
        mta = new ModeloTablaAdyacencias();
        mta.setEc(ec);
        tblAdyacencias.setModel(mta);
        tblAdyacencias.updateUI();
    }

    private GrafoDirigidoEtiquetado<Escuela> crearGrafoAux(ListaEnlazada<Escuela> lista) throws ListaVaciaException, PosicionNoEncontradaException, Exception {
        EscuelaController ecAux = new EscuelaController();
        ecAux.setListaEscuela(lista);
        ecAux.crearGrafo();
        for (int i = 0; i < lista.getTamanio() - 1; i++) {
            if (ec.getGrafoEscuela().existeAristaE(lista.obtener(i), lista.obtener(i + 1))) {
                ecAux.insertarAdyacenciaPeso(lista.obtener(i), lista.obtener(i + 1), ec.getGrafoEscuela().pesoArista(ec.getGrafoEscuela().obtenerCodigoE(lista.obtener(i)), ec.getGrafoEscuela().obtenerCodigoE(lista.obtener(i + 1))));
            } else {
                ecAux.insertarAdyacencia(lista.obtener(i), lista.obtener(i + 1));
            }
        }
        return ecAux.getGrafoEscuela();
    }

    private GrafoDirigidoEtiquetado<Escuela> crearGrafoAux(EscuelaController nueva) throws ListaVaciaException, PosicionNoEncontradaException, Exception {
        EscuelaController ecAux = Utilidades.leerJson();
        var lista = nueva.getListaEscuela();
        for (int i = 0; i < nueva.getListaEscuela().getTamanio() - 1; i++) {
            if (ecAux.getGrafoEscuela().existeAristaE(lista.obtener(i), lista.obtener(i + 1))) {
                nueva.insertarAdyacenciaPeso(lista.obtener(i), lista.obtener(i + 1), ecAux.getGrafoEscuela().pesoArista(ecAux.getGrafoEscuela().obtenerCodigoE(lista.obtener(i)), ecAux.getGrafoEscuela().obtenerCodigoE(lista.obtener(i + 1))));
            } else {
                nueva.insertarAdyacencia(lista.obtener(i), lista.obtener(i + 1));
            }
        }
        return nueva.getGrafoEscuela();
    }

    private void verGrafo(GrafoDirigidoEtiquetado<Escuela> grafo) {
        new FrmGrafo(null, true, grafo).setVisible(true);
    }

    private void buscar() throws ListaVaciaException, PosicionNoEncontradaException {
        LocalTime tiempoInicio = null;
        ListaEnlazada<Escuela> aux = null;
        Escuela origen = ec.getListaEscuela().obtener(cbxOrigenBusqueda.getSelectedIndex());
        Escuela destino = ec.getListaEscuela().obtener(cbxDestinoBusqueda.getSelectedIndex());

        if (!rdbtnFord.isSelected()) {
            JOptionPane.showMessageDialog(this, "Seleccione el método de búsqueda");
        } else {
            tiempoInicio = LocalTime.now();
            aux = ec.ford(origen, destino);
        }

        calcularTiempo(tiempoInicio);
        aux.imprimir();
        try {
            if (aux.getTamanio() != null) {
                verGrafo(crearGrafoAux(aux));
            } else {
                JOptionPane.showMessageDialog(this, "No existe Camino");
            }
        } catch (ListaVaciaException ex) {
            Logger.getLogger(PrincipalDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PosicionNoEncontradaException ex) {
            Logger.getLogger(PrincipalDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(PrincipalDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void calcularTiempo(LocalTime tiempoInicio) {
        Long[] tiempos = Utilidades.tiempoTranscurrido(tiempoInicio, LocalTime.now());
        String tiempo = "";
        String data = "";
        for (int i = 0; i < tiempos.length; i++) {
            switch (i) {
                case 0: {
                    tiempo = "minutos: " + tiempos[i] + " ";
                    data = tiempos[i] + ":";
                    continue;
                }
                case 1: {
                    tiempo = tiempo + "segundos: " + tiempos[i] + " ";
                    data = data + tiempos[i] + ":";
                    continue;
                }
                case 2: {
                    tiempo = tiempo + "milisegundos: " + tiempos[i] + " ";
                    data = data + tiempos[i] + "";
                    continue;
                }
                default:
                    break;
            }
        }
        txtTiempo.setText(data);
        System.out.println(tiempo);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btngMetodosBusqueda = new javax.swing.ButtonGroup();
        btngRecorridoGrafo = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cbxDestinoAdyacencia = new javax.swing.JComboBox<>();
        cbxOrigenAdyacencia = new javax.swing.JComboBox<>();
        btnAdyacencia = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAdyacencias = new javax.swing.JTable();
        btnVerGrafo = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtDistanciaAdyacencia = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        cbxOrigenBusqueda = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        btnBuscar = new javax.swing.JButton();
        cbxDestinoBusqueda = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        rdbtnFord = new javax.swing.JRadioButton();
        jLabel6 = new javax.swing.JLabel();
        txtTiempo = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        btnAgregarEscuela = new javax.swing.JButton();
        btnModificarEscuela = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblEscuelas = new javax.swing.JTable();
        btnEliminar1 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        guardarGson = new javax.swing.JMenuItem();
        cargarJson = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(50, 720));

        jPanel1.setMinimumSize(new java.awt.Dimension(50, 760));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Adyacencias"));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Destino:");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 70, -1, -1));

        jLabel2.setText("Origen:");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, -1, -1));

        cbxDestinoAdyacencia.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel2.add(cbxDestinoAdyacencia, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 70, 150, -1));

        cbxOrigenAdyacencia.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel2.add(cbxOrigenAdyacencia, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 40, 150, -1));

        btnAdyacencia.setText("ADYACENCIA");
        btnAdyacencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdyacenciaActionPerformed(evt);
            }
        });
        jPanel2.add(btnAdyacencia, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 70, -1, -1));

        tblAdyacencias.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblAdyacencias);

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 430, 340));

        btnVerGrafo.setText("VER GRAFO");
        btnVerGrafo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerGrafoActionPerformed(evt);
            }
        });
        jPanel2.add(btnVerGrafo, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 470, -1, -1));

        jLabel5.setText("Distancia:");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 40, -1, -1));
        jPanel2.add(txtDistanciaAdyacencia, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 40, 140, -1));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 480, 510));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Camino Corto"));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cbxOrigenBusqueda.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel3.add(cbxOrigenBusqueda, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 40, 320, -1));

        jLabel3.setText("Origen:");
        jPanel3.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, -1, -1));

        btnBuscar.setText("BUSCAR");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });
        jPanel3.add(btnBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 80, -1, -1));

        cbxDestinoBusqueda.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel3.add(cbxDestinoBusqueda, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 40, 320, -1));

        jLabel4.setText("Destino:");
        jPanel3.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 40, -1, -1));

        btngMetodosBusqueda.add(rdbtnFord);
        rdbtnFord.setText("FORD");
        jPanel3.add(rdbtnFord, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 90, -1, -1));

        jLabel6.setText("Tiempo empleado (mm:ss:ms):");
        jPanel3.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 80, 180, -1));

        txtTiempo.setText("00:00:00");
        jPanel3.add(txtTiempo, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 80, 60, -1));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 520, 870, 130));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("EscuelasCrud"));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnAgregarEscuela.setText("AGREGAR ESCUELA");
        btnAgregarEscuela.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarEscuelaActionPerformed(evt);
            }
        });
        jPanel4.add(btnAgregarEscuela, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 400, -1, -1));

        btnModificarEscuela.setText("MODIFICAR ESCUELA");
        btnModificarEscuela.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarEscuelaActionPerformed(evt);
            }
        });
        jPanel4.add(btnModificarEscuela, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 400, -1, -1));

        tblEscuelas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tblEscuelas);

        jPanel4.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 350, 310));

        btnEliminar1.setText("ELIMINAR ESCUELA");
        btnEliminar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminar1ActionPerformed(evt);
            }
        });
        jPanel4.add(btnEliminar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 440, -1, -1));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 20, 380, 500));

        jMenu1.setText("Archivo");

        guardarGson.setText("Guardar");
        guardarGson.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarGsonActionPerformed(evt);
            }
        });
        jMenu1.add(guardarGson);

        cargarJson.setText("Cargar");
        cargarJson.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cargarJsonActionPerformed(evt);
            }
        });
        jMenu1.add(cargarJson);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 902, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 6, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 671, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        try {
            long startTime = System.currentTimeMillis();
            buscar();
            long finalTime = System.currentTimeMillis() - startTime;

            // Convertir el tiempo en segundos, minutos y horas
            long seconds = finalTime / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            seconds = seconds % 60;
            minutes = minutes % 60;

            System.out.println("ORDENAR TIEMPO:");
            System.out.println("Horas: " + hours + ", Minutos: " + minutes + ", Segundos: " + seconds);

            txtTiempo.setText(hours + "h " + minutes + "m " + seconds + "s");

        } catch (ListaVaciaException ex) {
            Logger.getLogger(PrincipalDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PosicionNoEncontradaException ex) {
            Logger.getLogger(PrincipalDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnAgregarEscuelaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarEscuelaActionPerformed
        try {
            // TODO add your handling code here:
            EscuelaController aux = Utilidades.leerJson();
            var grafoAux = aux.getGrafoEscuela();
            EscuelaDialog ed = new EscuelaDialog(null, true, ec);
            ed.setVisible(true);
            ed.dispose();
            cargarTablaEscuelas();
            ec.crearGrafo();
            for (int i = 1; i <= aux.getListaEscuela().getTamanio(); i++) {
                for (int j = 1; j <= aux.getListaEscuela().getTamanio(); j++) {
                    if (grafoAux.existeArista(i, j)) {
                        ec.insertarAdyacenciaPeso(ec.getListaEscuela().obtener(i - 1), ec.getListaEscuela().obtener(j - 1), aux.getGrafoEscuela().pesoArista(i, j));
                    }
                }
            }
            Utilidades.guardarJSON(ec);
            cargarMatriz();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PrincipalDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PosicionNoEncontradaException ex) {
            Logger.getLogger(PrincipalDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(PrincipalDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAgregarEscuelaActionPerformed

    private void btnModificarEscuelaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarEscuelaActionPerformed
        // TODO add your handling code here:

        if (tblEscuelas.getSelectedRow() >= 0) {
            EscuelaController aux = Utilidades.leerJson();
            var grafoAux = aux.getGrafoEscuela();
            EscuelaDialog ed = new EscuelaDialog(null, true, ec, tblEscuelas.getSelectedRow());
            ed.setVisible(true);
            ed.dispose();
            cargarTablaEscuelas();
            ec.crearGrafo();
            for (int i = 1; i <= aux.getListaEscuela().getTamanio(); i++) {
                for (int j = 1; j <= aux.getListaEscuela().getTamanio(); j++) {
                    try {
                        if (grafoAux.existeArista(i, j)) {
                            ec.insertarAdyacenciaPeso(ec.getListaEscuela().obtener(i - 1), ec.getListaEscuela().obtener(j - 1), aux.getGrafoEscuela().pesoArista(i, j));
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(PrincipalDialog.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            try {
                Utilidades.guardarJSON(ec);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(PrincipalDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
            cargarMatriz();
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione la escuela a modificar");
        }

    }//GEN-LAST:event_btnModificarEscuelaActionPerformed

    private void btnAdyacenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdyacenciaActionPerformed
        try {
            // TODO add your handling code here:
            fijarAdyacencia();
            cargarMatriz();
        } catch (Exception ex) {
            Logger.getLogger(PrincipalDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAdyacenciaActionPerformed

    private void btnVerGrafoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerGrafoActionPerformed
        // TODO add your handling code here:
        verGrafo(ec.getGrafoEscuela());
    }//GEN-LAST:event_btnVerGrafoActionPerformed

    private void guardarGsonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarGsonActionPerformed
        try {
            // TODO add your handling code here:
            Utilidades.guardarJSON(ec);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PrincipalDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_guardarGsonActionPerformed

    private void cargarJsonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cargarJsonActionPerformed
        // TODO add your handling code here:
        ec = Utilidades.leerJson();
        cargarCombos();
        cargarMatriz();
        cargarTablaEscuelas();
    }//GEN-LAST:event_cargarJsonActionPerformed

    private void btnEliminar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminar1ActionPerformed
        try {
            // TODO add your handling code here:
            eliminarEscuela();
            cargarTablaEscuelas();
            ec.crearGrafo();
            cargarMatriz();
        } catch (ListaVaciaException ex) {
            Logger.getLogger(PrincipalDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PosicionNoEncontradaException ex) {
            Logger.getLogger(PrincipalDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnEliminar1ActionPerformed

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
            java.util.logging.Logger.getLogger(PrincipalDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PrincipalDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PrincipalDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PrincipalDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                PrincipalDialog dialog = new PrincipalDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnAdyacencia;
    private javax.swing.JButton btnAgregarEscuela;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnEliminar1;
    private javax.swing.JButton btnModificarEscuela;
    private javax.swing.JButton btnVerGrafo;
    private javax.swing.ButtonGroup btngMetodosBusqueda;
    private javax.swing.ButtonGroup btngRecorridoGrafo;
    private javax.swing.JMenuItem cargarJson;
    private javax.swing.JComboBox<String> cbxDestinoAdyacencia;
    private javax.swing.JComboBox<String> cbxDestinoBusqueda;
    private javax.swing.JComboBox<String> cbxOrigenAdyacencia;
    private javax.swing.JComboBox<String> cbxOrigenBusqueda;
    private javax.swing.JMenuItem guardarGson;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JRadioButton rdbtnFord;
    private javax.swing.JTable tblAdyacencias;
    private javax.swing.JTable tblEscuelas;
    private javax.swing.JTextField txtDistanciaAdyacencia;
    private javax.swing.JLabel txtTiempo;
    // End of variables declaration//GEN-END:variables

}
