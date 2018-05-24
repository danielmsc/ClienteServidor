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
	
	public Cliente(int puerto) throws UnknownHostException, IOException {
		this.puerto = puerto;
	}
	
	public class Conectar implements Runnable {

		@Override
		public void run() {
			try {
			//System.out.println("Cliente creado");
			//Me conecto al cliente, usando el localhost:puertoSV
			socketCliente = new Socket("127.0.0.1", puerto);
			
			//En cada cliente creo e inicio un thread para escribir al sv
			Scanner sc = new Scanner(System.in);
			Escribir esc = new Escribir(socketCliente, Thread.currentThread().getName(), sc);
			Thread esc_thread = new Thread(esc);
			esc_thread.start();
			
			//En cada cliente creo e inicio un thread para leer lo que viene del sv
			Leer leer = new Leer(socketCliente);
			Thread leer_thread = new Thread(leer);
			leer_thread.start();
			
			} catch(IOException e) {
				System.out.println("Error al conectar al servidor y/o inicializar los threads");
			}
		}
	}
	
	public class Leer implements Runnable {

		private ObjectInputStream in;
		private String msj;
		
		Leer(Socket socketCliente) throws IOException {
			//Inicializo el flujo de entrada del socket
			in = new ObjectInputStream(socketCliente.getInputStream());
		}
		
		@Override
		public void run() {
			do {
				if(in != null) {
					try {
						msj = (String) in.readObject();
					} catch (IOException | ClassNotFoundException e) {
						System.out.println("Error al leer. Puede que el cliente se haya desconectado");
					}
				} 
				
				if(!msj.equals("Salir")) {
					System.out.println("Mensaje del servidor: " + msj);

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) { }
				} else {
					in = null;
				}
			} while(in != null);
		}
	}

	public class Escribir implements Runnable {

		private ObjectOutputStream out;
		private String nombre;
		Scanner teclado;
		
		Escribir(Socket socketCliente, String nombreThread, Scanner sc) throws IOException {
			//Inicializo el flujo de salida del socket
			out = new ObjectOutputStream(socketCliente.getOutputStream());
			this.nombre = nombreThread;
			teclado = sc;
		}
		
		@Override
		public void run() {
			
			String str = null;
			do {
				System.out.print("\n" + nombre + " - Escribir al sv: ");
				str = teclado.nextLine();
				if(!str.equals("Salir"))
					str = nombre + " - " + str;
				try {
					//System.out.println("Escribiendo al sv");
					out.writeObject(str);
					out.flush();
				} catch (IOException e) {
					System.out.println("Error al escribir en el servidor");
				}
				
				try {
					Thread.sleep(6000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} while(!str.equals("Salir"));
			teclado.close();
		}
	}
	
	public static void main(String[] args) throws IOException {
		
		//Creo los clientes
		Cliente cl1 = new Cliente(10001);
		Cliente cl2 = new Cliente(10001);
		
		//Conecto los clientes al servidor
		Thread cl1_con = new Thread(cl1.new Conectar());
		cl1_con.setName("CL1");
		cl1_con.start();
		
		Thread cl2_con = new Thread(cl2.new Conectar());
		cl2_con.setName("CL2");
		cl2_con.start();
	}
}
