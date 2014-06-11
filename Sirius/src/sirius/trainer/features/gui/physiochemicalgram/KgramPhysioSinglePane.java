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
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import sirius.main.ApplicationData;
import sirius.nnsearcher.main.DefineConstraintsDialog;
import sirius.nnsearcher.main.MustHaveTableModel;
import sirius.trainer.features.Feature;
import sirius.trainer.features.KGramFeature;
import sirius.trainer.step2.FeatureTableModel;

public class KgramPhysioSinglePane extends JComponent implements ActionListener{
	static final long serialVersionUID = sirius.Sirius.version;
	
	private JButton okButton;
	private JButton cancelButton;
	private JDialog parent;
	
	private JTextField kgramField;
	private JTextField xmistakeField;
	private JTextField windowSizeFromField;
	private JTextField windowSizeToField;		
	
	private FeatureTableModel model;	
	private ApplicationData applicationData;
	
	private JRadioButton absoluteButton = new JRadioButton("Absolute");
	private JRadioButton relativeButton = new JRadioButton("Relative",true);
	
	private MustHaveTableModel constraintsModel;
	private JCheckBox isPercentageCheckBox = new JCheckBox("%");
	
	public KgramPhysioSinglePane(JDialog parent,FeatureTableModel model,ApplicationData applicationData, MustHaveTableModel constraintsModel){
		this.parent = parent;
		this.model = model;	
		this.applicationData = applicationData;
		this.constraintsModel = constraintsModel;
		
		setLayout(new BorderLayout());
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
		
		
		JPanel absoluteRelativePanel = new JPanel(new GridLayout(1,2));
		absoluteRelativePanel.setBorder(BorderFactory.createTitledBorder("Value"));		
		absoluteButton.addActionListener(this);		
		relativeButton.addActionListener(this);
		absoluteRelativePanel.add(absoluteButton);		
		absoluteRelativePanel.add(relativeButton);
		
		JPanel top = new JPanel(new BorderLayout());
		top.add(center_north, BorderLayout.CENTER);
		top.add(absoluteRelativePanel, BorderLayout.EAST);
		
		//center_south
		JPanel center_south = new JPanel();
		center_south.setBorder(BorderFactory.createTitledBorder("K-gram with X-mistakes allowed"));
		
		JPanel kgramPanel = new JPanel(new FlowLayout());
		JLabel kgramLabel = new JLabel("K: ");
		kgramField = new JTextField(30);
		kgramPanel.add(kgramLabel);
		kgramPanel.add(kgramField);
		center_south.add(kgramPanel);
		JPanel xmistakePanel = new JPanel(new FlowLayout());
		JLabel xmistakeLabel = new JLabel("X: ");
		xmistakeField = new JTextField(3);
		xmistakePanel.add(xmistakeLabel);
		xmistakePanel.add(xmistakeField);
		center_south.add(xmistakePanel);							
					
		JPanel center_center = new JPanel(new GridLayout(1,1));
		center_center.add(center_south);
		//center_center.add(absoluteRelativePanel);				
		
		JPanel north = new JPanel(new GridLayout(2,1));
		north.add(top);
		north.add(center_center);
		
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
		
			
		add(north,BorderLayout.NORTH);
		add(center,BorderLayout.CENTER);
		parent.pack();
	}

	
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource().equals(okButton)){			
			try{				
				int fromFieldInt = validateField(windowSizeFromField,"From Field");
				int toFieldInt = validateField(windowSizeToField,"To Field");				
				int xmistakeFieldInt = validateField(xmistakeField,"X-mistake Field");			
				String kgramFieldString = validateString(kgramField,"K-gram Field");
				int kgramFieldInt = kgramFieldString.length();
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
				else if(windowSize < kgramFieldInt){
					JOptionPane.showMessageDialog(parent,"Window Size is less than kgram","ERROR",
							JOptionPane.ERROR_MESSAGE);
				}	
				else{
					//Generate the features
					Feature tempData;
					if(absoluteButton.isSelected() == true){//G - Physiochemical k-gram with absolute value
						tempData = new KGramFeature('G',kgramFieldString,xmistakeFieldInt,fromFieldInt,
							toFieldInt,isPercentageCheckBox.isSelected());				
					}else{//H - Physiochemical k-gram with relative value
						tempData = new KGramFeature('H',kgramFieldString,xmistakeFieldInt,fromFieldInt,
							toFieldInt,isPercentageCheckBox.isSelected());
					}		
					if(this.model != null){
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
		}
		else if(ae.getSource().equals(cancelButton)){
			parent.dispose();
		}
		else if(ae.getSource().equals(absoluteButton)){
			//toggle of the radio buttons
			absoluteButton.setSelected(true);
			relativeButton.setSelected(false);
		}
		else if(ae.getSource().equals(relativeButton)){
			//toggle of the radio buttons
			absoluteButton.setSelected(false);
			relativeButton.setSelected(true);
		}
	}	
	
	private String validateString(JTextField textField,String name) throws NumberFormatException{
		try{
			String text = textField.getText().trim().toUpperCase();
			if(text.length() == 0)
				throw new NumberFormatException();			
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
			JOptionPane.showMessageDialog(parent,"K-gram cannot be empty","ERROR",
				JOptionPane.ERROR_MESSAGE);
			textField.requestFocusInWindow();
			throw new NumberFormatException();
		}catch(Exception e){			
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