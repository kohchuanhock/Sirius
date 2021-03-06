package sirius.gpi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sirius.predictor.main.SiriusClassifier;
import sirius.trainer.features.Feature;
import sirius.utils.Arff;
import sirius.utils.ContinuousMI;
import sirius.utils.FastaFileReader;
import sirius.utils.FastaFormat;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.MultiBoostAB;
import weka.classifiers.trees.ADTree;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

public class IndependentGPIClassifier {		
	public static void main(String[] args){
		try{
			/*
			 * Computes the external validation results
			 * 
			 * Arguments used during GA	 
			 * 2 or 4 => 2fold or 4fold features (DECIDED on 4 fold features)
			 * 80 or 100 => Subset80 or Subset100 (DECIDED on 100)
			 * 
			 * 1) O or U => Oversampling or undersampling were used during GA (Fold diff = 1)	 
			 * 
			 * Arguments to be use for accuracy estimation		 
			 * 1) O, U or N => Oversample, Undersample or Neutral
			 * 
			 * Arguments to select the test.fasta
			 * 1) Name of posTest.fasta
			 * 2) Name of negTest.fasta
			 * 3) OutputFilename Prefix
			 */
			if(args.length != 5){						
				throw new Error("Arguments is not 5!");
			}
			/*
			 * Initialization
			 */
			String fileDirectory = "./IndependentTestData/";
			List<String> posFastaFileList = new ArrayList<String>();		
			List<String> negFastaFileList = new ArrayList<String>();		
			for(int i = 1; i <= 5; i++) posFastaFileList.add(fileDirectory + "Pos" + i + ".fasta");
			for(int i = 1; i <= 5; i++) negFastaFileList.add(fileDirectory + "Neg" + i + ".fasta");

			/*
			 * Obtaining Arguments
			 */
			System.out.println("Obtaining Arguments..");
			char GASamplingStyle = args[0].charAt(0);		
			char trainingSamplingStyle = args[1].charAt(0);
			String posTestFilename = args[2];
			String negTestFilename = args[3];
			String outputFilenamePrefix = args[4].trim();
			/*
			 * Obtain training sequences
			 */
			System.out.println("Preparing training sequences..");
			List<FastaFormat> trainPosFastaList = new ArrayList<FastaFormat>();
			List<FastaFormat> trainNegFastaList = new ArrayList<FastaFormat>();
			for(int j = 0; j < posFastaFileList.size(); j++){			
				//Use all sequences as training sequence
				FastaFileReader posR = new FastaFileReader(posFastaFileList.get(j));
				FastaFileReader negR = new FastaFileReader(negFastaFileList.get(j));
				trainPosFastaList.addAll(posR.getData());
				trainNegFastaList.addAll(negR.getData());
			}
			System.out.println("Over or undersampling if needed..");
			switch(trainingSamplingStyle){
			case 'O': FastaFormat.oversample(trainPosFastaList, trainNegFastaList, true, 1); break;
			case 'N': break;
			case 'U': FastaFormat.undersample(trainPosFastaList, trainNegFastaList, 1, 0); break;
			default: throw new Error("Unsupported trainingSamplingStyle: " + trainingSamplingStyle);
			}
			List<FastaFormat> fullTrainFastaList = new ArrayList<FastaFormat>();
			fullTrainFastaList.addAll(trainPosFastaList);
			fullTrainFastaList.addAll(trainNegFastaList);
			double[] trainClassList = new double[trainPosFastaList.size() + 
			                                     trainNegFastaList.size()];
			for(int i = 0; i < trainClassList.length; i++){
				if(i < trainPosFastaList.size()) trainClassList[i] = 0.0;
				else trainClassList[i] = 1.0;
			}
			/*
			 * Obtain features and train classifiers
			 */
			System.out.println("Obtain features and train classifiers (Voted and Average)..");
			List<List<Classifier>> trainedClassifierListList = new ArrayList<List<Classifier>>();
			List<List<Feature>> dpiFeaturesListList = new ArrayList<List<Feature>>();
			for(int x = 1; x <= 5; x++){
				String featureFile = fileDirectory + "maxCFSFeature_100_" + GASamplingStyle +
				"_1_4_" + x + ".features";
				List<Feature> featureList = Feature.loadSettings(featureFile);		
				for(Feature f:featureList){
					f.setValueList(SiriusClassifier.getFeatureValue(f, fullTrainFastaList));
					f.setMI(ContinuousMI.MIUsingCellucciMethod(f.getValueList(), trainClassList, 
							true));
				}
				//1) Feature selection
				List<Feature> dpiFeaturesList = quickEstimateMBUsingDPI(featureList, 1.15);
				dpiFeaturesListList.add(dpiFeaturesList);
				//2) Train Classifiers
				List<String> featureNameList = new ArrayList<String>();
				List<double[]> featureValueList = new ArrayList<double[]>();
				for(Feature f:dpiFeaturesList){
					featureNameList.add(f.getName());
					featureValueList.add(f.getValueList());
				}
				List<Classifier> trainedClassifiers = 
					trainClassifiers(featureNameList, featureValueList, trainClassList);
				trainedClassifierListList.add(trainedClassifiers);
			}
			/*
			 * Train full classifier
			 */
			System.out.println("Training single classifier..");
			List<Feature> fullFeatureList = new ArrayList<Feature>();
			int count = 1;
			for(List<Feature> fList:dpiFeaturesListList){				
				for(Feature f:fList){
					f.setName(f.getName() + "_" + count);
					count++;
					fullFeatureList.add(f);
				}
			}
			List<Feature> fullDPIFeaturesList = quickEstimateMBUsingDPI(fullFeatureList, 1.15);
			List<String> featureNameList = new ArrayList<String>();
			List<double[]> featureValueList = new ArrayList<double[]>();		
			for(Feature f:fullDPIFeaturesList){
				featureNameList.add(f.getName());				
				featureValueList.add(f.getValueList());
			}
			List<Classifier> trainedFullClassifiers = 
				trainClassifiers(featureNameList, featureValueList, trainClassList);
			/*			 
			 * Obtain Predictions of Single, Voted and Average
			 *
			 * Single => Combine all features and all training sequences and build a single classifier
			 * Voted => Use each feature set and all training sequences to build multiple classifiers
			 * 		and final decision is based on votes
			 * Average => Use each feature set and all training sequences to build multiple classifiers
			 * 		and final decision is based on weights
			 * 
			 * Output Predictions in a way for Sirius to read
			 * 
			 * outputFilename => inputFilename_classifierName_(single or voted or average) 
			 * //first line is the fasta header
			 * //second line is the sequence
			 * //third line is the "class","position1"="score1","position2"="score2"..
			 */
			FastaFileReader posTestFastaFile = new FastaFileReader(fileDirectory + posTestFilename);
			FastaFileReader negTestFastaFile = new FastaFileReader(fileDirectory + negTestFilename);
			List<FastaFormat> posTestList = posTestFastaFile.getData();
			List<FastaFormat> negTestList = negTestFastaFile.getData();
			List<FastaFormat> fullTestList = new ArrayList<FastaFormat>();
			fullTestList.addAll(posTestList);
			fullTestList.addAll(negTestList);
			List<BufferedWriter> outputList = initOutput(fileDirectory, outputFilenamePrefix);
			System.out.println("Obtaining predictions..");
			for(int i = 0; i < fullTestList.size(); i++){
				if(i % fullTestList.size() / 100 == 0){
					System.out.println(i + " / " + fullTestList.size());
				}
				FastaFormat f = fullTestList.get(i);
				boolean isPos = false;
				if(i < posTestList.size()) isPos = true; 
				/*
				 * Single Classifiers' Predictions
				 */
				List<Double> fullClassifierPredictionList = 
					obtainClassifierPredictions(fullDPIFeaturesList, f, trainedFullClassifiers, isPos);
								
				//Keeps a voting for class 0
				List<Double> votingList = new ArrayList<Double>();
				//Keeps the total value for class 0
				List<Double> totalWeightList = new ArrayList<Double>();
				//+1 for the average classifier
				for(int x = 0; x < trainedClassifierListList.get(0).size() + 1; x++){
					votingList.add(0.0);
					totalWeightList.add(0.0);
				}
				for(int x = 0; x < dpiFeaturesListList.size(); x++){									
					List<Feature> selectedFeaturesList = dpiFeaturesListList.get(x);
					List<Classifier> classifierList = trainedClassifierListList.get(x);
					/*
					 * Sub classifiers' predictions
					 */
					List<Double> predictionList = obtainClassifierPredictions(selectedFeaturesList,
							f, classifierList, isPos);
					/*
					 * Update votingList and totalWeightList
					 */
					for(int y = 0; y < predictionList.size(); y++){
						if(predictionList.get(y) >= 0.5){
							votingList.set(y, votingList.get(y) + 1.0);
						}
						totalWeightList.set(y, totalWeightList.get(y) + predictionList.get(y));
					}
				}
				/*
				 * Finalize votingList and totalWeightList
				 */
				for(int x = 0; x < trainedClassifierListList.get(0).size(); x++){
					votingList.set(x, votingList.get(x) / dpiFeaturesListList.size());
					totalWeightList.set(x, totalWeightList.get(x) / dpiFeaturesListList.size());
				}
				/*
				 * Output the predictions
				 */
				for(int x = 0; x < outputList.size();){
					for(int y = 0; y < fullClassifierPredictionList.size(); y++){
						write(outputList.get(x), f, fullClassifierPredictionList.get(y), isPos);
						x++;
						write(outputList.get(x), f, votingList.get(y), isPos);
						x++;
						write(outputList.get(x), f, totalWeightList.get(y), isPos);
						x++;
					}
				}
			}
			/*
			 * Close all filewriter
			 */
			System.out.println("Ending..");
			for(BufferedWriter output:outputList) output.close();
			System.out.println("Done..");
		}catch(Exception e){e.printStackTrace();}
	}
	
