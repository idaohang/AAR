package recommendationevaluation;

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
import org.grouplens.lenskit.knn.user.UserUserItemScorer;
import org.grouplens.lenskit.transform.normalize.BaselineSubtractingUserVectorNormalizer;
import org.grouplens.lenskit.transform.normalize.MeanCenteringVectorNormalizer;
import org.grouplens.lenskit.transform.normalize.UserVectorNormalizer;
import org.grouplens.lenskit.transform.normalize.VectorNormalizer;

/**
 * Creates a RecommenderEngine for movie recommendations.
 *
 * @author Jordan
 */
public class MovieRecommenderEngine {

    // Connection settings
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver",
            DB_URL = "jdbc:mysql://localhost:3306/",
            USER = "root",
            PASS = "password";

    // Algorithms available to user
    private static final int ITEM_BASED_CF = 1,
            USER_BASED_CF = 2,
            LDA = 3, // latent dirichlet allocation
            WPM = 4; // word-based pattern mining

    // Data sources: 1st - ratings data, 2nd - LDA data, 3rd - WPM data
    private static final String dataSources[][] = new String[][]{
        {"capstone.movie_ratings_final", "MOVIE_ID", "RATING_VAL", "USER_ID"},
        {"capstone.lda_composition", "TOPIC_ID", "TOPIC_DISTRIBUTION", "USER_ID"},
        {"capstone.cosine_similarity", "COMPARISON_USER_ID", "SIMILARITY", "USER_ID"}};

    // dataSources indexes for algorithms
    private static final int DATA_RATINGS = 0,
            DATA_LDA = 1,
            DATA_WPM = 2;

    // Indexes for items within dataSources
    private static final int TABLE_NAME = 0,
            ITEM_COLUMN = 1,
            RATING_COLUMN = 2,
            USER_COLUMN = 3;

    // The actual LenskitRecommenderEngine encapsulated
    LenskitRecommenderEngine engine;

    // The size of the neghbourhood
    Integer neighbourhoodSize;

    /**
     * Constructor for a MovieRecommenderEngine creates a RecommenderEngine based upon a
     * neighbourhood size.
     *
     * @param neighbourhoodSize The size of the neighbourhood for this RecommenderEngine
     * @param algorithm Which algorithm to generate recommendations from
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     * @throws org.grouplens.lenskit.RecommenderBuildException
     */
    public MovieRecommenderEngine(Integer neighbourhoodSize, Integer algorithm)
            throws ClassNotFoundException, SQLException, RecommenderBuildException {

        this.neighbourhoodSize = neighbourhoodSize;

        LenskitConfiguration config = createConfiguration(neighbourhoodSize, algorithm);

        engine = createRecommenderEngine(config, algorithm);
    }

    /**
     * Writes this recommender engine to file.
     *
     * @param algorithm The algorithm used for this engine
     * @throws java.io.IOException
     */
    public void writeToFile(Integer algorithm) throws IOException {

        // Create folder for engines if doesn't already exist
        File dir = new File("RecommendationEvaluation/engines");

        if (!dir.exists()) {
            dir.mkdir();
        }

        // Serialise engine
        engine.write(new File(getEngineName(algorithm, neighbourhoodSize)));
    }

    /**
     * Gets the name of an engine file based upon an algorithm and neighbourhood size.
     *
     * @param algorithm The algorithm as an index
     * @param neighbourhoodSize The neighbourhood size
     * @return The name of the engine
     */
    public static String getEngineName(Integer algorithm, Integer neighbourhoodSize) {
        return "RecommendationEvaluation/engines/engine-" + getAlgorithmName(algorithm) + 
                "neighbours" + neighbourhoodSize + ".bin";
    }

    /**
     * Returns the engine encapsulated by this class.
     *
     * @return the movie recommender engine
     */
    public LenskitRecommenderEngine getEngine() {
        return engine;
    }

    /**
     * Gets the name of an algorithm based upon it's index
     *
     * @param algorithm The index of the algorithm a name is required for
     * @return The name of the algorithm
     */
    public static String getAlgorithmName(int algorithm) {
        String algorithmName = "";

        if (algorithm == MovieRecommenderEngine.ITEM_BASED_CF) {
            algorithmName = "ItemBasedCF";
        } else if (algorithm == MovieRecommenderEngine.USER_BASED_CF) {
            algorithmName = "UserBasedCF";
        } else if (algorithm == MovieRecommenderEngine.LDA) {
            algorithmName = "LDA";
        } else if (algorithm == MovieRecommenderEngine.WPM) {
            algorithmName = "WPM";
        }
        return algorithmName;
    }

