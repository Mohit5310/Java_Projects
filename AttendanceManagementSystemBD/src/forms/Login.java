package forms;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.Color;
import java.awt.Dimension;
import utility.BDUtility;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

public class Login extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtEmail;
	private JPasswordField txtPassword;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Login() {
		setUndecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(635, 506);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setForeground(new Color(255, 255, 255));
		contentPane.setFont(new Font("Tahoma", Font.BOLD, 12));
		contentPane.setAlignmentX(0.0f);
		contentPane.setMinimumSize(new Dimension(635, 506));
		contentPane.setMaximumSize(new Dimension(635, 506));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JButton btnExit = new JButton("X");
		btnExit.setForeground(new Color(255, 255, 255));
		btnExit.setBackground(new Color(128, 0, 0));
		btnExit.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnExit.setBounds(582, 11, 43, 36);
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		contentPane.setLayout(null);
		btnExit.setAlignmentX(0.5f);
		contentPane.add(btnExit);
		
		JLabel lblNewLabel = new JLabel("Attendance ");
		lblNewLabel.setForeground(new Color(255, 255, 255));
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 48));
		lblNewLabel.setBounds(175, 11, 310, 58);
		contentPane.add(lblNewLabel);
		
		JLabel lblManagementSystem = new JLabel("Management System");
		lblManagementSystem.setForeground(new Color(255, 255, 255));
		lblManagementSystem.setFont(new Font("Tahoma", Font.BOLD, 48));
		lblManagementSystem.setBounds(72, 80, 553, 58);
		contentPane.add(lblManagementSystem);
		
		JLabel lblNewLabel_1 = new JLabel("Email");
		lblNewLabel_1.setForeground(new Color(255, 255, 255));
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_1.setBounds(175, 241, 67, 14);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_1_1 = new JLabel("Password");
		lblNewLabel_1_1.setForeground(new Color(255, 255, 255));
		lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_1_1.setBounds(175, 281, 67, 14);
		contentPane.add(lblNewLabel_1_1);
		
		txtEmail = new JTextField();
		txtEmail.setFont(new Font("Tahoma", Font.BOLD, 14));
		txtEmail.setBounds(266, 241, 193, 20);
		contentPane.add(txtEmail);
		txtEmail.setColumns(10);
		
		txtPassword = new JPasswordField();
		txtPassword.setFont(new Font("Tahoma", Font.BOLD, 14));
		txtPassword.setBounds(266, 281, 193, 20);
		contentPane.add(txtPassword);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.setForeground(new Color(0, 0, 0));
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String email =txtEmail.getText().toString();
				String password = new String(txtPassword.getPassword());
				if("prasarmohit@gmail.com".equalsIgnoreCase(email) && "Mohit@5310".equalsIgnoreCase(password)) {
					Login.this.dispose();
					BDUtility.openForm(Dashboard.class.getSimpleName (), new Dashboard() );
				}else {
					JOptionPane.showMessageDialog(null,  "Invalid Credentials.", "Invalid", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnLogin.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnLogin.setBounds(360, 332, 99, 23);
		contentPane.add(btnLogin);
		
		BDUtility.setImage(this, "images/login-background.jpg",  635, 506);
		this.getRootPane().setBorder(BorderFactory.createMatteBorder(6,  6,  6,  6, Color.ORANGE));
		txtEmail.setBorder(BorderFactory.createMatteBorder(1,  1,  1,  1, Color.blue));
		txtPassword.setBorder(BorderFactory.createMatteBorder(1,  1,  1,  1, Color.blue));
		

	}

}
