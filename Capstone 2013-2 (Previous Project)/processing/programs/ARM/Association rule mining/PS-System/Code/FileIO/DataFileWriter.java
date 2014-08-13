/**
 * DataFileWriter class.
 * 
 * Start Date: 22 December 2006
 * @author Gavin Shaw
 * @version 1.0
 *
 * Log:
 *		  1.0 Start version of class.
 */
package FileIO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

import java.text.NumberFormat;

import java.util.ArrayList;

import Data.CandidateList;
import Data.FCItemsetList;
import Data.FItemsetList;
import Data.TransRecords;
import Data.AssociationRuleList;

public class DataFileWriter{

	private BufferedWriter bwriter;

	private FileWriter fwriter;

	private File file;

	private String filename;

	private boolean append = false;

	/**
	 * DataFileWriter method.
	 * Constructor.
	 * Method used to initialise this class and setup the
	 * path to the file that will be written to.
	 * PRE: Class is to be initialised and path to the file must be specified.
	 * POST: Class is initialised and path to file is setup.
	 */
	public DataFileWriter(String path){
		filename = path;
	}

	/**
	 * DataFileWriter method.
	 * Constructor.
	 * Default method used to initialise this class.
	 * PRE: Class is to be initialised.
	 * POST: Class is initialised.
	 */
	public DataFileWriter(){
	}

	final public void setPath(String path, boolean a){
		filename = path;
		append = a;
	}

	final public boolean setPathBatch(String path, boolean a){
		filename = path;
		append = a;
		try{
			file = new File(filename);
			fwriter = new FileWriter(file, false);
			bwriter = new BufferedWriter(fwriter);
			file.createNewFile();
			return true;
		}
		catch (IOException ioe){
			return false;
		}
	}

	/**
	 * writeFrequentItemsets method.
	 * Method used to write the frequent itemset list and
	 * candidate list to an external file for later viewing.
	 * PRE: The frequent itemsets are to be written to file
	 *      and the original dataset and list of frequent itemsets
	 *      must be specified.
	 * POST: The frequent itemsets, their frequency and supports have
	 *       been written to the previously specified file.
	 */
	final public boolean writeFrequentItemsets(String sourcePath, TransRecords tr, FItemsetList fl, CandidateList cl, long processTime){
		ArrayList frequentItems, counts, support, t1, t2, t3, out1, out2, out3;
		int itemsetLength = 0, countLength = 0, supportLength = 0;
		try{
			file = new File(filename);
			fwriter = new FileWriter(file, append);
			bwriter = new BufferedWriter(fwriter);
			file.createNewFile();

			out1 = new ArrayList();
			out2 = new ArrayList();
			out3 = new ArrayList();

			frequentItems = fl.getFrequent();
			counts = fl.getCounts();
			support = fl.getSupports();
			//Find the longest candidate, frequent itemset and support value.
			for (int i = 0; i < frequentItems.size(); i++){
				t1 = (ArrayList)frequentItems.get(i);
				t2 = (ArrayList)counts.get(i);
				t3 = (ArrayList)support.get(i);
				for (int j = 0; j < t1.size(); j++){
					out1.add(tr.getNames((int[])t1.get(j)));
					out2.add(String.valueOf((Integer)t2.get(j)));
					out3.add(String.valueOf((Float)t3.get(j)));
					if (((String)out1.get(j)).length() > itemsetLength){
						itemsetLength = ((String)out1.get(j)).length();
					}
					if ((String.valueOf((Integer)t2.get(j))).length() > countLength){
						countLength = ((String)out1.get(j)).length();
					}
					if ((String.valueOf((Float)t3.get(j))).length() > supportLength){
						supportLength = ((String)out1.get(j)).length();
					}
				}
			}

			//File header info...
			bwriter.write("--------------------------------------------------------------------------------------------------------------------------");
			bwriter.newLine();
			bwriter.write("Frequent Itemset Summary");
			bwriter.newLine();
			bwriter.newLine();
			bwriter.write("Data File: " + sourcePath);
			bwriter.newLine();
			bwriter.newLine();

			bwriter.write("Total number of frequent itemsets generated: " + fl.getTotalFrequent());
			bwriter.newLine();
			bwriter.newLine();
			ArrayList fitems = fl.getFrequent();
			for (int i = 0; i < fitems.size(); i++){
				bwriter.write("Number of frequent itemsets of length " + (i + 1) + ": " + ((ArrayList)fitems.get(i)).size());
				bwriter.newLine();
			}
			bwriter.newLine();
			bwriter.newLine();
			bwriter.newLine();
			bwriter.write("Total number of candidate frequent itemsets generated: " + cl.getTotalCandidates());
			bwriter.newLine();
			bwriter.newLine();
			ArrayList citems = cl.getCandidates();
			for (int i = 0; i < citems.size(); i++){
				bwriter.write("Number of candidate frequent itemsets of length " + (i + 1) + ": " + ((ArrayList)citems.get(i)).size());
				bwriter.newLine();
			}
			bwriter.newLine();
			bwriter.newLine();
			bwriter.newLine();
			bwriter.write("Total time taken to generate frequent itemsets: " + processTime + " ms");
			bwriter.newLine();
			bwriter.write("--------------------------------------------------------------------------------------------------------------------------");
			bwriter.newLine();

			//Data table header...
			if (itemsetLength < 17){
				bwriter.write("Frequent Itemset");
				for (int i = 0; i < ((itemsetLength - 16)); i++){
					bwriter.write(" ");
				}
				for (int i = 0; i < 8; i++){
					bwriter.write(" ");
				}
			}
			else{
				if ((itemsetLength - 16) % 2 == 0){
					for (int i = 0; i < ((itemsetLength - 16) / 2); i++){
						bwriter.write(" ");
					}
					bwriter.write("Frequent Itemset");
					for (int i = 0; i < ((itemsetLength - 16) / 2); i++){
						bwriter.write(" ");
					}
				}
				else{
					for (int i = 0; i < ((itemsetLength - 16) / 2); i++){
						bwriter.write(" ");
					}
					bwriter.write("Frequent Itemset");
					for (int i = 0; i < ((itemsetLength - 15) / 2); i++){
						bwriter.write(" ");
					}
				}
				for (int i = 0; i < 16; i++){
					bwriter.write(" ");
				}
			}

			bwriter.write("Occurrence");
			if (countLength <= 6){
				for (int i = 0; i < 9; i++){
					bwriter.write(" ");
				}
			}
			else{
				for (int i = 0; i < (countLength - 6 + 9); i++){
					bwriter.write(" ");
				}
			}
			bwriter.write("Support");
			bwriter.newLine();

			//Data table contents...
			for (int i = 0; i < out1.size(); i++){
				bwriter.write((String)out1.get(i));
				if (itemsetLength < 17){
					for (int j = 0; j < (17 - ((String)out1.get(i)).length() + 11); j++){
						bwriter.write(" ");
					}
				}
				else{
					for (int j = 0; j < (itemsetLength - ((String)out1.get(i)).length() + 20); j++){
						bwriter.write(" ");
					}
				}
				bwriter.write((String)out2.get(i));
				if (countLength < 7){
					for (int j = 0; j < (6 - ((String)out2.get(i)).length() + 11); j++){
						bwriter.write(" ");
					}
				}
				else{
					for (int j = 0; j < (17 - ((String)out2.get(i)).length()); j++){
						bwriter.write(" ");
					}
				}
				bwriter.write((String)out3.get(i));
				bwriter.newLine();
			}
			
			closeFile();
			return true;
		}
		catch (IOException ioe){
			System.out.println(ioe);
			return false;
		}
	}

	/**
	 * writeFrequentClosedItemsets method.
	 * Method used to write the frequent closed itemsets,
	 * their generators and support values to an external
	 * file for later viewing.
	 * PRE: The frequent closed itemsets are to be written to file
	 *      and the original dataset and list of frequent closed
	 *      itemsets must be specified.
	 * POST: The frequent closed itemsets, their frequency and
	 *       supports have been written to the previously
	 *       specified file.
	 */
	final public boolean writeFrequentClosedItemsets(String sourcePath, TransRecords tr, FCItemsetList fcl, long processTime){

		String ci = "Closed Itemsets";
		String g = "Generators";
		String s = "Support";
		ArrayList list = collapseList(fcl, tr.getNames());
		//Data table contents...
		ArrayList items = (ArrayList)list.get(0);
		ArrayList gens = (ArrayList)list.get(1);
		ArrayList supps = (ArrayList)list.get(2);
		int maxItemLength = (Integer)list.get(3);
		int maxGenLength = (Integer)list.get(4);
		int currentLength = 0;
		int[] item;
		ArrayList fitems;
		ArrayList lengthCount = new ArrayList();
		ArrayList uniqueFCI = new ArrayList();

		try{
			file = new File(filename);
			fwriter = new FileWriter(file, append);
			bwriter = new BufferedWriter(fwriter);
			file.createNewFile();

			//File header info...
			bwriter.write("--------------------------------------------------------------------------------------------------------------------------");
			bwriter.newLine();
			bwriter.write("Frequent Closed Itemset Summary");
			bwriter.newLine();
			bwriter.newLine();
			bwriter.write("Data File: " + sourcePath);
			bwriter.newLine();
			bwriter.newLine();

			bwriter.write("Total number of generators generated: " + fcl.getTotalFreqClosed());
			bwriter.newLine();
			bwriter.newLine();
			fitems = fcl.getFreqClosed();
			for (int i = 0; i < fitems.size(); i++){
				item = ((int[])((Object[])fitems.get(i))[0]);
				if (item.length > currentLength){
					for (int j = item.length - 1; j > currentLength; j--){
						lengthCount.add(0);
					}
					lengthCount.add(1);
					currentLength = item.length;
				}
				else{
					lengthCount.set(item.length - 1, ((Integer)lengthCount.get(item.length - 1)) + 1);
				}
			}
			for (int i = 0; i < lengthCount.size(); i++){
				bwriter.write("Number of generators of length " + (i + 1) + ": " + (Integer)lengthCount.get(i));
				bwriter.newLine();
			}
			bwriter.newLine();
			bwriter.newLine();
			bwriter.newLine();
			lengthCount.clear();
			currentLength = 0;
			for (int i = 0; i < fitems.size(); i++){
				if (uniqueFCI.indexOf((int[])((Object[])fitems.get(i))[1]) == -1){
					uniqueFCI.add((int[])((Object[])fitems.get(i))[1]);
					item = ((int[])((Object[])fitems.get(i))[1]);
					if (item.length > currentLength){
						for (int j = item.length - 1; j > currentLength; j--){
							lengthCount.add(0);
						}
						lengthCount.add(1);
						currentLength = item.length;
					}
					else{
						lengthCount.set(item.length - 1, ((Integer)lengthCount.get(item.length - 1)) + 1);
					}
				}
			}
			bwriter.write("Total number of frequent closed itemsets generated: " + uniqueFCI.size());
			bwriter.newLine();
			bwriter.newLine();
			for (int i = 0; i < lengthCount.size(); i++){
				bwriter.write("Number of frequent closed itemsets of length " + (i + 1) + ": " + (Integer)lengthCount.get(i));
				bwriter.newLine();
			}
			bwriter.newLine();
			bwriter.newLine();
			bwriter.newLine();
			bwriter.write("Total time taken to generate frequent closed itemsets: " + processTime + " ms");
			bwriter.newLine();
			bwriter.write("--------------------------------------------------------------------------------------------------------------------------");
			bwriter.newLine();

			//Data table header...
			if (maxItemLength < ci.length()){
				bwriter.write(ci + "    ");
				maxItemLength = ci.length();
			}
			else{
				bwriter.write(ci);
				for (int i = g.length(); i < maxItemLength; i++){
					bwriter.write(" ");
				}
				bwriter.write("    ");
			}
			if (maxGenLength < g.length()){
				bwriter.write(g + "    ");
				maxGenLength = g.length();
			}
			else{
				bwriter.write(g);
				for (int i = g.length(); i < maxGenLength; i++){
					bwriter.write(" ");
				}
				bwriter.write("    ");
			}
			bwriter.write(s);
			bwriter.newLine();

			//Data table contents...
			for (int i = 0; i < items.size(); i++){
				bwriter.write((String)items.get(i));
				for (int j = ((String)items.get(i)).length(); j < maxItemLength; j++){
					bwriter.write(" ");
				}
				bwriter.write("    " + (String)gens.get(i));
				for (int j = ((String)gens.get(i)).length(); j < maxGenLength; j++){
					bwriter.write(" ");
				}
				bwriter.write("    " + (Float)supps.get(i));
				bwriter.newLine();
			}

			closeFile();
			return true;
		}
		catch (IOException ioe){
			return false;
		}
	}

