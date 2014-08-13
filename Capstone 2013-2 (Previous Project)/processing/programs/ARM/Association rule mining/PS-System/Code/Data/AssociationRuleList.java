/**
 * AssociationRuleList class.
 * 
 * Start Date: 30 January 2007
 * @author Gavin Shaw
 * @version 1.0
 *
 * Log:
 *		  1.0 Start version of class.
 */
package Data;

import java.util.ArrayList;

public class AssociationRuleList{

	private ArrayList exactMinMaxBasis, exactMinMaxBasisSup, exactMinMaxBasisCov, exactMinMaxBasisDiv, exactMinMaxBasisDis;
	
	private ArrayList exactAll, exactAllSup, exactAllCov, exactAllDiv, exactAllDis;

	private ArrayList approxMinMaxBasis, approxMinMaxBasisSup, approxMinMaxBasisCon, approxMinMaxBasisCov, approxMinMaxBasisDiv, approxMinMaxBasisDis;
	
	private ArrayList approxAll, approxAllSup, approxAllCon, approxAllCov, approxAllDiv, approxAllDis;

	private float exactMinMaxBasisCovA = 0, exactMinMaxBasisCovC = 0, exactMinMaxBasisCovR = 0, exactAbstractBasisCovR = 0;

	private float exactAllCovA = 0, exactAllCovC = 0, exactAllCovR = 0, exactAllAbstractCovR = 0;

	private float approxMinMaxBasisCovA = 0, approxMinMaxBasisCovC = 0, approxMinMaxBasisCovR = 0, approxAbstractBasisCovR = 0;

	private float approxAllCovA = 0, approxAllCovC = 0, approxAllCovR = 0, approxAbstractCovR = 0;

	private float exactCovR = 0, exactAbstractCovR = 0, approxCovR = 0, approxAllAbstractCovR = 0;

	private int ebLeaf = 0, eeLeaf = 0, abLeaf = 0, aeLeaf = 0;

	private int ebAbstract = 0, eeAbstract = 0, abAbstract = 0, aeAbstract = 0;
	
	private Object[] disCounts;
	
	private int[][] freqs;

	/**
	 * AssociationRuleList method.
	 * Constructor.
	 * Method used to initialise this class and setup the
	 * variables to hold the mined association rules, support
	 * and confidence values.
	 * PRE: Class and variables to be initialised.
	 * POST: Class and variables have been initialised.
	 */
	public AssociationRuleList(int nodeCount){
		exactMinMaxBasis = new ArrayList();
		exactMinMaxBasisSup = new ArrayList();
		exactMinMaxBasisCov = new ArrayList();
		exactMinMaxBasisDiv = new ArrayList();
		exactMinMaxBasisDis = new ArrayList();

		exactAll = new ArrayList();
		exactAllSup = new ArrayList();
		exactAllCov = new ArrayList();
		exactAllDiv = new ArrayList();
		exactAllDis = new ArrayList();

		approxMinMaxBasis = new ArrayList();
		approxMinMaxBasisSup = new ArrayList();
		approxMinMaxBasisCon = new ArrayList();
		approxMinMaxBasisCov = new ArrayList();
		approxMinMaxBasisDiv = new ArrayList();
		approxMinMaxBasisDis = new ArrayList();

		approxAll = new ArrayList();
		approxAllSup = new ArrayList();
		approxAllCon = new ArrayList();
		approxAllCov = new ArrayList();
		approxAllDiv = new ArrayList();
		approxAllDis = new ArrayList();
		
		disCounts = new Object[nodeCount];
		for (int i = 0; i < nodeCount; i++){
			freqs = new int[3][4];
			freqs[0][0] = 0;
			freqs[0][1] = 0;
			freqs[0][2] = 0;
			freqs[0][3] = 0;
			freqs[1][0] = 0;
			freqs[1][1] = 0;
			freqs[1][2] = 0;
			freqs[1][3] = 0;
			freqs[2][0] = 0;
			freqs[2][1] = 0;
			freqs[2][2] = 0;
			freqs[2][3] = 0;
			disCounts[i] = freqs;
		}
	}

