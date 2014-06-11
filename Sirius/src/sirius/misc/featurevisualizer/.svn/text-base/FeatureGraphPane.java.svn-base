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
package sirius.misc.featurevisualizer;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.*;

import weka.core.Instances;

public class FeatureGraphPane extends JPanel implements MouseListener,MouseMotionListener{
	static final long serialVersionUID = sirius.Sirius.version;
	private JWindow toolTip;
	private Dimension parentDimension;
	private JLabel label;
	
	private int yIndex;//store the selected index of y axis combobox
	private int xIndex;//store the selected index of x axis combobox
	
	//starting point of the screen
	private final int topLeftX = 10;
	private final int topLeftY = 10;
	
	private Hashtable<Point,FeatureGraphPoint> positionFeatureHashtable;
	
	private Instances instances;
	private JTextField yIntervalTextField;//points to the interval textfield of y axis
	private JTextField xIntervalTextField;//points to the interval textfield of x axis
	
	private double yAxisMax;//to store the max value of y feature
	private double yAxisMin;//to store the min value of y feature
	private double xAxisMax;//to store the max value of x feature
	private double xAxisMin;//to store the min value of x feature
	
	private double yAxisLength;//to store the full length of yAxis
	private double xAxisLength;//to store the full length of xAxis
	
	private double yAxisIntervalLength;//to store the distance of each y axis interval
	private double xAxisIntervalLength;//to store the distance of each x axis interval
	
	private DecimalFormat df;	
	
	public FeatureGraphPane(Instances instances, JTextField yInterval, JTextField xInterval) {		
		this.instances = instances;
		this.yIntervalTextField = yInterval;
		this.xIntervalTextField = xInterval;
    	addMouseMotionListener(this);
    	addMouseListener(this);
    	initToolTip();
    	df = new DecimalFormat("0.####");
    }
    private void initToolTip() {
        label = new JLabel(" ");
        label.setOpaque(true);
        label.setBackground(UIManager.getColor("ToolTip.background"));
        toolTip = new JWindow(new Frame());
        toolTip.getContentPane().add(label);
    }
    
    public void setYIndex(int yIndex){
    	this.yIndex = yIndex;    
    	if(this.yIndex < 0)
    		return;
		yAxisMin = yAxisMax = instances.instance(0).value(yIndex);		
				
		for(int x = 1; x < instances.numInstances(); x++){
			double currentYValue = instances.instance(x).value(yIndex);
			if(currentYValue > yAxisMax)
				yAxisMax = currentYValue;
			else if(currentYValue < yAxisMin)
				yAxisMin = currentYValue;			
		}									
		double difference = yAxisMax - yAxisMin;
		difference /= 10;
		if(difference < 0)
			difference *= -1;
		else if(difference == 0)
			difference = 0.1;
		if(difference < 0.0001)
			difference = 0.0001;
		this.yIntervalTextField.setText(df.format(difference));
		recalibrateYAxisLength();    	
    }
    
    public void setXIndex(int xIndex){
    	this.xIndex = xIndex;
    	if(this.xIndex < 0)
    		return;
    	xAxisMin = xAxisMax = instances.instance(0).value(xIndex);
    	for(int x = 1; x < instances.numInstances(); x++){			
			double currentXValue = instances.instance(x).value(xIndex);
			if(currentXValue > xAxisMax)
				xAxisMax = currentXValue;
			else if(currentXValue < xAxisMin)
				xAxisMin = currentXValue;
    	}
    	double difference = xAxisMax - xAxisMin;
    	difference /= 10;
		if(difference < 0)
			difference *= -1;
		else if(difference == 0)
			difference = 0.1;
		if(difference < 0.0001)
			difference = 0.0001;
		this.xIntervalTextField.setText(df.format(difference));
		recalibrateXAxisLength();
    	    	    	    
    }
        
    
    public void recalibrateYAxisLength(){
    	double yIntervalDouble = Double.parseDouble(this.yIntervalTextField.getText());    	
    	this.yAxisIntervalLength = this.parentDimension.getHeight() * 0.078;    	
    	double difference = yAxisMax - yAxisMin;    	
		if(difference < 0)
			difference *= -1;
		else if(difference == 0)
			difference = 0.1;		
		int numOfInterval = (int)(difference / yIntervalDouble) + 1;		
		this.yAxisLength = (this.yAxisIntervalLength * numOfInterval) - this.topLeftY;
		populatePositionFeatureHashtable();
    }
    public void recalibrateXAxisLength(){
    	double xIntervalDouble = Double.parseDouble(this.xIntervalTextField.getText());
    	this.xAxisIntervalLength = this.parentDimension.getWidth() * 0.086;
    	double difference = xAxisMax - xAxisMin;
		if(difference < 0)
			difference *= -1;
		else if(difference == 0)
			difference = 0.1;
		int numOfInterval = (int)(difference / xIntervalDouble) + 1;
		this.xAxisLength = (this.xAxisIntervalLength * numOfInterval) - this.topLeftX;
		populatePositionFeatureHashtable();
    }
    
