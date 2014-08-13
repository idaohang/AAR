/**
 * DataCheckClean class.
 * 
 * Start Date: 01 December 2006
 * @author Gavin Shaw
 * @version 1.0
 *
 * Log:
 *		  1.0 Start version of class.
 */
package Processing;

import javax.swing.SwingUtilities;

import java.util.ArrayList;

import GUI.ARMMainGui;
import GUI.ARMMainNoGui;
import GUI.ARMCheckResponseGui;

import FileIO.DataFileReader;

import Data.TransRecords;

public class DataCheckClean{

	private ARMMainGui owner1;

	private ARMMainNoGui owner2;

	private ARMCheckResponseGui armcrg;

	private DataFileReader dfr;

	private TransRecords trec;

	private String stat;

	private ArrayList dList, aNameList, aValueList, aAltValueList, recordList, aIDList, aAllNameList;

	private int diffAttCount, moreAttCount, lessAttCount;
	
	private int missingAttCount, mrAttCount;

	private int invalidAttCount, irAttCount;

	private int totalAttCount;

	private long startTime, endTime;

	/**
	 * DataCheckClean method.
	 * Constructor.
	 * Method used to initialise this class which is used to
	 * take a just loaded dataset and check its contents to
	 * ensure it is as valid as possible.
	 */
	public DataCheckClean(ARMMainGui mgui){
		owner1 = mgui;
	}

	public DataCheckClean(ARMMainNoGui mnogui){
		owner2 = mnogui;
	}

	/**
	 * clearData method.
	 * Method used to destroy and clear the variables that hold the
	 * data and references that were used to perform the checking.
	 * This is to free up memory.
	 */
	final public void clearData(){
		trec.clearData();
		trec = null;
		armcrg = null;
		dList.clear();
		dList = null;
		aNameList = null;
		aValueList = null;
		aAltValueList = null;
		aIDList = null;
		recordList = null;
		System.gc();
	}

	/**
	 * runCheckClean method.
	 * Method used oversee the checking of a dataset's contents
	 * (multi or binary) and ensure it is as valid as possible.
	 * This process is run in its own thread allowing the GUI to
	 * stay responsive and report back to the user during the
	 * checking process.
	 */
	final public void runCheckClean(final String dataFilePath, final String dataInfoPath){
		Runnable cc = new Runnable(){
			public void run(){
				try{
					stat = "Loading data file";
					updateGui();
					//Try to load the file and read it...
					startTime = System.currentTimeMillis();
					dfr = new DataFileReader();
					if (dfr.setupReader(dataFilePath)){
						if (dfr.readDataFile()){
							dfr.closeFile();
							dList = dfr.getEntryList();
							if (dfr.setupReader(dataInfoPath)){
								if (dfr.readInfoFile()){
									dfr.closeFile();
									aNameList = dfr.getAttNameList();
									aValueList = dfr.getAttValueList();
									aAltValueList = dfr.getAttAltValueList();
									aIDList = dfr.getAttIDList();
									
									determineAllNodes();

									stat = "Identifying transactions/records";
									updateGui();
									//Process files contents into the separate transactions...
									formatTrans();

									stat = "Checking for missing attributes/values & cleaning as needed";
									updateGui();
									//Process the transactions and check for missing values/attributes...
									checkTrans();
//									convert();
									totalAttCount = ((String[])recordList.get(0)).length;
									armcrg = new ARMCheckResponseGui(owner1, recordList.size(), totalAttCount, 
																				diffAttCount, moreAttCount, lessAttCount,
																				missingAttCount, mrAttCount, invalidAttCount, irAttCount);
									trec = new TransRecords();
									trec.addData(recordList, aNameList, aValueList, aAltValueList, aIDList, aAllNameList);
									trec.setMTH(determineMaxTreeHeight());
									trec.setLTP(determineLongestTreePath());
									dfr.clearData();
									dfr = null;
									recordList.clear();
									aNameList.clear();
									aValueList.clear();
									aAltValueList.clear();
									aIDList.clear();
									owner1.attCountPassback(totalAttCount);
									owner1.dataPassback(trec);

									endTime = System.currentTimeMillis();
									stat = "Checking & cleaning complete. Total processing time: " + (endTime - startTime) + " ms";
									updateGui();
								}
								else{
									dfr.closeFile();
									stat = "Unable to read contents from selected data info file.";
									updateGui();
								}
							}
							else{
								stat = "Unable to locate selected data info (*.names) file.";
								updateGui();
							}
						}
						else{
							dfr.closeFile();
							stat = "Unable to read contents from selected data file.";
							updateGui();
						}
					}
					else{
						stat = "Unable to locate selected data (*.data) file.";
						updateGui();
					}
				}
				catch (OutOfMemoryError oom){
					stat = "Out of memory. Close application and restart.";
					updateGui();
				}
				catch (Exception e){
				}
			}
		};
		Thread dcc = new Thread(cc);
		dcc.start();
	}

