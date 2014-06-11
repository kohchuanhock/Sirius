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
package sirius.trainer.features.gui.kgram;


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import sirius.main.ApplicationData;
import sirius.trainer.features.KGramFeature;
import sirius.trainer.features.Feature;
import sirius.trainer.main.StatusPane;
import sirius.trainer.step2.FeatureTableModel;
import sirius.trainer.step2.Physiochemical2;

class KgramAutoPane extends JComponent implements ActionListener, ItemListener{
	static final long serialVersionUID = 23122007;
	
	private JButton okButton;
	private JButton cancelButton;
	private JDialog parent;
	
	private JTextField kgramField;
	private JTextField xmistakeField;
	private JTextField windowSizeFromField;
	private JTextField windowSizeToField;
	
	private JCheckBox aCheckBox;	
	private JCheckBox cCheckBox;
	private JCheckBox tCheckBox;
	private JCheckBox gCheckBox;	
	private JCheckBox xCheckBox;
	
	private ArrayList<JCheckBox> proteinCheckBoxArrayList;
	
	private FeatureTableModel model;
	private StatusPane statusPane;
	private ApplicationData applicationData;
	
	private JRadioButton absoluteButton = new JRadioButton("Absolute");;
	private JRadioButton relativeButton = new JRadioButton("Relative",true);;
	
	private JComboBox codingNamesComboBox;
	
	private JPanel centerPanel;
	private JPanel definitionsPanel;
	private JPanel center_east;
	
	private Physiochemical2 p2;
	private JCheckBox isPercentageCheckBox = new JCheckBox("%",true);	
	
