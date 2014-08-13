/**
 * ModAprioriAlgorithm class.
 * 
 * Start Date: 18 December 2006
 * @author Gavin Shaw
 * @version 1.0
 *
 * Log:
 *		  1.0 Start version of class.
 */
package Processing;

import java.util.ArrayList;

import Processing.BuildFrequentItemsets;
import Processing.Operations;

import Data.TransRecords;
import Data.CandidateList;
import Data.FItemsetList;

public class ModAprioriAlgorithm{

	private BuildFrequentItemsets bfi;
	
	private Operations ops;

	private CandidateList cl;

	private FItemsetList fl;

	private int minSupMode = 1;
	
	private byte itemsetSize;

	private ArrayList candidates, frequent, fCounters, fSupport, fTransList;
	
	private int[] cCounters, cLevel;
	
	private float[] cSupports;
	
	private Object[] cTransList;

	private String trl;

	/**
	 * ModAprioriAlgorithm method.
	 * Constructor.
	 * Method used to initialise this class and setup the variables
	 * to hold the candidates for frequent itemset status and the
	 * actual frequent itemsets.
	 */
	public ModAprioriAlgorithm(BuildFrequentItemsets owner){
		bfi = owner;
		cl = new CandidateList();
		fl = new FItemsetList();
		ops = new Operations();
	}

	/**
	 * clearData method.
	 * Method used to destroy and clear the variables that hold the
	 * data and references that were used to perform the construction
	 * of the frequent itemsets. This is to free up memory.
	 */
	final public void clearData(){
		if (cl != null){
			cl.clearData();
			cl = null;
		}
		if (fl != null){
			fl.clearData();
			fl = null;
		}
		if (candidates != null){
			candidates.clear();
			candidates = null;
		}
		if (frequent != null){
			frequent.clear();
			frequent = null;
		}
		if (cCounters != null){
			//cCounters.clear();
			cCounters = null;
		}
		if (fCounters != null){
			fCounters.clear();
			fCounters = null;
		}
		if (cSupports != null){
			//cSupports.clear();
			cSupports = null;
		}
		if (fSupport != null){
			fSupport.clear();
			fSupport = null;
		}
		if (cTransList != null){
			//cTransList.clear();
			cTransList = null;
		}
		if (fTransList != null){
			fTransList.clear();
			fTransList = null;
		}
	}

	/**
	 * runAlgorithm method.
	 * Method that oversees the execution of the Apriori algorithm
	 * for generating frequent itemsets. This method stops when
	 * there are no frequent itemsets generated at the previous
	 * itemset length or the length of the itemsets is going to
	 * exceed the total number of items/attributes within a
	 * transaction.
	 */
	final public void runAlgorithm(TransRecords tr, float minCon, float minSup){
		itemsetSize = 1;
		bfi.messageBox("Generating " + itemsetSize + " item list");
		createSet(tr);
		prune(tr, minSup, itemsetSize);
		itemsetSize++;
		while (frequent.size() != 0 && itemsetSize <= tr.getNumberAtts()){
			bfi.messageBox("Generating " + itemsetSize + " items list");
			candidates(tr, itemsetSize);
			prune(tr, minSup, itemsetSize);
			itemsetSize++;
		}
//		printCandidates();
//		printFrequentItemsets();
	}

	/**
	 * runAlgorithm method.
	 * Method that oversees the execution of the Apriori algorithm
	 * for generating frequent itemsets. This method stops when
	 * there are no frequent itemsets generated at the previous
	 * itemset length or the length of the itemsets is going to
	 * exceed the total number of items/attributes within a
	 * transaction.
	 */
	final public void runAlgorithm(TransRecords tr, float minCon, ArrayList minSup){
		minSupMode = ((Integer)minSup.get(0)).intValue();
		itemsetSize = 1;
		bfi.messageBox("Generating " + itemsetSize + " item list");
		createSet(tr);
		prune(tr, minSup, itemsetSize);
		itemsetSize++;
		while (frequent.size() != 0 && itemsetSize <= tr.getNumberAtts()){
			bfi.messageBox("Generating " + itemsetSize + " items list");
			candidates(tr, itemsetSize);
			prune(tr, minSup, itemsetSize);
			itemsetSize++;
		}
//		printCandidates();
//		printFrequentItemsets();
	}