	private static void write(BufferedWriter output, FastaFormat f, double prediction, boolean isPos)
		throws Exception{
		output.write(f.getHeader()); output.newLine();
		output.write(f.getSequence()); output.newLine();
		if(isPos) output.write("0");
		else output.write("1");
		output.write(",0=" + prediction);
	}
	
	private static List<BufferedWriter> initOutput(String fileDirectory, String outputFilenamePrefix)
		throws Exception{
		List<BufferedWriter> outputList = new ArrayList<BufferedWriter>();
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_J48_Single.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_J48_Voted.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_J48_Average.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_NB_Single.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_NB_Voted.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_NB_Average.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_NN5_Single.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_NN5_Voted.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_NN5_Average.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_SMO_Single.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_SMO_Voted.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_SMO_Average.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_rForest_Single.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_rForest_Voted.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_rForest_Average.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_rForest100_Single.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_rForest100_Voted.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_rForest100_Average.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_rForest1000_Single.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_rForest1000_Voted.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_rForest1000_Average.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_adTree_Single.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_adTree_Voted.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_adTree_Average.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_adTree100_Single.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_adTree100_Voted.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_adTree100_Average.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_adTree1000_Single.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_adTree1000_Voted.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_adTree1000_Average.score")));		
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_ada_Single.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_ada_Voted.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_ada_Average.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_ada100_Single.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_ada100_Voted.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_ada100_Average.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_ada1000_Single.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_ada1000_Voted.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_ada1000_Average.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_bag_Single.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_bag_Voted.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_bag_Average.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_bag100_Single.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_bag100_Voted.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_bag100_Average.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_bag1000_Single.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_bag1000_Voted.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_bag1000_Average.score")));		
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_multi_Single.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_multi_Voted.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_multi_Average.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_multi100_Single.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_multi100_Voted.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_multi100_Average.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_multi1000_Single.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_multi1000_Voted.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_multi1000_Average.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_Ave_Single.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_Ave_Voted.score")));
		outputList.add(new BufferedWriter(new FileWriter(fileDirectory + 
				outputFilenamePrefix + "_Ave_Average.score")));
		return outputList;
	}
	
