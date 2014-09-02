package itembasedcf;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import static itembasedcf.MovieRecommenderEngine.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.grouplens.lenskit.ItemRecommender;
import org.grouplens.lenskit.RecommenderBuildException;
import org.grouplens.lenskit.core.LenskitConfiguration;
import org.grouplens.lenskit.core.LenskitRecommenderEngine;
import org.grouplens.lenskit.core.RecommenderConfigurationException;
import org.grouplens.lenskit.data.sql.JDBCRatingDAO;
import org.grouplens.lenskit.scored.ScoredId;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Reads in a LenskitRecommenderEngine from file and calculates precision/recall based upon a 
 * neighbourhood size and a number of recommendations. 
 * 
 * Fir recommendations will be found for each user. These recommendations will be used to calculate 
 * the precision and recall of each user's recommendations, as well as an average for this engine. 
 * If a LenskitRecommenderEngine for the requested neighbourhood size does not exist, then one will 
 * be created.
 *
 * @author Jordan
 */
public class ItemBasedCF {
    // DB connection
    private static Connection con;
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver",
                                DB_URL = "jdbc:mysql://localhost:3306/",
                                USER = "root",
                                PASS = "password";
    
    private static Integer totalRecommendations, // recommendations to make
                           neighbourhoodSize; // neighbour size to make recommendations from
    
    private static LenskitRecommenderEngine  engine; // engine to make reommendations from
    
    

    /**
     * Entry point for the recommender
     * 
     * @param args
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     * @throws java.io.IOException
     * @throws org.grouplens.lenskit.core.RecommenderConfigurationException
     */
    public static void main(String[] args) throws ClassNotFoundException, SQLException, 
            IOException, RecommenderConfigurationException, RecommenderBuildException {
        
        ItemRecommender irec;
        double totalPrecision = 0, 
               totalRecall = 0; // precision/recall for each user added together 
        
        // set slf4j logger to only show errors
        Logger root = (Logger) getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.ERROR);
        
        // Set up db connection
        Class.forName(JDBC_DRIVER);
        con = DriverManager.getConnection(DB_URL, USER, PASS);
        
        // Set up engine based on user input
        getUserInput();
        createEngineIfNecessary();
        
        // Set up recommender using engine and DAO, and get item recommender   
        System.out.println("Creating Item Recommender...");
        irec = createItemRecommender();
        System.out.println("DONE\n");

        // Get all users
        ResultSet users = getUsers();

        System.out.println("Generating Precision/Recall Documents...");
        
        initialiseDirectories();
        
        // Iterate over all users and calculate precision and recall
        while (users.next()) {          
            // create PrecisionRecall for this user based upon their recommendations 
            // and a comparison set of data
            PrecisionRecall pr = new PrecisionRecall(
                    getMovieRecommendations(users.getInt("USER_ID"), irec), 
                    getComparisonDataSet(users.getInt("USER_ID")));
            
            // get precision and recall for this user
            double precision = pr.getPrecision();
            double recall = pr.getRecall();
            
            // output precision and recall to a file
            outputUserMetrics(users.getInt("USER_ID"), precision, recall);
            
            // add to global totals for precision and recall
            totalPrecision += precision;
            totalRecall += recall;
        }
        
        // output the averrage metrics for this recommendation engine
        outputAverageMetrics(totalPrecision, totalRecall);
        
        System.out.println("DONE\n");
        
