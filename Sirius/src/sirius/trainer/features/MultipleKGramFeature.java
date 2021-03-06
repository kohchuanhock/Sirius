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

import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

import sirius.trainer.main.ScoringMatrix;
import sirius.trainer.step2.Physiochemical2;
import sirius.utils.FastaFormat;

public class MultipleKGramFeature extends Feature{
	private ArrayList<String> featureList;
	private ArrayList<Integer> mistakeList;	
	private ArrayList<Integer> minGapList;
	private ArrayList<Integer> maxGapList;	
	private int physiochemical2Int;
		
	public MultipleKGramFeature(String name){
		super(null,null,name.charAt(0));
		StringTokenizer st = new StringTokenizer(name,"_");
		char type = st.nextToken().charAt(0);
		int numberOfKGram = Integer.parseInt(st.nextToken());
		String[] kField = new String[numberOfKGram];
		int[] xField = new int[numberOfKGram];
		int[] yField = new int[numberOfKGram];
		int[] zField = new int[numberOfKGram];
		for(int x = 0; x < numberOfKGram; x++){
			kField[x] = st.nextToken();
			xField[x] = Integer.parseInt(st.nextToken());
			if(x+1 < numberOfKGram){
				yField[x] = Integer.parseInt(st.nextToken());
				zField[x] = Integer.parseInt(st.nextToken());
			}
		}
		boolean isPercentage = Boolean.parseBoolean(st.nextToken());
		int windowFrom = Integer.parseInt(st.nextToken());
		int windowTo = Integer.parseInt(st.nextToken());		
		if(type == 'I' || type == 'J'){
			//physiochemical2
			int physiochemical2Index = Integer.parseInt(st.nextToken());
			setValues(type,kField,xField,yField,zField,windowFrom,windowTo,physiochemical2Index,isPercentage);
		}else{
			//normal and physiochemical			
			setValues(type,kField,xField,yField,zField,windowFrom,windowTo,isPercentage);
		}
	}
	
	private void setValues(char type,String[] kField,int[] xField,int[] yField,int[] zField,
			int windowFrom,int windowTo,int physiochemical2Index, boolean isPercentage){
		if(type == 'I' || type == 'J'){
			this.featureList = new ArrayList<String>();
			this.mistakeList = new ArrayList<Integer>();
			this.minGapList = new ArrayList<Integer>();
			this.maxGapList = new ArrayList<Integer>();
			this.type = type;
			this.name = type + "_" + kField.length;
			this.details  = "";
			for(int x = 0; x < kField.length; x++){
				this.name += "_" + kField[x] + "_" + xField[x];
				this.details += kField[x] + "," + xField[x] + " mistakes";
				if(x != kField.length - 1){
					this.name += "_" + yField[x] + "_" + zField[x];
					this.minGapList.add(yField[x]);
					this.maxGapList.add(zField[x]);
					this.details += " with " + yField[x] + " to " + zField[x] + " gaps from ";
				}							
				this.featureList.add(kField[x]);
				this.mistakeList.add(xField[x]);			
			}
			if(isPercentage)
				this.details += ", PercentageWindow(" + windowFrom + "," + windowTo + "), " + Physiochemical2.indexToName(physiochemical2Index);
			else
				this.details += ", Window(" + windowFrom + "," + windowTo + "), " + Physiochemical2.indexToName(physiochemical2Index);
			if(type == 'J')						
				this.details += ", value relative to length";
			this.name += "_" + isPercentage + "_" + windowFrom + "_" + windowTo + "_" + physiochemical2Index;
			this.windowFrom = windowFrom;
			this.windowTo = windowTo;
			this.box = false;
			this.physiochemical2Int = physiochemical2Index;
			this.isPercentage = isPercentage;
		}else
			throw new Error("Unsupported type: " + type);
	}
	
	//This constructor is for physiochemical2
	public MultipleKGramFeature(char type,String[] kField,int[] xField,int[] yField,int[] zField,
			int windowFrom,int windowTo,int physiochemical2Index, boolean isPercentage){
		super(null,null,type);
		setValues(type,kField,xField,yField,zField,windowFrom,windowTo,physiochemical2Index,isPercentage);
	}
	
