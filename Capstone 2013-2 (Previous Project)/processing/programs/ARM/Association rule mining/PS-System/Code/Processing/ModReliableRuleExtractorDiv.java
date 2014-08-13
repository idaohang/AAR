/**
 * ModReliableExactRuleExtractorDiv class.
 * 
 * Start Date: 30 November 2007
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

public class ModReliableRuleExtractorDiv{

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

	private Object[] attNames, attLeaf, abstractAtts, supset, basis;

	private float[] coverage, diversity, distance;

	private float con, sup;

	private int attCount, MTH, LTP;

	private int ebLeaf = 0, eeLeaf = 0, abLeaf = 0, aeLeaf = 0;

	private int ebAbstract = 0, eeAbstract = 0, abAbstract = 0, aeAbstract = 0;

	private boolean multiLevel;

	/**
	 * ModReliableRuleExtractor method.
	 * Constructor.
	 * Initialises this class and sets up a link to the object/class
	 * that called this class. This allows this class to access its
	 * parent and use methods contained within it (mainly to report
	 * back to the user while running).
	 */
	public ModReliableRuleExtractorDiv(ExtractAssociationRules owner, Object[] names, Object[] leaves, Object[] table, int h, int l){
		ears = owner;
		attNames = names;
		attLeaf = leaves;
		abstractAtts = table;
		MTH = h;
		LTP = l;
	}

	/**
	 * ModReliableRuleExtractor method.
	 * Constructor.
	 * Default constructor used when extracting association rules from
	 * a data file which contains multiple datasets and thus results will
	 * not be going to the GUI.
	 */
	public ModReliableRuleExtractorDiv(DataLoader owner){
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
				ears.messageBox("Generating mod reliable exact basis rules");
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
				dl.messageBox("Generating mod reliable exact basis rules");
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
		ArrayList genEntry1, genEntry2, g3;
		int size1, size2, size3, size4;
		boolean nonRedundant = false;
		boolean subset = false;

		size1 = ci.size();
		size4 = g.size();
		for (int i = 0; i < size1; i++){
			entry1 = (int[])ci.get(i);
			genEntry1 = (ArrayList)g.get(i);
			size2 = genEntry1.size();
			for (int j = 0; j < size2; j++){
				for (int k = 0; k < size1; k++){
					//Check so that a closed itemset is not being compared against itself and that it is a subset...
					if ((k != i) && (ops.subset(entry1, (int[])ci.get(k)))){
						subset = true;
						entry2 = (int[])ci.get(k);
						genEntry2 = (ArrayList)g.get(k);
						size3 = genEntry2.size();
						for (int l = 0; l < size3; l++){
							if (!ops.subset((int[])genEntry1.get(j), ops.unionRule((ops.negSubset(entry1, entry2)), (int[])genEntry2.get(l)))){
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
				//Check to see if the potential rule will be redundant due to the generator's ancestor forming a rule...
				if (nonRedundant){
					for (int x = 0; x < size4; x++){
						if (x != i){
							g3 = (ArrayList)g.get(x);
							size3 = g3.size();
							for (int y = 0; y < size3; y++){
								if (ops.parent(abstractAtts, (int[])g3.get(y), (int[])genEntry1.get(j)) && ops.child(abstractAtts, (int[])genEntry1.get(j), (int[])g3.get(y)) &&
									 ops.compare(ops.negSubset(entry1, (int[])genEntry1.get(j)), ops.negSubset((int[])ci.get(x), (int[])g3.get(y))) &&
									 ops.validRule(abstractAtts, (int[])g3.get(y), ops.negSubset((int[])ci.get(x), (int[])g3.get(y)))){
									nonRedundant = false;
									x = size4;
									break;
								}
							}
						}
					}
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

		ArrayList subsetList, temp, temp1, temp2;
		ArrayList supersetC, supersetG, supersetS, genList;
		Object[] supersetList;
		int[] rulesetItems;
		int[] itemParts, unionRule, negSubset;
		float basisSupport;
		int size1, size2, size3, size4;

		Object[] itemList = collapseList();
		ArrayList ci = (ArrayList)itemList[0];
		ArrayList g = (ArrayList)itemList[1];
		ArrayList s = (ArrayList)itemList[2];
		
		rulesetItems = getGeneratorItems();

		eMMRCopy= new ArrayList();
		eMMSCopy = new ArrayList();
		size1 = exactMinMaxRule.size();
		for (int i = 0; i < size1; i++){
			eMMRCopy.add((Object[])exactMinMaxRule.get(i));
			eMMSCopy.add((Float)exactMinMaxSup.get(i));
		}

		size2 = eMMRCopy.size();
		for (int i = 0; i < size2; i++){
			itemParts = (int[])((Object[])eMMRCopy.get(i))[1];
			if (itemParts.length > 1){
				subsetList = ops.determineSubsets(itemParts);
				size3 = subsetList.size();
				for (int j = 0; j < size3; j++){
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
					unionRule = ops.unionRule((int[])((Object[])eMMRCopy.get(i))[0], (int[])subsetList.get(j));
					negSubset = ops.negSubset(itemParts, (int[])subsetList.get(j));
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
			supersetList = determineSupset(ops.unionRule((int[])((Object[])eMMRCopy.get(i))[0], itemParts), ci, g , s);
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
							size2++;
						}
					}
				}
			}

			//Recover rules that are hierarchically redundant...
			if (i < size1){
				temp = recoverExactBasis((Object[])eMMRCopy.get(i), rulesetItems, ci, g, s);
				temp1 = (ArrayList)temp.get(0);
				temp2 = (ArrayList)temp.get(1);
				size4 = temp1.size();
				for (int x = 0; x < size4; x++){
					eMMRCopy.add((Object[])temp1.get(x));
					eMMSCopy.add((Float)temp2.get(x));
					size2++;
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
		ArrayList g = (ArrayList)itemList[1];
		ArrayList s = (ArrayList)itemList[2];
		ArrayList genList = fcl.getFreqClosed();
		ArrayList supList = fcl.getSupports();
		int[] closedItemset1, generator1, closedItemset2, generator2, generator3;
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
//					if((((Float)s.get(i) / (Float)supList.get(j))) >= con){
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
											//Check to see if the potential rule will be redundant due to the generator's
											//ancestor forming a rule...
											for (int x = 0 ; x < size2; x++){
												if (j != x && !ops.compare(generator1, (int[])((Object[])genList.get(x))[0])){
													generator3 = (int[])((Object[])genList.get(x))[0];
													if (ops.parent(abstractAtts, generator3, generator1) && ops.child(abstractAtts, generator1, generator3)){
														if (ops.validRule(abstractAtts, generator3, ops.negSubset(closedItemset1, generator1))){
															for (int y = 0; y < size1; y++){
																if (((Float)s.get(y) / (Float)supList.get(x)) >= ((Float)s.get(i) / (Float)supList.get(j))){
																	if (ops.subsetNE((int[])ci.get(y), generator3)){
																		if (ops.compare(ops.negSubset(closedItemset1, generator1), ops.negSubset((int[])ci.get(y), generator3))){
//																			if (ops.validRule(abstractAtts, generator3, ops.negSubset(closedItemset1, generator1))){
//																			if (((Float)s.get(y) / (Float)supList.get(x)) >= ((Float)s.get(i) / (Float)supList.get(j))){
																			newRule = false;
																			x = size2;
																			l = size1;
																			k = size2;
																			break;
																		}
																	}
																}
															}
														}
													}
												}
											}
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

		ArrayList subsetList;
		ArrayList temp, temp1, temp2;
		int[] antecedent, consequent, ebAntecedent, ebConsequent, a, itemParts, generator;
		int clength = 2;
		int size1, size2, size3, size4;
		boolean rules = true;
		float rsup, rcon;
		float ruleSupport, ruleConfedence;
		
		ArrayList supersetC, supersetG, supersetS, genList;
		Object[] supersetList;
		float basisSupport;

		NumberFormat formatter = NumberFormat.getNumberInstance();
		formatter.setMaximumFractionDigits(5);

		int[] rulesetItems;
		rulesetItems = getGeneratorItems();
		
		ArrayList genList1 = fcl.getFreqClosed();
		ArrayList supList = fcl.getSupports();
		Object[] itemList = collapseList();
		ArrayList ci = (ArrayList)itemList[0];
		ArrayList g = (ArrayList)itemList[1];
		ArrayList s = (ArrayList)itemList[2];

		eMMRCopy = new ArrayList();									//Store recovered MinMax exact basis
		eMMSCopy = new ArrayList();									//MinMax exact basis rules' supports
		MinMaxApproxBasis = new ArrayList();						//Store recovered MinMax approx basis
		MinMaxApproxSupp = new ArrayList();							//MinMax approx basis rules' supports
		MinMaxApproxConf = new ArrayList();							//MinMax approx basis rules' confidence
 
		size1 = exactMinMaxRule.size();
		size2 = approxMinMaxRule.size();
		for (int i = 0; i < size1; i++){		//Copy exactbasis & supports to eMMRCopy & eMMSCopy, exactMinMaxRule is reliable exact basis
			eMMRCopy.add((Object[])exactMinMaxRule.get(i));
			eMMSCopy.add((Float)exactMinMaxSup.get(i));
			//Recover the exact basis rules that are hierarchically redundant...
			temp = recoverExactBasis((Object[])exactMinMaxRule.get(i), rulesetItems, ci, g, s);
			temp1 = (ArrayList)temp.get(0);
			temp2 = (ArrayList)temp.get(1);
			size3 = temp1.size();
			for (int x = 0; x < size3; x++){
				eMMRCopy.add((Object[])temp1.get(x));
				eMMSCopy.add((Float)temp2.get(x));
			}
		}

		for (int i = 0; i < size2; i++){		//Copy approxbasis, supports & confidence to MinMaxApproxBasis, approxMinMaxRule is reliable approx basis
			MinMaxApproxBasis.add((Object[])approxMinMaxRule.get(i));
			MinMaxApproxSupp.add((Float)approxMinMaxSup.get(i));
			MinMaxApproxConf.add((Float)approxMinMaxCon.get(i));
		}

		//Recover ExactBasis rules
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

		//Recover ApproxBasis rules
		size1 = approxMinMaxRule.size();
		size2 = genList1.size();
		size3 = ci.size();
		for (int i = 0; i < size1; i++){
			antecedent = (int[])((Object[])approxMinMaxRule.get(i))[0];
			for (int j = 0; j < size2; j++){
				generator = (int[])((Object[])genList1.get(j))[0];
				consequent = ops.unionRule((int[])((Object[])approxMinMaxRule.get(i))[1], generator);
				if (ops.parent(abstractAtts, antecedent, generator) && ops.child(abstractAtts, generator, antecedent) && !ops.compare(generator, antecedent) &&
					 ops.supset(consequent, (int[])((Object[])genList1.get(j))[1])){
					rsup = 0;
					for (int k = 0; k < size3; k++){
						if (ops.compare(consequent, (int[])ci.get(k))){
							rsup = (Float)s.get(k);
							break;
						}
					}
					if ((rsup / (Float)supList.get(j)) >= con && !ops.rulePresent(MinMaxApproxBasis, generator, (int[])((Object[])approxMinMaxRule.get(i))[1])){
						//Recovered rule is a redundant basis rule so add...
						basis = new Object[2];
						basis[0] = generator;
						basis[1] = (int[])((Object[])approxMinMaxRule.get(i))[1];
						MinMaxApproxBasis.add(basis);
						MinMaxApproxSupp.add(rsup);
						MinMaxApproxConf.add(rsup / (Float)supList.get(j));
					}
				}
			}
		}

		//Derive approx expanded rules
		size1 = MinMaxApproxBasis.size();
		size2 = ci.size();
		size3 = g.size();
		for (int i = 0; i < size1; i++)	{
			antecedent = (int[])((Object[])MinMaxApproxBasis.get(i))[0];			//a1
			consequent = (int[])((Object[])MinMaxApproxBasis.get(i))[1];			//c1

			ruleConfedence = (Float)MinMaxApproxConf.get(i);				//confidence of r1:a1->c1

			boolean condition1, condition2; 
			Float support, confidence;
			for (int j = 0; j < size2; j++){
				//for each closed itemset c3 
				for (int k = 0; k < size3; k++){
					//for each generator a
					genList = (ArrayList) g.get(k);
					size4 = genList.size();
					for (int n = 0; n < size4; n++){
						a = (int[])genList.get(n);//a generator
						//a->c3\a confidence
						confidence = ((Float) s.get(j))/ ((Float)s.get(k));
						//r1 confidence is larger than a->c3\a confidence
						condition1 = ruleConfedence >= confidence;
						//a->c3\a confidence is larger than minconf
						condition2 = confidence >= con;
 
						if (ops.supsetE(a, antecedent) && ops.supset((int[])ci.get(j), (int[])ci.get(k)) && condition1 && condition2 &&
								ops.supsetE(a, ops.unionRule(antecedent, ops.negSubset(((int[])ci.get(j)), ops.unionRule(antecedent, consequent))))){
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
									ruleConfedence = (Float)MinMaxApproxConf.get(i);
									determineCoverage(basis, 4);
									determineDiversity(basis, 4);
									determineDistribution(basis, 4);
									determineDistance(basis, 4);
									allApproxRule.add(basis);
									allApproxSup.add(ruleSupport);
									allApproxCon.add(ruleConfedence);
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
												(ops.negSubset(consequent, (int[])subsetList.get(k)).length != 0)){		//not exist in AllApprox and ApproxBasis
							basis = new Object[2];
							coverage = new float[3];
							diversity = new float[3];
							distance = new float[4];
							basis[0] = ops.unionRule(antecedent, (int[])subsetList.get(k));
							basis[1] = ops.negSubset(consequent, (int[])subsetList.get(k));
							ruleSupport = rsup;
							ruleConfedence = rcon;
							determineCoverage(basis, 4);
							determineDiversity(basis, 4);
							determineDistribution(basis, 4);
							determineDistance(basis, 4);
							allApproxRule.add(basis);
							allApproxSup.add(ruleSupport);
							allApproxCon.add(ruleConfedence);
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
						ruleConfedence = rcon;
						determineCoverage(basis, 4);
						determineDiversity(basis, 4);
						determineDistribution(basis, 4);
						determineDistance(basis, 4);
						allApproxRule.add(basis);
						allApproxSup.add(ruleSupport);
						allApproxCon.add(ruleConfedence);
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
		Object[] itemList = new Object[4];
		ArrayList item = new ArrayList();
		ArrayList gen = new ArrayList();
		ArrayList sup = new ArrayList();
		ArrayList tgen;
		ArrayList itemsets = fcl.getFreqClosed();
		ArrayList supports = fcl.getSupports();
		int maxLength = 0;
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
				if (((int[])entry[1]).length > maxLength){
					maxLength = ((int[])entry[1]).length;
				}
			}
		}
		itemList[0] = item;
		itemList[1] = gen;
		itemList[2] = sup;
		itemList[3] = maxLength;
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

	final private int[] getGeneratorItems(){
		ArrayList items = new ArrayList();
		int[] itemset, itemlist;
		int size1, size2;
		ArrayList gens = fcl.getFreqClosed();
		size1 = gens.size();
		for (int i = 0; i < size1; i++){
			itemset = (int[])((Object[])gens.get(i))[0];
			size2 = itemset.length;
			for (int j = 0; j < size2; j++){
				if (items.indexOf(itemset[j]) == -1){
					items.add(itemset[j]);
				}
			}
		}
		size1 = items.size();
		itemlist = new int[size1];
		for (int i = 0; i < size1; i++){
			itemlist[i] = (Integer)items.get(i);
		}
		return itemlist;
	}

	final private ArrayList recoverExactBasis(Object[] rule, int[] rulesetItems, ArrayList ci, ArrayList g, ArrayList s){
		ArrayList tList, gList, t1, antecedentSubsets, eMMRCopy, eMMSCopy, temp;
		int[] itemParts, t2, t3, t5, potentialRuleItemSet;
		Object[] t4;
		float basisSupport;
		boolean child = false;
		int size1, size2, size3, size4;

		eMMRCopy = new ArrayList();
		eMMSCopy = new ArrayList();
		temp = new ArrayList();
		tList = new ArrayList();

		//Only use the rules that were considered to not be redundant...
		tList.clear();
		itemParts = (int[])rule[0];
		t1 = new ArrayList();
		size1 = itemParts.length;
		size2 = rulesetItems.length;
		for (int j = 0; j < size1; j++){
			for (int k = 0; k < size2; k++){
				if (ops.isChild(rulesetItems[k], itemParts[j], (int[])abstractAtts[1], (Object[])abstractAtts[2])){
					//This is a descendant of the item in the antecedent...
					t1.add(rulesetItems[k]);
				}
			}
		}
		//Convert from ArrayList to String[] for subset extraction...
		size1 = t1.size();
		t2 = new int[size1];
		for (int j = 0; j < size1; j++){
			t2[j] = (Integer)t1.get(j);
		}
		antecedentSubsets = ops.determineSubsets(t2);
		size1 = antecedentSubsets.size();
		size2 = itemParts.length;
		for (int j = 0; j < size1; j++){
			if (((int[])antecedentSubsets.get(j)).length > 0){
				if (!ops.parent(abstractAtts, (int[])rule[0], (int[])antecedentSubsets.get(j))){
					//The subset does not contain descendents from all of the original items in the antecedent...
					t3 = (int[])antecedentSubsets.get(j);
					size3 = t3.length;
					for (int k = 0; k < size2; k++){
						for (int m = 0; m < size3; m++){
							if (ops.isChild(t3[m], itemParts[k], (int[])abstractAtts[1], (Object[])abstractAtts[2])){
								child = true;
								break;
							}
						}
						if (!child){
							//This original item has no descendent in the new antecedent...
							size4 = ((int[])antecedentSubsets.get(j)).length;
							t5 = new int[size4 + 1];
							for (int n = 0; n < size4; n++){
								t5[n] = ((int[])antecedentSubsets.get(j))[n];
							}
							t5[size4] = itemParts[k];
							antecedentSubsets.set(j, t5);
							t3 = t5;//(int[])antecedentSubsets.get(j);
						}
						child = false;
					}
				}
				if (ops.validRule(abstractAtts, (int[])antecedentSubsets.get(j), (int[])rule[1])){
					t4 = new Object[2];
					t4[0] = (int[])antecedentSubsets.get(j);
					t4[1] = (int[])rule[1];
					tList.add(t4);
				}
			}
		}
		if (t2.length > 0){
			if (!ops.parent(abstractAtts, (int[])rule[0], t2)){
				//The complete set does not contain descendents from all of the original items in the antecedent...
				size1 = itemParts.length;
				for (int k = 0; k < size1; k++){
					size2 = t2.length;
					for (int m = 0; m < size2; m++){
						if (ops.isChild(t2[m], itemParts[k], (int[])abstractAtts[1], (Object[])abstractAtts[2])){
							child = true;
							break;
						}
					}
					if (!child){
						//This original item has no descendent in the new antecedent...
						t3 = new int[size2 + 1];
						for (int n = 0; n < size2; n++){
							t3[n] = t2[n];
						}
						t3[size2] = itemParts[k];
						t2 = t3;
					}
					child = false;
				}
			}
			if (ops.validRule(abstractAtts, t2, (int[])rule[1])){
				t4 = new Object[2];
				t4[0] = t2;
				t4[1] = (int[])rule[1];
				tList.add(t4);
			}
		}
		size1 = tList.size();
		size2 = ci.size();
		if (size1 > 0){
			//Have possible rules that were determined to be hierarchically redundant and have now been recovered...
			for (int k = 0; k < size1; k++){
				//For each potential recovered rule, get the full itemset (antecedent & consequent)...
				potentialRuleItemSet = ops.unionRule((int[])((Object[])tList.get(k))[0], (int[])((Object[])tList.get(k))[1]);
				for (int m = 0; m < size2; m++){
					if (ops.compare((int[])ci.get(m), potentialRuleItemSet)){
						gList = (ArrayList)g.get(m);
						size3 = gList.size();
						for (int n = 0; n < size3; n++){
							if (ops.compare((int[])gList.get(n), (int[])((Object[])tList.get(k))[0])){
								//The potential rule matches a closed itemset & associated generator...
								basis = new Object[2];
								basis[0] = (int[])((Object[])tList.get(k))[0];
								basis[1] = (int[])((Object[])tList.get(k))[1];
								basisSupport = (Float)s.get(m);
								if (!ops.rulePresent(eMMRCopy, (int[])basis[0], (int[])basis[1])){
									eMMRCopy.add(basis);
									eMMSCopy.add(basisSupport);
								}
								m = ci.size();
								break;
							}
						}
					}
				}
			}
		}
		temp.add(eMMRCopy);
		temp.add(eMMSCopy);
		return temp;
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
			System.out.println("Data in exactMinMax - (Yue's Exact Basis - Gavin's Addition)");
			System.out.println("No of rules in exactMinMax:  " + exactMinMaxRule.size());
			for (int i = 0; i < exactMinMaxRule.size(); i++){
				System.out.println("" + ((String[])exactMinMaxRule.get(i))[0] + " ==> " +
												((String[])exactMinMaxRule.get(i))[1]);// + "    " + (Float)exactMinMaxSup.get(i));
			}
		}
		if (allExactRule != null){
			System.out.println("Data in allExact - (Yue's Exact Basis Expanded - Gavin's Addition)");
			System.out.println("No of rules in allExact:     " + allExactRule.size());
			for (int i = 0; i < allExactRule.size(); i++){
				System.out.println("" + ((String[])allExactRule.get(i))[0] + " ==> " +
												((String[])allExactRule.get(i))[1]);// + "    " + (Float)allExactSup.get(i));
			}
		}
		if (approxMinMaxRule != null){
			System.out.println("Data in approxMinMax - (Yue's Approx Basis - Gavin's Addition)");
			System.out.println("No of rules in approxMinMax:  " + approxMinMaxRule.size());
			for (int i = 0; i < approxMinMaxRule.size(); i++){
				System.out.println("" + ((String[])approxMinMaxRule.get(i))[0] + " ==> " +
												((String[])approxMinMaxRule.get(i))[1]);// + "    " + (Float)approxMinMaxSup.get(i) + "    " + (Float)approxMinMaxCon.get(i));
			}
		}
		if (allApproxRule != null){
			System.out.println("Data in allApprox - (Yue's Approx Basis Expanded - Gavin's Addition)");
			System.out.println("No of rules in allApprox:    " + allApproxRule.size());
			for (int i = 0; i < allApproxRule.size(); i++){
				System.out.println("" + ((String[])allApproxRule.get(i))[0] + " ==> " +
												((String[])allApproxRule.get(i))[1]);// + "    " + (Float)allApproxSup.get(i) + "" + (Float)allApproxCon.get(i));
			}
		}
	}
}