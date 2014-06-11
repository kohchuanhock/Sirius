package sirius.tll;

import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileNameExtensionFilter;

import sirius.utils.Arff;
import sirius.utils.Graph;
import sirius.utils.R;
import sirius.utils.RPlotGraph;
import sirius.utils.Utils;
import weka.core.Instances;

public class Greg {
	public static void arff2Graphs(String arffFile, String arffFile2, String outputDir, String nameSuffix,String xLabel, 
			int numOfInstancesToInclude, int averageWindow, int stepSize){		
		/*
		 * Plot all the features into the feature Space
		 */
		String[] featureNameString = new String[11];
		featureNameString[0] = "Mean hydrophobicity";
		featureNameString[1] = "Net charge";
		featureNameString[2] = "Absolute net charge";
		featureNameString[3] = "Composition of R (arginine)";
		featureNameString[4] = "Mean net charge";
		featureNameString[5] = "Absolute mean net charge";
		featureNameString[6] = "Composition of Ordered Amino Acids";
		featureNameString[7] = "Composition of F (phenylalanine)";
		featureNameString[8] = "Composition of C (cystine)";
		featureNameString[9] = "Difference between Ordered and Disordered Amino Acids";		
		Instances instances = Arff.getAsInstances(arffFile);
		R r = new R();
		List<Double> xValueList = new ArrayList<Double>();		
		List<String> xStringList = new ArrayList<String>();				
		
		int stringValue = 0;
		for(int x = 0; x < instances.numInstances() && 
			(numOfInstancesToInclude == -1 || x < numOfInstancesToInclude); x += (stepSize * averageWindow), 
			stringValue += (stepSize * averageWindow)){			
			//System.out.println(x + 1);
			xStringList.add(stringValue + 1 + "");
		}
		
		int intValue = 0;
		for(int x = 0; x < instances.numInstances() / averageWindow && 
		(numOfInstancesToInclude == -1 || x < numOfInstancesToInclude); x += stepSize, intValue += stepSize){
			xValueList.add(intValue + 1.0);
		}	
		
		List<Double> xValueList2 = new ArrayList<Double>();
		Instances instances2 = null;
		if(arffFile2 != null){
			instances2 = Arff.getAsInstances(arffFile2);
			for(int x = 0; x < instances2.numInstances() && 
				(numOfInstancesToInclude == -1 || x < numOfInstancesToInclude); x += (stepSize * averageWindow), 
				stringValue += (stepSize * averageWindow)){			
				//System.out.println(x + 1);
				xStringList.add(stringValue + 1 + "");
			}
			
			for(int x = 0; x < instances2.numInstances() / averageWindow && 
			(numOfInstancesToInclude == -1 || x < numOfInstancesToInclude); x += stepSize, intValue += stepSize){
				xValueList2.add(intValue + 1.0);
			}	
		}
						
		
		//-1 because last attribute is class and there is no need to plot the class
		for(int x = 0; x < instances.numAttributes() - 1; x++){
			List<Double> yValueList = new ArrayList<Double>();
			for(int y = 0; y < instances.numInstances() && (numOfInstancesToInclude == -1 || y < numOfInstancesToInclude);){
				double value = 0.0;
				for(int z = 0; z < averageWindow && y < instances.numInstances(); z += stepSize, y += stepSize){
					value += instances.instance(y).value(x);
				}
				yValueList.add(value/averageWindow);
			}
			if(arffFile2 == null){
				//String name = x + "_" + instances.attribute(x).name();
				String name = featureNameString[x];			
				r.runCode(RPlotGraph.plotGraph(new Graph(name + " (" + nameSuffix + ")", xLabel, name, xValueList, yValueList), 
						outputDir + name + "_" + nameSuffix + ".png", xStringList, false, false));
				System.out.println("Plotted " + name + " (" + nameSuffix + ")" + ".png");
			}else{				
				
				List<Double> yValueList2 = new ArrayList<Double>();
				for(int y = 0; y < instances2.numInstances() && (numOfInstancesToInclude == -1 || y < numOfInstancesToInclude);){
					double value = 0.0;
					for(int z = 0; z < averageWindow && y < instances2.numInstances(); z += stepSize, y += stepSize){
						value += instances2.instance(y).value(x);
					}
					yValueList2.add(value/averageWindow);
				}
				
				String name = featureNameString[x];
				List<Graph> graphList = new ArrayList<Graph>();
				graphList.add(new Graph("Top 300", xLabel, name, xValueList, yValueList));
				graphList.add(new Graph("The rest", xLabel, name, xValueList2, yValueList2));				
				System.out.println(xValueList.size() + " / " + xValueList2.size() + " / " + xStringList.size());
				r.runCode(RPlotGraph.plotGraphs(graphList, outputDir + name + "_" + nameSuffix + ".png", name + " (" + nameSuffix + ")", 
						xStringList, false, false));
				System.out.println("Plotted " + name + " (" + nameSuffix + ")" + ".png");
			}
		}	
	}
	
	public static void main(String[] args){
		String arffFile = Utils.selectFile("Select Arff File to Plot", new FileNameExtensionFilter("Arff file", "arff"));
		String arffFile2 = Utils.selectFile("Select Arff File2 to Plot", new FileNameExtensionFilter("Arff file", "arff"));
		String outputDir = Utils.getDirOfFile(arffFile);
		//arff2Graphs(arffFile, outputDir, "Full", "Ranked", -1, 1, 1);
		//arff2Graphs(arffFile, outputDir, "Step 10", "Ranked", -1, 1, 10);
		arff2Graphs(arffFile, arffFile2, outputDir, "Average In Group Of 10", "Ranked (Average in group of 10)", -1, 10, 1);
		//arff2Graphs(arffFile, outputDir, "First 500", "Ranked (First 500)", 500, 1, 1);
		//arff2Graphs(arffFile, outputDir, "First 1000", "Ranked (First 1000)", 1000, 1, 1);
	}
}
