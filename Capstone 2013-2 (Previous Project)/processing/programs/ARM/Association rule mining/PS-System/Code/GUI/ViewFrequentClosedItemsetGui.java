/**
 * ViewFrequentClosedItemsetGui class.
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

import Data.FCItemsetList;

public class ViewFrequentClosedItemsetGui extends JFrame implements ActionListener{

	private ARMMainGui owner;

	private FCItemsetList fcl;

	private tmg tableGModel;

	private tmi tableIModel;

	private generatorViewer gv;

	private frequentClosedItemViewer fciv;

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
	 * ViewFrequentClosedItemsetGui method.
	 * Constructor.
	 * Method used to initialise and setup the GUI that displays the results
	 * from extracting the frequent closed itemsets from the specified dataset.
	 * This class allows the user to view frequent closed itemsets from two
	 * points of view, from the view of the generators or from the view of the
	 * closed itemsets.
	 */
	public ViewFrequentClosedItemsetGui(ARMMainGui main, String path, long time, FCItemsetList list, String[] list2){
		owner = main;
		fcl = list;
		dataPath = path;
		processTime = time;

		gv = new generatorViewer(fcl, list2);
		fciv = new frequentClosedItemViewer(fcl, list2);
		sd = new statDisplay(fcl);

		Container container = getContentPane();
		container.setBackground(Color.lightGray);
		container.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 7));
		this.setTitle("Frequent Closed Itemset Summary");

		tabPane = new JTabbedPane();
		container.add(tabPane);
		tabPane.setFont(standardFont);
		tabPane.setBackground(Color.lightGray);

		tabPane.add("Generator List View", gv);
		tabPane.add("Itemset List View", fciv);
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
		fcl.clearData();
		fcl = null;
		tableGModel.removeRows();
		tableGModel = null;
		tableIModel.removeRows();
		tableIModel = null;
		gv = null;
		fciv = null;
		table1 = null;
		table2 = null;
		System.gc();
	}

	/**
	 * generatorViewer class.
	 * Private class that sets up the sub-GUI that will display
	 * the list of all the frequent closed itemsets from the view
	 * of their generators that were extracted from the specified dataset.
	 */
	private class generatorViewer extends JPanel{

		/**
		 * generatorViewer method.
		 * Constructor.
		 * Method used to initialise this class and setup the sub-GUI
		 * that this class is responsible for displaying.
		 */
		public generatorViewer(FCItemsetList fcl, String[] list1){
			setBackground(Color.lightGray);
			setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

			setupMainPanel(fcl, list1);
			add(mainPanel);
		}

		/**
		 * setupMainPanel method.
		 * Method used to setup the main panel in this class's
		 * sub-GUI which will hold a table with the list of all the
		 * frequent closed itemsets from the view of their generators.
		 */
		final private void setupMainPanel(FCItemsetList fcl, String[] list1){
			mainPanel = new JPanel();
			mainPanel.setBackground(Color.lightGray);
			mainPanel.setPreferredSize(new Dimension(885, 570));
			mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
			mainPanel.setBorder(lBorder);

			tablePane1 = new JScrollPane();
			tablePane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			tablePane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			tablePane1.setPreferredSize(new Dimension(875, 550));

			tableGModel = new tmg(fcl, list1);
			table1 = new JTable(tableGModel);
			table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			setColumnAlign(3);

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
			tcm.getColumn(0).setPreferredWidth(362);
			tcm.getColumn(1).setPreferredWidth(362);
			tcm.getColumn(2).setPreferredWidth(125);
			mcr.setHorizontalAlignment(SwingConstants.CENTER);
			tcm.getColumn(2).setCellRenderer(mcr);
		}
	}

	/**
	 * frequentClosedItemViewer class.
	 * Private class that sets up the sub-GUI that will display
	 * the list of all the frequent closed itemsets from the view
	 * of the closed itemsets that were extracted from the specified
	 * dataset.
	 */
	private class frequentClosedItemViewer extends JPanel{

		/**
		 * frequentClosedItemViewer method.
		 * Constructor.
		 * Method used to initialise this class and setup the sub-GUI
		 * that this class is responsible for displaying.
		 */
		public frequentClosedItemViewer(FCItemsetList fcl, String[] list1){
			setBackground(Color.lightGray);
			setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

			setupMainPanel(fcl, list1);
			add(mainPanel);
		}

		/**
		 * setupMainPanel method.
		 * Method used to setup the main panel in this class's
		 * sub-GUI which will hold a table with the list of all the
		 * frequent closed itemsets from the view of the closed
		 * itemsets.
		 */
		final private void setupMainPanel(FCItemsetList fcl, String[] list1){
			mainPanel = new JPanel();
			mainPanel.setBackground(Color.lightGray);
			mainPanel.setPreferredSize(new Dimension(885, 570));
			mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
			mainPanel.setBorder(lBorder);

			tablePane2 = new JScrollPane();
			tablePane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			tablePane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			tablePane2.setPreferredSize(new Dimension(875, 550));

			tableIModel = new tmi(fcl, list1);
			table2 = new JTable(tableIModel);
			table2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table2.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			setColumnAlign(3);

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
			tcm.getColumn(0).setPreferredWidth(362);
			tcm.getColumn(1).setPreferredWidth(362);
			tcm.getColumn(2).setPreferredWidth(125);
			mcr.setHorizontalAlignment(SwingConstants.CENTER);
			tcm.getColumn(2).setCellRenderer(mcr);
		}
	}

	/**
	 * statDisplay class.
	 * Private class that sets up the sub-GUI that will display
	 * a summary about the frequent closed itemsets that were
	 * extracted from the specified dataset.
	 */
	private class statDisplay extends JPanel{

		/**
		 * statDisplay method.
		 * Constructor.
		 * Method used to initialise this class and setup the sub-GUI
		 * that this class is responsible for displaying.
		 */
		public statDisplay(FCItemsetList fcl){
			setBackground(Color.lightGray);
			setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

			setupMainPanel(fcl);
			add(mainPanel);
		}

		/**
		 * setupMainPanel method.
		 * Method used to setup the main panel in this class's
		 * sub-GUI which will hold a summary of the generators and
		 * actual frequent itemsets extracted. Will include total
		 * number, total number of each length and total processing
		 * time taken to determine the freqeuent itemsets.
		 */
		final private void setupMainPanel(FCItemsetList fcl){
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
			formatText(fcl);
		}

		/**
		 * formatText method.
		 * Method used to format the summary text displayed by this
		 * class.
		 */
		final private void formatText(FCItemsetList fcl){
			int currentLength = 0;
			int[] item;
			ArrayList fitems;
			ArrayList lengthCount = new ArrayList();
			ArrayList uniqueFCI = new ArrayList();
			ArrayList summaryText = new ArrayList();

			summaryText.add("Frequent Closed Itemset Summary\n");
			summaryText.add("-------------------------------\n\n");
			summaryText.add("Data File: " + dataPath + "\n\n");

			summaryText.add("Total number of generators generated: " + fcl.getTotalFreqClosed() + "\n\n");
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
				summaryText.add("Number of generators of length " + (i + 1) + ": " + (Integer)lengthCount.get(i) + "\n");
			}
			summaryText.add("\n\n\n");
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
			summaryText.add("Total number of frequent closed itemsets generated: " + uniqueFCI.size() + "\n\n");
			for (int i = 0; i < lengthCount.size(); i++){
				summaryText.add("Number of frequent closed itemsets of length " + (i + 1) + ": " + (Integer)lengthCount.get(i) + "\n");
			}
			summaryText.add("\n\n\n");
			summaryText.add("Total time taken to generate frequent closed itemsets: " + processTime + " ms");

			for (int i = 0; i < summaryText.size(); i++){
				statsArea.append((String)summaryText.get(i));
			}
		}
	}

	/**
	 * tmg class.
	 * Private class that models the data table that will hold the
	 * list of all the generators and their associated frequent closed
	 * itemsets that were extracted from the previously specified dataset.
	 */
	private class tmg extends AbstractTableModel{

		private Vector rowData;

		final String[] columnNames = {"Generators", "Associated Closed Itemsets", "Support"};

		/**
		 * tmg method.
		 * Constructor.
		 * Method that initialise this class and sets up the table
		 * structure and content for viewing.
		 */
		public tmg(FCItemsetList fcl, String[] list1){
			rowData = new Vector();
			String items;
			ArrayList generators = fcl.getFreqClosed();
			ArrayList supports = fcl.getSupports();
			for (int i = 0; i < generators.size(); i++){
				Object[] gen = (Object[])generators.get(i);
				Vector rows = new Vector();
				int[] d1 = (int[])gen[0];
				items = "";
				for (int j = 0; j < d1.length; j++){
					if (items.length() == 0){
						items = "" + list1[d1[j] - 1];
					}
					else{
						items = items + "," + list1[d1[j] - 1];
					}
				}
				rows.add("[" + items + "]");
				items = "";
				int[] d2 = (int[])gen[1];
				for (int j = 0; j < d2.length; j++){
					if (items.length() == 0){
						items = "" + list1[d2[j] - 1];
					}
					else{
						items = items + "," + list1[d2[j] - 1];
					}
				}
				rows.add("[" + items + "]");
				if (((Float)supports.get(i)).toString().length() > 4){
					rows.add(((Float)supports.get(i)).toString().substring(0, 5));
				}
				else{
					rows.add(((Float)supports.get(i)).toString());
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

	/**
	 * tmi class.
	 * Private class that models the data table that will hold the
	 * list of all the frequent closed itemsets that were extracted
	 * and their associated generators from the previously specified
	 * dataset.
	 */
	private class tmi extends AbstractTableModel{

		private Vector rowData;

		final String[] columnNames = {"Closed Itemsets", "Associated Generators", "Support"};

		/**
		 * tmi method.
		 * Constructor.
		 * Method that initialise this class and sets up the table
		 * structure and content for viewing.
		 */
		public tmi(FCItemsetList fcl, String[] list1){
			ArrayList list = collapseList(fcl, list1);
			rowData = new Vector();
			ArrayList item = (ArrayList)list.get(0);
			ArrayList gen = (ArrayList)list.get(1);
			ArrayList sup = (ArrayList)list.get(2);
			for (int i = 0; i < item.size(); i++){
				Vector rows = new Vector();
				rows.add((String)item.get(i));
				rows.add((String)gen.get(i));
				if (((Float)sup.get(i)).toString().length() > 4){
					rows.add(((Float)sup.get(i)).toString().substring(0, 5));
				}
				else{
					rows.add(((Float)sup.get(i)).toString());
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

		/**
		 * collapseList method.
		 * Method used to convert the list that holds all of the generators,
		 * frequent closed itemsets and their supports from one that is
		 * based on the generators, to one that is based on the closed
		 * itemsets.
		 */
		final private ArrayList collapseList(FCItemsetList fcl, String[] list1){
			String items = "";
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
				}
			}
			itemList.add(item);
			itemList.add(gen);
			itemList.add(sup);
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
	}
}