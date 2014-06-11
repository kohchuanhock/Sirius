/*==========================================================================
	  SiriusPSB - A Generic System for Analysis of Biological Sequences
	        http://compbio.ddns.comp.nus.edu.sg/~sirius/index.php
============================================================================
	  Copyright (C) 2007 by Chuan Hock Koh
	
	  This program is free software; you can redistribute it and/or
	  modify it under the terms of the GNU General Public
	  License as published by the Free Software Foundation; either
	  version 3 of the License, or (at your option) any later version.
	
	  This program is distributed in the hope that it will be useful,
	  but WITHOUT ANY WARRANTY; without even the implied warranty of
	  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
	  General Public License for more details.
	  	
	  You should have received a copy of the GNU General Public License
	  along with this program.  If not, see <http://www.gnu.org/licenses/>.
==========================================================================*/
package sirius.trainer.features;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import sirius.main.ApplicationData;
import sirius.main.FastaFileManipulation;
import sirius.trainer.main.ScoringMatrix;
import sirius.trainer.main.StatusPane;
import sirius.trainer.step1.Step1TableModel;
import sirius.trainer.step2.DefineFeaturePane;
import sirius.trainer.step3.SelectFeaturePane;
import sirius.utils.FastaFileReader;
import sirius.utils.FastaFormat;


//Generates Dataset1
public class GenerateFeatures {
	//FeatureTableModel featureTableModel;
	private ApplicationData applicationData;
	private JInternalFrame parent;
	private DecimalFormat df;
	//List<Feature> populationFeatures;
	private Integer positiveCount;// for chi-square of NNSearcher

	public GenerateFeatures(JInternalFrame parent,final ApplicationData applicationData,
			String GAOutputDirectory, 
			List<Feature> populationFeatures, String filename, List<FastaFormat> posFastaList, List<FastaFormat> negFastaList,
			Set<Integer> posIndexSet, Set<Integer> negIndexSet){		
		this.applicationData = applicationData;
		df = new DecimalFormat("0.#######");
		this.parent = parent;
		//Assumption: positiveStep1TableModel and negativeStep1TableModel files are in sequence
		//This method is called by GA in Sirius 3.1.0
		try{						  
	    	BufferedWriter outputFile;		    
		    outputFile = new BufferedWriter(new FileWriter(new File(GAOutputDirectory + File.separator + filename)));
			outputFile.write("@relation 'Settings @ " + applicationData.getWorkingDirectory() + 
					File.separator + "Step1_Settings.txt' ");
			outputFile.newLine();
			outputFile.newLine();
			outputFile.flush();
			for(int x = 0; x < populationFeatures.size(); x++){				
				outputFile.write("@attribute " + populationFeatures.get(x).getName() + " numeric");				
				outputFile.newLine();
				outputFile.flush();
			}			
			outputFile.write("@attribute Class {pos,neg}");
			outputFile.newLine();
			outputFile.newLine();
			outputFile.write("@data");
			outputFile.newLine();
			outputFile.newLine();
			outputFile.flush();
			generateARFF(outputFile,true, null,null,populationFeatures,filename, posFastaList, negFastaList, posIndexSet, negIndexSet);
			outputFile.close();			
		}catch(Exception e){
			e.printStackTrace();}
	}	

