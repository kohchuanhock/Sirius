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
package sirius.misc.randomizesequence;

import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.text.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

public class RandomizeSequenceDialog extends JDialog implements ActionListener{
	static final long serialVersionUID = sirius.Sirius.version;
	
	CompositionTableModel compositionTableModel;
	JButton sortByObserved;
	JButton sortByExpected;
	JButton sortByChiSquare;
	JButton sortByObservedPercent;
	JButton sortByExpectedPercent;
	JButton sortByChiSquarePercent;
	
	public RandomizeSequenceDialog(String header, String sequence){			
		setTitle("Sequence Composition");		
		setLayout(new BorderLayout());
		
		JPanel topPanel = new JPanel(new GridLayout(2,1));
		topPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Sequence Details"),BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		JLabel headerLabel;
		if(header.length() > 20)
			headerLabel = new JLabel("Header: " + header.substring(0,20));
		else
			headerLabel = new JLabel("Header: " + header);
		JLabel lengthLabel = new JLabel("Length: " + sequence.length());
		topPanel.add(headerLabel);
		topPanel.add(lengthLabel);
		add(topPanel, BorderLayout.NORTH);
				
		JPanel sequenceCompositionPanel = new JPanel(new GridLayout(1,1));
		sequenceCompositionPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Sequence Composition"),BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		this.compositionTableModel = new CompositionTableModel(sequence);
    	JTable sequenceCompositionTable = new JTable(this.compositionTableModel);
    	sequenceCompositionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);    	
    	sequenceCompositionTable.getColumnModel().getColumn(0).setMinWidth(20);    	
    	sequenceCompositionTable.getColumnModel().getColumn(1).setMinWidth(40);
    	sequenceCompositionTable.getColumnModel().getColumn(2).setMinWidth(30); 
    	sequenceCompositionTable.getColumnModel().getColumn(3).setMinWidth(30); 
    	sequenceCompositionTable.getColumnModel().getColumn(4).setMinWidth(30);
    	sequenceCompositionTable.getColumnModel().getColumn(5).setMinWidth(30); 
    	sequenceCompositionTable.getColumnModel().getColumn(6).setMinWidth(30); 
    	sequenceCompositionTable.getColumnModel().getColumn(7).setMinWidth(30); 
    	JScrollPane sequenceTableScrollPane = new JScrollPane(sequenceCompositionTable);    	
    	sequenceCompositionPanel.add(sequenceTableScrollPane,BorderLayout.CENTER);
    	add(sequenceCompositionPanel, BorderLayout.CENTER);
    	
    	this.sortByObserved = new JButton("Observed");
    	this.sortByObserved.addActionListener(this);
    	this.sortByExpected = new JButton("Expected");
    	this.sortByExpected.addActionListener(this);
    	this.sortByChiSquare = new JButton("ChiSquare");
    	this.sortByChiSquare.addActionListener(this);
    	this.sortByObservedPercent = new JButton("Observed(%)");
    	this.sortByObservedPercent.addActionListener(this);
    	this.sortByExpectedPercent = new JButton("Expected(%)");
    	this.sortByExpectedPercent.addActionListener(this);
    	this.sortByChiSquarePercent = new JButton("ChiSquare(%)");
    	this.sortByChiSquarePercent.addActionListener(this);
    	JPanel bottomPanel = new JPanel(new GridLayout(2,1,5,5));
		bottomPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Sort By"),BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		JPanel northPanel = new JPanel(new GridLayout(1,3,5,5));
		northPanel.add(this.sortByObserved);
		northPanel.add(this.sortByExpected);
		northPanel.add(this.sortByChiSquare);		
		JPanel southPanel = new JPanel(new GridLayout(1,3,5,5));
		southPanel.add(this.sortByObservedPercent);
		southPanel.add(this.sortByExpectedPercent);
		southPanel.add(this.sortByChiSquarePercent);		
		bottomPanel.add(northPanel);
		bottomPanel.add(southPanel);		
		add(bottomPanel, BorderLayout.SOUTH);
		//setSize(900,600);
		this.pack();
	}

	public void actionPerformed(ActionEvent ae) {		
		if(ae.getSource().equals(this.sortByObserved))
			this.compositionTableModel.sortByObserved();
		else if(ae.getSource().equals(this.sortByExpected))
			this.compositionTableModel.sortByExpected();
		else if(ae.getSource().equals(this.sortByChiSquare))
			this.compositionTableModel.sortByChiSquare();
		else if(ae.getSource().equals(this.sortByObservedPercent))
			this.compositionTableModel.sortByObservedPercent();
		else if(ae.getSource().equals(this.sortByExpectedPercent))
			this.compositionTableModel.sortByExpectedPercent();
		else if(ae.getSource().equals(this.sortByChiSquarePercent))
			this.compositionTableModel.sortByChiSquarePercent();
	}
}

