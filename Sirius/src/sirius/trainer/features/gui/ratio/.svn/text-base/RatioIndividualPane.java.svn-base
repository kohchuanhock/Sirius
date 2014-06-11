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
import sirius.nnsearcher.main.DefineConstraintsDialog;
import sirius.nnsearcher.main.MustHaveTableModel;
import sirius.trainer.features.Feature;
import sirius.trainer.features.RatioOfKGramFeature;
import sirius.trainer.step2.FeatureTableModel;
import sirius.trainer.step2.Physiochemical2;

public class RatioIndividualPane extends JComponent implements ActionListener,ItemListener{
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
	
	private FeatureTableModel model;
	private ApplicationData applicationData;
	private MustHaveTableModel constraintsModel;
	
	private JCheckBox isPercentageCheckBox = new JCheckBox("%");
	private JComboBox codingNamesComboBox = new JComboBox();
	private JPanel centerPanel = new JPanel(new BorderLayout());	
	private Physiochemical2 p2 = new Physiochemical2("Original");
	private JPanel definitionsPanel;
		
public RatioIndividualPane(JDialog parent,FeatureTableModel model,ApplicationData applicationData,MustHaveTableModel constraintsModel){
		this.parent = parent;
		this.model = model;		
		this.applicationData = applicationData;
		this.constraintsModel = constraintsModel;
		
		setLayout(new BorderLayout());
		//setTitle("K-gram with X mistakes");
		//setSize(300,220);				
		
		//South
		okButton = new JButton("OK");
		cancelButton = new JButton("Cancel");
		JPanel southPanel = new JPanel(new FlowLayout());
		southPanel.add(okButton);
		southPanel.add(cancelButton);
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		add(southPanel,BorderLayout.SOUTH);
		
		//ComboBox Panel
		JPanel codingNamePanel = new JPanel();
		codingNamePanel.setBorder(BorderFactory.createTitledBorder("Coding Schemes"));		
		this.codingNamesComboBox.addItemListener(this);		
		for(int x = 0; x < Physiochemical2.codingNameList.length; x++)
			this.codingNamesComboBox.addItem(Physiochemical2.codingNameList[x]);
		codingNamePanel.add(this.codingNamesComboBox);
		
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
		
		//center_south
		JPanel center_south = new JPanel(new GridLayout(2,1));
		center_south.setBorder(BorderFactory.createTitledBorder("Ratio of #X:#Y"));
		
		JPanel xPanel = new JPanel(new FlowLayout());
		JLabel xLabel = new JLabel("#X: ");
		xField = new JTextField(5);
		xPanel.add(xLabel);
		xPanel.add(xField);
		JLabel m1Label = new JLabel(" M1: ");
		m1Field = new JTextField(5);
		xPanel.add(m1Label);
		xPanel.add(m1Field);
		center_south.add(xPanel);
		
		JPanel yPanel = new JPanel(new FlowLayout());
		JLabel yLabel = new JLabel("#Y: ");
		yField = new JTextField(5);
		yPanel.add(yLabel);
		yPanel.add(yField);
		JLabel m2Label = new JLabel(" M2: ");
		m2Field = new JTextField(5);
		yPanel.add(m2Label);
		yPanel.add(m2Field);
		center_south.add(yPanel);							
		
		//Center		
		centerPanel.add(comboBoxAndWindowPanel,BorderLayout.NORTH);
		centerPanel.add(center_south,BorderLayout.CENTER);	
		add(centerPanel,BorderLayout.CENTER);
		}	
	public void actionPerformed(ActionEvent ae){
		if(ae.getSource().equals(okButton)){			
			//To be implement
			try{				
				int fromFieldInt = validateField(windowSizeFromField,"From Field");
				int toFieldInt = validateField(windowSizeToField,"To Field");				
				String xFieldString = validateString(xField,"#X Field");			
				String yFieldString = validateString(yField,"#Y Field");
				int xFieldInt = xFieldString.length();
				int yFieldInt = yFieldString.length();
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
					
				}									
				else if(fromFieldInt == 0 && applicationData.isLocationIndexMinusOne == false){
					JOptionPane.showMessageDialog(parent,"0 is not allowed, choose either -1 or +1.","ERROR",
						JOptionPane.ERROR_MESSAGE);
					windowSizeFromField.requestFocusInWindow();
				}
				else if(fromFieldInt < 0 && applicationData.isLocationIndexMinusOne == true){
					JOptionPane.showMessageDialog(parent,"Cannot be < 0","ERROR",
						JOptionPane.ERROR_MESSAGE);
					windowSizeFromField.requestFocusInWindow();
				}
				else if(toFieldInt == 0 && applicationData.isLocationIndexMinusOne == false){
					JOptionPane.showMessageDialog(parent,"0 is not allowed, choose either -1 or +1.","ERROR",
						JOptionPane.ERROR_MESSAGE);
					windowSizeToField.requestFocusInWindow();
				}
				else if(m1FieldInt < 0 || m2FieldInt < 0){
					JOptionPane.showMessageDialog(parent,"M1 Field or M2 Field cannot be less than 0","ERROR",
						JOptionPane.ERROR_MESSAGE);
						m1Field.requestFocusInWindow();
				}else if(windowSize < xFieldInt || windowSize < yFieldInt){
					JOptionPane.showMessageDialog(parent,"Window Size is less than #X size or #Y size","ERROR",
							JOptionPane.ERROR_MESSAGE);
				}else{					
					Feature tempData = null;
					if(codingNamesComboBox.getSelectedIndex() == 0){//normal ratio						
							tempData = new RatioOfKGramFeature('R',xFieldString,yFieldString,
									fromFieldInt,toFieldInt,m1FieldInt,m2FieldInt,isPercentageCheckBox.isSelected());				
					}else{//physiochemical2 kgram						
							tempData = new RatioOfKGramFeature('R',xFieldString,yFieldString,
									fromFieldInt,toFieldInt,m1FieldInt,m2FieldInt,isPercentageCheckBox.isSelected(),
									this.codingNamesComboBox.getSelectedIndex());																			
					}
					if(model != null){
						//Generate the features																
						model.add(tempData);
						JOptionPane.showMessageDialog(parent,"New Features Added!","Information",JOptionPane.INFORMATION_MESSAGE);
						//parent.dispose();
					}else{
						//this is called by NNSearch add constraint button
						DefineConstraintsDialog dialog = new DefineConstraintsDialog(tempData, this.constraintsModel);
			    		dialog.setLocationRelativeTo(parent);
			    		dialog.setVisible(true);
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
	
	private String validateString(JTextField textField,String name) throws NumberFormatException{
		String validString = "";
		try{
			String text = textField.getText().trim().toUpperCase();			
			if(text.length() == 0)
				throw new NumberFormatException();
			if(applicationData.getSequenceType().indexOf("DNA") != -1){
				for(int x = 0; x < text.length(); x++){
					switch(text.charAt(x)){
						case 'A': //adenosine
						case 'C': //cytidine
						case 'T': //guanine
						case 'G': //thymidine
						case 'X': //any
							break;
						default:
							throw new Exception();
					}
				}		
			}else if(applicationData.getSequenceType().indexOf("PROTEIN") != -1){					
				for(int x = 0; x < p2.getClassificationLetter().size(); x++){
					validString += p2.getLetter(x);
				}				
				for(int x = 0; x < text.length(); x++){
					String temp = text.charAt(x) + "";
					temp = temp.toUpperCase();
					if(validString.indexOf(temp) == -1)
						throw new Exception();
				}						
			}					
			return text;
		}catch(NumberFormatException ex){
			JOptionPane.showMessageDialog(parent,"K-gram cannot be empty","ERROR",
				JOptionPane.ERROR_MESSAGE);
			textField.requestFocusInWindow();
			throw new NumberFormatException();
		}catch(Exception e){
			if(applicationData.getSequenceType().indexOf("DNA") != -1)
				JOptionPane.showMessageDialog(parent,"Input only A,C,T,G or X in " + name,"ERROR",
					JOptionPane.ERROR_MESSAGE);
			else
				JOptionPane.showMessageDialog(parent,"Input only " + validString + " in " + name,"ERROR",JOptionPane.ERROR_MESSAGE);
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
			}else{				
				p2 = new Physiochemical2((String) this.codingNamesComboBox.getSelectedItem());
				//parent.setSize(680,300);
			}				
			parent.pack();
			revalidate();
			repaint();
		}
	}    	
}
