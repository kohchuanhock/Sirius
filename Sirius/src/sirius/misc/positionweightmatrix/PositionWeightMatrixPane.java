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
package sirius.misc.positionweightmatrix;
//Note that this is PWM pane

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.filechooser.FileNameExtensionFilter;

import sirius.trainer.main.ScoringMatrix;
import sirius.trainer.main.SiriusSettings;
import sirius.utils.ClassifierResults;
import sirius.utils.FastaFileReader;
import sirius.utils.FastaFormat;
import sirius.utils.PredictionStats;

public class PositionWeightMatrixPane extends JComponent implements ActionListener{
	static final long serialVersionUID = sirius.Sirius.version;
	
	JButton trainingFileBrowseButton = new JButton("Browse");
	JTextField trainingFileTextField = new JTextField(21);		
	PWMTableModel pwmTableModel = new PWMTableModel(true);
	JTable pwmTable = new JTable(pwmTableModel);
	
	PWMTableModel pairwiseEnergyTableModel = new PWMTableModel(false);
	JTable pairwiseEnergyTable = new JTable(this.pairwiseEnergyTableModel);
	JInternalFrame parent;
	FastaFileReader trainingFileFastaFileReader;
	FastaFileReader posFileFastaFileReader;
	FastaFileReader negFileFastaFileReader;
	JScrollPane pwmTableScrollPane;
	
	JTextField totalSequenceTextField = new JTextField(3);;
	JRadioButton frequencyRadioButton;	
	JRadioButton percentageRadioButton;
	
	JTextField posFileTextField;
	JTextField negFileTextField = new JTextField(5);
	JButton evaluateButton = new JButton("  Evaluate  ");
	JButton posBrowseButton;
	JButton negBrowseButton = new JButton("Browse");
	JTextArea outputTextArea;
	
	String outputDirectory;
	
	JRadioButton posFileRadioButton = new JRadioButton("+ve: ",true);
	JRadioButton crossValidationRadioButton = new JRadioButton("X-Validation: ");
	JTextField thresholdTextField = new JTextField("0.5",5);
	JTextField crossValidationTextField = new JTextField("10",2);
	
	BitSet positionsToConsider;//only consider positions that are lower than a threshold entropy
	
	JRadioButton allWeightOneRadioButton = new JRadioButton("Weights=1",true);;
	JRadioButton weightRelativeToEntropyRadioButton = new JRadioButton("Weights=(1-Entropy)");
	JRadioButton pwmWithGapRadioButton = new JRadioButton("Weights=0 if entropy>threshold:");;
	JTextField entropyThresholdTextField = new JTextField("0.6", 3);
	
	//components for energyPanel
	JButton computeButton = new JButton("Compute Matrix");
	JComboBox matrixComboBox = new JComboBox();
	JTextField maxInfluenceDistance = new JTextField("3",3);
	//put this away first since i cannot fully understand
	//JCheckBox influenceProportionalToDistance = new JCheckBox("1/Distance");
	
	JRadioButton pwmMatrixRadioButton = new JRadioButton("PWM Matrix",true);
	JRadioButton minEnergyMatrixRadioButton = new JRadioButton("Min Energy Matrix");
	
	public PositionWeightMatrixPane(final JInternalFrame parent,JTabbedPane tabbedPane){
		this.parent = parent;
		
		JPanel trainingFilePanel = new JPanel(new BorderLayout());
		trainingFilePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Training File"),BorderFactory.createEmptyBorder(0, 5, 5, 5)));
				
		trainingFileBrowseButton.addActionListener(this);
		trainingFileTextField.setEnabled(false);			
		
		JPanel trainingFileCenterPanel = new JPanel();
		trainingFileCenterPanel.add(new JLabel("Fasta File: "));
		trainingFileCenterPanel.add(trainingFileTextField);		
		trainingFileCenterPanel.add(trainingFileBrowseButton);
		
		this.allWeightOneRadioButton.addActionListener(this);
		this.weightRelativeToEntropyRadioButton.addActionListener(this);
		this.pwmWithGapRadioButton.addActionListener(this);
		
		JPanel trainingFileSouthPanel = new JPanel();
		trainingFileSouthPanel.add(allWeightOneRadioButton);
		trainingFileSouthPanel.add(weightRelativeToEntropyRadioButton);
		trainingFileSouthPanel.add(pwmWithGapRadioButton);
		trainingFileSouthPanel.add(entropyThresholdTextField);
		
		trainingFilePanel.add(trainingFileCenterPanel, BorderLayout.CENTER);
		trainingFilePanel.add(trainingFileSouthPanel, BorderLayout.SOUTH);
															
		JPanel pwmPanel = new JPanel(new BorderLayout());
		pwmPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Position Weight Matrix"),BorderFactory.createEmptyBorder(0, 5, 5, 5)));		
		//this code allows the JTable to be sized according to me
		pwmTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);		
		pwmTableModel.setTable(pwmTable);
		PWMViewPort pwmViewPort = new PWMViewPort();
		pwmViewPort.setView(pwmTable);
        pwmTableScrollPane = new JScrollPane();        
        pwmTableScrollPane.setViewport(pwmViewPort);
    	pwmPanel.add(pwmTableScrollPane,BorderLayout.CENTER);
		
