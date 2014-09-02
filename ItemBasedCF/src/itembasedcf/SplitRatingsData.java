package itembasedcf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Determines and outputs the precision and recall metrics for the system
 * 
 * @author Jordan
 */
public class SplitRatingsData {
    // Connection strings
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver",
            DB_URL = "jdbc:mysql://localhost:3306/",
            USER = "root",
            PASS = "password";
    
     // percentage of data that will be used for comparison
    private static final double comparisonDataPercentage = 30;
    
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // connect to database
        Connection con;
        Class.forName(JDBC_DRIVER);
        con = DriverManager.getConnection(DB_URL, USER, PASS);

        // create splitter with connection and comparison %
        RatingsDataSplitter splitter = new RatingsDataSplitter(con, comparisonDataPercentage);
        
        // set up database
        splitter.createDbTables();
        
        // fill database
        splitter.fillDb();
    }
}
