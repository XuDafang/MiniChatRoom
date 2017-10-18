package chat.client;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import chat.function.Bean;
import chat.util.Util;

class CellRenderer extends JLabel implements ListCellRenderer {
	CellRenderer() {
	    setOpaque(true);
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		if (value != null)  {
			setText(value.toString());
			setIcon(new ImageIcon("images\\icon1.jpg"));
		}
		if (isSelected) {
			setBackground(new Color(255, 255, 153));
			setForeground(Color.black);
		} else {
			setBackground(Color.white);
			setForeground(Color.black);
		}
		setEnabled(list.isEnabled());
		setFont(new Font("sdf", Font.ROMAN_BASELINE, 13));
		setOpaque(true);
		return this;
	}
}

class UUListModel extends AbstractListModel{
	private Vector vs;
	public UUListModel(Vector vs){
	    this.vs = vs;
	}

	@Override
	public Object getElementAt(int index) {
		// TODO Auto-generated method stub
		return vs.get(index);
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return vs.size();
	}
}

public class Chatroom extends JFrame {
	//serialVersionUID NOTE7
	private static final long serialVersionUID = 6129126482250125466L;
	private static JPanel contentPane;
	private static Socket mySocket;
	private static ObjectOutputStream oos;
	private static ObjectInputStream ois;
	private static String name;
	private static JTextArea textArea;
	private static AbstractListModel listmodel;
	private static JList list;
	private static String filePath;
	private static JLabel alertLabel;
	private static JProgressBar progressBar;//
	private static Vector onlines;
	private static boolean isSendFile = false;
	private static boolean isReceiveFile = false;

	private static File file1, file2;
	private static URL cb, cb2;
	private static AudioClip aau, aau2;

	/**
	 * Create the frame.
	 */

	public Chatroom(String u_name, Socket client) {
		//
		name = u_name;
		mySocket = client;
		onlines = new Vector();
		SwingUtilities.updateComponentTreeUI(this);
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		setTitle(name);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(200, 100, 688, 510);
		contentPane = new JPanel() {
			private static final long serialVersionUID = 1L;
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(new ImageIcon("images\\chatRoom.jpg").getImage(), 0, 0,
						getWidth(), getHeight(), null);
			}

		};
		setContentPane(contentPane);
		contentPane.setLayout(null);

		//Chat info shown in this area
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 410, 300);
		getContentPane().add(scrollPane);

		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setFont(new Font("sdf", Font.BOLD, 13));
		scrollPane.setViewportView(textArea);

		//Type area
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 347, 411, 97);
		getContentPane().add(scrollPane_1);

		final JTextArea textArea_1 = new JTextArea();
		textArea_1.setLineWrap(true);
		textArea_1.setWrapStyleWord(true);
		scrollPane_1.setViewportView(textArea_1);
