package chat.login;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import chat.util.Util;

public class Register extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField;
	private JPasswordField passwordField_1;
	private JLabel alertLabel;

	public Register() {
		setTitle("Registered cat chat room\n");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(350, 250, 450, 300);
		contentPane = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(new ImageIcon("images\\registerPage.jpg").getImage(), 0,0, getWidth(), getHeight(), null);
			}
		};
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		//昵称输入框
		textField = new JTextField();
		textField.setBounds(150, 42, 104, 21);
		textField.setOpaque(false);
		contentPane.add(textField);
		textField.setColumns(10);


		passwordField = new JPasswordField();
		passwordField.setEchoChar('*');
		passwordField.setOpaque(false);
		passwordField.setBounds(190, 98, 104, 21);
		contentPane.add(passwordField);

		passwordField_1 = new JPasswordField();
		passwordField_1.setBounds(192, 152, 104, 21);
		passwordField_1.setOpaque(false);
		contentPane.add(passwordField_1);

		//注册按钮
		final JButton btnRegister = new JButton();
		btnRegister.setIcon(new ImageIcon("images\\register1.jpg"));
		btnRegister.setBounds(320, 198, 80, 40);
		getRootPane().setDefaultButton(btnRegister);
		contentPane.add(btnRegister);

		//返回按钮
		final JButton btnLogin = new JButton("");
		btnLogin.setIcon(new ImageIcon("images\\back.jpg"));
		btnLogin.setBounds(230, 198, 70, 40);
		contentPane.add(btnLogin);

		//提示信息
		alertLabel = new JLabel();
		alertLabel.setBounds(55, 218, 185, 20);
		alertLabel.setForeground(Color.red);
		contentPane.add(alertLabel);
		
		//返回按钮监听，点击时触发
		btnLogin.addActionListener(e -> {
            btnLogin.setEnabled(false);
            //返回登陆界面
            Login frame = null;
            try {
                frame = new Login();
            } catch (IOException er) {
                er.printStackTrace();
            }
            frame.setVisible(true);
            setVisible(false);
        });
		
		//注册按钮监听
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Properties userPro = new Properties();
				File file = new File("Users.properties");
				Util.loadPro(userPro, file);
				
				String u_name = textField.getText();
				String u_pwd = new String(passwordField.getPassword());
				String u_pwd_ag = new String(passwordField_1.getPassword());

				// 判断用户名是否在普通用户中已存在
				if (u_name.length() != 0) {
					if (userPro.containsKey(u_name)) {
						alertLabel.setText("用户名已存在!");
					} else {
						isPassword(userPro, file, u_name, u_pwd, u_pwd_ag);
					}
				} else {
					alertLabel.setText("用户名不能为空！");
				}
			}

			private void isPassword(Properties userPro,
                File file, String u_name, String u_pwd, String u_pwd_ag) {
                if (u_pwd.equals(u_pwd_ag)) {
                    if (u_pwd.length() != 0) {
                        userPro.setProperty(u_name, u_pwd_ag);
                        try {
                            userPro.store(new FileOutputStream(file),
                                    "Copyright (c) Boxcode Studio");
                        } catch (FileNotFoundException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        btnRegister.setEnabled(false);
                        //返回登陆界面
                        Login frame = null;
                        try {
                            frame = new Login();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        frame.setVisible(true);
                        setVisible(false);
                    } else {
                        alertLabel.setText("密码为空！");
                    }
                } else {
                    alertLabel.setText("密码不一致！");
                }
			}
		});
	}
}
