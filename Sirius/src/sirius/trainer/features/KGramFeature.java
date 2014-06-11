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

import java.util.Random;
import java.util.StringTokenizer;

import sirius.trainer.main.ScoringMatrix;
import sirius.trainer.step2.Physiochemical2;
import sirius.utils.FastaFormat;

public class KGramFeature extends Feature{
	private String kgram;
	private int mistakeAllowed;	
	private int physiochemical2Int;
	
	//This constructor is used by zscorePane
	public KGramFeature(String name){
		super(null,null,name.charAt(0));
		try{
			StringTokenizer st = new StringTokenizer(name,"_");
			char type = st.nextToken().charAt(0);
			String kgram = st.nextToken();
			int xmistake = Integer.parseInt(st.nextToken());			
			boolean isPercentage = Boolean.parseBoolean(st.nextToken());
			int windowFrom = Integer.parseInt(st.nextToken());
			int windowTo = Integer.parseInt(st.nextToken());
			if(type == 'K' || type == 'L' || type == 'G' || type == 'H')
				setValues(type, kgram, xmistake, windowFrom, windowTo, isPercentage);
			else if(type == 'D' || type == 'E'){//physiochemical2
				int tempInt = Integer.parseInt(st.nextToken());
				setValues(type,kgram,xmistake,windowFrom,windowTo,tempInt,isPercentage);
			}else
				throw new Error("Unknown Type");
		}catch(Exception e){throw new Error("Errors in KGramFeature(String name)");}
	}
	
	//This constructor is for normalKGram and Physiochemical KGram	
	public KGramFeature(char type,String kgram,int xmistake,int windowFrom,int windowTo, boolean isPercentage){	
		super(null, null, type);
		setValues(type, kgram, xmistake, windowFrom, windowTo, isPercentage);
	}
	
	//This method is for normalKGram and Physiochemical KGram	
	private void setValues(char type,String kgram,int xmistake,int windowFrom,int windowTo, boolean isPercentage){
		this.name = type + "_" + kgram + "_" + xmistake + "_" + isPercentage + "_" + windowFrom + "_" + windowTo;
		this.windowFrom = windowFrom;
		this.windowTo = windowTo;
		this.kgram = kgram;
		this.mistakeAllowed = xmistake;		
		this.isPercentage = isPercentage;
		
		this.details = kgram + ", " + xmistake + " mistakes,";
		if(this.isPercentage)
			this.details += " PercentageWindow(" + windowFrom + " , " + windowTo + ")";
		else
			this.details += " Window(" + windowFrom + " , " + windowTo + ")";		
		if(type == 'L')
			this.details += ", value relative to length";
		else if(type == 'G')
			this.details += ", Physiochemical";
		else if(type == 'H')
			this.details += ", Physiochemical, value relative to length";		
	}
	
	//This method is for Physiochemical2
	private void setValues(char type,String kgram,int xmistake,int windowFrom,int windowTo,int tempInt, boolean isPercentage){		
		this.name = type + "_" + kgram + "_" + xmistake + "_" + isPercentage + "_" + windowFrom + "_" + windowTo + "_" + tempInt;
		this.windowFrom = windowFrom;
		this.windowTo = windowTo;
		this.kgram = kgram;
		this.mistakeAllowed = xmistake;
		this.physiochemical2Int = tempInt;
		this.isPercentage = isPercentage;
		
		this.details = kgram + ", " + xmistake + " mistakes,";
		if(this.isPercentage)
			this.details += " PercentageWindow(" + windowFrom + " , " + windowTo + "), " + Physiochemical2.indexToName(physiochemical2Int);
		else
			this.details += " Window(" + windowFrom + " , " + windowTo + "), " + Physiochemical2.indexToName(physiochemical2Int);
			
		if(type == 'E')
			this.details += ", value relative to length";	
	}
	
	//This is for Physiochemical2 K-gram with X-mistakes
	public KGramFeature(char type,String kgram,int xmistake,int windowFrom,int windowTo,int tempInt, boolean isPercentage){	
		super(null, null, type);
		setValues(type,kgram,xmistake,windowFrom,windowTo,tempInt,isPercentage);
	}	
	
