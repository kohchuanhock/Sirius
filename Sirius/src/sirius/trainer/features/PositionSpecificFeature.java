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
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import sirius.main.ApplicationData;
import sirius.trainer.main.ScoringMatrix;
import sirius.trainer.step2.Physiochemical2;
import sirius.utils.FastaFormat;

public class PositionSpecificFeature extends Feature{	
	private int p2Index;
	
	public int getP2(){
		return this.p2Index;
	}
	
	public PositionSpecificFeature(String name){
		super(name,null,name.charAt(0));
		setSettings(name);
	}
		
	public PositionSpecificFeature(String line, char type){
		super(null, null, type);				
		this.name = line.substring(line.indexOf("Name: ") + 
				("Name: ").length(),line.indexOf("Physiochemical2: ") - 1);
		this.p2Index = Integer.parseInt(line.substring(line.indexOf("Physiochemical2: ") + 
				("Physiochemical2: ").length()));		
		setSettings(this.name);
	}
	
	private void setSettings(String name){
		StringTokenizer st = new StringTokenizer(name, "_");
		st.nextToken();//P
		this.windowFrom = Integer.parseInt(st.nextToken());//windowFrom
		this.windowTo = Integer.parseInt(st.nextToken());//windowTo
		this.details = "";
		while(st.hasMoreTokens()){
			if(st.countTokens() == 1)//last token
				this.p2Index = Integer.parseInt(st.nextToken());
			else{
				this.details += st.nextToken();
				if(st.hasMoreTokens() && st.countTokens() != 1)
					this.details += "";
			}
		}
		this.details += ", Position-Specific Feature, Window(" + this.windowFrom + "," + this.windowTo +  ")";
		if(this.p2Index != 0)
			this.details += ", " + Physiochemical2.indexToName(this.p2Index);	
	}
	
	public String saveString(String saveDirectory){
		if(type == 'P')
			return "Type: " + this.type + " Name: " + this.name + " Physiochemical2: " + this.p2Index;
		else
			return "UNKNOWN TYPE";
	}	
	
	@Override
	public Object computeDNA(FastaFormat fastaFormat) {
		String sequence = fastaFormat.getSequence().toUpperCase();
    	int startIndex = fastaFormat.getStartIndex(this.windowFrom,this.isPercentage);
    	int endIndex = fastaFormat.getEndIndex(this.windowTo,this.isPercentage);
		//Any code before this is general for all
		
		String name = this.getName();
		StringTokenizer st = new StringTokenizer(name, "_");
		st.nextToken(); //'P'
		st.nextToken(); //windowFrom
		st.nextToken(); //windowTo
		boolean found = true;
		for(int x = startIndex; x <= endIndex && x < sequence.length() && st.countTokens() > 1; x++){
			String currentString = st.nextToken();
			if(currentString.indexOf("" + sequence.charAt(x)) != -1){					
			}else if(currentString.indexOf("X") != -1 || currentString.indexOf("N") != -1){
				//X and N means this position is ignored
			}else{
				//one of the position is not satisfied
				found = false;
			}
		}
		if(found)
			return 1.0;
		else 
			return 0.0;
	}

	@Override
	public Object computeProtein(FastaFormat fastaFormat,
			int scoringMatrixIndex, int countingStyleIndex,
			ScoringMatrix scoringMatrix) {
		String sequence = fastaFormat.getSequence().toUpperCase();
    	int startIndex = fastaFormat.getStartIndex(this.windowFrom,this.isPercentage);
    	int endIndex = fastaFormat.getEndIndex(this.windowTo,this.isPercentage);
		//Any code before this is general for all
		String name = this.getName();
		StringTokenizer st = new StringTokenizer(name, "_");
		st.nextToken(); //'P'
		st.nextToken(); //windowFrom
		st.nextToken(); //windowTo
		boolean found = true;
		Physiochemical2 p2 = null;
		if(this.p2Index != 0)
		p2 = new Physiochemical2(Physiochemical2.indexToName(this.p2Index));		
		for(int x = startIndex; x <= endIndex && x < sequence.length() && st.countTokens() > 1; x++){
			String currentString = st.nextToken();
			if(this.p2Index == 0){//original
				if(currentString.indexOf("" + sequence.charAt(x)) != -1){					
				}else if(currentString.indexOf("X") != -1){					
				}else{
					//one of the position is not satisfied
					found = false;
					break;
				}
			}else{
				if(currentString.indexOf("X") == -1){//if X is not inside this position
					boolean match = false;
					for(int a = 0; a < currentString.length(); a++){
						if(p2.isMatch(currentString.charAt(a),sequence.charAt(x)))
							match = true;// at least one matched
					}			
					if(match == false){
						found = false;
						break;
					}
				}
			}
		}			
		if(found)
			return 1.0;
		else 
			return 0.0;
	}
	
