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
package sirius.trainer.step1;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import sirius.main.ApplicationData;
import sirius.main.FastaFileManipulation;
import sirius.trainer.main.SiriusSettings;
import sirius.trainer.main.StatusPane;
import sirius.utils.FastaFormat;


public class DefineDataPane extends JComponent implements ActionListener, MouseListener{
	static final long serialVersionUID = sirius.Sirius.version;
	private JInternalFrame parent;
	private JTabbedPane tabbedPane;
	private ApplicationData applicationData;
	
	private String lastDirectoryOpened;

	private JButton addPositiveButton = new JButton("Add File (FASTA Format ONLY)");
	private JButton addNegativeButton = new JButton("Add File (FASTA Format ONLY)");

	private JButton deletePositiveButton = new JButton("Delete");
	private JButton deleteNegativeButton = new JButton("Delete");
	private JLabel positiveSequenceLabel = new JLabel("Total Sequences: 0");
	private JLabel negativeSequenceLabel = new JLabel("Total Sequences: 0");
	
	private Step1TableModel positiveStep1TableModel = new Step1TableModel();	
	private Step1TableModel negativeStep1TableModel = new Step1TableModel();
	private JTable positiveTable = new JTable(this.positiveStep1TableModel);
	private JTable negativeTable = new JTable(this.negativeStep1TableModel);	
	
	private JTextField positiveDataset1FromField = new JTextField(5);
	private JTextField positiveDataset2FromField = new JTextField(5);
	private JTextField positiveDataset3FromField = new JTextField(5);
	private JTextField positiveDataset1ToField = new JTextField(5);       	   
	private JTextField positiveDataset2ToField = new JTextField(5);       	   
	private JTextField positiveDataset3ToField = new JTextField(5);		
    			
	private JTextField negativeDataset1FromField = new JTextField(5);
	private JTextField negativeDataset2FromField = new JTextField(5);
	private JTextField negativeDataset3FromField = new JTextField(5);
	private JTextField negativeDataset1ToField = new JTextField(5);
	private JTextField negativeDataset2ToField = new JTextField(5);
	private JTextField negativeDataset3ToField = new JTextField(5);
		
	private JButton nextStepButton = new JButton("NEXT >>>");;	
	private JButton workingDirectoryButton;
	private JTextField workingDirectoryField;
	private JButton saveSettingButton;
	private JButton loadSettingButton;
	
	private StatusPane statusPane;	
		
	private JRadioButton dnaSequenceRadioButton = new JRadioButton("DNA");
	private JRadioButton proteinSequenceRadioButton = new JRadioButton("Protein");
	
	public Step1TableModel getPositiveStep1TableModel(){return this.positiveStep1TableModel;}
	public Step1TableModel getNegativeStep1TableModel(){return this.negativeStep1TableModel;}
	
