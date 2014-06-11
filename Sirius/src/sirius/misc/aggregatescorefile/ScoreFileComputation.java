package sirius.misc.aggregatescorefile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import sirius.utils.Utils;

public class ScoreFileComputation {
	public static void combine(List<String> fileLocationList, String outputFileLocation){
		/*
		 * Put all into one single file
		 */
		try{			
			List<BufferedReader> inputList = new ArrayList<BufferedReader>();
			for(String fileLocation:fileLocationList){				
				inputList.add(new BufferedReader(new FileReader(fileLocation)));
			}
			BufferedWriter output = new BufferedWriter(new FileWriter(outputFileLocation));
			String line;
			for(int x = 0; x < inputList.size(); x++){
				while((line = inputList.get(x).readLine()) != null){
					output.write(line);
					output.newLine();
				}				
			}
			for(int x = 0; x < inputList.size(); x++) inputList.get(x).close();
			output.close();			
		}catch(Exception e){e.printStackTrace();}
	}

	public static void aggregate(List<String> fileLocationList, String outputFileLocation){
		/*
		 * Average the score from all files
		 */
		try{
			List<BufferedReader> inputList = new ArrayList<BufferedReader>();
			for(String fileLocation:fileLocationList){
				inputList.add(new BufferedReader(new FileReader(fileLocation)));
			}
			//Here I assume that all the files are of same order
			BufferedWriter output = new BufferedWriter(new FileWriter(outputFileLocation));
			String line;
			while((line = inputList.get(0).readLine()) != null){
				output.write(line);
				output.newLine();
				output.write(inputList.get(0).readLine());
				output.newLine();
				double totalScore = 0.0;
				for(int x = 0; x < inputList.size(); x++){
					StringTokenizer st;
					if(x == 0){
						st = new StringTokenizer(inputList.get(x).readLine(), "=");
						output.write(st.nextToken());					
					}else{
						inputList.get(x).readLine();
						inputList.get(x).readLine();
						st = new StringTokenizer(inputList.get(x).readLine(), "=");
						st.nextToken();
					}
					totalScore += Double.parseDouble(st.nextToken());
				}
				DecimalFormat df = new DecimalFormat("0.###");
				output.write("=" + df.format(totalScore / inputList.size()));
				output.newLine();
			}
			for(int x = 0; x < inputList.size(); x++) inputList.get(x).close();
			output.close();			
		}catch(Exception e){e.printStackTrace();}
	}


	public static void aggregate(String classifierName, int numOfClassifiers){		
		String filePrefix = "classifierone_" + classifierName + "_";
		String fileSuffix = "_Validation.scores";
		//String fileSuffix = "_3_PosValidation.scores";
		//String fileSuffix = "_3_NegValidation.scores";
		String dir = Utils.selectDirectory("Please select Directory with the scores file to aggregate");
		for(int x = 0; x < numOfClassifiers; x++){
			List<String> fileLocationList = new ArrayList<String>();
			for(int y = 0; y <= x; y++){
				fileLocationList.add(dir + filePrefix + y + fileSuffix);			
			}
			ScoreFileComputation.aggregate(fileLocationList, 
					dir + "classifierone_" + classifierName + "_Aggregate_0_till_" + x + fileSuffix);
		}
	}
	
	public static void combine(String classifierName, int numOfClassifiers){		
		String filePrefix = "classifierone_" + classifierName + "_";
		String posFileSuffix = "_3_PosValidation.scores";
		String negFileSuffix = "_3_NegValidation.scores";
		String dir = Utils.selectDirectory("Please select Directory with the scores file");
		for(int x = 0; x < numOfClassifiers; x++){
			List<String> fileLocationList = new ArrayList<String>();
			fileLocationList.add(dir + filePrefix + x + posFileSuffix);
			fileLocationList.add(dir + filePrefix + x + negFileSuffix);
			ScoreFileComputation.combine(fileLocationList, 
					dir + "classifierone_" + classifierName + "_" + x + "_Validation.scores");
		}
	}
	
	public static void main(String[] args){
		/*
		 * 
		 */
		String classifierName = "J48";
		int numOfClassifiers = 20;
		//combine(classifierName, numOfClassifiers);
		aggregate(classifierName, numOfClassifiers);
	}
}
