/**
 * DataFileReader class.
 * 
 * Start Date: 03 December 2006
 * @author Gavin Shaw
 * @version 1.0
 *
 * Log:
 *		  1.0 Start version of class.
 */
package FileIO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;

public class DataFileReader{

	private BufferedReader breader;

	private FileReader freader;

	private File file;

	private ArrayList entryList, attNameList, attValueList, attAltValueList, attIDList;

	private ArrayList fiList, fciList, bfciList;

	private String fdata;

	/**
	 * DataFileReader method.
	 * Constructor.
	 * Method used to initialise this class.
	 */
	public DataFileReader(){
	}

	/**
	 * setupReader method.
	 * Method used to setup the necessary readers to allow this class
	 * to read from an external files, which is specified when this
	 * method is used.
	 */
	final public boolean setupReader(String filename){
		try{
			file = new File(filename);
			freader = new FileReader(file);
			breader = new BufferedReader(freader);
			return true;
		}
		catch (IOException ioe){
			return false;
		}
	}

	/**
	 * readFIFile method.
	 * Method used to attempt to read the contents of an external
	 * frequent itemset data file for which the readers to access
	 * it have been set up.
	 */
	final public boolean readFIFile(){
		fiList = new ArrayList();
		try{
			fdata = breader.readLine();
			while (fdata != null){
				fiList.add(fdata);
				fdata = breader.readLine();
			}
			return true;
		}
		catch (IOException ioe){
			return false;
		}
	}

	/**
	 * readFCIFile method.
	 * Method used to attempt to read the contents of an external
	 * frequent closed itemset data file for which the readers to
	 * access it have been set up.
	 */
	final public boolean readFCIFile(){
		fciList = new ArrayList();
		try{
			fdata = breader.readLine();
			while (fdata != null){
				fciList.add(fdata);
				fdata = breader.readLine();
			}
			return true;
		}
		catch (IOException ioe){
			return false;
		}
	}

	/**
	 * readBatchFCIFile method.
	 * Method used to attempt to read the contents of an external
	 * frequent closed itemset data file which contains frequent
	 * closed itemsets, generators and supports from multiple
	 * datasets for which the readers to access it have been set up.
	 */
	final public boolean readBatchFCIFile(){
		bfciList = new ArrayList();
		try{
			fdata = breader.readLine();
			while (fdata != null){
				bfciList.add(fdata);
				fdata = breader.readLine();
			}
			bfciList.add("<*EOF*>");
			return true;
		}
		catch (IOException ioe){
			return false;
		}
	}

	/**
	 * readDataFile method.
	 * Method used to attempt to read the contents of an external
	 * data file (represented by the .data extension) for which the
	 * readers to access it have been set up.
	 */
	final public boolean readDataFile(){
		entryList = new ArrayList();
		try{
			fdata = breader.readLine();
			while (fdata != null){
				entryList.add(fdata);
				fdata = breader.readLine();
			}
			return true;
		}
		catch (IOException ioe){
			return false;
		}

	}

