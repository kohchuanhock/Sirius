package sirius.gpi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import sirius.utils.Utils;

public class Obtain155Fasta {
	public static void main(String[] args){
		String swpFile = Utils.selectFile("Please select the 155 SWP file");
		String fastaFile = Utils.selectFile("Please select the GPI fasta file");
		String outputDir = Utils.selectDirectory("Please select the output dir");
		
		try{
			List<String> swpList = new ArrayList<String>();
			BufferedReader inputSWP = new BufferedReader(new FileReader(swpFile));
			String line;
			while((line = inputSWP.readLine()) != null){
				if(line.contains("#") == false && line.length() > 1){
					swpList.add(line.trim());
				}
			}
			inputSWP.close();
			BufferedReader input = new BufferedReader(new FileReader(fastaFile));
			BufferedWriter output = new BufferedWriter(new FileWriter(outputDir + "155.fasta"));
			boolean found = false;
			while((line = input.readLine()) != null){
				if(line.charAt(0) == '>'){
					found = false;
					for(String s:swpList){
						if(line.contains(s)) found = true;
					}
				}
				if(found){
					output.write(line);
					output.newLine();
				}
			}
			input.close();
			output.close();
		}catch(Exception e){e.printStackTrace();}
	}
}
