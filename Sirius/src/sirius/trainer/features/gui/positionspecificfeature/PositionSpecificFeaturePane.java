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
package sirius.trainer.features.gui.positionspecificfeature;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import sirius.main.ApplicationData;
import sirius.nnsearcher.main.DefineConstraintsDialog;
import sirius.nnsearcher.main.MustHaveTableModel;
import sirius.trainer.features.Feature;
import sirius.trainer.features.PositionSpecificFeature;
import sirius.trainer.step2.FeatureTableModel;
import sirius.trainer.step2.Physiochemical2;

public class PositionSpecificFeaturePane extends JComponent implements ActionListener, ItemListener{
	static final long serialVersionUID = sirius.Sirius.version;
	
	private JButton addFeatureButton;
	private JButton closeButton;
	
	private JDialog parent;
	
	private JTextField windowFromTextField;
	private JTextField windowToTextField;
	private JButton setButton;
	
	private JTable positionSpecificTable;
	private JTable characterTable;
	private PositionSpecificTableModel positionSpecificTableModel;
	private PositionSpecificTableModel characterTableModel;
	private JScrollPane positionSpecificTableScrollPane;
	private JScrollPane characterTableScrollPane;
		
	private ApplicationData applicationData;
	private FeatureTableModel model;
	private int setPositionFrom;
	private int setPositionTo;
	private MustHaveTableModel constraintsModel;
	
	private JComboBox codingNamesComboBox = new JComboBox();
	private JPanel definitionsPanel;
	private JPanel mainPanel = new JPanel(new BorderLayout());
	
	public PositionSpecificFeaturePane(JDialog parent,FeatureTableModel model,ApplicationData applicationData,
			MustHaveTableModel constraintsModel){
		this.parent = parent;
		this.applicationData = applicationData;
		this.model = model;
		this.constraintsModel = constraintsModel;
		
		JLabel windowFromLabel = new JLabel("Position From: ");
		this.windowFromTextField = new JTextField(3);
		JLabel windowToLabel = new JLabel("To: ");
		this.windowToTextField = new JTextField(3);
		this.setButton = new JButton("Set");
		this.setButton.addActionListener(this);
		
		JPanel codingNamePanel = new JPanel();
		codingNamePanel.setBorder(BorderFactory.createTitledBorder("Coding Schemes"));		
		this.codingNamesComboBox.addItemListener(this);	
		for(int x = 0; x < Physiochemical2.codingNameList.length; x++)
			this.codingNamesComboBox.addItem(Physiochemical2.codingNameList[x]);		
		codingNamePanel.add(this.codingNamesComboBox);
		
		JPanel locationPanel = new JPanel();
		locationPanel.setBorder(BorderFactory.createTitledBorder("Location"));
		locationPanel.add(windowFromLabel);
		locationPanel.add(this.windowFromTextField);
		locationPanel.add(windowToLabel);
		locationPanel.add(this.windowToTextField);
		locationPanel.add(this.setButton);
		
		JPanel northPanel = new JPanel(new BorderLayout());
		if(applicationData.getSequenceType().indexOf("PROTEIN") != -1)
			northPanel.add(codingNamePanel,BorderLayout.WEST);
		northPanel.add(locationPanel,BorderLayout.CENTER);
					
		JPanel positionSpecificPanel = new JPanel(new GridLayout(1,1));		
		this.positionSpecificTableModel = new PositionSpecificTableModel(this.applicationData, true);
		this.positionSpecificTable = new JTable(this.positionSpecificTableModel);
		//this code allows the JTable to be sized according to me
		this.positionSpecificTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);		
		//this.positionSpecificTableModel.setTable(this.positionSpecificTable);
		PositionSpecificViewPort positionSpecificViewPort = new PositionSpecificViewPort();
		positionSpecificViewPort.setView(this.positionSpecificTable);
        this.positionSpecificTableScrollPane = new JScrollPane();        
        this.positionSpecificTableScrollPane.setViewport(positionSpecificViewPort);
    	positionSpecificPanel.add(this.positionSpecificTableScrollPane);
    	
