/**
 * ViewEvaluationGui class.
 * 
 * Start Date: 26 October 2008
 * @author Gavin Shaw
 * @version 1.0
 *
 * Log:
 *		  1.0 Start version of class.
 */
package GUI;

import javax.swing.JButton;
import javax.swing.JComboBox;
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

import java.awt.BorderLayout;
import java.awt.CardLayout;
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

import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.Vector;

import GUI.ARMMainGui;

import Data.AssociationRuleList;

public class ViewEvaluationGui extends JFrame implements ActionListener{
	
	private ARMMainGui owner;
	
	private AssociationRuleList arl;
	
	private divView div;
	
	private disView dis;
	
	private dd2View dd2;
	
	private covView cov;
	
	private JPanel mainPanelD1, mainPanelD2, mainPanelD3, mainPanelC, buttonPanel;
	
	private JScrollPane covsumPane, covebPane, coveePane, covabPane, covaePane;
			  
	private JScrollPane divsumPane, divebPane, diveePane, divabPane, divaePane;
	
	private JScrollPane dissumPane, distrPane, disebPane, diseePane, disabPane, disaePane;
	
	private JScrollPane dd2sumPane, dd2ebPane, dd2eePane, dd2abPane, dd2aePane;
	
	private JTable covebt, coveet, covabt, covaet;

	private JTable divebt, diveet, divabt, divaet;
	
	private JTable distrt, disebt, diseet, disabt, disaet;
	
	private JTable dd2ebt, dd2eet, dd2abt, dd2aet;
	
	private JTabbedPane tabPane;
	
	private JTextArea covSumArea, divSumArea, disSumArea, dd2SumArea;
	
	private JButton okayButton;
	
	private LineBorder lBorder = new LineBorder(Color.black, 1);
	
	private Font standardFont = new Font ("SansSerif", Font.PLAIN, 12);
	private Font headFont = new Font ("SansSerif", Font.PLAIN, 14);
	
	private String dataPath;
	
	private Object[] attNames;
	
