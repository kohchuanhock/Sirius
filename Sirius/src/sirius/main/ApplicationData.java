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

package sirius.main;

import java.util.List;

import javax.swing.JOptionPane;

import sirius.trainer.main.ScoringMatrix;
import sirius.trainer.main.StatusPane;
import sirius.trainer.step1.Step1TableModel;
import sirius.trainer.step2.FeatureTableModel;
import sirius.utils.FastaFormat;
import sirius.utils.PredictionStats;
import weka.classifiers.Classifier;
//import weka.core.*;
import weka.core.Instances;

public class ApplicationData {	  	
	public boolean terminateThread;//Used to terminate thread
	private StatusPane statusPane;//the status pane below the trainer application
	private Thread oneThread;//One thread policy - means only one extra thread for whole trainer application	
	private int leftMostPosition;//most upstream position for features
	private int rightMostPosition;//most downstream position for features		
	private Instances dataset1Instances;//keep the instances of dataset1 after having the features generated
	private Instances dataset2Instances;//keep the instances of dataset2 after having the features generated
	private int totalDataset1Sequences;//number of sequences in dataset1 - this set of dataset would be used for genetic algorithm	
	private int totalDataset2Sequences;//number of sequences in dataset2
	private int totalDataset3Sequences;//number of sequences in dataset3
	private int setUpstream;//upstream parameter for classifier two
	private int setDownstream;//downstream parameter for classifier two
	private int availableUpstreamForClassifierTwo;//how far can classifier two upstream be
	private int availableDownstreamForClassifierTwo;//how far can classifier two downstream be
	private Classifier classifierOne;//classifier one
	private Classifier classifierTwo;//classifier two
	private String classifierOneSettings;//classifier one settings
	private String classifierTwoSettings;//classifier two settings
			
	private int dataset1PosFromFieldInt;//sequence number from (inclusive) for +ve dataset1
	private int dataset1PosToFieldInt;//sequence number to (inclusive) for +ve dataset1
	private int dataset2PosFromFieldInt;//sequence number from (inclusive) for +ve dataset2
	private int dataset2PosToFieldInt;//sequence number to (inclusive) for +ve dataset2
	private int dataset3PosFromFieldInt;//sequence number from (inclusive) for +ve dataset3
	private int dataset3PosToFieldInt;//sequence number to (inclusive) for +ve dataset3
	
	private int dataset1NegFromFieldInt;//sequence number from (inclusive) for -ve dataset1
	private int dataset1NegToFieldInt;//sequence number to (inclusive) for -ve dataset1
	private int dataset2NegFromFieldInt;//sequence number from (inclusive) for -ve dataset2
	private int dataset2NegToFieldInt;//sequence number to (inclusive) for -ve dataset2
	private int dataset3NegFromFieldInt;//sequence number from (inclusive) for -ve dataset3
	private int dataset3NegToFieldInt;//sequence number to (inclusive) for -ve dataset3
	
	private String workingDirectory;//where all the output files will be written to
	
	private Step1TableModel posTableModel;//keeps all the files in the positive table
	private Step1TableModel negTableModel;//keeps all the files in the negative table
	
	private FeatureTableModel featureTableModel;//keeps all the features 
	
	//public boolean hasLocationIndexBeenSet;	
	public boolean isLocationIndexMinusOne;//true if the +1_Index is -1, false otherwise
		
	private PredictionStats classifierOneStats;//used to show the classifier one statistics
	private PredictionStats classifierTwoStats;//used to show the classifier one statistics
	
	private String sequenceType;//store what is the sequence type in the files - DNA or Protein
	private int scoringMatrixIndex;//store what scoring matrix used - Identity, BLOSUM62 or Structure-Derived
	private int countingStyleIndex;//store what counting style used - +1 or +score
	
	private ScoringMatrix scoringMatrix;//store the scoring matrix used so that dun have to reinitialise whenever needed
	
	private int longestSequenceLength;
	private int shortestSequenceLength;
	private int totalSequenceLength;
	private int longestDownstream;
	private int shortestDownstream;
	private int totalDownstream;
	private int longestUpstream;
	private int shortestUpstream;
	private int totalUpstream;
	
