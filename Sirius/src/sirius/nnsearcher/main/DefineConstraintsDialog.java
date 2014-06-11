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
package sirius.nnsearcher.main;


import java.awt.Dialog;

import javax.swing.*;

import sirius.trainer.features.Feature;

import java.awt.*;
import java.awt.event.*;

public class DefineConstraintsDialog extends JDialog implements ActionListener{
	static final long serialVersionUID = sirius.Sirius.version;
	
	private JComboBox operatorComboBox;
	private JTextField valueTextField;
	private JButton addConstraintButton;
	private JButton cancelButton;
	private MustHaveTableModel model;
	private Feature feature;
	
	public DefineConstraintsDialog(Feature feature, MustHaveTableModel model){
		this.model = model;
		this.feature = feature;
		
		setTitle("Add Constraint");
    	setModalityType(Dialog.ModalityType.APPLICATION_MODAL);    	
    	setLayout(new GridLayout(2,1));
    	
    	JPanel centerPanel = new JPanel();
    	JLabel featureLabel = new JLabel("Feature: ");
    	JTextField featureTextField = new JTextField(feature.getDetails(), 35);
    	featureTextField.setEditable(false);
    	this.operatorComboBox = new JComboBox();
    	this.operatorComboBox.addItem(">=");
    	this.operatorComboBox.addItem(">");
    	this.operatorComboBox.addItem("==");
    	this.operatorComboBox.addItem("!=");
    	this.operatorComboBox.addItem("<=");
    	this.operatorComboBox.addItem("<");    
    	this.valueTextField = new JTextField(5);
    	
    	centerPanel.add(featureLabel);
    	centerPanel.add(featureTextField);
    	centerPanel.add(this.operatorComboBox);
    	centerPanel.add(this.valueTextField);
    	
    	JPanel southPanel = new JPanel();
    	this.addConstraintButton = new JButton("Add Constraint");
    	this.addConstraintButton.addActionListener(this);
    	this.cancelButton = new JButton("Cancel");
    	this.cancelButton.addActionListener(this);
    	
    	southPanel.add(this.addConstraintButton);
    	southPanel.add(this.cancelButton);
    	    	
    	add(centerPanel);
    	add(southPanel);
    	//setSize(730,140);
    	this.pack();
	}

	
	public void actionPerformed(ActionEvent ae) {		
		if(ae.getSource().equals(this.addConstraintButton)){
			try{
				double value = Double.parseDouble(this.valueTextField.getText());
				model.add(feature, this.operatorComboBox.getSelectedIndex(), value);
				this.dispose();
			}catch(NumberFormatException e){
				JOptionPane.showMessageDialog(null,"Please enter valid number into textfield","ERROR",JOptionPane.ERROR_MESSAGE);					
				this.valueTextField.requestFocusInWindow();
			}
		}else if(ae.getSource().equals(this.cancelButton)){
			this.dispose();		
		}
	}
}
