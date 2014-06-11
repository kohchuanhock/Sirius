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

import javax.swing.table.*;
import java.io.*;
import java.util.*;
import javax.swing.*;


public class RedundancyReductionModel extends AbstractTableModel{
	static final long serialVersionUID = sirius.Sirius.version;

	private String[] columnNames = {"No.","Sequence Name", "Sequence","Length", "# of Similar Seq", "Range(%)"};
	ArrayList<RedundancyReductionData> posData;
	ArrayList<RedundancyReductionData> negData;
	boolean posDataChecked;
	boolean negDataChecked;
    private boolean runningTest;
    private boolean completeRun;
	
    public RedundancyReductionModel() {
    	this.runningTest = false;
    	this.posDataChecked = false;
    	this.negDataChecked = false;
    	this.completeRun = false;
    	this.posData = new ArrayList<RedundancyReductionData>();
    	this.negData = new ArrayList<RedundancyReductionData>();
    }
    public boolean getCompleteRun(){
    	return this.completeRun;
    }
    public void write(BufferedWriter output,boolean isPos) throws IOException{
    	ArrayList<RedundancyReductionData> data;
    	if(isPos)
    		data = posData;
    	else 
    		data = negData;
    	for(int x = 0; x < data.size(); x++){
    		output.write(data.get(x).getHeader());
    		output.newLine();
    		output.write(data.get(x).getSequence());
    		output.newLine();
    		output.flush();
    	}
    }
    
    private void sort(){
    	int totalSize = 0;
    	if(posData != null){
    		Collections.sort(posData, new SortByNumOfSimilarSequence());
    		totalSize += posData.size();
    	}
    	if(negData != null){
    		Collections.sort(negData, new SortByNumOfSimilarSequence());
    		totalSize += negData.size();
    	}
    	fireTableRowsUpdated(0,totalSize);
    }
    
    /*public void reset(){
    	posData = null;
    	negData = null;
    	posData = new ArrayList<SequenceSimilarityData>();
    	negData = new ArrayList<SequenceSimilarityData>();
    }*/
    
    public Class<?> getColumnClass(int c) {
    	return getValueAt(0, c).getClass();
    }
    
    public String getColumnName(int col) {
        return columnNames[col];
    }
    
    public int getColumnCount(){
        return columnNames.length;	//	No.,Classifier Name, # of Features
    }
    
    public int getRowCount() {    
    	int totalRowCount = 0;
    	if(posData!=null)
        	totalRowCount += posData.size();       
        if(negData!=null)
        	totalRowCount += negData.size();
        if(posData == null && negData == null)
        	return -1;
        else
        	return totalRowCount;
    }       
    public int getPosDataSize(){
    	return posData.size();
    }
    public int getNegDataSize(){
    	return negData.size();
    }
    public int getTotalDataSize(){
    	return posData.size() + negData.size();
    }
    
    public Object getValueAt(int row, int col) {    
    	if(posData!=null && row < posData.size()){
    		switch(col){
    		case 0: return "" + (row + 1) + " (+)";
    		case 1: return posData.get(row).getHeader();
    		case 2: return posData.get(row).getSequence();
    		case 3: return posData.get(row).getSequence().length();
    		case 4: if(posData.get(row).getNumOfSimilarSequence() == -1)
    					return "";
    				else
    					return posData.get(row).getNumOfSimilarSequence();
    		case 5: return posData.get(row).getRangePercent();    		
    		default: return " ";
    		}
    	}		
    	else if(negData!=null){
    		int negRow;
    		if(posData != null)
    			negRow = row - posData.size();
    		else
    			negRow = row; 
    		switch(col){
    		case 0: return "" + (row + 1) + " (-)";
    		case 1: return negData.get(negRow).getHeader();
    		case 2: return negData.get(negRow).getSequence();
    		case 3: return negData.get(negRow).getSequence().length();
    		case 4: if(negData.get(negRow).getNumOfSimilarSequence() == -1)
    					return "";
    				else
    					return negData.get(negRow).getNumOfSimilarSequence();
    		case 5: return negData.get(negRow).getRangePercent();    		
    		default: return " ";
    		}
    	}else
    		return " ";
    }        

    //Add this method only when the cell is editable by user
    /*public boolean isCellEditable(int row,int column){
    	if(column == 6){
    		data.remove(row);
    		try{
    			Thread.sleep(100);
    		}catch(Exception e){e.printStackTrace();}
    		fireTableRowsDeleted(row,row);
    		return true;
    	}else
    		return false;
    }*/
    public void addPosData(RedundancyReductionData data){    	
    	this.posData.add(data);
    	fireTableRowsInserted(getRowCount(),getRowCount());
    }
    
