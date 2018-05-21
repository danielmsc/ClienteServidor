package hilos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Cliente {
	
	private Socket socketCliente;
	private int puerto;
	private String mensaje;
	
	public Cliente(int puerto, String mensaje) throws UnknownHostException, IOException {
		this.puerto = puerto;
		this.mensaje = mensaje;
	}
	
	public class Conectar implements Runnable {

		@Override
		public void run() {
			//System.out.println("Cliente creado");
			try {
				//Me conecto al cliente, usando el localhost:puertoSV
				socketCliente = new Socket("127.0.0.1", puerto);
				
				//En cada cliente creo e inicio un thread para escribir al sv
				Escribir esc = new Escribir(socketCliente);
				Thread esc_thread = new Thread(esc);
				esc_thread.start();
				
				//En cada cliente creo e inicio un thread para leer lo que viene del sv
				Leer leer = new Leer(socketCliente);
				Thread leer_thread = new Thread(leer);
				leer_thread.start();
				
			} catch (IOException e) {
				System.out.println("Error al conectar al servidor");
			}

		}
	}
	
	public class Leer implements Runnable {

		private ObjectInputStream in;
		
		Leer(Socket socketCliente) throws IOException {
			//Inicializo el flujo de entrada del socket
			in = new ObjectInputStream(socketCliente.getInputStream());
		}
		
		@Override
		public void run() {
			while(true) {
				try {
					//System.out.println("Chequear mensaje");
					//Leo el mensaje que manda el cliente
					String msj = (String) in.readObject();
					System.out.println("Mensaje del servidor: " + msj);
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			} 
		}
	}

	public class Escribir implements Runnable {

		private ObjectOutputStream out;
		
		Escribir(Socket socketCliente) throws IOException {
			//Inicializo el flujo de salida del socket
			out = new ObjectOutputStream(socketCliente.getOutputStream());
		}
		
		@Override
		public void run() {
			
			String str = null;
			
			while(true) {
				System.out.println("Escribir al sv: ");
				Scanner teclado = new Scanner(System.in);
				str = teclado.nextLine();
				//System.out.println(out.toString() + str);
				
				try {
					//System.out.println("Escribiendo al sv");
					out.writeObject(str);
					out.flush();
				} catch (IOException e) {
					System.out.println("Error al escribir en el servidor");
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