    	JPanel characterPanel = new JPanel(new GridLayout(1,1));    	
    	this.characterTableModel = new PositionSpecificTableModel(this.applicationData, false);
		this.characterTable = new JTable(this.characterTableModel);		
		//this code allows the JTable to be sized according to me		
		this.characterTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);		
		//this.characterTableModel.setTable(this.characterTable);
		PositionSpecificViewPort characterViewPort = new PositionSpecificViewPort();
		characterViewPort.setView(this.characterTable);
        this.characterTableScrollPane = new JScrollPane();        
        this.characterTableScrollPane.setViewport(characterViewPort);        
        characterPanel.add(this.characterTableScrollPane);
    	
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.1;
        c.weighty = 1.0;
        c.gridheight = GridBagConstraints.REMAINDER; //end row;
        
    	JPanel centerPanel = new JPanel(gb);    	
    	centerPanel.add(characterPanel, c);
    	c.weightx = 1.0;
    	c.gridwidth = GridBagConstraints.REMAINDER; //end row
    	centerPanel.add(positionSpecificPanel, c);    	
    	
    	this.mainPanel.add(centerPanel,BorderLayout.CENTER);
		
		this.addFeatureButton = new JButton("Add This");
		this.closeButton = new JButton("Close");
		this.addFeatureButton.addActionListener(this);
		this.closeButton.addActionListener(this);
		
		JPanel southPanel = new JPanel();
		southPanel.add(this.addFeatureButton);
		southPanel.add(this.closeButton);
		
		setLayout(new BorderLayout());
		add(northPanel, BorderLayout.NORTH);
		add(this.mainPanel, BorderLayout.CENTER);		
		add(southPanel, BorderLayout.SOUTH);
	}
	
	//Run this function before add Column, add feature or add all permutation
	private boolean setPositionRange(){
		try{
			int positionFrom = Integer.parseInt(this.windowFromTextField.getText());
			int positionTo = Integer.parseInt(this.windowToTextField.getText());			
			if(positionTo < positionFrom){
				JOptionPane.showMessageDialog(parent,"Position To cannot be < Position From","ERROR",
						JOptionPane.ERROR_MESSAGE);
				this.windowFromTextField.requestFocusInWindow();
				return false;
			}else if(this.applicationData.isLocationIndexMinusOne == false){
				if(positionFrom == 0){
					JOptionPane.showMessageDialog(parent,"Position From cannot be zero since +1_Index is not -1","ERROR",
							JOptionPane.ERROR_MESSAGE);
					this.windowFromTextField.requestFocusInWindow();
					return false;
				}else if(positionTo == 0){
					JOptionPane.showMessageDialog(parent,"Position To cannot be zero since +1_Index is not -1","ERROR",
							JOptionPane.ERROR_MESSAGE);
					this.windowToTextField.requestFocusInWindow();
					return false;
				}					
			}else if(this.applicationData.isLocationIndexMinusOne){
				//+1_Index(-1)
				if(positionFrom < 0){
					JOptionPane.showMessageDialog(parent,"Position From cannot be < 0 since +1_Index is -1","ERROR",
							JOptionPane.ERROR_MESSAGE);
					this.windowFromTextField.requestFocusInWindow();
					return false;
				}			
			}
			return true;
		}catch(NumberFormatException e){
			JOptionPane.showMessageDialog(parent,"Please enter only integers for Position From and Position To","ERROR",
					JOptionPane.ERROR_MESSAGE);
			this.windowFromTextField.requestFocusInWindow();
			return false;
		}
	}
	
	public void actionPerformed(ActionEvent ae){		
		if(ae.getSource().equals(this.addFeatureButton)){
			if(setPositionRange()){
				int positionFrom = Integer.parseInt(this.windowFromTextField.getText());
				int positionTo = Integer.parseInt(this.windowToTextField.getText());
				if(positionFrom != this.setPositionFrom || positionTo != this.setPositionTo){
					JOptionPane.showMessageDialog(parent,"Position From and/or Position To inside TextField does not tally with Table","ERROR",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				String featureName = "P_" + positionFrom + "_" + positionTo;				
				for(int x = positionFrom, y = 0; x <= positionTo; x++){
					if(this.applicationData.isLocationIndexMinusOne == false && x == 0)
						continue;
					featureName += "_";
					featureName += this.positionSpecificTableModel.getSelectedString(y);					
					y++;
				}			
				featureName += "_" + this.codingNamesComboBox.getSelectedIndex();
				Feature tempData = new PositionSpecificFeature(featureName); 
				if(this.model != null){
					JOptionPane.showMessageDialog(parent,"New Features Added!","Information",JOptionPane.INFORMATION_MESSAGE);
					this.model.add(tempData);
				}else{
					//this is called by NNSearch add constraint button
					DefineConstraintsDialog dialog = new DefineConstraintsDialog(tempData, this.constraintsModel);
		    		dialog.setLocationRelativeTo(parent);
		    		dialog.setVisible(true);
				}
			}
		}else if(ae.getSource().equals(this.closeButton)){			
			parent.dispose();
		}else if(ae.getSource().equals(this.setButton)){		
			if(setPositionRange()){
				int positionFrom = Integer.parseInt(this.windowFromTextField.getText());
				int positionTo = Integer.parseInt(this.windowToTextField.getText());
				this.setPositionFrom = positionFrom;
				this.setPositionTo = positionTo;
				this.positionSpecificTableModel.setColumnName(this.applicationData.isLocationIndexMinusOne, positionFrom, positionTo);
			}
		}
	}

	@Override
	public void itemStateChanged(ItemEvent ie) {		
		if(ie.getSource().equals(this.codingNamesComboBox))
			if(this.characterTableModel != null && this.codingNamesComboBox.getSelectedIndex() != -1){
				this.characterTableModel.setP2(Physiochemical2.indexToName(this.codingNamesComboBox.getSelectedIndex()));
				this.positionSpecificTableModel.setP2(Physiochemical2.indexToName(this.codingNamesComboBox.getSelectedIndex()));
				if(this.definitionsPanel != null)
					this.mainPanel.remove(this.definitionsPanel);		
				if(this.codingNamesComboBox.getSelectedIndex() != 0){
					this.definitionsPanel = new JPanel(new GridLayout(4,2));
					this.definitionsPanel.setBorder(BorderFactory.createTitledBorder("Definitions"));
					Physiochemical2 p2 = new Physiochemical2((String) this.codingNamesComboBox.getSelectedItem());
					p2.setPanel(this.definitionsPanel);
					this.mainPanel.add(this.definitionsPanel, BorderLayout.SOUTH);
				}
			}
	}	
}