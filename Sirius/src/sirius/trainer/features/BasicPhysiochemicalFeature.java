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
package sirius.trainer.features;

import java.util.Random;
import java.util.StringTokenizer;

import sirius.trainer.features.gui.basicphysiochemical.BasicPhysioTableModel;
import sirius.trainer.main.ScoringMatrix;
import sirius.utils.FastaFormat;

public class BasicPhysiochemicalFeature extends Feature{
	//true means there will be windowFrom and windowTo
	//false means take the whole sequence into consideration
	private boolean isLocal;
	
	public BasicPhysiochemicalFeature(String name){
		super(name, null, name.charAt(0));
		try{
			StringTokenizer st = new StringTokenizer(name, "_");
			char type = st.nextToken().charAt(0);
			String tokenName = st.nextToken();
			boolean isLocal = false;
			boolean isPercentage = false;
			int windowFrom = 0;
			int windowTo = 0;
			if(st.hasMoreTokens()){
				//with more tokens, it implies that it is local]\
				isLocal = true;
				isPercentage = Boolean.parseBoolean(st.nextToken());
				windowFrom = Integer.parseInt(st.nextToken());
				windowTo = Integer.parseInt(st.nextToken());
			}
			setValues(name, type, isLocal, windowFrom, windowTo, tokenName, isPercentage);
		}catch(Exception e){throw new Error("Error in BasicPhysiochemicalFeature(String name)");}
	}
	
	//To do deep copy
	public BasicPhysiochemicalFeature(BasicPhysiochemicalFeature bpc){
		super(bpc.getName(), bpc.getDetails(), bpc.getType(), false);
		this.isLocal = bpc.isLocal();
		this.isPercentage = bpc.isPercentage();
	}
	
	//Used by BasicPhysiochemicalTableModel
	public BasicPhysiochemicalFeature(char type, String name, String details, boolean box){
		super(name, details, type, box);
		this.isLocal = false;
		this.isPercentage = false;
	}
	
	private void setValues(String name, char type, boolean isLocal, int windowFrom, int windowTo, String tokenName, boolean isPercentage){
		this.isLocal = isLocal;
		this.windowFrom = windowFrom;
		this.windowTo = windowTo;
		this.isPercentage = isPercentage;
		setDetails(tokenName);	
	}
	
	//Used by level one classifierpane
	public BasicPhysiochemicalFeature(String name, char type, boolean isLocal, int windowFrom, int windowTo, String tokenName, boolean isPercentage){
		super(name, null, type);
		setValues(name, type, isLocal, windowFrom, windowTo, tokenName, isPercentage);
	}
	
	//Used by loadSettings
	public BasicPhysiochemicalFeature(String line, char type){
		super(null, null, type);		
		String name = line.substring(line.indexOf("Name: ") + ("Name: ").length(), line.indexOf("isLocal: ") - 1);
		this.name = name;
		String isLocalString = line.substring(line.indexOf("isLocal: ") + 
			("isLocal: ").length(),line.indexOf("isPercentage: ") - 1);			
		String isPercentageString = line.substring(line.indexOf("isPercentage: ") + 
				("isPercentage: ").length(),line.indexOf("Window_From: ") - 1);
		String windowFromString = line.substring(line.indexOf("Window_From: ") + 
			("Window_From: ").length(),line.indexOf("Window_To: ") - 1);
		String windowToString = line.substring(line.indexOf("Window_To: ") + ("Window_To: ").length());
		
		this.windowFrom = Integer.parseInt(windowFromString);
		this.windowTo = Integer.parseInt(windowToString);	 				
		this.isLocal = Boolean.parseBoolean(isLocalString);
		this.isPercentage = Boolean.parseBoolean(isPercentageString);
		StringTokenizer stName = new StringTokenizer(name, "_");
		stName.nextToken();
		String tokenName = stName.nextToken();
		setDetails(tokenName);	
	}	
	
	
	private boolean isLocal(){ return this.isLocal; }
	
