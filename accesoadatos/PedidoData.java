/*
	Trabajo práctico final de la Guía 6 del curso Desarrollo de Apps
	Universidad de La Punta en el marco del proyecto Argentina Programa 4.0

	Integrantes:
		John David Molina Velarde
		Leticia Mores
		Enrique Germán Martínez
		Carlos Eduardo Beltrán

	Controlador de Pedido. Permite almacenar y recuperar pedidos de la bd.
 */


package accesoadatos;

import static utiles.Utils.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import entidades.Pedido;
import entidades.Pedido.EstadoPedido;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


/**
 *
 * @author John David Molina Velarde, Leticia Mores, Enrique Germán Martínez, Carlos Eduardo Beltrán
 */
public class PedidoData {
	ConexionMySQL conexion; //gestiona la conexión con la bd
	public enum OrdenacionPedido {PORIDPEDIDO, PORIDMESA, PORIDMESERO, PORFECHAHORA}; //tipo de ordenamiento
	
	
	/**
	 * constructor. Gestiona la conexión con la bd.
	 */
	public PedidoData() {
		conexion = new ConexionMySQL();
		conexion.conectar(); //esto es opcional. Podría ponerse en el main.
	} //PedidoData

	
	
	/**
	 * Dada un enumerado Pedido.EstadoPedido que puede ser ACTIVO, PAGADO, CANCELADO
	 * devuelve la letra correspondiente A, P, C  para almacenarla en el campo 
	 * Estado de la tabla Pedido de la BD
	 * @param EstadoMesa (LIBRE, OCUPADA, ATENDIDA)
	 * @return letra "A", "P", "C"
	 */
	private String estadoPedidoEnumerado2EstadoPedidoLetra(Pedido.EstadoPedido estado){
		if (estado == Pedido.EstadoPedido.ACTIVO)
			return "A";
		else if (estado == Pedido.EstadoPedido.PAGADO)
			return "P";
		else if (estado == Pedido.EstadoPedido.CANCELADO)
			return "C";
		else // if (estado == Item.EstadoItem.CANCELADO)
			return "C";
	} // EstadoPedidoEnumerado2EstadoPedidoLetra
	
	
	
	/**
	 * Dada una letra que puede ser A, P, C en el campo Estado de la tabla Pedido
	 * de la BD, devuelve el correspondiente enumerado según la entidad Pedido.
	 * @param letra
	 * @return el enumerado ACTIVO, PAGADO, CANCELADO
	 */
	private Pedido.EstadoPedido estadoPedidoLetra2EstadoPedidoEnumerado(String letra){
		if (letra.equalsIgnoreCase("A"))
			return Pedido.EstadoPedido.ACTIVO;
		else if (letra.equalsIgnoreCase("P"))
			return Pedido.EstadoPedido.PAGADO;
		else if (letra.equalsIgnoreCase("C"))
			return Pedido.EstadoPedido.CANCELADO;
		else //if (letra.equalsIgnoreCase("C"))
			return Pedido.EstadoPedido.CANCELADO;
	} //EstadoPedidoLetra2EstadoPedidoEnumerado
	
	
	
	
	
	
	/**
	 * agrega el pedido a la BD. 
	 * @param pedido El que se dará de alta. Viene sin idpedido (se genera ahora)
	 * @return devuelve true si pudo darlo de alta
	 */
	public boolean altaPedido(Pedido pedido){// 
		// una alternativa es usar ?,?,? y luego insertarlo con preparedStatement.setInt(1, dato) // o setString, setBoolean, setData
		String sql = "Insert into pedido (idpedido, idMesa, idMesero, fechaHora, estado) " +
			"VALUES " + "(null,'" + 
			pedido.getIdMesa() + "','" + 
			pedido.getIdMesero() + "','" + 
			localDateTime2DateTimeBD( pedido.getFechaHora() ) + "','" + 
			estadoPedidoEnumerado2EstadoPedidoLetra(pedido.getEstado()) + "'" +  
			" )";
		
		if (conexion.sqlUpdate(sql)) {
			mensaje("Alta de pedido exitosa");
			pedido.setIdPedido(conexion.getKeyGenerado()); //asigno el id generado
			conexion.cerrarSentencia(); //cierra PreparedStatement y como consecuencia tambien el reultSet
			return true;
		} else {
			mensajeError("Falló el alta de pedido");
			return false;
		}
	} //altaPedido
	
	
	
	 
	/**
	 * Da de baja al pedido de la BD.
	 * @param pedido el pedido que se dará debaja (usando su idPedido)
	 * @return devuelve true si pudo darlo de baja
	 */
	public boolean bajaPedido(Pedido pedido){// 
		return bajaPedido(pedido.getIdPedido()); // llama a la baja usando el idpedido
	} //bajaPedido
	
	
	
	
	/**
	 * Da de baja al pedido de la BD en base al id (si no está inscripto en materias)
	 * @param id es el idpedido del pedido que se dará de baja
	 * @return  true si pudo darlo de baja
	 */
	public boolean bajaPedido(int id){// devuelve true si pudo darlo de baja
		//Averiguo si esta inscripto en alguna materia
		//PedidoData pedidoData = new PedidoData();
		//List<Pedido> listaPedidos = pedidoData.getListaPedidosXPedido(id);
		//if (listaPedidos.size()>0) {
		//	mensajeError("No se puede dar de baja al pedido porque está anotado en pedidos. Borre dichos pedidos antes.");
		//	return false;
		//}
		
		//Doy de baja al pedido
		String sql = "Delete from pedido where idpedido=" + id;
		if (conexion.sqlUpdate(sql)){
			mensaje("Baja de pedido exitosa");
			conexion.cerrarSentencia();
			return true;
		} 
		else {
			mensajeError("Falló la baja del pedido");
			return false;
		}
	} //bajaPedido
	
	
	
	
	
