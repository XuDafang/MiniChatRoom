package chat.login;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import chat.client.Chatroom;
import chat.function.ClientBean;
import chat.util.Util;

public class Login extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField;
	public static HashMap<String, ClientBean> onlines;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
	    //NOTE1
		EventQueue.invokeLater(() -> {
            try {
                // ������½����
                Login frame = new Login();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
	}
	/**
	 * Create the frame.
	 */
	public Login() throws IOException {
		setTitle("Landing cat chat room\n");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//�û�����رհ�ťʱ�رմ���
		setBounds(350, 250, 450, 300);//���㴰�ڵ�λ�ã�x,y)�Լ���С
		//JPanel in NOTE2
		contentPane = new JPanel() {//JPanel �м�����
			private static final long serialVersionUID = 1L;
			//Graphics NOTE3
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);//super ����JFrame
                try {
                    g.drawImage(new ImageIcon(ImageIO.read(new File("images\\registerBackground.jpg"))).getImage(), 0,
                            0, getWidth(), getHeight(), null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
 	           }
		};

		//EmptyBorder NOTE4
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		//The class JTextField is a component which allows the editing of a single line of text.
        //��¼���棬�س������
        textField = new JTextField();
		textField.setBounds(128, 153, 104, 21);//x,y�����JFrame
		textField.setOpaque(false);
		contentPane.add(textField);
		//Sets the number of columns in this TextField, and then invalidate the layout.
		textField.setColumns(10);

        //��¼���棬���������
		passwordField = new JPasswordField();
		passwordField.setForeground(Color.black);
		passwordField.setEchoChar('*');
		passwordField.setOpaque(false);
		passwordField.setBounds(128, 189, 104, 21);
		contentPane.add(passwordField);


		final JButton btnLogin = new JButton();
		btnLogin.setIcon(new ImageIcon(ImageIO.read(new File("images\\login.jpg"))));
		btnLogin.setBounds(246, 227, 50, 25);
		getRootPane().setDefaultButton(btnLogin);
		contentPane.add(btnLogin);

		final JButton btnRegister = new JButton();
		btnRegister.setIcon(new ImageIcon(ImageIO.read(new File("images\\register.jpg"))));
		btnRegister.setBounds(317, 227, 50, 25);
		contentPane.add(btnRegister);

		// ��ʾ��Ϣ���������벻�ԣ��û�������
		final JLabel alertLabel = new JLabel();
		alertLabel.setBounds(60, 220, 151, 21);
		alertLabel.setForeground(Color.red);
		getContentPane().add(alertLabel);

		btnLogin.addActionListener(e -> {
			//properties NOTE5
            Properties userPro = new Properties();
            File file = new File("Users.properties");
            Util.loadPro(userPro, file);
            String u_name = textField.getText();
            if (file.length() != 0) {
                if (userPro.containsKey(u_name)) {
                    String u_pwd = new String(passwordField.getPassword());
                    if (u_pwd.equals(userPro.getProperty(u_name))) {
                        try {
                        	//socket NOTE6
                            Socket client = new Socket("localhost", 8520);
                            btnLogin.setEnabled(false);//���õ�¼��ť
                            Chatroom frame = new Chatroom(u_name, client);
                            frame.setVisible(true);// ��ʾ�������
                            setVisible(false);// ���ص���½����
                        } catch (UnknownHostException e1) {
                            // TODO Auto-generated catch block
                            errorTip("The connection with the server is interrupted, please login again");
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            errorTip("The connection with the server is interrupted, please login again");
                        }

                    } else {
                        alertLabel.setText("���������������");
                        textField.setText("");
                        passwordField.setText("");
                        textField.requestFocus();
                    }
                } else {
                    alertLabel.setText("�������ǳƲ����ڣ�");
                    textField.setText("");
                    passwordField.setText("");
                    textField.requestFocus();
                }
            } else {
                alertLabel.setText("�������ǳƲ����ڣ�");
                textField.setText("");
                passwordField.setText("");
                textField.requestFocus();
            }
        });

		//ע�ᰴť����
		btnRegister.addActionListener(e -> {
            btnRegister.setEnabled(false);
            Register frame = new Register();
            frame.setVisible(true);// ��ʾע�����
            setVisible(false);// ���ص���½����
        });
	}

	protected void errorTip(String str) {
		// TODO Auto-generated method stub
		JOptionPane.showMessageDialog(contentPane, str, "Error Message",
				JOptionPane.ERROR_MESSAGE);
		textField.setText("");
		passwordField.setText("");
		textField.requestFocus();
	}
}