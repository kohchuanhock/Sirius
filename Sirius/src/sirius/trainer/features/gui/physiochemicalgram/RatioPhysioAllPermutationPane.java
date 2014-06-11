/*==========================================================================
	  SiriusPSB - A Generic System for Analysis of Biological Sequences
	        http://compbio.ddns.comp.nus.edu.sg/~sirius/index.php
============================================================================
	  Copyright (C) 2007 by Chuan Hock Koh
	
	  This program is free software; you can redistribute it and/or
	  modify it under the terms of the GNU General Public
	  License as published by the Free Software Foundation; either
	  version 3 of the License, or (at your option) any later version.
	
	  This program is distributed in the hope that it will be useful,
	  but WITHOUT ANY WARRANTY; without even the implied warranty of
	  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
	  General Public License for more details.
	  	
	  You should have received a copy of the GNU General Public License
	  along with this program.  If not, see <http://www.gnu.org/licenses/>.
==========================================================================*/
package sirius.trainer.features.gui.physiochemicalgram;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import sirius.main.ApplicationData;
import sirius.nnsearcher.main.MustHaveTableModel;
import sirius.trainer.features.RatioOfKGramFeature;
import sirius.trainer.main.StatusPane;
import sirius.trainer.step2.FeatureTableModel;

public class RatioPhysioAllPermutationPane extends JComponent implements ActionListener{
	static final long serialVersionUID = sirius.Sirius.version;
	private JDialog parent;
	private FeatureTableModel model;
	private StatusPane statusPane;
	private ApplicationData applicationData;
	//private MustHaveTableModel constraintsModel;
	
	private JButton okButton = new JButton("OK");
	private JButton cancelButton = new JButton("Cancel");
	private JTextField windowSizeFromField = new JTextField(5);		
	private JTextField windowSizeToField = new JTextField(5);	
	private JCheckBox isPercentageCheckBox = new JCheckBox("%");
	private JTextField xField = new JTextField(15);
	private JTextField m1Field = new JTextField(3);
	private JTextField yField = new JTextField(15);
	private JTextField m2Field = new JTextField(3);
	
