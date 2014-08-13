/**
 * ReliableExactRuleExtractorDiv class.
 * 
 * Start Date: 01 February 2007
 * @author Gavin Shaw
 * @version 1.0
 *
 * Log:
 *		  1.0 Start version of class.
 */
package Processing;

import java.text.NumberFormat;

import java.util.ArrayList;

import Processing.ExtractAssociationRules;
import Processing.DataLoader;
import Processing.Operations;

import Data.FCItemsetList;
import Data.AssociationRuleList;

public class ReliableRuleExtractorDiv{

	private ExtractAssociationRules ears;

	private DataLoader dl;
   
   private Operations ops;
   
	private FCItemsetList fcl;

	private AssociationRuleList arl;

	private ArrayList exactMinMaxRule, exactMinMaxSup, exactMinMaxCov, exactMinMaxDiv, exactMinMaxDis;
	
	private ArrayList allExactRule, allExactSup, allExactCov, allExactDiv, allExactDis;

	private ArrayList approxMinMaxRule, approxMinMaxSup, approxMinMaxCon, approxMinMaxCov, approxMinMaxDiv, approxMinMaxDis;
	
	private ArrayList allApproxRule, allApproxSup, allApproxCon, allApproxCov, allApproxDiv, allApproxDis;

	private ArrayList masterAntecedent, masterConsequent, masterRule, masterAbstractRule;

	private ArrayList masterRuleSet, masterAbstractRuleSet;

	private ArrayList eMMRCopy, eMMSCopy;

	private ArrayList MinMaxApproxBasis, MinMaxApproxSupp, MinMaxApproxConf;

	private Object[] attNames, attLeaf, supset, basis;

	private float[] coverage, diversity, distance;

	private float con, sup;

	private int attCount, MTH, LTP;

	private int ebLeaf = 0, eeLeaf = 0, abLeaf = 0, aeLeaf = 0;

	private int ebAbstract = 0, eeAbstract = 0, abAbstract = 0, aeAbstract = 0;

	private boolean multiLevel;

	/**
	 * ReliableRuleExtractor method.
	 * Constructor.
	 * Initialises this class and sets up a link to the object/class
	 * that called this class. This allows this class to access its
	 * parent and use methods contained within it (mainly to report
	 * back to the user while running).
	 */
	public ReliableRuleExtractorDiv(ExtractAssociationRules owner, Object[] names, Object[] leaves, int h, int l){
		ears = owner;
		attNames = names;
		attLeaf = leaves;
		MTH = h;
		LTP = l;
	}

