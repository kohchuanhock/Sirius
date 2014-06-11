package sirius.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class PredictionStats {
	
	private static final int intervalNum = 1001;
	private int thresholdLocation;
	private static double[] threshold = new double[intervalNum];
	private int[] TP;
	private int[] FN;
	//This is used to prevent double counting
	private int[] countTP;
	private int[] TN;
	private int[] FP;	
	//This means that if for each sequence, only choose the one with highest score to be the predicted position, what is the TP?
//	private int maxTP; 
	
	private double[] SN;
	private double[] SP;
	private double[] totalCorrect;
	private double[] totalIncorrect;
	private double[] precisionPos;
	private double[] precisionNeg;	
	
	private double SN_;
	private double SP_;
	private double SNEqualSPThreshold;
	
	private double roc;
		
	private int totalPosSequences;
	private int range;	
	private double setThreshold;
	private double maxMCC;
	private double maxMCCThreshold;
	private int maxMCCIndex;
	
	public double getMaxMCC(){return this.maxMCC;}
	public double getMaxMCCThreshold(){return this.maxMCCThreshold;}
	public int getMaxMCCIndex(){return this.maxMCCIndex;}
	public double getCoverage(int index){
		return this.TP[index] / (double)(this.TP[index] + this.FN[index]);
	}
	public double getAccuracy(int index){
		return this.TP[index] / (double)(this.TP[index] + this.FP[index]);
	}
	public double getFPRate(int index){
		return this.FP[index] / (double)(this.TN[index] + this.FP[index]);
	}
	public double getTP(int index){return this.TP[index];}
	public double getFP(int index){return this.FP[index];}

	
	public PredictionStats(List<Integer> classList, List<Double> predictionList, double threshold){
		/*
		 * classList is to contain the classes (pos as 0 and neg as 1)
		 * predictionList is to contain the prediction score for 0
		 */
		//The following is needed to calibrate
		DecimalFormat df = new DecimalFormat("0.###");
		double temp = 0.000;
		for(int x = 0; x < intervalNum; x++){
			PredictionStats.threshold[x] = temp;
			temp += 0.001;
			//do this to calibrate
			temp = Double.parseDouble(df.format(temp));
		}
		
		//What range tolerance is still consider as correct prediction
		this.range = 0;	
		this.thresholdLocation = this.thresholdToThresholdIndex(threshold);
		this.setThreshold = threshold;

		TP = new int[intervalNum]; 
		countTP = new int[intervalNum];
		TN = new int[intervalNum];
		FP = new int[intervalNum];
		FN = new int[intervalNum];
		
		//Ensure that the threshold can be represented accurately
		if(PredictionStats.threshold[this.thresholdLocation] - threshold != 0){
			System.err.println("Given threshold cannot be represented accurately. Please select another threshold.");
			throw new Error("Given threshold: " + threshold + "\tNearest threshold: " + PredictionStats.threshold[this.thresholdLocation]);
		}

		this.totalPosSequences = 0;	
		//Still here but I think this might not be needed for single prediction per sequence
		int lineCount = 0;
		for(int i = 0; i < classList.size(); i++){
			lineCount++;
			switch(classList.get(i)){
			case 0://Pos
				this.totalPosSequences++;
				computeTPFN(predictionList.get(i),lineCount);
				break;
			case 1://Neg
				computeTNFP(predictionList.get(i));
				break;
			default: throw new Error("Unhandled Case: " + classList.get(i));
			}				
		}
		this.computeMoreStats();
		this.computeSNEqualSPLocation();
		this.computeMaxMCC();
		this.computeROC();		
	}		
	
	public PredictionStats(String filename, int range, double threshold){
		/*
		 * This method is to handle .scores file
		 * This is for computing the stats for classification with and without range
		 * Format is multiple line with header and its sequence then the score
		 */
		//The following is needed to calibrate
    	DecimalFormat df = new DecimalFormat("0.###");
		double temp = 0.000;
		for(int x = 0; x < intervalNum; x++){
			PredictionStats.threshold[x] = temp;
			temp += 0.001;
			//do this to calibrate
			temp = Double.parseDouble(df.format(temp));
		}     
		
		//What range tolerance is still consider as correct prediction
		this.range = range;		
		//instead of point5Location, it should be called threshold location would be more appropriate
		this.thresholdLocation = this.thresholdToThresholdIndex(threshold);
		this.setThreshold = threshold;
		
		TP = new int[intervalNum]; 
    	countTP = new int[intervalNum];
		TN = new int[intervalNum];
		FP = new int[intervalNum];
		FN = new int[intervalNum];
		
		if(PredictionStats.threshold[this.thresholdLocation] - threshold != 0){
			System.err.println("Given threshold cannot be represented accurately. Please select another threshold.");
			throw new Error("Given threshold: " + threshold + "\tNearest threshold: " + PredictionStats.threshold[this.thresholdLocation]);
		}
		
		String thirdLine = null;
		try{
			totalPosSequences = 0;
			BufferedReader input = new BufferedReader(new FileReader(filename));						
			int lineCount = 0;								
			while((input.readLine()) != null){//first line is the fasta header
				lineCount++;
				input.readLine();//second line is the sequence
				thirdLine = input.readLine();//third line is the "class","position1"="score1","position2"="score2"..
				StringTokenizer st = new StringTokenizer(thirdLine, ",");				
				//According to current format, first token must be class attribute - 7th Dec 2007
				String _class = st.nextToken();
				boolean isPos = false;
				if(_class.indexOf("pos") != -1 || _class.indexOf("0") != -1){
					isPos = true;
				}
				if(isPos){
					totalPosSequences++;		
				}
//				int maxScorePosition = 0; //stores the position where the maximum score predicted for the sequence
				double maxScore = Double.NEGATIVE_INFINITY; //stores the maximum predicted score
				while(st.hasMoreTokens()){//loop through all the positions
					StringTokenizer st2 = new StringTokenizer(st.nextToken(),"=");
					int currentPosition = Integer.parseInt(st2.nextToken());
					double score = Double.parseDouble(st2.nextToken());
					if(isPos){//pos						
						if(currentPosition == 0){
							//this is for +1 index == -1
							computeTPFN(score,lineCount);
						}else{
							computeTPFN(score,lineCount,currentPosition,range);
							if(score > maxScore){
								maxScore = score;
//								maxScorePosition = currentPosition;
							}
						}						
					}else{//neg
						computeTNFP(score);
					}
				}
//				if(isPos && maxScorePosition >= (range * -1) && maxScorePosition <= (range + 1)){
//					maxTP++;
//				}
			}
			this.computeMoreStats();
			this.computeSNEqualSPLocation();
			this.computeMaxMCC();
			this.computeROC();
			input.close();
		}catch(Exception e){
			System.out.println(thirdLine);
			JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();}
	}		
	
	public static List<Integer> getClassListFromString(String line, String posClass, String negClass, String delimiter){
		String s[] = line.split(delimiter);
		List<Integer> iList = new ArrayList<Integer>();
		for(String a:s){
			a = a.trim();
			if(a.contains(posClass)){
				iList.add(0);
			}else if(a.contains(negClass)){
				iList.add(1);
			}else{
				throw new Error("Cannot classify to Pos or Neg: " + a);
			}
		}
		return iList;
	}
	
	public static List<Double> getPredictionFromString(String line, String delimiter){
		String s[] = line.split(delimiter);
		List<Double> pList = new ArrayList<Double>();
		for(String a:s){
			a = a.trim();
			pList.add(Double.parseDouble(a));	
		}
		return pList;
	}
	
	//This method is only called from ClustererClassificationPane
	public static void computeEqualWeightage(ClassifierResults results, JTextArea displayTextArea,
			double threshold,
			ArrayList<PredictionStats> statsArrayList, 
			ArrayList<ClassifierResults> crArrayList){	
		/*
		 * This method is used by ClustererClassificationPane only
		 */
		if(results != null)
			results.updateList(results.getSettingsList(), "Threshold: ", "" + threshold);				
		
		DecimalFormat df = new DecimalFormat("0.###");			
		
		double size = statsArrayList.size();
		int totalPosSequences = 0;
		int totalNegSequences = 0;
		int thresholdLocation = (int)(threshold * (intervalNum - 1));
		double totalTP = 0;
		double totalTN = 0;
		double totalFP = 0;
		double totalFN = 0;
		double totalCorrect = 0;
		double totalIncorrect = 0;
		double totalPrecisionPos = 0;
		double totalPrecisionNeg = 0;
		double totalROC = 0;
		double averageTotalCorrect[] = new double[intervalNum];
		double averageTotalIncorrect[] = new double[intervalNum];
		double averagePrecisionPos[] = new double[intervalNum];
		double averagePrecisionNeg[] = new double[intervalNum];
		double averageSN[] = new double[intervalNum];
		double averageSP[] = new double[intervalNum];		
		for(int x = 0; x < size; x++){
			PredictionStats stats = statsArrayList.get(x);
			int thisNegSequences = stats.TN[thresholdLocation] + stats.FP[thresholdLocation];
			totalPosSequences += stats.totalPosSequences;
			totalNegSequences += stats.TN[thresholdLocation] + stats.FP[thresholdLocation];  
			totalTP += (stats.TP[thresholdLocation] / (size * stats.totalPosSequences));
			totalTN += (stats.TN[thresholdLocation] / (size * thisNegSequences));
			totalFP += (thisNegSequences - stats.TN[thresholdLocation]) / (size * thisNegSequences);
			totalFN += (stats.totalPosSequences - stats.TP[thresholdLocation]) / (size * stats.totalPosSequences);
			totalCorrect += (stats.totalCorrect[thresholdLocation] / size);
			totalIncorrect += (stats.totalIncorrect[thresholdLocation] / size);
			totalPrecisionPos += (stats.precisionPos[thresholdLocation] / size);
			totalPrecisionNeg += (stats.precisionNeg[thresholdLocation] / size);
			totalROC += (stats.roc / size);
			for(int y = 0; y < intervalNum; y++){
				averageTotalCorrect[y] += (stats.totalCorrect[y] / size);
				averageTotalIncorrect[y] += (stats.totalIncorrect[y] / size);
				averagePrecisionPos[y] += (stats.precisionPos[y] / size);
				averagePrecisionNeg[y] += (stats.precisionNeg[y] / size);
				averageSN[y] += (stats.SN[y] / size);
				averageSP[y] += (stats.SP[y] / size);
			}
		}		
		if(results != null){
			results.updateList(results.getResultsList(), "Total +ve Instances: ", "" + totalPosSequences);				
			results.updateList(results.getResultsList(), "Total -ve Instances: " , "" + totalNegSequences);		
			
			results.updateList(results.getResultsList(), "TP predictions: " , " (" + df.format(totalTP) + ")");
			results.updateList(results.getResultsList(),"FN predictions: " , " (" + df.format(totalFN) + ")");
			results.updateList(results.getResultsList(), "TN predictions: ", " (" + df.format(totalTN) +  ")");
			results.updateList(results.getResultsList(), "FP predictions: ", " (" +	df.format(totalFP) + ")");	
			
			results.updateList(results.getResultsList(), "Total Correct Predictions: " , 
				df.format(totalCorrect) + "  \t" + "Area under Curve: " + df.format(area(averageTotalCorrect,intervalNum)));		
			results.updateList(results.getResultsList(), "Total Incorrect Predictions: " , 
				df.format(totalIncorrect) + "\tArea under Curve: " + df.format(area(averageTotalIncorrect,intervalNum)));		
			results.updateList(results.getResultsList(),"Precision(wrt +ve): ", df.format(totalPrecisionPos) + 
					"\tArea under Curve: " + df.format(area(averagePrecisionPos,intervalNum)));
			results.updateList(results.getResultsList(), "Precision(wrt -ve): ", df.format(totalPrecisionNeg) + 
					"\tArea under Curve: " + df.format(area(averagePrecisionNeg,intervalNum)));
			
			results.updateList(results.getResultsList(), "SN: ", df.format(averageSN[thresholdLocation]) + 
					"\tArea under Curve: " + df.format(area(averageSN,intervalNum)));
			results.updateList(results.getResultsList(), "SP: ", df.format(averageSP[thresholdLocation]) + 
					"\tArea under Curve: " + df.format(area(averageSP,intervalNum)) + "\n");
		}
		
		//to find approx (SN == SP), here i should simply display for all clusters
		//for(int x = 0; x < crArrayList.size(); x++)
		//	results.updateList(results.getResultsList(),"Cluster " + x + ": ", crArrayList.get(x).getSNSP());		
		//to find approx (SN == SP)		
		double SN_ = averageSN[0];
		double SP_ = averageSP[0];
		double SNEqualSPThreshold = PredictionStats.threshold[0];
		double difference = 1.1;		
		for(int x = 0; x < intervalNum; x++){		      				
			if(((averageSN[x] - averageSP[x]) > 0 && (averageSN[x] - averageSP[x]) < difference) || 
				((averageSP[x] - averageSN[x]) > 0 && (averageSP[x] - averageSN[x]) < difference)){
				SN_ = averageSN[x];
				SP_ = averageSP[x];
				SNEqualSPThreshold = PredictionStats.threshold[x];
				if((averageSN[x] - averageSP[x]) > 0)
					difference = (averageSN[x] - averageSP[x]);
				else 
					difference = (averageSP[x] - averageSN[x]);
			}		      				
		}		
		if(results != null){
			results.updateList(results.getResultsList(),"Approx (SN == SP): SN = ", df.format(SN_) + ", SP = " + df.format(SP_) +
				" @ threshold = " + df.format(SNEqualSPThreshold));    	
			results.updateList(results.getResultsList(),"Area Under Curve for ROC: ", df.format(totalROC) + "\n"); 
					
			results.updateDisplayTextArea(displayTextArea,true,crArrayList.size());		
		}
	}
	
	//This method is only called from ClustererClassificationPane
	public static void computeWeighted(ClassifierResults results, JTextArea displayTextArea,
			double threshold,ArrayList<PredictionStats> statsArrayList, 
			ArrayList<ClassifierResults> crArrayList){
		/*
		 * This method is also used by ClustererClassificationPane
		 */
		if(results != null)
			results.updateList(results.getSettingsList(), "Threshold: ", "" + threshold);		
		DecimalFormat df = new DecimalFormat("0.###");		
		int totalPosSequences = 0;
		int totalNegSequences = 0;
		int thresholdLocation = (int)(threshold * (intervalNum - 1));		
		int allTP[] = new int[intervalNum];
		int allTN[] = new int[intervalNum];
		int allFP[] = new int[intervalNum];
		int allFN[] = new int[intervalNum];
		
		double allCorrect[] = new double[intervalNum];
		double allIncorrect[] = new double[intervalNum];
		double allPrecisionPos[] = new double[intervalNum];
		double allPrecisionNeg[] = new double[intervalNum];
		double allSN[] = new double[intervalNum];
		double allSP[] = new double[intervalNum];
		
		for(int x = 0; x < statsArrayList.size(); x++){
			PredictionStats stats = statsArrayList.get(x);			
			totalPosSequences += stats.totalPosSequences;
			totalNegSequences += stats.TN[thresholdLocation] + stats.FP[thresholdLocation];  			
			for(int y = 0; y < intervalNum; y++){
				allTP[y] += stats.TP[y];
				allFP[y] += stats.FP[y];
				allTN[y] += stats.TN[y];
				allFN[y] += stats.totalPosSequences - stats.TP[y];											
			}
		}	
		int totalSequences = totalPosSequences + totalNegSequences;
		for(int x = 0; x < statsArrayList.size(); x++){
			PredictionStats stats = statsArrayList.get(x);
			int localTotal = stats.totalPosSequences + stats.TN[thresholdLocation] + stats.FP[thresholdLocation];
			double weight = (localTotal + 0.0) / totalSequences;			
			for(int y = 0; y < intervalNum; y++){
				allCorrect[y] += stats.getAverageTotalCorrect(y) * weight;
				allIncorrect[y] += stats.getAverageTotalIncorrect(y) * weight;			
				allPrecisionPos[y] += stats.getAveragePrecisionPos(y) * weight;
				allPrecisionNeg[y] += stats.getAveragePrecisionNeg(y) * weight;
				allSN[y] += stats.getAverageSN(y) * weight;
				allSP[y] += stats.getAverageSP(y) * weight;
			}
		}		
		double weightedROC = 0;		
		for(int x = 0; x < statsArrayList.size(); x++){			
			PredictionStats stats = statsArrayList.get(x);			
			weightedROC += stats.roc * (stats.totalPosSequences + stats.TN[thresholdLocation] + stats.FP[thresholdLocation]) /
				(totalPosSequences + totalNegSequences);			
		}		
		results.updateList(results.getResultsList(), "Total +ve Instances: ", "" + totalPosSequences);				
		results.updateList(results.getResultsList(), "Total -ve Instances: " , "" + totalNegSequences);		
		
		results.updateList(results.getResultsList(), "TP predictions: " , allTP[thresholdLocation] + " (" + 
				df.format(allTP[thresholdLocation] / (allTP[thresholdLocation] + allFN[thresholdLocation] + 0.0)) + ")");
		results.updateList(results.getResultsList(),"FN predictions: " , allFN[thresholdLocation] + " (" + 
				df.format(allFN[thresholdLocation] / (allTP[thresholdLocation] + allFN[thresholdLocation] + 0.0)) + ")");
		results.updateList(results.getResultsList(), "TN predictions: ", allTN[thresholdLocation] +  " (" + 
				df.format(allTN[thresholdLocation] / (allTN[thresholdLocation] + allFP[thresholdLocation] + 0.0)) +  ")");
		results.updateList(results.getResultsList(), "FP predictions: ", allFP[thresholdLocation] + " (" +	
				df.format(allFP[thresholdLocation] / (allTN[thresholdLocation] + allFP[thresholdLocation] + 0.0)) + ")");	
				
		results.updateList(results.getResultsList(), "Total Correct Predictions: " , 
			df.format(allCorrect[thresholdLocation]) + "  \t" + "Area under Curve: " + df.format(area(allCorrect,intervalNum)));		
		results.updateList(results.getResultsList(), "Total Incorrect Predictions: " , 
			df.format(allIncorrect[thresholdLocation]) + "\tArea under Curve: " + df.format(area(allIncorrect,intervalNum)));		
		results.updateList(results.getResultsList(),"Precision(wrt +ve): ", df.format(allPrecisionPos[thresholdLocation]) + 
				"\tArea under Curve: " + df.format(area(allPrecisionPos,intervalNum)));
		results.updateList(results.getResultsList(), "Precision(wrt -ve): ", df.format(allPrecisionNeg[thresholdLocation]) + 
				"\tArea under Curve: " + df.format(area(allPrecisionNeg,intervalNum)));
		
		results.updateList(results.getResultsList(), "SN: ", df.format(allSN[thresholdLocation]) + 
				"\tArea under Curve: " + df.format(area(allSN,intervalNum)));
		results.updateList(results.getResultsList(), "SP: ", df.format(allSP[thresholdLocation]) + 
				"\tArea under Curve: " + df.format(area(allSP,intervalNum)) + "\n");
		
		//to find approx (SN == SP), here i should simply display for all clusters
		//for(int x = 0; x < crArrayList.size(); x++)
		//	results.updateList(results.getResultsList(),"Cluster " + x + ": ", crArrayList.get(x).getSNSP());		
		//to find approx (SN == SP)		
		double SN_ = allSN[0];
		double SP_ = allSP[0];
		double SNEqualSPThreshold = PredictionStats.threshold[0];
		double difference = 1.1;		
		for(int x = 0; x < intervalNum; x++){		      				
			if(((allSN[x] - allSP[x]) > 0 && (allSN[x] - allSP[x]) < difference) || 
				((allSP[x] - allSN[x]) > 0 && (allSP[x] - allSN[x]) < difference)){
				SN_ = allSN[x];
				SP_ = allSP[x];
				SNEqualSPThreshold = PredictionStats.threshold[x];
				if((allSN[x] - allSP[x]) > 0)
					difference = (allSN[x] - allSP[x]);
				else 
					difference = (allSP[x] - allSN[x]);
			}		      				
		}		
		results.updateList(results.getResultsList(),"Approx (SN == SP): SN = ", df.format(SN_) + ", SP = " + df.format(SP_) +
			" @ threshold = " + df.format(SNEqualSPThreshold));    	
		results.updateList(results.getResultsList(),"Area Under Curve for ROC: ", df.format(weightedROC) + "\n"); 
				
		results.updateDisplayTextArea(displayTextArea,true,crArrayList.size());		
	}
	
	public void updateDisplay(ClassifierResults results, JTextArea displayTextArea, boolean show){
		this.updateDisplay(results, displayTextArea, show, null);
	}
	
    public void updateDisplay(ClassifierResults results, JTextArea displayTextArea, boolean show,
    		String inputFilename){    	
    	if(results != null){
    		results.updateList(results.getSettingsList(), "Threshold: ", "" + this.setThreshold);
    		results.updateList(results.getSettingsList(), "Range: ", "" + this.range);
    	}
		
		DecimalFormat df = new DecimalFormat("0.###");				      		
		
		
		if(results != null){
			results.updateList(results.getResultsList(), "Total +ve Instances: ", "" + this.totalPosSequences);
			//choose 0 because no matter how matter at what 1.. (TN + FP) should be the same
			int totalNegativeInstances = (TN[thresholdLocation] + FP[thresholdLocation]);		
			results.updateList(results.getResultsList(), "Total -ve Instances: " , "" + totalNegativeInstances);
			
			results.updateList(results.getResultsList(), "TP predictions: " , this.TP[thresholdLocation] + " (" + 
				df.format(this.TP[thresholdLocation] / (this.totalPosSequences + 0.0)) + ")");	
			results.updateList(results.getResultsList(),"FN predictions: " , this.FN[thresholdLocation] + " (" +
				df.format((this.FN[thresholdLocation]) / (this.totalPosSequences + 0.0)) + ")");
			results.updateList(results.getResultsList(), "TN predictions: ", this.TN[thresholdLocation] + " (" + 
				df.format(this.TN[thresholdLocation] / (totalNegativeInstances + 0.0)) +  ")");
			results.updateList(results.getResultsList(), "FP predictions: ", this.FP[thresholdLocation] + " (" +
				 df.format(this.FP[thresholdLocation] / (totalNegativeInstances + 0.0)) + ")");			
			results.updateList(results.getResultsList(), "MCC: ", df.format(this.getMCC(this.thresholdLocation)));
			
					      			
			results.updateList(results.getResultsList(), "Total Correct Predictions: " , 
				df.format(totalCorrect[thresholdLocation]) + "  \t" + "Area under Curve: " + df.format(area(totalCorrect,intervalNum)));
			results.updateList(results.getResultsList(), "Total Incorrect Predictions: " , 
				df.format(totalIncorrect[thresholdLocation]) + "\tArea under Curve: " + df.format(area(totalIncorrect,intervalNum)));
			
			results.updateList(results.getResultsList(),"Precision(wrt +ve): ", df.format(precisionPos[thresholdLocation]) + 
					"\tArea under Curve: " + df.format(area(precisionPos,intervalNum)));
			results.updateList(results.getResultsList(), "Precision(wrt -ve): ", df.format(precisionNeg[thresholdLocation]) + 
					"\tArea under Curve: " + df.format(area(precisionNeg,intervalNum)));
			
			results.updateList(results.getResultsList(), "SN: ", df.format(SN[thresholdLocation]) + 
					"\tArea under Curve: " + df.format(area(SN,intervalNum)));
			results.updateList(results.getResultsList(), "SP: ", df.format(SP[thresholdLocation]) + 
					"\tArea under Curve: " + df.format(area(SP,intervalNum)) + "\n");
		}
		
		
		if(results != null)
			results.updateList(results.getResultsList(),"Approx (SN == SP): SN = ", df.format(SN_) + 
					", SP = " + df.format(SP_) +
					" @ threshold = " + df.format(SNEqualSPThreshold));    				
		
		if(results != null)
			results.updateList(results.getResultsList(),"Max MCC = ", df.format(maxMCC) +
				" @ threshold = " + df.format(maxMCCThreshold) + "\n");		
		if(results != null)
				results.updateList(results.getResultsList(),"Area Under Curve for ROC: ", df.format(this.roc) + "\n");    
		
		if(results != null)
			results.updateDisplayTextArea(displayTextArea, show, 0, inputFilename);
    }
    
    public void computeROC(){
    	try{
			File tempFile = File.createTempFile("falsePositive","truePositive");	
			BufferedWriter output = new BufferedWriter(new FileWriter(tempFile));
			outputROC(output);
			output.close();
			String line;
			Process p = Runtime.getRuntime().exec("java -jar auc.jar " + 
					tempFile.getPath() + " ROC 1 1");
		 	BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		 	while ((line = input.readLine()) != null) {		 		
		 		if(line.indexOf("Area Under the Curve for ROC is") != -1){
		 			line = line.substring(("Area Under the Curve for ROC is").length() + 1);				 			
		 			this.roc = Double.parseDouble(line);
		 		}
		 	}
		 	input.close();
		 	tempFile.delete();
		}catch(Exception e){
			e.printStackTrace();
		}		
    }
    
    public int thresholdToThresholdIndex(double threshold){
    	double difference = Double.POSITIVE_INFINITY;
    	int index = -1;
    	for(int i = 0; i < PredictionStats.threshold.length; i++){
    		double d = PredictionStats.threshold[i] - threshold;
    		if(d < 0) d *= -1;
    		if(d < difference){
    			difference = d;
    			index = i;
    		}
    	}
    	return index;
    }
    
    public void getBasicStats(double threshold){
    	this.getBasicStats(this.thresholdToThresholdIndex(threshold));
    }
    
    public void getBasicStats(int thresholdIndex){
    	System.out.println("TP: " + this.TP[thresholdIndex]);
    	System.out.println("FP: " + this.FP[thresholdIndex]);
    	System.out.println("TN: " + this.TN[thresholdIndex]);
    	System.out.println("FN: " + this.FN[thresholdIndex]);
    }
    
    public void getFullStats(double threshold){
    	this.getFullStats(this.thresholdToThresholdIndex(threshold));
    }
    
    public void getFullStats(int thresholdIndex){
    	System.out.println("TP: " + this.TP[thresholdIndex]);
    	System.out.println("FP: " + this.FP[thresholdIndex]);
    	System.out.println("TN: " + this.TN[thresholdIndex]);
    	System.out.println("FN: " + this.FN[thresholdIndex]);
    	System.out.println();
    	System.out.println("SN: " + this.SN[thresholdIndex]);
    	System.out.println("SP: " + this.SP[thresholdIndex]);
    	System.out.println();
    	System.out.println("Total Correct: " + this.totalCorrect[thresholdIndex]);
    	System.out.println("Total Incorrect: " + this.totalIncorrect[thresholdIndex]);
    	System.out.println();
    	System.out.println("Precision (+ve): " + this.precisionPos[thresholdIndex]);
    	System.out.println("Precision (-ve): " + this.precisionNeg[thresholdIndex]);
    	System.out.println();
    	System.out.println("SN@" + this.SNEqualSPThreshold + ": " + this.SN_);
    	System.out.println("SP@" + this.SNEqualSPThreshold + ": " + this.SP_);
    	System.out.println();
    	System.out.println("ROC: " + this.roc);
    	System.out.println("MCC: " + this.getMCC(thresholdIndex));
    	System.out.println("MaxMCC@" + this.maxMCCThreshold + ": " + this.maxMCC);
    }
    
    public double getMCC(double threshold){
		return this.getMCC(this.thresholdToThresholdIndex(threshold));
	}
	
	public double getMCC(int thresholdIndex){
		/*
		 * MCC = [(TP * TN) - (FP * FN)] / sqrt((TP+FP)(TP+FN)(TN+FP)(TN+FN))
		 */
		double nominator = ((TP[thresholdIndex]*TN[thresholdIndex]) - (FP[thresholdIndex]*FN[thresholdIndex])); 
		double denominator =  Math.sqrt((double)(TP[thresholdIndex]+FP[thresholdIndex]) * (TP[thresholdIndex]+FN[thresholdIndex]) * 
				(TN[thresholdIndex]+FP[thresholdIndex]) * (TN[thresholdIndex]+FN[thresholdIndex]));
		if(denominator == 0.0){
			return 0.0;			
		}else{
			return nominator / denominator;
		}
	}
    
    private void computeSNEqualSPLocation(){
    	//to find approx (SN == SP)
		SN_ = SN[0];
		SP_ = SP[0];
		SNEqualSPThreshold = PredictionStats.threshold[0];
		double difference = 1.1;		
		for(int x = 0; x < intervalNum; x++){			
			if(Math.abs(SN[x] - SP[x]) < difference){
				SN_ = SN[x];
				SP_ = SP[x];
				SNEqualSPThreshold = PredictionStats.threshold[x];
				difference = Math.abs(SN[x] - SP[x]);				
			}		      				
		}		
    }
    
    private void computeMoreStats(){
    	//to find area under graph
		SN = new double[intervalNum];
		SP = new double[intervalNum];
		totalCorrect = new double[intervalNum];
		totalIncorrect = new double[intervalNum];
		precisionPos = new double[intervalNum];
		precisionNeg = new double[intervalNum];				
		for(int x = 0; x < intervalNum; x++){
			FN[x] = (this.totalPosSequences - this.TP[x]);
			if(TP[x] == 0 && FP[x] == 0)
				precisionPos[x] = 1.0;
			else
				precisionPos[x] += TP[x] / (TP[x] + FP[x] + 0.0);
			
			if((TN[x] + this.totalPosSequences - TP[x] + 0.0) == 0)
				precisionNeg[x] += 1.0;
			else
				precisionNeg[x] += TN[x] / (TN[x] + FN[x] + 0.0);						
			SN[x] = (TP[x] + 0.0) / this.totalPosSequences;
			SP[x] = TN[x] / (TN[x] + FP[x] + 0.0);
			totalCorrect[x] = (TP[x] + TN[x] + 0.0) / (FP[x] + TN[x] + this.totalPosSequences);
			totalIncorrect[x] = (FN[x] + FP[x] + 0.0) / (FP[x] + TN[x] + this.totalPosSequences);			 
		}			
    }
    
    private void computeMaxMCC(){
    	//to find the max MCC 
		//MCC = [(TP.TN) - (FP.FN)] / Math.sqrt([TP+FP][TP+FN][TN+FP][TN+FN])
		double maxMCC = Double.NEGATIVE_INFINITY;
		double maxMCCThreshold = PredictionStats.threshold[0];
		int maxMCCIndex = -1;
		for(int x = 0; x < PredictionStats.intervalNum; x++){
			int tpfp = TP[x]+FP[x];
			if(tpfp == 0) tpfp = 1;
			int tpfn = TP[x]+FN[x];
			if(tpfn == 0) tpfn = 1;	
			int tnfp = TN[x]+FP[x];
			if(tnfp == 0) tnfp = 1;
			int tnfn = TN[x]+FN[x];
			if(tnfn == 0) tnfn = 1;			
			double currentMCC = ((TP[x]*TN[x]) - (FP[x]*FN[x])) / 
				Math.sqrt((double)(tpfp) * (tpfn) * (tnfp) * (tnfn));
			if(currentMCC > maxMCC){
				maxMCC = currentMCC;
				maxMCCThreshold = PredictionStats.threshold[x];
				maxMCCIndex = x;
			}
		}
		if(maxMCCIndex == -1){			
			System.err.println("PredictionStats.intervalNum: " + PredictionStats.intervalNum);
			System.err.println("maxMCCIndex = -1!!");
			maxMCCIndex = 0;
			maxMCCThreshold = 0.0;
			maxMCC = 0;
		}
		this.maxMCCThreshold = maxMCCThreshold;
		this.maxMCCIndex = maxMCCIndex;
		this.maxMCC = maxMCC;
		//System.out.println(maxMCC);
    }
    
    private void computeTNFP(double score){	
		for(int y = 0; y < intervalNum; y++){
			if(score <= PredictionStats.threshold[y])
				TN[y]++;			
			else
				FP[y]++;
		}	
	}
	
    private void computeTPFN(double score,int lineCount,int currentPosition,int range){
		//need not range + 1 for upper limit because currentPosition consists index 0
		if(currentPosition >= (range * -1) && currentPosition <= (range + 1)){
			for(int y = 0; y < intervalNum; y++){
				if(score > PredictionStats.threshold[y]){
					if(countTP[y] != lineCount){
						countTP[y] = lineCount;
						TP[y]++;												
					}
				}			
			}				
		} 				
	}
    
	private void computeTPFN(double score,int lineCount){
		//need not range + 1 for upper limit because currentPosition consists index 0		
		for(int y = 0; y < intervalNum; y++){
			if(score > PredictionStats.threshold[y]){
				if(countTP[y] != lineCount){
					countTP[y] = lineCount;
					TP[y]++;												
				}
			}			
		}				
	}
	
	private static double area(double[] yValues,int intervals){
		double area = 0.000;
		for(int x = 0; x < intervals - 1; x++){
			area += ((yValues[x] + yValues[x+1]) / 2) * (1.0 / (intervals - 1));
		}
		return area;
	}
	
	public double getAverageSN(int x){
		return this.SN[x];		
	}
	
	public double getAverageSP(int x){
		return this.SP[x];
	}
	
	public double getAverageTotalCorrect(int x){
		return this.totalCorrect[x];
	}	

	public double getAverageTotalIncorrect(int x){
		return this.totalIncorrect[x];
	}
	
	public double getAveragePrecisionPos(int x){
		return this.precisionPos[x];
	}		
	
	public double getAveragePrecisionNeg(int x){
		return this.precisionNeg[x];
	}

	private void outputROC(BufferedWriter output) throws Exception{
		/*
		 * Output values for computation of ROC by auc.jar
		 */
		for(int x = 0; x < intervalNum; x++){
			output.write("" + (1 - this.SP[x]) + "\t" + this.SN[x]);
			output.newLine();
		}
	}
}
	