import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Client {

	private static final String JTextField = null;
	private static Socket socket;
	private static OutputStream os;
	private static InputStream is;
	private static String UserName;
	private static JFrame LoginF = new JFrame();
	private static JFrame UserF = new JFrame();
	private static JFrame RegisterF = new JFrame();
	private static DefaultListModel<String> list = new DefaultListModel<>();
	private static JList<String> List = new JList<>(list);
	private static ArrayList<Object> ChatArray = new ArrayList<Object>();

	public static void main(String argv[]) throws Exception {
		socket = new Socket("localhost", 9999);

		String title = "(Client#";
		title += Integer.toString(socket.getLocalPort());
		title += ")";
		LoginF.setTitle("Login Window" + title);
		RegisterF.setTitle("Register Window" + title);
		os = socket.getOutputStream();
		is = socket.getInputStream();
		LoginWindow();
		while (true) {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String Response = br.readLine();
			String[] ResponseArray = Response.split(":");

			switch (ResponseArray[0]) {
			case "Not Loggedin":
				JOptionPane.showMessageDialog(LoginF, ResponseArray[1]);
				break;
			case "Loggedin":
				UserName = ResponseArray[1];
				UserWindow(UserName);
				LoginF.dispose();
				break;
			case "Registered":
				RegisterF.dispose();
				break;
			case "Not Registered":
				JOptionPane.showMessageDialog(RegisterF, "UserName is already used");
				break;
			case "Friend Request":
				int a = JOptionPane.showConfirmDialog(UserF, "Friend Request from: " + ResponseArray[1]);
				if (a == JOptionPane.YES_OPTION) {
					AcceptRequest(ResponseArray[1]);
					list.addElement(ResponseArray[1]);
					List.setModel(list);
				}
				AddChatWindow(ResponseArray[1]);
				break;
			case "Accept Request":
				list.addElement(ResponseArray[1]);
				List.setModel(list);
				AddChatWindow(ResponseArray[1]);
				break;
			case "UserName NotFound":
				JOptionPane.showMessageDialog(UserF, ResponseArray[1]+": is invalid username");
				break;
			case "Join Chat":
				String chatname = "";
				for (int x = 2; x < ResponseArray.length; x++) {
					if (x == ResponseArray.length - 1) {
						chatname += ResponseArray[x];
					} else {
						chatname += ResponseArray[x] + ":";
					}
				}
				String newchatname = chatname + ":" + ResponseArray[1];
				int index = list.indexOf(chatname);
				list.set(index, newchatname);
				List.setModel(list);
				UpdateChatName(chatname, newchatname);
				break;
			case "GroupName NotFound":
				JOptionPane.showMessageDialog(UserF, "Invalid chatname");
				break;
			case "GroupName Founded":
				String chatname5 = "";
				for (int x = 1; x < ResponseArray.length; x++) {
					if (x == ResponseArray.length - 1) {
						chatname5 += ResponseArray[x];
					} else {
						chatname5 += ResponseArray[x] + ":";
					}
				}
				list.addElement(chatname5);
				List.setModel(list);
				AddChatWindow(chatname5);
				break;
			case "Private Message":
				for (int x = 0; x < ChatArray.size(); x++) {
					Object[] obj = new Object[3];
					obj = (Object[]) ChatArray.get(x);
					if (ResponseArray[1].equals(obj[0])) {
						if (!obj[2].equals("Blocked")) {
							((JTextArea) obj[1]).append(ResponseArray[1] + ": " + ResponseArray[2] + "\n");
						}
					}
				}
				break;
			case "Group Message":
				String chatname1 = "";
				for (int x = 2; x < ResponseArray.length - 1; x++) {
					if (x == ResponseArray.length - 2) {
						chatname1 += ResponseArray[x];
					} else {
						chatname1 += ResponseArray[x] + ":";
					}
				}
				char[] c = chatname1.toCharArray();
				Arrays.sort(c);
				for (int x = 0; x < ChatArray.size(); x++) {
					Object[] obj = new Object[3];
					obj = (Object[]) ChatArray.get(x);
					char[] c1 = ((String) obj[0]).toCharArray();
					Arrays.sort(c1);
					if (Arrays.equals(c, c1)) {
						if (!obj[2].equals("Blocked")) {
							((JTextArea) obj[1]).append(ResponseArray[1] + ": " + ResponseArray[ResponseArray.length - 1] + "\n");
						}
					}
				}
				break;
			case "Blocked":
				String chatname2 = "";
				for (int x = 1; x < ResponseArray.length; x++) {
					if (x == ResponseArray.length - 1) {
						chatname2 += ResponseArray[x];
					} else {
						chatname2 += ResponseArray[x] + ":";
					}
				}
				UpdateChatBlock(chatname2);
				break;
			default:
				break;
			}
		}
	}

	public static void Register(String User, String UserName, String Password) throws Exception {
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);
		bw.write("Register" + ":" + User + ":" + UserName + ":" + Password + "\r\n");
		bw.flush();
	}

	public static void Login(String UserName, String Password) throws Exception {
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);
		bw.write("Login" + ":" + UserName + ":" + Password + "\r\n");
		bw.flush();
	}

	public static void FriendRequest(String username) throws Exception {
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);
		bw.write("Friend Request" + ":" + UserName + ":" + username + "\r\n");
		bw.flush();
	}

	public static void AcceptRequest(String username) throws Exception {
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);
		bw.write("Accept Request" + ":" + UserName + ":" + username + "\r\n");
		bw.flush();
	}

	public static void SendPrivateMessage(String username, String message) throws Exception {
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);
		bw.write("Private Message" + ":" + UserName + ":" + username + ":" + message + "\r\n");
		bw.flush();
	}

	public static void SendGroupMessage(String usersname, String message) throws Exception {
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);
		bw.write("Group Message" + ":" + UserName + ":" + usersname + ":" + message + "\r\n");
		bw.flush();
	}

	public static void JoinChat(String username) throws Exception {
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);
		bw.write("Join Chat" + ":" + UserName + ":" + username + "\r\n");
		bw.flush();
	}

	public static void UpdateChatName(String chatname, String newchatname) {
		Object[] obj = new Object[3];
		for (int x = 0; x < ChatArray.size(); x++) {
			obj = (Object[]) ChatArray.get(x);
			if (obj[0].equals(chatname)) {
				obj[0] = newchatname;
				ChatArray.set(x, obj);
			}
		}
	}

	public static void UpdateChatBlock(String chatname) {
		Object[] obj = new Object[3];
		for (int x = 0; x < ChatArray.size(); x++) {
			obj = (Object[]) ChatArray.get(x);
			if (obj[0].equals(chatname)) {
				obj[2] = "Blocked";
				((JTextArea) obj[1]).append("You are blocked from this chat" + "\n");
				((JTextArea) obj[1]).append("You can't send or recieve message" + "\n");
				ChatArray.set(x, obj);
			}
		}
	}

	public static void AddChatWindow(String username) {
		JTextArea ta = new JTextArea();
		ta.setBounds(5, 5, 385, 285);
		ta.setEditable(false);
		Object[] obj = new Object[3];
		obj[0] = username;
		obj[1] = ta;
		obj[2] = "";
		ChatArray.add(obj);
	}

	public static void LoginWindow() {
		JLabel UserNameLabel = new JLabel("User Name");
		UserNameLabel.setBounds(5, 100, 70, 50);
		JTextField UserNametf = new JTextField();
		UserNametf.setBounds(75, 100, 300, 50);

		JLabel PasswordLabel = new JLabel("Password");
		PasswordLabel.setBounds(5, 150, 70, 50);
		JPasswordField PasswordField = new JPasswordField();
		PasswordField.setBounds(75, 150, 300, 50);

		JButton LoginBtn = new JButton("Login");
		LoginBtn.setBounds(135, 200, 130, 50);
		LoginBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (UserNametf.getText().equals("") || PasswordField.getText().equals("")) {
					JOptionPane.showMessageDialog(LoginF, "Please provide username and password.");
				} else {
					try {
						Login(UserNametf.getText(), PasswordField.getText());
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});

		JButton RegisterBtn = new JButton("Register");
		RegisterBtn.setBounds(135, 250, 130, 50);
		RegisterBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RegisterWindow();
			}
		});

		LoginF.add(LoginBtn);
		LoginF.add(RegisterBtn);
		LoginF.add(UserNameLabel);
		LoginF.add(UserNametf);
		LoginF.add(PasswordLabel);
		LoginF.add(PasswordField);

		LoginF.setSize(400, 400);
		LoginF.setLayout(null);
		LoginF.setVisible(true);

	}

	public static void UserWindow(String UserName) {
		UserF.setTitle(UserName);

		JLabel UserNameLabel = new JLabel("User Name");
		UserNameLabel.setBounds(5, 25, 70, 50);
		JTextField UserNametf = new JTextField();
		UserNametf.setBounds(80, 25, 220, 50);

		List.setBounds(0, 100, 390, 300);

		JButton JoinChatBtn = new JButton("Join");
		JoinChatBtn.setBounds(5, 5, 70, 30);
		JoinChatBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String ChatName = JOptionPane.showInputDialog(UserF, "Enter chat name");
				try {
					JoinChat(ChatName);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		JButton AddBtn = new JButton("Add");
		AddBtn.setBounds(305, 25, 75, 50);
		AddBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (UserNametf.getText().equals("")) {
					JOptionPane.showMessageDialog(UserF, "Please provide user name");
				} else {
					try {
						FriendRequest(UserNametf.getText());
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					UserNametf.setText("");
				}
			}
		});

		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseEvent) {
				JList theList = (JList) mouseEvent.getSource();
				if (mouseEvent.getClickCount() == 1) {
					int index = theList.locationToIndex(mouseEvent.getPoint());
					if (index >= 0) {
						Object o = theList.getModel().getElementAt(index);
						String username = o.toString();
						for (int x = 0; x < ChatArray.size(); x++) {
							Object[] obj = new Object[2];
							obj = (Object[]) ChatArray.get(x);
							if (username.equals(obj[0])) {
								ChatWindow(username, (JTextArea) obj[1]);
							}
						}
					}
				}
			}
		};
		List.addMouseListener(mouseListener);

		UserF.add(UserNameLabel);
		UserF.add(UserNametf);
		UserF.add(AddBtn);
		UserF.add(JoinChatBtn);
		UserF.add(List);

		UserF.setSize(400, 400);
		UserF.setLayout(null);
		UserF.setVisible(true);

	}

	public static void RegisterWindow() {
		JLabel NameLabel = new JLabel("Name");
		NameLabel.setBounds(5, 100, 70, 50);
		JTextField Nametf = new JTextField();
		Nametf.setBounds(75, 100, 300, 50);

		JLabel UserNameLabel = new JLabel("User Name");
		UserNameLabel.setBounds(5, 150, 70, 50);
		JTextField UserNametf = new JTextField();
		UserNametf.setBounds(75, 150, 300, 50);

		JLabel PasswordLabel = new JLabel("Password");
		PasswordLabel.setBounds(5, 200, 70, 50);
		JPasswordField PasswordField = new JPasswordField();
		PasswordField.setBounds(75, 200, 300, 50);

		JButton RegisterBtn = new JButton("Register");
		RegisterBtn.setBounds(135, 300, 130, 50);
		RegisterBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (Nametf.getText().equals("") || UserNametf.getText().equals("")
						|| PasswordField.getText().equals("")) {
					JOptionPane.showMessageDialog(LoginF, "Please provide name ,username and password.");
				} else {
					try {
						Register(Nametf.getText(), UserNametf.getText(), PasswordField.getText());
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});

		RegisterF.add(NameLabel);
		RegisterF.add(Nametf);
		RegisterF.add(UserNameLabel);
		RegisterF.add(UserNametf);
		RegisterF.add(PasswordLabel);
		RegisterF.add(PasswordField);
		RegisterF.add(RegisterBtn);

		RegisterF.setSize(400, 400);
		RegisterF.setLayout(null);
		RegisterF.setVisible(true);
	}

	public static void ChatWindow(String name, JTextArea ta) {
		JFrame f = new JFrame();
		f.setTitle(name);

		JTextField ChatInputtf = new JTextField();
		ChatInputtf.setBounds(5, 300, 300, 80);

		JButton SendBtn = new JButton("Send");
		SendBtn.setBounds(315, 325, 70, 30);
		SendBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String str = "You: " + ChatInputtf.getText() + "\n";
				try {
					String check = "";
					for (int x = 0; x < ChatArray.size(); x++) {
						Object[] obj = new Object[3];
						obj = (Object[]) ChatArray.get(x);
						if (obj[0].equals(name)) {
							check = (String) obj[2];
						}
					}
					if (!check.equals("Blocked")) {
						ta.append(str);
						String[] Parse = name.split(":");
						if (Parse.length > 1) {
							SendGroupMessage(name, ChatInputtf.getText());
						} else {
							SendPrivateMessage(name, ChatInputtf.getText());
						}
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				ChatInputtf.setText("");
			}
		});

		f.add(ChatInputtf);
		f.add(SendBtn);
		f.add(ta);

		f.setSize(400, 400);
		f.setLayout(null);
		f.setVisible(true);

	}

}