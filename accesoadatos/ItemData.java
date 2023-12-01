/*
	Trabajo práctico final de la Guía 6 del curso Desarrollo de Apps
	Universidad de La Punta en el marco del proyecto Argentina Programa 4.0

	Integrantes:
		John David Molina Velarde
		Leticia Mores
		Enrique Germán Martínez
		Carlos Eduardo Beltrán

	Controlador de Item. Permite almacenar y recuperar items de la bd.
 */


package accesoadatos;

import static utiles.Utils.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import entidades.Item;


/**
 *
 * @author John David Molina Velarde, Leticia Mores, Enrique Germán Martínez, Carlos Eduardo Beltrán
 */
public class ItemData {
	ConexionMySQL conexion; //gestiona la conexión con la bd
	public enum OrdenacionItem {PORIDITEM, PORIDPRODUCTO, PORIDPEDIDO}; //tipo de ordenamiento
	
	
	/**
	 * constructor. Gestiona la conexión con la bd.
	 */
	public ItemData() {
		conexion = new ConexionMySQL();
		conexion.conectar(); //esto es opcional. Podría ponerse en el main.
	} //ItemData

	
	
	/**
	 * Dada un enumerado Item.EstadoItem que puede ser ANOTADO, SOLICITADO, DESPACHADO, ENTREGADO 
	 * devuelve la letra correspondiente A, S, D, E para almacenarla en el campo 
	 * Estado de la tabla Mesa de la BD
	 * @param EstadoMesa (LIBRE, OCUPADA, ATENDIDA)
	 * @return letra "L", "O", "A"
	 */
	private String estadoItemEnumerado2EstadoItemLetra(Item.EstadoItem estado){
		if (estado == Item.EstadoItem.ANOTADO)
			return "A";
		else if (estado == Item.EstadoItem.SOLICITADO)
			return "S";
		else if (estado == Item.EstadoItem.DESPACHADO)
			return "D";
		else if (estado == Item.EstadoItem.ENTREGADO)
			return "E";
		else if (estado == Item.EstadoItem.CANCELADO)
			return "C";
		else //if (estado == Item.EstadoItem.CANCELADOVISTO)
			return "V";
		
	} // EstadoItemEnumerado2EstadoItemLetra
	
	
	
	/**
	 * Dada una letra que puede ser L, O, A en el campo EstadoMesa de la tabla Mesa
	 * de la BD, devuelve el correspondiente enumerado según la entidad Mesa.
	 * @param letra
	 * @return el enumerado LIBRE, OCUPADA, ATENDIDA
	 */
	private Item.EstadoItem estadoItemLetra2EstadoItemEnumerado(String letra){
		if (letra.equalsIgnoreCase("A"))
			return Item.EstadoItem.ANOTADO;
		else if (letra.equalsIgnoreCase("S"))
			return Item.EstadoItem.SOLICITADO;
		else if (letra.equalsIgnoreCase("D"))
			return Item.EstadoItem.DESPACHADO;
		else if (letra.equalsIgnoreCase("E"))
			return Item.EstadoItem.ENTREGADO;
		else if (letra.equalsIgnoreCase("C"))
			return Item.EstadoItem.CANCELADO;
		else //if (letra.equalsIgnoreCase("V"))
			return Item.EstadoItem.CANCELADOVISTO;
	} //EstadoItemLetra2EstadoItemEnumerado
	
	
	
	
	

