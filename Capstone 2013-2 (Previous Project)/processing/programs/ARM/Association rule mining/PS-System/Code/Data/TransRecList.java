/**
 * TransRecList class.
 * 
 * Start Date: 15 January 2007
 * @author Gavin Shaw
 * @version 1.0
 *
 * Log:
 *		  1.0 Start version of class.
 */
package Data;

import java.util.ArrayList;

public class TransRecList{

	private ArrayList recordList;

	/**
	 * TransRecList method.
	 * Constructor.
	 * Method used to initialise this class and setup the
	 * variable to hold the list of transaction ID's for
	 * associated with the itemset this instance is linked
	 * to.
	 * PRE: Class and variables to be initialised.
	 * POST: Class and variables have been initialised.
	 */
	public TransRecList(){
		recordList = new ArrayList();
	}

	/**
	 * addEntry method.
	 * Method used to add a transaction ID to the list of ID's
	 * held by this class.
	 * PRE: The transaction ID to be added to the mapping list
	 *      must be specified.
	 * POST: Transaction ID is added to the mapping list.
	 */
	final public void addEntry(int record){
		recordList.add(record);
	}

	/**
	 * getRecordList method.
	 * Method used to get and return the list of transaction ID's
	 * held by this instance.
	 * PRE: The list of transaction mappings is required.
	 * POST: The list of transaction mappings has been returned.
	 */
	final public ArrayList getRecordList(){
		return recordList;
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
		recordList.clear();
	}
}