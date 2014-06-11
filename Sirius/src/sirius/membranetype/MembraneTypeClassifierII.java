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

public class MembraneTypeClassifierII {
	/*
	 * This class only have two layer
	 * First Layer is Embed vs NonEmbed
	 * Second Layer is TypeI, TypeII, TypeIII, TypeIV, MultiPass for Embed
	 * and LipidAnchor, GPIAnchor, Peripheral and NonMembrane
	 */
	public static void main(String[] args){
		try{
			for(String s:args) System.out.print(s + " ");
			System.out.println();
			if(args.length != 5){
				System.err.println("1) MaxFoldDiff");
				System.err.println("2) NumOfClassifiers");
				System.err.println("3) 0 (Dun run GPI) or 1 (Run GPI)");
				System.err.println("4) 0 (J48), 1 (RF), 2 (SMO)");
				System.err.println("5) 0 (PredictOnlyOne == false) or 1 (PredictOnlyOne == true)");
				throw new Error("Arguments not equal to 5");
			}
			int maxFoldDiff = Integer.parseInt(args[0]);
			int numOfClassifiers = Integer.parseInt(args[1]);
			int runGPI = Integer.parseInt(args[2]);
			int classifierCode = Integer.parseInt(args[3]);
			int predictOnlyOne = Integer.parseInt(args[4]);
			System.out.println("MaxFoldDiff: " + maxFoldDiff);
			mainMethod(maxFoldDiff, numOfClassifiers, runGPI, classifierCode, predictOnlyOne);
		}catch(Exception e){e.printStackTrace();}
	}
	
	public static void mainMethod(int maxFoldDiff, int numOfClassifiers, int runGPI, int classifierCode, int predictOnlyOne) throws Exception{
		/*
		 * Init
		 */	
		String[] memtypeString = new String[9];
		memtypeString[0] = "NonMembrane";
		memtypeString[1] = "TypeI";
		memtypeString[2] = "TypeII";
		memtypeString[3] = "TypeIII";
		memtypeString[4] = "TypeIV";
		memtypeString[5] = "MultiPass";
		memtypeString[6] = "LipidAnchor";
		memtypeString[7] = "GPIAnchor";
		memtypeString[8] = "Peripheral";
		/*
		 * Features for Detail Splitting
		 */
		String[] featureString = new String[10];
		featureString[0] = "EmbedMembraneVsNonEmbed";
		featureString[1] = "MultiPassVsEmbedMembrane";
		featureString[2] = "TypeIVsEmbedMembrane";
		featureString[3] = "TypeIIVsEmbedMembrane";
		featureString[4] = "TypeIIIVsEmbedMembrane";
		featureString[5] = "TypeIVVsEmbedMembrane";
		featureString[6] = "LipidAnchorVsNonEmbed";
		featureString[7] = "GPIAnchorVsNonEmbed";
		featureString[8] = "PeripheralVsNonEmbed";
		featureString[9] = "NonMembraneVsNonEmbed";
		/*
		 * Sequences for Detail Splitting
		 */
		String[] sequenceString = new String[11];
		sequenceString[0] = "EmbedMembrane";
		sequenceString[1] = "NonEmbed";
		sequenceString[2] = "MultiPass";		
		sequenceString[3] = "TypeI";
		sequenceString[4] = "TypeII";
		sequenceString[5] = "TypeIII";
		sequenceString[6] = "TypeIV";
		sequenceString[7] = "LipidAnchor";
		sequenceString[8] = "GPIAnchor";
		sequenceString[9] = "Peripheral";
		sequenceString[10] = "NonMembrane";
		
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
		loadFeaturesAndSequences(memtypeString, sequenceString, featureString, dataDir, sequencesHashtable, featureHashtable, gaDir, 
				featureFilename, numOfClassifiers);
		/*
		 * Train Classifiers
		 */
		System.out.println("Train Classifiers");		
		Hashtable<String, Classifier> classifierHashtable = new Hashtable<String, Classifier>();	
		trainClassifiers(featureString, classifierHashtable, sequencesHashtable, featureHashtable, 
				maxFoldDiff, classifierOutputDir, numOfClassifiers, classifierCode);
		/*
		 * Run Classifiers
		 */
		System.out.println("Run Test Sequences");		
		predictMemtypeSequences(memtypeString, featureString, sequencesHashtable, 
				outputDir, featureHashtable, classifierHashtable, numOfClassifiers, predictOnlyOne == 1);
		/*
		 * Run Classifiers on GPI/NonGPI if needed
		 */
		if(runGPI == 1){
			List<FastaFormat> gpiList = FastaFileReader.readFastaFile(gpiFileLocation);
			List<FastaFormat> nonGPIList = FastaFileReader.readFastaFile(nonGPIFileLocation);
			predictGPISequences(gpiList, nonGPIList, outputDir, featureHashtable,
					classifierHashtable, numOfClassifiers, predictOnlyOne == 1);
		}
	}
	
