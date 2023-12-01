/*
	Trabajo práctico final de la Guía 6 del curso Desarrollo de Apps
	Universidad de La Punta en el marco del proyecto Argentina Programa 4.0

	Integrantes:
		John David Molina Velarde
		Leticia Mores
		Enrique Germán Martínez
		Carlos Eduardo Beltrán

	Controlador de Mesa. Permite almacenar y recuperar mesas de la bd.
 */


package accesoadatos;

import static utiles.Utils.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import entidades.Mesa;
import entidades.Pedido;


/**
 *
 * @author John David Molina Velarde, Leticia Mores, Enrique Germán Martínez, Carlos Eduardo Beltrán
 */
public class MesaData {
	ConexionMySQL conexion; //gestiona la conexión con la bd
	public enum OrdenacionMesa {PORIDMESA, PORCAPACIDAD, PORESTADO, PORMESERO}; //tipo de ordenamiento
	
	
	/**
	 * constructor. Gestiona la conexión con la bd.
	 */
	public MesaData() {
		conexion = new ConexionMySQL();
		conexion.conectar(); //esto es opcional. Podría ponerse en el main.
	} //MesaData

	
	
	
	
	/**
	 * Dada un enumerado Mesa.EstadoMesa que puede ser LIBRE, OCUPADO, ATENDIDA, 
	 * devuelve la letra correspondiente L, O, A para almacenarla en el campo 
	 * EstadoMesa de la tabla Mesa de la BD
	 * @param EstadoMesa (LIBRE, OCUPADA, ATENDIDA)
	 * @return letra "L", "O", "A"
	 */
	private String estadoMesaEnumerado2EstadoMesaLetra(Mesa.EstadoMesa estado){
		if (estado == Mesa.EstadoMesa.LIBRE)
			return "L";
		else if (estado == Mesa.EstadoMesa.OCUPADA)
			return "O";
		else //if (estado==Mesa.EstadoMesa.ATENDIDA)
			return "A";
	} // EstadoMesaEnumerado2EstadoMesaLetra
	
	
	
	/**
	 * Dada una letra que puede ser L, O, A en el campo EstadoMesa de la tabla Mesa
	 * de la BD, devuelve el correspondiente enumerado según la entidad Mesa.
	 * @param letra
	 * @return el enumerado LIBRE, OCUPADA, ATENDIDA
	 */
	private Mesa.EstadoMesa estadoMesaLetra2EstadoMesaEnumerado(String letra){
		if (letra.equalsIgnoreCase("L"))
			return Mesa.EstadoMesa.LIBRE;
		else if (letra.equalsIgnoreCase("O"))
			return Mesa.EstadoMesa.OCUPADA;
		else //if (letra.equalsIgnoreCase("A"))
			return Mesa.EstadoMesa.ATENDIDA;
	} //EstadoMesaLetra2EstadoMesaEnumerado
	
	
	
	
	
	/**
	 * agrega el mesa a la BD. 
	 * @param mesa El que se dará de alta. Viene sin idmesa (se genera ahora)
	 * @return devuelve true si pudo darlo de alta
	 */
	public boolean altaMesa(Mesa mesa){// 
		// una alternativa es usar ?,?,? y luego insertarlo con preparedStatement.setInt(1, dato) // o setString, setBoolean, setData
		String sql = "Insert into mesa (idmesa, capacidad, estado, idmesero) " +
			"VALUES " + "(null,'" + mesa.getCapacidad() +  "','" + 
			estadoMesaEnumerado2EstadoMesaLetra(mesa.getEstado()) +  "', " + 
			( (mesa.getIdMesero()==0) ? null : "'" + mesa.getIdMesero() + "'") + " )";
		if (conexion.sqlUpdate(sql)) {
			mensaje("Alta de mesa exitosa");
			mesa.setIdMesa(conexion.getKeyGenerado()); //asigno el id generado
			conexion.cerrarSentencia(); //cierra PreparedStatement y como consecuencia tambien el reultSet
			return true;
		} else {
			mensajeError("Falló el alta de mesa");
			return false;
		}
	} //altaMesa
	
	
	
	
	/**
	 * Da de baja al mesa de la BD.
	 * @param mesa el mesa que se dará debaja (usando su idMesa)
	 * @return devuelve true si pudo darlo de baja
	 */
	public boolean bajaMesa(Mesa mesa){// 
		return bajaMesa(mesa.getIdMesa()); // llama a la baja usando el idmesa
	} //bajaMesa
	
	
	
	
	/**
	 * Da de baja al mesa de la BD en base al id (si no está inscripto en materias)
	 * @param id es el idmesa del mesa que se dará de baja
	 * @return  true si pudo darlo de baja
	 */
	public boolean bajaMesa(int id){// devuelve true si pudo darlo de baja
		//Doy de baja al mesa
		String sql = "Delete from mesa where idmesa=" + id;
		if (conexion.sqlUpdate(sql)){
			mensaje("Baja de mesa exitosa");
			conexion.cerrarSentencia();
			return true;
		} 
		else {
			mensajeError("Falló la baja del mesa");
			return false;
		}
	} //bajaMesa
	
	
	
	
	