	private JCheckBox checkBoxH = new JCheckBox("H",true);
	private JCheckBox checkBoxL = new JCheckBox("L",true);
	private JCheckBox checkBoxP = new JCheckBox("P",true);
	private JCheckBox checkBoxN = new JCheckBox("N",true);
	private JCheckBox checkBoxA = new JCheckBox("A",true);
	private JCheckBox checkBoxK = new JCheckBox("K",true);
	private JCheckBox checkBoxO = new JCheckBox("O",true);
	private JCheckBox checkBoxD = new JCheckBox("D",true);
	private JCheckBox checkBoxX = new JCheckBox("X");
	
	
public RatioPhysioAllPermutationPane(JDialog parent,FeatureTableModel model,ApplicationData applicationData, 
		MustHaveTableModel constraintsModel){		
	this.parent = parent;
	this.model = model;
	this.applicationData = applicationData;
	//this.constraintsModel = constraintsModel;
	this.statusPane = applicationData.getStatusPane();
	
	setLayout(new BorderLayout());		
	//setSize(300,220);				
	
	//South
	JPanel southPanel = new JPanel(new FlowLayout());
	southPanel.add(okButton);
	southPanel.add(cancelButton);
	okButton.addActionListener(this);
	cancelButton.addActionListener(this);
	add(southPanel,BorderLayout.SOUTH);
	
	//Center_North
	JPanel center_north = new JPanel(new FlowLayout());		
	if(applicationData.isLocationIndexMinusOne == false){		
		center_north.setBorder(BorderFactory.createTitledBorder(
			"Window Size and Location (Relative to +1 Index)"));
	}
	else{
		center_north.setBorder(BorderFactory.createTitledBorder(
			"Window Size and Location"));
	}						
	center_north.add(new JLabel("From "));
	center_north.add(windowSizeFromField);
	center_north.add(new JLabel(" To "));
	center_north.add(windowSizeToField);	
	center_north.add(this.isPercentageCheckBox);				
	
	//ratio Panel
	JPanel ratioPanel = new JPanel(new GridLayout(2,1));
	ratioPanel.setBorder(BorderFactory.createTitledBorder("Ratio of #X:#Y"));
	
	JPanel xPanel = new JPanel(new FlowLayout());				
	xPanel.add(new JLabel("#X: "));
	xPanel.add(xField);				
	xPanel.add(new JLabel(" M1: "));
	xPanel.add(m1Field);
	ratioPanel.add(xPanel);
	
	JPanel yPanel = new JPanel(new FlowLayout());		
	yPanel.add(new JLabel("#Y: "));
	yPanel.add(yField);				
	yPanel.add(new JLabel(" M2: "));
	yPanel.add(m2Field);
	ratioPanel.add(yPanel);		
	
	//includes
	JPanel includesPanel = new JPanel(new GridLayout(3,3));
	includesPanel.setBorder(BorderFactory.createTitledBorder("Includes"));
	includesPanel.add(this.checkBoxH);		
	includesPanel.add(this.checkBoxL);
	includesPanel.add(this.checkBoxP);
	includesPanel.add(this.checkBoxN);
	includesPanel.add(this.checkBoxA);
	includesPanel.add(this.checkBoxK);
	includesPanel.add(this.checkBoxO);
	includesPanel.add(this.checkBoxD);
	includesPanel.add(this.checkBoxX);
	
	JPanel ratioAndIncludesPanel = new JPanel(new GridLayout(1,2));
	ratioAndIncludesPanel.add(ratioPanel);
	ratioAndIncludesPanel.add(includesPanel);		
	
	//Center
	JPanel center = new JPanel(new GridLayout(4,2));		
	center.setBorder(BorderFactory.createTitledBorder("Definitions - (Will allow user to define soon)"));						
	center.add(new JLabel("H - Hydrophobic (A, C, F, I, L, M, V)"));
	center.add(new JLabel("L - Hydrophilic (B, D, E, G, H, K, N, P, Q, R, S, T, W, Y, Z)"));
	center.add(new JLabel("P - (+ve) Charge (H, K, R)"));
	center.add(new JLabel("N - (-ve) Charge (D, E)"));
	center.add(new JLabel("A - Acidic (D, E, H)"));
	center.add(new JLabel("K - Alkaline (C, K, R, Y)"));
	center.add(new JLabel("O - Order (N, C, I, L, F, W, Y, V)"));
	center.add(new JLabel("D - Disorder (A, R, Q, E, G, K, P, S, Z)"));
	
	//Center
	JPanel north = new JPanel(new BorderLayout());		
	north.add(center_north,BorderLayout.NORTH);
	north.add(ratioAndIncludesPanel,BorderLayout.CENTER);		
	add(north, BorderLayout.NORTH);
	add(center,BorderLayout.CENTER);
	parent.pack();
	}

	private void okButtonPressed(){
		JTextField textField = null;
		try{
			int from = validateField(this.windowSizeFromField,"From");
			int to = validateField(this.windowSizeToField,"To");
			int x = validateField(this.xField,"#X");
			int y = validateField(this.yField,"#Y");
			int m1 = validateField(this.m1Field,"M1");
			int m2 = validateField(this.m2Field,"M2");
			if(from > to){
				textField = this.windowSizeFromField;
				throw new Exception("From cannot be greater than To");				
			}else if(m1 < 0){
				textField = this.m1Field;
				throw new Exception("Mistake Allowed cannot be < 0");	
			}else if(m2 < 0){
				textField = this.m2Field;
				throw new Exception("Mistake Allowed cannot be < 0");	
			}else if(x <= m1){
				textField = this.xField;
				throw new Exception("#X cannot be <= M1");
			}else if(y <= m2){
				textField = this.yField;
				throw new Exception("#Y cannot be <= M2");
			}
			generateRatioKgram(x,m1,y,m2,from,to);
		}catch(NumberFormatException ne){
			//do nothing since error message has already been showed in validateField
		}catch(Exception e){
			JOptionPane.showMessageDialog(parent,e.getMessage(),"ERROR",
				JOptionPane.ERROR_MESSAGE);
			if(textField != null)
	   		textField.requestFocusInWindow();
	   		throw new NumberFormatException();
		}
	}
	
	private int validateField(JTextField textField,String name) throws NumberFormatException{
		try{
			return Integer.parseInt(textField.getText());
		}
		catch(NumberFormatException e){
			JOptionPane.showMessageDialog(parent,"Input only numbers into " + name,"ERROR",
				JOptionPane.ERROR_MESSAGE);
   			textField.requestFocusInWindow();
   			throw new NumberFormatException();
		}
	}	

