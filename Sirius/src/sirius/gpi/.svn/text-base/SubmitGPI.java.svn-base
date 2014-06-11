package sirius.gpi;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class SubmitGPI {
	public static void main(String[] args){
		try{
			BufferedWriter output = new BufferedWriter(new FileWriter("./shell/SubmitGPI.sh"));
			output.write("#!/bin/sh");output.newLine();output.newLine();
			/*
			 * How the features are generated
			 * 1) 2 or 4 => 2fold or 4fold features
			 * 2) 80 or 100 => Subset80 or Subset100
			 * 3) O or U => Oversample or Undersample (Neutral is not possible due to MI computation)
			 * 4) 1,2 => Fold diff after resampling
			 * 
			 * Settings for measuring accuracy
			 * 1) 2, 4 or -1 => Train 2 Fold or Train 4 Fold or Train leave one out
			 * 2) O, U or N => Oversample, Undersample or Neutral
			 */
			
			//1 * 1 * 1 * 2 * 2 * 3 = 12
			int[] featureMode = new int[1];
			featureMode[0] = 4;
			//featureMode[1] = 2;
			int[] featureSubset = new int[1];
			featureSubset[0] = 100;
			//featureSubset[1] = 100;
			char[] featureSamplingStyle = new char[1];
			//featureSamplingStyle[1] = 'O';
			featureSamplingStyle[0] = 'U';
			int[] featureResamplingFoldDiff = new int[2];
			featureResamplingFoldDiff[0] = 1;
			featureResamplingFoldDiff[1] = 2;
			
			int[] trainingMode = new int[3];
			trainingMode[0] = 2;
			trainingMode[1] = 4;
			trainingMode[2] = -1;
			char[] trainingSamplingStyle = new char[3];
			trainingSamplingStyle[0] = 'O';
			trainingSamplingStyle[1] = 'U';
			trainingSamplingStyle[2] = 'N';
			
			String javaLocation = "/usr/local/package/java/jdk1.6.0_21_64/bin/java";
			int count = 1;
			for(int f:featureMode){				
				for(int s:featureSubset){
					for(char fss:featureSamplingStyle){
						for(int frf:featureResamplingFoldDiff){
							for(int t:trainingMode){
								if(f == 4 && t == 2) continue;
								for(char sam:trainingSamplingStyle){
									BufferedWriter current = new BufferedWriter(
											new FileWriter("./shell/local" + count + ".sh"));
									current.write("#!/bin/sh"); current.newLine(); current.newLine();
									current.write(javaLocation + 
										" -Xmx1024M -jar /share1/home/ahfu/Sirius/SiriusGPI.jar " +
										f + " " + s + " " + fss + " " + frf + " " + t + " " + sam + "");
									current.newLine();
									current.close();
									output.write("qsub -cwd -l ljob .//local" + count + ".sh");
									count++;
									output.newLine();
								}
							}
						}
					}
				}
			}
			output.close();
		}catch(Exception e){e.printStackTrace();}
	}
}
