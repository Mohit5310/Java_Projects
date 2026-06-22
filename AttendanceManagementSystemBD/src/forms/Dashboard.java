package forms;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import utility.BDUtility;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Dashboard extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Dashboard frame = new Dashboard();
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
	public Dashboard() {
		setUndecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1366, 768);
		setLocationRelativeTo(null);
		
		contentPane = new JPanel();
		contentPane.setFont(new Font("Tahoma", Font.BOLD, 14));
		contentPane.setMinimumSize(new Dimension(1366, 768));
		contentPane.setMaximumSize(new Dimension(1366, 768));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		this.getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.BLACK));
		
		// All buttons are added to the content pane first
		JButton btnExit = new JButton("X");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnExit.setBounds(1309, 18, 47, 38);
		btnExit.setForeground(new Color(255, 255, 255));
		btnExit.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnExit.setBackground(new Color(128, 0, 0));
		btnExit.setAlignmentX(0.5f);
		contentPane.add(btnExit);
		
		JButton btnRegisterUser = new JButton("Register User");
		btnRegisterUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BDUtility.openForm(UserRegistration.class.getSimpleName(), new UserRegistration());
			}
		});
		btnRegisterUser.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnRegisterUser.setBounds(10, 20, 131, 32);
		contentPane.add(btnRegisterUser);
		
		JButton btnViewUser = new JButton("View User");
		btnViewUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BDUtility.openForm(ViewUser.class.getSimpleName(), new ViewUser());
			}
		});
		btnViewUser.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnViewUser.setBounds(165, 20, 131, 32);
		contentPane.add(btnViewUser);
		
		JButton btnUpdateUser = new JButton("Update User");
		btnUpdateUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BDUtility.openForm(UpdateUser.class.getSimpleName(), new UpdateUser());
			}
		});
		btnUpdateUser.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnUpdateUser.setBounds(322, 20, 131, 32);
		contentPane.add(btnUpdateUser);
		
		JButton btnDeleteUser = new JButton("Delete User");
		btnDeleteUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BDUtility.openForm(DeleteUser.class.getSimpleName(), new DeleteUser());
			}
		});
		btnDeleteUser.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnDeleteUser.setBounds(478, 20, 131, 32);
		contentPane.add(btnDeleteUser);
		
		JButton btnGenerateQr = new JButton("Generate Qr");
		btnGenerateQr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BDUtility.openForm(GenerateQr.class.getSimpleName(), new GenerateQr());
			}
		});
		btnGenerateQr.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnGenerateQr.setBounds(632, 20, 131, 32);
		contentPane.add(btnGenerateQr);
		
		JButton btnViewQrs = new JButton("View Qr's");
		btnViewQrs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BDUtility.openForm(ViewQrs.class.getSimpleName(),  new ViewQrs());
			}
		});
		btnViewQrs.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnViewQrs.setBounds(787, 20, 131, 32);
		contentPane.add(btnViewQrs);
		
		JButton btnMarkAttendance = new JButton("Mark Attendance ");
		btnMarkAttendance.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BDUtility.openForm(MarkAttendance.class.getSimpleName(), new MarkAttendance());
			}
		});
		btnMarkAttendance.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnMarkAttendance.setBounds(943, 20, 160, 32);
		contentPane.add(btnMarkAttendance);
		
		JButton btnViewAttendance = new JButton("View Attendance ");
		btnViewAttendance.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BDUtility.openForm(ViewAttendance.class.getSimpleName(),new ViewAttendance());
			}
		});
		btnViewAttendance.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnViewAttendance.setBounds(1125, 20, 165, 32);
		contentPane.add(btnViewAttendance);
		
		// The BDUtility.setImage() method is called last to ensure the background is set
		// after all components are added.
		BDUtility.setImage(this, "images/abc1.jpg", 1366, 768); 
	}
}