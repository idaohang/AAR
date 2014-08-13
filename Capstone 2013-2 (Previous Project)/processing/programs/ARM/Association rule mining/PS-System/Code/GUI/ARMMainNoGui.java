/**
 * ARMMainNoGui class.
 * 
 * Start Date: 01 December 2006
 * @author Gavin Shaw
 * @version 1.0
 *
 * Log:
 *		  1.0 Start version of class.
 */
package GUI;

import java.util.ArrayList;

import Processing.ConvertToBinary;
import Processing.DataCheckClean;
import Processing.BuildFrequentItemsets;
import Processing.BuildFrequentClosedItemsets;
import Processing.ExtractAssociationRules;
import Processing.DataLoader;

import Data.TransRecords;
import Data.CandidateList;
import Data.FItemsetList;
import Data.FCItemsetList;
import Data.AssociationRuleList;

import FileIO.DataFileWriter;

public class ARMMainNoGui{

	private ConvertToBinary ctb;

	private DataCheckClean dcc;

	private BuildFrequentItemsets bfis;

	private BuildFrequentClosedItemsets bfcis;

	private ExtractAssociationRules ears;

	private DataLoader dl;

	private TransRecords tr;

	private CandidateList cl;

	private FItemsetList fl;

	private FCItemsetList fcl;

	private AssociationRuleList arl;

	private DataFileWriter dfw;

	private long processTimeBF, processTimeBFC, processTimeAR;

	private int attCount = 0;

	private boolean loadedFI = false, loadedFCI = false, multiLevel = false, nonderivableRun = false;

	private ArrayList msl;
	
	private Object[] attNames, attLeaf;
	
	private String dir = "/";

	/**
	 * Non Gui starting point for command line only...
	 */
	public ARMMainNoGui(String[] parameters){
		String dataFile = parameters[0];
		float minsup = 1;
		boolean ee, eb, ae, ab;
		if ((parameters[1].split(",")).length == 1){
			//Single min support threshold value...
			minsup = Float.valueOf(parameters[1]).floatValue();
		}
		else{
			//Multiple min support threshold values...
			msl = new ArrayList();
			String[] temp = parameters[1].split(",");
			msl.add(Integer.valueOf(temp[0]).intValue());
			if (Integer.valueOf(temp[0]).intValue() == 3){
				for (int i = 1; i < temp.length; i++){
					msl.add(Integer.valueOf(temp[i]).intValue());
				}
			}
			else if (Integer.valueOf(temp[0]).intValue() == 2){
				for (int i = 1; i < temp.length; i++){
					msl.add((Float.valueOf(temp[i]).floatValue()) / (float)100);
				}
			}
			else{
				for (int i = 1; i < temp.length; i++){
					msl.add(Float.valueOf(temp[i]).floatValue());
				}
			}
		}
		float mincon = Float.valueOf(parameters[2]).floatValue();
		if (parameters[3].equalsIgnoreCase("T")){
			eb = true;
		}
		else{
			eb = false;
		}
		if (parameters[4].equalsIgnoreCase("T")){
			ee = true;
		}
		else{
			ee = false;
		}
		if (parameters[5].equalsIgnoreCase("T")){
			ab = true;
		}
		else{
			ab = false;
		}
		if (parameters[6].equalsIgnoreCase("T")){
			ae = true;
		}
		else{
			ae = false;
		}
		int id1 = Integer.valueOf(parameters[7]).intValue();
		int id2 = Integer.valueOf(parameters[8]).intValue();
		int id3 = Integer.valueOf(parameters[9]).intValue();
		String outputBase = parameters[10];

		if (msl == null){
			run(dataFile, minsup, mincon, eb, ee, ab, ae, id1, id2, id3, outputBase);
		}
		else{
			run(dataFile, msl, mincon, eb, ee, ab, ae, id1, id2, id3, outputBase);
		}
	}

