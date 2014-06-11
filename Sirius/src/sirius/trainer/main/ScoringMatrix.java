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

package sirius.trainer.main;

//import java.text.DecimalFormat;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import javax.swing.*;

public class ScoringMatrix {	
	private final int AminoAcidCount = 24;	
	//private Hashtable<Character,Integer> aminoAcidTable;
	private Double[][] blosum62ScoreMatrix;
	private Double[][] structureDerivedScoreMatrix;	
	private Double[][] scoreMatrix;		
	
	private Double[][] zsuzsannaMatrix;
	private Double[][] zsuzsannaMatrix2;
		
	public ScoringMatrix(){
		//aminoAcidTable = new Hashtable<Character,Integer>();
		blosum62ScoreMatrix = new Double[AminoAcidCount][AminoAcidCount];
		structureDerivedScoreMatrix = new Double[AminoAcidCount][AminoAcidCount];
		//df = new DecimalFormat("0.##"); //set return scores to 2 decimal points.
		iniBlosum62ScoreMatrix();
		iniStructureDerivedScoreMatrix();
		zsuzsannaMatrix = iniSzuzsannaMatrix(zsuzsannaMatrix, "ZsuzsannaPairwiseEnergyMatrix");
		zsuzsannaMatrix2 = iniSzuzsannaMatrix(zsuzsannaMatrix2, "ZsuzsannaPairwiseEnergyMatrix2");
	}	
		
	public void setMatrix(int index){
		switch(index){
			case 0: break;//this is for identity matrix
			case 1: scoreMatrix = blosum62ScoreMatrix; break;
			case 2: scoreMatrix = structureDerivedScoreMatrix; break;
			case 3: scoreMatrix = zsuzsannaMatrix; break;
			case 4: scoreMatrix = zsuzsannaMatrix2; break;
			default: JOptionPane.showMessageDialog(null,"Invalid Scoring Matrix: " + index,"Error",JOptionPane.ERROR_MESSAGE);
			throw new Error();
		}		
	}
	
	/*public void printZsuzsannaMatrix(){
		for(int x = 0; x < this.zsuzsannaMatrix.length; x++){
			for(int y = 0; y < this.zsuzsannaMatrix.length; y++){
				System.out.print(this.zsuzsannaMatrix[x][y] + "\t");
			}
			System.out.println();
		}
	}
	
	public void printZsuzsannaMatrix2(){
		for(int x = 0; x < this.zsuzsannaMatrix2.length; x++){
			for(int y = 0; y < this.zsuzsannaMatrix2.length; y++){
				System.out.print(this.zsuzsannaMatrix2[x][y] + "\t");
			}
			System.out.println();
		}
	}*/
	
	private Double[][] iniSzuzsannaMatrix(Double[][] m, String filename){
		String[] dataMatrix = {
				"A","C","D","E","F","G","H","I","K","L","M","N","P","Q","R","S","T","V","W","Y"				
		};
		try{			
			BufferedReader input = new BufferedReader(new FileReader(filename));
			Hashtable<String,Double> hashTable = new Hashtable<String,Double>();
			String line = null;
			while((line = input.readLine()) != null){
				StringTokenizer st = new StringTokenizer(line);				
				if(st.countTokens() != 5){
					input.close();
					throw new Exception();
				}
				st.nextToken();
				String firstString = st.nextToken();
				st.nextToken();
				String secondString = st.nextToken();
				Double value = Double.parseDouble(st.nextToken());
				if(firstString.compareToIgnoreCase(secondString) == 0)
					hashTable.put(firstString+secondString,value);
				else{
					hashTable.put(firstString+secondString,value);
					hashTable.put(secondString+firstString,value);
				}
			}
			input.close();
			m = new Double[dataMatrix.length][dataMatrix.length];
			for(int x = 0; x < dataMatrix.length; x++){
				String first = dataMatrix[x];
				for(int y = 0; y < dataMatrix.length; y++){
					String second = dataMatrix[y];										
					m[x][y] = hashTable.get(first+second);
				}
			}			
			return m;
		}catch(IOException ioe){
			//ioe.printStackTrace();
			//JOptionPane.showMessageDialog(null, "Unable to open " + filename, "I/O Error", JOptionPane.ERROR_MESSAGE);	
			System.err.println("Note that " + filename + " is missing..");
		}catch(Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error format in " + filename, "Error", JOptionPane.ERROR_MESSAGE);
		}
		return null;
	}	
	