	/**
	 * Da de baja al pedido de la BD en base al id. Si está con inscripciones, 
	 * también las da de baja.
	 * @param idPedido es el idpedido del pedido que se dará de baja
	 * @return  true si pudo darlo de baja
	 */
	public boolean bajaPedidoconPedidosEnCascada(int idPedido){// devuelve true si pudo darlo de baja OJO FALTA PROBAR
		//Borro todas los pedidos de ese pedido
		//PedidoData pedidoData = new PedidoData();
		//List<Pedido> listaPedidos = pedidoData.getListaPedidosDelPedido(idPedido);
		//for (Pedido pedido : listaPedidos)
		//	pedidoData.bajaPedido(pedido);
		
		
		//Doy de baja al pedido
		String sql = "Delete from pedido where idpedido=" + idPedido;
		if (conexion.sqlUpdate(sql)){
			mensaje("Baja de pedido exitosa");
			conexion.cerrarSentencia();
			return true;
		} 
		else {
			mensajeError("Falló la baja del pedido");
			return false;
		}
	} //bajaPedidoInscripcionesEnCascada
	
	
	
	
	
	
	/**
	 * Modifica al pedido en la BD poniendole estos nuevos datos
	 * @param pedido el pedido que se modificará (en base a su idpedido)
	 * @return true si pudo modificarlo
	 */
	public boolean modificarPedido(Pedido pedido){
		String sql = 
				"Update pedido set " + 
				"idMesa='"    + pedido.getIdMesa() + "'," + 
				"idMesero='"  + pedido.getIdMesero() + "'," +
				"fechaHora='" + localDateTime2DateTimeBD( pedido.getFechaHora() ) + "'," +
				"estado='"    + estadoPedidoEnumerado2EstadoPedidoLetra(pedido.getEstado()) + "' " +
				"where idPedido='" + pedido.getIdPedido() + "'";
		if (conexion.sqlUpdate(sql)) {
			mensaje("Modificación de pedido exitosa");
			conexion.cerrarSentencia();
			return true;
		} 
		else {
			mensajeError("Falló la modificación de pedido");;
			return false;
		}
	} //modificarPedido
	
	
	
	
	
