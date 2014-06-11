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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import sirius.main.ApplicationData;
import sirius.nnsearcher.main.DefineConstraintsDialog;
import sirius.nnsearcher.main.MustHaveTableModel;
import sirius.trainer.features.Basic2PhysiochemicalFeature;
import sirius.trainer.features.Feature;
import sirius.trainer.features.GenerateArff;
import sirius.trainer.features.gui.advancedphysiochemical.AdvancedPhysioChemicalFeatureComboBoxItem;
import sirius.trainer.step2.FeatureTableModel;


public class Basic2PhysioChemicalFeaturePane extends JComponent implements ActionListener, ItemListener{
	static final long serialVersionUID = sirius.Sirius.version;
	private JButton markAllButton;
	private JButton unmarkAllButton;
	private JButton okButton;
	private JButton cancelButton;
	private JDialog parent;
	private JComboBox featureTypeComboBox;	
	private JTextField lengthTextField;
	private JTextField percentageTextField;//The name for this textField is not that appropriate as I used absolute value instead of percentage
	private JTextField valueTextField;//This is a double value which the windowlength has to be greater than
	private ArrayList<AdvancedPhysioChemicalFeatureComboBoxItem> comboboxItemArrayList;
	
	private JCheckBox maxValueCheckBox;
	private JCheckBox numRegionGreaterThanXPercentageCheckBox;
	private JCheckBox numRegionGreaterThanYValueCheckBox;
	private JCheckBox pfamLocalCheckBox;
	private JCheckBox pfamGlobalCheckBox;
	private JCheckBox prositeCheckBox;
	
	private ArrayList<JCheckBox> checkBoxArrayList;
	private FeatureTableModel model;
	private MustHaveTableModel constraintsModel;
	
