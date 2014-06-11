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


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import sirius.main.ApplicationData;
import sirius.utils.FastaFormat;

public class SetIndexTableModel extends AbstractTableModel{
	static final long serialVersionUID = sirius.Sirius.version;
	private String[] columnNames = {"No","Header", "Length", "+1 Index"};
	private ArrayList<FastaFormat> data;
	private JDialog parent;
	
    public SetIndexTableModel(JDialog parent,BufferedReader in) {
    	this.parent = parent;
    	data = new ArrayList<FastaFormat>();    	    	
    	String line;
    	String header = "";
    	String eachSequence = "";            	
    	try{
    		while((line = in.readLine()) != null) {		            	
            	if(line.indexOf(">")==0){
            		if(header != "")
            			data.add(new FastaFormat(header,eachSequence));
            		header = line;
            		eachSequence = "";
            	}
            	else{
            		eachSequence += line.trim();
            	}
            }
            data.add(new FastaFormat(header,eachSequence));
            in.close();      	
    	}catch(Exception e){
    		JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
    		e.printStackTrace();}
    }       
       
    public boolean checkLocationIndexConsistency(ApplicationData applicationData){
    	int locationIndex = 0;
    	//Check that the +1_Index within the file (all sequences in the file) are the same, 
    	//either all -1 or all non -1
    	for(int x = 0; x < data.size(); x++){
			String header = data.get(x).getHeader();
			if(locationIndex == 0){//if locationIndex has not been set
				if(header.indexOf("+1_Index(") == -1){//if header does not have +1_Index yet, set it to -1
					header += " +1_Index(-1)";
					locationIndex = -1;
				}
				else{
					locationIndex = data.get(x).getIndexLocation();
				}			
			}else{
				int headerLocationIndex;
				if(header.indexOf("+1_Index(") == -1){				
					headerLocationIndex = -1;
				}else{
					headerLocationIndex = data.get(x).getIndexLocation();				
				}
				if((headerLocationIndex != -1 && locationIndex == -1) || 
					(headerLocationIndex == -1 && locationIndex != -1)){
						JOptionPane.showMessageDialog(parent,
							"Sequences +1_Index should either be all -1 or all non -1","Error",
							JOptionPane.ERROR_MESSAGE);
						return false;					
					}					
			}									
		}
		/*//check if the locationIndex of this file tally with the rest of the files
		if(applicationData.hasLocationIndexBeenSet){
			if(applicationData.isLocationIndexMinusOne == true && locationIndex != -1){
				JOptionPane.showMessageDialog(parent,
						"+1_Index of this files are non -1 but +1_Index of other files are -1","Error",
						JOptionPane.ERROR_MESSAGE);
				return false;
			} 
			if(applicationData.isLocationIndexMinusOne == false && locationIndex == -1){
				JOptionPane.showMessageDialog(parent,
						"+1_Index of this files are -1 but +1_Index of other files are non -1","Error",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}				
		}*/
		return true; 
    }
    
    //Updating the file
    public void writeData(String absolutePath,Step1TableModel model,boolean byAddButton,int index){
    	try{    		   		
    		BufferedWriter out = new BufferedWriter(new FileWriter(absolutePath));
    		for(int x = 0; x < data.size(); x++){
    			String header = data.get(x).getHeader();
    			if(header.indexOf("+1_Index(") == -1){
					header += " +1_Index(-1)";				
				}			
    			out.write(header);
    			out.newLine();
    			out.write(data.get(x).getSequence());
    			out.newLine();
    			out.flush();    			
    		}    		
    		out.close();
    		if(byAddButton)
    			//use any index is fine for getIndexLocationInt since they are checked to be the same
    			model.add(absolutePath,data.size(),data.get(0).getIndexLocation()); 
    		else
    			model.setPlusOneIndex(index,getPlusOneIndex());   		
    	}catch(Exception e){
    		e.printStackTrace();
    		JOptionPane.showMessageDialog(parent,"Unable to write to " + absolutePath,"ERROR",
    				JOptionPane.ERROR_MESSAGE);    	
    	}    	
    }
    	
    public int getColumnCount(){
        return columnNames.length;
    }
    
    public String getColumnName(int col) {
        return columnNames[col];
    }
    
    public int getRowCount() {    
    	if(data!=null)
        	return data.size();       
        else 
        	return -1;
    }       
    	
    public Object getValueAt(int row, int col) {    
    	if(data!=null){
    		if(col == 0)
    			return row+1;
    		else
    			return data.get(row).get(col);    
    	}    	
    	else
    		return " ";
    }
    public void setValueAt(Object value, int row, int col) {
    	try{
    		int indexLocation = Integer.parseInt((String)value);
    		if(indexLocation + 1 > (Integer)data.get(row).get(2))
    			JOptionPane.showMessageDialog(parent,"+1 Index is out of range (Greater than Sequence Length)",
    				"ERROR",JOptionPane.ERROR_MESSAGE);
    		else if(indexLocation >= -1)    			
 		   		data.get(row).setIndexLocation(indexLocation);
 		   	else
 		   		JOptionPane.showMessageDialog(parent,"+1 Index needs to be > -1","ERROR",
        			JOptionPane.ERROR_MESSAGE);
    	}
        catch(Exception e){
        	JOptionPane.showMessageDialog(parent,"Please enter only numbers into +1 Index","ERROR",
        		JOptionPane.ERROR_MESSAGE);
        }
        fireTableCellUpdated(row, col);
    }
    public boolean isCellEditable(int row,int column){
    	if(column == 3)
    		return true;
    	else 
    		return false;
    }    
    
    //Ensure that all +1_Index is not out of range for any of the sequences, return -1
    //else return index of the sequence that is out of range
    public int validateLocationIndex(int locationIndex){
    	for(int x = 0; x < data.size(); x++){
    		if(locationIndex + 1 > (Integer)data.get(x).get(2)){
    			return x;
    		}
    	}
    	return -1;
    }
    
    public void setLocationIndexForAll(int locationIndex){
    	for(int x = 0; x < data.size(); x++){
    		data.get(x).setIndexLocation(locationIndex);
    	}
    	fireTableRowsUpdated(0, data.size());
    }
    
    //get plusOneIndex of the first entry in the file
    //does not matter which entry i take because all should either be -1 or non -1
    public int getPlusOneIndex(){
    	return data.get(0).getIndexLocation();
    }
}