	/**
	 * addExactMinMaxBasis method.
	 * Method used to add a list of exact basis rules and
	 * their supports.
	 * PRE: A collection of exact basis rules are to be added
	 *	     to the list.
	 * POST: The collection of exact basis rules have been added
	 *       to the list.
	 */
	final public void addExactMinMaxBasis(ArrayList rule, ArrayList sup, ArrayList cov, ArrayList div, ArrayList dis, float covA, float covC, float covR, float covAR){
		Object[] temp;
		float[] ct;
		for (int i = 0; i < rule.size(); i++){
			temp = (Object[])rule.get(i);
			exactMinMaxBasis.add(temp);
		}
		for (int i = 0; i < sup.size(); i++){
			exactMinMaxBasisSup.add((Float)sup.get(i));
		}
		for (int i = 0; i < cov.size(); i++){
			ct = (float[])cov.get(i);
			exactMinMaxBasisCov.add(ct);
		}
		for (int i = 0; i < div.size(); i++){
			ct = (float[])div.get(i);
			exactMinMaxBasisDiv.add(ct);
		}
		for (int i = 0; i < dis.size(); i++){
			ct = (float[])dis.get(i);
			exactMinMaxBasisDis.add(ct);
		}
		exactMinMaxBasisCovA = covA;
		exactMinMaxBasisCovC = covC;
		exactMinMaxBasisCovR = covR;
		exactAbstractBasisCovR = covAR;
	}

	final public void addExactMinMaxBasis(int count1, int count2){
		ebLeaf = count1;
		ebAbstract = count2;
	}

	/**
	 * addExactAll method.
	 * Method used to add a list of exact rules and
	 * their supports.
	 * PRE: A collection of exact rules are to be added
	 *      to the list.
	 * POST: A collection of exact rules have been added
	 *       to the list.
	 */
	final public void addExactAll(ArrayList rule, ArrayList sup, ArrayList cov, ArrayList div, ArrayList dis, float covA, float covC, float covR, float covAR){
		Object[] temp;
		float[] ct;
		for (int i = 0; i < rule.size(); i++){
			temp = (Object[])rule.get(i);
			exactAll.add(temp);
		}
		for (int i = 0; i < sup.size(); i++){
			exactAllSup.add((Float)sup.get(i));
		}
		for (int i = 0; i < cov.size(); i++){
			ct = (float[])cov.get(i);
			exactAllCov.add(ct);
		}
		for (int i = 0; i < div.size(); i++){
			ct = (float[])div.get(i);
			exactAllDiv.add(ct);
		}
		for (int i = 0; i < dis.size(); i++){
			ct = (float[])dis.get(i);
			exactAllDis.add(ct);
		}
		exactAllCovA = covA;
		exactAllCovC = covC;
		exactAllCovR = covR;
		exactAllAbstractCovR = covAR;
	}

	final public void addExactAll(int count1, int count2){
		eeLeaf = count1;
		eeAbstract = count2;
	}

	/**
	 * addApproxMinMaxBasis method.
	 * Method used to add a list of approximate basis rules,
	 * their supports and confidences.
	 * PRE: A collection of approx basis rules are to be added
	 *      to the list.
	 * POST: A collection of approx basis rules have been added
	 *       to the list.
	 */
	final public void addApproxMinMaxBasis(ArrayList rule, ArrayList sup, ArrayList con, ArrayList cov, ArrayList div, ArrayList dis, float covA, float covC, float covR, float covAR){
		Object[] temp;
		float[] ct;
		for (int i = 0; i < rule.size(); i++){
			temp = (Object[])rule.get(i);
			approxMinMaxBasis.add(temp);
		}
		for (int i = 0; i < sup.size(); i++){
			approxMinMaxBasisSup.add((Float)sup.get(i));
		}
		for (int i = 0; i < con.size(); i++){
			approxMinMaxBasisCon.add((Float)con.get(i));
		}
		for (int i = 0; i < cov.size(); i++){
			ct = (float[])cov.get(i);
			approxMinMaxBasisCov.add(ct);
		}
		for (int i = 0; i < div.size(); i++){
			ct = (float[])div.get(i);
			approxMinMaxBasisDiv.add(ct);
		}
		for (int i = 0; i < dis.size(); i++){
			ct = (float[])dis.get(i);
			approxMinMaxBasisDis.add(ct);
		}
		approxMinMaxBasisCovA = covA;
		approxMinMaxBasisCovC = covC;
		approxMinMaxBasisCovR = covR;
		approxAbstractBasisCovR = covAR;
	}

	final public void addApproxMinMaxBasis(int count1, int count2){
		abLeaf = count1;
		abAbstract = count2;
	}

