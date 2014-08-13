/**
 * TransRecords class.
 * 
 * Start Date: 08 December 2006
 * @author Gavin Shaw
 * @version 1.0
 *
 * Log:
 *		  1.0 Start version of class.
 */
package Data;

import java.util.ArrayList;

public class TransRecords{
	
	private Object[] records, abstractdescLists;
	
	private String[] attNames, attValues1, attValues2, abstractattNames, allNames;
	
	private int[] attIDs, abstractattIDs;
	
	private int MTH;
	
	private int LTP;

	/**
	 * TransRecords method.
	 * Constructor.
	 * Method used to initialise this class and setup the variables
	 * that will hold the transaction records, their items/attributes
	 * and values from the loaded dataset.
	 * PRE: Class and variables to be initialised.
	 * POST: Class and variables have been initialised.
	 */
	public TransRecords(){
		abstractattNames = new String[0];
		abstractattIDs = new int[0];
		abstractdescLists = new Object[0];
		MTH = 1;
		LTP = 2;
	}

	/**
	 * addData method.
	 * Method used to add ans store a list of transactions and their
	 * contents along with the list of item/attribute names and the
	 * allowed values that they may have.
	 * PRE: A set of transactions, item names and valid values are
	 *      to be added to the lists.
	 * POST: The new entries have been added to the lists.
	 */
	final public void addData(ArrayList r, ArrayList names, ArrayList val1, ArrayList val2, ArrayList aID, ArrayList all){
		String[] temp;
		String t;
		int s, a;
		s = r.size();
		records = new Object[s];
		for (int i = 0; i < s; i++){
			temp = ((String[])r.get(i));
			records[i] = temp;
		}

		s = names.size();
		attNames = new String[s];
		for (int i = 0; i < s; i++){
			t = (String)names.get(i);
			attNames[i] = t;
		}

		s = val1.size();
		attValues1 = new String[s];
		for (int i = 0; i < s; i++){
			t = (String)val1.get(i);
			attValues1[i] = t;
		}

		s = val2.size();
		attValues2 = new String[s];
		for (int i = 0; i < s; i++){
			t = (String)val2.get(i);
			attValues2[i] = t;
		}
		
		s = aID.size();
		attIDs = new int[s];
		for (int i = 0; i < s; i++){
			a = (Integer)aID.get(i);
			attIDs[i] = a;
		}
		
		s = all.size();
		allNames = new String[s];
		for (int i = 0; i < s; i++){
			t = (String)all.get(i);
			allNames[i] = t;
		}
	}
	
	final public void addData(ArrayList r, ArrayList names, ArrayList val1, ArrayList val2, ArrayList aID){
		String[] temp;
		String t;
		int s, a;
		s = r.size();
		records = new Object[s];
		for (int i = 0; i < s; i++){
			temp = ((String[])r.get(i));
			records[i] = temp;
		}

		s = names.size();
		attNames = new String[s];
		for (int i = 0; i < s; i++){
			t = (String)names.get(i);
			attNames[i] = t;
		}

		s = val1.size();
		attValues1 = new String[s];
		for (int i = 0; i < s; i++){
			t = (String)val1.get(i);
			attValues1[i] = t;
		}

		s = val2.size();
		attValues2 = new String[s];
		for (int i = 0; i < s; i++){
			t = (String)val2.get(i);
			attValues2[i] = t;
		}
		
		s = aID.size();
		attIDs = new int[s];
		for (int i = 0; i < s; i++){
			a = (Integer)aID.get(i);
			attIDs[i] = a;
		}
	}

	final public void addData(ArrayList r, ArrayList names, ArrayList val1, ArrayList val2){
		String[] temp;
		String t;
		int s, a;
		s = r.size();
		records = new Object[s];
		for (int i = 0; i < s; i++){
			temp = ((String[])r.get(i));
			records[i] = temp;
		}

		s = names.size();
		attNames = new String[s];
		for (int i = 0; i < s; i++){
			t = (String)names.get(i);
			attNames[i] = t;
		}

		s = val1.size();
		attValues1 = new String[s];
		for (int i = 0; i < s; i++){
			t = (String)val1.get(i);
			attValues1[i] = t;
		}

		s = val2.size();
		attValues2 = new String[s];
		for (int i = 0; i < s; i++){
			t = (String)val2.get(i);
			attValues2[i] = t;
		}
		
		s = names.size();
		attIDs = new int[s];
		for (int i = 0; i < s; i++){
			attIDs[i] = i + 1;
		}
	}
	
