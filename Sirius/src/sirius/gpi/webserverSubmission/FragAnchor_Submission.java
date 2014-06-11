package sirius.gpi.webserverSubmission;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import sirius.utils.ClientHttpRequest;
import sirius.utils.FastaFileReader;
import sirius.utils.FastaFormat;
import sirius.utils.Utils;

public class FragAnchor_Submission {
	private static String url = "http://navet.ics.hawaii.edu/~fraganchor/cgi-bin/nnhmm.pl";
	private static int invalid = 0;
	private static int rejected = 0;
	private static int accepted = 0;
	private static int highly = 0;
	private static int probable = 0;
	private static int weakly = 0;
	private static int potentialFP = 0;
	private static int notScored = 0;	
	
	public static void main(String[] args){
		//singleFileSubmission();
		batchSubmit();
	}
	
	public static void singleFileSubmission(){
		try{
			String inputFile = Utils.selectFile("Select fasta file to submit to FragAnchor");
			FastaFileReader fastaReader = new FastaFileReader(inputFile);
			List<FastaFormat> fastaList = fastaReader.getData();
			String line;
			for(FastaFormat f:fastaList){
				String submitString = ">1\n"+ f.getSequence();
				//System.out.println(submitString);
				ClientHttpRequest client = new ClientHttpRequest(url);	
				client.setParameter("inputseq", submitString);
				//Get Response	
				InputStream is = client.post();
				BufferedReader input = new BufferedReader(new InputStreamReader(is));
				String nextURL = null;
				while((line = input.readLine()) != null) {
					if(line.contains("window.location.href=")){
						int index = line.indexOf("window.location.href=") + 
							"window.location.href=".length() + 1;
						int endIndex = line.indexOf("'", index);
						nextURL = line.substring(index, endIndex);
					}
				}
				input.close();
				if(nextURL == null){
					throw new Error("nextURL is null!");
				}
				/*
				 * Go to results page
				 */				
				URL nextUrl = new URL("http://navet.ics.hawaii.edu/~fraganchor/cgi-bin/" + nextURL);
				HttpURLConnection connection = (HttpURLConnection)nextUrl.openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Content-Type", 
				"application/x-www-form-urlencoded");
				connection.setUseCaches (false);
				connection.setDoInput(true);
				connection.setDoOutput(true);
				//Get Response	
				is = connection.getInputStream();
				input = new BufferedReader(new InputStreamReader(is));
				boolean GPI = false;
				while((line = input.readLine()) != null) {
					if(line.indexOf("Time stamp:") == 0){
						invalid += parseLineForInteger(line, "Invalid sequences");
						rejected += parseLineForInteger(line, "Rejected");
						accepted += parseLineForInteger(line, "Accepted");
						int gpi = parseLineForInteger(line, "Highly probable");
						highly += gpi;
						if(gpi > 0) GPI = true;
						probable += parseLineForInteger(line, "Probable");
						weakly += parseLineForInteger(line, "Weakly probable");
						potentialFP += parseLineForInteger(line, "Potential false positive");
						notScored += parseLineForInteger(line, "NOT scored");						
					}
				}
				if(GPI){
					System.out.println("1");//Predicted to be GPI				
				}else{
					System.out.println("0");//Predicted to be NonGPI
				}
				input.close();
				connection.disconnect();
			}			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void batchSubmit(){
		try{
			String inputDirectory = Utils.selectDirectory("Select the directory to submit to FragAnchor");
			File[] fileList = new File(inputDirectory).listFiles();
			List<String> gpiACList = new ArrayList<String>();
			
			for(int x = 0; x < fileList.length; x++){
				System.out.println(x + " / " + fileList.length);
				if(fileList[x].toString().indexOf(".fasta") == -1){
					continue;
				}
				ClientHttpRequest client = new ClientHttpRequest(url);			
				client.setParameter("fasta", fileList[x]);
				//Get Response	
				InputStream is = client.post();
				BufferedReader input = new BufferedReader(new InputStreamReader(is));	      
				String line;
				String nextURL = null;
				while((line = input.readLine()) != null) {
					if(line.contains("window.location.href=")){
						int index = line.indexOf("window.location.href=") + 
							"window.location.href=".length() + 1;
						int endIndex = line.indexOf("'", index);
						nextURL = line.substring(index, endIndex);
					}
				}
				input.close();
				if(nextURL == null){
					throw new Error("nextURL is null!");
				}
				/*
				 * Go to results page
				 */				
				URL nextUrl = new URL("http://navet.ics.hawaii.edu/~fraganchor/cgi-bin/" + nextURL);
				HttpURLConnection connection = (HttpURLConnection)nextUrl.openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Content-Type", 
				"application/x-www-form-urlencoded");
				connection.setUseCaches (false);
				connection.setDoInput(true);
				connection.setDoOutput(true);
				//Get Response	
				is = connection.getInputStream();
				input = new BufferedReader(new InputStreamReader(is));				
				while((line = input.readLine()) != null) {
					if(line.indexOf("Time stamp:") == 0){
						invalid += parseLineForInteger(line, "Invalid sequences");
						rejected += parseLineForInteger(line, "Rejected");
						accepted += parseLineForInteger(line, "Accepted");
						highly += parseLineForInteger(line, "Highly probable");
						probable += parseLineForInteger(line, "Probable");
						weakly += parseLineForInteger(line, "Weakly probable");
						potentialFP += parseLineForInteger(line, "Potential false positive");
						notScored += parseLineForInteger(line, "NOT scored");
						
						URL posUrl = new URL("http://navet.ics.hawaii.edu/~fraganchor/cgi-bin/" + 
								nextURL + "&tp=hp");
						HttpURLConnection posconnection = (HttpURLConnection)posUrl.openConnection();
						posconnection.setRequestMethod("GET");
						posconnection.setRequestProperty("Content-Type", 
						"application/x-www-form-urlencoded");
						posconnection.setUseCaches (false);
						posconnection.setDoInput(true);
						posconnection.setDoOutput(true);
						//Get Response	
						InputStream posIS = posconnection.getInputStream();
						BufferedReader posInput = new BufferedReader(new InputStreamReader(posIS));
						String posLine;
						while((posLine = posInput.readLine()) != null){
							if(posLine.contains("AC=")){
								int startIndex = posLine.indexOf("AC=");
								int endIndex = posLine.indexOf(" ", startIndex);
								gpiACList.add(posLine.substring(startIndex, endIndex));
							}
						}
						posInput.close();
						posIS.close();
					}
				}
				input.close();
				connection.disconnect();
			}
			System.out.println("Invalid: " + invalid);
			System.out.println("Rejected: " + rejected);
			System.out.println("Accepted: " + accepted);			
			System.out.println("HighlyProb: " + highly);
			System.out.println("Prob: " + probable);
			System.out.println("WeaklyProb: " + weakly);
			System.out.println("Potential FP: " + potentialFP);
			System.out.println("NotScored: " + notScored);
			BufferedWriter output = new BufferedWriter(new FileWriter(
					inputDirectory + "FragAnchor_Output.txt"));
			output.write("Invalid: " + invalid); output.newLine();
			output.write("Rejected: " + rejected); output.newLine();
			output.write("Accepted: " + accepted); output.newLine();			
			output.write("HighlyProb: " + highly); output.newLine();
			output.write("Prob: " + probable); output.newLine();
			output.write("WeaklyProb: " + weakly); output.newLine();
			output.write("Potential FP: " + potentialFP); output.newLine();
			output.write("NotScored: " + notScored); output.newLine();
			for(String s:gpiACList){
				output.write(s);
				output.newLine();			
			}
			output.close();
		}catch(Exception e){e.printStackTrace();}	
	}

	private static int parseLineForInteger(String line, String startString){
		int index = line.indexOf(startString) + startString.length();
		int startIndex = line.indexOf("(", index) + 1;
		int endIndex = line.indexOf("sequence", startIndex);
		return Integer.parseInt(line.substring(startIndex, endIndex).trim());
	}
}

