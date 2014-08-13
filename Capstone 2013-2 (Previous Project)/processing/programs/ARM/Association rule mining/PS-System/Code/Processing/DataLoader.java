/**
 * DataLoader class.
 * 
 * Start Date: 15 March 2007
 * @author Gavin Shaw
 * @version 1.0
 *
 * Log:
 *		  1.0 Start version of class.
 */
package Processing;

import java.util.ArrayList;

import javax.swing.SwingUtilities;

import GUI.ARMMainGui;
import GUI.ARMMainNoGui;

import FileIO.DataFileReader;
import FileIO.DataFileWriter;

import Processing.MinMaxAssociationRuleExtractor;
import Processing.ReliableRuleExtractor;

import Data.CandidateList;
import Data.FItemsetList;
import Data.FCItemsetList;
import Data.AssociationRuleList;
import Data.TransRecords;

public class DataLoader{

	private ARMMainGui owner1;

	private ARMMainNoGui owner2;

	private DataFileReader dfr;

	private DataFileWriter dfw;

	private CandidateList cl;

	private FItemsetList fl;

	private FCItemsetList fcl;

	private AssociationRuleList arl;
	
	private TransRecords tr;

	private MinMaxAssociationRuleExtractor mmare;

	private ReliableRuleExtractor rre;

	private DataLoader dl;

	private String stat, statBackup;

	private ArrayList fileContents;

	private ArrayList freqItem, freqCount, freqSup, transRecNames, transRecIDs;

	private String[] freqCItem;

	private float minSupThresValue = -1, minCon;

	private long startTime, endTime;

	private boolean dataValid = true;

	/**
	 *
	 */
	public DataLoader(ARMMainGui mgui){
		owner1 = mgui;
		dfr = new DataFileReader();
		dfw = new DataFileWriter();
	}

	public DataLoader(ARMMainNoGui mgui){
		owner2 = mgui;
		dfr = new DataFileReader();
		dfw = new DataFileWriter();
	}

