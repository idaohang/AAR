/**
 * SetMinSupGui class.
 * 
 * Start Date: 01 December 2006
 * @author Gavin Shaw
 * @version 1.0
 *
 * Log:
 *		  1.0 Start version of class.
 */
package GUI;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
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

public class SetMinSupGui extends JFrame implements ActionListener{

	ARMMainGui owner;

	private tml tableModel;

	private JPanel headPanel, tablePanel, buttonPanel;

	private JLabel headLabel;

	private JRadioButton decimalButton, percentButton, explicitButton;

	private ButtonGroup formatGroup;

	private JButton okayButton;

	private JTable levelTable;

	private JScrollPane tablePane;

	private LineBorder lBorder = new LineBorder(Color.black, 1);

	private Font standardFont = new Font ("SansSerif", Font.PLAIN, 12);

	private Font headFont = new Font ("SansSerif", Font.PLAIN, 14);

	private int rc;

	/**
	 * SetMinSupGui method.
	 * Constructor.
	 * Oversees the setup of the main GUI to allow user interaction with the system.
	 */
	public SetMinSupGui(ARMMainGui main, int levels, int recCount){
		rc = recCount;
		owner = main;
		Container container = getContentPane();
		container.setBackground(Color.lightGray);
		container.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));
		this.setTitle("Setup Min Support Thresholds");

		setupHeadPanel();
		setupTablePanel(levels, recCount);
		setupButtonPanel();

		container.add(headPanel);
		container.add(tablePanel);
		container.add(buttonPanel);

		Toolkit theKit = this.getToolkit();
		Dimension windowSize = theKit.getScreenSize();
		setSize(400, 400);
		setLocation(new Point(0, 0));
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);
		owner.setEnabled(false);
	}

	final private void setupHeadPanel(){
		headPanel = new JPanel();
		headPanel.setBackground(Color.lightGray);
		headPanel.setPreferredSize(new Dimension(391, 75));
		headPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
		headPanel.setBorder(lBorder);

		headLabel = new JLabel("  Specify Minimum Support Format:  ");
		headLabel.setFont(headFont);
		headLabel.setBackground(Color.lightGray);
		headLabel.setHorizontalAlignment(JLabel.CENTER);
		headLabel.setPreferredSize(new Dimension(300, 25));

		decimalButton = new JRadioButton("Decimal", true);
		decimalButton.setBackground(Color.lightGray);
		decimalButton.setForeground(Color.black);
		decimalButton.setFont(standardFont);
		decimalButton.setPreferredSize(new Dimension(120, 25));

		percentButton = new JRadioButton("Percent", false);
		percentButton.setBackground(Color.lightGray);
		percentButton.setForeground(Color.black);
		percentButton.setFont(standardFont);
		percentButton.setPreferredSize(new Dimension(120, 25));

		explicitButton = new JRadioButton("Explicit", false);
		explicitButton.setBackground(Color.lightGray);
		explicitButton.setForeground(Color.black);
		explicitButton.setFont(standardFont);
		explicitButton.setPreferredSize(new Dimension(120, 25));

		formatGroup = new ButtonGroup();
		formatGroup.add(decimalButton);
		formatGroup.add(percentButton);
		formatGroup.add(explicitButton);

		headPanel.add(headLabel);
		headPanel.add(decimalButton);
		headPanel.add(percentButton);
		headPanel.add(explicitButton);
	}

	final private void setupTablePanel(int levels, int recCount){
		tablePanel = new JPanel();
		tablePanel.setBackground(Color.lightGray);
		tablePanel.setPreferredSize(new Dimension(391, 230));
		tablePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		tablePanel.setBorder(lBorder);

		tablePane = new JScrollPane();
		tablePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		tablePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		tablePane.setPreferredSize(new Dimension(380, 220));

		tableModel = new tml(levels, recCount);
		levelTable = new JTable(tableModel);
		levelTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		levelTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setColumnAlign();

		tablePane.setViewportView(levelTable);

		tablePanel.add(tablePane);
	}

	final private void setupButtonPanel(){
		buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.lightGray);
		buttonPanel.setPreferredSize(new Dimension(391, 45));
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

	final private void setColumnAlign(){
		DefaultTableCellRenderer mcr = new DefaultTableCellRenderer();
		TableColumnModel tcm = levelTable.getColumnModel();
		tcm.getColumn(0).setPreferredWidth(230);
		tcm.getColumn(1).setPreferredWidth(125);
		mcr.setHorizontalAlignment(SwingConstants.CENTER);
		tcm.getColumn(1).setCellRenderer(mcr);
	}

	final public void actionPerformed(ActionEvent event){
		float s1;
		int s2;
		boolean okay = false;
		if (event.getSource() == okayButton){
			ArrayList minSup = new ArrayList();
			if (decimalButton.isSelected()){
				minSup.add(1);
				for (int i = 0; i < levelTable.getRowCount(); i++){
					try{
						s1 = Float.valueOf(levelTable.getValueAt(i, 1).toString()).floatValue();
						if (s1 > 0 && s1 <= 1){
							minSup.add(s1);
							okay = true;
						}
						else{
							i = levelTable.getRowCount();
							okay = false;
							JOptionPane.showMessageDialog(null, "At least one value for the minimum support is invalid.\n" +
																			"Ensure that all entries are numerical values between 0 and 1.",
																			"Invalid Support Value(s)", JOptionPane.ERROR_MESSAGE);
						}
					}
					catch (NumberFormatException e){
						i = levelTable.getRowCount();
						okay = false;
						JOptionPane.showMessageDialog(null, "At least one value for the minimum support is invalid.\n" +
																		"Ensure that all entries are numerical values between 0 and 1.",
																		"Invalid Support Value(s)", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			else if (percentButton.isSelected()){
				minSup.add(2);
				for (int i = 0; i < levelTable.getRowCount(); i++){
					try{
						s1 = Float.valueOf(levelTable.getValueAt(i, 1).toString()).floatValue();
						if (s1 > 0 && s1 <= 100){
							minSup.add(s1/(float)100);
							okay = true;
						}
						else{
							i = levelTable.getRowCount();
							okay = false;
							JOptionPane.showMessageDialog(null, "At least one value for the minimum support is invalid.\n" +
																			"Ensure that all entries are numerical integer values between 0 and 100.",
																			"Invalid Support Value(s)", JOptionPane.ERROR_MESSAGE);
						}
					}
					catch (NumberFormatException e){
						i = levelTable.getRowCount();
						okay = false;
						JOptionPane.showMessageDialog(null, "At least one value for the minimum support is invalid.\n" +
																		"Ensure that all entries are numerical integer values between 0 and 100.",
																		"Invalid Support Value(s)", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			else{
				minSup.add(3);
				for (int i = 0; i < levelTable.getRowCount(); i++){
					try{
						s2 = Integer.valueOf(levelTable.getValueAt(i, 1).toString()).intValue();
						if (s2 <= rc && s2 > 0){
							minSup.add(s2);
							okay = true;
						}
						else{
							i = levelTable.getRowCount();
							okay = false;
							JOptionPane.showMessageDialog(null, "At least one value for the minimum support is invalid.\n" +
																			"Ensure that all entries are numerical integer values no higher than\n" +
																			"the total number of transactions in the dataset.",
																			"Invalid Support Value(s)", JOptionPane.ERROR_MESSAGE);
						}
					}
					catch (NumberFormatException e){
						i = levelTable.getRowCount();
						okay = false;
						JOptionPane.showMessageDialog(null, "At least one value for the minimum support is invalid.\n" +
																		"Ensure that all entries are numerical integer values no higher than\n" +
																		"the total number of transactions in the dataset.",
																		"Invalid Support Value(s)", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			if (okay){
				owner.levelMinSup(minSup);
				owner.setEnabled(true);
				this.dispose();
			}
		}
	}

	private class tml extends AbstractTableModel{

		private Vector rowData;

		final String[] columnNames = {"Level", "Min. Support"};

		/**
		 * tmc method.
		 * Constructor.
		 * Method that initialise this class and sets up the table
		 * structure and content for viewing.
		 */
		public tml(int levels, int recCount){
			rowData = new Vector();
			for (int i = 0; i < levels; i++){
				Vector rows = new Vector();
				rows.add(i + 1);
				if (decimalButton.isSelected()){
					rows.add(0.5);
				}
				else if (percentButton.isSelected()){
					rows.add(50);
				}
				else{
					rows.add(recCount/2);
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
			if (col == 1){
				return true;
			}
			else{
				return false;
			}
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