	/**
	 * candidates method.
	 * Method used to determine the candidates to be the frequent
	 * itemsets for this dataset. The generation of candidates is
	 * based on the frequent itemsets already generated of length
	 * n-1.
	 */
	final private void candidates(TransRecords tr, byte itemsetSize){
		candidates = new ArrayList();
		int[] tc, fp1, fp2;
		boolean match = true, ancestor = false;
		String i1, i2;
		int f1, c1, c2, p1, p2, index;
		//Generate list of possible candidates...
		f1 = frequent.size();
		for (int i = 0; i < f1; i++){
			fp1 = (int[])frequent.get(i);
			for (int j = 0; j < f1; j++){
				if (i != j){
					fp2 = (int[])frequent.get(j);
					tc = ops.unionRule(fp1, fp2);
					if (tc.length == itemsetSize){
						//Valid length candidate...
						candidates.add(tc);
					}
				}
			}
		}
		//Remove invalid candidate itemsets...
		c1 = candidates.size();
		for (int i = 0; i < c1; i++){
			fp1 = (int[])candidates.get(i);
			//Remove duplicate candidate itemsets...
			for (int j = 0; j < c1; j++){
				if (i != j){
					fp2 = (int[])candidates.get(j);
					if (ops.compare(fp1, fp2)){
						//Entries match, remove entry at j...
						candidates.remove(j);
						c1--;
						j--;
					}
				}
			}
			//Remove candidates which do not have frequent sub itemsets...
			p1 = fp1.length;
			for (int j = 0; j < p1; j++){
				fp2 = new int[p1 - 1];
				index = 0;
				for (int k = 0; k < p1; k++){
					if (j != k){
						fp2[index] = fp1[k];
						index++;
					}
				}
				match = false;
				for (int k = 0; k < f1; k++){
					if (ops.compare(fp2, (int[])frequent.get(k))){
						match = true;
						break;
					}
				}
				if (!match){
					candidates.remove(i);
					c1--;
					i--;
					break;
				}
			}
			if (match){
				//Remove an candidate that contains two or more expanded attributes from the same stem...
				match = false;
				for (int j = 0; j < p1; j++){
					i1 = tr.getName(fp1[j]);
					c2 = i1.lastIndexOf('_');
					if (c2 != -1){
						i1 = i1.substring(0, c2);
					}
					for (int k = 0; k < p1; k++){
						if (j != k){
							i2 = tr.getName(fp1[k]);
							c2 = i2.lastIndexOf('_');
							if (c2 != -1){
								i2 = i2.substring(0, c2);
							}
							if (i1.equals(i2)){
								//These items share the same stem...
								candidates.remove(i);
								match = true;
								c1--;
								i--;
								j = p1;
								break;
							}
						}
					}
				}
			}
			if (match){
				//Check to see if any of the items are ancestors of other items within the itemset...
				ancestor = false;
				for (int j = 0; j < p1; j++){
					for (int k = 0; k < p1; k++){
						if (j != k){
							if (tr.getName(fp1[j]).startsWith(tr.getName(fp1[k]) + "-")){
								ancestor = true;
								j = p1;
								break;
							}
						}
					}
				}
				if (ancestor){
					candidates.remove(i);
					c1--;
					i--;
				}
			}
		}
	}

