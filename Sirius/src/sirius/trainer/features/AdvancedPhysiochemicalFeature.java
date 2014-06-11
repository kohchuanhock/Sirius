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

import java.util.Hashtable;
import java.util.Random;
import java.util.StringTokenizer;

import sirius.misc.sequencevisualizer.SequenceQuencher;
import sirius.trainer.main.ScoringMatrix;
import sirius.utils.FastaFormat;


public class AdvancedPhysiochemicalFeature extends Feature{
	private int lengthCutoff;
	private Double valueCutoff;
	public static enum AdvancedPhysioType{Hydrophobic, Hydrophilic, Alkaline, Acidic, OrderAA, DisorderAA, 
		NetChargePositive, NetChargeNegative, O2DisorderAA, D2OrderAA};
	public static enum AdvancedValueType{NumOfRegion, TotalValue, TopValue, TotalSizeAbsolute, 
		TopLocationAbsolute, TotalSizeRelative, TopLocationRelative, AverageValue, TopSizeAbsolute,
		AverageSizeAbsolute, TopSizeRelative, AverageSizeRelative};
		
	public AdvancedPhysiochemicalFeature(char type, String name, String detail, boolean box, double valueCutoff, int lengthCutoff){		
		//super(name, detail, type);
		//this.valueCutoff = valueCutoff;
		//this.lengthCutoff = lengthCutoff;
		this(name);
	}
	
	public AdvancedPhysiochemicalFeature(String name, char type, int lengthCutoff, double valueCutoff, String typeString){
		super(name,null,type);
		StringTokenizer st = new StringTokenizer(name,"_");
		st.nextToken();//A
		String featureType = st.nextToken();//the featureType
		this.valueCutoff = valueCutoff;
		this.lengthCutoff = lengthCutoff;
		setDetails(featureType, typeString);
	}
	
	public AdvancedPhysiochemicalFeature(String name){
		super(name, null, name.charAt(0));
		StringTokenizer st = new StringTokenizer(name,"_");
		st.nextToken();//A
		String featureType = st.nextToken();//the featureType
		String typeString = st.nextToken();
		String valueCutoffString = st.nextToken();	
		String lengthCutoffString = st.nextToken();
		this.valueCutoff = Double.parseDouble(valueCutoffString);
		this.lengthCutoff = Integer.parseInt(lengthCutoffString);
		setDetails(featureType, typeString);
	}
	
	public AdvancedPhysiochemicalFeature(String line, char type){
		super(null,null,type);
		String name = line.substring(line.indexOf("Name: ") + ("Name: ").length());
		this.name = name;
		StringTokenizer st = new StringTokenizer(name, "_");
		st.nextToken();//A
		String featureType = st.nextToken();//the featureType
		String typeString = st.nextToken();
		String valueCutoffString = st.nextToken();	
		String lengthCutoffString = st.nextToken();
		this.valueCutoff = Double.parseDouble(valueCutoffString);
		this.lengthCutoff = Integer.parseInt(lengthCutoffString);
		setDetails(featureType, typeString);
	}
	
	private void setDetails(String featureType, String typeString){
		//this.details = "(" + featureType + ") ";
		switch(AdvancedValueType.valueOf(typeString)){
		case NumOfRegion: this.details += "Number of regions > cutoff"; break;
		case TotalValue: this.details += "Total value for all regions > cutoff"; break;
		case TopValue: this.details += "Value of top region > cutoff"; break;
		case TotalSizeAbsolute: this.details += "Total size of all regions > cutoff (Absolute)"; break;
		case TopLocationAbsolute: this.details += "Location of top region > cutoff (Absolute)"; break;
		case TotalSizeRelative: this.details += "Total size of all regions > cutoff (Relative)"; break;
		case TopLocationRelative: this.details += "Location of top region > cutoff (Relative)"; break;
		case AverageValue: this.details += "Average value of all regions > cutoff"; break;
		case TopSizeAbsolute: this.details += "Size of top region > cutoff (Absolute)"; break;
		case AverageSizeAbsolute: this.details += "Average size of all regions > cutoff (Absolute)"; break;
		case TopSizeRelative: this.details += "Size of top region > cutoff (Relative)"; break;
		case AverageSizeRelative: this.details += "Average size of all regions > cutoff (Relative)"; break;
		default: throw new Error("A Type but Unknown typeString: " + typeString);
		}	
		this.details += ", " + featureType + ", Value(" + this.valueCutoff + "), " + "Length(" + this.lengthCutoff + ")";
	}
	
	public String saveString(String saveDirectory){
		if(type == 'A')
			return "Type: " + type + " Name: " + name;
		else
			return "UNKNOWN TYPE";
	}

	public int getLengthCutoff(){ return lengthCutoff; }
	
	public double getValueCutoff(){ return valueCutoff; }

