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
package sirius.trainer.features.gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import sirius.trainer.features.MetaFeature;
import sirius.trainer.step2.FeatureTableModel;
import sirius.trainer.step2.Physiochemical2;

public class MetaFeatureDialog extends JDialog implements ActionListener{
	static final long serialVersionUID = sirius.Sirius.version;	
	
	JInternalFrame parent;
	FeatureTableModel model;
	
	JCheckBox headerCheckBox = new JCheckBox("Header");
	JCheckBox headerLocalCheckBox = new JCheckBox("Local");
	JTextField headerFromTextField = new JTextField(3);
	JTextField headerToTextField = new JTextField(3);
	JTextField headerPrefixTextField = new JTextField(3);
	JTextField headerSuffixTextField = new JTextField(3);
	JCheckBox sequenceCheckBox = new JCheckBox("Sequence");
	JComboBox codingNamesComboBox = new JComboBox();
	JRadioButton globalSequenceRadioButton = new JRadioButton("Global");
	JRadioButton localSequenceRadioButton = new JRadioButton("Local");
	JTextField windowFromTextField = new JTextField(3);
	JTextField windowToTextField = new JTextField(3);
	JCheckBox isPercentageCheckBox = new JCheckBox("%");
	
	JButton cancelButton = new JButton("Cancel");
	JButton addFeatureButton = new JButton("Add Features");
	
	public MetaFeatureDialog(JInternalFrame parent,FeatureTableModel model){
		this.parent = parent;
		this.model = model;
		
		//setSize(680,200);
		setTitle("META Features");
    	setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
    	setLayout(new BorderLayout());    	
    	
    	JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	headerPanel.add(this.headerCheckBox);
    	headerPanel.add(this.headerLocalCheckBox);
    	headerPanel.add(new JLabel(" From: "));
    	headerPanel.add(this.headerFromTextField);
    	headerPanel.add(new JLabel(" To: "));
    	headerPanel.add(this.headerToTextField);
    	headerPanel.add(new JLabel(" Prefix: "));
    	headerPanel.add(this.headerPrefixTextField);
    	headerPanel.add(new JLabel(" Suffix: "));
    	headerPanel.add(this.headerSuffixTextField);
    	this.headerLocalCheckBox.addActionListener(this);
    	this.headerFromTextField.setEnabled(false);
		this.headerToTextField.setEnabled(false);
		this.headerPrefixTextField.setEnabled(false);
		this.headerSuffixTextField.setEnabled(false);
    	
    	JLabel sequenceAsLabel = new JLabel("as ");		
    	for(int x = 0; x < Physiochemical2.codingNameList.length; x++)
			this.codingNamesComboBox.addItem(Physiochemical2.codingNameList[x]);
		this.globalSequenceRadioButton.addActionListener(this);
		this.globalSequenceRadioButton.setSelected(true);
		this.localSequenceRadioButton.addActionListener(this);
		JLabel fromLabel = new JLabel("From: ");
		JLabel toLabel = new JLabel("To: ");
		this.windowFromTextField.setEnabled(false);
		this.windowToTextField.setEnabled(false);
		
    	JPanel sequencePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	sequencePanel.add(this.sequenceCheckBox);
    	sequencePanel.add(sequenceAsLabel);
    	sequencePanel.add(this.codingNamesComboBox);
    	sequencePanel.add(this.globalSequenceRadioButton);
    	sequencePanel.add(this.localSequenceRadioButton);
    	sequencePanel.add(fromLabel);
    	sequencePanel.add(this.windowFromTextField);
    	sequencePanel.add(toLabel);
    	sequencePanel.add(this.windowToTextField);
    	sequencePanel.add(this.isPercentageCheckBox);
    	
    	JPanel centerPanel = new JPanel(new GridLayout(3,1));
    	centerPanel.add(headerPanel);
    	centerPanel.add(sequencePanel);
    	
    	this.addFeatureButton.addActionListener(this);
    	this.cancelButton.addActionListener(this);
    	
    	JPanel southPanel = new JPanel();
    	southPanel.add(addFeatureButton);
    	southPanel.add(cancelButton);
    	
    	add(centerPanel,BorderLayout.CENTER);
    	add(southPanel,BorderLayout.SOUTH);
    	this.pack();
	}
	
