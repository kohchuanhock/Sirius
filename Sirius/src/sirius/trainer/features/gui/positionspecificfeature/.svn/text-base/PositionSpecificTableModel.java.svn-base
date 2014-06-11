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
package sirius.trainer.features.gui.positionspecificfeature;

import javax.swing.table.AbstractTableModel;

import sirius.main.ApplicationData;
import sirius.trainer.step2.Physiochemical2;

import java.util.*;

public class PositionSpecificTableModel extends AbstractTableModel{
	static final long serialVersionUID = sirius.Sirius.version;
	private String[] columnNames;
	private ApplicationData applicationData;
	private boolean positionSpecificTableModel;
	private ArrayList<ArrayList<Boolean>> selection;
	private Physiochemical2 p2; 
	
	public PositionSpecificTableModel(ApplicationData applicationData, boolean positionSpecificTableModel){
		this.applicationData = applicationData;
		this.positionSpecificTableModel = positionSpecificTableModel;
		this.p2 = new Physiochemical2("Original");
	}
	
	public void setP2(String codingName){
		this.p2 = new Physiochemical2(codingName);
		this.fireTableDataChanged();
	}
	
	public String getColumnName(int col) {
		if(this.positionSpecificTableModel){
			return columnNames[col];
		}else{
			return "Char";
		}
   	}
	
	public Class<?> getColumnClass(int c) {
    	return getValueAt(0, c).getClass();
    }
	
	public int getColumnCount(){
		if(this.positionSpecificTableModel){
			if(columnNames == null)
				return 0;
			else
				return columnNames.length;
		}else{
			return 1;
		}
	}
    
    public int getRowCount() {    
    	if(applicationData.getSequenceType().indexOf("PROTEIN") != -1)
    		return this.p2.getClassificationLetter().size() + 1;//+1 because one row is taken up by ALL checkboxs    	
    	else if(applicationData.getSequenceType().indexOf("DNA") != -1)
    		return 6;
    	else 
    		return -1;
    }   
    
    public boolean isCellEditable(int row,int column){
    	return true;
    }
    
    public void setValueAt(Object value, int row, int col) {
    	boolean currentValue = this.selection.get(col).get(row);
        this.selection.get(col).set(row, !currentValue);        
        if(row == 0)
        	//this.getRowCount() - 1, the -1 is because I want to skip the last character which is any
        	for(int y = 1; y < this.getRowCount() - 1; y++){
        			this.selection.get(col).set(y, this.selection.get(col).get(row));
        			fireTableCellUpdated(y, col);
        	}
        fireTableCellUpdated(row, col);
    }
    
    private String getCode(int row){
    	if(applicationData.getSequenceType().indexOf("PROTEIN") != -1){  
    		return this.p2.getClassificationLetter().get(row-1) + "";			
		}else{
			switch(row){
				case 1: return "A"; //adenosine
				case 2: return "C"; //cytidine
				case 3: return "T"; //guanine
				case 4: return "G"; //thymidine				
				case 5: return "X"; //any							
			}
		}	
    	return null;
    }
    
    public Object getValueAt(int row, int col) {    
    	if(this.positionSpecificTableModel == false){
    		if(row == 0)
    			return "All";    		
    		return getCode(row);
    	}else
    		return selection.get(col).get(row);
    }
    
    public String getSelectedString(int col){
    	String returnString = "";
    	for(int x = 0; x < selection.get(col).size(); x++){
    		if(selection.get(col).get(x))
    			returnString += getCode(x);
    	}
    	if(returnString.length() == 0)
    		return "X";
    	else
    		return returnString;
    }
    
    public void setColumnName(boolean isLocationIndexMinusOne, int positionFrom, int positionTo){
    	int size = positionTo - positionFrom + 1;
    	if(isLocationIndexMinusOne == false){
    		if(positionTo > 0 && positionFrom < 0){
    			//since for this 0 should not be included hence have to minus one
    			size -= 1;
    		}
    	}   
    	this.selection = new ArrayList<ArrayList<Boolean>>();
    	this.selection.add(new ArrayList<Boolean>());
    	this.columnNames = new String[size];    	
    	int rowSize = this.getRowCount();    	
    	for(int x = positionFrom, y = 0; x <= positionTo; x++){
    		if(x == 0 && isLocationIndexMinusOne == false)
    			continue;    		
    		this.columnNames[y] = "" + x;    		
    		this.selection.add(new ArrayList<Boolean>());   
    		for(int z = 0; z <= rowSize; z++){
    			this.selection.get(y).add(new Boolean(false));
    		}
    		y++;
    	}    	    
    	fireTableStructureChanged();    	
    }
}
