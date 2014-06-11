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
package sirius.misc.zscore;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import sirius.MessageDialog;
import sirius.trainer.features.Feature;

import weka.attributeSelection.GainRatioAttributeEval;
import weka.core.Instances;

public class ZscoreTableModel extends AbstractTableModel{
	static final long serialVersionUID = sirius.Sirius.version;
	private String[] columnNames = {"No.","Attribute", "(+ve) Mean","(+ve) Std Dev", "(-ve) Mean", "(-ve) Std Dev", "Mean Z-Score", 
			"% > Z-Score(0.5)","% > Z-Score(1)", "% > Z-Score(2)", "% > Z-Score(3)", "Gain Ratio"};
	private List<Scores> scoreList;
	private Instances posInstances;
	private Instances negInstances;
	private Instances originalPosInstances;
	private Instances originalNegInstances;
	private JLabel label;
		
	public ZscoreTableModel(JLabel label){
		this.label = label;
	}
	
	public void setOriginalPosInstances(Instances original,boolean pos){
		if(pos)
			this.originalPosInstances = original;
		else
			this.originalNegInstances = original;
	}
	
	public void reset(){
		if(this.originalPosInstances != null && this.originalNegInstances != null)
			compute(new Instances(this.originalPosInstances),new Instances(this.originalNegInstances));
	}
	
	@Override
	public int getColumnCount() {
		return this.columnNames.length;
	}

	@Override
	public int getRowCount() {
		if(this.scoreList == null)
			return 0;
		else
			return this.scoreList.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		DecimalFormat df = new DecimalFormat("#.#######");
		switch(columnIndex){
		case 0: return rowIndex+1;
		case 1: return this.scoreList.get(rowIndex).getName();
		case 2: return df.format(this.scoreList.get(rowIndex).getPosMean());
		case 3: return df.format(this.scoreList.get(rowIndex).getPosStdDev());
		case 4: return df.format(this.scoreList.get(rowIndex).getNegMean());
		case 5: return df.format(this.scoreList.get(rowIndex).getNegStdDev());
		case 6: return df.format(this.scoreList.get(rowIndex).getMeanZScore());
		case 7: return df.format(this.scoreList.get(rowIndex).getPercentGTZScore0_5());
		case 8: return df.format(this.scoreList.get(rowIndex).getPercentGTZScore1());
		case 9: return df.format(this.scoreList.get(rowIndex).getPercentGTZScore2());
		case 10: return df.format(this.scoreList.get(rowIndex).getPercentGTZScore3());
		case 11: return df.format(this.scoreList.get(rowIndex).getGainRatio());
		}
		return "Error";
	}
	
	public void siriusCorrelationFiltering(final double stdDevDist, final double maxOverlapPercent,final boolean includeNegatives){		
		Thread thread = new Thread(){	      	
			public void run(){					
				Instances instances = ZscoreTableModel.this.posInstances;
				if(includeNegatives)
					for(int x = 0; x < ZscoreTableModel.this.negInstances.numInstances();x++)
						instances.add(ZscoreTableModel.this.negInstances.instance(x));									
				//for now, i will ignore the sign: as in, i would care only about the absolute change of stddev (ie. |stddev|)
				//use an O(a*a*n) algorithm where n = num of instances and a = num of attributes	
				MessageDialog m = new MessageDialog(null, "Progress", "0%");				
				for(int a = 0; a < instances.numAttributes(); a++){							
					int indexA = instances.attribute(ZscoreTableModel.this.scoreList.get(a).getName()).index();					
					if(instances.attribute(indexA).isNumeric() == false)
						continue;
					//for each attribute pair, check for the num of overlap percent					
					double attibuteAStddev = instances.attributeStats(indexA).numericStats.stdDev;
					for(int b = a+1; b < instances.numAttributes();){				
						m.update(a + "/" + instances.numAttributes());
						int indexB = instances.attribute(ZscoreTableModel.this.scoreList.get(b).getName()).index();
						if(instances.attribute(indexB).isNumeric() == false){
							b++;
							continue;
						}
						int numOfOverlap = 0;
						double attibuteBStddev = instances.attributeStats(indexB).numericStats.stdDev;
						for(int x = 0; x < instances.numInstances() - 1; x++){								
							//how do i consider an overlap?
							//absolute difference from the previous instance is same in stddev
							double attributeADifference = Math.abs(((instances.instance(x).value(indexA) - instances.instance(x+1).value(indexA))/attibuteAStddev));
							double attributeBDifference = Math.abs(((instances.instance(x).value(indexB) - instances.instance(x+1).value(indexB))/attibuteBStddev));
							if(Math.abs(attributeADifference - attributeBDifference) < stdDevDist)
								numOfOverlap++;
						}
						double overlapPercent = (numOfOverlap * 100) / (instances.numInstances() - 1);				
						if(overlapPercent > maxOverlapPercent){
							ZscoreTableModel.this.posInstances.deleteAttributeAt(indexB);
							ZscoreTableModel.this.negInstances.deleteAttributeAt(indexB);							
							ZscoreTableModel.this.scoreList.remove(b);
							indexA = instances.attribute(ZscoreTableModel.this.scoreList.get(a).getName()).index();
						}else
							b++;
					}
				}
				m.dispose();
				ZscoreTableModel.this.label.setText("" + instances.numAttributes());
				//compute(ZscoreTableModel.this.posInstances,ZscoreTableModel.this.negInstances);
				ZscoreTableModel.this.fireTableDataChanged();
			}};
		thread.setPriority(Thread.MIN_PRIORITY); // UI has most priority
		thread.start();			
	}
	
