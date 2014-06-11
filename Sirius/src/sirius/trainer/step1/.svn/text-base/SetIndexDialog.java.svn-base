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
import java.awt.*;
import java.awt.event.*;

public class SetIndexDialog extends JDialog implements ActionListener{
	static final long serialVersionUID = sirius.Sirius.version;

	private JButton okButton;
	private JTextField indexField;
	private JDialog parent;
	private SetIndexTableModel model;
		
    public SetIndexDialog(JDialog parent,SetIndexTableModel model) {
    	setTitle("Set +1 Index");
    	setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
    	this.parent = parent;
    	this.model = model;    	
    	//setSize(490,120);
    	setLayout(new GridLayout(1,1));
    	JPanel main = new JPanel(new GridLayout(1,1));
    	main.setBorder(BorderFactory.createCompoundBorder(
    		BorderFactory.createEmptyBorder(3,3,3,3),
    		BorderFactory.createTitledBorder(
    			"Note: If Xth character in the sequence is the +1 location,set +1 index to (X-1).")));
    	
    	JPanel center = new JPanel();
    	JLabel indexLabel = new JLabel("+1 Index: ");
    	indexField = new JTextField(10);
    	okButton = new JButton("OK");   	
    	okButton.addActionListener(this);
    	center.add(indexLabel);
    	center.add(indexField);
    	center.add(okButton);
		main.add(center);	    	    	
    	add(main);
    	this.pack();
    }
    
    public void actionPerformed(ActionEvent ae){
    	if(ae.getSource().equals(okButton)){
    		try{
    			int index = Integer.parseInt(indexField.getText());
    			if(index < -1){
    				JOptionPane.showMessageDialog(parent,"Index cannot be < -1","Invalid Input",
    					JOptionPane.WARNING_MESSAGE);
    				indexField.requestFocusInWindow();
    			}
    			else{
    				//check that the +1_Index is not greater than any of the sequence length
    				int returnValue = model.validateLocationIndex(index);
    				if(returnValue == -1){    				
    					model.setLocationIndexForAll(index);
    					dispose();
    				}
    				else
    					JOptionPane.showMessageDialog(parent,
    						"+1_Index cannot be set. Out of range for sequence number " + (returnValue + 1),
    						"ERROR",JOptionPane.ERROR_MESSAGE);    					
    			}
    		}catch(NumberFormatException e){
    			JOptionPane.showMessageDialog(parent,"Enter only numbers please","Number Format Exception",
    				JOptionPane.WARNING_MESSAGE);
    			indexField.requestFocusInWindow();
    		}
    	}
    }
    
    public int returnValue(){
    	return -1;
    }
}