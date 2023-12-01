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
public class Meseros extends javax.swing.JFrame implements Observer {
	private Servicio mesero;
	private static LinkedHashMap<Integer, Mesa> mapaMesas;
	private static LinkedHashMap<Integer, Pedido> mapaPedidos;
	private static LinkedHashMap<Integer, Servicio> mapaServicios;
	private static List<Servicio> listaRecepcionistas;
	private static LinkedHashMap<Integer, Categoria> mapaCategorias;
	private DefaultTableModel modeloTablaMesas, modeloTablaPedidos, modeloTablaItems, modeloTablaProductos;
	private ItemData itemData = new ItemData(); //conecto con la BD
	private MesaData mesaData = new MesaData(); //conecto con la BD
	private PedidoData pedidoData = new PedidoData(); //conecto con la BD
	private ProductoData productoData = new ProductoData();
	private ProductoData.OrdenacionProducto ordenacion = ProductoData.OrdenacionProducto.PORIDPRODUCTO; // defino el tipo de orden por defecto 
	private ServidorSocket servidor;
	
	
	
	public Meseros(Servicio mesero) {
		this.mesero = mesero;
		initComponents();
		
		modeloTablaMesas   = (DefaultTableModel) tablaMesas.getModel();
		modeloTablaPedidos = (DefaultTableModel) tablaPedidos.getModel();
		modeloTablaItems   = (DefaultTableModel) tablaItems.getModel();
		modeloTablaProductos=(DefaultTableModel) tablaProductos.getModel();
		
		//elijo alineacion centro para pedidos
		DefaultTableCellRenderer alinear = new DefaultTableCellRenderer();
		alinear.setHorizontalAlignment(SwingConstants.CENTER);//.LEFT .RIGHT .CENTER
		tablaPedidos.getColumnModel().getColumn(0).setCellRenderer(alinear);

		//cambio los renderer para las tablaMesas y tablaItems para así elegir colores diferentes según el estado
		tablaMesas.getColumnModel().getColumn(0).setCellRenderer(new RendererMesas());
		//tablaItems.setDefaultRenderer(Object.class, new RendererItems());
		for (int columna = 0; columna <=3; columna++)
			tablaItems.getColumnModel().getColumn(columna).setCellRenderer(new RendererItems());
		
		// cargo los datos
		cargarCategorias();
		cargarServicios();
		cargarMesas();
		cargarPedidos();
		cargarProductos();
		mostrarLabelsEncabezamientoItems();
		
		lanzarServidor();	// ejecuta un servidor un thread concurrente que escucha mensajes 
	} // constructor de Meseros
	
	
	
	
	
	//--------------------------- PARTE DE COMUNICACION VIA SOCKETS ----------------------------------------------------
	private void lanzarServidor(){
		//Inicialización de la parte de comunicación via sockets y sus threads respectivos.
		servidor = new ServidorSocket(mesero.getPuerto()); // el puerto donde escuchará
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
		System.out.println("En meseros mensaje recibido: " + s);
		if (s.startsWith("S")) {
			Utils.sonido1("src/sonidos/Campanilla.wav");
			cargarItems();
		}else if (s.startsWith("R")) {
			Utils.sonido1("src/sonidos/Silbido3.wav");
			cargarMesas();
		}
		
		//actualizarPantalla(); // y acá toma las acciones correspondientes para actualizar pantalla
	};
	
	
	
	
	private void actualizarPantalla(){
		cargarMesas();
		cargarPedidos();
		cargarItems();

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
		if (queServicio != null) {
			ClienteSocket cliente = new ClienteSocket( //creo un cliente que pueda mandar a ese host en ese puerto
				queServicio.getHost(), queServicio.getPuerto(), 
				"M " + mesero.getIdServicio() + " " + mensaje); 
			Thread hilo = new Thread(cliente);	//creo un hilo para el clienteSocket
			hilo.start();						//ejecuto ese hilo para el cliente	
		}
	}
	//--------------------------- FIN PARTE DE COMUNICACION VIA SOCKETS ----------------------------------------------------
	
	
	
	
	
	
	/** cargo el mapa de categorías */
	private void cargarCategorias(){
		CategoriaData categoriaData = new CategoriaData(); //conexión con la BD
		
		//Obtengo la lista de categorias
		List<Categoria> listaCategorias = categoriaData.getListaCategorias();
		
		//agrego la categoria ficticia "0 Todas las categorías"
		Categoria categoria0 = new Categoria(0, "Todas las categorías");
		listaCategorias.add(0, categoria0);
		
		//genero un mapa con las categorias
		mapaCategorias = new LinkedHashMap();
		listaCategorias.stream().forEach(categoria -> mapaCategorias.put(categoria.getIdCategoria(), categoria));
		
		
		//ahora rellenos los datos del combo box de categorías
		//borro el combo box de categorias
		int cantidad = cbCategorias.getItemCount();
		for (int i = 0; i < cantidad; i++){
			cbCategorias.removeItemAt(0);
		}
		
		//ahora cargo el combo box de categorias con la nuevas categorias
		listaCategorias.stream().forEach( categoria -> cbCategorias.addItem(categoria) );
	} //cargarCategorias
	
	
	
	
	/** cargo el mapa de Servicios (cocina, bar, etc) */
	private void cargarServicios(){
		ServicioData servicioData = new ServicioData(); //conexión con la BD
		
		//Obtengo la lista de servicios que despachan productos (cocina, bar, etc.)
		List<Servicio> listaServicios = servicioData.getListaServiciosXCriterioDeBusqueda(
				-1, "", "", -1, Servicio.TipoServicio.SERVICIO, ServicioData.OrdenacionServicio.PORIDSERVICIO);
		
		//genero un mapa con los servicios que despachan productos
		mapaServicios = new LinkedHashMap();
		listaServicios.stream().forEach(servicio -> mapaServicios.put(servicio.getIdServicio(), servicio));
		//System.out.println("CargarServicios: MapaServicios: " + mapaServicios);
		
		//Obtengo la lista de recepcionistas que asignan las mesas
		listaRecepcionistas = servicioData.getListaServiciosXCriterioDeBusqueda(
				-1, "", "", -1, Servicio.TipoServicio.RECEPCION, ServicioData.OrdenacionServicio.PORIDSERVICIO);
	} //cargarServicios
	
	
	
	
	/** cargo el mapa de mesas y la tabla de mesas que corresponden a ese mesero */
	private void cargarMesas(){
		//Obtengo la lista de meses que corresponden a este mesero.
		List<Mesa> listaMesas = mesaData.getListaMesasXCriterioDeBusqueda(
				-1, -1, null, mesero.getIdServicio(), MesaData.OrdenacionMesa.PORIDMESA);
		
		//genero un mapa con las mesas de este mesero.
		mapaMesas = new LinkedHashMap();
		listaMesas.stream().forEach(mesa -> mapaMesas.put(mesa.getIdMesa(), mesa));
		
		int filaSeleccionada = tablaMesas.getSelectedRow(); //conservo la anterior mesa seleccionada
		
		//borro las filas de la tabla mesas
		for (int fila = modeloTablaMesas.getRowCount() -  1; fila >= 0; fila--)
			modeloTablaMesas.removeRow(fila);
		
		//cargo esas mesas a la tabla de mesas
		for (Mesa mesa : listaMesas) {
			modeloTablaMesas.addRow(new Object[] {
				mesa.getIdMesa()
			} );
		}
		
		//si la fila que tenia seleccionada sigue siendo válida
		if (filaSeleccionada >= 0 && filaSeleccionada < tablaMesas.getRowCount())
			tablaMesas.setRowSelectionInterval(filaSeleccionada, filaSeleccionada); //restauro la fila que tenía seleccionada
		//else
		//	tablaMesas.removeRowSelectionInterval(0, tablaMesas.getRowCount()-1); // borro todas las selecciones de la tabla de pedidos
		
		//cargarPedidos();
	} //cargar mesas
	
	
	
	
	/**
	 * Me devuelve el idMesa que se seleccionó de la tabla. Si no hay ninguna 
	 * seleccionada, devuelve 0;
	 * @return 
	 */
	private int tablaMesasGetIdMesaSeleccionada(){
		int filaSeleccionada = tablaMesas.getSelectedRow();
		if (filaSeleccionada != -1)
			return (Integer) tablaMesas.getValueAt(filaSeleccionada, 0);
		else
			return 0;
	} //tablaMesasGetIdMesaSeleccionada
	
	
	
