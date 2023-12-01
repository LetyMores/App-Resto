/*
	Trabajo práctico final de la Guía 6 del curso Desarrollo de Apps
	Universidad de La Punta en el marco del proyecto Argentina Programa 4.0

	Integrantes:
		John David Molina Velarde
		Leticia Mores
		Enrique Germán Martínez
		Carlos Eduardo Beltrán

	Controlador de Producto. Permite almacenar y recuperar productos de la bd.
 */


package accesoadatos;

import static utiles.Utils.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import entidades.Producto;
import entidades.Pedido;


/**
 *
 * @author John David Molina Velarde, Leticia Mores, Enrique Germán Martínez, Carlos Eduardo Beltrán
 */
public class ProductoData {
	ConexionMySQL conexion; //gestiona la conexión con la bd
	public enum OrdenacionProducto {PORIDPRODUCTO, PORNOMBRE, PORIDCATEGORIAYNOMBRE}; //tipo de ordenamiento
	
	
	/**
	 * constructor. Gestiona la conexión con la bd.
	 */
	public ProductoData() {
		conexion = new ConexionMySQL();
		conexion.conectar(); //esto es opcional. Podría ponerse en el main.
	} //ProductoData

	

	/**
	 * agrega el producto a la BD. 
	 * @param producto El que se dará de alta. Viene sin idproducto (se genera ahora)
	 * @return devuelve true si pudo darlo de alta
	 */
	public boolean altaProducto(Producto producto){// 
		// una alternativa es usar ?,?,? y luego insertarlo con preparedStatement.setInt(1, dato) // o setString, setBoolean, setData
		String sql = "Insert into producto (idproducto, nombre, descripcion, stock, precio, disponible, idcategoria, despachadopor) " +
			"VALUES " + "(null,'" + 
			producto.getNombre() +  "',' " + 
			producto.getDescripcion() + "',' " + 
			producto.getStock() + "',' " + 
			producto.getPrecio() +  "', " + 
			producto.getDisponible()+ ", '" + 
			producto.getIdCategoria() + "', '" + 
			producto.getDespachadoPor() + "' )";
		if (conexion.sqlUpdate(sql)) {
			mensaje("Alta de producto exitosa");
			producto.setIdProducto(conexion.getKeyGenerado()); //asigno el id generado
			conexion.cerrarSentencia(); //cierra PreparedStatement y como consecuencia tambien el reultSet
			return true;
		} else {
			mensajeError("Falló el alta de producto");
			return false;
		}
	} //altaProducto
	
	
	
	
	/**
	 * Da de baja al producto de la BD.
	 * @param producto el producto que se dará debaja (usando su idProducto)
	 * @return devuelve true si pudo darlo de baja
	 */
	public boolean bajaProducto(Producto producto){// 
		return bajaProducto(producto.getIdProducto()); // llama a la baja usando el idproducto
	} //bajaProducto
	
	
	
	
	/**
	 * Da de baja al producto de la BD en base al id (si no está inscripto en materias)
	 * @param id es el idproducto del producto que se dará de baja
	 * @return  true si pudo darlo de baja
	 */
	public boolean bajaProducto(int id){// devuelve true si pudo darlo de baja
		//Averiguo si esta inscripto en alguna materia
		//PedidoData pedidoData = new PedidoData();
		//List<Pedido> listaPedidos = pedidoData.getListaPedidosXProducto(id);
		//if (listaPedidos.size()>0) {
		//	mensajeError("No se puede dar de baja al producto porque está anotado en pedidos. Borre dichos pedidos antes.");
		//	return false;
		//}
		
		//Doy de baja al producto
		String sql = "Delete from producto where idproducto=" + id;
		if (conexion.sqlUpdate(sql)){
			mensaje("Baja de producto exitosa");
			conexion.cerrarSentencia();
			return true;
		} 
		else {
			mensajeError("Falló la baja del producto");
			return false;
		}
	} //bajaProducto
	
	
	
	
	