	private void setValues(char type,String[] kField,int[] xField,int[] yField,int[] zField,int windowFrom,
			int windowTo, boolean isPercentage){
		this.featureList = new ArrayList<String>();
		this.mistakeList = new ArrayList<Integer>();
		this.minGapList = new ArrayList<Integer>();
		this.maxGapList = new ArrayList<Integer>();
		this.name = type + "_" + kField.length;
		this.details  = "";
		this.windowFrom = windowFrom;
		this.windowTo = windowTo;		
		this.isPercentage = isPercentage;
		for(int x = 0; x < kField.length; x++){
			this.name += "_" + kField[x] + "_" + xField[x];
			this.details += kField[x] + "," + xField[x] + " mistakes";
			if(x != kField.length - 1){
				this.name += "_" + yField[x] + "_" + zField[x];
				this.minGapList.add(yField[x]);
				this.maxGapList.add(zField[x]);
				this.details += " with " + yField[x] + " to " + zField[x] + " gaps from ";
			}							
			this.featureList.add(kField[x]);
			this.mistakeList.add(xField[x]);			
		}					
		if(this.isPercentage)
			this.details += ", PercentageWindow(" + windowFrom + "," + windowTo + ")";
		else
			this.details += ", Window(" + windowFrom + "," + windowTo + ")";
		if(type == 'N')
			this.details += ", value relative to length";		
		else if(type == 'U')
			this.details += ", Physiochemical";
		else
			this.details += ", Physiochemical, value relative to length";			
		this.name += "_" + this.isPercentage + "_" + windowFrom + "_" + windowTo;
	}
	
	//this constructor is for normal and physiochemical
	public MultipleKGramFeature(char type,String[] kField,int[] xField,int[] yField,int[] zField,int windowFrom,
			int windowTo, boolean isPercentage){
		super(null, null, type);
		setValues(type,kField,xField,yField,zField,windowFrom,windowTo,isPercentage);
	}
	
	public static MultipleKGramFeature loadSettings(String line, boolean isLocationIndexMinusOne, char type){
		int featureNum = Integer.parseInt(line.substring(line.indexOf("FeatureNum: ") + 
				("FeatureNum: ").length(),line.indexOf("Feature1: ") - 1));
		String[] featureList = new String[featureNum];
		int[] mistakeList = new int[featureNum];
		int[] minGapList = new int[featureNum - 1];
		int[] maxGapList = new int[featureNum - 1];
		int windowFrom = -1;
		int windowTo = -1;		
		int physiochemical2Index = -1;//used by only physiochemical2
		boolean isPercentage = false;
		if(type == 'M' || type == 'N' || type == 'U' || type == 'T'){//multiple-kgram absolute value			
			for(int x = 0; x < featureNum; x++){				
				featureList[x] = line.substring(line.indexOf("Feature"+(x+1)+": ") + 
				("Feature"+(x+1)+": ").length(),line.indexOf("Mistake_Allowed"+(x+1)+": ") - 1);
				if(x != featureNum - 1){
					mistakeList[x] = Integer.parseInt(line.substring(line.indexOf("Mistake_Allowed"+(x+1)+": ")+
						("Mistake_Allowed"+(x+1)+": ").length(),line.indexOf("Min_Gap"+(x+1)+": ") - 1));
					minGapList[x] = Integer.parseInt(line.substring(line.indexOf("Min_Gap"+(x+1)+": ") + 
						("Min_Gap"+(x+1)+": ").length(),line.indexOf("Max_Gap"+(x+1)+": ") - 1));
					maxGapList[x] = Integer.parseInt(line.substring(line.indexOf("Max_Gap"+(x+1)+": ") + 
						("Max_Gap"+(x+1)+": ").length(),line.indexOf("Feature"+(x+2)+": ") - 1));
				}else{
					mistakeList[x] = Integer.parseInt(line.substring(line.indexOf("Mistake_Allowed"+(x+1)+": ")+
						("Mistake_Allowed"+(x+1)+": ").length(),line.indexOf("isPercentage: ") - 1));
					isPercentage = Boolean.parseBoolean(line.substring(line.indexOf("isPercentage: ") + 
							("isPercentage: ").length(),line.indexOf("Window_From: ") - 1));
					windowFrom = Integer.parseInt(line.substring(line.indexOf("Window_From: ") + 
						("Window_From: ").length(),line.indexOf("Window_To: ") - 1));
					windowTo = Integer.parseInt(line.substring(line.indexOf("Window_To: ") + 
						("Window_To: ").length()));					
				}				
			}
			if(isLocationIndexMinusOne == true && windowFrom < 0)
				return null;
			else{
				return new MultipleKGramFeature(type,featureList,mistakeList,minGapList,maxGapList,windowFrom,windowTo,isPercentage);				 
			}			
		}else if(type == 'I' || type == 'J'){//multiple-kgram absolute value
			for(int x = 0; x < featureNum; x++){				
				featureList[x] = line.substring(line.indexOf("Feature"+(x+1)+": ") + 
				("Feature"+(x+1)+": ").length(),line.indexOf("Mistake_Allowed"+(x+1)+": ") - 1);
				if(x != featureNum - 1){
					mistakeList[x] = Integer.parseInt(line.substring(line.indexOf("Mistake_Allowed"+(x+1)+": ")+
						("Mistake_Allowed"+(x+1)+": ").length(),line.indexOf("Min_Gap"+(x+1)+": ") - 1));
					minGapList[x] = Integer.parseInt(line.substring(line.indexOf("Min_Gap"+(x+1)+": ") + 
						("Min_Gap"+(x+1)+": ").length(),line.indexOf("Max_Gap"+(x+1)+": ") - 1));
					maxGapList[x] = Integer.parseInt(line.substring(line.indexOf("Max_Gap"+(x+1)+": ") + 
						("Max_Gap"+(x+1)+": ").length(),line.indexOf("Feature"+(x+2)+": ") - 1));
				}else{
					mistakeList[x] = Integer.parseInt(line.substring(line.indexOf("Mistake_Allowed"+(x+1)+": ")+
						("Mistake_Allowed"+(x+1)+": ").length(),line.indexOf("isPercentage: ") - 1));
					isPercentage = Boolean.parseBoolean(line.substring(line.indexOf("isPercentage: ") + 
							("isPercentage: ").length(),line.indexOf("Window_From: ") - 1));
					windowFrom = Integer.parseInt(line.substring(line.indexOf("Window_From: ") + 
						("Window_From: ").length(),line.indexOf("Window_To: ") - 1));
					windowTo = Integer.parseInt(line.substring(line.indexOf("Window_To: ") + 
						("Window_To: ").length(),line.indexOf("Physiochemical2: ") - 1));
					physiochemical2Index = Integer.parseInt(line.substring(line.indexOf("Physiochemical2: ") + ("Physiochemical2: ").length()));
				}				
			}
			if(isLocationIndexMinusOne == true && windowFrom < 0)
				return null;
			else
				return new MultipleKGramFeature(type,featureList,mistakeList,minGapList,maxGapList,windowFrom,windowTo,
						physiochemical2Index,isPercentage);			
		}else
			throw new Error("Unknown Type: " + type);
	}
	
