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
package sirius.dotplot.main;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class DotPlotFrame extends JInternalFrame implements ChangeListener{
	static final long serialVersionUID = sirius.Sirius.version;
	JTabbedPane tabbedPane;
	DotPlotPane dotPlotPane;
	
	public DotPlotFrame(JFrame frame){
		super("Visualizer",true,true,true,true);
		
		tabbedPane = new JTabbedPane();	
		tabbedPane.addChangeListener(this);
		
		//dotPlotPane = new DotPlotPane(this,tabbedPane);
		tabbedPane.addTab("Dot Plot", null, dotPlotPane,"Dot Plot");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		
		BorderLayout thisLayout = new BorderLayout();
		thisLayout.setVgap(5);
		setLayout(thisLayout);
        add(tabbedPane,BorderLayout.CENTER);
	}
	
	public void stateChanged(ChangeEvent e){
		//This method is not in use yet. Check with hyp.analyser.main.AnalyserFrame
	}
}
