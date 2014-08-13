/**
 * ViewAssociationRulesGui class.
 * 
 * Start Date: 30 January 2007
 * @author Gavin Shaw
 * @version 1.0
 *
 * Log:
 *		  1.0 Start version of class.
 */
package GUI;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import javax.swing.border.LineBorder;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.Vector;

import GUI.ARMMainGui;

import Data.AssociationRuleList;

public class ViewAssociationRulesGui extends JFrame implements ActionListener{

	private ARMMainGui owner;

	private AssociationRuleList arl;

	private tmr rtm1, rtm2, rtm3, rtm4;

	private ruleViewer eb, ee, ab, ea;

	private statDisplay sd;

	private JTabbedPane tabPane;

	private JPanel mainPanel, buttonPanel;

	private JButton okayButton;

	private JScrollPane tablePane1, tablePane2, sumPane3;

	private JTable ebt, eet, abt, eat;

	private JTextArea statsArea;

	private LineBorder lBorder = new LineBorder(Color.black, 1);

	private Font standardFont = new Font ("SansSerif", Font.PLAIN, 12);
	private Font headFont = new Font ("SansSerif", Font.PLAIN, 14);

	private String dataPath;

	private long processTime;

	/**
	 * ViewAssociationRulesGui method.
	 * Constructor.
	 * Method used to initialise and setup the GUI that displays the
	 * association rules (both exact and approx) that were mined/extracted
	 * from the generators and frequent closed itemsets already obtained.
	 */
	public ViewAssociationRulesGui(ARMMainGui main, String path, long time, AssociationRuleList list, String[] list2){
		owner = main;
		arl = list;
		dataPath = path;
		processTime = time;

		eb = new ruleViewer(arl, 1, list2);
		ee = new ruleViewer(arl, 2, list2);
		ab = new ruleViewer(arl, 3, list2);
		ea = new ruleViewer(arl, 4, list2);
		sd = new statDisplay(arl);

		Container container = getContentPane();
		container.setBackground(Color.lightGray);
		container.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 7));
		this.setTitle("Association Rules Summary");

		tabPane = new JTabbedPane();
		container.add(tabPane);
		tabPane.setFont(standardFont);
		tabPane.setBackground(Color.lightGray);

		tabPane.add("Exact Basis Rule List", eb);
		tabPane.add("Expanded Exact Rule List", ee);
		tabPane.add("Approx Basis Rule List", ab);
		tabPane.add("Expanded Approx Rule List", ea);
		tabPane.add("Summary", sd);

		setupButtonPanel();
		container.add(buttonPanel);

		Toolkit theKit = this.getToolkit();
		Dimension windowSize = theKit.getScreenSize();
		setSize(1024, 700);
		setLocation(new Point(0, 0));
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);
		owner.setEnabled(false);
	}

	/**
	 * setupButtonPanel method.
	 * Method that sets up a panel which holds the button allowing the user
	 * to close this GUI and return to the main GUI when desired.
	 */
	final private void setupButtonPanel(){
		buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.lightGray);
		buttonPanel.setPreferredSize(new Dimension(1009, 45));
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

	/**
	 * clearData method.
	 * Method used to destroy and clear the variables that hold the
	 * data tables (references and actual data) and the table models.
	 * This is to free up memory.
	 */
	final public void clearData(){
		arl.clearData();
		arl = null;
		rtm1.removeRows();
		rtm1 = null;
		rtm2.removeRows();
		rtm2 = null;
		rtm3.removeRows();
		rtm3 = null;
		rtm4.removeRows();
		rtm4 = null;
		eb = null;
		ee = null;
		ab = null;
		ea = null;
		ebt = null;
		eet = null;
		abt = null;
		eat = null;
		System.gc();
	}

	/**
	 * ruleViewer class.
	 * Private class that sets up the sub-GUI that will display
	 * the list of association rules that were mined/extracted
	 * from the specified dataset.
	 */
	private class ruleViewer extends JPanel{

		/**
		 * ruleViewer method.
		 * Constructor.
		 * Method used to initialise this class and setup the sub-GUI
		 * that this class is responsible for displaying.
		 */
		public ruleViewer(AssociationRuleList arl, int mode, String[] list){
			setBackground(Color.lightGray);
			setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

			setupMainPanel(arl, mode, list);
			add(mainPanel);
		}

		/**
		 * setupMainPanel method.
		 * Method used to setup the main panel in this class's
		 * sub-GUI which will hold a table with the list of
		 * association rules mined/extracted from the generators
		 * and frequent closed itemsets obtained from the previously
		 * specified dataset.
		 */
		final private void setupMainPanel(AssociationRuleList arl, int mode, String[] list){
			mainPanel = new JPanel();
			mainPanel.setBackground(Color.lightGray);
			mainPanel.setPreferredSize(new Dimension(1009, 570));
			mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
			mainPanel.setBorder(lBorder);

			tablePane1 = new JScrollPane();
			tablePane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			tablePane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			tablePane1.setPreferredSize(new Dimension(999, 550));

			if (mode == 1){
				//Exact basis...
				rtm1 = new tmr(arl, mode, list);
				ebt = new JTable(rtm1);
				ebt.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				ebt.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				setColumnAlign(3, 1);
				tablePane1.setViewportView(ebt);
			}
			else if (mode == 2){
				//Expanded exact...
				rtm2 = new tmr(arl, mode, list);
				eet = new JTable(rtm2);
				eet.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				eet.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				setColumnAlign(3, 2);
				tablePane1.setViewportView(eet);
			}
			else if (mode == 3){
				//Approx basis...
				rtm3 = new tmr(arl, mode, list);
				abt = new JTable(rtm3);
				abt.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				abt.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				setColumnAlign(3, 3);
				tablePane1.setViewportView(abt);
			}
			else if (mode == 4){
				//Expanded approx...
				rtm4 = new tmr(arl, mode, list);
				eat = new JTable(rtm4);
				eat.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				eat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				setColumnAlign(3, 4);
				tablePane1.setViewportView(eat);
			}

			mainPanel.add(tablePane1);
		}

		/**
		 * setColumnAlign method.
		 * Method used to setup the horizontal alginment of the contents
		 * of the columns contained within the data table.
		 */
		final private void setColumnAlign(int cc, int mode){
			DefaultTableCellRenderer mcr = new DefaultTableCellRenderer();
			TableColumnModel tcm;
			if (mode == 1){
				tcm = ebt.getColumnModel();
			}
			else if (mode == 2){
				tcm = eet.getColumnModel();
			}
			else if (mode == 3){
				tcm = abt.getColumnModel();
			}
			else{
				tcm = eat.getColumnModel();
			}
			tcm.getColumn(0).setPreferredWidth(388);
			tcm.getColumn(1).setPreferredWidth(388);
			tcm.getColumn(2).setPreferredWidth(99);
			tcm.getColumn(3).setPreferredWidth(99);
			mcr.setHorizontalAlignment(SwingConstants.CENTER);
			tcm.getColumn(2).setCellRenderer(mcr);
			tcm.getColumn(3).setCellRenderer(mcr);
		}
	}

	/**
	 * statDisplay class.
	 * Private class that sets up the sub-GUI that will display
	 * a summary about the exact and approximate association rules
	 * that were mined/extracted from the specified dataset.
	 */
	private class statDisplay extends JPanel{

		/**
		 * statDisplay method.
		 * Constructor.
		 * Method used to initialise this class and setup the sub-GUI
		 * that this class is responsible for displaying.
		 */
		public statDisplay(AssociationRuleList arl){
			setBackground(Color.lightGray);
			setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

			setupMainPanel(arl);
			add(mainPanel);
		}

		/**
		 * setupMainPanel method.
		 * Method used to setup the main panel in this class's
		 * sub-GUI which will hold a summary of the exact association
		 * rules and approximate association extracted. Will include total
		 * number, total number of basis and expanded and total processing
		 * time taken to determine the association rules.
		 */
		final private void setupMainPanel(AssociationRuleList arl){
			mainPanel = new JPanel();
			mainPanel.setBackground(Color.lightGray);
			mainPanel.setPreferredSize(new Dimension(1009, 570));
			mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
			mainPanel.setBorder(lBorder);

			sumPane3 = new JScrollPane();
			sumPane3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			sumPane3.setPreferredSize(new Dimension(999, 550));

			statsArea = new JTextArea();
			statsArea.setLineWrap(true);
			statsArea.setWrapStyleWord(true);
			statsArea.setEditable(false);

			sumPane3.setViewportView(statsArea);

			mainPanel.add(sumPane3);
			formatText(arl);
		}

		/**
		 * formatText method.
		 * Method used to format the summary text displayed by this
		 * class.
		 */
		final private void formatText(AssociationRuleList arl){
			NumberFormat formatter = NumberFormat.getNumberInstance();
			formatter.setMaximumFractionDigits(3);
			int currentLength = 0;
			String[] item;
			ArrayList fitems;
			ArrayList lengthCount = new ArrayList();
			ArrayList uniqueFCI = new ArrayList();
			ArrayList summaryText = new ArrayList();

			summaryText.add("Association Rule Summary\n");
			summaryText.add("Data File: " + dataPath + "\n");
			summaryText.add("--------------------------------------------------------------------------------------------------------------------------\n");

			if (arl.getExactMinMaxBasisLeafSize() < 10){
				summaryText.add("Total number of exact basis extracted:                  " + arl.getExactMinMaxBasisSize() + "\t\t\tLeaf: " + arl.getExactMinMaxBasisLeafSize() + "\t\tAbstract: " + arl.getExactMinMaxBasisAbstractSize() + "\n");
			}
			else{
				summaryText.add("Total number of exact basis extracted:                  " + arl.getExactMinMaxBasisSize() + "\t\t\tLeaf: " + arl.getExactMinMaxBasisLeafSize() + "\tAbstract: " + arl.getExactMinMaxBasisAbstractSize() + "\n");
			}
			summaryText.add("Coverage of exact basis rule set (rule):                " + formatter.format((Float)arl.getExactMinMaxBasisCovR()) + "%\t\t\t\t\tAbstract: " + formatter.format(arl.getExactAbstractBasisCovR()) + "%\n\n");

			if (arl.getExactLeafSize() < 10){
				summaryText.add("Total number of all/expanded exact extracted:           " + arl.getExactAllSize() + "\t\t\tLeaf: " + arl.getExactLeafSize() + "\t\tAbstract: " + arl.getExactAbstractSize() + "\n");
			}
			else{
				summaryText.add("Total number of all/expanded exact extracted:           " + arl.getExactAllSize() + "\t\t\tLeaf: " + arl.getExactLeafSize() + "\tAbstract: " + arl.getExactAbstractSize() + "\n");
			}
			summaryText.add("Coverage of all/expanded exact rule set (rule):         " + formatter.format((Float)arl.getExactAllCovR()) + "%\t\t\t\t\tAbstract: " + formatter.format(arl.getExactAllAbstractCovR()) + "%\n\n");

			if ((arl.getExactMinMaxBasisLeafSize() + arl.getExactLeafSize()) < 10){
				summaryText.add("Total number of exact association rules extracted:      " + (arl.getExactMinMaxBasisSize() + arl.getExactAllSize()) + "\t\t\tLeaf: " + (arl.getExactMinMaxBasisLeafSize() + arl.getExactLeafSize()) + "\t\tAbstract: " + (arl.getExactMinMaxBasisAbstractSize() + arl.getExactAbstractSize()) + "\n");
			}
			else{
				summaryText.add("Total number of exact association rules extracted:      " + (arl.getExactMinMaxBasisSize() + arl.getExactAllSize()) + "\t\t\tLeaf: " + (arl.getExactMinMaxBasisLeafSize() + arl.getExactLeafSize()) + "\tAbstract: " + (arl.getExactMinMaxBasisAbstractSize() + arl.getExactAbstractSize()) + "\n");
			}

			summaryText.add("Total coverage of exact rule set(s):                    " + formatter.format((Float)arl.getExactRuleCoverage()) + "%\t\t\t\t\tAbstract: " + formatter.format((Float)arl.getExactAbstractRuleCoverage()) + "%\n");
			summaryText.add("--------------------------------------------------------------------------------------------------------------------------\n");

			if (arl.getApproxMinMaxBasisLeafSize() < 10){
				summaryText.add("Total number of approx basis extracted:                 " + arl.getApproxMinMaxBasisSize() + "\t\t\tLeaf: " + arl.getApproxMinMaxBasisLeafSize() + "\t\tAbstract: " + arl.getApproxMinMaxBasisAbstractSize() + "\n");
			}
			else{
				summaryText.add("Total number of approx basis extracted:                 " + arl.getApproxMinMaxBasisSize() + "\t\t\tLeaf: " + arl.getApproxMinMaxBasisLeafSize() + "\tAbstract: " + arl.getApproxMinMaxBasisAbstractSize() + "\n");
			}
			summaryText.add("Coverage of approx basis rule set (rule):               " + formatter.format((Float)arl.getApproxMinMaxBasisCovR()) + "%\t\t\t\t\tAbstract: " + formatter.format(arl.getApproxAbstractBasisCovR()) + "%\n\n");

			if (arl.getApproxLeafSize() < 10){
				summaryText.add("Total number of all/expanded approx extracted:          " + arl.getApproxAllSize() + "\t\t\tLeaf: " + arl.getApproxLeafSize() + "\t\tAbstract: " + arl.getApproxAbstractSize() + "\n");
			}
			else{
				summaryText.add("Total number of all/expanded approx extracted:          " + arl.getApproxAllSize() + "\t\t\tLeaf: " + arl.getApproxLeafSize() + "\tAbstract: " + arl.getApproxAbstractSize() + "\n");
			}
			summaryText.add("Coverage of all/expanded approx rule set (rule):        " + formatter.format((Float)arl.getApproxAllCovR()) + "%\t\t\t\t\tAbstract: " + formatter.format(arl.getApproxAbstractCovR()) + "%\n\n");

			if ((arl.getApproxMinMaxBasisLeafSize() + arl.getApproxLeafSize()) < 10){
				summaryText.add("Total number of approx association rules extracted:     " + (arl.getApproxMinMaxBasisSize() + arl.getApproxAllSize()) + "\t\t\tLeaf: " + (arl.getApproxMinMaxBasisLeafSize() + arl.getApproxLeafSize()) + "\t\tAbstract: " + (arl.getApproxMinMaxBasisAbstractSize() + arl.getApproxAbstractSize()) + "\n");
			}
			else{
				summaryText.add("Total number of approx association rules extracted:     " + (arl.getApproxMinMaxBasisSize() + arl.getApproxAllSize()) + "\t\t\tLeaf: " + (arl.getApproxMinMaxBasisLeafSize() + arl.getApproxLeafSize()) + "\tAbstract: " + (arl.getApproxMinMaxBasisAbstractSize() + arl.getApproxAbstractSize()) + "\n");
			}

			summaryText.add("Total coverage of approx rule set(s):                   " + formatter.format((Float)arl.getApproxRuleCoverage()) + "%\t\t\t\t\tAbstract: " + formatter.format((Float)arl.getApproxAbstractRuleCoverage()) + "%\n");
			summaryText.add("--------------------------------------------------------------------------------------------------------------------------\n");
			summaryText.add("Total time taken to extract association rules:          " + processTime + " ms");

			for (int i = 0; i < summaryText.size(); i++){
				statsArea.append((String)summaryText.get(i));
			}
		}
	}

	/**
	 * tmr class.
	 * Private class that models the data table that will hold the
	 * list of association rules that were mined/extracted from the
	 * previously specified dataset.
	 */
	private class tmr extends AbstractTableModel{

		private Vector rowData;

		final String[] columnNames = {"Antecedent", "Conseqent", "Support", "Confidence"};

		/**
		 * tmr method.
		 * Constructor.
		 * Method that initialise this class and sets up the table
		 * structure and content for viewing.
		 */
		public tmr(AssociationRuleList arl, int mode, String[] list){
			int[] a, c;
			String items;
			ArrayList rules, sup, con, cov;
			rowData = new Vector();
			if (mode == 1){
				rules = arl.getExactMinMaxBasis();
				sup = arl.getExactMinMaxBasisSup();
				con = null;
			}
			else if (mode == 2){
				rules = arl.getExactAll();
				sup = arl.getExactAllSup();
				con = null;
			}
			else if (mode == 3){
				rules = arl.getApproxMinMaxBasis();
				sup = arl.getApproxMinMaxBasisSup();
				con = arl.getApproxMinMaxBasisCon();
			}
			else{
				rules = arl.getApproxAll();
				sup = arl.getApproxAllSup();
				con = arl.getApproxAllCon();
			}
			for (int i = 0; i < rules.size(); i++){
				Object[] rule = (Object[])rules.get(i);
				a = (int[])rule[0];
				c = (int[])rule[1];
				Vector rows = new Vector();
				items = "";
				for (int j = 0; j < a.length; j++){
					if (items.length() == 0){
						items = "" + list[a[j] - 1];
					}
					else{
						items = items + "," + list[a[j] - 1];
					}
				}
				rows.add("[" + items + "]");
				items = "";
				for (int j = 0; j < c.length; j++){
					if (items.length() == 0){
						items = "" + list[c[j] - 1];
					}
					else{
						items = items + "," + list[c[j] - 1];
					}
				}
				rows.add("[" + items + "]");
				if (((Float)sup.get(i)).toString().length() > 4){
					rows.add(((Float)sup.get(i)).toString().substring(0, 5));
				}
				else{
					rows.add(((Float)sup.get(i)).toString());
				}
				if (con == null){
					rows.add(1.00);
				}
				else{
					if (((Float)con.get(i)).toString().length() > 4){
						rows.add(((Float)con.get(i)).toString().substring(0, 5));
					}
					else{
						rows.add(((Float)con.get(i)).toString());
					}
				}
				rowData.add(rows);
			}
		}

		/**
		 * getColumnName method.
		 * Method used to get the name of the chosen/specified
		 * column.
		 */
		public String getColumnName(int col){
    	   return columnNames[col].toString();
    	}

		/**
		 * getRowCount method.
		 * Method used to get the number of rows contained within
		 * the table.
		 */
		public int getRowCount(){
    		return rowData.size();
    	}

		/**
		 * getColumnCount method.
		 * Method used to get the number of columns contained within
		 * the table.
		 */
		public int getColumnCount(){
    		return columnNames.length;
    	}

		/**
		 * isCellEditable method.
		 * Method used to indicate if a particulr cell in the table can
		 * be edited. In this, no cell can be edited.
		 */
		public boolean isCellEditable(int row, int col){
			return false;
		}

		/**
		 * removeRows method.
		 * Method used to remove all of the rows with the table.
		 */
		public void removeRows(){
			rowData.clear();
			fireTableDataChanged();
		}

		/**
		 * getValueAt method.
		 * Method used to get the value contained within a specific
		 * cell of the table.
		 */
		public Object getValueAt(int row, int col){
			Object value;
			Vector tablerow;
			value = new Object();
			tablerow = (Vector)rowData.elementAt(row);
			value = tablerow.elementAt(col);
			return value;
		}

		/**
		 * setValueAt method.
		 * Method used to set the value contained within a specific
		 * cell of the table.
		 */
		public void setValueAt(Object value, int row, int col){
			Vector rows = (Vector)rowData.get(row);
			rows.setElementAt(value.toString(), col);
			rowData.setElementAt(rows, row);
			fireTableDataChanged();
		}

		/**
		 * getRowData method.
		 * Method used to get the data contained with a specified row
		 * of the table.
		 */
		public Vector getRowData(int row){
			return (Vector)rowData.get(row);
		}
	}
}