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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import sirius.main.ApplicationData;
import sirius.nnsearcher.main.MustHaveTableModel;
import sirius.trainer.step2.FeatureTableModel;


public class KgramDialog extends JDialog{
	static final long serialVersionUID = sirius.Sirius.version;
	
	JInternalFrame parent;

    public KgramDialog(JInternalFrame parent,FeatureTableModel model,ApplicationData applicationData,MustHaveTableModel constraintsModel) {
    	setTitle("K-gram with X mistakes");
    	setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
    	
    	this.parent = parent;    	
    	
    	//setSize(680,300);
    	setLayout(new BorderLayout());    	
    	
    	JTabbedPane tabbedPane = new JTabbedPane();
    	
    	KgramManualPane kgramManualPane = new KgramManualPane(this,model,applicationData, constraintsModel);
		tabbedPane.addTab("Single", null, kgramManualPane,
		                  "Generate only this particular one");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		
		if(model != null){
			KgramAutoPane kgramAutoPane = new KgramAutoPane(this,model,applicationData);
			tabbedPane.addTab("All Permutations", null, kgramAutoPane,
		                  "Generate all possible");
			tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		}
		
		add(tabbedPane,BorderLayout.CENTER);
		this.pack();
		this.setLocationRelativeTo(parent);
    }
    
    
}