    public ApplicationData(StatusPane statusPane) {
    	this.terminateThread = false;
    	this.oneThread = null;
    	this.statusPane = statusPane;   
    		 
    	//this.leftMostPosition = 0;
    	//this.rightMostPosition = 0;
    	    	    	
    	this.dataset1Instances = null;  
    	this.dataset2Instances = null;  	        	    	
    	
    	this.totalDataset1Sequences = -1;
    	this.totalDataset2Sequences = -1;
    	this.totalDataset3Sequences = -1;
    	
    	this.setUpstream = -1;
    	this.setDownstream = -1;
    	
    	this.availableUpstreamForClassifierTwo = -1;
    	this.availableDownstreamForClassifierTwo = -1;
    	
    	this.classifierOne = null;
    	this.classifierTwo = null;
    	
    	this.classifierOneSettings = "";
    	this.classifierTwoSettings = "";
    	
    	this.dataset1PosFromFieldInt = -1;
		this.dataset1PosToFieldInt = -1;
		this.dataset2PosFromFieldInt = -1;
		this.dataset2PosToFieldInt = -1;
		this.dataset3PosFromFieldInt = -1;
		this.dataset3PosToFieldInt = -1;
		
		this.dataset1NegFromFieldInt = -1;
		this.dataset1NegToFieldInt = -1;
		this.dataset2NegFromFieldInt = -1;
		this.dataset2NegToFieldInt = -1;
		this.dataset3NegFromFieldInt = -1;
		this.dataset3NegToFieldInt = -1;
		
		this.workingDirectory = "";
		
		this.posTableModel = null;
		this.negTableModel = null;
		
		this.featureTableModel = null;
		
		//this.hasLocationIndexBeenSet = false;
		this.isLocationIndexMinusOne = true;//here, it does not matter true or false
		this.sequenceType = "NOT_SET";
		this.scoringMatrixIndex = -1;
		this.countingStyleIndex = -1;
		
		this.scoringMatrix = new ScoringMatrix();		
		
		this.longestSequenceLength = -1;
		this.shortestSequenceLength = 999999999;
		this.totalSequenceLength = 0;
		this.longestDownstream = -1;
		this.shortestDownstream = 999999999;
		this.totalDownstream = 0;
		this.longestUpstream = -1;
		this.shortestUpstream = 999999999;
		this.totalUpstream = 0;
    }
    public ApplicationData(ApplicationData appData){
    	//this.oneThread = appData.getOneThread();//shallow copy
    	this.oneThread = null;//start afresh    	
    	this.statusPane = appData.getStatusPane();//shallow copy
    	this.leftMostPosition = appData.getLeftMostPosition();//deep copy
    	this.rightMostPosition = appData.getRightMostPosition();//deep copy    	
    	if(appData.getDataset1Instances()!=null)
    		this.dataset1Instances = new Instances(appData.getDataset1Instances());//deep copy
    	else
    		this.dataset1Instances = null;
    	//Dun do deep copy here because at this point, dataset2Instances has not been created yet
    	//this.dataset2Instances = new Instances(appData.getDataset2Instances());
    	this.totalDataset1Sequences = appData.getTotalSequences(1);//deep copy
    	this.totalDataset2Sequences = appData.getTotalSequences(2);//deep copy
    	this.totalDataset3Sequences = appData.getTotalSequences(3);//deep copy
    	this.setUpstream = appData.getSetUpstream();//deep copy
    	this.setDownstream = appData.getSetDownstream();//deep copy
    	this.availableUpstreamForClassifierTwo = appData.getAvailableUpstreamForClassifierTwo();//deep copy
    	this.availableDownstreamForClassifierTwo = appData.getAvailableDownstreamForClassifierTwo();//deep copy
    	try{
    		this.classifierOne = Classifier.makeCopy(appData.getClassifierOne());//deep copy
    		this.classifierTwo = Classifier.makeCopy(appData.getClassifierTwo());//deep copy
    	}catch(Exception e){
    		JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
    		e.printStackTrace();
    	}
    	this.classifierOneSettings = "";
    	this.classifierTwoSettings = "";
    	
    	this.dataset1PosFromFieldInt = appData.getPositiveDataset1FromField();
		this.dataset1PosToFieldInt = appData.getPositiveDataset1ToField();
		this.dataset2PosFromFieldInt = appData.getPositiveDataset2FromField();
		this.dataset2PosToFieldInt = appData.getPositiveDataset2ToField();
		this.dataset3PosFromFieldInt = appData.getPositiveDataset3FromField();
		this.dataset3PosToFieldInt = appData.getPositiveDataset3ToField();
		
		this.dataset1NegFromFieldInt = appData.getNegativeDataset1FromField();
		this.dataset1NegToFieldInt = appData.getNegativeDataset1ToField();
		this.dataset2NegFromFieldInt = appData.getNegativeDataset2FromField();
		this.dataset2NegToFieldInt = appData.getNegativeDataset2ToField();
		this.dataset3NegFromFieldInt = appData.getNegativeDataset3FromField();
		this.dataset3NegToFieldInt = appData.getNegativeDataset3ToField();
		
		this.workingDirectory = appData.getWorkingDirectory();
		
		this.posTableModel = appData.getPositiveStep1TableModel();
		this.negTableModel = appData.getNegativeStep1TableModel();
		
		this.featureTableModel = appData.getStep2FeatureTableModel();
		
		//this.hasLocationIndexBeenSet = appData.hasLocationIndexBeenSet;
		this.isLocationIndexMinusOne = appData.isLocationIndexMinusOne;
		this.sequenceType = appData.getSequenceType();
		this.scoringMatrixIndex = appData.getScoringMatrixIndex();
		this.countingStyleIndex = appData.getCountingStyleIndex();
		
		this.scoringMatrix = appData.getScoringMatrix();
		this.terminateThread = false;
		
		this.longestSequenceLength = appData.getLongestSequenceLength();
		this.shortestSequenceLength = appData.getShortestSequenceLength();
		this.totalSequenceLength = appData.getTotalSequenceLength();
		this.longestDownstream = appData.getLongestDownstream();
		this.shortestDownstream = appData.getShortestDownstream();
		this.totalDownstream = appData.getTotalDownstream();
		this.longestUpstream = appData.getLongestUpstream();
		this.shortestUpstream = appData.getShortestUpstream();
		this.totalUpstream = appData.getTotalUpstream();		
    }  
    public ApplicationData() {
    	this((StatusPane)null);
	}
	public void resetLengthInformation(){
    	this.longestSequenceLength = -1;
		this.shortestSequenceLength = 999999999;
		this.totalSequenceLength = 0;
		this.longestDownstream = -1;
		this.shortestDownstream = 999999999;
		this.totalDownstream = 0;
		this.longestUpstream = -1;
		this.shortestUpstream = 999999999;
		this.totalUpstream = 0;		
    }
    public int getLongestSequenceLength(){
    	return this.longestSequenceLength;
    }
    public void setLongestSequenceLength(int longestSequenceLength){
    	this.longestSequenceLength = longestSequenceLength;
    }
    public int getShortestSequenceLength(){
    	return this.shortestSequenceLength;
    }
    public void setShortestSequenceLength(int shortestSequenceLength){
    	this.shortestSequenceLength = shortestSequenceLength;
    }
    public void addToTotalSequenceLength(int sequenceLength){
    	this.totalSequenceLength += sequenceLength;
    }
    public int getTotalSequenceLength(){
    	return this.totalSequenceLength;
    }
    public int meanSequenceLength(){
    	return this.totalSequenceLength / this.totalDataset1Sequences;
    }
    public void setLongestDownstream(int longestDownstream){
    	this.longestDownstream = longestDownstream;
    }
    public int getLongestDownstream(){
    	return this.longestDownstream;
    }
    public void setShortestDownstream(int shortestDownstream){
    	this.shortestDownstream = shortestDownstream;
    }
    public int getShortestDownstream(){
    	return this.shortestDownstream;
    }
    public void addToTotalDownstream(int downstream){
    	this.totalDownstream += downstream;
    }
    public int getTotalDownstream(){
    	return this.totalDownstream;
    }
    public int meanDownstream(){
    	return this.totalDownstream / this.totalDataset1Sequences;
    }
    public void setLongestUpstream(int longestUpstream){
    	this.longestUpstream = longestUpstream;
    }
    public int getLongestUpstream(){
    	return this.longestUpstream;
    }
    public void setShortestUpstream(int shortestUpstream){
    	this.shortestUpstream = shortestUpstream;
    }
    public int getShortestUpstream(){
    	return this.shortestUpstream;
    }   
    public void addToTotalUpstream(int upstream){
    	this.totalUpstream += upstream;
    }
    public int getTotalUpstream(){
    	return this.totalUpstream;
    }
    public int meanUpstream(){
    	return this.totalUpstream / this.totalDataset1Sequences;
    }
    