    public void addNegData(RedundancyReductionData data){    	
    	this.negData.add(data);
    	fireTableRowsInserted(getRowCount(),getRowCount());
    }    
    public void loadFastaFile(File file, boolean posData) throws Exception{
    	this.completeRun = false;
    	if(posData == true){
    		this.posData = new ArrayList<RedundancyReductionData>();
    		this.posDataChecked = false;
    	}
    	else{
    		this.negData = new ArrayList<RedundancyReductionData>();
    		this.negDataChecked = false;
    	}
    	BufferedReader in = new BufferedReader(new FileReader(file.getAbsolutePath()));
        
        int countSequenceNumber = 0;
        String line;
        //there could be a bug within these few lines as having ">" at index 0 
        //is not limited to fasta only..
        //pdf files also have this feature..
        //sequenceNameTableModel.reset();
        //reset();
        String eachSequence = "";
        String sequenceName = "";	   
        while ((line = in.readLine()) != null) {		            	
        	if(line.indexOf(">")==0){
        		countSequenceNumber++;	            		
        		if(eachSequence.length()!=0){        			
        			if(posData)
        				addPosData(new RedundancyReductionData(sequenceName,eachSequence));
        			else
        				addNegData(new RedundancyReductionData(sequenceName,eachSequence));
        		}
        		sequenceName = line;	
        		eachSequence = "";
        	}
        	else{
        		eachSequence += line;
        		//if = exists in sequence, error
        		/*if(eachSequence.indexOf("=")!=-1){
        			throw new Exception("Please ensure that " + file.getAbsolutePath() + " is in FASTA format.");	            	
            	}*/	            			
        	}
        }
        in.close();
        if(countSequenceNumber == 0){
        	throw new Exception("Please ensure that " + file.getAbsolutePath() + " is in FASTA format.");        	
        }else{
        	//sequenceNameTableModel.add(new SequenceNameData(sequenceName,eachSequence,""));
        	if(posData)
        		addPosData(new RedundancyReductionData(sequenceName,eachSequence));
        	else
        		addNegData(new RedundancyReductionData(sequenceName,eachSequence));
        }	      
    }
    public void setRunningTest(boolean runningTest){
    	this.runningTest = runningTest;
    }
    
    public void reduceRedunancy(JScrollPane scrollPane, JTable modelTable, JTextField statusTextField){
    	//Always delete -ve first, starting with the one with most similar sequences. (Because background sequences are usually more around)
    	//Delete until all left one. Then start with +ve    	
    	
    	//Ensure that data has at least done some checking
    	if(this.posDataChecked == false || this.negDataChecked == false){
    		JOptionPane.showMessageDialog(null,"After loadin new data, please run similarity check before redunancy reduction",
    				"Error",JOptionPane.ERROR_MESSAGE);
    		return;
    	}    	    	
    	int runCount = 0;
    	int totalCount = 0;
    	if(negData != null)
    		totalCount += negData.size();
    	if(posData != null)
    		totalCount += posData.size();
    	if(negData != null){
    		for(int x = 0; x < negData.size();){
    			runCount++;
    			statusTextField.setText("Reducing.. " + runCount + " / " + totalCount);
    			RedundancyReductionData data = negData.get(x);
    			if(data.getNumOfSimilarSequence() >= 1){
    				//delete the sequence but before that, go through all the similar sequence to delete it from their link
    				for(int y = 0; y < data.getSimilarSequenceArray().size(); y++){
    					RedundancyReductionData similarData = data.getSimilarSequenceArray().get(y).getSimilarSequence();
    					similarData.deleteSimilarSequenceData(data);
    				}   
    				negData.remove(x);
    			}else
    				x++;
    		}
    	}
    	if(posData != null){
    		for(int x = 0; x < posData.size();){
    			runCount++;
    			statusTextField.setText("Reducing.. " + runCount + " / " + totalCount);
    			RedundancyReductionData data = posData.get(x);
    			if(data.getNumOfSimilarSequence() >= 1){
    				//delete the sequence but before that, go through all the similar sequence to delete it from their link
    				for(int y = 0; y < data.getSimilarSequenceArray().size(); y++){
    					RedundancyReductionData similarData = data.getSimilarSequenceArray().get(y).getSimilarSequence();
    					similarData.deleteSimilarSequenceData(data);
    				}
    				posData.remove(x);
    			}else
    				x++;
    		}
    	}
    	int sizeCount = 0;
    	if(posData != null)
    		sizeCount += posData.size();
    	if(negData != null)
    		sizeCount += negData.size();
    	sizeCount--;
    	fireTableRowsDeleted(0, sizeCount);
    	if(sizeCount >= 0){
    		modelTable.setRowSelectionInterval(sizeCount,sizeCount);     	
    		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
    	}
    	statusTextField.setText("Done");
    }
    
