package sirius.webpredictors;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import sirius.predictor.main.ClassifierData;
import sirius.predictor.main.SequenceNameData;
import sirius.trainer.features.Feature;
import sirius.trainer.features.GenerateArff;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

/*
 * This java file is got from decompiling my Polyadenylation.jar
 * Need to properly fix and test it
 * For now, simply use the first layer score while a second layer classifier should be deployed
 */
public class Polyadenylation{
	private static final String outputDir = "." + File.separator + "output" + File.separator;
	
	public static void main(String[] args){
		try{
			run(args[0]);
		}catch(Exception e){
			try {
				PrintStream eOutput = new PrintStream(new FileOutputStream(
						"." + File.separator + "log/error.txt"));				
				e.printStackTrace(eOutput);
				eOutput.close();
			} catch (IOException e1) {				
			}			
		}
	}

	public static void run(String inputFilename) throws Exception{
		File file = new File("." + File.separator + "input" + File.separator + inputFilename);
		ClassifierData classifier = loadClassifier();		
		List<SequenceNameData> dataList = loadFastaFile(file);
		runClassifier(file, classifier, dataList);
	}	

	private static List<SequenceNameData> loadFastaFile(File file) throws Exception {
		BufferedReader in = new BufferedReader(new FileReader(file.getAbsolutePath()));
		int countSequenceNumber = 0;
		List<SequenceNameData> dataList = new ArrayList<SequenceNameData>();
		String eachSequence = "";
		String sequenceName = "";
		String line;
		while ((line = in.readLine()) != null)
		{
			if (line.indexOf(">") == 0) {
				countSequenceNumber++;
				if (eachSequence.length() != 0) {
					if (eachSequence.charAt(eachSequence.length() - 1) == '*')
						dataList.add(new SequenceNameData(sequenceName, 
								eachSequence.substring(0, eachSequence.length() - 1), ""));
					else
						dataList.add(new SequenceNameData(sequenceName, eachSequence, ""));
				}
				sequenceName = line;
				eachSequence = "";
			}
			else {
				eachSequence = eachSequence + line;
				if (eachSequence.indexOf("=") != -1) {
					in.close();
					throw new Exception("Please ensure that " + file.getAbsolutePath() + 
					" is in FASTA format.");
				}
			}
		}
		in.close();
		if (countSequenceNumber == 0) {
			throw new Exception("Please ensure that " + file.getAbsolutePath() + 
			" is in FASTA format.");
		}
		if (eachSequence.charAt(eachSequence.length() - 1) == '*')
			dataList.add(new SequenceNameData(sequenceName, eachSequence.substring(0, 
					eachSequence.length() - 1), ""));
		else
			dataList.add(new SequenceNameData(sequenceName, 
					eachSequence, ""));
		return dataList;
	}

	private static ClassifierData loadClassifier() throws Exception{		
		File file = new File("." + File.separator + "classifiers" + File.separator + "IntronicAsControl.classifiertwo");
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);

