package sirius.gpi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sirius.trainer.features.Feature;
import sirius.trainer.features.GenerateArff;
import sirius.utils.Arff;
import sirius.utils.ContinuousMI;
import sirius.utils.FastaFileReader;
import sirius.utils.FastaFormat;
import sirius.utils.PredictionStats;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
//import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
//import commons.utils.Timer;

public class GPI {
	/*	
	 * Various classifiers
	 * 	J48
	 * 	NB
	 * 	SMO
	 * 	NN5
	 * 	RandomForest - Removed
	 * 
	 * Feature Generation - MaxFeatures
	 * 
	 */

	public static void main(String[] args){
		/*
		 * Arguments
		 * 1) 2 or 4 => 2fold or 4fold features
		 * 2) 80 or 100 => Subset80 or Subset100
		 * 3) O or U => Oversampling or undersampling were used during GA
		 * 4) 1,2 => Sampling Fold Diff 
		 * Note: For undersampling only - at least for now
		 * Note: For 5 and beyond, 0 features would be generated due to MI computation
		 * 
		 * 1) 2, 4 or -1 => Train 2 Fold or Train 4 Fold or Train leave one out
		 * 2) O, U or N => Oversample, Undersample or Neutral
		 * 
		 * Total Possibilities: 2 * 2 * 3 * 3 = 36 - 6
		 */

		if(args.length != 6){
			throw new Error("Arguments is not 6!");
		}		

		String fileDirectory = "./Data2/";
		List<String> posFastaFileList = new ArrayList<String>();		
		List<String> negFastaFileList = new ArrayList<String>();

		for(int i = 1; i <= 5; i++) posFastaFileList.add(fileDirectory + "Pos" + i + ".fasta");
		for(int i = 1; i <= 5; i++) negFastaFileList.add(fileDirectory + "Neg" + i + ".fasta");

		int featureMode = Integer.parseInt(args[0]);
		int subset = Integer.parseInt(args[1]);
		char featureSamplingStyle = args[2].charAt(0);
		int featureResamplingFoldDiff = Integer.parseInt(args[3]);
		
		int trainMode = Integer.parseInt(args[4]);
		char sampleStyle = args[5].charAt(0);
		
		try{
			switch(featureMode){
			case 2: consolidateResult(run2FoldFeatureMode(fileDirectory, posFastaFileList, 
					negFastaFileList, subset, featureSamplingStyle, featureResamplingFoldDiff, 
					trainMode, sampleStyle), fileDirectory, 
					featureMode, subset, featureSamplingStyle, featureResamplingFoldDiff,  
					trainMode, sampleStyle); break;
			case 4: consolidateResult(run4FoldFeatureMode(fileDirectory, posFastaFileList, 
					negFastaFileList, subset, featureSamplingStyle, featureResamplingFoldDiff,
					trainMode, sampleStyle), fileDirectory,
					featureMode, subset, featureSamplingStyle, featureResamplingFoldDiff,  
					trainMode, sampleStyle); break;
			default: throw new Error("Unknown Feature Mode: " + featureMode);
			}
		}catch(Exception e){e.printStackTrace();}

		//2Fold mode
		//1) train 2 fold - oversample, undersample, neutral
		//2) train 4 fold - oversample, undersample, neutral
		//3) jack-knife - oversample, undersample, neutral

		//4Fold mode
		//1) train 4 fold - oversample, undersample, neutral
		//2) jack-knife - oversample, undersample, neutral		
	}

