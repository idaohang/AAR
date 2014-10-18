package movielensspike;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Calculates the precision and recall for a user based upon a set of recommendations and a set of
 * comparison data. The comparison data is movies the user has already rated, and will be considered
 * 'good' recommendations. If the recommendations includes an item from the comparison data this
 * will be a true positive.<p>
 *
 * Ref: Collaborative Filtering Recommender Systems By Michael D. Ekstrand, John T. Riedl and Joseph
 * A. Konstan
 *
 * @author Jordan
 */
public class PrecisionRecall {

    // Private variables
    private final ArrayList<Long> recommendations, comparisons; // data sets
    private final double truePositives, falsePositives, falseNegatives; // for calculations

    /**
     * Constructor assigns data to private variables.
     *
     * @param recommendations The recommendation data set
     * @param comparisons The comparison data set
     */
    public PrecisionRecall(ArrayList<Long> recommendations, ArrayList<Long> comparisons) {

        // Initialise data sets
        this.recommendations = recommendations;
        this.comparisons = comparisons;

        // Initialise numbers for calcualtions, do it once here to save doing it for each metric
        this.truePositives = getTruePositives();
        this.falsePositives = getFalsePositives();
        this.falseNegatives = getFalseNegatives();
    }

    /**
     * Calculates and returns the precision for this set of recommendations<p>
     * <i>Precision = True Positives / (True Positives + False Positives)</i>
     *
     * @return The precision
     */
    public double getPrecision() {
        return truePositives / (truePositives + falsePositives);
    }

    /**
     * Calculates and returns the recall for this set of recommendations<p>
     * <i>Recall = True Positives / (True Positives + False Negatives)</i>
     *
     * @return The recall
     */
    public double getRecall() {
        return truePositives / (truePositives + falseNegatives);
    }

    /**
     * Find the true positives for a data set: items that have been correctly recommended.<p>
     * True positives will be present in both data sets.
     *
     * @return The count of true positives
     */
    private double getTruePositives() {
        double total = 0;

        // Iterate over each item in recommendations
        for (Long rec : recommendations) {
            // Check to see if this item is present in the comparison set
            for (Long comp : comparisons) {
                if (Objects.equals(rec, comp)) {
                    total++; // this is a true positive, add to counter
                }
            }
        }
        return total;
    }

    /**
     * Find the false positives for a data set: items that have been incorrectly recommended.<p>
     * False positives will be present in only the recommendations.
     *
     * @return The count of false positives
     */
    private double getFalsePositives() {

        // Assume all recommendations are false positives
        double total = recommendations.size();

        // Iterate over each item in recommendations
        for (Long rec : recommendations) {

            // Check to see if this item is present in the comparison set
            for (Long comp : comparisons) {
                if (Objects.equals(rec, comp)) {

                    // If items match then this is not a false positive, so reduce total
                    total--;
                }
            }
        }
        return total;
    }

    /**
     * Find the false negatives for a data set: items that have not been correctly recommended (ie.
     * missed)
     * .<p>
     * False negatives will be present in only the comparisons.
     *
     * @return The count of false negative
     */
    private double getFalseNegatives() {

        // Assume all comparisons are not present
        double total = comparisons.size();

        // Iterate over each item in comparisons
        for (Long comp : comparisons) {
            
            // Check to see if this item is present in the comparison set
            for (Long rec : recommendations) {
                if (Objects.equals(comp, rec)) {
                    
                    // If items match then this is not a false negative, so reduce total
                    total--;
                }
            }
        }
        return total;
    }
}