		int classifierNum = ois.readInt();
		String classifierName = (String)ois.readObject();
		String classifierOneSettings = (String)ois.readObject();
		Instances instances = (Instances)ois.readObject();
		Classifier classifierOne = (Classifier)ois.readObject();
		String sequenceType = (String)ois.readObject();
		int scoringMatrixIndex = ois.readInt();
		int countingStyleIndex = ois.readInt();
		int setUpstream = -1;
		int setDownstream = -1;
		String classifierTwoSettings = "";
		Instances instances2 = null;
		Classifier classifierTwo = null;
		if (classifierNum == 2) {
			setUpstream = ois.readInt();
			setDownstream = ois.readInt();
			classifierTwoSettings = (String)ois.readObject();
			instances2 = (Instances)ois.readObject();
			classifierTwo = (Classifier)ois.readObject();
		}
		ois.close();
		return new ClassifierData(classifierNum, classifierName, instances, 
				classifierOne, classifierTwo, classifierOneSettings, 
				classifierTwoSettings, setUpstream, 
				setDownstream, instances2, sequenceType, scoringMatrixIndex, 
				countingStyleIndex, false); 			
	}

	private static void runClassifier(File file, ClassifierData classifier, 
			List<SequenceNameData> dataList) throws Exception{		
		BufferedWriter output = new BufferedWriter(
				new FileWriter(Polyadenylation.outputDir + "classifierone_" + 
						classifier.getClassifierName() + 
						"_" + classifier.getClassifierType() + "_" + file.getName() + ".scores"));
		Classifier classifierOne = classifier.getClassifierOne();
		int leftMostPosition = classifier.getLeftMostPosition();
		int rightMostPosition = classifier.getRightMostPosition();

		Instances inst = classifier.getInstances();
		ArrayList<Feature> featureDataArrayList = new ArrayList<Feature>();
		for (int x = 0; x < inst.numAttributes() - 1; x++){
			featureDataArrayList.add(Feature.loadFeatureViaName(inst.attribute(x).name()));
		}

		for (int x = 0; x < dataList.size(); x++)
		{
			output.write(dataList.get(x).getHeader());
			output.newLine();
			output.write(dataList.get(x).getSequence());
			output.newLine();

			String sequence = dataList.get(x).getSequence();
			int targetLocationIndex;
			int minSequenceLengthRequired;        
			if ((leftMostPosition < 0) && (rightMostPosition > 0)) {
				minSequenceLengthRequired = leftMostPosition * -1 + rightMostPosition;
				targetLocationIndex = leftMostPosition * -1;
			}
			else
			{          
				if ((leftMostPosition < 0) && (rightMostPosition < 0)) {
					minSequenceLengthRequired = rightMostPosition - leftMostPosition + 1;
					targetLocationIndex = leftMostPosition * -1;
				} else {
					minSequenceLengthRequired = rightMostPosition - leftMostPosition + 1;
					targetLocationIndex = leftMostPosition * -1;
				}
			}
			boolean firstEntryForClassifierOne = true;
			for (int y = 0; y + (minSequenceLengthRequired - 1) < sequence.length(); y++) {
				String line2 = sequence.substring(y + 0, y + minSequenceLengthRequired);

				Instance tempInst = new Instance(inst.numAttributes());
				tempInst.setDataset(inst);
				for (int z = 0; z < inst.numAttributes() - 1; z++)
				{
					tempInst.setValue(z, (Double)GenerateArff.getMatchCount(
							"+1_Index(" + targetLocationIndex + ")", line2, featureDataArrayList.get(z), 
							classifier.getScoringMatrixIndex(), 
							classifier.getCountingStyleIndex(), classifier.getScoringMatrix()));
				}

				tempInst.setValue(inst.numAttributes() - 1, "neg");
				double[] results = classifierOne.distributionForInstance(tempInst);
				if (firstEntryForClassifierOne)
					firstEntryForClassifierOne = false;
				else
					output.write(",");
				output.write(y + targetLocationIndex + "=" + results[0]);
			}
			output.newLine();
			output.flush();
		}
		output.flush();
		output.close();

		if (classifier.getClassifierType() == 2) {
			BufferedWriter output2 = new BufferedWriter(
					new FileWriter("." + File.separator + "output" + File.separator + 
							"classifiertwo_" + 
							classifier.getClassifierName() + "_" + 
							classifier.getClassifierType() + "_" + 
							file.getName() + ".scores"));
			BufferedReader input2 = new BufferedReader(
					new FileReader("." + File.separator + "output" + File.separator + 
							"classifierone_" + 
							classifier.getClassifierName() + "_" + 
							classifier.getClassifierType() + "_" + file.getName() + 
					".scores"));
			Classifier classifierTwo = classifier.getClassifierTwo();
			Instances inst2 = classifier.getInstances2();
			int setUpstream = classifier.getSetUpstream();
			int setDownstream = classifier.getSetDownstream();
			int minScoreWindowRequired;
			if ((setUpstream < 0) && (setDownstream < 0)) {
				minScoreWindowRequired = setDownstream - setUpstream + 1;
			}else{          
				if((setUpstream < 0) && (setDownstream > 0)){
					minScoreWindowRequired = setUpstream * -1 + setDownstream;
				}else{
					minScoreWindowRequired = setDownstream - setUpstream + 1;
				}
			}

			String lineHeader;
			while ((lineHeader = input2.readLine()) != null){          
				String lineSequence = input2.readLine();
				output2.write(lineHeader);
				output2.newLine();
				output2.write(lineSequence);
				output2.newLine();
				StringTokenizer locationScore = new StringTokenizer(input2.readLine(), ",");
				int totalTokens = locationScore.countTokens();
				String[][] scores = new String[totalTokens][2];
				int scoreIndex = 0;
				while (locationScore.hasMoreTokens()) {
					StringTokenizer locationScoreToken = 
						new StringTokenizer(locationScore.nextToken(), 
								"=");
					scores[scoreIndex][0] = locationScoreToken.nextToken();
					scores[scoreIndex][1] = locationScoreToken.nextToken();
					scoreIndex++;
				}

				if ((setUpstream == 0) || (setDownstream == 0)){
					input2.close();
					output2.close();
					throw new Exception("setUpstream == 0 || setDownstream == 0");
				}
				int targetLocationIndex2;          
				if (setUpstream < 0)
					targetLocationIndex2 = Integer.parseInt(scores[0][0]) + -setUpstream;
				else {
					targetLocationIndex2 = Integer.parseInt(scores[0][0]);
				}
				for (int x = 0; x + minScoreWindowRequired - 1 < totalTokens; x++)
				{
					if (x != 0)
						output2.write(",");
					Instance tempInst2 = new Instance(minScoreWindowRequired + 1);
					tempInst2.setDataset(inst2);
					for (int y = 0; y < minScoreWindowRequired; y++) {
						tempInst2.setValue(y, Double.parseDouble(scores[(x + y)][1]));
					}
					tempInst2.setValue(tempInst2.numAttributes() - 1, "pos");
					double[] results = classifierTwo.distributionForInstance(tempInst2);
					output2.write(targetLocationIndex2 + "=" + results[0]);
					targetLocationIndex2++;
				}
				output2.newLine();
			}
			input2.close();
			output2.close();
		}		
	}
}
