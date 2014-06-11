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
package sirius.trainer.features.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import sirius.main.ApplicationData;
import sirius.nnsearcher.main.MustHaveTableModel;
import sirius.trainer.features.gui.advancedphysiochemical.AdvancedPhysioChemicalFeaturePane;
import sirius.trainer.features.gui.basicphysiochemical.BasicPhysioChemicalFeaturePane;
import sirius.trainer.features.gui.physiochemicalgram.PhysioKGramPane;
import sirius.trainer.features.gui.physiochemicalgram.PhysioMultipleKGramPane;
import sirius.trainer.features.gui.physiochemicalgram.PhysioRatioPane;
import sirius.trainer.step2.FeatureTableModel;

public class PhysioChemicalDialog extends JDialog{
	static final long serialVersionUID = sirius.Sirius.version;
	public PhysioChemicalDialog(JInternalFrame parent,FeatureTableModel model,ApplicationData applicationData, 
			MustHaveTableModel constraintModel){		
		setTitle("PhysioChemical Features");
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		
		//setSize(880,490);
    	setLayout(new BorderLayout());    	
    	    	 	       
        JTabbedPane tabbedPane = new JTabbedPane();
    	
        BasicPhysioChemicalFeaturePane basicPhysioChemicalFeaturePane = new BasicPhysioChemicalFeaturePane(this, model,applicationData,constraintModel);
		tabbedPane.addTab("Basic Features", null, basicPhysioChemicalFeaturePane,
		                  "Basic PhysioChemical Features");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);		
		
		Basic2PhysioChemicalFeaturePane basic2PhysioChemicalFeaturePane = new Basic2PhysioChemicalFeaturePane(this, model,applicationData,constraintModel);
		tabbedPane.addTab("Basic II Features", null, basic2PhysioChemicalFeaturePane,
		                  "Basic II PhysioChemical Features");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_2);		
		
		AdvancedPhysioChemicalFeaturePane advancedPhysioChemicalFeaturePane = new AdvancedPhysioChemicalFeaturePane(this,model,applicationData,constraintModel);
		tabbedPane.addTab("Advanced Features", null, advancedPhysioChemicalFeaturePane,
			"Advanced PhysioChemical Features");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_3);	
		
		PhysioKGramPane physioKGramPane = new PhysioKGramPane(this, model, applicationData, constraintModel);
		tabbedPane.addTab("K-grams Features", null, physioKGramPane,
			"K-grams Features");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_4);
		
		 
		PhysioMultipleKGramPane physioMultipleKGramPane = new PhysioMultipleKGramPane(this, model, applicationData, constraintModel);
		tabbedPane.addTab("Multiple K-grams Features", null, physioMultipleKGramPane,
			"Multiple K-grams Features");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_5);
		
		PhysioRatioPane physioRatioPane = new PhysioRatioPane(this, model, applicationData,constraintModel);
		tabbedPane.addTab("Ratio Features", null, physioRatioPane,
		"Ratio Features");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_6);
		
		add(tabbedPane,BorderLayout.CENTER);
		//this.pack();
		this.setSize(880,490);
	}    
}


