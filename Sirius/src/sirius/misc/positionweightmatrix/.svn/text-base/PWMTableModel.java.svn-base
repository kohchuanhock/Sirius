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
package sirius.misc.positionweightmatrix;

import java.text.DecimalFormat;
import java.util.*;

import javax.swing.table.AbstractTableModel;
import javax.swing.*;

import sirius.trainer.main.ScoringMatrix;

import java.awt.*;


public class PWMTableModel extends AbstractTableModel{
	static final long serialVersionUID = sirius.Sirius.version;
	private String[] columnNames;
	private Hashtable<Character, ArrayList<MyInteger>> data;//character <=> position/value	
	private ArrayList<Character> keyArrayList;//index <=> character 
	private ArrayList<Double> entropy;//position <=> entropy	
	private JTable table;
	private int showFreq;
	private ArrayList<Double> weights;
	private boolean hasEntropy;
	
	public PWMTableModel(boolean hasEntropy){
		this.hasEntropy = hasEntropy;	
	}
	
	public Hashtable<Character, ArrayList<MyInteger>> getData(){
		return this.data;
	}
	
	public void setTable(JTable table){
		this.table = table;
	}
	
	public String getColumnName(int col) {
        return columnNames[col];
   	}
    
    public int getColumnCount(){
    	if(columnNames == null)
    		return 0;
    	else
    		return columnNames.length;
    }
    
    public int getRowCount() {    
    	if(data!=null)
    		if(this.hasEntropy)
    			return data.size() + 1;
    		else
    			return data.size();
        else 
        	return -1;
    }       
    public Object getValueAt(int row, int col) {    
    	DecimalFormat df = new DecimalFormat("0.##");
    	if(data!=null){    	
    		if(col == 0){
    			if(row == data.size())
    				return "Entropy";
    			else
    				return this.keyArrayList.get(row);
    		}else if(this.hasEntropy && row == data.size()){
    			return df.format(this.entropy.get(--col));
    		}else{
    			switch(showFreq){    			
    			case 0: return this.data.get(keyArrayList.get(row)).get(--col).getValue();    			
    			case 1: return df.format(this.data.get(keyArrayList.get(row)).get(--col).getPercentageValue() * 100);    			
    			default: return -1;
    			}
    		}
    	}
    	else
    		return " ";
    }
    public void update(Hashtable<Character, ArrayList<MyInteger>> hashtable, int maxSequenceLength, int showFreq){    
    	this.showFreq = showFreq;
    	columnNames = new String[maxSequenceLength + 1];
    	columnNames[0] = " ";
    	for(int x = 1; x <= maxSequenceLength; x++){
    		columnNames[x] = "" + x;
    	}
    	this.data = hashtable;
    	this.keyArrayList = new ArrayList<Character>();
    	for (Enumeration<Character> e = hashtable.keys(); e.hasMoreElements();){    		
    		this.keyArrayList.add(e.nextElement());
    	}    	    	    	
    	fireTableStructureChanged();    	
    	for(int x = 0; x < maxSequenceLength; x++){
			table.getColumnModel().getColumn(x).setMinWidth(50);     
		}
    	table.setPreferredSize(new Dimension(70 * (maxSequenceLength + 1), getRowCount() * 20));    	
    }
    
    public void updateTable(int showFreq){
    	this.showFreq = showFreq;
    	fireTableDataChanged();
    }
    
    public double getEnergy(String sequence, ScoringMatrix scoringMatrix, int maxInfluenceDistance){
    	double energy = 0.0;    	
    	for(int x = 0; x < sequence.length(); x++){
    		char sequenceAA = sequence.charAt(x);
    		for(Enumeration<Character> e = this.data.keys(); e.hasMoreElements();){
				char currentAA = e.nextElement();
    			ArrayList<MyInteger> probabilityArrayList = this.data.get(currentAA);
    			for(int z = 0; z < ((maxInfluenceDistance-1)*2)+1; z++){
    				//*-1 here because more -ve is more favorable
					double pairwiseEnergy = scoringMatrix.getScore(currentAA, sequenceAA) * probabilityArrayList.get(x+z).value * -1;
					//if(influenceProportionToD)
						//pairwiseEnergy = pairwiseEnergy * (1.0/(Math.abs(x-maxInfluenceDistance)+1.0));
					energy += pairwiseEnergy;
    			}
			}
    	}
    	energy = ((energy - MyInteger.minEnergyValue) / (MyInteger.maxEnergyValue - MyInteger.minEnergyValue));    	
    	if(energy < 0 || energy > 1)
    		System.err.println("Error: Energy is < 0 or > 1");
    	return energy;
    }
    
    public double getPWMScore(String sequence, BitSet positionsToConsider){
    	double pwmScore = 0.0;
    	for(int x = 0; x < sequence.length(); x++){
    		if(positionsToConsider.get(x) == false)
    			continue;
    		ArrayList<MyInteger> temp = data.get(sequence.charAt(x));
    		if(temp != null)    	
    			pwmScore += (temp.get(x).getPercentageValue() * this.getWeightsAt(x));    	
    		else
    			throw new Error("Character in sequence is not found in PWM: " + sequence.charAt(x));    		
    	}    	    	
    	return ((pwmScore - MyInteger.minPWMValue) / (MyInteger.maxPWMValue - MyInteger.minPWMValue));
    }
    
    public void calulateEntropy(int maxSequenceLength, BitSet positionsToConsider, double threshold, boolean useEntropyAsWeights){
    	this.entropy = new ArrayList<Double>();    	
    	this.weights = new ArrayList<Double>();
    	positionsToConsider.clear();    	
    	for(int x = 0; x < maxSequenceLength; x++){
    		double positionSum = 0.0;
    		for(Enumeration<Character> e = data.keys(); e.hasMoreElements();){    			
    			ArrayList<MyInteger> positionFrequencyArrayList = data.get(e.nextElement());
    			double percentageValue = positionFrequencyArrayList.get(x).getPercentageValue();
    			if(percentageValue > 0)//log0 = math error and also an event with prob zero does not contribute to the entropy    				    		
    				positionSum += (percentageValue * Math.log10(percentageValue) / Math.log10(data.size()));    			
    		}    		   	    		
    		this.entropy.add(-1 * positionSum);
    		if((-1 * positionSum) <= threshold)
    			positionsToConsider.set(x);
    	}    
    	if(useEntropyAsWeights){
    		for(int x = 0; x < this.entropy.size(); x++){
    			this.weights.add(1 - this.entropy.get(x));
    		}
    	}else{
    		for(int x = 0; x < this.entropy.size(); x++){
    			this.weights.add(1.0);
    		}
    	}
    }
    
    public double getWeightsAt(int index){
    	return this.weights.get(index);
    }
}