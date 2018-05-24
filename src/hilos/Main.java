/*package hilos;

import java.io.IOException;

public class Main {

	public final static int PUERTO = 10001;
	
	public static void main(String[] args) throws IOException {
		
		//Creo los clientes
		Cliente cl1 = new Cliente(PUERTO, "Thread 1");
		Cliente cl2 = new Cliente(PUERTO, "Thread 2");
		//Cliente cl3 = new Cliente(PUERTO, "Thread 3");
		
		//Conecto los clientes al servidor
		Thread cl1_con = new Thread(cl1.new Conectar());
		cl1_con.start();
		
		Thread cl2_con = new Thread(cl2.new Conectar());
		cl2_con.start();
		
		/*Thread cl3_con = new Thread(cl3.new Conectar());
		cl3_con.start();

		

	}

}*/