	public String saveString(String saveDirectory){
		if(type == 'M' || type == 'N' || type == 'U' || type == 'T'){
			String returnValue = "Type: " + type + " FeatureNum: " + featureList.size();
			for(int x = 0; x < featureList.size(); x++){
				returnValue +=	" Feature" + (x+1) + ": " + featureList.get(x) + 
					" Mistake_Allowed" + (x+1) + ": " + mistakeList.get(x);
				if(x != featureList.size() - 1){
					returnValue += " Min_Gap" + (x+1) + ": " + minGapList.get(x) + 
						" Max_Gap" + (x+1) + ": " + maxGapList.get(x);
				}
			}
			returnValue += " isPercentage: " + this.isPercentage + " Window_From: " + windowFrom + " Window_To: " + windowTo;
			return returnValue;
		}else if(type == 'I' || type == 'J'){
			String returnValue = "Type: " + type + " FeatureNum: " + featureList.size();
			for(int x = 0; x < featureList.size(); x++){
				returnValue +=	" Feature" + (x+1) + ": " + featureList.get(x) + 
					" Mistake_Allowed" + (x+1) + ": " + mistakeList.get(x);
				if(x != featureList.size() - 1){
					returnValue += " Min_Gap" + (x+1) + ": " + minGapList.get(x) + 
						" Max_Gap" + (x+1) + ": " + maxGapList.get(x);
				}
			}
			returnValue += " isPercentage: " + this.isPercentage + " Window_From: " + windowFrom + " Window_To: " + windowTo + " Physiochemical2: " + this.physiochemical2Int;
			return returnValue;
		}else		
			return "UNKNOWN TYPE";
	}	
	
