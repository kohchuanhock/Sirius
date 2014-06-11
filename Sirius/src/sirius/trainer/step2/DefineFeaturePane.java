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
package sirius.trainer.step2;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import sirius.gpi.SortFeatureByMI;
import sirius.main.ApplicationData;
import sirius.trainer.features.Feature;
import sirius.trainer.features.GenerateArff;
import sirius.trainer.features.GenerateFeatures;
import sirius.trainer.features.gui.ClassifierFeatureDialog;
import sirius.trainer.features.gui.MetaFeatureDialog;
import sirius.trainer.features.gui.PhysioChemicalDialog;
import sirius.trainer.features.gui.SelectFeaturesDialog;
import sirius.trainer.features.gui.geneticalgorithm.GeneticAlgorithmDialog;
import sirius.trainer.features.gui.kgram.KgramDialog;
import sirius.trainer.features.gui.multiplekgram.MultipleKgramDialog;
import sirius.trainer.features.gui.positionspecificfeature.PositionRelatedDialog;
import sirius.trainer.features.gui.ratio.RatioDialog;
import sirius.trainer.main.SiriusSettings;
import sirius.trainer.main.StatusPane;
import sirius.trainer.step3.SelectFeaturePane;
import sirius.utils.ContinuousMI;
import sirius.utils.FastaFormat;

public class DefineFeaturePane extends JComponent implements ActionListener{
	static final long serialVersionUID = sirius.Sirius.version;
	
	private JInternalFrame parent;

	private JButton kgramButton;
	private JButton multiplekgramButton;
	private JButton ratioButton;
	private JButton markAllButton;
	private JButton markTopButton;
	private JTextField topTextField = new JTextField(5);
	private JButton unmarkAllButton;
	private JButton invertButton;
	private JButton removeFeatureButton;
	private JButton applyDPIButton = new JButton("Apply DPI");
	private JButton nextStepButton;
	private StatusPane statusPane;
	private JButton previousStepButton;
	private JButton saveFeaturesButton;
	private JButton loadFeaturesButton;
	public JButton generateFeaturesButton;
	private JButton geneticAlgorithmButton;
	private JButton physioFeaturesButton;
	private JButton classifierButton;
	private JButton metaButton;
	
	private FeatureTableModel featureTableModel;
	private JTable featureTable;
	private JTabbedPane tabbedPane;
	
	private ApplicationData applicationData;
	private SelectFeaturePane selectFeaturePane;
	
	private JLabel numberOfFeaturesLabel;
	
	private JPanel north;
	private JPanel featurePanel;
	private JPanel physioFeaturesPanel;
	
	private JPanel proteinPanel;
	private JComboBox countingStyleComboBox;
	private JComboBox scoringMatrixComboBox;
	
	private JButton featureFileManipulationButton;
	private JButton positionRelatedButton;
	public Boolean stopGenerating;	
	public boolean generatingFeatures;
	
	public FeatureTableModel getFeatureTableModel(){return this.featureTableModel;}

