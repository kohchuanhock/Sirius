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
package sirius.clustering.main;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
//import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import sirius.trainer.main.SiriusSettings;
import sirius.trainer.step4.GraphPane;
import sirius.utils.ClassifierResults;
import sirius.utils.PredictionStats;
import weka.classifiers.Classifier;
//import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.Utils;
import weka.gui.GenericObjectEditor;
import weka.gui.PropertyPanel;

public class ClustererClassificationPane extends JComponent implements ActionListener, ListSelectionListener{
	static final long serialVersionUID = sirius.Sirius.version;
	private JInternalFrame parent;
	
	private JButton startButton;
	private JButton stopButton;
	
	//Used for focusListener of their own field
	private int foldsLastValue;
	
	private Classifier classifierOne;//stored classifier
	
	/** Lets the user configure the classifier one*/
	private GenericObjectEditor m_ClassifierEditor = new GenericObjectEditor();
	/** The panel showing the current classifier one selection */
	private PropertyPanel m_CEPanel = new PropertyPanel(m_ClassifierEditor);
	
	private JTextArea levelOneClassifierOutputTextArea;
	
	private JRadioButton xValidationRadioButton;
	private JRadioButton jackKnifeRadioButton;
	private JTextField foldsField;			
	
	private GraphPane classifierOneGraph;		
	private JTabbedPane tabbedClassifierPane;
	
	private JButton classifierOneComputeButton;
	private JTextField classifierOneThresholdTextField;		
	
	private JScrollPane levelOneClassifierOutputScrollPane;
	
	//added variables for clustering classification
	private JTextField inputDirectoryTextField;
	private JButton inputDirectoryButton;
	private Thread clusteringClassificationThread;
	
	private JLabel statusLabel;
	private ResultsTableModel resultsTableModel;
	private JTable resultsTable;
	private int numOfCluster;
	
