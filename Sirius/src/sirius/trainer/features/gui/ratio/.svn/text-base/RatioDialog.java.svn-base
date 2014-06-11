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
package sirius.trainer.features.gui.ratio;

import javax.swing.*;

import sirius.main.ApplicationData;
import sirius.nnsearcher.main.MustHaveTableModel;
import sirius.trainer.step2.FeatureTableModel;

import java.awt.*;
import java.awt.event.*;

public class RatioDialog extends JDialog{    
	static final long serialVersionUID = sirius.Sirius.version;
	public RatioDialog(JInternalFrame parent,FeatureTableModel model,ApplicationData applicationData,MustHaveTableModel constraintsModel){		
		setTitle("Ratio of #X:#Y");
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		
		//setSize(590,270);
    	setLayout(new BorderLayout());    	
    	
    	JTabbedPane tabbedPane = new JTabbedPane();
    	
    	RatioIndividualPane individualPane = new RatioIndividualPane(this,model,applicationData, constraintsModel);
		tabbedPane.addTab("Single", null, individualPane,
		                  "Generate only this particular one");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		
		if(model != null){
			RatioAllPossiblePane allPossiblePane = new RatioAllPossiblePane(this,model,applicationData);
			tabbedPane.addTab("All Permutations", null, allPossiblePane,
		                  "Generate all possible");
			tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		}
		
		add(tabbedPane,BorderLayout.CENTER);
		this.pack();
	}    
	
}


