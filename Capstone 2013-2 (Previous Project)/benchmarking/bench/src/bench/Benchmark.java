package bench;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author	James Pyke 05090946 <j.pyke@connect.qut.edu.au>
 * @version	1.0
 */
public class Benchmark {

    // tests to run, ie. {5, 10, 15} to test top 5, top 10, top 15
    // should be ordered from lowest to highest
    static final int[] TESTS = {1, 2, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60};
    // number of articles to use to generate comparisons
    static final int NUM_REFERENCE_ARTICLES = 2;
    // minimum number of articles a user can view to be used for testing
    static final int MIN_VIEWED_ARTICLES = NUM_REFERENCE_ARTICLES + TESTS[0];

    /**
     * Receive command line input and check number of parameters
     * @param args the command line arguments
     */
    public static void main(String[] args) throws NoSuchAlgorithmException,
            UnsupportedEncodingException, FileNotFoundException, SQLException {
        if (args.length == 6) {
            bench(args);
        } else {
            System.out.println("ERROR: program takes 6 arguments:");
            System.out.println("    database server name");
            System.out.println("    database name");
            System.out.println("    database username");
            System.out.println("    database user password");
            System.out.println("    path of output file");
            System.out.println("    \"avg\" or \"sum\"");
        }
    }

    /**
     * Run the tests
     *
     * @param args
     */
    public static void bench(String[] args) throws NoSuchAlgorithmException,
            UnsupportedEncodingException, FileNotFoundException, SQLException {
        ConnectMSSQL dbConnection = new ConnectMSSQL(args[0], args[1], args[2], args[3]);
        ArrayList<Integer> users = dbConnection.getUserIDs();

        // open output file and write header
        PrintWriter writer = new PrintWriter(args[4], "UTF-8");
        writer.print("userID");
        for (int test : TESTS) {
            writer.print(";matches" + test + ";precision" + test + ";recall" + test);
        }
        writer.println("");

        // iterate through test users
        for (int i = 0; i < users.size(); i++) {

            // get user's articles
            ArrayList<Integer> viewedArticles = dbConnection.getViewedArticles(users.get(i));

            // put some (NUM_REFERENCE_ARTICLES) separate
            int[] referenceArticles = new int[NUM_REFERENCE_ARTICLES];
            for (int j = 0; j < NUM_REFERENCE_ARTICLES; j++) {
                int referenceArticleIndex = GetPseudoRandom(users.get(i), viewedArticles.size());
                referenceArticles[j] = viewedArticles.get(referenceArticleIndex);
                viewedArticles.remove(referenceArticleIndex);
            }

            // get recommendations based on those separated ones
            ArrayList<Integer> recommendedArticles = dbConnection.getRecommendedArticles(referenceArticles, TESTS[TESTS.length - 1], args[5]);

            // run tests and write to file
            if (recommendedArticles.size() >= TESTS[TESTS.length - 1]) {
                writer.print(users.get(i));
                for (int test : TESTS) {
                    int matches = 0;
                    for (int j = 0; j < test; j++) {
                        for (Integer viewedArticle : viewedArticles) {
                            if (viewedArticle.equals(recommendedArticles.get(j))) {
                                matches++;
                                break;
                            }
                        }
                    }

                    double precision = (double) matches / (double) recommendedArticles.size();
                    double recall = (double) matches / (double) viewedArticles.size();

                    writer.print(";" + matches + ";" + precision + ";" + recall);
                }
                writer.println("");
            }

            // report on progress every 5th user tested
            if (i % 5 == 4) {
                System.out.println((i + 1) + " users tested");
            }
        }
        writer.close();
    }

    /**
     * get pseudorandom number x where 0 <= x < max. uses hash of the seed and
     * max so is deterministic and repeatable, though modulo gives a (probably
     * negligible) bias towards lower numbers
     *
     * @param seed
     * @param max
     * @return
     */
    private static int GetPseudoRandom(int seed, int max) throws NoSuchAlgorithmException {
        String hashThis = seed + " " + max;
        byte[] hash = MessageDigest.getInstance("MD5").digest(hashThis.getBytes());
        return (((hash[0] + 128) * 65536
                + (hash[1] + 128) * 256
                + (hash[2] + 128))
                % max);
    }
}
