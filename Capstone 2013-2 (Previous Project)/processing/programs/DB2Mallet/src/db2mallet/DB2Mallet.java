package db2mallet;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Dumps articles from the database to text files for reading by MALLET.
 * 
 * @author	James Pyke 05090946 <j.pyke@connect.qut.edu.au>
 * @version	1.0
 */
public class DB2Mallet {

    // reads a batch of this many articles from the database at a time
    final static Integer QUERY_BLOCK = 100;    
    
    /**
     * Receive command line input and check number of parameters
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length == 5) {
            dbDump(args);
        } else {
            System.out.println("ERROR: program takes 5 arguments:");
            System.out.println("    database server name");
            System.out.println("    database name");
            System.out.println("    database username");
            System.out.println("    database user password");
            System.out.println("    path to save output files");
        }
    }

    /**
     * Dump database contents
     * @param args the command line arguments
     */
    private static void dbDump(String[] args) {
        ConnectMSSQL dbConnection = new ConnectMSSQL(args[0], args[1], args[2], args[3]);
        
        // read list of categories and their parents from database, then
        // recursively calculate each category's top-level category
        Map<String, String> categories = dbConnection.getCategoryParents();
        for (Map.Entry<String, String> category : categories.entrySet()) {
            fixParent(categories, category.getKey());
        }

        // iterate through article list
        ArrayList<String[]> articles;
        int i = 0;  // counter
        do {
            // output text to console giving the user a progress every so often
            if (i % 10 == 0) {
                System.out.println("Exported " + (i * QUERY_BLOCK) + " rows");
            }

            // get a batch of articles from database and increment counter
            articles = dbConnection.getArticles(i++);

            // iterate through each article in a batch
            for (String[] article : articles) {
                
                // find all top-level categories it belongs to
                HashMap<String, Boolean> articleCategories = getParentCategories(categories, article[2]);
                
                // for each top-level category, save it as a text file in that
                // category's folder
                for (Map.Entry<String, Boolean> articleCategory : articleCategories.entrySet()) {
                    String folder = args[4] + "\\" + articleCategory.getKey().replaceAll(" ", "_");
                    new File(folder).mkdirs();
                    try {
                        PrintWriter writer = new PrintWriter(folder + "\\" + article[0] + ".txt", "UTF-8");
                        writer.println(article[1]);
                        writer.close();
                    } catch (Exception ex) {
                        System.out.println("File IO error: " + ex.toString());
                    }
                }
            }
        // stop looping when no more articles left
        } while (articles.size() > 0);
    }

    /**
     * Recursively finds a category's parent category.
     * @param categories
     * @param key 
     */
    private static void fixParent(Map<String, String> categories, String key) {
        String value = categories.get(key);
        if (value.equals(key)) {
            return;
        }
        if (!categories.get(value).equals(value)) {
            fixParent(categories, value);
        }
        categories.put(key, categories.get(value));
    }

    /**
     * Finds all top-level categories an article belongs to
     * @param categories map of top-level categories
     * @param subjects Subject field from database (comma-separated list of categories)
     * @return article's top-level categories
     */
    private static HashMap<String, Boolean> getParentCategories(Map<String, String> categories, String subjects) {
        HashMap<String, Boolean> parents = new HashMap<>();
        String[] subjectsList = subjects.split("; ?");
        for (String subject : subjectsList) {
            if (!subject.equals("") && (categories.get(subject) != null)) {
                parents.put(categories.get(subject), true);
            }
        }
        return parents;
    }
}
