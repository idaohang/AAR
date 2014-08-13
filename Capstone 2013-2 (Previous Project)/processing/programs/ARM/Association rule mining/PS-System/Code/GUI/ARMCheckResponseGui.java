/**
 * ARMCheckResponseGui class.
 * 
 * Start Date: 04 December 2006
 * @author Gavin Shaw
 * @version 1.0
 *
 * Log:
 *		  1.0 Start version of class.
 */
package GUI;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import GUI.ARMMainGui;

public class ARMCheckResponseGui extends JFrame implements ActionListener{

	ARMMainGui owner;

	private JPanel trcPanel, iacPanel, mavPanel, iavPanel, buttonPanel;

	private JLabel trcLabel, tacLabel, dacLabel, macLabel, lacLabel, miacLabel, mracLabel, iacLabel, iracLabel;

	private JButton okayButton;

	private LineBorder lBorder = new LineBorder(Color.black, 1);

	private Font standardFont = new Font ("SansSerif", Font.PLAIN, 12);
	private Font largeFont = new Font ("SansSerif", Font.BOLD, 14);

	private int trc, tac;

	private int dac, mac, lac;

	private int miac, mrac;

	private int iac, irac;

	/**
	 * ARMCheckResponseGui method.
	 * Constructor.
	 * Method used to intialise and setup the GUI that displays the results
	 * of reading/loading a file into the system.
	 */
	public ARMCheckResponseGui(ARMMainGui main, int totalRecordCount, int totalAttCount, int diffAttCount,
										int moreAttCount, int lessAttCount, int missingAttCount, int mrAttCount,
										int invalidAttCount, int irAttCount){
		owner = main;
		trc = totalRecordCount;
		tac = totalAttCount;
		dac = diffAttCount;
		mac = moreAttCount;
		lac = lessAttCount;
		miac = missingAttCount;
		mrac = mrAttCount;
		iac = invalidAttCount;
		irac = irAttCount;

		Container container = getContentPane();
		container.setBackground(Color.lightGray);
		container.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 7));
		this.setTitle("Input Record Check Report");

		setupTotalRecCountPanel();
		setupIncorrectAttCountPanel();
		setupMissingAttValuesPanel();
		setupInvalidAttValuesPanel();
		setupButtonPanel();
		container.add(trcPanel);
		container.add(iacPanel);
		container.add(mavPanel);
		container.add(iavPanel);
		container.add(buttonPanel);

		Toolkit theKit = this.getToolkit();
		Dimension windowSize = theKit.getScreenSize();
		setSize(500, 425);
		setLocation(new Point(0, 0));
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);
		owner.setEnabled(false);
	}

	/**
	 * setupTotalRecCountPanel method.
	 * Method that sets up the panel that displays information on
	 * the number of transactions/records and items/attributes in the dataset.
	 */
	final private void setupTotalRecCountPanel(){
		trcPanel = new JPanel();
		trcPanel.setBackground(Color.lightGray);
		trcPanel.setPreferredSize(new Dimension(485, 65));
		trcPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		trcPanel.setBorder(lBorder);

		trcLabel = new JLabel("  Total number of records:  " + trc);
		trcLabel.setFont(largeFont);
		trcLabel.setBackground(Color.lightGray);
		trcLabel.setPreferredSize(new Dimension(425, 25));

		tacLabel = new JLabel("  Total number of items/attributes:  " + tac);
		tacLabel.setFont(largeFont);
		tacLabel.setBackground(Color.lightGray);
		tacLabel.setPreferredSize(new Dimension(425, 25));

		trcPanel.add(trcLabel);
		trcPanel.add(tacLabel);
	}

	/**
	 * setupIncorrectAttCountPanel method.
	 * Method that sets up the panel that displays information on
	 * the number of transactions/records that contained the wrong number
	 * of items/attributes.
	 */
	final private void setupIncorrectAttCountPanel(){
		iacPanel = new JPanel();
		iacPanel.setBackground(Color.lightGray);
		iacPanel.setPreferredSize(new Dimension(485, 100));
		iacPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		iacPanel.setBorder(lBorder);

		dacLabel = new JLabel("  Total number of records with incorrect number of items/attributes:  " + dac);
		dacLabel.setFont(standardFont);
		dacLabel.setBackground(Color.lightGray);
		dacLabel.setPreferredSize(new Dimension(425, 25));

		macLabel = new JLabel("  Total number of records with too many (extra) items/attributes:  " + mac);
		macLabel.setFont(standardFont);
		macLabel.setBackground(Color.lightGray);
		macLabel.setPreferredSize(new Dimension(425, 25));

		lacLabel = new JLabel("  Total number of records with too few (missing) items/attributes:  " + lac);
		lacLabel.setFont(standardFont);
		lacLabel.setBackground(Color.lightGray);
		lacLabel.setPreferredSize(new Dimension(425, 25));

		iacPanel.add(dacLabel);
		iacPanel.add(macLabel);
		iacPanel.add(lacLabel);
	}

	/**
	 * setupMissingAttValuesPanel method.
	 * Method that sets up the panel that displays information on
	 * the number of transactions/records that contained missing
	 * item/attribute information and the total number of
	 * items/attributes that had missing values in the dataset.
	 */
	final private void setupMissingAttValuesPanel(){
		mavPanel = new JPanel();
		mavPanel.setBackground(Color.lightGray);
		mavPanel.setPreferredSize(new Dimension(485, 70));
		mavPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		mavPanel.setBorder(lBorder);

		miacLabel = new JLabel("  Total number of missing/undefined items/attribute values:  " + miac);
		miacLabel.setFont(standardFont);
		miacLabel.setBackground(Color.lightGray);
		miacLabel.setPreferredSize(new Dimension(425, 25));

		mracLabel = new JLabel("  Total number of records with missing/undefined items/attribute values:  " + mrac);
		mracLabel.setFont(standardFont);
		mracLabel.setBackground(Color.lightGray);
		mracLabel.setPreferredSize(new Dimension(425, 25));

		mavPanel.add(miacLabel);
		mavPanel.add(mracLabel);
	}

	/**
	 * setupInvalidAttValuesPanel method.
	 * Method that sets up the panel that displays information on
	 * the number of transactions/records with invalid
	 * item/attribute values and the total number of items/attributes
	 * that had invalid values in the dataset.
	 */
	final private void setupInvalidAttValuesPanel(){
		iavPanel = new JPanel();
		iavPanel.setBackground(Color.lightGray);
		iavPanel.setPreferredSize(new Dimension(485, 70));
		iavPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		iavPanel.setBorder(lBorder);

		iacLabel = new JLabel("  Total number of invalid items/attribute values:  " + iac);
		iacLabel.setFont(standardFont);
		iacLabel.setBackground(Color.lightGray);
		iacLabel.setPreferredSize(new Dimension(425, 25));

		iracLabel = new JLabel("  Total number of records with invalid items/attribute values:  " + irac);
		iracLabel.setFont(standardFont);
		iracLabel.setBackground(Color.lightGray);
		iracLabel.setPreferredSize(new Dimension(425, 25));

		iavPanel.add(iacLabel);
		iavPanel.add(iracLabel);
	}

	/**
	 * setupButtonPanel method.
	 * Method used to setup the panel that holds the button allowing
	 * the user to leave this GUI and go back to the system's main GUI.
	 */
	final private void setupButtonPanel(){
		buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.lightGray);
		buttonPanel.setPreferredSize(new Dimension(485, 45));
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
		buttonPanel.setBorder(lBorder);

		okayButton = new JButton("Okay");
		okayButton.setBackground(Color.lightGray);
		okayButton.setForeground(Color.black);
		okayButton.setFont(standardFont);
		okayButton.setPreferredSize(new Dimension(175, 25));
		okayButton.addActionListener(this);

		buttonPanel.add(okayButton);
	}

	/**
	 * actionPerformed method.
	 * Method used to respond to user input/action.
	 */
	final public void actionPerformed(ActionEvent event){
		if (event.getSource() == okayButton){
			owner.setEnabled(true);
			this.dispose();
		}
	}
}