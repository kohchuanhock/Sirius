package sirius.membranetype;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import sirius.predictor.main.SiriusClassifier;
import sirius.trainer.features.Feature;
import sirius.utils.Arff;
import sirius.utils.FastaFileReader;
import sirius.utils.FastaFormat;
import sirius.utils.Sampling;
import sirius.utils.Utils;
import weka.classifiers.Classifier;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

public class MembraneTypeClassifier {
	/*
	 * 1) Ability to tune the first layer - OK
	 * 2) Output prediction for the GPI proteins - OK
	 * 3) Easy output of all the results for Memtype in a single file - OK
	 * 4) Port over the first two arguments of MemtypeClassifier - OK
	 * 5) Also can reuse many stuff from there - OK	
	 * 
	 * 
	 * 1) Check Performance of using Regulus Features
	 */
	public static void main(String[] args){
		/*
		 * First Layer is Membrane Vs NonMembrane
		 * Second Layer if NonMembrane is OneVsAll for all the membrane proteins
		 */
		try{
			if(args.length != 6){
				System.err.println("1) MaxFoldDiff");
				System.err.println("2) NumOfClassifiers");
				System.err.println("3) 0 (Dun run GPI) or 1 (Run GPI)");
				System.err.println("4) LayerOne Threshold");
				System.err.println("5) Voting Threshold");
				System.err.println("6) 0 (J48), 1 (RF), 2 (SMO)");
				throw new Error("Arguments not equal to 5");
			}
			int maxFoldDiff = Integer.parseInt(args[0]);
			int numOfClassifiers = Integer.parseInt(args[1]);
			int runGPI = Integer.parseInt(args[2]);
			double layerOneThreshold = Double.parseDouble(args[3]);
			double votingThreshold = Double.parseDouble(args[4]);
			int classifierCode = Integer.parseInt(args[5]);
			System.out.println("MaxFoldDiff: " + maxFoldDiff);
			mainMethod(maxFoldDiff, numOfClassifiers, runGPI, layerOneThreshold, votingThreshold, classifierCode);
		}catch(Exception e){e.printStackTrace();}
	}
	
	public static void mainMethod(int maxFoldDiff, int numOfClassifiers, int runGPI, double layerOneThreshold,
			double votingThreshold, int classifierCode) throws Exception{
		/*
		 * Init
		 */
		String[] memtypeString = new String[9];
		memtypeString[0] = "NonMembrane715";
		memtypeString[1] = "TypeI";
		memtypeString[2] = "TypeII";
		memtypeString[3] = "TypeIII";
		memtypeString[4] = "TypeIV";
		memtypeString[5] = "MultiPass";
		memtypeString[6] = "LipidAnchor";
		memtypeString[7] = "GPIAnchor";
		memtypeString[8] = "Peripheral";			
		String gaDir = Utils.selectDirectory("Select the GA dir", "GA: ");
		String dataDir = Utils.selectDirectory("Select the Data dir", "Data: ");
		String outputDir = Utils.selectDirectory("Select Output dir", "Output: ");
		System.out.println("GA Dir: " + gaDir);
		System.out.println("Data Dir: " + dataDir);
		System.out.println("Output Dir: " + outputDir);
		String gpiFileLocation = null;
		String nonGPIFileLocation = null;
		if(runGPI == 1){
			gpiFileLocation = Utils.selectFile("Select GPI File");
			nonGPIFileLocation = Utils.selectFile("Select NonGPI File");
		}
		String classifierOutputDir = outputDir;
		String featureFilename = "maxScoreFeature.features";
		/*
		 * Load Features
		 * Load Train and Test Sequences
		 */
		System.out.println("Load Features, Train Sequences and Test Sequences");
		Hashtable<String, List<FastaFormat>> sequencesHashtable = new Hashtable<String, List<FastaFormat>>();
		Hashtable<String, List<Feature>> featureHashtable = new Hashtable<String, List<Feature>>();
		loadFeaturesAndSequences(memtypeString, dataDir, sequencesHashtable, featureHashtable, gaDir, featureFilename, numOfClassifiers);
		/*
		 * Train Classifiers
		 */
		System.out.println("Train Classifiers");			
		Hashtable<String, Classifier> classifierHashtable = new Hashtable<String, Classifier>();	
		trainClassifiers(memtypeString, classifierHashtable, sequencesHashtable, featureHashtable, 
				maxFoldDiff, classifierOutputDir, numOfClassifiers, classifierCode);
		/*
		 * Run Classifiers
		 */
		System.out.println("Run Test Sequences");		
		predictMemtypeSequences(memtypeString, sequencesHashtable, 
				outputDir, featureHashtable, classifierHashtable, numOfClassifiers, layerOneThreshold, votingThreshold);
		/*
		 * Run Classifiers on GPI/NonGPI if needed
		 */
		if(runGPI == 1){
			List<FastaFormat> gpiList = FastaFileReader.readFastaFile(gpiFileLocation);
			List<FastaFormat> nonGPIList = FastaFileReader.readFastaFile(nonGPIFileLocation);
			predictGPISequences(memtypeString, gpiList, nonGPIList, outputDir, featureHashtable,
					classifierHashtable, numOfClassifiers, layerOneThreshold, votingThreshold);
		}
	}
	
