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
package sirius.trainer.step2;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Physiochemical2 {
	private String codingName;
	private List<String> classificationTypes;//stores the classification name 
	private List<String> aminoAcidTypes;//stores which AA belong to this classification
	private List<Character> classificationLetter;//stores the letter used to represent this name
	//stores all the different classification names
	public final static String[] codingNameList = {"Original","Hydrophobicities","Hydrophobicities2","Size",
		"Polarizability","Polarity","Acidity","Acidity2","Acidity3","Acidity4","Charge","Disorder"};
	
	public Physiochemical2(String codingName){
		this.codingName = codingName;		
		int index = Physiochemical2.nameToIndex(codingName);
		switch(index){
		case 0: setToOriginal(); break;
		case 1: setToHydrophobicities(); break;
		case 2: setToHydrophobicities2(); break;
		case 3: setToSize(); break;
		case 4: setToPolarizability(); break;
		case 5: setToPolarity(); break;
		case 6: setToAcidity(); break;
		case 7: setToAcidity2(); break;
		case 8: setToAcidity3(); break;
		case 9: setToAcidity4(); break;
		case 10: setToCharge(); break;
		case 11: setToDisorder(); break;
		default: throw new Error("Unknown Coding Name");
		}		
		this.classificationLetter.add('X');
	}	
	
	public List<String> getAminoAcidTypeArrayList(){
		return this.aminoAcidTypes;
	}
	
	public static int nameToIndex(String name){
		for(int x = 0; x < Physiochemical2.codingNameList.length; x++)
			if(name.equalsIgnoreCase(Physiochemical2.codingNameList[x]))
				return x;
		return -1;		
	}
	
	public static String indexToName(int index){
		if(index >=0 && index < Physiochemical2.codingNameList.length)
			return Physiochemical2.codingNameList[index];
		else
			return "Unknown";		
	}
	
	public List<Character> getClassificationLetter(){
		return this.classificationLetter;
	}	
	
	public Character getLetter(int index){
		return this.classificationLetter.get(index);
	}
	
	public void setCheckBox(JPanel panel, ArrayList<JCheckBox> proteinCheckBoxArrayList){
		for(int x = 0; x < this.classificationLetter.size(); x++){
			JCheckBox temp;
			if(this.classificationLetter.get(x) == 'X')
				temp = new JCheckBox(this.classificationLetter.get(x) + " = ANY",false);
			else
				temp = new JCheckBox(this.classificationLetter.get(x) + "",true);
			proteinCheckBoxArrayList.add(temp);
			panel.add(temp);
		}		
	}
		
	
	public void setPanel(JPanel panel){
		for(int x = 0; x < this.classificationTypes.size(); x++){
			String temp = this.classificationLetter.get(x) + " - " + this.classificationTypes.get(x) + " (";
			for(int y = 0; y < this.aminoAcidTypes.get(x).length(); y++){
				temp += this.aminoAcidTypes.get(x).charAt(y);
				if(y+1 < this.aminoAcidTypes.get(x).length())
					temp += ",";				
				else
					temp += ")";
			}
			panel.add(new JLabel(temp));
		}
	}
	
	public String getCodingName(){
		return this.codingName;
	}
	
	public String OriginalSequenceToPhysiochemical2Sequence(String originalSequence){
		String returnSequence = "";
		originalSequence = originalSequence.toUpperCase();
		for(int y = 0; y < originalSequence.length(); y++){
			char currentChar = originalSequence.charAt(y);
			int index = -1;
			for(int x = 0; x < this.aminoAcidTypes.size(); x++){
				if(this.aminoAcidTypes.get(x).indexOf("" + currentChar) != -1){
					index =  x;
					break;
				}				
			}
			if(index != -1)
				returnSequence += this.classificationLetter.get(index); 
			else
				//if the aminoacid does not belong to anywhere in particular then just set to ?
				returnSequence += "?";			
		}
		return returnSequence;
	}
	
	public boolean isMatch(char feature, char seq){		
		int index = -1;
		for(int x = 0; x < this.classificationLetter.size(); x++){
			if(this.classificationLetter.get(x) == feature)
				index = x;
		}
		if(this.aminoAcidTypes.get(index).indexOf("" + seq) != -1)
			return true;
		else
			return false;
	}
	
	private void setToOriginal(){
		this.classificationLetter = new ArrayList<Character>();
		this.classificationLetter.add('A');
		this.classificationLetter.add('C');
		this.classificationLetter.add('D');
		this.classificationLetter.add('E');
		this.classificationLetter.add('F');
		this.classificationLetter.add('G');
		this.classificationLetter.add('H');
		this.classificationLetter.add('I');
		this.classificationLetter.add('K');
		this.classificationLetter.add('L');
		this.classificationLetter.add('M');
		this.classificationLetter.add('N');
		this.classificationLetter.add('P');
		this.classificationLetter.add('Q');
		this.classificationLetter.add('R');
		this.classificationLetter.add('S');
		this.classificationLetter.add('T');
		this.classificationLetter.add('V');
		this.classificationLetter.add('W');
		this.classificationLetter.add('Y');
		this.aminoAcidTypes = new ArrayList<String>();
		this.aminoAcidTypes.add("ACDEFGHIKLMNPQRSTVWY");
	}
	
	private void setToDisorder(){		
		this.classificationTypes = new ArrayList<String>();
		this.classificationTypes.add("Order");
		this.classificationTypes.add("Disorder");
		this.classificationLetter = new ArrayList<Character>();
		this.classificationLetter.add('O');
		this.classificationLetter.add('D');
		this.aminoAcidTypes = new ArrayList<String>();
		this.aminoAcidTypes.add("NCILFWYV");
		this.aminoAcidTypes.add("ARQEGKPSZ");
	}
	
	private void setToCharge(){
		this.classificationTypes = new ArrayList<String>();
		this.classificationTypes.add("+ve charge");
		this.classificationTypes.add("-ve charge");
		this.classificationLetter = new ArrayList<Character>();
		this.classificationLetter.add('P');
		this.classificationLetter.add('N');
		this.aminoAcidTypes = new ArrayList<String>();
		this.aminoAcidTypes.add("HKR");
		this.aminoAcidTypes.add("DE");
	}
	
	private void setToAcidity4(){
		this.classificationTypes = new ArrayList<String>();
		this.classificationTypes.add("Acidic");
		this.classificationTypes.add("Akaline");
		this.classificationLetter = new ArrayList<Character>();
		this.classificationLetter.add('A');
		this.classificationLetter.add('K');
		this.aminoAcidTypes = new ArrayList<String>();
		this.aminoAcidTypes.add("DEH");
		this.aminoAcidTypes.add("LKRY");
	}
	
	private void setToAcidity3(){
		this.classificationTypes = new ArrayList<String>();
		this.classificationTypes.add("Acidic");
		this.classificationTypes.add("Basic");
		this.classificationTypes.add("Aromatic");
		this.classificationTypes.add("Amide");
		this.classificationTypes.add("Small hydroxyl");
		this.classificationTypes.add("Sulfur-containing");
		this.classificationTypes.add("Aliphatic 1");
		this.classificationTypes.add("Aliphatic 2");
		this.classificationLetter = new ArrayList<Character>();
		this.classificationLetter.add('A');
		this.classificationLetter.add('B');
		this.classificationLetter.add('R');
		this.classificationLetter.add('M');
		this.classificationLetter.add('S');
		this.classificationLetter.add('U');
		this.classificationLetter.add('L');
		this.classificationLetter.add('I');
		this.aminoAcidTypes = new ArrayList<String>();
		this.aminoAcidTypes.add("DE");
		this.aminoAcidTypes.add("HKR");
		this.aminoAcidTypes.add("FWY");
		this.aminoAcidTypes.add("NQ");
		this.aminoAcidTypes.add("ST");
		this.aminoAcidTypes.add("CM");
		this.aminoAcidTypes.add("AGP");
		this.aminoAcidTypes.add("ILV");
	}
	
	private void setToAcidity2(){
		this.classificationTypes = new ArrayList<String>();
		this.classificationTypes.add("Acidic");
		this.classificationTypes.add("Basic");
		this.classificationTypes.add("Aromatic");
		this.classificationTypes.add("Amide");
		this.classificationTypes.add("Small hydroxyl");
		this.classificationTypes.add("Sulfur-containing");
		this.classificationTypes.add("Aliphatic");
		this.classificationLetter = new ArrayList<Character>();
		this.classificationLetter.add('A');
		this.classificationLetter.add('B');
		this.classificationLetter.add('R');
		this.classificationLetter.add('M');
		this.classificationLetter.add('S');
		this.classificationLetter.add('U');
		this.classificationLetter.add('L');		
		this.aminoAcidTypes = new ArrayList<String>();
		this.aminoAcidTypes.add("DE");
		this.aminoAcidTypes.add("HKR");
		this.aminoAcidTypes.add("FWY");
		this.aminoAcidTypes.add("NQ");
		this.aminoAcidTypes.add("ST");
		this.aminoAcidTypes.add("CM");
		this.aminoAcidTypes.add("AGPILV");
	}
	
	private void setToAcidity(){
		this.classificationTypes = new ArrayList<String>();
		this.classificationTypes.add("Acidic");
		this.classificationTypes.add("Basic");
		this.classificationTypes.add("Polar");
		this.classificationTypes.add("Nonpolar");
		this.classificationLetter = new ArrayList<Character>();
		this.classificationLetter.add('A');
		this.classificationLetter.add('B');
		this.classificationLetter.add('P');
		this.classificationLetter.add('N');
		this.aminoAcidTypes = new ArrayList<String>();
		this.aminoAcidTypes.add("DE");
		this.aminoAcidTypes.add("HKR");
		this.aminoAcidTypes.add("CGNQSTY");
		this.aminoAcidTypes.add("AFILMPVW");
	}
	
	private void setToPolarity(){
		this.classificationTypes = new ArrayList<String>();
		this.classificationTypes.add("Low polarity");
		this.classificationTypes.add("Neutral polarity");
		this.classificationTypes.add("High polarity");
		this.classificationLetter = new ArrayList<Character>();
		this.classificationLetter.add('L');
		this.classificationLetter.add('N');
		this.classificationLetter.add('H');		
		this.aminoAcidTypes = new ArrayList<String>();
		this.aminoAcidTypes.add("LIFWCMVY");
		this.aminoAcidTypes.add("PATGS");
		this.aminoAcidTypes.add("HQRKNED");
	}
	
	private void setToPolarizability(){
		this.classificationTypes = new ArrayList<String>();
		this.classificationTypes.add("Low polarizability");
		this.classificationTypes.add("Medium polarizability");
		this.classificationTypes.add("High polarizability");
		this.classificationLetter = new ArrayList<Character>();
		this.classificationLetter.add('L');
		this.classificationLetter.add('M');
		this.classificationLetter.add('H');		
		this.aminoAcidTypes = new ArrayList<String>();
		this.aminoAcidTypes.add("GASDT");
		this.aminoAcidTypes.add("CPNVEQIL");
		this.aminoAcidTypes.add("KMHFRYW");
	}
	
	private void setToSize(){
		this.classificationTypes = new ArrayList<String>();
		this.classificationTypes.add("Small");
		this.classificationTypes.add("Medium");
		this.classificationTypes.add("Large");
		this.classificationLetter = new ArrayList<Character>();
		this.classificationLetter.add('S');
		this.classificationLetter.add('M');
		this.classificationLetter.add('L');		
		this.aminoAcidTypes = new ArrayList<String>();
		this.aminoAcidTypes.add("GASCTPD");
		this.aminoAcidTypes.add("NVEQIL");
		this.aminoAcidTypes.add("MHKFRYW");
	}
	
	private void setToHydrophobicities(){
		this.classificationTypes = new ArrayList<String>();
		this.classificationTypes.add("Polar");
		this.classificationTypes.add("Neutral");
		this.classificationTypes.add("Hydrophobic");
		this.classificationLetter = new ArrayList<Character>();
		this.classificationLetter.add('P');
		this.classificationLetter.add('N');
		this.classificationLetter.add('H');		
		this.aminoAcidTypes = new ArrayList<String>();
		this.aminoAcidTypes.add("RKEDQN");
		this.aminoAcidTypes.add("GASTPHY");
		this.aminoAcidTypes.add("CVLIMFW");
	}
	
	private void setToHydrophobicities2(){
		this.classificationTypes = new ArrayList<String>();
		this.classificationTypes.add("Hydrophobic");
		this.classificationTypes.add("Hydrophilic");
		this.classificationLetter = new ArrayList<Character>();
		this.classificationLetter.add('H');
		this.classificationLetter.add('L');			
		this.aminoAcidTypes = new ArrayList<String>();
		this.aminoAcidTypes.add("ACFILMV");
		this.aminoAcidTypes.add("BDEGHKNPQRSTWYZ");
	}
}
