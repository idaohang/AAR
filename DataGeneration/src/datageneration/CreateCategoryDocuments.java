/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datageneration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Creates a document for each user that shows all the categories of movies they've watched in a
 * random order
 *
 * @author Jordan & Michael
 */
public class CreateCategoryDocuments extends CreateDocuments {

    // Doubles used to enable arithemetic
    private static double totalUsers = 0,
            totalCats = 0,
            maxCats = 0,
            minCats = Double.MAX_VALUE;

    // Constructor; only calls superconstructor
    public CreateCategoryDocuments(String target) throws IOException, SQLException {
        super(target);
    }

    @Override
    ResultSet getUserArtifacts(int uID) throws SQLException {
        
        // Prepared statement to collect user tags
        PreparedStatement prepStatement;

        // SQL statement for collection of user tags, in random order
        prepStatement = con.getConnection().prepareStatement(
                "SELECT GENRE_VAL FROM capstone.movie_genres "
                + "INNER JOIN capstone.movie_tags "
                + "ON capstone.movie_genres.MOVIE_ID = capstone.movie_tags.MOVIE_ID "
                + "WHERE USER_ID = " + uID
                + " ORDER BY rand()");
        
        // Execute query and return results
        return prepStatement.executeQuery();
    }

    @Override
    void createUserDocument(int uID, ResultSet result) throws IOException, SQLException {
        PrintWriter writer;
        
        // Set path and name of new file
        File userDocument = new File("userCats/" + uID + ".dat");
        
        // Total categories viewed by this user 
        int catCounter = 0;

        // Create file for user
        userDocument.createNewFile();
        writer = new PrintWriter(userDocument);

        // Write each tag to file, with one category per line
        while (result.next()) {
            writer.println(result.getString("GENRE_VAL"));

            // Increment counters
            catCounter++;
            totalCats++;
        }
        
        // Close writer
        writer.close();

        // Check if this user has the most or least categories, if so set the max/min counters
        if (catCounter > maxCats) {
            maxCats = catCounter;
        } else if (catCounter < minCats) {
            minCats = catCounter;
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
            writer.println("Total Tags: " + (int) Math.round(totalCats));
            writer.println("Max Tags: " + (int) Math.round(maxCats));
            writer.println("Min Tags: " + (int) Math.round(minCats));
            writer.println("Average (Mean) Tags: " + (totalCats / totalUsers));
        }
    }

}