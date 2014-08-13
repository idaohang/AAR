/**
 * BuildFrequentClosedItemsets class.
 * 
 * Start Date: 02 January 2007
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

import Processing.CLOSEAlgorithm;
import Processing.ModCLOSEAlgorithm;
import Processing.ClosureGenAlgorithm;

import Data.FItemsetList;

public class BuildFrequentClosedItemsets{

	private ARMMainGui owner1;

	private ARMMainNoGui owner2;

	private BuildFrequentClosedItemsets bfci;

	private CLOSEAlgorithm closea;
	
	private ModCLOSEAlgorithm mclosea;

	private ClosureGenAlgorithm closeg;

	private FItemsetList fl;

	private long startTime, endTime;

	private float minCon, minSup;

	private String stat;
	
	private Object[] attNames, attLeaf;

	/**
	 * BuildFrequentClosedItemsets method.
	 * Constructor.
	 * Method used to initialise this class which oversees the
	 * generation of the frequent closed itemsets by one of the
	 * implemented algorithms chosen/specified by the user.
	 */
	public BuildFrequentClosedItemsets(Object[] names, Object[] leaves){
		attNames = names;
		attLeaf = leaves;
	}

	/**
	 * clearData method.
	 * Method used to destroy and clear the variables that hold the
	 * data and references that were used to perform the construction
	 * of the frequent closed itemsets. This is to free up memory.
	 */
	final public void clearData(){
		if (closeg != null){
			closeg.clearData();
			closeg = null;
		}
		if (closea != null){
			closea.clearData();
			closea = null;
		}
		System.gc();
	}

	/**
	 * build method.
	 * Method used to oversee the generation of the frequent closed
	 * itemsets by selecting and overseeing the execution of the
	 * algorithm chosen/specified by the user. The algorithm does
	 * the actual work, this method passes the work to it and gathers
	 * the results when it finishes. This approach allows multiple
	 * algorithms to be implemented and made available to the user to
	 * use and still be managed from one point. This process is run in
	 * its own thread allowing the GUI to stay responsive and report
	 * back to the user during the generation process.
	 */
	final public void build(ARMMainGui main, FItemsetList flist, float mc, float ms, int ac){
		final int algorithmCode = ac;
		owner1 = main;
		bfci = this;
		fl = flist;
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
					stat = "Preparing to build frequent closed itemsets";
					updateGui();
					startTime = System.currentTimeMillis();
					if (algorithmCode == 1){
						closeg = new ClosureGenAlgorithm(bfci);
						stat = "Running Yue Xu's ClosureGen Algorithm";
						updateGui();
						closeg.runAlgorithm(fl, minCon, minSup);
						owner1.freqClosedPassback(closeg.getFreqClosed());
					}
					else if (algorithmCode == 2){
						closea = new CLOSEAlgorithm(bfci);
						stat = "Running Pasquier's CLOSE+ Algorithm";
						updateGui();
						closea.runAlgorithm(fl, minCon, minSup);
						owner1.freqClosedPassback(closea.getFreqClosed());
					}
					else if (algorithmCode == 3){
						mclosea = new ModCLOSEAlgorithm(bfci, attNames, attLeaf);
						stat = "Running Modified CLOSE+ Algorithm";
						updateGui();
						mclosea.runAlgorithm(fl, minCon, minSup);
						owner1.freqClosedPassback(mclosea.getFreqClosed());
					}
					endTime = System.currentTimeMillis();
					stat = "Finished generating frequent closed itemsets. Total generation time: " + (endTime - startTime) + " ms";
					updateGui();
					owner1.timingPassbackBFC(endTime - startTime);
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
	 * Command line version.
	 */
	final public void build(ARMMainNoGui main, FItemsetList flist, float mc, float ms, int ac, int x){
		final int algorithmCode = ac;
		owner2 = main;
		bfci = this;
		fl = flist;
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
			System.out.println("Preparing to build frequent closed itemsets");
			startTime = System.currentTimeMillis();
			if (algorithmCode == 1){
				closeg = new ClosureGenAlgorithm(bfci);
				System.out.println("Running Yue Xu's ClosureGen Algorithm");
				closeg.runAlgorithm(fl, minCon, minSup);
				owner2.freqClosedPassback(closeg.getFreqClosed());
			}
			else if (algorithmCode == 2){
				closea = new CLOSEAlgorithm(bfci);
				System.out.println("Running Pasquier's CLOSE+ Algorithm");
				closea.runAlgorithm(fl, minCon, minSup);
				owner2.freqClosedPassback(closea.getFreqClosed());
			}
			else if (algorithmCode == 3){
				mclosea = new ModCLOSEAlgorithm(bfci, attNames, attLeaf);
				System.out.println("Running Modified CLOSE+ Algorithm");
				mclosea.runAlgorithm(fl, minCon, minSup);
				owner2.freqClosedPassback(mclosea.getFreqClosed());
			}
			endTime = System.currentTimeMillis();
			System.out.println("Finished generating frequent closed itemsets. Total generation time: " + (endTime - startTime) + " ms");
			owner2.timingPassbackBFC(endTime - startTime);
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