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

package sirius.predictor.main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.text.*;

public class ScoreGraphPane extends JPanel implements MouseListener,MouseMotionListener{
	static final long serialVersionUID = sirius.Sirius.version;
	private String scoreLine;
	private Dimension parentDimension;
	private Hashtable<Point,PositionScore> pointPositionScoreHashtable;
	private JWindow toolTip;
    private JLabel label;
    private boolean onePrediction;
	
	private final int topLeftX = 20;
	private final int topLeftY = 5;
    public ScoreGraphPane() {
    	scoreLine = "";
    	addMouseMotionListener(this);
    	addMouseListener(this);
    	initToolTip();
    }
    private void initToolTip() {
        label = new JLabel(" ");
        label.setOpaque(true);
        label.setBackground(UIManager.getColor("ToolTip.background"));
        toolTip = new JWindow(new Frame());
        toolTip.getContentPane().add(label);
    }
    public int getTopLeftX(){
    	return topLeftX;
    }
    public int getTopLeftY(){
    	return topLeftY;
    }
    public void setScoreLine(String scoreLine){    	
    	this.scoreLine = scoreLine;
    	populatePointPositionScoreHashtable();
    }
    public void setParentDimension(Dimension d){
    	//setPreferredSize(new Dimension(1000,(int)d.getHeight() - 50));  
    	this.parentDimension = d;
    	populatePointPositionScoreHashtable();
    	this.repaint();
    }
    public Dimension getParentDimension(){
    	return parentDimension;
    }
	private void populatePointPositionScoreHashtable(){
		pointPositionScoreHashtable = new Hashtable<Point,PositionScore>();
		StringTokenizer st = new StringTokenizer(scoreLine,",");
		int currentX = 0;
		int yAxisHeight = (int)parentDimension.getHeight() - 75;
		yAxisHeight -= yAxisHeight%10;
		onePrediction = false;
		while(st.hasMoreTokens()){
			String currentToken = st.nextToken();
			if(currentToken.indexOf("=")==-1)//skip first line that is _class {"pos,neg"}
				continue;			
			StringTokenizer st2 = new StringTokenizer(currentToken,"=");			
			int position = Integer.parseInt(st2.nextToken());
			double score = Double.parseDouble(st2.nextToken());
			if(position == 0){
				currentX = 15;
				onePrediction = true;
			}				
			int x = topLeftX + (currentX*20);
			int y = (int)((yAxisHeight) * (1 - score)) + topLeftY;
			pointPositionScoreHashtable.put(new Point(x,y),new PositionScore(position,score));
			currentX++;
		}		
		setPreferredSize(new Dimension((pointPositionScoreHashtable.size() * 20) + 100,
			(int)parentDimension.getHeight() - 50));		
	}    
    public void paintComponent(Graphics g){
    	int yAxisHeight = (int)parentDimension.getHeight() - 75;
    	yAxisHeight -= yAxisHeight%10;
    	if(scoreLine.length()==0)
    		//draw x-axis
			g.drawLine(topLeftX,yAxisHeight+topLeftY,
				topLeftX+(int)parentDimension.getWidth()-75,yAxisHeight+topLeftY);
		else{
			g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,9));
			//draw x-axis
			if(onePrediction == true){			
				g.drawLine(topLeftX,yAxisHeight+topLeftY,
					topLeftX+(int)parentDimension.getWidth()-75,yAxisHeight+topLeftY);
			}else{
				g.drawLine(topLeftX,yAxisHeight+topLeftY,
					topLeftX+(pointPositionScoreHashtable.size() * 20),yAxisHeight+topLeftY);
			}									
			for(Enumeration<Point> e = pointPositionScoreHashtable.keys(); e.hasMoreElements();){			
				Point p = e.nextElement();
				PositionScore ps = pointPositionScoreHashtable.get(p);
				//draw markers
				g.drawLine((int)p.getX(),yAxisHeight+topLeftY+3,(int)p.getX(),yAxisHeight+topLeftY-3);
				//draw markers numbers
				g.drawString(""+ps.getPosition(),(int)p.getX() - 7,yAxisHeight+topLeftY+15);
				g.setColor(Color.red);
				//draw graph - the dots
				g.fillOval((int)p.getX() - 1,(int)p.getY() - 1,4,4);
				g.setColor(Color.black);								
			}
		}
    }       	             
    public boolean isOpaque(){
        return false;
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
 		final int range = 5;//this range means how close the mouse pointer has to be near the point for the tooltip to show
 		Point p = e.getPoint();
 		boolean found = false;
 		for(int x = (int)p.getX() - range; x <= (int)p.getX() + range && !found; x++){
 			for(int y = (int)p.getY() - range; y <= (int)p.getY() + range && !found; y++){
 				PositionScore ps = pointPositionScoreHashtable.get(new Point(x,y));
 				if(ps!=null){
 					found = true;
 					label.setText("" + ps.getPosition() + "," + ps.getScore());
			        toolTip.pack();
			        toolTip.setVisible(true);			        			        
			        SwingUtilities.convertPointToScreen(p, this);			    			        
			        toolTip.setLocation(p.x+range, p.y-toolTip.getHeight()-range);			        
 				}
 			}
 		} 		 		
 	}                
}
class MyViewPort extends JViewport{
	static final long serialVersionUID = 23122007;
	ScoreGraphPane sgp;
	public MyViewPort(ScoreGraphPane sgp){
		this.sgp = sgp;
	}
    public void paintChildren(Graphics g){
        super.paintChildren(g);
		//g.setColor(Color.red);
		final int topLeftX = sgp.getTopLeftX();
		final int topLeftY = sgp.getTopLeftY();
		final Dimension parentDimension = sgp.getParentDimension();
		int yAxisHeight = (int)parentDimension.getHeight() - 75;
		yAxisHeight -= yAxisHeight%10;
		//draw y-axis
		g.drawLine(topLeftX,topLeftY,topLeftX,topLeftY+yAxisHeight);		
		DecimalFormat df = new DecimalFormat("0.##");
		g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,9));
		for(int y = 0; y < 11; y++){
			int distance = (yAxisHeight*y)/10;
			int distance2 = (((yAxisHeight*y)/10) + ((yAxisHeight*(y+1))/10)) / 2;
			if(y!=10){				
				g.drawLine(topLeftX-5,distance+topLeftY,topLeftX+5,distance+topLeftY);			
				g.drawLine(topLeftX-3,distance2+topLeftY,topLeftX+3,distance2+topLeftY);			
			}				
			g.drawString(df.format(1.0 - (y*0.10)),topLeftX - 20,distance+topLeftY+3);			
		}
    }

    public Color getBackground(){
        Component c = getView();
        return c == null ? super.getBackground() : c.getBackground();
    }        		   
}
class PositionScore{
	private int position;
	private double score;
	public PositionScore(int position,double score){
		this.position = position;
		this.score = score;
	}
	public int getPosition(){
		return this.position;
	}
	public double getScore(){
		return this.score;
	}
}