	/**
	 * Da de baja al producto de la BD en base al id. Si está con inscripciones, 
	 * también las da de baja.
	 * @param idProducto es el idproducto del producto que se dará de baja
	 * @return  true si pudo darlo de baja
	 */
	public boolean bajaProductoconPedidosEnCascada(int idProducto){// devuelve true si pudo darlo de baja OJO FALTA PROBAR
		//Borro todas los pedidos de ese producto
		//PedidoData pedidoData = new PedidoData();
		//List<Pedido> listaPedidos = pedidoData.getListaPedidosDelProducto(idProducto);
		//for (Pedido pedido : listaPedidos)
		//	pedidoData.bajaPedido(pedido);
		
		
		//Doy de baja al producto
		String sql = "Delete from producto where idproducto=" + idProducto;
		if (conexion.sqlUpdate(sql)){
			mensaje("Baja de producto exitosa");
			conexion.cerrarSentencia();
			return true;
		} 
		else {
			mensajeError("Falló la baja del producto");
			return false;
		}
	} //bajaProductoInscripcionesEnCascada
	
	
	
	
	
	
	/**
	 * Modifica al producto en la BD poniendole estos nuevos datos
	 * @param producto el producto que se modificará (en base a su idproducto)
	 * @return true si pudo modificarlo
	 */
	public boolean modificarProducto(Producto producto){
		String sql = 
				"Update producto set " + 
				"nombre='" + producto.getNombre() + "'," + 
				"descripcion='" + producto.getDescripcion() + "'," +
				"stock='" + producto.getStock() + "'," +
				"precio='" + producto.getPrecio() + "'," + 
				"disponible=" + producto.getDisponible() + ", " +
				"idcategoria=" + ( (producto.getIdCategoria()== 0 ) ? null : "'" + producto.getIdCategoria() + "'" ) + ", " +
				"despachadopor=" + ( (producto.getDespachadoPor() == 0) ? null : "'" + producto.getDespachadoPor() + "'" ) + " " +
				"where idProducto='" + producto.getIdProducto() + "'";
		if (conexion.sqlUpdate(sql)) {
			mensaje("Modificación de producto exitosa");
			conexion.cerrarSentencia();
			return true;
		} 
		else {
			mensajeError("Falló la modificación de producto");;
			return false;
		}
	} //modificarProducto
	
	
	
	
	
	/**
	 * Dado un resultSet lo convierte en un Producto
	 * @param rs es el ResultSet que se pasa para convertirlo en el objeto Producto
	 * @return el producto con los datos del resultSet
	 */
	public Producto resultSet2Producto(ResultSet rs){
		Producto producto = new Producto();
		try {
			producto.setIdProducto(rs.getInt("idProducto"));
			producto.setNombre(rs.getString("nombre"));
			producto.setDescripcion(rs.getString("descripcion"));
			producto.setStock(rs.getInt("stock"));
			producto.setPrecio(rs.getDouble("precio"));
			producto.setDisponible(rs.getBoolean("disponible"));
			producto.setIdCategoria(rs.getInt("idCategoria"));
			producto.setDespachadoPor(rs.getInt("despachadoPor"));
		} catch (SQLException ex) {
			//Logger.getLogger(ProductoData.class.getName()).log(Level.SEVERE, null, ex);
			mensajeError("Error al pasar de ResultSet a Producto"+ex.getMessage());
		}
		return producto;
	} // resultSet2Producto
	
	
	
	
	
	/**
	 * Devuelve una lista con los productos de la base de datos ordenados por idproducto
	 * @return la lista de productos
	 */
	public List<Producto> getListaProductos(){ 
		return getListaProductos(OrdenacionProducto.PORIDPRODUCTO);
	} // getListaProductos
	
	
	
	
	
	/**
	 * Devuelve una lista ordenada con los productos de la base de datos
	 * @param ordenacion es el orden en el que se devolverán
	 * @return devuelve la lista de productos
	 */
	public List<Producto> getListaProductos(OrdenacionProducto ordenacion){
		ArrayList<Producto> lista = new ArrayList();
		String sql = "Select * from producto";
		
		//defino orden
		if (ordenacion == OrdenacionProducto.PORIDPRODUCTO) 
			sql = sql + " Order by idproducto";
		else if (ordenacion == OrdenacionProducto.PORNOMBRE)
			sql = sql + " Order by nombre";
		else if (ordenacion == OrdenacionProducto.PORIDCATEGORIAYNOMBRE)
			sql = sql + " Order by idcategoria, nombre";
		
		//ejecuto
		ResultSet rs = conexion.sqlSelect(sql);
		
		//cargo la lista con los resultados
		try {
			while (rs.next()) {
				Producto producto = resultSet2Producto(rs);
				lista.add(producto);
			}
			conexion.cerrarSentencia(); // cierra el PreparedStatement y tambien cierra automaticamente el ResultSet
		} catch (SQLException ex) {
			mensajeError("Error al obtener lista de productos" + ex.getMessage());
		}
		return lista;
	} //getListaProductos
	
	
	
	
	
