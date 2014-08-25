/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datageneration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Create and maintain a connection with the database server
 *
 * @author Jordan & Michael
 */
public class CapstoneDBConnection {

    // Connection strings
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    private static final String PASS = "password";

    private static Statement statement;
    private static Connection con;

    /**
     * Create connection to the capstone database
     */
    public CapstoneDBConnection() {
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Create connection
            con = DriverManager.getConnection(DB_URL, USER, PASS);

        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Returns connection to the capstone database
     *
     * @return the connection
     */
    public Connection getConnection() {
        return con;
    }

    /**
     * Create MySQL database and a table to store tag information from flat file
     */
    public void createDatabase() {
        try {
            // Create the database
            statement = con.createStatement();
            String sql = "DROP DATABASE IF EXISTS capstone";
            statement.executeUpdate(sql);
            sql = "CREATE DATABASE capstone";
            statement.executeUpdate(sql);

            // Create the table(s)
            statement = con.createStatement();
            sql = "CREATE TABLE capstone.tags "
                    + "(TAG_ID INTEGER NOT NULL, "
                    + "TAG_VAL VARCHAR(100) NOT NULL, "
                    + "PRIMARY KEY(TAG_ID));";
            statement.executeUpdate(sql);

            sql = "CREATE TABLE capstone.movie_tags "
                    + "(USER_ID INTEGER NOT NULL, "
                    + "MOVIE_ID INTEGER NOT NULL, "
                    + "TAG_ID INTEGER NOT NULL, "
                    + "PRIMARY KEY(USER_ID, MOVIE_ID, TAG_ID));";
            statement.executeUpdate(sql);

            sql = "CREATE TABLE capstone.movie_genres"
                    + "(MOVIE_ID INTEGER NOT NULL,"
                    + "GENRE_VAL VARCHAR(100) NOT NULL,"
                    + "PRIMARY KEY (MOVIE_ID, GENRE_VAL));";
            statement.executeUpdate(sql);

            // Create the movie ratings table (all users)
            sql = "CREATE TABLE capstone.movie_ratings"
                    + "(USER_ID INTEGER NOT NULL,"
                    + "MOVIE_ID INTEGER NOT NULL,"
                    + "RATING_VAL DECIMAL(2,1) NOT NULL,"
                    + "PRIMARY KEY (USER_ID, MOVIE_ID));";
            statement.executeUpdate(sql);
            
            // Create the final movie ratings table (only for users with enough data)
            sql = "CREATE TABLE capstone.movie_ratings_final"
                    + "(USER_ID INTEGER NOT NULL,"
                    + "MOVIE_ID INTEGER NOT NULL,"
                    + "RATING_VAL DECIMAL(2,1) NOT NULL,"
                    + "PRIMARY KEY (USER_ID, MOVIE_ID));";
            statement.executeUpdate(sql);

            statement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Shuts down database resources
     */
    public void shutDown() {
        try {
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }
}
