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

import java.io.BufferedWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JLabel;
import javax.swing.table.AbstractTableModel;

import sirius.main.ApplicationData;
import sirius.utils.FastaFormat;


public class FastaFormatWithScoreTableModel extends AbstractTableModel{
	static final long serialVersionUID = sirius.Sirius.version;
	
	ArrayList<FastaFormatWithScoreAndArff> data;
	ArrayList<FastaFormatWithScoreAndArff> filteredData;//store the filtered away records so that can put them back when constraints are deleted
	ArrayList<sirius.nnsearcher.main.Constraints> constraintsData;
	String[] columnNames;
	long numOfRuns;
	
	public FastaFormatWithScoreTableModel(boolean showPValue){
		this.data = new ArrayList<FastaFormatWithScoreAndArff>();
		this.filteredData = new ArrayList<FastaFormatWithScoreAndArff>();
		this.columnNames = new String[4];
		this.columnNames[0] = "No.";
		this.columnNames[1] = "Header";
		this.columnNames[2] = "Length";
		this.columnNames[3] = "Score";
		/*if(showPValue == false){
			this.columnNames = new String[4];
			this.columnNames[0] = "No.";
			this.columnNames[1] = "Header";
			this.columnNames[2] = "Length";
			this.columnNames[3] = "Score";
		}else{
			this.columnNames = new String[5];
			this.columnNames[0] = "No.";
			this.columnNames[1] = "Header";
			this.columnNames[2] = "Length";
			this.columnNames[3] = "Score";
			this.columnNames[4] = "P-Value";
		}*/
	}
	
	public FastaFormatWithScoreAndArff get(int index){
		return data.get(index);
	}
		
	public void save(BufferedWriter output, boolean isFasta) throws Exception{
		for(int x = 0; x < this.data.size(); x++){
			output.write(this.data.get(x).getHeader());
			output.newLine();
			output.write(this.data.get(x).getSequence());
			output.newLine();
			if(isFasta == false){
				output.write("0=" + this.data.get(x).getScore());
				output.newLine();				
			}			
		}		
	}
	
	public void sort(){ Collections.sort(this.data, new SortByScore()); }
	
	public void sortInvert(){ Collections.sort(this.data, new SortByScoreInvert()); }
	
	public void update(){ fireTableDataChanged(); }
	
	public void computePValue(long numOfRuns){
		this.numOfRuns = numOfRuns;
		for(int x = 0; x < data.size(); x++){
			data.get(x).setPValue(numOfRuns);
		}
	}
	
	public void setConstraintsData(ArrayList<sirius.nnsearcher.main.Constraints> constraintsData){ this.constraintsData = constraintsData; }
		
	public int getColumnCount() { return this.columnNames.length; }

	public int getRowCount() { return this.data.size(); }
	
	public Object getValueAt(int row, int col) {		
		switch(col){    			
			case 0: return (row + 1);
			case 1: return data.get(row).getHeader();			    		
			case 2: return data.get(row).getSequence().length();
			case 3: return data.get(row).getScore();
			case 4: if(data.get(row).getPValue() == 0.0)
						return "<" + (1.0/this.numOfRuns);											
					else
						return data.get(row).getPValue();
			//should not reach default
			default: return "-1";
		}        		
    	
	}

	public void deleteConstraints(final ApplicationData applicationData, final JLabel statusLabel){
		Thread runThread = (new Thread(){	      	
			public void run(){	
				//scan through those filteredData and add back those that passed the remaining constraints back to data	
				DecimalFormat df = new DecimalFormat("0.##");
				for(int x = 0; x < filteredData.size();){
					statusLabel.setText("Filtering: " + df.format((x * 100.0) / filteredData.size()) + "%");
					boolean violated = false;
					FastaFormatWithScoreAndArff tempData = filteredData.get(x);
					for(int y = 0; y < constraintsData.size(); y++){
						//check if constraint have been violated
						if(constraintsData.get(y).isViolated(tempData.getFastaFormat(),tempData.getArff(), applicationData)){				
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
	
	public void transferToFiltered(int x){
		FastaFormatWithScoreAndArff tempData = data.get(x);
		filteredData.add(tempData);
		data.remove(x);
		fireTableRowsDeleted(x,x);
	}
	
	public void addConstraints(final ApplicationData applicationData, final JLabel statusLabel){
		Thread runThread = (new Thread(){	      	
			public void run(){	
				//scan through data and move those that failed the new constraints to filteredData	
				DecimalFormat df = new DecimalFormat("0.##");
				for(int x = 0; x < data.size();){
					statusLabel.setText("Filtering: " + df.format((x * 100.0) / data.size()) + "%");
					FastaFormatWithScoreAndArff tempData = data.get(x);
					if(constraintsData.get(constraintsData.size() - 1).isViolated(tempData.getFastaFormat(), tempData.getArff(), applicationData)){
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
					FastaFormatWithScoreAndArff tempData = data.get(x);
					boolean violated = false;
					for(int y = 0; y < constraintsData.size(); y++){
						if(constraintsData.get(y).isViolated(tempData.getFastaFormat(), tempData.getArff(), applicationData)){
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
	
	public int size(){ return this.data.size(); }	
	public void setArff(int index, weka.core.Instance arffData){ this.data.get(index).setArff(arffData); }
	public void resetData(){ this.data = new ArrayList<FastaFormatWithScoreAndArff>(); }	
	public void add(FastaFormat fastaFormat, double score, double pValue){ this.data.add(new FastaFormatWithScoreAndArff(new FastaFormatWithScore(fastaFormat, score, pValue))); }	
	public String getColumnName(int col) { return this.columnNames[col]; } 	
}

class SortByScore implements Comparator<FastaFormatWithScoreAndArff>{
	public int compare(FastaFormatWithScoreAndArff o1, FastaFormatWithScoreAndArff o2){
		if(o2.getScore() > o1.getScore())
			return 1;
		else if(o2.getScore() < o1.getScore())
			return -1;
		else
			return 0;    
		}
}

class SortByScoreInvert implements Comparator<FastaFormatWithScoreAndArff>{
	public int compare(FastaFormatWithScoreAndArff o1, FastaFormatWithScoreAndArff o2){
		if(o2.getScore() > o1.getScore())
			return -1;
		else if(o2.getScore() < o1.getScore())
			return 1;
		else
			return 0;    
		}
}

class FastaFormatWithScoreAndArff{
	private FastaFormatWithScore data;
	private weka.core.Instance arffData;		
	public FastaFormatWithScoreAndArff(FastaFormatWithScore data){ this.data = data; }	
	public FastaFormat getFastaFormat(){ return this.data.getFastaFormat(); }	
	public String getHeader(){ return this.data.getHeader(); }		
	public double getScore(){ return this.data.getScore(); }	
	public String getSequence(){ return this.data.getSequence(); }	
	public double getPValue(){ return this.data.getPValue(); }	
	public void setPValue(long numOfRuns){ this.data.setPValue(numOfRuns); }	
	public int getPValueCount(){ return this.data.getPValueCount(); }	
	public void incrementPValueCount(){ this.data.incrementPValueCount(); }	
	public void setArff(weka.core.Instance arffData){ this.arffData = arffData; }	
	public weka.core.Instance getArff(){ return this.arffData; }
}