	/**
	 * saveFrequentItemsets method.
	 * Method used to allow a user to specify where they wish to save a
	 * copy of the frequent itemsets that where found in the dataset.
	 */
	final private void saveFrequentItemsets(String in, String out){
		dfw = new DataFileWriter(out);
		dfw.writeFrequentItemsets(in, tr, fl, cl, processTimeBF);
	}

	/**
	 * saveFrequentClosedItemsets method.
	 * Method used to allow a user to specify where they wish to save a
	 * copy of the frequent closed itemsets that where found in the dataset.
	 */
	final private void saveFrequentClosedItemsets(String in, String out){
		dfw = new DataFileWriter(out);
		dfw.writeFrequentClosedItemsets(in, tr, fcl, processTimeBFC);
	}

	/**
	 * saveExtractedRules method.
	 * Method used to allow a user to specify where they wish to save a
	 * copy of the association rules that where extracted from the dataset.
	 */
	final private void saveExtractedRules(String in, String out, int id){
		dfw = new DataFileWriter(out);
		dfw.writeAssociationRules(in, arl, tr, id, processTimeAR, tr.getAllNodes(), attNames);
	}

	/**
	 * checkValues method.
	 * Method that checks the entries that hold the min support and
	 * confedence are valid values (numbers).
	 */
	final private boolean checkValues(float minsup, float mincon){
		if (mincon > 0 && mincon <= 1 && minsup > 0 && minsup <= 1){
			return true;
		}
		else{
			return false;
		}
	}

	final private boolean checkValues(ArrayList msl, float mincon){
		if (mincon > 0 && mincon <= 1){
			for (int i = 0; i < msl.size(); i++){
				if (((Float)msl.get(i)) <= 0 || ((Float)msl.get(i)) > 1){
					return false;
				}
			}
			return true;
		}
		else{
			return false;
		}
	}

	final private int determineNoLevels(){
		int a1, l1, levels = 0;
		String[] attNames = tr.getNames();
		a1 = attNames.length;
		for (int i = 0; i < a1; i++){
			l1 = attNames[i].split("-").length;
			if (l1 > levels){
				levels = l1;
			}
		}
		return levels;
	}

	/**
	 * clearMem method.
	 * Method used to try and clear/free memory by explicitly calling
	 * methods to destroy data which is being held in memory.
	 */
	final private void clearMem(int ID){
		if (ID == 1){
			if (tr != null){
				if (dcc != null){
					dcc.clearData();
					dcc = null;
				}
				tr.clearData();
				tr = null;
			}
		}
		if (ID == 1 || ID == 2){
			if (cl != null){
				cl.clearData();
				cl = null;
			}
			if (fl != null){
				fl.clearData();
				fl = null;
			}
			if (bfis != null){
				bfis.clearData();
				bfis = null;
			}
		}
		if (ID == 1 || ID == 2 || ID == 3){
			if (fcl != null){
				fcl.clearData();
				fcl = null;
			}
			if (bfcis != null){
				bfcis.clearData();
				bfcis = null;
			}
		}
		if (ID == 1 || ID == 2 || ID == 3 || ID == 4){
			if (arl != null){
				arl.clearData();
				arl = null;
			}
			if (ears != null){
				ears.clearData();
				ears = null;
			}
		}
	}

	final public void levelMinSup(ArrayList ms){
		msl = ms;
	}

	/**
	 * updateStatusMessage method.
	 * Method use to receive and hold the message to be displayed to the user.
	 */
	final public void updateStatusMessage(String message){
//		systemStatusField.setText(message);
	}

	/**
	 * timingPassbackBF method.
	 * Method used to receive and store the timing information about
	 * the extracted frequent itemsets.
	 */
	final public void timingPassbackBF(long time){
		processTimeBF = time;
	}

	/**
	 * timingPassbackBFC method.
	 * Method used to receive and store the timing information about
	 * the extracted frequent closed itemsets.
	 */
	final public void timingPassbackBFC(long time){
		processTimeBFC = time;
	}