	public static void loadFeaturesAndSequences(String[] memtypeString, String dataDir, 
			Hashtable<String, List<FastaFormat>> sequencesHashtable, Hashtable<String, List<Feature>> featureHashtable,
			String gaDir, String featureString, int numOfClassifiers){
		String sep = File.separator;
		for(int x = 0; x < memtypeString.length; x++){
			System.out.println(x + " / " + memtypeString.length);
			//Load Train Sequences
			String sequenceFile = dataDir + sep + "Train" + sep + memtypeString[x] + ".fasta";
			FastaFileReader reader = new FastaFileReader(sequenceFile);
			sequencesHashtable.put("Train_" + memtypeString[x], reader.getData());
			//Load Test Sequences
			sequenceFile = dataDir + sep + "Test" + sep + memtypeString[x] + ".fasta";
			reader = new FastaFileReader(sequenceFile);
			sequencesHashtable.put("Test_" + memtypeString[x], reader.getData());			
			//Load Features
			for(int y = 0; y < numOfClassifiers; y++){
				String featureFile = gaDir + sep + memtypeString[x] + sep + y + sep + featureString;
				List<Feature> featureList = Feature.loadSettings(featureFile);
				featureHashtable.put(memtypeString[x] + "_" + y, featureList);
			}
		}
	}
	
