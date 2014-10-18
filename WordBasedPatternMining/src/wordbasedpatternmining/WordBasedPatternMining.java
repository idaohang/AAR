package wordbasedpatternmining;

import Jama.Matrix;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Examine auto-generated files from Mallet execution and generate frequency matrix.
 *
 * @author Michael
 */
public class WordBasedPatternMining {

    // Number of header lines to skip when reading file
    private static final int SKIP_LINES = 3;

    // The minimum frequency to be accepted by the system (as selected by Yue)
    private static final int LOW_END_CUTOFF = 2;

    // Relative path to file location 
    private static final String FILE_PATH = "../LDA/mallet/topic-state.gz";

    private static int numTopics;

    // Reader object
    private static BufferedReader reader;

    // Data structure to store all information
    // HashMap<UserID, HashMap<TopicID, HashMap<WordID, HashMap<WordFreq, WordDist>>>>
    private static HashMap<String, HashMap<String, HashMap<String, Integer>>> users;

    // Writer object to output resultant structure to text file
    private static PrintWriter writer;

    private static HashMap<String, FrequencyMatrix> mat;

    // Database variables
    private static Connection con;
    private static String JDBC_DRIVER = "com.mysql.jdbc.Driver",
            DB_URL = "jdbc:mysql://localhost:3306/",
            USER = "root",
            PASS = "password";

    /**
     * Main entry point for word-based pattern mining project.
     *
     * @param args The command line arguments
     * @throws java.io.FileNotFoundException
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException,
            ClassNotFoundException, SQLException {

        // Track and determine the number of topics (instead of hard-coding value)
        numTopics = 0;

        // Grab and wrap .gz file
        reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(
                new FileInputStream(FILE_PATH))));

        // Instantiate HashMap structure for user/topic/word information
        users = new HashMap<>();
        mat = new HashMap<>();

        // Instantiate PrintWriter with output file name and charset
        writer = new PrintWriter("output.txt", "UTF-8");

        // Skip over header lines (straight to relevant data)
        for (int i = 0; i < SKIP_LINES; i++) {
            reader.readLine();
        }

        // Insert data into structure
        populateStructure();

        // Cut words if they do not occur frequently enough (unless cutting them empties the matrix)
        trimSize();

        // Calculate the largest matrix
        int biggest = calcBiggest();

        int count = 0;

        // Create matrix objects and write their form to text file
        for (Map.Entry currentUser : users.entrySet()) {
            count++;

            // Create, instantiate and add a Matrix object for each user
            mat.put(currentUser.toString(), new FrequencyMatrix(
                    (HashMap<String, HashMap<String, Integer>>) currentUser.getValue(),
                    numTopics, biggest));
        }

        // Open database connection
        Class.forName(JDBC_DRIVER);
        con = DriverManager.getConnection(DB_URL, USER, PASS);

        outputCosineSimilarity();

        con.close();
    }

    /**
     * Iterate through all users removing words that do not qualify for inclusion based on the field
     * <i>LOW_END_CUTOFF</i>
     */
    private static void trimSize() {
        for (Map.Entry user : users.entrySet()) {

            HashMap<String, HashMap<String, Integer>> userVal
                    = (HashMap<String, HashMap<String, Integer>>) user.getValue();

            userVal = enforceCutoff(userVal, LOW_END_CUTOFF);

            users.put((String) user.getKey(), userVal);
        }
    }

