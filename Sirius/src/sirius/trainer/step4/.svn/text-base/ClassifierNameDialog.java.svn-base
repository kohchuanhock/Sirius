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
package sirius.trainer.step4;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import sirius.main.ApplicationData;

import java.io.*;


public class ClassifierNameDialog extends JDialog implements ActionListener{
	static final long serialVersionUID = sirius.Sirius.version;
	
	private JButton okButton;
	private JButton cancelButton;
	
	private int classifierNum;
	private ApplicationData applicationData;
	
	private JDialog currentDialog;
	
	private JTextField classifierNameTextField;
	
	public ClassifierNameDialog(JFrame mainFrame,String title,int classifierNum,
		ApplicationData applicationData){
		super(mainFrame,title);
		
		this.classifierNum = classifierNum;
		this.applicationData = applicationData;
		this.currentDialog = this;
		
		setLayout(new GridLayout(2,1));
		//setSize(300,100);		
			
		JLabel classifierNameLabel = new JLabel("Classifier Name: ");
		classifierNameTextField = new JTextField(10);
		JPanel classifierNamePanel = new JPanel();
		classifierNamePanel.add(classifierNameLabel);
		classifierNamePanel.add(classifierNameTextField);
		add(classifierNamePanel);
		
		okButton = new JButton("OK");
		cancelButton = new JButton("Cancel");
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();				
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		add(buttonPanel);
		this.pack();
	}
	public void actionPerformed(ActionEvent ae){
		if(ae.getSource().equals(okButton)){
			if (applicationData.getOneThread() == null){
    			applicationData.setOneThread(new Thread(){
    				public void run(){
    					if(classifierNum == 1){
    						try{    						
    							String outputFilename = applicationData.getWorkingDirectory() + File.separator + 
    								classifierNameTextField.getText() + ".classifierone"; 
	    						FileOutputStream fos1 = new FileOutputStream(outputFilename);
						        ObjectOutputStream oos1 = new ObjectOutputStream(fos1);
						        if(applicationData.isLocationIndexMinusOne)
						        	oos1.writeInt(3);
						        else
						        	oos1.writeInt(1);
						        oos1.writeObject(classifierNameTextField.getText());
								oos1.writeObject(applicationData.getClassifierOneSettings());
						        oos1.writeObject(applicationData.getDataset1Instances());
						        oos1.writeObject(applicationData.getClassifierOne());		
						        //newly added for version 1.1
						        oos1.writeObject(applicationData.getSequenceType());
						        oos1.writeInt(applicationData.getScoringMatrixIndex());
						        oos1.writeInt(applicationData.getCountingStyleIndex());
								oos1.close();
	    						applicationData.getStatusPane().setText("Classifier One saved onto " + 
	    							outputFilename);
	    						currentDialog.dispose();
	    					}catch(IOException e){
	    						JOptionPane.showMessageDialog(currentDialog,
	    							"Error in saving Classifier One","Error",
	    							JOptionPane.ERROR_MESSAGE);
	    					}finally{
	    						applicationData.setOneThread(null);    						
	    					}    			
    					}
    					else if(classifierNum == 2){   
    						try{
    							String outputFilename = applicationData.getWorkingDirectory() + File.separator + 
    								classifierNameTextField.getText() + ".classifiertwo"; 
	    						FileOutputStream fos1 = new FileOutputStream(outputFilename);
						        ObjectOutputStream oos1 = new ObjectOutputStream(fos1);
						        oos1.writeInt(2);
						        //Classifier One Stuff
						        oos1.writeObject(classifierNameTextField.getText());
								oos1.writeObject(applicationData.getClassifierOneSettings());
						        oos1.writeObject(applicationData.getDataset1Instances());
						        oos1.writeObject(applicationData.getClassifierOne());
						        //newly added for version 1.1
						        oos1.writeObject(applicationData.getSequenceType());
						        oos1.writeInt(applicationData.getScoringMatrixIndex());
						        oos1.writeInt(applicationData.getCountingStyleIndex());
						        //Classifier Two stuff
						        oos1.writeInt(applicationData.getSetUpstream());
						        oos1.writeInt(applicationData.getSetDownstream());						        
						        oos1.writeObject(applicationData.getClassifierTwoSettings());
						        oos1.writeObject(applicationData.getDataset2Instances());
						        oos1.writeObject(applicationData.getClassifierTwo());						        
								oos1.close();
	    						applicationData.getStatusPane().setText("Classifier Two saved onto " + 
	    							outputFilename);
	    						currentDialog.dispose();
    						}catch(IOException e){
    							JOptionPane.showMessageDialog(currentDialog,
	    							"Error in saving Classifier One","Error",
	    							JOptionPane.ERROR_MESSAGE);
    						}finally{
    							applicationData.setOneThread(null);
    						}			
    					}
    				}
    			});
    			applicationData.getOneThread().setPriority(Thread.MIN_PRIORITY);
	     		applicationData.getOneThread().start();
    		}else{
    			JOptionPane.showMessageDialog(currentDialog,"IOThread is busy","Error",
    				JOptionPane.ERROR_MESSAGE);
    		}
		}else if(ae.getSource().equals(cancelButton)){
			//Do nothing
			this.dispose();
		}
	}
}