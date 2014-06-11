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

public class GPISOM_Submission {
	private static String url = "http://genomics.unibe.ch/cgi-bin/gpi.cgi";	
	public static void main(String[] args){
		//singleFileSubmission();
		batchSubmission();
	}
	
	public static void singleFileSubmission(){
		try{
			int[] results = new int[5];
			String inputFile = Utils.selectFile("Select the file to submit to GPISOM");
			FastaFileReader fastaReader = new FastaFileReader(inputFile);
			List<FastaFormat> fastaList = fastaReader.getData();
			int errorCount = 0;
			for(int x = 0; x < fastaList.size();){
				Thread.sleep(500);
				//System.out.println(x + " / " + fastaList.size());
				ClientHttpRequest client = new ClientHttpRequest(url);
				client.setParameter("seq", ">" + x + "\n" + fastaList.get(x).getSequence());
				client.setParameter("jobnam", x);
				//Get Response	
				InputStream is = client.post();
				BufferedReader input = new BufferedReader(new InputStreamReader(is));	      
				String line;	       
				String resultsURL = null;
				while((line = input.readLine()) != null) {
					if(line.contains("href=")){
						int startIndex = line.indexOf("href=") + "href=".length();
						int endIndex = line.indexOf(">", startIndex);
						resultsURL = line.substring(startIndex, endIndex);
					}
				}
				input.close();
				/*
				 * Retrieve results
				 */
				boolean found = false;
				while(found == false){
					Thread.sleep(500);
					//System.out.println("Try..");
					URL nextUrl = new URL(resultsURL);
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
						if(line.contains("Results for Job")){
							//Found
							found = true;
						}
						if(found){
							int count = 0;
							boolean foundLine = false;
							while((line = input.readLine()) != null){
								if(line.contains("<td align=center>")){
									foundLine = true;
									int startIndex;
									if(count < 4){
										startIndex = line.indexOf("<td align=center>") + 
											"<td align=center>".length();
									}else{
										startIndex = line.indexOf("<font color=#ff6666>") + 
											"<font color=#ff6666>".length();
									}
									int endIndex = line.indexOf("<", startIndex);
									String rString = line.substring(startIndex, endIndex);
									int n;
									if(rString.charAt(0) == 'O'){
										n = 0;
									}else{
										n = Integer.parseInt(rString);
									}
									results[count] += n;
									if(count == 4){
										if(n > 0){
											System.out.println("1");//Predicted to be GPI
										}else{
											System.out.println("0");//Predicted to be NonGPI
										}
									}
									count++;
								}
							}
							if(foundLine){
								x++;
								errorCount = 0;
							}else{
								//Means that something went wrong somewhere. 
								//Hence resubmit the same sequence.
								errorCount++;
								if(errorCount > 3){
									System.out.println("Retried this sequence for more than 3 times");
									System.out.println(fastaList.get(x).getHeader());
								}
							}
						}
					}
					input.close();
				}
			}
			System.out.println("Seqs Submitted: " + results[0]);
			System.out.println("Ignored Seqs (<32AAs): " + results[1]);
			System.out.println("Seqs with C-terminal signal (GPI-SOM): " + results[2]);
			System.out.println("Undecidable seqs: " + results[3]);
			System.out.println("GPI anchored (C&N-term signal) (SignalP): " + results[4]);			
		}catch(Exception e){e.printStackTrace();}
	}
	
	public static void batchSubmission(){
		try{
			int[] results = new int[5];
			String inputDirectory = Utils.selectDirectory("Select the directory to submit to GPISOM");
			File[] fileList = new File(inputDirectory).listFiles();
			List<String> gpiACList = new ArrayList<String>();
			for(int x = 0; x < fileList.length; x++){
				System.out.println(x + " / " + fileList.length);
				if(fileList[x].toString().indexOf(".fasta") == -1){
					continue;
				}
				ClientHttpRequest client = new ClientHttpRequest(url);
				client.setParameter("fasta", fileList[x]);
				client.setParameter("jobnam", x + "");
				//Get Response	
				InputStream is = client.post();
				BufferedReader input = new BufferedReader(new InputStreamReader(is));	      
				String line;	       
				String resultsURL = null;
				String id = null;
				while((line = input.readLine()) != null) {
					if(line.contains("href=")){
						int startIndex = line.indexOf("href=") + "href=".length();
						int endIndex = line.indexOf(">", startIndex);
						resultsURL = line.substring(startIndex, endIndex);
						id = line.substring(line.indexOf("id=") + "id=".length());
					}					
				}
				input.close();
				/*
				 * Retrieve results
				 */
				boolean found = false;
				while(found == false){
					Thread.sleep(5000);
					System.out.println("Try..");
					URL nextUrl = new URL(resultsURL);
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
						if(line.contains("Results for Job")){
							//Found
							found = true;
						}
						if(found){
							int count = 0;
							while((line = input.readLine()) != null){
								if(line.contains("<td align=center>")){
									int startIndex;
									if(count < 4){
										startIndex = line.indexOf("<td align=center>") + 
											"<td align=center>".length();
									}else{
										startIndex = line.indexOf("<font color=#ff6666>") + 
											"<font color=#ff6666>".length();
									}
									int endIndex = line.indexOf("<", startIndex);
									results[count] += 
										Integer.parseInt(line.substring(startIndex, endIndex));
									count++;
								}
							}
							/*
							 * Gather the Accessions that are predicted to be GPI
							 */
							URL posURL = new URL(
									"http://genomics.unibe.ch/cgi-bin/gpi.cgi?id=" + id + "&ch=.pos.gpi");
							HttpURLConnection posConnection = (HttpURLConnection)posURL.openConnection();
							posConnection.setRequestMethod("GET");
							posConnection.setRequestProperty("Content-Type", 
							"application/x-www-form-urlencoded");
							posConnection.setUseCaches (false);
							posConnection.setDoInput(true);
							posConnection.setDoOutput(true);
							InputStream posIS = posConnection.getInputStream();
							BufferedReader posInput = new BufferedReader(new InputStreamReader(posIS));
							String posLine;
							while((posLine = posInput.readLine()) != null){
								if(posLine.length() > 0 && posLine.charAt(0) == '>'){
									gpiACList.add(posLine.split(" ")[0].substring(1));
								}
							}
							posInput.close();
							posIS.close();
						}
					}
					input.close();
				}
			}
			System.out.println("Seqs Submitted: " + results[0]);
			System.out.println("Ignored Seqs (<32AAs): " + results[1]);
			System.out.println("Seqs with C-terminal signal (GPI-SOM): " + results[2]);
			System.out.println("Undecidable seqs: " + results[3]);
			System.out.println("GPI anchored (C&N-term signal) (SignalP): " + results[4]);
			BufferedWriter output = new BufferedWriter(new FileWriter(
					inputDirectory + "GPISOM_Output.txt"));
			output.write("Seqs Submitted: " + results[0]); output.newLine();
			output.write("Ignored Seqs (<32AAs): " + results[1]); output.newLine();
			output.write("Seqs with C-terminal signal (GPI-SOM): " + results[2]); output.newLine();
			output.write("Undecidable seqs: " + results[3]); output.newLine();
			output.write("GPI anchored (C&N-term signal) (SignalP): " + results[4]); output.newLine();
			for(String s:gpiACList){
				output.write(s); output.newLine();
			}
			output.close();
		}catch(Exception e){e.printStackTrace();}
	}
}
