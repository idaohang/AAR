package itembasedcf;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import org.grouplens.lenskit.ItemRecommender;
import org.grouplens.lenskit.RecommenderBuildException;
import org.grouplens.lenskit.core.LenskitConfiguration;
import org.grouplens.lenskit.core.LenskitRecommenderEngine;
import org.grouplens.lenskit.data.sql.JDBCRatingDAO;
import org.grouplens.lenskit.scored.ScoredId;

/**
 * Testing program for demonstrative purposes only (ie. rough).
 * Reads in a LenskitRecommenderEngine from file and makes recommendations
 *
 * @author Jordan
 */
public class ItemBasedCF {

    // Connection settings
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver",
                                DB_URL = "jdbc:mysql://localhost:3306/",
                                USER = "root",
                                PASS = "password";
    private static final Integer[] testingUsers = {127, 477}; // some testing users
    private static final Integer totalRecommendations = 10; // recommendations to make
    private static final String engineFileName = "engine.bin"; // file name for engine

    /**
     * Runs a test recommendation for a handful of users
     *
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     * @throws org.grouplens.lenskit.RecommenderBuildException
     */
    public static void main(String[] args)
            throws IOException, ClassNotFoundException, SQLException, RecommenderBuildException {
        // private variables
        Connection con; // connection to DB
        LenskitRecommenderEngine engine; // engine to be read from file
        ItemRecommender irec; // recommender for items
        JDBCRatingDAO dao = MovieRecommenderEngine.createDAO(); // data access object
        LenskitConfiguration dataConfig = new LenskitConfiguration(); // config for DAO free engine

        // set up connection
        Class.forName(JDBC_DRIVER);
        con = DriverManager.getConnection(DB_URL, USER, PASS);

        // add DAO to config
        dataConfig.addComponent(dao);

        // read in engine, and add data config
        engine = LenskitRecommenderEngine.newLoader()
                                         .addConfiguration(dataConfig)
                                         .load(new File(engineFileName));

        try {
            // set up recommender using engine and DAO
            irec = engine.createRecommender().getItemRecommender();

            // output recommendations
            for (Integer user : testingUsers) {
                makeRecommendations(user, totalRecommendations, irec);
            }

        } finally {
            con.close();
        }
    }

    /**
     * Gets the recommendations for a user and outputs to console
     *
     * @param userId the user to recommend items to
     * @param totalRecommendations the number of recommendations to make (ie. the N in top-N)
     * @param irec the ItemRecommender that contains the configuration and data for the system
     */
    private static void makeRecommendations(Integer userId, Integer totalRecommendations,
            ItemRecommender irec) {
        // Get top n recommendation for the user
        List<ScoredId> recs = irec.recommend(userId, totalRecommendations);

        System.out.format("Recommendations for %d:\n", userId);
        System.out.format("\tID\tScore\n");

        for (ScoredId item : recs) {
            System.out.format("\t%d\t%.2f\n", item.getId(), item.getScore());
        }
    }

}
