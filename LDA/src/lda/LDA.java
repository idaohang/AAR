package lda;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * Perform LDA algorithm
 *
 * @author Clarky
 */
public class LDA { // TODO: Automated copying/reading from ../DataGeneration/mergedTagCatProfile

    // Where the input for mallet is stored
    private static final String IMPORT_DIR = "mergedTagCatProfile";

    // Where Mallet can find the word stoplist
    private static final String STOPLIST_LOC = "lib/mallet-2.0/stoplists/en.txt";

    // The number of topics to generate
    private static final int NUM_TOPICS = 5;

    // The number of iterations to perform (Mallet devs advise 1000-2000)
    private static final int NUM_ITERATIONS = 1;

    // The number of concurrent threads
    private static final int NUM_THREADS = 2;

    /**
     * Main controller for LDA recommendation algorithm.
     *
     * @param args
     * @throws IOException
     * @throws Exception
     */
    public static void main(String[] args) throws IOException, Exception {

        // Get the names of all the files
        File folder = new File(IMPORT_DIR);
        File[] fileList = folder.listFiles();

        // Generate a topic model for each file (user)
        for (int i = 0; i < fileList.length; i++) {
            
            // Grab the directory and name of the file
            File f = new File(IMPORT_DIR + "/" + fileList[i].getName());
            
            // Read in the entire content of the file to string
            String content = new Scanner(new File(f.toString())).useDelimiter("\\Z").next();
            
            // Remove newlines from string (to conform to Mallet required format)
            content = content.replace("\n", " ").replace("\r", "");
            
            // Write the updated string back to the same file
            FileOutputStream fop = new FileOutputStream(f);
            byte[] contentInBytes = content.getBytes();
            fop.write(contentInBytes);
            fop.flush();
            fop.close();

            // Run topic modelling on the file
            TopicModel topics = new TopicModel(STOPLIST_LOC,
                    IMPORT_DIR + "/" + fileList[i].getName(),
                    NUM_TOPICS, NUM_ITERATIONS, NUM_THREADS);

        }
    }
}
