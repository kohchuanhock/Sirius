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
package sirius.trainer.features.gui.basicphysiochemical;

import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import sirius.nnsearcher.main.DefineConstraintsDialog;
import sirius.nnsearcher.main.MustHaveTableModel;
import sirius.trainer.features.BasicPhysiochemicalFeature;
import sirius.trainer.step2.FeatureTableModel;

public class BasicPhysioTableModel extends AbstractTableModel{
	static final long serialVersionUID = sirius.Sirius.version;
	private String[] columnNames;
	private ArrayList<BasicPhysiochemicalFeature> data;
	
	public static enum BasicPhysiochemicalType{Length, Mass, pIValue, NetCharge, MeanNetCharge, 
		AbsoluteNetCharge, AbsoluteMeanNetCharge, HydrophobicityTotal, HydrophobicityMean, 
		OrderAminoAcidTotal, OrderAminoAcidMean, DisorderAminoAcidTotal, DisorderAminoAcidMean,
		OrderDisorderAADifferenceTotal, OrderDisorderAADifferenceMean};
	
	public BasicPhysioTableModel(){		
		
		columnNames = new String[3];
		columnNames[0] = " ";
		columnNames[1] = "Feature Name";
		columnNames[2] = "Feature Details";				
		
		data = new ArrayList<BasicPhysiochemicalFeature>();
		data.add(new BasicPhysiochemicalFeature('B', "B_Length", "Number of Amino Acid Residues",true));
		data.add(new BasicPhysiochemicalFeature('B', "B_Mass", "Molecular Mass", true));
		data.add(new BasicPhysiochemicalFeature('B', "B_pIValue", "Theoretical pI Value", true));
		data.add(new BasicPhysiochemicalFeature('B', "B_NetCharge", "Net Charge", false));
		data.add(new BasicPhysiochemicalFeature('B', "B_MeanNetCharge", "Net Charge / Number of Amino Acid Residues", true));
		data.add(new BasicPhysiochemicalFeature('B', "B_AbsoluteNetCharge", "Absolute Net Charge", false));
		data.add(new BasicPhysiochemicalFeature('B', "B_AbsoluteMeanNetCharge", "Absolute Mean Net Charge", false));
		data.add(new BasicPhysiochemicalFeature('B', "B_HydrophobicityTotal", "Hydrophobicity of sequence based on Kyte and Doolittle approximation (Normalized)", false));
		data.add(new BasicPhysiochemicalFeature('B', "B_HydrophobicityMean", "HydrophobicityTotal / Number of Amino Acid Residues", true));
		data.add(new BasicPhysiochemicalFeature('B', "B_OrderAminoAcidTotal", "Order AA: {N, C, I, L, F, W, Y, V}", false));
		data.add(new BasicPhysiochemicalFeature('B', "B_OrderAminoAcidMean", "Order_Amino_Acid_Total / Number of Amino Acid Residues", true));
		data.add(new BasicPhysiochemicalFeature('B', "B_DisorderAminoAcidTotal", "Disorder AA: {A, R, Q, E, G, K, P, S}", false));
		data.add(new BasicPhysiochemicalFeature('B', "B_DisorderAminoAcidMean", "Disorder_Amino_Acid_Total / Number of Amino Acid Residues", true));
		data.add(new BasicPhysiochemicalFeature('B', "B_OrderDisorderAADifferenceTotal", "#OrderAA - #DisorderAA", false));
		data.add(new BasicPhysiochemicalFeature('B', "B_OrderDisorderAADifferenceMean", "(#OrderAA - #DisorderAA) / Number of Amino Acid Residues", true));
	}
	
	public Class<?> getColumnClass(int c) {
    	return getValueAt(0, c).getClass();
    }
	
	public int getColumnCount() {
        return columnNames.length;
    }
    
    public int getRowCount() {    
    	if(data!=null)
        	return data.size();       
        else 
        	return -1;
    }       
    
    public boolean isCellEditable(int row,int column){    	
    	if(column == 0)
    		return true;
    	else 
    		return false;
    }
    
    public void setValueAt(Object value, int row, int col) {
        if(col == 0){
        	data.get(row).invertBox();        	
        }
        fireTableCellUpdated(row, col);
    }
    
    public String getColumnName(int col) {
        return columnNames[col];
    } 
    
    public Object getValueAt(int row, int col) {		
    	if(data!=null){    		   	
			switch(col){    			
			case 0: return data.get(row).isMarked();
			case 1: return data.get(row).getName();    			
			case 2: return data.get(row).getDetails();			
			//should not reach default
			default: return "-1";
			}        		
    	}    		
    	else
    		return " ";
    }           
    
    public void generateFeatures(FeatureTableModel model, boolean isLocal, int windowFrom, int windowTo, JDialog parent,
    		MustHaveTableModel constraintsModel,boolean isPercentage){
    	if(model != null){
	    	for(int x = 0; x < data.size(); x++){
	    		if(data.get(x).isMarked()){
	    			BasicPhysiochemicalFeature temp = new BasicPhysiochemicalFeature(data.get(x));
	    			temp.setBTypeWindow(isLocal, windowFrom, windowTo, isPercentage);    			    			
	    			model.add(temp);
	    		}
	    	}
	    	JOptionPane.showMessageDialog(parent,"New Features Added!","Information",JOptionPane.INFORMATION_MESSAGE);
    	}else{    		
    		for(int x = 0; x < data.size(); x++){
	    		if(data.get(x).isMarked()){	    			
	    			BasicPhysiochemicalFeature temp = new BasicPhysiochemicalFeature(data.get(x));
	    			temp.setBTypeWindow(isLocal, windowFrom, windowTo, isPercentage);    			    			
	    			DefineConstraintsDialog dialog = new DefineConstraintsDialog(temp, constraintsModel);
		    		dialog.setLocationRelativeTo(parent);
		    		dialog.setVisible(true);    			
	    		}
	    	}
    	}
    }
    
    public void markAll(){
    	for(int x = 0; x < data.size(); x++){
    		data.get(x).setBox(true);
    	}
    	fireTableDataChanged();
    }
    
    public void unmarkAll(){
    	for(int x = 0; x < data.size(); x++){
    		data.get(x).setBox(false);
    	}
    	fireTableDataChanged();
    }
}
