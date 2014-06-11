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
package sirius.trainer.step4;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sirius.utils.PredictionStats;

public class GraphPane extends JPanel implements ActionListener{	
	static final long serialVersionUID = sirius.Sirius.version;
	MyGraph graph;

    public GraphPane() {
    	this.setLayout(new BorderLayout());    	    	
    	JPanel settingsPanel = new JPanel(new GridLayout(2,1));
    	this.add(settingsPanel,BorderLayout.EAST);
    	JPanel topSettingsPanel = new JPanel(new BorderLayout());
    	settingsPanel.add(topSettingsPanel);
    	topSettingsPanel.setBorder(BorderFactory.createCompoundBorder(
    	BorderFactory.createTitledBorder("Legend"),
    		BorderFactory.createEmptyBorder(0, 7, 7, 7)));
    	JPanel topCenterSettingsPanel = new JPanel(new GridLayout(7,1));
    	topSettingsPanel.add(topCenterSettingsPanel,BorderLayout.CENTER);
    	JPanel topEastSettingsPanel = new JPanel(new GridLayout(7,1));
    	topSettingsPanel.add(topEastSettingsPanel,BorderLayout.EAST);
    	topEastSettingsPanel.add(new JLabel(new MyIcon(1)));
    	topEastSettingsPanel.add(new JLabel(new MyIcon(2)));
    	topEastSettingsPanel.add(new JLabel(new MyIcon(3)));
    	topEastSettingsPanel.add(new JLabel(new MyIcon(4)));
    	topEastSettingsPanel.add(new JLabel(new MyIcon(5)));
    	topEastSettingsPanel.add(new JLabel(new MyIcon(6)));
    	topEastSettingsPanel.add(new JLabel(new MyIcon(7)));
    	
    	JCheckBox correctPredictionsCheckBox = new JCheckBox("Correct Predictions");
    	JCheckBox incorrectPredictionsCheckBox = new JCheckBox("Incorrect Predictions");
    	JCheckBox precisionPosCheckBox = new JCheckBox("Precision(wrt +ve)");
    	JCheckBox precisionNegCheckBox = new JCheckBox("Precision(wrt -ve)");
    	JCheckBox snCheckBox = new JCheckBox("Sensitivity(SN)",true);
    	JCheckBox spCheckBox = new JCheckBox("Specificity(SP)",true);
    	JCheckBox aucCheckBox = new JCheckBox("ROC (Area)");
    	correctPredictionsCheckBox.addActionListener(this);
    	incorrectPredictionsCheckBox.addActionListener(this);   
    	precisionPosCheckBox.addActionListener(this);   	
    	precisionNegCheckBox.addActionListener(this);   	
    	snCheckBox.addActionListener(this);   	
    	spCheckBox.addActionListener(this);   	
    	aucCheckBox.addActionListener(this);
    	topCenterSettingsPanel.add(correctPredictionsCheckBox);
    	topCenterSettingsPanel.add(incorrectPredictionsCheckBox);
    	topCenterSettingsPanel.add(precisionPosCheckBox);
    	topCenterSettingsPanel.add(precisionNegCheckBox);
    	topCenterSettingsPanel.add(snCheckBox);
    	topCenterSettingsPanel.add(spCheckBox);
    	topCenterSettingsPanel.add(aucCheckBox);
    	
    	graph = new MyGraph(correctPredictionsCheckBox,incorrectPredictionsCheckBox,precisionPosCheckBox,
    		precisionNegCheckBox,snCheckBox,spCheckBox,aucCheckBox,topSettingsPanel.getSize().getWidth());
    	this.add(graph,BorderLayout.CENTER);
    }     
    	
    public void setMyStats(PredictionStats myStats){
    	graph.setMyStats(myStats);
    }
    
