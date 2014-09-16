package itembasedcf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Jordan
 */
public abstract class RatingsSplitter {
    protected Connection con; // db connection
    
    /**
     * Constructor sets up private connection field
     * @param con connection to the database
     */
    public RatingsSplitter(Connection con) {
        this.con = con;
    }
    
    /**
     * Creates tables in the DB for data to be added to
     *
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     */
    public abstract void createDbTables() throws ClassNotFoundException, SQLException;
    
    /**
     * Fills tables in the database with split rating information
     * 
     * @throws java.sql.SQLException
     */
    public abstract void fillDb() throws SQLException;
    
    /**
     * Gets all users as a result set
     * 
     * @return all the users
     * @throws SQLException 
     */
    protected ResultSet getUsers() throws SQLException {
        // Prepared statement to collect users
        PreparedStatement prepStatement;

        // SQL statement to collect distinct users
        prepStatement = con.prepareStatement(
                "SELECT DISTINCT USER_ID FROM capstone.movie_ratings_final");

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
    protected ResultSet getDataForUser(int userId) throws SQLException {
        // Prepared statement to collect data
        PreparedStatement prepStatement;

        // SQL statement to collect user's data
        prepStatement = con.prepareStatement(
                "SELECT * FROM capstone.movie_ratings_final WHERE USER_ID = " + userId);

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
    protected int countDataForUser(int userId) throws SQLException {
        // Prepared statement to count user's data
        PreparedStatement prepStatement;
        ResultSet rs;

        // SQL statement to count user's data
        prepStatement = con.prepareStatement(
                "SELECT COUNT(*) FROM capstone.movie_ratings_final WHERE USER_ID = " + userId);

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
    protected PreparedStatement prepareMovieRatingsInsertion(String tableName, 
            ArrayList<MovieRating> ratings) throws SQLException {
        // intialise query
        String query = "INSERT INTO capstone." + tableName + 
                " (USER_ID, MOVIE_ID, RATING_VAL) VALUES (?, ?, ?)";
        
        PreparedStatement prepStatement = con.prepareStatement(query);
        
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
