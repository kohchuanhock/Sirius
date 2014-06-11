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
package sirius.trainer.main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import sirius.main.ApplicationData;
import sirius.trainer.step1.*;
import sirius.trainer.step2.*;
import sirius.trainer.step3.*;
import sirius.trainer.step4.*;


public class TrainerFrame extends JInternalFrame{  
	static final long serialVersionUID = sirius.Sirius.version;
		
	private DefineDataPane step1Pane;	
	private DefineFeaturePane step2Pane;
	private SelectFeaturePane step3Pane;
	private ClassifierPane step4Pane;
	
	private StatusPane statusPane;
	private JTabbedPane tabbedPane;	
	private ApplicationData applicationData;	
	
	public DefineDataPane getStep1Pane(){return this.step1Pane;}	
	public DefineFeaturePane getStep2Pane(){return this.step2Pane;}	
	public ApplicationData getApplicationData(){return this.applicationData;}
	
	public TrainerFrame(JFrame mainFrame) {		        
		super("Trainer",true,true,true,true);	
		
        tabbedPane = new JTabbedPane();	
       	tabbedPane.addChangeListener(new MyChangeListener(tabbedPane));
        statusPane = new StatusPane("");
       	applicationData = new ApplicationData(statusPane);
       	step3Pane = new SelectFeaturePane(this,tabbedPane,applicationData);
		
		step1Pane = new DefineDataPane(this,tabbedPane,applicationData);		
		tabbedPane.addTab("Step 1: Define Data", null, step1Pane,
		                  "Define all input Datas");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		
		step2Pane = new DefineFeaturePane(this,tabbedPane,applicationData,step3Pane);
		tabbedPane.addTab("Step 2: Define Feature", null, step2Pane,"Define Features");
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		tabbedPane.setEnabledAt(1,false);
			
		tabbedPane.addTab("Step 3: Select Feature", null, step3Pane,"Select Feature");
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
		tabbedPane.setEnabledAt(2,false);
		
		step4Pane = new ClassifierPane(this,tabbedPane,applicationData,mainFrame);		 		
		tabbedPane.addTab("Step 4: Choose Classifer", null, step4Pane,"Choose Classifier");
		tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);
		tabbedPane.setEnabledAt(3,false);				
		
		//Add the tabbed pane to this panel.
		BorderLayout thisLayout = new BorderLayout();
		thisLayout.setVgap(5);
		setLayout(thisLayout);
        add(tabbedPane,BorderLayout.CENTER);                
       	add(statusPane,BorderLayout.SOUTH);               	                 
    }   
    protected JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JTextArea filler = new JTextArea(text);        
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }   
    /**
     * Shutdown procedure when run as an application.
     */
    protected void windowClosed() {    	    	       
        System.exit(0);
    }           	   
}
class MyChangeListener implements ChangeListener{
	
	private JTabbedPane tabbedPane;
	private int previous = 0;
	
	public MyChangeListener(JTabbedPane tabbedPane){
		this.tabbedPane = tabbedPane;
	}
	public void stateChanged(ChangeEvent e){		
		if(tabbedPane.getSelectedIndex() == 3){
			//Whenever view is switched to step4, call method updateLabels()
			((ClassifierPane)tabbedPane.getComponentAt(3)).updateLabels();			
		}else if(tabbedPane.getSelectedIndex() == 1 && previous == 0){
			//Whenever view is switched to step2
			((DefineFeaturePane)tabbedPane.getComponentAt(1)).modifyNorthPanel();
		}
		previous = tabbedPane.getSelectedIndex();
	}
}