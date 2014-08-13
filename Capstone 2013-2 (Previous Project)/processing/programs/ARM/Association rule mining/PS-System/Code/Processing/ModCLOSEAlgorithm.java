/**
 * CLOSEAlgorithm class.
 * 
 * Start Date: 02 January 2007
 * @author Gavin Shaw
 * @version 1.0
 *
 * Log:
 *		  1.0 Start version of class.
 */
package Processing;

import java.util.ArrayList;

import Processing.BuildFrequentClosedItemsets;
import Processing.Operations;

import Data.FItemsetList;
import Data.FCItemsetList;

public class ModCLOSEAlgorithm{

	private BuildFrequentClosedItemsets bfci;
	
	private Operations ops;

	private FCItemsetList fcl;

	private int itemsetSize;

	private boolean isGen, isClosed;

	private ArrayList frequentItems, freqSupport, itemList, supportList;

	private ArrayList closedItemList, ciSupport, generators, gSupport;

	private ArrayList tempAssociations, tempSupports;

	private Object[] attNames, attLeaf, freqClosed;

	private int[] currentItem;

	private float currentSupport;

	/**
	 * CLOSEAlgorithm method.
	 * Constructor.
	 * Method used to initialise this class.
	 */
	public ModCLOSEAlgorithm(BuildFrequentClosedItemsets owner, Object[] names, Object[] leaves){
		bfci = owner;
		ops = new Operations();
		attNames = names;
		attLeaf = leaves;
	}

	/**
	 * clearData method.
	 * Method used to destroy and clear the variables that hold the
	 * data and references that were used to perform the construction
	 * of the frequent closed itemsets. This is to free up memory.
	 */
	final public void clearData(){
		if (frequentItems != null){
			frequentItems.clear();
			frequentItems = null;
		}
		if (freqSupport != null){
			freqSupport.clear();
			freqSupport = null;
		}
		if (itemList != null){
			itemList.clear();
			itemList = null;
		}
		if (supportList != null){
			supportList.clear();
			supportList = null;
		}
		if (closedItemList != null){
			closedItemList.clear();
			closedItemList = null;
		}
		if (ciSupport != null){
			ciSupport.clear();
			ciSupport = null;
		}
		if (generators != null){
			generators.clear();
			generators = null;
		}
		if (gSupport != null){
			gSupport.clear();
			gSupport = null;
		}
		if (tempAssociations != null){
			tempAssociations.clear();
			tempAssociations = null;
		}
		if (fcl != null){
			fcl.clearData();
			fcl = null;
		}
	}

	/**
	 * runAlgorithm method.
	 * Method that oversees the execution of the CLOSE+ algorithm
	 * for generating frequent closed itemsets. This method stops when
	 * all of the frequent itemset previously generated has been looked
	 * at and processed to see if it a closed itemset. Not all frequent
	 * itemsets will always become closed itemsets. Every closed itemset
	 * will also have at least one generator associated with it.
	 */
	final public void runAlgorithm(FItemsetList flist, float minCon, float minSup){
		fcl = new FCItemsetList();
		int f1, i1;
		tempAssociations = new ArrayList();
		tempSupports = new ArrayList();
		closedItemList = new ArrayList();
		ciSupport = new ArrayList();
		generators = new ArrayList();
		gSupport = new ArrayList();
		itemsetSize = 1;

		frequentItems = flist.getFrequent();
		freqSupport = flist.getSupports();
		f1 = frequentItems.size();
		for (int i = 0; i < f1; i++){
			itemList = (ArrayList)frequentItems.get(i);
			supportList = (ArrayList)freqSupport.get(i);
			i1 = itemList.size();
			for (int j = 0; j < i1; j++){
				currentItem = (int[])itemList.get(j);
				currentSupport = (Float)supportList.get(j);
				isGen = true;
				isGenerator();
				isClosed = true;
				isClosedSet();
				if (isClosed){
					//Current itemset is closed...
					determineGenerators();
				}
			}
			itemsetSize++;
		}
		buildList();
//		printData();
	}