    public void actionPerformed(ActionEvent ae){
    	this.repaint();
    }
}
class MyIcon implements Icon{
	private int caseNumber;
	public MyIcon(int caseNumber){		
		this.caseNumber = caseNumber;
	}
	public void paintIcon(Component c, Graphics g, int x, int y) {
		switch(caseNumber){
			case 1:
				//correct
				g.setColor(Color.BLUE);
				g.fillOval(x,y,getIconHeight(),getIconWidth());
				break;
			case 2:
				//incorrect
				g.setColor(Color.RED);
				g.fillOval(x,y,getIconHeight(),getIconWidth());
				break;
			case 3:
				//precision(+ve)
				g.setColor(Color.CYAN);
				g.fillOval(x,y,getIconHeight(),getIconWidth());
				break;
			case 4:
				//precision(-ve);
				g.setColor(Color.GREEN);
				g.fillOval(x,y,getIconHeight(),getIconWidth());
				break;
			case 5:
				//SN
				g.setColor(Color.BLACK);
				g.fillOval(x,y,getIconHeight(),getIconWidth());
				break;
			case 6:
				//SP
				g.setColor(Color.PINK);
				g.fillOval(x,y,getIconHeight(),getIconWidth());
				break;
			case 7:
				//Area under ROC
				g.setColor(Color.ORANGE);
				g.fillOval(x,y,getIconHeight(),getIconWidth());
		}
		
	}
	public int getIconHeight(){
		return 6;
	}
	public int getIconWidth(){
		return 6;
	}
}

class MyGraph extends JPanel{
	static final long serialVersionUID = 23122007;
	int yLength;
	int xLength;
	int xSpace;
	int ySpace;
	int xAxis;
	int yAxis;	
	int intervals;
	int iconSize;
	int legendBoxWidth;
	
	JCheckBox correctPredictionsCheckBox;
	JCheckBox incorrectPredictionsCheckBox;
	JCheckBox precisionPosCheckBox;
	JCheckBox precisionNegCheckBox;
	JCheckBox snCheckBox;
	JCheckBox spCheckBox;    	
	JCheckBox aucCheckBox;
	