	/**
	 * Da de baja al mesa de la BD en base al id. Si está con inscripciones, 
	 * también las da de baja.
	 * @param idMesa es el idmesa del mesa que se dará de baja
	 * @return  true si pudo darlo de baja
	 */
	public boolean bajaMesaconPedidosEnCascada(int idMesa){// devuelve true si pudo darlo de baja OJO FALTA PROBAR
		//Borro todas los pedidos de ese mesa
		//PedidoData pedidoData = new PedidoData();
		//List<Pedido> listaPedidos = pedidoData.getListaPedidosDelMesa(idMesa);
		//for (Pedido pedido : listaPedidos)
		//	pedidoData.bajaPedido(pedido);
		
		
		//Doy de baja al mesa
		String sql = "Delete from mesa where idmesa=" + idMesa;
		if (conexion.sqlUpdate(sql)){
			mensaje("Baja de mesa exitosa");
			conexion.cerrarSentencia();
			return true;
		} 
		else {
			mensajeError("Falló la baja del mesa");
			return false;
		}
	} //bajaMesaInscripcionesEnCascada
	
	
	
	
	
	
	/**
	 * Modifica al mesa en la BD poniendole estos nuevos datos
	 * @param mesa el mesa que se modificará (en base a su idmesa)
	 * @return true si pudo modificarlo
	 */
	public boolean modificarMesa(Mesa mesa){
		String sql = 
				"Update mesa set " + 
				"capacidad='" + mesa.getCapacidad() + "'," + 
				"estado='" + estadoMesaEnumerado2EstadoMesaLetra(mesa.getEstado()) + "'," +
				"idMesero=" + ( (mesa.getIdMesero()==0) ? null : "'" + mesa.getIdMesero() + "'") + " " +
				" where idMesa='" + mesa.getIdMesa() + "'";
		if (conexion.sqlUpdate(sql)) {
			mensaje("Modificación de mesa exitosa");
			conexion.cerrarSentencia();
			return true;
		} 
		else {
			mensajeError("Falló la modificación de mesa");;
			return false;
		}
	} //modificarMesa
	
	
	
	
	
	/**
	 * Dado un resultSet lo convierte en un Mesa
	 * @param rs es el ResultSet que se pasa para convertirlo en el objeto Mesa
	 * @return el mesa con los datos del resultSet
	 */
	public Mesa resultSet2Mesa(ResultSet rs){
		Mesa mesa = new Mesa();
		try {
			mesa.setIdMesa(rs.getInt("idMesa"));
			mesa.setCapacidad(rs.getInt("capacidad"));
			mesa.setEstado(estadoMesaLetra2EstadoMesaEnumerado(rs.getString("estado")) );
			mesa.setIdMesero( rs.getInt("idMesero") );
		} catch (SQLException ex) {
			//Logger.getLogger(MesaData.class.getName()).log(Level.SEVERE, null, ex);
			mensajeError("Error al pasar de ResultSet a Mesa"+ex.getMessage());
		}
		return mesa;
	} // resultSet2Mesa
	
	
	
	
	
	/**
	 * Devuelve una lista con los mesas de la base de datos ordenados por idmesa
	 * @return la lista de mesas
	 */
	public List<Mesa> getListaMesas(){ 
		return getListaMesas(OrdenacionMesa.PORIDMESA);
	} // getListaMesas
	
	
	
	
	
	/**
	 * Devuelve una lista ordenada con los mesas de la base de datos
	 * @param ordenacion es el orden en el que se devolverán
	 * @return devuelve la lista de mesas
	 */
	public List<Mesa> getListaMesas(OrdenacionMesa ordenacion){
		ArrayList<Mesa> lista = new ArrayList();
		String sql = "Select * from mesa";
		
		//defino orden
		if (ordenacion == OrdenacionMesa.PORIDMESA) 
			sql = sql + " Order by idmesa";
		else if (ordenacion == OrdenacionMesa.PORCAPACIDAD)
			sql = sql + " Order by capacidad";
		else if (ordenacion == OrdenacionMesa.PORESTADO)
			sql = sql + " Order by estado";
		else if (ordenacion == OrdenacionMesa.PORMESERO)
			sql = sql + " Order by idMesero";
		else //si no es ninguno anterior
			sql = sql + " Order by idmesa";
		
		//ejecuto
		ResultSet rs = conexion.sqlSelect(sql);
		
		//cargo la lista con los resultados
		try {
			while (rs.next()) {
				Mesa mesa = resultSet2Mesa(rs);
				lista.add(mesa);
			}
			conexion.cerrarSentencia(); // cierra el PreparedStatement y tambien cierra automaticamente el ResultSet
		} catch (SQLException ex) {
			mensajeError("Error al obtener lista de mesas" + ex.getMessage());
		}
		return lista;
	} //getListaMesas
	
	
	
	
	
