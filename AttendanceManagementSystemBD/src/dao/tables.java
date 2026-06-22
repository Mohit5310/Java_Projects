package dao;

import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import javax.swing.JOptionPane;

public class tables {

    public static void main(String[] args) {
        Connection con = null;
        Statement st = null;
        try {
            con = ConnectionProvider.getCon();
            st = con.createStatement();

            // Check and create userdetails table
            if (!tableExists(con, "userdetails")) {
                String createUserdetailsTableSQL = "CREATE TABLE userdetails ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "name VARCHAR(255) NOT NULL, "
                        + "gender VARCHAR(50) NOT NULL, "
                        + "email VARCHAR(255) NOT NULL, "
                        + "contact VARCHAR(20) NOT NULL, "
                        + "address VARCHAR(500), "
                        + "state VARCHAR(100), "
                        + "country VARCHAR(100), "
                        + "uniqueregid VARCHAR(100) NOT NULL, "
                        + "imagename VARCHAR(255));";
                st.executeUpdate(createUserdetailsTableSQL);
            }

            // Check and create userattendance table
            if (!tableExists(con, "userattendance")) {
                String createUserattendanceTableSQL = "CREATE TABLE userattendance ("
                        + "userid INT NOT NULL, "
                        + "date DATE NOT NULL, "
                        + "checkin DATETIME, "
                        + "checkout DATETIME, "
                        + "workduration VARCHAR(100));";
                st.executeUpdate(createUserattendanceTableSQL);
            }

            JOptionPane.showMessageDialog(null, "Tables Checked/Created Successfully");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // ✅ Correct tableExists method using DatabaseMetaData
    private static boolean tableExists(Connection con, String tableName) {
        try (ResultSet rs = con.getMetaData().getTables(null, null, tableName, null)) {
            return rs.next();  // true if table exists
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
