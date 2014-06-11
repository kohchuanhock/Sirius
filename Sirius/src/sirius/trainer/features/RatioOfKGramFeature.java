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

public class RatioOfKGramFeature extends Feature{
	private String kgram1;
	private String kgram2;
	private int mistakeAllowed1;
	private int mistakeAllowed2;
	private int physiochemical2Int;
	
	public static RatioOfKGramFeature loadSettings(String line, boolean isLocationIndexMinusOne, char type){	
		String feature1 = line.substring(line.indexOf("Feature1: ") + 
			("Feature1: ").length(),line.indexOf("Mistake_Allowed1: ") - 1);
		String m1Mistake = line.substring(line.indexOf("Mistake_Allowed1: ") + 
			("Mistake_Allowed1: ").length(),line.indexOf("Feature2: ") - 1);
		String feature2 = line.substring(line.indexOf("Feature2: ") + 
			("Feature2: ").length(),line.indexOf("Mistake_Allowed2: ") - 1);
		String m2Mistake = line.substring(line.indexOf("Mistake_Allowed2: ") + 
			("Mistake_Allowed2: ").length(),line.indexOf("isPercentage: ") - 1);
		String isPercentageString = line.substring(line.indexOf("isPercentage: ") + 
				("isPercentage: ").length(),line.indexOf("Window_From: ") - 1);		
		String windowFrom = line.substring(line.indexOf("Window_From: ") + 
			("Window_From: ").length(),line.indexOf("Window_To: ") - 1);
		String windowTo;	
		String p2 = "UDHJDHSJK";//to cause exception if it is not overwrite since i will do Integer.parseInt with it				
		if(type != 'Q'){
			 windowTo = line.substring(line.indexOf("Window_To: ") + ("Window_To: ").length());
		}else{			 			
			windowTo = line.substring(line.indexOf("Window_To: ") + 
					("Window_To: ").length(),line.indexOf("Physiochemical2: ") - 1);
			p2 = line.substring(line.indexOf("Physiochemical2: ") + ("Physiochemical2: ").length());
		}
		int windowFromInt = Integer.parseInt(windowFrom);
		int windowToInt = Integer.parseInt(windowTo);
		boolean isPercentage = Boolean.parseBoolean(isPercentageString);
		if(isLocationIndexMinusOne == true && windowFromInt < 0)
			return null;
		else if(type != 'Q')
			return new RatioOfKGramFeature(type,feature1,feature2,windowFromInt,windowToInt,Integer.parseInt(m1Mistake),
					Integer.parseInt(m2Mistake),isPercentage);
		else
			return new RatioOfKGramFeature(type,feature1,feature2,windowFromInt,windowToInt,Integer.parseInt(m1Mistake),
					Integer.parseInt(m2Mistake),isPercentage,Integer.parseInt(p2));		
	}
	
	public RatioOfKGramFeature(String name){
		super(null, null, name.charAt(0));
		try{
			StringTokenizer st = new StringTokenizer(name,"_");
			char type = st.nextToken().charAt(0);
			String kgram = st.nextToken();
			int m1Int = Integer.parseInt(st.nextToken());
			String kgram2 = st.nextToken();
			int m2Int = Integer.parseInt(st.nextToken());
			boolean isPercentage = Boolean.parseBoolean(st.nextToken());
			int windowFrom = Integer.parseInt(st.nextToken());
			int windowTo = Integer.parseInt(st.nextToken());
			//implies this is physiochemical2
			if(st.hasMoreTokens()){			
				int p2 = Integer.parseInt(st.nextToken());
				setValues(type,kgram,kgram2,m1Int,m2Int,windowFrom,windowTo,isPercentage,p2);
			}else{				
				setValues(type,kgram,kgram2,windowFrom,windowTo,m1Int,m2Int,isPercentage);
			}
		}catch(Exception e){
			System.out.println(name);
			e.printStackTrace();
			throw new Error("Error in RatioOfKGramFeature(String name)");
		}
	}
	
	//This is for both normal and physiochemical
	public RatioOfKGramFeature(char type,String kgram,String kgram2,int windowFrom,int windowTo,int m1Int,int m2Int,
			boolean isPercentage){
		super(null, null, type);
		setValues(type,kgram,kgram2,windowFrom,windowTo,m1Int,m2Int,isPercentage);
	}
	
