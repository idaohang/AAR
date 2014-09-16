package itembasedcf;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Splits data in the database either horizontally or vertically for metrics generation
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
    private static double comparisonDataPercentage;
    private static int totalSections;
    
    private static RatingsSplitter splitter;
    
    /**
     * Splits the data
     * @param args
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        // connect to database
        Connection con;
        Class.forName(JDBC_DRIVER);
        con = DriverManager.getConnection(DB_URL, USER, PASS);

        // create splitter with connection and appropriate parameters
        if (isVerticallySplit()) {
            splitter = new RatingsDataVerticalSplitter(con, comparisonDataPercentage);
        } else {
            splitter = new RatingsDataHorizontalSplitter(con, totalSections);
        }
        
        // set up database
        System.out.println("\nCreating Tables...");
        splitter.createDbTables();
        System.out.println("DONE\n");
        
        // fill database
        System.out.println("Filling Tables...");
        splitter.fillDb();
        System.out.println("DONE\n");
    }
    
    /**
     * Asks the user how they want to split the data
     * 
     * @return true is the data is vertically split
     * @throws IOException 
     */
    private static boolean isVerticallySplit() throws IOException {
        Scanner in = new Scanner(System.in);

        // Get user input for type of splitting
        System.out.print("Vertical or Horizontal split? (V/H): ");
        String input = in.nextLine().toUpperCase();
        
        // Check the type of split to be made
        switch (input) {
            case "V":
                // Get user input for comparison percentage
                System.out.print("Comparison percentage? ");
                input = in.nextLine();
                comparisonDataPercentage = Integer.parseInt(input);
                return true;
            case "H":
                // Get user input for number of horizontal partitions
                System.out.print("Number of sections? ");
                input = in.nextLine();
                totalSections = Integer.parseInt(input);
                return false;
            default:
                // incorrect character entered, throw exception
                throw new IOException("Incorrect charactered entered");
        }
    }
    
}
