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
package sirius.trainer.features;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import sirius.predictor.main.ClassifierData;
import sirius.trainer.main.ScoringMatrix;
import sirius.utils.FastaFormat;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

public class ClassifierFeature extends Feature implements Serializable{
	static final long serialVersionUID = sirius.Sirius.version;

	private ClassifierData classifier;

	public ClassifierData getClassifier(){
		return this.classifier;
	}

	//This is for Classifier Features	
	public ClassifierFeature(String name, ClassifierData classifier){
		super(name, 
				"Classifier - #Features: " + (classifier.getInstances().numAttributes() - 1) + " [" + 
				classifier.getLeftMostPosition() + "," + classifier.getRightMostPosition() + "]",
		'Z');		
		this.classifier = classifier;				
	}

	//This constructor is for loadSettings method in Feature
	public static ClassifierFeature loadSettings(String line, String loadDirectory){
		String name = line.substring(line.indexOf("Name: ") + 
				("Name: ").length(),line.indexOf("FileName: ") - 1);			
		String filename = line.substring(line.indexOf("FileName: ") + ("FileName: ").length());
		return new ClassifierFeature(name, filename, loadDirectory);
	}

	public ClassifierFeature(String name, String filename, String loadDirectory){		
		super(name,"",'Z');			
		loadClassifier(loadDirectory, filename);
		this.details = "Classifier - #Features: " + (classifier.getInstances().numAttributes() - 1) + " [" + 
		classifier.getLeftMostPosition() + "," + classifier.getRightMostPosition() + "]";
	}

	public String saveString(String saveDirectory){
		if(type != 'Z')
			return "UNKNOWN TYPE";
		//before returning the saveString, first save the write the classifier to file first
		File file = new File(saveDirectory + File.separator + this.name + ".classifierone");	                        	
		String savingFilename = file.getAbsolutePath();		
		try{										        		        		        			        		       		        
			FileOutputStream fos1 = new FileOutputStream(savingFilename);
			ObjectOutputStream oos1 = new ObjectOutputStream(fos1);
			oos1.writeInt(this.classifier.getClassifierType());	        	        	        
			oos1.writeObject(this.name);			        
			oos1.writeObject(this.classifier.getClassifierOneSettings());
			oos1.writeObject(this.classifier.getInstances());
			oos1.writeObject(this.classifier.getClassifierOne());		
			//newly added for version 1.1 and above
			oos1.writeObject(this.classifier.getSequenceType());
			oos1.writeInt(this.classifier.getScoringMatrixIndex());
			oos1.writeInt(this.classifier.getCountingStyleIndex());
			oos1.close();			
			return "Type: " + type + " Name: " + this.name + " FileName: " + this.name + ".classifierone";
		}catch(Exception e){e.printStackTrace(); return null;}	
	}

	private boolean loadClassifier(String loadDirectory, String filename){
		try{
			File file = new File(filename);		
			FileInputStream fis = new FileInputStream(loadDirectory + File.separator + file);
			ObjectInputStream ois = new ObjectInputStream(fis);

			int classifierNum = ois.readInt();		        					        		        
			String classifierName = (String) ois.readObject();
			String classifierOneSettings = (String) ois.readObject();		        		        
			Instances instances = (Instances) ois.readObject();
			Classifier classifierOne = (Classifier) ois.readObject();
			String sequenceType = (String) ois.readObject();		        
			int scoringMatrixIndex = ois.readInt();
			int countingStyleIndex = ois.readInt();
			int setUpstream = -1;
			int setDownstream = -1;
			String classifierTwoSettings = "";
			Instances instances2 = null;
			Classifier classifierTwo = null;
			if(classifierNum == 2){	        	
				setUpstream = ois.readInt();
				setDownstream = ois.readInt();
				classifierTwoSettings = (String) ois.readObject();
				instances2 = (Instances) ois.readObject();
				classifierTwo = (Classifier) ois.readObject();
				ois.close();
				throw new Error("Error: Cannot be here.. ");
			}		        										
			ois.close();
			this.classifier = new ClassifierData(classifierNum,classifierName,instances,
					classifierOne,classifierTwo,classifierOneSettings,classifierTwoSettings,setUpstream,
					setDownstream,instances2,sequenceType,scoringMatrixIndex,countingStyleIndex);
			this.details = "Classifier - #Features: " + (this.classifier.getInstances().numAttributes() - 1) + " [" + 
			this.classifier.getLeftMostPosition() + "," + this.classifier.getRightMostPosition() + "]";	
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	public double compute(FastaFormat fastaFormat) throws Exception{    	 
		//This is for type3 classifier - one prediction per sequence	
		//Reading and Storing the featureList
		Instances inst = classifier.getInstances();
		ArrayList<Feature> featureDataArrayList = new ArrayList<Feature>();
		for(int x = 0; x < inst.numAttributes() - 1; x++){
			//-1 because class attribute must be ignored
			featureDataArrayList.add(Feature.levelOneClassifierPane(inst.attribute(x).name()));
		}     	 			    	 		
		//Sequence Score -> index-score, index-score
		String sequence = fastaFormat.getSequence();
		Instance tempInst;
		tempInst = new Instance(inst.numAttributes());
		tempInst.setDataset(inst);     				
		for(int z = 0; z < inst.numAttributes() - 1; z++){
			//-1 because class attribute can be ignored
			//Give the sequence and the featureList to get the feature freqs on the sequence
			Object obj = GenerateArff.getMatchCount(
					"+1_Index(-1)",sequence,featureDataArrayList.get(z),this.classifier.getScoringMatrixIndex(),
					this.classifier.getCountingStyleIndex(),this.classifier.getScoringMatrix());
			if(obj.getClass().getName().equalsIgnoreCase("java.lang.Integer"))			
				tempInst.setValue(z,(Integer) obj);
			else if(obj.getClass().getName().equalsIgnoreCase("java.lang.Double"))
				tempInst.setValue(z,(Double) obj);
			else if(obj.getClass().getName().equalsIgnoreCase("java.lang.String"))
				tempInst.setValue(z, (String) obj);
			else
				throw new Error("Unknown: " + obj.getClass().getName());
		}     	
		//note that pos or neg does not matter as this is not used
		tempInst.setValue(inst.numAttributes() - 1,"pos");    	 
		this.classifier.getClassifierOne().toString();
		double[] results = this.classifier.getClassifierOne().distributionForInstance(tempInst);    	     			
		return results[0];		
	}	

	@Override
	public Object computeDNA(FastaFormat fastaFormat) {
		throw new Error("ClassifierFeature.computeDNA should not be called!");	
	}

	@Override
	public Object computeProtein(FastaFormat fastaFormat,
			int scoringMatrixIndex,int countingStyleIndex,ScoringMatrix scoringMatrix) {
		throw new Error("ClassifierFeature.computeProtein should not be called!");
	}

	@Override
	public Feature mutate(Random rand, int windowMin, int windowMax) {
		throw new Error("Unhandled case");
	}
}
