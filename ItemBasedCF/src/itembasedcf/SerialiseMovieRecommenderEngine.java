package itembasedcf;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import org.grouplens.lenskit.RecommenderBuildException;

/**
 * Serialises a MovieRecommenderEngine's engine to disk, for use with movie recommendations
 * 
 * @author Jordan
 */
public class SerialiseMovieRecommenderEngine {
    // private variables
    private static final Integer neighbourhoodSize = 30; // neighbourhood size for this engine
    private static final String engineFileName = "engine.bin"; // name of the file to be serialised
    
    /**
     * Creates a MovieRecommenderEngine and serialises it to disk
     * 
     * @param args the command line arguments
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     * @throws org.grouplens.lenskit.RecommenderBuildException
     * @throws java.io.IOException
     */
    public static void main(String[] args) 
            throws ClassNotFoundException, SQLException, RecommenderBuildException, IOException {
        // create new engine based on neighbourhood size
        MovieRecommenderEngine engine = new MovieRecommenderEngine(neighbourhoodSize);
        
        // write engine to disk
        engine.getEngine().write(new File(engineFileName));
    }
    
    
}
