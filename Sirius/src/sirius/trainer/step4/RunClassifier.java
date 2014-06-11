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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import sirius.main.ApplicationData;
import sirius.main.FastaFileManipulation;
import sirius.trainer.features.Feature;
import sirius.trainer.features.GenerateArff;
import sirius.trainer.main.SequenceManipulation;
import sirius.trainer.main.StatusPane;
import sirius.trainer.step1.Step1TableModel;
import sirius.utils.ClassifierResults;
import sirius.utils.FastaFormat;
import sirius.utils.PredictionStats;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.gui.GenericObjectEditor;

public class RunClassifier {		
    public RunClassifier() {
    }
    
    //For classifierOne on blind test set
    public static Classifier startClassifierOne(JInternalFrame parent,ApplicationData applicationData,
     	JTextArea classifierOneDisplayTextArea,GenericObjectEditor m_ClassifierEditor,GraphPane myGraph,
     	boolean test,ClassifierResults classifierResults,int range, double threshold){
		try{																
			StatusPane statusPane = applicationData.getStatusPane();
			
			long totalTimeStart = System.currentTimeMillis(),totalTimeElapsed;						
			//Setting up training dataset 1 for classifier one
  			statusPane.setText("Setting up...");
  			//Load Dataset1 Instances
  			Instances inst = new Instances(applicationData.getDataset1Instances());
  			inst.setClassIndex(applicationData.getDataset1Instances().numAttributes() - 1);
  			applicationData.getDataset1Instances().setClassIndex(
  				applicationData.getDataset1Instances().numAttributes() - 1);
			// for timing
  			long trainTimeStart = 0, trainTimeElapsed = 0;
			Classifier classifierOne = (Classifier) m_ClassifierEditor.getValue();
			statusPane.setText("Training Classifier One... May take a while... Please wait...");
			trainTimeStart = System.currentTimeMillis();
			inst.deleteAttributeType(Attribute.STRING);
		    classifierOne.buildClassifier(inst);		    
		    trainTimeElapsed = System.currentTimeMillis() - trainTimeStart;				   
			
			String classifierName = m_ClassifierEditor.getValue().getClass().getName();		
			classifierResults.updateList(classifierResults.getClassifierList(), "Classifier: ", classifierName);
			classifierResults.updateList(classifierResults.getClassifierList(), "Training Data: ", applicationData.getWorkingDirectory() + 
  					File.separator + "Dataset1.arff");
			classifierResults.updateList(classifierResults.getClassifierList(), "Time Used: ", Utils.doubleToString(trainTimeElapsed / 1000.0,2) 
					+ " seconds");
  				
  			if(test == false){
  				statusPane.setText("Classifier One Training Completed...Done...");
  				return classifierOne;
  			}  				
  			if(applicationData.terminateThread == true){
		    	statusPane.setText("Interrupted - Classifier One Training Completed");
		    	return classifierOne;
		    }  					    		    		 
		    //Running classifier one on dataset3
  			if(statusPane != null)
  				statusPane.setText("Running ClassifierOne on Dataset 3..");
		    //Step1TableModel positiveStep1TableModel = applicationData.getPositiveStep1TableModel();
	    	//Step1TableModel negativeStep1TableModel = applicationData.getNegativeStep1TableModel();	
	    	int positiveDataset3FromInt = applicationData.getPositiveDataset3FromField(); 
	    	int positiveDataset3ToInt = applicationData.getPositiveDataset3ToField();
	    	int negativeDataset3FromInt = applicationData.getNegativeDataset3FromField();
	    	int negativeDataset3ToInt = applicationData.getNegativeDataset3ToField();
	    	
	    	//Generate the header for ClassifierOne.scores on Dataset3	    			
	    	BufferedWriter dataset3OutputFile = new BufferedWriter(new FileWriter(
				applicationData.getWorkingDirectory() + File.separator + "ClassifierOne.scores"));		    	
			if (m_ClassifierEditor.getValue() instanceof OptionHandler)
				classifierName += " " + 
					Utils.joinOptions(((OptionHandler) m_ClassifierEditor.getValue()).getOptions());			
			
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
			    	statusPane.setText("Interrupted - Classifier One Training Completed");
			    	dataset3OutputFile.close();
			    	return classifierOne;
			    }
	 			lineCounter++;//Putting it here will mean if lineCounter is x then line == sequence x
	 			dataset3OutputFile.write(fastaFormat.getHeader());
	 			dataset3OutputFile.newLine();
	 			dataset3OutputFile.write(fastaFormat.getSequence());
	 			dataset3OutputFile.newLine();
	 			//if((lineCounter % 100) == 0){     					 				
	 				statusPane.setText("Running Classifier One on Dataset 3.. @ " + lineCounter + " / " 
	 					+ applicationData.getTotalSequences(3) + " Sequences"); 				
	 			//}
	 			
	 			// for +1 index being -1, only make one prediction for the whole sequence	 			
	 			if(fastaFormat.getIndexLocation() == -1){	 				
	 				//Should not have reached here...
	 				dataset3OutputFile.close();
	 				throw new Exception("SHOULD NOT HAVE REACHED HERE!!");
	 			}else{// for +1 index being non -1, make prediction on every possible position
	 				//For each sequence, you want to shift from predictPositionFrom till predictPositionTo
		 			//ie changing the +1 location
		 			//to get the scores given by classifier one so that 
		 			//you can use it to train classifier two later
		 			//Doing shift from predictPositionFrom till predictPositionTo		 			
		 			int predictPosition[];
		 			predictPosition = fastaFormat.getPredictPositionForClassifierOne(
		 				applicationData.getLeftMostPosition(),applicationData.getRightMostPosition());
		 			
					SequenceManipulation seq = new SequenceManipulation(fastaFormat.getSequence(),
						predictPosition[0],predictPosition[1]);					
					String line2;
					int currentPosition = predictPosition[0];
					dataset3OutputFile.write(_class);					
		 			while((line2 = seq.nextShift())!=null){	 				
		 				Instance tempInst;
		 				tempInst = new Instance(inst.numAttributes());
		 				tempInst.setDataset(inst);     				
		 				for(int x = 0; x < inst.numAttributes() - 1; x++){
		 					//-1 because class attribute can be ignored
		 					//Give the sequence and the featureList to get the feature freqs on the sequence
		 					Object obj = GenerateArff.getMatchCount(fastaFormat.getHeader(),line2,
			     					featureDataArrayList.get(x),applicationData.getScoringMatrixIndex(),applicationData.getCountingStyleIndex(),
			     					applicationData.getScoringMatrix());
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
			     		dataset3OutputFile.write("," + currentPosition + "=" + results[0]);
			     		//AHFU_DEBUG 
			     		/*if(currentPosition >= setClassifierTwoUpstreamInt && currentPosition <= setClassifierTwoDownstreamInt)
			     			testClassifierTwoArff.write(results[0] + ",");*/
			     		//AHFU_DEBUG_END
			     		currentPosition++;
			     		if(currentPosition == 0)
			     			currentPosition++;
		 			}// end of while((line2 = seq.nextShift())!=null) 
		 			//AHFU_DEBUG
		 			/*testClassifierTwoArff.write(_class);
		 			testClassifierTwoArff.newLine();
		 			testClassifierTwoArff.flush();*/
		 			//AHFU_DEBUG_END
		 			dataset3OutputFile.newLine();
		 			dataset3OutputFile.flush();
		 			if(lineCounter == totalDataset3PositiveInstances)
		 				_class = "neg";
	 			}//end of inside non -1	 			  			 			
	 		}// end of while((fastaFormat = fastaFile.nextSequence(_class))!=null) 		
			dataset3OutputFile.close();		
			PredictionStats classifierOneStatsOnBlindTest = new PredictionStats(
				applicationData.getWorkingDirectory() + File.separator + "ClassifierOne.scores",
				range,threshold);
			totalTimeElapsed = System.currentTimeMillis() - totalTimeStart;
	     	classifierResults.updateList(classifierResults.getResultsList(), "Total Time Used: ", 
	     		Utils.doubleToString(totalTimeElapsed / 60000,2) + " minutes " + Utils.doubleToString((totalTimeElapsed/1000.0) % 60.0,2) + 
  				" seconds");
			classifierOneStatsOnBlindTest.updateDisplay(classifierResults,classifierOneDisplayTextArea,true);
			applicationData.setClassifierOneStats(classifierOneStatsOnBlindTest);
			myGraph.setMyStats(classifierOneStatsOnBlindTest);
	     	statusPane.setText("Done!");	     	
  			fastaFile.cleanUp();  			  		
  			return classifierOne;	      			  			
		}catch(Exception ex){
			ex.printStackTrace();
			JOptionPane.showMessageDialog(parent,ex.getMessage() + "Classifier One on Blind Test Set","Evaluate classifier",
				JOptionPane.ERROR_MESSAGE);
			return null;
		}					
     }
     //For classifierTwo on blind test set
     public static Classifier startClassifierTwo(JInternalFrame parent,ApplicationData applicationData,
     	JTextArea classifierTwoDisplayTextArea,GenericObjectEditor m_ClassifierEditor2,
     	Classifier classifierOne,GraphPane myGraph,boolean test, ClassifierResults classifierResults,int range, double threshold){
    	 int arraySize = 0;
    	 int lineCount = 0;
     	try{     		     		
			StatusPane statusPane = applicationData.getStatusPane();     		
			//Initialising		
			long totalTimeStart = System.currentTimeMillis();
			Step1TableModel positiveStep1TableModel = applicationData.getPositiveStep1TableModel();
	    	Step1TableModel negativeStep1TableModel = applicationData.getNegativeStep1TableModel();
	    	int positiveDataset3FromInt = applicationData.getPositiveDataset3FromField(); 
	    	int positiveDataset3ToInt = applicationData.getPositiveDataset3ToField();
	    	int negativeDataset3FromInt = applicationData.getNegativeDataset3FromField();
	    	int negativeDataset3ToInt = applicationData.getNegativeDataset3ToField();
	    		
	    	//Preparing Dataset2.arff to train Classifier Two
		    statusPane.setText("Preparing Dataset2.arff...");
		    //This step generates Dataset2.arff
		    if(DatasetGenerator.generateDataset2(parent,applicationData,
		    	applicationData.getSetUpstream(),applicationData.getSetDownstream(),classifierOne) == false){
		    	//Interrupted or Error occurred
		    	return null;
		    }
	    			
			//Training Classifier Two
			statusPane.setText("Training Classifier Two... May take a while... Please wait...");
			Instances inst2 = new Instances(new BufferedReader(new FileReader(
				applicationData.getWorkingDirectory() + File.separator + "Dataset2.arff")));
			inst2.setClassIndex(inst2.numAttributes() - 1);							
			long trainTimeStart = 0;
			long trainTimeElapsed = 0;
			
			Classifier classifierTwo = (Classifier) m_ClassifierEditor2.getValue();
			trainTimeStart = System.currentTimeMillis();
			applicationData.setDataset2Instances(inst2);
		    classifierTwo.buildClassifier(inst2);
		    trainTimeElapsed = System.currentTimeMillis() - trainTimeStart;				    		    
		    
		    //Running Classifier Two	
		    String classifierName = m_ClassifierEditor2.getValue().getClass().getName();		   
		    classifierResults.updateList(classifierResults.getClassifierList(), "Classifier: ", classifierName);
		    classifierResults.updateList(classifierResults.getClassifierList(), "Training Data: ", applicationData.getWorkingDirectory() + 
  					File.separator + "Dataset2.arff");
		    classifierResults.updateList(classifierResults.getClassifierList(),"Time Used: ", Utils.doubleToString(trainTimeElapsed / 1000.0,2) + 
  				" seconds");
  				
			if(test == false){
		    	statusPane.setText("Classifier Two Trained...Done...");
		    	return classifierTwo;
		    }
			if(applicationData.terminateThread == true){
		    	statusPane.setText("Interrupted - Classifier One Training Completed");
		    	return classifierTwo;
		    }
		    statusPane.setText("Running Classifier Two on Dataset 3...");
		    
			//Generate the header for ClassifierTwo.scores on Dataset3				
			BufferedWriter classifierTwoOutput = new BufferedWriter(new FileWriter(
				applicationData.getWorkingDirectory() + File.separator + "ClassifierTwo.scores"));				
			if (m_ClassifierEditor2.getValue() instanceof OptionHandler)
				classifierName += " " + 
				Utils.joinOptions(((OptionHandler) m_ClassifierEditor2.getValue()).getOptions());			
			
			//Generating an Instance given a sequence with the current attributes
			int setClassifierTwoUpstreamInt = applicationData.getSetUpstream();
			int setClassifierTwoDownstreamInt = applicationData.getSetDownstream();			
			int classifierTwoWindowSize;
			if(setClassifierTwoUpstreamInt < 0 && setClassifierTwoDownstreamInt > 0)
				classifierTwoWindowSize = (setClassifierTwoUpstreamInt * -1) + setClassifierTwoDownstreamInt;
			else if(setClassifierTwoUpstreamInt < 0 && setClassifierTwoDownstreamInt < 0)
				classifierTwoWindowSize = 
					(setClassifierTwoUpstreamInt - setClassifierTwoDownstreamInt - 1) * -1;
			else//both +ve
				classifierTwoWindowSize = 
					(setClassifierTwoDownstreamInt - setClassifierTwoUpstreamInt + 1);							
			
 			Instances inst = applicationData.getDataset1Instances(); 			 			
 			
 			//NOTE: need to take care of this function; 	
 			FastaFileManipulation fastaFile = new FastaFileManipulation(positiveStep1TableModel,
 				negativeStep1TableModel,positiveDataset3FromInt,positiveDataset3ToInt,negativeDataset3FromInt,
 				negativeDataset3ToInt,applicationData.getWorkingDirectory());
     			
     		//loading in all the features..
     		ArrayList<Feature> featureDataArrayList = new ArrayList<Feature>();
     		for(int x = 0; x < inst.numAttributes() - 1; x++){
				//-1 because class attribute must be ignored
     			featureDataArrayList.add(Feature.levelOneClassifierPane(inst.attribute(x).name()));
     		}
     		
     		//Reading the fastaFile     		 			     		
     		String _class = "pos";
     		lineCount = 0;     	 				
     		int totalPosSequences = positiveDataset3ToInt - positiveDataset3FromInt + 1;
     		FastaFormat fastaFormat;
	 		while((fastaFormat = fastaFile.nextSequence(_class))!=null){
	 			if(applicationData.terminateThread == true){
			    	statusPane.setText("Interrupted - Classifier Two Trained");
			    	classifierTwoOutput.close();
			    	return classifierTwo;
			    }
     			lineCount++;
     			classifierTwoOutput.write(fastaFormat.getHeader());
     			classifierTwoOutput.newLine();
     			classifierTwoOutput.write(fastaFormat.getSequence());
     			classifierTwoOutput.newLine();
     			//if((lineCount % 100) == 0){          				
					statusPane.setText("Running ClassifierTwo on Dataset 3...@ " + lineCount + " / " 
					+ applicationData.getTotalSequences(3) + " Sequences");
     			//}
     			arraySize = fastaFormat.getArraySize(applicationData.getLeftMostPosition(),
					applicationData.getRightMostPosition());
     			//This area always generate -ve arraySize~! WHY?? Exception always occur here     			
				double scores[] = new double[arraySize];
				int predictPosition[] = fastaFormat.getPredictPositionForClassifierOne(
					applicationData.getLeftMostPosition(),applicationData.getRightMostPosition());
     			//Doing shift from upstream till downstream	
     			SequenceManipulation seq = new SequenceManipulation(fastaFormat.getSequence(),
     				predictPosition[0],predictPosition[1]);
     			int scoreCount = 0;
     			String line2; 		
     			while((line2 = seq.nextShift())!=null){
     				Instance tempInst = new Instance(inst.numAttributes());
     				tempInst.setDataset(inst);     				
     				//-1 because class attribute can be ignored
     				for(int x = 0; x < inst.numAttributes() - 1; x++){     	
     					Object obj = GenerateArff.getMatchCount(fastaFormat.getHeader(),line2,
		     					featureDataArrayList.get(x),applicationData.getScoringMatrixIndex(),applicationData.getCountingStyleIndex(),
		     					applicationData.getScoringMatrix());
	 					if(obj.getClass().getName().equalsIgnoreCase("java.lang.Integer"))
	 						tempInst.setValue(x,(Integer) obj);
	 					else if(obj.getClass().getName().equalsIgnoreCase("java.lang.Double"))				
	 						tempInst.setValue(x,(Double) obj);
	 					else if(obj.getClass().getName().equalsIgnoreCase("java.lang.String"))
	 						tempInst.setValue(x, (String) obj);
	 					else{
	 						classifierTwoOutput.close();
	 						throw new Error("Unknown: " + obj.getClass().getName());
	 					}
		     		}     	
		     		tempInst.setValue(inst.numAttributes() - 1,_class);
		     		//Run classifierOne		     		
		     		double[] results = classifierOne.distributionForInstance(tempInst);
		     		scores[scoreCount++] = results[0];		     		
     			}     		
     			//Run classifierTwo     				
 				int currentPosition = fastaFormat.getPredictionFromForClassifierTwo(
 					applicationData.getLeftMostPosition(),applicationData.getRightMostPosition(),
 					applicationData.getSetUpstream());  
 				classifierTwoOutput.write(_class);
 				for(int y = 0; y < arraySize - classifierTwoWindowSize + 1; y++){
 					//+1 is for the class index
     				Instance tempInst2 = new Instance(classifierTwoWindowSize + 1);
     				tempInst2.setDataset(inst2);     				
 					for(int x = 0; x < classifierTwoWindowSize; x++){
     					tempInst2.setValue(x,scores[x+y]);
     				}     					     				
					tempInst2.setValue(tempInst2.numAttributes() - 1,_class);
     				double[] results = classifierTwo.distributionForInstance(tempInst2);
     				classifierTwoOutput.write("," + currentPosition + "=" + results[0]);     				
		     		currentPosition++;
		     		if(currentPosition == 0)
		     			currentPosition++;
 				}     				 				
 				classifierTwoOutput.newLine();
 				classifierTwoOutput.flush();
 				if(lineCount == totalPosSequences)
 					_class = "neg";
     		}			 		
			classifierTwoOutput.close();
			statusPane.setText("Done!");						
			PredictionStats classifierTwoStatsOnBlindTest = new PredictionStats(
 				applicationData.getWorkingDirectory() + File.separator + "ClassifierTwo.scores",
 				range,threshold);
			//display(double range)
			long totalTimeElapsed = System.currentTimeMillis() - totalTimeStart;
	     	classifierResults.updateList(classifierResults.getResultsList(), "Total Time Used: ",
	     		Utils.doubleToString(totalTimeElapsed / 60000,2) + " minutes " + Utils.doubleToString((totalTimeElapsed/1000.0) % 60.0,2) + 
  				" seconds");
			classifierTwoStatsOnBlindTest.updateDisplay(classifierResults,classifierTwoDisplayTextArea,true);
			applicationData.setClassifierTwoStats(classifierTwoStatsOnBlindTest);
			myGraph.setMyStats(classifierTwoStatsOnBlindTest);	     	
  			fastaFile.cleanUp();  			  		
  			return classifierTwo;	    
     	}
   		catch(Exception ex){
   			ex.printStackTrace();
			JOptionPane.showMessageDialog(parent,ex.getMessage() + "Classifier Two On Blind Test Set - Check Console Output",
					"Evaluate classifier two",JOptionPane.ERROR_MESSAGE);
			System.err.println("applicationData.getLeftMostPosition(): " + applicationData.getLeftMostPosition());
			System.err.println("applicationData.getRightMostPosition(): " + applicationData.getRightMostPosition());
			System.err.println("arraySize: " + arraySize);
			System.err.println("lineCount: " + lineCount);
			return null;
		}		
     }
     //For classifierOne on cross-validation
     public static Classifier xValidateClassifierOne(JInternalFrame parent,ApplicationData applicationData,
     	JTextArea classifierOneDisplayTextArea,GenericObjectEditor m_ClassifierEditor,int folds,
     	GraphPane myGraph,ClassifierResults classifierResults,int range, double threshold, boolean outputClassifier){
		try{			
			StatusPane statusPane = applicationData.getStatusPane();			
			
			long totalTimeStart = System.currentTimeMillis(),totalTimeElapsed;						
			//Classifier tempClassifier = (Classifier) m_ClassifierEditor.getValue();
			int positiveDataset1FromInt = applicationData.getPositiveDataset1FromField(); 
	    	int positiveDataset1ToInt = applicationData.getPositiveDataset1ToField();
	    	int negativeDataset1FromInt = applicationData.getNegativeDataset1FromField();
	    	int negativeDataset1ToInt = applicationData.getNegativeDataset1ToField();
	    		
	    	Step1TableModel positiveStep1TableModel = applicationData.getPositiveStep1TableModel();
			Step1TableModel negativeStep1TableModel = applicationData.getNegativeStep1TableModel();					
			
			Instances inst = new Instances(applicationData.getDataset1Instances());
  			inst.setClassIndex(applicationData.getDataset1Instances().numAttributes() - 1);
  			
  			//Train classifier one with the full dataset first then do cross-validation to gauge its accuracy		     			
  			long trainTimeStart = 0, trainTimeElapsed = 0;
			Classifier classifierOne = (Classifier) m_ClassifierEditor.getValue();		
			statusPane.setText("Training Classifier One... May take a while... Please wait...");
			//Record Start Time
			trainTimeStart = System.currentTimeMillis();
			inst.deleteAttributeType(Attribute.STRING);
			if(outputClassifier)
				classifierOne.buildClassifier(inst);	
		    //Record Total Time used to build classifier one
		    trainTimeElapsed = System.currentTimeMillis() - trainTimeStart;		
		    //Training Done		      				
		    
		    String classifierName = m_ClassifierEditor.getValue().getClass().getName();		    
		    classifierResults.updateList(classifierResults.getClassifierList(), "Classifier: ", classifierName);
		    classifierResults.updateList(classifierResults.getClassifierList(), "Training Data: ",
		    	folds + " fold cross-validation on Dataset1.arff");
		    classifierResults.updateList(classifierResults.getClassifierList(), "Time Used: ",
		    	Utils.doubleToString(trainTimeElapsed / 1000.0,2) + " seconds");	
			
			//Reading and Storing the featureList
		 	ArrayList<Feature> featureDataArrayList = new ArrayList<Feature>();
			for(int y = 0; y < inst.numAttributes() - 1; y++){
				featureDataArrayList.add(Feature.levelOneClassifierPane(inst.attribute(y).name()));
			}					
			
			BufferedWriter outputCrossValidation = new BufferedWriter(new FileWriter(
				applicationData.getWorkingDirectory() + File.separator + "ClassifierOne.scores"));			
			
			for(int x = 0; x < folds; x++){
				File trainFile = new File(
					applicationData.getWorkingDirectory() + File.separator + "trainingDataset1_" + (x+1) + ".arff");
				File testFile = new File(
					applicationData.getWorkingDirectory() + File.separator + "testingDataset1_" + (x+1) + ".fasta");
				//AHFU_DEBUG
				//Generate also the training file in fasta format for debugging purpose
				File trainFileFasta = new File(applicationData.getWorkingDirectory() + File.separator + "trainingDataset1_" + (x+1) + ".fasta");
				//AHFU_DEBUG_END
								
				//AHFU_DEBUG - This part is to generate the TestClassifierTwo.arff for use in WEKA to test classifierTwo
	  			//TestClassifierTwo.arff - predictions scores from Set Upstream Field to Set Downstream Field
	  			//Now first generate the header for TestClassifierTwo.arff
	  			BufferedWriter testClassifierTwoArff = new BufferedWriter(new FileWriter(applicationData.getWorkingDirectory() + File.separator +
	  					"TestClassifierTwo_" + (x+1) + ".arff"));
	  			int setClassifierTwoUpstreamInt = -40;
				int setClassifierTwoDownstreamInt = 41;			
				testClassifierTwoArff.write("@relation \'Used to Test Classifier Two\'");
				testClassifierTwoArff.newLine();
				for(int d = setClassifierTwoUpstreamInt; d <= setClassifierTwoDownstreamInt; d++){
					if(d == 0)
						continue;
					testClassifierTwoArff.write("@attribute (" + d + ") numeric");
					testClassifierTwoArff.newLine();
				}
				if(positiveDataset1FromInt > 0 && negativeDataset1FromInt > 0)
					testClassifierTwoArff.write("@attribute Class {pos,neg}");
				else if(positiveDataset1FromInt > 0 && negativeDataset1FromInt == 0)
					testClassifierTwoArff.write("@attribute Class {pos}");
				else if(positiveDataset1FromInt == 0 && negativeDataset1FromInt > 0)
					testClassifierTwoArff.write("@attribute Class {neg}");				
				testClassifierTwoArff.newLine();
				testClassifierTwoArff.newLine();
				testClassifierTwoArff.write("@data");
				testClassifierTwoArff.newLine();
				testClassifierTwoArff.newLine();
	  			//END of AHFU_DEBUG
				statusPane.setText("Building Fold " + (x+1) + "...");
				FastaFileManipulation fastaFile = new FastaFileManipulation(positiveStep1TableModel,
				negativeStep1TableModel,positiveDataset1FromInt,positiveDataset1ToInt,
				negativeDataset1FromInt,negativeDataset1ToInt,applicationData.getWorkingDirectory());
				
				//1) generate trainingDatasetX.arff headings
				BufferedWriter trainingOutputFile = new BufferedWriter(new FileWriter(
					applicationData.getWorkingDirectory() + File.separator + "trainingDataset1_" + (x+1) + ".arff"));	
				trainingOutputFile.write("@relation 'A temp file for X-validation purpose' ");
				trainingOutputFile.newLine();
				trainingOutputFile.newLine();
				trainingOutputFile.flush();							
  					 		 
				for(int y = 0; y < inst.numAttributes() - 1; y++){	
					if(inst.attribute(y).type() == Attribute.NUMERIC)
						trainingOutputFile.write("@attribute " + inst.attribute(y).name() + " numeric");
					else if(inst.attribute(y).type() == Attribute.STRING)
						trainingOutputFile.write("@attribute " + inst.attribute(y).name() + " String");
					else{
						testClassifierTwoArff.close();
						outputCrossValidation.close();
						trainingOutputFile.close();
						throw new Error("Unknown type: " + inst.attribute(y).name());
					}
					trainingOutputFile.newLine();
					trainingOutputFile.flush();
				}
				if(positiveDataset1FromInt > 0 && negativeDataset1FromInt > 0)
					trainingOutputFile.write("@attribute Class {pos,neg}");
				else if(positiveDataset1FromInt > 0 && negativeDataset1FromInt == 0)
					trainingOutputFile.write("@attribute Class {pos}");
				else if(positiveDataset1FromInt == 0 && negativeDataset1FromInt > 0)
					trainingOutputFile.write("@attribute Class {neg}");	
				trainingOutputFile.newLine();
				trainingOutputFile.newLine();
				trainingOutputFile.write("@data");
				trainingOutputFile.newLine();
				trainingOutputFile.newLine();
				trainingOutputFile.flush();
				
				//2) generate testingDataset1.fasta
				BufferedWriter testingOutputFile = new BufferedWriter(new FileWriter(
					applicationData.getWorkingDirectory() + File.separator + "testingDataset1_" + (x+1) + ".fasta"));
				
				//AHFU_DEBUG
				//Open the IOStream for training file (fasta format)
				BufferedWriter trainingOutputFileFasta = new BufferedWriter(new FileWriter(
						applicationData.getWorkingDirectory() + File.separator + "trainingDataset1_" + (x+1) + ".fasta"));
				//AHFU_DEBUG_END
				
				//Now, populating data for both the training and testing files				
				int fastaFileLineCounter = 0;
				int posTestSequenceCounter = 0;
				int totalTestSequenceCounter = 0;
				//For pos sequences	
				FastaFormat fastaFormat;
		 		while((fastaFormat = fastaFile.nextSequence("pos"))!=null){
		 			if((fastaFileLineCounter%folds) == x){//This sequence for testing
		 				testingOutputFile.write(fastaFormat.getHeader());
		 				testingOutputFile.newLine();
		 				testingOutputFile.write(fastaFormat.getSequence());
						testingOutputFile.newLine();
						testingOutputFile.flush();
						posTestSequenceCounter++;
						totalTestSequenceCounter++;
		 			}
		 			else{//for training
		 				for(int z = 0; z < inst.numAttributes() - 1; z++){
							trainingOutputFile.write(GenerateArff.getMatchCount(fastaFormat,
								featureDataArrayList.get(z),applicationData.getScoringMatrixIndex(),applicationData.getCountingStyleIndex(),
								applicationData.getScoringMatrix()) + ",");
						}									
						trainingOutputFile.write("pos");
						trainingOutputFile.newLine();
						trainingOutputFile.flush();
						
						//AHFU_DEBUG
						//Write the datas into the training file in fasta format
						trainingOutputFileFasta.write(fastaFormat.getHeader());
						trainingOutputFileFasta.newLine();
						trainingOutputFileFasta.write(fastaFormat.getSequence());
						trainingOutputFileFasta.newLine();
						trainingOutputFileFasta.flush();
						//AHFU_DEBUG_END
		 			}
		 			fastaFileLineCounter++;
		 		}
		 		//For neg sequences
		 		fastaFileLineCounter = 0;
		 		while((fastaFormat = fastaFile.nextSequence("neg"))!=null){
		 			if((fastaFileLineCounter%folds) == x){//This sequence for testing
		 				testingOutputFile.write(fastaFormat.getHeader());
		 				testingOutputFile.newLine();
		 				testingOutputFile.write(fastaFormat.getSequence());
						testingOutputFile.newLine();
						testingOutputFile.flush();
						totalTestSequenceCounter++;
		 			}
		 			else{//for training
		 				for(int z = 0; z < inst.numAttributes() - 1; z++){
							trainingOutputFile.write(GenerateArff.getMatchCount(fastaFormat,
								featureDataArrayList.get(z),applicationData.getScoringMatrixIndex(),applicationData.getCountingStyleIndex(),
								applicationData.getScoringMatrix()) + ",");
						}									
						trainingOutputFile.write("neg");
						trainingOutputFile.newLine();
						trainingOutputFile.flush();
						
						//AHFU_DEBUG
						//Write the datas into the training file in fasta format
						trainingOutputFileFasta.write(fastaFormat.getHeader());
						trainingOutputFileFasta.newLine();
						trainingOutputFileFasta.write(fastaFormat.getSequence());
						trainingOutputFileFasta.newLine();
						trainingOutputFileFasta.flush();
						//AHFU_DEBUG_END
		 			}
		 			fastaFileLineCounter++;
		 		}						
		 		trainingOutputFileFasta.close();		
		 		trainingOutputFile.close();	
				testingOutputFile.close();
				//3) train and test the classifier then store the statistics  				
				Classifier foldClassifier = (Classifier) m_ClassifierEditor.getValue();						
				Instances instFoldTrain = new Instances(new BufferedReader(new FileReader(
					applicationData.getWorkingDirectory() + File.separator + "trainingDataset1_" + (x+1) + ".arff")));
  				instFoldTrain.setClassIndex(instFoldTrain.numAttributes() - 1);				
			    foldClassifier.buildClassifier(instFoldTrain);			   			    
  				
	  			//Reading the test file
	  			statusPane.setText("Evaluating fold " + (x+1) + "..");
	  			BufferedReader testingInput = new BufferedReader(new FileReader(
	  				applicationData.getWorkingDirectory() + File.separator + "testingDataset1_" + (x+1) + ".fasta"));
	  			int lineCounter = 0;
	  			String lineHeader;
	  			String lineSequence;
	     		while((lineHeader = testingInput.readLine())!=null){
	     			if(applicationData.terminateThread == true){
				    	statusPane.setText("Interrupted - Classifier One Training Completed");
				    	testingInput.close();
				    	testClassifierTwoArff.close();
				    	return classifierOne;
				    }
					lineSequence = testingInput.readLine();
					outputCrossValidation.write(lineHeader);
					outputCrossValidation.newLine();
					outputCrossValidation.write(lineSequence);
					outputCrossValidation.newLine();
	     			lineCounter++;
	     			//For each sequence, you want to shift from upstream till downstream 
	     			//ie changing the +1 location
	     			//to get the scores by classifier one so that can use it to train classifier two later
	     			//Doing shift from upstream till downstream	 
	     			//if(lineCounter % 100 == 0)
	     				statusPane.setText("Evaluating fold " + (x+1) + ".. @ " + lineCounter + " / " + 
	     					totalTestSequenceCounter);
	     			
	     			fastaFormat = new FastaFormat(lineHeader,lineSequence);			     			
	     			int predictPosition[] = fastaFormat.getPredictPositionForClassifierOne(
	     				applicationData.getLeftMostPosition(),applicationData.getRightMostPosition());
		 			
	 				SequenceManipulation seq = new SequenceManipulation(lineSequence,
	 					predictPosition[0],predictPosition[1]);	     		
	     			int currentPosition = predictPosition[0];  
					String line2;
					if(lineCounter > posTestSequenceCounter)
		     			outputCrossValidation.write("neg");
		     		else
		     			outputCrossValidation.write("pos");	
	     			while((line2 = seq.nextShift())!=null){	     				
	     				Instance tempInst;
	     				tempInst = new Instance(inst.numAttributes());
	     				tempInst.setDataset(inst);     				
	     				for(int i = 0; i < inst.numAttributes() - 1; i++){
	     					//-1 because class attribute can be ignored
	     					//Give the sequence and the featureList to get the feature freqs on the sequence
	     					Object obj = GenerateArff.getMatchCount(lineHeader,line2,featureDataArrayList.get(i),applicationData.getScoringMatrixIndex(),
			     					applicationData.getCountingStyleIndex(),applicationData.getScoringMatrix());
		 					if(obj.getClass().getName().equalsIgnoreCase("java.lang.Integer"))
		 						tempInst.setValue(x,(Integer) obj);
		 					else if(obj.getClass().getName().equalsIgnoreCase("java.lang.Double"))				
		 						tempInst.setValue(x,(Double) obj);
		 					else if(obj.getClass().getName().equalsIgnoreCase("java.lang.String"))
		 						tempInst.setValue(x, (String) obj);
		 					else{
		 						testingInput.close();	   
		 						testClassifierTwoArff.close();
		 						outputCrossValidation.close();
		 						throw new Error("Unknown: " + obj.getClass().getName());
		 					}
			     		}
			     		if(lineCounter > posTestSequenceCounter)
			     			tempInst.setValue(inst.numAttributes() - 1,"neg");
			     		else
			     			tempInst.setValue(inst.numAttributes() - 1,"pos");			     		
			     		double[] results = foldClassifier.distributionForInstance(tempInst);
			     		outputCrossValidation.write("," + currentPosition + "=" + results[0]);
			     		//AHFU_DEBUG 
			     		double[] resultsDebug = classifierOne.distributionForInstance(tempInst);
			     		if(currentPosition >= setClassifierTwoUpstreamInt && currentPosition <= setClassifierTwoDownstreamInt)
			     			testClassifierTwoArff.write(resultsDebug[0] + ",");
			     		//AHFU_DEBUG_END
	     				currentPosition++;
	     				if(currentPosition == 0)
	     					currentPosition++;			     				
	     			}//end of sequence shift	     			     			
			     	outputCrossValidation.newLine();
			     	outputCrossValidation.flush();   	
			     	//AHFU_DEBUG
			     	if(lineCounter > posTestSequenceCounter)
			     		testClassifierTwoArff.write("neg");
		     		else
		     			testClassifierTwoArff.write("pos");			 			
		 			testClassifierTwoArff.newLine();
		 			testClassifierTwoArff.flush();
		 			//AHFU_DEBUG_END
	     		}//end of reading test file
	     		outputCrossValidation.close();
	     		testingInput.close();	    
	     		testClassifierTwoArff.close();
				fastaFile.cleanUp();	
					
				//NORMAL MODE
				//trainFile.delete();
				//testFile.delete();
				//NORMAL MODE END
				//AHFU_DEBUG MODE
				//testClassifierTwoArff.close();				
				trainFile.deleteOnExit();			
				testFile.deleteOnExit();	
				trainFileFasta.deleteOnExit();
				//AHFU_DEBUG_MODE_END
			}//end of for loop for xvalidation
						
			PredictionStats classifierOneStatsOnXValidation = new PredictionStats(
				applicationData.getWorkingDirectory() + File.separator + "ClassifierOne.scores",
				range,threshold);
			//display(double range)
			totalTimeElapsed = System.currentTimeMillis() - totalTimeStart;		
			classifierResults.updateList(classifierResults.getResultsList(), "Total Time Used: ",
				Utils.doubleToString(totalTimeElapsed / 60000,2) + " minutes " + Utils.doubleToString((totalTimeElapsed/1000.0) % 60.0,2) + 
  				" seconds");
			classifierOneStatsOnXValidation.updateDisplay(classifierResults, classifierOneDisplayTextArea,true);	
			applicationData.setClassifierOneStats(classifierOneStatsOnXValidation);
			myGraph.setMyStats(classifierOneStatsOnXValidation);					   		   			   
		   								
  			statusPane.setText("Done!");	  			  			  		
  			
  			return classifierOne;
		}catch(Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(parent,e.getMessage(),"ERROR",JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
     //For classifierTwo on cross-validation
     public static Classifier xValidateClassifierTwo(JInternalFrame parent,ApplicationData applicationData,
     	JTextArea classifierTwoDisplayTextArea,GenericObjectEditor m_ClassifierEditor2,
     	Classifier classifierOne,int folds,GraphPane myGraph,ClassifierResults classifierResults,
     	int range, double threshold, boolean outputClassifier){     		
		try{			
			StatusPane statusPane = applicationData.getStatusPane();			
			
			long totalTimeStart = System.currentTimeMillis(),totalTimeElapsed;						
			//Classifier tempClassifier = (Classifier) m_ClassifierEditor2.getValue();
			final int positiveDataset2FromInt = applicationData.getPositiveDataset2FromField(); 
	    	final int positiveDataset2ToInt = applicationData.getPositiveDataset2ToField();
	    	final int negativeDataset2FromInt = applicationData.getNegativeDataset2FromField();
	    	final int negativeDataset2ToInt = applicationData.getNegativeDataset2ToField();
	    		
	    	final int totalDataset2Sequences = (positiveDataset2ToInt - positiveDataset2FromInt + 1) + 
	    		(negativeDataset2ToInt - negativeDataset2FromInt +1);
	    		
	    	final int classifierTwoUpstream = applicationData.getSetUpstream();
	    	final int classifierTwoDownstream = applicationData.getSetDownstream();
	    		
	    	Step1TableModel positiveStep1TableModel = applicationData.getPositiveStep1TableModel();
			Step1TableModel negativeStep1TableModel = applicationData.getNegativeStep1TableModel();
			
			
			//Train classifier two with the full dataset first then do cross-validation to gauge its accuracy		   		    	
  			//Preparing Dataset2.arff to train Classifier Two
		    long trainTimeStart = 0, trainTimeElapsed = 0;
		    statusPane.setText("Preparing Dataset2.arff...");
		  //This step generates Dataset2.arff
		    if(DatasetGenerator.generateDataset2(parent,applicationData,
		    	applicationData.getSetUpstream(),applicationData.getSetDownstream(),classifierOne) == false){
		    	//Interrupted or Error occurred
		    	return null;
		    }		      
		    Instances instOfDataset2 = new Instances(new BufferedReader(new FileReader(
				applicationData.getWorkingDirectory() + File.separator + "Dataset2.arff")));
			instOfDataset2.setClassIndex(instOfDataset2.numAttributes() - 1);
			applicationData.setDataset2Instances(instOfDataset2);
			Classifier classifierTwo = (Classifier) m_ClassifierEditor2.getValue();		
			statusPane.setText("Training Classifier Two... May take a while... Please wait...");	
			//Record Start Time
			trainTimeStart = System.currentTimeMillis();
			if(outputClassifier)
				classifierTwo.buildClassifier(instOfDataset2);		
		    //Record Total Time used to build classifier one
		    trainTimeElapsed = System.currentTimeMillis() - trainTimeStart;		
		    //Training Done		    
			
			String classifierName = m_ClassifierEditor2.getValue().getClass().getName();		
			classifierResults.updateList(classifierResults.getClassifierList(), "Classifier: ", classifierName);
			classifierResults.updateList(classifierResults.getClassifierList(), "Training Data: ", 
					folds + " fold cross-validation on Dataset2.arff");
			classifierResults.updateList(classifierResults.getClassifierList(), "Time Used: ",
				Utils.doubleToString(trainTimeElapsed / 1000.0,2) + " seconds");
			
			Instances instOfDataset1 = new Instances(applicationData.getDataset1Instances());
  			instOfDataset1.setClassIndex(applicationData.getDataset1Instances().numAttributes() - 1);
			//Reading and Storing the featureList
		 	ArrayList<Feature> featureDataArrayList = new ArrayList<Feature>();
			for(int y = 0; y < instOfDataset1.numAttributes() - 1; y++){
				featureDataArrayList.add(Feature.levelOneClassifierPane(instOfDataset1.attribute(y).name()));
			}			
			
			//Generating an Instance given a sequence with the current attributes
			int setClassifierTwoUpstreamInt = applicationData.getSetUpstream();
			int setClassifierTwoDownstreamInt = applicationData.getSetDownstream();			
			int classifierTwoWindowSize;
			if(setClassifierTwoUpstreamInt < 0 && setClassifierTwoDownstreamInt > 0)
				classifierTwoWindowSize = (setClassifierTwoUpstreamInt * -1) + setClassifierTwoDownstreamInt;
			else if(setClassifierTwoUpstreamInt < 0 && setClassifierTwoDownstreamInt < 0)
				classifierTwoWindowSize = 
					(setClassifierTwoUpstreamInt - setClassifierTwoDownstreamInt - 1) * -1;
			else//both +ve
				classifierTwoWindowSize = 
					(setClassifierTwoDownstreamInt - setClassifierTwoUpstreamInt + 1);
			
			int posTestSequenceCounter = 0;			
	 						
	 		BufferedWriter outputCrossValidation = new BufferedWriter(new FileWriter(
	 			applicationData.getWorkingDirectory() + File.separator + "classifierTwo.scores"));
	 		
			for(int x = 0; x < folds; x++){
				File trainFile = new File(
					applicationData.getWorkingDirectory() + File.separator + "trainingDataset2_" + (x+1) + ".arff");
				File testFile = new File(
					applicationData.getWorkingDirectory() + File.separator + "testingDataset2_" + (x+1) + ".fasta");								
				
								
				statusPane.setText("Preparing Training Data for Fold " + (x+1) + "..");		
				FastaFileManipulation fastaFile = new FastaFileManipulation(positiveStep1TableModel,
				negativeStep1TableModel,positiveDataset2FromInt,positiveDataset2ToInt,
				negativeDataset2FromInt,negativeDataset2ToInt,applicationData.getWorkingDirectory());
				
				//1) generate trainingDataset2.arff headings
				BufferedWriter trainingOutputFile = new BufferedWriter(new FileWriter(
					applicationData.getWorkingDirectory() + File.separator + "trainingDataset2_" + (x+1) + ".arff"));	
				trainingOutputFile.write("@relation 'A temp file for X-validation purpose' ");
				trainingOutputFile.newLine();
				trainingOutputFile.newLine();
				trainingOutputFile.flush();
				for(int y = classifierTwoUpstream; y <= classifierTwoDownstream; y++){
					if(y!=0){
						trainingOutputFile.write("@attribute (" + y + ") numeric");
						trainingOutputFile.newLine();
						trainingOutputFile.flush();
					}				
				}	
				if(positiveDataset2FromInt > 0 && negativeDataset2FromInt > 0)
					trainingOutputFile.write("@attribute Class {pos,neg}");
				else if(positiveDataset2FromInt > 0 && negativeDataset2FromInt == 0)
					trainingOutputFile.write("@attribute Class {pos}");
				else if(positiveDataset2FromInt == 0 && negativeDataset2FromInt > 0)
					trainingOutputFile.write("@attribute Class {neg}");		
				trainingOutputFile.newLine();
				trainingOutputFile.newLine();
				trainingOutputFile.write("@data");
				trainingOutputFile.newLine();
				trainingOutputFile.newLine();
				trainingOutputFile.flush();
				//AHFU_DEBUG 
				BufferedWriter testingOutputFileArff = new BufferedWriter(new FileWriter(
					applicationData.getWorkingDirectory() + File.separator + "testingDataset2_" + (x+1) + ".arff"));	
				testingOutputFileArff.write("@relation 'A temp file for X-validation purpose' ");
				testingOutputFileArff.newLine();
				testingOutputFileArff.newLine();
				testingOutputFileArff.flush();
				for(int y = classifierTwoUpstream; y <= classifierTwoDownstream; y++){
					if(y!=0){
						testingOutputFileArff.write("@attribute (" + y + ") numeric");
						testingOutputFileArff.newLine();
						testingOutputFileArff.flush();
					}				
				}	
				if(positiveDataset2FromInt > 0 && negativeDataset2FromInt > 0)
					testingOutputFileArff.write("@attribute Class {pos,neg}");
				else if(positiveDataset2FromInt > 0 && negativeDataset2FromInt == 0)
					testingOutputFileArff.write("@attribute Class {pos}");
				else if(positiveDataset2FromInt == 0 && negativeDataset2FromInt > 0)
					testingOutputFileArff.write("@attribute Class {neg}");					
				testingOutputFileArff.newLine();
				testingOutputFileArff.newLine();
				testingOutputFileArff.write("@data");
				testingOutputFileArff.newLine();
				testingOutputFileArff.newLine();
				testingOutputFileArff.flush();
				//AHFU_DEBUG END
				//2) generate testingDataset2.fasta
				BufferedWriter testingOutputFile = new BufferedWriter(new FileWriter(
					applicationData.getWorkingDirectory() + File.separator + "testingDataset2_" + (x+1) + ".fasta"));
				
				//Now, populating datas for both the training and testing files				
				int fastaFileLineCounter = 0;
				posTestSequenceCounter = 0;
				int totalTestSequenceCounter = 0;
				int totalTrainTestSequenceCounter = 0;
				FastaFormat fastaFormat;
				//For pos sequences	
		 		while((fastaFormat = fastaFile.nextSequence("pos"))!=null){
		 			if(applicationData.terminateThread == true){
				    	statusPane.setText("Interrupted - Classifier Two Trained");
				    	outputCrossValidation.close();
				    	testingOutputFileArff.close();
				    	testingOutputFile.close();
				    	trainingOutputFile.close();
				    	return classifierTwo;
				    }
		 			totalTrainTestSequenceCounter++;
		 			//if(totalTrainTestSequenceCounter%100 == 0)
		 				statusPane.setText("Preparing Training Data for Fold " + (x+1) + ".. @ " + 
		 					totalTrainTestSequenceCounter + " / " + totalDataset2Sequences);
		 			if((fastaFileLineCounter%folds) == x){//This sequence is for testing
		 				testingOutputFile.write(fastaFormat.getHeader());
		 				testingOutputFile.newLine();
		 				testingOutputFile.write(fastaFormat.getSequence());
						testingOutputFile.newLine();
						testingOutputFile.flush();
						posTestSequenceCounter++;
						totalTestSequenceCounter++;
						//AHFU DEBUG
						SequenceManipulation seq = new SequenceManipulation(fastaFormat.getSequence(),
		 					classifierTwoUpstream, classifierTwoDownstream);		     		
		     			String line2; 		
		     			while((line2 = seq.nextShift())!=null){
		     				Instance tempInst = new Instance(instOfDataset1.numAttributes());
		     				tempInst.setDataset(instOfDataset1);     				
		     				//-1 because class attribute can be ignored
		     				for(int w = 0; w < instOfDataset1.numAttributes() - 1; w++){
		     					Object obj = GenerateArff.getMatchCount(fastaFormat.getHeader(),line2,
				     					featureDataArrayList.get(w),applicationData.getScoringMatrixIndex(),
				     					applicationData.getCountingStyleIndex(),applicationData.getScoringMatrix());
			 					if(obj.getClass().getName().equalsIgnoreCase("java.lang.Integer"))
			 						tempInst.setValue(w,(Integer) obj);
			 					else if(obj.getClass().getName().equalsIgnoreCase("java.lang.Double"))				
			 						tempInst.setValue(w,(Double) obj);
			 					else if(obj.getClass().getName().equalsIgnoreCase("java.lang.String"))
			 						tempInst.setValue(w, (String) obj);
			 					else{
			 						outputCrossValidation.close();
							    	testingOutputFileArff.close();
							    	testingOutputFile.close();
							    	trainingOutputFile.close();
			 						throw new Error("Unknown: " + obj.getClass().getName());
			 					}
				     		}     	
				     		tempInst.setValue(tempInst.numAttributes() - 1,"pos");		     		
				     		double[] results = classifierOne.distributionForInstance(tempInst);
				     		testingOutputFileArff.write(results[0] + ",");		     		
		     			}     		
		     			testingOutputFileArff.write("pos");
		     			testingOutputFileArff.newLine();
		     			testingOutputFileArff.flush();		 	
						//AHFU DEBUG END
		 			}
		 			else{//This sequence is for training
		 				SequenceManipulation seq = new SequenceManipulation(fastaFormat.getSequence(),
		 					classifierTwoUpstream, classifierTwoDownstream);		     		
		     			String line2; 		
		     			while((line2 = seq.nextShift())!=null){
		     				Instance tempInst = new Instance(instOfDataset1.numAttributes());
		     				tempInst.setDataset(instOfDataset1);     				
		     				//-1 because class attribute can be ignored
		     				for(int w = 0; w < instOfDataset1.numAttributes() - 1; w++){
		     					Object obj = GenerateArff.getMatchCount(fastaFormat.getHeader(),line2,
				     					featureDataArrayList.get(w),applicationData.getScoringMatrixIndex(),
				     					applicationData.getCountingStyleIndex(),applicationData.getScoringMatrix());
			 					if(obj.getClass().getName().equalsIgnoreCase("java.lang.Integer"))
			 						tempInst.setValue(w,(Integer) obj);
			 					else if(obj.getClass().getName().equalsIgnoreCase("java.lang.Double"))				
			 						tempInst.setValue(w,(Double) obj);
			 					else if(obj.getClass().getName().equalsIgnoreCase("java.lang.String"))
			 						tempInst.setValue(w, (String) obj);
			 					else{
			 						outputCrossValidation.close();
							    	testingOutputFileArff.close();
							    	testingOutputFile.close();
							    	trainingOutputFile.close();
			 						throw new Error("Unknown: " + obj.getClass().getName());
			 					}
				     		}     	
				     		tempInst.setValue(tempInst.numAttributes() - 1,"pos");		     		
				     		double[] results = classifierOne.distributionForInstance(tempInst);
				     		trainingOutputFile.write(results[0] + ",");		     		
		     			}     		
			 			trainingOutputFile.write("pos");
						trainingOutputFile.newLine();
						trainingOutputFile.flush();		 				
		 			}
		 			fastaFileLineCounter++;
		 		}
		 		//For neg sequences
		 		fastaFileLineCounter = 0;
		 		while((fastaFormat = fastaFile.nextSequence("neg"))!=null){
		 			if(applicationData.terminateThread == true){
				    	statusPane.setText("Interrupted - Classifier Two Trained");
				    	outputCrossValidation.close();
				    	testingOutputFileArff.close();
				    	testingOutputFile.close();
				    	trainingOutputFile.close();
				    	return classifierTwo;
				    }
		 			totalTrainTestSequenceCounter++;
		 			//if(totalTrainTestSequenceCounter%100 == 0)
		 				statusPane.setText("Preparing Training Data for Fold " + (x+1) + ".. @ " + 
		 					totalTrainTestSequenceCounter + " / " + totalDataset2Sequences);
		 			if((fastaFileLineCounter%folds) == x){//This sequence is for testing
		 				testingOutputFile.write(fastaFormat.getHeader());
		 				testingOutputFile.newLine();
		 				testingOutputFile.write(fastaFormat.getSequence());
						testingOutputFile.newLine();
						testingOutputFile.flush();
						totalTestSequenceCounter++;
						//AHFU DEBUG
						SequenceManipulation seq = new SequenceManipulation(fastaFormat.getSequence(),
		 					classifierTwoUpstream, classifierTwoDownstream);		     		
		     			String line2; 		
		     			while((line2 = seq.nextShift())!=null){
		     				Instance tempInst = new Instance(instOfDataset1.numAttributes());
		     				tempInst.setDataset(instOfDataset1);     				
		     				//-1 because class attribute can be ignored
		     				for(int w = 0; w < instOfDataset1.numAttributes() - 1; w++){
		     					Object obj = GenerateArff.getMatchCount(fastaFormat.getHeader(),line2,
				     					featureDataArrayList.get(w),applicationData.getScoringMatrixIndex(),
				     					applicationData.getCountingStyleIndex(),applicationData.getScoringMatrix());
			 					if(obj.getClass().getName().equalsIgnoreCase("java.lang.Integer"))
			 						tempInst.setValue(w,(Integer) obj);
			 					else if(obj.getClass().getName().equalsIgnoreCase("java.lang.Double"))				
			 						tempInst.setValue(w,(Double) obj);
			 					else if(obj.getClass().getName().equalsIgnoreCase("java.lang.String"))
			 						tempInst.setValue(w, (String) obj);
			 					else{
			 						outputCrossValidation.close();
							    	testingOutputFileArff.close();
							    	testingOutputFile.close();
							    	trainingOutputFile.close();
			 						throw new Error("Unknown: " + obj.getClass().getName());
			 					}
				     		}     	
				     		tempInst.setValue(tempInst.numAttributes() - 1,"pos");//pos or neg does not matter here - not used   		
				     		double[] results = classifierOne.distributionForInstance(tempInst);
				     		testingOutputFileArff.write(results[0] + ",");		     		
		     			}     		
		     			testingOutputFileArff.write("neg");
		     			testingOutputFileArff.newLine();
		     			testingOutputFileArff.flush();		 	
						//AHFU DEBUG END
		 			}
		 			else{//This sequence is for training
		 				SequenceManipulation seq = new SequenceManipulation(fastaFormat.getSequence(),
		 					classifierTwoUpstream,classifierTwoDownstream);		     			
		     			String line2; 		
		     			while((line2 = seq.nextShift())!=null){
		     				Instance tempInst = new Instance(instOfDataset1.numAttributes());
		     				tempInst.setDataset(instOfDataset1);     				
		     				//-1 because class attribute can be ignored
		     				for(int w = 0; w < instOfDataset1.numAttributes() - 1; w++){
		     					Object obj = GenerateArff.getMatchCount(fastaFormat.getHeader(),line2,
				     					featureDataArrayList.get(w),applicationData.getScoringMatrixIndex(),
				     					applicationData.getCountingStyleIndex(),applicationData.getScoringMatrix());
			 					if(obj.getClass().getName().equalsIgnoreCase("java.lang.Integer"))
			 						tempInst.setValue(w,(Integer) obj);
			 					else if(obj.getClass().getName().equalsIgnoreCase("java.lang.Double"))				
			 						tempInst.setValue(w,(Double) obj);
			 					else if(obj.getClass().getName().equalsIgnoreCase("java.lang.String"))
			 						tempInst.setValue(w, (String) obj);
			 					else{
			 						outputCrossValidation.close();
							    	testingOutputFileArff.close();
							    	testingOutputFile.close();
							    	trainingOutputFile.close();
			 						throw new Error("Unknown: " + obj.getClass().getName());
			 					}
				     		}     	
				     		tempInst.setValue(tempInst.numAttributes() - 1,"pos");//pos or neg does not matter here - not used	     		
				     		double[] results = classifierOne.distributionForInstance(tempInst);
				     		trainingOutputFile.write(results[0] + ",");		     		
		     			}     		
			 			trainingOutputFile.write("neg");
						trainingOutputFile.newLine();
						trainingOutputFile.flush();		 	
		 			}
		 			fastaFileLineCounter++;
		 		}													
		 		trainingOutputFile.close();	
				testingOutputFile.close();
				
				//AHFU_DEBUG
				testingOutputFileArff.close();
				//AHFU DEBUG END
				//3) train and test classifier two then store the statistics
				statusPane.setText("Building Fold " + (x+1) + "..");
				//open an input stream to the arff file 
				BufferedReader trainingInput = new BufferedReader(new FileReader(
					applicationData.getWorkingDirectory() + File.separator + "trainingDataset2_" + (x+1) + ".arff"));
				//getting ready to train a foldClassifier using arff file
				Instances instOfTrainingDataset2 = new Instances(new BufferedReader(new FileReader(
					applicationData.getWorkingDirectory() + File.separator + "trainingDataset2_" + (x+1) + ".arff")));
					instOfTrainingDataset2.setClassIndex(instOfTrainingDataset2.numAttributes() - 1);  				
				Classifier foldClassifier = (Classifier) m_ClassifierEditor2.getValue();				
			    foldClassifier.buildClassifier(instOfTrainingDataset2);			    
  				trainingInput.close();  				  				  				  		
  				
	  			//Reading the test file
	  			statusPane.setText("Evaluating fold " + (x+1) + "..");
	  			BufferedReader testingInput = new BufferedReader(new FileReader(
	  				applicationData.getWorkingDirectory() + File.separator + "testingDataset2_" + (x+1) + ".fasta"));
	  			int lineCounter = 0;	 
	  			String lineHeader;
	  			String lineSequence; 				  			     			 					
	     		while((lineHeader = testingInput.readLine())!=null){
	     			if(applicationData.terminateThread == true){
				    	statusPane.setText("Interrupted - Classifier Two Not Trained");
				    	outputCrossValidation.close();
				    	testingOutputFileArff.close();
				    	testingOutputFile.close();
				    	trainingOutputFile.close();
				    	testingInput.close();
				    	return classifierTwo;
				    }
	     			lineSequence = testingInput.readLine();
	     			outputCrossValidation.write(lineHeader);
	     			outputCrossValidation.newLine();
	     			outputCrossValidation.write(lineSequence);
	     			outputCrossValidation.newLine();
	     			lineCounter++;
	     			fastaFormat = new FastaFormat(lineHeader,lineSequence);
	     			int arraySize = fastaFormat.getArraySize(
						applicationData.getLeftMostPosition(),applicationData.getRightMostPosition());
	     			double scores[] = new double[arraySize];
					int predictPosition[] = fastaFormat.getPredictPositionForClassifierOne(
						applicationData.getLeftMostPosition(),applicationData.getRightMostPosition());
	     			//For each sequence, you want to shift from upstream till downstream 
	     			//ie changing the +1 location
	     			//to get the scores by classifier one so that can use it to train classifier two later
	     			//Doing shift from upstream till downstream	 
	     			//if(lineCounter % 100 == 0)
	     				statusPane.setText("Evaluating fold " + (x+1) + ".. @ " + lineCounter + " / " + 
	     					totalTestSequenceCounter);
	 				SequenceManipulation seq = new SequenceManipulation(lineSequence,predictPosition[0],
	 					predictPosition[1]);
	     			int scoreCount = 0;	     			
					String line2;					    		
	     			while((line2 = seq.nextShift())!=null){	     				
	     				Instance tempInst = new Instance(instOfDataset1.numAttributes());
	     				tempInst.setDataset(instOfDataset1);     				
	     				for(int i = 0; i < instOfDataset1.numAttributes() - 1; i++){
	     					//-1 because class attribute can be ignored
	     					//Give the sequence and the featureList to get the feature freqs on the sequence
	     					Object obj = GenerateArff.getMatchCount(lineHeader,line2,featureDataArrayList.get(i),
			     					applicationData.getScoringMatrixIndex(),applicationData.getCountingStyleIndex(),
			     					applicationData.getScoringMatrix());
		 					if(obj.getClass().getName().equalsIgnoreCase("java.lang.Integer"))
		 						tempInst.setValue(i,(Integer) obj);
		 					else if(obj.getClass().getName().equalsIgnoreCase("java.lang.Double"))				
		 						tempInst.setValue(i,(Double) obj);
		 					else if(obj.getClass().getName().equalsIgnoreCase("java.lang.String"))
		 						tempInst.setValue(i, (String) obj);
		 					else{
		 						outputCrossValidation.close();
						    	testingOutputFileArff.close();
						    	testingOutputFile.close();
						    	trainingOutputFile.close();
						    	testingInput.close();
		 						throw new Error("Unknown: " + obj.getClass().getName());
		 					}
			     		}
			     		if(lineCounter > posTestSequenceCounter){//for neg
			     			tempInst.setValue(tempInst.numAttributes() - 1,"neg");
			     		}
			     		else{
			     			tempInst.setValue(tempInst.numAttributes() - 1,"pos");
			     		}
			     		double[] results = classifierOne.distributionForInstance(tempInst);
			     		scores[scoreCount++] = results[0];
	     			}//end of sequence shift 
	     			//Run classifierTwo     				
	 				int currentPosition = fastaFormat.getPredictionFromForClassifierTwo(
	 					applicationData.getLeftMostPosition(),applicationData.getRightMostPosition(),
	 					applicationData.getSetUpstream());  
	 				if(lineCounter > posTestSequenceCounter)//neg
	 					outputCrossValidation.write("neg");	 					
	 				else
	 					outputCrossValidation.write("pos");
	 				for(int y = 0; y < arraySize - classifierTwoWindowSize + 1; y++){
	 					//+1 is for the class index
	     				Instance tempInst2 = new Instance(classifierTwoWindowSize + 1);
	     				tempInst2.setDataset(instOfTrainingDataset2);
	 					for(int l = 0; l < classifierTwoWindowSize; l++){
	     					tempInst2.setValue(l,scores[l+y]);
	     				}     					     				
						if(lineCounter > posTestSequenceCounter)//for neg
							tempInst2.setValue(tempInst2.numAttributes() - 1,"neg");
			     		else//for pos				     			
			     			tempInst2.setValue(tempInst2.numAttributes() - 1,"pos");
			     		double[] results = foldClassifier.distributionForInstance(tempInst2);
			     		outputCrossValidation.write("," + currentPosition + "=" + results[0]);
			     		currentPosition++;
			     		if(currentPosition == 0)
			     			currentPosition++;
	 				}     				 				
	 				outputCrossValidation.newLine();
	 				outputCrossValidation.flush();
	     		}//end of reading test file
	     		outputCrossValidation.close();
		    	testingOutputFileArff.close();
		    	testingOutputFile.close();
		    	trainingOutputFile.close();
	     		testingInput.close();	     		     		
				fastaFile.cleanUp();	
				
				//AHFU_DEBUG
				trainFile.deleteOnExit();				
				testFile.deleteOnExit();
				
				//NORMAL MODE
				//trainFile.delete();
				//testFile.delete();
			}//end of for loop for xvalidation		
				
			PredictionStats classifierTwoStatsOnXValidation = new PredictionStats(
				applicationData.getWorkingDirectory() + File.separator + "classifierTwo.scores",
				range,threshold);
			//display(double range)
			totalTimeElapsed = System.currentTimeMillis() - totalTimeStart;		
			classifierResults.updateList(classifierResults.getResultsList(), "Total Time Used: ",
				Utils.doubleToString(totalTimeElapsed / 60000,2) + " minutes " + Utils.doubleToString((totalTimeElapsed/1000.0) % 60.0,2) + 
  				" seconds");
			classifierTwoStatsOnXValidation.updateDisplay(classifierResults, classifierTwoDisplayTextArea,true);
			applicationData.setClassifierTwoStats(classifierTwoStatsOnXValidation);
			myGraph.setMyStats(classifierTwoStatsOnXValidation);						
		   														
  			statusPane.setText("Done!");	  			  			
  			
  			return classifierTwo;
		}catch(Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(parent,e.getMessage(),"ERROR",JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}	
}