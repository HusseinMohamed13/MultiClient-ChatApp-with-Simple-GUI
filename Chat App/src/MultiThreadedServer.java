import java.awt.List;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MultiThreadedServer {

	private static Socket socket;
	public static DataClass dataclass;

	public static void main(String argv[]) throws Exception {
		dataclass = new DataClass();
		ServerSocket serverSocket = new ServerSocket(9999);
		System.out.println("Server is listening on port 9999");
		while (true) {
			socket = serverSocket.accept();
			RequestHandler request = new RequestHandler(socket,dataclass);
			Thread thread = new Thread(request);

			thread.start();
		}
	}
	
}
