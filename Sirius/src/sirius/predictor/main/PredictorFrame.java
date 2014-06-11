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
package sirius.predictor.main;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import sirius.blast.QBlast;
import sirius.main.ApplicationData;
import sirius.misc.clipboard.MyClipboard;
import sirius.nnsearcher.main.FilterPanel;
import sirius.trainer.features.Feature;
import sirius.trainer.features.GenerateArff;
import sirius.trainer.main.SiriusSettings;
import sirius.trainer.main.StatusPane;
import sirius.utils.Utils;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;



public class PredictorFrame extends JInternalFrame implements ActionListener,ListSelectionListener,	
	ComponentListener,MouseListener{
	static final long serialVersionUID = sirius.Sirius.version;
	private JMenu classifierMenu;
	private JMenuItem loadClassifierMenuItem;
	private JMenuItem deleteClassifierMenuItem;
	private JMenuItem runClassifierMenuItem;
	private JMenuItem stopClassifierMenuItem;
	private JMenuItem showClassifierDetailsMenuItem;
	
	private JMenu fileMenu;
	private JCheckBoxMenuItem loadFastaFileMenuItem;
	private JCheckBoxMenuItem loadScoreFileMenuItem;	
	
	private JMenu predictionMenu;
	private JCheckBoxMenuItem onAllPositionsMenuItem;
	private JCheckBoxMenuItem onMotifsOnlyMenuItem;	
	private JMenuItem showMotifListMenuItem;
	
	private JMenu outputMenu;
	private JMenuItem setDirectoryMenuItem;	
	
	private StatusPane statusPane;
	
	private JTable sequenceNameTable;
	private SequenceNameTableModel sequenceNameTableModel;
	private SequenceTableModel sequenceTableModel;
	
	private JTable classifierTable;
	private ClassifierTableModel classifierTableModel;
	private JFrame mainFrame;
	private Thread loaderThread;	
	private String outputDirectory;
	private String fastaFilename;
	private ScoreGraphPane scoreGraphPane;
	
	private JScrollPane scoreGraphScrollPane;
	
	private MyViewPort port;
			
	private boolean stopClassifier;	

	private MotifsTableModel motifListTableModel;
		
	private JPopupMenu classifierPopupMenu = new JPopupMenu("Title");
	private JMenuItem deleteClassifierPopupMenuItem = new JMenuItem("Delete");
	private JMenuItem runClassifierPopupMenuItem = new JMenuItem("Run");
	private JMenuItem stopClassifierPopupMenuItem = new JMenuItem("Stop");
	private JMenuItem showDetailsClassifierPopupMenuItem = new JMenuItem("Show Details");
	
	private JPopupMenu sequencePopupMenu = new JPopupMenu("Sequence");
	private JMenuItem copyHeaderMenuItem = new JMenuItem("Copy Header");
	private JMenuItem copySequenceMenuItem = new JMenuItem("Copy Sequence");
	private JMenuItem copyFastaMenuItem = new JMenuItem("Copy Fasta");
	
	private JButton filterButton = new JButton("Filter");
	private JButton checkAllButton = new JButton("Check All");
	private JButton uncheckAllButton = new JButton("Uncheck All");
	private JButton checkTopButton = new JButton("Check Top: ");
	private JTextField topCheckField = new JTextField(3);
	private JButton blastButton = new JButton("PSI-Blast Checked");
	private JCheckBox filterLowComplexityCheckedButton = new JCheckBox("Filter Low Complexity");
	private JRadioButton viewLocallyButton = new JRadioButton("View Locally");
	private JRadioButton viewInternetButton = new JRadioButton("View Internet");
	private JButton sortScoreButton;
	private JTextField sortScoreTextField;
	private JButton writeToFileButton;
	private FilterPanel filterPanel;
	private JDialog filterDialog;
	private ApplicationData applicationData = new ApplicationData((StatusPane)null);
	
    public PredictorFrame(JFrame mainFrame) {
    	super("Predictor",true,true,true,true);    	
    	
    	this.viewInternetButton.setSelected(true);
    	this.viewInternetButton.addActionListener(this);
    	this.viewLocallyButton.addActionListener(this);
    	
    	this.mainFrame = mainFrame;
    	this.loaderThread = null;    	
    	
    	this.checkAllButton.addActionListener(this);
    	this.uncheckAllButton.addActionListener(this);
    	this.checkTopButton.addActionListener(this);
    	
    	//Classifier PopupMenu
	    stopClassifierPopupMenuItem.setEnabled(false);
	    
	    deleteClassifierPopupMenuItem.addActionListener(this);
	    runClassifierPopupMenuItem.addActionListener(this);
	    stopClassifierPopupMenuItem.addActionListener(this);	    
	    showDetailsClassifierPopupMenuItem.addActionListener(this);
	    	    		    
		classifierPopupMenu.add(deleteClassifierPopupMenuItem);			    
		classifierPopupMenu.add(runClassifierPopupMenuItem);
		classifierPopupMenu.add(stopClassifierPopupMenuItem);			    
	    classifierPopupMenu.addSeparator();
	    classifierPopupMenu.add(showDetailsClassifierPopupMenuItem);		
	    
	    //Sequence PopupMenu
	    this.copyFastaMenuItem.addActionListener(this);
	    this.copyHeaderMenuItem.addActionListener(this);
	    this.copySequenceMenuItem.addActionListener(this);
	    
	    this.sequencePopupMenu.add(this.copyFastaMenuItem);
	    this.sequencePopupMenu.addSeparator();
	    this.sequencePopupMenu.add(this.copyHeaderMenuItem);
	    this.sequencePopupMenu.add(this.copySequenceMenuItem);
    	    	
    	//MenuBar
    	JMenuBar mb = new JMenuBar();       
    	    	
    	fileMenu = new JMenu("File");
    	fileMenu.setDisplayedMnemonicIndex(0);
    	fileMenu.setMnemonic(KeyEvent.VK_F);
   		loadFastaFileMenuItem = new JCheckBoxMenuItem("Load Fasta File",false);
   		loadFastaFileMenuItem.setDisplayedMnemonicIndex(5);
   		loadFastaFileMenuItem.setMnemonic(KeyEvent.VK_F);
   		loadScoreFileMenuItem = new JCheckBoxMenuItem("Load File(with Score)",false);
   		loadScoreFileMenuItem.setDisplayedMnemonicIndex(15);
   		loadScoreFileMenuItem.setMnemonic(KeyEvent.VK_S);   		  		   		
   		
   		loadFastaFileMenuItem.addActionListener(this);
   		loadScoreFileMenuItem.addActionListener(this);
   		
   		fileMenu.add(loadFastaFileMenuItem);
   		fileMenu.add(loadScoreFileMenuItem);
    	
    	classifierMenu = new JMenu("Classifier");
    	classifierMenu.setDisplayedMnemonicIndex(0);
    	classifierMenu.setMnemonic(KeyEvent.VK_C);    	
       	loadClassifierMenuItem = new JMenuItem("Load Classifier");
       	loadClassifierMenuItem.setDisplayedMnemonicIndex(0);
    	loadClassifierMenuItem.setMnemonic(KeyEvent.VK_L);
       	deleteClassifierMenuItem = new JMenuItem("Delete Classifier");
       	deleteClassifierMenuItem.setDisplayedMnemonicIndex(0);
    	deleteClassifierMenuItem.setMnemonic(KeyEvent.VK_D);
       	runClassifierMenuItem = new JMenuItem("Run Classifier");
       	runClassifierMenuItem.setDisplayedMnemonicIndex(0);
    	runClassifierMenuItem.setMnemonic(KeyEvent.VK_R);
       	stopClassifierMenuItem = new JMenuItem("Stop Classifier");
       	stopClassifierMenuItem.setDisplayedMnemonicIndex(0);
    	stopClassifierMenuItem.setMnemonic(KeyEvent.VK_S);
       	showClassifierDetailsMenuItem = new JMenuItem("Show Details");
       	showClassifierDetailsMenuItem.setDisplayedMnemonicIndex(1);
    	showClassifierDetailsMenuItem.setMnemonic(KeyEvent.VK_H);
       	
       	stopClassifierMenuItem.setEnabled(false);
       	stopClassifier = false;
       	
       	loadClassifierMenuItem.addActionListener(this);
       	deleteClassifierMenuItem.addActionListener(this);
       	runClassifierMenuItem.addActionListener(this);
       	stopClassifierMenuItem.addActionListener(this);
       	showClassifierDetailsMenuItem.addActionListener(this);
       
	    classifierMenu.add(loadClassifierMenuItem);
    	classifierMenu.add(deleteClassifierMenuItem);
    	classifierMenu.add(runClassifierMenuItem);
    	classifierMenu.add(stopClassifierMenuItem);
    	classifierMenu.add(showClassifierDetailsMenuItem);
    	
    	predictionMenu = new JMenu("Predictions");
    	predictionMenu.setDisplayedMnemonicIndex(0);
    	predictionMenu.setMnemonic(KeyEvent.VK_P);
		onAllPositionsMenuItem = new JCheckBoxMenuItem("On All Positions");
		onAllPositionsMenuItem.setDisplayedMnemonicIndex(3);
    	onAllPositionsMenuItem.setMnemonic(KeyEvent.VK_A);
		onMotifsOnlyMenuItem = new JCheckBoxMenuItem("On Motifs Only");		
		onMotifsOnlyMenuItem.setDisplayedMnemonicIndex(3);
    	onMotifsOnlyMenuItem.setMnemonic(KeyEvent.VK_M);
		showMotifListMenuItem = new JMenuItem("Show Motifs List");
		showMotifListMenuItem.setDisplayedMnemonicIndex(12);
    	showMotifListMenuItem.setMnemonic(KeyEvent.VK_L);
		
		onAllPositionsMenuItem.setState(true);		
		onMotifsOnlyMenuItem.setState(false);
		
		onAllPositionsMenuItem.addActionListener(this);
		onMotifsOnlyMenuItem.addActionListener(this);
		showMotifListMenuItem.addActionListener(this);
		
		predictionMenu.add(onAllPositionsMenuItem);
		predictionMenu.add(onMotifsOnlyMenuItem);
		predictionMenu.addSeparator();
		predictionMenu.add(showMotifListMenuItem);
    	
    	outputMenu = new JMenu("Output");    
    	outputMenu.setDisplayedMnemonicIndex(0);
    	outputMenu.setMnemonic(KeyEvent.VK_O);	
    	setDirectoryMenuItem = new JMenuItem("Set Directory");
    	setDirectoryMenuItem.setDisplayedMnemonicIndex(0);
    	setDirectoryMenuItem.setMnemonic(KeyEvent.VK_S);
    	setDirectoryMenuItem.addActionListener(this);
    	
    	outputMenu.add(setDirectoryMenuItem);    	    
        
        mb.add(fileMenu);               		       	
    	mb.add(classifierMenu);
    	mb.add(predictionMenu);
    	mb.add(outputMenu);
       	setJMenuBar(mb);  
       		
    	//GUI
    	JPanel main = new JPanel(new BorderLayout());
    	
    	JPanel main_west = new JPanel(new GridLayout(1,1));    	
    	JPanel main_north = new JPanel(new BorderLayout());
    	JPanel main_center = new JPanel(new GridLayout(1,1));
    	
    	main.add(main_center,BorderLayout.CENTER);
    	main.add(main_west,BorderLayout.WEST);
    	main.add(main_north,BorderLayout.NORTH);
    		
    	JPanel statusPanel = new JPanel(new GridLayout(1,1));
    	statusPane = new StatusPane("Ready");    	
    	statusPanel.add(statusPane);
    	main.add(statusPanel,BorderLayout.SOUTH);  	
    		
    	JPanel sequenceNamePanel = new JPanel(new BorderLayout());
    	sequenceNamePanel.setBorder(BorderFactory.createCompoundBorder(
    	BorderFactory.createTitledBorder("Sequences Name"),
    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));
    	sequenceNameTableModel = new SequenceNameTableModel();
    	sequenceNameTable = new JTable(sequenceNameTableModel);
    	sequenceNameTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
    	sequenceNameTable.getColumnModel().getColumn(0).setMinWidth(20);  
    	sequenceNameTable.getColumnModel().getColumn(0).setMaxWidth(20);
        sequenceNameTable.getColumnModel().getColumn(1).setMaxWidth(50);
        sequenceNameTable.getColumnModel().getColumn(2).setMinWidth(25);   
    	sequenceNameTable.getColumnModel().getColumn(1).setPreferredWidth(30);     
        sequenceNameTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        sequenceNameTable.getColumnModel().getColumn(3).setPreferredWidth(50);        
        sequenceNameTable.getSelectionModel().addListSelectionListener(this);
    	JScrollPane sequenceNameTableScrollPane = new JScrollPane(sequenceNameTable);
    	sequenceNameTableScrollPane.setPreferredSize(new Dimension(250,700));
    	sequenceNamePanel.add(sequenceNameTableScrollPane,BorderLayout.CENTER);
    	
    	JPanel sortButtonPanel = new JPanel();
    	sortScoreButton = new JButton("Sort By Score @: ");
    	sortScoreButton.setEnabled(false);
    	sortScoreButton.addActionListener(this);
    	sortScoreTextField = new JTextField(4);
    	sortButtonPanel.add(sortScoreButton);
    	sortButtonPanel.add(sortScoreTextField);   
    	
    	writeToFileButton = new JButton("Save");
    	writeToFileButton.addActionListener(this);
    	sortButtonPanel.add(writeToFileButton);
    	
    	JPanel selectionOptionPanel = new JPanel();
    	selectionOptionPanel.add(this.checkAllButton);
    	selectionOptionPanel.add(this.checkTopButton);
    	selectionOptionPanel.add(this.topCheckField);
    	
    	JPanel filteringPanel = new JPanel();
    	filteringPanel.add(this.uncheckAllButton);
    	filteringPanel.add(this.filterButton);
    	
    	JPanel blastButtonPanel = new JPanel();
    	blastButtonPanel.add(this.filterLowComplexityCheckedButton);
    	blastButtonPanel.add(this.blastButton);
    	
    	this.filterButton.addActionListener(this);
    	this.blastButton.addActionListener(this);
    	
    	
    	JPanel viewPanel = new JPanel();
    	viewPanel.add(this.viewLocallyButton);
    	viewPanel.add(this.viewInternetButton);
    	
    	JPanel southPanel = new JPanel(new GridLayout(5,1));
    	southPanel.add(sortButtonPanel);
    	southPanel.add(selectionOptionPanel);
    	southPanel.add(filteringPanel);
    	southPanel.add(blastButtonPanel);
    	southPanel.add(viewPanel);
    	
    	sequenceNamePanel.add(southPanel,BorderLayout.SOUTH);
    	main_west.add(sequenceNamePanel);
    	
    	JPanel classifierPanel = new JPanel(new BorderLayout());
    	classifierPanel.setBorder(BorderFactory.createCompoundBorder(
    	BorderFactory.createTitledBorder("Classifiers"),
    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));
    	classifierTableModel = new ClassifierTableModel();
    	classifierTable = new JTable(classifierTableModel);    	
    	classifierTable.addMouseListener(this);
    	classifierTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        classifierTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        classifierTable.getColumnModel().getColumn(1).setPreferredWidth(120);  
        classifierTable.getColumnModel().getColumn(2).setPreferredWidth(90);  
        sequenceNameTable.getColumnModel().getColumn(0).setMinWidth(20);       
        sequenceNameTable.getColumnModel().getColumn(1).setMinWidth(60);
        sequenceNameTable.getColumnModel().getColumn(2).setMinWidth(45);   
    	JScrollPane classifierTableScrollPane = new JScrollPane(classifierTable);
    	classifierTableScrollPane.setPreferredSize(new Dimension(250,50));
    	classifierPanel.add(classifierTableScrollPane,BorderLayout.CENTER);       	
    	main_north.add(classifierPanel,BorderLayout.WEST);
    		
    	JPanel sequencePanel = new JPanel(new BorderLayout());
    	sequencePanel.setBorder(BorderFactory.createCompoundBorder(
    	BorderFactory.createTitledBorder("Sequence"),
    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));
    	this.sequenceNameTable.addMouseListener(this);
    	sequenceTableModel = new SequenceTableModel();
    	JTable sequenceTable = new JTable(sequenceTableModel);
    	sequenceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sequenceTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        sequenceTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        sequenceTable.getColumnModel().getColumn(1).setPreferredWidth(500); 
        sequenceTable.getColumnModel().getColumn(0).setMinWidth(40);
        sequenceTable.getColumnModel().getColumn(0).setMaxWidth(80);
        sequenceTable.getColumnModel().getColumn(1).setMinWidth(250); 
    	JScrollPane sequenceTableScrollPane = new JScrollPane(sequenceTable);
    	sequenceTableScrollPane.setPreferredSize(new Dimension(250,100));
    	sequencePanel.add(sequenceTableScrollPane,BorderLayout.CENTER);
    	main_north.add(sequencePanel,BorderLayout.CENTER);
    	
    	JPanel motifPanel = new JPanel();
    	motifPanel.setBorder(BorderFactory.createCompoundBorder(
    	BorderFactory.createTitledBorder("Motif"),
    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));    		  
    	  
    	scoreGraphPane = new ScoreGraphPane();
    	port = new MyViewPort(scoreGraphPane);
    	JPanel scoreGraphPanel = new JPanel(new BorderLayout());
    	scoreGraphPanel.add(scoreGraphPane,BorderLayout.CENTER);
    	scoreGraphScrollPane = new JScrollPane();
    	scoreGraphScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    	scoreGraphScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
    	port.setView(scoreGraphPane);  
		scoreGraphScrollPane.setViewport(port);
    	scoreGraphScrollPane.addComponentListener(this);
    	scoreGraphScrollPane.setBorder(BorderFactory.createCompoundBorder(
    	BorderFactory.createTitledBorder("Score Graph"),
    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));    	
    	main_center.add(scoreGraphScrollPane); 
    		   	
    	scoreGraphPane.setParentDimension(scoreGraphScrollPane.getSize());
    	add(main);    	
    		
    	motifListTableModel = new MotifsTableModel(mainFrame);
    	/*
    	 */
    	this.applicationData.setSequenceType("PROTEIN");
    	this.applicationData.setScoringMatrixIndex(0);
		this.applicationData.setCountingStyleIndex(0);
    	this.filterPanel = new FilterPanel(mainFrame, this.sequenceNameTableModel, this.applicationData, this.statusPane);
    	this.filterDialog = new JDialog();
    	this.filterDialog.setLayout(new BorderLayout());
    	this.filterDialog.setSize(500, 500);
    	this.filterDialog.add(this.filterPanel);
    	this.sequenceNameTableModel.setConstraintsData(filterPanel.getMustHaveTableModel().getData());
    }     	         
	
	public void componentHidden(ComponentEvent e){
		//Invoked when the component has been made invisible. 
	}          
 	public void componentMoved(ComponentEvent e){
 		//Invoked when the component's position changes. 
 	}          
 	public void componentResized(ComponentEvent e){
 		//Invoked when the component's size changes.  		
 		scoreGraphPane.setParentDimension(scoreGraphScrollPane.getSize()); 		
 	}          
 	public void componentShown(ComponentEvent e){
 		//Invoked when the component has been made visible. 
 	}                           	    
    
    public void mouseClicked(MouseEvent e) {
    	//Invoked when the mouse button has been clicked (pressed and released) on a component. 
    	if(e.getSource().equals(this.sequenceNameTable) && e.getClickCount() == 2 && 
        		e.getButton() == MouseEvent.BUTTON1 && e.isControlDown() == false){
    		int selectedIndex = this.sequenceNameTable.getSelectedRow();
    		String RID = this.sequenceNameTableModel.getRID(selectedIndex);
    		String localID = this.sequenceNameTableModel.getLocalID(selectedIndex);
    		if(RID != null){
    			if(this.viewInternetButton.isSelected()){
    				QBlast.showBlastResultInBrowser(RID, null, false);
    			}else{
    				QBlast.showBlastResultInBrowser(localID, this.outputDirectory, true);
    			}
    		}else{
    			JOptionPane.showMessageDialog(null, "There is no Blast results associate with this sequence. Please Blast it first.");
    		}
    	}else if(e.getSource().equals(this.sequenceNameTable) && e.getButton() == MouseEvent.BUTTON1 &&
    			e.isControlDown()){
    		int selectedRow = this.sequenceNameTable.rowAtPoint(e.getPoint());    		
    		sequenceNameTable.setRowSelectionInterval(selectedRow,selectedRow);
    		this.sequencePopupMenu.show(sequenceNameTable,e.getX(),e.getY());
    	}else if(e.getSource().equals(classifierTable) && e.getClickCount() == 2 && 
    		e.getButton() == MouseEvent.BUTTON1 && !e.isControlDown() == false){
    		int selectedIndex = classifierTable.getSelectedRow();
			ClassifierDetailsDialog dialog = new ClassifierDetailsDialog(mainFrame,
				classifierTableModel.getIndexAt(selectedIndex));
			dialog.setLocationRelativeTo(this);
			dialog.setVisible(true);    		    				
    	}else if(e.getSource().equals(classifierTable) && e.getButton() == MouseEvent.BUTTON1 && e.isControlDown()){    		
    		int selectedRow = classifierTable.rowAtPoint(e.getPoint());    		
    		classifierTable.setRowSelectionInterval(selectedRow,selectedRow);
    		classifierPopupMenu.show(classifierTable,e.getX(),e.getY());
    	}    
    }          
 	public void mouseEntered(MouseEvent e) {
 		//Invoked when the mouse enters a component. 
 	}          
 	public void mouseExited(MouseEvent e) {
 		//Invoked when the mouse exits a component. 
 	}          
 	public void mousePressed(MouseEvent e) {
 		//Invoked when a mouse button has been pressed on a component. 
 	}          
 	public void mouseReleased(MouseEvent e) {
 		//Invoked when a mouse button has been released on a component. 
 	}          
 	
 	private void writeToFile(){
 		try{
			JFileChooser fc;				    	
	    	fc = new JFileChooser(outputDirectory);
	    	FileNameExtensionFilter filter = new FileNameExtensionFilter(
		            "Fasta Files", "fasta", "fasta");
		    fc.setFileFilter(filter);	
			int returnVal = fc.showSaveDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();       
				String savingFilename = file.getAbsolutePath();
				if(savingFilename.indexOf(".fasta") == -1)
					savingFilename += ".fasta";
				BufferedWriter output = new BufferedWriter(new FileWriter(savingFilename));
				BufferedWriter outputInfoOnly = new BufferedWriter(new FileWriter(savingFilename + ".info"));
				this.sequenceNameTableModel.writeToFile(output, outputInfoOnly, this.outputDirectory);			
				output.close();			
				outputInfoOnly.close();
			}
		}catch(Exception e){
			JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();}
 	}
 	
    public void actionPerformed(ActionEvent ae){
    	if(ae.getSource().equals(this.checkAllButton)){
    		this.sequenceNameTableModel.checkAll();
    	}else if(ae.getSource().equals(this.uncheckAllButton)){
    		this.sequenceNameTableModel.uncheckAll();
    	}else if(ae.getSource().equals(this.checkTopButton)){
    		try{
    			int top = Integer.parseInt(this.topCheckField.getText());
    			if(top <= 0){
    				throw new NumberFormatException();
    			}
    			this.sequenceNameTableModel.checkTop(top);
    		}catch(NumberFormatException e){
    			JOptionPane.showMessageDialog(null, "Enter integers > 0 only for Top");
    		}
    	}else if(ae.getSource().equals(this.viewLocallyButton)){
    		this.viewInternetButton.setSelected(false);
    	}else if(ae.getSource().equals(this.viewInternetButton)){
    		this.viewLocallyButton.setSelected(false);
    	}else if(ae.getSource().equals(this.copyFastaMenuItem)){
    		int selectedRow = this.sequenceNameTable.getSelectedRow(); 
    		SequenceNameData data = this.sequenceNameTableModel.getData().get(selectedRow);
    		String fasta = data.getHeader() + "\t" + data.getScoreLine() + data.getSequence();
    		MyClipboard board = new MyClipboard();
    		board.setClipboardContents(fasta);
    	}else if(ae.getSource().equals(this.copyHeaderMenuItem)){
    		int selectedRow = this.sequenceNameTable.getSelectedRow();
    		SequenceNameData data = this.sequenceNameTableModel.getData().get(selectedRow);
    		String header = data.getHeader();
    		MyClipboard board = new MyClipboard();
    		board.setClipboardContents(header);
    	}else if(ae.getSource().equals(this.copySequenceMenuItem)){
    		int selectedRow = this.sequenceNameTable.getSelectedRow();
    		SequenceNameData data = this.sequenceNameTableModel.getData().get(selectedRow);
    		String sequence = data.getSequence();
    		MyClipboard board = new MyClipboard();
    		board.setClipboardContents(sequence);
    	}else if(ae.getSource().equals(this.blastButton)){
    		if(this.outputDirectory == null){
    			JOptionPane.showMessageDialog(mainFrame,"Please set output directory first~!",
						"Output Directory not set!~",JOptionPane.INFORMATION_MESSAGE);
    			this.setOutputDirectory();
    		}
    		SwingWorker<Void,Void> worker = new SwingWorker<Void, Void>() {
				@Override
				public Void doInBackground() {
					blastButton.setEnabled(false);
					sequenceNameTableModel.blast(statusPane, filterLowComplexityCheckedButton.isSelected(),
							outputDirectory);
					blastButton.setEnabled(true);
					return null;
				}
				@Override
				protected void done() {										
				}
			};			
			worker.execute();	
    		
    	}else if(ae.getSource().equals(this.filterButton)){
    		this.filterDialog.setVisible(true);
    	}else if(ae.getSource().equals(this.writeToFileButton)){
    		writeToFile();
    	}
    	else if(ae.getSource().equals(this.sortScoreButton)){
    		try{
    			int pos = Integer.parseInt(this.sortScoreTextField.getText());
    			this.sequenceNameTableModel.sort(pos);
    			sequenceNameTable.setRowSelectionInterval(1,1);
    			sequenceNameTable.setRowSelectionInterval(0,0);
    		}catch(NumberFormatException ex){
    			JOptionPane.showMessageDialog(this,"Please enter number into Position TextField","Error", JOptionPane.WARNING_MESSAGE);
    		}
    	}
    	else if(ae.getSource().equals(stopClassifierMenuItem) || 
    		ae.getSource().equals(stopClassifierPopupMenuItem)){
    		stopClassifier = true;    	
    	}else if(ae.getSource().equals(onAllPositionsMenuItem)){    		
    		onAllPositionsMenuItem.setState(true);     	
    		onMotifsOnlyMenuItem.setState(false);
    	}else if(ae.getSource().equals(onMotifsOnlyMenuItem)){ 
    		MotifListDialog dialog = new MotifListDialog(motifListTableModel);
			dialog.setLocationRelativeTo(this);
			dialog.setVisible(true);       		
    		onAllPositionsMenuItem.setState(false);
    		onMotifsOnlyMenuItem.setState(true);
    	}else if(ae.getSource().equals(showMotifListMenuItem)){    		
			MotifListDialog dialog = new MotifListDialog(motifListTableModel);
			dialog.setLocationRelativeTo(this);
			dialog.setVisible(true);    		
    	}else if(ae.getSource().equals(loadFastaFileMenuItem)){
    		loadFastaFileMenuItem.setState(false);
    		if(this.loaderThread == null){
	    		this.loaderThread = new Thread(){
	    			public void run(){ 
	    				loadFastaFile();
	    				loaderThread = null; 	
	    			}
	    		};
	    		this.loaderThread.setPriority(Thread.MIN_PRIORITY); // UI has most priority
	      		this.loaderThread.start();
	    	}
	    	else{
	     		JOptionPane.showMessageDialog(this,"Can't Load Fasta File now,\n"
	      			+ "Currently busy with other processes","Load Fasta File", JOptionPane.WARNING_MESSAGE);
	    	}	    		
    	}else if(ae.getSource().equals(loadClassifierMenuItem)){
    		if(this.loaderThread == null){
	    		this.loaderThread = new Thread(){
	    			public void run(){ 
	    				try{
	    				FileNameExtensionFilter filter = new FileNameExtensionFilter(
	    			            "Classifier Files", "classifierone","classifiertwo");
	    				String classifierFileLocation = Utils.selectFile(
	    						"Please select a classifier", filter);
	    				ClassifierData cData = SiriusClassifier.loadClassifier(classifierFileLocation);
	    				if(cData != null){
	    					classifierTableModel.add(cData);
	    				}
	    				loaderThread = null;
	    				}catch(Exception e){e.printStackTrace();}
	    			}
	    		};
	    		this.loaderThread.setPriority(Thread.MIN_PRIORITY); // UI has most priority
	      		this.loaderThread.start();
	    	}
	    	else{
	     		JOptionPane.showMessageDialog(this,"Can't Load Classifier now,\n"
	      			+ "Currently busy with other processes","Load Classifier", JOptionPane.WARNING_MESSAGE);
	    	}	    		
    	}else if(ae.getSource().equals(deleteClassifierMenuItem) || 
    		ae.getSource().equals(deleteClassifierPopupMenuItem)){    		
    		int selectedIndex = classifierTable.getSelectedRow();
    		if(selectedIndex != -1){
    			classifierTableModel.remove(selectedIndex);  
    		}			    		
    		else{
    			JOptionPane.showMessageDialog(this,"Please choose a Classifier to Delete!",
    				"No Classifier selected",JOptionPane.INFORMATION_MESSAGE); 
    		} 		
    	}else if(ae.getSource().equals(runClassifierMenuItem)||
    		ae.getSource().equals(runClassifierPopupMenuItem)){
    		if(this.loaderThread == null){
	    		this.loaderThread = new Thread(){
	    			public void run(){
	    				stopClassifierMenuItem.setEnabled(true); 
	    				stopClassifierPopupMenuItem.setEnabled(true);
	    				int selectedIndex = classifierTable.getSelectedRow();
				    	//Check if a classifier has been selected
	    				while(outputDirectory == null){
	    					JOptionPane.showMessageDialog(mainFrame,"Please set output directory first~!",
									"Output Directory not set!~",JOptionPane.INFORMATION_MESSAGE);
	    					setOutputDirectory();
	    					//loaderThread = null;
	    					//return;
	    				}	    				
				    	if(selectedIndex != -1){
				    		ClassifierData classifierData = classifierTableModel.getIndexAt(selectedIndex);
				    		if(classifierData.getClassifierType()!=3){
				    			if(classifierData.getClassifierType() == 2 && onMotifsOnlyMenuItem.getState())
				    				runType2ClassifierWithMotifList(classifierData);
				    			else
				    				runClassifier(classifierData,onAllPositionsMenuItem.getState());				    			
				    		}			    				
			    			else 
			    				runType3Classifier(classifierData);
			    		}			    		
						else{
							JOptionPane.showMessageDialog(mainFrame,"Please choose a Classifier!",
								"No Classifier selected",JOptionPane.INFORMATION_MESSAGE); 
						} 		    		    			
				    	stopClassifierMenuItem.setEnabled(false);
				    	stopClassifierPopupMenuItem.setEnabled(false);
	    				loaderThread = null; 	
	    			}
	    		};
	    		this.loaderThread.setPriority(Thread.MIN_PRIORITY); // UI has most priority
	      		this.loaderThread.start();
	    	}
	    	else{
	     		JOptionPane.showMessageDialog(this,"Can't Run Classifier On Dataset now,\n"
	      			+ "Currently busy with other processes","Run Classifier", JOptionPane.WARNING_MESSAGE);
	    	}	    		
    	}else if(ae.getSource().equals(showClassifierDetailsMenuItem)||
    		ae.getSource().equals(showDetailsClassifierPopupMenuItem)){    		
    		int selectedIndex = classifierTable.getSelectedRow();
    		if(selectedIndex != -1){
    			ClassifierDetailsDialog dialog = new ClassifierDetailsDialog(mainFrame,
    				classifierTableModel.getIndexAt(selectedIndex));
    			dialog.setLocationRelativeTo(this);
				dialog.setVisible(true);
    		}			    		
    		else{
    			JOptionPane.showMessageDialog(this,"Please choose a Classifier!",
    				"No Classifier selected",JOptionPane.INFORMATION_MESSAGE); 
    		} 		
    	}else if(ae.getSource().equals(setDirectoryMenuItem)){
    		this.setOutputDirectory();
    	}else if(ae.getSource().equals(loadScoreFileMenuItem)){
    		loadScoreFileMenuItem.setState(false);
    		if(this.loaderThread == null){
	    		this.loaderThread = new Thread(){
	    			public void run(){ 
	    				loadScoreFile();
	    				loaderThread = null; 	
	    			}
	    		};
	    		this.loaderThread.setPriority(Thread.MIN_PRIORITY); // UI has most priority
	      		this.loaderThread.start();
	    	}
	    	else{
	     		JOptionPane.showMessageDialog(this,"Can't Load Score File now,\n"
	      			+ "Currently busy with other processes","Load Score File", JOptionPane.WARNING_MESSAGE);
	    	}	    		
    	}    		
    }      
    
    private void setOutputDirectory(){
    	JFileChooser chooser = new JFileChooser();
		String tempString = SiriusSettings.getInformation("LoaderOutputDirectory: ");
		if(tempString != null)
			chooser.setCurrentDirectory(new File(tempString));
		chooser.setDialogTitle("Set Working Directory");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
			outputDirectory = chooser.getSelectedFile().toString();
			SiriusSettings.updateInformation("LoaderOutputDirectory: ", outputDirectory);
	   	}
	    else{
	    	//no selection
	    }
    }
    
    private void runType3Classifier(ClassifierData classifierData){  
    	/*
    	 * This is for type3 classifier
    	 * Note that all position and motif list only does not apply to this classifier as
    	 * it will only give one score for each sequence
    	 */
    	if(sequenceNameTableModel.getRowCount() < 1){
    		JOptionPane.showMessageDialog(this,"Please load File first!",
    				"No Sequence",JOptionPane.INFORMATION_MESSAGE); 
    		return;
    	}
    	if(loadFastaFileMenuItem.getState()==false){
    		JOptionPane.showMessageDialog(this,"Please load Fasta File! Currently, you have score file!",
    				"Wrong File Format",JOptionPane.INFORMATION_MESSAGE); 
    		return;
    	}
    	if(onAllPositionsMenuItem.getState()==false){
    		JOptionPane.showMessageDialog(this,
    			"For type 3 classifier, it make only one prediction a sequence",
    			"Information",JOptionPane.INFORMATION_MESSAGE);     	
    	}    			
		try{    			
			BufferedWriter output = new BufferedWriter(
				new FileWriter(outputDirectory + File.separator + "classifierone_" + classifierData.getClassifierName() 
					+ "_" + classifierData.getClassifierType() + "_" + fastaFilename + ".scores"));			
			Classifier classifierOne = classifierData.getClassifierOne();					
			//Reading and Storing the featureList
			Instances inst = classifierData.getInstances();
	 		ArrayList<Feature> featureDataArrayList = new ArrayList<Feature>();
	 		for(int x = 0; x < inst.numAttributes() - 1; x++){
				//-1 because class attribute must be ignored
	 			featureDataArrayList.add(Feature.levelOneClassifierPane(inst.attribute(x).name()));
	 		}     	 			
	 		//Going through each and every sequence
			for(int x = 0; x < sequenceNameTableModel.getRowCount(); x++){
				if(stopClassifier == true){
					statusPane.setText("Running of Classifier Stopped!");
					stopClassifier = false;
					output.close();
					return;
				}
				//if(x%100 == 0)
					statusPane.setText("Running " + classifierData.getClassifierName() + 
						" - ClassifierOne @ " + x +" / " + sequenceNameTableModel.getRowCount());
				//Header  				
				output.write(sequenceNameTableModel.getHeader(x));
				output.newLine();
				output.write(sequenceNameTableModel.getSequence(x));
				output.newLine();
				//Sequence Score -> index-score, index-score
				String sequence = sequenceNameTableModel.getSequence(x);								
				Instance tempInst;
 				tempInst = new Instance(inst.numAttributes());
 				tempInst.setDataset(inst);     				
 				for(int z = 0; z < inst.numAttributes() - 1; z++){
 					//-1 because class attribute can be ignored
 					//Give the sequence and the featureList to get the feature freqs on the sequence
 					Object obj = GenerateArff.getMatchCount(
 		     				"+1_Index(-1)",sequence,featureDataArrayList.get(z),classifierData.getScoringMatrixIndex(),
 		     				classifierData.getCountingStyleIndex(),classifierData.getScoringMatrix());
 					if(obj.getClass().getName().equalsIgnoreCase("java.lang.Integer"))
 						tempInst.setValue(z,(Integer) obj);
 					else if(obj.getClass().getName().equalsIgnoreCase("java.lang.Double"))				
 						tempInst.setValue(z,(Double) obj);
 					else if(obj.getClass().getName().equalsIgnoreCase("java.lang.String"))
 						tempInst.setValue(z, (String) obj);
 					else{
 						output.close();
 						throw new Error("Unknown: " + obj.getClass().getName());
 					}
	     		}     	
	     		//note that pos or neg does not matter as this is not used
	     		tempInst.setValue(inst.numAttributes() - 1,"pos");
	     		try{
	     			double[] results = classifierOne.distributionForInstance(tempInst);
	     			output.write("0=" + results[0]);
	     		}
	     		catch(Exception e){
	     			//this is to ensure that the run will continue	     		
	     			output.write("0=-0.0");
	     			//change throw error to screen output if i want the run to continue
	     			System.err.println("Exception has Occurred for classifierOne.distributionForInstance(tempInst);");
	     		}
				output.newLine();
				output.flush();
			}
			output.flush();
			output.close();
			
			statusPane.setText("ClassifierOne finished running...");						
			loadScoreFile(outputDirectory + File.separator + "classifierone_" + classifierData.getClassifierName() 
					+ "_" + classifierData.getClassifierType() + "_" + fastaFilename + ".scores");
		}catch(Exception e){
			JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();}    				
    }
    
    private void runClassifier(ClassifierData classifierData, boolean allPositions){
    	//this method is for type 1 classifier with all positions and motif list
    	//and type 2 classifier with all positions
    	if(sequenceNameTableModel.getRowCount() < 1){
    		JOptionPane.showMessageDialog(this,"Please load File first!",
    				"No Sequence",JOptionPane.INFORMATION_MESSAGE); 
    		return;
    	}
    	if(loadFastaFileMenuItem.getState()==false){
    		JOptionPane.showMessageDialog(this,"Please load Fasta File! Currently, you have score file!",
    				"Wrong File Format",JOptionPane.INFORMATION_MESSAGE); 
    		return;
    	}
    	if(onAllPositionsMenuItem.getState()==false && motifListTableModel.getSize() == 0){
    		JOptionPane.showMessageDialog(this,"There are no Motifs chosen in Motif List!",
    				"No Motifs",JOptionPane.INFORMATION_MESSAGE); 
    		MotifListDialog dialog = new MotifListDialog(motifListTableModel);
			dialog.setLocationRelativeTo(this);
			dialog.setVisible(true);  
    		return;
    	}    			
    	while(outputDirectory == null){
    		JOptionPane.showMessageDialog(this,"Please set output directory first!",
    				"Output Directory not set",JOptionPane.INFORMATION_MESSAGE);
    		setOutputDirectory();
    		//return;
    	}
		try{    			
			BufferedWriter output = new BufferedWriter(
				new FileWriter(outputDirectory + File.separator + "classifierone_" + 
						classifierData.getClassifierName() 
					+ "_" + classifierData.getClassifierType() + "_" + fastaFilename + ".scores"));			
			Classifier classifierOne = classifierData.getClassifierOne();
			int leftMostPosition = classifierData.getLeftMostPosition();
			int rightMostPosition = classifierData.getRightMostPosition();
			//Reading and Storing the featureList
			Instances inst = classifierData.getInstances();
	 		ArrayList<Feature> featureDataArrayList = new ArrayList<Feature>();
	 		for(int x = 0; x < inst.numAttributes() - 1; x++){
				//-1 because class attribute must be ignored
	 			featureDataArrayList.add(Feature.levelOneClassifierPane(inst.attribute(x).name()));
	 		}     
	 			
			for(int x = 0; x < sequenceNameTableModel.getRowCount(); x++){
				if(stopClassifier == true){
					statusPane.setText("Running of Classifier Stopped!");
					stopClassifier = false;
					output.close();
					return;
				}
				//if(x%100 == 0)
					statusPane.setText("Running " + classifierData.getClassifierName() + 
						" - ClassifierOne @ " + x +" / " + sequenceNameTableModel.getRowCount());
				//Header  				
				output.write(sequenceNameTableModel.getHeader(x));
				output.newLine();
				output.write(sequenceNameTableModel.getSequence(x));
				output.newLine();
				//Sequence Score -> index-score, index-score
				String sequence = sequenceNameTableModel.getSequence(x);
				int minSequenceLengthRequired;
				int targetLocationIndex;
				if(leftMostPosition < 0 && rightMostPosition > 0){// -ve and +ve
					minSequenceLengthRequired = (leftMostPosition*-1) + rightMostPosition;
					targetLocationIndex = (leftMostPosition*-1);
				}else if(leftMostPosition < 0 && rightMostPosition < 0){//-ve and -ve
					minSequenceLengthRequired = rightMostPosition - leftMostPosition + 1;
					targetLocationIndex = (leftMostPosition*-1);
				}else{//+ve and +ve
					minSequenceLengthRequired = rightMostPosition - leftMostPosition + 1;
					targetLocationIndex = (leftMostPosition*-1);
				}
				boolean firstEntryForClassifierOne = true;
				for(int y = 0; y + (minSequenceLengthRequired - 1) < sequence.length(); y++){
					//Check if targetLocation match any motif in motif List
					if(allPositions == false && motifListTableModel.gotMotifMatch(sequence.substring(y+0,y+targetLocationIndex))== false)
						continue;
					String line2 = sequence.substring(y+0,y+minSequenceLengthRequired);
					Instance tempInst;
	 				tempInst = new Instance(inst.numAttributes());
	 				tempInst.setDataset(inst);     				
	 				for(int z = 0; z < inst.numAttributes() - 1; z++){
	 					//-1 because class attribute can be ignored
	 					//Give the sequence and the featureList to get the feature freqs on the sequence
	 					Object obj = GenerateArff.getMatchCount(
			     				"+1_Index(" + targetLocationIndex + ")",line2,featureDataArrayList.get(z),classifierData.getScoringMatrixIndex(),
			     				classifierData.getCountingStyleIndex(),classifierData.getScoringMatrix());
	 					if(obj.getClass().getName().equalsIgnoreCase("java.lang.Integer"))
	 						tempInst.setValue(z,(Integer) obj);
	 					else if(obj.getClass().getName().equalsIgnoreCase("java.lang.Double"))				
	 						tempInst.setValue(z,(Double) obj);
	 					else if(obj.getClass().getName().equalsIgnoreCase("java.lang.String"))
	 						tempInst.setValue(z, (String) obj);
	 					else{
	 						output.close();
	 						throw new Error("Unknown: " + obj.getClass().getName());
	 					}
		     		}     	
		     		//note that pos or neg does not matter as this is not used
		     		tempInst.setValue(inst.numAttributes() - 1,"neg");
		     		double[] results = classifierOne.distributionForInstance(tempInst);
		     		if(firstEntryForClassifierOne)
		     			firstEntryForClassifierOne = false;
		     		else
		     			output.write(",");
		     		output.write(y + targetLocationIndex + "=" + results[0]);
				}
				output.newLine();
				output.flush();
			}
			output.flush();
			output.close();
			
			statusPane.setText("ClassifierOne finished running...");
			
			//Run classifier Two if it is type 2
			if(classifierData.getClassifierType() == 2){    				
				BufferedWriter output2 = new BufferedWriter(
					new FileWriter(outputDirectory + File.separator + "classifiertwo_" + classifierData.getClassifierName() 
						+ "_" + classifierData.getClassifierType() + "_" + fastaFilename + ".scores"));				
				BufferedReader input2 = new BufferedReader(
					new FileReader(outputDirectory + File.separator + "classifierone_" + classifierData.getClassifierName() 
						+ "_" + classifierData.getClassifierType() + "_" + fastaFilename + ".scores"));				
				Classifier classifierTwo = classifierData.getClassifierTwo();
				Instances inst2 = classifierData.getInstances2();
				int setUpstream = classifierData.getSetUpstream();
				int setDownstream = classifierData.getSetDownstream();
				int minScoreWindowRequired;
				if(setUpstream < 0 && setDownstream < 0){//-ve and -ve
					minScoreWindowRequired = setDownstream - setUpstream + 1;
				}else if(setUpstream < 0 && setDownstream > 0){//-ve and +ve
					minScoreWindowRequired = (setUpstream*-1) + setDownstream;
				}else{//+ve and +ve
					minScoreWindowRequired = setDownstream - setUpstream + 1;
				}
				String lineHeader;
				String lineSequence;
				int lineCounter2 = 0;				
				while((lineHeader = input2.readLine()) != null){					
					if(stopClassifier == true){
    					statusPane.setText("Running of Classifier Stopped!");
    					stopClassifier = false;
    					output2.close();
    					input2.close();
    					return;
    				}
					//if(lineCounter2%100 == 0)
					statusPane.setText("Running " + classifierData.getClassifierName() + 
						" - ClassifierTwo @ " + lineCounter2 +" / " + sequenceNameTableModel.getRowCount());
					lineSequence = input2.readLine();
					output2.write(lineHeader);
					output2.newLine();
					output2.write(lineSequence);
					output2.newLine();
					StringTokenizer locationScore = new StringTokenizer(input2.readLine(),",");
					int totalTokens = locationScore.countTokens();
					String[][] scores = new String[totalTokens][2];
					int scoreIndex = 0;
					while(locationScore.hasMoreTokens()){
						StringTokenizer locationScoreToken = new StringTokenizer(locationScore.nextToken(),
							"=");
						scores[scoreIndex][0] = locationScoreToken.nextToken();//location
						scores[scoreIndex][1] = locationScoreToken.nextToken();//score
						scoreIndex++;
					}
					int targetLocationIndex2;
					if(setUpstream == 0 || setDownstream == 0){
						output2.close();
    					input2.close();
						throw new Exception("setUpstream == 0 || setDownstream == 0");
					}
					if(setUpstream < 0){
						targetLocationIndex2 = Integer.parseInt(scores[0][0]) + (-setUpstream);
					}else{//setUpstream > 0
						targetLocationIndex2 = Integer.parseInt(scores[0][0]); //first location
					}      					  					
					for(int x = 0; x + minScoreWindowRequired - 1 < totalTokens; x++){
						//+1 is for the class index
						if(x != 0)
							output2.write(",");
	     				Instance tempInst2 = new Instance(minScoreWindowRequired + 1);
	     				tempInst2.setDataset(inst2);
	 					for(int y = 0; y < minScoreWindowRequired; y++){
	     					tempInst2.setValue(y,Double.parseDouble(scores[x+y][1]));
	     				}     					     				
						tempInst2.setValue(tempInst2.numAttributes() - 1,"pos");
	     				double[] results = classifierTwo.distributionForInstance(tempInst2);
	     				output2.write(targetLocationIndex2 + "=" + results[0]);
	     				targetLocationIndex2++;
    				}
					lineCounter2++;
    				output2.newLine();
				}
				input2.close();
				output2.close();
				statusPane.setText("ClassifierTwo finished running...");
			}    			
			if(classifierData.getClassifierType() == 1)
				loadScoreFile(outputDirectory + File.separator + "classifierone_" + classifierData.getClassifierName() 
					+ "_" + classifierData.getClassifierType() + "_" + fastaFilename + ".scores");
			else
				loadScoreFile(outputDirectory + File.separator + "classifiertwo_" + classifierData.getClassifierName() 
					+ "_" + classifierData.getClassifierType() + "_" + fastaFilename + ".scores");
		}catch(Exception e){
			JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();}    				
    }
    
    private void runType2ClassifierWithMotifList(ClassifierData classifierData){
    	//Checking..    	
    	if(sequenceNameTableModel.getRowCount() < 1){
    		JOptionPane.showMessageDialog(this,"Please load File first!",
    				"No Sequence",JOptionPane.INFORMATION_MESSAGE); 
    		return;
    	}
    	if(loadFastaFileMenuItem.getState()==false){
    		JOptionPane.showMessageDialog(this,"Please load Fasta File! Currently, you have score file!",
    				"Wrong File Format",JOptionPane.INFORMATION_MESSAGE); 
    		return;
    	}
    	if(motifListTableModel.getSize() == 0){
    		JOptionPane.showMessageDialog(this,"There are no Motifs chosen in Motif List!",
    				"No Motifs",JOptionPane.INFORMATION_MESSAGE); 
    		MotifListDialog dialog = new MotifListDialog(motifListTableModel);
			dialog.setLocationRelativeTo(this);
			dialog.setVisible(true);  
    		return;
    	}        			
    	//Proper running start
		try{    			
			//classifierOne score output
			BufferedWriter output = new BufferedWriter(
				new FileWriter(outputDirectory + File.separator + "classifierone_" + classifierData.getClassifierName() 
					+ "_" + classifierData.getClassifierType() + "_" + fastaFilename + ".scores"));			
			Classifier classifierOne = classifierData.getClassifierOne();
			int leftMostPosition = classifierData.getLeftMostPosition();
			int rightMostPosition = classifierData.getRightMostPosition();
			//Reading and Storing the featureList
			Instances inst = classifierData.getInstances();
	 		ArrayList<Feature> featureDataArrayList = new ArrayList<Feature>();
	 		for(int x = 0; x < inst.numAttributes() - 1; x++){
				//-1 because class attribute must be ignored
	 			featureDataArrayList.add(Feature.levelOneClassifierPane(inst.attribute(x).name()));
	 		}	 	 			 			
			//initialization for type 2 classifier					
			BufferedWriter output2 = new BufferedWriter(
				new FileWriter(outputDirectory + File.separator + "classifiertwo_" + classifierData.getClassifierName() 
					+ "_" + classifierData.getClassifierType() + "_" + fastaFilename + ".scores"));			
			int setUpstream = classifierData.getSetUpstream();
			int setDownstream = classifierData.getSetDownstream();				
			int minScoreWindowRequired;
			if(setUpstream < 0 && setDownstream < 0){//-ve and -ve
				minScoreWindowRequired = setDownstream - setUpstream + 1;
			}else if(setUpstream < 0 && setDownstream > 0){//-ve and +ve
				minScoreWindowRequired = (setUpstream*-1) + setDownstream;
			}else{//+ve and +ve
				minScoreWindowRequired = setDownstream - setUpstream + 1;
			}
			Classifier classifierTwo = classifierData.getClassifierTwo();
			Instances inst2 = classifierData.getInstances2();
			if(setUpstream == 0 || setDownstream == 0){
				output.close();
				output2.close();
				throw new Exception("setUpstream == 0 || setDownstream == 0");
			}
			//for each sequence
			for(int x = 0; x < sequenceNameTableModel.getRowCount(); x++){
				if(stopClassifier == true){
					statusPane.setText("Running of Classifier Stopped!");
					stopClassifier = false;
					output.close();
					output2.close();
					return;
				}
				//if(x%100 == 0)
					statusPane.setText("Running " + classifierData.getClassifierName() + 
						" - ClassifierOne @ " + x +" / " + sequenceNameTableModel.getRowCount());
				//Header  				
				output.write(sequenceNameTableModel.getHeader(x));
				output.newLine();
				output.write(sequenceNameTableModel.getSequence(x));
				output.newLine();				
				output2.write(sequenceNameTableModel.getHeader(x));
				output2.newLine();
				output2.write(sequenceNameTableModel.getSequence(x));
				output2.newLine();				
				//Sequence Score -> index-score, index-score
				String sequence = sequenceNameTableModel.getSequence(x);
				int minSequenceLengthRequired;
				int targetLocationIndex;
				//set the targetLocationIndex and minSequenceLengthRequired
				if(leftMostPosition < 0 && rightMostPosition > 0){// -ve and +ve
					minSequenceLengthRequired = (leftMostPosition*-1) + rightMostPosition;
					targetLocationIndex = (leftMostPosition*-1);
				}else if(leftMostPosition < 0 && rightMostPosition < 0){//-ve and -ve
					minSequenceLengthRequired = rightMostPosition - leftMostPosition + 1;
					targetLocationIndex = (leftMostPosition*-1);
				}else{//+ve and +ve
					minSequenceLengthRequired = rightMostPosition - leftMostPosition + 1;
					targetLocationIndex = (leftMostPosition*-1);
				}
				//This hashtable is used to ensure that on positions where predictions are already made,
				//we just skip. This will happen only if it is a type 2 classifier
				Hashtable<Integer,Double> scoreTable = new Hashtable<Integer,Double>();
				boolean firstEntryForClassifierOne = true;
				boolean firstEntryForClassifierTwo = true;
				for(int y = 0; y + (minSequenceLengthRequired - 1) < sequence.length(); y++){
					int endPoint = y;//endPoint should be the exact position
					int currentY = y;
					int startPoint = y;
					//run only on Motifs?					
					if(onMotifsOnlyMenuItem.getState()){
						//Check if targetLocation match any motif in motif List
						if(motifListTableModel.
							gotMotifMatch(sequence.substring(y+0,y+targetLocationIndex))== false)
							continue; //position not found in motif list
						else
							//rollback to upstream and make prediction all the way till downstream
							//needed for type 2 classifier
							currentY += setUpstream;
							if(setUpstream > 0)
								currentY--;						
							startPoint = currentY;							
							//note that y starts from 0 so y is surely >= 0
							endPoint += setDownstream;
							if(setDownstream > 0)
								endPoint--;
							//check still within bound of the sequence
							if(startPoint < 0 || 
								endPoint >= sequence.length() - (minSequenceLengthRequired - 1))
								continue;//out of bounds						
					}						
					while(currentY <= endPoint){
						if(scoreTable.get(currentY + targetLocationIndex) != null){
							currentY++;
							continue;
						}										
						String line2 = sequence.substring(currentY+0,currentY+minSequenceLengthRequired);
						Instance tempInst;
		 				tempInst = new Instance(inst.numAttributes());
		 				tempInst.setDataset(inst);     				
		 				for(int z = 0; z < inst.numAttributes() - 1; z++){
		 					//-1 because class attribute can be ignored
		 					//Give the sequence and the featureList to get the feature freqs on the sequence
		 					Object obj = GenerateArff.getMatchCount(
				     				"+1_Index(" + targetLocationIndex + ")",line2,featureDataArrayList.get(z),
				     				classifierData.getScoringMatrixIndex(),classifierData.getCountingStyleIndex(),classifierData.getScoringMatrix());
		 					if(obj.getClass().getName().equalsIgnoreCase("java.lang.Integer"))
		 						tempInst.setValue(z,(Integer) obj);
		 					else if(obj.getClass().getName().equalsIgnoreCase("java.lang.Double"))				
		 						tempInst.setValue(z,(Double) obj);
		 					else if(obj.getClass().getName().equalsIgnoreCase("java.lang.String"))
		 						tempInst.setValue(z, (String) obj);
		 					else{
		 						output.close();
		 						output2.close();
		 						throw new Error("Unknown: " + obj.getClass().getName());
		 					}
			     		}     	
			     		//note that pos or neg does not matter as this is not used
			     		tempInst.setValue(inst.numAttributes() - 1,"neg");
			     		double[] results = classifierOne.distributionForInstance(tempInst);
			     		if(firstEntryForClassifierOne)
			     			firstEntryForClassifierOne = false;
			     		else
			     			output.write(",");
			     		output.write(currentY + targetLocationIndex + "=" + results[0]);
			     		scoreTable.put(currentY + targetLocationIndex,results[0]);
			     		currentY++;			     		
					}					
     				Instance tempInst2 = new Instance(minScoreWindowRequired + 1);//+1 for class attribute
     				tempInst2.setDataset(inst2);
     				int indexForClassifier2Inst = 0;
 					for(int z = startPoint; z <= endPoint; z++){
     					tempInst2.setValue(indexForClassifier2Inst,
     						scoreTable.get(targetLocationIndex + z));
     					indexForClassifier2Inst++;
     				}     	
     				//note that pos or neg does not matter as this is not used
					tempInst2.setValue(tempInst2.numAttributes() - 1,"pos");
     				double[] results = classifierTwo.distributionForInstance(tempInst2);
     				if(firstEntryForClassifierTwo == true)
     					firstEntryForClassifierTwo = false;
     				else
     					output2.write(",");	     				
     				output2.write(y + targetLocationIndex + "=" + results[0]);
				}//end of for loop				
				output2.newLine();
				output2.flush();				
				output.newLine();
				output.flush();
			}			
			output.close();			
			output2.close();
			
			statusPane.setText("Classifier Finished running...");			
			loadScoreFile(outputDirectory + File.separator + "classifiertwo_" + classifierData.getClassifierName() 
				+ "_" + classifierData.getClassifierType() + "_" + fastaFilename + ".scores");
		}catch(Exception e){
			JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();}    				
    }
    
    public void valueChanged(ListSelectionEvent e){
    	if(e.getSource().equals(sequenceNameTable.getSelectionModel())){
    		int index = sequenceNameTable.getSelectedRow();
    		if(index != -1){
				String header = sequenceNameTableModel.getHeader(index);
				String sequence = sequenceNameTableModel.getSequence(index);
				String scoreLine = sequenceNameTableModel.getScoreLine(index);
				sequenceTableModel.setSequence(header,sequence);
				scoreGraphPane.setScoreLine(scoreLine);    		
				port.revalidate();
				scoreGraphPane.repaint();
				port.repaint(); 
    		}
    	}
    }
    
    private void loadFastaFile(){    	
    	String lastFastaFileLocation = SiriusSettings.getInformation("LastFastaFileLocationByLoader: ");    	
    	JFileChooser fc = new JFileChooser(lastFastaFileLocation);
    	FileNameExtensionFilter filter = new FileNameExtensionFilter( "Fasta Files", "fasta");
	    fc.setFileFilter(filter);	
		int returnVal = fc.showOpenDialog(this);		
		if (returnVal == JFileChooser.APPROVE_OPTION) {	        
			File file = fc.getSelectedFile();
			SiriusSettings.updateInformation("LastFastaFileLocationByLoader: ", file.getAbsolutePath());			
            try{			            		          	            
	            String outputDirectory = sequenceNameTableModel.loadFastaFile(file);
	            if(outputDirectory != null){
	            	this.outputDirectory = outputDirectory;
	            }
	            StringTokenizer st = new StringTokenizer(file.getName(),".");
	            this.fastaFilename = st.nextToken();
	            sequenceNameTable.setRowSelectionInterval(1,1); 
	        	sequenceNameTable.setRowSelectionInterval(0,0);     
	            loadFastaFileMenuItem.setState(true);
	            loadScoreFileMenuItem.setState(false);
            }catch(Exception e){
            	JOptionPane.showMessageDialog(null,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            	e.printStackTrace();}                   		
	    }else{
	        statusPane.setText("Open command is cancelled by user.");	        
	    }			        
    }
    private void loadScoreFile(String filename){    	
        try{			            		          
            BufferedReader in = new BufferedReader(new FileReader(filename));            
            String line;
            //there could be a bug within these few lines as having ">" at index 0 
            //is not limited to fasta only..
            //pdf files also have this feature..
            sequenceNameTableModel.reset();
            String eachSequence = "";
            String sequenceName = "";	   
            String scoreLine = "";            
            while ((line = in.readLine()) != null) {            	
            	if(line.indexOf(">")==0){
            		sequenceName = line;
            		if(sequenceName.indexOf("OutputDirectory: ") != -1){
            			this.outputDirectory = sequenceName.substring(sequenceName.indexOf("OutputDirectory: ") + 
            					"OutputDirectory: ".length());
            		}
            		eachSequence = in.readLine();
            		scoreLine = in.readLine();
            		sequenceNameTableModel.add(new SequenceNameData(sequenceName,eachSequence,scoreLine, "", ""));
            	}else{
            		JOptionPane.showMessageDialog(this,"Please ensure that " + 
	            		filename + " is in Score File format.",
	            		"ERROR",JOptionPane.ERROR_MESSAGE);
            		in.close();
	            	return;
            	}
            }
            //need this so that there will be a change, and only with a change the view will be updated
            in.close();
    		sequenceNameTable.setRowSelectionInterval(1,1);
    		sequenceNameTable.setRowSelectionInterval(0,0);
            loadFastaFileMenuItem.setState(false);
            loadScoreFileMenuItem.setState(true);
            sortScoreButton.setEnabled(true);
            writeToFileButton.setEnabled(true);
        }catch(Exception e){
        	JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
        	e.printStackTrace();}                   			    
    }
    private void loadScoreFile(){
    	String lastScoreFileLocation = SiriusSettings.getInformation("LastScoreFileLocationByLoader: ");
    	JFileChooser fc = new JFileChooser(lastScoreFileLocation);
    	FileNameExtensionFilter filter = new FileNameExtensionFilter(
	            "Scores Files", "scores");
	    fc.setFileFilter(filter);
		int returnVal = fc.showOpenDialog(this);		
		if (returnVal == JFileChooser.APPROVE_OPTION) {	        
			File file = fc.getSelectedFile();
			SiriusSettings.updateInformation("LastScoreFileLocationByLoader: ", file.getAbsolutePath());
            try{			            		          
	            BufferedReader in = new BufferedReader(new FileReader(file.getAbsolutePath()));
	            StringTokenizer st = new StringTokenizer(file.getName(),".");
	            this.fastaFilename = st.nextToken();
	            String line;
	            //there could be a bug within these few lines as having ">" at index 0 
	            //is not limited to fasta only..
	            //pdf files also have this feature..
	            sequenceNameTableModel.reset();
	            String eachSequence = "";
	            String sequenceName = "";	   
	            String scoreLine = "";
	            while ((line = in.readLine()) != null) {	            
	            	if(line.indexOf(">")==0){
	            		sequenceName = line;
	            		eachSequence = in.readLine();
	            		scoreLine = in.readLine();
	            		sequenceNameTableModel.add(new SequenceNameData(sequenceName,eachSequence,scoreLine, "", ""));
	            	}else{
	            		JOptionPane.showMessageDialog(this,"Please ensure that " + 
		            		file.getAbsolutePath() + " is in Score File format.",
		            		"ERROR",JOptionPane.ERROR_MESSAGE);
	            		in.close();
	            		return;
	            	}
	            }
	            in.close();
    			sequenceNameTable.setRowSelectionInterval(1,1);
    			sequenceNameTable.setRowSelectionInterval(0,0);
	            loadFastaFileMenuItem.setState(false);
            	loadScoreFileMenuItem.setState(true);               	                          	
            }catch(Exception e){
            	JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
            	e.printStackTrace();}                   		
	    }else{
	        statusPane.setText("Open command is cancelled by user.");	        
	    }			        
    }
}