	private static List<Double> obtainClassifierPredictions(
			List<Feature> selectedFeaturesList, FastaFormat testFasta, 
			List<Classifier> classifierList, 
			boolean isPos) throws Exception{
		/*
		 * Obtain the test fasta values for the features
		 */
		for(Feature f:selectedFeaturesList){
			f.setValueList(SiriusClassifier.getFeatureValue(f, testFasta));
		}		
		/*
		 * Set test fasta class
		 */
		double[] testClassList = new double[1];
		if(isPos) testClassList[0] = 0.0;
		else testClassList[0] = 1.0;
		/*
		 * Prepare to write the features' value to arff
		 */
		List<double[]> featureValueList = new ArrayList<double[]>();
		List<String> featureNameList = new ArrayList<String>();
		for(Feature f:selectedFeaturesList){
			featureValueList.add(f.getValueList());	
			featureNameList.add(f.getName());
		}
		/*
		 * Writing to Arff
		 */
		File arffFile = Arff.writeToFileAsArff(testClassList, featureNameList, featureValueList);
		Instances testInstances = Arff.getAsInstances(arffFile);
		testInstances.setClassIndex(testInstances.numAttributes() - 1);
		/*
		 * Retrieve the predictions of each classifier
		 */
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
		 * Add the prediction of an average classifier
		 */
		double d = 0.0;
		for(int i = 0; i < predictionsList.size(); i++){
			d += predictionsList.get(i);
		}
		d /= (predictionsList.size());
		predictionsList.add(d);	
		return predictionsList;
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
		RandomForest rForest = new RandomForest();		
		RandomForest rForest100 = new RandomForest();
		rForest100.setNumTrees(100);		
		RandomForest rForest1000 = new RandomForest();
		rForest1000.setNumTrees(1000);		
		classifierList.add(rForest);
		classifierList.add(rForest100);
		classifierList.add(rForest1000);
		ADTree adTree = new ADTree();
		ADTree adTree100 = new ADTree();
		adTree100.setNumOfBoostingIterations(100);
		ADTree adTree1000 = new ADTree();
		adTree1000.setNumOfBoostingIterations(1000);		
		classifierList.add(adTree);
		classifierList.add(adTree100);
		classifierList.add(adTree1000);		
		
		
		AdaBoostM1 ada = new AdaBoostM1();
		ada.setClassifier(new J48());
		AdaBoostM1 ada100 = new AdaBoostM1();
		ada100.setClassifier(new J48());
		ada100.setNumIterations(100);
		AdaBoostM1 ada1000 = new AdaBoostM1();
		ada1000.setClassifier(new J48());
		ada1000.setNumIterations(1000);
		
		Bagging bag = new Bagging();
		bag.setClassifier(new J48());
		Bagging bag100 = new Bagging();
		bag100.setClassifier(new J48());
		bag100.setNumIterations(100);
		Bagging bag1000 = new Bagging();
		bag1000.setClassifier(new J48());
		bag1000.setNumIterations(1000);
		
		classifierList.add(ada);
		classifierList.add(ada100);
		classifierList.add(ada1000);
		classifierList.add(bag);
		classifierList.add(bag100);
		classifierList.add(bag1000);
				
		MultiBoostAB multi = new MultiBoostAB();
		multi.setClassifier(new J48());
		MultiBoostAB multi100 = new MultiBoostAB();
		multi100.setClassifier(new J48());
		multi100.setNumIterations(100);
		MultiBoostAB multi1000 = new MultiBoostAB();
		multi1000.setClassifier(new J48());
		multi1000.setNumIterations(1000);
				
		classifierList.add(multi);
		classifierList.add(multi100);
		classifierList.add(multi1000);
		
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
		 * 
		 * Hence to solve this problem, have to move Sirius Feature class to Commons first
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