	@Override
	public Object computeDNA(FastaFormat fastaFormat) {
		throw new Error("AdvancedPhysiochemicalFeature.computeDNA should not be called!");	
	}

	@Override
	public Object computeProtein(FastaFormat fastaFormat,
			int scoringMatrixIndex,int countingStyleIndex,ScoringMatrix scoringMatrix) {	
		
    	String sequence = fastaFormat.getSequence().toUpperCase();    	
		
		//Any code before this is general for all
		
		StringTokenizer stLocal = new StringTokenizer(this.getName(), "_");
		stLocal.nextToken();//A
		String featureType = stLocal.nextToken();
		String featureName = stLocal.nextToken();
		Hashtable<Character, Double> hashtable = null;
		switch(AdvancedPhysioType.valueOf(featureType)){
		case Hydrophobic: hashtable = GenerateArff.aminoAcidHydrophobicity; break;
		case Hydrophilic: hashtable = GenerateArff.aminoAcidHydrophobicity_neg; break;
		case Alkaline: hashtable = GenerateArff.aminoAcidPKa_wrt7_pos; break;
		case Acidic: hashtable = GenerateArff.aminoAcidPKa_wrt7_neg; break;
		case OrderAA: hashtable = GenerateArff.orderAminoAcid; break;
		case DisorderAA: hashtable = GenerateArff.disorderAminoAcid; break;
		case NetChargePositive: hashtable = GenerateArff.aminoAcidCharge; break;
		case NetChargeNegative: hashtable = GenerateArff.aminoAcidCharge_neg; break;
		case O2DisorderAA: hashtable = GenerateArff.orderDifferenceAminoAcid; break;
		case D2OrderAA: hashtable = GenerateArff.disorderDifferenceAminoAcid; break;
		default: throw new Error("Unknown featureType: " + featureType); 
		}
		
		SequenceQuencher sp = new SequenceQuencher(sequence, hashtable, this.getValueCutoff(), 
				this.getLengthCutoff(),	false, fastaFormat.getIndexLocation());
		if(sp.getNumOfRegion() == 0)
			return 0.0;
		switch(AdvancedValueType.valueOf(featureName)){
		case NumOfRegion: return sp.getNumOfRegion();
		case TotalValue: return sp.getTotalValue();
		case TopValue: return sp.getTopValue();
		case TotalSizeAbsolute: return sp.getTotalSizeAbsolute();
		case TopLocationAbsolute: return sp.getTopValueLocationAbsolute();
		case TotalSizeRelative: return sp.getTotalSizeRelative();
		case TopLocationRelative: return sp.getTopValueLocationRelative();
		case AverageValue: return sp.averageValue();
		case TopSizeAbsolute: return sp.getTopValueSizeAbsolute();
		case AverageSizeAbsolute: return sp.averageSizeAbsolute();
		case TopSizeRelative: return sp.getTopValueSizeRelative();
		case AverageSizeRelative: return sp.averageSizeRelative();
		default: throw new Error("Unknown featureName: " + featureName); 
		}					
	}
	
	public static AdvancedPhysiochemicalFeature randomlyGenerate(int windowFrom, int windowTo,Random rand){
		String featureName = "A";
		AdvancedPhysioType[] advancedPhysioType = AdvancedPhysioType.values();
		featureName += "_" + advancedPhysioType[rand.nextInt(advancedPhysioType.length)];
		AdvancedValueType[] advancedValueType = AdvancedValueType.values();
		featureName += "_" + advancedValueType[rand.nextInt(advancedValueType.length)];
		//limit both value to 3-20 first, allow it to change in mutation and crossover
		featureName += "_" + (rand.nextInt(17) + 3);
		featureName += "_" + (rand.nextInt(17) + 3);
		return new AdvancedPhysiochemicalFeature(featureName);
	}

	@Override
	public Feature mutate(Random rand, int windowMin, int windowMax) {
		//Do a simple mutation here. Just change the length or value cutoff
		StringTokenizer st = new StringTokenizer(this.name,"_");
		String featureName = st.nextToken() + "_" + st.nextToken() + "_" + st.nextToken();
		int value = Integer.parseInt(st.nextToken());
		int length = Integer.parseInt(st.nextToken());
		if(rand.nextBoolean()){
			//length cutoff
			if(rand.nextBoolean())
				length += rand.nextInt(length);
			else
				length -= rand.nextInt(length/2);
		}else{
			//value cutoff
			if(rand.nextBoolean())
				value += rand.nextInt(value);
			else
				value -= rand.nextInt(value/2);
		}
		featureName += "_" + value + "_" + length;
		if(this.name.equalsIgnoreCase(featureName))
			return this.mutate(rand, windowMin, windowMax);
		else
			return new AdvancedPhysiochemicalFeature(featureName);	
	}
}