	public Basic2PhysioChemicalFeaturePane(JDialog parent, FeatureTableModel model,ApplicationData applicationData,
			MustHaveTableModel constraintsModel){
		this.parent = parent;
		this.model = model;
		this.constraintsModel = constraintsModel;
		
		JPanel buttonPanel = new JPanel();
		this.markAllButton = new JButton("Mark All");
		this.markAllButton.addActionListener(this);
		this.unmarkAllButton = new JButton("Unmark All");
		this.unmarkAllButton.addActionListener(this);
        okButton = new JButton("Add Feature");
        okButton.addActionListener(this);
        cancelButton = new JButton("Close");
        cancelButton.addActionListener(this);
        buttonPanel.add(this.markAllButton);
        buttonPanel.add(this.unmarkAllButton);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
    
        JLabel featureLabel = new JLabel("Feature: ");
        featureTypeComboBox = new JComboBox();        
        featureTypeComboBox.addItem("       ");
        featureTypeComboBox.addItemListener(this);        
        JLabel lengthLabel = new JLabel("Sliding Window Length: ");
        this.lengthTextField = new JTextField(3);
        
        JPanel northPanel = new JPanel();
        northPanel.add(featureLabel);
        northPanel.add(featureTypeComboBox);                
        northPanel.add(lengthLabel);
        northPanel.add(this.lengthTextField);
        
        this.maxValueCheckBox = new JCheckBox("Max Value");
        this.numRegionGreaterThanXPercentageCheckBox = new JCheckBox("# Region with > X number of feature type, ");
        this.percentageTextField = new JTextField(3);
        JLabel percentageLabel = new JLabel("X: ",SwingConstants.LEFT);
        this.numRegionGreaterThanYValueCheckBox = new JCheckBox("# Region with > Y value, ");
        this.valueTextField = new JTextField(3);
        JLabel valueLabel = new JLabel("Y: ", SwingConstants.LEFT);
        
        this.pfamLocalCheckBox = new JCheckBox("Pfam Local (Options (--cut_tc) and Whole Sequence)");
        this.pfamGlobalCheckBox = new JCheckBox("Pfam Global (Options (--cut_tc) and Whole Sequence)");
        this.prositeCheckBox = new JCheckBox("Prosite (Options (-s) and Whole Sequence)");
        
    	this.checkBoxArrayList = new ArrayList<JCheckBox>();
    	this.checkBoxArrayList.add(this.maxValueCheckBox);
    	this.checkBoxArrayList.add(this.numRegionGreaterThanXPercentageCheckBox);
    	this.checkBoxArrayList.add(this.numRegionGreaterThanYValueCheckBox);    	
    	
    	JPanel centerPanel = new JPanel(new GridLayout(10,1));    	
    	JPanel percentagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	percentagePanel.add(this.numRegionGreaterThanXPercentageCheckBox);
    	percentagePanel.add(percentageLabel);
    	percentagePanel.add(this.percentageTextField);
    	JPanel maxValuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	maxValuePanel.add(this.maxValueCheckBox);
    	JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	valuePanel.add(this.numRegionGreaterThanYValueCheckBox);
    	valuePanel.add(valueLabel);
    	valuePanel.add(this.valueTextField);    	
    	
    	centerPanel.add(maxValuePanel);  
    	centerPanel.add(percentagePanel);
    	centerPanel.add(valuePanel);
    	
    	//Temporarily take out PFam & Prosite because these are external programs and hence would be more troublesome to distribute without problems
    	/*this.checkBoxArrayList.add(this.pfamLocalCheckBox);
    	this.checkBoxArrayList.add(this.pfamGlobalCheckBox);
    	this.checkBoxArrayList.add(this.prositeCheckBox);*/
    	/*JPanel prositeLocalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	prositeLocalPanel.add(this.pfamLocalCheckBox);
    	JPanel prositeGlobalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	prositeGlobalPanel.add(this.pfamGlobalCheckBox);
    	JPanel pfamPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	pfamPanel.add(this.prositeCheckBox);*/
    	/*centerPanel.add(prositeLocalPanel);
    	centerPanel.add(prositeGlobalPanel);
    	centerPanel.add(pfamPanel);*/
    	        
        setLayout(new BorderLayout());      
        add(northPanel,BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel,BorderLayout.SOUTH);
        
        this.comboboxItemArrayList = new ArrayList<AdvancedPhysioChemicalFeatureComboBoxItem>();
		this.comboboxItemArrayList.add(new AdvancedPhysioChemicalFeatureComboBoxItem("Hydrophobic", GenerateArff.aminoAcidHydrophobicity, 1.0, 1,false));
		this.comboboxItemArrayList.add(new AdvancedPhysioChemicalFeatureComboBoxItem("Hydrophilic", GenerateArff.aminoAcidHydrophobicity_neg, 1.0, 1,true));
		this.comboboxItemArrayList.add(new AdvancedPhysioChemicalFeatureComboBoxItem("Alkaline", GenerateArff.aminoAcidPKa_wrt7_pos, 0.0, 0, false));
		this.comboboxItemArrayList.add(new AdvancedPhysioChemicalFeatureComboBoxItem("Acidic", GenerateArff.aminoAcidPKa_wrt7_neg, 0.0, 0, true));
		this.comboboxItemArrayList.add(new AdvancedPhysioChemicalFeatureComboBoxItem("OrderAA", GenerateArff.orderAminoAcid, 10.0, 10, false));
		this.comboboxItemArrayList.add(new AdvancedPhysioChemicalFeatureComboBoxItem("DisorderAA", GenerateArff.disorderAminoAcid, 10.0, 10, false));		
		this.comboboxItemArrayList.add(new AdvancedPhysioChemicalFeatureComboBoxItem("NetCharge(+ve)", GenerateArff.aminoAcidCharge, 2.0, 2, false));
		this.comboboxItemArrayList.add(new AdvancedPhysioChemicalFeatureComboBoxItem("NetCharge(-ve)", GenerateArff.aminoAcidCharge_neg, 2.0, 2, true));
		addItemsIntoComboBox(this.featureTypeComboBox);
	}
	
	private void addItemsIntoComboBox(JComboBox comboBox){		
		for(int x = 0; x < this.comboboxItemArrayList.size(); x++){
			comboBox.addItem(this.comboboxItemArrayList.get(x).getItemName());
		}
	}	
	