	/**
	 * addApproxAll method.
	 * Method used to add a list of approximate rules,
	 * their supports and confidences.
	 * PRE: A collection of approx rules are to be added
	 *      to the list.
	 * POST: A collection of approx rules have been added
	 *       to the list.
	 */
	final public void addApproxAll(ArrayList rule, ArrayList sup, ArrayList con, ArrayList cov, ArrayList div, ArrayList dis, float covA, float covC, float covR, float covAR){
		Object[] temp;
		float[] ct;
		for (int i = 0; i < rule.size(); i++){
			temp = (Object[])rule.get(i);
			approxAll.add(temp);
		}
		for (int i = 0; i < sup.size(); i++){
			approxAllSup.add((Float)sup.get(i));
		}
		for (int i = 0; i < con.size(); i++){
			approxAllCon.add((Float)con.get(i));
		}
		for (int i = 0; i < cov.size(); i++){
			ct = (float[])cov.get(i);
			approxAllCov.add(ct);
		}
		for (int i = 0; i < div.size(); i++){
			ct = (float[])div.get(i);
			approxAllDiv.add(ct);
		}
		for (int i = 0; i < dis.size(); i++){
			ct = (float[])dis.get(i);
			approxAllDis.add(ct);
		}
		approxAllCovA = covA;
		approxAllCovC = covC;
		approxAllCovR = covR;
		approxAbstractCovR = covAR;
	}

	final public void addApproxAll(int count1, int count2){
		aeLeaf = count1;
		aeAbstract = count2;
	}

	/* Add rule set coverage methods... */

	final public void addExactRuleCoverage(float cov){
		exactCovR = cov;
	}

	final public void addExactRuleCoverage(float cov, float acov){
		exactCovR = cov;
		exactAbstractCovR = acov;
	}

	final public void addApproxRuleCoverage(float cov){
		approxCovR = cov;
	}

	final public void addApproxRuleCoverage(float cov, float acov){
		approxCovR = cov;
		approxAbstractCovR = acov;
	}

	/* Get rule coverage methods... */

	final public float getExactMinMaxBasisCovA(){
		return exactMinMaxBasisCovA;
	}

	final public float getExactMinMaxBasisCovC(){
		return exactMinMaxBasisCovC;
	}

	final public float getExactMinMaxBasisCovR(){
		return exactMinMaxBasisCovR;
	}

	final public float getExactAbstractBasisCovR(){
		return exactAbstractBasisCovR;
	}

	final public float getExactAllCovA(){
		return exactAllCovA;
	}

	final public float getExactAllCovC(){
		return exactAllCovC;
	}

	final public float getExactAllCovR(){
		return exactAllCovR;
	}

	final public float getExactAllAbstractCovR(){
		return exactAllAbstractCovR;
	}

	final public float getApproxMinMaxBasisCovA(){
		return approxMinMaxBasisCovA;
	}

	final public float getApproxMinMaxBasisCovC(){
		return approxMinMaxBasisCovC;
	}

	final public float getApproxMinMaxBasisCovR(){
		return approxMinMaxBasisCovR;
	}

	final public float getApproxAbstractBasisCovR(){
		return approxAbstractBasisCovR;
	}

	final public float getApproxAllCovA(){
		return approxAllCovA;
	}

	final public float getApproxAllCovC(){
		return approxAllCovC;
	}

	final public float getApproxAllCovR(){
		return approxAllCovR;
	}

	final public float getApproxAbstractCovR(){
		return approxAbstractCovR;
	}

	/* Get rule set coverage methods... */

	final public float getExactRuleCoverage(){
		return exactCovR;
	}

	final public float getExactAbstractRuleCoverage(){
		return exactAbstractCovR;
	}

	final public float getApproxRuleCoverage(){
		return approxCovR;
	}

	final public float getApproxAbstractRuleCoverage(){
		return approxAbstractCovR;
	}

	/**
	 * getExactMinMaxBasis method.
	 * Mmethod used to get and return the list of
	 * exact basis rules.
	 * PRE: The list of exact basis rules is required.
	 * POST: The list of exact basis rules has been returned.
	 */
	final public ArrayList getExactMinMaxBasis(){
		return exactMinMaxBasis;
	}

	/**
	 * getExactMinMaxBasisSup method.
	 * Method used to get and return the list of support
	 * values for the exact basis rules.
	 * PRE: The list of exact basis rule supports is required.
	 * POST: The list of exact basis rule supports has been
	 *       returned.
	 */
	final public ArrayList getExactMinMaxBasisSup(){
		return exactMinMaxBasisSup;
	}

	final public ArrayList getExactMinMaxBasisCov(){
		return exactMinMaxBasisCov;
	}
	
	final public ArrayList getExactMinMaxBasisDiv(){
		return exactMinMaxBasisDiv;
	}
	
