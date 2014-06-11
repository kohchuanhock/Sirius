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
package sirius.misc.sequencevisualizer;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import javax.swing.JViewport;
import javax.swing.JWindow;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import sirius.predictor.main.SequenceNameTableModel;
import sirius.trainer.features.GenerateArff;
import sirius.trainer.features.gui.advancedphysiochemical.AdvancedPhysioChemicalFeatureComboBoxItem;
import sirius.trainer.main.SiriusSettings;
import sirius.utils.FastaFormat;

public class SequenceVisualizerPane extends JComponent implements ActionListener , ComponentListener, ListSelectionListener, ItemListener,
	FocusListener, MouseMotionListener, MouseListener{
	static final long serialVersionUID = sirius.Sirius.version;
	
	private JTextField inputfileTextField;
	//private JButton inputfileButton;
	
	private JComboBox feature1ComboBox;
	private JTextField feature1ValueCutoffTextField;
	private JTextField feature1LengthCutoffTextField;
	private JTextField feature1WindowSizeTextField;
	private JTextField feature1StepSizeTextField;
	private JTextField feature1ReferenceLineTextField;
	private JCheckBox feature1AccumulativeCheckBox;
	private JCheckBox feature1ShowMaxCheckBox;
	
	private JTable sequenceNameTable;
	private SequenceNameTableModel sequenceNameTableModel;
	
	private JScrollPane sequenceGraphScrollPane;
	private SequenceGraphPane sequenceGraphPane;
	
	private SequenceViewPort sequenceViewPort;
	
	private JInternalFrame parent;		
	private ArrayList<AdvancedPhysioChemicalFeatureComboBoxItem> comboboxItemArrayList;
	
	private JWindow toolTip;
	private JLabel label;	
	
	private JButton deleteButton;
	private JButton undoButton;
	private JButton saveButton;
	
	public SequenceVisualizerPane(final JInternalFrame parent,JTabbedPane tabbedPane){
		this.parent = parent;			
		
		JPanel inputfilePanel = new JPanel();
		inputfilePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Fasta File (Proteins seq only)"),BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		inputfileTextField = new JTextField(15);
		inputfileTextField.setEnabled(false);
		this.inputfileTextField.addMouseListener(this);				
		inputfilePanel.add(inputfileTextField);		
		
		JPanel feature1Panel = new JPanel();
		feature1Panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Feature"),BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		feature1ComboBox = new JComboBox();
		feature1ComboBox.addItem("       ");
		feature1ComboBox.addItemListener(this);
		JLabel feature1ValueCutoffLabel = new JLabel("Value Cutoff: ");		
		feature1ValueCutoffLabel.setForeground(Color.RED);
		this.feature1ValueCutoffTextField = new JTextField(2);
		this.feature1ValueCutoffTextField.addFocusListener(this);
		JLabel feature1LengthCutoffLabel = new JLabel("Length Cutoff: ");
		feature1LengthCutoffLabel.setForeground(Color.RED);
		this.feature1LengthCutoffTextField = new JTextField(2);
		this.feature1LengthCutoffTextField.addFocusListener(this);
		JLabel feature1WindowSizeLabel = new JLabel("Window: ");
		feature1WindowSizeLabel.setForeground(Color.RED);
		this.feature1WindowSizeTextField = new JTextField(2);
		this.feature1WindowSizeTextField.addFocusListener(this);
		JLabel feature1StepSizeLabel = new JLabel("Step: ");
		feature1StepSizeLabel.setForeground(Color.RED);
		this.feature1StepSizeTextField = new JTextField(2);
		this.feature1StepSizeTextField.addFocusListener(this);
		JLabel feature1ReferenceLineLabel = new JLabel("Ref: ");
		feature1ReferenceLineLabel.setForeground(Color.RED);
		this.feature1ReferenceLineTextField = new JTextField(3);
		this.feature1ReferenceLineTextField.addFocusListener(this);
		this.feature1AccumulativeCheckBox = new JCheckBox("Cumulative");
		this.feature1AccumulativeCheckBox.setForeground(Color.RED);
		this.feature1AccumulativeCheckBox.addActionListener(this);
		this.feature1ShowMaxCheckBox = new JCheckBox("Show Max");
		this.feature1ShowMaxCheckBox.setForeground(Color.RED);
		this.feature1ShowMaxCheckBox.addActionListener(this);
		
		feature1Panel.add(feature1ComboBox);
		feature1Panel.add(feature1ValueCutoffLabel);
		feature1Panel.add(feature1ValueCutoffTextField);
		feature1Panel.add(feature1LengthCutoffLabel);
		feature1Panel.add(feature1LengthCutoffTextField);
		feature1Panel.add(feature1WindowSizeLabel);
		feature1Panel.add(feature1WindowSizeTextField);
		feature1Panel.add(feature1StepSizeLabel);
		feature1Panel.add(feature1StepSizeTextField);
		feature1Panel.add(feature1ReferenceLineLabel);
		feature1Panel.add(feature1ReferenceLineTextField);
		feature1Panel.add(feature1AccumulativeCheckBox);
		feature1Panel.add(feature1ShowMaxCheckBox);
		
		JPanel sequenceNameScrollPanePanel = new JPanel(new BorderLayout());
		sequenceNameScrollPanePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Sequences Name"),BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		sequenceNameTableModel = new SequenceNameTableModel(false);		
    	sequenceNameTable = new JTable(sequenceNameTableModel);
    	sequenceNameTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
    	sequenceNameTable.getColumnModel().getColumn(0).setMinWidth(20);       
        sequenceNameTable.getColumnModel().getColumn(1).setMinWidth(70);        
    	sequenceNameTable.getColumnModel().getColumn(0).setPreferredWidth(40);     
        sequenceNameTable.getColumnModel().getColumn(1).setPreferredWidth(200); 
        sequenceNameTable.getSelectionModel().addListSelectionListener(this);
        JScrollPane sequenceNameTableScrollPane = new JScrollPane(sequenceNameTable);
    	sequenceNameTableScrollPane.setPreferredSize(new Dimension(300,420));
    	sequenceNameScrollPanePanel.add(sequenceNameTableScrollPane, BorderLayout.CENTER);    
    	
    	JPanel buttonPanel = new JPanel();
    	this.deleteButton = new JButton(" Delete ");
    	this.deleteButton.addActionListener(this);
    	this.saveButton = new JButton("  Save  ");
    	this.saveButton.addActionListener(this);
    	this.undoButton = new JButton("  Undo  ");
    	this.undoButton.addActionListener(this);
    	buttonPanel.add(this.deleteButton);
    	buttonPanel.add(this.undoButton);
    	buttonPanel.add(this.saveButton);
    	sequenceNameScrollPanePanel.add(buttonPanel, BorderLayout.SOUTH);
    	    	    	    	
    	sequenceGraphPane = new SequenceGraphPane();
    	sequenceViewPort = new SequenceViewPort(sequenceGraphPane);    
    	JPanel sequenceGraphPanel = new JPanel(new GridLayout(1,1));
    	sequenceGraphPanel.add(sequenceGraphPane,BorderLayout.CENTER);
    	sequenceGraphScrollPane = new JScrollPane();    	
    	sequenceViewPort.setView(sequenceGraphPane);   
    	sequenceGraphScrollPane.setViewport(sequenceViewPort);
    	sequenceGraphScrollPane.addComponentListener(this);
    	sequenceGraphScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    	sequenceGraphScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    	sequenceGraphScrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Sequence Graph"),
    			BorderFactory.createEmptyBorder(0, 5, 5, 5)));
    	sequenceGraphScrollPane.addMouseMotionListener(this);
    	sequenceGraphScrollPane.addMouseListener(this);    	
    	
    	sequenceGraphPane.setParentDimension(sequenceGraphScrollPane.getSize());
    	
    	JPanel centerPanel = new JPanel(new BorderLayout());
    	centerPanel.add(sequenceNameScrollPanePanel, BorderLayout.WEST);
    	centerPanel.add(sequenceGraphScrollPane, BorderLayout.CENTER);
		
		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.add(inputfilePanel, BorderLayout.WEST);
		JPanel featurePanel = new JPanel(new GridLayout(1,2));		
		featurePanel.add(feature1Panel);	
		northPanel.add(featurePanel, BorderLayout.CENTER);		
		
		setLayout(new BorderLayout());
		add(northPanel, BorderLayout.NORTH);
		add(centerPanel,BorderLayout.CENTER);
		
		
		this.comboboxItemArrayList = new ArrayList<AdvancedPhysioChemicalFeatureComboBoxItem>();
		this.comboboxItemArrayList.add(new AdvancedPhysioChemicalFeatureComboBoxItem("Hydrophobic", GenerateArff.aminoAcidHydrophobicity, 1.0, 0,1,1,false));
		this.comboboxItemArrayList.add(new AdvancedPhysioChemicalFeatureComboBoxItem("Hydrophilic", GenerateArff.aminoAcidHydrophobicity_neg, 1.0, 0,1,1, false));
		this.comboboxItemArrayList.add(new AdvancedPhysioChemicalFeatureComboBoxItem("Alkaline", GenerateArff.aminoAcidPKa_wrt7_pos, 0.0, 0,1,1,false));
		this.comboboxItemArrayList.add(new AdvancedPhysioChemicalFeatureComboBoxItem("Acidic", GenerateArff.aminoAcidPKa_wrt7_neg, 0.0, 0,1,1,false));
		//Comment these 2 out first because they are not very useful as the Hashtable does deduce score 
		//this.comboboxItemArrayList.add(new AdvancedPhysioChemicalFeatureComboBoxItem("OrderAA", GenerateArff.orderAminoAcid, 10.0, 10, false));
		//this.comboboxItemArrayList.add(new AdvancedPhysioChemicalFeatureComboBoxItem("DisorderAA", GenerateArff.disorderAminoAcid, 10.0, 10, false));
		this.comboboxItemArrayList.add(new AdvancedPhysioChemicalFeatureComboBoxItem("O-DisorderAA", GenerateArff.orderDifferenceAminoAcid, 3.0, 3,1,1, false));
		this.comboboxItemArrayList.add(new AdvancedPhysioChemicalFeatureComboBoxItem("D-OrderAA", GenerateArff.disorderDifferenceAminoAcid, 3.0, 3,1,1, false));
		this.comboboxItemArrayList.add(new AdvancedPhysioChemicalFeatureComboBoxItem("NetCharge(+ve)", GenerateArff.aminoAcidCharge, 2.0, 2,1,1, false));
		this.comboboxItemArrayList.add(new AdvancedPhysioChemicalFeatureComboBoxItem("NetCharge(-ve)", GenerateArff.aminoAcidCharge_neg, 2.0, 2,1,1, false));
		addItemsIntoComboBox(this.feature1ComboBox);		
		
		initToolTip();
	}
	
	private void initToolTip() {
        label = new JLabel(" ");
        label.setOpaque(true);
        label.setBackground(UIManager.getColor("ToolTip.background"));
        toolTip = new JWindow(new Frame());
        toolTip.getContentPane().add(label);               
    }
	
	private void addItemsIntoComboBox(JComboBox comboBox){		
		for(int x = 0; x < this.comboboxItemArrayList.size(); x++){
			comboBox.addItem(this.comboboxItemArrayList.get(x).getItemName());
		}
	}
	
	private void save(){
		JFileChooser fc;	    	
		//if working directory not set then look at the Sirius Settings file
		String lastLocation = SiriusSettings.getInformation("LastSequenceVisualizerFileOutputLocation: ");
		if(lastLocation == null)
			fc = new JFileChooser();
		else
			fc = new JFileChooser(lastLocation);	    	
		
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Fasta File", "fasta");
        fc.setFileFilter(filter);			        			        			    	
		int returnVal = fc.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();			           
			String savingFilename = file.getAbsolutePath();			
			if(savingFilename.indexOf(".fasta") == -1)
				savingFilename += ".fasta";		
			SiriusSettings.updateInformation("LastSequenceVisualizerFileOutputLocation: ", savingFilename);
			try{
				BufferedWriter output = new BufferedWriter(new FileWriter(savingFilename));	
				this.sequenceNameTableModel.save(output);
				output.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
		
	public void actionPerformed(ActionEvent ae){
		if(ae.getSource().equals(this.undoButton)){
			sequenceNameTableModel.undo();
		}
		else if(ae.getSource().equals(this.saveButton)){
			save();
		}else if(ae.getSource().equals(this.deleteButton)){
			int row = sequenceNameTable.getSelectedRow();
    		if(row == -1)
    			return;
    		sequenceNameTableModel.delete(row);
    		if(sequenceNameTableModel.size() > 0){
    			if(row > sequenceNameTableModel.size())
    				sequenceNameTable.setRowSelectionInterval(row+1, row+1);
    			else
    				sequenceNameTable.setRowSelectionInterval(row, row);
    		}
    			
		}else if(ae.getSource().equals(this.feature1ShowMaxCheckBox)){
			sequenceGraphPane.setShowMax(this.feature1ShowMaxCheckBox.isSelected());
			sequenceGraphPane.repaint();
		}else if(ae.getSource().equals(this.feature1AccumulativeCheckBox)){		
			calledWhenFocusLost();
		}
	}
	
	public void loadFastaFile(final File file) throws Exception{
		Thread runThread = new Thread(){
			public void run(){	
				try{								
					sequenceNameTableModel.loadFastaFile(file);		
					if(sequenceNameTableModel.getRowCount() > 0)
						sequenceNameTable.setRowSelectionInterval(0,0);
				}catch(Exception e){
			    	JOptionPane.showMessageDialog(null,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			    	e.printStackTrace();
				}	
			}
		};
		runThread.setPriority(Thread.MIN_PRIORITY);
     	runThread.start();
	}	
	
	public void itemStateChanged(ItemEvent e){		
		if(e.getSource().equals(this.feature1ComboBox)){
			if(e.getStateChange() == ItemEvent.SELECTED){			
				this.feature1ReferenceLineTextField.setText("");
				this.sequenceGraphPane.setFeature1SequenceQuencher(null, false);
				this.feature1ValueCutoffTextField.setText("");
				this.feature1LengthCutoffTextField.setText("");
				if(this.feature1ComboBox.getSelectedIndex() != 0){
					int index = this.feature1ComboBox.getSelectedIndex() - 1;
					DecimalFormat df = new DecimalFormat("0.##");
					this.feature1ValueCutoffTextField.setText(df.format(this.comboboxItemArrayList.get(index).getDefaultValueCutoff()));
					this.feature1LengthCutoffTextField.setText(df.format(this.comboboxItemArrayList.get(index).getDefaultLengthCutoff()));	
					this.feature1WindowSizeTextField.setText(df.format(this.comboboxItemArrayList.get(index).getWindowSize()));
					this.feature1StepSizeTextField.setText(df.format(this.comboboxItemArrayList.get(index).getStepSize()));
					this.feature1AccumulativeCheckBox.setSelected(true);
					if(sequenceNameTableModel.getRowCount() > 0){
						int row = sequenceNameTable.getSelectedRow();						
						SequenceQuencher sq = new SequenceQuencher(sequenceNameTableModel.getSequence(row),
		    					this.comboboxItemArrayList.get(index).getHashtable(), Double.parseDouble(feature1ValueCutoffTextField.getText()),
		    					Integer.parseInt(this.feature1LengthCutoffTextField.getText()),true, -1, 
		    					Integer.parseInt(this.feature1WindowSizeTextField.getText()), 
		    					Integer.parseInt(this.feature1StepSizeTextField.getText()), this.feature1AccumulativeCheckBox.isSelected(),
		    					this.comboboxItemArrayList.get(index).getInvertValues(), this.feature1ReferenceLineTextField);		    					
						sequenceGraphPane.setFeature1SequenceQuencher(sq, this.comboboxItemArrayList.get(index).getInvertValues());
						this.sequenceViewPort.setSequenceQuencher(sq);
						sequenceGraphPane.setPreferredSize(new Dimension((sequenceNameTableModel.getSequence(row).length()*
								sequenceGraphPane.getXAxisSpacing()) + 60, 
		    					sequenceGraphPane.getYAxisHeight() + 60));
					}
				}
			}		
		}
		sequenceViewPort.revalidate();
		sequenceGraphPane.repaint();
		sequenceViewPort.repaint();		
	}
	
	public void componentHidden(ComponentEvent e){
		//Invoked when the component has been made invisible. 
	}          
 	public void componentMoved(ComponentEvent e){
 		//Invoked when the component's position changes. 
 	}          
 	public void componentResized(ComponentEvent e){
 		//Invoked when the component's size changes. 	
 		sequenceGraphPane.setParentDimension(sequenceGraphScrollPane.getSize());
 	}          
 	public void componentShown(ComponentEvent e){
 		//Invoked when the component has been made visible. 
 	}           
 	
 	public void valueChanged(ListSelectionEvent e){ 		
    	if(e.getSource().equals(sequenceNameTable.getSelectionModel())){
    		this.feature1ReferenceLineTextField.setText("");
    		int row = sequenceNameTable.getSelectedRow();
    		if(row == -1)
    			return;
    		FastaFormat fastaFormat = new FastaFormat(sequenceNameTableModel.getHeader(row),sequenceNameTableModel.getSequence(row));
    		this.sequenceGraphPane.setSequenceDetails(fastaFormat.getSequence(), fastaFormat.getIndexLocation());
    		if(this.feature1ComboBox.getSelectedIndex() != 0){
    			int index = this.feature1ComboBox.getSelectedIndex() - 1 ;    			
    			SequenceQuencher sq = new SequenceQuencher(fastaFormat.getSequence(),
    					this.comboboxItemArrayList.get(index).getHashtable(), Double.parseDouble(feature1ValueCutoffTextField.getText()), 
    					Integer.parseInt(feature1LengthCutoffTextField.getText()),true, 
    					fastaFormat.getIndexLocation(), Integer.parseInt(this.feature1WindowSizeTextField.getText()),
    					Integer.parseInt(this.feature1StepSizeTextField.getText()),
    					this.feature1AccumulativeCheckBox.isSelected(),
    					this.comboboxItemArrayList.get(index).getInvertValues(), this.feature1ReferenceLineTextField);
    			sequenceGraphPane.setFeature1SequenceQuencher(sq,
    					this.comboboxItemArrayList.get(index).getInvertValues());
    			this.sequenceViewPort.setSequenceQuencher(sq);
    			sequenceGraphPane.setPreferredSize(new Dimension((sequenceNameTableModel.getSequence(row).length()*
    					sequenceGraphPane.getXAxisSpacing()) + 60, 
    					sequenceGraphPane.getYAxisHeight() + 60));
    		}    		
    		sequenceViewPort.revalidate();
    		sequenceGraphPane.repaint();
    		sequenceViewPort.repaint();    	
    	}
    }

	public void focusGained(FocusEvent e) {		
		
	}

	private void calledWhenFocusLost(){
		int row = sequenceNameTable.getSelectedRow();
		FastaFormat fastaFormat = new FastaFormat(sequenceNameTableModel.getHeader(row),sequenceNameTableModel.getSequence(row));
		this.sequenceGraphPane.setSequenceDetails(fastaFormat.getSequence(), fastaFormat.getIndexLocation());
		if(this.feature1ComboBox.getSelectedIndex() != 0){
			int index = this.feature1ComboBox.getSelectedIndex() - 1 ;    			
			SequenceQuencher sq = new SequenceQuencher(fastaFormat.getSequence(),
					this.comboboxItemArrayList.get(index).getHashtable(), Double.parseDouble(feature1ValueCutoffTextField.getText()), 
					Integer.parseInt(feature1LengthCutoffTextField.getText()),true, 
					fastaFormat.getIndexLocation(), 
					Integer.parseInt(this.feature1WindowSizeTextField.getText()),
					Integer.parseInt(this.feature1StepSizeTextField.getText()),
					this.feature1AccumulativeCheckBox.isSelected(),
					this.comboboxItemArrayList.get(index).getInvertValues(), this.feature1ReferenceLineTextField);
			sequenceGraphPane.setFeature1SequenceQuencher(sq,    					
					this.comboboxItemArrayList.get(index).getInvertValues());
			sequenceGraphPane.setPreferredSize(new Dimension((sequenceNameTableModel.getSequence(row).length()*
					sequenceGraphPane.getXAxisSpacing()) + 60, 
					sequenceGraphPane.getYAxisHeight() + 60));
			this.sequenceViewPort.setSequenceQuencher(sq);
			sequenceViewPort.revalidate();
			sequenceGraphPane.repaint();
			sequenceViewPort.repaint();	
		}		
	}
	
	public void focusLost(FocusEvent e) {		
		if(e.getSource().equals(this.feature1ValueCutoffTextField) || e.getSource().equals(this.feature1LengthCutoffTextField) ||
				e.getSource().equals(this.feature1WindowSizeTextField) || e.getSource().equals(this.feature1StepSizeTextField)){
			calledWhenFocusLost();
		}else if(e.getSource().equals(this.feature1ReferenceLineTextField)){
			//do nothing.. only need to refresh will show the results
			sequenceViewPort.revalidate();
			sequenceGraphPane.repaint();
			sequenceViewPort.repaint();	
		}		
	}

	
	public void mouseDragged(MouseEvent arg0) {		
		
	}

	
	public void mouseMoved(MouseEvent me) {	
		if(me.getSource().equals(this.sequenceGraphScrollPane)){			
			Point p = me.getPoint();	 		
	 		Point convertedPoint = SwingUtilities.convertPoint(this.sequenceGraphScrollPane, p, this.sequenceGraphPane);	 		
	 		DecimalFormat df = new DecimalFormat("0.####");
	 		if(this.sequenceGraphPane.getFeature1SequenceQuencher() ==  null)
	 			return;
	 		double globalViewMaxValue = this.sequenceGraphPane.getFeature1SequenceQuencher().getGlobalViewMaxValue();
	    	double globalViewMinValue = this.sequenceGraphPane.getFeature1SequenceQuencher().getGlobalViewMinValue();
	    	double globalViewRange = globalViewMaxValue - globalViewMinValue; 
	 		
	    	int range = 3;
	    	boolean found = false;
	 		for(int x = 0; x < this.sequenceGraphPane.getFeature1SequenceQuencher().getGlobalViewSize(); x += 1){
	    		double yValue = ((this.sequenceGraphPane.getFeature1SequenceQuencher().getGobalView(x) - globalViewMinValue) / globalViewRange);	    		
	    		int xPoint = (((x*this.sequenceGraphPane.getFeature1SequenceQuencher().getStepSize())+1)*sequenceGraphPane.getXAxisSpacing()) + 
	    			this.sequenceGraphPane.getTopLeftX() - 2;	    		
	    		int yPoint = (int)(((this.sequenceGraphPane.getYAxisHeight()+this.sequenceGraphPane.getTopLeftY()) * (1 - (yValue))));	    	    		
	    		
	    		//can add a difference detector to increase runtime
	    		if(xPoint - range <= convertedPoint.x && xPoint + range >= convertedPoint.x && 
	    				yPoint - range <= convertedPoint.y && yPoint + range >= convertedPoint.y){
	    			label.setText("" + ((x+1)*this.sequenceGraphPane.getFeature1SequenceQuencher().getStepSize()) + ", " + 
	    					df.format((this.sequenceGraphPane.getFeature1SequenceQuencher().getGobalView(x))));
			 		int labelWidth = (int)label.getSize().getWidth();
			        toolTip.pack();			        			        
			        found = true;
			        SwingUtilities.convertPointToScreen(p, this.sequenceGraphScrollPane);
			        if(labelWidth + p.x + 20 < this.sequenceGraphScrollPane.getSize().getWidth())
			        	toolTip.setLocation(p.x + 10, p.y - 20);
			        else
			        	toolTip.setLocation(p.x - 10 - labelWidth, p.y - 20);
			        break;
	    		}
	    	}    	 		
	 		toolTip.setVisible(found);		
		}
		
	}

	
	public void mouseClicked(MouseEvent me) {	
		if(me.getSource().equals(this.inputfileTextField)){
			//applicationData.hasLocationIndexBeenSet = false;
			JFileChooser fc;	    	
    		//if working directory not set then look at the Sirius Settings file
    		String lastLocation = SiriusSettings.getInformation("LastSeqeunceVisualizerFastaFileLocation: ");
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
	            SiriusSettings.updateInformation("LastSeqeunceVisualizerFastaFileLocation: ", file.getAbsolutePath());
	            inputfileTextField.setText(file.getAbsolutePath());
	            try{
	            	sequenceNameTable.clearSelection();
	            	loadFastaFile(file);	            	
	            }catch(Exception e){
	            	JOptionPane.showMessageDialog(null,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
	            	e.printStackTrace();
	            } 	            
			}			
		}
		else if(me.getSource().equals(this.sequenceGraphScrollPane) && me.getButton() == MouseEvent.BUTTON1 && 
				me.isControlDown() == false){
			this.sequenceGraphPane.zoomIn();
		}
		else if(me.getSource().equals(this.sequenceGraphScrollPane) && me.getButton() == MouseEvent.BUTTON1 &&
				me.isControlDown() == true){
			this.sequenceGraphPane.zoomOut();
		}				
		int row = sequenceNameTable.getSelectedRow();
		if(row != -1)
			sequenceGraphPane.setPreferredSize(new Dimension
				((sequenceNameTableModel.getSequence(row).length()*sequenceGraphPane.getXAxisSpacing()) + 60, 
				sequenceGraphPane.getYAxisHeight() + 60));
		sequenceViewPort.revalidate();
		sequenceGraphPane.repaint();
		sequenceViewPort.repaint();			
	}
	public void mouseEntered(MouseEvent arg0) {}
	
	public void mouseExited(MouseEvent arg0) {}
	
	public void mousePressed(MouseEvent arg0) {}
	
	public void mouseReleased(MouseEvent arg0) {}
}

class SequenceViewPort extends JViewport{
	static final long serialVersionUID = sirius.Sirius.version;
	SequenceGraphPane sgp;	
	SequenceQuencher sq;
	int sequenceLength;
	
	public SequenceViewPort(SequenceGraphPane sgp){
		this.sgp = sgp;
	}
	
	public void setSequenceQuencher(SequenceQuencher sq){
		this.sq = sq;		
		repaint();
	}
	
    public void paintChildren(Graphics g){
        super.paintChildren(g);
        /*DecimalFormat df = new DecimalFormat("0.##");
        
        final int topLeftX = sgp.getTopLeftX();
		final int topLeftY = sgp.getTopLeftY();
		final Dimension parentDimension = sgp.getParentDimension();
		int yAxisHeight = (int)parentDimension.getHeight() - 88;
		yAxisHeight -= yAxisHeight%10;
		//draw y-axis
		g.drawLine(topLeftX,topLeftY,topLeftX,topLeftY+yAxisHeight);	
		
		//Draw markers for y-axis
		if(this.sq != null){
			g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,9));
			g.setColor(Color.RED);
			g.drawString(df.format(this.sq.getGlobalViewMaxValue()), topLeftX - 15, topLeftY + 3);		
			g.drawString(df.format(this.sq.getGlobalViewMinValue()), topLeftX - 10, topLeftY + 10 + yAxisHeight);		
			
			double globalViewMaxValue = sq.getGlobalViewMaxValue();
	    	double globalViewMinValue = sq.getGlobalViewMinValue();
	    	double globalViewRange = globalViewMaxValue - globalViewMinValue; 
	    	double midPointValue = ((Double.parseDouble(sq.getRefLine())) - globalViewMinValue) / globalViewRange;
	    	g.drawString(sq.getRefLine(), topLeftX,(int)((yAxisHeight+topLeftY) * (1 - (midPointValue))) - 2);
		}*/
    }

    public Color getBackground(){
        Component c = getView();
        return c == null ? super.getBackground() : c.getBackground();
    }        		   
}