	/**
	 * ReliableRuleExtractor method.
	 * Constructor.
	 * Default constructor used when extracting association rules from
	 * a data file which contains multiple datasets and thus results will
	 * not be going to the GUI.
	 */
	public ReliableRuleExtractorDiv(DataLoader owner){
		dl = owner;
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
		if (exactMinMaxCov != null){
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
		if (allExactRule != null){
			allExactRule.clear();
			allExactRule = null;
		}
		if (allExactSup != null){
			allExactSup.clear();
			allExactSup = null;
		}
		if (allExactCov != null){
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
		if (approxMinMaxRule != null){
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
		if (approxMinMaxCov != null){
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
		if (allApproxRule != null){
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
		if (allApproxCov != null){
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
	}

	/**
	 * runAlgorithm method.
	 * Method that initialises and starts the running of the
	 * association rule mining algorithms to mine the dataset
	 * for the association rules within.
	 */
	final public void runAlgorithm(FCItemsetList list, float minCon, float minSup, boolean eb, boolean ee, boolean ab, boolean ae, int ac, boolean ml){
		arl = new AssociationRuleList(((int[])attNames[1]).length);
		fcl = list;
		con = minCon;
		sup = minSup;

		attCount = ac;
		multiLevel = ml;
		
		ops = new Operations();

		if (ears != null){
			if (eb){
				ears.messageBox("Generating reliable exact basis rules");
				exactRule();
			}
			if (ee){
				ears.messageBox("Generating all exact rules");
				exactRecon();
			}
			if (ab){
				ears.messageBox("Generating min-max approximate basis rules");
				minmaxApprox();
			}
			if (ae){
				ears.messageBox("Generating all approximate rules");
				approxRecon();
			}
		}
		else if (dl != null){
			if (eb){
				dl.messageBox("Generating reliable exact basis rules");
				exactRule();
			}
			if (ee){
				dl.messageBox("Generating all exact rules");
				exactRecon();
			}
			if (ab){
				dl.messageBox("Generating min-max approximate basis rules");
				minmaxApprox();
			}
			if (ae){
				dl.messageBox("Generating all approximate rules");
				approxRecon();
			}
		}
//		printData();
	}

	/**
	 * exactRule method.
	 * Method used to run the algorithm for determining/extracting
	 * the exact association basis contained within the dataset.
	 */
	final private void exactRule(){
		exactMinMaxRule = new ArrayList();
		exactMinMaxSup = new ArrayList();
		exactMinMaxCov = new ArrayList();
		exactMinMaxDiv = new ArrayList();
		exactMinMaxDis = new ArrayList();
		masterAntecedent = new ArrayList();
		masterConsequent = new ArrayList();
		masterRule = new ArrayList();
		masterRuleSet = new ArrayList();
		masterAbstractRule = new ArrayList();
		masterAbstractRuleSet = new ArrayList();

		Object[] itemList = collapseList();
		ArrayList ci = (ArrayList)itemList[0];
		ArrayList g = (ArrayList)itemList[1];
		ArrayList s = (ArrayList)itemList[2];
		int[] entry1, entry2;
		ArrayList genEntry1, genEntry2;
		boolean nonRedundant = false;
		boolean subset = false;
		int size1 = ci.size();
		int size2, size3;

		for (int i = 0; i < size1; i++){
			entry1 = (int[])ci.get(i);
			genEntry1 = (ArrayList)g.get(i);
			size2 = genEntry1.size();
			for (int j = 0; j < size2; j++){
				for (int k = 0; k < i; k++){
					//Check so that a closed itemset is not being compared against itself and that it is a subset...
					if (/*(k != i) && */(ops.subsetNE(entry1, (int[])ci.get(k)))){
						subset = true;
						entry2 = (int[])ci.get(k);
						genEntry2 = (ArrayList)g.get(k);
						size3 = genEntry2.size();
						for (int l = 0; l < size3; l++){
							if (!ops.supsetE((int[])genEntry1.get(j), ops.unionRule(ops.negSubset(entry1, entry2), (int[])genEntry2.get(l)))){
								//This rule is non-redundant...
								nonRedundant = true;
							}
							else{
								nonRedundant = false;
								k = size1;
								break;
							}
						}
					}
				}
				//Check to see if the closed itemset and the generator are the same...
				if (!subset){
					subset = ops.compare(entry1, (int[])genEntry1.get(j));
				}
				//Add rule to list only if conditions are met (not redundant or itemset has no subsets and the generator is not the same as the itemset)...
				if (nonRedundant || !subset){
					basis = new Object[2];
					coverage = new float[3];
					diversity = new float[3];
					distance = new float[4];
					basis[0] = (int[])genEntry1.get(j);
					basis[1] = ops.removeGen((int[])genEntry1.get(j), entry1);
					if (!ops.rulePresent(exactMinMaxRule, (int[])basis[0], (int[])basis[1])){
						determineCoverage(basis, 1);
						determineDiversity(basis, 1);
						determineDistribution(basis, 1);
						determineDistance(basis, 1);
						exactMinMaxRule.add(basis);
						exactMinMaxSup.add((Float)s.get(i));
						exactMinMaxCov.add(coverage);
						exactMinMaxDiv.add(diversity);
						exactMinMaxDis.add(distance);
					}
				}
				nonRedundant = false;
				subset = false;
			}
		}
		arl.addExactMinMaxBasis(exactMinMaxRule, exactMinMaxSup, exactMinMaxCov, exactMinMaxDiv, exactMinMaxDis,
										((float)masterAntecedent.size() / (float)attCount * (float)100), ((float)masterConsequent.size() / (float)attCount * (float)100),
										((float)masterRule.size() / (float)attCount * (float)100), ((float)masterAbstractRule.size() / (float)attCount * (float)100));
		arl.addExactMinMaxBasis(ebLeaf, ebAbstract);
		arl.addExactRuleCoverage((float)masterRuleSet.size() / (float)attCount * (float)100, (float)masterAbstractRuleSet.size() / (float)attCount * (float)100);
	}

	/**
	 * exactRecon method.
	 * Method used to run the algorithm for determining/extracting
	 * the expanded set of exact rules based on the exact basis.
	 */
	final private void exactRecon(){
		allExactRule = new ArrayList();
		allExactSup = new ArrayList();
		allExactCov = new ArrayList();
		allExactDiv = new ArrayList();
		allExactDis = new ArrayList();
		masterAntecedent = new ArrayList();
		masterConsequent = new ArrayList();
		masterRule = new ArrayList();
		masterAbstractRule = new ArrayList();
		int size1, size2, size3, size4;

		eMMRCopy= new ArrayList();
		eMMSCopy = new ArrayList();
		size1 = exactMinMaxRule.size();
		for (int i = 0; i < size1; i++){
			eMMRCopy.add((Object[])exactMinMaxRule.get(i));
			eMMSCopy.add((Float)exactMinMaxSup.get(i));
		}
		ArrayList subsetList, supersetC, supersetG, supersetS, genList;
		Object[] supersetList;
		int[] itemParts, unionRule, negSubset;
		float basisSupport;
		
		Object[] itemList = collapseList();
		ArrayList ci = (ArrayList)itemList[0];
		ArrayList g = (ArrayList)itemList[1];
		ArrayList s = (ArrayList)itemList[2];

		size1 = eMMRCopy.size();
		for (int i = 0; i < size1; i++){
			itemParts = (int[])((Object[])eMMRCopy.get(i))[1];
			if (itemParts.length > 1){
				subsetList = ops.determineSubsets(itemParts);
				size2 = subsetList.size();
				for (int j = 0; j < size2; j++){
					if (!ops.rulePresent(allExactRule, (int[])((Object[])eMMRCopy.get(i))[0], (int[])subsetList.get(j))){
						basis = new Object[2];
						coverage = new float[3];
						diversity = new float[3];
						distance = new float[4];
						basis[0] = (int[])((Object[])eMMRCopy.get(i))[0];
						basis[1] = (int[])subsetList.get(j);
						basisSupport = (Float)eMMSCopy.get(i);
						determineCoverage(basis, 2);
						determineDiversity(basis, 2);
						determineDistribution(basis, 2);
						determineDistance(basis, 2);
						allExactRule.add(basis);
						allExactSup.add(basisSupport);
						allExactCov.add(coverage);
						allExactDiv.add(diversity);
						allExactDis.add(distance);
					}
					unionRule = ops.unionRule((int[])((Object[])eMMRCopy.get(i))[0], ((int[])subsetList.get(j)));
					negSubset = ops.negSubset(itemParts, ((int[])subsetList.get(j)));
					if (!ops.rulePresent(allExactRule, unionRule, negSubset)){
						basis = new Object[2];
						coverage = new float[3];
						diversity = new float[3];
						distance = new float[4];
						basis[0] = unionRule;
						basis[1] = negSubset;
						basisSupport = (Float)eMMSCopy.get(i);
						determineCoverage(basis, 2);
						determineDiversity(basis, 2);
						determineDistribution(basis, 2);
						determineDistance(basis, 2);
						allExactRule.add(basis);
						allExactSup.add(basisSupport);
						allExactCov.add(coverage);
						allExactDiv.add(diversity);
						allExactDis.add(distance);
					}
				}
			}
			supersetList = determineSupset(ops.unionRule((int[])((Object[])eMMRCopy.get(i))[0], itemParts), ci, g, s);
			supersetC = (ArrayList)supersetList[0];
			supersetG = (ArrayList)supersetList[1];
			supersetS = (ArrayList)supersetList[2];
			size3 = supersetC.size();
			for (int j = 0; j < size3; j++){
				genList = (ArrayList)supersetG.get(j);
				size4 = genList.size();
				for (int k = 0; k < size4; k++){
					if (ops.supsetE((int[])genList.get(k), ops.unionRule((int[])((Object[])eMMRCopy.get(i))[0], ops.negSubset((int[])supersetC.get(j), ops.unionRule((int[])((Object[])eMMRCopy.get(i))[0], itemParts))))){
						basis = new Object[2];
						basis[0] = (int[])genList.get(k);
						basis[1] = ops.removeGen((int[])genList.get(k), (int[])supersetC.get(j));
						basisSupport = (Float)supersetS.get(j);
						if (!ops.rulePresent(eMMRCopy, (int[])basis[0], (int[])basis[1])){
							eMMRCopy.add(basis);
							eMMSCopy.add(basisSupport);
							size1++;
						}
					}
				}
			}
		}
		arl.addExactAll(allExactRule, allExactSup, allExactCov, allExactDiv, allExactDis, ((float)masterAntecedent.size() / (float)attCount * (float)100),
								((float)masterConsequent.size() / (float)attCount * (float)100), ((float)masterRule.size() / (float)attCount * (float)100),
								((float)masterAbstractRule.size() / (float)attCount * (float)100));
		arl.addExactAll(eeLeaf, eeAbstract);
		arl.addExactRuleCoverage((float)masterRuleSet.size() / (float)attCount * (float)100, (float)masterAbstractRuleSet.size() / (float)attCount * (float)100);
	}

	/**
	 * minmaxApprox method.
	 * Method used to run the algorithm for determining/extracting
	 * the approximate min-max basis contained within the dataset.
	 */
	final private void minmaxApprox(){
		approxMinMaxRule = new ArrayList();
		approxMinMaxSup = new ArrayList();
		approxMinMaxCon = new ArrayList();
		approxMinMaxCov = new ArrayList();
		approxMinMaxDiv = new ArrayList();
		approxMinMaxDis = new ArrayList();
		masterAntecedent = new ArrayList();
		masterConsequent = new ArrayList();
		masterRule = new ArrayList();
		masterRuleSet = new ArrayList();
		masterAbstractRule = new ArrayList();
		masterAbstractRuleSet = new ArrayList();

		Object[] itemList = collapseList();
		ArrayList ci = (ArrayList)itemList[0];
		ArrayList s = (ArrayList)itemList[2];
		ArrayList genList = fcl.getFreqClosed();
		ArrayList supList = fcl.getSupports();
		int[] closedItemset1, generator1, closedItemset2, generator2;
		float basisSupport, basisConfedence;
		boolean newRule = false;
		int size1, size2;

		size1 = ci.size();
		size2 = genList.size();
		for (int i = 0; i < size1; i++){
			//Go through the list of closed itemsets...
			closedItemset1 = (int[])ci.get(i);					//f1
			for (int j = 0; j < size2; j++){
				//Go through the list of generators...
				generator1 = (int[])((Object[])genList.get(j))[0];	//g1
				//g1's closedItemset is a subset of f1, confidence is larger than minconf
				if (ops.subsetNE(closedItemset1, (int[])((Object[])genList.get(j))[1])){
					basis = new Object[2];
					diversity = new float[3];
					basis[0] = generator1;
					basis[1] = ops.removeGen(generator1, closedItemset1);
					determineDiversity(basis, 3);
					if (diversity[3] >= con){
//					if ((((Float)s.get(i) / (Float)supList.get(j))) >= con){
						newRule = true;
						for (int k = 0; k < size2; k++){
							//Go through the list of generators...
							generator2 = (int[])((Object[])genList.get(k))[0];
							if (ops.subset(generator1, generator2)){
								//The second generator is a subset of the first.
								for (int l = 0; l < size1; l++){
									//Go through the list of closed itemsets...
									closedItemset2 = (int[])ci.get(l);
									if ((l != i)&& ops.subsetNE(closedItemset2, (int[])((Object[])genList.get(k))[1])){ //&& (subset(closedItemset1, closedItemset2))){
										//g2's closedItemset is a subset of f2...
										if ((((Float)s.get(i)/(Float)supList.get(j)) >((Float)s.get(l)/(Float)supList.get(k))) ||
											(!ops.supsetE(generator1, ops.unionRule((ops.negSubset(closedItemset1, closedItemset2)), generator2)))){
											//This maybe a potential new rule so indicate that...
											newRule = true;
										}
										else{
											//This rule is redundant (not a new rule)..., stop the two loops for f2 ang g2
											newRule = false;
											k = size2;
											break;
										}
									}
								}
							}
						}
						if (newRule){
							//This is a new non-redundant rule...
							if (!ops.rulePresent(approxMinMaxRule, generator1, ops.removeGen(generator1, closedItemset1))){
//								basis = new Object[2];
								coverage = new float[3];
//								diversity = new float[3];
								distance = new float[4];
//								basis[0] = generator1;
//								basis[1] = ops.removeGen(generator1, closedItemset1);
								basisSupport = (Float)s.get(i);
								basisConfedence = ((Float)s.get(i) / (Float)supList.get(j));
								determineCoverage(basis, 3);
//								determineDiversity(basis, 3);
								determineDistribution(basis, 3);
								determineDistance(basis, 3);
								approxMinMaxRule.add(basis);
								approxMinMaxSup.add(basisSupport);
								approxMinMaxCon.add(basisConfedence);
								approxMinMaxCov.add(coverage);
								approxMinMaxDiv.add(diversity);
								approxMinMaxDis.add(distance);
							}
						}
						newRule = false;
					}
				}
			}
		}
		arl.addApproxMinMaxBasis(approxMinMaxRule, approxMinMaxSup, approxMinMaxCon, approxMinMaxCov, approxMinMaxDiv, approxMinMaxDis,
											((float)masterAntecedent.size() / (float)attCount * (float)100), ((float)masterConsequent.size() / (float)attCount * (float)100),
											((float)masterRule.size() / (float)attCount * (float)100), ((float)masterAbstractRule.size() / (float)attCount * (float)100));
		arl.addApproxMinMaxBasis(abLeaf, abAbstract);
		arl.addApproxRuleCoverage((float)masterRuleSet.size() / (float)attCount * (float)100, (float)masterAbstractRuleSet.size() / (float)attCount * (float)100);
	}

	/**
	 * approxRecon method.
	 * Method used to run the algorithm for determining/extracting
	 * the expanded set of approximate rules based on the
	 * approximate basis.
	 */
	final private void approxRecon(){
		allApproxRule = new ArrayList();
		allApproxSup = new ArrayList();
		allApproxCon = new ArrayList();
		allApproxCov = new ArrayList();
		allApproxDiv = new ArrayList();
		allApproxDis = new ArrayList();
		masterAntecedent = new ArrayList();
		masterConsequent = new ArrayList();
		masterRule = new ArrayList();
		masterAbstractRule = new ArrayList();

		ArrayList subsetList, generatorList;
		int[] antecedent, consequent;
		int[] a, ebAntecedent, ebConsequent;
		int clength = 2;
		int size1, size2, size3, size4;
		boolean rules = true;
		float rsup, rcon;
		float ruleSupport, ruleConfidence;

		Object[] itemList = collapseList();
		//supersetC is a set of closed itemsets
		ArrayList ci = (ArrayList)itemList[0];
		//all generators which are objects of Itemsets
		ArrayList g = (ArrayList)itemList[1];
		//all supports
		ArrayList s = (ArrayList)itemList[2];
		
		NumberFormat formatter = NumberFormat.getNumberInstance();
		formatter.setMaximumFractionDigits(5);

		eMMRCopy = new ArrayList();									//Store recovered MinMax exact basis
		eMMSCopy = new ArrayList();									//MinMax exact basis rules' supports
		MinMaxApproxBasis = new ArrayList();						//Store recovered MinMax approx basis
		MinMaxApproxSupp = new ArrayList();							//MinMax approx basis rules' supports
		MinMaxApproxConf = new ArrayList();							//MinMax approx basis rules' confidence
 
		size1 = exactMinMaxRule.size();
		for (int i = 0; i < size1; i++){		//Copy exactbasis to eMMRCopy & exactbasis supports to eMMSCopy, exactMinMaxRule is reliable exact basis
			eMMRCopy.add((Object[])exactMinMaxRule.get(i));
			eMMSCopy.add((Float)exactMinMaxSup.get(i));
		}

		size1 = approxMinMaxRule.size();
		for (int i = 0; i < size1; i++){		//Copy approxbasis, supports & confidence to MinMaxApproxBasis, approxMinMaxRule is reliable approx basis
			MinMaxApproxBasis.add((Object[])approxMinMaxRule.get(i));
			MinMaxApproxSupp.add((Float)approxMinMaxSup.get(i));
			MinMaxApproxConf.add((Float)approxMinMaxCon.get(i));
		}

		ArrayList supersetC, supersetG, supersetS, genList;
		Object[] supersetList;
		int[] itemParts;
		float basisSupport;

		//Recover MinMaxExactBasis
		size1 = eMMRCopy.size();
		for (int i = 0; i < size1; i++){
			itemParts = (int[])((Object[])eMMRCopy.get(i))[1];			//Get one exact basis rule itemParts
			supersetList = determineSupset(ops.unionRule((int[])((Object[])eMMRCopy.get(i))[0], itemParts), ci, g, s);
			supersetC = (ArrayList)supersetList[0];
			supersetG = (ArrayList)supersetList[1];
			supersetS = (ArrayList)supersetList[2];
			size2 = supersetC.size();
			for (int j = 0; j < size2; j++){
				genList = (ArrayList)supersetG.get(j);
				size3 = genList.size();
				for (int k = 0; k < size3; k++){
					if (ops.supsetE((int[])genList.get(k), ops.unionRule((int[])((Object[])eMMRCopy.get(i))[0], ops.negSubset(((int[])supersetC.get(j)), ops.unionRule((int[])((Object[])eMMRCopy.get(i))[0], itemParts))))){
						basis = new Object[2];
						basis[0] = (int[])genList.get(k);
						basis[1] = ops.removeGen((int[])genList.get(k), (int[])supersetC.get(j));
						basisSupport = (Float)supersetS.get(j);
						if (!ops.rulePresent(eMMRCopy, (int[])basis[0], (int[])basis[1])){
							eMMRCopy.add(basis);
							eMMSCopy.add(basisSupport);
							size1++;
						}
					}
				}
			}
		}

		size1 = MinMaxApproxBasis.size();
		for (int i = 0; i < size1; i++)	{
			antecedent = (int[])((Object[])MinMaxApproxBasis.get(i))[0];			//a1
			consequent = (int[])((Object[])MinMaxApproxBasis.get(i))[1];			//c1

			ruleConfidence = (Float)MinMaxApproxConf.get(i);				//confidence of r1:a1->c1

			boolean condition1, condition2; 
			Float support, confidence;
			size2 = ci.size();
			size3 = g.size();
			for (int j = 0; j < size2; j++){
				//for each closed itemset c3 
				for (int k = 0; k < size3; k++){
					//for each generator a
					generatorList = (ArrayList) g.get(k);
					size4 = generatorList.size();
					for (int n = 0; n < size4; n++){
						a = (int[])generatorList.get(n);//a generator
						//a->c3\a confidence
						confidence = ((Float) s.get(j))/ ((Float)s.get(k));
						//r1 confidence is larger than a->c3\a confidence
						condition1 = ruleConfidence >= confidence;
						//a->c3\a confidence is larger than minconf
						condition2 = confidence >= con;
 
						if (ops.supsetE(a, antecedent) && ops.supset((int[])ci.get(j), (int[])ci.get(k)) && condition1 && condition2 &&
								ops.supsetE(a, ops.unionRule(antecedent, ops.negSubset((int[])ci.get(j), ops.unionRule(antecedent, consequent))))){
							basis = new Object[2];
							basis[0] = a;
							basis[1] = ops.removeGen(a, (int[])ci.get(j));
							if (!ops.rulePresent(MinMaxApproxBasis, (int[])basis[0], (int[])basis[1]) && !ops.rulePresent(eMMRCopy, (int[])basis[0], (int[])basis[1])){
								MinMaxApproxBasis.add(basis);
								support = (Float)s.get(j);
								confidence = (Float)s.get(j) / (Float)s.get(k);
								MinMaxApproxSupp.add(support);
								MinMaxApproxConf.add(confidence);
								size1++;
							}
						}
					}
				}
			}
		}
	
		size1 = MinMaxApproxBasis.size();
		while (rules){
			rules = false; 
			for (int i = 0; i < size1; i++){
				if (((int[])((Object[])MinMaxApproxBasis.get(i))[1]).length == clength){
					rules = true;
					antecedent = (int[])((Object[])MinMaxApproxBasis.get(i))[0];//a1
					consequent = (int[])((Object[])MinMaxApproxBasis.get(i))[1];//c1
					subsetList = ops.determineSubsets(consequent);
					size2 = subsetList.size();
					for (int j = 0; j < size2; j++){
						if (!ops.rulePresent(approxMinMaxRule, antecedent, (int[])subsetList.get(j))){
 							if (!ops.rulePresent(eMMRCopy, antecedent, (int[])subsetList.get(j))){				//not exist in ExactBasis
								if (!ops.rulePresent(allApproxRule, antecedent, (int[])subsetList.get(j)) &&
										!ops.rulePresent(MinMaxApproxBasis, antecedent, (int[])subsetList.get(j))){		//not exist in AllApprox and not in ApproxBasis
									basis = new Object[2];
									coverage = new float[3];
									diversity = new float[3];
									distance = new float[4];
									basis[0] = antecedent;
									basis[1] = (int[])subsetList.get(j);
									ruleSupport = (Float)MinMaxApproxSupp.get(i);
									ruleConfidence = (Float)MinMaxApproxConf.get(i);
									determineCoverage(basis, 4);
									determineDiversity(basis, 4);
									determineDistribution(basis, 4);
									determineDistance(basis, 4);
									allApproxRule.add(basis);
									allApproxSup.add(ruleSupport);
									allApproxCon.add(ruleConfidence);
									allApproxCov.add(coverage);
									allApproxDiv.add(diversity);
									allApproxDis.add(distance);
								}
							}
						}
					}
				}
			}
			clength++;
		}

		size1 = allApproxRule.size();
		size2 = eMMRCopy.size();
		for (int i = 0; i < size1; i++){
			antecedent = (int[])((Object[])allApproxRule.get(i))[0];
			consequent = (int[])((Object[])allApproxRule.get(i))[1];
			rsup = (Float)allApproxSup.get(i);
			rcon = (Float)allApproxCon.get(i);
			for (int j = 0; j < size2; j++){				//change exactMinMaxRule to eMMRCopy
				ebAntecedent = (int[])((Object[])eMMRCopy.get(j))[0];
				ebConsequent = (int[])((Object[])eMMRCopy.get(j))[1];
				if (ops.compare(ebAntecedent, antecedent)){
					subsetList = ops.determineSubsets(ebConsequent);
					size3 = subsetList.size();
					for (int k = 0; k < size3; k++){
						if ((!ops.rulePresent(allApproxRule, ops.unionRule(antecedent, (int[])subsetList.get(k)),
												ops.negSubset(consequent, (int[])subsetList.get(k)))) &&
												(!ops.rulePresent(MinMaxApproxBasis, ops.unionRule(antecedent, (int[])subsetList.get(k)),
												ops.negSubset(consequent, (int[])subsetList.get(k)))) &&
												(ops.negSubset(consequent, ((int[])subsetList.get(k))).length != 0)){		//not exist in AllApprox and ApproxBasis
							basis = new Object[2];
							coverage = new float[3];
							diversity = new float[3];
							distance = new float[4];
							basis[0] = ops.unionRule(antecedent, (int[])subsetList.get(k));
							basis[1] = ops.negSubset(consequent, (int[])subsetList.get(k));
							ruleSupport = rsup;
							ruleConfidence = rcon;
							determineCoverage(basis, 4);
							determineDiversity(basis, 4);
							determineDistribution(basis, 4);
							determineDistance(basis, 4);
							allApproxRule.add(basis);
							allApproxSup.add(ruleSupport);
							allApproxCon.add(ruleConfidence);
							allApproxCov.add(coverage);
							allApproxDiv.add(diversity);
							allApproxDis.add(distance);
							size1++;
						}
					}
					if ((!ops.rulePresent(allApproxRule, ops.unionRule(antecedent, ebConsequent),
							ops.negSubset(consequent, ebConsequent))) &&
							(!ops.rulePresent(MinMaxApproxBasis, ops.unionRule(antecedent, ebConsequent),
							ops.negSubset(consequent, ebConsequent))) &&
							(ops.negSubset(consequent, ebConsequent).length != 0)){												//not exist in AllApprox and ApproxBasis
						basis = new Object[2];
						coverage = new float[3];
						diversity = new float[3];
						distance = new float[4];
						basis[0] = ops.unionRule(antecedent, ebConsequent);
						basis[1] = ops.negSubset(consequent, ebConsequent);
						ruleSupport = rsup;
						ruleConfidence = rcon;
						determineCoverage(basis, 4);
						determineDiversity(basis, 4);
						determineDistribution(basis, 4);
						determineDistance(basis, 4);
						allApproxRule.add(basis);
						allApproxSup.add(ruleSupport);
						allApproxCon.add(ruleConfidence);
						allApproxCov.add(coverage);
						allApproxDiv.add(diversity);
						allApproxDis.add(distance);
						size1++;
					}
				}
			}
		}
		arl.addApproxAll(allApproxRule, allApproxSup, allApproxCon, allApproxCov, allApproxDiv, allApproxDis,
								((float)masterAntecedent.size() / (float)attCount * (float)100), ((float)masterConsequent.size() / (float)attCount * (float)100),
								((float)masterRule.size() / (float)attCount * (float)100), ((float)masterAbstractRule.size() / (float)attCount * (float)100));
		arl.addApproxAll(aeLeaf, aeAbstract);
		arl.addApproxRuleCoverage((float)masterRuleSet.size() / (float)attCount * (float)100, (float)masterAbstractRuleSet.size() / (float)attCount * (float)100);
	}

	/**
	 * collapseList method.
	 * Method used to take a list based on generators and
	 * convert/collapse it into a list based on closed itemsets.
	 */
	final private Object[] collapseList(){
		Object[] itemList = new Object[3];
		ArrayList item = new ArrayList();
		ArrayList gen = new ArrayList();
		ArrayList sup = new ArrayList();
		ArrayList tgen;
		ArrayList itemsets = fcl.getFreqClosed();
		ArrayList supports = fcl.getSupports();
		int index, size = itemsets.size();
		for (int i = 0; i < size; i++){
			Object[] entry = (Object[])itemsets.get(i);
			index = ops.present(item, (int[])entry[1], 1);
			if (index != -1){
				//Item already in list...
				tgen = (ArrayList)gen.get(index);
				tgen.add((int[])entry[0]);
				gen.set(index, tgen);
			}
			else{
				//Item not in list, so add to list...
				item.add((int[])entry[1]);
				tgen = new ArrayList();
				tgen.add((int[])entry[0]);
				gen.add(tgen);
				sup.add((Float)supports.get(i));
			}
		}
		itemList[0] = item;
		itemList[1] = gen;
		itemList[2] = sup;
		return itemList;
	}

	/**
	 * determineSupset method.
	 * Method used to go through the list of closed itemsets and
	 * determine which ones are supsets of the current set.
	 * ( forall ci  s1 )
	 */
	final private Object[] determineSupset(int[] set, ArrayList ci, ArrayList g, ArrayList s){
		supset = new Object[3];
		ArrayList closed = new ArrayList();
		ArrayList gen = new ArrayList();
		ArrayList sup = new ArrayList();
		int size = ci.size();
		for (int i = 0; i < size; i++){
			if (ops.supset((int[])ci.get(i), set)){
				closed.add((int[])ci.get(i));
				gen.add((ArrayList)g.get(i));
				sup.add((Float)s.get(i));
			}
		}
		supset[0] = closed;
		supset[1] = gen;
		supset[2] = sup;
		return supset;
	}

	/**
	 * getAssocRules method.
	 * Method used to return the discovered association rules/basis
	 * to the parent class for further use.
	 */
	final public AssociationRuleList getAssocRules(){
		return arl;
	}

	final private void determineCoverage(Object[] basis, int ruleSet){
		int[] entry;
		int size1, size2;
//		coverage = new float[3];
		ArrayList rA, rC, rR;
		rA = new ArrayList();
		rC = new ArrayList();
		rR = new ArrayList();

		if (attCount > 0 && !multiLevel){
			//Any rule that falls in this category is a leaf rule as there is only one level in the dataset...
			coverage[0] = (float)((int[])basis[0]).length / (float)attCount * (float)100;
			coverage[1] = (float)((int[])basis[1]).length / (float)attCount * (float)100;
			coverage[2] = ((float)((int[])basis[0]).length + ((int[])basis[1]).length) / (float)attCount * (float)100;
			entry = (int[])basis[0];
			size1 = entry.length;
			for (int x = 0; x < size1; x++){
				if (masterAntecedent.indexOf(entry[x]) == -1){
					masterAntecedent.add(entry[x]);
				}
				if (masterRule.indexOf(entry[x]) == -1){
					masterRule.add(entry[x]);
				}
				if (masterRuleSet.indexOf(entry[x]) == -1){
					masterRuleSet.add(entry[x]);
				}
			}
			entry = (int[])basis[1];
			size1 = entry.length;
			for (int x = 0; x < size1; x++){
				if (masterConsequent.indexOf(entry[x]) == -1){
					masterConsequent.add(entry[x]);
				}
				if (masterRule.indexOf(entry[x]) == -1){
					masterRule.add(entry[x]);
				}
				if (masterRuleSet.indexOf(entry[x]) == -1){
					masterRuleSet.add(entry[x]);
				}
			}
			//Update the 'leaf' rule count...
			if (ruleSet == 1){
				ebLeaf++;
			}
			else if (ruleSet == 2){
				eeLeaf++;
			}
			else if (ruleSet == 3){
				abLeaf++;
			}
			else{
				aeLeaf++;
			}
		}
		else if (attCount > 0 && multiLevel){
			//Rules are from a multi-level dataset...
			//Determine if rule is a 'leaf' rule or abstract rule...
			if (allLeaf(basis)){
				//All items in the rule are leaves, so rule is a leaf rule...
				if (ruleSet == 1){
					ebLeaf++;
				}
				else if (ruleSet == 2){
					eeLeaf++;
				}
				else if (ruleSet == 3){
					abLeaf++;
				}
				else{
					aeLeaf++;
				}
				coverage[0] = (float)((int[])basis[0]).length / (float)attCount * (float)100;
				coverage[1] = (float)((int[])basis[1]).length / (float)attCount * (float)100;
				coverage[2] = ((float)((int[])basis[0]).length + ((int[])basis[1]).length) / (float)attCount * (float)100;
				//Antecedent...
				entry = (int[])basis[0];
				size1 = entry.length;
				for (int x = 0; x < size1; x++){
					if (masterAntecedent.indexOf(entry[x]) == -1){
						masterAntecedent.add(entry[x]);
					}
					if (masterRule.indexOf(entry[x]) == -1){
						masterRule.add(entry[x]);
					}
					if (masterRuleSet.indexOf(entry[x]) == -1){
						masterRuleSet.add(entry[x]);
					}
				}
				//Consequent...
				entry = (int[])basis[1];
				size1 = entry.length;
				for (int x = 0; x < size1; x++){
					if (masterConsequent.indexOf(entry[x]) == -1){
						masterConsequent.add(entry[x]);
					}
					if (masterRule.indexOf(entry[x]) == -1){
						masterRule.add(entry[x]);
					}
					if (masterRuleSet.indexOf(entry[x]) == -1){
						masterRuleSet.add(entry[x]);
					}
				}
			}
			else{
				//At least one item in the rule is abstract (non leaf)...
				//This means that the rule is abstract...
				if (ruleSet == 1){
					ebAbstract++;
				}
				else if (ruleSet == 2){
					eeAbstract++;
				}
				else if (ruleSet == 3){
					abAbstract++;
				}
				else{
					aeAbstract++;
				}
				//Antecedent...
				entry = (int[])basis[0];
				size1 = entry.length;
				for (int x = 0; x < size1; x++){
					if (ops.present((int[])attLeaf[1], entry[x])){
						//Attribute matched exactly, so this item is a leaf...
						if (masterAbstractRule.indexOf(entry[x]) == -1){
							masterAbstractRule.add(entry[x]);
						}
						if (masterAbstractRuleSet.indexOf(entry[x]) == -1){
							masterAbstractRuleSet.add(entry[x]);
						}
						if (rA.indexOf(entry[x]) == -1){
							rA.add(entry[x]);
						}
						if (rR.indexOf(entry[x]) == -1){
							rR.add(entry[x]);
						}
						if (masterAntecedent.indexOf(entry[x]) == -1){
							masterAntecedent.add(entry[x]);
						}
						if (masterRule.indexOf(entry[x]) == -1){
							masterRule.add(entry[x]);
						}
						if (masterRuleSet.indexOf(entry[x]) == -1){
							masterRuleSet.add(entry[x]);
						}
					}
					else{
						//No match, attribute is abstract (not a leaf)...
						size2 = ((int[])attLeaf[1]).length;
						for (int y = 0; y < size2; y++){
							if (ops.present((int[])((Object[])attLeaf[2])[y], entry[x])){
								if (masterAbstractRule.indexOf(((int[])attLeaf[1])[y]) == -1){
									masterAbstractRule.add(((int[])attLeaf[1])[y]);
								}
								if (masterAbstractRuleSet.indexOf(((int[])attLeaf[1])[y]) == -1){
									masterAbstractRuleSet.add(((int[])attLeaf[1])[y]);
								}
								if (rA.indexOf(((int[])attLeaf[1])[y]) == -1){
									rA.add(((int[])attLeaf[1])[y]);
								}
								if (rR.indexOf(((int[])attLeaf[1])[y]) == -1){
									rR.add(((int[])attLeaf[1])[y]);
								}
								if (masterAntecedent.indexOf(((int[])attLeaf[1])[y]) == -1){
									masterAntecedent.add(((int[])attLeaf[1])[y]);
								}
								if (masterRule.indexOf(((int[])attLeaf[1])[y]) == -1){
									masterRule.add(((int[])attLeaf[1])[y]);
								}
								if (masterRuleSet.indexOf(((int[])attLeaf[1])[y]) == -1){
									masterRuleSet.add(((int[])attLeaf[1])[y]);
								}
							}
						}
					}
				}
				//Consequent...
				entry = (int[])basis[1];
				size1 = entry.length;
				for (int x = 0; x < size1; x++){
					if (ops.present((int[])attLeaf[1], entry[x])){
						//Attribute matched exactly, so this item is a leaf...
						if (masterAbstractRule.indexOf(entry[x]) == -1){
							masterAbstractRule.add(entry[x]);
						}
						if (masterAbstractRuleSet.indexOf(entry[x]) == -1){
							masterAbstractRuleSet.add(entry[x]);
						}
						if (rC.indexOf(entry[x]) == -1){
							rC.add(entry[x]);
						}
						if (rR.indexOf(entry[x]) == -1){
							rR.add(entry[x]);
						}
						if (masterConsequent.indexOf(entry[x]) == -1){
							masterConsequent.add(entry[x]);
						}
						if (masterRule.indexOf(entry[x]) == -1){
							masterRule.add(entry[x]);
						}
						if (masterRuleSet.indexOf(entry[x]) == -1){
							masterRuleSet.add(entry[x]);
						}
					}
					else{
						//No match, attribute is abstract (not a leaf)...
						size2 = ((int[])attLeaf[1]).length;
						for (int y = 0; y < size2; y++){
							if (ops.present((int[])((Object[])attLeaf[2])[y], entry[x])){
								if (masterAbstractRule.indexOf(((int[])attLeaf[1])[y]) == -1){
									masterAbstractRule.add(((int[])attLeaf[1])[y]);
								}
								if (masterAbstractRuleSet.indexOf(((int[])attLeaf[1])[y]) == -1){
									masterAbstractRuleSet.add(((int[])attLeaf[1])[y]);
								}
								if (rC.indexOf(((int[])attLeaf[1])[y]) == -1){
									rC.add(((int[])attLeaf[1])[y]);
								}
								if (rC.indexOf(((int[])attLeaf[1])[y]) == -1){
									rC.add(((int[])attLeaf[1])[y]);
								}
								if (masterConsequent.indexOf(((int[])attLeaf[1])[y]) == -1){
									masterConsequent.add(((int[])attLeaf[1])[y]);
								}
								if (masterRule.indexOf(((int[])attLeaf[1])[y]) == -1){
									masterRule.add(((int[])attLeaf[1])[y]);
								}
								if (masterRuleSet.indexOf(((int[])attLeaf[1])[y]) == -1){
									masterRuleSet.add(((int[])attLeaf[1])[y]);
								}
							}
						}
					}
				}
				coverage[0] = (float)rA.size() / (float)attCount * (float)100;
				coverage[1] = (float)rC.size() / (float)attCount * (float)100;
				coverage[2] = ((float)rA.size() + rC.size()) / (float)attCount * (float)100;
			}
		}
		else{
			coverage[0] = -1;
			coverage[1] = -1;
			coverage[2] = -1;
		}
	}
	
	final private void determineDistribution(Object[] basis, int ruleSet){
		int size, index, node;
		int[] p;
		int[] attIDs = (int[])attNames[1];
		p = (int[])basis[0];
		size = p.length;
		for (int i = 0; i< size; i++){
			node = p[i];
			index = ops.present(attIDs, node, 1);
			arl.updateNodeFreqs(index, ruleSet, 1);
		}
		p = (int[])basis[1];
		size = p.length;
		for (int i = 0; i< size; i++){
			node = p[i];
			index = ops.present(attIDs, node, 2);
			arl.updateNodeFreqs(index, ruleSet, 2);
		}
	}
	
	final private void determineDistance(Object[] basis, int ruleset){
		int length;
		float d1 = 0, d2 = 0, d3 = 0, dist = 0;
		float[] r2;
		Object[] rule2;
		if (ruleset == 1){
			//Exact basis...
			length = exactMinMaxRule.size();
			for (int i = 0; i < length; i++){
				//Calculate the distance between R1 & R2...
				rule2 = (Object[])exactMinMaxRule.get(i);
				//Calculate X1Y1 - X2Y2...
				d1 = determineDiversity(ops.xor(ops.unionRule((int[])basis[0], (int[])basis[1]), ops.unionRule((int[])rule2[0], (int[])rule2[1])));
				//Calculate X1 - X2...
				d2 = determineDiversity(ops.xor((int[])basis[0], (int[])rule2[0]));
				//Calculate Y1 - Y2...
				d3 = determineDiversity(ops.xor((int[])basis[1], (int[])rule2[1]));
				//Calculate the distance...
				dist = d1 + d2 + d3;
				
				//Update the distance for R1...
				distance[0] = distance[0] + d1;
				distance[1] = distance[1] + d2;
				distance[2] = distance[2] + d3;
				distance[3] = distance[3] + dist;
				//Update the distance for R2...
				r2 = (float[])exactMinMaxDis.get(i);
				r2[0] = r2[0] + d1;
				r2[1] = r2[1] + d2;
				r2[2] = r2[2] + d3;
				r2[3] = r2[3] + dist;
				exactMinMaxDis.set(i, r2);
			}
		}
		else if (ruleset == 2){
			//Exact expanded...
			length = allExactRule.size();
			if (length > 0){
				for (int i = 0; i < length; i++){
					//Calculate the distance between R1 & R2...
					rule2 = (Object[])allExactRule.get(i);
					//Calculate X1Y1 - X2Y2...
					d1 = determineDiversity(ops.xor(ops.unionRule((int[])basis[0], (int[])basis[1]), ops.unionRule((int[])rule2[0], (int[])rule2[1])));
					//Calculate X1 - X2...
					d2 = determineDiversity(ops.xor((int[])basis[0], (int[])rule2[0]));
					//Calculate Y1 - Y2...
					d3 = determineDiversity(ops.xor((int[])basis[1], (int[])rule2[1]));
					//Calculate the distance...
					dist = d1 + d2 + d3;

					//Update the distance for R1...
					distance[0] = distance[0] + d1;
					distance[1] = distance[1] + d2;
					distance[2] = distance[2] + d3;
					distance[3] = distance[3] + dist;
					//Update the distance for R2...
					r2 = (float[])allExactDis.get(i);
					r2[0] = r2[0] + d1;
					r2[1] = r2[1] + d2;
					r2[2] = r2[2] + d3;
					r2[3] = r2[3] + dist;
					allExactDis.set(i, r2);
				}
			}
			else{
				//First rule for this set, so init distance to 0...
				distance[0] = 0;
				distance[1] = 0;
				distance[2] = 0;
				distance[3] = 0;
			}
		}
		else if (ruleset == 3){
			//Approx basis...
			length = approxMinMaxRule.size();
			for (int i = 0; i < length; i++){
				//Calculate the distance between R1 & R2...
				rule2 = (Object[])approxMinMaxRule.get(i);
				//Calculate X1Y1 - X2Y2...
				d1 = determineDiversity(ops.xor(ops.unionRule((int[])basis[0], (int[])basis[1]), ops.unionRule((int[])rule2[0], (int[])rule2[1])));
				//Calculate X1 - X2...
				d2 = determineDiversity(ops.xor((int[])basis[0], (int[])rule2[0]));
				//Calculate Y1 - Y2...
				d3 = determineDiversity(ops.xor((int[])basis[1], (int[])rule2[1]));
				//Calculate the distance...
				dist = d1 + d2 + d3;
				
				//Update the distance for R1...
				distance[0] = distance[0] + d1;
				distance[1] = distance[1] + d2;
				distance[2] = distance[2] + d3;
				distance[3] = distance[3] + dist;
				//Update the distance for R2...
				r2 = (float[])approxMinMaxDis.get(i);
				r2[0] = r2[0] + d1;
				r2[1] = r2[1] + d2;
				r2[2] = r2[2] + d3;
				r2[3] = r2[3] + dist;
				approxMinMaxDis.set(i, r2);
			}
		}
		else{
			//Approx expanded...
			length = allApproxRule.size();
			for (int i = 0; i < length; i++){
				//Calculate the distance between R1 & R2...
				rule2 = (Object[])allApproxRule.get(i);
				//Calculate X1Y1 - X2Y2...
				d1 = determineDiversity(ops.xor(ops.unionRule((int[])basis[0], (int[])basis[1]), ops.unionRule((int[])rule2[0], (int[])rule2[1])));
				//Calculate X1 - X2...
				d2 = determineDiversity(ops.xor((int[])basis[0], (int[])rule2[0]));
				//Calculate Y1 - Y2...
				d3 = determineDiversity(ops.xor((int[])basis[1], (int[])rule2[1]));
				//Calculate the distance...
				dist = d1 + d2 + d3;
				
				//Update the distance for R1...
				distance[0] = distance[0] + d1;
				distance[1] = distance[1] + d2;
				distance[2] = distance[2] + d3;
				distance[3] = distance[3] + dist;
				//Update the distance for R2...
				r2 = (float[])allApproxDis.get(i);
				r2[0] = r2[0] + d1;
				r2[1] = r2[1] + d2;
				r2[2] = r2[2] + d3;
				r2[3] = r2[3] + dist;
				allApproxDis.set(i, r2);
			}
		}
	}

	final private float determineDiversity(int[] basis){
		int l1 = basis.length;
		if (l1 == 0){
			return (float)0.0;
		}
		else if (l1 == 1){
			return (float)1.0;
		}
		else{
			String[] items = new String[l1];
			int index;
			for (int i = 0; i < l1; i++){
				index = ops.present((int[])attNames[1], basis[i], 1);
				if (index != -1){
					items[i] = ((String[])attNames[0])[index];
				}
			}
			String[] node1, node2;
			int level1, level2, count = 0, len = items.length;
			int d1, d2;
			float ldsum = 0, chrsum = 0;
			for (int i = 0; i < len; i++){
				node1 = items[i].split("-");
				level1 = node1.length;
				for (int j = i + 1; j < len; j++){
					node2 = items[j].split("-");
					level2 = node2.length;
					//Calculate LD (Level Distance/Diversity)...
					ldsum = ldsum + (float)(Math.abs(level1 - level2)) / (float)(MTH - 1);
					//Calculate CHR (Closeness of Hierarchical Relationship)...
					d1 = level1;
					d2 = level2;
					if (node1[0].equals(node2[0])){
						//The two nodes have an ancestor in common...
						d1--;
						d2--;
						int runs;
						if (level1 <= level2){
							runs = level1;
						}
						else{
							runs = level2;
						}
						for (int k = 1; k < runs; k++){
							if (node1[k].equals(node2[k])){
								//Still common ancestor...
								d1--;
								d2--;
							}
							else{
								//No longer common ancestor...
								break;
							}
						}
					}
					else{
						//The two nodes have no ancestor in common...
					}
					chrsum = chrsum + ((float)1 - (((float)MTH - ((float)(d1 + d2) / (float)2)) / (float)MTH));
					count++;
				}
			}
			ldsum = ldsum / (float)count;
			chrsum = chrsum / (float)count;
			return (((float)0.5 * ldsum) + ((float)0.5 * chrsum));
		}
	}
	
	final private float calculateDistance(int[] items){
		float result = 0, dist = 0;
		int d1, d2, m, t, index;
		String[] n1, n2;
		int level1, level2, runs, length = items.length;
		if (length == 0){
			return (float)0.0;
		}
		else if (length == 1){
			return (float)1.0;
		}
		else{
			for (int i = 0; i < length; i++){
				//Get the name of the first item...
				index = ops.present((int[])attNames[1], items[i], 1);
				if (index != -1){
					n1 = ((String[])attNames[0])[index].split("-");
					for (int j = (i + 1); j < length; j++){
						//Get the name of the second item...
						index = ops.present((int[])attNames[1], items[j], 1);
						if (index != -1){
							n2 = ((String[])attNames[0])[index].split("-");
							//Determine the distance between these two items...
							level1 = n1.length;
							level2 = n2.length;
							d1 = level1;
							d2 = level2;
							if (n1.equals(n2)){
								//The two items have a common ancestor...
								d1--;
								d2--;

								if (level1 <= level2){
									runs = level1;
								}
								else{
									runs = level2;
								}
								for (int k = 1; k < runs; k++){
									if (n1[k].equals(n2[k])){
										//Still common ancestor...
										d1--;
										d2--;
									}
									else{
										//No longer common ancestor...
										break;
									}
								}

								if (d1 > d2){
									m = d1;
								}
								else{
									m = d2;
								}
								t = d1 + d2;
								dist = (float)1 - ((float)1 / (float)(((m + t) + 1) / 2));
							}
							else{
								//The two items have no common ancestor, therefore distance is one...
								dist = 1;
							}
							//Sum up the new distance...
							result = result + dist;
						}
						else{
							dist = 1;
						}
					}
				}
				else{
					dist = 1;
				}
			}
			result = result / (float)(length * (length - 1));
			return result;
		}
	}
	
	final private void determineDiversity(Object[] basis, int ruleSet){
		diversity[0] = 0;
		diversity[1] = 0;
		diversity[2] = 0;
		String[] items = new String[((int[])basis[0]).length + ((int[])basis[1]).length];
		int[] part = (int[])basis[0];
		int l1 = part.length, l2;
		int index;
		for (int i = 0; i < l1; i++){
			index = ops.present((int[])attNames[1], part[i], 1);
			if (index != -1){
				items[i] = ((String[])attNames[0])[index];
			}
		}

		part = (int[])basis[1];
		l2 = part.length;
		for (int i = 0; i < l2; i++){
			index = ops.present((int[])attNames[1], part[i], 1);
			if (index != -1){
				items[l1 + i] = ((String[])attNames[0])[index];
			}
		}
		
		String[] node1, node2;
		int level1, level2, count = 0, len = items.length;
		int d1, d2;
		float ldsum = 0, chrsum = 0;
		for (int i = 0; i < len; i++){
			node1 = items[i].split("-");
			level1 = node1.length;
			for (int j = i + 1; j < len; j++){
				node2 = items[j].split("-");
				level2 = node2.length;
				//Calculate LD (Level Distance/Diversity)...
				ldsum = ldsum + (float)(Math.abs(level1 - level2)) / (float)(MTH - 1);
				//Calculate CHR (Closeness of Hierarchical Relationship)...
				d1 = level1;
				d2 = level2;
				if (node1[0].equals(node2[0])){
					//The two nodes have an ancestor in common...
					d1--;
					d2--;
					int runs;
					if (level1 <= level2){
						runs = level1;
					}
					else{
						runs = level2;
					}
					for (int k = 1; k < runs; k++){
						if (node1[k].equals(node2[k])){
							//Still common ancestor...
							d1--;
							d2--;
						}
						else{
							//No longer common ancestor...
							break;
						}
					}
				}
				else{
					//The two nodes have no ancestor in common...
				}
				chrsum = chrsum + ((float)1 - (((float)MTH - ((float)(d1 + d2) / (float)2)) / (float)MTH));
				count++;
			}
		}
		ldsum = ldsum / (float)count;
		chrsum = chrsum / (float)count;
		diversity[0] = ldsum;
		diversity[1] = chrsum;
		diversity[2] = (float)0.5 * ldsum + (float)0.5 * chrsum;
	}

	final private boolean allLeaf(Object[] rule){
		int[] part;

		part = (int[])rule[0];
		if (!ops.subset((int[])attLeaf[1], part)){
			return false;
		}
		part = (int[])rule[1];
		if (!ops.subset((int[])attLeaf[1], part)){
			return false;
		}
		return true;
	}

	/**
	 * printData method.
	 * Method used to print out all of the discovered association
	 * rules/basis found in this dataset.
	 * Used during testing only. Enableing this code will slow down
	 * the performance of the algorithm/s.
	 */
	final private void printData(){
		if (exactMinMaxRule != null){
			System.out.println("Data in exactMinMax - (Yue's Exact Basis)");
			System.out.println("No of rules in exactMinMax:  " + exactMinMaxRule.size());
			for (int i = 0; i < exactMinMaxRule.size(); i++){
				System.out.println("" + ((String[])exactMinMaxRule.get(i))[0] + " ==> " +
												((String[])exactMinMaxRule.get(i))[1]);// + "    " + (Float)exactMinMaxSup.get(i));
			}
		}
		if (allExactRule != null){
			System.out.println("Data in allExact - (Yue's Exact Basis Expanded)");
			System.out.println("No of rules in allExact:     " + allExactRule.size());
			for (int i = 0; i < allExactRule.size(); i++){
				System.out.println("" + ((String[])allExactRule.get(i))[0] + " ==> " +
												((String[])allExactRule.get(i))[1]);// + "    " + (Float)allExactSup.get(i));
			}
		}
		if (approxMinMaxRule != null){
			System.out.println("Data in approxMinMax - (Yue's Approx Basis)");
			System.out.println("No of rules in approxMinMax:  " + approxMinMaxRule.size());
			for (int i = 0; i < approxMinMaxRule.size(); i++){
				System.out.println("" + ((String[])approxMinMaxRule.get(i))[0] + " ==> " +
												((String[])approxMinMaxRule.get(i))[1]);// + "    " + (Float)approxMinMaxSup.get(i) + "    " + (Float)approxMinMaxCon.get(i));
			}
		}
		if (allApproxRule != null){
			System.out.println("Data in allApprox - (Yue's Approx Basis Expanded)");
			System.out.println("No of rules in allApprox:    " + allApproxRule.size());
			for (int i = 0; i < allApproxRule.size(); i++){
				System.out.println("" + ((String[])allApproxRule.get(i))[0] + " ==> " +
												((String[])allApproxRule.get(i))[1]);// + "    " + (Float)allApproxSup.get(i) + "" + (Float)allApproxCon.get(i));
			}
		}
	}
}