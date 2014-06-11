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

import java.util.Hashtable;
import java.util.Random;
import java.util.StringTokenizer;

import sirius.trainer.main.ScoringMatrix;
import sirius.utils.FastaFormat;

public class Basic2PhysiochemicalFeature extends Feature{
	private int lengthCutoff;
	private int valueCutoff;
	public static enum Basic2PhysioType{Hydrophobic, Hydrophilic, Alkaline, Acidic, OrderAA, DisorderAA,
		NetChargePositive, NetChargeNegative};
	public static enum Basic2ValueType{MaxValue, NumRegionGreaterThanX, NumRegionGreaterThanY};
		
	public Basic2PhysiochemicalFeature(char type, String name, String details, boolean box){
		super(name, details, type, box);
		StringTokenizer st = new StringTokenizer(name, "_");
		st.nextToken();//C
		st.nextToken();//the type of AA
		st.nextToken();//the feature (maxValue or # region > x or # region > Y)
		this.valueCutoff = Integer.parseInt(st.nextToken());// 0 for maxValue X value for
		this.lengthCutoff = Integer.parseInt(st.nextToken());
	}
	
	public Basic2PhysiochemicalFeature(String name){
		super(name, null, name.charAt(0));
		try{
			StringTokenizer st = new StringTokenizer(name, "_");
			st.nextToken();//C
			String typeAA = st.nextToken();//the type of AA
			String feature = st.nextToken();//the feature (maxValue or # region > x or # region > Y)
			this.valueCutoff = Integer.parseInt(st.nextToken());// 0 for maxValue X value for
			this.lengthCutoff = Integer.parseInt(st.nextToken());
			setDetails(feature,typeAA);
		}catch(Exception e){throw new Error("Error in Basic2PhysiochemicalFeature(String name)");}
	}
	
	public Basic2PhysiochemicalFeature(String name, char type, int lengthCutoff, int valueCutoff, String feature, String typeAA){
		super(name, null, type);
		this.name = name;
		this.lengthCutoff = lengthCutoff;
		this.valueCutoff = valueCutoff;		
		setDetails(feature,typeAA);
	}
	
	public Basic2PhysiochemicalFeature(String line, char type){
		super(null,null,type);
		String name = line.substring(line.indexOf("Name: ") + ("Name: ").length());
		this.name = name;
		StringTokenizer st = new StringTokenizer(name, "_");
		st.nextToken();//C
		String typeAA = st.nextToken();//the type of AA
		String feature = st.nextToken();//the feature (maxValue or # region > x or # region > Y)
		this.valueCutoff = Integer.parseInt(st.nextToken());// 0 for maxValue X value for
		this.lengthCutoff = Integer.parseInt(st.nextToken());
		
		setDetails(feature,typeAA);
		
	}
	
	private void setDetails(String feature,String typeAA){
		switch(Basic2ValueType.valueOf(feature)){
		case MaxValue: this.details = typeAA + ", Max Value, Window(" + this.lengthCutoff +  ")"; break;
		case NumRegionGreaterThanX: this.details = "# Region with > " + this.valueCutoff + 
			" number of " + typeAA + " Amino Acid, Window(" + this.lengthCutoff + ")"; break;
		case NumRegionGreaterThanY: this.details = "# Region with > " + this.valueCutoff + 
			" value of " + typeAA + " Amino Acid, Window(" + this.lengthCutoff + ")"; break; 
		default: throw new Error("Unknown Feature: " + feature); 		
		}				
	}
	
	public int getValueCutoff(){ return this.valueCutoff; }
	
	public String saveString(String saveDirectory){
		if(type == 'C')
			return "Type: " + type + " Name: " + name;
		else return "UNKNOWN TYPE";
	}

	@Override
	public Object computeDNA(FastaFormat fastaFormat) {
		throw new Error("Basic2PhysiochemicalFeature.computeDNA should not be called!");	
	}

