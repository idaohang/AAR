/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package readindat;

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

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    private static final String PASS = "password";

    private static Statement statement;
    private static ResultSet result;
    private static Connection con;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {

            // Create the database and table(s)
            CreateDatabase();

            // Read and insert the data into the database
            readData("user_taggedmovies.dat");

            // Shut down database resources
            con.close();
            statement.close();
            //result.close(); // NOTE: NOT IN USE YET; CAUSES NULLPOINTER EXCEPTION WHEN EMPTY
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Read in data from file, split on tab character into array and insert required columns only
     *
     * @param location The location of the file to be read
     * @throws java.io.IOException if the file is not found
     * @throws java.sql.SQLException if an error occurs with the database
     */
    public static void readData(String location) throws IOException, SQLException {
        BufferedReader reader = new BufferedReader(new FileReader(location));

        // Used to represent each line of the file as it is read
        String line;

        // Prepared statement for movie tags
        PreparedStatement prepStatement = con.prepareStatement("INSERT INTO test.movie_tags("
                + "USER_ID, MOVIE_ID, TAG_ID) VALUES (?, ?, ?)");

        // Jump to the second line, skipping over column names
        line = reader.readLine();

        // Read the file line-by-line, creating statements and adding to a batch insertion command
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\t");
            prepStatement.setString(1, parts[0]);
            prepStatement.setString(2, parts[1]);
            prepStatement.setString(3, parts[2]);
            prepStatement.addBatch();
        }

        // Execute the batch insertion
        prepStatement.executeBatch();
    }

    /**
     * Create MySQL database and a table to store tag information from flat file
     */
    private static void CreateDatabase() {
        try {

            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            con = DriverManager.getConnection(DB_URL, USER, PASS);

            // Create the database
            statement = con.createStatement();
            String sql = "DROP DATABASE test";
            statement.executeUpdate(sql);
            sql = "CREATE DATABASE test";
            statement.executeUpdate(sql);

            // Create the table(s)
            statement = con.createStatement();
            sql = "CREATE TABLE test.movie_tags "
                    + "(USER_ID INTEGER NOT NULL, "
                    + "MOVIE_ID INTEGER NOT NULL, "
                    + "TAG_ID INTEGER NOT NULL, "
                    + "PRIMARY KEY(USER_ID, MOVIE_ID, TAG_ID))";
            statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
