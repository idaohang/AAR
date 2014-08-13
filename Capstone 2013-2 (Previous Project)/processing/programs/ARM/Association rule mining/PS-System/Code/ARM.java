/**
 * ARM class.
 * 
 * Start Date: 01 December 2006
 * @author Gavin Shaw
 * @version 1.0
 *
 * Log:
 *		  1.0 Start version of class.
 */

import javax.swing.UIManager;

import GUI.ARMMainGui;
import GUI.ARMMainNoGui;

public class ARM{

	private ARMMainGui mgui;

	private ARMMainNoGui mnogui;

	public ARM(){
		mgui = new ARMMainGui();
	}

	public ARM(String[] args){
		mnogui = new ARMMainNoGui(args);
	}

	/**
	 * main method.
	 * Start point for the ARM program/system.
	 */
	public static void main(String args[]){
		if (args.length == 11){
			//Non-Gui version...
			new ARM(args);
		}
		else{
			//Gui version...
			try{
				UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
			}
			catch (Exception e){
			}
			new ARM();
		}
	}
}