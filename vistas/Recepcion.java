/*
	Trabajo práctico final de la Guía 6 del curso Desarrollo de Apps
	Universidad de La Punta en el marco del proyecto Argentina Programa 4.0

	Integrantes:
		John David Molina Velarde
		Leticia Mores
		Enrique Germán Martínez
		Carlos Eduardo Beltrán
 */
package vistas;

import accesoadatos.CategoriaData;
import accesoadatos.ItemData;
import accesoadatos.MesaData;
import accesoadatos.PedidoData;
import accesoadatos.ProductoData;
import accesoadatos.ProductoData.OrdenacionProducto;
import accesoadatos.ServicioData;
import entidades.Categoria;
import entidades.Item;
import entidades.Mesa;
import entidades.Pedido;
import entidades.Servicio;
import entidades.Producto;

import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

//para el RowsRenderer
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.time.LocalDateTime;
import java.util.Observable;
import java.util.Observer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import sockets.ClienteSocket;
import sockets.ServidorSocket;
import utiles.Utils;
//fin imports para el RowsRenderer

/**
 *
 * @author John David Molina Velarde, Leticia Mores, Enrique Germán Martínez, Carlos Eduardo Beltrán
 */
public class Recepcion extends javax.swing.JFrame implements Observer {
	private Servicio recepcionista;
	private static LinkedHashMap<Integer, Mesa> mapaMesas;
	private static LinkedHashMap<Integer, Servicio> mapaServicios;
	private DefaultTableModel modeloTablaMesas;
	private MesaData mesaData = new MesaData(); //conecto con la BD
	private ServidorSocket servidor;
	
	
	
	public Recepcion(Servicio recepcionista) {
		this.recepcionista = recepcionista;
		initComponents();
		
		modeloTablaMesas   = (DefaultTableModel) tablaMesas.getModel();
		
		//cambio los renderer para las tablaMesas para así elegir colores diferentes según el estado
		for (int columna = 0; columna <=3; columna++)
			tablaMesas.getColumnModel().getColumn(columna).setCellRenderer(new RendererMesas());
		
		// cargo los datos
		cargarServicios();
		cargarMesas();
		
		lanzarServidor();	// ejecuta un servidor un thread concurrente que escucha mensajes 
	} // constructor de Meseros
	
	
	
	
	
	//--------------------------- PARTE DE COMUNICACION VIA SOCKETS ----------------------------------------------------
	private void lanzarServidor(){
		//Inicialización de la parte de comunicación via sockets y sus threads respectivos.
		servidor = new ServidorSocket(recepcionista.getPuerto()); // el puerto donde escuchará
        servidor.addObserver(this);		// lo registramos como observador del servidor para que nos notifique mensajes
        Thread hilo = new Thread(servidor); // creamos un hilo paralelo para el servidor
        hilo.start();		
	}
	
	
	/**
	 * Este método es invocado por el ServidorSocket cuando recibe un mensaje de
	 * un servicio de despacho. Este método se encargará de llamar a los métodos para actualizar
	 * los datos en pantalla
	 * @param o
	 * @param mensaje 
	 */
	@Override
	public void update(Observable o, Object mensaje){ 
		//System.out.println("Me llego el mensaje: " + (String)mensaje);
		String s = (String) mensaje;
		if (s.startsWith("M"))
			Utils.sonido1("src/sonidos/tada.wav");
		actualizarPantalla(); // y acá toma las acciones correspondientes para actualizar pantalla
	};
	
	
	
	
	private void actualizarPantalla(){
		cargarMesas();
		habilitarBotones();
	}
	
	
	