    private void iniBlosum62ScoreMatrix(){    
    	String[] dataMatrix = 
    		//DO NOT CHANGE OR ADD THE AA CODES WITHOUT MAKING CHANGES TO init METHOD
    		{"A  R  N  D  C  Q  E  G  H  I  L  K  M  F  P  S  T  W  Y  V  B  Z  X  *",
			 "4 -1 -2 -2  0 -1 -1  0 -2 -1 -1 -1 -1 -2 -1  1  0 -3 -2  0 -2 -1  0 -4",//A
			"-1  5  0 -2 -3  1  0 -2  0 -3 -2  2 -1 -3 -2 -1 -1 -3 -2 -3 -1  0 -1 -4",//R
			"-2  0  6  1 -3  0  0  0  1 -3 -3  0 -2 -3 -2  1  0 -4 -2 -3  3  0 -1 -4",//N
			"-2 -2  1  6 -3  0  2 -1 -1 -3 -4 -1 -3 -3 -1  0 -1 -4 -3 -3  4  1 -1 -4",//D
			 "0 -3 -3 -3  9 -3 -4 -3 -3 -1 -1 -3 -1 -2 -3 -1 -1 -2 -2 -1 -3 -3 -2 -4",//C
			"-1  1  0  0 -3  5  2 -2  0 -3 -2  1  0 -3 -1  0 -1 -2 -1 -2  0  3 -1 -4",//Q
			"-1  0  0  2 -4  2  5 -2  0 -3 -3  1 -2 -3 -1  0 -1 -3 -2 -2  1  4 -1 -4",//E
			 "0 -2  0 -1 -3 -2 -2  6 -2 -4 -4 -2 -3 -3 -2  0 -2 -2 -3 -3 -1 -2 -1 -4",//G
			"-2  0  1 -1 -3  0  0 -2  8 -3 -3 -1 -2 -1 -2 -1 -2 -2  2 -3  0  0 -1 -4",//H
			"-1 -3 -3 -3 -1 -3 -3 -4 -3  4  2 -3  1  0 -3 -2 -1 -3 -1  3 -3 -3 -1 -4",//I
			"-1 -2 -3 -4 -1 -2 -3 -4 -3  2  4 -2  2  0 -3 -2 -1 -2 -1  1 -4 -3 -1 -4",//L
			"-1  2  0 -1 -3  1  1 -2 -1 -3 -2  5 -1 -3 -1  0 -1 -3 -2 -2  0  1 -1 -4",//K
			"-1 -1 -2 -3 -1  0 -2 -3 -2  1  2 -1  5  0 -2 -1 -1 -1 -1  1 -3 -1 -1 -4",//M
			"-2 -3 -3 -3 -2 -3 -3 -3 -1  0  0 -3  0  6 -4 -2 -2  1  3 -1 -3 -3 -1 -4",//F
			"-1 -2 -2 -1 -3 -1 -1 -2 -2 -3 -3 -1 -2 -4  7 -1 -1 -4 -3 -2 -2 -1 -2 -4",//P
			 "1 -1  1  0 -1  0  0  0 -1 -2 -2  0 -1 -2 -1  4  1 -3 -2 -2  0  0  0 -4",//S
			 "0 -1  0 -1 -1 -1 -1 -2 -2 -1 -1 -1 -1 -2 -1  1  5 -2 -2  0 -1 -1  0 -4",//T
			"-3 -3 -4 -4 -2 -2 -3 -2 -2 -3 -2 -3 -1  1 -4 -3 -2 11  2 -3 -4 -3 -2 -4",//W
			"-2 -2 -2 -3 -2 -1 -2 -3  2 -1 -1 -2 -1  3 -3 -2 -2  2  7 -1 -3 -2 -1 -4",//Y
			 "0 -3 -3 -3 -1 -2 -2 -3 -3  3  1 -2  1 -1 -2 -2  0 -3 -1  4 -3 -2 -1 -4",//V
			"-2 -1  3  4 -3  0  1 -1  0 -3 -4  0 -3 -3 -2  0 -1 -4 -3 -3  4  1 -1 -4",//B
			"-1  0  0  1 -3  3  4 -2  0 -3 -3  1 -1 -3 -1  0 -1 -3 -2 -2  1  4 -1 -4",//Z
			 "0 -1 -1 -1 -2 -1 -1 -1 -1 -1 -1 -1 -1 -1 -2  0  0 -2 -1 -1 -1 -1 -1 -4",//X
			"-4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4  1"};//*	
		/*StringTokenizer st = new StringTokenizer(dataMatrix[0]);
		for(int x = 0; x < AminoAcidCount; x++){
			aminoAcidTable.put(st.nextToken().charAt(0),x);			
		}*/	
		for(int x = 0; x < AminoAcidCount; x++){
			StringTokenizer st2 = new StringTokenizer(dataMatrix[x+1]);
			for(int y = 0; y < AminoAcidCount; y++){
				blosum62ScoreMatrix[x][y] = Double.parseDouble(st2.nextToken());
			}
		}				
    }
    private void iniStructureDerivedScoreMatrix(){    
    	String[] dataMatrix = {
    			//DO NOT CHANGE OR ADD THE AA CODES WITHOUT MAKING CHANGES TO init METHOD
    "A     R     N     D     C     Q     E     G     H     I     L     K     M     F     P     S     T     W     Y     V     B     Z     X     *",
 "2.09 -0.50 -0.57 -0.73  0.33 -0.75 -0.12  0.27 -1.42 -0.97 -0.39 -0.38 -0.04 -0.76 -0.53  0.34  0.13 -0.66 -1.25  0.02 -0.66 -0.36 -0.14 -5.34",//A
"-0.50  2.87  0.60  0.13 -1.30  0.13  0.99 -0.96  0.54 -1.40 -1.19  1.42 -0.63 -1.40  0.21 -0.06 -0.15 -0.04 -0.75 -1.52  0.34  0.66 -0.24 -5.34",//R
"-0.57  0.60  3.60  1.78 -2.08  0.33 -0.16  0.79  0.76 -2.43 -2.10  0.83 -2.01 -2.25 -1.10  0.40  0.30 -2.89 -0.36 -2.17  2.61  0.03 -0.44 -5.34",//N
"-0.73  0.13  1.78  4.02 -2.51  0.34  1.20 -1.20 -0.01 -2.77 -2.65  0.66 -2.58 -2.19  0.72  0.71 -0.75 -1.91 -1.21 -2.02  3.00  0.87 -0.60 -5.34",//D
" 0.33 -1.30 -2.08 -2.51  6.99 -0.83 -1.97 -2.11 -1.50  0.13 -0.31 -2.19  1.04  1.13 -2.19  0.31 -0.59 -0.76  0.13  0.34 -2.32 -1.53 -0.60 -5.34",//C
"-0.75  0.13  0.33  0.34 -0.83  2.60  1.23 -0.12 -0.46 -1.47 -1.49  0.92 -0.13 -2.31  0.24  1.04  0.60 -0.81 -0.61 -1.38  0.34  1.76 -0.23 -5.34",//Q
"-0.12  0.99 -0.16  1.20 -1.97  1.23  2.97 -0.41 -0.62 -1.81 -2.11  1.11 -1.86 -1.61 -0.26  0.31 -0.21 -2.70 -1.64 -1.84  0.58  2.30 -0.38 -5.34",//E
" 0.27 -0.96  0.79 -1.20 -2.11 -0.12 -0.41  4.36 -0.40 -2.93 -1.98 -0.71 -1.86 -2.67 -0.04  0.29 -0.81 -1.21 -1.62 -1.96 -0.29 -0.30 -0.68 -5.34",//G
"-1.42  0.54  0.76 -0.01 -1.50 -0.46 -0.62 -0.40  5.89 -1.76 -0.93  0.31 -1.04 -0.22 -1.44 -0.74 -0.52 -1.48 -0.12 -0.35  0.34 -0.56 -0.44 -5.34",//H
"-0.97 -1.40 -2.43 -2.77  0.13 -1.47 -1.81 -2.93 -1.76  2.76  1.56 -1.81  0.99  0.76 -2.00 -1.75 -0.96  0.25  0.08  1.94 -2.62 -1.68 -0.56 -5.34",//I
"-0.39 -1.19 -2.10 -2.65 -0.31 -1.49 -2.11 -1.98 -0.93  1.56  2.43 -1.96  1.61  1.23 -1.56 -2.30 -0.86 -0.14  0.70  0.81 -2.40 -1.87 -0.47 -5.34",//L
"-0.38  1.42  0.83  0.66 -2.19  0.92  1.11 -0.71  0.31 -1.81 -1.96  2.91 -1.62 -2.41 -0.19 -0.06 -0.10 -1.94 -1.72 -1.27  0.74  1.04 -0.37 -5.34",//K
"-0.04 -0.63 -2.01 -2.58  1.04 -0.13 -1.86 -1.86 -1.04  0.99  1.61 -1.62  3.75  0.80 -1.09 -1.34 -1.58  0.87 -0.41  0.61 -2.32 -1.19 -0.39 -5.34",//M
"-0.76 -1.40 -2.25 -2.19  1.13 -2.31 -1.61 -2.67 -0.22  0.76  1.23 -2.41  0.80  3.28 -0.91 -1.11 -0.69  2.29  1.96  0.51 -2.22 -1.88 -0.48 -5.34",//F
"-0.53  0.21 -1.10  0.72 -2.19  0.24 -0.26 -0.04 -1.44 -2.00 -1.56 -0.19 -1.09 -0.91  5.45 -0.29  0.93 -5.34 -1.98 -1.11 -0.10 -0.06 -0.53 -5.34",//P
" 0.34 -0.06  0.40  0.71  0.31  1.04  0.31  0.29 -0.74 -1.75 -2.30 -0.06 -1.34 -1.11 -0.29  2.36  1.20 -1.18 -1.56 -1.11  0.57  0.59 -0.26 -5.34",//S
" 0.13 -0.15  0.30 -0.75 -0.59  0.60 -0.21 -0.81 -0.52 -0.96 -0.86 -0.10 -1.58 -0.69  0.93  1.20  2.04 -0.57 -0.41  0.05 -0.27  0.10 -0.13 -5.34",//T
"-0.66 -0.04 -2.89 -1.91 -0.76 -0.81 -2.70 -1.21 -1.48  0.25 -0.14 -1.94  0.87  2.29 -5.34 -1.18 -0.57  6.96  2.15 -1.09 -2.36 -1.97 -0.74 -5.34",//W
"-1.25 -0.75 -0.36 -1.21  0.13 -0.61 -1.64 -1.62 -0.12  0.08  0.70 -1.72 -0.41  1.96 -1.98 -1.56 -0.41  2.15  3.95  0.21 -0.82 -1.25 -0.39 -5.34",//Y
" 0.02 -1.52 -2.17 -2.02  0.34 -1.38 -1.84 -1.96 -0.35  1.94  0.81 -1.27  0.61  0.51 -1.11 -1.11  0.05 -1.09  0.21  2.05 -2.09 -1.66 -0.33 -5.34",//V
"-0.66  0.34  2.61  3.00 -2.32  0.34  0.58 -0.29  0.34 -2.62 -2.40  0.74 -2.32 -2.22 -0.10  0.57 -0.27 -2.36 -0.82 -2.09  3.83  0.49 -0.39 -5.34",//B
"-0.36  0.66  0.03  0.87 -1.53  1.76  2.30 -0.30 -0.56 -1.68 -1.87  1.04 -1.19 -1.88 -0.06  0.59  0.10 -1.97 -1.25 -1.66  0.49  2.83 -0.39 -5.34",//Z
"-0.14 -0.24 -0.44 -0.60 -0.60 -0.23 -0.38 -0.68 -0.44 -0.56 -0.47 -0.37 -0.39 -0.48 -0.53 -0.26 -0.13 -0.74 -0.39 -0.33 -0.39 -0.39 -0.39 -5.34",//X
"-5.34 -5.34 -5.34 -5.34 -5.34 -5.34 -5.34 -5.34 -5.34 -5.34 -5.34 -5.34 -5.34 -5.34 -5.34 -5.34 -5.34 -5.34 -5.34 -5.34 -5.34 -5.34 -5.34  1.00"};//*
	
		/*StringTokenizer st = new StringTokenizer(dataMatrix[0]);
		for(int x = 0; x < AminoAcidCount; x++){
			aminoAcidTable.put(st.nextToken().charAt(0),x);			
		}*/		
		for(int x = 0; x < AminoAcidCount; x++){
			StringTokenizer st2 = new StringTokenizer(dataMatrix[x+1]);
			for(int y = 0; y < AminoAcidCount; y++){
				structureDerivedScoreMatrix[x][y] = Double.parseDouble(st2.nextToken());
			}
		}				
    }  
    public void testOutput(){
    	//test output
    	//DO NOT CHANGE OR ADD THE AA CODES WITHOUT MAKING CHANGES TO init METHOD
		String dataMatrix = "A  R  N  D  C  Q  E  G  H  I  L  K  M  F  P  S  T  W  Y  V";
		StringTokenizer st = new StringTokenizer(dataMatrix);		
		while(st.hasMoreTokens()){
			Character stchar = st.nextToken().charAt(0);
			StringTokenizer st2 = new StringTokenizer(dataMatrix);
			while(st2.hasMoreTokens()){				
				Character st2char = st2.nextToken().charAt(0);			
				JOptionPane.showMessageDialog(null,stchar + "-" + st2char + ": " + getScore(stchar,st2char),
	    				"Error",JOptionPane.ERROR_MESSAGE); 
			}
		}		
    }
    
