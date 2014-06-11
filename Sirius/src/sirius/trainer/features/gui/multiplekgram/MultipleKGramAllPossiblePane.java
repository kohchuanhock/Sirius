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
package sirius.trainer.features.gui.multiplekgram;


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import sirius.main.ApplicationData;
import sirius.trainer.features.MultipleKGramFeature;
import sirius.trainer.features.Feature;
import sirius.trainer.main.StatusPane;
import sirius.trainer.step2.FeatureTableModel;
import sirius.trainer.step2.Physiochemical2;

public class MultipleKGramAllPossiblePane extends JComponent implements ActionListener, ItemListener{
	
	static final long serialVersionUID = sirius.Sirius.version; 
	private JButton okButton;
	private JButton cancelButton;
	private JDialog parent;
	
	private ArrayList<JTextField> kFieldArrayList;
	private ArrayList<JTextField> xFieldArrayList;
	private ArrayList<JTextField> yFieldArrayList;
	private ArrayList<JTextField> zFieldArrayList;
	
	private JTextField windowSizeFromField;
	private JTextField windowSizeToField;
	
	private JCheckBox aCheckBox;	
	private JCheckBox cCheckBox;
	private JCheckBox tCheckBox;	
	private JCheckBox gCheckBox;
	private JCheckBox xCheckBox;
	
	private FeatureTableModel model;
	private StatusPane statusPane;
	private ApplicationData applicationData;
		
	private JButton addButton;
	private JButton deleteButton;
	
	private GridBagConstraints c;
	private JPanel fieldsPanel;
	
	private JRadioButton absoluteButton = new JRadioButton("Absolute");
	private JRadioButton relativeButton = new JRadioButton("Relative",true);
	
	private JComboBox codingNamesComboBox;
	
	private JPanel centerPanel;
	private JPanel definitionsPanel;
	private JPanel center_east;
	
	private Physiochemical2 p2;
	
	private ArrayList<JCheckBox> proteinCheckBoxArrayList;
	private JCheckBox isPercentageCheckBox = new JCheckBox("%");
		
