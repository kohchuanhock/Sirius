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
package sirius.misc.predictionfileanalysis;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import java.awt.Dimension;
import java.io.*;
import java.util.*;

public class ResultFileTableModel extends AbstractTableModel{
	static final long serialVersionUID = sirius.Sirius.version;
	private ArrayList<String> columnNames;
	private ArrayList<ArrayList<SequenceNameAndScore>> originalData;
	private ArrayList<SequenceNameAndScore> tabulatedData;
	private boolean isOriginalData;
	private int sizeGtThreshold;
	private int totalSizeInOriginalData;
	private JTable table;		
	
	public ResultFileTableModel(boolean isOriginalData){
		this.isOriginalData = isOriginalData;		
		this.totalSizeInOriginalData = 0;			
	}
	
	public void setTable(JTable table){
		this.table = table;
	}
	
	public boolean isCellEditable(int row,int column){
    	return false;
    }
    
	public int getColumnCount() {
		if(columnNames == null)
			return 0;
		else
			return columnNames.size() + 1;
	}
	
	public String getColumnName(int col) {
		if(col == 0)
			return "No.";
		else
			return columnNames.get(--col);		
   	}

	public int getRowCount() {	
		int maxSize = 0;
		if(this.isOriginalData){
			if(this.originalData != null){
				for(int x = 0; x < this.originalData.size(); x++){
					if(maxSize < this.originalData.get(x).size())
						maxSize = this.originalData.get(x).size();
				}
			}
		}else{
			if(this.tabulatedData != null){
				//maxSize = this.tabulatedData.size();
				maxSize = sizeGtThreshold;
			}
		}
		return maxSize;
	}

	public Object getValueAt(int row, int col) {			
		if(this.isOriginalData){
			if(col == 0){
				return row + 1;
			}else{				
				int index = (col - 1) / 2;
				if(row < this.originalData.get(index).size()){
					if(col%2 == 1)					
						return this.originalData.get(index).get(row).getName();
					else
						return this.originalData.get(index).get(row).getScore();
				}
			}
		}else{
			switch(col){
			case 0: return row + 1;
			case 1: return this.tabulatedData.get(row).getName();
			case 2: return this.tabulatedData.get(row).getScore();
			case 3: return this.tabulatedData.get(row).getRankScore();
			}
		}
		return " ";
	}
	
	public void deletePredictionFile(int index){
		this.totalSizeInOriginalData -= this.originalData.get(index).size();
		this.originalData.remove(index);
		if(this.originalData.size() == 0)
			this.originalData = null;
		this.columnNames.remove(index * 2);
		this.columnNames.remove(index * 2);
		if(this.columnNames.size() == 0)
			this.columnNames = null;		
		updateTable();
	}
	
	private void setRankScore(ArrayList<SequenceNameAndScore> currentArrayList){
		int rankScore = currentArrayList.size();
		for(int x = 0; x < currentArrayList.size(); x++){			
			currentArrayList.get(x).setRankScore(rankScore--);
		}
	}
	
	public void loadPredictionFile(BufferedReader in, String fileName) throws Exception{
		String inputName;		
		if(this.originalData == null)
			this.originalData = new ArrayList<ArrayList<SequenceNameAndScore>>();	
		ArrayList<SequenceNameAndScore> currentArrayList = new ArrayList<SequenceNameAndScore>();
		while((inputName = in.readLine()) != null){
			String sequence = in.readLine();
			String inputScoreLine = in.readLine();
			String positionZeroScore = inputScoreLine.substring(inputScoreLine.indexOf("0=") + ("0=").length());
			double score = Double.parseDouble(positionZeroScore);
			currentArrayList.add(new SequenceNameAndScore(inputName, score, sequence));			
		}		    			
		this.originalData.add(currentArrayList);
		setRankScore(currentArrayList);
		this.totalSizeInOriginalData += currentArrayList.size();
		if(this.columnNames == null)
			this.columnNames = new ArrayList<String>();
		this.columnNames.add(fileName);
		this.columnNames.add("Score");	
		updateTable();
	}
	
	private void updateTable(){
		fireTableStructureChanged();
		fireTableDataChanged();
		if(this.columnNames != null){
			this.table.getColumnModel().getColumn(0).setMinWidth(50);
			this.table.getColumnModel().getColumn(0).setMaxWidth(50);
			for(int x = 1; x <= this.columnNames.size(); x++){
				if(x%2 == 1)
					this.table.getColumnModel().getColumn(x).setMinWidth(150);			
				else
					this.table.getColumnModel().getColumn(x).setMinWidth(70);
			}
			this.table.setPreferredSize(new Dimension(this.table.getColumnModel().getTotalColumnWidth(),this.table.getRowCount() * 18));
		}else{
			this.table.setPreferredSize(new Dimension(0,0));
		}
	}
	
	public ArrayList<ArrayList<SequenceNameAndScore>> getOriginalData(){
		return this.originalData;
	}
	
	public int getTotalSizeInOriginalData(){
		return this.totalSizeInOriginalData;
	}
	
	public void sortByScore(){
		Collections.sort(this.tabulatedData, new SortByScore());
		fireTableDataChanged();
	}
	
	public void sortByRankScore(){
		Collections.sort(this.tabulatedData, new SortByRankScore());
		fireTableDataChanged();
	}
	
