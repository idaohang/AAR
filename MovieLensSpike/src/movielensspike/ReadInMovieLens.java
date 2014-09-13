package movielensspike;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Imports the MovieLens data set into MySQL. This is a program is intended for testing purposes 
 * only, ie. it is rough
 * 
 * @author Jordan
 */
public class ReadInMovieLens {
    // DB connection
    private static Connection con;
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver",
                                DB_URL = "jdbc:mysql://localhost:3306/",
                                USER = "root",
                                PASS = "password";
    
    // location of the movie lens file
    private static final String MOVIELENS_FILE = "movielens-10M.dat"; // 10M data set
    // private static final String MOVIELENS_FILE = "movielens-100K.data"; // 100K movielens data set
    
    // percentage of data that will be used for comparison
    private static final double comparisonDataPercentage = 30;
    
    
    /**
     * @param args the command line arguments
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws 
            ClassNotFoundException, SQLException, IOException {
        // Register JDBC driver
        Class.forName(JDBC_DRIVER);

        // Create connection
        con = DriverManager.getConnection(DB_URL, USER, PASS);

        System.out.println("Setting Up Total Table...");
        createMovieLensTables();
        System.out.println("DONE\n");

        System.out.println("Reading In All Data...");
        setUpData();
        System.out.println("DONE\n");
        
         // create splitter with connection and comparison percentage
        System.out.println("Creating Splitter...");
        MovieLensSplitter splitter = new MovieLensSplitter(con, comparisonDataPercentage);
        System.out.println("DONE\n");
        
        // set up database
        System.out.println("Setting Up Split Tables..");
        splitter.createDbTables();
        System.out.println("DONE\n");
        
        // fill database
        System.out.println("Filling Split Tables..");
        splitter.fillDb();
        System.out.println("DONE\n");

        con.close();
    }
 
    /**
     * Sets up the tables for the movielens data set
     * 
     * @throws SQLException 
     */
    private static void createMovieLensTables() throws SQLException {
        // Create the database
        Statement statement = con.createStatement();

        // drop table first
        String sql = "DROP TABLE IF EXISTS capstone.movielens_total";
        statement.executeUpdate(sql);
        
        // Create the movielens table (all users)
        sql = "CREATE TABLE capstone.movielens_total"
                + "(USER_ID INTEGER NOT NULL,"
                + "MOVIE_ID INTEGER NOT NULL,"
                + "RATING_VAL DECIMAL(2,1) NOT NULL,"
                + "PRIMARY KEY (USER_ID, MOVIE_ID));";
        statement.executeUpdate(sql);
    }
    
    /**
     * Sets up the movie lens table in the db
     *
     */
    private static void setUpData() throws SQLException, IOException {
        // String to represent each line of the file as it is read
        String line;
        String query = "INSERT INTO "
                + "capstone.movielens_total(USER_ID, MOVIE_ID, RATING_VAL) "
                + "VALUES (?, ?, ?)";
        PreparedStatement prepStatement;

        // BufferedReader to parse over file text
        BufferedReader reader = new BufferedReader(new FileReader(MOVIELENS_FILE));

        // Prepared statement for movie tags
        prepStatement = con.prepareStatement(query);

            // Read the file line-by-line, 
        // creating statements and adding to a batch insertion command
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("::");

            for (int i = 1; i <= 3; i++) {
                prepStatement.setString(i, parts[i - 1]);
            }

            prepStatement.addBatch( );
        }

        prepStatement.executeBatch();
    }
    
}