	public String saveString(String saveDirectory){
		if(type == 'K' || type == 'L' || type == 'G' || type == 'H')
			return "Type: " + type + " Feature: " + kgram + " Mistake_Allowed: " + 
				mistakeAllowed + " isPercentage: " + this.isPercentage + " Window_From: " + windowFrom + " Window_To: " + windowTo;			
		else if(type == 'D' || type == 'E')		
			return "Type: " + type + " Feature: " + kgram + " Mistake_Allowed: " + 
				mistakeAllowed + " isPercentage: " + this.isPercentage + " Window_From: " + windowFrom + " Window_To: " + windowTo + 
				" Physiochemical2: " + Physiochemical2.indexToName(this.physiochemical2Int);
		return "UNKNOWN TYPE";
	}
	
	public static KGramFeature loadSettings(String line, boolean isLocationIndexMinusOne, char type){
		String feature = line.substring(line.indexOf("Feature: ") + 
			("Feature: ").length(),line.indexOf("Mistake_Allowed: ") - 1);
		String xmistake = line.substring(line.indexOf("Mistake_Allowed: ") + 
			("Mistake_Allowed: ").length(),line.indexOf("isPercentage: ") - 1);
		String isPercentage = line.substring(line.indexOf("isPercentage: ") + 
				("isPercentage: ").length(),line.indexOf("Window_From: ") - 1);
		String windowFrom = line.substring(line.indexOf("Window_From: ") + 
			("Window_From: ").length(),line.indexOf("Window_To: ") - 1);
		int windowFromInt = Integer.parseInt(windowFrom);
		boolean isPercentageBoolean = Boolean.parseBoolean(isPercentage);
		
		if(type == 'D' || type == 'E'){
			String windowTo = line.substring(line.indexOf("Window_To: ") + 
					("Window_To: ").length(),line.indexOf("Physiochemical2: ") - 1);
			int windowToInt = Integer.parseInt(windowTo);
			String physiochemical2String = line.substring(line.indexOf("Physiochemical2: ") + ("Physiochemical2: ").length());
			int physiochemical2Int = Physiochemical2.nameToIndex(physiochemical2String);
			if(isLocationIndexMinusOne == true && windowFromInt < 0)
				return null;
			else
				return new KGramFeature(type,feature,Integer.parseInt(xmistake),windowFromInt,windowToInt,physiochemical2Int, isPercentageBoolean);
		}else if(type == 'K' || type == 'L' || type == 'G' || type == 'H'){
			String windowTo = line.substring(line.indexOf("Window_To: ") + 
					("Window_To: ").length());
			int windowToInt = Integer.parseInt(windowTo);
			if(isLocationIndexMinusOne == true && windowFromInt < 0)
				return null;
			else
				return new KGramFeature(type,feature,Integer.parseInt(xmistake),windowFromInt,windowToInt,isPercentageBoolean);		
		}else
			throw new Error("Unknown type: " + type);		
	}	
	
	public int getP2(){return this.physiochemical2Int;}
	public String getKGram(){ return this.kgram; }
	public void setFeature(String kgram){ this.kgram = kgram; }
	public int getFeatureSize(){ return this.kgram.length(); }
	public int getMistakeAllowed(){ return this.mistakeAllowed; }
	public void setMistakeAllowed(int mistake){ this.mistakeAllowed = mistake; }

	@Override
	public Object computeDNA(FastaFormat fastaFormat) {
		String sequence = fastaFormat.getSequence().toUpperCase();
    	int startIndex = fastaFormat.getStartIndex(this.windowFrom,this.isPercentage);
    	int endIndex = fastaFormat.getEndIndex(this.windowTo,this.isPercentage);
    	int windowSize = endIndex - startIndex + 1;
		
		//Any code before this is general for all
		
		double match1Count = 0.0;
		int featureLength = this.getKGram().length();
		String feature = this.getKGram();		
		if(GenerateArff.validateFeatureDNA(feature) == false)	
			throw new Error("Unknown Character in Feature");
		int mistakeAllowed = this.getMistakeAllowed();									
		for(int x = startIndex; x <= endIndex && x < sequence.length(); x++){				
			int mistake = 0;
			int y;
			for(y = 0; y < featureLength && (x+y) < sequence.length() && (x+y) <= endIndex; y++){
				char temp = sequence.charAt(x+y);
				if(feature.charAt(y) != temp && feature.charAt(y) != 'X')
					mistake++;					
			}
			if(y == featureLength && mistake <= mistakeAllowed)
				match1Count++;
		}
		if(type == 'K')
			return match1Count;
		else{// for 'L'
			if(windowSize < 0.0)
				return 0.0;
			else{
				int divisor = sequence.length();
				if(divisor > windowSize)
					divisor = windowSize;
				return match1Count / (divisor + 0.0);
			}
		}
	}

