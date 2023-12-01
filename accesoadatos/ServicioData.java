/*
	Trabajo práctico final de la Guía 6 del curso Desarrollo de Apps
	Universidad de La Punta en el marco del proyecto Argentina Programa 4.0

	Integrantes:
		John David Molina Velarde
		Leticia Mores
		Enrique Germán Martínez
		Carlos Eduardo Beltrán

	Controlador de Servicio. Permite almacenar y recuperar servicios de la bd.
 */


package accesoadatos;

import static utiles.Utils.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import entidades.Servicio;
import entidades.Pedido;


/**
 *
 * @author John David Molina Velarde, Leticia Mores, Enrique Germán Martínez, Carlos Eduardo Beltrán
 */
public class ServicioData {
	ConexionMySQL conexion; //gestiona la conexión con la bd
	public enum OrdenacionServicio {PORIDSERVICIO, PORNOMBRESERVICIO, PORTIPOSERVICIO}; //tipo de ordenamiento//tipo de ordenamiento
	
	
	/**
	 * constructor. Gestiona la conexión con la bd.
	 */
	public ServicioData() {
		conexion = new ConexionMySQL();
		conexion.conectar(); //esto es opcional. Podría ponerse en el main.
	} //ServicioData

	
		/**
	 * Dada un enumerado Servicio.TipoServicio que puede ser 
	 *		ADMINISTRACION, SERVICIO, RECEPCION, MESERO, 
	 * devuelve la letra correspondiente A, S, R o M para almacenarla en el campo 
	 * tipo de la tabla Servicio de la BD.
	 * @param tipoServicio (ADMINISTRACION, SERVICIO, RECEPCION, MESERO)
	 * @return letra "A", "S", "R", "M"
	 */
	private String tipoServicioEnumerado2TipoServicioLetra(Servicio.TipoServicio tipo){
		if (tipo == Servicio.TipoServicio.ADMINISTRACION)
			return "A";
		else if (tipo == Servicio.TipoServicio.SERVICIO)
			return "S";
		else if (tipo == Servicio.TipoServicio.RECEPCION)
			return "R";
		else // if (tipo == Servicio.TipoServicio.MESERO)
			return "M";
	} // tipoServicioEnumerado2TipoServicioLetra
	
	
	
	/**
	 * Dada una letra que puede ser A, S, R, M en el campo Tipo de la tabla Servicio
	 * de la BD, devuelve el correspondiente enumerado según la entidad Servicio.
	 * @param letra
	 * @return el enumerado ADMINISTRACION, SERVICIO, RECEPCION, MESERO
	 */
	private Servicio.TipoServicio tipoServicioLetra2TipoServicioEnumerado(String letra){
		if (letra.equalsIgnoreCase("A"))
			return Servicio.TipoServicio.ADMINISTRACION;
		else if (letra.equalsIgnoreCase("S"))
			return Servicio.TipoServicio.SERVICIO;
		else if (letra.equalsIgnoreCase("R"))
			return Servicio.TipoServicio.RECEPCION;
		else //if (letra.equalsIgnoreCase("M"))
			return Servicio.TipoServicio.MESERO;
	} //TipoServicioLetra2TipoServicioEnumerado
	
	
	
	
	

