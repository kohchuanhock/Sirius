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
package sirius.misc.sequencevisualizer;

import java.awt.*;
import java.text.DecimalFormat;

import javax.swing.JPanel;


public class SequenceGraphPane extends JPanel{
	static final long serialVersionUID = sirius.Sirius.version;

	private final int topLeftX = 20;
	private final int topLeftY = 5;
	private Dimension parentDimension;
	private SequenceQuencher feature1SequenceQuencher;		
	private String sequence;
	private int plusOneIndex;
	private boolean feature1InvertValues;		
	private boolean showMax;	
	
	private int xAxisSpacing = 1;
	//This method changes the pane from grey background to white background
	public SequenceGraphPane(){		
	}
	
	public boolean isOpaque(){
        return false;
    }
	
	public void setShowMax(boolean showMax){
		this.showMax = showMax;
	}
	
	public int getTopLeftX(){
		return this.topLeftX;
	}
	public int getTopLeftY(){
		return this.topLeftY;
	}
	public Dimension getParentDimension(){
		return this.parentDimension;
	}
	public void setParentDimension(Dimension d){    	 
    	this.parentDimension = d;
	}
	public void setSequenceDetails(String sequence, int plusOneIndex){
		this.sequence = sequence;
		this.plusOneIndex = plusOneIndex;
		//this.xAxisSpacing = 1;		
		/*for(int x = 1; x <= 64; x *= 2){
			if((sequence.length()*x + topLeftX)< this.parentDimension.getWidth()){				
				this.xAxisSpacing = x;				
			}
		}*/				
	}
	public void setFeature1SequenceQuencher(SequenceQuencher sq, boolean invertValues){		
		this.feature1SequenceQuencher = sq;				
		this.feature1InvertValues = invertValues;
	}	
	
	public SequenceQuencher getFeature1SequenceQuencher(){
		return this.feature1SequenceQuencher;
	}
	public int getYAxisHeight(){
		int yAxisHeight = (int)parentDimension.getHeight() - 88;
    	yAxisHeight -= yAxisHeight%10;
    	//return yAxisHeight * zoomLevel;
    	return yAxisHeight;
	}	
	
	public int getXAxisSpacing(){
		return this.xAxisSpacing;
	}
	
	public void zoomIn(){
		if(this.xAxisSpacing <= 64){
			this.xAxisSpacing *= 2;			
		}
	}
	
	public void zoomOut(){
		if(this.xAxisSpacing >= 2){
			this.xAxisSpacing /= 2;			
		}
	}
	