	/**
	 * Dado un resultSet lo convierte en un Pedido
	 * @param rs es el ResultSet que se pasa para convertirlo en el objeto Pedido
	 * @return el pedido con los datos del resultSet
	 */
	public Pedido resultSet2Pedido(ResultSet rs){
		Pedido pedido = new Pedido();
		try {
			pedido.setIdPedido(rs.getInt("idPedido"));
			pedido.setIdMesa(rs.getInt("idMesa"));
			pedido.setIdMesero(rs.getInt("idMesero"));
			pedido.setFechaHora( dateYTime2LocalDateTime(rs.getDate("fechaHora"), rs.getTime("fechaHora")) );
			pedido.setEstado(estadoPedidoLetra2EstadoPedidoEnumerado(rs.getString("estado")));
		} catch (SQLException ex) {
			//Logger.getLogger(PedidoData.class.getName()).log(Level.SEVERE, null, ex);
			mensajeError("Error al pasar de ResultSet a Pedido"+ex.getMessage());
		}
		return pedido;
	} // resultSet2Pedido
	
	
	
	
	
	/**
	 * Devuelve una lista con los pedidos de la base de datos ordenados por idpedido
	 * @return la lista de pedidos
	 */
	public List<Pedido> getListaPedidos(){ 
		return getListaPedidos(OrdenacionPedido.PORIDPEDIDO);
	} // getListaPedidos
	
	
	
	
	
	/**
	 * Devuelve una lista ordenada con los pedidos de la base de datos
	 * @param ordenacion es el orden en el que se devolverán
	 * @return devuelve la lista de pedidos
	 */
	public List<Pedido> getListaPedidos(OrdenacionPedido ordenacion){
		ArrayList<Pedido> lista = new ArrayList();
		String sql = "Select * from pedido";
		
		//defino orden
		if (ordenacion == OrdenacionPedido.PORIDPEDIDO) 
			sql = sql + " Order by idpedido";
		else if (ordenacion == OrdenacionPedido.PORIDMESA)
			sql = sql + " Order by idmesa";
		else if (ordenacion == OrdenacionPedido.PORIDMESERO)
			sql = sql + " Order by idmesero";
		else // (ordenacion == OrdenacionPedido.PORFECHAHORA)
			sql = sql + " Order by fechaHora";
		
		//ejecuto
		ResultSet rs = conexion.sqlSelect(sql);
		
		//cargo la lista con los resultados
		try {
			while (rs.next()) {
				Pedido pedido = resultSet2Pedido(rs);
				lista.add(pedido);
			}
			conexion.cerrarSentencia(); // cierra el PreparedStatement y tambien cierra automaticamente el ResultSet
		} catch (SQLException ex) {
			mensajeError("Error al obtener lista de pedidos" + ex.getMessage());
		}
		return lista;
	} //getListaPedidos
	
	
	
	
	
