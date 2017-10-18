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
                // 启动登陆界面
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
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//用户点击关闭按钮时关闭窗口
		setBounds(350, 250, 450, 300);//顶层窗口的位置（x,y)以及大小
		//JPanel in NOTE2
		contentPane = new JPanel() {//JPanel 中间容器
			private static final long serialVersionUID = 1L;
			//Graphics NOTE3
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);//super 代表JFrame
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
        //登录界面，呢称输入框
        textField = new JTextField();
		textField.setBounds(128, 153, 104, 21);//x,y相对于JFrame
		textField.setOpaque(false);
		contentPane.add(textField);
		//Sets the number of columns in this TextField, and then invalidate the layout.
		textField.setColumns(10);

        //登录界面，密码输入框
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

		// 提示信息，比如密码不对，用户不存在
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
                            btnLogin.setEnabled(false);//禁用登录按钮
                            Chatroom frame = new Chatroom(u_name, client);
                            frame.setVisible(true);// 显示聊天界面
                            setVisible(false);// 隐藏掉登陆界面
                        } catch (UnknownHostException e1) {
                            // TODO Auto-generated catch block
                            errorTip("The connection with the server is interrupted, please login again");
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            errorTip("The connection with the server is interrupted, please login again");
                        }

                    } else {
                        alertLabel.setText("您输入的密码有误！");
                        textField.setText("");
                        passwordField.setText("");
                        textField.requestFocus();
                    }
                } else {
                    alertLabel.setText("您输入昵称不存在！");
                    textField.setText("");
                    passwordField.setText("");
                    textField.requestFocus();
                }
            } else {
                alertLabel.setText("您输入昵称不存在！");
                textField.setText("");
                passwordField.setText("");
                textField.requestFocus();
            }
        });

		//注册按钮监听
		btnRegister.addActionListener(e -> {
            btnRegister.setEnabled(false);
            Register frame = new Register();
            frame.setVisible(true);// 显示注册界面
            setVisible(false);// 隐藏掉登陆界面
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