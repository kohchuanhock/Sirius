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
package sirius.trainer.features.gui.geneticalgorithm;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class SettingsDialog extends JDialog implements ActionListener, GASettingsInterface{
	/*
	 * Default settings for running in GUI mode
	 */
	static final long serialVersionUID = sirius.Sirius.version;
	private JComboBox scoringMatrixComboBox = new JComboBox();
	private JComboBox countingStyleComboBox = new JComboBox();	
	private JTextField outputIntervalTextField = new JTextField("10",3);
	private JTextField randomNumberTextField = new JTextField("0");//+new Random().nextInt());
	private JTextField populationSizeTextField = new JTextField("1000");
	private JTextField selectionPercentageTextField = new JTextField("10");
	private JTextField crossoverPercentageTextField = new JTextField("10");
	private JTextField terminationGenerationTextField = new JTextField("100");
	private JTextField maxNMITextField = new JTextField("0.25");
	private JTextField maxCNMITextField = new JTextField("-1");
	private JCheckBox kgramCheckBox = new JCheckBox("K-Gram",true);
	private JCheckBox multiplekgramCheckBox = new JCheckBox("Multiple K-Gram",false);
	private JCheckBox ratioofkgramCheckBox = new JCheckBox("Ratio of K-Gram",false);
	private JCheckBox positionSpecificCheckBox = new JCheckBox("Position Specific",true);
	private JCheckBox physiochemicalCheckBox = new JCheckBox("Physiochemical",true);
	private JButton applyButton = new JButton(" Apply ");
	private JButton closeButton = new JButton(" Close ");
	private JTextField eliteFeatureSizeTextField = new JTextField("100");
	private JTextField dpiEpsilonTextField = new JTextField("0.15");
	private JTextField generationYTextField = new JTextField("10");
	private JTextField subsetSelectionTextField = new JTextField("100");
	private JCheckBox undersampleCheckBox = new JCheckBox("Undersample", false);
	private JTextField samplingRatioTextField = new JTextField("1");
	
	private int scoringIndex;
	private int countingIndex;
	private int ouputInterval;
	private int randomNumber;
	private int populationSize;
	private int selectionPercent;
	private int crossoverPercent;
	private int terminationGeneration;
	//private int numOfThreads;
	//private long timeLimitInSeconds;
	//private int samplingRatio;
	private double maxNMI;
	private double maxCNMI;
	private boolean isKGramSelected;
	private boolean isMultipleSelected;
	private boolean isRatioSelected;
	private boolean isPositionSelected;
	private boolean isPhysiochemicalSelected;
	private int eliteSize;
	private double dpiEpsilon;
	//private String fitnessFunctionString;
	//private int topX;
	private int generationY;
	private int subsetSelection;
	public String outputLocation;
	public boolean isApplyPressed = false;
	public boolean oversample = false;
	public boolean undersample = false;
	public int samplingRatio = 1;
	
	public int getSelectedScoringIndex(){ return this.scoringMatrixComboBox.getSelectedIndex(); }
	public int getSelectedCountingIndex(){ return this.countingStyleComboBox.getSelectedIndex(); }
	public int getOutputInterval(){ return Integer.parseInt(this.outputIntervalTextField.getText()); }
	public int getRandomNumber(){ return Integer.parseInt(this.randomNumberTextField.getText()); }
	public int getPopulationSize(){ return Integer.parseInt(this.populationSizeTextField.getText()); }
	public int getSelectionPercentage(){ return Integer.parseInt(this.selectionPercentageTextField.getText()); }
	public int getCrossoverPercentage(){ return Integer.parseInt(this.crossoverPercentageTextField.getText()); }
	public int getTerminationGeneration(){ return Integer.parseInt(this.terminationGenerationTextField.getText()); }
	//public int getNumOfThreads(){ return Integer.parseInt(this.numOfThreadsTextField.getText()); }
	//public long getTimeLimitInSeconds(){ return Long.parseLong(this.timeLimitInSecondsTextField.getText()); }
	public int getSamplingRatio(){return Integer.parseInt(this.samplingRatioTextField.getText());}
	public double getMaxNMI(){ return Double.parseDouble(this.maxNMITextField.getText()); }
	public double getMaxCNMI(){return Double.parseDouble(this.maxCNMITextField.getText());}
	public int getMaxEliteSize(){ return Integer.parseInt(this.eliteFeatureSizeTextField.getText()); }
	public boolean isKgramSelected(){ return this.kgramCheckBox.isSelected(); }
	public boolean isMultipleKgramSelected(){ return this.multiplekgramCheckBox.isSelected(); }
	public boolean isRatioOfKgramSelected(){ return this.ratioofkgramCheckBox.isSelected(); }
	public boolean isPositionSpecificSelected(){ return this.positionSpecificCheckBox.isSelected(); }
	public boolean isPhysiochemicalSelected(){ return this.physiochemicalCheckBox.isSelected(); }
	public double getDPIEpsilon(){return this.dpiEpsilon;}
	//public String getFitnessFunctionString(){ return (String)this.fitnessFunctionComboBox.getSelectedItem(); }
	//public int getTopX(){ return Integer.parseInt(this.topXTextField.getText()); }
	public int getGenerationY(){ return Integer.parseInt(this.generationYTextField.getText()); }
	public int getSubsetSelection(){ return Integer.parseInt(this.subsetSelectionTextField.getText()); }
	public String getOutputLocation(){return this.outputLocation;}	
	public boolean getUndersample(){return this.undersampleCheckBox.isSelected();}
	//public int getSamplingRatio(){return this.samplingRatio;}
	
	public void setRandomNumber(int rand){ this.randomNumberTextField.setText(""+rand);}	
	
	
	private void updateSettings(){
		this.scoringIndex = this.scoringMatrixComboBox.getSelectedIndex();
		this.countingIndex = this.countingStyleComboBox.getSelectedIndex();
		this.ouputInterval = Integer.parseInt(this.outputIntervalTextField.getText());
		this.randomNumber = Integer.parseInt(this.randomNumberTextField.getText());
		this.populationSize = Integer.parseInt(this.populationSizeTextField.getText());
		this.selectionPercent = Integer.parseInt(this.selectionPercentageTextField.getText());
		this.crossoverPercent = Integer.parseInt(this.crossoverPercentageTextField.getText());
		this.terminationGeneration = Integer.parseInt(this.terminationGenerationTextField.getText());
		//this.numOfThreads = Integer.parseInt(this.numOfThreadsTextField.getText());
		//this.timeLimitInSeconds = Integer.parseInt(this.timeLimitInSecondsTextField.getText());
		this.samplingRatio = Integer.parseInt(this.samplingRatioTextField.getText());
		this.maxNMI = Double.parseDouble(this.maxNMITextField.getText());
		this.maxCNMI = Double.parseDouble(this.maxCNMITextField.getText());
		this.eliteSize = Integer.parseInt(this.eliteFeatureSizeTextField.getText());
		this.isKGramSelected = this.kgramCheckBox.isSelected();
		this.isMultipleSelected = this.multiplekgramCheckBox.isSelected();
		this.isRatioSelected = this.ratioofkgramCheckBox.isSelected();
		this.isPositionSelected = this.positionSpecificCheckBox.isSelected();
		this.isPhysiochemicalSelected = this.physiochemicalCheckBox.isSelected();
		this.dpiEpsilon = Double.parseDouble(this.dpiEpsilonTextField.getText());
		//this.fitnessFunctionString = (String)this.fitnessFunctionComboBox.getSelectedItem();
		//this.topX = Integer.parseInt(this.topXTextField.getText());
		this.generationY = Integer.parseInt(this.generationYTextField.getText());
		this.subsetSelection = Integer.parseInt(this.subsetSelectionTextField.getText());		
		this.undersample = this.undersampleCheckBox.isSelected();		
	}
	
	private void backToPreviousSettings(){
		this.scoringMatrixComboBox.setSelectedIndex(this.scoringIndex);
		this.countingStyleComboBox.setSelectedIndex(this.countingIndex);
		this.outputIntervalTextField.setText(this.ouputInterval + "");
		this.randomNumberTextField.setText(this.randomNumber + "");
		this.populationSizeTextField.setText(this.populationSize + "");
		this.selectionPercentageTextField.setText(this.selectionPercent + "");
		this.crossoverPercentageTextField.setText(this.crossoverPercent + "");
		this.terminationGenerationTextField.setText(this.terminationGeneration + "");
		//this.numOfThreadsTextField.setText(this.numOfThreads + "");
		//this.timeLimitInSecondsTextField.setText(this.timeLimitInSeconds + "");
		this.samplingRatioTextField.setText(this.samplingRatio + "");
		this.maxNMITextField.setText(this.maxNMI + "");
		this.maxCNMITextField.setText(this.maxCNMI + "");
		this.eliteFeatureSizeTextField.setText(this.eliteSize + "");
		this.kgramCheckBox.setSelected(this.isKGramSelected);
		this.multiplekgramCheckBox.setSelected(this.isMultipleSelected);
		this.ratioofkgramCheckBox.setSelected(this.isRatioSelected);
		this.positionSpecificCheckBox.setSelected(this.isPositionSelected);
		this.physiochemicalCheckBox.setSelected(this.isPhysiochemicalSelected);
		this.dpiEpsilonTextField.setText(this.dpiEpsilon + "");
		//this.fitnessFunctionComboBox.setSelectedItem(this.fitnessFunctionString);
		//this.topXTextField.setText(this.topX + "");
		this.generationYTextField.setText(this.generationY + "");
		this.subsetSelectionTextField.setText(this.subsetSelection + "");		
		this.undersampleCheckBox.setSelected(this.undersample);		
	}		
	
	public void setEnabled(boolean isEnabled){
		this.scoringMatrixComboBox.setEnabled(isEnabled);
		this.countingStyleComboBox.setEnabled(isEnabled);
		this.outputIntervalTextField.setEnabled(isEnabled);
		this.randomNumberTextField.setEnabled(isEnabled);
		this.populationSizeTextField.setEnabled(isEnabled);
		this.selectionPercentageTextField.setEnabled(isEnabled);		
		this.crossoverPercentageTextField.setEnabled(isEnabled);
		this.maxNMITextField.setEnabled(isEnabled);
		this.maxCNMITextField.setEnabled(isEnabled);
		this.kgramCheckBox.setEnabled(isEnabled);
		this.multiplekgramCheckBox.setEnabled(isEnabled);
		this.ratioofkgramCheckBox.setEnabled(isEnabled);
		this.positionSpecificCheckBox.setEnabled(isEnabled);
		this.physiochemicalCheckBox.setEnabled(isEnabled);
		this.applyButton.setEnabled(isEnabled);
		this.eliteFeatureSizeTextField.setEnabled(isEnabled);
		this.terminationGenerationTextField.setEnabled(isEnabled);
		//this.numOfThreadsTextField.setEnabled(isEnabled);
		//this.timeLimitInSecondsTextField.setEnabled(isEnabled);
		this.samplingRatioTextField.setEnabled(isEnabled);
		this.dpiEpsilonTextField.setEnabled(isEnabled);
		//this.fitnessFunctionComboBox.setEnabled(isEnabled);
		//this.topXTextField.setEnabled(isEnabled);
		this.generationYTextField.setEnabled(isEnabled);
		this.subsetSelectionTextField.setEnabled(isEnabled);		
		this.undersampleCheckBox.setEnabled(isEnabled);
	}
	
	public SettingsDialog(boolean calledFromStep2GA, String outputLocation){
		this.outputLocation = outputLocation;		
		
		//NorthPanel
		JPanel northPanel = new JPanel(new GridLayout(1,2,5,5));
		northPanel.add(createScoringAndCountingPanel());
		northPanel.add(createOutputSettingsPanel());
		//CenterPanel
		JPanel centerPanel = new JPanel(new GridLayout(1,2,5,5));
		centerPanel.add(createAlgorithmSettingsPanel());
		centerPanel.add(createFeatureSettingsPanel());
		//SouthPanel
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(this.applyButton);
		buttonPanel.add(this.closeButton);
		this.applyButton.addActionListener(this);
		this.closeButton.addActionListener(this);
		JPanel southPanel = new JPanel(new BorderLayout(5,5));
		southPanel.add(this.createStoppingCriteriaPanel(), BorderLayout.CENTER);
		southPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		if(calledFromStep2GA == false){
			this.applyButton.setText("Run");
			this.closeButton.setText("Cancel");
		}
		
		setTitle("Settings");
		setLayout(new BorderLayout(5,5));
		add(northPanel,BorderLayout.NORTH);
		add(centerPanel,BorderLayout.CENTER);
		add(southPanel,BorderLayout.SOUTH);
		
		this.updateSettings();
	}
	
	public SettingsDialog(String outputLocation){	
		this(true, outputLocation);
	}
	
	private JPanel createStoppingCriteriaPanel(){
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Additional Stopping Criteria"));
		panel.add(new JLabel("When total elite score is unsurpassed for "));
		//panel.add(this.topXTextField);
		//panel.add(new JLabel(" features did not change for "));
		panel.add(this.generationYTextField);
		panel.add(new JLabel(" generations."));
		return panel;
	}
	
	private JPanel createFeatureSettingsPanel(){
		JPanel northPanel = new JPanel(new GridLayout(4,2,5,5));
		northPanel.add(new JLabel("Max NMI: ",SwingConstants.RIGHT));
		northPanel.add(this.maxNMITextField);
		northPanel.add(new JLabel("Max Norm CMI: ", SwingConstants.RIGHT));
		northPanel.add(this.maxCNMITextField);
		northPanel.add(new JLabel("DPI Epsilon: ",SwingConstants.RIGHT));
		northPanel.add(this.dpiEpsilonTextField);
		northPanel.add(new JLabel("Max Elite Size: ", SwingConstants.RIGHT));
		northPanel.add(this.eliteFeatureSizeTextField);
		JPanel includePanel = new JPanel(new GridLayout(3,2,5,5));
		includePanel.setBorder(BorderFactory.createTitledBorder("Includes"));
		includePanel.add(this.kgramCheckBox);
		includePanel.add(this.multiplekgramCheckBox);
		includePanel.add(this.ratioofkgramCheckBox);
		includePanel.add(this.positionSpecificCheckBox);
		includePanel.add(this.physiochemicalCheckBox);
		JPanel featureSettingsPanel = new JPanel(new BorderLayout(5,5));
		featureSettingsPanel.setBorder(BorderFactory.createTitledBorder("Feature Settings"));
		featureSettingsPanel.add(northPanel,BorderLayout.NORTH);
		featureSettingsPanel.add(includePanel,BorderLayout.CENTER);
		return featureSettingsPanel;
	}
	
	private JPanel createAlgorithmSettingsPanel(){
		JPanel panel = new JPanel(new BorderLayout(5,5));
		panel.setBorder(BorderFactory.createTitledBorder("Algorithm Settings"));
		JPanel GASettingsPanel = new JPanel(new GridLayout(7,2,5,5));		
		GASettingsPanel.add(new JLabel("Random Number: ",SwingConstants.RIGHT));
		GASettingsPanel.add(this.randomNumberTextField);
		GASettingsPanel.add(new JLabel("Population Size: ",SwingConstants.RIGHT));
		GASettingsPanel.add(this.populationSizeTextField);
		GASettingsPanel.add(new JLabel("Selection %: ",SwingConstants.RIGHT));
		GASettingsPanel.add(this.selectionPercentageTextField);
		GASettingsPanel.add(new JLabel("Crossover %: ",SwingConstants.RIGHT));
		GASettingsPanel.add(this.crossoverPercentageTextField);
		GASettingsPanel.add(new JLabel("Termination Generation: ",SwingConstants.RIGHT));
		GASettingsPanel.add(this.terminationGenerationTextField);
		//GASettingsPanel.add(new JLabel("Number of Threads: ",SwingConstants.RIGHT));
		//GASettingsPanel.add(this.numOfThreadsTextField);
		//GASettingsPanel.add(new JLabel("Limit/Feature (secs): ",SwingConstants.RIGHT));
		//GASettingsPanel.add(this.timeLimitInSecondsTextField);
		GASettingsPanel.add(new JLabel("Subset Selection %: ",SwingConstants.RIGHT));
		GASettingsPanel.add(this.subsetSelectionTextField);
		GASettingsPanel.add(new JLabel("Sampling Ratio: ", SwingConstants.RIGHT));
		GASettingsPanel.add(this.samplingRatioTextField);
		panel.add(GASettingsPanel,BorderLayout.CENTER);
		JPanel samplingPanel = new JPanel();		
		samplingPanel.add(this.undersampleCheckBox);		
		panel.add(samplingPanel, BorderLayout.SOUTH);
		return panel;
	}
	
	private JPanel createOutputSettingsPanel(){
		JPanel savingIntervalPanel = new JPanel(new GridLayout(1,1,5,5));
		savingIntervalPanel.add(new JLabel("Output Interval: ",SwingConstants.RIGHT));
		savingIntervalPanel.add(this.outputIntervalTextField);
		JPanel outputSettingsPanel = new JPanel(new GridLayout(1,1,5,5));
		outputSettingsPanel.setBorder(BorderFactory.createTitledBorder("Output Settings"));
		outputSettingsPanel.add(savingIntervalPanel);		
		return outputSettingsPanel;
	}
	
	private JPanel createScoringAndCountingPanel(){
		//Scoring Panel					
		scoringMatrixComboBox.addItem("Identity");
		scoringMatrixComboBox.addItem("Blosum 62");
		scoringMatrixComboBox.addItem("Structure-Derived");
		//scoringMatrixComboBox.setSelectedIndex(1);
		JPanel scoringMatrixPanel = new JPanel();
		scoringMatrixPanel.setBorder(BorderFactory.createTitledBorder("Scoring Matrix"));
		scoringMatrixPanel.add(scoringMatrixComboBox);		
		countingStyleComboBox.addItem("+1              ");
		countingStyleComboBox.addItem("+Score          ");
		//countingStyleComboBox.setSelectedIndex(1);
		JPanel countingStylePanel = new JPanel();
		countingStylePanel.setBorder(BorderFactory.createTitledBorder("Counting Style"));		
		countingStylePanel.add(countingStyleComboBox);			
		JPanel scoringAndCountingPanel = new JPanel(new GridLayout(1,2,5,5));
		scoringAndCountingPanel.add(scoringMatrixPanel);
		scoringAndCountingPanel.add(countingStylePanel);		
		return scoringAndCountingPanel;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {		
		if(ae.getSource().equals(this.applyButton))
			this.apply();
		else if(ae.getSource().equals(this.closeButton)){
			this.backToPreviousSettings();
			this.dispose();	
		}
	}	
	
	private void apply(){
		try{
			int value = Integer.parseInt(outputIntervalTextField.getText());	
			if(value < 1)
				throw new Exception();
			Integer.parseInt(randomNumberTextField.getText());			
			value = Integer.parseInt(populationSizeTextField.getText());
			if(value < 1)
				throw new Exception();
			int selection = Integer.parseInt(selectionPercentageTextField.getText());
			if(selection < 0 || selection > 100)
				throw new Exception();
			int crossover = Integer.parseInt(crossoverPercentageTextField.getText());
			if(crossover < 0 || crossover > 100)
				throw new Exception();
			double dValue = Double.parseDouble(maxNMITextField.getText());
			if(dValue != -1 && (dValue <= 0.0 || dValue>= 1.0))
				throw new Exception();
			dValue = Double.parseDouble(this.maxCNMITextField.getText());
			if(dValue != -1 && (dValue <= 0.0 || dValue>= 1.0))
				throw new Exception();			
			value = Integer.parseInt(this.eliteFeatureSizeTextField.getText());
			if(value < 1)
				throw new Exception();
			if(selection + crossover > 100){
				JOptionPane.showMessageDialog(this, "Selection(%) + Crossover(%) MUST be less than or equal to 100(%)", "Error", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			value = Integer.parseInt(this.terminationGenerationTextField.getText());
			//value = Integer.parseInt(this.numOfThreadsTextField.getText());
			/*if(value < 1)
				throw new Exception();
			long valueL = Long.parseLong(this.timeLimitInSecondsTextField.getText());
			if(valueL < 1)
				throw new Exception();*/
			dValue = Double.parseDouble(this.dpiEpsilonTextField.getText());
			if(dValue != -1 && (dValue >= 1 || dValue <= 0)) throw new Exception();
			value = Integer.parseInt(this.samplingRatioTextField.getText());
			if(value < 1) throw new Exception();
			this.updateSettings();
			JOptionPane.showMessageDialog(this, "New settings applied", "Information", JOptionPane.INFORMATION_MESSAGE);			
			this.dispose();	
			this.isApplyPressed = true;
		}catch(Exception e){
			JOptionPane.showMessageDialog(this, "Please ensure values are properly set!", "Invalid settings", JOptionPane.ERROR_MESSAGE);
		}
	}	
}