	/**
	 * agrega el item a la BD. 
	 * @param item El que se dará de alta. Viene sin iditem (se genera ahora)
	 * @return devuelve true si pudo darlo de alta
	 */
	public boolean altaItem(Item item){// 
		// una alternativa es usar ?,?,? y luego insertarlo con preparedStatement.setInt(1, dato) // o setString, setBoolean, setData
		String sql = "Insert into item (iditem,idproducto, idpedido, cantidad, estado) " +
			"VALUES " + "(null,'" + item.getIdProducto() +  "','" + 
			item.getIdPedido() + "','" + 
			item.getCantidad() + "','" + 
			estadoItemEnumerado2EstadoItemLetra(item.getEstado()) +  "' )";
		if (conexion.sqlUpdate(sql)) {
			mensaje("Alta de item exitosa");
			item.setIdItem(conexion.getKeyGenerado()); //asigno el id generado
			conexion.cerrarSentencia(); //cierra PreparedStatement y como consecuencia tambien el reultSet
			return true;
		} else {
			mensajeError("Falló el alta de item");
			return false;
		}
	} //altaItem
	
	
	
	
	/**
	 * Da de baja al item de la BD.
	 * @param item el item que se dará debaja (usando su idItem)
	 * @return devuelve true si pudo darlo de baja
	 */
	public boolean bajaItem(Item item){// 
		return bajaItem(item.getIdItem()); // llama a la baja usando el iditem
	} //bajaItem
	
	
	
	
	/**
	 * Da de baja al item de la BD en base al id (si no está inscripto en materias)
	 * @param id es el iditem del item que se dará de baja
	 * @return  true si pudo darlo de baja
	 */
	public boolean bajaItem(int id){// devuelve true si pudo darlo de baja
		//Averiguo si esta inscripto en alguna materia
		//PedidoData pedidoData = new PedidoData();
		//List<Pedido> listaPedidos = pedidoData.getListaPedidosXItem(id);
		//if (listaPedidos.size()>0) {
		//	mensajeError("No se puede dar de baja al item porque está anotado en pedidos. Borre dichos pedidos antes.");
		//	return false;
		//}
		
		//Doy de baja al item
		String sql = "Delete from item where iditem=" + id;
		if (conexion.sqlUpdate(sql)){
			mensaje("Baja de item exitosa");
			conexion.cerrarSentencia();
			return true;
		} 
		else {
			mensajeError("Falló la baja del item");
			return false;
		}
	} //bajaItem
	
	
	
	
	
	/**
	 * Da de baja al item de la BD en base al id. Si está con inscripciones, 
	 * también las da de baja.
	 * @param idItem es el iditem del item que se dará de baja
	 * @return  true si pudo darlo de baja
	 */
	public boolean bajaItemconPedidosEnCascada(int idItem){// devuelve true si pudo darlo de baja OJO FALTA PROBAR
		//Borro todas los pedidos de ese item
		//PedidoData pedidoData = new PedidoData();
		//List<Pedido> listaPedidos = pedidoData.getListaPedidosDelItem(idItem);
		//for (Pedido pedido : listaPedidos)
		//	pedidoData.bajaPedido(pedido);
		
		
		//Doy de baja al item
		String sql = "Delete from item where iditem=" + idItem;
		if (conexion.sqlUpdate(sql)){
			mensaje("Baja de item exitosa");
			conexion.cerrarSentencia();
			return true;
		} 
		else {
			mensajeError("Falló la baja del item");
			return false;
		}
	} //bajaItemInscripcionesEnCascada
	
	
	
	
	
	
	/**
	 * Modifica al item en la BD poniendole estos nuevos datos
	 * @param item el item que se modificará (en base a su iditem)
	 * @return true si pudo modificarlo
	 */
	public boolean modificarItem(Item item){
		String sql = 
				"Update item set " + 
				"idProducto='" + item.getIdProducto() + "'," + 
				"idPedido='" + item.getIdPedido() + "'," +
				"cantidad='" + item.getCantidad() +  "', " +
				"estado='" + estadoItemEnumerado2EstadoItemLetra( item.getEstado() ) + "' " + 
				"where idItem='" + item.getIdItem() + "'";
		if (conexion.sqlUpdate(sql)) {
			mensaje("Modificación de item exitosa");
			conexion.cerrarSentencia();
			return true;
		} 
		else {
			mensajeError("Falló la modificación de item");;
			return false;
		}
	} //modificarItem
	
	
	
	
	
