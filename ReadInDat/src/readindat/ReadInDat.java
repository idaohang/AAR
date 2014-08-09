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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Clarky
 */
public class ReadInDat {

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "test";
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
            CreateDatabase();
            readData("user_taggedmovies.dat");
        } catch (IOException ex) {
            Logger.getLogger(ReadInDat.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Read in data from file, split on tab character into array and print all as independent pieces
     *
     * @param location The location of the file to be read
     * @throws java.io.IOException
     */
    public static void readData(String location) throws IOException, SQLException {
        BufferedReader reader = new BufferedReader(new FileReader(location));
        String line;
        
        line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\t");
            
            statement = con.createStatement();
            String sql = "INSERT INTO test.movie_tags(USER_ID, MOVIE_ID, TAG_ID)"
                    + "VALUES (" + parts[0] + ", " + parts[1] + ", " + parts[2] + ")";
            statement.executeUpdate(sql);
        }
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
            String sql = "CREATE DATABASE test";
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
