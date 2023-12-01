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
import entidades.Item;
import entidades.Mesa;
import entidades.Pedido;
import entidades.Producto;
import entidades.Servicio;
import java.awt.Color;
import java.awt.Component;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import utiles.Utils;


/**
 *
 * @author John David Molina Velarde, Leticia Mores, Enrique Germán Martínez, Carlos Eduardo Beltrán
 */
public class GestionPedidos extends javax.swing.JInternalFrame {
	MesaData mesaData;
	ServicioData servicioData;
	ItemData itemData;
	PedidoData pedidoData;
	ProductoData productoData;
	DefaultTableModel modeloTablaPedidos, modeloTablaItems;
	LinkedHashMap<Integer, Servicio> mapaMeseros;

	
	
	
	public GestionPedidos() {
		initComponents();
		mesaData = new MesaData();
		servicioData = new ServicioData();
		itemData = new ItemData();
		pedidoData = new PedidoData();
		productoData = new ProductoData();
		
		modeloTablaPedidos = (DefaultTableModel) tablaPedidos.getModel();
		modeloTablaItems = (DefaultTableModel) tablaItems.getModel();
		
		//cambio los renderer para tablaPedidos para así elegir colores diferentes según el estado
		for (int columna = 0; columna <=5; columna++)
			tablaPedidos.getColumnModel().getColumn(columna).setCellRenderer(new RendererPedidos());
		
		//cambio los renderer para tablaItems para así elegir colores diferentes según el estado
		for (int columna = 0; columna <=4; columna++)
			tablaItems.getColumnModel().getColumn(columna).setCellRenderer(new RendererItems());
		
		cargarMesas();
		cargarMeseros();
		cargarPedidos();
	} //constructor

	
	