    public ScoringMatrix getScoringMatrix(){
    	return this.scoringMatrix;    
    }
    
    public int getScoringMatrixIndex(){
    	return this.scoringMatrixIndex;    
    }   
    	
    public void setScoringMatrixIndex(int scoringMatrixIndex){
    	this.scoringMatrixIndex = scoringMatrixIndex;
    	this.scoringMatrix.setMatrix(scoringMatrixIndex);
    }
    	
    public int getCountingStyleIndex(){
    	return this.countingStyleIndex;
    }
    
    public void setCountingStyleIndex(int countingStyleIndex){
    	this.countingStyleIndex = countingStyleIndex;    	
    }
    	
    public void setClassifierOneStats(PredictionStats classifierOneStats){
    	this.classifierOneStats = classifierOneStats;
    }
    
    public void setClassifierTwoStats(PredictionStats classifierTwoStats){
    	this.classifierTwoStats = classifierTwoStats;
    }
    
    public PredictionStats getClassifierOneStats(){
    	return this.classifierOneStats;
    }
    
    public PredictionStats getClassifierTwoStats(){
    	return this.classifierTwoStats;
    }
    	    	    
    public Classifier getClassifierOne(){
    	return this.classifierOne;    	
    }
    
    public void setClassifierOne(Classifier classifier){
    	try{
    		this.classifierOne = Classifier.makeCopy(classifier);//deep copy
    		
    	}catch(Exception e){
    		JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
    		e.printStackTrace();
    	}
    }
    
