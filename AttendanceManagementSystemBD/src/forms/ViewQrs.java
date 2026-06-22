package forms;



import java.awt.EventQueue;

import javax.swing.JFrame;

import javax.swing.JPanel;

import utility.BDUtility;



import javax.swing.JLabel;

import javax.swing.JLayeredPane;

import java.awt.Font;



import javax.swing.BorderFactory;

import javax.swing.JButton;

import java.awt.Color;

import java.awt.Component;

import java.awt.Dimension;

import javax.swing.JOptionPane;

import javax.swing.ImageIcon; // <-- NEEDED for displaying the image



import javax.swing.JInternalFrame;

import javax.swing.JTable;

import javax.swing.table.DefaultTableModel;

import javax.swing.JScrollPane;

import javax.swing.table.TableModel; // <-- NEEDED for getting table model



import java.awt.BorderLayout;

import java.awt.event.ActionListener;

import java.awt.event.ActionEvent;

import java.awt.event.MouseAdapter; // <-- NEEDED for table click listener

import java.awt.event.MouseEvent; // <-- NEEDED for mouse event

import java.io.File;

import java.util.Arrays;



public class ViewQrs extends JFrame {



private static final long serialVersionUID = 1L;

private JPanel contentPane;

private JTable tblQrList;

private JScrollPane scrollPane;


// Instance variables to hold references to the QR display area components

private JInternalFrame internalFrame;

private JLabel lblQrImage;



/**

* Launch the application.

*/

public static void main(String[] args) {

EventQueue.invokeLater(new Runnable() {

public void run() {

try {

ViewQrs frame = new ViewQrs();

frame.setVisible(true);

} catch (Exception e) {

e.printStackTrace();

}

}

});

}


/**

* Loads the list of QR code files from the qrCodes directory into the table.

*/

private void loadQrList() {

DefaultTableModel model = (DefaultTableModel) tblQrList.getModel();

model.setRowCount(0); // Clear previous data



try {

// Get the directory where QR codes are saved

File directory = new File (BDUtility.getPath("qrCodes"));


if (!directory.exists()) {

directory.mkdirs();

return;

}


File[] files = directory.listFiles();


if (files != null) {

Arrays.sort(files);


for (File file : files) {

if (file.isFile()) {

String fileName = file.getName();

String email = fileName;



// Remove the file extension (e.g., .png or .jpg)

int dotIndex = fileName.lastIndexOf('.');

if (dotIndex > 0) {

email = fileName.substring(0, dotIndex);

}



model.addRow(new Object[] {email});

}

}

}

} catch (Exception ex) {

ex.printStackTrace();

JOptionPane.showMessageDialog(this, "Error loading QR codes: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);

}

}





/**

* Create the frame.

*/

public ViewQrs() {

setUndecorated(true);

setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

setSize(764, 460);

setLocationRelativeTo(null);

setMinimumSize(new Dimension(764, 460));

setMaximumSize(new Dimension(764, 460));


// --- Frame Setup ---

BDUtility.setImage(this, "images/abc1.jpg", 764, 460);

Component bg = getContentPane().getComponent(0);

JLayeredPane layeredPane = new JLayeredPane();

layeredPane.setBounds(0, 0, 764, 460);

setContentPane(layeredPane);



layeredPane.add(bg, Integer.valueOf(0));



contentPane = new JPanel();

contentPane.setOpaque(false);

contentPane.setBounds(0, 0, 764, 460);

layeredPane.add(contentPane, Integer.valueOf(1));

contentPane.setLayout(null);


this.getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.BLACK));




// --- UI Components ---

JLabel lblViewQrs = new JLabel("View Qr's");

lblViewQrs.setBounds(320, 11, 85, 37);

lblViewQrs.setFont(new Font("Tahoma", Font.BOLD, 18));

contentPane.add(lblViewQrs);


JButton btnNewButton = new JButton("X");

btnNewButton.addActionListener(new ActionListener() {

public void actionPerformed(ActionEvent e) {

ViewQrs.this.dispose();

}

});

btnNewButton.setBounds(684, 11, 52, 37);

btnNewButton.setForeground(Color.WHITE);

btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 18));

btnNewButton.setBackground(new Color(128, 0, 0));

contentPane.add(btnNewButton);


// Initialize and configure the JInternalFrame

internalFrame = new JInternalFrame(""); // Assigned to instance variable

internalFrame.setBounds(442, 122, 294, 308);

contentPane.add(internalFrame);


// Initialize and configure the JLabel where the QR will be displayed

lblQrImage = new JLabel(""); // Replaced lblNewLabel with lblQrImage instance variable

lblQrImage.setHorizontalAlignment(JLabel.CENTER); // Center the initial text

lblQrImage.setFont(new Font("Tahoma", Font.ITALIC, 14));


// Add the label to the center of the internal frame's content pane

internalFrame.getContentPane().setLayout(new BorderLayout()); // Set layout for the internal frame's content pane

internalFrame.getContentPane().add(lblQrImage, BorderLayout.CENTER);

internalFrame.setVisible(true);



// --- Table Setup ---

String[] columnNames = {"Email"};

DefaultTableModel tableModel = new DefaultTableModel(new Object[][]{}, columnNames);

tblQrList = new JTable(tableModel);


// ADDED MOUSE LISTENER HERE to handle clicks

tblQrList.addMouseListener(new MouseAdapter() {

@Override

public void mouseClicked(MouseEvent e) {

int index = tblQrList.getSelectedRow();

if (index < 0) return;


TableModel model = tblQrList.getModel();


// Column 0 contains the email (without extension)

String email = model.getValueAt(index, 0).toString();


// The actual file name includes the extension (assuming .png)

String fileName = email + ".png";


// Get the absolute path to the file

String filePath = BDUtility.getPath("qrCodes" + File.separator + fileName);


File qrFile = new File(filePath);


if (qrFile.exists()) {

try {

// Load the image file

ImageIcon icon = new ImageIcon(filePath);


// Set the image icon on the label

lblQrImage.setText(null); // Clear the initial text

lblQrImage.setIcon(icon);

} catch (Exception ex) {

lblQrImage.setIcon(null);

lblQrImage.setText("Error loading image.");

JOptionPane.showMessageDialog(ViewQrs.this, "Failed to load image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

ex.printStackTrace();

}

} else {

lblQrImage.setIcon(null);

lblQrImage.setText("QR file not found.");

}

}

});

// END MOUSE LISTENER



scrollPane = new JScrollPane(tblQrList);

scrollPane.setBounds(38, 122, 382, 308);

contentPane.add(scrollPane);


// Load initial data

loadQrList();

}

}