	/**
	 * devuelve una lista con los pedidos de la base de datos en base al criterio de búsqueda que se le pasa.
	 * Si dni no es -1 usa dni. Si apellido no es "" usa apellido. Si nombre no es "" usa nombre
	 * Si hay más de un criterio de búsqueda lo combina con ANDs
	 * Si no hay ningún criterio de búsqueda devuelve toda la tabla
	 * 
	 * @param idPedido si idPedido no es -1 usa idPedido como criterio de búsqueda 
	 * @param idMesa   si idMesa no es -1 usa idMesa como criterio de búsqueda
	 * @param idMesero si idMesero no es -1 usa idMesero como criterio de búsqueda
	 * @param fechaDesde si fechaDesde no es null usa fechaDesde como criterio de búsqueda
	 * @param fechaHasta si fechaHasta no es null usa fechaHasta como criterio de búsqueda
	 * @param estado si estado no es null, usa estado como criterio de búsqueda
	 * @param ordenacion es el orden en el que devolverá la lista
	 * @return lista de pedidos que cumplen con el criterio de búsqueda
	 */ 
	public List<Pedido> getListaPedidosXCriterioDeBusqueda(int idPedido, int idMesa, 
			int idMesero, LocalDateTime fechaDesde, LocalDateTime fechaHasta, 
			EstadoPedido estado,  OrdenacionPedido ordenacion){ 
		ArrayList<Pedido> lista = new ArrayList();
		String sql = "Select * from pedido";
		if ( idPedido != -1 || idMesa != -1 || idMesero != -1 || 
			fechaDesde != null || fechaHasta != null || estado !=null ) {
			sql = sql + " Where";
			
			if ( idPedido != -1 )
				sql = sql + " idpedido=" + idPedido;
			
			if ( idMesa != -1 ) {
				if (idPedido != -1) //Si ya puse el idPedido agrego and
					sql = sql+" AND";
				sql = sql+ " idMesa='" + idMesa + "'";
			}
			
			if ( idMesero != -1 ) {
				if (idPedido != -1 || idMesa != -1) //Si ya puse el idPedido o idMesa agrego and
					sql = sql+" AND";
				sql = sql+" idMesero=" + idMesero;
			}
			
			if ( fechaDesde != null ) {
				if (idPedido != -1 || idMesa != -1 || idMesero != -1) //Si ya puse el idPedido o idMesa o idMesero agrego and
					sql = sql+" AND";
				sql = sql+" fechaHora>='" + localDateTime2DateTimeBD(fechaDesde) + "'";
			}
			
			if ( fechaHasta != null ) {
				if (idPedido != -1 || idMesa != -1 || idMesero != -1 || fechaDesde != null) //Si ya puse el idPedido o idMesa o idMesero agrego and
					sql = sql+" AND";
				sql = sql+" fechaHora<='" + localDateTime2DateTimeBD(fechaHasta) + "'";
			}
			
			if ( estado != null ) {
				if (idPedido != -1 || idMesa != -1 || idMesero != -1 || fechaDesde != null || fechaHasta != null) //Si ya puse el idPedido o idMesa o idMesero agrego and
					sql = sql+" AND";
				sql = sql+" estado='" + estadoPedidoEnumerado2EstadoPedidoLetra(estado) + "' ";
			}
		}
		
		//defino orden
		if (ordenacion == OrdenacionPedido.PORIDPEDIDO) 
			sql = sql + " Order by idpedido";
		else if (ordenacion == OrdenacionPedido.PORIDMESA)
			sql = sql + " Order by idMesa";
		else if (ordenacion == OrdenacionPedido.PORIDMESERO)
			sql = sql + " Order by idmesero";
		else // (ordenacion == OrdenacionPedido.PORFECHAHORA)
			sql = sql + " Order by fechaHora";
		
		// ejecuto
		ResultSet rs = conexion.sqlSelect(sql);
		
		// cargo la lista con los resultados
		try {
			while (rs.next()) {
				Pedido pedido = resultSet2Pedido(rs);
				lista.add(pedido);
			}
			conexion.cerrarSentencia(); // cierra el PreparedStatement y tambien cierra automaticamente el ResultSet
		} catch (SQLException ex) {
			mensajeError("Error al obtener lista de pedidos" + ex.getMessage());
		}
		return lista;
	} // getListaPedidosXCriterioDeBusqueda
	
	
	
	
	
	/**
	 * Devuelve el pedido con ese idpedido
	 * @param id es el idpedido para identificarlo
	 * @return  el pedido retornado
	 */
	public Pedido getPedido(int id){
		String sql = "Select * from pedido where idpedido=" + id;
		ResultSet rs = conexion.sqlSelect(sql);
		Pedido pedido = null;
		try {
			if (rs.next()) {
				pedido = resultSet2Pedido(rs);
				conexion.cerrarSentencia();
			} else
				mensaje("Error al obtener un pedido");
		} catch (SQLException ex) {
			//Logger.getLogger(PedidoData.class.getName()).log(Level.SEVERE, null, ex);
			mensajeError("Error al obtener un Pedido " + ex.getMessage());
		}
		return pedido;
	} //getPedido
	
	
	
	
} //class PedidoData