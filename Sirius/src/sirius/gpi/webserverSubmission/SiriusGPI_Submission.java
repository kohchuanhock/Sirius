package sirius.gpi.webserverSubmission;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import sirius.utils.Utils;

public class SiriusGPI_Submission {
	/*
	 * Simply take the files that are prepared by ScoreFileComputation
	 * and output them to the format similiar to other GPI predictors, which is
	 * 
	 * Those predicted to be GPI will be output in the format
	 * "AC= XX"
	 */
	public static void main(String[] args){
		String scoreFile = Utils.selectFile("Please select the score file to use");
		String posOutputDir = Utils.selectDirectory("Please select pos output dir");
		String negOutputDir = Utils.selectDirectory("Please select neg output dir");
		double threshold = 0.2;
		
		try{
			BufferedReader input = new BufferedReader(new FileReader(scoreFile));
			BufferedWriter posOutput = new BufferedWriter(new FileWriter(posOutputDir + "SiriusGPI_Output.txt"));
			BufferedWriter negOutput = new BufferedWriter(new FileWriter(negOutputDir + "SiriusGPI_Output.txt"));
			String headerLine;			
			String scoreLine;
			while((headerLine = input.readLine()) != null){
				input.readLine();//This is the sequence line
				scoreLine = input.readLine();
				double score = obtainScore(scoreLine);
				if(score > threshold){
					//PredictedAC
					if(scoreLine.contains("pos")){
						posOutput.write(extractAC(headerLine));
						posOutput.newLine();
					}else if(scoreLine.contains("neg")){
						negOutput.write(extractAC(headerLine));
						negOutput.newLine();
					}else{
						input.close();
						posOutput.close();
						negOutput.close();
						throw new Error("Unknown Class: " + scoreLine);
					}
				}
			}
			input.close();
			posOutput.close();
			negOutput.close();
		}catch(Exception e){e.printStackTrace();}
	}
	
	private static String extractAC(String headerLine){		
		return headerLine.split(" ")[0].substring(1);
	}
	
	private static double obtainScore(String scoreLine) throws Exception{
		String[] s = scoreLine.split("=");
		if(s.length == 2){
			return Double.parseDouble(s[1]);
		}else{
			throw new Exception("Does not contain exactly one '=': " + scoreLine);
		}
	}
}