	public void tabulateTotalScore(ArrayList<ArrayList<SequenceNameAndScore>> originalData, double threshold, JLabel statusLabel, 
			int totalSizeInOriginalData, int index){
		this.totalSizeInOriginalData = totalSizeInOriginalData;
		this.originalData = originalData;
		if(this.columnNames == null){
			this.columnNames = new ArrayList<String>();
			this.columnNames.add("Sequence Name");
			this.columnNames.add("Score");
			this.columnNames.add("RankScore");
		}				
		this.tabulatedData = new ArrayList<SequenceNameAndScore>();
		int count = 0;
		for(int x = 0; x < this.originalData.size(); x++){			
			for(int y = 0; y < this.originalData.get(x).size(); y++){
				count++;
				statusLabel.setText(count + " / " + this.totalSizeInOriginalData);
				boolean found = false;
				SequenceNameAndScore current = this.originalData.get(x).get(y);
				for(int z = 0; z < this.tabulatedData.size(); z++){					 
					if(this.tabulatedData.get(z).getHashCode() == current.getHashCode()){						
						if(this.tabulatedData.get(z).getName().compareTo(current.getName()) == 0){
							this.tabulatedData.get(z).addOnToScore(current.getScore());
							this.tabulatedData.get(z).addOnToRankScore(current.getRankScore());
							found = true;
							break;
						}else{
							JOptionPane.showMessageDialog(null, 
								"If you see this, it means that even if hashcode are the same does not mean that the strings are the same",
								"ERROR",JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				if(found == false){
					this.tabulatedData.add(new SequenceNameAndScore(current.getName(), 
							current.getScore(), current.getRankScore(), current.getSequence()));					
				}
			}		
		}
		Collections.sort(this.tabulatedData, new SortByScore());
		int x;
		for(x = 0; x < this.tabulatedData.size(); x++){
			if(this.tabulatedData.get(x).getScore() < threshold)
				break;
		}		
		this.sizeGtThreshold = (x);		
		fireTableStructureChanged();
		fireTableDataChanged();	
		this.table.getColumnModel().getColumn(0).setMaxWidth(50);
		statusLabel.setText("Done");
		if(index == 1)
			this.sortByRankScore();
	}
	
	public void save(BufferedWriter output) throws Exception{
		for(int x = 0; x < this.sizeGtThreshold; x++){
			output.write(this.tabulatedData.get(x).getName());
			output.newLine();			
			output.write("" + this.tabulatedData.get(x).getScore());
			output.newLine();
		}
	}
	
	public void saveWithLinks(BufferedWriter output, int index) throws Exception{
		for(int x = 0; x < this.originalData.get(index).size(); x++){
			String name = this.originalData.get(index).get(x).getName();
			String remarks = " Remarks:";
			for(int y = 0; y < this.originalData.size(); y++){
				if(y == index)
					continue;
				boolean found = false;
				for(int z = 0; z < this.originalData.get(y).size(); z++){
					if(name.compareTo(this.originalData.get(y).get(z).getName()) == 0){
						found = true;
						remarks += " " +  (z+1);
						break;//assume that each files dun have repeated sequence name
					}
				}
				if(found == false)
					remarks += " -1";
			}			
			output.write(name + remarks);
			output.newLine();
			output.write(this.originalData.get(index).get(x).getSequence());
			output.newLine();
			output.write("0=" + this.originalData.get(index).get(x).getScore());
			output.newLine();
		}
	}
}

class SequenceNameAndScore{	
	String sequenceName;
	double score;
	int hashCode;
	int rankScore;
	String sequence;
	
	public SequenceNameAndScore(String sequenceName, double score, int rankScore, String sequence){
		this.sequence = sequence;
		this.sequenceName = sequenceName;
		this.score = score;
		this.hashCode = this.sequenceName.hashCode();
		this.rankScore = rankScore;
	}
	
	public SequenceNameAndScore(String sequenceName, double score,String sequence){
		this(sequenceName,score,0,sequence);
	}
	
	public String getSequence(){
		return this.sequence;
	}
	
	public String getName(){
		return this.sequenceName;
	}
	
	public double getScore(){
		return this.score;
	}	
	
	public int getRankScore(){
		return this.rankScore;
	}
	
	public int getHashCode(){
		return this.hashCode;
	}
	
	public void setRankScore(int rankScore){
		this.rankScore = rankScore;
	}
	
	public void addOnToRankScore(int rankScore){
		this.rankScore += rankScore;
	}
	
	public void addOnToScore(double score){
		this.score += score;
	}
}

class SortByRankScore implements Comparator<SequenceNameAndScore>{
	public SortByRankScore(){		
	}
	public int compare(SequenceNameAndScore o1, SequenceNameAndScore o2){
		double firstScore = o1.getRankScore();
		double secondScore = o2.getRankScore();
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

class SortByScore implements Comparator<SequenceNameAndScore>{
	public SortByScore(){		
	}
	public int compare(SequenceNameAndScore o1, SequenceNameAndScore o2){
		double firstScore = o1.getScore();
		double secondScore = o2.getScore();
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

class SortByHashCode implements Comparator<SequenceNameAndScore>{
	public SortByHashCode(){		
	}
	public int compare(SequenceNameAndScore o1, SequenceNameAndScore o2){
		double firstScore = o1.getHashCode();
		double secondScore = o2.getHashCode();
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