	public void pearsonCorrelationFiltering(final double score, final boolean includeNegatives){		
		Thread thread = new Thread(){	      	
			public void run(){					
				Instances instances = ZscoreTableModel.this.posInstances;
				if(includeNegatives)
					for(int x = 0; x < ZscoreTableModel.this.negInstances.numInstances();x++)
						instances.add(ZscoreTableModel.this.negInstances.instance(x));									
				//for now, i will ignore the sign: as in, i would care only about the absolute change of stddev (ie. |stddev|)
				//use an O(a*a*n) algorithm where n = num of instances and a = num of attributes
				MessageDialog m = new MessageDialog(null, "Progress", "0%");				
				for(int a = 0; a < instances.numAttributes(); a++){									
					int indexA = instances.attribute(ZscoreTableModel.this.scoreList.get(a).getName()).index();					
					if(instances.attribute(indexA).isNumeric() == false)
						continue;
					//for each attribute pair, check for the num of overlap percent					
					double attributeAStddev = instances.attributeStats(indexA).numericStats.stdDev;
					double attributeAMean = instances.attributeStats(indexA).numericStats.mean;
					for(int b = a+1; b < instances.numAttributes();){							
						m.update(a + "/" + instances.numAttributes());
						int indexB = instances.attribute(ZscoreTableModel.this.scoreList.get(b).getName()).index();
						if(instances.attribute(indexB).isNumeric() == false){
							b++;
							continue;						
						}
						double attributeBStddev = instances.attributeStats(indexB).numericStats.stdDev;
						double attributeBMean = instances.attributeStats(indexB).numericStats.mean;
						double nominator = 0.0;
						for(int x = 0; x < instances.numInstances(); x++){					
							nominator += ((instances.instance(x).value(indexA) - attributeAMean) * (instances.instance(x).value(indexB) - attributeBMean));							
						}						
						double pScore = Math.abs(nominator / ((instances.numInstances() - 1) * attributeAStddev * attributeBStddev));
						if(pScore > score){
							ZscoreTableModel.this.posInstances.deleteAttributeAt(indexB);
							ZscoreTableModel.this.negInstances.deleteAttributeAt(indexB);
							ZscoreTableModel.this.scoreList.remove(b);
							indexA = instances.attribute(ZscoreTableModel.this.scoreList.get(a).getName()).index();
						}else
							b++;
					}
				}
				m.dispose();
				ZscoreTableModel.this.label.setText("" + instances.numAttributes());
				//compute(ZscoreTableModel.this.posInstances,ZscoreTableModel.this.negInstances);
				ZscoreTableModel.this.fireTableDataChanged();
			}};
		thread.setPriority(Thread.MIN_PRIORITY); // UI has most priority
		thread.start();			
	}

