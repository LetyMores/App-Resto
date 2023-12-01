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
public class Item {
	private int idItem, idProducto, idPedido, cantidad;
	EstadoItem estado;
	public enum EstadoItem {ANOTADO, SOLICITADO, DESPACHADO, ENTREGADO, CANCELADO, CANCELADOVISTO};

	public Item() {
	}

	public Item(int idProducto, int idPedido, int cantidad, EstadoItem estado) {
		this.idProducto = idProducto;
		this.idPedido = idPedido;
		this.cantidad = cantidad;
		this.estado = estado;
	}

	public Item(int idItem, int idProducto, int idPedido, int cantidad, EstadoItem estado) {
		this.idItem = idItem;
		this.idProducto = idProducto;
		this.idPedido = idPedido;
		this.cantidad = cantidad;
		this.estado = estado;
	}

	public EstadoItem getEstado() {
		return estado;
	}

	public void setEstado(EstadoItem estado) {
		this.estado = estado;
	}

	
	
	public int getIdItem() {
		return idItem;
	}

	public void setIdItem(int idItem) {
		this.idItem = idItem;
	}

	public int getIdProducto() {
		return idProducto;
	}

	public void setIdProducto(int idProducto) {
		this.idProducto = idProducto;
	}

	public int getIdPedido() {
		return idPedido;
	}

	public void setIdPedido(int idPedido) {
		this.idPedido = idPedido;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	@Override
	public String toString() {
		return "Item{" + "idItem=" + idItem + ", idProducto=" + idProducto + 
				", idPedido=" + idPedido + ", cantidad=" + cantidad + 
				", estado=" + estado +  '}';
	}
	
	
}
