package forms;

import java.awt.EventQueue;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import utility.BDUtility;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import dao.ConnectionProvider;

import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.Objects;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent; 

public class DeleteUser extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;
	private JTextField txtSearch;
    
    private JScrollPane userScrollPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DeleteUser frame = new DeleteUser();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
    
    // Instance method to fetch user data
    private void fetchUser(String searchText) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        
        // Resource management using try-with-resources for better safety
        try (Connection con = ConnectionProvider.getCon();
             Statement statement = con.createStatement()) {
            
            String query = null;
            
            // Handle search logic
            if (Objects.isNull(searchText) || searchText.trim().isEmpty()) {
                query = "select * from userdetails";
            } else {
                // Ensure search text is properly formatted for SQL LIKE clause
                String searchPattern = "%" + searchText + "%";
                query = "select * from userdetails where name like '" + searchPattern + 
                        "' or email like '" + searchPattern + "'";
            }
            
            ResultSet resultSet = statement.executeQuery(query);
            
            while (resultSet.next()) {
                model.addRow(new Object [] {
                    resultSet.getString("id"),
                    resultSet.getString("name"),
                    resultSet.getString("gender"),
                    resultSet.getString("email"),
                    resultSet.getString("contact"),
                    resultSet.getString("address"),
                    resultSet.getString("state"),
                    resultSet.getString("country"),
                    resultSet.getString("uniqueregid"),
                    // Ensure imagename and other potentially null fields are handled by JDBC driver, 
                    // though they should be returned as strings by resultSet.getString().
                    resultSet.getString("imagename"), 
                });
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteFile(String filePath) {
    	File fileToDelete = new File(filePath);
    	if (fileToDelete.exists()) {
    		if (fileToDelete.delete()) {
    			System.out.println("File deleted successfully: " + filePath);
    		} else {
    			System.out.println("Failed to delete the file: " + filePath);
    		}
    	} else {
    		System.out.println("File doesn't exist: " + filePath);
    	}
    }


	/**
	 * Create the frame.
	 */
	public DeleteUser() {
		setUndecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1087, 491);
		setLocationRelativeTo(null);
		setMinimumSize(new Dimension(1087, 491));
		setMaximumSize(new Dimension(1087, 491));
		
		 // Step 1: Call BDUtility (it adds JLabel as background to the frame)
        BDUtility.setImage(this, "images/abc1.jpg", 1087, 491);

        // Step 2: Get that background JLabel
        Component bg = getContentPane().getComponent(0);

        // Step 3: Create a layeredPane and put background at bottom layer
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 1087, 491);
        setContentPane(layeredPane);

        layeredPane.add(bg, Integer.valueOf(0)); // background at layer 0

        // Step 4: Transparent content panel for UI components
        contentPane = new JPanel(); // Initialize contentPane instance variable
        
        contentPane.setOpaque(false);
        contentPane.setBounds(0, 0, 1087, 491);
        layeredPane.add(contentPane, Integer.valueOf(1)); // above background
        contentPane.setLayout(null);
        
        JLabel lblNewLabel = new JLabel("Delete User"); 
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblNewLabel.setBounds(494, 11, 119, 35);
        contentPane.add(lblNewLabel);
        
        JButton btnNewButton = new JButton("X");
        btnNewButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		 DeleteUser.this.dispose();
        	}
        });
        btnNewButton.setForeground(Color.WHITE);
        btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 18));
        btnNewButton.setBackground(new Color(128, 0, 0));
        btnNewButton.setBounds(1030, 11, 47, 35);
        contentPane.add(btnNewButton);
        
        // Define Column Names
        String[] columnNames = {
                "Id", "Name", "Gender", "Email", "Contact",
                "Address", "State", "Country", "Registration Id", "Image Name"
            };

        // Using DefaultTableModel
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, columnNames);
        
        // Instantiate the JTable with the model
        table = new JTable(model);

        // Attach the MouseListener directly to the JTable 'table'
        table.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
                // Get the row index that was clicked
                int index = table.getSelectedRow();
                
                // Only proceed if a row is actually selected
                if (index == -1) {
                    return;
                }
                
                // Get the TableModel from the JTable
        		TableModel tableModel = table.getModel();
                
        		try {
                    // FIX: Safely retrieve the email (index 3) and convert to string, handling potential nulls
                    Object emailObj = tableModel.getValueAt(index, 3);
                    String email = (emailObj != null) ? emailObj.toString() : "";
                    
                    // The email field should not be null for a delete, but if it is, stop immediately
                    if (email.isEmpty()) {
                         JOptionPane.showMessageDialog(DeleteUser.this, "Cannot delete user: Email field is empty.", "Error", JOptionPane.ERROR_MESSAGE);
                         return;
                    }
                    
        			int dialogResult = JOptionPane.showConfirmDialog(DeleteUser.this, 
                        "* User details\n* QR codes\n* Attendance\n\nAssociated with this user will be deleted. \nAre you sure you want to proceed for user: " + email + "?", 
                        "Confirmation", JOptionPane.YES_NO_OPTION); 
                    
        			if (dialogResult == JOptionPane.YES_OPTION) {
                        // FIX: Safely retrieve the Image Name (index 9) and convert to string, handling potential nulls
                        Object imageNameObj = tableModel.getValueAt(index, 9);
                        String imageName = (imageNameObj != null) ? imageNameObj.toString() : "";
                        
                        // Attempt to delete files only if the name is not empty
                        if (!imageName.isEmpty()) {
                            String imagePath = BDUtility.getPath("images" + File.separator + imageName);
                            deleteFile(imagePath);
                        }
                        
                        // The QR code file name is based on the email
                        String qrCodePath = BDUtility.getPath("qrCodes" + File.separator + email + ".png"); // assuming .png based on common QR library use
        				deleteFile(qrCodePath);
        				
        				// Resource management using try-with-resources
        				try (Connection connection = ConnectionProvider.getCon();
                             // Use PreparedStatement for safety
        				     PreparedStatement preparedStatement = connection.prepareStatement(
                                "DELETE userattendance, userdetails FROM userdetails LEFT JOIN userattendance ON userattendance.userid=userdetails.id where userdetails.email=?")) {
                        
                            // Set the parameter for the email
        				    preparedStatement.setString(1, email); 
                            
                            // Execute the prepared statement
                            preparedStatement.executeUpdate();
                            
                            // Show success message
                            JOptionPane.showMessageDialog(DeleteUser.this, "User Deleted Successfully.","Confirmation", JOptionPane.INFORMATION_MESSAGE);
                        }
                        
                        // Refresh the table data after successful deletion
        				fetchUser(null);
        				
        			} else {
        				JOptionPane.showMessageDialog(DeleteUser.this, "Deletion Canceled", "Confirmation", JOptionPane.INFORMATION_MESSAGE);
        			}
        			
        		} catch (Exception ex) {
                    ex.printStackTrace();
        			JOptionPane.showMessageDialog(DeleteUser.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        		}
        	}
        });
        
        // Initialize the JScrollPane instance variable
        userScrollPane = new JScrollPane(table);
        
        // Set the bounds for the JScrollPane and add the scrollPane to the contentPane
        userScrollPane.setBounds(10, 132, 1067, 348);
        contentPane.add(userScrollPane);
        
        JLabel lblNewLabel_1 = new JLabel("Search");
        lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblNewLabel_1.setBounds(787, 76, 58, 14);
        contentPane.add(lblNewLabel_1);
        
        txtSearch = new JTextField();
        txtSearch.addKeyListener(new KeyAdapter() {
        	@Override
        	public void keyReleased(KeyEvent e) {
        		// Use getText() without .toString()
        		fetchUser(txtSearch.getText());
        	}
        });
        // Add an ActionListener to txtSearch to re-fetch data based on input when ENTER is pressed
        txtSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DeleteUser.this.fetchUser(txtSearch.getText());
            }
        });
        
        txtSearch.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txtSearch.setBounds(787, 101, 290, 20);
        contentPane.add(txtSearch);
        txtSearch.setColumns(10);

        this.getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.BLACK));
        
        // Explicitly call fetchUser here in the constructor after the table is set up.
        fetchUser(null);
	}
}