	@Override
	public Object computeProtein(FastaFormat fastaFormat,
			int scoringMatrixIndex,int countingStyleIndex,ScoringMatrix scoringMatrix) {
		String sequence = fastaFormat.getSequence().toUpperCase();
    
		//Any code before this is general for all
		
		StringTokenizer stLocal = new StringTokenizer(this.getName(), "_");
		stLocal.nextToken();//C
		String featureType = stLocal.nextToken();//AA type
		String feature = stLocal.nextToken();//feature type (max Value or # region > X)
		int value = Integer.parseInt(stLocal.nextToken());
		int slidingWindowSize = Integer.parseInt(stLocal.nextToken());//Sliding Window size
		
		Hashtable<Character, Double> hashtable = null;
		switch(Basic2PhysioType.valueOf(featureType)){
		case Hydrophobic: hashtable = GenerateArff.aminoAcidHydrophobicity; break;
		case Hydrophilic: hashtable = GenerateArff.aminoAcidHydrophobicity_neg; break;
		case Alkaline: hashtable = GenerateArff.aminoAcidPKa_wrt7_pos; break;
		case Acidic: hashtable = GenerateArff.aminoAcidPKa_wrt7_neg; break;
		case OrderAA: hashtable = GenerateArff.orderAminoAcid; break;
		case DisorderAA: hashtable = GenerateArff.disorderAminoAcid; break;
		case NetChargePositive: hashtable = GenerateArff.aminoAcidCharge; break;
		case NetChargeNegative: hashtable = GenerateArff.aminoAcidCharge_neg; break;
		default: throw new Error("Unknown Amino Acid Type: " + featureType);
		}
		
		switch(Basic2ValueType.valueOf(feature)){
		case MaxValue: return getBasic2MaxValue(sequence, hashtable, slidingWindowSize);
		case NumRegionGreaterThanX: 
			return getBasic2NumRegionGreaterThanX(sequence, hashtable, value, slidingWindowSize);
		case NumRegionGreaterThanY:  
			return getBasic2NumRegionGreaterThanY(sequence, hashtable, value, slidingWindowSize);
		default: throw new Error("Unknown Feature: " + feature); 		
		}
	}	
	
	private double getBasic2MaxValue(String sequence, Hashtable<Character, Double> hashtable, int slidingWindow){
		double maxValue = Double.NEGATIVE_INFINITY;
		for(int x = 0; x < sequence.length(); x++){			
			double localValue = 0.0;
			for(int y = 0; y < slidingWindow && x+y < sequence.length(); y++){	
				if(hashtable.get(sequence.charAt(x+y)) != null)
					localValue += hashtable.get(sequence.charAt(x+y));
			}
			if(localValue > maxValue)
				maxValue = localValue;
		}		
		return maxValue;
	}
	
	private double getBasic2NumRegionGreaterThanX(String sequence, Hashtable<Character, Double> hashtable, int value, int slidingWindow){
		double count = 0;
		for(int x = 0; x + slidingWindow - 1 < sequence.length(); x++){			
			double localCount = 0.0;
			for(int y = 0; y < slidingWindow; y++){		
				if(hashtable.get(sequence.charAt(x+y)) != null && hashtable.get(sequence.charAt(x+y)) > 0)
					localCount++;				
			}
			//This is to prevent double counting
			//Once I got 1, shift it by the slidingWindowLength
			if(localCount > value){
				x += slidingWindow - 1;
				count++;
			}
		}
		return count;
	}
	
	private double getBasic2NumRegionGreaterThanY(String sequence, Hashtable<Character, Double> hashtable, double value, int slidingWindow){		
		double count = 0;
		for(int x = 0; x + slidingWindow - 1 < sequence.length(); x++){			
			double localValue = 0.0;
			for(int y = 0; y < slidingWindow; y++){	
				if(hashtable.get(sequence.charAt(x+y)) != null)
					localValue += hashtable.get(sequence.charAt(x+y));
			}
			//This is to prevent double counting
			//Once I got 1, shift it by the slidingWindowLength
			if(localValue > value){
				x += slidingWindow - 1;
				count++;
			}
		}
		return count;
	}
	
