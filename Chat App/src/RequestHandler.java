import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.ArrayList;

public class RequestHandler implements Runnable {

	private final Socket socket;
	private static DataClass dataclass;
	private final OutputStream os;
	private final InputStream is;
	private static int Warnings;

	public RequestHandler(Socket socket, DataClass dataclass) throws Exception {
		this.socket = socket;
		this.dataclass = dataclass;

		os = socket.getOutputStream();
		is = socket.getInputStream();

		Warnings = 0;
	}

	public void run() {
		try {
			processRequest();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private void processRequest() throws Exception {
		System.out.println("Client is connected");

		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);

		while (true) {
			String Request = br.readLine();
			if (Request != null) {
				String[] RequestArray = Request.split(":");
				String Response;
				switch (RequestArray[0]) {
				case "Login":
					Response = dataclass.Login(socket, RequestArray[1], RequestArray[2]);
					bw.write(Response + "\r\n");
					bw.flush();
					break;
				case "Register":
					Response = dataclass.Register(RequestArray[1], RequestArray[2], RequestArray[3]);
					bw.write(Response + "\r\n");
					bw.flush();
					break;
				case "Friend Request":
					Socket ToSocket = dataclass.GetSocket(RequestArray[2]);
					if (ToSocket == null) {
						bw.write("UserName NotFound" + ":" + RequestArray[2] + "\r\n");
						bw.flush();
					} else {
						OutputStream os1 = ToSocket.getOutputStream();
						OutputStreamWriter osw1 = new OutputStreamWriter(os1);
						BufferedWriter bw1 = new BufferedWriter(osw1);
						bw1.write("Friend Request" + ":" + RequestArray[1] + ":" + RequestArray[2] + "\r\n");
						bw1.flush();
					}
					break;
				case "Accept Request":
					dataclass.AddChat(RequestArray[1] + ":" + RequestArray[2]);
					Socket ToSocket1 = dataclass.GetSocket(RequestArray[2]);
					OutputStream os2 = ToSocket1.getOutputStream();
					OutputStreamWriter osw2 = new OutputStreamWriter(os2);
					BufferedWriter bw2 = new BufferedWriter(osw2);
					bw2.write("Accept Request" + ":" + RequestArray[1] + ":" + RequestArray[2] + "\r\n");
					bw2.flush();
					break;
				case "Private Message":
					if (dataclass.CheckContent(RequestArray[3])) {
						Warnings++;
						bw.write("Blocked" + ":" + RequestArray[2] + "\r\n");
						bw.flush();
					} else {
						Socket ToSocket2 = dataclass.GetSocket(RequestArray[2]);
						OutputStream os3 = ToSocket2.getOutputStream();
						OutputStreamWriter osw3 = new OutputStreamWriter(os3);
						BufferedWriter bw3 = new BufferedWriter(osw3);
						bw3.write("Private Message" + ":" + RequestArray[1] + ":" + RequestArray[3] + "\r\n");
						bw3.flush();
					}
					break;
				case "Group Message":
					if (dataclass.CheckContent(RequestArray[RequestArray.length - 1])) {
						Warnings++;
						String chatname = "";
						for (int x = 2; x < RequestArray.length - 1; x++) {
							if (x == RequestArray.length - 2) {
								chatname += RequestArray[x];
							} else {
								chatname += RequestArray[x] + ":";
							}
						}
						bw.write("Blocked" + ":" + chatname + "\r\n");
						bw.flush();
					} else {
						for (int x = 2; x < RequestArray.length - 1; x++) {
							Socket ToSocket3 = dataclass.GetSocket(RequestArray[x]);
							OutputStream os4 = ToSocket3.getOutputStream();
							OutputStreamWriter osw4 = new OutputStreamWriter(os4);
							BufferedWriter bw4 = new BufferedWriter(osw4);
							String chatname = "";
							String[] TempArray = RemoveElement(RequestArray, x);
							for (int x1 = 1; x1 < TempArray.length - 1; x1++) {
								if (x1 == TempArray.length - 2) {
									chatname += TempArray[x1];
								} else {
									chatname += TempArray[x1] + ":";
								}
							}
							bw4.write("Group Message" + ":" + RequestArray[1] + ":" + chatname + ":"
									+ RequestArray[RequestArray.length - 1] + "\r\n");
							bw4.flush();
						}
					}
					break;
				case "Join Chat":
					String chatname = "";
					for (int x = 2; x < RequestArray.length; x++) {
						if (x == RequestArray.length - 1) {
							chatname += RequestArray[x];
						} else {
							chatname += RequestArray[x] + ":";
						}
					}
					if (dataclass.JoinChat(RequestArray[1], chatname)) {
						bw.write("GroupName Founded" + ":" + chatname + "\r\n");
						bw.flush();
						for (int x = 2; x < RequestArray.length; x++) {
							String chatname1 = "";
							Socket ToSocket4 = dataclass.GetSocket(RequestArray[x]);
							OutputStream os5 = ToSocket4.getOutputStream();
							OutputStreamWriter osw5 = new OutputStreamWriter(os5);
							BufferedWriter bw5 = new BufferedWriter(osw5);
							String[] TempArray = RemoveElement(RequestArray, x);
							for (int x1 = 2; x1 < TempArray.length; x1++) {
								if (x1 == TempArray.length - 1) {
									chatname1 += TempArray[x1];
								} else {
									chatname1 += TempArray[x1] + ":";
								}
							}
							bw5.write("Join Chat" + ":" + RequestArray[1] + ":" + chatname1 + "\r\n");
							bw5.flush();
						}
					} else {
						bw.write("GroupName NotFound" + "\r\n");
						bw.flush();
					}
					break;
				default:
					break;
				}
			}
		}
	}

	public static String[] RemoveElement(String[] arr, int index) {

		if (arr == null || index < 0 || index >= arr.length) {

			return arr;
		}

		String[] anotherArray = new String[arr.length - 1];

		for (int i = 0, k = 0; i < arr.length; i++) {
			if (i == index) {
				continue;
			}
			anotherArray[k++] = arr[i];
		}
		return anotherArray;
	}
}
