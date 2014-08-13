/**
 * ClosureGenAlgorithm class.
 * 
 * Start Date: 08 January 2007
 * @author Gavin Shaw
 * @version 1.0
 *
 * Log:
 *		  1.0 Start version of class.
 */
package Processing;

import java.util.ArrayList;

import Processing.BuildFrequentClosedItemsets;

import Data.FItemsetList;
import Data.FCItemsetList;
import Data.TransRecList;

public class ClosureGenAlgorithm{

	private BuildFrequentClosedItemsets bfci;

	private FCItemsetList fcl;

	private TransRecList trl;

	private int itemsetSize;

	private ArrayList candidates, support, transMap, genList, prevCandidates, prevSupport, prevTransMap, prevGenList;

	private ArrayList closedItemsets;

	private float currentSupport;

	/**
	 * ClosureGenAlgroithm method.
	 * Constructor.
	 * Initialises this class and sets up a link to the object/class
	 * that called this class. This allows this class to access its
	 * parent and use methods contained within it (mainly to report
	 * back to the user while running).
	 */
	public ClosureGenAlgorithm(BuildFrequentClosedItemsets owner){
		bfci = owner;
	}

	/**
	 * clearData method.
	 * Method used to clear the data, variables and objects held
	 * by this class when they are no longer needed. This frees
	 * memory for other parts of the program.
	 */
	final public void clearData(){
		if (candidates != null){
			candidates.clear();
			candidates = null;
		}
		if (support != null){
			support.clear();
			support = null;
		}
		if (transMap != null){
			transMap.clear();
			transMap = null;
		}
		if (genList != null){
			genList.clear();
			genList = null;
		}
		if (prevCandidates != null){
			prevCandidates.clear();
			prevCandidates = null;
		}
		if (prevSupport != null){
			prevSupport.clear();
			prevSupport = null;
		}
		if (prevTransMap != null){
			prevTransMap.clear();
			prevTransMap = null;
		}
		if (prevGenList != null){
			prevGenList.clear();
			prevGenList = null;
		}
		if (fcl != null){
			fcl.clearData();
			fcl = null;
		}
/*		if (trl != null){
			trl.clearData();
			trl = null;
		}
*/	}

	/**
	 * runAlgrotihm method.
	 * Method that oversees the execution of the ClosureGen algorithm.
	 * Initialise all the variables and manages the main loop of the
	 * algorithm. Uses other methods to perform most of the calculations.
	 */
	final public void runAlgorithm(FItemsetList flist, float minCon, float minSup){
		fcl = new FCItemsetList();
		candidates = new ArrayList();
		support = new ArrayList();
		transMap = new ArrayList();
		genList = new ArrayList();
		prevCandidates = new ArrayList();
		prevSupport = new ArrayList();
		prevTransMap = new ArrayList();
		prevGenList = new ArrayList();

		closedItemsets = new ArrayList();

		itemsetSize = 1;
		System.out.println("Itemset Size: " + itemsetSize);
		//Call to setup the inital itemsets (length 1-itemsets).
		setInitList(flist);
		do{
			itemsetSize++;
			System.out.println("Itemset Size: " + itemsetSize);
			//Call to generate the candidate itemsets for closure.
			candidateGen(flist, minSup);
			//Call to determine the closed itemsets and their generators.
			closureGen();
//			printData();
		}
		while (candidates.size() != 0);
		System.out.println("Start build");
		buildList();
	}

	/**
	 * setInitList method.
	 * Sets up the initial length 1-itemset list from which all candidate
	 * closed itemsets are based from. To be in this list a length 1-itemset
	 * must already be considered frequent. So only length 1-itemsets already
	 * determined to be frequent are available to be included.
	 */
	final private void setInitList(FItemsetList flist){
		ArrayList gen;
		ArrayList items = flist.getFrequent1();
		for (int i = 0; i < items.size(); i++){
				candidates.add((String)items.get(i));
				support.add(flist.getEntrySupport((String)items.get(i)));
				transMap.add(flist.getTransMap((String)items.get(i)));
				gen = new ArrayList();
				genList.add(gen);
		}
	}