	/** devuelve la Mesa que se selecciono de la tablaMesas. Devuelve null si no hay seleccionada */
	private Mesa tablaMesasGetMesaSeleccionada(){
		int filaSeleccionada = tablaMesas.getSelectedRow();
		if (filaSeleccionada != -1)
			return mapaMesas.get(tablaMesasGetIdMesaSeleccionada());
		else
			return null;
	} //tablaMesasGetMesaSeleccionada
	
	
	
	/** cargo el mapa de mesas y la tabla de mesas que corresponden a ese mesero  */
	private void cargarPedidos(){
		//Obtengo la lista de pedidos que corresponden a esa mesa.
		List<Pedido> listaPedidos = pedidoData.getListaPedidosXCriterioDeBusqueda(
		//	idPedido, idMesa,				  idMesero, fechaDesde, fechaHasta,	  estado,						OrdenacionPedido ordenacion
			-1,		 tablaMesasGetIdMesaSeleccionada(), -1,		null,		null, Pedido.EstadoPedido.ACTIVO,	PedidoData.OrdenacionPedido.PORIDPEDIDO);
		
		//genero un mapa con los pedidos de esa mesa.
		mapaPedidos = new LinkedHashMap();
		listaPedidos.stream().forEach( pedido -> mapaPedidos.put(pedido.getIdPedido(), pedido) );
		
		int filaSeleccionada = tablaMesas.getSelectedRow(); //conservo el anterior pedido seleccionada
		
		//borro las filas de la tabla mesas
		for (int fila = modeloTablaPedidos.getRowCount() -  1; fila >= 0; fila--)
			modeloTablaPedidos.removeRow(fila);
		
		//cargo esos pedidos a la tabla de pedidos
		listaPedidos.stream().forEach(pedido -> modeloTablaPedidos.addRow(new Object[] {
				pedido.getIdPedido()
			} ) 
		);
		
		//si la fila que tenia seleccionada sigue siendo válida y no cambio el pedido
		if (filaSeleccionada >= 0 && filaSeleccionada < tablaPedidos.getRowCount())
			tablaPedidos.setRowSelectionInterval(filaSeleccionada, filaSeleccionada); //restauro la fila que tenía seleccionada
		//actualizarPantalla();
	} //cargarPedidos
	
	
	
	/**
	 * Me devuelve el idPedido que se seleccionó de la tabla. Si no hay ninguno 
	 * seleccionado, devuelve 0;
	 * @return 
	 */
	private int tablaPedidosGetIdPedidoSeleccionado(){
		int filaSeleccionada = tablaPedidos.getSelectedRow();
		if (filaSeleccionada != -1)
			return (Integer) tablaPedidos.getValueAt(filaSeleccionada, 0);
		else
			return 0;
	} //tablaPedidosGetIdPedidoSeleccionado
	
	
	/** devuelve el Pedido de la fila seleccionada de tablaPedidos */
	private Pedido tablaPedidosGetPedidoSeleccionado(){
		int filaSeleccionada = tablaPedidos.getSelectedRow();
		if (filaSeleccionada != -1)
			return mapaPedidos.get(tablaPedidosGetIdPedidoSeleccionado());
		else
			return null;
	} //tablaPedidosGetPedidoSeleccionado
	
	
	
	/** devuelve el importe total de todos los items del pedido que no tengan estado Cancelado */
	private double calcularImportePedido(){
		double importe = 0.0;
		Pedido pedido = tablaPedidosGetPedidoSeleccionado() ;
		if (pedido != null) { // si tiene seleccionado un pedido de la tablaPedidos
			for (int fila=0; fila < tablaItems.getRowCount(); fila++){
				if (tablaItemsGetEstado(fila) != Item.EstadoItem.CANCELADO &&
					tablaItemsGetEstado(fila) != Item.EstadoItem.CANCELADOVISTO)
					importe += tablaItemsGetProducto(fila).getPrecio() * tablaItemsGetCantidad(fila);
			} // for
		} else
			System.out.println("Error... está intentando calcular el importe de un pedido y no seleccionó ninguno en la tabla");
		return importe;
	} //calcularImportePedido
	
	
	
	/**
	 * cargo el lista de items y la tabla de items que corresponden a ese pedido
	 */
	private void cargarItems(){
		//Obtengo la lista de items que corresponden a ese pedido.
		List<Item> listaItems = itemData.getListaItemsXCriterioDeBusqueda(// idItemSeleccionado, idProducto, idPedido,				estado,		ordenacion	
			-1,		 -1,	   tablaPedidosGetIdPedidoSeleccionado(), null,	ItemData.OrdenacionItem.PORIDITEM);
		
		//System.out.println("Lista items: " + listaItems); //debug
		
		
		//borro las filas de la tabla items
		for (int fila = modeloTablaItems.getRowCount() -  1; fila >= 0; fila--)
			modeloTablaItems.removeRow(fila);
		
		//cargo esos pedidos items a la tabla de items
		listaItems.stream().forEach(item -> modeloTablaItems.addRow(new Object[] {
				item.getIdItem(),
				productoData.getProducto(item.getIdProducto()),
				item.getCantidad(),
				item.getEstado()
			} ) 
		);
	} //cargarItems
	
	
	
	/** dada la fila de tablaItems devuelve el IdItem */
	private int tablaItemsGetIdItem(int numfila) {
		return (Integer)tablaItems.getValueAt(numfila, 0);
	} // tablaItemsGetIdItem
	
	
	
	/** dada la fila de tablaItems devuelve el Producto */
	private Producto tablaItemsGetProducto(int numfila) {
		return (Producto)tablaItems.getValueAt(numfila, 1);
	} //tablaItemsGetProducto
	
	
	
	/** dada la fila de tablaItems devuelve la Cantidad */
	private int tablaItemsGetCantidad(int numfila) {
		return (Integer)tablaItems.getValueAt(numfila, 2);
	} // tablaItemsGetCantidad
	
	
	
	/** dada la fila de tablaItems, devuelve el Estado */
	private Item.EstadoItem tablaItemsGetEstado(int numfila) {
		return (Item.EstadoItem)tablaItems.getValueAt(numfila, 3);
	} //tablaItemsGetEstado
	
	
	
			
	/**
	 * cargo el lista de productos y la tabla de productos
	 */
	private void cargarProductos(){
		//Obtengo la lista de productos activos
		Categoria categoria = (Categoria)cbCategorias.getSelectedItem();
		int idCategoria = (categoria == null || categoria.getIdCategoria() == 0) ? -1 : categoria.getIdCategoria();
		List<Producto> listaProductos = productoData.getListaProductosXCriterioDeBusqueda(
			//idProducto, nombre, stock, precio, disponible, idCategoria, despachadoPor, ordenacion){ 
			-1,				"",	   -1,	 -1.0,	 true,		 idCategoria,		-1,			ordenacion);
		
		
		//borro las filas de la tabla productos
		for (int fila = modeloTablaProductos.getRowCount() -  1; fila >= 0; fila--)
			modeloTablaProductos.removeRow(fila);
		
		//cargo esos productos a la tabla de productos
		listaProductos.stream().forEach(producto -> modeloTablaProductos.addRow(new Object[] {
				producto,
				producto.getDescripcion(),
				producto.getPrecio(),
				mapaCategorias.get(producto.getIdCategoria()),
				mapaServicios.get(producto.getDespachadoPor())
			} ) 
		);
	} //cargarProductos
	
	
	
	/** dada la fila de tablaproductos devuelve el Producto */
	private Producto tablaProductosGetProducto(int numfila) {
		return (Producto)tablaProductos.getValueAt(numfila, 0);
	} // tablaProductosGetProducto
	
	
	/** dada la fila de tablaProductos devuelve la Descripcion */
	private String tablaProductosGetDescripcion(int numfila) {
		return (String)tablaProductos.getValueAt(numfila, 1);
	} //tablaProductosGetDescripcion
	
	
	
