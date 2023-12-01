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
public class Producto {
	private int idProducto;
	private String nombre, descripcion;
	private int stock;
	private double precio;
	private boolean disponible; // si el producto está disponible en la carta
	private int idCategoria; // categoría a la que pertenece: carnes, pescados, bebidas, pastas, entradas, etc.
	private int despachadoPor; // cual es el servicio.idServicio que se encarga de despachar este plato o producto: cocina, bar, etc. 0 es NADIE

	public Producto() {
	}

	public Producto(String nombre, String descripcion, int stock, double precio, boolean disponible, int categoria, int despachadoPor) {
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.stock = stock;
		this.precio = precio;
		this.disponible = disponible;
		this.idCategoria = categoria;
		this.despachadoPor = despachadoPor;
	}

	public Producto(int idProducto, String nombre, String descripcion, int stock, double precio, boolean disponible, int categoria, int despachadoPor) {
		this.idProducto = idProducto;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.stock = stock;
		this.precio = precio;
		this.disponible = disponible;
		this.idCategoria = categoria;
		this.despachadoPor = despachadoPor;
	}

	public int getIdProducto() {
		return idProducto;
	}

	public void setIdProducto(int idProducto) {
		this.idProducto = idProducto;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}
	
	public boolean getDisponible() {
		return disponible;
	}

	public void setDisponible(boolean disponible) {
		this.disponible = disponible;
	}

	public int getIdCategoria() {
		return idCategoria;
	}

	public void setIdCategoria(int idCategoria) {
		this.idCategoria = idCategoria;
	}

	public int getDespachadoPor() {
		return despachadoPor;
	}

	public void setDespachadoPor(int despachadoPor) {
		this.despachadoPor = despachadoPor;
	}

	@Override
	public String toString() {
		//return "Producto{" + "idProducto=" + idProducto + ", nombre=" + nombre + 
		// ", descripcion=" + descripcion + ", stock=" + stock + 
		// ", precio=" + precio + ", disponible=" + disponible + 
		// ", idCategoria=" + idCategoria + ", despachadoPor=" + despachadoPor + '}';
		return idProducto + ":" + nombre;
	}

	
	
}
