package sirius.blast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;


/*
 * This class provide methods to access NCBI blast via QBlast 
 */

public class QBlast {	
	private static final String QBlastLink = "http://www.ncbi.nlm.nih.gov/blast/Blast.cgi";

	public static void removeResultFromBlastServer(String RID){
		/*
		 * Request to remove the stored blast result
		 */
		try {                       
			URL url = new URL(QBlastLink);
			URLConnection connection = url.openConnection();
			connection.setRequestProperty("User-Agent", "User0715");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length", "200");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);

			DataOutputStream outStream = new DataOutputStream(connection.getOutputStream());
			String content = "CMD=" + URLEncoder.encode("Delete", "UTF-8");
			content += "&RID=" + URLEncoder.encode(RID, "UTF-8");
			outStream.writeBytes (content);
			outStream.flush ();
			outStream.close ();                       
		} catch (MalformedURLException me) {			
			JOptionPane.showMessageDialog(null, "IOException: " + me.getMessage(), "Exception Occurred", JOptionPane.ERROR_MESSAGE);
		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(null, "IOException: " + ioe.getMessage(), "Exception Occurred", JOptionPane.ERROR_MESSAGE);
		}
	}	

	public static String blastSequence(String sequence, int numOfReturnAlignment, double eValue, 
			boolean filterLowComplexity){
		/*
		 * Send the fasta sequence to blast
		 */
		try {                     
			URL url = new URL(QBlastLink);
			URLConnection connection = url.openConnection();
			connection.setRequestProperty("User-Agent", "User0715");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length", "200");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);

			DataOutputStream outStream = new DataOutputStream(connection.getOutputStream());
			String content = "CMD=" + URLEncoder.encode("Put", "UTF-8");
			content += "&SERVICE=" + URLEncoder.encode("plain", "UTF-8");
			content += "&PROGRAM=" + URLEncoder.encode("blastp", "UTF-8");
			content += "&DATABASE=" + URLEncoder.encode("nr", "UTF-8");
			if(filterLowComplexity){
				content += "&FILTER=" + URLEncoder.encode("L" + "", "UTF-8");  
			}
			//content += "&OTHER_ADVANCED=\'-c 0\'" + URLEncoder.encode("psi" + "", "UTF-8");
			content += "&SERVICE=" + URLEncoder.encode("psi" + "", "UTF-8"); 
			content += "&ALIGNMENTS=" + URLEncoder.encode(numOfReturnAlignment + "", "UTF-8");
			content += "&HITLIST_SIZE=" + URLEncoder.encode(numOfReturnAlignment + "", "UTF-8");
			content += "&EXPECT=" + URLEncoder.encode(eValue + "", "UTF-8");   
			content += "&I_THRESH=" + URLEncoder.encode(0.005 + "", "UTF-8");   
			content += "&FORMAT_TYPE=" + URLEncoder.encode("HTML", "UTF-8");            
			content += "&QUERY=" + URLEncoder.encode(sequence, "UTF-8");			
			outStream.writeBytes (content);
			outStream.flush ();
			outStream.close ();
			String RID = null;
			BufferedReader inStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));            
			String inputLine;            
			while ((inputLine = inStream.readLine()) != null) {				
				if(inputLine.indexOf("QBlastInfoBegin") != -1){
					inputLine = inStream.readLine();
					if(inputLine != null){
						StringTokenizer st = new StringTokenizer(inputLine, "=");
						if(st.countTokens() == 2){
							st.nextToken();//"RID"
							RID = st.nextToken().trim();//Store the RID value for future use
						}
					}
				}                         
			}            
			inStream.close();
			return RID;
		} catch (MalformedURLException me) {
			JOptionPane.showMessageDialog(null, "IOException: " + me.getMessage(), "Exception Occurred", JOptionPane.ERROR_MESSAGE);
		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(null, "IOException: " + ioe.getMessage(), "Exception Occurred", JOptionPane.ERROR_MESSAGE);
		}
		return null;
	}

	public static void showBlastResultInBrowser(String RID, String outputDirectory, boolean local){		
		/*
		 * This method is to show the blast result in user default browser given the rid
		 */
		if(RID.length() > 0){
			try{
				String fileLocation = null;
				if(local){
					if(outputDirectory != null){
						if(outputDirectory.charAt(outputDirectory.length() - 1) != File.separatorChar){
							outputDirectory += File.separator;
						}
						fileLocation = "file:" + File.separator + File.separator + outputDirectory + RID + ".html";
						fileLocation = fileLocation.replaceAll(" ", "%20");
					}else{
						JOptionPane.showMessageDialog(null, "Error!");
						return;
					}
				}else{
					fileLocation = "http://www.ncbi.nlm.nih.gov/blast/Blast.cgi?CMD=Get&RID=" + RID;
				}
				final String[] browsers = 
				{ "firefox", "opera", "konqueror", "epiphany", "seamonkey", "galeon", "kazehakase", "mozilla", "netscape" }; 
				String osName = System.getProperty("os.name");

				if(osName.startsWith("Mac OS")){
					Class<?> fileMgr = Class.forName("com.apple.eio.FileManager"); 
					Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[] {String.class}); 
					openURL.invoke(null, new Object[] {fileLocation});
				}else if(osName.startsWith("Windows")) 
					Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + fileLocation); 
				else{ 
					//assume Unix or Linux 
					boolean found = false;
					for(String browser : browsers) 
						if(!found){ 
							found = Runtime.getRuntime().exec( new String[] {"which", browser}).waitFor() == 0; 
							if (found) 
								Runtime.getRuntime().exec(new String[] {browser, fileLocation}); 
						} 
					if (!found) 
						throw new Exception(Arrays.toString(browsers)); 
				} 
			}catch(Exception e){ 
				JOptionPane.showMessageDialog(null, "Error attempting to launch web browser\n" + e.toString()); 
			} 
		}else{		
			JOptionPane.showMessageDialog(null,"Error occurred. Could be no internet access (more likely) or QBlast server down (less likely).", 
					"Error occurred", JOptionPane.ERROR_MESSAGE);
		}		
	}

	public static boolean isBlastResultReady(String RID){	
		/*
		 * Using the stored RID, attempt to retrieve blast results
		 */
		try{					
			if(RID.length() > 0){
				URL url = new URL(QBlastLink);
				URLConnection connection = url.openConnection();
				connection.setRequestProperty("User-Agent", "User0715");
				connection.setRequestProperty("Connection", "Keep-Alive");
				connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
				connection.setRequestProperty("Content-Length", "2000");
				connection.setDoOutput(true);
				connection.setDoInput(true);
				connection.setUseCaches(false);
				DataOutputStream outStream = new DataOutputStream(connection.getOutputStream());
				String content = "CMD=" + URLEncoder.encode("Get", "UTF-8");
				content += "&RID=" + URLEncoder.encode(RID, "UTF-8");
				content += "&FORMAT_TYPE=" + URLEncoder.encode("Text", "UTF-8");
				content += "&NCBI_GI=" + URLEncoder.encode("on", "UTF-8");
				outStream.writeBytes (content);
				outStream.flush ();
				outStream.close ();
				BufferedReader inStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));  	            
				String inputLine;
				boolean found = false;
				while ((inputLine = inStream.readLine()) != null) {					
					if(inputLine.indexOf("Sequences producing significant alignments:") != -1 ||
							inputLine.indexOf("No significant similarity found") != -1){
						found = true;
						break;
					}
				}						
				inStream.close();
				return found;
			}	            
		}catch (MalformedURLException me) {
			JOptionPane.showMessageDialog(null, "IOException: " + me.getMessage(), "Exception Occurred", JOptionPane.ERROR_MESSAGE);
			return false;
		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(null, "IOException: " + ioe.getMessage(), "Exception Occurred", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return false;
	}

	public static void saveBlastOutputToFile(String outputDirectory, String RID, String header){
		try{
			// url to image
			URL url = new URL("http://www.ncbi.nlm.nih.gov/blast/Blast.cgi?CMD=Get&RID=" + RID);
			// input from image
			InputStream in = new BufferedInputStream(url.openStream());
			// downloaded bytes
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			// output file
			String outputFile = outputDirectory;
			if(outputFile.charAt(outputFile.length() - 1) != File.separatorChar){
				outputFile += File.separator;
			}
			StringTokenizer st = new StringTokenizer(header);
			String name = st.nextToken();
			name = name.replaceAll(">", "");
			RandomAccessFile file = new RandomAccessFile(outputFile + name + ".html","rw");
			// download buffer
			byte[] buffer = new byte[4096]; 

			// download the bytes
			for (int read=0;(read=in.read(buffer))!=-1;out.write(buffer,0,read));
			// write all data out to the file
			file.write(out.toByteArray());
			// close file
			file.close();
		}catch(Exception e){e.printStackTrace();}
	}
}