	/**
	 * timingPassbackAR method.
	 * Method used to receive and store the timing information about
	 * the extracted association rules.
	 */
	final public void timingPassbackAR(long time){
		processTimeAR = time;
	}

	final public long passbackAR(){
		return processTimeAR;
	}

	/**
	 * mstPassback method.
	 * Method used to receive and store the minimum support threshold that
	 * was used to generate the contents of the file that contains frequent
	 * itemsets or frequent closed itemsets.
	 */
	final public void mstPassback(float mst){
//		minSupportField.setText("" + mst);
	}

	final public void attCountPassback(int ac){
		attCount = ac;
	}

	final public void multiLevelStat(boolean stat){
		multiLevel = stat;
	}

	final public void attNamesPassback(Object[] list){
		attNames = list;
	}
	
	final public void attLeafPassback(Object[] list){
		attLeaf = list;
	}

	/**
	 * dataPassback method.
	 * Method used to receive and store the dataset that was read/loaded
	 * by the system.
	 */
	final public void dataPassback(TransRecords data){
		tr = data;
	}

	/**
	 * loadFIStatusPassback method.
	 * Method used to receive and store the success of failure of loading
	 * in frequent itemsets from an external file.
	 */
	final public void loadFIStatusPassback(boolean status){
		loadedFI = status;
	}

	/**
	 * transRecordPassback method.
	 * Method used to receive and store a transRecords data object which holds
	 * the list of attribute/item names and their IDs.
	 */
	final public void transRecordPassback(TransRecords data){
		tr = data;
	}

	/**
	 * candidatePassback method.
	 * Method used to receive and store the list of candidates that was
	 * generated during the building of the frequent itmeset list.
	 */
	final public void candidatePassback(CandidateList data){
		cl = data;
	}

	/**
	 * frequentPassback method.
	 * Method used to receive and store the list of frequent itemsets that
	 * was generated.
	 */
	final public void frequentPassback(FItemsetList data){
		fl = data;
	}

	/**
	 * loadFCIStatusPassback method.
	 * Method used to receive and store the success of failure of loading
	 * in frequent closed itemsets from an external file.
	 */
	final public void loadFCIStatusPassback(boolean status){
		loadedFCI = status;
	}

	/**
	 * freqClosedPassback method.
	 * Method used to receive and store the list of frequent closed itemsets
	 * that was generated.
	 */
	final public void freqClosedPassback(FCItemsetList data){
		fcl = data;
	}

	/**
	 * assocRulePassback method.
	 * Method used to receive and store the list of association rules that
	 * was generated.
	 */
	final public void assocRulePassback(AssociationRuleList data){
		arl = data;
	}