	/**
	 * writeAssociationRules method.
	 * Method used to write the discovered association rules
	 * (both the antecedent and consequent) along with their
	 * supports (and confidence if appropriate) to an external
	 * file for later viewing.
	 * PRE: The association rules are to be written to file
	 *      and the original dataset and list of association rules
	 *      must be specified.
	 * POST: The association rule, their supports and confidences
	 *       have been written to the previously specified file.
	 */
	final public boolean writeAssociationRules(String sourcePath, AssociationRuleList arl, TransRecords tr, int mode, long processTime, String[] nodelist, Object[] names){
		try{
			file = new File(filename);
			fwriter = new FileWriter(file, append);
			bwriter = new BufferedWriter(fwriter);
			file.createNewFile();
			writeARToFile(sourcePath, arl, tr, mode, processTime, nodelist, names, null, null, false);
			closeFile();
			File f2 = new File(sourcePath.replaceAll("data", "names2"));
			if (f2.exists()){
				//There is a second names file which contains the translation between ID numbers & english descriptions...
				String[] t;
				ArrayList ID = new ArrayList();
				ArrayList Node = new ArrayList();
				FileReader freader = new FileReader(f2);
				BufferedReader breader = new BufferedReader(freader);
				String data;
				data = breader.readLine();
				while (data != null){
					t = data.split(":");
					ID.add(t[0].trim());
					Node.add(t[1].trim());
					data = breader.readLine();
				}
				breader.close();
				file = new File(filename.replaceAll(".txt", "-D.txt"));
				fwriter = new FileWriter(file, append);
				bwriter = new BufferedWriter(fwriter);
				file.createNewFile();
				writeARToFile(sourcePath, arl, tr, mode, processTime, nodelist, names, ID, Node, true);
				closeFile();
			}
			return true;
		}
		catch (IOException ioe){
			return false;
		}
	}