 public ClustererClassificationPane(final JInternalFrame parent) {
    	this.parent = parent;
    	
    	//classifierOneResults = new ClassifierResults(false);    	    	
    	setLayout(new BorderLayout());    	   	    	
    	m_ClassifierEditor.setClassType(Classifier.class);  	    
    	
    	JPanel inputDirectoryPanel = new JPanel(new BorderLayout());
    	inputDirectoryPanel.setBorder(BorderFactory.createCompoundBorder(
    	    	BorderFactory.createTitledBorder("Select The Largest Cluster Number Input File"),
    	    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));
    	this.inputDirectoryButton = new JButton("Choose");
    	this.inputDirectoryButton.addActionListener(this);
    	this.inputDirectoryTextField = new JTextField();
    	this.inputDirectoryTextField.setFocusable(false);
    	inputDirectoryPanel.add(this.inputDirectoryTextField,BorderLayout.CENTER);
    	inputDirectoryPanel.add(this.inputDirectoryButton,BorderLayout.WEST);    	
    	
    	JPanel classifierOnePanel = new JPanel(new GridLayout());
    	classifierOnePanel.setBorder(BorderFactory.createCompoundBorder(
    	BorderFactory.createTitledBorder("Select Classifier"),
    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));
    	classifierOnePanel.add(m_CEPanel);    	    
    	    	    	
    	JPanel north = new JPanel(new GridLayout(2,1));
    	north.add(inputDirectoryPanel);
    	north.add(classifierOnePanel);    	    	    	    	  		
		
		//Test Options
		JPanel testOptionsPanel = new JPanel(new GridLayout(2,1));		
		xValidationRadioButton = new JRadioButton("X-Validation: ");
		xValidationRadioButton.addActionListener(this);
		this.xValidationRadioButton.setSelected(true);
		JPanel foldsPanel = new JPanel(new BorderLayout());
		JLabel foldsLabel = new JLabel("Folds: ");
		foldsField = new JTextField();
		foldsField.setText("10");
		foldsLastValue = 10;
		foldsField.addFocusListener(new FocusListener() {
	         public void focusGained(FocusEvent e){	
	         }	
	         public void focusLost(FocusEvent e){	         		
	         	try{
	         		int temp = Integer.parseInt(foldsField.getText());
	         		if(temp <= 1){
	         			JOptionPane.showMessageDialog(parent,"Folds must be > 1","Error",
		         		JOptionPane.ERROR_MESSAGE);
		         		foldsField.requestFocusInWindow();
	         		}
					else{
						foldsLastValue = temp;
					}	         				         		
	         	}catch(NumberFormatException ex){
	         		JOptionPane.showMessageDialog(parent,"Invalid Input(Numbers Only) in Folds Field","Error",
		         		JOptionPane.ERROR_MESSAGE);
		         	foldsField.setText("" + foldsLastValue);
	         		foldsField.requestFocusInWindow();
	         	}
	         }
		});	
		foldsPanel.add(foldsLabel,BorderLayout.WEST);
		foldsPanel.add(foldsField,BorderLayout.CENTER);			
		this.jackKnifeRadioButton = new JRadioButton("Jack-Knife");
		this.jackKnifeRadioButton.addActionListener(this);
		
		JPanel xValidationPanel = new JPanel(new GridLayout(1,2));
		xValidationPanel.add(xValidationRadioButton);
		xValidationPanel.add(foldsField);
				
		testOptionsPanel.add(xValidationPanel);
		testOptionsPanel.add(this.jackKnifeRadioButton);
			
		//Classifier One Information Panel
		JPanel classifierOneWithButtonsPanel = new JPanel(new BorderLayout());
		classifierOneWithButtonsPanel.setBorder(
			BorderFactory.createTitledBorder("Controls"));
		//Start Stop Button Panel for Classifier One
		JPanel startStopButtonPanel = new JPanel(new GridLayout(1,2,5,5));
   		//startStopButtonPanel.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));   
   		startButton = new JButton("Start");   
   		startButton.addActionListener(this);	
   		stopButton = new JButton("Stop");
   		stopButton.addActionListener(this);
   		stopButton.setEnabled(false);   		
   		startStopButtonPanel.add(startButton);  
   		startStopButtonPanel.add(stopButton);
   		classifierOneWithButtonsPanel.add(startStopButtonPanel,BorderLayout.CENTER);			
			
   		JPanel testOptionsAndFoldPanel = new JPanel(new BorderLayout());
   		testOptionsAndFoldPanel.setBorder(
   				BorderFactory.createTitledBorder("Test Options"));
   		testOptionsAndFoldPanel.add(testOptionsPanel,BorderLayout.CENTER);   		   	   	
   		
   		JPanel centerNorthPanel = new JPanel(new GridLayout(2,1));   		   		
   		centerNorthPanel.add(testOptionsAndFoldPanel);	
   		centerNorthPanel.add(classifierOneWithButtonsPanel);  		
   		
   		resultsTableModel = new ResultsTableModel();
		resultsTable = new JTable(resultsTableModel);
		resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
		//allows to detect the changes in selection
		resultsTable.getSelectionModel().addListSelectionListener(this);
		JScrollPane resultsTableScrollPane = new JScrollPane(resultsTable);
		//this code changes the scrollpane into a size i like
		resultsTableScrollPane.setPreferredSize(new Dimension(100,100));
   		
   		JPanel westPanel = new JPanel(new BorderLayout());
   		westPanel.add(centerNorthPanel, BorderLayout.NORTH);
   		westPanel.add(resultsTableScrollPane,BorderLayout.CENTER);   		
   		
		JPanel center = new JPanel(new BorderLayout());   			
    		
    	//Classifier One Output TextArea
    	levelOneClassifierOutputTextArea = new JTextArea();
    	levelOneClassifierOutputTextArea.setEditable(false);
    	levelOneClassifierOutputScrollPane = new JScrollPane(levelOneClassifierOutputTextArea);
    	JPanel levelOneClassifierOutputPanel = new JPanel(new BorderLayout());
    	levelOneClassifierOutputPanel.add(levelOneClassifierOutputScrollPane,BorderLayout.CENTER);
    	
    	JPanel levelOneClassifierOutputNorthPanel = new JPanel();    	
    	JLabel classifierOneThresholdLabel = new JLabel("     Threshold:  ");
    	classifierOneThresholdTextField = new JTextField("0.5", 10);
    	JLabel classifierOneButtonLabel = new JLabel("     ");
    	classifierOneComputeButton = new JButton("Compute");
    	classifierOneComputeButton.addActionListener(this);
    	//classifierOneSaveScoreFileButton = new JButton("Save Score File");
    	//classifierOneSaveScoreFileButton.addActionListener(this);
    	JLabel classifierOneSaveScoreButtonLabel = new JLabel("     ");
    	levelOneClassifierOutputNorthPanel.add(classifierOneThresholdLabel);
    	levelOneClassifierOutputNorthPanel.add(classifierOneThresholdTextField);
    	levelOneClassifierOutputNorthPanel.add(classifierOneButtonLabel);
    	levelOneClassifierOutputNorthPanel.add(classifierOneComputeButton);
    	levelOneClassifierOutputNorthPanel.add(classifierOneSaveScoreButtonLabel);
    	//levelOneClassifierOutputNorthPanel.add(classifierOneSaveScoreFileButton); 
    	levelOneClassifierOutputPanel.add(levelOneClassifierOutputNorthPanel,BorderLayout.NORTH);
    		
    	//Classifier One Output Graph Area
    	JPanel classifierOneOutputGraphPanel = new JPanel(new GridLayout(1,1));
    	classifierOneGraph = new GraphPane();
    	classifierOneOutputGraphPanel.add(classifierOneGraph);
    		    	
    	//Definitions Pane    	
    	JTextArea definitionsPane = new JTextArea();  
    	definitionsPane.setEditable(false);
    	definitionsPane.append("=== Definitions ===\n\n");
		definitionsPane.append("TP => No of predictions > threshold in +ve sequences within range\n");
		definitionsPane.append("FN => No of predictions <= threshold in +ve sequences within range\n");
		definitionsPane.append("TN => No of predictions > threshold in -ve sequences\n");
		definitionsPane.append("FP => No of predictions <= threshold in -ve sequences\n\n");
		definitionsPane.append("Total Correct Predictions = (TP + TN)/(TP + FN + TN + TP)\n");
		definitionsPane.append("Total Incorrect Predictions = (FN + FP)/(TP + FN + TN + TP)\n\n");
		definitionsPane.append("Precision(wrt +ve) = TP/(TP + FP)\n");
		definitionsPane.append("Precision(wrt -ve) = TN/(TN + FN)\n\n");
		definitionsPane.append("SN = TP / (TP + FN)\n");
		definitionsPane.append("SP = TN / (TN + FP)\n\n");			
		JScrollPane definitionPane = new JScrollPane(definitionsPane);	
		
		tabbedClassifierPane = new JTabbedPane();
		tabbedClassifierPane.addTab("Classifier Summary",null,levelOneClassifierOutputPanel,
			"Show classifier output in text format");	
		tabbedClassifierPane.addTab("Classifier Graph",null,classifierOneOutputGraphPanel,
			"Show classifier output in Graph format");		
		tabbedClassifierPane.addTab("Definitions",null,definitionPane,
			"Clarify the definitions");
						
		//center.add(centerNorthPanel,BorderLayout.NORTH);
		center.add(tabbedClassifierPane,BorderLayout.CENTER);
    	
		JPanel statusPanel = new JPanel(new GridLayout(1,1));
		statusPanel.setBorder(
   				BorderFactory.createTitledBorder("Status"));
		this.statusLabel = new JLabel(" ");
		statusPanel.add(this.statusLabel);
		
    	add(center,BorderLayout.CENTER);
    	add(north, BorderLayout.NORTH);    	    
    	add(statusPanel,BorderLayout.SOUTH);
    	add(westPanel,BorderLayout.WEST);
    }
        