    /**
     * Creates and returns a Data Access Object for the database
     *
     * @param algorithm which algorithm to generate recommendations from
     * @return the DAO
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static JDBCRatingDAO createDAO(Integer algorithm)
            throws ClassNotFoundException, SQLException {

        // private variables
        Connection con; // connection to db
        JDBCRatingDAO dao; // data access object
        JDBCRatingDAOBuilder daoBuilder; // data builder        

        // Build data access object (DAO) and set up columns
        daoBuilder = JDBCRatingDAO.newBuilder();

        // Determine which data source to use and set up daoBuilder
        if (algorithm == ITEM_BASED_CF || algorithm == USER_BASED_CF) {
            // both CF methods use ratings data
            daoBuilder.setTableName(dataSources[DATA_RATINGS][TABLE_NAME]);
            daoBuilder.setItemColumn(dataSources[DATA_RATINGS][ITEM_COLUMN]);
            daoBuilder.setRatingColumn(dataSources[DATA_RATINGS][RATING_COLUMN]);
            daoBuilder.setUserColumn(dataSources[DATA_RATINGS][USER_COLUMN]);

        } else if (algorithm == LDA) {
            daoBuilder.setTableName(dataSources[DATA_LDA][TABLE_NAME]);
            daoBuilder.setItemColumn(dataSources[DATA_LDA][ITEM_COLUMN]);
            daoBuilder.setRatingColumn(dataSources[DATA_LDA][RATING_COLUMN]);
            daoBuilder.setUserColumn(dataSources[DATA_LDA][USER_COLUMN]);

        } else if (algorithm == WPM) {
            daoBuilder.setTableName(dataSources[DATA_WPM][TABLE_NAME]);
            daoBuilder.setItemColumn(dataSources[DATA_WPM][ITEM_COLUMN]);
            daoBuilder.setRatingColumn(dataSources[DATA_WPM][RATING_COLUMN]);
            daoBuilder.setUserColumn(dataSources[DATA_WPM][USER_COLUMN]);
        }

        daoBuilder.setTimestampColumn(null); // no timestamps

        // Initialise DAO with connection and DAO builder
        Class.forName(JDBC_DRIVER);
        con = DriverManager.getConnection(DB_URL, USER, PASS);
        dao = daoBuilder.build(con);

        return dao;
    }

    /**
     * Creates and returns a Data Access Object (DAO) for the ratings data in the database
     *
     * @return The DAO
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static JDBCRatingDAO createItemDAO() throws ClassNotFoundException, SQLException {
        return createDAO(ITEM_BASED_CF); // item-based CF uses ratings data
    }

    /**
     * Sets up and returns a LenskitConfiguration for item-based CF
     *
     * @param neighbourhoodSize The size of the neighbourhood for this configuration
     * @param algorithm Which algorithm to use
     * @return The configuration
     */
    private static LenskitConfiguration createConfiguration(Integer neighbourhoodSize,
            Integer algorithm) throws ClassNotFoundException, SQLException {

        LenskitConfiguration config = new LenskitConfiguration(); // config for recommender

        // determine which configuration to use
        if (algorithm == ITEM_BASED_CF) {
            // Only Item-based CF uses item-item recommender configuration
            config.bind(ItemScorer.class).to(ItemItemScorer.class);
            config.bind(BaselineScorer.class, ItemScorer.class).to(ItemMeanRatingItemScorer.class);
            config.bind(UserVectorNormalizer.class).to(BaselineSubtractingUserVectorNormalizer.class);

        } else {
            // User-user configuration for all other algorithms
            config.bind(ItemScorer.class).to(UserUserItemScorer.class);
            config.bind(BaselineScorer.class, ItemScorer.class).to(UserMeanItemScorer.class);
            config.bind(UserMeanBaseline.class, ItemScorer.class).to(ItemMeanRatingItemScorer.class);
            config.within(UserVectorNormalizer.class).bind(VectorNormalizer.class).to(MeanCenteringVectorNormalizer.class);

        }

        // Set neighbourhood size
        config.set(NeighborhoodSize.class).to(neighbourhoodSize);

        return config;
    }

    /**
     * Builds and returns a RecommenderEngine that is free from a DAO
     *
     * @param config The configuration containing only recommendation configuration (not DAO)
     * @param algorithm Which algorithm to generate recommendations from
     * @return The DAO free RecommenderEngine
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws RecommenderBuildException
     */
    private LenskitRecommenderEngine createRecommenderEngine(LenskitConfiguration config,
            Integer algorithm) throws ClassNotFoundException, SQLException,
            RecommenderBuildException {

        // Private variables
        JDBCRatingDAO dao = createDAO(algorithm);

        // Config to hold DAO
        LenskitConfiguration dataConfig = new LenskitConfiguration();
        LenskitRecommenderEngine recommenderEngine;

        dataConfig.addComponent(dao);

        // Build engine, with placeholder DAOs
        recommenderEngine = LenskitRecommenderEngine.newBuilder().addConfiguration(config)
                .addConfiguration(dataConfig, ModelDisposition.EXCLUDED).build();

        return recommenderEngine;
    }
}
