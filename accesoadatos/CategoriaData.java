/*
	Trabajo práctico final de la Guía 6 del curso Desarrollo de Apps
	Universidad de La Punta en el marco del proyecto Argentina Programa 4.0

	Integrantes:
		John David Molina Velarde
		Leticia Mores
		Enrique Germán Martínez
		Carlos Eduardo Beltrán

	Controlador de Categoria. Permite almacenar y recuperar categorias de la bd.
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
import entidades.Categoria;


/**
 *
 * @author John David Molina Velarde, Leticia Mores, Enrique Germán Martínez, Carlos Eduardo Beltrán
 */
public class CategoriaData {
	ConexionMySQL conexion; //gestiona la conexión con la bd
	public enum OrdenacionCategoria {PORIDCATEGORIA, PORNOMBRE}; //tipo de ordenamiento
	
	
	/**
	 * constructor. Gestiona la conexión con la bd.
	 */
	public CategoriaData() {
		conexion = new ConexionMySQL();
		conexion.conectar(); //esto es opcional. Podría ponerse en el main.
	} //CategoriaData

	

	/**
	 * agrega el categoria a la BD. 
	 * @param categoria El que se dará de alta. Viene sin idcategoria (se genera ahora)
	 * @return devuelve true si pudo darlo de alta
	 */
	public boolean altaCategoria(Categoria categoria){// 
		// una alternativa es usar ?,?,? y luego insertarlo con preparedStatement.setInt(1, dato) // o setString, setBoolean, setData
		String sql = "Insert into categoria (idcategoria, nombre) " +
			"VALUES " + "(null,'" + 
			categoria.getNombre() +  "' )";
		if (conexion.sqlUpdate(sql)) {
			mensaje("Alta de categoria exitosa");
			categoria.setIdCategoria(conexion.getKeyGenerado()); //asigno el id generado
			conexion.cerrarSentencia(); //cierra PreparedStatement y como consecuencia tambien el reultSet
			return true;
		} else {
			mensajeError("Falló el alta de categoria");
			return false;
		}
	} //altaCategoria
	
	
	
	
	/**
	 * Da de baja al categoria de la BD.
	 * @param categoria el categoria que se dará debaja (usando su idCategoria)
	 * @return devuelve true si pudo darlo de baja
	 */
	public boolean bajaCategoria(Categoria categoria){// 
		return bajaCategoria(categoria.getIdCategoria()); // llama a la baja usando el idcategoria
	} //bajaCategoria
	
	
	
	
	/**
	 * Da de baja al categoria de la BD en base al id (si no está inscripto en materias)
	 * @param id es el idcategoria del categoria que se dará de baja
	 * @return  true si pudo darlo de baja
	 */
	public boolean bajaCategoria(int id){// devuelve true si pudo darlo de baja
		//Averiguo si esta inscripto en alguna materia
		//PedidoData pedidoData = new PedidoData();
		//List<Pedido> listaPedidos = pedidoData.getListaPedidosXCategoria(id);
		//if (listaPedidos.size()>0) {
		//	mensajeError("No se puede dar de baja al categoria porque está anotado en pedidos. Borre dichos pedidos antes.");
		//	return false;
		//}
		
		//Doy de baja al categoria
		String sql = "Delete from categoria where idcategoria=" + id;
		if (conexion.sqlUpdate(sql)){
			mensaje("Baja de categoria exitosa");
			conexion.cerrarSentencia();
			return true;
		} 
		else {
			mensajeError("Falló la baja del categoria");
			return false;
		}
	} //bajaCategoria
	
	
	
	
	
