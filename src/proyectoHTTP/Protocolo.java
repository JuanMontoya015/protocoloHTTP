package proyectoHTTP;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Protocolo implements Runnable {

	Socket client;
	private static int BLOCK_SIZE = 1000;

	public Protocolo(Socket client) {
		this.client = client;
	}

	public void run() {
		// TODO Auto-generated method stub

		try {

			BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
			StringBuilder peticionConstructor = new StringBuilder();
			String linea;
			
			while (!(linea = br.readLine()).isEmpty()) {
				peticionConstructor.append(linea + "\r\n");
			}

			String peticion = peticionConstructor.toString();
			String[] peticionLineas = peticion.split("\r\n");
			String[] peticionLinea = peticionLineas[0].split(" ");
			String path = peticionLinea[1];
			System.out.println(path);

			Path rutaArchivo = obtenerRutaArchivo(path);
			enviarRespuesta(client, rutaArchivo);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private static void enviarRespuesta(Socket client, Path rutaArchivo)
			throws IOException {
		
		String status;
		boolean banderazo;
		String tipoContenido;
		String contentLength;
		byte[] bloque = null; 
	    int bytesRead = 0;
		
		FileInputStream content = null;
		
		if(Files.exists(rutaArchivo)) {
			content = new FileInputStream(rutaArchivo.toString());
	    	  bloque = new byte[BLOCK_SIZE];
	          status = "200 OK";
	          tipoContenido = Files.probeContentType(rutaArchivo);
	          contentLength = Files.readAllBytes(rutaArchivo).length+"";
	          banderazo = true;
	          
	      }else {
	    	  
	    	  status = "404 Not Found";
	          tipoContenido = "text/html";
	          bloque = "<h1>404 Not found</h1>".getBytes();
	          contentLength = bloque.length+"";
	          banderazo = false; 
	          
	      }
		
		OutputStream clientOutput = client.getOutputStream();
		clientOutput.write(("HTTP/1.1 \r\n" + status).getBytes());
		System.out.println("HTTP/1.1" + status);

		clientOutput.write(("Content-Length: " + contentLength + "\r\n").getBytes());
		System.out.println("Content-Length: " + contentLength);

		clientOutput.write(("Content-Type: " + tipoContenido + "\r\n").getBytes());
		System.out.println("Content-Type: " + tipoContenido);

		clientOutput.write("\r\n".getBytes());
		System.out.println("");

		
		if (banderazo) {
			while ((bytesRead = content.read(bloque)) == BLOCK_SIZE) {
				clientOutput.write(bloque, 0, bytesRead);
				System.out.println(new String(bloque));
			}
		} else {
			clientOutput.write(bloque);
		}
		
		clientOutput.write("\r\n\r\n".getBytes());

		clientOutput.flush();
		client.close();
	}

	private static Path obtenerRutaArchivo(String path) {
		if ("/".equals(path)) {
			path = "\\index.html";
		}
		return Paths.get("../proyectoHTTP/recursos/templates", path);
	}

	private static String solicitudTipoContenido(Path rutaArchivo) throws IOException {
		return Files.probeContentType(rutaArchivo);
	}
}