	final private boolean writeARToFile(String sourcePath, AssociationRuleList arl, TransRecords tr, int mode, long processTime, String[] nodelist, Object[] names, ArrayList ID, ArrayList Node, boolean desc){
		String ebr = "Exact Basis Rules";
		String ear = "Exact Association Rules";
		String abr = "Approximate Basis Rules";
		String aar = "Approximate Association Rules";
		String sup = "Support";
		String con = "Confidence";
		String coA = "A Coverage";
		String coC = "C Coverage";
		String coR = "R Coverage";
		String chr = "HRD";
		String ld = "LD";
		String tot = "Total Diversity";
		String aDis = "R Distance";
		String bDis = "A Distance";
		String cDis = "C Distance";
		String tDis = "Total Distance";
		String description1 = "", description2 = "";
		String[] descriptionList1, descriptionList2;
		int maxRuleLength = 0;
		ArrayList eb = arl.getExactMinMaxBasis();
		ArrayList ebs = arl.getExactMinMaxBasisSup();
		ArrayList ebcov = arl.getExactMinMaxBasisCov();
		ArrayList ea = arl.getExactAll();
		ArrayList eas = arl.getExactAllSup();
		ArrayList eacov = arl.getExactAllCov();
		ArrayList ab = arl.getApproxMinMaxBasis();
		ArrayList abs = arl.getApproxMinMaxBasisSup();
		ArrayList abc = arl.getApproxMinMaxBasisCon();
		ArrayList abcov = arl.getApproxMinMaxBasisCov();
		ArrayList aa = arl.getApproxAll();
		ArrayList aas = arl.getApproxAllSup();
		ArrayList aac = arl.getApproxAllCon();
		ArrayList aacov = arl.getApproxAllCov();
		ArrayList rules = new ArrayList();
		
		ArrayList div, dis;
		float[] d, d2;
		int count = 0;
		float minCHR, maxCHR, aveCHR, totminCHR = 1, totmaxCHR = 0, totaveCHR = 0;
		float minLD, maxLD, aveLD, totminLD = 1, totmaxLD = 0, totaveLD = 0;
		float mindiv, maxdiv, avediv, totmindiv = 1, totmaxdiv = 0, totavediv = 0;
		float minR, maxR, aveR;
		float minA, maxA, aveA;
		float minC, maxC, aveC;
		float minT, maxT, aveT;
		float minOR = Float.MAX_VALUE, maxOR = 0, aveOR = 0;
		float minOA = Float.MAX_VALUE, maxOA = 0, aveOA = 0;
		float minOC = Float.MAX_VALUE, maxOC = 0, aveOC = 0;
		float minOT = Float.MAX_VALUE, maxOT = 0, aveOT = 0;

		float[] values, values1;
		float vmax, vmin;
		
		Object[] disCounts = arl.getNodeFreqs();
		Object[] results2;
		int[] results;

		NumberFormat formatter = NumberFormat.getNumberInstance();
		formatter.setMaximumFractionDigits(5);
		formatter.setMinimumFractionDigits(5);
		NumberFormat formatter2 = NumberFormat.getNumberInstance();
		formatter2.setMaximumFractionDigits(3);
		formatter2.setMinimumFractionDigits(3);
		
		try{
			//File header info...
			bwriter.write("--------------------------------------------------------------------------------------------------------------------------");
			bwriter.newLine();
			bwriter.write("Association Rule Summary");
			bwriter.newLine();
			bwriter.newLine();
			bwriter.write("Data File: " + sourcePath);
			bwriter.newLine();
			bwriter.newLine();
			if (mode == 1){
				bwriter.write("Rule Extraction Algorithm: ReliableExactRule Association Rules");
			}
			else if (mode == 2){
				bwriter.write("Rule Extraction Algorithm: Min-Max Association Rules");
			}
			else if (mode == 3){
				bwriter.write("Rule Extraction Algorithm: ReliableExactRule with HRR Association Rules");
			}
			else if (mode == 4){
				bwriter.write("Rule Extraction Algorithm: Min-Max Association Rules with HRR");
			}
			else if (mode == 5){
				bwriter.write("Rule Extraction Algorithm: ReliableExactRule - 2 Association Rules");
			}
			else if (mode == 6){
				bwriter.write("Rule Extraction Algorithm: Min-Max Association Rules - Diversity Version");
			}
			else if (mode == 7){
				bwriter.write("Rule Extraction Algorithm: Min-Max Association Rules - Coverage Version");
			}
			else if (mode == 8){
				bwriter.write("Rule Extraction Algorithm: Min-Max Association Rules with HRR - Diversity Version");
			}
			else if (mode == 9){
				bwriter.write("Rule Extraction Algorithm: Min-Max Association Rules with HRR - Coverage Version");
			}
			else if (mode == 10){
				bwriter.write("Rule Extraction Algorithm: ReliableExactRule Association Rules - Diversity Version");
			}
			else if (mode == 11){
				bwriter.write("Rule Extraction Algorithm: ReliableExactRule Association Rules - Coverage Version");
			}
			else if (mode == 12){
				bwriter.write("Rule Extraction Algorithm: ReliableExactRule with HRR Association Rules - Diversity Version");
			}
			else if (mode == 13){
				bwriter.write("Rule Extraction Algorithm: ReliableExactRule with HRR Association Rules - Coverage Version");
			}
			bwriter.newLine();
			bwriter.write("--------------------------------------------------------------------------------------------------------------------------");

			//Rule stat info...
			bwriter.newLine();
			bwriter.write("Rule Counts");
			bwriter.newLine();
			bwriter.newLine();

			if (arl.getExactMinMaxBasisLeafSize() < 10){
				bwriter.write("Total number of exact basis extracted:                  " + arl.getExactMinMaxBasisSize() + "\t\tLeaf: " + arl.getExactMinMaxBasisLeafSize() + "\t\tAbstract: " + arl.getExactMinMaxBasisAbstractSize());
			}
			else{
				bwriter.write("Total number of exact basis extracted:                  " + arl.getExactMinMaxBasisSize() + "\t\tLeaf: " + arl.getExactMinMaxBasisLeafSize() + "\tAbstract: " + arl.getExactMinMaxBasisAbstractSize());
			}
			bwriter.newLine();
			if (arl.getExactLeafSize() < 10){
				bwriter.write("Total number of all/expanded exact extracted:           " + arl.getExactAllSize() + "\t\tLeaf: " + arl.getExactLeafSize() + "\t\tAbstract: " + arl.getExactAbstractSize());
				bwriter.newLine();
			}
			else{
				bwriter.write("Total number of all/expanded exact extracted:           " + arl.getExactAllSize() + "\t\tLeaf: " + arl.getExactLeafSize() + "\tAbstract: " + arl.getExactAbstractSize());
				bwriter.newLine();
			}
			if ((arl.getExactMinMaxBasisLeafSize() + arl.getExactLeafSize()) < 10){
				bwriter.write("Total number of exact association rules extracted:      " + (arl.getExactMinMaxBasisSize() + arl.getExactAllSize()) + "\t\tLeaf: " + (arl.getExactMinMaxBasisLeafSize() + arl.getExactLeafSize()) + "\t\tAbstract: " + (arl.getExactMinMaxBasisAbstractSize() + arl.getExactAbstractSize()));
				bwriter.newLine();
			}
			else{
				bwriter.write("Total number of exact association rules extracted:      " + (arl.getExactMinMaxBasisSize() + arl.getExactAllSize()) + "\t\tLeaf: " + (arl.getExactMinMaxBasisLeafSize() + arl.getExactLeafSize()) + "\tAbstract: " + (arl.getExactMinMaxBasisAbstractSize() + arl.getExactAbstractSize()));
				bwriter.newLine();
			}
			bwriter.newLine();
			if (arl.getApproxMinMaxBasisLeafSize() < 10){
				bwriter.write("Total number of approx basis extracted:                 " + arl.getApproxMinMaxBasisSize() + "\t\tLeaf: " + arl.getApproxMinMaxBasisLeafSize() + "\t\tAbstract: " + arl.getApproxMinMaxBasisAbstractSize());
				bwriter.newLine();
			}
			else{
				bwriter.write("Total number of approx basis extracted:                 " + arl.getApproxMinMaxBasisSize() + "\t\tLeaf: " + arl.getApproxMinMaxBasisLeafSize() + "\tAbstract: " + arl.getApproxMinMaxBasisAbstractSize());
				bwriter.newLine();
			}
			if (arl.getApproxLeafSize() < 10){
				bwriter.write("Total number of all/expanded approx extracted:          " + arl.getApproxAllSize() + "\t\tLeaf: " + arl.getApproxLeafSize() + "\t\tAbstract: " + arl.getApproxAbstractSize());
				bwriter.newLine();
			}
			else{
				bwriter.write("Total number of all/expanded approx extracted:          " + arl.getApproxAllSize() + "\t\tLeaf: " + arl.getApproxLeafSize() + "\tAbstract: " + arl.getApproxAbstractSize());
				bwriter.newLine();
			}
			if ((arl.getApproxMinMaxBasisLeafSize() + arl.getApproxLeafSize()) < 10){
				bwriter.write("Total number of approx association rules extracted:     " + (arl.getApproxMinMaxBasisSize() + arl.getApproxAllSize()) + "\t\tLeaf: " + (arl.getApproxMinMaxBasisLeafSize() + arl.getApproxLeafSize()) + "\t\tAbstract: " + (arl.getApproxMinMaxBasisAbstractSize() + arl.getApproxAbstractSize()));
				bwriter.newLine();
			}
			else{
				bwriter.write("Total number of approx association rules extracted:     " + (arl.getApproxMinMaxBasisSize() + arl.getApproxAllSize()) + "\t\tLeaf: " + (arl.getApproxMinMaxBasisLeafSize() + arl.getApproxLeafSize()) + "\tAbstract: " + (arl.getApproxMinMaxBasisAbstractSize() + arl.getApproxAbstractSize()));
				bwriter.newLine();
			}
			bwriter.write("--------------------------------------------------------------------------------------------------------------------------");
			
			//Support and confidence stat info...
			bwriter.newLine();
			bwriter.write("Support and Confidence Evaluation Summary");
			bwriter.newLine();
			bwriter.newLine();
			
			values = summation(ebs);
			bwriter.write("Support of exact basis rule set:             Min: " + formatter.format(values[2]) + "\tMax: " + formatter.format(values[1]) + "\tAve: " + formatter.format(values[0] / arl.getExactMinMaxBasisSize()));
			bwriter.newLine();
			bwriter.write("Confidence of exact basis rule set:          Min: 1.00000\tMax: 1.00000\tAve: 1.00000");
			bwriter.newLine();
			bwriter.newLine();
			
			values = summation(eas);
			bwriter.write("Support of all/expanded exact rule set:      Min: " + formatter.format(values[2]) + "\tMax: " + formatter.format(values[1]) + "\tAve: " + formatter.format(values[0] / arl.getExactAllSize()));
			bwriter.newLine();
			bwriter.write("Confidence of all/expanded exact rule set:   Min: 1.00000\tMax: 1.00000\tAve: 1.00000");
			bwriter.newLine();
			bwriter.newLine();
			
			values = summation(abs);
			bwriter.write("Support of approx basis rule set:            Min: " + formatter.format(values[2]) + "\tMax: " + formatter.format(values[1]) + "\tAve: " + formatter.format(values[0] / arl.getApproxMinMaxBasisSize()));
			bwriter.newLine();
			values = summation(abc);
			bwriter.write("Confidence of approx basis rule set:         Min: " + formatter.format(values[2]) + "\tMax: " + formatter.format(values[1]) + "\tAve: " + formatter.format(values[0] / arl.getApproxMinMaxBasisSize()));
			bwriter.newLine();
			bwriter.newLine();
			
			values = summation(aas);
			bwriter.write("Support of all/expanded approx rule set:     Min: " + formatter.format(values[2]) + "\tMax: " + formatter.format(values[1]) + "\tAve: " + formatter.format(values[0] / arl.getApproxAllSize()));
			bwriter.newLine();
			values = summation(aac);
			bwriter.write("Confidence of all/expanded approx rule set:  Min: " + formatter.format(values[2]) + "\tMax: " + formatter.format(values[1]) + "\tAve: " + formatter.format(values[0] / arl.getApproxAllSize()));
			bwriter.newLine();
			bwriter.write("--------------------------------------------------------------------------------------------------------------------------");

			//Coverage stat info...
/*			bwriter.newLine();
			bwriter.write("Coverage Evaluation Summary");
			bwriter.newLine();
			bwriter.newLine();

			values = summation(arl.getExactMinMaxBasisCov(), 0);
			bwriter.write("Coverage of exact basis rule set (antecedent):          Total: " + formatter.format((Float)arl.getExactMinMaxBasisCovA()) + "%\tMin: " + formatter.format(values[2]) + "%\tMax: " + formatter.format(values[1]) + "%\tAve: " + formatter.format(values[0] / arl.getExactMinMaxBasisSize()) + "%");
			bwriter.newLine();
			values = summation(arl.getExactMinMaxBasisCov(), 1);
			bwriter.write("Coverage of exact basis rule set (consequent):          Total: " + formatter.format((Float)arl.getExactMinMaxBasisCovC()) + "%\tMin: " + formatter.format(values[2]) + "%\tMax: " + formatter.format(values[1]) + "%\tAve: " + formatter.format(values[0] / arl.getExactMinMaxBasisSize()) + "%");
			bwriter.newLine();
			values = summation(arl.getExactMinMaxBasisCov(), 2);
			bwriter.write("Coverage of exact basis rule set (rule):                Total: " + formatter.format((Float)arl.getExactMinMaxBasisCovR()) + "%\tMin: " + formatter.format(values[2]) + "%\tMax: " + formatter.format(values[1]) + "%\tAve: " + formatter.format(values[0] / arl.getExactMinMaxBasisSize()) + "%\tAbstract: " + formatter.format(arl.getExactAbstractBasisCovR()) + "%");
			bwriter.newLine();
			bwriter.newLine();
			vmax = values[1];
			vmin = values[2];

			values1 = summation(arl.getExactAllCov(), 0);
			bwriter.write("Coverage of all/expanded exact rule set (antecedent):   Total: " + formatter.format((Float)arl.getExactAllCovA()) + "%\tMin: " + formatter.format(values1[2]) + "%\tMax: " + formatter.format(values1[1]) + "%\tAve: " + formatter.format(values1[0] / arl.getExactAllSize()) + "%");
			bwriter.newLine();
			values1 = summation(arl.getExactAllCov(), 1);
			bwriter.write("Coverage of all/expanded exact rule set (consequent):   Total: " + formatter.format((Float)arl.getExactAllCovC()) + "%\tMin: " + formatter.format(values1[2]) + "%\tMax: " + formatter.format(values1[1]) + "%\tAve: " + formatter.format(values1[0] / arl.getExactAllSize()) + "%");
			bwriter.newLine();
			values1 = summation(arl.getExactAllCov(), 2);
			bwriter.write("Coverage of all/expanded exact rule set (rule):         Total: " + formatter.format((Float)arl.getExactAllCovR()) + "%\tMin: " + formatter.format(values1[2]) + "%\tMax: " + formatter.format(values1[1]) + "%\tAve: " + formatter.format(values1[0] / arl.getExactAllSize()) + "%\tAbstract: " + formatter.format(arl.getExactAllAbstractCovR()) + "%");
			bwriter.newLine();
			bwriter.newLine();
			if (values1[1] > vmax){
				vmax = values1[1];
			}
			if (values1[2] < vmin && values1[2] != 0){
				vmin = values1[2];
			}

			bwriter.write("Total coverage of exact rule set(s):                    Total: " + formatter.format((Float)arl.getExactRuleCoverage()) + "%\tMin: " + formatter.format(vmin) + "%\tMax: " + formatter.format(vmax) + "%\tAve: " + formatter.format((values[0] + values1[0]) / (arl.getExactMinMaxBasisSize() + arl.getExactAllSize())) + "%\tAbstract: " + formatter.format((Float)arl.getExactAbstractRuleCoverage()) + "%");
			bwriter.newLine();
			bwriter.newLine();
			
			values = summation(arl.getApproxMinMaxBasisCov(), 0);
			bwriter.write("Coverage of approx basis rule set (antecedent):         Total: " + formatter.format((Float)arl.getApproxMinMaxBasisCovA()) + "%\tMin: " + formatter.format(values[2]) + "%\tMax: " + formatter.format(values[1]) + "%\tAve: " + formatter.format(values[0] / arl.getApproxMinMaxBasisSize()) + "%");
			bwriter.newLine();
			values = summation(arl.getApproxMinMaxBasisCov(), 1);
			bwriter.write("Coverage of approx basis rule set (consequent):         Total: " + formatter.format((Float)arl.getApproxMinMaxBasisCovC()) + "%\tMin: " + formatter.format(values[2]) + "%\tMax: " + formatter.format(values[1]) + "%\tAve: " + formatter.format(values[0] / arl.getApproxMinMaxBasisSize()) + "%");
			bwriter.newLine();
			values = summation(arl.getApproxMinMaxBasisCov(), 2);
			bwriter.write("Coverage of approx basis rule set (rule):               Total: " + formatter.format((Float)arl.getApproxMinMaxBasisCovR()) + "%\tMin: " + formatter.format(values[2]) + "%\tMax: " + formatter.format(values[1]) + "%\tAve: " + formatter.format(values[0] / arl.getApproxMinMaxBasisSize()) + "%\tAbstract: " + formatter.format(arl.getApproxAbstractBasisCovR()) + "%");
			bwriter.newLine();
			bwriter.newLine();
			vmax = values[1];
			vmin = values[2];

			values1 = summation(arl.getApproxAllCov(), 0);
			bwriter.write("Coverage of all/expanded approx rule set (antecedent):  Total: " + formatter.format((Float)arl.getApproxAllCovA()) + "%\tMin: " + formatter.format(values1[2]) + "%\tMax: " + formatter.format(values1[1]) + "%\tAve: " + formatter.format(values1[0] / arl.getApproxAllSize()) + "%");
			bwriter.newLine();
			values1 = summation(arl.getApproxAllCov(), 1);
			bwriter.write("Coverage of all/expanded approx rule set (consequent):  Total: " + formatter.format((Float)arl.getApproxAllCovC()) + "%\tMin: " + formatter.format(values1[2]) + "%\tMax: " + formatter.format(values1[1]) + "%\tAve: " + formatter.format(values1[0] / arl.getApproxAllSize()) + "%");
			bwriter.newLine();
			values1 = summation(arl.getApproxAllCov(), 2);
			bwriter.write("Coverage of all/expanded approx rule set (rule):        Total: " + formatter.format((Float)arl.getApproxAllCovR()) + "%\tMin: " + formatter.format(values1[2]) + "%\tMax: " + formatter.format(values1[1]) + "%\tAve: " + formatter.format(values1[0] / arl.getApproxAllSize()) + "%\tAbstract: " + formatter.format(arl.getApproxAbstractCovR()) + "%");
			bwriter.newLine();
			bwriter.newLine();
			if (values1[1] > vmax){
				vmax = values1[1];
			}
			if (values1[2] < vmin && values1[2] != 0){
				vmin = values1[2];
			}

			bwriter.write("Total coverage of approx rule set(s):                   Total: " + formatter.format((Float)arl.getApproxRuleCoverage()) + "%\tMin: " + formatter.format(vmin) + "%\tMax: " + formatter.format(vmax) + "%\tAve: " + formatter.format((values[0] + values1[0]) / (arl.getApproxMinMaxBasisSize() + arl.getApproxAllSize())) + "%\tAbstract: " + formatter.format((Float)arl.getApproxAbstractRuleCoverage()) + "%");
			bwriter.newLine();
			bwriter.newLine();
			bwriter.write("Average scores are based on the summation of the approriate coverage of each rule divided by the number of rules in that category.");
			bwriter.newLine();
			bwriter.write("Not the total coverage divided by the number of rules.");
			bwriter.newLine();
			bwriter.write("--------------------------------------------------------------------------------------------------------------------------\n");
			bwriter.newLine();

			//Diversity stat info...
			bwriter.write("Diversity Evaluation Summary");
			bwriter.newLine();
			bwriter.newLine();

			div = arl.getExactMinMaxBasisDiv();
			if (div.size() > 0){
				minCHR = Float.MAX_VALUE;
				maxCHR = Float.MIN_VALUE;
				aveCHR = 0;
				minLD = Float.MAX_VALUE;
				maxLD = Float.MIN_VALUE;
				aveLD = 0;
				mindiv = Float.MAX_VALUE;
				maxdiv = Float.MIN_VALUE;
				avediv = 0;
				for (int i = 0; i < div.size(); i++){
					d = (float[])div.get(i);
					if (d[1] > maxCHR){
						maxCHR = d[1];
					}
					if (d[1] < minCHR){
						minCHR = d[1];
					}
					aveCHR = aveCHR + d[1];
					if (d[0] > maxLD){
						maxLD = d[0];
					}
					if (d[0] < minLD){
						minLD = d[0];
					}
					aveLD = aveLD + d[0];
					if (d[2] > maxdiv){
						maxdiv = d[2];
					}
					if (d[2] < mindiv){
						mindiv = d[2];
					}
					avediv = avediv + d[2];
				}
				if (maxCHR > totmaxCHR){
					totmaxCHR = maxCHR;
				}
				if (minCHR < totminCHR){
					totminCHR = minCHR;
				}
				if (maxLD > totmaxLD){
					totmaxLD = maxLD;
				}
				if (minLD < totminLD){
					totminLD = minLD;
				}
				if (maxdiv > totmaxdiv){
					totmaxdiv = maxdiv;
				}
				if (mindiv < totmindiv){
					totmindiv = mindiv;
				}
				totaveCHR = totaveCHR + aveCHR;
				totaveLD = totaveLD + aveLD;
				totavediv = totavediv + avediv;
				count = count + div.size();
				aveCHR = aveCHR / (float)div.size();
				aveLD = aveLD / (float)div.size();
				avediv = avediv / (float)div.size();
			}
			else{
				minCHR = 0;
				maxCHR = 0;
				aveCHR = 0;
				minLD = 0;
				maxLD = 0;
				aveLD = 0;
				mindiv = 0;
				maxdiv = 0;
				avediv = 0;
			}
			bwriter.write("Exact Basis Evaluation Summary");
			bwriter.newLine();
			bwriter.write("HRD (Hierarchical Relationship Distance):     Min: " + formatter.format(minCHR) + "\tMax: " + formatter.format(maxCHR) + "\tAve: " + formatter.format(aveCHR));
			bwriter.newLine();
			bwriter.write("LD  (Level Distance/Diversity):               Min: " + formatter.format(minLD) + "\tMax: " + formatter.format(maxLD) + "\tAve: " + formatter.format(aveLD));
			bwriter.newLine();
			bwriter.write("Total Diversity:                              Min: " + formatter.format(mindiv) + "\tMax: " + formatter.format(maxdiv) + "\tAve: " + formatter.format(avediv));
			bwriter.newLine();
			bwriter.newLine();

			div = arl.getExactAllDiv();
			if (div.size() > 0){
				minCHR = Float.MAX_VALUE;
				maxCHR = Float.MIN_VALUE;
				aveCHR = 0;
				minLD = Float.MAX_VALUE;
				maxLD = Float.MIN_VALUE;
				aveLD = 0;
				mindiv = Float.MAX_VALUE;
				maxdiv = Float.MIN_VALUE;
				avediv = 0;
				for (int i = 0; i < div.size(); i++){
					d = (float[])div.get(i);
					if (d[1] > maxCHR){
						maxCHR = d[1];
					}
					if (d[1] < minCHR){
						minCHR = d[1];
					}
					aveCHR = aveCHR + d[1];
					if (d[0] > maxLD){
						maxLD = d[0];
					}
					if (d[0] < minLD){
						minLD = d[0];
					}
					aveLD = aveLD + d[0];
					if (d[2] > maxdiv){
						maxdiv = d[2];
					}
					if (d[2] < mindiv){
						mindiv = d[2];
					}
					avediv = avediv + d[2];
				}
				if (maxCHR > totmaxCHR){
					totmaxCHR = maxCHR;
				}
				if (minCHR < totminCHR){
					totminCHR = minCHR;
				}
				if (maxLD > totmaxLD){
					totmaxLD = maxLD;
				}
				if (minLD < totminLD){
					totminLD = minLD;
				}
				if (maxdiv > totmaxdiv){
					totmaxdiv = maxdiv;
				}
				if (mindiv < totmindiv){
					totmindiv = mindiv;
				}
				totaveCHR = totaveCHR + aveCHR;
				totaveLD = totaveLD + aveLD;
				totavediv = totavediv + avediv;
				count = count + div.size();
				aveCHR = aveCHR / (float)div.size();
				aveLD = aveLD / (float)div.size();
				avediv = avediv / (float)div.size();
			}
			else{
				minCHR = 0;
				maxCHR = 0;
				aveCHR = 0;
				minLD = 0;
				maxLD = 0;
				aveLD = 0;
				mindiv = 0;
				maxdiv = 0;
				avediv = 0;
			}
			bwriter.write("Exact Expanded Evaluation Summary");
			bwriter.newLine();
			bwriter.write("HRD (Hierarchical Relationship Distance):     Min: " + formatter.format(minCHR) + "\tMax: " + formatter.format(maxCHR) + "\tAve: " + formatter.format(aveCHR));
			bwriter.newLine();
			bwriter.write("LD  (Level Distance/Diversity):               Min: " + formatter.format(minLD) + "\tMax: " + formatter.format(maxLD) + "\tAve: " + formatter.format(aveLD));
			bwriter.newLine();
			bwriter.write("Total Diversity:                              Min: " + formatter.format(mindiv) + "\tMax: " + formatter.format(maxdiv) + "\tAve: " + formatter.format(avediv));
			bwriter.newLine();
			bwriter.newLine();

			div = arl.getApproxMinMaxBasisDiv();
			if (div.size() > 0){
				minCHR = Float.MAX_VALUE;
				maxCHR = Float.MIN_VALUE;
				aveCHR = 0;
				minLD = Float.MAX_VALUE;
				maxLD = Float.MIN_VALUE;
				aveLD = 0;
				mindiv = Float.MAX_VALUE;
				maxdiv = Float.MIN_VALUE;
				avediv = 0;
				for (int i = 0; i < div.size(); i++){
					d = (float[])div.get(i);
					if (d[1] > maxCHR){
						maxCHR = d[1];
					}
					if (d[1] < minCHR){
						minCHR = d[1];
					}
					aveCHR = aveCHR + d[1];
					if (d[0] > maxLD){
						maxLD = d[0];
					}
					if (d[0] < minLD){
						minLD = d[0];
					}
					aveLD = aveLD + d[0];
					if (d[2] > maxdiv){
						maxdiv = d[2];
					}
					if (d[2] < mindiv){
						mindiv = d[2];
					}
					avediv = avediv + d[2];
				}
				if (maxCHR > totmaxCHR){
					totmaxCHR = maxCHR;
				}
				if (minCHR < totminCHR){
					totminCHR = minCHR;
				}
				if (maxLD > totmaxLD){
					totmaxLD = maxLD;
				}
				if (minLD < totminLD){
					totminLD = minLD;
				}
				if (maxdiv > totmaxdiv){
					totmaxdiv = maxdiv;
				}
				if (mindiv < totmindiv){
					totmindiv = mindiv;
				}
				totaveCHR = totaveCHR + aveCHR;
				totaveLD = totaveLD + aveLD;
				totavediv = totavediv + avediv;
				count = count + div.size();
				aveCHR = aveCHR / (float)div.size();
				aveLD = aveLD / (float)div.size();
				avediv = avediv / (float)div.size();
			}
			else{
				minCHR = 0;
				maxCHR = 0;
				aveCHR = 0;
				minLD = 0;
				maxLD = 0;
				aveLD = 0;
				mindiv = 0;
				maxdiv = 0;
				avediv = 0;
			}
			bwriter.write("Approximate Basis Evaluation Summary");
			bwriter.newLine();
			bwriter.write("HRD (Hierarchical Relationship Distance):     Min: " + formatter.format(minCHR) + "\tMax: " + formatter.format(maxCHR) + "\tAve: " + formatter.format(aveCHR));
			bwriter.newLine();
			bwriter.write("LD  (Level Distance/Diversity):               Min: " + formatter.format(minLD) + "\tMax: " + formatter.format(maxLD) + "\tAve: " + formatter.format(aveLD));
			bwriter.newLine();
			bwriter.write("Total Diversity:                              Min: " + formatter.format(mindiv) + "\tMax: " + formatter.format(maxdiv) + "\tAve: " + formatter.format(avediv));
			bwriter.newLine();
			bwriter.newLine();

			div = arl.getApproxAllDiv();
			if (div.size() > 0){
				minCHR = Float.MAX_VALUE;
				maxCHR = Float.MIN_VALUE;
				aveCHR = 0;
				minLD = Float.MAX_VALUE;
				maxLD = Float.MIN_VALUE;
				aveLD = 0;
				mindiv = Float.MAX_VALUE;
				maxdiv = Float.MIN_VALUE;
				avediv = 0;
				for (int i = 0; i < div.size(); i++){
					d = (float[])div.get(i);
					if (d[1] > maxCHR){
						maxCHR = d[1];
					}
					if (d[1] < minCHR){
						minCHR = d[1];
					}
					aveCHR = aveCHR + d[1];
					if (d[0] > maxLD){
						maxLD = d[0];
					}
					if (d[0] < minLD){
						minLD = d[0];
					}
					aveLD = aveLD + d[0];
					if (d[2] > maxdiv){
						maxdiv = d[2];
					}
					if (d[2] < mindiv){
						mindiv = d[2];
					}
					avediv = avediv + d[2];
				}
				if (maxCHR > totmaxCHR){
					totmaxCHR = maxCHR;
				}
				if (minCHR < totminCHR){
					totminCHR = minCHR;
				}
				if (maxLD > totmaxLD){
					totmaxLD = maxLD;
				}
				if (minLD < totminLD){
					totminLD = minLD;
				}
				if (maxdiv > totmaxdiv){
					totmaxdiv = maxdiv;
				}
				if (mindiv < totmindiv){
					totmindiv = mindiv;
				}
				totaveCHR = totaveCHR + aveCHR;
				totaveLD = totaveLD + aveLD;
				totavediv = totavediv + avediv;
				count = count + div.size();
				aveCHR = aveCHR / (float)div.size();
				aveLD = aveLD / (float)div.size();
				avediv = avediv / (float)div.size();
			}
			else{
				minCHR = 0;
				maxCHR = 0;
				aveCHR = 0;
				minLD = 0;
				maxLD = 0;
				aveLD = 0;
				mindiv = 0;
				maxdiv = 0;
				avediv = 0;
			}
			bwriter.write("Approximate Expanded Evaluation Summary");
			bwriter.newLine();
			bwriter.write("HRD (Hierarchical Relationship Distance):     Min: " + formatter.format(minCHR) + "\tMax: " + formatter.format(maxCHR) + "\tAve: " + formatter.format(aveCHR));
			bwriter.newLine();
			bwriter.write("LD  (Level Distance/Diversity):               Min: " + formatter.format(minLD) + "\tMax: " + formatter.format(maxLD) + "\tAve: " + formatter.format(aveLD));
			bwriter.newLine();
			bwriter.write("Total Diversity:                              Min: " + formatter.format(mindiv) + "\tMax: " + formatter.format(maxdiv) + "\tAve: " + formatter.format(avediv));
			bwriter.newLine();
			bwriter.newLine();

			bwriter.write("Overall Evaluation Summary");
			bwriter.newLine();
			bwriter.write("HRD (Hierarchical Relationship Distance):     Min: " + formatter.format(totminCHR) + "\tMax: " + formatter.format(totmaxCHR) + "\tAve: " + formatter.format(totaveCHR / (float)count));
			bwriter.newLine();
			bwriter.write("LD  (Level Distance/Diversity):               Min: " + formatter.format(totminLD) + "\tMax: " + formatter.format(totmaxLD) + "\tAve: " + formatter.format(totaveLD / (float)count));
			bwriter.newLine();
			bwriter.write("Total Diversity:                              Min: " + formatter.format(totmindiv) + "\tMax: " + formatter.format(totmaxdiv) + "\tAve: " + formatter.format(totavediv / (float)count));
			bwriter.newLine();
			bwriter.write("MTH: " + tr.getMTH() + "\t\tLTP: " + tr.getLTP());
			bwriter.newLine();
			bwriter.write("--------------------------------------------------------------------------------------------------------------------------");
*/			bwriter.newLine();
			
			//Stat Dist stat info...
			bwriter.write("Statistical Distribution Evaluation Summary");
			bwriter.newLine();
			bwriter.newLine();

			results = determineUnique(disCounts, 2);
			results2 = determineMinMaxAve(disCounts, 2);
			bwriter.write("Exact Basis Evaluation Summary");
			bwriter.newLine();
			bwriter.write("Total No. of rules:        " + arl.getExactMinMaxBasisSize());
			bwriter.newLine();
			bwriter.write("Total No. of unique nodes: " + results[0] + "\tAntecedent: " + results[1] + "\tConsequent: " + results[2]);
			bwriter.newLine();
			bwriter.write("Node Frequency Summary:    Min: " + (Integer)results2[0] + "\tMax: " + (Integer)results2[1] + "\tAve: "+ formatter2.format((Float)results2[2]));
			bwriter.newLine();
			bwriter.newLine();

			results = determineUnique(disCounts, 3);
			results2 = determineMinMaxAve(disCounts, 3);
			bwriter.write("Exact Expanded Evaluation Summary");
			bwriter.newLine();
			bwriter.write("Total No. of rules:        " + arl.getExactAllSize());
			bwriter.newLine();
			bwriter.write("Total No. of unique nodes: " + results[0] + "\tAntecedent: " + results[1] + "\tConsequent: " + results[2]);
			bwriter.newLine();
			bwriter.write("Node Frequency Summary:    Min: " + (Integer)results2[0] + "\tMax: " + (Integer)results2[1] + "\tAve: "+ formatter2.format((Float)results2[2]));
			bwriter.newLine();
			bwriter.newLine();

			results = determineUnique(disCounts, 4);
			results2 = determineMinMaxAve(disCounts, 4);
			bwriter.write("Approximate Basis Evaluation Summary");
			bwriter.newLine();
			bwriter.write("Total No. of rules:        " + arl.getApproxMinMaxBasisSize());
			bwriter.newLine();
			bwriter.write("Total No. of unique nodes: " + results[0] + "\tAntecedent: " + results[1] + "\tConsequent: " + results[2]);
			bwriter.newLine();
			bwriter.write("Node Frequency Summary:    Min: " + (Integer)results2[0] + "\tMax: " + (Integer)results2[1] + "\tAve: "+ formatter2.format((Float)results2[2]));
			bwriter.newLine();
			bwriter.newLine();

			results = determineUnique(disCounts, 5);
			results2 = determineMinMaxAve(disCounts, 5);
			bwriter.write("Approximate Expanded Evaluation Summary");
			bwriter.newLine();
			bwriter.write("Total No. of rules:        " + arl.getApproxAllSize());
			bwriter.newLine();
			bwriter.write("Total No. of unique nodes: " + results[0] + "\tAntecedent: " + results[1] + "\tConsequent: " + results[2]);
			bwriter.newLine();
			bwriter.write("Node Frequency Summary:    Min: " + (Integer)results2[0] + "\tMax: " + (Integer)results2[1] + "\tAve: "+ formatter2.format((Float)results2[2]));
			bwriter.newLine();
			bwriter.newLine();
			
			results = determineUnique(disCounts, 1);
			results2 = determineMinMaxAve(disCounts, 1);
			bwriter.write("Overall Evaluation Summary");
			bwriter.newLine();
			bwriter.write("Total No. of rules:        " + (arl.getExactMinMaxBasisSize() + arl.getExactAllSize() + arl.getApproxMinMaxBasisSize() + arl.getApproxAllSize()));
			bwriter.newLine();
			bwriter.write("Total No. of unique nodes: " + results[0] + "\tAntecedent: " + results[1] + "\tConsequent: " + results[2]);
			bwriter.newLine();
			bwriter.write("Node Frequency Summary:    Min: " + (Integer)results2[0] + "\tMax: " + (Integer)results2[1] + "\tAve: "+ formatter2.format((Float)results2[2]));
			bwriter.newLine();
			bwriter.write("--------------------------------------------------------------------------------------------------------------------------");
			bwriter.newLine();

			//Distance Stat Info...
/*			count = 0;
			bwriter.write("Distance Evaluation Summary");
			bwriter.newLine();
			bwriter.newLine();

			minR = Float.MAX_VALUE;
			maxR = 0;
			aveR = 0;
			minA = Float.MAX_VALUE;
			maxA = 0;
			aveA = 0;
			minC = Float.MAX_VALUE;
			maxC = 0;
			aveC = 0;
			minT = Float.MAX_VALUE;
			maxT = 0;
			aveT = 0;
			dis = arl.getExactMinMaxBasisDis();
			count = count + dis.size();
			for (int i = 0; i < dis.size(); i++){
				d = (float[])dis.get(i);
				if (d[0] > maxR){
					maxR = d[0];
				}
				if (d[0] < minR){
					minR = d[0];
				}
				if (d[0] > maxOR){
					maxOR = d[0];
				}
				if (d[0] < minOR){
					minOR = d[0];
				}
				aveR = aveR + d[0];
				aveOR = aveOR + d[0];
				if (d[1] > maxA){
					maxA = d[1];
				}
				if (d[1] < minA){
					minA = d[1];
				}
				if (d[1] > maxOA){
					maxOA = d[1];
				}
				if (d[1] < minOA){
					minOA = d[1];
				}
				aveA = aveA + d[1];
				aveOA = aveOA + d[1];
				if (d[2] > maxC){
					maxC = d[2];
				}
				if (d[2] < minC){
					minC = d[2];
				}
				if (d[2] > maxOC){
					maxOC = d[2];
				}
				if (d[2] < minOC){
					minOC = d[2];
				}
				aveC = aveC + d[2];
				aveOC = aveOC + d[2];
				if (d[3] > maxT){
					maxT = d[3];
				}
				if (d[3] < minT){
					minT = d[3];
				}
				if (d[3] > maxOT){
					maxOT = d[3];
				}
				if (d[3] < minOT){
					minOT = d[3];
				}
				aveT = aveT + d[3];
				aveOT = aveOT + d[3];
			}
			aveR = aveR / (float)dis.size();
			aveA = aveA / (float)dis.size();
			aveC = aveC / (float)dis.size();
			aveT = aveT / (float)dis.size();
			bwriter.write("Exact Basis Evaluation Summary\n");
			bwriter.newLine();
			bwriter.write("R Distance:     Min: " + formatter2.format(minR) + "\tMax: " + formatter2.format(maxR) + "\tAve: " + formatter2.format(aveR) + "\n");
			bwriter.newLine();
			bwriter.write("A Distance:     Min: " + formatter2.format(minA) + "\tMax: " + formatter2.format(maxA) + "\tAve: " + formatter2.format(aveA) + "\n");
			bwriter.newLine();
			bwriter.write("C Distance:     Min: " + formatter2.format(minC) + "\tMax: " + formatter2.format(maxC) + "\tAve: " + formatter2.format(aveC) + "\n");
			bwriter.newLine();
			bwriter.write("Total Distance: Min: " + formatter2.format(minT) + "\tMax: " + formatter2.format(maxT) + "\tAve: " + formatter2.format(aveT) + "\n");
			bwriter.newLine();
			bwriter.newLine();
			
			minR = Float.MAX_VALUE;
			maxR = 0;
			aveR = 0;
			minA = Float.MAX_VALUE;
			maxA = 0;
			aveA = 0;
			minC = Float.MAX_VALUE;
			maxC = 0;
			aveC = 0;
			minT = Float.MAX_VALUE;
			maxT = 0;
			aveT = 0;
			dis = arl.getExactAllDis();
			count = count + dis.size();
			for (int i = 0; i < dis.size(); i++){
				d = (float[])dis.get(i);
				if (d[0] > maxR){
					maxR = d[0];
				}
				if (d[0] < minR){
					minR = d[0];
				}
				if (d[0] > maxOR){
					maxOR = d[0];
				}
				if (d[0] < minOR){
					minOR = d[0];
				}
				aveR = aveR + d[0];
				aveOR = aveOR + d[0];
				if (d[1] > maxA){
					maxA = d[1];
				}
				if (d[1] < minA){
					minA = d[1];
				}
				if (d[1] > maxOA){
					maxOA = d[1];
				}
				if (d[1] < minOA){
					minOA = d[1];
				}
				aveA = aveA + d[1];
				aveOA = aveOA + d[1];
				if (d[2] > maxC){
					maxC = d[2];
				}
				if (d[2] < minC){
					minC = d[2];
				}
				if (d[2] > maxOC){
					maxOC = d[2];
				}
				if (d[2] < minOC){
					minOC = d[2];
				}
				aveC = aveC + d[2];
				aveOC = aveOC + d[2];
				if (d[3] > maxT){
					maxT = d[3];
				}
				if (d[3] < minT){
					minT = d[3];
				}
				if (d[3] > maxOT){
					maxOT = d[3];
				}
				if (d[3] < minOT){
					minOT = d[3];
				}
				aveT = aveT + d[3];
				aveOT = aveOT + d[3];
			}
			aveR = aveR / (float)dis.size();
			aveA = aveA / (float)dis.size();
			aveC = aveC / (float)dis.size();
			aveT = aveT / (float)dis.size();
			bwriter.write("Exact Expanded Evaluation Summary\n");
			bwriter.newLine();
			bwriter.write("R Distance:     Min: " + formatter2.format(minR) + "\tMax: " + formatter2.format(maxR) + "\tAve: " + formatter2.format(aveR) + "\n");
			bwriter.newLine();
			bwriter.write("A Distance:     Min: " + formatter2.format(minA) + "\tMax: " + formatter2.format(maxA) + "\tAve: " + formatter2.format(aveA) + "\n");
			bwriter.newLine();
			bwriter.write("C Distance:     Min: " + formatter2.format(minC) + "\tMax: " + formatter2.format(maxC) + "\tAve: " + formatter2.format(aveC) + "\n");
			bwriter.newLine();
			bwriter.write("Total Distance: Min: " + formatter2.format(minT) + "\tMax: " + formatter2.format(maxT) + "\tAve: " + formatter2.format(aveT) + "\n");
			bwriter.newLine();
			bwriter.newLine();
			
			minR = Float.MAX_VALUE;
			maxR = 0;
			aveR = 0;
			minA = Float.MAX_VALUE;
			maxA = 0;
			aveA = 0;
			minC = Float.MAX_VALUE;
			maxC = 0;
			aveC = 0;
			minT = Float.MAX_VALUE;
			maxT = 0;
			aveT = 0;
			dis = arl.getApproxMinMaxBasisDis();
			count = count + dis.size();
			for (int i = 0; i < dis.size(); i++){
				d = (float[])dis.get(i);
				if (d[0] > maxR){
					maxR = d[0];
				}
				if (d[0] < minR){
					minR = d[0];
				}
				if (d[0] > maxOR){
					maxOR = d[0];
				}
				if (d[0] < minOR){
					minOR = d[0];
				}
				aveR = aveR + d[0];
				aveOR = aveOR + d[0];
				if (d[1] > maxA){
					maxA = d[1];
				}
				if (d[1] < minA){
					minA = d[1];
				}
				if (d[1] > maxOA){
					maxOA = d[1];
				}
				if (d[1] < minOA){
					minOA = d[1];
				}
				aveA = aveA + d[1];
				aveOA = aveOA + d[1];
				if (d[2] > maxC){
					maxC = d[2];
				}
				if (d[2] < minC){
					minC = d[2];
				}
				if (d[2] > maxOC){
					maxOC = d[2];
				}
				if (d[2] < minOC){
					minOC = d[2];
				}
				aveC = aveC + d[2];
				aveOC = aveOC + d[2];
				if (d[3] > maxT){
					maxT = d[3];
				}
				if (d[3] < minT){
					minT = d[3];
				}
				if (d[3] > maxOT){
					maxOT = d[3];
				}
				if (d[3] < minOT){
					minOT = d[3];
				}
				aveT = aveT + d[3];
				aveOT = aveOT + d[3];
			}
			aveR = aveR / (float)dis.size();
			aveA = aveA / (float)dis.size();
			aveC = aveC / (float)dis.size();
			aveT = aveT / (float)dis.size();
			bwriter.write("Approximate Basis Evaluation Summary\n");
			bwriter.newLine();
			bwriter.write("R Distance:     Min: " + formatter2.format(minR) + "\tMax: " + formatter2.format(maxR) + "\tAve: " + formatter2.format(aveR) + "\n");
			bwriter.newLine();
			bwriter.write("A Distance:     Min: " + formatter2.format(minA) + "\tMax: " + formatter2.format(maxA) + "\tAve: " + formatter2.format(aveA) + "\n");
			bwriter.newLine();
			bwriter.write("C Distance:     Min: " + formatter2.format(minC) + "\tMax: " + formatter2.format(maxC) + "\tAve: " + formatter2.format(aveC) + "\n");
			bwriter.newLine();
			bwriter.write("Total Distance: Min: " + formatter2.format(minT) + "\tMax: " + formatter2.format(maxT) + "\tAve: " + formatter2.format(aveT) + "\n");
			bwriter.newLine();
			bwriter.newLine();
			
			minR = Float.MAX_VALUE;
			maxR = 0;
			aveR = 0;
			minA = Float.MAX_VALUE;
			maxA = 0;
			aveA = 0;
			minC = Float.MAX_VALUE;
			maxC = 0;
			aveC = 0;
			minT = Float.MAX_VALUE;
			maxT = 0;
			aveT = 0;
			dis = arl.getApproxAllDis();
			count = count + dis.size();
			for (int i = 0; i < dis.size(); i++){
				d = (float[])dis.get(i);
				if (d[0] > maxR){
					maxR = d[0];
				}
				if (d[0] < minR){
					minR = d[0];
				}
				if (d[0] > maxOR){
					maxOR = d[0];
				}
				if (d[0] < minOR){
					minOR = d[0];
				}
				aveR = aveR + d[0];
				aveOR = aveOR + d[0];
				if (d[1] > maxA){
					maxA = d[1];
				}
				if (d[1] < minA){
					minA = d[1];
				}
				if (d[1] > maxOA){
					maxOA = d[1];
				}
				if (d[1] < minOA){
					minOA = d[1];
				}
				aveA = aveA + d[1];
				aveOA = aveOA + d[1];
				if (d[2] > maxC){
					maxC = d[2];
				}
				if (d[2] < minC){
					minC = d[2];
				}
				if (d[2] > maxOC){
					maxOC = d[2];
				}
				if (d[2] < minOC){
					minOC = d[2];
				}
				aveC = aveC + d[2];
				aveOC = aveOC + d[2];
				if (d[3] > maxT){
					maxT = d[3];
				}
				if (d[3] < minT){
					minT = d[3];
				}
				if (d[3] > maxOT){
					maxOT = d[3];
				}
				if (d[3] < minOT){
					minOT = d[3];
				}
				aveT = aveT + d[3];
				aveOT = aveOT + d[3];
			}
			aveR = aveR / (float)dis.size();
			aveA = aveA / (float)dis.size();
			aveC = aveC / (float)dis.size();
			aveT = aveT / (float)dis.size();
			bwriter.write("Approximate Expanded Evaluation Summary\n");
			bwriter.newLine();
			bwriter.write("R Distance:     Min: " + formatter2.format(minR) + "\tMax: " + formatter2.format(maxR) + "\tAve: " + formatter2.format(aveR) + "\n");
			bwriter.newLine();
			bwriter.write("A Distance:     Min: " + formatter2.format(minA) + "\tMax: " + formatter2.format(maxA) + "\tAve: " + formatter2.format(aveA) + "\n");
			bwriter.newLine();
			bwriter.write("C Distance:     Min: " + formatter2.format(minC) + "\tMax: " + formatter2.format(maxC) + "\tAve: " + formatter2.format(aveC) + "\n");
			bwriter.newLine();
			bwriter.write("Total Distance: Min: " + formatter2.format(minT) + "\tMax: " + formatter2.format(maxT) + "\tAve: " + formatter2.format(aveT) + "\n");
			bwriter.newLine();
			bwriter.newLine();

			bwriter.write("Overall Evaluation Summary\n");
			bwriter.newLine();
			bwriter.write("R Distance:     Min: " + formatter2.format(minOR) + "\tMax: " + formatter2.format(maxOR) + "\tAve: " + formatter2.format(aveOR / (float)count) + "\n");
			bwriter.newLine();
			bwriter.write("A Distance:     Min: " + formatter2.format(minOA) + "\tMax: " + formatter2.format(maxOA) + "\tAve: " + formatter2.format(aveOA / (float)count) + "\n");
			bwriter.newLine();
			bwriter.write("C Distance:     Min: " + formatter2.format(minOC) + "\tMax: " + formatter2.format(maxOC) + "\tAve: " + formatter2.format(aveOC / (float)count) + "\n");
			bwriter.newLine();
			bwriter.write("Total Distance: Min: " + formatter2.format(minOT) + "\tMax: " + formatter2.format(maxOT) + "\tAve: " + formatter2.format(aveOT / (float)count) + "\n");
			bwriter.newLine();

			bwriter.write("--------------------------------------------------------------------------------------------------------------------------\n");
			bwriter.newLine();
*/			bwriter.write("Total time taken to extract association rules:          " + processTime + " ms");
			bwriter.newLine();
			bwriter.write("--------------------------------------------------------------------------------------------------------------------------");
			bwriter.newLine();

			//Put all the exact basis rules together...
			div = arl.getExactMinMaxBasisDiv();
			dis = arl.getExactMinMaxBasisDis();
			for (int i = 0; i < eb.size(); i++){
				if (desc){
					description1 = tr.getNames((int[])((Object[])eb.get(i))[0]);
					descriptionList1 = description1.split(",");
					description1 = "";
					for (int j = 0; j < descriptionList1.length; j++){
						if (j == 0){
							description1 = (String)Node.get(ID.indexOf(descriptionList1[j]));
						}
						else{
							description1 = description1 + ","+ Node.get(ID.indexOf(descriptionList1[j]));
						}
					}
					description2 = tr.getNames((int[])((Object[])eb.get(i))[1]);
					descriptionList2 = description2.split(",");
					description2 = "";
					for (int j = 0; j < descriptionList2.length; j++){
						if (j == 0){
							description2 = (String)Node.get(ID.indexOf(descriptionList2[j]));
						}
						else{
							description2 = description2 + ","+ Node.get(ID.indexOf(descriptionList2[j]));
						}
					}
					rules.add(description1 + " ==> " + description2);
				}
				else{
					rules.add(tr.getNames((int[])((Object[])eb.get(i))[0]) + " ==> " + tr.getNames((int[])((Object[])eb.get(i))[1]));
				}
				if (((String)rules.get(i)).length() > maxRuleLength){
					maxRuleLength = ((String)rules.get(i)).length();
				}
			}
			if (ebr.length() > maxRuleLength){
				bwriter.write(ebr + "    " + sup + "\t\t" + con);// + "\t" + coA + "\t" + coC + "\t" + coR + "\t" + ld + "\t" + chr + "\t" + tot + "\t" + aDis + "\t" + bDis + "\t" + cDis + "\t" + tDis);
				bwriter.newLine();
				maxRuleLength = ebr.length();
			}
			else{
				bwriter.write(ebr);
				for (int i = ebr.length(); i < maxRuleLength; i++){
					bwriter.write(" ");
				}
				bwriter.write("    " + sup + "\t\t" + con);// + "\t" + coA + "\t" + coC + "\t" + coR + "\t" + ld + "\t" + chr + "\t" + tot + "\t" + aDis + "\t" + bDis + "\t" + cDis + "\t" + tDis);
				bwriter.newLine();
			}
			for (int i = 0; i < rules.size(); i++){
				d = (float[])div.get(i);
				d2 = (float[])dis.get(i);
				bwriter.write((String)rules.get(i));
				for (int j = ((String)rules.get(i)).length(); j < maxRuleLength; j++){
					bwriter.write(" ");
				}
				bwriter.write("    ");
				bwriter.write("" + formatter.format((Float)ebs.get(i)) + "\t\t1.0");//\t\t" + formatter.format(((float[])ebcov.get(i))[0]) + "%\t" + formatter.format(((float[])ebcov.get(i))[1]) + "%\t" + formatter.format(((float[])ebcov.get(i))[2]) + "%\t" + formatter.format(d[0]) + "\t" + formatter.format(d[1]) + "\t" + formatter.format(d[2]) + "\t\t" + formatter2.format(d2[0]) + "\t\t" + formatter2.format(d2[1]) + "\t\t" + formatter2.format(d2[2]) + "\t\t" + formatter2.format(d2[3]));
				bwriter.newLine();
			}
			rules.clear();
			maxRuleLength = 0;
			bwriter.newLine();
			bwriter.write("----------------------------------------------------------------------------------------------------");
			bwriter.newLine();
			bwriter.newLine();

			//Put all the exact rules together...
			div = arl.getExactAllDiv();
			dis = arl.getExactAllDis();
			for (int i = 0; i < ea.size(); i++){
				if (desc){
					description1 = tr.getNames((int[])((Object[])ea.get(i))[0]);
					descriptionList1 = description1.split(",");
					description1 = "";
					for (int j = 0; j < descriptionList1.length; j++){
						if (j == 0){
							description1 = (String)Node.get(ID.indexOf(descriptionList1[j]));
						}
						else{
							description1 = description1 + ","+ Node.get(ID.indexOf(descriptionList1[j]));
						}
					}
					description2 = tr.getNames((int[])((Object[])ea.get(i))[1]);
					descriptionList2 = description2.split(",");
					description2 = "";
					for (int j = 0; j < descriptionList2.length; j++){
						if (j == 0){
							description2 = (String)Node.get(ID.indexOf(descriptionList2[j]));
						}
						else{
							description2 = description2 + ","+ Node.get(ID.indexOf(descriptionList2[j]));
						}
					}
					rules.add(description1 + " ==> " + description2);
				}
				else{
					rules.add(tr.getNames((int[])((Object[])ea.get(i))[0]) + " ==> " + tr.getNames((int[])((Object[])ea.get(i))[1]));
				}
				if (((String)rules.get(i)).length() > maxRuleLength){
					maxRuleLength = ((String)rules.get(i)).length();
				}
			}
			if (ear.length() > maxRuleLength){
				bwriter.write(ear + "    " + sup + "\t\t" + con);// + "\t" + coA + "\t" + coC + "\t" + coR + "\t" + ld + "\t" + chr + "\t" + tot + "\t" + aDis + "\t" + bDis + "\t" + cDis + "\t" + tDis);
				bwriter.newLine();
				maxRuleLength = ear.length();
			}
			else{
				bwriter.write(ear);
				for (int i = ear.length(); i < maxRuleLength; i++){
					bwriter.write(" ");
				}
				bwriter.write("    " + sup + "\t\t" + con);// + "\t" + coA + "\t" + coC + "\t" + coR + "\t" + ld + "\t" + chr + "\t" + tot + "\t" + aDis + "\t" + bDis + "\t" + cDis + "\t" + tDis);
				bwriter.newLine();
			}
			for (int i = 0; i < rules.size(); i++){
				d = (float[])div.get(i);
				d2 = (float[])dis.get(i);
				bwriter.write((String)rules.get(i));
				for (int j = ((String)rules.get(i)).length(); j < maxRuleLength; j++){
					bwriter.write(" ");
				}
				bwriter.write("    ");
				bwriter.write("" + formatter.format((Float)eas.get(i)) + "\t\t1.0");//\t\t" + formatter.format(((float[])eacov.get(i))[0]) + "%\t" + formatter.format(((float[])eacov.get(i))[1]) + "%\t" + formatter.format(((float[])eacov.get(i))[2])  + "%\t" + formatter.format(d[0]) + "\t" + formatter.format(d[1]) + "\t" + formatter.format(d[2]) + "\t\t" + formatter2.format(d2[0]) + "\t\t" + formatter2.format(d2[1]) + "\t\t" + formatter2.format(d2[2]) + "\t\t" + formatter2.format(d2[3]));
				bwriter.newLine();
			}
			rules.clear();
			maxRuleLength = 0;
			bwriter.newLine();
			bwriter.write("----------------------------------------------------------------------------------------------------");
			bwriter.newLine();
			bwriter.newLine();

			//Put all the approximate basis rules together...
			div = arl.getApproxMinMaxBasisDiv();
			dis = arl.getApproxMinMaxBasisDis();
			for (int i = 0; i < ab.size(); i++){
				if (desc){
					description1 = tr.getNames((int[])((Object[])ab.get(i))[0]);
					descriptionList1 = description1.split(",");
					description1 = "";
					for (int j = 0; j < descriptionList1.length; j++){
						if (j == 0){
							description1 = (String)Node.get(ID.indexOf(descriptionList1[j]));
						}
						else{
							description1 = description1 + ","+ Node.get(ID.indexOf(descriptionList1[j]));
						}
					}
					description2 = tr.getNames((int[])((Object[])ab.get(i))[1]);
					descriptionList2 = description2.split(",");
					description2 = "";
					for (int j = 0; j < descriptionList2.length; j++){
						if (j == 0){
							description2 = (String)Node.get(ID.indexOf(descriptionList2[j]));
						}
						else{
							description2 = description2 + ","+ Node.get(ID.indexOf(descriptionList2[j]));
						}
					}
					rules.add(description1 + " ==> " + description2);
				}
				else{
					rules.add(tr.getNames((int[])((Object[])ab.get(i))[0]) + " ==> " + tr.getNames((int[])((Object[])ab.get(i))[1]));
				}
				if (((String)rules.get(i)).length() > maxRuleLength){
					maxRuleLength = ((String)rules.get(i)).length();
				}
			}
			if (abr.length() > maxRuleLength){
				bwriter.write(abr + "    " + sup + "\t\t" + con);// + "\t" + coA + "\t" + coC + "\t" + coR + "\t" + ld + "\t" + chr + "\t" + tot + "\t" + aDis + "\t" + bDis + "\t" + cDis + "\t" + tDis);
				bwriter.newLine();
				maxRuleLength = abr.length();
			}
			else{
				bwriter.write(abr);
				for (int i = abr.length(); i < maxRuleLength; i++){
					bwriter.write(" ");
				}
				bwriter.write("    " + sup + "\t\t" + con);// + "\t" + coA + "\t" + coC + "\t" + coR + "\t" + ld + "\t" + chr + "\t" + tot + "\t" + aDis + "\t" + bDis + "\t" + cDis + "\t" + tDis);
				bwriter.newLine();
			}
			for (int i = 0; i < rules.size(); i++){
				d = (float[])div.get(i);
				d2 = (float[])dis.get(i);
				bwriter.write((String)rules.get(i));
				for (int j = ((String)rules.get(i)).length(); j < maxRuleLength; j++){
					bwriter.write(" ");
				}
				bwriter.write("    ");
				bwriter.write("" + formatter.format((Float)abs.get(i)) + "\t\t" + formatter.format((Float)abc.get(i)));// + "\t\t" + formatter.format(((float[])abcov.get(i))[0]) + "%\t" + formatter.format(((float[])abcov.get(i))[1]) + "%\t" + formatter.format(((float[])abcov.get(i))[2]) + "%\t" + formatter.format(d[0]) + "\t" + formatter.format(d[1]) + "\t" + formatter.format(d[2]) + "\t\t" + formatter2.format(d2[0]) + "\t\t" + formatter2.format(d2[1]) + "\t\t" + formatter2.format(d2[2]) + "\t\t" + formatter2.format(d2[3]));
				bwriter.newLine();
			}
			rules.clear();
			maxRuleLength = 0;
			bwriter.newLine();
			bwriter.write("----------------------------------------------------------------------------------------------------");
			bwriter.newLine();
			bwriter.newLine();

			//Put all the approximate rules together...
			div = arl.getApproxAllDiv();
			dis = arl.getApproxAllDis();
			for (int i = 0; i < aa.size(); i++){
				if (desc){
					description1 = tr.getNames((int[])((Object[])aa.get(i))[0]);
					descriptionList1 = description1.split(",");
					description1 = "";
					for (int j = 0; j < descriptionList1.length; j++){
						if (j == 0){
							description1 = (String)Node.get(ID.indexOf(descriptionList1[j]));
						}
						else{
							description1 = description1 + ","+ Node.get(ID.indexOf(descriptionList1[j]));
						}
					}
					description2 = tr.getNames((int[])((Object[])aa.get(i))[1]);
					descriptionList2 = description2.split(",");
					description2 = "";
					for (int j = 0; j < descriptionList2.length; j++){
						if (j == 0){
							description2 = (String)Node.get(ID.indexOf(descriptionList2[j]));
						}
						else{
							description2 = description2 + ","+ Node.get(ID.indexOf(descriptionList2[j]));
						}
					}
					rules.add(description1 + " ==> " + description2);
				}
				else{
					rules.add(tr.getNames((int[])((Object[])aa.get(i))[0]) + " ==> " + tr.getNames((int[])((Object[])aa.get(i))[1]));
				}
				if (((String)rules.get(i)).length() > maxRuleLength){
					maxRuleLength = ((String)rules.get(i)).length();
				}
			}
			if (aar.length() > maxRuleLength){
				bwriter.write(aar + "    " + sup + "\t\t" + con);// + "\t" + coA + "\t" + coC + "\t" + coR + "\t" + ld + "\t" + chr + "\t" + tot + "\t" + aDis + "\t" + bDis + "\t" + cDis + "\t" + tDis);
				bwriter.newLine();
				maxRuleLength = aar.length();
			}
			else{
				bwriter.write(aar);
				for (int i = aar.length(); i < maxRuleLength; i++){
					bwriter.write(" ");
				}
				bwriter.write("    " + sup +"\t\t" + con);// + "\t" + coA + "\t" + coC + "\t" + coR + "\t" + ld + "\t" + chr + "\t" + tot + "\t" + aDis + "\t" + bDis + "\t" + cDis + "\t" + tDis);
				bwriter.newLine();
			}
			for (int i = 0; i < rules.size(); i++){
				d = (float[])div.get(i);
				d2 = (float[])dis.get(i);
				bwriter.write((String)rules.get(i));
				for (int j = ((String)rules.get(i)).length(); j < maxRuleLength; j++){
					bwriter.write(" ");
				}
				bwriter.write("    ");
				bwriter.write("" + formatter.format((Float)aas.get(i)) + "\t\t" + formatter.format((Float)aac.get(i)));// + "\t\t" + formatter.format(((float[])aacov.get(i))[0]) + "%\t" + formatter.format(((float[])aacov.get(i))[1]) + "%\t" + formatter.format(((float[])aacov.get(i))[2]) + "%\t" + formatter.format(d[0]) + "\t" + formatter.format(d[1]) + "\t" + formatter.format(d[2]) + "\t\t" + formatter2.format(d2[0]) + "\t\t" + formatter2.format(d2[1]) + "\t\t" + formatter2.format(d2[2]) + "\t\t" + formatter2.format(d2[3]));
				bwriter.newLine();
			}
			rules.clear();
			maxRuleLength = 0;
			bwriter.newLine();
			bwriter.write("====================================================================================================");
/*			bwriter.newLine();

			//Output the details of the statistical distribution...
			bwriter.write("Statistical Distribution Summary");
			bwriter.newLine();
			bwriter.newLine();
			bwriter.write("All Association Rules");
			bwriter.newLine();
			bwriter.write("Node / Item\t\tOverall Freq.\tA. Freq.\tC. Freq.\t% Rate\t\tA. % Rate\tC. % Rate");
			bwriter.newLine();
			for (int i = 0; i < nodelist.length; i++){
				results = determineFreq(names, disCounts, nodelist[i], 1);
				bwriter.write(nodelist[i] + "\t\t\t" + results[0] + "\t\t" + results[1] + "\t\t" + results[2] + "\t\t" + 
									formatter.format(((float)results[0] / ((float)(arl.getExactMinMaxBasisSize() + arl.getExactAllSize() + arl.getApproxMinMaxBasisSize() + arl.getApproxAllSize()))) * (float)100) + "%\t" +
									formatter.format(((float)results[1] / ((float)(arl.getExactMinMaxBasisSize() + arl.getExactAllSize() + arl.getApproxMinMaxBasisSize() + arl.getApproxAllSize()))) * (float)100) + "%\t" +
									formatter.format(((float)results[2] / ((float)(arl.getExactMinMaxBasisSize() + arl.getExactAllSize() + arl.getApproxMinMaxBasisSize() + arl.getApproxAllSize()))) * (float)100) + "%");
				bwriter.newLine();
			}
			bwriter.write("----------------------------------------------------------------------------------------------------");
			bwriter.newLine();
			bwriter.write("Exact Basis Rules");
			bwriter.newLine();
			bwriter.write("Node / Item\t\tOverall Freq.\tA. Freq.\tC. Freq.\t% Rate\t\tA. % Rate\tC. % Rate");
			bwriter.newLine();
			for (int i = 0; i < nodelist.length; i++){
				results = determineFreq(names, disCounts, nodelist[i], 2);
				bwriter.write(nodelist[i] + "\t\t\t" + results[0] + "\t\t" + results[1] + "\t\t" + results[2] + "\t\t" + 
									formatter.format(((float)results[0] / ((float)(arl.getExactMinMaxBasisSize() + arl.getExactAllSize() + arl.getApproxMinMaxBasisSize() + arl.getApproxAllSize()))) * (float)100) + "%\t" +
									formatter.format(((float)results[1] / ((float)(arl.getExactMinMaxBasisSize() + arl.getExactAllSize() + arl.getApproxMinMaxBasisSize() + arl.getApproxAllSize()))) * (float)100) + "%\t" +
									formatter.format(((float)results[2] / ((float)(arl.getExactMinMaxBasisSize() + arl.getExactAllSize() + arl.getApproxMinMaxBasisSize() + arl.getApproxAllSize()))) * (float)100) + "%");
				bwriter.newLine();
			}
			bwriter.write("----------------------------------------------------------------------------------------------------");
			bwriter.newLine();
			bwriter.write("Exact Expanded Rules");
			bwriter.newLine();
			bwriter.write("Node / Item\t\tOverall Freq.\tA. Freq.\tC. Freq.\t% Rate\t\tA. % Rate\tC. % Rate");
			bwriter.newLine();
			for (int i = 0; i < nodelist.length; i++){
				results = determineFreq(names, disCounts, nodelist[i], 3);
				bwriter.write(nodelist[i] + "\t\t\t" + results[0] + "\t\t" + results[1] + "\t\t" + results[2] + "\t\t" + 
									formatter.format(((float)results[0] / ((float)(arl.getExactMinMaxBasisSize() + arl.getExactAllSize() + arl.getApproxMinMaxBasisSize() + arl.getApproxAllSize()))) * (float)100) + "%\t" +
									formatter.format(((float)results[1] / ((float)(arl.getExactMinMaxBasisSize() + arl.getExactAllSize() + arl.getApproxMinMaxBasisSize() + arl.getApproxAllSize()))) * (float)100) + "%\t" +
									formatter.format(((float)results[2] / ((float)(arl.getExactMinMaxBasisSize() + arl.getExactAllSize() + arl.getApproxMinMaxBasisSize() + arl.getApproxAllSize()))) * (float)100) + "%");
				bwriter.newLine();
			}
			bwriter.write("----------------------------------------------------------------------------------------------------");
			bwriter.newLine();
			bwriter.write("Approx Basis Rules");
			bwriter.newLine();
			bwriter.write("Node / Item\t\tOverall Freq.\tA. Freq.\tC. Freq.\t% Rate\t\tA. % Rate\tC. % Rate");
			bwriter.newLine();
			for (int i = 0; i < nodelist.length; i++){
				results = determineFreq(names, disCounts, nodelist[i], 4);
				bwriter.write(nodelist[i] + "\t\t\t" + results[0] + "\t\t" + results[1] + "\t\t" + results[2] + "\t\t" + 
									formatter.format(((float)results[0] / ((float)(arl.getExactMinMaxBasisSize() + arl.getExactAllSize() + arl.getApproxMinMaxBasisSize() + arl.getApproxAllSize()))) * (float)100) + "%\t" +
									formatter.format(((float)results[1] / ((float)(arl.getExactMinMaxBasisSize() + arl.getExactAllSize() + arl.getApproxMinMaxBasisSize() + arl.getApproxAllSize()))) * (float)100) + "%\t" +
									formatter.format(((float)results[2] / ((float)(arl.getExactMinMaxBasisSize() + arl.getExactAllSize() + arl.getApproxMinMaxBasisSize() + arl.getApproxAllSize()))) * (float)100) + "%");
				bwriter.newLine();
			}
			bwriter.write("----------------------------------------------------------------------------------------------------");
			bwriter.newLine();
			bwriter.write("Approx Expanded Rules");
			bwriter.newLine();
			bwriter.write("Node / Item\t\tOverall Freq.\tA. Freq.\tC. Freq.\t% Rate\t\tA. % Rate\tC. % Rate");
			bwriter.newLine();
			for (int i = 0; i < nodelist.length; i++){
				results = determineFreq(names, disCounts, nodelist[i], 5);
				bwriter.write(nodelist[i] + "\t\t\t" + results[0] + "\t\t" + results[1] + "\t\t" + results[2] + "\t\t" + 
									formatter.format(((float)results[0] / ((float)(arl.getExactMinMaxBasisSize() + arl.getExactAllSize() + arl.getApproxMinMaxBasisSize() + arl.getApproxAllSize()))) * (float)100) + "%\t" +
									formatter.format(((float)results[1] / ((float)(arl.getExactMinMaxBasisSize() + arl.getExactAllSize() + arl.getApproxMinMaxBasisSize() + arl.getApproxAllSize()))) * (float)100) + "%\t" +
									formatter.format(((float)results[2] / ((float)(arl.getExactMinMaxBasisSize() + arl.getExactAllSize() + arl.getApproxMinMaxBasisSize() + arl.getApproxAllSize()))) * (float)100) + "%");
				bwriter.newLine();
			}
			bwriter.write("====================================================================================================");
*/
			return true;
		}
		catch (IOException ioe){
			return false;
		}
	}