	public static PositionSpecificFeature randomlyGenerate(int windowFrom, int windowTo,Random rand, ApplicationData applicationData){
		int positionFrom = Feature.randomBetween(windowFrom, windowTo, rand);
		//here, i limit the max size of ps feature to 15 but i will allow it to increase with Mutation and Crossover
		int max = positionFrom + 15;
		if(max > windowTo)
			max = windowTo;		
		int positionTo = Feature.randomBetween(positionFrom, max, rand);
		
		String featureName = "P_" + positionFrom + "_" + positionTo;
		int physio2 = rand.nextInt(Physiochemical2.codingNameList.length);
		for(int x = positionFrom; x <= positionTo; x++){
			if(applicationData.isLocationIndexMinusOne == false && x == 0)
				continue;
			featureName += "_";			
			//
			featureName += removeRepeats(PositionSpecificFeature.physiochemical2Gram(rand,physio2));					
		}			
		featureName += "_" + physio2;
		return new PositionSpecificFeature(featureName);
	}
	
	private static String removeRepeats(String kgram){
		String returnString = "";
		for(int x = 0; x < kgram.length(); x++)
			if(returnString.indexOf(kgram.charAt(x)) == -1)
				returnString += kgram.charAt(x);
		return returnString;
	}
		
	private static String physiochemical2Gram(Random rand, int p2){
		String gram = "";
		Physiochemical2 p = new Physiochemical2(Physiochemical2.indexToName(p2));
		//+1 because length must be at least one
		int length = rand.nextInt(p.getClassificationLetter().size()) + 1;
		for(int x = 0; x < length; x++)
			//-1 ensures that X is not included in the kgram
			gram += p.getLetter(rand.nextInt(p.getClassificationLetter().size()-1));
		return gram;
	}	
	
	private Feature add(Random rand, int positionFrom, int positionTo, List<String> stringList, int p2, int windowMin){
		int mutate = rand.nextInt(stringList.size());
		stringList.add(mutate, removeRepeats(PositionSpecificFeature.physiochemical2Gram(rand,p2)));
		if(rand.nextBoolean() && (positionFrom - 1) >= windowMin)
			positionFrom -= 1;
		else//no need to check windowMax because positionspecific would only be non-percentage and hence it is fine even if it goes beyond windowMax
			positionTo += 1;
		return this.stringToPositionSpecificFeature(positionFrom, positionTo, stringList, p2);
	}
	
	private Feature remove(Random rand, int positionFrom, int positionTo, List<String> stringList, int p2){
		int mutate = rand.nextInt(stringList.size());
		stringList.remove(mutate);
		if(rand.nextBoolean())
			positionFrom += 1;
		else
			positionTo -= 1;
		return this.stringToPositionSpecificFeature(positionFrom, positionTo, stringList, p2);
	}
	
	private Feature changeGrams(Random rand, int positionFrom, int positionTo, List<String> stringList, int p2){		
		int mutate = rand.nextInt(stringList.size());
		stringList.set(mutate, removeRepeats(PositionSpecificFeature.physiochemical2Gram(rand,p2)));
		return this.stringToPositionSpecificFeature(positionFrom, positionTo, stringList, p2);
	}
	
	private Feature changeLocation(Random rand, int positionFrom, int positionTo, List<String> stringList, int p2, int windowMin, int windowMax){			
		int windowSize = (windowMax - windowMin) + 1;
		while(stringList.size() > windowSize)
			this.remove(rand, positionFrom, positionTo, stringList, p2);
		do
			positionFrom = Feature.randomBetween(windowMin, windowMax, rand);
		while(positionFrom + stringList.size() - 1 > windowMax);
		positionTo = positionFrom + stringList.size() - 1;
		return this.stringToPositionSpecificFeature(positionFrom, positionTo, stringList, p2);
	}
	
	private Feature stringToPositionSpecificFeature(int positionFrom, int positionTo, List<String> stringList, int p2){
		String name = "P_" + positionFrom + "_" + positionTo;
		for(int x = 0; x < stringList.size(); x++)
			name += "_" + stringList.get(x);
		name += "_" + p2;
		return new PositionSpecificFeature(name);
	}

	@Override
	public Feature mutate(Random rand, int windowMin, int windowMax) {
		//first three tokens are P,positionfrom,positionto
		//last token is p2Int
		StringTokenizer st = new StringTokenizer(this.name,"_");
		st.nextToken();//P
		int positionFrom = Integer.parseInt(st.nextToken());
		int positionTo = Integer.parseInt(st.nextToken());
		List<String> stringList = new ArrayList<String>();
		while(st.hasMoreTokens() && st.countTokens() > 1){
			stringList.add(st.nextToken());
		}
		int p2 = Integer.parseInt(st.nextToken());
		int randIndex = 4;
		if(stringList.size() == 1)
			randIndex -= 1;
		int index = rand.nextInt(randIndex);
		Feature temp = null;
		switch(index){
		//add one position
		case 0: temp = this.add(rand, positionFrom, positionTo, stringList, p2, windowMin);	break;	
		//change grams
		case 1: temp = this.changeGrams(rand, positionFrom, positionTo, stringList, p2); break;
		//change location
		case 2: temp = this.changeLocation(rand, positionFrom, positionTo, stringList, p2, windowMin, windowMax); break;
		//remove one position
		case 3: temp = this.remove(rand, positionFrom, positionTo, stringList, p2); break;
		default: throw new Error("Unhandled case: " + index);
		}				
		if(temp.name.equalsIgnoreCase(this.name))
			return this.mutate(rand, windowMin, windowMax);
		else
			return temp;
	}
}