	final public ArrayList getExactMinMaxBasisDis(){
		return exactMinMaxBasisDis;
	}

	/**
	 * getExactAll method.
	 * Method used to get and return the list of
	 * exact rules.
	 * PRE: The list of exact rules is required.
	 * POST: The list of exact rules has been returned.
	 */
	final public ArrayList getExactAll(){
		return exactAll;
	}

	/**
	 * getExactAllSup method.
	 * Method used to get and return the list of support
	 * values for the exact rules.
	 * PRE: The list of exact rule supports is required.
	 * POST: The list of exact rule supports has been returned.
	 */
	final public ArrayList getExactAllSup(){
		return exactAllSup;
	}

	final public ArrayList getExactAllCov(){
		return exactAllCov;
	}
	
	final public ArrayList getExactAllDiv(){
		return exactAllDiv;
	}
	
	final public ArrayList getExactAllDis(){
		return exactAllDis;
	}

	/**
	 * getApproxMinMaxBasis method.
	 * Mmethod used to get and return the list of
	 * approximate basis rules.
	 * PRE: The list of approx basis rules is required.
	 * POST: The list of approx basis rules has been returned.
	 */
	final public ArrayList getApproxMinMaxBasis(){
		return approxMinMaxBasis;
	}

	/**
	 * getApproxMinMaxBasisSup method.
	 * Method used to get and return the list of support
	 * values for the approximate basis rules.
	 * PRE: The list of approx basis rule supports is required.
	 * POST: The list of approx basis rule supports have been
	 *       returned.
	 */
	final public ArrayList getApproxMinMaxBasisSup(){
		return approxMinMaxBasisSup;
	}

	/**
	 * getApproxMinMaxBasisCon method.
	 * Method used to get and return the list of confidence
	 * values for the approximate basis rules.
	 * PRE: The list of approx basis rule confidences is required.
	 * POST: The list of approx basis rule confidences have been
	 *       returned.
	 */
	final public ArrayList getApproxMinMaxBasisCon(){
		return approxMinMaxBasisCon;
	}

	final public ArrayList getApproxMinMaxBasisCov(){
		return approxMinMaxBasisCov;
	}
	
	final public ArrayList getApproxMinMaxBasisDiv(){
		return approxMinMaxBasisDiv;
	}
	
	final public ArrayList getApproxMinMaxBasisDis(){
		return approxMinMaxBasisDis;
	}
	

	/**
	 * getApproxAll method.
	 * Mmethod used to get and return the list of
	 * approximate rules.
	 * PRE: The list of approx rules is required.
	 * POST: The list of approx rules has been returned.
	 */
	final public ArrayList getApproxAll(){
		return approxAll;
	}

	/**
	 * getApproxAllSup method.
	 * Method used to get and return the list of support
	 * values for the approximate rules.
	 * PRE: The list of approx rule supports is required.
	 * POST: The list of approx rule supports have been returned.
	 */
	final public ArrayList getApproxAllSup(){
		return approxAllSup;
	}

	/**
	 * getApproxAllCon method.
	 * Method used to get and return the list of confidence
	 * values for the approximate rules.
	 * PRE: The list of approx rule confidences is required.
	 * POST: The list of approx rule confidences have been returned.
	 */
	final public ArrayList getApproxAllCon(){
		return approxAllCon;
	}

	final public ArrayList getApproxAllCov(){
		return approxAllCov;
	}
	
	final public ArrayList getApproxAllDiv(){
		return approxAllDiv;
	}
	
	final public ArrayList getApproxAllDis(){
		return approxAllDis;
	}

	/**
	 * getExactMinMaxBasisSize method.
	 * Method used to get and return the total number of
	 * exact basis rules found by the algorithm that mines
	 * association rules.
	 * PRE: The number of exact basis rules is required.
	 * POST: The number of exact basis rules have been returned.
	 */
	final public int getExactMinMaxBasisSize(){
		return exactMinMaxBasis.size();
	}

	/**
	 * getExactAllSize method.
	 * Method used to get and return the total number of
	 * exact rules found by the algorithm that mines
	 * association rules.
	 * PRE: The number of exact rules is required.
	 * POST: The number of exact rules have been returned.
	 */
	final public int getExactAllSize(){
		return exactAll.size();
	}

	/**
	 * getApproxMinMaxBasisSize method.
	 * Method used to get and return the total number of
	 * approximate basis rules found by the algorithm that mines
	 * association rules.
	 * PRE: The number of approx basis rules is required.
	 * POST: The number of approx basis rules have been returned.
	 */
	final public int getApproxMinMaxBasisSize(){
		return approxMinMaxBasis.size();
	}

