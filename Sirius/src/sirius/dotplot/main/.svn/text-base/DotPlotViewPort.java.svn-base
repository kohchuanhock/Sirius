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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JViewport;

public class DotPlotViewPort extends JViewport{
	static final long serialVersionUID = sirius.Sirius.version;
	DotPlotGraphPane sgp;
	public DotPlotViewPort(DotPlotGraphPane sgp){
		this.sgp = sgp;
	}
    public void paintChildren(Graphics g){
        super.paintChildren(g);
                
		//final int topLeftX = sgp.getTopLeftX();
		//final int topLeftY = sgp.getTopLeftY();		        				
    }

    public Color getBackground(){
        Component c = getView();
        return c == null ? super.getBackground() : c.getBackground();
    }        		   
}

