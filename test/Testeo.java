
import accesoadatos.ItemData;
import accesoadatos.MesaData;
import accesoadatos.PedidoData;
import accesoadatos.ProductoData;
import accesoadatos.ServicioData;
import utiles.Utils;
import static utiles.Utils.dateTimeBD2LocalDateTime;
import static utiles.Utils.localDateTime2DateTimeBD;
import entidades.Item;
import entidades.Mesa;
import entidades.Pedido;
import entidades.Producto;
import entidades.Servicio;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author john
 */
public class Testeo {

		public static void pruebaProductoData(){
		ProductoData productoData = new ProductoData();
		
		// prueba de alta producto
		Producto p = new Producto("Coca Cola Light", "Gaseosa Coca Cola Light Sin azucar 1L", 12, 1200.5, true, 1, 3);
		//productoData.altaProducto(p);
		//Producto p2 = productoData.getProducto(p.getIdProducto());
		//System.out.println("El producto agregado y recuperado de la tabla es " + p2);
		
		//prueba de baja producto
		//productoData.bajaProducto(84);
		//p = productoData.getProducto(81);
		//p.setNombre("Cerveza Quilmes 1L");
		//productoData.modificarProducto(p);
		
		
		
		List<Producto> listaProductos = productoData.getListaProductos();
		listaProductos.stream().forEach(producto -> System.out.println(producto));
		
//		System.out.println("");
//		System.out.println("POR CRITERIO DE BUSQUEDA");
//		System.out.println("========================");
//		listaProductos = productoData.getListaProductosXCriterioDeBusqueda(-1, "", -1, -1, ProductoData.OrdenacionProducto.PORNOMBRE);
//		
//		for (Producto producto: listaProductos)
//			System.out.println("**** " + producto);
//		
//		System.out.println("");
//		System.out.println("POR OTRO ORDEN");
//		System.out.println("========================");
//		listaProductos = productoData.getListaProductos(ProductoData.OrdenacionProducto.PORIDPRODUCTO);
//		
//		for (Producto producto: listaProductos)
//			System.out.println("**** " + producto);
//		
		
		p = productoData.getProducto("Taco de cerdo");
		System.out.println("=================");
		System.out.println(p);
		p.setPrecio(3555.0);
		productoData.modificarProducto(p);
		p = productoData.getProducto("Taco de cerdo");
		System.out.println(p);
	}
	


	
	
	public static void pruebaMesaData(){
		MesaData mesaData = new MesaData();
		
		// prueba de alta producto
		Mesa m = new Mesa(4, Mesa.EstadoMesa.ATENDIDA, 0);
		//mesaData.altaMesa(m);
		//Producto p2 = productoData.getProducto(p.getIdProducto());
		//System.out.println("El producto agregado y recuperado de la tabla es " + p2);
		
		//prueba de baja producto
		//productoData.bajaProducto(84);
		//p = productoData.getProducto(81);
		//p.setNombre("Cerveza Quilmes 1L");
		//productoData.modificarProducto(p);
		
		
		
		List<Mesa> listaMesas = mesaData.getListaMesas();
		
		for (Mesa mesa: listaMesas)
			System.out.println(mesa);
		
		System.out.println("");
		System.out.println("POR CRITERIO DE BUSQUEDA");
		System.out.println("========================");
		listaMesas = mesaData.getListaMesasXCriterioDeBusqueda(-1, -1, Mesa.EstadoMesa.ATENDIDA, -1, MesaData.OrdenacionMesa.PORCAPACIDAD);
		
		for (Mesa mesa: listaMesas)
			System.out.println("**** " + mesa);
		
//		System.out.println("");
//		System.out.println("POR OTRO ORDEN");
//		System.out.println("========================");
//		listaProductos = productoData.getListaProductos(ProductoData.OrdenacionProducto.PORIDPRODUCTO);
//		
//		for (Producto producto: listaProductos)
//			System.out.println("**** " + producto);
//		
		
		System.out.println("=========================");
		m = mesaData.getMesa(4);
		System.out.println(m);
		m.setCapacidad(1);
		m.setEstado(Mesa.EstadoMesa.OCUPADA);
		//mesaData.modificarMesa(m);
		System.out.println(m);
		
		//mesaData.bajaMesa(25);
	}

	public static void pruebaPedidoData(){
		PedidoData pedidoData = new PedidoData();

		// prueba de alta producto
		//						int idMesa, int idMesero, LocalDateTime fechaHora, boolean pagado
		Pedido p = new Pedido(1, 2, LocalDateTime.now(), Pedido.EstadoPedido.ACTIVO);
		//System.out.println("Pedido: " + p);
		//pedidoData.altaPedido(p);
		//Pedido p2 = pedidoData.getPedido(1);
		//System.out.println("El pedido agregado y recuperado de la tabla es " + p2);

		//prueba de baja pedido
		//pedidoData.bajaPedido(2);
		//p = productoData.getProducto(81);
		//p.setNombre("Cerveza Quilmes 1L");
		//productoData.modificarProducto(p);

		//prueba de modificacion
		Pedido p2= pedidoData.getPedido(3);
		p2.setIdMesa(3);
		p2.setIdMesero(2);
		//pedidoData.modificarPedido(p2);

		// List<Pedido> listaPedidos = pedidoData.getListaPedidos(PedidoData.OrdenacionPedido.PORFECHAHORA);
		//					idPedido, idMesa, idMesero, fechaDesde, fechaHasta, ordenacion
		System.out.println( LocalDateTime.of(2023, 9, 1, 0, 0, 0) );
		List<Pedido> listaPedidos = pedidoData.getListaPedidosXCriterioDeBusqueda(
				-1, -1, -1,
				null, //LocalDateTime.of(2023, 9, 1, 0, 0, 0), 
				LocalDateTime.of(2023, 9, 5, 0, 0, 0), null,
				PedidoData.OrdenacionPedido.PORFECHAHORA);

		for (Pedido pedido: listaPedidos)
			System.out.println(pedido);
	}
        