    public void populatePositionFeatureHashtable(){
    	if(instances == null || yIndex == -1 || xIndex == -1)
    		return;
    	positionFeatureHashtable = new Hashtable<Point, FeatureGraphPoint>();    	    
    	for(int index = 0; index < instances.numInstances(); index++){
    		double yFeatureValue = instances.instance(index).value(yIndex);
    		double xFeatureValue = instances.instance(index).value(xIndex);
    		boolean _class;
    		if(instances.instance(index).classValue() == 1.0)
    			_class = true;
    		else
    			_class = false;
    		double yPoint = ((1 - (yFeatureValue - yAxisMin) / (yAxisMax - yAxisMin)) * getYAxisLength());    		
    		double xPoint = (((xFeatureValue - xAxisMin) / (xAxisMax - xAxisMin)) * getXAxisLength());
    		positionFeatureHashtable.put(new Point(this.getTopLeftX() + (int)xPoint,this.getTopLeftY() + (int)yPoint), new FeatureGraphPoint(yFeatureValue, xFeatureValue, index+1, _class));
    	}    	
    	setPreferredSize(new Dimension((int)getXAxisLength() + 180, (int)getYAxisLength() + 10));		
    	this.repaint();
    }
    
    public int getTopLeftX(){
    	return topLeftX;
    }
    public int getTopLeftY(){
    	return topLeftY;
    }
        
	
	public void setParentDimension(Dimension d){
    	this.parentDimension = d;
    	//populatePositionFeatureHashtable();
    	//this.repaint();
    }
	
	/*public Dimension getParentDimension(){
		return this.parentDimension;
	}*/
	//This method changes the pane from grey background to white background
	public boolean isOpaque(){
        return false;
    }
	public void setInstances(Instances instances){
		this.instances = instances;
	}
	public void paintComponent(Graphics g){		
		if(instances == null || positionFeatureHashtable == null)
			return;
		for(Enumeration<Point> e = positionFeatureHashtable.keys(); e.hasMoreElements();){			
			Point p = e.nextElement();			
			//draw graph - the dots
			if(positionFeatureHashtable.get(p).isPos())				
				g.setColor(Color.BLUE);				
			else
				g.setColor(Color.RED);
			g.fillOval((int)p.getX(), (int)p.getY(),4,4);					
		}
		
		g.setColor(Color.BLACK);
		//draw y-axis
		//left of y
		g.drawLine(topLeftX - 3,topLeftY - 3,topLeftX - 3,(int)(topLeftY + getYAxisLength()) + 3);
		//right of y
		g.drawLine((int)(topLeftX + getXAxisLength()) + 3,topLeftY - 3,(int)(topLeftX + getXAxisLength()) + 3,(int)(topLeftY + getYAxisLength()) + 3);
		//draw x-axis
		//lower of x
		g.drawLine(topLeftX - 3,(int)(topLeftY + getYAxisLength()) + 3,(int)(topLeftX + getXAxisLength()) + 3,(int)(topLeftY + getYAxisLength()) + 3);
		//upper of x
		g.drawLine(topLeftX - 3,topLeftY - 3,(int)(topLeftX + getXAxisLength()) + 3,topLeftY - 3);
	}	
	public void mouseClicked(MouseEvent e){
    	//Invoked when the mouse button has been clicked (pressed and released) on a component. 
    }          
 	public void mouseEntered(MouseEvent e){
 		//Invoked when the mouse enters a component. 
 	}          
 	public void mouseExited(MouseEvent e){
 		//Invoked when the mouse exits a component. 
 		if(toolTip.isVisible())
            toolTip.setVisible(false); 
 	}          
 	public void mousePressed(MouseEvent e){
 		//Invoked when a mouse button has been pressed on a component. 
 	}          
 	public void mouseReleased(MouseEvent e){
 		//Invoked when a mouse button has been released on a component. 
 	}      
 	public void mouseDragged(MouseEvent e){
 		//Invoked when a mouse button is pressed on a component and then dragged. 
 	}          
 	public void mouseMoved(MouseEvent e){
 		//Invoked when the mouse cursor has been moved onto a component but no buttons have been pushed. 
 		//this is used to show information about a dot on the graph
 		if(positionFeatureHashtable == null)
 			return;
 		final int range = 3;
 		Point p = e.getPoint();
 		boolean found = false;
 		for(int x = (int)p.getX() - range; x <= (int)p.getX() + range && !found; x++){
 			for(int y = (int)p.getY() - range; y <= (int)p.getY() + range && !found; y++){
 				FeatureGraphPoint ps = positionFeatureHashtable.get(new Point(x,y));
 				if(ps!=null){
 					found = true;
 					label.setText("" + ps.getXValue() + ", " + ps.getYValue() + ", " + ps.getIndex());
			        toolTip.pack();
			        toolTip.setVisible(true);			        			        
			        SwingUtilities.convertPointToScreen(p, this);			    			        
			        toolTip.setLocation(p.x+range, p.y-toolTip.getHeight()-range);			        
 				}
 			}
 		} 		 		
 	}       
 	public double getYAxisLength(){
 		return this.yAxisLength;
 	}
 	public double getXAxisLength(){
 		return this.xAxisLength;
 	}
}
class FeatureViewPort extends JViewport{
	static final long serialVersionUID = sirius.Sirius.version;
	FeatureGraphPane sgp;
	public FeatureViewPort(FeatureGraphPane sgp){
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

class FeatureGraphPoint{
	private double yValue;
	private double xValue;
	private int index;
	private boolean _class;
	public FeatureGraphPoint(double yValue,double xValue, int index, boolean _class){		
		this.yValue = yValue;
		this.xValue = xValue;
		this.index = index;
		this._class = _class;
	}
	public double getYValue(){
		return this.yValue;
	}
	public double getXValue(){
		return this.xValue;
	}
	public int getIndex(){
		return this.index;
	}
	public boolean isPos(){
		return this._class;
	}
}