	public static Basic2PhysiochemicalFeature randomlyGenerate(int windowFrom, int windowTo, Random rand){				
		String featureName = "C";
		Basic2PhysioType[] basic2PhysioType = Basic2PhysioType.values();
		featureName += "_" + basic2PhysioType[rand.nextInt(basic2PhysioType.length)];
		//0 - Max Value 
		//1 - # of region with > X number of feature type 
		//2 - # of region with value > Y
		Basic2ValueType[] basic2ValueType = Basic2ValueType.values();
		int valueType = rand.nextInt(basic2ValueType.length);		
		featureName += "_" + basic2ValueType[valueType];
		int windowSizeLimit = windowTo - windowFrom;
		if(windowSizeLimit > 30)//dun allow it to have crazy range.. Limit to 30 for the start
			windowSizeLimit = 30;
		if(valueType == 0){
			featureName += "_" + 0; 
			//+3 to ensure that windowSize is at least 3 - windowrange would be from 3 to 33 with this
			featureName += "_" + (rand.nextInt(windowSizeLimit) + 3);			
		}else if(valueType == 1){
			//+3 to ensure windowSize is at least 3 - windowrange would be from 3 to 33 with this
			int windowSize = rand.nextInt(windowSizeLimit) + 3;
			//ensure that value be at least windowSize/3
			int value = windowSize/3 + rand.nextInt(windowSize*2/3);
			featureName += "_" + value; 
			featureName += "_" + windowSize;
		}else if(valueType == 2){
			//+3 to ensure windowSize is at least 3 - windowrange would be from 3 to 33 with this
			int windowSize = rand.nextInt(windowSizeLimit) + 3;
			int value = rand.nextInt(windowSize*2);			
			featureName += "_" + value; 
			featureName += "_" + windowSize;
		}
		return new Basic2PhysiochemicalFeature(featureName);
	}

	@Override
	public Feature mutate(Random rand, int windowMin, int windowMax) {
		StringTokenizer st = new StringTokenizer(this.name,"_");		
		String featureName = st.nextToken() + "_" + st.nextToken();
		String valueType = st.nextToken();
		String value = st.nextToken();
		String window = st.nextToken();
		int windowInt = Integer.parseInt(window);
		int valueInt = Integer.parseInt(value);
		int randIndex;
		if(valueType.equalsIgnoreCase("MaxValue"))//because value has no meaning in MaxValue hence no point mutating it
			randIndex = rand.nextInt(2);
		else
			randIndex = rand.nextInt(3);
		if(randIndex == 0){//mutate valueType
			Basic2ValueType[] basic2ValueType = Basic2ValueType.values();
			valueType = basic2ValueType[rand.nextInt(basic2ValueType.length)].toString();
		}else if(randIndex == 1){
			//mutate window			
			do{
				windowInt = Integer.parseInt(window);
				int windowShift = rand.nextInt(30);
				if(rand.nextBoolean())
					windowInt += windowShift;
				else 
					windowInt -= windowShift;
			}while(windowInt < 1);
			window = windowInt + "";
		}else{
			//mutate value
			if(valueType.equalsIgnoreCase("NumRegionGreaterThanX")){
				do
					//shift by at range of 1 to window/3
					if(rand.nextBoolean())
						valueInt += (rand.nextInt(windowInt/3) + 1);
					else
						valueInt -= (rand.nextInt(windowInt/3) + 1);
				while(valueInt < 1 || valueInt > windowInt);
			}else{//# of region with value > Y				
				//shift by range 1 - 10
				if(rand.nextBoolean())
					valueInt += (rand.nextInt(10) + 1);
				else
					valueInt -= (rand.nextInt(10) + 1);				
			}
			value = valueInt + "";			
		}
		featureName += "_" + valueType + "_" + value + "_" + window;
		if(this.name.equalsIgnoreCase(featureName))
			return this.mutate(rand, windowMin, windowMax);
		else
			return new Basic2PhysiochemicalFeature(featureName);	
	}
}
