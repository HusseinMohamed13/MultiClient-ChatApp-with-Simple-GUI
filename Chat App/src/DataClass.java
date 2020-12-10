import java.awt.List;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;

public class DataClass {

	private static ArrayList<Object> RegisteredUsersArray = new ArrayList<Object>();
	private static ArrayList<Object> LoggedinUsersArray = new ArrayList<Object>();
	private static ArrayList<String> ChatArray = new ArrayList<String>();
	private static ArrayList<String> InappropriateWords = new ArrayList<String>();
	private static DefaultListModel<String> list = new DefaultListModel<>();
	private static JList<String> List = new JList<>(list);
	private static JFrame ListF = new JFrame();

	public DataClass() {
		ListF.setTitle("Ongoing Chats");
		List.setBounds(0, 30, 390, 370);
		ListF.add(List);
		ListF.setSize(400, 400);
		ListF.setLayout(null);
		ListF.setVisible(true);

		Object[] obj = new Object[3];
		obj[0] = "dsad";
		obj[1] = "hussein";
		obj[2] = "000";
		RegisteredUsersArray.add(obj);
		Object[] obj1 = new Object[3];
		obj1[0] = "dsad";
		obj1[1] = "ziyad";
		obj1[2] = "000";
		RegisteredUsersArray.add(obj1);
		Object[] obj2 = new Object[3];
		obj2[0] = "dsad";
		obj2[1] = "mostafa";
		obj2[2] = "000";
		RegisteredUsersArray.add(obj2);
		Object[] obj3 = new Object[3];
		obj3[0] = "dsad";
		obj3[1] = "hassan";
		obj3[2] = "000";
		RegisteredUsersArray.add(obj3);

		InappropriateWords.add("1");
		InappropriateWords.add("3");
		InappropriateWords.add("5");
		InappropriateWords.add("12");
		InappropriateWords.add("word");
	}

	public String Login(Socket clientsocket, String UserName, String Password) {
		for (int x = 0; x < RegisteredUsersArray.size(); x++) {
			Object[] obj1 = new Object[3];
			obj1 = (Object[]) RegisteredUsersArray.get(x);
			String username = (String) obj1[1];
			String password = (String) obj1[2];
			if (!password.equals(Password) && username.equals(UserName)) {
				return "Not Loggedin" + ":" + "401 error password is incorrect";
			} else if (password.equals(Password) && username.equals(UserName)) {
				String Name = (String) obj1[0];
				Object[] obj2 = new Object[2];
				obj2[0] = UserName;
				obj2[1] = clientsocket;
				LoggedinUsersArray.add(obj2);
				System.out.println(UserName+" loggedin");
				return "Loggedin" + ":" + UserName;
			}
		}
		return "Not Loggedin" + ":" + "404 error username is not found";
	}

	public String Register(String Name, String UserName, String Password) {
		Object[] obj = new Object[3];
		obj[0] = Name;
		obj[1] = UserName;
		obj[2] = Password;
		for (int x = 0; x < RegisteredUsersArray.size(); x++) {
			Object[] obj1 = new Object[3];
			obj1 = (Object[]) RegisteredUsersArray.get(x);
			String username = (String) obj1[1];
			if (UserName.equals(username)) {
				return "Not Registered:";
			}
		}
		RegisteredUsersArray.add(obj);
		return "Registered:";
	}

	public Socket GetSocket(String UserName) {
		for (int x = 0; x < LoggedinUsersArray.size(); x++) {
			Object[] obj = new Object[2];
			obj = (Object[]) LoggedinUsersArray.get(x);
			String username = (String) obj[0];
			Socket socket = (Socket) obj[1];
			if (username.equals(UserName)) {
				return socket;
			}
		}
		return null;
	}

	public String GetUserName(Socket s) {
		for (int x = 0; x < LoggedinUsersArray.size(); x++) {
			Object[] obj = new Object[2];
			obj = (Object[]) LoggedinUsersArray.get(x);
			String username = (String) obj[0];
			Socket socket = (Socket) obj[1];
			if (socket.equals(s)) {
				return username;
			}
		}
		return null;
	}

	public void AddChat(String chatname) {
		ChatArray.add(chatname);
		list.addElement(chatname);
		List.setModel(list);
	}

	public boolean JoinChat(String member, String chatname) {
		String newchatname = chatname + ":" + member;
		int index = ChatArray.indexOf(chatname);
		if(index<0) {
			return false;
		}
		ChatArray.set(index, newchatname);
		index = list.indexOf(chatname);
		list.set(index, newchatname);
		List.setModel(list);
		return true;
	}

	public boolean CheckContent(String content) {
		for (int x = 0; x < InappropriateWords.size(); x++) {
			Pattern pattern = Pattern.compile(InappropriateWords.get(x), Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(content);
			boolean matchFound = matcher.find();
			if (matchFound) {
				return true;
			}
		}
		return false;
	}
}