	/**
	 * Da de baja al categoria de la BD en base al id. Si está con inscripciones, 
	 * también las da de baja.
	 * @param idCategoria es el idcategoria del categoria que se dará de baja
	 * @return  true si pudo darlo de baja
	 */
	public boolean bajaCategoriaconPedidosEnCascada(int idCategoria){// devuelve true si pudo darlo de baja OJO FALTA PROBAR
		//Borro todas los pedidos de ese categoria
		//PedidoData pedidoData = new PedidoData();
		//List<Pedido> listaPedidos = pedidoData.getListaPedidosDelCategoria(idCategoria);
		//for (Pedido pedido : listaPedidos)
		//	pedidoData.bajaPedido(pedido);
		
		
		//Doy de baja al categoria
		String sql = "Delete from categoria where idcategoria=" + idCategoria;
		if (conexion.sqlUpdate(sql)){
			mensaje("Baja de categoria exitosa");
			conexion.cerrarSentencia();
			return true;
		} 
		else {
			mensajeError("Falló la baja del categoria");
			return false;
		}
	} //bajaCategoriaInscripcionesEnCascada
	
	
	
	
	
	
	/**
	 * Modifica al categoria en la BD poniendole estos nuevos datos
	 * @param categoria el categoria que se modificará (en base a su idcategoria)
	 * @return true si pudo modificarlo
	 */
	public boolean modificarCategoria(Categoria categoria){
		String sql = 
				"Update categoria set " + 
				"nombre='" + categoria.getNombre() + "' " +
				"where idCategoria='" + categoria.getIdCategoria() + "'";
		if (conexion.sqlUpdate(sql)) {
			mensaje("Modificación de categoria exitosa");
			conexion.cerrarSentencia();
			return true;
		} 
		else {
			mensajeError("Falló la modificación de categoria");;
			return false;
		}
	} //modificarCategoria
	
	
	
	
	
	/**
	 * Dado un resultSet lo convierte en un Categoria
	 * @param rs es el ResultSet que se pasa para convertirlo en el objeto Categoria
	 * @return el categoria con los datos del resultSet
	 */
	public Categoria resultSet2Categoria(ResultSet rs){
		Categoria categoria = new Categoria();
		try {
			categoria.setIdCategoria(rs.getInt("idCategoria"));
			categoria.setNombre(rs.getString("nombre"));
		} catch (SQLException ex) {
			//Logger.getLogger(CategoriaData.class.getName()).log(Level.SEVERE, null, ex);
			mensajeError("Error al pasar de ResultSet a Categoria"+ex.getMessage());
		}
		return categoria;
	} // resultSet2Categoria
	
	
	
	
	
	/**
	 * Devuelve una lista con los categorias de la base de datos ordenados por idcategoria
	 * @return la lista de categorias
	 */
	public List<Categoria> getListaCategorias(){ 
		return getListaCategorias(OrdenacionCategoria.PORIDCATEGORIA);
	} // getListaCategorias
	
	
	
	
	
	/**
	 * Devuelve una lista ordenada con los categorias de la base de datos
	 * @param ordenacion es el orden en el que se devolverán
	 * @return devuelve la lista de categorias
	 */
	public List<Categoria> getListaCategorias(OrdenacionCategoria ordenacion){
		ArrayList<Categoria> lista = new ArrayList();
		String sql = "Select * from categoria";
		
		//defino orden
		if (ordenacion == OrdenacionCategoria.PORIDCATEGORIA) 
			sql = sql + " Order by idcategoria";
		else if (ordenacion == OrdenacionCategoria.PORNOMBRE)
			sql = sql + " Order by nombre";
		else 
			sql = sql + " Order by idcategoria";
		
		//ejecuto
		ResultSet rs = conexion.sqlSelect(sql);
		
		//cargo la lista con los resultados
		try {
			while (rs.next()) {
				Categoria categoria = resultSet2Categoria(rs);
				lista.add(categoria);
			}
			conexion.cerrarSentencia(); // cierra el PreparedStatement y tambien cierra automaticamente el ResultSet
		} catch (SQLException ex) {
			mensajeError("Error al obtener lista de categorias" + ex.getMessage());
		}
		return lista;
	} //getListaCategorias
	
	
	
	
	
