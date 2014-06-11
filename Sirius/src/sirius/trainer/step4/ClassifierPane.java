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

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import sirius.main.ApplicationData;
import sirius.trainer.features.gui.geneticalgorithm.GASettingsInterface;
import sirius.trainer.features.gui.geneticalgorithm.GeneticAlgorithmDialog;
import sirius.utils.ClassifierResults;
import sirius.utils.PredictionStats;
import weka.classifiers.Classifier;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.gui.GenericObjectEditor;
import weka.gui.PropertyPanel;

public class ClassifierPane extends JComponent implements ActionListener{
	static final long serialVersionUID = sirius.Sirius.version;
	
	private JInternalFrame parent;
	//private StatusPane statusPane;
	private JButton previousStepButton;
	private JButton saveClassifierOneButton;
	private JButton saveClassifierTwoButton;	
	private JTabbedPane tabbedPane;	
	
	private JLabel numberOfFeaturesLabelR;
	private JLabel featureLeftMostPositionLabelR;
	private JLabel featureRightMostPositionLabelR;
	
	private JTextField setClassifierTwoUpstreamField;
	private JTextField setClassifierTwoDownstreamField;
	
	private JButton startButton;
	private JButton stopButton;
	private JButton startButton2;
	private JButton stopButton2;
	
	private ApplicationData applicationData;
	private ApplicationData appDataForSaving;
	
	//Used for focusListener of their own field
	private int upstreamLastValue;
	private int downstreamLastValue;
	private int foldsLastValue;
	private double limitLastValue;
	
	private Classifier classifierOne;//stored classifier
	private Classifier classifierTwo;//stored classifier	
	
	/** Lets the user configure the classifier one*/
	private GenericObjectEditor m_ClassifierEditor = new GenericObjectEditor();
	/** The panel showing the current classifier one selection */
	private PropertyPanel m_CEPanel = new PropertyPanel(m_ClassifierEditor);
	
	/** Lets the user configure the classifier two*/
	private GenericObjectEditor m_ClassifierEditor2 = new GenericObjectEditor();
	/** The panel showing the current classifier two selection */
	private PropertyPanel m_CEPanel2 = new PropertyPanel(m_ClassifierEditor2);
	
	private JTextArea levelOneClassifierOutputTextArea;
	private JTextArea levelTwoClassifierOutputTextArea;	
		
	private JCheckBox needNotOutputClassifier = new JCheckBox("Output Classifier",false);
	private JRadioButton needNotTestRadioButton = new JRadioButton("Need Not Test");
	private JRadioButton dataset3RadioButton = new JRadioButton("Use Dataset 3");
	private JRadioButton xValidationRadioButton = new JRadioButton("X-Validation");
	private JTextField foldsField = new JTextField("10");
	private JRadioButton jackKnifeRadioButton = new JRadioButton("Jack-Knife");
	private JTextField limitField = new JTextField("3");
		
	private JPanel classifierTwoWithButtonsPanel;
	
	private GraphPane classifierOneGraph;
	private GraphPane classifierTwoGraph;	
		
	private JTabbedPane tabbedClassifierPane;
	
	private JButton classifierOneComputeButton;
	private JTextField classifierOneThresholdTextField;
	private JButton classifierOneSaveScoreFileButton;
	private JButton classifierTwoComputeButton;
	private JTextField classifierTwoRangeTextField;
	private JTextField classifierTwoThresholdTextField;
	private JButton classifierTwoSaveScoreFileButton;
	
	private ClassifierResults classifierOneResults = new ClassifierResults(false,0);
	private ClassifierResults classifierTwoResults = new ClassifierResults(true,0);
	
	private JScrollPane levelTwoClassifierOutputScrollPane;
	private JScrollPane levelOneClassifierOutputScrollPane;	

	private JPanel gaPanel = new JPanel(new GridLayout(1,1,5,5));
	private JButton gaButton = new JButton("Genetic Algorithm");
	private GeneticAlgorithmDialog gaDialog;
	
	private int classifierOneRandomNumber;
	
