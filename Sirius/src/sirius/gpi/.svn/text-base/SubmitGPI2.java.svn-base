package sirius.gpi;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class SubmitGPI2 {
	public static void main(String[] args){
		try{
			BufferedWriter output = new BufferedWriter(new FileWriter("./shell/SubmitGPI.sh"));
			output.write("#!/bin/sh");output.newLine();output.newLine();
			/*
			 * How the features are generated			 
			 * 1) O or U => Oversample or Undersample (Neutral is not possible due to MI computation)
			 * 
			 * Settings for measuring accuracy
			 * 1) 4 or -1 => Train 4 Fold or Train leave one out
			 * 2) O, U or N => Oversample, Undersample or Neutral
			 * 3) 1,2,3,4,5 => Which feature set to use
			 */

			//2 * 2 * 3 * 5 = 60
			char[] featureSamplingStyle = new char[2];
			featureSamplingStyle[1] = 'O';
			featureSamplingStyle[0] = 'U';			

			int[] trainingMode = new int[2];			
			trainingMode[0] = 4;
			trainingMode[1] = -1;
			char[] trainingSamplingStyle = new char[3];
			trainingSamplingStyle[0] = 'O';
			trainingSamplingStyle[1] = 'U';
			trainingSamplingStyle[2] = 'N';
			int[] trainingFeatureSet = new int[5];
			trainingFeatureSet[0] = 1;
			trainingFeatureSet[1] = 2;
			trainingFeatureSet[2] = 3;
			trainingFeatureSet[3] = 4;
			trainingFeatureSet[4] = 5;

			String javaLocation = "/usr/local/package/java/jdk1.6.0_21_64/bin/java";
			int count = 1;
			for(char fss:featureSamplingStyle){						
				for(int t:trainingMode){
					for(char ts:trainingSamplingStyle){
						for(int tfs:trainingFeatureSet){
							BufferedWriter current = new BufferedWriter(
									new FileWriter("./shell/local" + count + ".sh"));
							current.write("#!/bin/sh"); current.newLine(); current.newLine();
							current.write(javaLocation + 
									" -Xmx1024M -jar /share1/home/ahfu/Sirius/SiriusGPI.jar " +
									fss + " " + t + " " + ts + " " + tfs + " ");
							current.newLine();
							current.close();
							output.write("qsub -cwd -l ljob .//local" + count + ".sh");
							count++;
							output.newLine();
						}
					}
				}
			}
			output.close();
		}catch(Exception e){e.printStackTrace();}
	}
}
