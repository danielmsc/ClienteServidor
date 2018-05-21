# ClienteServidor
Implementación de servidor y clientes multihilo para enviar y recibir mensajes. 

El servidor se inicializa y crea hilos por cada cliente conectado para poder leer sus mensajes. Se utiliza un hilo adicional para escuchar conexiones entrantes.
Reenvía el mensaje al resto de los clientes, excepto al que lo originó, también mediante el uso de hilos.

Los clientes tienen hilos para conectarse al servidor, para escribirle mensajes al servidor, y también para recibir mensajes del mismo.

# DETALLES A REVISAR:
- Uso de teclado/mensaje hardcodeado.
- Emprolijar salidas por pantalla (consola).
- Emprolijar código.
- Mejorar intervalos de los timers.
- Los hilos corren por siempre (while(true)), por lo tanto no se implementa cierre de sockets.

# NOTAS:
- No se utiliza writeUTF (se utiliza writeObject en su lugar).
- No se utiliza readUTF (se utiliza (String) readObject en su lugar). 
- Se utiliza flush en los out.