	private static void consolidateResult(List<List<PredictionStats>> predictionListList, 
			String fileDirectory, int featureMode, int subset, char featureSamplingStyle, 
			int featureResamplingFoldDiff, int trainMode, char sampleStyle) 
	throws Exception{
		BufferedWriter output = new BufferedWriter(new FileWriter(fileDirectory + "_" + featureMode + 
				"_" + subset + "_" + featureSamplingStyle + "_" + featureResamplingFoldDiff + "_" + trainMode + 
				"_" + sampleStyle + ".txt"));
		output.write("FeatureMode: " + featureMode); output.newLine();
		output.write("Subset: " + subset); output.newLine();
		output.write("TrainMode: " + trainMode); output.newLine();
		output.write("SampleStyle: " + sampleStyle); output.newLine();
		for(int i = 0; i < predictionListList.get(0).size(); i++){			
			switch(i){
			case 0: output.write("Classifier: J48"); break;//j48
			case 1: output.write("Classifier: NB"); break;//nb
			case 2: output.write("Classifier: NN-5"); break;//nn5
			case 3: output.write("Classifier: SMO"); break;//smo
			//case 4: output.write("Classifier: RandomForest"); break;//randomForest
			case 4: output.write("Classifier: Average"); break;//Average of j48, nb, nn5, smo
			default: throw new Error("Unhandled case");
			}
			output.newLine();
			double[] tp = new double[predictionListList.size()];
			double[] maxtp = new double[predictionListList.size()];
			double[] fp = new double[predictionListList.size()];
			double[] maxfp = new double[predictionListList.size()];
			double[] cov = new double[predictionListList.size()];
			double[] maxcov = new double[predictionListList.size()];
			double[] acc = new double[predictionListList.size()];
			double[] maxacc = new double[predictionListList.size()];
			double[] fpr = new double[predictionListList.size()];
			double[] maxfpr = new double[predictionListList.size()];
			double[] mcc = new double[predictionListList.size()];
			double[] maxmcc = new double[predictionListList.size()];
			for(int j = 0; j < predictionListList.size(); j++){
				PredictionStats pred = predictionListList.get(j).get(i);
				int maxIndex = pred.getMaxMCCIndex();
				int index = pred.thresholdToThresholdIndex(0.5);
				tp[j] = pred.getTP(index);
				maxtp[j] = pred.getTP(maxIndex);
				fp[j] = pred.getFP(index);
				maxfp[j] = pred.getFP(maxIndex);
				cov[j] = pred.getCoverage(index);
				maxcov[j] = pred.getCoverage(maxIndex);
				acc[j] = pred.getAccuracy(index);
				maxacc[j] = pred.getAccuracy(maxIndex);
				fpr[j] = pred.getFPRate(index);
				maxfpr[j] = pred.getFPRate(maxIndex);
				mcc[j] = pred.getMCC(index);
				maxmcc[j] = pred.getMaxMCC();
			}
			write(output, tp, "TP", true);
			write(output, fp, "FP", true);
			write(output, cov, "Cov", false);
			write(output, acc, "Acc", false);
			write(output, fpr, "FP rate", false);			
			write(output, mcc, "MCC", false);

			write(output, maxtp, "max TP", true);
			write(output, maxfp, "max FP", true);
			write(output, maxcov, "max Cov", false);
			write(output, maxacc, "max Acc", false);
			write(output, maxfpr, "max FP rate", false);
			write(output, maxmcc, "max MCC", false);
			output.newLine();
		}
		output.close();
	}

	private static void write(BufferedWriter output, double[] dList, String name, boolean isSum) 
	throws Exception{
		output.write(name + ": ");
		double d = 0.0;
		for(double a:dList){
			d += a;
		}
		if(isSum == false){
			d /= dList.length;			
		}
		output.write(d + "\t - ");
		for(double a:dList){
			output.write(a + ",");
		}
		output.newLine();
	}

