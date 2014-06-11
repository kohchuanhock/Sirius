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
package sirius.misc.featurevisualizer;


import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import sirius.predictor.main.*;
import sirius.trainer.main.SiriusSettings;

import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.ChiSquaredAttributeEval;
import weka.core.Instances;

import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.BitSet;

public class FeatureVisualizerPane extends JComponent implements ActionListener, ComponentListener, ItemListener{
	static final long serialVersionUID = sirius.Sirius.version;
	
	private JTextField filenameTextField;
	private JButton browseButton;
	private JComboBox xAxisComboBox;
	private JComboBox yAxisComboBox;
	private JComboBox classComboBox;
	private JButton topChiSquareButton;
	private JButton topCFSButton;
	private JTextField yAxisIntervalTextField;
	private JTextField xAxisIntervalTextField;
	private Instances instances;
	private ChiSquaredAttributeEval myChiSquare;
	private int top1ChiSqIndex;
	private int top2ChiSqIndex;
	private int top1CFSIndex;
	private int top2CFSIndex;
	
	private JTextField statusTextField;
	private JTextField sequencesNumTextField;
	private JTextField featuresNumTextField;
	
	private JInternalFrame parent;
	
	private JTable sequenceNameTable;
	private SequenceNameTableModel sequenceNameTableModel;
	
	private JTextField sequenceNameTextField;
	private JButton sequenceNameButton;
	
	private FeatureGraphPane featureGraphPane;
	private FeatureViewPort featureViewPort;
	private JScrollPane featureGraphScrollPane;
	
	private double lastYAxisIntervalValue;
	private double lastXAxisIntervalValue;
	
