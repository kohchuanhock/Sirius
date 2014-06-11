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
package sirius.trainer.features.gui.kgram;


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
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import sirius.main.ApplicationData;
import sirius.nnsearcher.main.DefineConstraintsDialog;
import sirius.nnsearcher.main.MustHaveTableModel;
import sirius.trainer.features.KGramFeature;
import sirius.trainer.features.Feature;
import sirius.trainer.step2.FeatureTableModel;
import sirius.trainer.step2.Physiochemical2;

class KgramManualPane extends JComponent implements ActionListener,ItemListener{	
	static final long serialVersionUID = sirius.Sirius.version;
	private JButton okButton;
	private JButton cancelButton;
	private JDialog parent;
	
	private JTextField kgramField;
	private JTextField xmistakeField;
	private JTextField windowSizeFromField;
	private JTextField windowSizeToField;		
	
	private FeatureTableModel model;	
	private ApplicationData applicationData;
	
	private JRadioButton absoluteButton = new JRadioButton("Absolute");
	private JRadioButton relativeButton = new JRadioButton("Relative",true);
	
	private MustHaveTableModel constraintsModel;
	private JComboBox codingNamesComboBox;
	
	private JPanel centerPanel;
	private JPanel definitionsPanel;
	private Physiochemical2 p2 = new Physiochemical2("Original");
	private JCheckBox isPercentageCheckBox = new JCheckBox("%");
		
	public KgramManualPane(JDialog parent,FeatureTableModel model,ApplicationData applicationData, MustHaveTableModel constraintsModel){		
		this.parent = parent;
		this.model = model;	
		this.applicationData = applicationData;
		this.constraintsModel = constraintsModel;		
		
		setLayout(new BorderLayout());					
		
		//South
		okButton = new JButton("OK");
		cancelButton = new JButton("Cancel");
		JPanel southPanel = new JPanel(new FlowLayout());
		southPanel.add(okButton);
		southPanel.add(cancelButton);
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		add(southPanel,BorderLayout.SOUTH);
		
		JPanel codingNamePanel = new JPanel();
		codingNamePanel.setBorder(BorderFactory.createTitledBorder("Coding Schemes"));
		this.codingNamesComboBox = new JComboBox();
		this.codingNamesComboBox.addItemListener(this);		
		for(int x = 0; x < Physiochemical2.codingNameList.length; x++)
			this.codingNamesComboBox.addItem(Physiochemical2.codingNameList[x]);
		codingNamePanel.add(this.codingNamesComboBox);
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
		
		JPanel northPanel = new JPanel(new BorderLayout());
		if(applicationData.getSequenceType().indexOf("PROTEIN") != -1)
			northPanel.add(codingNamePanel,BorderLayout.WEST);
		northPanel.add(center_north,BorderLayout.CENTER);		
		
		//center_south
		JPanel center_south = new JPanel(new GridLayout(2,1));
		center_south.setBorder(BorderFactory.createTitledBorder("K-gram with X-mistakes allowed"));
		
		JPanel kgramPanel = new JPanel(new FlowLayout());
		JLabel kgramLabel = new JLabel("K: ");
		kgramField = new JTextField(5);
		kgramPanel.add(kgramLabel);
		kgramPanel.add(kgramField);
		center_south.add(kgramPanel);
		
		JPanel xmistakePanel = new JPanel(new FlowLayout());
		JLabel xmistakeLabel = new JLabel("X: ");
		xmistakeField = new JTextField(5);
		xmistakePanel.add(xmistakeLabel);
		xmistakePanel.add(xmistakeField);
		center_south.add(xmistakePanel);							
		
		JPanel absoluteRelativePanel = new JPanel(new GridLayout(2,1));
		absoluteRelativePanel.setBorder(BorderFactory.createTitledBorder("Value"));		
		absoluteButton.addActionListener(this);
		relativeButton.addActionListener(this);
		absoluteRelativePanel.add(absoluteButton);		
		absoluteRelativePanel.add(relativeButton);
		
		JPanel center_center = new JPanel(new BorderLayout());
		center_center.add(absoluteRelativePanel,BorderLayout.EAST);		
		center_center.add(center_south,BorderLayout.CENTER);				
		
		//Center
		this.centerPanel = new JPanel(new BorderLayout());		
		this.centerPanel.add(northPanel,BorderLayout.NORTH);
		this.centerPanel.add(center_center,BorderLayout.CENTER);	
		add(this.centerPanel,BorderLayout.CENTER);				
		}	
	public void actionPerformed(ActionEvent ae){
		if(ae.getSource().equals(okButton)){			
			try{				
				int fromFieldInt = validateField(windowSizeFromField,"From Field");
				int toFieldInt = validateField(windowSizeToField,"To Field");				
				int xmistakeFieldInt = validateField(xmistakeField,"X-mistake Field");			
				String kgramFieldString = validateString(kgramField,"K-gram Field");
				int kgramFieldInt = kgramFieldString.length();
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
				else if(xmistakeFieldInt < 0){
					JOptionPane.showMessageDialog(parent,"X has to be greater or equal to 0","ERROR",
						JOptionPane.ERROR_MESSAGE);
					xmistakeField.requestFocusInWindow();
				}	
				else if(windowSize < kgramFieldInt){
					JOptionPane.showMessageDialog(parent,"Window Size is less than kgram","ERROR",
							JOptionPane.ERROR_MESSAGE);
				}	
				else{
					//Generate the features
					Feature tempData;
					if(codingNamesComboBox.getSelectedIndex() == 0){//normal kgram
						if(absoluteButton.isSelected() == true){//k-gram with absolute value
							tempData = new KGramFeature('K',kgramFieldString,xmistakeFieldInt,fromFieldInt,toFieldInt,
									isPercentageCheckBox.isSelected());				
						}else{//k-gram with relative value
							tempData = new KGramFeature('L',kgramFieldString,xmistakeFieldInt,fromFieldInt,toFieldInt,
									isPercentageCheckBox.isSelected());
						}		
					}else{//physiochemical2 kgram
						if(absoluteButton.isSelected() == true){//k-gram with absolute value
							tempData = new KGramFeature('D',kgramFieldString,xmistakeFieldInt,fromFieldInt,toFieldInt,
									codingNamesComboBox.getSelectedIndex(),isPercentageCheckBox.isSelected());				
						}else{//k-gram with relative value
							tempData = new KGramFeature('E',kgramFieldString,xmistakeFieldInt,fromFieldInt,toFieldInt,
									codingNamesComboBox.getSelectedIndex(),isPercentageCheckBox.isSelected());
						}						
					}
					
					if(model != null){
						model.add(tempData);				
						JOptionPane.showMessageDialog(parent,"New Features Added!","Information",JOptionPane.INFORMATION_MESSAGE);
					}else{
						//this is called by NNSearch add constraint button
						DefineConstraintsDialog dialog = new DefineConstraintsDialog(tempData, this.constraintsModel);
			    		dialog.setLocationRelativeTo(parent);
			    		dialog.setVisible(true);
					}
				}				
			}
			catch(NumberFormatException e){
				//e.printStackTrace();
			}
		}
		else if(ae.getSource().equals(cancelButton)){
			parent.dispose();
		}
		else if(ae.getSource().equals(absoluteButton)){
			//toggle of the radio buttons
			absoluteButton.setSelected(true);
			relativeButton.setSelected(false);
		}
		else if(ae.getSource().equals(relativeButton)){
			//toggle of the radio buttons
			absoluteButton.setSelected(false);
			relativeButton.setSelected(true);
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
			//Not need to throw the exception since i already show the option pane
			//throw new NumberFormatException();
			return null;
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

