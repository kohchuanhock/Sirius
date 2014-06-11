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
package sirius.trainer.features.gui.ratio;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import sirius.main.ApplicationData;
import sirius.trainer.features.Feature;
import sirius.trainer.features.RatioOfKGramFeature;
import sirius.trainer.main.StatusPane;
import sirius.trainer.step2.FeatureTableModel;
import sirius.trainer.step2.Physiochemical2;

public class RatioAllPossiblePane extends JComponent implements ActionListener, ItemListener{
	static final long serialVersionUID = sirius.Sirius.version;
	private JButton okButton;
	private JButton cancelButton;
	private JDialog parent;
	
	private JTextField xField;
	private JTextField yField;
	private JTextField m1Field;
	private JTextField m2Field;
	private JTextField windowSizeFromField;
	private JTextField windowSizeToField;	
		
	private JCheckBox aCheckBox;	
	private JCheckBox cCheckBox;	
	private JCheckBox gCheckBox;		
	private JCheckBox tCheckBox;		
	private JCheckBox xCheckBox;
	private JCheckBox isPercentageCheckBox = new JCheckBox("%");
	
	private FeatureTableModel model;
	private StatusPane statusPane;
	private ApplicationData applicationData;
	private JComboBox codingNamesComboBox = new JComboBox();
	
	private JPanel centerPanel;
	private JPanel definitionsPanel;
	private JPanel center_east;
	
	private Physiochemical2 p2;
	private ArrayList<JCheckBox> proteinCheckBoxArrayList;
		