    public DefineDataPane(JInternalFrame parent,JTabbedPane tabbedPane,ApplicationData applicationData) {
    	this.lastDirectoryOpened = null;
    	this.statusPane = applicationData.getStatusPane();
    	this.parent = parent;
    	this.tabbedPane = tabbedPane;
    	this.applicationData = applicationData;
    	BorderLayout thisLayout = new BorderLayout();
    	thisLayout.setVgap(0);
    	thisLayout.setHgap(0);
    	setLayout(thisLayout);    			
		
    	//GridBagLayout gBag = new GridBagLayout();
		JPanel north_center = new JPanel(new BorderLayout(5,5));	
		north_center.setBorder(BorderFactory.createCompoundBorder(
		    	BorderFactory.createTitledBorder("Working Directory (where all output files are written to)"),
		    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));       	       		
        this.workingDirectoryField = new JTextField(); 
        this.workingDirectoryField.setFocusable(false);
        this.workingDirectoryButton = new JButton("Choose");
        this.workingDirectoryButton.addActionListener(this);        
        north_center.add(this.workingDirectoryField, BorderLayout.CENTER);
        north_center.add(this.workingDirectoryButton,BorderLayout.WEST);        
        
        JPanel sequenceTypePanel = new JPanel(new GridLayout(1,2));
        sequenceTypePanel.setBorder(BorderFactory.createTitledBorder("Sequence Type"));		
        this.dnaSequenceRadioButton.addActionListener(this);
        this.proteinSequenceRadioButton.addActionListener(this);
		sequenceTypePanel.add(this.dnaSequenceRadioButton);
		sequenceTypePanel.add(this.proteinSequenceRadioButton);
		
		JPanel north = new JPanel(new BorderLayout());
		north.add(north_center,BorderLayout.CENTER);
		north.add(sequenceTypePanel,BorderLayout.EAST);
        
        JPanel center = new JPanel(new GridLayout(2,1));
        
        // SubCenter 1
        JPanel subCenter1 = new JPanel(new BorderLayout());
        JPanel subCenter1_center = new JPanel(new BorderLayout());
        subCenter1_center.setBorder(BorderFactory.createTitledBorder("Files with +ve Sequences"));
        JPanel subCenter1_east = new JPanel(new GridLayout(3,1)); 
        subCenter1_east.setBorder(BorderFactory.createTitledBorder("Split all +ve Sequences into 3 Dataset"));
                        
        this.positiveTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.positiveTable.getColumnModel().getColumn(0).setPreferredWidth(350);
        this.positiveTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        this.positiveTable.getColumnModel().getColumn(2).setPreferredWidth(50);
        this.positiveTable.getColumnModel().getColumn(0).setMinWidth(50);
        this.positiveTable.getColumnModel().getColumn(1).setMinWidth(20);
        this.positiveTable.getColumnModel().getColumn(2).setMinWidth(20);        
        this.positiveTable.addFocusListener(new FocusListener(){
	         public void focusGained(FocusEvent e){	         	
	         	DefineDataPane.this.negativeTable.clearSelection();
	         }
	
	         public void focusLost(FocusEvent e){	         	
	         }
		});	
		this.positiveTable.addMouseListener(this);
        	 
       	JScrollPane positiveTableScrollPane = new JScrollPane(positiveTable);              
       	       	     	       	
       	JPanel subCenter1_center_center = new JPanel(new BorderLayout());
       	subCenter1_center_center.add(positiveTableScrollPane,BorderLayout.CENTER);
        subCenter1_center.add(subCenter1_center_center,BorderLayout.CENTER);
        
        JPanel subCenter1_center_south = new JPanel(new GridLayout(1,3));               
       	this.addPositiveButton.addActionListener(this);                  
        this.deletePositiveButton.addActionListener(this);      	        
                
        subCenter1_center_south.add(this.positiveSequenceLabel);
        subCenter1_center_south.add(this.addPositiveButton);
        subCenter1_center_south.add(this.deletePositiveButton);        
        subCenter1_center.add(subCenter1_center_south,BorderLayout.SOUTH);
        JPanel subCenter1_center_north = new JPanel(new BorderLayout());
        subCenter1_center_north.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));                
        subCenter1_center.add(subCenter1_center_north,BorderLayout.NORTH);
        subCenter1.add(subCenter1_center);
        
        JPanel positiveDataset1Panel = new JPanel();     
       	JPanel positiveDataset2Panel = new JPanel();     
      	JPanel positiveDataset3Panel = new JPanel();       	       
       	
       	positiveDataset1Panel.add(new JLabel("Dataset 1 (For Classifier One): Seq#"));
       	positiveDataset1Panel.add(positiveDataset1FromField);
       	positiveDataset1Panel.add(new JLabel(" To "));
       	positiveDataset1Panel.add(positiveDataset1ToField);
       	
       	positiveDataset2Panel.add(new JLabel("Dataset 2 (For Classifier Two): Seq#"));
       	positiveDataset2Panel.add(positiveDataset2FromField);
       	positiveDataset2Panel.add(new JLabel(" To "));
       	positiveDataset2Panel.add(positiveDataset2ToField);
       	
       	positiveDataset3Panel.add(new JLabel("Dataset 3  (Use As Test Set)  : Seq#"));
       	positiveDataset3Panel.add(positiveDataset3FromField);
       	positiveDataset3Panel.add(new JLabel(" To "));
       	positiveDataset3Panel.add(positiveDataset3ToField);       	     
       	                                      
        subCenter1_east.add(positiveDataset1Panel);
        subCenter1_east.add(positiveDataset2Panel); 
        subCenter1_east.add(positiveDataset3Panel); 
        subCenter1.add(subCenter1_east,BorderLayout.EAST);
        //SubCenter 2
        JPanel subCenter2 = new JPanel(new BorderLayout());
        JPanel subCenter2_center = new JPanel(new BorderLayout());
        subCenter2_center.setBorder(BorderFactory.createTitledBorder("Files with -ve Sequences"));
        JPanel subCenter2_east = new JPanel(new GridLayout(3,1)); 
        subCenter2_east.setBorder(BorderFactory.createTitledBorder("Split all -ve Sequences into 3 Dataset"));
                        
        this.negativeTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.negativeTable.getColumnModel().getColumn(0).setPreferredWidth(350);
        this.negativeTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        this.negativeTable.getColumnModel().getColumn(2).setPreferredWidth(50);
        this.negativeTable.getColumnModel().getColumn(0).setMinWidth(50);
        this.negativeTable.getColumnModel().getColumn(1).setMinWidth(20);
        this.negativeTable.getColumnModel().getColumn(2).setMinWidth(20);         
        this.negativeTable.addFocusListener(new FocusListener() {
	         public void focusGained(FocusEvent e){
	         	DefineDataPane.this.positiveTable.clearSelection();
	         }
	
	         public void focusLost(FocusEvent e){
	         }
		});
        this.negativeTable.addMouseListener(this);
       	JScrollPane negativeTableScrollPane = new JScrollPane(negativeTable);              
       	       	     	       	
       	JPanel subCenter2_center_center = new JPanel(new BorderLayout());
       	subCenter2_center_center.add(negativeTableScrollPane,BorderLayout.CENTER);
        subCenter2_center.add(subCenter2_center_center,BorderLayout.CENTER);
        
        JPanel subCenter2_center_south = new JPanel(new GridLayout(1,3));
				
        this.addNegativeButton.addActionListener(this);                
        this.deleteNegativeButton.addActionListener(this);               
                
        subCenter2_center_south.add(this.negativeSequenceLabel);
        subCenter2_center_south.add(this.addNegativeButton);
        subCenter2_center_south.add(this.deleteNegativeButton);        
        subCenter2_center.add(subCenter2_center_south,BorderLayout.SOUTH);
        subCenter2.add(subCenter2_center);
        
        JPanel negativeDataset1Panel = new JPanel();     
       	JPanel negativeDataset2Panel = new JPanel();     
      	JPanel negativeDataset3Panel = new JPanel();                	  
       	
       	negativeDataset1Panel.add(new JLabel("Dataset 1 (For Classifier One): Seq#"));
       	negativeDataset1Panel.add(this.negativeDataset1FromField);
       	negativeDataset1Panel.add(new JLabel(" To "));
       	negativeDataset1Panel.add(this.negativeDataset1ToField);
       	
       	negativeDataset2Panel.add(new JLabel("Dataset 2 (For Classifier Two): Seq#"));
       	negativeDataset2Panel.add(this.negativeDataset2FromField);
       	negativeDataset2Panel.add(new JLabel(" To "));
       	negativeDataset2Panel.add(this.negativeDataset2ToField);
       	
       	negativeDataset3Panel.add(new JLabel("Dataset 3  (Use As Test Set)  : Seq#"));
       	negativeDataset3Panel.add(negativeDataset3FromField);
       	negativeDataset3Panel.add(new JLabel(" To "));
       	negativeDataset3Panel.add(negativeDataset3ToField);
       	                                      
        subCenter2_east.add(negativeDataset1Panel);
        subCenter2_east.add(negativeDataset2Panel); 
        subCenter2_east.add(negativeDataset3Panel); 
        subCenter2.add(subCenter2_east,BorderLayout.EAST);
	    	
        center.add(subCenter1);
        center.add(subCenter2);
        
        //South
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        JPanel south = new JPanel(gridbag);
        saveSettingButton = new JButton("Save Step1 Settings");
        saveSettingButton.addActionListener(this);        
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 2.0;
        c.weighty = 1.0;
        gridbag.setConstraints(saveSettingButton,c);
        loadSettingButton = new JButton("Load Step1 Settings");
        loadSettingButton.addActionListener(this);
        gridbag.setConstraints(loadSettingButton,c);        
        nextStepButton.addActionListener(this);             
        c.weightx = 1.0;
        c.weighty = 1.0;
        gridbag.setConstraints(nextStepButton,c);
        south.add(saveSettingButton);            
        south.add(loadSettingButton);
        south.add(nextStepButton);
        
        add(north,BorderLayout.NORTH);	
       	add(center,BorderLayout.CENTER);	
       	add(south,BorderLayout.SOUTH);	                  
    } 
    private void setIndex(String absolutePath,Step1TableModel model,ApplicationData applicationData,
    	boolean showErrorDialog, boolean byAddButton, int index){
    	try{
    		BufferedReader in = new BufferedReader(new FileReader(absolutePath));
    		SetIndexMainDialog dialog = new SetIndexMainDialog(in,absolutePath,model,applicationData,
    			showErrorDialog, byAddButton,index);
    		dialog.pack();
    		dialog.setLocationRelativeTo(parent);    		  		
    		dialog.setVisible(true);    		
    		in.close();    		
    	}catch(Exception e){
    		JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
    		e.printStackTrace();}    	
    }
    //This method is called via loadSettings where each fasta file is read through to check it format
    public  void addFileMethod(String absolutePath,JTable table,Step1TableModel model){    	
		try{
			BufferedReader in = new BufferedReader(new FileReader(absolutePath));  
            int countSequenceNumber = 0;//to find out how many sequence does this file has            
            int plusOneIndex = -99;//error
            String line;
            boolean firstEntry = true;
            //boolean ignoreEntry = false;//need this because i only want to call setIndex once!
            //AHFU_TEMP
            //there could be a bug within these few lines as having ">" at index 0 
            //is not limited to fasta only..
            //pdf files also have this feature..
            //However, this is not a serious bug so just ignore it for now
            //String eachSequence = "";
            while ((line = in.readLine()) != null) {		            	
            	if(line.indexOf(">")==0){
            		countSequenceNumber++;
            		int index1 = line.indexOf("+1_Index(");
            		if(index1 == -1){//if +1_Index is not found in the fasta file
            			in.close();
            			firstEntry = false;
            			plusOneIndex = -1;
            			//show the setIndexMainDialog to set the +1_Index
            			setIndex(absolutePath,model,applicationData,false,true,-1);
            			return;
            		}
            		else{//if fasta file already have +1_Index
            			try{
            				if(firstEntry){
            					plusOneIndex = Integer.parseInt(line.substring(index1 + "+1_Index(".length(),
            							line.indexOf(")",index1)));            					
            					firstEntry = false;
            				}
            				else{
            					int value = Integer.parseInt(line.substring(index1 + "+1_Index(".length(),
            							line.indexOf(")",index1)));
            					//check for discrepency in the fasta file (in terms of +1_Index) 
            					if((value > 0 && plusOneIndex < 0) ||(value < 0 && plusOneIndex > 0)){
            							setIndex(absolutePath,model,applicationData,true,true,-1);            							
            							return;
            						}            						
            				}            				            				
            			}catch(Exception e){
            				JOptionPane.showMessageDialog(null,"Exception Occured","Error",
            						JOptionPane.ERROR_MESSAGE);
            				e.printStackTrace();}
            			
            		}	
            		//eachSequence = "";
            	}
            	else{
            		//eachSequence += line.trim(); 
            	}
            }
            in.close(); 
            //The file dun have > at position 0 anywhere
            if(countSequenceNumber == 0){
            	JOptionPane.showMessageDialog(parent,"Please ensure that " + absolutePath +
            		" is in FASTA format.","ERROR",JOptionPane.ERROR_MESSAGE);            
            }               
           	else{
           		model.add(absolutePath,countSequenceNumber,plusOneIndex);
           		if(table != null){
	            	table.getColumnModel().getColumn(0).setMinWidth(350);
	    			table.getColumnModel().getColumn(1).setMinWidth(50);
	    			table.getColumnModel().getColumn(2).setMinWidth(50);    					
           		}
           	}                      	           	
        }catch(Exception e){
        	//JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
        	e.printStackTrace();}           		        
    }    
    private void updateTotalSequences(Step1TableModel model,JLabel label){ 
    	int totalSequences = model.getTotalSequences();
    	//if this is 0 then i should check if the both the posTableModel and negTableModel is 0
    	//because i wont know if this model is the posTableModel or negTableModel
    	if(totalSequences == 0 && positiveStep1TableModel.getTotalSequences() == 0 && 
    		negativeStep1TableModel.getTotalSequences() == 0){
    			//applicationData.hasLocationIndexBeenSet = false;
    	}    		
    	label.setText("Total Sequences: " + totalSequences);
    }
    //this method is called via clicking the ADD button of the fasta files for positive and negative
    public void addFileMethod(JTable table,Step1TableModel model){
		JFileChooser fc;				
		if(this.workingDirectoryField.getText().isEmpty() == false)
			fc = new JFileChooser(this.workingDirectoryField.getText());
		else if(lastDirectoryOpened!=null)
			fc = new JFileChooser(lastDirectoryOpened);
		else
			fc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
	            "Fasta Files", "fasta");
	    fc.setFileFilter(filter);	
		int returnVal = fc.showOpenDialog(parent);		
		if (returnVal == JFileChooser.APPROVE_OPTION) {	        
			File file = fc.getSelectedFile();
            try{			            		            
            	lastDirectoryOpened = file.getPath();
            	addFileMethod(file.getAbsolutePath(),table,model);	            
            }catch(Exception e){
            	JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
            	e.printStackTrace();}            	        		
	    }else
	        statusPane.setText("Open command is cancelled by user.");	    
    }         
    private void saveSettings(){
    	//Ensure that no other processes are currently running
		if(applicationData.getOneThread() == null){
    		applicationData.setOneThread(new Thread(){	      	
			public void run(){    		
				//Ensure that user set a workingDirectory
	    		if(workingDirectoryField.getText().length() == 0){
	    			JOptionPane.showMessageDialog(parent,"Please set Working Directory!",
	    						"ERROR",JOptionPane.ERROR_MESSAGE);
	    			workingDirectoryButton.requestFocusInWindow();
	    		}else{
	    			try{	    				
				    	JFileChooser fc = new JFileChooser(workingDirectoryField.getText());				    	
				        FileNameExtensionFilter filter = new FileNameExtensionFilter(
				            "Step 1 Settings Files", "step1settings");
				        fc.setFileFilter(filter);			        			        
				    	
						int returnVal = fc.showSaveDialog(parent);
						if (returnVal == JFileChooser.APPROVE_OPTION) {	    				
	    				//create a file "Step1_Settings.txt" to save settings	
						File file = fc.getSelectedFile();				        
						String savingFilename = file.getAbsolutePath();
						if(savingFilename.indexOf(".step1settings") == -1)
							savingFilename += ".step1settings";
	    				BufferedWriter output = new BufferedWriter(new FileWriter(savingFilename));
	    				output.write("Step 1 Working Directory: " + workingDirectoryField.getText());
	    				output.newLine();
	    				//save each file's location, number of sequence and sequence no (from and to)
	    				for(int x = 0; x < positiveStep1TableModel.getRowCount();  x++){
	    					output.write("Step 1 Positive: " + positiveStep1TableModel.toString(x));
	    					output.newLine();
	    				}    				
	    				output.write("Step 1 Positive Dataset 1 From Sequence #: " + 
	    					positiveDataset1FromField.getText());
	    				output.newLine();
	    				output.write("Step 1 Positive Dataset 1 To Sequence #: " + 
	    					positiveDataset1ToField.getText());
	    				output.newLine();
	    				output.write("Step 1 Positive Dataset 2 From Sequence #: " + 
	    					positiveDataset2FromField.getText());
	    				output.newLine();
	    				output.write("Step 1 Positive Dataset 2 To Sequence #: " + 
	    					positiveDataset2ToField.getText());
	    				output.newLine();
	    				output.write("Step 1 Positive Dataset 3 From Sequence #: " + 
	    					positiveDataset3FromField.getText());
	    				output.newLine();
	    				output.write("Step 1 Positive Dataset 3 To Sequence #: " + 
	    					positiveDataset3ToField.getText());
	    				output.newLine();
	    				for(int x = 0; x < negativeStep1TableModel.getRowCount();  x++){
	    					output.write("Step 1 Negative: " + negativeStep1TableModel.toString(x));
	    					output.newLine();
	    				}  
	    				output.write("Step 1 Negative Dataset 1 From Sequence #: " + 
	    					negativeDataset1FromField.getText());
	    				output.newLine();
	    				output.write("Step 1 Negative Dataset 1 To Sequence #: " + 
	    					negativeDataset1ToField.getText());
	    				output.newLine();
	    				output.write("Step 1 Negative Dataset 2 From Sequence #: " + 
	    					negativeDataset2FromField.getText());
	    				output.newLine();
	    				output.write("Step 1 Negative Dataset 2 To Sequence #: " + 
	    					negativeDataset2ToField.getText());
	    				output.newLine();
	    				output.write("Step 1 Negative Dataset 3 From Sequence #: " + 
	    					negativeDataset3FromField.getText());
	    				output.newLine();
	    				output.write("Step 1 Negative Dataset 3 To Sequence #: " + 
	    					negativeDataset3ToField.getText());
	    				output.newLine();
	    				output.write("Sequence Type: ");
	    				if(dnaSequenceRadioButton.isSelected())
	    					output.write("DNA");
	    				else if(proteinSequenceRadioButton.isSelected())
	    					output.write("PROTEIN");
	    				else
	    					output.write("NOT_SET");
	    				output.newLine();
	    				output.close();
	    				statusPane.setText("Step 1 Settings Saved To " + savingFilename);
						}else{
							//JOptionPane.showMessageDialog(parent,"Please only save file with .step1settings extensions",
							//		"Error", JOptionPane.ERROR_MESSAGE);
						}
	    			}catch(Exception e){
	    				JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
	    				e.printStackTrace();}		    				    			
	    		}
	    		applicationData.setOneThread(null);
			}});
      		applicationData.getOneThread().setPriority(Thread.MIN_PRIORITY); // UI has most priority
      		applicationData.getOneThread().start();
    	}
    	else{
     		JOptionPane.showMessageDialog(parent,"Can't save step 1 settings now,\n"
      			+ "currently busy with other IO","Save Step 1 Settings", JOptionPane.WARNING_MESSAGE);
    	}			    		        
    }   
	private void loadSettings(){
		if(applicationData.getOneThread() == null){
    		applicationData.setOneThread(new Thread(){	      	
			public void run(){
				try{
					//applicationData.hasLocationIndexBeenSet = false;
					JFileChooser fc;
			    	if(workingDirectoryField.getText().length()!=0)
			    		//use the working directory to start searching for the step1settings
			    		fc = new JFileChooser(workingDirectoryField.getText());
			    	else{
			    		//if working directory not set then look at the Sirius Settings file
			    		String lastStep1SettingsLocation = SiriusSettings.getInformation("LastStep1SettingsFileLocation: ");
			    		if(lastStep1SettingsLocation == null)
			    			fc = new JFileChooser();
			    		else
			    			fc = new JFileChooser(lastStep1SettingsLocation);
			    	}			    	
			        FileNameExtensionFilter filter = new FileNameExtensionFilter(
			            "Step 1 Settings Files", "step1settings");
			        fc.setFileFilter(filter);			        			        			    	
					int returnVal = fc.showOpenDialog(parent);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
			            SiriusSettings.updateInformation("LastStep1SettingsFileLocation: ", file.getAbsolutePath());
			            statusPane.setText("Reading " + file.getAbsolutePath() + "...");
		            	BufferedReader in = new BufferedReader(new FileReader (file.getAbsolutePath()));
		            	String line;		            	
		            	final int searchLineNum = 16;
		            	String[] searchLine = new String[searchLineNum];
		            	searchLine[0] = "Step 1 Working Directory: ";
		            	searchLine[1] = "Step 1 Positive: ";
		            	searchLine[2] = "Step 1 Negative: ";
		            	searchLine[3] = "Step 1 Positive Dataset 1 From Sequence #: ";
		            	searchLine[4] = "Step 1 Positive Dataset 1 To Sequence #: ";
		            	searchLine[5] = "Step 1 Positive Dataset 2 From Sequence #: ";
		            	searchLine[6] = "Step 1 Positive Dataset 2 To Sequence #: ";
		            	searchLine[7] = "Step 1 Positive Dataset 3 From Sequence #: ";
		            	searchLine[8] = "Step 1 Positive Dataset 3 To Sequence #: ";
		            	searchLine[9] = "Step 1 Negative Dataset 1 From Sequence #: ";
		            	searchLine[10] = "Step 1 Negative Dataset 1 To Sequence #: ";
		            	searchLine[11] = "Step 1 Negative Dataset 2 From Sequence #: ";
		            	searchLine[12] = "Step 1 Negative Dataset 2 To Sequence #: ";
		            	searchLine[13] = "Step 1 Negative Dataset 3 From Sequence #: ";
		            	searchLine[14] = "Step 1 Negative Dataset 3 To Sequence #: ";
		            	searchLine[15] = "Sequence Type: ";
		            	positiveStep1TableModel.setDataToNull();
		            	negativeStep1TableModel.setDataToNull();		            	
		            	boolean foundAtLeastOne = false;
		            	while ((line = in.readLine()) != null){
		            		for(int x = 0; x < searchLineNum; x++){
		            			String absolutePath;
		            			if(line.indexOf(searchLine[x])!=-1){
		            				switch(x){	            				
		            				case 0: 
		            					workingDirectoryField.setText(
		            						line.substring(searchLine[x].length()));
		            					break;
		            				case 1: 
		            					absolutePath = line.substring(searchLine[x].length() + 15,
		            						line.indexOf("No of Sequences: ") - 1);
		            					addFileMethod(absolutePath,positiveTable,positiveStep1TableModel);
										break;
									case 2:
										absolutePath = line.substring(searchLine[x].length() + 15,
											line.indexOf("No of Sequences: ") - 1);
				            			addFileMethod(absolutePath,negativeTable,negativeStep1TableModel);
										break;
									case 3:
										positiveDataset1FromField.setText(line.substring(
											searchLine[x].length()));
										break;
									case 4:
										positiveDataset1ToField.setText(line.substring(
											searchLine[x].length()));
										break;
									case 5:
										positiveDataset2FromField.setText(line.substring(
											searchLine[x].length()));
										break;
									case 6:
										positiveDataset2ToField.setText(line.substring(
											searchLine[x].length()));
										break;
									case 7:
										positiveDataset3FromField.setText(line.substring(
											searchLine[x].length()));
										break;
									case 8:
										positiveDataset3ToField.setText(line.substring(
											searchLine[x].length()));
										break;
									case 9:
										negativeDataset1FromField.setText(line.substring(
											searchLine[x].length()));
										break;
									case 10:
										negativeDataset1ToField.setText(line.substring(
											searchLine[x].length()));
										break;
									case 11:
										negativeDataset2FromField.setText(line.substring(
											searchLine[x].length()));
										break;
									case 12:
										negativeDataset2ToField.setText(line.substring(
											searchLine[x].length()));
										break;
									case 13:
										negativeDataset3FromField.setText(line.substring(
											searchLine[x].length()));
										break;
									case 14:
										negativeDataset3ToField.setText(line.substring(
											searchLine[x].length()));
										break;
									case 15:
										String tempString = line.substring(searchLine[x].length());
										if(tempString.indexOf("DNA") != -1){
											dnaSequenceRadioButton.setSelected(true);
											proteinSequenceRadioButton.setSelected(false);
										}else if(tempString.indexOf("PROTEIN") != -1){
											dnaSequenceRadioButton.setSelected(false);
											proteinSequenceRadioButton.setSelected(true);
										}
										break;
		            				}			            				
		            				foundAtLeastOne = true;	            				
		            			}
		            		}	
		            	}
		            	if(foundAtLeastOne){
		            		statusPane.setText("Step 1 Settings Loaded From " + file.getAbsolutePath());
		            		updateTotalSequences(positiveStep1TableModel,positiveSequenceLabel);
    						updateTotalSequences(negativeStep1TableModel,negativeSequenceLabel); 
		            	}		            		
		            	else
		            		JOptionPane.showMessageDialog(parent,file.getAbsolutePath() + 
		            			" does not contains Step 1 Settings","Load Step 1 Settings", 
		            			JOptionPane.WARNING_MESSAGE);
		            	in.close();					            		            		
		    		}
				}    
	  			catch(Exception e){
	  				JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
	  				e.printStackTrace();}
	  			applicationData.setOneThread(null);
			}});
      		applicationData.getOneThread().setPriority(Thread.MIN_PRIORITY); // UI has most priority
      		applicationData.getOneThread().start();
    	}
    	else{
     		JOptionPane.showMessageDialog(parent,"Can't load step 1 settings now,\n"
      			+ "currently busy with other IO","Load Step 1 Settings", JOptionPane.WARNING_MESSAGE);
    	}
	}    
	
	public void mouseClicked(MouseEvent e){
		if(e.getClickCount() == 2){	//double clicked
			if(e.getSource().equals(positiveTable)){
		      int row = positiveTable.getSelectedRow();		      
		      setIndex(((Step1TableModel)positiveTable.getModel()).getFirstCol(row),
		      	(Step1TableModel)positiveTable.getModel(), applicationData, false, false, row);
			}else if(e.getSource().equals(negativeTable)){
				int row = negativeTable.getSelectedRow();		      
		      setIndex(((Step1TableModel)negativeTable.getModel()).getFirstCol(row),
		      	(Step1TableModel)negativeTable.getModel(), applicationData, false, false, row);	  
			}
		}	      
	}
	public void mouseExited(MouseEvent e){
	}
	public void mouseEntered(MouseEvent e){
	}
	public void mouseReleased(MouseEvent e){
	}
	public void mousePressed(MouseEvent e){
	}	
    public void actionPerformed(ActionEvent ae){
    	if(ae.getSource().equals(dnaSequenceRadioButton)){
    		dnaSequenceRadioButton.setSelected(true);
    		proteinSequenceRadioButton.setSelected(false);
    	}else if(ae.getSource().equals(proteinSequenceRadioButton)){
    		dnaSequenceRadioButton.setSelected(false);
    		proteinSequenceRadioButton.setSelected(true);
    	}else if(ae.getSource().equals(addPositiveButton)){
    		if(applicationData.getOneThread() == null){
    			applicationData.setOneThread(new Thread(){
    				public void run(){
    					addFileMethod(positiveTable,positiveStep1TableModel);	
						applicationData.setOneThread(null);	
						updateTotalSequences(positiveStep1TableModel,positiveSequenceLabel);
			    	}
    			});
    			applicationData.getOneThread().setPriority(Thread.MIN_PRIORITY);
    			applicationData.getOneThread().start();
    		}else{
    			JOptionPane.showMessageDialog(parent,"Can't add +ve file now,\n"
	      			+ "currently busy with other IO","Add +ve File", JOptionPane.WARNING_MESSAGE);
    		}			
    	}
   	 	else if(ae.getSource().equals(addNegativeButton)){
   	 		if(applicationData.getOneThread() == null){
    			applicationData.setOneThread(new Thread(){
    				public void run(){
    					addFileMethod(negativeTable,negativeStep1TableModel);			   	 		
			   	 		applicationData.setOneThread(null);			   	 		
    					updateTotalSequences(negativeStep1TableModel,negativeSequenceLabel);
    				}
    			});
    			applicationData.getOneThread().setPriority(Thread.MIN_PRIORITY);
    			applicationData.getOneThread().start();
    		}else{
    			JOptionPane.showMessageDialog(parent,"Can't add -ve file now,\n"
	      			+ "currently busy with other IO","Add -ve File", JOptionPane.WARNING_MESSAGE);
    		}   	 		
    	}else if(ae.getSource().equals(this.deletePositiveButton)){    		
    		int[] selectedIndex = this.positiveTable.getSelectedRows();
    		if(selectedIndex.length > 0){
    			for(int i = selectedIndex.length - 1; i >= 0; i--) this.positiveStep1TableModel.remove(selectedIndex[i]);
    			updateTotalSequences(this.positiveStep1TableModel, this.positiveSequenceLabel);				
    		}			    		
    		else{
    			JOptionPane.showMessageDialog(parent,"Please select a file!","No file selected",JOptionPane.INFORMATION_MESSAGE); 
    		}
    	}else if(ae.getSource().equals(deleteNegativeButton)){
    		int[] selectedIndex = this.negativeTable.getSelectedRows();
    		if(selectedIndex.length > 0){
    			for(int i = selectedIndex.length - 1; i >= 0; i--) this.negativeStep1TableModel.remove(selectedIndex[i]);
    			updateTotalSequences(this.negativeStep1TableModel, this.negativeSequenceLabel);				
    		}			    		
    		else{
    			JOptionPane.showMessageDialog(parent,"Please select a file!","No file selected",JOptionPane.INFORMATION_MESSAGE); 
    		}    	    					     		
    	}else if(ae.getSource().equals(workingDirectoryButton)){
    		JFileChooser chooser;
    		if(workingDirectoryField.getText().length()!=0)
	    		chooser = new JFileChooser(workingDirectoryField.getText());
	    	else{//AHFU_TEMP: This is for when the workinDirectory is not set and I want to shortcut
	    		//chooser = new JFileChooser("//media//sda2//Working Directory for HYP//Sirius//Sirius Output Directory//Output");
	    	String lastLocation = SiriusSettings.getInformation("LastWorkingDirectoryLocation: ");
    		if(lastLocation == null)
    			chooser = new JFileChooser();
    		else
    			chooser = new JFileChooser(lastLocation);
	    	}	    		
    		//JFileChooser chooser = new JFileChooser();
    		//chooser.setCurrentDirectory(new File("."));
    		chooser.setDialogTitle("Set Working Directory");
    		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    		chooser.setAcceptAllFileFilterUsed(false);
    		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
    			workingDirectoryField.setText(chooser.getSelectedFile().toString());  
    			SiriusSettings.updateInformation("LastWorkingDirectoryLocation: ", chooser.getSelectedFile().getAbsolutePath());
		   	}
		    else{
		    	//no selection
		    }
    	}    	
    	else if(ae.getSource().equals(saveSettingButton)){
			saveSettings();	    			
    	}	
    	else if(ae.getSource().equals(loadSettingButton)){    		
    		loadSettings();    		   		
    	}
    	else if(ae.getSource().equals(nextStepButton)){
    		nextStep();    		
    	}
    }                  
    	
    private void nextStep(){    
    	try{
			//Validate all inputs  
			//check working directory
			if(workingDirectoryField.getText().length() == 0){
				JOptionPane.showMessageDialog(parent,"Please set the working Directory!",
						"ERROR",JOptionPane.ERROR_MESSAGE);    
				workingDirectoryButton.requestFocusInWindow();				
				throw new NumberFormatException();
			}    			    			
			validateField(positiveDataset1FromField.getText(),"Dataset 1 From Field",
				positiveDataset1FromField);  
			validateField(positiveDataset1ToField.getText(),"Dataset 1 To Field",
				positiveDataset1ToField);  
			validateField(negativeDataset1FromField.getText(),"Dataset 1 From Field",
				negativeDataset1FromField);  
			validateField(negativeDataset1ToField.getText(),"Dataset 1 To Field",
				negativeDataset1ToField); 
			ensureFromToInSequence(positiveDataset1FromField,positiveDataset1ToField,"Dataset 1");
			ensureFromToInSequence(negativeDataset1FromField,negativeDataset1ToField,"Dataset 1");
					
			if(positiveDataset2FromField.getText().length() > 0 || 
				positiveDataset2ToField.getText().length() > 0 ||
				negativeDataset2FromField.getText().length() > 0 || 
				negativeDataset2ToField.getText().length() > 0 ){
					validateField(positiveDataset2FromField.getText(),"Dataset 2 From Field",
		    			positiveDataset2FromField);
		    		validateField(positiveDataset2ToField.getText(),"Dataset 2 To Field",
		    			positiveDataset2ToField);
		    		validateField(negativeDataset2FromField.getText(),"Dataset 2 From Field",
						negativeDataset2FromField);
					validateField(negativeDataset2ToField.getText(),"Dataset 2 To Field",
						negativeDataset2ToField);
					ensureFromToInSequence(positiveDataset2FromField,positiveDataset2ToField,"Dataset 2");
					ensureFromToInSequence(negativeDataset2FromField,negativeDataset2ToField,"Dataset 2");
	    	}
	    	
	    	if(positiveDataset3FromField.getText().length() > 0 || 
				positiveDataset3ToField.getText().length() > 0 ||
				negativeDataset3FromField.getText().length() > 0 || 
				negativeDataset3ToField.getText().length() > 0 ){
					validateField(positiveDataset3FromField.getText(),"Dataset 3 From Field",
		    			positiveDataset3FromField);
		    		validateField(positiveDataset3ToField.getText(),"Dataset 3 To Field",
		    			positiveDataset3ToField);	    			    	 	    		
		    		validateField(negativeDataset3FromField.getText(),"Dataset 3 From Field",
		    			negativeDataset3FromField);
		    		validateField(negativeDataset3ToField.getText(),"Dataset 3 To Field",
		    			negativeDataset3ToField);
		    		ensureFromToInSequence(positiveDataset3FromField,positiveDataset3ToField,"Dataset 3");
		    		ensureFromToInSequence(negativeDataset3FromField,negativeDataset3ToField,"Dataset 3");
	    	}
	    	int fromPos1 = -1;
	    	int fromPos2 = -1;
	    	int fromPos3 = -1;
	    	
	    	int toPos1 = -1;
	    	int toPos2 = -1;
	    	int toPos3 = -1;
	    	
	    	int fromNeg1 = -1;
	    	int fromNeg2 = -1;
	    	int fromNeg3 = -1;
	    	
	    	int toNeg1 = -1;
	    	int toNeg2 = -1;
	    	int toNeg3 = -1;
	    	
	    	if(positiveDataset1FromField.getText().length() > 0)
	    		fromPos1 = Integer.parseInt(positiveDataset1FromField.getText());
	    	if(positiveDataset2FromField.getText().length() > 0)
	    		fromPos2 = Integer.parseInt(positiveDataset2FromField.getText());
	    	if(positiveDataset3FromField.getText().length() > 0)
	    		fromPos3 = Integer.parseInt(positiveDataset3FromField.getText());
	    	
	    	if(positiveDataset1ToField.getText().length() > 0)
	    		toPos1 = Integer.parseInt(positiveDataset1ToField.getText());
	    	if(positiveDataset2ToField.getText().length() > 0)
	    		toPos2 = Integer.parseInt(positiveDataset2ToField.getText());
	    	if(positiveDataset3ToField.getText().length() > 0)
	    		toPos3 = Integer.parseInt(positiveDataset3ToField.getText());
	    	
	    	if(negativeDataset1FromField.getText().length() > 0)
	    		fromNeg1 = Integer.parseInt(negativeDataset1FromField.getText());
	    	if(negativeDataset2FromField.getText().length() > 0)	
	    		fromNeg2 = Integer.parseInt(negativeDataset2FromField.getText());
	    	if(negativeDataset3FromField.getText().length() > 0)
	    		fromNeg3 = Integer.parseInt(negativeDataset3FromField.getText());
	    		
	    	if(negativeDataset1ToField.getText().length() > 0)
	    		toNeg1 = Integer.parseInt(negativeDataset1ToField.getText());
	    	if(negativeDataset2ToField.getText().length() > 0)
	    		toNeg2 = Integer.parseInt(negativeDataset2ToField.getText());
	    	if(negativeDataset3ToField.getText().length() > 0)
	    		toNeg3 = Integer.parseInt(negativeDataset3ToField.getText()); 	  
			checkForSequenceOverlap(fromPos1,fromPos2,fromPos3,toPos1,toPos2,toPos3,fromNeg1,fromNeg2,fromNeg3,
				toNeg1,toNeg2,toNeg3);
			//Ensure that the files have same +1_Index value,
			//Either all -1 or all non -1
			int plusOneIndex = checkPlusOneIndex(positiveStep1TableModel,negativeStep1TableModel);
			if(plusOneIndex == -1){
				applicationData.isLocationIndexMinusOne = true;
			}else if(plusOneIndex > 0){				
				applicationData.isLocationIndexMinusOne = false;
			}else{
				JOptionPane.showMessageDialog(parent,
					"Ensure that all files have either -1 or non -1 for +1_Index!",
					"ERROR",JOptionPane.ERROR_MESSAGE);				
				return;
			}			
			if(dnaSequenceRadioButton.isSelected() == false && proteinSequenceRadioButton.isSelected() == false){
				JOptionPane.showMessageDialog(parent,
					"Please select the sequence type!",
					"ERROR",JOptionPane.ERROR_MESSAGE);				
				return;
			}
			
			//After ensuring all validation is valid.
			//Do whatever is needed after which
			//Write all the datas into ApplicationData 
			applicationData.setWorkingDirectory(workingDirectoryField.getText());			
			applicationData.setPositiveStep1TableModel(positiveStep1TableModel);
			applicationData.setNegativeStep1TableModel(negativeStep1TableModel);
			applicationData.setDatasetsValue(fromPos1,toPos1,fromPos2,toPos2,fromPos3,toPos3,fromNeg1,toNeg1,
				fromNeg2,toNeg2,fromNeg3,toNeg3);			
			if(dnaSequenceRadioButton.isSelected()){
				applicationData.setSequenceType("DNA");
				//0 is for identity matrix and +1
				applicationData.setScoringMatrixIndex(0);
				applicationData.setCountingStyleIndex(0);
			}				
			else
				applicationData.setSequenceType("PROTEIN");
			//Also store the sequenceLengthInformation
			updateSequenceInformation();
			tabbedPane.setSelectedIndex(1);
			tabbedPane.setEnabledAt(1,true);
			tabbedPane.setEnabledAt(0,false);
		}
		catch(NumberFormatException e){
			//Need not do anything here because warning already showed before here - irritating to show too much warnings			
			} 
		catch(Exception ex){
			//Need not do anything here because warning already showed before here - irritating to show too much warnings
			}	
    }
    //Note that this is introduced in Sirius v2.0 for use in genetic algorithm
    public void updateSequenceInformation(){
    	applicationData.resetLengthInformation();//reset before you do anything
    	int positiveFromInt = applicationData.getPositiveDataset1FromField(); 
    	int positiveToInt = applicationData.getPositiveDataset1ToField();
    	int negativeFromInt = applicationData.getNegativeDataset1FromField();
    	int negativeToInt = applicationData.getNegativeDataset1ToField();     	
    	Step1TableModel positiveTableModel = applicationData.getPositiveStep1TableModel();
    	Step1TableModel negativeTableModel = applicationData.getNegativeStep1TableModel();
    	FastaFileManipulation fastaFile = new FastaFileManipulation(positiveTableModel,
				negativeTableModel,positiveFromInt,positiveToInt,negativeFromInt,negativeToInt,
				applicationData.getWorkingDirectory());
    	FastaFormat fastaFormat;
		int lineCounter = 0;
    	while((fastaFormat = fastaFile.nextSequence("pos"))!=null){
    		int targetLocation = fastaFormat.getIndexLocation();
    		String sequence = fastaFormat.getSequence();
    		if(targetLocation == -1){//+1_Index is -1
    			if(sequence.length() > applicationData.getLongestSequenceLength())
    				applicationData.setLongestSequenceLength(sequence.length());
    			if(sequence.length() < applicationData.getShortestSequenceLength())
    				applicationData.setShortestSequenceLength(sequence.length());
    			applicationData.addToTotalSequenceLength(sequence.length());
    		}else{//+1_Index is non -1
    			int upstream = targetLocation;
    			int downstream = sequence.length() - targetLocation;
    			if(upstream > applicationData.getLongestUpstream())
    				applicationData.setLongestUpstream(upstream);
    			if(upstream < applicationData.getShortestUpstream())
    				applicationData.setShortestUpstream(upstream);
    			if(downstream > applicationData.getLongestDownstream())
    				applicationData.setLongestDownstream(downstream);
    			if(downstream < applicationData.getShortestDownstream())
    				applicationData.setShortestDownstream(downstream);
    			applicationData.addToTotalUpstream(upstream);
    			applicationData.addToTotalDownstream(downstream);
    		}
 			lineCounter++;     
    	}   
    	while((fastaFormat = fastaFile.nextSequence("neg"))!=null){
    		int targetLocation = fastaFormat.getIndexLocation();
    		String sequence = fastaFormat.getSequence();
    		if(targetLocation == -1){//+1_Index is -1
    			if(sequence.length() > applicationData.getLongestSequenceLength())
    				applicationData.setLongestSequenceLength(sequence.length());
    			if(sequence.length() < applicationData.getShortestSequenceLength())
    				applicationData.setShortestSequenceLength(sequence.length());
    			applicationData.addToTotalSequenceLength(sequence.length());
    		}else{//+1_Index is non -1
    			int upstream = targetLocation;
    			int downstream = sequence.length() - targetLocation;
    			if(upstream > applicationData.getLongestUpstream())
    				applicationData.setLongestUpstream(upstream);
    			if(upstream < applicationData.getShortestUpstream())
    				applicationData.setShortestUpstream(upstream);
    			if(downstream > applicationData.getLongestDownstream())
    				applicationData.setLongestDownstream(downstream);
    			if(downstream < applicationData.getShortestDownstream())
    				applicationData.setShortestDownstream(downstream);
    			applicationData.addToTotalUpstream(upstream);
    			applicationData.addToTotalDownstream(downstream);
    		}
 			lineCounter++;     
    	}
    	if(lineCounter != applicationData.getTotalSequences(1)){  
    		JOptionPane.showMessageDialog(null,"lineCount != applicationData.getTotalSequences(1)",
				"Error",JOptionPane.ERROR_MESSAGE);     		
    	}    		
    }
    private int checkPlusOneIndex(Step1TableModel positiveStep1TableModel,
    	Step1TableModel negativeStep1TableModel){
    	int plusOneIndex = positiveStep1TableModel.getPlusOneIndex(0);
    	for(int x = 1; x < positiveStep1TableModel.getRowCount(); x++){
    		if( (plusOneIndex > 0 && positiveStep1TableModel.getPlusOneIndex(x) < 0) ||
    			(plusOneIndex < 0 && positiveStep1TableModel.getPlusOneIndex(x) > 0))
    				return -99; //inconsistency found   			
    	}
    	for(int x = 0; x < negativeStep1TableModel.getRowCount(); x++){
    		if( (plusOneIndex > 0 && negativeStep1TableModel.getPlusOneIndex(x) < 0) ||
    			(plusOneIndex < 0 && negativeStep1TableModel.getPlusOneIndex(x) > 0))
    				return -99; //inconsistency found   			   
    	}
    	return plusOneIndex;
    }
    private void checkForSequenceOverlap(int fromPos1,int fromPos2,int fromPos3,
    	int toPos1,int toPos2,int toPos3,int fromNeg1,int fromNeg2,int fromNeg3,
    	int toNeg1,int toNeg2,int toNeg3) 
    		throws NumberFormatException,Exception{
    			
    	int positiveSequenceCount = positiveStep1TableModel.getTotalSequences();
    	int negativeSequenceCount = negativeStep1TableModel.getTotalSequences();
    	//Check Positive Dataset 1 with Positive Dataset 2    	    
    	if( (fromPos2 != -1) &&
    		((toPos1 > fromPos2 && toPos2 > fromPos1) || 
    		(toPos2 > fromPos1 && toPos1 > fromPos2) || 
    		(fromPos1 == toPos2) || (fromPos2 == toPos1)) ){   
    			JOptionPane.showMessageDialog(parent,
    				"There are overlap between positive Dataset 1 and Dataset 2",
    				"ERROR",JOptionPane.ERROR_MESSAGE);
    			positiveDataset1FromField.requestFocusInWindow();
    			throw new Exception(); 		   	
    	}    	    	    	
    	//Check Positive Dataset 1 with Positive Dataset 3
    	if( (fromPos3 != -1) && 
    		((toPos1 > fromPos3 && toPos3 > fromPos1) || 
    		(toPos3 > fromPos1 && toPos1 > fromPos3) || 
    		(fromPos1 == toPos3) || (fromPos3 == toPos1)) ){  
    			JOptionPane.showMessageDialog(parent,
    				"There are overlap between positive Dataset 1 and Dataset 3",
    				"ERROR",JOptionPane.ERROR_MESSAGE);
    			positiveDataset1FromField.requestFocusInWindow();
    			throw new Exception(); 	  		
    	}
    	//Check Positive Dataset 2 with Positive Dataset 3
    	if( (fromPos2 != -1 && fromPos3 != -1) && 
    		((toPos2 > fromPos3 && toPos3 > fromPos2) || 
    		(toPos3 > fromPos2 && toPos2 > fromPos3) || 
    		(fromPos2 == toPos3) || (fromPos3 == toPos2)) ){  
    			JOptionPane.showMessageDialog(parent,
    				"There are overlap between positive Dataset 2 and Dataset 3",
    				"ERROR",JOptionPane.ERROR_MESSAGE);
    			positiveDataset2FromField.requestFocusInWindow();
    			throw new Exception(); 	  		
    	}
    	
    	//Check Negative Dataset 1 with Negative Dataset 2    	
    	if( (fromNeg2 != -1) && 
    		((toNeg1 > fromNeg2 && toNeg2 > fromNeg1) || 
    		(toNeg2 > fromNeg1 && toNeg1 > fromNeg2) || 
    		(fromNeg1 == toNeg2) || (fromNeg2 == toNeg1)) ){  
    			JOptionPane.showMessageDialog(parent,
    				"There are overlap between negative Dataset 1 and Dataset 2",
    				"ERROR",JOptionPane.ERROR_MESSAGE);
    			negativeDataset1FromField.requestFocusInWindow();
    			throw new Exception(); 	  		
    	}
    	//Check Negative Dataset 1 with Negative Dataset 3
    	if( (fromNeg3 != -1) && 
    		((toNeg1 > fromNeg3 && toNeg3 > fromNeg1) || 
    		(toNeg3 > fromNeg1 && toNeg1 > fromNeg3) || 
    		(fromNeg1 == toNeg3) || (fromNeg3 == toNeg1)) ){  
    			JOptionPane.showMessageDialog(parent,
    				"There are overlap between negative Dataset 1 and Dataset 3",
    				"ERROR",JOptionPane.ERROR_MESSAGE);
    			negativeDataset1FromField.requestFocusInWindow();
    			throw new Exception(); 	  		
    	}
    	//Check Negative Dataset 2 with Negative Dataset 3
    	if( (fromNeg2 != -1 && fromNeg3 != -1) &&
    		((toNeg2 > fromNeg3 && toNeg3 > fromNeg2) || 
    		(toNeg3 > fromNeg2 && toNeg2 > fromNeg3) || 
    		(fromNeg2 == toNeg3) || (fromNeg3 == toNeg2)) ){  
    			JOptionPane.showMessageDialog(parent,
    				"There are overlap between negative Dataset 2 and Dataset 3",
    				"ERROR",JOptionPane.ERROR_MESSAGE);
    			negativeDataset2FromField.requestFocusInWindow();
    			throw new Exception(); 	  		
    	}
    	
    	//Check that indicate range is not beyond available sequences
    	if(toPos1 > positiveSequenceCount || 
    		(toPos2 > positiveSequenceCount && toPos2 != -1) || 
    			(toPos3 > positiveSequenceCount && toPos3 != -1)){
    		JOptionPane.showMessageDialog(parent,
    			"Indicated range is beyond available sequence number","ERROR",
    			JOptionPane.ERROR_MESSAGE);
    		if(toPos1 > toPos2 && toPos1 > toPos3)
    			positiveDataset1ToField.requestFocusInWindow();    		
    		else if(toPos2 > toPos3 && toPos2 > toPos1)    		
    			positiveDataset2ToField.requestFocusInWindow();
    		else
    			positiveDataset3ToField.requestFocusInWindow();
    		throw new Exception();
    	}
    	
    	//Check that indicate range is not beyond available sequences
    	if(toNeg1 > negativeSequenceCount || 
    		(toNeg2 > negativeSequenceCount && toNeg2 != -1) || 
    			(toNeg3 > negativeSequenceCount && toNeg3 != -1)){
    		JOptionPane.showMessageDialog(parent,
    			"Indicated range is beyond available sequence number","ERROR",
    			JOptionPane.ERROR_MESSAGE);
    		if(toNeg1 > toNeg2 && toNeg1 > toNeg3)
    			negativeDataset1ToField.requestFocusInWindow();    		
    		else if(toNeg2 > toNeg3 && toNeg2 > toNeg1)    		
    			negativeDataset2ToField.requestFocusInWindow();
    		else
    			negativeDataset3ToField.requestFocusInWindow();
    		throw new Exception();
    	}
    	
    	//Check that indicate range fully utilize available sequences
    	if((toPos1 == positiveSequenceCount || toPos2 == positiveSequenceCount || toPos3 == positiveSequenceCount) && 
    			(toNeg1 == negativeSequenceCount || toNeg2 == negativeSequenceCount || toNeg3 == negativeSequenceCount)){
    		//Fully Utilize
    	}else{
    		int response = JOptionPane.showConfirmDialog(this.parent, "Sequences not fully utilize. Do you still want to continue?");
    		if(response != JOptionPane.YES_OPTION){
    			throw new Exception();
    		}
    	}
    }

	//checks that fromField is not greater than toField    	
    private void ensureFromToInSequence(JTextField fromField, JTextField toField,String name)
    	throws NumberFormatException{    		
    	try{
    		int from = Integer.parseInt(fromField.getText());
    		int to = Integer.parseInt(toField.getText());
    		if(from > to){
    			JOptionPane.showMessageDialog(parent,
    				"From Field is greater than To Field For " + name, "ERROR",
    				JOptionPane.ERROR_MESSAGE);
    			throw new NumberFormatException();
    		}
    	}
    	catch(NumberFormatException e){
    		fromField.requestFocusInWindow();
    		throw new NumberFormatException();
    	}
    }
    
    //check that field consists of numbers only
    private void validateField(String value,String name,JComponent component) throws NumberFormatException{
    	try{
    		int temp = Integer.parseInt(value);
    		if(temp <= 0)
    			throw new Exception();
    	}catch(NumberFormatException e){
   			JOptionPane.showMessageDialog(parent,"Input only numbers into " + name,"ERROR",
   				JOptionPane.ERROR_MESSAGE);
   			component.requestFocusInWindow();
   			throw new NumberFormatException();
   		}catch(Exception e){
   			JOptionPane.showMessageDialog(parent,name + "'s value must be > 0.","ERROR",
   	   				JOptionPane.ERROR_MESSAGE);
   	   			component.requestFocusInWindow();
   	   			throw new NumberFormatException();
   		}
    }    
}