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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import sirius.clustering.main.*;
import sirius.misc.main.*;
import sirius.nnsearcher.main.*;
import sirius.predictor.main.*;
import sirius.trainer.main.*;


public class MainFrame extends JFrame implements ActionListener{
	static final long serialVersionUID = sirius.Sirius.version;
	public static final String version = "4.0";
	public static final String versionDate = "12th January 2011";
	
	private JMenu classifierMenu;	
	private JMenuItem trainClassifierMenuItem;
	private JMenuItem loadClassifierMenuItem;
	private JMenuItem postPredictionMenuItem;
	private JMenuItem nearestNeighborSearchMenuItem;
	private JMenuItem clusteringMenuItem;
	//private JMenuItem visualizerMenuItem;
	private JMenuItem exitClassifierMenuItem;
	
	private JMenu helpMenu;
	private JMenuItem aboutMenuItem;
	
	private JDesktopPane desktop;	
	private TrainerFrame trainerFrame;
	private PredictorFrame loaderFrame;
	private Thread oneThread;
	private MiscFrame analyserFrame;
	private NNSearchFrame nnSearchFrame;
	private ClusteringFrame clusteringFrame;
	private JFrame currentFrame;
	//private VisualizerFrame visualizerFrame;
	//private DotPlotPane dotPlotPane;
	//private PreparerFrame preparerFrame;
    public MainFrame() {    	
    	currentFrame = this;
    	setTitle("SIRIUS Prediction System Builder " + MainFrame.version);    
    	//setSize(800,600);    
    	
    	//Do this for the purpose of initialising stuff in the construtor    	
             
        // Add window listener to close properly
        this.addWindowListener
        (
            new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    MainFrame.this.windowClosed();
                }
            }
        );  
        	
       classifierMenu = new JMenu("Sequence Apps");
       classifierMenu.setDisplayedMnemonicIndex(9);
       classifierMenu.setMnemonic(KeyEvent.VK_A);     
       
       trainClassifierMenuItem = new JMenuItem("Trainer (Train Prediction Model)");
       trainClassifierMenuItem.setDisplayedMnemonicIndex(0);
       trainClassifierMenuItem.setMnemonic(KeyEvent.VK_T);
       
       loadClassifierMenuItem = new JMenuItem("Predictor (Use Prediction Model)");
       loadClassifierMenuItem.setDisplayedMnemonicIndex(0);
       loadClassifierMenuItem.setMnemonic(KeyEvent.VK_P);
       
       postPredictionMenuItem = new JMenuItem("Misc (Pre/Post Prediction Utilities)");
       postPredictionMenuItem.setDisplayedMnemonicIndex(0);
       postPredictionMenuItem.setMnemonic(KeyEvent.VK_M);
       
       this.nearestNeighborSearchMenuItem = new JMenuItem("NNSearcher (Nearest Neighbor Search)");
       this.nearestNeighborSearchMenuItem.setDisplayedMnemonicIndex(0);
       this.nearestNeighborSearchMenuItem.setMnemonic(KeyEvent.VK_N);
       
       this.clusteringMenuItem = new JMenuItem("Clustering (Cluster by features)");
       this.clusteringMenuItem.setDisplayedMnemonicIndex(0);
       this.clusteringMenuItem.setMnemonic(KeyEvent.VK_C);
       
       /*this.visualizerMenuItem = new JMenuItem("Dot Plot");
       this.visualizerMenuItem.setDisplayedMnemonicIndex(0);
       this.visualizerMenuItem.setMnemonic(KeyEvent.VK_D);*/
       
       exitClassifierMenuItem = new JMenuItem("Exit");
       exitClassifierMenuItem.setDisplayedMnemonicIndex(1);
       exitClassifierMenuItem.setMnemonic(KeyEvent.VK_X);
              
       trainClassifierMenuItem.addActionListener(this);
       loadClassifierMenuItem.addActionListener(this);
       postPredictionMenuItem.addActionListener(this);
       this.nearestNeighborSearchMenuItem.addActionListener(this);
       this.clusteringMenuItem.addActionListener(this);
       //this.visualizerMenuItem.addActionListener(this);
       exitClassifierMenuItem.addActionListener(this);
              
       classifierMenu.add(trainClassifierMenuItem);
       classifierMenu.add(loadClassifierMenuItem);
       classifierMenu.add(this.nearestNeighborSearchMenuItem);
       classifierMenu.add(this.clusteringMenuItem);
       classifierMenu.add(postPredictionMenuItem);       
       //Took this out because Dot Plot background program is in C and hence not universal.
       //classifierMenu.add(this.visualizerMenuItem);
       classifierMenu.addSeparator();
       classifierMenu.add(exitClassifierMenuItem);
       
       helpMenu = new JMenu("Help");
       helpMenu.setDisplayedMnemonicIndex(0);
       helpMenu.setMnemonic(KeyEvent.VK_H);
       
       aboutMenuItem = new JMenuItem("About");
       aboutMenuItem.setDisplayedMnemonicIndex(0);
       aboutMenuItem.setMnemonic(KeyEvent.VK_A);
       
       aboutMenuItem.addActionListener(this);
       
       helpMenu.add(aboutMenuItem);
       
       JMenuBar mb = new JMenuBar();
       mb.add(classifierMenu);
       mb.add(helpMenu);
       desktop = new BackgroundDesktopPane("nuslogo.gif");       
       desktop.setBackground(Color.white);
       setContentPane(desktop);       
       setJMenuBar(mb);
       currentFrame.setExtendedState(MAXIMIZED_BOTH);         
    }
    /**
     * Shutdown procedure when run as an application.
     */
    protected void windowClosed() {    	    	
        // Exit application.
        System.exit(0);
    }
    
    public void actionPerformed(ActionEvent ae){
    		if(ae.getSource().equals(aboutMenuItem)){
    			showAboutDialog(currentFrame);
    		}
    		else if(ae.getSource().equals(exitClassifierMenuItem)){    			
    			windowClosed();
    		}
    		else if(ae.getSource().equals(trainClassifierMenuItem)){
    		if(oneThread == null){    		    			    				
	    		oneThread = new Thread(){	      	
				public void run(){	
					try{
						MessageDialog dialog = null;										    		
		    			dialog = new MessageDialog(currentFrame,"Trainer","   Generating.. Please wait..");
		    			dialog.setLocationRelativeTo(currentFrame);
					    dialog.setVisible(true);
		    			trainerFrame = new TrainerFrame(currentFrame);        	        	
				        desktop.add(trainerFrame);				                
			        	//trainerFrame.setSize(1000,600);
				        trainerFrame.pack();
			      		trainerFrame.moveToFront();
				        trainerFrame.setVisible(true);			       		        
						oneThread = null;
						if(dialog != null)
							dialog.dispose();
						trainerFrame.setMaximum(true);	
					}catch(Exception e){
						JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
						}
					}};
		      		oneThread.setPriority(Thread.MIN_PRIORITY); // UI has most priority
		      		oneThread.start();									
			    }
	    	else{
	     		JOptionPane.showMessageDialog(this,
	     			"Cannot generate Trainer now.\n Please try again later.",
	     			"Unable to generate Trainer", JOptionPane.WARNING_MESSAGE);
	    	}				           
    	}
    	else if(ae.getSource().equals(loadClassifierMenuItem)){  
    		if(oneThread == null){    		    			    				
	    		oneThread = new Thread(){	      	
				public void run(){	
					try{
						MessageDialog dialog = null;										    		
		    			dialog = new MessageDialog(currentFrame,"Predictor","   Generating.. Please wait..");		    								   
		    			loaderFrame = new PredictorFrame(currentFrame);        	        	
				        desktop.add(loaderFrame);				                
			        	//loaderFrame.setSize(900,500);
				        loaderFrame.pack();
			      		loaderFrame.moveToFront();
				        loaderFrame.setVisible(true);			        				        				       	        
						oneThread = null;
						if(dialog != null)
							dialog.dispose();
						loaderFrame.setMaximum(true);
					}catch(Exception e){
						JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();}				
					}};
		      		oneThread.setPriority(Thread.MIN_PRIORITY); // UI has most priority
		      		oneThread.start();
			    }
	    	else{
	     		JOptionPane.showMessageDialog(this,
	     			"Cannot generate Predictor now.\n Please try again later.",
	     			"Unable to generate Predictor", JOptionPane.WARNING_MESSAGE);
	    	}		
    	}
		else if(ae.getSource().equals(postPredictionMenuItem)){  
    		if(oneThread == null){    		    			    				
	    		oneThread = new Thread(){	      	
				public void run(){	
					try{
						MessageDialog dialog = null;										    		
		    			dialog = new MessageDialog(currentFrame,"Misc","   Generating.. Please wait..");
		    			dialog.setLocationRelativeTo(currentFrame);
					    dialog.setVisible(true);
		    			analyserFrame = new MiscFrame(currentFrame);        	        	
				        desktop.add(analyserFrame);				                
				        //analyserFrame.setSize(900,500);
				        analyserFrame.pack();
				        analyserFrame.moveToFront();
				        analyserFrame.setVisible(true);			        				        				       	        
						oneThread = null;
						if(dialog != null)
							dialog.dispose();
						analyserFrame.setMaximum(true);
					}catch(Exception e){
						JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();}				
					}};
		      		oneThread.setPriority(Thread.MIN_PRIORITY); // UI has most priority
		      		oneThread.start();
			    }
	    	else{
	     		JOptionPane.showMessageDialog(this,
	     			"Cannot generate Analyser now.\n Please try again later.",
	     			"Unable to generate Analyser", JOptionPane.WARNING_MESSAGE);
	    	}				         
    	}else if(ae.getSource().equals(this.nearestNeighborSearchMenuItem)){  
    		if(oneThread == null){    		    			    				
	    		oneThread = new Thread(){	      	
				public void run(){	
					try{
						MessageDialog dialog = null;										    		
		    			dialog = new MessageDialog(currentFrame,"NNSearcher","   Generating.. Please wait..");
		    			dialog.setLocationRelativeTo(currentFrame);
					    dialog.setVisible(true);
					    nnSearchFrame = new NNSearchFrame(currentFrame);        	        	
				        desktop.add(nnSearchFrame);				                
				        //nnSearchFrame.setSize(900,500);
				        nnSearchFrame.pack();
				        nnSearchFrame.moveToFront();
				        nnSearchFrame.setVisible(true);			        				        				       	        
						oneThread = null;
						if(dialog != null)
							dialog.dispose();
						nnSearchFrame.setMaximum(true);
					}catch(Exception e){
						JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();}				
					}};
		      		oneThread.setPriority(Thread.MIN_PRIORITY); // UI has most priority
		      		oneThread.start();
			    }
	    	else{
	     		JOptionPane.showMessageDialog(this,
	     			"Cannot generate NNSearch now.\n Please try again later.",
	     			"Unable to generate NNSearch", JOptionPane.WARNING_MESSAGE);
	    	}				         
    	}else if(ae.getSource().equals(this.clusteringMenuItem)){
    		if(oneThread == null){    		    			    				
	    		oneThread = new Thread(){	      	
				public void run(){	
					try{
						MessageDialog dialog = null;										    		
		    			dialog = new MessageDialog(currentFrame,"Clustering","   Generating.. Please wait..");
		    			dialog.setLocationRelativeTo(currentFrame);
					    dialog.setVisible(true);
					    clusteringFrame = new ClusteringFrame(currentFrame);        	        	
				        desktop.add(clusteringFrame);				                
				        //clusteringFrame.setSize(900,500);	
				        clusteringFrame.pack();
				        clusteringFrame.moveToFront();
				        clusteringFrame.setVisible(true);			        				        				       	        
						oneThread = null;
						if(dialog != null)
							dialog.dispose();
						clusteringFrame.setMaximum(true);
					}catch(Exception e){
						JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();}				
					}};
		      		oneThread.setPriority(Thread.MIN_PRIORITY); // UI has most priority
		      		oneThread.start();
			    }
	    	else{
	     		JOptionPane.showMessageDialog(this,
	     			"Cannot generate Clustering now.\n Please try again later.",
	     			"Unable to generate Clustering", JOptionPane.WARNING_MESSAGE);
	    	}				         
    	}
    		
    		/*else if(ae.getSource().equals(this.visualizerMenuItem)){  
    		if(oneThread == null){    		    			    				
	    		oneThread = new Thread(){	      	
				public void run(){	
					try{
						MessageDialog dialog = null;										    		
		    			dialog = new MessageDialog(currentFrame,"Dot Plot");
		    			dialog.setLocationRelativeTo(currentFrame);
					    dialog.setVisible(true);
					    dotPlotPane = new DotPlotPane(currentFrame);        	        	
				        desktop.add(dotPlotPane);				                
				        dotPlotPane.setSize(900,500);			        	
				        dotPlotPane.moveToFront();
				        dotPlotPane.setVisible(true);			        				        				       	        
						oneThread = null;
						if(dialog != null)
							dialog.dispose();
						dotPlotPane.setMaximum(true);
					}catch(Exception e){
						JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();}				
					}};
		      		oneThread.setPriority(Thread.MIN_PRIORITY); // UI has most priority
		      		oneThread.start();
			    }
	    	else{
	     		JOptionPane.showMessageDialog(this,
	     			"Cannot generate NNSearch now.\n Please try again later.",
	     			"Unable to generate NNSearch", JOptionPane.WARNING_MESSAGE);
	    	}
    	}*/
    }
    
    private void showAboutDialog(Frame currentFrame){    	
    	AboutDialog aboutDialog = new AboutDialog(currentFrame);
    	aboutDialog.setVisible(true);
    }
}