    public GenerateFeatures(JInternalFrame parent,final ApplicationData appData,final JTabbedPane tabbedPane,
    	final SelectFeaturePane selectFeaturePane, final JButton previousStepButton, final DefineFeaturePane defineFeaturePane){    	
    	//this method is called by generate feature button    	    	
    	this.applicationData = appData;    	    				
    	this.parent = parent;
    	
    	df = new DecimalFormat("0.####"); //set return scores to 4 decimal points.
	
    	if(applicationData.getWorkingDirectory() == null)
    		applicationData.setWorkingDirectory(System.getProperty("java.io.tmpdir"));
    	
		if(applicationData.getOneThread() == null){
			applicationData.setOneThread(new Thread(){
				public void run(){
					//Assumption: positiveStep1TableModel and negativeStep1TableModel files are in sequence
					try{
						if(previousStepButton != null)
							previousStepButton.setEnabled(false); 
						int positiveDataset1FromInt = applicationData.getPositiveDataset1FromField(); 
				    	int positiveDataset1ToInt = applicationData.getPositiveDataset1ToField();
				    	int negativeDataset1FromInt = applicationData.getNegativeDataset1FromField();
				    	int negativeDataset1ToInt = applicationData.getNegativeDataset1ToField(); 
				    	Step1TableModel positiveStep1TableModel = applicationData.getPositiveStep1TableModel();
				    	Step1TableModel negativeStep1TableModel = applicationData.getNegativeStep1TableModel();
				    	List<Feature> featureList = applicationData.getStep2FeatureTableModel().getData();
				    	BufferedWriter outputFile;					   
				    	BufferedWriter outputSequenceNameFile;				    	
						outputFile = new BufferedWriter(new FileWriter(
							applicationData.getWorkingDirectory() + File.separator + "Dataset1.arff"));
						outputSequenceNameFile = new BufferedWriter(new FileWriter(
								applicationData.getWorkingDirectory() + File.separator + "Dataset1.sequencesName"));
						outputFile.write("@relation 'Settings @ " + applicationData.getWorkingDirectory() + 
								File.separator + "Step1_Settings.txt' ");
						outputFile.newLine();
						outputFile.newLine();
						outputFile.flush();
						for(int x = 0; x < featureList.size(); x++){
							if(featureList.get(x).getType() != 'X')
								outputFile.write("@attribute " + featureList.get(x).getName() + " numeric");
							else
								outputFile.write("@attribute " + featureList.get(x).getName() + " String");
							outputFile.newLine();
							outputFile.flush();
						}
						if(positiveDataset1FromInt > 0 && negativeDataset1FromInt > 0)
							outputFile.write("@attribute Class {pos,neg}");
						else if(positiveDataset1FromInt > 0 && negativeDataset1FromInt == 0)
							outputFile.write("@attribute Class {pos}");
						else if(positiveDataset1FromInt == 0 && negativeDataset1FromInt > 0)
							outputFile.write("@attribute Class {neg}");
						outputFile.newLine();
						outputFile.newLine();
						outputFile.write("@data");
						outputFile.newLine();
						outputFile.newLine();
						outputFile.flush();						
						if(generateARFF(outputFile,positiveStep1TableModel,negativeStep1TableModel,
							positiveDataset1FromInt,positiveDataset1ToInt,negativeDataset1FromInt,
							negativeDataset1ToInt,false,outputSequenceNameFile,defineFeaturePane,featureList,null)){						
							outputFile.close();	
							outputSequenceNameFile.close();
							//When tabbedPane == null, it means that this method is called from by GA
							//Dataset1 successfully built, switch to step 3
							if(tabbedPane != null){
								tabbedPane.setEnabledAt(1,false);
								tabbedPane.setSelectedIndex(2);
							    tabbedPane.setEnabledAt(2,true);  
							}
							if(selectFeaturePane != null){
								selectFeaturePane.setDataset1Instances();
							}
							if(applicationData.getStatusPane() != null)
								applicationData.getStatusPane().setText("Generating Dataset1.arff..DONE!");						    
						}else{
							outputFile.close();	
							outputSequenceNameFile.close();
							applicationData.getStatusPane().setText("Interrupted!");
						}
					}catch(Exception e){
						//JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
						previousStepButton.setEnabled(true);
					}
					finally{
						applicationData.setOneThread(null);
						if(previousStepButton != null)
							previousStepButton.setEnabled(true);
					}
				}
			});
			applicationData.getOneThread().setPriority(Thread.MIN_PRIORITY);
			applicationData.getOneThread().start();
		}
		else{			
			JOptionPane.showMessageDialog(parent,"Can't generate feature now,\n"
	      		+ "currently busy with other IO","Generate Features", JOptionPane.WARNING_MESSAGE);
		}		
    }   
    
    public int getPositiveCount(){
    	return this.positiveCount.intValue();
    }
    