	public int getP2(){return this.physiochemical2Int;}
	public int getFeatureListSize(){ return featureList.size(); }
	public void addFeatureAt(int index, String kgram){this.featureList.add(index, kgram);}
	public String getFeatureAt(int index){ return featureList.get(index); }
	public void setFeatureAt(int index, String feature){ featureList.set(index, feature); }
	public void addMistakeAt(int index, int mistake){this.mistakeList.add(index, mistake);}
	public int getMistakeAt(int index){ return mistakeList.get(index); }
	public int getMistakeListSize(){return this.mistakeList.size();}
	public void addMinGapAt(int minGap){this.minGapList.add(minGap);}
	public int getMinGapAt(int index){ return minGapList.get(index); }
	public int getMinGapListSize(){return this.minGapList.size();}
	public void addMaxGapAt(int maxGap){this.maxGapList.add(maxGap);}
	public int getMaxGapAt(int index){ return maxGapList.get(index); }
	public int getMaxGapListSize(){return this.maxGapList.size();}
	public void setMistakeAllowed(int index, int mistakeAllowed){ mistakeList.set(index, mistakeAllowed); }
	public void setMinGapAt(int index, int minGap){ minGapList.set(index, minGap); }
	public void setMaxGapAt(int index, int maxGap){ maxGapList.set(index, maxGap); }

	@Override
	public Object computeDNA(FastaFormat fastaFormat) {
		String sequence = fastaFormat.getSequence().toUpperCase();
    	int startIndex = fastaFormat.getStartIndex(this.windowFrom,this.isPercentage);
    	int endIndex = fastaFormat.getEndIndex(this.windowTo,this.isPercentage);
    	int windowSize = endIndex - startIndex + 1;
		
		//Any code before this is general for all
		
		if(type == 'M')//Multiple Kgram			
			return findMatchDNA(this,sequence,startIndex,endIndex,0,endIndex);
		else if(type == 'N'){		
			if(windowSize < 0.0)
				return 0.0;
			else{
				int divisor = windowSize;
				if(divisor > sequence.length())
					divisor = sequence.length();
				return findMatchDNA(this,sequence,startIndex,endIndex,0,endIndex) / (divisor + 0.0);
			}
		}else
			return -92.92;
	}
		
	//This method is for type M and N
    private static int findMatchDNA(MultipleKGramFeature featureData,String sequence,int indexFrom,int indexTo,
    	int currentIndex,int originalSequenceTo){
    	//this is for type 'M' and 'N'
    	int matchCount = 0;    	
    	for(int x = indexFrom; x <= indexTo && x < sequence.length(); x++){    			
			int mistakeFound = 0;
			String feature = featureData.getFeatureAt(currentIndex);
			int mistakeAllowed = featureData.getMistakeAt(currentIndex);
			int y;
			for(y = 0; y < feature.length() && (x+y) < sequence.length() && (x+y) <= indexTo && 
				(x+y) <= originalSequenceTo; y++){
				char sequenceChar = sequence.charAt(x+y);
				switch(feature.charAt(y)){
					case 'A': 
						if(sequenceChar != 'A' && sequenceChar != 'a')
							mistakeFound++;		
						break;
					case 'C': 
						if(sequenceChar != 'C' && sequenceChar != 'c')
							mistakeFound++;		
						break;
					case 'T': 
						if(sequenceChar != 'T' && sequenceChar != 't')
							mistakeFound++;		
						break;
					case 'G': 
						if(sequenceChar != 'G' && sequenceChar != 'g')
							mistakeFound++;		
						break;
					case 'X': 					
						break;
					default: throw new Error("Unknown Character in Feature");
				}
			}
			if(y == feature.length() && mistakeFound <= mistakeAllowed){	
				if(currentIndex == featureData.getFeatureListSize() - 1){
		    		//Base case: all features in List has been matched		    		
		    		matchCount++;
		    	}	
		    	else{
		    		String nextFeature = featureData.getFeatureAt(currentIndex + 1);		    		
		    		matchCount += findMatchDNA(featureData,sequence,
					x + feature.length() + featureData.getMinGapAt(currentIndex),
					x + feature.length() + nextFeature.length() + featureData.getMaxGapAt(currentIndex) - 1,
					(currentIndex+1),originalSequenceTo);
		    	}																
			}				
			else{//do nothing
			}
		}
		return matchCount;
    }

