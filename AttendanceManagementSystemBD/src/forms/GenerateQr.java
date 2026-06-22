package forms;

import java.awt.EventQueue;

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

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.sql.ResultSet;
import java.sql.SQLException; 

import dao.ConnectionProvider;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
// Required for Files.write
import java.nio.file.Files; 

public class GenerateQr extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField; 
	private JTable table; // The actual JTable component
    
    // Instance variables for the QR display area and image label
    private JInternalFrame qrDisplayFrame;
    private JLabel lblImage; 

    // Instance variables for data shared between listeners
    private ByteArrayOutputStream out = null; 
    private String email = null; 

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GenerateQr frame = new GenerateQr();
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
	public GenerateQr() {
		setUndecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1101, 501);
		setLocationRelativeTo(null);
		setMinimumSize(new Dimension(1101, 501));
		setMaximumSize(new Dimension(1101, 501)); 
		
        // --- Table Setup First (Needed for initialization) ---
        
        String[] columnNames = {
                "Id", "Name", "Gender", "Email", "Contact",
                "Address", "State", "Country", "Registration Id"
            };

        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, columnNames);
        table = new JTable(model); 

        // JScrollPane wraps the table
        JScrollPane userScrollPane = new JScrollPane(table); 
       
        table.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		int index = table.getSelectedRow();
        		TableModel model = table.getModel();
        		
                // Ensure a row is selected
                if (index < 0) return;

        		String id = model.getValueAt(index, 0).toString();
        		String name = model.getValueAt(index, 1).toString();
        		
                // Assign value to the instance variable 'email'
        		email = model.getValueAt(index, 3).toString(); 
                
                String registrationId = model.getValueAt(index, 8).toString();
        		
        		Map<String,String> data = new HashMap<>();
        		data.put("id", id);
        		data.put("name", name);
        		data.put("email", email);
        		data.put("registrationId", registrationId);
        		
        		Gson gson = new Gson();
        		String jsonData = gson.toJson(data);
        		
                // Reset 'out' before generating new QR code
                out = null; 
                
        		try {
                    // Generate QR code stream and assign to instance variable 'out'
        			out = QRCode.from(jsonData).withSize(250, 250).to(ImageType.PNG).stream();
                    
        			byte[] imageData = out.toByteArray();
        			ImageIcon icon = new ImageIcon(imageData);
        			
        			lblImage.setIcon(icon);
        		} catch (Exception ex) {
        			ex.printStackTrace();
                    JOptionPane.showMessageDialog(GenerateQr.this, "Error generating QR Code.", "Error", JOptionPane.ERROR_MESSAGE);
        		} 
        	}
        });
        
        // --- End Table Setup ---
        
		// Step 1: Call BDUtility (it adds JLabel as background to the frame)
        BDUtility.setImage(this, "images/abc1.jpg", 1101, 501);

        // Step 2: Get that background JLabel
        Component bg = getContentPane().getComponent(0);

        // Step 3: Create a layeredPane and put background at bottom layer
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 1101, 501);
        setContentPane(layeredPane);

        layeredPane.add(bg, Integer.valueOf(0)); // background at layer 0

        // Step 4: Transparent content panel for UI components
        contentPane = new JPanel(); 
        contentPane.setOpaque(false);
        contentPane.setBounds(0, 0, 1101, 501);
        layeredPane.add(contentPane, Integer.valueOf(1)); // above background
        contentPane.setLayout(null);
     
        this.getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.BLACK));
		
		JLabel lblNewLabel = new JLabel("Generate QR"); 
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNewLabel.setBounds(545, 11, 150, 35);
		contentPane.add(lblNewLabel);
		
		JButton btnNewButton = new JButton("X");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GenerateQr.this.dispose();
			}
		});
		btnNewButton.setForeground(Color.WHITE);
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 18));
		btnNewButton.setBackground(new Color(128, 0, 0));
		btnNewButton.setBounds(1044, 11, 47, 35);
		contentPane.add(btnNewButton);
		
		textField = new JTextField();
		textField.setColumns(10);
		
		// Initialized the instance variable qrDisplayFrame
		qrDisplayFrame = new JInternalFrame("");
		qrDisplayFrame.setBounds(835, 112, 256, 299);
		contentPane.add(qrDisplayFrame);
        qrDisplayFrame.setVisible(true);
        
        // Initialize lblImage and add it to the JInternalFrame
        lblImage = new JLabel();
        lblImage.setBounds(0, 0, 256, 299); 
        qrDisplayFrame.getContentPane().add(lblImage);

        // 4. Set bounds for the SCROLL PANE and add it to the content pane
		userScrollPane.setBounds(10, 112, 815, 360); 
		contentPane.add(userScrollPane);

        // Load the data after the table object has been fully initialized
        loadUserData();
        
		JButton btnSaveQrAt = new JButton("Save QR at");
		btnSaveQrAt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				// Check if QR stream and email are ready
				if (out == null || email == null) {
					// FIX: Use GenerateQr.this for correct component reference
					JOptionPane.showMessageDialog(GenerateQr.this, "Please select a user and generate the QR code first.", "Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}
                
				try {
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setDialogTitle("Save QR Code at");
                    // Suggest a file name based on the email
					fileChooser.setSelectedFile(new File(email + ".png")); 
					
                    // FIX: Passed GenerateQr.this as the parent component
					int userSelection = fileChooser.showSaveDialog(GenerateQr.this); 
                    
					if (userSelection == JFileChooser.APPROVE_OPTION) {
						File fileToSave = fileChooser.getSelectedFile();
						try {
							// Write the byte array to the selected file path
							Files.write(fileToSave.toPath(), out.toByteArray());
                            
                            // Close the stream immediately after saving
                            out.close();
                            out = null; // Mark as saved/closed
                            
							// FIX: Use GenerateQr.this for correct component reference
							JOptionPane.showMessageDialog(GenerateQr.this,  "QR Code saved successfully!");
						} catch (Exception ex) {
							// FIX: Use GenerateQr.this for correct component reference
							JOptionPane.showMessageDialog(GenerateQr.this, "Error saving QR Code.", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(GenerateQr.this, "Something went wrong during the save process.");
				}
			}
		});
		btnSaveQrAt.setFont(new Font("Tahoma", Font.BOLD, 13));
		btnSaveQrAt.setBounds(973, 438, 118, 35);
		contentPane.add(btnSaveQrAt);
		
		// This button is now redundant, but I'll update its logic to match the new 'Save QR At' button
		JButton btnSaveQr = new JButton("Default Save"); 
		btnSaveQr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				// Check if QR stream and email are ready
				if(out == null || email == null) {
					JOptionPane.showMessageDialog(GenerateQr.this, "Please select a user from the table and generate the QR code first.", "Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				String defaultDir = BDUtility.getPath("qrCodes");
				File directory = new File(defaultDir);
				if(!directory.exists()) {
					directory.mkdirs();
				}
				
				// Using PNG extension to match QRCode.from(...).to(ImageType.PNG)
				File defaultFile = new File(directory, email + ".png");
				
				try {
					Files.write(defaultFile.toPath(), out.toByteArray());
					
					// Close the stream immediately after saving
					out.close();
					out = null; // Mark as saved/closed
					
					JOptionPane.showMessageDialog(GenerateQr.this,  "QR Code saved successfully at: " + defaultFile.getAbsolutePath());
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(GenerateQr.this, "Error saving QR Code: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				} 
			}
		});
		btnSaveQr.setFont(new Font("Tahoma", Font.BOLD, 13));
		btnSaveQr.setBounds(835, 438, 128, 35);
		contentPane.add(btnSaveQr);
	}

    /**
     * Loads user data from the database into the JTable.
     */
    private void loadUserData() {
        DefaultTableModel defaultModel = (DefaultTableModel) table.getModel();
        defaultModel.setRowCount(0); 

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = ConnectionProvider.getCon();
            statement = connection.createStatement();
            
            // Query for user details
            resultSet = statement.executeQuery("select id, name, gender, email, contact, address, state, country, uniqueregid from userdetails");
            
            while (resultSet.next()) {
                defaultModel.addRow(new Object[] {
                    resultSet.getString("id"),
                    resultSet.getString("name"),
                    resultSet.getString("gender"),
                    resultSet.getString("email"),
                    resultSet.getString("contact"),
                    resultSet.getString("address"),
                    resultSet.getString("state"),
                    resultSet.getString("country"),
                    resultSet.getString("uniqueregid")
                });
            }
        } catch (SQLException ex) {
            System.err.println("Database Error loading user data: " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
             System.err.println("General Error loading user data: " + ex.getMessage());
             ex.printStackTrace();
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                System.err.println("Error closing database resources: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}