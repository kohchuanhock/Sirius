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
==========================================================================*/package sirius.clustering.main;


import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;

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

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Clusterer;
import weka.core.Attribute;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.gui.GenericObjectEditor;
import weka.gui.PropertyPanel;

public class TrainClustererPane extends JComponent implements ActionListener{
	static final long serialVersionUID = sirius.Sirius.version;
	
	private JTextField numberOfClusterTextField;
	
	/** Lets the user configure the clusterer*/
	private GenericObjectEditor m_ClustererEditor = new GenericObjectEditor();
	/** The panel showing the current classifier one selection */
	private PropertyPanel m_CEPanel = new PropertyPanel(m_ClustererEditor);
	
	private JButton fileButton;
	private JTextField fileTextField;
	private JFrame parent;
	private JButton startButton = new JButton("Start");
	private JButton stopButton;	
	private JLabel statusLabel;
	private Thread clusterThread;
	private Clusterer clusterer;
	
	private JButton saveClustererButton;
	
	private JTextArea outputTextArea;
	private JScrollPane outputScrollPane;
	
	private JComboBox m_ClassCombo = new JComboBox();
	
	public TrainClustererPane(JFrame mainFrame){
		this.parent = mainFrame;	
		
		m_ClustererEditor.setClassType(Clusterer.class);
		
		JPanel filePanel = new JPanel(new BorderLayout());
		filePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Choose Training File"),
		    	BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		this.fileButton = new JButton("Choose");
		this.fileButton.addActionListener(this);
		this.fileTextField = new JTextField();
		this.fileTextField.setFocusable(false);
		filePanel.add(this.fileButton,BorderLayout.WEST);
		filePanel.add(this.fileTextField,BorderLayout.CENTER);
		
		JPanel clusteringMethodPanel = new JPanel(new GridLayout(1,1,5,5));
		clusteringMethodPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Choose a Clustering Method"),
		    	BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		clusteringMethodPanel.add(m_CEPanel);
		
		JPanel northPanel = new JPanel(new BorderLayout());		
		northPanel.add(filePanel,BorderLayout.NORTH);
		northPanel.add(clusteringMethodPanel,BorderLayout.CENTER);
		
		JPanel outputPanel = new JPanel(new GridLayout(1,1));
		outputPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Output"),
		    	BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		this.outputTextArea = new JTextArea();
		this.outputTextArea.setFocusable(false);
		outputScrollPane = new JScrollPane(this.outputTextArea);
		outputPanel.add(outputScrollPane);
		
		JPanel startStopButtonsPanel = new JPanel(new GridLayout(1,2,5,5));	
		this.startButton.addActionListener(this);		
		this.stopButton = new JButton("Stop");
		this.stopButton.addActionListener(this);
		this.stopButton.setEnabled(false);
		startStopButtonsPanel.add(this.startButton);
		startStopButtonsPanel.add(this.stopButton);
		
		JPanel buttonsPanel = new JPanel(new GridLayout(2,1,5,5));
		buttonsPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Controls"),
		    	BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		buttonsPanel.add(startStopButtonsPanel);
		this.saveClustererButton = new JButton("Save Clusterer");
		this.saveClustererButton.addActionListener(this);
		buttonsPanel.add(this.saveClustererButton);
		
		JPanel numberOfClusterPanel = new JPanel(new GridLayout(1,2,5,5));
		numberOfClusterTextField = new JTextField();
		this.numberOfClusterTextField.setEnabled(false);
		JLabel numberOfClusterLabel = new JLabel("Number of Clusters: ");
		numberOfClusterPanel.add(numberOfClusterLabel);
		numberOfClusterPanel.add(this.numberOfClusterTextField);				
		
		JPanel southPanel = new JPanel(new GridLayout(1,1));
		southPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Status"),
		    	BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		this.statusLabel = new JLabel(" ");
		southPanel.add(this.statusLabel);			
		
		JPanel classIndexPanel = new JPanel(new BorderLayout());
		classIndexPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Settings"),
		    	BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		JLabel classIndexLabel = new JLabel("Class: ");
		JPanel extraPanel = new JPanel(new BorderLayout());
		extraPanel.add(classIndexLabel,BorderLayout.WEST);
		extraPanel.add(this.m_ClassCombo,BorderLayout.CENTER);
		classIndexPanel.add(extraPanel, BorderLayout.NORTH);			
		
		JPanel emptyPanel1 = new JPanel();
		JPanel emptyPanel2 = new JPanel();
		
		JPanel centerNorthPanel = new JPanel(new GridLayout(1,4,5,5));
		centerNorthPanel.add(classIndexPanel);
		centerNorthPanel.add(buttonsPanel);
		centerNorthPanel.add(emptyPanel1);
		centerNorthPanel.add(emptyPanel2);
		
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(outputPanel,BorderLayout.CENTER);
		centerPanel.add(centerNorthPanel,BorderLayout.NORTH);
		
		setLayout(new BorderLayout());
		add(northPanel,BorderLayout.NORTH);		
		add(southPanel,BorderLayout.SOUTH);
		add(centerPanel,BorderLayout.CENTER);
	}
	
