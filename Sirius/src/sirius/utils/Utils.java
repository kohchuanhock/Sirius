package sirius.utils;

import java.awt.GridLayout;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Utils {
	private static DecimalFormat df = new DecimalFormat("#.##");
	
	private static String lastFileLocation = "LastFile: ";
	
	private static String lastDirLocation = "LastDir: ";
	
	public static String selectDirectory(String title){return selectDirectory(title, Utils.lastDirLocation);}
	
	public static String selectDirectory(String title, String lastDir){		
		/*
		 * User is to select the directory that contains both the gene2go and gene2accession files
		 * returns the user selected directory
		 */					
		try{
			String lastDirLocation = Settings.getInformation(lastDir);
			JFileChooser fileChooser;
			if(lastDirLocation != null){			
				fileChooser = new JFileChooser(lastDirLocation + File.separator);
				//fc = new JFileChooser();
			}else{				
				fileChooser = new JFileChooser(Settings.getInformation(Utils.lastDirLocation));
			}			
			fileChooser.setDialogTitle("Please select Directory: " + title);		
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);		
			fileChooser.setAcceptAllFileFilterUsed(false);
			JPanel panel = new JPanel(new GridLayout(1,1));
			panel.add(fileChooser);
			JFrame frame = new JFrame("Select Directory");
			//frame.add(fileChooser);
			frame.add(panel);
			frame.setLocationRelativeTo(null);
			int returnVal = fileChooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				Settings.updateInformation(lastDir, file.getParentFile().getAbsolutePath());
				Settings.updateInformation(Utils.lastDirLocation, file.getParentFile().getAbsolutePath());
				String returnString = file.getAbsolutePath();
				if(returnString.charAt(returnString.length() - 1) != File.separatorChar){
					returnString += File.separator;
				}
				frame.setVisible(false);
				frame.dispose();
				return returnString;
			}			
			frame.dispose();
			return null;
		}catch(Exception e){e.printStackTrace(); return null;}
	}
	
	public static boolean cleanDirectory(String path, boolean deleteDir){
		return cleanDirectory(new File(path), deleteDir);
	}
	
	public static boolean cleanDirectory(File path, boolean deleteDirectory) {
		/*
		 * deleteDirectory - should the dir be deleted
		 * 
		 * returns false if the directory is not successfully cleaned
		 */
		if( path.exists() ) {
			File[] files = path.listFiles();
	        for(int i=0; i<files.length; i++) {
	        	if(files[i].isDirectory()) {
	             cleanDirectory(files[i],true);
	           }else {
	             files[i].delete();
	           }
	        }
	        if(deleteDirectory)
	        	path.delete();
		}
		if(path.listFiles().length > 0) return false;
		else return true;
	}
	
	public static String selectFile(){
		return selectFile("");
	}
	
	public static String selectFile(String title){
		return selectFile(title, lastFileLocation);
	}
	
	public static String selectFile(String title, String previousFileAccess) {
		return selectFile(title, previousFileAccess, null);
	}
	
	public static String selectFile(String title, FileNameExtensionFilter filter){
		return selectFile(title, lastFileLocation, filter);
	}
	
	public static String selectFile(String title, String previousFileAccess, FileNameExtensionFilter filter){
		/*
		 * Opens a dialog box to load a file with extension
		 */		
		JFileChooser fc;
		String previous = Settings.getInformation(previousFileAccess);
		if(previous == null){
			fc = new JFileChooser(Settings.getInformation(Utils.lastFileLocation));
		}else{
			fc = new JFileChooser(previous);
		}
		fc.setDialogTitle(title);
		if(filter != null){
			fc.setFileFilter(filter);
		}
		JFrame frame = new JFrame("Select File");
		//frame.add(fc);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		int returnVal = fc.showOpenDialog(frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			Settings.updateInformation(previousFileAccess, file.getParentFile().getAbsolutePath());
			Settings.updateInformation(Utils.lastFileLocation, file.getParentFile().getAbsolutePath());
			frame.dispose();
			return file.getAbsolutePath();
		}
		frame.dispose();
		return null;
	}
	
	public static String getNameFromStringLocation(String location, boolean withExtension){	
		try{
			String[] s = location.split("\\" + File.separator);			
			if(withExtension) return s[s.length - 1];
			else{			
				return s[s.length - 1].substring(0, s[s.length - 1].lastIndexOf("."));
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new Error("Location: " + location);
		}
	}

	public static void displayPercentageFeedback(String prefix, int current, int max){
		Utils.displayPercentageFeedback(prefix, current, max, 1);
	}
	
	public static void displayPercentageFeedback(String prefix, int current, int max, int denominator){
		/*
		 * Note that denominator should not be > 100 since my decimalFormat only limit to two decimal place
		 */
		if(max / (denominator * 100) > 0){
			if(current % (max / (denominator * 100)) == 0){
				System.out.println(prefix + " @ " + df.format((current * 100.0) / max) + "%");
			}
		}		
	}	
	
	public static void copyFile(File srcPath, File dstPath) throws IOException {
		/*
		 * Copy all the standard required file from common folder to output folder		 
		 */					
		FileInputStream fis = new FileInputStream(srcPath);
		FileOutputStream fos = new FileOutputStream(dstPath);
		FileChannel ic = fis.getChannel();				
		FileChannel oc = fos.getChannel();
		ic.transferTo(0, ic.size(), oc);
		ic.close();
		oc.close();	
		fis.close();
		fos.close();
	}
	
	public static void copyDirectory(File srcPath, File dstPath) throws IOException {
		/*
		 * Copy the directory contents from one directory to another
		 */
		if (srcPath.isDirectory()) {
			if (!dstPath.exists()) {
				dstPath.mkdir();
			}
			String files[] = srcPath.list();
			for (int i = 0; i < files.length; i++) {
				copyDirectory(new File(srcPath, files[i]), new File(dstPath,
						files[i]));
			}
		}else {
			if (!srcPath.exists()) {
				System.out.println("File or directory does not exist.");
				System.exit(0);
			}else{
				InputStream in = new FileInputStream(srcPath);
				OutputStream out = new FileOutputStream(dstPath);
				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
			}
		}
		System.out.println("Directory copied.");
	}
	
	public static String getDirOfFile(String fileLocation){
		return fileLocation.substring(0, fileLocation.lastIndexOf(File.separator)) + File.separator;
	}
	
	public static double roundToDecimals(double d, int c) {
		int temp = (int)(((d + 5.0 / Math.pow(10, c + 1))*Math.pow(10,c)));
		return (((double)temp)/Math.pow(10,c));
	}
	
	public static void gabageCollector(){
		Runtime r = Runtime.getRuntime();
		r.runFinalization();
		long before = r.freeMemory();
		r.gc();
		long after = r.freeMemory();
		System.out.println("GC Memory Gain: " + (after - before));
	}
	
	public static List<String> getColumn(String fileLocation, String delimiter, int col, boolean skipFirstRow) throws IOException{
		List<String> sList = new ArrayList<String>();
		BufferedReader input = new BufferedReader(new FileReader(fileLocation));
		String line;
		if(skipFirstRow) input.readLine();
		while((line = input.readLine()) != null){
			String[] s = line.split(delimiter);
			sList.add(s[col]);
		}
		input.close();
		return sList;
	}
	
	public static int countLine(String filename) throws IOException {
		/*
		 * Quick way to count the number of lines in the filename
		 */
	    InputStream is = new BufferedInputStream(new FileInputStream(filename));
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        while ((readChars = is.read(c)) != -1) {
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n')
	                    ++count;
	            }
	        }
	        return count;
	    } finally {
	        is.close();
	    }
	}
	
	public static List<String> readFile(String fileLocation){
		return readFile(fileLocation, -1);
	}
	
	public static List<String> readFile(String fileLocation, int top){
		/*
		 * Read only the top X lines. 
		 * Read all if top is <= 0
		 */
		try{
			List<String> sList = new ArrayList<String>();
			BufferedReader input = new BufferedReader(new FileReader(fileLocation));
			String line;
			while((line = input.readLine()) != null){
				sList.add(line);
				if(top > 0 && sList.size() == top){
					input.close();
					return sList;
				}
			}
			input.close();
			return sList;
		}catch(Exception e){e.printStackTrace(); return null;}
	}
	
	public static List<String> listDir(String dir){
		return listDir(dir, true, true);
	}
	
	public static List<String> listDir(String dir, boolean fullPathname, boolean withExtension){
		return listDir(dir, null, fullPathname, withExtension);
	}
	
	public static List<String> listDir(String dir, String containsInName, boolean fullPathname, boolean withExtension){
		return listDir(dir, containsInName, null, fullPathname, withExtension);
	}
	
	public static List<String> listDir(String dir, String containsInName,String extension, boolean fullPathname, boolean withExtension){
		/*
		 * Return a list of absolute file location
		 * Filter by containsInName => filename must contain containsInName
		 * Filter by extension => filename must contain extension
		 */
		File[] files = new File(dir).listFiles();
		if(files == null) throw new Error("Directory do not exist: " + dir);
		List<String> filelocationList = new ArrayList<String>();
		for(File f:files){
			String filelocation;
			if(fullPathname){
				filelocation = f.getAbsolutePath();
			}else{
				filelocation = f.getName();
			}
			if(withExtension == false){
				filelocation = Utils.getNameFromStringLocation(filelocation, withExtension);
			}
			if(extension != null && filelocation.contains("." + extension) == false) continue;
			if(containsInName != null && Utils.getNameFromStringLocation(filelocation, true).contains(containsInName)) continue;
			filelocationList.add(filelocation);
		}
		return filelocationList;
	}
	
	public static Set<String> listDirAsSet(String dir, boolean fullPathname, boolean returnWithExtension){
		return listDirAsSet(dir, null, null, fullPathname, returnWithExtension);
	}
	
	public static Set<String> listDirAsSet(String dir, String containsInName, String extension, boolean returnWithExtension){
		return listDirAsSet(dir, containsInName, extension, true, returnWithExtension);
	}
	
	public static Set<String> listDirAsSet(String dir, String containsInName, String extension, boolean fullPathname, boolean returnWithExtension){
		/*
		 * Return all absolute file location as Set
		 * Filter by containsInName => filename must contain containsInName
		 * Filter by extension => filename must contain extension
		 */
		File[] files = new File(dir).listFiles();
		if(files == null) throw new Error("Directory do not exist: " + dir);
		Set<String> filelocationList = new HashSet<String>();
		for(File f:files){
			String filelocation = f.getAbsolutePath();
			if(extension != null && filelocation.contains("." + extension) == false) continue;
			if(containsInName != null && Utils.getNameFromStringLocation(filelocation, false).contains(containsInName)) continue;
			filelocationList.add(dir + File.separator + Utils.getNameFromStringLocation(filelocation, returnWithExtension));
		}
		return filelocationList;
	}
	
	public static String getSubstring(String line, String promoterString, String startString, String endString){
		return getSubstring(line, promoterString, startString, endString, false);
	}
	
	public static String getSubstring(String line, String promoterString, String startString, String endString, boolean acceptNotFound){
		int promoterIndex = 0;
		if(promoterString != null){
			promoterIndex = line.indexOf(promoterString) + promoterString.length();
			if(promoterIndex == -1){
				if(acceptNotFound) return null;
				else throw new Error("PromoterString not found: " + line);
			}
		}
		int startIndex = line.indexOf(startString, promoterIndex) + startString.length();
		if(startIndex == -1){
			if(acceptNotFound) return null;
			else throw new Error("StartString not found: " + line);
		}
		int endIndex = line.indexOf(endString, startIndex);
		if(endIndex == -1){
			if(acceptNotFound) return null;
			else{
				System.out.println("Line: " + line);
				System.out.println("StartString: " + startString);
				System.out.println("EndString: " + endString);
				throw new Error("EndString not found. Line: " + line + " EndString: " + endString);
			}
		}
		return line.substring(startIndex, endIndex);
	}
	
	public static List<String> tokenize(String line, String token){
		String[] a = line.split(token);
		List<String> sList = new ArrayList<String>();
		for(String s:a){
			sList.add(s);
		}
		return sList;
	}
	
	public static void main(String[] args){
		//String file = Utils.selectDirectory("Select File");
		//System.out.println(Utils.getDirOfFile(file));		
		System.out.println(getNameFromStringLocation("Name.SI.txt", false));
	}
}