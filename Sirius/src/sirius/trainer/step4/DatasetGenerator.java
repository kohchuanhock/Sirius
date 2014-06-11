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
import java.io.FileWriter;
import java.util.ArrayList;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

import sirius.main.ApplicationData;
import sirius.main.FastaFileManipulation;
import sirius.trainer.features.Feature;
import sirius.trainer.features.GenerateArff;
import sirius.trainer.main.SequenceManipulation;
import sirius.trainer.main.StatusPane;
import sirius.utils.FastaFormat;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
 

public class DatasetGenerator {

    public DatasetGenerator() {
    }
        
    public static boolean generateDataset2(JInternalFrame parent,ApplicationData applicationData,
    	int classifierTwoUpstream,int classifierTwoDownstream,Classifier classifierOne){
    	try{
    		StatusPane statusPane = applicationData.getStatusPane();
    		    		    		
	    	int positiveDataset2FromInt = applicationData.getPositiveDataset2FromField(); 
			int positiveDataset2ToInt = applicationData.getPositiveDataset2ToField();
			int negativeDataset2FromInt = applicationData.getNegativeDataset2FromField();
			int negativeDataset2ToInt = applicationData.getNegativeDataset2ToField();
				
			int totalDataset2PositiveInstances = positiveDataset2ToInt - positiveDataset2FromInt + 1;
			int totalDataset2NegativeInstances = negativeDataset2ToInt - negativeDataset2FromInt + 1;
			int totalDataset2Instances = totalDataset2PositiveInstances + totalDataset2NegativeInstances;
			
			int scoringMatrixIndex = applicationData.getScoringMatrixIndex();
			int countingStyleIndex = applicationData.getCountingStyleIndex();
			
			//Generate the header for Dataset2.arff
			BufferedWriter dataset2OutputFile = new BufferedWriter(new FileWriter(
				applicationData.getWorkingDirectory() + File.separator + "Dataset2.arff"));
			dataset2OutputFile.write("@relation 'Dataset2.arff' ");
			dataset2OutputFile.newLine();
			dataset2OutputFile.newLine();
			dataset2OutputFile.flush();
			for(int x = classifierTwoUpstream; x <= classifierTwoDownstream; x++){
				if(x!=0){//This statment is used because in sequence position only -1,+1 dun have 0
					dataset2OutputFile.write("@attribute (" + x + ") numeric");
					dataset2OutputFile.newLine();
					dataset2OutputFile.flush();
				}				
			}						
			if(positiveDataset2FromInt > 0 && negativeDataset2FromInt > 0)
				dataset2OutputFile.write("@attribute Class {pos,neg}");
			else if(positiveDataset2FromInt > 0 && negativeDataset2FromInt == 0)
				dataset2OutputFile.write("@attribute Class {pos}");
			else if(positiveDataset2FromInt == 0 && negativeDataset2FromInt > 0)
				dataset2OutputFile.write("@attribute Class {neg}");
			dataset2OutputFile.newLine();
			dataset2OutputFile.newLine();
			dataset2OutputFile.write("@data");
			dataset2OutputFile.newLine();
			dataset2OutputFile.newLine();
			dataset2OutputFile.flush();
			
			//Generating an Instance given a sequence with the current attributes
			//for dataset2.arff
									
			//Need this for parameter setting for tempInst
			Instances inst = applicationData.getDataset1Instances();
			inst.deleteAttributeType(Attribute.STRING);
			FastaFileManipulation fastaFile = new FastaFileManipulation(
				applicationData.getPositiveStep1TableModel(),applicationData.getNegativeStep1TableModel(),
				positiveDataset2FromInt,positiveDataset2ToInt,negativeDataset2FromInt,negativeDataset2ToInt,
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
			FastaFormat fastaFormat;
	 		while((fastaFormat = fastaFile.nextSequence(_class))!=null){
	 			if(applicationData.terminateThread == true){
	 				statusPane.setText("Interrupted - Classifier Two Training Not Complete");
	 				dataset2OutputFile.close();
	 				return false;
	 			}
	 			lineCounter++;//Putting it here will mean if lineCounter is x then line == sequence x
	 			//if((lineCounter % 100) == 0){     				
	 				dataset2OutputFile.flush();
	 				statusPane.setText("Generating Dataset2.arff.. @ " + lineCounter + " / " 
	 					+ totalDataset2Instances + " Sequences"); 				
	 			//}
	 			//For each sequence, you want to shift from upstream till downstream 
	 			//ie changing the +1 location
	 			//to get the scores given by classifier one so that you can use it to train classifier two later
	 			//Doing shift from upstream till downstream	     			 			  
				SequenceManipulation seq = new SequenceManipulation(fastaFormat.getSequence(),
					classifierTwoUpstream,classifierTwoDownstream);	 			 
				String line2;     			 			   			
	 			while((line2 = seq.nextShift())!=null){
	 				Instance tempInst;
	 				tempInst = new Instance(inst.numAttributes());
	 				tempInst.setDataset(inst);     				
	 				for(int x = 0; x < inst.numAttributes() - 1; x++){
	 					//-1 because class attribute can be ignored
	 					//Give the sequence and the featureList to get the feature freqs on the sequence
	 					Object obj = GenerateArff.getMatchCount(fastaFormat.getHeader(),line2,featureDataArrayList.get(x),
		     					scoringMatrixIndex,countingStyleIndex,applicationData.getScoringMatrix());
	 					if(obj.getClass().getName().equalsIgnoreCase("java.lang.Integer"))
	 						tempInst.setValue(x,(Integer) obj);
	 					else if(obj.getClass().getName().equalsIgnoreCase("java.lang.Double"))				
	 						tempInst.setValue(x,(Double) obj);
	 					else if(obj.getClass().getName().equalsIgnoreCase("java.lang.String"))
	 						tempInst.setValue(x, (String) obj);
	 					else{
	 						dataset2OutputFile.close();
	 						throw new Error("Unknown: " + obj.getClass().getName());
	 					}
		     		}     	
		     		tempInst.setValue(inst.numAttributes() - 1,_class);				     	
		     		double[] results = classifierOne.distributionForInstance(tempInst);		     	
		     		dataset2OutputFile.write("" + results[0] + ",");	     						     		
	 			}     			
	 			dataset2OutputFile.write(_class);
	 			dataset2OutputFile.newLine();  
	 			if(lineCounter == totalDataset2PositiveInstances)
	 				_class = "neg";   			 			
	 		}     		     				    			
			dataset2OutputFile.close();			
			fastaFile.cleanUp();			
    	}catch(Exception e){
    		e.printStackTrace();
    		JOptionPane.showMessageDialog(parent,e.getMessage(),"ERROR",JOptionPane.ERROR_MESSAGE);
    		applicationData.getStatusPane().setText("Error - Classifier Two Training Not Complete");
    		return false;
    	}
    	return true;
    }   
}