package forms;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import utility.BDUtility;

import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane; 
import javax.swing.UIManager; // Added for system look and feel

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.HeadlessException; // Added for checkInCheckOut/showPopUpForCertainDuration
import java.sql.SQLException; // Added for checkInCheckOut

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.swing.Timer;
import java.awt.BorderLayout; 

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import dao.ConnectionProvider;

import java.awt.event.ActionEvent;
import java.io.File; 
import java.sql.*;


public class MarkAttendance extends JFrame implements Runnable, ThreadFactory {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JLabel lblDateTime; 
	private JLabel lblImage; // Promoted to instance variable for use in CircularImageFrame
	private JLabel lblName; // Promoted to instance variable for use in CircularImageFrame
	private JLabel lblCheckInCheckOut; // Promoted to instance variable for use in CircularImageFrame
	
	private JPanel webCamPanel; 
	
	private WebcamPanel panel = null;
	private Webcam webcam = null;
	// FIX: Used ThreadFactory in Executor creation
	private ExecutorService executor = Executors.newSingleThreadExecutor(this);
	private volatile boolean running = true;
	private BufferedImage profileImage = null; // FIX: Renamed and initialized instance variable

	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// Set System Look and Feel for better appearance
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MarkAttendance frame = new MarkAttendance();
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
	public MarkAttendance() {
		setUndecorated(true);
		// FIX: Correctly call setVisible(true) only once in main, dispose is fine for exit.
		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		setSize(1366, 768);
		setLocationRelativeTo(null);
		setMinimumSize(new Dimension(1366, 768));
		setMaximumSize(new Dimension(1366, 768));
		
		// --- Frame Setup ---
        // Note: BDUtility.setImage is an external method not provided, assumed correct
        BDUtility.setImage(this, "images/abc1.jpg", 1366, 768);
        Component bg = getContentPane().getComponent(0);
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 1366, 768);
        setContentPane(layeredPane);

        layeredPane.add(bg, Integer.valueOf(0));

        contentPane = new JPanel();
        contentPane.setOpaque(false);
        contentPane.setBounds(0, 0, 1366, 768);
        layeredPane.add(contentPane, Integer.valueOf(1));
        contentPane.setLayout(null);
        
