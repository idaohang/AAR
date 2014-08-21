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
import java.util.ArrayList;

/**
 * Outputs the unused tags to a .dat file
 * @author Jordan
 */
public class FindUnusedTags {
    // db con
    private static final CapstoneDBConnection con = new CapstoneDBConnection();

    public static void main(String[] args) throws SQLException, IOException {
        // get tags
        ResultSet movieTags = getMovieTags();
        ResultSet justTags = getJustTags();
        
        ArrayList<String> diff = getDifference(movieTags, justTags);
        
        outputDifferenceValues(diff);
        
        con.shutDown();
    }

    /**
     * Gets the tags users have used to tag movies
     * @return the tags
     * @throws SQLException 
     */
    private static ResultSet getMovieTags() throws SQLException {
        // Prepared statement for querying database
        PreparedStatement prepStatement;

        // SQL query to count the number of users
        prepStatement = con.getConnection().prepareStatement(
                "SELECT DISTINCT TAG_ID FROM capstone.movie_tags");

        // Execute the query and store the results
        return prepStatement.executeQuery();
    }
    
    /**
     * Gets all the tags
     * @return all the tags
     * @throws SQLException 
     */
    private static ResultSet getJustTags() throws SQLException {
        // Prepared statement for querying database
        PreparedStatement prepStatement;

        // SQL query to count the number of users
        prepStatement = con.getConnection().prepareStatement(
                "SELECT DISTINCT TAG_ID FROM capstone.tags");

        // Execute the query and store the results
        return prepStatement.executeQuery();
    }
    
    /**
     * Returns an array containing the difference between two result sets (b - a)
     * @param a a result set
     * @param b a comparison result set
     * @return ArrayList containing the difference
     * @throws SQLException 
     */
    private static ArrayList<String> getDifference(ResultSet a, ResultSet b) throws SQLException {
        ArrayList<String> aList = new ArrayList(),
                          bList = new ArrayList();

        // get all of a
        a.first();
        while (a.next()) {
            aList.add(a.getString(1));
        }

        // get all of b
        a.first();
        while (b.next()) {
            bList.add(b.getString(1));
        }

        // Find difference
        for (String s : aList) {
            bList.remove(s);
        }

        return bList;
    }

    /**
     * Writes all the unused tags to file
     * @param diff the unused tags
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws IOException 
     */
    private static void outputDifferenceValues(ArrayList<String> diff) throws SQLException, FileNotFoundException, IOException {
        PrintWriter writer;

        // Set path and name of file
        File doc = new File("tags_unused.dat");

        // Create file for user
        doc.createNewFile();
        writer = new PrintWriter(doc);
        
        writer.println("id\tvalue\t");

        for (String tagId : diff) {
            // Prepared statement for querying database
            PreparedStatement prepStatement;

            // SQL query to count the number of users
            prepStatement = con.getConnection().prepareStatement(
                    "SELECT TAG_VAL FROM capstone.tags WHERE TAG_ID = " + tagId);

            // Execute the query and store the results
            ResultSet rs = prepStatement.executeQuery();

            // Write each tag to file, with one tag per line
            while (rs.next()) {
                writer.println(tagId + "\t" + rs.getString("TAG_VAL"));
            }

        }

        // Close writer
        writer.close();
    }
}