    public void runSimilarityTest(JTable sequenceNameTable, JScrollPane sequenceNameTableScrollPane, JTextField statusTextField,
    		boolean reduceWhileRun,int identityPercent){    	
    	if(reduceWhileRun == false){
    		int posDataSize = 0;    	
	    	int negDataSize = 0;
	    	this.posDataChecked = true;
	    	this.negDataChecked = true;
	    	ArrayList<RedundancyReductionData> originalData = null;
	    	if(posData != null)
	    		posDataSize = posData.size();
	    	if(negData != null)
	    		negDataSize = negData.size();
	    	if(this.posDataChecked == true){
	    		//reset all the similarSequence previously found
	    		for(int x = 0; x < (posDataSize); x++)
	    			posData.get(x).reset();
	    	}
	    	if(this.negDataChecked == true){
	    		//reset all the similarSequence previously found
	    		for(int x = 0; x < (negDataSize); x++)
	    			negData.get(x).reset();
	    	}
	    	for(int x = 0; x < (posDataSize + negDataSize) && this.runningTest == true; x++){    		
	    		String originalSequence;
	    		//boolean originalIsPos;
	    		int originalIndex;    		
	    		if(posDataSize > x){
	    			//still running the records of PosData
	    			originalIndex = x;
	    			originalData = posData;
	    			//originalIsPos = true;
	    		}else{
	    			//now running the records of negData
	    			originalIndex = x - posDataSize;
	    			originalData = negData;
	    			//originalIsPos = false;
	    		}    		
	    		originalSequence = originalData.get(originalIndex).getSequence();
	    		for(int y = x + 1; y < (posDataSize + negDataSize) && this.runningTest == true; y++){
	    			String compareToSequence;
	    			//boolean compareToIsPos;
	    			int compareToindex;    			
	    			ArrayList<RedundancyReductionData> compareToData;
	    			if(posDataSize > y){
	        			//still running the records of PosData
	    				compareToindex = y;
	        			compareToData = posData; 
	        			//compareToIsPos = true;
	        		}else{
	        			//now running the records of negData
	        			compareToindex = y - posDataSize;
	        			compareToData = negData;
	        			//compareToIsPos = false;
	        		}   
	    			compareToSequence = compareToData.get(compareToindex).getSequence();
	    			short similarityResult = (short)similarityTest(originalSequence,compareToSequence,identityPercent);	    			
	    			originalData.get(originalIndex).ranTest();    			
	    			short similarityResultPercent = (short)((similarityResult * 100) / originalSequence.length());	    			
	    			if(similarityResultPercent >= identityPercent){    				
	    				originalData.get(originalIndex).addSimilarSequence(compareToData.get(compareToindex), similarityResultPercent);
	    				compareToData.get(compareToindex).addSimilarSequence(originalData.get(originalIndex), similarityResultPercent);
	    			}
	    			fireTableRowsUpdated(originalIndex,originalIndex);
	    			//sequenceNameTable.setRowSelectionInterval(originalIndex,originalIndex);
	    			statusTextField.setText("" + (x + 1) + " / " + (posDataSize + negDataSize));
	    		}//end of inner for loop    				
	    		if((x+1) == (posDataSize + negDataSize))
	    			this.completeRun = true;
	    	}//end of outer for loop
	    	sequenceNameTable.setRowSelectionInterval(0,0);
	    	if(originalData != null)
	    		originalData.get(originalData.size() - 1).ranTest();
	    	if(this.completeRun){
	    		statusTextField.setText("Sorting");
	    		sort();    	
	    		statusTextField.setText("Done");
	    	}else{
	    		statusTextField.setText("Interrupted");
	    	}
    	}else{//reduceWhileRun is set to true
	    	this.posDataChecked = true;
	    	this.negDataChecked = true;
	    	ArrayList<RedundancyReductionData> originalData = null;	    	
	    	if(this.posDataChecked == true){
	    		//reset all the similarSequence previously found
	    		for(int x = 0; x < posData.size(); x++)
	    			posData.get(x).reset();
	    	}
	    	if(this.negDataChecked == true){
	    		//reset all the similarSequence previously found
	    		for(int x = 0; x < negData.size(); x++)
	    			negData.get(x).reset();
	    	}
	    	for(int x = 0; x < (posData.size() + negData.size()) && this.runningTest == true; x++){    		
	    		String originalSequence;
	    		//boolean originalIsPos;
	    		int originalIndex;    		
	    		if(posData.size() > x){
	    			//still running the records of PosData
	    			originalIndex = x;
	    			originalData = posData;
	    			//originalIsPos = true;
	    		}else{
	    			//now running the records of negData
	    			originalIndex = x - posData.size();
	    			originalData = negData;
	    			//originalIsPos = false;
	    		}    		
	    		originalSequence = originalData.get(originalIndex).getSequence();
	    		for(int y = x + 1; y < (posData.size() + negData.size()) && this.runningTest == true;){
	    			String compareToSequence;
	    			//boolean compareToIsPos;
	    			int compareToindex;    			
	    			ArrayList<RedundancyReductionData> compareToData;
	    			if(posData.size() > y){
	        			//still running the records of PosData
	    				compareToindex = y;
	        			compareToData = posData; 
	        			//compareToIsPos = true;
	        		}else{
	        			//now running the records of negData
	        			compareToindex = y - posData.size();
	        			compareToData = negData;
	        			//compareToIsPos = false;
	        		}   
	    			compareToSequence = compareToData.get(compareToindex).getSequence();
	    			short similarityResult = (short)similarityTest(originalSequence,compareToSequence,identityPercent);
	    			originalData.get(originalIndex).ranTest();    			
	    			short similarityResultPercent = (short)((similarityResult * 100) / originalSequence.length());
	    			if(similarityResultPercent >= identityPercent){    					    	
	    				compareToData.remove(compareToindex);
	    			}else{
	    				y++;
	    			}	    			
	    			//sequenceNameTable.setRowSelectionInterval(originalIndex,originalIndex);
	    			statusTextField.setText("" + (x + 1) + " / " + (posData.size() + negData.size()));
	    		}//end of inner for loop    				
	    		if((x+1) == (posData.size() + negData.size()))
	    			this.completeRun = true;
	    	}//end of outer for loop
	    	sequenceNameTable.setRowSelectionInterval(0,0);
	    	if(originalData != null)
	    		originalData.get(originalData.size() - 1).ranTest();
	    	if(this.completeRun){
	    		statusTextField.setText("Sorting");
	    		sort();    	
	    		statusTextField.setText("Done");	    		
	    	}else{
	    		statusTextField.setText("Interrupted");
	    	}
	    	fireTableRowsDeleted(0,posData.size() + negData.size());
    	}
    }       
    
