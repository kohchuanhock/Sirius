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


import java.util.Enumeration;
import java.util.Hashtable;

import sirius.trainer.main.ScoringMatrix;
import sirius.trainer.step2.Physiochemical2;
import sirius.utils.FastaFormat;

public class GenerateArff{	
	
	public static Hashtable<Character, Double> aminoAcidHydrophobicity_neg;
	public static Hashtable<Character, Double> orderDifferenceAminoAcid;
	public static Hashtable<Character, Double> disorderDifferenceAminoAcid;
	public static Hashtable<Character, Double> aminoAcidPKa_wrt7_pos;
	public static Hashtable<Character, Double> aminoAcidPKa_wrt7_neg;
	public static Hashtable<Character, Double> aminoAcidCharge_neg;
	public static Hashtable<Character, Double> orderAminoAcid;
	public static Hashtable<Character, Double> disorderAminoAcid;
	public static Hashtable<Character, Double> aminoAcidCharge;
	public static Hashtable<Character, Double> aminoAcidMass;
	public static Hashtable<Character, Double> aminoAcidHydrophobicityNormalized;
	public static Hashtable<Character, Double> aminoAcidHydrophobicity;
	
	static{
		initializeHashtable();
	}
	
    public GenerateArff(){
    	initializeHashtable();
    }
    
    private static void initializeHashtable(){		
		initializeHydrophobicity();
		initializeMassHashtable();
		initializeOrder();
		initializeDisorder();
		initializeCharge();
		initializeHydrophobicityNeg();
		intializePKAwrt7Pos();
		intializePKAwet7Neg();
		initializeOrderDifference();
		initializeDisorderDifference();
		initializeChargeNeg();
	}
    
    private static void initializeChargeNeg(){
    	aminoAcidCharge_neg = new Hashtable<Character, Double>();
		aminoAcidCharge_neg.put('D', 1.0);//aspartate - asp
		aminoAcidCharge_neg.put('E', 1.0);//glutamate - glu
		aminoAcidCharge_neg.put('H', -0.5);//histidine - his
		aminoAcidCharge_neg.put('K', -1.0);//lysine - lys
		aminoAcidCharge_neg.put('R', -2.0);//arginine - arg	
    }
    
    private static void initializeDisorderDifference(){
    	disorderDifferenceAminoAcid = new Hashtable<Character, Double>();
		disorderDifferenceAminoAcid.put('N', -1.0);
		disorderDifferenceAminoAcid.put('C', -1.0);
		disorderDifferenceAminoAcid.put('I', -1.0);
		disorderDifferenceAminoAcid.put('L', -1.0);
		disorderDifferenceAminoAcid.put('F', -1.0);
		disorderDifferenceAminoAcid.put('W', -1.0);
		disorderDifferenceAminoAcid.put('Y', -1.0);
		disorderDifferenceAminoAcid.put('V', -1.0);		
		disorderDifferenceAminoAcid.put('A', 1.0);
		disorderDifferenceAminoAcid.put('R', 1.0);
		disorderDifferenceAminoAcid.put('Q', 1.0);
		disorderDifferenceAminoAcid.put('E', 1.0);
		disorderDifferenceAminoAcid.put('G', 1.0);
		disorderDifferenceAminoAcid.put('K', 1.0);
		disorderDifferenceAminoAcid.put('P', 1.0);
		disorderDifferenceAminoAcid.put('S', 1.0);
		disorderDifferenceAminoAcid.put('Z', 1.0);			
    }
    
    private static void initializeOrderDifference(){
    	orderDifferenceAminoAcid = new Hashtable<Character, Double>();
		orderDifferenceAminoAcid.put('N', 1.0);
		orderDifferenceAminoAcid.put('C', 1.0);
		orderDifferenceAminoAcid.put('I', 1.0);
		orderDifferenceAminoAcid.put('L', 1.0);
		orderDifferenceAminoAcid.put('F', 1.0);
		orderDifferenceAminoAcid.put('W', 1.0);
		orderDifferenceAminoAcid.put('Y', 1.0);
		orderDifferenceAminoAcid.put('V', 1.0);		
		orderDifferenceAminoAcid.put('A', -1.0);
		orderDifferenceAminoAcid.put('R', -1.0);
		orderDifferenceAminoAcid.put('Q', -1.0);
		orderDifferenceAminoAcid.put('E', -1.0);
		orderDifferenceAminoAcid.put('G', -1.0);
		orderDifferenceAminoAcid.put('K', -1.0);
		orderDifferenceAminoAcid.put('P', -1.0);
		orderDifferenceAminoAcid.put('S', -1.0);
		orderDifferenceAminoAcid.put('Z', -1.0);	
    }
    
