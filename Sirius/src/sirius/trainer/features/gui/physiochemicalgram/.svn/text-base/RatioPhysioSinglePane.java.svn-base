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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import sirius.main.ApplicationData;
import sirius.nnsearcher.main.DefineConstraintsDialog;
import sirius.nnsearcher.main.MustHaveTableModel;
import sirius.trainer.features.Feature;
import sirius.trainer.features.RatioOfKGramFeature;
import sirius.trainer.step2.FeatureTableModel;

public class RatioPhysioSinglePane extends JComponent implements ActionListener{
	static final long serialVersionUID = sirius.Sirius.version;
	private JButton okButton;
	private JButton cancelButton;
	private JDialog parent;
	
	private JTextField xField;
	private JTextField yField;
	private JTextField m1Field; 
	private JTextField m2Field;
	private JTextField windowSizeFromField;
	private JTextField windowSizeToField;		
	
	private FeatureTableModel model;	
	private ApplicationData applicationData;
	private MustHaveTableModel constraintsModel;
	private JCheckBox isPercentageCheckBox = new JCheckBox("%");
		
public RatioPhysioSinglePane(JDialog parent,FeatureTableModel model,ApplicationData applicationData, MustHaveTableModel constraintsModel){
		this.parent = parent;
		this.model = model;		
		this.applicationData = applicationData;
		this.constraintsModel = constraintsModel;
		
		setLayout(new BorderLayout());
		//setTitle("K-gram with X mistakes");
		//setSize(300,220);				
		
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
		if(applicationData.isLocationIndexMinusOne == false){		
			center_north.setBorder(BorderFactory.createTitledBorder(
				"Window Size and Location (Relative to +1 Index)"));
		}
		else{
			center_north.setBorder(BorderFactory.createTitledBorder(
				"Window Size and Location"));
		}
		
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
		JPanel center_south = new JPanel(new GridLayout(2,1));
		center_south.setBorder(BorderFactory.createTitledBorder("Ratio of #X:#Y"));
		
		JPanel xPanel = new JPanel(new FlowLayout());
		JLabel xLabel = new JLabel("#X: ");
		xField = new JTextField(15);
		xPanel.add(xLabel);
		xPanel.add(xField);
		JLabel m1Label = new JLabel(" M1: ");
		m1Field = new JTextField(3);
		xPanel.add(m1Label);
		xPanel.add(m1Field);
		center_south.add(xPanel);
		
		JPanel yPanel = new JPanel(new FlowLayout());
		JLabel yLabel = new JLabel("#Y: ");
		yField = new JTextField(15);
		yPanel.add(yLabel);
		yPanel.add(yField);
		JLabel m2Label = new JLabel(" M2: ");
		m2Field = new JTextField(3);
		yPanel.add(m2Label);
		yPanel.add(m2Field);
		center_south.add(yPanel);							
		
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
		JPanel north = new JPanel(new BorderLayout());		
		north.add(center_north,BorderLayout.NORTH);
		north.add(center_south,BorderLayout.CENTER);
		add(north, BorderLayout.NORTH);
		add(center,BorderLayout.CENTER);
		parent.pack();
		}	

	private void okButtonPressed(){
		try{				
			int fromFieldInt = validateField(windowSizeFromField,"From Field");
			int toFieldInt = validateField(windowSizeToField,"To Field");				
			String xFieldString = validateString(xField,"#X Field");			
			String yFieldString = validateString(yField,"#Y Field");
			int xFieldInt = xFieldString.length();
			int yFieldInt = yFieldString.length();
			int m1FieldInt = validateField(m1Field,"M1 Field");
			int m2FieldInt = validateField(m2Field,"M2 Field");
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
			else if(m1FieldInt < 0 || m2FieldInt < 0){
				JOptionPane.showMessageDialog(parent,"M1 Field or M2 Field cannot be less than 0","ERROR",
					JOptionPane.ERROR_MESSAGE);
					m1Field.requestFocusInWindow();
			}else if(windowSize < xFieldInt || windowSize < yFieldInt){
				JOptionPane.showMessageDialog(parent,"Window Size is less than #X size or #Y size","ERROR",
						JOptionPane.ERROR_MESSAGE);
			}else{
				Feature tempData = new RatioOfKGramFeature('O',xFieldString,yFieldString,
						fromFieldInt,toFieldInt,m1FieldInt,m2FieldInt,isPercentageCheckBox.isSelected());
				if(this.model != null){
					//Generate the features							
					model.add(tempData);		
					JOptionPane.showMessageDialog(parent,"New Features Added!","Information",JOptionPane.INFORMATION_MESSAGE);
				}else{
					DefineConstraintsDialog dialog = new DefineConstraintsDialog(tempData, this.constraintsModel);
		    		dialog.setLocationRelativeTo(parent);
		    		dialog.setVisible(true);
				}
			}				
		}
		catch(NumberFormatException e){e.printStackTrace();}
		catch(Exception e){e.printStackTrace();}	
	}

	public void actionPerformed(ActionEvent ae){
		if(ae.getSource().equals(okButton)){	
			okButtonPressed();
		}
		else if(ae.getSource().equals(cancelButton)){
			parent.dispose();
		}
	}
	private String validateString(JTextField textField,String name) throws NumberFormatException,Exception{
		try{
			if(textField.getText().length() == 0)
				throw new Exception();		
			String text = textField.getText().trim().toUpperCase();			
			for(int x = 0; x < text.length(); x++){
				switch(text.charAt(x)){
					case 'H': //Hydrophobic (A, C, F, I, L, M, V)
					case 'P': //(+ve) Charge (H, K, R)
					case 'A': //Acidic (D, E, H)
					case 'O': //Order (N, C, I, L, F, W, Y, V)
					case 'L': //Hydrophilic (B, D, E, G, H, K, N, P, Q, R, S, T, W, Y, Z)
					case 'N': //(-ve) Charge (D, E)
					case 'K': //Alkaline (C, K, R, Y)
					case 'D': //Disorder (A, R, Q, E, G, K, P, S, Z)
					case 'X': //any
						break;
					default:
						throw new Exception();
				}				
			}				
			return text;
		}catch(NumberFormatException ex){
			JOptionPane.showMessageDialog(parent,"You need to at least input something into " + name,"ERROR",
				JOptionPane.ERROR_MESSAGE);
			textField.requestFocusInWindow();
			throw new Exception();
		}catch(Exception e){
				if(applicationData.getSequenceType().indexOf("DNA") != -1)
				JOptionPane.showMessageDialog(parent,"Input only A,C,T,G or X in " + name,"ERROR",
					JOptionPane.ERROR_MESSAGE);
			else
				JOptionPane.showMessageDialog(parent,"Input only H,P,A,O,L,N,K,D" + 
					" or X in " + name,"ERROR",JOptionPane.ERROR_MESSAGE);
			textField.requestFocusInWindow();			
			throw new NumberFormatException();
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
}