	/**
	 * isGenerator method.
	 * Method used to determine if a frequent itemset is
	 * to be considered a generator for a closed itemset.
	 */
	final private void isGenerator(){
		int size;
		if (itemsetSize == 1){
			//1-itemset, therefore automatically is a generator...
			if (isLeaf(currentItem)){
				//Test to see if the generator is composed of only leaf/bottom items...
				generators.add(currentItem);
				gSupport.add(currentSupport);
			}
		}
		else{
			//Larger than 1-itemset so search through subsets...
			Object[] itemSubset = genSubsets();
			ArrayList subsetFreqItemList = (ArrayList)frequentItems.get(itemsetSize - 2);
			ArrayList subsetSupportList = (ArrayList)freqSupport.get(itemsetSize - 2);
			size = itemSubset.length;
			for (int i = 0; i < size; i++){
				//For all the subsets that are possible from the current itemset...
				int index = matchSet(subsetFreqItemList, (int[])itemSubset[i]);
				if (index != -1){
					//Get the support of the current itemset's subset...
					float subsetSupport = (Float)subsetSupportList.get(index);
					if (currentSupport >= subsetSupport){
						//If the support of the current itemset is equal or greater than the subset...
						isGen = false;
						break;
					}
				}
			}
			if (isGen){
				//Itemset is a valid generator...
				if (isLeaf(currentItem)){
					//Test to see if the generator is composed of only leaf/bottom items...
					generators.add(currentItem);
					gSupport.add(currentSupport);
				}
			}
		}
	}

	/**
	 * isClosedSet method.
	 * Method used to determine if a frequent itemset is
	 * to be considered a closed itemset.
	 */
	final private void isClosedSet(){
		if (itemsetSize < frequentItems.size()){
			ArrayList itemSuperset = genSupersets();
			ArrayList supersetItem = (ArrayList)itemSuperset.get(0);
			ArrayList supersetSupport = (ArrayList)itemSuperset.get(1);
			for (int i = 0; i < supersetSupport.size(); i++){
				//For all the supersets of the current itemset...
				if (currentSupport <= (Float)supersetSupport.get(i)){
					//If the support of the current itemset is equal or less than the superset...
					isClosed = false;
					i = supersetSupport.size();
				}
			}
			if (isClosed){
				//Itemset is closed...
				closedItemList.add(currentItem);
				ciSupport.add(currentSupport);
			}
		}
		else{
			closedItemList.add(currentItem);
			ciSupport.add(currentSupport);
		}
	}

	/**
	 * determineGenerators method.
	 * Method used to determine the generators of the current
	 * frequent itemset, which has been determined to be a
	 * closed itemset.
	 */
	final private void determineGenerators(){
		int size1 = generators.size();
		//Now process the generator list and see if each generator is a subset...
		for (int i = 0; i < size1; i++){
			int[] genCom = (int[])generators.get(i);
			if (ops.subset(currentItem, genCom)){
				//Generator is a subset of the current closed itemset...
				if ((Float)gSupport.get(i) == currentSupport){
					//Support equal, so associate generator and closed itemset...
					freqClosed = new Object[2];
					freqClosed[0] = (int[])generators.get(i);
					freqClosed[1] = currentItem;
					tempAssociations.add(freqClosed);
					tempSupports.add(currentSupport);
				}
			}
		}
	}

	/**
	 * buildList method.
	 * Method that takes the final generated list of closed itemsets, their
	 * corresponding generators and transaction mappings and enters/stores
	 * them in the object that holds the list for use later on in the program.
	 */
	final private void buildList(){
		for (int i = 0; i < tempAssociations.size(); i++){
			fcl.addEntry((Object[])tempAssociations.get(i), (Float)tempSupports.get(i));
		}
	}