	/**
	 * carga la lista de mesas al combo box cbMesas
	 */
	public void cargarMesas(){
		//cargo la lista de mesas
		List<Mesa> listaMesas = mesaData.getListaMesas();
		
		//borro el combo box de mesas
		int cantidad = cbMesas.getItemCount();
		for (int i = 0; i < cantidad; i++){
			cbMesas.removeItemAt(0);
		}
		
		//ahora cargo el combo box de mesas con las idMesas
		cbMesas.addItem("Todas");
		listaMesas.stream().forEach( mesa -> cbMesas.addItem("" + mesa.getIdMesa()) );
	} // cargarMesas
	
	
	
	
	/**
	 * carga la lista de meseros al combo box cbMeseros
	 */
	public void cargarMeseros(){
		//cargo la lista de meseros
		List<Servicio> listaMeseros = servicioData.getListaServiciosXCriterioDeBusqueda(
			//id, nombre, host, puerto, TipoServicio, ordenacion
			  -1, "",     "",	-1,		Servicio.TipoServicio.MESERO, ServicioData.OrdenacionServicio.PORIDSERVICIO);
		
		//borro el combo box de meseros
		int cantidad = cbMeseros.getItemCount();
		for (int i = 0; i < cantidad; i++){
			cbMeseros.removeItemAt(0);
		}
		
		//ahora cargo el combo box de meseros con los meseros
		Servicio meseroTodos = new Servicio(-1, "Todos", "", 0, Servicio.TipoServicio.MESERO, ""); //mesero ficticio que diga Todos
		cbMeseros.addItem(meseroTodos);
		listaMeseros.stream().forEach( mesero -> cbMeseros.addItem(mesero) );
		
		//cargo el mapa de meseros
		mapaMeseros = new LinkedHashMap();
		listaMeseros.stream().forEach(mesero -> mapaMeseros.put(mesero.getIdServicio(), mesero));
	} // cargarMeseros
	
	
	
	
	/**
	 * carga la lista de pedidos a la tabla tablapedidos en base al filtro de
	 * búsqueda especificado.
	 */
	private void cargarPedidos() {
		//del filtro de busqueda averiguo el idMesa
		int idMesa;
		if ( ((String)cbMesas.getSelectedItem()).equalsIgnoreCase("Todas") )
			idMesa = -1;
		else
			idMesa = Integer.parseInt( (String)cbMesas.getSelectedItem() );
		
		//del filtro de busqueda averiguo el idMesero
		int idMesero = ((Servicio)cbMeseros.getSelectedItem()).getIdServicio();
		
		//del filtro de busqueda averiguo la fechaDesde
		LocalDateTime fechaDesde;
		if (dcFechaDesde.getDate() == null)
			fechaDesde = null;
		else 
			fechaDesde = Utils.date2LocalDateTime(dcFechaDesde.getDate());
		
		//del filtro de busqueda averiguo la fechaHasta
		LocalDateTime fechaHasta;
		if (dcFechaHasta.getDate() == null)
			fechaHasta = null;
		else 
			fechaHasta = Utils.date2LocalDateTime(dcFechaHasta.getDate());
		
		//del filtro de busqueda averiguo el estado
		// Pedido.EstadoPedido puede ser ACTIVO / CANCELADO / PAGADO
		// en el combobox cbEstadoPedido tenemos: Todos, Activos, Pagados, Cancelados
		Pedido.EstadoPedido estadoPedido;
		int icbEstado = cbEstadoPedido.getSelectedIndex();
		if (icbEstado == 0) //Todos
			estadoPedido = null;
		else if (icbEstado == 1) //Activos
			estadoPedido = Pedido.EstadoPedido.ACTIVO;
		else if (icbEstado == 2) // Pagados
			estadoPedido = Pedido.EstadoPedido.PAGADO;
		else // Cancelados
			estadoPedido = Pedido.EstadoPedido.CANCELADO;
			
		//del filtro de busquda averiguo el orden 
		PedidoData.OrdenacionPedido ordenacion;
		 if (cbOrdenPedidos.getSelectedIndex() == 0)
			ordenacion = PedidoData.OrdenacionPedido.PORIDPEDIDO;
		else if (cbOrdenPedidos.getSelectedIndex() == 1)
			ordenacion = PedidoData.OrdenacionPedido.PORIDMESA;
		else if (cbOrdenPedidos.getSelectedIndex() == 2)
			ordenacion = PedidoData.OrdenacionPedido.PORIDMESERO;
		else if (cbOrdenPedidos.getSelectedIndex() == 3)
			ordenacion = PedidoData.OrdenacionPedido.PORFECHAHORA;
		else // por las dudas que no eligio uno correcto
			ordenacion = PedidoData.OrdenacionPedido.PORIDMESERO;
		
		
		//obtengo la lista de pedidos en base a ese filtro/criterio de búsqueda 
		List<Pedido> listaPedidos = pedidoData.getListaPedidosXCriterioDeBusqueda(
			//id, idMesa, idMesero, fechaDesde, fechaHasta, EstadoPedido,  OrdenacionPedido
			  -1, idMesa, idMesero, fechaDesde, fechaHasta, estadoPedido, ordenacion);
		
		//borro las filas de la tabla
		for (int fila = modeloTablaPedidos.getRowCount() -  1; fila >= 0; fila--)
			modeloTablaPedidos.removeRow(fila);
		

		// defino formato de importes para mostrar mejor los importes
		double importeTotal = 0.0;
		int cantPedidos = 0;
		NumberFormat formatoImporte = NumberFormat.getCurrencyInstance();
		//Si se desea forzar el formato español: formatoImporte = NumberFormat.getCurrencyInstance(new Locale("es","ES"));
		
		//cargo los pedidos de listaPedidos a la tabla y calculo importeTotal
		for (Pedido pedido : listaPedidos) {
			double importe = calcularImportePedido(pedido.getIdPedido()); //averiguo el importe de esos items del pedido
			importeTotal += importe; //lo agrego al importe total de TODOS los pedidos que se muestran
			cantPedidos++;
			modeloTablaPedidos.addRow(new Object[] {
				pedido.getIdPedido(),
				pedido.getIdMesa(),
				mapaMeseros.get(pedido.getIdMesero()).toString(),  //almaceno el string con los datos del mesero
				Utils.localDateTime2String(pedido.getFechaHora()), // almaceno el string con la localDateTime
				pedido.getEstado(),								   // almaceno el objeto EstadoPedido
				formatoImporte.format(importe) 					   // el importe acumulado de los items no cancelados de ese pedido
			}
			);
		} //for (Pedido...
		
		//muestro el importeTotal calculado
		lblImporte.setText(formatoImporte.format(importeTotal));
		lblCantPedidos.setText(""+cantPedidos);
		
		//borro la tabla de Items
		cargarItems(0);
	}//cargarPedidos
	
	
	
