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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        getUserIds();
        createDocuments();
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

            // create file for user
            userDocument.createNewFile();
            writer = new PrintWriter(userDocument);

            // write each tag to file, with one tag per line
            while (userTags.next()) {
                writer.println(userTags.getString("TAG_VAL"));
            }

            writer.close(); // close write

        } catch (IOException | SQLException ex) {
            ex.printStackTrace();
        }
    }
}
