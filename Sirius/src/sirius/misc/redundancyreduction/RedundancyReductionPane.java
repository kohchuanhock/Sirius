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
package sirius.misc.redundancyreduction;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.StringTokenizer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;

import sirius.trainer.main.SiriusSettings;

import java.awt.*;

public class RedundancyReductionPane extends JComponent implements ActionListener, MouseListener{
	static final long serialVersionUID = sirius.Sirius.version;
	private JInternalFrame parent;
	//private JButton browseButton;
	private JTextField filenameTextField;
	//private JButton negBrowseButton;
	private JTextField negFilenameTextField;
	private JTextField identityTextField;
	private JTable sequenceNameTable;
	private JButton runButton;
	private JButton stopButton;
	private JButton deleteButton;
	private JButton saveButton;
	private JButton savePosButton;
	private JButton saveNegButton;
	private JScrollPane sequenceNameTableScrollPane;
	private RedundancyReductionModel sequenceSimilarityCheckModel;	
	private JTextField statusTextField;
	private JTextField matchTextField;
	private JTextField mismatchTextField;
	private JTextField indelTextField;
	private JCheckBox reduceWhileRunCheckBox;
	private JTextField posFeatureTextField;
	private JTextField negFeatureTextField;
	private JTextField totalFeatureTextField;
	
