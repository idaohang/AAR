package db2mallet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Database handler class
 * 
 * @author      Andrew Hood (template only)
 * @author	James Pyke 05090946 <j.pyke@connect.qut.edu.au>
 * @version	1.0
 */
public class ConnectMSSQL {

    // settings overrided by command line parameters
    private String connectionString = "jdbc:jtds:sqlserver://WIN7_X86_ARS/capstone;instance=MSQLSERVER2012";
    private String userName = "qut";
    private String password = "vres2012";
    private Connection connection = null;    
    
    public ConnectMSSQL() {
        this.connection = getConnection();
    }
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
            System.out.print(e.getMessage());
        }
    }

    public Map<String, String> getCategoryParents() {
        Map<String, String> parents = new HashMap<>();
        try {
            if (connection != null) {
                String query = "SELECT a.[Category], b.[Category]\n"
                        + " FROM [capstone].[dbo].[tblCategoryID] a, [capstone].[dbo].[tblCategoryID] b\n"
                        + " WHERE a.[ParentID] = b.[CatID]\n"
                        + "UNION SELECT [Category], [Category]\n"
                        + " FROM [capstone].[dbo].[tblCategoryID]\n"
                        + " WHERE [ParentID] IS NULL";
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(query);
                while (rs.next()) {
                    parents.put(rs.getString(1), rs.getString(2));
                }
                rs.close();
            } else {
                System.out.print("No database connection");
            }
        } catch (SQLException e) {
            System.out.print(e.getMessage());
        } catch (NullPointerException e) {
            System.out.print(e.getMessage());
        }
        return parents;
    }

    public ArrayList<String[]> getArticles(Integer start) {
        ArrayList<String[]> articles = new ArrayList<>();

        try {
            if (connection != null) {
                String query = "SELECT [ePrintID], [Abstract], [Subject] "
                        + "FROM [capstone].[dbo].[tblArticleInfo] "
                        + "WHERE [Subject] IS NOT NULL "
                        + "ORDER BY [ePrintID] "
                        + "OFFSET " + (start * DB2Mallet.QUERY_BLOCK) + " ROWS "
                        + "FETCH NEXT " + DB2Mallet.QUERY_BLOCK + " ROWS ONLY;";
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(query);
                while (rs.next()) {
                    String[] entry = {rs.getString(1), rs.getString(2), rs.getString(3)};
                    articles.add(entry);
                }
                rs.close();
            } else {
                System.out.print("No database connection");
            }
        } catch (SQLException e) {
            System.out.print(e.getMessage());
        } catch (NullPointerException e) {
            System.out.print(e.getMessage());
        }
        return articles;
    }
}