	public static void trainClassifiers(String[] memtypeString,
			Hashtable<String, Classifier> classifierHashtable, 
			Hashtable<String, List<FastaFormat>> sequencesHashtable,
			Hashtable<String, List<Feature>> featureHashtable, int maxFoldDiff, 
			String classifierOutputDir, int numOfClassifiers, int classifierCode) throws Exception{		
		for(int x = 0; x < memtypeString.length; x++){
			System.out.println(x + " / " + memtypeString.length + " - " + memtypeString[x]);			
			List<FastaFormat> pList = sequencesHashtable.get("Train_" + memtypeString[x]);
			List<FastaFormat> nList = new ArrayList<FastaFormat>();			
			//y start from 1 because 0 is nonMembrane and it should not be used for training layerTwo
			for(int y = 1; y < memtypeString.length; y++){
				if(x == y) continue;
				nList.addAll(sequencesHashtable.get("Train_" + memtypeString[y]));
			}
			/*
			 * Prepare training sequences
			 */
			List<FastaFormat> posFastaList;
			List<FastaFormat> negFastaList;
			if(maxFoldDiff == -1){
				//No need to resample
				posFastaList = pList;
				negFastaList = nList;
			}else if(maxFoldDiff == 0 || maxFoldDiff == 1){
				//Oversample
				posFastaList = new ArrayList<FastaFormat>();
				negFastaList = new ArrayList<FastaFormat>();
				if(maxFoldDiff == 0)
					Sampling.oversample(posFastaList, negFastaList, pList, nList, false);
				else								
					Sampling.oversample(posFastaList, negFastaList, pList, nList, true);
			}else{
				//Undersample
				if(pList.size() >= nList.size()){
					if((pList.size() + 0.0) / nList.size() > maxFoldDiff){
						//Need to resample
						posFastaList = new ArrayList<FastaFormat>();
						negFastaList = new ArrayList<FastaFormat>();
						Sampling.undersample(posFastaList, negFastaList, pList, nList, maxFoldDiff);
					}else{
						//within limits
						posFastaList = pList;
						negFastaList = nList;
					}
				}else{
					if((nList.size() + 0.0) / pList.size() > maxFoldDiff){
						//Need to resample
						posFastaList = new ArrayList<FastaFormat>();
						negFastaList = new ArrayList<FastaFormat>();
						Sampling.undersample(posFastaList, negFastaList, pList, nList, maxFoldDiff);
					}else{
						//within limits
						posFastaList = pList;
						negFastaList = nList;
					}
				}
			}										
			List<FastaFormat> trainFastaList = new ArrayList<FastaFormat>();
			trainFastaList.addAll(posFastaList);
			trainFastaList.addAll(negFastaList);
			double[] classList = new double[trainFastaList.size()];
			for(int i = 0; i < trainFastaList.size(); i++){
				if(i < posFastaList.size()) classList[i] = 0.0;
				else classList[i] = 1.0;
			}
			
			for(int a = 0; a < numOfClassifiers; a++){
				List<String> featureNameList = new ArrayList<String>();
				List<double[]> featureValueList = new ArrayList<double[]>();
				List<Feature> featureList = featureHashtable.get(memtypeString[x] + "_" + a);
				for(int i = 0; i < featureList.size(); i++){
					featureNameList.add(featureList.get(i).getName());
					if(featureList.get(i).getValueList() != null){
						featureValueList.add(featureList.get(i).getValueList());
					}else{
						featureValueList.add(SiriusClassifier.getFeatureValue(
								featureList.get(i), trainFastaList));
					}
				}
				File arffFile = Arff.writeToFileAsArff(classList, featureNameList, featureValueList);
				Instances trainInstances = Arff.getAsInstances(arffFile);
				trainInstances.setClassIndex(trainInstances.numAttributes() - 1);
				Classifier c = null;
				String classifierNameSuffix = null;
				switch(classifierCode){
				case 0://J48
					classifierNameSuffix = "J48";
					J48 j48 = new J48();
					j48.setUseLaplace(true);
					c = j48;
					break;
				case 1://RF
					classifierNameSuffix = "RandomForest";
					RandomForest rf = new RandomForest();//default is 10 trees					
					c = rf;
					break;
				case 2://SMO
					classifierNameSuffix = "SMO";
					SMO smo = new SMO();
					smo.setBuildLogisticModels(true);
					c = smo;
					break;
				default: throw new Error("Unknown Classifier Code: " + classifierCode);
				}							
				c.buildClassifier(trainInstances);
				classifierHashtable.put(memtypeString[x] + "_" + a, c);
				/*
				 * Save Classifiers		 
				 */				
				SiriusClassifier.saveClassifierOne(
						classifierOutputDir + memtypeString[x] + "_" +  a + "_" + classifierNameSuffix + ".classifierone", 
						true, 
						memtypeString[x] + "_" + a +  "_" + classifierNameSuffix, 
						"Protein", 0, 0, c, trainInstances);	
			}
		}		
	}