	public RedundancyReductionPane(JInternalFrame parent,JTabbedPane tabbedPane){
		this.parent = parent;		
		
		JPanel northWestPanel = new JPanel(new BorderLayout(5,5));
		northWestPanel.setBorder(BorderFactory.createCompoundBorder(
		    	BorderFactory.createTitledBorder("+ve File Location"),
		    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));	
		filenameTextField = new JTextField();
		filenameTextField.setEnabled(false);
		filenameTextField.addMouseListener(this);
		northWestPanel.add(filenameTextField);
		
		JPanel northCenterPanel = new JPanel(new BorderLayout(5,5));
		northCenterPanel.setBorder(BorderFactory.createCompoundBorder(
		    	BorderFactory.createTitledBorder("-ve File Location"),
		    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		negFilenameTextField = new JTextField();
		negFilenameTextField.setEnabled(false);
		negFilenameTextField.addMouseListener(this);
		northCenterPanel.add(negFilenameTextField);
		
		JPanel identityPanel = new JPanel();		
		identityPanel.setBorder(BorderFactory.createCompoundBorder(
		    	BorderFactory.createTitledBorder("Define Similar"),
		    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		JLabel identityLabel = new JLabel(">= (%): ");
		identityTextField = new JTextField(3);		
		runButton = new JButton("Run");
		runButton.addActionListener(this);
		stopButton = new JButton("Stop");
		stopButton.addActionListener(this);
		deleteButton = new JButton("Reduce");
		deleteButton.addActionListener(this);
		identityPanel.add(identityLabel);
		identityPanel.add(identityTextField);		
		identityPanel.add(runButton);	
		identityPanel.add(deleteButton);
		identityPanel.add(stopButton);		
		reduceWhileRunCheckBox = new JCheckBox("Reduce While Run", true);		
		identityPanel.add(reduceWhileRunCheckBox);
		
		JPanel northEast2Panel = new JPanel(new GridLayout(1,1));
		northEast2Panel.setBorder(BorderFactory.createCompoundBorder(
		    	BorderFactory.createTitledBorder("Status"),
		    		BorderFactory.createEmptyBorder(10, 15, 15, 15)));
		statusTextField = new JTextField();
		statusTextField.setEditable(false);
		statusTextField.setEnabled(false);
		statusTextField.setMinimumSize(new Dimension(20,20));
		statusTextField.setPreferredSize(new Dimension(150,20));
		statusTextField.setMaximumSize(new Dimension(200,20));
		northEast2Panel.add(statusTextField);		
		
		JPanel northEast3Panel = new JPanel(new GridLayout(1,3));
		northEast3Panel.setBorder(BorderFactory.createCompoundBorder(
		    	BorderFactory.createTitledBorder("Output"),
		    		BorderFactory.createEmptyBorder(0, 15, 5, 5)));
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		JPanel panel3 = new JPanel();
		savePosButton = new JButton("Save (+) Seqs");
		savePosButton.addActionListener(this);
		saveNegButton = new JButton("Save (-) Seqs");
		saveNegButton.addActionListener(this);
		saveButton = new JButton("Save All Seqs");
		saveButton.addActionListener(this);
		panel1.add(savePosButton);
		panel2.add(saveNegButton);
		panel3.add(saveButton);
		northEast3Panel.add(panel1);
		northEast3Panel.add(panel2);
		northEast3Panel.add(panel3);		
		
		JPanel scoringMatrixPanel = new JPanel(new GridLayout(1,3));
		scoringMatrixPanel.setBorder(BorderFactory.createCompoundBorder(
		    	BorderFactory.createTitledBorder("Scoring Matrix"),
		    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		JPanel matchPanel = new JPanel();
		JLabel matchLabel = new JLabel("Match: ");
		matchTextField = new JTextField("1", 3);
		matchTextField.setEnabled(false);
		matchPanel.add(matchLabel);
		matchPanel.add(matchTextField);
		
		JPanel mismatchPanel = new JPanel();
		JLabel mismatchLabel = new JLabel("MisMatch: ");
		mismatchTextField = new JTextField("0", 3);
		mismatchTextField.setEnabled(false);
		mismatchPanel.add(mismatchLabel);
		mismatchPanel.add(mismatchTextField);
		
		JPanel indelPanel = new JPanel();
		JLabel indelLabel = new JLabel("Indel: ");		
		indelTextField = new JTextField("0", 3);
		indelTextField.setEnabled(false);
		indelPanel.add(indelLabel);
		indelPanel.add(indelTextField);			
		
		scoringMatrixPanel.add(matchPanel);
		scoringMatrixPanel.add(mismatchPanel);
		scoringMatrixPanel.add(indelPanel);
		
		JPanel northPanel = new JPanel(new GridLayout(1,3));
		northPanel.add(northWestPanel);
		northPanel.add(northCenterPanel);
		northPanel.add(scoringMatrixPanel);
		
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(northEast2Panel, BorderLayout.WEST);
		JPanel southCenterPanel = new JPanel(new GridLayout(1,2));
		southCenterPanel.add(identityPanel);
		southCenterPanel.add(northEast3Panel);
		southPanel.add(southCenterPanel, BorderLayout.CENTER);
		
		JPanel sequenceNamePanel = new JPanel(new BorderLayout());
    	sequenceNamePanel.setBorder(BorderFactory.createCompoundBorder(
    	BorderFactory.createTitledBorder("Sequences Name"),
    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));
    	sequenceSimilarityCheckModel = new RedundancyReductionModel();
    	sequenceNameTable = new JTable(sequenceSimilarityCheckModel);
    	sequenceNameTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
    	sequenceNameTable.getColumnModel().getColumn(0).setMinWidth(50);
    	sequenceNameTable.getColumnModel().getColumn(0).setMaxWidth(70);
        sequenceNameTable.getColumnModel().getColumn(1).setMinWidth(200);
        sequenceNameTable.getColumnModel().getColumn(1).setMaxWidth(550);
        sequenceNameTable.getColumnModel().getColumn(2).setMinWidth(180);   
        sequenceNameTable.getColumnModel().getColumn(3).setMinWidth(60);
        sequenceNameTable.getColumnModel().getColumn(3).setMaxWidth(80);
        sequenceNameTable.getColumnModel().getColumn(4).setMinWidth(120);
        sequenceNameTable.getColumnModel().getColumn(4).setMaxWidth(140);
        sequenceNameTable.getColumnModel().getColumn(5).setMinWidth(80);
        sequenceNameTable.getColumnModel().getColumn(5).setMaxWidth(100);        
    	sequenceNameTable.getColumnModel().getColumn(0).setPreferredWidth(40);     
        sequenceNameTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        sequenceNameTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        sequenceNameTable.getColumnModel().getColumn(3).setPreferredWidth(50);
        
        //create Button Column
        //new ButtonColumn(sequenceNameTable, 6);
        //sequenceNameTable.getSelectionModel().addListSelectionListener(this);
        sequenceNameTableScrollPane = new JScrollPane(sequenceNameTable);
    	sequenceNameTableScrollPane.setPreferredSize(new Dimension(250,700));
    	
    	JPanel featureStatusPanel = new JPanel();
    	JLabel posFeatureLabel = new JLabel("(+) Sequences: ");
    	posFeatureTextField = new JTextField("0",5);
    	posFeatureTextField.setEnabled(false);
    	JLabel negFeatureLabel = new JLabel("(-) Sequences: ");
    	negFeatureTextField = new JTextField("0",5);
    	negFeatureTextField.setEnabled(false);
    	JLabel totalFeatureLabel = new JLabel("Total Sequences: ");
    	totalFeatureTextField = new JTextField("0",5);
    	totalFeatureTextField.setEnabled(false);
    	featureStatusPanel.add(posFeatureLabel);
    	featureStatusPanel.add(posFeatureTextField);
    	featureStatusPanel.add(negFeatureLabel);
    	featureStatusPanel.add(negFeatureTextField);
    	featureStatusPanel.add(totalFeatureLabel);
    	featureStatusPanel.add(totalFeatureTextField);
    	
    	sequenceNamePanel.add(featureStatusPanel,BorderLayout.NORTH);
    	sequenceNamePanel.add(sequenceNameTableScrollPane,BorderLayout.CENTER);    	
		
		//Add the tabbed pane to this panel.
		BorderLayout thisLayout = new BorderLayout();		
		setLayout(thisLayout);
        add(northPanel,BorderLayout.NORTH);
        add(sequenceNamePanel,BorderLayout.CENTER);
        add(southPanel,BorderLayout.SOUTH);
	}
	public void actionPerformed(ActionEvent ae){
		if(ae.getSource().equals(savePosButton)){
			save(0);
		}else if(ae.getSource().equals(saveNegButton)){
			save(1);
		}
		else if(ae.getSource().equals(saveButton)){
			save(2);
		}
		else if(ae.getSource().equals(stopButton)){
			sequenceSimilarityCheckModel.setRunningTest(false);
		}
		else if(ae.getSource().equals(runButton)){								
			run();							
		}else if(ae.getSource().equals(deleteButton)){
			if(sequenceSimilarityCheckModel.getCompleteRun())
				delete();
			else
				JOptionPane.showMessageDialog(null,"You have to do a complete run for similarity test before you can do redundancy reduction",
						"Error",JOptionPane.ERROR_MESSAGE);
		}
		/*else if(ae.getSource().equals(negBrowseButton)){
			//applicationData.hasLocationIndexBeenSet = false;
			JFileChooser fc;	    	
    		//if working directory not set then look at the Sirius Settings file
    		String lastLocation = SiriusSettings.getInformation("LastSequenceSimilarityCheckFileLocation: ");
    		if(lastLocation == null)
    			fc = new JFileChooser();
    		else
    			fc = new JFileChooser(lastLocation);	    				    
	        FileNameExtensionFilter filter = new FileNameExtensionFilter(
	            "Fasta Files", "fasta");
	        fc.setFileFilter(filter);			        			        			    	
			int returnVal = fc.showOpenDialog(parent);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
	            SiriusSettings.updateInformation("LastSequenceSimilarityCheckFileLocation: ", file.getAbsolutePath());
	            negFilenameTextField.setText(file.getAbsolutePath());
	            try{
	            	sequenceSimilarityCheckModel.loadFastaFile(file,false);
	            	updateFeatureLabel();
	            }catch(Exception e){
	            	JOptionPane.showMessageDialog(null,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
	            	e.printStackTrace();
	            } 	            
			}
		}*/
		/*else if(ae.getSource().equals(browseButton)){
			//applicationData.hasLocationIndexBeenSet = false;
			JFileChooser fc;	    	
    		//if working directory not set then look at the Sirius Settings file
    		String lastLocation = SiriusSettings.getInformation("LastSequenceSimilarityCheckFileLocation: ");
    		if(lastLocation == null)
    			fc = new JFileChooser();
    		else
    			fc = new JFileChooser(lastLocation);	    				    
	        FileNameExtensionFilter filter = new FileNameExtensionFilter(
	            "Fasta Files", "fasta");
	        fc.setFileFilter(filter);			        			        			    	
			int returnVal = fc.showOpenDialog(parent);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
	            SiriusSettings.updateInformation("LastSequenceSimilarityCheckFileLocation: ", file.getAbsolutePath());
	            filenameTextField.setText(file.getAbsolutePath());
	            try{
	            	sequenceSimilarityCheckModel.loadFastaFile(file,true);
	            	updateFeatureLabel();
	            }catch(Exception e){
	            	JOptionPane.showMessageDialog(null,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
	            	e.printStackTrace();
	            } 	            
			}
		}*/
	}
	private void run(){
		//Check if the input % is valid
		try{
			final int identityInt = Integer.parseInt(identityTextField.getText());
			if(identityInt < 0 || identityInt > 100)
				throw new NumberFormatException();
			Thread runThread = new Thread(){
				public void run(){
					runButton.setEnabled(false);
					//browseButton.setEnabled(false);
					//negBrowseButton.setEnabled(false);
					deleteButton.setEnabled(false);
					saveButton.setEnabled(false);
					savePosButton.setEnabled(false);
					saveNegButton.setEnabled(false);
					identityTextField.setEnabled(false);
					sequenceSimilarityCheckModel.setRunningTest(true);
					sequenceSimilarityCheckModel.runSimilarityTest(sequenceNameTable,sequenceNameTableScrollPane,statusTextField,reduceWhileRunCheckBox.isSelected(),identityInt);
					updateFeatureLabel();
					runButton.setEnabled(true);
					//browseButton.setEnabled(true);
					//negBrowseButton.setEnabled(true);
					deleteButton.setEnabled(true);
					saveButton.setEnabled(true);
					savePosButton.setEnabled(true);
					saveNegButton.setEnabled(true);
					identityTextField.setEnabled(true);
				}
			};
			runThread.setPriority(Thread.MIN_PRIORITY);
	     	runThread.start();
		}catch(NumberFormatException e){
			JOptionPane.showMessageDialog(null,"Please enter only integer of range 0 to 100 in identity field","Error",JOptionPane.ERROR_MESSAGE);			
		}
	}
	private void delete(){				
		Thread runThread = new Thread(){
			public void run(){
				runButton.setEnabled(false);
				//browseButton.setEnabled(false);
				//negBrowseButton.setEnabled(false);
				deleteButton.setEnabled(false);
				saveButton.setEnabled(false);		
				identityTextField.setEnabled(false);
				sequenceSimilarityCheckModel.reduceRedunancy(sequenceNameTableScrollPane, sequenceNameTable, statusTextField);
				updateFeatureLabel();
				runButton.setEnabled(true);
				//browseButton.setEnabled(true);
				//negBrowseButton.setEnabled(true);
				deleteButton.setEnabled(true);
				saveButton.setEnabled(true);
				identityTextField.setEnabled(true);
			}
		};
		runThread.setPriority(Thread.MIN_PRIORITY);
     	runThread.start();		
	}
	private void updateFeatureLabel(){
		posFeatureTextField.setText("" + sequenceSimilarityCheckModel.getPosDataSize());
		negFeatureTextField.setText("" + sequenceSimilarityCheckModel.getNegDataSize());
		totalFeatureTextField.setText("" + sequenceSimilarityCheckModel.getTotalDataSize());
	}
	private void save(int dataTypeToSave){		
		JFileChooser fc;	    	
		//if working directory not set then look at the Sirius Settings file
		String lastLocation = SiriusSettings.getInformation("LastSequenceSimilarityCheckFileLocation: ");
		if(lastLocation == null)
			fc = new JFileChooser();
		else
			fc = new JFileChooser(lastLocation);	    				    
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Fasta Files", "fasta");
        fc.setFileFilter(filter);
        if(dataTypeToSave == 0){
        	String filename = this.filenameTextField.getText();
        	StringTokenizer st = new StringTokenizer(filename, File.separator);
        	String lastToken = "";
        	while(st.hasMoreTokens()){
        		lastToken = st.nextToken();
        	}
        	StringTokenizer st2 = new StringTokenizer(lastToken, ".");
        	fc.setSelectedFile(new File(st2.nextToken() + "_" + this.identityTextField.getText() + ".fasta"));
        }
        else if(dataTypeToSave == 1){
        	String filename = this.negFilenameTextField.getText();
        	StringTokenizer st = new StringTokenizer(filename, File.separator);
        	String lastToken = "";
        	while(st.hasMoreTokens()){
        		lastToken = st.nextToken();
        	}
        	StringTokenizer st2 = new StringTokenizer(lastToken, ".");
        	fc.setSelectedFile(new File(st2.nextToken() + "_" + this.identityTextField.getText() + ".fasta"));
        }
		int returnVal = fc.showSaveDialog(parent);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();			           
			String savingFilename = file.getAbsolutePath();
			if(savingFilename.indexOf(".fasta") == -1)
				savingFilename += ".fasta";
			SiriusSettings.updateInformation("LastSequenceSimilarityCheckFileLocation: ", savingFilename);
			try{
				BufferedWriter output = new BufferedWriter(new FileWriter(savingFilename));
				switch(dataTypeToSave){
					case 0: sequenceSimilarityCheckModel.write(output, true); break;				
					case 1: sequenceSimilarityCheckModel.write(output, false); break;
					case 2: sequenceSimilarityCheckModel.write(output, true); sequenceSimilarityCheckModel.write(output, false); break;
				}
				output.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void mouseClicked(MouseEvent me) {
		if(me.getSource().equals(this.negFilenameTextField)){
			//applicationData.hasLocationIndexBeenSet = false;
			JFileChooser fc;	    	
    		//if working directory not set then look at the Sirius Settings file
    		String lastLocation = SiriusSettings.getInformation("LastSequenceSimilarityCheckFileLocation: ");
    		if(lastLocation == null)
    			fc = new JFileChooser();
    		else
    			fc = new JFileChooser(lastLocation);	    				    
	        FileNameExtensionFilter filter = new FileNameExtensionFilter(
	            "Fasta Files", "fasta");
	        fc.setFileFilter(filter);			        			        			    	
			int returnVal = fc.showOpenDialog(parent);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
	            SiriusSettings.updateInformation("LastSequenceSimilarityCheckFileLocation: ", file.getAbsolutePath());
	            negFilenameTextField.setText(file.getAbsolutePath());
	            try{
	            	sequenceSimilarityCheckModel.loadFastaFile(file,false);
	            	updateFeatureLabel();
	            }catch(Exception e){
	            	JOptionPane.showMessageDialog(null,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
	            	e.printStackTrace();
	            } 	            
			}
		}else if(me.getSource().equals(this.filenameTextField)){
			//applicationData.hasLocationIndexBeenSet = false;
			JFileChooser fc;	    	
    		//if working directory not set then look at the Sirius Settings file
    		String lastLocation = SiriusSettings.getInformation("LastSequenceSimilarityCheckFileLocation: ");
    		if(lastLocation == null)
    			fc = new JFileChooser();
    		else
    			fc = new JFileChooser(lastLocation);	    				    
	        FileNameExtensionFilter filter = new FileNameExtensionFilter(
	            "Fasta Files", "fasta");
	        fc.setFileFilter(filter);			        			        			    	
			int returnVal = fc.showOpenDialog(parent);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
	            SiriusSettings.updateInformation("LastSequenceSimilarityCheckFileLocation: ", file.getAbsolutePath());
	            filenameTextField.setText(file.getAbsolutePath());
	            try{
	            	sequenceSimilarityCheckModel.loadFastaFile(file,true);
	            	updateFeatureLabel();
	            }catch(Exception e){
	            	JOptionPane.showMessageDialog(null,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
	            	e.printStackTrace();
	            } 	            
			}
		}
	}
	
	public void mouseEntered(MouseEvent arg0) {
		
	}
	
	public void mouseExited(MouseEvent arg0) {
		
	}
	
	public void mousePressed(MouseEvent arg0) {
		
	}
	
	public void mouseReleased(MouseEvent arg0) {
		
	}
}
class ButtonColumn extends AbstractCellEditor
implements TableCellRenderer, TableCellEditor, ActionListener
{
	static final long serialVersionUID = 28072008;
	JTable table;
	JButton renderButton;
	JButton editButton;
	String text;
	
	public ButtonColumn(JTable table, int column)
	{
	    super();
	    this.table = table;
	    renderButton = new JButton();
	
	    editButton = new JButton();
	    editButton.setFocusPainted( false );
	    editButton.addActionListener( this );
	
	    TableColumnModel columnModel = table.getColumnModel();
	    columnModel.getColumn(column).setCellRenderer( this );
	    columnModel.getColumn(column).setCellEditor( this );
	}
	
	public Component getTableCellRendererComponent(
	    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
	    if (hasFocus)
	    {
	        renderButton.setForeground(table.getForeground());
	        renderButton.setBackground(UIManager.getColor("Button.background"));
	    }
	    else if (isSelected)
	    {
	        renderButton.setForeground(table.getSelectionForeground());
	         renderButton.setBackground(table.getSelectionBackground());
	    }
	    else
	    {
	        renderButton.setForeground(table.getForeground());
	        renderButton.setBackground(UIManager.getColor("Button.background"));
	    }
	
	    renderButton.setText( (value == null) ? "" : value.toString() );
	    return renderButton;
	}
	
	public Component getTableCellEditorComponent(
	    JTable table, Object value, boolean isSelected, int row, int column)
	{
	    text = (value == null) ? "" : value.toString();
	    editButton.setText( text );
	    return editButton;
	}
	
	public Object getCellEditorValue()
	{
	    return text;
	}
	
	public void actionPerformed(ActionEvent e)
	{
	    fireEditingStopped();
	}
}