	@Override
	public Object computeProtein(FastaFormat fastaFormat,
			int scoringMatrixIndex,int countingStyleIndex,ScoringMatrix scoringMatrix) {
		String sequence = fastaFormat.getSequence().toUpperCase();		
    	int startIndex = fastaFormat.getStartIndex(this.windowFrom,this.isPercentage);    	
    	int endIndex = fastaFormat.getEndIndex(this.windowTo,this.isPercentage);
    	int windowSize = endIndex - startIndex + 1;
    	
		//Any code before this is general for all    	
		if(type == 'K' || type == 'L'){
			double match1Count = 0.0;
			int featureLength = this.getKGram().length();
			String feature = this.getKGram();			
			int mistakeAllowed = this.getMistakeAllowed();			
			if(GenerateArff.validateFeatureProtein(feature) == false)//contains unknown character
				throw new Error("Contain Unknown Character: " + feature);
			for(int x = startIndex; x <= endIndex && x < sequence.length(); x++){				
				int mistake = 0;
				double score = 0.0;
				int y;
				for(y = 0; y < featureLength && (x+y) < sequence.length() && (x+y) <= endIndex; y++){
					char temp = sequence.charAt(x+y);
					if(scoringMatrixIndex == 0){//identity matrix						
						if(feature.charAt(y)!=temp && feature.charAt(y)!='X')//mismatch
							mistake++;
						else//match
							score++;
					}
					else{ //blosum62 or structure-derived matrix						
						score += scoringMatrix.getScore(feature.charAt(y),temp);
					}
				}
				if(scoringMatrixIndex == 0){//identity matrix					
					if(y == featureLength && mistake <= mistakeAllowed){					
						if(countingStyleIndex == 0)
							match1Count++;
						else 
							match1Count += score;
					}
				}
				else{//blosum 62 or structure-derived matrix			
					if(y == featureLength && score >= 0){
						if(countingStyleIndex == 0)
							match1Count++;
						else
							match1Count += score;
					}						
				}
			}
			if(type == 'K')
				return match1Count;
			else{ //Type L
				if(windowSize < 0.0)
					return 0.0;
				else{
					int divisor = sequence.length();
					if(divisor > windowSize)
						divisor = windowSize;
					return match1Count / (divisor + 0.0);
				}
			}
		}else if(type == 'G' || type == 'H'){			
			double match1Count = 0.0;
			int featureLength = this.getKGram().length();
			String feature = this.getKGram();			
			int mistakeAllowed = this.getMistakeAllowed();			
			if(GenerateArff.validatePhysiochemicalFeatureProtein(feature) == false)//contains unknown character
				throw new Error("Contain Unknown Character~!");
			for(int x = startIndex; x <= endIndex && x < sequence.length(); x++){				
				int mistake = 0;
				int y;
				for(y = 0; y < featureLength && (x+y) < sequence.length() && (x+y) <= endIndex; y++){
					char temp = sequence.charAt(x+y);									
					if(feature.charAt(y)!='X' && !GenerateArff.isPhysiochemicalMatch(feature.charAt(y),temp))//mismatch
						mistake++;				
				}							
				if(y == featureLength && mistake <= mistakeAllowed){					
					match1Count++;
					//The following line means that it would not allow AAAA to be counted as 3 in AA but as 2 instead
					//x += featureLength - 1;
				}				
			}
			if(type == 'G')
				return match1Count;
			else{ //Type H
				if(windowSize < 0.0)
					return 0.0;
				else{
					int divisor = sequence.length();
					if(divisor > windowSize)
						divisor = windowSize;
					return match1Count / (divisor + 0.0);
				}
			}
		}else if(type == 'D' || type == 'E'){
			double match1Count = 0.0;
			int featureLength = this.getKGram().length();
			String feature = this.getKGram();			
			int physiochemical2Index = this.physiochemical2Int;
			int mistakeAllowed = this.getMistakeAllowed();					
			Physiochemical2 p2 = new Physiochemical2(Physiochemical2.indexToName(physiochemical2Index));
			if(GenerateArff.validatePhysiochemical2FeatureProtein(feature, p2) == false)//contains unknown character
				throw new Error("Contain Unknown Character: " + feature + " Name: " + Physiochemical2.indexToName(physiochemical2Index));
			for(int x = startIndex; x <= endIndex && x < sequence.length(); x++){				
				int mistake = 0;
				int y;
				for(y = 0; y < featureLength && (x+y) < sequence.length() && (x+y) <= endIndex; y++){
					char temp = sequence.charAt(x+y);									
					if(feature.charAt(y)!='X' && !p2.isMatch(feature.charAt(y),temp))//mismatch
						mistake++;
				}							
				if(y == featureLength && mistake <= mistakeAllowed){					
					match1Count++;
					//The following line means that it would not allow AAAA to be counted as 3 in AA but as 2 instead
					//x += featureLength - 1;
				}				
			}
			if(type == 'D')
				return match1Count;
			else{ //Type E
				if(windowSize < 0.0)
					return 0.0;
				else{
					int divisor = sequence.length();
					if(divisor > windowSize)
						divisor = windowSize;
					return match1Count / (divisor + 0.0);
				}
			}
		}else throw new Error("Should not reach here! KGramFeature.computeProtein");							
	}	

