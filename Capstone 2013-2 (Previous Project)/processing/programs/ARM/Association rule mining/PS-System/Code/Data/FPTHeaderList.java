/**
 * FPTHeaderList class.
 * 
 * Start Date: 5 April 2008
 * @author Gavin Shaw
 * @version 1.0
 *
 * Log:
 *		  1.0 Start version of class.
 */
package Data;

import java.util.ArrayList;

import Data.FPTreeNode;

public class FPTHeaderList{

	private ArrayList headerList, nodeList;

	public FPTHeaderList(){
		headerList = new ArrayList();
		nodeList = new ArrayList();
	}

	final public void addItem(String value){
		headerList.add(value);
		addNode();
	}

	final public void addItem(int index, String value){
		headerList.add(index, value);
		addNode();
	}

	final public void addItems(ArrayList items){
	}

	final public void addNode(FPTreeNode node){
		nodeList.add(node);
	}

	final public void addNode(FPTreeNode node, String value){
		if (nodeList.get(headerList.indexOf(value)) == null){
			nodeList.set(headerList.indexOf(value), node);
		}
		else{
			((FPTreeNode)nodeList.get(headerList.indexOf(value))).addNodeLink(node);
		}
	}

	final public void addNode(){
		nodeList.add(null);
	}

	final public ArrayList getItems(){
		return headerList;
	}

	final public void print(){
		System.out.println("Size of header table: " + headerList.size());
		for (int i = 0; i < headerList.size(); i++){
			System.out.println("ID: " + (String)headerList.get(i));
			((FPTreeNode)nodeList.get(i)).print();
		}
	}
}