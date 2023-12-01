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

import accesoadatos.ItemData;
import accesoadatos.MesaData;
import accesoadatos.PedidoData;
import accesoadatos.ProductoData;
import accesoadatos.ServicioData;
import entidades.Categoria;
import entidades.Item;
import entidades.Mesa;
import entidades.Pedido;
import entidades.Servicio;
import entidades.Producto;

import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.table.DefaultTableModel;

//para el RowsRenderer
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JLabel;
import javax.swing.JTable;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.table.TableCellRenderer;
import sockets.ClienteSocket;
import sockets.ServidorSocket;
import utiles.Utils;
//fin imports para el RowsRenderer

/**
 *
 * @author John David Molina Velarde, Leticia Mores, Enrique Germán Martínez, Carlos Eduardo Beltrán
 */
public class Despacho extends javax.swing.JFrame implements Observer {
	private Servicio servicio;
	private static LinkedHashMap<Integer, Pedido> mapaPedidos;
	private static LinkedHashMap<Integer, Servicio> mapaServicios;
	private static LinkedHashMap<Integer, Producto> mapaProductos;
	private static LinkedHashMap<Integer, Item> mapaItems;
	private DefaultTableModel modeloTablaItems;
	private ItemData itemData = new ItemData(); //conecto con la BD
	private PedidoData pedidoData = new PedidoData();
	private ProductoData productoData = new ProductoData();
	private ServicioData servicioData = new ServicioData();
	private ServidorSocket servidor;
	
	
	
	public Despacho(Servicio servicio) {
		this.servicio = servicio;
		initComponents();
		
		modeloTablaItems   = (DefaultTableModel) tablaItems.getModel();
		
		//defino el renderer de la tabla de items para poder cambiar las características de la tabla
		for (int columna = 0; columna <=6; columna++)
			tablaItems.getColumnModel().getColumn(columna).setCellRenderer(new RendererItems());

		//cargo los datos y actualizo la pantalla
		cargarServicios();
		cargarPedidos();
		cargarProductos();
		actualizarPantalla();
		
		lanzarServidor(); // ejecuta un servidor un thread concurrente que escucha mensajes 
	} // constructor de Meseros
	
	
	