    public void setClassifierOneSettings(String settings){
    	this.classifierOneSettings = settings;
    }
    
    public String getClassifierOneSettings(){
    	return this.classifierOneSettings;
    }
    
    public void setClassifierTwoSettings(String settings){
    	this.classifierTwoSettings = settings;
    }
    
    public String getClassifierTwoSettings(){
    	return this.classifierTwoSettings;
    }
    
    public Classifier getClassifierTwo(){
    	return this.classifierTwo;
    }
        
    public void setClassifierTwo(Classifier classifier){
    	try{
    		this.classifierTwo = Classifier.makeCopy(classifier);//deep copy    		
    	}catch(Exception e){
    		JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
    		e.printStackTrace();
    	}
    }            
    
    public void setDatasetsValue(int dataset1PosFrom,int dataset1PosTo,int dataset2PosFrom,int dataset2PosTo,
    	int dataset3PosFrom,int dataset3PosTo,int dataset1NegFrom,int dataset1NegTo,int dataset2NegFrom,
    	int dataset2NegTo,int dataset3NegFrom,int dataset3NegTo){
    		this.dataset1PosFromFieldInt = dataset1PosFrom;
			this.dataset1PosToFieldInt = dataset1PosTo;
			this.dataset2PosFromFieldInt = dataset2PosFrom;
			this.dataset2PosToFieldInt = dataset2PosTo;
			this.dataset3PosFromFieldInt = dataset3PosFrom;
			this.dataset3PosToFieldInt = dataset3PosTo;
			
			this.dataset1NegFromFieldInt = dataset1NegFrom;
			this.dataset1NegToFieldInt = dataset1NegTo;
			this.dataset2NegFromFieldInt = dataset2NegFrom;
			this.dataset2NegToFieldInt = dataset2NegTo;
			this.dataset3NegFromFieldInt = dataset3NegFrom;
			this.dataset3NegToFieldInt = dataset3NegTo;
			
			this.totalDataset1Sequences = dataset1PosTo - dataset1PosFrom + 1 + 
				dataset1NegTo - dataset1NegFrom + 1;
			if(dataset1PosTo == dataset1PosFrom && dataset1PosTo == 0)
				this.totalDataset1Sequences--;
			if(dataset1NegTo == dataset1NegFrom && dataset1NegTo == 0)
				this.totalDataset1Sequences--;
				
			this.totalDataset2Sequences = dataset2PosTo - dataset2PosFrom + 1 + 
				dataset2NegTo - dataset2NegFrom + 1;
			if(dataset2PosTo == dataset2PosFrom && dataset2PosTo == 0)
				this.totalDataset2Sequences--;
			if(dataset2NegTo == dataset2NegFrom && dataset2NegTo == 0)
				this.totalDataset2Sequences--;
			
			this.totalDataset3Sequences = dataset3PosTo - dataset3PosFrom + 1 + 
				dataset3NegTo - dataset3NegFrom + 1;
			if(dataset3PosTo == dataset3PosFrom && dataset3PosTo == 0)
				this.totalDataset3Sequences--;
			if(dataset3NegTo == dataset3NegFrom && dataset3NegTo == 0)
				this.totalDataset3Sequences--;
    }    		
    public int getSequenceNumber(boolean _class, int dataset){
    	if(_class){//pos
    		switch(dataset){
    			case 1: return dataset1PosToFieldInt - dataset1PosFromFieldInt + 1;
    			case 2: return dataset2PosToFieldInt - dataset2PosFromFieldInt + 1;
    			case 3: return dataset3PosToFieldInt - dataset3PosFromFieldInt + 1;
    			default: return -1;
    		}
    	}else{//neg
    		switch(dataset){
    			case 1: return dataset1NegToFieldInt - dataset1NegFromFieldInt + 1;
    			case 2: return dataset2NegToFieldInt - dataset2NegFromFieldInt + 1;
    			case 3: return dataset3NegToFieldInt - dataset3NegFromFieldInt + 1;
    			default: return -1;
    		}
    	}
    } 
     
