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
package sirius.misc.redundancyreduction;

import java.util.*;

public class RedundancyReductionData {
	String sequenceName;
	String sequence;	
	boolean ranTest;
	ArrayList<SimilarSequence> arrayOfSimilarSequence;	
	
	public RedundancyReductionData(String sequenceName,String sequence){
		this.sequenceName = sequenceName;
		this.sequence = sequence;		
		this.arrayOfSimilarSequence = new ArrayList<SimilarSequence>();
		this.ranTest = false;
	}
	
	//Reset the similar Sequences to this sequence
	public void reset(){
		if(this.ranTest == true){
			this.arrayOfSimilarSequence = new ArrayList<SimilarSequence>();
			this.ranTest = false;
		}		
	}
		
	public void addSimilarSequence(RedundancyReductionData data, short similarityPercent){		
		this.arrayOfSimilarSequence.add(new SimilarSequence(data, similarityPercent));
	}
	
	public ArrayList<SimilarSequence> getSimilarSequenceArray(){
		return this.arrayOfSimilarSequence;
	}	
	public void ranTest(){
		this.ranTest = true;
	}
	public String getSequence(){
		return this.sequence;
	}
	public String getHeader(){
		return this.sequenceName;
	}
	
	public int getNumOfSimilarSequence(){		
		if(this.ranTest == false)
			return -1;
		return this.arrayOfSimilarSequence.size();
	}
	public String getRangePercent(){
		if(this.ranTest == false)
			return "";
		if(this.arrayOfSimilarSequence.size() == 0)
			return "";
		int maxPercent = 0;
		int minPercent = 100;
		for(int x = 0; x < this.arrayOfSimilarSequence.size(); x++){
			int currentPercent = this.arrayOfSimilarSequence.get(x).getSimilarityPercent();
			if(currentPercent < minPercent)
				minPercent = currentPercent;
			if(currentPercent > maxPercent)
				maxPercent = currentPercent;
		}
		return minPercent + " - " + maxPercent;
	}
	public int getMinSimilarPercent(){
		if(this.arrayOfSimilarSequence.size() == 0)
			return 0;
		int minPercent = 100;
		for(int x = 0; x < this.arrayOfSimilarSequence.size(); x++){
			int currentPercent = this.arrayOfSimilarSequence.get(x).getSimilarityPercent();
			if(currentPercent < minPercent)
				minPercent = currentPercent;			
		}
		return minPercent;
	}
	public int getMaxSimilarPercent(){
		if(this.arrayOfSimilarSequence.size() == 0)
			return 0;
		int maxPercent = 0;
		for(int x = 0; x < this.arrayOfSimilarSequence.size(); x++){
			int currentPercent = this.arrayOfSimilarSequence.get(x).getSimilarityPercent();
			if(currentPercent > maxPercent)
				maxPercent = currentPercent;			
		}
		return maxPercent;
	}
	
	public boolean deleteSimilarSequenceData(RedundancyReductionData data){
		for(int x = 0; x < this.arrayOfSimilarSequence.size(); x++){
			if(data == this.arrayOfSimilarSequence.get(x).getSimilarSequence()){
				this.arrayOfSimilarSequence.remove(x);
				return true;
			}
		}
		return false;
	}
}

class SimilarSequence{	
	private short similarityPercent;
	private RedundancyReductionData similarSequence;
	
	public SimilarSequence(RedundancyReductionData similarSequence, short similarityPercent){			
		this.similarityPercent = similarityPercent;
		this.similarSequence = similarSequence;			
	}		
	
	public int getSimilarityPercent(){
		return this.similarityPercent;
	}
	
	public RedundancyReductionData getSimilarSequence(){
		return this.similarSequence;
	}	
}