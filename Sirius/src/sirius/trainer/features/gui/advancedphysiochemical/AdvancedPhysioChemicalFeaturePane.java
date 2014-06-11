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
package sirius.trainer.features.gui.advancedphysiochemical;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.util.ArrayList;

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
import sirius.trainer.features.AdvancedPhysiochemicalFeature;
import sirius.trainer.features.Feature;
import sirius.trainer.features.GenerateArff;
import sirius.trainer.step2.FeatureTableModel;

public class AdvancedPhysioChemicalFeaturePane extends JComponent implements ActionListener, ItemListener{
	static final long serialVersionUID = sirius.Sirius.version;
	private JButton markAllButton;
	private JButton unmarkAllButton;
	private JButton okButton;
	private JButton cancelButton;
	private JDialog parent;
	private JComboBox featureTypeComboBox;
	private JTextField valueCutoffTextField;
	private JTextField lengthCutoffTextField;
	private ArrayList<AdvancedPhysioChemicalFeatureComboBoxItem> comboboxItemArrayList;
	
	private JCheckBox numRegionGtCutoffCheckBox;
	
	private JCheckBox topRegionValueCheckBox;
	private JCheckBox topRegionLocationAbsoluteCheckBox;
	private JCheckBox topRegionLocationRelativeCheckBox;
	private JCheckBox topRegionSizeAbsoluteCheckBox;
	private JCheckBox topRegionSizeRelativeCheckBox;
	
	private JCheckBox totalValueOfRegionGtCutoffCheckBox;
	private JCheckBox totalSizeOfRegionGtCutoffAbsoluteCheckBox;
	private JCheckBox totalSizeOfRegionGtCutoffRelativeCheckBox;
	
	private JCheckBox averageValueOfRegionGtCutoffCheckBox;
	private JCheckBox averageSizeOfRegionGtCutoffAbsoluteCheckBox;
	private JCheckBox averageSizeOfRegionGtCutoffRelativeCheckBox;
	
	private ArrayList<JCheckBox> checkBoxArrayList;
	private FeatureTableModel model;
	private MustHaveTableModel constraintsModel;
	
	public AdvancedPhysioChemicalFeaturePane(JDialog parent, FeatureTableModel model,ApplicationData applicationData,
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
        JLabel valueCutoffLabel = new JLabel("Value Cutoff: ");        
        this.valueCutoffTextField = new JTextField(3);
        JLabel lengthCutoffLabel = new JLabel("Length Cutoff: ");
        this.lengthCutoffTextField = new JTextField(3);
        
        JPanel northPanel = new JPanel();
        northPanel.add(featureLabel);
        northPanel.add(featureTypeComboBox);
        northPanel.add(valueCutoffLabel);
        northPanel.add(this.valueCutoffTextField);
        northPanel.add(lengthCutoffLabel);
        northPanel.add(this.lengthCutoffTextField);
        
    	numRegionGtCutoffCheckBox = new JCheckBox("# Region > Cutoff", false);    	
    	topRegionValueCheckBox = new JCheckBox("Top Region Value", true);
    	topRegionLocationAbsoluteCheckBox = new JCheckBox("Top Region Location (Absolute)", false);
    	topRegionLocationRelativeCheckBox = new JCheckBox("Top Region Location (Relative)", true);
    	topRegionSizeAbsoluteCheckBox = new JCheckBox("Top Region Size (Absolute)", false);
    	topRegionSizeRelativeCheckBox = new JCheckBox("Top Region Size (Relative)", true);
    	
    	totalValueOfRegionGtCutoffCheckBox = new JCheckBox("Total Value of Region > Cutoff", false);
    	totalSizeOfRegionGtCutoffAbsoluteCheckBox = new JCheckBox("Total Size of Region > Cutoff (Absolute)", false);
    	totalSizeOfRegionGtCutoffRelativeCheckBox = new JCheckBox("Total Size of Region > Cutoff (Relative)", false);
    	
    	averageValueOfRegionGtCutoffCheckBox = new JCheckBox("Average Value of Region > Cutoff", false);
    	averageSizeOfRegionGtCutoffAbsoluteCheckBox = new JCheckBox("Average Size of Region > Cutoff (Absolute)", false);
    	averageSizeOfRegionGtCutoffRelativeCheckBox = new JCheckBox("Average Size of Region > Cutoff (Relative)", false);
        
    	this.checkBoxArrayList = new ArrayList<JCheckBox>();
    	this.checkBoxArrayList.add(this.numRegionGtCutoffCheckBox);
    	this.checkBoxArrayList.add(this.totalValueOfRegionGtCutoffCheckBox);
    	this.checkBoxArrayList.add(this.topRegionValueCheckBox);
    	this.checkBoxArrayList.add(this.totalSizeOfRegionGtCutoffAbsoluteCheckBox);
    	this.checkBoxArrayList.add(this.topRegionLocationAbsoluteCheckBox);
    	this.checkBoxArrayList.add(this.totalSizeOfRegionGtCutoffRelativeCheckBox);
    	this.checkBoxArrayList.add(this.topRegionLocationRelativeCheckBox);
    	this.checkBoxArrayList.add(this.averageValueOfRegionGtCutoffCheckBox);
    	this.checkBoxArrayList.add(this.topRegionSizeAbsoluteCheckBox);
    	this.checkBoxArrayList.add(this.averageSizeOfRegionGtCutoffAbsoluteCheckBox);
    	this.checkBoxArrayList.add(this.topRegionSizeRelativeCheckBox);    	
    	this.checkBoxArrayList.add(this.averageSizeOfRegionGtCutoffRelativeCheckBox);    	    	    	       	    	
    	
    	JPanel centerPanel = new JPanel(new GridLayout(6,2));
    	addCheckBoxToPanel(centerPanel);    	
        
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
		this.comboboxItemArrayList.add(new AdvancedPhysioChemicalFeatureComboBoxItem("O-DisorderAA", GenerateArff.orderDifferenceAminoAcid, 3.0, 3, false));
		this.comboboxItemArrayList.add(new AdvancedPhysioChemicalFeatureComboBoxItem("D-OrderAA", GenerateArff.disorderDifferenceAminoAcid, 3.0, 3, false));
		this.comboboxItemArrayList.add(new AdvancedPhysioChemicalFeatureComboBoxItem("NetCharge(+ve)", GenerateArff.aminoAcidCharge, 2.0, 2, false));
		this.comboboxItemArrayList.add(new AdvancedPhysioChemicalFeatureComboBoxItem("NetCharge(-ve)", GenerateArff.aminoAcidCharge_neg, 2.0, 2, true));
		addItemsIntoComboBox(this.featureTypeComboBox);
	}
	
