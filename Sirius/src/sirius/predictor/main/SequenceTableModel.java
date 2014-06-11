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

import javax.swing.table.*;

public class SequenceTableModel extends AbstractTableModel{
	static final long serialVersionUID = sirius.Sirius.version;

    private String[] columnNames = {"Position", "Sequence"};
	private String sequence;
	private String header;
	private int sequenceLengthPerRow;
	private int spacingEveryLength;
    
	public SequenceTableModel(){
		this.sequence = "";
    	this.sequenceLengthPerRow = 50;
    	this.spacingEveryLength = 10;
	}
	
    public SequenceTableModel(int sequenceLengthPerRow, int spacingEveryLength) {
    	this.sequence = "";
    	this.sequenceLengthPerRow = sequenceLengthPerRow;
    	this.spacingEveryLength = spacingEveryLength;
    }
    
    public String getColumnName(int col) {
        return columnNames[col];
    }
    
    public int getColumnCount(){
        return columnNames.length;//Position, Sequence
    }
    
    public int getRowCount() {
    	int returnValue = 0;
    	if(sequence.length() != 0){    		
    		returnValue += (sequence.length()/this.sequenceLengthPerRow) + 1;
    		if(sequence.length()%this.sequenceLengthPerRow!=0)
    			returnValue++;
    	}
    	return returnValue;		
    }       
    
    public Object getValueAt(int row, int col) {
    	if(row == 0){    		
    		switch(col){
    			case 0: return "Header";
    			case 1: return this.header;
    		}
    	}
    	row--;
    	int from = ((row*this.sequenceLengthPerRow) + 1);
    	int to = ((row*this.sequenceLengthPerRow) + this.sequenceLengthPerRow);
    	if(col == 0){
    		if(sequence.length() >= to)		
	    		return from + " - " + to;
	    	else
	    		return from + " - " + sequence.length();
    	}else if(col == 1){
    		String returnString;
    		if(sequence.length() >= to)
    			returnString = sequence.substring(from-1,to);    		
    		else
    			returnString = sequence.substring(from-1);
    		String finalReturnString = "";
    		for(int x = 0; x < returnString.length(); x+=this.spacingEveryLength){
    			if(x+this.spacingEveryLength < returnString.length())
    				finalReturnString += returnString.substring(x, x+this.spacingEveryLength) + "   ";
    			else
    				finalReturnString += returnString.substring(x) + "   ";
    		}	
    		return finalReturnString;
    	}
    	return "";
    }
    public void setSequence(String header, String sequence){
    	this.sequence = sequence;
    	this.header = header;
    	fireTableRowsInserted(0,(sequence.length()/50)+1);
    }
}
