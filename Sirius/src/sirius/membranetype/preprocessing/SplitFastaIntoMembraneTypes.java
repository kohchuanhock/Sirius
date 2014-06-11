package sirius.membranetype.preprocessing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import sirius.utils.FastaFileReader;
import sirius.utils.FastaFormat;
import sirius.utils.Utils;

public class SplitFastaIntoMembraneTypes {
	public static void main(String[] args){
		try{
			run();
		}catch(Exception e){e.printStackTrace();}
	}
	
	public static void run() throws Exception{
		String[] memTypeString = new String[8];
		memTypeString[0] = "pass type I ";
		memTypeString[1] = "pass type II ";
		memTypeString[2] = "pass type III ";
		memTypeString[3] = "pass type IV ";
		memTypeString[4] = "Multi-pass";
		memTypeString[5] = "Lipid-anchor";
		memTypeString[6] = "GPI-anchor";//Note that Lipid-anchor is a prereq for GPI-anchor		
		memTypeString[7] = "Peripheral";
		
		String inputFile = Utils.selectFile("Select fasta file to split into membrane type");
		String outputDir = Utils.selectDirectory("Select output dir");
		List<BufferedWriter> outputList = new ArrayList<BufferedWriter>();
		for(String s:memTypeString) 
			outputList.add(new BufferedWriter(new FileWriter(outputDir + s.trim() + ".fasta")));
		int count = 0; 
		List<FastaFormat> fastaList = FastaFileReader.readFastaFile(inputFile);
		for(FastaFormat f:fastaList){
			String header = f.getHeader();
			List<Integer> iList = new ArrayList<Integer>();
			for(int i = 0; i < memTypeString.length; i++){
				if(header.contains(memTypeString[i])){
					iList.add(i);
				}
			}
			switch(iList.size()){
			case 0:
				//Does not belong anywhere, print out to understand why
				if(header.contains("Multi- pass")){
					outputList.get(4).write(f.getHeader());
					outputList.get(4).newLine();
					outputList.get(4).write(f.getSequence());
					outputList.get(4).newLine();
				}else if(header.contains("Lipid- anchor")){
					outputList.get(5).write(f.getHeader());
					outputList.get(5).newLine();
					outputList.get(5).write(f.getSequence());
					outputList.get(5).newLine();
				}else if(header.contains("Single-pass membrane protein") ||
						header.contains("Single- pass membrane protein")){
					//Do nothing
				}else if(header.contains("Single") || header.contains("Multi") ||
						header.contains("anchor") || header.contains("Peri")){				
					count++;
					System.out.println(count + " - None: " + header);
				}
				break;
			case 1://belong to precisely one - GOOD
				int index = iList.get(0);
				//if(index == 4) System.out.println(f.getHeader());//Display all the multipass sequence's header
				outputList.get(index).write(f.getHeader());
				outputList.get(index).newLine();
				outputList.get(index).write(f.getSequence());
				outputList.get(index).newLine();
				break;
			case 2://belong to two, if it is GPI then it is fine
				if(iList.get(0) == 5 && iList.get(1) == 6){
					//GPI
					outputList.get(6).write(f.getHeader());
					outputList.get(6).newLine();
					outputList.get(6).write(f.getSequence());
					outputList.get(6).newLine();
					break;
				}				
			default:
				//Belong to 2 or more, print out to understand why
				System.out.println("2 or more: " + header);
				break;
			}
		}
		for(BufferedWriter output:outputList) output.close();
	}
}
