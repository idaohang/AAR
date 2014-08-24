package userbasedcf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
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
import org.grouplens.lenskit.knn.user.UserUserItemScorer;
import org.grouplens.lenskit.scored.ScoredId;
import org.grouplens.lenskit.transform.normalize.MeanCenteringVectorNormalizer;
import org.grouplens.lenskit.transform.normalize.UserVectorNormalizer;
import org.grouplens.lenskit.transform.normalize.VectorNormalizer;

/**
 * Testing program based on lenskit docs for UserBasedCF
 * https://github.com/lenskit/lenskit/wiki
 * http://lenskit.org/apidocs/
 * 
 * @author Jordan
 */
public class UserBasedCF {

    /**
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        LenskitConfiguration config = new LenskitConfiguration();
        
        // Use user-user CF to score items
        config.bind(ItemScorer.class).to(UserUserItemScorer.class);
        
        // use item-user mean when user-user fails
        config.bind(BaselineScorer.class, ItemScorer.class).to(UserMeanItemScorer.class);
        config.bind(UserMeanBaseline.class, ItemScorer.class).to(ItemMeanRatingItemScorer.class);
        
        // normalize by subtracting the user's mean rating
        // for normalization, just center on user means
        config.within(UserVectorNormalizer.class).bind(VectorNormalizer.class).to(MeanCenteringVectorNormalizer.class);
        
        // set NeighborhoodSize to 30
        config.set(NeighborhoodSize.class).to(30);
        
        // UserSimilarity — compute similarities between users. The default implementation, 
        // [UserVectorSimilarity][], just compares the users' vectors using a vector similarity 
        // function; the default vector similarity is CosineVectorSimilarity.
        // No need to change config as we want Consine Similarity
        
        // build data access object (DAO)
        JDBCRatingDAOBuilder daoBuilder = JDBCRatingDAO.newBuilder(); // init builder
        
        // set up builder
        daoBuilder.setTableName("capstone.movie_ratings");
        daoBuilder.setItemColumn("MOVIE_ID");
        daoBuilder.setRatingColumn("RATING_VAL");
        daoBuilder.setUserColumn("USER_ID");
        daoBuilder.setTimestampColumn("USER_ID");
        
        // init DAO with con
        Connection con;
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "password");
        JDBCRatingDAO dao = daoBuilder.build(con);
        
        config.addComponent(dao); // add DAO to configuratioj
        
        // Now that we have a factory, build a recommender from the configuration
        // and data source. This will compute the similarity matrix and return a recommender
        // that uses it.
        Recommender rec = null;
        try {
            rec = LenskitRecommender.build(config);
        } catch (RecommenderBuildException e) {
            throw new RuntimeException("recommender build failed", e);
        }
        
        // we want to recommend items
        ItemRecommender irec = rec.getItemRecommender();
        assert irec != null; // not null because we configured one
        
        // for testing purposes, create some users to generate recomendations for
        ArrayList<Integer> users = new ArrayList<Integer>();
        users.add(75); // just some test users 
        users.add(78);
        
        // for users
        for (Integer user : users) {
            // get top 10 recommendation for the user
            List<ScoredId> recs = irec.recommend(user, 10);
            System.out.format("Recommendations for %d:\n", user);
            System.out.format("\tID\tScore\n");
            
            for (ScoredId item : recs) {
                System.out.format("\t%d\t%.2f\n", item.getId(), item.getScore());
            }
        }

    }

}
