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
package sirius.trainer.features.gui.basicphysiochemical;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import sirius.main.ApplicationData;
import sirius.nnsearcher.main.MustHaveTableModel;
import sirius.trainer.step2.FeatureTableModel;

public class BasicPhysioChemicalFeaturePane extends JComponent implements ActionListener{
	static final long serialVersionUID = sirius.Sirius.version;
	private JButton okButton;
	private JButton cancelButton;
	private JButton markAllButton;
	private JButton unmarkAllButton;
	private JDialog parent;
	private BasicPhysioTableModel featureTableModel;
	private FeatureTableModel model;
	private JRadioButton globalRadioButton;
	private JRadioButton localRadioButton;
	private JTextField windowFrom;
	private JTextField windowTo;
	private ApplicationData applicationData;
	private MustHaveTableModel constraintsModel;
	private JCheckBox isPercentageCheckBox;
	
	public BasicPhysioChemicalFeaturePane(JDialog parent, FeatureTableModel model,ApplicationData applicationData, 
			MustHaveTableModel constraintsModel){
		this.parent = parent;
		this.model = model;
		this.applicationData = applicationData;
		this.constraintsModel = constraintsModel;
		
		featureTableModel = new BasicPhysioTableModel();
		JTable featureTable = new JTable(featureTableModel);
		JScrollPane featureTableScrollPane = new JScrollPane(featureTable);		
		featureTable.getColumnModel().getColumn(0).setMaxWidth(20);
        featureTable.getColumnModel().getColumn(1).setMinWidth(200);
        featureTable.getColumnModel().getColumn(1).setMaxWidth(300);
        featureTable.getColumnModel().getColumn(2).setMinWidth(300);
        featureTable.getColumnModel().getColumn(2).setMaxWidth(600);        
        featureTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
        
        
        JPanel buttonPanel = new JPanel();
        markAllButton = new JButton("Mark All");
        markAllButton.addActionListener(this);
        unmarkAllButton = new JButton("Unmark All");
        unmarkAllButton.addActionListener(this);
        okButton = new JButton("OK");
        okButton.addActionListener(this);
        cancelButton = new JButton("Close");
        cancelButton.addActionListener(this);
        buttonPanel.add(markAllButton);
        buttonPanel.add(unmarkAllButton);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        JPanel windowPanel = new JPanel();
        this.globalRadioButton = new JRadioButton("Global");
        this.globalRadioButton.setSelected(true);
        this.globalRadioButton.addActionListener(this);        
        this.localRadioButton = new JRadioButton("Local");
        this.localRadioButton.addActionListener(this);
        this.windowFrom = new JTextField(5);
        this.windowFrom.setEnabled(false);
        this.windowTo = new JTextField(5);
        this.windowTo.setEnabled(false);
        this.isPercentageCheckBox = new JCheckBox("%");
        JLabel windowFromLabel = new JLabel("From: ");
        JLabel windowToLabel = new JLabel("To: ");
        windowPanel.add(this.globalRadioButton);
        windowPanel.add(this.localRadioButton);
        windowPanel.add(windowFromLabel);
        windowPanel.add(this.windowFrom);
        windowPanel.add(windowToLabel);
        windowPanel.add(this.windowTo);
        windowPanel.add(this.isPercentageCheckBox);
        
        setLayout(new BorderLayout());
        add(windowPanel, BorderLayout.NORTH);
        add(featureTableScrollPane,BorderLayout.CENTER);
        add(buttonPanel,BorderLayout.SOUTH);
	}
	public void actionPerformed(ActionEvent ae){
		if(ae.getSource().equals(okButton)){			
			if(this.globalRadioButton.isSelected()){
				featureTableModel.generateFeatures(model, false, 0, 0, this.parent, this.constraintsModel,this.isPercentageCheckBox.isSelected());
				//parent.dispose();
				return;
			}
			int fromFieldInt = validateField(windowFrom,"From Field");
			int toFieldInt = validateField(windowTo,"To Field");			
			if(toFieldInt < fromFieldInt){
				JOptionPane.showMessageDialog(parent,"From field should not be smaller than To Field",
					"ERROR",JOptionPane.ERROR_MESSAGE);
				windowFrom.requestFocusInWindow();
				
			}									
			else if(fromFieldInt == 0 && applicationData.isLocationIndexMinusOne == false){
				JOptionPane.showMessageDialog(parent,"0 is not allowed, choose either -1 or +1.","ERROR",
					JOptionPane.ERROR_MESSAGE);
				windowFrom.requestFocusInWindow();
			}
			else if(fromFieldInt < 0 && applicationData.isLocationIndexMinusOne == true){
				JOptionPane.showMessageDialog(parent,"Cannot be < 0","ERROR",
					JOptionPane.ERROR_MESSAGE);
				windowFrom.requestFocusInWindow();
			}
			else if(toFieldInt == 0 && applicationData.isLocationIndexMinusOne == false){
				JOptionPane.showMessageDialog(parent,"0 is not allowed, choose either -1 or +1.","ERROR",
					JOptionPane.ERROR_MESSAGE);
				windowTo.requestFocusInWindow();
			}			
			else{				
				featureTableModel.generateFeatures(model, this.localRadioButton.isSelected(), fromFieldInt, toFieldInt, this.parent, constraintsModel
						,this.isPercentageCheckBox.isSelected());
				//parent.dispose();				
			}
		}else if(ae.getSource().equals(cancelButton)){
			parent.dispose();
		}else if(ae.getSource().equals(this.markAllButton)){
			featureTableModel.markAll();
		}else if(ae.getSource().equals(this.unmarkAllButton)){
			featureTableModel.unmarkAll();
		}else if(ae.getSource().equals(this.globalRadioButton)){
			this.localRadioButton.setSelected(false);
			this.windowFrom.setEnabled(false);
			this.windowTo.setEnabled(false);
		}else if(ae.getSource().equals(this.localRadioButton)){
			this.globalRadioButton.setSelected(false);
			this.windowFrom.setEnabled(true);
			this.windowTo.setEnabled(true);
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
}

