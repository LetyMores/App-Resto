/*
 	Trabajo práctico final de la Guía 6 del curso Desarrollo de Apps
	Universidad de La Punta en el marco del proyecto Argentina Programa 4.0

	Integrantes:
		John David Molina Velarde
		Leticia Mores
		Enrique Germán Martínez
		Carlos Eduardo Beltrán

	Esta clase se encarga de escuchar en un puerto para recibir los mensajes que
	le envía otra máquina a través de ClienteSocket. 
	
	El proceso se hace Runnable para que corra en un hilo paralelo, dado que
	siempre estará escuchando, así puede ejecutarse concurrentemente con el 
	proceso principal.

	Hereda la clase Observable para implementar el patrón de diseño Observer, es
	decir, para que otros objetos puedan registrarse con este objeto de forma
	tal que cuando este reciba un mensaje, notifique a todos los objetos que
	están como Observer de este servidor, así dichos objetos pueden tomar las
	medidas correspondientes (en nuestro caso, actualización de datos en pantalla.

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
	La clase del programa debe implementar la interfaz Observer para poder 
	ponerse como observador de servidorSocket:
			public class ClienteA extends javax.swing.JFrame implements Observer{...

	En el constructor del programa que usa este ServidorSocket poner:
		ServidorSocket servidor = new ServidorSocket(puerto); // el puerto donde 
															  // escuchará
        servidor.addObserver(this);		// lo registramos como observador del 
										//servidor para que nos notifique mensajes
        Thread hilo = new Thread(servidor); // creamos un hilo paralelo para el servidor
        hilo.start();					// ejecutamos ese hilo del servidor

	El programa que usa este ServidorSocket debe tener implementado un método
	update para que el ServidorSocket le avise cuando llega un mensaje:
		@Override
		public void update(Observable o, Object mensaje) {
			sout("me llego el mensaje " + (String)mensaje);
			// y acá toma las acciones correspondientes para actualizar pantalla

	Si en algún momento se desea detener el servidor, ejecutar: pararEjecucion();
		}
 */
package sockets;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;

/**
 *
 * @author John David Molina Velarde, Leticia Mores, Enrique Germán Martínez, Carlos Eduardo Beltrán
 */
public class ServidorSocket extends Observable implements Runnable {

    private int puerto;
	private boolean seguirEjecutando = true;

    public ServidorSocket(int puerto) {
        this.puerto = puerto;
    }

    @Override
    public void run() {
        ServerSocket servidor = null;
        Socket socket = null;
        DataInputStream entrada;

        try {
            //Creamos el servidor del socket
            servidor = new ServerSocket(puerto);
            // System.out.println("Servidor iniciado"); //solo para debug

            //Siempre estara escuchando peticiones
            while (seguirEjecutando) {
                //Espero que el cliente se contecte
                socket = servidor.accept();
                // System.out.println("Cliente conectado"); //solo para debug
                entrada = new DataInputStream(socket.getInputStream()); //genero un stream para entrada

                //Leemos el mensaje
                String mensaje = entrada.readUTF(); // puede ser entrada.readInt();
                System.out.println(mensaje);

                this.setChanged();				//ponemos que el estado de este objeto Observable cambió.
                this.notifyObservers(mensaje);	//notificamos del cambio a todos los observadores
                this.clearChanged();			//como ya notificarmos, ponemos que el estado de este no cambió.

                socket.close();
                // System.out.println("Cliente desconectado"); //solo para debug
            }
        } catch (IOException error) {
            System.out.println(error);
        }
    } //run
	
	public void pararEjecucion(){
		seguirEjecutando = false;
	}
}
