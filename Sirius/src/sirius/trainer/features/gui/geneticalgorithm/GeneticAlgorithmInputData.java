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
package sirius.trainer.features.gui.geneticalgorithm;

import javax.swing.JOptionPane;

import sirius.main.ApplicationData;
import sirius.main.FastaFileManipulation;
import sirius.trainer.step1.Step1TableModel;
import sirius.utils.FastaFormat;

public class GeneticAlgorithmInputData {	
	//applicationData.isLocationIndexMinusOne == true if +1_Index is -1 else false
	int maxLength;		//include both side for +1 index not -1
	int minLength;		//include both side for +1 index not -1			
	//Note that all these are only applicable when the +1_index is not -1
	//upstream
	int upstreamMax;
	int upstreamMin;	
	//downstream
	int downstreamMax;
	int downstreamMin;	
	boolean isIndexMinus1;
	
	public GeneticAlgorithmInputData(ApplicationData applicationData){
		maxLength = Integer.MIN_VALUE;
		minLength = Integer.MAX_VALUE;
		upstreamMax = Integer.MIN_VALUE;
		downstreamMax = Integer.MIN_VALUE;
		upstreamMin = Integer.MAX_VALUE;
		downstreamMin = Integer.MAX_VALUE;		
		
		this.isIndexMinus1 = applicationData.isLocationIndexMinusOne;
		
		//Read through Dataset1 and get some basic statistical data
		int positiveFromInt = applicationData.getPositiveDataset1FromField(); 
    	int positiveToInt = applicationData.getPositiveDataset1ToField();
    	int negativeFromInt = applicationData.getNegativeDataset1FromField();
    	int negativeToInt = applicationData.getNegativeDataset1ToField(); 
    	Step1TableModel positiveTableModel = applicationData.getPositiveStep1TableModel();
    	Step1TableModel negativeTableModel = applicationData.getNegativeStep1TableModel();
    	
    	try{			
			FastaFileManipulation fastaFile = new FastaFileManipulation(positiveTableModel,
				negativeTableModel,positiveFromInt,positiveToInt,negativeFromInt,negativeToInt,
				applicationData.getWorkingDirectory());
			FastaFormat fastaFormat;
			int lineCounter = 0;
			String _class = "pos";
			int totalPosSequences = positiveToInt - positiveFromInt + 1;					
     		while((fastaFormat = fastaFile.nextSequence(_class))!=null){
     			lineCounter++;
     			if(applicationData.isLocationIndexMinusOne == true){
     				//+1_index == -1
     				int sequenceLength = fastaFormat.getSequenceLength();     				
     				if(sequenceLength > maxLength) maxLength = sequenceLength;
     				if(sequenceLength < minLength) minLength = sequenceLength;
     			}else{
     				//+1_index != -1
     				int upstreamSequenceLength = fastaFormat.getUpstreamSequenceLength();
     				int downstreamSequenceLength = fastaFormat.getDownstreamSequenceLength();
     				     				
     				if(upstreamSequenceLength > maxLength) maxLength = upstreamSequenceLength;
     				if(upstreamSequenceLength < minLength) minLength = upstreamSequenceLength;     				
     				if(downstreamSequenceLength > maxLength) maxLength = downstreamSequenceLength;
     				if(downstreamSequenceLength < minLength) minLength = downstreamSequenceLength;     				
     				if(upstreamSequenceLength > upstreamMax) upstreamMax = upstreamSequenceLength;
     				if(upstreamSequenceLength < upstreamMin) upstreamMin = upstreamSequenceLength;     				
     				if(downstreamSequenceLength > downstreamMax) downstreamMax = downstreamSequenceLength;
     				if(downstreamSequenceLength < downstreamMin) downstreamMin = downstreamSequenceLength;     				     			
     			}
				if(lineCounter == totalPosSequences)
					_class = "neg";
     		}
     		fastaFile.cleanUp();     		     	
		}catch(Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,"Error in Gathering Dataset1 Data","Error",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public int getMaxLength(){ return maxLength; }	
	public int getMinLength(){ return minLength; }	
	public int getUpstreamMax(){ return upstreamMax; }	
	public int getUpstreamMin(){ return upstreamMin; }	
	public int getDownstreamMax(){ return downstreamMax; }	
	public int getDownstreamMin(){ return downstreamMin; }
	
	public int getWindowMin(){
		if(this.isIndexMinus1)
			return 0;
		else
			return this.upstreamMax * -1;
	}
	public int getWindowMax(){
		if(this.isIndexMinus1)
			return this.maxLength;
		else
			return this.downstreamMax;
	}
}
