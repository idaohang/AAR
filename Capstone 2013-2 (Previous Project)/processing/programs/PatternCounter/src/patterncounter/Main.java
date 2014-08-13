package patterncounter;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Simple wrapper for the PatternCounter class
 *
 * @author	James Pyke 05090946 <j.pyke@connect.qut.edu.au>
 * @version	1.0
 */
public class Main {

    /**
     * @param args the command line arguments
     * @throws FileNotFoundException
     * @throws IOException
     * @throws Exception
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, Exception {
        if (args.length == 9) {
            PatternCounter counter = new PatternCounter(args);
            counter.countPatterns();
        } else {
            System.out.println("ERROR: program takes 9 arguments:");
            System.out.println("    database server name");
            System.out.println("    database name");
            System.out.println("    database username");
            System.out.println("    database user password");
            System.out.println("    path to ARM output");
            System.out.println("    path to article txt files");
            System.out.println("    path to mallet file");
            System.out.println("    category name");
            System.out.println("    topic number");
        }
    }
}
