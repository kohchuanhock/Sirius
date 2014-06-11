package sirius.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

public class Arff {	
	/*
	 * Class Methods
	 */

	public static Instances getAsInstances(String fileLocation){
		return getAsInstances(fileLocation, true);
	}
	
	public static Instances getAsInstances(String fileLocation, boolean deleteAfterUse){
		try{			
			return getAsInstances(new File(fileLocation), deleteAfterUse);
		}catch(Exception e){e.printStackTrace(); return null;}
	}
	
	public static Instances getAsInstances(File file){
		//Changed to default to be true => always delete after use
		return getAsInstances(file, true);
	}
	
	public static Instances getAsInstances(File file, boolean deleteAfterUse){
		try{			
			//Read in the Arff file
			FileReader reader = new FileReader(file);
			ArffReader arff = new ArffReader(new BufferedReader(reader));
			reader.close();
			if(deleteAfterUse) file.delete();
//			else file.deleteOnExit();
			Instances inst = arff.getData();
			//Assumes that the last attribute is the class attributes
			inst.setClassIndex(inst.numAttributes() - 1);
			return inst;
		}catch(Exception e){
			System.err.println("Error at Arff.getAsInstances");
			e.printStackTrace(); 
			return null;
		}
	}
	
	public static File writeToFileAsArff(Table data){
		try{			
			return writeToFileAsArff(data, data.getRowName(), 
				File.createTempFile("Polaris_Reader_Generated_", ".arff"), null);
		}catch(IOException e){e.printStackTrace(); return null;}
	}
	
	public static File writeToFileAsArff(Table data, List<Integer> selectedList){
		try{			
			return writeToFileAsArff(data, data.getRowName(), 
				File.createTempFile("Polaris_Reader_Generated_", ".arff"), selectedList);
		}catch(IOException e){e.printStackTrace(); return null;}
	}
	
	public static File writeToFileAsArff(double[] classList, List<String> featureNameList, 
			List<double[]> featureValueList){
		/*
		 * Designed for used by Regulus
		 */
		try{
			File file = File.createTempFile("Regulus_", ".arff");
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write("@RELATION Regulus"); output.newLine();
			for(String name:featureNameList){
				output.write("@ATTRIBUTE " + name.replace(" ", "_") + " NUMERIC");
				output.newLine();
			}
			output.write("@ATTRIBUTE class {pos,neg}");
			output.newLine();
			output.write("@DATA");
			output.newLine();
			for(int i = 0; i < classList.length; i++){
				for(double[] d:featureValueList){
					output.write(d[i] + ",");					
				}
				if(classList[i] == 0.0){
					output.write("pos");
				}else if(classList[i] == 1.0){
					output.write("neg");
				}else{
					output.close();
					throw new Error("Unhandled Class Value: " + classList[i]);
				}
				output.newLine();
			}
			output.close();
			return file;
		}catch(Exception e){e.printStackTrace(); return null;}
	}
	
	public static File writeToFileAsArff(Table data, List<String> probeIDList,			
			File outputFileLocation, List<Integer> selectedList){
		try{
			BufferedWriter output = new BufferedWriter(new FileWriter(outputFileLocation));
			output.write("@RELATION ProbeData");
			output.newLine();
			if(selectedList == null){
				for(String probe:probeIDList){
					output.write("@ATTRIBUTE " + probe.replace(" ", "_") + " NUMERIC");
					output.newLine();
				}
			}else{
				for(int x = 0; x < selectedList.size(); x++){
					output.write("@ATTRIBUTE " + probeIDList.get(selectedList.get(x)).replace(" ", "_") + 
							" NUMERIC");
					output.newLine();
				}
			}
			output.write("@ATTRIBUTE class {pos,neg}");
			output.newLine();		
			output.write("@DATA");
			output.newLine();
			Arff.writeData(data, output, "pos", selectedList, true);
			Arff.writeData(data, output, "neg", selectedList, false);
			output.close();					
			return outputFileLocation;
		}catch(Exception e){e.printStackTrace(); return null;}
	}
	
	public static File writeToFileAsArff(List<String> probeIDList,
			Hashtable<String, double[]> probeID2PosSubsetData,
			Hashtable<String, double[]> probeID2NegSubsetData){
		try{
			return writeToFileAsArff(probeIDList, probeID2PosSubsetData, probeID2NegSubsetData, 
				File.createTempFile("Polaris_Reader_Generated_", ".arff"), null);
		}catch(IOException e){e.printStackTrace(); return null;}
	}
	
	public static File writeToFileAsArff(List<String> probeIDList,
			Hashtable<String, double[]> probeID2PosSubsetData,
			Hashtable<String, double[]> probeID2NegSubsetData, List<Integer> selectedList){
		try{
			return writeToFileAsArff(probeIDList, probeID2PosSubsetData, probeID2NegSubsetData, 
				File.createTempFile("Polaris_Reader_Generated_", ".arff"), selectedList);
		}catch(IOException e){e.printStackTrace(); return null;}
	}
	