	private static List<List<PredictionStats>> run4FoldFeatureMode(String fileDirectory, 
			List<String> posFastaFileList, List<String> negFastaFileList,
			int subset, char featureSamplingStyle, int featureResamplingFoldDiff, 
			int trainMode, char sampleStyle) throws Exception{
		List<String> featureFileList = new ArrayList<String>();		
		for(int i = 1; i <= 5; i++) featureFileList.add(fileDirectory + "maxCFSFeature_" + 
				subset + "_" + featureSamplingStyle + "_" + featureResamplingFoldDiff + "_4_" + i + ".features");

		int testIndex = 4;
		List<List<PredictionStats>> predictionStatsListList = new ArrayList<List<PredictionStats>>();
		for(int i = 0; i < featureFileList.size(); i++){
			System.out.println("========================");
			System.out.println("Feature Index: " + i);
			System.out.println("========================");
			List<Feature> featureList = Feature.loadSettings(featureFileList.get(i));
			List<FastaFormat> trainPosFastaList = new ArrayList<FastaFormat>();
			List<FastaFormat> trainNegFastaList = new ArrayList<FastaFormat>();
			List<FastaFormat> testPosFastaList = new ArrayList<FastaFormat>();
			List<FastaFormat> testNegFastaList = new ArrayList<FastaFormat>();
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
			System.out.println("Train Pos Size: " + trainPosFastaList.size());
			System.out.println("Test Pos Size: " + testPosFastaList.size());
			boolean jackknife;
			if(trainMode == -1){
				jackknife = true;
			}else if(trainMode == 4){
				jackknife = false;
			}else{
				throw new Error("Unhandled Train Mode: " + trainMode);
			}

			switch(sampleStyle){
			case 'O':
				if(jackknife){
					predictionStatsListList.add(
							runJackKnife(trainPosFastaList, trainNegFastaList, testPosFastaList, testNegFastaList, 
									featureList, true, true, false, -1.0, 0));
				}else{
					predictionStatsListList.add(
							runNormal(trainPosFastaList, trainNegFastaList, testPosFastaList, testNegFastaList, 
									featureList, true, true, false, -1.0, 0));
				}
				break;
			case 'U':
				if(jackknife){
					predictionStatsListList.add(
							runJackKnife(trainPosFastaList, trainNegFastaList, testPosFastaList, testNegFastaList, 
									featureList, false, false, true, 2.0, 0));
				}else{
					predictionStatsListList.add(
							runNormal(trainPosFastaList, trainNegFastaList, testPosFastaList, testNegFastaList, 
									featureList, false, false, true, 2.0, 0));
				}
				break;
			case 'N':
				if(jackknife){
					predictionStatsListList.add(
							runJackKnife(trainPosFastaList, trainNegFastaList, testPosFastaList, testNegFastaList, 
									featureList, false, false, false, 2.0, 0));
				}else{
					predictionStatsListList.add(				
							runNormal(trainPosFastaList, trainNegFastaList, testPosFastaList, testNegFastaList, 
									featureList, false, false, false, 2.0, 0));
				}
				break;
			default: throw new Error("Unhandled samplestyle: " + sampleStyle);
			}

			if(testIndex == 4){
				testIndex = 0;
			}else{
				testIndex++;
			}
		}
		return predictionStatsListList;
	}

	private static List<List<PredictionStats>> run2FoldFeatureMode(String fileDirectory,
			List<String> posFastaFileList, List<String> negFastaFileList,
			int subset, char featureSamplingStyle, int featureResamplingFoldDiff, 
			int trainMode, char sampleStyle) throws Exception{

		List<List<PredictionStats>> predictionStatsListList = new ArrayList<List<PredictionStats>>();
		for(int i = 1; i <= 5; i++){
			for(int j = i + 1; j <= 5; j++){	
				/*
				 * i and j decides which feature file to use
				 */
				System.out.println("========================");
				System.out.println("Feature Index: " + i + ", " + j);
				System.out.println("========================");
				String featureFile = fileDirectory + "maxCFSFeature_" + subset + "_" + featureSamplingStyle +
					"_" + featureResamplingFoldDiff + "_2_" + i + "_" + j + ".features";								
				Set<Integer> alreadyUsedTestSet = new HashSet<Integer>();				
				for(int k = 1; k <= 3; k++){//use different fold for testing - 5fold - 2 used for GA hence left 3
					List<Feature> featureList = Feature.loadSettings(featureFile);
					List<FastaFormat> trainPosFastaList = new ArrayList<FastaFormat>();
					List<FastaFormat> trainNegFastaList = new ArrayList<FastaFormat>();
					List<FastaFormat> testPosFastaList = new ArrayList<FastaFormat>();
					List<FastaFormat> testNegFastaList = new ArrayList<FastaFormat>();
					for(int l = 0; l < 5; l++){//run through the fastafiles
						if((l+1) == i || (l+1) == j){
							//used for GA - Train
							FastaFileReader posR = new FastaFileReader(posFastaFileList.get(l));
							FastaFileReader negR = new FastaFileReader(negFastaFileList.get(l));
							trainPosFastaList.addAll(posR.getData());
							trainNegFastaList.addAll(negR.getData());
						}else{
							//can be used for testing or training
							if(k > alreadyUsedTestSet.size() && alreadyUsedTestSet.contains(l) == false){
								//test
								alreadyUsedTestSet.add(l);
								FastaFileReader posR = new FastaFileReader(posFastaFileList.get(l));
								FastaFileReader negR = new FastaFileReader(negFastaFileList.get(l));
								testPosFastaList.addAll(posR.getData());
								testNegFastaList.addAll(negR.getData());
							}else if(trainMode == 4 || trainMode == -1){
								//train
								FastaFileReader posR = new FastaFileReader(posFastaFileList.get(l));
								FastaFileReader negR = new FastaFileReader(negFastaFileList.get(l));
								trainPosFastaList.addAll(posR.getData());
								trainNegFastaList.addAll(negR.getData());
							}
						}
					}					
					boolean jackknife;
					if(trainMode == -1){
						jackknife = true;
					}else if(trainMode == 4 || trainMode == 2){
						jackknife = false;
					}else{
						throw new Error("Unhandled Train Mode: " + trainMode);
					}

					switch(sampleStyle){
					case 'O':
						if(jackknife){
							predictionStatsListList.add(
									runJackKnife(trainPosFastaList, trainNegFastaList, testPosFastaList, testNegFastaList, 
											featureList, true, true, false, -1.0, 0));
						}else{
							predictionStatsListList.add(
									runNormal(trainPosFastaList, trainNegFastaList, testPosFastaList, testNegFastaList, 
											featureList, true, true, false, -1.0, 0));
						}
						break;
					case 'U':
						if(jackknife){
							predictionStatsListList.add(
									runJackKnife(trainPosFastaList, trainNegFastaList, testPosFastaList, 
											testNegFastaList, featureList, false, false, true, 2.0, 0));
						}else{
							predictionStatsListList.add(
									runNormal(trainPosFastaList, trainNegFastaList, testPosFastaList, 
											testNegFastaList, featureList, false, false, true, 2.0, 0));
						}
						break;
					case 'N':
						if(jackknife){
							predictionStatsListList.add(				
									runJackKnife(trainPosFastaList, trainNegFastaList, testPosFastaList, 
											testNegFastaList, featureList, false, false, false, 2.0, 0));
						}else{
							predictionStatsListList.add(				
									runNormal(trainPosFastaList, trainNegFastaList, testPosFastaList, 
											testNegFastaList, featureList, false, false, false, 2.0, 0));
						}
						break;
					default: throw new Error("Unhandled samplestyle: " + sampleStyle);
					}								
				}
			}
		}		
		return predictionStatsListList;
	}