	/**
	 * loadFI method.
	 */
	final public void loadFI(final String filepath){
		Runnable load = new Runnable(){
			public void run(){
				try{
					stat = "Reading specified file.";
					updateGui();
					startTime = System.currentTimeMillis();
					dfr.setupReader(filepath);
					dfr.readFIFile();
					dfr.closeFile();
					fileContents = dfr.getFIList();
					stat = "Building frequent itemset list.";
					updateGui();
					extractFI();
					owner1.frequentPassback(fl);
					owner1.candidatePassback(cl);
					owner1.dataPassback(tr);
					if (minSupThresValue > 0){
						owner1.mstPassback(minSupThresValue);
					}
					owner1.loadFIStatusPassback(dataValid);
					endTime = System.currentTimeMillis();
					if (dataValid){
						stat = "Finished building and loading frequent itemset list. Total generation time: " + (endTime - startTime) + " ms";
					}
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
		Thread lfi = new Thread(load);
		lfi.start();
	}

	/**
	 * loadFCI method.
	 */
	final public void loadFCI(final String filepath){
		Runnable load = new Runnable(){
			public void run(){
				try{
					stat = "Reading specified file.";
					updateGui();
					startTime = System.currentTimeMillis();
					dfr.setupReader(filepath);
					dfr.readFCIFile();
					dfr.closeFile();
					fileContents = dfr.getFCIList();
					stat = "Building frequent closed itemset list.";
					updateGui();
					extractFCI();
					owner1.freqClosedPassback(fcl);
					owner1.dataPassback(tr);
					if (minSupThresValue > 0){
						owner1.mstPassback(minSupThresValue);
					}
					owner1.loadFCIStatusPassback(dataValid);
					endTime = System.currentTimeMillis();
					if (dataValid){
						stat = "Finished building and loading frequent closed itemset list. Total generation time: " + (endTime - startTime) + " ms";
					}
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
		Thread lfci = new Thread(load);
		lfci.start();
	}

	/**
	 * loadFCI method.
	 */
	final public void loadFCI(String filepath, int x){
		try{
			System.out.println("Reading specified file.");
			startTime = System.currentTimeMillis();
			dfr.setupReader(filepath);
			dfr.readFCIFile();
			dfr.closeFile();
			fileContents = dfr.getFCIList();
			System.out.println("Building frequent closed itemset list.");
			extractFCI(1);
			owner2.freqClosedPassback(fcl);
			owner2.dataPassback(tr);
			if (minSupThresValue > 0){
//				owner2.mstPassback(minSupThresValue);
			}
			owner2.loadFCIStatusPassback(dataValid);
			endTime = System.currentTimeMillis();
			if (dataValid){
				System.out.println("Finished building and loading frequent closed itemset list. Total generation time: " + (endTime - startTime) + " ms");
			}
		}
		catch (OutOfMemoryError oom){
			System.out.println("Out of memory. Close application and restart.");
		}
		catch (Exception e){
		}
	}

	/**
	 * loadBatchFCI method.
	 */
	final public void loadBatchFCI(final String filepath, float mc, final boolean eb, final boolean ee, final boolean ab,
												final boolean ae,final int mode){
		dl = this;
		if (mc > 1){
			minCon = mc / 100;
		}
		else{
			minCon = mc;
		}
		Runnable load = new Runnable(){
			public void run(){
				try{
					stat = "Reading specified file.";
					updateGui();
					startTime = System.currentTimeMillis();
					dfr.setupReader(filepath);
					dfr.readBatchFCIFile();
					dfr.closeFile();
					if (dfw.setPathBatch(repath(filepath), true)){
						fileContents = dfr.getBatchFCIList();
						extractBatchFCI(minCon, eb, ee, ab, ae, mode);
						endTime = System.currentTimeMillis();
						stat = "Finished extracting association rules from multiple datasets. Total extraction time: " + (endTime - startTime) + " ms";
						updateGui();
					}
					else{
						stat = "Error attempting to setup output file for rules. Extraction process stopped.";
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
		Thread lbfci = new Thread(load);
		lbfci.start();
	}

	final private String repath(String oldpath){
		String newpath;
		if (oldpath.indexOf(".") != -1){
			newpath = oldpath.substring(0, oldpath.indexOf(".")) + "-rules" + oldpath.substring(oldpath.indexOf("."));
		}
		else{
			newpath = oldpath + "-rules.txt";
		}
		return newpath;
	}

	/**
	 * extractFI method.
	 */
	final private void extractFI(){
		ArrayList tempFI = new ArrayList();
		ArrayList tempSup = new ArrayList();
		String entry;
		String[] info;
		tr = new TransRecords();
		fl = new FItemsetList();
		cl = new CandidateList();
		int transCount = -1, length = 1, itemID = 1;
		float fiValue = -1;

		//Attempt to load the process and load the frequent itemsets...
		for (int i = 0; i < fileContents.size(); i++){
			entry = (String)fileContents.get(i);
			if (entry.startsWith("Total_Transaction_Count:")){
				//This line holds the transaction count...
				info = entry.split(":");
				try{
					transCount = Integer.valueOf(info[1]).intValue();
					dataValid =  true;
					if (transCount < 1){
						stat = "Format of total transaction count invalid (less than 1).";
						updateGui();
						transCount = -1;
						minSupThresValue = -1;
						i = fileContents.size();
						dataValid = false;
					}
				}
				catch (NumberFormatException nfe){
					stat = "Format of total transaction count invalid (not a number).";
					updateGui();
					transCount = -1;
					minSupThresValue = -1;
					i = fileContents.size();
					dataValid = false;
				}
			}
			else if (entry.startsWith("Min_Support_Threshold:")){
				//This line holds the minimum support threshold value that was used...
				info = entry.split(":");
				try{
					minSupThresValue = Float.valueOf(info[1]).floatValue();
					dataValid = true;
					if (minSupThresValue < 0 || minSupThresValue > 1){
						stat = "Format of minimum support threshold invalid (less than 0 or greater than 1).";
						updateGui();
						transCount = -1;
						minSupThresValue = -1;
						i = fileContents.size();
						dataValid = false;
					}
				}
				catch (NumberFormatException nfe){
					stat = "Format of minimum support threshold invalid (not a number).";
					updateGui();
					transCount = -1;
					minSupThresValue = -1;
					i = fileContents.size();
					dataValid = false;
				}
			}
			else if (i == 0){
				//First line of the file, could hold the min support threshold without label...
				try{
					minSupThresValue = Float.valueOf(entry).floatValue();
					dataValid = true;
					if (minSupThresValue < 0 || minSupThresValue > 1){
						stat = "Format of minimum support threshold invalid (less than 0 or greater than 1).";
						updateGui();
						transCount = -1;
						minSupThresValue = -1;
						i = fileContents.size();
						dataValid = false;
					}
				}
				catch (NumberFormatException nfe){
					minSupThresValue = -1;
					dataValid = false;
				}
			}
			else{
				//This line is a frequent itemset...
				info = entry.split(":");
				try{
					fiValue = Float.valueOf(info[1]).floatValue();
					info[0] = info[0].replaceAll(" ", ",");
					tempFI.add(info[0]);
					tempSup.add(fiValue);
				}
				catch (NumberFormatException nfe){
					stat = "Format of frequent itemset frequency/support invalid (not a number).";
					updateGui();
					fiValue = -1;
					i = fileContents.size();
					dataValid = false;
				}
			}
		}

		//Sort the frequent itemsets and store them into memory...
		if (dataValid){
			transRecNames = new ArrayList();
			transRecIDs = new ArrayList();
			//Do the first pass looking for length 1 itemsets (allows the name & id lists to be built)...
			freqItem = new ArrayList();
			freqCount = new ArrayList();
			freqSup = new ArrayList();
			for (int i = 0; i < tempFI.size(); i++){
				info = ((String)tempFI.get(i)).split(",");
				if (info.length == length){
					//This frequent itemset has the length we are searching for...
					//As this is a length 1 itemset, use it to build the name & id list...
					transRecNames.add((String)tempFI.get(i));
					transRecIDs.add(itemID);
					itemID++;
					freqItem.add(translate((String)tempFI.get(i)));
					if (transCount != -1){
						freqCount.add(((Float)tempSup.get(i)).intValue());
						freqSup.add((Float)tempSup.get(i) / (float)transCount);
					}
					else{
						freqCount.add("N/A");
						freqSup.add((Float)tempSup.get(i));
					}
					tempFI.remove(i);
					tempSup.remove(i);
					i--;
				}
			}
			//Store into memory...
			fl.addEntry(freqItem, freqCount, freqSup);
			length++;
			//Do the following passes until the temp list is empty...
			while (tempFI.size() != 0){
				freqItem = new ArrayList();
				freqCount = new ArrayList();
				freqSup = new ArrayList();
				for (int i = 0; i < tempFI.size(); i++){
					info = ((String)tempFI.get(i)).split(",");
					if (info.length == length){
						//This frequent itemset has the length we are searching for...
						freqItem.add(translate((String)tempFI.get(i)));
						if (transCount != -1){
							freqCount.add(((Float)tempSup.get(i)).intValue());
							freqSup.add((Float)tempSup.get(i) / (float)transCount);
						}
						else{
							freqCount.add("N/A");
							freqSup.add((Float)tempSup.get(i));
						}
						tempFI.remove(i);
						tempSup.remove(i);
						i--;
					}
				}
				//Store into memory...
				fl.addEntry(freqItem, freqCount, freqSup);
				length++;
			}
			tr.addNames(transRecNames);
			tr.addIDs(transRecIDs);
		}
	}

	final private int[] translate(String itemset){
		int[] id;
		String[] t = itemset.split(",");
		int count = t.length;
		id = new int[count];
		for (int i = 0; i < count; i++){
			id[i] = (Integer)transRecIDs.get(transRecNames.indexOf(t[i]));
		}
		return id;
	}

	/**
	 * extractFCI method.
	 */
	final private void extractFCI(){
		ArrayList tempFCI = new ArrayList();
		ArrayList tempGen = new ArrayList();
		ArrayList tempSup = new ArrayList();
		String entry;
		String[] info;
		fcl = new FCItemsetList();
		float fciValue = -1;

		//Attempt to load the process and load the frequent itemsets...
		for (int i = 0; i < fileContents.size(); i++){
			entry = (String)fileContents.get(i);
			if (entry.startsWith("Min_Support_Threshold:")){
				//This line holds the minimum support threshold value that was used...
				info = entry.split(":");
				try{
					minSupThresValue = Float.valueOf(info[1]).floatValue();
					dataValid = true;
					if (minSupThresValue < 0 || minSupThresValue > 1){
						stat = "Format of minimum support threshold invalid (less than 0 or greater than 1).";
						updateGui();
						minSupThresValue = -1;
						i = fileContents.size();
						dataValid = false;
					}
				}
				catch (NumberFormatException nfe){
					stat = "Format of minimum support threshold invalid (not a number).";
					updateGui();
					minSupThresValue = -1;
					i = fileContents.size();
					dataValid = false;
				}
			}
			else if (i == 0){
				//First line of the file, could hold the min support threshold without label...
				try{
					minSupThresValue = Float.valueOf(entry).floatValue();
					dataValid = true;
					if (minSupThresValue < 0 || minSupThresValue > 1){
						stat = "Format of minimum support threshold invalid (less than 0 or greater than 1).";
						updateGui();
						minSupThresValue = -1;
						i = fileContents.size();
						dataValid = false;
					}
				}
				catch (NumberFormatException nfe){
					minSupThresValue = -1;
					dataValid = false;
				}
			}
			else{
				//This line is a frequent closed itemset...
				info = entry.split("]");
				try{
					fciValue = Float.valueOf(info[1].trim()).floatValue();
					info = info[0].split(",");
					info[0] = info[0].replaceAll(" ", ",");
					info[0] = info[0].substring(1);
					for (int j = 1; j < info.length; j++){
						info[j] = info[j].replaceAll(" ", ",");
						tempFCI.add(info[0]);
						tempGen.add(info[j]);
						tempSup.add(fciValue);
					}
				}
				catch (NumberFormatException nfe){
					stat = "Format of frequent itemset frequency/support invalid (not a number).";
					updateGui();
					fciValue = -1;
					i = fileContents.size();
					dataValid = false;
				}
			}
		}

		//Sort the frequent itemsets and store them into memory...
		if (dataValid){
			while (tempFCI.size() != 0){
				freqCItem = new String[2];
				freqCItem[0] = (String)tempGen.get(0);
				freqCItem[1] = (String)tempFCI.get(0);
				fcl.addEntry(freqCItem, (Float)tempSup.get(0));
				tempFCI.remove(0);
				tempGen.remove(0);
				tempSup.remove(0);
			}
		}
	}

	/**
	 * extractFCI method.
	 */
	final private void extractFCI(int x){
		ArrayList tempFCI = new ArrayList();
		ArrayList tempGen = new ArrayList();
		ArrayList tempSup = new ArrayList();
		String entry;
		String[] info;
		fcl = new FCItemsetList();
		float fciValue = -1;

		//Attempt to load the process and load the frequent itemsets...
		for (int i = 0; i < fileContents.size(); i++){
			entry = (String)fileContents.get(i);
			if (entry.startsWith("Min_Support_Threshold:")){
				//This line holds the minimum support threshold value that was used...
				info = entry.split(":");
				try{
					minSupThresValue = Float.valueOf(info[1]).floatValue();
					dataValid = true;
					if (minSupThresValue < 0 || minSupThresValue > 1){
						minSupThresValue = -1;
						i = fileContents.size();
						dataValid = false;
					}
				}
				catch (NumberFormatException nfe){
					minSupThresValue = -1;
					i = fileContents.size();
					dataValid = false;
				}
			}
			else if (i == 0){
				//First line of the file, could hold the min support threshold without label...
				try{
					minSupThresValue = Float.valueOf(entry).floatValue();
					dataValid = true;
					if (minSupThresValue < 0 || minSupThresValue > 1){
						minSupThresValue = -1;
						i = fileContents.size();
						dataValid = false;
					}
				}
				catch (NumberFormatException nfe){
					minSupThresValue = -1;
					dataValid = false;
				}
			}
			else{
				//This line is a frequent closed itemset...
				info = entry.split("]");
				try{
					fciValue = Float.valueOf(info[1].trim()).floatValue();
					info = info[0].split(",");
					info[0] = info[0].replaceAll(" ", ",");
					info[0] = info[0].substring(1);
					for (int j = 1; j < info.length; j++){
						info[j] = info[j].replaceAll(" ", ",");
						tempFCI.add(info[0]);
						tempGen.add(info[j]);
						tempSup.add(fciValue);
					}
				}
				catch (NumberFormatException nfe){
					fciValue = -1;
					i = fileContents.size();
					dataValid = false;
				}
			}
		}

		//Sort the frequent itemsets and store them into memory...
		if (dataValid){
			while (tempFCI.size() != 0){
				freqCItem = new String[2];
				freqCItem[0] = (String)tempGen.get(0);
				freqCItem[1] = (String)tempFCI.get(0);
				fcl.addEntry(freqCItem, (Float)tempSup.get(0));
				tempFCI.remove(0);
				tempGen.remove(0);
				tempSup.remove(0);
			}
		}
	}

	/**
	 * extractBatchFCI method.
	 */
	final private void extractBatchFCI(float mc, boolean eb, boolean ee, boolean ab, boolean ae, int mode){
		String entry, datasetID;
		String[] info;
		fcl = new FCItemsetList();
		float fciValue = -1;

		entry = (String)fileContents.get(0);
		if (entry.startsWith("Min_Support_Threshold:")){
			//This line holds the minimum support threshold value that was used...
			info = entry.split(":");
			try{
				minSupThresValue = Float.valueOf(info[1]).floatValue();
				dataValid = true;
				if (minSupThresValue < 0 || minSupThresValue > 1){
					stat = "Format of minimum support threshold invalid (less than 0 or greater than 1).";
					updateGui();
					minSupThresValue = -1;
					dataValid = false;
				}
			}
			catch (NumberFormatException nfe){
				stat = "Format of minimum support threshold invalid (not a number).";
				updateGui();
				minSupThresValue = -1;
				dataValid = false;
			}
		}
		else{
			//First line of the file, could hold the min support threshold without label...
			try{
				minSupThresValue = Float.valueOf(entry).floatValue();
				dataValid = true;
				if (minSupThresValue < 0 || minSupThresValue > 1){
					stat = "Format of minimum support threshold invalid (less than 0 or greater than 1).";
					updateGui();
					minSupThresValue = -1;
					dataValid = false;
				}
			}
			catch (NumberFormatException nfe){
				minSupThresValue = -1;
				dataValid = false;
			}
		}
		fileContents.remove(0);
		if (dataValid){
			//Min support threshold was specified...
			//Get the first dataset ID tag and remove...
			datasetID = (String)fileContents.get(0);
			fileContents.remove(0);
			while (fileContents.size() != 0){
				ArrayList tempFCI = new ArrayList();
				ArrayList tempGen = new ArrayList();
				ArrayList tempSup = new ArrayList();
				stat = "Processing frequent closed itemsets for dataset " + datasetID.replaceAll(":", "") + ".";
				updateGui();
				while ((((String)fileContents.get(0)).indexOf(":") == -1) && dataValid &&
							((String)fileContents.get(0)).equals("<*EOF*>") != true){
					info = ((String)fileContents.get(0)).split("]");
					try{
						fciValue = Float.valueOf(info[1].trim()).floatValue();
						info = info[0].split(",");
						info[0] = info[0].replaceAll(" ", ",");
						info[0] = info[0].substring(1);
						for (int j = 1; j < info.length; j++){
							info[j] = info[j].replaceAll(" ", ",");
							tempFCI.add(info[0]);
							tempGen.add(info[j]);
							tempSup.add(fciValue);
						}
					}
					catch (NumberFormatException nfe){
						stat = "Format of frequent itemset frequency/support invalid (not a number) in dataset " + datasetID.replaceAll(":", "") + ".";
						updateGui();
						dataValid = false;
					}
					fileContents.remove(0);
				}
				//Extracted all entries for this dataset...
				//Sort the frequent itemsets and store them into memory...
				if (dataValid){
					fcl = new FCItemsetList();
					while (tempFCI.size() != 0){
						freqCItem = new String[2];
						freqCItem[0] = (String)tempGen.get(0);
						freqCItem[1] = (String)tempFCI.get(0);
						fcl.addEntry(freqCItem, (Float)tempSup.get(0));
						tempFCI.remove(0);
						tempGen.remove(0);
						tempSup.remove(0);
					}
					stat = "Extracting association rules for dataset " + datasetID.replaceAll(":", "");
					statBackup = stat;
					updateGui();
					//Extract the association rules for this dataset...

					if (mode == 1){
						//Use Yue's algorithm...
						rre = new ReliableRuleExtractor(dl);
						rre.runAlgorithm(fcl, mc, minSupThresValue, eb, ee, ab, ae, 0, false);
						arl = rre.getAssocRules();
					}
					else{
						//Use Pasquier's algorithm...
						mmare = new MinMaxAssociationRuleExtractor(dl);
						mmare.runAlgorithm(fcl, mc, minSupThresValue, eb, ee, ab, ae, 0, false);
						arl = mmare.getAssocRules();
					}

					//Add the extracted rules to the output file...
					dfw.writeAssociationRules(datasetID, arl, tr, mode, owner1.passbackAR(), tr.getAllNodes(), tr.getNames());
				}
				//Setup for the next dataset...
				fcl.clearData();
				arl.clearData();
				datasetID = (String)fileContents.get(0);
				fileContents.remove(0);
			}
		}
	}

	/**
	 * messageBox method.
	 * Method used to allow all of the algorithms implemented
	 * to generate the frequent closed itemsets to store
	 * messages to pass back to the user.
	 */
	final public void messageBox(String message){
		stat = statBackup + "   " + message;
		updateGui();
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
					owner1.updateStatusMessage(message);
				}
				catch (Exception ie){
				} 
			}
		};
		SwingUtilities.invokeLater(r);
	}
}