    public GenerateFeatures(ApplicationData applicationData, File queryFile, File databaseFile, String filename, JLabel statusLabel){
    	//This method is used by NNSearcher (Run Button)  for chi-square  	
    	try{
    		df = new DecimalFormat("0.####");
    		List<Feature> featureList = applicationData.getStep2FeatureTableModel().getData();
    		this.applicationData = applicationData;
    		FastaFileReader queryFastaFileReader = new FastaFileReader(queryFile.getAbsolutePath());
    		this.positiveCount = new Integer(queryFastaFileReader.size());    		
    		FastaFileReader databaseFastaFileReader = new FastaFileReader(databaseFile.getAbsolutePath());
    		BufferedWriter outputFile = new BufferedWriter(new FileWriter(filename));    		
    		outputFile.write("@relation '" + filename + "'");
			outputFile.newLine();
			outputFile.newLine();
			outputFile.flush();
			for(int x = 0; x < featureList.size(); x++){
				if(featureList.get(x).getType() != 'X')
					outputFile.write("@attribute " + featureList.get(x).getName() + " numeric");
				else
					outputFile.write("@attribute " + featureList.get(x).getName() + " String");
				outputFile.newLine();
				outputFile.flush();
			}
			outputFile.write("@attribute Class {pos,neg}");
			outputFile.newLine();
			outputFile.newLine();
			outputFile.write("@data");
			outputFile.newLine();
			outputFile.newLine();
    		for(int y = 0; y < queryFastaFileReader.size(); y++){
    			statusLabel.setText("Generate Features for " + filename + " " + y + " / " + queryFastaFileReader.size());
    			FastaFormat fastaFormat = queryFastaFileReader.getDataAt(y);    		         		 		
     			for(int x = 0; x < featureList.size(); x++){     				     			
     				outputFile.write(df.format(GenerateArff.getMatchCount(fastaFormat,
     						featureList.get(x),this.applicationData.getScoringMatrixIndex(),
							this.applicationData.getCountingStyleIndex(),
							this.applicationData.getScoringMatrix())) +							
							",");
				}									
     			outputFile.write("pos");
     			outputFile.newLine();
     			outputFile.flush();             	         	
    		}    		
    		for(int y = 0; y < databaseFastaFileReader.size(); y++){
    			statusLabel.setText("Generate Features for " + filename + " " + y + " / " + databaseFastaFileReader.size());
    			FastaFormat fastaFormat = databaseFastaFileReader.getDataAt(y);    		         		 		
     			for(int x = 0; x < featureList.size(); x++){     				     			
     				outputFile.write(df.format(GenerateArff.getMatchCount(fastaFormat,
     						featureList.get(x),this.applicationData.getScoringMatrixIndex(),
							this.applicationData.getCountingStyleIndex(),
							this.applicationData.getScoringMatrix())) +							
							",");
				}									
     			outputFile.write("neg");
     			outputFile.newLine();
     			outputFile.flush();             	         	
    		}    		
    		outputFile.close();
    	}catch(Exception e){
    		e.printStackTrace();
    		JOptionPane.showMessageDialog(null,"Exception Thrown","Exception", JOptionPane.ERROR_MESSAGE);
    	}
    }
    
