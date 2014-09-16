package itembasedcf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Splits the database horizontally for precision and recall generation. A number of tables will be 
 * produced, each with an equal of the users being excluded (eg. if totalSections = 5 --> table 1: 
 * first 20% excluded, table 2: second 20% excluded, etc.)
 * 
 * @author Jordan
 */
public class RatingsDataHorizontalSplitter extends RatingsSplitter {
    private double totalSections;

    /**
     * Constructor sets up private connection field
     * 
     * @param con connection to the database
     * @param totalSections the number of sections to split the data in to
     */
    public RatingsDataHorizontalSplitter(Connection con, int totalSections) {
        super(con);
        this.totalSections = totalSections;
    }

    
    @Override
    public void createDbTables() throws ClassNotFoundException, SQLException {
        Statement statement = con.createStatement();
        
        // Drop existing tables first, then add new ones
        for (int i = 1; i <= totalSections; i++) {
            String sql = "DROP TABLE IF EXISTS capstone.movie_ratings_" + i;
            statement.executeUpdate(sql);
            
            sql = "CREATE TABLE capstone.movie_ratings_" + i
                + "(USER_ID INTEGER NOT NULL,"
                + "MOVIE_ID INTEGER NOT NULL,"
                + "RATING_VAL DECIMAL(2,1) NOT NULL,"
                + "PRIMARY KEY (USER_ID, MOVIE_ID));";
            statement.executeUpdate(sql);
        }     
    }
    
    @Override
    public void fillDb() throws SQLException {
        // Get all user ids
        ResultSet rs = getUsers();
        
        // Convert result set to array
        ArrayList<Integer> allUsers = new ArrayList();
        
        while (rs.next()) {
            allUsers.add(rs.getInt("USER_ID"));
        }

        // Split array into appropriate number of sections
        List<List<Integer>> sectionsList = new LinkedList();
        
        for (int i = 1; i <= totalSections; i++) {
            sectionsList.add(allUsers.subList((int)((i - 1) / totalSections * allUsers.size()), 
                                              (int)(i / totalSections * allUsers.size())));
        }
        
        // Add data to database
        int currentTableIndex = 1; // table counter
        
        for (int i = 0; i < sectionsList.size(); i++) {
            // Add all users data except the section being excluded, ie. this section
            for (int j = 0; j < sectionsList.size(); j++) {
                
                if (i != j) {
                    for (Integer userId : sectionsList.get(j)) {
                        addToDatabase(currentTableIndex, userId);
                    }
                }      
            }
            currentTableIndex++; // this table is done
        }
    }

    /**
     * Adds a users information to the specified section of the database
     * 
     * @param currentTableIndex the index of the table to add data to
     * @param userId the users id for which information is to be added
     */
    private void addToDatabase(int currentTableIndex, Integer userId) throws SQLException {
        // intialise query with table name
        String query = "INSERT INTO capstone.movie_ratings_" + currentTableIndex + 
                " (USER_ID, MOVIE_ID, RATING_VAL) VALUES (?, ?, ?)";
        
        PreparedStatement prepStatement = con.prepareStatement(query);
        
        // collect user's data
        ResultSet userData = getDataForUser(userId);
        
        // add values to query
        while (userData.next()) {
            // get column values
            prepStatement.setInt(1, userData.getInt("USER_ID")); 
            prepStatement.setInt(2, userData.getInt("MOVIE_ID"));
            prepStatement.setDouble(3, userData.getDouble("RATING_VAL"));
                
            prepStatement.addBatch();
        }
        
        // Add this users data to the current table
        prepStatement.executeBatch();
    }

}