    	JPanel statsPanel = new JPanel();    	    	
    	totalSequenceTextField.setEnabled(false);
    	frequencyRadioButton = new JRadioButton("Frequency");
    	frequencyRadioButton.addActionListener(this);
    	frequencyRadioButton.setSelected(true);    	
    	percentageRadioButton = new JRadioButton("Percentage");
    	percentageRadioButton.addActionListener(this);
    	
    	statsPanel.add(new JLabel("Total Seq: "));
    	statsPanel.add(totalSequenceTextField);
    	statsPanel.add(frequencyRadioButton);
    	statsPanel.add(percentageRadioButton);  
    	pwmPanel.add(statsPanel, BorderLayout.NORTH);
    	
		JPanel trainingPanel = new JPanel(new BorderLayout());
		trainingPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Training"),BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		
		trainingPanel.add(trainingFilePanel, BorderLayout.NORTH);
				
		JPanel pwmNenergyPanel = new JPanel(new GridLayout(2,1));
		pwmNenergyPanel.add(pwmPanel);
		pwmNenergyPanel.add(initEnergyPanel());
		trainingPanel.add(pwmNenergyPanel, BorderLayout.CENTER);		
		
		JPanel testFilePanel = new JPanel(new GridLayout(3,1));
		testFilePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Test Files"),BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		
		JPanel matrixSelectionPanel = new JPanel();
		matrixSelectionPanel.add(this.pwmMatrixRadioButton);
		matrixSelectionPanel.add(this.minEnergyMatrixRadioButton);
		this.pwmMatrixRadioButton.addActionListener(this);
		this.minEnergyMatrixRadioButton.addActionListener(this);
		
		JPanel posFilePanel = new JPanel();		
		posFileRadioButton.addActionListener(this);
		posFileTextField = new JTextField(5);
		posFileTextField.setEnabled(false);
		posBrowseButton = new JButton("Browse");
		posBrowseButton.addActionListener(this);		
		posFilePanel.add(posFileRadioButton);
		posFilePanel.add(posFileTextField);
		posFilePanel.add(posBrowseButton);
		
		JPanel negFilePanel = new JPanel();				
		negFileTextField.setEnabled(false);		
		negBrowseButton.addActionListener(this);		
		negFilePanel.add(new JLabel("-ve: "));
		negFilePanel.add(negFileTextField);
		negFilePanel.add(negBrowseButton);
		
		JPanel crossValidationPanel = new JPanel();		
		crossValidationRadioButton.addActionListener(this);		
		crossValidationTextField.setEnabled(false);		
		evaluateButton.addActionListener(this);
		crossValidationPanel.add(crossValidationRadioButton);
		crossValidationPanel.add(crossValidationTextField);
		
		JPanel evaluatePanel = new JPanel();		
		evaluatePanel.add(new JLabel("Threshold: "));
		evaluatePanel.add(thresholdTextField);
		evaluatePanel.add(evaluateButton);		
		
		JPanel upperPanel = new JPanel();
		upperPanel.add(posFilePanel);
		upperPanel.add(crossValidationPanel);
		upperPanel.add(negFilePanel);	
		testFilePanel.add(matrixSelectionPanel);
		testFilePanel.add(upperPanel);
		testFilePanel.add(evaluatePanel);				
		
		JPanel outputPanel = new JPanel(new BorderLayout());
		outputPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Output"),BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		outputTextArea = new JTextArea();
		outputTextArea.setEditable(false);
		JScrollPane outputScrollPane = new JScrollPane(outputTextArea);    	
    	outputPanel.add(outputScrollPane,BorderLayout.CENTER);			
		
