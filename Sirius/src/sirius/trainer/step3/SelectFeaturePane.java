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
package sirius.trainer.step3;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.io.*;
import java.util.StringTokenizer;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;

import sirius.main.ApplicationData;
import sirius.trainer.main.*;

import weka.gui.*;
import weka.core.Attribute;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.SerializedInstancesLoader;
import weka.core.converters.ConverterUtils;
import weka.core.converters.Loader;

public class SelectFeaturePane extends JComponent implements ActionListener{
	static final long serialVersionUID = sirius.Sirius.version;
	private JInternalFrame parent;
	private StatusPane statusPane;
	private ApplicationData applicationData;
	private JButton removeAttributeButton;	
	private JButton applyFilterButton;
	private JButton undoButton;
	private JButton saveArffButton;
	private JButton saveArffPosOnlyButton;
	private JButton saveArffNegOnlyButton;
	private AttributeSummaryPanel attributeSummaryPanel;

	private MyAttributeSelectionPanel myAttributeSelectionPanel;
	private GenericObjectEditor filterEditor;
	private AttributeVisualizationPanel attributeVisualizePanel;
	
	private JLabel numberOfInstancesLabel;
	private JLabel numberOfFeaturesLabel;
	
	private JButton previousStepButton;
	private JButton nextStepButton;
	private JTabbedPane tabbedPane;
	
	/** Keeps track of undo points */
  	private File[] m_tempUndoFiles = new File[20]; // set number of undo ops here

  	/** The next available slot for an undo point */
  	private int m_tempUndoIndex = 0;
	