	/** dada la fila de la tablaProductos devuelve el precio */
	private double tablaProductosGetPrecio(int numfila) {
		return (double)tablaProductos.getValueAt(numfila, 2);
	} //tablaProductosGetPrecio
	
	
	
	/** dada la fila de tablProductos devuelve la cateoría */
	private Categoria tablaProductosGetCategoria(int numfila) {
		return (Categoria)tablaProductos.getValueAt(numfila, 3);
	} //tablaProductosGetCategoria
	
	
	
	/** dado el numero de fila de la tablaProductos, devuelve el Servicio */
	private Servicio tablaProductosGetDespachadoPor(int numfila) {
		return (Servicio)tablaProductos.getValueAt(numfila, 4);
	} //tablaProductosGetDespachadoPor
	
	
	
	/**
	 * Cuando no hay un itemSeleccionado seleccionado se deshabilitan los botones relacionados a los items
	 */
	private void deshabilitarBotonesItems(){
		btnAumentar.setEnabled(false);
		btnDisminuir.setEnabled(false);
		btnSolicitarItem.setEnabled(false);
		btnServirItem.setEnabled(false);
		btnIncluir.setEnabled(false);
		btnCancelarItem.setEnabled(false);
	} //deshabilitarBotonesItems
	
	
	
	/**
	 * Cuando se selecciona un item se habilitan los botones relacionados a los items
	 */
	private void habilitarBotonesItems(){
		btnAumentar.setEnabled(true);
		btnDisminuir.setEnabled(true);
		btnSolicitarItem.setEnabled(true);
		btnServirItem.setEnabled(true);
		//btnIncluir.setEnabled(true);
		btnCancelarItem.setEnabled(true);
	} //habilitarBotonesItems
	
	
	
	/**
	 * Cuando no hay un pedido seleccionado se deshabilitan los botones de pedidos
	 * (excepto el de alta pedidos)
	 */
	private void deshabilitarBotonesPedidos(){
		btnCancelarPedido.setEnabled(false);
		btnPagarPedido.setEnabled(false);
		//btnAltaPedido.setEnabled(false);
	} //deshabilitarBotonesPedidos
	
	
	
	/**
	 * Cuando se selecciona un item se habilitan los botones relacionados a los items
	 */
	private void habilitarBotonesPedidos(){
		btnCancelarPedido.setEnabled(true);
		btnPagarPedido.setEnabled(true);
		//btnAltaPedido.setEnabled(true);
	} //habilitarBotonesPedidos
	
	
	
	/**
	 * Muestra los datos del encabezamiento con el mesero, la mesa seleccionada
	 * y su estado, el pedido seleccionado y su fecha/hora, así como el total 
	 * del importe de los items de dicho pedido.
	 */
	private void mostrarLabelsEncabezamientoItems(){
		//mesero
		lblMesero.setText(mesero.getIdServicio() + ": " + mesero.getNombreServicio());
		
		//mesa
		if (tablaMesas.getSelectedRow() != -1) { // hay mesa seleccionada
			int idMesa = tablaMesasGetIdMesaSeleccionada();
			Mesa mesa = tablaMesasGetMesaSeleccionada();
			lblMesa.setText( idMesa + ": " + mesa.getEstado() );
		} else
			lblMesa.setText(" ");
		
		//pedido
		if (tablaPedidos.getSelectedRow() != -1) { // hay pedido seleccionada
			int idPedido = tablaPedidosGetIdPedidoSeleccionado();
			Pedido pedido = tablaPedidosGetPedidoSeleccionado();
			lblPedido.setText( idPedido + ": " + Utils.localDateTime2String(pedido.getFechaHora()) );
		} else
			lblPedido.setText("                  ");
		
		//importe
		//pedido
		if (tablaPedidos.getSelectedRow() != -1) { // hay pedido seleccionada
			lblImporte.setText("$" + calcularImportePedido());
		} else
			lblImporte.setText("           ");

	} // mostrarLabelsEncabezamiento
	
	
	
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
        panelPedidos = new javax.swing.JScrollPane();
        tablaPedidos = new javax.swing.JTable();
        panelItems = new javax.swing.JScrollPane();
        tablaItems = new javax.swing.JTable();
        botoneraVertical = new javax.swing.JPanel();
        btnIncluir = new javax.swing.JButton();
        btnAumentar = new javax.swing.JButton();
        btnDisminuir = new javax.swing.JButton();
        btnSolicitarItem = new javax.swing.JButton();
        btnServirItem = new javax.swing.JButton();
        btnCancelarItem = new javax.swing.JButton();
        panelProductos = new javax.swing.JScrollPane();
        tablaProductos = new javax.swing.JTable();
        panleEncabezamientoProductos = new javax.swing.JPanel();
        cbCategorias = new javax.swing.JComboBox<>();
        cbOrdenProductos = new javax.swing.JComboBox<>();
        lblProductosActivos = new javax.swing.JLabel();
        panelEncabezamientoItems = new javax.swing.JPanel();
        lblMesero = new javax.swing.JLabel();
        lblMesa = new javax.swing.JLabel();
        lblPedido = new javax.swing.JLabel();
        lblImporte = new javax.swing.JLabel();
        lblItemsDelPedido = new javax.swing.JLabel();
        botoneraSuperior = new javax.swing.JPanel();
        btnAltaPedido = new javax.swing.JButton();
        btnCancelarPedido = new javax.swing.JButton();
        btnPagarPedido = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setIconImage(getIconImage());

        panelMesas.setBackground(new java.awt.Color(153, 153, 255));

