/**
 * ExtractAssociationRules class.
 * 
 * Start Date: 24 January 2007
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

import Processing.MinMaxAssociationRuleExtractor;
import Processing.MinMaxAssociationRuleExtractorDiv;
import Processing.MinMaxAssociationRuleExtractorCov;

import Processing.ReliableRuleExtractor;
import Processing.ReliableRuleExtractorDiv;
import Processing.ReliableRuleExtractorCov;
import Processing.ReliableRuleExtractor2;

import Processing.ModMinMaxAssociationRuleExtractor;
import Processing.ModMinMaxAssociationRuleExtractorDiv;
import Processing.ModMinMaxAssociationRuleExtractorCov;

import Processing.ModReliableRuleExtractor;
import Processing.ModReliableRuleExtractorDiv;
import Processing.ModReliableRuleExtractorCov;

import Processing.NonDerivableRuleExtractor; //For the project students to use to implement Non-Derivable Association Rule Mining

import Data.FCItemsetList;
import Data.FItemsetList; //Specifically for the non-derivable association rule algorithm...

public class ExtractAssociationRules{

	private ARMMainGui owner1;

	private ARMMainNoGui owner2;

	private ExtractAssociationRules ears;

	private MinMaxAssociationRuleExtractor mmare;
	
	private MinMaxAssociationRuleExtractorDiv mmared;
	
	private MinMaxAssociationRuleExtractorCov mmarec;

	private ReliableRuleExtractor rere;
	
	private ReliableRuleExtractorDiv rered;
	
	private ReliableRuleExtractorCov rerec;
	
	private ReliableRuleExtractor2 rere2;

	private ModMinMaxAssociationRuleExtractor mare;
	
	private ModMinMaxAssociationRuleExtractorDiv mared;
	
	private ModMinMaxAssociationRuleExtractorCov marec;

	private ModReliableRuleExtractor mrre;
	
	private ModReliableRuleExtractorDiv mrred;
	
	private ModReliableRuleExtractorCov mrrec;

	private NonDerivableRuleExtractor ndre;

	private FCItemsetList fcl;

	private FItemsetList fl;

	private float minCon, minSup;

	private boolean ebRules, eeRules, abRules, aeRules;

	private long startTime, endTime;

	private int attCount, MTH, LTP;

	private boolean multiLevel;

	private Object[] attNames, attLeaf, abstractAtts;

	private String stat;

	/**
	 * ExtractAssociationRules method.
	 * Constructor.
	 * Method used to initialise this class which oversees the
	 * generation/mining/extraction of the association rules by
	 * one of the implemented algorithms chosen/specified by the user.
	 */
	public ExtractAssociationRules(int ac, boolean ml, Object[] names, Object[] leaves, Object[] table, int h, int l){
		attCount = ac;
		multiLevel = ml;
		attNames = names;
		attLeaf = leaves;
		abstractAtts = table;
		MTH = h;
		LTP = l;
	}

	/**
	 * clearData method.
	 * Method used to clear the data, variables and objects held
	 * by this class when they are no longer needed. This frees
	 * memory for other parts of the program.
	 */
	final public void clearData(){
		if (mmare != null){
			mmare.clearData();
			mmare = null;
		}
		if (rere != null){
			rere.clearData();
			rere = null;
		}
		if (rere2 != null){
			rere2.clearData();
			rere2 = null;
		}
		if (mrre != null){
			mrre.clearData();
			mrre = null;
		}
		if (mare != null){
			mare.clearData();
			mare = null;
		}
		if (mmared != null){
			mmared.clearData();
			mmared = null;
		}
		if (mmarec != null){
			mmarec.clearData();
			mmarec = null;
		}
		System.gc();
	}

	/**
	 * extractRules method.
	 * Method used to oversee the extraction of the association rules
	 * by selecting and overseeing the execution of the algorithm
	 * chosen/specified by the user. The algorithm does the actual work,
	 * this method passes the work to it and gathers the results when it
	 * finishes. This approach allows multiple algorithms to be implemented
	 * and made available to the user to use and still be managed from one
	 * point. This process is run in its own thread allowing the GUI to
	 * stay responsive and report back to the user during the mining process.
	 */
	final public void extractRules(ARMMainGui main, FItemsetList l, FCItemsetList list, float mc, float ms, boolean eb, boolean ee, boolean ab, boolean ae, int ac){
		final int algorithmCode = ac;
		owner1 = main;
		ears = this;
		fl = l;
		fcl = list;
		if (mc > 1){
			minCon = mc / 100;
		}
		else{
			minCon = mc;
		}
		if (ms > 1){
			minSup = ms / 100;
		}
		else{
			minSup = ms;
		}
		ebRules = eb;
		eeRules = ee;
		abRules = ab;
		aeRules = ae;

		Runnable er = new Runnable(){
			public void run(){
				try{
					stat = "Preparing to extract association rules";
					updateGui();
					startTime = System.currentTimeMillis();
					if (algorithmCode == 1){
						rere = new ReliableRuleExtractor(ears, attNames, attLeaf, MTH, LTP);
						stat = "Running Yue Xu's ReliableExactRule Algorithm";
						updateGui();
						rere.runAlgorithm(fcl, minCon, minSup, ebRules, eeRules, abRules, aeRules, attCount, multiLevel);
						owner1.assocRulePassback(rere.getAssocRules());
					}
					else if (algorithmCode == 2){
						mmare = new MinMaxAssociationRuleExtractor(ears, attNames, attLeaf, MTH, LTP);
						stat = "Running Pasquier's Min-max Algorithm";
						updateGui();
						mmare.runAlgorithm(fcl, minCon, minSup, ebRules, eeRules, abRules, aeRules, attCount, multiLevel);
						owner1.assocRulePassback(mmare.getAssocRules());
					}
					else if (algorithmCode == 3){
						mrre = new ModReliableRuleExtractor(ears, attNames, attLeaf, abstractAtts, MTH, LTP);
						stat = "Running Modified Yue's ReliableExactRule with HRR Algorithm";
						updateGui();
						mrre.runAlgorithm(fcl, minCon, minSup, ebRules, eeRules, abRules, aeRules, attCount, multiLevel);
						owner1.assocRulePassback(mrre.getAssocRules());
					}
					else if (algorithmCode == 4){
						mare = new ModMinMaxAssociationRuleExtractor(ears, attNames, attLeaf, abstractAtts, MTH, LTP);
						stat = "Running Modified Pasquier's Min-max with HRR Algorithm";
						updateGui();
						mare.runAlgorithm(fcl, minCon, minSup, ebRules, eeRules, abRules, aeRules, attCount, multiLevel);
						owner1.assocRulePassback(mare.getAssocRules());
					}
					else if (algorithmCode == 5){
						rere2 = new ReliableRuleExtractor2(ears, attNames, attLeaf, MTH, LTP);
						stat = "Running Yue Xu's ReliableExactRule - 2 Algorithm";
						updateGui();
						rere2.runAlgorithm(fcl, minCon, minSup, ebRules, eeRules, abRules, aeRules, attCount, multiLevel);
						owner1.assocRulePassback(rere2.getAssocRules());
					}
					else if (algorithmCode == 6){
						mmared = new MinMaxAssociationRuleExtractorDiv(ears, attNames, attLeaf, MTH, LTP);
						stat = "Running Pasquier's Min-max Algorithm - Diversity version";
						updateGui();
						mmared.runAlgorithm(fcl, minCon, minSup, ebRules, eeRules, abRules, aeRules, attCount, multiLevel);
						owner1.assocRulePassback(mmared.getAssocRules());
					}
					else if (algorithmCode == 7){
						mmarec = new MinMaxAssociationRuleExtractorCov(ears, attNames, attLeaf, MTH, LTP);
						stat = "Running Pasquier's Min-max Algorithm - Coverage version";
						updateGui();
						mmarec.runAlgorithm(fcl, minCon, minSup, ebRules, eeRules, abRules, aeRules, attCount, multiLevel);
						owner1.assocRulePassback(mmarec.getAssocRules());
					}
					else if (algorithmCode == 8){
						mared = new ModMinMaxAssociationRuleExtractorDiv(ears, attNames, attLeaf, abstractAtts, MTH, LTP);
						stat = "Running Modified Pasquier's Min-max with HRR Algorithm - Diversity version";
						updateGui();
						mared.runAlgorithm(fcl, minCon, minSup, ebRules, eeRules, abRules, aeRules, attCount, multiLevel);
						owner1.assocRulePassback(mared.getAssocRules());
					}
					else if (algorithmCode == 9){
						marec = new ModMinMaxAssociationRuleExtractorCov(ears, attNames, attLeaf, abstractAtts, MTH, LTP);
						stat = "Running Modified Pasquier's Min-max with HRR Algorithm - Coverage version";
						updateGui();
						marec.runAlgorithm(fcl, minCon, minSup, ebRules, eeRules, abRules, aeRules, attCount, multiLevel);
						owner1.assocRulePassback(marec.getAssocRules());
					}
					else if (algorithmCode == 10){
						rered = new ReliableRuleExtractorDiv(ears, attNames, attLeaf, MTH, LTP);
						stat = "Running Yue Xu's ReliableExactRule Algorithm - Diversity version";
						updateGui();
						rered.runAlgorithm(fcl, minCon, minSup, ebRules, eeRules, abRules, aeRules, attCount, multiLevel);
						owner1.assocRulePassback(rered.getAssocRules());
					}
					else if (algorithmCode == 11){
						rerec = new ReliableRuleExtractorCov(ears, attNames, attLeaf, MTH, LTP);
						stat = "Running Yue Xu's ReliableExactRule Algorithm - Coverage version";
						updateGui();
						rerec.runAlgorithm(fcl, minCon, minSup, ebRules, eeRules, abRules, aeRules, attCount, multiLevel);
						owner1.assocRulePassback(rerec.getAssocRules());
					}
					else if (algorithmCode == 12){
						mrred = new ModReliableRuleExtractorDiv(ears, attNames, attLeaf, abstractAtts, MTH, LTP);
						stat = "Running Modified Yue's ReliableExactRule with HRR Algorithm - Diversity version";
						updateGui();
						mrred.runAlgorithm(fcl, minCon, minSup, ebRules, eeRules, abRules, aeRules, attCount, multiLevel);
						owner1.assocRulePassback(mrred.getAssocRules());
					}
					else if (algorithmCode == 13){
						mrrec = new ModReliableRuleExtractorCov(ears, attNames, attLeaf, abstractAtts, MTH, LTP);
						stat = "Running Modified Yue's ReliableExactRule with HRR Algorithm - Coverage version";
						updateGui();
						mrrec.runAlgorithm(fcl, minCon, minSup, ebRules, eeRules, abRules, aeRules, attCount, multiLevel);
						owner1.assocRulePassback(mrrec.getAssocRules());
					}
					else if (algorithmCode == 14){
						ndre = new NonDerivableRuleExtractor(ears, attNames, attLeaf, MTH, LTP);
						stat = "Running Non-Derivable Association Rule Mining";
						updateGui();
						ndre.runAlgorithm(fl, minCon, minSup, ebRules, eeRules, abRules, aeRules, attCount, multiLevel);
						owner1.assocRulePassback(ndre.getAssocRules());
					}
					endTime = System.currentTimeMillis();
					stat = "Finished extracting association rules. Total extraction time: " + (endTime - startTime) + " ms";
					updateGui();
					owner1.timingPassbackAR(endTime - startTime);
				}
				catch (OutOfMemoryError oom){
					stat = "Out of memory. Close application and restart.";
					updateGui();
				}
				catch (Exception e){
					System.out.println(e);
				}
			}
		};
		Thread extract = new Thread(er);
		extract.start();
	}

	/**
	 * Command line version.
	 */
	final public void extractRules(ARMMainNoGui main, FItemsetList l, FCItemsetList list, float mc, float ms, boolean eb, boolean ee, boolean ab, boolean ae, int ac, int x){
		final int algorithmCode = ac;
		owner2 = main;
		ears = this;
		fl = l;
		fcl = list;
		if (mc > 1){
			minCon = mc / 100;
		}
		else{
			minCon = mc;
		}
		if (ms > 1){
			minSup = ms / 100;
		}
		else{
			minSup = ms;
		}
		ebRules = eb;
		eeRules = ee;
		abRules = ab;
		aeRules = ae;

		try{
			System.out.println("Preparing to extract association rules");
			startTime = System.currentTimeMillis();
			if (algorithmCode == 1){
				rere = new ReliableRuleExtractor(ears, attNames, attLeaf, MTH, LTP);
				System.out.println("Running Yue Xu's ReliableExactRule Algorithm");
				rere.runAlgorithm(fcl, minCon, minSup, ebRules, eeRules, abRules, aeRules, attCount, multiLevel);
				owner2.assocRulePassback(rere.getAssocRules());
			}
			else if (algorithmCode == 2){
				mmare = new MinMaxAssociationRuleExtractor(ears, attNames, attLeaf, MTH, LTP);
				System.out.println("Running Pasquier's Min-max Algorithm");
				mmare.runAlgorithm(fcl, minCon, minSup, ebRules, eeRules, abRules, aeRules, attCount, multiLevel);
				owner2.assocRulePassback(mmare.getAssocRules());
			}
			else if (algorithmCode == 3){
				mrre = new ModReliableRuleExtractor(ears, attNames, attLeaf, abstractAtts, MTH, LTP);
				System.out.println("Running Modified Yue's ReliableExactRule Algorithm");
				mrre.runAlgorithm(fcl, minCon, minSup, ebRules, eeRules, abRules, aeRules, attCount, multiLevel);
				owner2.assocRulePassback(mrre.getAssocRules());
			}
			else if (algorithmCode == 4){
				mare = new ModMinMaxAssociationRuleExtractor(ears, attNames, attLeaf, abstractAtts, MTH, LTP);
				System.out.println("Running Modified Pasquier's Min-max Algorithm");
				mare.runAlgorithm(fcl, minCon, minSup, ebRules, eeRules, abRules, aeRules, attCount, multiLevel);
				owner2.assocRulePassback(mare.getAssocRules());
			}
			else if (algorithmCode == 5){
				rere2 = new ReliableRuleExtractor2(ears, attNames, attLeaf, MTH, LTP);
				System.out.println("Running Yue Xu's ReliableExactRule - 2 Algorithm");
				rere2.runAlgorithm(fcl, minCon, minSup, ebRules, eeRules, abRules, aeRules, attCount, multiLevel);
				owner2.assocRulePassback(rere2.getAssocRules());
			}
			else if (algorithmCode == 6){
				mmared = new MinMaxAssociationRuleExtractorDiv(ears, attNames, attLeaf, MTH, LTP);
				System.out.println("Running Pasquier's Min-max Algorithm - Diversity version");
				mmared.runAlgorithm(fcl, minCon, minSup, ebRules, eeRules, abRules, aeRules, attCount, multiLevel);
				owner2.assocRulePassback(mmared.getAssocRules());
			}
			else if (algorithmCode == 7){
				mmarec = new MinMaxAssociationRuleExtractorCov(ears, attNames, attLeaf, MTH, LTP);
				System.out.println("Running Pasquier's Min-max Algorithm - Coverage version");
				mmarec.runAlgorithm(fcl, minCon, minSup, ebRules, eeRules, abRules, aeRules, attCount, multiLevel);
				owner2.assocRulePassback(mmarec.getAssocRules());
			}
			else if (algorithmCode == 8){
				mared = new ModMinMaxAssociationRuleExtractorDiv(ears, attNames, attLeaf, abstractAtts, MTH, LTP);
				System.out.println("Running Modified Pasquier's Min-max Algorithm - Diversity Version");
				mared.runAlgorithm(fcl, minCon, minSup, ebRules, eeRules, abRules, aeRules, attCount, multiLevel);
				owner2.assocRulePassback(mared.getAssocRules());
			}
			else if (algorithmCode == 9){
				marec = new ModMinMaxAssociationRuleExtractorCov(ears, attNames, attLeaf, abstractAtts, MTH, LTP);
				System.out.println("Running Modified Pasquier's Min-max Algorithm - Coverage Version");
				marec.runAlgorithm(fcl, minCon, minSup, ebRules, eeRules, abRules, aeRules, attCount, multiLevel);
				owner2.assocRulePassback(marec.getAssocRules());
			}
			else if (algorithmCode == 10){
				rered = new ReliableRuleExtractorDiv(ears, attNames, attLeaf, MTH, LTP);
				System.out.println("Running Yue Xu's ReliableExactRule Algorithm - Diversity version");
				rered.runAlgorithm(fcl, minCon, minSup, ebRules, eeRules, abRules, aeRules, attCount, multiLevel);
				owner2.assocRulePassback(rered.getAssocRules());
			}
			else if (algorithmCode == 11){
				rerec = new ReliableRuleExtractorCov(ears, attNames, attLeaf, MTH, LTP);
				System.out.println("Running Yue Xu's ReliableExactRule Algorithm - Coverage version");
				rerec.runAlgorithm(fcl, minCon, minSup, ebRules, eeRules, abRules, aeRules, attCount, multiLevel);
				owner2.assocRulePassback(rerec.getAssocRules());
			}
			else if (algorithmCode == 12){
				mrred = new ModReliableRuleExtractorDiv(ears, attNames, attLeaf, abstractAtts, MTH, LTP);
				System.out.println("Running Modified Yue's ReliableExactRule Algorithm - Diversity version");
				mrred.runAlgorithm(fcl, minCon, minSup, ebRules, eeRules, abRules, aeRules, attCount, multiLevel);
				owner2.assocRulePassback(mrred.getAssocRules());
			}
			else if (algorithmCode == 13){
				mrrec = new ModReliableRuleExtractorCov(ears, attNames, attLeaf, abstractAtts, MTH, LTP);
				System.out.println("Running Modified Yue's ReliableExactRule Algorithm - Coverage version");
				mrrec.runAlgorithm(fcl, minCon, minSup, ebRules, eeRules, abRules, aeRules, attCount, multiLevel);
				owner2.assocRulePassback(mrrec.getAssocRules());
			}
			else if (algorithmCode == 14){
				ndre = new NonDerivableRuleExtractor(ears, attNames, attLeaf, MTH, LTP);
				System.out.println("Running Modified Yue's ReliableExactRule Algorithm - Coverage version");
				ndre.runAlgorithm(fl, minCon, minSup, ebRules, eeRules, abRules, aeRules, attCount, multiLevel);
				owner2.assocRulePassback(ndre.getAssocRules());
			}
			endTime = System.currentTimeMillis();
			System.out.println("Finished extracting association rules. Total extraction time: " + (endTime - startTime) + " ms");
			owner2.timingPassbackAR(endTime - startTime);
		}
		catch (OutOfMemoryError oom){
			System.out.println("Out of memory. Close application and restart.");
		}
		catch (Exception e){
			System.out.println(e);
		}
	}

	/**
	 * messageBox method.
	 * Method used to allow all of the algorithms implemented
	 * to generate the frequent closed itemsets to store
	 * messages to pass back to the user.
	 */
	final public void messageBox(String message){
		stat = message;
		updateGui();
	}

	/**
	 * updateGui method.
	 * Method used to pass messages back to the system's main
	 * GUI to inform the user about the progress of the generation
	 * of frequent closed itemsets.
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