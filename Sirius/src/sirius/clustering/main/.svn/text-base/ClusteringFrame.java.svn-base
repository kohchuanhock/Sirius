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
package sirius.clustering.main;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import javax.swing.*;

public class ClusteringFrame extends JInternalFrame{
	static final long serialVersionUID = sirius.Sirius.version;
	
	public ClusteringFrame(JFrame mainFrame) {
		super("Clustering",true,true,true,true);
	
		JTabbedPane tabbedPane = new JTabbedPane();	
		TrainClustererPane trainClustererPane = new TrainClustererPane(mainFrame);		
		tabbedPane.addTab("Train Clusterer", null, trainClustererPane,"Train and Save Clusterer");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		
		UtilizeClustererPane utilizeClustererPane = new UtilizeClustererPane(mainFrame);
		tabbedPane.addTab("Utilize Clusterer", null, utilizeClustererPane,"Utilizing Trained Clusterer");
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		
		ClustererClassificationPane clustererClassificationPane = new ClustererClassificationPane(this);
		tabbedPane.addTab("Classification", null, clustererClassificationPane,"Batch Classification");
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
		//Add the tabbed pane to this panel.
		BorderLayout thisLayout = new BorderLayout();
		thisLayout.setVgap(5);
		setLayout(thisLayout);
        add(tabbedPane,BorderLayout.CENTER);
	}
}