	//This is for physiochemical2
	public RatioOfKGramFeature(char type,String kgram,String kgram2,int windowFrom,int windowTo,int m1Int,int m2Int,
			boolean isPercentage,int physiochemical2){
		super(null, null, type);
		setValues(type,kgram,kgram2,m1Int,m2Int,windowFrom,windowTo,isPercentage,physiochemical2);
	}
	
	//This method is for Physiochemical2
	private void setValues(char type,String kgram,String kgram2,int m1Int,int m2Int,
			int windowFrom,int windowTo,boolean isPercentage,int p2){		
		this.name = type + "_" + kgram + "_" + m1Int + "_" + kgram2 + "_" + m2Int + "_" + isPercentage + "_" + windowFrom + "_" + windowTo + "_" + p2;
		this.kgram1 = kgram;
		this.kgram2 = kgram2;
		this.mistakeAllowed1 = m1Int;
		this.mistakeAllowed2 = m2Int;
		this.windowFrom = windowFrom;
		this.windowTo = windowTo;
		this.isPercentage = isPercentage;
		this.physiochemical2Int = p2;
		
		this.details = kgram + "(" + m1Int + " mistakes) : " + kgram2 + "(" + m2Int + " mistakes) ratio in";
		if(this.isPercentage)
			this.details += " PercentageWindow(" + windowFrom + "," + windowTo + ")" + Physiochemical2.indexToName(physiochemical2Int);
		else
			this.details += " Window(" + windowFrom + "," + windowTo + ")" + Physiochemical2.indexToName(physiochemical2Int);
		
	}
	
	//this is for normal and physiochemical
	private void setValues(char type,String kgram,String kgram2,int windowFrom,int windowTo,int m1Int,int m2Int,
			boolean isPercentage){
		this.name = type + "_" + kgram + "_" + m1Int + "_" + kgram2 + "_" + m2Int + "_" + isPercentage + "_" + windowFrom + "_" + windowTo;
		this.kgram1 = kgram;
		this.kgram2 = kgram2;
		this.mistakeAllowed1 = m1Int;
		this.mistakeAllowed2 = m2Int;
		this.windowFrom = windowFrom;
		this.windowTo = windowTo;
		this.isPercentage = isPercentage;
		
		this.details = kgram + "(" + m1Int + " mistakes) : " + kgram2 + "(" + m2Int + " mistakes) ratio in";
		if(this.isPercentage)
			this.details += " PercentageWindow(" + windowFrom + "," + windowTo + ")";
		else
			this.details += " Window(" + windowFrom + "," + windowTo + ")";
		
		if(type == 'O')
			this.details += ", Physiochemical";		
	}
	
	public String saveString(String saveDirectory){
		if(type == 'R' || type == 'O')
			return "Type: " + type + " Feature1: " + kgram1 + " Mistake_Allowed1: " + 
				mistakeAllowed1 + " Feature2: " + kgram2 + " Mistake_Allowed2: " +
				mistakeAllowed2 + " isPercentage: " + this.isPercentage + " Window_From: " + 
				windowFrom + " Window_To: " + windowTo;
		else if(type == 'Q')
			return "Type: " + type + " Feature1: " + kgram1 + " Mistake_Allowed1: " + 
			mistakeAllowed1 + " Feature2: " + kgram2 + " Mistake_Allowed2: " +
			mistakeAllowed2 + " isPercentage: " + this.isPercentage + " Window_From: " + 
			windowFrom + " Window_To: " + windowTo + " Physiochemical2: " + this.physiochemical2Int;
		else
			return "UNKNOWN TYPE";
	}
	
	public int getP2(){return this.physiochemical2Int;}
	public String getFeature1(){ return this.kgram1; }
	public String getFeature2(){ return this.kgram2; }
	public void setFeature1(String kgram1){ this.kgram1 = kgram1; }
	public void setFeature2(String kgram2){ this.kgram2 = kgram2; }		
	public String getFeatureAt(int index){ if(index == 0) return this.kgram1; else return this.kgram2; }
	public void setFeatureAt(int index,String newkgram){ if(index == 0) this.kgram1 = newkgram; else this.kgram2 = newkgram; }
	public void setMistakeAllowed(int index, int mistake){ if(index == 0) this.mistakeAllowed1 = mistake; else this.mistakeAllowed2 = mistake; }
	public int getMistakeAt(int index){ if(index == 0) return this.mistakeAllowed1; else return this.mistakeAllowed2; }	
		
