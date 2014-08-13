/**
 * ModCrossLevelAlgorithm class.
 * 
 * Start Date: 24 July 2007
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

public class ModCrossLevelAlgorithm{

	private TransRecords tempRec;

	private BuildFrequentItemsets bfi;
	
	private Operations ops;

	private CandidateList cl;

	private FItemsetList fl;

	private byte itemsetSize;

	private ArrayList candidates, frequent, frequent1, frequent1Master, fCounters, fSupport, fTransList;
	
	private int[] cCounters;
	
	private float[] cSupports;
	
	private Object[] cTransList;

	private int numberOfLevels, minSupMode = 1;

	private String trl;

	/**
	 * ModCrossLevelAlgorithm method.
	 * Constructor.
	 * Method used to initialise this class and setup the variables
	 * to hold the candidates for frequent itemset status and the
	 * actual frequent itemsets.
	 */
	public ModCrossLevelAlgorithm(BuildFrequentItemsets owner){
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
		if (frequent1 != null){
			frequent1.clear();
			frequent1 = null;
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
	 * Method that oversees the execution of the Cross-Level algorithm
	 * for generating frequent itemsets.
	 */
	final public void runAlgorithm(TransRecords tr, float minCon, float minSup){
		deriveNumberOfLevels(tr.getName(1));
		tempRec = new TransRecords();
		tempRec.addData(tr.getRec(), tr.getNames(), tr.getVal1(), tr.getVal2(), tr.getIDs());

		//Find level 1 frequent itemsets...
		itemsetSize = 1;
		bfi.messageBox("Generating level 1 - " + itemsetSize + " item list");
		deriveTopicsForLevel(tr, tempRec.getNames(), 1);
		prune(tempRec, minSup, itemsetSize);
		frequent1 = new ArrayList();
		frequent1Master = new ArrayList();
		copyFreqItemsets();
		addFreq1Itemsets();
//		filterTransTable();
		itemsetSize++;
		while (frequent.size() != 0 && itemsetSize <= tempRec.getNumberAtts()){
			bfi.messageBox("Generating level 1 - " + itemsetSize + " item list");
			candidates(tr/*tempRec*/, 1, itemsetSize);
			prune(tempRec, minSup, itemsetSize);
			itemsetSize++;
		}

		//Find level 2 to n frequent itemsets...
		for (int level = 2; level <= numberOfLevels && frequent1.size() != 0; level++){
			itemsetSize = 1;
			bfi.messageBox("Generating level " + level + " - " + itemsetSize + " item list");
			deriveTopicsForLevel(tr, tempRec.getNames(), level);
			prune(tempRec, minSup, itemsetSize);
			frequent1.clear();
			copyFreqItemsets();
			addFreq1Itemsets();
//			filterTransTable();
			itemsetSize++;
			while (frequent.size() != 0 && itemsetSize <= tempRec.getNumberAtts()){
				bfi.messageBox("Generating level " + level + " - " + itemsetSize + " item list");
				candidates(tr/*tempRec*/, level, itemsetSize);
				prune(tempRec, minSup, itemsetSize);
				itemsetSize++;
			}
		}
	}

	/**
	 * runAlgorithm method.
	 * Method that oversees the execution of the Cross-Level algorithm
	 * for generating frequent itemsets.
	 */
	final public void runAlgorithm(TransRecords tr, float minCon, ArrayList minSup){
		minSupMode = ((Integer)minSup.get(0)).intValue();
		deriveNumberOfLevels(tr.getName(1));
		tempRec = new TransRecords();
		tempRec.addData(tr.getRec(), tr.getNames(), tr.getVal1(), tr.getVal2(), tr.getIDs());

		//Find level 1 frequent itemsets...
		itemsetSize = 1;
		bfi.messageBox("Generating level 1 - " + itemsetSize + " item list");
		deriveTopicsForLevel(tr, tempRec.getNames(), 1);
		if (minSupMode != 3){
			prune(tempRec, (Float)minSup.get(1), itemsetSize);
		}
		else{
			prune(tempRec, Float.valueOf((Integer)minSup.get(1)).floatValue(), itemsetSize);
		}
		frequent1 = new ArrayList();
		frequent1Master = new ArrayList();
		copyFreqItemsets();
		addFreq1Itemsets();
//		filterTransTable();
		itemsetSize++;
		while (frequent.size() != 0 && itemsetSize <= tempRec.getNumberAtts()){
			bfi.messageBox("Generating level 1 - " + itemsetSize + " item list");
			candidates(tr/*tempRec*/, 1, itemsetSize);
			if (minSupMode != 3){
				prune(tempRec, (Float)minSup.get(1), itemsetSize);
			}
			else{
				prune(tempRec, Float.valueOf((Integer)minSup.get(1)).floatValue(), itemsetSize);
			}
			itemsetSize++;
		}

		//Find level 2 to n frequent itemsets...
		for (int level = 2; level <= numberOfLevels && frequent1.size() != 0; level++){
			itemsetSize = 1;
			bfi.messageBox("Generating level " + level + " - " + itemsetSize + " item list");
			deriveTopicsForLevel(tr, tempRec.getNames(), level);
			if (minSupMode != 3){
				prune(tempRec, (Float)minSup.get(level), itemsetSize);
			}
			else{
				prune(tempRec, Float.valueOf((Integer)minSup.get(level)).floatValue(), itemsetSize);
			}
			frequent1.clear();
			copyFreqItemsets();
			addFreq1Itemsets();
//			filterTransTable();
			itemsetSize++;
			while (frequent.size() != 0 && itemsetSize <= tempRec.getNumberAtts()){
				bfi.messageBox("Generating level " + level + " - " + itemsetSize + " item list");
				candidates(tr/*tempRec*/, level, itemsetSize);
				if (minSupMode != 3){
					prune(tempRec, (Float)minSup.get(level), itemsetSize);
				}
				else{
					prune(tempRec, Float.valueOf((Integer)minSup.get(level)).floatValue(), itemsetSize);
				}
				itemsetSize++;
			}
		}
	}

	final private void deriveNumberOfLevels(String topicNames){
		numberOfLevels = topicNames.split("-").length;
	}

	final private void deriveTopicsForLevel(TransRecords tr, String[] topicNames, int level){
		String[] nameparts;
		String name;
		candidates = new ArrayList();
		ArrayList topics = new ArrayList();
		ArrayList ids = new ArrayList();
		ArrayList descList = new ArrayList();
		ArrayList desc;
		int index = -1;
		int maxID = tr.getMaxID();
		int[] t;
		int t1 = topicNames.length;

		for (int i = 0; i < t1; i++){
			name = new String();
			nameparts = topicNames[i].split("-");
			for (int j = 0; j < level; j++){
				if (nameparts.length >= level){
					if (name.length() == 0 || name == null){
						name = name + nameparts[j];
					}
					else{
						name = name + "-" + nameparts[j];
					}
				}
			}
			if (name.equals(topicNames[i])){
				t = new int[1];
				t[0] = tempRec.getID(i);
				candidates.add(t);
//				tr.addExtra(name, tempRec.getID(i));
//				tempRec.addExtra(name, tempRec.getID(i));
			}
			else if (name.length() > 0 && (index = topics.indexOf(name)) == -1){
				maxID++;
				topics.add(name);
				t = new int[1];
				t[0] = maxID;
				ids.add(maxID);
				candidates.add(t);
				desc = new ArrayList();
				desc.add(tempRec.getID(i));
				descList.add(desc);
			}
			else if (index != -1){
				desc = (ArrayList)descList.get(index);
				desc.add(tempRec.getID(i));
				descList.set(index, desc);
			}
		}
		tr.addExtra(topics, ids, descList);
		tempRec.addExtra(topics, ids, descList);
	}

	/**
	 * candidates method.
	 * Method used to determine the candidates to be the frequent
	 * itemsets for this dataset. The generation of candidates is
	 * based on the frequent itemsets already generated of length
	 * n-1.
	 */
	final private void candidates(TransRecords tr, int level, byte itemsetSize){
		candidates = new ArrayList();
		int[] tc, fp1, fp2;
		Object[] freq;
		boolean match = true, ancestor = false, levelCheck = false;
		String i1, i2;
		int f1, c1, c2, p1, p2, index;
		//Generate list of possible candidates...
		if (level == 1 || itemsetSize != 2){
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
		}
		else{
			f1 = frequent1Master.size();
			for (int i = 0; i < f1; i++){
				fp1 = (int[])frequent1Master.get(i);
				for (int j = 0; j < f1; j++){
					if (i != j){
						fp2 = (int[])frequent1Master.get(j);
						tc = ops.unionRule(fp1, fp2);
						if (tc.length == itemsetSize){
							//Valid length candidate...
							candidates.add(tc);
						}
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
			if (level == 1 || itemsetSize != 2){
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
					freq = fl.getFrequent(itemsetSize - 1);
					p2 = freq.length;
					for (int k = 0; k < p2; k++){
						if (ops.compare(fp2, (int[])freq[k])){
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
			}
			else{
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
						if (ops.compare(fp2, (int[])frequent1Master.get(k))){
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
			}
			if (match){
				//Remove an candidate that contains two or more expanded attributes from the same stem...
				match = true;
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
								match = false;
								c1--;
								i--;
								j = p1;
								break;
							}
						}
					}
				}
			}
			if (level >= 2 && match){
				//Check to see if any of the items are ancestors of other items within the itemset...
				ancestor = false;
				levelCheck = false;
				for (int j = 0; j < p1; j++){
					if (tr.getName(fp1[j]).split("-").length == level){
						levelCheck = true;
					}
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
				if (ancestor || !levelCheck){
					candidates.remove(i);
					//match = false;
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
		ArrayList matches;
		int incCount = 0;
		String[] trans;
		int[] can;
		int t1, t2, c1, c2, n1, m1;
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
			t2 = trans.length;
			//Check all candidates...
			for (int j = 0; j < c1; j++){
				can = (int[])candidates.get(j);
				c2 = can.length;
				//Check all items are present in this transaction...
				for (int k = 0; k < c2; k++){
					matches = tr.getMatches(can[k]);
					m1 = matches.size();
					for (int l = 0; l < m1; l++){
						if (((Integer)matches.get(l) < t2) && (trans[(Integer)matches.get(l)].equals("1"))){
							incCount++;
							break;
						}
					}
				}
				if (incCount == c2){
					//All candidates present, increase counter...
					cCounters[j] = cCounters[j] + 1;
				}
				incCount = 0;
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
		if (c1 > 0){
			cl.addEntryCollapse(itemsetSize, candidates, cCounters, cSupports, cTransList);
		}
		if (frequent.size() > 0){
			fl.addEntryCollapse(itemsetSize, frequent, fCounters, fSupport, fTransList);
		}
	}

	final private void copyFreqItemsets(){
		int[] temp;
		for (int i = 0; i < frequent.size(); i++){
			temp = (int[])frequent.get(i);
			frequent1.add(temp);
		}
	}

	final private void addFreq1Itemsets(){
		int[] temp;
		for (int i = 0; i < frequent.size(); i++){
			temp = (int[])frequent.get(i);
			frequent1Master.add(temp);
		}
	}

	final private void filterTransTable(){
		int r1, n1, i1, i2, t1, t2;
		ArrayList rec, names, ids;
		

		Object[] rec1 = tempRec.getRec();
		r1 = rec1.length;
		rec = new ArrayList(r1);
		t2 = ((String[])rec1[0]).length;
		for (int i = 0; i < r1; i++){
			rec.add(i, ((String[])rec1[i]));
		}

		String[] names1 = tempRec.getNames();
		n1 = names1.length;
		names = new ArrayList(n1);
		for (int i = 0; i < n1; i++){
			names.add(i, names1[i]);
		}

		int[] ids1 = tempRec.getIDs();
		n1 = ids1.length;
		ids = new ArrayList();
		for (int i = 0; i < n1; i++){
			ids.add(i, ids1[i]);
		}

		String[] trans1, trans2;
		boolean validTopic = false, validTrans = false;
		int index = 0;
		int[] items = tempRec.getMatches(ops.union(frequent));
		i1 = items.length;
		i2 = names.size();
		r1 = rec.size();

		for (int i = 0; i < i2; i++){
			if (i < t2){
				for (int j = 0; j < i1; j++){
					if ((Integer)ids.get(i) == items[j]){
						validTopic = true;
						break;
					}
				}
				if (!validTopic){
					//Remove this topic from the transaction records...
					names.remove(i);
					ids.remove(i);
					i2--;
					t2--;
					for (int j = 0; j < r1; j++){
						trans2 = ((String[])rec.get(j));
						t1 = trans2.length;
						trans1 = new String[t1 - 1];
						for (int k = 0; k < t1; k++){
							if (k != i){
								trans1[index] = trans2[k];
								if (trans1[index].equals("1")){
									validTrans = true;
								}
								index++;
							}
						}
						//Remove a transaction if it is completely empty (eg all 0)...
						if (validTrans){
							rec.set(j, trans1);
						}
						else{
							rec.remove(j);
							r1--;
							j--;
						}
						index = 0;
						validTrans = false;
					}
					i--;
				}
				validTopic = false;
			}
		}
		tempRec.addRec(rec);
		tempRec.addNames(names);
		tempRec.addIDs(ids);
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
}