	public static void predictMemtypeSequences(String[] memtypeString, Hashtable<String, List<FastaFormat>> sequencesHashtable, 
			String outputDir, Hashtable<String, List<Feature>> featureHashtable,
			Hashtable<String, Classifier> classifierHashtable, int numOfClassifiers, double layerOneThreshold, 
			double votingThreshold) throws Exception{
		/*
		 * Used by MembraneTypeClassifier
		 */
		System.out.println("Running Memtype..");
		int[] votingCorrect = new int[memtypeString.length];
		int[] aggregateCorrect = new int[memtypeString.length];
		for(int a = 0; a < memtypeString.length; a++){
			System.out.println(a + " / " + memtypeString.length);
			BufferedWriter aggregateOutput = new BufferedWriter(new FileWriter(outputDir + "Aggregate_" + memtypeString[a] + ".scores"));
			BufferedWriter aggregateFullOutput = new BufferedWriter(new FileWriter(outputDir + "Full_Aggregate_" + memtypeString[a] + ".txt"));
			BufferedWriter votingOutput = new BufferedWriter(new FileWriter(outputDir + "Voting_" + memtypeString[a] + ".scores"));
			BufferedWriter votingFullOutput = new BufferedWriter(new FileWriter(outputDir + "Full_Voting_" + memtypeString[a] + ".txt"));
			List<FastaFormat> fastaList = sequencesHashtable.get("Test_" + memtypeString[a]);
			
			List<MembraneTypePrediction> votingPredictionList = SiriusClassifier.predictMembraneType(memtypeString, 
					classifierHashtable, numOfClassifiers, 
					featureHashtable, fastaList, true, layerOneThreshold, votingThreshold);		
			for(int x = 0; x < fastaList.size(); x++){				
				votingOutput.write(fastaList.get(x).getHeader()); votingOutput.newLine();
				votingOutput.write(fastaList.get(x).getSequence()); votingOutput.newLine();
				if(votingPredictionList.get(x).containsIndex(a)){
					votingCorrect[a]++;
					votingOutput.write("pos,0=" + votingPredictionList.get(x).index2Score(a)); votingOutput.newLine();					
				}else{
					votingOutput.write("pos,0=0.0"); votingOutput.newLine();
				}
				votingFullOutput.write(votingPredictionList.get(x).toString());				
			}					
			
			List<MembraneTypePrediction> aggregatePredictionList = SiriusClassifier.predictMembraneType(memtypeString, 
					classifierHashtable, numOfClassifiers, 
					featureHashtable, fastaList, false, layerOneThreshold, votingThreshold);			
			for(int x = 0; x < fastaList.size(); x++){				
				aggregateOutput.write(fastaList.get(x).getHeader()); aggregateOutput.newLine();
				aggregateOutput.write(fastaList.get(x).getSequence()); aggregateOutput.newLine();
				if(aggregatePredictionList.get(x).containsIndex(a)){
					aggregateCorrect[a]++;
					aggregateOutput.write("pos,0=" + aggregatePredictionList.get(x).index2Score(a)); aggregateOutput.newLine();
				}else{
					aggregateOutput.write("pos,0=0.0"); aggregateOutput.newLine();
				}
				aggregateFullOutput.write(aggregatePredictionList.get(x).toString());				
			}			
			aggregateOutput.close();
			aggregateFullOutput.close();
			votingOutput.close();			
			votingFullOutput.close();
		}
		BufferedWriter output = new BufferedWriter(new FileWriter(outputDir + "Consolidate.txt"));
		output.write("Voting"); output.newLine();
		for(int i = 1; i < votingCorrect.length; i++){
			output.write(votingCorrect[i] + ""); output.newLine();
		}
		output.write(votingCorrect[0] + ""); output.newLine();
		
		output.write("Aggregate"); output.newLine();
		for(int i = 1; i < aggregateCorrect.length; i++){
			output.write(aggregateCorrect[i] + ""); output.newLine();
		}
		output.write(aggregateCorrect[0] + ""); output.newLine();
		output.close();
	}
	