    private static void intializePKAwet7Neg(){
    	aminoAcidPKa_wrt7_neg = new Hashtable<Character, Double>();
		aminoAcidPKa_wrt7_neg.put('C', (8.28 - 7) * -1);
		aminoAcidPKa_wrt7_neg.put('D', (3.8 - 7) * -1);
		aminoAcidPKa_wrt7_neg.put('E', (4.3 - 7) * -1);
		aminoAcidPKa_wrt7_neg.put('H', (6.08 - 7) * -1);
		aminoAcidPKa_wrt7_neg.put('K', (10.5 - 7) * -1);
		aminoAcidPKa_wrt7_neg.put('R', (12.0 - 7) * -1);
		aminoAcidPKa_wrt7_neg.put('Y', (10.1 - 7) * -1);
    }
    
    private static void intializePKAwrt7Pos(){
    	aminoAcidPKa_wrt7_pos = new Hashtable<Character, Double>();
		aminoAcidPKa_wrt7_pos.put('C', 8.28 - 7);
		aminoAcidPKa_wrt7_pos.put('D', 3.8 - 7);
		aminoAcidPKa_wrt7_pos.put('E', 4.3 - 7);
		aminoAcidPKa_wrt7_pos.put('H', 6.08 - 7);
		aminoAcidPKa_wrt7_pos.put('K', 10.5 - 7);
		aminoAcidPKa_wrt7_pos.put('R', 12.0 - 7);
		aminoAcidPKa_wrt7_pos.put('Y', 10.1 - 7);
    }
    
    private static void initializeHydrophobicityNeg(){
    	//This table is the opposite in sign of Kyte-Doolittle, used for the SequenceQuencher class to find more hydrophilic
		aminoAcidHydrophobicity_neg = new Hashtable<Character, Double>();
		aminoAcidHydrophobicity_neg.put('A', -1.8);//alanine - Ala
		aminoAcidHydrophobicity_neg.put('B', 3.5);//aspartate asp or asparagine asn
		aminoAcidHydrophobicity_neg.put('C', -2.5);//cystine - cys	
		aminoAcidHydrophobicity_neg.put('D', 3.5);//aspartate - asp
		aminoAcidHydrophobicity_neg.put('E', 3.5);//glutamate - glu
		aminoAcidHydrophobicity_neg.put('F', -2.8);//phenylalanine - phe
		aminoAcidHydrophobicity_neg.put('G', 0.4);//glycine - gly
		aminoAcidHydrophobicity_neg.put('H', 3.2);//histidine - his
		aminoAcidHydrophobicity_neg.put('I', -4.5);//isoleucine - ile
		aminoAcidHydrophobicity_neg.put('K', 3.9);//lysine - lys
		aminoAcidHydrophobicity_neg.put('L', -3.8);//leucine - leu
		aminoAcidHydrophobicity_neg.put('M', -1.9);//methionine - met
		aminoAcidHydrophobicity_neg.put('N', 3.5);//asparagine - asn
		aminoAcidHydrophobicity_neg.put('P', 1.6);//proline - pro
		aminoAcidHydrophobicity_neg.put('Q', 3.5);//glutamine - gln
		aminoAcidHydrophobicity_neg.put('R', 4.5);//arginine - arg
		aminoAcidHydrophobicity_neg.put('S', 0.8);//serine - ser
		aminoAcidHydrophobicity_neg.put('T', 0.7);//threonine - thr
		aminoAcidHydrophobicity_neg.put('V', -4.2);//valine - val
		aminoAcidHydrophobicity_neg.put('W', 0.9);//tryptophan - trp
		aminoAcidHydrophobicity_neg.put('Y', 1.3);//tyrosine - tyr
		aminoAcidHydrophobicity_neg.put('Z', 3.5);//glutamate or glutamine
		aminoAcidHydrophobicity_neg.put('X', 0.0);//any
		aminoAcidHydrophobicity_neg.put('*', 0.0);//translation stop
		aminoAcidHydrophobicity_neg.put('U', 0.0);//Unknown Value					
    }
    
