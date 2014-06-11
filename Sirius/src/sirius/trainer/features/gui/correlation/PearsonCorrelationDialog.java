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
package sirius.trainer.features.gui.correlation;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class PearsonCorrelationDialog extends JDialog{
	static final long serialVersionUID = sirius.Sirius.version;	
	private JTextField pearsonScore = new JTextField("0.5");
	private JCheckBox includeNegative = new JCheckBox("Include (-ve)",true);
	
	public PearsonCorrelationDialog(JInternalFrame parent){
		setTitle("Pearson Correlation");
		setLayout(new BorderLayout());
						
		setLocationRelativeTo(parent);
		
		JPanel mainPanel = new JPanel(new GridLayout(3,2,5,5));	
		mainPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Settings"),BorderFactory.createEmptyBorder(0, 5, 5, 5)));		
		mainPanel.add(new JLabel("Max Pearson Score Allowed: ",SwingConstants.RIGHT));
		mainPanel.add(this.pearsonScore);
		mainPanel.add(new JLabel(""));
		mainPanel.add(this.includeNegative);
		add(mainPanel, BorderLayout.CENTER);
		//setSize(390,150);
		this.pack();
	}

	public double getMaxPearsonScore(){
		try{
			return Double.parseDouble(this.pearsonScore.getText());
		}catch(NumberFormatException e){
			return 1.0;
		}	
	}	
	
	public boolean isNegativesSelected(){
		return this.includeNegative.isSelected();
	}
}