	private boolean checkFields(){
		try{
			Integer.parseInt(this.windowFromTextField.getText());
			Integer.parseInt(this.windowToTextField.getText());
		}catch(NumberFormatException e){
			JOptionPane.showMessageDialog(parent,"Please check fields","ERROR",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {		
		if(ae.getSource().equals(this.headerLocalCheckBox)){
			if(this.headerLocalCheckBox.isSelected()){
				this.headerFromTextField.setEnabled(true);
				this.headerToTextField.setEnabled(true);
				this.headerPrefixTextField.setEnabled(true);
				this.headerSuffixTextField.setEnabled(true);
			}else{
				this.headerFromTextField.setEnabled(false);
				this.headerToTextField.setEnabled(false);
				this.headerPrefixTextField.setEnabled(false);
				this.headerSuffixTextField.setEnabled(false);
			}
		}else if(ae.getSource().equals(this.cancelButton))
			this.dispose();
		else if(ae.getSource().equals(this.globalSequenceRadioButton)){
			this.localSequenceRadioButton.setSelected(false);
			this.windowFromTextField.setText("");
			this.windowToTextField.setText("");
			this.windowFromTextField.setEnabled(false);
			this.windowToTextField.setEnabled(false);
			this.isPercentageCheckBox.setSelected(false);
		}else if(ae.getSource().equals(this.localSequenceRadioButton)){
			this.globalSequenceRadioButton.setSelected(false);
			this.windowFromTextField.setEnabled(true);
			this.windowToTextField.setEnabled(true);
		}else if(ae.getSource().equals(this.addFeatureButton)){
			String name;
			int windowFrom = 0;
			int windowTo = 0;
			if(this.headerCheckBox.isSelected())
				if(this.headerLocalCheckBox.isSelected() == false)
					this.model.add(new MetaFeature("X_Header",'X',0,0,0,false,false));
				else{
					if(this.headerFromTextField.getText().trim().length() == 0 || this.headerToTextField.getText().trim().length() == 0 || 
							this.headerPrefixTextField.getText().trim().length() == 0 || this.headerSuffixTextField.getText().trim().length() == 0){
						JOptionPane.showMessageDialog(parent,"Cannot leave blank or just space in header local fields","Error",JOptionPane.ERROR_MESSAGE);
						return;
					}else
						this.model.add(new MetaFeature("X_Header" + "_" + this.headerFromTextField.getText().trim() + "_" + this.headerToTextField.getText().trim() + "_" + 
							this.headerPrefixTextField.getText().trim() + "_" + this.headerSuffixTextField.getText().trim(),'X',0,0,0,false,false));				
				}
			if(this.sequenceCheckBox.isSelected()){
				if(this.globalSequenceRadioButton.isSelected() == false && checkFields() == false)
					return;
				name = "X_Sequence_" + this.codingNamesComboBox.getSelectedIndex();				
				if(this.localSequenceRadioButton.isSelected()){
					windowFrom = Integer.parseInt(this.windowFromTextField.getText());
					windowTo = Integer.parseInt(this.windowToTextField.getText());
					name += "_" + this.isPercentageCheckBox.isSelected() + "_" + windowFrom + "_" + windowTo;
				}
				this.model.add(new MetaFeature(name,'X',this.codingNamesComboBox.getSelectedIndex(),windowFrom,windowTo,
					this.localSequenceRadioButton.isSelected(),this.isPercentageCheckBox.isSelected()));
			}			
			JOptionPane.showMessageDialog(parent,"New Features Added!","Information",JOptionPane.INFORMATION_MESSAGE);			
		}
	}

}