	public static File writeToFileAsArff(List<String> probeIDList,
			Hashtable<String, double[]> probeID2PosSubsetData,
			Hashtable<String, double[]> probeID2NegSubsetData, 
			File outputFileLocation, List<Integer> selectedList){
		try{
			BufferedWriter output = new BufferedWriter(new FileWriter(outputFileLocation));
			output.write("@RELATION ProbeData");
			output.newLine();
			if(selectedList == null){
				for(String probe:probeIDList){
					output.write("@ATTRIBUTE " + probe.replace(" ", "_") + " NUMERIC");
					output.newLine();
				}
			}else{
				for(int x = 0; x < selectedList.size(); x++){
					output.write("@ATTRIBUTE " + probeIDList.get(selectedList.get(x)).replace(" ", "_") + 
							" NUMERIC");
					output.newLine();
				}
			}			
			output.write("@ATTRIBUTE class {pos,neg}");
			output.newLine();		
			output.write("@DATA");
			output.newLine();
			Arff.writeData(probeIDList, probeID2PosSubsetData, output, "pos", selectedList);
			Arff.writeData(probeIDList, probeID2NegSubsetData, output, "neg", selectedList);
			output.close();					
			return outputFileLocation;
		}catch(Exception e){e.printStackTrace(); return null;}
	}
	
	public static File writeToFileAsArff(List<Integer> classSampleList, 
			List<String> featureNameList, List<List<Double>> dataListList){
		List<Integer> rankList = new ArrayList<Integer>();
		for(int x = 0; x < featureNameList.size(); x++){
			rankList.add(x);
		}
		return Arff.writeToFileAsArff(classSampleList, featureNameList, dataListList, rankList);
	}

	public static File writeToFileAsArff(List<Integer> classSampleList, 
			List<String> featureNameList, List<List<Double>> dataListList, List<Integer> rankList){
		/*
		 * This method is created for project with Alok
		 * Note: OutterList of featureValueList are the features indexing
		 * InnerList of featureValueList are the sample indexing
		 */		
		try{
			File file = File.createTempFile("Alok", ".arff");			
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			/*
			 * Prepare the header
			 */
			output.write("@RELATION Alok");
			output.newLine();
			for(String s:featureNameList){
				output.write("@ATTRIBUTE " + s + " NUMERIC");
				output.newLine();
			}
			String classString = "";
			for(int x = 0; x < classSampleList.size(); x++){
				if(x != 0) classString += ",";
				classString += x + "";
			}
			output.write("@ATTRIBUTE class {" + classString + "}");
			output.newLine();
			output.newLine();
			output.write("@DATA");
			output.newLine();
			/*
			 * Prepare the sampleClasses values
			 */
			String[] sampleClassList = new String[dataListList.get(0).size()];
			int index = 0;			
			for(int x = 0; x < classSampleList.size(); x++){
				int num = classSampleList.get(x);
				for(int y = 0; y < num; y++){
					sampleClassList[index++] = x + "";
				}
			}
			if(rankList.size() != dataListList.size()){
				output.close();
				throw new Error("RankList size not equal to Feature Size. Please rank all features.");
			}
			for(int x = 0; x < dataListList.get(0).size(); x++){
				for(int y = 0; y < rankList.size(); y++){
					output.write(dataListList.get(rankList.get(y)).get(x) + ",");
				}
				output.write(sampleClassList[x]);
				output.newLine();
			}
			output.close();			
			return file;
		}catch(Exception e){e.printStackTrace(); return null;}
	}		
	
	/*	
	 * Local Methods
	 */	
	
	private static void writeData(List<String> probeIDList, Hashtable<String, double[]> hashtable, 
			BufferedWriter output, String classString, List<Integer> selectedList) throws Exception{
		int sampleSize = hashtable.get(probeIDList.get(0)).length;
		for(int x = 0; x < sampleSize; x++){
			if(selectedList == null){
				for(String probe:probeIDList){
					output.write(hashtable.get(probe)[x] + ",");					
				}			
				output.write(classString);
				output.newLine();
			}else{
				for(int y = 0; y < selectedList.size(); y++){
					output.write(hashtable.get(probeIDList.get(selectedList.get(y)))[x] + ",");					
				}			
				output.write(classString);
				output.newLine();
			}
		}
	}
	
	private static void writeData(Table data,
			BufferedWriter output, String classString, List<Integer> selectedList, boolean isPos) 
			throws Exception{
		
		Set<Integer> selectedSampleSet;
		if(isPos) selectedSampleSet = data.getPosSampleSet();
		else selectedSampleSet = data.getNegSampleSet();
		for(int y = 0; y < data.getHeaderList().size(); y++){
			if(selectedSampleSet.contains(y) == false) continue;				
			if(selectedList == null){
				for(int x = 0; x < data.getRowName().size(); x++){
					output.write(data.getData().get(x, y) + ",");
				}
				output.write(classString);
				output.newLine();
			}else{
				for(int x = 0; x < selectedList.size(); x++){
					output.write(data.getData().get(selectedList.get(x), y) + ",");
				}
				output.write(classString);
				output.newLine();
			}
		}
	}
}