	@Override
	public Object computeDNA(FastaFormat fastaFormat) {
		String sequence = fastaFormat.getSequence().toUpperCase();
    	int startIndex = fastaFormat.getStartIndex(this.windowFrom,this.isPercentage);
    	int endIndex = fastaFormat.getEndIndex(this.windowTo,this.isPercentage);
		//Any code before this is general for all
		double match1Count = 0.0;
		double match2Count = 0.0;
		int feature1Length = this.getFeature1().length();
		int feature2Length = this.getFeature2().length();			
		String feature1 = this.getFeature1();
		int mistakeAllowed1 = this.getMistakeAt(0);		
		String feature2 = this.getFeature2();
		int mistakeAllowed2 = this.getMistakeAt(1);
		if(GenerateArff.validateFeatureDNA(feature1) == false || GenerateArff.validateFeatureDNA(feature2) == false)
			throw new Error("Unknown Character in Feature");
		for(int x = startIndex; x <= endIndex && x < sequence.length(); x++){				
			int mistake = 0;
			int y;
			for(y = 0; y < feature1Length && (x+y) < sequence.length() && (x+y) <= endIndex; y++){
				char temp = sequence.charAt(x+y);
				if(feature1.charAt(y) != temp && feature1.charAt(y) != 'X')
					mistake++;							
			}
			if(y == feature1Length && mistake <= mistakeAllowed1)
				match1Count++;
			mistake = 0;			
			for(y = 0; y < feature2Length && (x+y) < sequence.length() && (x+y) <= endIndex; y++){
				char temp = sequence.charAt(x+y);
				if(feature2.charAt(y) != temp && feature2.charAt(y) != 'X')
					mistake++;	
			}
			if(y == feature2Length && mistake <= mistakeAllowed2)
				match2Count++;
		}
		if(match2Count == 0)
			return -1;
		else	
			return match1Count/match2Count;
		}