    /**
     * Remove words from structure that have less than a parameterised minimum value.
     *
     * This method is employed to help limit the size of the resultant matrices
     *
     * @param input Original HashMap structure without any items removed
     * @param cutoff The minimum frequency count to retain a place in the structure
     * @return Updated HashMap structure
     */
    private static HashMap<String, HashMap<String, Integer>> enforceCutoff(
            HashMap<String, HashMap<String, Integer>> input, int cutoff) {

        // If the cutoff is one or less the list won't change anyway, so just return it
        if (cutoff <= 1) {
            return input;
        } else {

            // Create output object to be populated (rather than editing existing object)
            HashMap<String, HashMap<String, Integer>> output = new HashMap<>();

            // Iterate through all topics
            for (Map.Entry topic : input.entrySet()) {

                // If the topic is not yet in the output structure, add it
                if (!(output.containsKey(topic.getKey()))) {
                    output.put((String) topic.getKey(), new HashMap<String, Integer>());
                }

                // Iterate through all words
                for (Map.Entry word : ((HashMap<String, Integer>) topic.getValue()).entrySet()) {

                    // If the word's frequency is high enough to be added, add it
                    if ((Integer) word.getValue() >= cutoff) {
                        output.get(topic.getKey()).put((String) word.getKey(),
                                (Integer) word.getValue());
                    }
                }
            }

            // If the resultant matrix is empty (or worse, negative size), just return the original
            if (getMaxWords(output) <= 0) {
                return input;
            } else {
                return output;
            }
        }
    }

    /**
     * Calculate and return the largest measure of words throughout the entire corpus of data from a
     * single user. This will analyse any given number of topics and return the most words contained
     * in any one topic.
     *
     * @param get The HashMap object to be analysed
     * @return The maximum number of words in a user's corpus of data
     */
    private static int getMaxWords(HashMap<String, HashMap<String, Integer>> get) {
        int counter = 0;

        // Iterate through all topics, counting number of words in each, returning largest count
        for (Map.Entry topic : get.entrySet()) {
            String t = (String) topic.getKey();

            // If the current topic contains more elements than the previous largest, replace value
            if (get.get(t).size() > counter) {
                counter = get.get(t).size();
            }
        }
        return counter;
    }