	private static List<PredictionStats> runJackKnife(
			List<FastaFormat> posTrainFastaList, List<FastaFormat> negTrainFastaList,
			List<FastaFormat> posTestFastaList, List<FastaFormat> negTestFastaList, 
			List<Feature> featureList, boolean oversample, boolean oversampleEvenly,
			boolean undersample, double undersampleFoldDiff, int randNumber) 
			throws Exception{		
		if(oversample && undersample){
			throw new Error("Both Oversample and Undersample are TRUE!");
		}		
		List<List<Double>> predictionsListList = new ArrayList<List<Double>>();
		for(int x = 0; x < (posTestFastaList.size() + negTestFastaList.size()); x++){
			System.out.println(x + " / " + (posTestFastaList.size() + negTestFastaList.size()));
			//Timer t = new Timer();
			//0) Add more train sequences			
			List<FastaFormat> additionalPosTrainList = new ArrayList<FastaFormat>();
			List<FastaFormat> additionalNegTrainList = new ArrayList<FastaFormat>();
			FastaFormat testFasta = null;
			boolean isPos;
			if(x < posTestFastaList.size()){
				//test is pos
				isPos = true;
				for(int i = 0; i < posTestFastaList.size(); i++)
					if(i != x) additionalPosTrainList.add(posTestFastaList.get(i));
					else testFasta = posTestFastaList.get(i);
				for(FastaFormat fastaFormat:negTestFastaList)
					additionalNegTrainList.add(fastaFormat);
			}else{
				//test is neg
				isPos = false;
				for(int i = 0; i < negTestFastaList.size(); i++){
					if(i != (x - posTestFastaList.size())) 
						additionalNegTrainList.add(negTestFastaList.get(i));
					else testFasta = negTestFastaList.get(i);
				}
				for(FastaFormat fastaFormat:posTestFastaList)
					additionalPosTrainList.add(fastaFormat);
			}
			if(testFasta == null) throw new Error("testFasta is null!");

			List<FastaFormat> fullPosTrainList = new ArrayList<FastaFormat>();
			List<FastaFormat> fullNegTrainList = new ArrayList<FastaFormat>();
			fullPosTrainList.addAll(posTrainFastaList);
			fullPosTrainList.addAll(additionalPosTrainList);
			fullNegTrainList.addAll(negTrainFastaList);
			fullNegTrainList.addAll(additionalNegTrainList);								

			if(oversample){
				FastaFormat.oversample(fullPosTrainList, fullNegTrainList, oversampleEvenly, 1);
			}else if(undersample){
				FastaFormat.undersample(fullPosTrainList, fullNegTrainList, undersampleFoldDiff, 
						randNumber);
			}

			List<FastaFormat> fullTrainFastaList = new ArrayList<FastaFormat>();
			fullTrainFastaList.addAll(fullPosTrainList);
			fullTrainFastaList.addAll(fullNegTrainList);

			System.out.println("Full Train Sequences: " + fullTrainFastaList.size());
			System.out.println("Test: " + testFasta.getHeader());

			double[] fullTrainClassList = new double[fullPosTrainList.size() + fullNegTrainList.size()];
			for(int i = 0; i < fullTrainClassList.length; i++){
				if(i < fullPosTrainList.size()) fullTrainClassList[i] = 0.0;
				else fullTrainClassList[i] = 1.0;
			}
			//t.showTimeSinceInit("Init");

			//1) Compute MI			
			//Timer ti = new Timer();
			//long max = Long.MIN_VALUE;
			//long min = Long.MAX_VALUE;
			for(Feature f:featureList){
				//ti.startTime();
				f.setValueList(getFeatureValue(f, fullTrainFastaList));
				//ti.stopTime();
				//long l = ti.getTimeTaken();
				//if(l > max) max = l;
				//if(l < min) min = l;
			}
			//System.out.println(min + "\t" + max);
			//t.showTimeSinceInit("Compute Feature value");
			for(Feature f:featureList){
				f.setMI(ContinuousMI.MIUsingCellucciMethod(f.getValueList(), fullTrainClassList, true));
			}
			//t.showTimeSinceInit("ComputeMI");

			//2) Feature Selection
			System.out.println("Original Features: " + featureList.size());
			//List<Feature> firstFilteredFeatures = selectFeatureWithMI(featureList, 0.0);
			//System.out.println("Features that passed first filter: " + firstFilteredFeatures.size());
			List<Feature> dpiFeatures = quickEstimateMBUsingDPI(featureList, 1.15);
			System.out.println("Markov Blanket: " + dpiFeatures.size());
			//t.showTimeSinceInit("FeatureSelection");

			//3) Train Classifiers
			List<String> featureNameList = new ArrayList<String>();
			List<double[]> featureValueList = new ArrayList<double[]>();
			for(Feature f:dpiFeatures){
				featureNameList.add(f.getName());
				featureValueList.add(f.getValueList());
			}
			List<Classifier> trainedClassifiers = trainClassifiers(featureNameList, featureValueList, 
					fullTrainClassList);
			//t.showTimeSinceInit("TrainClassifiers");

			//4) Test Classifiers Accuracy		
			predictionsListList.add(
					obtainClassifierPredictionsForJackKnife(dpiFeatures, testFasta, trainedClassifiers, isPos));
			//t.showTimeSinceInit("Estimate Classifiers Accuracy");
		}
		List<Integer> testClassIList = new ArrayList<Integer>();
		for(int i = 0; i < posTestFastaList.size(); i++) testClassIList.add(0);
		for(int i = 0; i < negTestFastaList.size(); i++) testClassIList.add(1);
		List<PredictionStats> predictionStatsList = new ArrayList<PredictionStats>();
		for(int i = 0; i < predictionsListList.get(0).size(); i++){
			List<Double> predictionList = new ArrayList<Double>();
			for(int j = 0; j < predictionsListList.size(); j++){
				predictionList.add(predictionsListList.get(j).get(i));
			}
			predictionStatsList.add(new PredictionStats(testClassIList, predictionList, 0.5));
		}
		return predictionStatsList;
	}

