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
package sirius.main;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;

import javax.swing.JOptionPane;

import sirius.trainer.step1.Step1TableModel;
import sirius.utils.FastaFormat;

public class FastaFileManipulation {	
	String workingDirectory;		
	BufferedReader in;
	BufferedReader inPos;
	BufferedReader inNeg;
	
	//File file;
	File filePos;
	File fileNeg;	
	String prefix;
	
	public FastaFileManipulation(Step1TableModel posTableModel,Step1TableModel negTableModel,
		int posDatasetFrom,int posDatasetTo,int negDatasetFrom,int negDatasetTo,
		String workingDirectory){
		
		Random rand = new Random();
		this.prefix = rand.nextLong() + "";
		
		this.workingDirectory = workingDirectory;
		try{
			filePos = new File(workingDirectory + File.separator + prefix + "tempPosFile");
			BufferedWriter out = new BufferedWriter(new FileWriter(filePos));
			createTempRawFile(out,posTableModel,posDatasetFrom,posDatasetTo);			
			out.close();
			
			fileNeg = new File(workingDirectory + File.separator + prefix + "tempNegFile");
			out = new BufferedWriter(new FileWriter(fileNeg));
			createTempRawFile(out,negTableModel,negDatasetFrom,negDatasetTo);			
			out.close();
		}
    	catch(Exception e){
    		JOptionPane.showMessageDialog(null,"Error trying to create files in " + workingDirectory,
    			"ERROR",JOptionPane.ERROR_MESSAGE);
    	}
					
	}
	public void cleanUp(){
		try{
			if(inPos!=null)
				inPos.close();
			if(inNeg!=null)
				inNeg.close();
			filePos.delete(); 		   	
		   	fileNeg.delete(); 		       	
		}catch(Exception e){
			JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();			
		}
	}
	public FastaFormat nextSequence(String _class){    	
		try{
			if(_class.indexOf("pos") != -1){
				if(inPos == null)
					inPos = new BufferedReader(new FileReader(workingDirectory + File.separator + prefix + "tempPosFile"));
				String header = inPos.readLine();				
		    	if(header == null){
		    		inPos.close();
		    		inPos = null;
		    		filePos.delete();
		    		return null;		
		    	}else{
		    		String sequence = inPos.readLine();
		    		FastaFormat fastaFormat = new FastaFormat(header,sequence);
					return fastaFormat;
		    	}		    	
			}else{
				if(inNeg == null){					
					inNeg = new BufferedReader(new FileReader(workingDirectory + File.separator + prefix + "tempNegFile"));					
				}
				String header = inNeg.readLine();
		    	if(header == null){
		    		inNeg.close();	    		
		    		inNeg = null;
		    		fileNeg.delete();
		    		return null;
		    	}else{
		    		String sequence = inNeg.readLine();
					FastaFormat fastaFormat = new FastaFormat(header,sequence);
					return fastaFormat;
		    	}
			}			
		}catch(Exception e){
			e.printStackTrace();
			if(_class.indexOf("pos") != -1)
				JOptionPane.showMessageDialog(null,"Error trying to reading " + workingDirectory + 
						File.separator + prefix + "tempPosFile","ERROR",JOptionPane.ERROR_MESSAGE);
			else
				JOptionPane.showMessageDialog(null,"Error trying to reading " + workingDirectory + 
						File.separator + prefix + "tempNegFile","ERROR",JOptionPane.ERROR_MESSAGE);
			return null;
		}    		    	  		    
    }
    private void createTempRawFile(BufferedWriter out,Step1TableModel step1TableModel,int fromInt,int toInt){
    	int index = -1;
		for(int x = 0; x < step1TableModel.getRowCount(); x++){	
			//locate the first file where the dataset 1 range fall within			
			if(fromInt <= step1TableModel.getSeqTo(x)){
				index = x;
				break;
			}
		}		
		if(index == -1){
			//This situation should not occur since we had already previously ensure the range
			throw new Error("OUT OF RANGE ERROR");
		}
		else{
			try{
				int inputFileIndex = step1TableModel.getSeqFrom(index);							
				do{
					BufferedReader inputFile = new BufferedReader(
						new FileReader(step1TableModel.getFirstCol(index)));				
					String line;					
					String cascadingLine = "";
					String headerLine = "";
					boolean first = true;
					while ((line = inputFile.readLine()) != null) {
						if(line.indexOf(">")==0){							
							if(first == true)
								first = false;
							else{
								if(inputFileIndex >= fromInt && inputFileIndex <= toInt){
									//verified to be able to get every sequence correctly
									//now is to change it into a .arff file		
									//write header line
									out.write(headerLine);
									out.newLine();							
									out.write(cascadingLine);
									out.newLine();
									out.flush();
								}								
								inputFileIndex++;
								cascadingLine = "";
							}							
							headerLine = line;
		            	}
		            	else{
							cascadingLine += line;	            	
		            	}
		            	if(inputFileIndex > toInt)
		            		break;
					}
					//For the last > in the file
					if(inputFileIndex >= fromInt && inputFileIndex <= toInt){	
						out.write(headerLine);
						out.newLine();							
						out.write(cascadingLine);				
						out.newLine();
						out.flush();
					}
					inputFileIndex++;
					index++;
					inputFile.close();
				}while(inputFileIndex < toInt);//for version 2.2, in previous version, I use while instead of do-while
												//but in previous versions, i cannot have posFrom == posTo (or negFrom == negTo), 
												//will cause abit of error but at the same time, 
												//i wonder by changing, will i introduce other errors?
			}catch(Exception e){
				JOptionPane.showMessageDialog(null,"Exception Occured","Error",JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();}
		}
    }			
}