package sirius.membranetype.webserverSubmission;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

import sirius.utils.FastaFileReader;
import sirius.utils.FastaFormat;
import sirius.utils.Utils;

public class CleanExceptionSequences {
	public static void main(String[] args){
		String inputFile = Utils.selectFile("Select file to send to HongBin");
		List<FastaFormat> fastaList = FastaFileReader.readFastaFile(inputFile);
		try{
			BufferedWriter output = new BufferedWriter(new FileWriter(Utils.getDirOfFile(inputFile) + "Sequences.fasta"));
			int count = 1;
			for(FastaFormat f:fastaList){
				output.write(">Sequence " + count);
				output.newLine();
				output.write(f.getSequence());
				output.newLine();
				count++;
			}
			output.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