	public static void pruebaItemData(){
		ItemData itemData = new ItemData();
               
		// prueba de alta item
		Item i = new Item(49, 4, 1, Item.EstadoItem.ANOTADO);
		//itemData.altaItem(i);
		
		List<Item> listaItems = itemData.getListaItems();
		listaItems.stream().forEach( item -> System.out.println(item) );
		
		i = itemData.getItem(4);
		i.setIdPedido(1);
		//itemData.modificarItem(i);
		
		System.out.println("+++++++++++++++++++++++");
		listaItems = itemData.getListaItemsXCriterioDeBusqueda(
				//idItem, idProducto, idPedido, estado, ItemData.Ordenacion
				-1,			-1,			-1,		Item.EstadoItem.ENTREGADO,	ItemData.OrdenacionItem.PORIDPEDIDO);
		listaItems.stream().forEach( item -> System.out.println(item) );
		
		//i = new Item(58, 4, 15, Item.EstadoItem.ANOTADO);
		//itemData.altaItem(i);
		
		//System.out.println("======================");
		//listaItems = itemData.getListaItems();
		//listaItems.stream().forEach( item -> System.out.println(item) );
		
		//itemData.bajaItem(i);
		//System.out.println("======================");
		//listaItems = itemData.getListaItems();
		//listaItems.stream().forEach( item -> System.out.println(item) );
		
	}
	
	
	public static void pruebaDeFecha(){
		LocalDateTime ldt = LocalDateTime.now();
		System.out.println(ldt);
		System.out.println(localDateTime2DateTimeBD(ldt));
		
		String s = "2020-05-15 18:05:03";
		System.out.println(s);
		System.out.println(dateTimeBD2LocalDateTime(s));
		System.out.println("");
		System.out.println("");
		Date date = new Date();
		System.out.println("date " + date);
		System.out.println("localDateTime " + Utils.date2LocalDateTime(date));
	}
	
	
	
	public static void pruebaServicioData(){
		ServicioData servicioData = new ServicioData();
		
		// prueba de alta producto
		Servicio m = new Servicio("Luis Gonzalez", "localhost", 20016, Servicio.TipoServicio.RECEPCION, "miclave");
		//servicioData.altaServicio(m);
		
		//prueba de getServicio
		//Servicio s2 = servicioData.getServicio(10);
		//System.out.println(s2);
		
		//prueba de baja servicio
		//servicioData.bajaServicio(10);
		
		//prueba de modificacion servicio
		Servicio s3 = servicioData.getServicio(1);
		//System.out.println(s3);
		//System.out.println("==================");
		//s3.setNombreServicio("Administración");
		//s3.setTipo(Servicio.TipoServicio.ADMINISTRACION);
		//s3.setClave("12345");
		//servicioData.modificarServicio(s3);
		
		
		
		List<Servicio> listaServicios = servicioData.getListaServicios();
		
		for (Servicio servicio: listaServicios)
			System.out.println(servicio);
		
		System.out.println("");
		System.out.println("POR CRITERIO DE BUSQUEDA");
		System.out.println("========================");
		listaServicios = servicioData.getListaServiciosXCriterioDeBusqueda(
		//idServicio, nombreServicio, host, puerto, tipo,						ordenacion
			-1,			"",				"",		-1, Servicio.TipoServicio.MESERO, ServicioData.OrdenacionServicio.PORIDSERVICIO);
		
		listaServicios.stream().forEach(servicio -> System.out.println("**** " + servicio)); //listo listaServicios con streams y lambda
		// el equivalente clasico es for (Servicio servicio: listaServicios) System.out.println("**** " + servicio);
		
		System.out.println("");
		System.out.println("POR OTRO ORDEN");
		System.out.println("========================");
		listaServicios = servicioData.getListaServicios(ServicioData.OrdenacionServicio.PORTIPOSERVICIO);
		
        listaServicios.stream().forEach(servicio -> System.out.println("**** " + servicio)); //listo listaServicios con streams y lambda
		// el equivalente clasico es: for (Servicio servicio: listaServicios) System.out.println("**** " + servicio);
		
	}
	

	
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		//pruebaMesaData();
		//pruebaPedidoData();
		//pruebaItemData();
		//pruebaServicioData();
		//pruebaProductoData();
	}
	
}
