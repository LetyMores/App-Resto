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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Calendar;
/**
 *
 * @author John David Molina Velarde, Leticia Mores, Enrique Germán Martínez, Carlos Eduardo Beltrán
 */
public class Pedido {
	private int idPedido, idMesa, idMesero;
        private  LocalDateTime fechaHora;
	private EstadoPedido estado;
	public enum EstadoPedido {ACTIVO, PAGADO, CANCELADO};

	public Pedido() {
	}

	public Pedido(int idMesa, int idMesero, LocalDateTime fechaHora, EstadoPedido estado) {
		this.idMesa = idMesa;
		this.idMesero = idMesero;
        this.fechaHora = fechaHora;
		this.estado = estado;
	}

	public Pedido(int idPedido, int idMesa, int idMesero, java.time.LocalDateTime fechaHora, EstadoPedido estado) {
		this.idPedido = idPedido;
		this.idMesa = idMesa;
		this.idMesero = idMesero;
		this.fechaHora = fechaHora;
		this.estado = estado;
}

    	public int getIdPedido() {
		return idPedido;
	}

	public void setIdPedido(int idPedido) {
		this.idPedido = idPedido;
	}

	public int getIdMesa() {
		return idMesa;
	}

	public void setIdMesa(int idMesa) {
		this.idMesa = idMesa;
	}

	public int getIdMesero() {
		return idMesero;
	}

	public void setIdMesero(int idMesero) {
		this.idMesero = idMesero;
	}

	public LocalDateTime getFechaHora() {
        return fechaHora;
    }

	public void setFechaHora(LocalDateTime fechaHora) {
		this.fechaHora = fechaHora;
	}

	public EstadoPedido getEstado() {
		return estado;
	}

	public void setEstado(EstadoPedido estado) {
		this.estado = estado;
	}

	@Override
	public String toString() {
            
		return "Pedido{" + "idPedido=" + idPedido + ", idMesa=" + idMesa + ", idMesero=" + idMesero + ", fechaHora=" + fechaHora + ", estado=" + estado + '}';
	}
	
	
}
