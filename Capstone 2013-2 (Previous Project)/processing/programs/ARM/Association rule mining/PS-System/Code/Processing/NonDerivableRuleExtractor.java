/**
 * NonDerivableRuleExtractor class.
 *
 * This class is the empty class to serve for the project students
 * to implement the Non-Derivable Association Rule Mining algorithm by
 * Bart Goethals, Juho Muhonen & Hannu Toivonen.
 *
 * This class has been linked into the rest of the system and just
 * needs to have the implementation filled out.
 *
 * NOTE: Commented out code is used when supporting multi-level datasets
 * and loading from FI and/or FCI file(s). They are not needed in order
 * to implement a the basic rule mining algorithm for use on a single level
 * dataset. Methods to determine the coverage, diversity, distribution & distance
 * can be found in other rule mining algorithm classes.
 */
package Processing;

import java.util.ArrayList;

import Processing.ExtractAssociationRules;
//import Processing.DataLoader;
import Processing.Operations;

import Data.FItemsetList;
import Data.AssociationRuleList;

public class NonDerivableRuleExtractor{

	private ExtractAssociationRules ears;

//	private DataLoader dl;
   
   private Operations ops;

	private FItemsetList fl;

	private AssociationRuleList arl;

	private ArrayList exactMinMaxRule, exactMinMaxSup;//, exactMinMaxCov, exactMinMaxDiv, exactMinMaxDis;
	
	private ArrayList allExactRule, allExactSup;//, allExactCov, allExactDiv, allExactDis;

	private ArrayList approxMinMaxRule, approxMinMaxSup, approxMinMaxCon;//, approxMinMaxCov, approxMinMaxDiv, approxMinMaxDis;
	
	private ArrayList allApproxRule, allApproxSup, allApproxCon;//, allApproxCov, allApproxDiv, allApproxDis;

	private Object[] attNames, attLeaf;

	private float con, sup;

	private int attCount, MTH, LTP;

	private boolean multiLevel;

	/**
	 * MinMaxAssociationRuleExtractor method.
	 * Constructor.
	 * Initialises this class and sets up a link to the object/class
	 * that called this class. This allows this class to access its
	 * parent and use methods contained within it (mainly to report
	 * back to the user while running).
	 */
	public NonDerivableRuleExtractor(ExtractAssociationRules owner, Object[] names, Object[] leaves, int h, int l){
		ears = owner;
		attNames = names;
		attLeaf = leaves;
		MTH = h;
		LTP = l;
	}

	/**
	 * NonDerivableRuleExtractor method.
	 * Constructor.
	 * Default constructor used when extracting association rules from
	 * a data file which contains multiple datasets and thus results will
	 * not be going to the GUI.
	 */
	public NonDerivableRuleExtractor(DataLoader owner){
//		dl = owner;
	}

	/**
	 * clearData method.
	 * Method used to clear the data, variables and objects held
	 * by this class when they are no longer needed. This frees
	 * memory for other parts of the program.
	 */
	final public void clearData(){
		if (exactMinMaxRule != null){
			exactMinMaxRule.clear();
			exactMinMaxRule = null;
		}
		if (exactMinMaxSup != null){
			exactMinMaxSup.clear();
			exactMinMaxSup = null;
		}
/*		if (exactMinMaxCov != null){
			exactMinMaxCov.clear();
			exactMinMaxCov = null;
		}
		if (exactMinMaxDiv != null){
			exactMinMaxDiv.clear();
			exactMinMaxDiv = null;
		}
		if (exactMinMaxDis != null){
			exactMinMaxDis.clear();
			exactMinMaxDis = null;
		}
*/		if (allExactRule != null){
			allExactRule.clear();
			allExactRule = null;
		}
		if (allExactSup != null){
			allExactSup.clear();
			allExactSup = null;
		}
/*		if (allExactCov != null){
			allExactCov.clear();
			allExactCov = null;
		}
		if (allExactDiv != null){
			allExactDiv.clear();
			allExactDiv = null;
		}
		if (allExactDis != null){
			allExactDis.clear();
			allExactDis = null;
		}
*/		if (approxMinMaxRule != null){
			approxMinMaxRule.clear();
			approxMinMaxRule = null;
		}
		if (approxMinMaxSup != null){
			approxMinMaxSup.clear();
			approxMinMaxSup = null;
		}
		if (approxMinMaxCon != null){
			approxMinMaxCon.clear();
			approxMinMaxCon = null;
		}
/*		if (approxMinMaxCov != null){
			approxMinMaxCov.clear();
			approxMinMaxCov = null;
		}
		if (approxMinMaxDiv != null){
			approxMinMaxDiv.clear();
			approxMinMaxDiv = null;
		}
		if (approxMinMaxDis != null){
			approxMinMaxDis.clear();
			approxMinMaxDis = null;
		}
*/		if (allApproxRule != null){
			allApproxRule.clear();
			allApproxRule = null;
		}
		if (allApproxSup != null){
			allApproxSup.clear();
			allApproxSup = null;
		}
		if (allApproxCon != null){
			allApproxCon.clear();
			allApproxCon = null;
		}
/*		if (allApproxCov != null){
			allApproxCov.clear();
			allApproxCov = null;
		}
		if (allApproxDiv != null){
			allApproxDiv.clear();
			allApproxDiv = null;
		}
		if (allApproxDis != null){
			allApproxDis.clear();
			allApproxDis = null;
		}
*/	}

	/**
	 * runAlgorithm method.
	 * Method that oversees the execution of the Non-Derivable
	 * Association Rule Mining algorithm. Essentially this is 
	 * where the actual work starts.
	 * @param - FItemsetList list - The reference to the data class that holds the
	 *                              non-derivable itemsets from the dataset.
	 * @param - float minCon - The float that holds the minimum confidence value.
	 * @param - float minSup - The float that holds the minimum support value.
	 * @param - boolean eb - Boolean indicating if Exact Basis rules are to be extracted.
	 * @param - boolean eeb - Boolean indicating if Exact Expanded rules are to be extracted.
	 * @param - boolean ab - Boolean indicating if Approx Basis rules are to be extracted.
	 * @param - boolean ae - Boolean indicating if Approx Expanded rules are to be extracted.
	 * @param - int ac - Integer holding the count of the number of attributes in the dataset.
	 * @param - boolean ml - Boolean that indicates if the dataset is multi-level or not.
	 */
	final public void runAlgorithm(FItemsetList list, float minCon, float minSup, boolean eb, boolean ee, boolean ab, boolean ae, int ac, boolean ml){
		arl = new AssociationRuleList(((int[])attNames[1]).length);
		fl = list;
		con = minCon;
		sup = minSup;

		attCount = ac;
		multiLevel = ml;
	}

	/**
	 * getAssocRules method.
	 * Method used to return the discovered association rules/basis
	 * to the parent class for further use.
	 */
	final public AssociationRuleList getAssocRules(){
		return arl;
	}
}