	/**
	 * agrega el servicio a la BD. 
	 * @param sericio El que se dará de alta. Viene sin idservicio (se genera ahora)
	 * @return devuelve true si pudo darlo de alta
	 */
	public boolean altaServicio(Servicio servicio){// 
		// una alternativa es usar ?,?,? y luego insertarlo con preparedStatement.setInt(1, dato) // o setString, setBoolean, setData
		String sql = "Insert into servicio (idservicio, nombreServicio, host, puerto, tipo, clave) " +
			"VALUES " + "(null,'" + 
				servicio.getNombreServicio() +  "','" + 
				servicio.getHost() + "','" + 
				servicio.getPuerto() + "','" + 
				tipoServicioEnumerado2TipoServicioLetra(servicio.getTipo()) + "'," + 
				( (servicio.getClave() == null || servicio.getClave().isEmpty())? "null": ("'" + servicio.getClave() + "'") ) + ")";
		System.out.println(sql);
		if (conexion.sqlUpdate(sql)) {
			mensaje("Alta de servicio exitosa");
			servicio.setIdServicio(conexion.getKeyGenerado()); //asigno el id generado
			conexion.cerrarSentencia(); //cierra PreparedStatement y como consecuencia tambien el reultSet
			return true;
		} else {
			mensajeError("Falló el alta de servicio");
			return false;
		}
	} //altaServicio
	
	
	
	
	/**
	 * Da de baja al servicio de la BD.
	 * @param servicio el servicio que se dará debaja (usando su idServicio)
	 * @return devuelve true si pudo darlo de baja
	 */
	public boolean bajaServicio(Servicio servicio){// 
		return bajaServicio(servicio.getIdServicio()); // llama a la baja usando el idservicio
	} //bajaServicio
	
	
	
	
	/**
	 * Da de baja al servicio de la BD en base al id (si no está referenciado en Pedido)
	 * @param id es el idservicio del servicio que se dará de baja
	 * @return  true si pudo darlo de baja
	 */
	public boolean bajaServicio(int id){// devuelve true si pudo darlo de baja
		//Doy de baja al servicio
		String sql = "Delete from servicio where idservicio=" + id;
		if (conexion.sqlUpdate(sql)){
			mensaje("Baja de servicio exitosa");
			conexion.cerrarSentencia();
			return true;
		} 
		else {
			mensajeError("Falló la baja del servicio");
			return false;
		}
	} //bajaServicio
	
	
	
	
	
	/**
	 * Da de baja al servicio de la BD en base al id. Si está con pedidoss, 
	 * también las da de baja.
	 * @param idServicio es el idservicio del servicio que se dará de baja
	 * @return  true si pudo darlo de baja
	 */
	public boolean bajaServicioconPedidosEnCascada(int idServicio){// devuelve true si pudo darlo de baja OJO FALTA PROBAR
		//Borro todas los pedidos de ese servicio
		//PedidoData pedidoData = new PedidoData();
		//List<Pedido> listaPedidos = pedidoData.getListaPedidosDelServicio(idServicio);
		//for (Pedido pedido : listaPedidos)
		//	pedidoData.bajaPedido(pedido);
		
		
		//Doy de baja al servicio
		String sql = "Delete from servicio where idservicio=" + idServicio;
		if (conexion.sqlUpdate(sql)){
			mensaje("Baja de servicio exitosa");
			conexion.cerrarSentencia();
			return true;
		} 
		else {
			mensajeError("Falló la baja del servicio");
			return false;
		}
	} //bajaServicioInscripcionesEnCascada
	
	
	
	
	
	
	/**
	 * Modifica al servicio en la BD poniendole estos nuevos datos
	 * @param servicio el servicio que se modificará (en base a su idservicio)
	 * @return true si pudo modificarlo
	 */
	public boolean modificarServicio(Servicio servicio){
		String sql = 
			"Update servicio set " + 
			"nombreServicio='" + servicio.getNombreServicio() + "'," + 
			"host='" + servicio.getHost() + "'," +
			"puerto='" + servicio.getPuerto() + "'," +
			"tipo='" + tipoServicioEnumerado2TipoServicioLetra(servicio.getTipo()) + "'," +
			"clave=" + ( (servicio.getClave() == null || servicio.getClave().isEmpty())? "null": ("'" + servicio.getClave() +  "'") ) +
			" where idServicio='" + servicio.getIdServicio() + "'";
		if (conexion.sqlUpdate(sql)) {
			mensaje("Modificación de servicio exitosa");
			conexion.cerrarSentencia();
			return true;
		} 
		else {
			mensajeError("Falló la modificación de servicio");;
			return false;
		}
	} //modificarServicio
	
	
	
	
	
