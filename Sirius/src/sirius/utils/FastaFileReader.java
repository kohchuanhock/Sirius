package sirius.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FastaFileReader {
	private static final String lastInputFastaLocation = "LastInputFastaLocation";
	
	public static double averageLength;
	public static int shortestLength;
	public static int longestLength;
	
	private List<FastaFormat> data;

	public FastaFileReader(String fastaFileLocation){
		this.data = new ArrayList<FastaFormat>();		
		try{
			BufferedReader input = new BufferedReader(new FileReader(fastaFileLocation));
			String line;
			String header;
			String sequence = "";
			header = input.readLine();
			while((line = input.readLine()) != null){				
				if(header.charAt(0) != '>'){
					JOptionPane.showMessageDialog(null,"Please only load Fasta Format Files","Error",
							JOptionPane.ERROR_MESSAGE);
				}				
				if(line.length() > 0){
					if(line.charAt(0) != '>')
						sequence += line;
					else{
						this.data.add(new FastaFormat(header, sequence));
						header = line;
						sequence = "";
					}
				}
			}
			this.data.add(new FastaFormat(header,sequence));			
			//debugShowData();
			input.close();
		}catch(Exception e){			
			e.printStackTrace();
		}
	}

	public FastaFileReader(){
		this.data = new ArrayList<FastaFormat>();
	}

	public void add(FastaFormat fastaFormat){
		this.data.add(fastaFormat);
	}		

	public FastaFormat getDataAt(int index){
		return data.get(index);
	}

	public int size(){
		return data.size();
	}
	
	public List<FastaFormat> getData(){
		return this.data;
	}

	public static String loadFastaFile(){	
		/*
		 * Opens a dialog box to load a fasta file with extension .fasta and .fa
		 */
		JFileChooser fc = new JFileChooser(Settings.getInformation(lastInputFastaLocation));
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
	            "Fasta Files", "fasta", "fa");
	    fc.setFileFilter(filter);	
		int returnVal = fc.showOpenDialog(null);		
		if (returnVal == JFileChooser.APPROVE_OPTION) {	        
			File file = fc.getSelectedFile();
			Settings.updateInformation(lastInputFastaLocation, file.getParentFile().getAbsolutePath());	
			return file.getAbsolutePath();
		}
		return null;
	}
	
	public static void parseFastaFile(String fastaFileLocation, List<FastaFormat> data, int phenotypeIndex) 	
		throws Exception{
		/*
		 * This method parse the given fasta file with feedback to GUI interface
		 */
		try{			
			BufferedReader input = new BufferedReader(new FileReader(fastaFileLocation));
			String line;
			String header;
			String sequence = "";
			header = input.readLine();
			int totalLength = 0;
			shortestLength = Integer.MAX_VALUE;
			longestLength = Integer.MIN_VALUE;
			while((line = input.readLine()) != null){
				if(header.charAt(0) != '>'){
					JOptionPane.showMessageDialog(null,"Please only load Fasta Format Files","Error",
							JOptionPane.ERROR_MESSAGE);
					input.close();
					throw new Exception();			
				}				
				if(line.length() > 0){
					if(line.charAt(0) != '>'){						
						sequence = sequence.concat(line);
					}else{
						data.add(new FastaFormat(header, sequence, phenotypeIndex));
						int length = sequence.length();
						totalLength += length;
						if(shortestLength > length){
							shortestLength = length;							
						}
						if(longestLength < length){
							longestLength = length;
						}							
						header = line;
						sequence = "";
					}
				}
			}
			data.add(new FastaFormat(header,sequence, phenotypeIndex));
			int length = sequence.length();
			totalLength += length;
			if(shortestLength > length){
				shortestLength = length;							
			}
			if(longestLength < length){
				longestLength = length;
			}	
			averageLength = (totalLength + 0.0) / data.size();			
			input.close();
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception();			
		}
	}
	
	public static void parseFastaFile(String fastaFileLocation, List<FastaFormat> data) throws Exception{
	try{	
		/*
		 * This method simply parse the given fasta file
		 */
		BufferedReader input = new BufferedReader(new FileReader(fastaFileLocation));
		String line;
		String header;
		String sequence = "";
		header = input.readLine();
		while((line = input.readLine()) != null){
			if(header.charAt(0) != '>'){
				JOptionPane.showMessageDialog(null,"Please only load Fasta Format Files","Error",
						JOptionPane.ERROR_MESSAGE);
				input.close();
				throw new Exception();					
			}				
			if(line.length() > 0){
				if(line.charAt(0) != '>'){						
					sequence = sequence.concat(line);
				}else{
					data.add(new FastaFormat(header, sequence));	
					header = line;
					sequence = "";
				}
			}
		}
		data.add(new FastaFormat(header,sequence));
		input.close();
	}catch(Exception e){
		e.printStackTrace();
		throw new Exception();			
	}
}
	
	public static List<FastaFormat> readFastaFile(final String fastaFileLocation){
		/*
		 * This method reads a fasta file and returns it as List of SimpleFastaFormat
		 */
		List<FastaFormat> data = new ArrayList<FastaFormat>();
		try {
			parseFastaFile(fastaFileLocation, data);
		} catch (Exception e) {				
			e.printStackTrace();			
		}
		return data;
	}
	
	public static List<FastaFormat> readFastaFile(final JDialog parent, final String fastaFileLocation, final int phenotypeIndex) 
		throws Exception{	
		/*
		 * This method reads a fasta file and returns it as List of FastaFormat		
		 */			
		List<FastaFormat> data = new ArrayList<FastaFormat>();
		try {
			parseFastaFile(fastaFileLocation, data, phenotypeIndex);			
		} catch (Exception e) {				
			e.printStackTrace();
			throw new Exception();
		}
		return data;
	}
	
	public static void convertScoreFile2FastaFile(String scoreFile){
		try{
			BufferedReader input = new BufferedReader(new FileReader(scoreFile));
			BufferedWriter output = new BufferedWriter(new FileWriter(Utils.getDirOfFile(scoreFile) + 
					Utils.getNameFromStringLocation(scoreFile, false) + ".fasta"));
			String headerLine;
			String sequenceLine;
			while((headerLine = input.readLine()) != null){
				sequenceLine = input.readLine();
				input.readLine();//score line
				output.write(headerLine); output.newLine();
				output.write(sequenceLine); output.newLine();
			}
			input.close();
			output.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		String scoreFile = Utils.selectFile("Select Score File to convert to Fasta file");
		FastaFileReader.convertScoreFile2FastaFile(scoreFile);
	}
}