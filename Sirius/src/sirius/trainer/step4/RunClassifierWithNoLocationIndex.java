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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import sirius.main.ApplicationData;
import sirius.main.FastaFileManipulation;
import sirius.trainer.features.Feature;
import sirius.trainer.features.GenerateArff;
import sirius.trainer.features.GenerateFeatures;
import sirius.trainer.features.gui.geneticalgorithm.GASettingsInterface;
import sirius.trainer.features.gui.geneticalgorithm.GeneticAlgorithm;
import sirius.trainer.features.gui.geneticalgorithm.GeneticAlgorithmDialog;
import sirius.trainer.main.StatusPane;
import sirius.trainer.step1.Step1TableModel;
import sirius.trainer.step2.DefineFeaturePane;
import sirius.utils.ClassifierResults;
import sirius.utils.FastaFormat;
import sirius.utils.PredictionStats;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.gui.GenericObjectEditor;



public class RunClassifierWithNoLocationIndex {	
	private static Instances runDAandLoadResult(ApplicationData applicationData, GeneticAlgorithmDialog gaDialog, List<FastaFormat> posFastaList, List<FastaFormat> negFastaList){
		return runDAandLoadResult(applicationData, gaDialog, posFastaList, negFastaList, 0, 0);
	}
	
	private static Instances runDAandLoadResult(ApplicationData applicationData, GeneticAlgorithmDialog gaDialog, List<FastaFormat> posFastaList, List<FastaFormat> negFastaList, int foldCount,
			int randomNumber){
		//System.out.println(System.getProperty("java.io.tmpdir"));
		String outputFilename = gaDialog.getOutputLocation().getText() + File.separator + "GeneticAlgorithmFeatureGeneration" + new Random().nextInt() + "_" + randomNumber + "_" + foldCount + ".arff";	
		gaDialog.clearPreviousGAResults();
		gaDialog.setApplicationData(applicationData);
		gaDialog.run.setValue(true);		
		gaDialog.getSettingsDialog().setEnabled(false);
		gaDialog.setVisible(true);	
		gaDialog.setRandomNumber(new Random(gaDialog.getRandomNumber()).nextInt());
		gaDialog.run(true, posFastaList, negFastaList, outputFilename, foldCount, randomNumber);		
		try{
			while(gaDialog.isGAThreadAlive())
				Thread.sleep(500);			
			gaDialog.setVisible(false);
			return new Instances(new FileReader(outputFilename));
		}catch(Exception e){e.printStackTrace(); return null;}
	}
	
