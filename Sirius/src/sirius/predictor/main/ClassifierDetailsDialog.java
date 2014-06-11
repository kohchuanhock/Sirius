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

import java.awt.*;
import javax.swing.*;

public class ClassifierDetailsDialog extends JDialog{
	static final long serialVersionUID = sirius.Sirius.version;
	public ClassifierDetailsDialog(JFrame parent,ClassifierData classifierData){
		super(parent,"Name: " + classifierData.getClassifierName() + 
			"   Type: " + classifierData.getClassifierType());
		setLayout(new BorderLayout());		
				
		JPanel classifierOnePanel = new JPanel(new BorderLayout());
		classifierOnePanel.setBorder(BorderFactory.createCompoundBorder(
    	BorderFactory.createTitledBorder("Classifier One"),
    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));
    	JPanel classifierOneSettingsPanel = new JPanel();
		JLabel classifierOneSettingsLabel = new JLabel("Settings: ");
		classifierOneSettingsPanel.add(classifierOneSettingsLabel);
		
		String classifierOneSettingsString = classifierData.getClassifierOneSettings();		
		int maxLength = 121;
		int numOfLabels = classifierOneSettingsString.length()/maxLength + 1;
		JPanel classifierOneSettingsPanelR = new JPanel(new GridLayout(numOfLabels,1));
		JLabel[] classifierOneSettingsLabelR = new JLabel[numOfLabels];
		for(int x = 0; x < numOfLabels; x++){
			if(x + 1 != numOfLabels)
				classifierOneSettingsLabelR[x] = new JLabel(
					classifierOneSettingsString.substring(x*maxLength,x*maxLength + maxLength));
			else
				classifierOneSettingsLabelR[x] = new JLabel(
					classifierOneSettingsString.substring(x*maxLength));
			classifierOneSettingsPanelR.add(classifierOneSettingsLabelR[x]);
		}										
		classifierOneSettingsPanel.add(classifierOneSettingsPanelR);
		classifierOnePanel.add(classifierOneSettingsPanel,BorderLayout.NORTH);
		
		FeaturesDetailTableModel featuresDetailTableModel = 
			new FeaturesDetailTableModel(classifierData.getInstances());
    	JTable featuresDetailTable = new JTable(featuresDetailTableModel);
        featuresDetailTable.getColumnModel().getColumn(0).setMinWidth(40);
        featuresDetailTable.getColumnModel().getColumn(1).setMinWidth(300);  
        featuresDetailTable.getColumnModel().getColumn(2).setMinWidth(300);  
    	JScrollPane featuresDetailTableScrollPane = new JScrollPane(featuresDetailTable);
    	JPanel featuresDetailsPanel = new JPanel(new GridLayout(1,1));
    	featuresDetailsPanel.setBorder(BorderFactory.createCompoundBorder(
    		BorderFactory.createTitledBorder("Features"),
    		BorderFactory.createEmptyBorder(0, 5, 5, 5)));
    	featuresDetailsPanel.add(featuresDetailTableScrollPane);
		classifierOnePanel.add(featuresDetailsPanel,BorderLayout.CENTER);
			
		add(classifierOnePanel,BorderLayout.CENTER);
		
		if(classifierData.getClassifierType() == 2){
			JPanel classifierTwoPanel = new JPanel(new GridLayout(2,1));
			classifierTwoPanel.setBorder(BorderFactory.createCompoundBorder(
    			BorderFactory.createTitledBorder("Classifier Two"),
    			BorderFactory.createEmptyBorder(0, 5, 5, 5)));
			JPanel classifierTwoSettingsPanel = new JPanel();
			JLabel classifierTwoSettingsLabel = new JLabel("Settings: ");
			classifierTwoSettingsPanel.add(classifierTwoSettingsLabel);
			
			String classifierTwoSettingsString = classifierData.getClassifierTwoSettings();
			int maxTwoLength = 121;
			int numOfLabelsTwo = classifierTwoSettingsString.length()/maxTwoLength + 1;
			JPanel classifierTwoSettingsStringPanel = new JPanel(new GridLayout(numOfLabelsTwo,1));
			JLabel[] classifierTwoSettingsLabelR = new JLabel[numOfLabelsTwo];
			for(int x = 0; x < numOfLabelsTwo; x++){
				if(x + 1 != numOfLabelsTwo)
					classifierTwoSettingsLabelR[x] = new JLabel(
						classifierOneSettingsString.substring(x*maxTwoLength,x*maxTwoLength + maxTwoLength));
				else
					classifierTwoSettingsLabelR[x] = new JLabel(
						classifierOneSettingsString.substring(x*maxTwoLength));
				classifierTwoSettingsStringPanel.add(classifierTwoSettingsLabelR[x]);
			}			
			classifierTwoSettingsPanel.add(classifierTwoSettingsStringPanel);
			classifierTwoPanel.add(classifierTwoSettingsPanel);
			
			JPanel upstreamPanel = new JPanel();
			JLabel upstreamLabel = new JLabel("Upstream: ");
			JLabel upstreamLabelR = new JLabel("" + classifierData.getSetUpstream());
			upstreamPanel.add(upstreamLabel);
			upstreamPanel.add(upstreamLabelR);
			
			JPanel downstreamPanel = new JPanel();
			JLabel downstreamLabel = new JLabel("Downstream: ");
			JLabel downstreamLabelR = new JLabel("" + classifierData.getSetDownstream());
			downstreamPanel.add(downstreamLabel);
			downstreamPanel.add(downstreamLabelR);
			
			JPanel updownstreamPanel = new JPanel(new GridLayout(1,2));
			updownstreamPanel.add(upstreamPanel);
			updownstreamPanel.add(downstreamPanel);
			
			classifierTwoPanel.add(updownstreamPanel);
			add(classifierTwoPanel,BorderLayout.SOUTH);
		}		
		//setSize(900,600);
		this.pack();
	}
}