	//Randomly Generate a KGramFeature
	public static KGramFeature randomlyGenerate(int windowFrom, int windowTo, Random rand){
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
		String kgram = Feature.getGram(type,rand,physio2);
		int xmistake = Feature.getMistakeAllowed(rand, kgram.length());
		switch(type){
		case 0: //normal
				if(isRelative == false)
					return new KGramFeature('K',kgram,xmistake,window[0],window[1],isPercentage);
				else
					return new KGramFeature('L',kgram,xmistake,window[0],window[1],isPercentage);
		case 1: //Physiochemical
				if(isRelative == false)
					return new KGramFeature('G',kgram,xmistake,window[0],window[1],isPercentage);
				else 
					return new KGramFeature('H',kgram,xmistake,window[0],window[1],isPercentage);
		case 2: //Physiochemical2
				if(isRelative == false)
					return new KGramFeature('D',kgram,xmistake,window[0],window[1],physio2,isPercentage);
				else
					return new KGramFeature('E',kgram,xmistake,window[0],window[1],physio2,isPercentage);
		default: throw new Error("Unhandled Case: " + type);
		}
	}
	private void mutateType(){
		switch(this.type){
			case 'K': this.type = 'L'; break;
			case 'L': this.type = 'K'; break;
			case 'G': this.type = 'H'; break;
			case 'H': this.type = 'G'; break;
			case 'D': this.type = 'E'; break;
			case 'E': this.type = 'D'; break;
			default: throw new Error("Unhandled type: " + this.type);
		}
	}	
	private void mutateKgram(Random rand){
		int typeInt;
		if(this.type == 'K' || this.type == 'L')
			typeInt = 0;
		else if(this.type == 'G' || this.type == 'H')
			typeInt = 1;
		else
			typeInt = 2;
		this.kgram = this.mutateKgram(kgram, rand, typeInt, this.physiochemical2Int);
	}
	@Override
	public Feature mutate(Random rand, int windowMin, int windowMax) {
		//Do point mutation	
		//first decide which part to mutate		
		int index = rand.nextInt(10);
		KGramFeature feature = new KGramFeature(this.name);		
		switch(index){
			//mutate kgram - 40% chance
			case 0:  
			case 1:
			case 2: 
			case 3: feature.mutateKgram(rand); break;
			//mutate mistakeAllowed - 10% chance
			case 4: feature.mistakeAllowed = Feature.getMistakeAllowed(rand, feature.kgram.length()); break;
			//mutate type - 10% chance
			case 5: feature.mutateType(); break;
			//mutate window location - 40% chance
			case 6: 
			case 7:
			case 8:
			case 9: feature.mutateWindow(rand,windowMin,windowMax); break;
		}			
		//normal or Physiochemical
		if(feature.type == 'K' || feature.type == 'L' || feature.type == 'G' || feature.type == 'H'){			
			Feature temp = new KGramFeature(feature.type,feature.kgram,feature.mistakeAllowed,feature.windowFrom,feature.windowTo,feature.isPercentage);
			//To ensure that something has changed after the mutation
			if(temp.name.equalsIgnoreCase(feature.name))
				return this.mutate(rand, windowMin, windowMax);
			else				
				return temp;			
		}
		//Physiochemical2
		else if(feature.type == 'D' || feature.type == 'E'){	
			Feature temp = new KGramFeature(feature.type,feature.kgram,feature.mistakeAllowed,feature.windowFrom,feature.windowTo,feature.physiochemical2Int,
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