    public int getPositiveDataset1FromField(){
    	return dataset1PosFromFieldInt;
    }
    
    public int getPositiveDataset1ToField(){
    	return dataset1PosToFieldInt;
    }
    
    public int getNegativeDataset1FromField(){
    	return dataset1NegFromFieldInt;
    }
    
    public int getNegativeDataset1ToField(){
    	return dataset1NegToFieldInt;
    }
    public int getPositiveDataset2FromField(){
    	return dataset2PosFromFieldInt;
    }
    
    public int getPositiveDataset2ToField(){
    	return dataset2PosToFieldInt;
    }
    
    public int getNegativeDataset2FromField(){
    	return dataset2NegFromFieldInt;
    }
    
    public int getNegativeDataset2ToField(){
    	return dataset2NegToFieldInt;
    }
    
     public int getPositiveDataset3FromField(){
    	return dataset3PosFromFieldInt;
    }
    
    public int getPositiveDataset3ToField(){
    	return dataset3PosToFieldInt;
    }
    
    public int getNegativeDataset3FromField(){
    	return dataset3NegFromFieldInt;
    }
    
    public int getNegativeDataset3ToField(){
    	return dataset3NegToFieldInt;
    }
    
    public void setWorkingDirectory(String workingDirectory){
    	this.workingDirectory = workingDirectory;
    }  
    	 
    public void setPositiveStep1TableModel(Step1TableModel posTableModel){
    	this.posTableModel = posTableModel;
    }
    
    public void setNegativeStep1TableModel(Step1TableModel negTableModel){
    	this.negTableModel = negTableModel;
    }				

	public void setStep2FeatureTableModel(FeatureTableModel featureTableModel){
		this.featureTableModel = featureTableModel;
	}				
    
    public Step1TableModel getPositiveStep1TableModel(){
    	return posTableModel;
    }
    
    public Step1TableModel getNegativeStep1TableModel(){
    	return negTableModel;
    }
    
    public FeatureTableModel getStep2FeatureTableModel(){
    	return featureTableModel;
    }
    
    public String getWorkingDirectory(){
    	return workingDirectory;
    }
    
    public StatusPane getStatusPane(){
    	return statusPane;
    }        
    	    
