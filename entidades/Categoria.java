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
public class Categoria {
	private int idCategoria;
	private String nombre;

	public Categoria() {
	}

	public Categoria(String nombre) {
		this.nombre = nombre;
	}

	public Categoria(int idCategoria, String nombre) {
		this.idCategoria = idCategoria;
		this.nombre = nombre;
	}

	public int getIdCategoria() {
		return idCategoria;
	}

	public void setIdCategoria(int idCategoria) {
		this.idCategoria = idCategoria;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}



	@Override
	public String toString() {
		return idCategoria + " " + nombre;
	}

	
	
}
