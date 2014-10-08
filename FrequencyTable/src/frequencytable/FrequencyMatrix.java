package frequencytable;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Defines, implements and populates a matrix (two-dimensional array) object based on a
 * parameterised HashMap object passed in construction.
 *
 * @author Clarky
 */
public class FrequencyMatrix {

    // The parameterised number of topics that are present within model (user-passed parameter)
    private int numTopics;

    // The two-dimensional array (matrix) responsible for storing matricies
    private double[][] matrix;

    // HashMap object (one for each user) containing topics, words and the number of references to 
    // each word
    private HashMap<String, HashMap<String, Integer>> in;

    /**
     * Constructor. Instantiate two-dimensional array of required size
     *
     * @param input HashMap of topics & words/frequencies
     * @param topics The number of topics intended for model
     */
    public FrequencyMatrix(HashMap<String, HashMap<String, Integer>> input, int topics, int size) {

        // Assign input to local input variable
        in = input;//enforceCutoff(input, LOW_END_CUTOFF);

        // Assign the topic number to local variable
        numTopics = topics;

        // Create two-dimensional array given number of topics (in entire corpus, not just input)
        matrix = new double[numTopics][size+1];

        // Fill elements of matrix
        populateMatrix();
    }

    /**
     * Populate the two-dimensional array of integers with word distributions
     */
    private void populateMatrix() {

        // Iterate through all topics, adding word distribution to array if present, 0 if not
        for (int i = 0; i < numTopics; i++) {

            // Using i as topic pointer, test if topic is present
            // If so, iterate through all words, calculate word distribution and insert into array
            if (in.containsKey(Integer.toString(i))) {

                // Each word/frequenct combination object
                HashMap<String, Integer> word = in.get(Integer.toString(i));

                // Keep count of iterations through foreach
                int wordCount = 0;

                // Iterate through all words, adding to array
                for (Map.Entry w : word.entrySet()) {
                    wordCount++;
                    matrix[i][Integer.parseInt(w.getKey().toString())] = new Double(getDist((int) w.getValue(), getNumOccurences(word)));
                }
            } else {

                // If not, set all values at array[i][x] to 0
                for (int j = 0; j < matrix[i].length; j++) {
                    matrix[i][j] = 0.00;
                }
            }
        }
    }

    /**
     * @return The matrix object
     */
    public double[][] getMatrix() {
        return matrix;
    }

    /**
     * Calculate and return the number of occurrences (non-distinct word items) occurring within a
     * single topic. A single word may be used many times, we want to count every occurrence of this
     * word. This is achieved with frequency information. Result is used to calculate word
     * distribution within a given topic.
     *
     * @return The number of uses (i.e. not distinct) of words in any topic
     */
    private int getNumOccurences(HashMap<String, Integer> get) {

        // Counter for the number of words
        int counter = 0;

        // Iterate through all words, adding the number of occurences to counter
        for (Map.Entry topic : get.entrySet()) {
            counter += (int) topic.getValue();
        }

        return counter;
    }

    /**
     * Calculate the word distribution for any word given the number of other words in a topic.
     *
     * @param word The word for which distribution is being calculated
     * @param totalWords The total number of words within a topic
     * @return The distribution calculation for a word in a topic
     */
    private double getDist(int word, int totalWords) {
        double r = (double) word / (double) totalWords;
        return r;
    }

    @Override
    public String toString() {

        // Enforce a number of decimal places in output
        DecimalFormat df = new DecimalFormat("0.00000");

        // String to accrue output
        String s = "";

        // Iterate through entire matrix one level at-a-time, outputting cell information
        s += "\t\t  WORD DISTRIBUTION\n";
        for (int i = 0; i < numTopics; i++) {
            s += "TOPIC " + i + " : ";
            for (int j = 0; j < matrix[i].length; j++) {
                s += df.format(matrix[i][j]) + " | ";
            }
            s += "\n";
        }

        return s;

    }
}