	@Override
	public Object computeProtein(FastaFormat fastaFormat,
			int scoringMatrixIndex, int countingStyleIndex,
			ScoringMatrix scoringMatrix) {
		String sequence = fastaFormat.getSequence().toUpperCase();
    	int startIndex = fastaFormat.getStartIndex(this.windowFrom,this.isPercentage);
    	int endIndex = fastaFormat.getEndIndex(this.windowTo,this.isPercentage);
		//Any code before this is general for all
    	
    	if(type == 'R'){
			double match1Count = 0.0;
			double match2Count = 0.0;
			int feature1Length = this.getFeature1().length();
			int feature2Length = this.getFeature2().length();			
			String feature1 = this.getFeature1();
			int mistakeAllowed1 = this.getMistakeAt(0);			
			String feature2 = this.getFeature2();
			int mistakeAllowed2 = this.getMistakeAt(1);
			if(GenerateArff.validateFeatureProtein(feature1) == false || GenerateArff.validateFeatureProtein(feature2) == false)
				throw new Error("Unknown Character in Features");
			for(int x = startIndex; x <= endIndex && x < sequence.length(); x++){				
				int mistake = 0;
				double score = 0.0;
				int y;
				for(y = 0; y < feature1Length && (x+y) < sequence.length() && (x+y) <= endIndex; y++){
					char temp = sequence.charAt(x+y);
					if(scoringMatrixIndex == 0){//identity matrix
						if(feature1.charAt(y) != temp && feature1.charAt(y) != 'X')
							mistake++;								
						else
							score++;
					}
					else{//blosum62 or structure-derived matrix
						score += scoringMatrix.getScore(feature1.charAt(y),temp);	
					}
				}
				if(scoringMatrixIndex == 0){//identity matrix
					if(y == feature1Length && mistake <= mistakeAllowed1){					
						if(countingStyleIndex == 0)
							match1Count++;
						else 
							match1Count += score;
					}
				}
				else{//blosum 62 or structure-derived matrix
					if(y == feature1Length && score >= 0){					
						if(countingStyleIndex == 0)
							match1Count++;
						else
							match1Count += score;				
					}
				}
				mistake = 0;
				score = 0.0;			
				for(y = 0; y < feature2Length && (x+y) < sequence.length() && (x+y) <= endIndex; y++){
					char temp = sequence.charAt(x+y);
					if(scoringMatrixIndex == 0){//identity matrix
						if(feature2.charAt(y) != temp && feature2.charAt(y) != 'X')
							mistake++;								
						else
							score++;
					}
					else{ //blosum62 or structure-derived matrix
						score += scoringMatrix.getScore(feature2.charAt(y),temp);
					}
				}
				if(scoringMatrixIndex == 0){//identity matrix
					if(y == feature2Length && mistake <= mistakeAllowed2){				
						if(countingStyleIndex == 0)
							match2Count++;
						else 
							match2Count += score;
					}
				}
				else{//blosum 62 or structure-derived matrix
					if(y == feature2Length && score >= 0){					
						if(countingStyleIndex == 0)
							match2Count++;
						else
							match2Count += score;				
					}
				}
			}
			if(match2Count == 0)
				return -1;
			else	
				return match1Count/match2Count;
    	}else if(type == 'O'){
			double match1Count = 0.0;
			double match2Count = 0.0;
			int feature1Length = this.getFeature1().length();
			int feature2Length = this.getFeature2().length();			
			String feature1 = this.getFeature1();
			int mistakeAllowed1 = this.getMistakeAt(0);			
			String feature2 = this.getFeature2();
			int mistakeAllowed2 = this.getMistakeAt(1);
			if(GenerateArff.validatePhysiochemicalFeatureProtein(feature1) == false || GenerateArff.validatePhysiochemicalFeatureProtein(feature2) == false)
				throw new Error("Unknown Character in Features");
			for(int x = startIndex; x <= endIndex && x < sequence.length(); x++){				
				int mistake = 0;
				int y;
				for(y = 0; y < feature1Length && (x+y) < sequence.length() && (x+y) <= endIndex; y++){
					char temp = sequence.charAt(x+y);					
					if(feature1.charAt(y) != 'X' && !GenerateArff.isPhysiochemicalMatch(feature1.charAt(y),temp))
						mistake++;									
				}				
				if(y == feature1Length && mistake <= mistakeAllowed1){									
					match1Count++;
					//The following line means that it would not allow AAAA to be counted as 3 in AA but as 2 instead
					//x += feature1Length - 1;
				}			
			}
			for(int x = startIndex; x <= endIndex && x < sequence.length(); x++){
				int mistake = 0;
				int y;
				for(y = 0; y < feature2Length && (x+y) < sequence.length() && (x+y) <= endIndex; y++){
					char temp = sequence.charAt(x+y);					
					if(feature2.charAt(y) != 'X' && !GenerateArff.isPhysiochemicalMatch(feature2.charAt(y),temp))
						mistake++;									
				}				
				if(y == feature2Length && mistake <= mistakeAllowed2){									
					match2Count++;
					//The following line means that it would not allow AAAA to be counted as 3 in AA but as 2 instead
					//x += feature2Length - 1;
				}
			}
			if(match2Count == 0)//Not divisable
				return -1;
			else	
				return match1Count/match2Count;		
		}else if(type == 'Q'){			
			double match1Count = 0.0;
			int featureLength1 = this.getFeature1().length();
			String feature1 = this.getFeature1();			
			int physiochemical2Index = this.physiochemical2Int;
			int mistakeAllowed1 = this.getMistakeAt(0);		
			Physiochemical2 p2 = new Physiochemical2(Physiochemical2.indexToName(physiochemical2Index));			
			if(GenerateArff.validatePhysiochemical2FeatureProtein(feature1, p2) == false)//contains unknown character
				throw new Error("Contain Unknown Character: " + feature1 + " Name: " + Physiochemical2.indexToName(physiochemical2Index));
			for(int x = startIndex; x <= endIndex && x < sequence.length(); x++){				
				int mistake = 0;				
				int y;
				for(y = 0; y < featureLength1 && (x+y) < sequence.length() && (x+y) <= endIndex; y++){
					char temp = sequence.charAt(x+y);									
					if(feature1.charAt(y)!='X' && !p2.isMatch(feature1.charAt(y),temp))//mismatch
						mistake++;										
				}							
				if(y == featureLength1 && mistake <= mistakeAllowed1)					
					match1Count++;				
			}		
			
			double match2Count = 0.0;
			int featureLength2 = this.getFeature1().length();
			String feature2 = this.getFeature1();						
			int mistakeAllowed2 = this.getMistakeAt(0);					
			if(GenerateArff.validatePhysiochemical2FeatureProtein(feature2, p2) == false)//contains unknown character
				throw new Error("Contain Unknown Character: " + feature2 + " Name: " + Physiochemical2.indexToName(physiochemical2Index));
			for(int x = startIndex; x <= endIndex && x < sequence.length(); x++){				
				int mistake = 0;				
				int y;
				for(y = 0; y < featureLength2 && (x+y) < sequence.length() && (x+y) <= endIndex; y++){
					char temp = sequence.charAt(x+y);									
					if(feature2.charAt(y)!='X' && !p2.isMatch(feature2.charAt(y),temp))//mismatch
						mistake++;										
				}
				if(y == featureLength2 && mistake <= mistakeAllowed2)					
					match2Count++;				
			}			
			
			if(match2Count == 0)//Not divisable
				return -1;
			else	
				return match1Count/match2Count;		
		}else throw new Error("Should not be here! RatioOfKGramFeature.computeProtein");
	}
	