	/**
	 * Dado un resultSet lo convierte en un Servicio
	 * @param rs es el ResultSet que se pasa para convertirlo en el objeto Servicio
	 * @return el servicio con los datos del resultSet
	 */
	public Servicio resultSet2Servicio(ResultSet rs){
		Servicio servicio = new Servicio();
		try {
			servicio.setIdServicio(rs.getInt("idServicio"));
			servicio.setNombreServicio(rs.getString("nombreServicio"));
			servicio.setHost(rs.getString("host"));
			servicio.setPuerto(rs.getInt("puerto"));
			servicio.setTipo(tipoServicioLetra2TipoServicioEnumerado( rs.getString("tipo") ) );
			servicio.setClave( (rs.getString("clave")==null)? "" : rs.getString("clave") );
		} catch (SQLException ex) {
			//Logger.getLogger(ServicioData.class.getName()).log(Level.SEVERE, null, ex);
			mensajeError("Error al pasar de ResultSet a Servicio"+ex.getMessage());
		}
		return servicio;
	} // resultSet2Servicio
	
	
	
	
	
	/**
	 * Devuelve una lista con los servicios de la base de datos ordenados por idservicio
	 * @return la lista de servicios
	 */
	public List<Servicio> getListaServicios(){ 
		return getListaServicios(OrdenacionServicio.PORIDSERVICIO);
	} // getListaServicios
	
	
	
	
	
	/**
	 * Devuelve una lista ordenada con los servicios de la base de datos
	 * @param ordenacion es el orden en el que se devolverán
	 * @return devuelve la lista de servicios
	 */
	public List<Servicio> getListaServicios(OrdenacionServicio ordenacion){
		ArrayList<Servicio> lista = new ArrayList();
		String sql = "Select * from servicio";
		
		//defino orden
		if (ordenacion == OrdenacionServicio.PORIDSERVICIO) 
			sql = sql + " Order by idservicio";
		else if (ordenacion == OrdenacionServicio.PORNOMBRESERVICIO)
			sql = sql + " Order by nombreServicio";
		else // (ordenacion == OrdenacionServicio.PORTIPOSERVICIO)
			sql = sql + " Order by tipo";
		
		//ejecuto
		ResultSet rs = conexion.sqlSelect(sql);
		
		//cargo la lista con los resultados
		try {
			while (rs.next()) {
				Servicio servicio = resultSet2Servicio(rs);
				lista.add(servicio);
			}
			conexion.cerrarSentencia(); // cierra el PreparedStatement y tambien cierra automaticamente el ResultSet
		} catch (SQLException ex) {
			mensajeError("Error al obtener lista de servicios" + ex.getMessage());
		}
		return lista;
	} //getListaServicios
	
	
	
	
	
