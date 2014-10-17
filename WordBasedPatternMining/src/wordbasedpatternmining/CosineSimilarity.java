/**
 * From http://sujitpal.blogspot.com.au/2008/09/ir-math-with-java-similarity-measures.html
 */
package wordbasedpatternmining;

import Jama.Matrix;

public class CosineSimilarity extends AbstractSimilarity {

    @Override
    protected double computeSimilarity(Matrix sourceDoc, Matrix targetDoc) {
        double dotProduct = sourceDoc.arrayTimes(targetDoc).norm1();
        double eucledianDist = sourceDoc.normF() * targetDoc.normF();
        return dotProduct / eucledianDist;
    }

}