	/**
	 * writeBinaryDataFile method.
	 * Method used to write the generated binary dataset to
	 * an external file for any future use with this system.
	 * PRE: The binary dataset generated is to be written to file
	 *      and the binary dataset must be specified.
	 * POST: The specified binary dataset has been written to the
	 *       file previously specified.
	 */
	final public boolean writeBinaryDataFile(TransRecords tr){
		Object[] records;
		String[] rec;
		int r1;
		try{
			file = new File(filename);
			fwriter = new FileWriter(file);
			bwriter = new BufferedWriter(fwriter);
			file.createNewFile();

			records = tr.getRec();
			r1 = records.length;
			//Write each transaction to the file, one per line...
			for (int i = 0; i < r1; i++){
				rec = (String[])records[i];
				for (int j = 0; j < rec.length - 1; j++){
					bwriter.write(rec[j] + ",");
				}
				bwriter.write(rec[rec.length - 1] + ".");
				bwriter.newLine();
			}

			closeFile();
			return true;
		}
		catch (IOException ioe){
			System.out.println(ioe);
			return false;
		}
	}

	/**
	 * writeBinaryInfoFile method.
	 * Method used to write the generated binary dataset information
	 * (the item/attribute names and valid values) to an external
	 * file for any future use with this system.
	 * PRE: The item information generated for the binary dataset is to
	 *      be written to file and the binary dataset must be specified.
	 * POST: The item information for the binary dataset has been
	 *       witten to the file previously specified.
	 */
	final public boolean writeBinaryInfoFile(TransRecords tr){
		String[] attList, attValues;
		int a1;
		try{
			file = new File(filename);
			fwriter = new FileWriter(file);
			bwriter = new BufferedWriter(fwriter);
			file.createNewFile();

			for (int i = 0; i < 3; i++){
				bwriter.write("|");
				bwriter.newLine();
			}
			bwriter.newLine();
			attList = tr.getNames();
			attValues = tr.getVal1();
			a1 = attList.length;
			//Write the item/attribute information to the file, one per line...
			for (int i = 0; i < a1; i++){
				bwriter.write(attList[i] + ":	" + attValues[i]);
				bwriter.newLine();
			}

			closeFile();
			return true;
		}
		catch (IOException ioe){
			return false;
		}
	}

