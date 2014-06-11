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
package sirius.misc.randomizesequence;


import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import sirius.predictor.main.*;
import sirius.trainer.main.*;


public class RandomizeSequencePane extends JComponent implements ActionListener, ListSelectionListener, MouseListener{
	static final long serialVersionUID = sirius.Sirius.version;
	
	private JTable originalSequenceNameTable;
	private SequenceNameTableModel originalSequenceNameTableModel;
	
	private JTable randomizedSequenceNameTable;
	private SequenceNameTableModel randomizedSequenceNameTableModel;
	
	private SequenceTableModel sequenceTableModel;
	
	private JTextField inputTextField = new JTextField(10);	
	
	private JTextField numberOfRSeqTextField = new JTextField("1", 3);	
	private JTextField markovOrderTextField = new JTextField("0", 3);
	private JTextField keyFromTextField = new JTextField(3);
	private JTextField keyToTextField = new JTextField(3);//Note that this is only for Split Button
	private JButton randomizeSelectedButton = new JButton("Rand Selected");
	private JButton randomizeAllButton = new JButton("Rand All");	
	private JButton saveButton = new JButton("Save");
	private JButton clearButton = new JButton("Clear");
	private JButton viewCompositionButton = new JButton("View Seq Comp");
	private JButton randomizeSequenceOrderButton = new JButton("Rand Seq Order");
	private JButton splitButton = new JButton("Split");
	private JTextField splitIntoTextField = new JTextField(3);
	
	private final JInternalFrame parent;
	
