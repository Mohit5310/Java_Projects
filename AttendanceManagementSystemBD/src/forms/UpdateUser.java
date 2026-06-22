package forms;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import utility.BDUtility;

import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Image;
import java.awt.Graphics2D; // Needed for drawing to BufferedImage

import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import dao.ConnectionProvider;

import javax.swing.JRadioButton;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JInternalFrame;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.Objects;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class UpdateUser extends JFrame {

	private static final long serialVersionUID = 1L;
//	private JPanel contentPane;
	private JTextField txtName;
	private JTextField txtEmail;
	private JTextField txtContact;
	private JTextField txtAddress;
	private JTextField txtState;
	private JTextField txtCountry;
	private String uniqueReg;
	private String existingImageName = null;
	private BufferedImage originalImage = null;
	private File selectedFile = null;
    
    // FIX: Instance variables declared here for accessibility across methods/listeners
    private JLabel lblImage;
    private JRadioButton radioMale;
    private JRadioButton radioFemale;
    private ButtonGroup genderGroup; 
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UpdateUser frame = new UpdateUser();
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
	public UpdateUser() {
		setUndecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(860, 538);
		setLocationRelativeTo(null);
		setMinimumSize(new Dimension(860, 538));
		setMaximumSize(new Dimension(860, 538));
		
		 // Step 1: Call BDUtility (it adds JLabel as background to the frame)
        BDUtility.setImage(this, "images/abc1.jpg", 860, 538);

        // Step 2: Get that background JLabel
        Component bg = getContentPane().getComponent(0);

        // Step 3: Create a layeredPane and put background at bottom layer
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 860, 538);
        setContentPane(layeredPane);

        layeredPane.add(bg, Integer.valueOf(0)); // background at layer 0

        // Step 4: Transparent content panel for UI components
        JPanel contentPane = new JPanel();
        contentPane.setOpaque(false);
        contentPane.setBounds(0, 0, 860, 538);
        contentPane.setLayout(null);
        layeredPane.add(contentPane, Integer.valueOf(1)); // above background

        this.getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.BLACK));
		
		JLabel lblNewLabel = new JLabel("Update User");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNewLabel.setBounds(374, 11, 119, 35);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Name");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_1.setBounds(92, 182, 35, 14);
		contentPane.add(lblNewLabel_1);
		
		txtName = new JTextField();
		txtName.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtName.setColumns(10);
		txtName.setBounds(148, 177, 226, 20);
		contentPane.add(txtName);
		
		JLabel lblNewLabel_1_1 = new JLabel("Gender");
		lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_1_1.setBounds(83, 221, 44, 14);
		contentPane.add(lblNewLabel_1_1);
		
		// Initialize the instance variables here
		radioMale = new JRadioButton("Male");
		radioMale.setFont(new Font("Tahoma", Font.PLAIN, 14));
		radioMale.setBounds(148, 219, 109, 18);
		contentPane.add(radioMale);
		
		radioFemale = new JRadioButton("Female");
		radioFemale.setFont(new Font("Tahoma", Font.PLAIN, 14));
		radioFemale.setBounds(265, 219, 109, 18);
		contentPane.add(radioFemale);
		
		// Initialize the ButtonGroup instance variable
		genderGroup = new ButtonGroup();
		genderGroup.add(radioMale);
		genderGroup.add(radioFemale);

		
		JLabel lblNewLabel_1_2 = new JLabel("Email");
		lblNewLabel_1_2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_1_2.setBounds(92, 115, 35, 14);
		contentPane.add(lblNewLabel_1_2);
		
		txtEmail = new JTextField();
		txtEmail.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtEmail.setColumns(10);
		txtEmail.setBounds(148, 110, 226, 20);
		contentPane.add(txtEmail);
		
		JLabel lblNewLabel_1_3 = new JLabel("Contact");
		lblNewLabel_1_3.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_1_3.setBounds(78, 262, 49, 14);
		contentPane.add(lblNewLabel_1_3);
		
		txtContact = new JTextField();
		txtContact.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtContact.setColumns(10);
		txtContact.setBounds(148, 259, 226, 20);
		contentPane.add(txtContact);
		
		JLabel lblNewLabel_1_4 = new JLabel("Address");
		lblNewLabel_1_4.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_1_4.setBounds(78, 310, 49, 14);
		contentPane.add(lblNewLabel_1_4);
		
		txtAddress = new JTextField();
		txtAddress.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtAddress.setColumns(10);
		txtAddress.setBounds(148, 307, 226, 20);
		contentPane.add(txtAddress);
		
		JLabel lblNewLabel_1_5 = new JLabel("State");
		lblNewLabel_1_5.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_1_5.setBounds(92, 361, 35, 14);
		contentPane.add(lblNewLabel_1_5);
		
		txtState = new JTextField();
		txtState.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtState.setColumns(10);
		txtState.setBounds(148, 356, 226, 20);
		contentPane.add(txtState);
		
		JLabel lblNewLabel_1_6 = new JLabel("Country");
		lblNewLabel_1_6.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_1_6.setBounds(76, 404, 51, 20);
		contentPane.add(lblNewLabel_1_6);
		
		txtCountry = new JTextField();
		txtCountry.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtCountry.setColumns(10);
		txtCountry.setBounds(148, 404, 226, 20);
		contentPane.add(txtCountry);
		
		JButton btnNewButton = new JButton("X");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 UpdateUser.this.dispose();
			}
		});
		btnNewButton.setForeground(Color.WHITE);
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 18));
		btnNewButton.setBackground(new Color(128, 0, 0));
		btnNewButton.setBounds(803, 11, 47, 35);
		contentPane.add(btnNewButton);
		
		JButton btnUpdate = new JButton("Update");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String name = txtName.getText().toString();
					String gender = "";
					if (radioMale.isSelected()) {
						gender = "Male";
					}else if (radioFemale.isSelected()) {
						gender = "Female";
					}
					String email = txtEmail.getText().toString();
	    			String emailRegex = "^[A-Za-z0-9_.-]+@[A-Za-z0-9.-]+$";
	    			if(!email.matches(emailRegex)) {
	    				JOptionPane.showMessageDialog(UpdateUser.this, "Invalid Email.","Invalid",JOptionPane.ERROR_MESSAGE);
	    				return;
	    			}
					String contact = txtContact.getText().toString();
	    			String contactRegex = "^\\d{10}$";
	    			if(!contact.matches(contactRegex)) {
	    				JOptionPane.showMessageDialog(UpdateUser.this, "Invalid contact number.", "invalid", JOptionPane.ERROR_MESSAGE);
	    				return;
	    			}
	    			
	    			String address = txtAddress.getText().toString();
	    			String state = txtState.getText().toString();
	    			String country = txtCountry.getText().toString();
	    			
	    			if(name.isEmpty() || email.isEmpty() || contact.isEmpty() || address.isEmpty() || state.isEmpty() || country.isEmpty() || gender.isEmpty()) {
	    				JOptionPane.showMessageDialog(UpdateUser.this, "One or more fields are empty.","Find Empty",JOptionPane.WARNING_MESSAGE);
	    				return;
	    			}
	    			
	    			Connection connection = ConnectionProvider.getCon();
	    			
	    			try {
	    				Statement st = connection.createStatement();
	    				ResultSet rs = st.executeQuery("select * from userdetails where email = '" + email + "'");
	    				if (!rs.next()) {
	    					JOptionPane.showMessageDialog(UpdateUser.this, "Email not found", "Not Found", JOptionPane.INFORMATION_MESSAGE);
	    					return;
	    				}
	    			} catch (Exception ex) {
	    				JOptionPane.showMessageDialog(UpdateUser.this, ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                        return; 
	    			}
	    			
	    			String imageName = saveImage(email);
	    			String updateQuery;
	    			
	    			PreparedStatement preparedStatement = null; 
	    			
	    			if (imageName != null) {
	    				updateQuery = "UPDATE userdetails SET name=?, gender=?, contact=?, address=?, state=?, country=?, imagename=? where uniqueregid=?";
	    			} else {
	    				updateQuery = "UPDATE userdetails SET name=?, gender=?, contact=?, address=?, state=?, country=? where uniqueregid=?";
	    			}
	    			
	    			preparedStatement = connection.prepareStatement(updateQuery);
	    			
	    			preparedStatement.setString(1, name);
	    			preparedStatement.setString(2, gender);
	    			preparedStatement.setString(3, contact);
	    			preparedStatement.setString(4, address);
	    			preparedStatement.setString(5, state);
	    			preparedStatement.setString(6, country);
	    			
	    			if (imageName != null) {
	    			    preparedStatement.setString(7, imageName);
	    			    preparedStatement.setString(8, uniqueReg);
	    			} else {
	    				preparedStatement.setString(7, uniqueReg);
	    			}
	    			
	    			preparedStatement.executeUpdate();
	    			JOptionPane.showMessageDialog(UpdateUser.this, "User Updated Successfully.", "Confirmation", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Call the clearForm method here
	    			clearForm(); 
				} catch (Exception ex) {
					ex.printStackTrace();
                    JOptionPane.showMessageDialog(UpdateUser.this, ex.getMessage(), "Update Error", JOptionPane.ERROR_MESSAGE);
				}
			}
            
            // NOTE: The clearForm and saveImage methods are now defined outside this listener.
		});
		btnUpdate.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnUpdate.setBounds(447, 466, 174, 35);
		contentPane.add(btnUpdate);
		
		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Call the instance method on the outer class instance
				UpdateUser.this.clearForm(); 
			}
		});
		btnClear.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnClear.setBounds(631, 466, 172, 35);
		contentPane.add(btnClear);
		
		JInternalFrame internalFrame = new JInternalFrame("");
		internalFrame.setBounds(447, 110, 356, 314);
		contentPane.add(internalFrame);
		
		// Initialize the instance variable here
		lblImage = new JLabel("");
		lblImage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				 JDialog dialog = new JDialog();
				 dialog.setUndecorated(true);
				 dialog.setSize(600, 400);
				 JFileChooser fileChooser = new JFileChooser();
				 FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG Images", "jpg");
				 fileChooser.setFileFilter(filter);
				 fileChooser.addActionListener(new ActionListener () {
					 public void actionPerformed(ActionEvent e) {
						 if (e.getActionCommand() .equals (JFileChooser.APPROVE_SELECTION)) {
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
							 		
							 		Image scaledImage = originalImage.getScaledInstance(scaledWidth,scaledHeight,Image.SCALE_SMOOTH);
							 		ImageIcon icon = new ImageIcon(scaledImage);
							 		lblImage.setIcon(icon); 
							 		
							 	} catch (IOException ex) {
							 		ex.printStackTrace();
							 	}
						 }
						 dialog.dispose();
					 }
				 });
				 dialog.getContentPane().add(fileChooser);
				 dialog.setLocationRelativeTo(UpdateUser.this); 
				 dialog.setVisible(true);
			}
		});
		internalFrame.getContentPane().add(lblImage, BorderLayout.CENTER);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String email = txtEmail.getText().toString();
    			String emailRegex = "^[A-Za-z0-9_.-]+@[A-Za-z0-9.-]+$";
    			if(!email.matches(emailRegex)) {
    				JOptionPane.showMessageDialog(UpdateUser.this, "Invalid Email.","Invalid",JOptionPane.ERROR_MESSAGE);
    				return;
    			}
    			
    			try {
    				Connection con = ConnectionProvider.getCon();
    				Statement st = con.createStatement();
    				ResultSet rs = st.executeQuery("select * from userdetails where email='"+email+"'");
    				if (rs.next()) {
    					txtName.setText(rs.getString("name"));
    					if (rs.getString("gender").equalsIgnoreCase("Male")) {
    						radioMale.setSelected(true);
    						radioFemale.setSelected(false);
    					} else {
    						radioFemale.setSelected(true);
    						radioMale.setSelected(false);
    					}
    					txtContact.setText(rs.getString("contact"));
    					txtAddress.setText(rs.getString("address"));
    					txtState.setText(rs.getString("state"));
    					txtCountry.setText(rs.getString("country"));
    					uniqueReg = rs.getString("uniqueregid");
    					String imageNameDB = rs.getString("imagename");
    					existingImageName = Objects.isNull(imageNameDB) || imageNameDB.isEmpty() ? null : imageNameDB;
    					if (!Objects.isNull(existingImageName)) {
    						String imagePath = BDUtility.getPath("images" + File.separator + existingImageName);
    						File imageFile = new File (imagePath);
    						if (imageFile.exists()) {
    							ImageIcon icon = new ImageIcon(imagePath);
    							// Use lblImage.getWidth() and lblImage.getHeight() for resizing based on the label's actual size
                                Image image = icon.getImage().getScaledInstance(lblImage.getWidth(),  lblImage.getHeight(), Image.SCALE_SMOOTH);
    							ImageIcon resizedIcon = new ImageIcon (image);
    							lblImage.setIcon(resizedIcon);
    						} else {
    							lblImage.setIcon(null);
    						}
    					} else {
    						lblImage.setIcon(null);
    					}
    				} else {
    					JOptionPane.showMessageDialog(UpdateUser.this, "Email not found","Not Found",JOptionPane.WARNING_MESSAGE);
    				}
    			}catch(Exception ex) {
    				ex.printStackTrace();
    				JOptionPane.showMessageDialog(UpdateUser.this, ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    			}
    			
			}
		});
		btnSearch.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnSearch.setBounds(265, 141, 109, 25);
		contentPane.add(btnSearch);
		internalFrame.setVisible(true);

	}
    
    // FIX START: clearForm method defined as an instance method of UpdateUser
    
    /**
     * Clears all input fields and resets the image and radio button group selection.
     */
    public void clearForm() {
        txtName.setText("");
        txtEmail.setText("");
        txtContact.setText("");
        txtAddress.setText("");
        txtState.setText("");
        txtCountry.setText("");

        genderGroup.clearSelection(); 
        lblImage.setIcon(null);
        selectedFile = null; 
    }
    
    /**
     * Saves the selected image to the file system after scaling.
     * @param email The email address used as the filename prefix.
     * @return The generated image name (filename) or null if saving fails or no new image was selected.
     */
	private String saveImage(String email) {
		if(originalImage != null && selectedFile != null) {
			try {
				String savePath = BDUtility.getPath("images\\");
				String extension = BDUtility.getFileExtension(selectedFile.getName());
				String imageName = email + "." + extension;
				File saveFile = new File(savePath + imageName);

                // 1. Get the Image object scaled to the JLabel size
                Image scaledTempImage = originalImage.getScaledInstance(
                    lblImage.getWidth(), 
                    lblImage.getHeight(), 
                    Image.SCALE_SMOOTH
                );
                
                // 2. Create a new BufferedImage (required for ImageIO.write)
                BufferedImage scaledImage = new BufferedImage(
                    lblImage.getWidth(), 
                    lblImage.getHeight(), 
                    BufferedImage.TYPE_INT_RGB
                );
                
                // 3. Draw the scaled image onto the new BufferedImage
                Graphics2D g2d = scaledImage.createGraphics();
                g2d.drawImage(scaledTempImage, 0, 0, null);
                g2d.dispose();

                // 4. Write the BufferedImage to the file system
                ImageIO.write(scaledImage, extension, saveFile);
                
                return imageName;
			}catch(Exception ex) {
				ex.printStackTrace();
                JOptionPane.showMessageDialog(UpdateUser.this, "Failed to save image: " + ex.getMessage(), "Image Save Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		return null;
	}
    
    // FIX END
}