package sirius.gpi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sirius.trainer.features.Feature;
import sirius.trainer.features.GenerateArff;
import sirius.utils.Arff;
import sirius.utils.ContinuousMI;
import sirius.utils.FastaFileReader;
import sirius.utils.FastaFormat;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.core.Instances;

public class GPI2 {
	/*
	 * An Improvement over GPI.java
	 * 	 
	 * 1) Run as GPI but for each feature set only
	 * 2) Consolidate results
	 */

	/*
	 * Computes the internal validation results
	 * 
	 * Arguments used during GA	 
	 * 2 or 4 => 2fold or 4fold features (DECIDED on 4 fold features)
	 * 80 or 100 => Subset80 or Subset100 (DECIDED on 100)
	 * 
	 * 1) O or U => Oversampling or undersampling were used during GA (Fold diff = 1)	 
	 * 
	 * Arguments to be use for accuracy estimation
	 * 1) 4 or -1 => Train 4 Fold or Train leave one out
	 * 2) O, U or N => Oversample, Undersample or Neutral
	 * 3) [int] => Which feature set to use (1 to 5 inclusive)
	 * 
	 */
	public static void main(String[] args){
		try{
			if(args.length != 4 && args.length != 5){			
				//Have 5 because needs it for supercomputer
				throw new Error("Arguments is not 4 nor 5!");
			}
			/*
			 * Initialization
			 */
			String fileDirectory = "./Data3/";					
			List<String> posFastaFileList = new ArrayList<String>();		
			List<String> negFastaFileList = new ArrayList<String>();		
			for(int i = 1; i <= 5; i++) posFastaFileList.add(fileDirectory + "Pos" + i + ".fasta");
			for(int i = 1; i <= 5; i++) negFastaFileList.add(fileDirectory + "Neg" + i + ".fasta");

			/*
			 * Obtaining Arguments
			 */
			char GASamplingStyle = args[0].charAt(0);

			int trainingFold = Integer.parseInt(args[1]);
			char trainingSamplingStyle = args[2].charAt(0);
			int trainingFeatureSet = Integer.parseInt(args[3]);		
			/*
			 * Obtain train fasta, test fasta and features
			 */		
			String featureFile = fileDirectory + "maxCFSFeature_100_" + GASamplingStyle +
			"_1_4_" + trainingFeatureSet + ".features";
			List<Feature> featureList = Feature.loadSettings(featureFile);

			List<FastaFormat> trainPosFastaList = new ArrayList<FastaFormat>();
			List<FastaFormat> trainNegFastaList = new ArrayList<FastaFormat>();
			List<FastaFormat> testPosFastaList = new ArrayList<FastaFormat>();
			List<FastaFormat> testNegFastaList = new ArrayList<FastaFormat>();		
			int testIndex;
			switch(trainingFeatureSet){
			case 1: testIndex = 4; break;
			case 2: testIndex = 0; break;
			case 3: testIndex = 1; break;
			case 4: testIndex = 2; break;
			case 5: testIndex = 3; break;
			default: throw new Error("Only handle 5 fold for now!");
			}
			for(int j = 0; j < posFastaFileList.size(); j++){
				if(j == testIndex){
					//Test
					FastaFileReader posR = new FastaFileReader(posFastaFileList.get(j));
					FastaFileReader negR = new FastaFileReader(negFastaFileList.get(j));
					testPosFastaList.addAll(posR.getData());
					testNegFastaList.addAll(negR.getData());
				}else{
					//Train
					FastaFileReader posR = new FastaFileReader(posFastaFileList.get(j));
					FastaFileReader negR = new FastaFileReader(negFastaFileList.get(j));
					trainPosFastaList.addAll(posR.getData());
					trainNegFastaList.addAll(negR.getData());
				}
			}
			/*
			 * Do training and add additional training fasta if neccessary
			 */
			boolean oversample = false;
			boolean undersample = false;
			switch(trainingSamplingStyle){
			case 'O': oversample = true; break;
			case 'N': break;
			case 'U': undersample = true; break;
			default: throw new Error("Unsupported character: " + trainingSamplingStyle);
			}	
			List<List<Double>> predictionListList;
			if(trainingFold == 4){
				predictionListList = runNormal(trainPosFastaList, trainNegFastaList, 
						testPosFastaList, testNegFastaList,featureList, oversample, undersample);
			}else if(trainingFold == -1){
				predictionListList = runJackKnife(trainPosFastaList, trainNegFastaList, 
						testPosFastaList, testNegFastaList,featureList, oversample, undersample);
			}else{
				throw new Error("Unsupported trainingFold: " + trainingFold);
			}
			List<Integer> classList = new ArrayList<Integer>();
			for(int x = 0; x < testPosFastaList.size(); x++) classList.add(0);
			for(int x = 0; x < testNegFastaList.size(); x++) classList.add(1);
			/*
			 * Output the predictions and classes
			 */
			String outputFilelocation = fileDirectory + "Output_" + GASamplingStyle + "_" + 
				trainingFold + "_" + trainingSamplingStyle + "_" + trainingFeatureSet + ".txt";
			output(predictionListList, classList,outputFilelocation);
		}catch(Exception e){e.printStackTrace();}
	}
	