	/**
	 * devuelve una lista con los categorias de la base de datos en base al criterio de búsqueda que se le pasa.
	 * Si dni no es -1 usa dni. Si apellido no es "" usa apellido. Si nombre no es "" usa nombre
	 * Si hay más de un criterio de búsqueda lo combina con ANDs
	 * Si no hay ningún criterio de búsqueda devuelve toda la tabla
	 * 
	 * @param idCategoria si idCategoria no es -1 usa idCategoria como criterio de búsqueda 
	 * @param nombre     si nombre no es '' usa nombre como criterio de búsqueda
	 * @param stock      si stock no es -1 usa stock como criterio de búsqueda
	 * @param precio     si precio no es -1 usa precio como criterio de búsqueda
	 * @param ordenacion es el orden en el que devolverá la lista
	 * @return lista de categorias que cumplen con el criterio de búsqueda
	 */
	public List<Categoria> getListaCategoriasXCriterioDeBusqueda(
			int idCategoria, String nombre, OrdenacionCategoria ordenacion){ 
		ArrayList<Categoria> lista = new ArrayList();
		String sql = "Select * from categoria";
		if ( idCategoria != -1 || ! nombre.isEmpty() ) {
			sql = sql + " Where";
			
			if ( idCategoria != -1 )
				sql = sql + " idcategoria=" + idCategoria;
			
			if ( ! nombre.isEmpty() ) {
				if (idCategoria != -1) //Si ya puse el idCategoria agrego and
					sql = sql+" AND";
				sql = sql+ " nombre LIKE '" + nombre + "%'";
			}
			
		}
		
		//defino orden
		if (ordenacion == OrdenacionCategoria.PORIDCATEGORIA) 
			sql = sql + " Order by idcategoria";
		else if (ordenacion == OrdenacionCategoria.PORNOMBRE)
			sql = sql + " Order by nombre";
		else 
			sql = sql + " Order by idcategoria";		
	
		// ejecuto
		ResultSet rs = conexion.sqlSelect(sql);
		
		// cargo la lista con los resultados
		try {
			while (rs.next()) {
				Categoria categoria = resultSet2Categoria(rs);
				lista.add(categoria);
			}
			conexion.cerrarSentencia(); // cierra el PreparedStatement y tambien cierra automaticamente el ResultSet
		} catch (SQLException ex) {
			mensajeError("Error al obtener lista de categorias" + ex.getMessage());
		}
		return lista;
	} // getListaCategoriasXCriterioDeBusqueda
	
	
	
	
	
	/**
	 * Devuelve el categoria con ese idcategoria
	 * @param id es el idcategoria para identificarlo
	 * @return  el categoria retornado
	 */
	public Categoria getCategoria(int id){
		String sql = "Select * from categoria where idcategoria=" + id;
		ResultSet rs = conexion.sqlSelect(sql);
		Categoria categoria = null;
		try {
			if (rs.next()) {
				categoria = resultSet2Categoria(rs);
				conexion.cerrarSentencia();
			} else
				mensaje("Error al obtener un categoria");
		} catch (SQLException ex) {
			//Logger.getLogger(CategoriaData.class.getName()).log(Level.SEVERE, null, ex);
			mensajeError("Error al obtener un Categoria " + ex.getMessage());
		}
		return categoria;
	} //getCategoria
	
	
	
	
	
	/**
	 * Devuelve el categoria con ese nombre 
	 * @param nombre es el parametro para identificarlo
	 * @return  el categoria retornado. Si no lo encuentra devuelve null.
	 */
	public Categoria getCategoria(String nombre){
		String sql = "Select * from categoria where nombre='" + nombre + "' ";
		ResultSet rs = conexion.sqlSelect(sql);
		Categoria categoria = null;
		try {
			if (rs.next()) {
				categoria = resultSet2Categoria(rs);
				conexion.cerrarSentencia();
			} else
				mensaje("Error al obtener un categoria");
		} catch (SQLException ex) {
			//Logger.getLogger(CategoriaData.class.getName()).log(Level.SEVERE, null, ex);
			mensajeError("Error al obtener un Categoria " + ex.getMessage());
		}
		return categoria;
	} //getCategoria
	
} //class CategoriaData