	public void compute(final Instances posInstances, final Instances negInstances){
		if(posInstances == null || negInstances == null){
			JOptionPane.showMessageDialog(null, "Please load file before computing.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(posInstances.numAttributes() != negInstances.numAttributes()){
			JOptionPane.showMessageDialog(null, "Number of attributes between the two files does not tally.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		this.scoreList = new ArrayList<Scores>();
		this.posInstances = posInstances;
		this.negInstances = negInstances;				
		Thread thread = new Thread(){	      	
			public void run(){	
				MessageDialog m = new MessageDialog(null, "Progress", "0%");
				int percentCount = posInstances.numAttributes() / 100;
				if(percentCount == 0)
					percentCount = 1;
				for(int x = 0; x < posInstances.numAttributes(); x++){
					if(x % percentCount == 0)
						m.update(x / percentCount + "%");
					if(posInstances.attribute(x).isNumeric() == false){
						ZscoreTableModel.this.scoreList.add(new Scores(posInstances.attribute(x).name()));
						continue;
					}
					String name = posInstances.attribute(x).name();
					double posMean = posInstances.attributeStats(x).numericStats.mean;
					double posStdDev = posInstances.attributeStats(x).numericStats.stdDev;
					double negMean = negInstances.attributeStats(x).numericStats.mean;
					double negStdDev = negInstances.attributeStats(x).numericStats.stdDev;
					if(negStdDev == 0)
						negStdDev = 0.01;
					double totalZScore = 0.0;
					int numGTZScore0_5 = 0;
					int numGTZScore1 = 0;
					int numGTZScore2 = 0;
					int numGTZScore3 = 0;
					for(int y = 0; y < posInstances.numInstances(); y++){
						double zScore = Math.abs(((posInstances.instance(y).value(x) - negMean) / negStdDev)); 
						totalZScore += zScore;
						if(zScore > 0.5)
							numGTZScore0_5++;
						if(zScore > 1)
							numGTZScore1++;
						if(zScore > 2)
							numGTZScore2++;
						if(zScore > 3)
							numGTZScore3++;
					}
					double meanZScore = totalZScore / posInstances.numInstances();
					double percentGTZScore0_5 = (numGTZScore0_5 * 100) / posInstances.numInstances();
					double percentGTZScore1 = (numGTZScore1 * 100) / posInstances.numInstances();
					double percentGTZScore2 = (numGTZScore2 * 100) / posInstances.numInstances();
					double percentGTZScore3 = (numGTZScore3 * 100) / posInstances.numInstances();					
					ZscoreTableModel.this.scoreList.add(new Scores(name, posMean, posStdDev, negMean, negStdDev, meanZScore,percentGTZScore0_5, percentGTZScore1,
							percentGTZScore2, percentGTZScore3, -1));
				}	
				try{
					Instances instances =  new Instances(posInstances);
					for(int x = 0; x < negInstances.numInstances(); x++)
						instances.add(negInstances.instance(x));
					instances.setClassIndex(instances.numAttributes() - 1);
					//Evaluate the attributes individually and obtain the gainRatio		
					GainRatioAttributeEval gainRatio = new GainRatioAttributeEval();
					if(instances.numAttributes() > 0){
						gainRatio.buildEvaluator(instances);				
					}
					for(int x = 0; x < (instances.numAttributes() - 1); x++){				
						ZscoreTableModel.this.scoreList.get(x).setGainRatio(gainRatio.evaluateAttribute(x));
					}
				}catch(Exception e){e.printStackTrace();}
				Collections.sort(ZscoreTableModel.this.scoreList, new SortByMeanZScore());
				fireTableDataChanged();
				m.dispose();			
				ZscoreTableModel.this.label.setText("" + ZscoreTableModel.this.scoreList.size());
			}};
		thread.setPriority(Thread.MIN_PRIORITY); // UI has most priority
		thread.start();		
	}
	
	public void sort(int index){
		if(this.scoreList == null || this.scoreList.size() == 0)
			return;
		switch(index){		
			case 0: Collections.sort(this.scoreList, new SortByName()); break;
			case 1: Collections.sort(this.scoreList, new SortByMeanZScore()); break;
			case 2: Collections.sort(this.scoreList, new SortByPercentGTZScore0_5()); break;
			case 3: Collections.sort(this.scoreList, new SortByPercentGTZScore1()); break;
			case 4: Collections.sort(this.scoreList, new SortByPercentGTZScore2()); break;
			case 5: Collections.sort(this.scoreList, new SortByPercentGTZScore3()); break;
			case 6: Collections.sort(this.scoreList, new SortByGainRatio()); break;
			default: throw new Error("Unhandled sorting case");
		}		
		this.fireTableDataChanged();
	}
	
	public String getColumnName(int col) {
        return columnNames[col];
    }
	
	public boolean save(String fileLocation, int topX){
		try{
			BufferedWriter output = new BufferedWriter(new FileWriter(fileLocation));
			for(int x = 0; (topX == -1 || x < topX) && x < this.scoreList.size(); x++){
				if(this.scoreList.get(x).getMeanZScore() == 0)
					continue;
				output.write("Step 2: " + this.scoreList.get(x).saveString());
				output.newLine();
			}
			output.close();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}		
	}
}

class Scores{
	private String name;
	private double posMean;
	private double posStdDev;
	private double negMean;	
	private double negStdDev;
	private double meanZscore;
	private double percentGTZscore0_5;
	private double percentGTZscore1;
	private double percentGTZscore2;
	private double percentGTZscore3;
	private double gainRatio;
	
	public Scores(String name, double posMean, double posStdDev, double negMean, double negStdDev, double meanZscore, 
			double percentGTZscore0_5, double percentGTZscore1, double percentGTZscore2,double percentGTZscore3, double gainRatio){
		this.name = name;
		this.posMean = posMean;
		this.posStdDev = posStdDev;
		this.negMean = negMean;
		this.negStdDev = negStdDev;
		this.meanZscore = meanZscore;
		this.percentGTZscore0_5 = percentGTZscore0_5;
		this.percentGTZscore1 = percentGTZscore1;
		this.percentGTZscore2 = percentGTZscore2;
		this.percentGTZscore3 = percentGTZscore3;
		this.gainRatio = gainRatio;
		
	}
	
	public Scores(String name){
		this.name = name;
	}
	
	
	public void setGainRatio(double gainRatio){this.gainRatio = gainRatio;}
	public String saveString(){ return Feature.saveFeatureViaName(this.name);}
	public String getName(){return this.name;}
	public double getPosMean(){return this.posMean;}
	public double getPosStdDev(){return this.posStdDev;}
	public double getNegMean(){return this.negMean;}
	public double getNegStdDev(){return this.negStdDev;}
	public double getMeanZScore(){return this.meanZscore;}
	public double getPercentGTZScore0_5(){return this.percentGTZscore0_5;}
	public double getPercentGTZScore1(){return this.percentGTZscore1;}
	public double getPercentGTZScore2(){return this.percentGTZscore2;}
	public double getPercentGTZScore3(){return this.percentGTZscore3;}
	public double getGainRatio(){return this.gainRatio;}
}

class SortByName implements Comparator<Scores>{
	@Override
	public int compare(Scores o1, Scores o2) {
		return o1.getName().compareTo(o2.getName()); 	
	}	
}

class SortByMeanZScore implements Comparator<Scores>{
	@Override
	public int compare(Scores o1, Scores o2) {
		if(o1.getMeanZScore() > o2.getMeanZScore())
			return -1;
		else if(o1.getMeanZScore() < o2.getMeanZScore())
			return 1;	
		else 
			return 0;
	}	
}

class SortByPercentGTZScore0_5 implements Comparator<Scores>{
	@Override
	public int compare(Scores o1, Scores o2) {
		if(o1.getPercentGTZScore0_5() > o2.getPercentGTZScore0_5())
			return -1;
		else if(o1.getPercentGTZScore0_5() < o2.getPercentGTZScore0_5())
			return 1;	
		else 
			return 0;
	}	
}

class SortByPercentGTZScore1 implements Comparator<Scores>{
	@Override
	public int compare(Scores o1, Scores o2) {
		if(o1.getPercentGTZScore1() > o2.getPercentGTZScore1())
			return -1;
		else if(o1.getPercentGTZScore1() < o2.getPercentGTZScore1())
			return 1;	
		else 
			return 0;
	}	
}

class SortByPercentGTZScore2 implements Comparator<Scores>{
	@Override
	public int compare(Scores o1, Scores o2) {
		if(o1.getPercentGTZScore2() > o2.getPercentGTZScore2())
			return -1;
		else if(o1.getPercentGTZScore2() < o2.getPercentGTZScore2())
			return 1;	
		else 
			return 0;
	}	
}

class SortByPercentGTZScore3 implements Comparator<Scores>{
	@Override
	public int compare(Scores o1, Scores o2) {
		if(o1.getPercentGTZScore3() > o2.getPercentGTZScore3())
			return -1;
		else if(o1.getPercentGTZScore3() < o2.getPercentGTZScore3())
			return 1;	
		else 
			return 0;
	}	
}

class SortByGainRatio implements Comparator<Scores>{
	@Override
	public int compare(Scores o1, Scores o2) {
		if(o1.getGainRatio() > o2.getGainRatio())
			return -1;
		else if(o1.getGainRatio() < o2.getGainRatio())
			return 1;	
		else 
			return 0;
	}	
}