	/**
	 * devuelve una lista con los productos de la base de datos en base al criterio de búsqueda que se le pasa.
	 * Si dni no es -1 usa dni. Si apellido no es "" usa apellido. Si nombre no es "" usa nombre
	 * Si hay más de un criterio de búsqueda lo combina con ANDs
	 * Si no hay ningún criterio de búsqueda devuelve toda la tabla
	 * 
	 * @param idProducto si idProducto no es -1 usa idProducto como criterio de búsqueda 
	 * @param nombre     si nombre no es '' usa nombre como criterio de búsqueda
	 * @param stock      si stock no es -1 usa stock como criterio de búsqueda
	 * @param precio     si precio no es -1 usa precio como criterio de búsqueda
	 * @param ordenacion es el orden en el que devolverá la lista
	 * @return lista de productos que cumplen con el criterio de búsqueda
	 */
	public List<Producto> getListaProductosXCriterioDeBusqueda(
			int idProducto, String nombre, int stock, double precio, Boolean disponible,
			int idCategoria, int despachadoPor, OrdenacionProducto ordenacion){ 
		ArrayList<Producto> lista = new ArrayList();
		String sql = "Select * from producto";
		if (nombre == null)
			nombre = "";
		if ( idProducto != -1 || ! nombre.isEmpty() || stock != -1 ||  
			precio != -1.0 || disponible != null || idCategoria != -1 || despachadoPor != -1 ) {
			sql = sql + " Where";
			
			if ( idProducto != -1 )
				sql = sql + " idproducto=" + idProducto;
			
			if ( ! nombre.isEmpty() ) {
				if (idProducto != -1) //Si ya puse el idProducto agrego and
					sql = sql+" AND";
				sql = sql+ " nombre LIKE '" + nombre + "%'";
			}
			
			if ( stock != -1 ) {
				if (idProducto != -1 || ! nombre.isEmpty()) //Si ya puse el idProducto o nombre agrego and
					sql = sql+" AND";
				sql = sql+" stock="+stock;
			}
			
			if ( precio != -1.0 ) {
				if (idProducto != -1 || ! nombre.isEmpty() || stock != -1) //Si ya puse el idProducto o nombre agrego and
					sql = sql+" AND";
				sql = sql+" precio="+precio;
			}
			
			if ( idCategoria != -1 ) {
				if (idProducto != -1 || ! nombre.isEmpty() || stock != -1 || precio != -1.0) //Si ya puse el idProducto o nombre agrego and
					sql = sql+" AND";
				sql = sql+" idcategoria=" + idCategoria;
			}
			
			if ( despachadoPor != -1 ) {
				if (idProducto != -1 || ! nombre.isEmpty() || stock != -1 || precio != -1.0 || idCategoria != -1) //Si ya puse el idProducto o nombre agrego and
					sql = sql+" AND";
				sql = sql+" despachadoPor=" + despachadoPor;
			}
			
			if ( disponible != null ) {
				if (idProducto != -1 || ! nombre.isEmpty() || stock != -1 || precio != -1.0 || idCategoria != -1 || despachadoPor != -1) //Si ya puse el idProducto o nombre agrego and
					sql = sql+" AND";
				sql = sql+" disponible=" + disponible;
			}
			
		}
		
		//defino orden
		if (ordenacion == OrdenacionProducto.PORIDPRODUCTO) 
			sql = sql + " Order by idproducto";
		else if (ordenacion == OrdenacionProducto.PORNOMBRE)
			sql = sql + " Order by nombre";
		else if (ordenacion == OrdenacionProducto.PORIDCATEGORIAYNOMBRE)
			sql = sql + " Order by idcategoria, nombre";		
	
		// ejecuto
		ResultSet rs = conexion.sqlSelect(sql);
		
		// cargo la lista con los resultados
		try {
			while (rs.next()) {
				Producto producto = resultSet2Producto(rs);
				lista.add(producto);
			}
			conexion.cerrarSentencia(); // cierra el PreparedStatement y tambien cierra automaticamente el ResultSet
		} catch (SQLException ex) {
			mensajeError("Error al obtener lista de productos" + ex.getMessage());
		}
		return lista;
	} // getListaProductosXCriterioDeBusqueda
	
	
	
	
	
	/**
	 * Devuelve el producto con ese idproducto
	 * @param id es el idproducto para identificarlo
	 * @return  el producto retornado
	 */
	public Producto getProducto(int id){
		String sql = "Select * from producto where idproducto=" + id;
		ResultSet rs = conexion.sqlSelect(sql);
		Producto producto = null;
		try {
			if (rs.next()) {
				producto = resultSet2Producto(rs);
				conexion.cerrarSentencia();
			} else
				mensaje("Error al obtener un producto");
		} catch (SQLException ex) {
			//Logger.getLogger(ProductoData.class.getName()).log(Level.SEVERE, null, ex);
			mensajeError("Error al obtener un Producto " + ex.getMessage());
		}
		return producto;
	} //getProducto
	
	
	
	
	
	/**
	 * Devuelve el producto con ese apellido y nombre y con ese dni
	 * @param nombre es el parametro para identificarlo
	 * @return  el producto retornado. Si no lo encuentra devuelve null.
	 */
	public Producto getProducto(String nombre){
		String sql = "Select * from producto where nombre='" + nombre + "' ";
		ResultSet rs = conexion.sqlSelect(sql);
		Producto producto = null;
		try {
			if (rs.next()) {
				producto = resultSet2Producto(rs);
				conexion.cerrarSentencia();
			} else
				mensaje("Error al obtener un producto");
		} catch (SQLException ex) {
			//Logger.getLogger(ProductoData.class.getName()).log(Level.SEVERE, null, ex);
			mensajeError("Error al obtener un Producto " + ex.getMessage());
		}
		return producto;
	} //getProducto
	
} //class ProductoData