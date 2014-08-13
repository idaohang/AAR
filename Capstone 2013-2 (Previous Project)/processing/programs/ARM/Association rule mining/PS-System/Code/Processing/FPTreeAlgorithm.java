/**
 * FPTreeAlgorithm class.
 * 
 * Start Date: 26 March 2008
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
import Data.FPTreeNode;
import Data.FPTHeaderList;

public class FPTreeAlgorithm{

	private BuildFrequentItemsets bfi;
	
	private Operations ops;

	private CandidateList cl;

	private FItemsetList fl;

	private int minSupMode = 1;

	private byte itemsetSize;

	private ArrayList candidates, frequent, fCounters, fSupport, fTransList;
	
	private int[] cCounters;
	
	private float[] cSupports;
	
	private Object[] cTransList;

	private FPTreeNode root, cNode;

	private FPTHeaderList headerlist;

	/**
	 * FPTreeAlgorithm method.
	 * Constructor.
	 * Method used to initialise this class and setup the variables
	 * to hold the candidates for frequent itemset status and the
	 * actual frequent itemsets.
	 */
	public FPTreeAlgorithm(BuildFrequentItemsets owner){
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
		bfi.messageBox("Generating 1 item list");
		headerlist = new FPTHeaderList();
		createSet(tr);
		prune(tr, minSup, 1);
		if (frequent.size() != 0){
			//Create the root of the FP-Tree...
			bfi.messageBox("Generating FP-Tree structure");
			root = new FPTreeNode("root", 0, 1);
			buildTree(tr);
			bfi.messageBox("Finished generating FP-Tree structure, extracting frequent itemsets");
			extractFI();
//			headerlist.print();
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
	final private void prune(TransRecords tr, float minSup, int itemsetSize){
		boolean incCount = true;
		String[] trans;
		int[] can;
		int f1, f2, t1, c1, c2, n1, i1 = -1;
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
		c2 = candidates.size();
		for (int i = 0; i < c2; i++){
//			bfi.messageBox("Generating " + itemsetSize + " item list (Evaluating Candidate " + (i + 1) + " of " + candidates.size());
			//Determine the support level...
			if (minSupMode == 1 || minSupMode == 2){
				support = (float)(cCounters[i]) / (float)transCount;
				cSupports[i] = support;
				if (support >= minSup){
					//Candidate support high enough to be frequent item...
					f1 = frequent.size();
					if (f1 == 0){
						//First item in list...
						frequent.add((int[])candidates.get(i));
						fCounters.add(cCounters[i]);
						fSupport.add(support);
						headerlist.addItem((String)candidates.get(i));
					}
					else{
						//Sort so that frequent items are in decreasing order...
						f2 = fCounters.size();
						for (int j = 0; j < f2; j++){
							if ((Integer)fCounters.get(j) < cCounters[i]){
								//Insert new item at this position and break out of loop...
								frequent.add(j, (String)candidates.get(i));
								fCounters.add(j, cCounters[i]);
								fSupport.add(j, support);
								headerlist.addItem(j, (String)candidates.get(i));
								break;
							}
							else if (j == f2 - 1){
								//Reached the end of the list so add new item at the end...
								frequent.add((String)candidates.get(i));
								fCounters.add(cCounters[i]);
								fSupport.add(support);
								headerlist.addItem((String)candidates.get(i));
								break;
							}
						}
					}
				}
			}
			else{
				support = (float)(cCounters[i]) / (float)transCount;
				cSupports[i] = support;
				if ((float)(cCounters[i]) >= minSup){
					//Candidate support/frequency high enough to be frequent item...
					f1 = frequent.size();
					if (f1 == 0){
						//First item in list...
						frequent.add((String)candidates.get(i));
						fCounters.add(cCounters[i]);
						fSupport.add(support);
						headerlist.addItem((String)candidates.get(i));
					}
					else{
						//Sort so that frequent items are in decreasing order...
						f2 = fCounters.size();
						for (int j = 0; j < f2; j++){
							if ((Integer)fCounters.get(j) < cCounters[i]){
								//Insert new item at this position and break out of loop...
								frequent.add(j, (String)candidates.get(i));
								fCounters.add(j, cCounters[i]);
								fSupport.add(j, support);
								headerlist.addItem(j, (String)candidates.get(i));
								break;
							}
							else if (j == f2 - 1){
								//Reached the end of the list so add new item at the end...
								frequent.add((String)candidates.get(i));
								fCounters.add(cCounters[i]);
								fSupport.add(support);
								headerlist.addItem((String)candidates.get(i));
								break;
							}
						}
					}
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

	final private void buildTree(TransRecords tr){
		int t1, n1, i1 = -1;
		String[] trans;
		Object[] temp = tr.getRec();
		String[] name = tr.getNames();
		t1 = temp.length;
		n1 = name.length;
		int transCount = tr.getNumberRecs();
		

		//For each transaction/record...
//		System.out.println("S");
		for (int i = 0; i < t1; i++){
//			System.out.println("1 - " + i);
			trans = (String[])temp[i];
			cNode = root;
			for (int j = 0; j < frequent.size(); j++){
//				System.out.println("2 - " + j);
				i1 = -1;
				for (int k = 0; k < n1; k++){
					if (name[k].equals((String)frequent.get(j))){
						i1 = k;
						break;
					}
				}
				if (i1 != -1 && trans[i1].equals("1")){
					//Frequent item is present in this transaction so add to the tree...
//					System.out.println("3");
					if (cNode.childPresent((String)frequent.get(j))){
//						System.out.println("4");
						//Item already exists as a child of the current node, so update the child...
						cNode = cNode.getChild((String)frequent.get(j));
						cNode.incCount();
						cNode.updateSupport(transCount);
					}
					else{
//						System.out.println("5");
						//Item does not exist as a child of the current node and needs to be added...
//						System.out.println("5-1");
						cNode.addChild((String)frequent.get(j), 1, transCount);
//						System.out.println("5-2");
						cNode = cNode.getChild((String)frequent.get(j));
						headerlist.addNode(cNode, (String)frequent.get(j));
//						System.out.println("5-3");
					}
				}
//				System.out.println("2E");
			}
//			System.out.println("1E");
		}
	}

	final private void extractFI(){
//		frequent = new ArrayList();
//		fCounters = new ArrayList();
//		fSupport = new ArrayList();
//		fTransList = new ArrayList();
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