	@Override
	public Object computeProtein(FastaFormat fastaFormat,
			int scoringMatrixIndex,int countingStyleIndex,ScoringMatrix scoringMatrix) {
		String sequence = fastaFormat.getSequence().toUpperCase();
    	int startIndex = fastaFormat.getStartIndex(this.windowFrom,this.isPercentage);
    	int endIndex = fastaFormat.getEndIndex(this.windowTo,this.isPercentage);
    	int windowSize = endIndex - startIndex + 1;
		//Any code before this is general for all
		
		if(type == 'M'){//Multiple Kgram			
			if(scoringMatrixIndex == 0)//identity matrix
				return findMatchProtein(this,sequence,startIndex,endIndex,0,endIndex,scoringMatrixIndex,countingStyleIndex,0.0,null);
			else//blosum62 or structure-derived matrix
				return findMatchProtein(this,sequence,startIndex,endIndex,0,endIndex,scoringMatrixIndex,countingStyleIndex,0.0,
						scoringMatrix);
		}
		else if(type == 'N'){//Multiple Kgram 
			if(scoringMatrixIndex == 0){//identity matrix
				if(windowSize < 0.0)
					return 0.0;
				else{
					int divisor = sequence.length();
					if(divisor > windowSize)
						divisor = windowSize;
					return findMatchProtein(this,sequence,startIndex,endIndex,0,endIndex,scoringMatrixIndex,countingStyleIndex,0.0,null) /
						(divisor + 0.0);
				}	
			}
			else{//blosum62 or structure-derived matrix
				if(windowSize < 0.0)
					return 0.0;
				else{
					int divisor = sequence.length();
					if(divisor > windowSize)
						divisor = windowSize;
					return findMatchProtein(this,sequence,startIndex,endIndex,0,endIndex,scoringMatrixIndex,countingStyleIndex,0.0,
							scoringMatrix) / (divisor + 0.0);
				}
			}
		}else if(type == 'U')//Multiple Kgram						
			return findPhysiochemicalMatchProtein(this,sequence,startIndex,endIndex,0,endIndex);			
		else if(type == 'T'){//Multiple Kgram 			
			if(windowSize < 0.0)
				return 0.0;
			else{
				int divisor = sequence.length();
				if(divisor > windowSize)
					divisor = windowSize;
				return findPhysiochemicalMatchProtein(this,sequence,startIndex,endIndex,0,endIndex) / (divisor + 0.0);
			}			
		}else if(type == 'I'){//Multiple Kgram
			int physiochemical2Index = this.physiochemical2Int;			
			Physiochemical2 p2 = new Physiochemical2(Physiochemical2.indexToName(physiochemical2Index));
			return findPhysiochemical2MatchProtein(this,sequence,startIndex,endIndex,0,endIndex, p2);
		}
		else if(type == 'J'){//Multiple Kgram 	
			int physiochemical2Index = this.physiochemical2Int;			
			Physiochemical2 p2 = new Physiochemical2(Physiochemical2.indexToName(physiochemical2Index));
			if(windowSize < 0.0)
				return 0.0;
			else{
				int divisor = sequence.length();
				if(divisor > windowSize)
					divisor = windowSize;
				return findPhysiochemical2MatchProtein(this,sequence,startIndex,endIndex,0,endIndex, p2) / (divisor + 0.0);
			}			
		}else return -93.93;
	}
	
	//This is used by type I, J
    private double findPhysiochemical2MatchProtein(MultipleKGramFeature featureData,String sequence,int indexFrom,int indexTo,
    	int currentIndex,int originalSequenceTo, Physiochemical2 p2){
    	double matchCount = 0;    	
    	for(int x = indexFrom; x <= indexTo && x < sequence.length(); x++){    			
			int mistakeFound = 0;
			String feature = featureData.getFeatureAt(currentIndex);
			if(GenerateArff.validatePhysiochemical2FeatureProtein(feature, p2) == false)
				throw new Error("Unknown Character in Feature");
			int mistakeAllowed = featureData.getMistakeAt(currentIndex);			
			int y;
			for(y = 0; y < feature.length() && (x+y) < sequence.length() && (x+y) <= indexTo && (x+y) <= originalSequenceTo; y++){
				char sequenceChar = sequence.charAt(x+y);				
				if(feature.charAt(y)!='X' && !p2.isMatch(feature.charAt(y),sequenceChar))//mismatch
					 mistakeFound++;
				else{
					//match
				}
			}			
			if(y == feature.length() && mistakeFound <= mistakeAllowed){	
			//This feature is being matched
				if(currentIndex == featureData.getFeatureListSize() - 1){
		    		//Base case: all features in List has been matched		    		   	
		    		matchCount++;
		    		x += feature.length() - 1;
		    	}	
		    	else{
		    		String nextFeature = featureData.getFeatureAt(currentIndex + 1);
		    		double returnValue = findPhysiochemical2MatchProtein(featureData,sequence,
							x + feature.length() + featureData.getMinGapAt(currentIndex),
							x + feature.length() + nextFeature.length() + featureData.getMaxGapAt(currentIndex) - 1,
							(currentIndex+1),originalSequenceTo, p2);
		    		matchCount += returnValue;
		    		if(returnValue > 0.0)
		    			x += feature.length() - 1;
		    	}								
			}					
		}
		return matchCount;
    }
	