    public DefineFeaturePane(JInternalFrame frame,JTabbedPane tabbedPane,ApplicationData applicationData,
    	SelectFeaturePane selectFeaturePane) {
    	parent = frame;
    	this.statusPane = applicationData.getStatusPane();
    	if(this.statusPane == null)
    		this.statusPane = new StatusPane("");
    	this.applicationData = applicationData;
    	this.selectFeaturePane = selectFeaturePane;    	
    	this.tabbedPane = tabbedPane;
    	setLayout(new BorderLayout());    			
    	
    	JPanel autoGeneratePanel = new JPanel(new FlowLayout());
    	autoGeneratePanel.setBorder(BorderFactory.createTitledBorder("Auto-Generate"));
    	geneticAlgorithmButton = new JButton("Genetic Algorithm");
    	geneticAlgorithmButton.addActionListener(this);
    	autoGeneratePanel.add(geneticAlgorithmButton);
    	
    	featurePanel = new JPanel(new FlowLayout());    		
    	featurePanel.setBorder(BorderFactory.createTitledBorder("Choose a feature type to add"));	
		
    	this.metaButton = new JButton("META");
    	this.metaButton.addActionListener(this);
    	this.featurePanel.add(this.metaButton);
    	
		//kgramButton = new JButton("K-grams with X mistakes");
    	this.classifierButton = new JButton("Classifier");
		this.classifierButton.addActionListener(this);
		this.featurePanel.add(this.classifierButton);
		
    	this.positionRelatedButton = new JButton("Position-Related");
    	kgramButton = new JButton("Kgrams");
		multiplekgramButton = new JButton("Multiple Kgrams with X mistakes, min Y and max Z gaps");
		multiplekgramButton = new JButton("Multiple Kgrams");
		ratioButton = new JButton("Ratio");
		this.positionRelatedButton.addActionListener(this);
		kgramButton.addActionListener(this);
		multiplekgramButton.addActionListener(this);
		ratioButton.addActionListener(this);
		this.featurePanel.add(this.positionRelatedButton);
		featurePanel.add(kgramButton);
		featurePanel.add(multiplekgramButton);
		featurePanel.add(ratioButton);
		
		physioFeaturesPanel = new JPanel();
		physioFeaturesButton = new JButton("Physiochemical");
		physioFeaturesButton.addActionListener(this);
		physioFeaturesPanel.add(physioFeaturesButton);		
			
    	north = new JPanel(new BorderLayout());
    	north.add(autoGeneratePanel,BorderLayout.WEST);    	  
		north.add(featurePanel,BorderLayout.CENTER);	    	
				
		
		scoringMatrixComboBox = new JComboBox();
		scoringMatrixComboBox.addItem("Identity");
		scoringMatrixComboBox.addItem("Blosum 62");
		scoringMatrixComboBox.addItem("Structure-Derived");		
		JPanel scoringMatrixPanel = new JPanel();
		scoringMatrixPanel.setBorder(BorderFactory.createTitledBorder("Scoring Matrix"));
		scoringMatrixPanel.add(scoringMatrixComboBox);
						
		countingStyleComboBox = new JComboBox();
		countingStyleComboBox.addItem("+1              ");
		countingStyleComboBox.addItem("+Score          ");	
		JPanel countingStylePanel = new JPanel();
		countingStylePanel.setBorder(BorderFactory.createTitledBorder("Counting Style"));		
		countingStylePanel.add(countingStyleComboBox);				
		
		proteinPanel = new JPanel(new GridLayout(1,2));
		proteinPanel.add(scoringMatrixPanel);
		proteinPanel.add(countingStylePanel);
							
		JPanel center = new JPanel(new GridLayout(1,1));		
		//Center Left
		JPanel center_left = new JPanel(new BorderLayout());
		center.add(center_left);		
		center_left.setBorder(BorderFactory.createTitledBorder("Features to Generate"));
		JPanel center_left_north = new JPanel(new BorderLayout());		
		JPanel center_left_north_button = new JPanel(new FlowLayout());
		center_left.add(center_left_north,BorderLayout.NORTH);
		markAllButton = new JButton("Mark All");
		markAllButton.addActionListener(this);
		unmarkAllButton = new JButton("Unmark All");
		unmarkAllButton.addActionListener(this);
		invertButton = new JButton("Invert");
		invertButton.addActionListener(this);
		this.markTopButton = new JButton("Mark Top");
		this.markTopButton.addActionListener(this);
		JPanel center_left_north_label = new JPanel(new GridLayout(1,1));
		numberOfFeaturesLabel = new JLabel(" Features: 0");
		center_left_north_label.add(numberOfFeaturesLabel);
		center_left_north_button.add(this.markTopButton);
		center_left_north_button.add(this.topTextField);
		center_left_north_button.add(markAllButton);
		center_left_north_button.add(unmarkAllButton);
		center_left_north_button.add(invertButton);
		center_left_north.add(center_left_north_label,BorderLayout.WEST);
		center_left_north.add(center_left_north_button,BorderLayout.CENTER);
		JPanel center_left_north_east_button = new JPanel(new FlowLayout());
		featureFileManipulationButton = new JButton("Feature File Manipulation");
		featureFileManipulationButton.addActionListener(this);
		center_left_north_east_button.add(featureFileManipulationButton);
		center_left_north.add(center_left_north_east_button,BorderLayout.EAST);
		
		featureTableModel = new FeatureTableModel(false,this);
		featureTable = new JTable(featureTableModel);
		JScrollPane featureTableScrollPane = new JScrollPane(featureTable);
		center_left.add(featureTableScrollPane,BorderLayout.CENTER);
		featureTable.getColumnModel().getColumn(0).setMaxWidth(50);
        featureTable.getColumnModel().getColumn(1).setMaxWidth(20); 
        featureTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);   	
		