	private void setDetails(String tokenName){
		if(tokenName.equals("Length"))
			this.details = "Number of Amino Acid Residues";			
		else if(tokenName.equals("Mass"))
			this.details = "Molecular Mass";		
		else if(tokenName.equals("pIValue"))
			this.details = "Theoretical pI Value";		
		else if(tokenName.equals("NetCharge"))
			this.details = "Net Charge";		
		else if(tokenName.equals("MeanNetCharge"))
			this.details = "Net Charge / Number of Amino Acid Residues";		
		else if(tokenName.equals("AbsoluteNetCharge"))				
			this.details = "Absolute Net Charge";			
		else if(tokenName.equals("AbsoluteMeanNetCharge"))				
			this.details = "Absolute Mean Net Charge";		
		else if(tokenName.equals("HydrophobicityTotal"))
			this.details = "Hydrophobicity of sequence based on Kyte and Doolittle approximation (Normalized)";		
		else if(tokenName.equals("HydrophobicityMean"))
			this.details = "HydrophobicityTotal / Number of Amino Acid Residues";			
		else if(tokenName.equals("OrderAminoAcidTotal"))
			this.details = "Order AA: {N, C, I, L, F, W, Y, V}";			
		else if(tokenName.equals("OrderAminoAcidMean"))
			this.details = "OrderAminoAcidTotal / Number of Amino Acid Residues";			
		else if(tokenName.equals("DisorderAminoAcidTotal"))
			this.details = "Disorder AA: {A, R, Q, E, G, K, P, S}";			
		else if(tokenName.equals("DisorderAminoAcidMean"))
			this.details = "DisorderAminoAcidTotal / Number of Amino Acid Residues";		
		else if(tokenName.equals("OrderDisorderAADifferenceTotal"))
			this.details = "#OrderAA - #DisorderAA";		
		else if(tokenName.equals("OrderDisorderAADifferenceMean"))
			this.details = "(#OrderAA - #DisorderAA) / Number of Amino Acid Residues";			
		else
			throw new Error("B Type but Unknown Name: " + name);			
		if(this.isLocal){
			if(this.isPercentage)
				this.details += ", PercentageWindow(" + this.windowFrom + "," + this.windowTo + ")";
			else
				this.details += ", Window(" + this.windowFrom + "," + this.windowTo + ")";
		}
	}
	
	public String saveString(String saveDirectory){
		if(type == 'B')
			return "Type: " + type + " Name: " + name + " isLocal: " + this.isLocal + " isPercentage: " + this.isPercentage + " Window_From: " + this.windowFrom + 
				" Window_To: " + this.windowTo;
		else
			return "UNKNOWN TYPE";
	}
	
	public void setBTypeWindow(boolean isLocal, int windowFrom, int windowTo, boolean isPercentage){
		this.isLocal = isLocal;
		this.windowFrom = windowFrom;
		this.windowTo = windowTo;
		this.isPercentage = isPercentage;
		if(this.isLocal){
			this.name += "_" + this.isPercentage + "_" + this.windowFrom + "_" + this.windowTo;
			if(this.isPercentage)
				this.details += ", PercentageWindow(" + this.windowFrom + "," + this.windowTo + ")";			
			else
				this.details += ", Window(" + this.windowFrom + "," + this.windowTo + ")";						
		}		
	}

	@Override
	public Object computeDNA(FastaFormat fastaFormat) {
		throw new Error("BasicPhysiochemicalFeature.computeDNA should not be called!");	
	}

	@Override
	public Object computeProtein(FastaFormat fastaFormat,
			int scoringMatrixIndex,int countingStyleIndex,ScoringMatrix scoringMatrix) {
		String sequence = fastaFormat.getSequence().toUpperCase();
    	int startIndex = fastaFormat.getStartIndex(this.windowFrom,this.isPercentage);
    	int endIndex = fastaFormat.getEndIndex(this.windowTo,this.isPercentage);
    	int windowSize = endIndex - startIndex + 1;
		
		//Any code before this is general for all
    	
		int windowLength = sequence.length() - startIndex;
		if(endIndex+1 > sequence.length())
			endIndex = sequence.length() - 1;			
		if(windowLength > windowSize)
			windowLength = windowSize;
		if(this.isLocal == false){
			startIndex = 0;
			endIndex = sequence.length() - 1;
			windowLength = sequence.length();
		}
		//This feature is not applicable to this sequence
		if(startIndex >= sequence.length())
			return 0.0;
		if(startIndex > endIndex)
			throw new Error("startIndex > endIndex @ BPC.computeProtein");	
		if(startIndex < 0)
			throw new Error("startIndex is < 0 @ BPC.computeProtein");
		//Simply return the one that is smaller in length because endIndex could be longer than sequence length			
		StringTokenizer stName = new StringTokenizer(this.getName(),"_");
		stName.nextToken();
		String tokenName = stName.nextToken();
		double netCharge;
		switch(BasicPhysioTableModel.BasicPhysiochemicalType.valueOf(tokenName)){
		case Length: return windowLength;
		case Mass: return getMass(sequence.substring(startIndex, endIndex + 1));
		case pIValue: return getPIValue(sequence.substring(startIndex, endIndex + 1));
		//Net Charge at pH 7
		case NetCharge: return getNetCharge(sequence.substring(startIndex, endIndex + 1));
		case MeanNetCharge: return getNetCharge(sequence.substring(startIndex, endIndex + 1)) / windowLength;
		//Absolute Net Charge at pH 7
		case AbsoluteNetCharge: 			
			netCharge = getNetCharge(sequence.substring(startIndex, endIndex + 1));
			if(netCharge < 0) netCharge *= -1;
			return netCharge;
		//Absolute Mean Net Charge at pH 7
		case AbsoluteMeanNetCharge:			
			netCharge = getNetCharge(sequence.substring(startIndex, endIndex + 1));
			if(netCharge < 0) netCharge *= -1;
			return netCharge / windowLength;
		case HydrophobicityTotal: return getHydrophobicity(sequence.substring(startIndex, endIndex + 1));
		case HydrophobicityMean: 
			return getHydrophobicity(sequence.substring(startIndex, endIndex + 1)) / windowLength;
		case OrderAminoAcidTotal:
			return getOrderAminoAcid(sequence.substring(startIndex, endIndex + 1));
		case OrderAminoAcidMean:
			return getOrderAminoAcid(sequence.substring(startIndex, endIndex + 1)) / windowLength;
		case DisorderAminoAcidTotal:
			return getDisorderAminoAcid(sequence.substring(startIndex, endIndex + 1));
		case DisorderAminoAcidMean:
			return getDisorderAminoAcid(sequence.substring(startIndex, endIndex + 1)) / windowLength;
		case OrderDisorderAADifferenceTotal:
			return getOrderAminoAcid(
					sequence.substring(startIndex, endIndex + 1)) - 
					getDisorderAminoAcid(sequence.substring(startIndex, endIndex + 1));
		case OrderDisorderAADifferenceMean:
			return (getOrderAminoAcid(
					sequence.substring(startIndex, endIndex + 1)) - 
					getDisorderAminoAcid(sequence.substring(startIndex, endIndex + 1))) / 
					windowLength;
		default: throw new Error("Unknown: " + tokenName);
		}		
	}
	
