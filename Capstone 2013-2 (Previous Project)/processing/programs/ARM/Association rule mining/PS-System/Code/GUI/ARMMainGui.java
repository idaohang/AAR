/**
 * ARMMainGui class.
 * 
 * Start Date: 01 December 2006
 * @author Gavin Shaw
 * @version 1.0
 *
 * Log:
 *		  1.0 Start version of class.
 */
package GUI;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.util.ArrayList;
import java.util.Vector;

import GUI.SetMinSupGui;
import GUI.ViewInputDataGui;
import GUI.ViewFrequentItemsetGui;
import GUI.ViewFrequentClosedItemsetGui;
import GUI.ViewAssociationRulesGui;
import GUI.ViewEvaluationGui;

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

public class ARMMainGui extends JFrame implements ActionListener, ItemListener{

	private SetMinSupGui smsg;

	private ViewInputDataGui vidg;

	private ViewFrequentItemsetGui vfig;

	private ViewFrequentClosedItemsetGui vfcig;

	private ViewAssociationRulesGui varg;
	
	private ViewEvaluationGui veg;

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

	private Vector fisAlgorithms, cisAlgorithms, arsAlgorithms;

	private JMenuBar menuBar;

	private JPanel dataSetupPanel, csPanel, fiaPanel, fciaPanel, areaPanel, systemStatusPanel;

	private JLabel spacer1, dataSelectLabel, spacer2, spacer3, spacer4, minConfidenceLabel, minSupportLabel, spacer5, spacer5a, spacer6, spacer6a, spacer6b, noticeLabel;

	private JLabel fiaLabel, spacer7, spacer8, fciaLabel, spacer9, spacer10, areaLabel, spacer11, spacer12;

	private JButton dataBrowseButton, dataconButton, dataCCButton, vcdButton, setLevelSupportButton, batchfciButton;

	private JButton lfiButton, bfiButton, vfiButton, sfiButton, lfciButton, bfciButton, vfciButton, sfciButton, earButton, varButton, evalARButton, sarButton;

	private JTextField dataFilePathField, minConfidenceField, minSupportField, systemStatusField;

	private JCheckBox ebBox, eeBox, abBox, aeBox;

	private JFileChooser sourceDataFile, destDataFile, fiFile;

	private JComboBox fiaBox, fciaBox, areaBox;

	private LineBorder lBorder = new LineBorder(Color.black, 1);

	private Font standardFont = new Font ("SansSerif", Font.PLAIN, 12);

	private long processTimeBF, processTimeBFC, processTimeAR;

	private int attCount = 0;

	private boolean loadedFI = false, loadedFCI = false, multiLevel = false, nonderivableRun = false;

	private ArrayList msl;
	
	private Object[] attNames, attLeaf;

	/**
	 * ARMMainGui method.
	 * Constructor.
	 * Oversees the setup of the main GUI to allow user interaction with the system.
	 */
	public ARMMainGui(){
		Container container = getContentPane();
		container.setBackground(Color.lightGray);
		container.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));
		this.setTitle("Association Rule Miner System");
		setupMenuBar();
		this.setJMenuBar(menuBar);

		fisAlgorithms = new Vector();
		fisAlgorithms.add("Apriori Algorithm");
//		fisAlgorithms.add("Modified Apriori Algorithm");
//		fisAlgorithms.add("Modified Weighted Apriori Algorithm");
//		fisAlgorithms.add("Cross-Level Algorithm");
//		fisAlgorithms.add("Modified Cross-Level Algorithm (No Prune)");
//		fisAlgorithms.add("ML_T2L1 - Han & Fu");
//		fisAlgorithms.add("ML_T2L1 (With Cross Level) - Han & Fu");
//		fisAlgorithms.add("Frequent Pattern Tree (FP-Tree) Algorithm");
//		fisAlgorithms.add("Rapid Association Rule Mining (RARM) - N/A");
//		fisAlgorithms.add("Modified-2 Apriori Algorithm");
		fisAlgorithms.add("Non-Derivable Itemset Algorithm - Calders & Goethals");

		cisAlgorithms = new Vector();
		cisAlgorithms.add("Pasquier's CLOSE+ Algorithm");
//		cisAlgorithms.add("Yue Xu's ClosureGen Algorithm");
//		cisAlgorithms.add("Modified CLOSE+ Algorithm");

		arsAlgorithms = new Vector();
		arsAlgorithms.add("Min-max Association Rules");
//		arsAlgorithms.add("Min-max Association Rules - Diversity");
//		arsAlgorithms.add("Min-max Association Rules - Coverage");
//		arsAlgorithms.add("Min-max Association Rules with HRR");
//		arsAlgorithms.add("Min-max Association Rules with HRR - Diversity");
//		arsAlgorithms.add("Min-max Association Rules with HRR - Coverage");
		arsAlgorithms.add("ReliableExactRule");