	public static RatioOfKGramFeature randomlyGenerate(int windowFrom, int windowTo, Random rand){
		boolean isPercentage = rand.nextBoolean();
		int[] window;
		if(isPercentage)
			window = Feature.getWindow(0, 100, rand);
		else
			window = Feature.getWindow(windowFrom, windowTo, rand);							
		int type = rand.nextInt(3);//0 is normal, 1 is physio, 2 is physio2
		int physio2 = rand.nextInt(Physiochemical2.codingNameList.length);
		//this is to make sure that we do not have type2==0 and physio2==0 since would be same as type==0
		if(type == 2 && physio2 == 0)
			while(physio2 == 0)
				physio2 = rand.nextInt(Physiochemical2.codingNameList.length);
		String kgram = Feature.getGram(type,rand,physio2);		
		int m1 = Feature.getMistakeAllowed(rand, kgram.length());
		String kgram2 = Feature.getGram(type,rand,physio2);
		int m2 = Feature.getMistakeAllowed(rand, kgram2.length());
		switch(type){
		case 0: //normal
				return new RatioOfKGramFeature('R',kgram,kgram2,window[0],window[1],m1,m2,isPercentage);
		case 1: //Physiochemical
				return new RatioOfKGramFeature('O',kgram,kgram2,window[0],window[1],m1,m2,isPercentage);
		case 2: //Physiochemical2
				return new RatioOfKGramFeature('Q',kgram,kgram2,window[0],window[1],m1,m2,isPercentage,physio2);
		default: throw new Error("Unhandled Case: " + type);
		}
	}	
	private void mutateKgram(Random rand){
		int typeInt;
		if(this.type == 'R')
			typeInt = 0;
		else if(this.type == 'O')
			typeInt = 1;
		else
			typeInt = 2;
		if(rand.nextBoolean())
			this.kgram1 = this.mutateKgram(kgram1, rand, typeInt, this.physiochemical2Int);
		else
			this.kgram2 = this.mutateKgram(kgram2, rand, typeInt, this.physiochemical2Int);
	} 
	@Override
	public Feature mutate(Random rand, int windowMin, int windowMax) {					
		//Do point mutation	
		//first decide which part to mutate	
		int index = rand.nextInt(10);
		RatioOfKGramFeature feature = new RatioOfKGramFeature(this.name);		
		switch(index){
			//mutate kgram - 50% chance
			case 0:  
			case 1:
			case 2: 
			case 3: 
			case 4: feature.mutateKgram(rand); break;
			//mutate mistakeAllowed - 10% chance			 			
			case 5: if(rand.nextBoolean())
						feature.mistakeAllowed1 = Feature.getMistakeAllowed(rand, feature.kgram1.length());
					else
						feature.mistakeAllowed2 = Feature.getMistakeAllowed(rand, feature.kgram2.length());
			 		break;
			//mutate window location - 40% chance
			case 6: 
			case 7:
			case 8:
			case 9: feature.mutateWindow(rand,windowMin,windowMax); break;
		}			
		//normal or Physiochemical
		if(feature.type == 'R' || feature.type == 'O'){					
			Feature temp = new RatioOfKGramFeature(feature.type,feature.kgram1,feature.kgram2,feature.windowFrom,feature.windowTo,feature.mistakeAllowed1,
					feature.mistakeAllowed2,feature.isPercentage);
			//To ensure that something has changed after the mutation
			if(temp.name.equalsIgnoreCase(feature.name))
				return this.mutate(rand, windowMin, windowMax);
			else				
				return temp;			
		}
		//Physiochemical2
		else if(feature.type == 'Q'){	
			Feature temp = new RatioOfKGramFeature(feature.type,feature.kgram1,feature.kgram2,feature.windowFrom,feature.windowTo,feature.mistakeAllowed1,
					feature.mistakeAllowed2,feature.isPercentage,feature.physiochemical2Int);
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