    public ClassifierPane(final JInternalFrame parent,JTabbedPane tabbedPane,
    	ApplicationData applicationData,JFrame mainFrame) {    	
    	this.parent = parent;
    	this.applicationData = applicationData;    	    	
    	this.gaDialog = new GeneticAlgorithmDialog(null, null,true);
    	//this.statusPane = applicationData.getStatusPane();
    	this.tabbedPane = tabbedPane;     	    	
    	
    	setLayout(new BorderLayout());    	   	
    	
    	m_ClassifierEditor.setClassType(Classifier.class);
    	m_ClassifierEditor2.setClassType(Classifier.class);       
    	
    	JPanel classifierOnePanel = new JPanel(new GridLayout());
    	classifierOnePanel.setBorder(BorderFactory.createCompoundBorder(
    	BorderFactory.createTitledBorder("Level One Classifier - Feature Integration"),
    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));
    	classifierOnePanel.add(m_CEPanel);    	    	
    	
    	JPanel classifierTwoPanel = new JPanel(new GridLayout());    	
    	classifierTwoPanel.setBorder(BorderFactory.createCompoundBorder(
    	BorderFactory.createTitledBorder("Level Two Classifier - Cascade Classifier"),
    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));
    	classifierTwoPanel.add(m_CEPanel2);
    	
    	JPanel north;
    	if(this.applicationData.getDataset1Instances() == null){    		    		
    		this.gaButton.addActionListener(this);    		
    		north = new JPanel(new BorderLayout(5,5));    		
    		this.gaPanel.setBorder(BorderFactory.createCompoundBorder(
    		    	BorderFactory.createTitledBorder("Auto-Generate"),
    		    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));
    		this.gaPanel.add(this.gaButton);    		
    		
    		JPanel centerPanel = new JPanel(new GridLayout(2,1,5,5));
    		centerPanel.add(classifierOnePanel);
    		centerPanel.add(classifierTwoPanel);    		
    		north.add(this.gaPanel, BorderLayout.WEST);
    		north.add(centerPanel, BorderLayout.CENTER);
    	}else{
    		north = new JPanel(new GridLayout(2,1));
    		north.add(classifierOnePanel);
    		north.add(classifierTwoPanel);
    	}
    	
    	GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
    	JPanel south = new JPanel(gridbag);
    	previousStepButton = new JButton("<<< BACK");    	
    	previousStepButton.addActionListener(this);    
    	c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        gridbag.setConstraints(previousStepButton,c);	   	
    	saveClassifierOneButton = new JButton("Save Classifier One");
    	saveClassifierOneButton.addActionListener(this);
    	saveClassifierOneButton.setEnabled(false);
    	c.fill = GridBagConstraints.BOTH;
        c.weightx = 2.0;
        c.weighty = 1.0;
        gridbag.setConstraints(saveClassifierOneButton,c);
    	saveClassifierTwoButton = new JButton("Save Classifier Two");
    	saveClassifierTwoButton.addActionListener(this);
    	saveClassifierTwoButton.setEnabled(false);
    	c.fill = GridBagConstraints.BOTH;
        c.weightx = 2.0;
        c.weighty = 1.0;
        gridbag.setConstraints(saveClassifierTwoButton,c);
    	south.add(previousStepButton);   
    	south.add(saveClassifierOneButton);
    	south.add(saveClassifierTwoButton);    	    	    	  								   		
			
		//Classifier One Information Panel
		JPanel classifierOneWithButtonsPanel = new JPanel(new BorderLayout());
		classifierOneWithButtonsPanel.setBorder(
			BorderFactory.createTitledBorder("Level One Classifier Information"));
		JPanel classifierOneInformationPanel = new JPanel(new GridLayout(3,2));		
		JLabel numberOfFeaturesLabel = new JLabel("# Features: ",SwingConstants.RIGHT);
		numberOfFeaturesLabelR = new JLabel("");
		JLabel featureLeftMostPositionLabel = new JLabel("L.Most Pos: ",SwingConstants.RIGHT);
		featureLeftMostPositionLabelR = new JLabel("");
		JLabel featureRightMostPositionLabel = new JLabel("R.Most Pos: ",SwingConstants.RIGHT);
		featureRightMostPositionLabelR = new JLabel("");		
		classifierOneInformationPanel.add(numberOfFeaturesLabel);
		classifierOneInformationPanel.add(numberOfFeaturesLabelR);
		classifierOneInformationPanel.add(featureLeftMostPositionLabel);
		classifierOneInformationPanel.add(featureLeftMostPositionLabelR);
		classifierOneInformationPanel.add(featureRightMostPositionLabel);
		classifierOneInformationPanel.add(featureRightMostPositionLabelR);		
		classifierOneWithButtonsPanel.add(classifierOneInformationPanel,BorderLayout.CENTER);
		
		//Start Stop Button Panel for Classifier One
		JPanel startStopButtonPanel = new JPanel(new GridLayout(1,2));
   		startStopButtonPanel.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));   
   		startButton = new JButton("Start");   
   		startButton.addActionListener(this);	
   		stopButton = new JButton("Stop");
   		stopButton.addActionListener(this);
   		stopButton.setEnabled(false);   		
   		startStopButtonPanel.add(startButton);  
   		startStopButtonPanel.add(stopButton);
   		classifierOneWithButtonsPanel.add(startStopButtonPanel,BorderLayout.SOUTH);   		    		
		
		//Classifier Two Settings Panel
		BorderLayout classifierTwoLayout = new BorderLayout();
		classifierTwoLayout.setVgap(5);
		classifierTwoLayout.setHgap(3);
		classifierTwoWithButtonsPanel = new JPanel(classifierTwoLayout);
		classifierTwoWithButtonsPanel.setBorder(BorderFactory.createCompoundBorder(
   			BorderFactory.createTitledBorder("Level Two Classifier Settings"),
   			BorderFactory.createEmptyBorder(3,3,3,3)));   	
   		GridLayout classifierTwoSettingsLayout = new GridLayout(2,2);   		
   		classifierTwoSettingsLayout.setVgap(5);
    	classifierTwoSettingsLayout.setHgap(3);
    	JPanel levelTwoClassifierSettingsPanel = new JPanel(classifierTwoSettingsLayout);        	
   		JLabel setClassifierTwoUpstreamLabel = new JLabel("Upstream: ",SwingConstants.RIGHT);
   		JLabel setClassifierTwoDownstreamLabel = new JLabel("Downstream: ",SwingConstants.RIGHT);
   		setClassifierTwoUpstreamField = new JTextField();
   		setClassifierTwoUpstreamField.setText("-40");
   		upstreamLastValue = -40;
   		setClassifierTwoUpstreamField.addFocusListener(new FocusListener() {
	         public void focusGained(FocusEvent e){	
	         }
	
	         public void focusLost(FocusEvent e){	         		
	         	try{
	         		upstreamLastValue = Integer.parseInt(setClassifierTwoUpstreamField.getText());
	         		updateLabels();
	         	}catch(NumberFormatException ex){
	         		JOptionPane.showMessageDialog(parent,"Invalid Input(Numbers Only)","Error",
		         		JOptionPane.ERROR_MESSAGE);
		         	setClassifierTwoUpstreamField.setText("" + upstreamLastValue);
	         		setClassifierTwoUpstreamField.requestFocusInWindow();
	         	}
	         }
		});	
   		setClassifierTwoDownstreamField = new JTextField();
   		setClassifierTwoDownstreamField.setText("41");
   		downstreamLastValue = 41;
   		setClassifierTwoDownstreamField.addFocusListener(new FocusListener() {
	         public void focusGained(FocusEvent e){
	         }
	
	         public void focusLost(FocusEvent e){
	         	try{
	         		downstreamLastValue = Integer.parseInt(setClassifierTwoDownstreamField.getText());
	         		updateLabels();
	         	}catch(NumberFormatException ex){
	         		JOptionPane.showMessageDialog(parent,"Invalid Input(Numbers Only)","Error",
	         		JOptionPane.ERROR_MESSAGE);
	         		setClassifierTwoDownstreamField.setText("" + downstreamLastValue);
	         		setClassifierTwoDownstreamField.requestFocusInWindow();	         		
	         	}
	         }
		});
   		levelTwoClassifierSettingsPanel.add(setClassifierTwoUpstreamLabel);
   		levelTwoClassifierSettingsPanel.add(setClassifierTwoUpstreamField);
   		levelTwoClassifierSettingsPanel.add(setClassifierTwoDownstreamLabel);
   		levelTwoClassifierSettingsPanel.add(setClassifierTwoDownstreamField);
   		classifierTwoWithButtonsPanel.add(levelTwoClassifierSettingsPanel,BorderLayout.CENTER);
   			
   		//Start Stop Button Panel for Classifier Two Settings Panel
   		JPanel startStopButtonsPanel2 = new JPanel(new GridLayout(1,2));
   		startStopButtonsPanel2.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));   
   		startButton2 = new JButton("Start");
   		startButton2.addActionListener(this);
   		startButton2.setEnabled(false);
   		stopButton2 = new JButton("Stop");   		
   		stopButton2.addActionListener(this);
   		stopButton2.setEnabled(false); 
   		startStopButtonsPanel2.add(startButton2);
   		startStopButtonsPanel2.add(stopButton2);
   		classifierTwoWithButtonsPanel.add(startStopButtonsPanel2,BorderLayout.SOUTH);
			   		
   		JPanel west = new JPanel(new GridLayout(3,1));
   		west.add(createTestOptionsPanel());   		
   		west.add(classifierOneWithButtonsPanel);
   		west.add(classifierTwoWithButtonsPanel);
		
		JPanel center = new JPanel(new GridLayout(1,1));    
    	center.setBorder(BorderFactory.createTitledBorder("Output"));    			
    		
    	//Classifier One Output TextArea
    	levelOneClassifierOutputTextArea = new JTextArea();
    	levelOneClassifierOutputTextArea.setEditable(false);
    	levelOneClassifierOutputScrollPane = new JScrollPane(levelOneClassifierOutputTextArea);
    	JPanel levelOneClassifierOutputPanel = new JPanel(new BorderLayout());
    	levelOneClassifierOutputPanel.add(levelOneClassifierOutputScrollPane,BorderLayout.CENTER);
    	
    	JPanel levelOneClassifierOutputNorthPanel = new JPanel();
    	//JLabel classifierOneRangeLabel = new JLabel("Range:  ");
    	//classifierOneRangeTextField = new JTextField("0", 10);
    	JLabel classifierOneThresholdLabel = new JLabel("     Threshold:  ");
    	classifierOneThresholdTextField = new JTextField("0.5", 10);
    	JLabel classifierOneButtonLabel = new JLabel("     ");
    	classifierOneComputeButton = new JButton("Compute");
    	classifierOneComputeButton.addActionListener(this);
    	classifierOneSaveScoreFileButton = new JButton("Save Score File");
    	classifierOneSaveScoreFileButton.addActionListener(this);
    	JLabel classifierOneSaveScoreButtonLabel = new JLabel("     ");
    	//levelOneClassifierOutputNorthPanel.add(classifierOneRangeLabel);
    	//levelOneClassifierOutputNorthPanel.add(classifierOneRangeTextField);
    	levelOneClassifierOutputNorthPanel.add(classifierOneThresholdLabel);
    	levelOneClassifierOutputNorthPanel.add(classifierOneThresholdTextField);
    	levelOneClassifierOutputNorthPanel.add(classifierOneButtonLabel);
    	levelOneClassifierOutputNorthPanel.add(classifierOneComputeButton);
    	levelOneClassifierOutputNorthPanel.add(classifierOneSaveScoreButtonLabel);
    	levelOneClassifierOutputNorthPanel.add(classifierOneSaveScoreFileButton); 
    	levelOneClassifierOutputPanel.add(levelOneClassifierOutputNorthPanel,BorderLayout.NORTH);
    		
    	//Classifier One Output Graph Area
    	JPanel classifierOneOutputGraphPanel = new JPanel(new GridLayout(1,1));
    	classifierOneGraph = new GraphPane();
    	classifierOneOutputGraphPanel.add(classifierOneGraph);
    		
    	//Classifier Two Output TextArea
    	levelTwoClassifierOutputTextArea = new JTextArea();
    	levelTwoClassifierOutputTextArea.setEditable(false);
    	levelTwoClassifierOutputScrollPane = new JScrollPane(levelTwoClassifierOutputTextArea);
    	JPanel levelTwoClassifierOutputPanel = new JPanel(new BorderLayout());
    	levelTwoClassifierOutputPanel.add(levelTwoClassifierOutputScrollPane,BorderLayout.CENTER);
    	
    	JPanel levelTwoClassifierOutputNorthPanel = new JPanel();
    	JLabel classifierTwoRangeLabel = new JLabel("Range:  ");
    	classifierTwoRangeTextField = new JTextField("0", 10);
    	JLabel classifierTwoThresholdLabel = new JLabel("     Threshold:  ");
    	classifierTwoThresholdTextField = new JTextField("0.5", 10);
    	JLabel classifierTwoButtonLabel = new JLabel("     ");
    	classifierTwoComputeButton = new JButton("Compute");
    	classifierTwoComputeButton.addActionListener(this);
    	classifierTwoSaveScoreFileButton = new JButton("Save Score File");
    	classifierTwoSaveScoreFileButton.addActionListener(this);
    	JLabel classifierTwoSaveScoreButtonLabel = new JLabel("     ");
    	levelTwoClassifierOutputNorthPanel.add(classifierTwoRangeLabel);
    	levelTwoClassifierOutputNorthPanel.add(classifierTwoRangeTextField);
    	levelTwoClassifierOutputNorthPanel.add(classifierTwoThresholdLabel);
    	levelTwoClassifierOutputNorthPanel.add(classifierTwoThresholdTextField);
    	levelTwoClassifierOutputNorthPanel.add(classifierTwoButtonLabel);
    	levelTwoClassifierOutputNorthPanel.add(classifierTwoComputeButton);
    	levelTwoClassifierOutputNorthPanel.add(classifierTwoSaveScoreButtonLabel);
    	levelTwoClassifierOutputNorthPanel.add(classifierTwoSaveScoreFileButton);    	
    	levelTwoClassifierOutputPanel.add(levelTwoClassifierOutputNorthPanel,BorderLayout.NORTH);
    	
    	//Classifier Two Output Graph Area
    	JPanel classifierTwoOutputGraphPanel = new JPanel(new GridLayout(1,1));
    	classifierTwoGraph = new GraphPane();
    	classifierTwoOutputGraphPanel.add(classifierTwoGraph);
    	
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
		tabbedClassifierPane.addTab("Classifier One Summary",null,levelOneClassifierOutputPanel,
			"Show classifier one output in text format");	
		tabbedClassifierPane.addTab("Classifier One Graph",null,classifierOneOutputGraphPanel,
			"Show classifier one output in Graph format");
		tabbedClassifierPane.addTab("Classifier Two Summary",null,levelTwoClassifierOutputPanel,
			"Show classifier two output in text format");
		tabbedClassifierPane.addTab("Classifier Two Graph",null,classifierTwoOutputGraphPanel,
			"Show classifier two output in Graph format");
		tabbedClassifierPane.addTab("Definitions",null,definitionPane,
			"Clarify the definitions");
			
			
		center.add(tabbedClassifierPane);
    	
    	add(west,BorderLayout.WEST);    	
    	add(center,BorderLayout.CENTER);
    	add(south,BorderLayout.SOUTH);
    	add(north, BorderLayout.NORTH);    	    	
    }
    
    private JPanel createTestOptionsPanel(){
    	//Test Options    	
		JPanel testOptionsPanel = new JPanel(new GridLayout(4,1,5,5));		
		JPanel needNotTestPanel = new JPanel(new GridLayout(1,2,5,5));
		this.needNotTestRadioButton.addActionListener(this);
		needNotTestPanel.add(this.needNotTestRadioButton);
		needNotTestPanel.add(this.needNotOutputClassifier);
		this.needNotTestRadioButton.setSelected(true);				
		this.dataset3RadioButton.addActionListener(this);	
		this.jackKnifeRadioButton.addActionListener(this);
		JPanel xValidationPanel = new JPanel(new GridLayout(1,3,5,5));		
		this.xValidationRadioButton.addActionListener(this);
		JLabel foldsLabel = new JLabel("Folds: ",SwingConstants.RIGHT);
		this.foldsLastValue = 10;
		this.foldsField.addFocusListener(new FocusListener() {
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
	         			updateLabels();
					}	         				         		
	         	}catch(NumberFormatException ex){
	         		JOptionPane.showMessageDialog(parent,"Invalid Input(Numbers Only) in Folds Field","Error",
		         		JOptionPane.ERROR_MESSAGE);
		         	foldsField.setText("" + foldsLastValue);
	         		foldsField.requestFocusInWindow();
	         	}
	         }
		});
		this.limitLastValue = 3;
		this.limitField.addFocusListener(new FocusListener() {
	         public void focusGained(FocusEvent e){	
	         }
	
	         public void focusLost(FocusEvent e){	         		
	         	try{
	         		double temp = Double.parseDouble(limitField.getText());
	         		if(temp < 0 && temp != -1){
	         			JOptionPane.showMessageDialog(parent,"Ratio must be either > 0 or -1","Error",
		         		JOptionPane.ERROR_MESSAGE);
	         			limitField.requestFocusInWindow();
	         		}
					else{
						limitLastValue = temp;
	         			updateLabels();
					}	         				         		
	         	}catch(NumberFormatException ex){
	         		JOptionPane.showMessageDialog(parent,"Invalid Input(Numbers Only) in Ratio Field","Error",
		         		JOptionPane.ERROR_MESSAGE);
	         		limitField.setText("" + limitLastValue);
	         		limitField.requestFocusInWindow();
	         	}
	         }
		});
		xValidationPanel.add(this.xValidationRadioButton);
		xValidationPanel.add(foldsLabel);
		xValidationPanel.add(this.foldsField);					
		testOptionsPanel.add(needNotTestPanel);
		testOptionsPanel.add(this.dataset3RadioButton);
		testOptionsPanel.add(xValidationPanel);
		JPanel jackKnifePanel = new JPanel(new GridLayout(1,3,5,5));
		JLabel limitLabel = new JLabel("Ratio: ",SwingConstants.RIGHT);
		jackKnifePanel.add(this.jackKnifeRadioButton);
		jackKnifePanel.add(limitLabel);
		jackKnifePanel.add(this.limitField);			
		testOptionsPanel.add(jackKnifePanel);		
		JPanel testOptionsAndFoldPanel = new JPanel(new BorderLayout());
   		testOptionsAndFoldPanel.setBorder(
   				BorderFactory.createTitledBorder("Test Options"));
   		testOptionsAndFoldPanel.add(testOptionsPanel,BorderLayout.CENTER);   		
   		return testOptionsAndFoldPanel;
    }
    
    private int validateFieldAsRange(String name,JTextField component) throws Exception{
    	try{
    		int range = Integer.parseInt(component.getText());
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
    
    private void saveScoreFile(int classifierType){
    	try{
    		BufferedReader input;	
	    	if(classifierType == 1){
	    		input = new BufferedReader(new FileReader(
	    					applicationData.getWorkingDirectory() + File.separator + "ClassifierOne.scores"));
	    	}else{
	    		input = new BufferedReader(new FileReader(
	    				applicationData.getWorkingDirectory() + File.separator + "ClassifierTwo.scores"));
	    	}
    		JFileChooser fc;				    	
	    	fc = new JFileChooser(appDataForSaving.getWorkingDirectory());
	    	FileNameExtensionFilter filter = new FileNameExtensionFilter(
		            "Score Files", "scores");
		    fc.setFileFilter(filter);	
			int returnVal = fc.showOpenDialog(parent);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();	                        	
            	String savingFilename = file.getAbsolutePath();
				if(savingFilename.indexOf(".scores") == -1)
					savingFilename += ".scores";				
		    	BufferedWriter output = new BufferedWriter(new FileWriter(savingFilename));
		    	String line;
		    	while((line = input.readLine()) != null){
		    		output.write(line);
		    		output.newLine();
		    		output.flush();
		    	}
		    	output.close();
		    	input.close();
			}
			else
				return;	    			
    	}catch(FileNotFoundException e){
    		JOptionPane.showMessageDialog(parent,"Score file does not exist!","Save File",JOptionPane.ERROR_MESSAGE);
    	}catch(Exception ex){ex.printStackTrace();}
    }
          
    private double validateFieldAsThreshold(String name,JTextField component) throws Exception{
    	try{    		
    		double threshold = Double.parseDouble(component.getText());
    		BigDecimal b = new BigDecimal(0);
    		b = b.add(BigDecimal.valueOf(threshold));
    		b = b.multiply(new BigDecimal("100000"));    		
    		if(threshold < 0.0 || threshold > 1.0){    			
    			JOptionPane.showMessageDialog(parent,"Threshold must be within 0.0 to 1.0","ERROR",
    	   				JOptionPane.ERROR_MESSAGE);
    			component.requestFocusInWindow();
       			throw new Exception();
    		}    		    		
    		if(b.intValue() % 100 != 0){
    			System.out.println(b.intValue());
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
    
    private void computeStats(int classifierType){    				   
    	JTextField rangeTextField = null;
    	JTextField thresholdTextField = null;
    	ClassifierResults classifierResults = null;
    	JTextArea classifierTextArea = null;
    	String filename = null;
    	switch(classifierType){
    	case 1: thresholdTextField = classifierOneThresholdTextField; 
    			classifierResults = classifierOneResults; classifierTextArea = levelOneClassifierOutputTextArea; 
    			filename = "ClassifierOne_" + this.classifierOneRandomNumber + ".scores"; break;
    	case 2: rangeTextField = classifierTwoRangeTextField; thresholdTextField = classifierTwoThresholdTextField; 
    			classifierResults = classifierTwoResults; classifierTextArea = levelTwoClassifierOutputTextArea; 
    			filename = "ClassifierTwo.scores"; break;
    	}
    	try{
    		int range = 0;
    		if(rangeTextField != null)
    			range = validateFieldAsRange("Range Field", rangeTextField);
    		double threshold = validateFieldAsThreshold("Threshold Field", thresholdTextField);
    		//Display Statistics by reading the ClassifierOne.scores
			PredictionStats classifierStats = new PredictionStats(
				applicationData.getWorkingDirectory() + File.separator + filename,range,threshold);
			//display(double range)
			classifierStats.updateDisplay(classifierResults, classifierTextArea, true);
    	}catch(Exception ex){/*Dun have to print stack trace here because it has already been taken care of inside validateField method()*/}    	
    }
   
    private boolean validateStatsSettings(int classifierType){    	
    	JTextField rangeTextField = null;
    	JTextField thresholdTextField = null;    	
    	switch(classifierType){
    	case 1: rangeTextField = null; thresholdTextField = classifierOneThresholdTextField; break;
    	case 2: rangeTextField = classifierTwoRangeTextField; thresholdTextField = classifierTwoThresholdTextField; break;
    	}
    	try{
    		if(rangeTextField != null)
    			validateFieldAsRange("Range Field", rangeTextField);
    		validateFieldAsThreshold("Threshold Field", thresholdTextField);
    	}catch(Exception ex){return false;/*Dun have to print stack trace here because it has already been taken care of inside validateField method()*/}
    	return true;
    }
    
    private void runClassifierTwo(){
    	appDataForSaving.terminateThread = false;
		//Run Classifier Two 	
		if(applicationData.isLocationIndexMinusOne == true){
				JOptionPane.showMessageDialog(parent,
					"Target Location is -1!",
					"Not Valid Option",JOptionPane.ERROR_MESSAGE);
					setClassifierTwoUpstreamField.requestFocusInWindow();
				return;
			}    				
		if(applicationData.getPositiveDataset2FromField() == -1 ||
			applicationData.getPositiveDataset2ToField() == -1 ||
			applicationData.getNegativeDataset2FromField() == -1 ||
			applicationData.getNegativeDataset2ToField() == -1){
				JOptionPane.showMessageDialog(parent,
					"Dataset 2 not set!",
					"Null Dataset",JOptionPane.ERROR_MESSAGE);
					setClassifierTwoUpstreamField.requestFocusInWindow();
				return;
			}
		int tempInt = 0;
		try{	    			    			
			final int setClassifierTwoUpstreamInt = 
				Integer.parseInt(setClassifierTwoUpstreamField.getText());
			tempInt = 1;
			final int setClassifierTwoDownstreamInt = 
				Integer.parseInt(setClassifierTwoDownstreamField.getText());
			if(setClassifierTwoDownstreamInt <= setClassifierTwoUpstreamInt){
				JOptionPane.showMessageDialog(parent,
					"Classifier Two Downstream should not be smaller or equal than its Upstream",
					"Evaluate Classifier Two",JOptionPane.ERROR_MESSAGE);
					setClassifierTwoUpstreamField.requestFocusInWindow();
				return;
			}
			if(setClassifierTwoDownstreamInt == 0 || setClassifierTwoUpstreamInt == 0){
				JOptionPane.showMessageDialog(parent,
					"Classifier Two Upstream or Downstream should not be 0",
					"Evaluate Classifier Two",JOptionPane.ERROR_MESSAGE);
					setClassifierTwoDownstreamField.requestFocusInWindow();
				return;
			}    			
			//AHFU_TEMP taken out temporarily
			/*if(setClassifierTwoUpstreamInt < 0 && 
				(setClassifierTwoUpstreamInt * -1) > 
					applicationData.getAvailableUpstreamForClassifierTwo()){
				JOptionPane.showMessageDialog(parent,
					"Classifier Two Available Upstream should not be smaller than set Upstream",
					"Evaluate Classifier Two",JOptionPane.ERROR_MESSAGE);
					setClassifierTwoUpstreamField.requestFocusInWindow();
				return;
			}    	
			if(setClassifierTwoDownstreamInt > 0 && 
				applicationData.getAvailableDownstreamForClassifierTwo() < setClassifierTwoDownstreamInt){
				JOptionPane.showMessageDialog(parent,
					"Classifier Two Available Downstream should not be smaller than set Downstream",
					"Evaluate Classifier Two",JOptionPane.ERROR_MESSAGE);
					setClassifierTwoDownstreamField.requestFocusInWindow();
				return;
			} */   				
			updateLabels();
			//Clear the output text area
			if(m_ClassifierEditor2.getValue() == null){
				JOptionPane.showMessageDialog(parent,"Please choose Classifier Two!",
					"Evaluate Classifier",JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(validateStatsSettings(2) == false)
				return;
			if(applicationData.getOneThread() == null){
     		synchronized(this){
				startButton2.setEnabled(false);
				//m_CEPanel2.setVisible(false);
				previousStepButton.setEnabled(false);
				stopButton2.setEnabled(true);
			}
			tabbedClassifierPane.setSelectedIndex(2);
			applicationData.setOneThread(new Thread(){      		
				public void run(){			
    				levelTwoClassifierOutputTextArea.setText("");
    				updateLabels();	    				
    				//need this so that we can store the settings used to train the classifier
		    		//as the applicationData settings might be changed before saving
		    		
		    		//Assuming that the only 2 things needed for 
		    		//classifierTwo is the upstream and downstream
		    		appDataForSaving.setSetUpstream(setClassifierTwoUpstreamInt);
		    		appDataForSaving.setSetDownstream(setClassifierTwoDownstreamInt);
		    		String classifierSettings = "";
		    		if (m_ClassifierEditor2.getValue() instanceof OptionHandler)
						classifierSettings = "" + Utils.joinOptions(((OptionHandler) 
							m_ClassifierEditor2.getValue()).getOptions());
		    		appDataForSaving.setClassifierTwoSettings(classifierSettings);
		    		int range = Integer.parseInt(classifierTwoRangeTextField.getText());
		    		double threshold = Double.parseDouble(classifierTwoThresholdTextField.getText());
		    		if(needNotTestRadioButton.isSelected())//No Testing
		    			classifierTwo = RunClassifier.startClassifierTwo(parent,appDataForSaving,
	    					levelTwoClassifierOutputTextArea,m_ClassifierEditor2,classifierOne,
	    					classifierTwoGraph,false,classifierTwoResults,range,threshold);
    				else if(dataset3RadioButton.isSelected())//Use Dataset 3
    					classifierTwo = RunClassifier.startClassifierTwo(parent,appDataForSaving,
	    					levelTwoClassifierOutputTextArea,m_ClassifierEditor2,classifierOne,
	    					classifierTwoGraph,true,classifierTwoResults,range,threshold);
    				else//Use X-Validation
    					classifierTwo = RunClassifier.xValidateClassifierTwo(parent,appDataForSaving,
	    					levelTwoClassifierOutputTextArea,m_ClassifierEditor2,classifierOne,
	    					Integer.parseInt(foldsField.getText()),classifierTwoGraph,classifierTwoResults,range,threshold,needNotOutputClassifier.isSelected());
	    			appDataForSaving.setClassifierTwo(classifierTwo);
		    		synchronized(this){
					      startButton.setEnabled(true);
					      stopButton.setEnabled(false);						      
					      startButton2.setEnabled(true);
					      previousStepButton.setEnabled(true);
						  stopButton2.setEnabled(false);
					      applicationData.setOneThread(null);
					      if(classifierTwo != null)
					      	saveClassifierTwoButton.setEnabled(true);
					      levelTwoClassifierOutputScrollPane.getVerticalScrollBar().setValue(
					    		  levelTwoClassifierOutputScrollPane.getVerticalScrollBar().getMaximum());
					      ClassifierPane.this.repaint();
					    }	
				}//end of run()					
      		});
			applicationData.getOneThread().setPriority(Thread.MIN_PRIORITY);
	     	applicationData.getOneThread().start();
    		}else{
		    	JOptionPane.showMessageDialog(parent,
		    		"Cannot start classifier two because still running classifier one","ERROR",
		    		JOptionPane.ERROR_MESSAGE);
		    }		
		}catch(Exception e){
			JOptionPane.showMessageDialog(parent,"Enter numbers only!","Number Format Exception",
				JOptionPane.ERROR_MESSAGE);
			if(tempInt == 0)
				setClassifierTwoUpstreamField.requestFocusInWindow();
			else 
				setClassifierTwoDownstreamField.requestFocusInWindow();
			return;
		}    		
    }
    
    private void runClassifierOne(final int randomNumberForClassifier){
    	//Run Classifier One
		if(m_ClassifierEditor.getValue() == null){
			JOptionPane.showMessageDialog(parent,"Please choose Classifier One!","Evaluate Classifier",
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(validateStatsSettings(1) == false){
			return;
		}
     	if(applicationData.getOneThread() == null){
     		synchronized(this){
				startButton.setEnabled(false);
				//m_CEPanel.setVisible(false);
				previousStepButton.setEnabled(false);
				stopButton.setEnabled(true);
			}
			tabbedClassifierPane.setSelectedIndex(0);
      		applicationData.setOneThread(new Thread(){      		
				public void run(){
				GeneticAlgorithmDialog gaDialog = ClassifierPane.this.gaDialog;
				if(ClassifierPane.this.gaPanel.isVisible() == false)
					gaDialog = null;
				//Clear the output text area
				updateLabels();
	    		levelOneClassifierOutputTextArea.setText("");
	    		
	    		//need this so that we can store the settings used to train the classifier
	    		//as the applicationData settings might be changed before saving
	    		appDataForSaving = new ApplicationData(applicationData);		    		
	    		
	    		String classifierSettings = "";
	    		if (m_ClassifierEditor.getValue() instanceof OptionHandler)
					classifierSettings = "" + Utils.joinOptions(((OptionHandler) m_ClassifierEditor.getValue()).getOptions());
	    		appDataForSaving.setClassifierOneSettings(classifierSettings);
	    		//disable classifier two save button
	    		saveClassifierTwoButton.setEnabled(false);		    	
	    		//int range = Integer.parseInt(classifierOneRangeTextField.getText());
	    		//comment this out because it does not make sense to have range for classifierone
	    		double threshold = Double.parseDouble(classifierOneThresholdTextField.getText());
	    		if(applicationData.isLocationIndexMinusOne == false){
	    			//for non -1 sequences
	    			if(needNotTestRadioButton.isSelected()){//no testing
	    				classifierOne = RunClassifier.startClassifierOne(parent,appDataForSaving,
		    				levelOneClassifierOutputTextArea,m_ClassifierEditor,classifierOneGraph,false,classifierOneResults,
		    				0,threshold);
	    			}else if(dataset3RadioButton.isSelected()){//use dataset3
		    			classifierOne = RunClassifier.startClassifierOne(parent,appDataForSaving,
		    				levelOneClassifierOutputTextArea,m_ClassifierEditor,classifierOneGraph,true,classifierOneResults,
		    				0,threshold);
	    			}else{//cross-validation	    		
		    			classifierOne = RunClassifier.xValidateClassifierOne(parent,appDataForSaving,
		    				levelOneClassifierOutputTextArea,m_ClassifierEditor,
		    				Integer.parseInt(foldsField.getText()),classifierOneGraph,classifierOneResults,
		    				0,threshold,needNotOutputClassifier.isSelected());
	    			}
	    		}
	    		else{
	    			//for -1 sequences
	    			if(needNotTestRadioButton.isSelected())//No testing
	    				classifierOne = 
		    				(Classifier)RunClassifierWithNoLocationIndex.startClassifierOneWithNoLocationIndex(
		    				parent,appDataForSaving,levelOneClassifierOutputTextArea,
		    				classifierOneGraph,false,classifierOneResults,0,threshold,
		    				m_ClassifierEditor.getValue().getClass().getName(),
		    				((Classifier)m_ClassifierEditor.getValue()).getOptions(), true, gaDialog,
		    				randomNumberForClassifier);
	    			else if(dataset3RadioButton.isSelected())//Use Dataset3
		    			classifierOne = 
		    				(Classifier)RunClassifierWithNoLocationIndex.startClassifierOneWithNoLocationIndex(
		    				parent,appDataForSaving,levelOneClassifierOutputTextArea,
		    				classifierOneGraph,true,classifierOneResults,0,threshold,
		    				m_ClassifierEditor.getValue().getClass().getName(),
		    				((Classifier)m_ClassifierEditor.getValue()).getOptions(), true, gaDialog,
		    				randomNumberForClassifier);
		    		else if(ClassifierPane.this.xValidationRadioButton.isSelected()){//do cross-validation
		    			GASettingsInterface settings = null;
		    			if(gaDialog != null)
		    				settings = gaDialog.getSettingsDialog();
		    			classifierOne = 
		    				RunClassifierWithNoLocationIndex.xValidateClassifierOneWithNoLocationIndex(
		    				parent,appDataForSaving,levelOneClassifierOutputTextArea,
		    				m_ClassifierEditor.getValue().getClass().getName(),
		    				((Classifier)m_ClassifierEditor.getValue()).getOptions(),
		    				Integer.parseInt(foldsField.getText()),classifierOneGraph,
		    				classifierOneResults,0,threshold,needNotOutputClassifier.isSelected(),
		    				gaDialog, settings, randomNumberForClassifier);
		    		}else if(ClassifierPane.this.jackKnifeRadioButton.isSelected())//do jackknife
		    			classifierOne = 
		    				RunClassifierWithNoLocationIndex.jackKnifeClassifierOneWithNoLocationIndex(
		    				parent,appDataForSaving,levelOneClassifierOutputTextArea,m_ClassifierEditor,
		    				Double.parseDouble(limitField.getText()),classifierOneGraph,classifierOneResults,0,threshold,
		    				needNotOutputClassifier.isSelected(), randomNumberForClassifier);
		    		else
		    			throw new Error("Unhandled case");
	    		}		    		
	    		//set this classifier to be saved
	    		appDataForSaving.setClassifierOne(classifierOne);
	    		
	    		//classifier one trained
	    		synchronized(this){			
		    		//enable the start button for classifier one
		    		//disable the stop button for classifier one
				      startButton.setEnabled(true);		
				      previousStepButton.setEnabled(true);
				      //m_CEPanel.setVisible(true);
				      stopButton.setEnabled(false);
				      if(applicationData.isLocationIndexMinusOne == false)
				      	startButton2.setEnabled(true);
				      if(classifierOne != null){
				      	saveClassifierOneButton.setEnabled(true);
				      	levelOneClassifierOutputScrollPane.getVerticalScrollBar().setValue(
					    		  levelOneClassifierOutputScrollPane.getVerticalScrollBar().getMaximum());
				      }
	    					    		
			      applicationData.setOneThread(null);
			      ClassifierPane.this.repaint();
				}				     
			}});
			applicationData.getOneThread().setPriority(Thread.MIN_PRIORITY);
	     	applicationData.getOneThread().start();
 		}else{
     		JOptionPane.showMessageDialog(parent,
	    		"Cannot start classifier one because still running classifier two","ERROR",
	    		JOptionPane.ERROR_MESSAGE);
     	}  				
    }
    
    private void saveClassifierTwo(){
    	JFileChooser fc;				    	
    	fc = new JFileChooser(appDataForSaving.getWorkingDirectory());
    	FileNameExtensionFilter filter = new FileNameExtensionFilter(
	            "Classifier Two Files", "classifiertwo");
	    fc.setFileFilter(filter);	
		int returnVal = fc.showOpenDialog(parent);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();	                        	
        	String savingFilename = file.getAbsolutePath();
			if(savingFilename.indexOf(".classifiertwo") == -1)
				savingFilename += ".classifiertwo";			
			try{				
				FileOutputStream fos1 = new FileOutputStream(savingFilename);
		        ObjectOutputStream oos1 = new ObjectOutputStream(fos1);
		        oos1.writeInt(2);
		        //Classifier One Stuff
		        String tempString = file.getName();
		        tempString = tempString.substring(0,tempString.indexOf("."));
		        oos1.writeObject(tempString);
				oos1.writeObject(appDataForSaving.getClassifierOneSettings());
		        oos1.writeObject(appDataForSaving.getDataset1Instances());
		        oos1.writeObject(appDataForSaving.getClassifierOne());
		        //newly added for version 1.1 and above
		        oos1.writeObject(appDataForSaving.getSequenceType());
		        oos1.writeInt(appDataForSaving.getScoringMatrixIndex());
		        oos1.writeInt(appDataForSaving.getCountingStyleIndex());
		        //Classifier Two stuff
		        oos1.writeInt(appDataForSaving.getSetUpstream());
		        oos1.writeInt(appDataForSaving.getSetDownstream());						        
		        oos1.writeObject(appDataForSaving.getClassifierTwoSettings());
		        oos1.writeObject(appDataForSaving.getDataset2Instances());
		        oos1.writeObject(appDataForSaving.getClassifierTwo());						        
				oos1.close();
				appDataForSaving.getStatusPane().setText("Classifier Two saved onto " + savingFilename);
			}catch(Exception e){e.printStackTrace();}
		}
    }
    
    private void saveClassifierOne(){
    	JFileChooser fc;				    	
    	fc = new JFileChooser(appDataForSaving.getWorkingDirectory());
//    	FileNameExtensionFilter filter = new FileNameExtensionFilter(
//	            "Classifier One Files", "classifierone");
//	    fc.setFileFilter(filter);	
		int returnVal = fc.showSaveDialog(parent);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();	                        	
        	String savingFilename = file.getAbsolutePath();
			if(savingFilename.indexOf(".classifierone") == -1)
				savingFilename += ".classifierone";			
			try{										        		        		        			        		       		        
				FileOutputStream fos1 = new FileOutputStream(savingFilename);
		        ObjectOutputStream oos1 = new ObjectOutputStream(fos1);
		        if(appDataForSaving.isLocationIndexMinusOne){
		        	oos1.writeInt(3);			        	
		        }
		        else{
		        	oos1.writeInt(1);			        	
		        }
		        String tempString = file.getName();
		        if(tempString.indexOf(".") != -1)
		        	tempString = tempString.substring(0,tempString.indexOf("."));		        		        
		        oos1.writeObject(tempString);			        
				oos1.writeObject(appDataForSaving.getClassifierOneSettings());
		        oos1.writeObject(appDataForSaving.getDataset1Instances());
		        oos1.writeObject(appDataForSaving.getClassifierOne());		
		        //newly added for version 1.1 and above
		        oos1.writeObject(appDataForSaving.getSequenceType());
		        oos1.writeInt(appDataForSaving.getScoringMatrixIndex());
		        oos1.writeInt(appDataForSaving.getCountingStyleIndex());
				oos1.close();
				appDataForSaving.getStatusPane().setText("Classifier One saved onto " + savingFilename);		
			}catch(Exception e){e.printStackTrace();}
		}
    }
    
    public void actionPerformed(ActionEvent ae){
    	if(ae.getSource().equals(classifierOneComputeButton)){       		
			computeStats(1);				
    	}else if(ae.getSource().equals(classifierTwoComputeButton)){    		
			computeStats(2);		
    	}else if(ae.getSource().equals(classifierOneSaveScoreFileButton)){
    		saveScoreFile(1);
    	}else if(ae.getSource().equals(classifierTwoSaveScoreFileButton)){
    		saveScoreFile(2);
    	}else if(ae.getSource().equals(previousStepButton)){
    		tabbedPane.setSelectedIndex(2);
    		tabbedPane.setEnabledAt(3,false);
    		tabbedPane.setEnabledAt(2,true);
    	}else if(ae.getSource().equals(saveClassifierOneButton)){    	
    		saveClassifierOne();
    	}else if(ae.getSource().equals(saveClassifierTwoButton)){
    		saveClassifierTwo();
    	}else if(ae.getSource().equals(startButton)){ 
    		this.classifierOneRandomNumber = new Random().nextInt();
    		runClassifierOne(this.classifierOneRandomNumber);
    	}else if(ae.getSource().equals(stopButton)){
    		if(applicationData.getOneThread() != null){        			
    			appDataForSaving.terminateThread = true;
		    }		    
    	}else if(ae.getSource().equals(startButton2)){   
    		runClassifierTwo();
    	}else if(ae.getSource().equals(stopButton2)){
    		if(applicationData.getOneThread() != null){      			
    			appDataForSaving.terminateThread = true;
		    }		    
    	}else if(ae.getSource().equals(needNotTestRadioButton)){
    		needNotTestRadioButton.setSelected(true);
    		dataset3RadioButton.setSelected(false);    	
    		xValidationRadioButton.setSelected(false);
    		this.jackKnifeRadioButton.setSelected(false);
    	}else if(ae.getSource().equals(dataset3RadioButton)){
    		needNotTestRadioButton.setSelected(false);
    		dataset3RadioButton.setSelected(true);    	
    		xValidationRadioButton.setSelected(false);
    		this.jackKnifeRadioButton.setSelected(false);
    	}else if(ae.getSource().equals(xValidationRadioButton)){
    		needNotTestRadioButton.setSelected(false);
    		dataset3RadioButton.setSelected(false);    	
    		xValidationRadioButton.setSelected(true);
    		this.jackKnifeRadioButton.setSelected(false);
    	}else if(ae.getSource().equals(this.jackKnifeRadioButton)){
    		needNotTestRadioButton.setSelected(false);
    		dataset3RadioButton.setSelected(false);    	
    		xValidationRadioButton.setSelected(false);
    		this.jackKnifeRadioButton.setSelected(true);
    	}else if(ae.getSource().equals(this.gaButton))
    		openGADialog();
    }
    
    private void openGADialog(){
    	this.gaDialog.pack();
    	this.gaDialog.setLocationRelativeTo(this);
    	this.gaDialog.setVisible(true);
    }
    
    public void updateLabels(){        	
    	int numberOfFeatures = 0;
    	if(applicationData.getDataset1Instances()!=null){
    		// -1 because class is counted
    		numberOfFeatures = applicationData.getDataset1Instances().numAttributes() - 1;
    		this.gaPanel.setVisible(false);
    	}else
    		this.gaPanel.setVisible(true);
    	numberOfFeaturesLabelR.setText("" + numberOfFeatures);    	    
    		
    	featureLeftMostPositionLabelR.setText("" + applicationData.getLeftMostPosition());
    	featureRightMostPositionLabelR.setText("" + applicationData.getRightMostPosition());
    }    
}