	private static List<List<Double>> runNormal(
			List<FastaFormat> posTrainFastaList, List<FastaFormat> negTrainFastaList,
			List<FastaFormat> posTestFastaList, List<FastaFormat> negTestFastaList, 
			List<Feature> featureList, boolean oversample, boolean undersample) 
			throws Exception{		
		if(oversample && undersample){
			throw new Error("Both Oversample and Undersample are TRUE!");
		}
		if(oversample){
			FastaFormat.oversample(posTrainFastaList, negTrainFastaList, true, 1);
		}else if(undersample){
			FastaFormat.undersample(posTrainFastaList, negTrainFastaList, 1, 0);
		}

		//1) Compute MI
		List<FastaFormat> fullTrainFastaList = new ArrayList<FastaFormat>();
		fullTrainFastaList.addAll(posTrainFastaList);
		fullTrainFastaList.addAll(negTrainFastaList);
		double[] trainClassList = new double[posTrainFastaList.size() + negTrainFastaList.size()];
		for(int i = 0; i < trainClassList.length; i++){
			if(i < posTrainFastaList.size()) trainClassList[i] = 0.0;
			else trainClassList[i] = 1.0;
		}
		for(Feature f:featureList){
			f.setValueList(getFeatureValue(f, fullTrainFastaList));
			f.setMI(ContinuousMI.MIUsingCellucciMethod(f.getValueList(), trainClassList, true));
		}

		//2) Feature Selection
		System.out.println("Original Features: " + featureList.size());
		//List<Feature> firstFilteredFeatures = selectFeatureWithMI(featureList, 0.0);
		//System.out.println("Features that passed first filter: " + firstFilteredFeatures.size());
		List<Feature> dpiFeatures = quickEstimateMBUsingDPI(featureList, 1.15);
		System.out.println("Markov Blanket: " + dpiFeatures.size());

		//3) Train Classifiers
		List<String> featureNameList = new ArrayList<String>();
		List<double[]> featureValueList = new ArrayList<double[]>();
		for(Feature f:dpiFeatures){
			featureNameList.add(f.getName());
			featureValueList.add(f.getValueList());
		}
		List<Classifier> trainedClassifiers = trainClassifiers(featureNameList, featureValueList, 
				trainClassList);

		//4) Test Classifiers Accuracy		
		List<FastaFormat> testFullFastaList = new ArrayList<FastaFormat>();
		testFullFastaList.addAll(posTestFastaList);
		testFullFastaList.addAll(negTestFastaList);
		System.out.println("Train Size: " + fullTrainFastaList.size());
		System.out.println("Test Size: " + testFullFastaList.size());
		System.out.println("First Test: " + testFullFastaList.get(0).getHeader());
		
		List<List<Double>> predictionsListList = new ArrayList<List<Double>>();
		for(int x = 0; x < testFullFastaList.size(); x++){
			boolean isPos;
			if(x < posTestFastaList.size()){
				//test is pos
				isPos = true;
			}else{
				//test is neg
				isPos = false;
			}
			predictionsListList.add(obtainClassifierPredictions(dpiFeatures, 
					testFullFastaList.get(x), trainedClassifiers, isPos));
		}
		return predictionsListList;
	}