    private static void initializeHydrophobicity(){
		//The aminoAcidHydrophobicity is based on Kyte-Doolittle Approximation
    	aminoAcidHydrophobicity = new Hashtable<Character, Double>();
		aminoAcidHydrophobicity.put('A', 1.8);//alanine - Ala
		aminoAcidHydrophobicity.put('B', -3.5);//aspartate asp or asparagine asn
		aminoAcidHydrophobicity.put('C', 2.5);//cystine - cys	
		aminoAcidHydrophobicity.put('D', -3.5);//aspartate - asp
		aminoAcidHydrophobicity.put('E', -3.5);//glutamate - glu
		aminoAcidHydrophobicity.put('F', 2.8);//phenylalanine - phe
		aminoAcidHydrophobicity.put('G', -0.4);//glycine - gly
		aminoAcidHydrophobicity.put('H', -3.2);//histidine - his
		aminoAcidHydrophobicity.put('I', 4.5);//isoleucine - ile
		aminoAcidHydrophobicity.put('K', -3.9);//lysine - lys
		aminoAcidHydrophobicity.put('L', 3.8);//leucine - leu
		aminoAcidHydrophobicity.put('M', 1.9);//methionine - met
		aminoAcidHydrophobicity.put('N', -3.5);//asparagine - asn
		aminoAcidHydrophobicity.put('P', -1.6);//proline - pro
		aminoAcidHydrophobicity.put('Q', -3.5);//glutamine - gln
		aminoAcidHydrophobicity.put('R', -4.5);//arginine - arg
		aminoAcidHydrophobicity.put('S', -0.8);//serine - ser
		aminoAcidHydrophobicity.put('T', -0.7);//threonine - thr
		aminoAcidHydrophobicity.put('V', 4.2);//valine - val
		aminoAcidHydrophobicity.put('W', -0.9);//tryptophan - trp
		aminoAcidHydrophobicity.put('Y', -1.3);//tyrosine - tyr
		aminoAcidHydrophobicity.put('Z', -3.5);//glutamate or glutamine
		aminoAcidHydrophobicity.put('X', 0.0);//any		
		aminoAcidHydrophobicity.put('*', 0.0);//translation stop
		aminoAcidHydrophobicity.put('U', 0.0);//Unknown Value
		
		//Normalization
		aminoAcidHydrophobicityNormalized = new Hashtable<Character, Double>();
		for(Enumeration<Character> e = aminoAcidHydrophobicity.keys(); e.hasMoreElements();){
			Character tempChar = e.nextElement();
			aminoAcidHydrophobicityNormalized.put(tempChar, (aminoAcidHydrophobicity.get(tempChar) + 4.5)/9 );
		}
	}
	
	private static void initializeMassHashtable(){
		//the mass is based on the average and from several websites which tallies
		//http://www.matrixscience.com/help/aa_help.html
		//http://www.i-mass.com/guide/aamass.html
		//http://education.expasy.org/student_projects/isotopident/htdocs/aa-list.html - the values used directly						
		aminoAcidMass = new Hashtable<Character, Double>();		
		aminoAcidMass.put('A',  71.0788);
		aminoAcidMass.put('C',  103.1388);
		aminoAcidMass.put('D',	115.0886);
		aminoAcidMass.put('E',	129.1155);
		aminoAcidMass.put('F',	147.1766);
		aminoAcidMass.put('G',	57.0519);
		aminoAcidMass.put('H',	137.1411);
		aminoAcidMass.put('I',	113.1594);		
		aminoAcidMass.put('K',	128.1741);	
		aminoAcidMass.put('L',	113.1594);
		aminoAcidMass.put('M',  131.1926);
		aminoAcidMass.put('N',	114.1038);
		aminoAcidMass.put('P',	97.1167);
		aminoAcidMass.put('Q',	128.1307);
		aminoAcidMass.put('R',	156.1875);
		aminoAcidMass.put('S',	87.0782);
		aminoAcidMass.put('T',	101.1051);
		aminoAcidMass.put('V',	99.1326);
		aminoAcidMass.put('W',	186.2132);
		aminoAcidMass.put('Y',	163.1760);
		//as B could be D or N, hence took both and divide by 2		
		aminoAcidMass.put('B',	(115.0886 + 114.1038) / 2.0);
		//as Z could be E or Q, hence took both and divide by 2
		aminoAcidMass.put('Z',	(129.1155 + 128.1307) / 2.0);
		aminoAcidMass.put('X',  0.0);			
		aminoAcidMass.put('*', 0.0);
		aminoAcidMass.put('U', 0.0);//Unknown Value
	}
	
