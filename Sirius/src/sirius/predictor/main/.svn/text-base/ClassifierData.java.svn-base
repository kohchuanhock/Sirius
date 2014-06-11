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


import sirius.trainer.main.*;
import sirius.trainer.step3.*;
import weka.core.*;

import weka.classifiers.*;

public class ClassifierData {
	int classifierType;
	String classifierName;
	Instances instances;
	Instances instances2;
	Classifier classifierOne;
	Classifier classifierTwo;
	String classifierOneSettings;
	String classifierTwoSettings;
	int setUpstream;
	int setDownstream;
	int leftMostPosition;
	int rightMostPosition;
	String sequenceType;
	int scoringMatrixIndex;
	int countingStyleIndex;
	ScoringMatrix scoringMatrix;
	
	public ClassifierData(int classifierType,String classifierName,Instances instances,
			Classifier classifierOne,
		Classifier classifierTwo,String classifierOneSettings,
		String classifierTwoSettings,int setUpstream,
		int setDownstream,Instances instances2,String sequenceType,
		int scoringMatrixIndex,
		int countingStyleIndex, boolean checkBoundary){
		this.classifierType = classifierType;
		this.classifierName = classifierName;
		this.instances = instances;
		this.classifierOne = classifierOne;
		this.classifierTwo = classifierTwo;
		this.classifierOneSettings = classifierOneSettings;
		this.classifierTwoSettings = classifierTwoSettings;
		this.setUpstream = setUpstream;
		this.setDownstream = setDownstream;
		this.instances2 = instances2;
		
		this.sequenceType = sequenceType;
		this.scoringMatrixIndex = scoringMatrixIndex;
		this.countingStyleIndex = countingStyleIndex;
		
		if(checkBoundary) getBoundary();		
		this.scoringMatrix = new ScoringMatrix();
	}
	
	public ClassifierData(int classifierType,String classifierName,Instances instances,
			Classifier classifierOne,
		Classifier classifierTwo,String classifierOneSettings,
		String classifierTwoSettings,int setUpstream,
		int setDownstream,Instances instances2,String sequenceType,
		int scoringMatrixIndex,
		int countingStyleIndex){
		this(classifierType,classifierName,instances,classifierOne,
				classifierTwo,classifierOneSettings,
				classifierTwoSettings,setUpstream,
				setDownstream,instances2,sequenceType,
				scoringMatrixIndex,countingStyleIndex, true);
	}
	private void getBoundary(){
		int[] boundaryPosition = SelectFeaturePane.findBoundary(instances,null);
		leftMostPosition = boundaryPosition[0];
		rightMostPosition = boundaryPosition[1];
	}
	public String getSequenceType(){
		return this.sequenceType;
	}
	public ScoringMatrix getScoringMatrix(){
		return this.scoringMatrix;
	}
	
	public int getScoringMatrixIndex(){
		return this.scoringMatrixIndex;
	}
	public int getCountingStyleIndex(){
		return this.countingStyleIndex;
	}
	public int getClassifierType(){
		return classifierType;
	}		
	public String getClassifierName(){
		return classifierName;
	}
	public Instances getInstances(){
		return instances;
	}	
	public Instances getInstances2(){
		return instances2;
	}	
	public Classifier getClassifierOne(){
		return classifierOne;
	}	
	public Classifier getClassifierTwo(){
		return classifierTwo;	
	}
	public String getClassifierOneSettings(){
		return classifierOneSettings;
	}
	public String getClassifierTwoSettings(){
		return classifierTwoSettings;
	}
	public int getSetUpstream(){
		return setUpstream;
	}
	public int getSetDownstream(){
		return setDownstream;
	}
	public int getLeftMostPosition(){
		return leftMostPosition;
	}
	public int getRightMostPosition(){
		return rightMostPosition;
	}
	
	public Object get(int col){
		if(col == 0)
			return new Integer(classifierType);
		else if(col == 1)
			return classifierName;
		else if(col == 2)
			//-1 to reduce away class attribute
			return (instances.numAttributes() - 1) + " [" + leftMostPosition + "," + rightMostPosition + "]";
		return null;
	}
}