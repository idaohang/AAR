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
    private static final String[] datFiles = {"user_taggedmovies.dat", "tags.dat", 
                                              "movie_genres.dat"};
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
                ReadInDat.importTagData(datFiles);
                System.out.println("DONE\n");
            }

            // database is filled with tag data -> export documents
            System.out.println("Exporting documents...");
            
            // Create a Category or Tag Document object depending on the scope of the operation
            switch (dBScope) {
                case "cat":
                    CreateCategoryDocuments cd = new CreateCategoryDocuments(dBScope);
                    break;
                case "tag":
                    CreateTagsDocuments td = new CreateTagsDocuments(dBScope);
                    break;
            }
            
            System.out.println("DONE\n");

            con.shutDown();
        }
        
        // testing
        if (args.length == 1) {
            if ("testMerge".equals(args[0])) {
                System.out.println("Merging documents...");
                String [] abc = {"userTags", "userCats"};
                MergeDocuments md = new MergeDocuments("test", abc);
                System.out.println("DONE\n");
            }
        }
        
    }
}