	private static void initializeCharge(){
		//These charges are based on wikipedia
		//Strongly ==> 2.0
		//Weakly ==> 0.5
		aminoAcidCharge = new Hashtable<Character, Double>();
		aminoAcidCharge.put('D', -1.0);//aspartate - asp
		aminoAcidCharge.put('E', -1.0);//glutamate - glu
		aminoAcidCharge.put('H', 0.5);//histidine - his
		aminoAcidCharge.put('K', 1.0);//lysine - lys
		aminoAcidCharge.put('R', 2.0);//arginine - arg	
	}
	
	private static void initializeDisorder(){
		disorderAminoAcid = new Hashtable<Character, Double>();
		disorderAminoAcid.put('A', 1.0);
		disorderAminoAcid.put('R', 1.0);
		disorderAminoAcid.put('Q', 1.0);
		disorderAminoAcid.put('E', 1.0);
		disorderAminoAcid.put('G', 1.0);
		disorderAminoAcid.put('K', 1.0);
		disorderAminoAcid.put('P', 1.0);
		disorderAminoAcid.put('S', 1.0);
		disorderAminoAcid.put('Z', 1.0);		
	}
	
	private static void initializeOrder(){
		//The order/disorder AA are based on paper titled "Disorder in the nuclear pore complex: 
		//The FG repeat regions of nucleoporins are natively unfolded" by Daniel P. Denning et al
		orderAminoAcid = new Hashtable<Character, Double>();
		orderAminoAcid.put('N', 1.0);
		orderAminoAcid.put('C', 1.0);
		orderAminoAcid.put('I', 1.0);
		orderAminoAcid.put('L', 1.0);
		orderAminoAcid.put('F', 1.0);
		orderAminoAcid.put('W', 1.0);
		orderAminoAcid.put('Y', 1.0);
		orderAminoAcid.put('V', 1.0);		
	}      
    
    public static Object getMatchCount(String header, String sequence, Feature featureData,
    	int scoringMatrixIndex,int countingStyleIndex,ScoringMatrix scoringMatrix) throws Exception{
    	FastaFormat fastaFormat = new FastaFormat(header, sequence);
    	return getMatchCount(fastaFormat, featureData,scoringMatrixIndex,countingStyleIndex,scoringMatrix);
    }
    
    public static Object getMatchCount(FastaFormat fastaFormat,
    		Feature featureData,
    		int scoringMatrixIndex,
    		int countingStyleIndex,
    		ScoringMatrix scoringMatrix) throws Exception{
    	//This is one of the most complicated method but this is a very impt method
    	//hence the correctness is of very high importance
    	if(featureData.getType() == 'Z'){
    		//This is a classifier feature
    		return ((ClassifierFeature)featureData).compute(fastaFormat);
    	}else if(scoringMatrixIndex == -1){
    		//do this because for DNA sequences getMatchCount already works
    		//split them so that while making it work for Protein sequences wont affect the DNA sequences
    		//return getMatchCountDNA(fastaFormat,featureData);
    		return featureData.computeDNA(fastaFormat);
    	}else{
    		//return getMatchCountProtein(fastaFormat,featureData,scoringMatrixIndex,countingStyleIndex,scoringMatrix);
    		return featureData.computeProtein(fastaFormat, scoringMatrixIndex, countingStyleIndex, 
    				scoringMatrix);
    	}    	
    }        
    
