package sirius.membranetype;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import sirius.utils.Utils;

public class Memtype2L {
	public static void main(String[] args){
		try{
			computeTPFP();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void computeTPFP() throws Exception{
		/*Exception: 81
		Not Known: 0
		Non Membrane: 48
		SinglePass TypeI: 192
		SinglePass TypeII: 10
		SinglePass TypeIII: 0
		SinglePass TypeIV: 0
		MultiPass: 23
		LipidAnchor: 2
		Peripheral: 10
		GPIAnchor: 14
		Total (Must match seqs submitted else means missing counts): 380
		Submitted: 380*/
		String memtypeString[] = new String[9];
		memtypeString[0] = "TypeI.";
		memtypeString[1] = "TypeII.";
		memtypeString[2] = "TypeIII.";
		memtypeString[3] = "TypeIV.";
		memtypeString[4] = "MultiPass";
		memtypeString[5] = "LipidAnchor";
		memtypeString[6] = "GPIAnchor";
		memtypeString[7] = "Peripheral";
		memtypeString[8] = "NonMembrane";
		
		String outputStyleString[] = new String[9];
		outputStyleString[0] = "TypeI:";
		outputStyleString[1] = "TypeII:";
		outputStyleString[2] = "TypeIII:";
		outputStyleString[3] = "TypeIV:";
		outputStyleString[4] = "MultiPass:";
		outputStyleString[5] = "LipidAnchor:";
		outputStyleString[6] = "GPIAnchor:";
		outputStyleString[7] = "Peripheral:";
		outputStyleString[8] = "Non Membrane:";
		
		int totalException = 0;
		int totalUnknown = 0;
		int totalSubmitted = 0;
		int[] totalCorrect = new int[memtypeString.length];
		int[] totalIncorrect = new int[memtypeString.length];		
		String inputDir = Utils.selectDirectory("Please select dir with Memtype2L outputs");
		File[] fileList = new File(inputDir).listFiles();
		for(File f:fileList){
			if(f.getAbsolutePath().contains("_Output.txt") == false) continue;
			int index = text2Index(f.getName(), memtypeString);
			BufferedReader input = new BufferedReader(new FileReader(f));
			String line;
			int currentTotal = 0;
			int currentSubmitted = 0;
			while((line = input.readLine()) != null){
				if(line.contains("Exception:")){
					totalException += text2Number(line);
				}else if(line.contains("Not Known:")){
					totalUnknown += text2Number(line);
				}else if(line.contains("Submitted:")){
					currentSubmitted = text2Number(line); 
					totalSubmitted += currentSubmitted;
				}else if(line.contains("Total")){
					currentTotal = text2Number(line);
				}else{
					int i = text2Index(line, outputStyleString);
					if(index == i) totalCorrect[i] += text2Number(line);//True Positive
					else totalIncorrect[i] += text2Number(line);//False Positive
				}
			}
			input.close();
			if(currentTotal != currentSubmitted) throw new Error("Total do not match Submitted");
		}
		BufferedWriter output = new BufferedWriter(new FileWriter(inputDir + "TPFP.txt"));
		output.write("====================================================="); output.newLine();
		output.write("True Positive"); output.newLine();
		output.write("====================================================="); output.newLine();
		for(int x = 0; x < memtypeString.length; x++){
			output.write(outputStyleString[x] + "\t" + totalCorrect[x]); output.newLine();
		}
		output.write("====================================================="); output.newLine();
		output.write("False Positive"); output.newLine();
		output.write("====================================================="); output.newLine();
		for(int x = 0; x < memtypeString.length; x++){
			output.write(outputStyleString[x] + "\t" + totalIncorrect[x]); output.newLine();
		}
		output.write("====================================================="); output.newLine();
		output.write("Others"); output.newLine();
		output.write("====================================================="); output.newLine();		
		output.write("Exception:\t" + totalException); output.newLine();
		output.write("Unknown:\t" + totalUnknown); output.newLine();
		output.write("TotalSubmitted:\t" + totalSubmitted); output.newLine();
		output.close();
		//Ensure total tallies
		int total = 0;
		for(int x = 0; x < memtypeString.length; x++){
			total += totalCorrect[x];
			total += totalIncorrect[x];			
		}
		total += totalException;
		total += totalUnknown;
		if(total != totalSubmitted){
			throw new Error("Total and Total Submitted DO NOT TALLY!");
		}
	}
	
	private static int text2Number(String text){
		String[] s = text.split(":");
		return Integer.parseInt(s[1].trim());
	}
	
	private static int text2Index(String filename, String[] memtypeString){
		for(int x = 0; x < memtypeString.length; x++){
			if(filename.contains(memtypeString[x])) return x;
		}
		throw new Error("Unknown Filename: " + filename);
	}
	
}