	/**
	 * devuelve una lista con los servicios de la base de datos en base al criterio de búsqueda que se le pasa.
	 * Si idServicio no es -1 usa dni. 
	 * Si nombreServicio no es "" usa nombreServicio
	 * Si host no es "" usa host
	 * Si puerto no es -1 usa puerto
	 * Si tipo no es null usa tipo
	 * Si hay más de un criterio de búsqueda lo combina con ANDs
	 * Si no hay ningún criterio de búsqueda devuelve toda la tabla
	 * 
	 * @param idServicio si idServicio no es -1, usa idServicio como criterio de búsqueda 
	 * @param nombreServicio si nombre no es '', usa nombre como criterio de búsqueda
	 * @param host           si host no es '', usa host como criterio de búsqueda
	 * @param puerto		 si puerto no es -1, usa puerto como criterio de búsqueda
	 * @param tipo			 si tipo no es null, usa tipo como criterio de búsquda
	 * @param ordenacion es el orden en el que devolverá la lista
	 * @return lista de servicios que cumplen con el criterio de búsqueda
	 */
	public List<Servicio> getListaServiciosXCriterioDeBusqueda(int idServicio, 
			String nombreServicio, String host, int puerto, Servicio.TipoServicio tipo, 
			OrdenacionServicio ordenacion){ 
		ArrayList<Servicio> lista = new ArrayList();
		String sql = "Select * from servicio";
		if ( idServicio != -1 || ! nombreServicio.isEmpty() || ! host.isEmpty() ||
				puerto != -1 || tipo != null) {
			sql = sql + " Where";
			
			if ( idServicio != -1 )
				sql = sql + " idservicio=" + idServicio;
			
			if ( ! nombreServicio.isEmpty() ) {
				if (idServicio != -1) //Si ya puse el idServicio agrego and
					sql = sql+" AND";
				sql = sql+ " nombreServicio LIKE '" + nombreServicio + "%'";
			}
			
			if ( ! host.isEmpty() ) {
				if (idServicio != -1 || ! nombreServicio.isEmpty()) //Si ya puse el idServicio o nombreServicio agrego and
					sql = sql+" AND";
				sql = sql+" host LIKE '" + host + "%'";
			}
			
			if ( puerto != -1 ) {
				if (idServicio != -1 || ! nombreServicio.isEmpty() || ! host.isEmpty()) 
					sql = sql+" AND";
				sql = sql+" puerto ='" + puerto + "'";
			}
			
			if ( tipo != null ) {
				if (idServicio != -1 || ! nombreServicio.isEmpty() || ! host.isEmpty() || puerto != -1)
					sql = sql+" AND";
				sql = sql+" tipo ='" + tipoServicioEnumerado2TipoServicioLetra(tipo) + "'";
			}
			
		}
		
		//defino orden
		if (ordenacion == OrdenacionServicio.PORIDSERVICIO) 
			sql = sql + " Order by idservicio";
		else if (ordenacion == OrdenacionServicio.PORNOMBRESERVICIO)
			sql = sql + " Order by nombreServicio";
		else 
			sql = sql + " Order by tipo";		
	
		// ejecuto
		ResultSet rs = conexion.sqlSelect(sql);
		
		// cargo la lista con los resultados
		try {
			while (rs.next()) {
				Servicio servicio = resultSet2Servicio(rs);
				lista.add(servicio);
			}
			conexion.cerrarSentencia(); // cierra el PreparedStatement y tambien cierra automaticamente el ResultSet
		} catch (SQLException ex) {
			mensajeError("Error al obtener lista de servicios" + ex.getMessage());
		}
		return lista;
	} // getListaServiciosXCriterioDeBusqueda
	
	
	
	
	
	/**
	 * Devuelve el servicio con ese idservicio
	 * @param id es el idservicio para identificarlo
	 * @return  el servicio retornado
	 */
	public Servicio getServicio(int id){
		String sql = "Select * from servicio where idservicio=" + id;
		ResultSet rs = conexion.sqlSelect(sql);
		Servicio servicio = null;
		try {
			if (rs.next()) {
				servicio = resultSet2Servicio(rs);
				conexion.cerrarSentencia();
			} else
				mensaje("Error al obtener un servicio");
		} catch (SQLException ex) {
			//Logger.getLogger(ServicioData.class.getName()).log(Level.SEVERE, null, ex);
			mensajeError("Error al obtener un Servicio " + ex.getMessage());
		}
		return servicio;
	} //getServicio
	
	
	
	
	
	/**
	 * Devuelve el servicio con ese apellido y nombre y con ese dni
	 * @param nombre es el parametro para identificarlo
	 * @return  el servicio retornado. Si no lo encuentra devuelve null.
	 */
	public Servicio getServicio(String nombreCompleto){
		String sql = "Select * from servicio where nombreServicio='" + nombreCompleto + "' ";
		ResultSet rs = conexion.sqlSelect(sql);
		Servicio servicio = null;
		try {
			if (rs.next()) {
				servicio = resultSet2Servicio(rs);
				conexion.cerrarSentencia();
			} else
				mensaje("Error al obtener un servicio");
		} catch (SQLException ex) {
			//Logger.getLogger(ServicioData.class.getName()).log(Level.SEVERE, null, ex);
			mensajeError("Error al obtener un Servicio " + ex.getMessage());
		}
		return servicio;
	} //getServicio
	
} //class ServicioData