	/**
	 * Command line version.
	 */
	final public void runCheckClean(String dataFilePath, String dataInfoPath, int x){
		try{
			System.out.println("Loading data file");
			//Try to load the file and read it...
			startTime = System.currentTimeMillis();
			dfr = new DataFileReader();
			if (dfr.setupReader(dataFilePath)){
				if (dfr.readDataFile()){
					dfr.closeFile();
					dList = dfr.getEntryList();
					if (dfr.setupReader(dataInfoPath)){
						if (dfr.readInfoFile()){
							dfr.closeFile();
							aNameList = dfr.getAttNameList();
							aValueList = dfr.getAttValueList();
							aAltValueList = dfr.getAttAltValueList();
							aIDList = dfr.getAttIDList();
							
							determineAllNodes();

							System.out.println("Identifying transactions/records");
							//Process files contents into the separate transactions...
							formatTrans();

							System.out.println("Checking for missing attributes/values & cleaning as needed");
							//Process the transactions and check for missing values/attributes...
							checkTrans();
							totalAttCount = ((String[])recordList.get(0)).length;
							trec = new TransRecords();
							trec.addData(recordList, aNameList, aValueList, aAltValueList, aIDList, aAllNameList);
							trec.setMTH(determineMaxTreeHeight());
							trec.setLTP(determineLongestTreePath());
							dfr.clearData();
							dfr = null;
							recordList.clear();
							aNameList.clear();
							aValueList.clear();
							aAltValueList.clear();
							aIDList.clear();
							owner2.attCountPassback(totalAttCount);
							owner2.dataPassback(trec);

							endTime = System.currentTimeMillis();
							System.out.println("Checking & cleaning complete. Total processing time: " + (endTime - startTime) + " ms");
						}
						else{
							dfr.closeFile();
							System.out.println("Unable to read contents from selected data info file.");
						}
					}
					else{
						System.out.println("Unable to locate selected data info (*.names) file.");
					}
				}
				else{
					dfr.closeFile();
					System.out.println("Unable to read contents from selected data file.");
				}
			}
			else{
				System.out.println("Unable to locate selected data (*.data) file.");
			}
		}
		catch (OutOfMemoryError oom){
			System.out.println("Out of memory. Close application and restart.");
		}
		catch (Exception e){
		}
	}

	/**
	 * formatTrans method.
	 * Method used to format all of the transactions in a dataset
	 * into a standard format to be used by the rest of this class
	 * during future processing.
	 */
	final private void formatTrans(){
		String trans;
		String[] record;
		//Setup each record and break attributes apart...
		recordList = new ArrayList();
		int r1, d1;
		d1 = dList.size();
		for (int i = 0; i < d1; i++){
			trans = ((String)dList.get(i)).trim();
			if (trans.charAt(trans.length() - 1) == '.'){
				trans = trans.substring(0, trans.length() - 1);
			}
			record = trans.split(",");
			r1 = record.length;
			for (int j = 0; j < r1; j++){
				record[j] = record[j].trim();
			}
			recordList.add(record);
		}
	}

	/**
	 * checkTrans method.
	 * Method that checks each transaction from the dataset and
	 * checks that it is valid. Any invalid transactions or items
	 * within a transaction are dealt with by this method. It will
	 * attempt to deal with too many items, too few items, missing or
	 * undefined item values and invalid item values.
	 */
	final private void checkTrans(){
		diffAttCount = 0;
		moreAttCount = 0;
		lessAttCount = 0;
		missingAttCount = 0;
		mrAttCount = 0;
		invalidAttCount = 0;
		irAttCount = 0;
		int pos = -1;
		boolean rCounted = false;
		boolean iCounted = false;
		boolean entryNum = false;
		String[] record;
		int r1, r2, a1, a2, n1;
		r1 = recordList.size();
		a1 = aNameList.size();
		a2 = aValueList.size();
		for (int i = 0; i < r1; i++){
			record = (String[])recordList.get(i);
			r2 = record.length;
			//Check the number of attributes that each record has...
			if (r2 != a1){
				//Record has a different number of attributes to the master attribute list...
				diffAttCount++;
				if (r2 > a1){
					//Extra attributes...
					moreAttCount++;
					String[] newrec = new String[a1];
					n1 = newrec.length;
					for (int j = 0; j < n1 - 1; j++){
						newrec[j] = record[j];
					}
					//Try to find class entry...
					if (((String)aValueList.get(a2 - 1)).indexOf(record[r2 - 1]) != -1){
						//Last entry contains a value valid for class id...
						newrec[n1 - 1] = record[r2 - 1];
					}
					else{
						//Check the next entry for a valid class id...
						if (((String)aValueList.get(a2 - 1)).indexOf(record[n1 - 1]) != -1){
							//Next entry contains a value valid for class id...
							newrec[n1 - 1] = record[n1 - 1];
						}
						else{
							//Neither the last or next entries contain a value valid for class id...
							//Place a missing value indicator for the class id...
							newrec[n1 - 1] = "?";
						}
					}
					recordList.set(i, newrec);
				}
				else{
					//Missing attributes...
					lessAttCount++;
					String[] newrec = new String[a1];
					n1 = newrec.length;
					for (int j = 0; j < r2 - 1; j++){
						newrec[j] = record[j];
						pos = j;
					}
					//Try to find class entry...
					if (((String)aValueList.get(a2 - 1)).indexOf(record[r2 - 1]) != -1){
						//Last entry contains a value valid for class id...
						newrec[n1 - 1] = record[r2 - 1];
					}
					else{
						//Last entry value not valid for class id...
						//Place a missing value indicator for the class id...
						newrec[pos + 1] = record[r2 - 1];
						newrec[n1 - 1] = "?";
						pos++;
					}
					//Still need to fill in the missing attributes...
					if (pos != -1){
						for (int j = pos + 1; j < n1 - 1; j++){
							newrec[j] = "?";
						}
					}
					recordList.set(i, newrec);
				}
			}
		}
		for (int i = 0; i < r1; i++){
			entryNum = false;
			record = (String[])recordList.get(i);
			r2 = record.length;
			//Check to see if the record has any missing attribute values...
			for (int j = 0; j < r2; j++){
				if (record[j].indexOf('?') != -1){
					//Record has a missing/undefined attribute value...
					if (rCounted == false){
						mrAttCount++;
						rCounted = true;
					}
					missingAttCount++;
				}
				else{
					//Record has value for attribute, so see if it is valid...
					try{
						Float.valueOf(record[j]).floatValue();
						entryNum = true;
					}
					catch (NumberFormatException e){
						entryNum = false;
					}
					if ((((String)aValueList.get(j)).indexOf(record[j]) != -1) ||
						 (((String)aValueList.get(j)).equalsIgnoreCase("continuous") && entryNum)){
						//Value for attribute is valid...
					}
					else{
						//Value for attribute is invalid...
						if (iCounted == false){
							irAttCount++;
							iCounted = true;
						}
						if (rCounted == false){
							mrAttCount++;
							rCounted = true;
						}
						invalidAttCount++;
						missingAttCount++;
						record[j] = "?";
					}
				}
			}
			recordList.set(i, record);
			rCounted = false;
			iCounted = false;
		}
		//Checking complete...
	}