	private double calcularImportePedido(int idPedido){
		double importe = 0.0;
		Pedido pedido = pedidoData.getPedido(idPedido);
		if (pedido != null && pedido.getEstado() != Pedido.EstadoPedido.CANCELADO) { // si existe el pedido y no es uno cancelado
			List<Item> listaItems = itemData.getListaItemsXCriterioDeBusqueda( // obtengo los items de ese pedido
					//idItem, idProducto, idPedido, EstadoItem, OrdenacionItem
					  -1,		-1,		  idPedido, null,		ItemData.OrdenacionItem.PORIDITEM);

			// sumo esos items (si no son cancelados/canceladovistos)
			for (Item item:listaItems) 
				if (item.getEstado() != Item.EstadoItem.CANCELADO && item.getEstado() != Item.EstadoItem.CANCELADOVISTO) 
					importe += item.getCantidad() * productoData.getProducto(item.getIdProducto()).getPrecio();
		}
		
		return importe;
	} //calcularImportePedido
	
	
	
	
	
	/**
	 * carga la tabla de items correspondientes al idPedido pasado como 
	 * parámetro. Si el idPedido es 0 no carga ningun item.
	 * @param idPedido 
	 */
	private void cargarItems(int idPedido){
		//obtengo la lista de pedidos en base a ese filtro/criterio de búsqueda 
		List<Item> listaItems = itemData.getListaItemsXCriterioDeBusqueda(
			//idItem, idProducto, idPedido, Item.EstadoItem, OrdenacionItem)
			  -1,		-1,		 idPedido,   null,			 ItemData.OrdenacionItem.PORIDITEM);
		
		//borro las filas de la tabla de Items
		for (int fila = modeloTablaItems.getRowCount() -  1; fila >= 0; fila--)
			modeloTablaItems.removeRow(fila);
		

		// defino formato de importes para mostrar mejor los importes
		NumberFormat formatoImporte = NumberFormat.getCurrencyInstance();
		//Si se desea forzar el formato español: formatoImporte = NumberFormat.getCurrencyInstance(new Locale("es","ES"));
		
		//cargo los items de listaItems a la tabla
		for (Item item : listaItems) {
			Producto producto = productoData.getProducto(item.getIdProducto());
			double importe = (item.getEstado() == Item.EstadoItem.CANCELADO ||
					item.getEstado() == Item.EstadoItem.CANCELADOVISTO)? 
						0.0 : 
						(producto.getPrecio() * item.getCantidad()); //averiguo el importe de esos items del pedido
			modeloTablaItems.addRow(new Object[] {
				item.getIdPedido(),
				producto.toString(),
				item.getCantidad(),
				item.getEstado(),								   // almaceno el objeto EstadoItem
				formatoImporte.format(importe) 					   // el importe de los items items no cancelados
			}
			);
		} //for (Pedido...
	}
	
//================================================================================
//================================================================================


	
	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelTablas = new javax.swing.JPanel();
        panelItems = new javax.swing.JScrollPane();
        tablaItems = new javax.swing.JTable();
        panelPedidos = new javax.swing.JScrollPane();
        tablaPedidos = new javax.swing.JTable();
        lblPedidos = new javax.swing.JLabel();
        lblPedidos1 = new javax.swing.JLabel();
        lblImporte = new javax.swing.JLabel();
        lblCantPedidos = new javax.swing.JLabel();
        panelFiltro = new javax.swing.JPanel();
        cbMeseros = new javax.swing.JComboBox<>();
        dcFechaDesde = new com.toedter.calendar.JDateChooser();
        dcFechaHasta = new com.toedter.calendar.JDateChooser();
        cbMesas = new javax.swing.JComboBox<>();
        cbEstadoPedido = new javax.swing.JComboBox<>();
        cbOrdenPedidos = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        panelBotonera = new javax.swing.JPanel();
        btnFiltrar = new javax.swing.JButton();
        btnResetarFiltro = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();