    /**
     * Read through parameterised file and programmatically insert values into data structure
     *
     * @throws IOException
     */
    private static void populateStructure() throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {

            // Split on space character
            String parts[] = line.split(" ");

            // The user's ID
            String user = parts[1].substring((parts[1].lastIndexOf('\\') + 1),
                    parts[1].lastIndexOf('.'));

            // The unique ID of the word
            String typeIndex = parts[3];

            // The unique ID of the topic the word belongs to
            String topicId = parts[5];

            // If the user does not yet exist in the structure, add them
            if (!users.containsKey(user)) {
                users.put(user, new HashMap<String, HashMap<String, Integer>>());
            }

            // If the topic does not yet exist in the structure, add it
            if (!(users.get(user).containsKey(topicId))) {
                users.get(user).put(topicId, new HashMap<String, Integer>());
            }

            // If the word does not yet exist in the structure, add it
            if (!(users.get(user).get(topicId).containsKey(typeIndex))) {
                users.get(user).get(topicId).put(typeIndex, 0);
            }

            // Increment the counter of occurences of the word
            users.get(user).get(topicId).put(typeIndex,
                    users.get(user).get(topicId).get(typeIndex) + 1);

            if (users.get(user).size() > numTopics) {
                numTopics = users.get(user).get(topicId).size();
            }
        }
    }

    /**
     * Get the maximum required size of the matrices.<p>
     *
     * Every word in the dataset is assigned a unique ID by Mallet at runtime. The IDs of these
     * words are used to place the word in a matrix (i.e. ID = index in topic row)
     * .<p>
     *
     * In order to facilitate comparisons, all matrices must be of the same format (size/shape).
     * This requires all matrices to be as wide as the largest ID present in the database, so the
     * highest ID must be found and passed to the matrices upon creation.
     *
     * @return The biggest ID found in the data
     */
    private static int calcBiggest() {
        int biggest = 0;

        // Iterate through all users
        for (Map.Entry userEntry : users.entrySet()) {
            HashMap<String, HashMap<String, Integer>> userVal
                    = (HashMap<String, HashMap<String, Integer>>) userEntry.getValue();

            // Iterate through all user's topics in structures
            for (Map.Entry topicEntry : userVal.entrySet()) {
                HashMap<String, Integer> topicVal
                        = (HashMap<String, Integer>) topicEntry.getValue();

                // Iterate through all user's topic's words and get the ID, testing for size
                for (Map.Entry wordEntry : topicVal.entrySet()) {
                    if (biggest < Integer.parseInt(wordEntry.getKey().toString())) {
                        biggest = Integer.parseInt(wordEntry.getKey().toString());
                    }
                }
            }
        }
        return biggest;
    }

    /**
     * Insert the cosine similarity calculations into the database
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private static void outputCosineSimilarity() throws ClassNotFoundException, SQLException {
        createCosineSimilarityTable();

        // Set up prepared statement
        PreparedStatement prepStatement = null;
        String query = "INSERT INTO "
                + "capstone.cosine_similarity(USER_ID, COMPARISON_USER_ID, SIMILARITY) "
                + "VALUES (?, ?, ?)";
        prepStatement = con.prepareStatement(query);

        // Compare all users to each other
        for (Map.Entry user : mat.entrySet()) {
            for (Map.Entry comparisonUser : mat.entrySet()) {
                // Check users are not the same
                if (user != comparisonUser) {
                    double totalCosineSimilarity = 0;

                    // Get users data as matrices
                    FrequencyMatrix userFreqMat = (FrequencyMatrix) user.getValue();
                    FrequencyMatrix compFreqMat = (FrequencyMatrix) comparisonUser.getValue();
                    double[][] userData = userFreqMat.getMatrix();
                    double[][] comparisonData = compFreqMat.getMatrix();

                    // Get cosine similarity between users for each topic
                    for (int i = 0; i < numTopics - 1; i++) {
                        // Create matrices for just this row of user data
                        Matrix userMatrix = new Matrix(userData[i], 1);
                        Matrix comparisonMatrix = new Matrix(comparisonData[i], 1);

                        // Compute cosine similarity
                        CosineSimilarity cos = new CosineSimilarity();
                        double thisSimilarity = cos.computeSimilarity(userMatrix, comparisonMatrix);

                        // Add this similarity to total if it returned as a number
                        if (!Double.isNaN(thisSimilarity)) {
                            totalCosineSimilarity += thisSimilarity;
                        }
                    }

                    double averageSimilarity = totalCosineSimilarity / numTopics;

                    // Get user IDs, not sure why .getKey() doesn't work straight out of the box...
                    String userKey = user.getKey().toString().substring(0,
                            user.getKey().toString().indexOf("="));
                    String comparisonKey = comparisonUser.getKey().toString().substring(0,
                            comparisonUser.getKey().toString().indexOf("="));

                    // add to prep statement
                    // this if statement is required as sometimes the comparison/user key is "metrics"
                    if (!comparisonKey.equals("metrics") && !userKey.equals("metrics")) {
                        prepStatement.setInt(1, Integer.parseInt(userKey));
                        prepStatement.setInt(2, Integer.parseInt(comparisonKey));
                        prepStatement.setDouble(3, averageSimilarity);
                        prepStatement.addBatch();
                    }
                }
            }

            prepStatement.executeBatch();

        }
    }

    /**
     * Creates a table to store cosine similarity between users
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private static void createCosineSimilarityTable() throws ClassNotFoundException, SQLException {
        Statement statement = con.createStatement();

        // Create the table
        String sql = "DROP TABLE IF EXISTS capstone.cosine_similarity";
        statement.executeUpdate(sql);

        sql = "CREATE TABLE capstone.cosine_similarity"
                + "(USER_ID INTEGER NOT NULL,"
                + "COMPARISON_USER_ID INTEGER NOT NULL,"
                + "SIMILARITY DECIMAL(30, 30) NOT NULL,"
                + "PRIMARY KEY (USER_ID, COMPARISON_USER_ID));";
        statement.executeUpdate(sql);
    }
}