	/**
	 * candidateGen method.
	 * This method generates the candidate closed itemsets, along with
	 * the transaction mapping and proposed generators for the itemset.
	 * Not all of the itemsets that this method lists as candidates will
	 * become closed itemsets.
	 */
	final private void candidateGen(FItemsetList flist, float minSup){
		int comparision, index = -1, matchCount = 0;
		String xi, xj, newItemset = "";
		String[] xti, xtj;
		float candidateSupport;
		ArrayList tempGen;
		ArrayList newCandidates = new ArrayList();
		ArrayList newSupport = new ArrayList();
		ArrayList newTransMap = new ArrayList();
		ArrayList newGenerators = new ArrayList();

		if (candidates.size() > 1){
			for (int i = 0; i < candidates.size(); i++){
				xi = (String)candidates.get(i);
				for(int j = i + 1; j < candidates.size(); j++){
					xj = (String)candidates.get(j);
					xti = xi.split(",");
					xtj = xj.split(",");
					if (itemsetSize - 2 < 1){
						//This is for when the length 1-itemsets are used.
						newItemset = xi + "," + xj;
						candidateSupport = flist.getEntrySupport(newItemset);
						//Support for the itemset must be at least the minimum...
						if (candidateSupport >= minSup){
							//Call to compare the transaction mapping of the two itemsets.
							comparision = transRecComparer(flist, xi, xj);
							if (comparision == 0){
								//xit and xjt are the same...
								newTransMap.add(flist.getTransMap(xi));
								newGenerators.add(unionGenLists(i, j));
								if (((ArrayList)genList.get(i)).size() == 0){
									newGenerators.set(newGenerators.size() - 1, unionGenList((ArrayList)newGenerators.get(newGenerators.size() - 1), xi));
								}
								if (((ArrayList)genList.get(j)).size() == 0){
									newGenerators.set(newGenerators.size() - 1, unionGenList((ArrayList)newGenerators.get(newGenerators.size() - 1), xj));
								}
							}
							else if (comparision == 1){
								//xit is a subset of xjt...
								newTransMap.add(flist.getTransMap(xi));
								newGenerators.add((ArrayList)genList.get(i));
								if (((ArrayList)genList.get(i)).size() == 0){
									tempGen = (ArrayList)newGenerators.get(newGenerators.size() - 1);
										tempGen.add(xi);
										newGenerators.set(newGenerators.size() - 1, tempGen);
								}
							}
							else if (comparision == 2){
								//xjt is a subset of xit...
								newTransMap.add(flist.getTransMap(xj));
								newGenerators.add((ArrayList)genList.get(j));
								if (((ArrayList)genList.get(j)).size() == 0){
									tempGen = (ArrayList)newGenerators.get(newGenerators.size() - 1);
										tempGen.add(xj);
										newGenerators.set(newGenerators.size() - 1, tempGen);
								}
							}
							else{
								//xt is the intersection of xit and xjt...
								newTransMap.add(combineTransMaps(flist, xi, xj));
								newGenerators.add(genCombinationList(i, j));
							}
							index = newCandidates.indexOf(newItemset);
							if (index != -1){
								//This candidate is a duplicate entry.
								newGenerators.set(index, unionGenLists((ArrayList)newGenerators.get(index), (ArrayList)newGenerators.get(newGenerators.size() - 1)));
								newTransMap.remove(newTransMap.size() - 1);
								newGenerators.remove(newGenerators.size() - 1);
							}
							else{
								newCandidates.add(newItemset);
								newSupport.add(candidateSupport);
							}
						}
					}
					else{
						//This is for length 2-itemsets and longer.
						matchCount = 0;
						newItemset = "";
						//See if xi and xj have the right number of items in common.
						for (int k = 0; k < xti.length; k++){
							for (int l = 0; l < xtj.length; l++){
								if (xti[k].equals(xtj[l])){
									matchCount++;
									l = xtj.length;
								}
							}
						}
						if (matchCount == itemsetSize - 2){
							//Items in common for xi and xj are the right number to build a new itemset.
							//Generate new itemset by merging xi and xj.
							for (int k = 0; k < xti.length; k++){
								if (newItemset.length() == 0){
									newItemset = xti[k];
								}
								if (newItemset.indexOf(xti[k]) == -1){
									newItemset = newItemset + "," + xti[k];
								}
								if (newItemset.indexOf(xtj[k]) == -1){
									newItemset = newItemset + "," + xtj[k];
								}
							}
							candidateSupport = flist.getEntrySupport(newItemset);
							//Support for the itemset must be at least the minimum...
							if (candidateSupport >= minSup){
								//Call to compare the transaction mapping of the two itemsets.
								comparision = transRecComparer(flist, xi, xj);
								if (comparision == 0){
									//xit and xjt are the same...
									newTransMap.add(flist.getTransMap(xi));
									newGenerators.add(unionGenLists(i, j));
									if (((ArrayList)genList.get(i)).size() == 0){
										newGenerators.set(newGenerators.size() - 1, unionGenList((ArrayList)newGenerators.get(newGenerators.size() - 1), xi));
									}
									if (((ArrayList)genList.get(j)).size() == 0){
										newGenerators.set(newGenerators.size() - 1, unionGenList((ArrayList)newGenerators.get(newGenerators.size() - 1), xj));
									}
								}
								else if (comparision == 1){
									//xit is a subset of xjt...
									newTransMap.add(flist.getTransMap(xi));
									newGenerators.add((ArrayList)genList.get(i));
									if (((ArrayList)genList.get(i)).size() == 0){
										tempGen = (ArrayList)newGenerators.get(newGenerators.size() - 1);
										tempGen.add(xi);
										newGenerators.set(newGenerators.size() - 1, tempGen);
									}
								}
								else if (comparision == 2){
									//xjt is a subset of xit...
									newTransMap.add(flist.getTransMap(xj));
									newGenerators.add((ArrayList)genList.get(j));
									if (((ArrayList)genList.get(j)).size() == 0){
										tempGen = (ArrayList)newGenerators.get(newGenerators.size() - 1);
										tempGen.add(xj);
										newGenerators.set(newGenerators.size() - 1, tempGen);
									}
								}
								else{
									//xt is the intersection of xit and xjt...
									newTransMap.add(combineTransMaps(flist, xi, xj));
									newGenerators.add(genCombinationList(i, j));
								}
								index = newCandidates.indexOf(newItemset);
								if (index != -1){
									//This candidate is a duplicate entry.
									newGenerators.set(index, unionGenLists((ArrayList)newGenerators.get(index), (ArrayList)newGenerators.get(newGenerators.size() - 1)));
									newTransMap.remove(newTransMap.size() - 1);
									newGenerators.remove(newGenerators.size() - 1);
								}
								else{
									newCandidates.add(newItemset);
									newSupport.add(candidateSupport);
								}
							}
						}
					}
				}
			}
			//Update the variables that hold the candidates etc.
			prevCandidates = candidates;
			prevSupport = support;
			prevTransMap = transMap;
			prevGenList = genList;
			candidates = newCandidates;
			support = newSupport;
			transMap = newTransMap;
			genList = newGenerators;
		}
		else{
			//Update the variables that hold the candidates etc.
			//Used when the candidate list at the start is empty.
			//Allows the algorithm to finish correctly and cleanly.
			prevCandidates = candidates;
			prevSupport = support;
			prevTransMap = transMap;
			prevGenList = genList;
			candidates = newCandidates;
			support = newSupport;
			transMap = newTransMap;
			genList = newGenerators;
		}
	}

