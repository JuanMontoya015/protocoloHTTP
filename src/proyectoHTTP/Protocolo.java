package proyectoHTTP;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Protocolo implements Runnable {

	Socket client;

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
			if (Files.exists(rutaArchivo)) {
				// file exist
				String tipoContenido = solicitudTipoContenido(rutaArchivo);
				enviarRespuesta(client, "200 OK", tipoContenido, Files.readAllBytes(rutaArchivo));
			} else {
				// 404
				byte[] contenidoNoEncontrado = "<h1>NO SE ENCONTRÓ :(</h1>".getBytes();
				enviarRespuesta(client, "404 NO ENCONTRADO", "text/html", contenidoNoEncontrado);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private static void enviarRespuesta(Socket client, String status, String tipoContenido, byte[] content)
			throws IOException {
		OutputStream clientOutput = client.getOutputStream();
		clientOutput.write(("HTTP/1.1 \r\n" + status).getBytes());
		System.out.println("HTTP/1.1" + status);

		clientOutput.write(("Content-Length: " + content.length + "\r\n").getBytes());
		System.out.println("Content-Length: " + content.length);

		clientOutput.write(("Content-Length: " + tipoContenido + "\r\n").getBytes());
		System.out.println("Content-Length: " + tipoContenido);

		clientOutput.write("\r\n".getBytes());
		System.out.println("");

		clientOutput.write(content);
		System.out.println(content);
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