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
package sirius.trainer.features.subsetselection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import sirius.main.ApplicationData;
import sirius.trainer.features.Feature;
import sirius.trainer.features.GenerateFeatures;
import sirius.trainer.step2.FeatureTableModel;
import sirius.trainer.step4.RunClassifierWithNoLocationIndex;
import sirius.utils.PredictionStats;
import weka.core.Instances;

public class GreedyForwardSubsetSearch implements SubsetSelection{
	
	@Override
	public List<Feature> selectSubset(List<Feature> wholeList, String outputFileLocation, ApplicationData appData) throws Exception{
		//Based on the wholeList, starts with a empty list and add one Feature at a time for the wholeList
		//If the MCC value is increased then keep the Feature else remove it	
		//Train on Dataset1 and Test on Dataset3 in appData			
		String classifierName = "weka.classifiers.meta.AttributeSelectedClassifier";
		String[] classifierOptions = new String[13];		
		BufferedWriter output = new BufferedWriter(new FileWriter(outputFileLocation));
		List<Feature> selectedList = new ArrayList<Feature>();
		HashSet<Integer> selectedIndex = new HashSet<Integer>();		
		double lastMCC = 0.0;		
		FeatureTableModel currentFeatureTableModel = new FeatureTableModel(false);
		for(int x = 0; x < wholeList.size(); x++){
			currentFeatureTableModel.add(wholeList.get(x));
		}
		appData.setStep2FeatureTableModel(currentFeatureTableModel);
		new GenerateFeatures(null,appData,null,null, null, null);
		//Wait till GenerateFeatures is finished
		while(appData.getOneThread() != null){
			try{
				Thread.sleep(1000);
			}catch(Exception e){}
		}		
		
		for(int x = 0; x < wholeList.size(); x++){
			Instances currentInstances = new Instances(new BufferedReader(new FileReader(
					appData.getWorkingDirectory() + File.separator + "Dataset1.arff")));
			for(int y = wholeList.size() - 1; y >= 0; y--){
				if(selectedIndex.contains(y) == false && y != x)
					currentInstances.deleteAttributeAt(y);
			}						
			appData.setDataset1Instances(currentInstances);			
			classifierOptions[0] = "-E";
			classifierOptions[1] = "weka.attributeSelection.GainRatioAttributeEval";
			classifierOptions[2] = "-S";
			classifierOptions[3] = "weka.attributeSelection.Ranker -T 0.0 -N -1";
			classifierOptions[4] = "-W";
			classifierOptions[5] = "weka.classifiers.trees.RandomForest";
			classifierOptions[6] = "--";
			classifierOptions[7] = "-I";
			classifierOptions[8] = "1000";//10, 100, 1000
			classifierOptions[9] = "-K";
			classifierOptions[10] = "0";
			classifierOptions[11] = "-S";
			classifierOptions[12] = "1";				
			double MCC1000 = ((PredictionStats)RunClassifierWithNoLocationIndex.startClassifierOneWithNoLocationIndex(
					null,appData,null,null,true,null,0,0.5,classifierName,classifierOptions,false, null,
					new Random().nextInt())).getMaxMCC();	
			while(appData.getOneThread() != null){
				try{
					Thread.sleep(1000);
				}catch(Exception e){}
			}	
			output.write("" + MCC1000);
			output.newLine();
			output.flush();
			if(MCC1000 > lastMCC){
				selectedList.add(wholeList.get(x));
				selectedIndex.add(x);		
				lastMCC = MCC1000;
			}		
		}
		output.close();
		System.out.println(lastMCC);
		return selectedList;
	}

}
