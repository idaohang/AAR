package datageneration;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Entry point for DataGeneration project.
 *
 * Using command line arguments; a capstone DB can be created and filled. A .dat file can also be
 * created for each user, containing the tags they have used.
 *
 * @author Jordan & Michael
 */
public class DataGeneration {

    // Database connection instance
    private static final CapstoneDBConnection con = new CapstoneDBConnection();

    // Files from which to retrieve information
    private static final String[] datFiles = {"user_ratedmovies.dat", "user_taggedmovies.dat",
        "tags.dat", "movie_genres.dat"},
            foldersToMerge = {"userTags", "userCats"};

    // If a merged folder is to be created, this will be its name
    private static final String nameOfMergedFolder = "mergedTagCatProfile";

    // Parameters defining the extent of database creation and population to be performed
    private static String dBScope, dBStatus;

    // Minimum data count for users to be considered
    private static final double minIdealDataCount = 20;

    /**
     * Main insertion point for data generation, arguments represents status of db of which user
     * profile/s to generate<p>
     *
     * <b>Argument 1 - Scope</b>:<br>
     * <b>cat</b> - generate cat profile<br>
     * <b>tag</b> - generate tag profile<br>
     * <b>all</b> - generate profile encompassing all data in other profiles<p>
     *
     * <b>Argument 2 - Status</b>:<br>
     * <b>noDb</b> - no db has been created<br>
     * <b>dbEmpty</b> - db already exists but is empty<br>
     * <b>dbFull</b> - db exists and is already filled with data
     *
     * @param args The command line arguments (expects two)
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
                MergeDocuments md = new MergeDocuments(nameOfMergedFolder, con, foldersToMerge,
                        minIdealDataCount);
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