	public FeatureVisualizerPane(final JInternalFrame parent,JTabbedPane tabbedPane){
		this.parent = parent;
		
		JPanel arffFilePanel = new JPanel();
		arffFilePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Arff File"),BorderFactory.createEmptyBorder(0, 2, 2, 2)));
		
		filenameTextField = new JTextField(10);
		filenameTextField.setEnabled(false);
		browseButton = new JButton("Browse");
		browseButton.addActionListener(this);
		
		arffFilePanel.add(filenameTextField);
		arffFilePanel.add(browseButton);	
		
		JPanel yAxisPanel = new JPanel();
		yAxisPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("y-Axis"),BorderFactory.createEmptyBorder(0, 2, 2, 2)));		
		yAxisComboBox = new JComboBox();
		yAxisComboBox.addItem("               ");
		yAxisComboBox.addItemListener(this);
		JLabel yAxisIntervalLabel = new JLabel("Interval: ");
		yAxisIntervalTextField = new JTextField(4);
		yAxisIntervalTextField.addFocusListener(new FocusListener() {
	         public void focusGained(FocusEvent e){
	         }
	
	         public void focusLost(FocusEvent e){
	         	try{
	         		lastYAxisIntervalValue = Double.parseDouble(yAxisIntervalTextField.getText());
	         		featureGraphPane.recalibrateYAxisLength();
	         		featureViewPort.revalidate();
	         	}catch(NumberFormatException ex){
	         		JOptionPane.showMessageDialog(parent,"Invalid Input(Numbers Only)","Error",
	         		JOptionPane.ERROR_MESSAGE);
	         		yAxisIntervalTextField.setText("" + lastYAxisIntervalValue);	         		     	
	         	}
	         }
		});
		yAxisPanel.add(yAxisComboBox);		
		yAxisPanel.add(yAxisIntervalLabel);
		yAxisPanel.add(yAxisIntervalTextField);
		
		JPanel xAxisPanel = new JPanel();
		xAxisPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("x-Axis"),BorderFactory.createEmptyBorder(0, 2, 2, 2)));		
		xAxisComboBox = new JComboBox();
		xAxisComboBox.addItem("               ");
		xAxisComboBox.addItemListener(this);
		JLabel xAxisIntervalLabel = new JLabel("Interval: ");
		xAxisIntervalTextField = new JTextField(4);
		xAxisPanel.add(xAxisComboBox);
		xAxisPanel.add(xAxisIntervalLabel);
		xAxisPanel.add(xAxisIntervalTextField);
		xAxisIntervalTextField.addFocusListener(new FocusListener() {
	         public void focusGained(FocusEvent e){
	         }
	
	         public void focusLost(FocusEvent e){
	         	try{
	         		lastXAxisIntervalValue = Double.parseDouble(xAxisIntervalTextField.getText());
	         		featureGraphPane.recalibrateXAxisLength();
	         		featureViewPort.revalidate();
	         	}catch(NumberFormatException ex){
	         		JOptionPane.showMessageDialog(parent,"Invalid Input(Numbers Only)","Error",
	         		JOptionPane.ERROR_MESSAGE);
	         		xAxisIntervalTextField.setText("" + lastXAxisIntervalValue);	         		     	
	         	}
	         }
		});
		
		JPanel classPanel = new JPanel();
		classPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Class"),BorderFactory.createEmptyBorder(0, 5, 5, 5)));		
		classComboBox = new JComboBox();
		classComboBox.addItem("               ");
		classComboBox.setEnabled(false);
		classPanel.add(classComboBox);		
				
		JPanel autoSelectPanel = new JPanel();		
		autoSelectPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Auto-Select"),BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		topChiSquareButton = new JButton("Top 2 based on Chi-Square");
		topChiSquareButton.addActionListener(this);
		topChiSquareButton.setEnabled(false);
		topCFSButton = new JButton("Top pair based on CFS");
		topCFSButton.addActionListener(this);
		topCFSButton.setEnabled(false);
		autoSelectPanel.add(topChiSquareButton);
		autoSelectPanel.add(topCFSButton);
		
		JPanel statusPanel = new JPanel(new GridLayout(1,1));
		statusPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Status"),BorderFactory.createEmptyBorder(5, 5, 10, 5)));
		statusTextField = new JTextField(5);				
		statusTextField.setEnabled(false);		
		
		JPanel statsPanel = new JPanel();
		statsPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Stats"),BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		JLabel sequencesNumLabel = new JLabel("Sequences #: ");
		sequencesNumTextField = new JTextField(4);
		sequencesNumTextField.setEnabled(false);
		JLabel featuresNumLabel = new JLabel("Features #: ");
		featuresNumTextField = new JTextField(4);
		featuresNumTextField.setEnabled(false);
		
		statusPanel.add(statusTextField);
		statsPanel.add(sequencesNumLabel);
		statsPanel.add(sequencesNumTextField);
		statsPanel.add(featuresNumLabel);
		statsPanel.add(featuresNumTextField);
		
		
		JPanel northPanel = new JPanel(new BorderLayout());
		JPanel northCenterPanel = new JPanel(new GridLayout(1,2));
		northPanel.add(arffFilePanel, BorderLayout.WEST);
		northCenterPanel.add(xAxisPanel);
		northCenterPanel.add(yAxisPanel);		
		northPanel.add(northCenterPanel, BorderLayout.CENTER);	
		
		JPanel southPanel = new JPanel(new GridLayout(1,3));
		southPanel.add(statusPanel);
		southPanel.add(statsPanel);
		southPanel.add(autoSelectPanel);
		
		JPanel centerPanel = new JPanel(new BorderLayout());
		JPanel centerWestPanel = new JPanel(new BorderLayout());
		centerWestPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Sequences Name"),BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		sequenceNameTableModel = new SequenceNameTableModel(false);
    	sequenceNameTable = new JTable(sequenceNameTableModel);
    	sequenceNameTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
    	sequenceNameTable.getColumnModel().getColumn(0).setMinWidth(20);       
        sequenceNameTable.getColumnModel().getColumn(1).setMinWidth(70);        
    	sequenceNameTable.getColumnModel().getColumn(0).setPreferredWidth(40);     
        sequenceNameTable.getColumnModel().getColumn(1).setPreferredWidth(200);        
        JScrollPane sequenceNameTableScrollPane = new JScrollPane(sequenceNameTable);
    	centerWestPanel.add(sequenceNameTableScrollPane,BorderLayout.CENTER);
		
    	JPanel sequenceNamePanel = new JPanel();    	  
    	sequenceNamePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Sequences Name File"),BorderFactory.createEmptyBorder(0, 5, 5, 5)));
    	sequenceNameTextField = new JTextField(10);
    	sequenceNameTextField.setEnabled(false);
    	sequenceNameButton = new JButton("Browse");
    	sequenceNameButton.addActionListener(this);
    	sequenceNamePanel.add(sequenceNameTextField);
    	sequenceNamePanel.add(sequenceNameButton);    	
    	
    	centerPanel.add(centerWestPanel, BorderLayout.WEST);
    	
    	featureGraphPane = new FeatureGraphPane(instances, yAxisIntervalTextField, xAxisIntervalTextField);
    	featureViewPort = new FeatureViewPort(featureGraphPane);
    	JPanel featureGraphPanel = new JPanel(new BorderLayout());
    	featureGraphPanel.add(featureGraphPane,BorderLayout.CENTER);
    	featureGraphScrollPane = new JScrollPane();
    	featureGraphScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    	featureGraphScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    	featureViewPort.setView(featureGraphPane);
    	featureGraphScrollPane.setViewport(featureViewPort);
    	featureGraphScrollPane.addComponentListener(this);
    	featureGraphScrollPane.setBorder(BorderFactory.createCompoundBorder(
    	BorderFactory.createTitledBorder("Feature Graph"),
    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));    	
    	centerPanel.add(featureGraphScrollPane, BorderLayout.CENTER); 
    	featureGraphPane.setParentDimension(featureGraphScrollPane.getSize());
    	
		setLayout(new BorderLayout());
		add(northPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);
	}
	public void itemStateChanged(ItemEvent e){
		if(e.getSource().equals(xAxisComboBox)){
			if(e.getStateChange() == ItemEvent.SELECTED){
				featureGraphPane.setXIndex(xAxisComboBox.getSelectedIndex() - 1);
				featureViewPort.revalidate();				
			}
		}else if(e.getSource().equals(yAxisComboBox)){
			if(e.getStateChange() == ItemEvent.SELECTED){
				featureGraphPane.setYIndex(yAxisComboBox.getSelectedIndex() - 1);
				featureViewPort.revalidate();
			}
		}		
	}
	
	public void actionPerformed(ActionEvent ae){
		if(ae.getSource().equals(browseButton)){
			//applicationData.hasLocationIndexBeenSet = false;
			JFileChooser fc;	    	
    		//if working directory not set then look at the Sirius Settings file
    		String lastLocation = SiriusSettings.getInformation("LastArffFileLocation: ");
    		if(lastLocation == null)
    			fc = new JFileChooser();
    		else
    			fc = new JFileChooser(lastLocation);	    				    
	        FileNameExtensionFilter filter = new FileNameExtensionFilter(
	            "Arff Files", "arff");
	        fc.setFileFilter(filter);			        			        			    	
			int returnVal = fc.showOpenDialog(parent);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
	            SiriusSettings.updateInformation("LastArffFileLocation: ", file.getAbsolutePath());
	            filenameTextField.setText(file.getAbsolutePath());
	            try{
	            	loadArffFile(file);	            	
	            }catch(Exception e){
	            	JOptionPane.showMessageDialog(null,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
	            	e.printStackTrace();
	            } 	            
			}
		}else if(ae.getSource().equals(topChiSquareButton)){
			if(instances != null){
				setTop2ChiSqIndex();
				setTop2ChiSq();
			}
		}else if(ae.getSource().equals(topCFSButton)){
			if(instances != null){
				setTop2CFSIndex();
				setTop2CFS();
			}			
		}
			
	}
	private void loadArffFile(final File file) throws Exception{
		Thread runThread = new Thread(){
			public void run(){	
				try{
					instances = new Instances(new BufferedReader(new FileReader(file.getAbsolutePath())));
					String sequenceNameFile = file.getAbsolutePath().substring(0,file.getAbsolutePath().indexOf(".arff")) + ".sequencesName";
					featureGraphPane.setInstances(instances);
					sequenceNameTableModel.loadSequencesNameFile(sequenceNameFile);
					statusTextField.setText("Loading..");
			    	instances.setClassIndex(instances.numAttributes() - 1);
			    	myChiSquare = new ChiSquaredAttributeEval();
					myChiSquare.buildEvaluator(instances);
					yAxisComboBox.removeAllItems();
					xAxisComboBox.removeAllItems();
					yAxisComboBox.addItem("       ");
					xAxisComboBox.addItem("       ");
			    	updateComboBox(yAxisComboBox);
			    	updateComboBox(xAxisComboBox);
			    	updateComboBox(classComboBox);
			    	classComboBox.setSelectedIndex(classComboBox.getItemCount() - 1);
			    	classComboBox.repaint();
			    	sequencesNumTextField.setText("" + instances.numInstances());
			    	featuresNumTextField.setText("" + instances.numAttributes());
			    	topChiSquareButton.setEnabled(true);
			    	topCFSButton.setEnabled(true);			    	
			    	top1CFSIndex = -1;
			    	top2CFSIndex = -1;
			    	statusTextField.setText("Done");
				}catch(Exception e){
			    	JOptionPane.showMessageDialog(null,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			    	e.printStackTrace();
				}	
			}
		};
		runThread.setPriority(Thread.MIN_PRIORITY);
     	runThread.start();
	}
	
	private void updateComboBox(JComboBox comboBox) throws Exception{		
		DecimalFormat df = new DecimalFormat("0.##");		
		for(int x = 0; x < this.instances.numAttributes(); x++){		
			comboBox.addItem(this.instances.attribute(x).name() + " (" + df.format(myChiSquare.evaluateAttribute(x)) + ")");			
		}		
	}
	private void setTop2ChiSqIndex(){
		try{
			double top1ChiSq = -9999999;
			double top2ChiSq = -9999999;
			int divisor = instances.numAttributes() / 100;
			//-1 because assume that last attribute is class attribute
			for(int x = 0; x < this.instances.numAttributes() - 1; x++){
				if(divisor != 0 && x%divisor == 0)
					statusTextField.setText("Calculating Top Chi-Sq.. " + ((x*100)/instances.numAttributes()) + "%");
				if(myChiSquare.evaluateAttribute(x) > top1ChiSq){
					top2ChiSqIndex = top1ChiSqIndex;
					top2ChiSq = top1ChiSq;
					top1ChiSqIndex = x;
					top1ChiSq = myChiSquare.evaluateAttribute(x);				
				}else if(myChiSquare.evaluateAttribute(x) > top2ChiSq){
					top2ChiSqIndex = x;
					top2ChiSq = myChiSquare.evaluateAttribute(x);
				}			
			}
			statusTextField.setText("Done");		
		}catch(Exception e){
	    	JOptionPane.showMessageDialog(null,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
	    	e.printStackTrace();
		}	
	}
	
	private void setTop2ChiSq(){		
		yAxisComboBox.setSelectedIndex(top1ChiSqIndex + 1);		
		xAxisComboBox.setSelectedIndex(top2ChiSqIndex + 1);
		yAxisComboBox.repaint();
		xAxisComboBox.repaint();		
	}
	
	private void setTop2CFSIndex(){				
		if(top1CFSIndex != -1)
			return;		
		Thread runThread = new Thread(){
			public void run(){
				try{
					CfsSubsetEval myCfs = new CfsSubsetEval();
					myCfs.buildEvaluator(instances);
					double maxCFSScore = -9999999;
					int divisor = instances.numAttributes() / 100;
					BitSet bs = new BitSet();
					//-1 because assume that last attribute is class attribute
					for(int x = 0; x < instances.numAttributes() - 1; x++){
						if(divisor != 0 && x%divisor == 0)
							statusTextField.setText("Calculating Top CFS.. " + ((x*100)/instances.numAttributes()) + "%");
						bs.set(x,true);
						//-1 because assume that last attribute is class attribute
						for(int y = (x + 1); y < instances.numAttributes() - 1; y++){
							bs.set(y,true);									
							if(myCfs.evaluateSubset(bs) > maxCFSScore){
								maxCFSScore = myCfs.evaluateSubset(bs);
								top1CFSIndex = x;
								top2CFSIndex = y;
							}
							bs.set(y,false);
						}						
						bs.set(x,false);
					}
					statusTextField.setText("Done");					
					setTop2CFS();
				}catch(Exception e){
			    	JOptionPane.showMessageDialog(null,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			    	e.printStackTrace();
				}		
			}				
		};							
		runThread.setPriority(Thread.MIN_PRIORITY);
     	runThread.start();	     	
		}	
	private void setTop2CFS(){
		yAxisComboBox.setSelectedIndex(top1CFSIndex + 1);		
		xAxisComboBox.setSelectedIndex(top2CFSIndex + 1);			
		yAxisComboBox.repaint();
		xAxisComboBox.repaint();
	}
	
	public void componentHidden(ComponentEvent e){
		//Invoked when the component has been made invisible. 
	}          
 	public void componentMoved(ComponentEvent e){
 		//Invoked when the component's position changes. 
 	}          
 	public void componentResized(ComponentEvent e){
 		//Invoked when the component's size changes. 	
 		featureGraphPane.setParentDimension(featureGraphScrollPane.getSize()); 		 
 	}          
 	public void componentShown(ComponentEvent e){
 		//Invoked when the component has been made visible. 
 	}           
}