	private void addItemsIntoComboBox(JComboBox comboBox){		
		for(int x = 0; x < this.comboboxItemArrayList.size(); x++){
			comboBox.addItem(this.comboboxItemArrayList.get(x).getItemName());
		}
	}
	
	private void addCheckBoxToPanel(JPanel centerPanel){
		for(int x = 0; x < this.checkBoxArrayList.size(); x++){
			centerPanel.add(this.checkBoxArrayList.get(x));
		}
	}
	
	public void actionPerformed(ActionEvent ae){
		if(ae.getSource().equals(okButton)){		
			if(this.featureTypeComboBox.getSelectedIndex() != 0){
				int index = this.featureTypeComboBox.getSelectedIndex() - 1;
				String name = "A_" + this.comboboxItemArrayList.get(index).getItemName() + "_";				
				for(int x = 0; x < this.checkBoxArrayList.size(); x++){
					if(this.checkBoxArrayList.get(x).isSelected()){
						String featureName = name;
						String featureDetail = "";
						switch(x){
						case 0: featureName += "NumOfRegion"; featureDetail = "Number of regions > cutoff"; break;
						case 1: featureName += "TotalValue"; featureDetail = "Total value for all regions > cutoff"; break;
						case 2: featureName += "TopValue"; featureDetail = "Value of top region > cutoff"; break;
						case 3: featureName += "TotalSizeAbsolute"; featureDetail = "Total size of all regions > cutoff (Absolute)"; break;
						case 4: featureName += "TopLocationAbsolute"; featureDetail = "Location of top region > cutoff (Absolute)"; break;
						case 5: featureName += "TotalSizeRelative"; featureDetail = "Total size of all regions > cutoff (Relative)"; break;
						case 6: featureName += "TopLocationRelative"; featureDetail = "Location of top region > cutoff (Relative)"; break;
						case 7: featureName += "AverageValue"; featureDetail = "Average value of all regions > cutoff"; break;
						case 8: featureName += "TopSizeAbsolute"; featureDetail = "Size of top region > cutoff (Absolute)"; break;
						case 9: featureName += "AverageSizeAbsolute"; featureDetail = "Average size of all regions > cutoff (Absolute)"; break;
						case 10: featureName += "TopSizeRelative"; featureDetail = "Size of top region > cutoff (Relative)"; break;
						case 11: featureName += "AverageSizeRelative"; featureDetail = "Average size of all regions > cutoff (Relative)"; break;						
						}
						featureName += "_" + this.valueCutoffTextField.getText();
						featureName += "_" + this.lengthCutoffTextField.getText();							
						featureDetail += " " + this.valueCutoffTextField.getText();
						featureDetail += " " + this.lengthCutoffTextField.getText();
						Feature tempData = new AdvancedPhysiochemicalFeature('A', featureName, featureDetail, false, Double.parseDouble(this.valueCutoffTextField.getText()),Integer.parseInt(this.lengthCutoffTextField.getText()));
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
				this.valueCutoffTextField.setText("");
				this.lengthCutoffTextField.setText("");
				if(this.featureTypeComboBox.getSelectedIndex() != 0){
					int index = this.featureTypeComboBox.getSelectedIndex() - 1;
					DecimalFormat df = new DecimalFormat("0.##");
					this.valueCutoffTextField.setText(df.format(this.comboboxItemArrayList.get(index).getDefaultValueCutoff()));
					this.lengthCutoffTextField.setText(df.format(this.comboboxItemArrayList.get(index).getDefaultLengthCutoff()));
				}
			}
		}
	}
}