	private void paintGraph(Graphics g, SequenceQuencher sequenceQuencher, boolean invertValues){		
		int yAxisHeight = getYAxisHeight();
    	DecimalFormat df = new DecimalFormat("0.##");    	
    	double globalViewMaxValue = sequenceQuencher.getGlobalViewMaxValue();
    	double globalViewMinValue = sequenceQuencher.getGlobalViewMinValue();
    	double globalViewRange = globalViewMaxValue - globalViewMinValue;      	
    	
    	for(int x = 0; x < sequenceQuencher.getGlobalViewSize(); x += 1){
    		double yValue = ((sequenceQuencher.getGobalView(x) - globalViewMinValue) / globalViewRange);      		
    		g.fillOval((((x*sequenceQuencher.getStepSize())+1)*this.xAxisSpacing) + topLeftX - 2, (int)((yAxisHeight+topLeftY) * (1 - (yValue))),3,3);    		
    	}    	
    	
    	//draw ref line
    	double midPointValue = 0.0;
    	if(sequenceQuencher.getRefLine().isEmpty() == false){
    		midPointValue = ((Double.parseDouble(sequenceQuencher.getRefLine())) - globalViewMinValue) / globalViewRange;
    		g.drawLine(topLeftX,(int)((yAxisHeight+topLeftY) * (1 - (midPointValue))),
				topLeftX+(this.sequence.length() * this.xAxisSpacing) + 15,(int)((yAxisHeight+topLeftY) * (1 - (midPointValue))));
    	}
		
		//draw y-axis
		g.drawLine(topLeftX,topLeftY,topLeftX,topLeftY+yAxisHeight);	
		
		//Draw markers for y-axis
		if(sequenceQuencher != null){
			g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,9));
			g.setColor(Color.RED);
			g.drawString(df.format(globalViewMaxValue), topLeftX - 15, topLeftY + 3);		
			g.drawString(df.format(globalViewMinValue), topLeftX - 10, topLeftY + 10 + yAxisHeight);		
			if(sequenceQuencher.getRefLine().isEmpty() == false)
				g.drawString(sequenceQuencher.getRefLine(), topLeftX,(int)((yAxisHeight+topLeftY) * (1 - (midPointValue))) - 2);
		}
    	
    	//DO NOT DELETE THIS PORTION
		//ONLY NOT IN USE TEMPORARILY
		//USED FOR SHOWING THE MAX VALUE IN THE MINIMAL REGION
		if(this.showMax){
	    	double maxValue = sequenceQuencher.getMaxValue();
	    	//DecimalFormat df = new DecimalFormat("0.##");
			for(int x = 0; x < sequenceQuencher.getNumOfRegion(); x++){												
				//top line
				g.drawLine(((sequenceQuencher.get(x).getIndexFrom() + 1 ) * this.xAxisSpacing ) + topLeftX - (this.xAxisSpacing/2), 
						(int)((yAxisHeight+topLeftY) * (1 - (sequenceQuencher.get(x).getValue()/maxValue))), 
						((sequenceQuencher.get(x).getIndexTo() + 1 ) * this.xAxisSpacing ) + topLeftX + (this.xAxisSpacing/2), 
						(int)((yAxisHeight+topLeftY) * (1 - (sequenceQuencher.get(x).getValue()/maxValue))));
				//left line
				g.drawLine(((sequenceQuencher.get(x).getIndexFrom() + 1 ) * this.xAxisSpacing ) + topLeftX - (this.xAxisSpacing/2), 
						(int)((yAxisHeight+topLeftY) * (1 - (sequenceQuencher.get(x).getValue()/maxValue))), 
						((sequenceQuencher.get(x).getIndexFrom() + 1 ) * this.xAxisSpacing ) + topLeftX - (this.xAxisSpacing/2), 
						yAxisHeight+topLeftY);
				//right line
				g.drawLine(((sequenceQuencher.get(x).getIndexTo() + 1 ) * this.xAxisSpacing ) + topLeftX + (this.xAxisSpacing/2), 
						(int)((yAxisHeight+topLeftY) * (1 - (sequenceQuencher.get(x).getValue()/maxValue))), 
						((sequenceQuencher.get(x).getIndexTo() + 1 ) * this.xAxisSpacing ) + topLeftX + (this.xAxisSpacing/2), 
						yAxisHeight+topLeftY);
				//show value only if xAxisSpacing >= 2
				if(this.xAxisSpacing >= 2){
					if(invertValues)
						g.drawString("-" + df.format(sequenceQuencher.get(x).getValue()), 
							((sequenceQuencher.get(x).getIndexFrom() + 1 ) * this.xAxisSpacing ) + topLeftX - (this.xAxisSpacing/2) + 3,
							(int)((yAxisHeight+topLeftY) * (1 - (sequenceQuencher.get(x).getValue()/maxValue))) + 10);
					else
						g.drawString("" + df.format(sequenceQuencher.get(x).getValue()), 
								((sequenceQuencher.get(x).getIndexFrom() + 1 ) * this.xAxisSpacing ) + topLeftX - (this.xAxisSpacing/2) + 3,
								(int)((yAxisHeight+topLeftY) * (1 - (sequenceQuencher.get(x).getValue()/maxValue))) + 10);
				}
			}	
		}
	}
	public void paintComponent(Graphics g){		
		int yAxisHeight = getYAxisHeight(); 	    	    	
    	
    	g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,9));
		if(this.feature1SequenceQuencher == null){			
    		//draw x-axis
			g.drawLine(topLeftX,yAxisHeight+topLeftY,
				topLeftX+(int)parentDimension.getWidth()-75,yAxisHeight+topLeftY);
			//draw y-axis
			g.drawLine(topLeftX,topLeftY,topLeftX,topLeftY+yAxisHeight);	
		}else{
			//draw x-axis
			g.drawLine(topLeftX,yAxisHeight+topLeftY,
					topLeftX+(this.sequence.length() * this.xAxisSpacing) + 15,yAxisHeight+topLeftY);						
			
			int xLocation = 0;
			if(this.plusOneIndex == -1){
				for(int x = 0; x < this.sequence.length(); x++){
					xLocation = ((x+1)*this.xAxisSpacing) + topLeftX;
					//draw markers
					if(this.xAxisSpacing >= 32)
						g.drawLine(xLocation,yAxisHeight+topLeftY+3,xLocation,yAxisHeight+topLeftY-3);
					else if((x+1)%25 == 0)
						g.drawLine(xLocation,yAxisHeight+topLeftY+3,xLocation,yAxisHeight+topLeftY-3);
					//draw markers numbers 
					if(this.xAxisSpacing >= 32)
						g.drawString(""+(x+1),xLocation - 3,yAxisHeight+topLeftY+15);
					else if((x+1)%25 == 0)
						g.drawString(""+(x+1),xLocation - 3,yAxisHeight+topLeftY+15);
					//draw sequence if xAxisSpacing >= 32
					if(this.xAxisSpacing >= 16)
						g.drawString("" + this.sequence.charAt(x), xLocation - 3, yAxisHeight+topLeftY+30);
				}
			}else{								
				//Draw markers for x-axis
				for(int x = 0; x < this.sequence.length(); x++){
					xLocation = ((x+1)*this.xAxisSpacing) + topLeftX;
					//draw markers
					if(this.xAxisSpacing >= 32)
						g.drawLine(xLocation,yAxisHeight+topLeftY+3,xLocation,yAxisHeight+topLeftY-3);
					else if((x+1)%25 == 0)
						g.drawLine(xLocation,yAxisHeight+topLeftY+3,xLocation,yAxisHeight+topLeftY-3);
					//draw markers numbers
					if(this.xAxisSpacing >= 32){
						if(x - this.plusOneIndex < 0)
							g.drawString(""+(x - this.plusOneIndex),xLocation - 3,yAxisHeight+topLeftY+15);
						else
							g.drawString(""+(x - this.plusOneIndex + 1),xLocation - 3,yAxisHeight+topLeftY+15);					
					}else if((x+1)%25 == 0){
						if(x - this.plusOneIndex < 0)
							g.drawString(""+(x - this.plusOneIndex),xLocation - 3,yAxisHeight+topLeftY+15);
						else
							g.drawString(""+(x - this.plusOneIndex + 1),xLocation - 3,yAxisHeight+topLeftY+15);	
					}
					//draw sequence if xAxisSpacing >= 32
					if(this.xAxisSpacing >= 16)
						g.drawString("" + this.sequence.charAt(x), xLocation - 3, yAxisHeight+topLeftY+30);
				}			
			}			
			//somehow setPreferredSize works better when you call it not within paintComponent or any methods called by it
			//this.setPreferredSize(new Dimension(xLocation + 60, yAxisHeight));
			//draw graph
			if(this.feature1SequenceQuencher != null){
				g.setColor(Color.RED);				
				paintGraph(g, feature1SequenceQuencher, feature1InvertValues);
			}			
		}		
	}
}