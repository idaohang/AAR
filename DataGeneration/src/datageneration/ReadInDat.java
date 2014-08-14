/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datageneration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

/**
 * Prepare supplied dataset for processing.
 *
 * @author Clarky
 */
public class ReadInDat {
    // connection to the database
    private static final CapstoneDBConnection con = new CapstoneDBConnection();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Create the database and table(s)
        con.createDatabase();

        // Read and insert the data into the database
        readData("user_taggedmovies.dat");
        readData("tags.dat");

        // shut down connection
        con.shutDown();
    }

    /**
     * Read in data from file, split on tab character into array and insert required columns only
     *
     * @param location The location of the file to be read
     */
    public static void readData(String location) {
        // private variables
        String line; // Used to represent each line of the file as it is read
        PreparedStatement prepStatement = null;
        BufferedReader reader;

        try {
            // set up file reader
            reader = new BufferedReader(new FileReader(location));

            switch (location) {
                case "user_taggedmovies.dat":

                    // Prepared statement for movie tags
                    prepStatement = con.getConnection().prepareStatement("INSERT INTO "
                            + "capstone.movie_tags(USER_ID, MOVIE_ID, TAG_ID) VALUES (?, ?, ?)");

                    // Jump to the second line, skipping over column names
                    line = reader.readLine();

                    // Read the file line-by-line, 
                    // creating statements and adding to a batch insertion command
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split("\t");
                        prepStatement.setString(1, parts[0]);
                        prepStatement.setString(2, parts[1]);
                        prepStatement.setString(3, parts[2]);
                        prepStatement.addBatch();
                    }

                    break;

                case "tags.dat":

                    // Prepared statement for movie tags
                    prepStatement = con.getConnection().prepareStatement("INSERT INTO "
                            + "capstone.tags(TAG_ID, TAG_VAL) VALUES (?, ?)");

                    // Jump to the second line, skipping over column names
                    line = reader.readLine();

                    // Read the file line-by-line, 
                    // creating statements and adding to a batch insertion command
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split("\t");
                        prepStatement.setString(1, parts[0]);
                        prepStatement.setString(2, parts[1]);
                        prepStatement.addBatch();
                    }

                    break;
            }

            // Execute the batch insertion
            prepStatement.executeBatch();

        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        }
    }

}