	final public void addData(Object[] r, String[] names, String[] val1, String[] val2, int[] id){
		String[] temp;
		String t;
		int a, s;
		s = r.length;
		records = new Object[s];
		for (int i = 0; i < s; i++){
			temp = (String[])r[i];
			records[i] = temp;
		}

		s = names.length;
		attNames = new String[s];
		for (int i = 0; i < s; i++){
			t = names[i];
			attNames[i] = t;
		}

		s = val1.length;
		attValues1 = new String[s];
		for (int i = 0; i < s; i++){
			t = val1[i];
			attValues1[i] = t;
		}

		s = val2.length;
		attValues2 = new String[s];
		for (int i = 0; i < s; i++){
			t = val2[i];
			attValues2[i] = t;
		}
		
		s = id.length;
		attIDs = new int[s];
		for (int i = 0; i < s; i++){
			a = id[i];
			attIDs[i] = a;
		}
	}

	final public void addExtra(ArrayList t, ArrayList i, ArrayList d){
		int i1, i2, i3, i4;
		int s1, s2, s3, s4, s5;
		i1 = attNames.length;
		i2 = attIDs.length;
		i3 = t.size();
		i4 = i.size();
		s1 = abstractattNames.length;
		s2 = abstractattIDs.length;
		s3 = abstractdescLists.length;
		s4 = d.size();
		String[] r1 = new String[i1 + i3];
		int[] a1 = new int[i2 + i4];
		String[] r2 = new String[s1 + i3];
		int[] a2 = new int[s2 + i4];
		int [] d1;
		Object[] d2 = new Object[s3 + s4];
		ArrayList temp;
		for (int j = 0; j < i1; j++){
			r1[j] = attNames[j];
			a1[j] = attIDs[j];
		}
		for (int j = 0; j < s1; j++){
			r2[j] = abstractattNames[j];
			a2[j] = abstractattIDs[j];
			for (int k = 0; k < i3; k++){
				if (((String)t.get(k)).startsWith(abstractattNames[j] + "-")){
					s5 = ((int[])abstractdescLists[j]).length;
					d1 = new int[s5 + 1];
					for (int l = 0; l < s5; l++){
						d1[l] = ((int[])abstractdescLists[j])[l];
					}
					d1[s5] = (Integer)i.get(k);
					abstractdescLists[j] = d1;
				}
			}
			d2[j] = (int[])abstractdescLists[j];
		}
		for (int j = 0; j < i3; j++){
			r1[i1 + j] = (String)t.get(j);
			a1[i2 + j] = (Integer)i.get(j);
		}
		for (int j = 0; j < s4; j++){
			r2[s1 + j] = (String)t.get(j);
			a2[s2 + j] = (Integer)i.get(j);
			temp = (ArrayList)d.get(j);
			s5 = temp.size();
			d1 = new int[s5];
			for (int k = 0; k < s5; k++){
				d1[k] = (Integer)temp.get(k);
			}
			d2[s3 + j] = d1;
		}
		attNames = r1;
		attIDs = a1;
		abstractattNames = r2;
		abstractattIDs = a2;
		abstractdescLists = d2;
	}
	
	/**
	 * addRec method.
	 * Method used to add transaction records to the stored list.
	 * PRE: A set of transactions are to be added to the list.
	 * POST: The transactions have been added to the list.
	 */
	final public void addRec(ArrayList r){
		String[] temp;
		int s;
		s = r.size();
		records = new Object[s];
		for (int i = 0; i < s; i++){
			temp = ((String[])r.get(i));
			records[i] = temp;
		}
	}

	/**
	 * addNames method.
	 * Method used to add item/attribute names to the stored list.
	 * PRE: A set of item names are to be added to the list.
	 * POST: The item names have been added to the list.
	 */
	final public void addNames(ArrayList names){
		String temp;
		int s;
		s = names.size();
		attNames = new String[s];
		for (int i = 0; i < s; i++){
			temp = (String)names.get(i);
			attNames[i] = temp;
		}
	}
	