	final private void convert(){
		String[] t;
		byte[] b;
		int r1, t1;
		r1 = recordList.size();
		for (int i = 0; i < r1; i++){
			t = (String[])recordList.get(i);
			t1 = t.length;
			b = new byte[t1];
			for (int j = 0; j < t1; j++){
				b[j] = Byte.valueOf(t[j]).byteValue();
			}
			recordList.set(i, b);
		}
	}
	
	final private void determineAllNodes(){
		aAllNameList = new ArrayList();
		int size = aNameList.size(), length = 0;
		String attName, attTemp;
		String[] attParts;
		
		for (int i = 0; i < size; i++){
			attName = (String)aNameList.get(i);
			attParts = attName.split("-");
			length = attParts.length;
			attTemp = "";
			for (int j = 0; j < length; j++){
				if (attTemp.length() > 0){
					attTemp = attTemp + "-" + attParts[j];
				}
				else{
					attTemp = attParts[j];
				}
				if (aAllNameList.indexOf(attTemp) == -1){
					//This node is not in the list of all nodes in the universe...
					aAllNameList.add(attTemp);
				}
			}
			if (aAllNameList.indexOf(attName) == -1){
				//This leaf node is not in the list of all nodes in the universe...
				aAllNameList.add(attName);
			}
		}
	}
	
	final private int determineMaxTreeHeight(){
		int count = aNameList.size();
		int height = 1;
		int level = 0;
		for (int i = 0; i < count; i++){
			level = ((String)aNameList.get(i)).split("-").length;
			if (level > height){
				height = level;
			}
		}
		return height;
	}
	
	final private int determineLongestTreePath(){
		int length = 2, clength = 0, mlength;
		int count = aNameList.size();
		String[] p1, p2;
		for (int i = 0; i < count; i++){
			p1 = ((String)aNameList.get(i)).split("-");
			for (int j = i + 1; j < count; j++){
				p2 = ((String)aNameList.get(j)).split("-");
				clength = p1.length + p2.length;
				if (p1.length <= p2.length){
					mlength = p1.length;
				}
				else{
					mlength = p2.length;
				}
				for (int k = 0; k < mlength; k++){
					if (p1[k].equals(p2[k])){
						//Still common ancestor...
						clength = clength - 2;
					}
					else{
						//No longer common ancestor...
						break;
					}
				}
				if (clength > length){
					length = clength;
				}
			}
		}
		return length;
	}

	/**
	 * updateGui method.
	 * Method used to pass messages back to the system's main
	 * GUI to inform the user about the progress of the checking.
	 */
	final private void updateGui(){
		final String message = stat;
		Runnable r = new Runnable(){
			public void run(){
				try{
					owner1.updateStatusMessage(message);
				}
				catch (Exception ie){
				} 
			}
		};
		SwingUtilities.invokeLater(r);
	}

	/**
	 * print method.
	 * Method used to print out the loaded and checked
	 * data table.
	 */
	final public void printDataTable(){
		trec.print();
	}
}