package sirius.utils;

import java.util.*;
import javax.swing.*;

public class ClassifierResults {
	private ArrayList<String> classifierList;
	private ArrayList<String> settingsList;
	private ArrayList<String> resultsList;	
	
	public ClassifierResults(boolean rangeNeeded, int clusterNum){
		//clusterNum would be normally 0
		//clusterNum is only > 0 for Summary (Either EqualWeightage or Absolute)
		classifierList = new ArrayList<String>();
		classifierList.add("Classifier: ");
		if(clusterNum == 0){
			classifierList.add("Training Data: ");
			classifierList.add("Time Used: ");
		}
		
		settingsList = new ArrayList<String>();
		//if(clusterNum == 0)
		//	settingsList.add("Testing Data: ");				
		settingsList.add("Threshold: ");
		if(rangeNeeded)
			settingsList.add("Range: ");			
		
		resultsList = new ArrayList<String>();
		resultsList.add("Total +ve Instances: ");
		resultsList.add("Total -ve Instances: ");
		resultsList.add("TP predictions: ");
		resultsList.add("FN predictions: ");
		resultsList.add("TN predictions: ");
		resultsList.add("FP predictions: ");
		resultsList.add("MCC: ");
		resultsList.add("Total Correct Predictions: ");
		resultsList.add("Total Incorrect Predictions: ");
		resultsList.add("Precision(wrt +ve): ");
		resultsList.add("Precision(wrt -ve): ");
		resultsList.add("SN: ");
		resultsList.add("SP: ");						
		resultsList.add("Approx (SN == SP): SN = ");
		resultsList.add("Max MCC = "); 
		resultsList.add("Area Under Curve for ROC: ");
		
		if(clusterNum == 0)
			resultsList.add("Total Time Used: ");		
	}
	
	public void updateList(ArrayList<String> list, String header, String data){
		//find the line with the same header and update the data		
		boolean found = false;
		for(int x = 0; x < list.size(); x++){			
			if(list.get(x).indexOf(header) != -1){
				list.remove(x);				
				list.add(x, header + data);
				found = true;
			}
		}		
		if(found == false && header.indexOf("Range: ") == -1){//range is okie to be not found			
			for(int x = 0; x < list.size(); x++)
				System.err.println(list.get(x) + "/");			
			throw new RuntimeException("header not found in list - (ClassifierResults::updateList)");			
		}		
	}
	
	public void updateDisplayTextArea(JTextArea displayTextArea, boolean show, int clusterNum){
		this.updateDisplayTextArea(displayTextArea, show, clusterNum, null);
	}
	
	public void updateDisplayTextArea(JTextArea displayTextArea, boolean show, int clusterNum, 
			String inputFilename){		
		displayTextArea.setText(""); //clear it
		if(inputFilename != null){
			outputText(displayTextArea, inputFilename + "\n");
		}
		if(show){
			outputText(displayTextArea,"=== Classifiers ===");
			outputText(displayTextArea,"\n\n");
			for(int x = 0; x < classifierList.size(); x++)
				outputText(displayTextArea,classifierList.get(x) + "\n");
			outputText(displayTextArea,"\n");
		}
		
		outputText(displayTextArea,"=== Settings ===");
		outputText(displayTextArea,"\n\n");	
		for(int x = 0; x < settingsList.size(); x++)
			outputText(displayTextArea,settingsList.get(x) + "\n");		
		outputText(displayTextArea,"\n");
		
		outputText(displayTextArea,"=== Results ===");
		outputText(displayTextArea,"\n\n");
		for(int x = 0; x < resultsList.size(); x++){			
			if(clusterNum > 0 && x == 12 + clusterNum)
				outputText(displayTextArea,"\n");
			if(x == 2 || x == 7 || x == 9 || x == 11 || (x == 14 && clusterNum == 0))				
				outputText(displayTextArea,"\n");						
			if(show == false && x+1 == resultsList.size())
				continue;
			outputText(displayTextArea,resultsList.get(x) + "\n");
		}
		outputText(displayTextArea,"\n");			
	}
	
	private void outputText(JTextArea displayTextArea,String output){
    	final int maxLengthPerLine = 150;        	
    	while(output.length() > maxLengthPerLine){
    		String temp = output.substring(0,maxLengthPerLine);    		
    		displayTextArea.append(temp + "\n");
    		output = output.substring(maxLengthPerLine);		
    	}
    	displayTextArea.append(output);
    }
	
	public ArrayList<String> getClassifierList(){
		return classifierList;
	}
	
	public ArrayList<String> getSettingsList(){				
		return settingsList;
	}
	
	public ArrayList<String> getResultsList(){
		return resultsList;
	}
	
	public String getSNSP(){
		for(int x = 0; x < this.resultsList.size(); x++){			
			if(this.resultsList.get(x).indexOf("Approx (SN == SP): SN = ") != -1){
				return this.resultsList.get(x);
			}
		}
		return null;
	}		
	
	public double getThreshold(){
		double threshold = -1;				
		for(int x = 0; x < this.settingsList.size(); x++){			
			if(this.settingsList.get(x).indexOf("Threshold: ") != -1){
				try{
					String temp = this.settingsList.get(x).substring(
							this.settingsList.get(x).indexOf("Threshold: ") + ("Threshold: ").length());
					if(temp.length() > 0)
						threshold = Double.parseDouble(temp);
					return threshold;
				}catch(Exception e){e.printStackTrace();}				
			}
		}				
		return threshold;
	}
}