	//This is used by type U, T
    private double findPhysiochemicalMatchProtein(MultipleKGramFeature featureData,String sequence,int indexFrom,int indexTo,
    	int currentIndex,int originalSequenceTo){
    	double matchCount = 0;    	
    	for(int x = indexFrom; x <= indexTo && x < sequence.length(); x++){    			
			int mistakeFound = 0;
			String feature = featureData.getFeatureAt(currentIndex);
			if(GenerateArff.validatePhysiochemicalFeatureProtein(feature) == false)
				throw new Error("Unknown Character in Feature");
			int mistakeAllowed = featureData.getMistakeAt(currentIndex);			
			int y;
			for(y = 0; y < feature.length() && (x+y) < sequence.length() && (x+y) <= indexTo && (x+y) <= originalSequenceTo; y++){
				char sequenceChar = sequence.charAt(x+y);				
				if(feature.charAt(y)!='X' && !GenerateArff.isPhysiochemicalMatch(feature.charAt(y),sequenceChar))//mismatch
					 mistakeFound++;
				else{
					//match
				}
			}			
			if(y == feature.length() && mistakeFound <= mistakeAllowed){	
			//This feature is being matched
				if(currentIndex == featureData.getFeatureListSize() - 1){
		    		//Base case: all features in List has been matched		    		   	
		    		matchCount++;
		    		x += feature.length() - 1;
		    	}	
		    	else{
		    		String nextFeature = featureData.getFeatureAt(currentIndex + 1);
		    		double returnValue = findPhysiochemicalMatchProtein(featureData,sequence,
							x + feature.length() + featureData.getMinGapAt(currentIndex),
							x + feature.length() + nextFeature.length() + featureData.getMaxGapAt(currentIndex) - 1,
							(currentIndex+1),originalSequenceTo);
		    		matchCount += returnValue;
		    		if(returnValue > 0.0)
		    			x += feature.length() - 1;
		    	}								
			}					
		}
		return matchCount;
    }
	
	//this is used by type M, N
    private double findMatchProtein(MultipleKGramFeature featureData,String sequence,int indexFrom,int indexTo,
    	int currentIndex,int originalSequenceTo,int scoringMatrixIndex,int countingStyleIndex,double totalScore,ScoringMatrix scoringMatrix){
    	double matchCount = 0;    	
    	for(int x = indexFrom; x <= indexTo && x < sequence.length(); x++){    			
			int mistakeFound = 0;
			double thisFeatureScore = 0.0;
			String feature = featureData.getFeatureAt(currentIndex);
			if(GenerateArff.validateFeatureProtein(feature) == false)
				throw new Error("Unknown Character in Feature");
			int mistakeAllowed = featureData.getMistakeAt(currentIndex);			
			int y;
			for(y = 0; y < feature.length() && (x+y) < sequence.length() && (x+y) <= indexTo && (x+y) <= originalSequenceTo; y++){
				char sequenceChar = sequence.charAt(x+y);
				if(scoringMatrixIndex == 0){//identity matrix
					if(feature.charAt(y) != sequenceChar && feature.charAt(y)!='X')//mismatch
						 mistakeFound++;
					else//match
						thisFeatureScore++;
				}
				else{//blosum62 or structure-derived matrix
					thisFeatureScore += scoringMatrix.getScore(feature.charAt(y),sequenceChar);
				}
			}
			if(scoringMatrixIndex == 0){//identity matrix
				if(y == feature.length() && mistakeFound <= mistakeAllowed){
					totalScore += thisFeatureScore;	
					if(currentIndex == featureData.getFeatureListSize() - 1){
			    		//Base case: all features in List has been matched
			    		if(countingStyleIndex == 0)//+1   		
			    			matchCount++;
			    		else
			    			matchCount += totalScore;//+score				    		
			    	}	
			    	else{
			    		String nextFeature = featureData.getFeatureAt(currentIndex + 1);	
			    		//double beforeFunction = totalScore;
			    		matchCount += findMatchProtein(featureData,sequence,
						x + feature.length() + featureData.getMinGapAt(currentIndex),
						x + feature.length() + nextFeature.length() + featureData.getMaxGapAt(currentIndex) - 1,
						(currentIndex+1),originalSequenceTo,scoringMatrixIndex,countingStyleIndex,totalScore,scoringMatrix);			    		
			    	}				
					totalScore -= thisFeatureScore;//this line is needed to prevent double counting
				}				
				else{//do nothing
				}
			}else{//blosum62 or structure-derived matrix
				if(y == feature.length() && thisFeatureScore >= 0){
					//score for each feature must be >= 0
					totalScore += thisFeatureScore;
					if(currentIndex == featureData.getFeatureListSize() - 1){
			    		//Base case: all features in List has been matched
			    		if(countingStyleIndex == 0)		    		
			    			matchCount++;
			    		else
			    			matchCount += totalScore;
			    	}	
			    	else{
			    		String nextFeature = featureData.getFeatureAt(currentIndex + 1);		    		
			    		matchCount += findMatchProtein(featureData,sequence,
						x + feature.length() + featureData.getMinGapAt(currentIndex),
						x + feature.length() + nextFeature.length() + featureData.getMaxGapAt(currentIndex) - 1,
						(currentIndex+1),originalSequenceTo,scoringMatrixIndex,countingStyleIndex,totalScore,scoringMatrix);
			    	}			
					totalScore -= thisFeatureScore;//this line is needed to prevent double counting
				}				
				else{//do nothing
				}
			}	
		}
		return matchCount;
    }
    
