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
package sirius.misc.main;


import java.awt.*;
import java.awt.event.*;
import java.util.Random;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import sirius.misc.aggregatescorefile.AggregateScoreFilePane;
import sirius.misc.correlation.CorrelationPane;
import sirius.misc.featurevisualizer.FeatureVisualizerPane;
import sirius.misc.positionweightmatrix.PositionWeightMatrixPane;
import sirius.misc.predictionfileanalysis.PredictionFilePane;
import sirius.misc.randomizesequence.RandomizeSequencePane;
import sirius.misc.redundancyreduction.RedundancyReductionPane;
import sirius.misc.scorefileanalysis.ScoreFilePane;
import sirius.misc.sequencevisualizer.SequenceVisualizerPane;
import sirius.misc.zscore.ZscorePane;

public class MiscFrame extends JInternalFrame implements ChangeListener{
	static final long serialVersionUID = sirius.Sirius.version;
	private JTabbedPane tabbedPane;
	private ScoreFilePane scoreFilePane;
	private RedundancyReductionPane sequenceSimilarityCheckPane;
	private FeatureVisualizerPane visualizePane;
	private PositionWeightMatrixPane sequenceAnalyserPane;
	private SequenceVisualizerPane sequenceVisualizerPane;
	private PredictionFilePane	predictionFilePane;
	private RandomizeSequencePane randomizeSequencePane;
	private ZscorePane zscorePane;
	private CorrelationPane correlationPane;	
	private AggregateScoreFilePane aggregateScoreFilePane;
	
	public MiscFrame(JFrame frame){
		super("Misc",true,true,true,true);
		
		tabbedPane = new JTabbedPane();	
		tabbedPane.addChangeListener(this);
					
		visualizePane = new FeatureVisualizerPane(this,tabbedPane);
		tabbedPane.addTab("Feature Visualizer", null, visualizePane,"Feature Visualisation");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_0);
		
		sequenceVisualizerPane = new SequenceVisualizerPane(this,tabbedPane);
		tabbedPane.addTab("Sequence Visualizer", null, sequenceVisualizerPane,"Sequence Visualisation");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		
		sequenceAnalyserPane = new PositionWeightMatrixPane(this,tabbedPane);
		tabbedPane.addTab("Position Weight Matrix", null, sequenceAnalyserPane,"Position Weight Matrix");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_2);
				
		sequenceSimilarityCheckPane = new RedundancyReductionPane(this,tabbedPane);
		tabbedPane.addTab("Redundancy Reduction", null, sequenceSimilarityCheckPane,"Check for Sequence Similarity");
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_3);
				
		scoreFilePane = new ScoreFilePane(this,tabbedPane);
		tabbedPane.addTab("Score File Analysis", null, scoreFilePane,"Anaylse Score File");
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_4);
		
		this.aggregateScoreFilePane = new AggregateScoreFilePane();
		tabbedPane.addTab("Aggregate Score File", null, this.aggregateScoreFilePane,"Aggregate Score File");
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_5);
		
		predictionFilePane = new PredictionFilePane(this,tabbedPane);
		tabbedPane.addTab("Prediction Files Analysis", null, predictionFilePane,"Anaylse Prediction Files");
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_6);
		
		randomizeSequencePane = new RandomizeSequencePane(this);
		tabbedPane.addTab("Randomize Sequences", null, randomizeSequencePane,"Randomize Sequences");
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_7);
		
		zscorePane = new ZscorePane(this);
		tabbedPane.addTab("Z-Score", null, zscorePane,"Computes Z-Scores");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_8);
		
		this.correlationPane = new CorrelationPane();
		this.tabbedPane.addTab("Correlation", null, this.correlationPane,"Computes Correlation");
		this.tabbedPane.setMnemonicAt(0, KeyEvent.VK_9);
		
		//Add the tabbed pane to this panel.
		BorderLayout thisLayout = new BorderLayout();
		thisLayout.setVgap(5);
		setLayout(thisLayout);
        add(tabbedPane,BorderLayout.CENTER);
	}
	
	public void stateChanged(ChangeEvent e){		
		if(tabbedPane.getSelectedIndex() == 6){	
			Random rng = new Random();
			this.randomizeSequencePane.setKeyValue(rng.nextInt(1000));
		}
	}
}