	final public void addIDs(ArrayList ids){
		int a, s;
		s = ids.size();
		attIDs = new int[s];
		for (int i = 0; i < s; i++){
			a = (Integer)ids.get(i);
			attIDs[i] = a;
		}
	}

	/**
	 * getRec method.
	 * Method used to get and return the list of transactions
	 * and their items/attributes and values that are in the dataset.
	 * PRE: The list of transaction records is required.
	 * POST: The list of transactions records has been returned.
	 */
	final public Object[] getRec(){
		return records;
	}

	/**
	 * getNumberRecs method.
	 * Method used to get and return the number of transactions
	 * in the dataset.
	 * PRE: The number of transaction records is required.
	 * POST: The number of transactions records has been returned.
	 */
	final public int getNumberRecs(){
		return records.length;
	}
	
	final public int getRecordLength(){
		return ((String[])records[0]).length;
	}

	/**
	 * getNames method.
	 * Method used to get and return the item/attribute names
	 * that are in the transactions in the dataset.
	 * PRE: The list of item names is required.
	 * POST: The list of item names has been returned.
	 */
	final public String[] getNames(){
		return attNames;
	}
	
	final public String getNames(int[] ids){
		String names = "";
		int l = ids.length;
		for (int i = 0; i < l; i++){
			if (i == 0){
				names = attNames[ids[i] - 1];
			}
			else{
				names = names + "," + attNames[ids[i] - 1];
			}
		}
		return names;
	}
	
	final public String getName(int id){
		return attNames[id - 1];
	}
	
	final public int getID(int index){
		return attIDs[index];
	}
	
	final public int[] getIDs(){
		return attIDs;
	}
	
	final public int getMaxID(){
		return attIDs[attIDs.length - 1];
	}

	/**
	 * getNumberAtts method.
	 * Method to get and return the number of items/attributes
	 * that are in the dataset.
	 * PRE: The number of items is required.
	 * POST: The number of items has been returned.
	 */
	final public int getNumberAtts(){
		return attNames.length;
	}

	/**
	 * getVal1 method.
	 * Method used to get and return the list of valid values
	 * that the items/attributes contained within the dataset
	 * can have.
	 * PRE: The list of valid item values is required.
	 * POST: The list of valid item values has been returned.
	 */
	final public String[] getVal1(){
		return attValues1;
	}

	/**
	 * getVal2 method.
	 * Method used to get and return the list of alt valid values
	 * that the items/attributes contained within the dataset
	 * can have.
	 * PRE: The list of alt valid item values is required.
	 * POST: The list of alt valid item values has been returned.
	 */
	final public String[] getVal2(){
		return attValues2;
	}

	/**
	 * getSingleAttValues method.
	 * Method used to get and return a single item's/attribute's
	 * value from all the transactions in the dataset.
	 * PRE: The item/attribute who's values across transactions
	 *      is desired must be specified.
	 * POST: The list of values that the item/attribute has across
	 *       all transactions has been returned.
	 */
	final public String[] getSingleAttValues(int index){
		int r1 = records.length;
		String[] values = new String[r1];
		for (int i = 0; i < r1; i++){
			values[i] = ((String[])records[i])[index];
		}
		return values;
	}
	
	final public int[] getAncestors(int id){
		ArrayList matches = new ArrayList();
		String name = "";
		int[] match;
		int a1 = attIDs.length;
		for (int i = 0; i < a1; i++){
			if (attIDs[i] == id){
				name = attNames[i];
				break;
			}
		}
		for (int i = 0; i < a1; i++){
			if (name.startsWith(attNames[i] + "-")){
				//Ancestor found...
				matches.add(attIDs[i]);
			}
		}
		a1 = matches.size();
		match = new int[a1];
		for (int i = 0; i < a1; i++){
			match[i] = (Integer)matches.get(i);
		}
		return match;
	}
	
