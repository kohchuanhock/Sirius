package sirius.gpi.webserverSubmission;

//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.FileWriter;
//import java.net.URLEncoder;
//import java.util.ArrayList;
//import java.util.List;
//
//import commons.io.FastaFileReader;
//import commons.sequence.FastaFormat;
//import commons.utils.Utils;
//import commons.webaccess.WebAccess;

public class BigPI_Submission {
//	public static void main(String[] args){
//		try{
//			String inputFile = Utils.selectFile("Select the fasta file to submit to BigPI");
//			String outputDir = Utils.selectDirectory("Please select output directory for BigPI");
//			String formURL = "http://mendel.imp.ac.at/gpi/cgi-bin/gpi_pred.cgi";
//			String targetURL = "http://mendel.imp.ac.at/gpi/gpi_server.html";
//			int GPICount = 0;
//			int NonGPICount = 0;
//			int TooShortCount = 0;
//			int InvalidCount = 0;			
//			FastaFileReader fileReader = new FastaFileReader(inputFile);
//			List<FastaFormat> fastaList = fileReader.getData();
//			int noOfSequencesPerSubmission = 1000;
//			List<String> gpiACList = new ArrayList<String>();
//			for(int y = 0; y < fastaList.size();){
//				System.out.println(y + " / " + fastaList.size());
//				StringBuffer buffer = new StringBuffer();
//				for(int z = 0; z < noOfSequencesPerSubmission && y < fastaList.size(); z++, y++){
//					buffer.append(fastaList.get(y).getHeader() + "\r\n");						
//					buffer.append(fastaList.get(y).getSequence() + "\r\n");
//				}
//				String urlParameters =
//					"LSet=" + URLEncoder.encode("metazoa", "UTF-8") +
//					"&Sequence=" + URLEncoder.encode(buffer.toString(), "UTF-8");			
//				BufferedReader input = WebAccess.executeForm(targetURL, "POST", formURL, urlParameters);
//				String line;
//				String ACLine = null;
//				while((line = input.readLine()) != null) {
//					if(line.contains("Query sequence")){
//						int startIndex = line.indexOf("AC=");
//						int endIndex = line.indexOf(" ", startIndex);
//						ACLine = line.substring(startIndex, endIndex);
//					}
//					if(line.contains("<B>None</B> potential GPI-modification site was found.")){
//						NonGPICount++;						
//					}
//					if(line.contains("Potential GPI-modification site was found.")){
//						GPICount++;
//						gpiACList.add(ACLine);						
//					}
//					if(line.contains(
//							"Your sequence length is too short for the GPI prediction algorithm!")){
//						TooShortCount++;						
//					}
//					if(line.contains("Check correctness of your sequence entry, please !")){
//						InvalidCount++;						
//					}
//				}
//				input.close();
//			}						
//			System.out.println("GPICount: " + GPICount);
//			System.out.println("NonGPICount: " + NonGPICount);
//			System.out.println("TooShort (<55AAs): " + TooShortCount);
//			System.out.println("Invalid Count: " + InvalidCount);			
//			BufferedWriter output = new BufferedWriter(new FileWriter(outputDir + "BigPI_Output.txt"));
//			output.write("GPICount: " + GPICount); output.newLine();
//			output.write("NonGPICount: " + NonGPICount); output.newLine();
//			output.write("TooShort (<55AAs): " + TooShortCount); output.newLine();
//			output.write("Invalid Count: " + InvalidCount); output.newLine();
//			for(String s:gpiACList){
//				output.write(s);
//				output.newLine();
//			}
//			output.close();
//		}catch(Exception e){e.printStackTrace();}
//	}
}