	/**
	 * Este método crea un thread, un hilo paralelo de ejecución concurrente para
	 * enviar un mensaje al servicio de despacho del producto para avisarle de
	 * un cambio en el estado del item
	 * @param queServicio 
	 */
	private void comunicarConServicio(Servicio queServicio, String mensaje) {
		// esta es la parte de comunicación con la cocina
		ClienteSocket cliente = new ClienteSocket( //creo un cliente que pueda mandar a ese host en ese puerto
			queServicio.getHost(), queServicio.getPuerto(), 
			"R " + recepcionista.getIdServicio() + " " + mensaje); 
        Thread hilo = new Thread(cliente);	//creo un hilo para el clienteSocket
        hilo.start();						//ejecuto ese hilo para el cliente	
	}
	//--------------------------- FIN PARTE DE COMUNICACION VIA SOCKETS ----------------------------------------------------
	
	
	
	
	
	
	/** cargo el mapa de Servicios (cocina, bar, etc) */
	private void cargarServicios(){
		ServicioData servicioData = new ServicioData(); //conexión con la BD
		
		//Obtengo la lista de servicios que despachan productos (cocina, bar, etc.)
		List<Servicio> listaServicios = servicioData.getListaServiciosXCriterioDeBusqueda(
				-1, "", "", -1, Servicio.TipoServicio.MESERO, ServicioData.OrdenacionServicio.PORIDSERVICIO);
		
		//genero un mapa con las categorias
		mapaServicios = new LinkedHashMap();
		listaServicios.stream().forEach(servicio -> mapaServicios.put(servicio.getIdServicio(), servicio));
	} //cargarServicios
	
	
	
	
	/** cargo el mapa de mesas y la tabla de mesas que corresponden a ese mesero */
	private void cargarMesas(){
		//averiguo en que orden
		MesaData.OrdenacionMesa ordenacion;
		if (cbOrden.getSelectedIndex() == 0)
			ordenacion = MesaData.OrdenacionMesa.PORIDMESA;
        else if (cbOrden.getSelectedIndex() == 1)
			ordenacion = MesaData.OrdenacionMesa.PORCAPACIDAD;
        else if (cbOrden.getSelectedIndex() == 2)
			ordenacion = MesaData.OrdenacionMesa.PORESTADO;
		else if (cbOrden.getSelectedIndex() == 3)
			ordenacion = MesaData.OrdenacionMesa.PORMESERO;
		else
			ordenacion = MesaData.OrdenacionMesa.PORIDMESA;
		
		//Obtengo la lista de meses que corresponden a este mesero.
		List<Mesa> listaMesas = mesaData.getListaMesasXCriterioDeBusqueda(
				-1, -1, null, -1, ordenacion);		//MesaData.OrdenacionMesa.PORIDMESA);
		
		//genero un mapa con las mesas de este mesero.
		mapaMesas = new LinkedHashMap();
		listaMesas.stream().forEach(mesa -> mapaMesas.put(mesa.getIdMesa(), mesa));
		
		int filaSeleccionada = tablaMesas.getSelectedRow(); //conservo la anterior mesa seleccionada
		
		//borro las filas de la tabla mesas
		for (int fila = modeloTablaMesas.getRowCount() -  1; fila >= 0; fila--)
			modeloTablaMesas.removeRow(fila);
		
		//cargo esas mesas a la tabla de mesas
		int libres = 0; 
		int ocupadas = 0;
		int atendidas = 0;
		for (Mesa mesa : listaMesas) {
			if (mesa.getEstado() == Mesa.EstadoMesa.LIBRE)
				libres++;
			else if (mesa.getEstado() == Mesa.EstadoMesa.OCUPADA)
				ocupadas++;
			else 
				atendidas++;
			
			Servicio mesero = mapaServicios.get(mesa.getIdMesero());
			if (mesero != null)
				modeloTablaMesas.addRow(new Object[] {
					mesa.getIdMesa(), 
					mesa.getCapacidad(),
					mesa.getEstado(),
					(mesero == null) ? "" : mesero.toString()
				} );
		}
		
		//si la fila que tenia seleccionada sigue siendo válida
		if (filaSeleccionada >= 0 && filaSeleccionada < tablaMesas.getRowCount())
			tablaMesas.setRowSelectionInterval(filaSeleccionada, filaSeleccionada); //restauro la fila que tenía seleccionada
		//else
		//	tablaMesas.removeRowSelectionInterval(0, tablaMesas.getRowCount()-1); // borro todas las selecciones de la tabla de pedidos
		
		//actualizo las etiquetas del encabezamiento
		lblCantLibres.setText("" + libres);
		lblCantOcupadas.setText("" + ocupadas);
		lblCantAtendidas.setText("" + atendidas);
	} //cargar mesas
	
	
	
	
	
	
	

	/**
	 * Para poder poner el ícono de la aplicación en la ventana
	 * Para usar el ícono, ir a: JFrame -> Property -> iconImage -> click en [...]
	 * SetForm iconImage property using: [Value from existing component v]
	 * -> O property [...] -> component properties: iconImage
	 * @return 
	 */	
	@Override
	public Image getIconImage() { // defino el icono del jFrame
		Image retValue = Toolkit.getDefaultToolkit().
				getImage(ClassLoader.getSystemResource("imagenes/iconoComida.png")); //icono de la aplicación
		return retValue;
	} // getIconImage
	
	
	
	
	//=======================================================================================
	//=======================================================================================
	
	
	
	
	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelMesas = new javax.swing.JScrollPane();
        tablaMesas = new javax.swing.JTable();
        botoneraVertical = new javax.swing.JPanel();
        btnOcupar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        panelEncabezamientoItems = new javax.swing.JPanel();
        lblItemsDelPedido = new javax.swing.JLabel();
        lblCantLibres = new javax.swing.JLabel();
        lblCantOcupadas = new javax.swing.JLabel();
        lblCantAtendidas = new javax.swing.JLabel();
        cbOrden = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setIconImage(getIconImage());

