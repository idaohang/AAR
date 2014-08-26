package itembasedcf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import org.grouplens.lenskit.ItemRecommender;
import org.grouplens.lenskit.ItemScorer;
import org.grouplens.lenskit.Recommender;
import org.grouplens.lenskit.RecommenderBuildException;
import org.grouplens.lenskit.baseline.BaselineScorer;
import org.grouplens.lenskit.baseline.ItemMeanRatingItemScorer;
import org.grouplens.lenskit.baseline.UserMeanBaseline;
import org.grouplens.lenskit.baseline.UserMeanItemScorer;
import org.grouplens.lenskit.core.LenskitConfiguration;
import org.grouplens.lenskit.core.LenskitRecommender;
import org.grouplens.lenskit.data.sql.JDBCRatingDAO;
import org.grouplens.lenskit.data.sql.JDBCRatingDAOBuilder;
import org.grouplens.lenskit.knn.NeighborhoodSize;
import org.grouplens.lenskit.knn.item.ItemItemScorer;
import org.grouplens.lenskit.scored.ScoredId;
import org.grouplens.lenskit.transform.normalize.BaselineSubtractingUserVectorNormalizer;
import org.grouplens.lenskit.transform.normalize.UserVectorNormalizer;

/**
 * Item-item Collaborative Filtering recommendation system for movies
 * 
 * Uses lenskit for CF logic:
 * https://github.com/lenskit/lenskit/wiki
 * http://lenskit.org/apidocs/
 * 
 * @author Jordan
 */
public class ItemBasedCF {
    
    /**
     * Runs a test recommendation for a handful of users
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // change these parameters to alter recommendation
        Integer neighbourhoodSize = 30, // how many users in a neighbourhood
                totalRecommendations = 10; // total recomendations to make
        Integer [] testingUsers = {127, 477}; // some testing users
        
        LenskitConfiguration config = createConfiguration(neighbourhoodSize);
        
        ItemRecommender irec = createRecommender(config);
                
        // output recommendations
        for (Integer user : testingUsers) {
            makeRecommendations(user, totalRecommendations, irec);
        }

    }
    
    /**
     * Sets up and returns a LenskitConfiguration for item-item CF
     * @param neighbourhoodSize
     * @return the configuration
     */
    private static LenskitConfiguration createConfiguration(Integer neighbourhoodSize) {
        try {
            LenskitConfiguration config = new LenskitConfiguration(); // config for recommender
            Connection con; // connection to db
            JDBCRatingDAO dao; // data access object
            JDBCRatingDAOBuilder daoBuilder; // data builder
            
            // Use item-item CF to score items
            config.bind(ItemScorer.class).to(ItemItemScorer.class);
            
            // Let's use personalised mean rating as the baseline/fallback predictor.
            // 2-step process:
            // First, use the user mean rating as the baseline scorer
            config.bind(BaselineScorer.class, ItemScorer.class).to(UserMeanItemScorer.class);
            
            // Second, use the item mean rating as the base for user means
            config.bind(UserMeanBaseline.class, ItemScorer.class).
                    to(ItemMeanRatingItemScorer.class);
            
            // and normalize ratings by baseline prior to computing similarities
            config.bind(UserVectorNormalizer.class).
                    to(BaselineSubtractingUserVectorNormalizer.class);
            
            // Set number of neighbours
            config.set(NeighborhoodSize.class).to(neighbourhoodSize);
            
            // Build data access object (DAO) and set up columns
            daoBuilder = JDBCRatingDAO.newBuilder();
            
            daoBuilder.setTableName("capstone.movie_ratings_final");
            daoBuilder.setItemColumn("MOVIE_ID");
            daoBuilder.setRatingColumn("RATING_VAL");
            daoBuilder.setUserColumn("USER_ID");
            daoBuilder.setTimestampColumn("USER_ID"); // no timestamp
            
            // Initialise DAO with connection
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "password");
            dao = daoBuilder.build(con);
            
            config.addComponent(dao); // add DAO to configuration

            return config;
            
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
        
        return null;       
    }
    
    /**
     * Create an item recommender using a configuration
     * @param config the configuration to use
     * @return an item recommender to make recommendations
     */
    private static ItemRecommender createRecommender(LenskitConfiguration config) {
        // Build a recommender from the configuration
        Recommender rec = null;
        
        try {
            rec = LenskitRecommender.build(config);
        } catch (RecommenderBuildException e) {
            throw new RuntimeException("recommender build failed", e);
        }
        
        // We want to recommend items
       return rec.getItemRecommender();
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
