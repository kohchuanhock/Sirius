package sirius.gpi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import sirius.utils.Utils;

public class CorrectScoreFile {
	/*
	 * Because the output of the score file is slightly scaled, I had to write this program to correct it
	 * It is faster to correct it than re-running since it takes quite sometime to rerun
	 */
	public static void main(String[] args){
		try{
			String inputDirectory = Utils.selectDirectory("Select the directory with erronous score file");
			File[] fileList = new File(inputDirectory).listFiles();
			for(int x = 0; x < fileList.length; x++){
				System.out.println(x + " / " + fileList.length);
				if(fileList[x].toString().indexOf(".score") == -1){
					continue;
				}
				BufferedReader input = new BufferedReader(new FileReader(fileList[x]));
				String filename = fileList[x].getAbsolutePath();
				String outputFilename = filename.replaceFirst(".score", ".scores");
				BufferedWriter output = new BufferedWriter(new FileWriter(outputFilename));				
				String line;
				//Discard the first line, it has no problem
				line = input.readLine();
				output.write(line);
				output.newLine();
				while((line = input.readLine()) != null){
					if(line.indexOf(">") != -1){
						int index = line.indexOf(">");
						output.write(line.substring(0, index));
						output.newLine();
						output.write(line.substring(index));
						output.newLine();
					}else{
						output.write(line);
						output.newLine();
					}
				}
				output.close();
				input.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
