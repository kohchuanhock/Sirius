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
package sirius.misc.predictionfileanalysis;


import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import sirius.trainer.main.SiriusSettings;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class PredictionFilePane extends JComponent implements ActionListener, ItemListener{
	static final long serialVersionUID = sirius.Sirius.version;
	private JComboBox resultFileComboBox;
	private JButton addButton;
	private JButton deleteButton;
	private JButton saveIndividualFileButton;
	private JTextField scoreThresholdTextField;
	private JButton runButton;
	private ResultFileTableModel originalResultFileTableModel;
	private JTable originalResultFileTable;	
	private JInternalFrame parent;
		
	private ResultFileTableModel sortedTableModel;
	private JTable sortedTable;
	
	private JButton saveButton;
	private JLabel statusLabel;
	
	private JComboBox sortByComboBox;
	
	public PredictionFilePane(JInternalFrame parent,JTabbedPane tabbedPane){		
		this.parent = parent;
		
		JPanel resultFilePanel = new JPanel();
		this.resultFileComboBox = new JComboBox();
		this.resultFileComboBox.addItem("                     ");
		this.addButton = new JButton("Add");
		this.addButton.addActionListener(this);
		this.deleteButton = new JButton("Delete");
		this.deleteButton.addActionListener(this);
		this.saveIndividualFileButton = new JButton("Save");
		this.saveIndividualFileButton.addActionListener(this);
		resultFilePanel.add(resultFileComboBox);
		resultFilePanel.add(addButton);
		resultFilePanel.add(deleteButton);
		resultFilePanel.add(this.saveIndividualFileButton);
		resultFilePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Prediction Files (Only for those One Prediction Per Sequence)"),
				BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		
		this.originalResultFileTableModel = new ResultFileTableModel(true);
		this.originalResultFileTable = new JTable(this.originalResultFileTableModel);			
		//this code allows the JTable to be sized according to me
		this.originalResultFileTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.originalResultFileTableModel.setTable(this.originalResultFileTable);
		PredictionFilePaneViewPort predictionFilePaneViewPort = new PredictionFilePaneViewPort();
		predictionFilePaneViewPort.setView(this.originalResultFileTable);
		JScrollPane originalResultFileTableScrollPane = new JScrollPane();
		originalResultFileTableScrollPane.setViewport(predictionFilePaneViewPort);
		JPanel originalResultFilePanel = new JPanel(new GridLayout(1,1));
		originalResultFilePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Prediction Files (Sequence Name & Scores)"),
				BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		originalResultFilePanel.add(originalResultFileTableScrollPane);		
		
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(resultFilePanel, BorderLayout.NORTH);
		centerPanel.add(originalResultFilePanel,BorderLayout.CENTER);
		
		JPanel settingsPanel = new JPanel();
		settingsPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Settings"),BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		JLabel scoreThresholdLabel = new JLabel("Total Score > ");
		this.scoreThresholdTextField = new JTextField("0.5",3);
		this.runButton = new JButton("Run");
		this.runButton.addActionListener(this);
		this.saveButton = new JButton("Save");
		this.saveButton.addActionListener(this);
		JLabel sortByLabel = new JLabel("Sort By: ");
		this.sortByComboBox = new JComboBox();	
		this.sortByComboBox.addItem("Score");
		this.sortByComboBox.addItem("Rank");
		this.sortByComboBox.addItemListener(this);
		settingsPanel.add(scoreThresholdLabel);
		settingsPanel.add(this.scoreThresholdTextField);
		settingsPanel.add(this.runButton);
		settingsPanel.add(this.saveButton);
		settingsPanel.add(sortByLabel);		
		settingsPanel.add(this.sortByComboBox);
				
		this.sortedTableModel = new ResultFileTableModel(false);
		this.sortedTable = new JTable(this.sortedTableModel);	
		this.sortedTableModel.setTable(this.sortedTable);
		JScrollPane sortedTableScrollPane = new JScrollPane(this.sortedTable);
		JPanel sortedPanel = new JPanel(new GridLayout(1,1));
		sortedPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Combined Results"),
				BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		sortedPanel.add(sortedTableScrollPane);
		
		JPanel statusPanel = new JPanel();
		statusPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Status"),BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		this.statusLabel = new JLabel("                     ");
		statusPanel.add(this.statusLabel);
		
		JPanel eastNorthPanel = new JPanel(new BorderLayout());
		eastNorthPanel.add(settingsPanel, BorderLayout.CENTER);
		eastNorthPanel.add(statusPanel, BorderLayout.EAST);
		
		JPanel eastPanel = new JPanel(new BorderLayout());
		eastPanel.add(eastNorthPanel, BorderLayout.NORTH);
		eastPanel.add(sortedPanel, BorderLayout.CENTER);
		
		setLayout(new BorderLayout());
		add(centerPanel, BorderLayout.CENTER);
		add(eastPanel, BorderLayout.EAST);
	}

	public void actionPerformed(ActionEvent ae) {		
		if(ae.getSource().equals(this.addButton)){
			try{		
				JFileChooser fc;				    	
				String lastSettingsLocation = SiriusSettings.getInformation("LastPredictionFileLocation: ");
	    		if(lastSettingsLocation == null)
	    			fc = new JFileChooser();
	    		else
	    			fc = new JFileChooser(lastSettingsLocation);
		    	FileNameExtensionFilter filter = new FileNameExtensionFilter(
			            "Prediction Files", "scores");
			    fc.setFileFilter(filter);	
				int returnVal = fc.showOpenDialog(parent);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();		            
		            SiriusSettings.updateInformation("LastPredictionFileLocation: ", file.getAbsolutePath());
	            	BufferedReader in = new BufferedReader(new FileReader(file.getAbsolutePath()));
	            	this.originalResultFileTableModel.loadPredictionFile(in, file.getName());
	            	this.resultFileComboBox.addItem(file.getName());
	            	in.close();		
	    		}		
			}    
  			catch(Exception e){
  				e.printStackTrace();
  				JOptionPane.showMessageDialog(parent,e.getMessage(),"ERROR",JOptionPane.ERROR_MESSAGE);
  			}
		}else if(ae.getSource().equals(this.deleteButton)){
			int index = this.resultFileComboBox.getSelectedIndex();
			if(index != 0){
				this.resultFileComboBox.removeItemAt(index);
				this.originalResultFileTableModel.deletePredictionFile(index - 1);				
			}
		}else if(ae.getSource().equals(this.runButton)){				
    		Thread runThread = (new Thread(){	      	
			public void run(){					
				sortedTableModel.tabulateTotalScore(originalResultFileTableModel.getOriginalData(), 
						Double.parseDouble(scoreThresholdTextField.getText()), statusLabel, 
						originalResultFileTableModel.getTotalSizeInOriginalData(), sortByComboBox.getSelectedIndex());	
			}});
      		runThread.setPriority(Thread.MIN_PRIORITY); // UI has most priority
      		runThread.start();	    
		}else if(ae.getSource().equals(this.saveButton)){
			save();			
		}else if(ae.getSource().equals(this.saveIndividualFileButton)){
			int index = this.resultFileComboBox.getSelectedIndex();
			if(index != 0){
				saveIndividualFile(index);//add remarks to the sequence name which is links to other files				
			}			
		}
	}
	
	private void saveIndividualFile(int index){			
		JFileChooser fc;	    	
		//if working directory not set then look at the Sirius Settings file
		String lastLocation = SiriusSettings.getInformation("LastIndividualPredictionFileWithLinksOutputLocation: ");
		if(lastLocation == null)
			fc = new JFileChooser();
		else
			fc = new JFileChooser(lastLocation);	    				    
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Prediction Files", "scores");
        fc.setFileFilter(filter);			        			        			    	
		int returnVal = fc.showSaveDialog(parent);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();			           
			String savingFilename = file.getAbsolutePath();
			if(savingFilename.indexOf(".scores") == -1)
				savingFilename += ".scores";
			SiriusSettings.updateInformation("LastIndividualPredictionFileWithLinksOutputLocation: ", savingFilename);
			try{
				BufferedWriter output = new BufferedWriter(new FileWriter(savingFilename));	
				this.originalResultFileTableModel.saveWithLinks(output, index - 1);
				output.close();
				JOptionPane.showMessageDialog(parent,"Save Successfully","Done",JOptionPane.INFORMATION_MESSAGE);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private void save(){		
		JFileChooser fc;	    	
		//if working directory not set then look at the Sirius Settings file
		String lastLocation = SiriusSettings.getInformation("LastPredictionFileOutputLocation: ");
		if(lastLocation == null)
			fc = new JFileChooser();
		else
			fc = new JFileChooser(lastLocation);	    				    
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Prediction Files Total Score", "totalScores");
        fc.setFileFilter(filter);			        			        			    	
		int returnVal = fc.showSaveDialog(parent);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();			           
			String savingFilename = file.getAbsolutePath();
			if(savingFilename.indexOf(".totalScores") == -1)
				savingFilename += ".totalScores";
			SiriusSettings.updateInformation("LastPredictionFileOutputLocation: ", savingFilename);
			try{
				BufferedWriter output = new BufferedWriter(new FileWriter(savingFilename));	
				this.sortedTableModel.save(output);
				output.close();
				JOptionPane.showMessageDialog(parent,"Save Successfully","Done",JOptionPane.INFORMATION_MESSAGE);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public void itemStateChanged(ItemEvent e) {
		if(e.getSource().equals(this.sortByComboBox)){
			if(this.sortByComboBox.getSelectedIndex() == 0){
				this.sortedTableModel.sortByScore();
			}else{
				this.sortedTableModel.sortByRankScore();
			}
		}
	}
}

class PredictionFilePaneViewPort extends JViewport{
	static final long serialVersionUID = sirius.Sirius.version;
	public PredictionFilePaneViewPort(){		
	}
    public void paintChildren(Graphics g){
        super.paintChildren(g);                		        		
    }

    public Color getBackground(){
        Component c = getView();
        return c == null ? super.getBackground() : c.getBackground();
    }        		   
}
