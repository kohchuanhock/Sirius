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
package sirius.trainer.step2;

import javax.swing.table.*;

import sirius.trainer.features.Feature;

import java.text.DecimalFormat;
import java.util.*;

public class FeatureTableModel extends AbstractTableModel{
	static final long serialVersionUID = sirius.Sirius.version;	
	
	private String[] columnNames;
    private List<Feature> data;      
    private DefineFeaturePane defineFeaturePane;
    private boolean showScore;
    private boolean isGA;//used to find out that this tableModel is used in GA
    
    private DecimalFormat df = new DecimalFormat("0.000");   
    
 	//Have another hashtable that mirrors the ArrayList<Step2FeatureData> data just to ensure that wont have repeated feature
    //This is such a waste of resource
    Hashtable<String, Feature> dataHashtable = null;
        	
	public FeatureTableModel(boolean showScore,DefineFeaturePane defineFeaturePane){					
		this.defineFeaturePane = defineFeaturePane;
		this.showScore = showScore;
		this.data = new ArrayList<Feature>();
		if(showScore == true){
			columnNames = new String[4];
			columnNames[0] = "No.";
			columnNames[1] = " ";
			columnNames[2] = "Name of Feature";
			//columnNames[3] = "Details";
			columnNames[3] = "Chisq-Score";
		}else{
			columnNames = new String[4];
			columnNames[0] = "No.";
			columnNames[1] = " ";
			columnNames[2] = "Name of Feature";
			columnNames[3] = "Details";		
		}
	}
	
	public void setScoreString(String scoreString){
		if(this.columnNames.length >= 3)
			this.columnNames[2] = scoreString;
	}
	//This is for viewFeatures in GA
	public FeatureTableModel(FeatureTableModel model,boolean showScore,boolean isGA){
		this.showScore = showScore;
		this.data = new ArrayList<Feature>();		
		if(model != null && model.getData() != null)
			for(int x = 0; x < model.getData().size(); x++)
				this.data.add(model.getData().get(x));
		this.isGA = isGA;
		if(isGA){
			columnNames = new String[3];
			columnNames[0] = "No.";			
			columnNames[1] = "Name of Feature";			
			columnNames[2] = "Chisq-Score";
		}		
	}					
	//deep copy - do not need the showScore variable here because this is only used for the counting number of features
	//no~! need the showScore variable for other purpose like the exception caused
	public FeatureTableModel(FeatureTableModel model,boolean showScore){
		this.showScore = showScore;
		this.data = new ArrayList<Feature>();		
		if(model != null && model.getData() != null)
			for(int x = 0; x < model.getData().size(); x++)
				this.data.add(model.getData().get(x));	
		if(showScore == true){
			columnNames = new String[4];
			columnNames[0] = "No.";
			columnNames[1] = " ";
			columnNames[2] = "Name of Feature";
			//columnNames[3] = "Details";
			columnNames[3] = "Score";
		}else{
			columnNames = new String[4];
			columnNames[0] = "No.";
			columnNames[1] = " ";
			columnNames[2] = "Name of Feature";
			columnNames[3] = "Details";		
		}
	}					

	public FeatureTableModel(boolean showScore){		
		//Actually, I think even if use the other constructor (Step2FeatureTableModel(DefineFeaturePane defineFeaturePane,boolean showScore))
		//is also fine
		this((FeatureTableModel)null,showScore);
	}
	
	public void setData(List<Feature> data){
		this.data = data;
		this.fireTableDataChanged();
	}
	
	public void setEmpty(){
		for(int x = 0; x < getRowCount();x++){
			data.remove(x); 
			fireTableRowsDeleted(x,x);
    	}    	
		data = null;
	}
	
	public Object getValueAt(int row, int col) {		
    	if(data!=null){
    		if(isGA){    		
    			switch(col){
    			case 0: return "" + (row + 1);
    			case 1: return ((Feature)data.get(row)).getName();
    			case 2: return this.df.format(((Feature)data.get(row)).getScore());
    			//should not reach default
    			default: return "-1";
    			}
    		}
    		else if(showScore){
    			switch(col){
    			case 0: return "" + (row + 1);
    			case 1: case 2: return data.get(row).get(col);
    			case 3: return this.df.format(((Feature)data.get(row)).getScore());
    			//should not reach default
    			default: return "-1";
    			}    			
    		}else{
    			if(col == 0)
	    			return "" + (row+1); 
	    		else
	    			return data.get(row).get(col);
    		}
    	}    		
    	else
    		return " ";
    }
    
