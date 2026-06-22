package forms;

import java.awt.EventQueue;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import dao.ConnectionProvider;

import javax.swing.ButtonGroup;

import utility.BDUtility;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserRegistration extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtName;
    private JTextField txtEmail;
    private JTextField txtContact;
    private JTextField txtAddress;
    private JTextField txtState;
    private JTextField txtCountry;
    private JRadioButton radioMale;
    private JRadioButton radioFemale;
    private ButtonGroup genderGroup;
    private JLabel lblImage; 
    private BufferedImage originalImage = null; 
    private File selectedFile = null; 

    private void clearForm() {
    	txtName.setText("");
    	txtEmail.setText("");
    	txtContact.setText("");
    	txtAddress.setText("");
    	txtState.setText("");
    	txtCountry.setText("");
    	genderGroup.clearSelection();
    	lblImage.setIcon(null); 
    }
    
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UserRegistration frame = new UserRegistration();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public UserRegistration() {
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 600);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setFont(new Font("Tahoma", Font.PLAIN, 14));
        contentPane.setMinimumSize(new Dimension(850, 600));
        contentPane.setMaximumSize(new Dimension(850, 600));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        setContentPane(contentPane);
        this.getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.BLACK));

        JButton btnNewButton = new JButton("X");
        btnNewButton.setForeground(new Color(255, 255, 255));
        btnNewButton.setBackground(new Color(128, 0, 0));
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                UserRegistration.this.dispose();
            }
        });
        btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 18));
        btnNewButton.setBounds(793, 11, 47, 35);
        contentPane.add(btnNewButton);

        JLabel lblNewLabel = new JLabel("User Registration");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblNewLabel.setBounds(322, 11, 197, 35);
        contentPane.add(lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("Name");
        lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblNewLabel_1.setBounds(87, 115, 35, 14);
        contentPane.add(lblNewLabel_1);

        txtName = new JTextField();
        txtName.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txtName.setBounds(143, 110, 226, 20);
        contentPane.add(txtName);
        txtName.setColumns(10);

        JLabel lblNewLabel_1_1 = new JLabel("Gender");
        lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblNewLabel_1_1.setBounds(78, 160, 44, 14);
        contentPane.add(lblNewLabel_1_1);

        radioMale = new JRadioButton("Male");
        radioFemale = new JRadioButton("Female");
        
        genderGroup = new ButtonGroup();
        genderGroup.add(radioMale);
        genderGroup.add(radioFemale);

        radioMale.setFont(new Font("Tahoma", Font.PLAIN, 14));
        radioMale.setBounds(143, 158, 109, 18);
        contentPane.add(radioMale);

        radioFemale.setFont(new Font("Tahoma", Font.PLAIN, 14));
        radioFemale.setBounds(260, 158, 109, 18);
        contentPane.add(radioFemale);
        
        JLabel lblNewLabel_1_2 = new JLabel("Email");
        lblNewLabel_1_2.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblNewLabel_1_2.setBounds(87, 206, 35, 14);
        contentPane.add(lblNewLabel_1_2);

        txtEmail = new JTextField();
        txtEmail.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txtEmail.setColumns(10);
        txtEmail.setBounds(143, 201, 226, 20);
        contentPane.add(txtEmail);

        JLabel lblNewLabel_1_3 = new JLabel("Contact");
        lblNewLabel_1_3.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblNewLabel_1_3.setBounds(73, 251, 49, 14);
        contentPane.add(lblNewLabel_1_3);

        txtContact = new JTextField();
        txtContact.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txtContact.setColumns(10);
        txtContact.setBounds(143, 248, 226, 20);
        contentPane.add(txtContact);

        JLabel lblNewLabel_1_4 = new JLabel("Address");
        lblNewLabel_1_4.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblNewLabel_1_4.setBounds(73, 299, 49, 14);
        contentPane.add(lblNewLabel_1_4);

        txtAddress = new JTextField();
        txtAddress.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txtAddress.setColumns(10);
        txtAddress.setBounds(143, 296, 226, 20);
        contentPane.add(txtAddress);

        JLabel lblNewLabel_1_5 = new JLabel("State");
        lblNewLabel_1_5.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblNewLabel_1_5.setBounds(87, 350, 35, 14);
        contentPane.add(lblNewLabel_1_5);

        txtState = new JTextField();
        txtState.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txtState.setColumns(10);
        txtState.setBounds(143, 345, 226, 20);
        contentPane.add(txtState);

        JLabel lblNewLabel_1_6 = new JLabel("Country");
        lblNewLabel_1_6.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblNewLabel_1_6.setBounds(71, 393, 51, 20);
        contentPane.add(lblNewLabel_1_6);

        txtCountry = new JTextField();
        txtCountry.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txtCountry.setColumns(10);
        txtCountry.setBounds(143, 393, 226, 20);
        contentPane.add(txtCountry);

        JButton btnRegister = new JButton("Register");
        btnRegister.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
                Connection connection = null;
        		try {
        			String name = txtName.getText();
        			String gender = "";
        			if(radioMale.isSelected()) {
        				gender = "Male";
        			}else if (radioFemale.isSelected()) {
        				gender = "Female";
        			}
        			String email = txtEmail.getText();
        			String emailRegex = "^[A-Za-z0-9_.-]+@[A-Za-z0-9.-]+$";
        			if(!email.matches(emailRegex)) {
        				JOptionPane.showMessageDialog(null, "Invalid Email.","Invalid",JOptionPane.ERROR_MESSAGE);
        				return;
        			}
        			
        			String contact = txtContact.getText();
        			String contactRegex = "^\\d{10}$";
        			if(!contact.matches(contactRegex)) {
        				JOptionPane.showMessageDialog(null, "Invalid contact number", "Invalid", JOptionPane.ERROR_MESSAGE);
        				return;
        			}
        			
        			String address = txtAddress.getText();
        			String state = txtState.getText();
        			String country = txtCountry.getText();
        			String uniqueRegId = "" + System.nanoTime();
        			if(name.isEmpty() || email.isEmpty() || contact.isEmpty() || address.isEmpty() || state.isEmpty() || country.isEmpty() || gender.isEmpty()) {
        				JOptionPane.showMessageDialog(null, "One or more fields are empty.", "Field Empty", JOptionPane.WARNING_MESSAGE);
        				return;
        			}
        			
        			connection = ConnectionProvider.getCon();
                    if(connection == null) {
                        JOptionPane.showMessageDialog(null, "Failed to connect to the database. Check console for details.", "Connection Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

        			// Use a try-with-resources statement to ensure resources are closed
                    try (Statement st = connection.createStatement()) {
                        ResultSet rs = st.executeQuery("select * from userdetails where email = '" + email + "'");
                        if (rs.next()) {
                            JOptionPane.showMessageDialog(null, "Duplicate email.", "Duplicate", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }

        			String imageName = saveImage(email);
                    if (imageName == null) {
                        JOptionPane.showMessageDialog(null, "Failed to save image.", "Image Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
        			
        			String insertQuery = "INSERT into userdetails (name, gender, email, contact, address, state, country, uniqueregid, imagename) VALUES(?,?,?,?,?,?,?,?,?)";
        			try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                        preparedStatement.setString(1, name);
                        preparedStatement.setString(2, gender);
                        preparedStatement.setString(3, email);
                        preparedStatement.setString(4, contact);
                        preparedStatement.setString(5, address);
                        preparedStatement.setString(6, state);
                        preparedStatement.setString(7, country);
                        preparedStatement.setString(8, uniqueRegId);
                        preparedStatement.setString(9, imageName);

                        preparedStatement.executeUpdate();
                        JOptionPane.showMessageDialog(null, "User Registration Successfully");
                        clearForm();
                    }
        		} catch (Exception ex) {
        			ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "An error occurred during registration. Check console for details.", "Error", JOptionPane.ERROR_MESSAGE);
        		} finally {
                    try {
                        if (connection != null) {
                            connection.close();
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
        	}

        	private String saveImage(String email) {
        		if(originalImage != null && selectedFile != null) {
        			try {
        				String savePath = BDUtility.getPath("images\\");
        				String extension = BDUtility.getFileExtension(selectedFile.getName());
        				String imageName = email + "." + extension;
        				File saveFile = new File(savePath + imageName);

        				// The crucial line to write the image to the file system
                        BufferedImage scaledImage = BDUtility.scaleImage(originalImage, ImageIO.read(selectedFile)); // Use the correct scaling logic
                        ImageIO.write(scaledImage, extension, saveFile);
                        
                        return imageName;
        			}catch(Exception ex) {
        				ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Failed to save image: " + ex.getMessage(), "Image Save Error", JOptionPane.ERROR_MESSAGE);
        			}
        		}
        		return null;
        	}
        });
        btnRegister.setFont(new Font("Tahoma", Font.PLAIN, 14));
        btnRegister.setBounds(437, 466, 174, 35);
        contentPane.add(btnRegister);

        JButton btnClear = new JButton("Clear");
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	clearForm();
            }
        });
        btnClear.setFont(new Font("Tahoma", Font.PLAIN, 14));
        btnClear.setBounds(621, 466, 172, 35);
        contentPane.add(btnClear);
        
        lblImage = new JLabel("");
        lblImage.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(UserRegistration.this); 
                if(result == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                    try {
                        originalImage = ImageIO.read(selectedFile);
                        
                        int originalWidth = originalImage.getWidth();
                        int originalHeight = originalImage.getHeight();
                        
                        int labelWidth = lblImage.getWidth();
                        int labelHeight = lblImage.getHeight();
                        
                        double scaleX = (double) labelWidth / originalWidth;
                        double scaleY = (double) labelHeight / originalHeight;
                        
                        double scale = Math.min(scaleX,  scaleY);
                        
                        int scaledWidth = (int) (originalWidth * scale);
                        int scaledHeight = (int) (originalHeight * scale);
                        
                        Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                        
                        ImageIcon icon = new ImageIcon(scaledImage);
                        lblImage.setIcon(icon);
                    }catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Failed to load image: " + ex.getMessage(), "Image Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        lblImage.setBounds(447, 141, 335, 266);
        contentPane.add(lblImage);
        
        JInternalFrame internalFrame = new JInternalFrame("");
        internalFrame.setBounds(437, 110, 356, 303);
        contentPane.add(internalFrame);
        internalFrame.setVisible(true);

        BDUtility.setImage(this, "images/abc1.jpg", 850, 600);
    }
}