	private boolean validateInput(){
		try{
			Integer.parseInt(this.lengthTextField.getText());
		}catch(NumberFormatException ex){
			JOptionPane.showMessageDialog(null,"Integers only for Sliding Window Length","Invalid Input",JOptionPane.ERROR_MESSAGE);
			this.lengthTextField.requestFocusInWindow();
			return false;
		}
		if(this.checkBoxArrayList.get(1).isSelected()){
			try{
				Integer.parseInt(this.percentageTextField.getText());
			}catch(NumberFormatException ex){
				JOptionPane.showMessageDialog(null,"Integers only for X","Invalid Input",JOptionPane.ERROR_MESSAGE);
				this.percentageTextField.requestFocusInWindow();
				return false;
			}
		}
		if(this.checkBoxArrayList.get(2).isSelected()){
			try{
				Double.parseDouble(this.valueTextField.getText());
			}catch(NumberFormatException ex){
				JOptionPane.showMessageDialog(null,"Integers only for Y","Invalid Input",JOptionPane.ERROR_MESSAGE);
				this.valueTextField.requestFocusInWindow();
				return false;
			}
		}		
		return true;
	}
	
	public void actionPerformed(ActionEvent ae){
		if(ae.getSource().equals(okButton) && ((this.maxValueCheckBox.isSelected() == false && 
				this.numRegionGreaterThanXPercentageCheckBox.isSelected() == false && 
				this.numRegionGreaterThanYValueCheckBox.isSelected() == false) || validateInput())){					
			if(this.featureTypeComboBox.getSelectedIndex() != 0){
				int index = this.featureTypeComboBox.getSelectedIndex() - 1;
				String name = "C_" + this.comboboxItemArrayList.get(index).getItemName() + "_";				
				for(int x = 0; x < this.checkBoxArrayList.size(); x++){
					if(this.checkBoxArrayList.get(x).isSelected()){
						String featureName = name;
						String featureDetail = "";
						switch(x){
						case 0: featureName += "MaxValue_0_"; featureDetail = this.comboboxItemArrayList.get(index).getItemName() + 
							", Max Value, Window(" + this.lengthTextField.getText() +  ")"; break;
						case 1: featureName += "NumRegionGreaterThanX_" + this.percentageTextField.getText() + "_"; 
							featureDetail = "# Region with > " + this.percentageTextField.getText() + " number of " + 
								this.comboboxItemArrayList.get(index).getItemName() + " Amino Acid, Window(" + 
								this.lengthTextField.getText() + ")"; break;	
						case 2: featureName += "NumRegionGreaterThanY_" + this.valueTextField.getText() + "_"; 
							featureDetail = "# Region with > " + this.valueTextField.getText() + " value of " + 
							this.comboboxItemArrayList.get(index).getItemName() + " Amino Acid, Window(" + 
							this.lengthTextField.getText() + ")"; break;	
						}						
						featureName += this.lengthTextField.getText();
						Feature tempData = new Basic2PhysiochemicalFeature('C', featureName, featureDetail, false);
						if(this.model != null){
							this.model.add(tempData);							
						}
						else{
							DefineConstraintsDialog dialog = new DefineConstraintsDialog(tempData, this.constraintsModel);
				    		dialog.setLocationRelativeTo(parent);
				    		dialog.setVisible(true);
						}
					}					
				}
				JOptionPane.showMessageDialog(null,"Successfully Added New Features","Added New Feature",JOptionPane.INFORMATION_MESSAGE);
			}
			boolean added = false;
			if(this.pfamGlobalCheckBox.isSelected() == true){
				added = true;
			}
			if(this.pfamLocalCheckBox.isSelected() == true){
				added = true;
			}
			if(this.prositeCheckBox.isSelected() == true){
				added = true;
			}
			if(added == true){
				JOptionPane.showMessageDialog(null,"Successfully Added New Features","Added New Feature",JOptionPane.INFORMATION_MESSAGE);
			}
		}else if(ae.getSource().equals(cancelButton)){
			parent.dispose();
		}else if(ae.getSource().equals(this.markAllButton)){
			for(int x = 0; x < this.checkBoxArrayList.size(); x++){
				this.checkBoxArrayList.get(x).setSelected(true);
			}
		}else if(ae.getSource().equals(this.unmarkAllButton)){
			for(int x = 0; x < this.checkBoxArrayList.size(); x++){
				this.checkBoxArrayList.get(x).setSelected(false);
			}
		}
	}
	
	public void itemStateChanged(ItemEvent e){
		if(e.getSource().equals(this.featureTypeComboBox)){
			if(e.getStateChange() == ItemEvent.SELECTED){																
				if(this.featureTypeComboBox.getSelectedIndex() != 0){
					//Do nothing here 
					//Then why have it here?
					//Because I copy and paste the whole class from advancedPhysiochemical 
				}
			}
		}
	}
}