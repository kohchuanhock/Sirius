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
package sirius.misc.zscore;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import sirius.MessageDialog;
import sirius.trainer.features.gui.correlation.PearsonCorrelationDialog;
import sirius.trainer.features.gui.correlation.SiriusCorrelationDialog;
import sirius.trainer.main.SiriusSettings;
import weka.core.Instances;

public class ZscorePane extends JComponent implements ActionListener, MouseListener{
	static final long serialVersionUID = sirius.Sirius.version;
	private JTextField posFilenameTextField = new JTextField();
	private JTextField negFilenameTextField = new JTextField();
	private JLabel posDataLabel = new JLabel("\t");
	private JLabel negDataLabel = new JLabel("\t");
	private JLabel numOfAttributeLabel = new JLabel("\t");
	private JInternalFrame parent;
	private ZscoreTableModel zscoreTableModel = new ZscoreTableModel(this.numOfAttributeLabel);
	private JButton computeScoreButton = new JButton("Compute");
	private Instances posInstances;
	private Instances negInstances;
	private JComboBox sortByComboBox = new JComboBox();
	private JButton sortButton = new JButton("Sort");
	private JTextField topXTextField = new JTextField("-1",3);
	private JButton saveButton = new JButton("Save");
	private JButton filteringOptionsButton = new JButton("Options");
	private JButton filteringApplyButton = new JButton("Apply");
	private JButton filteringResetButton = new JButton("Reset");
	private SiriusCorrelationDialog siriusDialog;
	private PearsonCorrelationDialog pearsonDialog;
	private JComboBox filteringComboBox = new JComboBox();
	
	public ZscorePane(JInternalFrame parent){
		this.parent = parent;
		setLayout(new BorderLayout());
		initNorthPanel();
		initCenterPanel();
		this.siriusDialog = new SiriusCorrelationDialog(parent);
		this.pearsonDialog = new PearsonCorrelationDialog(parent);
	}
	
