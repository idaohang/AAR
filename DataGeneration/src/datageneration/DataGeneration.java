package datageneration;

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
    private static final String[] datFiles = {"user_taggedmovies.dat", "tags.dat"};

    /**
     * Main method with a maximum of 1 argument, argument represents status of db
     *
     * Arguments: 
     * noDb - no db has been created 
     * dbEmpty - db already exists but is empty 
     * dbFull - db exists and is already filled with data
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // exactly one argument required for this program to run
        if (args.length == 1) {
            // check if database needs to be filled
            if (!(args[0].equals("dbFull"))) {

                // check if database needs to be created
                if (args[0].equals("noDb")) {
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
            CreateTagsDocuments.exportDocuments();
            System.out.println("DONE\n");
            
            con.shutDown();
        }
    }
}
