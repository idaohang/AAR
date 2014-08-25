package datageneration;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Entry point for DataGeneration project. Using command line arguments; a capstone DB can be
 * created and filled. A .dat file can also be created for each user, containing the tags they have
 * used.
 *
 * @author Jordan
 */
public class DataGeneration {

    // private variables
    private static final CapstoneDBConnection con = new CapstoneDBConnection();
    private static final String[] datFiles = {"user_ratedmovies.dat", "user_taggedmovies.dat", "tags.dat", 
                                              "movie_genres.dat"};
    private static final String[] foldersToMerge = {"userTags", "userCats"};
    private static final String nameOfMergedFolder = "mergedTagCatProfile";
    private static String dBScope, dBStatus;
    

    /**
     * Main method with of 2 arguments, 
     * arguments represents status of db of which user profile/s to generate
     *
     * Argument 1 - scope: 
     * cat - generate cat profile
     * tag - generate tag profile
     * all - generate profile encompassing all data in other profiles
     * 
     * Argument 2 - status:
     * noDb - no db has been created 
     * dbEmpty - db already exists but is empty 
     * dbFull - db exists and is already filled with data
     *
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     */
    public static void main(String[] args) throws IOException, SQLException {

        // This program accepts exactly two arguments
        if (args.length == 2) {

            // First argument is the scope of the database process 
            // {Tags or Categories}
            dBScope = args[0];

            // Second argument is the status of the database prior to the process 
            // {No Database, Empty Database or Fully Populated Database}
            dBStatus = args[1];

            // Only do the required steps depending on the parameterised status of the database
            if (!(dBStatus.equals("dbFull"))) {

                // check if database needs to be created
                if (dBStatus.equals("noDb")) {
                    // Create the database and tables
                    System.out.println("Creating database...");
                    con.createDatabase();
                    System.out.println("DONE\n");
                }

                // database and tables exist -> read in required files
                System.out.println("Filling database...");
                ReadInDat rd = new ReadInDat(datFiles, con);
                System.out.println("DONE\n");
            }

            // database is filled with tag data -> export documents
            System.out.println("Exporting documents...");
            
            // Create a Category or Tag Document object/s depending on the scope of the operation
            if (dBScope.equals("cat") || dBScope.equals("all")) {
                CreateCategoryDocuments cd = new CreateCategoryDocuments(dBScope, con);
            }
            
            if (dBScope.equals("tag") || dBScope.equals("all")) {
                CreateTagsDocuments td = new CreateTagsDocuments(dBScope, con);
            }
            
            System.out.println("DONE\n");
            
            // merge documents and update ratings table if required
             if (dBScope.equals("all")) {
                System.out.println("Merging documents...");
                MergeDocuments md = new MergeDocuments(nameOfMergedFolder, con, foldersToMerge);
                System.out.println("DONE\n");

                System.out.println("Creating Final Ratings Table...");
                FinaliseMovieRatings fmr = new FinaliseMovieRatings(con);
                fmr.createFinalRatingsTable();
                System.out.println("DONE\n");
             }
            
            con.shutDown();
        }        
    }
}
