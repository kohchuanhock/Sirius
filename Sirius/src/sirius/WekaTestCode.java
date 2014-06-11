package sirius;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.JFileChooser;

import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

/*
 * Prepared especially for Mengling
 */
public class WekaTestCode {
	public static void main(String args[]){
		try{
			/*
			 * Loading
			 */
			Instances train = loadArffFile("Please select training file");
			//Assumes that the last attribute is the class label
			train.setClassIndex(train.numAttributes() - 1);
			//Note that the training and validation file MUST have the same number of attributes
			Instances test = loadArffFile("Please select validation file");
			//Likewise, we assume that the last attribute is the class label
			test.setClassIndex(test.numAttributes() - 1);
			
			/*
			 * The following codes are googled and mainly copied from 
			 * http://weka.wikispaces.com/Use+WEKA+in+your+Java+code
			 */
			// filter
			 Remove rm = new Remove();
			 rm.setAttributeIndices("1");  // remove 1st attribute
			 // classifier
			 J48 j48 = new J48();
			 j48.setUnpruned(true);        // using an unpruned J48
			 // meta-classifier
			 FilteredClassifier fc = new FilteredClassifier();
			 fc.setFilter(rm);
			 fc.setClassifier(j48);
			 // train and make predictions
			 fc.buildClassifier(train);
			 int correctPredictions = 0;
			 int incorrectPredictions = 0;
			 for (int i = 0; i < test.numInstances(); i++) {
			   double pred = fc.classifyInstance(test.instance(i));
			   System.out.print("ID: " + test.instance(i).value(0));
			   System.out.print(", actual: " + test.classAttribute().value((int) test.instance(i).classValue()));
			   System.out.print(", predicted: " + test.classAttribute().value((int) pred));
			   System.out.print(", value: " + pred);
			   if(test.classAttribute().value((int) test.instance(i).classValue())
					   == test.classAttribute().value((int) pred)){
				   System.out.println(", CORRECT Prediction");
				   correctPredictions++;
			   }else{
				   System.out.println(", INCORRECT Prediction");
				   incorrectPredictions++;
			   }
			 }
			 System.out.println("Total Correct Predictions: " + correctPredictions);
			 System.out.println("Total Incorrect Predictions: " + incorrectPredictions);
			 }catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static Instances loadArffFile(String title) throws Exception{
		String arffLocation = selectFile(title);
		return new Instances(new BufferedReader(new FileReader(arffLocation)));
	}
	
	public static String selectFile(String title){
		/*
		 * Opens a dialog box to load a file with extension
		 */		
		JFileChooser fc = new JFileChooser();		
		fc.setDialogTitle(title);		
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {	        
			File file = fc.getSelectedFile();
			return file.getAbsolutePath();
		}
		return null;
	}
}