class CompositionData{	
	String name;
	double observedPercent;		
	double chiSquare;
	double chiSquarePercent;
	double expectedPercent;
	int observed;
	int expected;	
	String background;
	
	public CompositionData(String name){		
		this.name = name;
		this.observed = 0;
	}
	
	public void increment(){
		this.observed ++;
	}
	
	public void computeStats(String name, int length, Hashtable<String,CompositionData> oneGram){
		this.name = name;
		this.observedPercent = (double) this.observed / (length - name.length() + 1);		
		if(name.length() > 1){			
			this.expectedPercent = 1;
			for(int x = 0; x < name.length(); x++){
				this.expectedPercent *= oneGram.get(name.substring(x,x+1)).getObservedPercent();
				this.background += "" + oneGram.get(name.substring(x,x+1)).getObservedPercent();
				if(x + 1 != name.length())
					this.background += ",";
			}
			this.expected = (int)Math.rint(this.expectedPercent * (length - name.length() + 1));
			this.chiSquarePercent = Math.pow(this.observedPercent - this.expectedPercent, 2) / this.expectedPercent;
			this.chiSquare = Math.pow(this.observed - this.expected, 2) / (this.expectedPercent + 1);
		}
	}
	
	public String getBackground(){
		return this.background;
	}
	
	public String getName(){
		return this.name;
	}
	
	public double getObservedPercent(){
		return this.observedPercent;
	}
	
	public int getObserved(){
		return this.observed;
	}	
	
	public int getExpected(){
		return this.expected;
	}
	
	public double getExpectedPercent(){
		return this.expectedPercent;
	}	
	
	public double getChiSquare(){
		return this.chiSquare;
	}
	
	public double getChiSquarePercent(){
		return this.chiSquarePercent;
	}
	
}

class CompositionTableModel extends AbstractTableModel{
	static final long serialVersionUID = sirius.Sirius.version;

	private String[] columnNames; 
	ArrayList<CompositionData> data;
	Hashtable<String,CompositionData> oneGram;
	Hashtable<String,CompositionData> twoGram;
	Hashtable<String,CompositionData> threeGram; 
	DecimalFormat df;
    
    public CompositionTableModel(String sequence){    	     	
    	this.df = new DecimalFormat("0.####");
    	columnNames = new String[8];
    	columnNames[0] = "No.";
    	columnNames[1] = "Name";
    	columnNames[2] = "Observed";
    	columnNames[3] = "Expected";
    	columnNames[4] = "Chi-Square";
    	columnNames[5] = "Observed(%)";    	
    	columnNames[6] = "Expected(%)";
    	columnNames[7] = "Chi-Square(%)";
    	
    	this.oneGram = new Hashtable<String, CompositionData>();
    	this.twoGram = new Hashtable<String, CompositionData>();
    	this.threeGram = new Hashtable<String, CompositionData>();
    	this.data = new ArrayList<CompositionData>();
    	
    	generateAllPossible(1, this.oneGram);
    	generateAllPossible(2, this.twoGram);
    	generateAllPossible(3, this.threeGram);
    	
    	computeStats(sequence,this.oneGram,1);
    	computeStats(sequence,this.twoGram,2);
    	computeStats(sequence,this.threeGram,3);    
    	    	
    	sortByChiSquare();
    	
    	showOneGramObservedZero();
    }
    
    private void showOneGramObservedZero(){
    	for(Enumeration<String> e = this.oneGram.keys(); e.hasMoreElements();){
			String current = e.nextElement();
			if(this.oneGram.get(current).getObserved() == 0)
				throw new Error("Not Found: " + current);
    	}
    }
    
    private void generateAllPossible(int kgram, Hashtable<String, CompositionData> gram){
    	    								
    	String symbol = "ACDEFGHIKLMNPQRSTVWY";    	
    	
    	for(int x = 0; x < (int)Math.pow(20,kgram); x++){
			int tempKgram = kgram;
			int y = x;
			String tempString = "";
			while((tempKgram--)>0){
				tempString += symbol.charAt(y%20);							
				y = y/20;
			}			
			gram.put(tempString, new CompositionData(tempString));
		}
    }
    
