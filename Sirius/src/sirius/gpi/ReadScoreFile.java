package sirius.gpi;

import java.io.BufferedReader;
import java.io.FileReader;

import sirius.utils.Utils;

public class ReadScoreFile {
	public static void main(String[] args){
		try{
			String inputFile = Utils.selectFile("Please select score file");
			BufferedReader input = new BufferedReader(new FileReader(inputFile));
			double threshold = 0.19;
			while(true){
				String headerLine = input.readLine();
				if(headerLine == null) break;
				input.readLine();//sequenceLine
				String scoreLine = input.readLine();
				int endIndex = headerLine.indexOf(" ");
				if(scoreLine.charAt(0) == '1') break;//I am currently only concerned with pos
				double score = Double.parseDouble(scoreLine.substring(4));
				System.out.print(headerLine.substring(4,endIndex) + "\t");
				if(score > threshold) System.out.println("1");//predicted to be GPI
				else System.out.println("0");//Predicted to be nonGPI
			}
			input.close();
		}catch(Exception e){e.printStackTrace();}
	}
}