	private double getOrderAminoAcid(String sequence){
		int orderAminoAcidNum = 0;
		for(int x = 0; x < sequence.length(); x++){
			if(GenerateArff.orderAminoAcid.containsKey(sequence.charAt(x)))
				orderAminoAcidNum += GenerateArff.orderAminoAcid.get(sequence.charAt(x));
		}
		return orderAminoAcidNum;
	}
	
	private double getDisorderAminoAcid(String sequence){
		int disorderAminoAcidNum = 0;
		for(int x = 0; x < sequence.length(); x++){
			if(GenerateArff.disorderAminoAcid.containsKey(sequence.charAt(x)))
				disorderAminoAcidNum += GenerateArff.disorderAminoAcid.get(sequence.charAt(x));
		}
		return disorderAminoAcidNum;
	}
	
	private double getPIValue(String sequence){
		int aspNumber = 0;
		int gluNumber = 0;
		int cysNumber = 0;
		int tyrNumber = 0;
		int hisNumber = 0;
		int lysNumber = 0;
		int argNumber = 0;		
		for(int x = 0; x < sequence.length(); x++){
			switch(sequence.charAt(x)){
			case 'D': aspNumber++; break;
			case 'E': gluNumber++; break;
			case 'C': cysNumber++; break;
			case 'Y': tyrNumber++; break;
			case 'H': hisNumber++; break;
			case 'K': lysNumber++; break;
			case 'R': argNumber++; break;
			}
		}
		
		double pH = 7.00;
		double QN1=-1/(1+Math.pow(10,(3.65-pH)));          //C-terminal charge
	    double QN2=-aspNumber/(1+Math.pow(10,(3.9-pH)));            //D charge
	    double QN3=-gluNumber/(1+Math.pow(10,(4.07-pH)));            //E charge
	    double QN4=-cysNumber/(1+Math.pow(10,(8.18-pH)));            //C charge
	    double QN5=-tyrNumber/(1+Math.pow(10,(10.46-pH)));        //Y charge
	    double QP1=hisNumber/(1+Math.pow(10,(pH-6.04)));            //H charge
	    double QP2=1/(1+Math.pow(10,(pH-8.2)));                //NH2charge
	    double QP3=lysNumber/(1+Math.pow(10,(pH-10.54)));            //K charge
	    double QP4=argNumber/(1+Math.pow(10,(pH-12.48)));            //R charge
	    double NQ=QN1+QN2+QN3+QN4+QN5+QP1+QP2+QP3+QP4;
	    
	    if(NQ > 0){
	    	while(NQ > 0){
	    		pH += 0.01;
	    		QN1=-1/(1+Math.pow(10,(3.65-pH)));          //C-terminal charge
	    		QN2=-aspNumber/(1+Math.pow(10,(3.9-pH)));            //D charge
	    	    QN3=-gluNumber/(1+Math.pow(10,(4.07-pH)));            //E charge
	    	    QN4=-cysNumber/(1+Math.pow(10,(8.18-pH)));            //C charge
	    	    QN5=-tyrNumber/(1+Math.pow(10,(10.46-pH)));        //Y charge
	    	    QP1=hisNumber/(1+Math.pow(10,(pH-6.04)));            //H charge
	    	    QP2=1/(1+Math.pow(10,(pH-8.2)));                //NH2charge
	    	    QP3=lysNumber/(1+Math.pow(10,(pH-10.54)));            //K charge
	    	    QP4=argNumber/(1+Math.pow(10,(pH-12.48)));            //R charge
	    	    NQ=QN1+QN2+QN3+QN4+QN5+QP1+QP2+QP3+QP4;
	    	}	    	
	    }else if(NQ < 0){
	    	while(NQ < 0){
	    		pH -= 0.01;
	    		QN1=-1/(1+Math.pow(10,(3.65-pH)));          //C-terminal charge
	    		QN2=-aspNumber/(1+Math.pow(10,(3.9-pH)));            //D charge
	    	    QN3=-gluNumber/(1+Math.pow(10,(4.07-pH)));            //E charge
	    	    QN4=-cysNumber/(1+Math.pow(10,(8.18-pH)));            //C charge
	    	    QN5=-tyrNumber/(1+Math.pow(10,(10.46-pH)));        //Y charge
	    	    QP1=hisNumber/(1+Math.pow(10,(pH-6.04)));            //H charge
	    	    QP2=1/(1+Math.pow(10,(pH-8.2)));                //NH2charge
	    	    QP3=lysNumber/(1+Math.pow(10,(pH-10.54)));            //K charge
	    	    QP4=argNumber/(1+Math.pow(10,(pH-12.48)));            //R charge
	    	    NQ=QN1+QN2+QN3+QN4+QN5+QP1+QP2+QP3+QP4;
	    	}	    	
	    }
	    return pH;		
	}
	private double getNetCharge(String sequence){
		double netCharge = 0.0;
		for(int x = 0; x < sequence.length(); x++){	
			if(GenerateArff.aminoAcidCharge.containsKey(sequence.charAt(x)))
				netCharge += GenerateArff.aminoAcidCharge.get(sequence.charAt(x));			
		}
		return netCharge;
	}
	private double getMass(String sequence){
		double mass = 0.0;		
		for(int x = 0; x < sequence.length(); x++){			
			//if(GenerateArff.aminoAcidMass.containsKey(sequence.charAt(x)))
				mass += GenerateArff.aminoAcidMass.get(sequence.charAt(x));
			//else{
				//System.err.println("Check Sequence: " + sequence);
				//throw new Error("UnKnown Character in GetMass: " + sequence.charAt(x));				
			//}
		}
		return mass;
	}
	