    private int validateFieldAsInteger(String value, String name, JComponent component) throws Exception{
    	try{
    		return Integer.parseInt(value);
    	}catch(NumberFormatException e){
    		JOptionPane.showMessageDialog(parent,"Input only Integer into " + name,"ERROR",
       			JOptionPane.ERROR_MESSAGE);
       		component.requestFocusInWindow();
       		throw new Exception();
    	}
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
    
    private void computeStats(int xValidation){    	
    	try{
    		int index = this.resultsTable.getSelectedRow();
    		double threshold = validateFieldAsThreshold(classifierOneThresholdTextField.getText(), "Threshold Field", 
    				classifierOneThresholdTextField);
    		if(index < numOfCluster + 1){
	    		String filename = inputDirectoryTextField.getText().replaceAll("_cluster" + numOfCluster + ".arff", 
	    				"_cluster" + index + ".score");	    		
	    		ClassifierResults cr = this.resultsTableModel.getClassifierResults(index);
	    		//By doing the below if else statement, I save some time by not having to read input file again and again but
	    		//I use more memory to store more data
	    		if(threshold != cr.getThreshold()){		    			
	    			PredictionStats classifierStats = new PredictionStats(filename,
							0,threshold);		
					classifierStats.updateDisplay(this.resultsTableModel.getClassifierResults(index), levelOneClassifierOutputTextArea,true);
					this.classifierOneGraph.setMyStats(classifierStats);
					this.resultsTableModel.update(cr, classifierStats, index);
	    		}else{	    			
	    			PredictionStats classifierStats = this.resultsTableModel.getStats(index); 
	    			classifierStats.updateDisplay(this.resultsTableModel.getClassifierResults(index), levelOneClassifierOutputTextArea,true);
					this.classifierOneGraph.setMyStats(classifierStats);
	    		}
    		}else if(index == (numOfCluster + 1)){//for last index is reserved for equal weightage    			
    			//EqualWeightage
	    		ClassifierResults classifierResults = new ClassifierResults(false,this.resultsTableModel.getCRArrayList().size());	    				
				String classifierName = m_ClassifierEditor.getValue().getClass().getName();	
				classifierResults.updateList(classifierResults.getClassifierList(), "Classifier: ", classifierName); 
				
				PredictionStats.computeEqualWeightage(classifierResults, levelOneClassifierOutputTextArea, threshold, 
						this.resultsTableModel.getStatArrayList(), this.resultsTableModel.getCRArrayList());
				this.classifierOneGraph.setMyStats(null);
    		}else{
    			//Weighted weightage
    			ClassifierResults classifierResults = new ClassifierResults(false,this.resultsTableModel.getCRArrayList().size());	    				
				String classifierName = m_ClassifierEditor.getValue().getClass().getName();	
				classifierResults.updateList(classifierResults.getClassifierList(), "Classifier: ", classifierName); 
				
				PredictionStats.computeWeighted(classifierResults, levelOneClassifierOutputTextArea, threshold, 
						this.resultsTableModel.getStatArrayList(), this.resultsTableModel.getCRArrayList());
				this.classifierOneGraph.setMyStats(null);
    		}
			repaint();
    	}catch(Exception ex){/*Dun have to print stack trace here because it has already been taken care of inside validateField method()*/}    	
    }
    
    private boolean validateStatsSettings(int classifierType){  
    	try{    		
    		if(this.xValidationRadioButton.isSelected())
    			validateFieldAsInteger(this.foldsField.getText(), "Cross-Validation Field", this.foldsField);
    		validateFieldAsThreshold(classifierOneThresholdTextField.getText(), "Threshold Field", classifierOneThresholdTextField);
    	}catch(Exception ex){return false;/*Dun have to print stack trace here because it has already been taken care of inside validateField method()*/}
    	return true;
    }
    
    private void setInputDirectory(){//note that from setting directory, this method has been changed to load file
    	try{
			JFileChooser fc;	    	
	    	String lastLocation = SiriusSettings.getInformation("LastClusteringOutputLocation: ");
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
	            SiriusSettings.updateInformation("LastClusteringOutputLocation: ", file.getAbsolutePath());
	            this.inputDirectoryTextField.setText(file.getAbsolutePath());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
    }
    
    
    
    private void start(){
    	//Run Classifier
    	if(this.inputDirectoryTextField.getText().length() == 0){
    		JOptionPane.showMessageDialog(parent,"Please set Input Directory to where the clusterer output are!","Evaluate Classifier",
    				JOptionPane.ERROR_MESSAGE);
    			return;
    	}
		if(m_ClassifierEditor.getValue() == null){
			JOptionPane.showMessageDialog(parent,"Please choose Classifier!","Evaluate Classifier",
				JOptionPane.ERROR_MESSAGE);
			return;
		}		
		if(validateStatsSettings(1) == false){
			return;
		}
     	if(this.clusteringClassificationThread == null){
			startButton.setEnabled(false);
			stopButton.setEnabled(true);
			tabbedClassifierPane.setSelectedIndex(0);
      		this.clusteringClassificationThread = (new Thread(){      		
				public void run(){
				//Clear the output text area
	    		levelOneClassifierOutputTextArea.setText("");
	    		resultsTableModel.reset();
	    		//double threshold = Double.parseDouble(classifierOneThresholdTextField.getText());		    		
    			//cross-validation
	    		int numFolds;
	    		if(jackKnifeRadioButton.isSelected())
	    			numFolds = -1;
	    		else
	    			numFolds = Integer.parseInt(foldsField.getText());	    		
	    		StringTokenizer st = new StringTokenizer(inputDirectoryTextField.getText(),File.separator);
	    		String filename = "";
	    		while(st.hasMoreTokens()){
		    		filename = st.nextToken();
	    		}
	    		StringTokenizer st2 = new StringTokenizer(filename, "_.");
	    		numOfCluster = 0;
	    		if(st2.countTokens() >= 2){
	    			st2.nextToken();
	    			String numOfClusterString = st2.nextToken().replaceAll("cluster", "");
	    			try{
	    				numOfCluster = Integer.parseInt(numOfClusterString);
	    			}catch(NumberFormatException e){
	    				JOptionPane.showMessageDialog(parent,
	    			    		"Please choose the correct file! (Output from Utilize Clusterer)","ERROR",
	    			    		JOptionPane.ERROR_MESSAGE);
	    			}
	    		}
	    		Classifier template = (Classifier) m_ClassifierEditor.getValue();	    		
	    			    		
	    		for(int x = 0; x <= numOfCluster && clusteringClassificationThread != null; x++){//Test each cluster
	    			try{
	    				long totalTimeStart = 0, totalTimeElapsed = 0;	    									
	    	  			totalTimeStart = System.currentTimeMillis();
	    				statusLabel.setText("Reading in cluster" + x + " file..");	    				
	    				String inputFilename = inputDirectoryTextField.getText().replaceAll("_cluster" + numOfCluster + ".arff", 
	    						"_cluster" + x + ".arff");
	    				String outputScoreFilename = inputDirectoryTextField.getText().replaceAll("_cluster" + numOfCluster + ".arff", 
	    						"_cluster" + x + ".score");
	    				BufferedWriter output = new BufferedWriter(new FileWriter(outputScoreFilename));		    
	    				Instances inst = new Instances(new FileReader(inputFilename));
	    				//Assume that class attribute is the last attribute - This should be the case for all Sirius produced Arff files
	    				inst.setClassIndex(inst.numAttributes() - 1);
	    				Random random = new Random(1);//Simply set to 1, shall implement the random seed option later
	    				inst.randomize(random);
	    				if (inst.attribute(inst.classIndex()).isNominal())	    					
	    					inst.stratify(numFolds);	    				      
	    				// for timing
	    				ClassifierResults classifierResults = new ClassifierResults(false,0);	    				
	    				String classifierName = m_ClassifierEditor.getValue().getClass().getName();	
	    				classifierResults.updateList(classifierResults.getClassifierList(), "Classifier: ", classifierName);
	    				classifierResults.updateList(classifierResults.getClassifierList(), "Training Data: ", inputFilename);
	    				classifierResults.updateList(classifierResults.getClassifierList(), "Time Used: ", "NA");	    				
	    				//ArrayList<Double> resultList = new ArrayList<Double>();	  
	    				if(jackKnifeRadioButton.isSelected() || numFolds > inst.numInstances() - 1)
	    					numFolds = inst.numInstances() - 1;
	    				 for (int fold = 0; fold < numFolds && clusteringClassificationThread != null; fold++) {//Doing cross-validation    		
	    					 	statusLabel.setText("Cluster: " + x + " - Training Fold " + (fold+1) + "..");
	    		    			Instances train = inst.trainCV(numFolds, fold, random); 			    		
	    		    			Classifier current = null;
	    		    			try{	    		    					    		    			    		    			
	    		    				current = Classifier.makeCopy(template);
	    		    				current.buildClassifier(train);
	    		    				Instances test = inst.testCV(numFolds, fold);
	    		    				statusLabel.setText("Cluster: " + x + " - Testing Fold " + (fold+1) + "..");
		    		    			for (int jj=0;jj<test.numInstances();jj++){
		    		    				double[] result = current.distributionForInstance(test.instance(jj));
		    		    				output.write("Cluster: " + x);
		    		    				output.newLine();		    		    				
		    		    				output.newLine();
		    		    				output.write(test.instance(jj).stringValue(test.classAttribute()) + ",0=" + result[0]);
		    		    				output.newLine();
		    		    			}
	    		    			}catch(Exception ex) {
	    		    				ex.printStackTrace();
	    		    				statusLabel.setText("Error in cross-validation!");
	    		    	    		startButton.setEnabled(true);
	    		    	    		stopButton.setEnabled(false);
	    		    			}	    		    			
	    				 }	    				  
	    				 output.close();	    				 		    			 
		    			 totalTimeElapsed = System.currentTimeMillis() - totalTimeStart;
		    		     	classifierResults.updateList(classifierResults.getResultsList(), "Total Time Used: ", 
		    		     		Utils.doubleToString(totalTimeElapsed / 60000,2) + " minutes " + 
		    		     		Utils.doubleToString((totalTimeElapsed/1000.0) % 60.0,2) + 
		    	  				" seconds");
		    		     double threshold = validateFieldAsThreshold(classifierOneThresholdTextField.getText(), "Threshold Field", 
		    	    				classifierOneThresholdTextField);
		    		     String filename2 = inputDirectoryTextField.getText().replaceAll("_cluster" + numOfCluster + ".arff", 
		    	    				"_cluster" + x + ".score");
		    		     PredictionStats classifierStats = new PredictionStats(filename2,
		    						0,threshold);
		    		     	
	    				 resultsTableModel.add("Cluster " + x,classifierResults,classifierStats);
	    				 resultsTable.setRowSelectionInterval(x, x);
	    				 computeStats(numFolds);//compute and display the results	    				 
	    			}catch(Exception e){
	    				e.printStackTrace();
	    				statusLabel.setText("Error in reading file!");
	    	    		startButton.setEnabled(true);
	    	    		stopButton.setEnabled(false);
	    			}	    			
	    		}//end of cluster for loop
	    		
	    		resultsTableModel.add("Summary - Equal Weightage",null,null);
	    		resultsTable.setRowSelectionInterval(numOfCluster+1,numOfCluster+1);
	    		computeStats(numFolds);
	    		resultsTableModel.add("Summary - Weighted Average",null,null);
	    		resultsTable.setRowSelectionInterval(numOfCluster+2,numOfCluster+2);
	    		computeStats(numFolds);
	    		
	    		if(clusteringClassificationThread != null)
	    			statusLabel.setText("Done!");
	    		else
	    			statusLabel.setText("Interrupted..");
	    		startButton.setEnabled(true);
	    		stopButton.setEnabled(false);					      
	    		if(classifierOne != null){
	    			levelOneClassifierOutputScrollPane.getVerticalScrollBar().setValue(
	    					levelOneClassifierOutputScrollPane.getVerticalScrollBar().getMaximum());
	    		}
				clusteringClassificationThread = null;				      
						     
			}});
      		this.clusteringClassificationThread.setPriority(Thread.MIN_PRIORITY);
      		this.clusteringClassificationThread.start();				
 		}else{
     		JOptionPane.showMessageDialog(parent,
	    		"Cannot start new job as previous job still running. Click stop to terminate previous job","ERROR",
	    		JOptionPane.ERROR_MESSAGE);
     	}  		
    }
    
    private void stop(){
    	this.clusteringClassificationThread = null;
    }
    
    public void actionPerformed(ActionEvent ae){
    	if(ae.getSource().equals(classifierOneComputeButton)){       		
			computeStats(-1);				
    	}else if(ae.getSource().equals(startButton)){
    		start();
    	}else if(ae.getSource().equals(stopButton)){
    		stop();	        	
    	}else if(ae.getSource().equals(xValidationRadioButton)){    		
    		xValidationRadioButton.setSelected(true);
    		this.jackKnifeRadioButton.setSelected(false);
    		this.foldsField.setEnabled(true);
    	}else if(ae.getSource().equals(this.jackKnifeRadioButton)){
    		this.jackKnifeRadioButton.setSelected(true);
    		this.xValidationRadioButton.setSelected(false);
    		this.foldsField.setEnabled(false);
    	}else if(ae.getSource().equals(this.inputDirectoryButton)){
    		setInputDirectory();
    	}
    }

	@Override
	public void valueChanged(ListSelectionEvent lse) {		
		if(lse.getSource().equals(this.resultsTable.getSelectionModel())){
			computeStats(-1);
		}
	}
} 