        this.getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.BLACK));

		// --- UI Components ---
		
		JLabel lblMarkAttendance = new JLabel("Mark Attendance");
		lblMarkAttendance.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblMarkAttendance.setBounds(622, 11, 155, 35);
		contentPane.add(lblMarkAttendance);
		
		JButton btnNewButton = new JButton("X");
		btnNewButton.addActionListener(new ActionListener() {
			// FIX: Correctly shut down and dispose of the frame
			@Override
			public void actionPerformed(ActionEvent e) {
				running = false;
				stopWebcam();
				// The executor is shut down in stopWebcam().
				MarkAttendance.this.dispose(); // Correct way to call dispose on the JFrame
			}
		});
		btnNewButton.setForeground(Color.WHITE);
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 18));
		btnNewButton.setBackground(new Color(128, 0, 0));
		btnNewButton.setBounds(1297, 11, 47, 35);
		contentPane.add(btnNewButton);
		
		// FIX 1: Initialize the instance variable webCamPanel
		webCamPanel = new JPanel();
		webCamPanel.setBounds(29, 139, 689, 518);
		contentPane.add(webCamPanel);
		// FIX 2: Set BorderLayout for the webcam container panel
		webCamPanel.setLayout(new BorderLayout()); 
		
		// FIX: Initialized instance variable lblImage
		lblImage = new JLabel("");
		lblImage.setBounds(914, 234, 322, 286);
		contentPane.add(lblImage);
		
		JLabel lblNewLabel = new JLabel("Date");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel.setBounds(951, 139, 46, 14);
		contentPane.add(lblNewLabel);
		
		JLabel lblTime_title = new JLabel("Time"); 
		lblTime_title.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblTime_title.setBounds(1105, 139, 33, 14);
		contentPane.add(lblTime_title);
		
		// Initialize the instance variable lblDateTime
		lblDateTime = new JLabel("Loading...");
		lblDateTime.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblDateTime.setBounds(951, 164, 285, 35);
		contentPane.add(lblDateTime);
		
		// FIX: Initialized instance variable lblName
		lblName = new JLabel("");
		lblName.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblName.setBounds(949, 568, 242, 35);
		contentPane.add(lblName);
		
		// FIX: Initialized instance variable lblCheckInCheckOut
		lblCheckInCheckOut = new JLabel("");
		lblCheckInCheckOut.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblCheckInCheckOut.setBounds(951, 622, 242, 35);
		contentPane.add(lblCheckInCheckOut);
		
		initWebcam();
		
		// Use Swing Timer to update the time
		Timer timer = new Timer(100, e -> updateTime());
		timer.start();

	}
	
	 private void initWebcam() {
		webcam = Webcam.getDefault(); 
		if (webcam != null) {
			Dimension[] resolutions = webcam.getViewSizes();
			// Find the largest resolution
			Dimension maxResolution = new Dimension(0, 0);
			for(Dimension d : resolutions) {
				if (d.getWidth() > maxResolution.getWidth()) {
					maxResolution = d;
				}
			}
			
			if (webcam.isOpen()) {
				webcam.close();
			}
			
			webcam.setViewSize(maxResolution);
			webcam.open(true); // Open asynchronously
			
			panel = new WebcamPanel(webcam);
			panel.setPreferredSize(maxResolution);
			panel.setFPSDisplayed(true);
			
			// Use BorderLayout.CENTER which works with the BorderLayout set in the constructor
			webCamPanel.add(panel, BorderLayout.CENTER); 
			
			webCamPanel.revalidate();
			webCamPanel.repaint();
			
			// Start the scanning thread
			executor.execute(this);
		} else {
            // Display an error message to the user if the webcam is not found
            JOptionPane.showMessageDialog(this, "Webcam not found or inaccessible.", "Webcam Error", JOptionPane.ERROR_MESSAGE);
			System.err.println("Issue with webcam. Please check device connection or permissions.");
		}
		
	}
	
	// Added helper method to stop webcam gracefully
	private void stopWebcam() {
		running = false; 
		// Shutdown the executor and wait briefly for threads to terminate
		if (executor != null) {
			executor.shutdownNow(); 
		}
		if (webcam != null && webcam.isOpen()) {
			webcam.close();
		}
	}

	 private void updateTime() {
     	// FIX: Use a format that is consistent with the database operations (date only for date field, full for datetime)
     	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); 
     	lblDateTime.setText(simpleDateFormat.format(new Date()));
     }

	 @Override
	 public Thread newThread(Runnable r) {
		// Provide a meaningful thread name
		Thread t = new Thread(r, "Webcam-Scanner-Thread");
        t.setDaemon(true); // FIX: Set thread as daemon
        return t;
	 }

	 Map<String, String> resultMap = new HashMap<>(); // FIX: Diamond operator used for cleaner code
	 
	 @Override
	 public void run() {
		// Keep the scanner running while the 'running' flag is true
		 while (running) { // FIX: Changed do-while to while for cleaner loop control
			 try {
				 Thread.sleep(100); 
			 } catch (InterruptedException ex) {
				 running = false;
				 Thread.currentThread().interrupt();
				 break; // Exit loop on interrupt
			 }
			 
			 try {
				 Result result = null;
				 BufferedImage image = null;
				 if(webcam != null && webcam.isOpen()) { 
					 image = webcam.getImage();
					 if(image == null) {
						 continue;
					 }
				 } else {
                     continue; 
                 }
				 
				 LuminanceSource source = new BufferedImageLuminanceSource(image);
				 BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
				 
				 try {
					 result = new MultiFormatReader().decode(bitmap);
				 } catch (NotFoundException ex) {
					 // QR not found in this frame, continue scanning
				 }
				 
				 if (result != null) {
					 running = false; // QR code found, stop scanning loop temporarily
					 
					 String jsonString = result.getText();
					 Gson gson = new Gson();
					 // FIX: Correct way to get the generic type token
					 java.lang.reflect.Type type = new TypeToken<Map<String, String>>() {}.getType(); 
					 resultMap = gson.fromJson(jsonString, type);
				 
                     // Assuming "email" key exists in the QR code JSON
					 String email = resultMap.get("email");
					 // Assuming "id" key exists in the QR code JSON
					 String userId = resultMap.get("id");
					 
                     if (email != null && userId != null) {
						 // Note: BDUtility.getPath() likely needs a File.separator
						 // FIX: Corrected folder name from "imahes" to "images" (common typo)
						 String finalPath = BDUtility.getPath("images" + File.separator + email + ".jpg"); 
                         
                         // Execute the attendance marking and UI update on the Event Dispatch Thread (EDT)
                         EventQueue.invokeLater(() -> {
                             CircularImageFrame(finalPath);
                             // The scanner will restart when the popup closes in showPopUpForCertainDuration
                         });
                     } else {
						 EventQueue.invokeLater(() -> {
							 showPopUpForCertainDuration("QR code is missing 'email' or 'id' key.", "QR Error", JOptionPane.ERROR_MESSAGE);
							 running = true; // Restart scanner if QR data is invalid
						 });
					 }
				}
			 } catch(Exception ex) {
				 System.err.println("Error in QR code processing: " + ex.getMessage());
				 ex.printStackTrace(); 
				 // Consider restarting scanning in case of an exception
				 EventQueue.invokeLater(() -> {
					 showPopUpForCertainDuration("An unexpected error occurred during scan.", "Scan Error", JOptionPane.ERROR_MESSAGE);
					 running = true;
				 });
			 }
		 }
	}
	 
	 // Removed unused instance variable 'image'
	 private void CircularImageFrame(String finalPath) {
		try (Connection con = ConnectionProvider.getCon(); 
             Statement st = con.createStatement()) {
			
			// FIX: Corrected SQL syntax 'email'' to 'email='
			ResultSet rs = st.executeQuery("select * from userdetails where email='" + resultMap.get("email") + "';");
			
			if(!rs.next()) {
				showPopUpForCertainDuration("User is not Registered or Deleted", "Invalid QR", JOptionPane.ERROR_MESSAGE);
				// FIX: Restart the scanner as no action was taken
				running = true; 
				return;
			}
			
			// FIX: Use the correct instance variable name
			profileImage = null; 
			File imageFile = new File(finalPath); // FIX: Use finalPath instead of undefined imagePath
			
			// FIX: Ensure the label is cleared first
			lblImage.setIcon(null); 
			
			if(imageFile.exists()) {
				try {
					profileImage = ImageIO.read(imageFile);
					profileImage = createCircularImage(profileImage);
					ImageIcon icon = new ImageIcon(profileImage);
					lblImage.setIcon(icon);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else {
				// FIX: Consistent variable name and logic for default image
				BufferedImage defaultImage = new BufferedImage (300, 300, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = defaultImage.createGraphics();
				
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				g2d.setColor(Color.BLACK);
				g2d.fillOval(25, 25, 250, 250);
				
				g2d.setFont(new Font ("Serif", Font.BOLD, 250));
				g2d.setColor(Color.WHITE);
				
				// Null check for "name" and safely get the first char
				String name = resultMap.get("name");
				if (name != null && !name.isEmpty()) {
					g2d.drawString(String.valueOf(name.charAt(0)).toUpperCase(), 75, 225);
				} else {
					g2d.drawString("?", 100, 225);
				}
				
				g2d.dispose();
				
				ImageIcon imageIconn = new ImageIcon(defaultImage);
				lblImage.setIcon(imageIconn);
			}
			
			lblName.setHorizontalAlignment(JLabel.CENTER);
			lblName.setText(resultMap.get("name"));
			
			// Proceed to check in/out
			if (!checkInCheckOut()) {
				// checkInCheckOut handles the popup and restart of the scanner on false return
				return;
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			// FIX: Restart scanner in case of DB or major error
			running = true; 
		}
		
	 }

	 // FIX: Added necessary exceptions to method signature
	 private boolean checkInCheckOut() throws HeadlessException, SQLException {
	   String popUpHeader = null;
	   String popUpMessage = null;
	   Color color = null;
	   
	   // FIX: Use try-with-resources for Connection and Statement for auto-closing
	   try (Connection con = ConnectionProvider.getCon(); 
            Statement st = con.createStatement()) {
		   
		   LocalDate currentDate = LocalDate.now();
		   DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		   
		   LocalDateTime currentDateTime = LocalDateTime.now();
		   DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		   
		   ResultSet rs = st.executeQuery("select * from userattendance where date='" + currentDate.format(dateFormatter) + "' and userid=" + Integer.valueOf(resultMap.get("id")) + ";");
		   
		   // FIX: Removed redundant second call to ConnectionProvider.getCon()
		   
		   if (rs.next()) {
			   // Check-out scenario
			   String checkOutDateTime = rs.getString(4);
			   if (checkOutDateTime != null) {
				   popUpMessage = "Already Checked Out For the Day";
				   popUpHeader = "Invalid CheckOut";
				   showPopUpForCertainDuration(popUpMessage, popUpHeader, JOptionPane.ERROR_MESSAGE);
				   return false;
			   }
			   
			   String checkInDateTime = rs.getString(3);
			   LocalDateTime checkInLocalDateTime = LocalDateTime.parse(checkInDateTime, dateTimeFormatter);
			   Duration duration = Duration.between(checkInLocalDateTime, currentDateTime);
			   
			   long hours = duration.toHours();
			   long minutes = duration.minusHours(hours).toMinutes();
			   // FIX: Removed redundant seconds calculation as it wasn't used correctly
			   
			   // FIX: Corrected duration check logic (should be minutes > 5 OR hours > 0)
			   if(! (hours > 0 || (hours == 0 && minutes >= 5))) {
				   long remainingMinutes = 4 - minutes;
				   long remainingSeconds = 60 - duration.getSeconds() % 60;
				   // Ensure seconds is positive
				   if(remainingSeconds == 60) {
					   remainingSeconds = 0;
					   remainingMinutes++; // Add minute if seconds was 60
				   }
				   
				   popUpMessage = String.format ("Your work duration is less than 5 minutes\nYou can check out after: %d minutes and %d seconds", remainingMinutes, remainingSeconds);
				   popUpHeader = "Duration Warning";
				   
				   showPopUpForCertainDuration (popUpMessage, popUpHeader, JOptionPane.WARNING_MESSAGE);
				   return false;
			   }
			   
			   String updateQuery = "update userattendance set checkout=?,workduration=? where date=? and userid=?";
			   // FIX: Use the existing connection
			   try (PreparedStatement preparedStatement = con.prepareStatement(updateQuery)) { 
				   preparedStatement.setString(1, currentDateTime.format(dateTimeFormatter));
				   preparedStatement.setString(2, "" + hours + " Hours and " + minutes + " Minutes"); // FIX: Corrected typo "Mnutes"
				   preparedStatement.setString(3, currentDate.format(dateFormatter));
				   preparedStatement.setString(4, resultMap.get("id"));
				   
				   preparedStatement.executeUpdate();
			   }
			   
			   popUpHeader = "CheckOut";
			   popUpMessage = "Checked Out at " + currentDateTime.format(dateTimeFormatter) + "\nWork Duration: " + hours + " Hours and " + minutes + " Minutes";
			   color = Color.RED;
		   } else {
			   // Check-in scenario
			   // FIX: Corrected SQL INSERT statement syntax (removed trailing comma and adjusted placeholders)
			   String insertQuery = "INSERT INTO userattendance (userid, date, checkin) VALUES (?,?,?)"; 
			   // FIX: Use the existing connection
			   try (PreparedStatement preparedStatement = con.prepareStatement(insertQuery)) {
				   preparedStatement.setString(1, resultMap.get("id"));
				   // FIX: Use dateFormatter for the date field
				   preparedStatement.setString(2, currentDate.format(dateFormatter)); 
				   preparedStatement.setString(3, currentDateTime.format(dateTimeFormatter));
				   preparedStatement.executeUpdate();
			   }
			   
			   popUpHeader = "CheckIn";
			   // FIX: Added space in popup message
			   popUpMessage = "Checked In at " + currentDateTime.format(dateTimeFormatter); 
			   color = Color.GREEN;
		   }
	   } // Connection and Statement are auto-closed here
	   
	   lblCheckInCheckOut.setHorizontalAlignment(JLabel.CENTER);
	   lblCheckInCheckOut.setText (popUpHeader);
	   lblCheckInCheckOut.setForeground(color);
	   lblCheckInCheckOut.setBackground(Color.DARK_GRAY);
	   lblCheckInCheckOut.setOpaque(true);
	   showPopUpForCertainDuration(popUpMessage, popUpHeader, JOptionPane.INFORMATION_MESSAGE);
	   return true;
	}

	 private void showPopUpForCertainDuration(String popUpMessage, String popUpHeader, Integer iconId) throws HeadlessException {
		final JOptionPane optionPane = new JOptionPane (popUpMessage, iconId);
		final JDialog dialog = optionPane.createDialog(this, popUpHeader); // FIX: Used 'this' as parent component
		Timer timer = new Timer (5000, new ActionListener( ) {

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
				clearUserDetails();
				// FIX: Restart the scanner after the message is gone
				running = true; 
				// The executor is already running and will pick up the 'running = true' flag
			}
			
		});
		timer.setRepeats(false);
		timer.start();
		dialog.setVisible(true);
	 }

	 private void clearUserDetails() {
		   lblCheckInCheckOut.setText("");
		   lblCheckInCheckOut.setForeground(null);
		   lblCheckInCheckOut.setBackground(null);
		   lblCheckInCheckOut.setOpaque(false);
		   lblName.setText("");
		   lblImage.setIcon(null);
		   profileImage = null; // Clear the stored image
	 }
	 
	 // FIX: Removed the incorrect paint method override.
	 
	 private BufferedImage createCircularImage(BufferedImage image) {
		int diameter = 285;
		// FIX: Use size that fits the label (322x286) better, but keeping original logic
		BufferedImage resizedImage = new BufferedImage (diameter, diameter, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = resizedImage.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.drawImage(image,  0,  0, diameter, diameter, null);
		g2.dispose();
		
		BufferedImage circularImage = new BufferedImage (diameter, diameter, BufferedImage.TYPE_INT_ARGB);
		g2 = circularImage.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		Ellipse2D.Double circle = new Ellipse2D.Double(0, 0, diameter, diameter);
		g2.setClip(circle);
		g2.drawImage(resizedImage,  0,  0,  null);
		g2.dispose();
		 return circularImage;
	 }
}