	PredictionStats myStats;
	public MyGraph(JCheckBox correctPredictionsCheckBox,JCheckBox incorrectPredictionsCheckBox,
			JCheckBox precisionPosCheckBox,JCheckBox precisionNegCheckBox,
			JCheckBox snCheckBox,JCheckBox spCheckBox,JCheckBox aucCheckBox,double legendBoxWidth){
		this.correctPredictionsCheckBox = correctPredictionsCheckBox;
		this.incorrectPredictionsCheckBox = incorrectPredictionsCheckBox;
		this.precisionPosCheckBox = precisionPosCheckBox;
		this.precisionNegCheckBox = precisionNegCheckBox;
		this.snCheckBox = snCheckBox;
		this.spCheckBox = spCheckBox;
		this.aucCheckBox = aucCheckBox;
		this.legendBoxWidth = (int) legendBoxWidth;
		myStats = null;
	}
	public void setMyStats(PredictionStats myStats){
		this.myStats = myStats;
	}
	private void drawLine(Graphics g,int xStart,int yStart,int xEnd,int yEnd){
    	g.drawLine(xStart + xSpace, yLength - (yStart + ySpace), xEnd + xSpace, yLength - (yEnd + ySpace));
    }
    private void drawGraph(Graphics g){
    	//drawing the dots for each selected check box    	
    	final int start = 0;
    	final int end = 100;
    	final int increment = 2;
    	int multiplier = 10;
    	if(myStats == null)
    		return;
    	if(correctPredictionsCheckBox.isSelected()){
    		g.setColor(Color.BLUE);
    		for(int x = start; x <= end; x+=increment){
    			g.fillOval(xSpace + (int)(((xAxis+0.0)/intervals)*(x/10.0) - iconSize/2),
    			((int)(yLength - ySpace - ((myStats.getAverageTotalCorrect(x*multiplier)+0.0)*yAxis)) - iconSize/2),
    				iconSize,iconSize); 
    			if(x+increment <= end)
    				g.drawLine(xSpace + (int)(((xAxis+0.0)/intervals)*(x/10.0)), 
    					((int)(yLength - ySpace - ((myStats.getAverageTotalCorrect(x*multiplier)+0.0)*yAxis))), 
    					xSpace + (int)(((xAxis+0.0)/intervals)*((x+increment)/10.0)), 
    					((int)(yLength - ySpace - ((myStats.getAverageTotalCorrect((x+increment)*multiplier)+0.0)*yAxis))));
    		}
    	}
    	if(incorrectPredictionsCheckBox.isSelected()){
    		g.setColor(Color.RED);
    		for(int x = start; x <= end; x+=increment){
    			g.fillOval(xSpace + (int)(((xAxis+0.0)/intervals)*(x/10.0) - iconSize/2),
    			((int)(yLength - ySpace - ((myStats.getAverageTotalIncorrect(x*multiplier)+0.0)*yAxis)) - iconSize/2),
    				iconSize,iconSize);    	
    			if(x+increment <= end)
    				g.drawLine(xSpace + (int)(((xAxis+0.0)/intervals)*(x/10.0)), 
    					((int)(yLength - ySpace - ((myStats.getAverageTotalIncorrect(x*multiplier)+0.0)*yAxis))), 
    					xSpace + (int)(((xAxis+0.0)/intervals)*((x+increment)/10.0)), 
    					((int)(yLength - ySpace - ((myStats.getAverageTotalIncorrect((x+increment)*multiplier)+0.0)*yAxis))));
    		}
    	}
    	if(precisionPosCheckBox.isSelected()){
    		g.setColor(Color.CYAN);
    		for(int x = start; x <= end; x+=increment){
    			g.fillOval(xSpace + (int)(((xAxis+0.0)/intervals)*(x/10.0) - iconSize/2),
    			((int)(yLength - ySpace - ((myStats.getAveragePrecisionPos(x*multiplier)+0.0)*yAxis)) - iconSize/2),
    				iconSize,iconSize);    	
    			if(x+increment <= end)
    				g.drawLine(xSpace + (int)(((xAxis+0.0)/intervals)*(x/10.0)), 
    					((int)(yLength - ySpace - ((myStats.getAveragePrecisionPos(x*multiplier)+0.0)*yAxis))), 
    					xSpace + (int)(((xAxis+0.0)/intervals)*((x+increment)/10.0)), 
    					((int)(yLength - ySpace - ((myStats.getAveragePrecisionPos((x+increment)*multiplier)+0.0)*yAxis))));
    		}
    	}
    	if(precisionNegCheckBox.isSelected()){
    		g.setColor(Color.GREEN);
    		for(int x = start; x <= end; x+=increment){
    			g.fillOval(xSpace + (int)(((xAxis+0.0)/intervals)*(x/10.0) - iconSize/2),
    			((int)(yLength - ySpace - ((myStats.getAveragePrecisionNeg(x*multiplier)+0.0)*yAxis)) - iconSize/2),
    				iconSize,iconSize);    			
    			if(x+increment <= end)
    				g.drawLine(xSpace + (int)(((xAxis+0.0)/intervals)*(x/10.0)), 
    					((int)(yLength - ySpace - ((myStats.getAveragePrecisionNeg(x*multiplier)+0.0)*yAxis))), 
    					xSpace + (int)(((xAxis+0.0)/intervals)*((x+increment)/10.0)), 
    					((int)(yLength - ySpace - ((myStats.getAveragePrecisionNeg((x+increment)*multiplier)+0.0)*yAxis))));
    		}
    	}
    	if(snCheckBox.isSelected()){
    		g.setColor(Color.BLACK);
    		for(int x = start; x <= end; x+=increment){
    			g.fillOval(xSpace + (int)(((xAxis+0.0)/intervals)*(x/10.0) - iconSize/2),
    			((int)(yLength - ySpace - ((myStats.getAverageSN(x*multiplier)+0.0)*yAxis)) - iconSize/2),
    				iconSize,iconSize);    		
    			if(x+increment <= end)
    				g.drawLine(xSpace + (int)(((xAxis+0.0)/intervals)*(x/10.0)), 
    					((int)(yLength - ySpace - ((myStats.getAverageSN(x*multiplier)+0.0)*yAxis))), 
    					xSpace + (int)(((xAxis+0.0)/intervals)*((x+increment)/10.0)), 
    					((int)(yLength - ySpace - ((myStats.getAverageSN((x+increment)*multiplier)+0.0)*yAxis))));
    		}
    	}
    	if(spCheckBox.isSelected()){
    		g.setColor(Color.PINK);
    		for(int x = start; x <= end; x+=increment){
    			g.fillOval(xSpace + (int)(((xAxis+0.0)/intervals)*(x/10.0) - iconSize/2),
    			((int)(yLength - ySpace - ((myStats.getAverageSP(x*multiplier)+0.0)*yAxis)) - iconSize/2),
    				iconSize,iconSize);    		
    			if(x+increment <= end)
    				g.drawLine(xSpace + (int)(((xAxis+0.0)/intervals)*(x/10.0)), 
    					((int)(yLength - ySpace - ((myStats.getAverageSP(x*multiplier)+0.0)*yAxis))), 
    					xSpace + (int)(((xAxis+0.0)/intervals)*((x+increment)/10.0)), 
    					((int)(yLength - ySpace - ((myStats.getAverageSP((x+increment)*multiplier)+0.0)*yAxis))));
    		}
    	}
    	if(aucCheckBox.isSelected()){
    		g.setColor(Color.ORANGE);
    		for(int x = start; x <= end; x+=increment){
    			g.fillOval(xSpace + (int)(((xAxis+0.0)/intervals)*((1.0 - myStats.getAverageSP(x*multiplier))*10.0) - iconSize/2),
    	    			((int)(yLength - ySpace - ((myStats.getAverageSN(x*multiplier)+0.0)*yAxis)) - iconSize/2),
    	    				iconSize,iconSize);    	
    			if(x+increment <= end)
    				g.drawLine(xSpace + (int)(((xAxis+0.0)/intervals)*((1.0 - myStats.getAverageSP(x*multiplier))*10.0)), 
    						((int)(yLength - ySpace - ((myStats.getAverageSN(x*multiplier)+0.0)*yAxis))),
    						xSpace + (int)(((xAxis+0.0)/intervals)*((1.0 - myStats.getAverageSP((x+increment)*multiplier))*10.0)), 
    						((int)(yLength - ySpace - ((myStats.getAverageSN((x+increment)*multiplier)+0.0)*yAxis))));
    		}    		
    	}
    }
    private void drawBackground(Graphics g){
    	//Y-axis
    	drawLine(g,0,0,0,yAxis);
    	
    	//X-axis
    	drawLine(g,0,0,xAxis,0);
    	
    	g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,11));
    	//Threshold
    	g.drawString("Threshold",xAxis - 5,yLength - 5);    	
    	//Fraction
    	g.drawString("Fraction",0,10);   	    	    	
		g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,9));
    	for(int x = 1; x <= intervals; x++){
    		//Draw markers for xAxis
    		//Use to check the spacing accuracy
    		//Note that the spacing is not exactly the same for all because of the conversion from double to int
    		//but at most a few pixel difference, likewise for yAxis markers    		
    		drawLine(g,(int)(xAxis*((x + 0.0)/intervals)),5,(int)(xAxis*((x + 0.0)/intervals)),-5);
    		g.drawString("" + ((x + 0.0)/10),xSpace + (int)(xAxis*((x + 0.0)/intervals)) - 6,yLength - ySpace + 15);
    		
    		
    		//Draw markers for yAxis    		
    		drawLine(g,5,(int)(yAxis*((x+0.0) / intervals)),-5,(int)(yAxis*((x+0.0) / intervals)));
    		g.drawString("" + ((x + 0.0)/10),xSpace - 20,
    			(int)(yLength - ySpace - (int)(yAxis*((x+0.0) / intervals)) + 4));
    	}
    }
    public void paint(Graphics g){    	
    	yLength = (int) this.getSize().getHeight();
    	xLength = (int) this.getSize().getWidth();
    	xSpace = 50;
    	ySpace = 30;
    	xAxis = xLength - this.legendBoxWidth - xSpace - 10;
    	yAxis = yLength - ySpace - 15;	
    	intervals = 10;
    	iconSize = 6;
    	drawBackground(g);    	
    	drawGraph(g);    	
    }    
}