    public static void main(String[] args){
    	ScoringMatrix m = new ScoringMatrix();
    	m.setMatrix(0);
    	System.out.println("A,R: " + m.getScore('A','R'));
    	System.out.println("L,N: " + m.getScore('L','N'));    	
    	m.setMatrix(2);
    	System.out.println("A,R: " + m.getScore('A','R'));
    	System.out.println("L,N: " + m.getScore('L','N'));    
    	
    	//m.printZsuzsannaMatrix();
    	System.out.println();
    	//m.printZsuzsannaMatrix2();
    }
    
    public double getScore(char firstChar,char secondChar){    
    	int firstIndex = this.char2Int(firstChar);
    	int secondIndex = this.char2Int(secondChar);    	
    	return scoreMatrix[firstIndex][secondIndex];
    	
    	/*Integer firstIndex = aminoAcidTable.get(firstChar);
    	Integer secondIndex = aminoAcidTable.get(secondChar);
    	if(firstIndex == null)//if first char is not in the list
    		firstIndex = aminoAcidTable.get('*');
    	if(secondIndex == null)//if second char is not in the list
    		secondIndex = aminoAcidTable.get('*');
    	return scoreMatrix[firstIndex][secondIndex];*/
    }
        
    
    private int char2Int(char _char){
    	//"A  R  N  D  C  Q  E  G  H  I  L  K  M  F  P  S  T  W  Y  V  B  Z  X  *"
    	switch(_char){
    	case 'A': case 'a': return 0; 
    	case 'R': case 'r': return 1;
    	case 'N': case 'n': return 2;    	
    	case 'D': case 'd': return 3;
    	case 'C': case 'c': return 4;
    	case 'Q': case 'q': return 5;
    	case 'E': case 'e': return 6;
    	case 'G': case 'g': return 7;
    	case 'H': case 'h': return 8;
    	case 'I': case 'i': return 9;
    	case 'L': case 'l': return 10;
    	case 'K': case 'k': return 11;
    	case 'M': case 'm': return 12;
    	case 'F': case 'f': return 13;
    	case 'P': case 'p': return 14;
    	case 'S': case 's': return 15;
    	case 'T': case 't': return 16;
    	case 'W': case 'w': return 17;
    	case 'Y': case 'y': return 18;
    	case 'V': case 'v': return 19;
    	case 'B': case 'b': return 20;
    	case 'Z': case 'z': return 21;
    	case 'X': case 'x': return 22;    	
    	default: return 23;
    	}
    	//"A  R  N  D  C  Q  E  G  H  I  L  K  M  F  P  S  T  W  Y  V  B  Z  X  *"
    }
}