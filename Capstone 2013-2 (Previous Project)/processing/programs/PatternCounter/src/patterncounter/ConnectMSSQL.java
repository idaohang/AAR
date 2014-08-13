package patterncounter;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database handler class
 * 
 * @author      Andrew Hood (template only)
 * @author	James Pyke 05090946 <j.pyke@connect.qut.edu.au>
 * @version	1.0
 */
public class ConnectMSSQL {

    private Connection connection = null;
    private String connectionString, userName, password;

    public ConnectMSSQL(String dbServer, String dbName, String dbUser, String dbPassword) {
        connectionString = "jdbc:jtds:sqlserver://" + dbServer + "/" + dbName + ";instance=MSQLSERVER2012";
        userName = dbUser;
        password = dbPassword;
        this.connection = getConnection();
    }
    
    private Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                connection = DriverManager.getConnection(connectionString, userName, password);
            } catch (Exception e) {
                System.out.println("Error Trace in getConnection(): " + e.getMessage());
            }
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Delete old entries from database left over from previous pre-processing
     * runs
     * @param patternID beginning of pattern ID
     */
    public void clearPatterns(String patternID) {
        patternID = patternID.replace("'", "");
        try {
            if (connection != null) {
                String query = "DELETE FROM [capstone].[dbo].[tblPatterns]\n"
                        + "WHERE [patternID] LIKE '" + patternID + "%'";
                Statement statement = connection.createStatement();
                statement.execute(query);
            } else {
                System.out.println("No database connection");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Adds a pattern to the database
     * @param ePrintID article ID
     * @param patternID pattern ID
     * @param patternRating calculated weighting for pattern in article
     */
    public void putPattern(Integer ePrintID, String patternID, BigDecimal patternRating) {
        patternID = patternID.replace("'", "");
        try {
            if (connection != null) {
                String query = "INSERT INTO [capstone].[dbo].[tblPatterns]\n"
                        + "([ePrintID], [patternID], [patternRating])\n"
                        + "VALUES (" + ePrintID + ", '" + patternID + "', " + patternRating + ");";
                Statement statement = connection.createStatement();
                statement.execute(query);
            } else {
                System.out.println("No database connection");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
    }
}