    //This uses the time saving technique taught by Limsoon
    private short similarityTest(String A, String B,int identityPercent){
    	//Parameters for the smith-waterman alignment
    	//int indel = 0;
    	//int mismatch = 0;
    	    	
    	
    	short match = 1;
    	double d = ((100.0 - identityPercent)/100.0);
    	
		short[][] result = new short[A.length()+1][B.length()+1];		
		short A_len = (short)A.length();
		short B_len = (short)B.length();
		for(short x=1,y=1;x<A_len+1&&y<B_len+1;x++,y++){
			for(short a=x; a<(d*(A_len+1))+y+1 && a<A_len+1; a++){
				result[a][y] = result[a-1][y]; 

				if(result[a][y] < result[a][y-1])
					result[a][y] = result[a][y-1];

				if(A.charAt(a-1) == B.charAt(y-1))
					if(result[a][y] < result[a-1][y-1] + match)
						result[a][y] = (short)(result[a-1][y-1] + match);

				else if(result[a][y] < result[a-1][y-1])
					result[a][y] = result[a-1][y-1];
			}
			for(short a=y; a<(d*(B_len+1))+x+1 && a<B_len+1; a++){
				result[x][a] = result[x-1][a]; 

				if(result[x][a] < result[x][a-1])
					result[x][a] = result[x][a-1];

				if(A.charAt(x-1) == B.charAt(a-1))
					if(result[x][a] < result[x-1][a-1] + match)
						result[x][a] = (short)(result[x-1][a-1] + match);

				else if(result[x][a] < result[x-1][a-1])
					result[x][a] = result[x-1][a-1];		
			}
		}								
		return result[A.length()][B.length()];
	}
}
class SortByNumOfSimilarSequence implements Comparator<RedundancyReductionData>{
	  public int compare(RedundancyReductionData o1, RedundancyReductionData o2){	    
		  if(o2.getNumOfSimilarSequence() > o1.getNumOfSimilarSequence())
			  return 1;
		  else if(o2.getNumOfSimilarSequence() < o1.getNumOfSimilarSequence())
			  return -1;
		  else if(o2.getMinSimilarPercent() > o1.getMinSimilarPercent())
			  return 1;
		  else if(o2.getMinSimilarPercent() < o1.getMinSimilarPercent())
			  return -1;
		  else if(o2.getMaxSimilarPercent() > o1.getMaxSimilarPercent())
			  return 1;
		  else if(o2.getMaxSimilarPercent() < o1.getMaxSimilarPercent())
			  return -1;
		  else
			  return 0;	    
	  }
	}
