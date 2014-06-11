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
import java.util.*;

public class ClassifierTableModel extends AbstractTableModel{
	static final long serialVersionUID = sirius.Sirius.version;
	private String[] columnNames = {"Type","Classifier Name", "# Features"};
	ArrayList<ClassifierData> data;
	
    public ClassifierTableModel() {
    	data = new ArrayList<ClassifierData>();
    }
    
    public String getColumnName(int col) {
        return columnNames[col];
    }
    
    public int getColumnCount(){
        return 3;//Type, Classifier Name, # of Features
    }
    
    public int getRowCount() {    
    	if(data!=null)
        	return data.size();       
        else 
        	return -1;
    }       
    
    public Object getValueAt(int row, int col) {    
    	if(data!=null)
    		return data.get(row).get(col);    
    	else
    		return " ";
    }
    public void add(ClassifierData classifierData){
    	data.add(classifierData);
    	fireTableRowsInserted(getRowCount(),getRowCount());
    }
    public void remove(int index){    	
    	data.remove(index);
    	fireTableRowsDeleted(index,index);
    }
    public ClassifierData getIndexAt(int index){
    	return data.get(index);
    }
}