        panelMesas.setBackground(new java.awt.Color(153, 153, 255));

        tablaMesas.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tablaMesas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Id Mesa", "Capacidad", "Estado", "Mesero"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.Object.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaMesas.setRowHeight(48);
        tablaMesas.getTableHeader().setReorderingAllowed(false);
        tablaMesas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaMesasMouseClicked(evt);
            }
        });
        panelMesas.setViewportView(tablaMesas);
        if (tablaMesas.getColumnModel().getColumnCount() > 0) {
            tablaMesas.getColumnModel().getColumn(0).setMinWidth(60);
            tablaMesas.getColumnModel().getColumn(0).setPreferredWidth(60);
            tablaMesas.getColumnModel().getColumn(0).setMaxWidth(60);
            tablaMesas.getColumnModel().getColumn(1).setMinWidth(60);
            tablaMesas.getColumnModel().getColumn(1).setPreferredWidth(60);
            tablaMesas.getColumnModel().getColumn(1).setMaxWidth(60);
            tablaMesas.getColumnModel().getColumn(2).setMinWidth(80);
            tablaMesas.getColumnModel().getColumn(2).setPreferredWidth(80);
            tablaMesas.getColumnModel().getColumn(2).setMaxWidth(80);
            tablaMesas.getColumnModel().getColumn(3).setPreferredWidth(100);
        }

        botoneraVertical.setBackground(new java.awt.Color(153, 153, 255));

        btnOcupar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/ocuparMesa1_32x32.png"))); // NOI18N
        btnOcupar.setText("Ocupar Mesa");
        btnOcupar.setEnabled(false);
        btnOcupar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOcupar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOcupar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOcuparActionPerformed(evt);
            }
        });

        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/cancelar3_32x32.png"))); // NOI18N
        btnCancelar.setText("Cancelar Mesa");
        btnCancelar.setEnabled(false);
        btnCancelar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCancelar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/salida1_32x32.png"))); // NOI18N
        btnSalir.setText("Salir");
        btnSalir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSalir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout botoneraVerticalLayout = new javax.swing.GroupLayout(botoneraVertical);
        botoneraVertical.setLayout(botoneraVerticalLayout);
        botoneraVerticalLayout.setHorizontalGroup(
            botoneraVerticalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(botoneraVerticalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(botoneraVerticalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                    .addComponent(btnOcupar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSalir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        botoneraVerticalLayout.setVerticalGroup(
            botoneraVerticalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(botoneraVerticalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnOcupar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancelar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSalir)
                .addContainerGap())
        );

        panelEncabezamientoItems.setBackground(new java.awt.Color(153, 153, 255));

        lblItemsDelPedido.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblItemsDelPedido.setText("Mesas");

        lblCantLibres.setText("CantLibres");
        lblCantLibres.setBorder(javax.swing.BorderFactory.createTitledBorder("Libres"));
        lblCantLibres.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        lblCantOcupadas.setText("Ocupadas");
        lblCantOcupadas.setBorder(javax.swing.BorderFactory.createTitledBorder("Ocupadas"));
        lblCantOcupadas.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        lblCantAtendidas.setText("Atendidas");
        lblCantAtendidas.setBorder(javax.swing.BorderFactory.createTitledBorder("Atendidas"));
        lblCantAtendidas.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        cbOrden.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "por Id Mesa", "por Capacidad", "por Estado", "por Mesero" }));
        cbOrden.setBorder(javax.swing.BorderFactory.createTitledBorder("Ordenar"));
        cbOrden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbOrdenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelEncabezamientoItemsLayout = new javax.swing.GroupLayout(panelEncabezamientoItems);
        panelEncabezamientoItems.setLayout(panelEncabezamientoItemsLayout);
        panelEncabezamientoItemsLayout.setHorizontalGroup(
            panelEncabezamientoItemsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEncabezamientoItemsLayout.createSequentialGroup()
                .addGap(237, 237, 237)
                .addComponent(lblItemsDelPedido)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(panelEncabezamientoItemsLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(lblCantLibres, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblCantOcupadas, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblCantAtendidas, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cbOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27))
        );
        panelEncabezamientoItemsLayout.setVerticalGroup(
            panelEncabezamientoItemsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelEncabezamientoItemsLayout.createSequentialGroup()
                .addComponent(lblItemsDelPedido)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEncabezamientoItemsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelEncabezamientoItemsLayout.createSequentialGroup()
                        .addGap(0, 2, Short.MAX_VALUE)
                        .addGroup(panelEncabezamientoItemsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblCantLibres)
                            .addComponent(lblCantOcupadas)
                            .addComponent(lblCantAtendidas)))
                    .addComponent(cbOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelEncabezamientoItems, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelMesas, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(botoneraVertical, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelEncabezamientoItems, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelMesas, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE)
                    .addComponent(botoneraVertical, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	
	
	//=======================================================================================
	//=======================================================================================
	
	
	
	/**
	 * Dada una mesa LIBRE seleccionada de la tablaMesas, cambia su estado a OCUPADA
	 * @param evt 
	 */
    private void btnOcuparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOcuparActionPerformed
        int numFila = tablaMesas.getSelectedRow();
		if (numFila == -1){ // si no hay alguna fila seleccionada
            btnOcupar.setEnabled(false); // deshabilito botón incluir
			btnCancelar.setEnabled(false);
			return;
        }
		
		//aca hay una mesa seleccionada
		Mesa mesa = mapaMesas.get(tablaMesas.getValueAt(numFila, 0));
		if (mesa.getEstado() == Mesa.EstadoMesa.LIBRE) {
			mesa.setEstado(Mesa.EstadoMesa.OCUPADA);
			mesaData.modificarMesa(mesa);
			comunicarConServicio(mapaServicios.get( mesa.getIdMesero() ),
						"O " + mesa.getIdMesa());
		} else 
			Utils.sonido1("src/sonidos/chord.wav");
		
		actualizarPantalla();
    }//GEN-LAST:event_btnOcuparActionPerformed

	
	
	
	
	/**
	 * Dada una mesa OCUPADA seleccionada de la tablaMesas, cambia su estado a LIBRE
	 * @param evt 
	 */
    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
		int numFila = tablaMesas.getSelectedRow();
		if (numFila == -1){ // si no hay alguna fila seleccionada
            btnOcupar.setEnabled(false); // deshabilito botón incluir
			btnCancelar.setEnabled(false);
			return;
        }
		
		//aca hay una mesa seleccionada
		Mesa mesa = mapaMesas.get(tablaMesas.getValueAt(numFila, 0));
		if (mesa.getEstado() == Mesa.EstadoMesa.OCUPADA) {
			mesa.setEstado(Mesa.EstadoMesa.LIBRE);
			mesaData.modificarMesa(mesa);
			comunicarConServicio(mapaServicios.get( mesa.getIdMesero() ),
						"L " + mesa.getIdMesa());
		} else 
			Utils.sonido1("src/sonidos/chord.wav");
		
		actualizarPantalla();
    }//GEN-LAST:event_btnCancelarActionPerformed

	
	

	
	
	
	

	/**
	 * Habilita / deshabilita los botones Ocupar y Cancelar según el estado
	 */
	public void habilitarBotones(){
		int numFila = tablaMesas.getSelectedRow();
		btnOcupar.setEnabled(false);
		btnCancelar.setEnabled(false);
		
		if (numFila != -1 && ((Mesa.EstadoMesa)tablaMesas.getValueAt(numFila, 2))== Mesa.EstadoMesa.LIBRE )
			btnOcupar.setEnabled(true);
		
		if (numFila != -1 && ((Mesa.EstadoMesa)tablaMesas.getValueAt(numFila, 2))== Mesa.EstadoMesa.OCUPADA )
			btnCancelar.setEnabled(true);
	}
	
	
	
	/** 
	 * Se hizo click sobre una mesa seleccionandola, por lo que hay que cargar
	 * los pedidos correspondientes a dicha mesa y reflejar los nuevos datos
	 * en el encabezamiento.
	 * @param evt 
	 */
    private void tablaMesasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaMesasMouseClicked
        habilitarBotones();
    }//GEN-LAST:event_tablaMesasMouseClicked

	
	
	
	
	

	
	/**
	 * Cierra la ventana
	 * @param evt 
	 */
    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
		servidor.pararEjecucion(); // detiene el servidor que escucha mensajes en la red.
		comunicarConServicio(recepcionista, "Desconectando el servidor..."); //mando este mensaje para que el servidor deje de esperar y vea que hay que parar la ejecución
		dispose();
    }//GEN-LAST:event_btnSalirActionPerformed

    private void cbOrdenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbOrdenActionPerformed
        actualizarPantalla();
    }//GEN-LAST:event_cbOrdenActionPerformed

	
	
	
	
	
	//=========================================================================================
	//=========================================================================================
	
	
	
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
			java.util.logging.Logger.getLogger(Meseros.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(Meseros.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(Meseros.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(Meseros.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Meseros(null).setVisible(true);
			}
		});
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel botoneraVertical;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnOcupar;
    private javax.swing.JButton btnSalir;
    private javax.swing.JComboBox<String> cbOrden;
    private javax.swing.JLabel lblCantAtendidas;
    private javax.swing.JLabel lblCantLibres;
    private javax.swing.JLabel lblCantOcupadas;
    private javax.swing.JLabel lblItemsDelPedido;
    private javax.swing.JPanel panelEncabezamientoItems;
    private javax.swing.JScrollPane panelMesas;
    private javax.swing.JTable tablaMesas;
    // End of variables declaration//GEN-END:variables


	
	
	//========================================================================================================
	//========================================================================================================
	
	
	
	/**
	 * Renderer de celdas de la tablaMesas. Pone el color según el estado de la
	 * mesa (Libre, Ocupada, Atendida). 
	 * También permite gestionar los eventos realizados sobre la tabla.
	 * 
	 * Para usarla, en la definicion de la tabla se pone:
	 *		tabla.getColumnModel().getColumn(3).setCellRenderer(new generalRenderer());
	 *	de esa manera aplica el renderer a la columna, los colores son diferentes si la celda esta seleccionada o no.
	 */
	class RendererMesas extends JLabel implements TableCellRenderer {    
		//Font f = new Font( "Helvetica",Font.PLAIN,10 );
		Color colorSeleccionado = new Color(184,207,229); //new Color(0,120,215); //new Color(117, 204, 169);
		Color colorGeneral = Color.WHITE; //new Color(255,255,255); //new Color(225, 244, 238);
		Color colorLibre = Color.WHITE;
		Color colorOcupada = Color.RED;
		Color colorAtendida = Color.GREEN;

		public RendererMesas() {
			setOpaque(true);
		}

		
		/*
		 * Este metodo controla toda la tabla, podemos obtener el valor que contiene
		 * definir que celda esta seleccionada, la fila y columna al tener el foco en ella.
		 * Cada evento sobre la tabla invocara a este metodo
		 */
		public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean isSelected, boolean hasFocus, int row, int column) {
			//defino el color de fondo según el tipo
			if (isSelected) {
				setBackground(colorSeleccionado);
				
				//Defino un borde alrededor de la fila seleccionada
				//if (column==0) //columna izquierda, con borde izquierdo
				//	setBorder(javax.swing.BorderFactory.createMatteBorder(6, 4, 6, 0, colorSeleccionado)); //top, left, bottom, right, colorGeneral
				//else if (column==5) //columna derecha, con borde derecho
				//	setBorder(javax.swing.BorderFactory.createMatteBorder(6, 0, 6, 4, colorSeleccionado));
				//else //columnas del medio, sin borde izquierdo ni derecho, solo arriba y abajo
				//	setBorder(javax.swing.BorderFactory.createMatteBorder(6, 0, 6, 0, colorSeleccionado));
			}
			else  if ( (Mesa.EstadoMesa) tabla.getValueAt(row, 2) == Mesa.EstadoMesa.LIBRE ) 
				setBackground(colorLibre);
			else if ( (Mesa.EstadoMesa) tabla.getValueAt(row, 2) == Mesa.EstadoMesa.OCUPADA ) 
				setBackground(colorOcupada);
			else if ( (Mesa.EstadoMesa) tabla.getValueAt(row, 2) == Mesa.EstadoMesa.ATENDIDA )  
				setBackground(colorAtendida);
			else 
				setBackground(Color.MAGENTA); //esto nunca debería pasar.
			
			//defino la alineación horizontal
			if (column == 0 || column==1)
				setHorizontalAlignment( JLabel.CENTER );
			else
				setHorizontalAlignment( JLabel.LEFT );
			
			//escribo el valor pasado en valor
			try {
				//setFont(f);
				setText(valor.toString());
			} catch (NullPointerException npe) {
				System.out.println("Error al escribir celda " + row + "," + column + ":" + valor.toString() + "." + npe);
				setText("0");
			}
			return this;
		}
	} //class rendererItems
	
} //class Meseros


