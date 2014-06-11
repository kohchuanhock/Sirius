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
package sirius.trainer.step1;

import javax.swing.table.*;
import java.util.*;

public class Step1TableModel extends AbstractTableModel{
	static final long serialVersionUID = sirius.Sirius.version;

	private String[] columnNames = {"File Location", "No of Seqs", "Seq No", "+1 Index"};
    private ArrayList<Step1Data> data;
    private int sequenceNumbering;//shows the number of each sequence in the table

    public Step1TableModel(){
    	setDataToNull();   	    		
    }
    
    public int getColumnCount(){
        return columnNames.length;
    }
    
    public void setDataToNull(){    	
    	for(int x = 0; x < getRowCount(); x++){
    		remove(x);
    	}
    	data = new ArrayList<Step1Data>();
    	sequenceNumbering = 1;    	    	
    }

    public int getRowCount() {    
    	if(data!=null)
        	return data.size();       
        else 
        	return -1;
    }
    
    public int size(){
    	return this.data.size();
    }
    
    public int getNumOfSequences(int index){
    	return this.data.get(index).getNumOfSequences();
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {    
    	if(data!=null)
    		return data.get(row).get(col);    
    	else
    		return " ";
    }
	 
    public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }   
    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    /*public void setValueAt(Object value, int row, int col) {
        setTargetLocationIndex(row,(String)value);
        fireTableCellUpdated(row, col);
    }*/
    public boolean isCellEditable(int row,int column){
    	/*if(column == 3)
    		return true;
    	else 
    		return false;*/
    	return false;
    }
    
    
    public void add(String absolutePath, int sequenceNum,int plusOneIndex){
    	int row = getRowCount();    	
    	Step1Data temp = new Step1Data(absolutePath,sequenceNum,sequenceNumbering,plusOneIndex);
    	sequenceNumbering += sequenceNum;
    	if(data == null)
    		data = new ArrayList<Step1Data>();    
    	data.add(temp);
    	fireTableRowsInserted(row,row);
    }
    
    public void remove(int index){
    	int returnValue = data.get(index).getNumOfSequences();
    	sequenceNumbering -= returnValue;
    	for(int x = index + 1; x < data.size(); x++){
    		data.get(x).adjust(returnValue);
    	}
    	data.remove(index);
    	fireTableRowsDeleted(index,index);    	
    }
    
    public String getFirstCol(int index){//gets the absolute path
    	return data.get(index).getFirstCol();
    }    
    
    public String toString(int index){
    	return "Absolute Path: " + data.get(index).getFirstCol() + 
    		" No of Sequences: " + data.get(index).getNumOfSequences() + 
    		" Sequence No: " + data.get(index).getThirdCol();
    }
    
    public int getSeqFrom(int index){
    	return data.get(index).getSeqFrom();
    }
    
    public int getSeqTo(int index){
    	return data.get(index).getSeqTo();
    }       
    	
    public int getTotalSequences(){    
    	return sequenceNumbering - 1;
    }
    public int getPlusOneIndex(int index){
    	return data.get(index).getPlusOneIndex();
    }
    public void setPlusOneIndex(int index,int plusOneIndex){
    	data.get(index).setPlusOneIndex(plusOneIndex);
    	fireTableRowsUpdated(index,index);
    }
    
    public String getAbsolutePath(int index){
    	return this.data.get(index).getAbsolutePath();
    }
}

class Step1Data{
	private String absolutePath;//store absolute path
	private int numOfSequences;//store num of sequence
	private int sequenceFrom;//store the seq from 
	private int sequenceTo;//store the seq to
	private int plusOneIndex;
	
	public Step1Data(String absolutePath, int sequenceNum, int sequenceNumbering,int plusOneIndex){
		this.absolutePath = absolutePath;
		this.numOfSequences = sequenceNum;		
		this.sequenceFrom = sequenceNumbering;
		this.sequenceTo = sequenceNumbering + sequenceNum - 1;
		this.plusOneIndex = plusOneIndex;
	}
	public String get(int col){
		if(col == 0)
			return absolutePath;
		else if(col == 1)
			return numOfSequences + "";
		else if(col == 2)			
			return sequenceFrom + " - " + sequenceTo;			
		else//col == 3
			if(plusOneIndex >= 0)
				return "non -1";
			else
				return "-1";		
	}
	
	public String getAbsolutePath(){
		return this.absolutePath;
	}
	
	public String getFirstCol(){
		return absolutePath;
	}
	public int getNumOfSequences(){
		return this.numOfSequences;
	}
	
	public String getThirdCol(){
		return "" + sequenceFrom + " - " + sequenceTo;
	}		
	
	public void adjust(int sequenceNum){
		sequenceFrom -= sequenceNum;
		sequenceTo -= sequenceNum;
	}
	public int getSeqFrom(){
		return sequenceFrom;
	}
	public int getSeqTo(){
		return sequenceTo;
	}
	public int getPlusOneIndex(){
		return plusOneIndex;
	}
	public void setPlusOneIndex(int plusOneIndex){
		this.plusOneIndex = plusOneIndex;
	}
}