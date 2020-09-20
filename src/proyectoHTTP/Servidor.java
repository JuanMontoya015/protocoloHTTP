package proyectoHTTP;

import java.net.ServerSocket;
import java.net.Socket;

import proyectoHTTP.Protocolo;

public class Servidor {

  public static void main( String[] args ) throws Exception {
      try (ServerSocket serverSocket = new ServerSocket(50000)) {
          while (true) {
        	  Socket client = serverSocket.accept();
        	  Protocolo hilo = new Protocolo(client);
        	  new Thread(hilo).start();
              // PRUEBA
          }
      }
  }
}

// INSTANCIA CALCULATE REQUESTE EN CALCULATOR NODE
// CALCULATOR_NODE UN RUNNABLE
//(se crea el servidor Calculator_Node)