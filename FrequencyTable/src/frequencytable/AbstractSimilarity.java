/**
 * From http://sujitpal.blogspot.com.au/2008/09/ir-math-with-java-similarity-measures.html
 */
package frequencytable;

import org.apache.commons.collections15.Transformer;

import Jama.Matrix;

public abstract class AbstractSimilarity implements Transformer<Matrix, Matrix> {

    @Override
    public Matrix transform(Matrix termDocumentMatrix) {
        int numDocs = termDocumentMatrix.getColumnDimension();
        Matrix similarityMatrix = new Matrix(numDocs, numDocs);
        for (int i = 0; i < numDocs; i++) {
            Matrix sourceDocMatrix = termDocumentMatrix.getMatrix(
                    0, termDocumentMatrix.getRowDimension() - 1, i, i);
            for (int j = 0; j < numDocs; j++) {
                Matrix targetDocMatrix = termDocumentMatrix.getMatrix(
                        0, termDocumentMatrix.getRowDimension() - 1, j, j);
                similarityMatrix.set(i, j,
                        computeSimilarity(sourceDocMatrix, targetDocMatrix));
            }
        }
        return similarityMatrix;
    }

    protected abstract double computeSimilarity(
            Matrix sourceDoc, Matrix targetDoc);
}