	public void actionPerformed(ActionEvent ae) {		
		if(ae.getSource().equals(this.fileButton)){
			loadFile();
		}else if(ae.getSource().equals(this.startButton)){
			start();
		}else if(ae.getSource().equals(this.stopButton)){
			stop();
		}else if(ae.getSource().equals(this.saveClustererButton)){
			saveClusterer();
		}
	}
	
	private void saveClusterer(){
		if(this.clusterer == null){
			JOptionPane.showMessageDialog(parent,"Please train clusterer first!","Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
		JFileChooser fc;				    	
		String lastLocation = SiriusSettings.getInformation("LastClusteringOutputLocation: ");
		if(lastLocation == null)
			fc = new JFileChooser();
		else
			fc = new JFileChooser(lastLocation);
    	FileNameExtensionFilter filter = new FileNameExtensionFilter(
	            "Clusterer Files", "clusterer");
	    fc.setFileFilter(filter);	
		int returnVal = fc.showSaveDialog(parent);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();	                        	
        	String savingFilename = file.getAbsolutePath();
			if(savingFilename.indexOf(".clusterer") == -1)
				savingFilename += ".clusterer";			
			try{									
				this.statusLabel.setText(" Saving..");
				FileOutputStream fos1 = new FileOutputStream(savingFilename);
		        ObjectOutputStream oos1 = new ObjectOutputStream(fos1);
		        oos1.writeObject(this.clusterer);
				oos1.close();		
				SiriusSettings.updateInformation("LastCLusteringOutputLocation: ", savingFilename);
				this.statusLabel.setText(" Saving..Done!");
			}catch(Exception e){e.printStackTrace();}
		}
	}
	
	private void start(){
		if(this.fileTextField.getText().length() == 0){
			JOptionPane.showMessageDialog(parent,"Please choose training file!","Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(m_ClustererEditor.getValue() == null){
			JOptionPane.showMessageDialog(parent,"Please choose clustering method!","Error",JOptionPane.ERROR_MESSAGE);
			return;
		}		
		if(clusterThread != null){
			JOptionPane.showMessageDialog(parent,"Cannot start training of Clusterer as another is running!","Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
		this.startButton.setEnabled(false);
		this.stopButton.setEnabled(true);
		this.numberOfClusterTextField.setText("");
		clusterThread = (new Thread(){   
			public void run(){
				try{	
					Instances inst = new Instances(new BufferedReader(new FileReader(fileTextField.getText())));
					inst.setClassIndex(m_ClassCombo.getSelectedIndex());
				    if(inst.classAttribute().isNumeric()) {
				    	JOptionPane.showMessageDialog(parent,"Class must be nominal!","Error",JOptionPane.ERROR_MESSAGE);
				    }else{
				    	outputTextArea.setText("");
						clusterer = (Clusterer) m_ClustererEditor.getValue();
						statusLabel.setText(" Training Clusterer..");
						clusterer.buildClusterer(removeClass(inst));
						ClusterEvaluation eval = new ClusterEvaluation();
					    eval.setClusterer(clusterer);					    
					    eval.evaluateClusterer(inst);					    
					    outputTextArea.append(eval.clusterResultsToString());
					    outputTextArea.append("\n");
						if(clusterer != null){
							numberOfClusterTextField.setText("" + clusterer.numberOfClusters());
							statusLabel.setText(" Clusterer Trained..");
						}
				    }
					startButton.setEnabled(true);
					stopButton.setEnabled(false);
					clusterThread = null;
				}catch(Exception e){e.printStackTrace();}
			}
		});
		clusterThread.setPriority(Thread.MIN_PRIORITY); // UI has most priority
		clusterThread.start();			
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
	
	private void stop(){
		this.clusterThread = null;
		this.clusterer = null;
		this.startButton.setEnabled(true);
		this.stopButton.setEnabled(false);
		this.statusLabel.setText(" Training Clusterer - Interrupted..");
	}
	
	private void loadFile(){
		try{
			JFileChooser fc;	    	
	    	String lastLocation = SiriusSettings.getInformation("LastClusteringOutputLocation: ");
    		if(lastLocation == null)
    			fc = new JFileChooser();
    		else
    			fc = new JFileChooser(lastLocation);	    	
	        FileNameExtensionFilter filter = new FileNameExtensionFilter("Arff File", "arff");
	        fc.setFileFilter(filter);			        			        			    	
			int returnVal = fc.showOpenDialog(parent);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
	            SiriusSettings.updateInformation("LastClusteringOutputLocation: ", file.getAbsolutePath());
	            this.fileTextField.setText(file.getAbsolutePath());
	            Instances inst = new Instances(new BufferedReader(new FileReader(fileTextField.getText())));
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
}