        con.close();
    }
    
    /**
     * Prompts user for total recommendations to make and neighbourhood size, stores in private 
     * variables
     */
    private static void getUserInput() {
        Scanner in = new Scanner(System.in);

        // Get user input for number of recommendations and neighbourhood size
        System.out.print("Neighbourhood Size: ");
        neighbourhoodSize = in.nextInt();
        System.out.print("\nNumber of Recommendations: ");
        totalRecommendations = in.nextInt();
        System.out.println("\n");
    }
    
    
    /**
     * Creates an engine file if one does not already exist
     * 
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws SQLException
     * @throws RecommenderBuildException 
     */
    private static void createEngineIfNecessary() 
            throws ClassNotFoundException, IOException, SQLException, RecommenderBuildException {
        // engine file for this CF operation
        File engineFile = new File(START_ENGINE_NAME + neighbourhoodSize + END_ENGINE_NAME);
        
        // check for existance of file
        if(!engineFile.exists()) {
            // Create new engine based on neighbourhood size, and write to file
            System.out.println("Creating New Engine...");
            MovieRecommenderEngine newEngine = new MovieRecommenderEngine(neighbourhoodSize);
            newEngine.writeToFile();
            System.out.println("DONE\n");
        } else {
            System.out.println("Engine Already Exists\n");
        }   
    }
        
    /**
     * Creates a recommender, based on engine file, and returns it's item recommender
     * 
     * @return the item recommender
     * @throws IOException
     * @throws RecommenderConfigurationException
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
    private static ItemRecommender createItemRecommender() throws IOException, 
            RecommenderConfigurationException, ClassNotFoundException, SQLException {
        
        JDBCRatingDAO dao = MovieRecommenderEngine.createDAO(); // data access object
        LenskitConfiguration dataConfig = new LenskitConfiguration(); // config for DAO free engine
        ItemRecommender ir;
        
        // Add DAO to config
        dataConfig.addComponent(dao);

        // Read in engine based on file name, and add data config
        engine = LenskitRecommenderEngine.newLoader().addConfiguration(dataConfig)
                .load(new File(START_ENGINE_NAME + neighbourhoodSize + END_ENGINE_NAME));
        
        ir = engine.createRecommender().getItemRecommender();
        
        // create recommender and return item recommender
        return ir;
    }
    
    /**
     * TODO class to hold all common database interactions
     * Gets all users as a result set
     * 
     * @return all the users
     * @throws SQLException 
     */
    private static ResultSet getUsers() throws SQLException {
        // Prepared statement to collect users
        PreparedStatement prepStatement;

        // SQL statement to collect distinct users
        prepStatement = con.prepareStatement(
                "SELECT DISTINCT USER_ID FROM capstone.movie_ratings_final");

        // Execute query and return results
        return prepStatement.executeQuery();
    }
    
    /**
     * Extracts just the movie IDs to be recommended to a user
     * 
     * @param userId the user to make recommendations for
     * @param irec the item recommender for this recommendation
     * @return the recommended movie's ids
     */
    private static ArrayList<Long> getMovieRecommendations(Integer userId, 
            ItemRecommender irec) {
        
        ArrayList<Long> movies = new ArrayList();

        // Get ScoredId recommendations
        List<ScoredId> recs = irec.recommend(userId, totalRecommendations);

        // Pull out just ratings from ScoredIds
        for (ScoredId item : recs) {
            movies.add(item.getId());
        }
        
        return movies;
    }
    
    /**
     * Gets the comparison data from the DB for a user
     * 
     * @param userId the id of the user to get the comparison set for
     * @return the comparison movie's ids
     */
    private static ArrayList<Long> getComparisonDataSet(Integer userId) throws SQLException {
        ResultSet rs;
        ArrayList<Long> movieIds = new ArrayList();
        PreparedStatement prepStatement; 

        // SQL statement to collect distinct users
        prepStatement = con.prepareStatement(
                "SELECT MOVIE_ID FROM capstone.movie_ratings_compare WHERE USER_ID = " + userId);

        // Execute query and return results
        rs = prepStatement.executeQuery();
        
        // Iterate over each row in result set and add movie id to array list
        while (rs.next()) {
            movieIds.add(rs.getLong("MOVIE_ID"));
        }
        
        return movieIds;
    }

    /**
     * Creates a directory for metric files to go in to
     */
    private static void initialiseDirectories() {
        File metricsDirectory = new File("metrics");
        File engineDirectory = new File("metrics/top" + totalRecommendations + 
                "neighbours" + neighbourhoodSize + "/");
        

        // If the directories do not exist then create them
        if (!metricsDirectory.exists()) {
            metricsDirectory.mkdir();
        }
        
        if (!engineDirectory.exists()) {
            engineDirectory.mkdir();
        }
    }
    
    /**
     * Creates a file containing the metrics for a user
     * 
     * @param userId the id of the user to create a file for
     * @param precision the precision of this users recommendations
     * @param recall the recall of this users recommendations
     * @throws FileNotFoundException
     * @throws IOException 
     */
    private static void outputUserMetrics(int userId, double precision, double recall) 
            throws FileNotFoundException, IOException {
        // file to be written to for user
        File userDocument = new File("metrics/top" + totalRecommendations + 
                "neighbours" + neighbourhoodSize + "/" + userId + ".dat");
        PrintWriter writer;

        // Create file for the metrics
        userDocument.createNewFile();
        writer = new PrintWriter(userDocument);
        
        // add metrics to file
        writer.println("User Id: " + userId);
        writer.println("Precision: " + precision);
        writer.println("Recall: " + recall);
        
        writer.close();
    }

    /**
     * Creates a file containing the average metrics for this recommendation engine
     * 
     * @param totalPrecision the total precision of all users
     * @param totalRecall the total recall of all users
     * @throws FileNotFoundException
     * @throws IOException
     * @throws SQLException 
     */
    private static void outputAverageMetrics(double totalPrecision, double totalRecall) 
            throws FileNotFoundException, IOException, SQLException {
                // file to be written to for user
        File metricsDocument = new File("metrics/top" + totalRecommendations + 
                "neighbours" + neighbourhoodSize + "/average.dat");
        PrintWriter writer;
        double userCount;

        // Create file for the metrics
        metricsDocument.createNewFile();
        writer = new PrintWriter(metricsDocument);
        
        // count all users
        userCount = countUsers();
        
        // add metrics to file
        writer.println("Total Recommendations: " + totalRecommendations);
        writer.println("Neighbourhood Size: " + neighbourhoodSize);
        writer.println("Avg. Precision: " + totalPrecision / userCount);
        writer.println("Avg. Recall: " + totalRecall / userCount);
        
        writer.close();
    }

    /**
     * Counts the number of users found in the movie_ratings_final table
     *
     * @return The number of users found
     * @throws SQLException
     */
    private static double countUsers() throws SQLException {
        // Prepared statement for querying database
        PreparedStatement prepStatement;

        // Store the results of the query
        ResultSet result;

        // SQL query to count the number of users
        prepStatement = con.prepareStatement(
                "SELECT COUNT(DISTINCT USER_ID) FROM capstone.movie_ratings_final");

        // Execute the query and store the results
        result = prepStatement.executeQuery();

        // Ensure resultset is set to the first record
        result.first();

        // Return the number of users
        return result.getInt("COUNT(DISTINCT USER_ID)");    
    }
    
}