	public KgramAutoPane(JDialog parent,FeatureTableModel model,ApplicationData applicationData){
		this.parent = parent;
		this.model = model;
		this.statusPane = applicationData.getStatusPane();
		this.applicationData = applicationData;
		
		setLayout(new BorderLayout());
		//setTitle("K-gram with X mistakes");				
		
		//South
		okButton = new JButton("OK");
		cancelButton = new JButton("Cancel");
		JPanel southPanel = new JPanel(new FlowLayout());
		southPanel.add(okButton);
		southPanel.add(cancelButton);
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		add(southPanel,BorderLayout.SOUTH);
		
		//Center_North
		JPanel center_north = new JPanel(new FlowLayout());	
		if(applicationData.isLocationIndexMinusOne == false)		
			center_north.setBorder(BorderFactory.createTitledBorder(
				"Window Size and Location (Relative to +1 Index)"));
		else
			center_north.setBorder(BorderFactory.createTitledBorder(
				"Window Size and Location"));
		
		JLabel windowSizeFromLabel = new JLabel("From ");
		windowSizeFromField = new JTextField(5);				
		JLabel windowSizeToLabel = new JLabel(" To ");
		windowSizeToField = new JTextField(5);			
		
		center_north.add(windowSizeFromLabel);
		center_north.add(windowSizeFromField);
		center_north.add(windowSizeToLabel);
		center_north.add(windowSizeToField);		
		center_north.add(this.isPercentageCheckBox);
		
		JPanel codingNamePanel = new JPanel();
		codingNamePanel.setBorder(BorderFactory.createTitledBorder("Coding Schemes"));
		this.codingNamesComboBox = new JComboBox();
		this.codingNamesComboBox.addItemListener(this);		
		for(int x = 0; x < Physiochemical2.codingNameList.length; x++)
			this.codingNamesComboBox.addItem(Physiochemical2.codingNameList[x]);
		codingNamePanel.add(this.codingNamesComboBox);
		
		//center_south
		JPanel center_south = new JPanel(new GridLayout(2,1));
		center_south.setBorder(BorderFactory.createTitledBorder("K-gram with X-mistakes allowed"));
		
		JPanel kgramPanel = new JPanel(new FlowLayout());
		JLabel kgramLabel = new JLabel("K = ");
		kgramField = new JTextField(5);
		kgramPanel.add(kgramLabel);
		kgramPanel.add(kgramField);
		center_south.add(kgramPanel);
		
		JPanel xmistakePanel = new JPanel(new FlowLayout());
		JLabel xmistakeLabel = new JLabel("X = ");
		xmistakeField = new JTextField(5);
		xmistakePanel.add(xmistakeLabel);
		xmistakePanel.add(xmistakeField);
		center_south.add(xmistakePanel);
		
		JPanel absoluteRelativePanel = new JPanel(new GridLayout(2,1));
		absoluteRelativePanel.setBorder(BorderFactory.createTitledBorder("Value"));
				
		absoluteButton.addActionListener(this);		
		relativeButton.addActionListener(this);
		absoluteRelativePanel.add(absoluteButton);		
		absoluteRelativePanel.add(relativeButton);
		
		JPanel center_center = new JPanel(new BorderLayout());
		center_center.add(absoluteRelativePanel,BorderLayout.EAST);		
		center_center.add(center_south,BorderLayout.CENTER);
		
		//center_east 
		center_east = null; 
		if(applicationData.getSequenceType().indexOf("DNA") != -1){		
			//setSize(300,220);		
			center_east = new JPanel(new GridLayout(5,1));
			center_east.setBorder(BorderFactory.createTitledBorder("Include"));
		
			aCheckBox = new JCheckBox("A",true);
			cCheckBox = new JCheckBox("C",true);
			tCheckBox = new JCheckBox("T",true);
			gCheckBox = new JCheckBox("G",true);
			xCheckBox = new JCheckBox("X = A,C,T or G");
			
			center_east.add(aCheckBox);
			center_east.add(cCheckBox);
			center_east.add(tCheckBox);
			center_east.add(gCheckBox);
			center_east.add(xCheckBox);
		}else if(applicationData.getSequenceType().indexOf("PROTEIN") != -1){
			//setSize(500,220);		
			center_east = new JPanel(new GridLayout(8,3));
			center_east.setBorder(BorderFactory.createTitledBorder("Includes"));
			
			this.proteinCheckBoxArrayList = new ArrayList<JCheckBox>();			
			p2 = new Physiochemical2("Original");
			p2.setCheckBox(center_east, proteinCheckBoxArrayList);			
		}
		
		JPanel northPanel = new JPanel(new BorderLayout());
		if(applicationData.getSequenceType().indexOf("PROTEIN") != -1)
			northPanel.add(codingNamePanel,BorderLayout.WEST);
		northPanel.add(center_north,BorderLayout.CENTER);
		
		//Center
		centerPanel = new JPanel(new BorderLayout());		
		centerPanel.add(northPanel,BorderLayout.NORTH);
		centerPanel.add(center_center,BorderLayout.CENTER);	
		add(centerPanel,BorderLayout.CENTER);
		add(center_east,BorderLayout.EAST);
		parent.pack();		
		}	
	public void actionPerformed(ActionEvent ae){
		if(ae.getSource().equals(okButton)){			
			//To be implement
			try{				
				int fromFieldInt = validateField(windowSizeFromField,"From Field");
				int toFieldInt = validateField(windowSizeToField,"To Field");
				int kgramFieldInt = validateField(kgramField,"K-gram Field");
				int xmistakeFieldInt = validateField(xmistakeField,"X-mistake Field");
				int windowSize = 0;
				if((toFieldInt >= 0 && fromFieldInt >= 0) || (toFieldInt <= 0 && fromFieldInt <= 0))
					windowSize = toFieldInt - fromFieldInt +1;
				else
					windowSize = toFieldInt - fromFieldInt;
				if(toFieldInt < fromFieldInt){
					JOptionPane.showMessageDialog(parent,"From field should not be smaller than To Field",
						"ERROR",JOptionPane.ERROR_MESSAGE);
					windowSizeFromField.requestFocusInWindow();
					
				}
				else if(fromFieldInt == 0 && applicationData.isLocationIndexMinusOne == false){
					JOptionPane.showMessageDialog(parent,"0 is not allowed, choose either -1 or +1.","ERROR",
						JOptionPane.ERROR_MESSAGE);
					windowSizeFromField.requestFocusInWindow();
				}
				else if(fromFieldInt < 0 && applicationData.isLocationIndexMinusOne == true){
					JOptionPane.showMessageDialog(parent,"Cannot be < 0","ERROR",
						JOptionPane.ERROR_MESSAGE);
					windowSizeFromField.requestFocusInWindow();
				}
				else if(toFieldInt == 0 && applicationData.isLocationIndexMinusOne == false){
					JOptionPane.showMessageDialog(parent,"0 is not allowed, choose either -1 or +1.","ERROR",
						JOptionPane.ERROR_MESSAGE);
					windowSizeToField.requestFocusInWindow();
				}					
				else if(xmistakeFieldInt < 0){
					JOptionPane.showMessageDialog(parent,"X has to be greater or equal to 0","ERROR",
						JOptionPane.ERROR_MESSAGE);
					xmistakeField.requestFocusInWindow();
				}					
				else if(kgramFieldInt < 1){
					JOptionPane.showMessageDialog(parent,"K has to be greater or equal to 1","ERROR",
						JOptionPane.ERROR_MESSAGE);
				}
				else if(windowSize < kgramFieldInt){
					JOptionPane.showMessageDialog(parent,"Window Size is less than k-gram","ERROR",
							JOptionPane.ERROR_MESSAGE);
				}	
				else{	
					if(model != null){
						//Generate the features
						generateKgram(kgramFieldInt,fromFieldInt,toFieldInt,xmistakeFieldInt);
						//parent.dispose();
					}else{
						//this is called by NNSearch add constraint button
						//But this should not be called as it will be quite messy if alot of constraints are added at the same time
						//Hence does not make sense to do this method
					}					
				}				
			}
			catch(NumberFormatException e){}			
		}
		else if(ae.getSource().equals(cancelButton)){
			parent.dispose();
		}
		else if(ae.getSource().equals(absoluteButton)){
			absoluteButton.setSelected(true);
			relativeButton.setSelected(false);
		}
		else if(ae.getSource().equals(relativeButton)){
			absoluteButton.setSelected(false);
			relativeButton.setSelected(true);
		}
	}
	private int validateField(JTextField textField,String name) throws NumberFormatException{
		try{
			return Integer.parseInt(textField.getText());
		}
		catch(NumberFormatException e){
			JOptionPane.showMessageDialog(parent,"Input only numbers into " + name,"ERROR",
				JOptionPane.ERROR_MESSAGE);
   			textField.requestFocusInWindow();
   			throw new NumberFormatException();
		}
	}
	private void generateKgram(final int kgram, final int windowFrom, final int windowTo,
		final int xmistake){
		if(applicationData.getOneThread() == null){
			statusPane.setText("Generating " + kgram + "-gram...");
			applicationData.setOneThread(new Thread(){
				public void run(){		
					int totalChecked = 0;
					String[] symbol;
					if(applicationData.getSequenceType().indexOf("DNA") != -1){						
						if(aCheckBox.isSelected()) totalChecked++;
						if(cCheckBox.isSelected()) totalChecked++;
						if(tCheckBox.isSelected()) totalChecked++;
						if(gCheckBox.isSelected()) totalChecked++;
						if(xCheckBox.isSelected()) totalChecked++;
						
						symbol = new String[totalChecked];
						int tempIndex = 0;
						if(aCheckBox.isSelected()){
							symbol[tempIndex] = "A";
							tempIndex++;
						}						
						if(cCheckBox.isSelected()){
							symbol[tempIndex] = "C";
							tempIndex++;
						}						
						if(tCheckBox.isSelected()){
							symbol[tempIndex] = "T";
							tempIndex++;
						}						
						if(gCheckBox.isSelected()){
							symbol[tempIndex] = "G";
							tempIndex++;
						}						
						if(xCheckBox.isSelected()){
							symbol[tempIndex] = "X";
							tempIndex++;
						}						
					}else{//protein						
						for(int x = 0; x < proteinCheckBoxArrayList.size(); x++){
							if(proteinCheckBoxArrayList.get(x).isSelected())
								totalChecked++;							
						}													
						symbol = new String[totalChecked];
						int tempIndex = 0;
						for(int x = 0; x < proteinCheckBoxArrayList.size(); x++){
							if(proteinCheckBoxArrayList.get(x).isSelected()){
								symbol[tempIndex] = p2.getLetter(x) + "";
								tempIndex++;
							}												
						}																		
					}						
					//Generate All possible permutations					
					for(int x = 0; x < (int)Math.pow(totalChecked,kgram); x++){
						int tempKgram = kgram;
						int y = x;
						String tempString = "";
						while((tempKgram--)>0){
							tempString += symbol[y%totalChecked];							
							y = y/totalChecked;
						}
						//invert the string for ease of checking purpose
						String tempFeature = "";
						for(int z = tempString.length()-1; z >= 0; z--)
							tempFeature += tempString.charAt(z);	
						if(tempFeature.charAt(0) == 'X')
							continue;
						if(tempFeature.charAt(tempFeature.length() - 1) == 'X')
							continue;
						Feature tempData;
						if(codingNamesComboBox.getSelectedIndex() == 0){
							if(absoluteButton.isSelected() == true)
								tempData = new KGramFeature('K',tempFeature,xmistake,windowFrom,windowTo,isPercentageCheckBox.isSelected());
							else						
								tempData = new KGramFeature('L',tempFeature,xmistake,windowFrom,windowTo,isPercentageCheckBox.isSelected());
						}else{
							if(absoluteButton.isSelected() == true)
								tempData = new KGramFeature('D',tempFeature,xmistake,windowFrom,windowTo,
										codingNamesComboBox.getSelectedIndex(),isPercentageCheckBox.isSelected());
							else						
								tempData = new KGramFeature('E',tempFeature,xmistake,windowFrom,windowTo,
										codingNamesComboBox.getSelectedIndex(),isPercentageCheckBox.isSelected());
						}
						model.add(tempData);
						tempString = "";						
					}
					statusPane.setText("Generating " + kgram + "-gram...DONE!");
					JOptionPane.showMessageDialog(parent,"New Features Added!","Information",JOptionPane.INFORMATION_MESSAGE);
					applicationData.setOneThread(null);
				}
			});
			applicationData.getOneThread().setPriority(Thread.MIN_PRIORITY);
			applicationData.getOneThread().start();				
		}else{
			JOptionPane.showMessageDialog(parent,"Still generating...\nPlease try again later...",
				"Already Generating",JOptionPane.WARNING_MESSAGE);
		}
	}
	
