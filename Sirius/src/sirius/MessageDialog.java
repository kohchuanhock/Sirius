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
package sirius;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class MessageDialog extends JDialog{
	private JLabel messageLabel;
	static final long serialVersionUID = 23122007;
	public MessageDialog(Frame parent,String title, String message){
		super(parent,title);
		setLayout(new GridLayout(1,1));
		//setSize(200,90);
		this.messageLabel = new JLabel(message,SwingConstants.CENTER);
		add(this.messageLabel);
		this.pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}	
	
	public MessageDialog(Component parent,String title, String message){		
		setTitle(title);
		setLayout(new GridLayout(1,1));
		//setSize(300,90);
		this.messageLabel = new JLabel(message,SwingConstants.CENTER);
		add(this.messageLabel);
		this.pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}	
	
	public void update(String message){
		this.messageLabel.setText(message);		
	}
		
}
