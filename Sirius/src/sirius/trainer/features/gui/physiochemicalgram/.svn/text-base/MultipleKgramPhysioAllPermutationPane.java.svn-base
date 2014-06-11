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
package sirius.trainer.features.gui.physiochemicalgram;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import sirius.main.ApplicationData;
import sirius.nnsearcher.main.MustHaveTableModel;
import sirius.trainer.features.Feature;
import sirius.trainer.features.MultipleKGramFeature;
import sirius.trainer.main.StatusPane;
import sirius.trainer.step2.FeatureTableModel;

public class MultipleKgramPhysioAllPermutationPane extends JComponent implements ActionListener{
	static final long serialVersionUID = sirius.Sirius.version;
	private JButton okButton;
	private JButton cancelButton;
	private JDialog parent;
	
	private JTextField windowSizeFromField;
	private JTextField windowSizeToField;		
	
	private FeatureTableModel model;
	private StatusPane statusPane;
	private ApplicationData applicationData;
	
	private JButton addButton;
	private JButton deleteButton;
	
	private GridBagConstraints c;
	private JPanel fieldsPanel;
	
	private ArrayList<JTextField> kFieldArrayList;
	private ArrayList<JTextField> xFieldArrayList;
	private ArrayList<JTextField> yFieldArrayList;
	private ArrayList<JTextField> zFieldArrayList;	
		
	private JRadioButton absoluteButton = new JRadioButton("Absolute");
	private JRadioButton relativeButton = new JRadioButton("Relative",true);
	
	//private MustHaveTableModel constraintsModel;
	
	private JCheckBox checkBoxH;
	private JCheckBox checkBoxL;
	private JCheckBox checkBoxP;
	private JCheckBox checkBoxN;
	private JCheckBox checkBoxA;
	private JCheckBox checkBoxK;
	private JCheckBox checkBoxO;
	private JCheckBox checkBoxD;
	private JCheckBox checkBoxX;
	private JCheckBox isPercentageCheckBox = new JCheckBox("%");
		
