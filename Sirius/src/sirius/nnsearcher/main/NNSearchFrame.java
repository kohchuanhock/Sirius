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
package sirius.nnsearcher.main;


import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
//import java.text.DecimalFormat;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import sirius.main.ApplicationData;
import sirius.misc.randomizesequence.RandomizeSequencePane;
import sirius.trainer.features.Feature;
import sirius.trainer.features.GenerateFeatures;
import sirius.trainer.features.gui.geneticalgorithm.GeneticAlgorithm;
import sirius.trainer.features.gui.geneticalgorithm.RunGA;
import sirius.trainer.features.gui.geneticalgorithm.SettingsDialog;
import sirius.trainer.features.gui.geneticalgorithm.ViewFeaturesDialog;
import sirius.trainer.main.SiriusSettings;
import sirius.trainer.main.StatusPane;
import sirius.trainer.step1.DefineDataPane;
import sirius.trainer.step2.FeatureTableModel;
import sirius.utils.FastaFileReader;
import sirius.utils.FastaFormat;
//import weka.attributeSelection.ChiSquaredAttributeEval;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

public class NNSearchFrame extends JInternalFrame implements ActionListener, MouseListener{
	static final long serialVersionUID = sirius.Sirius.version;
		
	private JTextField workingDirectoryTextField = new JTextField(10);
	private JTextField queryTextField = new JTextField(10);
	private File queryFile;	
	private JTextField databaseTextField = new JTextField(10);
	private File databaseFile;	
	private JTextField featureTextField = new JTextField(10);
	private File featureFile;
	private JButton runWithoutFeaturesButton = new JButton("Run GA");
	private JButton runButton = new JButton(" Run ");
	private JButton saveButton = new JButton("Save Score");
	private JButton saveFastaButton;
	private JButton saveArffButton = new JButton("Save Arff");
	private JButton loadArffButton = new JButton("Load Arff");
	private JButton viewFeatureButton = new JButton("View Features");
	private JButton viewGAButton = new JButton("View GA");
	private String loadArffString;	
	private StatusPane statusPane;	
	private JComboBox countingStyleComboBox;
	private JComboBox scoringMatrixComboBox;	
	private ApplicationData applicationData = new ApplicationData((StatusPane)null);
	private FeatureTableModel featureTableModel = new FeatureTableModel(null, true, true);			
	private Instances queryInstances;
	private Instances databaseInstances;
	private Instances filteredQueryInstances;
	private Instances filteredDatabaseInstances;		
	private double weightsOfDimensions[];
	private double range[];		
	private FastaFormatWithScoreTableModel fastaFormatWithScoreTableModel;	
	
	private JFrame parent;	
	private JRadioButton dnaSequenceRadioButton;
	private JRadioButton proteinSequenceRadioButton;	
	private JTable scoreTable;
	
	private boolean pValueComputing; 
	private SettingsDialog dialog = new SettingsDialog(false,null);	
	private ViewFeaturesDialog viewFeaturesDialog;
	private RunGA runGA = new RunGA(false);
	
