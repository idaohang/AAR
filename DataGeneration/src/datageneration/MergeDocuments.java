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
 * Merges documents together to create an overall user profile across both tags and ratings in the
 * database.
 *
 * @author Jordan & Michael
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
            lessThanMinIdealCount = 0;
    private static double minIdealDataCount; // minimum required data count per user

    // users with at least the min required data cound
    private static ArrayList<Integer> idealUsers = new ArrayList<Integer>();

    /**
     * Merges some documents together into one file
     *
     * @param target What type of documents need to be created
     * @param con Connection to database
     * @param profiles Which profiles to include when merging
     * @param minIdealDataCount Min ideal data count for users to be considered
     * @throws IOException
     * @throws SQLException
     */
    public MergeDocuments(String target, CapstoneDBConnection con, String[] profiles,
            double minIdealDataCount) throws IOException, SQLException {
        
        // Set up MergeDocuments and then send to super constructor
        // Call to super must be first operation called, so call a 'set up' method as a parameter
        super(setUpReturnTarget(target, profiles, minIdealDataCount), con);       
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
     * Returns a list of the ideal users.
     *
     * @return all the ideal users
     */
    public static ArrayList<Integer> getIdealUsers() {
        return idealUsers;
    }

    /**
     * Collects and returns all the relevant data for a user.
     *
     * @param userId the id of the user to collect data for
     * @return a shuffled ArrayList of data, with one piece of data at each index
     * @throws IOException
     */
    private ArrayList<String> getAllUserData(int userId) throws IOException {
        ArrayList<String> userData = new ArrayList<>();

        // Collect data from each profile
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
        
        // Shuffle data
        Collections.shuffle(userData);

        return userData;
    }

    /**
     * Create document for a user if they have enough data
     *
     * @param userID the id of the user to create a document for
     * @param userData a list of the user's data
     * @throws IOException
     */
    private void createUserDocument(int userID, ArrayList<String> userData) throws IOException {
        PrintWriter writer;
        File userDocument;
        int userDataCount = userData.size(); // Total pieces of data for this user

        // If the user has more than the minimum acceptable data pieces, write their file
        if (!(userDataCount < minIdealDataCount)) {

            // Set path and name of file
            userDocument = new File(target + "/" + userID + ".dat");

            // Create file for user
            userDocument.createNewFile();
            writer = new PrintWriter(userDocument);

            // Write each tag to file, with one tag per line
            for (String dataPiece : userData) {
                writer.println(dataPiece);

                // Increment counters
                totalDataCount++;
            }

            // Increment users counter
            totalUsers++;

            // this is an ideal user, add to list
            idealUsers.add(userID);

            // Check if this user has the most or least data, if so set the max/min counters
            // TODO: Remove
            if (userDataCount > maxDataPieces) {
                maxDataPieces = userDataCount;
            } else if (userDataCount < minDataPieces) {
                minDataPieces = userDataCount;
            }

            // Close writer
            writer.close();
        }

        // check if this user has enough data, add to counter if necissary
        // TODO: Remove 
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
     * @return Target for merging
     */
    private static String setUpReturnTarget(String mergeTarget, String[] mergeProfiles,
            double idealDataCount) {
        
        // update fields, even though 'this' techinically doesn't exist yet
        target = mergeTarget;
        profiles = mergeProfiles;
        minIdealDataCount = idealDataCount;

        return mergeTarget;
    }
}
