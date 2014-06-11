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
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import sirius.main.ApplicationData;
import sirius.nnsearcher.main.MustHaveTableModel;
import sirius.trainer.step2.FeatureTableModel;

public class PhysioMultipleKGramPane extends JComponent{
	static final long serialVersionUID = sirius.Sirius.version;
	
	public PhysioMultipleKGramPane(JDialog parent, FeatureTableModel model,ApplicationData applicationData, 
			MustHaveTableModel constraintsModel){
		setLayout(new BorderLayout());    	
		
		JTabbedPane tabbedPane = new JTabbedPane();
    	
    	MultipleKgramPhysioSinglePane multipleKgramPhysioSinglePane = new MultipleKgramPhysioSinglePane(parent,model,applicationData, 
    			constraintsModel);
		tabbedPane.addTab("Single", null, multipleKgramPhysioSinglePane,
		                  "Generate only this particular one");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		
    	MultipleKgramPhysioAllPermutationPane multipleKgramPhysioAllPermutationPane = new MultipleKgramPhysioAllPermutationPane(parent,model,applicationData, 
    			constraintsModel);
		tabbedPane.addTab("All Permutation", null, multipleKgramPhysioAllPermutationPane,
		                  "Generate all possible");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_2);						
		
		add(tabbedPane,BorderLayout.CENTER);
	}
}


