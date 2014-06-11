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
package sirius.clustering.main;


import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import sirius.utils.ClassifierResults;
import sirius.utils.PredictionStats;


public class ResultsTableModel extends AbstractTableModel{
	static final long serialVersionUID = sirius.Sirius.version;
	private ArrayList<ClassifierResults> data;
	private ArrayList<PredictionStats> statsData;
	private ArrayList<String> clusterList;
	private String[] columnNames;
	
	public ResultsTableModel(){
		this.data = new ArrayList<ClassifierResults>();	
		this.statsData = new ArrayList<PredictionStats>();
		this.clusterList = new ArrayList<String>();
		this.columnNames = new String[1];
		this.columnNames[0] = "Result List";
	}
	
	@Override
	public int getColumnCount() {		
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		if(this.clusterList!=null)
        	return this.clusterList.size();       
        else 
        	return -1;
	}

	@Override
	public Object getValueAt(int row, int col) {
		if(clusterList != null){
			return clusterList.get(row);
		}else
			return " ";		
	}
	
	public String getColumnName(int col) {
        return columnNames[col];
    } 
	
	public void reset(){
		this.data = new ArrayList<ClassifierResults>();
		this.clusterList = new ArrayList<String>();
		this.statsData = new ArrayList<PredictionStats>();
		fireTableRowsDeleted(0,0);
	}
	
	public void add(String name,ClassifierResults cr, PredictionStats stats){
		if(cr != null)
			this.data.add(cr);
		this.clusterList.add(name);
		if(stats != null)
		this.statsData.add(stats);
		fireTableRowsInserted(0,0); 
	}
			
	public ClassifierResults getClassifierResults(int index){
		if(this.data != null)
			return data.get(index);
		else
			return null;
	}
	
	public PredictionStats getStats(int index){
		if(this.statsData != null)
			return this.statsData.get(index);
		else 
			return null;
	}
	
	public ArrayList<ClassifierResults> getCRArrayList(){
		return this.data;
	}
	
	public ArrayList<PredictionStats> getStatArrayList(){
		return this.statsData;
	}
	
	public void update(ClassifierResults cr, PredictionStats stats, int index){
		//this.data.remove(index);
		this.data.set(index, cr);
		//this.statsData.remove(index);
		this.statsData.set(index, stats);
	}
}
