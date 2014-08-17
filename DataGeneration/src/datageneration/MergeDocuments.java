package datageneration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Merges documents together to create a user topic model
 * 
 * @author Jordan
 */
public class MergeDocuments extends Documents {
    // the names of the profiles to be merged
    private static String[] profiles;
    
    // folder location
    private static String target;
    
    // Metrics - Doubles used to enable arithemetic
    private static double totalUsers = 0,
            totalDataCount = 0,
            maxDataPieces = 0,
            minDataPieces = Double.MAX_VALUE;
        
    /**
     * Merges some documents together into one file
     * 
     * @param target What type of documents need to be created
     * @param profiles
     * @throws IOException
     * @throws SQLException 
     */
    public MergeDocuments(String target, String[] profiles) throws IOException, SQLException {    
        super(setUpMergeDocuments(target, profiles)); // super hack to the rescue!!!       
    }

    @Override
    protected void makeDocuments(String target) throws IOException, SQLException {
       // Create the directory in which to place the files
        createDirectory(target);
        
        // Iterate over all user IDs and generate their document
        while (userIds.next()) {
            ArrayList<String> userData = getAllUserData(userIds.getInt("USER_ID"));
            createUserDocument(userIds.getInt("USER_ID"), userData);
        }
    }

    @Override
    void createMetricsDocument() throws FileNotFoundException, IOException, SQLException {
        // TODO
    }
    
    /**
     * Collects and returns all the relevant data for a user
     * 
     * @param userId the id of the user to collect data for
     * @return a shuffled ArrayList of data, with one piece of data at each index
     * @throws IOException 
     */
    private ArrayList<String>getAllUserData(int userId) throws IOException {
        ArrayList<String> userData = new ArrayList<String>();
        
        // collect data from each profile
        for (String profile : profiles) {
            String line;

            // BufferedReader to parse over file text
            BufferedReader reader = 
                    new BufferedReader(new FileReader(profile + "/" + userId + ".dat"));
            
            // Read the file line-by-line and add contents of this profile to user data
            while ((line = reader.readLine()) != null) {
                userData.add(line);
            }
        }
        
        // shuffle data
        Collections.shuffle(userData);
        
        return userData;
    }
    
    private void createUserDocument(int userID, ArrayList<String> userData) throws IOException {
        PrintWriter writer;
        File userDocument;
        int userDataCount = 0; // Total pieces of data for this user
    
        // Set path and name of file
        userDocument = new File(target + "/" + userID + ".dat");

        // Create file for user
        userDocument.createNewFile();
        writer = new PrintWriter(userDocument);

        // Write each tag to file, with one tag per line
        for (String dataPiece : userData) {
            writer.println(dataPiece);
            // Increment counters
            userDataCount++;
            totalDataCount++;
        }
             
        // Close writer
        writer.close();
        
        // Check if this user has the most or least data, if so set the max/min counters
        if (userDataCount > maxDataPieces) {
            maxDataPieces = userDataCount;
        } else if (userDataCount < minDataPieces) {
            minDataPieces = userDataCount;
        }
    }
    
        
    /**
     * Sets up fields and then returns target to satisfy super constructor (pretty hacky I admit)
     */
    private static String setUpMergeDocuments(String mergeTarget, String[] mergeProfiles) {
        // update fields, even though 'this' techinically doesn't exist yet
        target = mergeTarget;
        profiles = mergeProfiles;
        
        return mergeTarget;
    }
}