    private void computeStats(String sequence,Hashtable<String,CompositionData> gram, int gramLength){    	
    	for(int x = 0; x <= sequence.length() - gramLength; x++){
    		String current = sequence.substring(x, x+gramLength);
    		if(current.indexOf("*") != -1)
    			continue;
    		if(gram.containsKey(current))    		    		
    			gram.get(current).increment();
    		else
    			gram.put(current, new CompositionData(current));
    	}    	
		for(Enumeration<String> e = gram.keys(); e.hasMoreElements();){
			String current = e.nextElement();
			gram.get(current).computeStats(current, sequence.length(), this.oneGram);
			if(gramLength > 1){
				data.add(gram.get(current));
			}    		      
    	}
    }
    
    public int size(){
    	return data.size();
    }
    
    public void reset(){
    	data = null;
    	data = new ArrayList<CompositionData>();
    	fireTableDataChanged();
    }
    
    public String getColumnName(int col) {
        return columnNames[col];
    }
    
    public int getColumnCount(){
        return columnNames.length;
    }
    
    public int getRowCount() {    
    	if(data!=null)
        	return data.size();       
        else 
        	return -1;
    }       
    
    public Object getValueAt(int row, int col) {    
    	if(data!=null){
    		switch(col){
    		case 0: return "" + (row + 1);    		    	
    		case 1: return data.get(row).getName();
    		case 2: return "" + data.get(row).getObserved();
    		case 3: return "" + data.get(row).getExpected();
    		case 4: return df.format(data.get(row).getChiSquare());
    		case 5: return df.format(data.get(row).getObservedPercent());    		
    		case 6: return df.format(data.get(row).getExpectedPercent());
    		case 7: return df.format(data.get(row).getChiSquarePercent());
    		default: return "ERROR";
    		}
    	}    		
    	else
    		return " ";
    }        
    
    public void add(CompositionData data){    	
    	this.data.add(data);
    	fireTableRowsInserted(getRowCount(),getRowCount());
    }
    
    public void sortByObserved(){
    	Collections.sort(data, new SortByObserved());
    	fireTableRowsUpdated(0,getRowCount());    	
    }
    
    public void sortByExpected(){
    	Collections.sort(data, new SortByExpected());
    	fireTableRowsUpdated(0,getRowCount());    	
    }   
    
    public void sortByChiSquare(){
    	Collections.sort(data, new SortByChiSquare());
    	fireTableRowsUpdated(0,getRowCount());    	
    }   
    
    public void sortByObservedPercent(){
    	Collections.sort(data, new SortByObservedPercent());
    	fireTableRowsUpdated(0,getRowCount());    	
    }   
    
    public void sortByExpectedPercent(){
    	Collections.sort(data, new SortByExpectedPercent());
    	fireTableRowsUpdated(0,getRowCount());    	
    }   
    
    public void sortByChiSquarePercent(){
    	Collections.sort(data, new SortByChiSquarePercent());
    	fireTableRowsUpdated(0,getRowCount());    	
    }   
}

class SortByObserved implements Comparator<CompositionData>{	
	public int compare(CompositionData o1, CompositionData o2){
		double firstScore = o1.getObserved();
		double secondScore = o2.getObserved();
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

class SortByExpected implements Comparator<CompositionData>{	
	public int compare(CompositionData o1, CompositionData o2){
		double firstScore = o1.getExpected();
		double secondScore = o2.getExpected();
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

class SortByChiSquare implements Comparator<CompositionData>{	
	public int compare(CompositionData o1, CompositionData o2){
		double firstScore = o1.getChiSquare();
		double secondScore = o2.getChiSquare();
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

class SortByObservedPercent implements Comparator<CompositionData>{	
	public int compare(CompositionData o1, CompositionData o2){
		double firstScore = o1.getObservedPercent();
		double secondScore = o2.getObservedPercent();
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

class SortByExpectedPercent implements Comparator<CompositionData>{	
	public int compare(CompositionData o1, CompositionData o2){
		double firstScore = o1.getExpectedPercent();
		double secondScore = o2.getExpectedPercent();
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

class SortByChiSquarePercent implements Comparator<CompositionData>{	
	public int compare(CompositionData o1, CompositionData o2){
		double firstScore = o1.getChiSquarePercent();
		double secondScore = o2.getChiSquarePercent();
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