package sirius.utils;

import java.util.List;



public class R {
	private RCaller caller = new RCaller();

	public R(){				
		if(this.caller.getRscriptExecutableFile() == null){
			this.caller.setRScriptExecutableFile(Utils.selectFile("Please select RScript(exe)"));
		}
	}
	
	public void runCode(StringBuffer s){
		runCode(s, false, true, true);
	}
	
	public void runCode(StringBuffer s, boolean echoStdOut, boolean echoStdErr, boolean interpret){
		this.runCode(s, echoStdOut, echoStdErr, interpret, false);
	}
	
	public void runCode(StringBuffer s, boolean showCodes){
		runCode(s, false, true, true, showCodes);
	}
	
	public void runCode(StringBuffer s, boolean echoStdOut, boolean echoStdErr, boolean interpret, boolean showCodes){
		this.runCode(s, echoStdOut, echoStdErr, interpret, showCodes, true);
	}
	
	public void runCode(StringBuffer s, boolean echoStdOut, boolean echoStdErr, boolean interpret, boolean showCodes, boolean autoSplitCode){		
		try{
			if(showCodes){
				String[] test = s.toString().split(";");
				for(String t:test){System.out.println(t + ";");}
			}
			this.caller.RunRCode(s.toString(), echoStdErr, echoStdOut, interpret, autoSplitCode);
		}catch(Exception e){e.printStackTrace();}
	}

	public List<String> getReturnedList(){
		return this.caller.getFieldList();
	}
}