	final private void run(String dataFile, float minsup, float mincon, boolean eb, boolean ee, boolean ab, boolean ae,
									int id1, int id2, int id3, String outputBase){
		System.out.println("Attempting to load input data file");
		if (id1 == -1 && id2 == 0){
			//Skip the FI stage as we are loading FCl & G from file (eg. the FCI & G have already been discovered)...
			System.out.println("Loading frequent closed itemsets and generators from data file");
			dl = new DataLoader(this);
			dl.loadFCI(dataFile, 1);
			ears = new ExtractAssociationRules(attCount, multiLevel, attNames, attLeaf, tr.getAbstractTable(), tr.getMTH(), tr.getLTP());
			if (id3 == 15){
				//Extract association rules using all four algorithms...
				ears.extractRules(this, fl, fcl, mincon, minsup, eb, ee, ab, ae, 1, 1);
				if (fcl != null && arl != null){
					saveExtractedRules(dataFile, outputBase + dir + "AssociationRules-1.txt", 1);
				}
				else{
					System.out.println("Unable to extract association rules from data file: " + dataFile);
				}
				ears.extractRules(this, fl, fcl, mincon, minsup, eb, ee, ab, ae, 2, 1);
				if (fcl != null && arl != null){
					saveExtractedRules(dataFile, outputBase + dir + "AssociationRules-2.txt", 2);
				}
				else{
					System.out.println("Unable to extract association rules from data file: " + dataFile);
				}
				ears.extractRules(this, fl, fcl, mincon, minsup, eb, ee, ab, ae, 3, 1);
				if (fcl != null && arl != null){
					saveExtractedRules(dataFile, outputBase + dir + "AssociationRules-3.txt", 3);
				}
				else{
					System.out.println("Unable to extract association rules from data file: " + dataFile);
				}
				ears.extractRules(this, fl, fcl, mincon, minsup, eb, ee, ab, ae, 4, 1);
				if (fcl != null && arl != null){
					saveExtractedRules(dataFile, outputBase + dir + "AssociationRules-4.txt", 4);
				}
				else{
					System.out.println("Unable to extract association rules from data file: " + dataFile);
				}
			}
			else{
				//Extract association rules using only one algorithm...
				ears.extractRules(this, fl, fcl, mincon, minsup, eb, ee, ab, ae, id3, 1);
				if (fcl != null && arl != null){
					saveExtractedRules(dataFile, outputBase + dir + "AssociationRules.txt", id3);
				}
				else{
					System.out.println("Unable to extract association rules from data file: " + dataFile);
				}
			}
		}
		else{
			//Must find FI, then FCI & G, then Association rules...
			if (dataFile.length() != 0 && dataFile != null){
				clearMem(1);
				dcc = new DataCheckClean(this);
				dcc.runCheckClean(dataFile, dataFile.replaceAll("\\.data", "\\.names"), 1);
				System.out.println("Finished loading input data file: " + dataFile);
				System.out.println("Attempting to extract frequent itemsets from data file");
				if (tr != null && checkValues(minsup, mincon)){
					clearMem(2);
					bfis = new BuildFrequentItemsets();
					bfis.build(this, tr, mincon, minsup, id1, 1);
					if (id1 == 11){
						//Used Non-Derivable Itemset Mining Algorithm...
						nonderivableRun = true;
					}
					else{
						nonderivableRun = false;
					}
					if (tr != null && fl != null){
						saveFrequentItemsets(dataFile, outputBase + dir + "FrequentItemsets-" + id3 +".txt");
						System.out.println("Attempting to extract frequent closed itemsets from data file");
						clearMem(3);
						bfcis = new BuildFrequentClosedItemsets(attNames, attLeaf);
						bfcis.build(this, fl, mincon, minsup, id2, 1);
						if (tr != null && fl != null && (fcl != null || nonderivableRun)){
							saveFrequentClosedItemsets(dataFile, outputBase + dir + "FrequentClosedItemsets-" + id3 +".txt");
							System.out.println("Attempting to extract association rules from data file");
//							clearMem(4);
							ears = new ExtractAssociationRules(attCount, multiLevel, attNames, attLeaf, tr.getAbstractTable(), tr.getMTH(), tr.getLTP());
							if (id3 == 15 && fcl != null){
								//Extract association rules using all four algorithms...
								ears.extractRules(this, fl, fcl, mincon, minsup, eb, ee, ab, ae, 1, 1);
								if (tr != null && fl != null && fcl != null && arl != null){
									saveExtractedRules(dataFile, outputBase + dir + "AssociationRules-1.txt", 1);
								}
								else{
									System.out.println("Unable to extract association rules from data file: " + dataFile);
								}
								ears.extractRules(this, fl, fcl, mincon, minsup, eb, ee, ab, ae, 2, 1);
								if (tr != null && fl != null && fcl != null && arl != null){
									saveExtractedRules(dataFile, outputBase + dir + "AssociationRules-2.txt", 2);
								}
								else{
									System.out.println("Unable to extract association rules from data file: " + dataFile);
								}
								ears.extractRules(this, fl, fcl, mincon, minsup, eb, ee, ab, ae, 3, 1);
								if (tr != null && fl != null && fcl != null && arl != null){
									saveExtractedRules(dataFile, outputBase + dir + "AssociationRules-3.txt", 3);
								}
								else{
									System.out.println("Unable to extract association rules from data file: " + dataFile);
								}
								ears.extractRules(this, fl, fcl, mincon, minsup, eb, ee, ab, ae, 4, 1);
								if (tr != null && fl != null && fcl != null && arl != null){
									saveExtractedRules(dataFile, outputBase + dir + "AssociationRules-4.txt", 4);
								}
								else{
									System.out.println("Unable to extract association rules from data file: " + dataFile);
								}
							}
							else{
								//Extract association rules using only one algorithm...
								if (id3 == 14 && nonderivableRun){
									ears.extractRules(this, fl, fcl, mincon, minsup, eb, ee, ab, ae, id3, 1);
								}
								else if (fcl != null){
									ears.extractRules(this, fl, fcl, mincon, minsup, eb, ee, ab, ae, id3, 1);
								}
								if (tr != null && fl != null && fcl != null && arl != null){
									saveExtractedRules(dataFile, outputBase + dir + "AssociationRules-" + id3 +".txt", id3);
								}
								else{
									System.out.println("Unable to extract association rules from data file: " + dataFile);
								}
							}
						}
						else{
							System.out.println("Unable to extract frequent closed itemsets from data file: " + dataFile);
						}
					}
					else{
						System.out.println("Unable to extract frequent itemsets from data file: " + dataFile);
					}
				}
				else{
					System.out.println("Unable to extract frequent itemsets from data file: " + dataFile);
				}
			}
			else{
				System.out.println("Unable to load input data file: " + dataFile);
			}
		}
	}