	private static Instances runDAandLoadResult(ApplicationData applicationData, GASettingsInterface gaSettings, List<FastaFormat> posFastaList, List<FastaFormat> negFastaList, int foldCount,
			int randomNumber){
		try{
			String outputFilename = gaSettings.getOutputLocation() + File.separator + "GeneticAlgorithmFeatureGeneration" + new Random().nextInt() + "_" + randomNumber + "_" + foldCount + ".arff";										
			gaSettings.setRandomNumber(new Random(gaSettings.getRandomNumber()).nextInt());
			new GeneticAlgorithm(gaSettings, gaSettings.getOutputLocation(), applicationData,null,null,null,null,posFastaList,negFastaList, foldCount, randomNumber);
			//Load MaxMCC features
			List<Feature> featureList = DefineFeaturePane.loadFeatureFile(
					gaSettings.getOutputLocation() + File.separator + "maxCFSFeature.features", 
					applicationData);				
			//generate features
			new GenerateFeatures(applicationData,featureList,posFastaList,negFastaList,outputFilename);		
			return new Instances(new FileReader(outputFilename));
		}catch(Exception e){e.printStackTrace(); return null;}
	}
	
	
    //For classifier one with blind test set 
    public static Object startClassifierOneWithNoLocationIndex(JInternalFrame parent,
    	ApplicationData applicationData,JTextArea classifierOneDisplayTextArea,
    	GraphPane myGraph,boolean test,ClassifierResults classifierResults,int range, double threshold,
    	String classifierName, String[] classifierOptions, boolean returnClassifier, 
    	GeneticAlgorithmDialog gaDialog, int randomNumberForClassifier){
		try{														
			
			if(gaDialog != null){
				//Run GA then load the result maxMCCFeatures into applicationData->Dataset1Instances
				int positiveDataset1FromInt = applicationData.getPositiveDataset1FromField();
		    	int positiveDataset1ToInt = applicationData.getPositiveDataset1ToField();
		    	int negativeDataset1FromInt = applicationData.getNegativeDataset1FromField();
		    	int negativeDataset1ToInt = applicationData.getNegativeDataset1ToField();
		    	FastaFileManipulation fastaFile = new FastaFileManipulation(
						applicationData.getPositiveStep1TableModel(),applicationData.getNegativeStep1TableModel(),
						positiveDataset1FromInt,positiveDataset1ToInt,negativeDataset1FromInt,negativeDataset1ToInt,
						applicationData.getWorkingDirectory());
		    	FastaFormat fastaFormat;
		    	List<FastaFormat> posFastaList = new ArrayList<FastaFormat>();
		    	List<FastaFormat> negFastaList = new ArrayList<FastaFormat>();
		    	while((fastaFormat = fastaFile.nextSequence("pos"))!=null){
		    		posFastaList.add(fastaFormat);
		    	}
		    	while((fastaFormat = fastaFile.nextSequence("neg"))!=null){
		    		negFastaList.add(fastaFormat);
		    	}
				applicationData.setDataset1Instances(runDAandLoadResult(applicationData, gaDialog, posFastaList, negFastaList));
			}
			
			StatusPane statusPane = applicationData.getStatusPane();			
			long totalTimeStart = System.currentTimeMillis(), totalTimeElapsed;						
			//Setting up training data set 1 for classifier one		
			if(statusPane != null)
				statusPane.setText("Setting up...");
  			//Load Dataset1 Instances
  			Instances inst = new Instances(applicationData.getDataset1Instances());  			
  			inst.setClassIndex(applicationData.getDataset1Instances().numAttributes() - 1);
  			applicationData.getDataset1Instances().setClassIndex(applicationData.getDataset1Instances().numAttributes() - 1);
			// for recording of time
  			long trainTimeStart = 0, trainTimeElapsed = 0;  			
			Classifier classifierOne = Classifier.forName(classifierName, classifierOptions);			
			/*//Used to show the classifierName and options so that I can use them for qsub
			System.out.println(classifierName);
			String[] optionString = classifierOne.getOptions();
			for(int x = 0; x < optionString.length; x++)
				System.out.println(optionString[x]);*/		
			if(statusPane != null)
				statusPane.setText("Training Classifier One... May take a while... Please wait...");
			//Record Start Time
			trainTimeStart = System.currentTimeMillis();
			//Train Classifier One				
			inst.deleteAttributeType(Attribute.STRING);
		    classifierOne.buildClassifier(inst);
		    //Record Total Time used to build classifier one
		    trainTimeElapsed = System.currentTimeMillis() - trainTimeStart;				   
			
			if(classifierResults != null){
				classifierResults.updateList(classifierResults.getClassifierList(), "Classifier: ", classifierName);
				classifierResults.updateList(classifierResults.getClassifierList(), "Training Data: ", applicationData.getWorkingDirectory() + 
	  					File.separator + "Dataset1.arff");
				classifierResults.updateList(classifierResults.getClassifierList(), "Time Used: ", Utils.doubleToString(trainTimeElapsed / 1000.0,2) + 
	  				" seconds");				
			}
		    if(test == false){
		    	//If Need Not Test option is selected
		    	if(statusPane != null)
		    		statusPane.setText("Done!");
		    	return classifierOne;
		    }
		    if(applicationData.terminateThread == true){
		    	//If Stop button is pressed
		    	if(statusPane != null)
		    		statusPane.setText("Interrupted - Classifier One Training Completed");
		    	return classifierOne;
		    }
		    //Running classifier one on dataset3
		    if(statusPane != null)
		    	statusPane.setText("Running ClassifierOne on Dataset 3..");
	    	int positiveDataset3FromInt = applicationData.getPositiveDataset3FromField();
	    	int positiveDataset3ToInt = applicationData.getPositiveDataset3ToField();
	    	int negativeDataset3FromInt = applicationData.getNegativeDataset3FromField();
	    	int negativeDataset3ToInt = applicationData.getNegativeDataset3ToField();
	    	
	    	//Generate the header for ClassifierOne.scores on Dataset3	   
	    	String classifierOneFilename = applicationData.getWorkingDirectory() + File.separator + 
	    		"ClassifierOne_" + randomNumberForClassifier + ".scores";
	    	BufferedWriter dataset3OutputFile = new BufferedWriter(new FileWriter(classifierOneFilename));		
			FastaFileManipulation fastaFile = new FastaFileManipulation(
				applicationData.getPositiveStep1TableModel(),applicationData.getNegativeStep1TableModel(),
				positiveDataset3FromInt,positiveDataset3ToInt,negativeDataset3FromInt,negativeDataset3ToInt,
				applicationData.getWorkingDirectory());
	 		 		     				     				
			//Reading and Storing the featureList
	 		ArrayList<Feature> featureDataArrayList = new ArrayList<Feature>();
	 		for(int x = 0; x < inst.numAttributes() - 1; x++){
				//-1 because class attribute must be ignored
	 			featureDataArrayList.add(Feature.levelOneClassifierPane(inst.attribute(x).name()));
	 		}     
			
			//Reading the fastaFile		
			int lineCounter = 0;
			String _class = "pos";
			int totalDataset3PositiveInstances = positiveDataset3ToInt - positiveDataset3FromInt + 1;
			FastaFormat fastaFormat;
	 		while((fastaFormat = fastaFile.nextSequence(_class))!=null){
	 			if(applicationData.terminateThread == true){
	 				if(statusPane != null)
	 					statusPane.setText("Interrupted - Classifier One Training Completed");
	 				dataset3OutputFile.close();
			    	return classifierOne;
			    }
	 			dataset3OutputFile.write(fastaFormat.getHeader());
	 			dataset3OutputFile.newLine();
	 			dataset3OutputFile.write(fastaFormat.getSequence());
	 			dataset3OutputFile.newLine();
	 			lineCounter++;//Putting it here will mean if lineCounter is x then line == sequence x	 			     				
	 			dataset3OutputFile.flush();
	 			if(statusPane != null)
	 				statusPane.setText("Running Classifier One on Dataset 3.. @ " + lineCounter + " / " + 
	 						applicationData.getTotalSequences(3) + 
	 						" Sequences");	 								
 				Instance tempInst;
 				tempInst = new Instance(inst.numAttributes());
 				tempInst.setDataset(inst);     				
 				for(int x = 0; x < inst.numAttributes() - 1; x++){
 					//-1 because class attribute can be ignored
 					//Give the sequence and the featureList to get the feature freqs on the sequence
 					Object obj = GenerateArff.getMatchCount(fastaFormat,featureDataArrayList.get(x),applicationData.getScoringMatrixIndex(),
	     					applicationData.getCountingStyleIndex(),applicationData.getScoringMatrix());
 					if(obj.getClass().getName().equalsIgnoreCase("java.lang.Integer"))			
 						tempInst.setValue(x,(Integer) obj);
 					else if(obj.getClass().getName().equalsIgnoreCase("java.lang.Double"))
 						tempInst.setValue(x,(Double) obj);
 					else if(obj.getClass().getName().equalsIgnoreCase("java.lang.String"))
 						tempInst.setValue(x, (String) obj);
 					else{
 						dataset3OutputFile.close();
 						throw new Error("Unknown: " + obj.getClass().getName());
 					}
	     		}     	
	     		tempInst.setValue(inst.numAttributes() - 1,_class);		     		
	     		double[] results = classifierOne.distributionForInstance(tempInst);		     		
	     		dataset3OutputFile.write(_class + ",0=" + results[0]);
	 			dataset3OutputFile.newLine();
	 			dataset3OutputFile.flush();
	 			if(lineCounter == totalDataset3PositiveInstances)
	 				_class = "neg";  			 			
	 		}     		     				    										
			dataset3OutputFile.close();	
			
			//Display Statistics by reading the ClassifierOne.scores
			PredictionStats classifierOneStatsOnBlindTest = new PredictionStats(classifierOneFilename,range,threshold);	
			//display(double range)
			totalTimeElapsed = System.currentTimeMillis() - totalTimeStart;
			if(classifierResults != null){
		     	classifierResults.updateList(classifierResults.getResultsList(),"Total Time Used: ", Utils.doubleToString(totalTimeElapsed / 60000,2)+ 
		     		" minutes " + Utils.doubleToString((totalTimeElapsed/1000.0) % 60.0,2) + " seconds");
				classifierOneStatsOnBlindTest.updateDisplay(classifierResults, classifierOneDisplayTextArea,true);
			}else
				classifierOneStatsOnBlindTest.updateDisplay(classifierResults, classifierOneDisplayTextArea,true);
			applicationData.setClassifierOneStats(classifierOneStatsOnBlindTest);
			if(myGraph != null)
				myGraph.setMyStats(classifierOneStatsOnBlindTest);
			if(statusPane != null)
				statusPane.setText("Done!");	     	
  			fastaFile.cleanUp();
  			if(returnClassifier)
  				return classifierOne;
  			else
  				return classifierOneStatsOnBlindTest;
		}catch(Exception ex){
			ex.printStackTrace();
			JOptionPane.showMessageDialog(parent,ex.getMessage(),"Evaluate classifier",
				JOptionPane.ERROR_MESSAGE);
			return null;
		}					
     }
    //For classifier one with cross-validation
    public static Classifier xValidateClassifierOneWithNoLocationIndex(JInternalFrame parent,
    	ApplicationData applicationData,JTextArea classifierOneDisplayTextArea,
    	String classifierName, String[] classifierOptions,int folds,GraphPane myGraph,
    	ClassifierResults classifierResults,int range, 
    	double threshold, boolean outputClassifier, GeneticAlgorithmDialog gaDialog, 
    	GASettingsInterface gaSettings, int randomNumberForClassifier){
		try{			
			StatusPane statusPane = applicationData.getStatusPane();
			if(statusPane == null)
				System.out.println("Null");
			//else
			//	stats
			
			long totalTimeStart = System.currentTimeMillis(),totalTimeElapsed;						
			Classifier tempClassifier = (Classifier) Classifier.forName(classifierName, classifierOptions);	
			
			Instances inst = null;
			if(applicationData.getDataset1Instances() != null){
				inst = new Instances(applicationData.getDataset1Instances());
				inst.setClassIndex(applicationData.getDataset1Instances().numAttributes() - 1);  			  				
			}
		    		     						  	  						
  			//Train classifier one with the full dataset first then do cross-validation to gauge its accuracy	
  			long trainTimeStart = 0, trainTimeElapsed = 0;  			
			Classifier classifierOne = (Classifier) Classifier.forName(classifierName, classifierOptions);
			if(statusPane != null)
				statusPane.setText("Training Classifier One... May take a while... Please wait...");
			//Record Start Time
			trainTimeStart = System.currentTimeMillis();
		    if(outputClassifier && gaSettings == null)
		    	classifierOne.buildClassifier(inst);	
		    //Record Total Time used to build classifier one
		    trainTimeElapsed = System.currentTimeMillis() - trainTimeStart;		
		    //Training Done		]
		    if(classifierResults != null){
		    	classifierResults.updateList(classifierResults.getClassifierList(), "Classifier: ", classifierName);
		    	classifierResults.updateList(classifierResults.getClassifierList(), "Training Data: ", folds + " fold cross-validation");
		    	classifierResults.updateList(classifierResults.getClassifierList(), "Time Used: ", Utils.doubleToString(trainTimeElapsed / 1000.0,2) + 
		    	" seconds");			  
		    }
		    int startRandomNumber;
		    if(gaSettings != null)
		    	startRandomNumber = gaSettings.getRandomNumber();
		    else
		    	startRandomNumber = 1;
		    String classifierOneFilename = applicationData.getWorkingDirectory() + File.separator + 
		    	"ClassifierOne_" + randomNumberForClassifier + "_" + startRandomNumber + ".scores";
			BufferedWriter outputCrossValidation = new BufferedWriter(new FileWriter(classifierOneFilename));			
			
			Instances foldTrainingInstance = null;
			Instances foldTestingInstance = null;
			int positiveDataset1FromInt = applicationData.getPositiveDataset1FromField(); 
	    	int positiveDataset1ToInt = applicationData.getPositiveDataset1ToField();
	    	int negativeDataset1FromInt = applicationData.getNegativeDataset1FromField();
	    	int negativeDataset1ToInt = applicationData.getNegativeDataset1ToField();
	    	Step1TableModel positiveStep1TableModel = applicationData.getPositiveStep1TableModel();
			Step1TableModel negativeStep1TableModel = applicationData.getNegativeStep1TableModel();		
			FastaFileManipulation fastaFile = new FastaFileManipulation(positiveStep1TableModel,
    				negativeStep1TableModel,positiveDataset1FromInt,positiveDataset1ToInt,
    				negativeDataset1FromInt,negativeDataset1ToInt,applicationData.getWorkingDirectory());
			FastaFormat fastaFormat;
			String header[] = null; 			
			String data[] = null; 
			if(inst != null){
				header = new String[inst.numInstances()];
				data = new String[inst.numInstances()];
			}
			List<FastaFormat> allPosList = new ArrayList<FastaFormat>();
			List<FastaFormat> allNegList = new ArrayList<FastaFormat>();
			int counter = 0;
			while((fastaFormat = fastaFile.nextSequence("pos"))!=null){
				if(inst != null){
					header[counter] = fastaFormat.getHeader();
					data[counter] = fastaFormat.getSequence();
					counter++;
				}				
				allPosList.add(fastaFormat);
			}	
			while((fastaFormat = fastaFile.nextSequence("neg"))!=null){
				if(inst != null){
					header[counter] = fastaFormat.getHeader();
					data[counter] = fastaFormat.getSequence();
					counter++;
				}				
				allNegList.add(fastaFormat);
			}			
			//run x folds
			for(int x = 0; x < folds; x++){
				if(applicationData.terminateThread == true){
					if(statusPane != null)
						statusPane.setText("Interrupted - Classifier One Training Completed");
					outputCrossValidation.close();
			    	return classifierOne;
			    }
				if(statusPane != null)
					statusPane.setPrefix("Running Fold " + (x+1) + ": ");
				if(inst != null){
					foldTrainingInstance = new Instances(inst, 0);
					foldTestingInstance = new Instances(inst, 0);
				}
				List<FastaFormat> trainPosList = new ArrayList<FastaFormat>();
				List<FastaFormat> trainNegList = new ArrayList<FastaFormat>();
				List<FastaFormat> testPosList = new ArrayList<FastaFormat>();
				List<FastaFormat> testNegList = new ArrayList<FastaFormat>();
				//split data into training and testing
				//This is for normal run
				int testInstanceIndex[] = null;
				if(inst != null)
					testInstanceIndex = new int[(inst.numInstances() / folds) + 1];
				if(gaSettings == null){					
					int testIndexCounter = 0;
					for(int y = 0; y < inst.numInstances(); y++){
						if((y%folds) == x){//this instance is for testing
							foldTestingInstance.add(inst.instance(y));
							testInstanceIndex[testIndexCounter] = y;
							testIndexCounter++;
						}else{//this instance is for training
							foldTrainingInstance.add(inst.instance(y));
						}
					}			
				}else{
					//This is for GA run
					for(int y = 0; y < allPosList.size(); y++){
						if((y%folds) == x){//this instance is for testing
							testPosList.add(allPosList.get(y));
						}else{//this instance is for training
							trainPosList.add(allPosList.get(y));
						}
					}
					for(int y = 0; y < allNegList.size(); y++){
						if((y%folds) == x){//this instance is for testing
							testNegList.add(allNegList.get(y));
						}else{//this instance is for training
							trainNegList.add(allNegList.get(y));
						}
					}
					if(gaDialog != null)
						foldTrainingInstance = runDAandLoadResult(applicationData, gaDialog, trainPosList, trainNegList, x+1, startRandomNumber);
					else
						foldTrainingInstance = runDAandLoadResult(applicationData, gaSettings, trainPosList, trainNegList, x+1, startRandomNumber);
					foldTrainingInstance.setClassIndex(foldTrainingInstance.numAttributes() - 1);
					//Reading and Storing the featureList
			 		ArrayList<Feature> featureList = new ArrayList<Feature>();
			 		for(int y = 0; y < foldTrainingInstance.numAttributes() - 1; y++){
						//-1 because class attribute must be ignored
			 			featureList.add(Feature.levelOneClassifierPane(foldTrainingInstance.attribute(y).name()));
			 		}     			 		
			 		String outputFilename;
			 		if(gaDialog != null)
			 			outputFilename = gaDialog.getOutputLocation().getText() + File.separator + "GeneticAlgorithmFeatureGenerationTest" + new Random().nextInt() + "_" + (x+1) + ".arff";
			 		else
			 			outputFilename = gaSettings.getOutputLocation() + File.separator + "GeneticAlgorithmFeatureGenerationTest" + new Random().nextInt() + "_" + (x+1) + ".arff";
			 		new GenerateFeatures(applicationData,featureList,testPosList,testNegList,outputFilename);
			 		foldTestingInstance = new Instances(new FileReader(outputFilename));
			 		foldTestingInstance.setClassIndex(foldTestingInstance.numAttributes() - 1);
				}
				
				
				Classifier foldClassifier = tempClassifier;
				foldClassifier.buildClassifier(foldTrainingInstance);
				for(int y = 0; y < foldTestingInstance.numInstances(); y++){
					if(applicationData.terminateThread == true){
						if(statusPane != null)
							statusPane.setText("Interrupted - Classifier One Training Completed");
						outputCrossValidation.close();
				    	return classifierOne;
				    }
					double[] results = foldClassifier.distributionForInstance(foldTestingInstance.instance(y));	
					int classIndex = foldTestingInstance.instance(y).classIndex();
					String classValue = foldTestingInstance.instance(y).toString(classIndex);
					if(inst != null){
						outputCrossValidation.write(header[testInstanceIndex[y]]);
						outputCrossValidation.newLine();
						outputCrossValidation.write(data[testInstanceIndex[y]]);
						outputCrossValidation.newLine();
					}else{
						if(y < testPosList.size()){
							outputCrossValidation.write(testPosList.get(y).getHeader());
							outputCrossValidation.newLine();
							outputCrossValidation.write(testPosList.get(y).getSequence());
							outputCrossValidation.newLine();
						}else{
							outputCrossValidation.write(testNegList.get(y - testPosList.size()).getHeader());
							outputCrossValidation.newLine();
							outputCrossValidation.write(testNegList.get(y - testPosList.size()).getSequence());
							outputCrossValidation.newLine();
						}
					}
					if(classValue.equals("pos"))					
						outputCrossValidation.write("pos,0=" + results[0]);						
					else if(classValue.equals("neg"))						
						outputCrossValidation.write("neg,0=" + results[0]);						
					else{
						outputCrossValidation.close();
						throw new Error("Invalid Class Type!");
					}
					outputCrossValidation.newLine();
					outputCrossValidation.flush();
				}
			}		
			outputCrossValidation.close();
			PredictionStats classifierOneStatsOnXValidation = new PredictionStats(classifierOneFilename,range,threshold);			
			totalTimeElapsed = System.currentTimeMillis() - totalTimeStart;		
			if(classifierResults != null){
				classifierResults.updateList(classifierResults.getResultsList(), "Total Time Used: ", 
						Utils.doubleToString(totalTimeElapsed / 60000,2) + " minutes " + Utils.doubleToString((totalTimeElapsed/1000.0) % 60.0,2) + 
					" seconds");			
				classifierOneStatsOnXValidation.updateDisplay(classifierResults, classifierOneDisplayTextArea,true);
			}
			applicationData.setClassifierOneStats(classifierOneStatsOnXValidation);
			if(myGraph != null)
				myGraph.setMyStats(classifierOneStatsOnXValidation);					   
		   	if(statusPane != null)													
		   		statusPane.setText("Done!");
  			//Note that this will be null if GA is run though maybe it is better if i run all sequence with GA and then build the classifier but this would be a waste of time
  			return classifierOne;
		}catch(Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(parent,e.getMessage(),"ERROR",JOptionPane.ERROR_MESSAGE);
			return null;
		}		
	}

