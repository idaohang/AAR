package datageneration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
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
            minDataPieces = Double.MAX_VALUE,
            minIdealDataCount = 10, // minimum required data count per user
            lessThanMinIdealCount = 0;

    /**
     * Merges some documents together into one file
     *
     * @param target What type of documents need to be created
     * @param profiles
     * @throws IOException
     * @throws SQLException
     */
    public MergeDocuments(String target, CapstoneDBConnection con, String[] profiles) throws IOException, SQLException {
        // set up MergeDocuments and then send to super constructor
        // call to super must be first operation called, so call a 'set up' method as a parameter
        // TODO - probably better to separate construction from functionality
        super(setUpReturnTarget(target, profiles), con); // super hack to the rescue!!!       
    }

    @Override
    protected void makeDocuments(String target) throws IOException, SQLException {
        // Create the directory in which to place the files
        createDirectory(target);

        // create local copy of user ids
        ResultSet userIds = getUserIds();

        // Iterate over all user IDs and generate their document
        while (userIds.next()) {
            ArrayList<String> userData = getAllUserData(userIds.getInt("USER_ID"));
            createUserDocument(userIds.getInt("USER_ID"), userData);
        }
    }

    @Override
    void createMetricsDocument() throws FileNotFoundException, IOException, SQLException {
        // Calculate and store the total number of users across the system
        totalUsers = countUsers();

        // initialise metric file
        File metricsDocument = new File(target + "/metrics.dat");

        // Create file and add metrics
        metricsDocument.createNewFile();
        try (PrintWriter writer = new PrintWriter(metricsDocument)) {
            writer.println("Total Users: " + (int) Math.round(totalUsers));
            writer.println("Total Data Pieces: " + (int) Math.round(totalDataCount));
            writer.println("Max Data: " + (int) Math.round(maxDataPieces));
            writer.println("Min Data: " + (int) Math.round(minDataPieces));
            writer.println("Average (Mean) Data: " + (totalDataCount / totalUsers));
            writer.println("% Users With Less Than " + (int) Math.round(minIdealDataCount)
                    + " Pieces Of Data: " + (lessThanMinIdealCount / totalUsers) * 100);
        }
    }

    /**
     * Collects and returns all the relevant data for a user
     *
     * @param userId the id of the user to collect data for
     * @return a shuffled ArrayList of data, with one piece of data at each index
     * @throws IOException
     */
    private ArrayList<String> getAllUserData(int userId) throws IOException {
        ArrayList<String> userData = new ArrayList<String>();

        // collect data from each profile
        for (String profile : profiles) {
            String line;

            // BufferedReader to parse over file text
            BufferedReader reader
                    = new BufferedReader(new FileReader(profile + "/" + userId + ".dat"));

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

        // check if this user has enough data, add to counter if necissary
        if (userDataCount < minIdealDataCount) {
            lessThanMinIdealCount++;
        }
    }

    /**
     * Sets up fields and then returns target to satisfy super constructor's first parameter
     * 'target'
     *
     * @param mergeTarget target for documents
     * @param mergeProfiles the profiles to be merged
     * @return
     */
    private static String setUpReturnTarget(String mergeTarget, String[] mergeProfiles) {
        // update fields, even though 'this' techinically doesn't exist yet
        target = mergeTarget;
        profiles = mergeProfiles;

        return mergeTarget;
    }
}
