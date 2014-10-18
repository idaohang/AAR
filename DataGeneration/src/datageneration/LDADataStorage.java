package datageneration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Store output of LDA execution in the database.<p>
 * 
 * This class is a utility class and not part of the main data generation class' functionality.
 * 
 * @author Jordan & Michael
 */
public class LDADataStorage {

    // Database connection instance
    private static CapstoneDBConnection con;

    // Files to insert into database
    private static final String keysFile = "../LDA/mallet/data_keys.txt";
    private static final String compositionFile = "../LDA/mallet/data_composition.txt";

    public static void main(String[] args) throws SQLException {

        // Connection instance
        con = new CapstoneDBConnection();

        // Create tables for LDA data
        con.createLDATables();

        PreparedStatement prepStatement = null;

        prepStatement = setupPreparedStatement("INSERT INTO "
                + "capstone.LDA_keys(TOPIC, DIRICHLET_PARAMETER, DATA_PIECES) "
                + "VALUES (?, ?, ?)", keysFile, prepStatement);

        prepStatement.executeBatch();

        prepStatement = setupPreparedStatement("INSERT INTO "
                + "capstone.LDA_composition(USER_ID, TOPIC_ID, TOPIC_DISTRIBUTION) "
                + "VALUES (?, ?, ?)", compositionFile, prepStatement);

        prepStatement.executeBatch();

    }

    /**
     * Dynamically generate prepared statement based on parameters
     *
     * @param query The SQL query string
     * @param location The filepath to be written to
     * @param prepStatement Statement object to be returned
     * @return
     */
    private static PreparedStatement setupPreparedStatement(String query, String location,
            PreparedStatement prepStatement) {
        try {
            String line;
            BufferedReader reader = new BufferedReader(new FileReader(location));
            int columns = query.length() - query.replace("?", "").length();
            prepStatement = con.getConnection().prepareStatement(query);

            line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");

                switch (location) {
                    case keysFile:
                        for (int i = 1; i <= columns; i++) {
                            prepStatement.setString(i, parts[i - 1]);
                        }
                        prepStatement.addBatch();
                        break;
                    case compositionFile:

                        // Extract user ID
                        String userID = parts[1];
                        String[] userNameParts = parts[1].split("/");
                        userID = userNameParts[userNameParts.length - 1];
                        userID = userID.substring(0, userID.indexOf("."));

                        // Don't process metrics
                        if (userID.equals("metrics")) {
                            break;
                        } else {

                            // Set user ID in prepared statement
                            prepStatement.setString(1, userID);

                            // Track where (in the split row of results) we are currently looking
                            int point = 2;

                            // For the length of the split results, assign every other result 
                            // between each of topic_id and topic_distribution
                            while (point < parts.length) {
                                prepStatement.setString(2, parts[point]);
                                point++;
                                prepStatement.setString(3, parts[point]);
                                point++;

                                prepStatement.addBatch();
                            }
                            break;
                        }
                }
            }
            return prepStatement;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return prepStatement;
    }
}
