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

import javax.swing.table.*;
import javax.swing.*;

import sirius.trainer.features.Feature;

import java.util.*;

import weka.core.*;

public class FeaturesDetailTableModel extends AbstractTableModel{
	static final long serialVersionUID = sirius.Sirius.version;
	
	private String[] columnNames = {"No.","Name of Feature", "Details"};
	ArrayList<Feature> data;
	
    public FeaturesDetailTableModel(Instances instances) {
    	this.data = new ArrayList<Feature>();
    	for(int x = 0; x < instances.numAttributes() - 1; x++){
    		try{
    			Feature fData = Feature.levelOneClassifierPane(instances.attribute(x).name());
    			this.data.add(fData);
    		}catch(Exception e){
    			JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
    			e.printStackTrace();}
    	}
    }
    
    public String getColumnName(int col) {
        return columnNames[col];
    }
    
    public int getColumnCount(){
        return 3;//No. , Name of Feature, Details
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
    			return "" + (row + 1);
    		else if(col == 1)
    			return data.get(row).getName();
    		else//col == 2
    			return data.get(row).getDetails();
    	}    		
    	else
    		return " ";
    }
}