    public Class<?> getColumnClass(int c) {
    	return getValueAt(0, c).getClass();
    }
    
    public int getColumnCount() {
        return columnNames.length;
    }
    
    public int getRowCount() {    
    	if(data!=null)
        	return data.size();       
        else 
        	return -1;
    }    
    public String getColumnName(int col) {
        return columnNames[col];
    } 
    public List<Feature> getData(){
    	return data;
    }
    public void add(Feature data){
    	if(this.data == null)
			this.data = new ArrayList<Feature>();
    	this.data.add(data);
    	fireTableRowsInserted(getRowCount(),getRowCount());
    	if(this.showScore == false && this.defineFeaturePane != null)
    		defineFeaturePane.updateNumberOfFeaturesLabel(); 
    }  
    public boolean isCellEditable(int row,int column){
    	if(column == 1)
    		return true;
    	else 
    		return false;
    }
    public void setValueAt(Object value, int row, int col) {
        data.get(row).setBox((Boolean)value);
        fireTableCellUpdated(row, col);
    }
    public void markAll(){
    	if(data == null || data.size() == 0)
    		return;
    	for(int x = 0; x < data.size(); x++){
    		data.get(x).setBox(true);
    	}
    	fireTableRowsUpdated(0,data.size());
    }
    public void markTop(int markTopX){
    	if(data == null || data.size() == 0)
    		return;
    	//clear previous selections
    	unmarkAll();
    	for(int x = 0; x < markTopX && x < data.size(); x++){
    		data.get(x).setBox(true);
    	}
    	fireTableRowsUpdated(0,markTopX);
    }
    public void markWindowBoundary(int windowFrom, int windowTo){
    	//Mark all those that are within a window boundary
    	if(data == null || data.size() == 0)
    		return;
    	//clear previous selections
    	unmarkAll();
    	for(int x = 0; x < data.size(); x++){
    		if(data.get(x).getWindowFrom() >= windowFrom && data.get(x).getWindowTo() <= windowTo)
    			data.get(x).setBox(true);
    	}
    	fireTableRowsUpdated(0,data.size());
    }
    public void unmarkAll(){
    	if(data == null || data.size() == 0)
    		return;
    	for(int x = 0; x < data.size(); x++){
    		data.get(x).setBox(false);
    		fireTableCellUpdated(x, 1);
    	}
    }
    public void invertBox(){
    	if(data == null || data.size() == 0)
    		return;
    	for(int x = 0; x < data.size(); x++){
    		data.get(x).invertBox();
    		fireTableCellUpdated(x, 1);
    	}
    }
    
    public void getMarked(FeatureTableModel model){    	
    	for(int x = 0; x < model.getRowCount();){
    		Feature feature = model.getData().get(x);
    		if(feature.isMarked()){
    			model.getData().remove(x);
    			this.data.add(feature);
    		}else
    			x++;
    	}
    	fireTableRowsInserted(0,this.data.size());    	
    	model.update();
    }      
    
    public void update(){    	
    	fireTableRowsDeleted(0,this.data.size());
    }       
    public void removeMarked(){
    	for(int x = 0; x < data.size();){
    		if(data.get(x).isMarked()){
    			data.remove(x); 
    			fireTableRowsDeleted(x,x);
    		}    			   				
    		else
    			x++;    		
    	}    	
    }
    public Feature getFeatureDataAt(int index){
    	return data.get(index);
    }
    
    public String getName(int index){
    	return (String)data.get(index).get(2);
    }
    
    public char getType(int index){
    	return (char)data.get(index).getType();
    }
    
    public String toString(int index){
    	return "No." + (index+1) + "\tName: " + data.get(index).get(2) + "\tDetails: " + data.get(index).get(3);
    }        
    
    public String saveString(int index){
    	return this.saveString(index,null);
    }
    
    public String saveString(int index, String saveDirectory){
    	return data.get(index).saveString(saveDirectory);
    }              
}