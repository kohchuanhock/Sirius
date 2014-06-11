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


import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import java.util.*;
import java.io.*;

import javax.swing.*;

import sirius.trainer.main.SiriusSettings;

public class MotifsTableModel extends AbstractTableModel{
	static final long serialVersionUID = sirius.Sirius.version;

	private String[] columnNames = {"No."," ","Motifs","#MisMatch"};
	private ArrayList<MotifData> data;	
	private JFrame parent;
	
    public MotifsTableModel(JFrame parent) {
    	data = new ArrayList<MotifData>();
    	this.parent = parent;
    }            
    
    public String getColumnName(int col){
    	return columnNames[col];    
    }         
    
    public int getColumnCount(){
    	return columnNames.length;    
    }        
    
    public int getRowCount(){
    	return data.size();
    }    	
    	
    public void remove(int index){
    	data.remove(index);
    	fireTableRowsDeleted(index,index);
    }
    
    public void add(String motif, int mismatchAllowed){
    	add(motif,mismatchAllowed,true);    	
    }
    
    public void add(String motif, int mismatchAllowed,Boolean box){
    	data.add(new MotifData(motif.toUpperCase(),mismatchAllowed,box));
    	fireTableRowsInserted(getRowCount(),getRowCount());
    }
    
    public int getSize(){
    	int chosen = 0;
    	for(int x = 0; x < data.size(); x++)
    		if(data.get(x).getBox())
    			chosen++;    	
    	return chosen;
    }
    
    public boolean gotMotifMatch(String sequence){
    	for(int x = 0; x < data.size(); x++){
    		if(data.get(x).getBox()){
    			//Check if the sequence contains motif
    			String motif = data.get(x).getMotif();    			
    			//extract only what is needed for comparison
    			sequence = sequence.toUpperCase();
    			String subSequence = sequence.substring(sequence.length() - motif.length());    			
				//set all to uppercase for ease of comparison    				
    			//subSequence = subSequence.toUpperCase();
    			int mismatchAllowed = data.get(x).getMismatchAllowed();
    			int mismatch = 0;
    			for(int y = motif.length() - 1; y >= 0; y--){
    				char currentChar = motif.charAt(y);
    				if(currentChar == 'X'){
    					//do nothing as X is ignore character
    				}else{
    					if(currentChar != subSequence.charAt(y))
    						mismatch++;
    				}		    				
    			}
    			if(mismatch <= mismatchAllowed)
    				return true;//motif found
    		}    			
    	}
    	return false;//no motif found    		
    }
    
    public Object getValueAt(int row, int col) {
    	switch(col){
    		case 0: return ++row;
    		case 1: return data.get(row).getBox();
    		case 2: return data.get(row).getMotif();
    		case 3: return "" + data.get(row).getMismatchAllowed();
    	}
    	return "";    	
    }   
    //This method is needed for the checkbox thingy to show up inside tableModel
    public Class<?> getColumnClass(int c) {
    	return getValueAt(0, c).getClass();
    }
    //This method is needed for the checkbox thingy to be able to change state inside tableModel
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
    public void saveData(){
    	try{
    		JFileChooser fc;	
    		String lastMotifFileLocation = SiriusSettings.getInformation("LastMotifFileLocation: ");
	    	fc = new JFileChooser(lastMotifFileLocation);
	    	FileNameExtensionFilter filter = new FileNameExtensionFilter(
		            "Motif List", "motiflist");
		    fc.setFileFilter(filter);	
			int returnVal = fc.showSaveDialog(parent);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();	
				SiriusSettings.updateInformation("LastMotifFileLocation: ", file.getAbsolutePath());
				String savingFilename = file.getAbsolutePath();
				if(savingFilename.indexOf(".motiflist") == -1)
					savingFilename += ".motiflist";
			BufferedWriter output = new BufferedWriter(new FileWriter(savingFilename));    		
    		for(int x = 0; x < data.size(); x++){
    			output.write("Motif List:" + data.get(x).toString());
    			output.newLine();
    		}
    		output.flush();
    		output.close();
    		JOptionPane.showMessageDialog(parent,"Motif List Successfully Saved",
    			"Success",JOptionPane.INFORMATION_MESSAGE); 
			}
    	}catch(Exception e){
    		e.printStackTrace();
    		JOptionPane.showMessageDialog(parent,"An Error Occured",
    			"Error",JOptionPane.ERROR_MESSAGE); 
    	}
    }
    public void loadData(){
    		String lastMotifFileLocation = SiriusSettings.getInformation("LastMotifFileLocation: ");
    		JFileChooser fc = new JFileChooser(lastMotifFileLocation);
    		FileNameExtensionFilter filter = new FileNameExtensionFilter(
		            "Motif List", "motiflist");
		    fc.setFileFilter(filter);
			int returnVal = fc.showOpenDialog(parent);					
			if(returnVal == JFileChooser.APPROVE_OPTION){
				File file = fc.getSelectedFile();
				SiriusSettings.updateInformation("LastMotifFileLocation: ", file.getAbsolutePath());
	            try{			            		          
		            BufferedReader in = new BufferedReader(new FileReader(file.getAbsolutePath()));
		            String line;
		            data = new ArrayList<MotifData>();
		            while((line = in.readLine()) != null){
		            	if(line.indexOf("Motif List:")!=-1){
		            		String boxString = line.substring(line.indexOf("Box:")+("Box:").length(),
		            			line.indexOf("Motif:"));
		            		String motif = line.substring(line.indexOf("Motif:")+("Motif:").length(),
		            			line.indexOf("Mismatch:"));
		            		String mismatchString = line.substring(line.indexOf("Mismatch:")+
		            			("Mismatch:").length());
		            		Boolean boxBoolean = Boolean.parseBoolean(boxString);
		            		int mismatchInt = Integer.parseInt(mismatchString);
		            		add(motif,mismatchInt,boxBoolean);
		            	}else{
		            		JOptionPane.showMessageDialog(parent,"Invalid Format for MotifList",
    							"Error",JOptionPane.ERROR_MESSAGE); 
		            		break;
		            	}
		            }
		            in.close();
	            }catch(Exception e){
	            	e.printStackTrace();
	            	JOptionPane.showMessageDialog(parent,"An Error has Occured",
    					"Error",JOptionPane.ERROR_MESSAGE); 
	            }                   		
		    }		        
    }
}

class MotifData{
	private String motif;
	private int mismatchAllowed;
	private Boolean box;
		
	public MotifData(String motif,int mismatchAllowed,Boolean box){
		this.motif = motif;
		this.mismatchAllowed = mismatchAllowed;
		this.box = box;
	}
	public String getMotif(){
		return motif;
	}
	public int getMismatchAllowed(){
		return mismatchAllowed;
	}
	public Boolean getBox(){
		return box;
	}
	public void setBox(Boolean box){
		this.box = box;
	}
	public String toString(){
		return "Box:" + box + "Motif:" + motif + "Mismatch:" + mismatchAllowed;
	}
}