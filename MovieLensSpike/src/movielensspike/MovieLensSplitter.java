package movielensspike;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Splits MovieLens data into two sections for use with IR metrics (precision/recall). One set of
 * data will be used to generate recommendations, and the other will be used for a comparison.
 * Each data set will be output to the database
 *
 * @author Jordan
 */
public class MovieLensSplitter {
    private final Connection con; // db connection
    private final double comparisonDataPercentage; // % of data that will be used for comparison

    /**
     * Constructor sets up private connection field
     * @param con connection to the database
     * @param percentage
     */
    public MovieLensSplitter(Connection con, double percentage) {
        this.con = con;
        this.comparisonDataPercentage = percentage;
    }
    
    /**
     * Creates two tables in the DB for data to be added to; one table for recommendation data and
     * one for comparison data
     *
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     */
    public void createDbTables() throws ClassNotFoundException, SQLException {
        String sql;
        Statement statement = con.createStatement();

        // drop tables first
        sql = "DROP TABLE IF EXISTS capstone.movielens_compare";
        statement.executeUpdate(sql);
        sql = "DROP TABLE IF EXISTS capstone.movielens_recommend";
        statement.executeUpdate(sql);
        
        // Create the table for recommendation data
        sql = "CREATE TABLE capstone.movielens_recommend"
                + "(USER_ID INTEGER NOT NULL,"
                + "MOVIE_ID INTEGER NOT NULL,"
                + "RATING_VAL DECIMAL(2,1) NOT NULL,"
                + "PRIMARY KEY (USER_ID, MOVIE_ID));";
        statement.executeUpdate(sql);

        // Create the table for comparison data
        sql = "CREATE TABLE capstone.movielens_compare"
                + "(USER_ID INTEGER NOT NULL,"
                + "MOVIE_ID INTEGER NOT NULL,"
                + "RATING_VAL DECIMAL(2,1) NOT NULL,"
                + "PRIMARY KEY (USER_ID, MOVIE_ID));";
        statement.executeUpdate(sql);
    }
    
    /**
     * Fills tables in the database with split rating information
     */
    public void fillDb() throws SQLException {
        // statement to insert all data into two tables
        PreparedStatement prepStatement = null;

        // Get all user ids
        ResultSet allUsers = getUsers();
        
        // Iterate over users, collect their data and add to appropriate tables
        while (allUsers.next()) {
            ResultSet allData;
            int currentUser, dataCount;
            
            // list for shuffling to access random comparison data pieces
            ArrayList<Integer> dataPositions = new ArrayList(); 
            
             // the locations of the comparison data pieces in allData
            ArrayList<Integer> comparisonLocations = new ArrayList();
            
            ArrayList<MovieRating> comparisonData = new ArrayList(), 
                                   recommenderData = new ArrayList(); // two data sets
            
            // Store current user
            currentUser = allUsers.getInt("USER_ID");
           
            // Collect all data pieces for that user
            allData = getDataForUser(currentUser);
            
            // count data for user
            dataCount = countDataForUser(currentUser);
            
            // initialise indices array for shuffling
            for (int i = 1; i <= dataCount; i++) {
                dataPositions.add(i);
            }
            
            Collections.shuffle(dataPositions); // shuffle array
            
            // add random data to comparison data using shuffle dataIndices
            for (int i = 0; i < comparisonDataPercentage/100 * dataCount; i ++) {
                // move allData result to the first random position
                allData.absolute(dataPositions.get(i));

                // add this movie rating to the comparison data
                comparisonData.add(new MovieRating(allData.getInt("USER_ID"), 
                        allData.getInt("MOVIE_ID"), allData.getDouble("RATING_VAL")));
                
                // mark this position as a comparison piece of data
                comparisonLocations.add(dataPositions.get(i) - 1);
            }
            
            // add recommendation data to array
            for (int i = 0; i < dataCount; i++) {
                // check if comparison already has this data piece
                if (!(comparisonLocations).contains(i)) {
                    allData.absolute(i + 1); // set allData to this location
                    
                    // add data to recommender set
                    recommenderData.add(new MovieRating(allData.getInt("USER_ID"),
                            allData.getInt("MOVIE_ID"), allData.getDouble("RATING_VAL")));
                }
            }
            
            // insert data into tables
            prepStatement = prepareMovieRatingsInsertion(
                    "movielens_compare", comparisonData, prepStatement); // comparison
            prepStatement.executeBatch();
            
            prepStatement = prepareMovieRatingsInsertion(
                    "movielens_recommend", recommenderData, prepStatement); // recommender
            prepStatement.executeBatch();
        }
        
        
    }
    
    /**
     * Gets all users as a result set
     * 
     * @return all the users
     * @throws SQLException 
     */
    private ResultSet getUsers() throws SQLException {
        // Prepared statement to collect users
        PreparedStatement prepStatement;

        // SQL statement to collect distinct users
        prepStatement = con.prepareStatement(
                "SELECT DISTINCT USER_ID FROM capstone.movielens_total");

        // Execute query and return results
        return prepStatement.executeQuery();
    }
    
    /**
     * Gets all the rating data for a user
     * 
     * @param userId the user to get data for
     * @return user's rating data
     * @throws SQLException 
     */
    private ResultSet getDataForUser(int userId) throws SQLException {
        // Prepared statement to collect data
        PreparedStatement prepStatement;

        // SQL statement to collect user's data
        prepStatement = con.prepareStatement(
                "SELECT * FROM capstone.movielens_total WHERE USER_ID = " + userId);

        // Execute query and return results
        return prepStatement.executeQuery();
    }
    
    /**
     * Counts the number of data pieces a user has
     * 
     * @param userId the user to count data pieces for
     * @return the count
     * @throws SQLException 
     */
    private int countDataForUser(int userId) throws SQLException {
        // Prepared statement to count user's data
        PreparedStatement prepStatement;
        ResultSet rs;

        // SQL statement to count user's data
        prepStatement = con.prepareStatement(
                "SELECT COUNT(*) FROM capstone.movielens_total WHERE USER_ID = " + userId);

        // Execute query and return count
        rs = prepStatement.executeQuery();
        rs.first();
        return rs.getInt("COUNT(*)");
    }
    
    /**
     * Adds batch statement to insert movie ratings
     * 
     * @param tableName the table to insert into
     * @param ratings the ratings to insert
     */
    private PreparedStatement prepareMovieRatingsInsertion(String tableName, 
            ArrayList<MovieRating> ratings, PreparedStatement prepStatement) throws SQLException {
        // intialise query
        String query = "INSERT INTO capstone." + tableName + 
                " (USER_ID, MOVIE_ID, RATING_VAL) VALUES (?, ?, ?)";
        
        prepStatement = con.prepareStatement(query);
        
        // add values to query
        for (MovieRating rating : ratings) { 
            // get column values
            prepStatement.setInt(1, rating.getUserId()); 
            prepStatement.setInt(2, rating.getMovieId());
            prepStatement.setDouble(3, rating.getRatingValue());
                
            prepStatement.addBatch();
        }
        
        return prepStatement;
    }
}