    public static boolean validateFeatureDNA(String feature){
    	for(int x = 0; x < feature.length(); x++){
    		switch(feature.charAt(x)){
    			case 'A': //adenosine
				case 'C': //cytosine
				case 'T': //thymine
				case 'G': //guanine
				case 'X': //any
					break;
    			default: return false;
    		}
    	}
    	return true;
    }
		
    public static boolean validateFeatureProtein(String feature){
    	for(int x = 0; x < feature.length(); x++){
    		switch(feature.charAt(x)){
    			case 'A': //alanine
				case 'C': //cystine
				case 'D': //aspartate
				case 'E': //glutamate
				case 'F': //phenylalanine
				case 'G': //glycine
				case 'H': //histidine
				case 'I': //isoleucine
				case 'K': //lysine
				case 'L': //leucine							
				case 'M': //methionine
				case 'N': //asparagine
				case 'P': //proline
				case 'Q': //glutamine
				case 'R': //arginine
				case 'S': //serine
				case 'T': //threonine						
				case 'V': //valine
				case 'W': //tryptophan
				case 'Y': //tyrosine						
				case 'X': //any
					break;
    			default: return false;
    		}
    	}
    	return true;
    }
    
    public static boolean validatePhysiochemical2FeatureProtein(String feature, Physiochemical2 p2){      	
    	boolean found = true;
    	String validString = "X";    	
    	for(int x = 0; x < p2.getAminoAcidTypeArrayList().size(); x++){
			validString += p2.getLetter(x);
		}
    	for(int y = 0; y < feature.length(); y++){    			
    		if(validString.indexOf("" + feature.charAt(y)) == -1){
    			found = false;
    			break;
    		}
    	}
    	return found;
    }
    
    public static boolean validatePhysiochemicalFeatureProtein(String feature){
    	for(int x = 0; x < feature.length(); x++){
    		switch(feature.charAt(x)){
    		case 'H': //Hydrophobic (A, C, F, I, L, M, V)
			case 'P': //(+ve) Charge (H, K, R)
			case 'A': //Acidic (D, E, H)
			case 'O': //Order (N, C, I, L, F, W, Y, V)
			case 'L': //Hydrophilic (B, D, E, G, H, K, N, P, Q, R, S, T, W, Y, Z)
			case 'N': //(-ve) Charge (D, E)
			case 'K': //Alkaline (C, K, R, Y)
			case 'D': //Disorder (A, R, Q, E, G, K, P, S, Z)
			case 'X': //any
					break;
    			default: return false;
    		}
    	}
    	return true;
    }
   
    public static boolean isPhysiochemicalMatch(char feature, char sequence){    	    	
    	if(feature == 'H'){
    		switch(sequence){
    		case 'A': case 'C': case 'F': case 'I': case 'L': case 'M': case 'V': 
    		return true;
    		default: return false;
    		}    		
    	}else if(feature == 'P'){
    		switch(sequence){
    		case 'H': case 'K': case 'R':  
    		return true;
    		default: return false;
    		}    		    		
    	}else if(feature == 'A'){
    		switch(sequence){
    		case 'D': case 'E': case 'H': 
    		return true;
    		default: return false;
    		}    	    		
    	}else if(feature == 'O'){
    		switch(sequence){
    		case 'N': case 'C': case 'I': case 'L': case 'F': case 'W': case 'Y': case 'V': 
    		return true;
    		default: return false;
    		}    			
    	}else if(feature == 'L'){
    		switch(sequence){
    		case 'B': case 'D': case 'E': case 'G': case 'H': case 'K': case 'N':
    		case 'P': case 'Q': case 'R': case 'S': case 'T': case 'W': case 'Y': case 'Z':
    		return true;
    		default: return false;
    		}    	
    	}else if(feature == 'N'){
    		switch(sequence){
    		case 'D': case 'E': 
    		return true;
    		default: return false;
    		}    		    		
    	}else if(feature == 'K'){
    		switch(sequence){
    		case 'C': case 'K': case 'R': case 'Y': 
    		return true;
    		default: return false;
    		}    		
    	}else if(feature == 'D'){
    		switch(sequence){
    		case 'A': case 'R': case 'Q': case 'E': case 'G': case 'K': case 'P': case 'S': case 'Z':
    		return true;
    		default: return false;
    		}    		
    	}else{
    		throw new Error("Unknown Feature: " + feature);
    	}		
    }
}