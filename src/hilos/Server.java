package hilos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {

	private HashMap<Integer,Socket> listaClientes;	//HashMap con clave=nroCliente, value=socketCliente -> lo uso para repetir los mensajes a todos los clientes
	private ServerSocket listener;	//Socket del sv para escuchar a los nuevos clientes
	private int cantClientes = 1;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	public Server(int puerto) throws IOException {
		listaClientes = new HashMap<Integer,Socket>();
		listener = new ServerSocket(puerto);
	}
	
	public class Escuchar implements Runnable {
		
		@Override
		public void run() {
			while(true) {
				try {
					//System.out.println("Escuchando...");
					Socket socketCliente = listener.accept(); 
					listaClientes.put(cantClientes, socketCliente);	//Al cliente aceptado lo agrego al HashMap
					System.out.println("Conexion de " + socketCliente.getInetAddress() + ":" + socketCliente.getPort());
					in = new ObjectInputStream(socketCliente.getInputStream());	//Inicializo flujo para leer data del cliente
					out = new ObjectOutputStream(socketCliente.getOutputStream());	//Inicializo flujo para escribir data al cliente
					
					//Thread para leer al cliente (uno por cada cliente)
					LeerCliente lc_thread = new LeerCliente(socketCliente, in);
					Thread leer = new Thread(lc_thread);
					leer.setName(Integer.toString(cantClientes));
					leer.start();
					
					cantClientes++;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public class LeerCliente implements Runnable {
		
		private Socket socketCliente;
		private ObjectInputStream in;			
		private String str;
		
		LeerCliente(Socket cliente, ObjectInputStream in) throws IOException {
			//in = new ObjectInputStream(cliente.getInputStream());
			this.in = in;
			socketCliente = cliente;
		}
		
		@Override
		public void run() {
			while(true) {
				try {
						//System.out.println("leo");
						str = (String) in.readObject();
						System.out.println("Mensaje del " + Thread.currentThread().getName() + ": " + str);
						
						//Creo threads para repetir el mensaje a todos los clientes, menos al que envio el mensaje original
						for(int i=0; i<listaClientes.size(); i++) {
							if((i+1) != Integer.parseInt(Thread.currentThread().getName())) {
								System.out.println("Mando mensaje a " + (i+1));
								Repetir rep = new Repetir(listaClientes.get(i+1), out, str);
								Thread rep_thread = new Thread(rep);
								rep_thread.start();
							}
						}
				} catch (IOException | ClassNotFoundException e) {
					System.out.println("Error al leer");
				}
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class Repetir implements Runnable {
		
		private Socket socketCliente;
		private ObjectOutputStream out;
		private String str;
		
		Repetir(Socket cliente, ObjectOutputStream out, String msj) throws IOException {
			this.socketCliente = cliente;
			this.out = out;
			str = msj;
		}
		
		@Override
		public void run() {
			try {
				System.out.println("Escribiendo a " + out.toString());
				out.writeObject(str);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
