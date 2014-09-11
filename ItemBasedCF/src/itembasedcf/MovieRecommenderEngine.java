package itembasedcf;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.grouplens.lenskit.ItemScorer;
import org.grouplens.lenskit.RecommenderBuildException;
import org.grouplens.lenskit.baseline.BaselineScorer;
import org.grouplens.lenskit.baseline.ItemMeanRatingItemScorer;
import org.grouplens.lenskit.baseline.UserMeanBaseline;
import org.grouplens.lenskit.baseline.UserMeanItemScorer;
import org.grouplens.lenskit.core.LenskitConfiguration;
import org.grouplens.lenskit.core.LenskitRecommenderEngine;
import org.grouplens.lenskit.core.ModelDisposition;
import org.grouplens.lenskit.data.sql.JDBCRatingDAO;
import org.grouplens.lenskit.data.sql.JDBCRatingDAOBuilder;
import org.grouplens.lenskit.knn.NeighborhoodSize;
import org.grouplens.lenskit.knn.item.ItemItemScorer;
import org.grouplens.lenskit.transform.normalize.BaselineSubtractingUserVectorNormalizer;
import org.grouplens.lenskit.transform.normalize.UserVectorNormalizer;

/**
 * Creates a RecommenderEngine for movie recommendations
 *
 * @author Jordan
 */
public class MovieRecommenderEngine {
    // public constants
    public static final String START_ENGINE_NAME = "engine-", // start of file name
                               END_ENGINE_NAME = "neighbours.bin"; // end of file name
    
    // Connection settings
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver",
                                DB_URL = "jdbc:mysql://localhost:3306/",
                                USER = "root",
                                PASS = "password";

    LenskitRecommenderEngine engine; // the actual LenskitRecommenderEngine encapsulated
    
    Integer neighbourhoodSize; // the size of the neghbourhood

    /**
     * Constructor for a MovieRecommenderEngine creates a RecommenderEngine based upon a
     * neighbourhood size
     *
     * @param neighbourhoodSize the size of the neighbourhood for this RecommenderEngine
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     * @throws org.grouplens.lenskit.RecommenderBuildException
     */
    public MovieRecommenderEngine(Integer neighbourhoodSize)
            throws ClassNotFoundException, SQLException, RecommenderBuildException {

        this.neighbourhoodSize = neighbourhoodSize;
        
        LenskitConfiguration config = createConfiguration(neighbourhoodSize);

        engine = createRecommenderEngine(config);
    }
    
    /**
     * Writes this recommender engine to file
     * 
     * @throws java.io.IOException
     */
    public void writeToFile() throws IOException {
        engine.write(new File(START_ENGINE_NAME + neighbourhoodSize + END_ENGINE_NAME));
    }

    /**
     * Returns the engine encapsulated by this class
     *
     * @return the movie recommender engine
     */
    public LenskitRecommenderEngine getEngine() {
        return engine;
    }
    
    
    /**
     * Creates and returns a Data Access Object for the database
     *
     * @return the DAO
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static JDBCRatingDAO createDAO() throws ClassNotFoundException, SQLException {
        // private variables
        Connection con; // connection to db
        JDBCRatingDAO dao; // data access object
        JDBCRatingDAOBuilder daoBuilder; // data builder        

        // Build data access object (DAO) and set up columns
        daoBuilder = JDBCRatingDAO.newBuilder();

        daoBuilder.setTableName("capstone.movie_ratings_recommend");
        daoBuilder.setItemColumn("MOVIE_ID");
        daoBuilder.setRatingColumn("RATING_VAL");
        daoBuilder.setUserColumn("USER_ID");
        daoBuilder.setTimestampColumn(null); // no timestamp

        // Initialise DAO with connection and DAO builder
        Class.forName(JDBC_DRIVER);
        con = DriverManager.getConnection(DB_URL, USER, PASS);
        dao = daoBuilder.build(con);

        return dao;
    }

    /**
     * Sets up and returns a LenskitConfiguration for item-item CF
     *
     * @param neighbourhoodSize
     * @return the configuration
     */
    private static LenskitConfiguration createConfiguration(Integer neighbourhoodSize)
            throws ClassNotFoundException, SQLException {

        LenskitConfiguration config = new LenskitConfiguration(); // config for recommender

        // Use item-item CF to score items
        config.bind(ItemScorer.class).to(ItemItemScorer.class);
        
        // Set up baseline predictor
        config.bind(BaselineScorer.class, ItemScorer.class).to(ItemMeanRatingItemScorer.class);
    
        // Use the baseline for normalizing user ratings
        config.bind(UserVectorNormalizer.class).to(BaselineSubtractingUserVectorNormalizer.class);

        // Set number of neighbours
        config.set(NeighborhoodSize.class).to(neighbourhoodSize);

        return config;
    }

    /**
     * Builds and returns a RecommenderEngine that is free from a DAO
     *
     * @param config the configuration containing only recommendation configuration (not DAO)
     * @return the DAO free RecommenderEngine
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws RecommenderBuildException
     */
    private LenskitRecommenderEngine createRecommenderEngine(LenskitConfiguration config)
            throws ClassNotFoundException, SQLException, RecommenderBuildException {

        // private variables
        JDBCRatingDAO dao = createDAO();
        LenskitConfiguration dataConfig = new LenskitConfiguration(); // config to hold dao
        LenskitRecommenderEngine recommenderEngine;

        dataConfig.addComponent(dao);

        // build engine, with placeholder DAOs
        recommenderEngine = LenskitRecommenderEngine.newBuilder().addConfiguration(config)
                .addConfiguration(dataConfig, ModelDisposition.EXCLUDED).build();

        return recommenderEngine;
    }

}