	private static List<List<Double>> runJackKnife(List<FastaFormat> trainPosFastaList, 
			List<FastaFormat> trainNegFastaList, List<FastaFormat> testPosFastaList,
			List<FastaFormat> testNegFastaList, List<Feature> featureList, 
			boolean oversample, boolean undersample) throws Exception{
		List<List<Double>> predictionListList = new ArrayList<List<Double>>();
		int totalTestFasta = (testPosFastaList.size() + testNegFastaList.size());
		for(int x = 0; x < totalTestFasta; x++){
			System.out.println(x + " / " + totalTestFasta);
			//0) Add more train sequences
			List<FastaFormat> additionalPosTrainList = new ArrayList<FastaFormat>();
			List<FastaFormat> additionalNegTrainList = new ArrayList<FastaFormat>();
			FastaFormat testFasta = null;
			boolean isPos;
			if(x < testPosFastaList.size()){
				//test is pos
				isPos = true;
				for(int i = 0; i < testPosFastaList.size(); i++)
					if(i != x) additionalPosTrainList.add(testPosFastaList.get(i));
					else testFasta = testPosFastaList.get(i);
				for(FastaFormat fastaFormat:testNegFastaList)
					additionalNegTrainList.add(fastaFormat);
			}else{
				//test is neg
				isPos = false;
				for(int i = 0; i < testNegFastaList.size(); i++){
					if(i != (x - testPosFastaList.size())) 
						additionalNegTrainList.add(testNegFastaList.get(i));
					else testFasta = testNegFastaList.get(i);
				}
				for(FastaFormat fastaFormat:testPosFastaList)
					additionalPosTrainList.add(fastaFormat);
			}
			if(testFasta == null) throw new Error("testFasta is null!");

			List<FastaFormat> fullPosTrainList = new ArrayList<FastaFormat>();
			List<FastaFormat> fullNegTrainList = new ArrayList<FastaFormat>();
			fullPosTrainList.addAll(testPosFastaList);			
			fullPosTrainList.addAll(additionalPosTrainList);
			fullNegTrainList.addAll(testPosFastaList);					
			fullNegTrainList.addAll(additionalNegTrainList);			

			if(oversample){
				FastaFormat.oversample(fullPosTrainList, fullNegTrainList, true, 1);
			}else if(undersample){
				FastaFormat.undersample(fullPosTrainList, fullNegTrainList, 1, 0);
			}

			List<FastaFormat> fullTrainFastaList = new ArrayList<FastaFormat>();
			fullTrainFastaList.addAll(fullPosTrainList);
			fullTrainFastaList.addAll(fullNegTrainList);

			System.out.println("Full Train Sequences: " + fullTrainFastaList.size());
			System.out.println("Test: " + testFasta.getHeader());

			double[] fullTrainClassList = new double[fullPosTrainList.size() + 
			                                         fullNegTrainList.size()];
			for(int i = 0; i < fullTrainClassList.length; i++){
				if(i < fullPosTrainList.size()) fullTrainClassList[i] = 0.0;
				else fullTrainClassList[i] = 1.0;
			}				

			//1) Compute MI
			for(Feature f:featureList){					
				f.setValueList(getFeatureValue(f, fullTrainFastaList));
			}
			for(Feature f:featureList){
				f.setMI(ContinuousMI.MIUsingCellucciMethod(f.getValueList(), 
						fullTrainClassList, true));
			}				

			//2) Feature Selection
			System.out.println("Original Features: " + featureList.size());
			List<Feature> dpiFeatures = quickEstimateMBUsingDPI(featureList, 1.15);
			System.out.println("Markov Blanket: " + dpiFeatures.size());			

			//3) Train Classifiers
			List<String> featureNameList = new ArrayList<String>();
			List<double[]> featureValueList = new ArrayList<double[]>();
			for(Feature f:dpiFeatures){
				featureNameList.add(f.getName());
				featureValueList.add(f.getValueList());
			}
			List<Classifier> trainedClassifiers = trainClassifiers(featureNameList, featureValueList, 
					fullTrainClassList);			

			//4) Obtain classifiers prediction score for 0
			predictionListList.add(obtainClassifierPredictions(dpiFeatures, testFasta, 
					trainedClassifiers, isPos));
		}
		return predictionListList;
	}

	private static List<Double> obtainClassifierPredictions(
			List<Feature> selectedFeaturesList, FastaFormat testFasta, 
			List<Classifier> classifierList, 
			boolean isPos) throws Exception{
		for(Feature f:selectedFeaturesList){
			f.setValueList(getFeatureValue(f, testFasta));
		}		
		double[] testClassList = new double[1];
		if(isPos) testClassList[0] = 0.0;
		else testClassList[0] = 1.0;

		List<double[]> featureValueList = new ArrayList<double[]>();
		List<String> featureNameList = new ArrayList<String>();
		for(Feature f:selectedFeaturesList){
			featureValueList.add(f.getValueList());			
			featureNameList.add(f.getName());
		}		
		File arffFile = Arff.writeToFileAsArff(testClassList, featureNameList, featureValueList);
		Instances testInstances = Arff.getAsInstances(arffFile);
		testInstances.setClassIndex(testInstances.numAttributes() - 1);

		List<Double> predictionsList = new ArrayList<Double>();
		for(int i = 0; i < classifierList.size(); i++){			
			for(int j = 0; j < testInstances.numInstances(); j++){
				predictionsList.add(
						classifierList.get(i).distributionForInstance(testInstances.instance(j))[0]);
			}			
		}		
		if(predictionsList.size() != classifierList.size()){
			throw new Error("there is more than one test instance!");
		}
		/*
		 * Average predictor
		 */
		double d = 0.0;
		for(int i = 0; i < predictionsList.size(); i++){
			d += predictionsList.get(i);
		}
		d /= (predictionsList.size());
		predictionsList.add(d);	
		return predictionsList;
	}

