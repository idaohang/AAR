/**
 * CandidateList class.
 * 
 * Start Date: 19 December 2006
 * @author Gavin Shaw
 * @version 1.0
 *
 * Log:
 *		  1.0 Start version of class.
 */
package Data;

import java.util.ArrayList;

import Data.TransRecList;

public class CandidateList{

	private ArrayList candidates, counts, weights, supports, transMap;
	
//	private Object[] candidates;
	
//	private int[] counts;
	
//	private float[] weights, supports;

	/**
	 * CandidateList method.
	 * Constructor.
	 * Method used to initialise this class and setup the
	 * variables to hold the list of candidate itemsets, along
	 * with their frequency and support.
	 * PRE: Class and variables to be initialised.
	 * POST: Class and variables have been initialised.
	 */
	public CandidateList(){
		candidates = new ArrayList();
		counts = new ArrayList();
		weights = new ArrayList();
		supports = new ArrayList();
		transMap = new ArrayList();
	}

	/**
	 * addEntry method.
	 * Method used to add a list of candidate itemsets, their
	 * frequency and support values.
	 * PRE: A set of length n candidate itemsets is to be added
	 *      to the list.
	 * POST: The set of candidate itemsets have been added to
	 *       the list.
	 */
	final public void addEntry(ArrayList c, int[] f, float[] s, Object[] m){
		candidates.add(c);
		counts.add(f);
		supports.add(s);
		transMap.add(m);
	}

	final public void addEntry(ArrayList c, int[] f, float[] w, float[] s, Object[] m){
		candidates.add(c);
		counts.add(f);
		weights.add(w);
		supports.add(s);
		transMap.add(m);
	}

	/**
	 * addEntryCollapse method.
	 * Method used to add a list of candidate itemsets, their
	 * frequency and support values.
	 * PRE: A set of length n candidate itemsets is to be added
	 *      to the list.
	 * POST: The set of candidate itemsets have been added to
	 *       the list.
	 */
	final public void addEntryCollapse(int itemsetSize, ArrayList c, int[] f, float[] s, Object[] m){
		if (candidates.size() < itemsetSize){
			candidates.add(c);
			counts.add(f);
			supports.add(s);
//			transMap.add(m);
		}
		else{
			int size1, size2;
			ArrayList temp1;
			int[] temp2a, temp2b;
			float[] temp3a, temp3b;
			
			temp1 = (ArrayList)candidates.get(itemsetSize - 1);
			size1 = c.size();
			for (int i = 0; i < size1; i++){
				temp1.add((int[])c.get(i));
			}
			candidates.set(itemsetSize - 1, temp1);
			
			temp2a = (int[])counts.get(itemsetSize - 1);
			size1 = f.length;
			size2 = temp2a.length;
			temp2b = new int[size1 + size2];
			for (int i = 0; i < size2; i++){
				temp2b[i] = temp2a[i];
			}
			for (int i = 0; i < size1; i++){
				temp2b[size2 + i] = f[i];
			}
			counts.set(itemsetSize - 1, temp2b);
			
			temp3a = (float[])supports.get(itemsetSize - 1);
			size1 = s.length;
			size2 = temp3a.length;
			temp3b = new float[size1 + size2];
			for (int i = 0; i < size2; i++){
				temp3b[i] = temp3a[i];
			}
			for (int i = 0; i < size1; i++){
				temp3b[size2 + i] = s[i];
			}
			supports.set(itemsetSize - 1, temp3b);
/*			temp = (ArrayList)transMap.get(itemsetSize - 1);
			for (int i = 0; i < m.size(); i++){
				temp.add((String)m.get(i));
			}*/
		}
	}

	/**
	 * getCandidates method.
	 * Method to get and return the list of candidate
	 * itemsets.
	 * PRE: The list of candidate itemsets is required.
	 * POST: The list of candidate itemset has been returned.
	 */
	final public ArrayList getCandidates(){
		return candidates;
	}

	/**
	 * getCounts method.
	 * Method used to get and return the list of candidate
	 * itemset frequency counts.
	 * PRE: The list of candidate itemset frequency counts
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
	 * getSupprts method.
	 * Method used to get and return the list of candidate
	 * itemset support values.
	 * PRE: The list of candidate itemset support values is
	 *      required.
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
	 * PRE: The list of candidate itemset transaction mappings
	 *      is required.
	 * POST: The list of transaction mappings has been returned.
	 */
	final public ArrayList getTransMap(){
		return transMap;
	}

	/**
	 * getTotalCandidates method.
	 * Method used to get and return the total number of candidate
	 * itemsets looked at by the algorithm that determined frequent
	 * itemsets.
	 * PRE: The number of candidate itemsets is required.
	 * POST: The number of candidate itemsets has been returned.
	 */
	final public int getTotalCandidates(){
		int totalCount = 0;
		for (int i = 0; i < candidates.size(); i++){
			totalCount = totalCount + ((ArrayList)candidates.get(i)).size();
		}
		return totalCount;
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
		candidates.clear();
		counts.clear();
		supports.clear();
		transMap.clear();
	}

	/**
	 * printData method.
	 * Method used to print the list of candidate itemsets along
	 * with their frequency and support value.
	 * PRE: The list of candidate itemsets is to be printed.
	 * POST: The list of candidate itemsets, their frequency and
	 *       support have been printed.
	 */
	final public void printData(){
		for (int i = 0; i < candidates.size(); i++){
			System.out.println("Length " + (i + 1) + " Candidates");
			ArrayList entry = (ArrayList)candidates.get(i);
			int[] count = (int[])counts.get(i);
			float[] support = (float[])supports.get(i);
			Object[] tlist = (Object[])transMap.get(i);
			for (int j = 0; j < entry.size(); j++){
				System.out.println((int[])entry.get(j) + "   " + count[j] + "   " + support[j]);
				ArrayList rec = ((TransRecList)tlist[j]).getRecordList();
				for (int k = 0; k < rec.size(); k++){
					System.out.print((Integer)rec.get(k) + "   ");
				}
				System.out.print("\n");
			}
		}
	}
}