    public void setLeftMostPosition(int leftMostPosition){
    	this.leftMostPosition = leftMostPosition;
    }
    public void setRightMostPosition(int rightMostPosition){
    	this.rightMostPosition = rightMostPosition;
    }
    public int getLeftMostPosition(){
    	return leftMostPosition;
    }
    public int getRightMostPosition(){
    	return rightMostPosition;
    } 
    public void setOneThread(Thread thread){
   		this.oneThread = thread;
    }
    public Thread getOneThread(){
    	return this.oneThread;
    }   
   	public void setDataset1Instances(Instances dataset1Instances){
   		this.dataset1Instances = dataset1Instances;
   	}
   	public void setDataset2Instances(Instances dataset2Instances){
   		this.dataset2Instances = dataset2Instances;
   	}
   	public Instances getDataset1Instances(){
   		return this.dataset1Instances;
   	}   	   	
   	public Instances getDataset2Instances(){
   		return this.dataset2Instances;
   	}
   	public int getTotalSequences(int dataset){
   		switch(dataset){
   			case 1: return totalDataset1Sequences;
   			case 2: return totalDataset2Sequences;
   			case 3: return totalDataset3Sequences;
   		}
   		return -1;
   	}
   	public void setTotalSequences(int dataset,int totalSequences){
   		switch(dataset){
   			case 1: this.totalDataset1Sequences = totalSequences; break;
   			case 2: this.totalDataset2Sequences = totalSequences; break;
   			case 3: this.totalDataset3Sequences = totalSequences; break;
   		}
   	}
   	public int getSetUpstream(){
   		return this.setUpstream;
   	}
   	public void setSetUpstream(int setUpstream){
   		this.setUpstream = setUpstream;
   	}
   	public int getSetDownstream(){
   		return this.setDownstream;
   	}
   	public void setSetDownstream(int setDownstream){
   		this.setDownstream = setDownstream;
   	}
   	public void setAvailableUpstreamForClassifierTwo(int availableUpstreamForClassifierTwo){
   		this.availableUpstreamForClassifierTwo = availableUpstreamForClassifierTwo;
   	}
   	public int getAvailableUpstreamForClassifierTwo(){
   		return this.availableUpstreamForClassifierTwo;
   	}
   	public void setAvailableDownstreamForClassifierTwo(int availableDownstreamForClassifierTwo){
   		this.availableDownstreamForClassifierTwo = availableDownstreamForClassifierTwo;
   	}
   	public int getAvailableDownstreamForClassifierTwo(){
   		return this.availableDownstreamForClassifierTwo;
   	}
   	public String getSequenceType(){
   		return sequenceType;
   	}
   	public void setSequenceType(String sequenceType){
   		this.sequenceType = sequenceType;
   	}
   	public void setStatusPane(StatusPane s){
   		this.statusPane = s;
   	}
   	
   	public static void obtainDataset1FastaSequences(ApplicationData applicationData, 
			List<FastaFormat> posList, List<FastaFormat> negList){
		int positiveDataset1FromInt = applicationData.getPositiveDataset1FromField(); 
    	int positiveDataset1ToInt = applicationData.getPositiveDataset1ToField();
    	int negativeDataset1FromInt = applicationData.getNegativeDataset1FromField();
    	int negativeDataset1ToInt = applicationData.getNegativeDataset1ToField(); 
    	Step1TableModel positiveStep1TableModel = applicationData.getPositiveStep1TableModel();
    	Step1TableModel negativeStep1TableModel = applicationData.getNegativeStep1TableModel();
    	    	
    	FastaFileManipulation fastaFile = new FastaFileManipulation(positiveStep1TableModel,
    			negativeStep1TableModel,positiveDataset1FromInt,positiveDataset1ToInt,negativeDataset1FromInt,negativeDataset1ToInt,
				applicationData.getWorkingDirectory());
    	FastaFormat fastaFormat;
    	int lineCounter = 0;
		String _class = "pos";
		int totalPosSequences = positiveDataset1ToInt - positiveDataset1FromInt + 1;
    	while((fastaFormat = fastaFile.nextSequence(_class)) != null){
    		if(_class.contains("pos")){
    			posList.add(fastaFormat);
    		}else{
    			negList.add(fastaFormat);
    		}
 			lineCounter++;
			if(lineCounter == totalPosSequences){
				_class = "neg";
			}
 		}     		
 		fastaFile.cleanUp();
	}
}