	private static double[] getFeatureValue(Feature feature, List<FastaFormat> fastaList){
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

	private static double[] getFeatureValue(Feature feature, FastaFormat fasta){
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

	private static void output(List<List<Double>> predictionListList, List<Integer> classList,
			String outputFileLocation){
		/*
		 * J48
		 * NB
		 * NN5
		 * SMO
		 * Average
		 */
		try{
			List<Double> j48List = new ArrayList<Double>();
			List<Double> nbList = new ArrayList<Double>();
			List<Double> nn5List = new ArrayList<Double>();
			List<Double> smoList = new ArrayList<Double>();
			List<Double> aveList = new ArrayList<Double>();
			if(predictionListList.get(0).size() != 5) throw new Error("Check classifiers again!");
			for(int x = 0; x < predictionListList.size(); x++){
				j48List.add(predictionListList.get(x).get(0));
				nbList.add(predictionListList.get(x).get(1));
				nn5List.add(predictionListList.get(x).get(2));
				smoList.add(predictionListList.get(x).get(3));
				aveList.add(predictionListList.get(x).get(4));
			}
			BufferedWriter output = new BufferedWriter(new FileWriter(outputFileLocation));
			output.write("Classes: ");
			for(int x = 0; x < classList.size(); x++){
				if(x != 0) output.write(",");
				output.write(classList.get(x) + "");
			}
			output.newLine();
			
			output.write("J48: ");
			for(int x = 0; x < j48List.size(); x++){
				if(x != 0) output.write(",");
				output.write(j48List.get(x) + "");
			}
			output.newLine();
			
			output.write("NB: ");
			for(int x = 0; x < nbList.size(); x++){
				if(x != 0) output.write(",");
				output.write(nbList.get(x) + "");
			}
			output.newLine();
			
			output.write("NN5: ");
			for(int x = 0; x < nn5List.size(); x++){
				if(x != 0) output.write(",");
				output.write(nn5List.get(x) + "");
			}
			output.newLine();
			
			output.write("SMO: ");
			for(int x = 0; x < smoList.size(); x++){
				if(x != 0) output.write(",");
				output.write(smoList.get(x) + "");
			}
			output.newLine();
			
			output.write("Ave: ");
			for(int x = 0; x < aveList.size(); x++){
				if(x != 0) output.write(",");
				output.write(aveList.get(x) + "");
			}
			output.newLine();
			output.close();
		}catch(Exception e){e.printStackTrace();}
	}
	
	private static List<Classifier> trainClassifiers(List<String> featureNameList, 
			List<double[]> featureValueList, double[] classList) throws Exception{		
		File arffFile = Arff.writeToFileAsArff(classList, featureNameList, featureValueList);
		Instances trainInstances = Arff.getAsInstances(arffFile);
		trainInstances.setClassIndex(trainInstances.numAttributes() - 1);
		List<Classifier> classifierList = new ArrayList<Classifier>();
		J48 j48 = new J48();
		NaiveBayes nb = new NaiveBayes();
		IBk nn5 = new IBk(5);
		classifierList.add(j48);
		classifierList.add(nb);
		classifierList.add(nn5);		
		String[] options = {"-M"};
		SMO smo = new SMO();
		smo.setOptions(options);
		classifierList.add(smo);
		for(Classifier c:classifierList){
			c.buildClassifier(trainInstances);
		}
		System.out.println("Completed Training Classifiers");
		return classifierList;
	}

	private static List<Feature> quickEstimateMBUsingDPI(List<Feature> originalList, 
			double epsilon){
		/*
		 * Cannot put this into Commons for two reasons
		 * 
		 * 1) Feature is different in Sirius and Regulus
		 * 2) Because of 1, Sirius will reference Commons and Commons will reference Sirius if this method is to 
		 * be moved to Commons
		 */
		List<Feature> featureList = new ArrayList<Feature>(originalList);
		Collections.sort(featureList, new SortFeatureByMI());
		for(int i = 0; i < featureList.size(); i++){
			double miic = featureList.get(i).getMutualInformation();
			double[] vList = featureList.get(i).getValueList();
			for(int j = i + 1; j < featureList.size();){
				double miij = ContinuousMI.MIUsingCellucciMethod(vList, 
						featureList.get(j).getValueList(), true);
				double mijc = featureList.get(j).getMutualInformation();
				if((mijc * epsilon) < miij && (mijc * epsilon) < miic){
					//i is between j and c - remove j					
					featureList.remove(j);
				}else{
					j++;
				}
			}
		}
		return featureList;
	}
}