	/**
	 * closeFile method.
	 * Method used to  close the readers connection to the external
	 * file that was the last one opened/read from.
	 * PRE: The currently open file is to be closed.
	 * POST: The file has been closed.
	 */
	final private boolean closeFile(){
		try{
			bwriter.flush();
			bwriter.close();
			return true;
		}
		catch (IOException ioe){
			return false;
		}
	}

	/**
	 * collapseList method.
	 * Method used to take a list based on generators and
	 * convert/collapse it into a list based on closed itemsets.
	 * PRE: A copy of the list of frequent closed itemsets is to be
	 *      made in which the order based on the closed itemsets
	 *      instead of the generators and the list of closed
	 *      itemsets is to be specified.
	 * POST: A copy of the closed itemset has been made and has its
	 *       order based on the itemsets and has been returned.
	 */
	final private ArrayList collapseList(FCItemsetList fcl, String[] list1){
		String items = "";
		int iLength = 0;
		int gLength = 0;
		ArrayList itemList = new ArrayList();
		ArrayList ids = new ArrayList();
		ArrayList item = new ArrayList();
		ArrayList gen = new ArrayList();
		ArrayList sup = new ArrayList();
		ArrayList itemsets = fcl.getFreqClosed();
		ArrayList supports = fcl.getSupports();
		for (int i = 0; i < itemsets.size(); i++){
			Object[] entry = (Object[])itemsets.get(i);
			int index = locater(ids, (int[])entry[1]);
			if (index != -1){
				//Item already in list...
				int[] d3 = (int[])entry[0];
				items = "";
				for (int j = 0; j < d3.length; j++){
					if (items.length() == 0){
						items = "" + list1[d3[j] - 1];
					}
					else{
						items = items + "," + list1[d3[j] - 1];
					}
				}
				gen.set(index, ((String)gen.get(index)) + "  [" + items + "]");
				if (((String)gen.get(index)).length() > gLength){
					gLength = ((String)gen.get(index)).length();
				}
			}
			else{
				//item not in list, so add to list...
				ids.add((int[])entry[1]);
				int[] d1 = (int[])entry[1];
				items = "";
				for (int j = 0; j < d1.length; j++){
					if (items.length() == 0){
						items = "" + list1[d1[j] - 1];
					}
					else{
						items = items + "," + list1[d1[j] - 1];
					}
				}
				item.add("[" + items + "]");
				if ((items.length() + 2) > iLength){
					iLength = items.length() + 2;
				}
				int[] d2 = (int[])entry[0];
				items = "";
				for (int j = 0; j < d2.length; j++){
					if (items.length() == 0){
						items = "" + list1[d2[j] - 1];
					}
					else{
						items = items + "," + list1[d2[j] - 1];
					}
				}
				gen.add("[" + items + "]");
				sup.add((Float)supports.get(i));
				if ((items.length() + 2) > gLength){
					gLength = items.length() + 2;
				}
			}
		}
		itemList.add(item);
		itemList.add(gen);
		itemList.add(sup);
		itemList.add(iLength);
		itemList.add(gLength);
		return itemList;
	}
	