	/**
	 * closureGen method.
	 * Method that determines if the candidate is actually a closed
	 * itemset or not. Looks for any supsets that the itemset (from
	 * the Ck-1 list) has (in the Ck list) and compares their transaction
	 * mappings. The itemset is closed if the transaction mapping of the
	 * itemsets supset is a subset (is smaller).
	 */
	final private void closureGen(){
		ArrayList data, generators;
		String[] cis;
		String itemset, superset;
		boolean noSuperset = true;
		boolean closedItemset = false;

		//See if Ck is empty or not.
		if (candidates.size() != 0 && candidates != null){
			for (int i = 0; i < prevCandidates.size(); i++){
				//For each itemset in Ck-1.
				itemset = (String)prevCandidates.get(i);
				for (int j = 0; j < candidates.size(); j++){
					//For each itemset in Ck.
					superset = (String)candidates.get(j);
					if (isSuperset(itemset, superset)){
						//The itemset from Ck-1 is a subset of this set from Ck...
						noSuperset = false;
						if (transRecComparer((ArrayList)prevTransMap.get(i), (ArrayList)transMap.get(j))){
							//The itemset from Ck-1 is a potential closed itemset...
							closedItemset = true;
						}
						else{
							//The itemset from Ck-1 is not a closed itemset...
							closedItemset = false;
							j = candidates.size();
						}
					}
				}
				if (closedItemset || noSuperset){
					//The itemset from Ck-1 is a closed itemset...
					generators = (ArrayList)prevGenList.get(i);
					if (generators.size() == 0){
						//Itemset has no generator, so it is its own generator.
							data = new ArrayList();
							cis = new String[2];
							cis[0] = (String)prevCandidates.get(i);
							cis[1] = (String)prevCandidates.get(i);

							data.add(cis);
							data.add((Float)prevSupport.get(i));
							data.add((ArrayList)prevTransMap.get(i));
							closedItemsets.add(data);
					}
					else{
						//Itemset has one or more generators listed.
						for (int j = 0; j < generators.size(); j++){
							data = new ArrayList();
							cis = new String[2];
							cis[0] = (String)generators.get(j);
							cis[1] = (String)prevCandidates.get(i);

							data.add(cis);
							data.add((Float)prevSupport.get(i));
							data.add((ArrayList)prevTransMap.get(i));
							closedItemsets.add(data);
						}
					}
				}
				closedItemset = false;
				noSuperset = true;
			}
		}
		else{
			//Ck is empty.
			//All entries in previous iteration are added to the closed list...
			for (int i = 0; i < prevCandidates.size(); i++){
				generators = (ArrayList)prevGenList.get(i);
				for (int j = 0; j < generators.size(); j++){
					data = new ArrayList();
					cis = new String[2];
					cis[0] = (String)generators.get(j);
					cis[1] = (String)prevCandidates.get(i);

					data.add(cis);
					data.add((Float)prevSupport.get(i));
					data.add((ArrayList)prevTransMap.get(i));
					closedItemsets.add(data);
				}
			}
		}
	}

