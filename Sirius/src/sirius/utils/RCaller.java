/*
 *
    RCaller, A solution for calling R from Java
    Copyright (C) 2010  Mehmet Hakan Satman

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * Mehmet Hakan Satman - mhsatman@yahoo.com
 * http://www.mhsatman.com
 *
 * John Oliver added Multi-threaded StreamReader to prevent read blocks and
 * deadlocks when output arrives from both stderr and stdout.
 */


package sirius.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

// Multi-threaded Stream reading to prevent read blocks & deadlocks
class StreamReader extends Thread
{
	static final String  STDERR = "STDERR";
	static final String  STDOUT = "STDOUT";
	private RCaller      rcObj;
	private String       streamName;
	private InputStream  stream;
	private boolean      echoStrm = false;
	private boolean interpret = false;

	// Create the object for a given stream.  Optionally echo the sub-process'
	// streams to the parent process's streams.
	StreamReader(InputStream inStream, String inStreamName, RCaller rc,
			boolean echoStrm, boolean interpret) {
		rcObj            = rc;
		stream           = inStream;
		streamName       = inStreamName;
		this.echoStrm    = echoStrm;
		this.interpret = interpret;
	}

	@Override
	public void run()
	{
		try {
			InputStreamReader sr  = new InputStreamReader(stream);
			BufferedReader    br  = new BufferedReader(sr);
			String line = null;

			while((line = br.readLine()) != null){
				if(streamName.equals(STDERR)){
					if(echoStrm){
						System.err.println(streamName + ": " + line);
					}
				}else if(streamName.equals(STDOUT)){
					if(echoStrm){
						System.out.println(streamName + ": " + line);
					}
					if(interpret){										
						rcObj.registerField(line);					
					}
				}else{
					throw new Exception("StreamReader: Unrecognised stream name");
				}
			}//while
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}//StreamReader


public class RCaller {

	private static final String MacOSString = "Mac";
	private static final String Windows32OSString = "Windows32";
	private static final String Windows64OSString = "Windows64";
	private static final String Unix32OSString = "Unix32";
	public static final String Unix64OSString = "Unix64";
	public static final String xString = "x";//This is needed because formatdb handle them differently
	public static final String iaString = "ia";//This is needed because formatdb handle them differently
	public static String userOS;
	public static String architecture;
	public static String version = "0.5.2";
	public static String author = "Mehmet Hakan Satman - mhsatman@yahoo.com - http://www.mhsatman.com";

	bsh.Interpreter Interpreter;
	String libraryCode;	
	String Rscript;	
	ArrayList<String> fields;

	public RCaller(){
		RCaller.userOS = obtainUserOS();
		if (RCaller.userOS.equals(RCaller.Windows32OSString) || RCaller.userOS.equals(Windows64OSString)) {
			this.Rscript = "C:/Program Files/R/R-2.11.1/bin/RScript.exe";
			File f = new File(this.Rscript);
			if (f.exists() == false) {
				this.Rscript = "C:/Program Files/R/R-2.15.0/bin/RScript.exe";
			}
		} else {
			this.Rscript = "/usr/bin/Rscript";
		}
		
		File f = new File(this.Rscript);
		if (f.exists() == false) {
			System.out.println("File does not exist: " + this.Rscript);
			if(this.Rscript == null)
				this.Rscript = Utils.selectFile("Please select location of RScript.exe");
			else{
				f = new File(this.Rscript);
				if(f.exists() == false)
					this.Rscript = Utils.selectFile("Please select location of RScript.exe");
			}

		}
		libraryCode = loadMakeJavaLibrary();		
		Interpreter = new bsh.Interpreter();		
		fields = new ArrayList<String>();		
	}

	private String obtainUserOS(){		
		/*
		 * Obtain the operating system that it is running on
		 */
		String osName = System.getProperty("os.name");
		int bit = Integer.parseInt(System.getProperty("sun.arch.data.model"));
		String osArch = System.getProperty("os.arch");
		if(osArch.equalsIgnoreCase("amd64")){
			architecture = xString;
		}else{
			architecture = iaString;
		}
		if(osName.startsWith("Mac OS")){	
			return MacOSString;
		}else if(osName.startsWith("Windows")){
			if(bit == 32)
				return Windows32OSString;
			else
				return Windows64OSString;
		}else{ 
			//Assume Linux/Unix
			if(bit == 32)
				return Unix32OSString;
			else 
				return Unix64OSString;
		}
	}

	public void setRScriptExecutableFile(String location){
		this.Rscript=location;
	}

	public String getRscriptExecutableFile(){
		return(this.Rscript);
	}

	public Object RGet(String s) throws Exception {
		Object o = null;
		o = Interpreter.eval(s);
		if(o == null) {
			throw new Exception("Variable " + s + " was null");
		}
		return(o);
	}

	public double[] RGetAsDoubleArray(String name) throws Exception{
		double[] result = (double[])Interpreter.eval(name);
		if( result == null ) {
			throw new Exception("Double array " + name + " was null");
		}
		return(result);
	}

	public int[] RGetAsIntArray(String name) throws Exception{
		int[] result = (int[])Interpreter.eval(name);
		if( result == null ) {
			throw new Exception("Int array " + name + " was null");
		}
		return(result);
	}

	public String[] RGetAsStringArray(String name) throws Exception{
		String[] result = null;
		result = (String[])Interpreter.eval(name);
		if(result == null) {
			throw new Exception("String array " + name + " was not extracted");
		}
		return(result);
	}

	public void RunRCode(String code)throws Exception{RunRCode(code, true, true, false, true);}   

	public void RunRCode(String code, boolean autoSplitCode)throws Exception{RunRCode(code, true, true, false, autoSplitCode);}

	public void RunRCode(String code, boolean echoStdErr, boolean echoStdOut, boolean interpret)throws Exception{
		RunRCode(code, echoStdErr, echoStdOut, interpret, true);
	}

	public void RunRCode(String code, boolean echoStdErr, boolean echoStdOut, boolean interpret, boolean autoSplitCode)
			throws Exception {		
		StringBuffer buf = new StringBuffer();
		buf.append(this.libraryCode);
		buf.append("\n\n");
		if(autoSplitCode){
			String[] codeArray = code.split(";");
			for(String s:codeArray){
				buf.append(s + ";\n");
			}
		}
		File Rcode=File.createTempFile("R2J", ".r");		
		//System.out.println(Rcode.getAbsolutePath());
		writeFile(Rcode, buf.toString());
		Runtime r=Runtime.getRuntime();		
		Process p=r.exec(new String[]{this.Rscript, Rcode.toString() } );		
		// Read process streams to prevent read blocks and deadlocks
		StreamReader srErr = new StreamReader(
				p.getErrorStream(), StreamReader.STDERR, this, echoStdErr , interpret);
		StreamReader srOut = new StreamReader(
				p.getInputStream(), StreamReader.STDOUT, this, echoStdOut, interpret);
		srErr.start();
		srOut.start();
		try{
			srOut.join();
		}catch (Exception e){
			//...
		}

		// Wait for process to finish, so that all R objects have been
		// populated, before returning to the caller which tries to access the
		// objects - obviates need for synchronisation too
		p.waitFor();
		Rcode.delete();
	}//RunRCode

	protected void registerField(String entry){this.fields.add(entry);}
	public ArrayList<String> getFieldList(){return(this.fields);}

	final private String loadMakeJavaLibrary(){
		StringBuffer buf=new StringBuffer();
		try{        				
			File file = new File("./lib/makejava.r");
			FileReader f = new FileReader(file);
			BufferedReader reader = new BufferedReader(f);
			char[] c=new char[1];
			int res;
			while(true){
				res=reader.read(c);
				if (res<0) {break;}
				buf.append(c);
			}
			reader.close();
		}catch (Exception e){
			e.printStackTrace();
		}

		return(buf.toString());
	}

	final private void writeFile(File f, String s){
		try{
			BufferedWriter writer=new BufferedWriter(new FileWriter(f));
			writer.write(s);
			writer.flush();
			writer.close();
		}catch (Exception e){
			System.out.println(e.toString());
		}
	}       
}