	public ViewEvaluationGui(ARMMainGui armg, String path, long time, AssociationRuleList list, String[] list2, String[] list3, Object[] names){
		owner = armg;
		arl = list;
		dataPath = path;
		attNames = names;

		div = new divView(arl, list2);
		dis = new disView(arl, list2, list3);
		dd2 = new dd2View(arl, list2, list3);
		cov = new covView(arl, list2);

		Container container = getContentPane();
		container.setBackground(Color.lightGray);
		container.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 7));
		this.setTitle("Association Rules Evaluation");

		tabPane = new JTabbedPane();
		container.add(tabPane);
		tabPane.setFont(standardFont);
		tabPane.setBackground(Color.lightGray);

		tabPane.add("Diversity", div);
		tabPane.add("Distribution", dis);
		tabPane.add("Distance", dd2);
		tabPane.add("Coverage", cov);

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
	
	private class divView extends JPanel implements ItemListener{
		
		JPanel sumCard, ebCard, eeCard, abCard, aeCard;
		
		tmrD2 ebtmr, eetmr, abtmr, aetmr;
			
		JComboBox clist;
			
		String sumPanel = "Diversity Summary";
		String ebPanel = "Exact Basis Rules";
		String eePanel = "Exact Expanded Rules";
		String abPanel = "Approx Basis Rules";
		String aePanel = "Approx Expanded Rules";
		String panelList[] = {sumPanel, ebPanel, eePanel, abPanel, aePanel};
		
		public divView(AssociationRuleList arl, String[] list2){
			setBackground(Color.lightGray);
			setLayout(new BorderLayout(5, 7));
			
			setupMainPanel(arl, list2);
			add(clist, BorderLayout.NORTH);
			add(mainPanelD1, BorderLayout.CENTER);
		}
		
		final private void setupMainPanel(AssociationRuleList arl, String[] list2){
			clist = new JComboBox(panelList);
			clist.setPreferredSize(new Dimension(200, 20));
			clist.setEditable(false);
			clist.addItemListener(this);
			
			mainPanelD1 = new JPanel();
			mainPanelD1.setBackground(Color.lightGray);
			mainPanelD1.setPreferredSize(new Dimension(1009, 550));
			mainPanelD1.setLayout(new CardLayout());
			mainPanelD1.setBorder(lBorder);
			
			sumCard = new JPanel();
			sumCard.setBackground(Color.lightGray);
			ebCard = new JPanel();
			ebCard.setBackground(Color.lightGray);
			eeCard = new JPanel();
			eeCard.setBackground(Color.lightGray);
			abCard = new JPanel();
			abCard.setBackground(Color.lightGray);
			aeCard = new JPanel();
			aeCard.setBackground(Color.lightGray);
			
			mainPanelD1.add(sumCard, sumPanel);
			mainPanelD1.add(ebCard, ebPanel);
			mainPanelD1.add(eeCard, eePanel);
			mainPanelD1.add(abCard, abPanel);
			mainPanelD1.add(aeCard, aePanel);
			
			divsumPane = new JScrollPane();
			divsumPane.setBackground(Color.lightGray);
			divsumPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			divsumPane.setPreferredSize(new Dimension(999, 540));

			divSumArea = new JTextArea();
			divSumArea.setLineWrap(true);
			divSumArea.setWrapStyleWord(true);
			divSumArea.setEditable(false);

			divsumPane.setViewportView(divSumArea);

			sumCard.add(divsumPane);
			
			divebPane = new JScrollPane();
			divebPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			divebPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			divebPane.setPreferredSize(new Dimension(999, 540));

			ebtmr = new tmrD2(arl, 1, list2);
			divebt = new JTable(ebtmr);
			divebt.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			divebt.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			setColumnAlign(3, 1);
			
			divebPane.setViewportView(divebt);
			
			ebCard.add(divebPane);
			
			diveePane = new JScrollPane();
			diveePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			diveePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			diveePane.setPreferredSize(new Dimension(999, 540));

			eetmr = new tmrD2(arl, 2, list2);
			diveet = new JTable(eetmr);
			diveet.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			diveet.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			setColumnAlign(3, 2);
			
			diveePane.setViewportView(diveet);
			
			eeCard.add(diveePane);
			
			divabPane = new JScrollPane();
			divabPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			divabPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			divabPane.setPreferredSize(new Dimension(999, 540));

			abtmr = new tmrD2(arl, 3, list2);
			divabt = new JTable(abtmr);
			divabt.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			divabt.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			setColumnAlign(3, 3);
			
			divabPane.setViewportView(divabt);
			
			abCard.add(divabPane);
			
			divaePane = new JScrollPane();
			divaePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			divaePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			divaePane.setPreferredSize(new Dimension(999, 540));

			aetmr = new tmrD2(arl, 4, list2);
			divaet = new JTable(aetmr);
			divaet.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			divaet.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			setColumnAlign(3, 4);
			
			divaePane.setViewportView(divaet);
			
			aeCard.add(divaePane);
			
			formatText(arl);
		}
		
		public void itemStateChanged(ItemEvent evt){
			CardLayout cl = (CardLayout)(mainPanelD1.getLayout());
			cl.show(mainPanelD1, (String)evt.getItem());
		}
		
		final private void setColumnAlign(int cc, int mode){
			DefaultTableCellRenderer mcr = new DefaultTableCellRenderer();
			TableColumnModel tcm;
			if (mode == 1){
				tcm = divebt.getColumnModel();
			}
			else if (mode == 2){
				tcm = diveet.getColumnModel();
			}
			else if (mode == 3){
				tcm = divabt.getColumnModel();
			}
			else{
				tcm = divaet.getColumnModel();
			}
			tcm.getColumn(0).setPreferredWidth(352);
			tcm.getColumn(1).setPreferredWidth(352);
			tcm.getColumn(2).setPreferredWidth(90);
			tcm.getColumn(3).setPreferredWidth(90);
			tcm.getColumn(4).setPreferredWidth(90);
			mcr.setHorizontalAlignment(SwingConstants.CENTER);
			tcm.getColumn(2).setCellRenderer(mcr);
			tcm.getColumn(3).setCellRenderer(mcr);
			tcm.getColumn(4).setCellRenderer(mcr);
		}
		
		final private void formatText(AssociationRuleList arl){
			NumberFormat formatter = NumberFormat.getNumberInstance();
			formatter.setMaximumFractionDigits(3);
			formatter.setMinimumFractionDigits(3);
			ArrayList summaryText = new ArrayList();
			ArrayList div;
			float[] d;
			int count = 0;
			float minCHR, maxCHR, aveCHR, totminCHR = 1, totmaxCHR = 0, totaveCHR = 0;
			float minLD, maxLD, aveLD, totminLD = 1, totmaxLD = 0, totaveLD = 0;
			float mindiv, maxdiv, avediv, totmindiv = 1, totmaxdiv = 0, totavediv = 0;

			summaryText.add("Association Rule Diversity Evaluation Summary\n");
			summaryText.add("Data File: " + dataPath + "\n");
			summaryText.add("==========================================================================================================================\n");
			summaryText.add("Diversity Evaluation Summary\n");
			summaryText.add("--------------------------------------------------------------------------------------------------------------------------\n");
			
			div = arl.getExactMinMaxBasisDiv();
			if (div.size() > 0){
				minCHR = Float.MAX_VALUE;
				maxCHR = Float.MIN_VALUE;
				aveCHR = 0;
				minLD = Float.MAX_VALUE;
				maxLD = Float.MIN_VALUE;
				aveLD = 0;
				mindiv = Float.MAX_VALUE;
				maxdiv = Float.MIN_VALUE;
				avediv = 0;
				for (int i = 0; i < div.size(); i++){
					d = (float[])div.get(i);
					if (d[1] > maxCHR){
						maxCHR = d[1];
					}
					if (d[1] < minCHR){
						minCHR = d[1];
					}
					aveCHR = aveCHR + d[1];
					if (d[0] > maxLD){
						maxLD = d[0];
					}
					if (d[0] < minLD){
						minLD = d[0];
					}
					aveLD = aveLD + d[0];
					if (d[2] > maxdiv){
						maxdiv = d[2];
					}
					if (d[2] < mindiv){
						mindiv = d[2];
					}
					avediv = avediv + d[2];
				}
				if (maxCHR > totmaxCHR){
					totmaxCHR = maxCHR;
				}
				if (minCHR < totminCHR){
					totminCHR = minCHR;
				}
				if (maxLD > totmaxLD){
					totmaxLD = maxLD;
				}
				if (minLD < totminLD){
					totminLD = minLD;
				}
				if (maxdiv > totmaxdiv){
					totmaxdiv = maxdiv;
				}
				if (mindiv < totmindiv){
					totmindiv = mindiv;
				}
				totaveCHR = totaveCHR + aveCHR;
				totaveLD = totaveLD + aveLD;
				totavediv = totavediv + avediv;
				count = count + div.size();
				aveCHR = aveCHR / (float)div.size();
				aveLD = aveLD / (float)div.size();
				avediv = avediv / (float)div.size();
			}
			else{
				minCHR = 0;
				maxCHR = 0;
				aveCHR = 0;

				minLD = 0;
				maxLD = 0;
				aveLD = 0;
				mindiv = 0;
				maxdiv = 0;
				avediv = 0;
			}
			summaryText.add("Exact Basis Evaluation Summary\n");
			summaryText.add("HRD (Hierarchical Relationship Distance):     Min: " + formatter.format(minCHR) + "\tMax: " + formatter.format(maxCHR) + "\tAve: " + formatter.format(aveCHR) + "\n");
			summaryText.add("LD  (Level Distance/Diversity):               Min: " + formatter.format(minLD) + "\tMax: " + formatter.format(maxLD) + "\tAve: " + formatter.format(aveLD) + "\n");
			summaryText.add("Total Diversity:                              Min: " + formatter.format(mindiv) + "\tMax: " + formatter.format(maxdiv) + "\tAve: " + formatter.format(avediv) + "\n");
			summaryText.add("--------------------------------------------------------------------------------------------------------------------------\n");
			
			div = arl.getExactAllDiv();
			if (div.size() > 0){
				minCHR = Float.MAX_VALUE;
				maxCHR = Float.MIN_VALUE;
				aveCHR = 0;
				minLD = Float.MAX_VALUE;
				maxLD = Float.MIN_VALUE;
				aveLD = 0;
				mindiv = Float.MAX_VALUE;
				maxdiv = Float.MIN_VALUE;
				avediv = 0;
				for (int i = 0; i < div.size(); i++){
					d = (float[])div.get(i);
					if (d[1] > maxCHR){
						maxCHR = d[1];
					}
					if (d[1] < minCHR){
						minCHR = d[1];
					}
					aveCHR = aveCHR + d[1];
					if (d[0] > maxLD){
						maxLD = d[0];
					}
					if (d[0] < minLD){
						minLD = d[0];
					}
					aveLD = aveLD + d[0];
					if (d[2] > maxdiv){
						maxdiv = d[2];
					}
					if (d[2] < mindiv){
						mindiv = d[2];
					}
					avediv = avediv + d[2];
				}
				if (maxCHR > totmaxCHR){
					totmaxCHR = maxCHR;
				}
				if (minCHR < totminCHR){
					totminCHR = minCHR;
				}
				if (maxLD > totmaxLD){
					totmaxLD = maxLD;
				}
				if (minLD < totminLD){
					totminLD = minLD;
				}
				if (maxdiv > totmaxdiv){
					totmaxdiv = maxdiv;
				}
				if (mindiv < totmindiv){
					totmindiv = mindiv;
				}
				totaveCHR = totaveCHR + aveCHR;
				totaveLD = totaveLD + aveLD;
				totavediv = totavediv + avediv;
				count = count + div.size();
				aveCHR = aveCHR / (float)div.size();
				aveLD = aveLD / (float)div.size();
				avediv = avediv / (float)div.size();
			}
			else{
				minCHR = 0;
				maxCHR = 0;
				aveCHR = 0;
				minLD = 0;
				maxLD = 0;
				aveLD = 0;
				mindiv = 0;
				maxdiv = 0;
				avediv = 0;
			}
			summaryText.add("Exact Expanded Evaluation Summary\n");
			summaryText.add("HRD (Hierarchical Relationship Distance):     Min: " + formatter.format(minCHR) + "\tMax: " + formatter.format(maxCHR) + "\tAve: " + formatter.format(aveCHR) + "\n");
			summaryText.add("LD  (Level Distance/Diversity):               Min: " + formatter.format(minLD) + "\tMax: " + formatter.format(maxLD) + "\tAve: " + formatter.format(aveLD) + "\n");
			summaryText.add("Total Diversity:                              Min: " + formatter.format(mindiv) + "\tMax: " + formatter.format(maxdiv) + "\tAve: " + formatter.format(avediv) + "\n");
			summaryText.add("--------------------------------------------------------------------------------------------------------------------------\n");
			
			div = arl.getApproxMinMaxBasisDiv();
			if (div.size() > 0){
				minCHR = Float.MAX_VALUE;
				maxCHR = Float.MIN_VALUE;
				aveCHR = 0;
				minLD = Float.MAX_VALUE;
				maxLD = Float.MIN_VALUE;
				aveLD = 0;
				mindiv = Float.MAX_VALUE;
				maxdiv = Float.MIN_VALUE;
				avediv = 0;
				for (int i = 0; i < div.size(); i++){
					d = (float[])div.get(i);
					if (d[1] > maxCHR){
						maxCHR = d[1];
					}
					if (d[1] < minCHR){
						minCHR = d[1];
					}
					aveCHR = aveCHR + d[1];
					if (d[0] > maxLD){
						maxLD = d[0];
					}
					if (d[0] < minLD){
						minLD = d[0];
					}
					aveLD = aveLD + d[0];
					if (d[2] > maxdiv){
						maxdiv = d[2];
					}
					if (d[2] < mindiv){
						mindiv = d[2];
					}
					avediv = avediv + d[2];
				}
				if (maxCHR > totmaxCHR){
					totmaxCHR = maxCHR;
				}
				if (minCHR < totminCHR){
					totminCHR = minCHR;
				}
				if (maxLD > totmaxLD){
					totmaxLD = maxLD;
				}
				if (minLD < totminLD){
					totminLD = minLD;
				}
				if (maxdiv > totmaxdiv){
					totmaxdiv = maxdiv;
				}
				if (mindiv < totmindiv){
					totmindiv = mindiv;
				}
				totaveCHR = totaveCHR + aveCHR;
				totaveLD = totaveLD + aveLD;
				totavediv = totavediv + avediv;
				count = count + div.size();
				aveCHR = aveCHR / (float)div.size();
				aveLD = aveLD / (float)div.size();
				avediv = avediv / (float)div.size();
			}
			else{
				minCHR = 0;
				maxCHR = 0;
				aveCHR = 0;
				minLD = 0;
				maxLD = 0;
				aveLD = 0;
				mindiv = 0;
				maxdiv = 0;
				avediv = 0;
			}
			summaryText.add("Approximate Basis Evaluation Summary\n");
			summaryText.add("HRD (Hierarchical Relationship Distance):     Min: " + formatter.format(minCHR) + "\tMax: " + formatter.format(maxCHR) + "\tAve: " + formatter.format(aveCHR) + "\n");
			summaryText.add("LD  (Level Distance/Diversity):               Min: " + formatter.format(minLD) + "\tMax: " + formatter.format(maxLD) + "\tAve: " + formatter.format(aveLD) + "\n");
			summaryText.add("Total Diversity:                              Min: " + formatter.format(mindiv) + "\tMax: " + formatter.format(maxdiv) + "\tAve: " + formatter.format(avediv) + "\n");
			summaryText.add("--------------------------------------------------------------------------------------------------------------------------\n");
			
			div = arl.getApproxAllDiv();
			if (div.size() > 0){
				minCHR = Float.MAX_VALUE;
				maxCHR = Float.MIN_VALUE;
				aveCHR = 0;
				minLD = Float.MAX_VALUE;
				maxLD = Float.MIN_VALUE;
				aveLD = 0;
				mindiv = Float.MAX_VALUE;
				maxdiv = Float.MIN_VALUE;
				avediv = 0;
				for (int i = 0; i < div.size(); i++){
					d = (float[])div.get(i);
					if (d[1] > maxCHR){
						maxCHR = d[1];
					}
					if (d[1] < minCHR){
						minCHR = d[1];
					}
					aveCHR = aveCHR + d[1];
					if (d[0] > maxLD){
						maxLD = d[0];
					}
					if (d[0] < minLD){
						minLD = d[0];
					}
					aveLD = aveLD + d[0];
					if (d[2] > maxdiv){
						maxdiv = d[2];
					}
					if (d[2] < mindiv){
						mindiv = d[2];
					}
					avediv = avediv + d[2];
				}
				if (maxCHR > totmaxCHR){
					totmaxCHR = maxCHR;
				}
				if (minCHR < totminCHR){
					totminCHR = minCHR;
				}
				if (maxLD > totmaxLD){
					totmaxLD = maxLD;
				}
				if (minLD < totminLD){
					totminLD = minLD;
				}
				if (maxdiv > totmaxdiv){
					totmaxdiv = maxdiv;
				}
				if (mindiv < totmindiv){
					totmindiv = mindiv;
				}
				totaveCHR = totaveCHR + aveCHR;
				totaveLD = totaveLD + aveLD;
				totavediv = totavediv + avediv;
				count = count + div.size();
				aveCHR = aveCHR / (float)div.size();
				aveLD = aveLD / (float)div.size();
				avediv = avediv / (float)div.size();
			}
			else{
				minCHR = 0;
				maxCHR = 0;
				aveCHR = 0;
				minLD = 0;
				maxLD = 0;
				aveLD = 0;
				mindiv = 0;
				maxdiv = 0;
				avediv = 0;
			}
			summaryText.add("Approximate Expanded Evaluation Summary\n");
			summaryText.add("HRD (Hierarchical Relationship Distance):     Min: " + formatter.format(minCHR) + "\tMax: " + formatter.format(maxCHR) + "\tAve: " + formatter.format(aveCHR) + "\n");
			summaryText.add("LD  (Level Distance/Diversity):               Min: " + formatter.format(minLD) + "\tMax: " + formatter.format(maxLD) + "\tAve: " + formatter.format(aveLD) + "\n");
			summaryText.add("Total Diversity:                              Min: " + formatter.format(mindiv) + "\tMax: " + formatter.format(maxdiv) + "\tAve: " + formatter.format(avediv) + "\n");
			summaryText.add("--------------------------------------------------------------------------------------------------------------------------\n");
			
			summaryText.add("Overall Evaluation Summary\n");
			summaryText.add("HRD (Hierarchical Relationship Distance):     Min: " + formatter.format(totminCHR) + "\tMax: " + formatter.format(totmaxCHR) + "\tAve: " + formatter.format(totaveCHR / (float)count) + "\n");
			summaryText.add("LD  (Level Distance/Diversity):               Min: " + formatter.format(totminLD) + "\tMax: " + formatter.format(totmaxLD) + "\tAve: " + formatter.format(totaveLD / (float)count) + "\n");
			summaryText.add("Total Diversity:                              Min: " + formatter.format(totmindiv) + "\tMax: " + formatter.format(totmaxdiv) + "\tAve: " + formatter.format(totavediv / (float)count));

			for (int i = 0; i < summaryText.size(); i++){
				divSumArea.append((String)summaryText.get(i));
			}
		}
	}
	
	private class disView extends JPanel implements ItemListener{
		
		JPanel sumCard, trCard, ebCard, eeCard, abCard, aeCard;
		
		tmrD trtmr, ebtmr, eetmr, abtmr, aetmr;
			
		JComboBox clist;
			
		String sumPanel = "Distribution Summary";
		String trPanel = "All Association Rules";
		String ebPanel = "Exact Basis Rules";
		String eePanel = "Exact Expanded Rules";
		String abPanel = "Approx Basis Rules";
		String aePanel = "Approx Expanded Rules";
		String panelList[] = {sumPanel, trPanel, ebPanel, eePanel, abPanel, aePanel};
		
		public disView(AssociationRuleList arl, String[] list2, String[] list3){
			setBackground(Color.lightGray);
			setLayout(new BorderLayout(5, 7));
			
			setupMainPanel(arl, list2, list3);
			add(clist, BorderLayout.NORTH);
			add(mainPanelD2, BorderLayout.CENTER);
		}
		
		final private void setupMainPanel(AssociationRuleList arl, String[] list2, String[] list3){
			clist = new JComboBox(panelList);
			clist.setPreferredSize(new Dimension(200, 20));
			clist.setEditable(false);
			clist.addItemListener(this);
			
			mainPanelD2 = new JPanel();
			mainPanelD2.setBackground(Color.lightGray);
			mainPanelD2.setPreferredSize(new Dimension(1009, 550));
			mainPanelD2.setLayout(new CardLayout());
			mainPanelD2.setBorder(lBorder);
			
			sumCard = new JPanel();
			sumCard.setBackground(Color.lightGray);
			trCard = new JPanel();
			trCard.setBackground(Color.lightGray);
			ebCard = new JPanel();
			ebCard.setBackground(Color.lightGray);
			eeCard = new JPanel();
			eeCard.setBackground(Color.lightGray);
			abCard = new JPanel();
			abCard.setBackground(Color.lightGray);
			aeCard = new JPanel();
			aeCard.setBackground(Color.lightGray);
			
			mainPanelD2.add(sumCard, sumPanel);
			mainPanelD2.add(trCard, trPanel);
			mainPanelD2.add(ebCard, ebPanel);
			mainPanelD2.add(eeCard, eePanel);
			mainPanelD2.add(abCard, abPanel);
			mainPanelD2.add(aeCard, aePanel);
			
			dissumPane = new JScrollPane();
			dissumPane.setBackground(Color.lightGray);
			dissumPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			dissumPane.setPreferredSize(new Dimension(999, 540));

			disSumArea = new JTextArea();
			disSumArea.setLineWrap(true);
			disSumArea.setWrapStyleWord(true);
			disSumArea.setEditable(false);

			dissumPane.setViewportView(disSumArea);

			sumCard.add(dissumPane);
			
			distrPane = new JScrollPane();
			distrPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			distrPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			distrPane.setPreferredSize(new Dimension(999, 540));

			trtmr = new tmrD(arl, 1, list3);
			distrt = new JTable(trtmr);
			distrt.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			distrt.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			setColumnAlign(3, 1);
			
			distrPane.setViewportView(distrt);
			
			trCard.add(distrPane);
			
			disebPane = new JScrollPane();
			disebPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			disebPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			disebPane.setPreferredSize(new Dimension(999, 540));

			ebtmr = new tmrD(arl, 2, list3);
			disebt = new JTable(ebtmr);
			disebt.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			disebt.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			setColumnAlign(3, 2);
			
			disebPane.setViewportView(disebt);
			
			ebCard.add(disebPane);
			
			diseePane = new JScrollPane();
			diseePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			diseePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			diseePane.setPreferredSize(new Dimension(999, 540));

			eetmr = new tmrD(arl, 3, list3);
			diseet = new JTable(eetmr);
			diseet.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			diseet.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			setColumnAlign(3, 3);
			
			diseePane.setViewportView(diseet);
			
			eeCard.add(diseePane);
			
			disabPane = new JScrollPane();
			disabPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			disabPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			disabPane.setPreferredSize(new Dimension(999, 540));

			abtmr = new tmrD(arl, 4, list3);
			disabt = new JTable(abtmr);
			disabt.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			disabt.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			setColumnAlign(3, 4);
			
			disabPane.setViewportView(disabt);
			
			abCard.add(disabPane);
			
			disaePane = new JScrollPane();
			disaePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			disaePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			disaePane.setPreferredSize(new Dimension(999, 540));

			aetmr = new tmrD(arl, 5, list3);
			disaet = new JTable(aetmr);
			disaet.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			disaet.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			setColumnAlign(3, 5);
			
			disaePane.setViewportView(disaet);
			
			aeCard.add(disaePane);
			
			formatText(arl);
		}
		
		public void itemStateChanged(ItemEvent evt){
			CardLayout cl = (CardLayout)(mainPanelD2.getLayout());
			cl.show(mainPanelD2, (String)evt.getItem());
		}
		
		final private void setColumnAlign(int cc, int mode){
			DefaultTableCellRenderer mcr = new DefaultTableCellRenderer();
			TableColumnModel tcm;
			if (mode == 1){
				tcm = distrt.getColumnModel();
			}
			else if (mode == 2){
				tcm = disebt.getColumnModel();
			}
			else if (mode == 3){
				tcm = diseet.getColumnModel();
			}
			else if (mode == 4){
				tcm = disabt.getColumnModel();
			}
			else{
				tcm = disaet.getColumnModel();
			}
			tcm.getColumn(0).setPreferredWidth(345);
			tcm.getColumn(1).setPreferredWidth(105);
			tcm.getColumn(2).setPreferredWidth(105);
			tcm.getColumn(3).setPreferredWidth(105);
			tcm.getColumn(4).setPreferredWidth(105);
			tcm.getColumn(5).setPreferredWidth(105);
			tcm.getColumn(6).setPreferredWidth(105);
			mcr.setHorizontalAlignment(SwingConstants.CENTER);
			tcm.getColumn(1).setCellRenderer(mcr);
			tcm.getColumn(2).setCellRenderer(mcr);
			tcm.getColumn(3).setCellRenderer(mcr);
			tcm.getColumn(4).setCellRenderer(mcr);
			tcm.getColumn(5).setCellRenderer(mcr);
			tcm.getColumn(6).setCellRenderer(mcr);
		}
		
		final private void formatText(AssociationRuleList arl){
			Object[] disCounts = arl.getNodeFreqs();
			Object[] results2;
			int[] results;
			NumberFormat formatter = NumberFormat.getNumberInstance();
			formatter.setMaximumFractionDigits(3);
			ArrayList summaryText = new ArrayList();

			summaryText.add("Association Rule Distribution Evaluation Summary\n");
			summaryText.add("Data File: " + dataPath + "\n");
			summaryText.add("==========================================================================================================================\n");
			summaryText.add("Distribution Evaluation Summary\n");
			summaryText.add("--------------------------------------------------------------------------------------------------------------------------\n");
			
			results = determineUnique(disCounts, 2);
			results2 = determineMinMaxAve(disCounts, 2);
			summaryText.add("Exact Basis Evaluation Summary\n");
			summaryText.add("Total No. of rules: " + arl.getExactMinMaxBasisSize() + "\n");
			summaryText.add("Total No. of unique nodes: " + results[0] + "\tAntecedent: " + results[1] + "\tConsequent: " + results[2] + "\n");
			summaryText.add("Node Frequency Summary: Min: " + (Integer)results2[0] + "\tMax: " + (Integer)results2[1] + "\tAve: "+ formatter.format((Float)results2[2]) +"\n");
			summaryText.add("--------------------------------------------------------------------------------------------------------------------------\n");
			
			results = determineUnique(disCounts, 3);
			results2 = determineMinMaxAve(disCounts, 3);
			summaryText.add("Exact Expanded Evaluation Summary\n");
			summaryText.add("Total No. of rules: " + arl.getExactAllSize() + "\n");
			summaryText.add("Total No. of unique nodes: " + results[0] + "\tAntecedent: " + results[1] + "\tConsequent: " + results[2] + "\n");
			summaryText.add("Node Frequency Summary: Min: " + (Integer)results2[0] + "\tMax: " + (Integer)results2[1] + "\tAve: "+ formatter.format((Float)results2[2]) +"\n");
			summaryText.add("--------------------------------------------------------------------------------------------------------------------------\n");
			
			results = determineUnique(disCounts, 4);
			results2 = determineMinMaxAve(disCounts, 4);
			summaryText.add("Approximate Basis Evaluation Summary\n");
			summaryText.add("Total No. of rules: " + arl.getApproxMinMaxBasisSize() + "\n");
			summaryText.add("Total No. of unique nodes: " + results[0] + "\tAntecedent: " + results[1] + "\tConsequent: " + results[2] + "\n");
			summaryText.add("Node Frequency Summary: Min: " + (Integer)results2[0] + "\tMax: " + (Integer)results2[1] + "\tAve: "+ formatter.format((Float)results2[2]) +"\n");
			summaryText.add("--------------------------------------------------------------------------------------------------------------------------\n");
			
			results = determineUnique(disCounts, 5);
			results2 = determineMinMaxAve(disCounts, 5);
			summaryText.add("Approximate Expanded Evaluation Summary\n");
			summaryText.add("Total No. of rules: " + arl.getApproxAllSize() + "\n");
			summaryText.add("Total No. of unique nodes: " + results[0] + "\tAntecedent: " + results[1] + "\tConsequent: " + results[2] + "\n");
			summaryText.add("Node Frequency Summary: Min: " + (Integer)results2[0] + "\tMax: " + (Integer)results2[1] + "\tAve: "+ formatter.format((Float)results2[2]) +"\n");
			summaryText.add("--------------------------------------------------------------------------------------------------------------------------\n");
			
			results = determineUnique(disCounts, 1);
			results2 = determineMinMaxAve(disCounts, 1);
			summaryText.add("Overall Evaluation Summary\n");
			summaryText.add("Total No. of rules: " + (arl.getExactMinMaxBasisSize() + arl.getExactAllSize() + arl.getApproxMinMaxBasisSize() + arl.getApproxAllSize()) + "\n");
			summaryText.add("Total No. of unique nodes: " + results[0] + "\tAntecedent: " + results[1] + "\tConsequent: " + results[2] + "\n");
			summaryText.add("Node Frequency Summary: Min: " + (Integer)results2[0] + "\tMax: " + (Integer)results2[1] + "\tAve: "+ formatter.format((Float)results2[2]));

			for (int i = 0; i < summaryText.size(); i++){
				disSumArea.append((String)summaryText.get(i));
			}
		}
	}
	
	private class dd2View extends JPanel implements ItemListener{
		
		JPanel sumCard, ebCard, eeCard, abCard, aeCard;
		
		tmrD3 ebtmr, eetmr, abtmr, aetmr;
			
		JComboBox clist;
			
		String sumPanel = "Distance Summary";
		String ebPanel = "Exact Basis Rules";
		String eePanel = "Exact Expanded Rules";
		String abPanel = "Approx Basis Rules";
		String aePanel = "Approx Expanded Rules";
		String panelList[] = {sumPanel, ebPanel, eePanel, abPanel, aePanel};
		
		public dd2View(AssociationRuleList arl, String[] list2, String[] list3){
			setBackground(Color.lightGray);
			setLayout(new BorderLayout(5, 7));
			
			setupMainPanel(arl, list2, list3);
			add(clist, BorderLayout.NORTH);
			add(mainPanelD3, BorderLayout.CENTER);
		}
		
		final private void setupMainPanel(AssociationRuleList arl, String[] list2, String[] list3){
			clist = new JComboBox(panelList);
			clist.setPreferredSize(new Dimension(200, 20));
			clist.setEditable(false);
			clist.addItemListener(this);
			
			mainPanelD3 = new JPanel();
			mainPanelD3.setBackground(Color.lightGray);
			mainPanelD3.setPreferredSize(new Dimension(1009, 550));
			mainPanelD3.setLayout(new CardLayout());
			mainPanelD3.setBorder(lBorder);
			
			sumCard = new JPanel();
			sumCard.setBackground(Color.lightGray);
			ebCard = new JPanel();
			ebCard.setBackground(Color.lightGray);
			eeCard = new JPanel();
			eeCard.setBackground(Color.lightGray);
			abCard = new JPanel();
			abCard.setBackground(Color.lightGray);
			aeCard = new JPanel();
			aeCard.setBackground(Color.lightGray);
			
			mainPanelD3.add(sumCard, sumPanel);
			mainPanelD3.add(ebCard, ebPanel);
			mainPanelD3.add(eeCard, eePanel);
			mainPanelD3.add(abCard, abPanel);
			mainPanelD3.add(aeCard, aePanel);
			
			dd2sumPane = new JScrollPane();
			dd2sumPane.setBackground(Color.lightGray);
			dd2sumPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			dd2sumPane.setPreferredSize(new Dimension(999, 540));

			dd2SumArea = new JTextArea();
			dd2SumArea.setLineWrap(true);
			dd2SumArea.setWrapStyleWord(true);
			dd2SumArea.setEditable(false);

			dd2sumPane.setViewportView(dd2SumArea);

			sumCard.add(dd2sumPane);
			
			dd2ebPane = new JScrollPane();
			dd2ebPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			dd2ebPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			dd2ebPane.setPreferredSize(new Dimension(999, 540));

			ebtmr = new tmrD3(arl, 1, list3);
			dd2ebt = new JTable(ebtmr);
			dd2ebt.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			dd2ebt.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			setColumnAlign(3, 1);
			
			dd2ebPane.setViewportView(dd2ebt);
			
			ebCard.add(dd2ebPane);
			
			dd2eePane = new JScrollPane();
			dd2eePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			dd2eePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			dd2eePane.setPreferredSize(new Dimension(999, 540));

			eetmr = new tmrD3(arl, 2, list3);
			dd2eet = new JTable(eetmr);
			dd2eet.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			dd2eet.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			setColumnAlign(3, 2);
			
			dd2eePane.setViewportView(dd2eet);
			
			eeCard.add(dd2eePane);
			
			dd2abPane = new JScrollPane();
			dd2abPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			dd2abPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			dd2abPane.setPreferredSize(new Dimension(999, 540));

			abtmr = new tmrD3(arl, 3, list3);
			dd2abt = new JTable(abtmr);
			dd2abt.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			dd2abt.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			setColumnAlign(3, 3);
			
			dd2abPane.setViewportView(dd2abt);
			
			abCard.add(dd2abPane);
			
			dd2aePane = new JScrollPane();
			dd2aePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			dd2aePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			dd2aePane.setPreferredSize(new Dimension(999, 540));

			aetmr = new tmrD3(arl, 4, list3);
			dd2aet = new JTable(aetmr);
			dd2aet.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			dd2aet.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			setColumnAlign(3, 4);
			
			dd2aePane.setViewportView(dd2aet);
			
			aeCard.add(dd2aePane);
			
			formatText(arl);
		}
		
		public void itemStateChanged(ItemEvent evt){
			CardLayout cl = (CardLayout)(mainPanelD3.getLayout());
			cl.show(mainPanelD3, (String)evt.getItem());
		}
		
		final private void setColumnAlign(int cc, int mode){
			DefaultTableCellRenderer mcr = new DefaultTableCellRenderer();
			TableColumnModel tcm;
			if (mode == 1){
				tcm = dd2ebt.getColumnModel();
			}
			else if (mode == 2){
				tcm = dd2eet.getColumnModel();
			}
			else if (mode == 3){
				tcm = dd2abt.getColumnModel();
			}
			else{
				tcm = dd2aet.getColumnModel();
			}
			tcm.getColumn(0).setPreferredWidth(307);
			tcm.getColumn(1).setPreferredWidth(307);
			tcm.getColumn(2).setPreferredWidth(90);
			tcm.getColumn(3).setPreferredWidth(90);
			tcm.getColumn(4).setPreferredWidth(90);
			tcm.getColumn(5).setPreferredWidth(90);
			mcr.setHorizontalAlignment(SwingConstants.CENTER);
			tcm.getColumn(2).setCellRenderer(mcr);
			tcm.getColumn(3).setCellRenderer(mcr);
			tcm.getColumn(4).setCellRenderer(mcr);
			tcm.getColumn(5).setCellRenderer(mcr);
		}
		
		final private void formatText(AssociationRuleList arl){
			ArrayList dis;
			float[] d;
			float minR, maxR, aveR;
			float minA, maxA, aveA;
			float minC, maxC, aveC;
			float minT, maxT, aveT;
			float minOR = Float.MAX_VALUE, maxOR = 0, aveOR = 0;
			float minOA = Float.MAX_VALUE, maxOA = 0, aveOA = 0;
			float minOC = Float.MAX_VALUE, maxOC = 0, aveOC = 0;
			float minOT = Float.MAX_VALUE, maxOT = 0, aveOT = 0;
			int count = 0;
			NumberFormat formatter = NumberFormat.getNumberInstance();
			formatter.setMaximumFractionDigits(3);
			formatter.setMinimumFractionDigits(3);
			ArrayList summaryText = new ArrayList();

			summaryText.add("Association Rule Distance Evaluation Summary\n");
			summaryText.add("Data File: " + dataPath + "\n");
			summaryText.add("==========================================================================================================================\n");
			summaryText.add("Distance Evaluation Summary\n");
			summaryText.add("--------------------------------------------------------------------------------------------------------------------------\n");
			
			minR = Float.MAX_VALUE;
			maxR = 0;
			aveR = 0;
			minA = Float.MAX_VALUE;
			maxA = 0;
			aveA = 0;
			minC = Float.MAX_VALUE;
			maxC = 0;
			aveC = 0;
			minT = Float.MAX_VALUE;
			maxT = 0;
			aveT = 0;
			dis = arl.getExactMinMaxBasisDis();
			count = count + dis.size();
			for (int i = 0; i < dis.size(); i++){
				d = (float[])dis.get(i);
				if (d[0] > maxR){
					maxR = d[0];
				}
				if (d[0] < minR){
					minR = d[0];
				}
				if (d[0] > maxOR){
					maxOR = d[0];
				}
				if (d[0] < minOR){
					minOR = d[0];
				}
				aveR = aveR + d[0];
				aveOR = aveOR + d[0];
				if (d[1] > maxA){
					maxA = d[1];
				}
				if (d[1] < minA){
					minA = d[1];
				}
				if (d[1] > maxOA){
					maxOA = d[1];
				}
				if (d[1] < minOA){
					minOA = d[1];
				}
				aveA = aveA + d[1];
				aveOA = aveOA + d[1];
				if (d[2] > maxC){
					maxC = d[2];
				}
				if (d[2] < minC){
					minC = d[2];
				}
				if (d[2] > maxOC){
					maxOC = d[2];
				}
				if (d[2] < minOC){
					minOC = d[2];
				}
				aveC = aveC + d[2];
				aveOC = aveOC + d[2];
				if (d[3] > maxT){
					maxT = d[3];
				}
				if (d[3] < minT){
					minT = d[3];
				}
				if (d[3] > maxOT){
					maxOT = d[3];
				}
				if (d[3] < minOT){
					minOT = d[3];
				}
				aveT = aveT + d[3];
				aveOT = aveOT + d[3];
			}
			aveR = aveR / (float)dis.size();
			aveA = aveA / (float)dis.size();
			aveC = aveC / (float)dis.size();
			aveT = aveT / (float)dis.size();
			summaryText.add("Exact Basis Evaluation Summary\n");
			summaryText.add("R Distance:     Min: " + formatter.format(minR) + "\tMax: " + formatter.format(maxR) + "\tAve: " + formatter.format(aveR) + "\n");
			summaryText.add("A Distance:     Min: " + formatter.format(minA) + "\tMax: " + formatter.format(maxA) + "\tAve: " + formatter.format(aveA) + "\n");
			summaryText.add("C Distance:     Min: " + formatter.format(minC) + "\tMax: " + formatter.format(maxC) + "\tAve: " + formatter.format(aveC) + "\n");
			summaryText.add("Total Distance: Min: " + formatter.format(minT) + "\tMax: " + formatter.format(maxT) + "\tAve: " + formatter.format(aveT) + "\n");
			summaryText.add("--------------------------------------------------------------------------------------------------------------------------\n");
			
			minR = Float.MAX_VALUE;
			maxR = 0;
			aveR = 0;
			minA = Float.MAX_VALUE;
			maxA = 0;
			aveA = 0;
			minC = Float.MAX_VALUE;
			maxC = 0;
			aveC = 0;
			minT = Float.MAX_VALUE;
			maxT = 0;
			aveT = 0;
			dis = arl.getExactAllDis();
			count = count + dis.size();
			for (int i = 0; i < dis.size(); i++){
				d = (float[])dis.get(i);
				if (d[0] > maxR){
					maxR = d[0];
				}
				if (d[0] < minR){
					minR = d[0];
				}
				if (d[0] > maxOR){
					maxOR = d[0];
				}
				if (d[0] < minOR){
					minOR = d[0];
				}
				aveR = aveR + d[0];
				aveOR = aveOR + d[0];
				if (d[1] > maxA){
					maxA = d[1];
				}
				if (d[1] < minA){
					minA = d[1];
				}
				if (d[1] > maxOA){
					maxOA = d[1];
				}
				if (d[1] < minOA){
					minOA = d[1];
				}
				aveA = aveA + d[1];
				aveOA = aveOA + d[1];
				if (d[2] > maxC){
					maxC = d[2];
				}
				if (d[2] < minC){
					minC = d[2];
				}
				if (d[2] > maxOC){
					maxOC = d[2];
				}
				if (d[2] < minOC){
					minOC = d[2];
				}
				aveC = aveC + d[2];
				aveOC = aveOC + d[2];
				if (d[3] > maxT){
					maxT = d[3];
				}
				if (d[3] < minT){
					minT = d[3];
				}
				if (d[3] > maxOT){
					maxOT = d[3];
				}
				if (d[3] < minOT){
					minOT = d[3];
				}
				aveT = aveT + d[3];
				aveOT = aveOT + d[3];
			}
			aveR = aveR / (float)dis.size();
			aveA = aveA / (float)dis.size();
			aveC = aveC / (float)dis.size();
			aveT = aveT / (float)dis.size();
			summaryText.add("Exact Expanded Evaluation Summary\n");
			summaryText.add("R Distance:     Min: " + formatter.format(minR) + "\tMax: " + formatter.format(maxR) + "\tAve: " + formatter.format(aveR) + "\n");
			summaryText.add("A Distance:     Min: " + formatter.format(minA) + "\tMax: " + formatter.format(maxA) + "\tAve: " + formatter.format(aveA) + "\n");
			summaryText.add("C Distance:     Min: " + formatter.format(minC) + "\tMax: " + formatter.format(maxC) + "\tAve: " + formatter.format(aveC) + "\n");
			summaryText.add("Total Distance: Min: " + formatter.format(minT) + "\tMax: " + formatter.format(maxT) + "\tAve: " + formatter.format(aveT) + "\n");
			summaryText.add("--------------------------------------------------------------------------------------------------------------------------\n");
			
			minR = Float.MAX_VALUE;
			maxR = 0;
			aveR = 0;
			minA = Float.MAX_VALUE;
			maxA = 0;
			aveA = 0;
			minC = Float.MAX_VALUE;
			maxC = 0;
			aveC = 0;
			minT = Float.MAX_VALUE;
			maxT = 0;
			aveT = 0;
			dis = arl.getApproxMinMaxBasisDis();
			count = count + dis.size();
			for (int i = 0; i < dis.size(); i++){
				d = (float[])dis.get(i);
				if (d[0] > maxR){
					maxR = d[0];
				}
				if (d[0] < minR){
					minR = d[0];
				}
				if (d[0] > maxOR){
					maxOR = d[0];
				}
				if (d[0] < minOR){
					minOR = d[0];
				}
				aveR = aveR + d[0];
				aveOR = aveOR + d[0];
				if (d[1] > maxA){
					maxA = d[1];
				}
				if (d[1] < minA){
					minA = d[1];
				}
				if (d[1] > maxOA){
					maxOA = d[1];
				}
				if (d[1] < minOA){
					minOA = d[1];
				}
				aveA = aveA + d[1];
				aveOA = aveOA + d[1];
				if (d[2] > maxC){
					maxC = d[2];
				}
				if (d[2] < minC){
					minC = d[2];
				}
				if (d[2] > maxOC){
					maxOC = d[2];
				}
				if (d[2] < minOC){
					minOC = d[2];
				}
				aveC = aveC + d[2];
				aveOC = aveOC + d[2];
				if (d[3] > maxT){
					maxT = d[3];
				}
				if (d[3] < minT){
					minT = d[3];
				}
				if (d[3] > maxOT){
					maxOT = d[3];
				}
				if (d[3] < minOT){
					minOT = d[3];
				}
				aveT = aveT + d[3];
				aveOT = aveOT + d[3];
			}
			aveR = aveR / (float)dis.size();
			aveA = aveA / (float)dis.size();
			aveC = aveC / (float)dis.size();
			aveT = aveT / (float)dis.size();
			summaryText.add("Approximate Basis Evaluation Summary\n");
			summaryText.add("R Distance:     Min: " + formatter.format(minR) + "\tMax: " + formatter.format(maxR) + "\tAve: " + formatter.format(aveR) + "\n");
			summaryText.add("A Distance:     Min: " + formatter.format(minA) + "\tMax: " + formatter.format(maxA) + "\tAve: " + formatter.format(aveA) + "\n");
			summaryText.add("C Distance:     Min: " + formatter.format(minC) + "\tMax: " + formatter.format(maxC) + "\tAve: " + formatter.format(aveC) + "\n");
			summaryText.add("Total Distance: Min: " + formatter.format(minT) + "\tMax: " + formatter.format(maxT) + "\tAve: " + formatter.format(aveT) + "\n");
			summaryText.add("--------------------------------------------------------------------------------------------------------------------------\n");
			
			minR = Float.MAX_VALUE;
			maxR = 0;
			aveR = 0;
			minA = Float.MAX_VALUE;
			maxA = 0;
			aveA = 0;
			minC = Float.MAX_VALUE;
			maxC = 0;
			aveC = 0;
			minT = Float.MAX_VALUE;
			maxT = 0;
			aveT = 0;
			dis = arl.getApproxAllDis();
			count = count + dis.size();
			for (int i = 0; i < dis.size(); i++){
				d = (float[])dis.get(i);
				if (d[0] > maxR){
					maxR = d[0];
				}
				if (d[0] < minR){
					minR = d[0];
				}
				if (d[0] > maxOR){
					maxOR = d[0];
				}
				if (d[0] < minOR){
					minOR = d[0];
				}
				aveR = aveR + d[0];
				aveOR = aveOR + d[0];
				if (d[1] > maxA){
					maxA = d[1];
				}
				if (d[1] < minA){
					minA = d[1];
				}
				if (d[1] > maxOA){
					maxOA = d[1];
				}
				if (d[1] < minOA){
					minOA = d[1];
				}
				aveA = aveA + d[1];
				aveOA = aveOA + d[1];
				if (d[2] > maxC){
					maxC = d[2];
				}
				if (d[2] < minC){
					minC = d[2];
				}
				if (d[2] > maxOC){
					maxOC = d[2];
				}
				if (d[2] < minOC){
					minOC = d[2];
				}
				aveC = aveC + d[2];
				aveOC = aveOC + d[2];
				if (d[3] > maxT){
					maxT = d[3];
				}
				if (d[3] < minT){
					minT = d[3];
				}
				if (d[3] > maxOT){
					maxOT = d[3];
				}
				if (d[3] < minOT){
					minOT = d[3];
				}
				aveT = aveT + d[3];
				aveOT = aveOT + d[3];
			}
			aveR = aveR / (float)dis.size();
			aveA = aveA / (float)dis.size();
			aveC = aveC / (float)dis.size();
			aveT = aveT / (float)dis.size();
			summaryText.add("Approximate Expanded Evaluation Summary\n");
			summaryText.add("R Distance:     Min: " + formatter.format(minR) + "\tMax: " + formatter.format(maxR) + "\tAve: " + formatter.format(aveR) + "\n");
			summaryText.add("A Distance:     Min: " + formatter.format(minA) + "\tMax: " + formatter.format(maxA) + "\tAve: " + formatter.format(aveA) + "\n");
			summaryText.add("C Distance:     Min: " + formatter.format(minC) + "\tMax: " + formatter.format(maxC) + "\tAve: " + formatter.format(aveC) + "\n");
			summaryText.add("Total Distance: Min: " + formatter.format(minT) + "\tMax: " + formatter.format(maxT) + "\tAve: " + formatter.format(aveT) + "\n");
			summaryText.add("--------------------------------------------------------------------------------------------------------------------------\n");

			summaryText.add("Overall Evaluation Summary\n");
			summaryText.add("R Distance:     Min: " + formatter.format(minOR) + "\tMax: " + formatter.format(maxOR) + "\tAve: " + formatter.format(aveOR / (float)count) + "\n");
			summaryText.add("A Distance:     Min: " + formatter.format(minOA) + "\tMax: " + formatter.format(maxOA) + "\tAve: " + formatter.format(aveOA / (float)count) + "\n");
			summaryText.add("C Distance:     Min: " + formatter.format(minOC) + "\tMax: " + formatter.format(maxOC) + "\tAve: " + formatter.format(aveOC / (float)count) + "\n");
			summaryText.add("Total Distance: Min: " + formatter.format(minOT) + "\tMax: " + formatter.format(maxOT) + "\tAve: " + formatter.format(aveOT / (float)count) + "\n");

			for (int i = 0; i < summaryText.size(); i++){
				dd2SumArea.append((String)summaryText.get(i));
			}
		}
	}
	
	private class covView extends JPanel implements ItemListener{
		
		JPanel sumCard, ebCard, eeCard, abCard, aeCard;
		
		tmrC ebtmr, eetmr, abtmr, aetmr;
			
		JComboBox clist;
			
		String sumPanel = "Coverage Summary";
		String ebPanel = "Exact Basis Rules";
		String eePanel = "Exact Expanded Rules";
		String abPanel = "Approx Basis Rules";
		String aePanel = "Approx Expanded Rules";
		String panelList[] = {sumPanel, ebPanel, eePanel, abPanel, aePanel};
		
		public covView(AssociationRuleList arl, String[] list2){
			setBackground(Color.lightGray);
			setLayout(new BorderLayout(5, 7));
			
			setupMainPanel(arl, list2);
			add(clist, BorderLayout.NORTH);
			add(mainPanelC, BorderLayout.CENTER);
		}
		
		final private void setupMainPanel(AssociationRuleList arl, String[] list2){

			clist = new JComboBox(panelList);
			clist.setPreferredSize(new Dimension(200, 20));
			clist.setEditable(false);
			clist.addItemListener(this);
			
			mainPanelC = new JPanel();
			mainPanelC.setBackground(Color.lightGray);
			mainPanelC.setPreferredSize(new Dimension(1009, 550));
			mainPanelC.setLayout(new CardLayout());
			mainPanelC.setBorder(lBorder);
			
			sumCard = new JPanel();
			sumCard.setBackground(Color.lightGray);
			ebCard = new JPanel();
			ebCard.setBackground(Color.lightGray);
			eeCard = new JPanel();
			eeCard.setBackground(Color.lightGray);
			abCard = new JPanel();
			abCard.setBackground(Color.lightGray);
			aeCard = new JPanel();
			aeCard.setBackground(Color.lightGray);
			
			mainPanelC.add(sumCard, sumPanel);
			mainPanelC.add(ebCard, ebPanel);
			mainPanelC.add(eeCard, eePanel);
			mainPanelC.add(abCard, abPanel);
			mainPanelC.add(aeCard, aePanel);
			
			covsumPane = new JScrollPane();
			covsumPane.setBackground(Color.lightGray);
			covsumPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			covsumPane.setPreferredSize(new Dimension(999, 540));

			covSumArea = new JTextArea();
			covSumArea.setLineWrap(true);
			covSumArea.setWrapStyleWord(true);
			covSumArea.setEditable(false);

			covsumPane.setViewportView(covSumArea);

			sumCard.add(covsumPane);
			
			covebPane = new JScrollPane();
			covebPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			covebPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			covebPane.setPreferredSize(new Dimension(999, 540));

			ebtmr = new tmrC(arl, 1, list2);
			covebt = new JTable(ebtmr);
			covebt.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			covebt.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			setColumnAlign(3, 1);
			
			covebPane.setViewportView(covebt);
			
			ebCard.add(covebPane);
			
			coveePane = new JScrollPane();
			coveePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			coveePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			coveePane.setPreferredSize(new Dimension(999, 540));

			eetmr = new tmrC(arl, 2, list2);
			coveet = new JTable(eetmr);
			coveet.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			coveet.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			setColumnAlign(3, 2);
			
			coveePane.setViewportView(coveet);
			
			eeCard.add(coveePane);
			
			covabPane = new JScrollPane();
			covabPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			covabPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			covabPane.setPreferredSize(new Dimension(999, 540));

			abtmr = new tmrC(arl, 3, list2);
			covabt = new JTable(abtmr);
			covabt.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			covabt.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			setColumnAlign(3, 3);
			
			covabPane.setViewportView(covabt);
			
			abCard.add(covabPane);
			
			covaePane = new JScrollPane();
			covaePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			covaePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			covaePane.setPreferredSize(new Dimension(999, 540));

			aetmr = new tmrC(arl, 4, list2);
			covaet = new JTable(aetmr);
			covaet.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			covaet.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			setColumnAlign(3, 4);
			
			covaePane.setViewportView(covaet);
			
			aeCard.add(covaePane);
			
			formatText(arl);
		}
		
		public void itemStateChanged(ItemEvent evt){
			CardLayout cl = (CardLayout)(mainPanelC.getLayout());
			cl.show(mainPanelC, (String)evt.getItem());
		}
		
		final private void setColumnAlign(int cc, int mode){
			DefaultTableCellRenderer mcr = new DefaultTableCellRenderer();
			TableColumnModel tcm;
			if (mode == 1){
				tcm = covebt.getColumnModel();
			}
			else if (mode == 2){
				tcm = coveet.getColumnModel();
			}
			else if (mode == 3){
				tcm = covabt.getColumnModel();
			}
			else{
				tcm = covaet.getColumnModel();
			}
			tcm.getColumn(0).setPreferredWidth(339);
			tcm.getColumn(1).setPreferredWidth(339);
			tcm.getColumn(2).setPreferredWidth(99);
			tcm.getColumn(3).setPreferredWidth(99);
			tcm.getColumn(4).setPreferredWidth(99);
			mcr.setHorizontalAlignment(SwingConstants.CENTER);
			tcm.getColumn(2).setCellRenderer(mcr);
			tcm.getColumn(3).setCellRenderer(mcr);
			tcm.getColumn(4).setCellRenderer(mcr);
		}
		
		final private void formatText(AssociationRuleList arl){
			float[] values, values1;
			float vmax, vmin;
			NumberFormat formatter = NumberFormat.getNumberInstance();
			formatter.setMaximumFractionDigits(3);
			ArrayList summaryText = new ArrayList();

			summaryText.add("Association Rule Coverage Evaluation Summary\n");
			summaryText.add("Data File: " + dataPath + "\n");
			summaryText.add("==========================================================================================================================\n");
			summaryText.add("Coverage Evaluation Summary\n");
			summaryText.add("--------------------------------------------------------------------------------------------------------------------------\n");

			values = summation(arl.getExactMinMaxBasisCov(), 0);
			summaryText.add("Coverage of exact basis rule set (antecedent):          Total: " + formatter.format((Float)arl.getExactMinMaxBasisCovA()) + "%\tMin: " + formatter.format(values[2]) + "%\tMax: " + formatter.format(values[1]) + "%\tAve: " + formatter.format(values[0] / arl.getExactMinMaxBasisSize()) + "%\n");
			values = summation(arl.getExactMinMaxBasisCov(), 1);
			summaryText.add("Coverage of exact basis rule set (consequent):          Total: " + formatter.format((Float)arl.getExactMinMaxBasisCovC()) + "%\tMin: " + formatter.format(values[2]) + "%\tMax: " + formatter.format(values[1]) + "%\tAve: " + formatter.format(values[0] / arl.getExactMinMaxBasisSize()) + "%\n");
			values = summation(arl.getExactMinMaxBasisCov(), 2);
			summaryText.add("Coverage of exact basis rule set (rule):                Total: " + formatter.format((Float)arl.getExactMinMaxBasisCovR()) + "%\tMin: " + formatter.format(values[2]) + "%\tMax: " + formatter.format(values[1]) + "%\tAve: " + formatter.format(values[0] / arl.getExactMinMaxBasisSize()) + "%\n\n");
			vmax = values[1];
			vmin = values[2];

			values1 = summation(arl.getExactAllCov(), 0);
			summaryText.add("Coverage of all/expanded exact rule set (antecedent):   Total: " + formatter.format((Float)arl.getExactAllCovA()) + "%\tMin: " + formatter.format(values1[2]) + "%\tMax: " + formatter.format(values1[1]) + "%\tAve: " + formatter.format(values1[0] / arl.getExactAllSize()) + "%\n");
			values1 = summation(arl.getExactAllCov(), 1);
			summaryText.add("Coverage of all/expanded exact rule set (consequent):   Total: " + formatter.format((Float)arl.getExactAllCovC()) + "%\tMin: " + formatter.format(values1[2]) + "%\tMax: " + formatter.format(values1[1]) + "%\tAve: " + formatter.format(values1[0] / arl.getExactAllSize()) + "%\n");
			values1 = summation(arl.getExactAllCov(), 2);
			summaryText.add("Coverage of all/expanded exact rule set (rule):         Total: " + formatter.format((Float)arl.getExactAllCovR()) + "%\tMin: " + formatter.format(values1[2]) + "%\tMax: " + formatter.format(values1[1]) + "%\tAve: " + formatter.format(values1[0] / arl.getExactAllSize()) + "%\n\n");
			if (values1[1] > vmax){
				vmax = values1[1];
			}
			if (values1[2] < vmin && values1[2] != 0){
				vmin = values1[2];
			}

			summaryText.add("Total coverage of exact rule set(s):                    Total: " + formatter.format((Float)arl.getExactRuleCoverage()) + "%\tMin: " + formatter.format(vmin) + "%\tMax: " + formatter.format(vmax) + "%\tAve: " + formatter.format((values[0] + values1[0]) / (arl.getExactMinMaxBasisSize() + arl.getExactAllSize())) + "%\n");
			summaryText.add("--------------------------------------------------------------------------------------------------------------------------\n");

			values = summation(arl.getApproxMinMaxBasisCov(), 0);
			summaryText.add("Coverage of approx basis rule set (antecedent):         Total: " + formatter.format((Float)arl.getApproxMinMaxBasisCovA()) + "%\tMin: " + formatter.format(values[2]) + "%\tMax: " + formatter.format(values[1]) + "%\tAve: " + formatter.format(values[0] / arl.getApproxMinMaxBasisSize()) + "%\n");
			values = summation(arl.getApproxMinMaxBasisCov(), 1);
			summaryText.add("Coverage of approx basis rule set (consequent):         Total: " + formatter.format((Float)arl.getApproxMinMaxBasisCovC()) + "%\tMin: " + formatter.format(values[2]) + "%\tMax: " + formatter.format(values[1]) + "%\tAve: " + formatter.format(values[0] / arl.getApproxMinMaxBasisSize()) + "%\n");
			values = summation(arl.getApproxMinMaxBasisCov(), 2);
			summaryText.add("Coverage of approx basis rule set (rule):               Total: " + formatter.format((Float)arl.getApproxMinMaxBasisCovR()) + "%\tMin: " + formatter.format(values[2]) + "%\tMax: " + formatter.format(values[1]) + "%\tAve: " + formatter.format(values[0] / arl.getApproxMinMaxBasisSize()) + "%\n\n");
			vmax = values[1];
			vmin = values[2];

			values1 = summation(arl.getApproxAllCov(), 0);
			summaryText.add("Coverage of all/expanded approx rule set (antecedent):  Total: " + formatter.format((Float)arl.getApproxAllCovA()) + "%\tMin: " + formatter.format(values1[2]) + "%\tMax: " + formatter.format(values1[1]) + "%\tAve: " + formatter.format(values1[0] / arl.getApproxAllSize()) + "%\n");
			values1 = summation(arl.getApproxAllCov(), 1);
			summaryText.add("Coverage of all/expanded approx rule set (consequent):  Total: " + formatter.format((Float)arl.getApproxAllCovC()) + "%\tMin: " + formatter.format(values1[2]) + "%\tMax: " + formatter.format(values1[1]) + "%\tAve: " + formatter.format(values1[0] / arl.getApproxAllSize()) + "%\n");
			values1 = summation(arl.getApproxAllCov(), 2);
			summaryText.add("Coverage of all/expanded approx rule set (rule):        Total: " + formatter.format((Float)arl.getApproxAllCovR()) + "%\tMin: " + formatter.format(values1[2]) + "%\tMax: " + formatter.format(values1[1]) + "%\tAve: " + formatter.format(values1[0] / arl.getApproxAllSize()) + "%\n\n");
			if (values1[1] > vmax){
				vmax = values1[1];
			}
			if (values1[2] < vmin && values1[2] != 0){
				vmin = values1[2];
			}


			summaryText.add("Total coverage of approx rule set(s):                   Total: " + formatter.format((Float)arl.getApproxRuleCoverage()) + "%\tMin: " + formatter.format(vmin) + "%\tMax: " + formatter.format(vmax) + "%\tAve: " + formatter.format((values[0] + values1[0]) / (arl.getApproxMinMaxBasisSize() + arl.getApproxAllSize())) + "%\n");
			summaryText.add("--------------------------------------------------------------------------------------------------------------------------\n");
			summaryText.add("Average scores are based on the summation of the approriate coverage of each rule divided by the number of rules in that category. Not the total coverage divided by the number of rules.");

			for (int i = 0; i < summaryText.size(); i++){
				covSumArea.append((String)summaryText.get(i));
			}
		}
		
		final private float[] summation(ArrayList cov, int index){
			float[] values = new float[3];
			float sum = 0, min = Float.MAX_VALUE, max = 0;
			int size = cov.size();
			for (int i = 0; i < size; i++){
				sum = sum + ((float[])cov.get(i))[index];
				if (((float[])cov.get(i))[index] > max){
					max = ((float[])cov.get(i))[index];
				}
				if (((float[])cov.get(i))[index] < min){
					min = ((float[])cov.get(i))[index];
				}
			}
			values[0] = sum;
			values[1] = max;
			if (min != Float.MAX_VALUE){
				values[2] = min;
			}
			else{
				values[2] = 0;
			}
			return values;
		}
	}
	
	private class tmrC extends AbstractTableModel{

		private Vector rowData;

		final String[] columnNames = {"Antecedent", "Conseqent", "A. Coverage", "C. Coverage", "R. Coverage"};

		/**
		 * tmr method.
		 * Constructor.
		 * Method that initialise this class and sets up the table
		 * structure and content for viewing.
		 */
		public tmrC(AssociationRuleList arl, int mode, String[] list){
			int[] a, c;
			String items;
			ArrayList rules, cov;
			rowData = new Vector();
			if (mode == 1){
				rules = arl.getExactMinMaxBasis();
				cov = arl.getExactMinMaxBasisCov();
			}
			else if (mode == 2){
				rules = arl.getExactAll();
				cov = arl.getExactAllCov();
			}
			else if (mode == 3){
				rules = arl.getApproxMinMaxBasis();
				cov = arl.getApproxMinMaxBasisCov();
			}
			else{
				rules = arl.getApproxAll();
				cov = arl.getApproxAllCov();
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
				
				if (String.valueOf(((float[])cov.get(i))[0]).length() > 5){
					rows.add(String.valueOf(((float[])cov.get(i))[0]).substring(0, 6) + "%");
				}
				else{
					rows.add(String.valueOf(((float[])cov.get(i))[0]) + "%");
				}
				if (String.valueOf(((float[])cov.get(i))[1]).length() > 5){
					rows.add(String.valueOf(((float[])cov.get(i))[1]).substring(0, 6) + "%");
				}
				else{
					rows.add(String.valueOf(((float[])cov.get(i))[1]) + "%");
				}
				if (String.valueOf(((float[])cov.get(i))[2]).length() > 5){
					rows.add(String.valueOf(((float[])cov.get(i))[2]).substring(0, 6) + "%");
				}
				else{
					rows.add(String.valueOf(((float[])cov.get(i))[2]) + "%");
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
	
	private class tmrD extends AbstractTableModel{

		private Vector rowData;

		final String[] columnNames = {"Node / Item", "Overall Freq.", "A. Freq.", "C. Freq.", "% Rate", "A. % Rate", "C. % Rate"};

		/**
		 * tmr method.
		 * Constructor.
		 * Method that initialise this class and sets up the table
		 * structure and content for viewing.
		 */
		public tmrD(AssociationRuleList arl, int mode, String[] list){
			Object[] disCounts = arl.getNodeFreqs();
			int[] results;
			rowData = new Vector();
			if (mode == 1){
				for (int i = 0; i < list.length; i++){
					results = determineFreq(disCounts, list[i], mode);
					Vector rows = new Vector();
					rows.add("" + list[i]);
					rows.add(results[0]);
					rows.add(results[1]);
					rows.add(results[2]);
					rows.add(((float)results[0] / ((float)arl.getExactMinMaxBasisSize() + (float)arl.getExactAllSize() + (float)arl.getApproxMinMaxBasisSize() + (float)arl.getApproxAllSize())) * (float)100 + "%");
					rows.add(((float)results[1] / ((float)arl.getExactMinMaxBasisSize() + (float)arl.getExactAllSize() + (float)arl.getApproxMinMaxBasisSize() + (float)arl.getApproxAllSize())) * (float)100 + "%");
					rows.add(((float)results[2] / ((float)arl.getExactMinMaxBasisSize() + (float)arl.getExactAllSize() + (float)arl.getApproxMinMaxBasisSize() + (float)arl.getApproxAllSize())) * (float)100 + "%");
					rowData.add(rows);
				}
			}
			else if (mode == 2){
				for (int i = 0; i < list.length; i++){
					results = determineFreq(disCounts, list[i], mode);
					Vector rows = new Vector();
					rows.add("" + list[i]);
					rows.add(results[0]);
					rows.add(results[1]);
					rows.add(results[2]);
					rows.add(((float)results[0] / (float)arl.getExactMinMaxBasisSize()) * (float)100 + "%");
					rows.add(((float)results[1] / (float)arl.getExactMinMaxBasisSize()) * (float)100 + "%");
					rows.add(((float)results[2] / (float)arl.getExactMinMaxBasisSize()) * (float)100 + "%");
					rowData.add(rows);
				}
			}
			else if (mode == 3){
				for (int i = 0; i < list.length; i++){
					results = determineFreq(disCounts, list[i], mode);
					Vector rows = new Vector();
					rows.add("" + list[i]);
					rows.add(results[0]);
					rows.add(results[1]);
					rows.add(results[2]);
					rows.add(((float)results[0] / (float)arl.getExactAllSize()) * (float)100 + "%");
					rows.add(((float)results[1] / (float)arl.getExactAllSize()) * (float)100 + "%");
					rows.add(((float)results[2] / (float)arl.getExactAllSize()) * (float)100 + "%");
					rowData.add(rows);
				}
			}
			else if (mode == 4){
				for (int i = 0; i < list.length; i++){
					results = determineFreq(disCounts, list[i], mode);
					Vector rows = new Vector();
					rows.add("" + list[i]);
					rows.add(results[0]);
					rows.add(results[1]);
					rows.add(results[2]);
					rows.add(((float)results[0] / (float)arl.getApproxMinMaxBasisSize()) * (float)100 + "%");
					rows.add(((float)results[1] / (float)arl.getApproxMinMaxBasisSize()) * (float)100 + "%");
					rows.add(((float)results[2] / (float)arl.getApproxMinMaxBasisSize()) * (float)100 + "%");
					rowData.add(rows);
				}
			}
			else{
				for (int i = 0; i < list.length; i++){
					results = determineFreq(disCounts, list[i], mode);
					Vector rows = new Vector();
					rows.add("" + list[i]);
					rows.add(results[0]);
					rows.add(results[1]);
					rows.add(results[2]);
					rows.add(((float)results[0] / (float)arl.getApproxAllSize()) * (float)100 + "%");
					rows.add(((float)results[1] / (float)arl.getApproxAllSize()) * (float)100 + "%");
					rows.add(((float)results[2] / (float)arl.getApproxAllSize()) * (float)100 + "%");
					rowData.add(rows);
				}
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
	
	private class tmrD2 extends AbstractTableModel{

		private Vector rowData;

		final String[] columnNames = {"Antecedent", "Consequent", "LD", "HRD", "Diversity"};

		/**
		 * tmr method.
		 * Constructor.
		 * Method that initialise this class and sets up the table
		 * structure and content for viewing.
		 */
		public tmrD2(AssociationRuleList arl, int mode, String[] list){
			ArrayList rules, div;
			int[] a, c;
			float[] d;
			String items;
			rowData = new Vector();
			if (mode == 1){
				rules = arl.getExactMinMaxBasis();
				div = arl.getExactMinMaxBasisDiv();
			}
			else if (mode == 2){
				rules = arl.getExactAll();
				div = arl.getExactAllDiv();
			}
			else if (mode == 3){
				rules = arl.getApproxMinMaxBasis();
				div = arl.getApproxMinMaxBasisDiv();
			}
			else{
				rules = arl.getApproxAll();
				div = arl.getApproxAllDiv();
			}
			for (int i = 0; i < rules.size(); i++){
				Object[] rule = (Object[])rules.get(i);
				a = (int[])rule[0];
				c = (int[])rule[1];
				d = (float[])div.get(i);
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
				rows.add("" + d[0]);
				rows.add("" + d[1]);
				rows.add("" + d[2]);
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
	
	private class tmrD3 extends AbstractTableModel{

		private Vector rowData;

		final String[] columnNames = {"Antecedent", "Consequent", "R Distance", "A Distance", "C Distance", "T Distance"};

		/**
		 * tmr method.
		 * Constructor.
		 * Method that initialise this class and sets up the table
		 * structure and content for viewing.
		 */
		public tmrD3(AssociationRuleList arl, int mode, String[] list){
			ArrayList rules, dis;
			int[] a, c;
			float[] d;
			String items;
			rowData = new Vector();
			if (mode == 1){
				rules = arl.getExactMinMaxBasis();
				dis = arl.getExactMinMaxBasisDis();
			}
			else if (mode == 2){
				rules = arl.getExactAll();
				dis = arl.getExactAllDis();
			}
			else if (mode == 3){
				rules = arl.getApproxMinMaxBasis();
				dis = arl.getApproxMinMaxBasisDis();
			}
			else{
				rules = arl.getApproxAll();
				dis = arl.getApproxAllDis();
			}
			for (int i = 0; i < rules.size(); i++){
				Object[] rule = (Object[])rules.get(i);
				a = (int[])rule[0];
				c = (int[])rule[1];
				d = (float[])dis.get(i);
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
				rows.add("" + d[0]);
				rows.add("" + d[1]);
				rows.add("" + d[2]);
				rows.add("" + d[3]);
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
	
	private int[] determineFreq(Object[] disCounts, String node, int mode){
		int[] results = new int[3];
		int[][] freqs;
		String[] names = (String[])attNames[0];
		int index = -1;
		for (int i = 0; i < names.length; i++){
			if (names[i].equals(node)){
				//Match...
				index = i;
				i = names.length;
			}
		}
		if (index != -1){
			freqs = (int[][])disCounts[index];
			if (mode == 1){
				//All rules...
				results[0] = freqs[0][0] + freqs[0][1] + freqs[0][2] + freqs[0][3];
				results[1] = freqs[1][0] + freqs[1][1] + freqs[1][2] + freqs[1][3];
				results[2] = freqs[2][0] + freqs[2][1] + freqs[2][2] + freqs[2][3];
			}
			else if (mode == 2){
				//EB rules...
				results[0] = freqs[0][0];
				results[1] = freqs[1][0];
				results[2] = freqs[2][0];
			}
			else if (mode == 3){
				//EE rules...
				results[0] = freqs[0][1];
				results[1] = freqs[1][1];
				results[2] = freqs[2][1];
			}
			else if (mode == 4){
				//AB rules...
				results[0] = freqs[0][2];
				results[1] = freqs[1][2];
				results[2] = freqs[2][2];
			}
			else{
				//AE rules...
				results[0] = freqs[0][3];
				results[1] = freqs[1][3];
				results[2] = freqs[2][3];
			}
		}
		else{
			results[0] = 0;
			results[1] = 0;
			results[2] = 0;
		}
		return results;
	}
	
	private int[] determineUnique(Object[] disCounts, int mode){
		int[] results = new int[3];
		int[][] freqs;
		if (mode ==1 ){
			//All rules...
			for (int i = 0; i < disCounts.length; i++){
				freqs = (int[][])disCounts[i];
				if (freqs[0][0] != 0 || freqs[0][1] != 0 || freqs[0][2] != 0 || freqs[0][3] != 0){
					results[0] = results[0] + 1;
				}
				if (freqs[1][0] != 0 || freqs[1][1] != 0 || freqs[1][2] != 0 || freqs[1][3] != 0){
					results[1] = results[1] + 1;
				}
				if (freqs[2][0] != 0 || freqs[2][1] != 0 || freqs[2][2] != 0 || freqs[2][3] != 0){
					results[2] = results[2] + 1;
				}
			}
		}
		else if (mode == 2){
			//EB...
			for (int i = 0; i < disCounts.length; i++){
				freqs = (int[][])disCounts[i];
				if (freqs[0][0] != 0){
					results[0] = results[0] + 1;
				}
				if (freqs[1][0] != 0){
					results[1] = results[1] + 1;
				}
				if (freqs[2][0] != 0){
					results[2] = results[2] + 1;
				}
			}
		}
		else if (mode == 3){
			//EE...
			for (int i = 0; i < disCounts.length; i++){
				freqs = (int[][])disCounts[i];
				if (freqs[0][1] != 0){
					results[0] = results[0] + 1;
				}
				if (freqs[1][1] != 0){
					results[1] = results[1] + 1;
				}
				if (freqs[2][1] != 0){
					results[2] = results[2] + 1;
				}
			}
		}
		else if (mode == 4){
			//AB...
			for (int i = 0; i < disCounts.length; i++){
				freqs = (int[][])disCounts[i];
				if (freqs[0][2] != 0){
					results[0] = results[0] + 1;
				}
				if (freqs[1][2] != 0){
					results[1] = results[1] + 1;
				}
				if (freqs[2][2] != 0){
					results[2] = results[2] + 1;
				}
			}
		}
		else{
			//AE...
			for (int i = 0; i < disCounts.length; i++){
				freqs = (int[][])disCounts[i];
				if (freqs[0][3] != 0){
					results[0] = results[0] + 1;
				}
				if (freqs[1][3] != 0){
					results[1] = results[1] + 1;
				}
				if (freqs[2][3] != 0){
					results[2] = results[2] + 1;
				}
			}
		}
		return results;
	}
	
	private Object[] determineMinMaxAve(Object[] disCounts, int mode){
		Object[] results = new Object[3];
		int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE, count = 0;
		float ave = (float)0.0;
		int[][] freqs;
		if (mode == 1){
			//All rules...
			int sum;
			for (int i = 0; i < disCounts.length; i++){
				sum = 0;
				freqs = (int[][])disCounts[i];
				sum = freqs[0][0] + freqs[0][1] + freqs[0][2] + freqs[0][3];
				if (sum != 0){
					if (sum < min){
						min = sum;
					}
					if (sum > max){
						max = sum;
					}
					ave = ave + (float)sum;
					count++;
				}
			}
		}
		else if (mode == 2){
			//EB...
			for (int i = 0; i < disCounts.length; i++){
				freqs = (int[][])disCounts[i];
				if (freqs[0][0] != 0){
					if (freqs[0][0] < min){
						min = freqs[0][0];
					}
					if (freqs[0][0] > max){
						max = freqs[0][0];
					}
					ave = ave + (float)freqs[0][0];
					count++;
				}
			}
		}
		else if (mode == 3){
			//EE...
			for (int i = 0; i < disCounts.length; i++){
				freqs = (int[][])disCounts[i];
				if (freqs[0][1] != 0){
					if (freqs[0][1] < min){
						min = freqs[0][1];
					}
					if (freqs[0][1] > max){
						max = freqs[0][1];
					}
					ave = ave + (float)freqs[0][1];
					count++;
				}
			}
		}
		else if (mode == 4){
			//AB...
			for (int i = 0; i < disCounts.length; i++){
				freqs = (int[][])disCounts[i];
				if (freqs[0][2] != 0){
					if (freqs[0][2] < min){
						min = freqs[0][2];
					}
					if (freqs[0][2] > max){
						max = freqs[0][2];
					}
					ave = ave + (float)freqs[0][2];
					count++;
				}
			}
		}
		else{
			//AE...
			for (int i = 0; i < disCounts.length; i++){
				freqs = (int[][])disCounts[i];
				if (freqs[0][3] != 0){
					if (freqs[0][3] < min){
						min = freqs[0][3];
					}
					if (freqs[0][3] > max){
						max = freqs[0][3];
					}
					ave = ave + (float)freqs[0][3];
					count++;
				}
			}
		}
		if (min == Integer.MAX_VALUE){
			min = 0;
		}
		if (max == Integer.MIN_VALUE){
			max = 0;
		}
		results[0] = min;
		results[1] = max;
		if (ave != 0){
			results[2] = ave / (float)count;
		}
		else{
			results[2] = (float)0;
		}
		return results;
	}
}