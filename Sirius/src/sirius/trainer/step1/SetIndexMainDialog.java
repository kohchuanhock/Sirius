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
package sirius.trainer.step1;

import javax.swing.*;

import sirius.main.ApplicationData;

import java.awt.*;
import java.awt.event.*;
import java.io.*;


public class SetIndexMainDialog extends JDialog implements ActionListener{
	static final long serialVersionUID = sirius.Sirius.version;
	
	private JButton okButton;
	private JButton cancelButton;
	private String absolutePath;
	private SetIndexTableModel setIndexTableModel;
	private Step1TableModel model;
	private JButton setIndexForAllButton;
	private ApplicationData applicationData;
	private boolean byAddButton;
	private int index;
	
    public SetIndexMainDialog(BufferedReader in,String absolutePath,Step1TableModel model,
    	ApplicationData applicationData,boolean showErrorDialog,boolean byAddButton,int index) {
    	//setSize(800,600);
    	setTitle("Set +1 Index");
    	setLayout(new BorderLayout());
    	setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
    	
    	this.absolutePath = absolutePath;
    	this.model = model;
    	this.applicationData = applicationData;
    	this.byAddButton = byAddButton;
    	this.index = index;    	
    	
    	JPanel northPanel = new JPanel(new BorderLayout());
    	northPanel.setBorder(BorderFactory.createCompoundBorder(
    		BorderFactory.createEmptyBorder(0,5,5,5),
    		BorderFactory.createTitledBorder(
    			"File Location")));
    	JLabel pathLabel = new JLabel(" Path:  ");
    	JTextField pathTextField = new JTextField(absolutePath);
    	pathTextField.setEnabled(false);    	
    	northPanel.add(pathLabel,BorderLayout.WEST);
    	northPanel.add(pathTextField,BorderLayout.CENTER);    	
    	add(northPanel,BorderLayout.NORTH);
    	
    	JPanel centerPanel = new JPanel(new BorderLayout());
    	centerPanel.setBorder(BorderFactory.createCompoundBorder(
    		BorderFactory.createEmptyBorder(0,5,5,5),
    		BorderFactory.createTitledBorder(
    			"File Details")));
    	setIndexTableModel = new SetIndexTableModel(this,in);
    	JTable setIndexTable = new JTable(setIndexTableModel);
    	setIndexTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	setIndexTable.getColumnModel().getColumn(0).setMinWidth(50);
        setIndexTable.getColumnModel().getColumn(0).setMaxWidth(50);
        //setIndexTable.getColumnModel().getColumn(1).setMaxWidth(550);
        setIndexTable.getColumnModel().getColumn(2).setMinWidth(50);
        setIndexTable.getColumnModel().getColumn(2).setMaxWidth(50);
        setIndexTable.getColumnModel().getColumn(3).setMinWidth(80);
        setIndexTable.getColumnModel().getColumn(3).setMaxWidth(80); 
    	JScrollPane setIndexTableScrollPane = new JScrollPane(setIndexTable);
    	centerPanel.add(setIndexTableScrollPane,BorderLayout.CENTER);
    	JPanel setIndexForAllPanel = new JPanel();
    	setIndexForAllButton = new JButton("Set Same +1 Index For All");
    	setIndexForAllButton.addActionListener(this);
    	setIndexForAllPanel.add(setIndexForAllButton);
    	setIndexForAllPanel.setBorder(BorderFactory.createEmptyBorder(0,5,5,5));
    	centerPanel.add(setIndexForAllPanel,BorderLayout.NORTH);
    	add(centerPanel,BorderLayout.CENTER);
    	
    	JPanel southPanel = new JPanel();
    	okButton = new JButton("OK");
    	okButton.addActionListener(this);
    	cancelButton = new JButton("Cancel");
    	cancelButton.addActionListener(this);
    	southPanel.add(okButton);
    	southPanel.add(cancelButton);
    	add(southPanel,BorderLayout.SOUTH);
    	
    	if(showErrorDialog){
    		JOptionPane.showMessageDialog(this,"Sequences +1_Index should either be all -1 or all non -1","Error",
    			JOptionPane.ERROR_MESSAGE);
    	}
    	this.pack();
    }        
    
    public void actionPerformed(ActionEvent ae){
    	if(ae.getSource().equals(okButton)){
    		if(setIndexTableModel.checkLocationIndexConsistency(this.applicationData)){
    			//update file
    			setIndexTableModel.writeData(this.absolutePath,this.model,this.byAddButton,this.index);
    			dispose();
    		}    			
    	}	
    	else if(ae.getSource().equals(cancelButton)){    	
    		dispose();    		
    	}
    	else if(ae.getSource().equals(setIndexForAllButton)){
    		SetIndexDialog dialog = new SetIndexDialog(this,this.setIndexTableModel);    		
    		dialog.setLocationRelativeTo(this);    		  		
    		dialog.setVisible(true);   
    	}
    } 
}