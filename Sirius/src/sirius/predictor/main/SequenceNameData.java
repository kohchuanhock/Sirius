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

package sirius.predictor.main;

import java.util.StringTokenizer;

import sirius.utils.FastaFormat;

public class SequenceNameData {
	String header;
	String sequence;
	String scoreLine;
	String RID = null;
	String localID = null;
	int row;//this is for the undo to insert back to the correct location
	Boolean checked = false;
	boolean ready = false;
	String outputDirectory;
	
	public SequenceNameData(String sequenceName,String sequence,String scoreLine, String RID, String localID){
		this.header = sequenceName;
		this.sequence = sequence;
		this.scoreLine = scoreLine;
		this.RID = RID;
		this.localID = localID;
	}
	
	public SequenceNameData(String sequenceName,String sequence,String scoreLine){
		this.header = sequenceName;
		this.sequence = sequence;
		this.scoreLine = scoreLine;
	}
	
	public void setBox(Boolean value){
		this.checked = value;
	}
		
	public void setRow(int row){
		this.row = row;
	}
	
	public int getRow(){
		return this.row;
	}
	
	public String getRID(){
		return this.RID;
	}
	
	public void setRID(String RID){
		this.RID = RID;
	}
	
	public String getLocalID(){
		if(this.localID == null){
			StringTokenizer st = new StringTokenizer(this.header);
			this.localID = st.nextToken();
			this.localID = this.localID.replaceAll(">", "");
			this.localID = this.localID.trim();
		}
		return this.localID;
	}
	
	public Object get(int col){
		if(col == 2)
			return header;
		else if(col == 3)
			return sequence.length();
		else if(col == 0)
			return checked;
		return null;
	}
	public FastaFormat getFastaFormat(){
		return new FastaFormat(this.header, this.sequence);
	}
	public String getSequence(){
		return this.sequence;
	}
	public String getHeader(){
		return this.header;
	}
	public String getScoreLine(){
		return this.scoreLine;
	}
	public double getScore(){
		return getScore(0);
	}
	public double getScore(int pos){
		double returnValue = -1.0;
		try{
			String firstPart = scoreLine.substring(scoreLine.indexOf(pos + "=") + (pos + "=").length());
			if(firstPart.indexOf(",") != -1)
				returnValue = Double.parseDouble(firstPart.substring(0, firstPart.indexOf(",")));
			else
				returnValue = Double.parseDouble(firstPart.substring(0));
		}catch(NumberFormatException ex){
			ex.printStackTrace();			
			throw new Error("Error in SequenceNameData.getScore()");
		}
		return returnValue;
	}
}