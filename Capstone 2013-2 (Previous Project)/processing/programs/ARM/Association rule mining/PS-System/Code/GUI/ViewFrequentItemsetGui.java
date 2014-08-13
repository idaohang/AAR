/**
 * ViewFrequentItemsetGui class.
 * 
 * Start Date: 20 December 2006
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

import java.util.ArrayList;
import java.util.Vector;

import GUI.ARMMainGui;

import Data.CandidateList;
import Data.FItemsetList;

public class ViewFrequentItemsetGui extends JFrame implements ActionListener{

	private ARMMainGui owner;

	private CandidateList cl;

	private FItemsetList fl;

	private tmc tableCModel;

	private tmf tableFModel;

	private candidateViewer cv;

	private frequentItemViewer fiv;

	private statDisplay sd;

	private JTabbedPane tabPane;

	private JPanel mainPanel, buttonPanel;

	private JButton okayButton;

	private JScrollPane tablePane1, tablePane2, sumPane3;

	private JTable table1, table2;

	private JTextArea statsArea;

	private LineBorder lBorder = new LineBorder(Color.black, 1);

	private Font standardFont = new Font ("SansSerif", Font.PLAIN, 12);
	private Font headFont = new Font ("SansSerif", Font.PLAIN, 14);

	private String dataPath;

	private long processTime;

	/**
	 * ViewFrequentItemsetGui method.
	 * Constructor.
	 * Method used to initialise and setup the GUI that displays the results
	 * from extracting the frequent itemsets from the specified dataset.
	 * This class allows the user to view both the candidates that were tested
	 * and the the actual frequent itemsets.
	 */
	public ViewFrequentItemsetGui(ARMMainGui main, String path, long time, CandidateList list1, FItemsetList list2, String[] list3){
		owner = main;
		cl = list1;
		fl = list2;
		dataPath = path;
		processTime = time;

		cv = new candidateViewer(cl, list3);
		fiv = new frequentItemViewer(fl, list3);
		sd = new statDisplay(fl);

		Container container = getContentPane();
		container.setBackground(Color.lightGray);
		container.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 7));
		this.setTitle("Frequent Itemset Summary");

		tabPane = new JTabbedPane();
		container.add(tabPane);
		tabPane.setFont(standardFont);
		tabPane.setBackground(Color.lightGray);

		tabPane.add("Candidate Lists", cv);
		tabPane.add("Frequent Itemsets", fiv);
		tabPane.add("Summary", sd);

		setupButtonPanel();
		container.add(buttonPanel);

		Toolkit theKit = this.getToolkit();
		Dimension windowSize = theKit.getScreenSize();
		setSize(900, 700);
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
		buttonPanel.setPreferredSize(new Dimension(885, 45));
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
		cl.clearData();
		cl = null;
		fl.clearData();
		fl = null;
		tableCModel.removeRows();
		tableCModel = null;
		tableFModel.removeRows();
		tableFModel = null;
		cv = null;
		fiv = null;
		sd = null;
		table1 = null;
		table2 = null;
		System.gc();
	}

	/**
	 * candidateViewer class.
	 * Private class that sets up the sub-GUI that will display
	 * the list of all the itemsets that were candidates to be
	 * frequent itemsets for the specified dataset.
	 */
	private class candidateViewer extends JPanel{

		/**
		 * candidateViewer method.
		 * Constructor.
		 * Method used to initialise this class and setup the sub-GUI
		 * that this class is responsible for displaying.
		 */
		public candidateViewer(CandidateList cl, String[] list1){
			setBackground(Color.lightGray);
			setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

			setupMainPanel(cl, list1);
			add(mainPanel);
		}

		/**
		 * setupMainPanel method.
		 * Method used to setup the main panel in this class's
		 * sub-GUI which will hold a table with the list of all the
		 * itemsets that were candidates to be frequent itemsets.
		 */
		final private void setupMainPanel(CandidateList cl, String[] list1){
			mainPanel = new JPanel();
			mainPanel.setBackground(Color.lightGray);
			mainPanel.setPreferredSize(new Dimension(885, 570));
			mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
			mainPanel.setBorder(lBorder);

			tablePane1 = new JScrollPane();
			tablePane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			tablePane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			tablePane1.setPreferredSize(new Dimension(875, 550));

			tableCModel = new tmc(cl, list1);
			table1 = new JTable(tableCModel);
			table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			setColumnAlign(4);

			tablePane1.setViewportView(table1);

			mainPanel.add(tablePane1);
		}

		/**
		 * setColumnAlign method.
		 * Method used to setup the horizontal alginment of the contents
		 * of the columns contained within the data table.
		 */
		final private void setColumnAlign(int cc){
			DefaultTableCellRenderer mcr = new DefaultTableCellRenderer();
			TableColumnModel tcm = table1.getColumnModel();
			tcm.getColumn(0).setPreferredWidth(625);
			tcm.getColumn(1).setPreferredWidth(75);
			tcm.getColumn(2).setPreferredWidth(75);
			tcm.getColumn(3).setPreferredWidth(75);
			mcr.setHorizontalAlignment(SwingConstants.CENTER);
			for (int i = 1; i < cc; i++){
				tcm.getColumn(i).setCellRenderer(mcr);
			}
		}
	}

	/**
	 * frequentItemViewer class.
	 * Private class that sets up the sub-GUI that will display
	 * the list of all the itemsets that are frequent itemset
	 * extracted from the specified dataset.
	 */
	private class frequentItemViewer extends JPanel{

		/**
		 * frequentItemViewer method.
		 * Constructor.
		 * Method used to initialise this class and setup the sub-GUI
		 * that this class is responsible for displaying.
		 */
		public frequentItemViewer(FItemsetList fl, String[] list1){
			setBackground(Color.lightGray);
			setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

			setupMainPanel(fl, list1);
			add(mainPanel);
		}

		/**
		 * setupMainPanel method.
		 * Method used to setup the main panel in this class's
		 * sub-GUI which will hold a table with the list of all the
		 * itemsets that were determined to be frequent itemsets.
		 */
		final private void setupMainPanel(FItemsetList fl, String[] list1){
			mainPanel = new JPanel();
			mainPanel.setBackground(Color.lightGray);
			mainPanel.setPreferredSize(new Dimension(885, 570));
			mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
			mainPanel.setBorder(lBorder);

			tablePane2 = new JScrollPane();
			tablePane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			tablePane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			tablePane2.setPreferredSize(new Dimension(875, 550));

			tableFModel = new tmf(fl, list1);
			table2 = new JTable(tableFModel);
			table2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table2.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			setColumnAlign(4);

			tablePane2.setViewportView(table2);

			mainPanel.add(tablePane2);
		}

		/**
		 * setColumnAlign method.
		 * Method used to setup the horizontal alginment of the contents
		 * of the columns contained within the data table.
		 */
		final private void setColumnAlign(int cc){
			DefaultTableCellRenderer mcr = new DefaultTableCellRenderer();
			TableColumnModel tcm = table2.getColumnModel();
			tcm.getColumn(0).setPreferredWidth(625);
			tcm.getColumn(1).setPreferredWidth(75);
			tcm.getColumn(2).setPreferredWidth(75);
			tcm.getColumn(3).setPreferredWidth(75);
			mcr.setHorizontalAlignment(SwingConstants.CENTER);
			for (int i = 1; i < cc; i++){
				tcm.getColumn(i).setCellRenderer(mcr);
			}
		}
	}

	/**
	 * statDisplay class.
	 * Private class that sets up the sub-GUI that will display
	 * a summary about the candidates and frequent itemsets that
	 * were extracted from the specified dataset.
	 */
	private class statDisplay extends JPanel{

		/**
		 * statDisplay method.
		 * Constructor.
		 * Method used to initialise this class and setup the sub-GUI
		 * that this class is responsible for displaying.
		 */
		public statDisplay(FItemsetList fl){
			setBackground(Color.lightGray);
			setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

			setupMainPanel(fl);
			add(mainPanel);
		}

		/**
		 * setupMainPanel method.
		 * Method used to setup the main panel in this class's
		 * sub-GUI which will hold a summary of the candidates
		 * considered and the actual frequent itemsets extracted.
		 * Will include total number, total number of each length
		 * and total processing time taken to determine the
		 * freqeuent itemsets.
		 */
		final private void setupMainPanel(FItemsetList fl){
			mainPanel = new JPanel();
			mainPanel.setBackground(Color.lightGray);
			mainPanel.setPreferredSize(new Dimension(885, 570));
			mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
			mainPanel.setBorder(lBorder);

			sumPane3 = new JScrollPane();
			sumPane3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			sumPane3.setPreferredSize(new Dimension(875, 550));

			statsArea = new JTextArea();
			statsArea.setLineWrap(true);
			statsArea.setWrapStyleWord(true);
			statsArea.setEditable(false);

			sumPane3.setViewportView(statsArea);

			mainPanel.add(sumPane3);
			formatText(fl);
		}

		/**
		 * formatText method.
		 * Method used to format the summary text displayed by this
		 * class.
		 */
		final private void formatText(FItemsetList fl){
			ArrayList summaryText = new ArrayList();

			summaryText.add("Frequent Itemset Summary\n");
			summaryText.add("------------------------\n\n");
			summaryText.add("Data File: " + dataPath + "\n\n");

			summaryText.add("Total number of frequent itemsets generated: " + fl.getTotalFrequent() + "\n\n");
			ArrayList fitems = fl.getFrequent();
			for (int i = 0; i < fitems.size(); i++){
				summaryText.add("Number of frequent itemsets of length " + (i + 1) + ": " + ((ArrayList)fitems.get(i)).size() + "\n");
			}
			summaryText.add("\n\n\n");
			summaryText.add("Total number of candidate frequent itemsets generated: " + cl.getTotalCandidates() + "\n\n");
			ArrayList citems = cl.getCandidates();
			for (int i = 0; i < citems.size(); i++){
				summaryText.add("Number of candidate frequent itemsets of length " + (i + 1) + ": " + ((ArrayList)citems.get(i)).size() + "\n");
			}
			summaryText.add("\n\n\n");
			summaryText.add("Total time taken to generate frequent itemsets: " + processTime + " ms");

			for (int i = 0; i < summaryText.size(); i++){
				statsArea.append((String)summaryText.get(i));
			}
		}
	}

	/**
	 * tmc class.
	 * Private class that models the data table that will hold the
	 * list of all the itemsets that were considered to be frequent
	 * itemsets (whether successful or not) and were therefore candidates
	 * to be the frequent itemsets extracted from the previously specified
	 * dataset.
	 */
	private class tmc extends AbstractTableModel{

		private Vector rowData;

		final String[] columnNames = {"Candidates", "Count", "Weight", "Support"};

		/**
		 * tmc method.
		 * Constructor.
		 * Method that initialise this class and sets up the table
		 * structure and content for viewing.
		 */
		public tmc(CandidateList cl, String[] list1){
			rowData = new Vector();
			String items = "";
			ArrayList candidates = cl.getCandidates();
			ArrayList counts = cl.getCounts();
			ArrayList weights = cl.getWeights();
			ArrayList support = cl.getSupports();
			for (int i = 0; i < candidates.size() ; i++){
				ArrayList data1 = (ArrayList)candidates.get(i);
				int[] data2 = (int[])counts.get(i);
				float[] data4 = null;
				if (weights.size() != 0){
					data4 = (float[])weights.get(i);
				}
				float[] data3 = (float[])support.get(i);
				for (int j = 0; j < data1.size(); j++){
					Vector rows = new Vector();
					items = "";
					int[] t = (int[])data1.get(j);
					for (int k = 0; k < t.length; k++){
						if (items.length() == 0){
							items = "" + list1[t[k] - 1];//t[k];
						}
						else{
							items = items + "," + list1[t[k] - 1];//t[k];
						}
					}
					rows.add("[" + items + "]");
					rows.add(data2[j]);
					if (weights.size() != 0){
						if (((Float)data4[j]).toString().length() > 4){
							rows.add(((Float)data4[j]).toString().substring(0, 5));
						}
						else{
							rows.add(((Float)data4[j]).toString());
						}
					}
					else{
						rows.add("N/A");
					}
					if (((Float)data3[j]).toString().length() > 4){
						rows.add(((Float)data3[j]).toString().substring(0, 5));
					}
					else{
						rows.add(((Float)data3[j]).toString());
					}
					rowData.add(rows);
				}
				Vector rows = new Vector();
				rows.add("");
				rows.add("");
				rows.add("");
				rows.add("");
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

	/**
	 * tmf class.
	 * Private class that models the data table that will hold the
	 * result list of the frequent itemsets extracted from the
	 * previously specified dataset.
	 */
	private class tmf extends AbstractTableModel{

		private Vector rowData;

		final String[] columnNames = {"Itemsets", "Count", "Weight", "Support"};

		/**
		 * tmf method.
		 * Constructor.
		 * Method that initialise this class and sets up the table
		 * structure and content for viewing.
		 */
		public tmf(FItemsetList fl, String[] list1){
			rowData = new Vector();
			String items = "";
			ArrayList frequent = fl.getFrequent();
			ArrayList counts = fl.getCounts();
			ArrayList weights = fl.getWeights();
			ArrayList support = fl.getSupports();
			for (int i = 0; i < frequent.size() ; i++){
				ArrayList data1 = (ArrayList)frequent.get(i);
				ArrayList data2 = (ArrayList)counts.get(i);
				ArrayList data4 = null;
				if (weights.size() != 0){
					data4 = (ArrayList)weights.get(i);
				}
				ArrayList data3 = (ArrayList)support.get(i);
				for (int j = 0; j < data1.size(); j++){
					Vector rows = new Vector();
					items = "";
					int[] t = (int[])data1.get(j);
					for (int k = 0; k < t.length; k++){
						if (items.length() == 0){
							items = "" + list1[t[k] - 1];//t[k];
						}
						else{
							items = items + "," + list1[t[k] - 1];//t[k];
						}
					}
					rows.add("[" + items + "]");
					rows.add(String.valueOf((Object)data2.get(j)));
					if (weights.size() != 0){
						if (((Float)data4.get(j)).toString().length() > 4){
							rows.add(((Float)data4.get(j)).toString().substring(0, 5));
						}
						else{
							rows.add(((Float)data4.get(j)).toString());
						}
					}
					else{
						rows.add("N/A");
					}
					if (((Float)data3.get(j)).toString().length() > 4){
						rows.add(((Float)data3.get(j)).toString().substring(0, 5));
					}
					else{
						rows.add(((Float)data3.get(j)).toString());
					}
					rowData.add(rows);
				}
				Vector rows = new Vector();
				rows.add("");
				rows.add("");
				rows.add("");
				rows.add("");
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