/**
 * FCItemsetList class.
 * 
 * Start Date: 05 January 2007
 * @author Gavin Shaw
 * @version 1.0
 *
 * Log:
 *		  1.0 Start version of class.
 */
package Data;

import java.util.ArrayList;

import Data.TransRecList;

public class FCItemsetList{

	private ArrayList freqClosed, supports, transMap;

	/**
	 * FCItemsetList method.
	 * Constructor.
	 * Method used to initialise this class and setup the
	 * variables used to hold the list of frequent closed
	 * itemsets, generators and supports.
	 * PRE: Class and variables to be initialised.
	 * POST: Class and variables have been initialised.
	 */
	public FCItemsetList(){
		freqClosed = new ArrayList();
		supports = new ArrayList();
		transMap = new ArrayList();
	}

	/**
	 * addEntry method.
	 * Method used to add a generator, its frequent closed itemset
	 * and the corresponding support value.
	 * PRE: A generator, frequent closed itemset and support are to
	 *      be added to the list.
	 * POST: The entries have been added to the list.
	 */
	final public void addEntry(Object[] entry, float support){
		freqClosed.add(entry);
		supports.add(support);
	}

	/**
	 * addEntry method.
	 * Method used to add a generator, its frequent closed itemset
	 * and the corresponding support value.
	 * PRE: A generator, frequent closed itemset support and
	 *      transaction mapping are to be added to the list.
	 * POST: The entries have been added to the list.
	 */
	final public void addEntry(String[] entry, float support, ArrayList m){
		freqClosed.add(entry);
		supports.add(support);
		transMap.add(m);
	}

	/**
	 * getFreqClosed method.
	 * Method used to get and return the list of generators and
	 * associated frequent closed itemsets.
	 * PRE: The list of generators and frequent closed itemsets
	 *      is required.
	 * POST: The list of generators and frequent closed itemsets
	 *       has been returned.
	 */
	final public ArrayList getFreqClosed(){
		return freqClosed;
	}

	/**
	 * getSupports method.
	 * Method used to get and return the list of generator - frequent
	 * closed itemset support values.
	 * PRE: The list of support values is required.
	 * POST: The list of support values has been returned.
	 */
	final public ArrayList getSupports(){
		return supports;
	}

	/**
	 * getTotalFreqClosed method.
	 * Method used to get and return the total number of frequent
	 * closed itemsets found by the algorithm that determines
	 * frequent closed itemsets.
	 * PRE: The number of frequent closed itemsets is required.
	 * POST: The number of frequent closed itemsets has been returned.
	 */
	final public int getTotalFreqClosed(){
		return freqClosed.size();
	}

	/**
	 * getTransMap method.
	 * Method used to get the transaction mapping for the
	 * specified itemset. The transaction mapping is a list
	 * which contains all of the transaction IDs for the
	 * transactions that this itemset appeared in.
	 * PRE: The frequent closed itemset is specified and is
	 *      present in list.
	 * POST: The transaction mapping has been returned.
	 */
	final public ArrayList getTransMap(String entry){
		ArrayList list = new ArrayList();
		for (int i = 0; i < freqClosed.size(); i++){
			list = (ArrayList)freqClosed.get(i);
			for (int j = 0; j < list.size(); j++){
				if (((String)list.get(j)).equals(entry)){
					return ((TransRecList)((ArrayList)transMap.get(i)).get(j)).getRecordList();
				}
			}
		}
		return null;
	}

	/**
	 * clearData method.
	 * Method used to clear the data, variables and objects held
	 * by this class when they are no longer needed. This frees
	 * memory for other parts of the program.
	 * PRE: The list are no longer needed and are to be cleared.
	 * POST: The list are cleared, releasing memory.
	 */
	final public void clearData(){
		freqClosed.clear();
		supports.clear();
	}
}