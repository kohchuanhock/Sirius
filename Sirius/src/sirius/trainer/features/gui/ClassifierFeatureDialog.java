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
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;


import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import sirius.predictor.main.ClassifierData;
import sirius.trainer.features.ClassifierFeature;
import sirius.trainer.main.SiriusSettings;
import sirius.trainer.step2.FeatureTableModel;

import weka.classifiers.Classifier;
import weka.core.Instances;

public class ClassifierFeatureDialog extends JDialog implements MouseListener, ActionListener{
	static final long serialVersionUID = sirius.Sirius.version;	
	
	JTextField classifierNameTextField;
	JTextField classifierLocationTextField;
	File classifierLocationFile;
	JButton addButton;
	JButton cancelButton;
	
	JInternalFrame parent;
	FeatureTableModel model;
	
	public ClassifierFeatureDialog(JInternalFrame parent,FeatureTableModel model){
		this.parent = parent;
		this.model = model;
		
		//setSize(480,200);
		setTitle("Classifier Features");
    	setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
    	setLayout(new BorderLayout());    	    	
    	
    	JPanel classifierNamePanel = new JPanel();
    	JLabel classifierNameLabel = new JLabel("    Classifier Name: ", SwingConstants.RIGHT);
    	classifierNameTextField = new JTextField(20);    	
    	classifierNamePanel.add(classifierNameLabel);
    	classifierNamePanel.add(this.classifierNameTextField);    	
    	
    	JPanel classifierLocationPanel = new JPanel();
    	JLabel classifierLocationLabel = new JLabel("Classifier Location: ", SwingConstants.RIGHT);
    	classifierLocationTextField = new JTextField(20);
    	this.classifierLocationTextField.setEnabled(false);    	
    	this.classifierLocationTextField.addMouseListener(this);
    	classifierLocationPanel.add(classifierLocationLabel);
    	classifierLocationPanel.add(this.classifierLocationTextField);    	
    	
    	JPanel buttonsPanel = new JPanel();
    	this.addButton = new JButton(" Add ");
    	this.addButton.addActionListener(this);
    	this.cancelButton = new JButton("Cancel");
    	this.cancelButton.addActionListener(this);
    	buttonsPanel.add(this.addButton);
    	buttonsPanel.add(this.cancelButton);
    	
    	JPanel centerPanel = new JPanel(new GridLayout(2,1));
    	centerPanel.add(classifierNamePanel);
    	centerPanel.add(classifierLocationPanel);
    	
    	add(centerPanel, BorderLayout.CENTER);    
    	add(buttonsPanel, BorderLayout.SOUTH);
    	this.pack();
	}
	
	private void loadClassifierLocation(){
		String lastFastaFileLocation = SiriusSettings.getInformation("LastClassifierLocation: ");    	
	    JFileChooser fc = new JFileChooser(lastFastaFileLocation);
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("Classifier Files", "classifierone");
		fc.setFileFilter(filter);	
		int returnVal = fc.showOpenDialog(this);		
		if(returnVal == JFileChooser.APPROVE_OPTION) {	        
			this.classifierLocationFile = fc.getSelectedFile();
			SiriusSettings.updateInformation("LastClassifierLocation: ", this.classifierLocationFile.getAbsolutePath());
			this.classifierLocationTextField.setText(this.classifierLocationFile.getAbsolutePath());			
		}else{
			//status.setText("Open command is cancelled by user.");
		}
	}
	
	private void loadClassifier() throws IOException, ClassNotFoundException{
		FileInputStream fis = new FileInputStream(this.classifierLocationFile);
        ObjectInputStream ois = new ObjectInputStream(fis);
		
		int classifierNum = ois.readInt();		        					        		        
        String classifierName = (String) ois.readObject();
        String classifierOneSettings = (String) ois.readObject();		        		        
        Instances instances = (Instances) ois.readObject();
        Classifier classifierOne = (Classifier) ois.readObject();
        String sequenceType = (String) ois.readObject();		        
        int scoringMatrixIndex = ois.readInt();
        int countingStyleIndex = ois.readInt();
        int setUpstream = -1;
        int setDownstream = -1;
        String classifierTwoSettings = "";
        Instances instances2 = null;
        Classifier classifierTwo = null;
        //int scoringMatrixIndex = -1;
        //int countingStyleIndex = -1;
        if(classifierNum == 2){        	
        	setUpstream = ois.readInt();
        	setDownstream = ois.readInt();
        	classifierTwoSettings = (String) ois.readObject();
        	instances2 = (Instances) ois.readObject();
        	classifierTwo = (Classifier) ois.readObject();
        	ois.close();
        	throw new Error("Error: Not Supposed to be inside here as ClassifierTwo is not supported");
        }		        										
        ois.close();
        this.model.add(new ClassifierFeature("Z_" + this.classifierNameTextField.getText() , 
        	(new ClassifierData(classifierNum,classifierName,instances,
        	classifierOne,classifierTwo,classifierOneSettings,classifierTwoSettings,setUpstream,
        	setDownstream,instances2,sequenceType,scoringMatrixIndex,countingStyleIndex))));
        JOptionPane.showMessageDialog(parent,"New Features Added!","Information",JOptionPane.INFORMATION_MESSAGE);
	}

	
	public void mouseClicked(MouseEvent me) {	
		if(me.getSource().equals(this.classifierLocationTextField)){
			loadClassifierLocation();
		}
	}

	
	public void mouseEntered(MouseEvent arg0) {}

	
	public void mouseExited(MouseEvent arg0) {}

	
	public void mousePressed(MouseEvent arg0) {}

	
	public void mouseReleased(MouseEvent arg0) {}

	
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource().equals(this.addButton)){
			try{
				if(this.classifierNameTextField.getText().length() != 0 && this.classifierLocationTextField.getText().length() != 0){
					loadClassifier();
					dispose();
				}
			}catch(Exception e){e.printStackTrace();}			
		}else if(ae.getSource().equals(this.cancelButton)){
			dispose();
		}
	}

}