    public static MultipleKGramFeature randomlyGenerate(int windowFrom, int windowTo,Random rand){
    	boolean isPercentage = rand.nextBoolean();
		int[] window;
		if(isPercentage)
			window = Feature.getWindow(0, 100, rand);
		else
			window = Feature.getWindow(windowFrom, windowTo, rand);			
		boolean isRelative = rand.nextBoolean();		
		int type = rand.nextInt(3);//0 is normal, 1 is physio, 2 is physio2
		int physio2 = rand.nextInt(Physiochemical2.codingNameList.length);
		//this is to make sure that we do not have type2==0 and physio2==0 since would be same as type==0
		if(type == 2 && physio2 == 0)
			while(physio2 == 0)
				physio2 = rand.nextInt(Physiochemical2.codingNameList.length);
		//Only randomly generate 2-3 multiple
		//However, allow for extension in mutation and crossover
		int gramCount = rand.nextInt(2) + 2;
		String[] kField = new String[gramCount];
		int[] xField = new int[gramCount];
		int[] yField = new int[gramCount - 1];
		int[] zField = new int[gramCount - 1];
		
		int windowSize = window[1] - window[0];
		for(int x = 0; x < gramCount; x++){
			kField[x] = Feature.getGram(type,rand,physio2);
			xField[x] = Feature.getMistakeAllowed(rand, kField[x].length());
			if(x+1 < gramCount){
				//allow gaps to range from 0 to windowSize/2
				yField[x] = rand.nextInt(windowSize/2 + 1);
				zField[x] = Feature.randomBetween(yField[x], windowSize/2 + 1, rand);
			}
		}
				
		switch(type){
		case 0: //normal
				if(isRelative == false)
					return new MultipleKGramFeature('M',kField,xField,yField,zField,window[0],window[1],isPercentage);
				else
					return new MultipleKGramFeature('N',kField,xField,yField,zField,window[0],window[1],isPercentage);
		case 1: //Physiochemical
				if(isRelative == false)
					return new MultipleKGramFeature('U',kField,xField,yField,zField,window[0],window[1],isPercentage);
				else 
					return new MultipleKGramFeature('T',kField,xField,yField,zField,window[0],window[1],isPercentage);
		case 2: //Physiochemical2
				if(isRelative == false)
					return new MultipleKGramFeature('I',kField,xField,yField,zField,window[0],window[1],physio2,isPercentage);
				else
					return new MultipleKGramFeature('J',kField,xField,yField,zField,window[0],window[1],physio2,isPercentage);
		default: throw new Error("Unhandled Case: " + type);
		}
    }
    private void mutateType(){
		switch(this.type){
			case 'M': this.type = 'N'; break;
			case 'N': this.type = 'M'; break;
			case 'U': this.type = 'T'; break;
			case 'T': this.type = 'U'; break;
			case 'I': this.type = 'J'; break;
			case 'J': this.type = 'I'; break;
			default: throw new Error("Unhandled type: " + this.type);
		}
	}	
	private void mutateKgram(Random rand){
		int typeInt;
		if(this.type == 'M' || this.type == 'N')
			typeInt = 0;
		else if(this.type == 'U' || this.type == 'T')
			typeInt = 1;
		else
			typeInt = 2;
		//choose which kgram to mutate
		int mutate = rand.nextInt(this.featureList.size());
		this.featureList.set(mutate, this.mutateKgram(this.featureList.get(mutate), rand, typeInt, this.physiochemical2Int));
	}
	private void mutateMistake(Random rand){
		int mutate = rand.nextInt(this.mistakeList.size());
		this.mistakeList.set(mutate, Feature.getMistakeAllowed(rand, this.featureList.get(mutate).length()));
	}
	private void mutateMinMaxGap(Random rand, int windowSize){
		//+1 to ensure that it at least move by 1
		int moveBy = rand.nextInt(windowSize/2 + 1);
		int mutate = rand.nextInt(this.minGapList.size());
		int minGap = this.minGapList.get(mutate);		
		int maxGap = this.maxGapList.get(mutate);
		do{
			minGap = randomBetween(minGap - moveBy, minGap + moveBy, rand);
			maxGap = randomBetween(maxGap - moveBy, maxGap + moveBy, rand);
		}while(minGap > maxGap || minGap < 0);
		this.minGapList.set(mutate, minGap);
		this.maxGapList.set(mutate, maxGap);
	}
	
