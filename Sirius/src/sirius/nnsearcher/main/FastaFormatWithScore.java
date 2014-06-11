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
package sirius.nnsearcher.main;

import sirius.utils.FastaFormat;

public class FastaFormatWithScore {
	private FastaFormat fastaFormat;
	private double score;
	private double pValue;
	private int pValueCount;
	
	public FastaFormatWithScore(FastaFormat fastaFormat, double score){
		this(fastaFormat, score, 0.0);
	}
	
	public FastaFormatWithScore(FastaFormat fastaFormat, double score, double pValue){
		this.fastaFormat = fastaFormat;
		this.score = score;
		this.pValue = pValue;
		this.pValueCount = 0;
	}
	
	public double getScore(){
		return this.score;
	}
	
	public String getHeader(){
		return this.fastaFormat.getHeader();
	}	
	
	public String getSequence(){
		return this.fastaFormat.getSequence();
	}
	
	public FastaFormat getFastaFormat(){ return this.fastaFormat; }		
	
	public void setPValue(long numOfRuns){
		this.pValue = (double)this.pValueCount / (double)numOfRuns;
	}
	
	public int getPValueCount(){
		return this.pValueCount;		
	}
	
	public void incrementPValueCount(){
		this.pValueCount++;
	}
	
	public double getPValue(){					
		return this.pValue;
	}
}
