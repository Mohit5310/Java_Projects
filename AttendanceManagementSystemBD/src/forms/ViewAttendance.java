package forms;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

import dao.ConnectionProvider;
import utility.BDUtility;

public class ViewAttendance extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable userTable;

	// class-level components used by multiple methods/listeners
	private JLabel lblAttendanceTrack;
	private JButton btnNewButton;

	private JDateChooser dateChooserFrom;
	private JDateChooser dateChooserTo;
	private JLabel lblSelectDate;
	private JTextField txtSearch;
	private JLabel lblTo;
	private JLabel lblSearch;
	private JLabel presentLBL;
	private JLabel lblPresent;
	private JLabel absentLBL;
	private JLabel lblAbsent;

	private JCheckBox checkBoxContact;
	private JCheckBox checkBoxAddress;
	private JCheckBox checkBoxState;
	private JCheckBox checkBoxCountry;
	private JCheckBox checkBoxUniqueRegId;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ViewAttendance frame = new ViewAttendance();
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
	public ViewAttendance() {
		setUndecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setSize(1101, 501);
		setLocationRelativeTo(null);
		setMinimumSize(new Dimension(1101, 501));
		setMaximumSize(new Dimension(1101, 501));

		// --- Frame Setup ---
		BDUtility.setImage(this, "images/abc1.jpg", 1101, 501);
		Component bg = getContentPane().getComponent(0);

		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, 1101, 501);
		setContentPane(layeredPane);
		layeredPane.add(bg, Integer.valueOf(0));

		contentPane = new JPanel();
		contentPane.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				loadDataInTable();
			}
		});
		contentPane.setFont(new Font("Tahoma", Font.BOLD, 14));
		contentPane.setOpaque(false);
		contentPane.setBounds(0, 0, 1101, 501);
		layeredPane.add(contentPane, Integer.valueOf(1));
		contentPane.setLayout(null);

		this.getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.BLACK));

		lblAttendanceTrack = new JLabel("Attendance Track");
		lblAttendanceTrack.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblAttendanceTrack.setBounds(494, 11, 160, 35);
		contentPane.add(lblAttendanceTrack);

		btnNewButton = new JButton("X");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ViewAttendance.this.dispose();
			}
		});
		btnNewButton.setForeground(Color.WHITE);
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 18));
		btnNewButton.setBackground(new Color(128, 0, 0));
		btnNewButton.setBounds(1044, 11, 47, 35);
		contentPane.add(btnNewButton);

		lblSelectDate = new JLabel("On / From");
		lblSelectDate.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblSelectDate.setBounds(279, 88, 63, 25);
		contentPane.add(lblSelectDate);

		dateChooserFrom = new JDateChooser();
		dateChooserFrom.setFont(new Font("Tahoma", Font.PLAIN, 14));
		dateChooserFrom.setDateFormatString("yyyy-MM-dd");
		dateChooserFrom.setBounds(279, 122, 230, 25);
		contentPane.add(dateChooserFrom);

		// dateChooserTo is now a class field
		dateChooserTo = new JDateChooser();
		dateChooserTo.setFont(new Font("Tahoma", Font.PLAIN, 14));
		dateChooserTo.setDateFormatString("yyyy-MM-dd");
		dateChooserTo.setBounds(533, 122, 230, 25);
		contentPane.add(dateChooserTo);

		txtSearch = new JTextField();
		txtSearch.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtSearch.setBounds(829, 127, 262, 20);
		contentPane.add(txtSearch);
		txtSearch.setColumns(10);

		lblTo = new JLabel("To");
		lblTo.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblTo.setBounds(533, 88, 22, 25);
		contentPane.add(lblTo);

		lblSearch = new JLabel("Search");
		lblSearch.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblSearch.setBounds(829, 88, 63, 25);
		contentPane.add(lblSearch);

		presentLBL = new JLabel("Present:");
		presentLBL.setFont(new Font("Tahoma", Font.PLAIN, 14));
		presentLBL.setBounds(48, 88, 63, 25);
		contentPane.add(presentLBL);

		lblPresent = new JLabel("-------");
		lblPresent.setForeground(new Color(0, 255, 0));
		lblPresent.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblPresent.setBounds(112, 88, 63, 25);
		contentPane.add(lblPresent);

		absentLBL = new JLabel("Absent:");
		absentLBL.setFont(new Font("Tahoma", Font.PLAIN, 14));
		absentLBL.setBounds(48, 122, 63, 25);
		contentPane.add(absentLBL);

		lblAbsent = new JLabel("-------");
		lblAbsent.setForeground(new Color(255, 0, 0));
		lblAbsent.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblAbsent.setBounds(112, 122, 63, 25);
		contentPane.add(lblAbsent);

		checkBoxContact = new JCheckBox("Contacts");
		checkBoxContact.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				loadDataInTable();
			}
		});
		checkBoxContact.setFont(new Font("Tahoma", Font.BOLD, 14));
		checkBoxContact.setBounds(48, 194, 97, 23);
		checkBoxContact.setOpaque(false);
		contentPane.add(checkBoxContact);

		checkBoxAddress = new JCheckBox("Address");
		checkBoxAddress.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				loadDataInTable();
			}
		});
		checkBoxAddress.setFont(new Font("Tahoma", Font.BOLD, 14));
		checkBoxAddress.setBounds(48, 238, 97, 23);
		checkBoxAddress.setOpaque(false);
		contentPane.add(checkBoxAddress);

		checkBoxState = new JCheckBox("State");
		checkBoxState.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				loadDataInTable();
			}
		});
		checkBoxState.setFont(new Font("Tahoma", Font.BOLD, 14));
		checkBoxState.setBounds(48, 286, 97, 23);
		checkBoxState.setOpaque(false);
		contentPane.add(checkBoxState);

		checkBoxCountry = new JCheckBox("Country");
		checkBoxCountry.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				loadDataInTable();
			}
		});
		checkBoxCountry.setFont(new Font("Tahoma", Font.BOLD, 14));
		checkBoxCountry.setBounds(48, 331, 97, 23);
		checkBoxCountry.setOpaque(false);
		contentPane.add(checkBoxCountry);

		checkBoxUniqueRegId = new JCheckBox("Unique Reg ID");
		checkBoxUniqueRegId.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				loadDataInTable();
			}
		});
		checkBoxUniqueRegId.setFont(new Font("Tahoma", Font.BOLD, 14));
		checkBoxUniqueRegId.setBounds(48, 379, 127, 23);
		checkBoxUniqueRegId.setOpaque(false);
		contentPane.add(checkBoxUniqueRegId);

		// Create table and put into scroll pane
		userTable = new JTable();
		JScrollPane scrollPane = new JScrollPane(userTable);
		scrollPane.setBounds(279, 167, 812, 323);
		contentPane.add(scrollPane);

		JButton btnResetFilters = new JButton("Reset Filters");
		btnResetFilters.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtSearch.setText("");
				dateChooserFrom.setDate(null);
				dateChooserTo.setDate(null);
				checkBoxContact.setSelected(false);
				checkBoxAddress.setSelected(false);
				checkBoxState.setSelected(false);
				checkBoxCountry.setSelected(false);
				checkBoxUniqueRegId.setSelected(false);

				loadDataInTable();
			}
		});
		btnResetFilters.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnResetFilters.setBounds(142, 455, 127, 35);
		contentPane.add(btnResetFilters);

		// trigger initial load
		loadDataInTable();
	}

	/**
	 * Class-level method used by listeners. Fixed and cleaned up SQL building and
	 * date handling.
	 */
	private void loadDataInTable() {
		List<String> columns = new ArrayList<>(
				Arrays.asList("ID", "Name", "Gender", "Email", "Date", "CheckIn", "CheckOut", "Work Duration"));

		String searchText = txtSearch.getText() != null ? txtSearch.getText().trim() : "";

		Date fromDateFromCal = dateChooserFrom.getDate();
		LocalDate fromDate = null;
		if (fromDateFromCal != null) {
			fromDate = fromDateFromCal.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		}

		Date toDateFromCal = dateChooserTo.getDate();
		LocalDate toDate = null;
		if (toDateFromCal != null) {
			toDate = toDateFromCal.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		}

		Long daysBetween = null;
		if (fromDate != null && toDate != null) {
			daysBetween = countWeekdays(fromDate, toDate);
		}

		Boolean contactIncluded = checkBoxContact.isSelected();
		Boolean addressIncluded = checkBoxAddress.isSelected();
		Boolean stateIncluded = checkBoxState.isSelected();
		Boolean countryIncluded = checkBoxCountry.isSelected();
		Boolean uniqueRegIdIncluded = checkBoxUniqueRegId.isSelected();

		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append(
				"SELECT ud.id, ud.name, ud.gender, ud.email, ua.date, ua.checkin, ua.checkout, ua.workduration");

		if (contactIncluded) {
			columns.add("Contact");
			sqlBuilder.append(", ud.contact");
		}
		if (addressIncluded) {
			columns.add("Address");
			sqlBuilder.append(", ud.address");
		}
		if (stateIncluded) {
			columns.add("State");
			sqlBuilder.append(", ud.state");
		}
		if (countryIncluded) {
			columns.add("Country");
			sqlBuilder.append(", ud.country");
		}
		if (uniqueRegIdIncluded) {
			columns.add("Unique Reg Id");
			sqlBuilder.append(", ud.uniqueregid");
		}

		sqlBuilder.append(" FROM userdetails AS ud INNER JOIN userattendance AS ua ON ud.id = ua.userid ");

		boolean whereAdded = false;
		if (!searchText.isEmpty()) {
			sqlBuilder.append(" WHERE (ud.name LIKE '%").append(escapeForLike(searchText))
					.append("%' OR ud.email LIKE '%").append(escapeForLike(searchText)).append("%') ");
			whereAdded = true;
		}

		if (fromDate != null && toDate != null) {
			if (!whereAdded) {
				sqlBuilder.append(" WHERE ");
				whereAdded = true;
			} else {
				sqlBuilder.append(" AND ");
			}
			sqlBuilder.append(" ua.date BETWEEN '").append(fromDate).append("' AND '").append(toDate).append("' ");
		} else if (fromDate != null) {
			if (!whereAdded) {
				sqlBuilder.append(" WHERE ");
			} else {
				sqlBuilder.append(" AND ");
			}
			sqlBuilder.append(" ua.date = '").append(fromDate).append("' ");
		}

		String sqlQuery = sqlBuilder.toString();

		DefaultTableModel model = new DefaultTableModel();
		model.setColumnIdentifiers(columns.toArray());
		userTable.setModel(model);

		try (Connection con = ConnectionProvider.getCon(); Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(sqlQuery)) {

			Long presentCount = 0L;
			Long absentCount = 0L;
			Set<String> emailList = new HashSet<>();

			while (rs.next()) {
				List<Object> row = new ArrayList<>();
				row.add(rs.getString("id"));
				row.add(rs.getString("name"));
				row.add(rs.getString("gender"));
				row.add(rs.getString("email"));
				emailList.add(rs.getString("email"));
				row.add(rs.getString("date"));
				row.add(rs.getString("checkin"));
				row.add(rs.getString("checkout"));
				row.add(rs.getString("workduration"));

				if (contactIncluded) {
					row.add(rs.getString("contact"));
				}
				if (addressIncluded) {
					row.add(rs.getString("address"));
				}
				if (stateIncluded) {
					row.add(rs.getString("state"));
				}
				if (countryIncluded) {
					row.add(rs.getString("country"));
				}
				if (uniqueRegIdIncluded) {
					row.add(rs.getString("uniqueregid"));
				}

				if (rs.getString("checkout") == null || rs.getString("checkout").trim().isEmpty()) {
					absentCount++;
				} else {
					presentCount++;
				}
				model.addRow(row.toArray());
			}

			// Show present/absent only if a single user's records are being displayed
			if (emailList.size() == 1 && emailList.iterator().hasNext()) {
				lblPresent.setVisible(true);
				lblAbsent.setVisible(true);
				presentLBL.setVisible(true);
				absentLBL.setVisible(true);
				lblPresent.setText(presentCount.toString());
				if (daysBetween != null && daysBetween > 0) {
					long calcAbsent = daysBetween - presentCount;
					if (calcAbsent < 0) {
						calcAbsent = 0;
					}
					lblAbsent.setText(Long.toString(calcAbsent));
				} else {
					lblAbsent.setText(absentCount.toString());
				}
			} else {
				lblPresent.setVisible(false);
				lblAbsent.setVisible(false);
				presentLBL.setVisible(false);
				absentLBL.setVisible(false);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Something went wrong while loading attendance.");
		}
	}

	/**
	 * Count weekdays inclusive between two LocalDate values.
	 */
	private Long countWeekdays(LocalDate start, LocalDate end) {
		long count = 0;
		LocalDate date = start;
		while (date.isBefore(end) || date.equals(end)) {
			if (!(date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY)) {
				count++;
			}
			date = date.plusDays(1);
		}
		return count;
	}

	/**
	 * Very small helper to prevent accidental SQL LIKE injection of % or '.
	 * (For production code use PreparedStatement instead; this is just a small
	 * escape.)
	 */
	private String escapeForLike(String s) {
		if (s == null)
			return "";
		return s.replace("'", "''").replace("%", "\\%").replace("_", "\\_");
	}
}