		JPanel center_left_south = new JPanel(new FlowLayout());		
		removeFeatureButton = new JButton("Remove Marked Features");
		removeFeatureButton.addActionListener(this);
		center_left_south.add(removeFeatureButton);
		center_left.add(center_left_south,BorderLayout.SOUTH);	
				
		GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
		JPanel south = new JPanel(gridbag);			
		previousStepButton = new JButton("<<< BACK");
		previousStepButton.addActionListener(this);	
		c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        gridbag.setConstraints(previousStepButton,c);
		saveFeaturesButton = new JButton("Save Features To File");
		saveFeaturesButton.addActionListener(this);
		gridbag.setConstraints(saveFeaturesButton,c);
		loadFeaturesButton = new JButton("Load Features From File");
		loadFeaturesButton.addActionListener(this);
		gridbag.setConstraints(loadFeaturesButton,c);
		nextStepButton = new JButton("NEXT >>>");
		nextStepButton.addActionListener(this);
		this.applyDPIButton.addActionListener(this);
		gridbag.setConstraints(nextStepButton,c);
		generateFeaturesButton = new JButton("Generate Features");
		generateFeaturesButton.addActionListener(this);
		gridbag.setConstraints(generateFeaturesButton,c);
		gridbag.setConstraints(this.applyDPIButton, c);
		south.add(previousStepButton);
		south.add(saveFeaturesButton);
		south.add(loadFeaturesButton);
		south.add(generateFeaturesButton);
		//south.add(this.applyDPIButton);
		south.add(nextStepButton);		
		
