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
public class Mesa {
	private int idMesa, capacidad;
	private EstadoMesa estado;
	private int idMesero; // idServicio del mesero correspondiente de servicios que atiende esa mesa
	
	public enum EstadoMesa {LIBRE, OCUPADA, ATENDIDA};

	public Mesa() {
	}

	public Mesa(int capacidad, EstadoMesa estado, int idMesero) {
		this.capacidad = capacidad;
		this.estado = estado;
		this.idMesero = idMesero;
	}

	public Mesa(int idMesa, int capacidad, EstadoMesa estado, int idMesero) {
		this.idMesa = idMesa;
		this.capacidad = capacidad;
		this.estado = estado;
		this.idMesero = idMesero;
	}

	public int getIdMesa() {
		return idMesa;
	}

	public void setIdMesa(int idMesa) {
		this.idMesa = idMesa;
	}

	public int getCapacidad() {
		return capacidad;
	}

	public void setCapacidad(int capacidad) {
		this.capacidad = capacidad;
	}

	public EstadoMesa getEstado() {
		return estado;
	}

	public void setEstado(EstadoMesa estado) {
		this.estado = estado;
	}

	public int getIdMesero() {
		return idMesero;
	}

	public void setIdMesero(int idMesero) {
		this.idMesero = idMesero;
	}

	@Override
	public String toString() {
		return "Mesa{" + "idMesa=" + idMesa + ", capacidad=" + capacidad + ", estado=" + estado + ", idMesero=" + idMesero + '}';
	}

	
	

}
