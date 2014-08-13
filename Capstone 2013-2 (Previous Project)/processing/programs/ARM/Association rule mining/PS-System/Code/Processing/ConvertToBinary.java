/**
 * ConvertToBinary class.
 * 
 * Start Date: 28 December 2006
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

import FileIO.DataFileReader;
import FileIO.DataFileWriter;

import Data.TransRecords;

public class ConvertToBinary{

	private ARMMainGui owner;

	private DataFileReader dfr;

	private DataFileWriter dfw;

	private TransRecords trec;

	private String stat;

	private ArrayList attExpanded;

	private ArrayList newRecList, newAttList, newAttValues;

	private ArrayList dList, aNameList, aValueList, aAltValueList, aIDList, recordList;

	private int diffAttCount, moreAttCount, lessAttCount;
	
	private int missingAttCount, mrAttCount;

	private int invalidAttCount, irAttCount;

	private long startTime, endTime;

	/**
	 * ConvertToBinary method.
	 * Constructor.
	 * Method used to initialise this class which is used to convert
	 * a multi-dimensional dataset into a binary dataset.
	 */
	public ConvertToBinary(ARMMainGui mgui){
		owner = mgui;
	}

	/**
	 * clearData method.
	 * Method used to destroy and clear the variables that hold the
	 * data and references that were used to perform the conversion.
	 * This is to free up memory.
	 */
	final public void clearData(){
		trec.clearData();
		trec = null;
		dList.clear();
		dList = null;
		aNameList = null;
		aValueList = null;
		aAltValueList = null;
		aIDList = null;
		recordList = null;
		newRecList.clear();
		newRecList = null;
		newAttList.clear();
		newAttList = null;
		newAttValues.clear();
		newAttValues = null;
		System.gc();
	}

	/**
	 * runConvert method.
	 * Method used to start and oversee the conversion of a
	 * multi-dimensional dataset into a binary dataset. This process
	 * is run in its own thread allowing the GUI to stay responsive
	 * and report back to the user during the conversion process.
	 */
	final public void runConvert(final String sourcePath, final String destPath){
		Runnable con = new Runnable(){
			public void run(){
				try{
					startTime = System.currentTimeMillis();
					stat = "Converting multi-dimensional data to binary format";
					updateGui();
					//Read in source file and build data table...
					stat = "Generating data table from data in source file";
					updateGui();
					dataCheckClean(sourcePath, sourcePath.replaceAll("\\.data", "\\.names"));
					//Convert multi-dimensional data into 'binary' format...
					stat = "Generating new attribute list (based on discrete and continuous data)";
					updateGui();
					createAttList(trec);
					stat = "Converting data table to binary format";
					updateGui();
					createBinaryDataTable(trec);
					updateData(trec);
					stat = "Generating new data file";
					updateGui();
					createNewDataFile(trec, destPath, destPath.replaceAll(".data", ".names"));
					owner.dataPassback(trec);
					endTime = System.currentTimeMillis();
					stat = "Conversion to binary format complete. Total processing time: " + (endTime - startTime) + " ms";
					updateGui();
				}
				catch (OutOfMemoryError oom){
					stat = "Out of memory. Close application and restart.";
					updateGui();
				}
				catch (Exception e){
				}
			}
		};
		Thread cb = new Thread(con);
		cb.start();
	}

	/**
	 * dataCheckClean method.
	 * Method used to take a dataset's contents and oversee the
	 * operation of formatting each transaction and also checking
	 * the contents of each transaction to see if there are missing
	 * or invalid entries.
	 */
	final private void dataCheckClean(String dataFilePath, String dataInfoPath){
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

						//Process files contents into the separate transactions...
						formatTrans();

						//Process the transactions and check for missing values/attributes...
						checkTrans();

						trec = new TransRecords();
						trec.addData(recordList, aNameList, aValueList, aAltValueList, aIDList);
						dfr.clearData();
						dfr = null;
						recordList.clear();
						aNameList.clear();
						aValueList.clear();
						aAltValueList.clear();
						aIDList.clear();
						owner.dataPassback(trec);
					}
					else{
						dfr.closeFile();
					}
				}
				else{
				}
			}
			else{
				dfr.closeFile();
			}
		}
		else{
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
		for (int i = 0; i < dList.size(); i++){
			trans = ((String)dList.get(i)).trim();
			if (trans.charAt(trans.length() - 1) == '.'){
				trans = trans.substring(0, trans.length() - 1);
			}
			record = trans.split(",");
			for (int j = 0; j < record.length; j++){
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
		for (int i = 0; i < recordList.size(); i++){
			record = (String[])recordList.get(i);
			//Check the number of attributes that each record has...
			if (record.length != aNameList.size()){
				//Record has a different number of attributes to the master attribute list...
				diffAttCount++;
				if (record.length > aNameList.size()){
					//Extra attributes...
					moreAttCount++;
					String[] newrec = new String[aNameList.size()];
					for (int j = 0; j < newrec.length - 1; j++){
						newrec[j] = record[j];
					}
					//Try to find class entry...
					if (((String)aValueList.get(aValueList.size() - 1)).indexOf(record[record.length - 1]) != -1){
						//Last entry contains a value valid for class id...
						newrec[newrec.length - 1] = record[record.length - 1];
					}
					else{
						//Check the next entry for a valid class id...
						if (((String)aValueList.get(aValueList.size() - 1)).indexOf(record[newrec.length - 1]) != -1){
							//Next entry contains a value valid for class id...
							newrec[newrec.length - 1] = record[newrec.length - 1];
						}
						else{
							//Neither the last or next entries contain a value valid for class id...
							//Place a missing value indicator for the class id...
							newrec[newrec.length - 1] = "?";
						}
					}
					recordList.set(i, newrec);
				}
				else{
					//Missing attributes...
					lessAttCount++;
					String[] newrec = new String[aNameList.size()];
					for (int j = 0; j < record.length - 1; j++){
						newrec[j] = record[j];
						pos = j;
					}
					//Try to find class entry...
					if (((String)aValueList.get(aValueList.size() - 1)).indexOf(record[record.length - 1]) != -1){
						//Last entry contains a value valid for class id...
						newrec[newrec.length - 1] = record[record.length - 1];
					}
					else{
						//Last entry value not valid for class id...
						//Place a missing value indicator for the class id...
						newrec[pos + 1] = record[record.length - 1];
						newrec[newrec.length - 1] = "?";
						pos++;
					}
					//Still need to fill in the missing attributes...
					if (pos != -1){
						for (int j = pos + 1; j < newrec.length - 1; j++){
							newrec[j] = "?";
						}
					}
					recordList.set(i, newrec);
				}
			}
		}
		for (int i = 0; i < recordList.size(); i++){
			entryNum = false;
			record = (String[])recordList.get(i);
			//Check to see if the record has any missing attribute values...
			for (int j = 0; j < record.length; j++){
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

	/**
	 * createAttList method.
	 * Method used to create a list of all of the binary items/attributes
	 * contained within the dataset's transactions. Each will be named
	 * after the original item/attribute with the value appended to the
	 * end. For items/attributes that are already binary (such as yes/no,
	 * true/false or 0,1) the item/attribute will be unchanged. Only those
	 * items/attributes that have multiple values (such as age) will be
	 * affected and will be expanded so that each instance built on the base
	 * item/attribute is binary and deals with one value found in the dataset.
	 * Eg. (AGE item has the following values in the dataset: 23, 25, 26. Three
	 * new items will be created: AGE-23, AGE-25 and AGE-26. Each one will be
	 * binary for the associated value).
	 */
	final private void createAttList(TransRecords tr){
		Object[] records;
		String[] attNames, attValues, attAltValues, temp1;
		ArrayList temp2;
		//ArrayList attNames, attValues, attAltValues, temp1, temp2;
		//ArrayList records;
		String valueList1, valueList2;
		String[] discreteValues;
		String[] rec;
		int a1, r1, t1;

		attExpanded = new ArrayList();
		newAttList = new ArrayList();
		records = tr.getRec();
		attNames = tr.getNames();
		attValues = tr.getVal1();
		attAltValues = tr.getVal2();
		a1 = attValues.length;
		r1 = records.length;
		for (int i = 0; i < a1; i++){
			valueList1 = attValues[i];
			valueList2 = attAltValues[i];
			if (valueList1.equalsIgnoreCase("continuous") != true){
				//Discrete values for this attribute...
				//See if attribute is based on true/false, yes/no or 0/1 values...
				if (valueList1.equalsIgnoreCase("0,1") || valueList1.equalsIgnoreCase("1,0")){
					for (int j = 0; j < r1; j++){
						rec = (String[])records[j];
						if (rec[i].equalsIgnoreCase("0")){
							rec[i] = "0";
						}
						else if (rec[i].equalsIgnoreCase("1")){
							rec[i] = "1";
						}
						else{
							rec[i] = "0";
						}
						records[j] = rec;
					}
					newAttList.add(attNames[i]);
					attExpanded.add(false);
				}
				else if (valueList1.equalsIgnoreCase("y,n") || valueList1.equalsIgnoreCase("n,y")){
					for (int j = 0; j < r1; j++){
						rec = (String[])records[j];
						if (rec[i].equalsIgnoreCase("y")){
							rec[i] = "1";
						}
						else if (rec[i].equalsIgnoreCase("n")){
							rec[i] = "0";
						}
						else{
							rec[i] = "0";
						}
						records[j] = rec;
					}
					newAttList.add(attNames[i]);
					attExpanded.add(false);
				}
				else if (valueList1.equalsIgnoreCase("yes,no") || valueList1.equalsIgnoreCase("no,yes")){
					for (int j = 0; j < r1; j++){
						rec = (String[])records[j];
						if (rec[i].equalsIgnoreCase("yes")){
							rec[i] = "1";
						}
						else if (rec[i].equalsIgnoreCase("no")){
							rec[i] = "0";
						}
						else{
							rec[i] = "0";
						}
						records[j] = rec;
					}
					newAttList.add(attNames[i]);
					attExpanded.add(false);
				}
				else if (valueList1.equalsIgnoreCase("t,f") || valueList1.equalsIgnoreCase("f,t")){
					for (int j = 0; j < r1; j++){
						rec = (String[])records[j];
						if (rec[i].equalsIgnoreCase("t")){
							rec[i] = "1";
						}
						else if (rec[i].equalsIgnoreCase("f")){
							rec[i] = "0";
						}
						else{
							rec[i] = "0";
						}
						records[j] = rec;
					}
					newAttList.add(attNames[i]);
					attExpanded.add(false);
				}
				else if (valueList1.equalsIgnoreCase("true,false") || valueList1.equalsIgnoreCase("false,true")){
					for (int j = 0; j < r1; j++){
						rec = (String[])records[j];
						if (rec[i].equalsIgnoreCase("true")){
							rec[i] = "1";
						}
						else if (rec[i].equalsIgnoreCase("false")){
							rec[i] = "0";
						}
						else{
							rec[i] = "0";
						}
						records[j] = rec;
					}
					newAttList.add(attNames[i]);
					attExpanded.add(false);
				}
				else if (valueList2.equalsIgnoreCase("1,0") || valueList2.equalsIgnoreCase("0,1")){
					discreteValues = valueList1.split(",");
					if (valueList2.equalsIgnoreCase("1,0")){
						for (int j = 0; j < r1; j++){
							rec = (String[])records[j];
							if (rec[i].equalsIgnoreCase(discreteValues[0])){
								rec[i] = "1";
							}
							else if (rec[i].equalsIgnoreCase(discreteValues[1])){
								rec[i] = "0";
							}
							else{
								rec[i] = "0";
							}
							records[j] = rec;
						}
					}
					else{
						for (int j = 0; j < r1; j++){
							rec = (String[])records[j];
							if (rec[i].equalsIgnoreCase(discreteValues[0])){
								rec[i] = "0";
							}
							else if (rec[i].equalsIgnoreCase(discreteValues[1])){
								rec[i] = "1";
							}
							else{
								rec[i] = "0";
							}
							records[j] = rec;
						}
					}
					newAttList.add(attNames[i]);
					attExpanded.add(false);
				}
				else if (valueList2.equalsIgnoreCase("y,n") || valueList2.equalsIgnoreCase("n,y")){
					discreteValues = valueList1.split(",");
					if (valueList2.equalsIgnoreCase("y,n")){
						for (int j = 0; j < r1; j++){
							rec = (String[])records[j];
							if (rec[i].equalsIgnoreCase(discreteValues[0])){
								rec[i] = "1";
							}
							else if (rec[i].equalsIgnoreCase(discreteValues[1])){
								rec[i] = "0";
							}
							else{
								rec[i] = "0";
							}
							records[j] = rec;
						}
					}
					else{
						for (int j = 0; j < r1; j++){
							rec = (String[])records[j];
							if (rec[i].equalsIgnoreCase(discreteValues[0])){
								rec[i] = "0";
							}
							else if (rec[i].equalsIgnoreCase(discreteValues[1])){
								rec[i] = "1";
							}
							else{
								rec[i] = "0";
							}
							records[j] = rec;
						}
					}
					newAttList.add(attNames[i]);
					attExpanded.add(false);
				}
				else if (valueList2.equalsIgnoreCase("yes,no") || valueList2.equalsIgnoreCase("no,yes")){
					discreteValues = valueList1.split(",");
					if (valueList2.equalsIgnoreCase("yes,no")){
						for (int j = 0; j < r1; j++){
							rec = (String[])records[j];
							if (rec[i].equalsIgnoreCase(discreteValues[0])){
								rec[i] = "1";
							}
							else if (rec[i].equalsIgnoreCase(discreteValues[1])){
								rec[i] = "0";
							}
							else{
								rec[i] = "0";
							}
							records[j] = rec;
						}
					}
					else{
						for (int j = 0; j < r1; j++){
							rec = (String[])records[j];
							if (rec[i].equalsIgnoreCase(discreteValues[0])){
								rec[i] = "0";
							}
							else if (rec[i].equalsIgnoreCase(discreteValues[1])){
								rec[i] = "1";
							}
							else{
								rec[i] = "0";
							}
							records[j] = rec;
						}
					}
					newAttList.add(attNames[i]);
					attExpanded.add(false);
				}
				else if (valueList2.equalsIgnoreCase("t,f") || valueList2.equalsIgnoreCase("f,t")){
					discreteValues = valueList1.split(",");
					if (valueList2.equalsIgnoreCase("t,f")){
						for (int j = 0; j < r1; j++){
							rec = (String[])records[j];
							if (rec[i].equalsIgnoreCase(discreteValues[0])){
								rec[i] = "1";
							}
							else if (rec[i].equalsIgnoreCase(discreteValues[1])){
								rec[i] = "0";
							}
							else{
								rec[i] = "0";
							}
							records[j] = rec;
						}
					}
					else{
						for (int j = 0; j < r1; j++){
							rec = (String[])records[j];
							if (rec[i].equalsIgnoreCase(discreteValues[0])){
								rec[i] = "0";
							}
							else if (rec[i].equalsIgnoreCase(discreteValues[1])){
								rec[i] = "1";
							}
							else{
								rec[i] = "0";
							}
							records[j] = rec;
						}
					}
					newAttList.add(attNames[i]);
					attExpanded.add(false);
				}
				else if (valueList2.equalsIgnoreCase("true,false") || valueList2.equalsIgnoreCase("false,true")){
					discreteValues = valueList1.split(",");
					if (valueList2.equalsIgnoreCase("true,false")){
						for (int j = 0; j < r1; j++){
							rec = (String[])records[j];
							if (rec[i].equalsIgnoreCase(discreteValues[0])){
								rec[i] = "1";
							}
							else if (rec[i].equalsIgnoreCase(discreteValues[1])){
								rec[i] = "0";
							}
							else{
								rec[i] = "0";
							}
							records[j] = rec;
						}
					}
					else{
						for (int j = 0; j < r1; j++){
							rec = (String[])records[j];
							if (rec[i].equalsIgnoreCase(discreteValues[0])){
								rec[i] = "0";
							}
							else if (rec[i].equalsIgnoreCase(discreteValues[1])){
								rec[i] = "1";
							}
							else{
								rec[i] = "0";
							}
							records[j] = rec;
						}
					}
					newAttList.add(attNames[i]);
					attExpanded.add(false);
				}
				else{
					discreteValues = valueList1.split(",");
					for (int j = 0; j < discreteValues.length; j++){
						if (discreteValues[j].equals("?") != true){
							newAttList.add(attNames[i] + "_" + discreteValues[j]);
						}
					}
					attExpanded.add(true);
				}
			}
			else{
				//Continuous values for this attribute...
				temp1 = trec.getSingleAttValues(i);
				t1 = temp1.length;
				temp2 = new ArrayList();
				for (int j = 0; j < t1; j++){
					if (temp2.indexOf(temp1[j]) == -1){
						temp2.add(temp1[j]);
					}
				}
				t1 = temp2.size();
				for (int j = 0; j < t1; j++){
					if (((String)temp2.get(j)).equals("?") != true){
						newAttList.add((attNames[i]) + "_" + (String)temp2.get(j));
					}
				}
				attExpanded.add(true);
			}
		}
		newAttValues = new ArrayList();
		for (int i = 0; i < newAttList.size(); i++){
			newAttValues.add("0,1");
		}
	}

	/**
	 * createBinaryDataTable method.
	 * Method used to create the binary data table built on the
	 * multi-dimension dataset. Each entry in table is a transaction
	 * and each entry within a transaction is an item/attribute.
	 */
	final private void createBinaryDataTable(TransRecords tr){
		Object[] temp1;
		String[] temp2;
		String[] rec, temp3;
		int t1, t3;
		int index = -1;

		newRecList = new ArrayList();
		temp1 = tr.getRec();
		temp2 = tr.getNames();
		t1 = temp1.length;
		//For each record in the original data table...
		for (int i = 0; i < t1; i++){
			rec = new String[newAttList.size()];
			temp3 = (String[])temp1[i];
			t3 = temp3.length;
			//Setup new record for binary data table and default all entries to 0...
			for (int j = 0; j < rec.length; j++){
				rec[j] = "0";
			}
			for (int j = 0; j < t3; j++){
				if ((Boolean)attExpanded.get(j)){
					index = newAttList.indexOf((temp2[j]) + "_" + temp3[j]);
					if (index != -1){
						rec[index] = "1";
					}
					index = -1;
				}
				else{
					index = newAttList.indexOf(temp2[j]);
					if (index != -1){
						rec[index] = temp3[j];
					}
					index = -1;
				}
			}
			newRecList.add(rec);
		}
	}

	/**
	 * updateData method.
	 * Method that updates the data class that represents the dataset
	 * table in memory from the original multi-dimensional one to the
	 * newly generated binary one.
	 */
	final private void updateData(TransRecords tr){
		tr.clearData();
		tr.addData(newRecList, newAttList, newAttValues, newAttValues);
	}

	/**
	 * createNewDataFile method.
	 * Method that oversees the exporting and saving of the new binary
	 * dataset to external file for future use.
	 */
	final private void createNewDataFile(TransRecords tr, String dataFile, String infoFile){
		if (dataFile.endsWith(".data") != true){
			dataFile = dataFile + ".data";
			infoFile = dataFile.replaceAll(".data", ".names");
		}
		dfw = new DataFileWriter(dataFile);
		dfw.writeBinaryDataFile(tr);
		dfw = new DataFileWriter(infoFile);
		dfw.writeBinaryInfoFile(tr);
	}

	/**
	 * updateGui method.
	 * Method used to pass messages back to the system's main
	 * GUI to inform the user about the progress of the conversion.
	 */
	final private void updateGui(){
		final String message = stat;
		Runnable r = new Runnable(){
			public void run(){
				try{
					owner.updateStatusMessage(message);
				}
				catch (Exception ie){
				} 
			}
		};
		SwingUtilities.invokeLater(r);
	}

	/**
	 * print method.
	 * Method used to print out the list of binary attributes.
	 */
	final private void print(){
		for (int i = 0; i < newAttList.size(); i++){
			System.out.println((String)newAttList.get(i));
		}
	}
}