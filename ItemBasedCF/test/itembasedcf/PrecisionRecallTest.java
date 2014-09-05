package itembasedcf;

import java.util.ArrayList;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the PrecisionRecall class
 * 
 * @author Jordan
 */
public class PrecisionRecallTest {
    // data sets
    ArrayList<Long> rec;
    ArrayList<Long> comp;

    /**
     * Initialise array lists before each test
     */
    @Before
    public void setUp() {
        rec = new ArrayList();
        comp = new ArrayList();
    }

    /**
     * Test Constructor creates an object
     */
    @Test
    public void testConstructor() {
        // create PrecisionRecall object with this data
        PrecisionRecall pr = new PrecisionRecall(rec, comp);
        
        assertNotNull(pr);
    }
    
    /**
     * Test of getPrecision method with all true positives, should output a precision of 1.
     */
    @Test
    public void testPrecisionAllTruePositives() {
        // set up data
        for (long l = 1; l <= 3; l++) {
            rec.add(l);
            comp.add(l);
        }

        // create PrecisionRecall object with this data
        PrecisionRecall pr = new PrecisionRecall(rec, comp);

        double expResult = 1.0;
        double result = pr.getPrecision();

        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getPrecision method with all true positives, should output a recall of 1.
     */
    @Test
    public void testRecallAllTruePositives() {
        // set up data
        for (long l = 1; l <= 3; l++) {
            rec.add(l);
            comp.add(l);
        }

        // create PrecisionRecall object with this data
        PrecisionRecall pr = new PrecisionRecall(rec, comp);

        double expResult = 1.0;
        double result = pr.getRecall();

        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getPrecision method with no true positives, should output a precision of 0.
     */
    @Test
    public void testPrecisionNoTruePositives() {
        // set up data
        for (long l = 1; l <= 3; l++) {
            rec.add(l);
        }

        for (long l = 5; l <= 7; l++) {
            comp.add(l);
        }

        // create PrecisionRecall object with this data
        PrecisionRecall pr = new PrecisionRecall(rec, comp);

        double expResult = 0.0;
        double result = pr.getPrecision();

        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getRecall method with no true positives, should output a recall of 0.
     */
    @Test
    public void testRecallNoTruePositives() {
        // set up data
        for (long l = 1; l <= 3; l++) {
            rec.add(l);
        }

        for (long l = 5; l <= 7; l++) {
            comp.add(l);
        }

        // create PrecisionRecall object with this data
        PrecisionRecall pr = new PrecisionRecall(rec, comp);

        double expResult = 0.0;
        double result = pr.getRecall();

        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getPrecision method with half true positives, should output a precision of 0.5
     */
    @Test
    public void testPrecisionHalfTruePositives() {
        // set up data        
        for (long l = 1; l <= 4; l++) {
            rec.add(l);
        }

        for (long l = 3; l <= 6; l++) {
            comp.add(l);
        }

        // create PrecisionRecall object with this data
        PrecisionRecall pr = new PrecisionRecall(rec, comp);

        double expResult = 0.5;
        double result = pr.getPrecision();

        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getRecall method with half true positives, should output a recall of 0.5
     */
    @Test
    public void testRecallHalfTruePositives() {
        // set up data
        for (long l = 1; l <= 4; l++) {
            rec.add(l);
        }

        for (long l = 3; l <= 6; l++) {
            comp.add(l);
        }

        // create PrecisionRecall object with this data
        PrecisionRecall pr = new PrecisionRecall(rec, comp);

        double expResult = 0.5;
        double result = pr.getRecall();

        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getPrecision method with many false positives
     */
    @Test
    public void testPrecisionManyFalsePositives() {
        // Set up data, 3 true positives 7 false positives        
        for (long l = 1; l <= 10; l++) {
            rec.add(l);
        }

        for (long l = 3; l <= 5; l++) {
            comp.add(l);
        }

        // create PrecisionRecall object with this data
        PrecisionRecall pr = new PrecisionRecall(rec, comp);

        double expResult = 0.3;
        double result = pr.getPrecision();

        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getRecall method with many false positives, recall should still be 1.0 as false
     * positives are not involved in the calculation of recall
     */
    @Test
    public void testRecallManyFalsePositives() {
        // Set up data, 3 true positives 7 false positives
        for (long l = 1; l <= 10; l++) {
            rec.add(l);
        }

        for (long l = 3; l <= 5; l++) {
            comp.add(l);
        }

        // create PrecisionRecall object with this data
        PrecisionRecall pr = new PrecisionRecall(rec, comp);

        double expResult = 1.0;
        double result = pr.getRecall();

        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getPrecision method with many false negatives. Precision should stay the same as
     * false negatives are not involved in the calculation of precision
     */
    @Test
    public void testPrecisionManyFalseNegatives() {
        // Set up data, 3 true positives 7 false negatives
        for (long l = 3; l <= 5; l++) {
            rec.add(l);
        }
        
        for (long l = 1; l <= 10; l++) {
            comp.add(l);
        }

        // create PrecisionRecall object with this data
        PrecisionRecall pr = new PrecisionRecall(rec, comp);

        double expResult = 1.0;
        double result = pr.getPrecision();

        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getRecall method with many false negatives.
     */
    @Test
    public void testRecallManyFalseNegatives() {
        // Set up data, 3 true positives 7 false negatives
        for (long l = 3; l <= 5; l++) {
            rec.add(l);
        }
        
        for (long l = 1; l <= 10; l++) {
            comp.add(l);
        }

        // create PrecisionRecall object with this data
        PrecisionRecall pr = new PrecisionRecall(rec, comp);

        double expResult = 0.3;
        double result = pr.getRecall();

        assertEquals(expResult, result, 0.0);
    }

}
