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
package sirius.dotplot.main;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*; 

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.text.DecimalFormat;
import java.util.*;


public class DotPlotPane extends JInternalFrame implements ActionListener, MouseListener, ComponentListener, MouseMotionListener{
	static final long serialVersionUID = sirius.Sirius.version;
	private DotPlotGraphPane dotPlotGraphPane;
	private DotPlotViewPort dotPlotViewPort;
	private JScrollPane dotPlotGraphScrollPane;	
	private JTextField inputFile1TextField;	
	private JTextField inputFile2TextField;
	private JTextField windowSizeTextField;
	private JTextField stepUpTextField;
	private JTextField matrixTextField;	
	private JTextField similarityLimitTextField;
	private JButton generateDotPlotButton;
	private JButton estimateSimilarityLimitButton;
	private JLabel runTimeLabel;
	private JProgressBar percentageDoneProgressBar;
	private JTextField runTimeTextField;	
	private JTextField estTimeToCompleteTextField;	
	private JButton printScreenButton;
	private JButton panButton;	
	
	private String lastDirectoryOpened;
	private JFrame parent;
	
	private Process dotPlotProcess;
	private Thread dotPlotThread;
	private JButton zoomOutButton;
	private JButton estimateDButton;
	private ArrayList<TwoSequencePoint> zoomer;
	
	private JPanel controlsPanel;
	private JSlider kmismatchSlider;
	private JSlider intensitySlider;
	
	private long trainTimeElasped;
	
	private JWindow toolTip;
	private JLabel label;
	private JWindow toolTip2;
	private JLabel label2;
	private int labelSequence1Start;
	private int labelSequence2Start;
	private int labelSequence1End;
	private int labelSequence2End;
	
	private JButton loadDataButton;
	private JButton saveDataButton;

	private DecimalFormat df;
	private int sequence1Length;
	private int sequence2Length;
	
	private int kmismatchOldValue;	
	private JTextField qTextField;
	
