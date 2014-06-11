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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import sirius.trainer.main.SiriusSettings;

public class CorrelationPane extends JComponent implements ActionListener{
	static final long serialVersionUID = sirius.Sirius.version;
	
	private JTextField filenameTextField = new JTextField(15);
	private JButton filenameButton = new JButton("Load");
	private JButton computeButton = new JButton("Compute");	
	private JTable table = new JTable();
	
	public CorrelationPane(){
		this.filenameTextField.setEnabled(false);
		this.setLayout(new BorderLayout(5,5));
		
		JPanel northPanel = new JPanel();	
		northPanel.add(new JLabel("Input File: "));
		northPanel.add(this.filenameTextField);
		northPanel.add(this.filenameButton);
		//northPanel.add(this.computeButton);
		this.filenameButton.addActionListener(this);
		this.computeButton.addActionListener(this);
				
		JScrollPane scrollPane = new JScrollPane(this.table);
		scrollPane.setBorder(BorderFactory.createCompoundBorder(
		    	BorderFactory.createTitledBorder("Scores"),
		    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		
		this.add(northPanel, BorderLayout.NORTH);
		this.add(scrollPane, BorderLayout.CENTER);
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource().equals(this.filenameButton))
			loadFile();
		else if(ae.getSource().equals(this.computeButton))
			compute();
	}
	
	private void loadFile(){
		JFileChooser fc;	    	
		//if working directory not set then look at the Sirius Settings file
		String lastLocation = SiriusSettings.getInformation("LastFileLocation: ");
		if(lastLocation == null)
			fc = new JFileChooser();
		else
			fc = new JFileChooser(lastLocation);	    				    
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Tab or Comma Seperated Files", "tsv", "csv");
        fc.setFileFilter(filter);			        			        			    	
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			final File file = fc.getSelectedFile();
            SiriusSettings.updateInformation("LastFileLocation: ", file.getAbsolutePath());  
            String seperator = "\t";
            if(file.getAbsolutePath().indexOf(".csv") != -1)
            	seperator = ",";
            this.filenameTextField.setText(file.getAbsolutePath());
            try{            			                	
            	this.table.setModel(new CorrelationTableModel(file, seperator));
            	this.revalidate();
            	this.repaint();
            }catch(Exception e){
            	JOptionPane.showMessageDialog(null,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            	e.printStackTrace();
            } 	            
		}
	}
	
	private void compute(){
		
	}

}