	//--------------------------- PARTE DE COMUNICACION VIA SOCKETS ----------------------------------------------------
	private void lanzarServidor() {
		//Inicialización de la parte de comunicación via sockets y sus threads respectivos.
		servidor = new ServidorSocket(servicio.getPuerto()); // el puerto donde escuchará
        servidor.addObserver(this);		// lo registramos como observador del servidor para que nos notifique mensajes
        Thread hilo = new Thread(servidor); // creamos un hilo paralelo para el servidor
        hilo.start();					// ejecutamos ese hilo del servidor
	}
	
	
	
	
	/**
	 * Este método es invocado por el ServidorSocket cuando recibe un mensaje de
	 * un mesero. Este método se encargará de llamar a los métodos para actualizar
	 * los datos en pantalla
	 * @param o
	 * @param mensaje 
	 */
	@Override
	public void update(Observable o, Object mensaje){ 
		//System.out.println("Me llego el mensaje: " + (String)mensaje);
		String s = (String) mensaje;
		System.out.println("En despacho mensaje recibido: " + s);
		if (s.startsWith("M"))
			Utils.sonido1("src/sonidos/Campanilla.wav");
		actualizarPantalla(); // y acá toma las acciones correspondientes para actualizar pantalla
	};



	
	private void actualizarPantalla(){
		cargarItems();
		deshabilitarBotonesItems();
		mostrarLabelsEncabezamientoItems();
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
			"S " + servicio.getIdServicio() + " " + mensaje);
        Thread hilo = new Thread(cliente);	//creo un hilo para el clienteSocket
        hilo.start();						//ejecuto ese hilo para el cliente	
	}
	//--------------------------- FIN PARTE DE COMUNICACION VIA SOCKETS ----------------------------------------------------
		
	
	
	/** cargo el mapa de Servicios (cocina, bar, etc) */
	private void cargarServicios(){
		//Obtengo la lista de servicios que despachan productos (cocina, bar, etc.)
		List<Servicio> listaServicios = servicioData.getListaServiciosXCriterioDeBusqueda(
				-1, "", "", -1, Servicio.TipoServicio.MESERO, ServicioData.OrdenacionServicio.PORIDSERVICIO);

		//genero un mapa con las categorias
		mapaServicios = new LinkedHashMap();
		listaServicios.stream().forEach(mesero -> mapaServicios.put(mesero.getIdServicio(), mesero));
	} //cargarServicios
	
	
	/** cargo el mapa de Productos */
	private void cargarProductos(){
		//Obtengo la lista de productos
		List<Producto> listaProductos = productoData.getListaProductosXCriterioDeBusqueda(
				//idProducto, nombre, stock, precio, disponible, idCategoria, despachadoPor, ordenacion
				-1,			   "",	   -1,   -1.0,	  true,		  -1,			-1,				ProductoData.OrdenacionProducto.PORNOMBRE);
		
		//genero un mapa con los productos
		mapaProductos = new LinkedHashMap();
		listaProductos.stream().forEach(producto -> mapaProductos.put(producto.getIdProducto(), producto));
	} //cargarProductos
	
	
	
	
	/** cargo el mapa de mesas y la tabla de mesas que corresponden a ese mesero  */
	private void cargarPedidos(){
		PedidoData pedidoData = new PedidoData();
		
		//Obtengo la lista de pedidos activos 
		List<Pedido> listaPedidos = pedidoData.getListaPedidosXCriterioDeBusqueda(
		//	idPedido, idMesa,	idMesero, fechaDesde, fechaHasta,	  estado,						OrdenacionPedido ordenacion
			-1,		 -1,		-1,			null,		null,		Pedido.EstadoPedido.ACTIVO,	PedidoData.OrdenacionPedido.PORIDPEDIDO);
		
		//genero un mapa con los pedidos de esa mesa.
		mapaPedidos = new LinkedHashMap();
		listaPedidos.stream().forEach( pedido -> mapaPedidos.put(pedido.getIdPedido(), pedido) );
	} //cargarPedidos
	
	
	
	/**
	 * cargo la lista de items
	 */
	private void cargarItems(){
		
		//Obtengo la lista de items cancelados.
		List<Item> listaItems = itemData.getListaItemsSCXIdServicio(
				servicio.getIdServicio(), ItemData.OrdenacionItem.PORIDPEDIDO);
		
		//cargo la lista de items al mapaItems
		mapaItems = new LinkedHashMap();
		listaItems.stream().forEach(item -> mapaItems.put(item.getIdItem(), item));
				
		//borro las filas de la tabla items
		for (int fila = modeloTablaItems.getRowCount() -  1; fila >= 0; fila--)
			modeloTablaItems.removeRow(fila);
		
		//cargo esos items a la tabla de items
		listaItems.stream().forEach(item -> {
			Pedido pedido = mapaPedidos.get(item.getIdPedido());         // averiguo el pedido
			if (pedido == null) // si el pedido no está en el mapa (es un pedido cancelado), lo buscamo en la BD.
				pedido = pedidoData.getPedido(item.getIdPedido());
			Producto producto = mapaProductos.get(item.getIdProducto()); // averiguo el producto
			Servicio mesero = mapaServicios.get(pedido.getIdMesero());	 // averiguo el mesero
			
			//lo agrego a la tablaItems
			modeloTablaItems.addRow(new Object[] {
				item.getIdItem(),	//idItem
				producto.toString(),//producto
				item.getCantidad(),	//cantidad
				item.getEstado(),	//estado
				item.getIdPedido(),	//idpedido
				mesero.toString(),	//Mesero
				pedido.getIdMesa()	//idMesa
			} ); 
		} );
	} //cargarItems
	
	
	
	/** dada la fila de tablaItems devuelve el IdItem */
	private int tablaItemsGetIdItem(int numfila) {
		return (Integer)tablaItems.getValueAt(numfila, 0);
	} // tablaItemsGetIdItem
	
	
	
	/** dada la fila de tablaItems devuelve el Producto */
	private Producto tablaItemsGetProducto(int numfila) {
		return mapaProductos.get(mapaItems.get(tablaItemsGetIdItem(numfila)).getIdProducto());
	} //tablaItemsGetProducto
	
	
	
	/** dada la fila de tablaItems devuelve la Cantidad */
	private int tablaItemsGetCantidad(int numfila) {
		return (Integer)tablaItems.getValueAt(numfila, 2);
	} // tablaItemsGetCantidad
	
	
	
	/** dada la fila de tablaItems, devuelve el Estado */
	private Item.EstadoItem tablaItemsGetEstado(int numfila) {
		return (Item.EstadoItem)tablaItems.getValueAt(numfila, 3);
	} //tablaItemsGetEstado
	
	
	/** dada la fila de tablaItems, devuelve el pedido */
	private Pedido tablaItemsGetPedido(int numfila) {
		int idPedido = (Integer)tablaItems.getValueAt(numfila, 4);
		return mapaPedidos.get(idPedido);
	} //tablaItemsGetPedido
	

	
	
	/**
	 * Cuando no hay un itemSeleccionado seleccionado se deshabilitan los botones relacionados a los items
	 */
	private void deshabilitarBotonesItems(){
		btnEliminarCancelados.setEnabled(false);
		btnDespachar.setEnabled(false);
	} //deshabilitarBotonesItems
	
	
	
	/**
	 * Cuando se selecciona un item se habilitan los botones relacionados a los items
	 */
	private void habilitarBotonesItems(){
		btnEliminarCancelados.setEnabled(true);
		btnDespachar.setEnabled(true);
	} //habilitarBotonesItems
	

	
	
	/**
	 * Muestra los datos del encabezamiento con el servicio correspondiente
	 */
	private void mostrarLabelsEncabezamientoItems(){
		lblServicio.setText(servicio.getIdServicio() + ": " + servicio.getNombreServicio());
	} // mostrarLabelsEncabezamiento
	
	
	
	/**
	 * Para poder poner el ícono de la aplicación en la ventana.
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

        panelItems = new javax.swing.JScrollPane();
        tablaItems = new javax.swing.JTable();
        botoneraVertical = new javax.swing.JPanel();
        btnDespachar = new javax.swing.JButton();
        btnEliminarCancelados = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        panelEncabezamientoItems = new javax.swing.JPanel();
        lblServicio = new javax.swing.JLabel();
        lblItemsDelPedido = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setIconImage(getIconImage());

        panelItems.setBackground(new java.awt.Color(153, 153, 255));

        tablaItems.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Producto", "Cant", "Estado", "Id Pedido", "Mesero", "Mesa"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaItems.setRowHeight(32);
        tablaItems.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaItemsMouseClicked(evt);
            }
        });
        panelItems.setViewportView(tablaItems);
        if (tablaItems.getColumnModel().getColumnCount() > 0) {
            tablaItems.getColumnModel().getColumn(0).setResizable(false);
            tablaItems.getColumnModel().getColumn(0).setPreferredWidth(20);
            tablaItems.getColumnModel().getColumn(1).setPreferredWidth(200);
            tablaItems.getColumnModel().getColumn(1).setMaxWidth(250);
            tablaItems.getColumnModel().getColumn(2).setPreferredWidth(50);
            tablaItems.getColumnModel().getColumn(2).setMaxWidth(50);
            tablaItems.getColumnModel().getColumn(3).setPreferredWidth(80);
            tablaItems.getColumnModel().getColumn(3).setMaxWidth(140);
            tablaItems.getColumnModel().getColumn(4).setResizable(false);
            tablaItems.getColumnModel().getColumn(4).setPreferredWidth(30);
            tablaItems.getColumnModel().getColumn(5).setResizable(false);
            tablaItems.getColumnModel().getColumn(5).setPreferredWidth(200);
            tablaItems.getColumnModel().getColumn(6).setResizable(false);
            tablaItems.getColumnModel().getColumn(6).setPreferredWidth(20);
        }

        botoneraVertical.setBackground(new java.awt.Color(153, 153, 255));

        btnDespachar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/mesero1_32x32.png"))); // NOI18N
        btnDespachar.setText("Despachar Producto");
        btnDespachar.setEnabled(false);
        btnDespachar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDespachar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDespachar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDespacharActionPerformed(evt);
            }
        });

        btnEliminarCancelados.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/borrar2_32x32.png"))); // NOI18N
        btnEliminarCancelados.setText("Eliminar Cancelado");
        btnEliminarCancelados.setEnabled(false);
        btnEliminarCancelados.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEliminarCancelados.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEliminarCancelados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarCanceladosActionPerformed(evt);
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
                .addComponent(btnDespachar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnEliminarCancelados, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        botoneraVerticalLayout.setVerticalGroup(
            botoneraVerticalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(botoneraVerticalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(botoneraVerticalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSalir)
                    .addComponent(btnDespachar)
                    .addComponent(btnEliminarCancelados))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelEncabezamientoItems.setBackground(new java.awt.Color(153, 153, 255));

        lblServicio.setText("9 Cocina");
        lblServicio.setBorder(javax.swing.BorderFactory.createTitledBorder("Servicio"));

        lblItemsDelPedido.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblItemsDelPedido.setText("Items a Despachar");

        javax.swing.GroupLayout panelEncabezamientoItemsLayout = new javax.swing.GroupLayout(panelEncabezamientoItems);
        panelEncabezamientoItems.setLayout(panelEncabezamientoItemsLayout);
        panelEncabezamientoItemsLayout.setHorizontalGroup(
            panelEncabezamientoItemsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEncabezamientoItemsLayout.createSequentialGroup()
                .addGroup(panelEncabezamientoItemsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelEncabezamientoItemsLayout.createSequentialGroup()
                        .addGap(289, 289, 289)
                        .addComponent(lblItemsDelPedido))
                    .addGroup(panelEncabezamientoItemsLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(lblServicio, javax.swing.GroupLayout.PREFERRED_SIZE, 337, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelEncabezamientoItemsLayout.setVerticalGroup(
            panelEncabezamientoItemsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEncabezamientoItemsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblServicio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblItemsDelPedido)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(panelEncabezamientoItems, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelItems, javax.swing.GroupLayout.DEFAULT_SIZE, 714, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(botoneraVertical, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelEncabezamientoItems, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelItems, javax.swing.GroupLayout.PREFERRED_SIZE, 387, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(botoneraVertical, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	
	
	//=======================================================================================
	//=======================================================================================
	
	
	
	/**
	 * Cambia el estado de los Items seleccinados que sean SOLICITADO a DESPACHADO.
	 * @param evt 
	 */
    private void btnDespacharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDespacharActionPerformed
        if (tablaItems.getSelectedRow() == -1){ 
			deshabilitarBotonesItems(); 
			Utils.sonido1("src/sonidos/chord.wav");
			return;
		}
			
		int[] arregloFilasItemsSeleccionados = tablaItems.getSelectedRows();
		for (int numfilaItemSeleccionado:arregloFilasItemsSeleccionados) {		  //recorro todas las filas seleccionadas de la tablaItems
			int idItemSeleccionado = tablaItemsGetIdItem(numfilaItemSeleccionado);//averiguamos el idItemSeleccionado de esa fila seleccionada
			Item itemSeleccionado = mapaItems.get(idItemSeleccionado);			  //averiguamos el itemSeleccionado de esa fila seleccionada
			
			if (itemSeleccionado.getEstado() == Item.EstadoItem.SOLICITADO) { // si es SOLICITADO lo puedo modificar
				itemSeleccionado.setEstado(Item.EstadoItem.DESPACHADO);		  //le pongo como DESPACHADO
				itemData.modificarItem(itemSeleccionado);					  // modifico el item en la bd
				comunicarConServicio(mapaServicios.get( mapaPedidos.get(itemSeleccionado.getIdPedido()).getIdMesero() ),
						"C " + itemSeleccionado.getIdItem());
			} else {//si no es solicitado, no lo puedo modificar. 
				Utils.sonido1("src/sonidos/chord.wav");
			}//else
		} //for
		actualizarPantalla();
    }//GEN-LAST:event_btnDespacharActionPerformed

	
	
	
	
	/**
	 * Cambia el estado de los Items seleccinados que sean CANCELADO a CANCELADOVISTO.
	 * @param evt 
	 */
    private void btnEliminarCanceladosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarCanceladosActionPerformed
        if (tablaItems.getSelectedRow() == -1){ 
			deshabilitarBotonesItems(); 
			Utils.sonido1("src/sonidos/chord.wav");
			return;
		}
			
		int[] arregloFilasItemsSeleccionados = tablaItems.getSelectedRows();
		for (int numfilaItemSeleccionado:arregloFilasItemsSeleccionados) {		  //recorro todas las filas seleccionadas de la tablaItems
			int idItemSeleccionado = tablaItemsGetIdItem(numfilaItemSeleccionado);//averiguamos el idItemSeleccionado de esa fila seleccionada
			Item itemSeleccionado = mapaItems.get(idItemSeleccionado);			  //averiguamos el itemSeleccionado de esa fila seleccionada
			
			if (itemSeleccionado.getEstado() == Item.EstadoItem.CANCELADO) { // si es CANCELADO lo puedo modificar
				itemSeleccionado.setEstado(Item.EstadoItem.CANCELADOVISTO);  //le pongo como CANCELADOVISTO
				itemData.modificarItem(itemSeleccionado);					 // modifico el item en la bd
				comunicarConServicio(mapaServicios.get( mapaPedidos.get(itemSeleccionado.getIdPedido()).getIdMesero() ),
						"C " + itemSeleccionado.getIdItem());
			} else {//si no es cancelado, no lo puedo modificar. 
				Utils.sonido1("src/sonidos/chord.wav");
			}//else
		} //for
		
		actualizarPantalla();
    }//GEN-LAST:event_btnEliminarCanceladosActionPerformed

	
	
	
	
	/**
	 * Se hizo click sobre un item para seleccionarlo, por lo que ya se pueden
	 * habilitar los botones de items que operan sobre esos items seleccionados.
	 * @param evt 
	 */
    private void tablaItemsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaItemsMouseClicked
        habilitarBotonesItems();
    }//GEN-LAST:event_tablaItemsMouseClicked

	
	
	
	
	/**
	 * Cierra la ventana
	 * @param evt 
	 */
    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
		servidor.pararEjecucion(); // detiene el servidor que escucha mensajes en la red.
		comunicarConServicio(servicio, "Desconectando el servidor..."); //mando este mensaje para que el servidor deje de esperar y vea que hay que parar la ejecución
		dispose(); // cierra la ventana
    }//GEN-LAST:event_btnSalirActionPerformed



	
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
    private javax.swing.JButton btnDespachar;
    private javax.swing.JButton btnEliminarCancelados;
    private javax.swing.JButton btnSalir;
    private javax.swing.JLabel lblItemsDelPedido;
    private javax.swing.JLabel lblServicio;
    private javax.swing.JPanel panelEncabezamientoItems;
    private javax.swing.JScrollPane panelItems;
    private javax.swing.JTable tablaItems;
    // End of variables declaration//GEN-END:variables


	
	//========================================================================================================
	//========================================================================================================
	
	
	
	
	/**
	 * Renderer de celdas de la tablaItems pone el color según el estado del 
	 * Item (Solicitado, Cancelado). La info la obtiene de mapaItems.
	 * También permite gestionar los eventos realizados sobre la tabla.
	 * 
	 * Para usarla, en la definicion de la tabla se pone:
	 *		tabla.getColumnModel().getColumn(3).setCellRenderer(new generalRenderer());
	 *	de esa manera aplica el renderer a la columna, los colores son diferentes si la celda esta seleccionada o no.
	 */
	class RendererItems extends JLabel implements TableCellRenderer {    
		//Font f = new Font( "Helvetica",Font.PLAIN,10 );
		Color colorSeleccionado = new Color(184,207,229); //new Color(0,120,215); //new Color(117, 204, 169);
		Color colorGeneral = Color.WHITE; //new Color(255,255,255); //new Color(225, 244, 238);
		Color colorAnotado = Color.WHITE;
		Color colorSolicitado = Color.YELLOW;
		Color colorDespachado = Color.RED;
		Color colorEntregado = Color.GREEN;
		Color colorCancelado = Color.LIGHT_GRAY;
		Color colorCanceladoVisto = Color.CYAN;

		public RendererItems() {
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
				// setBackground(colorSeleccionado);
				//BevelBorder, SoftBevelBorder, EtchedBorder, LineBorder, TitledBorder, and MatteBorder.
				//this.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
				//this.setBorder(javax.swing.BorderFactory.createLineBorder(colorSeleccionado, 2));
				
				//Defino un borde alrededor de la fila seleccionada
				if (column==0) //columna izquierda, con borde izquierdo
					setBorder(javax.swing.BorderFactory.createMatteBorder(6, 4, 6, 0, colorSeleccionado)); //top, left, bottom, right, colorGeneral
				else if (column==6) //columna derecha, con borde derecho
					setBorder(javax.swing.BorderFactory.createMatteBorder(6, 0, 6, 4, colorSeleccionado));
				else //columnas del medio, sin borde izquierdo ni derecho, solo arriba y abajo
					setBorder(javax.swing.BorderFactory.createMatteBorder(6, 0, 6, 0, colorSeleccionado));
			}
			else
				this.setBorder(null);
			
			if ( (Item.EstadoItem) tabla.getValueAt(row, 3) == Item.EstadoItem.ANOTADO ) 
				setBackground(colorAnotado);
			else if ( (Item.EstadoItem) tabla.getValueAt(row, 3) == Item.EstadoItem.SOLICITADO ) 
				setBackground(colorSolicitado);
			else if ( (Item.EstadoItem) tabla.getValueAt(row, 3) == Item.EstadoItem.DESPACHADO ) 
				setBackground(colorDespachado);
			else if ( (Item.EstadoItem) tabla.getValueAt(row, 3) == Item.EstadoItem.ENTREGADO ) 
				setBackground(colorEntregado);
			else if ( (Item.EstadoItem) tabla.getValueAt(row, 3) == Item.EstadoItem.CANCELADO ) 
				setBackground(colorCancelado);
			else if ( (Item.EstadoItem) tabla.getValueAt(row, 3) == Item.EstadoItem.CANCELADOVISTO ) 
				setBackground(colorCanceladoVisto);
			else 
				setBackground(Color.MAGENTA); //esto nunca debería pasar.
			
			//defino la alineación horizontal
			if (column == 0 || column==2 || column==4 || column==6)
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
	
} //class Despacho