	public NNSearchFrame(JFrame mainFrame) {
		super("NNSearcher",true,true,true,true);				
		
		this.parent = mainFrame;
					
		this.queryTextField.addMouseListener(this);
		this.queryTextField.setEnabled(false);								
		this.databaseTextField.setEnabled(false);
		this.databaseTextField.addMouseListener(this);						
		this.featureTextField.addMouseListener(this);
		this.featureTextField.setEnabled(false);	
		this.workingDirectoryTextField.setEnabled(false);
		this.workingDirectoryTextField.addMouseListener(this);
		this.viewFeatureButton.addActionListener(this);
		
		this.viewFeaturesDialog = new ViewFeaturesDialog(this.featureTableModel,this.workingDirectoryTextField, "Score");
				
		JPanel inputAndButtonPanel = new JPanel();
		inputAndButtonPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Input"),BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		inputAndButtonPanel.add(new JLabel("Working Directory: "));
		inputAndButtonPanel.add(this.workingDirectoryTextField);
		inputAndButtonPanel.add(new JLabel("Query: "));
		inputAndButtonPanel.add(this.queryTextField);
		inputAndButtonPanel.add(new JLabel("Database: "));
		inputAndButtonPanel.add(this.databaseTextField);
		inputAndButtonPanel.add(this.runWithoutFeaturesButton);
		inputAndButtonPanel.add(new JLabel("Features: "));
		inputAndButtonPanel.add(this.featureTextField);
		inputAndButtonPanel.add(this.runButton);
		
		this.saveArffButton.addActionListener(this);		
		this.loadArffButton.addActionListener(this);
		this.loadArffString = null;		
		this.runWithoutFeaturesButton.addActionListener(this);
		this.runButton.addActionListener(this);		
		this.saveButton.addActionListener(this);
		this.saveFastaButton = new JButton("Save As Fasta");
		this.saveFastaButton.addActionListener(this);		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(this.loadArffButton);
		buttonPanel.add(this.saveArffButton);
		buttonPanel.add(this.saveButton);
		buttonPanel.add(this.saveFastaButton);															
		
		scoringMatrixComboBox = new JComboBox();
		scoringMatrixComboBox.addItem("Identity");
		scoringMatrixComboBox.addItem("Blosum 62");
		scoringMatrixComboBox.addItem("Structure-Derived");											
		
		countingStyleComboBox = new JComboBox();
		countingStyleComboBox.addItem("+1");
		countingStyleComboBox.addItem("+Score");					
				
		dnaSequenceRadioButton = new JRadioButton("DNA");
		proteinSequenceRadioButton = new JRadioButton("Protein");
		dnaSequenceRadioButton.addActionListener(this);
		proteinSequenceRadioButton.addActionListener(this);
		this.proteinSequenceRadioButton.setSelected(true);
		applicationData.setSequenceType("PROTEIN");
		
		JPanel scoreAndCountPanel = new JPanel();
		scoreAndCountPanel.setBorder(BorderFactory.createCompoundBorder(
		    	BorderFactory.createTitledBorder("Settings"),
		    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		scoreAndCountPanel.add(new JLabel("Scoring Matrix"));
		scoreAndCountPanel.add(this.scoringMatrixComboBox);
		scoreAndCountPanel.add(new JLabel("Counting Style: "));
		scoreAndCountPanel.add(this.countingStyleComboBox);
		scoreAndCountPanel.add(new JLabel("Sequence Type: "));
		scoreAndCountPanel.add(this.dnaSequenceRadioButton);
		scoreAndCountPanel.add(this.proteinSequenceRadioButton);
		scoreAndCountPanel.add(this.viewFeatureButton);
		scoreAndCountPanel.add(this.viewGAButton);
		this.viewGAButton.setEnabled(false);
		this.viewGAButton.addActionListener(this);
				
		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.add(inputAndButtonPanel, BorderLayout.CENTER);
		northPanel.add(scoreAndCountPanel, BorderLayout.SOUTH);
		
		JPanel centerPanel = new JPanel(new BorderLayout());
				
		JPanel outputPanel = new JPanel(new BorderLayout());
		outputPanel.setBorder(BorderFactory.createCompoundBorder(
		    	BorderFactory.createTitledBorder("Output"),
		    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		this.fastaFormatWithScoreTableModel = new FastaFormatWithScoreTableModel(true);
		scoreTable = new JTable(this.fastaFormatWithScoreTableModel);
		JScrollPane scoreTableScrollPane = new JScrollPane(scoreTable);
		outputPanel.add(scoreTableScrollPane, BorderLayout.CENTER);
		scoreTable.getColumnModel().getColumn(0).setMaxWidth(50);
		scoreTable.getColumnModel().getColumn(2).setMaxWidth(200); 
		scoreTable.getColumnModel().getColumn(3).setPreferredWidth(200);
		scoreTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scoreTable.setRowSelectionAllowed(false);
		outputPanel.add(buttonPanel, BorderLayout.SOUTH);
				
		JPanel eastPanel = new JPanel(new BorderLayout());
		
		
		this.statusPane = new StatusPane("Ready.");
		FilterPanel filterPanel = new FilterPanel(mainFrame, this.fastaFormatWithScoreTableModel, this.applicationData, this.statusPane);			
		eastPanel.add(filterPanel, BorderLayout.CENTER);				
		centerPanel.add(outputPanel, BorderLayout.CENTER);
		centerPanel.add(eastPanel, BorderLayout.EAST);
		
		JPanel southPanel = new JPanel(new GridLayout(1,1));
		
		southPanel.add(this.statusPane);				
		
		this.setLayout(new BorderLayout());		
		this.add(northPanel, BorderLayout.NORTH);
		this.add(centerPanel, BorderLayout.CENTER);
		this.add(southPanel, BorderLayout.SOUTH);
		
		this.fastaFormatWithScoreTableModel.setConstraintsData(filterPanel.getMustHaveTableModel().getData());
	}

	
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource().equals(this.runButton)){
			this.pValueComputing = true;
			run(false);
		}else if(ae.getSource().equals(this.saveButton)){
			save(false);
		}else if(ae.getSource().equals(this.saveFastaButton)){
			save(true);
		}else if(ae.getSource().equals(this.loadArffButton)){		
			loadArff();
		}else if(ae.getSource().equals(this.saveArffButton)){
			saveArff();
		}else if(ae.getSource().equals(this.dnaSequenceRadioButton)){
			this.proteinSequenceRadioButton.setSelected(false);
			applicationData.setSequenceType("DNA");					
		}else if(ae.getSource().equals(this.proteinSequenceRadioButton)){
			this.dnaSequenceRadioButton.setSelected(false);
			applicationData.setSequenceType("PROTEIN");
		}else if(ae.getSource().equals(this.runWithoutFeaturesButton)){			
			if(this.runWithoutFeaturesButton.getText().equals("Stop GA"))
				stopGA();
			else
				runGA();
		}else if(ae.getSource().equals(this.viewFeatureButton)){
			viewFeatures();
		}else if(ae.getSource().equals(this.viewGAButton)){
			viewGA();
		}
	}
	
	private void viewGA(){
		this.dialog.setVisible(true);
	}
	
	private void stopGA(){
		this.runGA.setValue(false);
		this.statusPane.setSuffix(" - Will terminate @ the end of this Generation!");
	}
	
	private void viewFeatures(){		
		this.viewFeaturesDialog.pack();		
		this.viewFeaturesDialog.setLocationRelativeTo(this);
		this.viewFeaturesDialog.setVisible(true);
	}
	
	private void setWorkingDirectory(){
		String lastFastaFileLocation = SiriusSettings.getInformation("LastNNSearchOutputLocation: ");    	
	    JFileChooser fc = new JFileChooser(lastFastaFileLocation);
	    fc.setDialogTitle("Set Working Directory");
	    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    fc.setAcceptAllFileFilterUsed(false);	   
		int returnVal = fc.showOpenDialog(this);		
		if(returnVal == JFileChooser.APPROVE_OPTION) {	        			
			SiriusSettings.updateInformation("LastNNSearchOutputLocation: ", fc.getSelectedFile().toString());
			this.workingDirectoryTextField.setText(fc.getSelectedFile().toString());
		}else{
			//status.setText("Open command is cancelled by user.");
		}
	}
	
	public boolean runNNSearchGA(boolean runFromUI, String workingDirectory,String queryLocation, String databaseLocation, int scoringMatrixIndex, int countingStyleIndex){
		if(runFromUI){
			this.viewGAButton.setEnabled(true);
    		this.dialog.setEnabled(false);
    		this.runWithoutFeaturesButton.setText("Stop GA");
		}else{
			this.databaseFile = new File(databaseLocation);
		}
    	ApplicationData appData = new ApplicationData();
    	//STEP1:
    	appData.setWorkingDirectory(workingDirectory);
    	//Check if working directory exists - if not, create it
    	File file=new File(appData.getWorkingDirectory());
    	boolean exists = file.exists();
    	if (!exists) {
    		boolean success = file.mkdirs();
    		if(!success){
    			if(runFromUI)
    				this.statusPane.setText("Unable to create Working Directory: " + appData.getWorkingDirectory());
    			return false;
    		}
    	}		        	
    	DefineDataPane step1Pane = new DefineDataPane(null, null, appData);		        	    	
    	step1Pane.addFileMethod(queryLocation, null, step1Pane.getPositiveStep1TableModel());		        			        			        	
    	step1Pane.addFileMethod(databaseLocation, null, step1Pane.getNegativeStep1TableModel());		        	
		appData.setPositiveStep1TableModel(step1Pane.getPositiveStep1TableModel());
		appData.setNegativeStep1TableModel(step1Pane.getNegativeStep1TableModel());
		appData.setDatasetsValue(1,step1Pane.getPositiveStep1TableModel().getNumOfSequences(0),-1,-1,-1,-1,1,step1Pane.getNegativeStep1TableModel().getNumOfSequences(0),-1,-1,-1,-1);			    		
		appData.setSequenceType("PROTEIN");
		//Also store the sequenceLengthInformation
		step1Pane.updateSequenceInformation();	
		appData.setScoringMatrixIndex(scoringMatrixIndex);
		appData.setCountingStyleIndex(countingStyleIndex);		    		
		//GeneticAlgorithm
		//Check if output directory exists - if not, create it
    	file = new File(appData.getWorkingDirectory());
    	exists = file.exists();
    	if (!exists) {
    		boolean success = file.mkdirs();
    		if(!success){
    			if(runFromUI)
    				NNSearchFrame.this.statusPane.setText("Unable to create Working Directory: " + appData.getWorkingDirectory());    
    			return false;
    		}
    	}		    
    	if(runFromUI)
    		appData.setStatusPane(this.statusPane);
    	this.applicationData = appData;		
		this.runGA.setValue(true);
		try{
		new GeneticAlgorithm(this.dialog,appData.getWorkingDirectory(), appData,null,this.featureTableModel,null,this.runGA, -1, -1);
		}catch(Exception e){e.printStackTrace(); return false;}
		return true;
	}
	
	private void runGA(){	
		if((this.databaseFile == null)|| this.queryFile == null || this.workingDirectoryTextField.getText().length() == 0){
			JOptionPane.showMessageDialog(null,"Inputs are not all properly set","Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
		dialog.isApplyPressed = false;
		dialog.setModal(true);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
		if(dialog.isApplyPressed){
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
		        @Override
		        public Void doInBackground() {
		            // Call complicated code here
		            // If you want to return something, change the generic type to
		            // something other than Void.
		            // This method's return value will be available via get() once the
		            // operation has completed.			
		        	runNNSearchGA(true, NNSearchFrame.this.workingDirectoryTextField.getText(), NNSearchFrame.this.queryTextField.getText(), NNSearchFrame.this.databaseTextField.getText(), 
		        			NNSearchFrame.this.scoringMatrixComboBox.getSelectedIndex(),NNSearchFrame.this.countingStyleComboBox.getSelectedIndex());
		        	return null;
		        }

		        @Override
		        protected void done() {
		            // get() would be available here if you want to use it
		        	statusPane.setPrefix("");
		        	statusPane.setSuffix("");
		        	statusPane.setText("Done!");
		        	runWithoutFeaturesButton.setText("Run GA");
		        	NNSearchFrame.this.viewGAButton.setEnabled(false);
		        	NNSearchFrame.this.dialog.setEnabled(true);		        	   
		        	NNSearchFrame.this.computePScore(true);
		        }
		    };
		    this.statusPane.setText("Drawing.. Please wait..");		
		    worker.execute();	
		}
	}
	
	public void computePScore(boolean fromUI){//this computes based on the fact that if value is larger than min or smaller than max then consider match
		//Generate features for DB
		if(fromUI)
			this.statusPane.setPrefix("Computing Database Scores..");
		try{
			this.fastaFormatWithScoreTableModel.resetData();
			this.applicationData.setStep2FeatureTableModel(this.featureTableModel);
			new GenerateFeatures(this.applicationData, this.databaseFile, "Database.arff", null, -1, -1);
			FastaFileReader fastaFileReader = new FastaFileReader(this.databaseFile.getAbsolutePath());
			this.databaseInstances = new Instances(new BufferedReader(new FileReader("Database.arff")));
			for(int x = 0; x < this.databaseInstances.numInstances(); x++){		
				if(fromUI)
					this.statusPane.setText((x+1) + " / " + this.databaseInstances.numInstances());
				FastaFormat fastaFormat = fastaFileReader.getDataAt(x);				
				double score = 1.0;
				for(int y = 0; y < this.featureTableModel.getRowCount(); y++){
					double pValue = 1 - this.featureTableModel.getFeatureDataAt(y).getScore();
					if(this.featureTableModel.getFeatureDataAt(y).getMinCutOff() != null){
						if(this.databaseInstances.instance(x).value(y) >= this.featureTableModel.getFeatureDataAt(y).getMinCutOff())
							score *= pValue;
					}else if(this.featureTableModel.getFeatureDataAt(y).getMaxCutOff() != null){
						if(this.databaseInstances.instance(x).value(y) <= this.featureTableModel.getFeatureDataAt(y).getMaxCutOff())
							score *= pValue;
					}else{
						throw new Error("CANNOT BE HERE!!");
					}
				}
				//change from P-Value to E-Value
				score *= this.databaseInstances.numInstances();
				this.fastaFormatWithScoreTableModel.add(fastaFormat, score, score);
			}						
			this.fastaFormatWithScoreTableModel.sortInvert();
			this.fastaFormatWithScoreTableModel.update();
			if(fromUI){
				this.statusPane.setPrefix("");
				this.statusPane.setSuffix("");
				this.statusPane.setText("Done!");
			}
			try{
				BufferedWriter output = new BufferedWriter(new FileWriter(this.applicationData.getWorkingDirectory() + File.separator + "E-Score.score"));	
				this.fastaFormatWithScoreTableModel.save(output, false);
				output.close();
			}catch(Exception e){
				e.printStackTrace();
			}			
		}catch(Exception e){e.printStackTrace();}
	}	
	
	private void loadArff(){
		try{
			//applicationData.hasLocationIndexBeenSet = false;
			JFileChooser fc;	    	
	    	String lastLocation = SiriusSettings.getInformation("LastNNSearchOutputLocation: ");
    		if(lastLocation == null)
    			fc = new JFileChooser();
    		else
    			fc = new JFileChooser(lastLocation);	    	
	        FileNameExtensionFilter filter = new FileNameExtensionFilter(
	            "Arff File", "arff");
	        fc.setFileFilter(filter);			        			        			    	
			int returnVal = fc.showOpenDialog(parent);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
	            SiriusSettings.updateInformation("LastNNSearchOutputLocation: ", file.getAbsolutePath());
	            this.loadArffString = file.getAbsolutePath();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void saveArff(){
		JFileChooser fc;	    	
		//if working directory not set then look at the Sirius Settings file
		String lastLocation = SiriusSettings.getInformation("LastNNSearchOutputLocation: ");
		if(lastLocation == null)
			fc = new JFileChooser();
		else
			fc = new JFileChooser(lastLocation);	    				    
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Arff File", "arff");
        fc.setFileFilter(filter);			        			        			    	
		int returnVal = fc.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();			           
			String savingFilename = file.getAbsolutePath();
			if(savingFilename.indexOf(".arff") == -1)
				savingFilename += ".arff";
			SiriusSettings.updateInformation("LastNNSearchOutputLocation: ", savingFilename);
			try{
				BufferedWriter output = new BufferedWriter(new FileWriter(savingFilename));	
				BufferedReader input = new BufferedReader(new FileReader("Database.arff"));
				String line;
				while((line = input.readLine()) != null){
					output.write(line);
					output.newLine();
				}
				input.close();
				output.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private void save(boolean isFasta){
		JFileChooser fc;	    	
		//if working directory not set then look at the Sirius Settings file
		String lastLocation = SiriusSettings.getInformation("LastNNSearchOutputLocation: ");
		if(lastLocation == null)
			fc = new JFileChooser();
		else
			fc = new JFileChooser(lastLocation);	    	
		
        FileNameExtensionFilter filter;
        if(isFasta == false)
        	filter = new FileNameExtensionFilter("Score File", "scores");
        else
        	filter = new FileNameExtensionFilter("Fasta File", "fasta");
        fc.setFileFilter(filter);			        			        			    	
		int returnVal = fc.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();			           
			String savingFilename = file.getAbsolutePath();
			if(isFasta == false){
				if(savingFilename.indexOf(".scores") == -1)
					savingFilename += ".scores";
			}else{
				if(savingFilename.indexOf(".fasta") == -1)
					savingFilename += ".fasta";
			}
			SiriusSettings.updateInformation("LastNNSearchOutputLocation: ", savingFilename);
			try{
				BufferedWriter output = new BufferedWriter(new FileWriter(savingFilename));	
				this.fastaFormatWithScoreTableModel.save(output, isFasta);
				output.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private void loadQueryFile(){    	
		String lastFastaFileLocation = SiriusSettings.getInformation("LastNNSearchOutputLocation: ");    	
	    JFileChooser fc = new JFileChooser(lastFastaFileLocation);
	    FileNameExtensionFilter filter = new FileNameExtensionFilter( "Fasta Files", "fasta");
		fc.setFileFilter(filter);	
		int returnVal = fc.showOpenDialog(this);		
		if(returnVal == JFileChooser.APPROVE_OPTION) {	        
			this.queryFile = fc.getSelectedFile();
			SiriusSettings.updateInformation("LastNNSearchOutputLocation: ", this.queryFile.getAbsolutePath());
			this.queryTextField.setText(this.queryFile.getAbsolutePath());
		}else{
			//status.setText("Open command is cancelled by user.");
		}
	}
	
	private void loadDatabaseFile(){    	
		String lastFastaFileLocation = SiriusSettings.getInformation("LastNNSearchOutputLocation: ");    	
	    JFileChooser fc = new JFileChooser(lastFastaFileLocation);
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("Fasta Files", "fasta");
		fc.setFileFilter(filter);	
		int returnVal = fc.showOpenDialog(this);		
		if(returnVal == JFileChooser.APPROVE_OPTION) {	        
			this.databaseFile = fc.getSelectedFile();
			SiriusSettings.updateInformation("LastNNSearchOutputLocation: ", this.databaseFile.getAbsolutePath());
			this.databaseTextField.setText(this.databaseFile.getAbsolutePath());
			this.loadArffString = null;
		}else{
			//status.setText("Open command is cancelled by user.");
		}
	}
	
	private void loadFeatureFile(){    	
		String lastFastaFileLocation = SiriusSettings.getInformation("LastNNSearchOutputLocation: ");    	
	    JFileChooser fc = new JFileChooser(lastFastaFileLocation);
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("Feature File", "features");
		fc.setFileFilter(filter);	
		int returnVal = fc.showOpenDialog(this);		
		if(returnVal == JFileChooser.APPROVE_OPTION) {	        
			this.featureFile = fc.getSelectedFile();
			SiriusSettings.updateInformation("LastNNSearchOutputLocation: ", this.featureFile.getAbsolutePath());
			this.featureTextField.setText(this.featureFile.getAbsolutePath());
		}else{
			//status.setText("Open command is cancelled by user.");
		}
	}
	
	private void run(final boolean computingPValue){
		final long numOfRUNS = 1;		
		if((this.databaseFile == null)|| this.queryFile == null || this.featureFile == null || this.workingDirectoryTextField.getText().length() == 0){
			JOptionPane.showMessageDialog(null,"Inputs are not all properly set","Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(applicationData.getOneThread() == null){
    		applicationData.setOneThread(new Thread(){	      	
			public void run(){
				try{	
					Random rng = new Random(715);
					long x;
					for(x = 0; x < numOfRUNS && pValueComputing; x++){
						//Generate the randomizedFile
						File randomizedFile = null;
						if(computingPValue){
							generateRandomSequence(randomizedFile, rng);
							randomizedFile = new File("randomized.fasta");
						}
						
						if(computingPValue == false){
							statusPane.setText("Runs: " + (x+1) + " / " + numOfRUNS + "  Load Features");
							loadFeatures();
						}
						statusPane.setText("Runs: " + (x+1) + " / " + numOfRUNS + "  Generate Features");
						if(computingPValue == false)
							generateFeatures(queryFile, x+1, numOfRUNS);
						else
							generateFeatures(randomizedFile, x+1, numOfRUNS);
						statusPane.setText("Runs: " + (x+1) + " / " + numOfRUNS + "  Filter Features");//Does normalization of data
						filterFeatures();
						statusPane.setText("Runs: " + (x+1) + " / " + numOfRUNS + "  Calculating Weights of Dimensions");
						//Currently, when calculating the weights of Dimension does not take into consideration database sequences
						//This is something I can do in the near future
						calculateWeightsOfDimensions();
						//statusLabel.setText("Find the Centroid");
						//findCentroid();
						statusPane.setText("Runs: " + (x+1) + " / " + numOfRUNS + "  Calculate Weighted Distance");
						//calculate the distance of each sequence in the database from centroid using weighted Dimensions 
						calculateWeightedDistance(computingPValue);								
					}
					if(computingPValue && x > 0){
						fastaFormatWithScoreTableModel.computePValue(x);
						fastaFormatWithScoreTableModel.update();
					}
					statusPane.setText("Done");
					//pValueComputeButton.setText("Compute");
				}catch(Exception e){
					JOptionPane.showMessageDialog(null,"Exception Thrown","Exception",JOptionPane.ERROR_MESSAGE);    
					e.printStackTrace();
				}
				applicationData.setOneThread(null);
			}});						
      		applicationData.getOneThread().setPriority(Thread.MIN_PRIORITY); // UI has most priority
      		applicationData.getOneThread().start();	      		
		}
		else{
			JOptionPane.showMessageDialog(null,"Can't start the Run now,\n"
					+ "currently busy with other IO","Load Features", JOptionPane.WARNING_MESSAGE);
		}
	}	
	
	private void loadFeatures() throws Exception{					
    	BufferedReader in = new BufferedReader(new FileReader (this.featureFile.getAbsolutePath()));
    	boolean foundAtLeastOne = false;
    	String line;
    	this.featureTableModel.setEmpty();
    	boolean rejectedAtLeastOne = false;
    	while ((line = in.readLine()) != null){
    		if(line.indexOf("Step 2: ") != -1){
    			foundAtLeastOne = true;            			
    			//For load settings
    			Feature tempData = Feature.loadSettings(applicationData,this.featureFile.getParent(), line);
    			if(tempData != null)
    				this.featureTableModel.add(tempData);
    			else
    				rejectedAtLeastOne = true;
    		}
    	}
    	if(rejectedAtLeastOne == true)
    		JOptionPane.showMessageDialog(null,"Note that not all features in " + this.featureFile.getAbsolutePath() + " are added.\n" + 
    			"Rejected features are those with windowFrom < 0 because the sequences +1_Index is -1","Warning", 
    			JOptionPane.WARNING_MESSAGE);
    	if(!foundAtLeastOne)
    		JOptionPane.showMessageDialog(null,this.featureFile.getAbsolutePath() + " does not contains Features","Load Features",
    				JOptionPane.WARNING_MESSAGE);
    	in.close();
	}			

	private void calculateWeightsOfDimensions() throws Exception{
		//Version 2.33 method would be based on discussion with Limsoon
		//DecimalFormat df = new DecimalFormat("0.####");
		double[] V = new double[this.filteredQueryInstances.numAttributes() - 1];
		double[] A = new double[this.filteredQueryInstances.numAttributes() - 1];
		double[] B = new double[this.filteredQueryInstances.numAttributes() - 1];
		range = new double[this.filteredDatabaseInstances.numAttributes() - 1];
		double maxA = 0;
		for(int x = 0; x < this.filteredDatabaseInstances.numAttributes() - 1; x++){
			double databaseStddev = this.filteredDatabaseInstances.attributeStats(x).numericStats.stdDev;
			double databaseMean = this.filteredDatabaseInstances.attributeStats(x).numericStats.mean;
			double queryMean = this.filteredQueryInstances.attributeStats(x).numericStats.mean;
			double queryStddev = this.filteredQueryInstances.attributeStats(x).numericStats.stdDev;
			range[x] = this.filteredDatabaseInstances.attributeStats(x).numericStats.max -
				this.filteredDatabaseInstances.attributeStats(x).numericStats.min;
			
			//Take (query mean - database mean) / database stddev -> feature selection, select those feature with query mean far from database mean
			if(databaseStddev == 0.0){
				//With database stddev == 0, means that every proteins in database agrees on this. Hence we should throw this feature away				
				V[x] = 0.0;//throw this feature away								
			}else{
				//general case
				double cutoff = 0.5;//Limsoon randomly suggested 2.0. I should play with many other values.
				double stdDevCutoff = 0.5;
				//Here, I divide by DBStdDev because if the spread is tight at the database 
				//then a slight diff of query and DB would be more interesting
				double differenceBetweenQueryAndDatabase = (queryMean - databaseMean) / databaseStddev;				
				if(differenceBetweenQueryAndDatabase < 0)
					differenceBetweenQueryAndDatabase *= -1;
				if(differenceBetweenQueryAndDatabase <= cutoff && queryStddev >= stdDevCutoff){
					V[x] = 0.0;//throw this feature away
				}else{
					if(queryStddev == 0.0){
						A[x] = -1;
						B[x] = (differenceBetweenQueryAndDatabase);
						V[x] = A[x] + B[x];
					}						
					else{
						A[x] = (1/queryStddev);
						B[x] = (differenceBetweenQueryAndDatabase);
						V[x] = A[x] + B[x];	
					}
					if(V[x] < 0 && A[x] != -1)						
						throw new Error("Error: V[x] < 0");
					if(A[x] > maxA)
						maxA = A[x];
					
				}
			}
		}
		//Set all those with V[x] = 0 to V[x] = minV;
		for(int x = 0; x < this.filteredQueryInstances.numAttributes() - 1; x++){
			if(A[x] == -1)
				A[x] = maxA;
		}
		//Normalize sum of A[x] to 0.5
		double a = 0.0;
		for(int x = 0; x < A.length; x++){
			if(A[x] != 1.0)
				a += A[x];
		}
		for(int x = 0; x < A.length; x++){
			A[x] = (A[x] / a) * 0.5;
		}
		//Normalize sum of B[x] to 0.5
		double b = 0.0;
		for(int x = 0; x < B.length; x++){
			if(B[x] != 1.0)
				b += B[x];
		}
		for(int x = 0; x < B.length; x++){
			B[x] = (B[x] / b) * 0.5;
			V[x] = A[x] + B[x];
		}
		double sumW = 0.0;
		this.weightsOfDimensions = new double[V.length];
		for(int x = 0; x < V.length; x++){
			//if(V[x] != 1.0)
				//this.weightsOfDimensions[x] = Double.parseDouble(df.format(V[x]));
			this.weightsOfDimensions[x] = V[x];
			//else
				//this.weightsOfDimensions[x] = 0.0;//set to 0.0 is equal to throwing the feature away
			sumW += this.weightsOfDimensions[x];						
		}
		System.out.println("Should be 1(or close): " + sumW);				
	}
	
	private void filterFeatures() throws Exception{		
		Normalize filter = new Normalize();
		Normalize filter2 = new Normalize();		
		filter.setInputFormat(this.queryInstances);
		filter2.setInputFormat(this.databaseInstances);
		this.filteredQueryInstances = Filter.useFilter(this.queryInstances, filter);
		if(this.filteredQueryInstances.numInstances() <= 2)
			this.filteredDatabaseInstances = Filter.useFilter(this.databaseInstances, filter2);
		else
			this.filteredDatabaseInstances = Filter.useFilter(this.databaseInstances, filter);
	}
	
	private void generateRandomSequence(File randomizedFile, Random rng) throws Exception{
		int randomNumber;		
		BufferedWriter output = new BufferedWriter(new FileWriter("randomized.fasta"));		
		FastaFileReader fastaFileReader = new FastaFileReader(this.queryFile.getAbsolutePath());
		for(int x = 0; x < this.queryInstances.numInstances(); x++){
			randomNumber = rng.nextInt();
			FastaFormat fastaFormat = fastaFileReader.getDataAt(x);
			output.write(fastaFormat.getHeader() + " RandomNo=" + randomNumber);
			output.newLine();
			output.write(RandomizeSequencePane.randomize(fastaFormat.getSequence(), randomNumber, 0));
			output.newLine();
		}
		output.close();				
	}

	private void generateFeatures(File query, long currentRun, long totalRun) throws Exception{				
		this.applicationData.setScoringMatrixIndex(scoringMatrixComboBox.getSelectedIndex());
		this.applicationData.setCountingStyleIndex(countingStyleComboBox.getSelectedIndex());		
		this.applicationData.setDataset1Instances(null);
		this.applicationData.setStep2FeatureTableModel(this.featureTableModel);			
		new GenerateFeatures(this.applicationData, query, "Query.arff", this.statusPane.getStatusLabel(), currentRun, totalRun);
		this.queryInstances = new Instances(new BufferedReader(new FileReader("Query.arff")));
		if(this.loadArffString == null){
			new GenerateFeatures(this.applicationData, this.databaseFile, "Database.arff", this.statusPane.getStatusLabel(), currentRun, totalRun);
			this.loadArffString = "Database.arff";
			this.databaseInstances = new Instances(new BufferedReader(new FileReader("Database.arff")));			
		}else{
			this.databaseInstances = new Instances(new BufferedReader(new FileReader(this.loadArffString)));			
		}		
	}
	
	private double computePValue(int x){//this is the old approach
		double pValue = 1.0;
		for(int y = 0; y < this.filteredDatabaseInstances.numAttributes() - 1; y++){
			if(this.weightsOfDimensions[y] == 0.0)
				continue;			
				double difference = (this.filteredDatabaseInstances.instance(x).value(y) - 
						this.filteredQueryInstances.attributeStats(y).numericStats.mean); 							
			if(difference < 0)
				difference *= -1;
			difference *= 2;		
			if(difference*2 <= range[y] && range[y] > 0 && difference > 0)				
				pValue *= difference / range[y];
		}
		return pValue;
	}
	
	private void calculateWeightedDistance(boolean computingPValue){
		if(computingPValue == false)
			this.fastaFormatWithScoreTableModel.resetData();
		FastaFileReader fastaFileReader = new FastaFileReader(this.databaseFile.getAbsolutePath());
		double topScore = -999999999;
		for(int x = 0; x < this.filteredDatabaseInstances.numInstances(); x++){			
			FastaFormat fastaFormat = fastaFileReader.getDataAt(x);				
			double totalDifference = 0.0;			
			for(int y = 0; y < this.filteredDatabaseInstances.numAttributes() - 1; y++){
				if(this.weightsOfDimensions[y] == 0.0)
					continue;				
				double difference = (this.filteredDatabaseInstances.instance(x).value(y) - 
						this.filteredQueryInstances.attributeStats(y).numericStats.mean); 							
				if(difference < 0)
					difference *= -1;
				totalDifference += (difference * this.weightsOfDimensions[y]);					
			}							
			double pValue = computePValue(x);
			if(computingPValue == false)
				this.fastaFormatWithScoreTableModel.add(fastaFormat, 1 - totalDifference, pValue);
			else{
				if(topScore < 1 - totalDifference)
					topScore = 1 - totalDifference;
			}
		}
		if(computingPValue == false){
			for(int x = 0; x < this.fastaFormatWithScoreTableModel.size(); x++){
				this.fastaFormatWithScoreTableModel.setArff(x, this.databaseInstances.instance(x));
			}
			statusPane.setText("Sorting..");
			this.fastaFormatWithScoreTableModel.sort();
			this.fastaFormatWithScoreTableModel.update();
		}else{
			//add a counter to all the sequences that had score lower than topScore		
			for(int x = 0; x < this.fastaFormatWithScoreTableModel.size();){
				if(topScore > this.fastaFormatWithScoreTableModel.get(x).getScore()){
					this.fastaFormatWithScoreTableModel.get(x).incrementPValueCount();
					x++;
					//this.fastaFormatWithScoreTableModel.transferToFiltered(x);
				}else
					x++;
			}			
		}
	}
	
	public void mouseClicked(MouseEvent me) {
		if(me.getSource().equals(this.queryTextField)){
			loadQueryFile();
		}else if(me.getSource().equals(this.databaseTextField)){
			loadDatabaseFile();
		}else if(me.getSource().equals(this.featureTextField)){
			loadFeatureFile();
		}else if(me.getSource().equals(this.workingDirectoryTextField)){
			this.setWorkingDirectory();
		}
	}
	
	public void mouseEntered(MouseEvent arg0) {}	
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}
	
}


