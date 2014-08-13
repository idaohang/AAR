/**
 * BuildFrequentItemsets class.
 * 
 * Start Date: 11 December 2006
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

import Processing.Operations;

import Processing.AprioriAlgorithm;
import Processing.CrossLevelAlgorithm;
import Processing.ModAprioriAlgorithm;
import Processing.Mod2AprioriAlgorithm;
import Processing.ModCrossLevelAlgorithm;
import Processing.ModWeightedAprioriAlgorithm;
import Processing.ML_T2L1Algorithm;
import Processing.ML_T2L1CrossAlgorithm;

import Processing.NonDerivableAlgorithm; //For the project students to use to implement Non-Derivable Itemset Mining

import Processing.FPTreeAlgorithm;

import Data.TransRecords;

public class BuildFrequentItemsets{

	private ARMMainGui owner1;

	private ARMMainNoGui owner2;
	
	private Operations ops;

	private BuildFrequentItemsets bfi;

	private AprioriAlgorithm aa;

	private CrossLevelAlgorithm cla;

	private ModCrossLevelAlgorithm mcla;

	private ModAprioriAlgorithm maa;

	private Mod2AprioriAlgorithm maa2;

	private ModWeightedAprioriAlgorithm mwaa;

	private ML_T2L1Algorithm ml;

	private ML_T2L1CrossAlgorithm mlc;

	private FPTreeAlgorithm fpt;

	private NonDerivableAlgorithm nda;

	private TransRecords tr;

	private long startTime, endTime;

	private float minCon, minSup;

	private int attCount;

	private Object[] attNames, attLeaf;

	private String stat;

	/**
	 * BuildFrequentItemsets method.
	 * Constructor.
	 * Method used to initialise this class which oversees the
	 * generation of the frequent itemsets by one of the
	 * implemented algorithms chosen/specified by the user.
	 */
	public BuildFrequentItemsets(){
		ops = new Operations();
	}

	/**
	 * clearData method.
	 * Method used to destroy and clear the variables that hold the
	 * data and references that were used to perform the construction
	 * of the frequent itemsets. This is to free up memory.
	 */
	final public void clearData(){
		if (aa != null){
			aa.clearData();
			aa = null;
		}
		System.gc();
	}

	/**
	 * build method.
	 * Method used to oversee the generation of the frequent itemsets
	 * by selecting and overseeing the execution of the algorithm
	 * chosen/specified by the user. The algorithm does the actual
	 * work, this method passes the work to it and gathers the results
	 * when it finishes. This approach allows multiple algorithms to
	 * be implemented and made available to the user to use and still be
	 * managed from one point. This process is run in its own thread
	 * allowing the GUI to stay responsive and report back to the user
	 * during the generation process.
	 */
	final public void build(ARMMainGui main, TransRecords rec, float mc, float ms, int ac){
		final int algorithmCode = ac;
		bfi = this;
		owner1 = main;
		tr = rec;
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
		Runnable fi = new Runnable(){
			public void run(){
				try{
					stat = "Preparing to build frequent itemsets";
					updateGui();
					startTime = System.currentTimeMillis();
					if (algorithmCode == 1){
						//Apriori...
						aa = new AprioriAlgorithm(bfi);
						stat = "Running Apriori Algorithm";
						updateGui();
						aa.runAlgorithm(tr, minCon, minSup);
						determineLeafAtts();
						owner1.multiLevelStat(false);
						owner1.attCountPassback(attCount);
						owner1.attNamesPassback(attNames);
						owner1.attLeafPassback(attLeaf);
						owner1.candidatePassback(aa.getCandidateLists());
						owner1.frequentPassback(aa.getFrequentItemsets());
					}
					else if (algorithmCode == 2){
						//Modified Apriori...
						maa = new ModAprioriAlgorithm(bfi);
						stat = "Running Modified Apriori Algorithm";
						updateGui();
						maa.runAlgorithm(tr, minCon, minSup);
						determineLeafAtts();
						owner1.multiLevelStat(true);
						owner1.attCountPassback(attCount);
						owner1.attNamesPassback(attNames);
						owner1.attLeafPassback(attLeaf);
						owner1.candidatePassback(maa.getCandidateLists());
						owner1.frequentPassback(maa.getFrequentItemsets());
					}
					else if (algorithmCode == 3){
						//Modified Weighted Apriori...
						mwaa = new ModWeightedAprioriAlgorithm(bfi);
						stat = "Running Modified Weighted Apriori Algorithm";
						updateGui();
						mwaa.runAlgorithm(tr, minCon, minSup);
						determineLeafAtts();
						owner1.multiLevelStat(true);
						owner1.attCountPassback(attCount);
						owner1.attNamesPassback(attNames);
						owner1.attLeafPassback(attLeaf);
						owner1.candidatePassback(mwaa.getCandidateLists());
						owner1.frequentPassback(mwaa.getFrequentItemsets());
					}
					else if (algorithmCode == 4){
						//Cross-Level...
						cla = new CrossLevelAlgorithm(bfi);
						stat = "Running Cross-Level Algorithm";
						updateGui();
						cla.runAlgorithm(tr, minCon, minSup);
						determineLeafAtts();
						owner1.multiLevelStat(true);
						owner1.attCountPassback(attCount);
						owner1.attNamesPassback(attNames);
						owner1.attLeafPassback(attLeaf);
						owner1.candidatePassback(cla.getCandidateLists());
						owner1.frequentPassback(cla.getFrequentItemsets());
					}
					else if (algorithmCode == 5){
						//Modified Cross-Level...
						mcla = new ModCrossLevelAlgorithm(bfi);
						stat = "Running Cross-Level Algorithm";
						updateGui();
						mcla.runAlgorithm(tr, minCon, minSup);
						determineLeafAtts();
						owner1.multiLevelStat(true);
						owner1.attCountPassback(attCount);
						owner1.attNamesPassback(attNames);
						owner1.attLeafPassback(attLeaf);
						owner1.candidatePassback(mcla.getCandidateLists());
						owner1.frequentPassback(mcla.getFrequentItemsets());
					}
					else if (algorithmCode == 6){
						//ML_T2L1...
						ml = new ML_T2L1Algorithm(bfi);
						stat = "Running ML_T2L1 Algorithm";
						updateGui();
						ml.runAlgorithm(tr, minCon, minSup);
						determineLeafAtts();
						owner1.multiLevelStat(true);
						owner1.attCountPassback(attCount);
						owner1.attNamesPassback(attNames);
						owner1.attLeafPassback(attLeaf);
						owner1.candidatePassback(ml.getCandidateLists());
						owner1.frequentPassback(ml.getFrequentItemsets());
					}
					else if (algorithmCode == 7){
						//ML_T2L1 with Cross Level...
						mlc = new ML_T2L1CrossAlgorithm(bfi);
						stat = "Running ML_T2L1 (With Cross Level) Algorithm";
						updateGui();
						mlc.runAlgorithm(tr, minCon, minSup);
						determineLeafAtts();
						owner1.multiLevelStat(true);
						owner1.attCountPassback(attCount);
						owner1.attNamesPassback(attNames);
						owner1.attLeafPassback(attLeaf);
						owner1.candidatePassback(mlc.getCandidateLists());
						owner1.frequentPassback(mlc.getFrequentItemsets());
					}
					else if (algorithmCode == 8){
						//FP-Tree...
						fpt = new FPTreeAlgorithm(bfi);
						stat = "Running FP-Tree Algorithm";
						updateGui();
						fpt.runAlgorithm(tr, minCon, minSup);
						owner1.multiLevelStat(false);
						owner1.candidatePassback(fpt.getCandidateLists());
						owner1.frequentPassback(fpt.getFrequentItemsets());
					}
					else if (algorithmCode == 9){
						//RARM...
					}
					else if (algorithmCode == 10){
						//Modified-2 Apriori...
						maa2 = new Mod2AprioriAlgorithm(bfi);
						stat = "Running Modified-2 Apriori Algorithm";
						updateGui();
						maa2.runAlgorithm(tr, minCon, minSup);
						determineLeafAtts();
						owner1.multiLevelStat(true);
						owner1.attCountPassback(attCount);
						owner1.attNamesPassback(attNames);
						owner1.attLeafPassback(attLeaf);
						owner1.candidatePassback(maa2.getCandidateLists());
						owner1.frequentPassback(maa2.getFrequentItemsets());
					}
					else if (algorithmCode == 11){
						//Non-Derivable Itemset Mining (Calders & Goethals)...
						nda = new NonDerivableAlgorithm(bfi);
						stat = "Running Non-Derivable Itemset Mining Algorithm";
						updateGui();
						nda.runAlgorithm(tr, minCon, minSup);
						determineLeafAtts();
						owner1.multiLevelStat(true);
						owner1.attCountPassback(attCount);
						owner1.attNamesPassback(attNames);
						owner1.attLeafPassback(attLeaf);
						owner1.candidatePassback(nda.getCandidateLists());
						owner1.frequentPassback(nda.getFrequentItemsets());
					}
					endTime = System.currentTimeMillis();
					stat = "Finished generating frequent itemsets. Total generation time: " + (endTime - startTime) + " ms";
					updateGui();
					owner1.timingPassbackBF(endTime - startTime);
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
		Thread build = new Thread(fi);
		build.start();
	}

	final public void build(ARMMainGui main, TransRecords rec, float mc, final ArrayList ms, int ac){
		final int algorithmCode = ac;
		bfi = this;
		owner1 = main;
		tr = rec;
		if (mc > 1){
			minCon = mc / 100;
		}
		else{
			minCon = mc;
		}
		Runnable fi = new Runnable(){
			public void run(){
				try{
					stat = "Preparing to build frequent itemsets";
					updateGui();
					startTime = System.currentTimeMillis();
					if (algorithmCode == 1){
						//Apriori...
					}
					else if (algorithmCode == 2){
						//Modified Apriori...
						maa = new ModAprioriAlgorithm(bfi);
						stat = "Running Modified Apriori Algorithm";
						updateGui();
						maa.runAlgorithm(tr, minCon, ms);
						determineLeafAtts();
						owner1.multiLevelStat(true);
						owner1.attCountPassback(attCount);
						owner1.attNamesPassback(attNames);
						owner1.attLeafPassback(attLeaf);
						owner1.candidatePassback(maa.getCandidateLists());
						owner1.frequentPassback(maa.getFrequentItemsets());
					}
					else if (algorithmCode == 3){
						//Modified Weighted Apriori...
						mwaa = new ModWeightedAprioriAlgorithm(bfi);
						stat = "Running Modified Weighted Apriori Algorithm";
						updateGui();
						mwaa.runAlgorithm(tr, minCon, ms);
						determineLeafAtts();
						owner1.multiLevelStat(true);
						owner1.attCountPassback(attCount);
						owner1.attNamesPassback(attNames);
						owner1.attLeafPassback(attLeaf);
						owner1.candidatePassback(mwaa.getCandidateLists());
						owner1.frequentPassback(mwaa.getFrequentItemsets());
					}
					else if (algorithmCode == 4){
						//Cross-Level...
						cla = new CrossLevelAlgorithm(bfi);
						stat = "Running Cross-Level Algorithm";
						updateGui();
						cla.runAlgorithm(tr, minCon, ms);
						determineLeafAtts();
						owner1.multiLevelStat(true);
						owner1.attCountPassback(attCount);
						owner1.attNamesPassback(attNames);
						owner1.attLeafPassback(attLeaf);
						owner1.candidatePassback(cla.getCandidateLists());
						owner1.frequentPassback(cla.getFrequentItemsets());
					}
					else if (algorithmCode == 5){
						//Modified Cross-Level...
						mcla = new ModCrossLevelAlgorithm(bfi);
						stat = "Running Cross-Level Algorithm";
						updateGui();
						mcla.runAlgorithm(tr, minCon, ms);
						determineLeafAtts();
						owner1.multiLevelStat(true);
						owner1.attCountPassback(attCount);
						owner1.attNamesPassback(attNames);
						owner1.attLeafPassback(attLeaf);
						owner1.candidatePassback(mcla.getCandidateLists());
						owner1.frequentPassback(mcla.getFrequentItemsets());
					}
					else if (algorithmCode == 6){
						//ML_T2L1...
						ml = new ML_T2L1Algorithm(bfi);
						stat = "Running ML_T2L1 Algorithm";
						updateGui();
						ml.runAlgorithm(tr, minCon, ms);
						determineLeafAtts();
						owner1.multiLevelStat(true);
						owner1.attCountPassback(attCount);
						owner1.attNamesPassback(attNames);
						owner1.attLeafPassback(attLeaf);
						owner1.candidatePassback(ml.getCandidateLists());
						owner1.frequentPassback(ml.getFrequentItemsets());
					}
					else if (algorithmCode == 7){
						//ML_T2L1 with Cross Level...
						mlc = new ML_T2L1CrossAlgorithm(bfi);
						stat = "Running ML_T2L1 (With Cross Level) Algorithm";
						updateGui();
						mlc.runAlgorithm(tr, minCon, ms);
						determineLeafAtts();
						owner1.multiLevelStat(true);
						owner1.attCountPassback(attCount);
						owner1.attNamesPassback(attNames);
						owner1.attLeafPassback(attLeaf);
						owner1.candidatePassback(mlc.getCandidateLists());
						owner1.frequentPassback(mlc.getFrequentItemsets());
					}
					else if (algorithmCode == 8){
						//FP-Tree...
					}
					else if (algorithmCode == 9){
						//RARM...
					}
					else if (algorithmCode == 10){
						//Modified-2 Apriori...
						maa2 = new Mod2AprioriAlgorithm(bfi);
						stat = "Running Modified Apriori Algorithm";
						updateGui();
						maa2.runAlgorithm(tr, minCon, ms);
						determineLeafAtts();
						owner1.multiLevelStat(true);
						owner1.attCountPassback(attCount);
						owner1.attNamesPassback(attNames);
						owner1.attLeafPassback(attLeaf);
						owner1.candidatePassback(maa2.getCandidateLists());
						owner1.frequentPassback(maa2.getFrequentItemsets());
					}
					else if (algorithmCode == 11){
						//Non-Derivable Itemset Mining (Calders & Goethals)...
					}
					endTime = System.currentTimeMillis();
					stat = "Finished generating frequent itemsets. Total generation time: " + (endTime - startTime) + " ms";
					updateGui();
					owner1.timingPassbackBF(endTime - startTime);
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
		Thread build = new Thread(fi);
		build.start();
	}

	/**
	 * Command line version
	 */
	final public void build(ARMMainNoGui main, TransRecords rec, float mc, float ms, int ac, int x){
		int algorithmCode = ac;
		bfi = this;
		owner2 = main;
		tr = rec;
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
		try{
			System.out.println("Preparing to build frequent itemsets");
			startTime = System.currentTimeMillis();
			if (algorithmCode == 1){
				//Apriori...
				aa = new AprioriAlgorithm(bfi);
				System.out.println("Running Apriori Algorithm");
				aa.runAlgorithm(tr, minCon, minSup);
				determineLeafAtts();
				owner2.multiLevelStat(false);
				owner2.attCountPassback(attCount);
				owner2.attNamesPassback(attNames);
				owner2.attLeafPassback(attLeaf);
				owner2.candidatePassback(aa.getCandidateLists());
				owner2.frequentPassback(aa.getFrequentItemsets());
			}
			else if (algorithmCode == 2){
				//Modified Apriori...
				maa = new ModAprioriAlgorithm(bfi);
				System.out.println("Running Modified Apriori Algorithm");
				maa.runAlgorithm(tr, minCon, minSup);
				determineLeafAtts();
				owner2.multiLevelStat(true);
				owner2.attCountPassback(attCount);
				owner2.attNamesPassback(attNames);
				owner2.attLeafPassback(attLeaf);
				owner2.candidatePassback(maa.getCandidateLists());
				owner2.frequentPassback(maa.getFrequentItemsets());
			}
			else if (algorithmCode == 3){
				//Modified Weighted Apriori...
				mwaa = new ModWeightedAprioriAlgorithm(bfi);
				System.out.println("Running Modified Weighted Apriori Algorithm");
				mwaa.runAlgorithm(tr, minCon, minSup);
				determineLeafAtts();
				owner2.multiLevelStat(true);
				owner2.attCountPassback(attCount);
				owner2.attNamesPassback(attNames);
				owner2.attLeafPassback(attLeaf);
				owner2.candidatePassback(mwaa.getCandidateLists());
				owner2.frequentPassback(mwaa.getFrequentItemsets());
			}
			else if (algorithmCode == 4){
				//Cross-Level...
				cla = new CrossLevelAlgorithm(bfi);
				System.out.println("Running Cross-Level Algorithm");
				cla.runAlgorithm(tr, minCon, minSup);
				determineLeafAtts();
				owner2.multiLevelStat(true);
				owner2.attCountPassback(attCount);
				owner2.attNamesPassback(attNames);
				owner2.attLeafPassback(attLeaf);
				owner2.candidatePassback(cla.getCandidateLists());
				owner2.frequentPassback(cla.getFrequentItemsets());
			}
			else if (algorithmCode == 5){
				//Modified Cross-Level...
				mcla = new ModCrossLevelAlgorithm(bfi);
				System.out.println("Running Cross-Level Algorithm");
				mcla.runAlgorithm(tr, minCon, minSup);
				determineLeafAtts();
				owner2.multiLevelStat(true);
				owner2.attCountPassback(attCount);
				owner2.attNamesPassback(attNames);
				owner2.attLeafPassback(attLeaf);
				owner2.candidatePassback(mcla.getCandidateLists());
				owner2.frequentPassback(mcla.getFrequentItemsets());
			}
			else if (algorithmCode == 6){
				//ML_T2L1...
				ml = new ML_T2L1Algorithm(bfi);
				System.out.println("Running ML_T2L1 Algorithm");
				ml.runAlgorithm(tr, minCon, minSup);
				determineLeafAtts();
				owner2.multiLevelStat(true);
				owner2.attCountPassback(attCount);
				owner2.attNamesPassback(attNames);
				owner2.attLeafPassback(attLeaf);
				owner2.candidatePassback(ml.getCandidateLists());
				owner2.frequentPassback(ml.getFrequentItemsets());
			}
			else if (algorithmCode == 7){
				//ML_T2L1 with Cross Level...
				mlc = new ML_T2L1CrossAlgorithm(bfi);
				System.out.println("Running ML_T2L1 (With Cross Level) Algorithm");
				mlc.runAlgorithm(tr, minCon, minSup);
				determineLeafAtts();
				owner2.multiLevelStat(true);
				owner2.attCountPassback(attCount);
				owner2.attNamesPassback(attNames);
				owner2.attLeafPassback(attLeaf);
				owner2.candidatePassback(mlc.getCandidateLists());
				owner2.frequentPassback(mlc.getFrequentItemsets());
			}
			else if (algorithmCode == 8){
				//FP-Tree...
				fpt = new FPTreeAlgorithm(bfi);
				System.out.println("Running FP-Tree Algorithm");
				fpt.runAlgorithm(tr, minCon, minSup);
				owner2.multiLevelStat(false);
				owner2.candidatePassback(fpt.getCandidateLists());
				owner2.frequentPassback(fpt.getFrequentItemsets());
			}
			else if (algorithmCode == 9){
				//RARM...
			}
			else if (algorithmCode == 10){
				//Modified-2 Apriori...
				maa2 = new Mod2AprioriAlgorithm(bfi);
				System.out.println("Running Modified-2 Apriori Algorithm");
				maa2.runAlgorithm(tr, minCon, minSup);
				determineLeafAtts();
				owner2.multiLevelStat(true);
				owner2.attCountPassback(attCount);
				owner2.attNamesPassback(attNames);
				owner2.attLeafPassback(attLeaf);
				owner2.candidatePassback(maa2.getCandidateLists());
				owner2.frequentPassback(maa2.getFrequentItemsets());
			}
			else if (algorithmCode == 11){
				//Non-Derivable Itemset Mining (Calders & Goethals)...
				nda = new NonDerivableAlgorithm(bfi);
				System.out.println("Running Non-Derivable Itemset Mining Algorithm");
				nda.runAlgorithm(tr, minCon, minSup);
				determineLeafAtts();
				owner2.multiLevelStat(true);
				owner2.attCountPassback(attCount);
				owner2.attNamesPassback(attNames);
				owner2.attLeafPassback(attLeaf);
				owner2.candidatePassback(nda.getCandidateLists());
				owner2.frequentPassback(nda.getFrequentItemsets());
			}
			endTime = System.currentTimeMillis();
			System.out.println("Finished generating frequent itemsets. Total generation time: " + (endTime - startTime) + " ms");
			owner2.timingPassbackBF(endTime - startTime);
		}
		catch (OutOfMemoryError oom){
			System.out.println("Out of memory. Close application and restart.");
		}
		catch (Exception e){
			System.out.println(e);
		}
	}

	/**
	 * Command line version.
	 */
	final public void build(ARMMainNoGui main, TransRecords rec, float mc, final ArrayList ms, int ac, int x){
		int algorithmCode = ac;
		bfi = this;
		owner2 = main;
		tr = rec;
		if (mc > 1){
			minCon = mc / 100;
		}
		else{
			minCon = mc;
		}
		try{
			System.out.println("Preparing to build frequent itemsets");
			startTime = System.currentTimeMillis();
			if (algorithmCode == 1){
				//Apriori...
			}
			else if (algorithmCode == 2){
				//Modified Apriori...
				maa = new ModAprioriAlgorithm(bfi);
				System.out.println("Running Modified Apriori Algorithm");
				maa.runAlgorithm(tr, minCon, ms);
				determineLeafAtts();
				owner2.multiLevelStat(true);
				owner2.attCountPassback(attCount);
				owner2.attNamesPassback(attNames);
				owner2.attLeafPassback(attLeaf);
				owner2.candidatePassback(maa.getCandidateLists());
				owner2.frequentPassback(maa.getFrequentItemsets());
			}
			else if (algorithmCode == 3){
				//Modified Weighted Apriori...
				mwaa = new ModWeightedAprioriAlgorithm(bfi);
				System.out.println("Running Modified Weighted Apriori Algorithm");
				mwaa.runAlgorithm(tr, minCon, ms);
				determineLeafAtts();
				owner2.multiLevelStat(true);
				owner2.attCountPassback(attCount);
				owner2.attNamesPassback(attNames);
				owner2.attLeafPassback(attLeaf);
				owner2.candidatePassback(mwaa.getCandidateLists());
				owner2.frequentPassback(mwaa.getFrequentItemsets());
			}
			else if (algorithmCode == 4){
				//Cross-Level...
				cla = new CrossLevelAlgorithm(bfi);
				System.out.println("Running Cross-Level Algorithm");
				cla.runAlgorithm(tr, minCon, ms);
				determineLeafAtts();
				owner2.multiLevelStat(true);
				owner2.attCountPassback(attCount);
				owner2.attNamesPassback(attNames);
				owner2.attLeafPassback(attLeaf);
				owner2.candidatePassback(cla.getCandidateLists());
				owner2.frequentPassback(cla.getFrequentItemsets());
			}
			else if (algorithmCode == 5){
				//Modified Cross-Level...
				mcla = new ModCrossLevelAlgorithm(bfi);
				System.out.println("Running Cross-Level Algorithm");
				mcla.runAlgorithm(tr, minCon, ms);
				determineLeafAtts();
				owner2.multiLevelStat(true);
				owner2.attCountPassback(attCount);
				owner2.attNamesPassback(attNames);
				owner2.attLeafPassback(attLeaf);
				owner2.candidatePassback(mcla.getCandidateLists());
				owner2.frequentPassback(mcla.getFrequentItemsets());
			}
			else if (algorithmCode == 6){
				//ML_T2L1...
				ml = new ML_T2L1Algorithm(bfi);
				System.out.println("Running ML_T2L1 Algorithm");
				ml.runAlgorithm(tr, minCon, ms);
				determineLeafAtts();
				owner2.multiLevelStat(true);
				owner2.attCountPassback(attCount);
				owner2.attNamesPassback(attNames);
				owner2.attLeafPassback(attLeaf);
				owner2.candidatePassback(ml.getCandidateLists());
				owner2.frequentPassback(ml.getFrequentItemsets());
			}
			else if (algorithmCode == 7){
				//ML_T2L1 with Cross Level...
				mlc = new ML_T2L1CrossAlgorithm(bfi);
				System.out.println("Running ML_T2L1 (With Cross Level) Algorithm");
				mlc.runAlgorithm(tr, minCon, ms);
				determineLeafAtts();
				owner2.multiLevelStat(true);
				owner2.attCountPassback(attCount);
				owner2.attNamesPassback(attNames);
				owner2.attLeafPassback(attLeaf);
				owner2.candidatePassback(mlc.getCandidateLists());
				owner2.frequentPassback(mlc.getFrequentItemsets());
			}
			else if (algorithmCode == 8){
				//FP-Tree...
			}
			else if (algorithmCode == 9){
				//RARM...
			}
			else if (algorithmCode == 10){
				//Modified-2 Apriori...
				maa2 = new Mod2AprioriAlgorithm(bfi);
				System.out.println("Running Modified Apriori Algorithm");
				maa2.runAlgorithm(tr, minCon, ms);
				determineLeafAtts();
				owner2.multiLevelStat(true);
				owner2.attCountPassback(attCount);
				owner2.attNamesPassback(attNames);
				owner2.attLeafPassback(attLeaf);
				owner2.candidatePassback(maa2.getCandidateLists());
				owner2.frequentPassback(maa2.getFrequentItemsets());
			}
			endTime = System.currentTimeMillis();
			System.out.println("Finished generating frequent itemsets. Total generation time: " + (endTime - startTime) + " ms");
			owner2.timingPassbackBF(endTime - startTime);
		}
		catch (OutOfMemoryError oom){
			System.out.println("Out of memory. Close application and restart.");
		}
		catch (Exception e){
			System.out.println(e);
		}
	}

	final private void determineLeafAtts(){
		boolean uniform = true;
		attCount = 0;
		ArrayList tname, tid, tmatch;
		String[] name;
		int[] id;
		Object[] match;
		attNames = new Object[3];
		attLeaf = new Object[3];
		boolean leaf;
		String[] names = tr.getNames();
		int[] ids = tr.getIDs();
		int n1 = names.length;
		tname = new ArrayList();
		tid = new ArrayList();
		tmatch = new ArrayList();

		int md = (names[0]).split("-").length;
		for (int i = 1; i < n1; i++){
			if ((names[i]).split("-").length == md){
				//Same level as attribute 1...
				attCount++;
			}
			else{
				//Not at same level as attribute 1...
				//Dataset is not uniformly at one level...
				uniform = false;
				break;
			}
		}
		if (attCount > 0 && uniform){
			attCount++;
			name = new String[n1];
			id = new int[n1];
			match = new Object[n1];
			for (int i = 0; i < n1; i++){
				name[i] = names[i];
				id[i] = ids[i];
				match[i] = tr.getAncestors(ids[i]);
			}
		}
		else{
			for (int i = 0; i < n1; i++){
				tname.add(names[i]);
				tid.add(ids[i]);
				tmatch.add(tr.getAncestors(ids[i]));
			}
			n1 = tname.size();
			name = new String[n1];
			id = new int[n1];
			match = new Object[n1];
			for (int i = 0; i < n1; i++){
				name[i] = (String)tname.get(i);
				id[i] = (Integer)tid.get(i);
				match[i] = (int[])tmatch.get(i);
			}
		}
		attNames[0] = name;
		attNames[1] = id;
		attNames[2] = match;
		tname = new ArrayList();
		tid = new ArrayList();
		tmatch = new ArrayList();
		n1 = name.length;
		for (int i = 0; i < n1; i++){
			leaf = true;
			for (int j = 0; j < n1; j++){
				if (i != j){
					if (ops.present((int[])match[j], id[i])){
						//Match found, thus item at i is not a leaf as it is an ancestor of item at j...
						leaf = false;
						i = n1;
						break;
					}
				}
			}
			if (leaf){
				//Item at i is a leaf item...
				tname.add(name[i]);
				tid.add(id[i]);
				tmatch.add(tr.getAncestors(id[i]));
			}
		}
		n1 = tname.size();
		name = new String[n1];
		id = new int[n1];
		match = new Object[n1];
		for (int i = 0; i < n1; i++){
			name[i] = (String)tname.get(i);
			id[i] = (Integer)tid.get(i);
			match[i] = (int[])tmatch.get(i);
		}
		attCount = n1;
		attLeaf[0] = name;
		attLeaf[1] = id;
		attLeaf[2] = match;
	}

	/**
	 * messageBox method.
	 * Method used to allow all of the algorithms implemented
	 * to generate the frequent itemsets to store messages to
	 * pass back to the user.
	 */
	final public void messageBox(String message){
		stat = message;
		updateGui();
	}

	/**
	 * updateGui method.
	 * Method used to pass messages back to the system's main
	 * GUI to inform the user about the progress of the generation
	 * of frequent itemsets.
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