	/**
	 * Dado un resultSet lo convierte en un Item
	 * @param rs es el ResultSet que se pasa para convertirlo en el objeto Item
	 * @return el item con los datos del resultSet
	 */
	public Item resultSet2Item(ResultSet rs){
		Item item = new Item();
		try {
			item.setIdItem(rs.getInt("idItem"));
			item.setIdProducto(rs.getInt("idProducto"));
			item.setIdPedido(rs.getInt("idPedido"));
			item.setCantidad(rs.getInt("cantidad"));
			item.setEstado(estadoItemLetra2EstadoItemEnumerado(rs.getString("estado")));
		} catch (SQLException ex) {
			//Logger.getLogger(ItemData.class.getName()).log(Level.SEVERE, null, ex);
			mensajeError("Error al pasar de ResultSet a Item"+ex.getMessage());
		}
		return item;
	} // resultSet2Item
	
	
	
	
	
	/**
	 * Devuelve una lista con los items de la base de datos ordenados por iditem
	 * @return la lista de items
	 */
	public List<Item> getListaItems(){ 
		return getListaItems(OrdenacionItem.PORIDITEM);
	} // getListaItems
	
	
	
	
	
	/**
	 * Devuelve una lista ordenada con los items de la base de datos
	 * @param ordenacion es el orden en el que se devolverán
	 * @return devuelve la lista de items
	 */
	public List<Item> getListaItems(OrdenacionItem ordenacion){
		ArrayList<Item> lista = new ArrayList();
		String sql = "Select * from item";
		
		//defino orden
		if (ordenacion == OrdenacionItem.PORIDITEM) 
			sql = sql + " Order by iditem";
		else if (ordenacion == OrdenacionItem.PORIDPRODUCTO)
			sql = sql + " Order by idProducto";
		else // (ordenacion == OrdenacionItem.PORIDPEDIDO)
			sql = sql + " Order by idPedido";
		
		//ejecuto
		ResultSet rs = conexion.sqlSelect(sql);
		
		//cargo la lista con los resultados
		try {
			while (rs.next()) {
				Item item = resultSet2Item(rs);
				lista.add(item);
			}
			conexion.cerrarSentencia(); // cierra el PreparedStatement y tambien cierra automaticamente el ResultSet
		} catch (SQLException ex) {
			mensajeError("Error al obtener lista de items" + ex.getMessage());
		}
		return lista;
	} //getListaItems
	
	
	
	
	
	/**
	 * devuelve una lista con los items de la base de datos en base al criterio de búsqueda que se le pasa.
	 * Si dni no es -1 usa dni. Si apellido no es "" usa apellido. Si nombre no es "" usa nombre
	 * Si hay más de un criterio de búsqueda lo combina con ANDs
	 * Si no hay ningún criterio de búsqueda devuelve toda la tabla
	 * 
	 * @param idItem     si idItem no es -1 usa idItem como criterio de búsqueda 
	 * @param idProducto si idProducto no es -1 usa idProducot como criterio de búsqueda
	 * @param idPedido   si idPedido no es -1 usa idPedido como criterio de búsqueda
	 * @param ordenacion es el orden en el que devolverá la lista
	 * @return lista de items que cumplen con el criterio de búsqueda
	 */
	public List<Item> getListaItemsXCriterioDeBusqueda(int idItem, int idProducto,
			int idPedido, Item.EstadoItem estado, OrdenacionItem ordenacion){ 
		ArrayList<Item> lista = new ArrayList();
		String sql = "Select * from item";
		if ( idItem != -1 || idProducto != -1 || idPedido != -1 || estado != null) {
			sql = sql + " Where";
			
			if ( idItem != -1 )
				sql = sql + " iditem=" + idItem;
			
			if ( idProducto != -1 ) {
				if (idItem != -1) //Si ya puse el idItem agrego and
					sql = sql+" AND";
				sql = sql+ " idProducto='" + idProducto + "'";
			}
			
			if ( idPedido != -1 ) {
				if (idItem != -1 || idProducto != -1) //Si ya puse el idItem o idProducto agrego and
					sql = sql+" AND";
				sql = sql+" idPedido=" + idPedido;
			}
			
			if ( estado != null ) {
				if (idItem != -1 || idProducto != -1 || idPedido != -1) //Si ya puse el idItem o idProducto agrego and
					sql = sql+" AND";
				sql = sql+" estado='" + estadoItemEnumerado2EstadoItemLetra(estado) + "'";
			}
		}
		
		//defino orden
		if (ordenacion == OrdenacionItem.PORIDITEM) 
			sql = sql + " Order by iditem";
		else if (ordenacion == OrdenacionItem.PORIDPRODUCTO)
			sql = sql + " Order by idProducto";
		else // (ordenacion == OrdenacionItem.PORIDPEDIDO)
			sql = sql + " Order by idPedido";		
	
		// ejecuto
		ResultSet rs = conexion.sqlSelect(sql);
		
		// cargo la lista con los resultados
		try {
			while (rs.next()) {
				Item item = resultSet2Item(rs);
				lista.add(item);
			}
			conexion.cerrarSentencia(); // cierra el PreparedStatement y tambien cierra automaticamente el ResultSet
		} catch (SQLException ex) {
			mensajeError("Error al obtener lista de items" + ex.getMessage());
		}
		return lista;
	} // getListaItemsXCriterioDeBusqueda
	
	
	
	
	
