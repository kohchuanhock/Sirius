package sirius.gpi;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class IndependentGPIClassifierShellGenerator {
	public static void main(String[] args){
		/*
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
		try{
			char[] GASamplingStyle = new char[2];
			GASamplingStyle[0] = 'O';
			GASamplingStyle[1] = 'U';
			char[] trainingSamplingStyle = new char[3];
			trainingSamplingStyle[0] = 'O';
			trainingSamplingStyle[1] = 'N';
			trainingSamplingStyle[2] = 'U';
			String[] posTestFasta = new String[1];
			posTestFasta[0] = "NewGPIAnchor_PredGPIAnchor_0.3_0.5_0.5.fasta";
			String[] negTestFasta = new String[2];
			negTestFasta[0] = "NewNonGPIAnchor_0.5_0.5.fasta";
			negTestFasta[1] = "NonMembrane_0.5_Pred_NonGPI_0.5.fasta";
			
			String outputDirectory = "./ishell/";
			int count = 1;
			for(char ga:GASamplingStyle){
				for(char t:trainingSamplingStyle){
					for(String pos:posTestFasta){
						for(String neg:negTestFasta){
							BufferedWriter output = new BufferedWriter(new FileWriter(
									outputDirectory + "local" + count + ".sh"));
							output.write("#!/bin/sh");
							output.newLine();
							output.newLine();
							output.write("/usr/local/package/java/jdk1.6.0_21_64/bin/java -Xmx2000M -jar /share1/home/ahfu/Sirius/SiriusIndependentGPIClassifier.jar " +
									ga + " " + t + " " + pos + " " + neg + " " + ga + "_" + t + "_" + neg.split("_")[0] + "");
							output.newLine();
							output.close();
							count++;
						}
					}
				}
			}
		}catch(Exception e){e.printStackTrace();}
	}
}
