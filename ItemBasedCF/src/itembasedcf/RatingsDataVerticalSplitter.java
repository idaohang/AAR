package itembasedcf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Splits ratings data into two sections for use with IR metrics (precision/recall). One set of 
 * data will be used to generate recommendations, and the other will be used for a comparison. 
 * Data will be split vertically, meaning each user will have some data for comparison and some for 
 * recommendation. Both data sets will be output to the database.
 *
 * @author Jordan
 */
public class RatingsDataVerticalSplitter extends RatingsSplitter {
    private final double comparisonDataPercentage; // % of data that will be used for comparison

    /**
     * Constructor sets up private comparisonDataPercentage field
     * 
     * @param con connection to the database
     * @param comparisonDataPercentage the percentage of data to be used for comparison
     */
    public RatingsDataVerticalSplitter(Connection con, double comparisonDataPercentage) {
        super(con);
        this.comparisonDataPercentage = comparisonDataPercentage;
    }
    
    @Override
    public void createDbTables() throws ClassNotFoundException, SQLException {
        Statement statement = con.createStatement();

        // drop tables first
        String sql = "DROP TABLE IF EXISTS capstone.movie_ratings_compare";
        statement.executeUpdate(sql);
        sql = "DROP TABLE IF EXISTS capstone.movie_ratings_recommend";
        statement.executeUpdate(sql);
        
        // Create the table for recommendation data
        sql = "CREATE TABLE capstone.movie_ratings_recommend"
                + "(USER_ID INTEGER NOT NULL,"
                + "MOVIE_ID INTEGER NOT NULL,"
                + "RATING_VAL DECIMAL(2,1) NOT NULL,"
                + "PRIMARY KEY (USER_ID, MOVIE_ID));";
        statement.executeUpdate(sql);

        // Create the table for comparison data
        sql = "CREATE TABLE capstone.movie_ratings_compare"
                + "(USER_ID INTEGER NOT NULL,"
                + "MOVIE_ID INTEGER NOT NULL,"
                + "RATING_VAL DECIMAL(2,1) NOT NULL,"
                + "PRIMARY KEY (USER_ID, MOVIE_ID));";
        statement.executeUpdate(sql);
    }
    
    @Override
    public void fillDb() throws SQLException {
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
            PreparedStatement prepStatement = prepareMovieRatingsInsertion(
                    "movie_ratings_compare", comparisonData); // comparison
            prepStatement.executeBatch();
            
            prepStatement = prepareMovieRatingsInsertion(
                    "movie_ratings_recommend", recommenderData); // recommender
            prepStatement.executeBatch();
        }
    }

}