		add(center,BorderLayout.CENTER);	
		add(north,BorderLayout.NORTH);
		add(south,BorderLayout.SOUTH);				
    }       
    	
    public void modifyNorthPanel(){    				    	   	
		if(applicationData.getSequenceType().indexOf("PROTEIN") != -1){
			north.add(proteinPanel,BorderLayout.EAST);
			featurePanel.add(physioFeaturesPanel);
		}else{
			north.remove(proteinPanel);			
			featurePanel.remove(physioFeaturesPanel);
		}
    }
    
    public void generateFeatures(boolean fromUI){
    	if(featureTableModel.getRowCount() <= 0){
			JOptionPane.showMessageDialog(parent,
				"There are no features to generate!",
				"ERROR",JOptionPane.ERROR_MESSAGE);
		}else{    					
			if(fromUI){
				statusPane.setText("Generating Dataset1.arff..");
				this.generateFeaturesButton.setText("Stop Generation!");
				if(applicationData.getSequenceType().indexOf("PROTEIN") != -1){			
					applicationData.setScoringMatrixIndex(scoringMatrixComboBox.getSelectedIndex());
					applicationData.setCountingStyleIndex(countingStyleComboBox.getSelectedIndex());
				}  
			}
			applicationData.setDataset1Instances(null);
			applicationData.setStep2FeatureTableModel(featureTableModel);
			new GenerateFeatures(parent,applicationData,tabbedPane,
					selectFeaturePane, this.previousStepButton, this);	
			/*
			try{
				while(this.applicationData.getOneThread() != null)
					Thread.sleep(1000);
				List<Feature> selectedList = new GreedyForwardSubsetSearch().selectSubset(featureTableModel.getData(), 
					this.applicationData.getWorkingDirectory() + File.separator + "SubsetSelectionScores.txt", this.applicationData);			
				for(int x = 0; x < selectedList.size(); x++)
					System.out.println(selectedList.get(x).saveString(null));
			}catch(Exception e){e.printStackTrace();}*/
		}    		
    }
    
    public static ArrayList<Feature> loadFeatureFile(String inputFilename, ApplicationData applicationData){
    	try{
	    	File file = new File(inputFilename);
	    	BufferedReader in = new BufferedReader(new FileReader (inputFilename));
	    	boolean foundAtLeastOne = false;
	    	String line;	            	
	    	boolean rejectedAtLeastOne = false;
	    	ArrayList<Feature> loadedFeatureList = new ArrayList<Feature>();
	    	while ((line = in.readLine()) != null){
	    		if(line.indexOf("Step 2: ") != -1){
	    			foundAtLeastOne = true;			            			
	    			//For load settings
	    			Feature tempData = Feature.loadSettings(applicationData, file.getParent(), line);
	    			if(tempData != null)
	    				loadedFeatureList.add(tempData);    		
	    			else if(rejectedAtLeastOne == false)
	    				rejectedAtLeastOne = true;
	    		}
	    	}
	    	if(rejectedAtLeastOne == true){
	    		JOptionPane.showMessageDialog(null,"Note that not all features in " + 
	    				inputFilename + " are added.\n" + 
	    			"Rejected features are those with windowFrom < 0" + 
	    			" because the sequences +1_Index is -1","Warning", 
	    			JOptionPane.WARNING_MESSAGE);
	    	}
	    	if(foundAtLeastOne == false){
	    		System.out.println(inputFilename + " does not contains Features");
	    		JOptionPane.showMessageDialog(null,inputFilename + 
	    			" does not contains Features","Load Features", 
	    			JOptionPane.WARNING_MESSAGE);			            	
	    	}       
	    	in.close();
	    	return loadedFeatureList;	    	
    	}catch(Exception e){e.printStackTrace();return null;}
    	
    }
    
    public void loadFeatures(String fileAbsolutePath, String fileParent) throws Exception{    	
    	BufferedReader in = new BufferedReader(new FileReader (fileAbsolutePath));
    	boolean foundAtLeastOne = false;
    	String line;
    	//featureTableModel.setEmpty();			            	
    	boolean rejectedAtLeastOne = false;
    	/*
    	 * Do ensure that same feature is not loaded twice
    	 */
    	Set<String> featureNameSet = new HashSet<String>();
    	List<Feature> featureList = this.featureTableModel.getData();
    	for(Feature f:featureList) featureNameSet.add(f.getName());
    	
    	while ((line = in.readLine()) != null){
    		//if(line.indexOf("Step 2: ") != -1){
    			foundAtLeastOne = true;			            			
    			//For load settings
    			//Feature tempData = new Feature(applicationData);
    			//accepted = tempData.loadSettings(file.getParent(), line);
    			Feature tempData = Feature.loadSettings(applicationData, fileParent, line);
    			if(tempData != null){
    				if(featureNameSet.contains(tempData.getName()) == false){
    					//Only add if not already exists
    					this.featureTableModel.add(tempData);
    					featureNameSet.add(tempData.getName());
    				}
    			}else if(rejectedAtLeastOne == false){
    				rejectedAtLeastOne = true;
    			}
    		//}
    	}
    	if(rejectedAtLeastOne == true)
    		JOptionPane.showMessageDialog(parent,"Note that not all features in " + 
    				fileAbsolutePath + " are added.\n" + 
    			"Rejected features are those with windowFrom < 0" + 
    			" because the sequences +1_Index is -1","Warning", 
    			JOptionPane.WARNING_MESSAGE);			            		
    	if(foundAtLeastOne)
    		statusPane.setText("Features Loaded From " + fileAbsolutePath);
    	else{
    		System.out.println(fileAbsolutePath + " does not contains Features");
    		JOptionPane.showMessageDialog(parent,fileAbsolutePath + 
    			" does not contains Features","Load Features", 
    			JOptionPane.WARNING_MESSAGE);			            	
    	}
        statusPane.setText("Features Loaded From " + fileAbsolutePath + 
        	"... Done!");
    	in.close();	
    }
    
    private void loadFeatures(){
    	if(applicationData.getOneThread() == null){
    		applicationData.setOneThread(new Thread(){	      	
			public void run(){
				try{		
					JFileChooser fc;				    	
					String lastLocation = SiriusSettings.getInformation("LastLoadFeatureLocation: ");	
					if(lastLocation == null)
						fc = new JFileChooser(applicationData.getWorkingDirectory());
					else 
						fc = new JFileChooser(lastLocation);
			    	FileNameExtensionFilter filter = new FileNameExtensionFilter("Features Files", "features");
				    fc.setFileFilter(filter);	
					int returnVal = fc.showOpenDialog(parent);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
			            File file = fc.getSelectedFile();
			            SiriusSettings.updateInformation("LastLoadFeatureLocation: ", fc.getSelectedFile().getAbsolutePath());
			            statusPane.setText("Reading " + file.getAbsolutePath() + "...");
			            loadFeatures(file.getAbsolutePath(), file.getParent());
		            	updateNumberOfFeaturesLabel();			            		            		
		    		}		
				}    
	  			catch(Exception e){
	  				e.printStackTrace();
	  				JOptionPane.showMessageDialog(parent,e.getMessage(),"ERROR",JOptionPane.ERROR_MESSAGE);
	  			}
	  			applicationData.setOneThread(null);
			}});
      		applicationData.getOneThread().setPriority(Thread.MIN_PRIORITY); // UI has most priority
      		applicationData.getOneThread().start();	      		
		}
		else{
			JOptionPane.showMessageDialog(parent,"Can't load features now,\n"
	      			+ "currently busy with other IO","Load Features", JOptionPane.WARNING_MESSAGE);
		}
    }
    
    private void saveFeatures(){
    	if(applicationData.getOneThread() == null){
    		applicationData.setOneThread(new Thread(){	      	
			public void run(){
				if(featureTableModel.getRowCount() <= 0){
	    			JOptionPane.showMessageDialog(parent,"There are no features to save!",
	    				"ERROR",JOptionPane.ERROR_MESSAGE);
	    		}else{
	    			try{
	    				JFileChooser fc;				    	
				    	fc = new JFileChooser(applicationData.getWorkingDirectory());
				    	FileNameExtensionFilter filter = new FileNameExtensionFilter("Features Files", "features");
					    fc.setFileFilter(filter);	
						int returnVal = fc.showSaveDialog(parent);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
		    				File file = fc.getSelectedFile();				        
							String savingFilename = file.getAbsolutePath();
							if(savingFilename.indexOf(".features") == -1)
								savingFilename += ".features";
		    				BufferedWriter output = new BufferedWriter(new FileWriter(savingFilename));			
		    				for(int x = 0; x < featureTableModel.getRowCount();  x++){
		    					output.write("Step 2: " + featureTableModel.saveString(x, file.getParent()));
		    					output.newLine();
		    				}  	    				
		    				output.close();
		    				statusPane.setText("Features are save to " + savingFilename);
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
     		JOptionPane.showMessageDialog(parent,"Can't save features now,\n"
      			+ "currently busy with other IO","Save Features", JOptionPane.WARNING_MESSAGE);
    	}	
    }
    
    public void dpiFiltering() throws Exception{
    	//Maybe can try the bootstrap selection idea in my head
		List<Feature> featureList = this.featureTableModel.getData();		
		List<FastaFormat> posFastaList = new ArrayList<FastaFormat>();
		List<FastaFormat> negFastaList = new ArrayList<FastaFormat>();
		ApplicationData.obtainDataset1FastaSequences(this.applicationData, posFastaList, negFastaList);		
		int ratio;
		int size;
		if(posFastaList.size() < negFastaList.size()){
			ratio = negFastaList.size() / posFastaList.size();
			size = negFastaList.size() + posFastaList.size() * ratio;
		}else{
			ratio = posFastaList.size() / negFastaList.size();
			size = posFastaList.size() + negFastaList.size() * ratio;
		}
		this.statusPane.setPrefix("Computing feature values.. ");
		for(int i = 0; i < featureList.size(); i++){
			this.statusPane.setText(i + " / " + featureList.size());
			double posd[] = new double[posFastaList.size()];
			double negd[] = new double[negFastaList.size()];
			for(int j = 0; j < posFastaList.size(); j++){							
				Object obj = GenerateArff.getMatchCount(posFastaList.get(j),
					featureList.get(i),this.scoringMatrixComboBox.getSelectedIndex(),
						this.countingStyleComboBox.getSelectedIndex(),
						this.applicationData.getScoringMatrix());
				if(obj.getClass().getName().equalsIgnoreCase("java.lang.Double"))
					posd[j] = (Double)obj;
 				else//Assume Integer - else exception will be thrown
 					posd[j] = (Integer)obj;
			}
			for(int j = 0; j < negFastaList.size(); j++){							
				Object obj = GenerateArff.getMatchCount(negFastaList.get(j),
					featureList.get(i),this.scoringMatrixComboBox.getSelectedIndex(),
					this.countingStyleComboBox.getSelectedIndex(),
					this.applicationData.getScoringMatrix());
				if(obj.getClass().getName().equalsIgnoreCase("java.lang.Double"))
					negd[j] = (Double)obj;
 				else//Assume Integer - if not, exception will be thrown
 					negd[j] = (Integer)obj;
			}			
			/*
			 * Oversample by direct multiplying
			 */
			if(posFastaList.size() < negFastaList.size()){				
				double fullPosD[] = new double[posFastaList.size() * ratio];
				for(int j = 0, k = 0; j < fullPosD.length; j++, k++){
					fullPosD[j] = posd[k];
					if(k == posd.length - 1) k = 0;
				}
				double full[] = new double[size];
				int index = 0;
				for(int j = 0; j < fullPosD.length; j++, index++){
					full[index] = fullPosD[j];
				}
				for(int j = 0; j < negd.length; j++, index++){
					full[index] = negd[j];
				}
				featureList.get(i).setValueList(full);
			}else{				
				double fullNegD[] = new double[negFastaList.size() * ratio];
				for(int j = 0, k = 0; j < fullNegD.length; j++, k++){
					fullNegD[j] = negd[k];
					if(k == negd.length - 1) k = 0;
				}
				double full[] = new double[size];
				int index = 0;
				for(int j = 0; j < posd.length; j++, index++){
					full[index] = posd[j];
				}
				for(int j = 0; j < fullNegD.length; j++, index++){
					full[index] = fullNegD[j];				
				}
				featureList.get(i).setValueList(full);
			}			
		}
		double[] classValueList = new double[size];
		int posSize;
		if(posFastaList.size() < negFastaList.size()){
			posSize = posFastaList.size() * ratio;
		}else{
			posSize = posFastaList.size();
		}
		for(int j = 0; j < size; j++){
			if(j < posSize) classValueList[j] = 0.0;
			else classValueList[j] = 1.0;
		}		
		for(int i = 0; i < featureList.size(); i++){			
			featureList.get(i).setMI(
					ContinuousMI.MIUsingCellucciMethod(featureList.get(i).getValueList(), 
							classValueList,	true));
		}
		this.statusPane.setPrefix("Running DPI.. ");
		//double epsilon = 1.15;
		double maxSU = 0.25;		
		//List<Feature> selectedFeatureList = this.quickEstimateMBUsingDPI(featureList, epsilon);
		List<Feature> selectedFeatureList = featureList;
		this.statusPane.setPrefix("Removing features by SU.. ");
		for(int x = 0; x < selectedFeatureList.size(); x++){
			this.statusPane.setText(x + " / " + selectedFeatureList.size());
			for(int y = x + 1; y < selectedFeatureList.size();){
				double su = ContinuousMI.SUUsingCellucciMethod(selectedFeatureList.get(x).getValueList(), 
						selectedFeatureList.get(y).getValueList(), true);
				if(su > maxSU){
					selectedFeatureList.remove(y);
				}else{
					y++;
				}
			}
		}
		this.featureTableModel.setData(selectedFeatureList);
		this.statusPane.setPrefix("");
		this.statusPane.setText("Apply DPI.. Done!");
		this.updateNumberOfFeaturesLabel();
    }
    
    public List<Feature> quickEstimateMBUsingDPI(List<Feature> originalList, 
			double epsilon){
		/*
		 * Cannot put this into Commons for two reasons
		 * 
		 * 1) Feature is different in Sirius and Regulus
		 * 2) Because of 1, Sirius will reference Commons and Commons will reference Sirius if this method is to 
		 * be moved to Commons
		 * 
		 * Hence to solve this problem, have to move Sirius Feature class to Commons first
		 */
		List<Feature> featureList = new ArrayList<Feature>(originalList);
		Collections.sort(featureList, new SortFeatureByMI());
		for(int i = 0; i < featureList.size(); i++){
			this.statusPane.setText(i + " / " + featureList.size());
			double miic = featureList.get(i).getMutualInformation();
			double[] vList = featureList.get(i).getValueList();
			for(int j = i + 1; j < featureList.size();){
				double miij = ContinuousMI.MIUsingCellucciMethod(vList, 
						featureList.get(j).getValueList(), true);
				double mijc = featureList.get(j).getMutualInformation();
				if((mijc * epsilon) < miij && (mijc * epsilon) < miic){
					//i is between j and c - remove j		
					featureList.remove(j);
				}else{
					j++;
				}
			}
		}
		return featureList;
	}
    
    public void actionPerformed(ActionEvent ae){
    	if(ae.getSource().equals(this.metaButton)){
    		MetaFeatureDialog dialog = new MetaFeatureDialog(parent,featureTableModel);    		
    		dialog.setLocationRelativeTo(parent);    		
    		dialog.setVisible(true); 
    		updateNumberOfFeaturesLabel();
    	}else if(ae.getSource().equals(this.classifierButton)){
    		ClassifierFeatureDialog dialog = new ClassifierFeatureDialog(parent,featureTableModel);
    		dialog.setLocationRelativeTo(parent);    		
    		dialog.setVisible(true); 
    		updateNumberOfFeaturesLabel();
    	}else if(ae.getSource().equals(physioFeaturesButton)){
    		PhysioChemicalDialog dialog = new PhysioChemicalDialog(parent,featureTableModel,applicationData,null);
    		dialog.setLocationRelativeTo(parent);    		
    		dialog.setVisible(true); 
    		updateNumberOfFeaturesLabel();
    	}else if(ae.getSource().equals(featureFileManipulationButton)){
    		SelectFeaturesDialog dialog = new SelectFeaturesDialog(parent,applicationData,this);
    		dialog.setLocationRelativeTo(parent);    		
    		dialog.setVisible(true); 
    	}else if(ae.getSource().equals(geneticAlgorithmButton)){
    		//This is updated so that during the GA, we know what scoring and counting scheme to use.
    		/*if(applicationData.getSequenceType().indexOf("PROTEIN") != -1){
				applicationData.setScoringMatrixIndex(scoringMatrixComboBox.getSelectedIndex());
				applicationData.setCountingStyleIndex(countingStyleComboBox.getSelectedIndex());
			} */   
    		GeneticAlgorithmDialog dialog = new GeneticAlgorithmDialog(this.applicationData,this.featureTableModel);    		        		
    		dialog.pack();
    		dialog.setLocationRelativeTo(parent);
    		dialog.setVisible(true);      		     		
    	}else if(ae.getSource().equals(this.positionRelatedButton)){
    		PositionRelatedDialog dialog = new PositionRelatedDialog(parent,featureTableModel,applicationData,null);
    		dialog.setLocationRelativeTo(parent);    		
    		dialog.setVisible(true);      		
    		updateNumberOfFeaturesLabel();
    	}else if(ae.getSource().equals(kgramButton)){
    		KgramDialog dialog = new KgramDialog(parent,featureTableModel,applicationData, null);
    		dialog.setLocationRelativeTo(parent);    		
    		dialog.setVisible(true);      		
    		updateNumberOfFeaturesLabel();
    	}else if(ae.getSource().equals(multiplekgramButton)){
    		MultipleKgramDialog dialog = new MultipleKgramDialog(parent,featureTableModel,applicationData, null);
    		dialog.setLocationRelativeTo(parent);
    		dialog.setVisible(true);
    		updateNumberOfFeaturesLabel();
    	}else if(ae.getSource().equals(ratioButton)){
    		RatioDialog dialog = new RatioDialog(parent,featureTableModel,applicationData, null);
    		dialog.setLocationRelativeTo(parent);
    		dialog.setVisible(true); 
    		updateNumberOfFeaturesLabel();
    	}else if(ae.getSource().equals(this.markTopButton)){
    		try{
    			int topX = Integer.parseInt(this.topTextField.getText());
    			this.featureTableModel.markTop(topX);    			
    		}catch(Exception e){JOptionPane.showMessageDialog(this, "Invalid value in Top field. Integer Only.", "Invalid value", JOptionPane.ERROR_MESSAGE);}
    	}else if(ae.getSource().equals(markAllButton)){ 
    		featureTableModel.markAll();   	
    	}else if(ae.getSource().equals(unmarkAllButton)){
    		featureTableModel.unmarkAll();
    	}else if(ae.getSource().equals(invertButton)){
    		featureTableModel.invertBox();
    	}else if(ae.getSource().equals(removeFeatureButton)){
    		featureTableModel.removeMarked();
    		featureTable.clearSelection();
    		updateNumberOfFeaturesLabel();
    	}else if(ae.getSource().equals(nextStepButton)){	
			tabbedPane.setEnabledAt(1,false);
			tabbedPane.setSelectedIndex(2);
	    	tabbedPane.setEnabledAt(2,true);  	    	
		}else if(ae.getSource().equals(this.applyDPIButton)){
			if(applicationData.getOneThread() == null){
				applicationData.setOneThread(new Thread(){
					public void run(){
						try{
						dpiFiltering();
						}catch(Exception e){e.printStackTrace();}
						applicationData.setOneThread(null);
					}
				});	
				applicationData.getOneThread().setPriority(Thread.MIN_PRIORITY);
				applicationData.getOneThread().start();
			}else{
				JOptionPane.showMessageDialog(parent,"Can't run DPI now,\n"
			      		+ "currently busy with other process","Apply DPI", JOptionPane.WARNING_MESSAGE);
			}
		}else if(ae.getSource().equals(generateFeaturesButton)){
			if(this.generateFeaturesButton.getText().equalsIgnoreCase("Generate Features")){
				this.stopGenerating = false;
				generateFeatures(true);
			}else{
				this.stopGenerating = true;
				this.generateFeaturesButton.setText("Generate Features");
			}
			
		}else if(ae.getSource().equals(previousStepButton)){
			tabbedPane.setSelectedIndex(0);
			tabbedPane.setEnabledAt(0,true);
			tabbedPane.setEnabledAt(1,false);
		}else if(ae.getSource().equals(saveFeaturesButton)){
			saveFeatures();
		}else if(ae.getSource().equals(loadFeaturesButton)){
			loadFeatures();
	    } 
    }
    
    public FeatureTableModel getStep2FeatureTableModel(){
    	return featureTableModel;
    }
    
    public void updateNumberOfFeaturesLabel(){
    	int rowCount = featureTableModel.getRowCount();
    	//Reset it to 0 if it is -1 as -1 is return just because the model is empty.
    	if(rowCount == -1)
    		rowCount = 0;
    	numberOfFeaturesLabel.setText(" Features: " + rowCount);
    }
}

