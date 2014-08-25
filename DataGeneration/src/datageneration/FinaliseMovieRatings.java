package datageneration;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Updates the DB to reflect ideal users and their rated movies (ie. users with enough data)
 * 
 * @author Jordan
 */
public class FinaliseMovieRatings {
    private final CapstoneDBConnection con;
    
    /**
     * Create a finalise movie ratings object and sets its connection
     * 
     * @param con 
     */
    public FinaliseMovieRatings(CapstoneDBConnection con) {
        this.con = con;
    }
    
    /**
     * Add only ideal users to final movie rating table
     */
    public void createFinalRatingsTable() {
        // get ideal users
        ArrayList<Integer> userIdsToKeep = MergeDocuments.getIdealUsers();
        
        for (Integer userId : userIdsToKeep) {
            try {
                String query;
                PreparedStatement prepStatement;
                
                // insert data for ideal users, just take data from existing ratings table
                query = "INSERT INTO capstone.movie_ratings_final " + 
                        "SELECT * FROM capstone.movie_ratings WHERE USER_ID = " + userId;
                prepStatement = con.getConnection().prepareStatement(query);
                prepStatement.executeUpdate();
                
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