	/**
	 * devuelve una lista con los mesas de la base de datos en base al criterio de búsqueda que se le pasa.
	 * Si dni no es -1 usa dni. Si apellido no es "" usa apellido. Si nombre no es "" usa nombre
	 * Si hay más de un criterio de búsqueda lo combina con ANDs
	 * Si no hay ningún criterio de búsqueda devuelve toda la tabla
	 * 
	 * @param idMesa si idMesa no es -1 usa idMesa como criterio de búsqueda 
	 * @param capacidad si capacidad no es -1 usa capacidad como criterio de búsqueda
	 * @param estado    si el estado no es null, usa estado como criterio de búsqueda
	 * @param idMesero  si el idMesero no es -1, usa idMesero como criterio de búsqueda
	 * @param ordenacion es el orden en el que devolverá la lista
	 * @return lista de mesas que cumplen con el criterio de búsqueda
	 */
	public List<Mesa> getListaMesasXCriterioDeBusqueda(int idMesa, int capacidad, Mesa.EstadoMesa estado, int idMesero,OrdenacionMesa ordenacion){ 
		ArrayList<Mesa> lista = new ArrayList();
		String sql = "Select * from mesa";
		if ( idMesa != -1 || capacidad != -1 || estado != null || idMesero != -1 ) {
			sql = sql + " Where";
			
			if ( idMesa != -1 )
				sql = sql + " idmesa=" + idMesa;
			
			if ( capacidad != -1 ) {
				if (idMesa != -1) //Si ya puse el idMesa agrego and
					sql = sql+" AND";
				sql = sql+ " capacidad ='" + capacidad + "'";
			}
			
			if ( estado != null ) {
				if (idMesa != -1 || capacidad != -1) //Si ya puse el idMesa o capacidad agrego and
					sql = sql+" AND";
				sql = sql+" estado='" + estadoMesaEnumerado2EstadoMesaLetra(estado) + "'";
			}
			
			if ( idMesero != -1 ) {
				if (idMesa != -1 || capacidad != -1 || estado != null) //Si ya puse el idMesa o capacidad agrego and
					sql = sql+" AND";
				sql = sql+" idMesero" + ((idMesero==0) ? " is null" : ("='" + idMesero + "'"));
			}
		}
		
		//defino orden
		if (ordenacion == OrdenacionMesa.PORIDMESA) 
			sql = sql + " Order by idmesa";
		else if (ordenacion == OrdenacionMesa.PORCAPACIDAD)
			sql = sql + " Order by capacidad";
		else if (ordenacion == OrdenacionMesa.PORESTADO)
			sql = sql + " Order by estado";
		else if (ordenacion == OrdenacionMesa.PORMESERO)
			sql = sql + " Order by idMesero";
		else //si no es ninguno anterior
			sql = sql + " Order by idmesa";
	
		// System.out.println(sql); //debug
		// ejecuto
		ResultSet rs = conexion.sqlSelect(sql);
		
		// cargo la lista con los resultados
		try {
			while (rs.next()) {
				Mesa mesa = resultSet2Mesa(rs);
				lista.add(mesa);
			}
			conexion.cerrarSentencia(); // cierra el PreparedStatement y tambien cierra automaticamente el ResultSet
		} catch (SQLException ex) {
			mensajeError("Error al obtener lista de mesas" + ex.getMessage());
		}
		return lista;
	} // getListaMesasXCriterioDeBusqueda
	
	
	
	
	
	/**
	 * Devuelve el mesa con ese idmesa
	 * @param id es el idmesa para identificarlo
	 * @return  el mesa retornado
	 */
	public Mesa getMesa(int id){
		String sql = "Select * from mesa where idmesa=" + id;
		ResultSet rs = conexion.sqlSelect(sql);
		Mesa mesa = null;
		try {
			if (rs.next()) {
				mesa = resultSet2Mesa(rs);
				conexion.cerrarSentencia();
			} else
				mensaje("Error al obtener una mesa");
		} catch (SQLException ex) {
			//Logger.getLogger(MesaData.class.getName()).log(Level.SEVERE, null, ex);
			mensajeError("Error al obtener un Mesa " + ex.getMessage());
		}
		return mesa;
	} //getMesa


	
} //class MesaData