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
import javax.swing.*;
import javax.swing.border.*;

public class StatusPane extends JComponent{
	static final long serialVersionUID = sirius.Sirius.version;
	private JLabel statusLabel;
	private String prefix;
	private String text;
	private String suffix;
	
    public StatusPane(String text) {
    	Border statusBorder = BorderFactory.createTitledBorder("Status");
    	JPanel statusPanel = new JPanel(new GridLayout(1,1));
        statusLabel = new JLabel("Status Pane");
        statusPanel.setBorder(BorderFactory.createEmptyBorder(0,5,5,5));
        setBorder(statusBorder);
        statusLabel.setHorizontalAlignment(JLabel.LEFT);
        setLayout(new GridLayout(1, 1));
        statusPanel.add(statusLabel);
        add(statusPanel);
        setText(text);
    }
    
    public void setPrefix(String prefix){
    	this.prefix = prefix;
    	this.update();
    }
    
    public void setText(String text){
    	this.text = text; 
    	this.update();
    }
    
    public void setSuffix(String suffix){
    	this.suffix = suffix;
    	this.update();
    }
    
    private void update(){
    	String showString = "  ";
    	if(this.prefix != null)
    		showString += this.prefix;
    	showString += this.text;    		
    	if(this.suffix != null)
    		showString += this.suffix;   
    	statusLabel.setText(showString);
    }
    
    public JLabel getStatusLabel(){ return this.statusLabel; }
}