	/**
	 * devuelve una lista con los items SOLICITADO o CANCELADO de la base de 
	 * datos que pertenezcan al idServicio indicado (que son los items que verán
	 * en los servicios que despachan productos tales como  cocina, bar, cafetería, etc.
	 * @param idServicio es el idServicio de cuyos items traeremos la lista
	 * @return lista de items que cumplen con el criterio de búsqueda
	 */
	public List<Item> getListaItemsSCXIdServicio(int idServicio, OrdenacionItem ordenacion){ 
		ArrayList<Item> lista = new ArrayList();
		// traigo todos los items (de estado S o C) cuyos idProductos sean los que son despachadosPor este idServicio
		String sql = 
			"Select * from item where estado in ('S', 'C') and idProducto in " + 
			"(select producto.idproducto from producto where despachadoPor = '" + idServicio + "') "; //lista de idProductos que son despachadosPor idServicio
		
		//defino orden
		if (ordenacion == OrdenacionItem.PORIDITEM) 
			sql = sql + " Order by iditem";
		else if (ordenacion == OrdenacionItem.PORIDPRODUCTO)
			sql = sql + " Order by idProducto";
		else // (ordenacion == OrdenacionItem.PORIDPEDIDO)
			sql = sql + " Order by idPedido";		
	
		// ejecuto
		ResultSet rs = conexion.sqlSelect(sql);
		
		// cargo la lista con los resultados
		try {
			while (rs.next()) {
				Item item = resultSet2Item(rs);
				lista.add(item);
			}
			conexion.cerrarSentencia(); // cierra el PreparedStatement y tambien cierra automaticamente el ResultSet
		} catch (SQLException ex) {
			mensajeError("Error al obtener lista de items" + ex.getMessage());
		}
		return lista;
	} // getListaItemsSCXIdServicio
	
	
	

	
	
	
	
	/**
	 * Devuelve el item con ese iditem
	 * @param id es el iditem para identificarlo
	 * @return  el item retornado
	 */
	public Item getItem(int id){
		String sql = "Select * from item where iditem=" + id;
		ResultSet rs = conexion.sqlSelect(sql);
		Item item = null;
		try {
			if (rs.next()) {
				item = resultSet2Item(rs);
				conexion.cerrarSentencia();
			} else
				mensaje("Error al obtener un item");
		} catch (SQLException ex) {
			//Logger.getLogger(ItemData.class.getName()).log(Level.SEVERE, null, ex);
			mensajeError("Error al obtener un Item " + ex.getMessage());
		}
		return item;
	} //getItem
	
	
	
	
	
} //class ItemData