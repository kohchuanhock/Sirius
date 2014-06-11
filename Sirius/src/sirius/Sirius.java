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
package sirius;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.UIManager;

import sirius.main.ApplicationData;
import sirius.trainer.features.Feature;
import sirius.trainer.features.gui.geneticalgorithm.GASettings;
import sirius.trainer.features.gui.geneticalgorithm.GeneticAlgorithm;
import sirius.trainer.features.subsetselection.GreedyForwardSubsetSearch;
import sirius.trainer.step1.DefineDataPane;
import sirius.trainer.step2.DefineFeaturePane;
import sirius.trainer.step2.FeatureTableModel;
import sirius.trainer.step4.RunClassifierWithNoLocationIndex;
import sirius.utils.PredictionStats;
import weka.core.Instances;

public class Sirius {
	public final static long version = 0303;
    
    public static void main(String[] args) {
    	try{
			// Set System L&F
		    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e) {
			e.printStackTrace();
		}
		if(args.length == 0){
			/*
			 * Run Sirius with GUI
			 */
			MainFrame frame = new MainFrame();
			frame.setVisible(true);
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		}else if(args.length == 2){
			/*
			 * Run Sirius with Console
			 */
			try{
				int optionNum = Integer.parseInt(args[0]);
				File settingsFile = new File(args[1]);
				Hashtable<String,String> settingsHashtable = new Hashtable<String, String>();
				BufferedReader input = new BufferedReader(new FileReader(settingsFile));
				String line;
				while((line = input.readLine()) != null){
					StringTokenizer st = new StringTokenizer(line, "=>");
					settingsHashtable.put(st.nextToken(), st.nextToken());
				}
				switch(optionNum){
					case 1: runClassifierAndObtainMCCUsingDataset3(settingsHashtable); break;
					case 2: runGeneticAlgorithm(settingsHashtable); break;
					case 3: runNNSearch(settingsHashtable); break;
					case 4: runXValidationWithGA(settingsHashtable); break;
					default: input.close(); throw new Exception();	
				}
				input.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}else{
			System.out.println("Error!");
			System.err.println("You should either run with (no args) or (2 args - OptionNum, SettingsFileName)");
			System.out.println("Option 1) Run Classifier and obtain MCC");
			System.out.println("Option 2) Run GeneticAlgorithm");
			System.out.println("Option 3) Run NNSearch");
			System.out.println("Option 4) Run XValidation With GA");
		}
    }
    
    private static void runXValidationWithGA(Hashtable<String, String> settings) throws Exception{
    	ApplicationData appData = new ApplicationData();
    	//STEP1:
    	appData.setWorkingDirectory(settings.get("WorkingDirectory"));
    	//Check if working directory exists - if not, create it
    	File file=new File(appData.getWorkingDirectory());
    	boolean exists = file.exists();
    	if (!exists) {
    		boolean success = file.mkdirs();
    		if(!success)
    			throw new Exception("Unable to create Working Directory: " + appData.getWorkingDirectory());
    	}
    	DefineDataPane step1Pane = new DefineDataPane(null, null, appData);
    	int count = 1;
    	while(settings.containsKey("PositiveStep1File" + count)){    		
    		step1Pane.addFileMethod(settings.get("PositiveStep1File" + count), null, step1Pane.getPositiveStep1TableModel());
    		count++;
    	}
    	count = 1;
    	while(settings.containsKey("NegativeStep1File" + count)){    		
    		step1Pane.addFileMethod(settings.get("NegativeStep1File" + count), null, step1Pane.getNegativeStep1TableModel());
    		count++;
    	}    	
		appData.setPositiveStep1TableModel(step1Pane.getPositiveStep1TableModel());
		appData.setNegativeStep1TableModel(step1Pane.getNegativeStep1TableModel());
		appData.setDatasetsValue(
				Integer.parseInt(settings.get("Dataset1PosFrom")), 
				Integer.parseInt(settings.get("Dataset1PosTo")),-1,-1,-1,-1,
				Integer.parseInt(settings.get("Dataset1NegFrom")), 
				Integer.parseInt(settings.get("Dataset1NegTo")),-1,-1,-1,-1);	
		if(settings.get("SequenceType").equals("DNA")){
			appData.setSequenceType("DNA");
			appData.setScoringMatrixIndex(0);
			appData.setCountingStyleIndex(0);
		}else
			appData.setSequenceType("PROTEIN");					
		//Also store the sequenceLengthInformation
		step1Pane.updateSequenceInformation();
		
		//GeneticAlgorithm
		//Check if output directory exists - if not, create it
    	file = new File(settings.get("GAOutputLocation"));
    	exists = file.exists();
    	if (!exists) {
    		boolean success = file.mkdirs();
    		if(!success)
    			throw new Exception("Unable to create Working Directory: " + settings.get("GAOutputLocation"));
    	}
		GASettings sDialog = new GASettings();
		sDialog.setSelectedScoringString(settings.get("GAScoring"));
		sDialog.setSelectedCountingString(settings.get("GACounting"));
		sDialog.setOutputInterval(settings.get("GAOutputInterval"));
		sDialog.setRandomNumber(settings.get("GARandomNumber"));
		sDialog.setPopulationSize(settings.get("GAPopulationSize"));
		sDialog.setSelectionPercentage(settings.get("GASelectionPercentage"));
		sDialog.setCrossoverPercentage(settings.get("GACrossoverPercentage"));
		sDialog.setTerminationGeneration(settings.get("GATerminationGeneration"));
		sDialog.setMaxNMI(settings.get("GAMaxCorrelation"));
		sDialog.setOutputSize(settings.get("GAOutputSize"));
		sDialog.setKgramSelected(Boolean.parseBoolean(settings.get("GAKGram")));
		sDialog.setMultipleKgramSelected(Boolean.parseBoolean(settings.get("GAMultiple")));
		sDialog.setRatioOfKgramSelected(Boolean.parseBoolean(settings.get("GARatio")));
		sDialog.setPositionSpecificSelected(Boolean.parseBoolean(settings.get("GAPosition")));
		sDialog.setPhysiochemicalSelected(Boolean.parseBoolean(settings.get("GAPhysiochemical")));
		//sDialog.setFitnessFunction(settings.get("GAFitnessFunction"));
		sDialog.setDPIEpsilon(settings.get("GADPIEpsilon"));
		sDialog.setGenerationY(settings.get("GAGenerationY"));
		sDialog.setOutputLocation(file.getAbsolutePath());
		sDialog.setOversample(Boolean.parseBoolean(settings.get("GAOversample")));
		sDialog.setUndersample(Boolean.parseBoolean(settings.get("GAUndersample")));
		sDialog.setResamplingFold(Integer.parseInt(settings.get("GAResampleFold")));
		
		//For x-validation
		int foldNum = Integer.parseInt(settings.get("FoldNum"));
		List<String> classifierOptionList = new ArrayList<String>();
		count = 1;
		while(settings.containsKey("ClassifierOption"+count)){
			classifierOptionList.add(settings.get("ClassifierOption"+count));
			count++;
		}
		String[] classifierOptions = new String[classifierOptionList.size()];
		for(int x = 0; x < classifierOptions.length; x++)
			classifierOptions[x] = classifierOptionList.get(x);		
		
		RunClassifierWithNoLocationIndex.xValidateClassifierOneWithNoLocationIndex(null,
		    	appData,null,settings.get("ClassifierName"),classifierOptions,foldNum,null,null,0,0.5,
		    	false,null,sDialog, new Random().nextInt());
    }
    
    private static void runGeneticAlgorithm(Hashtable<String,String> settings) throws Exception{
    	ApplicationData appData = new ApplicationData();
    	//STEP1:
    	appData.setWorkingDirectory(settings.get("WorkingDirectory"));
    	//Check if working directory exists - if not, create it
    	File file=new File(appData.getWorkingDirectory());
    	boolean exists = file.exists();
    	if (!exists) {
    		boolean success = file.mkdirs();
    		if(!success)
    			throw new Exception("Unable to create Working Directory: " + appData.getWorkingDirectory());
    	}
    	DefineDataPane step1Pane = new DefineDataPane(null, null, appData);
    	int count = 1;
    	while(settings.containsKey("PositiveStep1File" + count)){    		
    		step1Pane.addFileMethod(settings.get("PositiveStep1File" + count), null, step1Pane.getPositiveStep1TableModel());
    		count++;
    	}
    	count = 1;
    	while(settings.containsKey("NegativeStep1File" + count)){    		
    		step1Pane.addFileMethod(settings.get("NegativeStep1File" + count), null, step1Pane.getNegativeStep1TableModel());
    		count++;
    	}    	
		appData.setPositiveStep1TableModel(step1Pane.getPositiveStep1TableModel());
		appData.setNegativeStep1TableModel(step1Pane.getNegativeStep1TableModel());
		appData.setDatasetsValue(
				Integer.parseInt(settings.get("Dataset1PosFrom")), 
				Integer.parseInt(settings.get("Dataset1PosTo")),-1,-1,-1,-1,
				Integer.parseInt(settings.get("Dataset1NegFrom")), 
				Integer.parseInt(settings.get("Dataset1NegTo")),-1,-1,-1,-1);	
		if(settings.get("SequenceType").equals("DNA")){
			appData.setSequenceType("DNA");
			appData.setScoringMatrixIndex(0);
			appData.setCountingStyleIndex(0);
		}else
			appData.setSequenceType("PROTEIN");					
		//Also store the sequenceLengthInformation
		step1Pane.updateSequenceInformation();
		
		//GeneticAlgorithm
		//Check if output directory exists - if not, create it
    	file = new File(settings.get("GAOutputLocation"));
    	exists = file.exists();
    	if (!exists) {
    		boolean success = file.mkdirs();
    		if(!success)
    			throw new Exception("Unable to create Working Directory: " + settings.get("GAOutputLocation"));
    	}
		GASettings sDialog = new GASettings();
		sDialog.setSelectedScoringString(settings.get("GAScoring"));
		sDialog.setSelectedCountingString(settings.get("GACounting"));
		sDialog.setOutputInterval(settings.get("GAOutputInterval"));
		sDialog.setRandomNumber(settings.get("GARandomNumber"));
		sDialog.setPopulationSize(settings.get("GAPopulationSize"));
		sDialog.setSelectionPercentage(settings.get("GASelectionPercentage"));
		sDialog.setCrossoverPercentage(settings.get("GACrossoverPercentage"));
		sDialog.setTerminationGeneration(settings.get("GATerminationGeneration"));
		//sDialog.setNumOfThreads(settings.get("GANumOfThreads"));
		//sDialog.setTimeLimitInSeconds(settings.get("GATimeLimitInSeconds"));
		sDialog.setMaxNMI(settings.get("GAMaxCorrelation"));
		sDialog.setOutputSize(settings.get("GAOutputSize"));
		sDialog.setKgramSelected(Boolean.parseBoolean(settings.get("GAKGram")));
		sDialog.setMultipleKgramSelected(Boolean.parseBoolean(settings.get("GAMultiple")));
		sDialog.setRatioOfKgramSelected(Boolean.parseBoolean(settings.get("GARatio")));
		sDialog.setPositionSpecificSelected(Boolean.parseBoolean(settings.get("GAPosition")));
		sDialog.setPhysiochemicalSelected(Boolean.parseBoolean(settings.get("GAPhysiochemical")));
		//sDialog.setFitnessFunction(settings.get("GAFitnessFunction"));
		sDialog.setDPIEpsilon(settings.get("GADPIEpsilon"));
		sDialog.setSubsetSelection(settings.get("GASubsetSelection"));
		sDialog.setGenerationY(settings.get("GAGenerationY"));				
		sDialog.setOversample(Boolean.parseBoolean(settings.get("GAOversample")));
		sDialog.setUndersample(Boolean.parseBoolean(settings.get("GAUndersample")));
		sDialog.setResamplingFold(Integer.parseInt(settings.get("GAResampleFold")));		
		new GeneticAlgorithm(sDialog,settings.get("GAOutputLocation"), appData,null,null,null,null, -1, -1);		
    }
    
    private static void runClassifierAndObtainMCCUsingDataset3(Hashtable<String,String> settings) throws Exception{
    	ApplicationData appData = new ApplicationData();
    	//STEP1:
    	appData.setWorkingDirectory(settings.get("WorkingDirectory"));
    	//Check if working directory exists - if not, create it
    	File file=new File(appData.getWorkingDirectory());
    	boolean exists = file.exists();
    	if (!exists) {
    		boolean success = file.mkdirs();
    		if(!success)
    			throw new Exception("Unable to create Working Directory: " + appData.getWorkingDirectory());
    	}
    	DefineDataPane step1Pane = new DefineDataPane(null,null,appData);
    	int count = 1;
    	while(settings.containsKey("PositiveStep1File" + count)){    		
    		step1Pane.addFileMethod(settings.get("PositiveStep1File" + count), null, step1Pane.getPositiveStep1TableModel());
    		count++;
    	}
    	count = 1;
    	while(settings.containsKey("NegativeStep1File" + count)){    		
    		step1Pane.addFileMethod(settings.get("NegativeStep1File" + count), null, step1Pane.getNegativeStep1TableModel());
    		count++;
    	}    	
    	appData.setPositiveStep1TableModel(step1Pane.getPositiveStep1TableModel());
    	appData.setNegativeStep1TableModel(step1Pane.getNegativeStep1TableModel());		
    	appData.setDatasetsValue(
				Integer.parseInt(settings.get("Dataset1PosFrom")), 
				Integer.parseInt(settings.get("Dataset1PosTo")),-1,-1, 
				Integer.parseInt(settings.get("Dataset3PosFrom")),
				Integer.parseInt(settings.get("Dataset3PosTo")),
				Integer.parseInt(settings.get("Dataset1NegFrom")), 
				Integer.parseInt(settings.get("Dataset1NegTo")),-1,-1, 
				Integer.parseInt(settings.get("Dataset3NegFrom")),
				Integer.parseInt(settings.get("Dataset3NegTo")));	
		if(settings.get("SequenceType").equals("DNA")){
			appData.setSequenceType("DNA");
			appData.setScoringMatrixIndex(0);
			appData.setCountingStyleIndex(0);
		}else{
			appData.setSequenceType("PROTEIN");
			if(settings.get("Scoring").equals("Identity"))
				appData.setScoringMatrixIndex(0);
			else
				appData.setScoringMatrixIndex(1);
			
			if(settings.get("Counting").equals("+1"))
				appData.setCountingStyleIndex(0);
			else
				appData.setCountingStyleIndex(1);
		}
		//Also store the sequenceLengthInformation
		step1Pane.updateSequenceInformation();				
		
		//STEP2:		
		DefineFeaturePane step2Pane = new DefineFeaturePane(null,null,appData,null);
		step2Pane.loadFeatures(settings.get("FeatureFileAbsolutePath"), settings.get("FeatureFileParentDirectory"));
		step2Pane.generateFeatures(false);		
		FeatureTableModel featureTableModel = step2Pane.getFeatureTableModel();
		
		//STEP3:
		while(appData.getOneThread() != null)
			Thread.sleep(1000);
		Instances dataset1Instances = new Instances(new BufferedReader(new FileReader(
				appData.getWorkingDirectory() + File.separator + "Dataset1.arff")));
		appData.setDataset1Instances(dataset1Instances);
		
		//STEP4:		
		List<String> classifierOptionList = new ArrayList<String>();
		count = 1;
		while(settings.containsKey("ClassifierOption"+count)){
			classifierOptionList.add(settings.get("ClassifierOption"+count));
			count++;
		}
		String[] classifierOptions = new String[classifierOptionList.size()];
		for(int x = 0; x < classifierOptions.length; x++)
			classifierOptions[x] = classifierOptionList.get(x);			
		if(Integer.parseInt(settings.get("Dataset3PosFrom")) == -1){
			//X-validation
			PredictionStats temp = (PredictionStats)RunClassifierWithNoLocationIndex.jackKnifeClassifierOneWithNoLocationIndex(
					null,appData,null,
		        	null,-1,null,null,0, 0.5, false, settings.get("ClassifierName"),classifierOptions,false,
		        	new Random().nextInt());
			int index = temp.getMaxMCCIndex();
			//TP	FP	Coverage	Accuracy	FPrate	maxMCC
			System.out.println(temp.getTP(index) + "\t" + temp.getFP(index) + "\t" + temp.getCoverage(index) + "\t" + temp.getAccuracy(index) + "\t" +
					temp.getFPRate(index) + "\t" + temp.getMaxMCC());
		}else{
			if(settings.get("SubsetSelection") == null){
				//Dataset3 test - Blind test set
				PredictionStats temp = (PredictionStats)RunClassifierWithNoLocationIndex.startClassifierOneWithNoLocationIndex(
						null,appData,null,null,true,null,0,0.5,settings.get("ClassifierName"),classifierOptions,false,null,
						new Random().nextInt());
				int index = temp.getMaxMCCIndex();
				//TP	FP	Coverage	Accuracy	FPrate	maxMCC
				System.out.println(temp.getTP(index) + "\t" + temp.getFP(index) + "\t" + temp.getCoverage(index) + "\t" + temp.getAccuracy(index) + "\t" +
						temp.getFPRate(index) + "\t" + temp.getMaxMCC());
			}else{
				//Run subsetselection
				//Run a GreedyForwardSubsetSelection
				List<Feature> selectedList = new GreedyForwardSubsetSearch().selectSubset(featureTableModel.getData(), 
						appData.getWorkingDirectory() + File.separator + "SubsetSelectionScores.txt", appData);
				try{
					BufferedWriter output = new BufferedWriter(new FileWriter(appData.getWorkingDirectory() + File.separator + "SubsetSelection.features"));					
					BufferedWriter output2 = new BufferedWriter(new FileWriter("." + File.separator + "Final_G" + settings.get("Generation") + 
							"(All)_Top0.features"));
					for(int x = 0; x < selectedList.size(); x++){
						output.write("Step 2: " + selectedList.get(x).saveString(null));
						output.newLine();
						output2.write("Step 2: " + selectedList.get(x).saveString(null));
						output2.newLine();
					}
					output.close();
					output2.close();
				}catch(Exception e){e.printStackTrace();}
			}
		}		
    }
    
    private static void runNNSearch(Hashtable<String, String> settings) throws Exception{
    	throw new Error("This method is not ready yet!");
    }
   
    /*
    *	Testing out the Markov Model claimed by MSB paper titled, "Temporal switching and cell-to-cell variability in Ca2+ release activity in mammalian cells" 
    * 
    *  public static void test(){
    	Random r = new Random();
    	final double p = r.nextDouble();
    	final double q = r.nextDouble();
    	long x = r.nextLong();    	
    	long y = r.nextLong();    	
    	if(x < 0)
    		x*=-1;
    	if(y < 0)
    		y*=-1;
    	
    	long maxCount = 999999999;
    	long count = 0;
    	double difference = 1;
    	    	
    	while(count < maxCount && difference > 0.00000000000000001){
    		long newX = Math.round((x * (1-p)) + (y * q));
    		long newY = Math.round(((1-q) * y) + (x * p));
    		difference = ((x+0.0)/(x+y)) - ((newX+0.0)/(newY + newX));
    		if(difference < 0)
    			difference *= -1;
    		x = newX;
    		y = newY;
    		count++;
    	}    
    }*/
}