    public GenerateFeatures(ApplicationData applicationData, List<Feature> featureList, List<FastaFormat> posFastaList, 
    		List<FastaFormat> negFastaList, String outputFilename){
    	//This method is used by Genetic Algorithm after running in step4
    	try{
    		df = new DecimalFormat("0.####");    		    		    		    		    	
    		BufferedWriter outputFile = new BufferedWriter(new FileWriter(outputFilename));    		
    		outputFile.write("@relation 'TempFile'");
			outputFile.newLine();
			outputFile.newLine();
			outputFile.flush();
			for(int x = 0; x < featureList.size(); x++){
				if(featureList.get(x).getType() != 'X')
					outputFile.write("@attribute " + featureList.get(x).getName() + " numeric");
				else
					outputFile.write("@attribute " + featureList.get(x).getName() + " String");
				outputFile.newLine();
				outputFile.flush();
			}
			outputFile.write("@attribute Class {pos,neg}");
			outputFile.newLine();
			outputFile.newLine();
			outputFile.write("@data");
			outputFile.newLine();
			outputFile.newLine();
    		for(int y = 0; y < posFastaList.size(); y++){    			
    			FastaFormat fastaFormat = posFastaList.get(y);
     			for(int x = 0; x < featureList.size(); x++){     				     			
     				outputFile.write(df.format(GenerateArff.getMatchCount(fastaFormat,
     						featureList.get(x),applicationData.getScoringMatrixIndex(),
     						applicationData.getCountingStyleIndex(),
							applicationData.getScoringMatrix())) +							
							",");
				}									
     			outputFile.write("pos");
     			outputFile.newLine();
     			outputFile.flush();             	         	
    		}    		
    		for(int y = 0; y < negFastaList.size(); y++){    			
    			FastaFormat fastaFormat = negFastaList.get(y);    		         		 		
     			for(int x = 0; x < featureList.size(); x++){     				     			
     				outputFile.write(df.format(GenerateArff.getMatchCount(fastaFormat,
     						featureList.get(x),applicationData.getScoringMatrixIndex(),
							applicationData.getCountingStyleIndex(),
							applicationData.getScoringMatrix())) +							
							",");
				}									
     			outputFile.write("neg");
     			outputFile.newLine();
     			outputFile.flush();             	         	
    		}    		
    		outputFile.close();
    	}catch(Exception e){
    		e.printStackTrace();
    		JOptionPane.showMessageDialog(null,"Exception Thrown","Exception", JOptionPane.ERROR_MESSAGE);
    	}
    }
    
    public static double getValue(FastaFormat fastaFormat, Feature feature, ApplicationData applicationData){
    	//This method is used by NNSearch - Constraints Matching
    	try{
    		DecimalFormat df = new DecimalFormat("0.####");        		 			     				     		
 			double value = Double.parseDouble(df.format(GenerateArff.getMatchCount(fastaFormat,feature,applicationData.getScoringMatrixIndex(),
						applicationData.getCountingStyleIndex(),applicationData.getScoringMatrix())));
 			return value;
    	}catch(Exception e){
    		e.printStackTrace();
    		JOptionPane.showMessageDialog(null,"Exception Thrown","Exception", JOptionPane.ERROR_MESSAGE);
    		return -1.99;
    	}
    }
    
    public GenerateFeatures(ApplicationData applicationData, File file, String filename, JLabel statusLabel, long currentRun, long totalRun){
    	//This method is used by NNSearcher (Run Button)    	
    	try{
    		df = new DecimalFormat("0.####");
    		List<Feature> featureList = applicationData.getStep2FeatureTableModel().getData();
    		this.applicationData = applicationData;
    		FastaFileReader fastaFileReader = new FastaFileReader(file.getAbsolutePath());
    		BufferedWriter outputFile = new BufferedWriter(new FileWriter(filename));    		
    		outputFile.write("@relation '" + filename + "'");
			outputFile.newLine();
			outputFile.newLine();
			outputFile.flush();
			for(int x = 0; x < featureList.size(); x++){
				if(featureList.get(x).getType() != 'X')
					outputFile.write("@attribute " + featureList.get(x).getName() + " numeric");
				else
					outputFile.write("@attribute " + featureList.get(x).getName() + " String");
				outputFile.newLine();
				outputFile.flush();
			}
			outputFile.write("@attribute Class {pos,neg}");
			outputFile.newLine();
			outputFile.newLine();
			outputFile.write("@data");
			outputFile.newLine();
			outputFile.newLine();
    		for(int y = 0; y < fastaFileReader.size(); y++){
    			if(statusLabel != null)
    				statusLabel.setText("Runs: " + currentRun + " / " + totalRun + "  Generate Features for " + filename +
    						" " + y + " / " + fastaFileReader.size());
    			FastaFormat fastaFormat = fastaFileReader.getDataAt(y);    		         		 		
     			for(int x = 0; x < featureList.size(); x++){     				     			
     				outputFile.write(df.format(GenerateArff.getMatchCount(fastaFormat,
     						featureList.get(x),this.applicationData.getScoringMatrixIndex(),
							this.applicationData.getCountingStyleIndex(),
							this.applicationData.getScoringMatrix())) +							
							",");
				}									
     			outputFile.write("pos");
     			outputFile.newLine();
     			outputFile.flush();             	         	
    		}    		
    		outputFile.close();
    	}catch(Exception e){
    		e.printStackTrace();
    		JOptionPane.showMessageDialog(null,"Exception Thrown","Exception", JOptionPane.ERROR_MESSAGE);
    	}
    }
    