	final private void run(String dataFile, ArrayList msl, float mincon, boolean eb, boolean ee, boolean ab, boolean ae,
									int id1, int id2, int id3, String outputBase){
		System.out.println("Attempting to load input data file");
		if (id1 == -1 && id2 == 0){
			//Skip the FI stage as we are loading FCI & G from file (eg. the FCI & G have already been discovered)...
			System.out.println("Loading frequent closed itemsets and generators from data file");
			dl = new DataLoader(this);
			dl.loadFCI(dataFile, 1);
			ears = new ExtractAssociationRules(attCount, multiLevel, attNames, attLeaf, tr.getAbstractTable(), tr.getMTH(), tr.getLTP());
			if (id3 == 15){
				//Extract association rules using all four algorithms...
				if ((Integer)msl.get(0) != 3){
					ears.extractRules(this, fl, fcl, mincon, (Float)msl.get(1), eb, ee, ab, ae, 1, 2);
				}
				else{
					ears.extractRules(this, fl, fcl, mincon, (Integer)msl.get(1) / (float)tr.getNumberRecs(), eb, ee, ab, ae, 1, 2);
				}
				if (fcl != null && arl != null){
					saveExtractedRules(dataFile, outputBase + dir + "AssociationRules-1.txt", 1);
				}
				else{
					System.out.println("Unable to extract association rules from data file: " + dataFile);
				}
				if ((Integer)msl.get(0) != 3){
					ears.extractRules(this, fl, fcl, mincon, (Float)msl.get(1), eb, ee, ab, ae, 2, 2);
				}
				else{
					ears.extractRules(this, fl, fcl, mincon, (Integer)msl.get(1) / (float)tr.getNumberRecs(), eb, ee, ab, ae, 2, 2);
				}
				if (fcl != null && arl != null){
					saveExtractedRules(dataFile, outputBase + dir + "AssociationRules-2.txt", 2);
				}
				else{
					System.out.println("Unable to extract association rules from data file: " + dataFile);
				}
				if ((Integer)msl.get(0) != 3){
					ears.extractRules(this, fl, fcl, mincon, (Float)msl.get(1), eb, ee, ab, ae, 3, 2);
				}
				else{
					ears.extractRules(this, fl, fcl, mincon, (Integer)msl.get(1) / (float)tr.getNumberRecs(), eb, ee, ab, ae, 3, 2);
				}
				if (fcl != null && arl != null){
					saveExtractedRules(dataFile, outputBase + dir + "AssociationRules-3.txt", 3);
				}
				else{
					System.out.println("Unable to extract association rules from data file: " + dataFile);
				}
				if ((Integer)msl.get(0) != 3){
					ears.extractRules(this, fl, fcl, mincon, (Float)msl.get(1), eb, ee, ab, ae, 4, 2);
				}
				else{
					ears.extractRules(this, fl, fcl, mincon, (Integer)msl.get(1) / (float)tr.getNumberRecs(), eb, ee, ab, ae, 4, 2);
				}
				if (fcl != null && arl != null){
					saveExtractedRules(dataFile, outputBase + dir + "AssociationRules-4.txt", 4);
				}
				else{
					System.out.println("Unable to extract association rules from data file: " + dataFile);
				}
			}
			else{
				//Extract association rules using only one algorithm...
				if ((Integer)msl.get(0) != 3){
					ears.extractRules(this, fl, fcl, mincon, (Float)msl.get(1), eb, ee, ab, ae, id3, 2);
				}
				else{
					ears.extractRules(this, fl, fcl, mincon, (Integer)msl.get(1) / (float)tr.getNumberRecs(), eb, ee, ab, ae, id3, 2);
				}
				if (fcl != null && arl != null){
					saveExtractedRules(dataFile, outputBase + dir + "AssociationRules-" + id3 +".txt", id3);
				}
				else{
					System.out.println("Unable to extract association rules from data file: " + dataFile);
				}
			}
		}
		else{
			if (dataFile.length() != 0 && dataFile != null){
				clearMem(1);
				dcc = new DataCheckClean(this);
				dcc.runCheckClean(dataFile, dataFile.replaceAll("\\.data", "\\.names"), 2);
				System.out.println("Finished loading input data file: " + dataFile);
				System.out.println("Attempting to extract frequent itemsets from data file");
				if (tr != null){// && checkValues(msl, mincon)){
					clearMem(2);
					bfis = new BuildFrequentItemsets();
					if (id1 == 1 || id1 == 8 || id1 == 9){
						bfis.build(this, tr, mincon, (Float)msl.get(1), id1, 2);
					}
					else{
						bfis.build(this, tr, mincon, msl, id1, 2);
					}
					if (id1 == 11){
						//Used Non-Derivable Itemset Mining Algorithm...
						nonderivableRun = true;
					}
					else{
						nonderivableRun = false;
					}
					if (tr != null && fl != null){
						saveFrequentItemsets(dataFile, outputBase + dir + "FrequentItemsets-" + id3 +".txt");
						System.out.println("Attempting to extract frequent closed itemsets from data file");
						clearMem(3);
						bfcis = new BuildFrequentClosedItemsets(attNames, attLeaf);
						if ((Integer)msl.get(0) != 3){
							bfcis.build(this, fl, mincon, (Float)msl.get(1) / (float)tr.getNumberRecs(), id2, 2);
						}
						else{
							bfcis.build(this, fl, mincon, (Integer)msl.get(1) / (float)tr.getNumberRecs(), id2, 2);
						}
						if (tr != null && fl != null && (fcl != null || nonderivableRun)){
							saveFrequentClosedItemsets(dataFile, outputBase + dir + "FrequentClosedItemsets-" + id3 +".txt");
							System.out.println("Attempting to extract association rules from data file");
//							clearMem(4);
							ears = new ExtractAssociationRules(attCount, multiLevel, attNames, attLeaf, tr.getAbstractTable(), tr.getMTH(), tr.getLTP());
							if (id3 == 15 && fcl != null){
								//Extract association rules using all four algorithms...
								if ((Integer)msl.get(0) != 3){
									ears.extractRules(this, fl, fcl, mincon, (Float)msl.get(1), eb, ee, ab, ae, 1, 2);
								}
								else{
									ears.extractRules(this, fl, fcl, mincon, (Integer)msl.get(1) / (float)tr.getNumberRecs(), eb, ee, ab, ae, 1, 2);
								}
								if (tr != null && fl != null && fcl != null && arl != null){
									saveExtractedRules(dataFile, outputBase + dir + "AssociationRules-1.txt", 1);
								}
								else{
									System.out.println("Unable to extract association rules from data file: " + dataFile);
								}
								if ((Integer)msl.get(0) != 3){
									ears.extractRules(this, fl, fcl, mincon, (Float)msl.get(1), eb, ee, ab, ae, 2, 2);
								}
								else{
									ears.extractRules(this, fl, fcl, mincon, (Integer)msl.get(1) / (float)tr.getNumberRecs(), eb, ee, ab, ae, 2, 2);
								}
								if (tr != null && fl != null && fcl != null && arl != null){
									saveExtractedRules(dataFile, outputBase + dir + "AssociationRules-2.txt", 2);
								}
								else{
									System.out.println("Unable to extract association rules from data file: " + dataFile);
								}
								if ((Integer)msl.get(0) != 3){
									ears.extractRules(this, fl, fcl, mincon, (Float)msl.get(1), eb, ee, ab, ae, 3, 2);
								}
								else{
									ears.extractRules(this, fl, fcl, mincon, (Integer)msl.get(1) / (float)tr.getNumberRecs(), eb, ee, ab, ae, 3, 2);
								}
								if (tr != null && fl != null && fcl != null && arl != null){
									saveExtractedRules(dataFile, outputBase + dir + "AssociationRules-3.txt", 3);
								}
								else{
									System.out.println("Unable to extract association rules from data file: " + dataFile);
								}
								if ((Integer)msl.get(0) != 3){
									ears.extractRules(this, fl, fcl, mincon, (Float)msl.get(1), eb, ee, ab, ae, 4, 2);
								}
								else{
									ears.extractRules(this, fl, fcl, mincon, (Integer)msl.get(1) / (float)tr.getNumberRecs(), eb, ee, ab, ae, 4, 2);
								}
								if (tr != null && fl != null && fcl != null && arl != null){
									saveExtractedRules(dataFile, outputBase + dir + "AssociationRules-4.txt", 4);
								}
								else{
									System.out.println("Unable to extract association rules from data file: " + dataFile);
								}
							}
							else{
								//Extract association rules using only one algorithm...
								if (id3 == 14 && nonderivableRun){
									if ((Integer)msl.get(0) != 3){
										ears.extractRules(this, fl, fcl, mincon, (Float)msl.get(1), eb, ee, ab, ae, id3, 2);
									}
									else{
										ears.extractRules(this, fl, fcl, mincon, (Integer)msl.get(1) / (float)tr.getNumberRecs(), eb, ee, ab, ae, id3, 2);
									}
								}
								else if (fcl != null){
									if ((Integer)msl.get(0) != 3){
										ears.extractRules(this, fl, fcl, mincon, (Float)msl.get(1), eb, ee, ab, ae, id3, 2);
									}
									else{
										ears.extractRules(this, fl, fcl, mincon, (Integer)msl.get(1) / (float)tr.getNumberRecs(), eb, ee, ab, ae, id3, 2);
									}
								}
								if (tr != null && fl != null && fcl != null && arl != null){
									saveExtractedRules(dataFile, outputBase + dir + "AssociationRules-" + id3 +".txt", id3);
								}
								else{
									System.out.println("Unable to extract association rules from data file: " + dataFile);
								}
							}
						}
						else{
							System.out.println("Unable to extract frequent closed itemsets from data file: " + dataFile);
						}
					}
					else{
						System.out.println("Unable to extract frequent itemsets from data file: " + dataFile);
					}
				}
				else{
					System.out.println("Unable to extract frequent itemsets from data file: " + dataFile);
				}
			}
			else{
				System.out.println("Unable to load input data file: " + dataFile);
			}
		}
	}
}