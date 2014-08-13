package patterncounter;

import java.math.BigDecimal;

/**
 * Data structure used by PatternCounter class
 *
 * @author	James Pyke 05090946 <j.pyke@connect.qut.edu.au>
 * @version	1.0
 */
public class ClosedPattern {
    public String itemset = "";
    public BigDecimal support = new BigDecimal(0);
    
    public ClosedPattern (String itemset, BigDecimal support) {
        this.itemset = itemset;
        this.support = support;
    }
}