/*
		final JButton btnClose = new JButton("close");
		btnClose.setBounds(214, 448, 70, 30);
		getContentPane().add(btnClose);
*/
		JButton btnSend = new JButton("send");
		btnSend.setBounds(313, 448, 60, 30);
		getRootPane().setDefaultButton(btnSend);
		getContentPane().add(btnSend);

		//Online clients
		listmodel = new UUListModel(onlines) ;
		list = new JList(listmodel);
		list.setCellRenderer(new CellRenderer());
		list.setOpaque(false);
        Border etch = BorderFactory.createEtchedBorder();
		list.setBorder(BorderFactory.createTitledBorder(etch, "  :", TitledBorder.LEADING, TitledBorder.TOP,
				new Font("sdf", Font.BOLD, 20), Color.black));
		JScrollPane scrollPane_2 = new JScrollPane(list);
		scrollPane_2.setBounds(430, 10, 245, 375);
		scrollPane_2.setOpaque(false);
		scrollPane_2.getViewport().setOpaque(false);
		getContentPane().add(scrollPane_2);

		progressBar = new JProgressBar();
		progressBar.setBounds(430, 390, 245, 15);
		progressBar.setMinimum(1);
		progressBar.setMaximum(100);
		getContentPane().add(progressBar);

		//Alert in file transfer
		alertLabel = new JLabel("File transfer progress:");
		alertLabel.setFont(new Font("SimSun", Font.PLAIN, 12));
		alertLabel.setBackground(Color.WHITE);
		alertLabel.setBounds(430, 410, 245, 15);
		getContentPane().add(alertLabel);

		try {
			oos = new ObjectOutputStream(mySocket.getOutputStream());
			Bean bean = new Bean();
			bean.setType(0);
			bean.setName(name);
			bean.setTimer(Util.getTimer());
			oos.writeObject(bean);
			oos.flush();

			file1 = new File("sounds\\msg.wav");
			cb = file1.toURL();
			aau = Applet.newAudioClip(cb);
			file2 = new File("sounds\\NewOnline.wav");
			cb2 = file2.toURL();
			aau2 = Applet.newAudioClip(cb2);

			new ClientInputThread().start();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		btnSend.addActionListener(e -> {
            String info = textArea_1.getText();
            List to = list.getSelectedValuesList();

            if (to.size() < 1) {
                JOptionPane.showMessageDialog(getContentPane(), "Please choose a client!");
                return;
            }
            if (to.toString().contains(name+"(??)")) {
                JOptionPane
                        .showMessageDialog(getContentPane(), "Can't chat with yourself!");
                return;
            }
            if (info.equals("")) {
                JOptionPane.showMessageDialog(getContentPane(), "Nothing to send!");
                return;
            }

            Bean clientBean = new Bean();
            clientBean.setType(1);
            clientBean.setName(name);
            String time = Util.getTimer();
            clientBean.setTimer(time);
            clientBean.setInfo(info);
            HashSet set = new HashSet();
            set.addAll(to);
            clientBean.setClients(set);

            textArea.append(time + " I say to " + to + ":\r\n" + info + "\r\n");

            sendMessage(clientBean);
            textArea_1.setText(null);
            textArea_1.requestFocus();
        });
/*
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(isSendFile || isReceiveFile){
					JOptionPane.showMessageDialog(contentPane,
							"Sending File ...",
							"Error Message", JOptionPane.ERROR_MESSAGE);
				}else{
				btnClose.setEnabled(false);
				Bean clientBean = new Bean();
				clientBean.setType(-1);
				clientBean.setName(name);
				clientBean.setTimer(Util.getTimer());
				sendMessage(clientBean);
				}
			}
		});
*/
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				if(isSendFile || isReceiveFile){
					JOptionPane.showMessageDialog(contentPane,
							"Sending File ...Wait a moment please",
							"Error Message", JOptionPane.ERROR_MESSAGE);
				}else{
				int result = JOptionPane.showConfirmDialog(getContentPane(),
						"Are you sure to leave ?");
				if (result == 0) {
					Bean clientBean = new Bean();
					clientBean.setType(-1);
					clientBean.setName(name);
					clientBean.setTimer(Util.getTimer());
					sendMessage(clientBean);
				}
				}
			}
		});

		list.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				List to = list.getSelectedValuesList();
				if (e.getClickCount() == 2) {
					if (to.toString().contains(name+"(I)")) {
						JOptionPane
								.showMessageDialog(getContentPane(), "Sorry,you can't send file to yourself.");
						return;
					}
					JFileChooser chooser = new JFileChooser();
					chooser.setDialogTitle("Choose a file");
					chooser.showDialog(getContentPane(), "Chosse");

					if (chooser.getSelectedFile() != null) {
						filePath = chooser.getSelectedFile().getPath();
						File file = new File(filePath);
						//
						if (file.length() == 0) {
							JOptionPane.showMessageDialog(getContentPane(),
									filePath + "Empty file, not allow!");
							return;
						}

						Bean clientBean = new Bean();
						clientBean.setType(2);//
						clientBean.setSize(new Long(file.length()).intValue());
						clientBean.setName(name);
						clientBean.setTimer(Util.getTimer());
						clientBean.setFileName(file.getName());
						clientBean.setInfo("I want to send a file");

						//which client should be sent to
						HashSet<String> set = new HashSet<String>();
						set.addAll(list.getSelectedValuesList());
						clientBean.setClients(set);
						sendMessage(clientBean);
					}
				}
			}
		});
	}

	class ClientInputThread extends Thread {

		@Override
		public void run() {
			try {
				while (true) {
					ois = new ObjectInputStream(mySocket.getInputStream());
					final Bean  bean = (Bean) ois.readObject();
					switch (bean.getType()) {
					case 0: {
						// new client is online, update the online-list of all online clients
						onlines.clear();
						HashSet<String> clients = bean.getClients();
						Iterator<String> it = clients.iterator();
						while (it.hasNext()) {
							String ele = it.next();
							if (name.equals(ele)) {
								onlines.add(ele + "(I)");
							} else {
								onlines.add(ele);
							}
						}

						listmodel = new UUListModel(onlines);
						list.setModel(listmodel);
						aau2.play();
						textArea.append(bean.getInfo() + "\r\n");
						textArea.selectAll();
						break;
					}
                    //off line

					case -1: {
						return;
					}

                    //Chat
					case 1: {
					    String tmp = bean.getClients().toString();
					    String tmp2 = tmp.substring(tmp.indexOf("[")+1,tmp.indexOf("]"));

						String info = bean.getTimer() + "  " + bean.getName()
								+ " says to " + tmp2 + ":\r\n";
						if (tmp2.equals(name) ) {
                            info = bean.getTimer() + "  " + bean.getName() + " says to me: \r\n";
						}
						aau.play();
						textArea.append(info+bean.getInfo() + "\r\n");
						textArea.selectAll();
						break;
					}
					//want to send a file
					case 2: {
						new Thread(() -> {
                            //show dialog of Accept/NotAccept file
                            int result = JOptionPane.showConfirmDialog(getContentPane(), bean.getInfo());
                            switch(result){
                            //Accept the file
                            case 0:{
                                JFileChooser chooser = new JFileChooser();
                                chooser.setDialogTitle("Save file");
                                chooser.setSelectedFile(new File(bean.getFileName()));
                                chooser.showDialog(getContentPane(), "Save");
                                String saveFilePath =chooser.getSelectedFile().toString();
                                Bean acceptBean = new Bean();
								acceptBean.setType(3);
								acceptBean.setName(name);
								acceptBean.setTimer(Util.getTimer());
								acceptBean.setFileName(saveFilePath);
								acceptBean.setInfo("Sure to accept the file");

                                //which client(s) the file should be sent to
                                HashSet<String> set = new HashSet<>();
                                set.add(bean.getName());
								acceptBean.setClients(set);
								acceptBean.setTo(bean.getClients());

                                try {
                                    ServerSocket ss = new ServerSocket(0);

									acceptBean.setIp(mySocket.getInetAddress().getHostAddress());
									acceptBean.setPort(ss.getLocalPort());
                                    sendMessage(acceptBean);

                                    isReceiveFile=true;
                                    Socket sk = ss.accept();
                                    textArea.append(Util.getTimer() + "  " + bean.getFileName()
                                            + "Saving file\r\n");
                                    DataInputStream dis = new DataInputStream(
                                            new BufferedInputStream(sk.getInputStream()));
                                    DataOutputStream dos = new DataOutputStream(
                                            new BufferedOutputStream(new FileOutputStream(saveFilePath)));

                                    int count = 0;
                                    int num = bean.getSize() / 100;
                                    int index = 0;
                                    while (count < bean.getSize()) {
                                        int t = dis.read();
                                        dos.write(t);
                                        count++;

                                        if(num>0){
                                            if (count % num == 0 && index < 100) {
                                                progressBar.setValue(++index);
                                            }
                                            alertLabel.setText("Downloading Progress:" + count
                                                    + "/" + bean.getSize() + "All" + index
                                                    + "%");
                                        }else{
                                            alertLabel.setText("Downloading Progress:" + count
                                                    + "/" + bean.getSize() +"All:"+new Double(new Double(count).doubleValue()/new Double(bean.getSize()).doubleValue()*100).intValue()+"%");
                                            if(count==bean.getSize()){
                                                progressBar.setValue(100);
                                            }
                                        }

                                    }

                                    PrintWriter out = new PrintWriter(sk.getOutputStream(),true);
                                    out.println(Util.getTimer() + " File " + "[" + bean.getFileName()+"]"
                                            + " sent to "+name+" is saved \r\n");
                                    out.flush();
                                    dos.flush();
                                    dos.close();
                                    out.close();
                                    dis.close();
                                    sk.close();
                                    ss.close();
                                    textArea.append(Util.getTimer()
                                            + " File"  + bean.getFileName() + " is saved in:"+saveFilePath+"\r\n");
                                    isReceiveFile = false;
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                                break;
                            }
                            default: {
                                Bean msgBean = new Bean();
								msgBean.setType(4);
								msgBean.setName(name);
								msgBean.setTimer(Util.getTimer());
								msgBean.setFileName(bean.getFileName());
								msgBean.setInfo(Util.getTimer() + "  "
                                        + name + "Cancel"
                                        + bean.getFileName() + "]");

                                HashSet<String> set = new HashSet<String>();
                                set.add(bean.getName());
								msgBean.setClients(set);
								msgBean.setTo(bean.getClients());
                                sendMessage(msgBean);
                                break;
                            }
                        }
                        }).start();
						break;
					}
					case 3: {
						textArea.append(bean.getTimer() + "  "+ bean.getName() + "Sure to accept the file" + "File transferring\r\n");
						new Thread(() -> {
                            try {
                                isSendFile = true;
                                Socket s = new Socket(bean.getIp(),bean.getPort());
                                DataInputStream dis = new DataInputStream(
                                        new FileInputStream(filePath));
                                DataOutputStream dos = new DataOutputStream(
                                        new BufferedOutputStream(s.getOutputStream()));
                                int size = dis.available();
                                int count = 0;
                                int num = size / 100;
                                int index = 0;
                                while (count < size) {
                                    int t = dis.read();
                                    dos.write(t);
                                    count++;
                                    if(num>0){
                                        if (count % num == 0 && index < 100) {
                                            progressBar.setValue(++index);
                                        }
                                        alertLabel.setText("Uploading Progress:" + count + "/"
                                                        + size + index
                                                        + "%");
                                    }else{
                                        alertLabel.setText("Uploading Progress:" + count + "/"
                                                + size +new Double(new Double(count).doubleValue()/new Double(size).doubleValue()*100).intValue()+"%"
                                                );
                                        if(count==size){
                                            progressBar.setValue(100);
                                        }
                                    }
                                }
                                dos.flush();
                                dis.close();
                                BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                                textArea.append( br.readLine() + "\r\n");
                                isSendFile = false;
                                br.close();
                                s.close();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        }).start();
						break;
					}
					case 4: {
						textArea.append(bean.getInfo() + "\r\n");
						break;
					}
					default: {
						break;
					}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (mySocket != null) {
					try {
						mySocket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.exit(0);
			}
		}
	}

	private void sendMessage(Bean clientBean) {
		try {
			oos = new ObjectOutputStream(mySocket.getOutputStream());
			oos.writeObject(clientBean);
			oos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
