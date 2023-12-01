/*
 	Trabajo práctico final de la Guía 6 del curso Desarrollo de Apps
	Universidad de La Punta en el marco del proyecto Argentina Programa 4.0

	Integrantes:
		John David Molina Velarde
		Leticia Mores
		Enrique Germán Martínez
		Carlos Eduardo Beltrán

	Esta clase se encarga de comunicarse con otra máquina que escucha en un puerto
	y le transmite un mensaje. En dicha máquina habrá un proceso servidorSocket
	que estará corriendo y escuchando en un puerto.
	El proceso se hace Runnable para que corra en un hilo paralelo, de forma que 
	lo que tarde en conectar no retrase al proceso principal.

	Esto será usado por la aplicación del mozo para avisar a la aplicación de la
	cocina que le hizo una solicitud, para que la cocina pueda leer la solicitud
	y actualizar la pantalla. De forma recíproca, cuando la cocina despacha ese
	plato solicitado, la aplicación de cocina avisará a la aplicación del mozo
	para que actualice los datos en pantalla (y haga un sonido) para que el 
	mozo pueda venir a retirar el plato despachado.

	Algo similar sucede con todos los servicios: el mozo solicita un producto
	(licuado por ejemplo) al bar mandando un mensaje a dicha aplicación, y el 
	cuando el bar lo tiene listo lo despacha y envía un mensaje al mozo para que
	lo retire.

	También la aplicación de recepción manda mensajes al mozo correspondiente
	avisando que actualice pantalla porque una mesa se ocupó.

	NOTAS: 
	En el código del programa que usa este Cliente socket cuando quiera enviar
	un mensaje, deberá poner:
		ClienteSocket cliente = new ClienteSocket(host, puerto, mensaje); //creo un cliente que pueda mandar a ese host en ese puerto
        Thread hilo = new Thread(cliente);	//creo un hilo para el clienteSocket
        hilo.start();						//ejecuto ese hilo para el cliente
 */
package sockets;

import utiles.Utils;
import java.io.*;
import java.net.Socket;

/**
 *
 * @author John David Molina Velarde, Leticia Mores, Enrique Germán Martínez, Carlos Eduardo Beltrán
 */
public class ClienteSocket implements Runnable { // lo hacemos Runnable para que pueda correr en un hilo paralelo
	private String host; // host del servidor al que le enviaremos mensaje
    private int puerto;  // puerto en el que escucha ese servidor
    private String mensaje; // mensaje a enviar
	
	/**
	 * Constructor del clienteSocket.
	 * @param host		máquina a la que se conectará
	 * @param puerto	puerto en el que escucha esa máquina
	 * @param mensaje	mensaje que se enviará
	 */
    public ClienteSocket(String host, int puerto, String mensaje) {
		this.host = host;
        this.puerto = puerto;
        this.mensaje = mensaje;
    } // constructor
	
	
    
	/**
	 * Este método es el que se ejecuta cuando se da start() al Thread que corre
	 * el código de este cliente.
	 */
    @Override
    public void run() {
        DataOutputStream salida; // aca almacenamos el stream de salida
        
        try{
            Socket socket = new Socket(host, puerto); // conectamos al puerto de dicho host
            salida = new DataOutputStream(socket.getOutputStream()); //generamos un stream de salida
            
            //enviamos el mensaje
            salida.writeUTF(mensaje); // puede ser salida.writeInt(mensaje); si vamos a escribir un int
        }catch(IOException ex){
            //Utils.mensajeError("Error conexión de Red: " + ex + " No se pudo realizar la conexión . ");
			Utils.mensaje("No se pudo realizar la conexión...");
        }
    } //run
    
} //class ClienteSocket