	public DotPlotPane(final JFrame parent){		
		super("Dot Plot",true,true,true,true);
			
		this.parent = parent;
		df = new DecimalFormat("0.##");
		
		zoomer = new ArrayList<TwoSequencePoint>();
				
		final int fullSizeTextField = 6;
			
		this.inputFile1TextField = new JTextField(fullSizeTextField);
		this.inputFile1TextField.setEnabled(false);
		this.inputFile1TextField.setText("Click Here!");
		this.inputFile1TextField.addMouseListener(this);		
		this.inputFile2TextField = new JTextField(fullSizeTextField);
		this.inputFile2TextField.setEnabled(false);
		this.inputFile2TextField.addMouseListener(this);
		this.inputFile2TextField.setText("Click Here!");
		
		
		JPanel sequencesPanel = new JPanel(new BorderLayout());
		sequencesPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Sequences"),BorderFactory.createEmptyBorder(0, 5, 5, 5)));			
		JPanel sequencePanelCenter = new JPanel(new GridLayout(2,2,5,5));
		JLabel inputFile1Label = new JLabel("Sequence 1: ",SwingConstants.RIGHT);
		JLabel inputFile2Label = new JLabel("Sequence 2: ",SwingConstants.RIGHT);			
		
		sequencePanelCenter.add(inputFile1Label);		               
		sequencePanelCenter.add(inputFile1TextField);							
		sequencePanelCenter.add(inputFile2Label);		
		sequencePanelCenter.add(inputFile2TextField);
		
		sequencesPanel.add(sequencePanelCenter, BorderLayout.CENTER);		
		
		JPanel settingsPanel = new JPanel(new BorderLayout());
		settingsPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Settings"),BorderFactory.createEmptyBorder(0, 5, 5, 5)));			
					
		JPanel settingsPanelWest = new JPanel(new GridLayout(5,2,5,5));	
		JLabel matrixLabel = new JLabel("Similarity Matrix: ",SwingConstants.RIGHT);
		JLabel windowSizeLabel = new JLabel("Window Size: ",SwingConstants.RIGHT);
		JLabel stepUpLabel = new JLabel("Step Size: ", SwingConstants.RIGHT);
		JLabel similarityLimitLabel = new JLabel("Similarity Limit: ",SwingConstants.RIGHT);		
		JLabel qLabel = new JLabel("q: ",SwingConstants.RIGHT);
		this.matrixTextField = new JTextField(fullSizeTextField);
		this.matrixTextField.setEnabled(false);
		this.matrixTextField.addMouseListener(this);	
		this.matrixTextField.setText("Click Here!");
		this.windowSizeTextField = new JTextField(3);
		this.stepUpTextField = new JTextField(3);
		this.similarityLimitTextField = new JTextField(3);
		this.qTextField = new JTextField(3);
		this.kmismatchSlider = new JSlider(0,0);
		this.kmismatchSlider.setEnabled(false);
		this.kmismatchSlider.setMajorTickSpacing(1);					
		this.kmismatchSlider.setSnapToTicks(true);		
										               		
		settingsPanelWest.add(matrixLabel);
		settingsPanelWest.add(matrixTextField);		
		settingsPanelWest.add(windowSizeLabel);
		settingsPanelWest.add(windowSizeTextField);		
		settingsPanelWest.add(stepUpLabel);
		settingsPanelWest.add(stepUpTextField);
		settingsPanelWest.add(similarityLimitLabel);
		settingsPanelWest.add(similarityLimitTextField);
		settingsPanelWest.add(qLabel);
		settingsPanelWest.add(qTextField);
		
		settingsPanel.add(settingsPanelWest, BorderLayout.CENTER);		
			
		JPanel graphPanel = new JPanel(new BorderLayout());
		graphPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Graph"),BorderFactory.createEmptyBorder(0, 2, 2, 2)));					
		dotPlotGraphPane = new DotPlotGraphPane(parent);
		dotPlotViewPort = new DotPlotViewPort(dotPlotGraphPane);    
    	graphPanel.add(dotPlotGraphPane,BorderLayout.CENTER);
    	dotPlotGraphScrollPane = new JScrollPane();
    	dotPlotGraphScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    	dotPlotGraphScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
    	dotPlotGraphScrollPane.addComponentListener(this);
    	dotPlotGraphScrollPane.addMouseListener(this);    
    	this.dotPlotGraphScrollPane.addMouseMotionListener(this);
    	dotPlotViewPort.setView(dotPlotGraphPane);  
    	dotPlotGraphScrollPane.setViewport(dotPlotViewPort);    	
    	dotPlotGraphScrollPane.setBorder(BorderFactory.createCompoundBorder(
    	BorderFactory.createTitledBorder("Dot Plot Graph"),
    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));    	
    	this.add(dotPlotGraphScrollPane, BorderLayout.CENTER); 
    	dotPlotGraphPane.setParentDimension(dotPlotGraphScrollPane.getSize());
		
    	JPanel statusPanel = new JPanel(new GridLayout(3,2,5,5));    	    	
    	this.percentageDoneProgressBar = new JProgressBar(0,100);
    	this.percentageDoneProgressBar.setStringPainted(true);
    	this.runTimeLabel = new JLabel("Elasped Time: ", SwingConstants.RIGHT);
    	this.runTimeTextField = new JTextField(fullSizeTextField);
    	this.runTimeTextField.setEnabled(false);    	
    	JLabel estTimeToCompleteLabel = new JLabel("Est Time Remaining: ", SwingConstants.RIGHT);
    	this.estTimeToCompleteTextField = new JTextField(fullSizeTextField);
    	this.estTimeToCompleteTextField.setEnabled(false);
    	JLabel percentageDoneLabel = new JLabel("Percentage Done: ",SwingConstants.RIGHT);    	
    	statusPanel.add(runTimeLabel);
    	statusPanel.add(runTimeTextField);    	
    	statusPanel.add(estTimeToCompleteLabel);
    	statusPanel.add(this.estTimeToCompleteTextField);
    	statusPanel.add(percentageDoneLabel);    	
    	statusPanel.add(this.percentageDoneProgressBar);
		
		JPanel statusMainPanel = new JPanel(new BorderLayout());
		statusMainPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Status"),BorderFactory.createEmptyBorder(0, 5, 5,5)));
    	statusMainPanel.add(statusPanel, BorderLayout.CENTER);    	
    	
    	controlsPanel = new JPanel(new BorderLayout(5,5));
    	controlsPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Controls"),BorderFactory.createEmptyBorder(0, 5, 5,5)));    	
    	this.generateDotPlotButton = new JButton("Generate Dot Plot");	
		this.generateDotPlotButton.addActionListener(this);
		this.zoomOutButton = new JButton("Zoom Out");
		this.zoomOutButton.addActionListener(this);
		this.zoomOutButton.setEnabled(false);
		this.estimateDButton = new JButton("Estimate D");
		this.estimateDButton.addActionListener(this);
		this.estimateDButton.setEnabled(true);
		this.printScreenButton = new JButton("Save Graph");
		this.printScreenButton.addActionListener(this);		
		this.printScreenButton.setEnabled(false);
		this.estimateSimilarityLimitButton = new JButton("Est Similarity Limit");
		this.estimateSimilarityLimitButton.addActionListener(this);
		this.estimateSimilarityLimitButton.setEnabled(false);
		this.panButton = new JButton("Pan Graph");
		this.panButton.setEnabled(false);
		this.panButton.addActionListener(this);
		this.loadDataButton = new JButton("Load Data");
		this.loadDataButton.addActionListener(this);
		this.saveDataButton = new JButton("Save Data");
		this.saveDataButton.setEnabled(false);
		this.saveDataButton.addActionListener(this);
		
		JPanel controlsPanelNorth = new JPanel(new GridLayout(2,1));
		JLabel kMismatchLabel = new JLabel("K-Mismatch: ");
		JPanel controlsPanelNorth1 = new JPanel();
		controlsPanelNorth1.add(kMismatchLabel);
		controlsPanelNorth1.add(this.kmismatchSlider);
		this.kmismatchSlider.addMouseListener(this);
		
		JLabel intensityLabel = new JLabel("     Intensity: ");
		this.intensitySlider = new JSlider(0,0);
		this.intensitySlider.setEnabled(false);
		this.intensitySlider.setMajorTickSpacing(1);					
		this.intensitySlider.setSnapToTicks(true);		
		this.intensitySlider.addMouseListener(this);
		JPanel controlsPanelNorth2 = new JPanel();
		controlsPanelNorth2.add(intensityLabel);
		controlsPanelNorth2.add(this.intensitySlider);
		
		controlsPanelNorth.add(controlsPanelNorth1);
		controlsPanelNorth.add(controlsPanelNorth2);
		
		
		JPanel controlsPanelCenter = new JPanel(new GridLayout(2,2,5,5));		
		controlsPanelCenter.add(this.panButton);
		controlsPanelCenter.add(this.printScreenButton);		
		//controlsPanelCenter.add(this.loadDataButton);
		//controlsPanelCenter.add(this.saveDataButton);
		controlsPanelCenter.add(this.zoomOutButton);
		controlsPanelCenter.add(this.estimateDButton);
		JPanel controlsPanelSouth = new JPanel(new GridLayout(1,1));
		controlsPanelSouth.add(this.generateDotPlotButton);
		controlsPanel.add(controlsPanelNorth,BorderLayout.NORTH);
    	controlsPanel.add(controlsPanelCenter,BorderLayout.CENTER);
    	controlsPanel.add(controlsPanelSouth,BorderLayout.SOUTH);
		
    	JPanel controlsPanelMain = new JPanel(new BorderLayout());
    	controlsPanelMain.add(controlsPanel, BorderLayout.CENTER);
    	
		JPanel westPanelCenter = new JPanel(new BorderLayout());
    	westPanelCenter.add(settingsPanel,BorderLayout.NORTH);
    	westPanelCenter.add(statusMainPanel,BorderLayout.SOUTH);
    	westPanelCenter.add(controlsPanelMain,BorderLayout.CENTER);
		
    	JPanel westPanel = new JPanel(new BorderLayout());
    	westPanel.add(sequencesPanel, BorderLayout.NORTH);
    	westPanel.add(westPanelCenter,BorderLayout.CENTER);     	    
    	
		setLayout(new BorderLayout());
		this.add(westPanel, BorderLayout.WEST);
		this.add(dotPlotGraphScrollPane, BorderLayout.CENTER);	
		
		initToolTip();
		this.addMouseMotionListener(this);
		this.addComponentListener(this);
	}		
	
