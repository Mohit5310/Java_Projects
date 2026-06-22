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
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JInternalFrame;
import java.awt.BorderLayout;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import dao.ConnectionProvider;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import java.sql.*;
import java.util.Objects;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class ViewUser extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField txtSearch;
//    private JTable table;
    private JTable userTable;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ViewUser frame = new ViewUser();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public ViewUser() {
        setUndecorated(true);
        setMinimumSize(new Dimension(1223, 476));
        setMaximumSize(new Dimension(1223, 476));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1223, 476);
		setLocationRelativeTo(null);

        // Step 1: Call BDUtility (it adds JLabel as background to the frame)
        BDUtility.setImage(this, "images/abc1.jpg", 1223, 476);

        // Step 2: Get that background JLabel
        Component bg = getContentPane().getComponent(0);

        // Step 3: Create a layeredPane and put background at bottom layer
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 1223, 476);
        setContentPane(layeredPane);

        layeredPane.add(bg, Integer.valueOf(0)); // background at layer 0

        // Step 4: Transparent content panel for UI components
        JPanel contentPane = new JPanel();
        contentPane.setOpaque(false);
        contentPane.setBounds(0, 0, 1223, 476);
        contentPane.setLayout(null);
        layeredPane.add(contentPane, Integer.valueOf(1)); // above background

        this.getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.BLACK));

        JLabel lblNewLabel = new JLabel("View User");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblNewLabel.setBounds(539, 11, 98, 35);
        lblNewLabel.setForeground(Color.BLACK);
        contentPane.add(lblNewLabel);

        JButton btnNewButton = new JButton("X");
        btnNewButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		ViewUser.this.dispose();
        	}
        });
        btnNewButton.setForeground(Color.WHITE);
        btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 18));
        btnNewButton.setBackground(new Color(128, 0, 0));
        btnNewButton.setBounds(1154, 11, 47, 35);
        contentPane.add(btnNewButton);

        JInternalFrame internalFrame = new JInternalFrame("New JInternalFrame");
        internalFrame.setBounds(891, 107, 310, 297);
        ((JPanel) internalFrame.getContentPane()).setOpaque(false);
        contentPane.add(internalFrame);

        JLabel lblImage = new JLabel("");
        internalFrame.getContentPane().add(lblImage, BorderLayout.CENTER);

        // Table Columns
        String[] columnNames = {
            "Id", "Name", "Gender", "Email", "Contact",
            "Address", "State", "Country", "Registration Id", "Image Name"
        };

        // Sample Data
        Object[][] data = {};

        JLabel lblNewLabel_1 = new JLabel("Search");
        lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblNewLabel_1.setBounds(655, 57, 47, 14);
        lblNewLabel_1.setForeground(Color.BLACK);
        contentPane.add(lblNewLabel_1);

        txtSearch = new JTextField();
        txtSearch.addKeyListener(new KeyAdapter() {
        	@Override
        	public void keyReleased(KeyEvent e) {
        		 try {
        		lblImage.setIcon(null);	 
        		fetchUser(txtSearch.getText().toString());
        		 }catch (Exception ex) {
        	            ex.printStackTrace();
        	        }
        	}
        });
        txtSearch.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txtSearch.setColumns(10);
        txtSearch.setBounds(655, 76, 226, 20);
        contentPane.add(txtSearch);

        // Create Table
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        userTable = new JTable(model);

        // ✅ Wrap in JScrollPane (IMPORTANT)
        JScrollPane scrollPane = new JScrollPane(userTable);

        // **FIX:** Add the MouseListener to the JTable, not the JScrollPane
        userTable.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		int index = userTable.getSelectedRow();
        		if (index >= 0) { // Check if a row is actually selected
        		    TableModel model = userTable.getModel();
        		    String name = Objects.isNull(model.getValueAt(index,  9)) ? null : model.getValueAt(index, 9) .toString();
        		    if (!Objects.isNull(name) && !name.isEmpty()) {
        		        // **IMPROVEMENT:** Use a better way to construct the path, assuming BDUtility handles it correctly.
        		        String imagePath = BDUtility.getPath("images/" + name);
        		        File imageFile = new File(imagePath);
        		        if (imageFile.exists()) {
        		            ImageIcon icon = new ImageIcon(imagePath);
        		            Image image = icon.getImage().getScaledInstance(322, 286, Image.SCALE_SMOOTH);
        		            ImageIcon resizedIcon = new ImageIcon(image);
        		            lblImage.setIcon(resizedIcon);
        		        } else {
        		            lblImage.setIcon(null);
        		            JOptionPane.showMessageDialog(null, "Either image has been deleted or not found.", "Image not found", JOptionPane.WARNING_MESSAGE);
        		        }
        		    } else {
        		        lblImage.setIcon(null);
        		    }
        		}
        	}
        });

        scrollPane.setBounds(10, 107, 871, 297);
        contentPane.add(scrollPane);

        internalFrame.setVisible(true);
        
        try {
            fetchUser(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void fetchUser(String searchText) throws Exception {
        DefaultTableModel model = (DefaultTableModel) userTable.getModel();
        model.setRowCount(0); // Clear existing data
        
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            con = ConnectionProvider.getCon();
            st = con.createStatement();
            String query = null;
            if(Objects.isNull(searchText) || searchText.isEmpty()) {
                query = "select * from userdetails";
            } else {
                query = "select * from userdetails where name like '%"+searchText+"%' or email like '%"+searchText+"%'";
            }
            
            rs = st.executeQuery(query);
            while (rs.next()) {
                model.addRow(new Object[] {
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("gender"),
                    rs.getString("email"),
                    rs.getString("contact"),
                    rs.getString("address"),
                    rs.getString("state"),
                    rs.getString("country"),
                    rs.getString("uniqueregid"),
                    rs.getString("imagename")
                });
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Something went wrong while fetching data from the database.");
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}