    private List<FastaFormat> obtainSelectedList(List<FastaFormat> fastaList, Set<Integer> selectedSet){
    	List<FastaFormat> rList = new ArrayList<FastaFormat>();
    	for(int i = 0; i < fastaList.size(); i++){
    		if(selectedSet.contains(i)) rList.add(fastaList.get(i));
    	}
    	return rList;
    }
    
    private boolean generateARFF(BufferedWriter outputFile, boolean byGA, BufferedWriter outputSequenceNameFile,
    		DefineFeaturePane defineFeaturePane,
    		List<Feature> featureList, String filename, List<FastaFormat> posList, List<FastaFormat> negList,
    		Set<Integer> posIndexSet, Set<Integer> negIndexSet){    	
		try{
			StatusPane statusPane = applicationData.getStatusPane();			
			int lineCounter = 0;
			List<FastaFormat> selectedPosList = obtainSelectedList(posList, posIndexSet);
			List<FastaFormat> selectedNegList = obtainSelectedList(negList, negIndexSet);
			int totalSequences = posIndexSet.size() + negIndexSet.size();			
			int countingStyleIndex = applicationData.getCountingStyleIndex();
			int scoringIndex = applicationData.getScoringMatrixIndex();
			ScoringMatrix s = applicationData.getScoringMatrix();
			for(int y = 0; y < totalSequences && (defineFeaturePane == null || defineFeaturePane.stopGenerating == null || 
					defineFeaturePane.stopGenerating == false); y++){
				FastaFormat fastaFormat;
				if(y < selectedPosList.size()){
					fastaFormat = selectedPosList.get(y);
				}else{
					fastaFormat = selectedNegList.get(y - selectedPosList.size());
				}
				if(outputSequenceNameFile != null){
     				outputSequenceNameFile.write(fastaFormat.getHeader());
     				outputSequenceNameFile.newLine();
     			}
     			lineCounter++;  
     			if(statusPane != null){
	     			if(byGA == false)
	     				statusPane.setText("Generating Dataset1.arff.. @ " + lineCounter + " / " + totalSequences);
	     			else
	     				statusPane.setText("Generating " + filename + ".. @ " + lineCounter + " / " + totalSequences);
     			}
     			for(int x = 0; x < featureList.size() && 
     				(defineFeaturePane == null || defineFeaturePane.stopGenerating == null || 
     						defineFeaturePane.stopGenerating == false); x++){     				
     				Object obj = GenerateArff.getMatchCount(fastaFormat,
     						featureList.get(x),scoringIndex,countingStyleIndex,s);
     				if(obj.getClass().getName().equalsIgnoreCase("java.lang.Double"))
     					outputFile.write(df.format(obj) + ",");
     				else
     					outputFile.write(obj + ",");
				}					
     			if(y < selectedPosList.size()){
     				outputFile.write("pos");
     			}else{
     				outputFile.write("neg");
     			}
				outputFile.newLine();
				outputFile.flush();			
			}     		
     		if(defineFeaturePane != null){
     			defineFeaturePane.generateFeaturesButton.setText("Generate Features");
	     		if(defineFeaturePane.stopGenerating != null && defineFeaturePane.stopGenerating){
	     			JOptionPane.showMessageDialog(parent,"Generation of features is Interrupted","Interrupted",
	     					JOptionPane.INFORMATION_MESSAGE);
	     			return false;
	     		}
     		}
		}catch(Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(parent,"Error in generateFeatures","Error",JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
    }
    
    private boolean generateARFF(BufferedWriter outputFile, Step1TableModel positiveTableModel,
    		Step1TableModel negativeTableModel,int positiveFromInt,int positiveToInt,
    		int negativeFromInt,int negativeToInt,boolean byGA, BufferedWriter outputSequenceNameFile,DefineFeaturePane defineFeaturePane,
    		List<Feature> featureList, String filename){
		try{
			StatusPane statusPane = applicationData.getStatusPane();		
			FastaFileManipulation fastaFile = new FastaFileManipulation(positiveTableModel,
				negativeTableModel,positiveFromInt,positiveToInt,negativeFromInt,negativeToInt,
				applicationData.getWorkingDirectory());
			FastaFormat fastaFormat;
			int lineCounter = 0;
			String _class = "pos";
			int totalPosSequences = positiveToInt - positiveFromInt + 1;
			int totalNegSequences = negativeToInt - negativeFromInt + 1;
			int totalSequences = totalPosSequences + totalNegSequences;
			int countingStyleIndex = applicationData.getCountingStyleIndex();
			int scoringIndex = applicationData.getScoringMatrixIndex();
			ScoringMatrix s = applicationData.getScoringMatrix();
     		while((fastaFormat = fastaFile.nextSequence(_class))!=null && (defineFeaturePane == null || 
     				defineFeaturePane.stopGenerating == null || defineFeaturePane.stopGenerating == false)){
     			if(outputSequenceNameFile != null){
     				outputSequenceNameFile.write(fastaFormat.getHeader());
     				outputSequenceNameFile.newLine();
     			}
     			lineCounter++;  
     			if(statusPane != null){
	     			if(byGA == false)
	     				statusPane.setText("Generating Dataset1.arff.. @ " + lineCounter + " / " + totalSequences);
	     			else
	     				statusPane.setText("Generating " + filename + ".. @ " + lineCounter + " / " + totalSequences);
     			}
     			for(int x = 0; x < featureList.size() && 
     				(defineFeaturePane == null || defineFeaturePane.stopGenerating == null || 
     						defineFeaturePane.stopGenerating == false); x++){
     				Object obj = GenerateArff.getMatchCount(fastaFormat,
     						featureList.get(x),scoringIndex,countingStyleIndex,s);
     				if(obj.getClass().getName().equalsIgnoreCase("java.lang.Double"))
     					outputFile.write(df.format(obj) + ",");
     				else
     					outputFile.write(obj + ",");
				}									
				outputFile.write(_class);
				outputFile.newLine();
				outputFile.flush();
				if(lineCounter == totalPosSequences){
					_class = "neg";
				}
     		}     		
     		fastaFile.cleanUp();
     		if(defineFeaturePane != null){
     			defineFeaturePane.generateFeaturesButton.setText("Generate Features");
	     		if(defineFeaturePane.stopGenerating != null && defineFeaturePane.stopGenerating){
	     			JOptionPane.showMessageDialog(parent,"Generation of features is Interrupted","Interrupted",
	     					JOptionPane.INFORMATION_MESSAGE);
	     			return false;
	     		}
     		}
		}catch(Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(parent,"Error in generateFeatures","Error",JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
    }
}

/*class Worker extends SwingWorker<Object, Object> {
	private FastaFormat fastaFormat;
	private Feature feature;
	private int scoringIndex;
	private int countingStyleIndex;
	private ScoringMatrix s;
	private List<Object> objectList;
	private int objectListIndex;
	public Worker(FastaFormat fastaFormat, Feature feature,int scoringIndex,int countingStyleIndex,ScoringMatrix s,List<Object> objectList,
			int objectListIndex){
		this.fastaFormat = fastaFormat;
		this.feature = feature;
		this.scoringIndex = scoringIndex;
		this.countingStyleIndex = countingStyleIndex;
		this.s = s;
		this.objectList = objectList;
		this.objectListIndex = objectListIndex;
	}
	
    @Override
    public Object doInBackground() {    	
    	Object obj = GenerateArff.getMatchCount(fastaFormat,feature,scoringIndex,countingStyleIndex,s);
    	this.objectList.set(this.objectListIndex, obj);
        return obj;
    }
}*/
