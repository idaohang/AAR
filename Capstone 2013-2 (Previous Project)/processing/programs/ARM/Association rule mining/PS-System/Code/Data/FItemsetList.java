/**
 * FItemsetList class.
 * 
 * Start Date: 20 December 2006
 * @author Gavin Shaw
 * @version 1.0
 *
 * Log:
 *		  1.0 Start version of class.
 */
package Data;

import java.util.ArrayList;

import Data.TransRecList;

public class FItemsetList{

	private ArrayList items, counts, weights, supports, transMap;

	/**
	 * FItemsetList method.
	 * Constructor.
	 * method used to initialise this class and setup the
	 * variables that will hold the freqeunt itemsets, their
	 * frequencies and supports.
	 * PRE: Class and variables to be initialised.
	 * POST: Class and variables have been initialised.
	 */
	public FItemsetList(){
		items = new ArrayList();
		counts = new ArrayList();
		weights = new ArrayList();
		supports = new ArrayList();
		transMap = new ArrayList();
	}

	/**
	 * addEntry method.
	 * Method used to add a list of frequent itemsets, their
	 * frequency and support values.
	 * PRE: A set of length n frequent itemsets is to be added
	 *      to the list.
	 * POST: The set of frequent itemsets have been added to
	 *       the list.
	 */
	final public void addEntry(ArrayList f, ArrayList c, ArrayList s, ArrayList m){
		items.add(f);
		counts.add(c);
		supports.add(s);
		transMap.add(m);
	}

	final public void addEntry(ArrayList f, ArrayList c, ArrayList w, ArrayList s, ArrayList m){
		items.add(f);
		counts.add(c);
		weights.add(w);
		supports.add(s);
		transMap.add(m);
	}

	/**
	 * addEntryCollapse method.
	 * Method used to add a list of frequent itemsets, their
	 * frequency and support values.
	 * PRE: A set of length n frequent itemsets is to be added
	 *      to the list.
	 * POST: The set of frequent itemsets have been added to
	 *       the list.
	 */
	final public void addEntryCollapse(int itemsetSize, ArrayList f, ArrayList c, ArrayList s, ArrayList m){
		if (items.size() < itemsetSize){
			items.add(f);
			counts.add(c);
			supports.add(s);
//			transMap.add(m);
		}
		else{
			ArrayList temp;
			temp = (ArrayList)items.get(itemsetSize - 1);
			for (int i = 0; i < f.size(); i++){
				temp.add((int[])f.get(i));
			}
			temp = (ArrayList)counts.get(itemsetSize - 1);
			for (int i = 0; i < c.size(); i++){
				temp.add((Integer)c.get(i));
			}
			temp = (ArrayList)supports.get(itemsetSize - 1);
			for (int i = 0; i < s.size(); i++){
				temp.add((Float)s.get(i));
			}
/*			temp = (ArrayList)transMap.get(itemsetSize - 1);
			for (int i = 0; i < m.size(); i++){
				temp.add((String)m.get(i));
			}*/
		}
	}

	/**
	 * addEntry method.
	 * Method used to add a list of frequent itemsets, their
	 * frequency and support values.
	 * PRE: A set of length n frequent itemsets is to be added
	 *      to the list.
	 * POST: The set of frequent itemsets have been added to
	 *       the list.
	 */
	final public void addEntry(ArrayList f, ArrayList c, ArrayList s){
		items.add(f);
		counts.add(c);
		supports.add(s);
	}

	/**
	 * getFrequent method.
	 * Method used to get and return the list of frequent
	 * itemsets.
	 * PRE: The list of frequent itemsets is required.
	 * POST: The list of frequent itemset has been returned.
	 */
	final public ArrayList getFrequent(){
		return items;
	}
	
	/**
	 * getFrequent1 method.
	 * Method used to get and return the list of length 1
	 * frequent itemsets.
	 * PRE: The list of length 1 frequent itemsets is required.
	 * POST: The list of length 1 frequent itemset has been returned.
	 */
	final public ArrayList getFrequent1(){
		return (ArrayList)items.get(0);
	}
	
	final public Object[] getFrequent(int level){
		ArrayList t = (ArrayList)items.get(level - 1);
		int s = t.size();
		Object[] f = new Object[s];
		for (int i = 0; i < s; i++){
			f[i] = (int[])t.get(i);
		}
		return f;
	}