	/**
	 * transRecComparer method.
	 * Method that compares the transaction mapping of two itemsets and
	 * determines if they are equal or if one is a subset of the other or
	 * that they are not equal or a subset.
	 */
	final private int transRecComparer(FItemsetList flist, String xi, String xj){
		int indexPos = -1;
		int comparision = -2;
		ArrayList xit = flist.getTransMap(xi);
		ArrayList xjt = flist.getTransMap(xj);
		//Test to see if the two list are the same or if one is a subset of the other...
		if (xit.size() == xjt.size()){
			//Same number of records, check to see it they are all identical...
			for (int i = 0; i < xit.size(); i++){
				indexPos = xjt.indexOf((Integer)xit.get(i));
				if (indexPos != -1){
					comparision = 0;
				}
				else{
					comparision = -1;
					i = xit.size();
				}
				indexPos = -1;
			}
			return comparision;
		}
		else{
			//Different number of records, therefore can not be equal. See if one is a subset...
			if (xit.size() > xjt.size()){
				//See if xjt is the subset...
				for (int i = 0; i < xjt.size(); i++){
					indexPos = xit.indexOf((Integer)xjt.get(i));
					if (indexPos != -1){
						comparision = 2;
					}
					else{
						comparision = -1;
						i = xjt.size();
					}
					indexPos = -1;
				}
				return comparision;
			}
			else{
				//See if xit is the subset...
				for (int i = 0; i < xit.size(); i++){
					indexPos = xjt.indexOf((Integer)xit.get(i));
					if (indexPos != -1){
						comparision = 1;
					}
					else{
						comparision = -1;
						i = xit.size();
					}
					indexPos = -1;
				}
				return comparision;
			}
		}
	}

