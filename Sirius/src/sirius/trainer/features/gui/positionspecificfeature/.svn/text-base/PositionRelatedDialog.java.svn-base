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
package sirius.trainer.features.gui.positionspecificfeature;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import sirius.main.ApplicationData;
import sirius.nnsearcher.main.MustHaveTableModel;
import sirius.trainer.step2.FeatureTableModel;

public class PositionRelatedDialog extends JDialog{
	static final long serialVersionUID = sirius.Sirius.version;
	JInternalFrame parent;

    public PositionRelatedDialog(JInternalFrame parent,FeatureTableModel model,ApplicationData applicationData, 
    		MustHaveTableModel constraintsModel) {
    	setTitle("Position-Related Features");
    	setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
    	
    	this.parent = parent;
    	
    	if(applicationData.getSequenceType().indexOf("PROTEIN") != -1)
    		setSize(670,630);
    	else if(applicationData.getSequenceType().indexOf("DNA") != -1)
    		setSize(670,330);
    	
    	setLayout(new BorderLayout());
    	
    	JTabbedPane tabbedPane = new JTabbedPane();
    	
    	PositionSpecificFeaturePane positionSpecificFeaturePane = new PositionSpecificFeaturePane(this,model,applicationData,constraintsModel);
		tabbedPane.addTab("Position Specific Feature", null, positionSpecificFeaturePane,
		                  "Position Specific Feature");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);			
		
		add(tabbedPane,BorderLayout.CENTER);
		//this.pack();
    }
}

class PositionSpecificViewPort extends JViewport{
	static final long serialVersionUID = sirius.Sirius.version;
	public PositionSpecificViewPort(){		
	}
    public void paintChildren(Graphics g){
        super.paintChildren(g);                		        		
    }

    public Color getBackground(){
        Component c = getView();
        return c == null ? super.getBackground() : c.getBackground();
    }        		   
}