	/**
	 * prune method.
	 * Method used to prune off any candidates that are not
	 * frequent enough to be frequent itemsets. Each candidate
	 * is checked and the frequeny of the itemset is determined.
	 * From the frequency, the candidate itemset's support is
	 * generated. Only if this support is greater the the user
	 * specified threshold will the candidate become a frequent
	 * itemset. Otherwise the candidate will not become a frequent
	 * itemset. Regardless of which outcome, all candidates will be
	 * remembered so that the user can view all of the itemsets that
	 * were considered.
	 */
	final private void prune(TransRecords tr, float minSup, byte itemsetSize){
		boolean incCount = true;
		String[] trans;
		int[] can;
		int t1, c1, c2, n1, i1 = -1;
		c1 = candidates.size();
		cCounters = new int[c1];
		cSupports = new float[c1];
		cTransList = new Object[c1];
		for (int i = 0; i < c1; i++){
			cCounters[i] = 0;
		}
		//Determine candidate frequency...
		Object[] temp = tr.getRec();
		String[] name = tr.getNames();
		int[] ids = tr.getIDs();
		t1 = temp.length;
		n1 = name.length;
		//Check all transactions...
		for (int i = 0; i < t1; i++){
			trans = (String[])temp[i];
			//Check all candidates...
			for (int j = 0; j < c1; j++){
				can = (int[])candidates.get(j);
				c2 = can.length;
				//Check all items are present in this transaction...
				for (int k = 0; k < c2; k++){
					i1 = -1;
					for (int l = 0; l < n1; l++){
						if (ids[l] == can[k]){
							i1 = l;
							break;
						}
					}
					if (i1 != -1 && trans[i1].equals("0")){
						incCount = false;
						break;
					}
				}
				if (incCount){
					//All candidate items present...
					cCounters[j] = cCounters[j] + 1;
				}
				incCount = true;
			}
		}

		//Determine frequent itemset...
		frequent = new ArrayList();
		fCounters = new ArrayList();
		fSupport = new ArrayList();
		fTransList = new ArrayList();
		int transCount = tr.getNumberRecs();
		float support;
		//For each candidate...
		for (int i = 0; i < c1; i++){
			bfi.messageBox("Generating " + itemsetSize + " item list (Evaluating Candidate " + (i + 1) + " of " + c1);
			//Determine the support level...
			if (minSupMode == 1 || minSupMode == 2){
				support = (float)cCounters[i] / (float)transCount;
				cSupports[i] = support;
				if (support >= minSup){
					//Candidate support high enough to be frequent item...
					frequent.add((int[])candidates.get(i));
					fCounters.add(cCounters[i]);
					fSupport.add(support);
				}
			}
			else{
				support = (float)cCounters[i] / (float)transCount;
				cSupports[i] = support;
				if ((float)cCounters[i] >= minSup){
					//Candidate support/frequency high enough to be frequent item...
					frequent.add((int[])candidates.get(i));
					fCounters.add(cCounters[i]);
					fSupport.add(support);
				}
			}
		}
		if (candidates.size() > 0){
			cl.addEntry(candidates, cCounters, cSupports, cTransList);
		}
		if (frequent.size() > 0){
			fl.addEntry(frequent, fCounters, fSupport, fTransList);
		}
	}