	private void initToolTip() {
        label = new JLabel(" ");
        label.setOpaque(true);
        label.setBackground(UIManager.getColor("ToolTip.background"));
        toolTip = new JWindow(new Frame());
        toolTip.getContentPane().add(label);
        
        label2 = new JLabel(" ");
        label2.setOpaque(true);
        label2.setBackground(UIManager.getColor("ToolTip.background"));
        toolTip2 = new JWindow(new Frame());
        toolTip2.getContentPane().add(label2);
    }

	private boolean checkInputForEstimateD(){
		if(this.matrixTextField.getText().isEmpty() || this.inputFile1TextField.getText().isEmpty() || 
				this.inputFile2TextField.getText().isEmpty() ||	this.matrixTextField.getText().equalsIgnoreCase("Click Here!") || 
				this.inputFile1TextField.getText().equalsIgnoreCase("Click Here!") || 
				this.inputFile2TextField.getText().equalsIgnoreCase("Click Here!")){
			JOptionPane.showMessageDialog(null,"Please set all input files properly!","Error",JOptionPane.ERROR_MESSAGE);
			return false;
		}
		try{			
			int windowSizeInteger = Integer.parseInt(this.windowSizeTextField.getText());
			int qInteger = Integer.parseInt(this.qTextField.getText());			
			if(windowSizeInteger < 1){
				JOptionPane.showMessageDialog(null,"Please ensure Window Size > 0!","Error",JOptionPane.ERROR_MESSAGE);
				return false;
			}			
			if(qInteger <= 0){
				JOptionPane.showMessageDialog(null,"Please ensure q > 0!","Error",JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}catch(Exception e){			
			JOptionPane.showMessageDialog(null,"Please ensure Window Size and q are Integer!",
					"Error",JOptionPane.ERROR_MESSAGE);
			return false;
		}					
		return true;
	}
	
	private boolean checkInputs(){		
		if(this.matrixTextField.getText().isEmpty() || this.inputFile1TextField.getText().isEmpty() || 
				this.inputFile2TextField.getText().isEmpty() ||	this.matrixTextField.getText().equalsIgnoreCase("Click Here!") || 
				this.inputFile1TextField.getText().equalsIgnoreCase("Click Here!") || 
				this.inputFile2TextField.getText().equalsIgnoreCase("Click Here!")){
			JOptionPane.showMessageDialog(null,"Please set all input files properly!","Error",JOptionPane.ERROR_MESSAGE);
			return false;
		}
		try{
			int similarityLimitInteger = Integer.parseInt(this.similarityLimitTextField.getText());
			int windowSizeInteger = Integer.parseInt(this.windowSizeTextField.getText());
			int stepUpInteger = Integer.parseInt(this.stepUpTextField.getText());
			if(similarityLimitInteger < 0){
				JOptionPane.showMessageDialog(null,"Please ensure Similarity Limit >= 0!","Error",JOptionPane.ERROR_MESSAGE);
				return false;
			}
			if(windowSizeInteger < 1){
				JOptionPane.showMessageDialog(null,"Please ensure Window Size > 0!","Error",JOptionPane.ERROR_MESSAGE);
				return false;
			}
			if(windowSizeInteger - similarityLimitInteger > 3){			
				JOptionPane.showMessageDialog(null,"The program currently could only support K-mismatch up to 3","Error",JOptionPane.ERROR_MESSAGE);
				return false;
			}
			if(stepUpInteger <= 0){
				JOptionPane.showMessageDialog(null,"Please ensure Step Size > 0!","Error",JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}catch(Exception e){			
			JOptionPane.showMessageDialog(null,"Please ensure Window Size, Similarity Limit and Step Size are Integer!",
					"Error",JOptionPane.ERROR_MESSAGE);
			return false;
		}					
		return true;
	}
	
	private void printScreen(String outputFileName){
		try{
		// determine current screen size		
		//Dimension screenSize = 		
		Rectangle screenRect = new Rectangle(dotPlotGraphScrollPane.getLocationOnScreen(),dotPlotGraphScrollPane.getSize());
		// create screen shot
		Robot robot = new Robot();
		BufferedImage image = robot.createScreenCapture(screenRect);
		// save captured image to PNG file
		ImageIO.write(image, "png", new File(outputFileName));
		// give feedback
		JOptionPane.showMessageDialog(null,"Saved Dot Plot (" + image.getWidth() +" x " + image.getHeight() + " pixels) to file \"" +
			outputFileName + "\".", "Dot Plot Successfully Saved", JOptionPane.INFORMATION_MESSAGE);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void showTimeInTextField(JTextField textField, int trainTimeInSec){		
		int trainTimeInMins;
		int trainTimeInHours;
		if(trainTimeInSec <= 59)
			textField.setText("" + trainTimeInSec + " s");
		else{
			trainTimeInMins = trainTimeInSec / 60;
			trainTimeInSec = trainTimeInSec % 60;
			if(trainTimeInMins < 60)
				textField.setText("" + trainTimeInMins + " m " + trainTimeInSec + " s");
			else{
				trainTimeInHours = trainTimeInMins / 60;
				trainTimeInMins = trainTimeInMins % 60;				
				textField.setText("" + trainTimeInHours + " h " +  trainTimeInMins + " m " + trainTimeInSec + " s");
			}
		}
	}
	
	private void generateDotPlot(){
		dotPlotThread = (new Thread(){      		
			public void run(){
				generateDotPlotButton.setText("Terminate");
				windowSizeTextField.setEnabled(false);
				similarityLimitTextField.setEnabled(false);
				panButton.setEnabled(false);
				loadDataButton.setEnabled(false);
				saveDataButton.setEnabled(false);
				printScreenButton.setEnabled(false);
				percentageDoneProgressBar.setValue(0);
				percentageDoneProgressBar.setString("0%");
				kmismatchSlider.setEnabled(false);				
				intensitySlider.setEnabled(false);			
				stepUpTextField.setEnabled(false);
				qTextField.setEnabled(false);
				estimateDButton.setEnabled(false);
				zoomOutButton.setEnabled(false);
				boolean done = false;
				try{
					String command = "./dotplot " + inputFile1TextField.getText() + " " + inputFile2TextField.getText() + 
					" " + matrixTextField.getText() + " " + Integer.parseInt(windowSizeTextField.getText()) + " " + 
					Integer.parseInt(similarityLimitTextField.getText()) + " " + Integer.parseInt(stepUpTextField.getText());					
					dotPlotProcess = Runtime.getRuntime().exec(command);					
				 	BufferedReader input = new BufferedReader(new InputStreamReader(dotPlotProcess.getInputStream()));
				 	String line;
				 	while((line = input.readLine()) != null){
				 		if(line.indexOf("percentage:") != -1){
				 		Float percentageDone = Float.parseFloat(line.substring("percentage:".length()));					 		
				 			percentageDoneProgressBar.setValue((int)Float.parseFloat(line.substring("percentage:".length())));
				 			percentageDoneProgressBar.setString("" + df.format(Float.parseFloat(line.substring("percentage:".length()))) + "%");
					 		if(percentageDone != 0)
					 			showTimeInTextField(estTimeToCompleteTextField,
					 				(int)((((trainTimeElasped + 3000)/ percentageDone) * (100 - percentageDone)) / 1000));
					 		if(percentageDone == 100)
					 			done = true;
				 		}
				 	}							 	
				 	input.close();
				 	MessageDialog2 dialog = null;										    		
					dialog = new MessageDialog2(parent,"Rendering", "Rendering.. Please wait..");
					dialog.setLocationRelativeTo(parent);
				    dialog.setVisible(true);
				 	command = "./dotplotquery matches.txt " + dotPlotGraphPane.getSequence1Start() + " " + 
				 		dotPlotGraphPane.getSequence1End() + " " + dotPlotGraphPane.getSequence2Start() + " " + 
				 		dotPlotGraphPane.getSequence2End() + " " + dotPlotGraphPane.getXAxisLength() + " " 
				 		+ dotPlotGraphPane.getYAxisLength() + " " + Integer.parseInt(similarityLimitTextField.getText());			
					Process p = Runtime.getRuntime().exec(command);					
					input = new BufferedReader(new InputStreamReader(p.getInputStream()));
				 	BufferedWriter output = new BufferedWriter(new FileWriter("dots.txt"));				 	
				 	while((line = input.readLine()) != null){		 		
				 		output.write(line);
				 		output.newLine();
				 	}
				 	output.close();
				 	dotPlotGraphPane.updateDots();
				 	if(dialog != null)
						dialog.dispose();
				}catch(Exception e){
					e.printStackTrace();
				}				
				int kmismatch = Integer.parseInt(windowSizeTextField.getText()) - Integer.parseInt(similarityLimitTextField.getText());							
				if(done && kmismatch > 0){
					kmismatchOldValue = kmismatch;
					kmismatchSlider.setMaximum(kmismatch);
					kmismatchSlider.setValue(kmismatch);	
					kmismatchSlider.setEnabled(true);	
				}
				intensitySlider.setEnabled(true);
				int max = dotPlotGraphPane.getForwardMax();
				if(max < dotPlotGraphPane.getReverseMax())
					max = dotPlotGraphPane.getReverseMax();				
				intensitySlider.setMaximum(max);				
				printScreenButton.setEnabled(true);
				panButton.setEnabled(true);
				loadDataButton.setEnabled(true);
				saveDataButton.setEnabled(true);
				generateDotPlotButton.setText("Generate Dot Plot");
				windowSizeTextField.setEnabled(true);
				similarityLimitTextField.setEnabled(true);
				stepUpTextField.setEnabled(true);
				qTextField.setEnabled(true);
				estimateDButton.setEnabled(true);
				zoomOutButton.setEnabled(true);
				dotPlotGraphPane.updateDots();				
			}
		});
		dotPlotThread.setPriority(Thread.MIN_PRIORITY);
		dotPlotThread.start();
	}
	
	
	public void actionPerformed(ActionEvent ae) {		
		if(ae.getSource().equals(this.estimateSimilarityLimitButton)){
			if(checkInputs()){
				
			}
		}else if(ae.getSource().equals(this.generateDotPlotButton)){			
			if(checkInputs()){	
				if(dotPlotThread != null && dotPlotThread.isAlive()){
					dotPlotProcess.destroy();
					this.dotPlotGraphPane.clearDots();
				}
				else{
					generateDotPlot();
					Thread thread = (new Thread(){      		
						public void run(){
							long trainTimeStart = System.currentTimeMillis();
							runTimeLabel.setText("Elasped Time: ");
							while(dotPlotThread.isAlive()){					
								trainTimeElasped = System.currentTimeMillis() - trainTimeStart;
								int trainTimeInSec = (int) trainTimeElasped / 1000;
								showTimeInTextField(runTimeTextField, trainTimeInSec);					
							}							
							runTimeLabel.setText("Time Used: ");
						}
					});
					thread.setPriority(Thread.MIN_PRIORITY);
					thread.start();
				}
			}
		}else if(ae.getSource().equals(this.printScreenButton)){
			JFileChooser fc;				    	
	    	fc = new JFileChooser();
	    	FileNameExtensionFilter filter = new FileNameExtensionFilter(
		            "png", "png");
		    fc.setFileFilter(filter);	
			int returnVal = fc.showOpenDialog(parent);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();	                        	
            	String savingFilename = file.getAbsolutePath();
				if(savingFilename.indexOf(".png") == -1)
					savingFilename += ".png";			
				printScreen(savingFilename);
			}
		}else if(ae.getSource().equals(this.zoomOutButton)){
			zoomOut();
		}else if(ae.getSource().equals(this.panButton)){
			pan();
		}else if(ae.getSource().equals(this.saveDataButton)){
			saveData();
		}else if(ae.getSource().equals(this.loadDataButton)){
			loadData();
		}else if(ae.getSource().equals(this.estimateDButton)){
			estimateD();
		}
	}
	
	private void estimateD(){
		
		Thread tempThread = (new Thread(){      	
			public void run(){	
				try{
					if(checkInputForEstimateD() == false)
						return;			
					String command = "./estimatedots " + inputFile1TextField.getText() + " " + inputFile2TextField.getText() + 
					" " + matrixTextField.getText() + " " + Integer.parseInt(windowSizeTextField.getText()) + " " + 
					Integer.parseInt(qTextField.getText());					
					dotPlotProcess = Runtime.getRuntime().exec(command);	
					MessageDialog2 dialog = null;										    		
					dialog = new MessageDialog2(parent,"Estimating D", "Estimating D.. Please wait..");
					dialog.setLocationRelativeTo(parent);
				    dialog.setVisible(true);
				 	BufferedReader input = new BufferedReader(new InputStreamReader(dotPlotProcess.getInputStream()));
				 	String line;
				 	while((line = input.readLine()) != null){
				 		if(line.indexOf("Estimated threshold: ") != -1){
				 			similarityLimitTextField.setText(line.substring("Estimated threshold: ".length()));
				 		}
				 	}							 
				 	if(dialog != null)
				 		dialog.dispose();
				 	JOptionPane.showMessageDialog(null,"To have less than " + qTextField.getText() + " points, set Similarity Limit to " + 
				 			similarityLimitTextField.getText(),"Estimate D",
				 			JOptionPane.INFORMATION_MESSAGE);
				 	input.close();
				}catch(Exception e){e.printStackTrace();}
			}
		});
		tempThread.setPriority(Thread.MIN_PRIORITY);
		tempThread.start();	
	}
	
	private void saveData(){
		
	}
	
	private void loadData(){
		
	}
	
	private void zoomOut(){
		this.dotPlotGraphPane.setSequenceStartEnd(this.zoomer.get(this.zoomer.size() -1), 
				Integer.parseInt(this.similarityLimitTextField.getText()),true, null);
		this.zoomer.remove(this.zoomer.size() - 1);
		if(this.zoomer.size() == 0)
			this.zoomOutButton.setEnabled(false);
	}
	
	private void pan(){
		PanDialog dialog = new PanDialog(parent, this.dotPlotGraphPane.getSequence1Start(), this.dotPlotGraphPane.getSequence1End(),
				this.dotPlotGraphPane.getSequence2Start(), this.dotPlotGraphPane.getSequence2End(), this.sequence1Length, 
				this.sequence2Length, this.dotPlotGraphPane, this.similarityLimitTextField, this.zoomer);
		dialog.setLocationRelativeTo(this.controlsPanel);
	    dialog.setVisible(true);		
	}

	
	public void mouseClicked(MouseEvent me) {		
		if(me.getSource().equals(this.inputFile1TextField)){
			setInputFile1();
		}else if(me.getSource().equals(this.inputFile2TextField)){
			setInputFile2();
		}else if(me.getSource().equals(this.matrixTextField)){
			setSimilarityMatrixFile();
		}else if(me.getSource().equals(this.dotPlotGraphScrollPane) && me.getButton() == MouseEvent.BUTTON3){
			zoomOut();
		}
	}

	
	public void mouseEntered(MouseEvent arg0) {				
	}

	
	public void mouseExited(MouseEvent arg0) {			
	}

	
	public void mousePressed(MouseEvent me) {		
		if(me.getSource().equals(this.dotPlotGraphScrollPane) && me.getButton() == MouseEvent.BUTTON1)
			this.dotPlotGraphPane.setStartPoint(me.getPoint());		
	}

	
	public void mouseReleased(MouseEvent me) {		
		if(me.getSource().equals(this.dotPlotGraphScrollPane) && me.getButton() == MouseEvent.BUTTON1){
			//zoom in
			if(this.inputFile1TextField.getText().equalsIgnoreCase("Click Here!") || 
					this.inputFile2TextField.getText().equalsIgnoreCase("Click Here!")){
				this.dotPlotGraphPane.setStartAndCurrentPointNull();
				return;
			}
			this.dotPlotGraphPane.setEndPoint(me.getPoint(), this.zoomer, this.labelSequence1Start, this.labelSequence2Start,
					this.labelSequence1End,this.labelSequence2End, Integer.parseInt(this.similarityLimitTextField.getText()));
			if(this.zoomer.size() > 0)
				this.zoomOutButton.setEnabled(true);
			toolTip.setVisible(false);
			toolTip2.setVisible(false);
		}else if(me.getSource().equals(this.kmismatchSlider)){
			if(this.kmismatchSlider.getValue() == this.kmismatchOldValue)
				return;//do nothing		
			this.kmismatchOldValue = this.kmismatchSlider.getValue();			
			Thread tempThread = (new Thread(){      	
				public void run(){			
				MessageDialog2 dialog = null;										    		
				dialog = new MessageDialog2(parent,"Rendering", "Rendering.. Please wait..");
				dialog.setLocationRelativeTo(parent);
			    dialog.setVisible(true);
			 	String command = "./dotplotquery matches.txt " + dotPlotGraphPane.getSequence1Start() + " " + 
			 		dotPlotGraphPane.getSequence1End() + " " + dotPlotGraphPane.getSequence2Start() + " " + 
			 		dotPlotGraphPane.getSequence2End() + " " + dotPlotGraphPane.getXAxisLength() + " " 
			 		+ dotPlotGraphPane.getYAxisLength() + " " + (Integer.parseInt(windowSizeTextField.getText()) - kmismatchSlider.getValue());
			 	try{
					Process p = Runtime.getRuntime().exec(command);					
					BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
				 	BufferedWriter output = new BufferedWriter(new FileWriter("dots.txt"));	
				 	String line;
				 	while((line = input.readLine()) != null){		 		
				 		output.write(line);
				 		output.newLine();
				 	}
				 	output.close();
				 	dotPlotGraphPane.updateDots();
			 	}catch(Exception e){e.printStackTrace();}
			 	if(dialog != null)
					dialog.dispose();
				}	
			});
			tempThread.setPriority(Thread.MIN_PRIORITY);
			tempThread.start();
		}else if(me.getSource().equals(this.intensitySlider)){
			this.dotPlotGraphPane.updateIntensity(this.intensitySlider.getValue());
		}
	}
	
	private void swap(){
		if(dotPlotGraphPane.getSequence2Length() > dotPlotGraphPane.getSequence1Length()){
			//must swap them
			dotPlotGraphPane.swap();
			String inputFile1 = this.inputFile1TextField.getText();			
			this.inputFile1TextField.setText(this.inputFile2TextField.getText());
			this.inputFile2TextField.setText(inputFile1);
		}
	}
	
	private int readFastaFile(String filename, int sequenceNum){
		try{
			MessageDialog2 dialog = null;										    		
			dialog = new MessageDialog2(parent,"Reading File", "Reading File.. Please wait..");
			dialog.setLocationRelativeTo(parent);
		    dialog.setVisible(true);
		    
			this.zoomer.clear();
			this.zoomOutButton.setEnabled(false);
			BufferedReader input = new BufferedReader(new FileReader(filename));
			//BufferedWriter output = new BufferedWriter(new FileWriter(filename + "new"));
			String line;
			int count = 0;
			boolean first = true;
			while((line = input.readLine()) != null){
				if(line.length() == 0){					
					continue;
				}
				if(line.charAt(0) == '>' && first){					
					first = false;
					String name;
					if(line.length() > 1){
						name = line.substring(1);
						if(name.length() > 50){
							name = name.substring(0,50);							
						}
					}else						
						name = "Sequence " + sequenceNum;										
					if(sequenceNum == 1)
						this.dotPlotGraphPane.setSequence1Name(name);
					else
						this.dotPlotGraphPane.setSequence2Name(name);
				}else if(first){					
					first = false;
					if(sequenceNum == 1)
						this.dotPlotGraphPane.setSequence1Name("Sequence 1");
					else
						this.dotPlotGraphPane.setSequence2Name("Sequence 2");
					//output.write(">Human Chromosome 11");
					//output.newLine();
					count += line.length();
					//output.write(line);
					//output.newLine();
				}
				else{
					count += line.length();
					//output.write(line);
					//output.newLine();
				}
			}			
			if(dialog != null)
				dialog.dispose();	
			input.close();
			return count;
		}catch(Exception e){
			e.printStackTrace();
			return -1;
		}
	}
	
	private void setInputFile1(){
		JFileChooser fc;				
		if(this.inputFile1TextField.getText().isEmpty() == false)
			fc = new JFileChooser(this.inputFile1TextField.getText());
		else if(lastDirectoryOpened!=null)
			fc = new JFileChooser(lastDirectoryOpened);
		else
			fc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
	            "Fasta Files", "fasta","fa","fas");
	    fc.setFileFilter(filter);	
		int returnVal = fc.showOpenDialog(parent);		
		if (returnVal == JFileChooser.APPROVE_OPTION) {	        
			final File file = fc.getSelectedFile();
			Thread zoomThread = (new Thread(){
				public void run(){
			            try{			            		            
			            	lastDirectoryOpened = file.getPath();
			            	inputFile1TextField.setText(file.getAbsolutePath());
			            	
			            	int length = readFastaFile(file.getAbsolutePath(),1);
			            	sequence1Length = length;			            	
			            	dotPlotGraphPane.setSequence1Length(length);
			            	swap();
			            	dotPlotGraphPane.repaint();
			            }catch(Exception e){
			            	JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
			            	e.printStackTrace();}            	        	
					}
				});
				zoomThread.setPriority(Thread.MIN_PRIORITY);
				zoomThread.start();	
	    }
	}
	
	private void setInputFile2(){
		JFileChooser fc;				
		if(this.inputFile2TextField.getText().isEmpty() == false)
			fc = new JFileChooser(this.inputFile2TextField.getText());
		else if(lastDirectoryOpened!=null)
			fc = new JFileChooser(lastDirectoryOpened);
		else
			fc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
	            "Fasta Files", "fasta","fa", "fas");
	    fc.setFileFilter(filter);	
		int returnVal = fc.showOpenDialog(parent);		
		if (returnVal == JFileChooser.APPROVE_OPTION) {	        
			final File file = fc.getSelectedFile();
			Thread zoomThread = (new Thread(){
				public void run(){
		            try{			            		            
		            	lastDirectoryOpened = file.getPath();
		            	inputFile2TextField.setText(file.getAbsolutePath());
		            	int length = readFastaFile(file.getAbsolutePath(),2);
		            	sequence2Length = length;
		            	dotPlotGraphPane.setSequence2Length(length); 
		            	swap();
		            	dotPlotGraphPane.repaint();
		            }catch(Exception e){
		            	JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
		            	e.printStackTrace();} 
				}
			});
			zoomThread.setPriority(Thread.MIN_PRIORITY);
			zoomThread.start();	
	    }
	}
	
	private void setSimilarityMatrixFile(){
		JFileChooser fc;				
		if(this.matrixTextField.getText().isEmpty() == false)
			fc = new JFileChooser(this.matrixTextField.getText());
		else if(lastDirectoryOpened!=null)
			fc = new JFileChooser(lastDirectoryOpened);
		else
			fc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
	            "Matrix Files", "matrix");
	    fc.setFileFilter(filter);	
		int returnVal = fc.showOpenDialog(parent);		
		if (returnVal == JFileChooser.APPROVE_OPTION) {	        
			File file = fc.getSelectedFile();
            try{			            		            
            	lastDirectoryOpened = file.getPath();
            	this.matrixTextField.setText(file.getAbsolutePath());
            }catch(Exception e){
            	JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
            	e.printStackTrace();}            	        		
	    }
	}

	
	public void componentHidden(ComponentEvent arg0) {		
		toolTip.setVisible(false);
		toolTip2.setVisible(false);
	}

	
	public void componentMoved(ComponentEvent arg0) {		
		toolTip.setVisible(false);
		toolTip2.setVisible(false);
		
	}

	
	public void componentResized(ComponentEvent arg0) {		
		dotPlotGraphPane.setParentDimension(dotPlotGraphScrollPane.getSize());
		toolTip.setVisible(false);
		toolTip2.setVisible(false);
	}

	
	public void componentShown(ComponentEvent arg0) {		
		toolTip.setVisible(false);
		toolTip2.setVisible(false);		
	}

	
	public void mouseDragged(MouseEvent me) {		
		if(me.getSource().equals(this.dotPlotGraphScrollPane)){
			this.dotPlotGraphPane.setCurrentPoint(me.getPoint());
			Point p = me.getPoint();	 		
	 		Point convertedPoint = SwingUtilities.convertPoint(this.dotPlotGraphScrollPane, p, this.dotPlotGraphPane);
	 		Point sequenceIndexPoint = this.dotPlotGraphPane.convertPointToSequenceIndex(convertedPoint);	 		
	 		if(sequenceIndexPoint.getX() >= this.dotPlotGraphPane.getSequence1Start() && 
	 				sequenceIndexPoint.getY() >= this.dotPlotGraphPane.getSequence2Start() &&
	 				sequenceIndexPoint.getX() <= this.dotPlotGraphPane.getSequence1End() &&
	 				sequenceIndexPoint.getY() <= this.dotPlotGraphPane.getSequence2End() &&
	 				sequenceIndexPoint.getX() > 0 && sequenceIndexPoint.getY() > 0){
	 			this.labelSequence1End = (int)sequenceIndexPoint.getX();
		 		this.labelSequence2End = (int)sequenceIndexPoint.getY();
		 		label2.setText("" + sequenceIndexPoint.getX() + ", " + sequenceIndexPoint.getY());
		 		int labelWidth = (int)label2.getSize().getWidth();
		        toolTip2.pack();
		        toolTip2.setVisible(true);
		        SwingUtilities.convertPointToScreen(p, this.dotPlotGraphScrollPane);
		        if(labelWidth + p.x + 20 < this.dotPlotGraphScrollPane.getSize().getWidth())
		        	toolTip2.setLocation(p.x + 10, p.y - 20);
		        else
		        	toolTip2.setLocation(p.x - 10 - labelWidth, p.y - 20);
	 		}else
	 			toolTip2.setVisible(false);
		}
	}

	
	public void mouseMoved(MouseEvent me) {
		//this is used to show information about a dot on the graph
		if(me.getSource().equals(this.dotPlotGraphScrollPane)){			
	 		Point p = me.getPoint();	 		
	 		Point convertedPoint = SwingUtilities.convertPoint(this.dotPlotGraphScrollPane, p, this.dotPlotGraphPane);
	 		Point sequenceIndexPoint = this.dotPlotGraphPane.convertPointToSequenceIndex(convertedPoint);
	 		this.labelSequence1Start = (int)sequenceIndexPoint.getX();
	 		this.labelSequence2Start = (int)sequenceIndexPoint.getY();
	 		if(sequenceIndexPoint.getX() >= this.dotPlotGraphPane.getSequence1Start() && 
	 				sequenceIndexPoint.getY() >= this.dotPlotGraphPane.getSequence2Start() &&
	 				sequenceIndexPoint.getX() <= this.dotPlotGraphPane.getSequence1End() &&
	 				sequenceIndexPoint.getY() <= this.dotPlotGraphPane.getSequence2End() &&
	 				sequenceIndexPoint.getX() > 0 && sequenceIndexPoint.getY() > 0){
		 		label.setText("" + sequenceIndexPoint.getX() + ", " + sequenceIndexPoint.getY());
		 		int labelWidth = (int)label.getSize().getWidth();
		        toolTip.pack();
		        toolTip.setVisible(true);
		        SwingUtilities.convertPointToScreen(p, this.dotPlotGraphScrollPane);
		        if(labelWidth + p.x + 20 < this.dotPlotGraphScrollPane.getSize().getWidth())
		        	toolTip.setLocation(p.x + 10, p.y - 20);
		        else
		        	toolTip.setLocation(p.x - 10 - labelWidth, p.y - 20);
	 		}else{
				toolTip.setVisible(false);
			}
			
		}else{
			toolTip.setVisible(false);
			toolTip2.setVisible(false);
		}
	}	
}

class TwoSequencePoint {
	int sequence1Start;
	int sequence1End;
	int sequence2Start;
	int sequence2End;
	
	public TwoSequencePoint(int start1,int end1, int start2, int end2){
		this.sequence1Start = start1;
		this.sequence1End = end1;
		this.sequence2Start = start2;
		this.sequence2End = end2;		
	}
	
	public int getSequence1Start(){
		return this.sequence1Start;
	}
	
	public int getSequence1End(){
		return this.sequence1End;
	}
	
	public int getSequence2Start(){
		return this.sequence2Start;
	}
	
	public int getSequence2End(){
		return this.sequence2End;
	}
}

class PanDialog extends JDialog implements ActionListener{
	static final long serialVersionUID = 23122007;
	JTextField sequence1FromTextField;
	JTextField sequence1ToTextField;
	JTextField sequence2FromTextField;
	JTextField sequence2ToTextField;	
	JButton okButton;
	JButton cancelButton;
	int sequence1Length;
	int sequence2Length;
	DotPlotGraphPane dotPlotGraphPane;
	JTextField similarityLimitTextField;
	ArrayList<TwoSequencePoint> zoomer;
	public PanDialog(Frame parent, int sequence1From, int sequence1To, int sequence2From, int sequence2To,
			int sequence1Length, int sequence2Length, DotPlotGraphPane dotPlotGraphPane,
			JTextField similarityLimitTextField, ArrayList<TwoSequencePoint> zoomer){
		super(parent,"Pan Graph");
		
		this.sequence1Length = sequence1Length;
		this.sequence2Length = sequence2Length;
		this.dotPlotGraphPane = dotPlotGraphPane;
		this.similarityLimitTextField = similarityLimitTextField;
		this.zoomer = zoomer;
		
		setLayout(new GridLayout(3,1));
		
		JPanel sequence1Panel = new JPanel(new GridLayout(1,4));
		sequence1Panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Sequence 1 (Length: " + sequence1Length + ")"),BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		JLabel sequence1FromLabel = new JLabel("From: ", SwingConstants.RIGHT);
		this.sequence1FromTextField = new JTextField("" + sequence1From, 5);
		JLabel sequence1ToLabel = new JLabel("To: ", SwingConstants.RIGHT);
		this.sequence1ToTextField = new JTextField("" + sequence1To, 5);
		sequence1Panel.add(sequence1FromLabel);
		sequence1Panel.add(sequence1FromTextField);
		sequence1Panel.add(sequence1ToLabel);
		sequence1Panel.add(sequence1ToTextField);
		
		JPanel sequence2Panel = new JPanel(new GridLayout(1,4));
		sequence2Panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Sequence 2 (Length: " + sequence2Length + ")"),BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		JLabel sequence2FromLabel = new JLabel("From: ", SwingConstants.RIGHT);
		this.sequence2FromTextField = new JTextField("" + sequence2From, 5);
		JLabel sequence2ToLabel = new JLabel("To: ", SwingConstants.RIGHT);		
		this.sequence2ToTextField = new JTextField("" + sequence2To, 5);
		sequence2Panel.add(sequence2FromLabel);
		sequence2Panel.add(sequence2FromTextField);
		sequence2Panel.add(sequence2ToLabel);
		sequence2Panel.add(sequence2ToTextField);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		this.okButton = new JButton("  OK  ");
		this.okButton.addActionListener(this);
		this.cancelButton = new JButton("Cancel");
		this.cancelButton.addActionListener(this);
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		
		add(sequence1Panel);
		add(sequence2Panel);
		add(buttonPanel);
		
		//setSize(300,200);
		this.pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {		
		if(e.getSource().equals(this.okButton)){
			this.dispose();
			try{
				int sequence1Start = Integer.parseInt(this.sequence1FromTextField.getText());
				int sequence1End = Integer.parseInt(this.sequence1ToTextField.getText());
				int sequence2Start = Integer.parseInt(this.sequence2FromTextField.getText());
				int sequence2End = Integer.parseInt(this.sequence2ToTextField.getText());							
				if(sequence1End < sequence1Start){
					JOptionPane.showMessageDialog(null,"Please ensure that 'Sequence 1 To' value > 'Sequence 1 From'!","Error",JOptionPane.ERROR_MESSAGE);
					this.sequence1ToTextField.requestFocusInWindow();
					return;
				}
				if(sequence2End < sequence2Start){
					JOptionPane.showMessageDialog(null,"Please ensure that 'Sequence 2 To' value > 'Sequence 2 From'!","Error",JOptionPane.ERROR_MESSAGE);
					this.sequence2ToTextField.requestFocusInWindow();
					return;
				}
				if(sequence1End > sequence1Length){
					JOptionPane.showMessageDialog(null,"Please ensure that 'Sequence 1 To' value <= 'Sequence 1 Length'!","Error",JOptionPane.ERROR_MESSAGE);
					this.sequence1ToTextField.requestFocusInWindow();
					return;
				}
				if(sequence2End > sequence2Length){
					JOptionPane.showMessageDialog(null,"Please ensure that 'Sequence 2 To' value <= 'Sequence 2 Length'!","Error",JOptionPane.ERROR_MESSAGE);
					this.sequence2ToTextField.requestFocusInWindow();
					return;
				}
				this.dotPlotGraphPane.setSequenceStartEnd(new TwoSequencePoint(sequence1Start,sequence1End,sequence2Start,sequence2End), 
						Integer.parseInt(this.similarityLimitTextField.getText()),false, this.zoomer);
				}catch(NumberFormatException ex){
					JOptionPane.showMessageDialog(null,"Please ensure that values are entered correctly!","Error",JOptionPane.ERROR_MESSAGE);
				}
		}else if(e.getSource().equals(this.cancelButton)){
			this.dispose();
		}
	}
}

class MessageDialog2 extends JDialog{
	static final long serialVersionUID = 23122007;
	public MessageDialog2(Frame parent,String title, String message){
		super(parent,title);
		setLayout(new GridLayout(1,1));
		
		JLabel messageLabel = new JLabel(message,SwingConstants.CENTER);
		add(messageLabel);
		//setSize(200,90);
		this.pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}
}