	final private int locater (ArrayList list1, int[] set1){
		int index = -1;
		int size1 = list1.size();
		int[] set2;
		for (int i = 0; i < size1; i++){
			set2 = (int[])list1.get(i);
			if (compare(set1, set2)){
				//Matched so this set already in the list...
				index = i;
				break;
			}
		}
		return index;
	}
		
	final protected boolean compare(int[] entry1, int[] entry2){
		boolean match = false;
		int l1 = entry1.length;
		int l2 = entry2.length;
		if (l1 == l2){
			for (int i = 0; i < l1; i++){
				match = false;
				for (int j = 0; j < l2; j++){
					if (entry1[i] == entry2[j]){
						match = true;
						break;
					}
				}
				if (!match){
					return false;
				}
			}
			if (match){
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	final private float[] summation(ArrayList cov, int index){
		float[] values = new float[3];
		float sum = 0, min = Float.MAX_VALUE, max = 0;
		int size = cov.size();
		for (int i = 0; i < size; i++){
			sum = sum + ((float[])cov.get(i))[index];
			if (((float[])cov.get(i))[index] > max){
				max = ((float[])cov.get(i))[index];
			}
			if (((float[])cov.get(i))[index] < min){
				min = ((float[])cov.get(i))[index];
			}
		}
		values[0] = sum;
		values[1] = max;
		if (min != Float.MAX_VALUE){
			values[2] = min;
		}
		else{
			values[2] = 0;
		}
		return values;
	}
	
	final private float[] summation(ArrayList cov){
		float[] values = new float[3];
		float sum = 0, min = Float.MAX_VALUE, max = 0;
		int size = cov.size();
		for (int i = 0; i < size; i++){
			sum = sum + (Float)cov.get(i);
			if ((Float)cov.get(i) > max){
				max = (Float)cov.get(i);
			}
			if ((Float)cov.get(i) < min){
				min = (Float)cov.get(i);
			}
		}
		values[0] = sum;
		values[1] = max;
		if (min != Float.MAX_VALUE){
			values[2] = min;
		}
		else{
			values[2] = 0;
		}
		return values;
	}
	
	private int[] determineUnique(Object[] disCounts, int mode){
		int[] results = new int[3];
		int[][] freqs;
		if (mode ==1 ){
			//All rules...
			for (int i = 0; i < disCounts.length; i++){
				freqs = (int[][])disCounts[i];
				if (freqs[0][0] != 0 || freqs[0][1] != 0 || freqs[0][2] != 0 || freqs[0][3] != 0){
					results[0] = results[0] + 1;
				}
				if (freqs[1][0] != 0 || freqs[1][1] != 0 || freqs[1][2] != 0 || freqs[1][3] != 0){
					results[1] = results[1] + 1;
				}
				if (freqs[2][0] != 0 || freqs[2][1] != 0 || freqs[2][2] != 0 || freqs[2][3] != 0){
					results[2] = results[2] + 1;
				}
			}
		}
		else if (mode == 2){
			//EB...
			for (int i = 0; i < disCounts.length; i++){
				freqs = (int[][])disCounts[i];
				if (freqs[0][0] != 0){
					results[0] = results[0] + 1;
				}
				if (freqs[1][0] != 0){
					results[1] = results[1] + 1;
				}
				if (freqs[2][0] != 0){
					results[2] = results[2] + 1;
				}
			}
		}
		else if (mode == 3){
			//EE...
			for (int i = 0; i < disCounts.length; i++){
				freqs = (int[][])disCounts[i];
				if (freqs[0][1] != 0){
					results[0] = results[0] + 1;
				}
				if (freqs[1][1] != 0){
					results[1] = results[1] + 1;
				}
				if (freqs[2][1] != 0){
					results[2] = results[2] + 1;
				}
			}
		}
		else if (mode == 4){
			//AB...
			for (int i = 0; i < disCounts.length; i++){
				freqs = (int[][])disCounts[i];
				if (freqs[0][2] != 0){
					results[0] = results[0] + 1;
				}
				if (freqs[1][2] != 0){
					results[1] = results[1] + 1;
				}
				if (freqs[2][2] != 0){
					results[2] = results[2] + 1;
				}
			}
		}
		else{
			//AE...
			for (int i = 0; i < disCounts.length; i++){
				freqs = (int[][])disCounts[i];
				if (freqs[0][3] != 0){
					results[0] = results[0] + 1;
				}
				if (freqs[1][3] != 0){
					results[1] = results[1] + 1;
				}
				if (freqs[2][3] != 0){
					results[2] = results[2] + 1;
				}
			}
		}
		return results;
	}
	
	private Object[] determineMinMaxAve(Object[] disCounts, int mode){
		Object[] results = new Object[3];
		int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE, count = 0;
		float ave = (float)0.0;
		int[][] freqs;
		if (mode == 1){
			//All rules...
			int sum;
			for (int i = 0; i < disCounts.length; i++){
				sum = 0;
				freqs = (int[][])disCounts[i];
				sum = freqs[0][0] + freqs[0][1] + freqs[0][2] + freqs[0][3];
				if (sum != 0){
					if (sum < min){
						min = sum;
					}
					if (sum > max){
						max = sum;
					}
					ave = ave + (float)sum;
					count++;
				}
			}
		}
		else if (mode == 2){
			//EB...
			for (int i = 0; i < disCounts.length; i++){
				freqs = (int[][])disCounts[i];
				if (freqs[0][0] != 0){
					if (freqs[0][0] < min){
						min = freqs[0][0];
					}
					if (freqs[0][0] > max){
						max = freqs[0][0];
					}
					ave = ave + (float)freqs[0][0];
					count++;
				}
			}
		}
		else if (mode == 3){
			//EE...
			for (int i = 0; i < disCounts.length; i++){
				freqs = (int[][])disCounts[i];
				if (freqs[0][1] != 0){
					if (freqs[0][1] < min){
						min = freqs[0][1];
					}
					if (freqs[0][1] > max){
						max = freqs[0][1];
					}
					ave = ave + (float)freqs[0][1];
					count++;
				}
			}
		}
		else if (mode == 4){
			//AB...
			for (int i = 0; i < disCounts.length; i++){
				freqs = (int[][])disCounts[i];
				if (freqs[0][2] != 0){
					if (freqs[0][2] < min){
						min = freqs[0][2];
					}
					if (freqs[0][2] > max){
						max = freqs[0][2];
					}
					ave = ave + (float)freqs[0][2];
					count++;
				}
			}
		}
		else{
			//AE...
			for (int i = 0; i < disCounts.length; i++){
				freqs = (int[][])disCounts[i];
				if (freqs[0][3] != 0){
					if (freqs[0][3] < min){
						min = freqs[0][3];
					}
					if (freqs[0][3] > max){
						max = freqs[0][3];
					}
					ave = ave + (float)freqs[0][3];
					count++;
				}
			}
		}
		if (min == Integer.MAX_VALUE){
			min = 0;
		}
		if (max == Integer.MIN_VALUE){
			max = 0;
		}
		results[0] = min;
		results[1] = max;
		if (ave != 0){
			results[2] = ave / (float)count;
		}
		else{
			results[2] = (float)0;
		}
		return results;
	}
	
	private int[] determineFreq(Object[] attnames, Object[] disCounts, String node, int mode){
		int[] results = new int[3];
		int[][] freqs;
		String[] names = (String[])attnames[0];
		int index = -1;
		for (int i = 0; i < names.length; i++){
			if (names[i].equals(node)){
				//Match...
				index = i;
				i = names.length;
			}
		}
		if (index != -1){
			freqs = (int[][])disCounts[index];
			if (mode == 1){
				//All rules...
				results[0] = freqs[0][0] + freqs[0][1] + freqs[0][2] + freqs[0][3];
				results[1] = freqs[1][0] + freqs[1][1] + freqs[1][2] + freqs[1][3];
				results[2] = freqs[2][0] + freqs[2][1] + freqs[2][2] + freqs[2][3];
			}
			else if (mode == 2){
				//EB rules...
				results[0] = freqs[0][0];
				results[1] = freqs[1][0];
				results[2] = freqs[2][0];
			}
			else if (mode == 3){
				//EE rules...
				results[0] = freqs[0][1];
				results[1] = freqs[1][1];
				results[2] = freqs[2][1];
			}
			else if (mode == 4){
				//AB rules...
				results[0] = freqs[0][2];
				results[1] = freqs[1][2];
				results[2] = freqs[2][2];
			}
			else{
				//AE rules...
				results[0] = freqs[0][3];
				results[1] = freqs[1][3];
				results[2] = freqs[2][3];
			}
		}
		else{
			results[0] = 0;
			results[1] = 0;
			results[2] = 0;
		}
		return results;
	}
}