/*
	Trabajo práctico final de la Guía 6 del curso Desarrollo de Apps
	Universidad de La Punta en el marco del proyecto Argentina Programa 4.0

	Integrantes:
		John David Molina Velarde
		Leticia Mores
		Enrique Germán Martínez
		Carlos Eduardo Beltrán
 */
package entidades;

/**
 *
 * @author John David Molina Velarde, Leticia Mores, Enrique Germán Martínez, Carlos Eduardo Beltrán
 */
public class Servicio {
	private int idServicio;
	private String nombreServicio, host;
	private int puerto;
	private TipoServicio tipo;
	private String clave;
	
	public enum TipoServicio {ADMINISTRACION, SERVICIO, MESERO, RECEPCION};

	
	
	public Servicio() {
	}

	
	public Servicio(String nombreServicio, String host, int puerto, TipoServicio tipo, String clave) {
		this.nombreServicio = nombreServicio;
		this.host = host;
		this.puerto = puerto;
		this.tipo = tipo;
		this.clave = clave;
	}

	
	public Servicio(int idServicio, String nombreServicio, String host, int puerto, TipoServicio tipo, String clave) {
		this.idServicio = idServicio;
		this.nombreServicio = nombreServicio;
		this.host = host;
		this.puerto = puerto;
		this.tipo = tipo;
		this.clave = clave;
	}

	
	public int getIdServicio() {
		return idServicio;
	}

	public void setIdServicio(int idServicio) {
		this.idServicio = idServicio;
	}

	public String getNombreServicio() {
		return nombreServicio;
	}

	public void setNombreServicio(String nombreServicio) {
		this.nombreServicio = nombreServicio;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPuerto() {
		return puerto;
	}

	public void setPuerto(int puerto) {
		this.puerto = puerto;
	}

	public TipoServicio getTipo() {
		return tipo;
	}

	public void setTipo(TipoServicio tipo) {
		this.tipo = tipo;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	@Override
	public String toString() {
		//return "Servicio{" + "idServicio=" + idServicio + ", nombreServicio=" + nombreServicio + ", host=" + host + ", puerto=" + puerto + ", tipo=" + tipo + ", clave=" + clave + '}';
		return "" + idServicio + " " + nombreServicio;
	}
	
}
