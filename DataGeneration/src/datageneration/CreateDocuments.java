/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datageneration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Abstract class templates each of the different forms of data the system will extract from the
 * database. 
 * @author Clarky
 */
public abstract class CreateDocuments {
    
    // Instance connection to the database
    static final CapstoneDBConnection con = new CapstoneDBConnection();
    
    // An SQL resultset of unique IDs of each user
    static ResultSet userIds;

    /**
     * Extract, analyse and export data from database. This constructor functions more as a main
     * method (calling methods in order) than a typical object constructor which initialises fields.
     * @param target What type of documents need to be created
     * @throws IOException 
     * @throws SQLException
     */
    public CreateDocuments(String target) throws IOException, SQLException {
        
        // Query and store user IDs
        getUserIds();
        
        // Generate a document for each user from the targeted dataset
        makeDocuments(target);
        
        // Analyse the returned data and calculate simple metrics
        createMetricsDocument();
        
        // Close the connection to the database
        con.shutDown();
    }

    /**
     * Counts the number of users found in the movie_tags table
     * @return The number of users found
     * @throws SQLException
     */
    int countUsers() throws SQLException {
        
        // Prepared statement for querying database
        PreparedStatement prepStatement;
        
        // Store the results of the query
        ResultSet result;
        
        // SQL query to count the number of users
        prepStatement = con.getConnection().prepareStatement(
                "SELECT COUNT(DISTINCT USER_ID) FROM capstone.movie_tags");
        
        // Execute the query and store the results
        result = prepStatement.executeQuery();
        
        // Ensure resultset is set to the first record
        result.first();
        
        // Return the number of users
        return result.getInt("COUNT(DISTINCT USER_ID)");
    }

    /**
     * Creates the documents for each user for the given target data. Each document consists of all
     * a user's artifacts in a random order
     * @param target The type of documents to be generated
     * @throws IOException 
     */
    private void makeDocuments(String target) throws IOException {
        
        // Create the directory in which to place the files
        createDirectory(target);

        try {
            
            // Iterate over all user IDs and generate their document
            while (userIds.next()) {
                int currentUserId = userIds.getInt("USER_ID");
                createUserDocument(currentUserId, getUserArtifacts(currentUserId));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Creates the directory for user tag documents if it does not already exist
     * @param target 
     */
    private static void createDirectory(String target) {
        File directory;
        
        // Depending on the target data, name the folder differently
        if (target.equals("tag")) {
            directory = new File("userTags");
        } else {
            directory = new File("userCats");
        }
        
        // If the directory does not exist then create it
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    /**
     * Collects all the user IDs and stores them in the userIds resultset
     */
    private void getUserIds() {
        // Prepared statement to query for user IDs
        PreparedStatement prepStatement;

        try {
            prepStatement = con.getConnection().prepareStatement(
                    "SELECT DISTINCT USER_ID FROM capstone.movie_tags");
            userIds = prepStatement.executeQuery();          

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Query the database for data about each user and return a set of results
     * @param uID The user's ID
     * @return A ResultSet object of query results
     * @throws SQLException 
     */
    abstract ResultSet getUserArtifacts(int uID) throws SQLException;

    /**
     * Generate, populate and store the document for each user
     * @param uID The user's ID
     * @param result A ResultSet object of query results
     * @throws IOException 
     * @throws SQLException 
     */
    abstract void createUserDocument(int uID, ResultSet result) throws IOException, SQLException;

    /**
     * Generate the document for the overall metrics of the data. The metrics include:
     * The number of users, the largest amount of data for any one user, the smallest amount of data
     * for any one user and the average amount of data across the entire userbase.
     * @throws FileNotFoundException File or filepath not found
     * @throws IOException
     * @throws SQLException 
     */
    abstract void createMetricsDocument() throws FileNotFoundException, IOException, SQLException ;
}
