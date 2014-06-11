/*
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
/* This class will be used to quench a sequence and find all the non-overlapping regions that are above a certain cutoff
 * Input: The sequence, The matrix, The cutoff
 * 
 * Algorithm is as follows
 * Si = Sum of sequence from 0 to i
 * 
 * Mi = Min Sl (where i>l>=0)
 * 
 * Bi = Max(Bi-1 , Si - Mi)
 * */


package sirius.misc.sequencevisualizer;

import javax.swing.*;
import java.text.DecimalFormat;
import java.util.*;

public class SequenceQuencher{
	ArrayList<SequenceResult> results;//Used to draw all the max boxes
	double maxValue;	
	int plusOneIndex;
	int sequenceLength;
	double globalViewMaxValue;
	double globalViewMinValue;
	int windowSize;
	int stepSize;
	ArrayList<Double> globalViewResults;//Used to draw the dots
	boolean cumulative;
	boolean invertValues;//used for computeGlobalView
	JTextField refLine;
	
	public SequenceQuencher(String sequence, Hashtable<Character, Double> matrix, double valueCutoff, 
			int lengthCutoff, boolean showCalculation, 
			int plusOneIndex){		
		this(sequence, matrix, valueCutoff, lengthCutoff, showCalculation, plusOneIndex, 1,1, true, false, null);
	}
	
	public SequenceQuencher(String sequence, Hashtable<Character, Double> matrix, double valueCutoff, 
			int lengthCutoff, boolean showCalculation, 
			int plusOneIndex, int windowSize, int stepSize, boolean cumulative, boolean invertValues, JTextField refLine){
		this.plusOneIndex = plusOneIndex;
		this.sequenceLength = sequence.length();
		this.results = new ArrayList<SequenceResult>();		
		this.maxValue = 0;		
		this.cumulative = cumulative;
		this.windowSize = windowSize;
		this.stepSize = stepSize;
		this.invertValues = invertValues;
		this.refLine = refLine;
		if(matrix == null){
			System.err.println("matrix is null in sirius/misc/sequencevisualizer/SequenceQuencher.java");
			return;
		}
		
		quenchSequence(sequence, matrix, valueCutoff, lengthCutoff, 0);
		
		this.globalViewResults = new ArrayList<Double>();
		computeGlobalView(sequence, matrix, windowSize);
		
		//OVERWRITE - I dun want to show DEBUG for time being
		showCalculation = false;
		//Below is purely for DEBUG purpose
		if(showCalculation){
			System.out.println("Sequence: " + sequence);
			System.out.print("Matrix: ");
			for(int x = 0; x < sequence.length(); x++){
				if(matrix.containsKey(sequence.charAt(x)))
					System.out.print(matrix.get(sequence.charAt(x)));
				else
					System.out.print("0.0");
				if(x+1 != sequence.length())
					System.out.print(",");
			}
			System.out.println();
			for(int x = 0; x < results.size(); x++){
				System.out.println(results.get(x));
			}
		}
	}
	
	public int getGlobalViewSize(){
		return this.globalViewResults.size();
	}
	
	public int getStepSize(){
		return this.stepSize;
	}
	
	public String getRefLine(){
		return this.refLine.getText();
	}
	
	public List<Double> getGlobalViewResults(){
		return this.globalViewResults;
	}
	
	private void computeGlobalView(String sequence, Hashtable<Character, Double> matrix, int windowSize){
		this.globalViewMaxValue = Double.NEGATIVE_INFINITY;
		this.globalViewMinValue = Double.POSITIVE_INFINITY;
		double current = 0.0;			
		for(int x = 0; x < sequence.length(); x += this.stepSize){			
			double temp = 0.0;
			for(int y = 0; y < windowSize && y + x < sequence.length(); y++){
				if(matrix.containsKey(sequence.charAt(x + y)) == false)
					continue;
				if(this.invertValues == false)					
					temp += matrix.get(sequence.charAt(x + y));				
				else
					temp += (matrix.get(sequence.charAt(x + y)) * -1);				
				
			}
			if(this.cumulative)
				current+=temp;
			else
				current = temp;
			this.globalViewResults.add(current);
			if(current > this.globalViewMaxValue)
				this.globalViewMaxValue = current;
			if(current < this.globalViewMinValue)
				this.globalViewMinValue = current;
		}	
		DecimalFormat df = new DecimalFormat("0.###");
		if(this.refLine != null && this.refLine.getText().length() == 0)
			this.refLine.setText(df.format((this.globalViewMaxValue + this.globalViewMinValue) / 2));		
	}
	
	public double getGlobalViewMaxValue(){
		return this.globalViewMaxValue;
	}
	
	public double getGlobalViewMinValue(){
		return this.globalViewMinValue;
	}
	