	public MultipleKGramAllPossiblePane(JDialog parent,FeatureTableModel model,ApplicationData applicationData){
		this.parent = parent;
		this.model = model;
		this.statusPane = applicationData.getStatusPane();
		this.applicationData = applicationData;
		
		setLayout(new BorderLayout());
		
		JPanel codingNamePanel = new JPanel();
		codingNamePanel.setBorder(BorderFactory.createTitledBorder("Coding Schemes"));
		this.codingNamesComboBox = new JComboBox();
		this.codingNamesComboBox.addItemListener(this);		
		for(int x = 0; x < Physiochemical2.codingNameList.length; x++)
			this.codingNamesComboBox.addItem(Physiochemical2.codingNameList[x]);
		codingNamePanel.add(this.codingNamesComboBox);
		
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
		
		JPanel northPanel = new JPanel(new BorderLayout());
		if(applicationData.getSequenceType().indexOf("PROTEIN") != -1)
			northPanel.add(codingNamePanel,BorderLayout.WEST);
		northPanel.add(center_north,BorderLayout.CENTER);
		
		//center_south	
		JPanel center_south = new JPanel(new BorderLayout());
		center_south.setBorder(BorderFactory.createTitledBorder(
			"Multiple K-gram with X-mistakes, min Y and max Z gaps"));
			
		JPanel buttonsPanel = new JPanel(new GridLayout(1,2));
		addButton = new JButton("Add");
		addButton.addActionListener(this);
		deleteButton = new JButton("Delete");
		deleteButton.addActionListener(this);
		buttonsPanel.add(addButton);		
		buttonsPanel.add(deleteButton);
		center_south.add(buttonsPanel,BorderLayout.SOUTH);

		kFieldArrayList = new ArrayList<JTextField>();		
		xFieldArrayList = new ArrayList<JTextField>();		
		yFieldArrayList = new ArrayList<JTextField>();
		zFieldArrayList = new ArrayList<JTextField>();
		fieldsPanel = new JPanel(new GridBagLayout());
		c = new GridBagConstraints();
		
		JPanel panel1 = new JPanel();	
		JLabel kgramLabel1 = new JLabel("K = ");
		JTextField kgramField1 = new JTextField(5);
		JLabel xmistakeLabel1 = new JLabel("X = ");
		JTextField xmistakeField1 = new JTextField(5);
		panel1.add(kgramLabel1);
		panel1.add(kgramField1);
		panel1.add(xmistakeLabel1);
		panel1.add(xmistakeField1);
		kFieldArrayList.add(kgramField1);
		xFieldArrayList.add(xmistakeField1);
									
		JPanel panel2 = new JPanel();		
		JLabel kgramLabel2 = new JLabel("K = ");
		JTextField kgramField2 = new JTextField(5);
		JLabel xmistakeLabel2 = new JLabel("X = ");
		JTextField xmistakeField2 = new JTextField(5);
		panel2.add(kgramLabel2);
		panel2.add(kgramField2);
		panel2.add(xmistakeLabel2);
		panel2.add(xmistakeField2);
		kFieldArrayList.add(kgramField2);
		xFieldArrayList.add(xmistakeField2);						
					
		JPanel panel3 = new JPanel();
		JLabel minYLabel1 = new JLabel("Y = ");
		JTextField minYField1 = new JTextField(5);
		JLabel maxZLabel1 = new JLabel("Z = ");
		JTextField maxZField1 = new JTextField(5);
		panel3.add(minYLabel1);
		panel3.add(minYField1);
		panel3.add(maxZLabel1);
		panel3.add(maxZField1);
		yFieldArrayList.add(minYField1);
		zFieldArrayList.add(maxZField1);				
		
		c.weighty = 0.0;                //reset to the default
        c.gridwidth = 1; //end row
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;  
		fieldsPanel.add(panel1,c);
		c.gridwidth = 1;
        c.gridheight = 2;
        c.weighty = 1.0;
        c.gridx = 1;
        c.gridy = 0;
		fieldsPanel.add(panel3,c);
		c.weighty = 0.0;                //reset to the default
        c.gridwidth = 1; 
        c.gridheight = 1;  
        c.gridx = 0;
        c.gridy = 1;   
		fieldsPanel.add(panel2,c);		
		JScrollPane scrollPane = new JScrollPane(fieldsPanel);
		center_south.add(scrollPane,BorderLayout.CENTER);
		
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
		if(applicationData.getSequenceType().indexOf("DNA") != -1){		
			//setSize(700,220);		
			center_east = new JPanel(new GridLayout(5,1));
			center_east.setBorder(BorderFactory.createTitledBorder("Includes"));
		
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
			//setSize(700,220);		
			center_east = new JPanel(new GridLayout(8,3));
			center_east.setBorder(BorderFactory.createTitledBorder("Includes"));					
			
			this.proteinCheckBoxArrayList = new ArrayList<JCheckBox>();			
			p2 = new Physiochemical2("Original");
			p2.setCheckBox(center_east, proteinCheckBoxArrayList);	
		}
		
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
			try{				
				int fromFieldInt = validateField(windowSizeFromField,"From Field");
				int toFieldInt = validateField(windowSizeToField,"To Field");				
				int[] kFieldInt = validateField(kFieldArrayList,"K-gram Field");
				int[] xFieldInt = validateField(xFieldArrayList,"X-mistake Field");
				int[] yFieldInt = validateField(yFieldArrayList,"Y-minGap Field");
				int[] zFieldInt = validateField(zFieldArrayList,"Z-maxGap Field");
				compareMinMaxGap(yFieldArrayList,zFieldArrayList);			
				int largestKFieldInt = getLargest(kFieldInt);
				int smallestXFieldInt = getSmallest(xFieldInt);
				int smallestYFieldInt = getSmallest(yFieldInt);
				int smallestZFieldInt = getSmallest(zFieldInt);
				int windowSize = 0;
				if((toFieldInt >= 0 && fromFieldInt >= 0) || (toFieldInt <= 0 && fromFieldInt <= 0))
					windowSize = toFieldInt - fromFieldInt +1;
				else
					windowSize = toFieldInt - fromFieldInt;
				int minWindowSizeNeeded = 0;
				for(int x = 0; x < xFieldInt.length; x++){
					minWindowSizeNeeded += kFieldInt[x];
					if(x+1 != xFieldInt.length)
						minWindowSizeNeeded += yFieldInt[x];	
				}								
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
				else if(smallestXFieldInt < 0){
					JOptionPane.showMessageDialog(parent,"X has to be greater or equal to 0","ERROR",
						JOptionPane.ERROR_MESSAGE);
				}					
				else if(largestKFieldInt < 1){
					JOptionPane.showMessageDialog(parent,"K has to be greater or equal to 1","ERROR",
						JOptionPane.ERROR_MESSAGE);
				}
				else if(smallestYFieldInt < 0){
					JOptionPane.showMessageDialog(parent,"Y has to be greater or equal to 0","ERROR",
						JOptionPane.ERROR_MESSAGE);
				}
				else if(smallestZFieldInt < 0){
					JOptionPane.showMessageDialog(parent,"Z has to be greater or equal to 0","ERROR",
						JOptionPane.ERROR_MESSAGE);
				}
				else if(windowSize < minWindowSizeNeeded){
					JOptionPane.showMessageDialog(parent,"Window size is " + windowSize + " but minimum window size needed is " + 
							minWindowSizeNeeded + ". Please adjust.","ERROR",JOptionPane.ERROR_MESSAGE);					
				}
				else{
					if(model != null){
						//Generate the features
						generateMultipleKgram(kFieldInt,fromFieldInt,toFieldInt,xFieldInt,yFieldInt,zFieldInt);
						JOptionPane.showMessageDialog(parent,"New Features Added!","Information",JOptionPane.INFORMATION_MESSAGE);
						//parent.dispose();
					}else{
						
					}
				}				
			}
			catch(Exception e){e.printStackTrace();}			
		}else if(ae.getSource().equals(cancelButton)){
			parent.dispose();
		}else if(ae.getSource().equals(addButton)){						
			JPanel panel1 = new JPanel();	
			JLabel kgramLabel1 = new JLabel("K = ");
			JTextField kgramField1 = new JTextField(5);
			JLabel xmistakeLabel1 = new JLabel("X = ");
			JTextField xmistakeField1 = new JTextField(5);
			panel1.add(kgramLabel1);
			panel1.add(kgramField1);
			panel1.add(xmistakeLabel1);
			panel1.add(xmistakeField1);
			kFieldArrayList.add(kgramField1);
			xFieldArrayList.add(xmistakeField1);
			
			JPanel panel3 = new JPanel();
			JLabel minYLabel1 = new JLabel("Y = ");
			JTextField minYField1 = new JTextField(5);
			JLabel maxZLabel1 = new JLabel("Z = ");
			JTextField maxZField1 = new JTextField(5);
			panel3.add(minYLabel1);
			panel3.add(minYField1);
			panel3.add(maxZLabel1);
			panel3.add(maxZField1);
			yFieldArrayList.add(minYField1);
			zFieldArrayList.add(maxZField1);
			
			c.weighty = 0.0;                //reset to the default
	        c.gridwidth = 1; 
	        c.gridheight = 1;  
	        c.gridx = 0;
	        c.gridy = xFieldArrayList.size() - 1;   
			fieldsPanel.add(panel1,c);
			c.weighty = 0.0;                //reset to the default
	        c.gridwidth = 1; 
	        c.gridheight = 2;  
	        c.gridx = 1;
	        c.gridy = yFieldArrayList.size() - 1; 
			fieldsPanel.add(panel3,c);	
			repaint();
			
		}else if(ae.getSource().equals(deleteButton)){
			int count = fieldsPanel.getComponentCount();
			if(count == 3){
				JOptionPane.showMessageDialog(parent,"Reached Minimum. Cannot delete anymore.","Error",
					JOptionPane.ERROR_MESSAGE);
			}else{
				fieldsPanel.remove(count - 1);
				fieldsPanel.remove(count - 2);
				kFieldArrayList.remove(kFieldArrayList.size() - 1);
				xFieldArrayList.remove(xFieldArrayList.size() - 1);
				yFieldArrayList.remove(yFieldArrayList.size() - 1);
				zFieldArrayList.remove(zFieldArrayList.size() - 1);
				repaint();
			}			
		}else if(ae.getSource().equals(absoluteButton)){
			absoluteButton.setSelected(true);
			relativeButton.setSelected(false);
		}
		else if(ae.getSource().equals(relativeButton)){
			absoluteButton.setSelected(false);
			relativeButton.setSelected(true);
		}
	}
	private void compareMinMaxGap(ArrayList<JTextField> minGapList,ArrayList<JTextField> maxGapList) 
		throws Exception{
		for(int x = 0; x < minGapList.size(); x++){
			int minGap = Integer.parseInt(minGapList.get(x).getText());
			int maxGap = Integer.parseInt(maxGapList.get(x).getText());
			if(minGap > maxGap){
				JOptionPane.showMessageDialog(parent,"min gap cannot be greater than max gap","ERROR",
					JOptionPane.ERROR_MESSAGE);
				minGapList.get(x).requestFocusInWindow();
				throw new Exception();				
			}				
		}
	}
	private int getLargest(int[] array){
		int returnValue = array[0];
		for(int x = 1; x < array.length; x++){
			if(array[x] > returnValue)
				returnValue = array[x];
		}
		return returnValue;
	}
	private int getSmallest(int[] array){
		int returnValue = array[0];
		for(int x = 1; x < array.length; x++){
			if(array[x] < returnValue)
				returnValue = array[x];
		}
		return returnValue;
	}
	private int[] validateField(ArrayList<JTextField> arrayList, String name) throws NumberFormatException{		
		int[] returnValue = new int[arrayList.size()];
		for(int x = 0; x < arrayList.size(); x++){
			returnValue[x] = validateField(arrayList.get(x),name);			
		}
		return returnValue;			
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
	private void generateMultipleKgram(final int[] kgram, final int windowFrom, final int windowTo,
		final int[] xmistake,final int[] minGap,final int[] maxGap){		
		if(applicationData.getOneThread() == null){
			statusPane.setText("Generating Multiple K-gram...");
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
					//Idea: first combine the string and generate all possible permuatation then chop it down
					//into an array of String
					
					//find out the combineLength
					int combineLength = 0;
					for(int x = 0; x < kgram.length; x++){
						combineLength += kgram[x];
					}
					//Generate all possible based on combinedLength
					for(int x = 0; x < (int)Math.pow(totalChecked,combineLength); x++){
						int tempKgram = combineLength;
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
						tempString = tempFeature;
						//chop them down
						String[] kgramString = new String[kgram.length];
						int tempInt = 0;
						for(int z = 0; z < kgram.length; z++){
							kgramString[z] = tempString.substring(tempInt,tempInt + kgram[z]);
							tempInt += kgram[z];
						}
						Feature tempData;
						if(codingNamesComboBox.getSelectedIndex() == 0){//Normal multiple kgram
							if(absoluteButton.isSelected() == true)
								tempData = new MultipleKGramFeature('M',kgramString,xmistake,minGap,maxGap,
									windowFrom,windowTo,isPercentageCheckBox.isSelected());
							else
								tempData = new MultipleKGramFeature('N',kgramString,xmistake,minGap,maxGap,
									windowFrom,windowTo,isPercentageCheckBox.isSelected());
						}else{//Physiochemical2 multiple kgram
							if(absoluteButton.isSelected() == true)
								tempData = new MultipleKGramFeature('I',kgramString,xmistake,minGap,maxGap,
									windowFrom,windowTo,codingNamesComboBox.getSelectedIndex(),isPercentageCheckBox.isSelected());
							else
								tempData = new MultipleKGramFeature('J',kgramString,xmistake,minGap,maxGap,
									windowFrom,windowTo,codingNamesComboBox.getSelectedIndex(),isPercentageCheckBox.isSelected());
						}						
						model.add(tempData);
						tempString = "";
					}										
					statusPane.setText("Generating Multiple K-gram...DONE!");
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
	
	public void itemStateChanged(ItemEvent ie) {
		if(ie.getSource().equals(this.codingNamesComboBox)){			
			if(this.definitionsPanel != null)
				this.centerPanel.remove(this.definitionsPanel);		
			if(this.codingNamesComboBox.getSelectedIndex() != 0){
				//parent.setSize(830,450);
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
				//parent.setSize(830,350);
			}	
			parent.pack();
			revalidate();
			repaint();
		}		
	}
}