	/**
	 * genSubsets method.
	 * Method used to generate a list of all of the different
	 * posssible subsets of length n-1 from the current
	 * frequent itemset, closed itemset or generator.
	 */
	final private Object[] genSubsets(){
		int index = 0;
		int s = currentItem.length;
		int[] subSet;
		Object[] subsets = new Object[s];
		for (int i = 0; i < s; i++){
			subSet = new int[s -1];
			for (int j = 0; j < s; j++){
				if (j != i){
					subSet[index] = currentItem[j];
					index++;
				}
			}
			subsets[i] = subSet;
			index = 0;
		}
		return subsets;
	}

	/**
	 * genSupersets method.
	 * Method used to determine and generate a list of all the
	 * different existing supersets of the current frequent
	 * itemset, closed itemset or generator with length n+1.
	 * The supersets generated are based on the previously
	 * generated frequent itemsets and must be contained within
	 * that list.
	 */
	final private ArrayList genSupersets(){
		int size;
		int[] set1;
		ArrayList supersetFreqItemList = (ArrayList)frequentItems.get(itemsetSize);
		ArrayList supersetSupportList = (ArrayList)freqSupport.get(itemsetSize);
		ArrayList supersetInfo = new ArrayList();
		ArrayList supersetsItems = new ArrayList();
		ArrayList supersetsSupport = new ArrayList();
		size = supersetFreqItemList.size();
		for (int i = 0; i < size; i++){
			set1 = (int[])supersetFreqItemList.get(i);
			if (ops.supset(set1, currentItem)){
				supersetsItems.add(set1);
				supersetsSupport.add((Float)supersetSupportList.get(i));
			}
		}
		supersetInfo.add(supersetsItems);
		supersetInfo.add(supersetsSupport);
		return supersetInfo;
	}

	/**
	 * matchSet method.
	 * Method used to determine the index position of a given set in
	 * a list of sets. Used when a subset of a generator is built and
	 * the subset needs to be found in the list of frequent itemsets of
	 * that length so that the support can be determined. In this case
	 * set2 is the generator's subset.
	 */
	final private int matchSet(ArrayList list1, int[] set2){
		int[] set1;
		int size = list1.size();
		for (int i = 0; i < size; i++){
			set1 = (int[])list1.get(i);
			if (ops.compare(set1, set2)){
				return i;
			}
		}
		return -1;
	}

	final private boolean isLeaf(int[] gen){
		int[] leaves = (int[])attLeaf[1];
		int i1 = gen.length;
		int i2 = leaves.length;
		boolean match = false;
		for (int i = 0; i < i1; i++){
			match = false;
			match = ops.present(leaves, gen[i]);
			if (!match){
				return false;
			}
		}
		return match;
	}
	
	final private boolean containsLeaf(int[] itemset){
		int[] leaves = (int[])attLeaf[1];
		int i1 = itemset.length;
		int i2 = leaves.length;
		boolean match = false;
		
		for (int i = 0; i < i1; i++){
			match = false;
			match = ops.present(leaves, itemset[i]);
			if (match){
				return true;
			}
		}
		return match;
	}
	
	/**
	 * getFreqClosed method.
	 * Method that passes back a reference to the object instance that holds the
	 * list of closed itemsets, generators and transaction mappings. This allows
	 * the GUI component to access this data and present it to the user for viewing.
	 */
	final public FCItemsetList getFreqClosed(){
		return fcl;
	}

	/**
	 * printData method.
	 * Method use to print the list of frequent closed itemsets.
	 */
	final private void printData(){
		System.out.println("Associated Generators, Closed Itemsets and Supports...");
		for (int i = 0; i < tempAssociations.size(); i++){
			System.out.println(((String[])tempAssociations.get(i))[0] + "  " + ((String[])tempAssociations.get(i))[1] + "  " + (Float)tempSupports.get(i));
		}
	}
}