class AboutDialog extends JDialog{
	static final long serialVersionUID = 22062008;
	public AboutDialog(Frame currentFrame){
		super(new JFrame(), "About Sirius Prediction System Builder");
		setLayout(new GridLayout(3,1));
		//setSize(400,120);
		JLabel messageLabel = new JLabel("Version " + MainFrame.version + ", " + MainFrame.versionDate,SwingConstants.CENTER);
		add(messageLabel);
		JLabel messageLabel2 = new JLabel("We welcome all questions, suggestions, feedback and bugs report.",SwingConstants.CENTER);
		add(messageLabel2);
		JLabel messageLabel3 = new JLabel("Email: kohchuanhock@gmail.com",SwingConstants.CENTER);
		add(messageLabel3);		
		setVisible(true);
		this.pack();
		setLocationRelativeTo(currentFrame);
	}
}

class BackgroundDesktopPane extends JDesktopPane {

	/** for serialization */
	private static final long serialVersionUID = 2046713123452402745L;
	
	/** the actual background image */
	protected Image img;

	/**
	 * intializes the desktop pane
	 * 
	 * @param image	the image to use as background
	 */
	public BackgroundDesktopPane(String image) {
	  super();
	  
	  try{
		  img = Toolkit.getDefaultToolkit().getImage("./" + image);
	  }
	  catch (Exception e){ 
		  e.printStackTrace();
	  }
	}

	/**
	 * draws the background image
	 * 
	 * @param g		the graphics context
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		int width  = img.getWidth(null);
		int height = img.getHeight(null);
		int x = (getWidth() - width) / 2;
		int y = (getHeight() - height) / 2;
		g.drawImage(img, x, y, width, height, this);
	}
}