	/**
	 * transRecComparer method.
	 * Method used to see if the transaction mapping of itemset x is
	 * a supset of the transaction mapping of itemset y. If this is
	 * the case then itemset x could be a closed itemset.
	 */
	final private boolean transRecComparer(ArrayList x, ArrayList y){
		int indexPos = -1;
		boolean closed = false;

		if (x.size() == y.size()){
			//Same number of records, so x is not larger than this supset...
			closed = false;
			return closed;
		}
		else{
			//Different number of records, therefore can not be equal. See if x is a supset...
			if (x.size() > y.size()){
				//See if x is a closed itemset by having a transaction mapping that is a subset of y.
				for (int i = 0; i < y.size(); i++){
					indexPos = x.indexOf((Integer)y.get(i));
					if (indexPos != -1){
						closed = true;
					}
					else{
						closed = false;
						i = y.size();
					}
					indexPos = -1;
				}
				return closed;
			}
			else{
				//Supset larger than x...
				closed = false;
				return closed;
			}
		}
	}

	/**
	 * combineTransmaps method.
	 * method used to perform the intersection operation (and) on
	 * two itemsets transaction mappings. The new mapping only holds
	 * those entries that were found in both of the original mappings.
	 */
	final private ArrayList combineTransMaps(FItemsetList flist, String xi, String xj){
		ArrayList xit = flist.getTransMap(xi);
		ArrayList xjt = flist.getTransMap(xj);
		ArrayList recList = new ArrayList();
		for (int i = 0; i < xit.size(); i++){
			if (xjt.indexOf((Integer)xit.get(i)) != -1){
				recList.add((Integer)xit.get(i));
			}
		}
		return recList;
	}

	/**
	 * unionGenLists method.
	 * Method used to perform the union operation (or) on two itemsets
	 * generator lists. The itemsets are identified by their
	 * index in the list that holds them.
	 */
	final private ArrayList unionGenLists(int i, int j){
		ArrayList xig = (ArrayList)genList.get(i);
		ArrayList xjg = (ArrayList)genList.get(j);
		ArrayList generators = new ArrayList();
		for (int k = 0; k < xig.size(); k++){
			generators.add((String)xig.get(k));
		}
		for (int k = 0; k < xjg.size(); k++){
			if (generators.indexOf((String)xjg.get(k)) == -1){
				generators.add((String)xjg.get(k));
			}
		}
		return duplicateRemover(generators);
	}

	/**
	 * unionGenLists method.
	 * Method used to perform the union operation (or) on two itemsets
	 * generator lists. The two generator list are provided and no
	 * reference to either of the two itemsets is involved.
	 */
	final private ArrayList unionGenLists(ArrayList x, ArrayList y){
		ArrayList generators = new ArrayList();
		for (int i = 0; i < x.size(); i++){
			generators.add((String)x.get(i));
		}
		for (int i = 0; i < y.size(); i++){
			if (generators.indexOf((String)y.get(i)) == -1){
				generators.add((String)y.get(i));
			}
		}
		return duplicateRemover(generators);
	}

	/**
	 * unionGenList method.
	 * Method used to perform the union operation (or) on a generator
	 * list and an itemset itself. If the itemset id already in the
	 * generator list then it will not be added, if it is not in the
	 * list then it is added and the new generator list returned.
	 */
	final private ArrayList unionGenList(ArrayList gens, String x){
		ArrayList generatorList = new ArrayList();
		for (int i = 0; i < gens.size(); i++){
			generatorList.add((String)gens.get(i));
		}
		if (generatorList.indexOf(x) == -1){
			generatorList.add(x);
		}
		return duplicateRemover(generatorList);
	}

	/**
	 * genCombinationList method.
	 * Method that performs the forming of a single generator list from
	 * two separate lists where each entry in the new list is made up from
	 * an entry in each of the original lists. Thus the entries in each of
	 * the two original lists should be sub-entries for at least one entry
	 * in the new list.
	 */
	final private ArrayList genCombinationList(int xi, int xj){
		String[] xji;
		String gen = "";
		ArrayList xig = (ArrayList)genList.get(xi);
		ArrayList xjg = (ArrayList)genList.get(xj);
		ArrayList generators = new ArrayList();
		for (int i = 0; i < xig.size(); i++){
			gen = (String)xig.get(i);
			for (int j = 0; j < xjg.size(); j++){
				xji = ((String)xjg.get(j)).split(",");
				for (int k = 0; k < xji.length; k++){
					if (gen.indexOf(xji[k]) == -1){
						gen = gen + "," + xji[k];
					}
				}
				generators.add(gen);
				gen = (String)xig.get(i);
			}
		}
		return duplicateRemover(generators);
	}