	private void initCenterPanel(){
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBorder(BorderFactory.createCompoundBorder(
		    	BorderFactory.createTitledBorder(""),
		    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));		
		JPanel informationPanel = new JPanel(new GridLayout(1,3,5,5));			
		JPanel firstPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		firstPanel.setBorder(BorderFactory.createCompoundBorder(
		    	BorderFactory.createTitledBorder("Information"),
		    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));	
		JPanel secondPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		secondPanel.setBorder(BorderFactory.createCompoundBorder(
		    	BorderFactory.createTitledBorder("Sorting"),
		    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));	
		JPanel thirdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		thirdPanel.setBorder(BorderFactory.createCompoundBorder(
		    	BorderFactory.createTitledBorder("Filtering"),
		    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		JPanel fourthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		fourthPanel.setBorder(BorderFactory.createCompoundBorder(
		    	BorderFactory.createTitledBorder("Saving"),
		    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));	
		informationPanel.add(firstPanel);
		informationPanel.add(secondPanel);
		informationPanel.add(thirdPanel);
		informationPanel.add(fourthPanel);
		firstPanel.add(new JLabel("(+ve) Instances: "));
		firstPanel.add(this.posDataLabel);
		firstPanel.add(new JLabel("(-ve) Instances: "));
		firstPanel.add(this.negDataLabel);
		firstPanel.add(new JLabel("# of Attributes: "));
		firstPanel.add(this.numOfAttributeLabel);
		secondPanel.add(new JLabel("Sort By: "));
		secondPanel.add(this.sortByComboBox);
		secondPanel.add(this.sortButton);	
		thirdPanel.add(this.filteringComboBox);
		thirdPanel.add(this.filteringOptionsButton);
		thirdPanel.add(this.filteringApplyButton);
		thirdPanel.add(this.filteringResetButton);
		fourthPanel.add(new JLabel("Save Top X Attributes: "));
		fourthPanel.add(this.topXTextField);
		fourthPanel.add(this.saveButton);
		this.filteringComboBox.addItem("Sirius");
		this.filteringComboBox.addItem("Pearson");
		this.filteringComboBox.setSelectedIndex(1);
		this.filteringApplyButton.addActionListener(this);
		this.filteringOptionsButton.addActionListener(this);
		this.filteringResetButton.addActionListener(this);
		this.saveButton.addActionListener(this);
		this.sortButton.addActionListener(this);				
		this.sortByComboBox.addItem(this.zscoreTableModel.getColumnName(1));
		this.sortByComboBox.addItem(this.zscoreTableModel.getColumnName(6));
		this.sortByComboBox.addItem(this.zscoreTableModel.getColumnName(7));
		this.sortByComboBox.addItem(this.zscoreTableModel.getColumnName(8));
		this.sortByComboBox.addItem(this.zscoreTableModel.getColumnName(9));
		this.sortByComboBox.addItem(this.zscoreTableModel.getColumnName(10));
		this.sortByComboBox.addItem(this.zscoreTableModel.getColumnName(11));
		centerPanel.add(informationPanel,BorderLayout.NORTH);
				
		JTable zscoreTable = new JTable(this.zscoreTableModel);
		zscoreTable.getColumnModel().getColumn(0).setMaxWidth(80);
		JScrollPane scrollPane = new JScrollPane(zscoreTable);
		scrollPane.setBorder(BorderFactory.createCompoundBorder(
		    	BorderFactory.createTitledBorder("Scores"),
		    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		centerPanel.add(scrollPane,BorderLayout.CENTER);
		add(centerPanel, BorderLayout.CENTER);
	}
	
	private void initNorthPanel(){
		JPanel northPanel = new JPanel(new BorderLayout());
		JPanel filenamePanel = new JPanel(new GridLayout(1,2,5,5));
		JPanel posFilenamePanel = new JPanel(new GridLayout(1,1,5,5));
		posFilenamePanel.setBorder(BorderFactory.createCompoundBorder(
		    	BorderFactory.createTitledBorder("+ve File Location"),
		    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		posFilenamePanel.add(this.posFilenameTextField);
		this.posFilenameTextField.setEnabled(false);
		this.posFilenameTextField.addMouseListener(this);
		JPanel negFilenamePanel = new JPanel(new GridLayout(1,1,5,5));
		negFilenamePanel.setBorder(BorderFactory.createCompoundBorder(
		    	BorderFactory.createTitledBorder("-ve File Location"),
		    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));	
		negFilenamePanel.add(this.negFilenameTextField);
		this.negFilenameTextField.setEnabled(false);
		this.negFilenameTextField.addMouseListener(this);
		filenamePanel.add(posFilenamePanel);
		filenamePanel.add(negFilenamePanel);
		northPanel.add(filenamePanel,BorderLayout.CENTER);
		northPanel.add(this.computeScoreButton,BorderLayout.EAST);
		this.computeScoreButton.addActionListener(this);
		add(northPanel, BorderLayout.NORTH);
	}

	private void compute(){
		this.zscoreTableModel.compute(this.posInstances, this.negInstances);
	}
	
	private String saveFileDialog(){
		JFileChooser fc;	    	
		//if working directory not set then look at the Sirius Settings file
		String lastLocation = SiriusSettings.getInformation("LastZScoreFileLocation: ");
		if(lastLocation == null)
			fc = new JFileChooser();
		else
			fc = new JFileChooser(lastLocation);	    				    
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Features Files", "features");
        fc.setFileFilter(filter);        
		int returnVal = fc.showSaveDialog(parent);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();			           
			String savingFilename = file.getAbsolutePath();
			if(savingFilename.indexOf(".features") == -1)
				savingFilename += ".features";
			SiriusSettings.updateInformation("LastZScoreFileLocation: ", savingFilename);
			return savingFilename;
		}else
			return null;
	}
	
	private void save(){
		try{
			int topX = Integer.parseInt(this.topXTextField.getText());
			String fileLocation = saveFileDialog();
			if(topX < -1)
				throw new NumberFormatException();
			if(fileLocation != null)
				if(this.zscoreTableModel.save(fileLocation,topX))
					JOptionPane.showMessageDialog(this.parent, "Features saved!", "Done", JOptionPane.INFORMATION_MESSAGE);
				else
					JOptionPane.showMessageDialog(this.parent, "Unable to save file!", "Error", JOptionPane.ERROR_MESSAGE);
		}catch(NumberFormatException e){
			JOptionPane.showMessageDialog(this.parent, "Invalid top x value. Please enter integers", "Invalid Input", 
				JOptionPane.ERROR_MESSAGE);
			this.topXTextField.requestFocus();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {		
		if(ae.getSource().equals(this.computeScoreButton)){
			compute();
		}else if(ae.getSource().equals(this.sortButton)){
			this.zscoreTableModel.sort(this.sortByComboBox.getSelectedIndex());
		}else if(ae.getSource().equals(this.saveButton)){
			save();
		}else if(ae.getSource().equals(this.filteringOptionsButton)){
			showOptionsDialog();
		}else if(ae.getSource().equals(this.filteringApplyButton)){
			apply();
		}else if(ae.getSource().equals(this.filteringResetButton)){
			this.zscoreTableModel.reset();
		}
	}
		
	
	private void showOptionsDialog(){
		if(this.filteringComboBox.getSelectedIndex() == 0)
			this.siriusDialog.setVisible(true);
		else 
			this.pearsonDialog.setVisible(true);
	}
	
	private void apply(){
		if(this.filteringComboBox.getSelectedIndex() == 0)
			this.zscoreTableModel.siriusCorrelationFiltering(this.siriusDialog.getStdDevDistance(),this.siriusDialog.getOverlapPercent(), this.siriusDialog.isNegativesSelected());
		else
			this.zscoreTableModel.pearsonCorrelationFiltering(this.pearsonDialog.getMaxPearsonScore(), this.siriusDialog.isNegativesSelected());
	}

	private void loadArffFile(boolean pos,File file){
		try{
			if(pos){
				this.posInstances = new Instances(new java.io.BufferedReader(new java.io.FileReader(file)));
				this.zscoreTableModel.setOriginalPosInstances(new Instances(this.posInstances), pos);
				this.posDataLabel.setText(this.posInstances.numInstances()+"     ");
				this.numOfAttributeLabel.setText(this.posInstances.numAttributes()+"     ");
			}else{
				this.negInstances = new Instances(new java.io.BufferedReader(new java.io.FileReader(file)));
				this.zscoreTableModel.setOriginalPosInstances(new Instances(this.negInstances), pos);
				this.negDataLabel.setText(this.negInstances.numInstances()+"     ");
				this.numOfAttributeLabel.setText(this.negInstances.numAttributes()+"     ");
			}
			if(this.posInstances != null && this.negInstances != null && this.posInstances.numAttributes() != this.negInstances.numAttributes())
				this.numOfAttributeLabel.setText("Error! (+ve) attributes and (-ve) attributes size do not match!");
		}catch(Exception e){
			JOptionPane.showMessageDialog(this.parent, "Error reading " + file.getAbsolutePath(), "I/O Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void openFile(JTextField textField, final boolean pos){		
		JFileChooser fc;	    	
		//if working directory not set then look at the Sirius Settings file
		String lastLocation = SiriusSettings.getInformation("LastZScoreFileLocation: ");
		if(lastLocation == null)
			fc = new JFileChooser();
		else
			fc = new JFileChooser(lastLocation);	    				    
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Arff Files", "arff");
        fc.setFileFilter(filter);			        			        			    	
		int returnVal = fc.showOpenDialog(parent);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			final File file = fc.getSelectedFile();
            SiriusSettings.updateInformation("LastZScoreFileLocation: ", file.getAbsolutePath());
            textField.setText(file.getAbsolutePath());
            try{
            	Thread oneThread = new Thread(){	      	
    				public void run(){	
	    					MessageDialog m = new MessageDialog(ZscorePane.this, "Loading", "Loading File.. Please wait.."); 	    					
	    	            	loadArffFile(pos,file);	    	            	
	    	            	m.dispose();
    					}};
	      		oneThread.setPriority(Thread.MIN_PRIORITY); // UI has most priority
	      		oneThread.start();    			    	
            }catch(Exception e){
            	JOptionPane.showMessageDialog(null,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            	e.printStackTrace();
            } 	            
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent me) {
		if(me.getSource().equals(this.negFilenameTextField)){			
			openFile(this.negFilenameTextField, false);
		}else if(me.getSource().equals(this.posFilenameTextField)){
			openFile(this.posFilenameTextField, true);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}
}