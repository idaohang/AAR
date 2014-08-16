package datageneration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Creates a document for each user that shows all their used tags in a random order
 *
 * @author Jordan & Michael
 */
public class CreateTagsDocuments extends CreateDocuments {

    // Doubles used to enable arithemetic
    private static double totalUsers = 0,
            totalTags = 0,
            maxTags = 0,
            minTags = Double.MAX_VALUE;

    // Constructor; only calls superconstructor
    CreateTagsDocuments(String target) throws IOException, SQLException {
        super(target);
    }

    @Override
    ResultSet getUserArtifacts(int currentUserId) throws SQLException {

        // Prepared statement to collect user tags
        PreparedStatement prepStatement;

        // SQL statement for collection of user tags, in random order
        prepStatement = con.getConnection().prepareStatement(
                "SELECT TAG_VAL FROM capstone.movie_tags "
                + "INNER JOIN capstone.tags "
                + "ON capstone.movie_tags.TAG_ID = capstone.tags.TAG_ID "
                + "WHERE USER_ID = " + currentUserId
                + " ORDER BY rand()");

        // Execute query and return results
        return prepStatement.executeQuery();
    }

    @Override
    void createUserDocument(int uID, ResultSet result) throws IOException, SQLException {
        PrintWriter writer;

        // Set path and name of file
        File userDocument = new File("userTags/" + uID + ".dat");

        // Total tags this user has used
        int tagCounter = 0;

        // Create file for user
        userDocument.createNewFile();
        writer = new PrintWriter(userDocument);

        // Write each tag to file, with one tag per line
        while (result.next()) {
            writer.println(result.getString("TAG_VAL"));

            // Increment counters
            tagCounter++;
            totalTags++;
        }

        // Close writer
        writer.close();

        // Check if this user has the most or least tags, if so set the max/min counters
        if (tagCounter > maxTags) {
            maxTags = tagCounter;
        } else if (tagCounter < minTags) {
            minTags = tagCounter;
        }
    }

    @Override
    void createMetricsDocument() throws FileNotFoundException, IOException, SQLException {

        // Calculate and store the total number of users across the system
        totalUsers = countUsers();

        File metricsDocument = new File("userCats/metrics.dat");

        // Create file for the metrics
        metricsDocument.createNewFile();
        try (PrintWriter writer = new PrintWriter(metricsDocument)) {
            writer.println("Total Users: " + (int) Math.round(totalUsers));
            writer.println("Total Tags: " + (int) Math.round(totalTags));
            writer.println("Max Tags: " + (int) Math.round(maxTags));
            writer.println("Min Tags: " + (int) Math.round(minTags));
            writer.println("Average (Mean) Tags: " + (totalTags / totalUsers));
        }
    }
}