	/**
	 * prune method.
	 * Method used to prune off any candidates that are not
	 * frequent enough to be frequent itemsets. Each candidate
	 * is checked and the frequeny of the itemset is determined.
	 * From the frequency, the candidate itemset's support is
	 * generated. Only if this support is greater the the user
	 * specified threshold will the candidate become a frequent
	 * itemset. Otherwise the candidate will not become a frequent
	 * itemset. Regardless of which outcome, all candidates will be
	 * remembered so that the user can view all of the itemsets that
	 * were considered.
	 */
	final private void prune(TransRecords tr, ArrayList minSup, int itemsetSize){
		boolean incCount = true;
		String[] trans;
		int[] can;
		int t1, c1, c2, n1, i1 = -1;
		int level = 1;
		c1 = candidates.size();
		cCounters = new int[c1];
		cLevel = new int[c1];
		cSupports = new float[c1];
		cTransList = new Object[c1];
		for (int i = 0; i < c1; i++){
			cCounters[i] = 0;
			can = (int[])candidates.get(i);
			t1 = can.length;
			for (int j = 0; j < t1; j++){
				n1 = tr.getName(can[j]).split("-").length;
				if (n1 > level){
					level = n1;
				}
			}
			cLevel[i] = level;
			level = 1;
		}
		//Determine candidate frequency...
		Object[] temp = tr.getRec();
		String[] name = tr.getNames();
		int[] ids = tr.getIDs();
		t1 = temp.length;
		n1 = name.length;
		//Check all transactions...
		for (int i = 0; i < t1; i++){
			trans = (String[])temp[i];
			//Check all candidates...
			for (int j = 0; j < c1; j++){
				can = (int[])candidates.get(j);
				c2 = can.length;
				//Check all items are present in this transaction...
				for (int k = 0; k < c2; k++){
					i1 = -1;
					for (int l = 0; l < n1; l++){
						if (ids[l] == can[k]){
							i1 = l;
							break;
						}
					}
					if (i1 != -1 && trans[i1].equals("0")){
						incCount = false;
						break;
					}
				}
				if (incCount){
					//All candidate items present...
					cCounters[j] = cCounters[j] + 1;
				}
				incCount = true;
			}
		}

		//Determine frequent itemset...
		frequent = new ArrayList();
		fCounters = new ArrayList();
		fSupport = new ArrayList();
		fTransList = new ArrayList();
		int transCount = tr.getNumberRecs();
		float support;
		//For each candidate...
		for (int i = 0; i < c1; i++){
			bfi.messageBox("Generating " + itemsetSize + " item list (Evaluating Candidate " + (i + 1) + " of " + c1);
			//Determine the support level...
			if (minSupMode == 1 || minSupMode == 2){
				support = (float)cCounters[i] / (float)transCount;
				cSupports[i] = support;
				if (support >= (Float)minSup.get(cLevel[i])){
					//Candidate support high enough to be frequent item...
					frequent.add((int[])candidates.get(i));
					fCounters.add(cCounters[i]);
					fSupport.add(support);
				}
			}
			else{
				support = (float)cCounters[i] / (float)transCount;
				cSupports[i] = support;
				if ((float)cCounters[i] >= Float.valueOf((Integer)minSup.get(cLevel[i])).floatValue()){
					//Candidate support/frequency high enough to be frequent item...
					frequent.add((int[])candidates.get(i));
					fCounters.add(cCounters[i]);
					fSupport.add(support);
				}
			}
		}
		if (candidates.size() > 0){
			cl.addEntry(candidates, cCounters, cSupports, cTransList);
		}
		if (frequent.size() > 0){
			fl.addEntry(frequent, fCounters, fSupport, fTransList);
		}
	}

	/**
	 * createSet method.
	 * Method used to create the initial list of length 1 candidates
	 * from the loaded dataset.
	 */
	final private void createSet(TransRecords tr){
		int[] t;
		int[] ids = tr.getIDs();
		int n1 = ids.length;
		candidates = new ArrayList(n1);
		for (int i = 0; i < n1; i++){
			t = new int[1];
			t[0] = ids[i];
			candidates.add(i, t);
		}
	}

	/**
	 * getFrequentItemsets method.
	 * Method used to get and pass back a reference to the data class
	 * that holds the frequent itemsets generated by this algorithm.
	 */
	final public FItemsetList getFrequentItemsets(){
		return fl;
	}

	/**
	 * getCandidateLists method.
	 * Method used to get and pass back a reference to the data class
	 * that holds the candidates that were considered to be frequent
	 * itemsets.
	 */
	final public CandidateList getCandidateLists(){
		return cl;
	}

	/**
	 * printCandidates method.
	 * Method use to print the list of candidate itemsets.
	 */
	final private void printCandidates(){
		cl.printData();
	}

	/**
	 * printFrequentItemsets method.
	 * Method use to print the list of candidate itemsets.
	 */
	final private void printFrequentItemsets(){
		fl.printData();
	}
}