	 public String checkEnds(String feature){
	    	while(feature.charAt(0) == 'X')
				feature = feature.substring(1);
			while(feature.charAt(feature.length() - 1) == 'X')
				feature = feature.substring(0, feature.length() - 1);
			return feature;
	    }
	
	public void itemStateChanged(ItemEvent ie) {
		if(ie.getSource().equals(this.codingNamesComboBox)){			
			if(this.definitionsPanel != null)
				this.centerPanel.remove(this.definitionsPanel);		
			if(this.codingNamesComboBox.getSelectedIndex() != 0){
				//parent.setSize(800,400);
				this.definitionsPanel = new JPanel(new GridLayout(4,2));
				this.definitionsPanel.setBorder(BorderFactory.createTitledBorder("Definitions"));
				p2 = new Physiochemical2((String) this.codingNamesComboBox.getSelectedItem());
				p2.setPanel(this.definitionsPanel);
				this.centerPanel.add(this.definitionsPanel, BorderLayout.SOUTH);
				center_east.removeAll();
				this.proteinCheckBoxArrayList = new ArrayList<JCheckBox>();			
				p2 = new Physiochemical2((String) this.codingNamesComboBox.getSelectedItem());
				p2.setCheckBox(center_east, proteinCheckBoxArrayList);	
			}else{		
				if(center_east != null)
					center_east.removeAll();				
				this.proteinCheckBoxArrayList = new ArrayList<JCheckBox>();			
				p2 = new Physiochemical2("Original");
				if(center_east != null)
					p2.setCheckBox(center_east, proteinCheckBoxArrayList);	
				//parent.setSize(680,300);
			}	
			parent.pack();
			revalidate();
			repaint();
		}
	}
}