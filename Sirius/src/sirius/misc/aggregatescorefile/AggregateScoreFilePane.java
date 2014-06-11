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
package sirius.misc.aggregatescorefile;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import sirius.trainer.main.SiriusSettings;


public class AggregateScoreFilePane extends JComponent implements ActionListener{
	static final long serialVersionUID = sirius.Sirius.version;
	private GridLayout gridLayout = new GridLayout(1,1,5,5);
	private List<JPanel> panelList = new ArrayList<JPanel>();
	private JPanel centerPanel = new JPanel(this.gridLayout);
	private List<JTextField> locationFieldList = new ArrayList<JTextField>();
	private List<JButton> browseButtonList = new ArrayList<JButton>();
	private JButton addScoreFileButton = new JButton("Add Score File");
	private JButton deleteScoreFileButton = new JButton("Delete Score File");
	private JButton averageButton = new JButton("Average");
	private JButton combineButton = new JButton("Combine");
	private JTextField outputLocationTextField = new JTextField(35);
	private JButton outputBrowseButton = new JButton("Browse");
	
	public AggregateScoreFilePane(){
		this.setLayout(new BorderLayout(5,5));
		this.addScoreFileButton.addActionListener(this);
		this.deleteScoreFileButton.addActionListener(this);
		this.averageButton.addActionListener(this);
		this.combineButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(this.addScoreFileButton);
		buttonPanel.add(this.deleteScoreFileButton);
		buttonPanel.add(this.averageButton);
		buttonPanel.add(this.combineButton);
		JPanel outputPanel = new JPanel();
		outputPanel.add(new JLabel("Output Location: "));
		outputPanel.add(this.outputLocationTextField);
		this.outputLocationTextField.setEnabled(false);
		outputPanel.add(this.outputBrowseButton);
		this.outputBrowseButton.addActionListener(this);
		JPanel southPanel = new JPanel(new GridLayout(2,1,5,5));
		southPanel.add(outputPanel);
		southPanel.add(buttonPanel);
		
		for(int x = 0; x < 10; x++)
			this.addScoreFile();
		
		this.add(southPanel, BorderLayout.SOUTH);
		this.add(this.centerPanel, BorderLayout.CENTER);	
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {		
		if(ae.getSource().equals(this.addScoreFileButton))
			this.addScoreFile();
		else if(ae.getSource().equals(this.deleteScoreFileButton))
			this.deleteScoreFile();
		else if(ae.getSource().equals(this.averageButton))
			aggregate();	
		else if(ae.getSource().equals(this.combineButton))
			combine();
		else if(ae.getSource().equals(this.outputBrowseButton))
			setLocation(-1);
		else{
			for(int x = 0; x < this.browseButtonList.size(); x++)
				if(ae.getSource().equals(this.browseButtonList.get(x)))
					setLocation(x);
		}
	}
	
	private void combine(){
		/*
		 * Put all into one single file
		 */
		try{
			if(this.outputLocationTextField.getText().length() == 0)
				return;		
			List<BufferedReader> inputList = new ArrayList<BufferedReader>();
			for(int x = 0; x < this.locationFieldList.size(); x++)
				if(this.locationFieldList.get(x).getText().length() > 0)
					inputList.add(new BufferedReader(new FileReader(this.locationFieldList.get(x).getText())));
			if(inputList.size() == 0)
				return;			
			BufferedWriter output = new BufferedWriter(new FileWriter(this.outputLocationTextField.getText()));
			String line;
			for(int x = 0; x < inputList.size(); x++){
				while((line = inputList.get(x).readLine()) != null){
					output.write(line);
					output.newLine();
				}
				
			}
			for(int x = 0; x < inputList.size(); x++)
				inputList.get(x).close();
			output.close();					
			JOptionPane.showMessageDialog(null, "Done!");
		}catch(Exception e){e.printStackTrace();}
	}
	
	private void aggregate(){
		/*
		 * Average the score from all files
		 */
		try{
			if(this.outputLocationTextField.getText().length() == 0)
				return;		
			List<BufferedReader> inputList = new ArrayList<BufferedReader>();
			for(int x = 0; x < this.locationFieldList.size(); x++)
				if(this.locationFieldList.get(x).getText().length() > 0)
					inputList.add(new BufferedReader(new FileReader(this.locationFieldList.get(x).getText())));
			if(inputList.size() == 0)
				return;
			//Here I assume that all the files are of same order
			BufferedWriter output = new BufferedWriter(new FileWriter(this.outputLocationTextField.getText()));
			String line;
			while((line = inputList.get(0).readLine()) != null){
				output.write(line);
				output.newLine();
				output.write(inputList.get(0).readLine());
				output.newLine();
				double totalScore = 0.0;
				for(int x = 0; x < inputList.size(); x++){
					StringTokenizer st;
					if(x == 0){
						st = new StringTokenizer(inputList.get(x).readLine(), "=");
						output.write(st.nextToken());					
					}else{
						inputList.get(x).readLine();
						inputList.get(x).readLine();
						st = new StringTokenizer(inputList.get(x).readLine(), "=");
						st.nextToken();
					}
					totalScore += Double.parseDouble(st.nextToken());
				}
				DecimalFormat df = new DecimalFormat("0.###");
				output.write("=" + df.format(totalScore / inputList.size()));
				output.newLine();
			}
			for(int x = 0; x < inputList.size(); x++)
				inputList.get(x).close();
			output.close();
			JOptionPane.showMessageDialog(null, "Done!");
		}catch(Exception e){e.printStackTrace();}
	}
	
	private void setLocation(int index){
		try{
			//load file
			//applicationData.hasLocationIndexBeenSet = false;
			JFileChooser fc;		    	
    		//if working directory not set then look at the Sirius Settings file
    		String lastLocation = 
    			SiriusSettings.getInformation("LastFileLocation: ");
    		if(lastLocation == null)
    			fc = new JFileChooser();
    		else
    			fc = new JFileChooser(lastLocation);		    	    
	        FileNameExtensionFilter filter = new FileNameExtensionFilter(
	            "Score Files", "scores");
	        fc.setFileFilter(filter);			        			        			    	
			int returnVal = fc.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();	
				SiriusSettings.updateInformation("LastFileLocation: ", 
						file.getAbsolutePath());
				SiriusSettings.updateInformation("LastSaveFileLocation: ", 
						file.getAbsolutePath());
				if(index == -1)
					this.outputLocationTextField.setText(file.getAbsolutePath());
				else
					this.locationFieldList.get(index).setText(file.getAbsolutePath());
			}			
		}catch(Exception e){e.printStackTrace();}
	}
	
	private void deleteScoreFile(){
		this.centerPanel.remove(this.panelList.get(this.panelList.size() - 1));
		this.panelList.remove(this.panelList.size() - 1);
		this.locationFieldList.remove(this.locationFieldList.size() - 1);
		this.browseButtonList.remove(this.browseButtonList.size() - 1);
		this.gridLayout.setRows(this.gridLayout.getRows() - 1);		
		this.repaint();
	}
	
	private void addScoreFile(){
		JPanel panel = new JPanel();
		JButton button = new JButton("Browse");
		button.addActionListener(this);
		JTextField textField = new JTextField(35);
		textField.setEnabled(false);
		panel.add(new JLabel("Score File " + (this.panelList.size() + ": ")));
		panel.add(textField, BorderLayout.CENTER);
		panel.add(button, BorderLayout.EAST);
		this.gridLayout.setRows(this.gridLayout.getRows() + 1);
		this.centerPanel.add(panel);
		this.locationFieldList.add(textField);
		this.browseButtonList.add(button);
		
		this.panelList.add(panel);		
		this.repaint();
	}

}