		JPanel testingPanel = new JPanel(new BorderLayout());
		testingPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Evaluating"),BorderFactory.createEmptyBorder(0, 5, 5, 5)));	
		testingPanel.add(testFilePanel, BorderLayout.NORTH);
		testingPanel.add(outputPanel, BorderLayout.CENTER);
		
		setLayout(new GridLayout(1,2));
		add(trainingPanel);
		add(testingPanel);
	}
	
	public JPanel initEnergyPanel(){
		JPanel energyPanel = new JPanel(new BorderLayout());
		energyPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Min Energy Matrix"),BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		
		JPanel statsPanel = new JPanel();
		statsPanel.add(new JLabel("Max Dist to consider: "));
		statsPanel.add(this.maxInfluenceDistance);		
		//statsPanel.add(this.influenceProportionalToDistance);
		statsPanel.add(this.matrixComboBox);		
		statsPanel.add(this.computeButton);
		this.computeButton.addActionListener(this);
		
		this.matrixComboBox.addItem("Zsuzsanna");
		this.matrixComboBox.addItem("Zsuzsanna2");
		
		JPanel energyMatrixPanel = new JPanel(new GridLayout(1,1));
		//this code allows the JTable to be sized according to me
		this.pairwiseEnergyTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);		
		this.pairwiseEnergyTableModel.setTable(pairwiseEnergyTable);
		PWMViewPort pwmViewPort = new PWMViewPort();
		pwmViewPort.setView(pairwiseEnergyTable);
        JScrollPane pairwiseEnergyScrollPane = new JScrollPane();        
        pairwiseEnergyScrollPane.setViewport(pwmViewPort);
        energyMatrixPanel.add(pairwiseEnergyScrollPane);
		
		energyPanel.add(statsPanel,BorderLayout.NORTH);
		energyPanel.add(energyMatrixPanel,BorderLayout.CENTER);
		return energyPanel;
	}
	
	private void computeMinEnergyMatrix(FastaFileReader trainingFastaFile, boolean update){
		//NOTE: More negative values indicates more favorable interaction
		//here, we assume that the input file is aligned and all sequences are of same length
		//do some basic init
		ScoringMatrix scoringMatrix = new ScoringMatrix();
		//+3 because identity, blosum62 and structure-derived are not involved here
		scoringMatrix.setMatrix(this.matrixComboBox.getSelectedIndex()+3);
		Hashtable<Character, ArrayList<MyInteger>> matrixHashtable = new Hashtable<Character, ArrayList<MyInteger>>();
		String[] dataMatrix = {
				"A","C","D","E","F","G","H","I","K","L","M","N","P","Q","R","S","T","V","W","Y"				
		};
		for(int x = 0; x < dataMatrix.length; x++)
			matrixHashtable.put(dataMatrix[x].charAt(0), new ArrayList<MyInteger>());		
		//first find the length of the mininmum energy matrix		
		int lengthOfMinEnergyMatrix = trainingFastaFile.getDataAt(0).getSequenceLength();
		int maxDistanceToConsider = Integer.parseInt(this.maxInfluenceDistance.getText());
		//more init
		lengthOfMinEnergyMatrix += (maxDistanceToConsider-1)*2;		
		for(Enumeration<Character> e = matrixHashtable.keys(); e.hasMoreElements();){    		
    		ArrayList<MyInteger> tempArrayList = matrixHashtable.get(e.nextElement());
    		for(int x = 0; x < lengthOfMinEnergyMatrix; x++){
    			tempArrayList.add(new MyInteger());
    		}
    	}
		//then for each AA in each position, find the probability
		//for each AA
		for(Enumeration<Character> e = matrixHashtable.keys(); e.hasMoreElements();){
			char currentAA = e.nextElement();			
			//for each sequence				
			for(int y = 0; y < trainingFastaFile.size(); y++){
				String sequence = trainingFastaFile.getDataAt(y).getSequence();					
				for(int x = 0; x < sequence.length(); x++){
					char sequenceAA = sequence.charAt(x);
					for(int z = 0; z < ((maxDistanceToConsider-1)*2)+1; z++){
						double pairwiseEnergy = scoringMatrix.getScore(currentAA, sequenceAA);
						//put this away for now since it complicates things alot
						//if(this.influenceProportionalToDistance.isSelected())
							//pairwiseEnergy = pairwiseEnergy * (1.0/(Math.abs((x-z)/2.0)+1.0));
						matrixHashtable.get(currentAA).get(x+z).increment(pairwiseEnergy);
					}
				}										
			}								
		}
		//*-1 to all values since the more negative values indicates more favorable interactions		
		//convert energy matrix to position probability matrix	
		energy2proability(trainingFastaFile, matrixHashtable,lengthOfMinEnergyMatrix);		
		//set this into the tablemodel
		if(update)
			//the third argument 0 means dun attempt to divide it and get the percentage value since it already is
			//1 would have of course meant that go ahead and divide it to obtain percentage value
			this.pairwiseEnergyTableModel.update(matrixHashtable, lengthOfMinEnergyMatrix, 0);
		//Lastly, I need to compute the min and max Energy achievable using this minEnergyMatrix and Selected pairwise EnergyMatrix
		double minEnergyValue = 0.0;
		double maxEnergyValue = 0.0;
		//for each position in the sequence
		for(int x = 0; x < trainingFastaFile.getDataAt(0).getSequenceLength(); x++){				
    		double currentMinValue = Double.POSITIVE_INFINITY;
    		double currentMaxValue = Double.NEGATIVE_INFINITY;
    		//what would be the energy if it was A, C, N etc etc
    		for(int y = 0; y < dataMatrix.length;y++){
    			char currentAA = dataMatrix[y].charAt(0);
    			double currentEnergy = 0.0;
    			for(Enumeration<Character> e = matrixHashtable.keys(); e.hasMoreElements();){
    				char sequenceAA = e.nextElement();
        			ArrayList<MyInteger> probabilityArrayList = matrixHashtable.get(sequenceAA);
        			for(int z = 0; z < ((maxDistanceToConsider-1)*2)+1; z++){
        				//*-1 here because more -ve is more favorable
    					double pairwiseEnergy = scoringMatrix.getScore(currentAA, sequenceAA) * probabilityArrayList.get(x+z).value * -1;
    					//if(this.influenceProportionalToDistance.isSelected())
    						//pairwiseEnergy = pairwiseEnergy * (1.0/(Math.abs(x-maxDistanceToConsider)+1.0));
    					currentEnergy += pairwiseEnergy;
        			}
    			}
    			if(currentMinValue > currentEnergy)
    				currentMinValue = currentEnergy;
    			if(currentMaxValue < currentEnergy)
    				currentMaxValue = currentEnergy;
    		}    		    		    			    
    		minEnergyValue += (currentMinValue);
    		maxEnergyValue += (currentMaxValue);
    	}    	    	    	
		MyInteger.minEnergyValue = minEnergyValue;
		MyInteger.maxEnergyValue = maxEnergyValue;
	}
	
	private void energy2proability(FastaFileReader trainingFastaFile, Hashtable<Character, ArrayList<MyInteger>> matrixHashtable, 
			int lengthOfMinEnergyMatrix){
		//get the total score for each position
		ArrayList<Double> positionTotalScoreList = new ArrayList<Double>();
		//get the most negative value for each position
		ArrayList<Double> positionMostNegList = new ArrayList<Double>();
		for(int x = 0; x < lengthOfMinEnergyMatrix; x++){
			positionTotalScoreList.add(0.0);
			positionMostNegList.add(Double.POSITIVE_INFINITY);
		}		
		//*-1 to all
		for(Enumeration<Character> e = matrixHashtable.keys(); e.hasMoreElements();){
			Character c = e.nextElement();
    		ArrayList<MyInteger> tempArrayList = matrixHashtable.get(c);    		
    		for(int x = 0; x < tempArrayList.size(); x++){    			    			
    			double temp = tempArrayList.get(x).negateAndAverageOut(trainingFastaFile.size());     			    			 			
    			positionTotalScoreList.set(x,positionTotalScoreList.get(x)+temp);    			
    			if(temp < positionMostNegList.get(x))
    				positionMostNegList.set(x, temp);
    		}    		
    	}		
		//make all values non-zero by shifting
		for(int x = 0; x < positionTotalScoreList.size(); x++){
			double negValue = positionMostNegList.get(x);
			if(negValue < 0){
				negValue *= -1;				
				positionTotalScoreList.set(x, positionTotalScoreList.get(x) + (matrixHashtable.size()*negValue));
			}
		}
		//normalize - this step will make them probability matrix
		for(Enumeration<Character> e = matrixHashtable.keys(); e.hasMoreElements();){    		
    		ArrayList<MyInteger> tempArrayList = matrixHashtable.get(e.nextElement());
    		for(int x = 0; x < tempArrayList.size(); x++){
    			if(positionMostNegList.get(x) < 0)
    				tempArrayList.get(x).increment(positionMostNegList.get(x)*-1);
    			tempArrayList.get(x).normalize(positionTotalScoreList.get(x));
    		}
    	}
	}
	
	private void openTrainingFile(){
		JFileChooser fc;	    	
		//if working directory not set then look at the Sirius Settings file
		String lastLocation = SiriusSettings.getInformation("LastPWMFileLocation: ");
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
			trainingFileFastaFileReader = new FastaFileReader(file.getAbsolutePath());
			totalSequenceTextField.setText("" + trainingFileFastaFileReader.size());
			MyInteger.totalSequence = trainingFileFastaFileReader.size();
            SiriusSettings.updateInformation("LastPWMFileLocation: ", file.getAbsolutePath());	
            outputDirectory = file.getParent();
            if(outputDirectory == null)
            	outputDirectory = "." + File.separator;	            
            trainingFileTextField.setText(file.getAbsolutePath());
            calculatePWM(this.trainingFileFastaFileReader);
		}
	}
	
	private void openPosTestFile(){
		JFileChooser fc;	    	
		//if working directory not set then look at the Sirius Settings file
		String lastLocation = SiriusSettings.getInformation("LastPWMFileLocation: ");
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
			posFileFastaFileReader = new FastaFileReader(file.getAbsolutePath());							
            SiriusSettings.updateInformation("LastPWMFileLocation: ", file.getAbsolutePath());	            
            posFileTextField.setText(file.getAbsolutePath());
		}
	}
	
	private void openNegTestFile(){
		JFileChooser fc;	    	
		//if working directory not set then look at the Sirius Settings file
		String lastLocation = SiriusSettings.getInformation("LastPWMFileLocation: ");
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
			negFileFastaFileReader = new FastaFileReader(file.getAbsolutePath());							
            SiriusSettings.updateInformation("LastPWMFileLocation: ", file.getAbsolutePath());	            
            negFileTextField.setText(file.getAbsolutePath());
		}			
	}
	
	private void evaluateBasedOnMinEnergy(ScoringMatrix scoringMatrix,int maxDist){
		double threshold = 0.5;
		if(negFileFastaFileReader == null || this.pairwiseEnergyTableModel.getData() == null){
			JOptionPane.showMessageDialog(null,"Please ensure Min Energy Matrix are trained and test files are set properly before evaluation.",
					"Settings not proper",JOptionPane.ERROR_MESSAGE);
			return;
		}else if(this.posFileRadioButton.isSelected() == true && posFileFastaFileReader == null){
			JOptionPane.showMessageDialog(null,"Please ensure PWM are trained and test files are set properly before evaluation.",
					"Settings not proper",JOptionPane.ERROR_MESSAGE);
			return;
		}			
		try{			
			threshold = Double.parseDouble(this.thresholdTextField.getText());
		}catch(NumberFormatException e){			
			JOptionPane.showMessageDialog(null,"Threshold must be a number.",
					"Threshold must be a number",JOptionPane.ERROR_MESSAGE);
			return;
		}
		try{
			int foldNumber = 0;
			BufferedWriter output = new BufferedWriter(new FileWriter(outputDirectory + File.separator + "MinEnergyResults.score"));
			if(this.crossValidationRadioButton.isSelected()){
				try{
					foldNumber = Integer.parseInt(this.crossValidationTextField.getText());
					crossValidateEnergy(foldNumber, this.trainingFileFastaFileReader, output,scoringMatrix,maxDist);
				}catch(NumberFormatException e){
					JOptionPane.showMessageDialog(null,"CrossValidation must be an Integer.",
							"CrossValidation must be an Integer",JOptionPane.ERROR_MESSAGE);
					output.close();
					return;
				}catch(Exception e){
					e.printStackTrace();
				}
			}else{
				evaluateEnergy(output, posFileFastaFileReader, "pos",scoringMatrix,maxDist);					
			}
			evaluateEnergy(output, negFileFastaFileReader, "neg",scoringMatrix,maxDist);
			output.close();
			showEnergyResults(threshold);
			//calculatePWM(this.trainingFileFastaFileReader);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void evaluateBasedOnPWM(){
		double threshold = 0.5;
		if(negFileFastaFileReader == null || pwmTableModel.getData() == null){
			JOptionPane.showMessageDialog(null,"Please ensure PWM are trained and test files are set properly before evaluation.",
					"Settings not proper",JOptionPane.ERROR_MESSAGE);
			return;
		}else if(this.posFileRadioButton.isSelected() == true && posFileFastaFileReader == null){
			JOptionPane.showMessageDialog(null,"Please ensure PWM are trained and test files are set properly before evaluation.",
					"Settings not proper",JOptionPane.ERROR_MESSAGE);
			return;
		}			
		try{
			threshold = Double.parseDouble(this.thresholdTextField.getText());
		}catch(NumberFormatException e){
			JOptionPane.showMessageDialog(null,"Threshold must be a number.",
					"Threshold must be a number",JOptionPane.ERROR_MESSAGE);
			return;
		}
		try{
			int foldNumber = 0;
			BufferedWriter output = new BufferedWriter(new FileWriter(outputDirectory + File.separator + "PWMResults.score"));
			if(this.crossValidationRadioButton.isSelected()){
				try{
					foldNumber = Integer.parseInt(this.crossValidationTextField.getText());
					crossValidate(foldNumber, this.trainingFileFastaFileReader, output);
				}catch(NumberFormatException e){
					JOptionPane.showMessageDialog(null,"CrossValidation must be an Integer.",
							"CrossValidation must be an Integer",JOptionPane.ERROR_MESSAGE);
					output.close();
					return;
				}catch(Exception e){
					e.printStackTrace();
				}
			}else{
				evaluate(output, posFileFastaFileReader, "pos");					
			}
			evaluate(output, negFileFastaFileReader, "neg");
			output.close();
			showResults(threshold);
			calculatePWM(this.trainingFileFastaFileReader);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void actionPerformed(ActionEvent ae){
		if(ae.getSource().equals(this.minEnergyMatrixRadioButton)){
			this.pwmMatrixRadioButton.setSelected(false);
			this.minEnergyMatrixRadioButton.setSelected(true);
		}else if(ae.getSource().equals(this.pwmMatrixRadioButton)){
			this.pwmMatrixRadioButton.setSelected(true);
			this.minEnergyMatrixRadioButton.setSelected(false);
		}else if(ae.getSource().equals(this.computeButton)){
			computeMinEnergyMatrix(this.trainingFileFastaFileReader,true);
		}else if(ae.getSource().equals(trainingFileBrowseButton)){
			openTrainingFile();
		}else if(ae.getSource().equals(frequencyRadioButton)){
			frequencyRadioButton.setSelected(true);			
			percentageRadioButton.setSelected(false);
			pwmTableModel.updateTable(0);
		}else if(ae.getSource().equals(percentageRadioButton)){
			frequencyRadioButton.setSelected(false);			
			percentageRadioButton.setSelected(true);
			pwmTableModel.updateTable(1);
		}else if(ae.getSource().equals(posBrowseButton)){			
			openPosTestFile();
		}else if(ae.getSource().equals(negBrowseButton)){
			openNegTestFile();
		}else if(ae.getSource().equals(evaluateButton)){
			if(this.pwmMatrixRadioButton.isSelected())
				evaluateBasedOnPWM();
			else{
				ScoringMatrix scoringMatrix = new ScoringMatrix();
				//+3 because identity, blosum62 and structure-derived are not involved here
				scoringMatrix.setMatrix(this.matrixComboBox.getSelectedIndex()+3);
				int maxDistanceToConsider = Integer.parseInt(this.maxInfluenceDistance.getText());
				evaluateBasedOnMinEnergy(scoringMatrix,maxDistanceToConsider);
			}
		}else if(ae.getSource().equals(this.posFileRadioButton)){
			this.posFileRadioButton.setSelected(true);
			this.posBrowseButton.setEnabled(true);
			this.crossValidationRadioButton.setSelected(false);
			this.crossValidationTextField.setEnabled(false);
		}else if(ae.getSource().equals(this.crossValidationRadioButton)){
			this.posFileRadioButton.setSelected(false);
			this.posBrowseButton.setEnabled(false);
			this.crossValidationRadioButton.setSelected(true);
			this.crossValidationTextField.setEnabled(true);
		}else if(ae.getSource().equals(this.allWeightOneRadioButton)){
			this.allWeightOneRadioButton.setSelected(true);
			this.pwmWithGapRadioButton.setSelected(false);
			this.weightRelativeToEntropyRadioButton.setSelected(false);
			calculatePWM(this.trainingFileFastaFileReader);
		}else if(ae.getSource().equals(this.pwmWithGapRadioButton)){
			this.allWeightOneRadioButton.setSelected(false);
			this.pwmWithGapRadioButton.setSelected(true);
			this.weightRelativeToEntropyRadioButton.setSelected(false);
			calculatePWM(this.trainingFileFastaFileReader);
		}else if(ae.getSource().equals(this.weightRelativeToEntropyRadioButton)){
			this.allWeightOneRadioButton.setSelected(false);
			this.pwmWithGapRadioButton.setSelected(false);
			this.weightRelativeToEntropyRadioButton.setSelected(true);
			calculatePWM(this.trainingFileFastaFileReader);
		}
	}
	private void crossValidate(int foldNumber, FastaFileReader fileFastaFileReader, 
			BufferedWriter output)throws Exception{		
		for(int x = 0; x < foldNumber; x++){
			FastaFileReader trainingFile = new FastaFileReader();
			FastaFileReader testFile = new FastaFileReader();
			for(int y = 0; y < fileFastaFileReader.size(); y++){
				if(y%foldNumber == x){
					testFile.add(fileFastaFileReader.getDataAt(y));
				}else{
					trainingFile.add(fileFastaFileReader.getDataAt(y));
				}
			}
			calculatePWM(trainingFile);
			evaluate(output, testFile, "pos");
		}
	}
	
	private void crossValidateEnergy(int foldNumber, FastaFileReader fileFastaFileReader, 
			BufferedWriter output,ScoringMatrix scoringMatrix,
			int maxDist)throws Exception{		
		for(int x = 0; x < foldNumber; x++){
			FastaFileReader trainingFile = new FastaFileReader();
			FastaFileReader testFile = new FastaFileReader();
			for(int y = 0; y < fileFastaFileReader.size(); y++){
				if(y%foldNumber == x){
					testFile.add(fileFastaFileReader.getDataAt(y));
				}else{
					trainingFile.add(fileFastaFileReader.getDataAt(y));
				}
			}
			computeMinEnergyMatrix(trainingFile,false);
			evaluateEnergy(output, testFile, "pos",scoringMatrix,maxDist);
		}
	}
	
	private void showResults(double threshold){
		//This is PWM pane hence range does not apply
		ClassifierResults pwmResults = new ClassifierResults(false,0);
		PredictionStats pwmStats = 
			new PredictionStats(outputDirectory + File.separator + 
					"PWMResults.score",0,threshold);		
		pwmStats.updateDisplay(pwmResults, outputTextArea, false);
	}
	private void showEnergyResults(double threshold){
		//This is PWM pane hence range does not apply
		ClassifierResults energyResults = new ClassifierResults(false,0);
		PredictionStats energyStats = new PredictionStats(
				outputDirectory + File.separator + "MinEnergyResults.score",0,threshold);		
		energyStats.updateDisplay(energyResults, outputTextArea, false);
	}
	private void evaluate(BufferedWriter output, FastaFileReader fileFastaFileReader, String _class) 
	throws Exception{		
		for(int x = 0; x < fileFastaFileReader.size(); x++){
			FastaFormat temp = fileFastaFileReader.getDataAt(x);
			output.write(temp.getHeader());
			output.newLine();
			output.write(temp.getSequence());
			output.newLine();
			output.write(_class + ",0=" + pwmTableModel.getPWMScore(temp.getSequence(), 
					this.positionsToConsider));
			output.newLine();
		}		
	}
	
	private void evaluateEnergy(BufferedWriter output, FastaFileReader fileFastaFileReader, 
			String _class, ScoringMatrix scoringMatrix,
			int maxInfluenceDistance) throws Exception{		
		for(int x = 0; x < fileFastaFileReader.size(); x++){
			FastaFormat temp = fileFastaFileReader.getDataAt(x);
			output.write(temp.getHeader());
			output.newLine();
			output.write(temp.getSequence());
			output.newLine();
			output.write(_class + ",0=" + this.pairwiseEnergyTableModel.getEnergy(temp.getSequence(), 
					scoringMatrix, maxInfluenceDistance));					
			output.newLine();
		}		
	}
	private void calculatePWM(FastaFileReader trainingFileFastaFileReader){
		Hashtable<Character, ArrayList<MyInteger>> pwmHashtable = new Hashtable<Character, 
		ArrayList<MyInteger>>();
		int maxSequenceLength = 0;
		//Have two run for the sequence file
		//first run, get the maximum sequence length and all the different characters in the sequences		
		for(int x = 0; x < trainingFileFastaFileReader.size(); x++){
			String sequence = trainingFileFastaFileReader.getDataAt(x).getSequence();
			if(sequence.length() > maxSequenceLength)
				maxSequenceLength = sequence.length();
			sequence = sequence.toUpperCase();
			for(int y = 0; y < sequence.length(); y++){
				char tempChar = sequence.charAt(y);
				if(pwmHashtable.containsKey(tempChar) == false){
					//yet to encounter this character
					pwmHashtable.put(tempChar, new ArrayList<MyInteger>());
				}				
			}
		}		
		for(Enumeration<Character> e = pwmHashtable.keys(); e.hasMoreElements();){    		
    		ArrayList<MyInteger> tempArrayList = pwmHashtable.get(e.nextElement());
    		for(int x = 0; x < maxSequenceLength; x++){
    			tempArrayList.add(new MyInteger());
    		}
    	}
		//second run, calculate the frequency
		for(int x = 0; x < trainingFileFastaFileReader.size(); x++){
			String sequence = trainingFileFastaFileReader.getDataAt(x).getSequence();
			sequence = sequence.toUpperCase();
			for(int y = 0; y < sequence.length(); y++){
				char tempChar = sequence.charAt(y);				
				ArrayList<MyInteger> positionFrequencyArrayList = pwmHashtable.get(tempChar);					
				positionFrequencyArrayList.get(y).increment();
			}
		}
		
		int showFreq = -1;
		if(frequencyRadioButton.isSelected()){
			showFreq = 0;
		}else if(percentageRadioButton.isSelected()){
			showFreq = 1;
		}		
		pwmTableModel.update(pwmHashtable, maxSequenceLength, showFreq);
		this.positionsToConsider = new BitSet(maxSequenceLength);
		if(this.pwmWithGapRadioButton.isSelected())
			pwmTableModel.calulateEntropy(maxSequenceLength, this.positionsToConsider, 
					Double.parseDouble(this.entropyThresholdTextField.getText()), false);
		else if(this.allWeightOneRadioButton.isSelected())
			pwmTableModel.calulateEntropy(maxSequenceLength, this.positionsToConsider, 1.0, false);
		else if(this.weightRelativeToEntropyRadioButton.isSelected())
			pwmTableModel.calulateEntropy(maxSequenceLength, this.positionsToConsider, 1.0, true);
		
		//third run, find the min,max possible value and entropy of each position based on the PWM
		double minPWMValue = 0.0;
		double maxPWMValue = 0.0;
		for(int x = 0; x < maxSequenceLength; x++){	
			if(this.positionsToConsider.get(x) == false)
				continue;
    		double currentMinValue = Double.POSITIVE_INFINITY;
    		double currentMaxValue = Double.NEGATIVE_INFINITY;
    		for(Enumeration<Character> e = pwmHashtable.keys(); e.hasMoreElements();){
    			ArrayList<MyInteger> positionFrequencyArrayList = pwmHashtable.get(e.nextElement());
    			double currentValue = positionFrequencyArrayList.get(x).getPercentageValue();  
    			if(currentMinValue > currentValue)
    				currentMinValue = currentValue;
    			if(currentMaxValue < currentValue)
    				currentMaxValue = currentValue;
    		}    		    		
    		minPWMValue += (currentMinValue * this.pwmTableModel.getWeightsAt(x));
    		maxPWMValue += (currentMaxValue * this.pwmTableModel.getWeightsAt(x));
    	}    	    	    	
		MyInteger.minPWMValue = minPWMValue;
		MyInteger.maxPWMValue = maxPWMValue;				
	}	
}

class MyInteger{
	double value;	
	static double minPWMValue;
	static double maxPWMValue;
	static double minEnergyValue;
	static double maxEnergyValue;
	static int totalSequence;
	
	public MyInteger(){
		value = 0;		
	}
	
	public void increment(){
		value++;
	}
	
	public void normalize(double total){
		this.value /= total;
		this.value*=100.0;
		DecimalFormat df = new DecimalFormat("#.###");
		this.value = Double.parseDouble(df.format(this.value));
	}
	
	public void increment(double value){
		this.value += value;
	}
	
	public double getValue(){
		return value;
	}
	
	public double negateAndAverageOut(int size){
		this.value *= -1;
		this.value /= size;
		if(this.value < 0)
			this.value = 0;
		this.value = this.value * this.value * this.value;
		return this.value;
	}
	
	
	public double getPercentageValue(){
		return (value + 0.0) / (totalSequence + 0.0);
	}
}

class PWMViewPort extends JViewport{
	static final long serialVersionUID = sirius.Sirius.version;
	public PWMViewPort(){		
	}
    public void paintChildren(Graphics g){
        super.paintChildren(g);                		        		
    }

    public Color getBackground(){
        Component c = getView();
        return c == null ? super.getBackground() : c.getBackground();
    }        		   
}