	@Override
	public void actionPerformed(ActionEvent ae) {			
		if(ae.getSource().equals(this.okButton)){
			okButtonPressed();
		}else if(ae.getSource().equals(this.cancelButton)){
			this.parent.dispose();
		}
	}
	
	private void generateRatioKgram(final int xGram, final int xMistake, final int yGram, final int yMistake, 
			final int windowFrom, final int windowTo){
		if(applicationData.getOneThread() == null){
			statusPane.setText("Generating Ratio k-gram...");
			applicationData.setOneThread(new Thread(){
				public void run(){		
					int totalChecked = 0;
					String[] symbol;														
					if(checkBoxH.isSelected()) totalChecked++;
					if(checkBoxL.isSelected()) totalChecked++;
					if(checkBoxP.isSelected()) totalChecked++;
					if(checkBoxN.isSelected()) totalChecked++;
					if(checkBoxA.isSelected()) totalChecked++;
					if(checkBoxK.isSelected()) totalChecked++;
					if(checkBoxO.isSelected()) totalChecked++;
					if(checkBoxD.isSelected()) totalChecked++;
					if(checkBoxX.isSelected()) totalChecked++;
					
					symbol = new String[totalChecked];
					int tempIndex = 0;
					if(checkBoxH.isSelected()){ symbol[tempIndex] = "H"; tempIndex++; }
					if(checkBoxL.isSelected()){ symbol[tempIndex] = "L"; tempIndex++; }
					if(checkBoxP.isSelected()){ symbol[tempIndex] = "P"; tempIndex++; }
					if(checkBoxN.isSelected()){ symbol[tempIndex] = "N"; tempIndex++; }
					if(checkBoxA.isSelected()){ symbol[tempIndex] = "A"; tempIndex++; }
					if(checkBoxK.isSelected()){ symbol[tempIndex] = "K"; tempIndex++; }
					if(checkBoxO.isSelected()){ symbol[tempIndex] = "O"; tempIndex++; }
					if(checkBoxD.isSelected()){ symbol[tempIndex] = "D"; tempIndex++; }
					if(checkBoxX.isSelected()){ symbol[tempIndex] = "X"; tempIndex++; }															
					//Generate All possible permutations
					for(int x = 0; x < (int)Math.pow(totalChecked,xGram); x++){						
						int tempXgram = xGram;
						int y = x;
						String tempXString = "";
						while((tempXgram--)>0){
							tempXString += symbol[y%totalChecked];	
							y = y/totalChecked;
						}
						//invert the string for ease of checking purpose
						String tempXFeature = "";
						for(int z = tempXString.length()-1; z >= 0; z--)
							tempXFeature += tempXString.charAt(z);
						
						for(int w = 0; w < (int)Math.pow(totalChecked,yGram); w++){		
							int tempYgram = yGram;
							int y2 = w;
							String tempYString = "";
							while((tempYgram--)>0){
								tempYString += symbol[y2%totalChecked];	
								y2 = y2/totalChecked;
							}
							//invert the string for ease of checking purpose
							String tempYFeature = "";
							for(int z = tempYString.length()-1; z >= 0; z--)
								tempYFeature += tempYString.charAt(z);
							
							//generate the ratio kgram feature 							
							String featureString = "O_"+tempXFeature+"_"+xMistake+"_"+tempYFeature+"_"+yMistake+"_"+
							isPercentageCheckBox.isSelected()+"_"+windowFrom+"_"+windowTo;													
							model.add(new RatioOfKGramFeature(featureString));							
						}						
					}
					statusPane.setText("Generating Ratio K-gram...DONE!");
					JOptionPane.showMessageDialog(parent,"New Features Added!","Information",JOptionPane.INFORMATION_MESSAGE);
					applicationData.setOneThread(null);
				}
			});
			applicationData.getOneThread().setPriority(Thread.MIN_PRIORITY);
			applicationData.getOneThread().start();				
		}else{
			JOptionPane.showMessageDialog(parent,"Still generating...\nPlease try again later...",
				"Already Generating",JOptionPane.WARNING_MESSAGE);
		}
	}
}
