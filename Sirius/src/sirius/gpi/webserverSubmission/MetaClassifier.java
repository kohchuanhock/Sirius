package sirius.gpi.webserverSubmission;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Hashtable;
import java.util.List;

import sirius.utils.CombinationGenerator;
import sirius.utils.FastaFileReader;
import sirius.utils.FastaFormat;
import sirius.utils.Utils;

public class MetaClassifier {
	/*
	 * Aggregate and compute the scores for the different combinations of GPI predictors
	 */
	public static void main(String[] args){
		try{
			String resultFilename[] = new String[5];
			resultFilename[0] = "FragAnchor";
			resultFilename[1] = "GPISOM";
			resultFilename[2] = "PredGPI";
			resultFilename[3] = "BigPI";
			resultFilename[4] = "SiriusGPI";						

			String posInputFastaFile = Utils.selectFile("Please select the GPI fasta file");
			String negInputFastaFile = Utils.selectFile("Please select the nonGPI fasta file");
			String posDir = Utils.selectDirectory("Please select the dir for GPI fasta file predictions");
			String negDir = Utils.selectDirectory("Please select the dir for NonGPI fasta file predictions");
			String outputDir = Utils.selectDirectory("Please select scores file output location");
			for(int x = 1; x <= resultFilename.length; x++){				
				CombinationGenerator combi = new CombinationGenerator (resultFilename.length, x);
				while(combi.hasMore()){
					String[] currentName = new String[x];
					int[] indices = combi.getNext(); 
					for(int z = 0; z < indices.length; z++){
						currentName[z] = resultFilename[indices[z]];
					}
					consolidateBatchResults(posInputFastaFile, negInputFastaFile, posDir, negDir, currentName,
							outputDir);
					for(String s:currentName) System.out.print(s + ", "); System.out.println("Done!");
				}
			}
		}catch(Exception e){e.printStackTrace();}
	}

	public static void consolidateBatchResults(String posInputFastaFile, 
			String negInputFastaFile, String posDir, String negDir, String[] resultFilename,
			String outputDir) throws Exception{
		/*
		 *
		 */		
		Hashtable<String, Integer> predictionCount = new Hashtable<String, Integer>();
		
		FastaFileReader posFasta = new FastaFileReader(posInputFastaFile);
		FastaFileReader negFasta = new FastaFileReader(negInputFastaFile);
		List<FastaFormat> posFastaList = posFasta.getData();
		List<FastaFormat> negFastaList = negFasta.getData();
		
		obtainACFromHeader(posFastaList, predictionCount);
		obtainACFromHeader(negFastaList, predictionCount);
		/*for(String s:predictionCount.keySet()){
			System.out.println(s);
		}*/
		
		resultFileACCounting(resultFilename, posDir, predictionCount);
		resultFileACCounting(resultFilename, negDir, predictionCount);
		
		outputScoreFile(posFastaList, negFastaList, predictionCount, resultFilename, outputDir);			
	}
	
	
	private static void outputScoreFile(List<FastaFormat> posFastaList, List<FastaFormat> negFastaList,
			Hashtable<String, Integer> hashtable, String[] resultFilename, String outputDir) throws Exception{
		String outputfilename = "MetaOutput_";
		for(String s:resultFilename){
			outputfilename += s.charAt(0);
		}
		outputfilename += ".scores";
		
		BufferedWriter output = new BufferedWriter(new FileWriter(outputDir + outputfilename));
		for(FastaFormat f:posFastaList){
			output.write(f.getHeader());
			output.newLine();
			output.write(f.getSequence());
			output.newLine();
			double predictionScore = hashtable.get(obtainACFromString(f.getHeader()));
			predictionScore /= resultFilename.length;
			output.write("pos,0=" + predictionScore);
			output.newLine();
		}
		for(FastaFormat f:negFastaList){
			output.write(f.getHeader());
			output.newLine();
			output.write(f.getSequence());
			output.newLine();
			double predictionScore = hashtable.get(obtainACFromString(f.getHeader()));
			predictionScore /= resultFilename.length;			
			output.write("neg,0=" + predictionScore);
			output.newLine();
		}
		output.close();
	}
	
	private static void resultFileACCounting(String[] resultFilename, String dir, 
			Hashtable<String, Integer> hashtable) throws Exception{
		for(String s:resultFilename){
			String fullPathname = dir + s + "_Output.txt";
			BufferedReader input = new BufferedReader(new FileReader(fullPathname));			
			String line;
			while((line = input.readLine()) != null){
				if(line.contains("AC=")){					
					hashtable.put(line, hashtable.get(line) + 1);
				}
			}
			input.close();
		}	
	}
	
	private static void obtainACFromHeader(List<FastaFormat> fastaList, 
			Hashtable<String, Integer> hashtable){
		for(FastaFormat f:fastaList){		
			hashtable.put(obtainACFromString(f.getHeader()), 0);
		}
	}
	
	private static String obtainACFromString(String header){		
		int startIndex = header.indexOf("AC=");
		int endIndex = header.indexOf(" ", startIndex);
		return header.substring(startIndex, endIndex);
	}
}
