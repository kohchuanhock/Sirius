package sirius.membranetype;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class MembraneTypePrediction{	
	/*
	 * Class Members
	 */
	private static String[] memtypeString = new String[16];
	private static DecimalFormat df = new DecimalFormat("#.###");
	static {
		memtypeString[0] = "NonMembrane";
		memtypeString[1] = "TypeI";
		memtypeString[2] = "TypeII";
		memtypeString[3] = "TypeIII";
		memtypeString[4] = "TypeIV";
		memtypeString[5] = "MultiPass";
		memtypeString[6] = "LipidAnchor";
		memtypeString[7] = "GPIAnchor";
		memtypeString[8] = "Peripheral";
		memtypeString[9] = "EmbedMembrane";
		memtypeString[10] = "NonEmbed";
		memtypeString[11] = "SinglePass";
		memtypeString[12] = "N2C";
		memtypeString[13] = "C2N";
		memtypeString[14] = "Anchor";
		memtypeString[15] = "NonAnchor";
	}
	private List<Integer> predictionIndexList = new ArrayList<Integer>();
	private List<Double> scoreList = new ArrayList<Double>();
	private List<List<String>> ruleListList = new ArrayList<List<String>>();
	private List<List<String>> featureListList = new ArrayList<List<String>>();
	
	/*
	 * Class Methods
	 */
	public MembraneTypePrediction(){
		//this is for no prediction
	}
	
	public MembraneTypePrediction(int index, double score, List<String> reasonList, List<String> featureList){		
		/*
		 * Single prediction
		 */
		this.predictionIndexList.add(index);
		this.scoreList.add(score);
		this.ruleListList.add(reasonList);
		this.featureListList.add(featureList);
	}
	
	public MembraneTypePrediction(List<Integer> indexList, List<Double> scoreList, List<List<String>> ruleListList, 
			List<List<String>> featureListList){		
		/*
		 * Multiple Predictions
		 */
		this.predictionIndexList.addAll(indexList);
		this.scoreList.addAll(scoreList);
		this.ruleListList = ruleListList;
		this.featureListList = featureListList;
	}
	
	public MembraneTypePrediction(List<MembraneTypePrediction> membraneTypePredictionList){
		/*
		 * Combine several MembraneTypePrediction
		 */
		for(MembraneTypePrediction m:membraneTypePredictionList){
			this.predictionIndexList.addAll(m.predictionIndexList);
			this.scoreList.addAll(m.scoreList);
			this.ruleListList.addAll(m.ruleListList);
			this.featureListList.addAll(m.featureListList);
		}
	}
	
	public String toString(){
		String s = "";
		s += "Predictions: ";
		if(this.predictionIndexList.size() == 0){
			return s + "No Prediction\r\n";
		}
		for(int x = 0; x < this.predictionIndexList.size(); x++){
			if(x != 0) s += ", ";
			s += memtypeString[this.predictionIndexList.get(x)] + " (Score = " + df.format(scoreList.get(x)) + ")";
		}
		s += "\r\n";
		return s;
	}
	
	public String toStringWithRules(){
		/*
		 * Print the string value wtih rules
		 */
		String s = "";
		if(this.predictionIndexList.size() == 0){
			return s + "No Prediction\r\n";
		}
		for(int x = 0; x < this.predictionIndexList.size(); x++){
			s += "Prediction: " + memtypeString[this.predictionIndexList.get(x)] + " (Score = " + df.format(scoreList.get(x)) + ")\r\n";
			for(String ruleString:this.ruleListList.get(x)){				
				s += "Rule for Prediction: " + ruleString.replace("PREDICT: pos", 
						"PREDICT: " + memtypeString[this.predictionIndexList.get(x)]);
			}
		}
		s += "\r\n";
		return s;
	}
	
	public String toStringWithRulesAndFeatureValues(){
		/*
		 * Print the string values with rules and feature values
		 */
		String s = "";
		if(this.predictionIndexList.size() == 0){
			return s + "No Prediction\r\n";
		}
		for(int x = 0; x < this.predictionIndexList.size(); x++){
			s += "Prediction: " + memtypeString[this.predictionIndexList.get(x)] + " (Score = " + df.format(scoreList.get(x)) + ")\r\n";
		}
		for(int x = 0; x < this.predictionIndexList.size(); x++){
			//s += "Prediction: " + memtypeString[this.predictionIndexList.get(x)] + " (Score = " + df.format(scoreList.get(x)) + ")\r\n";
			for(int y = 0; y < this.ruleListList.get(x).size(); y++){
				s+= "\r\nSequence Feature Values: " + this.featureListList.get(x).get(y) + "\r\n";
				s += "Rule for Prediction: " + this.ruleListList.get(x).get(y).replace("PREDICT: pos", 
						"PREDICT: " + memtypeString[this.predictionIndexList.get(x)]).
						replace("PREDICT: neg", "PREDICT: " + memtypeString[this.predictionIndexList.get(x)]);
			}
		}
		s += "\r\n";
		return s;
	}
	
	public String toStringWithRulesWithFeatureValuesEmbedded(){
		/*
		 * Print the string values with rules and feature values with values embedded
		 */
		String s = "";
		if(this.predictionIndexList.size() == 0){
			return s + "No Prediction\r\n";
		}
		for(int x = 0; x < this.predictionIndexList.size(); x++){
			s += "Prediction: " + memtypeString[this.predictionIndexList.get(x)] + " (Score = " + df.format(scoreList.get(x)) + ")\r\n";
		}		
		s += "\r\nFormat: Condition (Sequence's Feature Value) AND ...";
		for(int x = 0; x < this.predictionIndexList.size(); x++){
			//s += "Prediction: " + memtypeString[this.predictionIndexList.get(x)] + " (Score = " + df.format(scoreList.get(x)) + ")\r\n";
			for(int y = 0; y < this.ruleListList.get(x).size(); y++){
				String ruleString = this.ruleListList.get(x).get(y);
				String featureString = this.featureListList.get(x).get(y);				
				s += "\r\nRule: ";
				String[] ruleStringList = ruleString.split(" AND ");
				String predictString = ruleStringList[ruleStringList.length - 1].split("\\r\\n")[1];
				ruleStringList[ruleStringList.length - 1] = ruleStringList[ruleStringList.length - 1].split("\\r\\n")[0];
				String[] featureStringList = featureString.split(" AND ");				
				for(int z = 0; z < ruleStringList.length; z++){
					StringTokenizer st = new StringTokenizer(ruleStringList[z]);
					String name = st.nextToken();
					String operator = st.nextToken();
					String value = st.nextToken();
					
					for(String f:featureStringList){
						//0 => name, 1 => value
						String[] fs = f.split(" IS ");
						if(name.equals(fs[0])){
							if(z != 0) s += " AND ";
							s += name + " " + operator + " " + value + " (" + fs[1] + ")";
							break;
						}
					}
				}
				s += "\r\n" + predictString.replace("PREDICT: pos", "PREDICT: " + 
						memtypeString[this.predictionIndexList.get(x)]).replace("PREDICT: neg", "PREDICT: " + memtypeString[this.predictionIndexList.get(x)]);				
			}
		}
		s += "\r\n";
		return s;
	}
	
	public int size(){
		/*
		 * Returns the number of predictions
		 */
		return this.predictionIndexList.size();
	}
	
	public boolean containsIndex(int index){
		/*
		 * Check if prediction has been made for the given index
		 */
		for(int i:predictionIndexList){
			if(i == index){
				return true;
			}
		}
		return false;
	}
	
	public double getScore(){
		/*
		 * Returns the score of index 0
		 */
		return this.scoreList.get(0);
	}
	
	public double index2Score(int index){
		/*
		 * Returns the score of the given index
		 */
		for(int i = 0; i < this.predictionIndexList.size(); i++){
			if(this.predictionIndexList.get(i) == index){
				return this.scoreList.get(i);
			}
		}
		throw new Error("Does not contain Index: " + index);
	}
}