	private void removeKgram(Random rand){
		int index = rand.nextInt(this.featureList.size());
		if(index == this.featureList.size() - 1){			
			this.minGapList.remove(index - 1);
			this.maxGapList.remove(index - 1);
		}else{			
			this.minGapList.remove(index);
			this.maxGapList.remove(index);
		}
		this.mistakeList.remove(index);
		this.featureList.remove(index);		
	}
	
	@Override
	public Feature mutate(Random rand, int windowMin, int windowMax) {
		//Do point mutation	
		//first decide which part to mutate					
		MultipleKGramFeature feature = new MultipleKGramFeature(this.name);
		int index = rand.nextInt(10);
		if(feature.featureList.size() == 2)
			index = rand.nextInt(8);
		switch(index){			
			//mutate mistakeAllowed - 10% chance
			case 0: feature.mutateMistake(rand); break;
			//mutate type - 10% chance
			case 1: feature.mutateType(); break;
			//mutate gap - 10% chance
			case 2: feature.mutateMinMaxGap(rand,feature.windowTo - feature.windowFrom); break;
			//mutate kgram - 30% chance
			case 3: 
			case 4:  
			case 5: feature.mutateKgram(rand); break;
			//mutate window location - 20% chance
			case 6: 			
			case 7: feature.mutateWindow(rand,windowMin,windowMax); break;
			//remove one kgram
			case 8:
			case 9: feature.removeKgram(rand); break;
		}
		String[] kField = new String[feature.featureList.size()];
		for(int x = 0; x < feature.featureList.size(); x++)
			kField[x] = feature.featureList.get(x);
		int[] xField = new int[feature.mistakeList.size()];
		for(int x = 0; x < feature.mistakeList.size(); x++)
			xField[x] = feature.mistakeList.get(x);
		int[] yField = new int[feature.minGapList.size()];
		for(int x = 0; x < feature.minGapList.size(); x++)
			yField[x] = feature.minGapList.get(x);
		int[] zField = new int[feature.maxGapList.size()];
		for(int x = 0; x < feature.maxGapList.size(); x++)
			zField[x] = feature.maxGapList.get(x);		
		//normal or Physiochemical
		if(feature.type == 'M' || feature.type == 'N' || feature.type == 'U' || feature.type == 'T'){			
			Feature temp = new MultipleKGramFeature(feature.type,kField,xField,yField,zField,feature.windowFrom,feature.windowTo,
					feature.isPercentage);
			//To ensure that something has changed after the mutation
			if(temp.name.equalsIgnoreCase(feature.name))
				return this.mutate(rand, windowMin, windowMax);
			else				
				return temp;			
		}
		//Physiochemical2
		else if(feature.type == 'I' || feature.type == 'J'){				
			Feature temp = new MultipleKGramFeature(feature.type,kField,xField,yField,zField,feature.windowFrom,feature.windowTo,feature.physiochemical2Int,
					feature.isPercentage);
			//To ensure that something has changed after the mutation
			if(temp.name.equalsIgnoreCase(feature.name))
				return this.mutate(rand, windowMin, windowMax);
			else				
				return temp;			
		}
		else
			throw new Error("Unknown type");
	}	
}

