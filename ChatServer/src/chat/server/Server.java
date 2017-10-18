package chat.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


import chat.function.Bean;
import chat.function.ClientBean;

public class Server {
	private static ServerSocket ss;
	public static HashMap<String, ClientBean> onlines;
	static {
		try {
			ss = new ServerSocket(8520);
			onlines = new HashMap<>();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class CatClientThread extends Thread {
		private Socket client;
		private Bean bean;
		private ObjectInputStream ois;
		private ObjectOutputStream oos;

		public CatClientThread(Socket client) {

		    this.client = client;
		}

		@Override
		public void run() {
			try {
				//Always waiting sockets from clients
				while (true) {
					ois = new ObjectInputStream(client.getInputStream());
					bean = (Bean)ois.readObject();
					switch (bean.getType()) {
					case 0: {
						ClientBean cbean = new ClientBean();
						cbean.setName(bean.getName());
						cbean.setSocket(client);
						onlines.put(bean.getName(), cbean);
						Bean serverBean = new Bean();
						serverBean.setType(0);
						serverBean.setInfo(bean.getTimer() + "  " + bean.getName() + " is online");
						HashSet<String> set = new HashSet<>();
						set.addAll(onlines.keySet());
						serverBean.setClients(set);
						sendAll(serverBean);
						break;
					}
					//client is off line
					case -1: {
						Bean serverBean = new Bean();
						serverBean.setType(-1);

						try {
							oos = new ObjectOutputStream(client.getOutputStream());
							oos.writeObject(serverBean);
							oos.flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						onlines.remove(bean.getName());
						Bean serverBean2 = new Bean();
						serverBean2.setInfo(bean.getTimer() + "  "
								+ bean.getName() + " " + "is off line");
						serverBean2.setType(0);
						HashSet<String> set = new HashSet<>();
						set.addAll(onlines.keySet());
						serverBean2.setClients(set);

						sendAll(serverBean2);
						return;
					}
					//chat
					case 1: {
						Bean serverBean = new Bean();
						serverBean.setType(1);
						serverBean.setClients(bean.getClients());
						serverBean.setInfo(bean.getInfo());
						serverBean.setName(bean.getName());
						serverBean.setTimer(bean.getTimer());
						sendMessage(serverBean);
						break;
					}
					//want to send a file
					case 2: {
						Bean serverBean = new Bean();
						String info = bean.getTimer() + "  " + bean.getName()
								+ " want to send a file to you";
						serverBean.setType(2);
						serverBean.setClients(bean.getClients()); // destination of the file
						serverBean.setFileName(bean.getFileName());
						serverBean.setSize(bean.getSize());
						serverBean.setInfo(info);
						serverBean.setName(bean.getName());
						serverBean.setTimer(bean.getTimer());
						sendMessage(serverBean);
						break;
					}
					//Accept the file
					case 3: {
						Bean serverBean = new Bean();
						serverBean.setType(3);
						serverBean.setClients(bean.getClients());
						serverBean.setTo(bean.getTo());
						serverBean.setFileName(bean.getFileName());
						serverBean.setIp(bean.getIp());
						serverBean.setPort(bean.getPort());
						serverBean.setName(bean.getName());
						serverBean.setTimer(bean.getTimer());
						sendMessage(serverBean);
						break;
					}
					case 4: {
						Bean serverBean = new Bean();

						serverBean.setType(4);
						serverBean.setClients(bean.getClients());
						serverBean.setTo(bean.getTo());
						serverBean.setFileName(bean.getFileName());
						serverBean.setInfo(bean.getInfo());
						serverBean.setName(bean.getName());
						serverBean.setTimer(bean.getTimer());
						sendMessage(serverBean);

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
				close();
			}
		}
		private void sendMessage(Bean serverBean) {
			// get the online clients
			Set<String> cbs = onlines.keySet();
			Iterator<String> it = cbs.iterator();
			HashSet<String> clients = serverBean.getClients();
			while (it.hasNext()) {
				String client = it.next();
				if (clients.contains(client)) {
					Socket c = onlines.get(client).getSocket();
					ObjectOutputStream oos;
					try {
						oos = new ObjectOutputStream(c.getOutputStream());
						oos.writeObject(serverBean);
						oos.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}

		public void sendAll(Bean serverBean) {
			Collection<ClientBean> clients = onlines.values();
			Iterator<ClientBean> it = clients.iterator();
			ObjectOutputStream oos;
			while (it.hasNext()) {
				Socket c = it.next().getSocket();
				try {
					oos = new ObjectOutputStream(c.getOutputStream());
					oos.writeObject(serverBean);
					oos.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		private void close() {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void start() {
		try {
			while (true) {
				Socket client = ss.accept();
				new CatClientThread(client).start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		new Server().start();
	}

}