	/**
	 * duplicateRemover method.
	 * Method that removes duplicate generator entries in a given list. Duplicates
	 * include those generators that have exactly the same items/attributes but in
	 * a different order.
	 */
	final private ArrayList duplicateRemover(ArrayList generatorList){
		ArrayList list = generatorList;;
		boolean match = false;
		String[] gen1, gen2;
		for (int i = 0; i < list.size(); i++){
			gen1 = ((String)list.get(i)).split(",");
			for (int j = (i + 1); j < list.size(); j++){
				gen2 = ((String)list.get(j)).split(",");
				if (gen1.length == gen2.length){
					//Length of generators are equal so check for duplication...
					for (int a = 0; a < gen1.length; a++){
						for (int b = 0; b < gen2.length; b++){
							if (gen1[a].equals(gen2[b])){
								match = true;
								b = gen2.length;
							}
							else{
								match = false;
							}
						}
						if (match == false){
							//This item/attribute from gen1 was not found in gen2...
							a = gen1.length;
						}
					}
					if (match){
						//Duplication, so remove the second entry...
						list.remove(j);
						j--;
					}
				}
				else if (gen1.length > gen2.length){
					//Length of 1 is greater than 2, see if 1 is a supset of 2...
					if (isSuperset((String)list.get(j), (String)list.get(i))){
						list.remove(i);
						i--;
					}
				}
				else if (gen1.length < gen2.length){
					//Length of 2 is greater than 1, see if 2 is a supset of 1...
					if (isSuperset((String)list.get(i), (String)list.get(j))){
						list.remove(j);
						j--;
					}
				}
			}
		}
		return list;
	}

	/**
	 * isSuperset method.
	 * Method used to determine if one itemset is the supset of the other.
	 * In this case itemset 2 is being tested to see if it is the supset of
	 * itemset 1.
	 */
	final private boolean isSuperset(String set, String sSet){
		boolean match = false;
		String[] set1 = set.split(",");
		String[] set2 = sSet.split(",");
		for (int i = 0; i < set1.length; i++){
			for (int j = 0; j < set2.length; j++){
				if (set1[i].equals(set2[j])){
					match = true;
					j = set2.length;
				}
				else{
					match = false;
				}
			}
			if (match == false){
				i = set1.length;
			}
		}
		return match;
	}

	/**
	 * buildList method.
	 * Method that takes the final generated list of closed itemsets, their
	 * corresponding generators and transaction mappings and enters/stores
	 * them in the object that holds the list for use later on in the program.
	 */
	final private void buildList(){
		for (int i = 0; i < closedItemsets.size(); i++){
			fcl.addEntry((String[])((ArrayList)closedItemsets.get(i)).get(0), (Float)((ArrayList)closedItemsets.get(i)).get(1),
								(ArrayList)((ArrayList)closedItemsets.get(i)).get(2));
		}
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
	 * Method used to print the list of candidate itemsets that were considered
	 * for being frequent itemsets and the list of frequent itemsets.
	 */
	final private void printData(){
		if (candidates.size() > 0){
			System.out.println("Candidate List");
			for (int i = 0; i < candidates.size(); i++){
				System.out.print((String)candidates.get(i) + "   ");
				ArrayList rec = (ArrayList)transMap.get(i);
				for (int j = 0; j < rec.size(); j++){
					System.out.print((Integer)rec.get(j) + " ");
				}
				ArrayList gen = (ArrayList)genList.get(i);
				for (int j = 0; j < gen.size(); j++){
					System.out.print("[" + (String)gen.get(j) + "] ");
				}
				System.out.print("  " + transMap.size() + "   " + genList.size());
				System.out.println();
			}
		}
		if (closedItemsets.size() > 0){
			System.out.println("Closed Itemsets");
			for (int i = 0; i < closedItemsets.size(); i++){
				ArrayList data = (ArrayList)closedItemsets.get(i);
				System.out.print(((String[])data.get(0))[0] + "   " + ((String[])data.get(0))[1] + "   " + (Float)data.get(1) + "   ");
				ArrayList tm = (ArrayList)data.get(2);
				for (int j = 0; j < tm.size(); j++){
					System.out.print((Integer)tm.get(j) + " ");
				}
				System.out.println();
			}
		}
	}
}