	public RandomizeSequencePane(final JInternalFrame parent){
		this.parent = parent;
		JPanel settingsPanel = new JPanel();
		settingsPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Settings"),BorderFactory.createEmptyBorder(0, 5, 5, 5)));
				
		this.inputTextField.addMouseListener(this);
		this.inputTextField.setEnabled(false);
		this.viewCompositionButton.addActionListener(this);
		
		this.randomizeSelectedButton.addActionListener(this);
		this.randomizeAllButton.addActionListener(this);
		this.clearButton.addActionListener(this);
		this.saveButton.addActionListener(this);
		this.randomizeSequenceOrderButton.addActionListener(this);
		this.splitButton.addActionListener(this);
				
		settingsPanel.add(this.inputTextField);		
		settingsPanel.add(this.viewCompositionButton);
		settingsPanel.add(new JLabel(" # Random Seq: "));
		settingsPanel.add(this.numberOfRSeqTextField);
		settingsPanel.add(new JLabel(" Markov Order: "));
		settingsPanel.add(this.markovOrderTextField);
		settingsPanel.add(new JLabel(" Key From: "));
		settingsPanel.add(this.keyFromTextField);
		settingsPanel.add(new JLabel(" Key To: "));
		settingsPanel.add(this.keyToTextField);
		settingsPanel.add(this.randomizeSelectedButton);
		settingsPanel.add(this.randomizeAllButton);
		settingsPanel.add(this.randomizeSequenceOrderButton);
		settingsPanel.add(this.clearButton);
		settingsPanel.add(this.saveButton);
		settingsPanel.add(new JLabel("Split Into: "));
		settingsPanel.add(this.splitIntoTextField);
		settingsPanel.add(this.splitButton);
					
		JPanel originalSequencePanel = new JPanel(new GridLayout(1,1));
		originalSequencePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Original Sequences"),BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		originalSequenceNameTableModel = new SequenceNameTableModel(false);		
		originalSequenceNameTable = new JTable(originalSequenceNameTableModel);
		originalSequenceNameTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
		originalSequenceNameTable.getColumnModel().getColumn(0).setMinWidth(20);       
		originalSequenceNameTable.getColumnModel().getColumn(1).setMinWidth(70);        
		originalSequenceNameTable.getColumnModel().getColumn(0).setMaxWidth(60);     
		originalSequenceNameTable.getColumnModel().getColumn(1).setPreferredWidth(200); 
		originalSequenceNameTable.getSelectionModel().addListSelectionListener(this);
		JScrollPane originalSequenceNameTableScrollPane = new JScrollPane(originalSequenceNameTable);
		originalSequenceNameTableScrollPane.setPreferredSize(new Dimension(300,420));
		originalSequencePanel.add(originalSequenceNameTableScrollPane);
				
		JPanel randomizedSequencePanel = new JPanel(new GridLayout(1,1));
		randomizedSequencePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Randomized Sequences"),BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		randomizedSequenceNameTableModel = new SequenceNameTableModel(false);		
		randomizedSequenceNameTable = new JTable(randomizedSequenceNameTableModel);
		randomizedSequenceNameTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
		randomizedSequenceNameTable.getColumnModel().getColumn(0).setMinWidth(20);       
		randomizedSequenceNameTable.getColumnModel().getColumn(1).setMinWidth(70);        
		randomizedSequenceNameTable.getColumnModel().getColumn(0).setMaxWidth(60);     
		randomizedSequenceNameTable.getColumnModel().getColumn(1).setPreferredWidth(200); 
		randomizedSequenceNameTable.getSelectionModel().addListSelectionListener(this);
		JScrollPane randomizedSequenceNameTableScrollPane = new JScrollPane(randomizedSequenceNameTable);
		randomizedSequenceNameTableScrollPane.setPreferredSize(new Dimension(300,420));
		randomizedSequencePanel.add(randomizedSequenceNameTableScrollPane);
		
		JPanel centerPanel = new JPanel(new GridLayout(1,2));
		centerPanel.add(originalSequencePanel);
		centerPanel.add(randomizedSequencePanel);
		
		JPanel sequencePanel = new JPanel(new GridLayout(1,1));
		sequencePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Sequence"),BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		sequenceTableModel = new SequenceTableModel(100, 10);
    	JTable sequenceTable = new JTable(sequenceTableModel);
    	sequenceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sequenceTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        sequenceTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        sequenceTable.getColumnModel().getColumn(1).setPreferredWidth(500); 
        sequenceTable.getColumnModel().getColumn(0).setMinWidth(40);
        sequenceTable.getColumnModel().getColumn(0).setMaxWidth(80);
        sequenceTable.getColumnModel().getColumn(1).setMinWidth(250); 
    	JScrollPane sequenceTableScrollPane = new JScrollPane(sequenceTable);
    	sequenceTableScrollPane.setPreferredSize(new Dimension(250,100));
    	sequencePanel.add(sequenceTableScrollPane,BorderLayout.CENTER);
		
		setLayout(new BorderLayout());
		add(settingsPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
		add(sequencePanel, BorderLayout.SOUTH);
	}
	
	public void setKeyValue(int key){
		this.keyFromTextField.setText("" + key);
	}
		
	public void actionPerformed(ActionEvent ae){
		if(ae.getSource().equals(this.randomizeSequenceOrderButton)){
			//Randomize Sequence Order
			randomizeSequenceOrder();
		}else if(ae.getSource().equals(this.randomizeSelectedButton)){
			//Randomize Selected			
			if(this.originalSequenceNameTable.getSelectedRow() != -1)
				randomizeSelected(this.originalSequenceNameTable.getSelectedRow(), Integer.parseInt(this.keyFromTextField.getText()));
		}else if(ae.getSource().equals(this.randomizeAllButton)){
			//Randomize All	
			randomizeAll();
		}else if(ae.getSource().equals(this.saveButton)){
			//Save
			save();
		}else if(ae.getSource().equals(this.clearButton)){
			//Clear
			this.randomizedSequenceNameTableModel.reset();
		}else if(ae.getSource().equals(this.viewCompositionButton)){
			//View Sequence Composition
			if(this.originalSequenceNameTable.getSelectedRow() != -1){
				int index = this.originalSequenceNameTable.getSelectedRow();
				RandomizeSequenceDialog dialog = null;				
				dialog = new RandomizeSequenceDialog(this.originalSequenceNameTableModel.getHeader(index), 
						this.originalSequenceNameTableModel.getSequence(index));
				dialog.setLocationRelativeTo(parent);
			    dialog.setVisible(true);
			}
		}else if(ae.getSource().equals(this.splitButton)){
			try{
				int keyFrom = Integer.parseInt(this.keyFromTextField.getText());
				int keyTo = Integer.parseInt(this.keyToTextField.getText());
				if(keyTo <= keyFrom)
					split();
				else{
					int splitInto = Integer.parseInt(this.splitIntoTextField.getText());
					if(splitInto <= 0){
						JOptionPane.showMessageDialog(parent,"Split Into must be > 0","Error",JOptionPane.ERROR_MESSAGE);
						return;
					}
					JFileChooser fc;	    	
					//if working directory not set then look at the Sirius Settings file
					String lastLocation = SiriusSettings.getInformation("LastRandomizeSequenceInputFileLocation: ");
					if(lastLocation == null)
						fc = new JFileChooser();
					else
						fc = new JFileChooser(lastLocation);	    				    
			        FileNameExtensionFilter filter = new FileNameExtensionFilter(
			            "Fasta Files", "fasta");
			        fc.setFileFilter(filter);			        			        			    	
					int returnVal = fc.showSaveDialog(parent);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();			           
						String savingFilename = file.getAbsolutePath();
						if(savingFilename.indexOf(".fasta") == -1)
							savingFilename += ".fasta";
						int endIndex = savingFilename.indexOf(".fasta");
						savingFilename = savingFilename.substring(0, endIndex);
						SiriusSettings.updateInformation("LastRandomizeSequenceInputFileLocation: ", savingFilename + "_fold(1).fasta");
						for(int x = keyFrom; x <= keyTo; x++){
							this.randomizedSequenceNameTableModel.reset();
							randomizeSequenceOrder();
							split(x, splitInto, savingFilename);							
						}
						JOptionPane.showMessageDialog(parent,"Save Successfully","Done",JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}catch(NumberFormatException e){split();}
		}
	}
	
	private void split(int key, int splitInto, String savingFilename){
		try{
			for(int x = 0; x < splitInto; x++){	
				BufferedWriter output = new BufferedWriter(new FileWriter(savingFilename + "_Key(" + key + ")_Fold(" + (x+1) + ").fasta"));	
				this.randomizedSequenceNameTableModel.saveFasta(output,x,splitInto);
				output.close();
			}			
		}catch(Exception e){
			e.printStackTrace();
		}				
	}
	
	private void split(){
		try{
			int splitInto = Integer.parseInt(this.splitIntoTextField.getText());
			if(splitInto <= 0){
				JOptionPane.showMessageDialog(parent,"Split Into must be > 0","Error",JOptionPane.ERROR_MESSAGE);
				return;
			}
			JFileChooser fc;	    	
			//if working directory not set then look at the Sirius Settings file
			String lastLocation = SiriusSettings.getInformation("LastRandomizeSequenceInputFileLocation: ");
			if(lastLocation == null)
				fc = new JFileChooser();
			else
				fc = new JFileChooser(lastLocation);	    				    
	        FileNameExtensionFilter filter = new FileNameExtensionFilter(
	            "Fasta Files", "fasta");
	        fc.setFileFilter(filter);			        			        			    	
			int returnVal = fc.showSaveDialog(parent);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();			           
				String savingFilename = file.getAbsolutePath();
				if(savingFilename.indexOf(".fasta") == -1)
					savingFilename += ".fasta";
				int endIndex = savingFilename.indexOf(".fasta");
				savingFilename = savingFilename.substring(0, endIndex);
				SiriusSettings.updateInformation("LastRandomizeSequenceInputFileLocation: ", savingFilename + "_fold(1).fasta");						
				try{
					for(int x = 0; x < splitInto; x++){	
						BufferedWriter output = new BufferedWriter(new FileWriter(savingFilename + "_fold(" + (x+1) + ").fasta"));	
						this.randomizedSequenceNameTableModel.saveFasta(output,x,splitInto);
						output.close();
					}
					JOptionPane.showMessageDialog(parent,"Save Successfully","Done",JOptionPane.INFORMATION_MESSAGE);
				}catch(Exception e){
					e.printStackTrace();
				}				
			}
		}catch(NumberFormatException ex){
			JOptionPane.showMessageDialog(parent,"Enter Integers only in Split Into","Error",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void save(){		
		JFileChooser fc;	    	
		//if working directory not set then look at the Sirius Settings file
		String lastLocation = SiriusSettings.getInformation("LastRandomizeSequenceInputFileLocation: ");
		if(lastLocation == null)
			fc = new JFileChooser();
		else
			fc = new JFileChooser(lastLocation);	    				    
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Fasta Files", "fasta");
        fc.setFileFilter(filter);			        			        			    	
		int returnVal = fc.showSaveDialog(parent);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();			           
			String savingFilename = file.getAbsolutePath();
			if(savingFilename.indexOf(".fasta") == -1)
				savingFilename += ".fasta";
			SiriusSettings.updateInformation("LastRandomizeSequenceInputFileLocation: ", savingFilename);
			try{
				BufferedWriter output = new BufferedWriter(new FileWriter(savingFilename));	
				this.randomizedSequenceNameTableModel.saveFasta(output);
				output.close();
				JOptionPane.showMessageDialog(parent,"Save Successfully","Done",JOptionPane.INFORMATION_MESSAGE);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private boolean checkFields(){		
		try{
			int num = Integer.parseInt(this.numberOfRSeqTextField.getText());
			if(num <= 0)
				throw new Exception();
			int markov = Integer.parseInt(this.markovOrderTextField.getText());
			if(markov < 0)
				throw new Exception();
			Integer.parseInt(this.keyFromTextField.getText());
		}catch(Exception e){
			return false;
		}
		return true;
	}		
	
	public static String randomize(String sequence, int randomNumber, int markov){
		markov++;
		int remainder = sequence.length() % markov;
		String remainderSequence = "";		
		if(remainder != 0){
			remainderSequence = sequence.substring(sequence.length() - remainder);
			sequence = sequence.substring(0, sequence.length() - remainder);
		}
		//chopping
		ArrayList<String> choppedList = new ArrayList<String>();
		for(int x = 0; x < sequence.length(); x += markov){
			choppedList.add(sequence.substring(x, x+markov));
		}
		//randomize
		Random rng = new Random(randomNumber);
		String returnSequence = "";
		while(choppedList.size() > 0){			
			returnSequence += choppedList.remove(rng.nextInt(choppedList.size()));			
		}
		return returnSequence + remainderSequence;
	}
	
	private void randomizeSequenceOrder(){
		if(checkFields() == false){
			JOptionPane.showMessageDialog(null,"Ensure all fields are properly filled in","Error",JOptionPane.ERROR_MESSAGE);
			return;
		}		
		SequenceNameTableModel tempModel = new SequenceNameTableModel(this.originalSequenceNameTableModel);
		Random rand = new Random(Integer.parseInt(this.keyFromTextField.getText()));
		while(tempModel.size() > 0){			
			int randInt = rand.nextInt(tempModel.size());
			this.randomizedSequenceNameTableModel.add(tempModel.getData().get(randInt));
			tempModel.getData().remove(randInt);
		}
	}
	
	private void randomizeAll(){
		if(checkFields() == false){
			JOptionPane.showMessageDialog(null,"Ensure all fields are properly filled in","Error",JOptionPane.ERROR_MESSAGE);
			return;
		}		
		int key = Integer.parseInt(this.keyFromTextField.getText());			
		for(int y = 0; y < this.originalSequenceNameTableModel.size(); y++){
			randomizeSelected(y, key);
		}		
	}
	
	private void randomizeSelected(int index, int key){
		if(checkFields() == false){
			JOptionPane.showMessageDialog(null,"Ensure all fields are properly filled in","Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
		String sequence = this.originalSequenceNameTableModel.getSequence(index);
		String sequenceHeader = this.originalSequenceNameTableModel.getHeader(index);
		int numberOfRandomSequenceEach = Integer.parseInt(this.numberOfRSeqTextField.getText());
		int markov = Integer.parseInt(this.markovOrderTextField.getText());
		Random rng = new Random(key);
		for(int x = 0; x < numberOfRandomSequenceEach; x++){
			String randomizedSequence = randomize(sequence, rng.nextInt(), Integer.parseInt(this.markovOrderTextField.getText()));
			this.randomizedSequenceNameTableModel.add(new SequenceNameData("> Random(" + (x+1) + ") Key(" + key + 
					") Markov("  +  markov + ") " + sequenceHeader.substring(1), randomizedSequence, ""));
			if(checkPropertiesRetained(sequence, randomizedSequence, markov) == false)
				JOptionPane.showMessageDialog(null,"Errors in randomizing - PROPERTIES NOT RETAINED","Error",JOptionPane.ERROR_MESSAGE);
		}		
	}
	
	private boolean checkPropertiesRetained(String originalSequence, String randomSequence, int markov){
		//first, check the length
		if(originalSequence.length() != randomSequence.length())
			return false;
		Hashtable<String, Integer> originalHashtable = new Hashtable<String, Integer>();
		Hashtable<String, Integer> randomHashtable = new Hashtable<String, Integer>();
		for(int x = 0; x < originalSequence.length(); x+= 1 + markov){			
			String currentOriginal = originalSequence.substring(x, x + markov + 1);
			String currentRandom = originalSequence.substring(x, x + markov + 1);
			if(originalHashtable.containsKey(currentOriginal)){				
				Integer temp = originalHashtable.get(currentOriginal);				
				originalHashtable.put(currentOriginal, temp+1);				
			}				
			else
				originalHashtable.put(currentOriginal, new Integer(1));				
			if(randomHashtable.containsKey(currentRandom)){
				Integer temp = randomHashtable.get(currentRandom);
				randomHashtable.put(currentRandom, temp+1);
			}
			else						
				randomHashtable.put(currentRandom, new Integer(1));				
		}
		//compare
		for(int x = 0; x < originalSequence.length(); x+= 1 + markov){
			String current = originalSequence.substring(x, x + markov + 1);			
			if(originalHashtable.get(current).intValue() != randomHashtable.get(current).intValue())				
				throw new Error(current + ": " + originalHashtable.get(current) + " " + randomHashtable.get(current));		
		}
		return true;
	}
	
	private void browse(){
		//applicationData.hasLocationIndexBeenSet = false;
		JFileChooser fc;	    	
		//if working directory not set then look at the Sirius Settings file
		String lastLocation = SiriusSettings.getInformation("LastRandomizeSequenceInputFileLocation: ");
		if(lastLocation == null)
			fc = new JFileChooser();
		else
			fc = new JFileChooser(lastLocation);	    				    
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Fasta Files", "fasta");
        fc.setFileFilter(filter);			        			        			    	
		int returnVal = fc.showOpenDialog(parent);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
            SiriusSettings.updateInformation("LastRandomizeSequenceInputFileLocation: ", file.getAbsolutePath());
            this.inputTextField.setText(file.getAbsolutePath());
            try{
            	loadFastaFile(file);	            	
            }catch(Exception e){
            	JOptionPane.showMessageDialog(null,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            	e.printStackTrace();
            } 	            
		}			
	}
	
	public void loadFastaFile(final File file) throws Exception{
		Thread runThread = new Thread(){
			public void run(){	
				try{								
					originalSequenceNameTableModel.loadFastaFile(file);		
					if(originalSequenceNameTableModel.getRowCount() > 0)
						originalSequenceNameTable.setRowSelectionInterval(0,0);
				}catch(Exception e){
			    	JOptionPane.showMessageDialog(null,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			    	e.printStackTrace();
				}	
			}
		};
		runThread.setPriority(Thread.MIN_PRIORITY);
     	runThread.start();
	}	

	
	public void valueChanged(ListSelectionEvent e) {
		if(e.getSource().equals(this.originalSequenceNameTable.getSelectionModel())){
			if(this.originalSequenceNameTable.getSelectedRow() != -1){
				this.randomizedSequenceNameTable.clearSelection();
				String header = this.originalSequenceNameTableModel.getHeader(this.originalSequenceNameTable.getSelectedRow());
	    		String sequence = this.originalSequenceNameTableModel.getSequence(this.originalSequenceNameTable.getSelectedRow());	    		
	    		sequenceTableModel.setSequence(header,sequence);
			}
		}else if(e.getSource().equals(this.randomizedSequenceNameTable.getSelectionModel())){
			if(this.randomizedSequenceNameTable.getSelectedRow() != -1){
				this.originalSequenceNameTable.clearSelection();
				String header = this.randomizedSequenceNameTableModel.getHeader(this.randomizedSequenceNameTable.getSelectedRow());
    			String sequence = this.randomizedSequenceNameTableModel.getSequence(this.randomizedSequenceNameTable.getSelectedRow());    			
    			sequenceTableModel.setSequence(header,sequence);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent me) {	
		if(me.getSource().equals(this.inputTextField))		
			browse();		
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}
}