	/**
	 * getCounts method.
	 * Method used to get and return the list of frequent
	 * itemset frequency counts.
	 * PRE: The list of frequent itemset frequency counts
	 *      is required.
	 * POST: The list of frequency counts has been returned.
	 */
	final public ArrayList getCounts(){
		return counts;
	}

	final public ArrayList getWeights(){
		return weights;
	}

	/**
	 * getSupports method.
	 * Method used to get and return the list of frequent
	 * itemset support values.
	 * PRE: The list of frequent itemset support values is required.
	 * POST: The list of support values has been returned.
	 */
	final public ArrayList getSupports(){
		return supports;
	}

	/**
	 * getTransMap method.
	 * Method used to get and return the list of transaction
	 * mappings to the candidate itemsets (which transactions
	 * does this itemset appear in, based on transaction ID).
	 * PRE: The list of frequent itemset transaction mappings is
	 *      required.
	 * POST: The list of transaction mappings has been returned.
	 */
	final public ArrayList getTransMap(){
		return transMap;
	}

	/**
	 * getTotalFrequent method.
	 * Method used to get and return the total number of
	 * frequent itemsets found by the algorithm that determines
	 * frequent itemsets.
	 * PRE: The number of frequent itemsets is required.
	 * POST: The number of frequent itemsets has been returned.
	 */
	final public int getTotalFrequent(){
		int totalCount = 0;
		for (int i = 0; i < items.size(); i++){
			totalCount = totalCount + ((ArrayList)items.get(i)).size();
		}
		return totalCount;
	}

	/**
	 * getEntrySupport method.
	 * Method used to get and return the support value for
	 * a specified frequent itemset. If the specified itemset
	 * is not a frequent itemset then -1 is returned as the
	 * support.
	 * PRE: The itemset who's support is required must be specified.
	 * POST: The support for the itemset has been returned
	 *       (if itemset present in list).
	 */
	final public float getEntrySupport(String entry){
		ArrayList list = new ArrayList();
		for (int i = 0; i < items.size(); i++){
			list = (ArrayList)items.get(i);
			for (int j = 0; j < list.size(); j++){
				if (((String)list.get(j)).equals(entry)){
					return (Float)((ArrayList)supports.get(i)).get(j);
				}
			}
		}
		return -1;
	}

	/**
	 * getTransMap method.
	 * Method used to get the transaction mapping for the
	 * specified itemset. The transaction mapping is a list
	 * which contains all of the transaction IDs for the
	 * transactions that this itemset appeared in.
	 * PRE: The itemset who's transaction mapping is required
	 *      must be specified.
	 * POST: The transaction mapping has been returned
	 *       (if itemset present in list).
	 */
	final public ArrayList getTransMap(String entry){
		ArrayList list = new ArrayList();
		for (int i = 0; i < items.size(); i++){
			list = (ArrayList)items.get(i);
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
		items.clear();
		counts.clear();
		supports.clear();
		transMap.clear();
	}

	final private boolean compare(String itemset1, String itemset2){
		String[] itemlist1, itemlist2;
		boolean match = false;
		int i1, i2;

		itemlist1 = itemset1.split(",");
		itemlist2 = itemset2.split(",");
		i1 = itemlist1.length;
		i2 = itemlist2.length;
		if (i1 == i2){
			for (int i = 0; i < i1; i++){
				match = false;
				for (int j = 0; j < i2; j++){
					if (itemlist1[i].equals(itemlist2[j])){
						match = true;
						break;
					}
				}
				if (!match){
					return false;
				}
			}
			if (match){
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}

	/**
	 * printData method.
	 * Method used to print the list of frequent itemsets along
	 * with their frequency and support value.
	 * PRE: The list of frequent itemsets is to be printed.
	 * POST: The list of frequent itemsets, their frequency and
	 *       support have been printed.
	 */
	final public void printData(){
		for (int i = 0; i < items.size(); i++){
			System.out.println("Length " + (i + 1) + " Itemsets");
			ArrayList entry = (ArrayList)items.get(i);
			ArrayList count = (ArrayList)counts.get(i);
			ArrayList support = (ArrayList)supports.get(i);
			ArrayList tlist = (ArrayList)transMap.get(i);
			for (int j = 0; j < entry.size(); j++){
				System.out.println((String)entry.get(j) + "   " + (Integer)count.get(j) + "   " + (Float)support.get(j));
				ArrayList rec = ((TransRecList)tlist.get(j)).getRecordList();
				for (int k = 0; k < rec.size(); k++){
					System.out.print((Integer)rec.get(k) + "   ");
				}
				System.out.print("\n");
			}
		}
	}
}