    public SelectFeaturePane(JInternalFrame parent,JTabbedPane tabbedPane,ApplicationData applicationData) {
    	this.parent = parent;
    	this.applicationData = applicationData;
    	this.statusPane = applicationData.getStatusPane(); 
    	this.tabbedPane = tabbedPane;   	
    	
    	JPanel north = new JPanel(new BorderLayout());
    	north.setBorder(BorderFactory.createTitledBorder("Filter Features"));
    	applyFilterButton = new JButton("Apply Filter");  
    	applyFilterButton.addActionListener(this); 
    	filterEditor = new GenericObjectEditor();
		/** Filter configuration */
		PropertyPanel filterPanel = new PropertyPanel(filterEditor);		
		filterEditor.setClassType(weka.filters.Filter.class);
		north.add(filterPanel,BorderLayout.CENTER);	
		undoButton = new JButton("Undo (Remove/Filter)");
    	undoButton.setEnabled(false);
    	undoButton.addActionListener(this);
		JPanel filterButtonsPanel = new JPanel(new GridLayout(1,2));
		filterButtonsPanel.add(applyFilterButton);
		north.add(filterButtonsPanel,BorderLayout.EAST);
		
    	
    	JPanel center = new JPanel(new GridLayout(1,2));    
		//center_left   
		JPanel center_left = new JPanel(new BorderLayout()); 		    					
		JPanel instancesSummaryPanel = new JPanel(new GridLayout(1,2));
		instancesSummaryPanel.setBorder(BorderFactory.createTitledBorder("Current File"));
		numberOfInstancesLabel = new JLabel(" Instances: ");
		numberOfFeaturesLabel = new JLabel("Features: ");
		instancesSummaryPanel.add(numberOfInstancesLabel);
		instancesSummaryPanel.add(numberOfFeaturesLabel);
		JPanel center_left_center = new JPanel(new BorderLayout());
		center_left_center.setBorder(BorderFactory.createTitledBorder("Features"));
    	myAttributeSelectionPanel = new MyAttributeSelectionPanel();    	
    	center_left_center.add(myAttributeSelectionPanel,BorderLayout.CENTER); 
		JPanel removeAttributePanel = new JPanel(new GridLayout(1,1));
		removeAttributePanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));    		 	    	
    	removeAttributeButton = new JButton("Remove Marked Features");
    	removeAttributeButton.setEnabled(false);
    	removeAttributeButton.setToolTipText("Remove selected attributes.");
    	removeAttributeButton.addActionListener(this);
    	removeAttributePanel.add(removeAttributeButton);    	
    	center_left_center.add(removeAttributePanel,BorderLayout.SOUTH);
    	center_left.add(instancesSummaryPanel,BorderLayout.NORTH);
    	center_left.add(center_left_center,BorderLayout.CENTER);
    	this.saveArffButton = new JButton("Save as Arff (All)");
    	this.saveArffButton.addActionListener(this);
    	this.saveArffNegOnlyButton = new JButton("Save as Arff (-ve Only)");
    	this.saveArffNegOnlyButton.addActionListener(this);
    	this.saveArffPosOnlyButton = new JButton("Save as Arff (+ve Only)");
    	this.saveArffPosOnlyButton.addActionListener(this);
    	JPanel outputPanel = new JPanel(new GridLayout(1,3));
    	outputPanel.setBorder(BorderFactory.createTitledBorder("Output")); 
    	outputPanel.add(this.saveArffButton);
    	outputPanel.add(this.saveArffPosOnlyButton);
    	outputPanel.add(this.saveArffNegOnlyButton);
    	center_left.add(outputPanel,BorderLayout.SOUTH);
    	center.add(center_left);
    	
    	
    	JPanel center_right = new JPanel(new GridLayout(2,1));
    	attributeSummaryPanel = new AttributeSummaryPanel();
    	attributeSummaryPanel.setBorder(BorderFactory.createTitledBorder("Selected Features"));    	
    	attributeVisualizePanel = new AttributeVisualizationPanel();
    	center_right.add(attributeSummaryPanel);
    	center_right.add(attributeVisualizePanel);
    	
    	center.add(center_right);
    	
    	GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
    	JPanel south = new JPanel(gridbag);
    	south.setBorder(BorderFactory.createEmptyBorder(10,5,0,5));
    	previousStepButton = new JButton("<<< BACK");
    	previousStepButton.addActionListener(this);
    	c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        gridbag.setConstraints(previousStepButton,c);
    	
        c.weightx = 3.0;
        c.weighty = 1.0;
        gridbag.setConstraints(this.undoButton,c);
    	nextStepButton = new JButton("NEXT >>>");	
    	nextStepButton.addActionListener(this);
    	c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        gridbag.setConstraints(nextStepButton,c);
    	south.add(previousStepButton);	
    	south.add(this.undoButton);
    	south.add(nextStepButton);
    	
    	setLayout(new BorderLayout());
    	add(center, BorderLayout.CENTER);
    	add(north, BorderLayout.NORTH);
    	add(south, BorderLayout.SOUTH);
    	
    	myAttributeSelectionPanel.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
			  if (!e.getValueIsAdjusting()) {	  
			    ListSelectionModel lm = (ListSelectionModel) e.getSource();
			    for (int i = e.getFirstIndex(); i <= e.getLastIndex(); i++) {
			      if (lm.isSelectedIndex(i)) {
				attributeSummaryPanel.setAttribute(i);
				attributeVisualizePanel.setAttribute(i);
				break;
			      }
			    }
			  }
			}
		    });    			
    }   
    public void setDataset1Instances(){    	
    	try{
    		Instances dataset1Instances = new Instances(new BufferedReader(new FileReader(
	    		applicationData.getWorkingDirectory() + File.separator + "Dataset1.arff")));
	    	applicationData.setDataset1Instances(dataset1Instances);
	    	myAttributeSelectionPanel.setInstances(null,dataset1Instances);
	    	attributeSummaryPanel.setInstances(dataset1Instances);
	    	attributeSummaryPanel.setAttribute(0);
	    	attributeVisualizePanel.setInstances(dataset1Instances);
	    	attributeVisualizePanel.setAttribute(0);
	    	removeAttributeButton.setEnabled(true);
	    	numberOfInstancesLabel.setText(" Instances: " + dataset1Instances.numInstances());
	    	numberOfFeaturesLabel.setText("Features: " + dataset1Instances.numAttributes());
    	}    	
	    catch(Exception e){e.printStackTrace();}	    
    }   
    	
   	public void setDataset1Instances(Instances instances){
   		applicationData.setDataset1Instances(instances);
   		myAttributeSelectionPanel.setInstances(null,instances);
	    attributeSummaryPanel.setInstances(instances);
	    attributeSummaryPanel.setAttribute(0);
	    attributeVisualizePanel.setInstances(instances);
	    attributeVisualizePanel.setAttribute(0);
	    numberOfInstancesLabel.setText(" Instances: " + instances.numInstances());
	    numberOfFeaturesLabel.setText("Features:" + instances.numAttributes());
   	}
    
   	private void saveArff(int index){   		
   		//0 - save positive
   		//1 - save negative 
   		//2 - save all
   		try{
			JFileChooser fc;				    	
	    	fc = new JFileChooser(applicationData.getWorkingDirectory());
	    	FileNameExtensionFilter filter = new FileNameExtensionFilter("Arff Files", "arff");
		    fc.setFileFilter(filter);	
			int returnVal = fc.showSaveDialog(parent);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();				        
				String savingFilename = file.getAbsolutePath();
				if(savingFilename.indexOf(".arff") == -1)
					savingFilename += ".arff";
				StatusPane statusPane = applicationData.getStatusPane();
				statusPane.setText("Saving as Arff..");
				BufferedWriter output = new BufferedWriter(new FileWriter(savingFilename));	
				Instances inst = applicationData.getDataset1Instances();
				inst.setClassIndex(inst.numAttributes() - 1);
				//write the relation
				output.write("@relation 'Attributes: " + inst.numAttributes() + " Instances: " + inst.numInstances() + "'");
				output.newLine();
				output.newLine();
				//write the attributes
				for(int x = 0; x < inst.numAttributes(); x++){
					if(!inst.attribute(x).name().equalsIgnoreCase("Class")){
						output.write("@attribute ");
						if(inst.attribute(x).type() == Attribute.NUMERIC)
							output.write(inst.attribute(x).name() + " numeric");
						else
							output.write(inst.attribute(x).name() + " String");
						output.newLine();
					}
				}
				output.write("@attribute Class {pos,neg}");
				output.newLine();
				output.newLine();
				output.write("@data");
				output.newLine();
				output.newLine();
				//write the instances
				for(int x = 0; x < inst.numInstances();  x++){
					if(index != 2){
						if(index != inst.instance(x).classValue())
							continue;
						if(index != inst.instance(x).classValue())
							continue;
					}						
					output.write(inst.instance(x).toString());
					output.newLine();
				}  	    				
				output.close();
				statusPane.setText("Features are save to " + savingFilename);
			}
		}catch(Exception e){
			JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
   	}
   	
   	private void save(final int index){
   		if(applicationData.getOneThread() != null){
   			JOptionPane.showMessageDialog(null,"Unable to save file due to other processes still running.. Please wait and try again later..","Error",JOptionPane.ERROR_MESSAGE);
   			return;
   		}
   		applicationData.setOneThread(new Thread(){   
			public void run(){
				saveArff(index);
				StatusPane statusPane = applicationData.getStatusPane();
				statusPane.setText("Saving as Arff..Done!");
				applicationData.setOneThread(null);
			}
		});
		applicationData.getOneThread().setPriority(Thread.MIN_PRIORITY); // UI has most priority
		applicationData.getOneThread().start();		
   	}
   	
    public void actionPerformed(ActionEvent ae){
    	if(ae.getSource().equals(this.saveArffButton)){
    		save(2);
    	}
    	else if(ae.getSource().equals(this.saveArffPosOnlyButton)){
    		save(0);
    	}
    	else if(ae.getSource().equals(this.saveArffNegOnlyButton)){
    		save(1);
    	}
    	else if(ae.getSource().equals(removeAttributeButton)){
    		try{
			    Remove r = new Remove();
			    int [] selected = myAttributeSelectionPanel.getSelectedAttributes();
			    if(selected.length == 0){//nothing to remove
			    	return;
			    }			    
			    if(selected.length == applicationData.getDataset1Instances().numAttributes()){
			    	//Do not allow all attribute to be remove
			   		// Pop up an error optionpane
			      	JOptionPane.showMessageDialog(parent,"Can't remove all attributes from data!\n",
							    "Remove Attributes",JOptionPane.ERROR_MESSAGE);
			      return;
			    }
			    r.setAttributeIndicesArray(selected);
			    applyFilter(r);
			  }catch(Exception ex){
			    // Pop up an error optionpane
				 ex.printStackTrace();
			    JOptionPane.showMessageDialog(parent,"Problem filtering instances2:\n" + ex.getMessage(),
			    	"Remove Attributes",JOptionPane.ERROR_MESSAGE);
			  }
    	}else if(ae.getSource().equals(applyFilterButton)){
    		applyFilter((Filter) filterEditor.getValue());
    	}else if(ae.getSource().equals(undoButton)){
    		undo();
    	}else if(ae.getSource().equals(previousStepButton)){//Previous Button
    		tabbedPane.setSelectedIndex(1);
    		tabbedPane.setEnabledAt(1,true);
    		tabbedPane.setEnabledAt(2,false);
    	}else if(ae.getSource().equals(nextStepButton)){//Next Button
    		//Comment out the following now I want to allow those even without features for GA
    		/*if(applicationData.getDataset1Instances() == null){    		
    			JOptionPane.showMessageDialog(parent,
    				"No Features Or In the process of Generating Features!"+
    					"\n Please go back to Step2!",
    				"ERROR",JOptionPane.ERROR_MESSAGE);
    			return;
    		}*/
    		if(applicationData.getOneThread() == null){
	    	applicationData.setOneThread(new Thread(){	      	
			public void run(){
				StatusPane statusPane = applicationData.getStatusPane();
    			statusPane.setText("Checking Attributes..");
				int[] boundaryPosition = SelectFeaturePane.findBoundary(applicationData.getDataset1Instances(),
					parent);
				applicationData.setLeftMostPosition(boundaryPosition[0]);
	    		applicationData.setRightMostPosition(boundaryPosition[1]);
	    		tabbedPane.setSelectedIndex(3);
	    		tabbedPane.setEnabledAt(2,false);
	    		tabbedPane.setEnabledAt(3,true);
	    		statusPane.setText("Done!");
    			applicationData.setOneThread(null);
			}});
		      applicationData.getOneThread().setPriority(Thread.MIN_PRIORITY); // UI has most priority
		      applicationData.getOneThread().start();
		    }
		    else{		      		      	
		      //Just skip to next step without checking attributes
		      tabbedPane.setSelectedIndex(3);
	    	  tabbedPane.setEnabledAt(2,false);
	    	  tabbedPane.setEnabledAt(3,true);
		    }
    	}
    }
    public static int[] findBoundary(Instances instances,JInternalFrame parent){
    	int leftMostPosition = Integer.MAX_VALUE;
    	int rightMostPosition = Integer.MIN_VALUE;
    	if(instances == null){
    		int[] returnValue = new int[2];
    		returnValue[0] = -1;
    		returnValue[1] = -1;
    		return returnValue;
    	}
    	try{    			
			//Trying to determine the leftmost and rightmost feature			    		
    		for(int x = 0; x < instances.numAttributes(); x++){
    			if(instances.attribute(x).name().equals("class")) continue;
    			StringTokenizer st = new StringTokenizer(instances.attribute(x).name(),"_");    			
    			char type = st.nextToken().charAt(0);
    			if(type == 'M' || type == 'N' || type == 'U' || type == 'T' || type == 'I' || type == 'J'){    				
    				while(st.countTokens() > 2)
    					st.nextToken();
    				int temp = Integer.parseInt(st.nextToken());
 					if(leftMostPosition > temp) leftMostPosition = temp;
 					temp = Integer.parseInt(st.nextToken());
 					if(rightMostPosition < temp) rightMostPosition = temp;
    			}else if(type == 'K' || type == 'L' || type == 'G' || type == 'H' || type == 'D' || type == 'E'){
    				st.nextToken();//kgram
    				st.nextToken();//mistakeAllowed
    				st.nextToken();//isPercentage
    				int temp = Integer.parseInt(st.nextToken());
 					if(leftMostPosition > temp) leftMostPosition = temp;
 					temp = Integer.parseInt(st.nextToken());
 					if(rightMostPosition < temp) rightMostPosition = temp;
    			}else if(type == 'R' || type == 'O' || type == 'Q'){
    				st.nextToken();//kgram1
    				st.nextToken();//mistakeAllowed1
    				st.nextToken();//kgram2
    				st.nextToken();//mistakeAllowed2
    				st.nextToken();//isPercentage
    				int temp = Integer.parseInt(st.nextToken());
 					if(leftMostPosition > temp) leftMostPosition = temp;
 					temp = Integer.parseInt(st.nextToken());
 					if(rightMostPosition < temp) rightMostPosition = temp;
    			}else if(type == 'B'){
 					if(st.countTokens() > 3){
 						st.nextToken();//type
 						st.nextToken();//isPercentage
 						int temp = Integer.parseInt(st.nextToken());
 	 					if(leftMostPosition > temp) leftMostPosition = temp;
 	 					temp = Integer.parseInt(st.nextToken());
 	 					if(rightMostPosition < temp) rightMostPosition = temp;
 					}
 				}else if(type == 'P'){
 					int temp = Integer.parseInt(st.nextToken());
 					if(leftMostPosition > temp) leftMostPosition = temp;
 					temp = Integer.parseInt(st.nextToken());
 					if(rightMostPosition < temp) rightMostPosition = temp;
 				}
    			else if(type == 'A' || type == 'Z' || type == 'C' || type == 'X'){
 					//do nothing
 				}else{ 					
 					System.out.println("Attribute Name: " + instances.attribute(x).name());
 					throw new Error("Unknown Type: " + type);
 				}
   			
    		}	    		
		}
		catch(NumberFormatException e){
			JOptionPane.showMessageDialog(parent,"Number Format Exception!",
		    	"Number Format Exception",JOptionPane.ERROR_MESSAGE);
		}
		catch(Exception ex){
			
			JOptionPane.showMessageDialog(parent,"Incorrect Attributes Format!",
		    	"Incorrect Attributes Format",JOptionPane.ERROR_MESSAGE);
		    ex.printStackTrace();
		}
		if(leftMostPosition == 999999999)
			leftMostPosition = -1;
    	if(rightMostPosition == -999999999)
    		rightMostPosition = -1;
		int[] returnValue = new int[2];
		returnValue[0] = leftMostPosition;
		returnValue[1] = rightMostPosition;
		return returnValue;
    }
    public static int[] oldfindBoundary(Instances instances,JInternalFrame parent){
    	int leftMostPosition = 999999999;
    	int rightMostPosition = -999999999;
    	try{    			
			//Trying to determine the leftmost and rightmost feature			    		
    		for(int x = 0; x < instances.numAttributes(); x++){    			
    			StringTokenizer st = new StringTokenizer(instances.attribute(x).name(),"_");
    			int noOfTokens = st.countTokens();
    			int tokenNumber = 1;
    			char type = ' ';
    			int multipleKgram = 0;
     			while (st.hasMoreTokens()){	
     				String tempString = st.nextToken();
     				if(tokenNumber == 1)
     					type = tempString.charAt(0);
     				if((type == 'M' || type == 'N' || type == 'U' || type == 'T' || type == 'I' || type == 'J') && tokenNumber == 2)
     					multipleKgram = Integer.parseInt(tempString);
     				else if((type == 'K' || type == 'L' || type == 'D' || type == 'E') && tokenNumber == 5){
     					int temp = Integer.parseInt(tempString);
     					if(leftMostPosition > temp)
     						leftMostPosition = temp;
     				}
     				else if((type == 'K' || type == 'L' || type == 'D' || type == 'E') && tokenNumber == 6){
     					int temp = Integer.parseInt(tempString);
     					if(rightMostPosition < temp)
     						rightMostPosition = temp;
     				}
     				else if(type == 'R' && tokenNumber == 6){
     					int temp = Integer.parseInt(tempString);
     					if(leftMostPosition > temp)
     						leftMostPosition = temp;
     				}
     				else if(type == 'R' && tokenNumber == 7){
     					int temp = Integer.parseInt(tempString);
     					if(rightMostPosition < temp)
     						rightMostPosition = temp;
     				}
     				else if(multipleKgram != 0 && (type == 'M' || type == 'N' || type == 'U' || type == 'T' || type == 'I' || type == 'J') &&
     					tokenNumber == ((multipleKgram*4) + 1)){
     					int temp = Integer.parseInt(tempString);
     					if(leftMostPosition > temp)
     						leftMostPosition = temp;
     				}
     				else if(multipleKgram != 0 && (type == 'M' || type == 'N' || type == 'U' || type == 'T' || type == 'I' || type == 'J')
     					&& tokenNumber == ((multipleKgram*4) + 2)){
     					int temp = Integer.parseInt(tempString);
     					if(rightMostPosition < temp)
     						rightMostPosition = temp;
     				}
     				else if(type == 'B' && tokenNumber == 1){
     					if(noOfTokens == 2){     					
     						tokenNumber = -1;
     						break;
     					}     					     				
     				}
     				else if(type == 'B' && tokenNumber == 4){
     					int temp = Integer.parseInt(tempString);
     					if(leftMostPosition > temp)
     						leftMostPosition = temp;
     				}
     				else if(type == 'B' && tokenNumber == 5){
     					int temp = Integer.parseInt(tempString);
     					if(rightMostPosition < temp)
     						rightMostPosition = temp;
     				}
     				else if(type == 'A'){
     					tokenNumber = -1;
     					break;
     				}
     				tokenNumber++;
     			}
     			//2 is for the Feature Class
     			//5 is for B type with window
     			//6 is for Kgram features
     			//7 is for Physiochemical2 Kgram
     			//8 is for Ratio features
     			//tokenNumber != ((multipleKgram*4) + 4) is for Physiochemical2 MultipleKgram      			
     			//However, for Multiple K-gram.. hmm... DONE!
     			if(tokenNumber != 7 && tokenNumber != 8 && tokenNumber != 6 && tokenNumber != 5 && tokenNumber != 2 && 
     					tokenNumber != ((multipleKgram*4) + 3) && tokenNumber != -1 && tokenNumber != ((multipleKgram*4) + 4)){     				
     				throw new Exception();
     			}	     				
    		}	    		
		}
		catch(NumberFormatException e){
			JOptionPane.showMessageDialog(parent,"Number Format Exception!",
		    	"Number Format Exception",JOptionPane.ERROR_MESSAGE);
		}
		catch(Exception ex){
			JOptionPane.showMessageDialog(parent,"Incorrect Attributes Format!",
		    	"Incorrect Attributes Format",JOptionPane.ERROR_MESSAGE);
		    ex.printStackTrace();
		}
		if(leftMostPosition == 999999999)
			leftMostPosition = -1;
    	if(rightMostPosition == -999999999)
    		rightMostPosition = -1;
		int[] returnValue = new int[2];
		returnValue[0] = leftMostPosition;
		returnValue[1] = rightMostPosition;
		return returnValue;
    }    
    /*
     * method called when the applyfilter button is pressed
     */
    protected void applyFilter(final Filter filter){    	
	    if(applicationData.getOneThread() == null){
	    	applicationData.setOneThread(new Thread(){	      	
		public void run(){
			try{
				if(filter != null){		    	
//		      		String cmd = filter.getClass().getName();
//		      		if(filter instanceof OptionHandler)
//						cmd += " " + Utils.joinOptions(((OptionHandler) filter).getOptions());		      
		      		/*comment away for the time being 
		      		int classIndex = m_AttVisualizePanel.getColoringIndex();
		      		if ((classIndex < 0) && (filter instanceof SupervisedFilter)) {
						throw new IllegalArgumentException("Class (colour) needs to " +
							   "be set for supervised " +
							   "filter.");
		      		}*/
		      		Instances copy = new Instances(applicationData.getDataset1Instances());
		      		//copy.setClassIndex(classIndex);
		      		copy.setClassIndex(applicationData.getDataset1Instances().numAttributes() - 1);		
		      		copy.deleteAttributeType(Attribute.STRING);
		      		filter.setInputFormat(copy);
		      		statusPane.setText("Applying Filter.. May take a while.. Please wait..");
		      		Instances newInstances = Filter.useFilter(copy, filter);
		      		if(newInstances == null || newInstances.numAttributes() < 1){
						throw new Exception("Dataset is empty.");
					}
					addUndoPoint();
					//m_AttVisualizePanel.setColoringIndex(copy.classIndex());
					// if class was not set before, reset it again after use of filter
					if (applicationData.getDataset1Instances().classIndex() < 0)
						newInstances.setClassIndex(-1);
					//dataset1Instances = newInstances;
					setDataset1Instances(newInstances);
					statusPane.setText("Filter Applied..");													
		    	}		    
		  }catch(Exception ex){
		  	// Pop up an error optionpane
			ex.printStackTrace();
		    JOptionPane.showMessageDialog(parent,"Problem filtering instances:\n"+ ex.getMessage(),
						  "Apply Filter",JOptionPane.ERROR_MESSAGE);
		  }
		  applicationData.setOneThread(null);
		}});
	      applicationData.getOneThread().setPriority(Thread.MIN_PRIORITY); // UI has most priority
	      applicationData.getOneThread().start();
	    }
	    else{
	      JOptionPane.showMessageDialog(parent,"Can't apply filter at this time,\n"
	      		+ "currently busy with other IO","Apply Filter", JOptionPane.WARNING_MESSAGE);
	    }
	}         
	/**
	 * Backs up the current state of the dataset, so the changes can be undone.
	 * 
	 * @throws Exception 	if an error occurs
	 */
	 public void addUndoPoint() throws Exception{
	 	if(applicationData.getDataset1Instances() != null){
	 		//create temporary file
	 		File tempFile = File.createTempFile("weka", SerializedInstancesLoader.FILE_EXTENSION);
	 		tempFile.deleteOnExit();
	 		ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(
	 			new FileOutputStream(tempFile)));
    		oos.writeObject(applicationData.getDataset1Instances());
      		oos.flush();
      		oos.close();
			//update undo file list
      		if(m_tempUndoFiles[m_tempUndoIndex] != null)//remove undo points that are too old
      			m_tempUndoFiles[m_tempUndoIndex].delete();      		
      		m_tempUndoFiles[m_tempUndoIndex] = tempFile;
      		if(++m_tempUndoIndex >= m_tempUndoFiles.length)//wrap pointer around
				m_tempUndoIndex = 0;			
			undoButton.setEnabled(true);
		}
	}
	/**
	 * Reverts to the last backed up version of the dataset.
	 */
	 public void undo(){
	 	if(--m_tempUndoIndex < 0) //wrap pointer around
	 		m_tempUndoIndex = m_tempUndoFiles.length-1;	 	
	 	if(m_tempUndoFiles[m_tempUndoIndex] != null){
      		//load instances from the temporary file
      		AbstractFileLoader loader = ConverterUtils.getLoaderForFile(m_tempUndoFiles[m_tempUndoIndex]);
      		try{
      			loader.setFile(m_tempUndoFiles[m_tempUndoIndex]);
      			setInstancesFromFile(loader);
      		}catch(Exception e){
      			e.printStackTrace();
				JOptionPane.showMessageDialog(parent,"Cannot perform undo operation!\n" + 
					e.toString(),"Undo",JOptionPane.ERROR_MESSAGE);
			}
		//update undo file list
      	m_tempUndoFiles[m_tempUndoIndex] = null;
      	}
      	//update undo button
    	int temp = m_tempUndoIndex-1;
    	if(temp < 0)
      		temp = m_tempUndoFiles.length-1;    
    	undoButton.setEnabled(m_tempUndoFiles[temp] != null);
    }
    /**
     * Loads results from a set of instances retrieved with the supplied loader. 
     * This is started in the IO thread, and a dialog is popped up
     * if there's a problem.
     *
     * @param loader	the loader to use
     */
     public void setInstancesFromFile(final AbstractFileLoader loader){      
     	if(applicationData.getOneThread() == null){
      		applicationData.setOneThread(new Thread(){
			public void run(){
	  			try{	    
				    Instances inst = loader.getDataSet();
	    			setDataset1Instances(inst);
	    		}
	 			catch(Exception ex){
			    	if(JOptionPane.showOptionDialog(parent,"File '" + loader.retrieveFile() + 
			    		"' not recognised as an '" + loader.getFileDescription() + "' file.\n" + 
			    		"Reason:\n" + ex.getMessage(), "Load Instances", 0, JOptionPane.ERROR_MESSAGE,
			    		null, new String[] {"OK", "Use Converter"}, null) == 1) {
			    			converterQuery(loader.retrieveFile());
			    	}
			    	ex.printStackTrace();
	  			}
	  			applicationData.setOneThread(null);
			}
      		});
      		applicationData.getOneThread().setPriority(Thread.MIN_PRIORITY); // UI has most priority
      		applicationData.getOneThread().start();
    	}else{
      		JOptionPane.showMessageDialog(this, "Can't load at this time,\n" + "currently busy with other IO",
      			"Load Instances", JOptionPane.WARNING_MESSAGE);
    	}
  	}
  	/**
  	 * Pops up generic object editor with list of conversion filters
  	 *
  	 * @param f the File
  	 */
  	 private void converterQuery(final File f){
  	 	final GenericObjectEditor convEd = new GenericObjectEditor(true);
  	 	try{
  	 		convEd.setClassType(weka.core.converters.Loader.class);
  	 		convEd.setValue(new weka.core.converters.CSVLoader());
  	 		((GenericObjectEditor.GOEPanel)convEd.getCustomEditor()).addOkListener(new ActionListener(){
  	 			public void actionPerformed(ActionEvent e){
  	 				tryConverter((Loader)convEd.getValue(), f);
  	 			}
  	 		});
  	 	}catch(Exception ex){ex.printStackTrace();}
  	 	//PropertyDialog pd = new PropertyDialog(convEd, 100, 100);
  	 }
  	 /**
  	  * Applies the selected converter
  	  *
  	  * @param cnv the converter to apply to the input file
  	  * @param f the input file
  	  */
  	  private void tryConverter(final Loader cnv, final File f){
  	  	if(applicationData.getOneThread() == null){
      		applicationData.setOneThread(new Thread(){
	  			public void run(){
				    try{
	      				cnv.setSource(f);
	      				Instances inst = cnv.getDataSet();
	      				setDataset1Instances(inst);
	    			}catch(Exception ex){
	    				JOptionPane.showMessageDialog(parent, cnv.getClass().getName()+" failed to load '"
					    	+ f.getName() + "'.\n" + "Reason:\n" + ex.getMessage(), "Convert File",
					    	JOptionPane.ERROR_MESSAGE);	      			
	      				converterQuery(f);
	      			}
	    			applicationData.setOneThread(null);
	  			}});
      		applicationData.getOneThread().setPriority(Thread.MIN_PRIORITY); // UI has most priority
      		applicationData.getOneThread().start();
    	}
    }
}