	/**
	 * getApproxAllSize method.
	 * Method used to get and return the total number of
	 * approximate rules found by the algorithm that mines
	 * association rules.
	 * PRE: The number of approx rules is required.
	 * POST: The number of approx rules have been returned.
	 */
	final public int getApproxAllSize(){
		return approxAll.size();
	}

	/* Get counts of leaf/abstract rules in a rule set... */

	final public int getExactMinMaxBasisLeafSize(){
		return ebLeaf;
	}

	final public int getExactMinMaxBasisAbstractSize(){
		return ebAbstract;
	}

	final public int getExactLeafSize(){
		return eeLeaf;
	}

	final public int getExactAbstractSize(){
		return eeAbstract;
	}

	final public int getApproxMinMaxBasisLeafSize(){
		return abLeaf;
	}

	final public int getApproxMinMaxBasisAbstractSize(){
		return abAbstract;
	}

	final public int getApproxLeafSize(){
		return aeLeaf;
	}

	final public int getApproxAbstractSize(){
		return aeAbstract;
	}
	
	final public void updateNodeFreqs(int index, int ruleSet, int p){
		int[][] f = (int[][])disCounts[index];
		if (ruleSet == 1){
			//EB
			f[0][0] = f[0][0] + 1;
			if (p == 1){
				//A
				f[1][0] = f[1][0] + 1;
			}
			else{
				//C
				f[2][0] = f[2][0] + 1;
			}
		}
		else if (ruleSet == 2){
			//EE
			f[0][1] = f[0][1] + 1;
			if (p == 1){
				//A
				f[1][1] = f[1][1] + 1;
			}
			else{
				//C
				f[2][1] = f[2][1] + 1;
			}
		}
		else if (ruleSet == 3){
			//AB
			f[0][2] = f[0][2] + 1;
			if (p == 1){
				//A
				f[1][2] = f[1][2] + 1;
			}
			else{
				//C
				f[2][2] = f[2][2] + 1;
			}
		}
		else{
			//AE
			f[0][3] = f[0][3] + 1;
			if (p == 1){
				//A
				f[1][3] = f[1][3] + 1;
			}
			else{
				//C
				f[2][3] = f[2][3] + 1;
			}
		}
		disCounts[index] = f;
	}
	
	final public Object[] getNodeFreqs(){
		return disCounts;
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
		exactMinMaxBasis.clear();
		exactMinMaxBasisSup.clear();
		exactMinMaxBasisCov.clear();
		exactMinMaxBasisDiv.clear();
		exactMinMaxBasisDis.clear();

		exactAll.clear();
		exactAllSup.clear();
		exactAllCov.clear();
		exactAllDiv.clear();
		exactAllDis.clear();

		approxMinMaxBasis.clear();
		approxMinMaxBasisSup.clear();
		approxMinMaxBasisCon.clear();
		approxMinMaxBasisCov.clear();
		approxMinMaxBasisDiv.clear();
		approxMinMaxBasisDis.clear();

		approxAll.clear();
		approxAllSup.clear();
		approxAllCon.clear();
		approxAllCov.clear();
		approxAllDiv.clear();
		approxAllDis.clear();
	}

	final public void printRules(){
		System.out.println("Exact Basis Rules...");
		for (int i = 0; i < exactMinMaxBasis.size(); i++){
			System.out.println((int[])((Object[])exactMinMaxBasis.get(i))[0] + " ==> " + (int[])((Object[])exactMinMaxBasis.get(i))[1]);
		}
		System.out.println("");
		System.out.println("Exact All Rules...");
		for (int i = 0; i < exactAll.size(); i++){
			System.out.println((int[])((Object[])exactAll.get(i))[0] + " ==> " + (int[])((Object[])exactAll.get(i))[1]);
		}
		System.out.println("");
		System.out.println("Approx Basis Rules...");
		for (int i = 0; i < approxMinMaxBasis.size(); i++){
			System.out.println((int[])((Object[])approxMinMaxBasis.get(i))[0] + " ==> " + (int[])((Object[])approxMinMaxBasis.get(i))[1]);
		}
		System.out.println("");
		System.out.println("Approx All Rules...");
		for (int i = 0; i < approxAll.size(); i++){
			System.out.println((int[])((Object[])approxAll.get(i))[0] + " ==> " + (int[])((Object[])approxAll.get(i))[1]);
		}
	}
}