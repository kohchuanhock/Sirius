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

import javax.swing.JOptionPane;
//NOTE: if upstream and/or downstream is beyond the length of sequence
// nextShift will just return a sequence of same length full of 'N'
// to save time, maybe want to see what to do with this

public class SequenceManipulation {
	String sequence;
	int upstream;
	int downstream;
	int currentShift;
	boolean once = true;
    public SequenceManipulation(String sequence,int upstream,int downstream) {
    	this.sequence = sequence;
    	this.upstream = upstream;
    	this.downstream = downstream;    	
    	this.currentShift = upstream;     	
    }
    public String nextShift(){		
    	//returns the sequence by shifting from upstream till downstream
    	if(currentShift == 0)//0 is not allowed for sequences
    		currentShift++;	
    	if(currentShift <= downstream && currentShift <= sequence.length() + 1 && 
			(currentShift > 0 || (currentShift*-1) <= sequence.length())){
    		String temp = "";    		
    		if(currentShift < 0){
    			for(int x = currentShift; x < 0; x++){
    				temp+="N";
    			}
    			//note that currentShift is -ve
    			temp += sequence.substring(0,sequence.length() + currentShift);    		
    		}
    		else{//currentShift > 0
    			temp = sequence.substring(currentShift - 1);
    			for(int x = 0; x < currentShift - 1; x++){
    				temp += "N";
    			}
    		}
    		//Debug purpose
    		if(temp.length() != sequence.length())    			
    			JOptionPane.showMessageDialog(null,"Error at Sequence Manipulation",
    				"Error",JOptionPane.ERROR_MESSAGE); 
    		currentShift++;
    		return temp;
    	}
		else if(currentShift <= downstream){
			String temp = "";
			for(int x = 0; x < sequence.length(); x++){
				temp += "N";
			}
			currentShift++;
			return temp;
		}
    	else 
    		return null;    	
    }
}