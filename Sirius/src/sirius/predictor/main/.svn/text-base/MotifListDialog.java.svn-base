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

package sirius.predictor.main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MotifListDialog extends JFrame implements ActionListener{
	static final long serialVersionUID = sirius.Sirius.version;
	private JButton addButton;
	private JButton deleteButton;
	private JButton loadButton;
	private JButton saveButton;
	private JTable motifListTable;
	private MotifsTableModel motifListTableModel;	

    public MotifListDialog(MotifsTableModel motifListTableModel) {
    	this.motifListTableModel = motifListTableModel;    	
    	
    	setTitle("Motif List");
    	setLayout(new BorderLayout());
    	    	
    	motifListTable = new JTable(motifListTableModel);
    	motifListTable.getColumnModel().getColumn(0).setMinWidth(33);
    	motifListTable.getColumnModel().getColumn(0).setMaxWidth(33);
    	motifListTable.getColumnModel().getColumn(1).setMinWidth(20);
    	motifListTable.getColumnModel().getColumn(1).setMaxWidth(20);
    	motifListTable.getColumnModel().getColumn(2).setMinWidth(80);
        //motifListTable.getColumnModel().getColumn(2).setMaxWidth(80);
        motifListTable.getColumnModel().getColumn(3).setMinWidth(80);  
        motifListTable.getColumnModel().getColumn(3).setMaxWidth(80);  
    	JScrollPane motifListScrollPane = new JScrollPane(motifListTable);
    	add(motifListScrollPane,BorderLayout.CENTER);
    	
    	JPanel southPanel = new JPanel(new GridLayout(3,1));
    	JPanel addDeletePanel = new JPanel(new GridLayout(1,2));
		addButton = new JButton("Add Motif");
		deleteButton = new JButton("Delete Motif");
		addButton.addActionListener(this);
		deleteButton.addActionListener(this);
		addDeletePanel.add(addButton);
		addDeletePanel.add(deleteButton);
		
		loadButton = new JButton("Load Motif List");
		saveButton = new JButton("Save Motif List");
		loadButton.addActionListener(this);
		saveButton.addActionListener(this);
		
    	southPanel.add(addDeletePanel);
    	southPanel.add(loadButton);
    	southPanel.add(saveButton);
    	
    	add(southPanel,BorderLayout.SOUTH);
    	//setSize(270,250);
    	this.pack();
    }
    public void actionPerformed(ActionEvent ae){
    	if(ae.getSource().equals(addButton)){
    		AddMotifDialog dialog = new AddMotifDialog(motifListTableModel);
			dialog.setLocationRelativeTo(this);
			dialog.setVisible(true);
    	}else if(ae.getSource().equals(deleteButton)){
    		int selectedIndex = motifListTable.getSelectedRow();
    		if(selectedIndex != -1){
    			motifListTableModel.remove(selectedIndex);
    		}			    		
    		else{
    			JOptionPane.showMessageDialog(this,"Please choose a Motif to delete!",
    				"No Motif selected",JOptionPane.INFORMATION_MESSAGE); 
    		} 		
    	}else if(ae.getSource().equals(loadButton)){    		
    		motifListTableModel.loadData();    		
    		this.toFront();
    	}else if(ae.getSource().equals(saveButton)){
    		if(motifListTableModel.getSize() == 0)
    			JOptionPane.showMessageDialog(this,"No Motifs to save!",
        				"No Motif..",JOptionPane.INFORMATION_MESSAGE); 
    		else
    			motifListTableModel.saveData();
    		this.toFront();
    	}    	
    }
}

class AddMotifDialog extends JDialog implements ActionListener{
	static final long serialVersionUID = 23122007;
	
	private JButton okButton;
	private JTextField motifTextField;
	private JTextField mismatchTextField;
	private MotifsTableModel motifsTableModel;
	
	public AddMotifDialog(MotifsTableModel motifsTableModel){
		this.motifsTableModel = motifsTableModel;
		
		setTitle("Add Motif");
		
		setLayout(new BorderLayout());
		
		GridLayout centerGridLayout = new GridLayout(2,2);
		centerGridLayout.setHgap(5);
		centerGridLayout.setVgap(5);
		JPanel center = new JPanel(centerGridLayout);
   		center.setBorder(BorderFactory.createEmptyBorder(2,2,2,2)); 
		JLabel motifLabel = new JLabel("Motif: ",SwingConstants.RIGHT);
		motifTextField = new JTextField(5);
		JLabel mismatchLabel = new JLabel("#Mismatch: ",SwingConstants.RIGHT);
		mismatchTextField = new JTextField(3);
		
		center.add(motifLabel);
		center.add(motifTextField);
		center.add(mismatchLabel);
		center.add(mismatchTextField);
		add(center,BorderLayout.CENTER);
				
		okButton = new JButton("OK");
		okButton.addActionListener(this);		
		add(okButton,BorderLayout.SOUTH);	
		//setSize(200,120);
		this.pack();
	}
	public void actionPerformed(ActionEvent ae){
		try{
			if(ae.getSource().equals(okButton)){
				if(motifTextField.getText().length() == 0){
					JOptionPane.showMessageDialog(this,"Motif TextField is empty",
	    				"Error",JOptionPane.INFORMATION_MESSAGE); 
	    			return;
				}
				if(mismatchTextField.getText().length() == 0){
					JOptionPane.showMessageDialog(this,"Mismatch TextField is empty",
	    				"Error",JOptionPane.INFORMATION_MESSAGE); 
	    			return;
				}
				//Skip this checking step. Allow any character in Motif. Let this be user responsibility to ensure correctness of motif.
				/*String motifString = motifTextField.getText();
				motifString = motifString.toUpperCase();				
				for(int x = 0; x < motifString.length(); x++){
					if(!checkValid(motifString.charAt(x))){
						JOptionPane.showMessageDialog(this,"Invalid character in Motif TextField\r\n" + 
							"Only A,C,T,G and X allowed!",
		    				"Error",JOptionPane.INFORMATION_MESSAGE); 
		    			return;
					}
				}*/
				int mismatchInt = Integer.parseInt(mismatchTextField.getText());
				motifsTableModel.add(motifTextField.getText(),mismatchInt);			
				dispose();
			}
		}catch(NumberFormatException e){
			JOptionPane.showMessageDialog(this,"Enter only numbers in Mismatch TextField",
    				"Error",JOptionPane.INFORMATION_MESSAGE);
		}		
	}
	/*private boolean checkValid(char check){
		boolean returnValue = false;
		switch(check){
			case 'A':
			case 'C':
			case 'T':
			case 'G':
			case 'X': returnValue = true; break;				
		}
		return returnValue;
	}*/
}