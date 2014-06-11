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

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

public class GASettings implements GASettingsInterface{
	/*
	 * Default values for running in Supercomputer mode
	 */
	private JComboBox scoringMatrixComboBox = new JComboBox();
	private JComboBox countingStyleComboBox = new JComboBox();	
	private JTextField outputIntervalTextField = new JTextField("1",3);
	private JTextField randomNumberTextField = new JTextField("0");//new Random().nextInt());
	private JTextField populationSizeTextField = new JTextField("1000");
	private JTextField selectionPercentageTextField = new JTextField("40");
	private JTextField crossoverPercentageTextField = new JTextField("30");
	private JTextField terminationGenerationTextField = new JTextField("300");
	//private JTextField timeLimitInSecondsTextField = new JTextField("NOT IN USE");
	private JTextField maxNMITextField = new JTextField("0.25");
	private JTextField maxCNMITextField = new JTextField("0.25");
	private JCheckBox kgramCheckBox = new JCheckBox("K-Gram",true);
	private JCheckBox multiplekgramCheckBox = new JCheckBox("Multiple K-Gram",true);
	private JCheckBox ratioofkgramCheckBox = new JCheckBox("Ratio of K-Gram",true);
	private JCheckBox positionSpecificCheckBox = new JCheckBox("Position Specific",true);
	private JCheckBox physiochemicalCheckBox = new JCheckBox("Physiochemical",true);
	private JTextField outputFeatureSizeTextField = new JTextField("1000");
	private JTextField dpiEpsilonTextField = new JTextField("0.15");
	//private JComboBox fitnessFunctionComboBox = new JComboBox();
	private JTextField generationYTextField = new JTextField("10");
	private JTextField subsetSelectionTextField = new JTextField("80");
	private String outputLocation;
	private boolean oversample;
	private boolean undersample;
	private int resamplingFold;
	
	public GASettings(){
		//this.timeLimitInSecondsTextField.setEnabled(false);
		scoringMatrixComboBox.addItem("Identity");
		scoringMatrixComboBox.addItem("Blosum 62");
		scoringMatrixComboBox.addItem("Structure-Derived");
		countingStyleComboBox.addItem("+1");
		countingStyleComboBox.addItem("+Score");
	}

	public int getSelectedScoringIndex(){ return this.scoringMatrixComboBox.getSelectedIndex(); }
	public int getSelectedCountingIndex(){ return this.countingStyleComboBox.getSelectedIndex(); }
	public int getOutputInterval(){ return Integer.parseInt(this.outputIntervalTextField.getText()); }
	public int getRandomNumber(){ return Integer.parseInt(this.randomNumberTextField.getText()); }
	public int getPopulationSize(){ return Integer.parseInt(this.populationSizeTextField.getText()); }
	public int getSelectionPercentage(){ return Integer.parseInt(this.selectionPercentageTextField.getText()); }
	public int getCrossoverPercentage(){ return Integer.parseInt(this.crossoverPercentageTextField.getText()); }
	public int getTerminationGeneration(){ return Integer.parseInt(this.terminationGenerationTextField.getText()); }
	//public long getTimeLimitInSeconds(){ return Long.parseLong(this.timeLimitInSecondsTextField.getText()); }
	public double getMaxNMI(){ return Double.parseDouble(this.maxNMITextField.getText()); }
	public double getMaxCNMI(){return Double.parseDouble(this.maxCNMITextField.getText());}
	public int getMaxEliteSize(){ return Integer.parseInt(this.outputFeatureSizeTextField.getText()); }
	public boolean isKgramSelected(){ return this.kgramCheckBox.isSelected(); }
	public boolean isMultipleKgramSelected(){ return this.multiplekgramCheckBox.isSelected(); }
	public boolean isRatioOfKgramSelected(){ return this.ratioofkgramCheckBox.isSelected(); }
	public boolean isPositionSpecificSelected(){ return this.positionSpecificCheckBox.isSelected(); }
	public boolean isPhysiochemicalSelected(){ return this.physiochemicalCheckBox.isSelected(); }
	//public String getFitnessFunctionString(){ return (String)this.fitnessFunctionComboBox.getSelectedItem(); }
	public double getDPIEpsilon(){return this.getDPIEpsilon();}
	public int getGenerationY(){ return Integer.parseInt(this.generationYTextField.getText()); }
	public int getSubsetSelection(){ return Integer.parseInt(this.subsetSelectionTextField.getText()); }
	public String getOutputLocation(){return this.outputLocation;}
	public boolean getOversample(){return this.oversample;}
	public boolean getUndersample(){return this.undersample;}
	public int getSamplingRatio(){return this.resamplingFold;}
	
	public void setSelectedScoringString(String string){this.scoringMatrixComboBox.setSelectedItem(string);}
	public void setSelectedCountingString(String string){this.countingStyleComboBox.setSelectedItem(string);}
	public void setOutputInterval(String string){this.outputIntervalTextField.setText(string);}
	public void setRandomNumber(String string){this.randomNumberTextField.setText(string);}
	public void setPopulationSize(String string){this.populationSizeTextField.setText(string);}
	public void setSelectionPercentage(String string){this.selectionPercentageTextField.setText(string);}
	public void setCrossoverPercentage(String string){this.crossoverPercentageTextField.setText(string);}
	public void setTerminationGeneration(String string){this.terminationGenerationTextField.setText(string);}
	//public void setTimeLimitInSeconds(String string){this.timeLimitInSecondsTextField.setText(string);}
	public void setMaxNMI(String string){this.maxNMITextField.setText(string);}
	public void setMaxCNMI(String string){this.maxCNMITextField.setText(string);}
	public void setOutputSize(String string){this.outputFeatureSizeTextField.setText(string);}
	public void setKgramSelected(boolean b){this.kgramCheckBox.setSelected(b);}
	public void setMultipleKgramSelected(boolean b){this.multiplekgramCheckBox.setSelected(b);}
	public void setRatioOfKgramSelected(boolean b){this.ratioofkgramCheckBox.setSelected(b);}
	public void setPositionSpecificSelected(boolean b){this.positionSpecificCheckBox.setSelected(b);}
	public void setPhysiochemicalSelected(boolean b){this.physiochemicalCheckBox.setSelected(b);}
	//public void setFitnessFunction(String string){this.fitnessFunctionComboBox.setSelectedItem(string);}
	public void setDPIEpsilon(String string){this.dpiEpsilonTextField.setText(string);}
	public void setGenerationY(String string){this.generationYTextField.setText(string);}
	public void setSubsetSelection(String string){this.subsetSelectionTextField.setText(string);}
	public void setOutputLocation(String outputLocation){this.outputLocation = outputLocation;}
	public void setRandomNumber(int randomNumber){this.randomNumberTextField.setText(""+randomNumber);}
	public void setOversample(boolean oversample){this.oversample = oversample;}
	public void setUndersample(boolean undersample){this.undersample = undersample;}
	public void setResamplingFold(int fold){this.resamplingFold = fold;}
}