    public static Classifier jackKnifeClassifierOneWithNoLocationIndex(JInternalFrame parent,
        	ApplicationData applicationData,JTextArea classifierOneDisplayTextArea,
        	GenericObjectEditor m_ClassifierEditor,double ratio,GraphPane myGraph,ClassifierResults classifierResults,int range, double threshold, 
        	boolean outputClassifier, int randomNumberForClassifier){
    	return (Classifier)RunClassifierWithNoLocationIndex.jackKnifeClassifierOneWithNoLocationIndex(parent,
    			applicationData,classifierOneDisplayTextArea,m_ClassifierEditor,ratio,myGraph,classifierResults,range,threshold, 
    			outputClassifier,"",null, true, randomNumberForClassifier);
    }
    
    public static Object jackKnifeClassifierOneWithNoLocationIndex(JInternalFrame parent,
        	ApplicationData applicationData,JTextArea classifierOneDisplayTextArea,
        	GenericObjectEditor m_ClassifierEditor,double ratio,GraphPane myGraph,ClassifierResults classifierResults,int range, double threshold, 
        	boolean outputClassifier, String classifierName, String[] classifierOptions, 
        	boolean returnClassifier, int randomNumberForClassifier){
    		try{			
    			StatusPane statusPane = applicationData.getStatusPane();			
    			
    			long totalTimeStart = System.currentTimeMillis(),totalTimeElapsed;						
    			Classifier tempClassifier;
    			if(m_ClassifierEditor != null)
    				tempClassifier = (Classifier) m_ClassifierEditor.getValue();
    			else
    				tempClassifier = Classifier.forName(classifierName, classifierOptions);    			
    			
    			//Assume that class attribute is the last attribute - This should be the case for all Sirius produced Arff files					
    			//split the instances into positive and negative
    			Instances posInst = new Instances(applicationData.getDataset1Instances());
    			posInst.setClassIndex(posInst.numAttributes() - 1);
    			for(int x = 0; x < posInst.numInstances();)    				
    				if(posInst.instance(x).stringValue(posInst.numAttributes() - 1).equalsIgnoreCase("pos"))
    					x++;
    				else
    					posInst.delete(x);
    			posInst.deleteAttributeType(Attribute.STRING);
    			Instances negInst = new Instances(applicationData.getDataset1Instances());
    			negInst.setClassIndex(negInst.numAttributes() - 1); 
    			for(int x = 0; x < negInst.numInstances();)    				
    				if(negInst.instance(x).stringValue(negInst.numAttributes() - 1).equalsIgnoreCase("neg"))
    					x++;
    				else
    					negInst.delete(x);	
    			negInst.deleteAttributeType(Attribute.STRING);
      			//Train classifier one with the full dataset first then do cross-validation to gauge its accuracy	
      			long trainTimeStart = 0, trainTimeElapsed = 0;  		
      			if(statusPane != null)
      				statusPane.setText("Training Classifier One... May take a while... Please wait...");
    			//Record Start Time
    			trainTimeStart = System.currentTimeMillis();
    			Instances fullInst = new Instances(applicationData.getDataset1Instances());
    			fullInst.setClassIndex(fullInst.numAttributes() - 1);
    			Classifier classifierOne;
    			if(m_ClassifierEditor != null) 
    				classifierOne = (Classifier) m_ClassifierEditor.getValue();
    			else
    				classifierOne = Classifier.forName(classifierName, classifierOptions);
    			if(outputClassifier)
    				classifierOne.buildClassifier(fullInst);	
    		    //Record Total Time used to build classifier one
    		    trainTimeElapsed = System.currentTimeMillis() - trainTimeStart;		
    		    //Training Done
    		    
    		    String tclassifierName;
    		    if(m_ClassifierEditor != null)
    		    	tclassifierName = m_ClassifierEditor.getValue().getClass().getName();
    		    else
    		    	tclassifierName = classifierName;
    		    if(classifierResults != null){
	    		    classifierResults.updateList(classifierResults.getClassifierList(), "Classifier: ", tclassifierName);
	    		    classifierResults.updateList(classifierResults.getClassifierList(), "Training Data: ", " Jack Knife Validation");
	    		    classifierResults.updateList(classifierResults.getClassifierList(), "Time Used: ", Utils.doubleToString(trainTimeElapsed / 1000.0,2) + 
	    				" seconds");
    		    }
      			String classifierOneFilename = applicationData.getWorkingDirectory() + File.separator + 
      				"ClassifierOne_" + randomNumberForClassifier + ".scores";
    			BufferedWriter outputCrossValidation = new BufferedWriter(new FileWriter(classifierOneFilename));		
    			
    			//Instances foldTrainingInstance;
    			//Instances foldTestingInstance;
    			int positiveDataset1FromInt = applicationData.getPositiveDataset1FromField(); 
    	    	int positiveDataset1ToInt = applicationData.getPositiveDataset1ToField();
    	    	int negativeDataset1FromInt = applicationData.getNegativeDataset1FromField();
    	    	int negativeDataset1ToInt = applicationData.getNegativeDataset1ToField();
    	    	Step1TableModel positiveStep1TableModel = applicationData.getPositiveStep1TableModel();
    			Step1TableModel negativeStep1TableModel = applicationData.getNegativeStep1TableModel();		
    			FastaFileManipulation fastaFile = new FastaFileManipulation(positiveStep1TableModel,
        				negativeStep1TableModel,positiveDataset1FromInt,positiveDataset1ToInt,
        				negativeDataset1FromInt,negativeDataset1ToInt,applicationData.getWorkingDirectory());
    			FastaFormat fastaFormat;
    			String header[] = new String[fullInst.numInstances()];
    			String data[] = new String[fullInst.numInstances()];
    			int counter = 0;
    			while((fastaFormat = fastaFile.nextSequence("pos"))!=null){
    				header[counter] = fastaFormat.getHeader();
    				data[counter] = fastaFormat.getSequence();
    				counter++;
    			}	
    			while((fastaFormat = fastaFile.nextSequence("neg"))!=null){
    				header[counter] = fastaFormat.getHeader();
    				data[counter] = fastaFormat.getSequence();
    				counter++;
    			}
    			    			
    			//run jack knife validation
    			for(int x = 0; x < fullInst.numInstances(); x++){
    				if(applicationData.terminateThread == true){
    					if(statusPane != null)
    						statusPane.setText("Interrupted - Classifier One Training Completed");
    					outputCrossValidation.close();
    			    	return classifierOne;
    			    }
    				if(statusPane != null)
    					statusPane.setText("Running " + (x+1) + " / "  + fullInst.numInstances()); 
    				Instances trainPosInst = new Instances(posInst);
    				Instances trainNegInst = new Instances(negInst);
    				Instance testInst;
    				//split data into training and testing
    				if(x < trainPosInst.numInstances()){
    					testInst = posInst.instance(x);
    					trainPosInst.delete(x);    					
    				}else{
    					testInst = negInst.instance(x - posInst.numInstances());
    					trainNegInst.delete(x - posInst.numInstances());
    				}    				 		    				
    				Instances trainInstances;
    				if(trainPosInst.numInstances() < trainNegInst.numInstances()){
    					trainInstances = new Instances(trainPosInst);
    					int max = (int)(ratio * trainPosInst.numInstances());
    					if(ratio == -1)
    						max = trainNegInst.numInstances();
    					Random rand = new Random(1);
    					for(int y = 0; y < trainNegInst.numInstances() && y < max; y++){
    						int index = rand.nextInt(trainNegInst.numInstances());
        					trainInstances.add(trainNegInst.instance(index));
        					trainNegInst.delete(index);
    					}
    				}else{
    					trainInstances = new Instances(trainNegInst);    	
    					int max = (int)(ratio * trainNegInst.numInstances());
    					if(ratio == -1)
    						max = trainPosInst.numInstances();
    					Random rand = new Random(1);
    					for(int y = 0; y < trainPosInst.numInstances() && y < max; y++){
    						int index = rand.nextInt(trainPosInst.numInstances());
        					trainInstances.add(trainPosInst.instance(index));
        					trainPosInst.delete(index);
    					}
    				}    				    				    		
    				Classifier foldClassifier = tempClassifier;
    				foldClassifier.buildClassifier(trainInstances);    				    				
    				double[] results = foldClassifier.distributionForInstance(testInst);	
					int classIndex = testInst.classIndex();
					String classValue = testInst.toString(classIndex);
					outputCrossValidation.write(header[x]);
					outputCrossValidation.newLine();
					outputCrossValidation.write(data[x]);
					outputCrossValidation.newLine();
					if(classValue.equals("pos"))					
						outputCrossValidation.write("pos,0=" + results[0]);						
					else if(classValue.equals("neg"))						
						outputCrossValidation.write("neg,0=" + results[0]);						
					else {
						outputCrossValidation.close();
						throw new Error("Invalid Class Type!");
					}
					outputCrossValidation.newLine();
					outputCrossValidation.flush();    				
    			}					
    			outputCrossValidation.close();
    			PredictionStats classifierOneStatsOnJackKnife = new PredictionStats(classifierOneFilename,
    					range,threshold);			
    			totalTimeElapsed = System.currentTimeMillis() - totalTimeStart;		
    			if(classifierResults != null)
    				classifierResults.updateList(classifierResults.getResultsList(), "Total Time Used: ", 
    				Utils.doubleToString(totalTimeElapsed / 60000,2) + " minutes " + Utils.doubleToString((totalTimeElapsed/1000.0) % 60.0,2) + 
    				" seconds");
    			
    			//if(classifierOneDisplayTextArea != null)
    			classifierOneStatsOnJackKnife.updateDisplay(classifierResults, classifierOneDisplayTextArea,true);
    			applicationData.setClassifierOneStats(classifierOneStatsOnJackKnife);
    			if(myGraph != null)
    				myGraph.setMyStats(classifierOneStatsOnJackKnife);					   
    		   					
    			if(statusPane != null)
    				statusPane.setText("Done!");
    			if(returnClassifier)
    				return classifierOne;
    			else
    				return classifierOneStatsOnJackKnife;
    		}catch(Exception e){
    			e.printStackTrace();
    			JOptionPane.showMessageDialog(parent,e.getMessage(),"ERROR",JOptionPane.ERROR_MESSAGE);
    			return null;
    		}		
    	}
}