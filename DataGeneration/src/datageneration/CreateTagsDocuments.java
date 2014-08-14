package datageneration;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Creates a document for each user that shows all their used tags in a random order
 *
 * @author Jordan
 */
public class CreateTagsDocuments {

    // private variables

    private static final CapstoneDBConnection con = new CapstoneDBConnection(); // db connection
    private static ResultSet userIds; // all user IDs
    private static double totalUsers = 0, // doubles used to enable arithemetic
                          totalTags = 0,
                          maxTags = 0,
                          minTags = Double.MAX_VALUE; // default to max value

    /**
     * Export all the tag documents based upon information in the db
     */
    public static void exportDocuments() {
        getUserIds();
        createDocuments();
        createMetricsDocument();
        con.shutDown();
    }

    /**
     * Collects all the user IDs and stores them in userIds
     */
    private static void getUserIds() {
        PreparedStatement prepStatement; // prepared statement to collect user ids

        try {
            // Prepared statement for user ids
            prepStatement = con.getConnection().prepareStatement(
                    "SELECT DISTINCT USER_ID FROM capstone.movie_tags");

            userIds = prepStatement.executeQuery(); // collect IDs and store            

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Creates the tag documents for each user Documents consist of all a user's tags in a random
     * order
     */
    private static void createDocuments() {
        createDirectory();

        try {
            // create user documents
            while (userIds.next()) {
                int currentUserId = userIds.getInt("USER_ID");
                createUserDocument(currentUserId, getUserTags(currentUserId));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Creates the directory for user tag documents if it does not already exist
     */
    private static void createDirectory() {
        File tagsDirectory = new File("userTags");

        // if the directory does not exist then create it
        if (!tagsDirectory.exists()) {
            tagsDirectory.mkdir();
        }
    }

    /**
     * Collects all the tags for a user
     */
    private static ResultSet getUserTags(int currentUserId) {
        try {
            PreparedStatement prepStatement; // prepared statement to collect user tags

            // Prepared statement for collection of user tags, in random order
            prepStatement = con.getConnection().prepareStatement(
                    "SELECT TAG_VAL FROM capstone.movie_tags "
                    + "INNER JOIN capstone.tags "
                    + "ON capstone.movie_tags.TAG_ID = capstone.tags.TAG_ID "
                    + "WHERE USER_ID = " + currentUserId
                    + " ORDER BY rand()");

            return prepStatement.executeQuery(); // collect tags and r

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Creates a tags document for an individual user
     */
    private static void createUserDocument(int userId, ResultSet userTags) {
        try {
            PrintWriter writer;
            File userDocument = new File("userTags/" + userId + ".dat"); // user file
            int tagCounter = 0; // total tags this user has used

            // create file for user
            userDocument.createNewFile();
            writer = new PrintWriter(userDocument);

            // write each tag to file, with one tag per line
            while (userTags.next()) {
                writer.println(userTags.getString("TAG_VAL"));

                // increment counters
                tagCounter++;
                totalTags++;
            }

            writer.close(); // close writer

            // check if this user has the most or least tags
            if (tagCounter > maxTags) {
                maxTags = tagCounter;
            } else if (tagCounter < minTags) {
                minTags = tagCounter;
            }

        } catch (IOException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Counts the total number of users
     */
    private static void countUsers() {
        try {
            PreparedStatement prepStatement; // prepared statement to collect user tag
            ResultSet result; // the result of the query

            // Prepared statement for counting users
            prepStatement = con.getConnection().prepareStatement(
                    "SELECT COUNT(DISTINCT USER_ID) FROM capstone.movie_tags");

            result = prepStatement.executeQuery();
            result.first();
            totalUsers = result.getInt("COUNT(DISTINCT USER_ID)");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Creates a document containing some metrics relating to user tags
     */
    private static void createMetricsDocument() {
        countUsers();
        
        try {
            PrintWriter writer;
            File metricsDocument = new File("userTags/metrics.dat");

            // create file for user
            metricsDocument.createNewFile();
            writer = new PrintWriter(metricsDocument);

            // write out metrics
            writer.println("Total Users: " + (int)Math.round(totalUsers));
            writer.println("Total Tags: " + (int)Math.round(totalTags));
            writer.println("Max Tags: " + (int)Math.round(maxTags));
            writer.println("Min Tags: " + (int)Math.round(minTags));
            writer.println("Average (Mean) Tags: " + (totalTags/totalUsers));

            writer.close(); // close writer

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
