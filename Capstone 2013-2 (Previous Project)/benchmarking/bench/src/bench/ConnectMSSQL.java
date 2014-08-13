package bench;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Database handler class
 * 
 * @author      Andrew Hood (template only)
 * @author	James Pyke 05090946 <j.pyke@connect.qut.edu.au>
 * @version	1.0
 */
public class ConnectMSSQL {
    
    // settings provided by command line parameters
    private String connectionString, userName, password;
    private Connection connection = null;    
    
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

    public void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }

    /**
     * Get a list of all users who have viewed the minimum number of articles
     * @return list of user IDs
     */
    public ArrayList<Integer> getUserIDs() throws SQLException {
        ArrayList<Integer> users = new ArrayList<>();

        // run query
        String query = "SELECT [userID]\n"
                + "FROM [capstone].[dbo].[tblUserIDePrintID]\n"
                + "GROUP BY [userID]\n"
                + "HAVING COUNT([ePrintID]) >= " + Benchmark.MIN_VIEWED_ARTICLES;
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        
        // read and return the results
        while (rs.next()) {
            users.add(rs.getInt(1));
        }
        rs.close();
        return users;
    }

    /**
     * Get list of articles a user viewed
     * @param user user's ID
     * @return list of article IDs
     */
    public ArrayList<Integer> getViewedArticles(Integer user) throws SQLException {
        ArrayList<Integer> articles = new ArrayList<>();
        
        // run query
        String query = "SELECT [ePrintID]\n"
                + "FROM [capstone].[dbo].[tblUserIDePrintID]\n"
                + "WHERE [userID] = " + user;
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        
        // read and return the results
        while (rs.next()) {
            articles.add(rs.getInt(1));
        }
        rs.close();
        return articles;
    }

    /**
     * Return a list of article recommendations
     * @param articleIDs article(s) to make recommendations on
     * @param numResults number of recommendations to make
     * @param algorithm avg or sum
     * @return recommendations
     */
    public ArrayList<Integer> getRecommendedArticles(int[] articleIDs,
            int numResults, String algorithm) throws SQLException {
        
        // format articleIDs array as comma-separated string
        String articleIDsString = String.valueOf(articleIDs[0]);
        for (int i = 1; i < articleIDs.length; i++) {
            articleIDsString += ", " + articleIDs[i];
        }
        
        // if algorithm = avg, use average. otherwise assume sum
        String algFunction;
        if (algorithm.toLowerCase().equals("avg")) {
            algFunction = "AVG";
        } else {
            algFunction = "SUM";
        }

        // run the query
        ArrayList<Integer> recommendations = new ArrayList<>();
        String query = "SELECT TOP " + numResults + " [ePrintID], "
                + algFunction + "([patternRating])\n"
                + "FROM [capstone].[dbo].[tblPatterns]\n"
                + "WHERE [patternID] IN\n"
                + "(SELECT [patternID] FROM [capstone].[dbo].[tblPatterns]\n"
                + "WHERE [ePrintID] IN (" + articleIDsString + "))\n"
                + "AND [tblPatterns].[ePrintID] NOT IN (" + articleIDsString + ")\n"
                + "GROUP BY [tblPatterns].[ePrintID]\n"
                + "ORDER BY " + algFunction + "([patternRating]) DESC;";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        
        // read and return the results
        while (rs.next()) {
            recommendations.add(rs.getInt(1));
        }
        rs.close();
        return recommendations;
    }
}