	public void quenchSequence(String sequence, Hashtable<Character, Double> matrix, double valueCutoff, int lengthCutoff, int indexShifter){
		if(sequence.length() <= 0)
			return;		
		//First run to compute Mi 
		ArrayList<MList> mList = new ArrayList<MList>();
		double minValue = 0.0;		
		double currentValue = 0.0;
		int index = -1;		
		for(int x = 0; x < sequence.length(); x++){			
			if(matrix.containsKey(sequence.charAt(x)))
				currentValue += matrix.get(sequence.charAt(x));			
			if(currentValue <= minValue){
				minValue = currentValue;
				index = x;
			}							
			mList.add(new MList(minValue, index));
		}		
		//This run is to find the max value of sequence
		//Need to generalize later to find all that are greater than cutoff
		double currentMax = Double.NEGATIVE_INFINITY;
		double currentSum = 0.0;
		int indexFrom = 0;
		int indexTo = 0;
		for(int x = 0; x < sequence.length(); x++){			
			if(matrix.containsKey(sequence.charAt(x)))
				currentSum += matrix.get(sequence.charAt(x));									
			if(x==0){
				currentMax = currentSum;
			}else{			
				if(currentMax < (currentSum - mList.get(x).getValue())){
					currentMax = currentSum - mList.get(x).getValue();
					indexFrom = mList.get(x).getIndex() + 1;
					indexTo = x;
				}
			}
		}			
		if(currentMax > valueCutoff){			
			if(currentMax > this.maxValue)
				this.maxValue = currentMax;
			int length = indexTo - indexFrom + 1;
			if(length > lengthCutoff)
				results.add(new SequenceResult(currentMax, indexFrom + indexShifter, indexTo + indexShifter));
			quenchSequence(sequence.substring(0, indexFrom), matrix, valueCutoff, lengthCutoff, indexShifter);
			quenchSequence(sequence.substring(indexTo + 1), matrix, valueCutoff, lengthCutoff, indexTo + 1 + indexShifter);
		}
		
	}
	
	public double getMaxValue(){		
		return this.maxValue;
	}
	
	public int getNumOfRegion(){//this can also be used for # of region
		return this.results.size();
	}
	
	public SequenceResult get(int index){
		return this.results.get(index);
	}
	
	public double getGobalView(int index){
		return this.globalViewResults.get(index);
	}
	
	public int getTopValueIndex(){
		if(this.results.size() == 0)
			return -1;
		int topValueIndex = 0;
		double topValue = this.results.get(0).getValue();
		for(int x = 1; x < this.results.size(); x++){
			double currentValue = this.results.get(x).getValue();
			if(currentValue > topValue){
				topValue = currentValue;
				topValueIndex = x;
			}
		}
		return topValueIndex;
	}
	
	public List<SequenceResult> getResults(){
		return this.results;
	}
	
	public double getTopValue(){		
		return this.results.get(getTopValueIndex()).getValue();
	}
	
	public int getTopValueSizeAbsolute(){
		return this.results.get(getTopValueIndex()).length();
	}
	
	public double getTopValueSizeRelative(){
		return (this.results.get(getTopValueIndex()).length() + 0.0) / (this.sequenceLength + 0.0);
	}
	
	public int getTopValueLocationAbsolute(){
		int midPoint = this.results.get(getTopValueIndex()).getMidPoint();
		if(this.plusOneIndex == -1)
			return midPoint;
		else if(midPoint >= this.plusOneIndex)
			return midPoint - this.plusOneIndex + 1;
		else
			return midPoint - this.plusOneIndex;
	}
	
	public double getTopValueLocationRelative(){
		return (getTopValueLocationAbsolute() + 0.0) / (this.sequenceLength + 0.0);
	}
	
	public double getTotalValue(){
		double totalValue = 0.0;
		for(int x = 0; x < this.results.size(); x++){
			totalValue += this.results.get(x).getValue();
		}
		return totalValue;
	}
	
	public int getTotalSizeAbsolute(){
		int totalSize = 0;
		for(int x = 0; x < this.results.size(); x++){
			totalSize += this.results.get(x).length();
		}
		return totalSize;
	}
	
	public double getTotalSizeRelative(){
		return (getTotalSizeAbsolute() + 0.0) / (this.sequenceLength + 0.0);
	}
	
	public double averageValue(){
		return (getTotalValue() + 0.0) / (this.results.size() + 0.0); 
	}
	
	public double averageSizeAbsolute(){
		return (getTotalSizeAbsolute() + 0.0) / (this.results.size() + 0.0);			
	}
	
	public double averageSizeRelative(){
		return (getTotalSizeRelative() + 0.0) / (this.results.size() + 0.0);
	}
	
	public int getSequenceLength(){
		return this.sequenceLength;
	}
}


class MList{
	double value;
	int l;	
	
	public MList(double value, int l){
		this.value = value;
		this.l = l;
	}
	
	public double getValue(){
		return this.value;
	}
	
	public int getIndex(){
		return this.l;
	}
}