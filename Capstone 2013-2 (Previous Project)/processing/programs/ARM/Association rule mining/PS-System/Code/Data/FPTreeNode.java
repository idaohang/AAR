/**
 * FPTreeNode class.
 * 
 * Start Date: 2 April 2008
 * @author Gavin Shaw
 * @version 1.0
 *
 * Log:
 *		  1.0 Start version of class.
 */
package Data;

import java.util.ArrayList;

import Data.FPTreeNode;

public class FPTreeNode{

	private String value;

	private ArrayList cValue, children;

	private FPTreeNode nodelink;

	private int count;

	private float support;

	/**
	 * FPTreeNode method.
	 * Constructor.
	 */
	public FPTreeNode(){
		cValue = new ArrayList();
		children = new ArrayList();
		nodelink = null;
	}

	public FPTreeNode(String v, int c, int t){
		value = v;
		count = c;
		support = (float)c / (float)t;
		cValue = new ArrayList();
		children = new ArrayList();
		nodelink = null;
	}

	final public void setValue(String id){
		value = id;
	}

	final public String getValue(){
		return value;
	}

	final public void setCount(int c){
		count = c;
	}

	final public void incCount(){
		count = count + 1;
	}

	final public int getCount(){
		return count;
	}

	final public void setSupport(float s){
		support = s;
	}

	final public void updateSupport(int trans){
		support = (float)count / (float)trans;
	}

	final public float getSupport(){
		return support;
	}

	final public void addChild(String value, int count, int trans){
		FPTreeNode child = new FPTreeNode(value, count, trans);
		children.add(child);
		cValue.add(value);
	}

	final public boolean childPresent(String value){
		if (cValue.indexOf(value) != -1){
			return true;
		}
		else{
			return false;
		}
	}

	final public FPTreeNode getChild(String value){
		return (FPTreeNode)children.get(cValue.indexOf(value));
	}

	final public int getNoChildren(){
		return children.size();
	}

	final protected void addNodeLink(FPTreeNode node){
		if (nodelink == null){
			nodelink = node;
		}
		else{
			nodelink.addNodeLink(node);
		}
	}

	final public void print(){
		System.out.println("Value: " + value);
		System.out.println("Count: " + count);
		System.out.println("Supp:  " + support);
		if (nodelink != null){
			System.out.println("Link:  " + nodelink.getValue() + "  " + nodelink.getCount() + "  " + nodelink.getSupport());
		}
	}
}