	public static void loadFeaturesAndSequences(String[] memtypeString, String[] sequenceString, String[] featureString, String dataDir, 
			Hashtable<String, List<FastaFormat>> sequencesHashtable, Hashtable<String, List<Feature>> featureHashtable,
			String gaDir, String featureFileString, int numOfClassifiers){
		String sep = File.separator;
		/*
		 * Load Sequences
		 */
		for(String s:sequenceString){
			//Load Train Sequences
			sequencesHashtable.put("Train_" + s, FastaFileReader.readFastaFile(dataDir + sep + "Train" + sep + s + ".fasta"));			
		}
		for(String s:memtypeString){
			//Load Test Sequences
			sequencesHashtable.put("Test_" + s, FastaFileReader.readFastaFile(dataDir + sep + "Test" + sep + s + ".fasta"));
		}
		/*
		 * Load Features
		 */
		for(String s:featureString){
			List<List<Feature>> featureListList = new ArrayList<List<Feature>>();
			for(int y = 0; y < numOfClassifiers; y++){
				List<Feature> featureList = Feature.loadSettings(gaDir + sep + s + sep + y + sep + featureFileString);
				featureHashtable.put(s + "_" + y, featureList);
				featureListList.add(featureList);
			}
			for(int x = 0; x < featureListList.size(); x++){
				for(int y = x + 1; y < featureListList.size(); y++){
					if(featureListList.get(x).size() == featureListList.get(y).size()){
						boolean allEqual = true;
						for(int z = 0; z < featureListList.get(x).size(); z++){
							if(featureListList.get(x).get(z).getName().equals(featureListList.get(y).get(z).getName()) == false){
								allEqual = false;
								break;
							}
						}
						if(allEqual){
							throw new Error(s + " got same feature list in different dir. Most likely problem with running GA");
						}
					}
				}
			}
		}
	}
	
	public static void prepareTrainingSequence(int maxFoldDiff, List<FastaFormat> pList, List<FastaFormat> nList, 
			List<FastaFormat> posFastaList, List<FastaFormat> negFastaList){
		/*
		 * Prepare training sequences
		 */
		if(maxFoldDiff == -1){
			//No need to resample
			for(FastaFormat f:pList) posFastaList.add(f);
			for(FastaFormat f:nList) negFastaList.add(f);
		}else if(maxFoldDiff == 0 || maxFoldDiff == 1){
			//Oversample
			Sampling.oversample(posFastaList, negFastaList, pList, nList, maxFoldDiff == 1);
		}else{
			//Undersample
			if(pList.size() >= nList.size()){
				if((pList.size() + 0.0) / nList.size() > maxFoldDiff){
					//Need to resample
					Sampling.undersample(posFastaList, negFastaList, pList, nList, maxFoldDiff);
				}else{
					//within limits
					for(FastaFormat f:pList) posFastaList.add(f);
					for(FastaFormat f:nList) negFastaList.add(f);
				}
			}else{
				if((nList.size() + 0.0) / pList.size() > maxFoldDiff){
					//Need to resample
					Sampling.undersample(posFastaList, negFastaList, pList, nList, maxFoldDiff);
				}else{
					//within limits
					for(FastaFormat f:pList) posFastaList.add(f);
					for(FastaFormat f:nList) negFastaList.add(f);
				}
			}
		}
	}
	