	public static void predictGPISequences(String[] memtypeString, List<FastaFormat> gpiList, 
			List<FastaFormat> nonGPIList, String outputDir, Hashtable<String, 
			List<Feature>> featureHashtable,
			Hashtable<String, Classifier> classifierHashtable, int numOfClassifiers, double layerOneThreshold, 
			double votingThreshold) throws Exception{
		System.out.println("Running GPI..");
		String aFilename = outputDir + "Aggregate_GPIvsNonGPI.scores";
		String vFilename = outputDir + "Voting_GPIvsNonGPI.scores";		
		BufferedWriter aggregateOutput = new BufferedWriter(new FileWriter(aFilename));
		BufferedWriter votingOutput = new BufferedWriter(new FileWriter(vFilename));
		
		int gpiIndex = 7;//GPI's index is 7 in memtypeString
		List<MembraneTypePrediction> votingGPIPredictionList = SiriusClassifier.predictMembraneType(memtypeString, classifierHashtable, numOfClassifiers, 
				featureHashtable, gpiList, true, layerOneThreshold, votingThreshold);
		for(int x = 0; x < gpiList.size(); x++){
			votingOutput.write(gpiList.get(x).getHeader()); votingOutput.newLine();
			votingOutput.write(gpiList.get(x).getSequence()); votingOutput.newLine();
			if(votingGPIPredictionList.get(x).containsIndex(gpiIndex)){
				votingOutput.write("pos,0=" + votingGPIPredictionList.get(x).index2Score(gpiIndex)); votingOutput.newLine();
			}else{
				votingOutput.write("pos,0=0.0"); votingOutput.newLine();
			}
		}
		List<MembraneTypePrediction> votingNonGPIPredictionList = SiriusClassifier.predictMembraneType(memtypeString, classifierHashtable, numOfClassifiers, 
				featureHashtable, nonGPIList, true, layerOneThreshold, votingThreshold);		
		for(int x = 0; x < nonGPIList.size(); x++){
			votingOutput.write(nonGPIList.get(x).getHeader()); votingOutput.newLine();
			votingOutput.write(nonGPIList.get(x).getSequence()); votingOutput.newLine();
			if(votingNonGPIPredictionList.get(x).containsIndex(gpiIndex)){				
				votingOutput.write("neg,0=0.0"); votingOutput.newLine();
			}else{
				votingOutput.write("neg,0=" + votingNonGPIPredictionList.get(x).index2Score(gpiIndex)); votingOutput.newLine();				
			}
		}
		
		List<MembraneTypePrediction> aggregateGPIPredictionList = SiriusClassifier.predictMembraneType(memtypeString, classifierHashtable, numOfClassifiers, 
				featureHashtable, gpiList, false, layerOneThreshold, votingThreshold);
		for(int x = 0; x < gpiList.size(); x++){
			aggregateOutput.write(gpiList.get(x).getHeader()); aggregateOutput.newLine();
			aggregateOutput.write(gpiList.get(x).getSequence()); aggregateOutput.newLine();
			if(aggregateGPIPredictionList.get(x).containsIndex(gpiIndex)){
				aggregateOutput.write("pos,0=" + aggregateGPIPredictionList.get(x).index2Score(gpiIndex)); aggregateOutput.newLine();
			}else{
				aggregateOutput.write("pos,0=0.0"); aggregateOutput.newLine();
			}
		}
		
		List<MembraneTypePrediction> aggregateNonGPIPredictionList = SiriusClassifier.predictMembraneType(memtypeString, classifierHashtable, numOfClassifiers, 
				featureHashtable, nonGPIList, false, layerOneThreshold, votingThreshold);
		for(int x = 0; x < nonGPIList.size(); x++){
			aggregateOutput.write(nonGPIList.get(x).getHeader()); aggregateOutput.newLine();
			aggregateOutput.write(nonGPIList.get(x).getSequence()); aggregateOutput.newLine();
			if(aggregateNonGPIPredictionList.get(x).containsIndex(gpiIndex)){				
				aggregateOutput.write("neg,0=0.0"); aggregateOutput.newLine();
			}else{
				aggregateOutput.write("neg,0=" + aggregateNonGPIPredictionList.get(x).index2Score(gpiIndex)); aggregateOutput.newLine();
			}
		}
		
		aggregateOutput.close();
		votingOutput.close();	
	}
}