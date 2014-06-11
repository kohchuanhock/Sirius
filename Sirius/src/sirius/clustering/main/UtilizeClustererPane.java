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
package sirius.clustering.main;


import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import sirius.trainer.main.SiriusSettings;

import weka.clusterers.Clusterer;
import weka.core.Attribute;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class UtilizeClustererPane extends JComponent implements ActionListener{
	static final long serialVersionUID = sirius.Sirius.version;

	private Thread inputThread;	
	private JButton inputFileButton;
	private JTextField inputFileTextField;		
	private JButton outputFileButton;
	private JTextField outputFileTextField;	
	private JButton bottomStartButton;
	private JButton bottomStopButton;	
	private boolean run;
	private String inputFileName;	
	private JLabel statusLabel;
	private JFrame parent;	
	private JButton clustererButton;
	private JTextField clustererTextField;	
	private Clusterer clusterer;
	private JTextArea outputTextArea; 
	
	private JScrollPane outputScrollPane;
	private JComboBox m_ClassCombo = new JComboBox();
	
	public UtilizeClustererPane(JFrame mainFrame){
		this.parent = mainFrame;
		
		JPanel clustererPanel = new JPanel(new BorderLayout());
		clustererPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Choose Trained Clusterer"),
		    	BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		this.clustererButton = new JButton("Choose");
		this.clustererButton.addActionListener(this);
		this.clustererTextField = new JTextField();
		this.clustererTextField.setFocusable(false);
		clustererPanel.add(this.clustererButton,BorderLayout.WEST);
		clustererPanel.add(this.clustererTextField,BorderLayout.CENTER);
		
		JPanel outputPanel = new JPanel(new GridLayout(1,1));
		outputPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Output"),
		    	BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		this.outputTextArea = new JTextArea();
		this.outputTextArea.setFocusable(false);				
		outputScrollPane = new JScrollPane(outputTextArea);
		outputPanel.add(outputScrollPane,BorderLayout.CENTER);
		
		JPanel inputFilePanel = new JPanel(new BorderLayout());
		inputFilePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Choose Input File"),
		    	BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		this.inputFileButton = new JButton("Choose");
		this.inputFileButton.addActionListener(this);
		this.inputFileTextField = new JTextField();
		this.inputFileTextField.setFocusable(false);
		inputFilePanel.add(this.inputFileButton,BorderLayout.WEST);
		inputFilePanel.add(this.inputFileTextField,BorderLayout.CENTER);
		
		JPanel outputFilePanel = new JPanel(new BorderLayout());
		outputFilePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Choose Output Location"),
		    	BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		this.outputFileButton = new JButton("Choose");
		this.outputFileButton.addActionListener(this);
		this.outputFileTextField = new JTextField();
		this.outputFileTextField.setFocusable(false);
		outputFilePanel.add(this.outputFileButton,BorderLayout.WEST);
		outputFilePanel.add(this.outputFileTextField,BorderLayout.CENTER);
				
		JPanel startStopButtonsPanel = new JPanel(new GridLayout(1,2,5,5));
		startStopButtonsPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Controls"),
		    	BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		this.bottomStartButton = new JButton("Start");
		this.bottomStartButton.addActionListener(this);
		this.bottomStopButton = new JButton("Stop");
		this.bottomStopButton.addActionListener(this);
		this.bottomStopButton.setEnabled(false);
		startStopButtonsPanel.add(this.bottomStartButton);
		startStopButtonsPanel.add(this.bottomStopButton);				
		
		JPanel classIndexPanel = new JPanel(new BorderLayout());
		classIndexPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Settings"),
		    	BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		JLabel classIndexLabel = new JLabel("Class: ");
		JPanel extraPanel = new JPanel(new BorderLayout());
		extraPanel.add(classIndexLabel,BorderLayout.WEST);
		extraPanel.add(this.m_ClassCombo,BorderLayout.CENTER);
		classIndexPanel.add(extraPanel, BorderLayout.NORTH);	
		
		
		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.add(clustererPanel,BorderLayout.NORTH);
		northPanel.add(inputFilePanel,BorderLayout.CENTER);
		northPanel.add(outputFilePanel,BorderLayout.SOUTH);		
		
		JPanel statusPanel = new JPanel(new GridLayout(1,1));
		statusPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Status"),
		    	BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		this.statusLabel = new JLabel(" ");
		statusPanel.add(this.statusLabel);
		
		JPanel emptyPanel1 = new JPanel();
		JPanel emptyPanel2 = new JPanel();				
		
		JPanel centerNorthPanel = new JPanel(new GridLayout(1,4,5,5));
		centerNorthPanel.add(classIndexPanel);
		centerNorthPanel.add(startStopButtonsPanel);
		centerNorthPanel.add(emptyPanel1);
		centerNorthPanel.add(emptyPanel2);
		
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(outputPanel,BorderLayout.CENTER);
		centerPanel.add(centerNorthPanel,BorderLayout.NORTH);
		
		setLayout(new BorderLayout());
		//add(settingsPanel,BorderLayout.WEST);
		add(northPanel,BorderLayout.NORTH);
		add(centerPanel,BorderLayout.CENTER);
		add(statusPanel,BorderLayout.SOUTH);
	}
	
	private void setOutputLocation(){
		JFileChooser chooser;
    	String lastLocation = SiriusSettings.getInformation("LastClusteringOutputLocation: ");
		if(lastLocation == null)
			chooser = new JFileChooser();
		else
			chooser = new JFileChooser(lastLocation); 		
		chooser.setDialogTitle("Set Working Directory");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
			this.outputFileTextField.setText(chooser.getSelectedFile().toString());  
			SiriusSettings.updateInformation("LastCLusteringOutputLocation: ", chooser.getSelectedFile().getAbsolutePath());
	   	}
	    else{
	    	//no selection
	    }
	}
	
	private void stop(){
		run = false;
	}
	
	private void loadClusterer(){
		String lastClassifierFileLocation = SiriusSettings.getInformation("LastClusteringOutputLocation: ");
    	JFileChooser fc = new JFileChooser(lastClassifierFileLocation);
    	FileNameExtensionFilter filter = new FileNameExtensionFilter(
	            "Clusterer Files", "clusterer","clusterer");
	    fc.setFileFilter(filter);	
		int returnVal = fc.showOpenDialog(this);		
		if (returnVal == JFileChooser.APPROVE_OPTION) {	  
			try{
				statusLabel.setText(" Loading Clusterer..");
				File file = fc.getSelectedFile();
				SiriusSettings.updateInformation("LastClusteringOutputLocation: ", file.getAbsolutePath());
		    	FileInputStream fis = new FileInputStream(file);
		        ObjectInputStream ois = new ObjectInputStream(fis);
				
		        clusterer = (Clusterer) ois.readObject();								
		        ois.close();		        
		        this.clustererTextField.setText(file.getAbsolutePath());
		        statusLabel.setText(" Loading Clusterer..Done!");
			}catch(Exception e){
				e.printStackTrace();
				statusLabel.setText(" Loading Clusterer..Error!");
			}
		}
	}
	
	private Instances removeClass(Instances inst) {//Copied directly from Weka's ClustererPanel.java
		Remove af = new Remove();
		Instances retI = null;
		try{
			if (inst.classIndex() < 0) {
				//do nothing since classindex is not set
				retI = inst;
			} else {
				//remove class attribute
				af.setAttributeIndices(""+(inst.classIndex()+1));
				af.setInvertSelection(false);
				af.setInputFormat(inst);
				retI = Filter.useFilter(inst, af);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retI;
	}
	private void start(){
		if(this.clustererTextField.getText().length() == 0){
			JOptionPane.showMessageDialog(parent,"Please set clusterer!","Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(this.inputFileTextField.getText().length() == 0){
			JOptionPane.showMessageDialog(parent,"Please choose input file!","Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(this.outputFileTextField.getText().length() == 0){
			JOptionPane.showMessageDialog(parent,"Please choose output location!","Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(inputThread != null){
			JOptionPane.showMessageDialog(parent,"Cannot start because another is still running!","Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
		this.bottomStartButton.setEnabled(false);
		this.bottomStopButton.setEnabled(true);
		run = true;
		inputThread = (new Thread(){   
			public void run(){
				try{	
					statusLabel.setText(" Evaluating Input File..");
					BufferedWriter[] outputArray = new BufferedWriter[clusterer.numberOfClusters()];
					int[] counter = new int[clusterer.numberOfClusters()];
					for(int x = 0; x < clusterer.numberOfClusters(); x++){
						outputArray[x] = new BufferedWriter(new FileWriter(outputFileTextField.getText() + File.separator + inputFileName + "_cluster" + x + ".arff"));
					}
					BufferedReader input = new BufferedReader(new FileReader(inputFileTextField.getText()));
					String line;
					while((line = input.readLine()) != null){
						if(run == false)
							break;
						for(int x = 0; x < clusterer.numberOfClusters(); x++){
							outputArray[x].write(line);
							outputArray[x].newLine();
						}
						if(line.equalsIgnoreCase("@data")){
							for(int x = 0; x < clusterer.numberOfClusters(); x++){
								outputArray[x].newLine();
							}
							break;
						}
					}
					input.close();
					Instances inst = new Instances(new BufferedReader(new FileReader(inputFileTextField.getText())));					
					inst.setClassIndex(m_ClassCombo.getSelectedIndex());					
				    if(inst.classAttribute().isNumeric()) {
				    	JOptionPane.showMessageDialog(parent,"Class must be nominal!","Error",JOptionPane.ERROR_MESSAGE);
				    }else{
				    	Instances instWithoutClass = removeClass(inst);
				    	outputTextArea.setText("Number of Clusters: " + clusterer.numberOfClusters() + "\r\n");
						outputTextArea.append("Number of Attributes: " + (inst.numAttributes() - 1 )+ "\r\n");
						outputTextArea.append( "Number of Instances: " + inst.numInstances() + "\r\n\r\n");
						for(int x = 0; x < inst.numInstances(); x++){
							if(run == false)
								break;
							int clusterIndex = clusterer.clusterInstance(instWithoutClass.instance(x));
							counter[clusterIndex]++;
							outputArray[clusterIndex].write(inst.instance(x).toString());
							outputArray[clusterIndex].newLine();
						}
						for(int x = 0; x < clusterer.numberOfClusters(); x++){
							outputArray[x].close();
						}
						inputThread = null;
						bottomStartButton.setEnabled(true);
						bottomStopButton.setEnabled(false);
						if(run != false){
							statusLabel.setText(" Evaluating Input File..Done!");						
							for(int x = 0; x < clusterer.numberOfClusters(); x++){
								outputTextArea.append("Cluster " + x + ": " + counter[x] + "\r\n");
							}
						}
						else
							statusLabel.setText(" Evaluating Input File..Interrupted!");
				    }
				}catch(Exception e){
					e.printStackTrace();
					JOptionPane.showMessageDialog(parent,"Please ensure that the Trained Clusterer and Input File have same Attributes!","Error",JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		inputThread.setPriority(Thread.MIN_PRIORITY); // UI has most priority
		inputThread.start();			
	}	
	
	private void loadInputFile(){
		try{
			//applicationData.hasLocationIndexBeenSet = false;
			JFileChooser fc;	    	
	    	String lastLocation = SiriusSettings.getInformation("LastClusteringOutputLocation: ");
    		if(lastLocation == null)
    			fc = new JFileChooser();
    		else
    			fc = new JFileChooser(lastLocation);	    	
	        FileNameExtensionFilter filter = new FileNameExtensionFilter(
	            "Arff File", "arff");
	        fc.setFileFilter(filter);
			int returnVal = fc.showOpenDialog(parent);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
	            SiriusSettings.updateInformation("LastClusteringOutputLocation: ", file.getAbsolutePath());
	            this.inputFileTextField.setText(file.getAbsolutePath());
	            StringTokenizer st = new StringTokenizer(file.getName(), ".");
	            this.inputFileName = st.nextToken();
	            Instances inst = new Instances(new BufferedReader(new FileReader(this.inputFileTextField.getText())));
	            setInstances(inst);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void setInstances(Instances inst) {//Directly copied with slight modification from weka's ClustererPanel.java
		 String [] attribNames = new String [inst.numAttributes()];
		 for (int i = 0; i < inst.numAttributes(); i++) {
			 String type = "";
			 switch (inst.attribute(i).type()) {
			 	case Attribute.NOMINAL:
			 		type = "(Nom) ";
			 		break;
			 	case Attribute.NUMERIC:
			 		type = "(Num) ";
			 		break;
			 	case Attribute.STRING:
			 		type = "(Str) ";
			 		break;
			 	case Attribute.DATE:
			 		type = "(Dat) ";
			 		break;
			 	case Attribute.RELATIONAL:
			 		type = "(Rel) ";
			 		break;
			 	default:
			 		type = "(???) ";
			 }
			 String attnm = inst.attribute(i).name();
			 attribNames[i] = type + attnm;
		 }    		   
		 m_ClassCombo.setModel(new DefaultComboBoxModel(attribNames));
		 if (inst.classIndex() == -1)
			 m_ClassCombo.setSelectedIndex(attribNames.length - 1);
		 else
			 m_ClassCombo.setSelectedIndex(inst.classIndex());		    
	 }	 
	
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource().equals(this.inputFileButton)){
			loadInputFile();
		}else if(ae.getSource().equals(this.bottomStartButton)){
			start();
		}else if(ae.getSource().equals(this.bottomStopButton)){
			stop();
		}else if(ae.getSource().equals(this.outputFileButton)){
			setOutputLocation();
		}else if(ae.getSource().equals(this.clustererButton)){
			loadClusterer();
		}
	}
}