	final public ArrayList getMatches(int id){
		ArrayList matches = new ArrayList();
		int[] desc;
		int a1, a2, a3;
		a1 = attIDs.length;
		for (int i = 0; i < a1; i++){
			if (attIDs[i] == id){
				matches.add(i);
				break;
			}
		}
		a3 = abstractattIDs.length;
		for (int i = 0; i < a3; i++){
			if (abstractattIDs[i] == id){
				desc = (int[])abstractdescLists[i];
				a2 = desc.length;
				for (int j = 0; j < a2; j++){
					for (int k = 0; k < a1; k++){
						if (attIDs[k] == desc[j]){
							matches.add(k);
							break;
						}
					}
				}
				break;
			}
		}
		return matches;
	}
	
	final public int[] getMatches(int[] ids){
		ArrayList matches = new ArrayList();
		int[] desc, items;
		int i1, a1, a2, a3;
		i1 = ids.length;
		a1 = attIDs.length;
		for (int x = 0; x < i1; x++){
			for (int i = 0; i < a1; i++){
				if (attIDs[i] == ids[x]){
					matches.add(attIDs[i]);
					break;
				}
			}
			a3 = abstractattIDs.length;
			for (int i = 0; i < a3; i++){
				if (abstractattIDs[i] == ids[x]){
					desc = (int[])abstractdescLists[i];
					a2 = desc.length;
					for (int j = 0; j < a2; j++){
						for (int k = 0; k < a1; k++){
							if (attIDs[k] == desc[j]){
								matches.add(attIDs[k]);
								break;
							}
						}
					}
					break;
				}
			}
		}
		a1 = matches.size();
		items = new int[a1];
		for (int i = 0; i < a1; i++){
			items[i] = (Integer)matches.get(i);
		}
		return items;
	}
	
	final public Object[] getAbstractTable(){
		Object[] abstractTable = new Object[3];
		abstractTable[0] = abstractattNames;
		abstractTable[1] = abstractattIDs;
		abstractTable[2] = abstractdescLists;
		return abstractTable;
	}
	
	final public String[] getAllNodes(){
		return allNames;
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
		records = null;
		attNames = null;
		attValues1 = null;
		attValues2 = null;
		attIDs = null;
		allNames = null;
	}

	/**
	 * clearRecords method.
	 * Method used to clear all the transactions already stored
	 * for this dataset.
	 * PRE: The list of transaction records is to be cleared.
	 * POST: The list of transaction records has been cleared.
	 */
	final public void clearRecords(){
		records = null;
	}

	/**
	 * clearAttNames method.
	 * Method used to clear all the item/attribute names already
	 * entered/stored for this dataset.
	 * PRE: The list of item/attribute names is to be cleared.
	 * POST: The list of item/attribute names has been cleared.
	 */
	final public void clearAttNames(){
		attNames = null;
	}

	/**
	 * clearAttValues1 method.
	 * Method used to clear athe list of valid values that each
	 * item/attribute in the dataset has.
	 * PRE: The list of valid item values is to be cleared.
	 * POST: The list of valid item values has been cleared.
	 */
	final public void clearAttValues1(){
		attValues1 = null;
	}

	/**
	 * clearAttValues1 method.
	 * Method used to clear athe list of alt valid values that each
	 * item/attribute in the dataset has.
	 * PRE: The list of alt valid item values is to be cleared.
	 * POST: The list of alt valid item values has been cleared.
	 */
	final public void clearAttValues2(){
		attValues2 = null;
	}
	
	final public void setMTH(int height){
		MTH = height;
	}
	
	final public int getMTH(){
		return MTH;
	}
	
	final public void setLTP(int length){
		LTP = length;
	}
	
	final public int getLTP(){
		return LTP;
	}

	/**
	 * print method.
	 * Method used to print the dataset's contents; thus prints out
	 * each transaction in the dataset, with the values for each
	 * item/attribute.
	 * PRE: The list of transaction records is to be printed out.
	 * POST: The list of transaction records has been printed.
	 */
	final public void print(){
		String[] record;
		System.out.println("Record Listing");
		for (int i = 0; i < records.length; i++){
			record = (String[])records[i];
			for (int j = 0; j < record.length; j++){
				System.out.print(record[j] + " ");
			}
			System.out.println();
		}

		for (int i = 0; i < attNames.length; i++){
			System.out.println((String)attNames[i] + "  " + (String)attValues1[i] + "  " + (String)attValues2[i]);
		}
	}
}