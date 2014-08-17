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
 * @author Jordan & Michael
 */
public class ReadInDat {

    // Connection to the database
    private static final CapstoneDBConnection con = new CapstoneDBConnection();

    /**
     * Read in an array of .dat files and close connection to db when done
     *
     * @param files all the files to be read in as Strings
     */
    public static void importTagData(String[] files) {
        // Read and insert the data into the database
        for (String thisFile : files) {
            readData(thisFile);
        }

        // shut down connection
        con.shutDown();
    }

    /**
     * Read in data from file, split on tab character into array and insert required columns only
     *
     * @param location The location of the file to be read
     */
    public static void readData(String location) {
        try {
            // PreparedStatement to pass query string to the database
            PreparedStatement prepStatement = null;

            // Depending on the provided name and path of the file, read differently
            switch (location) {
                case "user_taggedmovies.dat":
                    prepStatement = setupPreparedStatement("INSERT INTO "
                            + "capstone.movie_tags(USER_ID, MOVIE_ID, TAG_ID) VALUES (?, ?, ?)",
                            location,
                            prepStatement);
                    break;

                case "tags.dat":
                    prepStatement = setupPreparedStatement("INSERT INTO "
                            + "capstone.tags(TAG_ID, TAG_VAL) VALUES (?, ?)",
                            location,
                            prepStatement);
                    break;

                case "movie_genres.dat":
                    prepStatement = setupPreparedStatement("INSERT INTO "
                            + "capstone.movie_genres(MOVIE_ID, GENRE_VAL) VALUES (?, ?)",
                            location,
                            prepStatement);
                    break;
            }

            prepStatement.executeBatch();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Sets up and returns a prepared statement for batch queries on the database by reading in data
     * from file
     *
     * @param query the query to execute
     *
     * @param location the location of the file to be read from
     * @param prepStatement the prepared statement object
     * @return
     */
    private static PreparedStatement setupPreparedStatement(String query, String location,
            PreparedStatement prepStatement) {
        try {
            // String to represent each line of the file as it is read
            String line;

            // BufferedReader to parse over file text
            BufferedReader reader = new BufferedReader(new FileReader(location));

            // count the number of columns that require data (ie. count of "?" in query)
            int columns = query.length() - query.replace("?", "").length();

            // Prepared statement for movie tags
            prepStatement = con.getConnection().prepareStatement(query);

            // Jump to the second line, skipping over column names
            line = reader.readLine();

            // Read the file line-by-line, 
            // creating statements and adding to a batch insertion command
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");

                for (int i = 1; i <= columns; i++) {
                    prepStatement.setString(i, normaliseStrings(parts[i - 1]));
                }

                prepStatement.addBatch();
            }

            return prepStatement;

        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Format strings as required by Mallet.
     *
     * @param input Original string
     * @return Correctly formatted string
     */
    private static String normaliseStrings(String input) {

        // Capitalise first letter
        String output = input.substring(0, 1).toUpperCase() + input.substring(1);

        // Remove all spaces
        output = output.replaceAll("\\s", "");

        // Remove all other non-word character (punctuation etc.)
        output = output.replaceAll("\\W", "");

        return output;
    }
}
