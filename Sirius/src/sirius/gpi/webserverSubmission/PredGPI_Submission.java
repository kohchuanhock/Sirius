package sirius.gpi.webserverSubmission;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import sirius.utils.ClientHttpRequest;
import sirius.utils.FastaFileReader;
import sirius.utils.FastaFormat;
import sirius.utils.Utils;

/*
 * This class does a submission of all the files in a selected directory and parse for the results
 */
public class PredGPI_Submission {
	private static String url = "http://gpcr2.biocomp.unibo.it/cgi-bin/predictors/gpi/gpipe_1.4.cgi";
	private static int highlyProb = 0;
	private static int prob = 0;
	private static int weaklyProb = 0;
	private static int nonGPI = 0;
	
	public static void main(String[] args){
		//singleFileSubmission();
		batchSubmission();
	}
	
	public static void singleFileSubmission(){
		try{
			String inputFile = Utils.selectFile("Select the fasta file to submit to PredGPI");
			FastaFileReader fastaReader = new FastaFileReader(inputFile);
			List<FastaFormat> fastaList = fastaReader.getData();
			
						
			for(FastaFormat f:fastaList){
				ClientHttpRequest client = new ClientHttpRequest(url);
				client.setParameter("tipo_hmm", "0");
				client.setParameter("upfile", "");
				client.setParameter("SEQ", ">1\n" + f.getSequence());
				client.setParameter("email", "");
				//Get Response	
				InputStream is = client.post();
				BufferedReader input = new BufferedReader(new InputStreamReader(is));	      
				String line;	       
				while((line = input.readLine()) != null) {
					if(line.indexOf("Prediction accuracy summary:") != -1){
						int n = parseLineForInteger(input.readLine(), "<td class=\"FP_g\">", "<");
						highlyProb += n;
						if(n > 0){
							System.out.println("1");//Predicted to be GPI
						}else{
							System.out.println("0");//Predicted to be NonGPI
						}
						prob += parseLineForInteger(input.readLine(), "<td class=\"FP_y\">", "<");
						weaklyProb += parseLineForInteger(input.readLine(), "<td class=\"FP_o\">", "<");
						nonGPI += parseLineForInteger(input.readLine(), "<td class=\"FP_r\">", "<");
					}
				}
				input.close();
			}
			System.out.println("HighlyProb: " + highlyProb);
			System.out.println("Prob: " + prob);
			System.out.println("WeaklyProb: " + weaklyProb);
			System.out.println("NonGPI: " + nonGPI);			
		}catch(Exception e){e.printStackTrace();}
	}
	
	public static void batchSubmission(){
		try{
			String inputDirectory = Utils.selectDirectory("Select the directory to submit to PredGPI");
			File[] fileList = new File(inputDirectory).listFiles();
			List<String> gpiACList = new ArrayList<String>();
						
			for(int x = 0; x < fileList.length; x++){
				System.out.println(x + " / " + fileList.length);
				if(fileList[x].toString().indexOf(".fasta") == -1){
					continue;
				}
				ClientHttpRequest client = new ClientHttpRequest(url);
				client.setParameter("tipo_hmm", "0");
				client.setParameter("upfile", fileList[x]);
				client.setParameter("SEQ", "");
				client.setParameter("email", "");
				//Get Response	
				InputStream is = client.post();
				BufferedReader input = new BufferedReader(new InputStreamReader(is));	      
				String line;	       
				int hpCount = -1;
				List<String> acList = new ArrayList<String>();
				while((line = input.readLine()) != null) {
					if(line.indexOf("Prediction accuracy summary:") != -1){
						hpCount = parseLineForInteger(input.readLine(), "<td class=\"FP_g\">", "<");
						highlyProb += hpCount;
						prob += parseLineForInteger(input.readLine(), "<td class=\"FP_y\">", "<");
						weaklyProb += parseLineForInteger(input.readLine(), "<td class=\"FP_o\">", "<");
						nonGPI += parseLineForInteger(input.readLine(), "<td class=\"FP_r\">", "<");
					}
					if(line.contains("AC=")){
						int startIndex = 0;
						while(acList.size() < hpCount){
							startIndex = line.indexOf("AC=", startIndex + 1);
							int endIndex = line.indexOf(" ", startIndex);
							acList.add(line.substring(startIndex, endIndex));
						}
					}
				}
				gpiACList.addAll(acList);
				input.close();
			}
			System.out.println("HighlyProb: " + highlyProb);
			System.out.println("Prob: " + prob);
			System.out.println("WeaklyProb: " + weaklyProb);
			System.out.println("NonGPI: " + nonGPI);
			BufferedWriter output = new BufferedWriter(new FileWriter(
					inputDirectory + "PredGPI_Output.txt"));
			output.write("HighlyProb: " + highlyProb); output.newLine();
			output.write("Prob: " + prob); output.newLine();
			output.write("WeaklyProb: " + weaklyProb); output.newLine();
			output.write("NonGPI: " + nonGPI); output.newLine();
			for(String s:gpiACList){
				output.write(s);
				output.newLine();
			}
			output.close();
		}catch(Exception e){e.printStackTrace();}
	}
	
	private static int parseLineForInteger(String line, String startString, String endString){
		int index = line.indexOf(startString) + startString.length();
		int endIndex = line.indexOf(endString, index);
		return Integer.parseInt(line.substring(index, endIndex).trim());
	}
}
