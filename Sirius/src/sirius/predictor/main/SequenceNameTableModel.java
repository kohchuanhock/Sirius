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

import javax.swing.JLabel;
import javax.swing.table.*;

import sirius.blast.QBlast;
import sirius.main.ApplicationData;
import sirius.trainer.main.StatusPane;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class SequenceNameTableModel extends AbstractTableModel{
	static final long serialVersionUID = sirius.Sirius.version;

	private String[] columnNames; 
	ArrayList<SequenceNameData> data;
	ArrayList<SequenceNameData> undoList;//this it to allow deletion to be undone
	ArrayList<SequenceNameData> filteredData;//store the filtered away records so that can put them back when constraints are deleted
	ArrayList<sirius.nnsearcher.main.Constraints> constraintsData;
    
    public SequenceNameTableModel(){
    	this(true);    	
    }
    
    public void checkAll(){
    	int row = 0;
    	synchronized (this){
	    	for(SequenceNameData d:this.data){
	    		d.setBox(true);
	    		fireTableCellUpdated(row, 0);
	    		row++;
	    	}
    	}
    }
    
    public void uncheckAll(){
    	int row = 0;
    	synchronized (this){
	    	for(SequenceNameData d:this.data){
	    		d.setBox(false);
	    		fireTableCellUpdated(row, 0);
	    		row++;
	    	}
    	}
    }
    
    public void checkTop(int top){
    	synchronized (this){
	    	for(int x = 0; x < top; x++){
	    		this.data.get(x).setBox(true);
	    		fireTableCellUpdated(x, 0);
	    	}
    	}
    }
    
    public void setValueAt(Object value, int row, int col) {
        data.get(row).setBox((Boolean)value);
        fireTableCellUpdated(row, col);
    }
    
    public void blast(StatusPane statusPane, Boolean filterLowComplexity, String outputDirectory){		
		/*
		 * Blast sequences and retrieve the results
		 */					
    	statusPane.setText("Blasting..");
		int numOfReturnAlignment = 500;
		double eValue = 10;
		
		int errors = 0;
		int totalSequenceToBlast = 0;
		int sent = 0;
		for(SequenceNameData s:this.data){
			if(s.checked){
				totalSequenceToBlast++;
			}
		}
		int totalSequenceToRetrieve = 0;
		
		for(SequenceNameData s:this.data){
			if(s.checked){
				//Blast the sequence
				if(s.getRID() != null){
					//if previous blast results exists, request to remove it from blast server
					QBlast.removeResultFromBlastServer(s.getRID());
				}
				String RID = QBlast.blastSequence(s.getSequence(), numOfReturnAlignment, eValue, filterLowComplexity);
				if(RID != null){
					totalSequenceToRetrieve++;
					s.setRID(RID);
				}else{
					errors++;
					s.setRID(null);
				}
				sent++;
				if(errors > 0){
					statusPane.setText("Please wait.. Sending.. " + sent + "/" + totalSequenceToBlast + " Errors: " + errors + 
							" Please see log.txt in Output Directory.");
				}else{
					statusPane.setText("Please wait.. Sending.. " + sent + "/" + totalSequenceToBlast);
				}
			}
		}
		int notReady = 1;
		int resultsObtained = 0;
		for(int x = 0; x < 18 && notReady > 0; x++){
			notReady = 0;
			for(SequenceNameData s:this.data){
				if(s.checked && s.getRID() != null && s.ready == false){
					if(QBlast.isBlastResultReady(s.getRID())){
						s.ready = true;
						resultsObtained++;
						QBlast.saveBlastOutputToFile(outputDirectory, s.getRID(), s.getHeader());
					}else{
						notReady++;
					}
				}
			}
			if(errors > 0){
				statusPane.setText("Please wait.. Retrieving.. " + resultsObtained + "/" + totalSequenceToRetrieve + " Errors: " + errors + 
						" Please see log.txt in Output Directory.");
			}else{
				statusPane.setText("Please wait.. Retrieving.. " + resultsObtained + "/" + totalSequenceToRetrieve);
			}
			if(notReady > 0){
				try{
					Thread.sleep(10000);
				}catch(Exception e){}
			}
		}
		
		if(errors > 0){
			statusPane.setText("Done! Sent: " + sent + " Retrieved: " + resultsObtained + " Errors: " + errors + 
					" Please see log.txt in Output Directory");
		}else{
			statusPane.setText("Done! Sent: " + sent + " Retrieved: " + resultsObtained);
		}
	}
    
    public void setConstraintsData(ArrayList<sirius.nnsearcher.main.Constraints> constraintsData){
    	this.constraintsData = constraintsData;
    }
    
    //This constructor is used for RandomizeSequencePane.java
    public SequenceNameTableModel(SequenceNameTableModel copy){
    	data = new ArrayList<SequenceNameData>(copy.getData());
    	undoList = new ArrayList<SequenceNameData>();  
    	this.filteredData = new ArrayList<SequenceNameData>();
    	columnNames = new String[2];
    	columnNames[0] = "No.";
    	columnNames[1] = "Sequence Name";  
    }
    
    public void deleteConstraints(final ApplicationData applicationData, final JLabel statusLabel){
		Thread runThread = (new Thread(){	      	
			public void run(){	
				//scan through those filteredData and add back those that passed the remaining constraints back to data	
				DecimalFormat df = new DecimalFormat("0.##");
				for(int x = 0;filteredData != null && x < filteredData.size();){
					statusLabel.setText("Filtering: " + df.format((x * 100.0) / filteredData.size()) + "%");
					boolean violated = false;
					SequenceNameData tempData = filteredData.get(x);
					for(int y = 0; y < constraintsData.size(); y++){
						//check if constraint have been violated
						if(constraintsData.get(y).isViolated(tempData.getFastaFormat(),null, applicationData)){				
							violated = true;
							break;
						}				
					}			
					if(violated == false){
						//move back to data
						data.add(tempData);
						filteredData.remove(x);
						sort();
						fireTableRowsInserted(0,getRowCount());
					}else{
						x++;
					}
				}
				update();				
				statusLabel.setText("Done");
			}});
      		runThread.setPriority(Thread.MIN_PRIORITY); // UI has most priority
      		runThread.start();	 		
	}
    
    public void update(){ fireTableDataChanged(); }

    class SortByScore implements Comparator<SequenceNameData>{
    	public int compare(SequenceNameData o1, SequenceNameData o2){
    		if(o2.getScore() > o1.getScore())
    			return 1;
    		else if(o2.getScore() < o1.getScore())
    			return -1;
    		else
    			return 0;    
    		}
    }
    
    public void loadConstraints(final ApplicationData applicationData, final JLabel statusLabel){
		Thread runThread = (new Thread(){	      	
			public void run(){		
				statusLabel.setText("Filtering: Initializing..");
				//move all filteredData back to data first
				while(filteredData.size() > 0){
					data.add(filteredData.get(0));
					filteredData.remove(0);
				}
				sort();
				fireTableRowsInserted(0,getRowCount());
				DecimalFormat df = new DecimalFormat("0.##");
				//then run through all constraints		
				for(int x = 0; x < data.size();){
					statusLabel.setText("Filtering: " + df.format((x * 100.0) / data.size()) + "%");
					SequenceNameData tempData = data.get(x);
					boolean violated = false;
					for(int y = 0; y < constraintsData.size(); y++){
						if(constraintsData.get(y).isViolated(tempData.getFastaFormat(), null, applicationData)){
							filteredData.add(tempData);
							data.remove(x);
							fireTableRowsDeleted(x,x);
							violated = true;
							break;
						}													
					}
					if(violated == false)
						x++;
				}		
				statusLabel.setText("Done");
			}});
      		runThread.setPriority(Thread.MIN_PRIORITY); // UI has most priority
      		runThread.start();	 						
	}
    
    public void sort(){ Collections.sort(this.data, new SortByScore()); }
    
    public void addConstraints(final ApplicationData applicationData, final JLabel statusLabel){
		Thread runThread = (new Thread(){	      	
			public void run(){	
				//scan through data and move those that failed the new constraints to filteredData	
				DecimalFormat df = new DecimalFormat("0.##");
				for(int x = 0; x < data.size();){
					statusLabel.setText("Filtering: " + df.format((x * 100.0) / data.size()) + "%");
					SequenceNameData tempData = data.get(x);
					if(constraintsData.get(constraintsData.size() - 1).isViolated(tempData.getFastaFormat(), null, applicationData)){
						filteredData.add(tempData);
						data.remove(x);
						fireTableRowsDeleted(x,x);
					}else{
						x++;
					}
				}			
				statusLabel.setText("Done");
			}});
      		runThread.setPriority(Thread.MIN_PRIORITY); // UI has most priority
      		runThread.start();	 				
	}
    
    public int size(){
    	return data.size();
    }    
    
    public Class<?> getColumnClass(int c) {
    	return getValueAt(0, c).getClass();
    }
    
    public boolean isCellEditable(int row,int column){
    	if(column == 0)
    		return true;
    	else 
    		return false;
    }

    public SequenceNameTableModel(boolean showLength){
    	data = new ArrayList<SequenceNameData>();
    	undoList = new ArrayList<SequenceNameData>();
    	this.filteredData = new ArrayList<SequenceNameData>();
    	if(showLength){
    		columnNames = new String[4];
    		columnNames[3] = "Length";
    	}
    	else{
    		columnNames = new String[3];
    	}
    	columnNames[0] = " ";
    	columnNames[1] = "No.";
    	columnNames[2] = "Sequence Name";  	    		
    }
    
    public ArrayList<SequenceNameData> getData(){
    	return this.data;
    }
    
    public void reset(){
    	data = null;
    	data = new ArrayList<SequenceNameData>();
    	fireTableDataChanged();
    }
    
    public String getColumnName(int col) {
        return columnNames[col];
    }
    
    public int getColumnCount(){
        return columnNames.length;	//	No.,Classifier Name, # of Features
    }
    
    public int getRowCount() {    
    	if(data!=null)
        	return data.size();       
        else 
        	return -1;
    }       
    
    public Object getValueAt(int row, int col) {    
    	if(data!=null){
    		if(col == 1)
    			return "" + (row + 1);
    		else
    			return data.get(row).get(col);     		
    	}    		
    	else
    		return " ";
    }        
    
    public void add(SequenceNameData data){    	
    	this.data.add(data);
    	fireTableRowsInserted(getRowCount(),getRowCount());
    }
    
    public void undo(){
    	if(this.undoList.size() > 0){
    		SequenceNameData data = this.undoList.get(this.undoList.size() - 1);
    		this.data.add(data.getRow(), data);
    		this.undoList.remove(this.undoList.size() - 1);
    		fireTableRowsInserted(getRowCount(),getRowCount());
    	}
    }
    
    public void delete(int row){
    	SequenceNameData temp = this.data.get(row);
    	temp.setRow(row);
    	this.undoList.add(temp);    	
    	this.data.remove(row);    	
    	fireTableRowsDeleted(row, row);
    }
    
    public String getSequence(int row){
    	if(row < data.size())
    		return data.get(row).getSequence();
    	else
    		return "";
    }
    
    public String getRID(int index){
    	return this.data.get(index).getRID();
    }
    
    public String getLocalID(int index){
    	return this.data.get(index).getLocalID();
    }
    
    public String getHeader(int index){
    	if(index < data.size())
    		return data.get(index).getHeader();
    	else return "";
    }
    public String getScoreLine(int index){
    	if(index < data.size())
    		return data.get(index).getScoreLine();
    	else return "";
    }
    public void loadSequencesNameFile(String filename) throws Exception{
    	BufferedReader in = new BufferedReader(new FileReader(filename));                       
        reset();
        String line;
        while ((line = in.readLine()) != null) {	
        	add(new SequenceNameData(line,"","","",""));
        }
        in.close();
    }
    public String loadFastaFile(File file) throws Exception{
    	BufferedReader in = new BufferedReader(new FileReader(file.getAbsolutePath()));
        String outputDirectory = null;
        int countSequenceNumber = 0;
        String line;
        //there could be a bug within these few lines as having ">" at index 0 
        //is not limited to fasta only..
        //pdf files also have this feature..
        //sequenceNameTableModel.reset();
        reset();
        String eachSequence = "";
        String sequenceName = "";	   
        while ((line = in.readLine()) != null) {		            	
        	if(line.indexOf(">")==0){
        		countSequenceNumber++;	            		
        		if(eachSequence.length()!=0){
        			//sequenceNameTableModel.add(new SequenceNameData(sequenceName,eachSequence,""));
        			//Remove * at the end of sequence
        			int RIDindex = sequenceName.indexOf("RID: ");
        			String RID = null;
        			String localID = null; 
	        		if(RIDindex != -1){
	        			RID = sequenceName.substring(RIDindex + "RID: ".length(), 
	        					sequenceName.indexOf("\t", RIDindex));
	        			int localIDIndex = sequenceName.indexOf("LocalID: ");
	        			int outputDirectoryIndex = sequenceName.indexOf("\tOutputDirectory: ", localIDIndex);
	        			if(outputDirectoryIndex == -1){
	        				localID = sequenceName.substring(localIDIndex + "LocalID: ".length());
	        			}else{
	        				localID = sequenceName.substring(localIDIndex + "LocalID: ".length(), 
	        						outputDirectoryIndex);
	        				outputDirectory = sequenceName.substring(sequenceName.indexOf("OutputDirectory: ") + 
	        						"OutputDirectory: ".length());
	        			}
        			}
        			if(eachSequence.charAt(eachSequence.length() - 1) == '*')
        				add(new SequenceNameData(sequenceName,eachSequence.substring(0,eachSequence.length() - 1),"", RID, localID));
        			else
        				add(new SequenceNameData(sequenceName,eachSequence,"", RID, localID));
        		}
        		sequenceName = line;	
        		eachSequence = "";
        	}
        	else{
        		eachSequence += line;
        		//if = exists in sequence, error
        		if(eachSequence.indexOf("=")!=-1){
        			in.close();
        			throw new Exception("Please ensure that " + file.getAbsolutePath() + " is in FASTA format.");	            	
            	}	            			
        	}
        }	            
        if(countSequenceNumber == 0){
        	in.close();
        	throw new Exception("Please ensure that " + file.getAbsolutePath() + " is in FASTA format.");        	
        }else{
        	//sequenceNameTableModel.add(new SequenceNameData(sequenceName,eachSequence,""));
        	//Remove * at the end of sequence
        	int RIDindex = sequenceName.indexOf("RID: ");
			String RID = null;
			String localID = null; 
    		if(RIDindex != -1){
    			RID = sequenceName.substring(RIDindex + "RID: ".length(), 
    					sequenceName.indexOf("\t", RIDindex));
    			int localIDIndex = sequenceName.indexOf("LocalID: ");
    			int outputDirectoryIndex = sequenceName.indexOf("\tOutputDirectory: ", localIDIndex);
    			if(outputDirectoryIndex == -1){
    				localID = sequenceName.substring(localIDIndex + "LocalIDIndex: ".length());
    			}else{
    				localID = sequenceName.substring(localIDIndex + "LocalIDIndex: ".length(), 
    						outputDirectoryIndex);
    				outputDirectory = sequenceName.substring(sequenceName.indexOf("OutputDirectory: ") + 
    						"OutputDirectory: ".length());
    			}
			}
			if(eachSequence.charAt(eachSequence.length() - 1) == '*')
				add(new SequenceNameData(sequenceName,eachSequence.substring(0,eachSequence.length() - 1),"", RID, localID));
			else
				add(new SequenceNameData(sequenceName,eachSequence,"", RID, localID));
        }	      
        in.close();
        return outputDirectory;
    }
    
    public void save(BufferedWriter output) throws Exception{
		for(int x = 0; x < this.data.size(); x++){
			output.write(this.data.get(x).getHeader());
			output.newLine();
			output.write(this.data.get(x).getSequence());
			output.newLine();			
		}		
	}
    
    public void sort(int pos){
    	Collections.sort(data, new SortByPositionScore(pos));
    	fireTableRowsUpdated(0,getRowCount());    	
    }
    
    public void writeToFile(BufferedWriter output, BufferedWriter outputInfoOnly, String outputDirectory) throws IOException{
    	for(int x = 0; x < data.size(); x++){
    		String header = this.data.get(x).getHeader();
    		StringTokenizer st = new StringTokenizer(header, " ");
    		String localID = st.nextToken();
    		localID = localID.replaceAll(">", "");
    		localID = localID.trim();
    		outputInfoOnly.write(data.get(x).getHeader() + "\t" + data.get(x).getScoreLine() + "\tRID: " + 
    				data.get(x).getRID() + "\tLocalID: " + localID);
    		output.write(data.get(x).getHeader() + "\t" + data.get(x).getScoreLine() + "\tRID: " + 
    				data.get(x).getRID() + "\tLocalID: " + localID);
    		if(x == 0){
    			output.write("\tOutputDirectory: " + outputDirectory);
    			outputInfoOnly.write("\tOutputDirectory: " + outputDirectory);
    		}
    		output.newLine();
    		output.write(data.get(x).getSequence());
    		output.newLine();
    		outputInfoOnly.newLine();
    	}
    }
    
    public void writeToFile(BufferedWriter output, String outputDirectory) throws IOException{
    	for(int x = 0; x < data.size(); x++){
    		String header = this.data.get(x).getHeader();
    		StringTokenizer st = new StringTokenizer(header, " ");
    		String localID = st.nextToken();
    		localID = localID.replaceAll(">", "");
    		localID = localID.trim();
    		output.write(data.get(x).getHeader() + "\t" + data.get(x).getScoreLine() + "\tRID: " + 
    				data.get(x).getRID() + "\tLocalID: " + localID);
    		if(x == 0){
    			output.write("\tOutputDirectory: " + outputDirectory);
    		}
    		output.newLine();
    		output.write(data.get(x).getSequence());
    		output.newLine();
    	}
    }
    public void saveFasta(BufferedWriter output, int foldNum, int numOfFolds) throws IOException{
    	for(int x = 0; x < data.size(); x++){
    		if(numOfFolds == -1 || x % numOfFolds == foldNum){
	    		output.write(data.get(x).getHeader());
	    		output.newLine();
	    		output.write(data.get(x).getSequence());
	    		output.newLine();
    		}
    	}
    }
    
    public void saveFasta(BufferedWriter output) throws IOException{
    	saveFasta(output,-1,-1);
    }
}

class SortByPositionScore implements Comparator<SequenceNameData>{
	private int pos;
	public SortByPositionScore(int pos){
		this.pos = pos;
	}
	public int compare(SequenceNameData o1, SequenceNameData o2){
		double firstScore = o1.getScore(pos);
		double secondScore = o2.getScore(pos);
		if(firstScore == -1 || secondScore == -1)
			throw new ClassCastException();
		if(secondScore > firstScore)
			return 1;
		else if(secondScore < firstScore)
			return -1;
		else
			return 0;		
	}
}