	/**
	 * readInfoFile method.
	 * Method used to attempt to read and process the contents of
	 * an external names/info file (represented by the .names
	 * extension) for which the readers to access it have been set up.
	 */
	final public boolean readInfoFile(){
		attNameList = new ArrayList();
		attValueList = new ArrayList();
		attAltValueList = new ArrayList();
		attIDList = new ArrayList();
		String classInfo = null, classAltInfo = null;
		int attID = 1;
		try{
			fdata = breader.readLine();
			while (fdata != null){
				if ((fdata.startsWith("|") != true)){
					if (fdata.indexOf(':') != -1){
						//Attribute names and values allowed...
						attNameList.add(fdata.substring(0, fdata.indexOf(':')));
						attIDList.add(attID);
						attID++;
						if (fdata.indexOf('|') != -1){
							//Alt attribute value information...
							attValueList.add(fdata.substring(fdata.indexOf(':') + 1, fdata.indexOf('|')).trim().replaceAll(" ", ""));
							attAltValueList.add(fdata.substring(fdata.indexOf('|') + 1).trim().replaceAll(" ", ""));
						}
						else{
							if (fdata.charAt(fdata.length() - 1) == '.'){
								attValueList.add(fdata.substring(fdata.indexOf(':') + 1, fdata.length() - 1).trim().replaceAll(" ", ""));
								attAltValueList.add("---");
							}
							else{
								attValueList.add(fdata.substring(fdata.indexOf(':') + 1).trim().replaceAll(" ", ""));
								attAltValueList.add("---");
							}
						}
					}
					else{
						//Not attribute info, could be class info...
						if ((fdata.length() > 2) && (fdata.indexOf(',') != -1)){
							//Class information...
							if (fdata.indexOf('|') != -1){
								classInfo = fdata.substring(0, fdata.indexOf('|')).trim().replaceAll(" ", "");
								classAltInfo = fdata.substring(fdata.indexOf('|') + 1).trim().replaceAll(" ", "");
								if (classInfo.charAt(classInfo.length() - 1) == '.'){
									classInfo = classInfo.substring(0, classInfo.length() - 1);
								}
								if (classAltInfo.charAt(classAltInfo.length() - 1) == '.'){
									classAltInfo = classAltInfo.substring(0, classAltInfo.length() - 1);
								}
							}
							else{
								if (fdata.charAt(fdata.length() - 1) == '.'){
									classInfo = fdata.substring(0, fdata.length() - 1).trim().replaceAll(" ", "");
								}
								else{
									classInfo = fdata.trim().replaceAll(" ", "");
								}
							}
						}
					}
				}
				fdata = breader.readLine();
			}
			if (classInfo != null && classInfo.length() > 0){
				attNameList.add("class");
				attIDList.add(attID);
				attValueList.add(classInfo);
				if (classAltInfo != null){
					attAltValueList.add(classAltInfo);
				}
				else{
					attAltValueList.add("---");
				}
			}
			return true;
		}
		catch (IOException ioe){
			return false;
		}
	}

	/**
	 * getFIList method.
	 * Method used to get and return the contents from the
	 * read in frequent itemsets data file, which will be a
	 * list of items (in the itemset) and their frequency or
	 * support values.
	 */
	final public ArrayList getFIList(){
		return fiList;
	}

	/**
	 * getFCIList method.
	 * Method used to get and return the contents from the
	 * read in frequent closed itemsets data file, which will
	 * be a list of items (in the closed itemset), the generators
	 * that are associated with that itemset and their frequency or
	 * support values.
	 */
	final public ArrayList getFCIList(){
		return fciList;
	}

	/**
	 * getBatchFCIList method.
	 * Method used to get and return the contents from the
	 * read in multi-source frequent closed itemsets data file,
	 * which will be a list of items (in the closed itemset),
	 * the generators that are associated with that itemset
	 * and their frequency or support values. These will come
	 * from multiple datasets.
	 */
	final public ArrayList getBatchFCIList(){
		return bfciList;
	}

	/**
	 * getEntryList method.
	 * Method used to get and return the contents from the
	 * read in data file, which will be a list of transactions.
	 */
	final public ArrayList getEntryList(){
		return entryList;
	}

	/**
	 * getAttNameList method.
	 * Method used to get and return the list of item/attribute
	 * names contained within the transactions for the dataset
	 * that has been read.
	 */
	final public ArrayList getAttNameList(){
		return attNameList;
	}

	/**
	 * getAttValueList method.
	 * Method used to get and return the list of valid item/attribute
	 * values contained within the transactions for the dataset
	 * that has been read.
	 */
	final public ArrayList getAttValueList(){
		return attValueList;
	}

	/**
	 * getAttAltValueList method.
	 * Method used to get and return the list of alt valid
	 * item/attribute values contained within the transactions
	 * for the dataset that has been read.
	 */
	final public ArrayList getAttAltValueList(){
		return attAltValueList;
	}
	
	final public ArrayList getAttIDList(){
		return attIDList;
	}

	/**
	 * closeFile method.
	 * Method used to  close the readers connection to the external
	 * file that was the last one opened/read from.
	 */
	final public boolean closeFile(){
		try{
			breader.close();
			return true;
		}
		catch (IOException ioe){
			return false;
		}
	}

	/**
	 * clearData method.
	 * Method used to clear the data, variables and objects held
	 * by this class when they are no longer needed. This frees
	 * memory for other parts of the program.
	 */
	final public void clearData(){
		if (fiList != null){
			fiList.clear();
			fiList = null;
		}
		if (entryList != null){
			entryList.clear();
			entryList = null;
		}
		if (attNameList != null){
			attNameList.clear();
			attNameList = null;
		}
		if (attValueList != null){
			attValueList.clear();
			attValueList = null;
		}
		if (attAltValueList != null){
			attAltValueList.clear();
			attAltValueList = null;
		}
		breader = null;
		freader = null;
		file = null;
	}
}