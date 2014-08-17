package datageneration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Abstract class templates each of the different forms of data the system will extract from the
 * database.
 *
 * @author Clarky
 */
public abstract class CreateDocuments extends Documents {

    /**
     * Extract, analyse and export data from database. This constructor functions more as a main
     * method (calling methods in order) than a typical object constructor which initialises fields.
     *
     * @param target What type of documents need to be created
     * @throws IOException
     * @throws SQLException
     */
    public CreateDocuments(String target) throws IOException, SQLException {
        super(target);
    }

    /**
     * Creates the documents for each user for the given target data. Each document consists of all
     * a user's artifacts in a random order
     *
     * @param target The type of documents to be generated
     * @throws IOException
     */
    @Override
    protected void makeDocuments(String target) throws IOException, SQLException {
        // Create the directory in which to place the files
        createDirectory(target);

        // Iterate over all user IDs and generate their document
        while (userIds.next()) {
            int currentUserId = userIds.getInt("USER_ID");
            createUserDocument(currentUserId, getUserArtifacts(currentUserId));
        }
    }

    /**
     * Creates the directory for user tag documents if it does not already exist
     *
     * @param target What type of documents need to be created
     */
    @Override
    protected void createDirectory(String target) {
        // Depending on the target data, name the folder differently
        switch (target) {
            case "tag":
                super.createDirectory("userTags");
                break;

            case "cat":
                super.createDirectory("userCats");
                break;
        }
    }

    /**
     * Query the database for data about each user and return a set of results
     *
     * @param uID The user's ID
     * @return A ResultSet object of query results
     * @throws SQLException
     */
    abstract ResultSet getUserArtifacts(int uID) throws SQLException;

    /**
     * Generate, populate and store the document for each user
     *
     * @param uID The user's ID
     * @param result A ResultSet object of query results
     * @throws IOException
     * @throws SQLException
     */
    abstract void createUserDocument(int uID, ResultSet result) throws IOException, SQLException;

}