        panelTablas.setBackground(new java.awt.Color(153, 153, 255));

        panelItems.setBackground(new java.awt.Color(153, 153, 255));

        tablaItems.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Producto", "Cant", "Estado", "Importe"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Object.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaItems.setRequestFocusEnabled(false);
        tablaItems.setRowSelectionAllowed(false);
        panelItems.setViewportView(tablaItems);
        if (tablaItems.getColumnModel().getColumnCount() > 0) {
            tablaItems.getColumnModel().getColumn(0).setMinWidth(30);
            tablaItems.getColumnModel().getColumn(0).setPreferredWidth(30);
            tablaItems.getColumnModel().getColumn(0).setMaxWidth(30);
            tablaItems.getColumnModel().getColumn(1).setPreferredWidth(100);
            tablaItems.getColumnModel().getColumn(2).setMinWidth(40);
            tablaItems.getColumnModel().getColumn(2).setPreferredWidth(40);
            tablaItems.getColumnModel().getColumn(2).setMaxWidth(40);
            tablaItems.getColumnModel().getColumn(3).setPreferredWidth(50);
            tablaItems.getColumnModel().getColumn(4).setPreferredWidth(50);
        }

        tablaPedidos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Id ", "Mesa", "Mesero", "Fecha", "Estado", "Importe"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaPedidos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaPedidosMouseClicked(evt);
            }
        });
        panelPedidos.setViewportView(tablaPedidos);
        if (tablaPedidos.getColumnModel().getColumnCount() > 0) {
            tablaPedidos.getColumnModel().getColumn(0).setMinWidth(40);
            tablaPedidos.getColumnModel().getColumn(0).setPreferredWidth(40);
            tablaPedidos.getColumnModel().getColumn(0).setMaxWidth(40);
            tablaPedidos.getColumnModel().getColumn(1).setMinWidth(40);
            tablaPedidos.getColumnModel().getColumn(1).setPreferredWidth(40);
            tablaPedidos.getColumnModel().getColumn(1).setMaxWidth(40);
            tablaPedidos.getColumnModel().getColumn(2).setPreferredWidth(80);
            tablaPedidos.getColumnModel().getColumn(3).setPreferredWidth(50);
            tablaPedidos.getColumnModel().getColumn(4).setPreferredWidth(50);
            tablaPedidos.getColumnModel().getColumn(5).setPreferredWidth(50);
        }

        lblPedidos.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblPedidos.setText("Pedidos segun filtro búsqueda");

        lblPedidos1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblPedidos1.setText("Items del Pedido");

        lblImporte.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblImporte.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblImporte.setText("Total importe Pedidos");
        lblImporte.setBorder(javax.swing.BorderFactory.createTitledBorder("Total importe"));

        lblCantPedidos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblCantPedidos.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCantPedidos.setText("Cantidad pedidos");
        lblCantPedidos.setBorder(javax.swing.BorderFactory.createTitledBorder("Cantidad Pedidos"));

        javax.swing.GroupLayout panelTablasLayout = new javax.swing.GroupLayout(panelTablas);
        panelTablas.setLayout(panelTablasLayout);
        panelTablasLayout.setHorizontalGroup(
            panelTablasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTablasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTablasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelPedidos)
                    .addGroup(panelTablasLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lblPedidos)
                        .addGap(18, 18, 18)
                        .addComponent(lblCantPedidos, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblImporte, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelTablasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelItems, javax.swing.GroupLayout.PREFERRED_SIZE, 524, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPedidos1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        panelTablasLayout.setVerticalGroup(
            panelTablasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTablasLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelTablasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblImporte, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCantPedidos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblPedidos)
                    .addComponent(lblPedidos1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelTablasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelItems, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                    .addComponent(panelPedidos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
        );

        panelFiltro.setBackground(new java.awt.Color(153, 153, 255));

        cbMeseros.setBorder(javax.swing.BorderFactory.createTitledBorder("Mesero"));

        dcFechaDesde.setBorder(javax.swing.BorderFactory.createTitledBorder("Fecha desde"));
        dcFechaDesde.setDateFormatString("dd/MM/yyyy HH:mm");

        dcFechaHasta.setBorder(javax.swing.BorderFactory.createTitledBorder("Fecha hasta"));
        dcFechaHasta.setDateFormatString("dd/MM/yyyy HH:mm");

        cbMesas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Mesa", "Item 2", "Item 3", "Item 4" }));
        cbMesas.setBorder(javax.swing.BorderFactory.createTitledBorder("Mesa"));

        cbEstadoPedido.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos", "Activos", "Pagados", "Cancelados" }));
        cbEstadoPedido.setSelectedIndex(1);
        cbEstadoPedido.setBorder(javax.swing.BorderFactory.createTitledBorder("Estado del Pedido"));

        cbOrdenPedidos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "por Pedido", "por Mesa", "por Mesero", "por Fecha y hora" }));
        cbOrdenPedidos.setBorder(javax.swing.BorderFactory.createTitledBorder("Orden"));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setText("Criterio de búsqueda de pedidos");

        javax.swing.GroupLayout panelFiltroLayout = new javax.swing.GroupLayout(panelFiltro);
        panelFiltro.setLayout(panelFiltroLayout);
        panelFiltroLayout.setHorizontalGroup(
            panelFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltroLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(cbMesas, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addComponent(cbMeseros, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54)
                .addGroup(panelFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFiltroLayout.createSequentialGroup()
                        .addComponent(dcFechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(54, 54, 54)
                        .addComponent(dcFechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cbEstadoPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cbOrdenPedidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelFiltroLayout.setVerticalGroup(
            panelFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltroLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addGroup(panelFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbMesas)
                        .addComponent(cbMeseros))
                    .addGroup(panelFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(dcFechaDesde, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                        .addComponent(dcFechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbEstadoPedido)
                        .addComponent(cbOrdenPedidos)))
                .addContainerGap())
        );

        panelBotonera.setBackground(new java.awt.Color(153, 153, 255));

        btnFiltrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/buscar1_32x32.png"))); // NOI18N
        btnFiltrar.setText("Filtrar por criterio de búsqueda");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        btnResetarFiltro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/restart3_32x32.png"))); // NOI18N
        btnResetarFiltro.setText("Resetear filtro");
        btnResetarFiltro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetarFiltroActionPerformed(evt);
            }
        });

        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/salida1_32x32.png"))); // NOI18N
        btnSalir.setText("Salir");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelBotoneraLayout = new javax.swing.GroupLayout(panelBotonera);
        panelBotonera.setLayout(panelBotoneraLayout);
        panelBotoneraLayout.setHorizontalGroup(
            panelBotoneraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBotoneraLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(btnFiltrar)
                .addGap(38, 38, 38)
                .addComponent(btnResetarFiltro)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSalir)
                .addGap(50, 50, 50))
        );
        panelBotoneraLayout.setVerticalGroup(
            panelBotoneraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBotoneraLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBotoneraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFiltrar)
                    .addComponent(btnResetarFiltro)
                    .addComponent(btnSalir))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelTablas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelBotonera, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelFiltro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelBotonera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(panelTablas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	
	
//================================================================================
//================================================================================

	
	
	/**
	 * Resetea el filtro de búsqueda al filtro por defecto
	 * @param evt 
	 */
    private void btnResetarFiltroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetarFiltroActionPerformed
        cbMesas.setSelectedIndex(0);
		cbMeseros.setSelectedIndex(0);
		dcFechaDesde.setDate(null);
		dcFechaHasta.setDate(null);
		cbEstadoPedido.setSelectedIndex(1);
		cbOrdenPedidos.setSelectedIndex(0);
		cargarPedidos();
    }//GEN-LAST:event_btnResetarFiltroActionPerformed

	
	
	/**
	 * carga los items del pedido seleccionado de la tabla de pedidos
	 * @param evt 
	 */
    private void tablaPedidosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaPedidosMouseClicked
        int numFila = tablaPedidos.getSelectedRow();
		if (numFila != -1){
			int idPedido = (Integer) tablaPedidos.getValueAt(numFila, 0);
			cargarItems(idPedido);
		}
    }//GEN-LAST:event_tablaPedidosMouseClicked

	
	
	/**
	 * Vuelve a cargar los pedidos a su tabla en base al criterio del filtro de búsqueda
	 * @param evt 
	 */
    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        cargarPedidos();
    }//GEN-LAST:event_btnFiltrarActionPerformed

	
	
	/**
	 * cierra la ventana
	 * @param evt 
	 */
    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        dispose();
    }//GEN-LAST:event_btnSalirActionPerformed

	
	
	

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnResetarFiltro;
    private javax.swing.JButton btnSalir;
    private javax.swing.JComboBox<String> cbEstadoPedido;
    private javax.swing.JComboBox<String> cbMesas;
    private javax.swing.JComboBox<Servicio> cbMeseros;
    private javax.swing.JComboBox<String> cbOrdenPedidos;
    private com.toedter.calendar.JDateChooser dcFechaDesde;
    private com.toedter.calendar.JDateChooser dcFechaHasta;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblCantPedidos;
    private javax.swing.JLabel lblImporte;
    private javax.swing.JLabel lblPedidos;
    private javax.swing.JLabel lblPedidos1;
    private javax.swing.JPanel panelBotonera;
    private javax.swing.JPanel panelFiltro;
    private javax.swing.JScrollPane panelItems;
    private javax.swing.JScrollPane panelPedidos;
    private javax.swing.JPanel panelTablas;
    private javax.swing.JTable tablaItems;
    private javax.swing.JTable tablaPedidos;
    // End of variables declaration//GEN-END:variables





	
	/**
	 * Renderer de celdas de la tablaPedidos. Pone el color según el estado del 
	 * pedido (Activo, Pagado, Cancelado). 
	 * También permite gestionar los eventos realizados sobre la tabla.
	 * 
	 * Para usarla, en la definicion de la tabla se pone:
	 *		tabla.getColumnModel().getColumn(3).setCellRenderer(new generalRenderer());
	 *	de esa manera aplica el renderer a la columna, los colores son diferentes si la celda esta seleccionada o no.
	 */
	class RendererPedidos extends JLabel implements TableCellRenderer {    
		//Font f = new Font( "Helvetica",Font.PLAIN,10 );
		Color colorSeleccionado = new Color(184,207,229); //new Color(0,120,215); //new Color(117, 204, 169);
		Color colorGeneral = Color.WHITE; //new Color(255,255,255); //new Color(225, 244, 238);
		Color colorActivo = Color.WHITE;
		Color colorPagado = Color.GREEN;
		Color colorCancelado = Color.LIGHT_GRAY;

		public RendererPedidos() {
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
			else  if ( (Pedido.EstadoPedido) tabla.getValueAt(row, 4) == Pedido.EstadoPedido.ACTIVO ) 
				setBackground(colorActivo);
			else if ( (Pedido.EstadoPedido) tabla.getValueAt(row, 4) == Pedido.EstadoPedido.PAGADO ) 
				setBackground(colorPagado);
			else if ( (Pedido.EstadoPedido) tabla.getValueAt(row, 4) == Pedido.EstadoPedido.CANCELADO ) 
				setBackground(colorCancelado);
			else 
				setBackground(Color.MAGENTA); //esto nunca debería pasar.
			
			//defino la alineación horizontal
			if (column == 0 || column==1)
				setHorizontalAlignment( JLabel.CENTER );
			else if (column == 5)
				setHorizontalAlignment( JLabel.RIGHT );
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
	} //class rendererPedidos
	
	
	
	
	
	
	
	
	
	/**
	 * Renderer de celdas de la tablaItemss. Pone el color según el estado del 
	 * item (Anotado, Solicitado, Despachado, Cancelado, CanceladoVisto). 
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
		Color colorCanceladoVisto = new Color(100, 100, 100); //gris clarito

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
				else if (column==4) //columna derecha, con borde derecho
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
			if (column == 0 || column==2)
				setHorizontalAlignment( JLabel.CENTER );
			else if (column == 4)
				setHorizontalAlignment( JLabel.RIGHT );
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
	
	
}
