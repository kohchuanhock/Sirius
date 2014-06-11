package sirius.predictor.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JOptionPane;

import sirius.membranetype.MembraneTypePrediction;
import sirius.trainer.features.ClassifierFeature;
import sirius.trainer.features.Feature;
import sirius.trainer.features.GenerateArff;
import sirius.trainer.main.SiriusSettings;
import sirius.utils.Arff;
import sirius.utils.ContinuousMI;
import sirius.utils.FastaFormat;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class SiriusClassifier {	
	public static void saveClassifierOne(String saveFilename,
			boolean isLocationIndexMinusOne, String classifierName, String sequenceType,
			int scoringMatrixIndex, int countingStyleIndex, Classifier trainedClassifierOne,
			Instances trainingInstances){
		try{
			if(sequenceType.equals("DNA") == false && sequenceType.equals("Protein") == false){
				throw new Error("sequenceType should be either 'DNA' or 'Protein'");
			}
			if(scoringMatrixIndex != 0 && scoringMatrixIndex != 1 && scoringMatrixIndex != 2){
				throw new Error("scoringMatrixIndex should be either '0', '1' or '2'");
			}
			if(countingStyleIndex != 0 && countingStyleIndex != 1){
				throw new Error("countingStyleIndex should be either '0' or '1'");
			}
			if(trainedClassifierOne == null){
				throw new Error("trainedClassifierOne should not be null");
			}
			FileOutputStream fos1 = new FileOutputStream(saveFilename);
			ObjectOutputStream oos1 = new ObjectOutputStream(fos1);
			if(isLocationIndexMinusOne){
				oos1.writeInt(3);			        	
			}else{
				oos1.writeInt(1);			        	
			}	        		        
			oos1.writeObject(classifierName);	        
			oos1.writeObject(Utils.joinOptions(trainedClassifierOne.getOptions()));
			oos1.writeObject(trainingInstances);
			oos1.writeObject(trainedClassifierOne);		
			//newly added for version 1.1 and above
			oos1.writeObject(sequenceType);//DNA or Protein
			oos1.writeInt(scoringMatrixIndex);
			oos1.writeInt(countingStyleIndex);
			oos1.close();				
		}catch(Exception e){e.printStackTrace();}		
	}

	public static ClassifierData loadClassifier(String fileLocation) throws Exception{		
		File file = new File(fileLocation);
		SiriusSettings.updateInformation("LastClassifierFileLocationByLoader: ", file.getAbsolutePath());
		FileInputStream fis = new FileInputStream(file);
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
		}		        										
		ois.close();
		return new ClassifierData(classifierNum,classifierName,instances,
				classifierOne,classifierTwo,classifierOneSettings,classifierTwoSettings,setUpstream,
				setDownstream,instances2,sequenceType,scoringMatrixIndex,countingStyleIndex);	
	}

	public static double[] obtainPredictions(Classifier classifier, List<Feature> featureList, 
			List<FastaFormat> fastaList) throws Exception{
		for(Feature f:featureList){
			f.setValueList(SiriusClassifier.getFeatureValue(f, fastaList));
		}
		double[] classList = new double[fastaList.size()];//does not matter since it is used for test
		File arffFile = generateArffFromFeature(featureList, classList);
		Instances testInstances = Arff.getAsInstances(arffFile);
		testInstances.setClassIndex(testInstances.numAttributes() - 1);
		double[] scoreList = new double[fastaList.size()];
		for(int x = 0; x < scoreList.length; x++){
			scoreList[x] = classifier.distributionForInstance(testInstances.instance(x))[0];
		}
		return scoreList;
	}

	public static Instance convert2Instance(List<Feature> featureList, 
			FastaFormat fasta){
		for(Feature f:featureList){
			f.setValueList(SiriusClassifier.getFeatureValue(f, fasta));
		}
		double[] classList = new double[1];//does not matter since it is used for test
		File arffFile = generateArffFromFeature(featureList, classList);
		Instances testInstances = Arff.getAsInstances(arffFile);
		testInstances.setClassIndex(testInstances.numAttributes() - 1);
		return testInstances.instance(0);
	}

	public static double[] obtainPredictions(Classifier classifier, List<Feature> featureList, 
			FastaFormat fasta) throws Exception{
		for(Feature f:featureList){
			f.setValueList(SiriusClassifier.getFeatureValue(f, fasta));
		}
		double[] classList = new double[1];//does not matter since it is used for test
		File arffFile = generateArffFromFeature(featureList, classList);
		Instances testInstances = Arff.getAsInstances(arffFile);
		testInstances.setClassIndex(testInstances.numAttributes() - 1);		
		return classifier.distributionForInstance(testInstances.instance(0));
	}

	public static double[] runType3Classifier(ClassifierData classifierData, 
			FastaFormat fasta) throws Exception{
		Classifier classifierOne = classifierData.getClassifierOne();					
		//Reading and Storing the featureList
		Instances inst = classifierData.getInstances();
		ArrayList<Feature> featureDataArrayList = new ArrayList<Feature>();
		for(int x = 0; x < inst.numAttributes() - 1; x++){
			//-1 because class attribute must be ignored
			featureDataArrayList.add(Feature.levelOneClassifierPane(inst.attribute(x).name()));
		}
		String sequence = fasta.getSequence();								
		Instance tempInst;
		tempInst = new Instance(inst.numAttributes());
		tempInst.setDataset(inst);     				
		for(int z = 0; z < inst.numAttributes() - 1; z++){
			//-1 because class attribute can be ignored
			//Give the sequence and the featureList to get the feature freqs on the sequence
			Object obj = GenerateArff.getMatchCount(
					"+1_Index(-1)",sequence,featureDataArrayList.get(z),
					classifierData.getScoringMatrixIndex(),
					classifierData.getCountingStyleIndex(),classifierData.getScoringMatrix());
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
		return classifierOne.distributionForInstance(tempInst);
	}

	public static List<double[]> runType3Classifier(ClassifierData classifierData, 
			List<FastaFormat> fastaList) throws Exception{	

		Classifier classifierOne = classifierData.getClassifierOne();					
		//Reading and Storing the featureList
		Instances inst = classifierData.getInstances();
		ArrayList<Feature> featureDataArrayList = new ArrayList<Feature>();
		for(int x = 0; x < inst.numAttributes() - 1; x++){
			//-1 because class attribute must be ignored
			featureDataArrayList.add(Feature.levelOneClassifierPane(inst.attribute(x).name()));
		}
		//Going through each and every sequence
		List<double[]> predictionList = new ArrayList<double[]>();
		for(int x = 0; x < fastaList.size(); x++){
			//Sequence Score -> index-score, index-score
			String sequence = fastaList.get(x).getSequence();								
			Instance tempInst;
			tempInst = new Instance(inst.numAttributes());
			tempInst.setDataset(inst);     				
			for(int z = 0; z < inst.numAttributes() - 1; z++){
				//-1 because class attribute can be ignored
				//Give the sequence and the featureList to get the feature freqs on the sequence
				Object obj = GenerateArff.getMatchCount(
						"+1_Index(-1)",sequence,featureDataArrayList.get(z),
						classifierData.getScoringMatrixIndex(),
						classifierData.getCountingStyleIndex(),classifierData.getScoringMatrix());
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
			predictionList.add(classifierOne.distributionForInstance(tempInst));
		}
		return predictionList;
	}

	public static void runType3Classifier(ClassifierData classifierData, List<FastaFormat> fastaList,
			String outputDirectory, String fastaFilename, boolean outputACHeader, double threshold,
			Boolean isPos){  
		/*
		 * This method is specially written for GPIAnchor problem
		 * 
		 * This is for type3 classifier
		 * Note that all position and motif list only does not apply to this classifier as
		 * it will only give one score for each sequence
		 */    	
		try{		
			BufferedWriter scoreOutput = new BufferedWriter(
					new FileWriter(outputDirectory + File.separator + "classifierone_" + 
							classifierData.getClassifierName() 
							+ "_" + classifierData.getClassifierType() + "_" + fastaFilename + ".scores"));
			BufferedWriter acOutput = null;
			if(outputACHeader){
				acOutput = new BufferedWriter(new FileWriter(outputDirectory + File.separator + 
				"Sirius_Output.txt"));
			}
			Classifier classifierOne = classifierData.getClassifierOne();					
			//Reading and Storing the featureList
			Instances inst = classifierData.getInstances();
			ArrayList<Feature> featureDataArrayList = new ArrayList<Feature>();
			for(int x = 0; x < inst.numAttributes() - 1; x++){
				//-1 because class attribute must be ignored
				featureDataArrayList.add(Feature.levelOneClassifierPane(inst.attribute(x).name()));
			}     	 			
			//Going through each and every sequence
			for(int x = 0; x < fastaList.size(); x++){
				System.out.println(x + " / " + fastaList.size());
				//Header  				
				scoreOutput.write(fastaList.get(x).getHeader());
				scoreOutput.newLine();
				scoreOutput.write(fastaList.get(x).getSequence());
				scoreOutput.newLine();
				//Sequence Score -> index-score, index-score
				String sequence = fastaList.get(x).getSequence();								
				Instance tempInst;
				tempInst = new Instance(inst.numAttributes());
				tempInst.setDataset(inst);     				
				for(int z = 0; z < inst.numAttributes() - 1; z++){
					//-1 because class attribute can be ignored
					//Give the sequence and the featureList to get the feature freqs on the sequence
					Object obj = GenerateArff.getMatchCount(
							"+1_Index(-1)",sequence,featureDataArrayList.get(z),
							classifierData.getScoringMatrixIndex(),
							classifierData.getCountingStyleIndex(),classifierData.getScoringMatrix());
					if(obj.getClass().getName().equalsIgnoreCase("java.lang.Integer"))
						tempInst.setValue(z,(Integer) obj);
					else if(obj.getClass().getName().equalsIgnoreCase("java.lang.Double"))				
						tempInst.setValue(z,(Double) obj);
					else if(obj.getClass().getName().equalsIgnoreCase("java.lang.String"))
						tempInst.setValue(z, (String) obj);
					else{
						scoreOutput.close();
						throw new Error("Unknown: " + obj.getClass().getName()); 					
					}
				}     	
				//note that pos or neg does not matter as this is not used
				tempInst.setValue(inst.numAttributes() - 1,"pos");
				try{
					double[] results = classifierOne.distributionForInstance(tempInst);
					if(isPos == null){
						scoreOutput.write("0=" + results[0]);
					}else{
						if(isPos)
							scoreOutput.write("pos,0=" + results[0]);
						else
							scoreOutput.write("neg,0=" + results[0]);
					}
					if(acOutput != null && results[0] > threshold){	     				
						acOutput.write(fastaList.get(x).getHeader().split(" ")[0].substring(1));
						acOutput.newLine();
					}
				}
				catch(Exception e){
					e.printStackTrace();
					scoreOutput.close();
					throw new Error();
					//this is to ensure that the run will continue	     		
					//scoreOutput.write("0=-0.0");
					//change throw error to screen output if i want the run to continue
					//System.err.println(
					//	"Exception has Occurred for classifierOne.distributionForInstance(tempInst);");
				}
				scoreOutput.newLine();
				scoreOutput.flush();
			}
			scoreOutput.flush();
			scoreOutput.close();
			if(acOutput != null) acOutput.close();
		}catch(Exception e){
			JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();}    				
	}

	public static ClassifierFeature loadClassifierAsFeature(String name, 
			String classifierLocationFile) throws IOException, ClassNotFoundException{
		FileInputStream fis = new FileInputStream(classifierLocationFile);
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
		//int scoringMatrixIndex = -1;
		//int countingStyleIndex = -1;
		if(classifierNum == 2){        	
			setUpstream = ois.readInt();
			setDownstream = ois.readInt();
			classifierTwoSettings = (String) ois.readObject();
			instances2 = (Instances) ois.readObject();
			classifierTwo = (Classifier) ois.readObject();
			ois.close();
			throw new Error("Error: Not Supposed to be inside here as ClassifierTwo is not supported");
		}		        										
		ois.close();
		return new ClassifierFeature("Z_" + name , 
				(new ClassifierData(classifierNum,classifierName,instances,
						classifierOne,classifierTwo,classifierOneSettings,classifierTwoSettings,setUpstream,
						setDownstream,instances2,sequenceType,scoringMatrixIndex,countingStyleIndex)));        
	}

	public static double[] getFeatureValue(Feature feature, FastaFormat fasta){
		try{
			double[] values = new double[1];		
			Object obj = GenerateArff.getMatchCount(fasta,feature,0,0,null);
			if(obj instanceof Double){
				values[0] = (Double) obj;
			}else if(obj instanceof Integer){
				values[0] = (Integer) obj;
			}else{
				throw new Error("Object is neither Integer nor Double: " + obj);
			}		
			return values;
		}catch(Exception e){e.printStackTrace(); return null;}
	}

	public static double[] getFeatureValue(Feature feature, List<FastaFormat> fastaList){
		try{
			double[] values = new double[fastaList.size()];
			for(int i = 0; i < fastaList.size(); i++){
				Object obj = GenerateArff.getMatchCount(fastaList.get(i),feature,0,0,null);
				if(obj instanceof Double){
					values[i] = (Double) obj;
				}else if(obj instanceof Integer){
					values[i] = (Integer) obj;
				}else{
					throw new Error("Object is neither Integer nor Double: " + obj);
				}
			}
			return values;
		}catch(Exception e){e.printStackTrace(); return null;}
	}

	public static void obtainFeatureValue(List<Feature> featureList, List<FastaFormat> fastaList){
		for(Feature f:featureList){
			f.setValueList(SiriusClassifier.getFeatureValue(f, fastaList));
		}	
	}

	public static File generateArffFromFeature(List<Feature> featureList, double[] classList){
		List<String> featureNameList = new ArrayList<String>();
		List<double[]> featureValueList = new ArrayList<double[]>();
		for(Feature f:featureList){
			featureNameList.add(f.getName());
			featureValueList.add(f.getValueList());
		}
		return Arff.writeToFileAsArff(classList, featureNameList, featureValueList);
	}

	public static void computeMI(List<Feature> featureList, double[] classList){
		for(Feature f:featureList){
			f.setMI(ContinuousMI.MIUsingCellucciMethod(f.getValueList(), classList, true));
		}
	}
	
	public static List<MembraneTypePrediction> predictMembraneTypeIV( 
			Hashtable<String, Classifier> classifierHashtable, 
			int numOfClassifiers, Hashtable<String, List<Feature>> featureHashtable, List<FastaFormat> fastaList, 
			boolean useVoting, boolean predictOnlyOne) throws Exception{
		/*
		 * This method is different from predictMembraneType
		 * Embed vs NonEmbed
		 * If Embed, compare between TypeI, TypeII, TypeIII, TypeIV and MultiPass
		 * If nonEmbed, compare between LipidAnchor, GPIAnchor, Peripheral and NonMembrane
		 * 		 
		 */		
		List<MembraneTypePrediction> predictionList = new ArrayList<MembraneTypePrediction>();
		for(FastaFormat fasta:fastaList){								
			String[] featureStringList = new String[9];
			featureStringList[0] = "TypeIVsAll";
			featureStringList[1] = "TypeIIVsAll";
			featureStringList[2] = "TypeIIIVsAll";
			featureStringList[3] = "TypeIVVsAll";
			featureStringList[4] = "MultiPassVsAll";
			featureStringList[5] = "LipidAnchorVsAll";
			featureStringList[6] = "GPIAnchorVsAll";
			featureStringList[7] = "PeripheralVsAll";
			featureStringList[8] = "NonMembraneVsAll";
			int[] indexList = new int[9];
			indexList[0] = 1;
			indexList[1] = 2;
			indexList[2] = 3;
			indexList[3] = 4;
			indexList[4] = 5;
			indexList[5] = 6;
			indexList[6] = 7;
			indexList[7] = 8;
			indexList[8] = 0;				
			predictionList.add(getGoodScorePrediction(featureStringList, indexList, fasta, classifierHashtable, featureHashtable,
					numOfClassifiers, useVoting, predictOnlyOne));		
		}
		return predictionList;
	}

	public static List<MembraneTypePrediction> predictMembraneTypeII( 
			Hashtable<String, Classifier> classifierHashtable, 
			int numOfClassifiers, Hashtable<String, List<Feature>> featureHashtable, List<FastaFormat> fastaList, 
			boolean useVoting, boolean predictOnlyOne) throws Exception{
		/*
		 * This method is different from predictMembraneType
		 * Embed vs NonEmbed
		 * If Embed, compare between TypeI, TypeII, TypeIII, TypeIV and MultiPass
		 * If nonEmbed, compare between LipidAnchor, GPIAnchor, Peripheral and NonMembrane
		 * 		 
		 */		
		List<MembraneTypePrediction> predictionList = new ArrayList<MembraneTypePrediction>();
		for(FastaFormat fasta:fastaList){	
			/*
			 * Embed Vs NonEmbed
			 */			
			List<Integer> predictionIndexList = new ArrayList<Integer>();
			List<Double> predictionScoreList = new ArrayList<Double>();
			List<List<String>> predictionRuleList = new ArrayList<List<String>>();
			List<List<String>> featureValueList = new ArrayList<List<String>>();
			double score = predictLeftOrRight("EmbedMembraneVsNonEmbed", fasta, classifierHashtable, 
					featureHashtable, numOfClassifiers, useVoting, predictionRuleList, featureValueList);
			if(score > 0.5){
				/*
				 * Embed - Index 9
				 */
				predictionIndexList.add(9);
				predictionScoreList.add(score);				

				String[] featureStringList = new String[5];
				featureStringList[0] = "TypeIVsEmbedMembrane";
				featureStringList[1] = "TypeIIVsEmbedMembrane";
				featureStringList[2] = "TypeIIIVsEmbedMembrane";
				featureStringList[3] = "TypeIVVsEmbedMembrane";
				featureStringList[4] = "MultiPassVsEmbedMembrane";
				int[] indexList = new int[5];
				indexList[0] = 1;
				indexList[1] = 2;
				indexList[2] = 3;
				indexList[3] = 4;
				indexList[4] = 5;
				predictionList.add(getGoodScorePrediction(featureStringList, indexList, fasta, classifierHashtable, featureHashtable,
						numOfClassifiers, useVoting, predictOnlyOne));
			}else{
				/*
				 * NonEmbed - Index 10
				 */
				predictionIndexList.add(9);
				predictionScoreList.add(1 - score);
				String[] featureStringList = new String[4];
				featureStringList[0] = "LipidAnchorVsNonEmbed";
				featureStringList[1] = "GPIAnchorVsNonEmbed";
				featureStringList[2] = "PeripheralVsNonEmbed";
				featureStringList[3] = "NonMembraneVsNonEmbed";				
				int[] indexList = new int[4];
				indexList[0] = 6;
				indexList[1] = 7;
				indexList[2] = 8;
				indexList[3] = 0;

				predictionList.add(getGoodScorePrediction(featureStringList, indexList, fasta, classifierHashtable, featureHashtable,
						numOfClassifiers, useVoting, predictOnlyOne));
			}					
		}
		return predictionList;
	}

	private static MembraneTypePrediction getGoodScorePrediction(String[] featureStringList, int[] indexList, 
			FastaFormat fasta, Hashtable<String, Classifier> classifierHashtable, Hashtable<String, List<Feature>> featureHashtable, 
			int numOfClassifiers, boolean useVoting, boolean predictOnlyOne) throws Exception{
		/*
		 * Will return the top prediction and prediction within 95% of top score if predictOnlyOne is false
		 * else will return top prediction and predictions with score same as top score
		 */
		List<MembraneTypePrediction> membraneTypePredictionList = new ArrayList<MembraneTypePrediction>();
		double maxScore = -1;
		for(int x = 0; x < featureStringList.length; x++){
			List<String> predictionRuleList = new ArrayList<String>();
			List<String> featureValueList = new ArrayList<String>();
			double score = 0.0;
			for(int a = 0; a < numOfClassifiers; a++){
				Classifier c = classifierHashtable.get(featureStringList[x] + "_" + a);
				Instance instance = convert2Instance(featureHashtable.get(featureStringList[x] + "_" + a), fasta);
				double currentScore;
				if(c instanceof J48){
					StringBuffer ruleBuffer = new StringBuffer();
					StringBuffer featureBuffer = new StringBuffer();
//					currentScore = ((J48) c).distributionForInstanceAHFU(instance, ruleBuffer, featureBuffer)[0];
					currentScore = ((J48) c).distributionForInstance(instance)[0];//(instance, ruleBuffer, featureBuffer)[0];
					predictionRuleList.add(ruleBuffer.toString());
					featureValueList.add(featureBuffer.toString());
				}else{
					currentScore = c.distributionForInstance(instance)[0];
				}
				if(useVoting){
					if(currentScore > 0.5){
						score++;
					}
				}else{
					score += currentScore;
				}
			}	
			//System.out.println("score Before: " + score);
			score /= numOfClassifiers;
			if(score > maxScore) maxScore = score;
			membraneTypePredictionList.add(new MembraneTypePrediction(indexList[x], score, predictionRuleList, featureValueList));
		}
		double factor;
		if(predictOnlyOne == false) factor = 0.95;
		else factor = 1.0;			
		/*
		 * Find those with score within 95% or 100% depending on predictOnlyOne of maxScore
		 */		
		if(maxScore > 0.0){
			for(int x = 0; x < membraneTypePredictionList.size();){
				if(membraneTypePredictionList.get(x).getScore() >= factor * maxScore){
					x++;
				}else{
					//remove
					membraneTypePredictionList.remove(x);
				}
			}
			return new MembraneTypePrediction(membraneTypePredictionList);
		}else{
			//throw new Error("No Prediction");
			return new MembraneTypePrediction();//No Prediction - Strange to be here though
		}
	}

	private static double predictLeftOrRight(String featureString, FastaFormat fasta, Hashtable<String, Classifier> classifierHashtable, 
			Hashtable<String, List<Feature>> featureHashtable, int numOfClassifiers, boolean useVoting,
			List<List<String>> predictionRuleListList, List<List<String>> featureValueListList) throws Exception{
		/*
		 * Returns the score for the left class or class index 0
		 */
		List<String> predictionRuleList = new ArrayList<String>();
		List<String> featureValueList = new ArrayList<String>();
		double score = 0.0;
		for(int a = 0; a < numOfClassifiers; a++){
			Classifier c = classifierHashtable.get(featureString + "_" + a);
			Instance instance = convert2Instance(featureHashtable.get(featureString + "_" + a), fasta);
			double currentScore;
			if(c instanceof J48){
				StringBuffer ruleBuffer = new StringBuffer();
				StringBuffer featureBuffer = new StringBuffer();
//				currentScore = ((J48) c).distributionForInstanceAHFU(instance, ruleBuffer, featureBuffer)[0];
				currentScore = ((J48) c).distributionForInstance(instance)[0];
				predictionRuleList.add(ruleBuffer.toString());
				featureValueList.add(featureBuffer.toString());
			}else{
				currentScore = c.distributionForInstance(instance)[0];
			}
			if(useVoting){
				if(currentScore > 0.5){
					score++;
				}
			}else{//Aggregate
				score += currentScore;
			}
		}
		score /= numOfClassifiers;
		predictionRuleListList.add(predictionRuleList);
		featureValueListList.add(featureValueList);
		return score;
	}

	public static List<MembraneTypePrediction> predictMembraneTypeIII( 
			Hashtable<String, Classifier> classifierHashtable, 
			int numOfClassifiers, Hashtable<String, List<Feature>> featureHashtable, List<FastaFormat> fastaList, 
			boolean useVoting) throws Exception{
		/*
		 * This method is different from predictMembraneType
		 * Embed vs NonEmbed
		 * If Embed, MultiPassVsSinglePass
		 * 		If MultiPass, Predict MultiPass
		 * 		If SinglePass, Predict N2C vs C2N
		 * 			If N2C, TypeI vs TypeIII
		 * 				If TypeI, Predict TypeI
		 * 				If TypeIII, Predict TypeIII
		 * 			If C2N, TypeII vs TypeIV
		 * 				If TypeII, Predict TypeII
		 * 				If TypeIV, Predict TypeIV
		 * If NonEmbed, Anchor vs NonAnchor
		 * 		If Anchor, LipidAnchor vs GPIAnchor
		 * 			If LipiAnchor, Predict LipidAnchor
		 * 			If GPIAnchor, Predict GPIAnchor
		 * 		If NonAnchor, Peripheral vs NonMembrane
		 * 			If Peripheral, Predict Peripheral
		 * 			If NonMembrane, Predict NonMembrane
		 * 
		 */		
		List<MembraneTypePrediction> predictionList = new ArrayList<MembraneTypePrediction>();
		for(int x = 0; x < fastaList.size(); x++){	
			/*
			 * Embed Vs NonEmbed
			 */			
			List<Integer> predictionIndexList = new ArrayList<Integer>();
			List<Double> predictionScoreList = new ArrayList<Double>();
			List<List<String>> predictionRuleList = new ArrayList<List<String>>();
			List<List<String>> featureValueList = new ArrayList<List<String>>();
			double score = predictLeftOrRight("EmbedMembraneVsNonEmbed", fastaList.get(x), classifierHashtable, 
					featureHashtable, numOfClassifiers, useVoting, predictionRuleList, featureValueList);
			if(score > 0.5){
				/*
				 * Embed - Index 9
				 * 		MultiPassVsSinglePass
				 */
				predictionIndexList.add(9);
				predictionScoreList.add(score);
				double score2 = predictLeftOrRight("MultiPassVsSinglePass", fastaList.get(x), classifierHashtable, 
						featureHashtable, numOfClassifiers, useVoting, predictionRuleList, featureValueList);
				if(score2 > 0.5){
					/*
					 * MultiPass - Index 5
					 */
					predictionIndexList.add(5);
					predictionScoreList.add(score2);
				}else{
					/*
					 * SinglePass - Index 11
					 * 		N2C vs C2N
					 */
					predictionIndexList.add(11);
					predictionScoreList.add(1 - score2);
					double score3 = predictLeftOrRight("N2CVsC2N", fastaList.get(x), classifierHashtable, 
							featureHashtable, numOfClassifiers, useVoting, predictionRuleList, featureValueList);
					if(score3 > 0.5){
						/*
						 * N2C - Index 12
						 * 		TypeI vs TypeIII
						 */
						predictionIndexList.add(12);
						predictionScoreList.add(score3);
						double score4 = predictLeftOrRight("TypeIVsTypeIII", fastaList.get(x), classifierHashtable, 
								featureHashtable, numOfClassifiers, useVoting, predictionRuleList, featureValueList);
						if(score4 > 0.5){
							//TypeI - Index 1
							predictionIndexList.add(1);
							predictionScoreList.add(score4);
						}else{
							//TypeIII - Index 3
							predictionIndexList.add(3);
							predictionScoreList.add(1 - score4);
						}
					}else{
						/*
						 * C2N - Index 13
						 * 		TypeII vs TypeIV
						 */
						predictionIndexList.add(13);
						predictionScoreList.add(1 - score3);
						double score4 = predictLeftOrRight("TypeIIVsTypeIV", fastaList.get(x), classifierHashtable, 
								featureHashtable, numOfClassifiers, useVoting, predictionRuleList, featureValueList);
						if(score4 > 0.5){
							//TypeII - Index 2
							predictionIndexList.add(2);
							predictionScoreList.add(score4);
						}else{
							//TypeIV - Index 4
							predictionIndexList.add(4);
							predictionScoreList.add(1 - score4);
						}
					}						
				}
			}else{
				/*
				 * NonEmbed - Index 10
				 * 		Anchor vs NonAnchor
				 */
				predictionIndexList.add(10);
				predictionScoreList.add(1 - score);
				double score2 = predictLeftOrRight("AnchorVsNonAnchor", fastaList.get(x), classifierHashtable, 
						featureHashtable, numOfClassifiers, useVoting, predictionRuleList, featureValueList);
				if(score2 > 0.5){
					/*
					 * Anchor - Index 14
					 * 		LipidAnchor vs GPIAnchor
					 */
					predictionIndexList.add(14);
					predictionScoreList.add(score2);
					//String vs = "LipidAnchorVsGPIAnchor";
					String vs = "GPIAnchorVsLipidAnchor";
					double score3 = predictLeftOrRight(vs, fastaList.get(x), classifierHashtable, 
							featureHashtable, numOfClassifiers, useVoting, predictionRuleList, featureValueList);
					if(score3 > 0.5){
						/*
						 * GPIAnchor - Index 7
						 */
						predictionIndexList.add(7);
						predictionScoreList.add(score3);
					}else{
						/*
						 * LipidAnchor - Index 6
						 */
						predictionIndexList.add(6);
						predictionScoreList.add(1 - score3);
					}
				}else{
					/*
					 * NonAnchor - Index 15
					 * 		Peripheral vs NonMembrane
					 */
					predictionIndexList.add(15);
					predictionScoreList.add(1 - score2);
					double score3 = predictLeftOrRight("PeripheralVsNonMembrane", fastaList.get(x), classifierHashtable, 
							featureHashtable, numOfClassifiers, useVoting, predictionRuleList, featureValueList);
					if(score3 > 0.5){
						/*
						 * Peripheral - Index 8
						 */
						predictionIndexList.add(8);
						predictionScoreList.add(score3);
					}else{
						/*
						 * NonMembrane - Index 0
						 */
						predictionIndexList.add(0);
						predictionScoreList.add(1 - score3);
					}
				}
			}
			predictionList.add(new MembraneTypePrediction(predictionIndexList, predictionScoreList, predictionRuleList, featureValueList));
		}
		return predictionList;
	}

	public static List<MembraneTypePrediction> predictMembraneType(String[] memtypeString, Hashtable<String, Classifier> classifierHashtable, 
			int numOfClassifiers, Hashtable<String, List<Feature>> featureHashtable, List<FastaFormat> fastaList, 
			boolean useVoting, double layerOneThreshold, double votingThreshold) throws Exception{
		List<MembraneTypePrediction> predictionList = new ArrayList<MembraneTypePrediction>();
		for(int x = 0; x < fastaList.size(); x++){			
			/*
			 * First Layer - Membrane or NonMembrane
			 */
			double nonMembraneScore = 0.0;
			List<String> nonMembraneRuleList = new ArrayList<String>();
			List<String> nonMembraneFeatureList = new ArrayList<String>();
			for(int a = 0; a < numOfClassifiers; a++){
				Classifier c = classifierHashtable.get(memtypeString[0] + "_" + a);
				Instance instance = convert2Instance(
						featureHashtable.get(memtypeString[0] + "_" + a), fastaList.get(x));
				double currentScore;
				if(c instanceof J48){
					StringBuffer ruleBuffer = new StringBuffer();
					StringBuffer featureBuffer = new StringBuffer();
//					currentScore = ((J48) c).distributionForInstanceAHFU(instance, ruleBuffer, featureBuffer)[0];
					currentScore = ((J48) c).distributionForInstance(instance)[0];
					nonMembraneRuleList.add(ruleBuffer.toString());
					nonMembraneFeatureList.add(featureBuffer.toString());
				}else{
					currentScore = c.distributionForInstance(instance)[0];
				}
				if(useVoting){
					if(currentScore > votingThreshold){
						nonMembraneScore++;
					}
				}else{
					nonMembraneScore += currentScore;
				}
			}
			nonMembraneScore /= numOfClassifiers;
			//Decide whether Membrane or NonMembrane			
			if(nonMembraneScore > layerOneThreshold){
				//index 0 stands for nonMembrane
				predictionList.add(new MembraneTypePrediction(0, nonMembraneScore, nonMembraneRuleList, nonMembraneFeatureList));				
			}else{
				//Predicted to be Membrane hence go to second layer
				/*
				 * Second Layer
				 */
				double[] membraneScoreList = new double[memtypeString.length];
				List<List<String>> ruleListList = new ArrayList<List<String>>();
				List<List<String>> featureListList = new ArrayList<List<String>>();
				ruleListList.add(nonMembraneRuleList);
				featureListList.add(nonMembraneFeatureList);
				double maxScore = -1;
				for(int z = 1; z < memtypeString.length; z++){
					double membraneScore =0.0;
					List<String> ruleList = new ArrayList<String>();
					List<String> featureList = new ArrayList<String>();
					for(int a = 0; a < numOfClassifiers; a++){
						Classifier c = classifierHashtable.get(memtypeString[z] + "_" + a);
						Instance instance = convert2Instance(
								featureHashtable.get(memtypeString[z] + "_" + a), fastaList.get(x));
						double currentScore;
						if(c instanceof J48){
							StringBuffer ruleBuffer = new StringBuffer();
							StringBuffer featureBuffer = new StringBuffer();
//							currentScore = ((J48) c).distributionForInstanceAHFU(instance, ruleBuffer, featureBuffer)[0];
							currentScore = ((J48) c).distributionForInstance(instance)[0];
							ruleList.add(ruleBuffer.toString());
							featureList.add(featureBuffer.toString());
						}else{
							currentScore = c.distributionForInstance(instance)[0];
						}
						if(useVoting){							
							if(currentScore > votingThreshold){
								membraneScore++;
							}
						}else{
							membraneScore += currentScore;
						}
					}
					ruleListList.add(ruleList);
					featureListList.add(featureList);
					if(membraneScore > maxScore){
						maxScore = membraneScore;
					}
					membraneScoreList[z] = membraneScore;
				}	
				/*				 
				 * All indexes equal or close to (95%) of maxScore are predicted to be type
				 */				
				if(maxScore > 0.0){
					List<Integer> pList = new ArrayList<Integer>();
					List<Double> sList = new ArrayList<Double>();
					List<List<String>> rListList = new ArrayList<List<String>>();
					List<List<String>> fListList = new ArrayList<List<String>>();
					for(int z = 1; z < membraneScoreList.length; z++){
						if(membraneScoreList[z] > maxScore * 0.95){
							pList.add(z);
							sList.add(membraneScoreList[z]);
							rListList.add(ruleListList.get(z));
							fListList.add(featureListList.get(z));
						}
					}
					predictionList.add(new MembraneTypePrediction(pList, sList, rListList, fListList));
				}else{
					//If all classifiers score it to be 0.0;
					//Let it be NonMembrane
					if(nonMembraneScore > 0.0)
						predictionList.add(new MembraneTypePrediction(0, nonMembraneScore, ruleListList.get(0), featureListList.get(0)));
					else
						predictionList.add(new MembraneTypePrediction());
				}
			}
		}
		return predictionList;
	}

	public static void main(String[] args){
		double a = 1.0;
		int b = 2;
		a /= b;
		a++;
		System.out.println(a);
	}
}
