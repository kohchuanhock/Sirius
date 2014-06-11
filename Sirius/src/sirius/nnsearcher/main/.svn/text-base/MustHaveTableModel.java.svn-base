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


import java.awt.GridLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;

import sirius.trainer.features.Feature;
import sirius.trainer.main.SiriusSettings;

public class MustHaveTableModel extends AbstractTableModel{
	static final long serialVersionUID = sirius.Sirius.version;
	String[] columnNames;
	ArrayList<sirius.nnsearcher.main.Constraints> data;
	JTable table;
	JFrame mainFrame;
	
	public MustHaveTableModel(){
		this.columnNames = new String[2];
		this.columnNames[0] = "No.";
		this.columnNames[1] = "Must Have Constraints";
		this.data = new ArrayList<sirius.nnsearcher.main.Constraints>();
	}
	
	public ArrayList<sirius.nnsearcher.main.Constraints> getData(){
		return this.data;
	}
	
	public void setFrame(JFrame mainFrame){
		this.mainFrame = mainFrame;		
	}
	
	public void setTable(JTable table){
		this.table = table;
	}
	
	public int getColumnCount() {		
		return this.columnNames.length;
	}	

	
	public int getRowCount() {		
		return this.data.size();
	}

	
	public Object getValueAt(int row, int col) {		
		if(this.data!=null){    		
    		switch(col){
    			case 0: return "" + (row + 1);
    			case 1: return this.data.get(row).display();
    			//should not reach default
    			default: return "-1";
    		}    		    		
    	}    		
    	else
    		return " ";
	}
	
	public String getColumnName(int col) {
        return this.columnNames[col];
    } 
		
	public void add(Feature feature, int operator, double value){
		this.data.add(new sirius.nnsearcher.main.Constraints(feature,operator,value));		
		fireTableRowsInserted(getRowCount(),getRowCount());
	}
	
	public void delete(int index){
		this.data.remove(index);
		fireTableRowsDeleted(index,index);
	}
	
	public void showConstraint(int index){		
		ShowConstraintsDialog dialog = new ShowConstraintsDialog(this.data.get(index).display());
		dialog.setLocationRelativeTo(null);    		
		dialog.setVisible(true);	
	}
	
	public boolean loadConstraints(){
		String lastConstraintsFileLocation = SiriusSettings.getInformation("LastConstraintsFileLocation: ");
    	JFileChooser fc = new JFileChooser(lastConstraintsFileLocation);
    	FileNameExtensionFilter filter = new FileNameExtensionFilter(
	            "Constraints Files", "constraints");
	    fc.setFileFilter(filter);	
		int returnVal = fc.showOpenDialog(this.mainFrame);		
		if (returnVal == JFileChooser.APPROVE_OPTION) {	    
			try{
				File file = fc.getSelectedFile();
				SiriusSettings.updateInformation("LastConstraintsFileLocation: ", file.getAbsolutePath());
		    	FileInputStream fis = new FileInputStream(file);
		        ObjectInputStream ois = new ObjectInputStream(fis);
				
				int constraintsNum = ois.readInt();		  
				for(int x = 0; x < constraintsNum; x++){
					this.data.add((sirius.nnsearcher.main.Constraints)ois.readObject());
				}		        					
		        ois.close();		 
		        fireTableRowsInserted(getRowCount(),getRowCount());
		        return true;
			}
			catch(ClassCastException cce){
				JOptionPane.showMessageDialog(this.mainFrame,"Format of Input File is Incorrect",
	            		"ERROR",JOptionPane.ERROR_MESSAGE);
				return false;
			}
			catch(StreamCorruptedException sce){
				JOptionPane.showMessageDialog(this.mainFrame,"Incompatible File Format",
	            		"ERROR",JOptionPane.ERROR_MESSAGE);
				return false;
			}
			catch(Exception e){
				JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return false;
			}    										
		}else
			return false;
	}
	
	public void saveConstraints(){
		JFileChooser fc;				    	
		String lastConstraintsFileLocation = SiriusSettings.getInformation("LastConstraintsFileLocation: ");
    	fc = new JFileChooser(lastConstraintsFileLocation);
    	FileNameExtensionFilter filter = new FileNameExtensionFilter(
	            "Constraints Files", "constraints");
	    fc.setFileFilter(filter);	
		int returnVal = fc.showOpenDialog(this.mainFrame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();	                        	
        	String savingFilename = file.getAbsolutePath();
			if(savingFilename.indexOf(".constraints") == -1)
				savingFilename += ".constraints";			
			try{										        		        		        			        		       		        
				FileOutputStream fos1 = new FileOutputStream(savingFilename);
		        ObjectOutputStream oos1 = new ObjectOutputStream(fos1);		        
		        oos1.writeInt(this.data.size());
		        for(int x = 0; x < this.data.size(); x++){
		        	oos1.writeObject(this.data.get(x));
		        }		        			        					        
				oos1.close();					
				JOptionPane.showMessageDialog(null,"Constraints have been successfully saved~!","Successfully Saved",
						JOptionPane.INFORMATION_MESSAGE);
			}catch(Exception e){e.printStackTrace();}
		}
	}
}

class ShowConstraintsDialog extends JDialog{	
	static final long serialVersionUID = sirius.Sirius.version;
	
	public ShowConstraintsDialog(String display){		
		setLayout(new GridLayout(1,1));
		
		JLabel label = new JLabel("Constraint Details: " + display, SwingConstants.CENTER);
		add(label);		
		//setSize(640, 100);
		this.pack();
	}
}
