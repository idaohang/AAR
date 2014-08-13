/**
 * ViewInputDataGui class.
 * 
 * Start Date: 08 December 2006
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
import javax.swing.JTable;
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

import Data.TransRecords;

public class ViewInputDataGui extends JFrame implements ActionListener{

	ARMMainGui owner;

	TransRecords tr;

	private tm tableModel;

	private Vector rowData;

	private JPanel mainPanel, buttonPanel;

	private JButton okayButton;

	private JScrollPane tablePane;

	private JTable table;

	private LineBorder lBorder = new LineBorder(Color.black, 1);

	private Font standardFont = new Font ("SansSerif", Font.PLAIN, 12);

	/**
	 * ViewInputDataGui method.
	 * Constructor.
	 * Method used to intialise and setup the GUI that displays the transactions
	 * read in or loaded from the specified dataset.
	 */
	public ViewInputDataGui(ARMMainGui main, TransRecords rec){
		owner = main;
		tr = rec;

		Container container = getContentPane();
		container.setBackground(Color.lightGray);
		container.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 7));
		this.setTitle("Input Data Summary");

		setupMainPanel();
		setupButtonPanel();
		container.add(mainPanel);
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
	 * setupMainPanel method.
	 * Method that sets up the main panel which displays the transactions
	 * as they are stored in memory from the dataset previously specified.
	 */
	final private void setupMainPanel(){
		mainPanel = new JPanel();
		mainPanel.setBackground(Color.lightGray);
		mainPanel.setPreferredSize(new Dimension(885, 605));
		mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
		mainPanel.setBorder(lBorder);

		tablePane = new JScrollPane();
		tablePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		tablePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		tablePane.setPreferredSize(new Dimension(875, 585));

		tableModel = new tm(tr.getRec(), tr.getNames());
		table = new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		int colCount = tr.getRecordLength() + 1;
		setColumnAlign(colCount);

		tablePane.setViewportView(table);

		mainPanel.add(tablePane);
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
	 * setColumnAlign method.
	 * Method used to setup the horizontal alginment of the contents
	 * of the columns contained within the data table.
	 */
	final private void setColumnAlign(int cc){
		DefaultTableCellRenderer mcr = new DefaultTableCellRenderer();
		TableColumnModel tcm = table.getColumnModel();
		mcr.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0; i < cc; i++){
			tcm.getColumn(i).setCellRenderer(mcr);
		}
	}

	/**
	 * clearData method.
	 * Method used to destroy and clear the variables that hold the
	 * data table (references and actual data) and the table model.
	 * This is to free up memory.
	 */
	final public void clearData(){
		tr.clearData();
		tr = null;
		tableModel.removeRows();
		tableModel = null;
		table = null;
		System.gc();
	}

	/**
	 * tm class.
	 * Private class that models the data table that will hold the
	 * data from the transactions that were successfully read in/loaded
	 * from the previously specified dataset.
	 */
	private class tm extends AbstractTableModel{

		final String[] columnNames;

		/**
		 * tm method.
		 * Constructor.
		 * Method that initialise this class and sets up the table
		 * structure and content for viewing.
		 */
		public tm(Object[] r, String[] a){
			int a1 = ((String[])r[0]).length;
			columnNames = new String[a1 + 1];
			columnNames[0] = "Trans. ID";
			for (int i = 0; i < a1; i++){
				columnNames[i + 1] = a[i];
			}
			rowData = new Vector();
			int r1 = r.length;
			for (int i = 0; i < r1; i++){
				String[] data = (String[])r[i];
				Vector rows = new Vector();
				rows.add("" + i);
				for (int j = 0; j < data.length; j++){
					rows.add(data[j]);
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