	private static List<PredictionStats> runNormal(
			List<FastaFormat> posTrainFastaList, List<FastaFormat> negTrainFastaList,
			List<FastaFormat> posTestFastaList, List<FastaFormat> negTestFastaList, 
			List<Feature> featureList, boolean oversample, boolean oversampleEvenly,
			boolean undersample, double undersampleFoldDiff, int randNumber) 
			throws Exception{		
		if(oversample && undersample){
			throw new Error("Both Oversample and Undersample are TRUE!");
		}
		if(oversample){
			FastaFormat.oversample(posTrainFastaList, negTrainFastaList, oversampleEvenly, 1);
		}else if(undersample){
			FastaFormat.undersample(posTrainFastaList, negTrainFastaList, undersampleFoldDiff, 
					randNumber);
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
		return obtainClassifierPredictionStats(dpiFeatures, testFullFastaList, posTestFastaList, 
				negTestFastaList, trainedClassifiers);
	}

	private static List<Double> obtainClassifierPredictionsForJackKnife(
			List<Feature> selectedFeaturesList, FastaFormat testFasta, List<Classifier> classifierList, 
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
				
		double d = 0.0;
		for(int i = 0; i < predictionsList.size(); i++){
			d += predictionsList.get(i);
		}
		d /= (predictionsList.size());
		predictionsList.add(d);	
		return predictionsList;
	}

	private static List<PredictionStats> obtainClassifierPredictionStats(List<Feature> selectedFeaturesList, 
			List<FastaFormat> testFullFastaList, List<FastaFormat> testPosFastaList, 
			List<FastaFormat> testNegFastaList, List<Classifier> classifierList) throws Exception{			
		for(Feature f:selectedFeaturesList){
			f.setValueList(getFeatureValue(f, testFullFastaList));
		}		
		double[] testClassList = new double[testFullFastaList.size()];		
		for(int k = 0; k < testClassList.length; k++){
			if(k < testPosFastaList.size()) testClassList[k] = 0.0;
			else testClassList[k] = 1.0;
		}
		List<double[]> featureValueList = new ArrayList<double[]>();
		List<String> featureNameList = new ArrayList<String>();
		for(Feature f:selectedFeaturesList){
			featureValueList.add(f.getValueList());			
			featureNameList.add(f.getName());
		}		
		File arffFile = Arff.writeToFileAsArff(testClassList, featureNameList, featureValueList);
		Instances testInstances = Arff.getAsInstances(arffFile);
		testInstances.setClassIndex(testInstances.numAttributes() - 1);		
		List<Integer> testClassIList = new ArrayList<Integer>();
		for(int i = 0; i < testPosFastaList.size(); i++) testClassIList.add(0);
		for(int i = 0; i < testNegFastaList.size(); i++) testClassIList.add(1);

		List<PredictionStats> predictionStatsList = new ArrayList<PredictionStats>();
		List<List<Double>> predictionListList = new ArrayList<List<Double>>();
		for(int i = 0; i < classifierList.size(); i++){
			List<Double> predictionsList = new ArrayList<Double>();
			for(int j = 0; j < testInstances.numInstances(); j++){
				predictionsList.add(
						classifierList.get(i).distributionForInstance(testInstances.instance(j))[0]);
			}
			predictionStatsList.add(new PredictionStats(testClassIList, predictionsList, 0.5));
			predictionListList.add(predictionsList);
		}				
		List<Double> averageVotePredictionList = new ArrayList<Double>();
		for(int h = 0; h < predictionListList.get(0).size(); h++){
			double d = 0.0;
			for(int i = 0; i < predictionListList.size(); i++){
				d += predictionListList.get(i).get(h);
			}
			d /= (predictionListList.size());
			averageVotePredictionList.add(d);
		}
		predictionStatsList.add(new PredictionStats(testClassIList, averageVotePredictionList, 0.5));
		return predictionStatsList;
	}
	
	private static List<Feature> quickEstimateMBUsingDPI(List<Feature> originalList, double epsilon){
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

	/*private static List<Feature> selectFeatureWithMI(List<Feature> featureList, double miCutoff){
		List<Feature> selectedFeatureList = new ArrayList<Feature>();
		for(int i = 0; i < featureList.size(); i++){
			if(featureList.get(i).getMutualInformation() > miCutoff){
				selectedFeatureList.add(featureList.get(i));
			}
		}
		return selectedFeatureList;
	}*/

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

	private static List<Classifier> trainClassifiers(List<String> featureNameList, 
			List<double[]> featureValueList, double[] classList) throws Exception{		
		File arffFile = Arff.writeToFileAsArff(classList, featureNameList, featureValueList);
		Instances trainInstances = Arff.getAsInstances(arffFile);
		trainInstances.setClassIndex(trainInstances.numAttributes() - 1);
		List<Classifier> classifierList = new ArrayList<Classifier>();
		J48 j48 = new J48();
		NaiveBayes nb = new NaiveBayes();
		IBk nn5 = new IBk(5);
		//RandomForest rForest = new RandomForest();
		//rForest.setNumTrees(1000);
		//rForest.buildClassifier(trainInstances);
		classifierList.add(j48);
		classifierList.add(nb);
		classifierList.add(nn5);		
		String[] options = {"-M"};
		SMO smo = new SMO();
		smo.setOptions(options);
		classifierList.add(smo);
		//Removed Random Forest because it give stack over flow error
		//classifierList.add(rForest);
		for(Classifier c:classifierList){
			c.buildClassifier(trainInstances);
		}
		System.out.println("Completed Training Classifiers");
		return classifierList;
	}

}