	private double getHydrophobicity(String sequence){
		double hydro = 0.0;
		for(int x = 0; x < sequence.length(); x++){			
			//if(GenerateArff.aminoAcidHydrophobicityNormalized.containsKey(sequence.charAt(x)))
				hydro += GenerateArff.aminoAcidHydrophobicityNormalized.get(sequence.charAt(x));
			//else{
				//System.err.println("Check Sequence: " + sequence);
				//throw new Error("UnKnown Character In getHydrophobicity" + sequence.charAt(x));				
			//}
		}		
		return hydro;
	}
	
	public static BasicPhysiochemicalFeature randomlyGenerate(int windowFrom, int windowTo, Random rand){		
		String featureName = "B";
		BasicPhysioTableModel.BasicPhysiochemicalType[] basicPhysiochemicalType = 
			BasicPhysioTableModel.BasicPhysiochemicalType.values();
		featureName += "_" + basicPhysiochemicalType[rand.nextInt(basicPhysiochemicalType.length)];
		//here, i do not consider generating global because it is already done so since there are only 15 global BPC
		boolean isPercentage = rand.nextBoolean();
		featureName += "_" + isPercentage;
		int[] window;
		if(isPercentage)
			window = Feature.getWindow(0, 100, rand);
		else
			window = Feature.getWindow(windowFrom, windowTo, rand);
		featureName += "_" + window[0] + "_" + window[1];								
		return new BasicPhysiochemicalFeature(featureName);
	}

	@Override
	public Feature mutate(Random rand, int windowMin, int windowMax) {
		//for BPC, very simple. Only mutate the windowLocation
		StringTokenizer st = new StringTokenizer(this.name, "_");
		String featureName = st.nextToken() + "_" + st.nextToken() + "_" + this.isPercentage;	
		int[] window;
		if(this.isPercentage)
			window = Feature.getWindow(0, 100, rand);
		else
			window = Feature.getWindow(windowFrom, windowTo, rand);	
		featureName += "_" + window[0] + "_" + window[1];
		return new BasicPhysiochemicalFeature(featureName);
	}
}