	public static void trainClassifiers(String[] featureString,
			Hashtable<String, Classifier> classifierHashtable, 
			Hashtable<String, List<FastaFormat>> sequencesHashtable,
			Hashtable<String, List<Feature>> featureHashtable, int maxFoldDiff, 
			String classifierOutputDir, int numOfClassifiers, int classifierCode) throws Exception{		
		for(String fs:featureString){
			System.out.println("Training " + fs + " Classifier..");
			String[] classString = fs.split("Vs");
			List<FastaFormat> pList = new ArrayList<FastaFormat>(sequencesHashtable.get("Train_" + classString[0]));
			List<FastaFormat> nList = new ArrayList<FastaFormat>(sequencesHashtable.get("Train_" + classString[1]));
			/*
			 * Need to remove the sequences that repeats itself in pos and neg
			 * Remove from neg
			 * Note: this happens because I am lazy and it is troublesome to extract individually for each membrane type from
			 * EmbedMembrane or NonEmbedMembrane manually.
			 */
			int beforeP = pList.size();
			int beforeN = nList.size();			
			for(int x = 0; x < pList.size(); x++){
				for(int y = 0; y < nList.size();){
					if(pList.get(x).getSequence().equals(nList.get(y).getSequence())){
						//System.out.println(pList.get(x).getHeader());
						//System.out.println(nList.get(y).getHeader());
						nList.remove(y);
					}else{
						y++;
					}
				}
			}			
			int afterN = nList.size();
			if(beforeN - afterN > 0 && beforeN - afterN != beforeP) 
				System.err.println("Something is wrong. Before Neg = " + beforeN + ", After Neg = " + afterN + ", Before Pos = " + beforeP);
			List<FastaFormat> posFastaList = new ArrayList<FastaFormat>();
			List<FastaFormat> negFastaList = new ArrayList<FastaFormat>();			
			prepareTrainingSequence(maxFoldDiff, pList, nList, posFastaList, negFastaList);
			System.out.println("After Resampling: Pos = " + posFastaList.size() + " Neg = " + negFastaList.size());
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
				List<Feature> featureList = featureHashtable.get(fs + "_" + a);
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
				classifierHashtable.put(fs + "_" + a, c);
				/*
				 * Save Classifiers		 
				 */				
				SiriusClassifier.saveClassifierOne(classifierOutputDir + fs + "_" +  a + "_" + classifierNameSuffix + ".classifierone", true, 
						fs + "_" + a +  "_" + classifierNameSuffix, "Protein", 0, 0, c, trainInstances);	
			}
		}			
	}
	
	public static void predictMemtypeSequences(String[] memtypeString, String[] featureString, 
			Hashtable<String, List<FastaFormat>> sequencesHashtable, String outputDir, Hashtable<String, List<Feature>> featureHashtable,
			Hashtable<String, Classifier> classifierHashtable, int numOfClassifiers, boolean predictOnlyOne) throws Exception{		
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
			
			List<MembraneTypePrediction> votingPredictionList = SiriusClassifier.predictMembraneTypeII(
					classifierHashtable, numOfClassifiers, featureHashtable, fastaList, true, predictOnlyOne);		
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
			
			List<MembraneTypePrediction> aggregatePredictionList = SiriusClassifier.predictMembraneTypeII(
					classifierHashtable, numOfClassifiers, featureHashtable, fastaList, false, predictOnlyOne);			
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
	
	public static void predictGPISequences(List<FastaFormat> gpiList, List<FastaFormat> nonGPIList, String outputDir, Hashtable<String, 
			List<Feature>> featureHashtable,Hashtable<String, Classifier> classifierHashtable, int numOfClassifiers, 
			boolean predictOnlyOne) throws Exception{
		System.out.println("Running GPI..");
		String aFilename = outputDir + "Aggregate_GPIvsNonGPI.scores";
		String vFilename = outputDir + "Voting_GPIvsNonGPI.scores";		
		BufferedWriter aggregateOutput = new BufferedWriter(new FileWriter(aFilename));
		BufferedWriter votingOutput = new BufferedWriter(new FileWriter(vFilename));
		
		int gpiIndex = 7;//GPI's index is 7 in memtypeString
		List<MembraneTypePrediction> votingGPIPredictionList = SiriusClassifier.predictMembraneTypeII(
				classifierHashtable, numOfClassifiers, featureHashtable, gpiList, true, predictOnlyOne);
		for(int x = 0; x < gpiList.size(); x++){
			votingOutput.write(gpiList.get(x).getHeader()); votingOutput.newLine();
			votingOutput.write(gpiList.get(x).getSequence()); votingOutput.newLine();
			if(votingGPIPredictionList.get(x).containsIndex(gpiIndex)){
				votingOutput.write("pos,0=" + votingGPIPredictionList.get(x).index2Score(gpiIndex)); votingOutput.newLine();
			}else{
				votingOutput.write("pos,0=0.0"); votingOutput.newLine();
			}
		}
		List<MembraneTypePrediction> votingNonGPIPredictionList = SiriusClassifier.predictMembraneTypeII(
				classifierHashtable, numOfClassifiers, featureHashtable, nonGPIList, true, predictOnlyOne);
		for(int x = 0; x < nonGPIList.size(); x++){
			votingOutput.write(nonGPIList.get(x).getHeader()); votingOutput.newLine();
			votingOutput.write(nonGPIList.get(x).getSequence()); votingOutput.newLine();
			if(votingNonGPIPredictionList.get(x).containsIndex(gpiIndex)){				
				votingOutput.write("neg,0=0.0"); votingOutput.newLine();
			}else{
				votingOutput.write("neg,0=" + votingNonGPIPredictionList.get(x).index2Score(gpiIndex)); votingOutput.newLine();				
			}
		}
		
		List<MembraneTypePrediction> aggregateGPIPredictionList = SiriusClassifier.predictMembraneTypeII(
				classifierHashtable, numOfClassifiers, featureHashtable, gpiList, false, predictOnlyOne);
		for(int x = 0; x < gpiList.size(); x++){
			aggregateOutput.write(gpiList.get(x).getHeader()); aggregateOutput.newLine();
			aggregateOutput.write(gpiList.get(x).getSequence()); aggregateOutput.newLine();
			if(aggregateGPIPredictionList.get(x).containsIndex(gpiIndex)){
				aggregateOutput.write("pos,0=" + aggregateGPIPredictionList.get(x).index2Score(gpiIndex)); aggregateOutput.newLine();
			}else{
				aggregateOutput.write("pos,0=0.0"); aggregateOutput.newLine();
			}
		}
		
		List<MembraneTypePrediction> aggregateNonGPIPredictionList = SiriusClassifier.predictMembraneTypeII(
				classifierHashtable, numOfClassifiers, featureHashtable, nonGPIList, false, predictOnlyOne);
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