	public MultipleKgramPhysioAllPermutationPane(JDialog parent,FeatureTableModel model,ApplicationData applicationData,
			MustHaveTableModel constraintsModel){
		this.parent = parent;
		this.model = model;
		this.statusPane = applicationData.getStatusPane();
		this.applicationData = applicationData;
		//his.constraintsModel = constraintsModel;
		
		setLayout(new BorderLayout());				
		
		//South
		okButton = new JButton("OK");
		cancelButton = new JButton("Cancel");
		JPanel southPanel = new JPanel(new FlowLayout());
		southPanel.add(okButton);
		southPanel.add(cancelButton);
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		add(southPanel,BorderLayout.SOUTH);
		
		JPanel includesPanel = new JPanel(new GridLayout(3,3));
		includesPanel.setBorder(BorderFactory.createTitledBorder("Includes"));
		this.checkBoxH = new JCheckBox("H", true);
		this.checkBoxL = new JCheckBox("L", true);
		this.checkBoxP = new JCheckBox("P", true);
		this.checkBoxN = new JCheckBox("N", true);
		this.checkBoxA = new JCheckBox("A", true);
		this.checkBoxK = new JCheckBox("K", true);
		this.checkBoxO = new JCheckBox("O", true);
		this.checkBoxD = new JCheckBox("D", true);
		this.checkBoxX = new JCheckBox("X");
		includesPanel.add(this.checkBoxH);		
		includesPanel.add(this.checkBoxL);
		includesPanel.add(this.checkBoxP);
		includesPanel.add(this.checkBoxN);
		includesPanel.add(this.checkBoxA);
		includesPanel.add(this.checkBoxK);
		includesPanel.add(this.checkBoxO);
		includesPanel.add(this.checkBoxD);
		includesPanel.add(this.checkBoxX);
		
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
		JTextField kgramField1 = new JTextField(15);
		JLabel xmistakeLabel1 = new JLabel("X = ");
		JTextField xmistakeField1 = new JTextField(3);
		panel1.add(kgramLabel1);
		panel1.add(kgramField1);
		panel1.add(xmistakeLabel1);
		panel1.add(xmistakeField1);
		kFieldArrayList.add(kgramField1);
		xFieldArrayList.add(xmistakeField1);
									
		JPanel panel2 = new JPanel();		
		JLabel kgramLabel2 = new JLabel("K = ");
		JTextField kgramField2 = new JTextField(15);
		JLabel xmistakeLabel2 = new JLabel("X = ");
		JTextField xmistakeField2 = new JTextField(3);
		panel2.add(kgramLabel2);
		panel2.add(kgramField2);
		panel2.add(xmistakeLabel2);
		panel2.add(xmistakeField2);
		kFieldArrayList.add(kgramField2);
		xFieldArrayList.add(xmistakeField2);						
					
		JPanel panel3 = new JPanel();
		JLabel minYLabel1 = new JLabel("Y = ");
		JTextField minYField1 = new JTextField(3);
		JLabel maxZLabel1 = new JLabel("Z = ");
		JTextField maxZField1 = new JTextField(3);
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
				
		
		//Center
		JPanel center = new JPanel(new GridLayout(4,2));		
		center.setBorder(BorderFactory.createTitledBorder("Definitions - (Will allow user to define soon)"));
		JLabel label1 = new JLabel("H - Hydrophobic (A, C, F, I, L, M, V)");
		JLabel label2 = new JLabel("L - Hydrophilic (B, D, E, G, H, K, N, P, Q, R, S, T, W, Y, Z)");
		JLabel label3 = new JLabel("P - (+ve) Charge (H, K, R)");
		JLabel label4 = new JLabel("N - (-ve) Charge (D, E)");
		JLabel label5 = new JLabel("A - Acidic (D, E, H)");
		JLabel label6 = new JLabel("K - Alkaline (C, K, R, Y)");
		JLabel label7 = new JLabel("O - Order (N, C, I, L, F, W, Y, V)");
		JLabel label8 = new JLabel("D - Disorder (A, R, Q, E, G, K, P, S, Z)");			
		
		center.add(label1);
		center.add(label2);
		center.add(label3);
		center.add(label4);
		center.add(label5);
		center.add(label6);
		center.add(label7);
		center.add(label8);
		
		//Center
		JPanel windowAndIncludes = new JPanel(new GridLayout(1,2));
		windowAndIncludes.add(center_north);
		windowAndIncludes.add(includesPanel);
		JPanel north = new JPanel(new BorderLayout());		
		north.add(windowAndIncludes,BorderLayout.NORTH);
		north.add(center_center,BorderLayout.CENTER);	
		add(center,BorderLayout.NORTH);
		add(north, BorderLayout.CENTER);
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
				int smallestXFieldInt = getSmallest(xFieldInt);
				int smallestYFieldInt = getSmallest(yFieldInt);
				int smallestZFieldInt = getSmallest(zFieldInt);
				int windowSize = 0;
				if((toFieldInt >= 0 && fromFieldInt >= 0) || (toFieldInt <= 0 && fromFieldInt <= 0))
					windowSize = toFieldInt - fromFieldInt +1;
				else
					windowSize = toFieldInt - fromFieldInt;
				int minWindowSizeNeeded = 0;
				for(int x = 0; x < kFieldInt.length; x++){
					minWindowSizeNeeded += kFieldInt[x];
					if(x+1 != kFieldInt.length)
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
					if(model != null)
						generateMultipleKgram(kFieldInt,fromFieldInt,toFieldInt,xFieldInt,yFieldInt,zFieldInt);
				}				
			}
			catch(NumberFormatException e){e.printStackTrace();}
			catch(Exception ex){ex.printStackTrace();}			
		}
		else if(ae.getSource().equals(cancelButton)){
			parent.dispose();
		}else if(ae.getSource().equals(addButton)){						
			JPanel panel1 = new JPanel();	
			JLabel kgramLabel1 = new JLabel("K = ");
			JTextField kgramField1 = new JTextField(15);
			JLabel xmistakeLabel1 = new JLabel("X = ");
			JTextField xmistakeField1 = new JTextField(3);
			panel1.add(kgramLabel1);
			panel1.add(kgramField1);
			panel1.add(xmistakeLabel1);
			panel1.add(xmistakeField1);
			kFieldArrayList.add(kgramField1);
			xFieldArrayList.add(xmistakeField1);
			
			JPanel panel3 = new JPanel();
			JLabel minYLabel1 = new JLabel("Y = ");
			JTextField minYField1 = new JTextField(3);
			JLabel maxZLabel1 = new JLabel("Z = ");
			JTextField maxZField1 = new JTextField(3);
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
	
	private void generateMultipleKgram(final int[] kgram, final int windowFrom, final int windowTo,
	final int[] xmistake,final int[] minGap,final int[] maxGap){
	if(applicationData.getOneThread() == null){
		statusPane.setText("Generating Multiple K-gram...");
		applicationData.setOneThread(new Thread(){
			public void run(){					
				int totalChecked = 0;
				String[] symbol;
				if(checkBoxH.isSelected()) totalChecked++;
				if(checkBoxL.isSelected()) totalChecked++;
				if(checkBoxP.isSelected()) totalChecked++;
				if(checkBoxN.isSelected()) totalChecked++;
				if(checkBoxA.isSelected()) totalChecked++;
				if(checkBoxK.isSelected()) totalChecked++;
				if(checkBoxO.isSelected()) totalChecked++;
				if(checkBoxD.isSelected()) totalChecked++;
				if(checkBoxX.isSelected()) totalChecked++;
				
				symbol = new String[totalChecked];
				int tempIndex = 0;
				if(checkBoxH.isSelected()){ symbol[tempIndex] = "H"; tempIndex++; }
				if(checkBoxL.isSelected()){ symbol[tempIndex] = "L"; tempIndex++; }
				if(checkBoxP.isSelected()){ symbol[tempIndex] = "P"; tempIndex++; }
				if(checkBoxN.isSelected()){ symbol[tempIndex] = "N"; tempIndex++; }
				if(checkBoxA.isSelected()){ symbol[tempIndex] = "A"; tempIndex++; }
				if(checkBoxK.isSelected()){ symbol[tempIndex] = "K"; tempIndex++; }
				if(checkBoxO.isSelected()){ symbol[tempIndex] = "O"; tempIndex++; }
				if(checkBoxD.isSelected()){ symbol[tempIndex] = "D"; tempIndex++; }
				if(checkBoxX.isSelected()){ symbol[tempIndex] = "X"; tempIndex++; }			
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
					if(absoluteButton.isSelected() == true)
						tempData = new MultipleKGramFeature('U',kgramString,xmistake,minGap,maxGap,windowFrom,windowTo,
								isPercentageCheckBox.isSelected());					
					else
						tempData = new MultipleKGramFeature('T',kgramString,xmistake,minGap,maxGap,windowFrom,windowTo,
								isPercentageCheckBox.isSelected());
					model.add(tempData);
					tempString = "";
				}										
				statusPane.setText("Generating Multiple K-gram...DONE!");
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
}


