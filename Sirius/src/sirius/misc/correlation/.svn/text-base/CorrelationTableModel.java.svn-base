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
package sirius.misc.correlation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class CorrelationTableModel extends AbstractTableModel{
	static final long serialVersionUID = sirius.Sirius.version;
	private List<List<Double>> inputListList = null;
	private List<Double> meanList;
	private List<Double> stddevList;
	private Hashtable<String, Double> correlationHashtable;
	
	public CorrelationTableModel(File file, String seperator) throws Exception{
		BufferedReader input = new BufferedReader(new FileReader(file));
		String line;
		this.inputListList = null;
		this.meanList = new ArrayList<Double>();
		this.stddevList = new ArrayList<Double>();
		this.correlationHashtable = new Hashtable<String, Double>();
		while((line = input.readLine()) != null){
			StringTokenizer st = new StringTokenizer(line, seperator);
			if(inputListList == null){
				int numCol = st.countTokens();
				inputListList = new ArrayList<List<Double>>();
				for(int x = 0; x < numCol; x++)
					inputListList.add(new ArrayList<Double>());
			}
			int index = 0;
			while(st.hasMoreTokens())
				inputListList.get(index++).add(Double.parseDouble(st.nextToken()));
		}
		input.close();		
		//Compute the mean and stddev
		for(int x = 0; x < this.inputListList.size(); x++){
			double total = 0.0;
			for(int y = 0; y < this.inputListList.get(x).size(); y++)
				total += this.inputListList.get(x).get(y);			
			double mean = total / this.inputListList.get(x).size(); 
			this.meanList.add(mean);
			double deviation = 0.0;
			for(int y = 0; y < this.inputListList.get(x).size(); y++)
				deviation += Math.pow(this.inputListList.get(x).get(y) - mean, 2);
			double stddev = Math.sqrt(deviation / (this.inputListList.get(x).size() - 1));
			this.stddevList.add(stddev);
		}		
		//compute the correlation
		for(int x = 0; x < this.inputListList.size(); x++){
			for(int y = 0; y < this.inputListList.size(); y++){
				double XY = 0.0;
				double X = 0.0;
				double Y = 0.0;
				double Xsq = 0.0;
				double Ysq = 0.0;
				for(int z = 0; z < this.inputListList.get(x).size(); z++){
					XY += (this.inputListList.get(x).get(z) * this.inputListList.get(y).get(z));
					X += this.inputListList.get(x).get(z);
					Y += this.inputListList.get(y).get(z);
					Xsq += Math.pow(this.inputListList.get(x).get(z),2);
					Ysq += Math.pow(this.inputListList.get(y).get(z),2);
				}
				double numOfRow = this.inputListList.get(x).size();
				double pearsonCorrelation = (XY - (X * Y / numOfRow)) / 
					Math.sqrt((Xsq - (Math.pow(X, 2) / numOfRow)) * (Ysq - (Math.pow(Y, 2) / numOfRow)));
				this.correlationHashtable.put(x + "," + y, pearsonCorrelation);
			}
		}
	}
	
	public String getColumnName(int col) {
		if(col == 0)
			return "";
        return col + "";
    }
	
	@Override
	public int getColumnCount() {		
		return this.inputListList.size() + 1;//+1 for label
	}

	@Override
	public int getRowCount() {		
		return this.inputListList.size() + 2;//+2 for Std Dev and Mean
	}

	@Override
	public Object getValueAt(int row, int col) {
		if(col == 0){
			if(row == 0)
				return "Mean";
			else if(row == 1)
				return "StdDev";
			else 
				return row-1;
		}			
		col--;
		if(row == 0)//mean
			return this.meanList.get(col);
		else if(row == 1)//stddev
			return this.stddevList.get(col);
		else
			return this.correlationHashtable.get((row - 2) + "," + col);		
	}

}