//		arsAlgorithms.add("ReliableExactRule - Diversity");
//		arsAlgorithms.add("ReliableExactRule - Coverage");
//		arsAlgorithms.add("ReliableExactRule with HRR");
//		arsAlgorithms.add("ReliableExactRule with HRR - Diversity");
//		arsAlgorithms.add("ReliableExactRule with HRR - Coverage");
//		arsAlgorithms.add("ReliableExactRule - 2");
		arsAlgorithms.add("Non-Derivable Rules - Goethals et.");

		setupInputPanel();
		setupCSPanel();
		setupFIAPanel();
		setupFCIAPanel();
		setupAREAPanel();
		setupStatusPanel();
		container.add(dataSetupPanel);
		container.add(csPanel);
		container.add(fiaPanel);
		container.add(fciaPanel);
		container.add(areaPanel);
		container.add(systemStatusPanel);

		Toolkit theKit = this.getToolkit();
		Dimension windowSize = theKit.getScreenSize();
		setSize(1024, 768);
		setLocation(new Point(0, 0));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);
	}

	/**
	 * setupMenuBar method.
	 * Method used to setup the menu bar in the system's main GUI.
	 */
	final private void setupMenuBar(){
		menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
		JMenu helpMenu = new JMenu("Help");

		JMenuItem exitSys = new JMenuItem("Exit");
		exitSys.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				System.exit(0);
			}
		});
		fileMenu.add(exitSys);

		JMenuItem help = new JMenuItem("How To Use");
		help.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){

			}
		});
		helpMenu.add(help);

		menuBar.add(fileMenu);
		menuBar.add(helpMenu);
	}

	/**
	 * setupInputPanel method.
	 * Method that sets up the first panel in the main GUI which allows the user
	 * to specify which data file is to be used and read in or converted to binary
	 * format.
	 */
	final private void setupInputPanel(){
		dataSetupPanel = new JPanel();
		dataSetupPanel.setBackground(Color.lightGray);
		dataSetupPanel.setPreferredSize(new Dimension(1015, 120));
		dataSetupPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
		dataSetupPanel.setBorder(lBorder);

		spacer1 = new JLabel("");
		spacer1.setBackground(Color.lightGray);
		spacer1.setPreferredSize(new Dimension(250, 25));

		dataconButton = new JButton("Convert Multi-Dimensional Data File To Binary Format Data File");
		dataconButton.setBackground(Color.lightGray);
		dataconButton.setForeground(Color.black);
		dataconButton.setFont(standardFont);
		dataconButton.setPreferredSize(new Dimension(400, 25));
		dataconButton.addActionListener(this);

		spacer2 = new JLabel("");
		spacer2.setBackground(Color.lightGray);
		spacer2.setPreferredSize(new Dimension(250, 25));

		spacer3 = new JLabel("");
		spacer3.setBackground(Color.lightGray);
		spacer3.setPreferredSize(new Dimension(100, 25));

		dataSelectLabel = new JLabel("  Selected Data File (*.data)  ");
		dataSelectLabel.setFont(standardFont);
		dataSelectLabel.setBackground(Color.lightGray);
		dataSelectLabel.setPreferredSize(new Dimension(155, 25));

		dataFilePathField = new JTextField("");
		dataFilePathField.setFont(standardFont);
		dataFilePathField.setBackground(Color.white);
		dataFilePathField.setForeground(Color.black);
		dataFilePathField.setPreferredSize(new Dimension(500, 25));

		dataBrowseButton = new JButton("Browse");
		dataBrowseButton.setBackground(Color.lightGray);
		dataBrowseButton.setForeground(Color.black);
		dataBrowseButton.setFont(standardFont);
		dataBrowseButton.setPreferredSize(new Dimension(100, 25));
		dataBrowseButton.addActionListener(this);

		spacer4 = new JLabel("");
		spacer4.setBackground(Color.lightGray);
		spacer4.setPreferredSize(new Dimension(100, 25));

		dataCCButton = new JButton("Data Checker / Cleaner");
		dataCCButton.setBackground(Color.lightGray);
		dataCCButton.setForeground(Color.black);
		dataCCButton.setFont(standardFont);
		dataCCButton.setPreferredSize(new Dimension(175, 25));
		dataCCButton.addActionListener(this);

		vcdButton = new JButton("View Cleaned Data");
		vcdButton.setBackground(Color.lightGray);
		vcdButton.setForeground(Color.black);
		vcdButton.setFont(standardFont);
		vcdButton.setPreferredSize(new Dimension(175, 25));
		vcdButton.addActionListener(this);

		dataSetupPanel.add(spacer1);
		dataSetupPanel.add(dataconButton);
		dataSetupPanel.add(spacer2);
		dataSetupPanel.add(spacer3);
		dataSetupPanel.add(dataSelectLabel);
		dataSetupPanel.add(dataFilePathField);
		dataSetupPanel.add(dataBrowseButton);
		dataSetupPanel.add(spacer4);
		dataSetupPanel.add(dataCCButton);
		dataSetupPanel.add(vcdButton);
	}

	/**
	 * setupCSPanel method.
	 * Method used to setup the panel that allows the user to specify the min
	 * support and confedence values which are used when building frequent
	 * itemsets, frequent closed itemsets and association rules.
	 */
	final private void setupCSPanel(){
		csPanel = new JPanel();
		csPanel.setBackground(Color.lightGray);
		csPanel.setPreferredSize(new Dimension(1015, 225));
		csPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
		csPanel.setBorder(lBorder);

		spacer5 = new JLabel("");
		spacer5.setBackground(Color.lightGray);
		spacer5.setPreferredSize(new Dimension(300, 25));

		minConfidenceLabel = new JLabel("  Uniform Minimum Confidence:  ");
		minConfidenceLabel.setFont(standardFont);
		minConfidenceLabel.setBackground(Color.lightGray);
		minConfidenceLabel.setPreferredSize(new Dimension(200, 25));

		minConfidenceField = new JTextField("0.5");
		minConfidenceField.setFont(standardFont);
		minConfidenceField.setBackground(Color.white);
		minConfidenceField.setForeground(Color.black);
		minConfidenceField.setPreferredSize(new Dimension(100, 25));

		spacer5a = new JLabel("");
		spacer5a.setBackground(Color.lightGray);
		spacer5a.setPreferredSize(new Dimension(300, 25));

		spacer6 = new JLabel("");
		spacer6.setBackground(Color.lightGray);
		spacer6.setPreferredSize(new Dimension(250, 25));

		minSupportLabel = new JLabel("  Uniform Minimum Support:  ");
		minSupportLabel.setFont(standardFont);
		minSupportLabel.setBackground(Color.lightGray);
		minSupportLabel.setPreferredSize(new Dimension(200, 25));

		minSupportField = new JTextField("0.5");
		minSupportField.setFont(standardFont);
		minSupportField.setBackground(Color.white);
		minSupportField.setForeground(Color.black);
		minSupportField.setPreferredSize(new Dimension(100, 25));

		setLevelSupportButton = new JButton("Set Minimum Support For Each Level");
		setLevelSupportButton.setBackground(Color.lightGray);
		setLevelSupportButton.setForeground(Color.black);
		setLevelSupportButton.setFont(standardFont);
		setLevelSupportButton.setPreferredSize(new Dimension(250, 25));
//		setLevelSupportButton.addActionListener(this);

		spacer6a = new JLabel("");
		spacer6a.setBackground(Color.lightGray);
		spacer6a.setPreferredSize(new Dimension(250, 25));

		ebBox = new JCheckBox("Extract Exact Basis Rules", true);
		ebBox.setFont(standardFont);
		ebBox.setBackground(Color.lightGray);
		ebBox.setPreferredSize(new Dimension(200, 25));
		ebBox.addItemListener(this);
		
		eeBox = new JCheckBox("Extract Extended Basis Rules", false);
		eeBox.setFont(standardFont);
		eeBox.setBackground(Color.lightGray);
		eeBox.setPreferredSize(new Dimension(200, 25));
		eeBox.addItemListener(this);

		spacer6b = new JLabel("");
		spacer6b.setBackground(Color.lightGray);
		spacer6b.setPreferredSize(new Dimension(250, 25));

		abBox = new JCheckBox("Extract Approx Basis Rules", true);
		abBox.setFont(standardFont);
		abBox.setBackground(Color.lightGray);
		abBox.setPreferredSize(new Dimension(200, 25));
		abBox.addItemListener(this);
		
		aeBox = new JCheckBox("Extract Extended Approx Rules", false);
		aeBox.setFont(standardFont);
		aeBox.setBackground(Color.lightGray);
		aeBox.setPreferredSize(new Dimension(200, 25));
		aeBox.addItemListener(this);

		noticeLabel = new JLabel("(Please select an Association Rule Extractor Algorithm from below before using the Multi-Source feature)", JLabel.CENTER);
		noticeLabel.setBackground(Color.lightGray);
		noticeLabel.setFont(standardFont);
		noticeLabel.setPreferredSize(new Dimension(600, 25));

		batchfciButton = new JButton("Multi-Source Load Frequent Closed Itemsets & Extract Association Rules");
		batchfciButton.setBackground(Color.lightGray);
		batchfciButton.setForeground(Color.black);
		batchfciButton.setFont(standardFont);
		batchfciButton.setPreferredSize(new Dimension(450, 25));
//		batchfciButton.addActionListener(this);

		csPanel.add(spacer5);
		csPanel.add(minConfidenceLabel);
		csPanel.add(minConfidenceField);
		csPanel.add(spacer5a);
		csPanel.add(spacer6);
		csPanel.add(minSupportLabel);
		csPanel.add(minSupportField);
		csPanel.add(setLevelSupportButton);
		csPanel.add(spacer6a);
		csPanel.add(ebBox);
		csPanel.add(eeBox);
		csPanel.add(spacer6b);
		csPanel.add(abBox);
		csPanel.add(aeBox);
		csPanel.add(noticeLabel);
		csPanel.add(batchfciButton);
	}

	/**
	 * setupFIAPanel method.
	 * Method which sets up the panel that allows the user to instruct the system to
	 * determine the frequent itemsets. Allows them to choose the algorithm used, to view
	 * the candidates and frequent itemsets and save the list to an external file.
	 */
	final private void setupFIAPanel(){
		fiaPanel = new JPanel();
		fiaPanel.setBackground(Color.lightGray);
		fiaPanel.setPreferredSize(new Dimension(1015, 90));
		fiaPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
		fiaPanel.setBorder(lBorder);

		spacer7 = new JLabel("");
		spacer7.setBackground(Color.lightGray);
		spacer7.setPreferredSize(new Dimension(250, 25));

		fiaLabel = new JLabel("  Select Frequent Itemsets Algorithm  ");
		fiaLabel.setFont(standardFont);
		fiaLabel.setBackground(Color.lightGray);
		fiaLabel.setPreferredSize(new Dimension(210, 25));

		fiaBox = new JComboBox(fisAlgorithms);
		fiaBox.setBackground(Color.lightGray);
		fiaBox.setForeground(Color.black);
		fiaBox.setFont(standardFont);
		fiaBox.setPreferredSize(new Dimension(275, 25));
		fiaBox.setEditable(false);

		spacer8 = new JLabel("");
		spacer8.setBackground(Color.lightGray);
		spacer8.setPreferredSize(new Dimension(250, 25));

		lfiButton = new JButton("Load Frequent Itemsets");
		lfiButton.setBackground(Color.lightGray);
		lfiButton.setForeground(Color.black);
		lfiButton.setFont(standardFont);
		lfiButton.setPreferredSize(new Dimension(175, 25));
//		lfiButton.addActionListener(this);

		bfiButton = new JButton("Build Frequent Itemsets");
		bfiButton.setBackground(Color.lightGray);
		bfiButton.setForeground(Color.black);
		bfiButton.setFont(standardFont);
		bfiButton.setPreferredSize(new Dimension(175, 25));
		bfiButton.addActionListener(this);

		vfiButton = new JButton("View Frequent Itemsets");
		vfiButton.setBackground(Color.lightGray);
		vfiButton.setForeground(Color.black);
		vfiButton.setFont(standardFont);
		vfiButton.setPreferredSize(new Dimension(175, 25));
		vfiButton.addActionListener(this);

		sfiButton = new JButton("Save Frequent Itemsets");
		sfiButton.setBackground(Color.lightGray);
		sfiButton.setForeground(Color.black);
		sfiButton.setFont(standardFont);
		sfiButton.setPreferredSize(new Dimension(175, 25));
		sfiButton.addActionListener(this);

		fiaPanel.add(spacer7);
		fiaPanel.add(fiaLabel);
		fiaPanel.add(fiaBox);
		fiaPanel.add(spacer8);
		fiaPanel.add(lfiButton);
		fiaPanel.add(bfiButton);
		fiaPanel.add(vfiButton);
		fiaPanel.add(sfiButton);
	}

	/**
	 * setupFCIAPanel method.
	 * Method which sets up the panel that allows the user to instruct the system to
	 * determine the frequent closed itemsets. Allows them to choose the algorithm used, to view
	 * the generators and frequent closed itemsets and save the list to an external file.
	 */
	final private void setupFCIAPanel(){
		fciaPanel = new JPanel();
		fciaPanel.setBackground(Color.lightGray);
		fciaPanel.setPreferredSize(new Dimension(1015, 90));
		fciaPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
		fciaPanel.setBorder(lBorder);

		spacer9 = new JLabel("");
		spacer9.setBackground(Color.lightGray);
		spacer9.setPreferredSize(new Dimension(250, 25));

		fciaLabel = new JLabel("  Select Frequent Closed Itemsets Algorithm  ");
		fciaLabel.setFont(standardFont);
		fciaLabel.setBackground(Color.lightGray);
		fciaLabel.setPreferredSize(new Dimension(250, 25));

		fciaBox = new JComboBox(cisAlgorithms);
		fciaBox.setBackground(Color.lightGray);
		fciaBox.setForeground(Color.black);
		fciaBox.setFont(standardFont);
		fciaBox.setPreferredSize(new Dimension(225, 25));
		fciaBox.setEditable(false);

		spacer10 = new JLabel("");
		spacer10.setBackground(Color.lightGray);
		spacer10.setPreferredSize(new Dimension(250, 25));

		lfciButton = new JButton("Load Frequent Closed Itemsets");
		lfciButton.setBackground(Color.lightGray);
		lfciButton.setForeground(Color.black);
		lfciButton.setFont(standardFont);
		lfciButton.setPreferredSize(new Dimension(225, 25));
//		lfciButton.addActionListener(this);

		bfciButton = new JButton("Build Frequent Closed Itemsets");
		bfciButton.setBackground(Color.lightGray);
		bfciButton.setForeground(Color.black);
		bfciButton.setFont(standardFont);
		bfciButton.setPreferredSize(new Dimension(225, 25));
		bfciButton.addActionListener(this);

		vfciButton = new JButton("View Frequent Closed Itemsets");
		vfciButton.setBackground(Color.lightGray);
		vfciButton.setForeground(Color.black);
		vfciButton.setFont(standardFont);
		vfciButton.setPreferredSize(new Dimension(225, 25));
		vfciButton.addActionListener(this);

		sfciButton = new JButton("Save Frequent Closed Itemsets");
		sfciButton.setBackground(Color.lightGray);
		sfciButton.setForeground(Color.black);
		sfciButton.setFont(standardFont);
		sfciButton.setPreferredSize(new Dimension(225, 25));
		sfciButton.addActionListener(this);

		fciaPanel.add(spacer9);
		fciaPanel.add(fciaLabel);
		fciaPanel.add(fciaBox);
		fciaPanel.add(spacer10);
		fciaPanel.add(lfciButton);
		fciaPanel.add(bfciButton);
		fciaPanel.add(vfciButton);
		fciaPanel.add(sfciButton);
	}

	/**
	 * setupAREAPanel method.
	 * Method which sets up the panel that allows the user to instruct the system to
	 * extract the association rules. Allows them to choose the algorithm used, to view
	 * the exact and approximate rules and save the lists to an external file.
	 */
	final private void setupAREAPanel(){
		areaPanel = new JPanel();
		areaPanel.setBackground(Color.lightGray);
		areaPanel.setPreferredSize(new Dimension(1015, 90));
		areaPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
		areaPanel.setBorder(lBorder);

		spacer11 = new JLabel("");
		spacer11.setBackground(Color.lightGray);
		spacer11.setPreferredSize(new Dimension(220, 25));

		areaLabel = new JLabel("  Select Association Rule Extractor Algorithm  ");
		areaLabel.setFont(standardFont);
		areaLabel.setBackground(Color.lightGray);
		areaLabel.setPreferredSize(new Dimension(250, 25));

		areaBox = new JComboBox(arsAlgorithms);
		areaBox.setBackground(Color.lightGray);
		areaBox.setForeground(Color.black);
		areaBox.setFont(standardFont);
		areaBox.setPreferredSize(new Dimension(300, 25));
		areaBox.setEditable(false);

		spacer12 = new JLabel("");
		spacer12.setBackground(Color.lightGray);
		spacer12.setPreferredSize(new Dimension(220, 25));

		earButton = new JButton("Extract Association Rules");
		earButton.setBackground(Color.lightGray);
		earButton.setForeground(Color.black);
		earButton.setFont(standardFont);
		earButton.setPreferredSize(new Dimension(200, 25));
		earButton.addActionListener(this);

		varButton = new JButton("View Association Rules");
		varButton.setBackground(Color.lightGray);
		varButton.setForeground(Color.black);
		varButton.setFont(standardFont);
		varButton.setPreferredSize(new Dimension(200, 25));
		varButton.addActionListener(this);
		
		evalARButton = new JButton("Evaluate Association Rules");
		evalARButton.setBackground(Color.lightGray);
		evalARButton.setForeground(Color.black);
		evalARButton.setFont(standardFont);
		evalARButton.setPreferredSize(new Dimension(200, 25));
		evalARButton.addActionListener(this);

		sarButton = new JButton("Save Association Rules");
		sarButton.setBackground(Color.lightGray);
		sarButton.setForeground(Color.black);
		sarButton.setFont(standardFont);
		sarButton.setPreferredSize(new Dimension(200, 25));
		sarButton.addActionListener(this);

		areaPanel.add(spacer11);
		areaPanel.add(areaLabel);
		areaPanel.add(areaBox);
		areaPanel.add(spacer12);
		areaPanel.add(earButton);
		areaPanel.add(varButton);
		areaPanel.add(evalARButton);
		areaPanel.add(sarButton);
	}

	/**
	 * setupStatusPanel method.
	 * Method that sets up the panel that is used to pass messages to the
	 * user while the system is running to keep them informed about what is
	 * happening.
	 */
	final private void setupStatusPanel(){
		systemStatusPanel = new JPanel();
		systemStatusPanel.setBackground(Color.lightGray);
		systemStatusPanel.setPreferredSize(new Dimension(1015, 40));
		systemStatusPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		systemStatusPanel.setBorder(lBorder);

		systemStatusField = new JTextField("");
		systemStatusField.setFont(standardFont);
		systemStatusField.setBackground(Color.lightGray);
		systemStatusField.setForeground(Color.black);
		systemStatusField.setEditable(false);
		systemStatusField.setBorder(null);
		systemStatusField.setHorizontalAlignment(JTextField.CENTER);
		systemStatusField.setPreferredSize(new Dimension(750, 25));

		systemStatusPanel.add(systemStatusField);
	}

	/**
	 * actionPerformed method.
	 * Method that handles how the system should respond when the user clicks
	 * on a button in the GUI.
	 */
	final public void actionPerformed(ActionEvent event){
		if (ctb != null){
			ctb.clearData();
			ctb = null;
		}
		if (event.getSource() == dataconButton){
			//User wishes to convert file to 'binary' format...
			convertFormat();
		}
		else if (event.getSource() == dataBrowseButton){
			//User will specify data file to mine...
			dataFilePathField.setText(selectDataSource());
		}
		else if (event.getSource() == dataCCButton){
			//System is to check & clean data file as needed...
			if (dataFilePathField.getText().length() != 0 && dataFilePathField.getText() != null){
				clearMem(1);
				dcc = new DataCheckClean(this);
				dcc.runCheckClean(dataFilePathField.getText(), (dataFilePathField.getText()).replaceAll("\\.data", "\\.names"));
			}
			else{
				JOptionPane.showMessageDialog(null, "Invalid selection for data file to be processed.\n" +
																"A valid data file needs to be specified prior to\n" +
																"performing checking and cleaning operations.",
																"Invalid Data File", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (event.getSource() == vcdButton){
			//Display cleaned data to user...
			if (tr != null){
				vidg = new ViewInputDataGui(this, tr);
			}
			else{
				JOptionPane.showMessageDialog(null, "No data file has been processed by this system.\n" +
																"A valid data file needs to be processed prior to\n" +
																"viewing the data. Please use the Data Checker/Cleaner\n" +
																"feature to process the data.",
																"No Input Data In System", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (event.getSource() == setLevelSupportButton){
			//User wishes to specify individual minimum support thresholds for each potential level in the dataset...
			if (tr != null){
				smsg = new SetMinSupGui(this, determineNoLevels(), tr.getNumberRecs());
			}
			else{
				JOptionPane.showMessageDialog(null, "No data file has been processed by this system.\n" +
																"A valid data file needs to be processed prior to\n" +
																"viewing the data. Please use the Data Checker/Cleaner\n" +
																"feature to process the data.",
																"No Input Data In System", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (event.getSource() == batchfciButton){
			JOptionPane.showMessageDialog(null, "By loading a multi-source file (one which contains frequent closed\n" +
															"itemsets from multiple datasets) you will not be able to view any of\n" +
															"the following:\n" +
															"Frequent Itemsets, Frequent Closed Itemsets & Association Rules.\n\n" +
															"The association rules will be automatically determined for each dataset\n" +
															"in turn and stored into a single external file, located in the same directory\n" +
															"as the input file (xxxx-rules.txt). Please use this file to view the extracted\n" +
															"rules.", "Viewing Will Not Be Available", JOptionPane.WARNING_MESSAGE);
			importBatchFCI();
		}
		else if (event.getSource() == lfiButton){
			importFI();
		}
		else if (event.getSource() == bfiButton){
			//Generate / build the frequent itemsets for the current data...
			//Check the values for min confidence and min support...
			if (tr != null){
				if (checkValues()){
					clearMem(2);
					bfis = new BuildFrequentItemsets();
					if (msl == null){
						nonderivableRun = false;
						if (fiaBox.getSelectedItem().equals("Apriori Algorithm")){
							bfis.build(this, tr, Float.valueOf(minConfidenceField.getText()).floatValue(),
										Float.valueOf(minSupportField.getText()).floatValue(), 1);
						}
						else if (fiaBox.getSelectedItem().equals("Modified Apriori Algorithm")){
							bfis.build(this, tr, Float.valueOf(minConfidenceField.getText()).floatValue(),
										Float.valueOf(minSupportField.getText()).floatValue(), 2);
						}
						else if (fiaBox.getSelectedItem().equals("Modified Weighted Apriori Algorithm")){
							bfis.build(this, tr, Float.valueOf(minConfidenceField.getText()).floatValue(),
										Float.valueOf(minSupportField.getText()).floatValue(), 3);
						}
						else if (fiaBox.getSelectedItem().equals("Cross-Level Algorithm")){
							//Ask if uniform min support is to be used or individual min support for each level...
							bfis.build(this, tr, Float.valueOf(minConfidenceField.getText()).floatValue(),
										Float.valueOf(minSupportField.getText()).floatValue(), 4);
						}
						else if (fiaBox.getSelectedItem().equals("Modified Cross-Level Algorithm (No Prune)")){
							bfis.build(this, tr, Float.valueOf(minConfidenceField.getText()).floatValue(),
										Float.valueOf(minSupportField.getText()).floatValue(), 5);
						}
						else if (fiaBox.getSelectedItem().equals("ML_T2L1 - Han & Fu")){
							bfis.build(this, tr, Float.valueOf(minConfidenceField.getText()).floatValue(),
										Float.valueOf(minSupportField.getText()).floatValue(), 6);
						}
						else if (fiaBox.getSelectedItem().equals("ML_T2L1 (With Cross Level) - Han & Fu")){
							bfis.build(this, tr, Float.valueOf(minConfidenceField.getText()).floatValue(),
										Float.valueOf(minSupportField.getText()).floatValue(), 7);
						}
						else if (fiaBox.getSelectedItem().equals("Frequent Pattern Tree (FP-Tree) Algorithm")){
							bfis.build(this, tr, Float.valueOf(minConfidenceField.getText()).floatValue(),
										Float.valueOf(minSupportField.getText()).floatValue(), 8);
						}
						else if (fiaBox.getSelectedItem().equals("Rapid Association Rule Mining (RARM)")){
							bfis.build(this, tr, Float.valueOf(minConfidenceField.getText()).floatValue(),
										Float.valueOf(minSupportField.getText()).floatValue(), 9);
						}
						else if (fiaBox.getSelectedItem().equals("Modified-2 Apriori Algorithm")){
							bfis.build(this, tr, Float.valueOf(minConfidenceField.getText()).floatValue(),
										Float.valueOf(minSupportField.getText()).floatValue(), 10);
						}
						else if (fiaBox.getSelectedItem().equals("Non-Derivable Itemset Algorithm - Calders & Goethals")){
							bfis.build(this, tr, Float.valueOf(minConfidenceField.getText()).floatValue(),
										Float.valueOf(minSupportField.getText()).floatValue(), 11);
							nonderivableRun = true;
						}
					}
					else{
						nonderivableRun = false;
						if (fiaBox.getSelectedItem().equals("Apriori Algorithm")){
							bfis.build(this, tr, Float.valueOf(minConfidenceField.getText()).floatValue(),
										(Float)msl.get(1), 1);
						}
						else if (fiaBox.getSelectedItem().equals("Modified Apriori Algorithm")){
							bfis.build(this, tr, Float.valueOf(minConfidenceField.getText()).floatValue(),
										msl, 2);
						}
						else if (fiaBox.getSelectedItem().equals("Modified Weighted Apriori Algorithm")){
							bfis.build(this, tr, Float.valueOf(minConfidenceField.getText()).floatValue(),
										msl, 3);
						}
						else if (fiaBox.getSelectedItem().equals("Cross-Level Algorithm")){
							bfis.build(this, tr, Float.valueOf(minConfidenceField.getText()).floatValue(),
										msl, 4);
						}
						else if (fiaBox.getSelectedItem().equals("Modified Cross-Level Algorithm (No Prune)")){
							bfis.build(this, tr, Float.valueOf(minConfidenceField.getText()).floatValue(),
										msl, 5);
						}
						else if (fiaBox.getSelectedItem().equals("ML_T2L1 - Han & Fu")){
							bfis.build(this, tr, Float.valueOf(minConfidenceField.getText()).floatValue(),
										msl, 6);
						}
						else if (fiaBox.getSelectedItem().equals("ML_T2L1 (With Cross Level) - Han & Fu")){
							bfis.build(this, tr, Float.valueOf(minConfidenceField.getText()).floatValue(),
										msl, 7);
						}
						else if (fiaBox.getSelectedItem().equals("Frequent Pattern Tree (FP-Tree) Algorithm")){
							bfis.build(this, tr, Float.valueOf(minConfidenceField.getText()).floatValue(),
										(Float)msl.get(1), 8);
						}
						else if (fiaBox.getSelectedItem().equals("Rapid Association Rule Mining (RARM)")){
							bfis.build(this, tr, Float.valueOf(minConfidenceField.getText()).floatValue(),
										(Float)msl.get(1), 9);
						}
						else if (fiaBox.getSelectedItem().equals("Modified-2 Apriori Algorithm")){
							bfis.build(this, tr, Float.valueOf(minConfidenceField.getText()).floatValue(),
										msl, 10);
						}
						else if (fiaBox.getSelectedItem().equals("Non-Derivable Itemset Algorithm - Calders & Goethals")){
							bfis.build(this, tr, Float.valueOf(minConfidenceField.getText()).floatValue(),
										msl, 11);
							nonderivableRun = true;
						}
					}
				}
				else{
					JOptionPane.showMessageDialog(null, "At least one value for the minimum confidence and\n" +
																	"minimum support is invalid. Ensure that both entries\n" +
																	"are numerical values between 0 and 1.",
																	"Invalid Confidence/Support Value(s)", JOptionPane.ERROR_MESSAGE);
				}
			}
			else{
				JOptionPane.showMessageDialog(null, "No data file has been processed by this system.\n" +
																"A valid data file needs to be processed prior to\n" +
																"generating frequent itemsets. Please use the Data\n" +
																"Checker/Cleaner feature to process the data.",
																"No Input Data In System", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (event.getSource() == vfiButton){
			//Display the generated frequent itemsets...
			if (tr != null || loadedFI){
				if ((cl != null && fl != null) || loadedFI){
					vfig = new ViewFrequentItemsetGui(this, dataFilePathField.getText(), processTimeBF, cl, fl, tr.getNames());
				}
				else{
					JOptionPane.showMessageDialog(null, "The frequent itemsets for this data file have not yet\n" +
																	"been generated. Itemsets must be generated before they\n" +
																	"can be viewed. Please use the Build Frequent Itemsets\n" +
																	"feature to construct the itemsets.",
																	"No Frequent Itemsets Built", JOptionPane.ERROR_MESSAGE);
				}
			}
			else{
				JOptionPane.showMessageDialog(null, "No data file has been processed by this system.\n" +
																"A valid data file needs to be processed and the\n" +
																"frequent itemsets generated prior to viewing the\n" +
																"data. Please use the Data Checker/Cleaner feature\n" +
																"to process the data file and then use the Build\n" +
																"Frequent Itemsets feature to construct the itemsets.",
																"No Input Data In System", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (event.getSource() == sfiButton){
			//Save the generated frequent itemsets...
			if (tr != null || loadedFI){
				if (fl != null){
					saveFrequentItemsets();
				}
				else{
					JOptionPane.showMessageDialog(null, "The frequent itemsets for this data file have not yet\n" +
																	"been generated. Itemsets must be generated before they\n" +
																	"can be saved. Please use the Build Frequent Itemsets\n" +
																	"feature to construct the itemsets.",
																	"No Frequent Itemsets Built", JOptionPane.ERROR_MESSAGE);
				}
			}
			else{
				JOptionPane.showMessageDialog(null, "No data file has been processed by this system.\n" +
																"A valid data file needs to be processed and the\n" +
																"frequent itemsets generated prior to saving the\n" +
																"data. Please use the Data Checker/Cleaner feature\n" +
																"to process the data file and then use the Build\n" +
																"Frequent Itemsets feature to construct the itemsets.",
																"No Input Data In System", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (event.getSource() == lfciButton){
			importFCI();
		}
		else if (event.getSource() == bfciButton){
			//Generate / build the frequent closed itemsets...
			if (tr != null || loadedFI){
				if (fl != null){
					clearMem(3);
					bfcis = new BuildFrequentClosedItemsets(attNames, attLeaf);
					if (msl == null){
						if (fciaBox.getSelectedItem().equals("Yue Xu's ClosureGen Algorithm")){
							bfcis.build(this, fl, Float.valueOf(minConfidenceField.getText()).floatValue(),
										Float.valueOf(minSupportField.getText()).floatValue(), 1);
						}
						else if (fciaBox.getSelectedItem().equals("Pasquier's CLOSE+ Algorithm")){
							bfcis.build(this, fl, Float.valueOf(minConfidenceField.getText()).floatValue(),
										Float.valueOf(minSupportField.getText()).floatValue(), 2);
						}
						else if (fciaBox.getSelectedItem().equals("Modified CLOSE+ Algorithm")){
							bfcis.build(this, fl, Float.valueOf(minConfidenceField.getText()).floatValue(),
										Float.valueOf(minSupportField.getText()).floatValue(), 3);
						}
					}
					else{
						if ((Integer)msl.get(0) != 3){
							if (fciaBox.getSelectedItem().equals("Yue Xu's ClosureGen Algorithm")){
								bfcis.build(this, fl, Float.valueOf(minConfidenceField.getText()).floatValue(),
												(Float)msl.get(1) / (float)tr.getNumberRecs(), 1);
							}
							else if (fciaBox.getSelectedItem().equals("Pasquier's CLOSE+ Algorithm")){
								bfcis.build(this, fl, Float.valueOf(minConfidenceField.getText()).floatValue(),
												(Float)msl.get(1) / (float)tr.getNumberRecs(), 2);
							}
							else if (fciaBox.getSelectedItem().equals("Modified CLOSE+ Algorithm")){
								bfcis.build(this, fl, Float.valueOf(minConfidenceField.getText()).floatValue(),
												(Float)msl.get(1) / (float)tr.getNumberRecs(), 3);
							}
						}
						else{
							if (fciaBox.getSelectedItem().equals("Yue Xu's ClosureGen Algorithm")){
								bfcis.build(this, fl, Float.valueOf(minConfidenceField.getText()).floatValue(),
												(Integer)msl.get(1) / (float)tr.getNumberRecs(), 1);
							}
							else if (fciaBox.getSelectedItem().equals("Pasquier's CLOSE+ Algorithm")){
								bfcis.build(this, fl, Float.valueOf(minConfidenceField.getText()).floatValue(),
												(Integer)msl.get(1) / (float)tr.getNumberRecs(), 2);
							}
							else if (fciaBox.getSelectedItem().equals("Modified CLOSE+ Algorithm")){
								bfcis.build(this, fl, Float.valueOf(minConfidenceField.getText()).floatValue(),
												(Integer)msl.get(1) / (float)tr.getNumberRecs(), 3);
							}
						}
					}
				}
				else{
					JOptionPane.showMessageDialog(null, "The frequent itemsets for this data file have not yet\n" +
																	"been generated. The frequent itemsets must be generated\n" +
																	"before the frequent closed itemsets can be generated.\n" +
																	"Please use the Build Frequent Itemsets feature to construct\n" + 
																	"the freequent itemsets.",
																	"No Frequent Itemsets Built", JOptionPane.ERROR_MESSAGE);
				}
			}
			else{
				JOptionPane.showMessageDialog(null, "No data file has been processed by this system.\n" +
																"A valid data file needs to be processed and the\n" +
																"frequent itemsets generated prior to generating\n" +
																"the frequent closed itemsets. Please use the Data\n" +
																"Checker/Cleaner feature to process the data file and\n" +
																"then use the Build Frequent Itemsets feature to\n" +
																"construct the itemsets.",
																"No Input Data In System", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (event.getSource() == vfciButton){
			//Display the generated frequent closed itemsets and generators...
			if (tr != null || loadedFI || loadedFCI){
				if ((cl != null && fl != null) || loadedFI  || loadedFCI){
					if (fcl != null){
						vfcig = new ViewFrequentClosedItemsetGui(this, dataFilePathField.getText(), processTimeBFC, fcl, tr.getNames());
					}
					else{
						JOptionPane.showMessageDialog(null, "The frequent closed itemsets for this data file have not\n" +
																		"yet been generated. The frequent closed itemsets must be\n" +
																		"generated before they can be viewed. Please use the Build\n" +
																		"Frequent Closed Itemsets feature to construct the itemsets.",
																		"No Frequent Closed Itemsets Built", JOptionPane.ERROR_MESSAGE);
					}
				}
				else{
					JOptionPane.showMessageDialog(null, "The frequent itemsets for this data file have not yet\n" +
																	"been generated. The frequent itemsets must be generated\n" +
																	"before the frequent closed itemsets can be generated.\n" +
																	"Please use the Build Frequent Itemsets feature to construct\n" + 
																	"the freequent itemsets.",
																	"No Frequent Itemsets Built", JOptionPane.ERROR_MESSAGE);
				}
			}
			else{
				JOptionPane.showMessageDialog(null, "No data file has been processed by this system.\n" +
																"A valid data file needs to be processed and the\n" +
																"frequent itemsets generated prior to generating\n" +
																"the frequent closed itemsets. Please use the Data\n" +
																"Checker/Cleaner feature to process the data file and\n" +
																"then use the Build Frequent Itemsets feature to\n" +
																"construct the itemsets.",
																"No Input Data In System", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (event.getSource() == sfciButton){
			//Save the generated frequent closed itemsets...
			if (tr != null || loadedFI || loadedFCI){
				if (fl != null || loadedFCI){
					if (fcl != null){
						saveFrequentClosedItemsets();
					}
					else{
						JOptionPane.showMessageDialog(null, "The frequent closed itemsets for this data file have not\n" +
																		"yet been generated. The frequent closed itemsets must be\n" +
																		"generated before they can be saved. Please use the Build\n" +
																		"Frequent Closed Itemsets feature to construct the itemsets.",
																		"No Frequent Closed Itemsets Built", JOptionPane.ERROR_MESSAGE);
					}
				}
				else{
					JOptionPane.showMessageDialog(null, "The frequent itemsets for this data file have not yet\n" +
																	"been generated. Frequent itemsets must be generated,\n" +
																	"followed by the generation of the frequent closed itemsets\n" +
																	"before the frequent closed itemsets can be saved. Please use\n" +
																	"the Build Frequent Itemsets feature to construct the frequent\n" +
																	"itemsets and then use the Build Frequent Closed Itemsets feature\n" +
																	"to build the frequent closed itemsets.",
																	"No Frequent Itemsets Built", JOptionPane.ERROR_MESSAGE);
				}
			}
			else{
				JOptionPane.showMessageDialog(null, "No data file has been processed by this system.\n" +
																"A valid data file needs to be processed and the\n" +
																"frequent itemsets and frequent closed itemsets\n" +
																"generated prior to saving the data. Please use the\n" +
																"Data Checker/Cleaner feature to process the data file,\n" +
																"then use the Build Frequent Itemsets feature to construct\n" +
																"the frequent itemsets and then use the Build Frequent\n" +
																"Closed Itemsets feature to construct the frequent closed\n." +
																"itemsets.",
																"No Input Data In System", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (event.getSource() == earButton){
			//Generate / extract the association rules...
			if (tr != null || loadedFI || loadedFCI || nonderivableRun){
				if (fl != null || loadedFCI || nonderivableRun){
					if (fcl != null || nonderivableRun){
//						clearMem(4);
						ears = new ExtractAssociationRules(attCount, multiLevel, attNames, attLeaf, tr.getAbstractTable(), tr.getMTH(), tr.getLTP());
						if (msl == null){
							if (fcl != null && areaBox.getSelectedItem().equals("ReliableExactRule")){
								ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
											Float.valueOf(minSupportField.getText()).floatValue(), ebBox.isSelected(), eeBox.isSelected(),
											abBox.isSelected(), aeBox.isSelected(), 1);
							}
							else if (fcl != null && areaBox.getSelectedItem().equals("Min-max Association Rules")){
								ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
											Float.valueOf(minSupportField.getText()).floatValue(), ebBox.isSelected(), eeBox.isSelected(),
											abBox.isSelected(), aeBox.isSelected(), 2);
							}
							else if (fcl != null && areaBox.getSelectedItem().equals("ReliableExactRule with HRR")){
								ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
											Float.valueOf(minSupportField.getText()).floatValue(), ebBox.isSelected(), eeBox.isSelected(),
											abBox.isSelected(), aeBox.isSelected(), 3);
							}
							else if (fcl != null && areaBox.getSelectedItem().equals("Min-max Association Rules with HRR")){
								ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
											Float.valueOf(minSupportField.getText()).floatValue(), ebBox.isSelected(), eeBox.isSelected(),
											abBox.isSelected(), aeBox.isSelected(), 4);
							}
							else if (fcl != null && areaBox.getSelectedItem().equals("ReliableExactRule - 2")){
								ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
											Float.valueOf(minSupportField.getText()).floatValue(), ebBox.isSelected(), eeBox.isSelected(),
											abBox.isSelected(), aeBox.isSelected(), 5);
							}
							else if (fcl != null && areaBox.getSelectedItem().equals("Min-max Association Rules - Diversity")){
								ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
											Float.valueOf(minSupportField.getText()).floatValue(), ebBox.isSelected(), eeBox.isSelected(),
											abBox.isSelected(), aeBox.isSelected(), 6);
							}
							else if (fcl != null && areaBox.getSelectedItem().equals("Min-max Association Rules - Coverage")){
								ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
											Float.valueOf(minSupportField.getText()).floatValue(), ebBox.isSelected(), eeBox.isSelected(),
											abBox.isSelected(), aeBox.isSelected(), 7);
							}
							else if (fcl != null && areaBox.getSelectedItem().equals("Min-max Association Rules with HRR - Diversity")){
								ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
											Float.valueOf(minSupportField.getText()).floatValue(), ebBox.isSelected(), eeBox.isSelected(),
											abBox.isSelected(), aeBox.isSelected(), 8);
							}
							else if (fcl != null && areaBox.getSelectedItem().equals("Min-max Association Rules with HRR - Coverage")){
								ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
											Float.valueOf(minSupportField.getText()).floatValue(), ebBox.isSelected(), eeBox.isSelected(),
											abBox.isSelected(), aeBox.isSelected(), 9);
							}
							else if (fcl != null && areaBox.getSelectedItem().equals("ReliableExactRule - Diversity")){
								ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
											Float.valueOf(minSupportField.getText()).floatValue(), ebBox.isSelected(), eeBox.isSelected(),
											abBox.isSelected(), aeBox.isSelected(), 10);
							}
							else if (fcl != null && areaBox.getSelectedItem().equals("ReliableExactRule - Coverage")){
								ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
											Float.valueOf(minSupportField.getText()).floatValue(), ebBox.isSelected(), eeBox.isSelected(),
											abBox.isSelected(), aeBox.isSelected(), 11);
							}
							else if (fcl != null && areaBox.getSelectedItem().equals("ReliableExactRule with HRR - Diversity")){
								ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
											Float.valueOf(minSupportField.getText()).floatValue(), ebBox.isSelected(), eeBox.isSelected(),
											abBox.isSelected(), aeBox.isSelected(), 12);
							}
							else if (fcl != null && areaBox.getSelectedItem().equals("ReliableExactRule with HRR - Coverage")){
								ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
											Float.valueOf(minSupportField.getText()).floatValue(), ebBox.isSelected(), eeBox.isSelected(),
											abBox.isSelected(), aeBox.isSelected(), 13);
							}
							else if (nonderivableRun && areaBox.getSelectedItem().equals("Non-Derivable Rules - Goethals et.")){
								ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
											Float.valueOf(minSupportField.getText()).floatValue(), ebBox.isSelected(), eeBox.isSelected(),
											abBox.isSelected(), aeBox.isSelected(), 14);
							}
						}
						else{
							if ((Integer)msl.get(0) != 3){
								if (fcl != null && areaBox.getSelectedItem().equals("ReliableExactRule")){
									ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
															(Float)msl.get(1), ebBox.isSelected(), eeBox.isSelected(),
															abBox.isSelected(), aeBox.isSelected(), 1);
								}
								else if (fcl != null && areaBox.getSelectedItem().equals("Min-max Association Rules")){
									ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
															(Float)msl.get(1), ebBox.isSelected(), eeBox.isSelected(),
															abBox.isSelected(), aeBox.isSelected(), 2);
								}
								else if (fcl != null && areaBox.getSelectedItem().equals("ReliableExactRule with HRR")){
									ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
															(Float)msl.get(1), ebBox.isSelected(), eeBox.isSelected(),
															abBox.isSelected(), aeBox.isSelected(), 3);
								}
								else if (fcl != null && areaBox.getSelectedItem().equals("Min-max Association Rules with HRR")){
									ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
															(Float)msl.get(1), ebBox.isSelected(), eeBox.isSelected(),
															abBox.isSelected(), aeBox.isSelected(), 4);
								}
								else if (fcl != null && areaBox.getSelectedItem().equals("ReliableExactRule - 2")){
									ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
															(Float)msl.get(1), ebBox.isSelected(), eeBox.isSelected(),
															abBox.isSelected(), aeBox.isSelected(), 5);
								}
								else if (fcl != null && areaBox.getSelectedItem().equals("Min-max Association Rules - Diversity")){
									ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
															(Float)msl.get(1), ebBox.isSelected(), eeBox.isSelected(),
															abBox.isSelected(), aeBox.isSelected(), 6);
								}
								else if (fcl != null && areaBox.getSelectedItem().equals("Min-max Association Rules - Coverage")){
									ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
															(Float)msl.get(1), ebBox.isSelected(), eeBox.isSelected(),
															abBox.isSelected(), aeBox.isSelected(), 7);
								}
								else if (fcl != null && areaBox.getSelectedItem().equals("Min-max Association Rules with HRR - Diversity")){
									ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
															(Float)msl.get(1), ebBox.isSelected(), eeBox.isSelected(),
															abBox.isSelected(), aeBox.isSelected(), 8);
								}
								else if (fcl != null && areaBox.getSelectedItem().equals("Min-max Association Rules with HRR - Coverage")){
									ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
															(Float)msl.get(1), ebBox.isSelected(), eeBox.isSelected(),
															abBox.isSelected(), aeBox.isSelected(), 9);
								}
								else if (fcl != null && areaBox.getSelectedItem().equals("ReliableExactRule - Diversity")){
									ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
															(Float)msl.get(1), ebBox.isSelected(), eeBox.isSelected(),
															abBox.isSelected(), aeBox.isSelected(), 10);
								}
								else if (fcl != null && areaBox.getSelectedItem().equals("ReliableExactRule - Coverage")){
									ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
															(Float)msl.get(1), ebBox.isSelected(), eeBox.isSelected(),
															abBox.isSelected(), aeBox.isSelected(), 11);
								}
								else if (fcl != null && areaBox.getSelectedItem().equals("ReliableExactRule with HRR - Diversity")){
									ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
															(Float)msl.get(1), ebBox.isSelected(), eeBox.isSelected(),
															abBox.isSelected(), aeBox.isSelected(), 12);
								}
								else if (fcl != null && areaBox.getSelectedItem().equals("ReliableExactRule with HRR - Coverage")){
									ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
															(Float)msl.get(1), ebBox.isSelected(), eeBox.isSelected(),
															abBox.isSelected(), aeBox.isSelected(), 13);
								}
								else if (nonderivableRun && areaBox.getSelectedItem().equals("Non-Derivable Rules - Goethals et.")){
									ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
															(Float)msl.get(1), ebBox.isSelected(), eeBox.isSelected(),
															abBox.isSelected(), aeBox.isSelected(), 14);
								}
							}
							else{
								if (fcl != null && areaBox.getSelectedItem().equals("ReliableExactRule")){
									ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
															(Integer)msl.get(1) / (float)tr.getNumberRecs(), ebBox.isSelected(), eeBox.isSelected(),
															abBox.isSelected(), aeBox.isSelected(), 1);
								}
								else if (fcl != null && areaBox.getSelectedItem().equals("Min-max Association Rules")){
									ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
															(Integer)msl.get(1) / (float)tr.getNumberRecs(), ebBox.isSelected(), eeBox.isSelected(),
															abBox.isSelected(), aeBox.isSelected(), 2);
								}
								else if (fcl != null && areaBox.getSelectedItem().equals("ReliableExactRule with HRR")){
									ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
															(Integer)msl.get(1) / (float)tr.getNumberRecs(), ebBox.isSelected(), eeBox.isSelected(),
															abBox.isSelected(), aeBox.isSelected(), 3);
								}
								else if (fcl != null && areaBox.getSelectedItem().equals("Min-max Association Rules with HRR")){
									ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
															(Integer)msl.get(1) / (float)tr.getNumberRecs(), ebBox.isSelected(), eeBox.isSelected(),
															abBox.isSelected(), aeBox.isSelected(), 4);
								}
								else if (fcl != null && areaBox.getSelectedItem().equals("ReliableExactRule - 2")){
									ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
															(Integer)msl.get(1) / (float)tr.getNumberRecs(), ebBox.isSelected(), eeBox.isSelected(),
															abBox.isSelected(), aeBox.isSelected(), 5);
								}
								else if (fcl != null && areaBox.getSelectedItem().equals("Min-max Association Rules - Diversity")){
									ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
															(Integer)msl.get(1) / (float)tr.getNumberRecs(), ebBox.isSelected(), eeBox.isSelected(),
															abBox.isSelected(), aeBox.isSelected(), 6);
								}
								else if (fcl != null && areaBox.getSelectedItem().equals("Min-max Association Rules - Coverage")){
									ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
															(Integer)msl.get(1) / (float)tr.getNumberRecs(), ebBox.isSelected(), eeBox.isSelected(),
															abBox.isSelected(), aeBox.isSelected(), 7);
								}
								else if (fcl != null && areaBox.getSelectedItem().equals("Min-max Association Rules with HRR - Diversity")){
									ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
															(Integer)msl.get(1) / (float)tr.getNumberRecs(), ebBox.isSelected(), eeBox.isSelected(),
															abBox.isSelected(), aeBox.isSelected(), 8);
								}
								else if (fcl != null && areaBox.getSelectedItem().equals("Min-max Association Rules with HRR - Coverage")){
									ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
															(Integer)msl.get(1) / (float)tr.getNumberRecs(), ebBox.isSelected(), eeBox.isSelected(),
															abBox.isSelected(), aeBox.isSelected(), 9);
								}
								else if (fcl != null && areaBox.getSelectedItem().equals("ReliableExactRule - Diversity")){
									ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
															(Integer)msl.get(1) / (float)tr.getNumberRecs(), ebBox.isSelected(), eeBox.isSelected(),
															abBox.isSelected(), aeBox.isSelected(), 10);
								}
								else if (fcl != null && areaBox.getSelectedItem().equals("ReliableExactRule - Coverage")){
									ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
															(Integer)msl.get(1) / (float)tr.getNumberRecs(), ebBox.isSelected(), eeBox.isSelected(),
															abBox.isSelected(), aeBox.isSelected(), 11);
								}
								else if (fcl != null && areaBox.getSelectedItem().equals("ReliableExactRule with HRR - Diversity")){
									ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
															(Integer)msl.get(1) / (float)tr.getNumberRecs(), ebBox.isSelected(), eeBox.isSelected(),
															abBox.isSelected(), aeBox.isSelected(), 12);
								}
								else if (fcl != null && areaBox.getSelectedItem().equals("ReliableExactRule with HRR - Coverage")){
									ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
															(Integer)msl.get(1) / (float)tr.getNumberRecs(), ebBox.isSelected(), eeBox.isSelected(),
															abBox.isSelected(), aeBox.isSelected(), 13);
								}
								else if (nonderivableRun && areaBox.getSelectedItem().equals("Non-Derivable Rules - Goethals et.")){
									ears.extractRules(this, fl, fcl, Float.valueOf(minConfidenceField.getText()).floatValue(),
															(Integer)msl.get(1) / (float)tr.getNumberRecs(), ebBox.isSelected(), eeBox.isSelected(),
															abBox.isSelected(), aeBox.isSelected(), 14);
								}
							}
						}
					}
					else{
						JOptionPane.showMessageDialog(null, "The frequent closed itemsets for this data file have not\n" +
																		"yet been generated. The frequent closed itemsets must be\n" +
																		"generated before any association rules can be mined. Please\n" +
																		"use the Build Frequent Closed Itemsets feature to construct the\n" +
																		"itemsets.",
																		"No Frequent Closed Itemsets Built", JOptionPane.ERROR_MESSAGE);
					}
				}
				else{
					JOptionPane.showMessageDialog(null, "The frequent itemsets for this data file have not yet\n" +
																	"been generated. Frequent itemsets must be generated,\n" +
																	"followed by the generation of the frequent closed itemsets\n" +
																	"before any association rules can be mined. Please use\n" +
																	"the Build Frequent Itemsets feature to construct the frequent\n" +
																	"itemsets and then use the Build Frequent Closed Itemsets feature\n" +
																	"to build the frequent closed itemsets.",
																	"No Frequent Itemsets Built", JOptionPane.ERROR_MESSAGE);
				}
			}
			else{
				JOptionPane.showMessageDialog(null, "No data file has been processed by this system.\n" +
																"A valid data file needs to be processed and the\n" +
																"frequent itemsets and frequent closed itemsets\n" +
																"generated before association rules can be mined. Please\n" +
																"use the Data Checker/Cleaner feature to process the data\n" +
																"file, then use the Build Frequent Itemsets feature to\n" +
																"construct the frequent itemsets and then use the Build Frequent\n" +
																"Closed Itemsets feature to construct the frequent closed\n" +
																"itemsets.",
																"No Input Data In System", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (event.getSource() == varButton){
			//Display the extracted association rules...
			if (tr != null || loadedFI || loadedFCI){
				if ((cl != null && fl != null) ||loadedFI || loadedFCI){
					if (fcl != null){
						if (arl != null){
							varg = new ViewAssociationRulesGui(this, dataFilePathField.getText(), processTimeAR, arl, tr.getNames());
						}
						else{
							JOptionPane.showMessageDialog(null, "The association rules for this data file have not yet been\n" +
																			"extracted. The association rules must be extracted before they\n" +
																			"can be viewed. Please use the Extract Association Rules feature\n" +
																			"to extract any associations that can be obtained from this dataset.",
																			"No Association Rules Extracted", JOptionPane.ERROR_MESSAGE);
						}
					}
					else{
						JOptionPane.showMessageDialog(null, "The frequent closed itemsets for this data file have not\n" +
																		"yet been generated. The frequent closed itemsets must be\n" +
																		"generated before any association rules can be mined. Please\n" +
																		"use the Build Frequent Closed Itemsets feature to construct the\n" +
																		"itemsets.",
																		"No Frequent Closed Itemsets Built", JOptionPane.ERROR_MESSAGE);
					}
				}
				else{
					JOptionPane.showMessageDialog(null, "The frequent itemsets for this data file have not yet\n" +
																	"been generated. Frequent itemsets must be generated,\n" +
																	"followed by the generation of the frequent closed itemsets\n" +
																	"before any association rules can be mined. Please use\n" +
																	"the Build Frequent Itemsets feature to construct the frequent\n" +
																	"itemsets and then use the Build Frequent Closed Itemsets feature\n" +
																	"to build the frequent closed itemsets.",
																	"No Frequent Itemsets Built", JOptionPane.ERROR_MESSAGE);
				}
			}
			else{
				JOptionPane.showMessageDialog(null, "No data file has been processed by this system.\n" +
																"A valid data file needs to be processed and the\n" +
																"frequent itemsets and frequent closed itemsets\n" +
																"generated before association rules can be mined. Please\n" +
																"use the Data Checker/Cleaner feature to process the data\n" +
																"file, then use the Build Frequent Itemsets feature to\n" +
																"construct the frequent itemsets and then use the Build Frequent\n" +
																"Closed Itemsets feature to construct the frequent closed\n" +
																"itemsets.",
																"No Input Data In System", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (event.getSource() == evalARButton){
			//Evaluate the discovered association rules & display the results...
			if (tr != null || loadedFI || loadedFCI){
				if ((cl != null && fl != null) ||loadedFI || loadedFCI){
					if (fcl != null){
						if (arl != null){
							veg = new ViewEvaluationGui(this, dataFilePathField.getText(), processTimeAR, arl, tr.getNames(), tr.getAllNodes(), attNames);
						}
						else{
							JOptionPane.showMessageDialog(null, "The association rules for this data file have not yet been\n" +
																			"extracted. The association rules must be extracted before they\n" +
																			"can be evaluated. Please use the Extract Association Rules feature\n" +
																			"to extract any associations that can be obtained from this dataset.",
																			"No Association Rules Extracted", JOptionPane.ERROR_MESSAGE);
						}
					}
					else{
						JOptionPane.showMessageDialog(null, "The frequent closed itemsets for this data file have not\n" +
																		"yet been generated. The frequent closed itemsets must be\n" +
																		"generated before any association rules can be mined. Please\n" +
																		"use the Build Frequent Closed Itemsets feature to construct the\n" +
																		"itemsets.",
																		"No Frequent Closed Itemsets Built", JOptionPane.ERROR_MESSAGE);
					}
				}
				else{
					JOptionPane.showMessageDialog(null, "The frequent itemsets for this data file have not yet\n" +
																	"been generated. Frequent itemsets must be generated,\n" +
																	"followed by the generation of the frequent closed itemsets\n" +
																	"before any association rules can be mined. Please use\n" +
																	"the Build Frequent Itemsets feature to construct the frequent\n" +
																	"itemsets and then use the Build Frequent Closed Itemsets feature\n" +
																	"to build the frequent closed itemsets.",
																	"No Frequent Itemsets Built", JOptionPane.ERROR_MESSAGE);
				}
			}
			else{
				JOptionPane.showMessageDialog(null, "No data file has been processed by this system.\n" +
																"A valid data file needs to be processed and the\n" +
																"frequent itemsets and frequent closed itemsets\n" +
																"generated before association rules can be mined. Please\n" +
																"use the Data Checker/Cleaner feature to process the data\n" +
																"file, then use the Build Frequent Itemsets feature to\n" +
																"construct the frequent itemsets and then use the Build Frequent\n" +
																"Closed Itemsets feature to construct the frequent closed\n" +
																"itemsets.",
																"No Input Data In System", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (event.getSource() == sarButton){
			//Save the extracted association rules...
			if (tr != null || loadedFI || loadedFCI){
				if (fl != null || loadedFCI){
					if (fcl != null){
						if (arl != null){
							saveExtractedRules();
						}
						else{
							JOptionPane.showMessageDialog(null, "The association rules for this data file have not yet been\n" +
																			"extracted. The association rules must be extracted before they\n" +
																			"can be saved. Please use the Extract Association Rules feature\n" +
																			"to extract any associations that can be obtained from this dataset.",
																			"No Association Rules Extracted", JOptionPane.ERROR_MESSAGE);
						}
					}
					else{
						JOptionPane.showMessageDialog(null, "The frequent closed itemsets for this data file have not\n" +
																		"yet been generated. The frequent closed itemsets must be\n" +
																		"generated, followed by the extraction of association rules\n" +
																		"before any association rules can be saved. Please use the\n" +
																		"Build Frequent Closed Itemsets feature to construct the\n" +
																		"itemsets and then use the Extract Association Rules feature to\n" +
																		"extact any associations that can be obtained from this dataset.",
																		"No Frequent Closed Itemsets Built", JOptionPane.ERROR_MESSAGE);
					}
				}
				else{
					JOptionPane.showMessageDialog(null, "The frequent itemsets for this data file have not yet\n" +
																	"been generated. Frequent itemsets must be generated,\n" +
																	"followed by the generation of the frequent closed itemsets,\n" +
																	"followed by the extraction of association rules before any\n" +
																	"association rules can be saved. Please use the Build Frequent\n" +
																	"Itemsets feature to construct the frequent itemsets, then use\n" +
																	"the Build Frequent Closed Itemsets feature to build the frequent\n" +
																	"closed itemsets and then use the Extract Association Rules feature\n" +
																	"to extact any associations that can be obtained from this dataset.",
																	"No Frequent Itemsets Built", JOptionPane.ERROR_MESSAGE);
				}
			}
			else{
				JOptionPane.showMessageDialog(null, "No data file has been processed by this system.\n" +
																"A valid data file needs to be processed and the\n" +
																"frequent itemsets and frequent closed itemsets need to\n" +
																"be generated along with the extraction of association rules\n" +
																"before association rules can be saved. Please use the Data\n" +
																"Checker/Cleaner feature to process the data file, then use the\n" +
																"Build Frequent Itemsets feature to construct the frequent itemsets,\n" +
																"then use the Build Frequent Closed Itemsets feature to construct\n" +
																"the frequent closed itemsets and then use the Extract Association\n" +
																"Rules feature to extact any associations that can be obtained from\n" +
																"this dataset.",
																"No Input Data In System", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	final public void itemStateChanged(ItemEvent event){
		if (event.getSource() == ebBox){
			if (eeBox.isSelected() || aeBox.isSelected()){
				ebBox.setSelected(true);
			}
		}
		else if (event.getSource() == eeBox){
			if (eeBox.isSelected()){
				ebBox.setSelected(true);
			}
		}
		else if (event.getSource() == abBox){
			if (aeBox.isSelected()){
				abBox.setSelected(true);
			}
		}
		else if (event.getSource() == aeBox){
			if (aeBox.isSelected()){
				ebBox.setSelected(true);
				abBox.setSelected(true);
			}
		}
	}

	/**
	 * convertFormat method.
	 * Method used to initiate the conversion of a multi-dimensional
	 * dataset into a binary dataset.
	 */
	final private void convertFormat(){
		String origData, convertData;
		int useraction1, useraction2;

		sourceDataFile = new JFileChooser();
		sourceDataFile.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		sourceDataFile.setDialogTitle("Select Data File For Conversion:");
		useraction1 = sourceDataFile.showOpenDialog(this);
		if (useraction1 == JFileChooser.APPROVE_OPTION){
			origData = sourceDataFile.getSelectedFile().getAbsolutePath();
			destDataFile = new JFileChooser(origData);
			destDataFile.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			destDataFile.setDialogTitle("Select Destination File For Converted Data:");
			useraction2 = destDataFile.showSaveDialog(this);
			if (useraction2 == JFileChooser.APPROVE_OPTION){
				convertData = destDataFile.getSelectedFile().getAbsolutePath();
				dataFilePathField.setText(convertData);
				//Perform data conversion...
				ctb = new ConvertToBinary(this);
				ctb.runConvert(origData, convertData);
			}
			else{
			}
		}
		else{
		}
	}

	/**
	 * importFI method.
	 */
	final private void importFI(){
		String dataFile;
		int useraction1;

		sourceDataFile = new JFileChooser();
		sourceDataFile.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		sourceDataFile.setDialogTitle("Select Frequent Itemsets Data File For Loading:");
		useraction1 = sourceDataFile.showOpenDialog(this);
		if (useraction1 == JFileChooser.APPROVE_OPTION){
			dataFile = sourceDataFile.getSelectedFile().getAbsolutePath();
			dl = new DataLoader(this);
			dl.loadFI(dataFile);
		}
	}

	/**
	 * importFCI method.
	 */
	final private void importFCI(){
		String dataFile;
		int useraction1;

		sourceDataFile = new JFileChooser();
		sourceDataFile.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		sourceDataFile.setDialogTitle("Select Frequent Closed Itemsets Data File For Loading:");
		useraction1 = sourceDataFile.showOpenDialog(this);
		if (useraction1 == JFileChooser.APPROVE_OPTION){
			dataFile = sourceDataFile.getSelectedFile().getAbsolutePath();
			dl = new DataLoader(this);
			dl.loadFCI(dataFile);
		}
	}

	/**
	 *
	 */
	final private void importBatchFCI(){
		String dataFile;
		int useraction1;

		sourceDataFile = new JFileChooser();
		sourceDataFile.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		sourceDataFile.setDialogTitle("Select Multi-Source Frequent Closed Itemsets Data File For Loading:");
		useraction1 = sourceDataFile.showOpenDialog(this);
		if (useraction1 == JFileChooser.APPROVE_OPTION){
			dataFile = sourceDataFile.getSelectedFile().getAbsolutePath();
			if (checkValues()){
				dl = new DataLoader(this);
				if (areaBox.getSelectedItem().equals("Yue Xu's Exact Rule Algorithm")){
					dl.loadBatchFCI(dataFile, Float.valueOf(minConfidenceField.getText()).floatValue(),
						ebBox.isSelected(), eeBox.isSelected(), abBox.isSelected(), aeBox.isSelected(), 1);
				}
				else if (areaBox.getSelectedItem().equals("Pasquier's Rule Mining Algorithm")){
					dl.loadBatchFCI(dataFile, Float.valueOf(minConfidenceField.getText()).floatValue(),
						ebBox.isSelected(), eeBox.isSelected(), abBox.isSelected(), aeBox.isSelected(), 2);
				}
			}
		}
	}

	/**
	 * selectDataSource method.
	 * Method used to allow a user to specify the source file/dataset
	 * to be processed/loaded. This will be from where the frequent
	 * itemsets, closed itemsets and association rules are extracted.
	 */
	final private String selectDataSource(){
		String currentSourcePath = dataFilePathField.getText();
		int useraction;

		sourceDataFile = new JFileChooser(currentSourcePath);
		sourceDataFile.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		sourceDataFile.setDialogTitle("Select Data File:");
		useraction = sourceDataFile.showOpenDialog(this);
		if (useraction == JFileChooser.APPROVE_OPTION){
			return sourceDataFile.getSelectedFile().getAbsolutePath();
		}
		else{
			return currentSourcePath;
		}
	}

	/**
	 * saveFrequentItemsets method.
	 * Method used to allow a user to specify where they wish to save a
	 * copy of the frequent itemsets that where found in the dataset.
	 */
	final private void saveFrequentItemsets(){
		int useraction;

		fiFile = new JFileChooser();
		fiFile.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fiFile.setDialogTitle("Select Save To Destination:");
		useraction = fiFile.showSaveDialog(this);
		if (useraction == JFileChooser.APPROVE_OPTION){
			dfw = new DataFileWriter(fiFile.getSelectedFile().getAbsolutePath());
			dfw.writeFrequentItemsets(dataFilePathField.getText(), tr, fl, cl, processTimeBF);
		}
		else{
		}
	}

	final private void saveFrequentItemsets(String in, String out){
		dfw = new DataFileWriter(out);
		dfw.writeFrequentItemsets(in, tr, fl, cl, processTimeBF);
	}

	/**
	 * saveFrequentClosedItemsets method.
	 * Method used to allow a user to specify where they wish to save a
	 * copy of the frequent closed itemsets that where found in the dataset.
	 */
	final private void saveFrequentClosedItemsets(){
		int useraction;

		fiFile = new JFileChooser();
		fiFile.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fiFile.setDialogTitle("Select Save To Destination:");
		useraction = fiFile.showSaveDialog(this);
		if (useraction == JFileChooser.APPROVE_OPTION){
			dfw = new DataFileWriter(fiFile.getSelectedFile().getAbsolutePath());
			dfw.writeFrequentClosedItemsets(dataFilePathField.getText(), tr, fcl, processTimeBFC);
		}
		else{
		}
	}

	final private void saveFrequentClosedItemsets(String in, String out){
		dfw = new DataFileWriter(out);
		dfw.writeFrequentClosedItemsets(in, tr, fcl, processTimeBFC);
	}

	/**
	 * saveExtractedRules method.
	 * Method used to allow a user to specify where they wish to save a
	 * copy of the association rules that where extracted from the dataset.
	 */
	final private void saveExtractedRules(){
		int useraction;

		fiFile = new JFileChooser();
		fiFile.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fiFile.setDialogTitle("Select Save To Destination:");
		useraction = fiFile.showSaveDialog(this);
		if (useraction == JFileChooser.APPROVE_OPTION){
			dfw = new DataFileWriter(fiFile.getSelectedFile().getAbsolutePath());
			if (areaBox.getSelectedItem().equals("ReliableExactRule")){
				dfw.writeAssociationRules(dataFilePathField.getText(), arl, tr, 1, processTimeAR, tr.getAllNodes(), attNames);
			}
			else if (areaBox.getSelectedItem().equals("Min-max Association Rules")){
				dfw.writeAssociationRules(dataFilePathField.getText(), arl, tr, 2, processTimeAR, tr.getAllNodes(), attNames);
			}
			else if (areaBox.getSelectedItem().equals("Min-max Association Rules with HRR")){
				dfw.writeAssociationRules(dataFilePathField.getText(), arl, tr, 3, processTimeAR, tr.getAllNodes(), attNames);
			}
			else if (areaBox.getSelectedItem().equals("ReliableExactRule with HRR")){
				dfw.writeAssociationRules(dataFilePathField.getText(), arl, tr, 4, processTimeAR, tr.getAllNodes(), attNames);
			}
		}
		else{
		}
	}

/*	final private void saveExtractedRules(String in, String out, int id){
		dfw = new DataFileWriter(out);
		dfw.writeAssociationRules(in, arl, tr, id, processTimeAR, tr.getAllNodes(), attNames);
	}*/

	/**
	 * checkValues method.
	 * Method that checks the entries that hold the min support and
	 * confedence are valid values (numbers).
	 */
	final private boolean checkValues(){
		float mincon, minsup;
		try{
			mincon = Float.valueOf(minConfidenceField.getText()).floatValue();
			minsup = Float.valueOf(minSupportField.getText()).floatValue();
			if (mincon > 0 && mincon <= 1 && minsup > 0 && minsup <= 1){
				return true;
			}
			else if (mincon > 0 && mincon <= 100 && minsup > 0 && minsup <= 100){
				mincon = mincon / 100;
				minsup = minsup / 100;
				return true;
			}
			else{
				return false;
			}
		}
		catch (NumberFormatException e){
			return false;
		}
	}

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
				if (vidg != null){
					vidg.clearData();
					vidg = null;
				}
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
				if (vfig != null){
					vfig.clearData();
					vfig = null;
				}
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
				if (vfcig != null){
					vfcig.clearData();
					vfcig = null;
				}
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
				if (varg != null){
					varg.clearData();
					varg = null;
				}
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
		systemStatusField.setText(message);
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
		minSupportField.setText("" + mst);
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
	 * Method used to receive and store the success or failure of loading
	 * in frequent itemsets from an external file.
	 */
	final public void loadFIStatusPassback(boolean status){
		loadedFI = status;
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
	 * Method used to receive and store the success or failure of loading
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
}