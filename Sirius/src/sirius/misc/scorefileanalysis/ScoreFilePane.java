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
package sirius.misc.scorefileanalysis;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import sirius.trainer.main.SiriusSettings;
import sirius.utils.ClassifierResults;
import sirius.utils.PredictionStats;

public class ScoreFilePane extends JComponent implements ActionListener{
	static final long serialVersionUID = sirius.Sirius.version;
	JTextArea outputTextArea;
	JTextField rangeTextField;
	JTextField thresholdTextField;
	JButton loadScoreFileButton;
	JButton computeButton;
	JInternalFrame parent;
	String loadingFilename;
	JLabel scoreFileLocationLabel;
	
	public ScoreFilePane(JInternalFrame parent,JTabbedPane tabbedPane){
		this.parent = parent;
		
        //Output TextArea
    	outputTextArea = new JTextArea();
    	outputTextArea.setEditable(false);    	
    	JScrollPane outputScrollPane = new JScrollPane(outputTextArea);
    	JPanel outputPanel = new JPanel(new BorderLayout());
    	outputPanel.add(outputScrollPane,BorderLayout.CENTER);
    	
    	JPanel outputNorthPanel = new JPanel();
    	JLabel rangeLabel = new JLabel("Range:  ");
    	rangeTextField = new JTextField("0", 10);
    	JLabel thresholdLabel = new JLabel("     Threshold:  ");
    	thresholdTextField = new JTextField("0.5", 10);
    	JLabel computeButtonLabel = new JLabel("     ");
    	computeButton = new JButton("Compute");
    	computeButton.addActionListener(this);
    	loadScoreFileButton = new JButton("Load Score File");
    	loadScoreFileButton.addActionListener(this);
    	JLabel loadScoreButtonLabel = new JLabel("     ");
    	outputNorthPanel.add(rangeLabel);
    	outputNorthPanel.add(rangeTextField);
    	outputNorthPanel.add(thresholdLabel);
    	outputNorthPanel.add(thresholdTextField);
    	outputNorthPanel.add(computeButtonLabel);
    	outputNorthPanel.add(computeButton);
    	outputNorthPanel.add(loadScoreButtonLabel);
    	outputNorthPanel.add(loadScoreFileButton);    	
    	outputPanel.add(outputNorthPanel,BorderLayout.NORTH);
    	
    	JPanel outputSouthPanel = new JPanel();
    	scoreFileLocationLabel = new JLabel();
    	outputSouthPanel.add(scoreFileLocationLabel);
    	outputPanel.add(outputSouthPanel,BorderLayout.SOUTH);
    	//Add the tabbed pane to this panel.
		BorderLayout thisLayout = new BorderLayout();		
		setLayout(thisLayout);
        add(outputPanel,BorderLayout.CENTER);
	}
	 private int validateFieldAsRange(String value,String name,JComponent component) throws Exception{
    	try{
    		int range = Integer.parseInt(value);
    		if(range < 0){
    			JOptionPane.showMessageDialog(parent,"Range cannot be less than 0","ERROR",
    	   				JOptionPane.ERROR_MESSAGE);
    			component.requestFocusInWindow();
       			throw new Exception();
    		}
    		return range;
    	}
   		catch(NumberFormatException e){
   			JOptionPane.showMessageDialog(parent,"Input only numbers into " + name,"ERROR",
   				JOptionPane.ERROR_MESSAGE);
   			component.requestFocusInWindow();
   			throw new Exception();
   		}
    }   
	
	 private void loadScoreFile(){
	    	try{	    		
	    		JFileChooser fc;
	    		String lastLoadScoreFileLocation = SiriusSettings.getInformation("LastLoadScoreFileLocation: ");	
	    		if(lastLoadScoreFileLocation == null)
	    			fc = new JFileChooser();
	    		else
	    			fc = new JFileChooser(lastLoadScoreFileLocation);	            		    
		    	FileNameExtensionFilter filter = new FileNameExtensionFilter(
			            "Score Files", "scores");
			    fc.setFileFilter(filter);	
				int returnVal = fc.showOpenDialog(parent);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();	                        	
	            	loadingFilename = file.getAbsolutePath();		            	
	            	SiriusSettings.updateInformation("LastLoadScoreFileLocation: ", file.getAbsolutePath());
	            	scoreFileLocationLabel.setText("Score File Location: " + loadingFilename);
				}
				else
					return;	    			
	    	}catch(Exception ex){ex.printStackTrace();}
	    }
	 
	 private double validateFieldAsThreshold(String value,String name,JComponent component) throws Exception{
	    	try{
	    		double threshold = Double.parseDouble(value);
	    		if(threshold < 0.0 || threshold > 1.0){    			
	    			JOptionPane.showMessageDialog(parent,"Threshold must be within 0.0 to 1.0","ERROR",
	    	   				JOptionPane.ERROR_MESSAGE);
	    			component.requestFocusInWindow();
	       			throw new Exception();
	    		}
	    		int thresholdInt = (int)(threshold * 100000);
	    		if(thresholdInt % 100 != 0){
	    			JOptionPane.showMessageDialog(parent,"Threshold must be from 0.00 to 1.00 with each step-up of 0.001","ERROR",
	    	   				JOptionPane.ERROR_MESSAGE);
	    			component.requestFocusInWindow();
	       			throw new Exception();
	    		}
	    		return threshold;
	    	}
	   		catch(NumberFormatException e){
	   			JOptionPane.showMessageDialog(parent,"Input only numbers into " + name,"ERROR",
	   				JOptionPane.ERROR_MESSAGE);
	   			component.requestFocusInWindow();
	   			throw new Exception();
	   		}
	    }
	    
	 
	private void computeStats(){    	    	
		//this can load both classifierone.scores or classifiertwo.scores hence must set to true
    	ClassifierResults classifierResults = new ClassifierResults(true,0);
    	try{
    		int range = validateFieldAsRange(this.rangeTextField.getText(), "Range Field", 
    				this.rangeTextField);
    		double threshold = validateFieldAsThreshold(this.thresholdTextField.getText(), 
    				"Threshold Field", thresholdTextField);
    		//Display Statistics by reading the ClassifierOne
			PredictionStats classifierStats = new PredictionStats(this.loadingFilename,range,threshold);
			//display(double range)			
			classifierStats.updateDisplay(classifierResults, this.outputTextArea, false, 
					this.loadingFilename);			
    	}catch(Exception ex){/*Dun have to print stack trace here because it has already been taken care of inside validateField method()*/}
    }
		
	
	public void actionPerformed(ActionEvent ae){	
		if(ae.getSource().equals(computeButton)){
			computeStats();
		}else if(ae.getSource().equals(loadScoreFileButton)){			
			loadScoreFile();			
			computeStats();
		}
	}

}