        tablaMesas.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tablaMesas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Mesa"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false
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
            tablaMesas.getColumnModel().getColumn(0).setResizable(false);
        }

        panelPedidos.setBackground(new java.awt.Color(153, 153, 255));

        tablaPedidos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tablaPedidos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null}
            },
            new String [] {
                "Pedido"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaPedidos.setRowHeight(48);
        tablaPedidos.getTableHeader().setReorderingAllowed(false);
        tablaPedidos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaPedidosMouseClicked(evt);
            }
        });
        panelPedidos.setViewportView(tablaPedidos);
        if (tablaPedidos.getColumnModel().getColumnCount() > 0) {
            tablaPedidos.getColumnModel().getColumn(0).setResizable(false);
        }

        panelItems.setBackground(new java.awt.Color(153, 153, 255));

        tablaItems.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Producto", "Cant", "Estado"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Object.class
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
        tablaItems.setRowHeight(32);
        tablaItems.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaItemsMouseClicked(evt);
            }
        });
        panelItems.setViewportView(tablaItems);
        if (tablaItems.getColumnModel().getColumnCount() > 0) {
            tablaItems.getColumnModel().getColumn(0).setPreferredWidth(20);
            tablaItems.getColumnModel().getColumn(0).setMaxWidth(20);
            tablaItems.getColumnModel().getColumn(1).setPreferredWidth(200);
            tablaItems.getColumnModel().getColumn(1).setMaxWidth(250);
            tablaItems.getColumnModel().getColumn(2).setPreferredWidth(50);
            tablaItems.getColumnModel().getColumn(2).setMaxWidth(50);
            tablaItems.getColumnModel().getColumn(3).setPreferredWidth(80);
            tablaItems.getColumnModel().getColumn(3).setMaxWidth(140);
        }

        botoneraVertical.setBackground(new java.awt.Color(153, 153, 255));

        btnIncluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/flecha_izquierda1_32x32 .png"))); // NOI18N
        btnIncluir.setText("Incluir Producto");
        btnIncluir.setEnabled(false);
        btnIncluir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnIncluir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnIncluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIncluirActionPerformed(evt);
            }
        });

        btnAumentar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/mas32x32.png"))); // NOI18N
        btnAumentar.setText("Aumentar Cantidad");
        btnAumentar.setEnabled(false);
        btnAumentar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAumentar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAumentar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAumentarActionPerformed(evt);
            }
        });

        btnDisminuir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/menos32x32.png"))); // NOI18N
        btnDisminuir.setText("Disminuir Cantidad");
        btnDisminuir.setEnabled(false);
        btnDisminuir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDisminuir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDisminuir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDisminuirActionPerformed(evt);
            }
        });

        btnSolicitarItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/itemSolicitar1_32x32.png"))); // NOI18N
        btnSolicitarItem.setText("Solicitar Item");
        btnSolicitarItem.setEnabled(false);
        btnSolicitarItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSolicitarItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSolicitarItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSolicitarItemActionPerformed(evt);
            }
        });

        btnServirItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/mesero1_32x32.png"))); // NOI18N
        btnServirItem.setText("Servir Item");
        btnServirItem.setEnabled(false);
        btnServirItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnServirItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnServirItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnServirItemActionPerformed(evt);
            }
        });

        btnCancelarItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/cancelar3_32x32.png"))); // NOI18N
        btnCancelarItem.setText("Cancelar Item");
        btnCancelarItem.setEnabled(false);
        btnCancelarItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCancelarItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCancelarItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarItemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout botoneraVerticalLayout = new javax.swing.GroupLayout(botoneraVertical);
        botoneraVertical.setLayout(botoneraVerticalLayout);
        botoneraVerticalLayout.setHorizontalGroup(
            botoneraVerticalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(botoneraVerticalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(botoneraVerticalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnServirItem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSolicitarItem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDisminuir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAumentar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnIncluir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCancelarItem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        botoneraVerticalLayout.setVerticalGroup(
            botoneraVerticalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(botoneraVerticalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnIncluir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAumentar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDisminuir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSolicitarItem)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnServirItem)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancelarItem)
                .addContainerGap())
        );

        panelProductos.setBackground(new java.awt.Color(153, 153, 255));

        tablaProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Producto", "Descripcion", "Precio", "Categoría", "Despachado por"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.Double.class, java.lang.String.class, java.lang.String.class
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
        tablaProductos.setRowHeight(32);
        tablaProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaProductosMouseClicked(evt);
            }
        });
        panelProductos.setViewportView(tablaProductos);
        if (tablaProductos.getColumnModel().getColumnCount() > 0) {
            tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(100);
            tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(120);
            tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(20);
            tablaProductos.getColumnModel().getColumn(3).setPreferredWidth(50);
            tablaProductos.getColumnModel().getColumn(4).setPreferredWidth(50);
        }

        panleEncabezamientoProductos.setBackground(new java.awt.Color(153, 153, 255));

        cbCategorias.setBorder(javax.swing.BorderFactory.createTitledBorder("Mostrar solo categorías:"));
        cbCategorias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbCategoriasActionPerformed(evt);
            }
        });

        cbOrdenProductos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "por IdProducto", "por Nombre", "por Categoría" }));
        cbOrdenProductos.setBorder(javax.swing.BorderFactory.createTitledBorder("Orden de productos"));
        cbOrdenProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbOrdenProductosActionPerformed(evt);
            }
        });

        lblProductosActivos.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblProductosActivos.setText("Productos activos");

        javax.swing.GroupLayout panleEncabezamientoProductosLayout = new javax.swing.GroupLayout(panleEncabezamientoProductos);
        panleEncabezamientoProductos.setLayout(panleEncabezamientoProductosLayout);
        panleEncabezamientoProductosLayout.setHorizontalGroup(
            panleEncabezamientoProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panleEncabezamientoProductosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbOrdenProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 91, Short.MAX_VALUE)
                .addComponent(lblProductosActivos)
                .addGap(69, 69, 69)
                .addComponent(cbCategorias, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panleEncabezamientoProductosLayout.setVerticalGroup(
            panleEncabezamientoProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panleEncabezamientoProductosLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panleEncabezamientoProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panleEncabezamientoProductosLayout.createSequentialGroup()
                        .addGroup(panleEncabezamientoProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbOrdenProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbCategorias, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(23, 23, 23))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panleEncabezamientoProductosLayout.createSequentialGroup()
                        .addComponent(lblProductosActivos)
                        .addContainerGap())))
        );

        panelEncabezamientoItems.setBackground(new java.awt.Color(153, 153, 255));

        lblMesero.setText("1 Jorge Alberto González");
        lblMesero.setBorder(javax.swing.BorderFactory.createTitledBorder("Mesero"));

        lblMesa.setText("5 (Ocupada)");
        lblMesa.setBorder(javax.swing.BorderFactory.createTitledBorder("Mesa"));

        lblPedido.setText("4 16/10/23 15:45");
        lblPedido.setBorder(javax.swing.BorderFactory.createTitledBorder("Pedido"));

        lblImporte.setText("$ 15.314,50");
        lblImporte.setBorder(javax.swing.BorderFactory.createTitledBorder("Importe"));

        lblItemsDelPedido.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblItemsDelPedido.setText("Items del pedido");

        javax.swing.GroupLayout panelEncabezamientoItemsLayout = new javax.swing.GroupLayout(panelEncabezamientoItems);
        panelEncabezamientoItems.setLayout(panelEncabezamientoItemsLayout);
        panelEncabezamientoItemsLayout.setHorizontalGroup(
            panelEncabezamientoItemsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEncabezamientoItemsLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(lblMesero, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblMesa, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblImporte, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelEncabezamientoItemsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblItemsDelPedido)
                .addGap(162, 162, 162))
        );
        panelEncabezamientoItemsLayout.setVerticalGroup(
            panelEncabezamientoItemsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEncabezamientoItemsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelEncabezamientoItemsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMesero)
                    .addComponent(lblMesa)
                    .addComponent(lblPedido)
                    .addComponent(lblImporte))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblItemsDelPedido)
                .addGap(5, 5, 5))
        );

        botoneraSuperior.setBackground(new java.awt.Color(153, 153, 255));

        btnAltaPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/pedidoNuevo32x32.png"))); // NOI18N
        btnAltaPedido.setText("Nuevo Pedido");
        btnAltaPedido.setEnabled(false);
        btnAltaPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAltaPedidoActionPerformed(evt);
            }
        });

        btnCancelarPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/pedidoCancelar32x32.png"))); // NOI18N
        btnCancelarPedido.setText("Cancela Pedido");
        btnCancelarPedido.setEnabled(false);
        btnCancelarPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarPedidoActionPerformed(evt);
            }
        });

        btnPagarPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/pedidoPagar2_32x32.png"))); // NOI18N
        btnPagarPedido.setText("Pagar Pedido");
        btnPagarPedido.setEnabled(false);
        btnPagarPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPagarPedidoActionPerformed(evt);
            }
        });

        btnSalir.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/salida1_32x32.png"))); // NOI18N
        btnSalir.setText("Salir");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout botoneraSuperiorLayout = new javax.swing.GroupLayout(botoneraSuperior);
        botoneraSuperior.setLayout(botoneraSuperiorLayout);
        botoneraSuperiorLayout.setHorizontalGroup(
            botoneraSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(botoneraSuperiorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnAltaPedido)
                .addGap(18, 18, 18)
                .addComponent(btnCancelarPedido)
                .addGap(18, 18, 18)
                .addComponent(btnPagarPedido)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSalir)
                .addContainerGap())
        );
        botoneraSuperiorLayout.setVerticalGroup(
            botoneraSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(botoneraSuperiorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(botoneraSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSalir)
                    .addComponent(btnCancelarPedido)
                    .addComponent(btnAltaPedido)
                    .addComponent(btnPagarPedido))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(botoneraSuperior, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(panelMesas, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelPedidos, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelItems, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE))
                            .addComponent(panelEncabezamientoItems, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(botoneraVertical, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelProductos)
                            .addComponent(panleEncabezamientoProductos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(botoneraSuperior, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelEncabezamientoItems, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panleEncabezamientoProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelProductos, javax.swing.GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(panelMesas, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE)
                            .addComponent(panelPedidos, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(botoneraVertical, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelItems, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addGap(5, 5, 5))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	
	
	//=======================================================================================
	//=======================================================================================
	
	
	
	/**
	 * Incorpora todos los productos de las filas seleccionadas de la tabla de productos.
	 * Para cada uno de ellos, si el producto no está entre los items, lo incorpora al final
 Si el producto ya está en un itemSeleccionado anotado, incrementa la cantidad.
 Si el producto ya está en un itemSeleccionado no anotado, no puede modificar ese itemSeleccionado, 
 así que lo agrega al final como un nuevo itemSeleccionado con el mismo producto.
	 * @param evt 
	 */
    private void btnIncluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIncluirActionPerformed
        if (tablaProductos.getSelectedRow() == -1 || tablaPedidos.getSelectedRow() == -1){ // si hay alguna fila seleccionada en ambas tablas
            btnIncluir.setEnabled(false); // deshabilito botón incluir
			return;
        }
		
        int[] arregloFilasProductosSeleccionados = tablaProductos.getSelectedRows();
		for (int numfilaProductos:arregloFilasProductosSeleccionados){
			int idProducto = tablaProductosGetProducto(numfilaProductos).getIdProducto();//obtengo el producto

			//recorro la tabla de items para ver si está ese producto en la tabla (y que sea anotado)
			int numfilaItems = 0;
			while	(numfilaItems < tablaItems.getRowCount() && 
					  !(tablaItemsGetProducto(numfilaItems).getIdProducto() == idProducto && 
					    tablaItemsGetEstado(numfilaItems) == Item.EstadoItem.ANOTADO) 
					)
				numfilaItems++;
			
			//ahora salio porque lo encontro o termino la tabla
			if ( numfilaItems >= tablaItems.getRowCount() )  //no lo encontro... hay que agregarlo
				itemData.altaItem(new Item(idProducto, tablaPedidosGetIdPedidoSeleccionado(), 1, Item.EstadoItem.ANOTADO) ); //agrego el itemSeleccionado en la bd
			else {// lo encontró, hay que aumentar la cantidad
				Item item = itemData.getItem(tablaItemsGetIdItem(numfilaItems));
				item.setCantidad(item.getCantidad()+1);
				itemData.modificarItem(item);
			}
        } //for 
		
		cargarItems();
		mostrarLabelsEncabezamientoItems();
    }//GEN-LAST:event_btnIncluirActionPerformed

	
	
	
	
	/**
	 * Recorre las filas seleccionadas de la tabla de items incrementando las cantidades.
	 * Para ello debe asegurarse que sea un itemSeleccionado anotado: lo incrementa directamente
 Si no es un itemSeleccionado anotado, busca otro itemSeleccionado con el mismo producto (que sea anotado):
    - si lo encuentra incrementa a este itemSeleccionado con el mismo producto
    - si no lo encuentra, agrega un itemSeleccionado nuevo al final con el mismo producto.
	 * @param evt 
	 */
    private void btnAumentarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAumentarActionPerformed
		if (tablaItems.getSelectedRow() == -1){ 
			deshabilitarBotonesItems(); 
			return;
		}
			
		int[] arregloFilasItemsSeleccionados = tablaItems.getSelectedRows();
		for (int numfilaItemSeleccionado:arregloFilasItemsSeleccionados) { //recorro todas las filas seleccionadas de la tablaItems
			int idItemSeleccionado = tablaItemsGetIdItem(numfilaItemSeleccionado);//averiguamos el idItemSeleccionado de esa fila seleccionada
			Item itemSeleccionado = itemData.getItem(idItemSeleccionado); //averiguamos el itemSeleccionado de esa fila seleccionada
			
			if (itemSeleccionado.getEstado() == Item.EstadoItem.ANOTADO) {// si es anotado lo puedo modificar
				itemSeleccionado.setCantidad(itemSeleccionado.getCantidad()+1); //le incremento la cantidad
				itemData.modificarItem(itemSeleccionado);						// modifico el item en la bd
			} else {//si no es anotado, no lo puedo modificar. Busco otro itemSeleccionado con el mismo producto para subir cantidad, sino agrego uno al final
				//recorro la tabla de items para ver si está ese producto en la tabla (y que sea anotado)
				int numfila = 0;
				while	(numfila < tablaItems.getRowCount() && 
						  !(tablaItemsGetProducto(numfila).getIdProducto() == itemSeleccionado.getIdProducto() && 
						    tablaItemsGetEstado(numfila) == Item.EstadoItem.ANOTADO ) 
						)
					numfila++;

				//ahora salio porque lo encontro (en numfila) o termino la tabla
				if ( numfila >= tablaItems.getRowCount() )  //no lo encontro... hay que agregarlo
					itemData.altaItem(new Item(itemSeleccionado.getIdProducto(), tablaPedidosGetIdPedidoSeleccionado(), 1, Item.EstadoItem.ANOTADO) ); //agrego el itemSeleccionado en la bd
				else {// lo encontró, hay que aumentar la cantidad
					Item item2 = itemData.getItem(tablaItemsGetIdItem(numfila)); //averiguo los datos del item encontrado
					item2.setCantidad(item2.getCantidad()+1); // le incremento la cantidad
					itemData.modificarItem(item2);			  // modifico el item en la bd
				}
			}//else
		} //for
		
		cargarItems();
		mostrarLabelsEncabezamientoItems();
		
		//restauro las filas que tenia seleccionadas
		for (int fila:arregloFilasItemsSeleccionados)
			tablaItems.addRowSelectionInterval(fila, fila);
    }//GEN-LAST:event_btnAumentarActionPerformed

	
	
	/**
	 * Disminuye las cantidades de las filas seleccionadas de la tabla items (si el itemSeleccionado es Anotado)
     - Si es > 0 lo decrementa
     - si es 0 lo elimina
 Si el itemSeleccionado no es anotado, no lo puede modificar... hace un ruido de error.
	 * @param evt 
	 */
    private void btnDisminuirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDisminuirActionPerformed
		if (tablaItems.getSelectedRow() == -1){ 
			deshabilitarBotonesItems(); 
			return;
		}
		
		int[] arregloFilasItemsSeleccionados = tablaItems.getSelectedRows();
		boolean bajeAlgunItem=false; //si di de baja algun itemSeleccionado
		for (int numfilaItems:arregloFilasItemsSeleccionados) { //recorro todas las filas seleccionadas de la tablaItems
			int idItem = tablaItemsGetIdItem(numfilaItems);//averiguamos el idItemSeleccionado
			Item item = itemData.getItem(idItem);
			if (item.getEstado() == Item.EstadoItem.ANOTADO) { //si es anotado lo puedo modificar
				if (item.getCantidad() > 1) { // si hay varios, disminuyo la cantidad
					item.setCantidad(item.getCantidad()-1);
					itemData.modificarItem(item);
				} else { // solo hay uno, lo elimino
					itemData.bajaItem(item);
					bajeAlgunItem = true;
				}
			} else { //si no es anotado NO lo puedo modificar
				Utils.sonido1("src/sonidos/chord.wav");
			}
		} //for
		
		cargarItems();
		mostrarLabelsEncabezamientoItems();
		
		if (bajeAlgunItem) // si di de baja algun itemSeleccionado las filas seleccionadas en los items pueden no ser válidas, no selecciono nada, deshabilito botones
			deshabilitarBotonesItems();
		else { // como no hubo ninguna baja, restauro las filas que tenia seleccionadas
			for (int fila:arregloFilasItemsSeleccionados)
				tablaItems.addRowSelectionInterval(fila, fila);
		}
    }//GEN-LAST:event_btnDisminuirActionPerformed


	
	
	
	
	/**
	 * Solicita los productos seleccionados de la tabla Items, siempre que esten en estado Anotados
	 * @param evt 
	 */
    private void btnSolicitarItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSolicitarItemActionPerformed
        if (tablaItems.getSelectedRow() == -1){ 
			deshabilitarBotonesItems(); 
			return;
		}
			
		int[] arregloFilasItemsSeleccionados = tablaItems.getSelectedRows();
		for (int numfilaItems:arregloFilasItemsSeleccionados) { //recorro todas las filas seleccionadas de la tablaItems
			int idItem = tablaItemsGetIdItem(numfilaItems);//averiguamos el idItemSeleccionado
			Item item = itemData.getItem(idItem);
			if (item.getEstado() == Item.EstadoItem.ANOTADO) {// si es anotado lo puedo modificar
				item.setEstado(Item.EstadoItem.SOLICITADO);
				itemData.modificarItem(item);
				// me comunico con el servicio que despacha el producto de este item.
				comunicarConServicio(mapaServicios.get(tablaItemsGetProducto(numfilaItems).getDespachadoPor()), "S " + item.getIdItem());
			} else {//si no es anotado, no lo puedo modificar. 
				Utils.sonido1("src/sonidos/chord.wav");
			}//else
		} //for
		
		cargarItems();
		mostrarLabelsEncabezamientoItems();
		
		//restauro las filas que tenia seleccionadas
		for (int fila:arregloFilasItemsSeleccionados)
			tablaItems.addRowSelectionInterval(fila, fila);
    }//GEN-LAST:event_btnSolicitarItemActionPerformed
	
	
	
	
	/**
	 * Entrega los items seleccionados de la tabla items siempre que esten en estado despachado
	 * @param evt 
	 */
    private void btnServirItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnServirItemActionPerformed
          if (tablaItems.getSelectedRow() == -1){ 
			deshabilitarBotonesItems(); 
			return;
		}
			
		int[] arregloFilasItemsSeleccionados = tablaItems.getSelectedRows();
		for (int numfilaItems:arregloFilasItemsSeleccionados) { //recorro todas las filas seleccionadas de la tablaItems
			int idItem = tablaItemsGetIdItem(numfilaItems);//averiguamos el idItemSeleccionado
			Item item = itemData.getItem(idItem);
			if (item.getEstado() == Item.EstadoItem.DESPACHADO || item.getEstado() == Item.EstadoItem.SOLICITADO) {// si es anotado lo puedo modificar
				item.setEstado(Item.EstadoItem.ENTREGADO);
				itemData.modificarItem(item);
			} else {//si no es despachado, no lo puedo modificar. 
				Utils.sonido1("src/sonidos/chord.wav");
			}//else
		} //for
		
		cargarItems();
		mostrarLabelsEncabezamientoItems();
		
		//restauro las filas que tenia seleccionadas
		for (int fila:arregloFilasItemsSeleccionados)
			tablaItems.addRowSelectionInterval(fila, fila);
    }//GEN-LAST:event_btnServirItemActionPerformed

	
	
	/** 
	 * Se hizo click sobre una mesa seleccionandola, por lo que hay que cargar
	 * los pedidos correspondientes a dicha mesa y reflejar los nuevos datos
	 * en el encabezamiento.
	 * @param evt 
	 */
    private void tablaMesasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaMesasMouseClicked
        cargarPedidos();
		cargarItems();
		mostrarLabelsEncabezamientoItems();
		deshabilitarBotonesItems();
		deshabilitarBotonesPedidos();
		btnAltaPedido.setEnabled(true);
		// mostrarLabelsEncabezamientoItems();
    }//GEN-LAST:event_tablaMesasMouseClicked

	
	
	/**
	 * Se hizo click sobre un pedido, por lo que se cargan los items correspondientes
	 * a ese pedido y se actualiza el encabezamiento para reflejar los nuevos datos.
	 * @param evt 
	 */
    private void tablaPedidosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaPedidosMouseClicked
        cargarItems();
		mostrarLabelsEncabezamientoItems();
		deshabilitarBotonesItems();
		btnCancelarPedido.setEnabled(true);
		if (tablaProductos.getSelectedRow() != -1)
			btnIncluir.setEnabled(true);
		habilitarBotonesPedidos();
    }//GEN-LAST:event_tablaPedidosMouseClicked

	
	
	/**
	 * Se hizo click sobre un item para seleccionarlo, por lo que ya se pueden
	 * habilitar los botones de items que operan sobre esos items seleccionados.
	 * @param evt 
	 */
    private void tablaItemsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaItemsMouseClicked
        habilitarBotonesItems();
    }//GEN-LAST:event_tablaItemsMouseClicked

	
	
	/**
	 * Se hizo click sobre un producto para seleccionarlo. Por ello ya se puede
	 * habilitar el botón btnIncluirProducto.
	 * @param evt 
	 */
    private void tablaProductosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaProductosMouseClicked
        if (tablaPedidos.getSelectedRow() != -1)
			btnIncluir.setEnabled(true);
    }//GEN-LAST:event_tablaProductosMouseClicked

	
	
	
	/**
	 * Cambió el orden que se eligió para mostrar los productos, por lo que 
	 * hay que cargar nuevamente los productos con el nuevo orden.
	 * @param evt 
	 */
    private void cbOrdenProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbOrdenProductosActionPerformed
        if (cbOrdenProductos.getSelectedIndex() == 0)
			ordenacion = OrdenacionProducto.PORIDPRODUCTO;
        else if (cbOrdenProductos.getSelectedIndex() == 1)
			ordenacion = OrdenacionProducto.PORNOMBRE;
        else
			ordenacion = OrdenacionProducto.PORIDCATEGORIAYNOMBRE;

        cargarProductos();
    }//GEN-LAST:event_cbOrdenProductosActionPerformed

	
	
	/**
	 * Cambió la selección de categorías de productos que se mostrarán, por lo
	 * que hay que cargar nuevamente los productos con el nuevo filtro.
	 * @param evt 
	 */
    private void cbCategoriasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbCategoriasActionPerformed
        if (mapaServicios != null && mapaCategorias != null)
			cargarProductos();
    }//GEN-LAST:event_cbCategoriasActionPerformed

	
	
	/**
	 * Dado un numero de fila de la tablaItems, cancela todas las cantidades del item correspondiente:
	 * Si el item era anotado, solo disminuye en 1 su cantidad (o lo borra si solo quedaba 1)
	 * Si el item era cancelado, no lo puede eliminar (si conSonido es true, hace un ruido por el error)
	 * Si el item era de otro tipo, disminuye en 1 su cantidad (o lo borra si
	 *    solo quedaba 1) y lo agrega al final de la tabla como Cancelado ( o si
	 *    ya había un item cancelado con el mismo idProducto, lo incrementa en 1)
	 * @param numfilaItems fila de tablaItems con el item a cancelar
	 * @param conSonido True si hara sonido cuando intente cancelar un item ya cancelado.
	 * @return true si tuvo que borrar un item de la lista (cuando la cantidad era 1)
	 */
	private void cancelarTodosLosItem(int numfilaItems){
		boolean bajeAlgunItem = false;
		int idItem = tablaItemsGetIdItem(numfilaItems);//averiguamos el idItemSeleccionado
		Item item = itemData.getItem(idItem);
		if (item.getEstado() == Item.EstadoItem.ANOTADO) { //si es anotado lo puedo decrementar directamente
			itemData.bajaItem(item);
		} else if (item.getEstado() == Item.EstadoItem.CANCELADOVISTO ||
				item.getEstado() == Item.EstadoItem.CANCELADOVISTO) { //si ya esta cancelado y visto no se puede volver a cancelar
			// no hago nada Utils.sonido1("src/sonidos/chord.wav");
		} else if (item.getEstado() == Item.EstadoItem.SOLICITADO ){// SOLICITADO, lo cancelo y aviso al servicio de despacho (cocina)
			item.setEstado(Item.EstadoItem.CANCELADO);
			itemData.modificarItem(item);
			// me comunico con el servicio que despacha el producto de este item.
			comunicarConServicio(mapaServicios.get(tablaItemsGetProducto(numfilaItems).getDespachadoPor()), "C " + item.getIdItem());
		} else {
			item.setEstado(Item.EstadoItem.CANCELADO);
			itemData.modificarItem(item);
		}
	} // cancelarTodosLosItem
	
	
	
	
	/**
	 * Dado un numero de fila de la tablaItems, cancela 1 item desminuyendo su cantidad en 1:
	 * Si el item era anotado, solo disminuye en 1 su cantidad (o lo borra si solo quedaba 1)
	 * Si el item era cancelado, no lo puede eliminar (si conSonido es true, hace un ruido por el error)
	 * Si el item era de otro tipo, disminuye en 1 su cantidad (o lo borra si
	 *    solo quedaba 1) y lo agrega al final de la tabla como Cancelado ( o si
	 *    ya había un item cancelado con el mismo idProducto, lo incrementa en 1)
	 * @param numfilaItems fila de tablaItems con el item a cancelar
	 * @param conSonido True si hara sonido cuando intente cancelar un item ya cancelado.
	 * @return true si tuvo que borrar un item de la lista (cuando la cantidad era 1)
	 */
	private boolean cancelar1Item(int numfilaItems){
		boolean bajeAlgunItem = false;
		int idItem = tablaItemsGetIdItem(numfilaItems);//averiguamos el idItemSeleccionado
		Item item = itemData.getItem(idItem);
		if (item.getEstado() == Item.EstadoItem.ANOTADO) { //si es anotado lo puedo decrementar directamente
			if (item.getCantidad() > 1) { // si hay varios, disminuyo la cantidad
				item.setCantidad(item.getCantidad()-1);
				itemData.modificarItem(item);
			} else { // solo hay uno, lo elimino
				itemData.bajaItem(item);
				bajeAlgunItem = true;
			}
		} else if (item.getEstado() == Item.EstadoItem.CANCELADO ||
				item.getEstado() == Item.EstadoItem.CANCELADOVISTO) { //si ya esta cancelado no se puede volver a cancelar
			Utils.sonido1("src/sonidos/chord.wav");
		} else {//if (item.getEstado()==Item.EstadoItem.SOLICITADO || 
				// item.getEstado()==Item.EstadoItem.DESPACHADO) || 
				// item.getEstado()==Item.EstadoItem.ENTREGADO      // es decir, en cualquier otro caso, lo cancelo
			if (item.getCantidad() > 1) { // si hay varios, disminuyo la cantidad y lo pongo al final como cancelado
				item.setCantidad(item.getCantidad()-1);
				itemData.modificarItem(item);

				//Busco otro item cancelado con el mismo producto para subir cantidad, sino agrego uno al final uno cancelado
				//recorro la tabla de items para ver si está ese producto en la tabla (y que sea cancelado)
				int numfila = 0;
				while	(numfila < tablaItems.getRowCount() && 
						  !(tablaItemsGetProducto(numfila).getIdProducto() == item.getIdProducto() && 
							tablaItemsGetEstado(numfila) == Item.EstadoItem.CANCELADO ) 
						)
					numfila++;

				//ahora salio porque lo encontro (en numfila) o termino la tabla
				if ( numfila >= tablaItems.getRowCount() )  //no lo encontro... hay que agregarlo al final
					itemData.altaItem(new Item(item.getIdProducto(), tablaPedidosGetIdPedidoSeleccionado(), 1, Item.EstadoItem.CANCELADO) ); //agrego el item en la bd
				else {// encontro otro cancelado con el mismo idProducto, hay que aumentar la cantidad
					Item item2 = itemData.getItem(tablaItemsGetIdItem(numfila)); //averiguo los datos del item encontrado
					item2.setCantidad(item2.getCantidad()+1); // le incremento la cantidad
					itemData.modificarItem(item2);			  // modifico el item en la bd
				}
				
				//ya se modificó el producto. Si ese producto era SOLICITADO, comunico su cancelación al servicio de despacho (cocina)
				if (item.getEstado() == Item.EstadoItem.SOLICITADO) {
					comunicarConServicio(mapaServicios.get(tablaItemsGetProducto(numfilaItems).getDespachadoPor()), 
							"C " + item.getIdItem()); // me comunico con el servicio que despacha el producto de este item.
				}
				
			} else { // solo hay uno, lo marco como cancelado
				if (item.getEstado() == Item.EstadoItem.SOLICITADO) {
					item.setEstado(Item.EstadoItem.CANCELADO);
					itemData.modificarItem(item);
					comunicarConServicio(mapaServicios.get(tablaItemsGetProducto(numfilaItems).getDespachadoPor()),
							"C " + item.getIdItem()); // me comunico con el servicio que despacha el producto de este item.
				} else {
					item.setEstado(Item.EstadoItem.CANCELADO);
					itemData.modificarItem(item);
				}
			}
		}
		return bajeAlgunItem;
	} // cancelar1Item
	
	
	
	/**
	 * Cancela un item seleccionado de tablaItems. 
	 * Si el item es anotado, lo disminuye (o lo borra si solo quedaba cantidad 1)
	 * Si el item es cancelado, no lo puede volver a cancelar, hace sonido
	 * Cualquier otro estado, lo disminuye (o borra si solo queda cantidad 1) y lo agrega
	 * al final como item cancelado (o si ya había otro item cancelado con el 
	 * mismo idProducto, incrementa en 1 la cantidad de ese item cancelado).
	 * @param evt 
	 */
    private void btnCancelarItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarItemActionPerformed
		if (tablaItems.getSelectedRow() == -1){ 
			deshabilitarBotonesItems(); 
			return;
		}
		
		int[] arregloFilasItemsSeleccionados = tablaItems.getSelectedRows();
		boolean bajeAlgunItem=false; //si di de baja algun itemSeleccionado
		for (int numfilaItems:arregloFilasItemsSeleccionados) { //recorro todas las filas seleccionadas de la tablaItems
			bajeAlgunItem = bajeAlgunItem || cancelar1Item(numfilaItems); //cancelo el item de la fila especificada. Si no puede cancelarse, hace sonido	
		} //for
		
		cargarItems();
		mostrarLabelsEncabezamientoItems();
		
		if (bajeAlgunItem) // si di de baja algun itemSeleccionado las filas seleccionadas en los items pueden no ser válidas, no selecciono nada, deshabilito botones
			deshabilitarBotonesItems();
		else { // como no hubo ninguna baja, restauro las filas que tenia seleccionadas
			for (int fila:arregloFilasItemsSeleccionados)
				tablaItems.addRowSelectionInterval(fila, fila);
		}
    }//GEN-LAST:event_btnCancelarItemActionPerformed

	
	
	/**
	 * Da de alta un nuevo pedido, cambiando el estado de la mesa correspondiente a Atendida
	 * @param evt 
	 */
    private void btnAltaPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAltaPedidoActionPerformed
        //doy de alta el nuevo pedido
		Pedido pedido = new Pedido(tablaMesasGetIdMesaSeleccionada(), 
				mesero.getIdServicio(), LocalDateTime.now(), Pedido.EstadoPedido.ACTIVO);
		pedidoData.altaPedido(pedido); // lo agrego a la bd
		
		//cambio el estado de la mesa a Atendida
		Mesa mesa = tablaMesasGetMesaSeleccionada();
		if (mesa.getEstado() != Mesa.EstadoMesa.ATENDIDA) {
			mesa.setEstado(Mesa.EstadoMesa.ATENDIDA);
			mesaData.modificarMesa(mesa);
			
			//Mandar a Recepcion un mensaje informando que se atendió la mesa
			//recorro todos los elementos de recepcion
			for (Servicio recepcionista:listaRecepcionistas)
				comunicarConServicio(recepcionista,	"M " + mesa.getIdMesa()); // me comunico con el recepcionista para informar cambio de estado de mesa
		
		}
		
		//cargo los datos
		cargarPedidos();
		
		//deshabilito boton de cancelarPedido (no quedó ningun pedido seleccionado) y botonesItems
		deshabilitarBotonesItems();
		deshabilitarBotonesPedidos();
		
    }//GEN-LAST:event_btnAltaPedidoActionPerformed

	
	/** 
	 * Cancela el pedido, cambiando también el estado de cada uno de sus
	 * items a Cancelado (si no era ya Cancelado o CanceladoVisto)
	 * @param evt 
	 */
    private void btnCancelarPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarPedidoActionPerformed
		Object[] opciones = { "SI, cancélalo", "NO, no lo canceles" };
		int respuesta = JOptionPane.showOptionDialog(null, 
			"PRECAUCIÓN:\n" + 
			"           Si cancela el pedido se cancelaran TODOS su items.\n           " + 
			"                 ¿Está seguro que desea cancelarlo?                    ", 
			"Precaución",
			JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
			null, opciones, opciones[0]);
		//respuesta = 0 SI, 1 NO
		if (respuesta == 1) // NO
			return;
		
		//Acá cancelo el pedido: Primero cancelo cada uno de sus items
		for (int numfilaItems = 0; numfilaItems < tablaItems.getRowCount(); numfilaItems++) { //recorro todas las filas de la tablaItems
			cancelarTodosLosItem(numfilaItems); //cancelo el item de la tablaItems de esa fila
		} //for
		
		//Marco el pedido como cancelado
		Pedido pedido = tablaPedidosGetPedidoSeleccionado();
		pedido.setEstado(Pedido.EstadoPedido.CANCELADO);
		pedidoData.modificarPedido(pedido);
        //Utils.sonido1("src/sonidos/agua.wav");
		Utils.sonido1("src/sonidos/reciclar.wav");
		cargarPedidos();
		if (tablaPedidos.getRowCount() == 0) { // si no tiene pedidos
			//libero la mesa, queda libre
			Mesa mesa = tablaMesasGetMesaSeleccionada();
			mesa.setEstado(Mesa.EstadoMesa.LIBRE);
			mesaData.modificarMesa(mesa);
			cargarMesas();
			cargarPedidos();
		}
		deshabilitarBotonesItems();
		deshabilitarBotonesPedidos();
		mostrarLabelsEncabezamientoItems();
    }//GEN-LAST:event_btnCancelarPedidoActionPerformed

	
	/** Cierra la aplicación */
    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        
		servidor.pararEjecucion(); // detiene el servidor que escucha mensajes en la red.
		comunicarConServicio(mesero, "Desconectando el servidor..."); //mando este mensaje para que el servidor deje de esperar y vea que hay que parar la ejecución
		dispose(); // cierra la ventana
    }//GEN-LAST:event_btnSalirActionPerformed

	
	/**
	 * Paga el pedido, cambiando el estado del pedido a Pagado y cambiando
	 * el estado de cada uno de sus items a Entregado (si no eran Cancelado o CanceladoVisto)
	 * @param evt 
	 */
    private void btnPagarPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPagarPedidoActionPerformed
        Object[] opciones = { "SI, págalo", "NO, no lo pagues" };
		int respuesta = JOptionPane.showOptionDialog(null, 
			"PRECAUCIÓN:\n" + 
			"           Si paga el pedido TODOS su items quedarán como Entregados.\n           " + 
			"                 ¿Está seguro que desea pagarlo?                    ", 
			"Precaución",
			JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
			null, opciones, opciones[0]);
		//respuesta = 0 SI, 1 NO
		if (respuesta == 1) // NO
			return;
		
		//Acá cambio el estado de todos los items Anotados, Solicitados, Despachados por Entregados
		for (int numfilaItems = 0; numfilaItems < tablaItems.getRowCount(); numfilaItems++) { //recorro todas las filas de la tablaItems
			Item.EstadoItem estado = tablaItemsGetEstado(numfilaItems);
			if (estado == Item.EstadoItem.ANOTADO || estado == Item.EstadoItem.SOLICITADO || estado == Item.EstadoItem.DESPACHADO) {
				Item item = itemData.getItem(tablaItemsGetIdItem(numfilaItems));
				item.setEstado(Item.EstadoItem.ENTREGADO);
				itemData.modificarItem(item);
			}
		} //for
		
		//Marco el pedido como pagado
		Pedido pedido = tablaPedidosGetPedidoSeleccionado();
		pedido.setEstado(Pedido.EstadoPedido.PAGADO);
		pedidoData.modificarPedido(pedido);
		Utils.sonido1("src/sonidos/cajaregistradora2.wav");
		cargarPedidos();
		if (tablaPedidos.getRowCount() == 0) { // si no tiene pedidos
			//libero la mesa, queda libre
			Mesa mesa = tablaMesasGetMesaSeleccionada();
			mesa.setEstado(Mesa.EstadoMesa.LIBRE);
			mesaData.modificarMesa(mesa);
			cargarMesas();
			cargarPedidos();
			
			//Mandar a Recepcion un mensaje informando que se atendió la mesa
			//recorro todos los elementos de recepcion
			for (Servicio recepcionista:listaRecepcionistas)
				comunicarConServicio(recepcionista,	"M " + mesa.getIdMesa()); // me comunico con el recepcionista para informar cambio de estado de mesa
		
		}
		deshabilitarBotonesItems();
		deshabilitarBotonesPedidos();
		mostrarLabelsEncabezamientoItems();
    }//GEN-LAST:event_btnPagarPedidoActionPerformed

	
	
	
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
    private javax.swing.JPanel botoneraSuperior;
    private javax.swing.JPanel botoneraVertical;
    private javax.swing.JButton btnAltaPedido;
    private javax.swing.JButton btnAumentar;
    private javax.swing.JButton btnCancelarItem;
    private javax.swing.JButton btnCancelarPedido;
    private javax.swing.JButton btnDisminuir;
    private javax.swing.JButton btnIncluir;
    private javax.swing.JButton btnPagarPedido;
    private javax.swing.JButton btnSalir;
    private javax.swing.JButton btnServirItem;
    private javax.swing.JButton btnSolicitarItem;
    private javax.swing.JComboBox<Categoria> cbCategorias;
    private javax.swing.JComboBox<String> cbOrdenProductos;
    private javax.swing.JLabel lblImporte;
    private javax.swing.JLabel lblItemsDelPedido;
    private javax.swing.JLabel lblMesa;
    private javax.swing.JLabel lblMesero;
    private javax.swing.JLabel lblPedido;
    private javax.swing.JLabel lblProductosActivos;
    private javax.swing.JPanel panelEncabezamientoItems;
    private javax.swing.JScrollPane panelItems;
    private javax.swing.JScrollPane panelMesas;
    private javax.swing.JScrollPane panelPedidos;
    private javax.swing.JScrollPane panelProductos;
    private javax.swing.JPanel panleEncabezamientoProductos;
    private javax.swing.JTable tablaItems;
    private javax.swing.JTable tablaMesas;
    private javax.swing.JTable tablaPedidos;
    private javax.swing.JTable tablaProductos;
    // End of variables declaration//GEN-END:variables


	
	//========================================================================================================
	//========================================================================================================
	
	
	
	/**
	 * Renderer de celdas de la tablaMesas. Pone el color según el estado de la 
	 * mesa (Libre, Ocupada, Atendida). La info la obtiene de mapaMesas;
	 *  en la definicion de la tabla se pone
	 *		tabla.getColumnModel().getColumn(3).setCellRenderer(new generalRenderer());
	 *	de esa manera aplica el renderer a la columna, los colores son diferentes si la celda esta seleccionada o no.
	 */
	class RendererMesas extends JLabel implements TableCellRenderer {    
		//Font f = new Font( "Helvetica",Font.PLAIN,10 );
		Color colorSeleccionado = new Color(184,207,229); //new Color(0,120,215); //new Color(117, 204, 169);
		Color colorGeneral = Color.BLUE; //new Color(255,255,255); //new Color(225, 244, 238);
		Color colorLibre = Color.WHITE;
		Color colorOcupada = Color.RED;
		Color colorAtendida = Color.GREEN;

		public RendererMesas() {
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean isSelected, boolean hasFocus, int row, int column) {
			setHorizontalAlignment(CENTER);
			if (isSelected) {
				setBackground(colorSeleccionado);
			} else if ( mapaMesas.get((Integer)valor).getEstado() == Mesa.EstadoMesa.LIBRE ) {
				setBackground(colorLibre);
			} else if ( mapaMesas.get((Integer)valor).getEstado() == Mesa.EstadoMesa.OCUPADA ) {
				setBackground(colorOcupada);
			} else if ( mapaMesas.get((Integer)valor).getEstado() == Mesa.EstadoMesa.ATENDIDA ) {
				setBackground(colorAtendida);
			} else {
				setBackground(colorGeneral);
			}
			try {
				//setFont(f);
				setText(valor.toString());
			} catch (NullPointerException npe) {
				System.out.println(valor.toString());
				setText("0");
			}
			return this;
		}
	} //class rendererMesas
	
	
	
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
				else if (column==3) //columna derecha, con borde derecho
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