	public RatioAllPossiblePane(JDialog parent,FeatureTableModel model,ApplicationData applicationData){
		this.parent = parent;
		this.model = model;
		this.statusPane = applicationData.getStatusPane();
		this.applicationData = applicationData;
		
		setLayout(new BorderLayout());
		//setTitle("K-gram with X mistakes");
		//setSize(300,220);				
		
		//ComboBox Panel
		JPanel codingNamePanel = new JPanel();
		codingNamePanel.setBorder(BorderFactory.createTitledBorder("Coding Schemes"));		
		this.codingNamesComboBox.addItemListener(this);		
		for(int x = 0; x < Physiochemical2.codingNameList.length; x++)
			this.codingNamesComboBox.addItem(Physiochemical2.codingNameList[x]);
		codingNamePanel.add(this.codingNamesComboBox);
		
		//South
		okButton = new JButton("OK");
		cancelButton = new JButton("Cancel");
		JPanel southPanel = new JPanel(new FlowLayout());
		southPanel.add(okButton);
		southPanel.add(cancelButton);
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		add(southPanel,BorderLayout.SOUTH);
		
		//Center_North
		JPanel center_north = new JPanel(new FlowLayout());
		if(applicationData.isLocationIndexMinusOne == false)	
			center_north.setBorder(BorderFactory.createTitledBorder(
				"Window Size and Location (Relative to +1 Index)"));
		else
			center_north.setBorder(BorderFactory.createTitledBorder(
				"Window Size and Location"));
		
		JLabel windowSizeFromLabel = new JLabel("From ");
		windowSizeFromField = new JTextField(5);		
		JLabel windowSizeToLabel = new JLabel(" To ");
		windowSizeToField = new JTextField(5);		
		
		center_north.add(windowSizeFromLabel);
		center_north.add(windowSizeFromField);
		center_north.add(windowSizeToLabel);
		center_north.add(windowSizeToField);
		center_north.add(this.isPercentageCheckBox);
		
		JPanel comboBoxAndWindowPanel = new JPanel(new BorderLayout());
		comboBoxAndWindowPanel.add(codingNamePanel,BorderLayout.WEST);
		comboBoxAndWindowPanel.add(center_north,BorderLayout.CENTER);
		
		//center_east 
		center_east = null; 
		if(applicationData.getSequenceType().indexOf("DNA") != -1){		
			//setSize(300,220);		
			center_east = new JPanel(new GridLayout(5,1));
			center_east.setBorder(BorderFactory.createTitledBorder("Includes"));
		
			aCheckBox = new JCheckBox("A",true);
			cCheckBox = new JCheckBox("C",true);
			tCheckBox = new JCheckBox("T",true);
			gCheckBox = new JCheckBox("G",true);
			xCheckBox = new JCheckBox("X = A,C,T or G");
			
			center_east.add(aCheckBox);
			center_east.add(cCheckBox);
			center_east.add(tCheckBox);
			center_east.add(gCheckBox);
			center_east.add(xCheckBox);
		}else if(applicationData.getSequenceType().indexOf("PROTEIN") != -1){
			//setSize(500,220);		
			center_east = new JPanel(new GridLayout(7,3));
			center_east.setBorder(BorderFactory.createTitledBorder("Includes"));					
			
			this.proteinCheckBoxArrayList = new ArrayList<JCheckBox>();	
			p2 = new Physiochemical2("Original");
			p2.setCheckBox(center_east, proteinCheckBoxArrayList);	
		}				
		
		//center_south
		JPanel center_south = new JPanel(new GridLayout(2,1));
		center_south.setBorder(BorderFactory.createTitledBorder("Ratio of #X:#Y"));
		
		JPanel xPanel = new JPanel(new FlowLayout());
		JLabel xLabel = new JLabel("#X = ");
		xField = new JTextField(5);
		xPanel.add(xLabel);
		xPanel.add(xField);
		JLabel m1Label = new JLabel(" M1 = ");
		m1Field = new JTextField(5);
		xPanel.add(m1Label);
		xPanel.add(m1Field);
		center_south.add(xPanel);
		
		JPanel yPanel = new JPanel(new FlowLayout());
		JLabel yLabel = new JLabel("#Y = ");
		yField = new JTextField(5);
		yPanel.add(yLabel);
		yPanel.add(yField);
		JLabel m2Label = new JLabel(" M2 = ");
		m2Field = new JTextField(5);
		yPanel.add(m2Label);
		yPanel.add(m2Field);
		center_south.add(yPanel);							
		
		//Center
		centerPanel = new JPanel(new BorderLayout());		
		centerPanel.add(comboBoxAndWindowPanel,BorderLayout.NORTH);
		centerPanel.add(center_south,BorderLayout.CENTER);		
		add(centerPanel,BorderLayout.CENTER);
		add(center_east,BorderLayout.EAST);
		parent.pack();
		}	
	public void actionPerformed(ActionEvent ae){
		if(ae.getSource().equals(okButton)){			
			try{				
				int fromFieldInt = validateField(windowSizeFromField,"From Field");
				int toFieldInt = validateField(windowSizeToField,"To Field");				
				int xFieldInt = validateField(xField,"#X Field");			
				int yFieldInt = validateField(yField,"#Y Field");
				int m1FieldInt = validateField(m1Field,"M1 Field");
				int m2FieldInt = validateField(m2Field,"M2 Field");
				int windowSize = 0;
				if((toFieldInt >= 0 && fromFieldInt >= 0) || (toFieldInt <= 0 && fromFieldInt <= 0))
					windowSize = toFieldInt - fromFieldInt +1;
				else
					windowSize = toFieldInt - fromFieldInt;
				if(toFieldInt < fromFieldInt){
					JOptionPane.showMessageDialog(parent,"From field should not be smaller than To Field",
						"ERROR",JOptionPane.ERROR_MESSAGE);
					windowSizeFromField.requestFocusInWindow();
					
				}else if(fromFieldInt == 0 && applicationData.isLocationIndexMinusOne == false){
					JOptionPane.showMessageDialog(parent,"0 is not allowed, choose either -1 or +1.","ERROR",
						JOptionPane.ERROR_MESSAGE);
					windowSizeFromField.requestFocusInWindow();
				}else if(fromFieldInt < 0 && applicationData.isLocationIndexMinusOne == true){
					JOptionPane.showMessageDialog(parent,"Cannot be < 0","ERROR",
						JOptionPane.ERROR_MESSAGE);
					windowSizeFromField.requestFocusInWindow();
				}else if(toFieldInt == 0 && applicationData.isLocationIndexMinusOne == false){
					JOptionPane.showMessageDialog(parent,"0 is not allowed, choose either -1 or +1.","ERROR",
						JOptionPane.ERROR_MESSAGE);
					windowSizeToField.requestFocusInWindow();
				}else if(xFieldInt <= 0){
					JOptionPane.showMessageDialog(parent,"#X can only be > 0","ERROR",
						JOptionPane.ERROR_MESSAGE);
					xField.requestFocusInWindow();
				}else if(yFieldInt <= 0){
					JOptionPane.showMessageDialog(parent,"#Y can only be > 0","ERROR",
						JOptionPane.ERROR_MESSAGE);
					yField.requestFocusInWindow();
				}else if(m1FieldInt < 0 || m2FieldInt < 0){
					JOptionPane.showMessageDialog(parent,"M1 Field or M2 Field cannot be less than 0","ERROR",
						JOptionPane.ERROR_MESSAGE);
						m1Field.requestFocusInWindow();
				}else if(windowSize < xFieldInt || windowSize < yFieldInt){
					JOptionPane.showMessageDialog(parent,"Window Size is less than #X size or #Y size","ERROR",
							JOptionPane.ERROR_MESSAGE);
				}else{
					if(model != null){
						generateFeatures(xFieldInt,yFieldInt,fromFieldInt,toFieldInt,m1FieldInt,m2FieldInt);
					}else{
						
					}
				}				
			}
			catch(NumberFormatException e){}
			catch(Exception e){e.printStackTrace();}			
		}
		else if(ae.getSource().equals(cancelButton)){
			parent.dispose();
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
	private void generateFeatures(final int xFieldInt,final int yFieldInt,final int fromFieldInt,
		final int toFieldInt,final int m1FieldInt,final int m2FieldInt){
		if(applicationData.getOneThread() == null){
			statusPane.setText("Generating features...");
			applicationData.setOneThread(new Thread(){
			public void run(){
				//Generate the features
				int totalChecked = 0;
				String[] symbol;
				if(applicationData.getSequenceType().indexOf("DNA") != -1){						
					if(aCheckBox.isSelected())
						totalChecked++;
					if(cCheckBox.isSelected())
						totalChecked++;
					if(tCheckBox.isSelected())
						totalChecked++;
					if(gCheckBox.isSelected())
						totalChecked++;
					if(xCheckBox.isSelected())
						totalChecked++;
					
					symbol = new String[totalChecked];
					int tempIndex = 0;
					if(aCheckBox.isSelected()){
						symbol[tempIndex] = "A";
						tempIndex++;
					}						
					if(cCheckBox.isSelected()){
						symbol[tempIndex] = "C";
						tempIndex++;
					}						
					if(tCheckBox.isSelected()){
						symbol[tempIndex] = "T";
						tempIndex++;
					}						
					if(gCheckBox.isSelected()){
						symbol[tempIndex] = "G";
						tempIndex++;
					}						
					if(xCheckBox.isSelected()){
						symbol[tempIndex] = "X";
						tempIndex++;
					}						
				}else{//protein					
					for(int x = 0; x < proteinCheckBoxArrayList.size(); x++){
						if(proteinCheckBoxArrayList.get(x).isSelected())
							totalChecked++;							
					}													
					symbol = new String[totalChecked];
					int tempIndex = 0;
					for(int x = 0; x < proteinCheckBoxArrayList.size(); x++){
						if(proteinCheckBoxArrayList.get(x).isSelected()){
							symbol[tempIndex] = p2.getLetter(x) + "";
							tempIndex++;
						}												
					}			
				}									
				
				//generate all possible permutation
				for(int x = 0; x < (int)Math.pow(totalChecked,xFieldInt); x++){
					String tempString1 = "";
					int w = x;
					int temp1 = xFieldInt;
					while((temp1--)>0){
						tempString1 += symbol[w%totalChecked];							
						w = w/totalChecked;
					}
					int yValue = 0;
					if(xFieldInt == yFieldInt){
						//This is because Ratio of A:T and T:A are correlated, no point having both feature
						//+1 because ratio of A:A is always going to be 1							
						yValue = x + 1;
					}				
					for(int y = yValue; y < (int)Math.pow(totalChecked,yFieldInt); y++){
						String tempString2 = "";
						int z = y;
						int temp2 = yFieldInt;
						while((temp2--)>0){
							tempString2 += symbol[z%totalChecked];
							z = z/totalChecked;
						}
						//invert the string for ease of checking purpose
						String tempFeature1 = "";
						for(int a = tempString1.length() - 1; a >= 0; a--)
							tempFeature1 += tempString1.charAt(a);
						String tempFeature2 = "";
						for(int b = tempString2.length() - 1; b >= 0; b--)
							tempFeature2 += tempString2.charAt(b);
						Feature tempData = null;
						if(codingNamesComboBox.getSelectedIndex() == 0){
							tempData = new RatioOfKGramFeature('R',tempFeature1,tempFeature2,
									fromFieldInt,toFieldInt,m1FieldInt,m2FieldInt,isPercentageCheckBox.isSelected());
						}else{
							tempData = new RatioOfKGramFeature('Q',tempFeature1,tempFeature2,
									fromFieldInt,toFieldInt,m1FieldInt,m2FieldInt,isPercentageCheckBox.isSelected(),
									codingNamesComboBox.getSelectedIndex());
						}
						model.add(tempData);
					}
				}
				statusPane.setText("Generating features...DONE!");
				applicationData.setOneThread(null);
				JOptionPane.showMessageDialog(parent,"New Features Added!","Information",JOptionPane.INFORMATION_MESSAGE);
				//parent.dispose();
				}
			});
			applicationData.getOneThread().setPriority(Thread.MIN_PRIORITY);
			applicationData.getOneThread().start();
		}else{
			JOptionPane.showMessageDialog(parent,"Still generating...\nPlease try again later...",
				"Already Generating",JOptionPane.WARNING_MESSAGE);
		}
	}
	@Override
	public void itemStateChanged(ItemEvent ie) {
		if(ie.getSource().equals(this.codingNamesComboBox)){			
			if(this.definitionsPanel != null)
				this.centerPanel.remove(this.definitionsPanel);		
			if(this.codingNamesComboBox.getSelectedIndex() != 0){
				//parent.setSize(800,400);
				this.definitionsPanel = new JPanel(new GridLayout(4,2));
				this.definitionsPanel.setBorder(BorderFactory.createTitledBorder("Definitions"));
				p2 = new Physiochemical2((String) this.codingNamesComboBox.getSelectedItem());
				p2.setPanel(this.definitionsPanel);
				this.centerPanel.add(this.definitionsPanel, BorderLayout.SOUTH);
				center_east.removeAll();
				this.proteinCheckBoxArrayList = new ArrayList<JCheckBox>();			
				p2 = new Physiochemical2((String) this.codingNamesComboBox.getSelectedItem());
				p2.setCheckBox(center_east, proteinCheckBoxArrayList);	
			}else{		
				if(center_east != null)
					center_east.removeAll();				
				this.proteinCheckBoxArrayList = new ArrayList<JCheckBox>();			
				p2 = new Physiochemical2("Original");
				if(center_east != null)
					p2.setCheckBox(center_east, proteinCheckBoxArrayList);	
				//parent.setSize(680,300);
			}		
			parent.pack();
			revalidate();
			repaint();
		}
	}
}

